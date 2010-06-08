/*
 * $Id: PrintAction.java,v 1.7 2004/07/21 09:09:42 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.awt.print.PrinterException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.text.PlainDocument;

import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.TextPrinter;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.PrintPreferences;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * An action that can be used to print the XML Document.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/07/21 09:09:42 $
 * @author Dogsbay
 */
 public class PrintAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which prints the current document.
	 *
	 * @param parent the ExchangerEditor main class.
	 */
 	public PrintAction( ExchangerEditor parent, ConfigurationProperties properties) {
		super( "Print");
		
		if (DEBUG) System.out.println( "PrintAction( "+parent+", "+properties+")");
		
		putValue( MNEMONIC_KEY, new Integer( 'P'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_P, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Print16.gif"));
		putValue( SHORT_DESCRIPTION, "Print");
		
		this.properties = properties;
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the print action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "PrintAction.actionPerformed( "+event+")");
		Editor editor = parent.getView().getEditor();
		
		parent.getView().updateModel();
		
		TextPrinter printer = TextPrinter.getPrinter();
		PrintPreferences prefs = properties.getPrintPreferences();
		
		printer.setFont( prefs.getFont());
		printer.setPrintHeader( prefs.isPrintHeader());
		printer.setPrintLineNumber( prefs.isPrintLineNumbers());
		printer.setWrapText( prefs.isWrapText());
		
		String location = "New Document";
		URL url = parent.getDocument().getURL();
		
		if ( url != null) {
			location = url.toString();
		}
		
		try {
			printer.print( (PlainDocument)editor.getEditor().getDocument(), location, TextPreferences.getTabSize());
		} catch ( PrinterException e) {
			MessageHandler.showError( "Error printing the Document", "Printing Error");
			e.printStackTrace();
		}
		
		ExchangerView view = parent.getView();
		
		if ( view != null) {
			view.getCurrentView().setFocus();
		}
 	}
}
