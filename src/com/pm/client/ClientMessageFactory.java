// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.util.List;
import java.util.Vector;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.bean.MapRule;
import com.pm.bean.MapServerBean;

public class ClientMessageFactory {
	
	public static List<MapRule>  getRuleListJSONObject(List<JSONObject> ruleList){
		List<MapRule> ruleList2=new Vector<MapRule>();
		for(JSONObject ho:ruleList){
			MapRule r=new MapRule();
			r.init(ho);
			ruleList2.add(r);
		}
		return ruleList2;
	}
	
	public static List<MapServerBean>  getMapServerListJSONObject(List<JSONObject> ruleList){
		List<MapServerBean> ruleList2=new Vector<MapServerBean>();
		for(JSONObject ho:ruleList){
			MapServerBean r=new MapServerBean();
			r.init(ho);
			ruleList2.add(r);
		}
		return ruleList2;
	}
	
	public static JSONObject getGetMapRuleListMessage(String userId,String password){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_GetMapRuleListMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Password, password);
		return hm;
	}
	
	public static JSONObject getAddMapRuleMessage(
			String userId,String password,
			String dstAddress,int dstPort,int protocal,
			int mapType,String domainName,String name,
			String domainNamePrefix){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_AddMapRuleMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Password, password);
		hm.put(Constant.Key_DstAddress, dstAddress);
		hm.put(Constant.Key_DstPort, dstPort);
		hm.put(Constant.Key_Protocal, protocal);
		hm.put(Constant.Key_MapType, mapType);
		hm.put(Constant.Key_DomainName, domainName);
		hm.put(Constant.Key_Name, name);
		hm.put(Constant.Key_DomainNamePrefix, domainNamePrefix);
		return hm;
	}
	
	public static JSONObject getUpdateMapRuleMessage(
			String userId,String password,
			String dstAddress,int dstPort,int protocal,
			int mapType,String domainName,String name,int mapId,
			String domainNamePrefix){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_UpdateMapRuleMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Password, password);
		hm.put(Constant.Key_DstAddress, dstAddress);
		hm.put(Constant.Key_DstPort, dstPort);
		hm.put(Constant.Key_Protocal, protocal);
		hm.put(Constant.Key_MapType, mapType);
		hm.put(Constant.Key_DomainName, domainName);
		hm.put(Constant.Key_Name, name);
		hm.put(Constant.Key_MapId, mapId);
		hm.put(Constant.Key_DomainNamePrefix, domainNamePrefix);
		
		return hm;
	}
	
	public static JSONObject getRemoveMapRuleMessage(String userId,String password,int mapId){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_RemoveMapRuleMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Password, password);
		hm.put(Constant.Key_MapId, mapId);
		return hm;
	}
	
	public static JSONObject getHeartBeatMessage(String userId){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_HeartBeatMessage);
		hm.put(Constant.Key_UserId, userId);
		return hm;
	}
	
	public static JSONObject getLoginMessage(String userId,String pwd){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_LoginMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Password, pwd);
		return hm;
	}
	
	public static JSONObject getMapConnectMessageMessage(String userId,long mapId,long mapConnectId){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_MapConnectMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_MapId, mapId);
		hm.put(Constant.Key_MapConnectId, mapConnectId);
		return hm;
	}
}
