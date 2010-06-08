/*
 * $Id: ImportProjectAction.java,v 1.3 2005/09/05 09:08:29 tcurley Exp $
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
 * An action that can be used to copy information 
 * in a XML Document.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class ImportProjectAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
 	private ExchangerEditor parent = null;
 	private File dir = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public ImportProjectAction( ExchangerEditor parent, Project project) {
 		super( "Import Project ...");
 		
 		this.project = project;
 		this.parent = parent;

		putValue( MNEMONIC_KEY, new Integer( 'm'));
		putValue( SHORT_DESCRIPTION, "Import a Project from file");
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		JFileChooser chooser = FileUtilities.getFileChooser();
		
		if ( dir != null) {
			chooser.setCurrentDirectory( dir);
			chooser.rescanCurrentDirectory();
		}

		int value = chooser.showOpenDialog( parent);
		dir = chooser.getCurrentDirectory();
		
	 	if ( value == JFileChooser.APPROVE_OPTION) {
		 	File file = chooser.getSelectedFile();
		 	boolean isStartup = false;
			project.importProject( file, isStartup);
	 	}
 	}
}
