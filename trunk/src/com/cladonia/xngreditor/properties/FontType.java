/*
 * $Id: FontType.java,v 1.1 2004/03/25 18:56:13 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import java.awt.Color;
import java.awt.Font;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the Xml Plus configuration document.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:56:13 $
 * @author Dogsbay
 */
public class FontType extends Properties {

	public static final String FONT_TYPE	= "font-type";
	
	private static final String NAME		= "name";
	private static final String STYLE		= "style";
	private static final String COLOR		= "color";

	private Font font = null;

	/**
	 * Creates the Font Property with initial values.
	 *
	 * @param name the name of the font-type.
	 * @param style the font style.
	 * @param color the font color.
	 */
	public FontType( String name, int style, Color color) {
		super( new XElement( FONT_TYPE));
		
		setName( name);
		setStyle( style);
		setColor( color);
	}
	
	/**
	 * Creates the Font Property with initial values.
	 *
	 * @param element the font-type element.
	 */
	public FontType( XElement element) {
		super( element);
	}

	/**
	 * Get the name of this font-type.
	 *
	 * @return the name of the type.
	 */
	public String getName() {
		return getText( NAME);
	}

	// Set the name of this font-type.
	private void setName( String name) {
		set( NAME, name);
	}

	// Set the base font for this type.
	public void setFont( Font font) {
		this.font = font.deriveFont( getStyle());
	}

	// Set the base font for this type.
	public Font getFont() {
		return font;
	}

	/**
	 * Get the font-style.
	 *
	 * @return the style of the font.
	 */
	public int getStyle() {
		return getInteger( STYLE);
	}

	/**
	 * Set the font style.
	 *
	 * @param style the font-style.
	 */
	public void setStyle( int style) {
		set( STYLE, style);
		
		if ( font != null) {
			this.font = font.deriveFont( getStyle());
		}
	}

	/**
	 * Get the color for this font-type.
	 *
	 * @return the color.
	 */
	public Color getColor() {
		return getColor( COLOR);
	}

	/**
	 * Set the color font property.
	 *
	 * @param color the font style color.
	 */
	public void setColor( Color color) {
		set( COLOR, color);
	}
}

