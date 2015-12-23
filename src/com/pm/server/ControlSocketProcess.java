// Copyright (c) 2015 D1SM.net

package com.pm.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.PM;
import com.pm.Tools;
import com.pm.bean.MapRule;

public class ControlSocketProcess {

	Socket socketA;

	Runnable mainThread;

	DataInputStream disA;

	DataOutputStream dosA;

	Random ran=new Random();

	MapServer mapServer;

	String userId;

	boolean logined=false;
	
	MapClientManager clientManager;
	
	InputStream is;
	
	OutputStream os;

	ControlSocketProcess(MapServer mapServer,Socket socket,InputStream is,
			OutputStream os){
		this.mapServer=mapServer;
		this.socketA=socket;
		this.is=is;
		this.os=os;
		clientManager=mapServer.getMapClientManager();
		mainThread=new Runnable(){

			public void run(){
				process();
			}

		};
		PM.exec(mainThread);
		
	}

	void process(){
		boolean close=true;
		try {
			disA=new DataInputStream(is);
			dosA=new DataOutputStream(os);
			long n=Tools.readProtocal(disA);
			if(n==Constant.Protocal_Control){
				//接收心跳,各种指令
				Tools.writeProtocal(Constant.Protocal_Control, dosA);

				processControl();
			
			}else if(n==Constant.Protocal_Map){
				//转发隧道
				Tools.writeProtocal(Constant.Protocal_Map, dosA);
				MapSocketProcess p=new MapSocketProcess(mapServer,socketA,disA,dosA);
				p.process();
				close=false;
			}
			
		} catch (Exception e2) {
			//e2.printStackTrace();
		}finally{
			if(close){
				close();
			}
		}
	}
	
	void processControl() {
		String type=null;
		try {
		MapRuleManage mapRuleManage=mapServer.getMapRuleManage();
		while(true){
			JSONObject requestMessage;
			
				requestMessage = Tools.readMessage(disA);
			
			type=requestMessage.getString(Constant.Key_Type);
			userId=requestMessage.getString(Constant.Key_UserId);
			long sessionId=-1;
			if(mapServer.getMapBManager()==null){
				return;
			}
			if(logined){
				if(type.equals(Constant.Type_HeartBeatMessage)){
					MapClient client=clientManager.getMapClient(userId);
					if(client!=null){
						JSONObject msg=ServerMessageFactory.getHeartBeatMessage2(userId);
						sendMessage(msg);
					}else{
						break;
					}
				}
			}else{
				JSONObject responeMessage=null;
				String pwd_md5=requestMessage.getString(Constant.Key_Password);
				boolean loginSuccess=false;
				ArrayList<String> pwdList=mapServer.getServerConfig().getPasswordList();
				if(pwdList.size()==0){
					if(pwd_md5.equals("")){
						loginSuccess=true;
					}
				}else{
					if(pwdList.contains(pwd_md5)){
						loginSuccess=true;
					}
					
				}

				if(!loginSuccess){
					responeMessage=ServerMessageFactory.getLoginMessage2(userId,Constant.code_AccountError,sessionId);
					sendMessage(responeMessage);
				}else{
					if(type.equals(Constant.Type_LoginMessage)){
						MapClient mapClient=null;
						logined=true;;
						mapClient=clientManager.getMapClient(userId);
						if(mapClient==null){
							sessionId=Math.abs(ran.nextLong());
							mapClient=new MapClient(userId,this);
							mapClient.setSessionId(sessionId);
						}
						sessionId=mapClient.getSessionId();
						mapClient.setProcess(this);
						responeMessage=ServerMessageFactory.getLoginMessage2(userId,Constant.code_Success,sessionId);
						sendMessage(responeMessage);
						ArrayList<MapRule> ruleList=mapRuleManage.getMapRule(userId);

						clientManager.addMapClient(userId, mapClient);
						onClientLogin(mapClient,userId,ruleList);
					}else if(type.equals(Constant.Type_GetMapRuleListMessage)){
						ArrayList<MapRule> ruleList=mapRuleManage.getMapRule(userId);
						responeMessage=ServerMessageFactory.getMapRuleListMessage(userId,Constant.code_Success,ruleList);
						sendMessage(responeMessage);
						break;
					}else if(type.equals(Constant.Type_AddMapRuleMessage)){
						String message=null;
						String code=Constant.code_Success;
						String dstAddress=requestMessage.getString(Constant.Key_DstAddress);
						int dstPort=requestMessage.getIntValue(Constant.Key_DstPort);
						int protocal=requestMessage.getIntValue(Constant.Key_Protocal);
						int mapType=requestMessage.getIntValue(Constant.Key_MapType);
						String domainName=requestMessage.getString(Constant.Key_DomainName);
						String name=requestMessage.getString(Constant.Key_Name);
						String domainNamePrefix=requestMessage.getString(Constant.Key_DomainNamePrefix);
						domainNamePrefix=domainNamePrefix.replace(" ", "");
						domainNamePrefix=domainNamePrefix.trim().toLowerCase();
						
						
//						if(ruleList.size()>=maxPort){
//							code=Constant.code_Failed;
//							message=name+"最多只能添加"+maxPort+"个映射";
//						}
						
						if(mapType==MapRule.type_web){
							if(domainNamePrefix.trim().equals("")){
								message="输入正确域名前缀";
								code=Constant.code_Failed;
							}else{
								MapRule rule=mapRuleManage.getMapRuleDomainNamePrefix(domainNamePrefix);
								if(rule!=null){
									message="域名前缀已经被占用";
									code=Constant.code_Failed;
								}
							}
							if(domainName.trim().equals("")){
								message+=" 输入正确自定义域名";
							}else{
								MapRule rule=mapRuleManage.getMapRuleDomainName(domainName);
								if(rule!=null){
									message="自定义域名已经被占用";
									code=Constant.code_Failed;
								}
							}
							if(mapServer.getMapServerBean().getDomainName()==null){
								message="服务器必须绑定域名后才能添加网站映射!";
								code=Constant.code_Failed;
							}
						}
						
						MapRule rule_added=null;
						int port=0;
						int mapId=10000+Math.abs(ran.nextInt(90000));
						if(code.equals(Constant.code_Success)){
							if(mapType==MapRule.type_single_port){
								port=mapRuleManage.getUnusedPort();
							}
							mapRuleManage.addMapRule(
									mapId, userId,port, dstAddress, dstPort, protocal,
									0, mapType, domainName,name,domainNamePrefix);
							rule_added=mapRuleManage.getMapRuleById(mapId);
						}

						List<MapRule> ruleList=mapRuleManage.getMapRule(userId);
						responeMessage=ServerMessageFactory.getAddRuleMessage2(userId,code ,ruleList,message,mapId);
						sendMessage(responeMessage);
						addBind(rule_added);
						break;
					}else if(type.equals(Constant.Type_UpdateMapRuleMessage)){
						String message="";
						
						String code=Constant.code_Success;
						String dstAddress=requestMessage.getString(Constant.Key_DstAddress);
						int dstPort=requestMessage.getIntValue(Constant.Key_DstPort);
						int protocal=requestMessage.getIntValue(Constant.Key_Protocal);
						int mapType=requestMessage.getIntValue(Constant.Key_MapType);
						String domainName=requestMessage.getString(Constant.Key_DomainName);
						String name=requestMessage.getString(Constant.Key_Name);
						String domainNamePrefix=requestMessage.getString(Constant.Key_DomainNamePrefix);
						int mapId=requestMessage.getIntValue(Constant.Key_MapId);
						domainNamePrefix=domainNamePrefix.replace(" ", "");
						domainNamePrefix=domainNamePrefix.trim().toLowerCase();

						
						MapRule rule=mapRuleManage.getMapRuleById(mapId);
						int port=rule.getPort();
						
						code=Constant.code_Success;
						
						if(mapType==MapRule.type_web){
							if(domainNamePrefix.trim().equals("")){
								message+="输入正确域名前缀";
								code=Constant.code_Failed;
							}else{ 
								MapRule r=mapRuleManage.getMapRuleDomainNamePrefix(domainNamePrefix);
								if(r!=null){
									if(r.getMapId()!=mapId){
										message+=" 域名前缀已经被占用";
										code=Constant.code_Failed;
									}
								}
							}
							if(domainName.trim().equals("")){
								message+=" 输入正确自定义域名";
							}else{
								MapRule r=mapRuleManage.getMapRuleDomainName(domainName);
								if(r!=null){
									if(r.getMapId()!=mapId){
										message+=" 自定义域名已经被使用";
										code=Constant.code_Failed;
									}
								}
							}
							if(mapServer.getMapServerBean().getDomainName()==null){
								message="服务器必须绑定域名后才能添加网站映射!";
								code=Constant.code_Failed;
							}
						}else if(mapType==MapRule.type_single_port){
							if(rule.getType()!=MapRule.type_single_port){
								if(port<1){
									port=mapRuleManage.getUnusedPort();
								}
							}
						}
						if(code==Constant.code_Success){
							removeBind(rule);
							mapRuleManage.updateMapRule(mapId, userId, port, dstAddress, dstPort, protocal, 0, mapType, domainName, name,domainNamePrefix);
							addBind(rule);
						}
						List<MapRule> ruleList=mapRuleManage.getMapRule(userId);
						//MapB mapB=new MapB(mapServer,mapRule);
						//mapServer.getMapBManager().addMapB(mapRule.getMapId(), mapB);
						responeMessage=ServerMessageFactory.getAddRuleMessage2(userId, code,ruleList,message,mapId);
						sendMessage(responeMessage);
						break;
					}
					else if(type.equals(Constant.Type_RemoveMapRuleMessage)){
						int mapId=requestMessage.getIntValue(Constant.Key_MapId);
						MapRule rule=mapRuleManage.removeMapRule(mapId, userId);
						String code=Constant.code_Success;
						List<MapRule> ruleList=mapRuleManage.getMapRule(userId);
						responeMessage=ServerMessageFactory.getAddRuleMessage2(userId, code,ruleList,null,-1);
						sendMessage(responeMessage);
						removeBind(rule);
						break;
					}else{
						responeMessage=ServerMessageFactory.getLoginMessage2(userId,Constant.code_Error,sessionId);
						sendMessage(responeMessage);
						break;
					}
				}
			}
		}
		} catch (Exception e) {
			//e.printStackTrace();
		}finally {
			if(type!=null&&
				(type.equals(Constant.Type_LoginMessage)
				||type.equals(Constant.Type_HeartBeatMessage))){
				clientManager.removeMapClient(userId);
				onClientClose(userId);
			}
			close();
		}
		
		

	}
	
	void removeBind(MapRule rule){
		MapBManager mapBManager=mapServer.getMapBManager();
		if(rule!=null){
			int type=rule.getType();
			if(type==MapRule.type_single_port){
				mapBManager.removeSingelPortBind(rule.getPort());
			}else if(type==MapRule.type_web){
				mapBManager.removeDomainBind(rule);
			}
		}
	}
	
	void addBind(MapRule rule){
		MapBManager mapBManager=mapServer.getMapBManager();
		if(rule!=null){
			int type=rule.getType();
			if(type==MapRule.type_single_port){
				mapBManager.addSingelPortBind(rule.getPort(), rule);
			}else if(type==MapRule.type_web){
				mapBManager.addDomainBind(rule);
			}
		}
	}
	
	synchronized void onClientClose(String userId){
		try {
			ArrayList<MapRule> mapRules=mapServer.getMapRuleManage().getMapRule(userId);
			for(MapRule rule:mapRules){
				removeBind(rule);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	synchronized void onClientLogin(MapClient mapClient,String userId,List<MapRule> newRuleList){
		for(MapRule rule:newRuleList){
			addBind(rule);
		}
	}
	
	synchronized void onUpdateMapRule(MapRule rule){
		removeBind(rule);
		addBind(rule);
	}	
	
	boolean containsRule(List<MapRule> newRuleList,long id){
		boolean newContain=false;
		for(MapRule mapRule:newRuleList){
			if(mapRule.getMapId()==id){
				newContain=true;
				break;
			}
		}
		return newContain;
	}

	void sendMapConnectRequestMessage(long mapId,long mapConnectId,String dstAddress,int dstPort,int protocal) throws Exception{
		JSONObject message=ServerMessageFactory.getMapConnectRequestMessage(userId,mapId, mapConnectId,dstAddress,dstPort,protocal);
		sendMessage(message);
	}

	synchronized void sendMessage(JSONObject message) throws Exception{
		Tools.sendMessage(message, dosA);
	}

	void close(){
		if(socketA!=null){
			try {
				socketA.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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

	}

}
