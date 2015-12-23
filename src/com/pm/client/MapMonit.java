// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.Tools;
import com.pm.bean.MapRule;
import com.pm.client.ui.PMClientUI;
import com.pm.server.ConnectionProcessor;

public class MapMonit implements ConnectionProcessor{
	
	//String serverAddress="localhost";
	
	String serverAddress;
		
	int serverPort;
	
	int serverMapPort;
	
	Thread heartBeatThread;
	
	boolean conneted=false;
	
	long lastSendHeartBeatTime;
	
	int sendHeatBeatInterval=30*1000;
	
	MonitConnection conn;

	Thread loadRuleThread;
	
	MapMonit pmClient;
	
	PMClientUI ui;
	
	long lastActiveTime;
	
	int offlineTime=3*sendHeatBeatInterval;
	
	int protocal;
	
	ClientConfig config;
	
	boolean run=true;
	
	boolean logined=false;
		
	public MapMonit(final PMClientUI ui,String serverAddress,int serverPort,final int protocal){
		this.protocal=protocal;
		this.ui=ui;
		config=ui.getConfig();
		pmClient=this;
		this.serverAddress=serverAddress;
		this.serverPort=serverPort;
	}
	
	public void close(){
		run=false;
		logined=false;
		conneted=false;
		if(heartBeatThread!=null){
			heartBeatThread.interrupt();
		}
		if(conn!=null){
			conn.close();
		}
	}
	
	public void reconnect(){
		if(conn!=null){
			conn.reconnect();
		}
	}
	
	public void start(){
		conn=new MonitConnection(Constant.Protocal_Control,serverAddress,serverPort);
		conn.addProcessor(this);
		heartBeatThread=new Thread(){
			public void run(){
				while(run){
					try {
						Thread.sleep(1*1000);
						if(sendAvail()){
							if(logined){
								try {
									sendHeartBeat();
								}catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						ui.updateUIStatus();
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			}
		};
		heartBeatThread.start();
	}
	
	void active(){
		lastActiveTime=System.currentTimeMillis();
	}
	
	public boolean isOnline(){
		return System.currentTimeMillis()-lastActiveTime<offlineTime&&conneted&&logined;
	}
	
	void sendMessage(JSONObject message)throws Exception{
		byte[] data=message.toJSONString().getBytes("utf-8");
		conn.sendData(data);
	}
	
	void sendLogin() throws Exception{
		sendMessage(ClientMessageFactory.getLoginMessage(ui.getConfig().getUserId(),ui.getConfig().getPasswordMd5()));
	}
	
	void sendHeartBeat() throws Exception{
		lastSendHeartBeatTime=System.currentTimeMillis();
		sendMessage(ClientMessageFactory.getHeartBeatMessage(ui.getConfig().getUserId()));
	}
	
	boolean sendAvail(){
		boolean b=false;
		b=conneted&(System.currentTimeMillis()-lastSendHeartBeatTime>sendHeatBeatInterval)&run;
		return b;
	}

	public void onConnected(MonitConnection conn) {
		conneted=true;
		try {
			sendLogin();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onConnectClosed(MonitConnection conn) {
		logined=false;
		conneted=false;
	}
	
	public void onReveiveData(MonitConnection conn, byte[] data) {
		try {
			JSONObject hm=JSONObject.parseObject(new String(data,"utf-8"));
			String type=hm.getString(Constant.Key_Type);
			String userId=hm.getString(Constant.Key_UserId);
			JSONObject sendMessage=null;
			if(type.equals(Constant.Type_HeartBeatMessage2)){
				active();
			}else if(type.equals(Constant.Type_MapConnectRequestMessage)){
				long mapConnectId=hm.getLong(Constant.Key_MapConnectId);
				int mapRuleId=hm.getIntValue(Constant.Key_MapId);
				String dstAddress=hm.getString(Constant.Key_DstAddress);
				int dstPort=hm.getIntValue(Constant.Key_DstPort);
				ui.getMapRuleManager().createNewConnect(mapRuleId,mapConnectId,dstAddress,dstPort);
			}else if(type.equals(Constant.Type_LoginMessage2)){
				String code=hm.getString(Constant.Key_Code);
				if(code.equals(Constant.code_Success)){
					logined=true;
				}else{
					logined=false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getServerAddress() {
		return serverAddress;
	}

	public void setServerAddress(String serverAddress) {
		this.serverAddress = serverAddress;
		conn.setAddress(serverAddress);
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
		conn.setPort(serverPort);
	}
	
}
