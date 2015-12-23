// Copyright (c) 2015 D1SM.net

package com.pm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import com.pm.bean.MapRule;
import com.pm.server.DataEditor;
import com.pm.server.EditResult;

public class StreamPipe {
	
	DataInputStream is;
	
	DataOutputStream os;
			
	boolean closed=false;
		
	long lastResetTime;
	
	int port=0;
	
	boolean autoClose=false;
	
	String userId="";
		
	byte[] preReadData;
	
	int preReadDataLength;
	
	DataEditor dataEditor=null;
	
	Socket socketA,socketB;
		
	boolean writing=false;
	
	int BUF_SIZE;
	
	MapRule rule;
	
	ArrayList<PipeListener> listeners=new ArrayList<PipeListener>();
	
	StreamPipe pipe;
		
	Object syn_close=new Object();
	
	boolean vip=false;
	
	Thread writeThread;
	
	LinkedBlockingQueue<byte[]> packetBuffer=new LinkedBlockingQueue<byte[]>();

	
	{
		pipe=this;
	}
	
	public StreamPipe(final DataInputStream is,final DataOutputStream os,final int BUF_SIZE){
		this(is, os, BUF_SIZE, null, 0);
	}
	
	public StreamPipe(final DataInputStream is,final DataOutputStream os,int BUF_SIZE1,final byte[] preReadData,final int preReadDataLength){
		this.preReadData=preReadData;
		this.is=is;
		this.os=os;
		BUF_SIZE=BUF_SIZE1;
		Runnable thread=new Runnable(){
			
			public void run(){
				byte[] data=new byte[BUF_SIZE];
				int len=0;
				try {
					if(preReadData!=null){
						os.write(preReadData,0,preReadDataLength);
					}
					while((len=is.read(data))>0){
						fireEvent(new PipeEvent(pipe, PipeEvent.type_pipe_data));
						if(dataEditor!=null){
							EditResult result=dataEditor.edit(data,len);
							data=result.getData();
							len=result.getLength();
						}
						os.write(data,0,len);
					}
				} catch (Exception e) {
					//e.printStackTrace();
				}finally{
					close();
				}
			}
		};
		PM.exec(thread);
	}
	
	 public static String byteArray2String(byte[] data,int length){
		 StringBuffer sb=new StringBuffer();
		 for(int i=0;i<length;i++){
			 byte b=data[i];
			sb.append(Long.toHexString(b));
		 }
		 return sb.toString();
	 }
	
	void addPipeListener(PipeListener listener){
		listeners.add(listener);
	}
	
	void fireEvent(PipeEvent event){
		for(PipeListener l:listeners){
			l.onPipeEvent(event);
		}
	}
	
	void close(){
		synchronized (syn_close) {
			if(!closed){
				closed=true;
				if(socketA!=null){
					try {
						socketA.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
					try {
						socketA.getInputStream().close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
					try {
						socketA.getOutputStream().close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
				if(socketB!=null){
					try {
						socketB.close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
					try {
						socketB.getInputStream().close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
					try {
						socketB.getOutputStream().close();
					} catch (IOException e) {
						//e.printStackTrace();
					}
				}
				if(is!=null){
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(os!=null){
					try {
						os.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				fireClose();
			}
		}
	}
	
	public void addListener(PipeListener listener){
		listeners.add(listener);
	}
	
	void fireClose(){
		for(PipeListener listener:listeners){
			listener.pipeClose();
		}
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public DataEditor getDataEditor() {
		return dataEditor;
	}

	public void setDataEditor(DataEditor dataEditor) {
		this.dataEditor = dataEditor;
	}

	public Socket getSocketA() {
		return socketA;
	}

	public void setSocketA(Socket socketA) {
		this.socketA = socketA;
	}

	public void setSocketB(Socket socketB) {
		this.socketB = socketB;
	}
	
	public MapRule getRule() {
		return rule;
	}

	public void setRule(MapRule rule) {
		this.rule = rule;
	}

	public boolean isVip() {
		return vip;
	}

	public void setVip(boolean vip) {
		this.vip = vip;
	}
	
}
