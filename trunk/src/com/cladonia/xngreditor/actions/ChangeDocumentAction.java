/*
 * $Id: ChangeDocumentAction.java,v 1.5 2004/07/28 17:00:00 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JTabbedPane;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ChangeDocumentDialog;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to change between documents.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/07/28 17:00:00 $
 * @author Dogs bay
 */
 public class ChangeDocumentAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ChangeDocumentDialog dialog = null;

 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows changing which document is active
	 */
 	public ChangeDocumentAction( ExchangerEditor parent) {
 		super( "Select document...");

		//putValue( MNEMONIC_KEY, new Integer('d'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_1,InputEvent.ALT_MASK, false));
		putValue( SHORT_DESCRIPTION,"Select document");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
	
	private ChangeDocumentDialog getDialog() {
		if ( dialog == null) {
			dialog = new ChangeDocumentDialog(parent);
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
		ChangeDocumentDialog dialog = getDialog();
		
		final JTabbedPane tab = parent.getTabbedPane();
		
		if ( parent.getViews().size() > 1) {
			dialog.show( parent.getViews(), parent.getView(), parent.getPreviousView());
	
			if (!dialog.isCancelled()) {
				ExchangerView view = dialog.getSelectedView();
				
				// select the chose tab
				parent.select( view);	
				view.getCurrentView().setFocus();
			}
		}
 	}
 }
