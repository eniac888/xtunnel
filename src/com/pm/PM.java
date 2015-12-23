// Copyright (c) 2015 D1SM.net

package com.pm;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PM {
	
	public static ThreadPoolExecutor es;
	
	static{
		SynchronousQueue queue = new SynchronousQueue();
		ThreadPoolExecutor executor = new ThreadPoolExecutor(100, Integer.MAX_VALUE, 10*1000, TimeUnit.MILLISECONDS, queue); 
		es=executor;
	}
	
	public static void exec(Runnable run){
		es.execute(run);
	}
	
}
