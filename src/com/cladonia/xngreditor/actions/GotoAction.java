/*
 * $Id: GotoAction.java,v 1.6 2004/10/27 16:23:53 edankert Exp $
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
import javax.swing.JFrame;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.GotoDialog;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to goto a specific line in the
 * Xml Editor.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/10/27 16:23:53 $
 * @author Dogsbay
 */
 public class GotoAction extends AbstractAction {
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
 	public GotoAction( JFrame parent) {
		super( "Goto...");
		
		if (DEBUG) System.out.println( "GotoAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'G'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_G, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Goto16.gif"));
		putValue( SHORT_DESCRIPTION, "Goto...");

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
 		if (DEBUG) System.out.println( "GotoAction.actionPerformed( "+event+")");
		
		if ( dialog == null) {
			dialog = new GotoDialog( parent);
		}

		dialog.show();
		editor.setFocus();
		
		if ( !dialog.isCancelled()) {
			editor.gotoLine( dialog.getLine());
		}

 	}
}
