/*
 * $Id: TextPreferences.java,v 1.4 2004/11/03 10:00:31 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.util.Vector;

import javax.swing.JTextArea;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertiesFile;

/**
 * Handles the Xml Plus configuration document.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/11/03 10:00:31 $
 * @author Dogsbay
 */
public class TextPreferences extends PropertiesFile {

	public static final String TEXT_PREFERENCES		= "text-preferences";

	private Vector types	= null;

	public static final String CDATA			= "CDATA";
	public static final String COMMENT			= "Comment";
	public static final String ENTITY			= "Entity";
	public static final String SPECIAL			= "Special";
	public static final String PREFIX			= "Prefix";

	private static final String ANTIALIASING	= "antialiasing";

	public static final String NAMESPACE_NAME 	= "Namespace Name";
	public static final String NAMESPACE_VALUE	= "Namespace Value";
	public static final String NAMESPACE_PREFIX = PREFIX;

	public static final String ATTRIBUTE_NAME 	= "Attribute Name";
	public static final String ATTRIBUTE_VALUE	= "Attribute Value";
	public static final String ATTRIBUTE_PREFIX = PREFIX;

	public static final String ELEMENT_NAME 	= "Element Name";
	public static final String ELEMENT_VALUE	= "Element Value";
	public static final String ELEMENT_PREFIX 	= PREFIX;

	public static final String PI_NAME 		= "PI Name";
	public static final String PI_VALUE		= "PI Value";
	public static final String PI_TARGET	= "PI Target";

	public static final String STRING_VALUE 		= "DTD: String Value";
	public static final String ENTITY_VALUE 		= "DTD: Entity Reference";

	public static final String ENTITY_DECLARATION	= "DTD: ENTITY Declaration";
	public static final String ENTITY_NAME			= "DTD: Entity Name";
	public static final String ENTITY_TYPE 			= "DTD: Entity Type";

	public static final String ATTLIST_DECLARATION	= "DTD: ATTLIST Declaration";
	public static final String ATTLIST_NAME			= "DTD: Attribute Name";
	public static final String ATTLIST_TYPE			= "DTD: Attribute Type";
	public static final String ATTLIST_VALUE		= "DTD: Attribute Enumerated Value";
	public static final String ATTLIST_DEFAULT		= "DTD: Attribute Defaults (#REQUIRED, #IMPLIED, #FIXED)";

	public static final String ELEMENT_DECLARATION			= "DTD: ELEMENT Declaration";
	public static final String ELEMENT_DECLARATION_NAME		= "DTD: Element Name";
	public static final String ELEMENT_DECLARATION_TYPE		= "DTD: Element Type (EMPTY, ANY)";
	public static final String ELEMENT_DECLARATION_PCDATA	= "DTD: #PCDATA";
	public static final String ELEMENT_DECLARATION_OPERATOR	= "DTD: Element Operator";

	public static final String NOTATION_DECLARATION			= "DTD: NOTATION Declaration";
	public static final String NOTATION_DECLARATION_NAME	= "DTD: Notation Name";
	public static final String NOTATION_DECLARATION_TYPE	= "DTD: Notation Type (PUBLIC, SYSTEM)";

	public static final String DOCTYPE_DECLARATION			= "DTD: DOCTYPE Declaration";
	public static final String DOCTYPE_DECLARATION_TYPE		= "DTD: Doctype Type (PUBLIC, SYSTEM)";

	public static final Color DEFAULT_CDATA_COLOR		= new Color( 102, 102, 102);
	public static final Color DEFAULT_COMMENT_COLOR		= new Color( 102, 102, 102);
	public static final Color DEFAULT_ENTITY_COLOR		= new Color( 102, 102, 102);
	public static final Color DEFAULT_SPECIAL_COLOR		= new Color( 51, 51, 255);
	public static final Color DEFAULT_PREFIX_COLOR		= new Color( 152, 51, 51);

	public static final Color DEFAULT_NAMESPACE_NAME_COLOR	= new Color( 255, 51, 51);
	public static final Color DEFAULT_NAMESPACE_VALUE_COLOR	= new Color( 255, 51, 51);

	public static final Color DEFAULT_ATTRIBUTE_NAME_COLOR	= new Color( 152, 51, 51);
	public static final Color DEFAULT_ATTRIBUTE_VALUE_COLOR	= Color.black;

	public static final Color DEFAULT_ELEMENT_NAME_COLOR	= new Color( 152, 51, 51);
	public static final Color DEFAULT_ELEMENT_VALUE_COLOR	= Color.black;

	public static final Color PI_TARGET_COLOR 		= new Color(0,102,255);
	public static final Color PI_NAME_COLOR 		= new Color(0,102,255);
	public static final Color PI_VALUE_COLOR 		= new Color(0,102,255);

	public static final Color STRING_VALUE_COLOR 		= new Color( 102, 102, 102);
	public static final Color ENTITY_VALUE_COLOR 		= new Color( 0, 102, 51);

	public static final Color ENTITY_DECLARATION_COLOR	= new Color( 0, 51, 0);
	public static final Color ENTITY_NAME_COLOR			= new Color( 0, 102, 0);
	public static final Color ENTITY_TYPE_COLOR 		= new Color( 51, 153, 51);

	public static final Color ATTLIST_DECLARATION_COLOR	= new Color( 0, 0, 102);
	public static final Color ATTLIST_NAME_COLOR		= new Color( 153, 0, 0);
	public static final Color ATTLIST_TYPE_COLOR		= new Color( 153, 102, 0);
	public static final Color ATTLIST_VALUE_COLOR		= Color.black;
	public static final Color ATTLIST_DEFAULT_COLOR		= new Color( 102, 102, 0);

	public static final Color ELEMENT_DECLARATION_COLOR				= new Color( 0, 0, 102);
	public static final Color ELEMENT_DECLARATION_NAME_COLOR		= new Color( 0, 0, 204);
	public static final Color ELEMENT_DECLARATION_TYPE_COLOR		= new Color( 51, 102, 153);
	public static final Color ELEMENT_DECLARATION_PCDATA_COLOR		= new Color( 102, 102, 0);
	public static final Color ELEMENT_DECLARATION_OPERATOR_COLOR	= new Color( 204, 0, 0);

	public static final Color NOTATION_DECLARATION_COLOR		= new Color( 0, 51, 51);
	public static final Color NOTATION_DECLARATION_NAME_COLOR	= new Color( 0, 102, 102);
	public static final Color NOTATION_DECLARATION_TYPE_COLOR	= new Color( 0, 153, 153);

	public static final Color DOCTYPE_DECLARATION_COLOR			= new Color( 51, 0, 51);
	public static final Color DOCTYPE_DECLARATION_TYPE_COLOR	= new Color( 102, 0, 102);

	public static final int DEFAULT_CDATA_STYLE			= Font.PLAIN;
	public static final int DEFAULT_COMMENT_STYLE		= Font.PLAIN;
	public static final int DEFAULT_ENTITY_STYLE		= Font.PLAIN;
	public static final int DEFAULT_SPECIAL_STYLE		= Font.PLAIN;
	public static final int DEFAULT_PREFIX_STYLE		= Font.PLAIN;

	public static final int DEFAULT_NAMESPACE_NAME_STYLE	= Font.PLAIN;
	public static final int DEFAULT_NAMESPACE_VALUE_STYLE	= Font.PLAIN;

	public static final int DEFAULT_ATTRIBUTE_NAME_STYLE	= Font.PLAIN;
	public static final int DEFAULT_ATTRIBUTE_VALUE_STYLE	= Font.PLAIN;

	public static final int DEFAULT_ELEMENT_NAME_STYLE		= Font.PLAIN;
	public static final int DEFAULT_ELEMENT_VALUE_STYLE		= Font.PLAIN;

	private static Font defaultFont	= null;

	private static Font font					= null;
	private static TextPreferences preferences	= null;

	public static final int DEFAULT_TAB_SIZE	= 4;

	private static final String SPACES		= "spaces";

	private static final String FONT_NAME	= "font-name";
	private static final String FONT_SIZE	= "font-size";
	private static final String FONT_STYLE	= "font-style";

	private static final String CONVERT_TAB	= "convert-tab";

	/**
	 * Creates the Font Property with initial values.
	 *
	 * @param element the font-type element.
	 */
	public TextPreferences(String fileName, String rootName) {
		super(loadPropertiesFile(fileName, rootName));
		
		if ( getFontTypes().size() == 0) {
			setFont( getDefaultFont());
			
			// check font for bold/plain/italic length ... otherwise only plain.
			addFontType( new FontType( ELEMENT_NAME, DEFAULT_ELEMENT_NAME_STYLE, DEFAULT_ELEMENT_NAME_COLOR));
			addFontType( new FontType( ELEMENT_VALUE, DEFAULT_ELEMENT_VALUE_STYLE, DEFAULT_ELEMENT_VALUE_COLOR));
			addFontType( new FontType( ATTRIBUTE_NAME, DEFAULT_ATTRIBUTE_NAME_STYLE, DEFAULT_ATTRIBUTE_NAME_COLOR));
			addFontType( new FontType( ATTRIBUTE_VALUE, DEFAULT_ATTRIBUTE_VALUE_STYLE, DEFAULT_ATTRIBUTE_VALUE_COLOR));
			addFontType( new FontType( PREFIX, DEFAULT_PREFIX_STYLE, DEFAULT_PREFIX_COLOR));
			addFontType( new FontType( NAMESPACE_NAME, DEFAULT_NAMESPACE_NAME_STYLE, DEFAULT_NAMESPACE_NAME_COLOR));
			addFontType( new FontType( NAMESPACE_VALUE, DEFAULT_NAMESPACE_VALUE_STYLE, DEFAULT_NAMESPACE_VALUE_COLOR));

			addFontType( new FontType( CDATA, DEFAULT_CDATA_STYLE, DEFAULT_CDATA_COLOR));
			addFontType( new FontType( COMMENT, DEFAULT_COMMENT_STYLE, DEFAULT_COMMENT_COLOR));
			addFontType( new FontType( ENTITY, DEFAULT_ENTITY_STYLE, DEFAULT_ENTITY_COLOR));
			addFontType( new FontType( SPECIAL, DEFAULT_SPECIAL_STYLE, DEFAULT_SPECIAL_COLOR));

			addFontType( new FontType( PI_TARGET, Font.PLAIN, PI_TARGET_COLOR));
			addFontType( new FontType( PI_NAME, Font.PLAIN, PI_NAME_COLOR));
			addFontType( new FontType( PI_VALUE, Font.PLAIN, PI_VALUE_COLOR));

			addFontType( new FontType( STRING_VALUE, Font.PLAIN, STRING_VALUE_COLOR));
			addFontType( new FontType( ENTITY_VALUE, Font.PLAIN, ENTITY_VALUE_COLOR));
			addFontType( new FontType( ENTITY_DECLARATION, Font.PLAIN, ENTITY_DECLARATION_COLOR));
			addFontType( new FontType( ENTITY_NAME, Font.PLAIN, ENTITY_NAME_COLOR));
			addFontType( new FontType( ENTITY_TYPE, Font.PLAIN, ENTITY_TYPE_COLOR));
			addFontType( new FontType( ATTLIST_DECLARATION, Font.PLAIN, ATTLIST_DECLARATION_COLOR));
			addFontType( new FontType( ATTLIST_NAME, Font.PLAIN, ATTLIST_NAME_COLOR));
			addFontType( new FontType( ATTLIST_TYPE, Font.PLAIN, ATTLIST_TYPE_COLOR));

			addFontType( new FontType( ATTLIST_VALUE, Font.PLAIN, ATTLIST_VALUE_COLOR));
			addFontType( new FontType( ATTLIST_DEFAULT, Font.PLAIN, ATTLIST_DEFAULT_COLOR));
			addFontType( new FontType( ELEMENT_DECLARATION, Font.PLAIN, ELEMENT_DECLARATION_COLOR));
			addFontType( new FontType( ELEMENT_DECLARATION_NAME, Font.PLAIN, ELEMENT_DECLARATION_NAME_COLOR));
			addFontType( new FontType( ELEMENT_DECLARATION_TYPE, Font.PLAIN, ELEMENT_DECLARATION_TYPE_COLOR));
			addFontType( new FontType( ELEMENT_DECLARATION_PCDATA, Font.PLAIN, ELEMENT_DECLARATION_PCDATA_COLOR));
			addFontType( new FontType( ELEMENT_DECLARATION_OPERATOR, Font.PLAIN, ELEMENT_DECLARATION_OPERATOR_COLOR));
			addFontType( new FontType( NOTATION_DECLARATION, Font.PLAIN, NOTATION_DECLARATION_COLOR));
			addFontType( new FontType( NOTATION_DECLARATION_NAME, Font.PLAIN, NOTATION_DECLARATION_NAME_COLOR));
		
			addFontType( new FontType( NOTATION_DECLARATION_TYPE, Font.PLAIN, NOTATION_DECLARATION_TYPE_COLOR));
			addFontType( new FontType( DOCTYPE_DECLARATION, Font.PLAIN, DOCTYPE_DECLARATION_COLOR));
			addFontType( new FontType( DOCTYPE_DECLARATION_TYPE, Font.PLAIN, DOCTYPE_DECLARATION_TYPE_COLOR));

			types = null;
		}
		
		font = new Font( getName(), getStyle(), getSize());

		Vector fonts = getFontTypes();
		
		for ( int i = 0; i < fonts.size(); i++) {
			((FontType)fonts.elementAt(i)).setFont( getFont());
		}
		
		preferences = this;
	}

	/**
	 * Get the default font.
	 *
	 * @return the default font.
	 */
	public static Font getDefaultFont() {
		if ( defaultFont == null) {
			if ( System.getProperty( "mrj.version") != null) {
				defaultFont = new Font( "Courier", Font.PLAIN, 12);
			} else {
				
				//this handles different cases in the font name
				GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
				Font[] fonts = env.getAllFonts();
				
				
				//this handles the difference in naming between 1.4 and 1.5+
				String v = System.getProperty( "java.class.version","44.0");

				if ( "49.0".compareTo(v) > 0) { // less than jdk 1.5...
					String defaultFontName = "monospaced";
					
					int cnt=0;
					boolean found = false;
					if(fonts != null) {
						while((found == false) && (cnt < fonts.length)) {
							
							if(defaultFontName.equalsIgnoreCase(fonts[cnt].getName())) {
								defaultFontName = fonts[cnt].getName();
								found = true;
							}
							cnt++;
						}
					}
					
					defaultFont = new Font( defaultFontName, Font.PLAIN, 12);					
				}
				else {
					String defaultFontName = "monospaced.plain";
					
					int cnt=0;
					boolean found = false;
					if(fonts != null) {
						while((found == false) && (cnt < fonts.length)) {
							
							if(defaultFontName.equalsIgnoreCase(fonts[cnt].getName())) {
								defaultFontName = fonts[cnt].getName();
								found = true;
							}
							cnt++;
						}
					}
					defaultFont = new Font( defaultFontName, Font.PLAIN, 12);					
				}
			}
		}

		return defaultFont;
	}

	/**
	 * Get the name of this font-type.
	 *
	 * @return the name of the type.
	 */
	public static Font getBaseFont() {
		if ( preferences != null) {
			return preferences.getFont();
		} else {
			return getDefaultFont();
		}
	}

	/**
	 * Get the name of this font-type.
	 *
	 * @return the name of the type.
	 */
	public Font getFont() {
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
		
		Vector fonts = getFontTypes();
		
		for ( int i = 0; i < fonts.size(); i++) {
			((FontType)fonts.elementAt(i)).setStyle( font.getStyle());
			((FontType)fonts.elementAt(i)).setFont( font);
		}
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
	 * Set the number of spaces to substitute for a tab.
	 *
	 * @param spaces the number of spaces.
	 */
	public void setSpaces( int spaces) {
		set( SPACES, spaces);
	}

	/**
	 * Gets the number of spaces to substitute for a tab.
	 *
	 * @return the number of spaces.
	 */
	public static int getTabSize() {
		return preferences.getSpaces();
	}

	public static boolean isAntialiasing() {
		return preferences.getBoolean( ANTIALIASING, false);
	}

	public void setAntialiasing( boolean enabled) {
		set( ANTIALIASING, enabled);
	}

	/**
	 * Gets the number of spaces to substitute for a tab.
	 *
	 * @return the number of spaces.
	 */
	public int getSpaces() {
		return getInteger( SPACES, DEFAULT_TAB_SIZE);
	}

	/**
	 * Gets the string for the tab, either a string of 
	 * spaces or one tab.
	 *
	 * @return the tab string.
	 */
	public static String getTabString() {
		if ( isConvertTab()) {
			int size = getTabSize();
			char[] chars = new char[size];
			
			for ( int i = 0; i < chars.length; i++) {
				chars[i] = ' ';
			}
			
			return new String( chars);
		}

		return "\t";
	}

	/**
	 * Gets the number of spaces to substitute for a tab.
	 *
	 * @return the number of spaces.
	 */
	public boolean convertTab() {
		return getBoolean( CONVERT_TAB, false);
	}

	public static boolean isConvertTab() {
		return preferences.convertTab();
	}

	public void setConvertTab( boolean enabled) {
		set( CONVERT_TAB, enabled);
	}

	/**
	 * Returns the font-type list.
	 *
	 * @return the font-types.
	 */
	public static FontType getFontType( String name) {
		Vector types = preferences.getFontTypes();
		
		for ( int i = 0; i < types.size(); i++) {
			FontType type = (FontType)types.elementAt(i);
			if ( type.getName().equals( name)) {
				return type;
			}
		}
		
		return null;
	}

	/**
	 * Returns the font-type list.
	 *
	 * @return the font-types.
	 */
	public Vector getFontTypes() {
		if ( types == null) {
			Vector result = new Vector();
			Vector list = getProperties( FontType.FONT_TYPE);
			boolean cdataFound = false;
			boolean dtdFound = false;

			for ( int i = 0; i < list.size(); i++) {
				FontType type = new FontType( ((Properties)list.elementAt(i)).getElement());
				if ( type.getName().equals( CDATA)) {
					cdataFound = true;
				}

				if ( type.getName().equals( STRING_VALUE)) {
					dtdFound = true;
				}

				result.addElement( type);
			}
			
			if ( !cdataFound && list.size() > 0) {
				addFontType( new FontType( CDATA, DEFAULT_CDATA_STYLE, DEFAULT_CDATA_COLOR));

				result = getFontTypes();
			}
			
			if ( !dtdFound && list.size() > 0) {
				addFontType( new FontType( PI_TARGET, Font.PLAIN, PI_TARGET_COLOR));
				addFontType( new FontType( PI_NAME, Font.PLAIN, PI_NAME_COLOR));
				addFontType( new FontType( PI_VALUE, Font.PLAIN, PI_VALUE_COLOR));

				addFontType( new FontType( STRING_VALUE, Font.PLAIN, STRING_VALUE_COLOR));
				addFontType( new FontType( ENTITY_VALUE, Font.PLAIN, ENTITY_VALUE_COLOR));

				addFontType( new FontType( ENTITY_DECLARATION, Font.PLAIN, ENTITY_DECLARATION_COLOR));
				addFontType( new FontType( ENTITY_NAME, Font.PLAIN, ENTITY_NAME_COLOR));
				addFontType( new FontType( ENTITY_TYPE, Font.PLAIN, ENTITY_TYPE_COLOR));

				addFontType( new FontType( ATTLIST_DECLARATION, Font.PLAIN, ATTLIST_DECLARATION_COLOR));
				addFontType( new FontType( ATTLIST_NAME, Font.PLAIN, ATTLIST_NAME_COLOR));
				addFontType( new FontType( ATTLIST_TYPE, Font.PLAIN, ATTLIST_TYPE_COLOR));
				addFontType( new FontType( ATTLIST_VALUE, Font.PLAIN, ATTLIST_VALUE_COLOR));
				addFontType( new FontType( ATTLIST_DEFAULT, Font.PLAIN, ATTLIST_DEFAULT_COLOR));

				addFontType( new FontType( ELEMENT_DECLARATION, Font.PLAIN, ELEMENT_DECLARATION_COLOR));
				addFontType( new FontType( ELEMENT_DECLARATION_NAME, Font.PLAIN, ELEMENT_DECLARATION_NAME_COLOR));
				addFontType( new FontType( ELEMENT_DECLARATION_TYPE, Font.PLAIN, ELEMENT_DECLARATION_TYPE_COLOR));
				addFontType( new FontType( ELEMENT_DECLARATION_PCDATA, Font.PLAIN, ELEMENT_DECLARATION_PCDATA_COLOR));
				addFontType( new FontType( ELEMENT_DECLARATION_OPERATOR, Font.PLAIN, ELEMENT_DECLARATION_OPERATOR_COLOR));

				addFontType( new FontType( NOTATION_DECLARATION, Font.PLAIN, NOTATION_DECLARATION_COLOR));
				addFontType( new FontType( NOTATION_DECLARATION_NAME, Font.PLAIN, NOTATION_DECLARATION_NAME_COLOR));
				addFontType( new FontType( NOTATION_DECLARATION_TYPE, Font.PLAIN, NOTATION_DECLARATION_TYPE_COLOR));

				addFontType( new FontType( DOCTYPE_DECLARATION, Font.PLAIN, DOCTYPE_DECLARATION_COLOR));
				addFontType( new FontType( DOCTYPE_DECLARATION_TYPE, Font.PLAIN, DOCTYPE_DECLARATION_TYPE_COLOR));

				result = getFontTypes();
			}

			types = result;
		}
	
		return types;
	}

	/**
	 * Adds a font-type object to this element.
	 *
	 * @param props the font-type.
	 */
	public void addFontType( FontType props) {
		add( props);
	}

	/**
	 * Adds a font-type object to this element.
	 *
	 * @param props the font-type.
	 */
	public void removeFontType( FontType props) {
		remove( props);
	}
	
	private boolean hasSameWidth( Font font, int style1, int style2) {
	    String testString = "<Test test:nms=\"http://test.org\"/>";
		JTextArea pane = new JTextArea();
		
	    Font font1 = font.deriveFont( style1, 12);
	    FontMetrics fm = pane.getFontMetrics( font1);
	    int width1 = fm.stringWidth( testString);

	    Font font2 = font.deriveFont( style2, 12);
	    fm = pane.getFontMetrics( font2);
	    int width2 = fm.stringWidth( testString);

		if ( width1 == width2) {	// && italicWidth == italicBoldWidth) { 
			return true;
		} 
		
		return false;
	}

} 
