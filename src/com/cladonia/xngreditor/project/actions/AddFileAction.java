/*
 * $Id: AddFileAction.java,v 1.2 2005/09/05 09:08:29 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.project.Project;

/**
 * An action that can be used to add a file to a project or 
 * folder.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class AddFileAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param parent the parent component.
	 * @param project the project component.
	 */
 	public AddFileAction( ExchangerEditor parent, Project project) {
 		super( "Add File ...");
 		
 		this.parent = parent;
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'F'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Add a File");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		JFileChooser chooser = FileUtilities.getFileChooser();
		
	 	int value = chooser.showOpenDialog( parent);
		
	 	if ( value == JFileChooser.APPROVE_OPTION) {
		 	File file = chooser.getSelectedFile();
		 	boolean isStartup = false;
			project.addFile( file, isStartup);
	 	}
 	}
}
