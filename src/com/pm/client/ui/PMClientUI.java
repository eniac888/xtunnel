// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.AWTException;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.Tools;
import com.pm.bean.MapRule;
import com.pm.bean.MapServerBean;
import com.pm.client.ClientConfig;
import com.pm.client.ControlClient;
import com.pm.client.MapRuleManage;
import com.pm.client.MapServerItem;
import com.pm.client.MapMonit;
import com.pm.client.Updater;

public class PMClientUI {
	
	JFrame mainFrame;
	
	Dimension size=new Dimension(560,420);
	
	static int monPort=15587;
	
	MainPanel mainPanel;
	
	static String logoImg="img/logo.png";

	static String offlineImg="img/offline.png";
	
	private TrayIcon trayIcon;
	
	private SystemTray tray;
	
	static String name="XTunnel 1.0";
	
	MapRuleManage mapRuleManager;
	
	public ControlClient controlClient;

	ClientConfig config;

	String configFileName="clientconfig.json";
	
	Thread loadThread;
	
	boolean accountError=false;
	
	static String baseUrl="http://pm.youtusoft.com/wantong/";
	String serviceUrl=baseUrl+"clientservice";
	public static String updateUrl=baseUrl+"clientaccess_xt/update.properties";
	public static String messageUrl=baseUrl+"clientaccess_xt/message.html";
	
	static PMClientUI ui;
	
	boolean min=false;
		
	public static String wetsite="http://www.d1sm.net/?client_xt";
		
	public static String  upgradeUrl;
	
	public static int localVersion=1;
	
	boolean connectable;
	
	boolean connecting=false;
	
	
	public PMClientUI(boolean min) throws Exception{
		ui=this;
		this.min=min;
		try {
			final ServerSocket socket=new ServerSocket(monPort);
			new Thread(){
				public void run(){
					try {
						socket.accept();
					} catch (IOException e) {
						e.printStackTrace();
						System.exit(0);
					}
				}
			}.start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		loadConfig();
		if(config.getUserId().equals("")){
			String id=""+Math.abs(new Random().nextInt());
			config.setUserId(id);
		}
		saveConfig();
		setAutoRun(config.isAutoStart());
		
		initUI();
		mainFrame=new JFrame(name);
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(logoImg));
		mainFrame.setSize(size);
		mainFrame.setLocationRelativeTo(null);
		
		mainPanel=new MainPanel(this);
		mainFrame.setContentPane(mainPanel);
		if(!min){
			mainFrame.setVisible(true);
		}
		
		mapRuleManager=new MapRuleManage(this);
		controlClient=new ControlClient(this);
		loadRule(-1);
		
		final Updater updater=new Updater(this);
		new Thread(){
			public void run(){
				updater.checkUpdate();
			}
		}.start();
	}
	
	public static PMClientUI get(){
		return ui;
	}
	
	void exit(){
		System.exit(0);
	}
	
	void loadRule(final int selectMapId){
		if(loadThread!=null){
			loadThread.interrupt();
		}
		if(!config.getServerAddress().trim().equals("")
				&&config.getServerPort()>0){
			loadThread=new Thread(){
				public void run(){
					while(true){
						try {
							setConnecting(true);
							controlClient.startLoadRule(selectMapId);
							setConnectable(true);
							break;
						} catch (Exception e) {
							//e.printStackTrace();
							setConnectable(false);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e1) {
								//e1.printStackTrace();
								break;
							}
						}
					}
					setConnecting(false);
				}
			};
			loadThread.start();
		}
	}
	
	void select(int mapId){
		mainPanel.select(mapId);
	}
	
	boolean isEmpty(String s){
		boolean b=false;
		if(s==null){
			b=true;
		}else{
			b=s.equals("");
		}
		return b;
	}
	
	public void updateTcpMapRule(List<MapRule> list,int selectMapId){
		mainPanel.getTcpMapRuleListTable().setMapRuleList(list);
		getMapRuleManager().setMapRuleList(list);
		if(selectMapId>0){
			select(selectMapId);
		}
	}
	
	void initUI(){
		ToolTipManager.sharedInstance().setInitialDelay(100);
		ToolTipManager.sharedInstance().setDismissDelay(99999999);
	    
		Font font = new Font("宋体",Font.PLAIN,12);
		UIManager.put("ToolTip.font",font);
		UIManager.put("Table.font",font);
		UIManager.put("TableHeader.font",font);
		UIManager.put("TextField.font",font);
		UIManager.put("ComboBox.font",font);
		UIManager.put("TextField.font",font);
		UIManager.put("PasswordField.font",font);
		UIManager.put("TextArea.font,font",font);
		UIManager.put("TextPane.font",font);
		UIManager.put("EditorPane.font",font);
		UIManager.put("FormattedTextField.font",font);
		UIManager.put("Button.font",font);
		UIManager.put("CheckBox.font",font);
		UIManager.put("RadioButton.font",font);
		UIManager.put("ToggleButton.font",font);
		UIManager.put("ProgressBar.font",font);
		UIManager.put("DesktopIcon.font",font);
		UIManager.put("TitledBorder.font",font);
		UIManager.put("Label.font",font);
		UIManager.put("List.font",font);
		UIManager.put("TabbedPane.font",font);
		UIManager.put("MenuBar.font",font);
		UIManager.put("Menu.font",font);
		UIManager.put("MenuItem.font",font);
		UIManager.put("PopupMenu.font",font);
		UIManager.put("CheckBoxMenuItem.font",font);
		UIManager.put("RadioButtonMenuItem.font",font);
		UIManager.put("Spinner.font",font);
		UIManager.put("Tree.font",font);
		UIManager.put("ToolBar.font",font);
		UIManager.put("OptionPane.messageFont",font);
		UIManager.put("OptionPane.buttonFont",font);
		
		PopupMenu trayMenu = new PopupMenu();
		tray = SystemTray.getSystemTray();
		trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(offlineImg),name,trayMenu);
		trayIcon.setImageAutoSize(true);
		trayIcon.setImageAutoSize(true);
		ActionListener listener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.toFront();
				mainFrame.setVisible(true);
			}
		};
		trayIcon.addActionListener(listener);
		
		try {
			tray.add(trayIcon);
		} catch (AWTException e1) {
			e1.printStackTrace();
		}
		MenuItem item3;
		try {
			item3 = new MenuItem("退出");
			ActionListener al=new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			};
			item3.addActionListener(al);
			trayMenu.add(item3);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void setStatus(String message){
		mainPanel.setStatus(message);
	}
	
	public void updateUIStatus(){
		String message="";
		if(trayIcon!=null){
			if(connectable){
				if(accountError){
					message="未登录,请检查登录密码";
					trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(offlineImg));
				}else{
					if(connecting){
						message="登录中";
						trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(offlineImg));
					}else{
						message="正常";
						trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(logoImg));
					}
				}
			}else{
				message="连接失败";
				trayIcon.setImage(Toolkit.getDefaultToolkit().getImage(offlineImg));
			}
			setStatus(message);
		}
	}
	

	public void loadConfig() {
		config=new ClientConfig();
		String content;
		try {
			content = Tools.readFileData(configFileName);
			JSONObject config_json=JSONObject.parseObject(content);
			config.setUserId(config_json.getString("user_id"));
			config.setServerAddress(config_json.getString("server_address"));
			config.setServerPort(config_json.getIntValue("server_port"));
			config.setPasswordMd5(config_json.getString("password_md5"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void saveConfig(){
		JSONObject config_json=new JSONObject();
		config_json.put("user_id", config.getUserId());
		config_json.put("server_address", config.getServerAddress());
		config_json.put("server_port", config.getServerPort());
		config_json.put("password_md5", config.getPasswordMd5());
		try {
			Tools.saveFile(config_json.toString(), configFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setAutoRun(boolean run) {
		String s = new File(".").getAbsolutePath();
		String currentPaht = s.substring(0, s.length() - 1);
		StringBuffer sb = new StringBuffer();
		StringTokenizer st = new StringTokenizer(currentPaht, "\\");
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
			sb.append("\\\\");
		}
		ArrayList<String> list = new ArrayList<String>();
		list.add("Windows Registry Editor Version 5.00");
		String name="xtunnel";
		if (run) {
			list.add("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run]");
			list.add("\""+name+"\"=\"" + sb.toString() + "PortMapClient.exe -min" + "\"");
		} else {
			list.add("[HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run]");
			list.add("\""+name+"\"=-");
		}

		File file = null;
		try {
			file = new File("import.reg");
			FileWriter fw = new FileWriter(file);
			PrintWriter pw = new PrintWriter(fw);
			for (int i = 0; i < list.size(); i++) {
				String ss = list.get(i);
				if (!ss.equals("")) {
					pw.println(ss);
				}
			}
			pw.flush();
			pw.close();
			Process p = Runtime.getRuntime().exec("regedit /s " + "import.reg");
			p.waitFor();
		} catch (Exception e1) {
			// e1.printStackTrace();
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public MainPanel getMainPanel() {
		return mainPanel;
	}

	public void setMainPanel(MainPanel mainPanel) {
		this.mainPanel = mainPanel;
	}
	
	public MapRuleManage getMapRuleManager() {
		return mapRuleManager;
	}

	public void setMapRuleManager(MapRuleManage mapRuleManager) {
		this.mapRuleManager = mapRuleManager;
	}

	public ControlClient getControlClient() {
		return controlClient;
	}

	public void setControlClient(ControlClient controlClient) {
		this.controlClient = controlClient;
	}

	public ClientConfig getConfig() {
		return config;
	}

	public void setConfig(ClientConfig config) {
		this.config = config;
	}

	public boolean isAccountError() {
		return accountError;
	}

	public void setAccountError(boolean accountError) {
		this.accountError = accountError;
		updateUIStatus();
	}

	public String getServiceUrl() {
		return serviceUrl;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public boolean isConnectable() {
		return connectable;
	}

	public void setConnectable(boolean connectable) {
		this.connectable = connectable;
		ui.updateUIStatus();
	}

	public boolean isConnecting() {
		return connecting;
	}

	public void setConnecting(boolean connecting) {
		this.connecting = connecting;
	}

}
