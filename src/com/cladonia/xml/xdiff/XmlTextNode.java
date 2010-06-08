/*
 * $Id: XmlTextNode.java,v 1.1 2004/10/27 15:31:02 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.xdiff;

import java.util.Vector;

import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;


/**
 * The node for the XML diff tree, containing a text node.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/10/27 15:31:02 $
 * @author Dogs bay
 */
public class XmlTextNode extends XmlElementNode {
	private Text textNode = null;
	private Text dummyTextNode = null;
	private Line[] lines = null;
	public String updateTextFrom = null;
	private String combinedText = null;

	/**
	 * Constructs the node for the XML Text node.
	 *
	 * @param textNode The text node
	 */	
	public XmlTextNode(Text textNode,ProcessingInstruction pi, XmlElementNode parent) 
	{
		super();
		
		this.textNode = textNode;
		this.parent = parent;
		
		parsePI(pi);
		
		format();		
	}
	
	public XmlTextNode(Text textNode,XmlElementNode parent) 
	{
		super();
		
		this.textNode = textNode;
		this.parent = parent;
		
		
		format();		
	}
	
	public XmlTextNode(Text dummyText, String text,XmlElementNode parent, boolean update) 
	{
		super();
		
		this.dummyTextNode = dummyText;
		this.parent = parent;
		
		if (update)
		{
			this.updateTextFrom = text;
		}
		else
		{
			this.combinedText = text;
		}
		
		format();		
	}
	
	public XmlTextNode(String text,ProcessingInstruction pi, XmlElementNode parent) 
	{
		super();
		
		this.combinedText = text;
		this.parent = parent;
		
		parsePI(pi);
		
		format();		
	}
	
	private void format() {
		
		Line current = new Line();
		Vector lines = new Vector();
		lines.add( current);
		if (textNode != null)
		{
			if (!"".equals(textNode.asXML().trim()) || getMerged())
			{
				current = parseTextNode( lines, current, textNode);
			}
			else
			{
				return;
			}
		}
		else if (textNode == null && updateTextFrom != null && combinedText == null)
		{
			current = parseTextContent(lines,current,null,updateTextFrom);
		}
		else if (textNode == null && combinedText != null)
		{
			current = parseTextContent(lines,current,combinedText,updateTextFrom);
		}
		
		

		this.lines = new Line[lines.size()];
		
		for ( int i = 0; i < lines.size(); i++) {
			this.lines[i] = (Line)lines.elementAt(i);
		}
	}
	
	protected Line parseTextNode( Vector lines, Line current, Text textNode) 
	{
		current = parseTextContent(lines,current,textNode.asXML().trim(),updateTextFrom);
		return current;
	}
	
	/**
	 * Returns the formatted lines for this element.
	 *
	 * @return the formatted Lines.
	 */	
	public Line[] getLines() {
		return lines;
	}
	
	public Text getTextNode()
	{
		return this.textNode;
	}
	
	public Text getDummyNode()
	{
		return this.dummyTextNode;
	}
	
	public void setTextNode(Text textNode)
	{
		this.textNode = textNode;
	}
	
	public void updateTextNode() 
	{
		format();
	}
	
	private void parsePI(ProcessingInstruction node)
	{	
		String pi = node.asXML();
		
		if (pi.startsWith(UPDATEFROM))
		{
			updateTextFrom = getUpdatePIValue(pi);
		}
		else if (pi.startsWith(INSERT))
		{
			// check for mixed insert which we treat as a change
			if (pi.equals(MIXED_INSERT))
			{
				updateTextFrom = "";
			}
		}
		else if (pi.startsWith(DELETE))
		{	
			// check for mixed delete which we treat as a change
			if (pi.indexOf("\"") != -1 && pi.lastIndexOf(MIXED_DELETE_PI_END) != -1)
			{
				updateTextFrom = getUpdatePIValue(pi);
			}
		}
	}
	
	public XmlTextNode getXmlTextNode()
	{
		return this;
	}
	
	
} 