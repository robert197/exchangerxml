package com.cladonia.xngreditor.properties;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.PropertiesFile;
import com.cladonia.xngreditor.Main;

public class XercesProperties extends PropertiesFile {

	public static final String XERCES_PROPERTIES_FILENAME = "xerces-properties";
	public static final String XERCES_PROPERTIES = "xerces-properties";
	
	//http://apache.org/xml/features/warn-on-duplicate-entitydef
	private static final String WARN_ON_DUPLICATE_ENTITYDEF = "warn-on-duplicate-entitydef";
	
	public XercesProperties(String fileName, String rootName) {
		super(loadPropertiesFile(fileName, rootName));
	}
	/**
	 * Creates the Xerces Properties.
	 *
	 * @param element the print preferences element.
	 */
	/*public XercesProperties(ExchangerDocument document, XElement element) {
		super(document, element);
	}*/
	
	public void setWarnOnDuplicateEntityDef( boolean enable) {
		set( WARN_ON_DUPLICATE_ENTITYDEF, enable);
	}

	public boolean isWarnOnDuplicateEntityDef() {
		return getBoolean( WARN_ON_DUPLICATE_ENTITYDEF, false);
	}
}
