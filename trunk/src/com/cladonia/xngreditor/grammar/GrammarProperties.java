/*
 * $Id: GrammarProperties.java,v 1.13 2004/11/05 12:29:26 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.poi.hssf.record.CalcCountRecord;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.dom4j.DocumentType;
import org.dom4j.Namespace;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLGrammar;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xngreditor.Identity;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * Handles the properties for the Text Editor.
 *
 * @version	$Revision: 1.13 $, $Date: 2004/11/05 12:29:26 $
 * @author Dogsbay
 */
public class GrammarProperties extends Properties { // implements XMLGrammar {
	
	private static final boolean DEBUG	= false;

	private static final String ID		= "id";

	public static final String GRAMMAR_PROPERTIES	= "grammar-properties";

	private static final String SCENARIO			= "scenario";
	private static final String DEFAULT_SCENARIO	= "default-scenario";

	private static final String DEFAULT_NAMED_XPATH	= "default-named-xpath";

	private static final String DESCRIPTION			= "description";
	private static final String EXTENSIONS			= "extensions";

	private static final String ROOT_ELEMENT		= "root-element";
	private static final String NAMESPACE			= "namespace";
	private static final String NAMESPACE_PREFIX	= "namespace-prefix";
	private static final String PUBLIC_ID			= "public-ID";
	private static final String SYSTEM_ID			= "system-ID";

	// validation
	private static final String ICON_LOCATION		= "icon-location";

	// validation
	private static final String VALIDATION_LOCATION			= "validation-location";
	private static final String USE_XML_VALIDATION_LOCATION	= "use-xml-validation-location"; // use validation location as declared in XML file for validation
	private static final String VALIDATION_GRAMMAR_TYPE		= "validation-grammar"; // use validation location as declared in XML file for validation

	// template
//	private static final String TEMPLATE_GRAMMAR_TYPE	= "template-grammar"; // use validation location as declared in XML file for validation
//	private static final String TEMPLATE_LOCATION		= "template-location";
	private static final String BASE_LOCATION			= "base-location";

	private static final String SCHEMA_LOCATION			= "schema-location";

	private ConfigurationProperties properties = null;

	/**
	 * Constructor for the garmmar properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the grammar-type.
	 */
//	public GrammarProperties( ConfigurationProperties props, XElement element) {
//		super( element);
//		
//		this.properties = props;
//	}

	/**
	 * Constructor for the grammar properties, 
	 * constructed from an imported <type> element.
	 *
	 * @param type the imported type element.
	 */
	public GrammarProperties( ConfigurationProperties properties, URL url, XElement type) {
		super( new XElement( GRAMMAR_PROPERTIES));

		this.properties = properties;
		
		importType( url, type);
	}
	
	/**
	 * Constructor for the garmmar properties.
	 *
	 * @param props the higher level properties object.
	 */
	public GrammarProperties( ConfigurationProperties properties, Properties props) {
		super( props.getElement());

		this.properties = properties;
	}

	/**
	 * Constructor for a new grammar properties object.
	 */
	public GrammarProperties( ConfigurationProperties props) {
		super( new XElement( GRAMMAR_PROPERTIES));
		
		this.properties = props;
	}

	/**
	 * Constructor for the grammar properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the editor.
	 */
	public GrammarProperties( ConfigurationProperties props, ExchangerDocument document) {
		super( new XElement( GRAMMAR_PROPERTIES));
		
		this.properties = props;
		
		if ( document != null) {

			String name = document.getName();
			int dotPos = name.lastIndexOf( '.');
			
			if ( dotPos != -1) {
				setExtensions( name.substring( dotPos+1, name.length()));
			}
			
			if ( !document.isError()) {
				XElement root = document.getRoot();
				
				if ( root != null) {
					setRootElementName( root.getName());
					setNamespace( root.getNamespaceURI());
					setNamespacePrefix( root.getNamespacePrefix());
				}
				
				List namespaces = root.additionalNamespaces();
				
				for ( int i = 0; i < namespaces.size(); i++) {
					Namespace namespace = (Namespace)namespaces.get(i);
					addNamespace( new NamespaceProperties( namespace.getURI(), namespace.getPrefix()));
				}

				DocumentType docType = document.getDocument().getDocType();

				if ( docType != null) {
					
					setPublicID( docType.getPublicID());
					setSystemID( docType.getSystemID());
					setValidationLocation( docType.getSystemID());
					setUseXMLValidationLocation( true);
					
					addTagCompletion( new TagCompletionProperties( docType.getSystemID(), XMLGrammar.TYPE_DTD));

//					setTemplateLocation( docType.getSystemID());
//					setTemplateGrammar( XMLGrammar.TYPE_DTD);

					setValidationGrammar( XMLGrammar.TYPE_DTD);
				} else {
					String location = root.getAttribute( "schemaLocation");
					
					if ( location == null) {
						location = root.getAttribute( "noNamespaceSchemaLocation");
					}
					
					if ( location != null) {
						StringTokenizer tokenizer = new StringTokenizer( location, " \t\n\r\f");
						String targetURI = null;
						
						// if there is only one token than use this as the uri
						if ( tokenizer.hasMoreTokens()) {
							targetURI = tokenizer.nextToken();
						}
						
						// there is more than one token, use this token as the uri
						if ( tokenizer.hasMoreTokens()) {
							targetURI = tokenizer.nextToken();
						}
						
						setValidationLocation( targetURI);

						addTagCompletion( new TagCompletionProperties( targetURI, XMLGrammar.TYPE_XSD));

						setUseXMLValidationLocation( true);
						setValidationGrammar( XMLGrammar.TYPE_XSD);
					}
				}
				
				setDescription( getRootElementName());
			} else {
				setDescription( getExtensions());
			}
		}
	}
	
	public void importType( URL url, XElement element) {
		setDescription( getAttributeValue( element, "name"));
		setExtensions( getAttributeValue( element, "extensions"));
		setPublicID( getAttributeValue( element, "publicID"));
		setSystemID( getAttributeValue( element, "systemID"));
		setIconLocation( URLUtilities.resolveURL( url, getAttributeValue( element, "icon")));
		
		XElement rootElement = element.getElement( "root");
		if ( rootElement != null) {
			setRootElementName( getAttributeValue( rootElement, "name"));

			setNamespace( getAttributeValue( rootElement, "namespace", "uri"));
			setNamespacePrefix( getAttributeValue( rootElement, "namespace", "prefix"));
		}
		
		Vector nss = getNamespaces();
		for ( int i = 0; i < nss.size(); i++) {
			removeNamespace( (NamespaceProperties)nss.elementAt(i));
		}

		XElement[] namespaces = element.getElements( "namespace");
		for ( int i = 0; i < namespaces.length; i++) {
			addNamespace( new NamespaceProperties( namespaces[i].getAttribute( "uri"), namespaces[i].getAttribute( "prefix")));
		}

		Vector frags = getFragments();
		for ( int i = 0; i < frags.size(); i++) {
			removeFragment( (FragmentProperties)frags.elementAt(i));
		}

		XElement[] fragments = element.getElements( "fragment");
		for ( int i = 0; i < fragments.length; i++) {
			FragmentProperties fragment = new FragmentProperties();

			fragment.setContent( fragments[i].getValue());
			fragment.setBlock( fragments[i].getAttribute( "block").equals( "true"));
			fragment.setName( fragments[i].getAttribute( "name"));
			fragment.setOrder( Integer.parseInt( fragments[i].getAttribute( "order")));
			fragment.setIcon( URLUtilities.resolveURL( url, fragments[i].getAttribute( "icon")));
			fragment.setKey( fragments[i].getAttribute( "key"));
			
			addFragment( fragment);
		}

		Vector xpaths = getNamedXPaths();
		for ( int i = 0; i < xpaths.size(); i++) {
			removeNamedXPath( (NamedXPathProperties)xpaths.elementAt(i));
		}

		XElement[] namedXPaths = element.getElements( "xpath");
		for ( int i = 0; i < namedXPaths.length; i++) {
			NamedXPathProperties xpath = new NamedXPathProperties();

			xpath.setShowElementNames( namedXPaths[i].getAttribute( "showElementNames").equals( "true"));
			xpath.setShowAttributeNames( namedXPaths[i].getAttribute( "showAttributeNames").equals( "true"));
			xpath.setShowAttributes( namedXPaths[i].getAttribute( "showAttributes").equals( "true"));
			xpath.setShowElementContent( namedXPaths[i].getAttribute( "showElementContent").equals( "true"));
			xpath.setName( namedXPaths[i].getAttribute( "name"));
			xpath.setXPath( namedXPaths[i].getAttribute( "xpath"));
			
			if ( namedXPaths[i].getAttribute( "default").equals( "true")) { 
				setDefaultNamedXPath( xpath);
			}

			addNamedXPath( xpath);
		}

		// tag-completion || template
		Vector temps = getTagCompletionPropertiesList();
		for ( int i = 0; i < temps.size(); i++) {
			removeTagCompletion( (TagCompletionProperties)temps.elementAt(i));
		}

		XElement[] templates = element.getElements( "template");
		for ( int i = 0; i < templates.length; i++) {
			TagCompletionProperties tagCompletion = new TagCompletionProperties();

			tagCompletion.setLocation( URLUtilities.resolveURL( url, templates[i].getAttribute( "src")));
			tagCompletion.setType( convertTemplateType( templates[i].getAttribute( "type")));

			addTagCompletion( tagCompletion);
		}

		// validation
		setValidationGrammar( convertType( getAttributeValue( element, "validation", "type")));
		setValidationLocation( URLUtilities.resolveURL( url, getAttributeValue( element, "validation", "src")));
		
		String attribute = getAttributeValue( element, "validation", "useDocumentLocation");

		boolean validationUseLocationInDocument = false;
		
		if ( attribute != null) {
			validationUseLocationInDocument = attribute.equals( "true");
		}
		
		setUseXMLValidationLocation( validationUseLocationInDocument);

		setSchemaLocation( URLUtilities.resolveURL( url, getAttributeValue( element, "schema", "src")));
	}

	public XElement exportType( URL url) { // throws IOException, SAXParseException {
		//String namespace = "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/";
		//XElement type = new XElement( "type", namespace);
		XElement type = new XElement( "type");
		addAttribute( type, "name", getDescription());
		addAttribute( type, "icon", URLUtilities.getRelativePath( url, getIconLocation()));
		addAttribute( type, "publicID", getPublicID());
		addAttribute( type, "systemID", getSystemID());
		addAttribute( type, "extensions", getExtensions());

		XElement rootElement = addElement( type, "root", null);
		addAttribute( rootElement, "name", getRootElementName());

		XElement namespaceElement = addElement( rootElement, "namespace", null);
		addAttribute( namespaceElement, "prefix", getNamespacePrefix());
		addAttribute( namespaceElement, "uri", getNamespace());
		
		Vector namespaces = getNamespaces();
		for ( int i = 0; i < namespaces.size(); i++) {
			NamespaceProperties namespaceProps = (NamespaceProperties)namespaces.elementAt(i);

			XElement namespaceElem = addElement( type, "namespace", null);
			addAttribute( namespaceElem, "prefix", namespaceProps.getPrefix());
			addAttribute( namespaceElem, "uri", namespaceProps.getURI());
		}

		Vector fragments = getFragments();
		for ( int i = 0; i < fragments.size(); i++) {
			FragmentProperties fragment = (FragmentProperties)fragments.elementAt(i);

			XElement fragmentElem = addElement( type, "fragment", null);

			addAttribute( fragmentElem, "block", ""+fragment.isBlock());
			addAttribute( fragmentElem, "name", fragment.getName());
			addAttribute( fragmentElem, "key", fragment.getKey());
			addAttribute( fragmentElem, "order", ""+fragment.getOrder());
			addAttribute( fragmentElem, "icon", URLUtilities.getRelativePath( url, fragment.getIcon()));

			fragmentElem.setValue( fragment.getContent());
		}

		Vector xpaths = getNamedXPaths();
		for ( int i = 0; i < xpaths.size(); i++) {
			NamedXPathProperties xpath = (NamedXPathProperties)xpaths.elementAt(i);

			XElement xpathElem = addElement( type, "xpath", null);

			addAttribute( xpathElem, "name", xpath.getName());
			addAttribute( xpathElem, "xpath", xpath.getXPath());
			
			if ( getDefaultNamedXPath() != null && getDefaultNamedXPath().getID().equals( xpath.getID())) { 
				addAttribute( xpathElem, "default", "true");
			} else {
				addAttribute( xpathElem, "default", "false");
			}

			addAttribute( xpathElem, "showElementNames", ""+xpath.showElementNames());
			addAttribute( xpathElem, "showElementContent", ""+xpath.showElementContent());
			addAttribute( xpathElem, "showAttributes", ""+xpath.showAttributes());
			addAttribute( xpathElem, "showAttributeNames", ""+xpath.showAttributeNames());
		}

		Vector tagCompletionList = getTagCompletionPropertiesList();
		for ( int i = 0; i < tagCompletionList.size(); i++) {
			TagCompletionProperties props = (TagCompletionProperties)tagCompletionList.elementAt(i);

			XElement templateElement = addElement( type, "template", null);
			addAttribute( templateElement, "src", URLUtilities.getRelativePath( url, props.getLocation()));
			addAttribute( templateElement, "type", convertType( props.getType()));
		}

		XElement validationElement = addElement( type, "validation", null);
		addAttribute( validationElement, "src", URLUtilities.getRelativePath( url, getValidationLocation()));
		addAttribute( validationElement, "type", convertType( getValidationGrammar()));
		addAttribute( validationElement, "useDocumentLocation", ""+useXMLValidationLocation());
		
		XElement schemaElement = addElement( type, "schema", null);
		addAttribute( schemaElement, "src", URLUtilities.getRelativePath( url, getSchemaLocation()));

		return type;
	}

	public void update( GrammarProperties props) {
		setDefaultScenario( props.getDefaultScenario());
		setDescription( props.getDescription());
		setExtensions( props.getExtensions());
		setNamespace( props.getNamespace());
		setNamespacePrefix( props.getNamespacePrefix());
		setPublicID( props.getPublicID());
		setSystemID( props.getSystemID());
		setRootElementName( props.getRootElementName());
		setUseXMLValidationLocation( props.useXMLValidationLocation());
		setValidationGrammar( props.getValidationGrammar());
		setValidationLocation( props.getValidationLocation());
		setSchemaLocation( props.getSchemaLocation());
		
		Vector namespaces = getNamespaces();
		for ( int i = 0; i < namespaces.size(); i++) {
			removeNamespace( (NamespaceProperties)namespaces.elementAt(i));
		}

		namespaces = props.getNamespaces();
		for ( int i = 0; i < namespaces.size(); i++) {
			addNamespace( (NamespaceProperties)namespaces.elementAt(i));
		}

		Vector scenarios = getScenarios();
		for ( int i = 0; i < scenarios.size(); i++) {
			removeScenario( (ScenarioProperties)scenarios.elementAt(i));
		}

		scenarios = props.getScenarios();
		for ( int i = 0; i < scenarios.size(); i++) {
			addScenario( (ScenarioProperties)scenarios.elementAt(i));
		}

		Vector fragments = getFragments();
		for ( int i = 0; i < fragments.size(); i++) {
			removeFragment( (FragmentProperties)fragments.elementAt(i));
		}

		fragments = props.getFragments();
		for ( int i = 0; i < fragments.size(); i++) {
			addFragment( (FragmentProperties)fragments.elementAt(i));
		}

		Vector xpaths = getNamedXPaths();
		for ( int i = 0; i < xpaths.size(); i++) {
			removeNamedXPath( (NamedXPathProperties)xpaths.elementAt(i));
		}

		xpaths = props.getNamedXPaths();
		for ( int i = 0; i < xpaths.size(); i++) {
			addNamedXPath( (NamedXPathProperties)xpaths.elementAt(i));
		}

		Vector tagCompletionList = getTagCompletionPropertiesList();
		for ( int i = 0; i < tagCompletionList.size(); i++) {
			removeTagCompletion( (TagCompletionProperties)tagCompletionList.elementAt(i));
		}

		tagCompletionList = props.getTagCompletionPropertiesList();
		for ( int i = 0; i < tagCompletionList.size(); i++) {
			addTagCompletion( (TagCompletionProperties)tagCompletionList.elementAt(i));
		}
	}

	/**
	 * Return the ID.
	 *
	 * @return the identifier of this scenario.
	 */
	public String getID() {
		String result = getText( ID);
		
		if ( result == null || result.length() < 2) {
			Date date = new Date();
			set( ID, "GP"+date.getTime()+getDescription().hashCode());
			
			result = getText( ID);
		}

		return result;
	}
	
	/**
	 * Return the public ID.
	 *
	 * @return the public identifier.
	 */
	public String getPublicID() {
		return getText( PUBLIC_ID);
	}

	/**
	 * Set the public ID.
	 *
	 * @param publicId the public identifier.
	 */
	public void setPublicID( String publicId) {
		set( PUBLIC_ID, publicId);
	}

	/**
	 * Return the system ID.
	 *
	 * @return the system identifier.
	 */
	public String getSystemID() {
		return getText( SYSTEM_ID);
	}

	/**
	 * Set the system ID.
	 *
	 * @param systemId the system identifier.
	 */
	public void setSystemID( String systemId) {
		set( SYSTEM_ID, systemId);
	}

	/**
	 * Return true when the validation url in the document should be used.
	 *
	 * @return true when the validation location in the document should be used.
	 */
	public boolean useXMLValidationLocation() {
		return getBoolean( USE_XML_VALIDATION_LOCATION, true);
	}

	/**
	 * Set true when the xml should be validated with the url in the document
	 *
	 * @param enabled true when the location in the document should be used.
	 */
	public void setUseXMLValidationLocation( boolean enabled) {
		set( USE_XML_VALIDATION_LOCATION, enabled);
	}

	/**
	 * Return the root element namespace.
	 *
	 * @return the root namespace.
	 */
	public String getNamespace() {
		return getText( NAMESPACE);
	}

	/**
	 * Set the root namespace.
	 *
	 * @param namespace the root namespace.
	 */
	public void setNamespace( String namespace) {
		set( NAMESPACE, namespace);
	}

	/**
	 * Return the root element namespace prefix.
	 *
	 * @return the root namespace prefix.
	 */
	public String getNamespacePrefix() {
		return getText( NAMESPACE_PREFIX);
	}

	/**
	 * Set the root namespace prefix.
	 *
	 * @param prefix the root namespace prefix.
	 */
	public void setNamespacePrefix( String prefix) {
		set( NAMESPACE_PREFIX, prefix);
	}

	/**
	 * Return the validation grammar type.
	 *
	 * @return the validation grammar type.
	 */
	public int getValidationGrammar() {
		return getInteger( VALIDATION_GRAMMAR_TYPE, XMLGrammar.TYPE_XSD);
	}

	/**
	 * Set the validation grammar type.
	 *
	 * @param type the validation grammar type.
	 */
	public void setValidationGrammar( int type) {
		set( VALIDATION_GRAMMAR_TYPE, type);
	}

	/**
	 * Return the icon location.
	 *
	 * @return the icon location.
	 */
	public String getIconLocation() {
		return getText( ICON_LOCATION);
	}

	/**
	 * Set the icon location.
	 *
	 * @param location the icon location.
	 */
	public void setIconLocation( String location) {
		set( ICON_LOCATION, location);
	}

	/**
	 * Return the validation grammar location.
	 *
	 * @return the validation grammar location.
	 */
	public String getValidationLocation() {
		return getText( VALIDATION_LOCATION);
	}

	/**
	 * Set the validation grammar location.
	 *
	 * @param location the validation grammar location.
	 */
	public void setValidationLocation( String location) {
		set( VALIDATION_LOCATION, location);
	}

	/**
	 * Return the template schema location.
	 *
	 * @return the template schema location.
	 */
//	public String getTemplateLocation() {
//		return getText( TEMPLATE_LOCATION);
//	}

	/**
	 * Set the template schema location.
	 *
	 * @param location the template schema location.
	 */
//	public void setTemplateLocation( String location) {
//		set( TEMPLATE_LOCATION, location);
//	}

	/**
	 * Return the template grammar type.
	 *
	 * @return the template grammar type.
	 */
//	public int getTemplateGrammar() {
//		return getInteger( TEMPLATE_GRAMMAR_TYPE, XMLGrammar.TYPE_XSD);
//	}

	/**
	 * Set the template grammar type.
	 *
	 * @param type the template grammar type.
	 */
//	public void setTemplateGrammar( int type) {
//		set( TEMPLATE_GRAMMAR_TYPE, type);
//	}

	/**
	 * Return the schema grammar location.
	 *
	 * @return the schema grammar location.
	 */
	public String getSchemaLocation() {
		return getText( SCHEMA_LOCATION);
	}

	/**
	 * Set the schema grammar location.
	 *
	 * @param location the schema grammar location.
	 */
	public void setSchemaLocation( String location) {
		set( SCHEMA_LOCATION, location);
	}

	/**
	 * Return the base document location.
	 *
	 * @return the base document location.
	 */
//	public String getBaseLocation() {
//		return getText( BASE_LOCATION);
//	}

	/**
	 * Set the base document location.
	 *
	 * @param location the base document location.
	 */
//	public void setBaseLocation( String location) {
//		set( BASE_LOCATION, location);
//	}

	/**
	 * Return true when the validation url in the document should be used for the template.
	 *
	 * @return true when the validation location in the document should be used.
	 */
//	public boolean useXMLTemplateLocation() {
//		return getBoolean( USE_XML_TEMPLATE_LOCATION);
//	}

	/**
	 * Set true when the xml should be validated with the url in the document
	 *
	 * @param enabled true when the location in the document should be used.
	 */
//	public void setUseXMLTemplateLocation( boolean enabled) {
//		set( USE_XML_TEMPLATE_LOCATION, enabled);
//	}

	/**
	 * Return the grammars description.
	 *
	 * @return the grammars description.
	 */
	public String getDescription() {
		return getText( DESCRIPTION);
	}

	/**
	 * Set the grammars description.
	 *
	 * @param description the grammars description.
	 */
	public void setDescription( String description) {
		set( DESCRIPTION, description);
	}

	/**
	 * Return the grammars associated file extensions.
	 *
	 * @return the grammars associated file extensions.
	 */
	public String getExtensions() {
		return getText( EXTENSIONS);
	}

	/**
	 * Set the grammars associated file extensions.
	 *
	 * @param extensions the grammars associated file extensions.
	 */
	public void setExtensions( String extensions) {
		set( EXTENSIONS, extensions);
	}

	/**
	 * Return the grammars root element name.
	 *
	 * @return the grammars root element name.
	 */
	public String getRootElementName() {
		return getText( ROOT_ELEMENT);
	}

	/**
	 * Set the grammars root element name.
	 *
	 * @param name the grammars root element name.
	 */
	public void setRootElementName( String name) {
		set( ROOT_ELEMENT, name);
	}

	/**
	 * Returns the list of additional namespaces.
	 *
	 * @return the namespaces.
	 */
	public Vector getNamespaces() {
		Vector result = new Vector();
		Vector list = getProperties( NamespaceProperties.NAMESPACE);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new NamespaceProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a namespace properties object to this element.
	 *
	 * @param props the namespace properties.
	 */
	public void addNamespace( NamespaceProperties props) {
		add( props);
	}

	/**
	 * Removes a namespace properties object from this element.
	 *
	 * @param props the namespace properties.
	 */
	public void removeNamespace( NamespaceProperties props) {
		remove( props);
	}

	/**
	 * Returns the list of tag-completion properties.
	 *
	 * @return the tag-completion properties list.
	 */
	public Vector getTagCompletionPropertiesList() {
		Vector result = new Vector();
		Vector list = getProperties( TagCompletionProperties.TAGCOMPLETION);
		
		for ( int i = 0; i < list.size(); i++) {
			TagCompletionProperties props = new TagCompletionProperties( (Properties)list.elementAt(i));
			
			if ( !StringUtilities.isEmpty( props.getLocation())) {
				result.addElement( props);
			}
		}
		
		return result;
	}

	/**
	 * Adds a tag-completion properties object to this element.
	 *
	 * @param props the tag-completion properties.
	 */
	public void addTagCompletion( TagCompletionProperties props) {
		add( props);
	}

	/**
	 * Removes a tag-completion properties object from this element.
	 *
	 * @param props the tag-completion properties.
	 */
	public void removeTagCompletion( TagCompletionProperties props) {
		remove( props);
	}

	/**
	 * Returns the list of additional fargments.
	 *
	 * @return the fragments.
	 */
	public Vector getFragments() {
		Vector result = new Vector();
		Vector list = getProperties( FragmentProperties.FRAGMENT);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new FragmentProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a fragment properties object to this element.
	 *
	 * @param props the fragment properties.
	 */
	public void addFragment( FragmentProperties props) {
		add( props);
	}

	/**
	 * Removes a fragment properties object from this element.
	 *
	 * @param props the fragment properties.
	 */
	public void removeFragment( FragmentProperties props) {
		remove( props);
	}

	/**
	 * Returns the list of named xpaths.
	 *
	 * @return the named xpaths.
	 */
	public Vector getNamedXPaths() {
		Vector result = new Vector();
		Vector list = getProperties( NamedXPathProperties.NAMED_XPATH);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new NamedXPathProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a named xpath properties object to this element.
	 *
	 * @param props the named xpath properties.
	 */
	public void addNamedXPath( NamedXPathProperties props) {
		add( props);
	}

	/**
	 * Removes a named xpath properties object from this element.
	 *
	 * @param props the named xpath properties.
	 */
	public void removeNamedXPath( NamedXPathProperties props) {
		remove( props);
	}

	/**
	 * Sets the default named xpath pointer.
	 *
	 * @param props the named xpath to set.
	 */
	public void setDefaultNamedXPath( NamedXPathProperties props) {
		if ( props != null) {
			set( DEFAULT_NAMED_XPATH, props.getID());
		} else {
			set( DEFAULT_NAMED_XPATH, (String)null);
		}
	}

	/**
	 * Gets the default scenario pointer.
	 *
	 * @return props the scenario to get.
	 */
	public NamedXPathProperties getDefaultNamedXPath() {
		String id = getText( DEFAULT_NAMED_XPATH);
		
		return getNamedXPath( id);
	}

	private NamedXPathProperties getNamedXPath( String id) {
		if (DEBUG) System.out.println( "GrammarProperties.getNamedXPath( "+id+")");
		Vector xpaths = getNamedXPaths();
		
		for ( int i = 0; i < xpaths.size(); i++) {
			NamedXPathProperties xpath = (NamedXPathProperties)xpaths.elementAt(i);
			
			if ( id.equals( xpath.getID())) {
				return xpath;
			}
		}
		
		return null;
	}

	/**
	 * Sets the default scenario pointer.
	 *
	 * @param props the scenario to set.
	 */
	public void setDefaultScenario( ScenarioProperties props) {
		if ( props != null) {
			set( DEFAULT_SCENARIO, props.getID());
		} else {
			set( DEFAULT_SCENARIO, (String)null);
		}
	}

	/**
	 * Gets the default scenario pointer.
	 *
	 * @return props the scenario to get.
	 */
	public ScenarioProperties getDefaultScenario() {
		String id = getText( DEFAULT_SCENARIO);
		
		return getScenario( id);
	}

	/**
	 * Add a scenario pointer.
	 *
	 * @param props the scenario to add.
	 */
	public void addScenario( ScenarioProperties props) {
		if (DEBUG) System.out.println( "GrammarProperties.addScenario( "+props.getID()+")");
		add( SCENARIO, props.getID());
	}

	/**
	 * Remove a scenario pointer.
	 *
	 * @param props the scenario to remove.
	 */
	public void removeScenario( ScenarioProperties props) {
		if (DEBUG) System.out.println( "GrammarProperties.removeScenario( "+props.getID()+")");
		ScenarioProperties defaultScenario = getDefaultScenario();
		
		if ( defaultScenario != null && defaultScenario.getID().equals( props.getID())) {
			setDefaultScenario( null);
		}

		remove( SCENARIO, props.getID());
	}

	/**
	 * Get a list of scenarios.
	 *
	 * @return list of scenarios.
	 */
	public Vector getScenarios() {
		if (DEBUG) System.out.println( "GrammarProperties.getScenarios()");
		Vector scenarios = new Vector();
		Vector ids = getStringList( SCENARIO);
		
		for ( int i = 0; i < ids.size(); i++) {
			String id = ((XElement)ids.elementAt(i)).getText();
			ScenarioProperties scenario = getScenario( id);

			if ( scenario != null) {
				scenarios.addElement( scenario);
			} else {
				remove( SCENARIO, id);
			}
		}
		
		return scenarios;
	}
	
// Implementation of XMLGrammar
	public String getLocation() {
		return getValidationLocation();
	}
	
	public int getType() {
		return getValidationGrammar();
	}

	public boolean useExternal() {
		return !useXMLValidationLocation();
	}
	
	private ScenarioProperties getScenario( String id) {
		if (DEBUG) System.out.println( "GrammarProperties.getScenario( "+id+")");
		Vector scenarios = properties.getScenarioProperties();
		
		for ( int i = 0; i < scenarios.size(); i++) {
			ScenarioProperties scenario = (ScenarioProperties)scenarios.elementAt(i);
			
			if ( id.equals( scenario.getID())) {
				return scenario;
			}
		}
		
		return null;
	}
	
	private static int convertType( String value) {
		int result = XMLGrammar.TYPE_DTD;

		if ( value != null) {
			if ( value.equals( "xsd")) {
				result = XMLGrammar.TYPE_XSD;
			} else if ( value.equals( "rng")) {
				result = XMLGrammar.TYPE_RNG;
			} else if ( value.equals( "rnc")) {
				result = XMLGrammar.TYPE_RNC;
			} else if ( value.equals( "nrl")) {
				result = XMLGrammar.TYPE_NRL;
			}
		}
		
		return result;
	}
	
	private static int convertTemplateType( String value) {
		int result = XMLGrammar.TYPE_XSD;

		if ( value != null) {
			if ( value.equals( "dtd")) {
				result = XMLGrammar.TYPE_DTD;
			} else if ( value.equals( "rng")) {
				result = XMLGrammar.TYPE_RNG;
			} else if ( value.equals( "rnc")) {
				result = XMLGrammar.TYPE_RNC;
			} else if ( value.equals( "nrl")) {
				result = XMLGrammar.TYPE_NRL;
			}
		}
		
		return result;
	}

	private static String convertType( int value) {
		switch ( value) {
			case XMLGrammar.TYPE_XSD:
				return "xsd";
			case XMLGrammar.TYPE_RNG:
				return "rng";
			case XMLGrammar.TYPE_RNC:
				return "rnc";
			case XMLGrammar.TYPE_NRL:
				return "nrl";
		}

		return "dtd";
	}

	private static String getElementValue( XElement element, String name) {
		if (DEBUG) System.out.println( "GrammarProperties.getElementValue( "+element.getName()+", "+name+")");

		XElement value = element.getElement( name);

		if ( value != null) {
			return value.getText();
		}
		
		return null;
	}

	private static String getAttributeValue( XElement element, String attributeName) {
		if ( element != null) {
			return element.getAttribute( attributeName);
		}
		
		return null;
	}

	private static String getAttributeValue( XElement element, String elementName, String attributeName) {
		XElement value = element.getElement( elementName);
		
		if ( value != null) {
			return value.getAttribute( attributeName);
		}
		
		return null;
	}
	
	private static XElement addElement( XElement element, String name, String value) {
		//XElement e = new XElement( name, "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
		XElement e = new XElement( name);

		if ( value != null) {
			e.setText( value);
		}

		element.add( e);
		
		return e;
	}
	
	private static void addAttribute( XElement element, String name, String value) {
		if ( value != null) {
			element.putAttribute( new XAttribute( name, value));
		}
	}
	
	public String toString() {
	    return(this.getDescription());
	}
} 
