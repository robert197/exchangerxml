/*
 * $Id: XmlCellRendererUI.java,v 1.3 2004/09/28 13:54:28 edankert Exp $
 *
 * Copyright (C) 2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.metal.MetalLabelUI;

import com.cladonia.xml.XAttribute;

/**
 * Renderers the XML information in the Element Node.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/28 13:54:28 $
 * @author Dogsbay
 */
public class XmlCellRendererUI extends MetalLabelUI {
    protected static XmlCellRendererUI labelUI = new XmlCellRendererUI();
    private static final Color SELECTION_BACKGROUND = UIManager.getColor("Tree.selectionBackground");
    private static final Color SELECTION_FOREGROUND = UIManager.getColor("Tree.selectionForeground");
    
    public static ComponentUI createUI( JComponent c) {
        return labelUI;
    }

    public void paint( Graphics g, JComponent c) {
    	XmlCellRenderer renderer = (XmlCellRenderer)c;
        Icon icon = renderer.getIcon();
        
        g.setColor( renderer.getBackground());
		Dimension size = getPreferredSize( renderer);

//		if ( renderer.isSelected()) {
		    g.fillRect( 10, 0, size.width, size.height);
//		}

        if (icon == null) {
            return;
        }

        FontMetrics fm = g.getFontMetrics();
        
        Rectangle rect = new Rectangle();
        Rectangle textRect = new Rectangle();
        Rectangle iconRect = new Rectangle();

        rect.x = 0;
        rect.y = 0;
        rect.width = c.getWidth();
        rect.height = c.getHeight();

        iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;
        textRect.x = textRect.y = textRect.width = textRect.height = 0;

        if (icon != null) {
        	iconRect.y = (size.height - icon.getIconHeight()) / 2;
        	iconRect.x = 1;
            icon.paintIcon( c, g, iconRect.x, iconRect.y);
            
            
        }
		int textX = 11;
		int textY = textRect.y + fm.getAscent();
		
		// paint the element name
		g.setColor( renderer.getForeground());
		g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
		fm = g.getFontMetrics();
		
		if ( renderer.showElementNames()) {
		    paintText( renderer, g, renderer.getName(), textX, textY);
		    textX = textX + fm.stringWidth( renderer.getName());
		}

		// paint possible attributes...
	    Vector attributes = renderer.getAttributes();
	    for ( int i = 0; i < attributes.size(); i++) {
			g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
			fm = g.getFontMetrics();
			
		    String name = " \"";

		    if ( renderer.showAttributeNames()) {
			    name = " "+((XAttribute)attributes.elementAt(i)).getName()+"=\"";
			}

			paintText( renderer, g, name, textX, textY);
		    textX = textX + fm.stringWidth( name);

			g.setFont( renderer.getFont().deriveFont( Font.BOLD));
			fm = g.getFontMetrics();

		    String value = clipValue( ((XAttribute)attributes.elementAt(i)).getValue());

		    paintText( renderer, g, value, textX, textY);
		    textX = textX + fm.stringWidth( value);

			g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
			fm = g.getFontMetrics();

		    value = "\"";

		    paintText( renderer, g, value, textX, textY);
		    textX = textX + fm.stringWidth( value);

	    }

		// paint possible attributes...
	    String value = clipValue( renderer.getValue());

	    if ( value != null && value.length() > 0) {
//		    g.setColor( Color.gray);
			g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
			fm = g.getFontMetrics();

		    String bracket = " [";

		    paintText( renderer, g, bracket, textX, textY);
		    textX = textX + fm.stringWidth( bracket);

			g.setFont( renderer.getFont().deriveFont( Font.BOLD));
			fm = g.getFontMetrics();

			g.setColor( renderer.getForeground());

		    paintText( renderer, g, value, textX, textY);
		    textX = textX + fm.stringWidth( value);

			g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
			fm = g.getFontMetrics();

		    bracket = "]";

		    paintText( renderer, g, bracket, textX, textY);
		    textX = textX + fm.stringWidth( bracket);
	    }
    }
    
    protected void paintText(JLabel l, Graphics g, String s, int textX, int textY)
    {
        BasicGraphicsUtils.drawStringUnderlineCharAt(g, s, -1, textX, textY);
    }

    private int calculateWidth( XmlCellRenderer renderer, Graphics g) {
    	int width = 11; // icon
    	
    	if ( renderer.getName() == null) {
    		return 0;
    	}

		// paint the element name
		g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
		FontMetrics fm = g.getFontMetrics();

	    if ( renderer.showElementNames()) {
	    	width = width + fm.stringWidth( renderer.getName());
	    }
	    
		// paint possible attributes...
	    Vector attributes = renderer.getAttributes();
	    for ( int i = 0; i < attributes.size(); i++) {
			g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
			fm = g.getFontMetrics();

		    String name = " \"\"";

		    if ( renderer.showAttributeNames()) {
		    	name = " "+((XAttribute)attributes.elementAt(i)).getName()+"=\"\"";
		    }

		    width = width + fm.stringWidth( name);

			g.setFont( renderer.getFont().deriveFont( Font.BOLD));
			fm = g.getFontMetrics();
		    String value = clipValue( ((XAttribute)attributes.elementAt(i)).getValue());

		    width = width + fm.stringWidth( value);
	    }

		// paint possible attributes...
	    String value = clipValue( renderer.getValue());

	    if ( value != null && value.length() > 0) {
			g.setFont( renderer.getFont().deriveFont( Font.PLAIN));
			fm = g.getFontMetrics();

		    String brackets = " []";

		    width = width + fm.stringWidth( brackets);

			g.setFont( renderer.getFont().deriveFont( Font.BOLD));
			fm = g.getFontMetrics();

		    width = width + fm.stringWidth( value);
	    }
	    
	    return width;
    }
    
    private String clipValue( String value) {
    	if ( value != null) {
    		value = value.trim();

    		int index = value.indexOf( '\n');
    		
    		if ( index == -1) {
        		index = value.indexOf( '\r');
    		}
    		
    		if ( index != -1) {
    			value = value.substring( 0, index);
    		}
    	}
    	
    	return value;
    }

    /**
     * Paint clippedText at textX, textY with the labels foreground color.
     */
//    protected void paintLine( JLabel l, Graphics g, Line line, int x, int y) {
//		StyledString[] strings = line.getStyledStrings();
//		
//		if ( ((XmlCellRenderer)l).isSelected()) {
//			g.setColor( SELECTION_BACKGROUND);
//		    FontMetrics fm = g.getFontMetrics();
//		    g.fillRect( x, (y - fm.getAscent()), fm.stringWidth( line.getText()) - 1, fm.getHeight());
//		}
//
//		for ( int i = 0; i < strings.length; i++) {
//
//			g.setFont( strings[i].getFont());
//
//			if ( ((XmlCellRenderer)l).isSelected()) {
//				g.setColor( SELECTION_FOREGROUND);
//			} else {
//				g.setColor( strings[i].getColor());
//			}
//
//			g.drawString( strings[i].getText(), x, y);
//			x = x + (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getWidth();
//		}		
//    }

    /** 
     * Paint the label text in the foreground color, if the label
     * is opaque then paint the entire background with the background
     * color.  The Label text is drawn by paintEnabledText() or
     * paintDisabledText().  The locations of the label parts are computed
     * by layoutCL.
     */
//    public void paint( Graphics g, JComponent c) {
//        XmlCellRenderer renderer = (XmlCellRenderer)c;
//        Line[] lines = renderer.getLines();
//
//        FontMetrics fm = g.getFontMetrics();
//
//        if ( lines != null && lines.length > 0) {
//			int x = 0;
//			int y = fm.getAscent();
//		
//			for ( int i = 0; i < lines.length; i++) {
//				if ( ((XmlCellRenderer)l).isSelected()) {
//					g.setColor( SELECTION_BACKGROUND);
//				    FontMetrics fm = g.getFontMetrics();
//				    g.fillRect( x, (y - fm.getAscent()), fm.stringWidth( line.getText()) - 1, fm.getHeight());
//				}
//
//				for ( int i = 0; i < strings.length; i++) {
//
//					g.setFont( strings[i].getFont());
//
//					if ( ((XmlCellRenderer)l).isSelected()) {
//						g.setColor( SELECTION_FOREGROUND);
//					} else {
//						g.setColor( strings[i].getColor());
//					}
//
//					g.drawString( strings[i].getText(), x, y);
//					x = x + (int)g.getFontMetrics().getStringBounds( strings[i].getText(), g).getWidth();
//				}		
//
//				y = y + fm.getHeight();
//			}
//        }
//    }


    public Dimension getPreferredSize( JComponent c) {
        XmlCellRenderer renderer = (XmlCellRenderer)c;
		Graphics gc = renderer.getGraphics();
		int height = 0;
		int width = 0;
		
		if ( gc != null) {
			width = calculateWidth( renderer, gc);
			height = gc.getFontMetrics().getHeight();
		}
		
//		System.out.println( "preferredSize() ["+width+", "+height+"]");
		
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
