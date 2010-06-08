/*
 * $Id: ToggleBookmarkAction.java,v 1.3 2004/07/21 09:09:42 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.GotoDialog;

/**
 * An action that can be used to toggle a bookmark on the current line.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/07/21 09:09:42 $
 * @author Dogsbay
 */
 public class ToggleBookmarkAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private GotoDialog dialog = null;
	private Editor editor = null;
	private JFrame parent = null;

 	/**
	 * The constructor for the action which allows the user to 
	 * goto a specific line in the Xml Editor.
	 *
	 * @param editor the XML Editor
	 */
 	public ToggleBookmarkAction( JFrame parent) {
		super( "Toggle Bookmark");
		
		if (DEBUG) System.out.println( "ToggleBookmarkAction( "+parent+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'B'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_B, InputEvent.CTRL_MASK, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Goto16.gif"));
		putValue( SHORT_DESCRIPTION, "Toggle Bookmark");

	 	this.parent = parent;
		
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
	 * The implementation of the goto action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "ToggleBookmarkAction.actionPerformed( "+event+")");
		
		editor.toggleBookmarkCurrentLine();
		editor.setFocus();
 	}
}
