/*
 * $Id: XmlCellRendererUI.java,v 1.1 2004/03/25 18:50:40 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR Skeleton code. (org.xngr.skeleton.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */

package com.cladonia.xml.viewer;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLabelUI;

import com.cladonia.xml.viewer.XmlElementNode.Line;

/**
 * Renderers the XML information in the Element Node.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:50:40 $
 * @author Dogsbay
 */
public class XmlCellRendererUI extends MetalLabelUI {
    protected static XmlCellRendererUI labelUI = new XmlCellRendererUI();
	private static final Color SELECTION_BACKGROUND = UIManager.getColor("Tree.selectionBackground");
	private static final Color SELECTION_FOREGROUND = UIManager.getColor("Tree.selectionForeground");;

    public static ComponentUI createUI( JComponent c) {
        return labelUI;
    }

    /**
     * Paint clippedText at textX, textY with the labels foreground color.
     */
    protected void paintLine( JLabel l, Graphics g, Line line, int x, int y) {
		StyledString[] strings = line.getStyledStrings();
		
		if ( ((XmlCellRenderer)l).isSelected()) {
			g.setColor( SELECTION_BACKGROUND);
		    FontMetrics fm = g.getFontMetrics();
		    g.fillRect( x, (y - fm.getAscent()), fm.stringWidth( line.getText()) - 1, fm.getHeight());
		}

		for ( int i = 0; i < strings.length; i++) {

//			if ( ((XmlCellRenderer)l).isSelected()) {
//				g.setFont( strings[i].getFont().deriveFont( Font.BOLD));
//				g.setColor( strings[i].getColor().darker().darker());
//			} else {
			g.setFont( strings[i].getFont());

			if ( ((XmlCellRenderer)l).isSelected()) {
				g.setColor( SELECTION_FOREGROUND);
			} else {
				g.setColor( strings[i].getColor());
			}

			g.drawString( strings[i].getText(), x, y);
			x = x + (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getWidth();
		}		
    }

    /** 
     * Paint the label text in the foreground color, if the label
     * is opaque then paint the entire background with the background
     * color.  The Label text is drawn by paintEnabledText() or
     * paintDisabledText().  The locations of the label parts are computed
     * by layoutCL.
     */
    public void paint( Graphics g, JComponent c) {
        XmlCellRenderer renderer = (XmlCellRenderer)c;
        Line[] lines = renderer.getLines();

//        if ( renderer.isSelected()) {
//            Color bsColor = UIManager.getColor("Tree.selectionBorderColor");
//
//       		g.setColor( bsColor);
//   		    g.drawRect( 0, 0, c.getWidth() - 1, c.getHeight() - 1);
//        }

        FontMetrics fm = g.getFontMetrics();

        if ( lines != null && lines.length > 0) {
			int x = 0;
			int y = fm.getAscent();
		
			for ( int i = 0; i < lines.length; i++) {
	    		paintLine( renderer, g, lines[i], x, y);
				y = y + fm.getHeight();
			}
        }
    }


    public Dimension getPreferredSize( JComponent c) {
        XmlCellRenderer renderer = (XmlCellRenderer)c;
		Line[] lines = renderer.getLines();
		Graphics gc = renderer.getGraphics();
		int height = 0;
		int width = 0;
		
		if ( gc != null) {
			FontMetrics fm = gc.getFontMetrics();
			
			if ( lines != null && lines.length > 0) {
			
				for ( int i = 0; i < lines.length; i++) {
					width = Math.max( width, fm.stringWidth( lines[i].getText()));
				}
				
				height = fm.getHeight() * lines.length;
			}
		}
		
//		System.out.println( "XmlCellRendererUI.getPreferredSize() ["+width+", "+height+"]");
		
        return new Dimension( width, height);
    }


    /**
     * @return getPreferredSize(c)
     */
    public Dimension getMinimumSize(JComponent c) {
        return getPreferredSize(c);
    }

    /**
     * @return getPreferredSize(c)
     */
    public Dimension getMaximumSize(JComponent c) {
        return getPreferredSize(c);
    }

}
