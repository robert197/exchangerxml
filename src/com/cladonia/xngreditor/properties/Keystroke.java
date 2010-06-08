/*
 * $Id: Keystroke.java,v 1.2 2004/07/29 12:40:50 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;



/**
 * Handles the KeyStrokes
 *
 * @version	$Revision: 1.2 $, $Date: 2004/07/29 12:40:50 $
 * @author Dogs bay
 */
public class Keystroke extends Properties{
	
	public static final String KEYSTROKE	= "keystroke";
	private static final String MASK	= "mask";
	private static final String VALUE	= "value";
	
	/**
	 * Creates the KeyStroke
	 *
	 * @param mask the mask required
	 * @param value the key value
	 */
	public Keystroke( String mask, String value) {
		super( new XElement( KEYSTROKE));
		
		if(mask != null) {
			if(mask.equalsIgnoreCase("null")) {
				mask = null;
			}
		}
		
		if(value != null) {
			if(value.equalsIgnoreCase("null")) {
				value = null;
			}
		}
		
		setMask(mask);
		setValue(value);
	}
	
	/**
	 * Creates the KeyStroke
	 *
	 * @param keystrokeElement The keystroke XElement
	 */
	public Keystroke( XElement keystrokeElement) {
		super( new XElement( KEYSTROKE));
		
		XElement mask = (XElement)keystrokeElement.element(MASK);
		
		String maskValue = null;
		if (mask != null)
		{
			maskValue = mask.getText();
		}
		setMask(maskValue);
		
		XElement valueEle = (XElement)keystrokeElement.element(VALUE);
		String value = null;
		if (valueEle != null)
		{
			value = valueEle.getText();
		}
		setValue(value);
	}
	
	//	Set the mask for this keystroke
	private void setMask(String mask) {
		if (mask != null)
		{
			set(MASK, mask);
		}
	}
	
	//	Set the value for this keystroke
	private void setValue(String value) {
		if (value != null)
		{
			set(VALUE, value);
		}
	}
	
	/**
	 * Gets the mask
	 *
	 * @return The mask
	 */
	public String getMask()
	{
		return getText(MASK);
	}
	
	/**
	 * Gets the value
	 *
	 * @return The key value
	 */
	public String getValue()
	{
		return getText(VALUE);
	}
	
	/**
	 * Compares two Keystroke objects
	 *
	 * @return boolean depending on if the Keystroke objects are the same or not
	 */
	public boolean equals(Keystroke keystroke)
	{
		if (getMask().equalsIgnoreCase(keystroke.getMask()) && getValue().equalsIgnoreCase(keystroke.getValue()))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
}
