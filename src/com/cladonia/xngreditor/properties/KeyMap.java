/*
 * $Id: KeyMap.java,v 1.2 2004/07/29 12:40:24 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import java.util.Iterator;
import java.util.Vector;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;


/**
 * Handles the KeyMaps
 *
 * @version	$Revision: 1.2 $, $Date: 2004/07/29 12:40:24 $
 * @author Dogs bay
 */
public class KeyMap extends Properties{
	
	public static final String KEYMAP	= "keymap";
	private static final String ACTION	= "action";
	private static final String DESCRIPTION	= "description";
	
	/**
	 * Creates the KeyMap
	 *
	 * @param action the action required
	 * @param description a description of the action 
	 * @param keystroke the keystroke
	 */
	public KeyMap(String action, String description, Keystroke keyStroke) {
		super( new XElement(KEYMAP));
		
		setAction(action);
		setDescription(description);
		getElement().add(keyStroke.getElement());
	}
	
	/**
	 * Creates the KeyMap
	 *
	 * @param action the action required
	 * @param description a description of the action 
	 * @param keystroke the keystroke
	 * @param keystroke2 the second keystroke
	 */
	public KeyMap(String action, String description, Keystroke keystroke,Keystroke keystroke2) {
		super( new XElement(KEYMAP));
		
		setAction(action);
		setDescription(description);
		getElement().add(keystroke.getElement());
		getElement().add(keystroke2.getElement());
	}
	
	/**
	 * Creates the KeyMap
	 *
	 * @param keyMapElement Thr KeyMap Element
	 */
	public KeyMap(XElement keyMapElement) {
		super( new XElement(KEYMAP));
		
		XElement action = (XElement)keyMapElement.element(ACTION);
		String actionName = action.getText(); 
		setAction(actionName);
		
		XElement desc = (XElement)keyMapElement.element(DESCRIPTION);
		String description = desc.getText(); 
		setDescription(description);
		
		Iterator keystrokeElements = keyMapElement.elementIterator(Keystroke.KEYSTROKE);
		while (keystrokeElements.hasNext())
		{
			XElement keystrokeElement = (XElement)keystrokeElements.next();
			Keystroke keystroke = new Keystroke(keystrokeElement);
			getElement().add(keystroke.getElement());
		}
	}
	
	//	Set the action for this keymap
	private void setAction(String action) {
			set(ACTION, action);
	}
	
	
	/**
	 * Gets the action name
	 *
	 * @return The action
	 */
	public String getAction()
	{
		return getText(ACTION);
	}
	
	//	Set the description for this keymap
	private void setDescription(String description) {
		if (description != null)
		{
			set(DESCRIPTION, description);
		}
	}
	
	/**
	 * Gets the description
	 *
	 * @return The description
	 */
	public String getDescription()
	{
		return getText(DESCRIPTION);
	}
	
	
	/**
	 * Gets the keystroke elements
	 *
	 * @return The keystroke as a vector of XElements
	 */
	public Vector getKeystrokesElements()
	{
		Vector keystrokes = new Vector(); 
			
		Iterator keystrokeElements = getElement().elementIterator(Keystroke.KEYSTROKE);
		while (keystrokeElements.hasNext())
		{
			XElement keystrokeElement = (XElement)keystrokeElements.next();
			keystrokes.add(keystrokeElement);
		}
		
		return keystrokes;
	}
	
	/**
	 * Gets the keystroke objects
	 *
	 * @return The keystroke as a vector of Keystroke objects
	 */
	public Vector getKeystrokes()
	{
		Vector keystrokes = new Vector(); 
			
		Iterator keystrokeElements = getElement().elementIterator(Keystroke.KEYSTROKE);
		while (keystrokeElements.hasNext())
		{
			XElement keystrokeElement = (XElement)keystrokeElements.next();
			Keystroke stroke = new Keystroke(keystrokeElement);
			keystrokes.add(stroke);
		}
		
		return keystrokes;
	}
}
