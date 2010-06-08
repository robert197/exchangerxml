/*
 * $Id: TagCompletionProperties.java,v 1.2 2004/10/08 14:12:36 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLGrammar;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xngreditor.URLUtilities;

/**
 * Handles the properties for a namespace.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/08 14:12:36 $
 * @author Dogsbay
 */
public class TagCompletionProperties extends Properties {
	
	public static final String TAGCOMPLETION	= "tag-completion";

	private static final String TYPE		= "type";
	private static final String LOCATION	= "location";

	/**
	 * Constructor for the tag-completion properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the tag-completion.
	 */
	public TagCompletionProperties( XElement element) {
		super( element);
	}

	/**
	 * Constructor for the tag-completion properties.
	 *
	 * @param props the higher level properties object.
	 */
	public TagCompletionProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for the tag-completion properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public TagCompletionProperties( TagCompletionProperties original) {
		super( new XElement( TAGCOMPLETION));
		
		setLocation( original.getLocation());
		setType( original.getType());
	}

	/**
	 * Constructor for the tag-completion properties.
	 *
	 * @param location the tag-completion location.
	 * @param type the tag-completion type.
	 */
	public TagCompletionProperties( String location, int type) {
		super( new XElement( TAGCOMPLETION));
		
		setLocation( location);
		setType( type);
	}

	/**
	 * Constructor for a new tag-completion properties object.
	 */
	public TagCompletionProperties() {
		super( new XElement( TAGCOMPLETION));
	}

	/**
	 * Return the Location.
	 *
	 * @return the Location.
	 */
	public String getLocation() {
		return getText( LOCATION);
	}

	/**
	 * Set the Location.
	 *
	 * @param location the tag-completion location.
	 */
	public void setLocation( String location) {
		set( LOCATION, location);
	}

	/**
	 * Return the tag-completion type.
	 *
	 * @return the tag-completion type.
	 */
	public int getType() {
		return getInteger( TYPE, XMLGrammar.TYPE_XSD);
	}

	/**
	 * Set the tag-completion type.
	 *
	 * @param type the tag-completion type.
	 */
	public void setType( int type) {
		set( TYPE, type);
	}
	
	/**
	 * Set the toString method 
	 */
	public String toString() {
	    return(URLUtilities.getFileName( getLocation()));
	}
} 
