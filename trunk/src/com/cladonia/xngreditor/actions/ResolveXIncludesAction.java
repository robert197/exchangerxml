/*
 * $Id: ResolveXIncludesAction.java,v 1.5 2004/10/11 08:38:29 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerOutputFormat;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ChangeManager;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.FileUtilities;


/**
 * An action that can be used to reolve any XIncludes.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/11 08:38:29 $
 * @author Dogs bay
 */
 public class ResolveXIncludesAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	private Editor editor = null;
	private ConfigurationProperties props = null;
	
 	/**
	 * The constructor for the action which allows for resolving XIncludes.
	 *
	 * @param editor the XML Editor
	 */
 	public ResolveXIncludesAction( ExchangerEditor parent,ConfigurationProperties props) {
		super( "Resolve XIncludes");

		putValue( MNEMONIC_KEY, new Integer( 'I'));
		putValue( SHORT_DESCRIPTION, "Resolve XIncludes");
		
		this.parent = parent;
		this.props = props;
		
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
		parent.setStatus( "Resolving XIncludes ...");

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
			if ( document.isError()) {
				throw new Exception("Please make sure the document is well-formed");
			}

			ExchangerOutputFormat format = new ExchangerOutputFormat( "", false, document.getEncoding());
			
			if ( document.hasDeclaration()) {
				if ( document.getStandalone() != ExchangerDocument.STANDALONE_NONE) {
					format.setStandalone( document.getStandalone());
					format.setOmitStandalone( false);
				}
				
				format.setVersion( document.getVersion());

				format.setOmitEncoding( !document.hasEncoding());
				
				format.setSuppressDeclaration( false);
			} else {
				format.setSuppressDeclaration( true);
			}

			final String updatedText = XMLUtilities.resolveXIncludes( document.getText(), 
					document.getEncoding(),document.getURL(), format);
			
			SwingUtilities.invokeLater( new Runnable(){
				public void run() {
				if (!props.isOpenXIncludeInNewDocument()) 
				{
					parent.switchToEditor();
					parent.getView().getEditor().setText(updatedText);
					parent.getView().updateModel();
				}	
				else 
				{
					try{
					
					ExchangerDocument newDocument = FileUtilities.createDocument();
					
					newDocument.setText(updatedText);
					
					parent.open( newDocument, null);
					
					}
					catch(Exception e)
					{
						// ignore
					}
				}
				
				}
			});
		} catch ( final Exception e) {
			MessageHandler.showError( e, "Resolving XIncludes Error");
			e.printStackTrace();
		} catch ( final Throwable t) {
			t.printStackTrace();
		}
 	}
}