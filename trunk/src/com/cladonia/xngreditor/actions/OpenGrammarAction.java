/*
 * $Id: OpenGrammarAction.java,v 1.3 2004/09/23 10:48:09 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.GrammarSelectionDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/23 10:48:09 $
 * @author Dogsbay
 */
public class OpenGrammarAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public OpenGrammarAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Set Type ...");
// 		super( parent, props, "Open");

		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( SHORT_DESCRIPTION, "Open a XML Type");
 	}
 	
 	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		ExchangerDocument doc = parent.getDocument();
 		GrammarProperties grammar = parent.getGrammar();
 		
 		// bring up a type dialog and let the user 
 		// change the type for this document...
 		GrammarSelectionDialog dialog = FileUtilities.getGrammarSelectionDialog();
 		dialog.setProperties( properties.getGrammarProperties());
		dialog.setSelectedGrammar( grammar);
 		dialog.setVisible( true);
 			
 		if ( !dialog.isCancelled()) {
			grammar = dialog.getSelectedType();
 			XMLSchema schema = FileUtilities.createSchema( doc, grammar);
 		
 			parent.setGrammar( grammar);
 			parent.setSchema( schema);
 			parent.setTagCompletionSchemas( FileUtilities.createTagCompletionSchemas( doc, grammar, schema));

 			parent.updateStatus();
 			parent.getView().updateValidationGrammar();

// 			parent.setGrammar( grammar);
// 			parent.setRelax( relax);
// 			parent.setDTD( dtd);
// 			parent.setSchema( schema);
 		} 
 	}
}
