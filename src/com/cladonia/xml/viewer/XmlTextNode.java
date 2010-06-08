/*
 * $Id: XmlTextNode.java,v 1.1 2004/08/30 14:55:28 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.viewer;

import java.util.Vector;

import org.dom4j.Text;

/**
 * The node for the XML tree, containing a text node.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/08/30 14:55:28 $
 * @author Dogs bay
 */
public class XmlTextNode extends XmlElementNode {
	private Text textNode = null;
	private Line[] lines = null;

	/**
	 * Constructs the node for the XML Text node.
	 *
	 * @param cdata The CDATA section.
	 */	
	public XmlTextNode( Viewer viewer, Text textNode) {
		super( viewer);
		
		this.textNode = textNode;
		
		format();		
	}
	
	private void format() {
		Vector lines = new Vector();
		Line current = new Line();
		lines.add( current);
		if (!"".equals(textNode.asXML().trim()))
		{
			current = parseTextNode( lines, current, textNode);
		}
		else
		{
			return;
		}
		

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