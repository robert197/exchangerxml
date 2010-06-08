/*
 * $Id: XmlEditorKit.java,v 1.6 2004/10/19 14:39:44 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

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
 * @version	$Revision: 1.6 $, $Date: 2004/10/19 14:39:44 $
 * @author Dogsbay
 */
public class XmlEditorKit extends ExchangerEditorKit {

    private static Font _font = null;

    private XmlContext context = null;
    private ErrorList errors = null;
	private XmlViewFactory factory = null;
	private XmlEditorPane editor = null;
	
	/**
	 * Constructs the view factory and the Context.
	 */
    public XmlEditorKit( XmlEditorPane editor, ErrorList errors) {
		super();
		
		this.editor = editor;
		this.errors = errors;
		
		factory = new XmlViewFactory();
	    context = new XmlContext();

	    context.setAttributes( 0, Color.red, Font.PLAIN);
    }

    public void setErrorHighlighting( boolean enabled) {
    	factory.setErrorHighlighting( enabled);
    }

    public boolean isErrorHighlighting() {
    	return factory.isErrorHighlighting();
    }

    /**
     * Get the MIME type of the data that this
     * kit represents support for. This kit supports
     * the type <code>text/xml</code>.
	 *
	 * @return the type.
     */
    public String getContentType() {
		return "text/xml";
    }

    public void setFont( Font font) {
		_font = font;
		
		context.setFont( _font);
    }

	public Font getFont() {
    	return _font;
    }

	public void setHighlight( boolean enabled) {
    	factory.setHighlight( enabled);
    }

    public boolean isHighlight() {
    	return factory.isHighlight();
    }

    public void setAttributes( int id, Color color, int style) {
    	context.setAttributes( id, color, style);
    }
	
    /**
     * Creates an uninitialized xml document.
     *
     * @return the document
     */
    public Document createDefaultDocument() {
		return new XmlDocument( editor, new BufferContent( 1024));
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

	/**
	 * A simple view factory implementation. 
	 */
	class XmlViewFactory implements ViewFactory {
		private View current = null;
		private boolean highlight = false;
		private boolean errorHighlighting = false;
		
		// Creates the XML View.
		public View create(Element elem) {
			if ( editor.isWrapped()) {
				current = new XmlWrappedView( context, elem, errors);
				((XmlWrappedView)current).setHighlight( highlight);
				((XmlWrappedView)current).setErrorHighlighting( errorHighlighting);
			} else {
				current = new XmlView( context, elem, errors);
				((XmlView)current).setHighlight( highlight);
				((XmlView)current).setErrorHighlighting( errorHighlighting);
			}

			return current;
		}
		
		public void setHighlight( boolean enabled) {
			highlight = enabled;
			
			if ( current instanceof XmlView) {
				((XmlView)current).setHighlight( enabled);
			}

			if ( current instanceof XmlWrappedView) {
				((XmlWrappedView)current).setHighlight( enabled);
			}
		}

		public boolean isHighlight() {
			return highlight;
		}

		public void setErrorHighlighting( boolean enabled) {
			errorHighlighting = enabled;
			
			if ( current instanceof XmlView) {
				((XmlView)current).setErrorHighlighting( enabled);
			}

			if ( current instanceof XmlWrappedView) {
				((XmlWrappedView)current).setErrorHighlighting( enabled);
			}
		}

		public boolean isErrorHighlighting() {
			return errorHighlighting;
		}
	}
}







