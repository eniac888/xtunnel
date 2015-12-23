// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.util.HashMap;
import java.util.Iterator;

public class MapClientManager {
	
	HashMap<String, MapClient> mapClientTable=new HashMap<String, MapClient>();
	
	synchronized void addMapClient(String userId,MapClient mapClient){
		mapClientTable.put(userId, mapClient);
	}
	
	synchronized MapClient getMapClient(String userId){
		return mapClientTable.get(userId);
	}
	
	synchronized void removeMapClient(String userId){
		mapClientTable.remove(userId);
	}
	
}
