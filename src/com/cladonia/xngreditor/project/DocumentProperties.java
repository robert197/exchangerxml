/*
 * $Id: DocumentProperties.java,v 1.3 2004/05/18 16:57:45 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.io.File;
import java.net.URL;
import java.util.Date;

import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xngreditor.URLUtilities;

/**
 * Handles the Xml Plus configuration document.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/18 16:57:45 $
 * @author Dogsbay
 */
public class DocumentProperties extends Properties {

	public static final String DOCUMENT_PROPERTIES	= "document";

	public static final int STATE_UNKNOWN		= 0;
	public static final int STATE_ERROR			= 1;
	public static final int STATE_WELLFORMED	= 2;
	public static final int STATE_VALID			= 3;

	private static final String TYPE		= "type";
	private static final String URL			= "url";
	private static final String NAME		= "name";
	private static final String STATE		= "state";
	private static final String MODIFIED	= "modified";
	private static final String READ_ONLY	= "read-only";
	public static final String REMOTE		= "remote";

	/**
	 * Creates the Document Properties wrapper.
	 *
	 * @param element the properties element.
	 */
	public DocumentProperties( XElement element) {
		super( element);
	}

	/**
	 * Creates the Document Properties wrapper.
	 *
	 * @param props the properties object.
	 */
	public DocumentProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Creates the Configuration Document wrapper.
	 * It reads in the root element and if it has to, it creates the property file.
	 *
	 * @param url the url to the remote document.
	 * @param name the name of the remote document.
	 */
	public DocumentProperties( URL url) {
		super( new XElement( DOCUMENT_PROPERTIES));
		
		String name = URLUtilities.getFileName( url);

		setURL( url);
		setName( name);
		setRemote( true);
	}

	/**
	 * Creates the Configuration Document wrapper.
	 * It reads in the root element and if it has to, it creates the property file.
	 *
	 * @param file the XML file.
	 */
	public DocumentProperties( File file) {
		super( new XElement( DOCUMENT_PROPERTIES));
		
		try {
			setURL( XngrURLUtilities.getURLFromFile(file));
			setName( file.getName());
		} catch ( Exception e) {
			e.printStackTrace(); // Should not happen.
		}
	}

	/**
	 * Sets the url for the document.
	 *
	 * @param url the url for the document.
	 */
	public void setURL( URL url) {
		set( URL, URLUtilities.encrypt( url));
	}

	/**
	 * Sets the type id.
	 *
	 * @param type the type id.
	 */
	public void setType( String type) {
		set( TYPE, type);
	}

	/**
	 * Get the type for the document.
	 *
	 * @return the document type.
	 */
	public String getType() {
		return getText( TYPE);
	}

	/**
	 * Get the url for the document.
	 *
	 * @return the document url.
	 */
	public URL getURL() {
		return URLUtilities.decrypt( getText( URL));
	}

	/**
	 * Sets wether the document is read-only.
	 *
	 * @param enabled true when the document is readonly.
	 */
	public void setReadOnly( boolean enabled) {
		set( READ_ONLY, enabled);
	}

	/**
	 * Gets wether the document is read-only.
	 *
	 * @return true when the document is readonly.
	 */
	public boolean isReadOnly() {
		if ( isRemote()) {
			return true;
		} else {
			return getBoolean( READ_ONLY, false);
		}
	}

	/**
	 * Gets wether the document is remote.
	 *
	 * @return true when the document is remote.
	 */
	public boolean isRemote() {
		return getBoolean( REMOTE, false);
	}

	/**
	 * Sets wether the document is remote.
	 *
	 * @param enabled true when the document is remote.
	 */
	private void setRemote( boolean enabled) {
		set( REMOTE, enabled);
	}

	/**
	 * Sets the state of the document.
	 *
	 * @param state the state of the document: 
	 *        STATE_UNKNOWN, STATE_ERROR, STATE_WELLFORMED, STATE_VALID.
	 */
	public void setState( int state) {
		set( STATE, state);
		setModified();
	}

	/**
	 * Sets the modification date for the document.
	 */
	public void setModified() {
		long modified = (new Date()).getTime();
	
		set( MODIFIED, modified);
	}

	/**
	 * Gets the modification date for the document.
	 */
	public boolean isModified() {
//		try {
//			URL url = getURL();
//			URLConnection connection = url.openConnection();
//			
//			if ( connection.getLastModified() > getModified()) {
//				return true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return false;
	}

	/**
	 * Gets the modification date for the document.
	 */
	public long getModified() {
		return getLong( MODIFIED, (new Date()).getTime());
	}

	/**
	 * Gets the state of the document.
	 *
	 * @return the state of the document: 
	 *         STATE_UNKNOWN, STATE_ERROR, STATE_WELLFORMED, STATE_VALID.
	 */
	public int getState() {
		if ( !isModified()) {
			return getInteger( STATE, STATE_UNKNOWN);
		} else {
			return STATE_UNKNOWN;
		}
	}

	/**
	 * Sets the name for the document.
	 *
	 * @param name the name for the document.
	 */
	public void setName( String name) {
		set( NAME, name);
	}

	/**
	 * Get the name for the document.
	 *
	 * @return the document name.
	 */
	public String getName() {
		return getText( NAME);
	}
} 
