/*
 * $Id: OpenFileAction.java,v 1.10 2004/11/03 15:25:11 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.project.DocumentNode;
import com.cladonia.xngreditor.project.DocumentProperties;
import com.cladonia.xngreditor.project.Project;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.10 $, $Date: 2004/11/03 15:25:11 $
 * @author Dogsbay
 */
 public class OpenFileAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private Project project = null;
 	private ExchangerEditor parent = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public OpenFileAction( ExchangerEditor parent, Project project) {
 		super( "Open File");
 		
 		this.parent = parent;
 		this.project = project;

		putValue( MNEMONIC_KEY, new Integer( 'O'));
		putValue( SHORT_DESCRIPTION, "Open a File");
		
		setEnabled( false);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		execute();
 	}
	
	public void execute() {
//		System.out.println("OpenFileAction.execute()");
		parent.setWait( true);
		parent.setStatus( "Opening ...");
		
		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {

				try {
					DocumentNode node = (DocumentNode)project.getSelectedNode();
					DocumentProperties properties = node.getProperties();
					
					parent.open( properties.getURL(), null, true);
			 	} finally {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							DocumentNode node = (DocumentNode)project.getSelectedNode();
							node.setDocument( parent.getDocument());
						 	node.setType( parent.getGrammar());

						 	ExchangerView view = parent.getView();
		
							if ( view != null) {
								view.getCurrentView().setFocus();
							}
						}
					});

					parent.setStatus( "Done");
			 		parent.setWait( false);
			 	}
			}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
	}
}
