/*
 * $Id: NewGrammarAction.java,v 1.4 2004/09/23 10:48:09 edankert Exp $
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

import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLGrammar;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.GrammarPropertiesDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to create a new XML Type.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/09/23 10:48:09 $
 * @author Dogsbay
 */
 public class NewGrammarAction extends AbstractAction {
 	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which creates a new
	 * document.
	 *
	 * @param parent the parent frame.
	 */
 	public NewGrammarAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Create Type");
// 		super( parent, props, "New");

		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'C'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "Create XML Type");
 	}
 	
	/**
	 * The implementation of the new grammar action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ExchangerView view = parent.getView();
		GrammarProperties grammar = null;
		
		if ( view != null) {
			ExchangerDocument doc = view.getDocument();
			XMLGrammar validationGrammar = view.getValidationGrammar();
			grammar = new GrammarProperties( properties, doc);

			XMLSchema schema = view.getSchema();

//			if ( schema != null) {
//				grammar.setTemplateLocation( schema.getURL().toString());
//			}

			if ( validationGrammar.getLocation() != null && validationGrammar.getLocation().trim().length() > 0) {
				grammar.setValidationGrammar( validationGrammar.getType());
				grammar.setValidationLocation( validationGrammar.getLocation());
				grammar.setUseXMLValidationLocation( !validationGrammar.useExternal());
			}
		} else {
			grammar = new GrammarProperties( properties);
		}
		
		Vector gs = properties.getGrammarProperties();
		Vector names = new Vector();

		for ( int i = 0; i < gs.size(); i++) {
			String name = ((GrammarProperties)gs.elementAt( i)).getDescription();

			names.addElement( name);
		}

		// bring up a type creation dialog and let the user 
		// create a type for this document...
		GrammarPropertiesDialog dialog = FileUtilities.getGrammarPropertiesDialog( "Create", true);
		dialog.show( grammar, true, names);
			
		if ( !dialog.isCancelled()) {
			boolean grammarUpdated = false;
			
			for ( int i = 0; i < gs.size(); i++) {
				String name = ((GrammarProperties)gs.elementAt( i)).getDescription();

				if ( name.equals( grammar.getDescription())) {
					GrammarProperties oldGrammar = (GrammarProperties)gs.elementAt( i);
					oldGrammar.update( grammar);
					grammar = oldGrammar;
					grammarUpdated = true;
					break;
				}
			}
			
			if ( !grammarUpdated) {
				properties.addGrammarProperties( grammar);
			}
			
			if ( view != null) {
				ExchangerDocument doc = view.getDocument();
				XMLSchema schema = FileUtilities.createSchema( doc, grammar);
			
				parent.setGrammar( grammar);
				parent.setSchema( schema);
				parent.setTagCompletionSchemas( FileUtilities.createTagCompletionSchemas( doc, grammar, schema));

				parent.updateStatus();
				parent.getView().updateValidationGrammar();
			}
	

			parent.updateGrammarActions();
			properties.save();
		} 
	}
}
