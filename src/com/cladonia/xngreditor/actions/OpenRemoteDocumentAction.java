/*
 * $Id: OpenRemoteDocumentAction.java,v 1.5 2004/05/21 17:30:49 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.OpenRemoteDocumentDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to open a remote document.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/05/21 17:30:49 $
 * @author Dogsbay
 */
 public class OpenRemoteDocumentAction extends AbstractAction {
 	private OpenRemoteDocumentDialog dialog = null;
 	private ExchangerEditor parent = null;
 	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows opening
	 * of remote documents.
	 *
	 * @param parent the parent app.
	 */
 	public OpenRemoteDocumentAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Open Remote ...");

//		putValue( MNEMONIC_KEY, new Integer( 't'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/OpenRemote16.gif"));
		putValue( SHORT_DESCRIPTION, "Open Remote Document");

		this.properties = props;
		this.parent = parent;
		dialog = new OpenRemoteDocumentDialog( props, parent);
		
//		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the add document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {

		dialog.show( "http://");
		
		if ( !dialog.isCancelled()) {
		
//			try {
		        final URL url = dialog.getURL();

//				if ( !url.getProtocol().equalsIgnoreCase( "http")) {
//					JOptionPane.showMessageDialog(	parent,
//												    "The "+url.getProtocol()+" protocol is not supported for a Remote Document.\n",
//												    "Document Error",
//												    JOptionPane.ERROR_MESSAGE);
//				} else {
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
//				}
//			} catch ( MalformedURLException mue) {
//				JOptionPane.showMessageDialog(	parent,
//											    "Invalid URL: "+dialog.getURL()+"\n"+
//												mue.getMessage(),
//											    "Document Error",
//											    JOptionPane.ERROR_MESSAGE);
//			} 
 		}
 	}
 }
