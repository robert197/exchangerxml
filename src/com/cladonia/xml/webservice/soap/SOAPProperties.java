/*
 * $Id: SOAPProperties.java,v 1.1 2004/03/25 18:52:12 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.soap;

import java.util.Vector;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertyList;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * Handles the properties for the Text Editor.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:52:12 $
 * @author Dogsbay
 */
public class SOAPProperties extends Properties {
	
	private static final boolean DEBUG	= false;

	public static final String SOAP_PROPERTIES	= "SOAP-properties";

	private static final int MAX_TARGETS 	= 10;
	private static final int MAX_ACTIONS 	= 10;

	private static final String TARGET 	= "target";
	private static final String ACTION 	= "action";

	private PropertyList targets = null;
	private PropertyList actions = null;

	private static final String FORMAT_RESULT			= "format-result";
	private static final String OPEN_AS_NEW_DOCUMENT	= "open-as-new-document";

	private ConfigurationProperties properties = null;

	/**
	 * Constructor for a new SOAP properties object.
	 */
	public SOAPProperties( XElement root) {
		super( root);

		targets = getList( TARGET, MAX_TARGETS);
		actions = getList( ACTION, MAX_ACTIONS);
	}

	/**
	 * Adds a SOAP Target string to the properties.
	 *
	 * @param target the SOAP Target.
	 */
	public void addTarget( String target) {
		targets.add( target);
	}

	/**
	 * Returns the list of SOAP Targets.
	 *
	 * @return the list of SOAP Targets.
	 */
	public Vector getTargets() {
		return targets.get();
	}

	/**
	 * Adds a SOAP Action string to the properties.
	 *
	 * @param action the SOAP Action.
	 */
	public void addAction( String action) {
		actions.add( action);
	}

	/**
	 * Returns the list of SOAP Actions.
	 *
	 * @return the list of SOAP Actions.
	 */
	public Vector getActions() {
		return actions.get();
	}
	
	/**
	 * Check to find out if the result should be formatted.
	 *
	 * @return true when the result should be formatted.
	 */
	public boolean isFormatResult() {
		return getBoolean( FORMAT_RESULT, true);
	}

	/**
	 * Set the format option.
	 *
	 * @param format true when the output should be formatted.
	 */
	public void setFormatResult( boolean format) {
		set( FORMAT_RESULT, format);
	}

	/**
	 * Check to find out if the result should be opened as a new document.
	 *
	 * @return true when the result should be opened as a new document.
	 */
	public boolean isOpenAsNewDocument() {
		return getBoolean( OPEN_AS_NEW_DOCUMENT, false);
	}

	/**
	 * Set the open as a new document option.
	 *
	 * @param open true when the result should be opened as a new document.
	 */
	public void setOpenAsNewDocument( boolean open) {
		set( OPEN_AS_NEW_DOCUMENT, open);
	}

	/**
	 * Update the values.
	 */
	public void update() {
		targets.update();
		actions.update();
	}
} 
