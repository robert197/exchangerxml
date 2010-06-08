/*
 * $Id: OpenAction.java,v 1.7 2005/08/29 08:32:39 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.7 $, $Date: 2005/08/29 08:32:39 $
 * @author Dogsbay
 */
public class OpenAction extends AbstractAction {
 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public OpenAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Open ...");
		
		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'O'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Open16.gif"));
		putValue( SHORT_DESCRIPTION, "Open Document");
 	}
 	
	/**
	 * The implementation of the add document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		//boolean alreadyLoaded = false;
		//XMLSchema schema = null;
		//ExchangerDocument document = null;

		//try {
		//	LicenseManager licenseManager = LicenseManager.getInstance();
		//	licenseManager.isValid( com.cladonia.license.KeyGenerator.generate(2), "Exchanger XML Editor");
		//} catch (Exception x) {
		//	System.exit(0);
		//	return;
		//}

		final JFileChooser chooser = FileUtilities.getFileChooser();
	 	int value = chooser.showOpenDialog( parent);

	 	if ( value == JFileChooser.APPROVE_OPTION) {
	 		parent.setWait( true);
	 		parent.setStatus( "Opening ...");

	 		// Run in Thread!!!
	 		Runnable runner = new Runnable() {
	 			public void run()  {
			 		try {
			 	        File file = chooser.getSelectedFile();

			 	        URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			 	        
			 	        parent.open( url, FileUtilities.getSelectedGrammar( chooser), true);
						
			 			// set the document somewhere....
			 		} catch ( MalformedURLException mue) {
			 			// This should never happen, just report and continue
			 			MessageHandler.showUnexpectedError( mue);
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
	 	else
	 	{
	 		ExchangerView view = parent.getView();
	 		if (view != null)
	 		{
	 			view.requestFocus();
	 		}
	 		else
	 		{
	 			// no view available, so set focus back
	 			parent.setIntialFocus();
	 		}
	 	}
 	}
	
}
