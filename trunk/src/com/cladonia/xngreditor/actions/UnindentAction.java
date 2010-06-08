/*
 * $Id: UnindentAction.java,v 1.1 2004/03/25 18:53:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to unindent the selected text.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:18 $
 * @author Dogsbay
 */
 public class UnindentAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;

 	/**
	 * The constructor for the action which unindents 
	 * the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public UnindentAction() {
		super( "Unindent");
		
		if (DEBUG) System.out.println( "UnindentAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'e'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_TAB, InputEvent.SHIFT_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Unindent16.gif"));
		putValue( SHORT_DESCRIPTION, "Unindent");
		
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
		
		setEnabled( editor != null);
	}

	/**
	 * The implementation of the unindent action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "UnindentAction.actionPerformed( "+event+")");
		
 		editor.unindentSelectedText();
		editor.setFocus();
 	}
}
