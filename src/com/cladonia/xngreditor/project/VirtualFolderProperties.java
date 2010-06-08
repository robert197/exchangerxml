/*
 * $Id: VirtualFolderProperties.java,v 1.1 2005/09/05 09:08:30 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Vector;

import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the Xml Plus configuration document.
 *
 * @version	$Revision: 1.1 $, $Date: 2005/09/05 09:08:30 $
 * @author Dogsbay
 */
public class VirtualFolderProperties extends Properties {
	public static final String VIRTUAL_FOLDER_PROPERTIES = "virtual-folder";
	public static final String NAME = "name";
	public static final String URL = "url";
		
	/**
	 * Creates the VirtualFolder properties object from a xml element.
	 *
	 * @param element the XML element.
	 */
	public VirtualFolderProperties( XElement element) {
		super( element);
	}

	/**
	 * Creates the VirtualFolder Properties wrapper.
	 *
	 * @param props the properties object.
	 */
	public VirtualFolderProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Creates the VirtualFolder properties object from a directory.
	 *
	 * @param dir the directory to create the folder for.
	 */
	public VirtualFolderProperties( File dir) {
		this();
		if( dir.getName().length() > 0) {
			this.setName(dir.getName());
		}
		else {
			this.setName(dir.getPath());
		}
		try {
			
			setURL(XngrURLUtilities.getURLFromFile(dir).toString());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// add all the files...
		/*File[] files = dir.listFiles();
		
		for ( int i = 0; i < files.length; i++) {
			File file = files[i];
			
			if ( file.isDirectory()) {
				addVirtualFolderProperties( new VirtualFolderProperties( file));
			} else { // file
				addDocumentProperties( new DocumentProperties( file));
			}
		}*/
	}
	
	/**
	 * Creates the VirtualFolder properties object.
	 *
	 * @param name the name for the folder.
	 */
	public VirtualFolderProperties( String name) {
		super( new XElement( VIRTUAL_FOLDER_PROPERTIES));
		
		setName( name);
	}
	
	/**
	 * Creates the VirtualFolder properties object.
	 *
	 */
	public VirtualFolderProperties( ) {
		super( new XElement( VIRTUAL_FOLDER_PROPERTIES));
		
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
	
	/**
	 * Sets the url for the document.
	 *
	 * @param url the url for the document.
	 */
	public void setURL( String url) {
		set( URL, url);
	}

	/**
	 * Get the name for the document.
	 *
	 * @return the document name.
	 */
	public String getURL() {
		return getText( URL);
	}

	/**
	 * Returns the document properties list.
	 *
	 * @return the document properties.
	 */
	public Vector getDocumentProperties() {
		Vector result = new Vector();
		Vector list = getProperties( DocumentProperties.DOCUMENT_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new DocumentProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a document properties object to the project.
	 *
	 * @param props the document properties.
	 */
	public void addDocumentProperties( DocumentProperties props) {
		Vector documents = getDocumentProperties();
		boolean exists = false;
		
		// remove a previous document/file with the same name
		for ( int i = 0; (i < documents.size()) && !exists; i++) {
			DocumentProperties doc = (DocumentProperties)documents.elementAt(i);

			if ( doc.getName().equals( props.getName())) {
				remove( doc);
				break;
			}
		}
		
		add( props);
	}

	/**
	 * Removes a document properties object from the project.
	 *
	 * @param props the document properties.
	 */
	public void removeDocumentProperties( DocumentProperties props) {
		remove( props);
	}

	/**
	 * Returns the folder properties list.
	 *
	 * @return the folder properties.
	 */
	public Vector getVirtualFolderProperties() {
		Vector result = new Vector();
		Vector list = getProperties( VIRTUAL_FOLDER_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new VirtualFolderProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}
	
	/**
	 * Returns the folder properties list.
	 *
	 * @return the folder properties.
	 */
	public Vector getFolderProperties() {
		Vector result = new Vector();
		Vector list = getProperties( FolderProperties.FOLDER_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new FolderProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a folder properties object to the project.
	 *
	 * @param props the folder properties.
	 */
	public void addVirtualFolderProperties( VirtualFolderProperties props) {
		Vector folders = getVirtualFolderProperties();

		// remove a previous folder with the same name
		for ( int i = 0; i < folders.size(); i++) {
			VirtualFolderProperties fProps = (VirtualFolderProperties)folders.elementAt(i);

			if ( fProps.getName().equals( props.getName())) {
				remove( fProps);
			}
		}

		add( props);
	}

	/**
	 * Removes a folder properties object from the project.
	 *
	 * @param props the folder properties.
	 */
	public void removeVirtualFolderProperties( VirtualFolderProperties props) {
		remove( props);
	}

	
	
} 
