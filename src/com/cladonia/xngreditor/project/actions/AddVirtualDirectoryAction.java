/*
 * $Id: AddVirtualDirectoryAction.java,v 1.2 2005/09/05 09:14:18 tcurley Exp $
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

import com.cladonia.xngreditor.DirectoryChooserDialog;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.OpenRemoteDocumentDialog;
import com.cladonia.xngreditor.project.Project;
import com.l2fprod.common.swing.JDirectoryChooser;

/**
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:14:18 $
 * @author Dogsbay
 */
 public class AddVirtualDirectoryAction extends AbstractAction {
 	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;

 	private Project project = null;

	private ExchangerEditor parent;
	
	private DirectoryChooserDialog dialog = null;
	
 	/**
	 * The constructor for the add action.
 	 * @param parent 
	 *
	 * @param editor the editor to copy information from.
	 */
 	public AddVirtualDirectoryAction( ExchangerEditor parent, Project project) {
 		super( "Add Virtual Folder ...");
 		
 		this.project = project;
 		this.parent = parent;

		//putValue( MNEMONIC_KEY, new Integer( 'o'));
		putValue( SHORT_DESCRIPTION, "Add a Virtual Folder");
		
		setEnabled( false);
		
		dialog = new DirectoryChooserDialog( parent);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		dialog.setVisible(true);

	 	if ( !dialog.isCancelled()) {
	 		
	 		File folder = dialog.getSelectedFolder();
	 		if((folder != null) && (folder.exists() == true)) {
	 			
		 		boolean isStartup = false;
		 		project.addVirtualDirectory( folder, isStartup);
		 		
		 	}
	 	}
 		/*JDirectoryChooser chooser = FileUtilities.getDirectoryChooser();
		
	 	int value = chooser.showOpenDialog( parent);
		
	 	if ( value == JDirectoryChooser.APPROVE_OPTION) {
		 	final File file = chooser.getSelectedFile();
			
		 	if(file != null) {
		 		boolean isStartup = false;
		 		project.addVirtualDirectory( file, isStartup);
		 		
		 	}
			
	 	}*/
 	}
 }
