/*
 * $Id: ManageGrammarAction.java,v 1.1 2004/03/25 18:53:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.grammar.GrammarManagementDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to create a new XML Type.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:53:19 $
 * @author Dogsbay
 */
 public class ManageGrammarAction extends AbstractAction {
 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	private GrammarManagementDialog dialog = null;

 	/**
	 * The constructor for the action which creates a new
	 * document.
	 *
	 * @param parent the parent frame.
	 */
 	public ManageGrammarAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Manage Types");

		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'M'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Manage Types");
 	}
 	
	/**
	 * The implementation of the new grammar action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		if ( dialog == null) {
			dialog = new GrammarManagementDialog( parent, properties);
		}
		
		dialog.show();
		
		parent.updateGrammarActions();
	}
}
