/*
 * $Id: SchemaDocument.java,v 1.2 2004/09/23 10:50:00 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.net.URL;
import java.util.Vector;

import com.cladonia.xml.XMLGrammar;

/**
 * A cross-grammar container for element related information.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:50:00 $
 * @author Dogsbay
 */
public interface SchemaDocument {
	public static final int TYPE_XSD = XMLGrammar.TYPE_XSD;
	public static final int TYPE_DTD = XMLGrammar.TYPE_DTD;
	public static final int TYPE_RNG = XMLGrammar.TYPE_RNG;
	public static final int TYPE_RNC = XMLGrammar.TYPE_RNC;

	/**
	 * Get all the elements defined by this schema.
	 *
	 * @return a list of ElementInformation objects.
	 */
	public Vector getElements();

	/**
	 * Get all the any elements defined by this schema.
	 *
	 * @return a list of any elements.
	 */
	public Vector getAnyElements();

	/**
	 * Get all the global elements defined by this schema.
	 *
	 * @return a list of ElementInformation objects.
	 */
	public Vector getGlobalElements();

	/**
	 * Updates all prefixes based on the namespace 
	 * declarations supplied.
	 *
	 * @param declarations a list of namespace declarations.
	 */
	public void updatePrefixes( Vector declarations);

	/**
	 * Get the url for this schema document.
	 *
	 * @return the url.
	 */
	public URL getURL();

	/**
	 * Get this document's type.
	 *
	 * @return the type of this document.
	 */
	public int getType();
} 
