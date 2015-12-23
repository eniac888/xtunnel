// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

public class PortBean {
	
	int port;
	
	long lastActiveTime;
	
	ServerSocket serverSocket;
	
	HashMap<Long, MapBSocketProcess> processTable=new HashMap<Long, MapBSocketProcess>();
	
	long connectSumUpdateTime;
	
	public void addProcess(MapBSocketProcess process){
		processTable.put(process.getConnectId(), process);
		connectSumUpdateTime=System.currentTimeMillis();
	}
	
	public void  removeProcess(long connectId) {
		processTable.remove(connectId);
		connectSumUpdateTime=System.currentTimeMillis();
	}
	
	void close() throws IOException{
		serverSocket.close();
	}
	
	public int getConnectSum() {
		return processTable.size();
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public long getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public long getConnectSumUpdateTime() {
		return connectSumUpdateTime;
	}

	public void setConnectSumUpdateTime(long connectSumUpdateTime) {
		this.connectSumUpdateTime = connectSumUpdateTime;
	}
	
}
