/*
 * $Id: SetSchemaLocationAction.java,v 1.3 2004/10/11 08:38:30 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.SchemaLocationDialog;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xml.XMLUtilities;


/**
 * An action that can be used to set the Schema Location
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/11 08:38:30 $
 * @author Dogs bay
 */
public class SetSchemaLocationAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
 	private ExchangerEditor parent = null;
 	private SchemaLocationDialog dialog = null;
  	
 	private static final String SCHEMALOCATION = "schemaLocation";
	private static final String NOSCHEMALOCATION = "noNamespaceSchemaLocation";

 	/**
	 * The constructor for the action which sets the Schema Location
	 *
	 * @param parent the parent frame.
	 */
 	public SetSchemaLocationAction( ExchangerEditor parent) 
	{
 		super( "Set Schema Location...");

		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'L'));
		putValue( SHORT_DESCRIPTION, "Set Schema Location");
		
		// initally don't have this option available
		setEnabled( false);
 	}
 	
 	/**
 	 * The implementation of the Set Schema Location action
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
			dialog = new SchemaLocationDialog(parent);
		}
				
		if (document != null)
		{
			dialog.show(document);
		}
		
		if (!dialog.isCancelled()) 
		{
			try
			{
				
				// get the element name
				String schemaType;
				if (dialog.isNamespaceRequired())
					schemaType = SCHEMALOCATION;
				else
					schemaType = NOSCHEMALOCATION;
					
				
				// get the keyword (SYSTEM or PUBLIC)
				String namespace = dialog.getNamespace();
				
				// get the public identifier
				String schemaURL = dialog.getSchemaURL();
				
				// Need to crete a temp exchangerdocument as if we update the existing one
				// before we set text on the editor, the undo functionality is not available
				// This event model should be looked at again in future version.
				ExchangerDocument tempDoc =  new ExchangerDocument(document.getText());
				
				// update the temp dom with the new schema location
				XMLUtilities.setSchemaLocation(tempDoc,schemaType,namespace,schemaURL);
				tempDoc.update();
				
				// get the text of this updated dom
				final String updatedText = tempDoc.getText();
				
				SwingUtilities.invokeLater( new Runnable(){
					public void run() {
						try{
						parent.switchToEditor();
						view.getEditor().setText(updatedText);
						view.updateModel();
						}
						catch ( Exception e) 
						{
							e.printStackTrace();
						}
					}
				});
			
			} 
			catch ( final Exception err) {
				MessageHandler.showError( err, "Set Schema Location Error");
				err.printStackTrace();
			} 
			catch ( final Throwable t) {
				t.printStackTrace();
			}
		}
		
 	}
} 	