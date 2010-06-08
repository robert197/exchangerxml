/*
 * $Id: VerifySignatureAction.java,v 1.5 2004/05/25 09:09:11 knesbitt Exp $
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

import com.cladonia.security.signature.SignatureVerifier;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;

/**
 * An action that can be used Sign a document.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/05/25 09:09:11 $
 * @author Dogsbay
 */
public class VerifySignatureAction extends AbstractAction {
 	private static final boolean DEBUG = false;
 	
 	private ExchangerEditor parent;
	
 	/**
	 * The constructor for the action which signs a xml document.
	 *
	 * @param parent the parent frame.
	 */
 	public VerifySignatureAction( ExchangerEditor parent) {
 		super( "Verify Signature");
 		
 		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'g'));
		putValue( SHORT_DESCRIPTION, "Verify Signature");
		
		setEnabled( false);
 	}
 	
 	/**
 	 * The implementation of the sign document action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		// make sure the document is up to date
		ExchangerDocument document = parent.getDocument();
		Object current = null;

		parent.getView().updateModel();
		
		try	{
			URL url = document.getURL();
			String baseURI = "file:";

			if ( url != null) {
				baseURI = url.toString();
			}
			
			SignatureVerifier verifier = new SignatureVerifier( document.getW3CDocument(), baseURI);
			boolean valid = verifier.verify();
			
			if ( valid) { 
				MessageHandler.showMessage( "Valid Signature.");
			} else {
				MessageHandler.showError( "Invalid Signature.", "Signature Error");
			}
			
		} catch ( Exception x) {
			MessageHandler.showError( "Could not verify Signature.\n"+x.getMessage(), "Signature Error");
			x.printStackTrace();
			return;
		}
 	}
}
