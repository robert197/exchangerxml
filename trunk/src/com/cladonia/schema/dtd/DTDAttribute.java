/*
 * $Id: DTDAttribute.java,v 1.3 2005/08/25 10:48:21 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.dtd;

import java.util.Vector;

import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.AttributeValue;

/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/25 10:48:21 $
 * @author Dogsbay
 */
public class DTDAttribute implements AttributeInformation {
	public static final String ATTRIBUTE_TYPE_CDATA 		= "CDATA";
	public static final String ATTRIBUTE_TYPE_ENTITY		= "ENTITY";
	public static final String ATTRIBUTE_TYPE_ENTITIES		= "ENTITIES";
	public static final String ATTRIBUTE_TYPE_ENUMERATION	= "ENUMERATION";
	public static final String ATTRIBUTE_TYPE_ID			= "ID";
	public static final String ATTRIBUTE_TYPE_IDREF			= "IDREF";
	public static final String ATTRIBUTE_TYPE_IDREFS		= "IDREFS";
	public static final String ATTRIBUTE_TYPE_NMTOKEN		= "NMTOKEN";
	public static final String ATTRIBUTE_TYPE_NMTOKENS 		= "NMTOKENS";

	public static final String ATTRIBUTE_DEFAULT_TYPE_FIXED		= "#FIXED";
	public static final String ATTRIBUTE_DEFAULT_TYPE_IMPLIED	= "#IMPLIED";
	public static final String ATTRIBUTE_DEFAULT_TYPE_REQUIRED 	= "#REQUIRED";

	private String name = null;
	private String prefix = null;
	private String type = null;
	private String[] enumeration = null;
	private String defaultType = null;
	private String defaultValue = null;
	private String namespace = null;
	private Vector values = null;
	private boolean required = false;
	
	public DTDAttribute( String qname, String type, String[] enumeration, String defaultType, String defaultValue) {
		this.type = type;
		this.enumeration = enumeration;
		this.defaultType	= defaultType;
		this.defaultValue	= defaultValue;
		
		int index = qname.indexOf( ':');
		
		if ( index != -1) {
			prefix = qname.substring( 0, index);
			name = qname.substring( index+1, qname.length());
		} else {
			prefix = "";
			name = qname;
		}
	}
	
	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace( String namespace) {
		this.namespace = namespace;
	}

	public String getQualifiedName() {
		String qname = "";
		
		if ( prefix != null && prefix.trim().length() > 0) {
			qname = prefix+":";
		}
		
		return qname+getName();
	}

	public String getUniversalName() {
		StringBuffer uname = new StringBuffer();
		if ( namespace != null && namespace.trim().length() > 0) {
			uname.append( "{");
			uname.append( namespace);
			uname.append( "}");
		}
		
		uname.append( name);
		
		return uname.toString();
	}

	public void setPrefix( String prefix) {
		this.prefix = prefix;
	}

	public boolean isRequired() {
		return defaultType == ATTRIBUTE_DEFAULT_TYPE_REQUIRED;
	}
	
	public String getType() {
		return type;
	}

	public String[] getEnumeration() {
		return enumeration;
	}

	public String getDefaultType() {
		return defaultType;
	}

	public String getDefaultValue() {
		return defaultValue;
	}
	
	public String getFixedValue() {
		if ( defaultType == ATTRIBUTE_DEFAULT_TYPE_FIXED) {
			return defaultValue;
		} else {
			return null;
		}
	}

	public boolean isNamespace() {
		if ( (prefix != null && prefix.equals( "xmlns")) || name.equals( "xmlns")) {
			return true;
		}
		
		return false;
	}
	
	public String getNamespacePrefix() {
		if ( prefix != null && prefix.equals( "xmlns")) {
			return name;
		}
		
		return null;
	}

	public Vector getValues() {
		if ( values == null) {
			values = new Vector();
			
			if ( enumeration != null) {
				for ( int i = 0; i < enumeration.length; i++) {
	
					AttributeValue value = null;
	
					if ( getFixedValue() != null && enumeration[i].equals( getFixedValue())) {
						value = new AttributeValue( enumeration[i], AttributeValue.FIXED_TYPE);
					} else if ( getDefaultValue() != null && enumeration[i].equals( getDefaultValue())) {
						value = new AttributeValue( enumeration[i], AttributeValue.DEFAULT_TYPE);
					} else {
						value = new AttributeValue( enumeration[i], AttributeValue.NORMAL_TYPE);
					}
	
					values.addElement( value);
				}
			}
			
			// Fixed value is not declared in the enumeration.
			if ( values.size() == 0 && getFixedValue() != null) {
				values.addElement( new AttributeValue( getFixedValue(), AttributeValue.FIXED_TYPE));
	
			// There might be a default value even though there is no enumeration.
			} else if ( values.size() == 0 && getDefaultValue() != null) {
				values.addElement( new AttributeValue( getDefaultValue(), AttributeValue.DEFAULT_TYPE));
			}
		}
		
		return new Vector( values);
	}

	// Note: In some case this attribute is disguised as a 
	// namespace and its default value is the namespace URI.
	public String getNamespaceURI() {
		return defaultValue;
	}
} 
