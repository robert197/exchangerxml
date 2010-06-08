/*
 * $Id: XmlScanner.java,v 1.5 2004/09/23 10:59:21 edankert Exp $
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

import java.io.IOException;

import javax.swing.text.Document;

import org.apache.xerces.util.XMLChar;

/**
 * Associates XML input stream characters with styles specific 
 * for the XML Editor.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.5 $, $Date: 2004/09/23 10:59:21 $
 * @author Dogsbay
 */
public class XmlScanner {
	private Scanner tagScanner = null;

	private AttributeScanner ATTRIBUTE_SCANNER = new AttributeScanner();
	private ElementEndTagScanner ELEMENT_END_TAG_SCANNER = new ElementEndTagScanner();
	private ElementStartTagScanner ELEMENT_START_TAG_SCANNER = new ElementStartTagScanner();
	private StartTagNameScanner START_TAG_NAME_SCANNER = new StartTagNameScanner();
	private EndTagNameScanner END_TAG_NAME_SCANNER = new EndTagNameScanner();
	private GenericDeclarationScanner GENERIC_DECLARATION_SCANNER = new GenericDeclarationScanner();
	private CommentScanner COMMENT_SCANNER = new CommentScanner();
	private CDATAScanner CDATA_SCANNER = new CDATAScanner();
	private ProcessingInstructionScanner PI_SCANNER = new ProcessingInstructionScanner();

	private EntityDeclarationScanner ENTITY_DECLARATION_SCANNER = new EntityDeclarationScanner();
	private AttlistDeclarationScanner ATTLIST_DECLARATION_SCANNER = new AttlistDeclarationScanner();
	private ElementDeclarationScanner ELEMENT_DECLARATION_SCANNER = new ElementDeclarationScanner();
	private NotationDeclarationScanner NOTATION_DECLARATION_SCANNER = new NotationDeclarationScanner();
	private DoctypeDeclarationScanner DOCTYPE_DECLARATION_SCANNER = new DoctypeDeclarationScanner();
	private EntityTagScanner ENTITY_TAG_SCANNER = new EntityTagScanner();

	private IncludeExcludeStartTagScanner INCLUDE_EXCLUDE_START_TAG_SCANNER = new IncludeExcludeStartTagScanner();
//	private IncludeExcludeEndTagScanner INCLUDE_EXCLUDE_END_TAG_SCANNER = new IncludeExcludeEndTagScanner();

	private TagScanner TAG_SCANNER = new TagScanner();
	
    private int start = 0;

    protected XmlInputReader in	= null;

    public int token = -1;
    public boolean error = false;
    public long pos = 0;

	/**
	 * Constructs a scanner for the Document.
	 *
	 * @param document the document containing the XML content.
	 */
    public XmlScanner( Document document) throws IOException {
	    try {
	        in = new XmlInputReader( new XmlInputStream( document));
	    } catch( Exception exception) {
	    	exception.printStackTrace();
	    }

	    in.read();
	    scan();
    }

	/**
	 * Sets the scanning range.
	 *
	 * @param start the start of the range.
	 * @param end the end of the range.
	 */
	public void setRange( int start, int end) throws IOException {
		in.setRange( start, end);

	    this.start = start;

	    token = -1;
	    pos = 0;
	    tagScanner = null;

	    in.read();
	    scan();
	}

    /**
     * Gets the starting location of the current token in the 
	 * document.
     *
     * @return the starting location.
     */
    public final int getStartOffset() {
	    int begOffs = (int) (pos & Constants.MAXFILESIZE);
	    return start + begOffs;
	}

    /**
     * Gets the end location of the current token in the 
     * document.
     *
     * @return the end location.
     */
    public final int getEndOffset() {
	    int endOffs = (int) (in.pos & Constants.MAXFILESIZE);
	    return start + endOffs;
	}

    /**
     * Scans the Xml Stream for XML specific tokens.
     *
     * @return the last location.
     */
    public long scan() throws IOException {
    	error = false;
        long l = pos;
        
//        if ( tagScanner != null && in.getLastChar() != '<') {
		if ( tagScanner != null) {
			token = tagScanner.scan( in);

			if ( tagScanner.isFinished()) {
//				prevScanner = tagScanner;
				tagScanner = null;
			}
			
			return l;
		} else {

	        while ( true) {
			    pos = in.pos;
				int ch = in.getLastChar();
//				System.out.print( "["+(char)ch+"]");
	          
			    switch(ch) {
	            case -1: 
	                token = -1;
	                return l;

	            case 60: // '<'
					ch = in.read();

					tagScanner = TAG_SCANNER;
	                tagScanner.reset();
					
	                token = tagScanner.scan( in);
	                return l;
					
			    case 93: // ']'
			    	ch = in.read();
			    	if ( ch == 62) { // '>'
			    		ch = in.read();
				    	token = Constants.SPECIAL;
			    	} else if ( ch == 93) { // ']'
			    		ch = in.read();
				    	if ( ch == 62) { // '>'
					    	ch = in.read();
					    	token = Constants.SPECIAL;
				    	}
			    	} else {
						token = Constants.ELEMENT_VALUE;
			    	}
					return l;

	            default:
                    scanValue();
                    token = Constants.ELEMENT_VALUE;
                    return l;
	            }
	        }
		}
    }
	
	// Scans a XML element value.
	private void scanValue() throws IOException {
		int ch = in.read();

		do {
		    switch( ch) {
		        case -1: 
		            // eof
		            return;

		        case 60: // '<'
					return;

		        case 93: // ']'
		        	return;

		        default:
		            ch = in.read();
		            break;

		    }
		} while( true);
	}
	
	// Scans until it reaches whitespace.
	private void scanString() throws IOException {
		int ch = in.read();
	
	    while ( true) {
			if ( !Character.isWhitespace( (char)ch) && ch != '>' && ch != -1) {
				ch = in.read();
			} else {
				return;
	        }
	    }
	}

	// Returns when a non whitespace character has been detected.
	private void skipWhitespace() throws IOException {
		int ch = in.read();
//		int ch = in.getLastChar();
	
	    while ( true) {
			if ( Character.isWhitespace( (char)ch)) {
				ch = in.read();
			} else {
				return;
	        }
	    }
	}

	private void scanDeclarationString( int end) throws IOException {
//		System.out.println( "scanDeclarationString( "+((char)end)+")");
		int ch = in.read();

		while ( ch != end && ch != -1) {
		    ch = in.read();
		}
	}

	// Scans a String.
	private void scanString( int end) throws IOException {
	    int ch = in.read();

	    while ( ch != end && ch != '<' && ch != -1) {
// <    while ( ch != end && ch != -1) {
            ch = in.read();
            
// <        if ( ch == '<') {
// <          	error = true;
// <            return;
// <        }
	    }
	}

	// Scans a DTD Entity.
	private void scanEntity() throws IOException {
	    int ch = in.read();

	    while ( ch != ';' && ch != '>' && ch != -1) {
	        ch = in.read();
	    }
	}

	/**
	 * A scanner for anything starting with a '<'.
	 */
	private class TagScanner extends Scanner {
		private Scanner scanner = null;
		private Scanner prevScanner = null;
// <    private boolean waitingForEnd = false;
		
		public int scan( XmlInputReader in) throws IOException {
			if ( scanner != null) {
				int token = scanner.scan( in);

				if ( scanner.isFinished()) {
					prevScanner = scanner;
					scanner = null;
// <    			waitingForEnd = true;
				}
				
				return token;
// <		} else if ( !waitingForEnd){
			} else {
				int character = in.getLastChar();

				if ( character == 33) { // '!'
					character = in.read();
					if ( character == 45) { // '-'
						error = true;
						character = in.read();
						if ( character == 45) { // '-'
							error = false;
							scanner = COMMENT_SCANNER;
						}
							
					} else if ( character == 91) { // '['
						character = in.read();
						if ( character == 67) { // 'C'
							error = true;
							character = in.read();
							if ( character == 68) { // 'D'
								character = in.read();
								if ( character == 65) { // 'A'
									character = in.read();
									if ( character == 84) { // 'T'
										character = in.read();
										if ( character == 65) { // 'A'
											character = in.read();
											if ( character == 91) { // '['
												scanner = CDATA_SCANNER;
												error = false;
											}
										}
									}
								}
							}
						} else {
							scanner = INCLUDE_EXCLUDE_START_TAG_SCANNER;
						}
					}

					if ( scanner == null) {
						scanner = ENTITY_TAG_SCANNER;
					}
					
					scanner.reset();

					if ( scanner instanceof CommentScanner) {
						return Constants.COMMENT;
					} else if ( scanner instanceof CDATAScanner) {
						return Constants.CDATA;
					} else {
						return Constants.SPECIAL;
					}

				} else if ( character == 63) { // '?'
					character = in.read();
					scanner = PI_SCANNER;
					scanner.reset();

				    return Constants.SPECIAL;

				} else if ( character == 47) { // '/'
					character = in.read();
					scanner = ELEMENT_END_TAG_SCANNER;
					scanner.reset();
					
				    return Constants.SPECIAL;

				} else if ( character == 91) { // '['
					character = in.read();

				    if ( prevScanner instanceof EntityTagScanner || prevScanner instanceof IncludeExcludeStartTagScanner) {
				    	finished();
			    	}
			    	return Constants.SPECIAL;

				} else if ( character == 62) { // '>'
					character = in.read();
				    finished();

				    if ( prevScanner instanceof CommentScanner) {
				    	return Constants.COMMENT;
				    } else if ( prevScanner instanceof CDATAScanner) {
				    	return Constants.CDATA;
				    } else {
				    	return Constants.SPECIAL;
				    }

				} else  {
					scanner = ELEMENT_START_TAG_SCANNER;
					scanner.reset();

					return Constants.SPECIAL;
				}
//	>		} else {
//	>			int character = in.getLastChar();
//				
//				while ( character != 62 && character != 60) { // '>' && '<'
//					if ( character != 62 && !Character.isWhitespace( (char)character)) {
//						error = true;
//					}
//
//					character = in.read();
//				}
//				
//				if ( character != 60) { // '<'
//					skipWhitespace();
//				}
//			    
//			    finished();
//
//			    if ( prevScanner instanceof CommentScanner) {
//			    	return Constants.COMMENT;
//			    } else if ( prevScanner instanceof CDATAScanner) {
//			    	return Constants.CDATA;
//			    } else {
//			    	return Constants.SPECIAL;
//			    }
			}
		}

		public void reset() {
			super.reset();
			scanner = null;
//			waitingForEnd = false;
		}
	}
	
	/**
	 * Scans an entity '<!'.
	 */
//	private class EntityTagScanner extends Scanner {
//
//		public int scan( XmlInputReader in) throws IOException {
//			int character = in.read();
//
//			while ( true) {
//			    switch( character) {
//			        case -1: 
//			            finished();
//			            return Constants.ENTITY;
//	
//			        case 62: // '>'
//						finished();
//						return Constants.ENTITY;
//	
//			        default:
//			            character = in.read();
//			            break;
//	
//			    }
//			}
//		}
//
//		public void reset() {
//			super.reset();
//		}
//	}

	/**
	 * Scans a comment entity '<!--'.
	 */
	private class CommentScanner extends Scanner {
		public int scan( XmlInputReader in) throws IOException {
			int character = in.read();

			while ( true) {
//				System.out.print((char)character);
			
			    switch( character) {
			        case -1: // EOF
			            finished();
			            return Constants.COMMENT;
	
			        case 45: // '-'
						character = in.read();
						if ( character == 45) { // '-'
							character = in.read();
							if ( character == 62) { // '>'
//								character = in.read();
								finished();
								return Constants.COMMENT;
							}
							
							error = true;
						}
						break;
	
			        default:
			            character = in.read();
			            break;
	
			    }
			}
		}

		public void reset() {
			super.reset();
		}
	}

	/**
	 * Scans a CDATA entity '<![CDATA['.
	 */
	private class CDATAScanner extends Scanner {
		public int scan( XmlInputReader in) throws IOException {
			int character = in.read();
			
			//tcurley 08-05-08
			//finish for a cdata section should be ]]>
			//but a problem or bug can occur when you have cdata like <![CDATA[blah[anything]]]>
			//the three ]]] cause the syntax highlighting to throw an error.
			
			boolean endConditionFirstBracket = false;
			boolean endConditionSecondBracket = false;
			
			while ( true) {
//				System.out.print((char)character);
				
			    switch( character) {
			        case -1: // EOF
			            finished();
			            return Constants.CDATA;
	
			        case 93: // ']'
						character = in.read();
						if ( character == 93) { // ']'
							character = in.read();
							
							while(character == 93) {
								//just continue
								character = in.read();
							}
							if ( character == 62) { // '>'
//								character = in.read();
								finished();
								return Constants.CDATA;
							}
							
							//error = true;
						}
						break;
						
			        /*case 93: //']'
			        	character = in.read();
			        	if(endConditionFirstBracket == false) {
			        		//this is the first of 3 end conditions
			        		System.out.println("endConditionFirstBracket true");
			        		endConditionFirstBracket = true;
			        	}
			        	else if(endConditionSecondBracket == false) {
			        		//this is the second of 3 end conditions
			        		System.out.println("endConditionSecondBracket true");
			        		endConditionSecondBracket = true;
			        	}
			        	else {
			        		//this is a third occurence of the bracket
			        		//assume first was a normal character and 
			        		//that this is still the second condition
			        		//just repeating this
			        		System.out.println("endConditionSecondBracket continuing to be true after finding another ]");
			        		endConditionSecondBracket = true;
			        	}
			        	break;
			        
			        case 62: //'>'
			        	character = in.read();
			        	System.out.println("found >");
			        	if((endConditionFirstBracket == true) && (endConditionSecondBracket == true)) {
			        		System.out.println("found > with endConditionSecondBracket and endConditionFirstBracket are true");
			        		finished();
							return Constants.CDATA;
			        	}
			        	break;*/
	
			        default:
			            character = in.read();
			            break;
	
			    }
			}
		}

		public void reset() {
			super.reset();
		}
	}


	/**
	 * Scans an element end tag '</xxx:xxxx>'.
	 */
	private class ElementEndTagScanner extends Scanner {
		private Scanner scanner = null;
		
		public int scan( XmlInputReader in) throws IOException {
            if ( scanner == null) {
				scanner = END_TAG_NAME_SCANNER;
				scanner.reset();
			}

			int token = scanner.scan( in);

			if ( scanner.isFinished()) {
				finished();
			}
			
			return token;
		}

		public void reset() {
			super.reset();
			scanner = null;
		}
	}

	/**
	 * Scans an element start tag '<xxx:xxxx yyy:yyyy="yyyyy" xmlns:hsshhs="sffsfsf">'.
	 */
	private class ElementStartTagScanner extends Scanner {
		private Scanner scanner = null;
		
		public int scan( XmlInputReader in) throws IOException {
			int token = 0;
			
			if ( scanner == null) {
				scanner = START_TAG_NAME_SCANNER;
				scanner.reset();

				token = scanner.scan( in);
			} else {
				token = scanner.scan( in);
			}
			
			if ( scanner.isFinished()) {
				if ( scanner instanceof StartTagNameScanner) {
					scanner = ATTRIBUTE_SCANNER;
					scanner.reset();
				} else {
					finished();
				}
			}

			return token;
		}
		
		public void reset() {
			super.reset();
			scanner = null;
		}
	}

	/**
	 * A scanner for anything starting with a '<!'.
	 */
	private class EntityTagScanner extends Scanner {
		private Scanner scanner = null;
		
		public int scan( XmlInputReader in) throws IOException {

			if ( scanner != null) {
				int token = scanner.scan( in);

				if ( scanner.isFinished()) {
					finished();
					scanner = null;
				}
				
				return token;
			} else {
				int character = in.getLastChar();

				if ( character == 69) { // 'E'
					character = in.read();
					if ( character == 78) { // 'N'
						character = in.read();
						if ( character == 84) { // 'T'
							character = in.read();
							if ( character == 73) { // 'I'
								character = in.read();
								if ( character == 84) { // 'T'
									character = in.read();
									if ( character == 89) { // 'Y'
										skipWhitespace();
										scanner = ENTITY_DECLARATION_SCANNER;
									}
								}
							}
						}
					} else if ( character == 76) { // 'L'
						character = in.read();
						if ( character == 69) { // 'E'
							character = in.read();
							if ( character == 77) { // 'M'
								character = in.read();
								if ( character == 69) { // 'E'
									character = in.read();
									if ( character == 78) { // 'N'
										character = in.read();
										if ( character == 84) { // 'T'
											skipWhitespace();
											scanner = ELEMENT_DECLARATION_SCANNER;
										}
									}
								}
							}
						}
					}
					
				} else if ( character == 65) { // 'A'
					character = in.read();
					if ( character == 84) { // 'T'
						character = in.read();
						if ( character == 84) { // 'T'
							character = in.read();
							if ( character == 76) { // 'L'
								character = in.read();
								if ( character == 73) { // 'I'
									character = in.read();
									if ( character == 83) { // 'S'
										character = in.read();
										if ( character == 84) { // 'T'
											skipWhitespace();
											scanner = ATTLIST_DECLARATION_SCANNER;
										}
									}
								}
							}
						}
					}
				} else if ( character == 78) { // 'N'
					character = in.read();
					if ( character == 79) { // 'O'
						character = in.read();
						if ( character == 84) { // 'T'
							character = in.read();
							if ( character == 65) { // 'A'
								character = in.read();
								if ( character == 84) { // 'T'
									character = in.read();
									if ( character == 73) { // 'I'
										character = in.read();
										if ( character == 79) { // 'O'
											character = in.read();
											if ( character == 78) { // 'N'
												skipWhitespace();
												scanner = NOTATION_DECLARATION_SCANNER;
											}
										}
									}
								}
							}
						}
					}
				} else if ( character == 68) { // 'D'
					character = in.read();
					if ( character == 79) { // 'O'
						character = in.read();
						if ( character == 67) { // 'C'
							character = in.read();
							if ( character == 84) { // 'T'
								character = in.read();
								if ( character == 89) { // 'Y'
									character = in.read();
									if ( character == 80) { // 'P'
										character = in.read();
										if ( character == 69) { // 'E'
											skipWhitespace();
											scanner = DOCTYPE_DECLARATION_SCANNER;
										}
									}
								}
							}
						}
					}
				}

				if ( scanner == null) {
					error = true;
					scanner = GENERIC_DECLARATION_SCANNER;
				}
				
				scanner.reset();
				
				if ( scanner instanceof ElementDeclarationScanner) {
					return Constants.ELEMENT_DECLARATION;
				} else if ( scanner instanceof NotationDeclarationScanner) {
					return Constants.NOTATION_DECLARATION;
				} else if ( scanner instanceof DoctypeDeclarationScanner) {
					return Constants.DOCTYPE_DECLARATION;
				} else if ( scanner instanceof EntityDeclarationScanner) {
					return Constants.ENTITY_DECLARATION;
				} else if ( scanner instanceof AttlistDeclarationScanner) {
					return Constants.ATTLIST_DECLARATION;
				} else {
					return Constants.SPECIAL;
				}
			}
		}

		public void reset() {
			super.reset();
			scanner = null;
		}
	}

	/**
	 * Scans an entity declaration '<!???????'.
	 */
	private class GenericDeclarationScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
			        	skipWhitespace();
						token = Constants.ENTITY_NAME;
			        	return Constants.SPECIAL;

			        case 39: // '''
			        case 34: // '"'
						if ( token != Constants.ATTRIBUTE_VALUE) {
							int oldToken = token;
							token = Constants.ATTRIBUTE_VALUE;

							return oldToken;
						}

			        	scanDeclarationString( character);
		        		skipWhitespace();
		        		return token;

			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
						int oldToken = token;

						if ( token == Constants.ELEMENT_NAME) {
			            	token = Constants.ELEMENT_VALUE;
						} else if ( token == Constants.ELEMENT_VALUE) {
							token = Constants.ATTRIBUTE_NAME;
						}

		            	skipWhitespace();
			            return oldToken;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.ENTITY_NAME;
		}
	}

	/**
	 * Scans an entity declaration '<!ENTITY'.
	 */
	private class EntityDeclarationScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
						skipWhitespace();
			        	return Constants.SPECIAL;

			        case 39: // '''
			        case 34: // '"'
						if ( token != Constants.STRING_VALUE) {
							int oldToken = token;
							token = Constants.STRING_VALUE;

							return oldToken;
						}

			        	scanDeclarationString( character);
		        		skipWhitespace();
		        		return token;

			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9: // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
						int oldToken = token;

						if ( token == Constants.ENTITY_NAME) {
							token = Constants.ENTITY_TYPE;
						}

		            	skipWhitespace();
			            return oldToken;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.ENTITY_NAME;
		}
	}

	/**
	 * Scans an attlist declaration '<!ATTLIST'.
	 */
	private class AttlistDeclarationScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
//			System.out.print((char)character);
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
						scanEntity();
						skipWhitespace();
						return Constants.ENTITY_VALUE;

			        case 40: // '('
						if ( token != Constants.ATTLIST_VALUE) {
							int oldToken = token;
							token = Constants.ATTLIST_VALUE;

							return oldToken;
						}
			        	skipWhitespace();
			        	return Constants.SPECIAL;

			        case 41: // ')'
			        	if ( token != Constants.ATTLIST_NAME) {
			        		int oldToken = token;
			        		token = Constants.ATTLIST_NAME;

			        		return oldToken;
			        	}
			        	skipWhitespace();
			        	return Constants.SPECIAL;

			        case 124: // '|'
			        	if ( token != Constants.SPECIAL) {
			        		int oldToken = token;
			        		token = Constants.SPECIAL;

			        		return oldToken;
			        	}

						token = Constants.ATTLIST_VALUE;
			        	skipWhitespace();
			        	return Constants.SPECIAL;

			        case 35: // '#'
						scanString();
						if ( in.getLastChar() != 62) { // '>'
							skipWhitespace();
						}
						return Constants.ATTLIST_DEFAULT;

			        case 39: // '''
			        case 34: // '"'
			        	scanDeclarationString( character);
						skipWhitespace();
		        		return Constants.STRING_VALUE;

			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
						int oldToken = token;
						
						if ( token != Constants.ATTLIST_NAME) {
							token = Constants.ATTLIST_NAME;
						}
						error = false;
		            	skipWhitespace();
			            return oldToken;
						
					// ATTLIST Type?
					// CDATA
			        case 67: // 'C'
						character = in.read();

						if ( character == 68) { // 'D'
							character = in.read();
							if ( character == 65) { // 'A'
								character = in.read();
								if ( character == 84) { // 'T'
									character = in.read();
									if ( character == 65) { // 'A'
										character = in.read();
										if ( Character.isWhitespace( (char)character)) {
											skipWhitespace();
											return Constants.ATTLIST_TYPE;
										}
									}
								}
							}
						}
//						error = true;
						break;

			        // ID/IDREF/IDREFS
			        case 73: // 'I'
			        	character = in.read();

			        	if ( character == 68) { // 'D'
			        		character = in.read();

			        		if ( Character.isWhitespace( (char)character)) {
			        			skipWhitespace();
			        			return Constants.ATTLIST_TYPE;
			        		} else if ( character == 82) { // 'R'
			        			character = in.read();
			        			if ( character == 69) { // 'E'
			        				character = in.read();
			        				if ( character == 70) { // 'F'
			        					character = in.read();

			        					if ( Character.isWhitespace( (char)character)) {
			        						skipWhitespace();
			        						return Constants.ATTLIST_TYPE;
			        					} else if ( character == 83) { // 'S'
			        						character = in.read();

				        					if ( Character.isWhitespace( (char)character)) {
				        						skipWhitespace();
				        						return Constants.ATTLIST_TYPE;
				        					}
			        					}
			        				}
			        			}
			        		}
			        	}
//						error = true;
			        	break;


			        // ENTITY/ENTITIES
			        case 69: // 'E'
			        	character = in.read();
			        	if ( character == 78) { // 'N'
			        		character = in.read();
							if ( character == 84) { // 'T'
			        			character = in.read();
			        			if ( character == 73) { // 'I'
			        				character = in.read();
			        				if ( character == 84) { // 'T'
			        					character = in.read();

										if ( character == 89) { // 'Y'
			        						character = in.read();

				        					if ( Character.isWhitespace( (char)character)) {
				        						skipWhitespace();
				        						return Constants.ATTLIST_TYPE;
				        					}
			        					} else if ( character == 73) { // 'I'
				        					character = in.read();
				        					if ( character == 69) { // 'E'
				        						character = in.read();
					        					if ( character == 83) { // 'S'
			    		    						character = in.read();

						        					if ( Character.isWhitespace( (char)character)) {
						        						skipWhitespace();
						        						return Constants.ATTLIST_TYPE;
						        					}
					        					}
				        					}
			        					}
			        				}
			        			}
			        		}
			        	}
//						error = true;
			        	break;

			        // NMTOKEN/NMTOKENS/NOTATION
			        case 78: // 'N'
			        	character = in.read();
			        	if ( character == 77) { // 'M'
			        		character = in.read();
			        		if ( character == 84) { // 'T'
			        			character = in.read();
			        			if ( character == 79) { // 'O'
			        				character = in.read();
			        				if ( character == 75) { // 'K'
			        					character = in.read();
			        					if ( character == 69) { // 'E'
			        						character = in.read();
			        						if ( character == 78) { // 'N'
			        							character = in.read();

				            					if ( Character.isWhitespace( (char)character)) {
				            						skipWhitespace();
				            						return Constants.ATTLIST_TYPE;
				            					} else if ( character == 83) { // 'S'
					            					character = in.read();
		
			        	        					if ( Character.isWhitespace( (char)character)) {
			        	        						skipWhitespace();
			        	        						return Constants.ATTLIST_TYPE;
			        	        					}
			                					}
			            					}
			        					}
			        				}
			        			}
			        		}
						// NOTATION
			        	} else if ( character == 79) { // 'O'
				        	character = in.read();
				        	if ( character == 84) { // 'T'
				        		character = in.read();
				        		if ( character == 65) { // 'A'
				        			character = in.read();
				        			if ( character == 84) { // 'T'
				        				character = in.read();
				        				if ( character == 73) { // 'I'
				        					character = in.read();
				        					if ( character == 79) { // 'O'
				        						character = in.read();
				        						if ( character == 78) { // 'N'
				        							character = in.read();

				        	    					if ( Character.isWhitespace( (char)character)) {
				        	    						skipWhitespace();
				        	    						return Constants.ATTLIST_TYPE;
				        	    					}
				        						}
				        					}
				        				}
				        			}
				        		}
				        	}
			        	}
//						error = true;
			        	break;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.ELEMENT_DECLARATION_NAME;
		}
	}

	/**
	 * Scans an attlist declaration '<!NOTATION'.
	 */
	private class NotationDeclarationScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
//			System.out.print((char)character);
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
						scanEntity();
						skipWhitespace();
						return Constants.ENTITY_VALUE;

			        case 39: // '''
			        case 34: // '"'
			        	scanDeclarationString( character);
						skipWhitespace();
		        		return Constants.STRING_VALUE;

			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
		            	skipWhitespace();
			            return token;
						
					// NOTATION Type?
					// SYSTEM
			        case 83: // 'S'
						character = in.read();
						if ( character == 89) { // 'Y'
							character = in.read();
							if ( character == 83) { // 'S'
								character = in.read();
								if ( character == 84) { // 'T'
									character = in.read();
									if ( character == 69) { // 'E'
										character = in.read();
										if ( character == 77) { // 'M'
											character = in.read();
											if ( Character.isWhitespace( (char)character)) {
												skipWhitespace();
												return Constants.NOTATION_DECLARATION_TYPE;
											}
										}
									}
								}
							}
						}
//						error = true;
						break;

					// PUBLIC
			        case 80: // 'P'
			        	character = in.read();
			        	if ( character == 85) { // 'U'
			        		character = in.read();
			        		if ( character == 66) { // 'B'
			        			character = in.read();
			        			if ( character == 76) { // 'L'
			        				character = in.read();
			        				if ( character == 73) { // 'I'
			        					character = in.read();
			        					if ( character == 67) { // 'C'
			        						character = in.read();
			        						if ( Character.isWhitespace( (char)character)) {
			        							skipWhitespace();
			        							return Constants.NOTATION_DECLARATION_TYPE;
			        						}
			        					}
			        				}
			        			}
			        		}
			        	}
//						error = true;
			        	break;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.NOTATION_DECLARATION_NAME;
		}
	}

	/**
	 * Scans an attlist declaration '<!DOCTYPE'.
	 */
	private class DoctypeDeclarationScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
//			System.out.print((char)character);
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
						scanEntity();
						skipWhitespace();
						return Constants.ENTITY_VALUE;

			        case 39: // '''
			        case 34: // '"'
			        	scanDeclarationString( character);
						skipWhitespace();
		        		return Constants.STRING_VALUE;

			        case 91: // '['
			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
		            	skipWhitespace();
			            return token;
						
					// DOCTYPE Type?
					// SYSTEM
			        case 83: // 'S'
						character = in.read();
						if ( character == 89) { // 'Y'
							character = in.read();
							if ( character == 83) { // 'S'
								character = in.read();
								if ( character == 84) { // 'T'
									character = in.read();
									if ( character == 69) { // 'E'
										character = in.read();
										if ( character == 77) { // 'M'
											character = in.read();
											if ( Character.isWhitespace( (char)character)) {
												skipWhitespace();
												return Constants.DOCTYPE_DECLARATION_TYPE;
											}
										}
									}
								}
							}
						}
//						error = true;
						break;

					// PUBLIC
			        case 80: // 'P'
			        	character = in.read();
			        	if ( character == 85) { // 'U'
			        		character = in.read();
			        		if ( character == 66) { // 'B'
			        			character = in.read();
			        			if ( character == 76) { // 'L'
			        				character = in.read();
			        				if ( character == 73) { // 'I'
			        					character = in.read();
			        					if ( character == 67) { // 'C'
			        						character = in.read();
			        						if ( Character.isWhitespace( (char)character)) {
			        							skipWhitespace();
			        							return Constants.DOCTYPE_DECLARATION_TYPE;
			        						}
			        					}
			        				}
			        			}
			        		}
			        	}
						error = true;
			        	break;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.ELEMENT_DECLARATION_NAME;
		}
	}

	/**
	 * Scans a processing instruction '<?xml version="1.0"?>'.
	 */
	private class ProcessingInstructionScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
//			System.out.print((char)character);
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 39: // '''
			        case 34: // '"'
			        	scanString( character);
						skipWhitespace();
		        		return Constants.PI_VALUE;

			        case 61: // '='
						if ( token != Constants.SPECIAL) {
							int oldToken = token;
							token = Constants.SPECIAL;
							
							return oldToken;
						}
						token = Constants.PI_NAME;
						skipWhitespace();
						return Constants.SPECIAL;
						
			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
		            	if ( token != Constants.PI_NAME) {
		            		int oldToken = token;
		            		token = Constants.PI_NAME;
		            		
		            		return oldToken;
		            	}

		            	skipWhitespace();
			            return token;
						
			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.PI_TARGET;
		}
	}

	/**
	 * Scans an attlist declaration '<!ELEMENT'.
	 */
	private class ElementDeclarationScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
//			System.out.print((char)character);
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
						scanEntity();
						skipWhitespace();
						return Constants.ENTITY_VALUE;

//			        case 40: // '('
//						if ( token != Constants.ELEMENT_DECLARATION_CHILD) {
//							int oldToken = token;
//							token = Constants.ELEMENT_DECLARATION_CHILD;
//
//							return oldToken;
//						}
//			        	skipWhitespace();
//			        	return Constants.SPECIAL;

			        case 40: // '('
			        case 41: // ')'
			        case 44: // ','
		        	case 124: // '|'
			        	if ( token != Constants.SPECIAL) {
			        		int oldToken = token;
			        		token = Constants.SPECIAL;

			        		return oldToken;
			        	}

						character = in.read();
			        	return Constants.SPECIAL;

			        case 42: // '*'
			        case 43: // '+'
			        case 63: // '?'
			        	if ( token != Constants.ELEMENT_DECLARATION_OPERATOR) {
			        		int oldToken = token;
			        		token = Constants.ELEMENT_DECLARATION_OPERATOR;

			        		return oldToken;
			        	}

			        	token = Constants.ELEMENT_DECLARATION_CHILD;
			        	skipWhitespace();
			        	return Constants.ELEMENT_DECLARATION_OPERATOR;

			        case 35: // '#'
						character = in.read();
						if ( character == 80) { // 'P'
							character = in.read();
							if ( character == 67) { // 'C'
								character = in.read();
								if ( character == 68) { // 'D'
									character = in.read();
									if ( character == 65) { // 'A'
										character = in.read();
										if ( character == 84) { // 'T'
											character = in.read();
											if ( character == 65) { // 'A'
												skipWhitespace();
												return Constants.ELEMENT_DECLARATION_PCDATA;
											}
										}
									}
								}
							}
						}
//						error = true;
						break;

			        case 39: // '''
			        case 34: // '"'
			        	scanDeclarationString( character);
						skipWhitespace();
		        		return Constants.STRING_VALUE;

			        case 62: // '>'
				        finished();
						return Constants.SPECIAL;

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
						int oldToken = token;
						
						if ( token != Constants.ELEMENT_DECLARATION_CHILD) {
							token = Constants.ELEMENT_DECLARATION_CHILD;
						}
						
						error = false;
		            	skipWhitespace();
			            return oldToken;
						
					// ELEMENT Type?
					// ANY
			        case 65: // 'A'
						character = in.read();
						if ( character == 78) { // 'N'
							character = in.read();
							if ( character == 89) { // 'Y'
								character = in.read();
								if ( Character.isWhitespace( (char)character)) {
									skipWhitespace();
									return Constants.ELEMENT_DECLARATION_TYPE;
								} else if ( character == 62) {
									return Constants.ELEMENT_DECLARATION_TYPE;
								}
							}
						}

//						error = true;
						break;

					// EMPTY
					case 69: // 'E'
						character = in.read();
						if ( character == 77) { // 'M'
							character = in.read();
							if ( character == 80) { // 'P'
								character = in.read();
								if ( character == 84) { // 'T'
									character = in.read();
									if ( character == 89) { // 'Y'
										character = in.read();
										if ( Character.isWhitespace( (char)character)) {
											skipWhitespace();
											return Constants.ELEMENT_DECLARATION_TYPE;
										} else if ( character == 62) {
											return Constants.ELEMENT_DECLARATION_TYPE;
										} 
									}
								}
							}
						}

//						error = true;
						break;

			        default:
						if ( token != Constants.ELEMENT_DECLARATION_NAME) {
							token = Constants.ELEMENT_DECLARATION_CHILD;
						}
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.ELEMENT_DECLARATION_NAME;
		}
	}

	private class IncludeExcludeStartTagScanner extends Scanner {
		private int token;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return token;
	
			        case 37: // '%'
			        	if ( token != Constants.ENTITY_VALUE) {
			        		int oldToken = token;
			        		token = Constants.ENTITY_VALUE;

			        		return oldToken;
			        	}

			        	scanEntity();
			        	return token;

			        case 91: // '['
				        finished();
						return Constants.SPECIAL;

			        default:
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			token = Constants.SPECIAL;
		}
	}

	/**
	 * Scans an element name '<xxx:xxxx'.
	 */
	private class StartTagNameScanner extends Scanner {
		private boolean hasPrefix = false;
		private boolean emptyElement = false;
		private boolean hasName = false;
		
		public int scan( XmlInputReader in) throws IOException {
			int character =  in.getLastChar();
		
			do {
			    switch( character) {
			        case -1: 
//			            System.err.println("Error ["+pos+"]: eof in element name!");
			            finished();
			            return Constants.ELEMENT_NAME;
	
			        case 58: // ':'
			        	if ( hasPrefix) {
			        		character = in.read();

			        		if ( !XMLChar.isNameStart( (char)character)) {
			        			error = true;
			        		}
			        		
			        		return Constants.SPECIAL;
			        	} else {
			        		hasName = false;
			        		hasPrefix = true;
			        		return Constants.ELEMENT_PREFIX;
			        	}

			        case 47: // '/'
						if ( emptyElement) {
				            character = in.read();
						} else {
			            	emptyElement = true;
			            	return Constants.ELEMENT_NAME;
						}

			        case 62: // '>'
				        finished();

						if ( emptyElement) {
							return Constants.SPECIAL;
						} else {
					        return Constants.ELEMENT_NAME;
						}

			        case 60: // '<'
				        character = in.read();
			        	finished();
			        	error = true;

			        	if ( emptyElement) {
							return Constants.SPECIAL;
						} else {
					        return Constants.ELEMENT_NAME;
						}

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
			        	if ( !hasName) {
			        		error = true;
			        	}
			        	
			        	skipWhitespace();
						finished();
				        return Constants.ELEMENT_NAME;

			        default:
			        	hasName = true;
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			hasName = false;
			emptyElement = false;
			hasPrefix = false;
		}
	}

	private class EndTagNameScanner extends Scanner {
		private boolean hasPrefix = false;
		private boolean emptyElement = false;
		private boolean hasName = false;
		private boolean whitespace = false;
		
		public int scan( XmlInputReader in) throws IOException {
			int character =  in.getLastChar();
		
			do {
			    switch( character) {
			        case -1: 
			            finished();
			            return Constants.ELEMENT_NAME;
	
			        case 58: // ':'
			        	if ( hasPrefix) {
			        		character = in.read();

			        		if ( !XMLChar.isNameStart( (char)character)) {
			        			error = true;
			        		}
			        		
			        		return Constants.SPECIAL;
			        	} else {
			        		hasName = false;
			        		hasPrefix = true;
			        		return Constants.ELEMENT_PREFIX;
			        	}

			        case 47: // '/'
						if ( emptyElement) {
				            character = in.read();
				            // falls through
						} else {
			            	emptyElement = true;
			            	return Constants.ELEMENT_NAME;
						}

			        case 62: // '>'
				        finished();

						if ( emptyElement) {
							return Constants.SPECIAL;
						} else {
					        return Constants.ELEMENT_NAME;
						}

			        case 60: // '<'
				        character = in.read();
			        	finished();
			        	error = true;

			        	if ( emptyElement) {
							return Constants.SPECIAL;
						} else {
					        return Constants.ELEMENT_NAME;
						}

			        case 32: // ' '
			        case 9:  // '\t'
			        case 10: // '\r'
			        case 13: // '\n'
			        	if ( !hasName) {
			        		error = true;
			        	}
			        	whitespace = true;
			        	skipWhitespace();
				        return Constants.ELEMENT_NAME;

			        default:
			        	if ( whitespace) {
			        		error = true;
			        	}
			        	hasName = true;
			            character = in.read();
			            break;
	
			    }
			} while( true);
		}
		
		public void reset() {
			super.reset();
			whitespace = false;
			hasName = false;
			emptyElement = false;
			hasPrefix = false;
		}
	}

	/**
	 * Scans an elements attribute 'xxx:xxxx="hhhh"' or 'xmlns:xxxx="hhhh"'.
	 */
	private class AttributeScanner extends Scanner {
		private final int NAME = 0;
		private final int VALUE = 1;
		private final int END = 2;

		private int mode = NAME;
		
		private boolean hasName = false;
		private boolean hasPrefix = false;
		private boolean firstTime = true;
		private boolean isNamespace = false;
		private boolean whiteSpaceDetected = false;
		
		public int scan( XmlInputReader in) throws IOException {

			int character =  in.getLastChar();
			
//			System.out.println("AttributeScanner.scan() ["+(char)character+"]");
		
			do {
//		        System.out.print( "["+(char)in.getLastChar()+"]");

		        if ( mode == NAME) {
				    switch( character) {
				        case -1: 
//				            System.err.println("Error ["+pos+"]: eof in attribute!");
				            finished();
				            return Constants.ATTRIBUTE_NAME;
		
				        case 120: // 'x'
				        	if ( firstTime) { // Still before a prefix has been established
								character = in.read();
								if ( character == 109) { // 'm'
									character = in.read();

									if ( character == 108) { // 'l'
										character = in.read();

										if ( character == 110) { // 'n'

											character = in.read();
											if ( character == 115) { // 's'
												skipWhitespace();
												character = in.getLastChar();
												
												if ( character == 58 || character == 61) { // ':' '='
													isNamespace = true;
												}
											}
										}
									}
								}
				        	} else {
					        	character = in.read();
				        	}

				        	hasName = true;
							break;

				        case 58: // ':'
				        	if ( !hasName) {
				        		error = true;
				        	}
				        	if ( hasPrefix) {
				        		character = in.read();

				        		if ( character == '<') {
						        	error = true;
						        	finished();
				        		} else if ( character == '>' || Character.isWhitespace( (char)character) || character == '=') {
						        	error = true;
				        		}
				        		if ( error) {
				        			return Constants.SPECIAL;
				        		}
				        		return Constants.SPECIAL;

				        	} else if ( isNamespace) {
					        	hasPrefix = true;
					        	return Constants.NAMESPACE_NAME;
				        	} else {
				        		hasPrefix = true;
				        		return Constants.ATTRIBUTE_PREFIX;
				        	}

				        case 60: // '<'
				        	error = true;
				        	finished();
						    return Constants.SPECIAL;

				        case 47: // '/'
						    character = in.read();
						    if ( hasName) {
						    	error = true;
						    }

							if ( character == 62) { // '>'
						        finished();
						        return Constants.SPECIAL;
						    }
						    break;

				        case 62: // '>'
				        	error = true;
					        finished();
					        return token;

				        case 61: // '='
							mode = VALUE;
							
							if ( !hasName) {
								error = true;
							}

							if ( isNamespace && hasPrefix) {
								return Constants.NAMESPACE_PREFIX;
							} else if ( isNamespace) {
								return Constants.NAMESPACE_NAME;
							} else {
								return Constants.ATTRIBUTE_NAME;
							}

				        case 32: // ' '
				        case 9:  // '\t'
				        case 10: // '\r'
				        case 13: // '\n'
				        	if ( hasName) {
				        		whiteSpaceDetected = true;
				        	}
				        	skipWhitespace();
				        	character = in.getLastChar();
					        break;

				        default:
				        	if ( whiteSpaceDetected) {
				        		error = true;
				        	}
				        	hasName = true;
				            character = in.read();
				            break;
				    }

					firstTime = false;
				} else if ( mode == VALUE) {

//					System.out.println("VALUE ["+(char)character+"]");

					switch( character) {
					    case -1: 
//					        System.err.println("Error ["+pos+"]: eof in attribute value!");
					        return -1;
		
				        case 60: // '<'
				        	error = true;
				        	finished();
						    return token;

				        case 61: // '='
				        	if ( !hasName) {
				        		error = true;
				        	}
						    character = in.read();
						    return Constants.SPECIAL;

					    case 39: // '''
					    case 34: // '"'
							scanString( character);

							if ( in.getLastChar() == '<') {
					        	finished();
					        } else {
					        	skipWhitespace();
					        }
														
							if ( isNamespace) {
								reset();
								return Constants.NAMESPACE_VALUE;
							} else {
								reset();
								return Constants.ATTRIBUTE_VALUE;
							}

					    case 62: // '>'
				        	error = true;
						    character = in.read();
					        finished();
					        return token;

				        case 32: // ' '
				        case 9:  // '\t'
				        case 10: // '\r'
				        case 13: // '\n'
					        character = in.read();
					        break;

				        case 47: // '/'
				        	error = true;
						    character = in.read();
						    break;

				        default:
				        	error = true;
					        character = in.read();
					        break;
					}

				} 
			} while( true);
		}
		
		public void reset() {
			super.reset();
			mode = NAME;
			
			hasPrefix = false;
			firstTime = true;
			isNamespace = false;
			hasName = false;
			whiteSpaceDetected = false;
		}
	}


	/**
	 * Abstract scanner class..
	 */
	abstract class Scanner {
		protected int token = -1;
		private boolean finished = false;
		
		public abstract int scan( XmlInputReader in) throws IOException;

		/**
		 * The scanner has finished scanning the information, 
		 * only a reset can change this.
		 */
		protected void finished() {
			finished = true;
		}

		/**
		 * returns whether this scanner has finished scanning all
		 * it was supposed to scan.
		 *
		 * @return true when the scanner is finished.
		 */
		public boolean isFinished() {
			return finished;
		}

		/**
		 * Resets all the variables to the start value.
		 */
		public void reset() {
			finished = false;
			token = -1;
		}

		/**
		 * returns the token value for the currently scanned text.
		 *
		 * @return the token value.
		 */
		public int getToken() {
			return token;
		}
	}

	public void cleanup() {
		finalize();
	}
	
	protected void finalize() {
		tagScanner = null;

		ATTRIBUTE_SCANNER = null;
		ELEMENT_END_TAG_SCANNER = null;
		ELEMENT_START_TAG_SCANNER = null;
		END_TAG_NAME_SCANNER = null;
		START_TAG_NAME_SCANNER = null;
		ENTITY_TAG_SCANNER = null;
		COMMENT_SCANNER = null;
		CDATA_SCANNER = null;
		TAG_SCANNER = null;
		
		in.cleanup();
		in	= null;
	}
}
