// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.pm.Tools;
import com.pm.client.ClientConfig;
import com.pm.client.MapMonit;

import net.miginfocom.swing.MigLayout;

public class SettingFrame extends JDialog{
	
	Dimension size=new Dimension(400,130);
	
	PMClientUI ui;
	
	ClientConfig config;

	//JPasswordField pswField;

	JTextField text_serverAddress,text_serverPort;

	JPasswordField pswField;
	
	public SettingFrame(final PMClientUI ui,JFrame parent){
		super(parent,Dialog.ModalityType.APPLICATION_MODAL);
		this.ui=ui;
		ui.loadConfig();
		config=ui.getConfig();
		setSize(size);
		setLocationRelativeTo(parent);
		setTitle("登录服务器");
		
		JPanel panel=(JPanel) getContentPane();
		panel.setLayout(new MigLayout("alignx center,aligny center,insets 10 10 10 10"));
		
		JPanel loginPanel=new JPanel();
		add(loginPanel,"wrap,growx ,growy");
		loginPanel.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));

		JPanel p5=new JPanel();
		loginPanel.add(p5,"width :: ,wrap");
		p5.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));

		p5.add(new JLabel("地址:"));
		text_serverAddress=new JTextField(config.getServerAddress());
		p5.add(text_serverAddress,"width 120::");
		TextComponentPopupMenu.installToComponent(text_serverAddress);

		p5.add(new JLabel("端口:"));
		text_serverPort=new JTextField("180");
		p5.add(text_serverPort,"width 50::");
		TextComponentPopupMenu.installToComponent(text_serverPort);
		text_serverPort.setText(config.getServerPort()+"");
		
		
		
		JPanel p6=new JPanel();
		loginPanel.add(p6,"width :: ,wrap");
		p6.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));
		p6.add(new JLabel("密码:"));
		pswField=new JPasswordField(config.getPasswordMd5());
		p6.add(pswField,"width 70::70");
		TextComponentPopupMenu.installToComponent(pswField);
		pswField.setText(config.getPasswordMd5());
		
		JPanel p2=new JPanel();
		p2.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));
		panel.add(p2,"growx,wrap");
		JButton b1=createButton("确定");
		p2.add(b1);
		b1.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e) {
				ClientConfig config=ui.getConfig();
				String p1=new String(pswField.getPassword());
				p1=p1.trim();
				if(!p1.equals("")){
					if(!p1.equals(config.getPasswordMd5())){
						String pmd5=Tools.getMD5(p1);
						pswField.setText(pmd5);
					}
				}
				
				config.setServerAddress(text_serverAddress.getText());
				String p=new String(pswField.getPassword());
				config.setPasswordMd5(p);
				
				String portStr=text_serverPort.getText().trim();
				int port=Integer.parseInt(portStr);
				config.setServerPort(port);
				
				ui.saveConfig();
				MapMonit monit=ui.getControlClient().getMapMonit();
				if(monit!=null){
					monit.setServerAddress(config.getServerAddress());
					monit.setServerPort(config.getServerPort());
					monit.reconnect();
				}
				ui.setAccountError(false);
				ui.setStatus("离线");
				ui.loadRule(-1);
				setVisible(false);
			}
			
		});
		JButton b2= createButton("取消");
		p2.add(b2);
		b2.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
			
		});
		pack();
	}
	
	JButton createButton(String name){
		JButton button=new JButton(name);
		button.setMargin(new Insets(0,5,0,5));
		button.setFocusable(false);
		return button;
	}

}
