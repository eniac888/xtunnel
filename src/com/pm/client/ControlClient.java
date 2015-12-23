// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.bean.MapRule;
import com.pm.client.ui.PMClientUI;

public class ControlClient {
	
	Thread loadRuleThread;
	
		
	PMClientUI ui;
		
	Random ran=new Random();
	
	MapMonit mapMonit=null;
	
	public ControlClient(final PMClientUI ui){
		this.ui=ui;
	}
	
	public List<MapRule> startLoadRule(int selectMapId) throws Exception{
		JSONObject hmsg;
		hmsg = sendRequest(ClientMessageFactory.getGetMapRuleListMessage(getUserId(),getPasswordMd5()));
		String code=hmsg.getString(Constant.Key_Code);
		List<MapRule> ruleList=null;
		List<JSONObject> ruleList_json=null;
		if(code.equals(Constant.code_Success)){
			ruleList_json=(List<JSONObject>) hmsg.get(Constant.Key_MapRule);
			ruleList=ClientMessageFactory.getRuleListJSONObject(ruleList_json);
			ui.setAccountError(false);
			if(mapMonit==null){
				mapMonit=new MapMonit(ui,ui.getConfig().getServerAddress(),ui.getConfig().getServerPort(),MapRule.protocal_TCP);
				mapMonit.start();
			}else{
				//System.out.println("zzz");
				//mapMonit.reconnect();
			}
			ui.updateTcpMapRule(ruleList,selectMapId);
		}else if(code.equals(Constant.code_AccountError)){
			ui.setAccountError(true);
			ui.updateTcpMapRule(new ArrayList<MapRule>(),selectMapId);
		}
		return ruleList;
	}
	
	public JSONObject sendRequest(JSONObject requestMeaaage) throws Exception{
		return sendRequest(requestMeaaage, ui.getConfig().getServerAddress(),ui.getConfig().getServerPort());
	}
	
	public JSONObject addTCPMapRule(String dstAddress,int dstPort,int type,String domainName,String name,String domainNamePrefix) throws Exception{
		JSONObject hmsg;
		hmsg = sendRequest(ClientMessageFactory.getAddMapRuleMessage(getUserId(),getPasswordMd5(),dstAddress,dstPort,MapRule.protocal_TCP,
				type,domainName,name,domainNamePrefix));
		return hmsg;
	}
	
	public JSONObject updateTCPMapRule(String dstAddress,int dstPort,int type,String domainName,String name,int mapId,String domainNamePrefix) throws Exception{
		JSONObject hmsg;
		int code=-1;
		hmsg = sendRequest(ClientMessageFactory.getUpdateMapRuleMessage(getUserId(),getPasswordMd5(),dstAddress,dstPort,MapRule.protocal_TCP,
				type,domainName,name,mapId,domainNamePrefix));
		return hmsg;
	}
	
	public JSONObject removeMapRule(int mapRuleId) throws Exception{
		JSONObject hmsg;
		int code=-1;
		hmsg = sendRequest(ClientMessageFactory.getRemoveMapRuleMessage(getUserId(),getPasswordMd5(),mapRuleId));
		return hmsg;
	}
	
	private String getUserId(){
		return ui.getConfig().getUserId();
	}
	
	private String getPasswordMd5(){
		return ui.getConfig().getPasswordMd5();
	}

	public static JSONObject sendRequest(JSONObject message,String address,int port) throws Exception{
		int n=0;
		byte[] data=message.toJSONString().getBytes("utf-8");
		boolean success=false;
		JSONObject responeMessage=null;
		while(n<1){
			n++;
			Socket socket=null;
			DataOutputStream dos=null;
			DataInputStream dis=null;
			try {
				socket=new Socket(address, port);
				dos=new DataOutputStream(socket.getOutputStream());
				dos.writeLong(Constant.Protocal_Control);
				dis=new DataInputStream(socket.getInputStream());
				long p2=dis.readLong();
				if(p2==Constant.Protocal_Control){
					dos.writeInt(data.length);
					dos.write(data);
					int length=dis.readInt();
					byte[] data_respone=new byte[length];
					dis.readFully(data_respone);
					String respone=new String(data_respone,"utf-8");
					responeMessage=JSONObject.parseObject(respone);
					success=true;
					break;
				}else{
					break;
				}
			}catch (Exception e) {
				//e.printStackTrace();
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e1) {
					//e1.printStackTrace();
				}
			}finally{
				if(socket!=null){
					socket.close();
				}
				if(dis!=null){
					try {
						dis.close();
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
			}
		}
		if(!success){
			throw new Exception("request failed!");
		}
		return responeMessage;
	}

	public MapMonit getMapMonit() {
		return mapMonit;
	}

	public void setMapMonit(MapMonit mapMonit) {
		this.mapMonit = mapMonit;
	}
	
	
}
