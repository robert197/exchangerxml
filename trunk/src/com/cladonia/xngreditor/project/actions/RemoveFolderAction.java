/*
 * $Id: RemoveFolderAction.java,v 1.2 2005/09/05 09:08:29 tcurley Exp $
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
import com.cladonia.xngreditor.project.FolderNode;
import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.project.VirtualFolderNode;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class RemoveFolderAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
 
 	/**
 	 * The constructor for the delete action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public RemoveFolderAction( Project project) {
 		super( "Remove Folder");
 		
 		this.project = project;

	 	putValue( MNEMONIC_KEY, new Integer( 'm'));
 		putValue( SHORT_DESCRIPTION, "Remove a Folder from the Project");
 	
 		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the delete project action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
	 	BaseNode node = project.getSelectedNode();
	  	int result = MessageHandler.showConfirm( "Are you sure you want to remove this folder \""+node.getName()+"\"?");

	  	if ( result == JOptionPane.YES_OPTION) {
	  		
	  		if(node instanceof FolderNode) {
	  			project.removeFolder();
	  		}
	  		else if(node instanceof VirtualFolderNode){
	  			project.removeVirtualFolder();
	  		}
	  	}
 	}
}
