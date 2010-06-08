/*
 * $Id: XSLTTransformation.java,v 1.5 2005/04/29 12:37:44 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.util.Vector;

import javax.xml.transform.TransformerException;

import com.cladonia.xml.transform.TransformerUtilities;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.scenario.BreakpointProperties;
import com.cladonia.xngreditor.scenario.InputBreakpointProperties;
import com.cladonia.xngreditor.scenario.ParameterProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.scenario.StylesheetBreakpointProperties;
import com.cladonia.xslt.debugger.Breakpoint;
import com.cladonia.xslt.debugger.BreakpointList;

/**
 * An XSLT Transformation object.
 *
 * @version	$Revision: 1.5 $, $Date: 2005/04/29 12:37:44 $
 * @author Dogsbay
 */
public class XSLTTransformation {
	private Vector parameters = null;
	private ScenarioProperties scenario = null;
	private String styleURL = null;
	private String inputURL = null;
	private int processor = -1;
	private BreakpointList inputBreakpoints = null;
	private BreakpointList styleBreakpoints = null;
	
	public XSLTTransformation() {
		this( null, null, -1);
	}

	public XSLTTransformation( ScenarioProperties scenario) {
		if ( scenario != null) {
			if ( scenario.getInputType() == ScenarioProperties.INPUT_FROM_URL) {
				this.inputURL = scenario.getInputURL();
			}

			if ( scenario.getXSLType() == ScenarioProperties.XSL_FROM_URL) {
				this.styleURL = scenario.getXSLURL();
			} else if ( scenario.getXSLType() == ScenarioProperties.XSL_USE_PROCESSING_INSTRUCTIONS && !StringUtilities.isEmpty( inputURL)) {
				try {
					styleURL = TransformerUtilities.getPIStylesheetLocation( inputURL);
				} catch ( TransformerException x) {
					x.printStackTrace(); // should never happen
				}
			}
			
			this.processor = ScenarioDebugUtilities.getProcessor( scenario);
	
			this.scenario = scenario;
	
			styleBreakpoints = convertBreakpoints( scenario.getStylesheetBreakpoints());
			inputBreakpoints = convertBreakpoints( scenario.getInputBreakpoints());

			parameters = scenario.getParameters();
		} else {
			parameters = new Vector();
		}
	}
		
	public XSLTTransformation( String styleURL, String inputURL, int processor) {
		this.styleURL = styleURL;
		this.inputURL = inputURL;
		this.processor = processor;
		
		styleBreakpoints = new BreakpointList();
		inputBreakpoints = new BreakpointList();

		parameters = new Vector();
	}

	

	/**
	 * Get the current scenario.
	 */
	public ScenarioProperties getScenario() {
		return scenario;
	}
	
	/**
	 * Set the current scenario.
	 */
	public void setScenario( ScenarioProperties scenario) {
		this.scenario = scenario;
	}

	/**
	 * Set the current scenario.
	 */
	public String getName() {
		return URLUtilities.getFileName( styleURL)+" - "+URLUtilities.getFileName( inputURL);
	}

	/**
	 * Get all parameters.
	 */
	public Vector getParameters() {
		return parameters;
	}

	/**
	 * Get all parameters.
	 */
	public void setParameters( Vector parameters) {
		this.parameters = parameters;
	}

//	/**
//	 * Add the specified Parameter.
//	 */
//	public void addParameter( ParameterProperties parameter) {
//		parameters.addElement( parameter);
//	}
//
//	/**
//	 * Remove the specified parameter.
//	 */
//	public void removeParameter( ParameterProperties parameter) {
//		parameters.removeElement( parameter);
//	}

	/**
	 * Get the input URL.
	 */
	public String getInputURL() {
		return inputURL;
	}

	/**
	 * Set the input URL.
	 */
	public void setInputURL( String url) {
		inputURL = url;
	}

	/**
	 * Get the style URL.
	 */
	public String getStyleURL() {
		return styleURL;
	}

	/**
	 * Set the style URL.
	 */
	public void setStyleURL( String url) {
		styleURL = url;
	}

	/**
	 * Get the processor.
	 */
	public int getProcessor() {
		return processor;
	}

	/**
	 * Set the processor.
	 */
	public void setProcessor( int processor) {
		this.processor = processor;
	}

	/**
	 * Get the style breakpoints.
	 */
	public BreakpointList getStyleBreakpoints() {
		return styleBreakpoints;
	}

	/**
	 * Get the input breakpoints.
	 */
	public BreakpointList getInputBreakpoints() {
		return inputBreakpoints;
	}
	
	/**
	 * Create a new Scenario for the current XSLTTransformation.
	 */
	public ScenarioProperties createScenario() {
		ScenarioProperties scenario = new ScenarioProperties();

		scenario.setInputType( ScenarioProperties.INPUT_FROM_URL);
		scenario.setInputFile( inputURL);

 		scenario.setXSLType( ScenarioProperties.XSL_FROM_URL);
		scenario.setXSLURL( styleURL);

 		scenario.setXSLEnabled( true);
 		scenario.setFOPEnabled( false);
 		
 		save( scenario);

 		return scenario;
	}

	/**
	 * Saves the current settings to a scenario if this has a scenario associated.
	 */
	public void save() {
		if ( scenario != null) {
			save( scenario);
		}
	}
	
	private void save( ScenarioProperties scenario) {
		// Save parameters...
		Vector params = scenario.getParameters();

		for ( int i = 0; i < params.size(); i++) {
			ParameterProperties parameter = (ParameterProperties)params.elementAt(i);
			scenario.removeParameter( parameter);
		}
		
		for ( int i = 0; i < parameters.size(); i++) {
			ParameterProperties parameter = (ParameterProperties)parameters.elementAt(i);
			scenario.addParameter( parameter);
		}

		// Save processor.
 		scenario.setProcessor( processor);

 		// Save breakpoints.
		Vector breakpoints = scenario.getInputBreakpoints();

		for ( int i = 0; i < breakpoints.size(); i++) {
			InputBreakpointProperties breakpoint = (InputBreakpointProperties)breakpoints.elementAt(i);
			scenario.removeInputBreakpoint( breakpoint);
		}
		
		Vector breaks = inputBreakpoints.getBreakpoints();
		
		for ( int i = 0; i < breaks.size(); i++) {
			Breakpoint bp = (Breakpoint)breaks.elementAt(i);
			
			scenario.addInputBreakpoint( new InputBreakpointProperties( bp.getFilename(), bp.getLineNumber(), bp.isEnabled()));
		}

		breakpoints = scenario.getStylesheetBreakpoints();

		for ( int i = 0; i < breakpoints.size(); i++) {
			StylesheetBreakpointProperties breakpoint = (StylesheetBreakpointProperties)breakpoints.elementAt(i);
			scenario.removeStylesheetBreakpoint( breakpoint);
		}
		
		breaks = styleBreakpoints.getBreakpoints();
		
		for ( int i = 0; i < breaks.size(); i++) {
			Breakpoint bp = (Breakpoint)breaks.elementAt(i);
			
			scenario.addStylesheetBreakpoint( new StylesheetBreakpointProperties( bp.getFilename(), bp.getLineNumber(), bp.isEnabled()));
		}
	}

	private BreakpointList convertBreakpoints( Vector props) {
		BreakpointList breakpoints = new BreakpointList();
		
		for ( int i = 0; i < props.size(); i++) {
			BreakpointProperties prop = (BreakpointProperties)props.elementAt(i);
			
			breakpoints.addBreakpoint( new Breakpoint( prop.getURL(), prop.getLine(), prop.isEnabled()));
		}
		
		return breakpoints;
	}
}
