/*
 * $Id: Properties.java,v 1.2 2004/07/16 08:20:45 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.properties;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.cladonia.xml.XElement;

/**
 * Handles the properties.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/07/16 08:20:45 $
 * @author Dogsbay
 */
public class Properties {
	private XElement element = null;
	private Hashtable lists = null;

	/**
	 * Creates the Properties for the element.
	 *
	 * @param element the root element to get the properties from.
	 */
	public Properties( XElement element) {
		this.element = element;
		
		lists = new Hashtable();
	}
	
	/**
	 * Sets the parent element.
	 *
	 * @param element the root element to get the properties from.
	 */
	public void setRoot( XElement element) {
		this.element = element;
	}

	/**
	 * Returns the parent element.
	 *
	 * @return the parent element to get the properties from.
	 */
	public XElement getElement() {
		return element;
	}

	/**
	 * Returns the element with the name supplied, it will create 
	 * a new element if the element does not yet exist.
	 *
	 * @param name the name for the child element.
	 *
	 * @return the element.
	 */
	public XElement get( String name) {
		XElement elem = element.getElement( name);
		
		if ( elem == null) {
			element.addElement( name);
			elem = element.getElement( name);
		}
	
		return elem;
	}

	/**
	 * Returns a properties list that can be used to handle a 
	 * LIFO list of elements with a maximum amount of entries.
	 * Note: this method creates only one list per name.
	 *
	 * @param name the name for the child list elements.
	 * @param max the maximum amount of entries in the list.
	 *
	 * @return the list.
	 */
	public PropertyList getList( String name, int max) {
	
		PropertyList list = (PropertyList)lists.get( name);
		
		if ( list == null) {
			list = new PropertyList( element, name, max);
			lists.put( name, list);
		}	
		
		return list;
	}

	/**
	 * Returns the text for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * Note: It will return null when no value has been set.
	 *
	 * @param name the name for the child element.
	 *
	 * @return the text from the element.
	 */
	public String getText( String name) {
		return get( name).getText();
	}

	/**
	 * Returns the text for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * It will return the default value when no value has been set.
	 *
	 * @param name the name for the child element.
	 * @param defaultValue the default value for the child element.
	 *
	 * @return the text from the element.
	 */
	public String getText( String name, String defaultValue) {
		String result = get( name).getText();
		
		if ( result == null || result.trim().length() == 0) {
			result = defaultValue;
		}
		
		return result;
	}

	/**
	 * Returns the boolean value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * Note: It will return false when the element cannot be parsed or 
	 * when no value has been set.
	 *
	 * @param name the name for the child element.
	 *
	 * @return the boolean value from the element.
	 */
	public boolean getBoolean( String name) {
		String value = get( name).getText();
		
		return "true".equals( value);
	}

	/**
	 * Returns the boolean value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * It will return the default value when the element cannot be 
	 * parsed or when no value has been set.
	 *
	 * @param name the name for the child element.
	 * @param defaultValue the default value for the child element.
	 *
	 * @return the boolean value from the element.
	 */
	public boolean getBoolean( String name, boolean defaultValue) {
		String value = get( name).getText();
		
		if ( "true".equals( value)) {
			return true;
		} else if ( "false".equals( value)) {
			return false;
		} else {
			return defaultValue;
		}
	}

	/**
	 * Returns the integer value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * Note: It will return -1 when the element cannot be parsed or 
	 * when no value has been set.
	 *
	 * @param name the name for the child element.
	 *
	 * @return the integer value from the element.
	 */
	public int getInteger( String name) {
		String string = get( name).getText();
		int value = -1;
		
		if ( string != null) {
			try {
				value = Integer.parseInt( string);
			} catch ( NumberFormatException e) {
				value = -1;
			}
		}

		return value;
	}
	
	/**
	 * Returns the integer value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * It will return the default value when the element cannot be 
	 * parsed or when no value has been set.
	 *
	 * @param name the name for the child element.
	 * @param defaultValue the default value for the child element.
	 *
	 * @return the boolean value from the element.
	 */
	public int getInteger( String name, int defaultValue) {
		int value = getInteger( name);
		
		if ( value == -1) {
			return defaultValue;
		} 

		return value;
	}

	/**
	 * Returns the long value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * Note: It will return -1 when the element cannot be parsed or 
	 * when no value has been set.
	 *
	 * @param name the name for the child element.
	 *
	 * @return the long value from the element.
	 */
	public long getLong( String name) {
		String string = get( name).getText();
		long value = -1;
		
		if ( string != null) {
			try {
				value = Long.parseLong( string);
			} catch ( NumberFormatException e) {
				value = -1;
			}
		}

		return value;
	}
	
	/**
	 * Returns the long value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * It will return the default value when the element cannot be 
	 * parsed or when no value has been set.
	 *
	 * @param name the name for the child element.
	 * @param defaultValue the default value for the child element.
	 *
	 * @return the long value from the element.
	 */
	public long getLong( String name, long defaultValue) {
		long value = getLong( name);
		
		if ( value == -1) {
			return defaultValue;
		} 

		return value;
	}

	/**
	 * Returns the Color value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * Note: It will return -1 when the element cannot be parsed or 
	 * when no value has been set.
	 *
	 * @param name the name for the child element.
	 *
	 * @return the Color value from the element.
	 */
	public Color getColor( String name) {
		Color value = Color.white;
		
		if ( name != null) {
			try {
				int rgb = getInteger( name);

				value = new Color( rgb);
			} catch ( NumberFormatException e) {
				value = Color.white;
			}
		}

		return value;
	}
	
	/**
	 * Returns the color value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 * It will return the default value when the element cannot be 
	 * parsed or when no value has been set.
	 *
	 * @param name the name for the child element.
	 * @param defaultValue the default value for the child element.
	 *
	 * @return the Color value from the element.
	 */
	public Color getColor( String name, Color defaultValue) {
		Color value = getColor( name);
		
		if ( value == null) {
			return defaultValue;
		} 

		return value;
	}

	/**
	 * Sets the Color for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 *
	 * @param name the name for the child element.
	 * @param value the value from the element.
	 */
	public void set( String name, Color value) {
		if ( value == null) {
			value = Color.black;
		}

		set( name, value.getRGB());
	}

	/**
	 * Sets the text for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 *
	 * @param name the name for the child element.
	 * @param value the value from the element.
	 */
	public void set( String name, String value) {
		if ( value == null) {
			value = "";
		}

		get( name).setText( value);
	}

	/**
	 * Sets the boolean value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 *
	 * @param name the name for the child element.
	 * @param value the value for the child element.
	 */
	public void set( String name, boolean value) {
		get( name).setText( ""+value);
	}

	/**
	 * Sets the integer value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 *
	 * @param name the name for the child element.
	 * @param value the value for the child element.
	 */
	public void set( String name, int value) {
		get( name).setText( ""+value);
	}

	/**
	 * Sets the long value for the element with the name supplied, 
	 * it will create a new element if the element does not exist.
	 *
	 * @param name the name for the child element.
	 * @param value the value for the child element.
	 */
	public void set( String name, long value) {
		get( name).setText( ""+value);
	}

	/**
	 * Adds properties to the list of properties.
	 *
	 * @param props the properties to add.
	 */
	public void add( Properties props) {
		element.add( (XElement) props.getElement().clone());
	}

	/**
	 * Adds a property to the list of properties.
	 *
	 * @param props the properties to add.
	 */
	public void add( String name, String value) {
//		System.out.println( "Properties.add( "+name+", "+value+")");
		XElement e = new XElement( name);
		e.setText( value);

		element.add( e);
	}

	/**
	 * Removes a property from the list of properties.
	 *
	 * @param props the properties to remove.
	 */
	public void remove( String name, String value) {
		XElement[] list = element.getElements( name);
		
		for ( int i = 0; i < list.length; i++) {
			if ( value.equals( list[i].getText())) {
				element.remove( list[i]);
			}
		}
	}

	/**
	 * Get a list of values for the name.
	 *
	 * @param name the properties to name.
	 *
	 * @return the list of properties.
	 */
	public Vector getStringList( String name) {
//		System.out.println( "Properties.getStringList() ["+name+"]");
		Vector elements = new Vector();
		
		XElement[] list = element.getElements( name);
		
		for ( int i = 0; i < list.length; i++) {
			elements.addElement( list[i]);
//			System.out.println( "Properties.getStringList() ["+list[i]+"]");
		}
		
		return elements;
	}

	/**
	 * Removes properties from the list of properties.
	 *
	 * @param props the properties to remove.
	 */
	public void remove( Properties props) {
		element.remove( props.getElement());
	}

	/**
	 * Get a vector of properties.
	 *
	 * @param name the name of the list of properties.
	 *
	 * @return the list of properties.
	 */
	public Vector getProperties( String name) {
		Vector entries = new Vector();

		List list = element.elements( name);
		Iterator i = list.iterator();
		
		while ( i.hasNext()) {
			XElement e = (XElement)i.next();
			entries.addElement( new Properties( e));
		}
		
		return entries;
	}

	/**
	 * Makes sure the properties are up to date.
	 */
	public void update() {
	}
	
	/**
	 * Returns the text for the element with the name supplied, 
	 * Note: It will return null if the element does not exist
	 *
	 * @param name the name for the child element.
	 *
	 * @return the text from the element.
	 */
	public String getContentIfExists( String name) {
		XElement elem = element.getElement( name);
		
		if ( elem == null) {
			return null;
		}
		else
			return elem.getText();

	}
	
} 
