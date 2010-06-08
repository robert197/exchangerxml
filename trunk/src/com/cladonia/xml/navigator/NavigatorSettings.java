		   /*
 * $Id: NavigatorSettings.java,v 1.3 2004/10/26 16:04:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import org.dom4j.Namespace;

/**
 * Handles the settings for the navigator for a specific view.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/10/26 16:04:21 $
 * @author Dogsbay
 */
public class NavigatorSettings {
	private boolean isFirstTime = true;

	private boolean xpathSelected = false;
	private boolean attributesSelected = false;
	private boolean attributeNamesSelected = true;
	private boolean elementValuesSelected = false;
	private boolean elementNamesSelected = true;

	private boolean xpathAttributesSelected = false;
	private boolean xpathAttributeNamesSelected = true;
	private boolean xpathElementValuesSelected = false;
	private boolean xpathElementNamesSelected = true;

	private int type = Navigator.SELECTED_NAMESPACE_TYPE_ALL;
	private Namespace namespace = null;

	private String xpathValue = null;

	public void setXPathSelected( boolean selected) {
		xpathSelected = selected;
	}

	public boolean isXPathSelected() {
		return xpathSelected;
	}

	public void setAttributesSelected( boolean selected) {
		attributesSelected = selected;
	}

	public boolean isAttributesSelected() {
		return attributesSelected;
	}

	public void setAttributeNamesSelected( boolean selected) {
		attributeNamesSelected = selected;
	}

	public boolean isAttributeNamesSelected() {
		return attributeNamesSelected;
	}

	public void setElementValuesSelected( boolean selected) {
		elementValuesSelected = selected;
	}

	public boolean isElementValuesSelected() {
		return elementValuesSelected;
	}

	public void setElementNamesSelected( boolean selected) {
		elementNamesSelected = selected;
	}

	public boolean isElementNamesSelected() {
		return elementNamesSelected;
	}

	public void setXPathAttributesSelected( boolean selected) {
		xpathAttributesSelected = selected;
	}

	public boolean isXPathAttributesSelected() {
		return xpathAttributesSelected;
	}

	public void setXPathAttributeNamesSelected( boolean selected) {
		xpathAttributeNamesSelected = selected;
	}

	public boolean isXPathAttributeNamesSelected() {
		return xpathAttributeNamesSelected;
	}

	public void setXPathElementValuesSelected( boolean selected) {
		xpathElementValuesSelected = selected;
	}

	public boolean isXPathElementValuesSelected() {
		return xpathElementValuesSelected;
	}

	public void setXPathElementNamesSelected( boolean selected) {
		xpathElementNamesSelected = selected;
	}

	public boolean isXPathElementNamesSelected() {
		return xpathElementNamesSelected;
	}

	public void setType( int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public void setXPathValue( String text) {
		xpathValue = text;
	}

	public String getXPathValue() {
		return xpathValue;
	}

	public void setNamespace( Namespace namespace) {
		this.namespace = namespace;
	}

	public Namespace getNamespace() {
		return namespace;
	}

	public boolean isFirstTime() {
		if ( isFirstTime) {
			isFirstTime = false;
			
			return true;
		}

		return false;
	}
} 
