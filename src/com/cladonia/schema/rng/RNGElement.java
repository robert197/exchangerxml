/*
 * $Id: RNGElement.java,v 1.3 2004/09/23 16:39:48 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.rng;

import java.util.Vector;

import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.ElementInformation;
import com.thaiopensource.relaxng.edit.NameNameClass;

/**
 * A container for a SchemaElement and possible attributes/child elements
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/23 16:39:48 $
 * @author Dogsbay
 */
public class RNGElement extends RNGPattern implements ElementInformation {
	private RNGElement parent	= null;
	private StringBuffer annotations	= null;
	private String type			= null;
	private String name			= null;
	private String prefix		= null;
	private String namespace	= null;
	private Vector attributes	= null;
	private Vector anyElements	= null;
	private Vector elements		= null;
	private Vector names 		= null;
	private boolean empty		= false;
	private boolean required	= false;

	public RNGElement( boolean required) {
		this( null, null, null, required);
		
		names = new Vector();
	}

	public RNGElement( NameNameClass qname, boolean required) {
		this( qname.getLocalName(), qname.getNamespaceUri(), qname.getPrefix(), required);
	}
	
	public RNGElement( String name, String namespace, String prefix, boolean required) {
		super();
		
		this.name = name;
		
		if ( namespace != NameNameClass.INHERIT_NS) {
			this.namespace = namespace;
		}

		this.prefix = prefix;
		this.required = required;
		
		annotations = new StringBuffer();

//		System.out.println( "["+hashCode()+"]RNGElement() ["+getQualifiedName()+"]");
	}

	public Vector getChildElements() {
		if ( elements == null) {
			elements = new Vector();
			
			Vector elems = getElements();
			
			for ( int i = 0; i < elems.size(); i++) {
				ElementInformation element = (ElementInformation)elems.elementAt(i);
				
				if ( !element.getName().equals( "*") && !contains( elements, element)) {
					elements.add( element);
				}
			}
		}

		return new Vector( elements);
	}
	
	private boolean contains( Vector elements, ElementInformation element) {
		for ( int i = 0; i < elements.size(); i++) {
			ElementInformation e = (ElementInformation)elements.elementAt(i);
			
			if ( element.getQualifiedName().equals( e.getQualifiedName())) {
				return true;
			}
		}
		
		return false;
	}
	
	public Vector getAttributes() {
//		System.out.println( "["+hashCode()+"]RNGElement.getAttributes() ["+getQualifiedName()+"]");

		if ( attributes == null) {
			attributes = new Vector();
			
			Vector attribs = super.getAttributes();
			
			for ( int i = 0; i < attribs.size(); i++) {
				AttributeInformation attribute = (AttributeInformation)attribs.elementAt(i);
				
				if ( !attribute.getName().equals( "*")) {
					attributes.add( attribute);
				}
			}
		} else {
			for ( int i = 0; i < attributes.size(); i++) {
				AttributeInformation attribute = (AttributeInformation)attributes.elementAt(i);
			}
		}

//		System.out.println( "<<< getAttributes() "+getQualifiedName());
		return new Vector( attributes);
	}

	public boolean isAbstract() {
		return name == null;
	}

	public RNGElement getSubstitute( NameNameClass name) {
		RNGElement element = new RNGElement( name, required);
		element.addReferences( references);
		element.addAttributes( attributes);
		element.addElements( elements);
		
		return element;
	}

//	public Vector getSubstitutes() {
//		Vector substitutes = new Vector();
//
//		for ( int i = 0; i < names.size(); i++) {
//			NameNameClass name = (NameNameClass)names.elementAt(i);
//			RNGElement element = new RNGElement( name, required);
//			element.addReferences( references);
//			element.addAttributes( attributes);
//			element.addElements( elements);
//			
//			substitutes.addElement( element);
//		}
//		
//		return substitutes;
//	}

//	public void addSubstitute( NameNameClass qname) {
//		names.addElement( qname);
//	}

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

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix( String prefix) {
		this.prefix = prefix;
	}

	public boolean isEmpty() {
		return empty;
	}

	public void setEmpty( boolean empty) {
		this.empty = empty;
	}

	public String getName() {
		return name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace( String namespace) {
		this.namespace = namespace;
	}

	public boolean isRequired() {
		return required;
	}

	public String getQualifiedName() {
		String qname = "";
		
		if ( prefix != null && prefix.trim().length() > 0) {
			qname = prefix+":";
		}
		
		return qname+getName();
	}
	
	public String getAnnotations() {
		return null;
	}

	public String getType() {
		return null;
	}

	public void setType( String type ) {
		this.type = type;
	}

	public String getParentName() {
		if ( parent != null) {
			return parent.getName();
		}
		
		return null;
	}

	public void merge( RNGElement element) {
		addElements( element.getElements());
		elements = null;
		
		Vector attribs = element.getAttributes();
		getAttributes();
		
		// merge the attributes, reset the required flag when 
		// an attribute does not exist in the current element.
		for ( int i = 0; i < attributes.size(); i++) {
			RNGAttribute attribute = (RNGAttribute)attributes.elementAt(i);
//			System.out.println( "Merging [1]: "+attribute.getQualifiedName());
			
			if ( attribute.isRequired()) {
				boolean found = false;

				for ( int j = 0; j < attribs.size(); j++) {
					RNGAttribute attrib = (RNGAttribute)attribs.elementAt(j);
					
					if ( attrib.getUniversalName().equals( attribute.getUniversalName())) {
						found = true;
						break;
					}
				}
				
				if ( !found) {
					attribute.setRequired( false);
				}
			}
		}

		for ( int i = 0; i < attribs.size(); i++) {
			RNGAttribute attrib = (RNGAttribute)attribs.elementAt(i);
//			System.out.println( "Merging: "+attrib.getQualifiedName());
			
			if ( attrib.isRequired()) {
				boolean found = false;

				for ( int j = 0; j < attributes.size(); j++) {
					RNGAttribute attribute = (RNGAttribute)attributes.elementAt(j);
					
					if ( attrib.getUniversalName().equals( attribute.getUniversalName())) {
						found = true;
						break;
					}
				}
				
				if ( !found) {
					attrib.setRequired( false);
				}
			}
		}

		addAttributes( attribs);
		attributes = null;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		boolean empty = true;
		
		buffer.append( "<");
		buffer.append( getUniversalName());
		
		Vector attributes = getAttributes();
		
//		if ( attributes.size() > 0) {
//			buffer.append( ">\n");
//			empty = false;
//		}
		
		for ( int i = 0; i < attributes.size(); i++) {
			buffer.append( "  ");
			buffer.append( attributes.elementAt(i).toString());
			buffer.append( "\n");
		}

		Vector elements = getElements();
		
		if ( elements.size() > 0 && empty) {
			buffer.append( ">\n");
			empty = false;
		}

		for ( int i = 0; i < elements.size(); i++) {
			RNGElement child = (RNGElement)elements.elementAt(i);
			buffer.append( "  <");
			buffer.append( child.getUniversalName());
			buffer.append( ">\n");
		}
		
		if ( empty) {
			buffer.append( "/>\n");
		} else {
			buffer.append( "</");
			buffer.append( getUniversalName());
			buffer.append( ">\n");
		}
		
		return buffer.toString();
	}
} 
