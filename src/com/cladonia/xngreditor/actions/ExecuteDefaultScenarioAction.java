/*
 * $Id: ExecuteDefaultScenarioAction.java,v 1.5 2004/10/13 18:24:32 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.transform.ScenarioUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.scenario.ScenarioSelectionDialog;
/**

 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/13 18:24:32 $
 * @author Dogsbay
 */
public class ExecuteDefaultScenarioAction extends AbstractAction {
	private ExchangerEditor parent = null;	
	private ConfigurationProperties properties = null;
	private ScenarioSelectionDialog allDialog = null;
	private ScenarioSelectionDialog dialog = null;
//	private ExecuteScenarioDialog executeDialog = null;

 	/**
	 * The constructor for the action which changes Grammar properties.
	 *
	 * @param parent the parent frame.
	 */
 	public ExecuteDefaultScenarioAction( ExchangerEditor parent, ConfigurationProperties props) {
// 		super( parent, props, "Properties");
 		super( "Execute Scenario ...");
		
		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'E'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExecuteScenario16.gif"));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F4, 0, false));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Execute Scenario");
		
//		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the execute default scenario action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ExchangerDocument document = parent.getDocument();
		GrammarProperties grammar = parent.getGrammar();
		ScenarioProperties defaultScenario = null;
		
		Vector scenarios = null;

		if ( grammar != null) {
			scenarios = grammar.getScenarios();
			defaultScenario = grammar.getDefaultScenario();
		}
		
		if ( scenarios != null && scenarios.size() > 0) {
			if ( dialog == null) {
				dialog = new ScenarioSelectionDialog( parent, properties, "Execute", true, false);
			}
			
			dialog.show( scenarios, defaultScenario);
			
			if ( !dialog.isCancelled()) {
				ScenarioProperties scenario = dialog.getSelectedScenario();
				parent.getExecutePreviousScenarioAction().setScenario( scenario);
				ScenarioUtilities.execute( document, scenario);
			}
		} else {
			scenarios = properties.getScenarioProperties();
			
			if ( scenarios.size() > 0) {
				if ( allDialog == null) {
					allDialog = new ScenarioSelectionDialog( parent, properties, "Execute", false, false);
				}
				
				allDialog.show();

				if ( !allDialog.isCancelled()) {
					ScenarioProperties scenario = allDialog.getSelectedScenario();
					parent.getExecutePreviousScenarioAction().setScenario( scenario);

					ScenarioUtilities.execute( document, scenario);
				}
			} else {
				MessageHandler.showMessage( "No Scenarios Available.");
			}
		}
		
		ExchangerView view = parent.getView();
		
		if ( view != null) {
			view.getCurrentView().setFocus();
		}
	}
}
