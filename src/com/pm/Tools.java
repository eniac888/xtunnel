// Copyright (c) 2015 D1SM.net

package com.pm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.alibaba.fastjson.JSONObject;


public class Tools {
	


	public static byte[] downloadHttpFile(String url) throws Exception{
		byte[] data=null;
		URL url2 = new URL(url);
		HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
		conn.setUseCaches(false);
		conn.connect();
		int length = conn.getContentLength();
		InputStream is = conn.getInputStream();
		DataInputStream dis=new DataInputStream(is);
		if(length>0){
			data=new byte[length];
			dis.readFully(data);
		}
		return data;
	}
		
	public static void saveFile(String content,String path) throws Exception{
		byte[] data=content.getBytes("utf-8");
		File parent=new File(path).getParentFile();
		if(parent!=null&&!parent.exists()){
			parent.mkdirs();
		}
		FileOutputStream fos=null;
		try {
			fos=new FileOutputStream(path);
			fos.write(data);
		} catch (Exception e) {
			throw e;
		} finally {
			if(fos!=null){
				fos.close();
			}
		}
	}
	
	public static String readFileStringValue(String path) throws Exception {
		String value=null;
		String content=readFileData(path);
		content=content.trim();
		content=content.replaceAll("\n", "");
		content=content.replaceAll("\r", "");
		value=content;
		return value;
	}
	
	public static int readFileIntValue(String path) throws Exception {
		int value=-1;
		String content=readFileData(path);
		content=content.trim();
		content=content.replaceAll("\n", "");
		content=content.replaceAll("\r", "");
		value=Integer.parseInt(content);
		return value;
	}
	
	public static String readFileData(String path) throws Exception {
		String content = null;
		FileInputStream fis = null;
		DataInputStream dis = null;
		try {
			File file = new File(path);
			fis = new FileInputStream(file);
			dis = new DataInputStream(fis);
			byte[] data = new byte[(int) file.length()];
			dis.readFully(data);
			content = new String(data, "utf-8");
		} catch (Exception e) {
			throw e;
		} finally {
			if (dis != null) {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return content;
	}
	
	public static void sendMessage(JSONObject message,DataOutputStream dos) throws Exception{
		byte[] data =message.toJSONString().getBytes("utf-8");
		dos.writeInt(data.length);
		dos.write(data);
	}
	
	public static JSONObject readMessage(DataInputStream dis) throws Exception {
		int length=dis.readInt();
		byte[] data=new byte[length];
		dis.readFully(data);
		JSONObject message=JSONObject.parseObject(new String(data,"utf-8"));
		return message;
	}
	
	public static void checkProtocal(long protocalId,DataOutputStream dos,DataInputStream dis) throws Exception{
		writeProtocal(protocalId,dos);
		long n=readProtocal(dis);
		if(n!=protocalId){
			throw new Exception("协议错误!");
		}
	}
	
	public static void checkProtocal(long protocalId,DataInputStream dis) throws Exception{
		long n=readProtocal(dis);
		if(n!=protocalId){
			throw new Exception("协议错误!");
		}
	}
	
	public static long readProtocal(DataInputStream dis) throws IOException{
		long n=dis.readLong();
		return n;
	}
	
	public static void writeProtocal(long protocalId,DataOutputStream dos) throws IOException{
		dos.writeLong(protocalId);
	}
	
	public static JSONObject sendRequestMessage(long protocalId,String address,int port,JSONObject message) throws Exception{
		Socket socket=null;
		DataOutputStream dos=null;
		DataInputStream dis=null;
		JSONObject responeMessage = null;
		try {
			socket = new Socket(address,port);
			Tools.setSocket(socket);
			dos=new DataOutputStream(socket.getOutputStream());
			dis=new DataInputStream(socket.getInputStream());
			checkProtocal(protocalId, dos, dis);
			sendMessage(message,dos);
			responeMessage=Tools.readMessage(dis);
		}catch (Exception e) {
			//e.printStackTrace();
			throw e;
		}finally{
			if(socket!=null){
				try {
					socket.close();
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
			if(dos!=null){
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return responeMessage;
	}
	
	public static void writeMessage(JSONObject msg,HttpURLConnection conn) throws Exception {
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		byte[] data = msg.toString().getBytes("utf-8");
		dos.writeInt(data.length);
		dos.write(data);
	}
	
	public static void setSocket(Socket socket) throws SocketException{
		socket.setTcpNoDelay(true);
//		socket.setSoTimeout(0);
//		setSocketCommond(socket);
	}
	
	public static HttpURLConnection getConnection(String urlString) throws Exception{
		URL url = new URL(urlString);
		HttpURLConnection conn = null;
		if(urlString.startsWith("http://")){
			conn = (HttpURLConnection) url.openConnection();
		}else if(urlString.startsWith("https://")){
			HttpsURLConnection conns=(HttpsURLConnection)url.openConnection();
			conns.setHostnameVerifier(new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});
			conn=conns;
		}
		if(conn!=null){
			conn.setConnectTimeout(10*1000);
			conn.setReadTimeout(10*1000);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);
		}
		return conn;
	}



	public static String getMD5(String str) {
		byte[] source=str.getBytes();
		return getMD5(source);
	}   

	public static String getMD5(byte[] source) {   
		String s = null;   
		char hexDigits[] = {
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',  'e', 'f'};    
		try  
		{   
			java.security.MessageDigest md = java.security.MessageDigest.getInstance( "MD5" );   
			md.update( source );   
			byte tmp[] = md.digest();
			char str[] = new char[16 * 2];
			int k = 0; 
			for (int i = 0; i < 16; i++) { 
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
				str[k++] = hexDigits[byte0 & 0xf];
			}    
			s = new String(str);
		}catch( Exception e )   
		{   
			e.printStackTrace();   
		}   
		return s;   
	}  

}
