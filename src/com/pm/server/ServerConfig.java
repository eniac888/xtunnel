// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.util.ArrayList;

public class ServerConfig {

	int listenPort=180;
	
	ArrayList<String> passwordList=new ArrayList<String>();
	
	public int getListenPort() {
		return listenPort;
	}

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	public ArrayList<String> getPasswordList() {
		return passwordList;
	}

	public void setPasswordList(ArrayList<String> passwordList) {
		this.passwordList = passwordList;
	}
	
}
