/*
 * $Id: RefreshVirtualDirectoryAction.java,v 1.2 2005/09/05 09:14:18 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.project.Project;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:14:18 $
 * @author Dogsbay
 */
 public class RefreshVirtualDirectoryAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;

	private ExchangerEditor parent;
	
 	/**
	 * The constructor for the add action.
 	 * @param parent 
	 *
	 * @param editor the editor to copy information from.
	 */
 	public RefreshVirtualDirectoryAction( ExchangerEditor parent, Project project) {
 		super( "Refresh Virtual Folder ...");
 		
 		this.project = project;
 		this.parent = parent;

		//putValue( MNEMONIC_KEY, new Integer( 'o'));
		putValue( SHORT_DESCRIPTION, "Refresh a Virtual Folder");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 			
		project.refreshVirtualDirectory();
	 	
 	}
 }
