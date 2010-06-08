/*
 * $Id: RenameElementAction.java,v 1.2 2004/10/28 16:17:07 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.editor.Tag;
import com.cladonia.xml.editor.XmlDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.RenameElementDialog;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * An action that can be used to Comment the selected text.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/28 16:17:07 $
 * @author Dogsbay
 */
 public class RenameElementAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private Editor editor = null;
	private ExchangerEditor parent = null;
	private RenameElementDialog dialog = null;

 	/**
	 * The constructor for the action that Comments the selected text.
	 *
	 * @param editor the XML Editor
	 */
 	public RenameElementAction( ExchangerEditor parent) {
		super( "Rename Element ...");
		
		this.parent = parent;

		if (DEBUG) System.out.println( "RenameElementAction( "+editor+")");
		
		//putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_DOWN, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/RenameElement16.gif"));
		putValue( SHORT_DESCRIPTION, "Rename Element");
		
		setEnabled( false);
	}
	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( view instanceof Editor) {
			editor = (Editor)view;
		} else {
			editor = null;
		}
		
		setDocument( parent.getDocument());
	}

	public void setDocument( ExchangerDocument doc) {
		if ( doc != null) {
			setEnabled( editor != null);
		} else {
			setEnabled( false);
		}
	}
 	
	/**
	 * The implementation of the comment action, called 
	 * after a user action.
	 *
	 * @param event the action event.
	 */
 	public void actionPerformed( ActionEvent event) {
 		if (DEBUG) System.out.println( "RenameElementAction.actionPerformed( "+event+")");
		
		if ( dialog == null) {
			dialog = new RenameElementDialog( parent);
		}

		Tag endTag = null;
		Tag startTag = editor.getCurrentTag();

		if ( startTag != null && startTag.getType() == Tag.END_TAG) {
			endTag = startTag;
			startTag = ((XmlDocument)editor.getEditor().getDocument()).getStartTag( startTag);
		} else if ( startTag == null || (startTag.getType() != Tag.START_TAG && startTag.getType() != Tag.EMPTY_TAG)) {
			startTag = editor.getParentStartTag();
		}

		if ( startTag == null) {
			// Could not find a start tag.
			MessageHandler.showError( "Could not find a Start tag.", "Rename Error");
			return;
		} else if ( startTag.getType() != Tag.EMPTY_TAG) {
			
			if ( startTag.getType() == Tag.START_TAG) {
				if ( endTag == null) {
					endTag = ((XmlDocument)editor.getEditor().getDocument()).getEndTag( startTag);
				}
				
				if ( endTag == null || !endTag.getQualifiedName().equals( startTag.getQualifiedName())) {
					// could not find matching end tag
					MessageHandler.showError( "Could not find a matching End tag.", "Rename Error");
					return;
				}
			} else { 
				// could not find start-tag
				MessageHandler.showError( "Could not find a Start tag.", "Rename Error");
				return;
			}
		}

		dialog.init( editor, startTag);
		dialog.show();
	
		if ( !dialog.isCancelled()) {
			String tag = dialog.getTag();
			
			if ( tag != null && tag.trim().length() > 0) {
				editor.renameCurrentElement( tag);
			}
		}

		editor.setFocus();
 	}
}
