/*
 * $Id: BreakpointProperties.java,v 1.1 2004/03/25 18:56:35 edankert Exp $
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
public class BreakpointProperties extends Properties {
	
	public static final String BREAKPOINT	= "breakpoint";

	private static final String URL		= "url";
	private static final String LINE	= "line";
	private static final String ENABLED	= "enabled";

	/**
	 * Constructor for the scenario properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the scenario-type.
	 */
	public BreakpointProperties( XElement element) {
		super( element);
	}

	/**
	 * Constructor for the scenario properties.
	 *
	 * @param props the higher level properties object.
	 */
	public BreakpointProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for the scenario properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public BreakpointProperties( BreakpointProperties original) {
		super( new XElement( BREAKPOINT));
		
		setURL( original.getURL());
		setLine( original.getLine());
	}

	/**
	 * Constructor for the scenario properties, creates a copy.
	 *
	 * @param props the original to copy.
	 */
	public BreakpointProperties( String url, int line, boolean enabled) {
		super( new XElement( BREAKPOINT));
		
		setURL( url);
		setLine( line);
		setEnabled( enabled);
	}

	/**
	 * Constructor for a new scenario properties object.
	 */
	public BreakpointProperties() {
		super( new XElement( BREAKPOINT));
	}

	/**
	 * Return the url.
	 *
	 * @return the url.
	 */
	public String getURL() {
		return getText( URL);
	}

	/**
	 * Set the url.
	 *
	 * @param url the breakpoint url.
	 */
	public void setURL( String url) {
		set( URL, url);
	}

	/**
	 * Return the line number.
	 *
	 * @return the line number.
	 */
	public int getLine() {
		return getInteger( LINE);
	}

	/**
	 * Set the line number.
	 *
	 * @param line the line number.
	 */
	public void setLine( int line) {
		set( LINE, line);
	}

	/**
	 * Return wether this breakpoint is enabled.
	 *
	 * @return true when this breakpoint is enabled.
	 */
	public boolean isEnabled() {
		return getBoolean( ENABLED, true);
	}

	/**
	 * Set wether this breakpoint is enabled.
	 *
	 * @param enabled the enables the breakpoint.
	 */
	public void setEnabled( boolean enabled) {
		set( ENABLED, enabled);
	}
} 
