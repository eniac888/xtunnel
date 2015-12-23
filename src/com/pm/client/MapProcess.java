// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.pm.Constant;
import com.pm.PM;
import com.pm.PipeEvent;
import com.pm.PipeListener;
import com.pm.StreamPipe;
import com.pm.Tools;
import com.pm.bean.MapRule;
import com.pm.client.ui.PMClientUI;

public class MapProcess  implements PipeListener{
	
	Socket socketB,socketA;
	
	DataInputStream disB;
	
	DataOutputStream dosB;
	
	DataInputStream disA;
	
	DataOutputStream dosA;
		
	long mapConnectId;
	
	PMClientUI ui;
	
	public static InetAddress localIP;
	
	MapRule mapRule;
	
	String dstAddress;
	
	int dstPort;
	
	static {
		try {
			localIP=InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	MapProcess(PMClientUI ui,long mapConnectId,MapRule mapRule,String dstAddress,int dstPort){
		this.ui=ui;
		this.mapConnectId=mapConnectId;
		this.mapRule=mapRule;
		this.dstAddress=dstAddress;
		this.dstPort=dstPort;
		Runnable run=new Runnable(){
			public void run(){
				process();
			}
		};
		PM.exec(run);
	}
	
	void process(){
		try {
			socketB=new Socket(ui.getConfig().getServerAddress(),ui.getConfig().getServerPort());
			Tools.setSocket(socketB);
			disB=new DataInputStream(socketB.getInputStream());
			dosB=new DataOutputStream(socketB.getOutputStream());
			Tools.writeProtocal(Constant.Protocal_Map, dosB);
			Tools.checkProtocal(Constant.Protocal_Map, disB);

			Tools.sendMessage(ClientMessageFactory.getMapConnectMessageMessage(
					ui.
					getConfig().getUserId(), 
					mapRule.getMapId(), 
					mapConnectId), dosB);
			String address=mapRule.getDstAddress();
			if(dstAddress!=null){
				address=dstAddress;
			}
			int port=mapRule.getDstPort();
			if(dstPort>0){
				port=dstPort;
			}
			//System.out.println("转向到 "+address+" "+port);
			
			socketA=new Socket(address,port);
			Tools.setSocket(socketA);
			//System.out.println("转向到 "+address+" "+port+" 成功");
			disA=new DataInputStream(socketA.getInputStream());
			dosA=new DataOutputStream(socketA.getOutputStream());
			int bufferSize=100*1024;
			
			StreamPipe pipe1=new StreamPipe(disA,dosB,bufferSize);
			StreamPipe pipe2=new StreamPipe(disB,dosA,bufferSize);
			
			pipe1.setSocketA(socketA);
			pipe1.setSocketB(socketB);
			
			pipe2.setSocketA(socketA);
			pipe2.setSocketB(socketB);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void close(){
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
				disB.close();
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
	

	public void pipeClose() {
		//close();
	}

	public void onPipeEvent(PipeEvent event) {
		
	}

}
