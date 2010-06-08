/*
 * $Id: DeleteAction.java,v 1.1 2004/03/25 18:55:19 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.project.BaseNode;
import com.cladonia.xngreditor.project.DocumentNode;
import com.cladonia.xngreditor.project.FolderNode;
import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.project.ProjectNode;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:55:19 $
 * @author Dogsbay
 */
 public class DeleteAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
	private DeleteProjectAction deleteProject = null;
	private RemoveFolderAction deleteFolder = null;
	private RemoveFileAction deleteFile = null;
	
 	/**
	 * The constructor for the delete action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public DeleteAction( Project project, DeleteProjectAction deleteProject, RemoveFolderAction deleteFolder, RemoveFileAction deleteFile) {
 		super( "Delete");
 		
 		this.project = project;

 		this.deleteProject = deleteProject;
 		this.deleteFolder = deleteFolder;
 		this.deleteFile = deleteFile;
 	}
 	
	/**
	 * The implementation of the delete project action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		BaseNode node = project.getSelectedNode();
		
		if ( node instanceof DocumentNode) {
			deleteFile.actionPerformed( e);
		} else if ( node instanceof ProjectNode) {
			deleteProject.actionPerformed( e);
		} else if ( node instanceof FolderNode) {
			deleteFolder.actionPerformed( e);
		}
 	}
 }
