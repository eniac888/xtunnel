// Copyright (c) 2015 D1SM.net

package com.pm.client;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import javax.swing.JOptionPane;

import com.pm.Tools;
import com.pm.client.ui.PMClientUI;

public class Updater {
	
	int serverVersion=-1;
	
	boolean checkingUpdate=false;
	
	PMClientUI ui;
	
	public Updater(PMClientUI ui){
		this.ui=ui;
	}
	
	boolean haveNewVersion(){
		return serverVersion>PMClientUI.localVersion;
	}

	public void checkUpdate(){
		for(int i=0;i<3;i++){
			checkingUpdate=true;  
			try {
				Properties propServer=new Properties();
				HttpURLConnection uc=Tools.getConnection(PMClientUI.updateUrl);
				uc.setUseCaches(false);
				InputStream in = uc.getInputStream();
				propServer.load(in);
				serverVersion=Integer.parseInt(propServer.getProperty("version"));
				break;
			} catch (Exception e) {
				//e.printStackTrace();
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}finally{
				checkingUpdate=false;
			}
		}
		if(this.haveNewVersion()){
			int option=JOptionPane.showConfirmDialog(ui.getMainFrame(), "发现新版本,立即更新吗?","提醒",JOptionPane.WARNING_MESSAGE);
			if(option==JOptionPane.YES_OPTION){
				try {
					Desktop.getDesktop().browse(new URL(ui.wetsite).toURI());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		}

	}
	
}
