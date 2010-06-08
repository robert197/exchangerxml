/*
 * $Id: XmlCDATANode.java,v 1.1 2004/08/18 10:19:21 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.viewer;

import java.util.Vector;

import org.dom4j.CDATA;

/**
 * The node for the XML tree, containing a CDATA section.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/08/18 10:19:21 $
 * @author Dogs bay
 */
public class XmlCDATANode extends XmlElementNode {
	private CDATA cdata = null;
	private Line[] lines = null;

	/**
	 * Constructs the node for the XML CDATA Section.
	 *
	 * @param cdata The CDATA section.
	 */	
	public XmlCDATANode( Viewer viewer, CDATA cdata) {
		super( viewer);
		
		this.cdata = cdata;
		
		format();		
	}
	
	private void format() {
		Vector lines = new Vector();
		Line current = new Line();
		lines.add( current);
		
		current = parseCDATA( lines, current, cdata);

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