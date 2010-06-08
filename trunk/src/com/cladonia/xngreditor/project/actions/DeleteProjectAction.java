/*
 * $Id: DeleteProjectAction.java,v 1.1 2004/03/25 18:55:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.project.BaseNode;
import com.cladonia.xngreditor.project.Project;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:55:19 $
 * @author Dogsbay
 */
 public class DeleteProjectAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
	
 	/**
	 * The constructor for the delete action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public DeleteProjectAction( Project project) {
 		super( "Delete Project");
 		
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'D'));
		putValue( SHORT_DESCRIPTION, "Delete a Project");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the delete project action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		BaseNode node = project.getSelectedNode();
	 	int result = MessageHandler.showConfirm( "Are you sure you want to delete this project \""+node.getName()+"\"?");

	 	if ( result == JOptionPane.YES_OPTION) {
 			project.deleteProject();
	 	}
 	}
 }
