		   /*
 * $Id: HelperProperties.java,v 1.1 2004/03/25 18:45:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.helper;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the properties for the Helper.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:45:46 $
 * @author Dogsbay
 */
public class HelperProperties extends Properties {
	private static final String DIVIDER_LOCATION		= "divider-location";
	private static final int DEFAULT_DIVIDER_LOCATION	= 100;

	/**
	 * Constructor for the Helper properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the Helper.
	 */
	public HelperProperties( XElement element) {
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
} 
