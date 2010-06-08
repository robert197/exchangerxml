/*
 * $Id: SaveScenarioAction.java,v 1.4 2005/03/16 17:49:19 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.scenario.ScenarioPropertiesDialog;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;
import com.cladonia.xslt.debugger.ui.XSLTTransformation;

/**
 * An action that can be used to save a scenario.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/03/16 17:49:19 $
 * @author Dogsbay
 */
public class SaveScenarioAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ScenarioPropertiesDialog scenarioPropertiesDialog = null;

	private XSLTDebuggerFrame parent = null;
	private XSLTDebuggerPane debugger = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public SaveScenarioAction( XSLTDebuggerFrame parent, XSLTDebuggerPane debugger, ConfigurationProperties props) {
 		super( "Save As Scenario ...");

		this.parent = parent;
		this.debugger = debugger;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Save the current Transformation as a Scenario");
 	}
 	
 	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		execute();
 	}
 	
	private ScenarioPropertiesDialog getScenarioPropertiesDialog() {
		if ( scenarioPropertiesDialog == null) {
			scenarioPropertiesDialog = new ScenarioPropertiesDialog( parent);
		} 
		
		return scenarioPropertiesDialog;
	}

	public void execute() {
 		XSLTTransformation transformation = debugger.getTransformation();
 		
 		if ( transformation != null) {
 		  ScenarioProperties currentScenario = transformation.getScenario();
 		 
 		  if (currentScenario == null)
 		  {
	 		   ScenarioProperties scenario = transformation.createScenario();
	 		 
	 		    
		 		ScenarioPropertiesDialog dialog = getScenarioPropertiesDialog();
		 		
		 		Vector scs = properties.getScenarioProperties();
		 		Vector names = new Vector();
	
		 		for ( int i = 0; i < scs.size(); i++) {
		 			String name = ((ScenarioProperties)scs.elementAt( i)).getName();
	
					if ( !name.equals( scenario.getName())) {
			 			names.addElement( name);
					}
		 		}
	
		 		dialog.show( scenario, null, names);
		 		
		 		if ( dialog.isCancelled()) {
		 			return;
		 		}
		 		
		 		transformation.setScenario( scenario);
		 		properties.addScenarioProperties( scenario);
		 		
		 		transformation.save();
 		  }
 		  else
 		  {
		 		ScenarioPropertiesDialog dialog = getScenarioPropertiesDialog();
		 		
		 		Vector scs = properties.getScenarioProperties();
		 		Vector names = new Vector();
		
		 		for ( int i = 0; i < scs.size(); i++) {
		 			String name = ((ScenarioProperties)scs.elementAt( i)).getName();
		
					if ( !name.equals( currentScenario.getName())) {
			 			names.addElement( name);
					}
		 		}
		
		 		dialog.show( currentScenario, null, names);
		 		
		 		if ( dialog.isCancelled()) {
		 			return;
		 		}
		 		
		 		transformation.setScenario( currentScenario);
		 		//properties.addScenarioProperties( currentScenario);
		 		
		 		transformation.save();
		 		 		    
 		  }
	 	
	
 		}
 	}
}
