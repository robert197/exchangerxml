/*
 * $Id: CanonicalizeAction.java,v 1.13 2004/10/11 08:38:29 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
//import javax.swing.SwingUtilities;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.C14NDialog;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.FileUtilities;
import org.xml.sax.SAXParseException;

/**
 * An action that can be used to parse the XML content.
 *
 * @version	$Revision: 1.13 $, $Date: 2004/10/11 08:38:29 $
 * @author Dogsbay
 */
 public class CanonicalizeAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	private Editor editor = null;
	private C14NDialog dialog = null;
	private ConfigurationProperties props = null;
	
 	/**
	 * The constructor for the action which allows for validating
	 * the XML content.
	 *
	 * @param editor the XML Editor
	 */
 	public CanonicalizeAction( ExchangerEditor parent,ConfigurationProperties props) {
		super( "Canonicalize");

		putValue( MNEMONIC_KEY, new Integer( 'a'));
		putValue( SHORT_DESCRIPTION, "Canonicalize");
		
		this.parent = parent;
		this.props = props;
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( view instanceof Editor) {
			editor = (Editor)view;
		} else {
			editor = null;
		}

		setDocument( parent.getDocument());
	}
	
	public void setDocument( ExchangerDocument doc) {
		 if ( doc != null && doc.isXML()) {
		 	setEnabled( editor != null);
		 } else {
		 	setEnabled( false);
		 }
	}
	
	/**
 	 * The implementation of the Canonicalization action
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) 
	{
 		// make sure the document is up to date
 		final ExchangerView view = parent.getView();
		view.updateModel();

		// get the document
		ExchangerDocument document = parent.getDocument();
		
		if ( document.isError()) {
			MessageHandler.showError( "Please make sure the document is well-formed.", "Parser Error");
			return;
		}

		if (dialog == null)
		{
			dialog = new C14NDialog(parent,props);
		}
				
		if (document != null)
		{
			dialog.show(document);
		}
		
		if (!dialog.isCancelled()) 
		{
			try
			{
				
				// get the cano name
				String name = dialog.getC14N();
				
				// get the xpath expression
				String xpathPredicate = dialog.getXpathPredicate();
				
				// get the updated text with the new declaration
				final String updatedText = XMLUtilities.canonicalize(document,name,xpathPredicate,props);
				
				ExchangerDocument currentDocument = document;
				
				if ( dialog.isOutputToSameDocument()) 
				{
					parent.switchToEditor();
					parent.getView().getEditor().setText(updatedText);
					parent.getView().updateModel();
				}	
				else 
				{
					ExchangerDocument newDocument = FileUtilities.createDocument();
					try{
						newDocument.setText(updatedText);
					}
					catch(SAXParseException saxerr)
					{
						// ignore as result of cano is not always well-formed XML
					}
					parent.open( newDocument, null);
				}
			} 
			catch ( final Exception err) {
				MessageHandler.showError( err, "Canonicalization Error");
				err.printStackTrace();
			} 
			catch ( final Throwable t) {
				t.printStackTrace();
			}
		}
		
 	}
}
