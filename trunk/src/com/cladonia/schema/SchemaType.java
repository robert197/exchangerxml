/*
 * $Id: SchemaType.java,v 1.2 2005/07/12 12:01:02 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import org.exolab.castor.xml.schema.XMLType;

/**
 * The type of the element/attribute.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/07/12 12:01:02 $
 * @author Dogsbay
 */
public class SchemaType implements SchemaObject {
	private XMLType type	= null;
	private SchemaType base	= null;

	/**
	 * The constructor for the element node.
	 *
	 * @param parent this types (parent) base type.
	 */
	public SchemaType( XMLType type) {
		this.type = type;
	}

	// defined in SchemaObject	
	public String getId() {
		return type.getId();
	}

	/**
	 * Returns the name for this type.
	 *
	 * @return the name for this type.
	 */
	public String getName() {
		return type.getName();
	}

	// Return the type...
	public XMLType getType() {
		return type;
	}
	
	

	/**
	 * Sets the base type for this type.
	 *
	 * @param type the base type.
	 */
	public void setBase( SchemaType type) {
		base = type;
	}

	/**
	 * Returns the base type for this type.
	 *
	 * @return the base type.
	 */
	public SchemaType getBase() {
		return base;
	}
} 
