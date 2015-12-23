// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Random;

import com.pm.PM;
import com.pm.bean.MapRule;

public class MapBSocketProcess {

	Runnable mainThread;

	Socket socketB;

	MapServer mapServer;

	long connectId;

	Random ran=new Random();

	boolean closed=false;

	InputStream isB;

	OutputStream osB;

	int port;

	byte[] preReadData;

	int preReadDataLength;

	int portType;
	
	MapRule rule=null;
		
	Object syn_close=new Object();
	
	MapBSocketProcess(MapServer mapServer,final Socket socketB,InputStream isB,OutputStream osB,int port){
		this.mapServer=mapServer;
		//this.mapB=mapB;
		this.socketB=socketB;
		this.isB=isB;
		this.osB=osB;
		this.port=port;
		connectId=Math.abs(ran.nextLong());
		mainThread=new Runnable(){
			public void run(){
				boolean needClose=process();
				if(needClose){
					close();
				}
			}
		};
		PM.exec(mainThread);
	}

	boolean process(){
		boolean needClose=true;
		MapClient mapClient=null;
		MapClientManager clientManager=mapServer.getMapClientManager();
		MapBManager mapBManager=mapServer.getMapBManager();
		if(port==mapServer.getMapServerBean().getWebPort()){

			int bufSize=10*1024;
			byte[] array = new byte[bufSize];
			int len;
			try {
				len = isB.read(array);
				if(len>-1){
					preReadData=array;
					preReadDataLength=len;
					String s=new String(array,0,len);
					HttpHost hh=readHost(s);
					//System.out.println("web host: "+hh.getAddress()+":"+hh.getPort());
					MapRule rule=mapBManager.getDomainBind(hh.getAddress());
					if(rule!=null){
						mapClient=clientManager.getMapClient(rule.getUserId());
						if(mapClient!=null){
							try {
								mapClient.getProcess().sendMapConnectRequestMessage(rule.getMapId(),connectId,rule.getDstAddress(),rule.getDstPort(),MapRule.type_single_port);
								needClose=false;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}else{
			rule=mapBManager.getSingelPortBind(port);
			if(rule!=null){
				mapClient=clientManager.getMapClient(rule.getUserId());
				if(mapClient!=null){
					try {
						mapClient.getProcess().sendMapConnectRequestMessage(rule.getMapId(),connectId,null,-1,MapRule.type_single_port);
						needClose=false;
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			}
			
		}
		return needClose;
	}
	
	 public static String byteArray2String(byte[] data,int length){
		 StringBuffer sb=new StringBuffer();
		 for(int i=0;i<length;i++){
			 byte b=data[i];
			sb.append(Long.toHexString(b));
		 }
		 return sb.toString();
	 }

	class HttpHost{
		String address;
		int port=80;
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}

	}

	HttpHost readHost(String data){
		HttpHost hh=new HttpHost();
		String host=null;
		data=data.replaceAll("\r", "");
		data=data.replaceAll(" ", "");
		String[] ls=data.split("\n");
		for(String l:ls){
			if(l.startsWith("Host:")){
				String s1=l.substring(5);
				int index2=s1.indexOf(":");
				if(index2>-1){
					int port=Integer.parseInt(s1.substring(index2+1));
					hh.setPort(port);
					s1=s1.substring(0,index2);
				}
				host=s1;
				hh.setAddress(host);
				//System.out.println("ddd "+s1);
			}
		}
		return hh;
	}

	void close(){
		synchronized (syn_close) {
			if(!closed){
				closed=true;
				try {
					socketB.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				MapServer.get().getMapBManager().removeProcess(connectId);
				preReadData=null;
				mapServer.getMapBManager().onMapClose(this);
			}
		}
	}
	
	void close_from_a(){
		synchronized (syn_close) {
			close();
		}
	}

	public long getConnectId() {
		return connectId;
	}

	public void setConnectId(long connectId) {
		this.connectId = connectId;
	}

	public Socket getSocketB() {
		return socketB;
	}

	public void setSocketB(Socket socketB) {
		this.socketB = socketB;
	}

	public InputStream getIsB() {
		return isB;
	}

	public void setIsB(InputStream isB) {
		this.isB = isB;
	}

	public OutputStream getOsB() {
		return osB;
	}

	public void setOsB(OutputStream osB) {
		this.osB = osB;
	}

	public byte[] getPreReadData() {
		return preReadData;
	}

	public void setPreReadData(byte[] preReadData) {
		this.preReadData = preReadData;
	}

	public int getPreReadDataLength() {
		return preReadDataLength;
	}

	public void setPreReadDataLength(int preReadDataLength) {
		this.preReadDataLength = preReadDataLength;
	}
	
	public MapRule getRule() {
		return rule;
	}

	public void setRule(MapRule rule) {
		this.rule = rule;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
