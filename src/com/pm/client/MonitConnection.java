// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

import com.pm.Tools;
import com.pm.server.ConnectionProcessor;

public class MonitConnection {

	boolean connected;

	Thread mainThread;

	Thread readThread;

	Thread sendThread;

	String address;

	int port;

	Socket socket=null;

	boolean connecting=false;

	DataInputStream dis;

	DataOutputStream dos;

	List<ConnectionProcessor> processorList;

	MonitConnection conn;

	boolean clientSocket=true;

	public long protocalId;

	boolean protocallError=false;
	
	boolean run=true;
	
	boolean forReconnect=false;

	public MonitConnection(long protocalId,String address,int port){
		this.protocalId=protocalId;
		this.address=address;
		this.port=port;
		processorList=new Vector<ConnectionProcessor>();
		conn=this;
		mainThread=new Thread(){
			public void run(){
				connect();
			}
		};
		mainThread.start();
	}

	public MonitConnection(Socket socket){
		clientSocket=false;
		this.socket=socket;
		try {
			OutputStream os=socket.getOutputStream();
			dos=new DataOutputStream(os);
			InputStream is=socket.getInputStream();
			dis=new DataInputStream(is);
		} catch (IOException e) {
			e.printStackTrace();
			onReadWriteException();
		}
		startRead();
	}
	
	public void close(){
		run=false;
		release();
	}
	
	public void reconnect(){
		forReconnect=true;
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			connect();
		}
	}

	public void addProcessor(ConnectionProcessor processor){
		processorList.add(processor);
	}

	public boolean isConnected(){
		return connected;
	}

	public boolean isConnecting(){
		return connecting;
	}

	private synchronized void onReadWriteException(){
		if(!forReconnect&&connected){
			connected=false;
			release();
			readThread.interrupt();
			for(ConnectionProcessor listener:processorList){
				listener.onConnectClosed(this);
			}
			if(clientSocket){
				if(!connecting&!protocallError&&run){
					connect();
				}
			}
		}
		forReconnect=false;
	}

	public void release(){
		if(socket!=null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(dos!=null){
			try {
				dos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(dis!=null){
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void startRead(){
		readThread=new Thread(){
			public void run(){
				while(run){
					try {
						int len=dis.readInt();
						byte[] data=new byte[len];
						dis.readFully(data);
						for(ConnectionProcessor listener:processorList){
							listener.onReveiveData(conn, data);
						}
					} catch (IOException e) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) {
							//e1.printStackTrace();
						}
						//e.printStackTrace();
						onReadWriteException();
						break;
					}
				}
			}
		};
		readThread.start();
	}

	synchronized public void sendData(byte[] data) throws Exception{
		if(connected&&run){
			try {
				dos.writeInt(data.length);
				dos.write(data);
			} catch (IOException e) {
				//e.printStackTrace();
				onReadWriteException();
				throw e;
			}
		}else{
			throw new IOException("未连接");
		}
	}

	synchronized private void connect(){		

		if(!connecting){
			connecting=true;
			new Thread(){
				public void run(){
					while(run){
						try {
							System.out.println("连接... "+address+" "+port);
							socket=new Socket(address,port);
							Tools.setSocket(socket);
							OutputStream os=socket.getOutputStream();
							dos=new DataOutputStream(os);
							InputStream is=socket.getInputStream();
							dis=new DataInputStream(is);
							Tools.checkProtocal(protocalId, dos, dis);
							connecting=false;
							connected=true;
							System.out.println("连接成功");
							break;
						} catch (Exception e) {
							//e.printStackTrace();
							System.out.println("连接失败");
							release();
							try {
								Thread.sleep(5*1000);
							} catch (InterruptedException e1) {
								//e1.printStackTrace();
							}
						}
					}
					if(connected&&run){
						for(ConnectionProcessor listener:processorList){
							listener.onConnected(conn);
						}
						startRead();
					}
				}
			}.start();
		}
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
