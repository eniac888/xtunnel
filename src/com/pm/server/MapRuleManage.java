// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.Tools;
import com.pm.bean.MapRule;
import com.pm.bean.MapServerBean;

public class MapRuleManage {
	
	HashMap<String, UserConfig> userTable=new HashMap<String, UserConfig>();
	
	HashMap<String, MapRule> domainNameTable=new HashMap<String, MapRule>();
	
	HashMap<String, MapRule> domainNamePrefixTable=new HashMap<String, MapRule>();
	
	HashMap<Integer, MapRule> portTable=new HashMap<Integer, MapRule>();

	HashMap<Integer, MapRule> ruleTable=new HashMap<Integer, MapRule>();
	
	MapServer mapServer;
	
	MapRuleManage(MapServer mapServer){
		this.mapServer=mapServer;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void init() throws Exception{
		File[] fs=new File("user").listFiles();
		if(fs!=null){
			for(File f:fs){
				UserConfig userConfig=loadUserConfig(f.getAbsolutePath());
				userTable.put(userConfig.getUserId(), userConfig);
			}
		}
	}
	
	UserConfig loadUserConfig(String path) throws Exception{
		String content=Tools.readFileData(path);
		JSONObject user_config=JSONObject.parseObject(content);
		JSONArray rules=user_config.getJSONArray("rule");
		ArrayList<MapRule> mapRuleList=new ArrayList<MapRule>();
		for (int i = 0; i < rules.size(); i++) {
			MapRule mapRule=new MapRule();
			mapRule.setMapserver(mapServer.getMapServerBean());
			mapRule.init((JSONObject) rules.get(i));
			ruleTable.put(mapRule.getMapId(), mapRule);
			if(mapRule.getType()==MapRule.type_web){
				domainNameTable.put(mapRule.getDomainName(), mapRule);
				domainNamePrefixTable.put(mapRule.getDomainNamePrefix(), mapRule);
			}
			portTable.put(mapRule.getPort(), mapRule);
			mapRuleList.add(mapRule);
		}
		UserConfig userConfig=new UserConfig();
		String userId=new File(path).getName().replace(".json", "");
		userConfig.setUserId(userId);
		userConfig.setMapRuleList(mapRuleList);
		return userConfig;
	}
	
	void saveUserConfig(UserConfig userConfig) throws Exception{
		JSONObject user_config=new JSONObject();
		JSONArray ruleList_json=new JSONArray();
		user_config.put("rule", ruleList_json);
		
		for(MapRule rule:userConfig.getMapRuleList()){
			ruleList_json.add(rule.getJSONObject());
		}
		String path=new File("user/"+userConfig.getUserId()).getAbsolutePath()+".json";
		Tools.saveFile(user_config.toString(), path);
	}
	
	public MapRule removeMapRule(int mapId,String userId) {
		UserConfig userConfig=userTable.get(userId);
		MapRule rule=null;
		if(userConfig!=null){
			rule=ruleTable.remove(mapId);
			if(rule!=null){
				userConfig.getMapRuleList().remove(rule);
				if(rule.getType()==MapRule.type_web){
					domainNameTable.remove(rule.getDomainName());
					domainNamePrefixTable.remove(rule.getDomainNamePrefix());
				}
			}
			try {
				saveUserConfig(userConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rule;
	}
	
	MapRule getMapRuleById(int id){
		return ruleTable.get(id);
	}
	
	public ArrayList<MapRule> getMapRule(String userId) throws Exception {
		ArrayList<MapRule> mapRuleList=null;
		UserConfig userConfig=userTable.get(userId);
		if(userConfig!=null){
			mapRuleList=userConfig.getMapRuleList();
		}else{
			mapRuleList=new ArrayList<MapRule>();
		}
		return mapRuleList;
	}
	
	public void updateMapRule(int mapId,String userId,int port,String dstAddress,int dstPort,int protocal,
			int mapserverId,int mapType,String domainName,String name,String domainNamePrefix) throws Exception {
		
		UserConfig userConfig=userTable.get(userId);
		if(userConfig==null){
			userConfig=new UserConfig();
			userConfig.setUserId(userId);
			userTable.put(userId, userConfig);
		}
		
		MapRule mapRule=ruleTable.get(mapId);
		if(mapRule!=null){
			
			if(mapRule.getType()==MapRule.type_web){
				domainNameTable.remove(mapRule.getDomainName());
				domainNamePrefixTable.remove(mapRule.getDomainNamePrefix());
			}
			
			mapRule.setMapserver(mapServer.getMapServerBean());
			mapRule.setDomainName(domainName);
			mapRule.setDomainNamePrefix(domainNamePrefix);
			mapRule.setDstAddress(dstAddress);
			mapRule.setDstPort(dstPort);
			mapRule.setPort(port);
			mapRule.setType(mapType);
			mapRule.setName(name);
			mapRule.setProtocal(protocal);
			mapRule.setUserId(userId);
			
			if(mapRule.getType()==MapRule.type_web){
				domainNameTable.put(mapRule.getDomainName(), mapRule);
				domainNamePrefixTable.put(mapRule.getDomainNamePrefix(), mapRule);
			}
			
			saveUserConfig(userConfig);
		}
	}
	
	synchronized public void addMapRule(int mapId,String userId,int port,String dstAddress,int dstPort,int protocal,
			int mapserverId,int mapType,String domainName,String name,String domainNamePrefix) throws Exception {
		
		UserConfig userConfig=userTable.get(userId);
		if(userConfig==null){
			userConfig=new UserConfig();
			userConfig.setUserId(userId);
			userTable.put(userId, userConfig);
		}
		if(!ruleTable.containsKey(mapId)){
			MapRule mapRule=new MapRule();
			mapRule.setMapserver(mapServer.getMapServerBean());
			mapRule.setMapId(mapId);
			mapRule.setUserId(userId);
			mapRule.setPort(port);
			mapRule.setDstAddress(dstAddress);
			mapRule.setDstPort(dstPort);
			mapRule.setDomainName(domainName);
			mapRule.setDomainNamePrefix(domainNamePrefix);
			mapRule.setType(mapType);
			mapRule.setName(name);
			mapRule.setProtocal(protocal);
			userConfig.getMapRuleList().add(mapRule);
			ruleTable.put(mapId, mapRule);
			domainNameTable.put(mapRule.getDomainName(), mapRule);
			domainNamePrefixTable.put(mapRule.getDomainNamePrefix(), mapRule);
			portTable.put(mapRule.getPort(), mapRule);
			
			saveUserConfig(userConfig);
		}
	}
	
	public MapRule getMapRuleDomainName(String name) throws Exception {
		return domainNameTable.get(name);
	}
	
	public MapRule getMapRuleDomainNamePrefix(String prefix) throws Exception {
		return domainNamePrefixTable.get(prefix);
	}
	
	public MapRule getMapRule_port(int port) throws Exception {
		return portTable.get(port);
	}
	
	public int  getUnusedPort() throws Exception{
		Random ran=new Random();
		int port=0;
		for(int i=0;i<10;i++){
			int n=10000+ran.nextInt(10000);
			MapRule r=getMapRule_port(n);
			if(r==null){
				port=n;
				break;
			}
		}
		return port;
	}
	
}
