/*
 * $Id: SendSOAPAction.java,v 1.7 2005/03/16 17:53:14 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;

import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.webservice.soap.SOAPDialog;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XMLDocumentChooserDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * Sends a SOAP message.
 *
 * @version	$Revision: 1.7 $, $Date: 2005/03/16 17:53:14 $
 * @author Dogsbay
 */
public class SendSOAPAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ConfigurationProperties properties = null;
	private SOAPDialog dialog = null;
	private XMLDocumentChooserDialog chooser = null;
	private ExchangerEditor parent = null;
	private ExchangerDocument document = null;

 	/**
	 * The constructor for the action which allows for sending a SOAP message.
	 *
	 * @param parent the parent frame.
	 */
 	public SendSOAPAction( ExchangerEditor parent, ConfigurationProperties properties) {
 		super( "Send SOAP Message");

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( SHORT_DESCRIPTION, "Send a SOAP Message");
		
		this.parent = parent;
		this.properties = properties;
	}
 	
 	public void updatePreferences() {
 		if ( dialog != null) {
 			dialog.updatePreferences();
 		}
 	}
	
 	/**
 	 * The implementation of the send SOAP action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		// System.out.println( "SendSOAPAction.actionPerformed( "+e+")");
		if ( chooser == null) {
			chooser = new XMLDocumentChooserDialog( parent, "Open SOAP Document", "Specify SOAP Document location", parent, false);
		}
		
		ExchangerView view = parent.getView();
		
		if ( view != null) {
			view.updateModel();
		}

		ExchangerDocument document = parent.getDocument();
		
		if ( document != null) {
			chooser.show( document.isSOAP());
		} else{
			chooser.show( false);
		}
		
		if ( !chooser.isCancelled()) {
			try {
				if ( chooser.isOpenDocument()) {				  
				  document = chooser.getOpenDocument();				  
				}  
				else if ( !chooser.isCurrentDocument()) {
					URL url = new URL( chooser.getInputLocation());

					document = new ExchangerDocument( url);
					document.load();
				}

				if ( dialog == null) {
					dialog = new SOAPDialog( parent, properties);
				}

				dialog.show( document);
			} catch ( IOException x) {
				MessageHandler.showError( "Could not create the Document:\n"+chooser.getInputLocation(), "Document Error");
			} catch ( SAXParseException x) {
				MessageHandler.showError( "Could not parse the Document.", x, "Document Error");
			}
		}
 	}
}
