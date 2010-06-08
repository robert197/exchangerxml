/*
 * $Id: CDATAAction.java,v 1.2 2004/10/28 16:17:07 edankert Exp $
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

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to Comment the selected text.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
 public class CDATAAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action that Comments the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public CDATAAction(  ExchangerEditor parent) {
		super( "CDATA");
		
		this.parent = parent;

		if (DEBUG) System.out.println( "CDATAAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'A'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_K, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CDATA16.gif"));
		putValue( SHORT_DESCRIPTION, "CDATA");
		
		setEnabled( false);
	}
	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	 public void setView( Object view) {
	 	if ( view instanceof Editor) {
	 		editor = (Editor)view;
	 	} else {
	 		editor = null;
	 	}
	 	
	 	setDocument( parent.getDocument());
	 }
	 
	 public void setDocument( ExchangerDocument doc) {
		 if ( doc != null) {
		 	setEnabled( editor != null);
		 } else {
		 	setEnabled( false);
		 }
	 }
 	
	/**
	 * Sets wether the action is being used 
	 * for CDATA or unCDATA.
	 *
	 * @param enabled enable un-CDATA.
	 */
	public void setUnCDATA( boolean enabled) {
		if ( enabled) {
			putValue( NAME, "Un-CDATA");
			putValue( SHORT_DESCRIPTION, "Un-CDATA");
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/UnCDATA16.gif"));
		} else {
			putValue( NAME, "CDATA");
			putValue( SHORT_DESCRIPTION, "CDATA");
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CDATA16.gif"));
		}
	}

	/**
	 * The implementation of the comment action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "CDATAAction.actionPerformed( "+event+")");
		
		editor.cdataSelectedText();
		editor.setFocus();
 	}
}
