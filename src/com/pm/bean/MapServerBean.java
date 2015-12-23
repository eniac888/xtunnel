// Copyright (c) 2015 D1SM.net

package com.pm.bean;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;

public class MapServerBean {
	
	int id;
	
	String address;
	
	int port;
	
	String name="";
	
	String description;
	
	String domainName;
	
	int index;
	
	int webPort=80;
	
	public JSONObject getJSONObject(){
		JSONObject ho=new JSONObject();
		ho.put(Constant.Key_Id, id);
		ho.put(Constant.Key_Address, address);
		ho.put(Constant.Key_Port, port);
		ho.put(Constant.Key_Name, name);
		ho.put(Constant.Key_Description, description);
		ho.put(Constant.Key_DomainName, domainName);
		ho.put(Constant.Key_WebPort, webPort);
		return ho;
	}
	
	public void init(JSONObject ho){
		id=ho.getIntValue(Constant.Key_Id);
		address=ho.getString(Constant.Key_Address);
		port=ho.getIntValue(Constant.Key_Port);
		name=ho.getString(Constant.Key_Name);
		description=ho.getString(Constant.Key_Description);
		domainName=ho.getString(Constant.Key_DomainName);
		index=ho.getIntValue(Constant.Key_Index);
		webPort=ho.getIntValue(Constant.Key_WebPort);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getWebPort() {
		return webPort;
	}

	public void setWebPort(int webPort) {
		this.webPort = webPort;
	}
	
}
