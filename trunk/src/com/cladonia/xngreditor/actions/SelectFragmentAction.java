/*
 * $Id: SelectFragmentAction.java,v 1.2 2004/08/20 15:33:28 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.SelectFragmentDialog;
import com.cladonia.xngreditor.grammar.FragmentProperties;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to change between documents.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/08/20 15:33:28 $
 * @author Dogs bay
 */
 public class SelectFragmentAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private SelectFragmentDialog dialog = null;

 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows changing which document is active
	 */
 	public SelectFragmentAction( ExchangerEditor parent) {
 		super( "Insert Fragment...");

		putValue( MNEMONIC_KEY, new Integer('m'));
		putValue( SHORT_DESCRIPTION, "Insert Fragment");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
	
	private SelectFragmentDialog getDialog() {
		if ( dialog == null) {
			dialog = new SelectFragmentDialog( parent);
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
 		SelectFragmentDialog dialog = getDialog();
 		
 		GrammarProperties grammar = parent.getView().getGrammar();
 		
 		if ( grammar != null) {
			dialog.show( grammar.getFragments());
	
			if (!dialog.isCancelled()) {
				FragmentProperties fragment = dialog.getSelectedFragment();
	
				parent.getView().getEditor().insertFragment( fragment.isBlock(), fragment.getContent());
				parent.getView().getEditor().setFocus();
			}
 		}
 	}
 }
