/*
 * $Id: ExchangerEditorKit.java,v 1.2 2004/10/19 14:39:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.awt.Color;
import java.awt.Font;

import javax.swing.text.DefaultEditorKit;

/**
 * The XML editor kit supports handling of editing XML content.  
 * It supports syntax highlighting, tab replacements and automatic 
 * indents.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/19 14:39:44 $
 * @author Dogsbay
 */
public abstract class ExchangerEditorKit extends DefaultEditorKit {

    /**
     * Get the MIME type of the data that this
     * kit represents support for. This kit supports
     * the type <code>text/xml</code>.
	 *
	 * @return the type.
     */
    public abstract String getContentType();

    public abstract void setHighlight( boolean enabled);

    public abstract boolean isHighlight();

    public abstract void setErrorHighlighting( boolean enabled);

    public abstract boolean isErrorHighlighting();

    public abstract void cleanup();
	
    public abstract void setFont( Font font);
    public abstract Font getFont();

    public abstract void setAttributes( int id, Color color, int style);
}







