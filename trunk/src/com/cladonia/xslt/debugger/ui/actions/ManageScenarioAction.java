/*
 * $Id: ManageScenarioAction.java,v 1.1 2004/03/25 18:58:13 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioManagementDialog;

/**
 * An action that can be used to manage scenarios.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:58:13 $
 * @author Dogsbay
 */
 public class ManageScenarioAction extends AbstractAction {
 	private JFrame parent = null;
	private ConfigurationProperties properties = null;
	private ScenarioManagementDialog dialog = null;

 	/**
	 * The constructor for the action which creates a new
	 * document.
	 *
	 * @param parent the parent frame.
	 */
 	public ManageScenarioAction( JFrame parent, ConfigurationProperties props) {
 		super( "Manage Scenarios");

		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'M'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Manage Scenarios");
 	}
 	
	/**
	 * The implementation of the manage scenarios action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		if ( dialog == null) {
			dialog = new ScenarioManagementDialog( parent, properties);
		}
		
		dialog.show();
		
//		parent.updateScenarioActions();
	}
}
