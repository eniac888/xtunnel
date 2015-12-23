// Copyright (c) 2015 D1SM.net

package com.pm.server;

public class MapClient {
	
	private String userId;
	
	private ControlSocketProcess process;
	
	long sessionId;
	
	MapClient(String userId,ControlSocketProcess process){
		this.userId=userId;
		this.process=process;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public ControlSocketProcess getProcess() {
		return process;
	}

	public void setProcess(ControlSocketProcess process) {
		this.process = process;
	}
	
	public long getSessionId() {
		return sessionId;
	}

	public void setSessionId(long sessionId) {
		this.sessionId = sessionId;
	}

}
