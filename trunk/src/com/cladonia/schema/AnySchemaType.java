/*
 * $Id: AnySchemaType.java,v 1.1 2004/03/25 18:37:18 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import org.exolab.castor.xml.schema.AnyType;

/**
 * The any schema type.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:18 $
 * @author Dogsbay
 */
public class AnySchemaType extends SchemaType {

	/**
	 * The constructor for the any schema type.
	 *
	 * @param type the any schema type for the attribute or element.
	 */
	public AnySchemaType( AnyType type) {
		super( type);
	}
	
	/**
	 * The constructor for the any schema type.
	 */
	public AnySchemaType() {
		super( null);
	}

	// defined in SchemaObject	
	public String getId() {
		return "";
	}

	/**
	 * Returns the name for this type.
	 *
	 * @return the name for this type.
	 */
	public String getName() {
		return "anyType";
	}

	/**
	 * Returns the final field for any type (null).<br/>
	 *
	 * @return the final field.
	 */
	public String getFinal() {
		return null;
	}
	
} 
