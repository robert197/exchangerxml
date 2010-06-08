/*
 * $Id: PluginViewProperties.java,v 1.0 9 May 2007 12:17:21 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.PropertiesFile;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 9 May 2007 12:17:21 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginViewProperties extends PropertiesFile {

	private PluginView pluginView = null;
	
	/**
	 * Initialise the class PluginViewProperties.java
	 * @param element
	 */
	public PluginViewProperties(PluginView pluginView, ExchangerDocument document, XElement element) {

		super(document, element);
		this.pluginView = pluginView;
	}

	/**
	 * @param pluginView the pluginView to set
	 */
	public void setPluginView(PluginView pluginView) {

		this.pluginView = pluginView;
	}

	/**
	 * @return the pluginView
	 */
	public PluginView getPluginView() {

		return pluginView;
	}
	
	

}
