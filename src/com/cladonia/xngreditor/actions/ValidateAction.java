/*
 * $Id: ValidateAction.java,v 1.10 2005/08/19 13:22:05 tcurley Exp $
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
import javax.swing.ImageIcon;

import org.xml.sax.SAXParseException;

import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLError;
import com.cladonia.xml.XMLErrorHandler;
import com.cladonia.xml.XMLErrorReporter;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.ValidationDialog;
import com.cladonia.xngreditor.XMLGrammarImpl;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to parse the XML content.
 *
 * @version	$Revision: 1.10 $, $Date: 2005/08/19 13:22:05 $
 * @author Dogsbay
 */
 public class ValidateAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	private ValidationDialog dialog = null;
	private Object view = null;
	
	private ImageIcon validatedIcon = null;
	private ImageIcon unvalidatedIcon = null;
	
 	/**
	 * The constructor for the action which allows for validating
	 * the XML content.
	 *
	 * @param editor the XML Editor
	 */
 	public ValidateAction( ExchangerEditor parent) {
		super( "Validate");
		
		validatedIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Validate16.gif");
		unvalidatedIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Unvalidate16.gif");

		putValue( MNEMONIC_KEY, new Integer( 'V'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F7, 0, false));
		putValue( SMALL_ICON, unvalidatedIcon);
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Parse16.gif"));
		putValue( SHORT_DESCRIPTION, "Validate");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( !(view instanceof SchemaViewer)) {
			this.view = view;
		} else {
			this.view = null;
		}
		
		setDocument( parent.getDocument());
	}

	public void setDocument( ExchangerDocument doc) {
		if ( doc != null && doc.isXML()) {
			setEnabled( view != null);
		} else {
			setEnabled( false);
		}
	}
	
	public void setValidated( boolean valid) {
		if ( valid) {
			putValue( SMALL_ICON, validatedIcon);
		} else {
			putValue( SMALL_ICON, unvalidatedIcon);
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

		Runnable runner = new Runnable() {
			public void run()  {
				try {
					execute();
				} finally {
					parent.setWait( false);
					parent.setStatus( "Done");
					parent.getView().getCurrentView().setFocus();
				}
			}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
 	}
	
	public void execute() {
		
		//Vector expandedRows = null;
		try {
			ExchangerDocument document = parent.getDocument();

			parent.getView().updateModel();
			
//			if ( document.isError()) {
//				return;
//			}
			
			if(view instanceof PluginViewPanel) {
				((PluginViewPanel)view).saveState();
				
			}

			String schemaLocation = "Internal DocType Declaration";
			
			boolean invalid = true;
			while ( invalid) {
				try {
					URL url = document.checkSchemaLocation();

					if ( url != null) {
						schemaLocation = url.toString();
					}
					
					invalid = false;
				} catch ( IOException ioe) {
					// show the message
					if ( dialog == null) {																			
						dialog = new ValidationDialog( parent, "Please provide a valid schema location.");
					}

					dialog.show( parent.getView().getValidationGrammar(), ioe.getMessage());
					
					if ( dialog.isCancelled()) {
						return;
					} 
					
					parent.updateStatus();
				}
			}
			XMLGrammarImpl grammar = parent.getView().getValidationGrammar();
			
			if ( grammar.getType() == XMLGrammarImpl.TYPE_DTD || grammar.getType() == XMLGrammarImpl.TYPE_XSD) {
				parent.getOutputPanel().startCheck( "VAL", "["+FileUtilities.getXercesVersion()+"] Validating \""+document.getName()+"\" against \""+schemaLocation+"\" ...");
			} else {
				parent.getOutputPanel().startCheck( "VAL", "["+FileUtilities.getRelaxNGVersion()+"] Validating \""+document.getName()+"\" against \""+schemaLocation+"\" ...");
			}
			XMLErrorHandler handler = new XMLErrorHandler( new XMLErrorReporter() {
				public void report( XMLError error) {
					parent.getOutputPanel().addError( "VAL", error);
				}
			}, 100);
			document.validate( handler);
			
			if ( !handler.hasErrors()) {
				parent.getView().getChangeManager().markValid();
				parent.getOutputPanel().endCheck( "VAL", "Valid Document.");
			} else {
				int errors = handler.getErrors().size();
				if ( errors > 0) {
					parent.getOutputPanel().endCheck( "VAL", errors+" Errors");
				} else {
					parent.getOutputPanel().endCheck( "VAL", errors+" Error");
				}
			}
		} catch ( final SAXParseException e) {
			parent.getOutputPanel().endCheck( "VAL", "1 Error");
			e.printStackTrace();
//			parent.getOutputPanel().setError( (SAXParseException)e);
//			e.printStackTrace();
		} catch ( final IOException e) {
			parent.getOutputPanel().setError( "VAL", (IOException)e);
			parent.getOutputPanel().endCheck( "VAL", "1 Error");
			e.printStackTrace();
		} catch ( final Throwable t) {
			t.printStackTrace();
		}
		
		if(view instanceof PluginViewPanel) {
			((PluginViewPanel)view).returnToPreviousState();
			
		}
 	}
}
