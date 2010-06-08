/*
 * $Id: XmlCommentNode.java,v 1.1 2004/03/25 18:50:40 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml.viewer;

import java.util.Vector;

import org.dom4j.Comment;

/**
 * The node for the XML tree, containing an XML comment.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:50:40 $
 * @author Dogsbay
 */
public class XmlCommentNode extends XmlElementNode {
	private Comment comment = null;
	private Line[] lines = null;

	/**
	 * Constructs the node for the XML element.
	 *
	 * @param element the XML element.
	 */	
	public XmlCommentNode( Viewer viewer, Comment comment) {
		super( viewer);
		
		this.comment = comment;
		
		format();		
	}
	
	private void format() {
		Vector lines = new Vector();
		Line current = new Line();
		lines.add( current);
		
		current = parseComment( lines, current, comment);

		this.lines = new Line[lines.size()];
		
		for ( int i = 0; i < lines.size(); i++) {
			this.lines[i] = (Line)lines.elementAt(i);
		}
	}
	
	/**
	 * Returns the formatted lines for this element.
	 *
	 * @return the formatted Lines.
	 */	
	public Line[] getLines() {
		return lines;
	}
} 
