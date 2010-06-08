/*
 * $Id: NamespaceProperties.java,v 1.1 2004/03/25 18:54:01 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the properties for a namespace.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:54:01 $
 * @author Dogsbay
 */
public class NamespaceProperties extends Properties {
	
	public static final String NAMESPACE	= "additional-namespace";

	private static final String URI		= "uri";
	private static final String PREFIX	= "prefix";

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the namespace.
	 */
	public NamespaceProperties( XElement element) {
		super( element);
	}

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param props the higher level properties object.
	 */
	public NamespaceProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for the namespace properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public NamespaceProperties( NamespaceProperties original) {
		super( new XElement( NAMESPACE));
		
		setURI( original.getURI());
		setPrefix( original.getPrefix());
	}

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param uri the namespace uri.
	 * @param prefix the namespace prefix.
	 */
	public NamespaceProperties( String uri, String prefix) {
		super( new XElement( NAMESPACE));
		
		setURI( uri);
		setPrefix( prefix);
	}

	/**
	 * Constructor for a new namespace properties object.
	 */
	public NamespaceProperties() {
		super( new XElement( NAMESPACE));
	}

	/**
	 * Return the URI.
	 *
	 * @return the URI.
	 */
	public String getURI() {
		return getText( URI);
	}

	/**
	 * Set the namespace URI.
	 *
	 * @param uri the namespace URI.
	 */
	public void setURI( String uri) {
		set( URI, uri);
	}

	/**
	 * Return the namespace prefix.
	 *
	 * @return the namespace prefix.
	 */
	public String getPrefix() {
		return getText( PREFIX);
	}

	/**
	 * Set the namespace prefix.
	 *
	 * @param prefix the namespace prefix.
	 */
	public void setPrefix( String prefix) {
		set( PREFIX, prefix);
	}
} 
