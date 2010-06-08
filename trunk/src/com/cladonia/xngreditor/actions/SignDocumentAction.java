/*
 * $Id: SignDocumentAction.java,v 1.15 2004/10/11 08:38:30 edankert Exp $
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

import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import com.cladonia.security.signature.KeyBuilder;
import com.cladonia.security.signature.SignDocumentDialog;
import com.cladonia.security.signature.SignatureGenerator;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerOutputFormat;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.SecurityPreferences;

/**
 * An action that can be used Sign a document.
 *
 * @version	$Revision: 1.15 $, $Date: 2004/10/11 08:38:30 $
 * @author Dogsbay
 */
public class SignDocumentAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ConfigurationProperties properties = null;

	private ExchangerEditor parent = null;
	private SignDocumentDialog dialog = null;

 	/**
	 * The constructor for the action which signs a xml document.
	 *
	 * @param parent the parent frame.
	 */
 	public SignDocumentAction( ExchangerEditor parent, ConfigurationProperties properties) {
 		super( "Sign Document ...");

		this.parent = parent;
		this.properties = properties;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( SHORT_DESCRIPTION, "Sign a XML Document");
 	}
 	
 	/**
 	 * The implementation of the sign document action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		if ( dialog == null) {
			dialog = new SignDocumentDialog( parent,properties);
		}
 		
 		// make sure the document is up to date
		ExchangerDocument document = parent.getDocument();
		Object current = null;
		
		if ( document != null) {
			current = parent.getView().getCurrentView();
			
			parent.getView().updateModel();
		}
		
		// set the keystore
		SecurityPreferences prefs = properties.getSecurityPreferences();
		try{
			KeyBuilder.setParams( 
					prefs.getKeystoreType(),
					prefs.getKeystoreFile(),
					prefs.getKeystorePassword(),
					prefs.getPrivatekeyAlias(),
					prefs.getPrivatekeyPassword(),
					prefs.getCertificateAlias());
		} catch ( Exception x) {
			MessageHandler.showError( "Could not find a valid private key.\nPlease make sure the private key settings are correct.", "Key Store Error");
			return;
		}

		dialog.show( document);

		if ( !dialog.isCancelled()) {
			try{
				Document input = null;
				ExchangerDocument currentDocument = document;
				
				if ( dialog.isInputCurrentDocument()) {
					input = currentDocument.getW3CDocument();
					//System.out.println( input.toString());
				} else if ( dialog.isEnvelope()){
					URL url = null;
				
					try {
						url = URLUtilities.toURL( dialog.getInputLocation());
						currentDocument = new ExchangerDocument( url);
						currentDocument.load();
						input = currentDocument.getW3CDocument();
					} catch (Exception x) {
						MessageHandler.showError( "Could not create XML Document \""+url.toString()+"\".", "XML Document Error");
						//x.printStackTrace();
						return;
					}
				}
				
				SignatureGenerator generator = null;
				
				if ( dialog.isEnvelope()) {
					generator = new SignatureGenerator( KeyBuilder.getPrivateKey(), KeyBuilder.getCertificate(), input);
				} else {
					if ( currentDocument != null) {
						generator = new SignatureGenerator( KeyBuilder.getPrivateKey(), KeyBuilder.getCertificate(), currentDocument.getURL().toString());
					} else {
						generator = new SignatureGenerator( KeyBuilder.getPrivateKey(), KeyBuilder.getCertificate(), dialog.getInputLocation());
					}
				}

				generator.setC14nAlgorithm( dialog.getC14NMethod());
				generator.setId( dialog.getID());
				generator.setXpath( dialog.getXPath());
				
				Document doc = generator.sign(properties);
				String encoding = "UTF-8";
				
				if ( currentDocument != null) {
					encoding = currentDocument.getEncoding();					
				}
				
				String text = XMLUtilities.serialise( doc, encoding);
				
				URL url = null;
				String systemId = null;
				
				if ( document != null) {
					document.getURL();
				}
				
				if ( url != null) {
					systemId = url.toString();
				}

				ExchangerOutputFormat format = new ExchangerOutputFormat();
				format.setEncoding( encoding);
				
				if ( currentDocument != null) {
					if ( currentDocument.hasDeclaration()) {
						if ( currentDocument.getStandalone() != ExchangerDocument.STANDALONE_NONE) {
							format.setStandalone( currentDocument.getStandalone());
							format.setOmitStandalone( false);
						}
						
						format.setOmitEncoding( !currentDocument.hasEncoding());
							
						format.setVersion( currentDocument.getVersion());
						format.setSuppressDeclaration( false);
					} else {
						format.setSuppressDeclaration( true);
					}
				}

				text = format( text, systemId, encoding, format);

				if ( dialog.isOutputToSameDocument()) {
					if ( dialog.isInputCurrentDocument()) {
						parent.switchToEditor();
						parent.getView().getEditor().setText( text);
						parent.getView().updateModel();
					} else {
						currentDocument.setText( text);
						parent.open( currentDocument, null);
					}
				} else {
					ExchangerDocument newDocument = FileUtilities.createDocument();
					newDocument.setText( text);
					parent.open( newDocument, null);
				}
				
			} catch ( Exception x) {
				MessageHandler.showError( "Could not create Signature.\n"+x.getMessage(), "Signature Error");
				x.printStackTrace();
				return;
			}
		}
 	}
 	
 	private String format( String text, String systemId, String encoding, ExchangerOutputFormat format) throws IOException, SAXParseException {
		String indent = "";
		boolean newLines = false;
		boolean padText = false;
		boolean preserveMixed = false;
		boolean trim = false;
		int lineLength = -1;
		
		return XMLUtilities.format( text, systemId, encoding, "", false, false, -1, false, false, format);
 	}
 	
}
