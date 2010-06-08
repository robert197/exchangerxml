package com.cladonia.xml.properties;

import java.awt.Color;
import java.io.File;
import java.net.URL;

import org.dom4j.Namespace;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.Identity;
import com.cladonia.xngreditor.Main;


/**
 * This file allows the original properties to be broken up into 
 * individual files, each one with their own document which can be 
 * saved or updated as needed.
 * 
 * @author Thomas Curley
 *
 */
public class PropertiesFile extends Properties {
	
	private static final boolean DEBUG = false;
	private ExchangerDocument document	= null;	
	private boolean dirty = false;
	
	public PropertiesFile(String fileName, String rootName) {
		this(loadPropertiesFile(fileName, rootName));
	}
	
	public PropertiesFile(ExchangerDocument document) {

		super(document.getRoot());
		this.setDocument(document);
	}
	
	public PropertiesFile(ExchangerDocument document, XElement element) {

		super(element);
		this.setDocument(document);
	}
	
	public void save() {
		
		this.update();
	}
	
	public void saveToDisk() {
		try {
			if(isDirty() == true) {
				if(DEBUG) System.out.println(getDocument().getName() +" is dirty");
				//setDefaultNamespace( getDocument().getRoot());
				
				XMLUtilities.write( getDocument().getDocument(), getDocument().getURL());
				this.setDirty(false);
			}
			else {
				if(DEBUG) System.out.println(getDocument().getName() +" is not dirty");
			}
					
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setDefaultNamespace( XElement element) {
		XElement[] elements = element.getElements();
		
		for ( int i = 0; i < elements.length; i++) {
			setDefaultNamespace( elements[i]);
		}
		
		//element.setNamespace( Namespace.get( "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/"));
	}
	
	@Override
	public void set(String name, boolean value) {
		// TODO Auto-generated method stub
		super.set(name, value);
		this.setDirty(true);
	}
	
	@Override
	public void set(String name, Color value) {
		// TODO Auto-generated method stub
		super.set(name, value);
		this.setDirty(true);
	}
	
	@Override
	public void set(String name, int value) {
		// TODO Auto-generated method stub
		super.set(name, value);
		this.setDirty(true);
	}
	
	@Override
	public void set(String name, long value) {
		// TODO Auto-generated method stub
		super.set(name, value);
		this.setDirty(true);
	}
	
	@Override
	public void set(String name, String value) {
		// TODO Auto-generated method stub
		super.set(name, value);
		this.setDirty(true);
		if(DEBUG) System.out.println("Set flag to dirty for "+name+"- value: "+value);
	}
	

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public boolean isDirty() {
		return dirty;
	}
	
	
	public static ExchangerDocument loadPropertiesFile(String fileName, String rootName) {
		
		ExchangerDocument document = null;
		boolean firstTime = false;

		File dir = new File( Main.XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File( dir, fileName);
		URL url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
		
		firstTime = true;
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				firstTime = false;
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
				return null;
			}
		}
		
		XElement root = null;
		//String namespaceURI = null;
		
		if(firstTime == true) {
			//root = new XElement( rootName, "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
			root = new XElement( rootName);
			root.setText( "\n");
			document = new ExchangerDocument( url, root);
					
			//namespaceURI = root.getNamespaceURI();
		}
		else {
			root = document.getRoot();
			//namespaceURI = root.getNamespaceURI();
		}
		
		return(document);
		
	}

	public void setDocument(ExchangerDocument document) {
		this.document = document;
	}

	public ExchangerDocument getDocument() {
		return document;
	}
	
}
