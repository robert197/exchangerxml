/*
 * $Id: RNGReference.java,v 1.2 2004/09/23 10:49:47 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.rng;

import java.util.Vector;

/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:49:47 $
 * @author Dogsbay
 */
public class RNGReference {
	private Vector elements 	= null;
	private Vector attributes	= null;

	private RNGDefinition definition = null;
	private String name = null;
	private boolean required = false;
	private boolean external = false;
	
	public RNGReference( String name, boolean required) {
		this( name, required, false);
	}

	public RNGReference( String name, boolean required, boolean external) {
		this.name		= name;
		this.required	= required;
		this.external	= external;
	}
	
	public String getName() {
		return name;
	}

	public boolean isRequired() {
		return required;
	}

	public boolean isExternal() {
		return external;
	}

	public Vector getElements() {
		if ( elements == null && isResolved()) {
			elements = new Vector();
			
			Vector elems = definition.getElements();
			
			for ( int i = 0; i < elems.size(); i++) {
				RNGElement element = (RNGElement)elems.elementAt(i);
//				RNGElement childElement = new RNGElement( element.getName(), element.getNamespace(), element.getPrefix(), element.isRequired() && required);
				elements.addElement( element);
			}
		}

		return elements;
	}

	public Vector getAttributes() {
//		System.out.println( "RNGReference.getAttributes() ["+getName()+"]");
		if ( attributes == null && isResolved()) {
			attributes = new Vector();
			
			Vector attribs = definition.getAttributes();
			
			for ( int i = 0; i < attribs.size(); i++) {
				RNGAttribute attribute = (RNGAttribute)attribs.elementAt(i);
//				RNGAttribute childAttribute = new RNGAttribute( attribute.getName(), attribute.getNamespace(), attribute.getPrefix(), attribute.getType(), attribute.getEnumeration(), attribute.isRequired() && required);
				attributes.addElement( attribute);
			}
		}

		return attributes;
	}

	public void setDefinition( RNGDefinition definition) {
		this.definition = definition;
	}
	
	public boolean isResolved() {
		return definition != null;
	}
} 
