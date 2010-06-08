/*
 * $Id: OpenCurrentURLAction.java,v 1.2 2005/08/29 08:32:59 gmcgoldrick Exp $
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
import javax.swing.ImageIcon;

import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/29 08:32:59 $
 * @author Dogsbay
 */
public class OpenCurrentURLAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public OpenCurrentURLAction( ExchangerEditor parent) {
 		super( "Open ...");
		
		this.parent = parent;

//		putValue( MNEMONIC_KEY, new Integer( 'O'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
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
		boolean alreadyLoaded = false;
		XMLSchema schema = null;
		ExchangerDocument document = null;

		//try {
		//	LicenseManager licenseManager = LicenseManager.getInstance();
		//	licenseManager.isValid( com.cladonia.license.KeyGenerator.generate(2), "Exchanger XML Editor");
		//} catch (Exception x) {
		//	System.exit(0);
		//	return;
		//}

 		parent.setWait( true);
 		parent.setStatus( "Opening ...");

 		// Run in Thread!!!
 		Runnable runner = new Runnable() {
 			public void run()  {
		 		try {
		 	        URL url = parent.getView().getEditor().getCurrentURL();
					parent.open( url, null, true);
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
	
}
