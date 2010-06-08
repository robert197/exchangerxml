/*
 * $Id: CleanUpHTMLAction.java,v 1.4 2004/10/11 08:38:29 edankert Exp $
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
import javax.swing.SwingUtilities;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.HTMLUtilities;
import com.cladonia.xngreditor.ChangeManager;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;

/**
 * An action that can be used to make HTML well-formed.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/10/11 08:38:29 $
 * @author Dogs bay
 */
 public class CleanUpHTMLAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	//private Editor editor = null;
	
 	/**
	 * The constructor for the action which allows for cleaning up
	 * the HTML content.
	 *
	 * @param editor the XML Editor
	 */
 	public CleanUpHTMLAction( ExchangerEditor parent) {
		super( "Clean Up HTML");

		putValue( MNEMONIC_KEY, new Integer( 'u'));
		putValue( SHORT_DESCRIPTION, "Cleans Up HTML");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	/*public void setView( Object view) {
		if ( view instanceof Editor) {
			editor = (Editor)view;
		} else {
			editor = null;
		}

		setDocument( parent.getDocument());
	}*/
	
	/*public void setDocument( ExchangerDocument doc) {
		 if ( doc != null && doc.isXML()) {
		 	setEnabled( editor != null);
		 } else {
		 	setEnabled( false);
		 }
	}*/

	/**
	 * The implementation of the validate action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
		parent.setWait( true);
		parent.setStatus( "Cleaning Up HTML ...");

		Runnable runner = new Runnable() {
			public void run()  {
				try {
					execute();
				} finally {
					parent.setWait( false);
					parent.setStatus( "Done");
					parent.getView().getEditor().setFocus();
				}
			}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
 	}
	
	public void execute() {

		try {
//			Editor editor = parent.getView().getEditor();
			ChangeManager changeManager = parent.getView().getChangeManager();
			final ExchangerView view = parent.getView();

			view.updateModel();
			
			ExchangerDocument document = parent.getDocument();
			
			
			
			
			String cleantext = document.getText();
			// strip out XML declaration if present
			int xmlDeclStart = cleantext.indexOf("<?xml");
			if (xmlDeclStart != -1)
			{
				int xmlDeclEnd = cleantext.indexOf("?>",xmlDeclStart);
				cleantext = cleantext.substring(xmlDeclEnd+2,cleantext.length());
			}
			
			final String text = (HTMLUtilities.cleanUpHTML( cleantext)).trim();
			
			URL url = document.getURL();
			String systemId = null;
			
			if ( url != null) {
				systemId = url.toString();
			}

			final String formattedText = parent.getFormatAction().format( text, document.getEncoding(), systemId);
			
			SwingUtilities.invokeLater( new Runnable(){
				public void run() {
					try{
					parent.switchToEditor();
					view.getEditor().setText( formattedText);
					view.updateModel();
					}
					catch ( Exception e) 
					{
						e.printStackTrace();
					}
					
					//FormatAction action = new FormatAction(parent);
					//action.actionPerformed(null);
					
				}
			});
		} catch ( final Exception e) {
			MessageHandler.showError( e, "Clean Up HTML Error");
			e.printStackTrace();
		} catch ( final Throwable t) {
			t.printStackTrace();
		}
 	}
}