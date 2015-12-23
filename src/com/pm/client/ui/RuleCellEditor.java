// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class RuleCellEditor implements TableCellEditor{

	private static final long serialVersionUID = 6717289111747317645L;

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return false;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return false;
	}

	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void cancelCellEditing() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
