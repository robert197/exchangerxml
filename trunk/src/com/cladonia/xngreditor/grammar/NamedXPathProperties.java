/*
 * $Id: NamedXPathProperties.java,v 1.2 2004/10/26 16:04:20 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import java.util.Date;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the properties for a namespace.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/26 16:04:20 $
 * @author Dogsbay
 */
public class NamedXPathProperties extends Properties {
	
	public static final String NAMED_XPATH	= "named-xpath";

	private static final String ID						= "id";
	private static final String NAME					= "name";
	private static final String XPATH					= "xpath";
	private static final String SHOW_ELEMENT_NAMES		= "show-element-names";
	private static final String SHOW_ELEMENT_CONTENT	= "show-element-content";
	private static final String SHOW_ATTRIBUTES			= "show-attributes";
	private static final String SHOW_ATTRIBUTE_NAMES	= "show-attribute-names";

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the namespace.
	 */
	public NamedXPathProperties( XElement element) {
		super( element);
	}

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param props the higher level properties object.
	 */
	public NamedXPathProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for the namespace properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public NamedXPathProperties( NamedXPathProperties original) {
		super( new XElement( NAMED_XPATH));
		
		setName( original.getName());
		setXPath( original.getXPath());
		setShowElementNames( original.showElementNames());
		setShowElementContent( original.showElementContent());
		setShowAttributeNames( original.showAttributeNames());
		setShowAttributes( original.showAttributes());
	}

	/**
	 * Constructor for a new namespace properties object.
	 */
	public NamedXPathProperties() {
		super( new XElement( NAMED_XPATH));
	}

	/**
	 * Return the Name.
	 *
	 * @return the Name.
	 */
	public String getName() {
		return getText( NAME);
	}
	public void setName( String name) {
		set( NAME, name);
	}

	/**
	 * Return the XPath.
	 *
	 * @return the XPath.
	 */
	public String getXPath() {
		return getText( XPATH);
	}
	public void setXPath( String xpath) {
		set( XPATH, xpath);
	}

	/**
	 * Return wether the element names should be visible.
	 *
	 * @return wether the element names should be visible.
	 */
	public boolean showElementNames() {
		return getBoolean( SHOW_ELEMENT_NAMES, true);
	}
	public void setShowElementNames( boolean show) {
		set( SHOW_ELEMENT_NAMES, show);
	}

	/**
	 * Return wether the element content should be visible.
	 *
	 * @return wether the element content should be visible.
	 */
	public boolean showElementContent() {
		return getBoolean( SHOW_ELEMENT_CONTENT, true);
	}
	public void setShowElementContent( boolean show) {
		set( SHOW_ELEMENT_CONTENT, show);
	}

	/**
	 * Return wether the attribute names should be visible.
	 *
	 * @return wether the attribute names should be visible.
	 */
	public boolean showAttributeNames() {
		return getBoolean( SHOW_ATTRIBUTE_NAMES, true);
	}
	public void setShowAttributeNames( boolean show) {
		set( SHOW_ATTRIBUTE_NAMES, show);
	}

	/**
	 * Return wether the attributes should be visible.
	 *
	 * @return wether the attributes should be visible.
	 */
	public boolean showAttributes() {
		return getBoolean( SHOW_ATTRIBUTES, true);
	}
	public void setShowAttributes( boolean show) {
		set( SHOW_ATTRIBUTES, show);
	}

	public String toString() {
	    return(this.getName());
	}
	
	/**
	 * Set the name.
	 *
	 * @param name the scenario name.
	 */
	private void setID( String id) {
		set( ID, id);
	}

	/**
	 * Return the ID.
	 *
	 * @return the identifier of this scenario.
	 */
	public String getID() {
		String result = getText( ID);
		
		if ( result == null || result.length() < 2) {
			Date date = new Date();
			set( ID, "NX"+date.getTime()+getName().hashCode());
			
			result = getText( ID);
		}

		return result;
	}
} 
