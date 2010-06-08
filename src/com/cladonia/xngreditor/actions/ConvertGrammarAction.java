/*
 * $Id: ConvertGrammarAction.java,v 1.2 2004/05/31 09:04:20 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;

import com.cladonia.schema.converter.ConverterDialog;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;

/**
 * Converts one grammar to another, using Trang.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/31 09:04:20 $
 * @author Dogsbay
 */
public class ConvertGrammarAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ConverterDialog dialog = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public ConvertGrammarAction( ExchangerEditor parent) {
 		super( "Convert Schema");

		putValue( MNEMONIC_KEY, new Integer( 'C'));
		putValue( SHORT_DESCRIPTION, "Convert Schema");
		
		this.parent = parent;
 	}
 	
 	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
		if ( dialog == null) {
			dialog = new ConverterDialog( parent);
		}
		
		ExchangerDocument doc = parent.getDocument();
		URL url = null;
		
		if ( doc != null) {
			url = doc.getURL();
		}

		dialog.show( url);
		
		ExchangerView view = parent.getView();
		
		if ( view != null) {
			view.getCurrentView().setFocus();
		}
 	}
}
