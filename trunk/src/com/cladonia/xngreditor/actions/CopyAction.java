/*
 * $Id: CopyAction.java,v 1.5 2005/05/05 09:58:03 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.5 $, $Date: 2005/05/05 09:58:03 $
 * @author Dogsbay
 */
 public class CopyAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ExchangerEditor parent = null;

    private Object view = null;
	
 	/**
	 * The constructor for the copy action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public CopyAction( ExchangerEditor parent) {
 		super( "Copy");

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Copy");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
	    this.view = view;
	    if( view instanceof Editor) {
	        setEnabled(true);
	    }
	    else if( view instanceof PluginViewPanel) {
	        setEnabled(true);
	    }
	    else {
	        setEnabled(false);
	    }
	}

	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 	    
 	   if( view instanceof Editor) {
 	       Editor editor = (Editor)parent.getCurrent();
 	       editor.copy();
 	       editor.setFocus();
	    }
	    else if( view instanceof PluginViewPanel) {
	    	PluginViewPanel pluginViewPanel = (PluginViewPanel)parent.getCurrent();
	    	pluginViewPanel.copy();
	    	pluginViewPanel.setFocus();
	    }
		
 	}
 }
