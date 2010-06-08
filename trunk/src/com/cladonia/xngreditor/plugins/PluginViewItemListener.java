/*
 * $Id: PluginViewItemListener.java,v 1.0 13 Mar 2007 16:02:32 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.ViewPanel;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 13 Mar 2007 16:02:32 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginViewItemListener implements ItemListener {

	private PluginView plugin = null;
	private ExchangerEditor exchangerEditor = null;
	
	public PluginViewItemListener(PluginView pluginView, ExchangerEditor editor) {
		this.exchangerEditor = editor;
		this.plugin = pluginView;
		
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 */
	public void itemStateChanged(ItemEvent event) {

		if((exchangerEditor != null) && (exchangerEditor.getView() != null)) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				ViewPanel current = exchangerEditor.getView().getCurrentView();
				
				try {
					exchangerEditor.switchToPluginView(plugin);
				} catch (Exception e) {
				    e.printStackTrace();
					if (current instanceof Editor) {
						//TODO exchangerEditor.getEditorButton().setSelected(true);
						//TODO exchangerEditor.getEditorViewItem().setSelected(true);
					} else if (current instanceof SchemaViewer) {
						//TODO exchangerEditor.getSchemaButton().setSelected(true);
						//TODO exchangerEditor.getSchemaViewItem().setSelected(true);
//					} else if (current instanceof Browser) {
//						browserButton.setSelected(true);
//						getBrowserViewItem().setSelected(true);
					}
					MessageHandler.showMessage(	"Please ensure the document is well-formed\nbefore switching to the \"Grid\".");
					current.setFocus();
				}
				
			}
		}

	}
	/**
	 * @param exchangerEditor the exchangerEditor to set
	 */
	public void setExchangerEditor(ExchangerEditor exchangerEditor) {

		this.exchangerEditor = exchangerEditor;
	}
	/**
	 * @return the exchangerEditor
	 */
	public ExchangerEditor getExchangerEditor() {

		return exchangerEditor;
	}

}
