// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class SetEditor extends JPanel implements TableCellRenderer,TableCellEditor {

	{
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		// TODO Auto-generated method stub
		return  new JButton("scccsss");
	}
	
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		// TODO Auto-generated method stub
		return new JButton("ssss");
	}
	@Override
	public Object getCellEditorValue() {
		// TODO Auto-generated method stub
		return "cccccccc";
	}
	@Override
	public boolean isCellEditable(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public boolean stopCellEditing() {
		// TODO Auto-generated method stub
		return true;
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
	
	

}
