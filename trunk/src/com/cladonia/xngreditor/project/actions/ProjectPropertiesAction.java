/*
 * $Id: ProjectPropertiesAction.java,v 1.1 2004/03/25 18:55:19 edankert Exp $
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
 * An action that can be used to add a directory to a project or 
 * folder.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:55:19 $
 * @author Dogsbay
 */
public class ProjectPropertiesAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the rename action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public ProjectPropertiesAction( ExchangerEditor parent, Project project) {
 		super( "Project Properties");
 		
 		this.parent = parent;
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'j'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Project Properties");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 	}
}
