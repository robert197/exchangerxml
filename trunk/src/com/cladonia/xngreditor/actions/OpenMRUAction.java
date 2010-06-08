/*
 * $Id: OpenMRUAction.java,v 1.3 2004/05/18 16:57:45 edankert Exp $
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

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.URLUtilities;

/**
 * An action that can be used to open a recently used XML Document.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/18 16:57:45 $
 * @author Dogsbay
 */
 public class OpenMRUAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private URL url = null;
 	private ExchangerEditor parent = null;

 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public OpenMRUAction( ExchangerEditor parent, URL url, int index) {
 		super( index+" "+URLUtilities.getFileName( url));
		
		char[] chars = String.valueOf( index).toCharArray();

 		putValue( MNEMONIC_KEY, new Integer( chars[0]));

	 	this.parent = parent;
	 	this.url = url;
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		parent.setWait( true);
		parent.setStatus( "Opening ...");

		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {

				try {
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
