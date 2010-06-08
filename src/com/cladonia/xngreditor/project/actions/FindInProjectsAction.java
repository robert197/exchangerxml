/*
 * $Id: FindInProjectsAction.java,v 1.2 2005/08/31 09:18:15 tcurley Exp $
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
import com.cladonia.xngreditor.FindInProjectsDialog;
import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to add a directory to a project or 
 * folder.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/31 09:18:15 $
 * @author Dogsbay
 */
public class FindInProjectsAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ConfigurationProperties properties = null; 
 	private ExchangerEditor parent = null;
	private FindInProjectsDialog findDialog = null;

 	private Project project = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public FindInProjectsAction( ExchangerEditor parent, ConfigurationProperties props, Project project) {
 		super( "Find in Projects ...");
 		
		this.parent = parent;
 		this.project = project;
		this.properties = props;

//		putValue( MNEMONIC_KEY, new Integer( 'V'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Find in Projects ...");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
	 	if ( findDialog == null) {
	 		findDialog = new FindInProjectsDialog( parent, properties);
	 	}
	 	
	 	findDialog.init(project);
		
	 	findDialog.setVisible( true);
	 	
	 	if ( !findDialog.isCancelled()) {
	 		String search = findDialog.getSearch();
	 		boolean matchCase = findDialog.isCaseSensitive();
	 		boolean regExp = findDialog.isRegularExpression();
	 		boolean wholeWord = findDialog.isMatchWholeWord();
	 		String workingProject = findDialog.getProject();

	 		if ( search != null) {
	 			properties.addSearch( search);
	 			properties.setMatchCase( matchCase);
	 			properties.setRegularExpression( regExp);
	 			properties.setMatchWholeWord( wholeWord);
				
	 			project.selectProject(workingProject);
	 			project.findInFiles( search, regExp, matchCase, wholeWord);

	 		}
	 	}
 	}
}
