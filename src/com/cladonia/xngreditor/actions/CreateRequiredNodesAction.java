/*
 * $Id: CreateRequiredNodesAction.java,v 1.2 2005/06/23 15:14:05 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerListener;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xngreditor.plugins.PluginViewPanel;

/**
 * An action that can be used to create the required nodes 
 * in an element node.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/06/23 15:14:05 $
 * @author Dogsbay
 */
public class CreateRequiredNodesAction extends AbstractAction implements DesignerListener {
	private static final boolean DEBUG = false;

	private Designer designer = null;

    //private PluginViewPanel pluginViewPanel;
	private PluginViewPanel pluginViewPanel;

	/**
	 * The constructor for the copy action.
	 *
	 * @param editor the editor to copy information from.
	 */
	public CreateRequiredNodesAction() {
		super( "Create Required Nodes");

		putValue( MNEMONIC_KEY, new Integer( 'N'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Create the Required Nodes");

		setEnabled( false);
	}

	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( designer != null) {
			designer.removeDesignerListener( this);
		}

		if ( view instanceof Designer) {
			designer = (Designer)view;
			pluginViewPanel = null;
			designer.addDesignerListener( this);
			selectionChanged( designer.getSelectedNode());
		} else if( view instanceof PluginViewPanel) {
			pluginViewPanel = (PluginViewPanel) view;
		    setEnabled(true);
		    designer = null;		
		} else {
			designer = null;
			pluginViewPanel = null;
			setEnabled( false);
		}
	}

	public void selectionChanged( DesignerNode node) {
		boolean enable = false;

		if ( node != null) {
			if ( node instanceof ElementNode) {
				ElementNode e = (ElementNode)node;

				if ( !e.isVirtual()) {
					enable = true;
				} 
			} 
		} 

		setEnabled( enable);
	}

	/**
	* The implementation of the copy action.
	*
	* @param e the action event.
	*/
	public void actionPerformed( ActionEvent e) {
	    if(designer != null) {
	        designer.createRequired();
	    }
	    else if(pluginViewPanel != null) {
	    	pluginViewPanel.createRequired();
	    }
	}
}
