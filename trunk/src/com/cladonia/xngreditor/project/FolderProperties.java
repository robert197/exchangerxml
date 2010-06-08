/*
 * $Id: FolderProperties.java,v 1.2 2005/09/05 09:08:29 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.io.File;
import java.util.Vector;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the Xml Plus configuration document.
 *
 * @version	$Revision: 1.2 $, $Date: 2005/09/05 09:08:29 $
 * @author Dogsbay
 */
public class FolderProperties extends Properties {
	public static final String FOLDER_PROPERTIES = "folder";
	public static final String NAME = "name";

	/**
	 * Creates the Folder properties object from a xml element.
	 *
	 * @param element the XML element.
	 */
	public FolderProperties( XElement element) {
		super( element);
	}

	/**
	 * Creates the Folder Properties wrapper.
	 *
	 * @param props the properties object.
	 */
	public FolderProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Creates the Folder properties object from a directory.
	 *
	 * @param dir the directory to create the folder for.
	 */
	public FolderProperties( File dir) {
		this( dir.getName());
		
		// add all the files...
		File[] files = dir.listFiles();
		
		for ( int i = 0; i < files.length; i++) {
			File file = files[i];
			
			if ( file.isDirectory()) {
				addFolderProperties( new FolderProperties( file));
			} else { // file
				addDocumentProperties( new DocumentProperties( file));
			}
		}
	}
	
	/**
	 * Creates the Folder properties object.
	 *
	 * @param name the name for the folder.
	 */
	public FolderProperties( String name) {
		super( new XElement( FOLDER_PROPERTIES));
		
		setName( name);
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
	 * Returns the virtual folder properties list.
	 *
	 * @return the virtual folder properties.
	 */
	public Vector getVirtualFolderProperties() {
		Vector result = new Vector();
		Vector list = getProperties( VirtualFolderProperties.VIRTUAL_FOLDER_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new VirtualFolderProperties( (Properties)list.elementAt(i)));
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
	public Vector getFolderProperties() {
		Vector result = new Vector();
		Vector list = getProperties( FOLDER_PROPERTIES);
		
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
	public void addFolderProperties( FolderProperties props) {
		Vector folders = getFolderProperties();

		// remove a previous folder with the same name
		for ( int i = 0; i < folders.size(); i++) {
			FolderProperties fProps = (FolderProperties)folders.elementAt(i);

			if ( fProps.getName().equals( props.getName())) {
				remove( fProps);
			}
		}

		add( props);
	}
	
	/**
	 * Adds a virtual folder properties object to the project.
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
	public void removeFolderProperties( FolderProperties props) {
		remove( props);
	}
	
	public void removeFolderProperties( VirtualFolderProperties props) {
		remove( props);
	}
} 
