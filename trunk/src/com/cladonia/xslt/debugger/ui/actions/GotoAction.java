/*
 * $Id: GotoAction.java,v 1.2 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.GotoDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.OutputPane;
import com.cladonia.xslt.debugger.ui.OutputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to goto a specific line in the
 * Xml Editor.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
 public class GotoAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private GotoDialog dialog = null;
	private JFrame parent = null;
	private XSLTDebuggerPane debugger = null;

 	/**
	 * The constructor for the action which allows the user to 
	 * goto a specific line in the Xml Editor.
	 *
	 * @param editor the XML Editor
	 */
 	public GotoAction( JFrame parent, XSLTDebuggerPane debugger) {
		super( "Goto...");
		
		putValue( MNEMONIC_KEY, new Integer( 'G'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_G, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xml/editor/icons/Goto16.gif"));
		putValue( SHORT_DESCRIPTION, "Goto...");

	 	this.parent = parent;
	 	this.debugger = debugger;
 	}
 	
	/**
	 * The implementation of the goto action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "GotoAction.actionPerformed( "+event+")");
		
		if ( dialog == null) {
			dialog = new GotoDialog( parent);
		}

		dialog.show();
		
		if ( !dialog.isCancelled()) {
			
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
				if(isInputPane) {
					
					if ( pane != null) {
						pane.gotoLine( dialog.getLine());
						
					}	
				}
				else {
					
					if ( outPane != null) {
						
						outPane.gotoLine( dialog.getLine());
						
					}
					
					
				}
			}
			
			
			
		}
 	}
}
