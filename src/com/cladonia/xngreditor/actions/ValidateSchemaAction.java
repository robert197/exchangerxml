/*
 * $Id: ValidateSchemaAction.java,v 1.4 2004/10/11 14:30:12 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.AbstractAction;

import org.xml.sax.SAXParseException;

import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLError;
import com.cladonia.xml.XMLErrorHandler;
import com.cladonia.xml.XMLErrorReporter;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;

/**
 * An action that can be used to validate an XML Schema document.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/11 14:30:12 $
 * @author Dogsbay
 */
 public class ValidateSchemaAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the action which allows for validating
	 * a XML Schema document.
	 *
	 * @param parent the main parent frame
	 */
 	public ValidateSchemaAction( ExchangerEditor parent) {
		super( "Validate XML Schema");

		putValue( MNEMONIC_KEY, new Integer( 'X'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F8, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Validate16.gif"));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Parse16.gif"));
		putValue( SHORT_DESCRIPTION, "Validate XML Schema");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( view instanceof SchemaViewer) {
			setEnabled( false);
		} else {
			setEnabled( parent.isSchemaDocument());
		}
	}

	/**
	 * The implementation of the validate action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
		
		parent.setWait( true);
		parent.setStatus( "Validating ...");

		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {
				try {
					execute();
				} finally {
					parent.setWait( false);
					parent.setStatus( "Done");
				}
		 	}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
 	}
 	
 	public void execute() {
		try {
			parent.getView().updateModel();
			ExchangerDocument doc = parent.getView().getDocument();

			if ( doc.isError()) {
				return;
			}

			ByteArrayInputStream stream = new ByteArrayInputStream( doc.getText().getBytes( doc.getJavaEncoding()));
			InputStreamReader reader = new InputStreamReader( stream, doc.getJavaEncoding());
			String systemId = null;
			URL url = doc.getURL();

			if ( url != null) {
				systemId = url.toString();
			}

			parent.getOutputPanel().startCheck( "VAL XSD", "["+FileUtilities.getXercesVersion()+"] Validating XML Schema \""+doc.getName()+"\" ...");


			XMLErrorHandler handler = new XMLErrorHandler( new XMLErrorReporter() {
				public void report( XMLError error) {
					parent.getOutputPanel().addError( "VAL XSD", error);
				}
			}, 100);

			XMLUtilities.validateSchema( handler, new BufferedReader( reader), systemId, doc.getEncoding());

			if ( !handler.hasErrors()) {
				parent.getOutputPanel().endCheck( "VAL XSD", "Valid XML Schema.");
			} else {
				int errors = handler.getErrors().size();
				if ( errors > 1) {
					parent.getOutputPanel().endCheck( "VAL XSD", errors+" Errors");
				} else {
					parent.getOutputPanel().endCheck( "VAL XSD", "1 Error");
				}
			}
		} catch ( final SAXParseException e) {
		} catch ( final IOException e) {
			parent.getOutputPanel().setError( "VAL XSD", (IOException)e);
			parent.getOutputPanel().endCheck( "VAL XSD", "1 Error");
		} catch ( final Throwable t) {
			t.printStackTrace();
		}
 	}
}
