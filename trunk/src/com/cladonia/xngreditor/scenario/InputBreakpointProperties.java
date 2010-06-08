/*
 * $Id: InputBreakpointProperties.java,v 1.1 2004/03/25 18:56:35 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the properties for a Transformation scenario.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:56:35 $
 * @author Dogsbay
 */
public class InputBreakpointProperties extends BreakpointProperties {
	
	public static final String INPUT_BREAKPOINT	= "input-breakpoint";

	/**
	 * Constructor for the scenario properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the scenario-type.
	 */
	public InputBreakpointProperties( XElement element) {
		super( element);
	}

	/**
	 * Constructor for the scenario properties.
	 *
	 * @param props the higher level properties object.
	 */
	public InputBreakpointProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for the scenario properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public InputBreakpointProperties( BreakpointProperties original) {
		super( new XElement( INPUT_BREAKPOINT));
		
		setURL( original.getURL());
		setLine( original.getLine());
		setEnabled( original.isEnabled());
	}

	/**
	 * Constructor for the scenario properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public InputBreakpointProperties( String url, int line, boolean enabled) {
		super( new XElement( INPUT_BREAKPOINT));
		
		setURL( url);
		setLine( line);
		setEnabled( enabled);
	}

	/**
	 * Constructor for a new scenario properties object.
	 */
	public InputBreakpointProperties() {
		super( new XElement( INPUT_BREAKPOINT));
	}
} 
