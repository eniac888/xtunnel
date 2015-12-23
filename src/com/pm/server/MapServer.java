// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.pm.PM;
import com.pm.Tools;
import com.pm.bean.MapServerBean;

public class MapServer {

	MapPortManager mapPortManager;

	MapClientManager mapClientManager;

	ServerSocket controlServerSocket;
	
	MapBManager mapBManager;

	static MapServer mapServer;
	
	ServerConfig serverConfig;
	
	MapRuleManage mapRuleManage;
	
	MapServerBean mapServerBean;
	
	ServerSocket serverSocket;
	
	String systemName = System.getProperty("os.name").toLowerCase();

	public static void main(String[] args) throws Exception {
		new MapServer();
	}

	public static MapServer get(){
		return mapServer;
	}

	MapServer()  {
		mapServer=this;
		
		serverConfig=new ServerConfig();
		try {
			serverConfig.setListenPort(Tools.readFileIntValue("./cnf/listen_port"));
		} catch (Exception e2) {
			//e2.printStackTrace();
		}
		try {
			File file=new File("./cnf/password");
			if (file.exists()) {
				FileReader fr=new FileReader(file);
				BufferedReader br=new BufferedReader(fr);
				String line="";
				while((line=br.readLine())!=null){
					String pwd=line.trim();
					if(!pwd.equals("")){
						serverConfig.getPasswordList().add(Tools.getMD5(pwd));
					}
				}
				fr.close();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
		mapServerBean=new MapServerBean();
		try {
			mapServerBean.setDomainName(Tools.readFileStringValue("./cnf/domain_name"));
		} catch (Exception e2) {
			//e2.printStackTrace();
		}
		try {
			mapServerBean.setDescription(Tools.readFileStringValue("./cnf/description"));
		} catch (Exception e2) {
			//e2.printStackTrace();
		}
		try {
			mapServerBean.setWebPort(Tools.readFileIntValue("./cnf/web_port"));
		} catch (Exception e2) {
			//e2.printStackTrace();
		}

		mapRuleManage=new MapRuleManage(this);
		mapPortManager=new MapPortManager();
		mapClientManager=new MapClientManager();
		mapBManager=new MapBManager(this);
		try {
			mapBManager.bindPort(mapServerBean.getWebPort());
		} catch (IOException e1) {
			//e1.printStackTrace();
			System.out.println("Start failed,can't bind web port:"+mapServerBean.getWebPort());
			return;
		}
		
		try {
			serverSocket=new ServerSocket(serverConfig.getListenPort(),100,InetAddress.getByName("0.0.0.0"));
		} catch (Exception e1) {
			//e1.printStackTrace();
			System.out.println("Start failed,can't bind port:"+serverConfig.getListenPort());
			return;
		}

		Runnable threadRunnable=new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true){
						Socket socket=serverSocket.accept();
						new ControlSocketProcess(mapServer,socket,socket.getInputStream(),socket.getOutputStream());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		};
		PM.exec(threadRunnable);
		
		
		if (systemName.equals("linux")) {
			setFireWall_linux_tcp();
		}
		
		System.out.println("Start success,listen on port:"+serverConfig.getListenPort()+",web port:"+mapServerBean.getWebPort()+",domain name:"+mapServerBean.getDomainName());
		
	}
	
	void setFireWall_linux_tcp() {
		cleanTcpTunRule();
		String cmd2 = "iptables -I INPUT -p tcp --dport 10000:20000 -j ACCEPT"
				+ " -m comment --comment xtunel_server ";
		runCommand(cmd2);
		String cmd3 = "iptables -I INPUT -p tcp --dport "+serverConfig.getListenPort()+" -j ACCEPT"
				+ " -m comment --comment xtunel_server ";
		runCommand(cmd3);
		String cmd4 = "iptables -I INPUT -p tcp --dport "+mapServerBean.getWebPort()+" -j ACCEPT"
				+ " -m comment --comment xtunel_server ";
		runCommand(cmd4);

	}
	
	void cleanTcpTunRule() {
		while (true) {
			int row = getRow("xtunel_server");
			if (row > 0) {
				// MLog.println("删除行 "+row);
				String cmd = "iptables -D INPUT " + row;
				runCommand(cmd);
			} else {
				break;
			}
		}
	}

	int getRow(String name) {
		int row_delect = -1;
		String cme_list_rule = "iptables -L -n --line-number";
		// String [] cmd={"netsh","advfirewall set allprofiles state on"};
		Thread errorReadThread = null;
		try {
			final Process p = Runtime.getRuntime().exec(cme_list_rule, null);

			errorReadThread = new Thread() {
				public void run() {
					InputStream is = p.getErrorStream();
					BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
					while (true) {
						String line;
						try {
							line = localBufferedReader.readLine();
							if (line == null) {
								break;
							} else {
								// System.out.println("erroraaa "+line);
							}
						} catch (IOException e) {
							e.printStackTrace();
							// error();
							break;
						}
					}
				}
			};
			errorReadThread.start();

			InputStream is = p.getInputStream();
			BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
			while (true) {
				String line;
				try {
					line = localBufferedReader.readLine();
					// System.out.println("standaaa "+line);
					if (line == null) {
						break;
					} else {
						if (line.contains(name)) {
							int index = line.indexOf("   ");
							if (index > 0) {
								String n = line.substring(0, index);
								try {
									if (row_delect < 0) {
										// System.out.println("standaaabbb
										// "+line);
										row_delect = Integer.parseInt(n);
									}
								} catch (Exception e) {

								}
							}
						}
						;
					}
				} catch (IOException e) {
					e.printStackTrace();
					break;
				}
			}

			errorReadThread.join();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			// error();
		}
		return row_delect;
	}
	
	void runCommand(String command) {
		Thread standReadThread = null;
		Thread errorReadThread = null;
		try {
			final Process p = Runtime.getRuntime().exec(command, null);
			standReadThread = new Thread() {
				public void run() {
					InputStream is = p.getInputStream();
					BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
					while (true) {
						String line;
						try {
							line = localBufferedReader.readLine();
							// System.out.println("stand "+line);
							if (line == null) {
								break;
							}
						} catch (IOException e) {
							e.printStackTrace();
							break;
						}
					}
				}
			};
			standReadThread.start();

			errorReadThread = new Thread() {
				public void run() {
					InputStream is = p.getErrorStream();
					BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(is));
					while (true) {
						String line;
						try {
							line = localBufferedReader.readLine();
							if (line == null) {
								break;
							} else {
								// System.out.println("error "+line);
							}
						} catch (IOException e) {
							e.printStackTrace();
							// error();
							break;
						}
					}
				}
			};
			errorReadThread.start();
			standReadThread.join();
			errorReadThread.join();
			p.waitFor();
		} catch (Exception e) {
			e.printStackTrace();
			// error();
		}
	}

	public MapPortManager getMapPortManager() {
		return mapPortManager;
	}

	public void setMapPortManager(MapPortManager mapPortManager) {
		this.mapPortManager = mapPortManager;
	}

	public MapClientManager getMapClientManager() {
		return mapClientManager;
	}

	public void setMapClientManager(MapClientManager mapClientManager) {
		this.mapClientManager = mapClientManager;
	}

	public MapBManager getMapBManager() {
		return mapBManager;
	}

	public void setMapBManager(MapBManager mapBManager) {
		this.mapBManager = mapBManager;
	}

	public ServerConfig getServerConfig() {
		return serverConfig;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public MapRuleManage getMapRuleManage() {
		return mapRuleManage;
	}

	public void setMapRuleManage(MapRuleManage mapRuleManage) {
		this.mapRuleManage = mapRuleManage;
	}

	public MapServerBean getMapServerBean() {
		return mapServerBean;
	}

	public void setMapServerBean(MapServerBean mapServerBean) {
		this.mapServerBean = mapServerBean;
	}

}
