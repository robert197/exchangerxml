/*
 * $Id: TextEditorKit.java,v 1.2 2004/10/19 14:39:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

import com.cladonia.xngreditor.ErrorList;

/**
 * The XML editor kit supports handling of editing XML content.  
 * It supports syntax highlighting, tab replacements and automatic 
 * indents.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/19 14:39:44 $
 * @author Dogsbay
 */
public class TextEditorKit extends ExchangerEditorKit {

	private TextViewFactory factory = null;
    private XmlContext context = null;
    private ErrorList errors = null;
	private XmlEditorPane editor = null;
	private Font font = null;
	
	/**
	 * Constructs the view factory and the Context.
	 */
    public TextEditorKit( XmlEditorPane editor) {
		super();
		
		this.editor = editor;
		
	    context = new XmlContext();

		factory = new TextViewFactory();
    }

    /**
     * Get the MIME type of the data that this
     * kit represents support for. This kit supports
     * the type <code>text/xml</code>.
	 *
	 * @return the type.
     */
    public String getContentType() {
		return "text/txt";
    }

    public void setHighlight( boolean enabled) {
    	factory.setHighlight( enabled);
    }

    public boolean isHighlight() {
    	return factory.isHighlight();
    }

    public void setErrorHighlighting( boolean enabled) {
    	factory.setErrorHighlighting( enabled);
    }

    public boolean isErrorHighlighting() {
    	return factory.isErrorHighlighting();
    }

    /**
     * Creates an uninitialized xml document.
     *
     * @return the document
     */
    public Document createDefaultDocument() {
		return new XmlDocument( editor, new BufferContent( 1024));
    }

    public void setFont( Font font) {
		this.font = font;
    }

	public Font getFont() {
    	return font;
    }

	/**
     * Fetches the XML factory that can produce views for 
	 * XML Documents.
     *
     * @return the XML factory
     */
    public final ViewFactory getViewFactory() {
		return factory;
    }
	
	public void cleanup() {
		finalize();
	}
	
	protected void finalize() {
		context.cleanup();
		context = null;
		factory = null;
		editor = null;
	}

    public void setAttributes( int id, Color color, int style) {
    	// do nothing
    }
	
    /**
	 * A simple view factory implementation. 
	 */
	class TextViewFactory implements ViewFactory {
		private View current = null;
		private boolean highlight = false;
		private boolean errorHighlighting = false;
		
		// Creates the XML View.
		public View create( Element elem) {
			if ( editor.isWrapped()) {
				current = new WrappedPlainView( elem);
			} else {
				current = new PlainView( elem);
			}

			return current;
		}
		
		public void setHighlight( boolean enabled) {
			highlight = enabled;
		}

		public boolean isHighlight() {
			return highlight;
		}

		public void setErrorHighlighting( boolean enabled) {
			errorHighlighting = enabled;
		}

		public boolean isErrorHighlighting() {
			return errorHighlighting;
		}
	}
}







