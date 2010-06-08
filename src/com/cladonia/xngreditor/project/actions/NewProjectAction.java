/*
 * $Id: NewProjectAction.java,v 1.2 2005/09/05 09:08:29 tcurley Exp $
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
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class NewProjectAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public NewProjectAction( Project project) {
 		super( "New Project");
 		
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'N'));
		putValue( SHORT_DESCRIPTION, "Add a Project");
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		boolean isStartup = false;
		project.addProject(isStartup);
 	}
}
