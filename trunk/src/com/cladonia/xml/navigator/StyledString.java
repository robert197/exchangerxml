/*
 * $Id: StyledString.java,v 1.1 2004/03/25 18:46:20 edankert Exp $
 *
 * Copyright (C) 2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import java.awt.Color;
import java.awt.Font;

/**
 * Holds information for a styled string.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:46:20 $
 * @author Dogsbay
 */
 public class StyledString {
	private Font font = null;
	private String text = null;
	private Color color = null;

	public StyledString( String text, Color color, Font font) {
		this.text = text;
		this.color = color;
		this.font = font;
	}
	
	public StyledString( String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public Color getColor() {
		return color;
	}

	public Font getFont() {
		return font;
	}
}
