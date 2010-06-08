/*
 * $Id: Match.java,v 1.2 2004/10/08 11:43:18 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.net.URL;

import javax.swing.text.Element;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.editor.XmlDocument;

/**
 * Represents a Match for the Finder.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/08 11:43:18 $
 * @author Dogsbay
 */
public class Match {
	private Element element = null;
	private ExchangerDocument document = null;
	
	private boolean removed = false;

	private int lineNumber = -1;
	private int start = -1;
	private int end = -1;
	private String lineValue = null;
	private URL url = null;
	
	public Match( URL url, int number, int start, int end, String value) {
		// System.out.println( "Match( "+url+", "+number+", "+start+", "+end+", "+value+")");
		this.url = url;
		this.lineNumber = number;
		this.start = start;
		this.end = end;
		this.lineValue = value;
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
			return lineNumber;
		}
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public void update() {
		lineValue = getLineValue();
		lineNumber = getLineNumber();
	}

	public ExchangerDocument getDocument() {
		testElement();
		
		return document;
	}

	public void setDocument( ExchangerDocument document) {
		this.document = document;
	}

	public String getLineValue() {
		testElement();
		
		if ( element != null) {
			XmlDocument doc = (XmlDocument)element.getDocument();

			try {
				return doc.getText( element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
			} catch ( Exception e) {
				e.printStackTrace();
			}
		}
		
		return lineValue;
	}
	
	public boolean equals( Object object) {
		testElement();
		
		if ( object instanceof Match) {
			Match result = (Match)object;

			if ( result.element != null && element != null && result.element == element) {
				return true;
			}
			
			if ( result.getURL() != null && getURL() != null && getURL().equals( result.getURL()) && result.getLineNumber() == getLineNumber()) {
				return true;
			}
		}
		
		return false;
	}

	public URL getURL() {
		return url;
	}

	public String toString() {
		return getPath()+" ["+lineNumber+"] "+lineValue;
	}
	
	private String getPath() {
		String path = url.toString();
		
		if ( url.getProtocol().equals( "file")) {
			path = path.substring( 6, path.length());
		}

		return path;
	}
	
} 
