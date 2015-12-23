// Copyright (c) 2015 D1SM.net

package com.pm;

public interface PipeListener {

	void pipeClose();
	
	void onPipeEvent(PipeEvent event);
	
}
