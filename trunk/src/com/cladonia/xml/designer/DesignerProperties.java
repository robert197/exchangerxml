/*
 * $Id: DesignerProperties.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
//import com.cladonia.xml.properties.PropertyList;

/**
 * Handles the properties for the XML Tree Editor.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public class DesignerProperties extends Properties {
	private static final String DIVIDER_LOCATION		= "divider-location";
	private static final int DEFAULT_DIVIDER_LOCATION	= 50;
	private static final String AUTO_CREATE_REQUIRED	= "auto-create-required";
	private static final String SHOW_ATTRIBUTE_VALUES	= "show-attribute-values";
	private static final String SHOW_ELEMENT_VALUES		= "show-element-values";

	/**
	 * Constructor for the editor properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the editor.
	 */
	public DesignerProperties( XElement element) {
		super( element);
	}

	/**
	 * Set the split divider location.
	 *
	 * @param location the split divider location.
	 */
	public void setDividerLocation( int location) {
		set( DIVIDER_LOCATION, location);
	}

	/**
	 * Gets the location of the split divider.
	 *
	 * @return the location of the split divider.
	 */
	public int getDividerLocation() {
		return getInteger( DIVIDER_LOCATION, DEFAULT_DIVIDER_LOCATION);
	}
	
	/**
	 * Should required elements and attributes atomatically
	 * be created when encountered.
	 *
	 * @return true automatically create required nodes.
	*/
	public boolean isAutoCreateRequired() {
		return getBoolean( AUTO_CREATE_REQUIRED, false);
	}

	 /**
	  * Should required elements and attributes atomatically
	  * be created when encountered.
	  *
	  * @param create automatically create required nodes.
	 */
	public void setAutoCreateRequired( boolean create) {
		set( AUTO_CREATE_REQUIRED, create);
	}

	/**
	 * Should element/attribute values be visible.
	 *
	 * @return true when element/attribute values should be visible.
	*/
	public boolean isShowAttributeValues() {
		return getBoolean( SHOW_ATTRIBUTE_VALUES, false);
	}

	 /**
	  * Should element/attribute values be visible.
	  *
	  * @param visible set true when element/attribute values should be visible.
	 */
	public void setShowAttributeValues( boolean visible) {
		set( SHOW_ATTRIBUTE_VALUES, visible);
	}

	/**
	 * Should element/attribute values be visible.
	 *
	 * @return true when element/attribute values should be visible.
	*/
	public boolean isShowElementValues() {
		return getBoolean( SHOW_ELEMENT_VALUES, false);
	}

	 /**
	  * Should element values be visible.
	  *
	  * @param visible set true when element values should be visible.
	 */
	public void setShowElementValues( boolean visible) {
		set( SHOW_ELEMENT_VALUES, visible);
	}
} 
