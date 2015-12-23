// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.util.ArrayList;
import java.util.List;

import com.pm.bean.MapRule;

public class UserConfig {
	
	String userId;
	
	ArrayList<MapRule> mapRuleList=new ArrayList<MapRule>();

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ArrayList<MapRule> getMapRuleList() {
		return mapRuleList;
	}

	public void setMapRuleList(ArrayList<MapRule> mapRuleList) {
		this.mapRuleList = mapRuleList;
	}

}
