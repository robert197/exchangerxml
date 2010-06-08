/*
 * $Id: GrammarPropertiesAction.java,v 1.6 2004/09/28 13:51:01 edankert Exp $
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

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.GrammarPropertiesDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/09/28 13:51:01 $
 * @author Dogsbay
 */
public class GrammarPropertiesAction extends AbstractAction {
	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which changes Grammar properties.
	 *
	 * @param parent the parent frame.
	 */
 	public GrammarPropertiesAction( ExchangerEditor parent, ConfigurationProperties props) {
// 		super( parent, props, "Properties");
 		super( "Type Properties");
		
		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'P'));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Schema16.gif"));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_N, InputEvent.CTRL_MASK, false));
		putValue( SHORT_DESCRIPTION, "XML Type Properties");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the new grammar action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		ExchangerDocument doc = parent.getDocument();
		GrammarProperties grammar = parent.getGrammar();
		
		Vector gs = properties.getGrammarProperties();
		Vector names = new Vector();

		for ( int i = 0; i < gs.size(); i++) {
			String name = ((GrammarProperties)gs.elementAt( i)).getDescription();
			
			if ( !name.equals( grammar.getDescription())) {
				names.addElement( name);
			}
		}

		// bring up a type dialog and let the user 
		// change the type for this document...
		GrammarPropertiesDialog dialog = FileUtilities.getGrammarPropertiesDialog( "OK", false);
		dialog.show( grammar, false, names);
			
		if ( !dialog.isCancelled()) {
			parent.updateGrammar( grammar);
			properties.save();
		} 
	}
}
