/*
 * $Id: Range.java,v 1.3 2004/10/29 14:45:16 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xml.editor;

import org.dom4j.Node;
import org.dom4j.Text;

import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;

/**
 * This Range is used to ...
 *
 * @version $Revision: 1.3 $, $Date: 2004/10/29 14:45:16 $
 * @author Dogsbay
 */
public class Range {
	private int start = -1;
	private int end = -1;

	public Range( int start, int end) {
		this.start = start;
		this.end = end;
	}
	
	public Range( XAttribute attribute) {
		this.start = attribute.getAttributeEndPosition() - 1 - (attribute.getText() != null ? attribute.getText().length() : 0);
		this.end = attribute.getAttributeEndPosition()-1;
	}

	public Range( XElement element) {
		XAttribute[] attributes = element.getAttributes();
		
		this.end = element.getContentEndPosition()+1;

		if ( attributes != null && attributes.length > 0) {
			this.start = end;

			for ( int i = 0; i < attributes.length; i++) {
				this.start = Math.min( attributes[i].getAttributeStartPosition(), this.start);
			}
		} else {
			this.start = element.getContentStartPosition();
		}
	}

	public Range( Text text) {
		XElement element = (XElement)text.getParent();
		this.start = element.getContentStartPosition();
		this.end = element.getContentEndPosition()+1;
		boolean found = false;

		for ( int i = 0; i < element.nodeCount(); i++) {
			Node node = element.node( i);
			
			if ( node instanceof XElement) {
				if ( !found) {
					this.start = ((XElement)node).getElementEndPosition();
				} else {
					this.end = ((XElement)node).getElementStartPosition();
					return;
				}
			} else if ( node == text) {
				found = true;
			}
		}
	}

	public int getStart() {
		return start;
	}

	public int getEnd() {
		return end;
	}

	public boolean contains( Range range) {
		return contains( range.getStart()) && contains( range.getEnd());
	}

	public boolean contains( int pos) {
		return pos <= end && pos >= start;
	}
}
