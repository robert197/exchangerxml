/*
 * $Id: SaveAction.java,v 1.2 2005/09/01 09:57:16 tcurley Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.AutomaticProgressMonitor;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.OutputPane;
import com.cladonia.xslt.debugger.ui.OutputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to save
 * Xml Editor.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/01 09:57:16 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class SaveAction extends AbstractAction {
	private static final boolean DEBUG = false;
	
	private JFrame parent = null;
	private XSLTDebuggerPane debugger = null;
	
	/**
	 * The constructor for the action which allows the user to 
	 * goto a specific line in the Xml Editor.
	 *
	 * @param editor the XML Editor
	 */
	public SaveAction( JFrame parent, XSLTDebuggerPane debugger) {
		super( "Save");
		
		putValue( MNEMONIC_KEY, new Integer( 'S'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_S, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Save16.gif"));
		putValue( SHORT_DESCRIPTION, "Save current Document");
		
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
			
			// Run in Thread!!!
			Runnable runner = new Runnable() {
				public void run()  {
					AutomaticProgressMonitor monitor = null;
					try {
						String sourceName = "Output";
						if(isInputPane) {
							sourceName = URLUtilities.toString( pane.getSourceURL());
						}
						else {
							
						}
						
						
						
						if(isInputPane) {
							monitor = new AutomaticProgressMonitor( parent, null, "Saving \""+sourceName+"\".", 250);
							
							monitor.start();
							
							pane.save(); 
							
							pane.reload();
						}
						else {
							
							debugger.getDebuggerFrame().getSaveAsAction().execute();
						}
						
						
						
						
					} catch ( Exception x) {
						String sourceName = "Output";
						if(isInputPane) {
							sourceName = pane.getSourceName(); 
						}
						else {
							
						}
						JOptionPane.showMessageDialog(	parent,
								"Could not save "+sourceName+"\n"+
								x.getMessage(),
								"Save Error",
								JOptionPane.ERROR_MESSAGE);
						x.printStackTrace();
						
					} finally {
						if(monitor != null) {
							monitor.stop();
						}
					}
				}
			};
			
			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
		}
	}
	
	
}
