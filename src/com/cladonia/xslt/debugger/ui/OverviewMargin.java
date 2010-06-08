/*
 * $Id: OverviewMargin.java,v 1.1 2004/09/30 11:29:32 edankert Exp $
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
package com.cladonia.xslt.debugger.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import com.cladonia.xslt.debugger.Breakpoint;

/**
 * The margin component for the editor.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/09/30 11:29:32 $
 * @author Dogsbay
 */
public class OverviewMargin extends JComponent {
	private static final Color ENABLED_BREAKPOINT_FILL_COLOR = new Color( 204, 102, 102);
	private static final Color ENABLED_BREAKPOINT_BORDER_COLOR = new Color( 153, 51, 51);

	private static final Color DISABLED_BREAKPOINT_FILL_COLOR = new Color( 255, 204, 51);
	private static final Color DISABLED_BREAKPOINT_BORDER_COLOR = new Color( 204, 153, 51);

	// Set right/left margin
	private final static int WIDTH = 13;

	// heights and widths
//	private int lineHeight		= 16;
//	private int topMargin		= 3;
//	private int bottomMargin	= 3;

	// Metrics of this LineNumber component
	private FontMetrics fontMetrics = null;
	private InputPane panel = null;
	private JScrollPane scroller = null;

	private static final int BOOKMARK_HEIGHT = 5;

//	private int lines = 0;

	/**
	 * Convenience constructor for Text Components
	 */
	public OverviewMargin( InputPane _panel, JScrollPane scroller) {
		this.panel = _panel;
		this.scroller = scroller;
		
		this.addMouseMotionListener( new MouseMotionListener() {
			public void mouseMoved( MouseEvent e) {
				Breakpoint bp = getBreakpoint( e.getY());

				if ( bp != null) {
					setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR));
					
					try {
						if ( bp.isEnabled()) {
							setToolTipText( "Breakpoint [line: "+bp.getLineNumber()+"]");
						} else {
							setToolTipText( "Disabled Breakpoint [line: "+bp.getLineNumber()+"]");
						}
					} catch ( Exception x) {
					}
				} else if ( isCursor( e.getY())) {
					setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR));
					setToolTipText( "Goto Cursor: ["+panel.getCursorLine()+"]");
				} else {
					setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR));
					setToolTipText( null);
				}
			}

			public void mouseDragged( MouseEvent e) {
				Breakpoint bp = getBreakpoint( e.getY());

				if ( bp != null) {
					setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR));
					
					try {
						if ( bp.isEnabled()) {
							setToolTipText( "Breakpoint ["+bp.getLineNumber()+"]");
						} else {
							setToolTipText( "Disabled Breakpoint ["+bp.getLineNumber()+"]");
						}
					} catch ( Exception x) {
					}
				} else if ( isCursor( e.getY())) {
					setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR));
					setToolTipText( "Goto Cursor: ["+panel.getCursorLine()+"]");
				} else {
					setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR));
					setToolTipText( null);
				}
			}
		});

		this.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent e) {
				panel.setFocus();

				Breakpoint bp = getBreakpoint( e.getY());

				if ( bp != null) {
					panel.selectLineWithoutEnd( bp.getLineNumber());
				} else if ( isCursor( e.getY())) {
					panel.gotoCursor();
				}
			}
		});
	}

	public Dimension getPreferredSize() {
		Dimension size = super.getPreferredSize();
		
		return new Dimension( WIDTH, size.height);		
	}

	public Dimension getMaximumSize() {
		Dimension size = super.getMaximumSize();
		
		return new Dimension( WIDTH, size.height);		
	}

	public Dimension getMinimumSize() {
		Dimension size = super.getMinimumSize();
		
		return new Dimension( WIDTH, size.height);		
	}

	/**
	 * The line height defaults to the line height of the font for this
	 * component. The line height can be overridden by setting it to a
	 * positive non-zero value.
	 */
//	public int getLineHeight() {
//		return lineHeight;
//	}

	public int getStartOffset() {
		return 4;
	}
	
//	public void setFont( Font font) {
//		super.setFont( font);
//		
//		if ( font != null) {
//			fontMetrics = getFontMetrics( font);
//			lineHeight = fontMetrics.getHeight();
//		}
//	}


	public void paint( Graphics g ) {
		int topMargin = getTopMargin();
		int bottomMargin = getBottomMargin();

		int startOffset = getStartOffset();
		Rectangle drawHere = new Rectangle( 0, 0, getSize().width, getSize().height);

		// Paint the background
		g.setColor( getBackground());
		g.fillRect( drawHere.x, drawHere.y, drawHere.width, drawHere.height);
		
		drawHere.y = drawHere.y + topMargin;
		drawHere.height = drawHere.height - (topMargin + bottomMargin);
		
		g.setColor( UIManager.getColor("controlShadow"));
		g.drawRect( drawHere.x+1, drawHere.y, drawHere.width-2, drawHere.height-1);

		// Determine the number of lines to draw in the foreground.
		int current = panel.getCursorLine();
		
		int total = panel.getLines();
		double lHeight = (double)(((double)drawHere.height -( (BOOKMARK_HEIGHT-1) + 2 + 2)) / ((double)total));

		Vector breakpoints = panel.getBreakpoints();
		for ( int i = 0; i < breakpoints.size(); i++) {
			Breakpoint bp = (Breakpoint)breakpoints.elementAt(i);
			int index = bp.getLineNumber() - 1;
			
			if ( index != -1) {
				int startY = ((int)(lHeight * index)) + topMargin + 2;
				
				if ( bp.isEnabled()) {
					g.setColor( ENABLED_BREAKPOINT_BORDER_COLOR);
					g.drawRect( 3, startY, 7, 4);
	
					g.setColor( ENABLED_BREAKPOINT_FILL_COLOR);
					g.fillRect( 4, startY+1, 6, 3);
				} else {
					g.setColor( DISABLED_BREAKPOINT_BORDER_COLOR);
					g.drawRect( 3, startY, 7, 4);
	
					g.setColor( DISABLED_BREAKPOINT_FILL_COLOR);
					g.fillRect( 4, startY+1, 6, 3);
				}
			}
		}
		
		lHeight = (double)(((double)drawHere.height -( (BOOKMARK_HEIGHT-1) + 2 + 2)) / ((double)total));
		int startY = ((int)(lHeight * current)) + topMargin + 2 + 1;
		
		g.setColor( Color.black);
		g.drawLine( 3, startY, 3, startY);
		g.drawLine( 3, startY+1, 4, startY+1);
		g.drawLine( 3, startY+2, 3, startY+2);

		g.drawLine( 10, startY, 10, startY);
		g.drawLine( 9, startY+1, 10, startY+1);
		g.drawLine( 10, startY+2, 10, startY+2);
	}
	
	private Breakpoint getBreakpoint( int y) {
		Vector breakpoints = panel.getBreakpoints();

		int topMargin = getTopMargin();
		int bottomMargin = getBottomMargin();

		int height = getSize().height - (topMargin + bottomMargin);

		for ( int i = 0; i < breakpoints.size(); i++) {
			Breakpoint bp = (Breakpoint)breakpoints.elementAt(i);
			int index = bp.getLineNumber() - 1;

			if ( index != -1) {
				int lines = panel.getLines();
	
				double lHeight = (double)(((double)height- ((BOOKMARK_HEIGHT-1) + 2 + 2)) / ((double)lines));
				int startY = ((int)(lHeight * index)) + topMargin + 2;
				
				if ( y >= startY && y <= startY + 5) {
					return bp;
				}
			}
		}
		
		return null;
	}
	
	private int getTopMargin() {
		if ( scroller.getVerticalScrollBar().isVisible()) {
			return scroller.getVerticalScrollBar().getWidth() + 1;
		} else {
			return 1;
		}
	}
	
	private int getBottomMargin() {
		if ( scroller.getHorizontalScrollBar().isVisible()) {
			return getTopMargin() + (scroller.getHorizontalScrollBar().getHeight() + 1);
		} else {
			return getTopMargin() + 1;
		}
	}

	private boolean isCursor( int y) {
		int topMargin = getTopMargin();
		int bottomMargin = getBottomMargin();

		int current = panel.getCursorLine();
		int total = panel.getLines();

		int height = getSize().height - (topMargin + bottomMargin);

		double lHeight = (double)(((double)height -( (BOOKMARK_HEIGHT-1) + 2 + 2)) / ((double)total));
//		double lHeight = (double)(((double)height-( 2 + 2 + 2 + 4)) / ((double)total));
		int startY = ((int)(lHeight * current)) + topMargin + 2 + 1;
//		int startY = ((int)(lHeight * current)) + topMargin + 2 + 2;

		if ( y >= (startY-1) && y <= startY + 1) {
			return true;
		}
		
		return false;
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
		panel = null;
//		editor = null;
	}
}
