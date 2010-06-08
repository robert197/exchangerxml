/*
 * $Id: OpenStylesheetAction.java,v 1.2 2004/05/23 14:46:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/23 14:46:44 $
 * @author Dogsbay
 */
public class OpenStylesheetAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private JFileChooser chooser = null;
	private JFrame parent = null;
	private XSLTDebuggerPane debugger = null;
	private ConfigurationProperties properties = null;

	private String lastOpenedFile = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public OpenStylesheetAction( JFrame parent, XSLTDebuggerPane debugger, ConfigurationProperties props) {
 		super( "Stylesheet ...");
		
		this.parent = parent;
		this.properties = props;
		this.debugger = debugger;

//		putValue( MNEMONIC_KEY, new Integer( 'S'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Open16.gif"));
		putValue( SHORT_DESCRIPTION, "Open a XSLT Stylesheet");
 	}
 	
	/**
	 * The implementation of the add document action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
		final JFileChooser chooser = getFileChooser();
	 	int value = chooser.showOpenDialog( parent);

	 	if ( value == JFileChooser.APPROVE_OPTION) {

	 		try {
	 	        File file = chooser.getSelectedFile();
	 	        lastOpenedFile = file.getAbsolutePath();

	 	        URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				debugger.openStylesheet( url.toString());
	 		} catch ( MalformedURLException mue) {
	 			// This should never happen, just report and continue
	 			mue.printStackTrace();
	 		}
	 	}
 	}
	
 	private JFileChooser getFileChooser() {
 		if ( chooser == null) {
 			chooser = FileUtilities.createFileChooser();
			chooser.setAcceptAllFileFilterUsed( true);
			chooser.addChoosableFileFilter( FileUtilities.getXSLFilter());
 		}
 		
 		if ( lastOpenedFile == null) {
 			lastOpenedFile = properties.getLastOpenedDocument();
 		}
 		
		File file = new File( lastOpenedFile);
		chooser.setSelectedFile( file);
		chooser.setCurrentDirectory( file);
		chooser.rescanCurrentDirectory();
 		
 		return chooser;
 	}
}
