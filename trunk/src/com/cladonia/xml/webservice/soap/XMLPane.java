/*
 * $Id: XMLPane.java,v 1.3 2004/09/30 11:36:25 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.soap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.FocusEvent;

import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultCaret;
import javax.swing.text.DefaultEditorKit;

import com.cladonia.xml.editor.Constants;
import com.cladonia.xml.editor.ScrollableEditorPanel;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * XML Text Pane.
 * 
 * @version	$Revision: 1.3 $, $Date: 2004/09/30 11:36:25 $
 * @author Dogsbay
 */
public class XMLPane extends JPanel {
	private static final boolean DEBUG = false;
	
	private XmlEditorPane editor = null;
	
	/**
	 * Construct a view.
	 */
	public XMLPane( boolean scrollable) {
		super( new BorderLayout());
		
		if (DEBUG) System.out.println( "InputPane()");
		
		
		editor = new XmlEditorPane( scrollable);
//		editor.setCaret( new XmlCaret( editable));
		editor.setEditable( !scrollable);
		editor.setCaretPosition( 0);
		
		InputMap im = editor.getInputMap();    // remove old binding    
		KeyStroke typed010 = KeyStroke.getKeyStroke("typed \010");    
		InputMap pim = im;    
		
		while ( pim != null) {      
			pim.remove( typed010);      
			pim = pim.getParent();    
		}    // rebind backspace    
		
		KeyStroke bksp = KeyStroke.getKeyStroke("BACK_SPACE");
		im.put( bksp, DefaultEditorKit.deletePrevCharAction);

		JScrollPane scroller = new JScrollPane( new ScrollableEditorPanel( editor));
		scroller.getViewport().setPreferredSize( new Dimension( 100, 100));
		
		add( scroller, BorderLayout.CENTER);

		updatePreferences();
	}
	
	/**
	 * Update the UI stuff...
	 */
	public void updatePreferences() {
		if (DEBUG) System.out.println( "InputPane.updatePreferences()");

		editor.setFont( TextPreferences.getBaseFont());

		setAttributes( Constants.ELEMENT_NAME, TextPreferences.ELEMENT_NAME);
		setAttributes( Constants.ELEMENT_VALUE, TextPreferences.ELEMENT_VALUE);
		setAttributes( Constants.ELEMENT_PREFIX, TextPreferences.ELEMENT_PREFIX);
	
		setAttributes( Constants.ATTRIBUTE_NAME, TextPreferences.ATTRIBUTE_NAME);
		setAttributes( Constants.ATTRIBUTE_VALUE, TextPreferences.ATTRIBUTE_VALUE);
		setAttributes( Constants.ATTRIBUTE_PREFIX, TextPreferences.ATTRIBUTE_PREFIX);
	
		setAttributes( Constants.NAMESPACE_NAME, TextPreferences.NAMESPACE_NAME);
		setAttributes( Constants.NAMESPACE_VALUE, TextPreferences.NAMESPACE_VALUE);
		setAttributes( Constants.NAMESPACE_PREFIX, TextPreferences.NAMESPACE_PREFIX);
	
		setAttributes( Constants.ENTITY, TextPreferences.ENTITY);
		setAttributes( Constants.COMMENT, TextPreferences.COMMENT);
		setAttributes( Constants.CDATA, TextPreferences.CDATA);
		setAttributes( Constants.SPECIAL, TextPreferences.SPECIAL);

		setAttributes( Constants.PI_TARGET, TextPreferences.PI_TARGET);
		setAttributes( Constants.PI_NAME, TextPreferences.PI_NAME);
		setAttributes( Constants.PI_VALUE, TextPreferences.PI_VALUE);

		setAttributes( Constants.STRING_VALUE, TextPreferences.STRING_VALUE);
		setAttributes( Constants.ENTITY_VALUE, TextPreferences.ENTITY_VALUE);

		setAttributes( Constants.ENTITY_DECLARATION, TextPreferences.ENTITY_DECLARATION);
		setAttributes( Constants.ENTITY_NAME, TextPreferences.ENTITY_NAME);
		setAttributes( Constants.ENTITY_TYPE, TextPreferences.ENTITY_TYPE);
	
		setAttributes( Constants.ATTLIST_DECLARATION, TextPreferences.ATTLIST_DECLARATION);
		setAttributes( Constants.ATTLIST_NAME, TextPreferences.ATTLIST_NAME);
		setAttributes( Constants.ATTLIST_TYPE, TextPreferences.ATTLIST_TYPE);
		setAttributes( Constants.ATTLIST_VALUE, TextPreferences.ATTLIST_VALUE);
		setAttributes( Constants.ATTLIST_DEFAULT, TextPreferences.ATTLIST_DEFAULT);
	
		setAttributes( Constants.ELEMENT_DECLARATION, TextPreferences.ELEMENT_DECLARATION);
		setAttributes( Constants.ELEMENT_DECLARATION_NAME, TextPreferences.ELEMENT_DECLARATION_NAME);
		setAttributes( Constants.ELEMENT_DECLARATION_TYPE, TextPreferences.ELEMENT_DECLARATION_TYPE);
		setAttributes( Constants.ELEMENT_DECLARATION_PCDATA, TextPreferences.ELEMENT_DECLARATION_PCDATA);
		setAttributes( Constants.ELEMENT_DECLARATION_OPERATOR, TextPreferences.ELEMENT_DECLARATION_OPERATOR);

		setAttributes( Constants.NOTATION_DECLARATION, TextPreferences.NOTATION_DECLARATION);
		setAttributes( Constants.NOTATION_DECLARATION_NAME, TextPreferences.NOTATION_DECLARATION_NAME);
		setAttributes( Constants.NOTATION_DECLARATION_TYPE, TextPreferences.NOTATION_DECLARATION_TYPE);

		setAttributes( Constants.DOCTYPE_DECLARATION, TextPreferences.DOCTYPE_DECLARATION);
		setAttributes( Constants.DOCTYPE_DECLARATION_TYPE, TextPreferences.DOCTYPE_DECLARATION_TYPE);

		editor.setAntialiasing( TextPreferences.isAntialiasing());
		editor.setTabSize( TextPreferences.getTabSize());
	}
	
	public void setText( String text) {
		editor.setText( text);
		editor.setCaretPosition( 0);
	}
	
	public String getText() {
		return editor.getText();
	}
	
	private void setAttributes( int id, String property) {
		FontType type = TextPreferences.getFontType( property);
		editor.setAttributes( id, type.getColor(), type.getStyle());
	}

	private class XmlCaret extends DefaultCaret {
		boolean focus = false;
		boolean editable = false;
	
		public XmlCaret( boolean editable) {
			setBlinkRate( 500);
			this.editable = editable;
		}

	    // Always leave the selection highlighted
    	public void setSelectionVisible( boolean whatever) {
        	super.setSelectionVisible( true);
	    }

		// Always leave the caret visible
		public boolean isVisible() {
			return editable;
		}
		
		public void focusGained(FocusEvent e) {
			focus = true;
			super.focusGained( e);
		}
	
		public void focusLost(FocusEvent e) {
			focus = false;
		}
	}
}
