/*
 * $Id: XElement.java,v 1.3 2004/08/18 10:30:19 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.CDATA;
import org.dom4j.Element;
import org.dom4j.Entity;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Text;
import org.dom4j.tree.DefaultElement;

/**
 * The default implementation of the XElement interface.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/08/18 10:30:19 $
 * @author Dogsbay
 */
public class XElement extends DefaultElement {
//	private ExchangerDocument document = null;
	private int elementStart 	= -1;
	private int elementEnd 		= -1;
	private int contentStart	= -1;
	private int contentEnd		= -1;

	private boolean parsed = true;

	/**
	 * Constructs a default element with an initial name.
	 *
	 * @param name the unmutable name.
	 */
	public XElement( String name) {
		this( new QName( name), true);
	}

	/**
	 * Constructs a default element with an initial type.
	 *
	 * @param name the unmutable name.
	 */
	public XElement( String name, String namespace) {
		this( new QName( name, Namespace.get( namespace)), true);
	}

	/**
	 * Constructs a default element with an initial type.
	 *
	 * @param name the unmutable name.
	 */
	public XElement( String name, String namespace, String prefix) {
		this( new QName( name, Namespace.get( prefix, namespace)), true);
	}

	/**
	 * Constructs a default element with an initial type.
	 *
	 * @param name the unmutable name.
	 * @param name the unmutable namespace.
	 * @param parsed the element has been parsed from text.
	 */
	public XElement( String name, String namespace, boolean parsed) {
		this( new QName( name, Namespace.get( namespace)), parsed);
	}

	/**
	 * Constructs a default element with an initial type.
	 *
	 * @param name the unmutable name.
	 * @param name the unmutable namespace.
	 * @param parsed the element has been parsed from text.
	 */
	public XElement( String name, String namespace, String prefix, boolean parsed) {
		this( new QName( name, Namespace.get( prefix, namespace)), parsed);
	}

	/**
	 * Constructs a default element with a dom4j element.
	 *
	 * @param the dom4j element.
	 */
	public XElement( QName name) {
		this( name, true);
	}

	/**
	 * Constructs a default element with a dom4j element.
	 *
	 * @param the dom4j element.
	 */
	public XElement( QName name, boolean parsed) {
		super( name);
		
		this.parsed = parsed;
//		outputFormat.setIndent("  ");
		//outputFormat.setNewlines( false);
	}

	/**
	 * The element has been parsed from text.
	 *
	 * @return false if the element has been created programmaticly.
	 */
	public boolean isParsed() {
		return parsed;
	}

	/**
	 * Adds an attribute to the list of attributes or overwrites 
	 * the attribute if the attribute already exists.
	 *
	 * @param attribute the attribute.
	 */
	public void putAttribute( XAttribute attribute) {
		super.add( attribute);
	}

	/**
	 * Returns an array of attributes.
	 *
	 * @return an array of attributes.
	 */
	public XAttribute[] getAttributes() {
		List attributes = super.attributes();
		Iterator iterator = attributes.iterator();
		
		XAttribute[] result = new XAttribute[ attributes.size()];
		
		for ( int i = 0; iterator.hasNext(); i++) {
			result[i] = (XAttribute)iterator.next();
		}
		
		return result;
	}

	/**
	 * Returns the value of the attribute with the name.
	 *
	 * @param name the name of the attribute.
	 *
	 * @return the attribute value.
	 */
	public String getAttribute( String name) {
		return super.attributeValue( name);
	}

	/**
	 * Returns the first element for the given name.
	 *
	 * @param name the name of the element.
	 *
	 * @return the element.
	 */
	public XElement getElement( String name) {
		return (XElement)super.element( name);
	}
	
	/**
	 * Returns all the child elements of this element.
	 *
	 * @return an array of elements.
	 */
	public XElement[] getElements() {
		return convert( super.elements());
	}

	/**
	 * Returns all the child elements with a given name
	 * for this element.
	 *
	 * @param name the name of the element.
	 *
	 * @return an array of elements.
	 */
	public XElement[] getElements( String name) {
		return convert( super.elements( name));
	}

	/**
	 * Returns the namespace for this element.
	 *
	 * @return a namespace representation.
	 */
	public String namespace() {
		return super.getNamespaceURI();
	}

	/**
	 * Returns the universal name for this element.
	 * The name is in the form:
	 * {namespace}localname
	 *
	 * @return a universal name representation.
	 */
	public String getUniversalName() {
		String result = "";
		String namespace = getNamespaceURI();
		
		if ( namespace != null && namespace.length() > 0) {
			result = "{" +namespace+ "}";
		}
		
		return result+getName();	
	}

	/**
	 * Returns the (local)name for this element.
	 *
	 * @return a name for the element.
	 */
	public String getName() {
		return super.getName();
	}

	/**
	 * A check wether this element is the root element.
	 *
	 * @return true when the element is the root element.
	 */
	public boolean isRoot() {
		//tcurley  09-05-08 try to check if its a root element is returning false for stylesheet for xslt schema instance
		boolean localDebug = true;
		return(super.isRootElement());
	}

	/**
	 * Returns the parent of this element.
	 *
	 * @return the parent element.
	 */
	public XElement parent() {
		return (XElement)super.getParent();
	}
	
	/**
	 * Gets the text value of this element.
	 *
	 * @return the value of this element.
	 */
	public String getValue() {
		return super.getText();
	}

	/**
	 * Sets the text value of this element.
	 *
	 * @param the value of this element.
	 */
	public void setValue( String text) {
		super.setText( text);
	}
	
	/**
	 * Adds a child element to this element.
	 *
	 * @param the child element.
	 */
	public void add( XElement child) {
		super.add( (Element)child);
	}

	/**
	 * Removes a child element from this element and all 
	 * the empty text nodes before it.
	 *
	 * @param the child element.
	 */
	public void remove( XElement child) {
		Vector children = getChildren();
		
		Vector textNodes = new Vector();
		for ( int i = 0; i < children.size(); i++) {
			Node c = (Node)children.elementAt(i);
			
			if ( c instanceof Text) {
				textNodes.addElement( c);
			} else if ( c instanceof XElement) {
				if ( c == child) {
					super.remove( (Element)child);
					
					for ( int j = textNodes.size(); j > 0; j--) {
						Text node = (Text)textNodes.elementAt(j-1);
						String text = node.getText().trim();
						
						if ( text.length() > 0) {
							break;
						} else {
							super.remove( (Text)node);
						}
					}
					
					return;
				}

				textNodes.removeAllElements();
			}
		}
	}
	
	/**
	 * Returns an XPath result, uniquely identifying this element.
	 *
	 * @return the XPath result identifying this element.
	 */
	public String path() {
		return super.getUniquePath();
	}

	/**
	 * Sets the document for this element.
	 *
	 * @param the document.
	 */
//	public void document( ExchangerDocument document) {
//		this.document = document;
//	}

	/**
	 * Search through the tree for the document.
	 */
//	private ExchangerDocument findDocument( XElement element) {
//		if ( element != null) {
//			document = element.document();
//			
//			if ( document == null) {
//				document = findDocument( (XElement)element.parent());
//			}
//		}
//		
//		return document;
//	}

	/**
	 * Returns the document for this element.
	 * Will return null if the element does not 
	 * have a document.
	 *
	 * @return the document.
	 */
//	public ExchangerDocument document() {
//		if ( document == null) {
//			document = findDocument( (XElement)this.parent());
//		} 
//		
//		return document;
//	}

	/**
	 * Returns the list nodes for this element.
	 *
	 * @return the children.
	 */
	public Vector getChildren() {
		Vector children =  new Vector();
		
		List attributes = super.attributes();
		Iterator iterator = attributes.iterator();
		
		while ( iterator.hasNext()) {
			children.addElement( iterator.next());
		}
		
		for ( int i = 0; i < nodeCount(); i++) {
			children.addElement( node(i));
		}
		
		return children;
	}

	/**
	 * Remove All child nodes from this node.
	 */
	public void removeAllChildren() {
		Vector children = getChildren();

		for ( int i = 0; i < children.size(); i++) {
			Object child = (Node)children.elementAt(i);
			
			if ( child instanceof Node) {
				remove( (Node)child);
			} else if ( child instanceof XAttribute) {
				remove( (XAttribute)child);
			}
		}
	}

	// solves a problem in the Element that hasMixedContent returns true when the content 
	// has comment information.
	private boolean isMixed() {
		if ( super.hasMixedContent()) {
			boolean elementFound = false;
			boolean textFound = false;

			for ( int i = 0; i < nodeCount(); i++) {
				Node node = node( i);
				
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

	public boolean isWhiteSpaceText() {
		return getText().trim().length() == 0;
	}

	private boolean isWhiteSpace( Node node) {
		return node.getText().trim().length() == 0;
	}

	/**
	 * Adds the child node to the element.
	 *
	 * @param child the child node
	 */
	public void addChild( Object child) {
		if ( child instanceof Node) {
			add( (Node)child);
		} else if ( child instanceof XAttribute) {
			add( (XAttribute)child);
		}
	}

	// See super...
	public String asXML() {
		StringWriter writer = new StringWriter();
		
		try {
			write( writer);
		} catch( Exception e) {
			// Should never happen...
			e.printStackTrace();
		}

		return writer.toString();
	}
	
	/**
	 * Returns the contents of this element as an XML 
	 * formatted String.
	 *
	 * @return the XML formatted String.
	 */
	public String toString() {
		return asXML();
	}

	/**
	 * Insert the element as the nth element.
	 *
	 * @param index the element index.
	 * @param e the element to insert.
	 */
	public void insert( int index, XElement e) {
		List list = content();

		int nodeCount = 0;
		int elementCount = 0;
		//List list = elements();
		
		while ( index > elementCount) {
			Object node = list.get( nodeCount);

			if ( node instanceof XElement) {
				elementCount++;
			}
			
			nodeCount++;
		}
		
		if ( nodeCount < list.size()) {
			list.add( nodeCount, e);
		} else {
			list.add( e);
		}
	}

	/**
	 * Returns the start position in the text of the element.
	 * returns -1 if the element has not been written yet.
	 *
	 * @return the start postion of the element.
	 */
	public int getElementStartPosition() {
		return elementStart;
	}

	/**
	 * Sets the start position in the text of the element.
	 *
	 * @return the start postion of the element.
	 */
	public void setElementStartPosition( int pos) {
		elementStart = pos;
	}

	/**
	 * Returns the end position in the text of the element.
	 * returns -1 if the element has not been written yet.
	 *
	 * @return the end postion of the element.
	 */
	public int getElementEndPosition() {
		return elementEnd;
	}

	/**
	 * Sets the end position in the text of the element.
	 *
	 * @return pos the end postion of the element.
	 */
	public void setElementEndPosition( int pos) {
		elementEnd = pos;
	}

	/**
	 * Returns the start position in the text of the element content.
	 * returns -1 if the element has not been written yet.
	 *
	 * @return the start postion of the element content.
	 */
	public int getContentStartPosition() {
		return contentStart;
	}

	/**
	 * Sets the start position in the text of the element content.
	 *
	 * @param pos the start postion of the element content.
	 */
	public void setContentStartPosition( int pos) {
		contentStart = pos;
	}

	/**
	 * Returns the end position in the text of the element content.
	 * returns -1 if the element has not been written yet.
	 *
	 * @return the end postion of the element content.
	 */
	public int getContentEndPosition() {
		return contentEnd;
	}

	/**
	 * Sets the end position in the text of the element content.
	 *
	 * @param pos the end postion of the element content.
	 */
	public void setContentEndPosition( int pos) {
		contentEnd = pos;
	}

	private XElement[] convert( List list) {
		XElement[] result = new XElement[ list.size()];
		
		if ( list != null && list.size() > 0) {
			Iterator elements = list.iterator();
			
			for ( int i = 0; i < list.size(); i++) {
				result[i] = (XElement)elements.next();
			}
		}
		
		return result;
	}
	
	public void cleanup() {
		Vector children = getChildren();
		
		for ( int i = 0; i < children.size(); i++) {
			Node c = (Node)children.elementAt(i);
			
			if ( c instanceof XAttribute) {
				super.remove( (XAttribute)c);
			} else if ( c instanceof XElement) {
				((XElement)c).cleanup();
				super.remove( (XElement)c);
			} else {
				super.remove( c);
			}
		}
	}
	
	public boolean hasChildElements()
	{
		// checks to see if this element has child elements
		if (this.elements().size()  <= 0)
		{
			return false;
		}
		
		return true;
	}
	
	@Override
	public List content() {
		// TODO Auto-generated method stub
		return super.content();
	}
	
	@Override
	public List elements() {
		// TODO Auto-generated method stub
		return super.elements();
	}
} 
