/*
 * $Id: StyledString.java,v 1.1 2004/09/09 15:49:05 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.xdiff;

import java.awt.Color;
import java.awt.Font;

/**
 * Holds information for a styled string.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/09/09 15:49:05 $
 * @author Dogsbay
 */
 public class StyledString {
	private Font font = null;
	private String text = null;
	private Color color = null;
	private boolean strikeThrough = false;

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
	
	public void setStrikeThrough(boolean condition)
	{
		this.strikeThrough = condition;
	}
	
	public boolean isStrikeThrough()
	{
		return strikeThrough;
	}
}
