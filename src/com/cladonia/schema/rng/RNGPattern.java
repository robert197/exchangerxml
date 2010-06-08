/*
 * $Id: RNGPattern.java,v 1.3 2004/09/23 16:39:48 edankert Exp $
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
 * @version	$Revision: 1.3 $, $Date: 2004/09/23 16:39:48 $
 * @author Dogsbay
 */
public class RNGPattern {
	private Vector elements	= null;
	private Vector attributes	= null;
	protected Vector references	= null;
	
	public RNGPattern() {
		elements = new Vector();
		attributes = new Vector();
		references = new Vector();
	}
	
	public void addElement( RNGElement element) {
		if ( element != null) {
//			for ( int i = 0; i < elements.size(); i++) {
//				RNGElement elem = (RNGElement)elements.elementAt(i);
//				
//				if ( elem.getQualifiedName().equals( element.getQualifiedName())) {
////					return false;
//				}
//			}
			
			elements.addElement( element);
//			return true;
//		} else {
//			return false;
		}
	}

	protected void addElements( Vector elements) {
		if ( elements != null) {
			for ( int i = 0; i < elements.size(); i++) {
				RNGElement element = (RNGElement)elements.elementAt(i);
				addElement( element);
			}
		}
	}

	public Vector getElements() {
		for ( int i = 0; i < references.size(); i++) {
			RNGReference reference = (RNGReference)references.elementAt(i);
			
			if ( reference.isResolved()) {
				Vector elements = reference.getElements();

				for ( int j = 0; j < elements.size(); j++) {
					addElement( (RNGElement)elements.elementAt(j));
				}
			}
		}
		
		return elements;
	}

	public void addAttribute( RNGAttribute attribute) {
//		if ( attribute != null) {
//			for ( int i = 0; i < attributes.size(); i++) {
//				RNGAttribute attrib = (RNGAttribute)attributes.elementAt(i);
//				
//				if ( attrib.getUniversalName().equals( attribute.getUniversalName())) {
//					if ( attrib != attribute) {
//						attrib.merge( attribute);
//					}
//					
//					return false;
//				}
//			}

			attributes.addElement( attribute);
//			return true;
//		} else {
//			return false;
//		}
	}

	protected void addAttributes( Vector attributes) {
		if ( attributes != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				RNGAttribute attribute = (RNGAttribute)attributes.elementAt(i);
				addAttribute( attribute);
			}
		}
	}

	public Vector getAttributes() {
		for ( int i = 0; i < references.size(); i++) {
			RNGReference reference = (RNGReference)references.elementAt(i);

			if ( reference.isResolved()) {
				Vector attributes = reference.getAttributes();

				for ( int j = 0; j < attributes.size(); j++) {
					RNGAttribute attribute = (RNGAttribute)attributes.elementAt(j);
					addAttribute( attribute);
				}
			} else {
				System.out.println( "ERROR: Reference "+reference.getName()+" not resolved!");
			}
		}
		
//		System.out.println( "RNGPattern.getAttributes() ["+attributes+"]");

		return attributes;
	}
	
	protected void addReferences( Vector references) {
		if ( references != null) {
			for ( int i = 0; i < references.size(); i++) {
				RNGReference reference = (RNGReference)references.elementAt(i);
				addReference( reference);
			}
		}
	}

	public boolean addReference( RNGReference reference) {
		if ( reference != null) {
			for ( int i = 0; i < references.size(); i++) {
				RNGReference ref = (RNGReference)references.elementAt(i);
				
				if ( ref.getName().equals( reference.getName())) {
					return false;
				}
			}

			references.addElement( reference);
			return true;
		} else {
			return false;
		}
	}

	public Vector getReferences() {
		return references;
	}
} 
