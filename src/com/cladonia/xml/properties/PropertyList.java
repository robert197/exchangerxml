/*
 * $Id: PropertyList.java,v 1.3 2004/05/25 09:49:14 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.cladonia.xml.XElement;

/**
 * Handles a properties list, this list has a fixed amount of entries, 
 * the entries are placed in a LIFO order and it can't have duplicate 
 * entries.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/25 09:49:14 $
 * @author Dogsbay
 */
public class PropertyList {
	private static final boolean DEBUG = false;
	private XElement parent = null;
	private String name = null;
	private int max = -1;

	private Vector entries = null;

	/**
	 * Creates the Properties List for the element 
	 * with the name supplied.
	 *
	 * @param element the root element to get the properties from.
	 * @param name the elements to get for the list.
	 * @param max the maximum number of entries.
	 */
	public PropertyList( XElement element, String name, int max) {
		if (DEBUG) System.out.println( "PropertyList( "+element.getName()+", "+name+", "+max+") ["+name+"]");
		this.parent = element;
		this.name = name;
		this.max = max;
		
		entries = new Vector();

		List list = parent.elements( name);
		Iterator i = list.iterator();
		int counter = 0;
		
		while ( i.hasNext()) {
			XElement e = (XElement)i.next();

			if ( counter < max || max == -1) {
				addUnique( e.getText());
			}

			counter++;
		}
	}
	
	/**
	 * Returns the list of entries as a Vector of Strings.
	 *
	 * @return the list of entries.
	 */
	public Vector get() {
		if (DEBUG) System.out.println( "PropertyList.get() ["+name+"]");

		if (DEBUG) {
			for( int i = 0; i < entries.size(); i++) {
				System.out.println("entry["+i+"] = "+entries.elementAt(i));
			}
		}

		return new Vector( entries);
	}

	private void addUnique( String value) {
		if (DEBUG) System.out.println( "PropertyList.addUnique( "+value+") ["+name+"]");
		if ( value != null && value.length() > 0) {
			int index = -1;
			
			for( int i = 0; (i < entries.size()) && (index == -1); i++) {
				if ( ((String)entries.elementAt(i)).equals( value)) {
					return;
				}
			}
			
			entries.addElement( value);
		}
	}

	/**
	 * Adds a value to the list.
	 *
	 * @param value the value to add to the list.
	 */
	public void add( String value) {
		if (DEBUG) System.out.println( "PropertyList.add( "+value+") ["+name+"]");
		if ( value != null && value.length() > 0) {
			int index = -1;
			
			for( int i = 0; (i < entries.size()) && (index == -1); i++) {
				if ( ((String)entries.elementAt(i)).equals( value)) {
					index = i;
					break;
				}
			}
			
			if ( index != -1) {
				entries.removeElementAt( index);
			}

			entries.insertElementAt( value, 0);
			
			if ( max != -1 && entries.size() > max) {
				entries.removeElementAt( max);
			}
			
			if (DEBUG) {
				for( int i = 0; i < entries.size(); i++) {
					System.out.println("entry["+i+"] = "+entries.elementAt(i));
				}
			}
		}
	}

	/**
	 * Removes a value from the list.
	 *
	 * @param value the value to remove from the list.
	 */
	public void remove( String value) {
		if (DEBUG) System.out.println( "PropertyList.remove( "+value+") ["+name+"]");
		if ( value != null && value.length() > 0) {

			for( int i = 0; i < entries.size(); i++) {
				if ( ((String)entries.elementAt(i)).equals( value)) {
					entries.removeElementAt( i);
					return;
				}
			}
		}
	}

	/**
	 * Updates the list values in the parent element.
	 */
	public void update() {
		if (DEBUG) System.out.println( "PropertyList.update() ["+name+"]");
		
		// remove the previous values!
		List list = parent.elements( name);
		Iterator elements = list.iterator();
		
		while ( elements.hasNext()) {
			parent.remove( (XElement)elements.next());
		}

		// add the new values!
		for ( int i = 0; i < entries.size(); i++) {
			parent.addElement( name).setText( (String)entries.elementAt( i));
		}
	}
} 
