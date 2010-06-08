/*
 * $Id: FragmentProperties.java,v 1.3 2004/09/28 18:09:06 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the properties for a namespace.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/28 18:09:06 $
 * @author Dogsbay
 */
public class FragmentProperties extends Properties {
	
	public static final String FRAGMENT	= "fragment";

	private static final String NAME		= "name";
	private static final String ICON		= "icon";
	private static final String CONTENT		= "content";
	private static final String BLOCK		= "block";
	private static final String KEY			= "key";
	private static final String ORDER		= "order";

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the namespace.
	 */
	public FragmentProperties( XElement element) {
		super( element);
	}

	/**
	 * Constructor for the namespace properties.
	 *
	 * @param props the higher level properties object.
	 */
	public FragmentProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for the namespace properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public FragmentProperties( FragmentProperties original) {
		super( new XElement( FRAGMENT));
		
		setName( original.getName());
		setIcon( original.getIcon());
		setContent( original.getContent());
		setBlock( original.isBlock());
		setKey( original.getKey());
	}

	/**
	 * Constructor for a new namespace properties object.
	 */
	public FragmentProperties() {
		super( new XElement( FRAGMENT));
	}

	/**
	 * Return the Name.
	 *
	 * @return the Name.
	 */
	public String getName() {
		return getText( NAME);
	}
	public void setName( String name) {
		set( NAME, name);
	}

	/**
	 * Return the Icon Location.
	 *
	 * @return the Icon Location.
	 */
	public String getIcon() {
		return getText( ICON);
	}
	public void setIcon( String icon) {
		set( ICON, icon);
	}

	/**
	 * Return the Order of the Fragment.
	 *
	 * @return the Order of the Fragment.
	 */
	public int getOrder() {
		return getInteger( ORDER);
	}
	public void setOrder( int order) {
		set( ORDER, order);
	}

	/**
	 * Return the Content.
	 *
	 * @return the Content.
	 */
	public String getContent() {
		return getText( CONTENT);
	}
	public void setContent( String content) {
		set( CONTENT, content);
	}

	/**
	 * Return wether the fragment should be surrounded by \n.
	 *
	 * @return wether the fragment should surrounded by \n.
	 */
	public boolean isBlock() {
		return getBoolean( BLOCK, false);
	}
	public void setBlock( boolean indent) {
		set( BLOCK, indent);
	}

	/**
	 * Return the key shortcut.
	 *
	 * @return the key shortcut.
	 */
	public String getKey() {
		return getText( KEY);
	}
	public void setKey( String shortcut) {
		set( KEY, shortcut);
	}
	
	public String toString() {
	    return(this.getName());
	}
} 
