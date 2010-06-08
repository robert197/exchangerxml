/*
 * $Id: ToggleFullScreenAction.java,v 1.3 2004/10/21 15:43:40 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to collapse all nodes in the viewer.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/21 15:43:40 $
 * @author Dogsbay
 */
public class ToggleFullScreenAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the action to collapse all nodes 
	 * in the viewer.
	 *
	 * @param editor the XML viewer
	 */
 	public ToggleFullScreenAction( ExchangerEditor parent) {
		super( "Full Screen");
		
		this.parent = parent;
		
		putValue( MNEMONIC_KEY, new Integer( 'F'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/FullScreen16.gif"));
		putValue( SHORT_DESCRIPTION, "Toggle Full Screen");
 	}
 	
	/**
	 * The implementation of the collapse all action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		parent.toggleFullScreen();
 		
 		if ( parent.getCurrent() != null) {
 			parent.getCurrent().setFocus();
 		}
 	}
}
