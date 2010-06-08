/*
 * $Id: Tag.java,v 1.7 2004/10/01 15:57:25 edankert Exp $
 *
 * Copyright (C) 2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.util.StringTokenizer;
import java.util.Vector;

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
 * @version	$Revision: 1.7 $, $Date: 2004/10/01 15:57:25 $
 * @author Dogsbay
 */
public class Tag {
	public static final int UNKNOWN = -1;
	public static final int EMPTY_TAG = 0;
	public static final int START_TAG = 1;
	public static final int END_TAG = 2;
	public static final int COMMENT_TAG = 3;
	public static final int CDATA_TAG = 4;
	public static final int PI_TAG = 5;
	public static final int DECLARATION_TAG = 6;

	private int type = EMPTY_TAG;
	private String name = null;
	private String tag = null;
	private String prefix = null;
	private String qname = null;
	private int start = -1;
	private int end = -1;
	private int realStart = -1;
	private int realEnd = -1;
	private BufferContent chars = null;
	private Vector attributes = null;
	
	public Tag( BufferContent chars, int initialStart, int initialEnd) {
		this.realStart = initialStart;
		this.realEnd = initialEnd + 1;

		this.start = initialStart + 1;
		this.end = initialEnd;
		
		this.chars = chars;

		if ( (end - start) > 0) {
			char first = chars.getChar( start);
			char last = chars.getChar( end-1);

			if ( first == '/') {
//				System.out.println("EndTag");
				type = END_TAG;
				start = start + 1;
			} else if ( first == '!') {
				if ( (start + 2 < end) && chars.getChar( start+1) == '-' && chars.getChar( start+2) == '-') {
					type = COMMENT_TAG;

				// ![CDATA[
				} else if ( (start + 7 < end) && chars.getChar( start+1) == '[' && chars.getChar( start+2) == 'C' && chars.getChar( start+3) == 'D' && chars.getChar( start+4) == 'A' && chars.getChar( start+5) == 'T' && chars.getChar( start+6) == 'A' && chars.getChar( start+7) == '[') {
					type = CDATA_TAG;
				
				// !ELEMENT
				} else if ( (start + 7 < end) && chars.getChar( start+1) == 'E' && chars.getChar( start+2) == 'L' && chars.getChar( start+3) == 'E' && chars.getChar( start+4) == 'M' && chars.getChar( start+5) == 'E' && chars.getChar( start+6) == 'N' && chars.getChar( start+7) == 'T') {
					type = DECLARATION_TAG;
					name = "ELEMENT";
					start = start + 1;

				// !ATTLIST
				} else if ( (start + 7 < end) && chars.getChar( start+1) == 'A' && chars.getChar( start+2) == 'T' && chars.getChar( start+3) == 'T' && chars.getChar( start+4) == 'L' && chars.getChar( start+5) == 'I' && chars.getChar( start+6) == 'S' && chars.getChar( start+7) == 'T') {
					type = DECLARATION_TAG;
					name = "ATTLIST";
					start = start + 1;

				// !DOCTYPE
				} else if ( (start + 7 < end) && chars.getChar( start+1) == 'D' && chars.getChar( start+2) == 'O' && chars.getChar( start+3) == 'C' && chars.getChar( start+4) == 'T' && chars.getChar( start+5) == 'Y' && chars.getChar( start+6) == 'P' && chars.getChar( start+7) == 'E') {
					type = DECLARATION_TAG;
					name = "DOCTYPE";
					start = start + 1;

				// !ENTITY
				} else if ( (start + 6 < end) && chars.getChar( start+1) == 'E' && chars.getChar( start+2) == 'N' && chars.getChar( start+3) == 'T' && chars.getChar( start+4) == 'I' && chars.getChar( start+5) == 'T' && chars.getChar( start+6) == 'Y') {
					type = DECLARATION_TAG;
					name = "ENTITY";
					start = start + 1;

				// !NOTATION
				} else if ( (start + 8 < end) && chars.getChar( start+1) == 'N' && chars.getChar( start+2) == 'O' && chars.getChar( start+3) == 'T' && chars.getChar( start+4) == 'A' && chars.getChar( start+5) == 'T' && chars.getChar( start+6) == 'I' && chars.getChar( start+7) == 'O' && chars.getChar( start+8) == 'N') {
					type = DECLARATION_TAG;
					name = "NOTATION";
					start = start + 1;
				} else {
					type = DECLARATION_TAG;
					start = start + 1;
				}

			} else if ( first == '?') {
				type = PI_TAG;
				return;
			} else if ( last == '/') {
				type = EMPTY_TAG;
				end = end - 1;
			} else {
				type = START_TAG;
			}
		} else {
			type = UNKNOWN;
		}
	}
	
	private String getTag() {
		if ( tag == null) {
			StringBuffer buffer = new StringBuffer();
			
			for ( int i = start; i < end; i++) {
				char ch = chars.getChar( i);
	
				buffer.append( ch);
			}
			
			tag = buffer.toString();
		}
		
		return tag;
	}

	private String getQName() {
		if ( qname == null) {
			StringBuffer buffer = new StringBuffer();
			
			for ( int i = start; i < end; i++) {
				char ch = chars.getChar( i);
	
				if ( Character.isWhitespace( ch) || ch == '>') {
					break;
				}
	
				buffer.append( ch);
			}
			
			qname = buffer.toString();
		}
		
		return qname;
	}

	public int getType() {
		return type;
	}
	
	// Return the previous characters, if not a whitespace
	public Vector getValues() {
		String tag = getTag();

		StringTokenizer tokenizer = new StringTokenizer( tag);
		Vector values = new Vector();
		
		while ( tokenizer.hasMoreTokens()) {
			values.addElement( tokenizer.nextToken());
		}
		
		return values;
	}

	// Return the previous strings, if not a whitespace
	public Vector getValues( int off) {
		String tag = getTag();

		StringTokenizer tokenizer = new StringTokenizer( tag.substring( 0, Math.min( (off-start)-1, tag.length())));
		Vector values = new Vector();
		
		while ( tokenizer.hasMoreTokens()) {
			values.addElement( tokenizer.nextToken());
		}
		
		return values;
	}

	public String getPrefix() {
		if ( prefix == null) {
			String qname = getQName();
			int index = qname.indexOf( ':');
			
			if ( index != -1) {
				prefix = qname.substring( 0, index);
			} else {
				prefix = "";
			}
		}
		
		return prefix;
	}

	public String getName() {
		String qname = getQName();

		if ( name == null && qname != null) {
			int index = qname.indexOf( ':');
			
			if ( index != -1) {
				name = qname.substring( index+1, qname.length());
			} else {
				name = qname;
			}
		}

		return name;
	}

	public String getQualifiedName() {
		return getQName();
	}

	public int getEnd() {
		return realEnd;
	}

	public int getStart() {
		return realStart;
	}
	
	public boolean isIncomplete() {
		for ( int i = start; i < end; i++) {
			if ( chars.getChar( i) == '<') {
				return true;
			}
		}

		return false;
	}

	public Vector getAttributeNames() {
//		System.out.println( "getAttributeNames()");
		Vector attributes = getAttributes();
		Vector names = new Vector();
		
		for ( int i = 0; i < attributes.size(); i++) {
			names.addElement( ((String[])attributes.elementAt(i))[0]);
//			System.out.println( ((String[])attributes.elementAt(i))[0]);
		}
//		if ( attributes == null) {
//			attributes = new Vector();
//			int space = firstIndexOfWhitespace( tag);
//			
//			if ( space != -1) {
//				StringTokenizer attributeStrings = new StringTokenizer( tag.substring( space+1));
//				
//				while ( attributeStrings.hasMoreTokens()) {
//					String attribute = attributeStrings.nextToken();
//					int delimiter = attribute.indexOf( '=');
//
//					if ( delimiter != -1) {
//						attributes.addElement( attribute.substring( 0, delimiter));
//					}
//				}
//			}
//		}
		
		return names;
	}
	
	public Vector getAttributeValues() {
//		System.out.println( "getAttributeValues()");

		Vector attributes = getAttributes();
		Vector values = new Vector();
		
		for ( int i = 0; i < attributes.size(); i++) {
			values.addElement( ((String[])attributes.elementAt(i))[1]);
//			System.out.println( ((String[])attributes.elementAt(i))[1]);
		}
		
		return values;
	}

	public Vector getAttributes() {
		if ( attributes == null) {
			attributes = new Vector();
			String tag = getTag();
			int space = firstIndexOfWhitespace( tag);
			
			if ( space != -1) {
				String content = tag.substring( space+1);
				
				int bracket = content.indexOf( '<');

				if ( bracket != -1) {
					content = content.substring( 0, bracket).trim();
				} 
				
				// Content does not have brackets anymore...
				int equals = content.indexOf( '=');
				
				while ( equals != -1) {
					String name = (content.substring( 0, equals)).trim();
					
					// get value...
					content = content.substring( Math.min( equals+1, content.length())).trim();
					String endTag = "\"";

					if ( content.startsWith( "\'")) {
						endTag = "'";
					}
					
					int valueEnd = content.indexOf( endTag, 1);
					
					if ( valueEnd != -1) {
						String value = content.substring( 1, valueEnd);
					
						attributes.addElement( new String[] { name, value});
					} else {
						return attributes;
					}
					
					content = content.substring( Math.min( valueEnd+1, content.length()));
					
					equals = content.indexOf( '=');
				}
			}
		}
		
		return attributes;
	}

	public int getAttributeValueOffset( String name) {
		int pointer = 0;

//		if ( attributes == null) {
			String tag = getTag();
			
			int space = firstIndexOfWhitespace( tag);
			
			if ( space != -1) {
				pointer = space+1;
				String content = tag.substring( space+1);
				
				int bracket = content.indexOf( '<');

				if ( bracket != -1) {
					content = content.substring( 0, bracket);
				} 
				
				// Content does not have brackets anymore...

				int namePos = content.indexOf( name);
				
				while ( namePos != -1) {
					pointer += namePos+name.length();
					content = content.substring( namePos+name.length());
					
					if ( content.trim().startsWith( "=")) {
						int equals = content.indexOf( '=');

						pointer += equals+1;
						content = content.substring( equals + 1);
						
						if ( content.trim().startsWith( "\"")) {
							int quot = content.indexOf( '"');
	
							pointer += quot+2;

							return pointer;
						}
					}
	
					namePos = content.indexOf( name);
				}
			}
//		}
		
		return -1;
	}

	public boolean inAttributeValue( int off) {
		boolean result = false;
		String tag = getTag();
		
		if ( (off - start) < 0) {
			return false;
		}

		tag = tag.substring( 0, Math.min( off-start, tag.length()));
		int space = firstIndexOfWhitespace( tag);

		if ( space != -1) {
			String content = tag.substring( space+1);
			int bracket = content.indexOf( '<');

			if ( bracket != -1) {
				content = content.substring( 0, bracket).trim();
			} 
			
			// Content does not have brackets anymore...
			int equals = content.indexOf( '=');
			
			while ( equals != -1) {
				// get value...
				content = content.substring( Math.min( equals+1, content.length())).trim();

				if ( content.length() > 0) {
					String endTag = "\"";

					if ( content.startsWith( "\'")) {
						endTag = "'";
					}
					
					int valueEnd = content.indexOf( endTag, 1);
					
					if ( valueEnd == -1) {
						// this must be in an attribute value
						return true;
					}
					
					content = content.substring( Math.min( valueEnd+1, content.length()));
				}
			
				equals = content.indexOf( '=');
			}
		}
		
		return result;
	}

	public String getAttributeName( int off) {
		String name = null;
		String tag = getTag();
		
		if ( (off - start) < 0) {
			return name;
		}

		tag = tag.substring( 0, Math.min( off-start, tag.length()));
		int space = firstIndexOfWhitespace( tag);

		if ( space != -1) {
			String content = tag.substring( space+1);
			int bracket = content.indexOf( '<');

			if ( bracket != -1) {
				content = content.substring( 0, bracket).trim();
			} 
			
			// Content does not have brackets anymore...
			int equals = content.indexOf( '=');
			name = content.substring( 0, equals).trim();
			
			while ( equals != -1) {
				// get value...
				content = content.substring( Math.min( equals+1, content.length())).trim();

				if ( content.length() > 0) {
					String endTag = "\"";

					if ( content.startsWith( "\'")) {
						endTag = "'";
					}
					
					int valueEnd = content.indexOf( endTag, 1);
					
					if ( valueEnd == -1) {
						// this must be in an attribute value
						return name;
					}
					
					content = content.substring( Math.min( valueEnd+1, content.length()));
				}
			
				equals = content.indexOf( '=');
				
				if ( equals != -1) {
					name = content.substring( 0, equals).trim();
				}
			}
		}
		
		return name;
	}

	private int firstIndexOfWhitespace( String string) {
		for ( int i = 0; i < string.length(); i++) {
			char ch = string.charAt( i);
	
			if ( Character.isWhitespace( ch)) {
				return i;
			}
		}
		
		return -1;
	}
	
	private int firstIndexOfWhitespaceOrGt( String string) {
		for ( int i = 0; i < string.length(); i++) {
			char ch = string.charAt( i);
	
			if ( Character.isWhitespace( ch) || ch == '>') {
				return i;
			}
		}
		
		return -1;
	}

	public String toString() {
		return "Tag[name=\""+getQualifiedName()+"\":type=\""+toString( getType())+"\":start=\""+getStart()+"\":end=\""+getEnd()+"\"]";
	}

	public boolean equals( Object object) {
		if ( object instanceof Tag) {
			Tag tag = (Tag)object;
			
			if ( start == tag.start && end == tag.end) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static String toString( int type) {
		switch (type) {
			case EMPTY_TAG:
				return "Empty Element";
			case START_TAG:
				return "Element Start";
			case END_TAG:
				return "Element End";
			case COMMENT_TAG:
				return "Comment";
			case CDATA_TAG:
				return "CDATA";
			case PI_TAG:
				return "Processing Instruction";
			case DECLARATION_TAG:
				return "Declaration";
			default:
				return "Unknown";
		}
	}
}

