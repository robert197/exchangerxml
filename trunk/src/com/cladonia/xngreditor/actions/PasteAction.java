/*
 * $Id: PasteAction.java,v 1.5 2005/05/06 14:46:35 tcurley Exp $
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
 * An action that can be used to paste information in 
 * the XML Document.
 *
 * @version	$Revision: 1.5 $, $Date: 2005/05/06 14:46:35 $
 * @author Dogsbay
 */
 public class PasteAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
    private Object view;

 	/**
	 * The constructor for the paste action.
	 *
	 * @param editor the editor to paste information to.
	 */
 	public PasteAction( ExchangerEditor parent) {
 		super( "Paste");

		putValue( MNEMONIC_KEY, new Integer( 'P'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Paste16.gif"));
		putValue( SHORT_DESCRIPTION, "Paste");
		
		this.parent = parent;
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		//setEnabled( view instanceof Editor);
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
	 * The implementation of the paste action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		/*((Editor)parent.getCurrent()).paste();
		((Editor)parent.getCurrent()).setFocus();*/
 	    
 	   if( view instanceof Editor) {
 	       Editor editor = (Editor)parent.getCurrent();
 	       editor.paste();
 	       editor.setFocus();
	    }
	    else if( view instanceof PluginViewPanel) {
	    	PluginViewPanel pluginViewPanel = (PluginViewPanel)parent.getCurrent();
	    	pluginViewPanel.paste();
	    	pluginViewPanel.setFocus();
	    }
 	}
 }
