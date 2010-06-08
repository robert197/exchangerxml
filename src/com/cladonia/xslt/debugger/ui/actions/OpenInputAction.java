/*
 * $Id: OpenInputAction.java,v 1.1 2004/03/25 18:58:13 edankert Exp $
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
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:58:13 $
 * @author Dogsbay
 */
public class OpenInputAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private JFileChooser chooser = null;
	private XSLTDebuggerPane debugger = null;
	private JFrame parent = null;
	private ConfigurationProperties properties = null;

	private String lastOpenedFile = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public OpenInputAction( JFrame parent, XSLTDebuggerPane debugger, ConfigurationProperties props) {
 		super( "Input ...");
		
		this.parent = parent;
		this.debugger = debugger;
		this.properties = props;

//		putValue( MNEMONIC_KEY, new Integer( 'I'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_O, InputEvent.CTRL_MASK, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Open16.gif"));
		putValue( SHORT_DESCRIPTION, "Open an Input Document");
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
				debugger.openInput( url.toString());
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
			chooser.addChoosableFileFilter( FileUtilities.getXMLFilter());
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
