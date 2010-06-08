/*
 * $Id: TemplateProperties.java,v 1.3 2004/09/15 15:01:27 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.template;

import java.net.URL;

import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xngreditor.Identity;
import com.cladonia.xngreditor.URLUtilities;

/**
 * Handles the template properties.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/15 15:01:27 $
 * @author Dogsbay
 */
public class TemplateProperties extends Properties {
	
	private static final boolean DEBUG	= false;

	public static final String TEMPLATE_PROPERTIES	= "template";

	private static final String NAME	= "name";
	private static final String URL		= "url";

	/**
	 * Constructor for a new grammar properties object.
	 */
	public TemplateProperties() {
		super( new XElement( TEMPLATE_PROPERTIES));
	}

	/**
	 * Creates the Template Properties wrapper.
	 *
	 * @param element the properties element.
	 */
	public TemplateProperties( XElement element) {
		super( element);
	}

	/**
	 * Creates the Template Properties wrapper.
	 *
	 * @param props the properties object.
	 */
	public TemplateProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for a new template properties object.
	 *
	 * @param template imported template.
	 */
	public TemplateProperties( URL url, XElement template) {
		super( new XElement( TEMPLATE_PROPERTIES));
		
		importTemplate( url, template);
	}

	/**
	 * Creates the Template wrapper.
	 *
	 * @param url the url to the template document.
	 * @param name the name of the template  document.
	 */
	public TemplateProperties( String name, URL url) {
		super( new XElement( TEMPLATE_PROPERTIES));
		
		setURL( url);
		setName( name);
	}

	/**
	 * Sets the url for the document.
	 *
	 * @param url the url for the document.
	 */
	public void setURL( URL url) {
		set( URL, url.toString());
	}

	/**
	 * Sets the url for the document.
	 *
	 * @param url the url for the document.
	 */
	public void setURL( String url) {
		set( URL, url);
	}

	/**
	 * Get the url for the document.
	 *
	 * @return the document url.
	 */
	public URL getURL() {
//		System.out.println("DocumentProperties.getURL()");
		URL url = null;

		try {
			url = new URL( getText( URL));
		} catch (Exception e) { 
			// should not happen
//			e.printStackTrace();
		}
		
		return url;
	}

	/**
	 * Return the name.
	 *
	 * @return the name.
	 */
	public String getName() {
		return getText( NAME);
	}

	/**
	 * Set the name.
	 *
	 * @param name the template name.
	 */
	public void setName( String name) {
		set( NAME, name);
	}
	
	public void importTemplate( URL url, XElement element) {
		setName( getAttributeValue( element, "name"));
		setURL( URLUtilities.resolveURL( url, getAttributeValue( element, "src")));
	}

	public XElement exportTemplate( URL url) {
		//XElement root = new XElement( "template", "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
		XElement root = new XElement( "template");

		addAttribute( root, "name", getName());
		addAttribute( root, "src", URLUtilities.getRelativePath( url, getURL().toString()));

		return root;
	}
	
	private static String getAttributeValue( XElement element, String attributeName) {
		if ( element != null) {
			return element.getAttribute( attributeName);
		}
		
		return null;
	}
	
	private static void addAttribute( XElement element, String name, String value) {
		if ( value != null) {
			element.putAttribute( new XAttribute( name, value));
		}
	}
	
	public String toString() {
	    return(this.getName());
	}
} 
