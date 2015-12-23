// Copyright (c) 2015 D1SM.net

package com.pm.bean;

import java.io.Serializable;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;



public class MapRule implements Serializable{
	
	private static final long serialVersionUID = -7368564104699452865L;

	public static int protocal_TCP=1;

	public static int protocal_HTTP=2;
	
	public static int protocal_UDP=3;
	
	public static int type_single_port=1;
	
	public static int type_web=3;
	
	public static String type_string_single_port="单端口";
				
	public static String type_string_web="网站映射";
	
	public static String type_string_all_port="全端口";
		
	String dstAddress;
	
	int dstPort;
	
	int protocal=protocal_TCP;
	
	String userId;
	
	int mapId;
	
	int port;
	
	int type;
		
	MapServerBean mapserverbean;
	
	int mapserverId;
	
	String domainName;
	
	String name;
	
	String domainNamePrefix;
	
	String wanAddress="";
	
	String wanAddress_get="";
	
	String lanAddress="";
	
	String typeString;
	
	String customAddress;
	
	public JSONObject getJSONObject(){
		JSONObject ho=new JSONObject();
		ho.put(Constant.Key_DstAddress, dstAddress);
		ho.put(Constant.Key_DstPort, dstPort);
		ho.put(Constant.Key_Port, port);
		ho.put(Constant.Key_MapId, mapId);
		ho.put(Constant.Key_UserId, userId);
		ho.put(Constant.Key_Type, type);
		ho.put(Constant.Key_MapServerId, mapserverId);
		ho.put(Constant.Key_DomainName, domainName);
		ho.put(Constant.Key_Name, name);
		ho.put(Constant.Key_DomainNamePrefix, domainNamePrefix);
		if(mapserverbean!=null){
			JSONObject hm=mapserverbean.getJSONObject();
			ho.put(Constant.Key_MapServer, hm);
		}
		return ho;
	}
	
	public void init(JSONObject ho){
		dstAddress=ho.getString(Constant.Key_DstAddress);
		dstPort=ho.getIntValue(Constant.Key_DstPort);
		port=ho.getIntValue(Constant.Key_Port);
		mapId=ho.getIntValue(Constant.Key_MapId);
		userId=ho.getString(Constant.Key_UserId);
		type=ho.getIntValue(Constant.Key_Type);
		mapserverId=ho.getIntValue(Constant.Key_MapServerId);
		domainName=ho.getString(Constant.Key_DomainName);
		name=ho.getString(Constant.Key_Name);
		domainNamePrefix=ho.getString(Constant.Key_DomainNamePrefix);
		if(mapserverbean==null){
			mapserverbean=new MapServerBean();
			mapserverbean.init(ho.getJSONObject(Constant.Key_MapServer));
		}
		if(type==MapRule.type_single_port){
			typeString=MapRule.type_string_single_port;
			wanAddress=mapserverbean.
					getDomainName()+":"+getPort();
			lanAddress=getDstAddress()+":"+getDstPort();
		}else if(type==MapRule.type_web){
			typeString=MapRule.type_string_web;
			wanAddress=getWanWebAddress(this);
			if(mapserverbean.getWebPort()!=80){
				wanAddress+=(":"+mapserverbean.getWebPort());
			}
			lanAddress=getDstAddress();
			if(getDstPort()!=80){
				lanAddress+=(":"+getDstPort());
			}
		}
		
		if(domainName!=null&&!domainName.trim().equals("")){
			if(type==MapRule.type_single_port){
				customAddress=domainName+":"+getPort();
			}else if(type==MapRule.type_web){
				customAddress=domainName;
				if(mapserverbean.getWebPort()!=80){
					customAddress+=(":"+mapserverbean.getWebPort());
				}
			}
		}
	}
	
	public static String getWanWebAddress(MapRule rule){
		MapServerBean mp=rule.getMapserver();
		String wanAddress=rule.getDomainNamePrefix()+"."+mp.getDomainName();
		
		return wanAddress;
	}
	
	public String getDstAddress() {
		return dstAddress;
	}

	public void setDstAddress(String dstAddress) {
		this.dstAddress = dstAddress;
	}

	public int getDstPort() {
		return dstPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

	public int getProtocal() {
		return protocal;
	}

	public void setProtocal(int protocal) {
		this.protocal = protocal;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public MapServerBean getMapserver() {
		return mapserverbean;
	}

	public void setMapserver(MapServerBean mapserver) {
		this.mapserverbean = mapserver;
	}

	public int getMapserverId() {
		return mapserverId;
	}

	public void setMapserverId(int mapserverId) {
		this.mapserverId = mapserverId;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDomainNamePrefix() {
		return domainNamePrefix;
	}

	public void setDomainNamePrefix(String domainNamePrefix) {
		this.domainNamePrefix = domainNamePrefix;
	}

	public String getWanAddress() {
		return wanAddress;
	}

	public void setWanAddress(String wanAddress) {
		this.wanAddress = wanAddress;
	}

	public String getLanAddress() {
		return lanAddress;
	}

	public void setLanAddress(String lanAddress) {
		this.lanAddress = lanAddress;
	}

	public String getTypeString() {
		return typeString;
	}

	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}

	public String getWanAddress_get() {
		return wanAddress_get;
	}

	public void setWanAddress_get(String wanAddress_get) {
		this.wanAddress_get = wanAddress_get;
	}

	public String getCustomAddress() {
		return customAddress;
	}

	public void setCustomAddress(String customAddress) {
		this.customAddress = customAddress;
	}

}
