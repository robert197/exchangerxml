/*
 * $Id: PrintPreferences.java,v 1.1 2004/03/25 18:56:13 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import java.awt.Font;

import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;

/**
 * Handles the Print Preferences.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:56:13 $
 * @author Dogsbay
 */
public class PrintPreferences extends Properties {

	public static final String PRINT_PREFERENCES	= "print-preferences";

	public final static Font DEFAULT_FONT			= new Font( "monospaced", Font.PLAIN, 10);

	private static final String FONT_NAME			= "font-name";
	private static final String FONT_STYLE			= "font-style";
	private static final String FONT_SIZE			= "font-size";
	
	private static final String HEADER				= "print-header";
	private static final String LINE_NUMBERS		= "print-line-numbers";
	private static final String WRAP_TEXT			= "print-wrap-text";

	private Font font	= null;

	/**
	 * Creates the Print Preferences.
	 *
	 * @param element the print preferences element.
	 */
	public PrintPreferences( XElement element) {
		super( element);
	}

	/**
	 * Get the current font.
	 *
	 * @return the current font.
	 */
	public Font getFont() {
		if ( font == null) {
			if ( getName() != null && getName().length() > 0) {
				font = new Font( getName(), getStyle(), getSize());
			} else {
				font = DEFAULT_FONT;
			}
		}
		return font;
	}

	/**
	 * Get the name of this font-type.
	 *
	 * @return the name of the type.
	 */
	public void setFont( Font f) {
		font = f;
		
		setName( font.getName());
		setStyle( font.getStyle());
		setSize( font.getSize());
	}

	// Set/Get the name of this font-type.
	private void setName( String name) {
		set( FONT_NAME, name);
	}

	private String getName() {
		return getText( FONT_NAME);
	}

	// Set/Get the style of this font-type.
	private void setStyle( int style) {
		set( FONT_STYLE, style);
	}

	private int getStyle() {
		return getInteger( FONT_STYLE);
	}

	// Set/Get the size of this font-type.
	private void setSize( int size) {
		set( FONT_SIZE, size);
	}

	private int getSize() {
		return getInteger( FONT_SIZE);
	}

	/**
	 * Enables/Disables the print header info.
	 *
	 * @param enable the print header info.
	 */
	public void setPrintHeader( boolean enable) {
		set( HEADER, enable);
	}

	/**
	 * gets the print header info.
	 *
	 * @return true when the print header info should be displayed.
	 */
	public boolean isPrintHeader() {
		return getBoolean( HEADER, false);
	}

	/**
	 * Enables/Disables the print line numbers.
	 *
	 * @param enable the print line numbers.
	 */
	public void setPrintLineNumbers( boolean enable) {
		set( LINE_NUMBERS, enable);
	}

	/**
	 * gets the print line numbers.
	 *
	 * @return true when the line numbers should be displayed.
	 */
	public boolean isPrintLineNumbers() {
		return getBoolean( LINE_NUMBERS, false);
	}

	/**
	 * Enables/Disables the text wrapping.
	 *
	 * @param enable the text wrapping.
	 */
	public void setWrapText( boolean enable) {
		set( WRAP_TEXT, enable);
	}

	/**
	 * gets the wrap text.
	 *
	 * @return true when the text should be wrapped.
	 */
	public boolean isWrapText() {
		return getBoolean( WRAP_TEXT, true);
	}
} 
