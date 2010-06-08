/*
 * $Id: RepeatTagAction.java,v 1.2 2004/10/28 16:17:07 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * An action that can be used to tag the selected text.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
 public class RepeatTagAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;
	private ExchangerEditor parent = null;
	private String tag = null;

 	/**
	 * The constructor for the action which tags the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public RepeatTagAction( ExchangerEditor parent) {
		super( "Repeat last Tag");
		
		if (DEBUG) System.out.println( "RepeatTagAction( "+editor+")");
		
		putValue( SHORT_DESCRIPTION, "Repeat last Tag...");

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
		if ( doc != null && tag != null) {
			setEnabled( editor != null);
		} else {
			setEnabled( false);
		}
	}
	
	public void setTag( String tag) {
		
		if ( tag != null && tag.trim().length() > 0) {
			this.tag = tag;
		} else {
			this.tag = null;
		}
		
		setDocument( parent.getDocument());
	}

	/**
	 * The implementation of the tag action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "RepeatTagAction.actionPerformed( "+event+")");
		
		if ( tag != null) {
	 		editor.insertTag( tag);
		}

		editor.setFocus();
 	}
}
