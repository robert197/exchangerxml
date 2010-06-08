/*
 * $Id: SetSchemaPropertiesAction.java,v 1.3 2004/11/01 11:14:06 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.SchemaPropertiesDialog;
import com.cladonia.xngreditor.grammar.TagCompletionProperties;

/**
 * An action that can be used to set the schema properties.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/11/01 11:14:06 $
 * @author Dogs bay
 */
 public class SetSchemaPropertiesAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	private ExchangerEditor parent = null;
	private SchemaPropertiesDialog dialog = null;
	
 	/**
	 * The constructor for the action which allows for validating
	 * the XML content.
	 *
	 * @param editor the XML Editor
	 */
 	public SetSchemaPropertiesAction( ExchangerEditor parent) {
		super( "Set Properties ...");

		putValue( MNEMONIC_KEY, new Integer( 'P'));
		putValue( SHORT_DESCRIPTION, "Set Document Properties ...");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the validate action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		Vector tagCompletionList = null;
 		
		if ( dialog == null) {
			dialog = new SchemaPropertiesDialog( parent);
		}
		
		// show the dialog with any exisitng validation grammar, tag completion schema and viewer/outliner schema
		dialog.show( parent.getView().getValidationGrammar(), parent.getView().getTagCompletionSchemas(), parent.getView().getSchema());
		
		if ( !dialog.isCancelled()) {
			
			// update the validation grammar
			int validationType = -1;
			String validationSchema = null;
			URL validationURL = dialog.getValidationURL();
		
			if (!dialog.useLocationInDocument())
			{
				if (validationURL != null)
				{
					// set the validation properties
					validationSchema = validationURL.toString();
					validationType = dialog.getValidationType();
				}
			}
			
			// update the tag completion schema
			if (dialog.useValidationForTagCompletion()) {
				tagCompletionList = new Vector();
				tagCompletionList.addElement( new TagCompletionProperties( validationURL.toString(), validationType));
			} else {
				tagCompletionList = dialog.getTagCompletionList();
			}
			
			// update the viewer/outliner schema
			URL viewerOutlinerSchema = null;
			
			if (dialog.useValidationForViewerSchema())
			{
				viewerOutlinerSchema = validationURL;
			}
			else
			{
				viewerOutlinerSchema = dialog.getViewerURL();
			}
			
			// can only use final variables in running thread
			final String validationFinal = validationSchema;
			final int validationFinalType = validationType;
			final URL viewerOutlinerFinal = viewerOutlinerSchema;
			final Vector tagCompletionListFinal = tagCompletionList;
			
			parent.setWait( true);
			parent.setStatus( "Setting Document Properties...");

			// Run in Thread!!!
			Runnable runner = new Runnable() {
				public void run()  {
					Vector tagCompletionSchemas = null;
					
					try {
						
						if (validationFinal != null)
						{
							// set the validation schema
							parent.getView().getValidationGrammar().setLocation(validationFinal);
							parent.getView().getValidationGrammar().setType(validationFinalType);
							parent.getView().getValidationGrammar().setExternal(true);
						}
						else
						{
							// using "location defined in document" for validation
							parent.getView().getValidationGrammar().setExternal(false);
							parent.getView().getValidationGrammar().setLocation(null);
						}
						
						parent.openSchema( viewerOutlinerFinal, false, false);
						tagCompletionSchemas = parent.openTagCompletionSchemas( tagCompletionListFinal);

						final Vector ts = tagCompletionSchemas;
						SwingUtilities.invokeLater( new Runnable() {
							public void run() {
								parent.setTagCompletionSchemas( ts);
							}
						});
					} finally {
						parent.updateStatus();
						parent.setStatus("Done");
						parent.setWait( false);
					}
				}
			};

			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
		}
 	}
}
