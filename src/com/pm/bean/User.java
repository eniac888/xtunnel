// Copyright (c) 2015 D1SM.net

package com.pm.bean;

import java.io.Serializable;
import java.sql.Timestamp;

public class User implements Serializable{

	private static final long serialVersionUID = 8157873734638225060L;

	String userId;
	
	Timestamp activeTime;
	
	Timestamp createTime;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Timestamp getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(Timestamp activeTime) {
		this.activeTime = activeTime;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

}
