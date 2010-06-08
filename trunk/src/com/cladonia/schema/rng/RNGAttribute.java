/*
 * $Id: RNGAttribute.java,v 1.2 2004/09/23 10:49:47 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.rng;

import java.util.Vector;

import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.AttributeValue;
import com.thaiopensource.relaxng.edit.NameNameClass;

/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:49:47 $
 * @author Dogsbay
 */
public class RNGAttribute implements AttributeInformation {
	private static Vector booleanValues = null;
	
	private String name = null;
	private String prefix = null;
	private String namespace = null;

	private String type = null;
	private Vector values = null;
	private boolean required = false;
	
	public RNGAttribute( boolean required) {
		this( null, null, null, null, null, required);
	}

	public RNGAttribute( NameNameClass qname, String type, Vector values, boolean required) {
		this( qname.getLocalName(), qname.getNamespaceUri(), qname.getPrefix(), type, values, required);
	}

	public RNGAttribute( String name, String namespace, String prefix, String type, Vector values, boolean required) {
		if ( values == null) {
			this.values = new Vector();
		} else {
			this.values	= values;
		}

		this.type		= type;
		this.required	= required;
		
		this.name = name;
		this.prefix = prefix;

		if ( namespace != NameNameClass.INHERIT_NS) {
			this.namespace = namespace;
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
		return required;
	}
	
	public void setRequired( boolean required) {
		this.required = required;
	}

	public String getType() {
		return type;
	}

	public Vector getValues() {
		if ( values.size() == 0 && "boolean".equals( type)) {
			return getBooleanValues();
			
		} else if ( values.size() == 1) {
			AttributeValue value = (AttributeValue)values.elementAt( 0);
			
			if ( !value.isFixed()) {
				values.removeElementAt( 0);
				values.addElement( new AttributeValue( value.getValue(), AttributeValue.FIXED_TYPE));
			}
		}

		return values;
	}

	public void addValue( String value) {
		values.addElement( new AttributeValue( value, AttributeValue.NORMAL_TYPE));
	}

	public String toString() {
		String string = "= "+getUniversalName();
		
		if ( isRequired()) {
			string = string+" [!]";
		}
		
		return string;
	}
	
	public RNGAttribute getSubstitute( NameNameClass name) {
		RNGAttribute attribute = new RNGAttribute( name, type, values, required);

		return attribute;
	}
	
	public boolean isAbstract() {
		return name == null;
	}

	public void merge( RNGAttribute attribute) {
		if ( isRequired() != attribute.isRequired()) {
			required = false;
		}
	}
	
	private static Vector getBooleanValues() {
		if ( booleanValues == null) {
			booleanValues = new Vector();
			
			booleanValues.addElement( new AttributeValue( "false", AttributeValue.NORMAL_TYPE));
			booleanValues.addElement( new AttributeValue( "true", AttributeValue.NORMAL_TYPE));
		}

		return booleanValues;
	}

//	public String getDefaultType() {
//		return defaultType;
//	}
//
//	public String getDefaultValue() {
//		return defaultValue;
//	}
} 
