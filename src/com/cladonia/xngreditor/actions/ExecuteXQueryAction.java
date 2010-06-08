/*
 * $Id: ExecuteXQueryAction.java,v 1.3 2004/10/13 18:24:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xml.transform.ScenarioUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ExecuteXQueryDialog;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * An action that can be used to execute a XQuery.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/13 18:24:32 $
 * @author Dogsbay
 */
public class ExecuteXQueryAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ExecuteXQueryDialog dialog = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which executes a XQuery.
	 *
	 * @param parent the parent frame.
	 */
 	public ExecuteXQueryAction( ExchangerEditor parent) {
 		super( "Execute XQuery ...");

		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'X'));
		putValue( SHORT_DESCRIPTION, "Execute a XQuery");
 	}
 	
 	/**
 	 * The implementation of the execute XSLT action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		if ( dialog == null) {
			dialog = new ExecuteXQueryDialog( parent);
		}

		dialog.show( parent.getDocument());

		if ( !dialog.isCancelled()) {
			ScenarioProperties scenario = dialog.getScenario();
			parent.getExecutePreviousXQueryAction().setScenario( scenario);
			ScenarioUtilities.execute( parent.getDocument(), scenario);
		}
 	}
}
