/*
 * $Id: AttributeValue.java,v 1.1 2004/09/23 10:27:53 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

/**
 * A cross-grammar container for attribute value related information.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/09/23 10:27:53 $
 * @author Dogsbay
 */
public class AttributeValue {
	public static final int NORMAL_TYPE = 0;
	public static final int DEFAULT_TYPE = 1;
	public static final int FIXED_TYPE = 2;
	
	private String value = null;
	private int type = NORMAL_TYPE;

	public AttributeValue( String value, int type) {
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public boolean isFixed() {
		return type == FIXED_TYPE;
	}

	public boolean isDefault() {
		return type == DEFAULT_TYPE;
	}
	
	public String toString() {
		return value;
	}
} 
