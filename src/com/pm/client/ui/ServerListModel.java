// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import com.pm.bean.MapServerBean;
import com.pm.client.MapServerItem;

public class ServerListModel  extends AbstractListModel implements ComboBoxModel{

	private static final long serialVersionUID = 5785132141877064430L;
	
	MapServerBean selected=null;
	
	List<MapServerBean> list=new ArrayList<MapServerBean>();

	@Override
	public Object getElementAt(int index) {
		return list.get(index);
	}

	@Override
	public int getSize() {
		return list.size();
	}

	@Override
	public MapServerBean getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object o) {
		selected=(MapServerBean) o;
	}

	public void setList(List<MapServerBean> list) {
		this.list = list;
	}

}
