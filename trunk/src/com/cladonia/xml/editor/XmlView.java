/*
 * $Id: XmlView.java,v 1.9 2004/10/19 14:39:44 edankert Exp $
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

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.Utilities;

import com.cladonia.xml.XMLError;
import com.cladonia.xngreditor.ErrorList;

/**
 * The XML View uses the XML scanner to determine the style (font, color) of the 
 * text that it renders.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.9 $, $Date: 2004/10/19 14:39:44 $
 * @author Dogsbay
 */
// ED Wrapping:
// public class XmlView extends WrappedPlainView {
public class XmlView extends FoldingPlainView { // WrappedPlainView {
	
	private static final Color WARNING_COLOR = new Color( 255, 204, 51);
	private static final Color ERROR_COLOR = new Color( 255, 0, 0);

	private static final Color[] HIGHLIGHT_COLORS = { 
    		new Color( 255, 255, 204),
    		new Color( 255, 204, 255),
    		new Color( 204, 255, 255),
			new Color( 204, 255, 204),
    		new Color( 204, 204, 255),
    		new Color( 255, 204, 204)
    	};

    private static final boolean DEBUG = false;
    
	private Vector prefixes				= null;
    private boolean highlight			= false;
    private boolean errorHighlighting	= false;
    private Prefix prefix				= null;

    private XmlScanner lexer;
	private boolean lexerValid;
	private XmlContext context = null;
	private ErrorList errors = null;
	
	/**
	 * Construct a colorized view of xml text for the element.
	 * Gets the current document and creates a new Scanner object.
	 *
	 * @param context the styles used to colorize the view.
	 * @param elem the element to create the view for.
	 */
	public XmlView( XmlContext context, Element elem, ErrorList errors) {
	    super( elem);
		
		this.errors = errors;
		this.context = context;
	    XmlDocument doc = (XmlDocument) getDocument();

	    try {
		    lexer = new XmlScanner( doc);
	    } catch ( Exception e) {
	        lexer = null;
	    }
			
	    lexerValid = false;
	    
	    prefixes = new Vector();
	}

	/**
	 * Invalidates the scanner, to make sure a new range is set later. 
	 *
	 * @see View#paint
	 */
    public void paint( Graphics g, Shape a) {
	    super.paint( g, a);
	    lexerValid = false;
	}
    
	/**
	 * Enable/diables the highlighter 
	 *
	 * @param enabled true the enable the highlighter.
	 */
    public void setHighlight( boolean enabled) {
    	highlight = enabled;

    	prefixes = new Vector();
    }
    
    public boolean isHighlight() {
    	return highlight;
    }

	/**
	 * Enable/diables the error highlighting
	 *
	 * @param enabled true the enable the error highlighting.
	 */
    public void setErrorHighlighting( boolean enabled) {
    	errorHighlighting = enabled;
    }
    
    public boolean isErrorHighlighting() {
    	return errorHighlighting;
    }

    /**
	 * Renders the given range in the model as normal unselected
	 * text. This will paint the text according to the styles..
	 *
	 * @param g the graphics context
	 * @param x the starting X coordinate
	 * @param y the starting Y coordinate
	 * @param start the beginning position in the model
	 * @param end the ending position in the model
	 *
	 * @returns the location of the end of the range
	 *
	 * @exception BadLocationException if the range is invalid
	 */
	protected int drawUnselectedText( Graphics g, int x, int y, int start, int end) throws BadLocationException {
	    Document doc = getDocument();
	    Style lastStyle = null;
	    int mark = start;
	    boolean lastError = false;
	    XMLError error = null;

	    if (DEBUG) System.out.println( "drawUnselectedText()");

	    while ( start < end) {
			updateScanner( start);

			int p = Math.min( lexer.getEndOffset(), end);
			p = (p <= start) ? end : p;

			Style style = context.getStyle( lexer.token);
			
			// If the style changes, do paint...
			if ( (style != lastStyle || lexer.error) && lastStyle != null) {

				// color change, flush what we have
			    Segment text = getLineBuffer();
			    doc.getText( mark, start - mark, text);

			    g.setFont( context.getFont( lastStyle));
			    
			    if ( isHighlight()) {
			    	drawHighlights( lastStyle, text, g, x, y);
			    }

			    int x1 = x;

			    g.setColor( context.getForeground( lastStyle));
			    x = Utilities.drawTabbedText( text, x, y, g, this, mark);
			    
				if ( lastError || error != null) {
					if ( error != null && error.getType() == XMLError.WARNING) {
						drawWarning( text, g, x1, x-x1, y, false);
					} else {
						drawError( text, g, x1, x-x1, y, false);
					}

					lastError = false;
					error = null;
				}

				mark = start;
			}

			lastStyle = style;
			
			if ( !lastError) {
				lastError = lexer.error;
			}
			
			if ( error == null) {
				error = getError( start, p);
			}

			start = p;
	    }

		// flush remaining
	    g.setFont( context.getFont( lastStyle));

	    Segment text = getLineBuffer();
	    doc.getText( mark, end - mark, text);

		if ( isHighlight()) {
	    	drawHighlights( lastStyle, text, g, x, y);
	    }

		g.setColor( context.getForeground( lastStyle));

//	    if (DEBUG) System.out.println( "flush: "+text.toString()+" ["+lastToken+"]");
	    int x1 = x;
	    x = Utilities.drawTabbedText( text, x, y, g, this, mark);

		if ( lastError || error != null) {
			if ( error != null && error.getType() == XMLError.WARNING) {
				drawWarning( text, g, x1, x-x1, y, false);
			} else {
				drawError( text, g, x1, x-x1, y, false);
			}

			lastError = false;
			error = null;
		}

		return x;
	}
	
	private XMLError getError( int start, int end) {
		if ( errors != null) {
			Vector es = errors.getCurrentErrors();
	
			for ( int i = 0; i < es.size(); i++) {
				XMLError error = (XMLError)es.elementAt(i);

				if ( error.getLineNumber() > 0) {
					XmlDocument doc = (XmlDocument)getDocument();
					
					if ( doc.getDefaultRootElement().getElementCount() > error.getLineNumber()) {
						Element element = doc.getDefaultRootElement().getElement( error.getLineNumber()-1);
						
						if ( element != null) {
							int errorPos = element.getStartOffset()+(error.getColumnNumber()-1);
				
							if ( errorPos >= start && errorPos <= end) {
								return error;
							}
						}
					}
				}
			}
		}

		return null;
	}
	
	private void drawWarning( Segment text, Graphics g, int x, int width, int y, boolean selected) {
		if ( !selected) {
			g.setColor( WARNING_COLOR);
		}

		drawSquiggle( text, g, x, width, y);
	}

	private void drawError( Segment text, Graphics g, int x, int width, int y, boolean selected) {
		if ( !selected) {
		    g.setColor( ERROR_COLOR);
		}
		
		drawSquiggle( text, g, x, width, y);
	}

	private void drawSquiggle( Segment text, Graphics g, int x, int width, int y) {
		if ( isErrorHighlighting()) {
	    	FontMetrics fm = g.getFontMetrics();
		    int nameStart = text.getBeginIndex();
		    int nameEnd = text.getEndIndex();
		    
		    if (DEBUG) System.out.println( "Error: \""+text.toString()+"\"");
		    
		    int pos = 0;
		    
		    int npoints = ((width / 4)*4);
		    
		    if ( npoints > 0) {
		    	npoints += 3;
	
			    int[] xpoints = new int[npoints];
			    int[] ypoints = new int[npoints];
			    
			    for ( int i = 0; i < npoints; i+=4) {
			    	xpoints[i] = x+pos;
			    	ypoints[i] = y+1;
			    	
			    	pos +=1;
			    	
			    	xpoints[i+1] = x+pos;
			    	ypoints[i+1] = y;
		
			    	pos +=1;
		
			    	xpoints[i+2] = x+pos;
			    	ypoints[i+2] = y+1;
		
			    	pos +=1;
		
			    	if ( i+4 < npoints) {
				    	xpoints[i+3] = x+pos;
				    	ypoints[i+3] = y+2;
		
				    	pos += 1;
				    }
			    }
			    
			    g.drawPolyline( xpoints, ypoints, npoints);
			}
		}
	}

	private void drawHighlights( Style style, Segment text, Graphics g, int x, int y) {
		
	    if ( style == context.getStyle( Constants.ELEMENT_PREFIX) || style == context.getStyle( Constants.ATTRIBUTE_PREFIX)) {
	    	
	    	prefix = getPrefix( text);

	    	FontMetrics fm = g.getFontMetrics();
		    int nameStart = text.getBeginIndex();
		    int nameEnd = text.getEndIndex();
		    
		    int width = fm.charsWidth( text.array, nameStart, nameEnd - nameStart);

		    g.setColor( prefix.getBackground());
		    g.fillRect( x, y-fm.getAscent() +1, width, fm.getHeight()-2);
	    } else if ( style == context.getStyle( Constants.ATTRIBUTE_NAME) && prefix != null) {
		    FontMetrics fm = g.getFontMetrics();
		    int nameStart = text.getBeginIndex();
		    int nameEnd = text.getEndIndex();
		    
//		    for ( int i = text.getEndIndex() -1; i > text.getBeginIndex(); i--) { 
//		    	if ( Character.isWhitespace( text.array[i])) {
//		    		nameEnd--;
//		    	} else {
//		    		break;
//		    	}
//		    }

		    int width = fm.charsWidth( text.array, nameStart, nameEnd - nameStart);

		    g.setColor( prefix.getBackground());
		    g.fillRect( x, y-fm.getAscent()+1, width, fm.getHeight()-2);
	    } else if ( style == context.getStyle( Constants.ATTRIBUTE_VALUE) && prefix != null) {
		    FontMetrics fm = g.getFontMetrics();
		    int nameStart = text.getBeginIndex();
		    int nameEnd = text.getEndIndex();
		    
		    for ( int i = text.getEndIndex() -1; i > text.getBeginIndex(); i--) { 
		    	if ( Character.isWhitespace( text.array[i])) {
		    		nameEnd--;
		    	} else {
		    		break;
		    	}
		    }

		    int width = fm.charsWidth( text.array, nameStart, nameEnd - nameStart);

		    g.setColor( prefix.getBackground());
		    g.fillRect( x, y-fm.getAscent()+1, width, fm.getHeight()-2);
		    prefix = null;
	    } else if ( style == context.getStyle( Constants.ELEMENT_NAME)) {
		    FontMetrics fm = g.getFontMetrics();
		    int nameStart = text.getBeginIndex();
		    int nameEnd = text.getEndIndex();
		    
		    for ( int i = text.getEndIndex() -1; i > text.getBeginIndex(); i--) { 
		    	if ( Character.isWhitespace( text.array[i])) {
		    		nameEnd--;
		    	} else {
		    		break;
		    	}
		    }

		    int width = fm.charsWidth( text.array, nameStart, nameEnd - nameStart);

		    if ( prefix != null) {
		    	g.setColor( prefix.getBackground());
		    } else {
		    	g.setColor( HIGHLIGHT_COLORS[0]);
		    }

		    g.fillRect( x, y-fm.getAscent()+1, width, fm.getHeight()-2);
		    
		    prefix = null;
	    } else if ( (style == context.getStyle( Constants.SPECIAL) && (text.array[ text.getBeginIndex()] == ':' || text.array[ text.getBeginIndex()] == '=')) && prefix != null) {
		    FontMetrics fm = g.getFontMetrics();
		    int nameStart = text.getBeginIndex();
		    int nameEnd = text.getEndIndex();
		    
		    int width = fm.charsWidth( text.array, nameStart, nameEnd - nameStart);

		    g.setColor( prefix.getBackground());
		    g.fillRect( x, y-fm.getAscent()+1, width, fm.getHeight()-2);
	    } else {
	    	prefix = null;
	    }
	}

	private void drawSelectedHighlights( Style style, Segment text, Graphics g, int x, int y) {
		
	    if ( style == context.getStyle( Constants.ELEMENT_PREFIX) || style == context.getStyle( Constants.ATTRIBUTE_PREFIX)) {
	    	prefix = getPrefix( text);
	    } else if ( style != context.getStyle( Constants.ATTRIBUTE_NAME) &&
	    			style != context.getStyle( Constants.ATTRIBUTE_VALUE) &&
	    			style != context.getStyle( Constants.ELEMENT_NAME) &&
	    			style != context.getStyle( Constants.SPECIAL)) {
	    	prefix = null;
	    } else if ( style == context.getStyle( Constants.SPECIAL) && text.array[ text.getBeginIndex()] != ':' && text.array[ text.getBeginIndex()] != '=') {
	    	prefix = null;
	    }
	}

	/**
	 * Renders the given range in the model as selected text. 
	 * This will paint the text according to the font as found in the styles..
	 *
	 * @param g the graphics context
	 * @param x the starting X coordinate
	 * @param y the starting Y coordinate
	 * @param start the beginning position in the model
	 * @param end the ending position in the model
	 *
	 * @returns the location of the end of the range
	 *
	 * @exception BadLocationException if the range is invalid
	 */
	protected int drawSelectedText( Graphics g, int x, int y, int start, int end) throws BadLocationException {
	    Document doc = getDocument();
	    Style lastStyle = null;
		int lastToken = 0;
	    int mark = start;
	    boolean lastError = false;
	    XMLError error = null;
	    
	    while ( start < end) {
			updateScanner( start);

			int p = Math.min( lexer.getEndOffset(), end);
			p = (p <= start) ? end : p;

			Style style = context.getStyle( lexer.token);

			// If the style changes, do paint...
			if ( (style != lastStyle || lexer.error) && lastStyle != null) {
			    // color change, flush what we have
			    g.setFont( context.getFont( lastStyle));

			    Segment text = getLineBuffer();
			    doc.getText( mark, start - mark, text);

			    if ( isHighlight()) {
			    	drawSelectedHighlights( lastStyle, text, g, x, y);
			    }

			    g.setColor( UIManager.getColor( "TextPane.selectionForeground"));

			    if (DEBUG) System.out.println( text.toString()+" ["+lastToken+"]");
				
			    int x1 = x;
			    x = Utilities.drawTabbedText( text, x, y, g, this, mark);

				if ( lastError || error != null) {
					if ( error != null && error.getType() == XMLError.WARNING) {
						drawWarning( text, g, x1, x-x1, y, true);
					} else {
						drawError( text, g, x1, x-x1, y, true);
					}

					lastError = false;
					error = null;
				}

			    mark = start;
			}

			lastToken = lexer.token;
			lastStyle = style;

			if ( !lastError) {
				lastError = lexer.error;
			}
			
			if ( error == null) {
				error = getError( start, p);
			}

			start = p;
	    }

	    // flush remaining
	    g.setFont( context.getFont( lastStyle));
	    Segment text = getLineBuffer();
	    doc.getText( mark, end - mark, text);

	    if ( isHighlight()) {
	    	drawSelectedHighlights( lastStyle, text, g, x, y);
	    }

	    g.setColor( UIManager.getColor( "TextPane.selectionForeground"));

	    int x1 = x;

	    if (DEBUG) System.out.println( "flush: "+text.toString()+" ["+lastToken+"]");
	    x = Utilities.drawTabbedText( text, x, y, g, this, mark);

		if ( lastError || error != null) {
			if ( error != null && error.getType() == XMLError.WARNING) {
				drawWarning( text, g, x1, x-x1, y, true);
			} else {
				drawError( text, g, x1, x-x1, y, true);
			}

			lastError = false;
			error = null;
		}

	    return x;
	}

	// Update the scanner to point to the '<' begin token.
	private void updateScanner( int p) {
	    try {
			if ( !lexerValid) {
			    XmlDocument doc = (XmlDocument) getDocument();
			    lexer.setRange( doc.getTagStart( p), doc.getLength());
			    lexerValid = true;
			}

			while ( lexer.getEndOffset() <= p) {
			    lexer.scan();
			}
	    } catch ( Throwable e) {
			// can't adjust scanner... calling logic
			// will simply render the remaining text.
			e.printStackTrace();
	    }
	}
	
	public void cleanup() {
		lexer.cleanup();

		finalize();
	}
	
	protected void finalize() {
		lexer = null;
		context = null;
	}
	
	private Prefix getPrefix( Segment segment) {
//		System.out.println( "getPrefix( "+segment+")");

		if ( prefix != null) {
			if ( prefix.getPrefix().endsWith( segment.toString())) {
				return prefix;
			} else {
				String pref1 = prefix.getPrefix();
				String pref2 = segment.toString();
				String pref = pref1.substring( 0, pref1.length() - pref2.length()) + pref2;
				
				for ( int i = 0; i < prefixes.size(); i++) {
					if ( ((Prefix)prefixes.elementAt(i)).getPrefix().equals( pref)) {
						return (Prefix)prefixes.elementAt(i);
					}
				}
				
				return new Prefix( pref, HIGHLIGHT_COLORS[ (prefixes.size() + 1) % HIGHLIGHT_COLORS.length]);
			}
		}

		for ( int i = 0; i < prefixes.size(); i++) {
			if ( ((Prefix)prefixes.elementAt(i)).equals( segment)) {
				return (Prefix)prefixes.elementAt(i);
			}
		}
		
		Prefix prefix = new Prefix( segment.toString(), HIGHLIGHT_COLORS[ (prefixes.size() + 1) % HIGHLIGHT_COLORS.length]);
		
		prefixes.addElement( prefix);
		
		return prefix;
	}
	
	public static class Prefix {
		private String prefix = null;
		private Color background = null;
		
		public Prefix( String prefix, Color color) {
			this.prefix = prefix;
			background = color;
		}
		
		public String getPrefix() {
			return prefix;
		}
		
		public boolean equals( Segment segment) {
			if ( prefix.length() >= segment.getEndIndex() - segment.getBeginIndex()) {
				for ( int i = segment.getBeginIndex(); i < segment.getEndIndex(); i++) {
					if ( prefix.charAt( i-segment.getBeginIndex()) != segment.array[i]) {
						return false;
					}
				}
			} else {
				return false;
			}

			return true;
		}
		
		public Color getBackground() {
			return background;
		}
	}
}
