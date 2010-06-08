/*
 * $Id: Margin.java,v 1.13 2004/10/11 15:58:04 edankert Exp $
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
 * The Original Code is eXchaNGeR browser code. (org.xngr.browser.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.bounce.event.DoubleClickListener;

/**
 * The margin component for the editor.
 *
 * @version	$Revision: 1.13 $, $Date: 2004/10/11 15:58:04 $
 * @author Dogsbay
 */
public class Margin extends JComponent {
	// Set right/left margin
	private final static int LEFT_MARGIN = 2;
	private final static int RIGHT_MARGIN = 3;

	// heights and widths
	private int lineHeight	= 16;
	private int topMargin	= 3;
//	private int width		= 50;

	// Metrics of this LineNumber component
	private FontMetrics fontMetrics = null;
	private XmlEditorPane editor = null;
	private EditorPanel parent = null;

	private int lines = 0;

	public Margin( XmlEditorPane editor) {
		this( null, editor);
	}

	/**
	 * Convenience constructor for Text Components
	 */
	public Margin( EditorPanel _parent, XmlEditorPane editor) {
		this.editor = editor;
		this.parent = _parent;
		
		setBackground( UIManager.getColor( "control"));
		setForeground( UIManager.getColor( "textText"));
		setFont( editor.getFont());
		
		Insets insets = editor.getMargin();
		
		if ( insets != null) {
			topMargin = insets.top;
		} else {
			topMargin = 0;
		}
		
		editor.addCaretListener( new CaretListener() {
			public void caretUpdate( CaretEvent evt) {
				if ( Margin.this.editor.getLines() != lines) {
					revalidate();
					repaint();
					
					lines = Margin.this.editor.getLines();
				}
			}
		});
		
		this.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				Margin.this.editor.selectLineForOffset( e.getY());
			}
		});
		
	
		if ( parent != null) {
			this.addMouseListener( new MouseAdapter() {
				public void mouseClicked( MouseEvent e) {
					parent.setFocus();
				}
			});
		}

	//		setBorder( new EmptyBorder( insets.top, insets.left, insets.bottom, insets.right));

//		setPreferredSize( 9999);
	}

	public Dimension getPreferredSize() {
		if ( isVisible()) {
			Dimension size = super.getPreferredSize();
			
			return new Dimension( LEFT_MARGIN+getMarginwidth()+RIGHT_MARGIN, editor.getPreferredSize().height);		
		} else {
			return null;
		}
	}

	public Dimension getMaximumSize() {
		if ( isVisible()) {
			Dimension size = super.getMaximumSize();
			
			return new Dimension( LEFT_MARGIN+getMarginwidth()+RIGHT_MARGIN, editor.getPreferredSize().height);		
		} else {
			return null;
		}
	}
	
	private int getMarginwidth() {
		int lines = editor.getLines();
		int width = 0;
		
		if ( fontMetrics != null) {
			if ( lines >= 1000000) {
				width = fontMetrics.stringWidth( "9999999");
			} else if ( lines >= 100000) {
				width = fontMetrics.stringWidth( "999999");
			} else if ( lines >= 10000) {
				width = fontMetrics.stringWidth( "99999");
			} else if ( lines >= 1000) {
				width = fontMetrics.stringWidth( "9999");
			} else { // if ( lines >= 100) {
				width = fontMetrics.stringWidth( "999");
	//		} else {
	//			width = fontMetrics.stringWidth( "99");
			}
		}
		
		return width;
	}

	public Dimension getMinimumSize() {
		if ( isVisible()) {
			Dimension size = super.getMinimumSize();
			
			return new Dimension( LEFT_MARGIN+getMarginwidth()+RIGHT_MARGIN, editor.getPreferredSize().height);		
		} else {
			return null;
		}
	}

	public void setFont( Font font) {
		super.setFont( font);
		
		if ( font != null) {
			fontMetrics = getFontMetrics( font);
			lineHeight = fontMetrics.getHeight();
		}
	}

	/**
	 * The line height defaults to the line height of the font for this
	 * component. The line height can be overridden by setting it to a
	 * positive non-zero value.
	 */
	public int getLineHeight() {
		return lineHeight;
	}

	public int getStartOffset() {
		return 4;
	}
	
	public void paintComponent( Graphics g ) {
		if ( fontMetrics != null) {
			int lineHeight = getLineHeight();
			int startOffset = getStartOffset();
			Rectangle drawHere = g.getClipBounds();
			
			int width = getPreferredSize().width;

			// System.out.println( drawHere );
			// Paint the background
			g.setColor( getBackground());
			g.fillRect( drawHere.x, drawHere.y, drawHere.width, drawHere.height );

//			g.setColor( editor.getBackground());
//			g.drawLine( width-2, drawHere.y, width-2, drawHere.y + drawHere.height);
			
			g.setColor( UIManager.getColor( "controlShadow"));
			g.drawLine( width-1, drawHere.y, width-1, drawHere.y + drawHere.height);

			// Determine the number of lines to draw in the foreground.
			g.setColor( getForeground());

			int startLineNumber = 0; //( drawHere.y / lineHeight ) + 1;
			int endLineNumber = startLineNumber + ( drawHere.height / lineHeight);
			int start = (( drawHere.y / lineHeight ) * lineHeight + lineHeight - startOffset) + 3;
			
			try{
				startLineNumber = editor.getLineNumber( drawHere.y);

				if ( startLineNumber > 1) {
					startLineNumber = startLineNumber - 1;
				}
			} catch ( Exception e) {
				e.printStackTrace();
			}

			try{
				int lines = editor.getLines();
				endLineNumber = editor.getLineNumber( drawHere.y + drawHere.height);

				if ( endLineNumber <= lines - 1) {
					endLineNumber = endLineNumber+1;
				} else {
					endLineNumber = lines;
				}
			} catch ( Exception e) {
				e.printStackTrace();
			}

			int line = startLineNumber;
			int previous = -1;

			while ( line < endLineNumber) {
				String lineNumber = String.valueOf( line+1);
				
				try {
					start = editor.getLineStart( line);
					
					if ( start != -1) { 
						if ( previous != -1 && line - previous > 1) {
							g.setColor( UIManager.getColor( "controlShadow"));
							g.drawLine( 1, start-1, getMarginwidth()+2, start-1);
							g.setColor( getForeground());
						}

						int stringWidth = fontMetrics.stringWidth( lineNumber);
						g.drawString( lineNumber, LEFT_MARGIN+(getMarginwidth() - stringWidth), start + (lineHeight - startOffset));
					}

					previous = line;
					line = editor.getNextLineNumber( line);
				} catch ( Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}
	
	protected void removeAllListeners() {
		MouseListener[] mouseListeners = getMouseListeners();

		for ( int i = 0; i >= mouseListeners.length; i++) {
			removeMouseListener( mouseListeners[i]);
		}

		// Guaranteed to return a non-null array
		Object[] list = listenerList.getListenerList();
		
		for ( int i = list.length-2; i >= 0; i -= 2) {
			listenerList.remove( (Class)list[i], (EventListener)list[i+1]);
		}
	}

	public void cleanup() {
		removeAllListeners();
		finalize();
	}
	
	protected void finalize() {
		fontMetrics = null;
		editor = null;
	}
}
