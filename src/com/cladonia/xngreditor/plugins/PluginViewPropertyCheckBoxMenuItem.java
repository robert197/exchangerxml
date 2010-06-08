/*
 * $Id: PluginViewPropertyCheckBoxMenuItem.java,v 1.0 18 Apr 2007 12:17:40 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Vector;

import javax.swing.JCheckBoxMenuItem;

import com.cladonia.xngreditor.ExchangerView;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 18 Apr 2007 12:17:40 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginViewPropertyCheckBoxMenuItem extends JCheckBoxMenuItem implements ItemListener {

	private PluginView pluginView = null;
	private String propertyName = null;
	private String propertyLabel = null;

	public PluginViewPropertyCheckBoxMenuItem(PluginView view, String propertyName, String propertyLabel) {
		
		super(propertyLabel);
		this.pluginView = view;
		this.propertyName = propertyName;
		
		this.addItemListener(this);
		
		if(pluginView.getProperties() != null) {
			this.setSelected(pluginView.getProperties().getBoolean((propertyName)));
		}
	
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent e) {
	
//		properties.getGridProperties().hideContainerTables( gridHideContainerTables.isSelected());
			
		//Vector views = getViews();
		//for (int i = 0; i < views.size(); i++) {
//				((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
		//}
		
		pluginView.getProperties().set(propertyName, this.isSelected());
		
		Vector views = pluginView.getExchangerEditor().getViews();		
		for (int i = 0; i < views.size(); i++) {
			((ExchangerView) views.elementAt(i)).updatePreferences();
		}
		
		
	}
	
}
