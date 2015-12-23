// Copyright (c) 2015 D1SM.net

package com.pm.server;

import com.pm.client.MonitConnection;

public interface ConnectionProcessor {
	
	public void onConnected(MonitConnection conn);
	
	public void onConnectClosed(MonitConnection conn);
	
	public void onReveiveData(MonitConnection client,byte[] data);
	
}
