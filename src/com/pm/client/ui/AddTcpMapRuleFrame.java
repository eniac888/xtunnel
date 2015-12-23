// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.PM;
import com.pm.bean.MapRule;
import com.pm.client.ClientConfig;
import com.pm.client.ClientMessageFactory;
import com.pm.client.MapMonit;

import net.miginfocom.swing.MigLayout;

public class AddTcpMapRuleFrame extends JDialog{
	
	private static final long serialVersionUID = 8774274005381851211L;
	
	static Dimension size=new Dimension( 660,470);
	
	PMClientUI ui;
	
	JTextField addressTextField, domainNamePrefixField;
	
	JTextField portTextField,customDomainField;
	
	JTextField nameTextField;
	
	AddTcpMapRuleFrame frame;
	
	ClientConfig config;
	
	JCheckBox b5;
	JCheckBox b6;
	
	MapRule rule=null;
	
	int mapType=1;
		
	String domainName="";
	
	int frameType=0;
	
	static int type_add=1;
	
	static int type_edit=2;
	
	public AddTcpMapRuleFrame(final PMClientUI ui,JFrame parent,final MapRule rule,int type){
		super(parent,Dialog.ModalityType.APPLICATION_MODAL);
		this.frameType=type;
		this.rule=rule;
		frame=this;
		this.ui=ui;
		config=ui.getConfig();
		setSize(size);
		if(frameType==type_add){
			setTitle("添加映射");
		}else if(frameType==type_edit){
			setTitle("编辑映射");
		}
		
		JPanel panel=(JPanel) getContentPane();
		panel.setLayout(new MigLayout("alignx center,aligny center,insets 5 5 5 5"));
		
		ButtonGroup group=new ButtonGroup();
		JPanel p5=new JPanel();
		panel.add(p5,"wrap");
		p5.setBorder(BorderFactory.createTitledBorder("选择映射类型:"));
		p5.setLayout(new MigLayout("insets 0 0 0 0"));
		final JRadioButton rb1=new JRadioButton(MapRule.type_string_single_port);
		final JRadioButton rb2=new JRadioButton(MapRule.type_string_all_port);
		final JRadioButton rb3=new JRadioButton(MapRule.type_string_web);
		rb1.setToolTipText("");
		group.add(rb1);
		group.add(rb2);
		group.add(rb3);
		p5.add(rb1);
		p5.add(new JLabel("只映射一个TCP端口,分配随机外网端口,支持网站,我的世界,3389远程桌面,数据库,svn等."),"wrap");

		p5.add(rb3);
		p5.add(new JLabel("只支持网站,通过域名访问.")," wrap");
		
		p5.add(rb2);
		rb2.setEnabled(false);
		JLabel l1=new JLabel("映射所有TCP端口,内外网端口一致,支持监听多个端口或者无法修改端口的应用.");
		p5.add(l1,"width :500:,wrap");
		
		JPanel p=new JPanel();
		p.setLayout(new MigLayout("insets 5 5 5 5 ,alignx center"));
		panel.add(p,"wrap");
		p.add(new JLabel("名称:"),"width 100:100:");
		p.add(new JLabel("内网IP:"),"width 100:100:");
		p.add(new JLabel("内网端口:"),"width 50:80:,wrap");
		
		nameTextField=new JTextField("");
		p.add(nameTextField,"growx");
		nameTextField.setToolTipText("映射名称");
		
		addressTextField=new JTextField("127.0.0.1");
		p.add(addressTextField,"growx");
		addressTextField.setToolTipText("映射目标的IP地址,如果是本机应用,使用默认的127.0.0.1");
		portTextField=new JTextField("");
		p.add(portTextField,"growx");
		portTextField.setToolTipText("映射目标端口号,范围1-65535");
		
		JPanel p21=new JPanel();
		p21.setLayout(new MigLayout("insets 5 5 5 5,alignx center"));

		panel.add(p21,"wrap");
		
		p21.add(new JLabel("域名前缀:"),"width 50:80:");
		String t="自定义域名(选填):";
		p21.add(new JLabel(t),"width 100:: ,wrap");
		
		domainNamePrefixField=new JTextField("");
		p21.add(domainNamePrefixField,"growx");
		domainNamePrefixField.setToolTipText("外网地址的域名前缀,字母和数字组合");
		customDomainField=new JTextField("");
		p21.add(customDomainField,"growx");
		customDomainField.setToolTipText("请同时在该域名的控制面板增加cname记录或别名记录指向外网地址的域名部分");

		JPanel p2=new JPanel();
		p2.setLayout(new MigLayout("insets 0 0 0 0 , alignx center"));
		panel.add(p2,"growx,wrap");
		JButton b1=createButton("确定");
		p2.add(b1);
		b1.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				List<MapRule> list=null;
				try {
					if(frameType==type_add){
						list=addMap();
					}else if(frameType==type_edit){
						list=updateMap(rule.getMapId());
					}
					setVisible(false);
				} catch (Exception e1) {
					//e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, e1.getMessage(),"消息",JOptionPane.WARNING_MESSAGE);
				}
				if(list!=null){
					//ui.updateTcpMapRule(list);
				}
			}
			
		});
		JButton b2= createButton("取消");
		p2.add(b2);
		b2.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
			
		});
		addWindowListener(new WindowAdapter(){

		       public void windowOpened(WindowEvent e) {
		    	   if(frameType!=type_edit){
			    	   nameTextField.requestFocus();
		    	   }
		       }

		});
		

		rb1.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(rb1.isSelected()){
					portTextField.setEditable(true);
					customDomainField.setEditable(true);
					domainNamePrefixField.setEditable(false);
					mapType=MapRule.type_single_port;
				}
			}
		});
		rb3.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(rb3.isSelected()){
					portTextField.setEditable(true);
					customDomainField.setEditable(true);
					domainNamePrefixField.setEditable(true);
					mapType=MapRule.type_web;
				}
			}
		});
		
		
		rb1.setSelected(true);
		
		if(frameType==type_edit){
			int ruleType=rule.getType();
			if(ruleType==MapRule.type_single_port){
				rb1.setSelected(true);
			}else if (ruleType==MapRule.type_web) {
				rb3.setSelected(true);
			}
			
			nameTextField.setText(rule.getName());
			portTextField.setText(rule.getDstPort()+"");
			addressTextField.setText(rule.getDstAddress());
			customDomainField.setText(rule.getDomainName());
			domainNamePrefixField.setText(rule.getDomainNamePrefix());
		}
		
		try {
			pack();
			setLocationRelativeTo(parent);
			setVisible(true);
		} catch (Exception e1) {
			e1.printStackTrace();
			//setVisible(false);
			JOptionPane.showMessageDialog(PMClientUI.get().getMainFrame(), "加载失败.");
		}
	
	}
	
	List<MapRule> addMap() throws Exception{
		String domainName=customDomainField.getText();
		String name=nameTextField.getText();
		String domainNamePrefix=domainNamePrefixField.getText();

		checkName(name);
		List<MapRule> ruleList=null;
		int dstPort=0;
		if(mapType==MapRule.type_single_port|mapType==MapRule.type_web){
			checkPort(portTextField.getText());
			dstPort=Integer.parseInt(portTextField.getText());
		};
		if(mapType==MapRule.type_web){
			checkDomainNamePrefix(domainNamePrefix);
		}
		String message=null;
		try {
			JSONObject hmsg=ui.getControlClient().addTCPMapRule(addressTextField.getText(), dstPort,
					mapType,domainName,name,domainNamePrefix);
			String code=hmsg.getString(Constant.Key_Code);
			int mapId=hmsg.getIntValue(Constant.Key_MapId);
			if(code.equals(Constant.code_Failed)){
				message=hmsg.getString(Constant.Key_Message);
				throw new Exception();
			}
			List<JSONObject> ruleList1=(List<JSONObject>) hmsg.get(Constant.Key_MapRule);
			ruleList=ClientMessageFactory.getRuleListJSONObject(ruleList1);
			ui.updateTcpMapRule(ruleList,mapId);
		} catch (Exception e1) {
			e1.printStackTrace();
			if(message==null){
				throw new Exception("添加失败");
			}else{
				throw new Exception("添加失败: "+message);
			}
		}
		return ruleList;
	}
	
	List<MapRule> updateMap(int selectMapId) throws Exception{
	
		String domainName=customDomainField.getText();
		String name=nameTextField.getText();
		String domainNamePrefix=domainNamePrefixField.getText();
		int dstPort=Integer.parseInt(portTextField.getText());

		checkName(nameTextField.getText());
		List<MapRule> ruleList=null;
		if(mapType==MapRule.type_single_port|mapType==MapRule.type_web){
			checkPort(portTextField.getText());
			dstPort=Integer.parseInt(portTextField.getText());
		};
		if(mapType==MapRule.type_web){
			checkDomainNamePrefix(domainNamePrefix);
		}
		String message=null;
		try {
			JSONObject hmsg=ui.getControlClient().updateTCPMapRule(addressTextField.getText(), dstPort,
					mapType,domainName,name,rule.getMapId(),domainNamePrefix);
			String code=hmsg.getString(Constant.Key_Code);
			if(code.equals(Constant.code_Failed)){
				message=hmsg.getString(Constant.Key_Message);
				throw new Exception();
			}
			List<JSONObject> ruleList1=(List<JSONObject>) hmsg.get(Constant.Key_MapRule);
			ruleList=ClientMessageFactory.getRuleListJSONObject(ruleList1);
			ui.updateTcpMapRule(ruleList,selectMapId);
		} catch (Exception e1) {
			if(message==null){
				throw new Exception("修改失败");
			}else{
				throw new Exception("修改失败:"+message);
			}
		}
		return ruleList;
	}
	
	void checkName(String s) throws Exception{
		if(s.trim().equals("")){
			throw new Exception("请输入名称");
		}
	}
	
	void checkDomainNamePrefix(String s) throws Exception{
		if(s.trim().equals("")){
			throw new Exception("请输入域名前缀");
		}
	}
	
	void checkPort(String s) throws Exception{
		int port=0;
		try {
			port=Integer.parseInt(s);
		} catch (Exception e1) {
			throw new Exception("请输入正确端口号");
		}
		if(port<1|port>256*256){
			throw new Exception("请输入正确端口号");
		}
		if(addressTextField.getText().equals("")){
			throw new Exception("请输入映射地址");
		}
	}
	
	JButton createButton(String name){
		JButton button=new JButton(name);
		button.setMargin(new Insets(0,5,0,5));
		button.setFocusable(false);
		return button;
	}
}
