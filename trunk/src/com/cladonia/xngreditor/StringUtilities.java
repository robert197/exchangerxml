/*
 * $Id: StringUtilities.java,v 1.6 2004/11/03 17:25:52 edankert Exp $
 *
 * Copyright (C) 2002-2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A String utility class.
 *
 * @version $Revision: 1.6 $, $Date: 2004/11/03 17:25:52 $
 * @author Dogsbay
 */
public class StringUtilities {
	private static final byte pattern = (byte)0x0D;
	private static final char[][] table = { 
											{ 'a', '3' },
											{ 'b', 'x' },
											{ 'c', '0' },
											{ 'd', 'G' },
											{ 'e', '4' },
											{ 'f', 'D' },
											{ 'g', '8' },
											{ 'h', 'w' },
											{ 'i', 'y' },
											{ 'j', '2' },
											{ 'k', 'C' },
											{ 'l', 'f' },
											{ 'm', 'F' },
											{ 'n', 'j' },
											{ 'o', 'z' },
											{ 'p', 'H' },
											{ 'q', 'b' },
											{ 'r', 'n' },
											{ 's', 'M' },
											{ 't', 'v' },
											{ 'u', 'T' },
											{ 'v', 'B' },
											{ 'w', 'k' },
											{ 'x', 'e' },
											{ 'y', 'U' },
											{ 'z', '1' },

											{ '0', 'O' },
											{ '1', 'p' },
											{ '2', 'I' },
											{ '3', 'r' },
											{ '4', 'K' },
											{ '5', 't' },
											{ '6', 'i' },
											{ '7', 'q' },
											{ '8', 'N' },
											{ '9', 'P' },
											
											{ 'A', 'V' },
											{ 'B', '6' },
											{ 'C', 'Z' },
											{ 'D', 'c' },
											{ 'E', 'd' },
											{ 'F', 'h' },
											{ 'G', 'E' },
											{ 'H', 'o' },
											{ 'I', 'g' },
											{ 'J', 'l' },
											{ 'K', '5' },
											{ 'L', 'a' },
											{ 'M', 'm' },
											{ 'N', 'W' },
											{ 'O', 'J' },
											{ 'P', 'Y' },
											{ 'Q', 'X' },
											{ 'R', 'R' },
											{ 'S', 'L' },
											{ 'T', 'u' },
											{ 'U', '9' },
											{ 'V', 's' },
											{ 'W', 'A' },
											{ 'X', '7' },
											{ 'Y', 'Q' },
											{ 'Z', 'S' }
										};

	public static String decrypt( String string) {
		char[] chars = string.toCharArray();
		
		for ( int i = 0; i < chars.length; i++) {
			chars[i] = decrypt( chars[i]);
		}
		
		String result =  new String( chars);

//		System.out.println( "StringUtilities.decrypt( "+string+") ["+result+"]");
		return result;
	}
	
	private static char decrypt( char ch) {
		for ( int i = 0; i < table.length; i++) {
			if ( table[i][1] == ch) {
				return table[i][0];
			}
		}
		
		return ch;
	}

	private static char encrypt( char ch) {
		for ( int i = 0; i < table.length; i++) {
			if ( table[i][0] == ch) {
				return table[i][1];
			}
		}
		
		return ch;
	}

	public static String encrypt( String string) {
		if ( string != null) {
			return encrypt( string.toCharArray());
		}

		return null;
	}

	public static String encrypt( char[] chars) {
		for ( int i = 0; i < chars.length; i++) {
			chars[i] = encrypt( chars[i]);
		}

		String result =  new String( chars);
//		System.out.println( "StringUtilities.encrypt( "+new String( chars)+") ["+result+"]");
		return result;
	}

	/**
	 * Returns true when the string is all whitespace or null.
	 *
	 * @param string the string to test.
	 *
	 * @return true when the string is empty.
	 */
	public static boolean isEmpty( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}
	
	public static String replace( String source, String search, String replacement) {
		System.out.println( "replace( "+source+", "+search+", "+replacement+")");
		String result = null;
		
		search = "\\Q"+prepareNonRegularExpression( search)+"\\E";
		replacement = prepareNonRegularExpressionReplacement( replacement);

		Pattern pattern = Pattern.compile( search, Pattern.CASE_INSENSITIVE);

		try {
			Matcher matcher = pattern.matcher( source);
			result = matcher.replaceAll( replacement);
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public static String prepareNonRegularExpression( String regexp) {
		StringBuffer result = new StringBuffer( regexp);
		
		int index = result.indexOf( "\\E");
		
		while ( index != -1) {
			result.replace( index, index+2, "\\E\\\\E\\Q");
			index = result.indexOf( "\\E", index+7);
		}
		
		// Last character is a '\', make sure to escape otherwise it will escape 
		// the \E and we don't see the end of the non-regular expression.
		if ( result.charAt( result.length()-1) == '\\') {
			result.append( "E\\\\\\Q");
		}

		return result.toString();
	}
	
	public static String prepareNonRegularExpressionReplacement( String regexp) {
		StringBuffer result = new StringBuffer( regexp);
		
		int index = result.indexOf( "\\");
		
		while ( index != -1) {
			result.replace( index, index+1, "\\\\");
			index = result.indexOf( "\\", index+2);
		}
		
		index = result.indexOf( "$");
		
		while ( index != -1) {
			result.replace( index, index+1, "\\$");
			index = result.indexOf( "$", index+2);
		}

//		// Last character is a '\', make sure to escape otherwise it will escape 
//		// the \E and we don't see the end of the non-regular expression.
//		if ( result.length() > 0 && result.charAt( result.length()-1) == '\\') {
//			result.append( "E\\\\\\Q");
//		}

		return result.toString();
	}

	public static String stripWhitespace( String value) {
		if ( value != null && value.length() > 0) {
			StringBuffer result = new StringBuffer();
			char[] chars = value.toCharArray();
			
			for ( int i = 0; i < chars.length; i++) {
				if ( !Character.isWhitespace( chars[i])) {
					result.append( (char)chars[i]);
				}
			}

			return result.toString();
		}
		
		return value;
	}
}
