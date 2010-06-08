/*
 * $Id: IndentAction.java,v 1.1 2004/03/25 18:53:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to indent the selected text.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:19 $
 * @author Dogsbay
 */
 public class IndentAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;

 	/**
	 * The constructor for the action which indents 
	 * the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public IndentAction() {
		super( "Indent");
		
		if (DEBUG) System.out.println( "IndentAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'I'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 0, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Indent16.gif"));
		putValue( SHORT_DESCRIPTION, "Indent");
		
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
 		if (DEBUG) System.out.println( "IndentAction.actionPerformed( "+event+")");
		
		editor.indentSelectedText( false);
		editor.setFocus();
 	}
}
