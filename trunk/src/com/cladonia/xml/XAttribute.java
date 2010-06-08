/*
 * $Id: XAttribute.java,v 1.2 2004/07/02 10:06:04 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.tree.DefaultAttribute;

/**
 * The default implementation of the XElement interface.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/07/02 10:06:04 $
 * @author Dogsbay
 */
public class XAttribute extends DefaultAttribute {
	private int attributeStart 	= -1;
	private int attributeEnd 	= -1;

	/**
	 * Constructs a default element with an initial name.
	 *
	 * @param name the unmutable name.
	 */
	public XAttribute( String name) {
		this( new QName( name), null);
	}

	/**
	 * Constructs a default element with an initial name.
	 *
	 * @param name the unmutable name.
	 */
	public XAttribute( String name, String value) {
		this( new QName( name), value);
	}

	/**
	 * Constructs a default element with an initial type.
	 *
	 * @param name the unmutable name.
	 */
	public XAttribute( String name, String namespace, String value) {
		this( new QName( name, Namespace.get( namespace)), value);
	}

	/**
	 * Constructs a default attribute with a dom4j element.
	 *
	 * @param the dom4j element.
	 */
	public XAttribute( QName name, String value) {
		super( name, value);
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
	 * Returns the start position in the text of the attribute.
	 * returns -1 if the attribute has not been written yet.
	 *
	 * @return the start postion of the attribute.
	 */
	public int getAttributeStartPosition() {
		return attributeStart;
	}

	/**
	 * Sets the start position in the text of the attribute.
	 *
	 * @return the start postion of the attribute.
	 */
	public void setAttributeStartPosition( int pos) {
		attributeStart = pos;
	}

	/**
	 * Returns the end position in the text of the attribute.
	 * returns -1 if the attribute has not been written yet.
	 *
	 * @return the end postion of the attribute.
	 */
	public int getAttributeEndPosition() {
		return attributeEnd;
	}

	/**
	 * Sets the end position in the text of the attribute.
	 *
	 * @return pos the end postion of the attribute.
	 */
	public void setAttributeEndPosition( int pos) {
		attributeEnd = pos;
	}
	
    public String getPath( Element context) {
        Element parent = getParent();

        return ( parent != null && parent != context ) 
            ? parent.getPath( context ) + "/@" + getQualifiedName() : "@" + getQualifiedName();
    }
} 
