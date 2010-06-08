		   /*
 * $Id: NavigatorProperties.java,v 1.1 2004/03/25 18:46:20 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the properties for the navigator.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:46:20 $
 * @author Dogsbay
 */
public class NavigatorProperties extends Properties {
	private static final String SHOW_ATTRIBUTE_VALUES	= "show-attribute-values";

	/**
	 * Constructor for the navigator properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the navigator.
	 */
	public NavigatorProperties( XElement element) {
		super( element);
	}

	public void setShowAttributeValues( boolean show) {
		set( SHOW_ATTRIBUTE_VALUES, show);
	}

	public boolean isShowAttributeValues() {
		return getBoolean( SHOW_ATTRIBUTE_VALUES, false);
	}
} 
