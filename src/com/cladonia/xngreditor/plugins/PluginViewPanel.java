/*
 * $Id: PluginViewPanel.java,v 1.0 13 Mar 2007 16:05:25 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import java.awt.LayoutManager;
import java.io.IOException;

import org.xml.sax.SAXParseException;

import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLError;
import com.cladonia.xngreditor.ViewPanel;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 13 Mar 2007 16:05:25 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public abstract class PluginViewPanel extends ViewPanel {

	private PluginView pluginView = null;
	
	public PluginViewPanel(PluginView pluginView, LayoutManager layout) {
		super(layout);
		this.setPluginView(pluginView);
	}
	
	/**
	 * @return
	 */
	public abstract XElement getSelectedElement();

	public abstract void setSelectedElement(XElement element);
	public abstract void setSelectedElement(XAttribute attribute);
	
	public abstract void setSchema(XMLSchema schema);
	public abstract void updateDocument();
	public abstract ExchangerDocument getDocument();
	public abstract void cleanup();
	
	public abstract void setFocus();
	
	/**
	 * @return
	 */
	public abstract boolean hasLatestInformation();
	/**
	 * @param document
	 */
	public abstract void setDocument(ExchangerDocument document);
	/**
	 * @param selectedElement
	 */
	public abstract void selectElement(XElement selectedElement);
	
	public abstract void updateHelper();
	
	public abstract void selectError(XMLError error);

	/**
	 * 
	 */
	public abstract void createRequired();

	/**
	 * 
	 */
	public abstract void parse() throws SAXParseException, IOException;

	/**
	 * @param name
	 */
	public abstract void addNewElementToSelected(String name);

	/**
	 * @param name
	 */
	public abstract void addNewAttributeToSelected(String name);

	/**
	 * @param name
	 */
	public abstract void selectAttribute(String name);

	/**
	 * @param attribute
	 * @param i
	 */
	public abstract void selectAttribute(XAttribute attribute, int i);

	/**
	 * 
	 */
	public abstract void collapseAll();
	public abstract void expandAll();

	/**
	 * 
	 */
	public abstract void copy() ;

	/**
	 * 
	 */
	public abstract void cut();

	/**
	 * 
	 */
	public abstract void paste();

	/**
	 * @return
	 */
	public abstract void saveState();

	/**
	 * 
	 */
	public abstract void returnToPreviousState();

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