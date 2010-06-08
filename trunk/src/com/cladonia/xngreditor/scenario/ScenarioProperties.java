/*
 * $Id: ScenarioProperties.java,v 1.6 2004/11/04 11:14:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.net.URL;
import java.util.Date;
import java.util.Vector;

import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertyList;
import com.cladonia.xngreditor.Identity;
import com.cladonia.xngreditor.URLUtilities;

/**
 * Handles the properties for a Transformation scenario.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/11/04 11:14:44 $
 * @author Dogsbay
 */
public class ScenarioProperties extends Properties {

	private static final boolean DEBUG = false;
	
	public static final String SCENARIO_PROPERTIES	= "scenario-properties";

	private static final String ID		= "id";
	private static final String NAME	= "name";

	// input
	public static final int INPUT_FROM_URL				= 0;
	public static final int INPUT_CURRENT_DOCUMENT		= 1;
	public static final int INPUT_PROMPT_FOR_DOCUMENT	= 2;

	private static final String INPUT_TYPE				= "input-type";
	private static final String INPUT_URL 				= "input-url";

	// xslt
	public static final int XSL_FROM_URL					= 0;
	public static final int XSL_CURRENT_DOCUMENT			= 1;
	public static final int XSL_PROMPT_FOR_DOCUMENT			= 2;
	public static final int XSL_USE_PROCESSING_INSTRUCTIONS	= 3;

	private static final String ENABLE_XSL					= "enable-xsl";
	private static final String XSL_URL						= "xsl-url";
	private static final String XSL_TYPE					= "xsl-type";
	
	private static final String XSL_SYSTEM_ID				= "xsl-system-id";

	// xquery
	public static final int XQUERY_FROM_URL					= 0;
	public static final int XQUERY_CURRENT_DOCUMENT			= 1;
	public static final int XQUERY_PROMPT_FOR_DOCUMENT		= 2;

	private static final String ENABLE_XQUERY				= "enable-xquery";
	private static final String XQUERY_URL					= "xquery-url";
	private static final String XQUERY_TYPE					= "xquery-type";

	// FOP Output
	public static final int FOP_TYPE_PDF	= 0;
	public static final int FOP_TYPE_PS		= 1;
	public static final int FOP_TYPE_TXT	= 2;
	public static final int FOP_TYPE_SVG	= 3;

	private static final String ENABLE_FOP		= "enable-fop"; // use fop
	private static final String FOP_TYPE		= "fop-type"; 
	private static final String FOP_OUTPUT_TYPE	= "fop-output-type"; 
	private static final String FOP_OUTPUT_FILE	= "fop-output-file"; 

	public static final int FOP_OUTPUT_TO_FILE			= 0;
	public static final int FOP_OUTPUT_TO_VIEWER		= 1;
	public static final int FOP_OUTPUT_PROMPT_FOR_FILE	= 2;
	
	// Output
	public static final int OUTPUT_TO_FILE			= 0;
	
//	public static final int OUTPUT_TO_CONSOLE		= 1; // @deprecated
//	public static final int OUTPUT_TO_BROWSER		= 2; // @deprecated
	public static final int OUTPUT_PROMPT_FOR_FILE	= 3;
	public static final int OUTPUT_TO_NEW_DOCUMENT	= 4;
	public static final int OUTPUT_TO_INPUT			= 5; // new 2.0
	public static final int OUTPUT_DO_NOTHING		= 6;
	
	public static final String ENABLE_BROWSER	= "enable-browser";
	public static final String BROWSER_URL		= "browser-url";

	private static final String OUTPUT			= "output-type";
	private static final String OUTPUT_FILE		= "output-file";
	
	// processor
	public static final int PROCESSOR_DEFAULT		= 0;
	public static final int PROCESSOR_XALAN			= 1;
	public static final int PROCESSOR_SAXON_XSLT1	= 2;
	public static final int PROCESSOR_SAXON_XSLT2	= 3;

	private static final String PROCESSOR	= "processor";

	private PropertyList searches = null;

	/**
	 * Constructor for the scenario properties.
	 *
	 * @param props the higher level properties object.
	 */
	public ScenarioProperties( Properties props) {
		super( props.getElement());
	}

	/**
	 * Constructor for a new scenario properties object.
	 *
	 * @param scenario imported scenario.
	 */
	public ScenarioProperties( URL url, XElement scenario) {
		super( new XElement( SCENARIO_PROPERTIES));
		
//		Date date = new Date();
//		set( ID, "SP"+date.getTime());
//		
		importScenario( url, scenario);
	}
	
	/**
	 * Constructor for a new scenario properties object.
	 */
	public ScenarioProperties() {
		super( new XElement( SCENARIO_PROPERTIES));
		
//		Date date = new Date();
//		set( ID, "SP"+date.getTime());
	}

	public void importScenario( URL url, XElement element) {
		setName( getAttributeValue( element, "name"));
		
		setInputType( convertInputType( getAttributeValue( element, "xml", "type")));
		setInputFile( URLUtilities.resolveURL( url, getAttributeValue( element, "xml", "src")));

		XElement xslElement = element.getElement( "xsl");
		XElement xqueryElement = element.getElement( "xquery");
		if ( xslElement != null) {
			setXSLEnabled( true);
			setXSLType( convertXSLType( getAttributeValue( element, "xsl", "type")));
			setXSLURL( URLUtilities.resolveURL( url, getAttributeValue( element, "xsl", "src")));
			setProcessor( convertProcessor( getAttributeValue( element, "xsl", "processor")));

			Vector params = getParameters();
			for ( int i = 0; i < params.size(); i++) {
				removeParameter( (ParameterProperties)params.elementAt(i));
			}

			XElement[] parameters = xslElement.getElements( "parameter");
			for ( int i = 0; i < parameters.length; i++) {
				addParameter( new ParameterProperties( parameters[i].getAttribute( "name"), parameters[i].getAttribute( "value")));
			}
		} else if ( xqueryElement != null) {
			setXSLEnabled( false);
			setXQueryEnabled( true);
			setXQueryType( convertXQueryType( getAttributeValue( element, "xquery", "type")));
			setXQueryURL( URLUtilities.resolveURL( url, getAttributeValue( element, "xquery", "src")));
		} else {
			setXQueryEnabled( false);
			setXSLEnabled( false);
		}

		if ( element.getElement( "fop") != null) {
			setFOPEnabled( true);
			setFOPOutputFile( resolveFile( url, getAttributeValue( element, "fop", "src")));
			setFOPOutputType( convertFOPOutputType( getAttributeValue( element, "fop", "type")));
			setFOPType( convertFOPType( getAttributeValue( element, "fop", "output")));
		} else {
			setFOPEnabled( false);
			setOutputFile( resolveFile( url, getAttributeValue( element, "output", "src")));
			setOutputType( convertOutputType( getAttributeValue( element, "output", "type")));
			String value = getAttributeValue( element, "output", "browser");

			if ( value != null && value.trim().length() > 0 && value.equals( "true")) {
				setBrowserEnabled( true);
				setBrowserURL( getAttributeValue( element, "output", "browserURL"));
			} else {
				setBrowserEnabled( false);
			}
		}
	}

	public XElement exportScenario( URL url) {
		//XElement root = new XElement( "scenario", "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
		XElement root = new XElement( "scenario");

		addAttribute( root, "name", getName());

		XElement xmlElement = addElement( root, "xml", null);
		addAttribute( xmlElement, "src", URLUtilities.getRelativePath( url, getInputURL()));
		addAttribute( xmlElement, "type", convertInputType( getInputType()));

		if ( isXSLEnabled()) {
			XElement xslElement = addElement( root, "xsl", null);
			addAttribute( xslElement, "src", URLUtilities.getRelativePath( url, getXSLURL()));
			addAttribute( xslElement, "type", convertXSLType( getXSLType()));
			addAttribute( xslElement, "processor", convertProcessor( getProcessor()));
			
			Vector params = getParameters();
			for ( int i = 0; i < params.size(); i++) {
				ParameterProperties param = (ParameterProperties)params.elementAt(i);

				XElement parameterElement = addElement( xslElement, "parameter", null);
				addAttribute( parameterElement, "name", param.getName());
				addAttribute( parameterElement, "value", param.getValue());
			}
		} else if ( isXQueryEnabled()) {
			XElement xqueryElement = addElement( root, "xquery", null);
			addAttribute( xqueryElement, "src", URLUtilities.getRelativePath( url, getXQueryURL()));
			addAttribute( xqueryElement, "type", convertXQueryType( getXQueryType()));
		}
		
		if ( isFOPEnabled()) {
			XElement fopElement = addElement( root, "fop", null);
			addAttribute( fopElement, "src", generateRelativeFile( url, getFOPOutputFile()));
			addAttribute( fopElement, "type", convertFOPOutputType( getFOPOutputType()));
			addAttribute( fopElement, "output", convertFOPType( getFOPType()));
		} else {
			XElement outputElement = addElement( root, "output", null);
			addAttribute( outputElement, "src", generateRelativeFile( url, getOutputFile()));
			addAttribute( outputElement, "type", convertOutputType( getOutputType()));

			if ( isBrowserEnabled()) {
				addAttribute( outputElement, "browser", "true");
				addAttribute( outputElement, "browserURL", getBrowserURL());
			}
		}

		return root;
	}
	
	private String generateRelativeFile( URL base, String file) {
		if ( file != null && file.trim().length() > 0) {
			String url = URLUtilities.toURL( file).toString();
			return URLUtilities.getRelativePath( base, url);
		}
		
		return "";
	}

	private String resolveFile( URL base, String file) {
		
		if ( file != null && file.trim().length() > 0) {
			String url = URLUtilities.resolveURL( base, file);
			return URLUtilities.toFile( url).getPath();
		}
		
		return "";
	}

	public void update( ScenarioProperties props) {
		setFOPEnabled( props.isFOPEnabled());
		setFOPOutputFile( props.getFOPOutputFile());
		setFOPOutputType( props.getFOPOutputType());
		setFOPType( props.getFOPType());
		setInputFile( props.getInputURL());
		setInputType( props.getInputType());
		setOutputFile( props.getOutputFile());
		setOutputType( props.getOutputType());
		setProcessor( props.getProcessor());
		setXSLEnabled( props.isXSLEnabled());
		setXSLType( props.getXSLType());
		setXSLURL( props.getXSLURL());

		Vector params = getParameters();
		for ( int i = 0; i < params.size(); i++) {
			removeParameter( (ParameterProperties)params.elementAt(i));
		}

		params = props.getParameters();
		for ( int i = 0; i < params.size(); i++) {
			addParameter( (ParameterProperties)params.elementAt(i));
		}
	}

	/**
	 * Set the name.
	 *
	 * @param name the scenario name.
	 */
	private void setID( String id) {
		set( ID, id);
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
			set( ID, "SP"+date.getTime()+getName().hashCode());
			
			result = getText( ID);
		}

		return result;
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
	 * @param name the scenario name.
	 */
	public void setName( String name) {
		set( NAME, name);
	}

	/**
	 * Return the xsl document url.
	 *
	 * @return the xsl url.
	 */
	public String getXSLURL() {
		return getText( XSL_URL);
	}

	/**
	 * Set the xsl URL.
	 *
	 * @param url the url for the xsl document.
	 */
	public void setXSLURL( String url) {
		set( XSL_URL, url);
	}

	/**
	 * Return the XSL type.
	 *
	 * @return the XSL type.
	 */
	public int getXSLType() {
		return getInteger( XSL_TYPE);
	}

	/**
	 * Set the XSL type.
	 *
	 * @param type the XSL type.
	 */
	public void setXSLType( int type) {
		set( XSL_TYPE, type);
	}
	
	public void setXSLSystemId(String xslSystemId) {
		set( XSL_SYSTEM_ID, xslSystemId);
	}
	
	public String getXSLSystemId() {
		return(getText(XSL_SYSTEM_ID));
	}

	/**
	 * Return true when XSL is enabled.
	 *
	 * @return true when XSL is enabled.
	 */
	public boolean isXSLEnabled() {
		return getBoolean( ENABLE_XSL, false);
	}

	/**
	 * Set true when XSL should be used.
	 *
	 * @param enabled true when XSL should be used.
	 */
	public void setXSLEnabled( boolean enabled) {
		set( ENABLE_XSL, enabled);
	}

	/**
	 * Return true when a Browser should be used to display the output.
	 *
	 * @return true when a browser should be used.
	 */
	public boolean isBrowserEnabled() {
		int type = getOutputType();

		// deprecated!
		if ( type == 2) { // OUTPUT_TO_BROWSER
			setBrowserEnabled( true);
		}

		return getBoolean( ENABLE_BROWSER, false);
	}

	/**
	 * Set true when the browser should display the output.
	 *
	 * @param enabled true when the browser should be used.
	 */
	public void setBrowserEnabled( boolean enabled) {
		set( ENABLE_BROWSER, enabled);
	}

	/**
	 * Return the Browser URL.
	 *
	 * @return the Browser URL.
	 */
	public String getBrowserURL() {
		return getText( BROWSER_URL);
	}

	/**
	 * Set the Browser URL.
	 *
	 * @param url the Browser url.
	 */
	public void setBrowserURL( String url) {
		set( BROWSER_URL, url);
	}

	/**
	 * Return the XQuery document url.
	 *
	 * @return the XQuery url.
	 */
	public String getXQueryURL() {
		return getText( XQUERY_URL);
	}

	/**
	 * Set the XQuery URL.
	 *
	 * @param url the url for the XQuery document.
	 */
	public void setXQueryURL( String url) {
		set( XQUERY_URL, url);
	}

	/**
	 * Return the XQuery type.
	 *
	 * @return the XQuery type.
	 */
	public int getXQueryType() {
		return getInteger( XQUERY_TYPE);
	}

	/**
	 * Set the XQuery type.
	 *
	 * @param type the XQuery type.
	 */
	public void setXQueryType( int type) {
		set( XQUERY_TYPE, type);
	}

	/**
	 * Return true when XQuery is enabled.
	 *
	 * @return true when XQuery is enabled.
	 */
	public boolean isXQueryEnabled() {
		return getBoolean( ENABLE_XQUERY, false);
	}

	/**
	 * Set true when XQuery should be used.
	 *
	 * @param enabled true when XQuery should be used.
	 */
	public void setXQueryEnabled( boolean enabled) {
		set( ENABLE_XQUERY, enabled);
	}

	/**
	 * Return true when FOP should be used.
	 *
	 * @return true when FOP should be used.
	 */
	public boolean isFOPEnabled() {
		return getBoolean( ENABLE_FOP, false);
	}

	/**
	 * Set true when FOP should be used.
	 *
	 * @param enabled true when FOP should be used.
	 */
	public void setFOPEnabled( boolean enabled) {
		set( ENABLE_FOP, enabled);
	}

	/**
	 * Return the FOP type.
	 *
	 * @return the FOP type.
	 */
	public int getFOPType() {
		return getInteger( FOP_TYPE, FOP_TYPE_PDF);
	}

	/**
	 * Set the FOP type.
	 *
	 * @param type the FOP type.
	 */
	public void setFOPType( int type) {
		set( FOP_TYPE, type);
	}

	/**
	 * Return the FOP ouput type.
	 *
	 * @return the FOP output type.
	 */
	public int getFOPOutputType() {
		return getInteger( FOP_OUTPUT_TYPE);
	}

	/**
	 * Set the FOP type.
	 *
	 * @param type the FOP type.
	 */
	public void setFOPOutputType( int type) {
		set( FOP_OUTPUT_TYPE, type);
	}

	/**
	 * Return the FOP output file location.
	 *
	 * @return the FOP output file location.
	 */
	public String getFOPOutputFile() {
		return getText( FOP_OUTPUT_FILE);
	}

	/**
	 * Set the FOP output-file location.
	 *
	 * @param location the FOP output-file location.
	 */
	public void setFOPOutputFile( String location) {
		set( FOP_OUTPUT_FILE, location);
	}

	/**
	 * Return the Input type.
	 *
	 * @return the Input type.
	 */
	public int getInputType() {
		return getInteger( INPUT_TYPE);
	}

	/**
	 * Set the input type.
	 *
	 * @param type the input type.
	 */
	public void setInputType( int type) {
		set( INPUT_TYPE, type);
	}

	/**
	 * Return the input url location.
	 *
	 * @return the input url location.
	 */
	public String getInputURL() {
		return getText( INPUT_URL);
	}

	/**
	 * Set the input-url location.
	 *
	 * @param location the input-url location.
	 */
	public void setInputFile( String location) {
		set( INPUT_URL, location);
	}

	/**
	 * Return the output file location.
	 *
	 * @return the output file location.
	 */
	public String getOutputFile() {
		return getText( OUTPUT_FILE);
	}

	/**
	 * Set the output-file location.
	 *
	 * @param location the output-file location.
	 */
	public void setOutputFile( String location) {
		set( OUTPUT_FILE, location);
	}
	
	
	/**
	 * Return the output type.
	 *
	 * @return the output type.
	 */
	public int getOutputType() {
		int type = getInteger( OUTPUT);

		// Deprecated!
		if ( type == 2 || type == 1) { // OUTPUT_TO_BROWSER || OUTPUT_TO_CONSOLE
			setOutputType( OUTPUT_TO_NEW_DOCUMENT);
		}
		
		return getInteger( OUTPUT);
	}

	/**
	 * Set the output type.
	 *
	 * @param type the output type.
	 */
	public void setOutputType( int type) {
		set( OUTPUT, type);
	}
	
	/**
	 * Return the processor.
	 *
	 * @return the processor.
	 */
	public int getProcessor() {
		return getInteger( PROCESSOR, PROCESSOR_DEFAULT);
	}

	/**
	 * Set the Processor.
	 *
	 * @param processor Processor.
	 */
	public void setProcessor( int processor) {
		set( PROCESSOR, processor);
	}

	/**
	 * Returns the parameters list.
	 *
	 * @return the parameters.
	 */
	public Vector getParameters() {
		Vector result = new Vector();
		Vector list = getProperties( ParameterProperties.PARAMETER);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new ParameterProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a parameters properties object to this element.
	 *
	 * @param props the parameters properties.
	 */
	public void addParameter( ParameterProperties props) {
		add( props);
	}

	/**
	 * Removes a parameter properties object from this element.
	 *
	 * @param props the parameters properties.
	 */
	public void removeParameter( ParameterProperties props) {
		remove( props);
	}
	
	/**
	 * Returns the list of input breakpoints.
	 *
	 * @return the input breakpoints.
	 */
	public Vector getInputBreakpoints() {
		Vector result = new Vector();
		Vector list = getProperties( InputBreakpointProperties.INPUT_BREAKPOINT);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new InputBreakpointProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a input breakpoint properties object to this element.
	 *
	 * @param props the input breakpoint properties.
	 */
	public void addInputBreakpoint( InputBreakpointProperties props) {
		add( props);
	}

	/**
	 * Removes an input breakpoint properties object from this element.
	 *
	 * @param props the input breakpoint properties.
	 */
	public void removeInputBreakpoint( InputBreakpointProperties props) {
		remove( props);
	}

	/**
	 * Returns the list of stylesheet breakpoints.
	 *
	 * @return the stylesheet breakpoints.
	 */
	public Vector getStylesheetBreakpoints() {
		Vector result = new Vector();
		Vector list = getProperties( StylesheetBreakpointProperties.STYLESHEET_BREAKPOINT);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new StylesheetBreakpointProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a stylesheet breakpoint properties object to this element.
	 *
	 * @param props the stylesheet breakpoint properties.
	 */
	public void addStylesheetBreakpoint( StylesheetBreakpointProperties props) {
		add( props);
	}

	/**
	 * Removes an stylesheet breakpoint properties object from this element.
	 *
	 * @param props the stylesheet breakpoint properties.
	 */
	public void removeStylesheetBreakpoint( StylesheetBreakpointProperties props) {
		remove( props);
	}

	private static int convertInputType( String value) {
		int result = INPUT_PROMPT_FOR_DOCUMENT;

		if ( value != null) {
			if ( value.equals( "url")) {
				result = INPUT_FROM_URL;
			} else if ( value.equals( "current")) {
				result = INPUT_CURRENT_DOCUMENT;
			} else if ( value.equals( "prompt")) {
				result = INPUT_PROMPT_FOR_DOCUMENT;
			}
		}
		
		return result;
	}

	private static String convertInputType( int value) {
		String result = "prompt";

		switch (value) {
			case INPUT_FROM_URL:
				result = "url";
				break;
			case INPUT_CURRENT_DOCUMENT:
				result = "current";
				break;
			case INPUT_PROMPT_FOR_DOCUMENT:
				result = "prompt";
				break;
		}
		
		return result;
	}

	private static int convertXSLType( String value) {
		int result = XSL_PROMPT_FOR_DOCUMENT;

		if ( value != null) {
			if ( value.equals( "url")) {
				result = XSL_FROM_URL;
			} else if ( value.equals( "current")) {
				result = XSL_CURRENT_DOCUMENT;
			} else if ( value.equals( "prompt")) {
				result = XSL_PROMPT_FOR_DOCUMENT;
			} else if ( value.equals( "pis")) {
				result = XSL_USE_PROCESSING_INSTRUCTIONS;
			}
		}
		
		return result;
	}

	private static String convertXSLType( int value) {
		String result = "prompt";

		switch (value) {
			case XSL_FROM_URL:
				result = "url";
				break;
			case XSL_CURRENT_DOCUMENT:
				result = "current";
				break;
			case XSL_PROMPT_FOR_DOCUMENT:
				result = "prompt";
				break;
			case XSL_USE_PROCESSING_INSTRUCTIONS:
				result = "pis";
				break;
		}
		
		return result;
	}

	private static int convertXQueryType( String value) {
		int result = XQUERY_PROMPT_FOR_DOCUMENT;

		if ( value != null) {
			if ( value.equals( "url")) {
				result = XQUERY_FROM_URL;
			} else if ( value.equals( "current")) {
				result = XQUERY_CURRENT_DOCUMENT;
			} else if ( value.equals( "prompt")) {
				result = XQUERY_PROMPT_FOR_DOCUMENT;
			}
		}
		
		return result;
	}

	private static String convertXQueryType( int value) {
		String result = "prompt";

		switch (value) {
			case XQUERY_FROM_URL:
				result = "url";
				break;
			case XQUERY_CURRENT_DOCUMENT:
				result = "current";
				break;
			case XQUERY_PROMPT_FOR_DOCUMENT:
				result = "prompt";
				break;
		}
		
		return result;
	}

	private static int convertProcessor( String value) {
		int result = PROCESSOR_DEFAULT;

		if ( value != null) {
			if ( value.equals( "default")) {
				result = PROCESSOR_DEFAULT;
			} else if ( value.equals( "xalan")) {
				result = PROCESSOR_XALAN;
			} else if ( value.equals( "saxon")) {
				result = PROCESSOR_SAXON_XSLT1;
			} else if ( value.equals( "saxon2")) {
				result = PROCESSOR_SAXON_XSLT2;
			}
		}
		
		return result;
	}

	private static String convertProcessor( int value) {
		String result = "default";

		switch (value) {
			case PROCESSOR_XALAN:
				result = "xalan";
				break;
			case PROCESSOR_SAXON_XSLT1:
				result = "saxon";
				break;
			case PROCESSOR_SAXON_XSLT2:
				result = "saxon2";
				break;
			default:
				result = "default";
				break;
		}
		
		return result;
	}

	private static int convertFOPOutputType( String value) {
		int result = FOP_OUTPUT_PROMPT_FOR_FILE;

		if ( value != null) {
			if ( value.equals( "url")) {
				result = FOP_OUTPUT_TO_FILE;
			} else if ( value.equals( "prompt")) {
				result = FOP_OUTPUT_PROMPT_FOR_FILE;
			} else if ( value.equals( "viewer")) {
				result = FOP_OUTPUT_TO_VIEWER;
			}
		}
		
		return result;
	}

	private static String convertFOPOutputType( int value) {
		String result = "prompt";

		switch (value) {
			case FOP_OUTPUT_TO_FILE:
				result = "url";
				break;
			case FOP_OUTPUT_TO_VIEWER:
				result = "viewer";
				break;
			case FOP_OUTPUT_PROMPT_FOR_FILE:
				result = "prompt";
				break;
		}
		
		return result;
	}

	private static int convertFOPType( String value) {
		int result = FOP_TYPE_PDF;

		if ( value != null) {
			if ( value.equals( "pdf")) {
				result = FOP_TYPE_PDF;
			} else if ( value.equals( "ps")) {
				result = FOP_TYPE_PS;
			} else if ( value.equals( "txt")) {
				result = FOP_TYPE_TXT;
			} else if ( value.equals( "svg")) {
				result = FOP_TYPE_SVG;
			}
		}
		
		return result;
	}

	private static String convertFOPType( int value) {
		String result = "pdf";

		switch (value) {
			case FOP_TYPE_PDF:
				result = "pdf";
				break;
			case FOP_TYPE_PS:
				result = "ps";
				break;
			case FOP_TYPE_TXT:
				result = "txt";
				break;
			case FOP_TYPE_SVG:
				result = "svg";
				break;
		}
		
		return result;
	}

	private static int convertOutputType( String value) {
		int result = OUTPUT_PROMPT_FOR_FILE;

		if ( value != null) {
			if ( value.equals( "url")) {
				result = OUTPUT_TO_FILE;
			} else if ( value.equals( "prompt")) {
				result = OUTPUT_PROMPT_FOR_FILE;
			} else if ( value.equals( "console")) { // deprecated
				result = OUTPUT_TO_NEW_DOCUMENT;
			} else if ( value.equals( "document")) {
				result = OUTPUT_TO_NEW_DOCUMENT;
			} else if ( value.equals( "browser")) { // deprecated
				result = OUTPUT_TO_NEW_DOCUMENT;
			} else if ( value.equals( "input")) {
				result = OUTPUT_TO_INPUT;
			} else if ( value.equals( "nothing")) {
				result = OUTPUT_DO_NOTHING;
			}
		}
		
		return result;
	}

	private String convertOutputType( int value) {
		String result = "prompt";

		switch (value) {
			case OUTPUT_TO_FILE:
				result = "url";
				break;
			case OUTPUT_PROMPT_FOR_FILE:
				result = "prompt";
				break;
			case 1: // OUTPUT_TO_CONSOLE: deprecated
				result = "document";
				break;
			case OUTPUT_TO_NEW_DOCUMENT:
				result = "document";
				break;
			case 2: // OUTPUT_TO_BROWSER: deprecated
				result = "document";
				setBrowserEnabled( true);
				break;
			case OUTPUT_TO_INPUT:
				result = "input";
				break;
			case OUTPUT_DO_NOTHING:
				result = "nothing";
				break;
		}
		
		return result;
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
} 
