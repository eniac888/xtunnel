// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import com.pm.bean.MapServerBean;

import sun.swing.DefaultLookup;

public class MapServerRenderer extends JLabel implements ListCellRenderer{

	private static final long serialVersionUID = -568516585325710077L;


	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		String text="";
		if(value!=null){
			MapServerBean mapserver=(MapServerBean)value;
			text=mapserver.getName()+" "+mapserver.getDescription()+"";
		}
		setText(text);
		setOpaque(true);

        if (isSelected) {
            setBackground(DefaultLookup.getColor(this, ui, "Table.dropCellBackground"));
        } else {
            setBackground( DefaultLookup.getColor(this, ui, "Table.alternateRowColor"));
        }
        
		return this;
	}

}
