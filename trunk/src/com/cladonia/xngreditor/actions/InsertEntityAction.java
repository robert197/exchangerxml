/*
 * $Id: InsertEntityAction.java,v 1.4 2004/10/28 16:17:07 edankert Exp $
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
import com.cladonia.xngreditor.EntitySelectionDialog;
import com.cladonia.xngreditor.ExchangerEditor;

/**
 * An action that can be used to indent the selected text.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
 public class InsertEntityAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;
	private ExchangerEditor parent = null;
	private EntitySelectionDialog dialog = null;

 	/**
	 * The constructor for the action which indents 
	 * the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public InsertEntityAction( ExchangerEditor parent) {
		super( "Insert Special Character");
		
		this.parent = parent;
		
		if (DEBUG) System.out.println( "InsertEntityAction( "+editor+")");
		
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_I, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Insert Special Character");
		
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
	
	public void updatePreferences() {
		getEntitySelectionDialog().updatePreferences();
	}
	
	private EntitySelectionDialog getEntitySelectionDialog() {
		if ( dialog == null) {
			dialog = new EntitySelectionDialog( parent);
		}
		
		return dialog;
	}
	
	/**
	 * The implementation of the unindent action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "IndentAction.actionPerformed( "+event+")");

		execute();
 	}
	
	public void execute() {
		EntitySelectionDialog dialog = getEntitySelectionDialog();
		dialog.show();
		
		if ( !dialog.isCancelled()) {
			String value = dialog.getSelectedValue();

			if ( value != null) {
				editor.insert( value);
			}
		}
	}
}
