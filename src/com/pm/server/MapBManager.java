// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.pm.PM;
import com.pm.bean.MapRule;


public class MapBManager {

	long lastModifiedTime=0;

	MapServer mapServer;

	Selector selector_mapport=null;
	Thread select_mapportThread;
	String ip_mapport="192.168.2.2";
	
	HashMap<Long, MapBSocketProcess> processTable=new HashMap<Long, MapBSocketProcess>();
	
	HashMap<Integer, MapRule> singlePortBindTable=new HashMap<Integer, MapRule>();
	
	HashMap<String, MapRule> domainBindTable=new HashMap<String, MapRule>();
	
	HashMap<Integer, MapRule> mapRuleTable=new HashMap<Integer, MapRule>();
		
	HashMap<Integer, PortBean> portTable=new HashMap<Integer, PortBean>();
	
	Object syn_portTable=new Object();

	MapBManager(final MapServer mapServer) {
		this.mapServer=mapServer;
	}
	
	
	Iterator<Integer> getPortTableIterator(){
		Iterator<Integer> it=null;
		synchronized (syn_portTable) {
			it=new CopiedIterator(portTable.keySet().iterator());
		}
		return it;
	}
	
	boolean containsPort(int port){
		return portTable.containsKey(port);
	}
		
	void bindPort(final int port) throws IOException{
		PortBean bean=new PortBean();
		bean.setLastActiveTime(System.currentTimeMillis());
		bean.setPort(port);
		ServerSocket ss=bindIPPort("0.0.0.0",port);
		bean.serverSocket=ss;
		synchronized (syn_portTable) {
			portTable.put(port, bean);
		}
	}
	
	private ServerSocket bindIPPort(String address,int port) throws IOException{
		final ServerSocket ss = new ServerSocket();
		System.out.println("bind port "+address+":"+port);
		ss.setReuseAddress(true);
		ss.bind(new InetSocketAddress(address, port));
		Runnable threadRunnable=new Runnable() {
			
			@Override
			public void run() {
				try {
					while(true){
						Socket s=ss.accept();
						processSocket(ss,s);
					}
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			
		};
		PM.exec(threadRunnable);
		return ss;
	}
	
	 void processSocket(ServerSocket ss,Socket s) throws IOException{
		s.setTcpNoDelay(true);
		synchronized(this){
			MapBSocketProcess process=new MapBSocketProcess(mapServer,s,s.getInputStream(),s.getOutputStream(),ss.getLocalPort());
			processTable.put(process.getConnectId(), process);
			PortBean bean=portTable.get(ss.getLocalPort());
			if(bean!=null){
				bean.addProcess(process);
				//System.out.println("接收 "+s.getRemoteSocketAddress()+" -> "+s.getLocalSocketAddress());
			}
		}
		
	}
	
	void onMapClose(MapBSocketProcess process){
		int port=process.getPort();
		PortBean bean=portTable.get(port);
		if(bean!=null){
			bean.removeProcess(process.getConnectId());
			//System.out.println("监控_连接关闭 "+bean.getPort()+" "+bean.getConnectSum()+" "+process.getConnectId());
			//bean.setLastActiveTime(System.currentTimeMillis());
		}
	}
	
	public void removeProcess(long id){
		processTable.remove(id);
	}
		
	   public static boolean isPortUsing(int port) throws UnknownHostException{  
	        boolean flag = true;  
	        Socket socket=null;
	        try {  
	            socket = new Socket();  
	            socket.bind(new InetSocketAddress(port));
	            flag = false;  
	        } catch (IOException e) {  
	              
	        }  finally{
	        	if(socket!=null){
	        		try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        	}
	        }
	        return flag;  
	    }
	
	MapRule  getSingelPortBind(int port){
		return singlePortBindTable.get(port);
	}
	
	void addSingelPortBind(int port,MapRule rule){
		singlePortBindTable.put(port, rule);
		if(!portTable.containsKey(rule.getPort())){
			try {
				bindPort(rule.getPort());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void removeSingelPortBind(int port){
		singlePortBindTable.remove(port);
		PortBean bean=portTable.remove(port);
		if(bean!=null){
			System.out.println("remve bind port "+port);
			try {
				bean.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	void addMapRule(MapRule rule){
		mapRuleTable.put(rule.getMapId(), rule);
	}
	
	void removeMapRule(MapRule rule){
		mapRuleTable.remove(rule.getMapId());
	}
	
	public MapRule getMapRule(int id){
		return mapRuleTable.get(id);
	}
	
	void addDomainBind(MapRule rule){
		String domain=MapRule.getWanWebAddress(rule);
		domainBindTable.put(domain, rule);
		String customDomain=rule.getDomainName();
		if(customDomain!=null&!customDomain.equals("")){
			domainBindTable.put(customDomain, rule);
			domainBindTable.put("www."+customDomain, rule);
		}
	}
	
	void removeDomainBind(MapRule rule){
		String domain=MapRule.getWanWebAddress(rule);
		domainBindTable.remove(domain);
		domainBindTable.remove(rule.getDomainName());
		domainBindTable.remove("www."+rule.getDomainName());
//		System.out.println("remove bind domain "+domain);
//		System.out.println("remove bind domain "+rule.getDomainName());
//		System.out.println("remove bind domain "+"www."+rule.getDomainName());
	}
	
	MapRule getDomainBind(String domain){
		return domainBindTable.get(domain);
	}
	
	MapBSocketProcess getMapBSocketProcess(long connectId){
		return processTable.get(connectId);
	}
	
}
