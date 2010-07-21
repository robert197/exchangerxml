/*
 * $Id: Identity.java,v 1.7 2005/09/05 13:55:11 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

/**
 * The version and name of the XML plus editor application.
 *
 * Sets 6 System-properties for general use:<br/>
 * <code>xngreditor.title</code><br/>
 * <code>xngreditor.vendor</code><br/>
 * <code>xngreditor.reference</code><br/>
 * <code>xngreditor.version</code><br/>
 * <code>xngreditor.description</code><br/>
 * <code>xngreditor.copyright</code><br/>
 *
 * @version	$Revision: 1.7 $, $Date: 2005/09/05 13:55:11 $
 * @author Dogsbay
 */
public class Identity {
	private static Identity identity = null;
	
	public static final String XMLPLUS_TITLE_PROPERTY 		= "xngreditor.title";
	public static final String XMLPLUS_VENDOR_PROPERTY		= "xngreditor.vendor";
	public static final String XMLPLUS_REFERENCE_PROPERTY	= "xngreditor.reference";
	public static final String XMLPLUS_VERSION_PROPERTY		= "xngreditor.version";
	public static final String XMLPLUS_DESCRIPTION_PROPERTY	= "xngreditor.description";
	public static final String XMLPLUS_COPYRIGHT_PROPERTY	= "xngreditor.copyright";
	public static final String XMLPLUS_EDITION_PROPERTY	= "xngreditor.edition";
	
	public static final String XMLPLUS_EDITION_LITE = "Lite";
	public static final String XMLPLUS_EDITION_PROFESSIONAL = "Professional";
	public static final String XMLPLUS_EDITION_ENTERPRISE= "Enterprise";
	
	/**
	 * Gets the one version of the identity.
	 */
	public static Identity getIdentity() {
		if ( identity == null) {
			identity = new Identity();
		}
		
		return identity;
	}
	
	/**
	 * Sets the identity of the application as System properties...
	 */
	private Identity() {
		System.setProperty( XMLPLUS_TITLE_PROPERTY, "Exchanger XML Editor");
		System.setProperty( XMLPLUS_VENDOR_PROPERTY, "Cladonia Ltd.");
		System.setProperty( XMLPLUS_REFERENCE_PROPERTY, "http://www.exchangerxml.com/");
		System.setProperty( XMLPLUS_VERSION_PROPERTY, "3.3.01");
		System.setProperty( XMLPLUS_DESCRIPTION_PROPERTY, "XML Editor");
		System.setProperty( XMLPLUS_COPYRIGHT_PROPERTY, "Copyright 2002 - 2010 \u00a9 Cladonia Ltd.");
		System.setProperty( XMLPLUS_EDITION_PROPERTY, XMLPLUS_EDITION_PROFESSIONAL );
		//System.setProperty( XMLPLUS_EDITION_PROPERTY, XMLPLUS_EDITION_LITE );
	}

	/**
	 * Gets the product's title.
	 */
	public String getTitle()  {
		return System.getProperty( XMLPLUS_TITLE_PROPERTY);
	}

	/**
	 * Gets the vendor's name.
	 */
	public String getVendor() {
		return System.getProperty( XMLPLUS_VENDOR_PROPERTY);
	}

	/**
	 * Gets a reference, ie. the products home page.
	 */
	public String getReference() {
		return System.getProperty( XMLPLUS_REFERENCE_PROPERTY);
	}
	
	
	/**
	 * Gets the version number for the product.
	 */
	public String getVersion() {
		return System.getProperty( XMLPLUS_VERSION_PROPERTY);
	}



	/**
	 * Gets a description for the product.
	 */
	public String getDescription() {
		return System.getProperty( XMLPLUS_DESCRIPTION_PROPERTY);
	}

	/**
	 * Gets the copyright information for the product.
	 */
	public String getCopyright() {
		return System.getProperty( XMLPLUS_COPYRIGHT_PROPERTY);
	}
	
	/**
	 * Gets the product's edition.
	 */
	public String getEdition()  {
		return System.getProperty( XMLPLUS_EDITION_PROPERTY);
	}


} 
