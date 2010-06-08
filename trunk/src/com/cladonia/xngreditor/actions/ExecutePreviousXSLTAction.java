/*
 * $Id: ExecutePreviousXSLTAction.java,v 1.2 2005/03/16 17:45:15 gmcgoldrick Exp $
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
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/03/16 17:45:15 $
 * @author Dogsbay
 */
public class ExecutePreviousXSLTAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ScenarioProperties scenario = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ExecutePreviousXSLTAction( ExchangerEditor parent) {
// 		super( "Repeat XSLT");
 		super( "Execute Previous XSLT");

		this.parent = parent;

		putValue( SHORT_DESCRIPTION, "Execute the previous XSLT Transformation");
		
		setEnabled( false);
 	}
 	
 	public void setScenario( ScenarioProperties scenario) {
 		this.scenario = scenario;
 		
 		setEnabled( scenario != null);
 	}
 	
 	/**
 	 * The implementation of the execute XSLT action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
		ScenarioUtilities.execute( parent.getDocument(), scenario);
 	}
}
