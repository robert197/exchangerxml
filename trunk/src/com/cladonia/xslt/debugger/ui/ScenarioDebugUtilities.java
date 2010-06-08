/*
 * $Id: ScenarioDebugUtilities.java,v 1.3 2005/03/16 17:51:25 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xslt.debugger.ui;

import java.net.URL;

//import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLChooserDialog;
//import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * This ScenarioUtilities is used to ...
 *
 * @version $Revision: 1.3 $, $Date: 2005/03/16 17:51:25 $
 * @author Dogsbay
 */
public class ScenarioDebugUtilities {
	private static ConfigurationProperties properties	= null;
	private static XSLTDebuggerFrame parent				= null;
	private static URLChooserDialog inputUrlChooser		= null;
	private static URLChooserDialog styleUrlChooser		= null;
	
 	private static URLChooserDialog getInputURLChooser() {
 		if ( inputUrlChooser == null) {
 			inputUrlChooser = new URLChooserDialog( parent, "Select XML Input", "Specify a XML Document");
 			inputUrlChooser.setLocationRelativeTo( parent);
 		}

 		return inputUrlChooser;
 	}

 	private static URLChooserDialog getStyleURLChooser() {
 		if ( styleUrlChooser == null) {
 			styleUrlChooser = new URLChooserDialog( parent, "Select XSLT Stylesheet", "Specify a XSLT Stylesheet");
 			styleUrlChooser.setLocationRelativeTo( parent);
 		}

 		return styleUrlChooser;
 	}

 	private static String selectInputURL() {
		URLChooserDialog chooser = getInputURLChooser();
		chooser.show();
		
		if ( !chooser.isCancelled()) {
			URL url = chooser.getURL();
			return url.toString();
		}
		
		return null;
	}

 	private static String selectStyleURL() {
		URLChooserDialog chooser = getStyleURLChooser();
		chooser.show();
		
		if ( !chooser.isCancelled()) {
			URL url = chooser.getURL();
			return url.toString();
		}
		
		return null;
	}

 	public static void init( XSLTDebuggerFrame _parent, ConfigurationProperties _properties) {
		properties = _properties;
		parent = _parent;
	}
	
	/**
     * Returns the url for the input ...
	 */
/* 	
	public static URL resolveInputURL( ScenarioProperties properties) {
		URL result = null;

		switch ( properties.getInputType()) {
			case ScenarioProperties.INPUT_PROMPT_FOR_DOCUMENT:
			case ScenarioProperties.INPUT_CURRENT_DOCUMENT:
				String url = selectInputURL();

				if ( url != null) {
					result = URLUtilities.toURL( url);
				}
				break;
			
			
			case ScenarioProperties.INPUT_FROM_URL:
				url = properties.getInputURL();
				
				if ( StringUtilities.isEmpty( (String)url)) {
					url = selectInputURL();
				}

				if ( url != null) {
					result = URLUtilities.toURL( url);
				}
				break;
		}
		
		return result;
	}
*/
 	
	/**
     * Resolves the stylesheet url ...
	 */
/*	
	public static URL resolveStylesheetURL( ScenarioProperties properties) {
		URL result = null;

		switch ( properties.getXSLType()) {
			case ScenarioProperties.XSL_PROMPT_FOR_DOCUMENT:
			case ScenarioProperties.XSL_USE_PROCESSING_INSTRUCTIONS:
			case ScenarioProperties.XSL_CURRENT_DOCUMENT:
				String url = selectStyleURL();
				
				if ( url != null) {
					result = URLUtilities.toURL( url);
				}
				break;
			
			case ScenarioProperties.XSL_FROM_URL:
				url = properties.getXSLURL();
				
				if ( StringUtilities.isEmpty( url)) {
					url = selectStyleURL();
				}

				if ( url != null) {
					result = URLUtilities.toURL( url);
				}
				break;
		}
		
		return result;
	}
*/
	public static int getProcessor( ScenarioProperties scenario) {
		int result = -1;
		
		if ( scenario.getProcessor() == ScenarioProperties.PROCESSOR_DEFAULT) {
			String processor = properties.getXSLTProcessor();
			if ( processor.equals( ConfigurationProperties.XSLT_PROCESSOR_XALAN)) {
				result = ScenarioProperties.PROCESSOR_XALAN;
			} else if ( processor.equals( ConfigurationProperties.XSLT_PROCESSOR_SAXON_XSLT1)) {
				result = ScenarioProperties.PROCESSOR_SAXON_XSLT1;
			} else if ( processor.equals( ConfigurationProperties.XSLT_PROCESSOR_SAXON_XSLT2)) {
				result = ScenarioProperties.PROCESSOR_SAXON_XSLT2;
			}
		} else {
			result = scenario.getProcessor();
		}
		
		return result;
	}
}
