// Copyright (c) 2015 D1SM.net

package com.pm;

public class PipeEvent {
	
	StreamPipe pipe;
	
	int type=0;
	
	public static int type_pipe_data=10;
	
	PipeEvent(StreamPipe pipe,int type){
		this.pipe=pipe;
	}

	public StreamPipe getPipe() {
		return pipe;
	}

	public void setPipe(StreamPipe pipe) {
		this.pipe = pipe;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
