// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import com.alibaba.fastjson.JSONObject;
import com.pm.Constant;
import com.pm.bean.MapRule;
import com.pm.client.ClientMessageFactory;


public class MapRuleMenu extends JPopupMenu{
	
	private static final long serialVersionUID = 5308865552190504933L;
	
	MapRule mapRule;

	MapRuleMenu(final PMClientUI pui,final MapRule mapRule){
		this.mapRule=mapRule;
		JMenuItem m1= new JMenuItem("复制外网地址");
		add(m1);
		m1.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String wanAddress=mapRule.getWanAddress();
				if(mapRule.getType()==MapRule.type_single_port){
					if(mapRule.getMapserver().getDomainName()==null){
						wanAddress=pui.getConfig().getServerAddress()+":"+mapRule.getPort();
					}
				}
				copyToClipBoard(wanAddress);
			}
			
		});
		
		JMenuItem m2= new JMenuItem("复制内网地址");
		add(m2);
		m2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				copyToClipBoard(mapRule.getLanAddress());
			}
			
		});
		
		
		if(mapRule.getCustomAddress()!=null){
			JMenuItem m4= new JMenuItem("复制自定义地址");
			add(m4);
			m4.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					copyToClipBoard(mapRule.getCustomAddress());
				}
				
			});
			
		}
	
		if(mapRule!=null){
			addSeparator();
			JMenuItem m4= new JMenuItem("编辑");
			add(m4);
			m4.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					PMClientUI ui=PMClientUI.get();
					ui.mainPanel.tcpMapRuleListTable.editRule();
				}
				
			});
			addSeparator();
			JMenuItem m3= new JMenuItem("删除");
			add(m3);
			m3.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					PMClientUI ui=PMClientUI.get();
					int result=JOptionPane.showConfirmDialog(ui.getMainFrame(), "确定删除吗?","消息", JOptionPane.YES_NO_OPTION);
					if(result==JOptionPane.OK_OPTION){
						try {
							JSONObject msg=ui.getControlClient().removeMapRule( mapRule.getMapId());
							List<JSONObject> ruleList1=(List<JSONObject>) msg.get(Constant.Key_MapRule);
							List<MapRule> ruleList=ClientMessageFactory.getRuleListJSONObject(ruleList1);
							//ui.loadRule();
							ui.updateTcpMapRule(ruleList,-1);
						} catch (Exception e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(ui.getMainFrame(), "删除失败!","消息",JOptionPane.WARNING_MESSAGE);
						}
					}
				}
				
			});
		}
		
	}
	
	
	void copyToClipBoard(String text){
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection stringSel = new StringSelection(text);
		clipboard.setContents(stringSel, null);
	}

}
