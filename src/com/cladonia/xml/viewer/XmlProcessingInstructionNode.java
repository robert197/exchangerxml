/*
 * $Id: XmlProcessingInstructionNode.java,v 1.1 2004/08/18 10:18:58 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.viewer;

import java.util.Vector;

import org.dom4j.ProcessingInstruction;

/**
 * The node for the XML tree, containing an XML PI.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/08/18 10:18:58 $
 * @author Dogs bay
 */
public class XmlProcessingInstructionNode extends XmlElementNode {
	private ProcessingInstruction pi = null;
	private Line[] lines = null;

	/**
	 * Constructs the node for the XML PI.
	 *
	 * @param pi The Processing instructiont.
	 */	
	public XmlProcessingInstructionNode( Viewer viewer, ProcessingInstruction pi) {
		super( viewer);
		
		this.pi = pi;
		
		format();		
	}
	
	private void format() {
		Vector lines = new Vector();
		Line current = new Line();
		lines.add( current);
		
		current = parseProcessingInstruction( lines, current, pi);

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