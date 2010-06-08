/*
 * $Id: XmlElementNode.java,v 1.7 2004/10/27 15:21:43 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xml.xdiff;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.dom4j.Attribute;
import org.dom4j.CDATA;
import org.dom4j.Comment;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.dom4j.tree.DefaultText;

import com.cladonia.xml.XElement;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The node for the XDiff XML tree, containing an XML element.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/10/27 15:21:43 $
 * @author Edwin Dankertr <edankert@cladonia.com>
 * @author Dogs bay
 */
public class XmlElementNode extends DefaultMutableTreeNode {
	private static final int MAX_LINE_LENGTH = 80;
	private XElement element = null;
	private Line[] lines = null;
	
	private boolean isEndTag = false;
	
	// for xdiff changes
	public boolean insertElement = false;
	public Vector insertAttributes = null;
	public boolean deleteElement = false;
	public Vector deleteAttributes = null;
	public String updateElementFrom = null;
	private boolean updateElementSet = false;
	public Hashtable updateAttributes = null;
	private boolean merged = false;
	private XElement mergeAdded = null; 
	public XElement mergeDelete = null;
	
	public XmlElementNode parent = null;
	private ImageIcon diffIcon = null;
	private ImageIcon loadedIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/XDiff5.gif");
	
	public final String INSERT = "<?INSERT";
	public final String MIXED_INSERT = "<?INSERT ?>";
	private final String UPDATE = "<?UPDATE";
	public final String UPDATEFROM = "<?UPDATE FROM";
	public final String DELETE = "<?DELETE";
	public final String MIXED_DELETE = "<?DELETE ?>";
	private final String FROM = "FROM";
	private final String PI_END = "?>";
	public final String MIXED_DELETE_PI_END = "\"?>";
	
	private Color currentColor = Color.BLACK;
	private final static Color COLOR_GREEN = new Color(0,128,64);
	private final static Color COLOR_MERGED = Color.MAGENTA;


	/**
	 * Constructs the node for the XML element.
	 *
	 * @param element the XML element.
	 */	
	public XmlElementNode() {
		// do nothing constructor
	}
	
	/**
	 * Constructs the node for the XML element.
	 *
	 * @param element the XML element.
	 */	
	public XmlElementNode( XElement element) {
		this(element, false,null);
	}
	
	
	
	
	/**
	 * Constructs the node for the XML element.
	 *
	 * @param element the XML element.
	 */	
	public XmlElementNode( XElement element, XmlElementNode parent) {
		this(element, false,parent);
		
	}
	
	/**
	 * Constructs the the XML element node.
	 *
	 * @param element the XML element.
	 */	
	public XmlElementNode( XElement element, boolean end,XmlElementNode parent) {
		
		
		this.element = element;
		this.parent = parent;
		
		isEndTag = end;
		format();
		
		if ( !isEndTag()) {
			
				List childNodes = element.content();
				
				for ( int i = 0; i < element.nodeCount(); i++) {
					Node node = element.node( i);
					
					if ( node instanceof XElement) {
						add( new XmlElementNode( (XElement)node,this));
					}
					else if ( (node instanceof Text) && isMixedElementAndContent(element) && !isWhiteSpace(node)) 
					{
						
						if (i<element.nodeCount())
						{
							Node nextNode = element.node(i+1);
							if (nextNode instanceof ProcessingInstruction)
							{
								add( new XmlTextNode((Text)node,(ProcessingInstruction)nextNode,this));
							}
							else
							{
								// can't be PI after it
								add( new XmlTextNode((Text)node,this));
							}
						}
						
						
						
						
						
						
//						if (i<element.nodeCount())
//						{
//							Node nextNode = element.node(i+1);
//							if (nextNode instanceof ProcessingInstruction)
//							{
//								add( new XmlTextNode((Text)node,(ProcessingInstruction)nextNode,this));
//							}
//							else if (nextNode instanceof Text)
//							{
//								//	append the node and check for the next one
//								String combined = node.asXML().trim()+" "+nextNode.asXML().trim();
//								i++;
//								while (i<element.nodeCount())
//								{
//									Node nextNextNode = element.node(i+1);
//									if (nextNextNode instanceof Text)
//									{
//										// combine
//										combined = combined+" "+nextNextNode.asXML().trim();
//										i++;
//									}
//									else
//										break;
//								}
//								
//								if (i< element.nodeCount())
//								{
//									Node nextNextNode = element.node(i+1);
//									if (nextNextNode instanceof ProcessingInstruction)
//									{
//										add( new XmlTextNode(combined,(ProcessingInstruction)nextNextNode,this));
//									}
//									else
//									{
//										add( new XmlTextNode(combined,this,false));
//									}
//								}
//								else
//								{
//									add( new XmlTextNode(combined,this,false));
//								}
//							}
//							else
//							{
//								// can't be PI after it
//								add( new XmlTextNode((Text)node,this));
//							}
//						}
//						else
//						{
//							// can't be PI after it
//							add( new XmlTextNode((Text)node,this));
//						}
						
					}
					else if ( (node instanceof ProcessingInstruction) && isMixedElementAndContent(element)) 
					{
						String pi = node.asXML();
						if (pi.startsWith(DELETE))
						{	
							// check for mixed delete which we treat as a change
							if (pi.indexOf("\"") != -1 && pi.lastIndexOf(MIXED_DELETE_PI_END) != -1)
							{
								String updateTextFrom = getUpdatePIValue(pi);
								
								DefaultText dummy = new DefaultText("");
								childNodes.add(i+1,dummy);
								
								add( new XmlTextNode(dummy,updateTextFrom,this,true));
							}
						}
					}
				}
				
				List elements = element.elements();
    			// create an end node...	
				if ( elements != null && elements.size() > 0 ) {
					add( new XmlElementNode( element, true, this));
				}
		}
	
		//format();
		
	}

	public boolean isEndTag() {
		return isEndTag;
	}
	
	public void update() {
//		for ( int i = 0; i < getChildCount(); i++) {
//			XmlElementNode node = (XmlElementNode)getChildAt( i);
//			node.update();
//		}
		
		format();
	}
	
	public void updateCurrentAndChildren() {
		for ( int i = 0; i < getChildCount(); i++) {
			XmlElementNode node = (XmlElementNode)getChildAt( i);
			node.updateCurrentAndChildren();
		}
		
		if (getElement() != null)
		{
			format();
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
	

	/**
	 * Constructs the node for the XML element.
	 *
	 * @return element the XML element.
	 */	
	public XElement getElement() {
		return element;
	}
	
	public XElement getMergeAdded()
	{
		return mergeAdded;
	}
	
	public void setMergeAdded(XElement element)
	{
		this.mergeAdded = element;
	}
	
	public XElement getMergeDelete()
	{
		return mergeDelete;
	}
	
	public void setMergeDelete(XElement element)
	{
		this.mergeDelete = element;
	}
	
	/**
	 * Set the underlying XML element.
	 *
	 * @param element the XML element.
	 */	
	public void setElement(XElement element) {
		this.element = element;
	}
		
	public void setMerged(boolean flag) {
		this.merged = flag;
	}
	
	public boolean getMerged() {
		return this.merged;
	}
	
	private void format() {
		Vector lines = new Vector();
		Line current = new Line();
		lines.add( current);
		
		if ( isEndTag()) {
			current = parseEndTag( lines, current, element);
		} else {
			current = parseElement( lines, current, element);
		}

		this.lines = new Line[lines.size()];
		
		for ( int i = 0; i < lines.size(); i++) {
			this.lines[i] = (Line)lines.elementAt(i);
		}
	}

	protected Line parseElement( Vector lines, Line current, XElement elem) 
	{

		// need to parse PI diff instruction
		if (hasProcessingInstruction(elem))
		{
			parseDiffPIs(elem);
		}
		
		current = parseStartTag( lines, current, elem);
		
		// if its mixed content then let text nodes take care of that
		if (isMixedElementAndContent(elem))
		{
			// let the text nodes take care of it
			return current;
		}
		

		if (hasTextOrCDATA(elem))
		{	
			// parse the content, note do not strip whitespace as xdiff detects these
			if (!hasCDATA(elem))
			{
				if (insertElement || deleteElement)
				{
					// then we don't want whitespace
					current = parseContent( lines, current, elem.getText().trim());
				}
				else
					current = parseContent( lines, current, elem.getText());
			}
			else
			{
				for ( int i = 0; i < elem.nodeCount(); i++) 
				{
					Node node = elem.node( i);
					
					
					if ((node instanceof Text) || (node instanceof Entity)) 
					{
						String text;
						if (insertElement || deleteElement)
						{
							// can delete trailing whitspace in these cases
							text = node.getText().trim();
						}
						else
						{
							// xdiff includes trailing whitespace at the moment
							text = node.getText();
						}
						
						current = parseContent( lines, current, text);	
					} 
					else if ( (node instanceof CDATA)) 
					{
						current = parseCDATA( lines, current, (CDATA)node);
					}
				}
			}

			if ( !elem.hasChildElements()) {
				current = parseEndTag( lines, current, elem);
			}
		}
		else
		{
			// check blanked nodes
			if (updateElementFrom != null)
			{
				// element content must have been blanked
				current = parseContent( lines, current, "");
				
				if ( !elem.hasChildElements()) {
					current = parseEndTag( lines, current, elem);
				}
			}
		}

		//clearDiffPIValues();
		
		return current;
	}
	
	private void clearDiffPIValues()
	{
		insertElement = false;
		insertAttributes = null;
		deleteElement = false;
		deleteAttributes = null;
		updateElementFrom = null;
		updateAttributes = null;
	}
	
	// parse the xdiff ProcessingInstructions
	private void parseDiffPIs(XElement element)
	{	
		boolean updateFromFound = false;
		
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node(i);
			
			if  (node instanceof ProcessingInstruction) 
			{
				String pi = node.asXML();
				
				if (pi.startsWith(UPDATEFROM))
				{
					// element update
					if (updateFromFound == false)
					{
						updateElementFrom = getUpdatePIValue(pi);
						updateFromFound = true;
					}
					else
					{
						// here before, probably mixed content
						updateElementFrom += " "+getUpdatePIValue(pi);
					}
					setDiffIcon();
				}
				else if (pi.startsWith(UPDATE))
				{
					// attribute update
					updateAttributes = getUpdateAttributeTable();
					String attrName =  getUpdateAttrName(pi);
					String attrValue = getUpdatePIValue(pi);
					
					updateAttributes.put(attrName, attrValue);
					setDiffIcon();
				}
				else if (pi.startsWith(INSERT))
				{
					// check for mixed insert which we treat as a change
					if (pi.equals(MIXED_INSERT))
					{
						updateElementFrom = "";
					}
					else
					{
						// element or attribute?
						String name = getPIName(pi);
				
						if (element.getQualifiedName().equals(name))
						{
							// element insert
							insertElement = true;
						}
						else
						{
							// attribute insert
							insertAttributes = getInsertAttributes();
							insertAttributes.add(name);
						}
					}
					
					setDiffIcon();
				}
				else if (pi.startsWith(DELETE))
				{	
					// check for mixed delete which we treat as a change
					if (pi.indexOf("\"") != -1 && pi.lastIndexOf(MIXED_DELETE_PI_END) != -1)
					{
						updateElementFrom = getUpdatePIValue(pi);
					}
					else
					{
					
						//element or attribute?
						String name = getPIName(pi);
				
						if (element.getQualifiedName().equals(name))
						{
							// element delete
							deleteElement = true;
						}
						else
						{
							// attribute delete
							deleteAttributes = getDeleteAttributes();
							deleteAttributes.add(name);
						}
					}
					
					setDiffIcon();
				}
			}
		}
	}
	
	private Vector getDeleteAttributes()
	{
		if (deleteAttributes == null)
		{
			deleteAttributes = new Vector();
		}
		
		return deleteAttributes;
	}
	
	private Vector getInsertAttributes()
	{
		if (insertAttributes == null)
		{
			insertAttributes = new Vector();
		}
		
		return insertAttributes;
	}
	
	private String getPIName(String pi)
	{
		int end = pi.lastIndexOf(PI_END);
		return pi.substring(INSERT.length()+1,end);
	}
	
	private String getUpdateAttrName(String pi)
	{
		int from = pi.indexOf(FROM);
		return pi.substring(UPDATE.length()+1,from-1);
	}
	
	private Hashtable getUpdateAttributeTable()
	{
		if (updateAttributes == null)
		{
			updateAttributes = new Hashtable();
		}
		
		return updateAttributes;		
	}
	
	// parse an update PI
	public String getUpdatePIValue(String pi)
	{
		int firstQuote = pi.indexOf("\"");
		int lastQuote = pi.lastIndexOf("\"");
		
		return pi.substring(firstQuote+1,lastQuote);
	}
	
	//	 has text and\or cdata 
	private static boolean hasTextOrCDATA(XElement element) 
	{		
		boolean textCDATAFound = false;
			
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if (node instanceof CDATA)
			{
				textCDATAFound = true;
			}
			else if (node instanceof Text)
			{
				if ( !isWhiteSpace( node)) {
					textCDATAFound = true;
				}
			}
		}

		return textCDATAFound;
	}
	
	//	returns true if the element contains a PI
	private static boolean hasProcessingInstruction( XElement element) 
	{
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if  (node instanceof ProcessingInstruction) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	//	 returns true if the element contains a CDATA section 
	private static boolean hasCDATA( XElement element) 
	{
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if  (node instanceof CDATA) 
			{
				return true;
			}
		}
		
		return false;
	}
	
	// Elements parsed here can be both mixed and normal but then contained in a mixed element...
	protected Line parseMixedElement( Vector lines, Line current, XElement elem) {

		current = parseStartTag( lines, current, elem);

	
			if ( isMixed( elem)) {
				for ( int i = 0; i < elem.nodeCount(); i++) {
					Node node = elem.node( i);
					
					if ( node instanceof XElement) {
						current = parseMixedElement( lines, current, (XElement)node);
					} else if ( (node instanceof Text) || (node instanceof CDATA) || (node instanceof Entity)) {
						String text = node.getText();
						current = parseContent( lines, current, text);
					} else if ( (node instanceof Comment)) {
						current = parseComment( lines, current, (Comment)node);
					}
				}
			} else {
				List elements = (List)elem.elements();

				if ( elements != null && elements.size() > 0) {
					Iterator iterator = elements.iterator();
					
					while ( iterator.hasNext()) {
						current = parseMixedElement( lines, current, (XElement)iterator.next());
					}

					current = parseEndTag( lines, current, elem);
				} else if ( elem.hasContent()) {
					current = parseContent( lines, current, elem.getText());
					current = parseEndTag( lines, current, elem);
				}
			}
		
		return current;
	}

	protected Line parseComment( Vector lines, Line current, Comment comment) {
		StyledElement styledElement = new StyledElement();
		styledElement.addString( COMMENT_START);
		
		current.addStyledElement( styledElement);
		current = parseCommentContent( lines, current, comment.getText());
		
		styledElement = new StyledElement();
		styledElement.addString( COMMENT_END);
		current.addStyledElement( styledElement);
		
		return current;
	}

	// Create a styled version of the start-tag.
	protected Line parseStartTag( Vector lines, Line current, Element elem) {
		
		boolean localInsertElement = false;
		boolean localDeleteElement = false;
		
		StyledElement styledElement = new StyledElement();
		
		if (insertElement || insideInsertChain(parent))
		{
			localInsertElement = true;
			styledElement.addString(INSERT_OPEN_BRACKET);
			currentColor = COLOR_GREEN;
		}
		else if (deleteElement || insideDeleteChain(parent))
		{
			localDeleteElement = true;
			styledElement.addString(DELETE_OPEN_BRACKET);
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			styledElement.addString(MERGED_OPEN_BRACKET);
			currentColor = COLOR_MERGED;
		}
		else
		{
			styledElement.addString(OPEN_BRACKET);
			currentColor = Color.BLACK;	
		}
	
		styledElement.addString( new ElementName( elem.getQualifiedName()));
		current.addStyledElement( styledElement);

		Namespace ns = elem.getNamespace();

		if ( ns != null) {
			XElement parent = (XElement)elem.getParent();
		
			if ( parent != null) {
				Namespace prev = parent.getNamespaceForPrefix( ns.getPrefix());

				if ( prev == null || !ns.equals( prev)) {
					StyledElement sns = formatNamespace( ns);
					
					if ( sns != null) {
						if ( current.length()+sns.length()+1 > MAX_LINE_LENGTH) {
							current = new Line();
							lines.add( current);
							current.addStyledString( TAB);
						} else {
							current.addStyledString( SPACE);
						}
					
						current.addStyledElement( sns);
					}
				} 
			} else {
				StyledElement sns = formatNamespace( ns);
				
				if ( sns != null) {
					if ( current.length()+sns.length()+1 > MAX_LINE_LENGTH) {
						current = new Line();
						lines.add( current);
						current.addStyledString( TAB);
					} else {
						current.addStyledString( SPACE);
					}
				
					current.addStyledElement( sns);
				}
			}
		}

		List namespaces = elem.additionalNamespaces();

		if ( namespaces != null && namespaces.size() > 0) {
			Iterator iterator = namespaces.iterator();
		
			for ( int i = 0; i < namespaces.size(); i++) {
				StyledElement sns = formatNamespace( (Namespace)iterator.next());
				
				if ( sns != null) {
					if ( current.length()+sns.length()+1 > MAX_LINE_LENGTH) {
						current = new Line();
						lines.add( current);
						current.addStyledString( TAB);
					} else {
						current.addStyledString( SPACE);
					}
				
					current.addStyledElement( sns);
				}
			}
		}
		

		
		List attributes = elem.attributes();

		if ( attributes != null && attributes.size() > 0) {
			Iterator iterator = attributes.iterator();
		
			for ( int i = 0; i < attributes.size(); i++) {
				StyledElement sa = formatAttribute( (Attribute)iterator.next());
				
				if ( current.length()+sa.length()+1 > MAX_LINE_LENGTH) {
					current = new Line();
					lines.add( current);
					current.addStyledString( TAB);
				} else {
					current.addStyledString( SPACE);
				}
			
				current.addStyledElement( sa);
			}
		}
		

		if ( !elem.hasContent() || hasPIorWhiteSpaceOnly((XElement)elem)) 
		{	
			if (updateElementFrom != null)
			{
				// content was blanked, don't add closing slash
			}
			else if (localInsertElement)
			{
				current.addStyledString(INSERT_SLASH);
			}
			else if (localDeleteElement)
			{
				current.addStyledString(DELETE_SLASH);
			}
			else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
			{
				current.addStyledString(MERGED_SLASH);
			}
			else
			{
				current.addStyledString(SLASH);
			}
		} 

		
		if (localInsertElement)
		{
			current.addStyledString(INSERT_CLOSE_BRACKET);
		}
		else if (localDeleteElement)
		{
			current.addStyledString(DELETE_CLOSE_BRACKET);
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			current.addStyledString(MERGED_CLOSE_BRACKET);
		}
		else
		{
			current.addStyledString(CLOSE_BRACKET);
		}
		
		currentColor = Color.BLACK;
		return current;
	}
	
	// checks to see if any ancestors was a new inserted elemnt 
	private boolean insideInsertChain(XmlElementNode parent)
	{
		if (parent == null)
		{
			return false;
		}
		
		while (parent != null)
		{
			if (parent.insertElement)
			{
				return true;
			}
			else
			{
				parent = parent.getXmlElementNodeParent();
			}
		}
		
		return false;
	}
	
	// checks to see if any ancestors was a new inserted elemnt as part of a merge 
	private boolean insideInsertMergeChain(XmlElementNode parent)
	{
		if (parent == null)
		{
			return false;
		}
		
		while (parent != null)
		{
			if (parent.getMergeAdded() != null)
			{
				return true;
			}
			else
			{
				parent = parent.getXmlElementNodeParent();
			}
		}
		
		return false;
	}
	
	// checks to see if corresponding startvtag was merged 
	private boolean startTagMixedMerged(XmlElementNode parent,XElement elem)
	{
		// must have elements
		if (!elem.hasChildElements())
		{
			return false;
		}
	
		if (parent == null)
		{
			return false;
		}
		
		if (parent.getMerged())
		{
			return true;
		}
		
		return false;
	}
	
	//	checks to see if any ancestors was a deleted elemnt 
	private boolean insideDeleteChain(XmlElementNode parent)
	{
		if (parent == null)
		{
			return false;
		}
		
		while (parent != null)
		{
			if (parent.deleteElement)
			{
				return true;
			}
			else
			{
				parent = parent.getXmlElementNodeParent();
			}
		}
		
		return false;
	}
	
	private XmlElementNode getXmlElementNodeParent()
	{
		return this.parent;
	}
	
	public void setDiffIcon()
	{
		diffIcon = loadedIcon;
		
		XmlElementNode tempParent = this.parent;
		while (tempParent != null)
		{
			tempParent.setDiffIcon();
			tempParent = tempParent.getXmlElementNodeParent();
		}
	}
	
	public void unSetDiffIcon()
	{
		// check the children of the current node
		for (int i=0;i<this.getChildCount();i++)
		{
			XmlElementNode node = (XmlElementNode)this.getChildAt(i);
			if (node.getDiffIcon() != null)
			{
				return;
			}
		}
		
		diffIcon = null;
		
		if (parent == null)
		{
			// at the root
			return;
		}
		
		// do any other nodes at this level have the difficon?
		for (int i=0;i<parent.getChildCount();i++)
		{
			XmlElementNode node = (XmlElementNode)parent.getChildAt(i);
			if (node.getDiffIcon() != null)
			{
				return;
			}
		}
		
		
		XmlElementNode tempParent = this.parent;
		while (tempParent != null && !tempParent.getMerged())
		{	
			
			tempParent.unSetDiffIcon();
			tempParent = tempParent.getXmlElementNodeParent();
			
		}
	}
	
	public ImageIcon getDiffIcon()
	{
		return this.diffIcon;
	}
	
	//	checks for existance of PI only
	private static boolean hasPIorWhiteSpaceOnly(XElement element) 
	{		
		boolean piWhiteFound = false;
			
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			
			if ((node instanceof XElement)|| (node instanceof Entity) || 
					(node instanceof Comment) || (node instanceof CDATA))
			{
				return false;
			}
			else if (node instanceof Text)
			{
				if ( !isWhiteSpace( node)) {
					return false;
				}
				else
				{
					piWhiteFound = true;
				}
			}
			else if (node instanceof ProcessingInstruction)
			{
				piWhiteFound = true;
			}
			
		}

		return piWhiteFound;
	}
	
	// Create a styled version of the element content.
	protected Line parseContent( Vector lines, Line current, String text) {
		

		if (insertElement || insideInsertChain(parent))
		{
			currentColor = COLOR_GREEN;
		}
		else if (deleteElement || insideDeleteChain(parent))
		{
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			currentColor = COLOR_MERGED;
		}
		else
		{
			currentColor = Color.BLACK;
		}
		
		// check for updated content
		if (!updateElementSet && updateElementFrom != null)
		{
			StyledString updatedValue = new ElementPreviousValue(updateElementFrom);
			updatedValue.setStrikeThrough(true);
			current.addStyledString(updatedValue);
			
			// set updated content flag so it only gets added once (when mutiple text nodes are called)
			updateElementSet = true;
			
			currentColor = Color.BLUE;
		}
		
		
		if ( (current.length()+1 >= MAX_LINE_LENGTH) && (text.length() > 0)) {
			current = new Line();
			lines.add( current);
			current.addStyledString( TAB);
		}

		if ( text.length() > 0) {
			boolean parsed = false;
			
			while ( !parsed) {
				int length = MAX_LINE_LENGTH - (current.length()+1);
				
				if ( length > text.length()) {
					int index = 0;

					if ( text.indexOf( "\n") != -1) {
						index = text.indexOf( "\n");
					} else if ( text.indexOf( "\r") != -1) {
						index = text.indexOf( "\r");
					} else {
						index = text.length();
					}
					
					if ( index != 0) {
						String string = text.substring( 0, index);
						current.addStyledString( new ElementValue( string));
					}

					if ( index == text.length()) {
						parsed = true;
					} else {
						text = text.substring( index + 1, text.length());
					}
				} else {
					int index = 0;
					String sub = text.substring( 0, length);

					if ( sub.indexOf( "\n") != -1) {
						index = sub.indexOf( "\n");
					} else if ( sub.indexOf( "\r") != -1) {
						index = sub.indexOf( "\r");
					} else if ( sub.lastIndexOf( " ") != -1) {
						index = sub.lastIndexOf( " ");
					} 
					
					if ( index != 0) {
						String string = sub.substring( 0, index);
						current.addStyledString( new ElementValue( string));

						text = text.substring( index + 1, text.length());
					} else { // Text is too long without any whitespaces...
						int nlindex = text.indexOf( "\n");
						int rindex = text.indexOf( "\r");
						int spindex = sub.indexOf( " ");
						
						if ( nlindex == -1) {
							nlindex = Integer.MAX_VALUE;
						}
						if ( rindex == -1) {
							rindex = Integer.MAX_VALUE;
						}
						if ( spindex == -1) {
							spindex = Integer.MAX_VALUE;
						}
						
						index = Math.min( nlindex, rindex);
						index = Math.min( index, spindex);
						index = Math.min( index, text.length());

						String string = text.substring( 0, index);
						current.addStyledString( new ElementValue( string));

						if ( index == text.length()) {
							parsed = true;
						} else {
							text = text.substring( index + 1, text.length());
						}
					}
				}

				if ( !parsed) {
					current = new Line();
					lines.add( current);
					current.addStyledString( TAB);
				}
			}
		}
		
		currentColor = Color.BLACK;
		return current;
	}
	
//	 Create a styled version of the element content.
	protected Line parseTextContent( Vector lines, Line current, String text, String updateTextFrom) {
		

		if (insertElement || insideInsertChain(parent))
		{
			currentColor = COLOR_GREEN;
		}
		else if (deleteElement || insideDeleteChain(parent))
		{
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			currentColor = COLOR_MERGED;
		}
		else
		{
			currentColor = Color.BLACK;
		}
		
		// check for updated text content
		if (updateTextFrom != null)
		{
			StyledString updatedValue = new ElementPreviousValue(updateTextFrom);
			updatedValue.setStrikeThrough(true);
			current.addStyledString(updatedValue);
			
			currentColor = Color.BLUE;
		}
		
		
		if ( (current.length()+1 >= MAX_LINE_LENGTH) && (text.length() > 0)) {
			current = new Line();
			lines.add( current);
			current.addStyledString( TAB);
		}

		if (text != null && text.length() > 0) {
			boolean parsed = false;
			
			while ( !parsed) {
				int length = MAX_LINE_LENGTH - (current.length()+1);
				
				if ( length > text.length()) {
					int index = 0;

					if ( text.indexOf( "\n") != -1) {
						index = text.indexOf( "\n");
					} else if ( text.indexOf( "\r") != -1) {
						index = text.indexOf( "\r");
					} else {
						index = text.length();
					}
					
					if ( index != 0) {
						String string = text.substring( 0, index);
						current.addStyledString( new ElementValue( string));
					}

					if ( index == text.length()) {
						parsed = true;
					} else {
						text = text.substring( index + 1, text.length());
					}
				} else {
					int index = 0;
					String sub = text.substring( 0, length);

					if ( sub.indexOf( "\n") != -1) {
						index = sub.indexOf( "\n");
					} else if ( sub.indexOf( "\r") != -1) {
						index = sub.indexOf( "\r");
					} else if ( sub.lastIndexOf( " ") != -1) {
						index = sub.lastIndexOf( " ");
					} 
					
					if ( index != 0) {
						String string = sub.substring( 0, index);
						current.addStyledString( new ElementValue( string));

						text = text.substring( index + 1, text.length());
					} else { // Text is too long without any whitespaces...
						int nlindex = text.indexOf( "\n");
						int rindex = text.indexOf( "\r");
						int spindex = sub.indexOf( " ");
						
						if ( nlindex == -1) {
							nlindex = Integer.MAX_VALUE;
						}
						if ( rindex == -1) {
							rindex = Integer.MAX_VALUE;
						}
						if ( spindex == -1) {
							spindex = Integer.MAX_VALUE;
						}
						
						index = Math.min( nlindex, rindex);
						index = Math.min( index, spindex);
						index = Math.min( index, text.length());

						String string = text.substring( 0, index);
						current.addStyledString( new ElementValue( string));

						if ( index == text.length()) {
							parsed = true;
						} else {
							text = text.substring( index + 1, text.length());
						}
					}
				}

				if ( !parsed) {
					current = new Line();
					lines.add( current);
					current.addStyledString( TAB);
				}
			}
		}
		
		currentColor = Color.BLACK;
		return current;
	}
	
	protected Line parseCDATA( Vector lines, Line current, CDATA cdata) {
		
		if (insertElement || insideInsertChain(parent))
		{
			currentColor = COLOR_GREEN;
		}
		else if (deleteElement || insideDeleteChain(parent))
		{
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			currentColor = COLOR_MERGED;
		}
		else
		{
			currentColor = Color.BLACK;
		}
		
		
		// check for updated content
		if (updateElementFrom != null)
		{
			currentColor = Color.BLUE;
		}
		
		StyledElement styledElement = new StyledElement();
		styledElement.addString(new CDATA_SECTION(cdata.asXML()));
		current.addStyledElement( styledElement);
		
		currentColor = Color.BLACK;
		return current;
	}
	
	protected Line parseCommentContent( Vector lines, Line current, String text) {

		if ( (current.length()+1 >= MAX_LINE_LENGTH) && (text.length() > 0)) {
			current = new Line();
			lines.add( current);
			current.addStyledString( TAB);
		}

		if ( text.length() > 0) {
			boolean parsed = false;
			
			while ( !parsed) {
				int length = MAX_LINE_LENGTH - (current.length()+1);
				
				if ( length > text.length()) {
					int index = 0;

					if ( text.indexOf( "\n") != -1) {
						index = text.indexOf( "\n");
					} else if ( text.indexOf( "\r") != -1) {
						index = text.indexOf( "\r");
					} else {
						index = text.length();
					}
					
					if ( index != 0) {
						String string = text.substring( 0, index);
						current.addStyledString( new CommentText( string));
					}

					if ( index == text.length()) {
						parsed = true;
					} else {
						text = text.substring( index + 1, text.length());
					}
				} else {
					int index = 0;
					String sub = text.substring( 0, length);

					if ( sub.indexOf( "\n") != -1) {
						index = sub.indexOf( "\n");
					} else if ( sub.indexOf( "\r") != -1) {
						index = sub.indexOf( "\r");
					} else if ( sub.lastIndexOf( " ") != -1) {
						index = sub.lastIndexOf( " ");
					} 
					
					if ( index != 0) {
						String string = sub.substring( 0, index);
						current.addStyledString( new CommentText( string));

						text = text.substring( index + 1, text.length());
					} else { // Text is too long without any whitespaces...
						int nlindex = text.indexOf( "\n");
						int rindex = text.indexOf( "\r");
						int spindex = sub.indexOf( " ");
						
						if ( nlindex == -1) {
							nlindex = Integer.MAX_VALUE;
						}
						if ( rindex == -1) {
							rindex = Integer.MAX_VALUE;
						}
						if ( spindex == -1) {
							spindex = Integer.MAX_VALUE;
						}
						
						index = Math.min( nlindex, rindex);
						index = Math.min( index, spindex);
						index = Math.min( index, text.length());

						String string = text.substring( 0, index);
						current.addStyledString( new CommentText( string));

						if ( index == text.length()) {
							parsed = true;
						} else {
							text = text.substring( index + 1, text.length());
						}
					}
				}

				if ( !parsed) {
					current = new Line();
					lines.add( current);
					current.addStyledString( TAB);
				}
			}
		}
		
		return current;
	}

	// Create a styled version of the end-tag.
	protected Line parseEndTag( Vector lines, Line current, XElement elem) {
		
		boolean localInsertElement = false;
		boolean localDeleteElement = false;
		
		// need to parse PI diff instruction
		if (hasProcessingInstruction(elem))
		{
			parseDiffPIs(elem);
		}
		
		StyledElement styledEnd = new StyledElement();
		if (insertElement || insideInsertChain(parent))
		{
			localInsertElement = true;
			styledEnd.addString(INSERT_OPEN_BRACKET);
			styledEnd.addString(INSERT_SLASH);
			currentColor = COLOR_GREEN;
			
		}
		else if (deleteElement || insideDeleteChain(parent))
		{
			localDeleteElement = true;
			styledEnd.addString(DELETE_OPEN_BRACKET);
			styledEnd.addString(DELETE_SLASH);
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()) || 
										startTagMixedMerged(getXmlElementNodeParent(),elem))
		{
			styledEnd.addString(MERGED_OPEN_BRACKET);
			styledEnd.addString(MERGED_SLASH);
			currentColor = COLOR_MERGED;
		}
		else
		{
			styledEnd.addString(OPEN_BRACKET);
			styledEnd.addString(SLASH);
			currentColor = Color.BLACK;
		}
		
		String prefix = elem.getNamespacePrefix();
	
		if ( prefix != null && prefix.length() > 0) {
			styledEnd.addString( new ElementPrefix( prefix));
			
			if (localInsertElement)
			{
				styledEnd.addString(INSERT_ELEMENT_COLON);
				
			}
			else if (localDeleteElement)
			{
				styledEnd.addString(DELETE_ELEMENT_COLON);
			}
			else if (merged || insideInsertMergeChain(getXmlElementNodeParent()) ||
											startTagMixedMerged(getXmlElementNodeParent(),elem))
			{
				styledEnd.addString(MERGED_ELEMENT_COLON);
			}
			else
			{
				styledEnd.addString(ELEMENT_COLON);
			}	
		}
		

		styledEnd.addString( new ElementName( elem.getName()));
		
		if (localInsertElement)
		{
			styledEnd.addString(INSERT_CLOSE_BRACKET);
			
		}
		else if (localDeleteElement)
		{
			styledEnd.addString(DELETE_CLOSE_BRACKET);
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()) || 
										startTagMixedMerged(getXmlElementNodeParent(),elem))
		{
			styledEnd.addString(MERGED_CLOSE_BRACKET);
		}
		else
		{
			styledEnd.addString(CLOSE_BRACKET);
		}
		
		current.addStyledElement( styledEnd);
		
		currentColor = Color.BLACK;
		return current;
	}

	private StyledElement formatAttribute( Attribute a) {
		StyledElement styledAttribute = new StyledElement();
		
		String name = a.getName();
		String qualifiedName = a.getQualifiedName();
		String value = a.getValue();
		
		boolean localInsertAttribute = false;
		boolean localDeleteAttribute = false;

		if (findAttribute(qualifiedName,insertAttributes) || insideInsertChain(this))
		{	
			localInsertAttribute = true;
			currentColor = COLOR_GREEN;
		}
		else if (findAttribute(qualifiedName,deleteAttributes) || insideDeleteChain(this))
		{	
			localDeleteAttribute = true;
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			currentColor = COLOR_MERGED;
		}
		else
		{
			currentColor = Color.BLACK;
		}
		
		String prefix = a.getNamespacePrefix();

		if ( prefix != null && prefix.length() > 0) {
			styledAttribute.addString( new AttributePrefix( prefix));
			
			if (localInsertAttribute)
			{	
				styledAttribute.addString( INSERT_ATTRIBUTE_COLON);
			}
			else if (localDeleteAttribute)
			{	
				styledAttribute.addString( DELETE_ATTRIBUTE_COLON);
			}
			else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
			{
				styledAttribute.addString( MERGED_ATTRIBUTE_COLON);
			}
			else
			{
				styledAttribute.addString( ATTRIBUTE_COLON);
			}
		}

		styledAttribute.addString( new AttributeName( name));
		
		if (localInsertAttribute)
		{	
			styledAttribute.addString(INSERT_ATTRIBUTE_ASIGN);
			styledAttribute.addString(INSERT_ATTRIBUTE_QUOTE);
		}
		else if (localDeleteAttribute)
		{	
			styledAttribute.addString(DELETE_ATTRIBUTE_ASIGN);
			styledAttribute.addString(DELETE_ATTRIBUTE_QUOTE);
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			styledAttribute.addString(MERGED_ATTRIBUTE_ASIGN);
			styledAttribute.addString(MERGED_ATTRIBUTE_QUOTE);
		}
		else
		{
			styledAttribute.addString(ATTRIBUTE_ASIGN);
			styledAttribute.addString(ATTRIBUTE_QUOTE);
		}
	
		String attrPreviousText = findUpdatedAttribute(qualifiedName,updateAttributes);
		if (attrPreviousText != null)
		{
			StyledString updatedValue = new AttributePreviousValue(attrPreviousText);
			updatedValue.setStrikeThrough(true);
			styledAttribute.addString(updatedValue);
			
			currentColor = Color.BLUE;
		}
		
		styledAttribute.addString( new AttributeValue( value));
		
		if (localInsertAttribute)
		{	
			styledAttribute.addString(INSERT_ATTRIBUTE_QUOTE);
		}
		else if (localDeleteAttribute)
		{	
			styledAttribute.addString(DELETE_ATTRIBUTE_QUOTE);
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			styledAttribute.addString(MERGED_ATTRIBUTE_QUOTE);
		}
		else
		{
			styledAttribute.addString(ATTRIBUTE_QUOTE);
		}
			
		currentColor = Color.BLACK;
		return styledAttribute;
	}
	
	private String findUpdatedAttribute(String name,Hashtable attrTable)
	{
		if (attrTable == null)
		{
			return null;
		}
		
		return (String)attrTable.get(name);
	}
	
	private boolean findAttribute(String name,Vector attributes)
	{
		if (attributes == null)
		{
			return false;
		}
		
		for (int i=0;i<attributes.size();i++)
		{
			String attrName = (String)attributes.get(i);
			if (name.equals(attrName))
			{
				return true;
			}
		}
		
		return false;
	}
		
	private StyledElement formatNamespace( Namespace n) {
		StyledElement styledNamespace = null;

		String prefix = n.getPrefix();
		String value = n.getText();
		String name = "xmlns:"+prefix;
		
		boolean localInsertNamespace = false;
		boolean localDeleteNamespace = false;
		
		if (findAttribute(name,insertAttributes) || insideInsertChain(this))
		{	
			localInsertNamespace = true;
			currentColor = COLOR_GREEN;
		}
		else if (findAttribute(name,deleteAttributes) || insideDeleteChain(this))
		{	
			localDeleteNamespace = true;
			currentColor = Color.RED;
		}
		else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
		{
			currentColor = COLOR_MERGED;
		}
		else
		{
			currentColor = Color.BLACK;
		}
		
		if ( value != null && value.length() > 0) {
			styledNamespace = new StyledElement();
			
			if (localInsertNamespace)
			{	
				styledNamespace.addString(INSERT_NAMESPACE_NAME);
			}
			else if (localDeleteNamespace)
			{	
				styledNamespace.addString(DELETE_NAMESPACE_NAME);
			}
			else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
			{
				styledNamespace.addString(MERGED_NAMESPACE_NAME);
			}
			else
			{
				styledNamespace.addString( NAMESPACE_NAME);
			}
		
			if ( prefix != null && prefix.length() > 0) {
				
				if (localInsertNamespace)
				{	
					styledNamespace.addString(INSERT_NAMESPACE_COLON);
				}
				else if (localDeleteNamespace)
				{	
					styledNamespace.addString(DELETE_NAMESPACE_COLON);
				}
				else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
				{
					styledNamespace.addString(MERGED_NAMESPACE_COLON);
				}
				else
				{
					styledNamespace.addString( NAMESPACE_COLON);
				}
				
				styledNamespace.addString( new NamespacePrefix( prefix));
			}

			if (localInsertNamespace)
			{	
				styledNamespace.addString(INSERT_NAMESPACE_ASIGN);
				styledNamespace.addString(INSERT_NAMESPACE_QUOTE);
			}
			else if (localDeleteNamespace)
			{	
				styledNamespace.addString(DELETE_NAMESPACE_ASIGN);
				styledNamespace.addString(DELETE_NAMESPACE_QUOTE);
			}
			else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
			{
				styledNamespace.addString(MERGED_NAMESPACE_ASIGN);
				styledNamespace.addString(MERGED_NAMESPACE_QUOTE);
			}
			else
			{
				styledNamespace.addString(NAMESPACE_ASIGN);
				styledNamespace.addString(NAMESPACE_QUOTE);
			}
			
			String namespacePrevious = findUpdatedAttribute(name,updateAttributes);
			if (namespacePrevious != null)
			{
				StyledString updatedValue = new AttributePreviousValue(namespacePrevious);
				updatedValue.setStrikeThrough(true);
				styledNamespace.addString(updatedValue);
				
				currentColor = Color.BLUE;
			}
			
			styledNamespace.addString( new NamespaceURI( value));
			
			if (localInsertNamespace)
			{	
				styledNamespace.addString(INSERT_NAMESPACE_QUOTE);
			}
			else if (localDeleteNamespace)
			{	
				styledNamespace.addString(DELETE_NAMESPACE_QUOTE);
			}
			else if (merged || insideInsertMergeChain(getXmlElementNodeParent()))
			{
				styledNamespace.addString(MERGED_NAMESPACE_QUOTE);
			}
			else
			{
				styledNamespace.addString(NAMESPACE_QUOTE);
			}
		}

		currentColor = Color.BLACK;
		return styledNamespace;
	}

	public class StyledElement {
		private Vector strings = null;
		
		public StyledElement() {
			strings = new Vector();
		}
	
		public void addString( StyledString string) {
			strings.addElement( string);
		}
		
		public int length() {
			int result = 0;
			
			for ( int i = 0; i < strings.size(); i++) {
				result += ((StyledString)strings.elementAt(i)).getText().length();
			}
			
			return result;
		}
		
		public Vector getStrings() {
			return strings;
		}
	}

	public class Line {
		private Vector strings = null;
		
		public Line() {
			strings = new Vector();
		}
		
		public void addStyledString( StyledString string) {
			strings.add( string);
		}
		
		public void addStyledElement( StyledElement element) {
			Vector strings = element.getStrings();

			for ( int i =0; i < strings.size(); i++) {
				addStyledString( (StyledString)strings.elementAt(i));
			}
		}

		public StyledString[] getStyledStrings() {
			StyledString[] ss = new StyledString[ strings.size()];
			
			for ( int i = 0; i < strings.size(); i++) {
				ss[i] = (StyledString)strings.elementAt(i);
			}

			return ss;
		}
		
		public int length() {
			int result = 0;
			
			for ( int i = 0; i < strings.size(); i++) {
				result += ((StyledString)strings.elementAt(i)).getText().length();
			}
			
			return result;
		}

		public String getText() {
			String result = "";
			
			for ( int i = 0; i < strings.size(); i++) {
				result += ((StyledString)strings.elementAt(i)).getText();
			}
			
			return result;
		}
	}
	
	// solves a problem in the Element that hasMixedContent returns true when the content 
	// has comment information.
	private static boolean isMixed( XElement element) {
		if ( element.hasMixedContent()) {
			boolean elementFound = false;
			boolean textFound = false;

			for ( int i = 0; i < element.nodeCount(); i++) {
				Node node = element.node( i);
				
				if ( node instanceof XElement) {
					elementFound = true;
				} else if ( (node instanceof Text) || (node instanceof CDATA) || (node instanceof Entity)) {
					if ( !isWhiteSpace( node)) {
						textFound = true;
					}
				}
				
				if ( textFound && elementFound) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	// does the element have mixed content, i.e content and child elements
	private static boolean isMixedElementAndContent( XElement element) {
		if ( element.hasMixedContent()) {
			boolean elementFound = false;
			boolean textFound = false;

			for ( int i = 0; i < element.nodeCount(); i++) {
				Node node = element.node( i);
				
				if ( node instanceof XElement) {
					elementFound = true;
				} else if ( node instanceof Text){
					if ( !isWhiteSpace( node)) {
						textFound = true;
					}
				}
				
				if ( textFound && elementFound) {
					return true;
				}
			}
		}
		
		return false;
	}

	private static boolean isWhiteSpace( Node node) {
		return node.getText().trim().length() == 0;
	}

	private static final StyledString COMMENT_START = new ControlString( "<!--");
	private static final StyledString COMMENT_END = new ControlString( "-->");
	private static final StyledString SPACE = new ControlString( " ");
	private static final StyledString TAB = new ControlString( "  ");
	
	private static final StyledString SLASH = new ControlString( "/");
	private static final StyledString INSERT_SLASH = new InsertControlString( "/");
	private static final StyledString DELETE_SLASH = new DeleteControlString( "/");
	private static final StyledString MERGED_SLASH = new MergedControlString( "/");
	
	private static final StyledString ATTRIBUTE_ASIGN = new ControlString( "=");
	private static final StyledString INSERT_ATTRIBUTE_ASIGN = new InsertControlString("=");
	private static final StyledString DELETE_ATTRIBUTE_ASIGN = new DeleteControlString("=");
	private static final StyledString MERGED_ATTRIBUTE_ASIGN = new MergedControlString("=");
	
	private static final StyledString ATTRIBUTE_QUOTE = new ControlString( "\"");
	private static final StyledString INSERT_ATTRIBUTE_QUOTE = new InsertControlString("\"");
	private static final StyledString DELETE_ATTRIBUTE_QUOTE = new DeleteControlString("\"");
	private static final StyledString MERGED_ATTRIBUTE_QUOTE = new MergedControlString("\"");
	
	private static final StyledString NAMESPACE_QUOTE = new ControlString( "\"");
	private static final StyledString INSERT_NAMESPACE_QUOTE = new InsertControlString("\"");
	private static final StyledString DELETE_NAMESPACE_QUOTE = new DeleteControlString("\"");
	private static final StyledString MERGED_NAMESPACE_QUOTE = new MergedControlString("\"");
	
	
	private static final StyledString ATTRIBUTE_COLON = new ControlString( ":");
	private static final StyledString INSERT_ATTRIBUTE_COLON = new InsertControlString(":");
	private static final StyledString DELETE_ATTRIBUTE_COLON = new DeleteControlString(":");
	private static final StyledString MERGED_ATTRIBUTE_COLON = new MergedControlString(":");
	
	
	private static final StyledString NAMESPACE_ASIGN = new ControlString( "=");
	private static final StyledString INSERT_NAMESPACE_ASIGN = new InsertControlString("=");
	private static final StyledString DELETE_NAMESPACE_ASIGN = new DeleteControlString("=");
	private static final StyledString MERGED_NAMESPACE_ASIGN = new MergedControlString("=");
	
	private static final StyledString NAMESPACE_COLON = new ControlString( ":");
	private static final StyledString INSERT_NAMESPACE_COLON = new InsertControlString(":");
	private static final StyledString DELETE_NAMESPACE_COLON = new DeleteControlString(":");
	private static final StyledString MERGED_NAMESPACE_COLON = new MergedControlString(":");
	
	//private static final StyledString NAMESPACE_NAME = new NamespaceName();
	private static final StyledString NAMESPACE_NAME = new ControlString("xmlns");
	private static final StyledString INSERT_NAMESPACE_NAME  = new InsertControlString("xmlns");
	private static final StyledString DELETE_NAMESPACE_NAME  = new DeleteControlString("xmlns");
	private static final StyledString MERGED_NAMESPACE_NAME  = new MergedControlString("xmlns");
	
	private static final StyledString ELEMENT_COLON = new ControlString( ":");
	private static final StyledString INSERT_ELEMENT_COLON = new InsertControlString( ":");
	private static final StyledString DELETE_ELEMENT_COLON = new DeleteControlString( ":");
	private static final StyledString MERGED_ELEMENT_COLON = new MergedControlString( ":");

	private static final StyledString OPEN_BRACKET = new ControlString( "<");
	private static final StyledString INSERT_OPEN_BRACKET = new InsertControlString( "<");
	private static final StyledString DELETE_OPEN_BRACKET = new DeleteControlString( "<");
	private static final StyledString MERGED_OPEN_BRACKET = new MergedControlString( "<");
	
	private static final StyledString CLOSE_BRACKET = new ControlString( ">");
	private static final StyledString INSERT_CLOSE_BRACKET = new InsertControlString( ">");
	private static final StyledString DELETE_CLOSE_BRACKET = new DeleteControlString( ">");
	private static final StyledString MERGED_CLOSE_BRACKET = new MergedControlString( ">");

	public class CommentText extends StyledString {
		public CommentText( String text) {
			super( text);
		}
		
		public Color getColor() {
			return TextPreferences.getFontType( TextPreferences.COMMENT).getColor();
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.COMMENT).getFont();
		}
	}
	
	public class CDATA_SECTION extends StyledString {
		
		private Color color = null;
		
		public CDATA_SECTION( String text) {
			super( text);
			this.color = currentColor;
		}
		
		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.CDATA).getFont();
		}
	}

	public class ElementValue extends StyledString {
		
		private Color color = null;
		
		public ElementValue( String text) {
			super( text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_VALUE).getFont();
		}
	}
	
	public class ElementPreviousValue extends StyledString {
		
		public ElementPreviousValue( String text) {
			super( text);
		}

		public Color getColor() {
			return Color.BLACK;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_VALUE).getFont();
		}
	}

	public class AttributeValue extends StyledString {
		
		private Color color = null;
		
		public AttributeValue( String text) {
			super(text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_VALUE).getFont();
		}
	}
	
	public class AttributePreviousValue extends StyledString {

		public AttributePreviousValue( String text) {
			super(text);
		}

		public Color getColor() {
			return Color.BLACK;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_VALUE).getFont();
		}
	}

	public class AttributePrefix extends StyledString {
		
		private Color color = null;
		
		public AttributePrefix( String text) {
			super( text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_PREFIX).getFont();
		}
	}

	public class AttributeName extends StyledString {
		
		private Color color = null;
		
		public AttributeName( String text) {
			super( text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ATTRIBUTE_NAME).getFont();
		}
	}

	public class NamespaceURI extends StyledString {
		
		private Color color = null;
		
		public NamespaceURI( String text) {
			super(text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE).getFont();
		}
	}

	public static class NamespaceName extends StyledString {
		
		
		
		public NamespaceName() {
			super( "xmlns");
		}

		public Color getColor() {
			return Color.BLACK;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_NAME).getFont();
		}
	}

	public static class ControlString extends StyledString {
		
		public ControlString( String text) {
			super( text);
		}

		public Color getColor() {
			return Color.BLACK;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.SPECIAL).getFont();
		}
	}
	
	public static class InsertControlString extends StyledString {
		
		public InsertControlString( String text) {
			super( text);
		}

		public Color getColor() {
			return COLOR_GREEN;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.SPECIAL).getFont();
		}
	}
	
	public static class DeleteControlString extends StyledString {
		
		public DeleteControlString( String text) {
			super( text);
		}

		public Color getColor() {
			return Color.RED;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.SPECIAL).getFont();
		}
	}
	
	public static class MergedControlString extends StyledString {
		
		public MergedControlString( String text) {
			super( text);
		}

		public Color getColor() {
			return COLOR_MERGED;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.SPECIAL).getFont();
		}
	}

	public class NamespacePrefix extends StyledString {
		
		private Color color = null;
		
		public NamespacePrefix( String text) {
			super( text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.NAMESPACE_PREFIX).getFont();
		}
	}

	public class ElementName extends StyledString {
		
		private Color color = null;
		
		public ElementName( String text) {
			super( text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_NAME).getFont();
		}
	}

	public class ElementPrefix extends StyledString {
		
		private Color color = null;
		
		public ElementPrefix( String text) {
			super( text);
			this.color = currentColor;
		}

		public Color getColor() {
			return this.color;
		}

		public Font getFont() {
			return TextPreferences.getFontType( TextPreferences.ELEMENT_PREFIX).getFont();
		}
	}
	
	public void cleanup() {
		Enumeration list = children();
		
		while ( list.hasMoreElements()) {
			((XmlElementNode)list.nextElement()).cleanup();
		}
		
		finalize();
	}
	
	protected void finalize() {
		element = null;
		lines = null;
	}
	
	public String toString() {
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}
} 
