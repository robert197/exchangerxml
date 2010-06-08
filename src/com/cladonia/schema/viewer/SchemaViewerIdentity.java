/*
 * $Id: SchemaViewerIdentity.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

/**
 * The version and name of the Schema Viewer browser service.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class SchemaViewerIdentity {
	
	/**
	 * Gets the product's title.
	 */
	public String getTitle()  {
		return "Schema Viewer";
	}

	/**
	 * Gets the vendor's name.
	 */
	public String getVendor() {
		return "Cladonia Ltd";
	}

	/**
	 * Gets a reference, ie. the products home page.
	 */
	public String getReference() {
		return "http://www.cladonia.com/";
	}
	
	
	/**
	 * Gets the version number for the product.
	 */
	public String getVersion() {
		return "0.3";
	}

	/**
	 * Gets a description for the product.
	 */
	public String getDescription() {
		return "Schema Viewer Service";
	}

	/**
	 * Gets the copyright information for the product.
	 */
	public String getCopyright() {
		return "Copyright 2002 (C) Cladonia Ltd.";
	}
} 
