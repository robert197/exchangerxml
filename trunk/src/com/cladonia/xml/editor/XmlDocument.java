/*
 * $Id: XmlDocument.java,v 1.25 2005/08/26 11:02:11 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.util.EventListener;
import java.util.Vector;

import javax.swing.event.DocumentEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Segment;

import org.apache.xerces.util.XMLChar;

import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The XML Document is responsible for handling the user insertions and 
 * deletions, for changing the tab characters to spaces and to automatically 
 * indent the text correctly.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.25 $, $Date: 2005/08/26 11:02:11 $
 * @author Dogsbay
 */
public class XmlDocument extends PlainDocument {
	private static final boolean DEBUG = false;
	
	private XmlEditorPane editor = null;
	
	private Vector updates = null;
	
	private boolean loading = false;
//	private boolean locked = true;
	private BufferContent content = null;

	/**
	 * Constructs the XML Document with a new GapContent buffer.
	 */
    public XmlDocument( XmlEditorPane editor, BufferContent content) {
		super( content);

		this.content = content;
		this.editor = editor;

		putProperty( XmlDocument.tabSizeAttribute, new Integer( editor.getTabSize()));
		
		this.updates = new Vector();
    }
	
//    protected Element createLeafElement(Element parent, AttributeSet a, int p0, int p1) {
//    	return new XmlLeafElement(parent, a, p0, p1);
//    }
//
//    protected Element createBranchElement(Element parent, AttributeSet a) {
//       	return new XmlBranchElement(parent, a);
//    }


    /**
	 * Lets the document know that it is being loaded, don't attempt to 
	 * do any text conversion.
	 */
	public void setLoading( boolean loading) {
		this.loading = loading;
	}
	
	public boolean isLoading() {
		return(loading);
	}
	
	public void resetUpdates() {
		if (DEBUG) System.out.println( "xmlDocument.resetUpdates()");

		this.updates = new Vector();
	}

    protected void fireInsertUpdate( DocumentEvent event) {
		if (DEBUG) System.out.println( "xmlDocument.fireInsertUpdate( "+event+")");

		updates.addElement( event);
		super.fireInsertUpdate( event);
    }

    protected void fireRemoveUpdate( DocumentEvent event) {
	    if (DEBUG) System.out.println( "xmlDocument.fireRemoveUpdate( "+event+")");

		updates.addElement( event);
	    super.fireRemoveUpdate( event);
    }
	
	public int calculateNewPosition( int pos) {
		for ( int i = 0; i < updates.size(); i++) {
			DocumentEvent event = (DocumentEvent)updates.elementAt( i);
			
			if ( event.getType().equals( DocumentEvent.EventType.INSERT)) {
				if ( pos > event.getOffset()) {
					pos = pos+event.getLength();
				}
			} else if ( event.getType().equals( DocumentEvent.EventType.REMOVE)) {
				if ( pos > event.getOffset()) {
					if ( pos > event.getOffset() + event.getLength()) {
						pos = pos-event.getLength();
					} else { // the previous position does no longer exist
						return -1;
					}
				}
			}
		}
		
		return pos;
	}

    public int calculateOldPosition( int pos) {
    	for ( int i = updates.size() - 1; i >= 0; i--) {
    		DocumentEvent event = (DocumentEvent)updates.elementAt( i);
    		
    		if ( event.getType().equals( DocumentEvent.EventType.INSERT)) {
    			if ( pos > event.getOffset()) {
    				pos = pos-event.getLength();
    			}
    		} else if ( event.getType().equals( DocumentEvent.EventType.REMOVE)) {
    			if ( pos > event.getOffset()) {
					// the current position always exists
   					pos = pos+event.getLength();
	  			}
    		}
    	}
    	
    	return pos;
    }
    
    private XMLSegment cachedSegment = null;
    
    private synchronized XMLSegment getCachedSegment( int pos) {
    	if ( cachedSegment == null) {
    		cachedSegment = new XMLSegment();
    	}
    	
    	if ( pos > 0) {
	    	pos = pos - 1;
	    }
    	
    	if ( cachedSegment.getIndex() != pos) {
			try {
				
				cachedSegment.setCurrentIndex( pos);
				cachedSegment.parse();
			} catch ( Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return cachedSegment;
    }

    private XMLSegment foldingSegment = null;
    
    private XMLSegment getCachedFoldingSegment( int pos) {
    	if ( foldingSegment == null) {
    		foldingSegment = new XMLSegment();
    	}
    	
    	if ( pos > 0) {
	    	pos = pos - 1;
	    }
    	
    	if ( foldingSegment.getIndex() != pos) {
			try {
				
				foldingSegment.setCurrentIndex( pos);
//				cachedSegment.parse();
			} catch ( Exception e) {
				e.printStackTrace();
			}
    	}
    	
    	return foldingSegment;
    }

    private XMLSegment getNewSegment( int pos) {
    	XMLSegment segment = new XMLSegment();
    	
    	if ( pos > 0) {
	    	pos = pos - 1;
	    }

		try {
			segment.setCurrentIndex( pos);
			segment.parse();
		} catch ( Exception e) {
			e.printStackTrace();
		}
    	
    	return segment;
    }

    /**
     * Gets the location where the last significant tag startedd, this
	 * location can be used as a start for the scanner. 
	 * If the current element is a comment, the last significant tag 
	 * is before the begin of the comment. 
	 *
	 * @param p the preferred start position.
	 *
	 * @return the position where the last significant tag started or 0. 
     */
    public int getTagStart( int p) {
		int tagStart = 0;

		if ( p >= 0) {
			int index = 0;
			
			XMLSegment segment = getCachedSegment( p);
			
			if ( segment.endsWithComment()) {
				index = segment.getLastCommentStartIndex();
			} else if ( segment.endsWithCDATA()) {
				index = segment.getLastCDATAStartIndex();
			} else {
				index = segment.lastIndexOf( '<');
			}

		    if ( index != -1) {
				// go two tags back, to be be sure...
		    	tagStart = segment.lastIndexOf( '<', Math.max( 0, index-1));
	    	}
		}

	    if ( tagStart == -1) {
	    	tagStart = 0;
	    }

	    return tagStart;
    }
    
    public boolean isMultipleLineTagStart( int lineNumber) {
    	Element line = getDefaultRootElement().getElement( lineNumber);
    	int start = line.getStartOffset();
    	int end = line.getEndOffset();
		XMLSegment segment = getCachedFoldingSegment( line.getStartOffset());
    	
		if ( true) { //!segment.endsWithComment() && !segment.endsWithCDATA()) {
	    	int startTagIndex = -1;
	    	int index = -1;

	    	// get start of tag
	    	for ( int i = start; i < end; i++) {
				char ch = content.getChar(i);

				if ( ch == '<') {
					if ( !XMLChar.isNameStart( content.getChar(i+1))) {
						// end tag
						return false;
					}
					
					startTagIndex = i;
					break;
//				} else if ( !XMLChar.isName( ch)) {
//					// no tag
//					return false;
				} else if ( !Character.isWhitespace( ch)) {
					// no tag
					return false;
				}
			}
	    	
	    	if ( startTagIndex == -1) {
	    		// only whitespace
	    		return false;
	    	}
	    	
	    	index = startTagIndex;
	    	
	    	// is empty tag???
//	    	for ( int i = index+1; i < end + 1; i++) { // Why (end + 1) ???
	    	for ( int i = index+1; i < end; i++) {
				char ch = content.getChar(i);

				if ( ch == '>') {
					if ( content.getChar(i-1) == '/') {
						// empty tag
						return false;
					}
					index = i;
					break;
				}
			}
	    	
	    	// has end tag???
	    	for ( int i = index+1; i < end; i++) {
				char ch = content.getChar(i);

				if ( ch == '<' && content.getChar( i+1) == '/') {
					int counter = startTagIndex + 1;
					char ch2 = content.getChar( counter);
					char ch3 = content.getChar( i+1+(counter-startTagIndex));

					while ( XMLChar.isName( ch2) && ch2 == ch3) {
						counter++;

						if ( i+1+(counter-startTagIndex) < end) {
							ch3 = content.getChar( i+1+(counter-startTagIndex));
						} else {
							return false;
						}

						ch2 = content.getChar( counter);
					}
					
					if ( Character.isWhitespace( ch2) || ch2 == '>') {
						if ( Character.isWhitespace( ch3) || ch3 == '>') {
							return false;
						}
					}
				}
			}
		}

		return true;
    }

	public char getLastCharacter( int p) {
		if ( p > 0) {
			int index = 0;
			
			XMLSegment segment = getCachedSegment( p);
			return segment.charAt( p-1);
		}

		return (char)-1;
	}

	/**
	 * Returns the attribute name, before the position, if this is an attribute.
	 *
	 * @param p the current position.
	 *
	 * @return the attribute name when there is an attribute at the cursor position. 
	 */
	public String getAttributeName( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			
			if ( !segment.endsWithComment() && !segment.endsWithCDATA()) {
				int elementStart = segment.lastIndexOf( '<');
				int elementEnd = segment.lastIndexOf( '>');
				int whitespace = segment.lastIndexOfWhitespace();

				if ( elementStart >= 0 && elementStart > elementEnd && whitespace > elementStart) {
					// in element, try to find out wether the user is editing an attribute name
					int endQuote = segment.lastIndexOf( '"', elementStart, p);
					int startQuote = segment.lastIndexOf( '"', elementStart, endQuote-1);
					int equalsStart = segment.lastIndexOf( '=', elementStart, p);
					
					if ( startQuote < endQuote && equalsStart < startQuote && whitespace > endQuote) { // could be in attribute value...
						String attribute = segment.getString( whitespace+1, p-1);
						return attribute;
					} else if ( endQuote == -1 && whitespace > -1) {
						String attribute = segment.getString( whitespace+1, p-1);
						return attribute;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns the attribute value, before the position, if this is an attribute.
	 *
	 * @param p the current position.
	 *
	 * @return the attribute value when there is an attribute at the cursor position. 
	 */
	public String getAttributeValue( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			
			if ( !segment.endsWithComment() && !segment.endsWithCDATA()) {
				int elementStart = segment.lastIndexOf( '<');
				int elementEnd = segment.lastIndexOf( '>');
				int whitespace = segment.lastIndexOfWhitespace();

				if ( elementStart >= 0 && elementStart > elementEnd && whitespace > elementStart) {
					// in element, try to find out wether the user is editing an attribute name
					int endQuote = segment.lastIndexOf( '"', elementStart, p-1);
					int startQuote = segment.lastIndexOf( '"', elementStart, endQuote-1);
					int equalsStart = segment.lastIndexOf( '=', elementStart, p-1);

					if ( startQuote < equalsStart && endQuote > equalsStart) { // could be in attribute value...
						String value = segment.getString( endQuote+1, p-1);
						return value;
					}
				}
			}
		}

		return null;
	}

	/**
	 * Returns the string before the position.
	 *
	 * @param p the current position.
	 *
	 * @return the current string at the cursor position. 
	 */
	public String getString( int p) {
		if ( p >= 0) {
			int index = 0;
			
			XMLSegment segment = getCachedSegment( p);
			
			int elementStart = segment.lastIndexOf( '<');
			int whitespace = segment.lastIndexOfWhitespace();
			
			if (elementStart > whitespace) {
				whitespace = elementStart;
			}
			
			if ( whitespace+1 < p) {
				return segment.getString( whitespace+1, p-1);
			}
		}

		return null;
	}

	public String getParentElementName( int p) {
		XMLSegment segment = getCachedSegment( p);
		
		if ( p >= 0 && !segment.endsWithComment() && !segment.endsWithCDATA()) {
			Tag tag = segment.getParentStartTag();

			if ( tag != null) {
				return tag.getQualifiedName();
			}
		}

		return null;
	}

	public Tag getEndTag( Tag startTag) {
//		System.out.println( "getEndTag( "+startTag+")");
		if ( startTag.getType() == Tag.START_TAG) {
			XMLSegment segment = getCachedSegment( startTag.getEnd());
			return segment.getParentEndTag();
		}

		return null;
	}

	public Tag getStartTag( Tag endTag) {
		if ( endTag.getType() == Tag.END_TAG) {
			XMLSegment segment = getCachedSegment( endTag.getStart() - 1);
			return segment.getParentStartTag();
		}

		return null;
	}

	public Tag getParentStartTag( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			return segment.getParentStartTag();
		}

		return null;
	}

	public Tag getDoctypeDeclarationTag() {
		int length = getLength();
		
		if ( length > 250000) {
			length = 250000;
		}
		
		XMLSegment segment = getCachedSegment( length);
		return segment.getDocumentTypeTag();
	}

	/**
	 * Returns the element name at the current offset, 
	 * null if there was no element name at the caret position.
	 *
	 * @param p the current position.
	 *
	 * @return element name at the caret position. 
	 */
	public String getElementName( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);

			if ( !segment.endsWithComment() && !segment.endsWithCDATA()) {
				int elementStart = segment.lastIndexOf( '<');
				int elementEnd = segment.lastIndexOf( '>');
				int whitespace = segment.lastIndexOfWhitespace();

				if ( elementStart >= 0 && elementStart > elementEnd && elementStart > whitespace) {
					return segment.getString( elementStart+1, Math.max( 0, p-1));
				}
			}
		}

		return null;
	}

	/**
	 * Returns the entity name at the current offset, 
	 * null if there was no element name at the caret position.
	 *
	 * @param p the current position.
	 *
	 * @return element name at the caret position. 
	 */
	public String getEntityName( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			
			if ( !segment.endsWithComment() && !segment.endsWithCDATA()) {
				int entityStart = segment.lastIndexOf( '&');
				int entityEnd = segment.indexOf( ';', entityStart+1);
				int whitespace = segment.lastIndexOfWhitespace();

				if ( entityEnd == -1 && entityStart != -1 && whitespace < entityStart) {
					return segment.getString( entityStart, p-1);
				}

			}
		}

		return null;
	}

	public void remove( int off, int len) throws BadLocationException {
		if ( editor != null && !(editor.getLocked() == XmlEditorPane.NOT_LOCKED)){
			Tag tag = getCurrentTag( off);
			Tag tag2 = getCurrentTag( off+len);
	
			if ( !isElementTag( tag) && !isElementTag( tag2)) {
				String str = getText( off, len);

				super.remove( off, len);
			// same tag
			} else if ( editor.getLocked() != XmlEditorPane.DOUBLE_LOCKED && isElementTag( tag) && isElementTag( tag2) && tag.equals( tag2)) {
				if ( tag.inAttributeValue( off) && tag.inAttributeValue( off+len)) {
					super.remove( off, len);
				}
			}
		} else { 
			super.remove( off, len);
		}
	}
	
	private boolean isElementTag( Tag tag) {
		if ( tag != null) {
			if ( tag.getType() == Tag.START_TAG || tag.getType() == Tag.END_TAG) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Inserts some content into the document.
	 * When the content is a tab character, the character will be replaced 
	 * by spaces. When the content is a new line character, an indentation will
	 * be added to the content.
	 */ 
	public void insertString( int off, String str, AttributeSet set) throws BadLocationException {
//		System.out.println("[1]insertString( "+str+")");
		if ( !loading && str.equals( ">") && !isComment( off) && !isCDATA( off)) {
			if ( editor.isTagCompletion()) {
				int caretPosition = editor.getCaretPosition();
				StringBuffer endTag = new StringBuffer( str);

				String text = getText( 0, off);
				int startTag = text.lastIndexOf( '<', off);
				int entityStartTag = text.lastIndexOf( "<!", off);
				int prefEndTag = text.lastIndexOf( '>', off);
				
				// If there was a start tag and if the start tag is not empty and 
				// if the start-tag has not got an end-tag already.
				if ( (startTag >= 0) && (startTag > prefEndTag) && (startTag < text.length()-1) ) {
					String tag = text.substring( startTag, text.length());
					char first = tag.charAt( 1);
					
					int lastQuoteIndex = Math.max( tag.lastIndexOf( '"'), tag.lastIndexOf( '\''));
					int secondLastQuoteIndex = Math.max( tag.lastIndexOf( '"', lastQuoteIndex-1), tag.lastIndexOf( '\'', lastQuoteIndex-1));;
					int lastEqualsIndex = tag.lastIndexOf( '=');
					
					if ( lastQuoteIndex == -1 || (lastQuoteIndex > secondLastQuoteIndex && secondLastQuoteIndex > lastEqualsIndex)) {
						if ( first != '/' && first != '!' && first != '?' && !Character.isWhitespace( first)) {
							boolean finished = false;
							char previous = tag.charAt( tag.length() - 1);

							if ( previous != '/' && previous != '-') {

								endTag.append( "</");
								
								for ( int i = 1; (i < tag.length()) && !finished; i++) {
									char ch = tag.charAt( i);
									
									if ( !Character.isWhitespace( ch)) {
										endTag.append( ch);
									} else {
										finished = true;
									}
								}

								endTag.append( ">");
							}
						}
					}
				}
				
				str = endTag.toString();
			
				super.insertString( off, str, set);

				editor.setCaretPosition( caretPosition+1);
			} else {
				super.insertString( off, str, set);
			}
		} else if ( !loading && str.equals( "\n")) {
			Tag tag = getCurrentTag( off);
			boolean locked = editor.getLocked() != XmlEditorPane.NOT_LOCKED;
			if ( (locked && !isElementTag( tag)) || (editor.getLocked() != XmlEditorPane.DOUBLE_LOCKED && isElementTag( tag) && tag.inAttributeValue( off)) || !locked) {

				if ( editor.isSmartIndent()) {
					StringBuffer newStr = new StringBuffer( str);
					Element elem = getDefaultRootElement().getElement( getDefaultRootElement().getElementIndex( off));
					int start = elem.getStartOffset();
					int end = elem.getEndOffset();
				    String line = getText( start, off - start);
					
					for ( int i = 0; (i < line.length()); i++) {
						char ch = line.charAt( i);
						
						if ( ((ch != '\n') && (ch != '\f') && (ch != '\r')) && Character.isWhitespace( ch)) {
							newStr.append( ch);
						} else {
							break;
						}
					}
					
					String endText = getText( off, end - off);
					
					if ( isInDeclaration( line)) {
	//					System.out.println( "In Declaration");
						newStr.append( TextPreferences.getTabString());
					} else if ( isStartElement( line)) {
	//					System.out.println( "Start Element on previous line");
						if ( !isEndElement( endText)) {
	//						System.out.println( "!End Element");
							newStr.append( TextPreferences.getTabString());
						}
					} else if ( isEndElement( endText)) {
	//					System.out.println( "End Element on previous line");
						Tag startTag = getParentStartTag( off);
						
						if ( startTag != null) {
							int pos = startTag.getStart();
							Element startElem = getDefaultRootElement().getElement( getDefaultRootElement().getElementIndex( pos));
							int lineStart = startElem.getStartOffset();
							String startTagLine = getText( lineStart, Math.max( pos - lineStart, 0));
							
							newStr = new StringBuffer( str);
	
							for ( int i = 0; (i < startTagLine.length()); i++) {
								char ch = startTagLine.charAt( i);
								
								if ( ((ch != '\n') && (ch != '\f') && (ch != '\r')) && Character.isWhitespace( ch)) {
									newStr.append( ch);
								} else {
									break;
								}
							}
						}
					} else {
	//					System.out.println( "else...");
						Tag previous = getPreviousTag( off);
						
						if ( previous != null && !isInTag( getText( 0, off)) && previous.getType() == Tag.DECLARATION_TAG) {
	//						System.out.println( "Previous tag is a declaration tag...");
							int pos = previous.getStart();
							Element startElem = getDefaultRootElement().getElement( getDefaultRootElement().getElementIndex( pos));
							int lineStart = startElem.getStartOffset();
							String startTagLine = getText( lineStart, pos - lineStart);
							
							newStr = new StringBuffer( str);
	
							for ( int i = 0; (i < startTagLine.length()); i++) {
								char ch = startTagLine.charAt( i);
								
								if ( ((ch != '\n') && (ch != '\f') && (ch != '\r')) && Character.isWhitespace( ch)) {
									newStr.append( ch);
								} else {
									break;
								}
							}
						}
					}
							
					str = newStr.toString();
				}
				
				super.insertString( off, str, set);
			}
		} else if ( (editor.getLocked() != XmlEditorPane.NOT_LOCKED) && !loading){
			Tag tag = getCurrentTag( off);

			if ( !isElementTag( tag)) {
				str = substituteCDATACharacters( str);
				super.insertString( off, str, set);
			} else if ( editor.getLocked() != XmlEditorPane.DOUBLE_LOCKED && isElementTag( tag) && tag.inAttributeValue( off)) {
				str = substituteCDATACharacters( str);
				super.insertString( off, str, set);
			}
		} else {
			super.insertString( off, str, set);
		}
	}

	private String substituteCDATACharacters( String text) {
		if ( text != null) {
			StringBuffer newText =  new StringBuffer();
			
			for ( int i = 0; i < text.length(); i++) {
				char character = text.charAt( i);
				
				if ( (character == '<')) {
					newText.append( "&lt;");
				} else if ( character == '"') {
					newText.append( "&quot;");
				} else if ( character == '\'') {
					newText.append( "&apos;");
				} else if ( character == '>') {
					newText.append( "&gt;");
				} else {
					newText.append( (char)character);
				}
			}
			
			return newText.toString();
		}

		return null;
	}

	// Tries to find out if the line finishes with an element start
	private boolean isStartElement( String line) {
		boolean result = false;
		
		int first = line.lastIndexOf( '<');
		int firstDecl = line.lastIndexOf( "<!");
		int last = line.lastIndexOf( '>');
		
		if ( first == firstDecl) {
			return false;
		}
		
		if ( last < first) { // In the Tag
			result = true;
		} else {
			int firstEnd = line.lastIndexOf( "</");
			int lastEnd = line.lastIndexOf( "/>");

			// Last Tag is not an End Tag
			if ( (firstEnd != first) && ((lastEnd + 1) != last)) {
				result = true;
			}
		}
		
//		System.out.println("XmlDocument.isStartElement( "+line+") ["+result+"]");

		return result;
	}
	
	// Tries to find out if the line finishes with an element start
	private boolean isInDeclaration( String line) {
		boolean result = false;
		
		int first = line.lastIndexOf( "<!");
		int last = line.lastIndexOf( '>');
		
		if ( last < first) { // In the Tag
			result = true;
		} 

		return result;
	}

	private boolean isInTag( String line) {
		boolean result = false;
		
		int first = line.lastIndexOf( '<');
		int last = line.lastIndexOf( '>');
		
		if ( last < first) { // In the Tag
			result = true;
		} 

		return result;
	}

	private boolean isEndElement( String line) {
		boolean result = (line.trim()).startsWith( "</");
//		System.out.println("XmlDocument.isEndElement( "+line+") ["+result+"]");
		
		return result;
	}

	private int lastIndexOfWhitespace( String string) {
		for ( int i = string.length(); i > 0; i--) {
			char ch = string.charAt( i-1);
	
			if ( Character.isWhitespace( ch)) {
				return i-1;
			}
		}
		
		return -1;
	}
	
	public boolean isCDATA( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			return segment.endsWithCDATA();
		}

		return false;
	}
	
	public boolean isComment( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			return segment.endsWithComment();
		}

		return false;
	}

	/**
	 * Returns the current tag, either a start/end element tag (have to find 
	 * matching start and end element tags) or it is an empty element tag, 
	 * comment or cdata tag.
	 *
	 * @param p the current position.
	 *
	 * @return the current tag.
	 */
	public Tag getCurrentTag( int p) {
		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);
			return segment.getCurrentTag();
		}

		return null;
	}

	public Tag getNextTag( int p) {
		Tag tag = null;

		if ( p >= 0) {
			XMLSegment segment = getCachedSegment( p);

			int start = 0;
			int end = segment.indexOf( '<', p, content.length() - 1);
			
			if ( end == -1) {
				return null;
			}

			segment.setCurrentIndex( end + 1);
			segment.parse();
			
			tag = segment.getCurrentTag();
		}

		return tag;
	}

	// TODO include this in Segment???
	public Tag getPreviousTag( int p) {
		Tag tag = null;

		if ( p >= 0) {
			XMLSegment segment = getNewSegment( p);

			int start = 0;
			int end = segment.lastIndexOf( '>');
			
			if ( end == -1) {
				return null;
			}

			segment.setCurrentIndex( end - 1);
			segment.parse();
			
			tag = segment.getCurrentTag();
		}

		return tag;
	}
	
	protected void removeAllListeners() {
		// Guaranteed to return a non-null array
		Object[] list = listenerList.getListenerList();
		
		for ( int i = list.length-2; i >= 0; i -= 2) {
			listenerList.remove( (Class)list[i], (EventListener)list[i+1]);
		}
	}

	public void cleanup() {
		if ( editor != null) {
			removeAllListeners();
			
			try {
			    remove(0, getLength());
			} catch (BadLocationException e) {
			}
	
			BufferContent content = (BufferContent)getContent();
			content.cleanup();
	
			finalize();
		}
	}
	
	protected void finalize() {
		editor = null;
	}
	
	public class XMLSegment extends Segment {
		private int index = -1;
		private int cdataEndIndex = -1;
		private int cdataStartIndex = -1;
		private int commentEndIndex = -1;
		private int commentStartIndex = -1;
	
		public void setCurrentIndex( int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

		public int getLastCDATAEndIndex() {
			return cdataEndIndex;
		}

		public int getLastCDATAStartIndex() {
			return cdataStartIndex;
		}

		public int getLastCommentStartIndex() {
			return commentStartIndex;
		}
		
		public int getLastCommentEndIndex() {
			return commentEndIndex;
		}
		
		public boolean endsWithComment() {
			if ( commentStartIndex != -1 && commentEndIndex < commentStartIndex) {
				// could still end with a cdata section.
				if ( cdataStartIndex != -1 && cdataEndIndex < cdataStartIndex) {
					// The comment is inside a cdata section.
					if ( cdataStartIndex < commentStartIndex) {
						return false;
					}
				}
				
				return true;
			}

			return false;
		}
		
		public boolean endsWithCDATA() {
			if ( cdataStartIndex != -1 && cdataEndIndex < cdataStartIndex) {
				// could still end with a comment section.
				if ( commentStartIndex != -1 && commentEndIndex < commentStartIndex) {
					// The cdata section is inside a comment section.
					if ( commentStartIndex < cdataStartIndex) {
						return false;
					}
				}
				
				return true;
			}

			return false;
		}

		public void parse() {
			previousEnd = -1;
			
			parse( getIndex());
		}

		public void parse( int end) {
			int begin = 0;

			cdataEndIndex = -1;
			cdataStartIndex = -1;
			commentEndIndex = -1;
			commentStartIndex = -1;
						
			if ( content.length() > 250000 && end > 250000) {
				begin = end - 250000;
			}
		
			for ( int i = end; i >= begin; i--) {
				int ch = content.getChar(i);

				// Search for '!' since this normally has less occurrences than '<'
				if ( (i + 3) < content.length() && '<' == ch) {
					if ( content.getChar( i+1) == '!') {
						// <!--
						if ( (commentStartIndex == -1) && content.getChar( i+2) == '-' && content.getChar( i+3) == '-') {
							commentStartIndex = i;
						}
						
						// <![CDATA[
						if ( (cdataStartIndex == -1) && ((i + 8) < content.length()) && content.getChar( i+2) == '[' && content.getChar( i+3) == 'C' && content.getChar( i+4) == 'D' && content.getChar( i+5) == 'A' && content.getChar( i+6) == 'T' && content.getChar( i+7) == 'A' && content.getChar( i+8) == '[') {
							cdataStartIndex = i;
						}
					}

				// Search for ']' since this normally has less occurrences than '<'
				} else if ( cdataEndIndex == -1 && ch == ']') {
					// ]]>
					if ( i > 0  && ((i + 2) <= (content.length() -1)) && content.getChar(i+1) == ']' && content.getChar(i+2) == '>') {
						cdataEndIndex = i+2;
					}

				// Search for '-' since this normally has less occurrences than '<'
				} else if ( commentEndIndex == -1 && ch == '-') {
					// -->
					if ( i > 0  && ((i + 2) <= (content.length() -1)) && content.getChar(i+1) == '-' && content.getChar(i+2) == '>') {
						commentEndIndex = i+2;
					}
				} else if ( ch == '>' || ch == '-' || ch == '[') {
					// can skip the next character...
					i--;
				} else if ( ch != '!') {
					// skip the next 2 characters...
					i = i - 2;
				}
				
				if ( cdataStartIndex != -1 && commentStartIndex != -1) {
					return;
				}
			}
		}
		
		int previousEnd = -1;
		
		int parsableCDATAEndIndex = -1;
		int parsableCDATAStartIndex = -1;
		int parsableCommentEndIndex = -1;
		int parsableCommentStartIndex = -1;

		private boolean isParsable( int end) {
			int i = -1;
			
			if ( end < previousEnd || previousEnd == -1) {
				parsableCDATAEndIndex = -1;
				parsableCDATAStartIndex = -1;
				parsableCommentEndIndex = -1;
				parsableCommentStartIndex = -1;
				
				for ( i = end; i > 0; i--) {
					int ch = content.getChar(i);
	
					// Search for '!' since this normally has less occurrences than '<'
					if ( (i + 3) < content.length() && '<' == ch) {
						if ( content.getChar( i+1) == '!') {
							// <!--
							if ( (parsableCommentStartIndex == -1) && content.getChar( i+2) == '-' && content.getChar( i+3) == '-') {
								parsableCommentStartIndex = i;
							}
							
							// <![CDATA[
							if ( (parsableCDATAStartIndex == -1) && ((i + 8) < content.length()) && content.getChar( i+2) == '[' && content.getChar( i+3) == 'C' && content.getChar( i+4) == 'D' && content.getChar( i+5) == 'A' && content.getChar( i+6) == 'T' && content.getChar( i+7) == 'A' && content.getChar( i+8) == '[') {
								parsableCDATAStartIndex = i;
							}
						}
	
					// Search for ']' since this normally has less occurrences than '<'
					} else if ( parsableCDATAEndIndex == -1 && ch == ']') {
						// ]]>
						if ( i > 0  && ((i + 2) <= (content.length() -1)) && content.getChar(i+1) == ']' && content.getChar(i+2) == '>') {
							parsableCDATAEndIndex = i+2;
						}
	
					// Search for '-' since this normally has less occurrences than '<'
					} else if ( parsableCommentEndIndex == -1 && ch == '-') {
						// -->
						if ( i > 0  && ((i + 2) <= (content.length() -1)) && content.getChar(i+1) == '-' && content.getChar(i+2) == '>') {
							parsableCommentEndIndex = i+2;
						}
					} else if ( ch == '>' || ch == '-' || ch == '[') {
						// can skip the next character...
						i--;
					} else if ( ch != '!') {
						// skip the next 2 characters...
						i = i - 2;
					}
	
					if ( parsableCommentStartIndex != -1 && parsableCommentEndIndex < parsableCommentStartIndex) {
						previousEnd = i;
						return false;
					} else if ( parsableCDATAStartIndex != -1 && parsableCDATAEndIndex < parsableCDATAStartIndex) {
						previousEnd = i;
						return false;
					}
				}
				
				previousEnd = 0;

			} else {
				if ( end < parsableCDATAEndIndex) {
					parsableCDATAEndIndex = -1;
				}

				if ( end < parsableCDATAStartIndex) {
					parsableCDATAStartIndex = -1;
				}

				if ( end < parsableCommentEndIndex) {
					parsableCommentEndIndex = -1;
				}

				if ( end < parsableCommentStartIndex) {
					parsableCommentStartIndex = -1;
				}

				if ( parsableCommentStartIndex != -1 && parsableCommentEndIndex < parsableCommentStartIndex) {
					return false;
				} else if ( parsableCDATAStartIndex != -1 && parsableCDATAEndIndex < parsableCDATAStartIndex) {
					return false;
				}
			}

			return true;
		}

		private int findCommentEnd( int p) {
			if ( p >= 0) {
				for ( int i = p; i < content.length(); i++) {
	
					// Search for '-'
					if ( content.getChar(i) == '-') {
						// -->
						if ( i > 0  && ((i + 2) <= (content.length() -1)) && content.getChar(i+1) == '-' && content.getChar(i+2) == '>') {
							return i+2;
						}
						
					}
				}
			}
			
			return -1;
		}

		private int findCDATAEnd( int p) {
			if ( p >= 0) {
				for ( int i = p; i < content.length(); i++) {
	
					// Search for ']'
					if ( content.getChar(i) == ']') {
						// -->
						if ( i > 0  && ((i + 2) <= (content.length() -1)) && content.getChar(i+1) == ']' && content.getChar(i+2) == '>') {
							return i+2;
						}
						
					}
				}
			}
			
			return -1;
		}

		public int indexOf( char ch) {
			return indexOf( ch, 0, getIndex());
		}

		public int indexOf( char ch, int start) {
			return indexOf( ch, start, getIndex());
		}

		public int indexOf( char ch, int start, int end) {
			for ( int i = start; i < end + 1; i++) {
				if ( ch == content.getChar(i)) {
					return i;
				}
			}

			return -1;
		}

		public int lastIndexOf( char ch) {
			return lastIndexOf( ch, 0, getIndex());
		}

		public int lastIndexOf( char ch, int end) {
			return lastIndexOf( ch, 0, end);
		}
		
		public int lastIndexOf( char ch, int start, int end) {
			for ( int i = end; i >= start; i--) {
				if ( ch == content.getChar(i)) {
					return i;
				}
			}

			return -1;
		}

		public int lastIndexOfWhitespace() {
			return lastIndexOfWhitespace( 0, getIndex());
		}
		
		public int lastIndexOfWhitespace( int end) {
			return lastIndexOfWhitespace( 0, end);
		}

		public int lastIndexOfWhitespace( int start, int end) {
			for ( int i = end; i >= start; i--) {
				if ( Character.isWhitespace( content.getChar(i))) {
					return i;
				}
			}

			return -1;
		}

		public char charAt( int i) {
			if ( i >= 0 && i < content.length()) {
				return content.getChar( i);		
			} else {
				return (char)-1;
			}
		}
		
		public String getString( int start, int end) {
			char[] chars = new char[end-start+1];
			
			for ( int i = 0; (start + i) <= end; i++) {
				chars[i] = content.getChar(i+start);
			}
			
			return new String( chars);
		}
		
		public Tag getDocumentTypeTag() {
			Tag tag = null;
			int start = indexOf( '<');
		
			if ( start != -1 && (content.length() > start + 1) && content.getChar( start+1) == '?') {
				start = indexOf( '<', start+1);
			}

			if ( start != -1 && (content.length() > start + 8) && content.getChar( start+1) == '!' && content.getChar( start+2) == 'D' && content.getChar( start+3) == 'O'  && content.getChar( start+4) == 'C'  && content.getChar( start+5) == 'T' && content.getChar( start+6) == 'Y' && content.getChar( start+7) == 'P' && content.getChar( start+8) == 'E') {
				int end1 = indexOf( '[', start);
				int end2 = indexOf( '>', start);
				int nextStart = indexOf( '<', start+8);
				
				if ( start < end1 && end1 < end2) {
					// find the real end...
					int end = indexOf( ']', end1);
					
					if ( end != -1 && (content.length() > end + 1) && content.getChar( end+1) == '>') {
						tag = new Tag( content, start, end+1);
					}
				} else {
					tag = new Tag( content, start, end2);
				}
			}
			
			return tag;
		}

		public Tag getParentStartTag() {
			int index = getCurrentTagStart();
			
			if ( index > 0) {
				int start = lastIndexOf( '<', index-1);
				int end = indexOf( '>', Math.max( 0, start));
				int endTags = 0;

				while ( start != -1 && end != -1) {
					if ( isParsable( start+1)) {
						char ch = content.getChar(start+1);
						
						if ( ch != '-' && ch != '!' && ch != '?' && content.getChar(end-1) != '/') {
							if ( ch == '/') {
								endTags++;
							} else {
								if ( endTags == 0) {
									// This must be a start-tag
									break;
								} else {
									endTags--;
								}
							}
		
						}
					}

					index = start;
					
					if ( index > 0) {
						start = lastIndexOf( '<', index-1);
						
						if ( start >= 0) {
							end = indexOf( '>', start);
						}
					} else {
						return null;
					}
				}

				return new Tag( content, start, Math.min( content.length(), end));
			}
			
			return null;
		}

		public Tag getParentEndTag() {
			int index = getCurrentTagEnd();
			
			if ( index > 0) {
				int start = indexOf( '<', index, content.length() - 1);
				int end = indexOf( '>', Math.max( start, 0), Math.max( content.length() - 1, 0));
				int startTags = 0;
				
				while ( start != -1 && end != -1) {
					char ch = content.getChar(start+1);
					
					if ( ch == '!' ) {
						// <!--
						if ( ((start + 3) < content.length()) && content.getChar( start+2) == '-' && content.getChar( start+3) == '-') {
							end = findCommentEnd( start+3);
						}
						
						// <![CDATA[
						if ( ((start + 8) < content.length()) && content.getChar( start+2) == '[' && content.getChar( start+3) == 'C' && content.getChar( start+4) == 'D' && content.getChar( start+5) == 'A' && content.getChar( start+6) == 'T' && content.getChar( start+7) == 'A' && content.getChar( start+8) == '[') {
							end = findCDATAEnd( start+3);
						}
					} else if ( ch != '?' && content.getChar(end-1) != '/') {
						if ( ch == '/') {
							if ( startTags == 0) {
//									System.out.println( "Start Tags == 0");
								// This must be the end-tag
								break;
							} else {
//									System.out.println( "Start Tag --");
								startTags--;
							}
						} else {
//								System.out.println( "Start Tag ++");
							startTags++;
						}
					}
					
					index = end;
					
					if ( index < content.length() && index >= 0) {
						start = indexOf( '<', index, content.length() - 1);
						end = indexOf( '>', Math.max( start, 0), content.length() - 1);
					} else {
						return null;
					}
				}

				return new Tag( content, start, Math.min( content.length(), end));
			}
			
			return null;
		}

		public Tag getCurrentTag() {
			if ( endsWithComment()) { 
				int start = commentStartIndex;
				int end = findCommentEnd( commentStartIndex + 2);
				
				if ( end > 0) {
					return new Tag( content, start, Math.min( content.length(), end));
				}
			} else if ( endsWithCDATA()) {
				int start = cdataStartIndex;
				int end = findCDATAEnd( start + 5);
				
				if ( end > 0) {
					return new Tag( content, start, Math.min( content.length(), end));
				}
			} else {
				int start = lastIndexOf( '<');
				int end = lastIndexOf( '>');
				
				if ( end < start) { // in Tag
					end = indexOf( '>', start, content.length()-1);

					if ( end > 0) {
						return new Tag( content, start, Math.min( getLength(), end));
					} else { // could not find end, probably the last tag (not closed yet.
						return new Tag( content, start, getLength());
					}
				}
			}

			return null;
		}

		public int getCurrentTagStart() {
			int start = -1;

			if ( endsWithComment()) { 
				start = commentStartIndex;
			} else if ( endsWithCDATA()) {
				start = cdataStartIndex;
			} else {
				start = lastIndexOf( '<');
				int end = lastIndexOf( '>');
				
				if ( end > start) { // not in a Tag
					start = getIndex();
				}
			}

			return start;
		}

		public int getCurrentTagEnd() {
			int end = -1;

			if ( endsWithComment()) { 
				end = commentStartIndex;
			} else if ( endsWithCDATA()) {
				end = cdataStartIndex;
			} else {
				int start = lastIndexOf( '<');
				end = indexOf( '>', start, content.length() - 1);
				
				if ( start > end) { // ????
					end = getIndex();
				}
			}

			return end;
		}
	}
	
//    public class XmlBranchElement extends BranchElement {
//    	public XmlBranchElement( Element parent, AttributeSet a) {
//    	    super( parent, a);
//    	}
//
//        /**
//         * Gets a child element.
//         *
//         * @param index the child index, >= 0 && < getElementCount()
//         * @return the child element, null if none
//         */
//    	public Element getElement(int index) {
//    		int visibleIndex = -1;
//    		
//    		for ( int i = 0; i < super.getElementCount(); i++) {
//    			if ( ((XmlLeafElement)super.getElement( i)).isVisible()) {
//    				visibleIndex++;
//    			}
//    			
//    			if ( visibleIndex == index) {
//    				return super.getElement( i);
//    			}
//    		}
//
//    	    return null;
//    	}
//
//        /**
//         * Gets the number of children for the element.
//         *
//         * @return the number of children >= 0
//         */
//    	public int getElementCount()  {
//    		int count = 0;
//    		for ( int i = 0; i < super.getElementCount(); i++) {
//    			if ( ((XmlLeafElement)super.getElement( i)).isVisible()) {
//    				count++;
//    			}
//    		}
//
//    		return count;
//    	}
//    }
//    
//    public class XmlLeafElement extends LeafElement {
//    	private boolean visible = true;
//
//		public XmlLeafElement(Element parent, AttributeSet a, int offs0, int offs1) {
//		    super(parent, a, offs0, offs1);
//		}
//		
//		public boolean isVisible() {
//			return visible;
//		}
//		
//		public void setVisible( boolean visible) {
//			this.visible = visible;
//		}
//	}
}

