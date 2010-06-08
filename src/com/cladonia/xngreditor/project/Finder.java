/*
 * $Id: Finder.java,v 1.4 2005/09/07 16:20:51 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.StringUtilities;

/**
 * The base node class.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/09/07 16:20:51 $
 * @author Dogsbay
 */
public class Finder {
	private static final boolean DEBUG = false;
	
	public static Vector find( URL url, String search, boolean regExp, boolean matchCase, boolean wholeWord) {
		if (DEBUG) System.out.println( "Finder.find( "+url+", "+search+", "+regExp+", "+matchCase+")");

		return find( url, createPattern( search, regExp, matchCase, wholeWord));
	}

	public static Vector find( URL url, Pattern pattern) {
		if (DEBUG) System.out.println( "Finder.find( "+url+", "+pattern+")");
		Vector matches = new Vector();
		
		try {
			String text = XMLUtilities.getText( url, new XMLUtilities.XMLDeclaration());
			LineNumberReader reader = new LineNumberReader( new StringReader( text));

			String line = reader.readLine();
			
			while ( line != null) {
				Matcher matcher = pattern.matcher( line);
				
				while ( matcher.find()) {
					matches.addElement( new Match( url, reader.getLineNumber(), matcher.start(), matcher.end(), line));
				}
				
				line = reader.readLine();
			}
		} catch (IOException e) {
			//System.err.println( "Error: Could not read file '"+url+"'!");
//			matches.addElement( new Match( url, -1, -1, -1, "ERROR: Could Not Read File."));
//			e.printStackTrace(); // don't worry about it, just continue...
		}
		
		return matches;
	}

	private static Pattern createPattern( String search, boolean regExp, boolean matchCase, boolean wholeWord) {
		if (DEBUG) System.out.println( "Finder.createPattern( "+search+", "+regExp+", "+matchCase+")");

		String regularSearch = search;

		// Maybe pre-compile the matcher for a list of documents???
		Pattern pattern = null;
		
		if ( !regExp) {
			regularSearch = "\\Q"+StringUtilities.prepareNonRegularExpression( search)+"\\E";
		}

		if ( wholeWord) { 
			regularSearch = "\\b"+regularSearch+"\\b";
		}

		if ( !matchCase) {
			pattern = Pattern.compile( regularSearch, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile( regularSearch);
		}

		return pattern;
	}
}