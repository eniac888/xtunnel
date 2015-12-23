// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.pm.PM;
import com.pm.Tools;
import com.pm.client.ClientConfig;

import net.miginfocom.swing.MigLayout;

public class MainPanel extends JComponent{

	private static final long serialVersionUID = 1990532525675732875L;

	public MapRuleListTable tcpMapRuleListTable;

	PMClientUI ui;

	JTextField addressTextField,statusTextField;

	JButton settingButton;

	ClientConfig config;
	
	SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
	
	MapRuleListModel model;
	
	JLabel messageField ;
	
	String systemName;
	
	MainPanel(final PMClientUI ui){
		setLayout(new MigLayout("alignx center,insets 10 10 10 10"));
		this.ui=ui;
		config=ui.getConfig();
		systemName=System.getProperty("os.name").toLowerCase();
		JPanel wp=new JPanel();
		add(wp,"wrap");
		wp.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));
		messageField = new JLabel();
		
		wp.add(messageField,"height 80::,width 100:10240:,wrap");
		messageField.setBorder(BorderFactory.createEtchedBorder());
		
		JTabbedPane tabPanel=new JTabbedPane();
		add(tabPanel,"growx ,growy,width 200:10240:,height 100:10240:,wrap");
		tabPanel.setFocusable(false);
		
		
		JScrollPane tablePanel=new JScrollPane();
		tabPanel.add(tablePanel,"映射列表");
		model=new MapRuleListModel(ui);
		tcpMapRuleListTable=new MapRuleListTable(ui,model);
		tablePanel.setViewportView(tcpMapRuleListTable);
		tablePanel.addMouseListener(new MouseListener(){

			public void mouseClicked(MouseEvent e) {
				tcpMapRuleListTable.clearSelection();
			}

			public void mouseEntered(MouseEvent e) {}

			public void mouseExited(MouseEvent e) {}

			public void mousePressed(MouseEvent e) {}

			public void mouseReleased(MouseEvent e) {}

		});
		

		JScrollPane tablePanel2=new JScrollPane();
		tablePanel2.setBorder(BorderFactory.createTitledBorder("映射列表"));

		JPanel p3=new JPanel();
		add(p3,"wrap,growx ,growy");
		p3.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));

		Font font1 = new Font("宋体",Font.BOLD,12);

		settingButton=createButton("登录");
		p3.add(settingButton);
		settingButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				SettingFrame af=new SettingFrame(ui,ui.getMainFrame());
				af.setVisible(true);
			}

		});
		
		JButton addButton=createButton("添加映射");
		p3.add(addButton);
		addButton.setFont(font1);
		addButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				AddTcpMapRuleFrame af=new AddTcpMapRuleFrame(ui,ui.getMainFrame(),null,AddTcpMapRuleFrame.type_add);
				//af.setVisible(true);
			}

		});


		JButton websiteButton=createButton("网站");
		p3.add(websiteButton);
		websiteButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
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

		});
		
		
		JButton exitButton=createButton("退出");
		p3.add(exitButton);
		exitButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				ui.exit();
			}

		});


		JPanel p5=new JPanel();
		add(p5,"wrap,growx ,growy");
		p5.setLayout(new MigLayout("alignx center,insets 0 0 0 0"));
		p5.add(new JLabel("状态"));
		statusTextField=new JTextField("离线");
		p5.add(statusTextField,"width 150::");
		statusTextField.setEditable(false);

		final JCheckBox cb=new JCheckBox("开机启动",config.isAutoStart());
		if(systemName.contains("windows")){
			p5.add(cb,"");
		}
		cb.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				ui.getConfig().setAutoStart(cb.isSelected());
				ui.saveConfig();
				PMClientUI.setAutoRun(config.isAutoStart());
			}

		});
		
		PM.exec(new Runnable() {
			
			@Override
			public void run() {
				updateMessage();
			}
			
		});
	}
	
	void updateMessage(){
		Thread thread=new Thread(){
			
			public void run() {

				for(int i=0;i<20;i++){
					String content="";
					try {
						byte[] data=Tools.downloadHttpFile(PMClientUI.messageUrl);
						content=new String(data,"utf-8");
						messageField.setText(content);
						break;
					} catch (Exception e) {
						//e.printStackTrace();
						try {
							Thread.sleep(3000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
				}
			
			}
			
		};
		thread.start();
	}
	
	void setStatus(String message){
		statusTextField.setText(message);
		
	}

	JButton createButton(String text){
		JButton button=new JButton(text);
		button.setMargin(new Insets(0,5,0,5));
		button.setFocusable(false);
		return button;
	}
	
	void select(int mapId){
		int index=model.getMapRuleIndex(mapId);
		if(index>-1){
			tcpMapRuleListTable.getSelectionModel().setSelectionInterval(index, index);
		}
	}

	public MapRuleListTable getTcpMapRuleListTable() {
		return tcpMapRuleListTable;
	}

}
