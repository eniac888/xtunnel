// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.util.HashMap;
import java.util.List;

import com.pm.bean.MapRule;
import com.pm.client.ui.PMClientUI;

public class MapRuleManage {
	
	HashMap<Integer,MapRule> ruleTable=new HashMap<Integer,MapRule>();
	
	PMClientUI ui;
	
	public MapRuleManage(PMClientUI ui){
		this.ui=ui;
		
	}
	
	synchronized public void setMapRuleList(List<MapRule> list){
		ruleTable.clear();
		if(list!=null){
			for(MapRule rule:list){
				ruleTable.put(rule.getMapId(), rule);
			}
		}
	}
	
	void createNewConnect(int mapRuleId,final long mapConnectId,String dstAddress,int dstPort){
		new MapProcess(ui,mapConnectId,ruleTable.get(mapRuleId),dstAddress,dstPort);
	}
	
}
