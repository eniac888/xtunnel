// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import javax.swing.table.DefaultTableCellRenderer;

public class AlignCellRenderer extends DefaultTableCellRenderer{
	private static final long serialVersionUID = -6003599724059557606L;

	public AlignCellRenderer(int align){
		super();
		setHorizontalAlignment(align);
	}
	
}
