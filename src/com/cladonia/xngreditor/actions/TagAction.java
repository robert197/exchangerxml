/*
 * $Id: TagAction.java,v 1.5 2004/10/28 16:17:07 edankert Exp $
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

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.TagDialog;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to tag the selected text.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
 public class TagAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private TagDialog dialog = null;
	private Editor editor = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which tags the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public TagAction( ExchangerEditor parent) {
		super( "Tag...");
		
		if (DEBUG) System.out.println( "TagAction( "+editor+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'T'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_T, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Tag16.gif"));
		putValue( SHORT_DESCRIPTION, "Tag...");

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
	 * The implementation of the tag action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "TagAction.actionPerformed( "+event+")");
		
		if ( dialog == null) {
			dialog = new TagDialog( parent);
		}

		dialog.init( editor);
		dialog.show();
		
		if ( !dialog.isCancelled()) {
			String tag = dialog.getTag();
			
			if ( tag != null && tag.trim().length() > 0) {
				parent.getRepeatTagAction().setTag( tag);
		 		editor.insertTag( tag);
			}
		}

		editor.setFocus();
 	}
}
