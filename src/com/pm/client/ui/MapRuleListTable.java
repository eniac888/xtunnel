// Copyright (c) 2015 D1SM.net

package com.pm.client.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.pm.bean.MapRule;

public class MapRuleListTable extends JTable{
	
	private static final long serialVersionUID = -547936371303904463L;
	
	MapRuleListModel model;
	
	MapRuleListTable table;
	
	MapRuleListTable(final PMClientUI pui,final MapRuleListModel model){
		super();
		this.model=model;
		table=this;
		setModel(model);

		//setAutoCreateRowSorter(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setRowSorter(null);
		
		getColumnModel().getColumn(0).setMinWidth(30);
//		getColumnModel().getColumn(1).setMinWidth(80);
//		getColumnModel().getColumn(2).setMinWidth(100);
		
		AlignCellRenderer renderer=new AlignCellRenderer(AlignCellRenderer.CENTER);
		
		MapRuleRender rr=new MapRuleRender(pui);
		getColumnModel().getColumn(0).setCellRenderer(rr);
		setRowHeight(50);
		
		new Thread(){
			public void run() {
				while(true){
					try {
						Thread.sleep(1000);
						refresh();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
		
		addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(e.getButton()==MouseEvent.BUTTON3&&e.getClickCount()==1){
				int index=rowAtPoint(e.getPoint());
				int modelIndex=convertRowIndexToModel(index);
				getSelectionModel().setSelectionInterval(modelIndex, modelIndex);
				
				MapRule mapRule=getModel().getMapRuleAt(modelIndex);
				MapRuleMenu mapRuleMenu=new MapRuleMenu(pui,mapRule);
				mapRuleMenu.show(e.getComponent(),e.getX(),e.getY());
				
			}}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			

				if(e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==2){
					editRule();
				}
			}
		});
		
	}
	
	void editRule(){

		int index=getSelectedRow();
		int modelIndex=convertRowIndexToModel(index);
		MapRule mapRule=getModel().getMapRuleAt(modelIndex);
		
		AddTcpMapRuleFrame af=new AddTcpMapRuleFrame(PMClientUI.get(),PMClientUI.get().getMainFrame(),mapRule,AddTcpMapRuleFrame.type_edit);
		//af.setVisible(true);
	
	}
	
	void refresh(){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				updateUI();
			}
		});
	}
	
	public void setMapRuleList(List<MapRule> list){
		model.setMapRuleList(list);
	}

	public MapRuleListModel getModel() {
		return model;
	}

	public void setModel(MapRuleListModel model) {
		super.setModel(model);
		this.model = model;
	}
	
}
