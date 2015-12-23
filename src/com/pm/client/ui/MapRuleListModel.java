// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.table.AbstractTableModel;

import com.pm.Constant;
import com.pm.bean.MapRule;


public class MapRuleListModel extends AbstractTableModel{
	
	private static final long serialVersionUID = 2267856423317178816L;

	private List<MapRule> mapRuleList;
	
	String titles[] ;

	Class<?> types[] = new Class[] {String.class,  String.class, String.class,String.class,  String.class, String.class};
	
	PMClientUI ui;
	
	MapRuleListModel(PMClientUI ui){
		this.ui=ui;
		mapRuleList=new ArrayList<MapRule> ();
		titles = new String[] {"内网地址","内网端口","外网地址","外网端口"};
	//	titles = new String[] {"类型","内网地址","外网地址","线路","状态","设置"};
		titles = new String[] {""};
	}
	
	public void setMapRuleList(List<MapRule> list){
		mapRuleList.clear();
		if(list!=null){
			mapRuleList.addAll(list);
		}
		fireTableDataChanged();
	}
	
	public int getMapRuleIndex(int mapId){
		int index=-1;
		int i=0;
		for(MapRule r:mapRuleList){
			if(mapId==r.getMapId()){
				index=i;
				break;
			}
			i++;
		}
		return index;
	}
	
	List<MapRule> getMapRuleList(){
		return mapRuleList;
	}
	
	public MapRule getMapRuleAt(int row){
		if(row>-1&row<mapRuleList.size()){
			return mapRuleList.get(row);
		}else{
			return null;
		}
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		MapRule node=mapRuleList.get(rowIndex);
		return node;
	}
	
	public void setValueAt(Object value, int row, int col) {
	      fireTableCellUpdated(row, col);
	 }

	public int getRowCount() {
		return mapRuleList.size();
	}

	public int getColumnCount() {
		return titles.length;
	}

	public String getColumnName(int c) {
		return titles[c];
	}

	public Class<?> getColumnClass(int c) {
		return types[c];
	}


	public boolean isCellEditable(int row, int col) {
		boolean b=false;
		if(col==0){
			b=true;
		}
		return false;
	}
}
