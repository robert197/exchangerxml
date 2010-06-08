/*
 * $Id: FindNextAction.java,v 1.3 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.OutputPane;
import com.cladonia.xslt.debugger.ui.OutputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to find the next information 
 * in a XML Document.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
public class FindNextAction extends AbstractAction {
	private static final boolean DEBUG = false;
	
	private XSLTDebuggerPane debugger = null;
	private ConfigurationProperties properties = null;
	
	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
	public FindNextAction( XSLTDebuggerPane debugger, ConfigurationProperties props) {
		super( "Find Next");
		
		putValue( MNEMONIC_KEY, new Integer( 'N'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/FindNext16.gif"));
		putValue( SHORT_DESCRIPTION, "Find Next");
		
		this.debugger = debugger;
		this.properties = props;
	}
	
	/**
	 * The implementation of the find action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
	public void actionPerformed( ActionEvent e) {
		Vector searches = properties.getSearches();
		
		if ( searches.size() > 0) {
			String search = (String)searches.elementAt(0);
			boolean matchCase = properties.isMatchCase();
			boolean down = properties.isDirectionDown();
			boolean regExp = properties.isRegularExpression();
			boolean matchWord = properties.isMatchWholeWord();
			boolean wrapSearch = properties.isWrapSearch();
			
			if ( search != null) {
				
				JPanel selectedPanel = debugger.getSelectedArea();
				
				boolean isInputPaneBoolean = false;
				
				if(selectedPanel != null) {
					
					if(selectedPanel instanceof InputView) {
						isInputPaneBoolean = true;
					}
					else if(selectedPanel instanceof OutputView) {
						isInputPaneBoolean = false;
					}
					else {
						isInputPaneBoolean = false;			
					}
					
				}
				final InputPane pane = debugger.getSelectedPane();
				final OutputPane outPane = debugger.getSelectedOutputPane();
				final boolean isInputPane = isInputPaneBoolean;
				
				/*System.out.println("pane: "+pane);
				 System.out.println("outPane: "+outPane);
				 System.out.println("messagePane: "+messagePane);*/
				
				if (( pane != null) || (outPane != null)){
					
					
					
					if(isInputPane) {
						
						if ( pane != null) {
							pane.search( search, regExp, matchCase, matchWord, down, wrapSearch);
							
						}	
					}
					else {
						
						if ( outPane != null) {
							
							Object editorObj = outPane.getEditor();
							if(editorObj instanceof XmlEditorPane) {
								outPane.search( search, regExp, matchCase, matchWord, down, wrapSearch);
							}
							else if(editorObj instanceof JTextArea) {
								
								outPane.search( search, regExp, matchCase, matchWord, down, wrapSearch);
							}
							else {
								//System.err.println("unknown class: "+editorObj.getClass());
								
							}
						}
						
						
					}
					
					properties.addSearch( search);
				}
			}
		}
		
		
		
		
	}
}
