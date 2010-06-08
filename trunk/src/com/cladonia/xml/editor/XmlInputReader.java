/*
 * $Id: XmlInputReader.java,v 1.1 2004/03/25 18:44:46 edankert Exp $
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

import java.io.FilterReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * A Reader for XML input, which can handle escape characters.
 *
 * <p>
 * <b>Note:</b> The XML Editor package is based on the JavaEditorKit example as 
 * described in the article <i>'Customizing a Text Editor'</i> by <b>Timothy Prinzing</b>.
 * See: http://java.sun.com/products/jfc/tsc/articles/text/editor_kit/
 * </p>
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:44:46 $
 * @author Dogsbay
 */
public class XmlInputReader extends FilterReader {

    private static final int BUFFERLEN = 10240;

//    private int buffer[] = new int[10240];

    private XmlInputStream stream = null;

    long pos = 0;

    private long chpos = 0x100000000L;
    private int pushBack = -1;
    private int lastChar = -1;
    private int currentIndex = 0;
    private int numChars = 0;

	/**
	 * Constructs the new input stream reader out of the Xml
	 * input strem.
	 *
	 * @param inputstream the XML input stream.
	 */
    public XmlInputReader( XmlInputStream inputstream) throws UnsupportedEncodingException {
		super( new InputStreamReader( inputstream));
		
		stream = inputstream;
    }

	/**
	 * Sets the scan range of the reader.
	 *
	 * @param start the start position.
	 * @param end the end position.
	 */
	public void setRange( int start, int end) {
		stream.setRange( start, end);

		pos = 0;
		chpos = 0x100000000L;
		pushBack = -1;
		lastChar = -1;
		currentIndex = 0;
		numChars = 0;
	}

    /**
     * Reads one character from the stream and increases 
	 * the index.
     *
     * @return the character or -1 for an eof.
     */
    public int read() throws IOException {
		lastChar = readInternal();

		return lastChar;
    }
	
	/**
	 * Returns the last read character.
	 *
	 * @return the last read character or -1 for an eof.
	 */
	public int getLastChar() {
		return lastChar;
	}
	
	// The implementation of the read method.
    private int readInternal() throws IOException {
        int i;
        label0:
        {
            pos = chpos;
            chpos++;
            i = pushBack;

            if ( i == -1) {
//				if ( currentIndex >= numChars) {
//					numChars = in.read(buffer);
//					
//					if ( numChars == -1) {
//						i = -1;
//						break label0;
//					}
//	
//					currentIndex = 0;
//				}
//				i = buffer[ currentIndex++];
				i = stream.read();
            } else {
                pushBack = -1;
            }
        }

        switch(i) {
        case -2: 
            return 92;

        case 92: // '\\'
            if((i = getNextChar()) != 117)
            {
                pushBack = i != 92 ? i : -2;
                return 92;
            }
            for(chpos++; (i = getNextChar()) == 117; chpos++);
			
            int j = 0;
            for( int k = 0; k < 4;) {
                switch(i)
                {
                case 48: // '0'
                case 49: // '1'
                case 50: // '2'
                case 51: // '3'
                case 52: // '4'
                case 53: // '5'
                case 54: // '6'
                case 55: // '7'
                case 56: // '8'
                case 57: // '9'
                    j = ((j << 4) + i) - 48;
                    break;

                case 97: // 'a'
                case 98: // 'b'
                case 99: // 'c'
                case 100: // 'd'
                case 101: // 'e'
                case 102: // 'f'
                    j = ((j << 4) + 10 + i) - 97;
                    break;

                case 65: // 'A'
                case 66: // 'B'
                case 67: // 'C'
                case 68: // 'D'
                case 69: // 'E'
                case 70: // 'F'
                    j = ((j << 4) + 10 + i) - 65;
                    break;

                case 58: // ':'
                case 59: // ';'
                case 60: // '<'
                case 61: // '='
                case 62: // '>'
                case 63: // '?'
                case 64: // '@'
                case 71: // 'G'
                case 72: // 'H'
                case 73: // 'I'
                case 74: // 'J'
                case 75: // 'K'
                case 76: // 'L'
                case 77: // 'M'
                case 78: // 'N'
                case 79: // 'O'
                case 80: // 'P'
                case 81: // 'Q'
                case 82: // 'R'
                case 83: // 'S'
                case 84: // 'T'
                case 85: // 'U'
                case 86: // 'V'
                case 87: // 'W'
                case 88: // 'X'
                case 89: // 'Y'
                case 90: // 'Z'
                case 91: // '['
                case 92: // '\\'
                case 93: // ']'
                case 94: // '^'
                case 95: // '_'
                case 96: // '`'
                default:
//                    System.err.println("Error ["+pos+"]: invalid escape character!");
                    pushBack = i;
                    return j;

                }
                k++;
                chpos++;
                i = getNextChar();
            }

            pushBack = i;
            switch(j)
            {
            case 10: // '\n'
                chpos += 0x100000000L;
                return 10;

            case 13: // '\r'
                if((i = getNextChar()) != 10)
                    pushBack = i;
                else
                    chpos++;
                chpos += 0x100000000L;
                return 10;

            }
            return j;

        case 10: // '\n'
            chpos += 0x100000000L;
            return 10;

        case 13: // '\r'
            if((i = getNextChar()) != 10)
                pushBack = i;
            else
                chpos++;
            chpos += 0x100000000L;
            return 10;
        }

        return i;
    }

	// Returns the next character from the stream
	private int getNextChar() throws IOException {
//        if( currentIndex >= numChars) {
//            numChars = in.read(buffer);
//            
//			if ( numChars == -1) {
//                return -1;
//            }
//            
//			currentIndex = 0;
//        }

//        return buffer[currentIndex++];
        return stream.read();
    }
	
    public void cleanup() {
    	finalize();
    }
    
    protected void finalize() {
//    	buffer = null;
    	stream = null;
    }
}
