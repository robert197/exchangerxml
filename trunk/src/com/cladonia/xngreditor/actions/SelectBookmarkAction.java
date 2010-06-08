/*
 * $Id: SelectBookmarkAction.java,v 1.3 2004/07/21 09:09:42 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.editor.Bookmark;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.SelectBookmarkDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to change between documents.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/07/21 09:09:42 $
 * @author Dogs bay
 */
 public class SelectBookmarkAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private SelectBookmarkDialog dialog = null;

 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows changing which document is active
	 */
 	public SelectBookmarkAction( ExchangerEditor parent) {
 		super( "Select Bookmark...");

		putValue( MNEMONIC_KEY, new Integer('o'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_B, InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK, false));
		putValue( SHORT_DESCRIPTION, "Select Bookmark");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
	
	private SelectBookmarkDialog getDialog() {
		if ( dialog == null) {
			dialog = new SelectBookmarkDialog( parent);
		}
		
		return dialog;
	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		setEnabled( ( view instanceof Editor));
	}

	/**
	 * The implementation of the Select document action
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		SelectBookmarkDialog dialog = getDialog();
		
		dialog.show( parent.getView().getEditor().getBookmarks());

		if (!dialog.isCancelled()) {
			Bookmark bookmark = dialog.getSelectedBookmark();
			parent.getView().getEditor().selectLineWithoutEnd( bookmark.getLineNumber()+1);
		}
 	}
 }
