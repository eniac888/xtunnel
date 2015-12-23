// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.pm.PM;
import com.pm.bean.MapRule;
import com.pm.bean.MapServerBean;
import com.pm.client.MapMonit;

import sun.swing.DefaultLookup;
import net.miginfocom.swing.MigLayout;

public class MapRuleRender extends JLabel implements TableCellRenderer {

	private static final long serialVersionUID = -3260748459008436510L;

	JPanel pleft,pright,p1;


	JLabel label1;
	JLabel label_wan_address;

	MapRule rule;
	
	PMClientUI pui;

	private Color unselectedForeground;
	private Color unselectedBackground;

	{
		setOpaque(true);
		setLayout(new MigLayout("alignx left,aligny center,insets 0 10 0 0"));
		
		label1=new JLabel();
		add(label1,"growx,width :1000:,height ::,wrap");
		label1.setBackground(new Color(0f,0f,0f,0f));
		label1.setOpaque(true);
		
		label_wan_address=new JLabel();
		add(label_wan_address,"growx,width :1000:,height ::");
		label_wan_address.setBackground(new Color(0f,0f,0f,0f));
		label_wan_address.setOpaque(true);
	}
	
	MapRuleRender(PMClientUI pui){
		this.pui=pui;
	}


	void update(MapRule rule,JTable table,int row){
		this.rule=rule;

		StringBuffer sb1=new StringBuffer();
		StringBuffer sb=new StringBuffer();
		MapServerBean mp=rule.getMapserver();
		//mp.setDomainName("mhbz.net");

		int rowHeight=50;
		int type=rule.getType();
		
		int h=table.getRowHeight(row);
		if(h!=rowHeight){
			table.setRowHeight(row, rowHeight);
		}

		String name=rule.getName();
		if(name==null){
			name="无";
		}else if(name.trim().equals("")){
			name="无";
		}
		//		
		String status="离线";
		MapMonit client=PMClientUI.get().getControlClient().getMapMonit();
		if(client!=null){
			if(client.isOnline()){
				status="在线";
			}
		}
		
		sb1.append("名称:"+name+" ");
		sb1.append("类型:"+rule.getTypeString()+" 状态:"+status);
		
		label1.setText(sb1.toString());
		
		String wanAddress=rule.getWanAddress();
		if(type==MapRule.type_single_port){
			if(rule.getMapserver().getDomainName()==null){
				wanAddress=pui.getConfig().getServerAddress()+":"+rule.getPort();
			}
		}
		sb.append("外网地址:"+wanAddress+" 内网地址:"+rule.getLanAddress()+
				(rule.getCustomAddress()!=null?(" 自定义地址:"+rule.getCustomAddress()):""));
		
		label_wan_address.setText(sb.toString());
		
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		Color fg = null;
		Color bg = null;

		JTable.DropLocation dropLocation = table.getDropLocation();
		if (dropLocation != null
				&& !dropLocation.isInsertRow()
				&& !dropLocation.isInsertColumn()
				&& dropLocation.getRow() == row
				&& dropLocation.getColumn() == column) {

			fg = DefaultLookup.getColor(this, ui, "Table.dropCellForeground");
			bg = DefaultLookup.getColor(this, ui, "Table.dropCellBackground");

			isSelected = true;
		}

		if (isSelected) {
			setBackground(DefaultLookup.getColor(this, ui, "Table.dropCellBackground"));
		} else {
			setBackground( DefaultLookup.getColor(this, ui, "Table.alternateRowColor"));
		}

		MapRule rule=(MapRule)value;
		update(rule,table,row);
		return this;
	}

}
