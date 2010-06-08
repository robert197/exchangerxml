/*
 * $Id: AddRemoteDocumentAction.java,v 1.4 2005/09/05 09:08:29 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.OpenRemoteDocumentDialog;
import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to add a new remote document to the
 * application.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class AddRemoteDocumentAction extends AbstractAction {
// 	private DocumentCategory category = null;
 	private Project project = null;
 	private ExchangerEditor parent = null;

 	private OpenRemoteDocumentDialog dialog = null;
 	private ConfigurationProperties properties = null;
	
 	/**
	 * The constructor for the action which allows addition of 
	 * remote documents to the application.
	 *
	 * @param frame the parent frame.
	 */
 	public AddRemoteDocumentAction( ConfigurationProperties props, ExchangerEditor parent, Project project) {
 		super( "Add Remote ...");

 		this.parent = parent;
 		this.project = project;
 		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'R'));
		putValue( SHORT_DESCRIPTION, "Add Remote Document");

		dialog = new OpenRemoteDocumentDialog( props, parent);
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the ad remote document action.
	 *
	 * @param e the action event.
	 */
	public void actionPerformed( ActionEvent e) {
		dialog.show( "http://");

	 	if ( !dialog.isCancelled()) {
	 	
 	        URL url = dialog.getURL();

 			if ( url == null) {
 				JOptionPane.showMessageDialog(	parent,
 											    "Could not create a valid URL.",
 											    "Document Error",
 											    JOptionPane.ERROR_MESSAGE);
 			} else {
 				boolean isStartup = false;
	 			project.addRemote( url, isStartup);
 			}
	 	}
	}
}
