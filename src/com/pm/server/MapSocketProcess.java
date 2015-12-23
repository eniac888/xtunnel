// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.PipeEvent;
import com.pm.PipeListener;
import com.pm.StreamPipe;
import com.pm.Tools;

public class MapSocketProcess implements PipeListener{

	Socket socketA;

	Thread mainThread;

	DataInputStream disA;

	DataOutputStream dosA;

	Random ran=new Random();

	MapServer mapServer;

	DataInputStream disB;

	DataOutputStream dosB;

	Socket socketB;

	MapBSocketProcess processB;

	boolean closed=false;

	InputStream isB;

	OutputStream osB;

	int bufferSize=100*1024;
	
	MapSocketProcess(MapServer mapServer,Socket socketA,DataInputStream disA,DataOutputStream dosA){
		this.mapServer=mapServer;
		this.socketA=socketA;
		this.disA=disA;
		this.dosA=dosA;
	}

	void process(){
		try {
			JSONObject hm=Tools.readMessage(disA);
			String type=hm.getString(Constant.Key_Type);
			String userId=hm.getString(Constant.Key_UserId);
			JSONObject sendMessage=null;
			if(type.equals(Constant.Type_MapConnectMessage)){
				long mapId=hm.getLong(Constant.Key_MapId);
				long connectId=hm.getLong(Constant.Key_MapConnectId);
				sendMessage=ServerMessageFactory.getMapConnectMessageMessage2(userId,Constant.code_Success);

				//Tools.sendMessage(sendMessage, dosA);
				//�Խ�
				processB=mapServer.getMapBManager().getMapBSocketProcess(connectId);
				if(processB==null){
					throw new Exception("processB==null");
				}
				mapServer.getMapBManager().removeProcess(connectId);
				socketB=processB.getSocketB();
				isB=processB.getIsB();
				osB=processB.getOsB();

				disB=new DataInputStream(isB);
				dosB=new DataOutputStream(osB);

				MapClient client=mapServer.getMapClientManager().getMapClient(userId);

				StreamPipe pipe1=null;

				pipe1=new StreamPipe(disA,dosB,bufferSize);
				pipe1.addListener(this);
				

				StreamPipe pipe2=null;
				if(processB.getPreReadData()!=null){
					pipe2=new StreamPipe(disB,dosA,bufferSize,processB.getPreReadData(),processB.getPreReadDataLength());
				}else{
					pipe2=new StreamPipe(disB,dosA,bufferSize);
				}
				pipe2.addListener(this);
				pipe2.setUserId(client.getUserId());
				
				pipe1.setRule(processB.getRule());
				pipe2.setRule(processB.getRule());
				
				pipe1.addListener(this);
				pipe2.addListener(this);
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			close();
		}finally{
			
		}

	}

	void close(){
		if(!closed){
			closed=true;
			if(disA!=null){
				try {
					disA.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(dosA!=null){
				try {
					dosA.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(disB!=null){
				try {
					disA.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(dosB!=null){
				try {
					dosB.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(processB!=null){
				processB.close_from_a();
			}
			if(socketA!=null){
				try {
					socketA.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(socketB!=null){
				try {
					socketB.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void pipeClose() {
		close();
	}

	@Override
	public void onPipeEvent(PipeEvent event) {
		
	}

}
