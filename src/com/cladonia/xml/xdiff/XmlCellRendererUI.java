/*
 * $Id: XmlCellRendererUI.java,v 1.2 2004/09/16 13:33:42 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xml.xdiff;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalLabelUI;

import com.cladonia.xml.xdiff.XmlElementNode.Line;

/**
 * Renderers the XML information in the Element Node.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/16 13:33:42 $
 * @author Dogsbay
 */
public class XmlCellRendererUI extends MetalLabelUI {
    protected static XmlCellRendererUI labelUI = new XmlCellRendererUI();
	//private static final Color SELECTION_BACKGROUND = UIManager.getColor("Tree.selectionBackground");
	private static final Color SELECTION_FOREGROUND = UIManager.getColor("Tree.selectionForeground");
    private static final Color SELECTION_BACKGROUND = new Color(204,204,204);

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
				if (strings[i].getColor() == Color.BLACK)
				{
					g.setColor( SELECTION_FOREGROUND);
				}
				else
				{
					g.setColor( strings[i].getColor());
				}
			} else {
				g.setColor( strings[i].getColor());
			}

			if (strings[i].isStrikeThrough())
			{
				g.drawString( strings[i].getText(), x, y);
				
				int height = (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getHeight();
			    int width = (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getWidth();
			      
			    g.drawLine( x, y-(height/3), x+width, y-(height/3));
			    
			    x = x + (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getWidth();
			}
			else
			{	
				g.drawString( strings[i].getText(), x, y);
				x = x + (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getWidth();
			}
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
