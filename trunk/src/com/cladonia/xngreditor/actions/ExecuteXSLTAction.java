/*
 * $Id: ExecuteXSLTAction.java,v 1.4 2005/03/16 17:46:50 gmcgoldrick Exp $
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
import com.cladonia.xngreditor.scenario.ExecuteXSLTDialog;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/03/16 17:46:50 $
 * @author Dogsbay
 */
public class ExecuteXSLTAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ExecuteXSLTDialog dialog = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ExecuteXSLTAction( ExchangerEditor parent) {
 		super( "Execute Advanced XSLT ...");

		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( SHORT_DESCRIPTION, "Execute a complex XSLT Transformation");
 	}
 	
 	/**
 	 * The implementation of the execute XSLT action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		if ( dialog == null) {
			dialog = new ExecuteXSLTDialog( parent);
		}

	
		dialog.show( parent.getDocument());

		if ( !dialog.isCancelled()) {
			ScenarioProperties scenario = dialog.getScenario();
			
			parent.getExecutePreviousXSLTAction().setScenario( scenario);
			ScenarioUtilities.execute( parent.getDocument(), scenario);
		}
 	}
}
