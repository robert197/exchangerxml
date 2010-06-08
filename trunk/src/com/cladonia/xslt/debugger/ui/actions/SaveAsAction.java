/*
 * $Id: SaveAsAction.java,v 1.1 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.xml.sax.SAXParseException;

import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.AutomaticProgressMonitor;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.OutputPane;
import com.cladonia.xslt.debugger.ui.OutputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to saveAs
 * Xml Editor.
 *
 * @version	$Revision: 1.1 $, $Date: 2005/08/26 11:03:41 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
 public class SaveAsAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private JFrame parent = null;
	private XSLTDebuggerPane debugger = null;

 	/**
	 * The constructor for the action which allows the user to 
	 * goto a specific line in the Xml Editor.
	 *
	 * @param editor the XML Editor
	 */
 	public SaveAsAction( JFrame parent, XSLTDebuggerPane debugger) {
		super( "Save As");
		
		putValue( MNEMONIC_KEY, new Integer( 'C'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_C, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "SaveAs");

	 	this.parent = parent;
	 	this.debugger = debugger;
 	}
 	
	/**
	 * The implementation of the cut action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 	
 		execute();
 	}
 	
 	public void execute() {
 		
 			JPanel selectedPanel = debugger.getSelectedArea();
 			
 			boolean isInputPaneBoolean = true;
 			
 			if(selectedPanel != null) {
 				
 				if(selectedPanel instanceof InputView) {
 					isInputPaneBoolean = true;
 				}
 				else if(selectedPanel instanceof OutputView) {
 					isInputPaneBoolean = false;
 				} 
 				
 			}
			final InputPane pane = debugger.getSelectedPane();
			final OutputPane outPane = debugger.getSelectedOutputPane();
			final boolean isInputPane = isInputPaneBoolean;
			
			if (( pane != null) || (outPane != null) ) {
				
				//File file = FileUtilities.selectOutputFile( (File)null, null);
				final File file = FileUtilities.selectOutputFile( parent, null, null);
				
				if ( file != null) {
				
					try	{
						final URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
	
						// Run in Thread!!!
						Runnable runner = new Runnable() {
							public void run()  {
								URL oldURL = null;
								String sourceName = "Output";
		 						
								if(isInputPane) {
									oldURL = pane.getSourceURL();
									sourceName = URLUtilities.toString( url);
								}
								else {
									oldURL = null;
								}
								
								AutomaticProgressMonitor monitor = new AutomaticProgressMonitor( parent, null, "Saving \""+sourceName+"\".", 250);
				
								try {
									//document.setURL( url);
									
									monitor.start();
									
									if(isInputPane) {
										pane.save(url); 
									}
									else {
										outPane.save(url);
									}
									
									
									
									if(isInputPane) {
										pane.getView().select(XngrURLUtilities.getURLFromFile(file).toString());
									}
									//pane.open(file.toURL().toString());
									
									/*SwingUtilities.invokeLater( new Runnable() {
									    public void run() {
											
									    }
									});*/
								} catch ( SAXParseException e) {
									//e.printStackTrace();
									MessageHandler.showError(parent, "Cannot save badly-formed Document.", e, "Saving Error");
								} catch ( IOException ex){
									ex.printStackTrace();
									MessageHandler.showError(parent, "Could not save Document.", ex, "Saving Error");
								} catch ( Exception ex){
									ex.printStackTrace();
								} finally {
									
							    	if ( !monitor.isCanceled()) {
							    		monitor.stop();	
							    	} else {
							    		//document.setURL( oldURL);
							    	}
				
									
								}
							}
						};
				
						// Create and start the thread ...
						Thread thread = new Thread( runner);
						thread.start();
					} catch ( MalformedURLException ex){
						ex.printStackTrace();
						MessageHandler.showError( "Could not save Document.", ex, "Saving Error");
					}
				}
		
			}
 	}
}
