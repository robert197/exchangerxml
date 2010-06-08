/*
 * $Id: LockAction.java,v 1.3 2004/05/06 11:09:59 edankert Exp $
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

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to Comment the selected text.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/06 11:09:59 $
 * @author Dogsbay
 */
 public class LockAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action that Comments the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public LockAction( ExchangerEditor parent) {
		super( "Unlock");
		
		this.parent = parent;
		
		if (DEBUG) System.out.println( "LockAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'l'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_L, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Unlock16.gif"));
		putValue( SHORT_DESCRIPTION, "Unlock");
		
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
		 if ( doc != null && (doc.isXML() || doc.isDTD())) {
		 	setEnabled( editor != null);
		 } else {
		 	setEnabled( false);
		 }
	}

	/**
	 * Sets wether the action is being used 
	 * for commenting or uncommenting.
	 *
	 * @param enabled enable un-commenting.
	 */
	public void setUnlock( int value) {
		if ( value == XmlEditorPane.NOT_LOCKED) {
			putValue( NAME, "Lock");
			putValue( SHORT_DESCRIPTION, "Allow all changes.");
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Unlock16.gif"));
		} else if ( value == XmlEditorPane.LOCKED){
			putValue( NAME, "Double Lock");
			putValue( SHORT_DESCRIPTION, "Allow Attribute and Element content changes.");
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Lock16.gif"));
		} else if ( value == XmlEditorPane.DOUBLE_LOCKED){
			putValue( NAME, "Unlock");
			putValue( SHORT_DESCRIPTION, "Allow Element content changes.");
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/DoubleLock16.gif"));
		}
	}

	/**
	 * The implementation of the comment action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "CommentAction.actionPerformed( "+event+")");
		
		editor.lock();
		editor.setFocus();
 	}
}
