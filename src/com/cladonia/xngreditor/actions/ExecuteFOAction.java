/*
 * $Id: ExecuteFOAction.java,v 1.2 2004/10/13 18:24:32 edankert Exp $
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
import com.cladonia.xngreditor.scenario.ExecuteFODialog;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/13 18:24:32 $
 * @author Dogsbay
 */
public class ExecuteFOAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ExecuteFODialog dialog = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ExecuteFOAction( ExchangerEditor parent) {
 		super( "Execute FO ...");

		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'F'));
		putValue( SHORT_DESCRIPTION, "Execute a FO Transformation");
 	}
 	
 	/**
 	 * The implementation of the execute XSLT action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		if ( dialog == null) {
			dialog = new ExecuteFODialog( parent);
		}

		dialog.show( parent.getDocument());

		if ( !dialog.isCancelled()) {
			ScenarioProperties scenario = dialog.getScenario();
			parent.getExecutePreviousFOAction().setScenario( scenario);
			ScenarioUtilities.execute( parent.getDocument(), scenario);
		}
 	}
}
