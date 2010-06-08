/*
 * $Id: OpenScenarioAction.java,v 1.3 2004/10/13 18:36:29 edankert Exp $
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
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.scenario.ScenarioPropertiesDialog;
import com.cladonia.xngreditor.scenario.ScenarioSelectionDialog;
import com.cladonia.xslt.debugger.ui.XSLTDebugController;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;
import com.cladonia.xslt.debugger.ui.XSLTTransformation;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/13 18:36:29 $
 * @author Dogsbay
 */
public class OpenScenarioAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ScenarioPropertiesDialog scenarioPropertiesDialog = null;

	private XSLTDebuggerFrame parent = null;
	private XSLTDebuggerPane debugger = null;
	private ConfigurationProperties properties = null;
	private ScenarioSelectionDialog dialog = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public OpenScenarioAction( XSLTDebuggerFrame parent, XSLTDebuggerPane debugger, ConfigurationProperties props) {
 		super( "From Scenario ...");

		this.parent = parent;
		this.properties = props;
		this.debugger = debugger;

//		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Open a Transformation from a Scenario");
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
 			XSLTDebugController controller = debugger.getDebugController();
 			
 			if ( controller != null && controller.isRunning()) {
				int result = JOptionPane.showConfirmDialog( parent,
						"Do you want to stop the Debugger?",
						"Please Confirm",
						JOptionPane.YES_NO_OPTION);
				
				if ( result == JOptionPane.NO_OPTION) {
					return;
				} else {
					debugger.stopDebugging();
				}
 			}

 			if ( transformation.getScenario() == null) {
				int result = JOptionPane.showConfirmDialog( parent,
									"Do you want to save the current XSLT Transformation as a Scenario?",
									"Please Confirm",
									JOptionPane.YES_NO_CANCEL_OPTION);
	
				if ( result == JOptionPane.CANCEL_OPTION) {
					return;
				} else if ( result == JOptionPane.OK_OPTION) {
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
				}
	 		}
	 		
	 		transformation.save();
 		}

		ScenarioProperties scenario = null;
		Vector scenarios = null;
	
		if ( dialog == null) {
			dialog = new ScenarioSelectionDialog( parent, properties, "Open", false, false);
		}
	
		dialog.show();
		
		if ( !dialog.isCancelled()) {
			parent.setTransformation( new XSLTTransformation( dialog.getSelectedScenario()));
		}
 	}
}
