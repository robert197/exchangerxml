/*
 * $Id: CreateSchemaAction.java,v 1.3 2004/10/19 17:09:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.bounce.DefaultFileFilter;
import org.xml.sax.SAXException;

import com.cladonia.schema.converter.Converter;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;

/**
 * Converts the current DTD Grammar to a schema.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/19 17:09:54 $
 * @author Dogsbay
 */
public class CreateSchemaAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private JFileChooser schemaChooser = null;
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public CreateSchemaAction( ExchangerEditor parent) {
 		super( "Infer XML Schema");

		putValue( MNEMONIC_KEY, new Integer( 'I'));
		putValue( SHORT_DESCRIPTION, "Creates an XML Schema from the current XML Document");
		
		this.parent = parent;
		
		setEnabled( false);
 	}
 	
 	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		ExchangerDocument document = parent.getDocument();

 		parent.getView().updateModel();
 		
		if ( document.isError()) {
			MessageHandler.showError( "Please make sure the document is well-formed.", "Parser Error");
			return;
		}

		URL url = document.getURL();
		
		String path = null;

		if ( url != null && url.getProtocol().equals( "file")) {
			path = url.getFile();
		}
		
		File selectedFile = null;
		
		if ( path != null) {
			int extIndex = path.lastIndexOf( '.');
			
			if ( extIndex != -1) {
				path = path.substring( 0, extIndex);
			}
			
			selectedFile = new File( path+".xsd");
		} else {	
			selectedFile = FileUtilities.getLastOpenedFile();
		}
		
		JFileChooser chooser = getSchemaFileChooser( selectedFile);
		
		File file = FileUtilities.selectOutputFile( chooser, "xsd");
		
		if ( file != null) {
			writeSchema( file, document);
		}
 	}
	
 	private void writeSchema( final File file, final ExchangerDocument document) {
		parent.setWait( true);
		parent.setStatus( "Creating schema ...");

		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {
				try {
					Converter.inferSchema( document.getInputSource(), file);
//					parent.openSchema( file.toURL(), true, true);
					
				} catch (MalformedURLException e) {
					e.printStackTrace(); // This should not happen!
				} catch ( SAXException e) {
					e.printStackTrace(); // This should not happen!
				} catch (IOException e) {
					e.printStackTrace();
					MessageHandler.showError( "Could not create "+file.getName(), e, "Create Schema Error");
			 	} finally {
			 		parent.setStatus( "Done");
			 		parent.setWait( false);
			 	}
			}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
 	}

 	private JFileChooser getSchemaFileChooser( File file) {
 		if ( schemaChooser == null) {
 			schemaChooser = FileUtilities.createFileChooser();
 			schemaChooser.addChoosableFileFilter( new DefaultFileFilter( "xsd", "XML Schema Document"));
 		} 

 		schemaChooser.rescanCurrentDirectory();
 		schemaChooser.setSelectedFile( file);
 		
 		return schemaChooser;
 	}
	
}
