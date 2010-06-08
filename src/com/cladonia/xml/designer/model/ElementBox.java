/*
 * $Id: ElementBox.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.SchemaElement;
import com.cladonia.xml.XElement;

/**
 * The box containing an element.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public class ElementBox {
	private static final boolean DEBUG = false;
	
	private XElement element = null;
	private SchemaElement type = null;
	private boolean optional = true;
	private ElementList parent = null;

	/**
	 * Constructs the element-box for an element of the type supplied.
	 *
	 * @param parent the parent element list.
	 * @param type the type of the element that the box can contain.
	 * @param optional wether the element is required or optional
	 */
	public ElementBox( ElementList parent, SchemaElement type, boolean optional) {
		if (DEBUG) System.out.println( "ElementBox( "+type+", "+optional+")");

		this.parent 	= parent;
		this.type 		= type;
		this.optional 	= optional;
	}
	
	/**
	 * Returns the element type.
	 *
	 * @return the schema type of the element.
	 */
	public SchemaElement getType() {
		return type;
	}

	/**
	 * Returns wether this box has an element.
	 *
	 * @return false if there is no element in this box.
	 */
	public boolean isEmpty() {
		return element == null;
	}

	/**
	 * Checks wether this box has a parent that is a choice model.
	 *
	 * @return false if this element does not have a choice model as its parent.
	 */
	public boolean isChoice() {
		Object p = parent.getParent();
		
		while ( p != null) {
			if ( p instanceof ChoiceModelBox) {
				return true;
			} else if ( p instanceof ModelBox) {
				p = ((ModelBox)p).getParent();
			} else if ( p instanceof ParticleList) {
				p = ((ParticleList)p).getParent();
			} else { // should not happen
				p = null;
			}
		}
	
		return false;
	}

	/**
	 * Returns the element.
	 *
	 * @return the element.
	 */
	public XElement get() {
		return element;
	}

	/**
	 * Sets an element in the box.
	 *
	 * @param element the element to set.
	 */
	public void set( XElement element) {
		if (DEBUG) System.out.println( "ElementBox.set( "+element+")");

		this.element = element;
	}

	/**
	 * Returns true when this box has a type that is the same as the 
	 * element.
	 *
	 * @param element the element to test the type for.
	 *
	 * @return true when the element has the same type.
	 */
	public boolean isSameType( XElement element) {
		if (DEBUG) System.out.println( "ElementBox.isSameType( "+element+")");
		String name = element.getName();
		
		if ( isAbstract()) {
// SUBS		Vector list = type.getSubstitutes();
			Vector list = type.getSubstituteElements();
			
			for ( int i = 0; i < list.size(); i++) {
				SchemaElement e = (SchemaElement)list.elementAt(i);
				
				if ( e.getName().equals( name)) {
					return true;
				}
			}
			
			return false;
		} else {
			return type.getName().equals( name);
		}
	}


	/**
	 * Returns true when this box type is abstract.
	 *
	 * @return true when the box has an abstract element-type.
	 */
	public boolean isAbstract() {
		if (DEBUG) System.out.println( "ElementBox.isAbstract()");

		return type.isAbstract();
	}

	/**
	 * Returns true when this element is not required.
	 *
	 * @return false if the element is optional.
	 */
	public boolean isOptional() {
		return optional;
	}
	
	/**
	 * Sets the optional value to false.
	 */
	public void setRequired() {
		optional = false;
	}

	public String toString() {
		return "<"+type.getName()+">" +(optional ? "*" : "")+(element==null ? " X" : "");
	}
}
