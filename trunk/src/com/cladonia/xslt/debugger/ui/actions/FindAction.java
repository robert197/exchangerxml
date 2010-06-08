/*
 * $Id: FindAction.java,v 1.8 2005/08/26 11:03:41 tcurley Exp $
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
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.FindDialog;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xslt.debugger.ui.InputPane;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.OutputPane;
import com.cladonia.xslt.debugger.ui.OutputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to find information in a XML Document.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
public class FindAction extends AbstractAction {
	private static final boolean DEBUG = false;
	
	private FindDialog dialog = null;
	
	private XSLTDebuggerFrame parent = null;
	private XSLTDebuggerPane debugger = null;
	private ConfigurationProperties properties = null;
	
	/**
	 * The constructor for the action which allows addition of 
	 * documents to the application.
	 */
	public FindAction( XSLTDebuggerFrame parent, ConfigurationProperties props, XSLTDebuggerPane debugger) {
		super( "Find...");
		
		putValue( MNEMONIC_KEY, new Integer( 'F'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Find16.gif"));
		putValue( SHORT_DESCRIPTION, "Find");
		
		this.parent = parent;
		this.debugger = debugger;
		this.properties = props;
	}
	
	private FindDialog getDialog() {
		if ( dialog == null) {
			dialog = new FindDialog( parent, properties,null);
		}
		dialog.init( false);
		
		return dialog;
	}
	
	/**
	 * The implementation of the find action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
	public void actionPerformed( ActionEvent e) {
		FindDialog dialog = getDialog();
		
		
		
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
			
			/*if(dialog.isXPath() == true) {
			 parent.getView().updateModel();
			 }*/
			
			dialog.init( true);
			String selectedText = null;
			if(isInputPane) {
				selectedText = pane.getEditor().getSelectedText();
			}
			else {
				if ( outPane != null) {
					selectedText = outPane.getEditor().getSelectedText();
				}
				
			}
			dialog.show( selectedText);
			
			boolean matchCase = dialog.isCaseSensitive();
			boolean down = dialog.isSearchDirectionDown();
			boolean regExp = dialog.isRegularExpression();
			boolean matchWord = dialog.isMatchWholeWord();
			boolean wrapSearch = dialog.isWrapSearch();
			
			properties.setMatchCase( matchCase);
			properties.setMatchWholeWord( matchWord);
			properties.setDirectionDown( down);
			properties.setBasicSearch( dialog.isBasic());
			properties.setRegularExpression( regExp);
			properties.setWrapSearch( wrapSearch);
			
			if ( dialog.isBasic()) {
				wrapSearch = true;
				regExp = false;
			}
			
			if ( !dialog.isCancelled()) {
				String search = dialog.getSearch();
				
				if ( search != null) {
					//pane = debugger.getSelectedPane();
					
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
		
		
		
		
		
		
		
		
		
		
		
		
		
//		parent.getCurrent().setFocus();
	}
}
