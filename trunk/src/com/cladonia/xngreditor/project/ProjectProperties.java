/*
 * $Id: ProjectProperties.java,v 1.1 2004/03/25 18:54:53 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the Project properties and can have many documents and folders.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:54:53 $
 * @author Dogsbay
 */
public class ProjectProperties extends FolderProperties {
	public static final String PROJECT_PROPERTIES = "project";

	/**
	 * Creates the Configuration Document wrapper.
	 * It reads in the root element and if it has to, it creates the property file.
	 *
	 * @param the url to the XML document.
	 */
	public ProjectProperties( XElement element) {
		super( element);
	}
	
	/**
	 * Creates the Configuration Document wrapper.
	 * It reads in the root element and if it has to, it creates the property file.
	 *
	 * @param props the properties.
	 */
	public ProjectProperties( Properties props) {
		super( props);
	}

	/**
	 * Creates the Configuration Document wrapper.
	 * It reads in the root element and if it has to, it creates the property file.
	 *
	 * @param the url to the XML document.
	 */
	public ProjectProperties( String name) {
		super( new XElement( PROJECT_PROPERTIES));
		
		setName( name);
	}
	
	private void test() {
	}
} 
