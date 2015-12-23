// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.util.List;
import java.util.Vector;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.bean.MapRule;


public class ServerMessageFactory {
	
	public static JSONObject getMapRuleListMessage(String userId,String code,List<MapRule> ruleList){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_MapRuleListMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Code, code);
		hm.put(Constant.Key_MapRule,  getRuleListJSONObject(ruleList));
		return hm;
	}
	
	public static JSONObject getGetMapRuleListMessage(String userId,String password,String protocal){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_GetMapRuleListMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Password, password);
		hm.put(Constant.Key_Protocal, protocal);
		return hm;
	}
	
	public static JSONObject getUpdateRuleMessage2(){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_UpdateMapRuleMessage2);
		return hm;
	}
	
	public static JSONObject getMapRuleMessage(String userId,List<MapRule> ruleList){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_MapRuleMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_MapRule,  getRuleListJSONObject(ruleList));
		return hm;
	}
	
	public static JSONObject getAddRuleMessage2(String userId,String code,List<MapRule> ruleList,String message,int mapId){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_AddMapRuleMessage2);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Code, code);
		hm.put(Constant.Key_Message,message);
		hm.put(Constant.Key_MapRule,  getRuleListJSONObject(ruleList));
		hm.put(Constant.Key_MapId,mapId);
		return hm;
	}
	
	public static JSONObject getRemoveRuleMessage2(String userId,String code,List<MapRule> ruleList){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_AddMapRuleMessage2);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Code, code);
		hm.put(Constant.Key_MapRule, getRuleListJSONObject(ruleList));
		return hm;
	}
	
	public static List<JSONObject>  getRuleListJSONObject(List<MapRule> ruleList){
		List<JSONObject> ruleList2=new Vector<JSONObject>();
		for(MapRule rule:ruleList){
			ruleList2.add(rule.getJSONObject());
		}
		return ruleList2;
	}
	
	public static JSONObject getHeartBeatMessage2(String userId){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_HeartBeatMessage2);
		hm.put(Constant.Key_UserId, userId);
		return hm;
	}
	
	public static JSONObject getLoginMessage2(String userId,String code,long sessionId){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_LoginMessage2);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Code, code);
		hm.put(Constant.Key_SessionId, sessionId);
		return hm;
	}
	
	public static JSONObject getMapConnectRequestMessage(String userId,long mapId,long mapConnectId,String dstAddress,int dstPort,int type){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_MapConnectRequestMessage);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_MapId, mapId);
		hm.put(Constant.Key_MapConnectId, mapConnectId);
		hm.put(Constant.Key_DstAddress, dstAddress);
		hm.put(Constant.Key_DstPort, dstPort);
		hm.put(Constant.Key_MapType, type);
		return hm;
	}
	
	public static JSONObject getMapConnectMessageMessage2(String userId,String code){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_MapConnectMessage2);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Code, code);
		return hm;
	}
	
	public static JSONObject getMapGConnectMessageMessage2(String userId,String code){
		JSONObject hm=new JSONObject();
		hm.put(Constant.Key_Type, Constant.Type_MapConnectMessage2);
		hm.put(Constant.Key_UserId, userId);
		hm.put(Constant.Key_Code, code);
		return hm;
	}
	
}
