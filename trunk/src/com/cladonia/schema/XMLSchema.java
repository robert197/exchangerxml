/*
 * $Id: XMLSchema.java,v 1.6 2005/08/25 10:46:39 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Namespace;
import org.dom4j.QName;
import org.exolab.castor.xml.schema.BlockList;
import org.exolab.castor.xml.schema.ElementDecl;
import org.exolab.castor.xml.schema.FinalList;
import org.exolab.castor.xml.schema.Form;
import org.exolab.castor.xml.schema.Schema;
import org.exolab.castor.xml.schema.SchemaException;
import org.exolab.castor.xml.schema.reader.SchemaReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLUtilities;

/**
 * The schema.
 *
 * @version	$Revision: 1.6 $, $Date: 2005/08/25 10:46:39 $
 * @author Dogsbay
 */
public class XMLSchema implements SchemaDocument {
	private static final boolean DEBUG = true;
	
	public static final String FORM_QUALIFIED	= "qualified";
	public static final String FORM_UNQUALIFIED	= "unqualified";

	public static final String BLOCK_ALL 			= "#all";
	public static final String BLOCK_EXTENSION 		= "extension";
	public static final String BLOCK_RESTRICTION	= "restriction";
	public static final String BLOCK_SUBSTITUTION	= "substitution";

	public static final String FINAL_ALL 			= "#all";
	public static final String FINAL_EXTENSION 		= "extension";
	public static final String FINAL_RESTRICTION	= "restriction";

	private boolean schemaDocument = false;
	
	private URL url = null;

	private Schema schema = null;
	private Hashtable globalElements = null;
	private Hashtable substitutes = null;
//	private Hashtable substitutions = null;
	private SchemaElement root = null;
	
	private Vector allElements = null;
//	private Hashtable allElements = null;
	private Vector elementNames = null;
	private Vector attributeNames = null;

	private Hashtable elementChildren = null;
	private Hashtable elementAttributes = null;

	public XMLSchema( URL url) throws IOException, SchemaException, SAXParseException { 
//		schemaDocument = isSchema;
		
		this.url = url;
		
		// check schema
		XMLUtilities.validateSchema( url);
		
		// see if it is a schema...
		SchemaReader reader = new SchemaReader( url.toString());
		reader.setValidation( false);
		this.schema = reader.read();
		
		globalElements = new Hashtable();
		substitutes = new Hashtable();

		Enumeration enumeration = schema.getGlobalElements();

//		while( enumeration.hasMoreElements()) {
//			ElementDecl e = (ElementDecl)enumeration.nextElement();
//			System.out.println( "Global: "+e.getName());
//		}

		while( enumeration.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumeration.nextElement();
			
			SchemaElement element = new SchemaElement( this, null, e);
//			System.out.println( "Global: "+element.getName());
			globalElements.put( element.getUniversalName(), element);
			
			// set the substitution group
			String substitutionGroupName = element.getElement().getSubstitutionGroup();
			
			if ( substitutionGroupName != null) {
				int colon = substitutionGroupName.lastIndexOf(':');
				
				if ( colon > 0)  {
					substitutionGroupName = substitutionGroupName.substring( colon+1);
				}
				
				Vector substitutionGroup = (Vector)substitutes.get( substitutionGroupName);
				
				if ( substitutionGroup == null) {
					substitutionGroup = new Vector();
					substitutes.put( substitutionGroupName, substitutionGroup);
				}
				
				substitutionGroup.addElement( element);
			}
			
		}

//		createNames();
	}
	
	public XMLSchema( ExchangerDocument document) throws IOException, SchemaException, SAXParseException { 
		this.url = document.getURL();
		
		byte[] bytes = document.getText().getBytes( document.getJavaEncoding());

		ByteArrayInputStream stream = new ByteArrayInputStream( bytes);
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( stream, document.getJavaEncoding()));
		String systemId = null;

		if ( url != null) {
			systemId = url.toString();
		}

//		// check schema
//		XMLUtilities.validateSchema( bufferedReader, systemId, document.getEncoding());
//
//		stream = new ByteArrayInputStream( bytes);
//		bufferedReader = new BufferedReader( new InputStreamReader( stream, document.getJavaEncoding()));

		InputSource source = new InputSource( bufferedReader);
		source.setSystemId( systemId);

		// see if it is a schema...
		SchemaReader reader = new SchemaReader( source);
		reader.setValidation( false);
		
		this.schema = reader.read();
		
		globalElements = new Hashtable();
		substitutes = new Hashtable();

		Enumeration enumeration = schema.getGlobalElements();

		while( enumeration.hasMoreElements()) {
			ElementDecl e = (ElementDecl)enumeration.nextElement();
			
			SchemaElement element = new SchemaElement( this, null, e);
			globalElements.put( element.getUniversalName(), element);
			
			// set the substitution group
			String substitutionGroupName = element.getElement().getSubstitutionGroup();
			
			if ( substitutionGroupName != null) {
				int colon = substitutionGroupName.lastIndexOf(':');
				
				if ( colon > 0)  {
					substitutionGroupName = substitutionGroupName.substring( colon+1);
				}
				
				Vector substitutionGroup = (Vector)substitutes.get( substitutionGroupName);
				
				if ( substitutionGroup == null) {
					substitutionGroup = new Vector();
					substitutes.put( substitutionGroupName, substitutionGroup);
				}
				
				substitutionGroup.addElement( element);
			}
			
		}
	}

	
	public Schema getSchema()
	{
	  return this.schema;
	
	}
	/**
	 * Gets a list of globally defined elements.
	 *
	 * @return list of global elements.
	 */
	public Vector getGlobalElements() {
		Enumeration enumeration = globalElements.elements();
		Vector elements = new Vector();
		
		while ( enumeration.hasMoreElements()) {
			elements.addElement( enumeration.nextElement());
		}

		return elements;
	}

	/**
	 * Gets a globally defined element.
	 *
	 * @return the global element.
	 */
	public SchemaElement getGlobalElement( SchemaElement element) {
		if ( element != null) {
			return (SchemaElement)globalElements.get( element.getUniversalName());
		}
		
		return null;
	}

	/**
	 * Gets a list of elements that are in the same substitution group.
	 *
	 * @return the list of elements.
	 */
	public Vector getSubstitutionGroup( String name) {
		return (Vector)substitutes.get( name);
	}

	/**
	 * Gets a list of globally defined elements.
	 *
	 * @return list of global elements.
	 */
	public void write( Writer writer) throws SAXException, IOException {
		org.exolab.castor.xml.dtd.Converter.marshalSchema( schema, writer);
	}

	/**
	 * Returns the current schema/dtd url. 
	 *
	 * @return the url.
	 */
	public URL getURL() {
		return url;
	}
	
	public int getType() {
		return TYPE_XSD;
	}

	/**
	 * Returns true if the document used to create 
	 * this grammar was a schema.
	 *
	 * @return true when the document was a schema.
	 */
	public boolean isSchema() {
		return schemaDocument;
	}

	/**
	 * Returns a list of all the possible element names. 
	 *
	 * @return list of element names and namespace uris.
	 */
	public Vector getElementNames() {
		return elementNames;
	}

	/**
	 * Returns a list of all the possible attribute names. 
	 *
	 * @return list of attribute names and namespace uris.
	 */
	public Vector getAttributeNames() {
		return attributeNames;
	}

	/**
	 * Returns a list of possible element names for the parent element name. 
	 *
	 * @return list of element names and namespace uris for a parent.
	 */
	public Vector getElementNames( String parent) {
		return (Vector)elementChildren.get( parent);
	}

	/**
	 * Returns a list of all the possible attribute names, for a parent element. 
	 *
	 * @return list of attribute names and namespace uris for a parent element.
	 */
	public Vector getAttributeNames( String parent) {
		return (Vector)elementAttributes.get( parent);
	}

	/**
	 * Returns a list of possible element names. 
	 *
	 * @return list of element names and namespace uris.
	 */
	public Hashtable getElementChildren() {
		return elementChildren;
	}

	/**
	 * Returns a list of all the possible attribute names, for a parent element. 
	 *
	 * @return list of attribute names and namespace uris for a parent element.
	 */
	public Hashtable getElementAttributes() {
		return elementAttributes;
	}

	/**
	 * Gets the root element, if the root element was set.
	 *
	 * @return the root element.
	 */
//	public SchemaElement getRootElement() {
//		return root;
//	}

	/**
	 * returns true when the schema has global elements.
	 *
	 * @return true when the schema has global elements.
	 */
	public boolean hasGlobalElements() {
		return globalElements.size() > 0;
	}

	/**
	 * Gets a list of referers to the element.
	 *
	 * @param element the elemen to get the referers for.
	 *
	 * @return list of referencing elements.
	 */
	public Vector getReferers( SchemaElement element) {
		Vector referers = new Vector();
		
		Enumeration enumeration = globalElements.elements();
		while ( enumeration.hasMoreElements()) {
			SchemaElement elem = (SchemaElement)enumeration.nextElement();
			
			getReferers( elem, referers, element);
		}
		
		return referers;
	}

	/**
	 * Gets a list of referers to the attribute.
	 *
	 * @param attribute the attribute to get the referers for.
	 *
	 * @return list of referencing elements.
	 */
	public Vector getReferers( SchemaAttribute attribute) {
		Vector referers = new Vector();
		
		Enumeration enumeration = globalElements.elements();
		while ( enumeration.hasMoreElements()) {
			SchemaElement elem = (SchemaElement)enumeration.nextElement();
			
			getReferers( elem, referers, attribute);
		}
		
		return referers;
	}

	/**
	 * Gets a list of substitues for the element.
	 *
	 * @param name the substitution group name to get the 
	 * substitutes for.
	 *
	 * @return list of substitution elements.
	 */
//	public Vector getSubstitutes( String name) {
//		System.out.println("XMLSchema.getSubstitutes( "+name+")");
//		return (Vector)substitutions.get( name);
//	}

	/**
	 * Check to see if the element is global.
	 *
	 * @param element the element to check.
	 *
	 * @return true when the element is a global element.
	 */
	public boolean isGlobal( SchemaElement element) {
		
		return globalElements.get( element.getUniversalName()) != null;
	}

	/**
	 * Get the id field value.
	 *
	 * @return the value for the id field.
	 */
	public String getId() {
		return schema.getId();
	}

	/**
	 * Get the default attribute form field value.
	 *
	 * @return the default attribute form value field.
	 */
	public String getAttributeFormDefault() {
		if ( schema.getAttributeFormDefault() == Form.Qualified) {
			return FORM_QUALIFIED;
		} else {
			return FORM_UNQUALIFIED;
		}
	}

	/**
	 * Get the default block field value.
	 *
	 * @return the default block field value.
	 */
	public String getBlockDefault() {
		String result = null;
		BlockList list = schema.getBlockDefault();
		
		if ( list != null) {
			if ( list.hasAll()) {
				result = BLOCK_ALL;
			} else if ( list.hasExtension()) {
				result = BLOCK_EXTENSION;
			} else if ( list.hasSubstitution()) {
				result = BLOCK_SUBSTITUTION;
			} else if ( list.hasRestriction()) {
				result = BLOCK_RESTRICTION;
			}
		}
		
		return result;
	}

	/**
	 * Get the default element form field value.
	 *
	 * @return the default element form value field.
	 */
	public String getElementFormDefault() {
		if ( schema.getElementFormDefault() == Form.Qualified) {
			return FORM_QUALIFIED;
		} else {
			return FORM_UNQUALIFIED;
		}
	}

	/**
	 * Get the default block field value.
	 *
	 * @return the default block field value.
	 */
	public String getFinalDefault() {
		FinalList list = schema.getFinalDefault();
		String result = null;
		
		if ( list != null) {
			if ( list.hasAll()) {
				result = FINAL_ALL;
			} else if ( list.hasExtension()) {
				result = FINAL_EXTENSION;
			} else if ( list.hasRestriction()) {
				result = FINAL_RESTRICTION;
			}
		}
		
		return result;
	}

	public Vector getAnyElements() {
		return null;
	}

	/**
	 * Get the target namespace value.
	 *
	 * @return the target namespace value.
	 */
	public String getTargetNamespace() {
		return schema.getTargetNamespace();
	}

	/**
	 * Get the version value.
	 *
	 * @return the version value.
	 */
	public String getVersion() {
		return schema.getVersion();
	}
	
	/*
	 * Get the reference element.
	 *
	 * @return the reference element.
	 */
//	SchemaElement getReference( ElementDecl e) {
//		for ( int i = 0; i < globalElements.size(); i++) {
//			SchemaElement ref = (SchemaElement)globalElements.elementAt(i);
//			
//			if ( e ==  ref.getElement()) {
//				return ref;
//			}
//		}
//	
//		return null;
//	}

	public Vector getElements() {
		return getAllElements();
	}

	/**
	 * Returns a list of all elements.
	 */
	public Vector getAllElements() {
//		System.out.println( ">>> XMLSchema.getAllElements()");
		long time = System.currentTimeMillis();
		
		if ( allElements == null) {
			Enumeration globals = globalElements.elements();
			allElements = new Vector();
			
			while ( globals.hasMoreElements()) {
				SchemaElement elem = (SchemaElement)globals.nextElement();
				
				getAllElements( elem);
			}
		}
		
		Enumeration enumeration = allElements.elements();
		Vector elements = new Vector();
		
		while ( enumeration.hasMoreElements()) {
			elements.addElement( enumeration.nextElement());
		}

//		System.out.println( "<<< XMLSchema.getAllElements() ["+(System.currentTimeMillis()-time)+"]");
		return elements;
	}
	
	private boolean addElement( SchemaElement element) {
//		ElementModel model = (ElementModel)allElements.get( element.getUniversalName());
		
//		if ( model != null) { // element already added
//			model.merge( element);
//			return false;
		if ( !allElements.contains( element)) {
			allElements.addElement( element);
			return true;
		}

		return false;
	}

	private void getAllElements( SchemaElement element) {
//		System.out.println( ">>> XMLSchema.getAllElements( "+element+")");

		if ( addElement( element)) {
			Vector elements = element.getChildElements();
			
			if ( elements != null) {
				for( int i = 0; i < elements.size(); i++) {
					SchemaElement elem = ((SchemaElement)elements.elementAt(i));
					
//					if ( !elem.isReference() && !globalElements.containsKey( elem.getUniversalName())) {
					if ( !elem.isReference()) {
						getAllElements( elem);
					}
				}
			}
		}

//		System.out.println( "<<< XMLSchema.getAllElements( "+element+")");
	}

	/**
	 * Creates a list of attribute and element names.
	 */
	private void createNames() {
//		elementChildren = new Hashtable();
//		elementAttributes = new Hashtable();
//
//		elementNames = new Vector();
//		attributeNames = new Vector();
//		
//		for ( int i = 0; i < globalElements.size(); i++) {
//			SchemaElement elem = (SchemaElement)globalElements.elementAt( i);
//			
//			getNames( elem, elementNames, attributeNames, elem.getName(), elementChildren, elementAttributes);
//		}
	}

	private void getNames( SchemaElement element, Vector elementNames, Vector attributeNames, String parentElementName, Hashtable elementChildren, Hashtable elementAttributes) {
		Vector attributes = element.getAttributes();
		Vector childAttributes = (Vector)elementAttributes.get( parentElementName);
		
		if ( childAttributes == null) {
			childAttributes = new Vector();
			elementAttributes.put( parentElementName, childAttributes);
		}
		
		QName ename = QName.get( element.getName(), element.getNamespace());
		if ( !elementNames.contains( ename)) {
			elementNames.addElement( ename);
		}

		if ( attributes != null) {
			for( int i = 0; i < attributes.size(); i++) {
				SchemaAttribute attribute = (SchemaAttribute)attributes.elementAt(i);
				QName aname = QName.get( attribute.getName(), attribute.getNamespace());
				
				if ( !attributeNames.contains( aname)) {
					attributeNames.addElement( aname);
				}

				if ( !childAttributes.contains( aname)) {
					childAttributes.addElement( aname);
				}
			}
		}

		Vector models = element.getModels();
		
		if ( models != null) {
			for( int i = 0; i < models.size(); i++) {
				getNames( (SchemaModel)models.elementAt(i), elementNames, attributeNames, parentElementName, elementChildren, elementAttributes);
			}
		}
	}

	private void getNames( SchemaModel model, Vector elementNames, Vector attributeNames, String parentElementName, Hashtable elementChildren, Hashtable elementAttributes) {
		Vector models = model.getModels();
		
		if ( models != null) {
			for( int i = 0; i < models.size(); i++) {
				getNames( (SchemaModel)models.elementAt(i), elementNames, attributeNames, parentElementName, elementChildren, elementAttributes);
			}
		}

		Vector elements = model.getElements();
		Vector childElements = (Vector)elementChildren.get( parentElementName);
		
		if ( childElements == null) {
			childElements = new Vector();
			elementChildren.put( parentElementName, childElements);
		}
		
		if ( elements != null) {
			for( int i = 0; i < elements.size(); i++) {
				SchemaElement element = ((SchemaElement)elements.elementAt(i));
				boolean isGlobal = element.isReference();
				
				if ( isGlobal) {
					// resolve the reference to be used here but don't 
					// continue with it...
					element.resolveReference();
				}

//				System.out.println( ">>> XMLSchema.getNames()");		
				Vector subs = element.getSubstituteElements();
//				System.out.println( "<<< XMLSchema.getNames()");		

				if ( subs.size() > 0) {
					for ( int j = 0; j < subs.size(); j++) {
						SchemaElement sub = (SchemaElement)subs.elementAt(j);

						QName ename = QName.get( sub.getName(), sub.getNamespace());
						if ( !childElements.contains( ename)) {
//							System.out.println( "XMLSchema add substituteElement ["+sub.getName()+"]");		
							childElements.addElement( ename);
						}
					}
				} else {
					QName ename = QName.get( element.getName(), element.getNamespace());
					if ( !childElements.contains( ename)) {
						childElements.addElement( ename);
					}
				}
				
				if ( !isGlobal) {
					// the element is part of the global elements ...
					getNames( element, elementNames, attributeNames, element.getName(), elementChildren, elementAttributes);
				}
			}
		}
	}


	private static void getReferers( SchemaElement parent, Vector referers, SchemaAttribute ref) {
		Vector children = parent.getChildren();
		
		for ( int i = 0; i < children.size(); i++) {
			SchemaModel model = (SchemaModel)children.elementAt(i);
			
			getReferers( model, referers, ref);
		}
		
		
		Vector attributes = parent.getAttributes();
		
		if ( attributes != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				SchemaAttribute attribute = (SchemaAttribute)attributes.elementAt(i);

				if ( attribute.equals( ref)) {
					if ( !inList( referers, parent)) {
						referers.addElement( parent);
					}
					// no point checking the other attributes
					return;
				}
			}
		}
	}

	private static void getReferers( SchemaModel model, Vector referers, SchemaAttribute ref) {
		Vector children = model.getChildren();
		
		for ( int i = 0; i < children.size(); i++) {
			SchemaParticle node = (SchemaParticle)children.elementAt(i);
			
			if ( node instanceof SchemaModel) {
				getReferers( (SchemaModel)node, referers, ref);
			} else if ( node instanceof SchemaElement) {
				getReferers( (SchemaElement)node, referers, ref);
			} // else wildcard....
		}
	}

	private static void getReferers( SchemaElement root, Vector referers, SchemaElement ref) {
		Vector children = root.getChildren();
		
		for ( int i = 0; i < children.size(); i++) {
			SchemaModel model = (SchemaModel)children.elementAt(i);
			
			getReferers( model, referers, ref);
		}
		
		if ( root.equals( ref)) {
			SchemaElement parent = root.getParentElement();
			
			if ( parent != null) {
				if ( !inList( referers, parent)) {
					referers.addElement( parent);
				}
			}
		}
	}

	private static void getReferers( SchemaModel model, Vector referers, SchemaElement ref) {
		Vector children = model.getChildren();
		
		for ( int i = 0; i < children.size(); i++) {
			SchemaParticle node = (SchemaParticle)children.elementAt(i);
			
			if ( node instanceof SchemaModel) {
				getReferers( (SchemaModel)node, referers, ref);
			} else if ( node instanceof SchemaElement) {
				getReferers( (SchemaElement)node, referers, ref);
			} // else wildcard....
		}
	}
	
	private static boolean inList( Vector list, Object object) {
		if ( list != null) {
			for ( int i = 0; i < list.size(); i++) {
				if ( list.elementAt(i).equals( object)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void updatePrefixes( Vector declarations, ElementInformation element) {
		Vector attributes = element.getAttributes();
		Vector children = element.getChildElements();

		for ( int i = 0; i < children.size(); i++) {
			ElementInformation child = (ElementInformation)children.elementAt(i);
			
			for ( int k = 0; k < declarations.size(); k++) {
				Namespace ns = (Namespace)declarations.elementAt(k);

				if ( ns.getURI().equals( child.getNamespace())) {// && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
					child.setPrefix( ns.getPrefix());
					break;
				}
			}
		}

		if ( attributes != null) {
			for ( int j = 0; j < attributes.size(); j++) {
				AttributeInformation attribute = (AttributeInformation)attributes.elementAt(j);
				
				for ( int k = 0; k < declarations.size(); k++) {
					Namespace ns = (Namespace)declarations.elementAt(k);

					if ( ns.getURI().equals( attribute.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
						attribute.setPrefix( ns.getPrefix());
						break;
					}
				}
			}
		}
	}
	
	public void updatePrefixes( Vector declarations) {
		Vector allElements = getAllElements();
		
		for ( int i = 0; i < allElements.size(); i++) {
			ElementInformation model = (ElementInformation)allElements.elementAt(i);
			Vector attributes = model.getAttributes();
			Vector children = model.getChildElements();
			
			if ( children != null) {
				for ( int j = 0; j < children.size(); j++) {
					ElementInformation child = (ElementInformation)children.elementAt(j);
					
					updatePrefixes( declarations, child);
					
					for ( int k = 0; k < declarations.size(); k++) {
						Namespace ns = (Namespace)declarations.elementAt(k);

						if ( ns.getURI().equals( child.getNamespace())) {// && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
							child.setPrefix( ns.getPrefix());
							break;
						}
					}
				}
			}
			
			if ( attributes != null) {
				for ( int j = 0; j < attributes.size(); j++) {
					AttributeInformation attribute = (AttributeInformation)attributes.elementAt(j);
					
					for ( int k = 0; k < declarations.size(); k++) {
						Namespace ns = (Namespace)declarations.elementAt(k);

						if ( ns.getURI().equals( attribute.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
							attribute.setPrefix( ns.getPrefix());
							break;
						}
					}
				}
			}
			
			for ( int j = 0; j < declarations.size(); j++) {
				Namespace ns = (Namespace)declarations.elementAt(j);

				if ( ns.getURI().equals( model.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
					model.setPrefix( ns.getPrefix());
					break;
				}
			}
		}
	}
} 
