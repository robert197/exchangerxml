/*
 * $Id: ElementList.java,v 1.1 2004/03/25 18:44:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer.model;

import java.util.Vector;

import com.cladonia.schema.SchemaElement;

/**
 * Keeps a reference to a list of element-boxes of the same type.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:21 $
 * @author Dogsbay
 */
public class ElementList extends ParticleList {
	private static final boolean DEBUG = false;

	private SchemaElement type;
	private Vector elements;
	private boolean init = false;

	/**
	 * Constructs a list elements for the type supplied.
	 *
	 * @param type the type of the elements that the list can contain.
	 */
	public ElementList( ModelBox parent, SchemaElement type) {
		super( parent);
		if (DEBUG) System.out.println( "ElementList( "+type+")");

		this.type = type;
		
		elements = new Vector();
		
		elements.addElement( new ElementBox( this, type, true));
	}
	
	/**
	 * Returns the element type for the list.
	 *
	 * @return the schema type of the list.
	 */
	public SchemaElement getType() {
		return type;
	}

	/**
	 * Sets an element in the list, will return true if a 
	 * place for the element in the list could be found.
	 *
	 * @param element the element to set in the list.
	 *
	 * @return true if a place for the element in the list could be found.
	 */
//	public boolean set( XElement element) {
//		if (DEBUG) System.out.println( "ElementList.set( "+element+")");
//
//		if ( !isFull() && element.getName().equals( type.getName())) {
//			for ( int i = 0; i < elements.size(); i++) {
//				ElementBox box = (ElementBox)elements.elementAt(i);
//				
//				// found an empty box...
//				if ( box.isEmpty()) {
//					box.set( element);
//
//					if ( !init) {
//						init();
//					} 
//
//					break;
//				}
//			}
//			
//			// The list could be full now, if not add a new element...
//			if ( !isFull() && !((ElementBox)elements.lastElement()).isEmpty()) {
//				elements.addElement( new ElementBox( type, true));
//			}
//
//			return true;
//		}
//		
//		return false;
//	}
	
	/**
	 * Initialises the list of elements.
	 */
	public void init() {
		if (DEBUG) System.out.println( "ElementList.init()");

		int min = type.getMinOccurs() - elements.size();
		
		// set optional/required value on previous elements
		for ( int i = 0; i < type.getMinOccurs() && i < elements.size(); i++) {
			ElementBox element = (ElementBox)elements.elementAt(i);
			
			element.setRequired();
		}

		// add all mandatory elements
		if ( min > 0) {
			for ( int i = 0; i < min; i++) {
				elements.addElement( new ElementBox( this, type, false));
			}
		}
		
		// add an optional element, Handled by set() method
//		if ( !reachedMax() && !((ElementBox)elements.lastElement()).isEmpty()) {
//			elements.addElement( new ElementBox( type, true));
//		}
	
		init = true;
	}

	/**
	 * Is this list still empty or does it have an element already?
	 *
	 * @return true if no box in the list contains a element.
	 */
	public boolean isEmpty() {
		for ( int i = 0; i < elements.size(); i++) {
			if ( !((ElementBox)elements.elementAt(i)).isEmpty()) {
				return false;
			}
		}
		
		return true;
	}

	/**
	 * Is this list full completely?
	 *
	 * @return true if the last box in the list contains a element 
	 *         and the maximum number of elements has been reached.
	 */
	public boolean isFull() {
		if ( reachedMax() && !((ElementBox)elements.lastElement()).isEmpty()) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Gets the list of elements.
	 *
	 * @return the list of elements.
	 */
	public Vector getElements() {
		return elements;
	}
	
	/**
	 * Appends the elements in this list to the list of elements.
	 *
	 * @param list the list of elements.
	 */
	public void appendElements( Vector list) {
		if (DEBUG) System.out.println( "ElementList.appendElements()");

		for (int i = 0; i < elements.size(); i++) {
			list.addElement( elements.elementAt(i));
		}
	}
	

	/**
	 * Checks the information in the model and updates accordingly.
	 */
	public void update() {
		if (DEBUG) System.out.println( "ElementList.update()");
		
		if ( init) {
			trim();
			init();
		}

		if ( !isFull()) {
			for ( int i = 0; i < elements.size(); i++) {
				ElementBox box = (ElementBox)elements.elementAt(i);
				
				// found a non empty box, initialise...
				if ( !box.isEmpty() && !init) {
					init();
					break;
				}
			}
			
			ElementBox lastBox = null;

			if ( elements.size() > 0) {
				lastBox = (ElementBox)elements.lastElement();
			}

			// The list could be full now, if not add a new element...
			if ( lastBox != null && !lastBox.isEmpty()) {
				elements.addElement( new ElementBox( this, type, true));
			}
		}
	}

	// removes empty boxes
	private void trim() {
		Vector temp = new Vector( elements);
		
		for ( int i = 0; i < temp.size(); i++) {
			ElementBox box = (ElementBox)temp.elementAt(i);

			if ( elements.size() > 1) {
				if ( box.isEmpty()) {
					elements.remove( box);
				}
			} else {
				return;
			}
		}
	}
	
	// true if the maximum number of models has been reached.
	private boolean reachedMax() {
		int max = type.getMaxOccurs();

		return (max > 0 && (elements.size() == max));
	}
}
