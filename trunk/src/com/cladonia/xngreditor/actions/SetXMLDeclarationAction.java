/*
 * $Id: SetXMLDeclarationAction.java,v 1.2 2004/10/11 08:38:30 edankert Exp $
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
import com.cladonia.xngreditor.XMLDeclarationDialog;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xml.XMLUtilities;


/**
 * An action that can be used to set the XML Declaration
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/11 08:38:30 $
 * @author Dogs bay
 */
public class SetXMLDeclarationAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
 	private ExchangerEditor parent = null;
 	private XMLDeclarationDialog dialog = null;
 	
 	

 	/**
	 * The constructor for the action which sets the XML Declaration
	 *
	 * @param parent the parent frame.
	 */
 	public SetXMLDeclarationAction( ExchangerEditor parent) 
	{
 		super( "Set XML Declaration ...");

		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'S'));
		putValue( SHORT_DESCRIPTION, "Set XML Declaration");
		
		// initally don't have this option available
		setEnabled( false);
 	}
 	
 	/**
 	 * The implementation of the Set XML Declaration action
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

		// create a new dialog each time, as the dailog must represent the current document
		if (dialog == null)
		{
			dialog = new XMLDeclarationDialog(parent);
		}
				
		if (document != null)
		{
			dialog.show(document);
		}
		
		if (!dialog.isCancelled()) 
		{
			try
			{
				
				// get the version
				String version;
				if (dialog.isVersion10())
				{
					version = "1.0";
				}
				else
				{
					version = "1.1";
				}
				
				// get the encoding
				String encoding = dialog.getEncoding();
				
				// get the standalone
				String standalone = dialog.getStandalone();
				
				// get the updated text with the new declaration
				final String updatedText = XMLUtilities.updateXMLDeclaration(document,version,encoding,standalone);
				
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
				MessageHandler.showError( err, "Set XML Declaration Error");
				err.printStackTrace();
			} 
			catch ( final Throwable t) {
				t.printStackTrace();
			}
		}
		
 	}
} 	