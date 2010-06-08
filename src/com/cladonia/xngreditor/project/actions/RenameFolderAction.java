/*
 * $Id: RenameFolderAction.java,v 1.1 2004/03/25 18:55:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.project.Project;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:55:19 $
 * @author Dogsbay
 */
 public class RenameFolderAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public RenameFolderAction( Project project) {
 		super( "Rename Folder");
 		
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'e'));
		putValue( SHORT_DESCRIPTION, "Rename a Folder");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		project.renameFolder();
 	}
 }
