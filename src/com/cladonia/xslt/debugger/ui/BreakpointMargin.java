/*
 * $Id: BreakpointMargin.java,v 1.5 2004/10/13 18:33:35 edankert Exp $
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

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.text.Element;

import org.bounce.event.DoubleClickListener;

import com.cladonia.xml.editor.Tag;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.Breakpoint;

/**
 * The margin component for the editor.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/13 18:33:35 $
 * @author Dogsbay
 */
public class BreakpointMargin extends JComponent {
	private static final ImageIcon ENABLED_BREAKPOINT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointEnabledIcon.gif");
	private static final ImageIcon DISABLED_BREAKPOINT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointDisabledIcon.gif");

	private Tag startTag = null;
	private Tag endTag = null;

	// Set right/left margin
	private final static int WIDTH = 9;

	// heights and widths
	private int lineHeight	= 16;
	private int topMargin	= 3;

	// Metrics of this LineNumber component
	private FontMetrics fontMetrics = null;
	private XmlEditorPane editor = null;
	private InputPane input = null;

	private int lines = 0;

	/**
	 * Convenience constructor for Text Components
	 */
	public BreakpointMargin( InputPane _input, XmlEditorPane _editor) {
		this.editor = _editor;
		this.input = _input;
		
		setBackground( Color.white);
		setForeground( UIManager.getColor( "textText"));
		setFont( editor.getFont());
		
		Insets insets = editor.getMargin();
		
		if ( insets != null) {
			topMargin = insets.top;
		} else {
			topMargin = 0;
		}
		
		this.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent e) {
				input.setFocus();
			}
		});

		this.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				int line = input.getLineForOffset( e.getY());

				input.setBreakpoint( line);
			}
		});
	}

	public Dimension getPreferredSize() {
		return new Dimension( WIDTH, editor.getPreferredSize().height);		
	}

	public Dimension getMaximumSize() {
		return new Dimension( WIDTH, editor.getPreferredSize().height);		
	}

	public Dimension getMinimumSize() {
		return new Dimension( WIDTH, editor.getPreferredSize().height);		
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
	
	public void setFont( Font font) {
		super.setFont( font);
		
		if ( font != null) {
			fontMetrics = getFontMetrics( font);
			lineHeight = fontMetrics.getHeight();
		}
	}


	public void paintComponent( Graphics g ) {
		if ( fontMetrics != null) {
			Element root = editor.getDocument().getDefaultRootElement();
			//????
			Tag sTag = input.getCurrentStartTag();
			Tag eTag = input.getEndTag( sTag);
			
			if ( sTag != null) {
				startTag = sTag;
				endTag = eTag;
			}

			int selectionStart = -1;
			int selectionEnd = -1;

			if ( startTag != null) {
				int line = root.getElementIndex( startTag.getStart());
				try {
					selectionStart = editor.getLineStart( line);
				} catch ( Exception e) {
					e.printStackTrace();
					return;
				}
			} 
			
			if ( endTag != null) {
				int line = root.getElementIndex( endTag.getEnd());

				try {
					selectionEnd = editor.getLineStart( line) + getLineHeight();
				} catch ( Exception e) {
					e.printStackTrace();
					return;
				}
			} else if ( startTag != null) {
				int line = root.getElementIndex( startTag.getEnd());

				try {
					selectionEnd = editor.getLineStart( line) + getLineHeight();
				} catch ( Exception e) {
					e.printStackTrace();
					return;
				}
			}

			int lineHeight = getLineHeight();
			int startOffset = getStartOffset();
			Rectangle drawHere = g.getClipBounds();

			int width = getPreferredSize().width;

			// System.out.println( drawHere );
			// Paint the background
			g.setColor( getBackground());
			g.fillRect( drawHere.x, drawHere.y, drawHere.width, drawHere.height );

			if ( selectionStart != -1 && selectionEnd != -1) {
				g.setColor( UIManager.getColor("control"));
				g.fillRect( drawHere.x, selectionStart, drawHere.width-1, selectionEnd - selectionStart);
			}

			g.setColor( UIManager.getColor("controlShadow"));
			g.drawLine( width-1, drawHere.y, width-1, drawHere.y + drawHere.height);

			// Determine the number of lines to draw in the foreground.
			g.setColor( getForeground());

			int startLineNumber = 0; //( drawHere.y / lineHeight ) + 1;
			int endLineNumber = 0;
			try{
				startLineNumber = editor.getLineNumber( drawHere.y);
				if ( startLineNumber > 1) {
					startLineNumber = startLineNumber - 1;
				}
			} catch ( Exception e) {
				e.printStackTrace();
				return;
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
				return;
			}

			int line = startLineNumber;
			int previous = -1;

			// System.out.println( startLineNumber + " : " + endLineNumber + " : " + start );
			while ( line < endLineNumber) {
				try {
					Breakpoint breakpoint = input.getBreakpoint( line);
					int start = editor.getLineStart( line);
					
					if ( previous != -1 && line - previous > 1) {
						g.setColor( UIManager.getColor( "controlShadow"));
						g.drawLine( 1, start-1, WIDTH-3, start-1);
						g.setColor( getForeground());
					}

					if ( breakpoint != null && start != -1) {
						if ( breakpoint.isEnabled()) {
							ENABLED_BREAKPOINT_ICON.paintIcon( this, g, 0, start + ((lineHeight - ENABLED_BREAKPOINT_ICON.getIconHeight())/2));
						} else {
							DISABLED_BREAKPOINT_ICON.paintIcon( this, g, 0, start + ((lineHeight - DISABLED_BREAKPOINT_ICON.getIconHeight())/2));
						}
					}

					previous = line;
					line = editor.getNextLineNumber( line);
				} catch ( Exception e) {
					e.printStackTrace();
					return;
				}

//				start += lineHeight;
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
		input = null;
		editor = null;
	}
}
