/*
 * $Id: Bookmark.java,v 1.2 2004/07/16 10:08:26 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xml.editor;

import javax.swing.text.Element;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xngreditor.URLUtilities;

/**
 * This Bookmark is used to encapsulate bookmark specific information, 
 * it can both be used as a properties object and a bookmark object.
 *
 * @version $Revision: 1.2 $, $Date: 2004/07/16 10:08:26 $
 * @author Dogsbay
 */
public class Bookmark extends Properties {
	public static final String BOOKMARK = "bookmark";
	private static final String LINE_NUMBER = "line-number";
	private static final String URL = "url";
	private static final String CONTENT = "content";

	private Element element = null;
	private ExchangerDocument document = null;
	private boolean removed = false;
	
	/**
	 * Creates a bookmark for a document element.
	 */
	public Bookmark( Element element, ExchangerDocument document) {
		super( new XElement( BOOKMARK));

		this.element = element;
		this.document = document;
	}
	
	public Bookmark( Properties props) {
		super( props.getElement());
	}
	
	public void init( Element element, ExchangerDocument document) {
		this.element = element;
		this.document = document;
	}

	public boolean isRemoved() {
		return removed;
	}

	public void setRemoved( boolean removed) {
		this.removed = removed;
	}

	public Element getTextElement() {
		testElement();
		
		return element;
	}
	
	private void testElement() {
		if ( element != null && element.getEndOffset() <= 0) {
			element = null;
			document = null;
		}
	}

	public int getLineNumber() {
		testElement();
		
		if ( element != null) {
			Element root = element.getParentElement();
			return root.getElementIndex( element.getStartOffset());
		} else {
			return getInteger( LINE_NUMBER);
		}
	}

	public String getURL() {
		testElement();
		
		if ( element != null && document.getURL() != null) {
			return document.getURL().toString();
		} else {
			return getText( URL);
		}
	}

	public ExchangerDocument getDocument() {
		testElement();
		
		return document;
	}

	public void setDocument( ExchangerDocument document) {
		this.document = document;
	}

	public String getName() {
		testElement();
		
		if ( element != null) {
			if ( document.getURL() != null) {
				return URLUtilities.getFileName( document.getURL().toString());
			} else {
				return document.getName();
			}
		} else {
			return URLUtilities.getFileName( getText( URL));
		}
	}

	public String getContent() {
		testElement();
		
		if ( element != null) {
			XmlDocument doc = (XmlDocument)element.getDocument();

			try {
				return doc.getText( element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
			} catch ( Exception e) {
				e.printStackTrace();
			}
		}
		
		return getText( CONTENT);
	}
	
	public void update() {
		set( CONTENT, getContent());
		set( URL, getURL());
		set( LINE_NUMBER, getLineNumber());
	}
	
	public boolean equals( Object object) {
		testElement();
		
		if ( object instanceof Bookmark) {
			Bookmark bm = (Bookmark)object;

			if ( bm.element != null && element != null && bm.element == element) {
				return true;
			}
			
			if ( bm.getURL() != null && getURL() != null && getURL().equals( bm.getURL()) && bm.getLineNumber() == getLineNumber()) {
				return true;
			}
		}
		
		return false;
	}
}
