// Copyright (c) 2015 D1SM.net

package com.pm.client;

public class MapServerItem {
	
	String address;
	
	int port;
	
	String name;
	
	String id;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String toString(){
		return address;
	}

}
