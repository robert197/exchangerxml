/*
 * $Id: PasteAction.java,v 1.1 2005/08/26 11:03:41 tcurley Exp $
 *
 * Pasteright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
/*
 * $Id: PasteAction.java,v 1.1 2005/08/26 11:03:41 tcurley Exp $
 *
 * Pasteright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.MessagePane;
import com.cladonia.xslt.debugger.ui.OutputPane;
import com.cladonia.xslt.debugger.ui.OutputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to paste
 * Xml Editor.
 *
 * @version	$Revision: 1.1 $, $Date: 2005/08/26 11:03:41 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PasteAction extends AbstractAction {
	private static final boolean DEBUG = false;
	
	private JFrame parent = null;
	private XSLTDebuggerPane debugger = null;
	
	/**
	 * The constructor for the action which allows the user to 
	 * goto a specific line in the Xml Editor.
	 *
	 * @param editor the XML Editor
	 */
	public PasteAction( JFrame parent, XSLTDebuggerPane debugger) {
		super( "Paste");
		
		putValue( MNEMONIC_KEY, new Integer( 'P'));
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_V, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Paste16.gif"));
		putValue( SHORT_DESCRIPTION, "Paste");
		
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
		if (DEBUG) System.out.println( "PasteAction.actionPerformed( "+event+")");
		
		
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
		final MessagePane messagePane = debugger.getSelectedMessagePane();
		final boolean isInputPane = isInputPaneBoolean;
		
		if (( pane != null) || (outPane != null) ) {
			if(isInputPane) {
				
				if ( pane != null) {
					((XmlEditorPane)pane.getEditor()).paste();
				}	
			}
			else {
				
				if ( outPane != null) {
					
					Object editorObj = outPane.getEditor();
					if(editorObj instanceof XmlEditorPane) {
						((XmlEditorPane)editorObj).paste();
					}
					else if(editorObj instanceof JTextArea) {
						
						((JTextArea)editorObj).paste();
					}
					else {
						//System.err.println("unknown class: "+editorObj.getClass());
						
					}
				}
				else {
					
					Object editorObj = messagePane.getEditor();
					if(editorObj instanceof XmlEditorPane) {
						((XmlEditorPane)editorObj).paste();
					}
					else if(editorObj instanceof JTextArea) {
						
						((JTextArea)editorObj).paste();
					}
					else {
						//System.err.println("unknown class: "+editorObj.getClass());
						
					}
				}
				
			}
		}
	}
}
