/*
 * $Id: AddDirectoryAction.java,v 1.3 2005/09/05 09:08:29 tcurley Exp $
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

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.project.Project;
import com.l2fprod.common.swing.JDirectoryChooser;

/**
 * An action that can be used to add a directory to a project or 
 * folder.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class AddDirectoryAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public AddDirectoryAction( ExchangerEditor parent, Project project) {
 		super( "Add Directory ...");
 		
 		this.parent = parent;
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'A'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Add a Directory");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		JDirectoryChooser chooser = FileUtilities.getDirectoryChooser();
		
	 	int value = chooser.showOpenDialog( parent);
		
	 	if ( value == JDirectoryChooser.APPROVE_OPTION) {
		 	File file = chooser.getSelectedFile();
		 	boolean isStartup = false;
			project.addDirectory( file, isStartup);
	 	}
 	}
}
