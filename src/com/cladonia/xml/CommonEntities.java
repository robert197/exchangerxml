/*
 * $Id: CommonEntities.java,v 1.1 2004/03/25 18:41:32 edankert Exp $
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
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;


/**
 * Utilities for reading and writing of XML documents.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:32 $
 * @author Dogsbay
 */
public class CommonEntities {
	private static Vector orderedCharacters = null;
	private static Vector orderedEntities = null;
	private static Hashtable entitiesByCharacter = null;
	private static Hashtable commonEntities = null;
	private static String dtdString = null;
	
	public static String asDTD() {
		if ( dtdString == null) {
			StringBuffer buffer = new StringBuffer();
			
			Hashtable table = getCommonEntities();
			Enumeration keys = table.keys();
			
			while ( keys.hasMoreElements()) {
				String key = (String)keys.nextElement();
				
				buffer.append( "<!ENTITY ");
				buffer.append( key);
				buffer.append( " \"");
				buffer.append( (String)table.get( key));
				buffer.append( "\">");
			}
			
			
			dtdString = buffer.toString();
		}
		
		return dtdString;
	}

	public static Hashtable getCommonEntities() {
		if ( commonEntities == null) {
			commonEntities = new Hashtable();
			
			for ( int i = 0; i < ISO_ENITITIES.length; i++) {
				String[][] entities = (String[][])ISO_ENITITIES[i];
				
				for ( int j = 0; j < entities.length; j++) {
					commonEntities.put( entities[j][0], entities[j][1]);
				}
			}
		}
		
		return commonEntities;
	}
	
	public static Vector getEntityNamesStartingWith( String start) {
		Vector result = new Vector();
		Vector entities = getOrderedEntityNames();
		boolean found = false;

		for ( int i = 0; i < entities.size(); i++) {
			String name = (String)entities.elementAt(i);

			if ( name.startsWith( start)) {
				result.addElement( name);
				found = true;
			} else { // have gone past the entities...
				if ( found) {
					break;
				}
			}
		}
		
		return result;
	}
	
	public static String getEntityName( char character) {
		getOrderedCharacters();
		return (String)entitiesByCharacter.get( new Character( character));
	}

	public static Vector getOrderedCharacters() {
		if ( orderedCharacters == null) {
			orderedCharacters = new Vector();
			entitiesByCharacter = new Hashtable();

			for ( int i = 0; i < ISO_ENITITIES.length; i++) {
				String[][] entities = (String[][])ISO_ENITITIES[i];
				
				for ( int j = 0; j < entities.length; j++) {
					String entityName = entities[j][0];
					char character = getChar( entityName);
					boolean append = true;
					
					for ( int k = 0; k < orderedCharacters.size(); k++) {
						char orderedCharacter = ((Character)orderedCharacters.elementAt(k)).charValue();

						// Compare alphabeticaly
						if ( character < orderedCharacter) {
							orderedCharacters.insertElementAt( new Character( character), k);
							entitiesByCharacter.put( new Character( character), entityName);
							append = false;
							break;
						} else if ( character == orderedCharacter) {
							append = false;
							break;
						} 
					}
				
					if ( append) {
						entitiesByCharacter.put( new Character( character), entityName);
						orderedCharacters.addElement( new Character( character));
					}
				}
			}
		}
		
		return orderedCharacters;
	}

	public static Vector getOrderedEntityNames() {
		if ( orderedEntities == null) {
			orderedEntities = new Vector();

			for ( int i = 0; i < ISO_ENITITIES.length; i++) {
				String[][] entities = (String[][])ISO_ENITITIES[i];
				
				for ( int j = 0; j < entities.length; j++) {
					String name = entities[j][0];

					boolean append = true;
					
					for ( int k = 0; k < orderedEntities.size(); k++) {
						int comp = name.compareTo( (String)orderedEntities.elementAt( k));

						// Compare alphabeticaly
						if ( comp < 0) {
							orderedEntities.insertElementAt( name, k);
							append = false;
							break;
						} else if ( comp == 0) {
							append = false;
							break;
						} 
					}
				
					if ( append) {
						orderedEntities.addElement( name);
					}
				}
			}
		}
		
		return orderedEntities;
	}

	public static char getChar( String name) {
		char value = (char)0;
		
		value = convert( getValue( name));
		
		return value;
	}

	public static String getValue( String name) {
		String value = null;
		
		if ( "gt".equals( name)) {
			value = "&#062;";
		} else if ( "lt".equals( name)) {
			value = "&#060;";
		} else if ( "quot".equals( name)) {
			value = "&#034;";
		} else if ( "amp".equals( name)) {
			value = "&#038;";
		} else if ( "apos".equals( name)) {
			value = "&#039;";
		} else {
			value = (String)getCommonEntities().get( name);
		}
		
		return value;
	}

	public static boolean isCommonEntity( String name) {
		boolean result = false;
		
		if ( "gt".equals( name)) {
			result = true;
		} else if ( "lt".equals( name)) {
			result = true;
		} else if ( "quot".equals( name)) {
			result = true;
		} else if ( "amp".equals( name)) {
			result = true;
		} else if ( getCommonEntities().get( name) != null) {
			result = true;
		}

		return result;
	}
	
	public static char convertCharacterEntity( String value) {
		return convert( value);
	}

	private static char convert( String value) {
		char result = (char)-1;
		 
		if ( value != null && value.length() > 3) {
			String code = value.substring( 2, value.length()-1);
			
			if ( code.startsWith( "x") || code.startsWith( "X")) {
				try {
					Integer val = Integer.decode( "0"+code);
					result = (char)val.intValue();
				} catch ( NumberFormatException e) {
					e.printStackTrace();
				}
			} else {
				try {
					result = (char)Integer.parseInt( code);
				} catch ( NumberFormatException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}

	private static final String ISO_LATIN1_ENITITIES[][] =  { // isolat1.ent
	 { "nbsp",   "&#160;" }, // no-break space = non-breaking space,
	 { "iexcl",  "&#161;" }, // inverted exclamation mark,
	 { "cent",   "&#162;" }, // cent sign,
	 { "pound",  "&#163;" }, // pound sign,
	 { "curren", "&#164;" }, // currency sign
	 { "yen",    "&#165;" }, // yen sign = yuan sign
	 { "brvbar", "&#166;" }, // broken bar = broken vertical bar,
	 { "sect",   "&#167;" }, // section sign,
	 { "uml",    "&#168;" }, // diaeresis = spacing diaeresis,
	 { "copy",   "&#169;" }, // copyright sign,
	 { "ordf",   "&#170;" }, // feminine ordinal indicator, 
	 { "laquo",  "&#171;" }, // left-pointing double angle quotation mark = left pointing guillemet,
	 { "not",    "&#172;" }, // not sign,
	 { "shy",    "&#173;" }, // soft hyphen = discretionary hyphen,
	 { "reg",    "&#174;" }, // registered sign = registered trade mark sign,
	 { "macr",   "&#175;" }, // macron = spacing macron = overline
	 { "deg",    "&#176;" }, // degree sign, 
	 { "plusmn", "&#177;" }, // plus-minus sign = plus-or-minus sign,
	 { "sup2",   "&#178;" }, // superscript two = superscript digit two = squared,
	 { "sup3",   "&#179;" }, // superscript three = superscript digit three = cubed, 
	 { "acute",  "&#180;" }, // acute accent = spacing acute,
	 { "micro",  "&#181;" }, // micro sign,
	 { "para",   "&#182;" }, // pilcrow sign = paragraph sign,
	 { "middot", "&#183;" }, // middle dot = Georgian comma = Greek middle dot,
	 { "cedil",  "&#184;" }, // cedilla = spacing cedilla,
	 { "supl",   "&#185;" }, // superscript one = superscript digit one,
	 { "ordm",   "&#186;" }, // masculine ordinal indicator,
	 { "raquo",  "&#187;" }, // right-pointing double angle quotation mark = right pointing guillemet,
	 { "frac14", "&#188;" }, // vulgar fraction one quarter = fraction one quarter,
	 { "frac12", "&#189;" }, // vulgar fraction one half = fraction one half,
	 { "frac34", "&#190;" }, // vulgar fraction three quarters = fraction three quarters,
	 { "iquest", "&#191;" }, // inverted question mark = turned question mark,
	 { "Agrave", "&#192;" }, // latin capital letter A with grave = latin capital letter A grave,
	 { "Aacute", "&#193;" }, // latin capital letter A with acute,
	 { "Acirc",  "&#194;" }, // latin capital letter A with circumflex,
	 { "Atilde", "&#195;" }, // latin capital letter A with tilde,
	 { "Auml",   "&#196;" }, // latin capital letter A with diaeresis,
	 { "Aring",  "&#197;" }, // latin capital letter A with ring above = latin capital letter A ring,
	 { "AElig",  "&#198;" }, // latin capital letter AE = latin capital ligature AE,
	 { "Ccedil", "&#199;" }, // latin capital letter C with cedilla,
	 { "Egrave", "&#200;" }, // latin capital letter E with grave,
	 { "Eacute", "&#201;" }, // latin capital letter E with acute,
	 { "Ecirc",  "&#202;" }, // latin capital letter E with circumflex,
	 { "Euml",   "&#203;" }, // latin capital letter E with diaeresis,
	 { "Igrave", "&#204;" }, // latin capital letter I with grave,
	 { "Iacute", "&#205;" }, // latin capital letter I with acute,
	 { "Icirc",  "&#206;" }, // latin capital letter I with circumflex,
	 { "Iuml",   "&#207;" }, // latin capital letter I with diaeresis,
	 { "ETH",    "&#208;" }, // latin capital letter ETH,
	 { "Ntilde", "&#209;" }, // latin capital letter N with tilde,
	 { "Ograve", "&#210;" }, // latin capital letter O with grave,
	 { "Oacute", "&#211;" }, // latin capital letter O with acute,
	 { "Ocirc",  "&#212;" }, // latin capital letter O with circumflex,
	 { "Otilde", "&#213;" }, // latin capital letter O with tilde,
	 { "Ouml",   "&#214;" }, // latin capital letter O with diaeresis,
	 { "times",  "&#215;" }, // multiplication sign,
	 { "Oslash", "&#216;" }, // latin capital letter O with stroke = latin capital letter O slash,
	 { "Ugrave", "&#217;" }, // latin capital letter U with grave,
	 { "Uacute", "&#218;" }, // latin capital letter U with acute,
	 { "Ucirc",  "&#219;" }, // latin capital letter U with circumflex,
	 { "Uuml",   "&#220;" }, // latin capital letter U with diaeresis,
	 { "Yacute", "&#221;" }, // latin capital letter Y with acute,
	 { "THORN",  "&#222;" }, // latin capital letter THORN,
	 { "szlig",  "&#223;" }, // latin small letter sharp s = ess-zed,
	 { "agrave", "&#224;" }, // latin small letter a with grave = latin capital letter A grave,
	 { "aacute", "&#225;" }, // latin small letter a with acute,
	 { "acirc",  "&#226;" }, // latin small letter a with circumflex,
	 { "atilde", "&#227;" }, // latin small letter a with tilde,
	 { "auml",   "&#228;" }, // latin small letter a with diaeresis,
	 { "aring",  "&#229;" }, // latin small letter a with ring above = latin capital letter A ring,
	 { "aElig",  "&#230;" }, // latin small letter ae = latin small ligature ae,
	 { "ccedil", "&#231;" }, // latin small letter c with cedilla,
	 { "egrave", "&#232;" }, // latin small letter e with grave,
	 { "eacute", "&#233;" }, // latin small letter e with acute,
	 { "ecirc",  "&#234;" }, // latin small letter e with circumflex,
	 { "euml",   "&#235;" }, // latin small letter e with diaeresis,
	 { "igrave", "&#236;" }, // latin small letter i with grave,
	 { "iacute", "&#237;" }, // latin small letter i with acute,
	 { "icirc",  "&#238;" }, // latin small letter i with circumflex,
	 { "iuml",   "&#239;" }, // latin small letter i with diaeresis,
	 { "eth",    "&#240;" }, // latin small letter etc,
	 { "ntilde", "&#241;" }, // latin small letter n with tilde,
	 { "ograve", "&#242;" }, // latin small letter o with grave,
	 { "oacute", "&#243;" }, // latin small letter o with acute,
	 { "ocirc",  "&#244;" }, // latin small letter o with circumflex,
	 { "otilde", "&#245;" }, // latin small letter o with tilde,
	 { "ouml",   "&#246;" }, // latin small letter o with diaeresis,
	 { "divide", "&#247;" }, // division sign,
	 { "oslash", "&#248;" }, // latin small letter o with stroke = latin small letter O slash,
	 { "ugrave", "&#249;" }, // latin small letter u with grave,
	 { "uacute", "&#250;" }, // latin small letter u with acute,
	 { "ucirc",  "&#251;" }, // latin small letter u with circumflex,
	 { "uuml",   "&#252;" }, // latin small letter u with diaeresis,
	 { "yacute", "&#253;" }, // latin small letter y with acute,
	 { "thorn",  "&#254;" }, // latin small letter thorn,
	 { "yuml",   "&#255;" } }; // latin small letter y with diaeresis,

	private static final String ISO_LATIN2_ENITITIES[][] =  { // isolat2.ent
		 { "abreve"  ,"&#x00103;" }, // small a, breve
		 { "Abreve"  ,"&#x00102;" }, // capital A, breve -->
		 { "amacr"   ,"&#x00101;" }, // small a, macron -->
		 { "Amacr"   ,"&#x00100;" }, // capital A, macron -->
		 { "aogon"   ,"&#x00105;" }, // small a, ogonek -->
		 { "Aogon"   ,"&#x00104;" }, // capital A, ogonek -->
		 { "cacute"  ,"&#x00107;" }, // small c, acute accent -->
		 { "Cacute"  ,"&#x00106;" }, // capital C, acute accent -->
		 { "ccaron"  ,"&#x0010D;" }, // small c, caron -->
		 { "Ccaron"  ,"&#x0010C;" }, // capital C, caron -->
		 { "ccirc"   ,"&#x00109;" }, // small c, circumflex accent -->
		 { "Ccirc"   ,"&#x00108;" }, // capital C, circumflex accent -->
		 { "cdot"    ,"&#x0010B;" }, // small c, dot above -->
		 { "Cdot"    ,"&#x0010A;" }, // capital C, dot above -->
		 { "dcaron"  ,"&#x0010F;" }, // small d, caron -->
		 { "Dcaron"  ,"&#x0010E;" }, // capital D, caron -->
		 { "dstrok"  ,"&#x00111;" }, // small d, stroke -->
		 { "Dstrok"  ,"&#x00110;" }, // capital D, stroke -->
		 { "ecaron"  ,"&#x0011B;" }, // small e, caron -->
		 { "Ecaron"  ,"&#x0011A;" }, // capital E, caron -->
		 { "edot"    ,"&#x00117;" }, // small e, dot above -->
		 { "Edot"    ,"&#x00116;" }, // capital E, dot above -->
		 { "emacr"   ,"&#x00113;" }, // small e, macron -->
		 { "Emacr"   ,"&#x00112;" }, // capital E, macron -->
		 { "eng"     ,"&#x0014B;" }, // small eng, Lapp -->
		 { "ENG"     ,"&#x0014A;" }, // capital ENG, Lapp -->
		 { "eogon"   ,"&#x00119;" }, // small e, ogonek -->
		 { "Eogon"   ,"&#x00118;" }, // capital E, ogonek -->
		 { "gacute"  ,"&#x001F5;" }, // small g, acute accent -->
		 { "gbreve"  ,"&#x0011F;" }, // small g, breve -->
		 { "Gbreve"  ,"&#x0011E;" }, // capital G, breve -->
		 { "Gcedil"  ,"&#x00122;" }, // capital G, cedilla -->
		 { "gcirc"   ,"&#x0011D;" }, // small g, circumflex accent -->
		 { "Gcirc"   ,"&#x0011C;" }, // capital G, circumflex accent -->
		 { "gdot"    ,"&#x00121;" }, // small g, dot above -->
		 { "Gdot"    ,"&#x00120;" }, // capital G, dot above -->
		 { "hcirc"   ,"&#x00125;" }, // small h, circumflex accent -->
		 { "Hcirc"   ,"&#x00124;" }, // capital H, circumflex accent -->
		 { "hstrok"  ,"&#x00127;" }, // small h, stroke -->
		 { "Hstrok"  ,"&#x00126;" }, // capital H, stroke -->
		 { "Idot"    ,"&#x00130;" }, // capital I, dot above -->
		 { "ijlig"   ,"&#x00133;" }, // small ij ligature -->
		 { "IJlig"   ,"&#x00132;" }, // capital IJ ligature -->
		 { "imacr"   ,"&#x0012B;" }, // small i, macron -->
		 { "Imacr"   ,"&#x0012A;" }, // capital I, macron -->
		 { "inodot"  ,"&#x00131;" }, // small i without dot -->
		 { "iogon"   ,"&#x0012F;" }, // small i, ogonek -->
		 { "Iogon"   ,"&#x0012E;" }, // capital I, ogonek -->
		 { "itilde"  ,"&#x00129;" }, // small i, tilde -->
		 { "Itilde"  ,"&#x00128;" }, // capital I, tilde -->
		 { "jcirc"   ,"&#x00135;" }, // small j, circumflex accent -->
		 { "Jcirc"   ,"&#x00134;" }, // capital J, circumflex accent -->
		 { "kcedil"  ,"&#x00137;" }, // small k, cedilla -->
		 { "Kcedil"  ,"&#x00136;" }, // capital K, cedilla -->
		 { "kgreen"  ,"&#x00138;" }, // small k, Greenlandic -->
		 { "lacute"  ,"&#x0013A;" }, // small l, acute accent -->
		 { "Lacute"  ,"&#x00139;" }, // capital L, acute accent -->
		 { "lcaron"  ,"&#x0013E;" }, // small l, caron -->
		 { "Lcaron"  ,"&#x0013D;" }, // capital L, caron -->
		 { "lcedil"  ,"&#x0013C;" }, // small l, cedilla -->
		 { "Lcedil"  ,"&#x0013B;" }, // capital L, cedilla -->
		 { "lmidot"  ,"&#x00140;" }, // small l, middle dot -->
		 { "Lmidot"  ,"&#x0013F;" }, // capital L, middle dot -->
		 { "lstrok"  ,"&#x00142;" }, // small l, stroke -->
		 { "Lstrok"  ,"&#x00141;" }, // capital L, stroke -->
		 { "Nacute"  ,"&#x00143;" }, // capital N, acute accent -->
		 { "napos"   ,"&#x00149;" }, // small n, apostrophe -->
		 { "ncaron"  ,"&#x00148;" }, // small n, caron -->
		 { "Ncaron"  ,"&#x00147;" }, // capital N, caron -->
		 { "ncedil"  ,"&#x00146;" }, // small n, cedilla -->
		 { "Ncedil"  ,"&#x00145;" }, // capital N, cedilla -->
		 { "odblac"  ,"&#x00151;" }, // small o, double acute accent -->
		 { "Odblac"  ,"&#x00150;" }, // capital O, double acute accent -->
		 { "oelig"   ,"&#x00153;" }, // small oe ligature -->
		 { "OElig"   ,"&#x00152;" }, // capital OE ligature -->
		 { "omacr"   ,"&#x0014D;" }, // small o, macron -->
		 { "Omacr"   ,"&#x0014C;" }, // capital O, macron -->
		 { "racute"  ,"&#x00155;" }, // small r, acute accent -->
		 { "Racute"  ,"&#x00154;" }, // capital R, acute accent -->
		 { "rcaron"  ,"&#x00159;" }, // small r, caron -->
		 { "Rcaron"  ,"&#x00158;" }, // capital R, caron -->
		 { "rcedil"  ,"&#x00157;" }, // small r, cedilla -->
		 { "Rcedil"  ,"&#x00156;" }, // capital R, cedilla -->
		 { "sacute"  ,"&#x0015B;" }, // small s, acute accent -->
		 { "Sacute"  ,"&#x0015A;" }, // capital S, acute accent -->
		 { "scaron"  ,"&#x00161;" }, // small s, caron -->
		 { "Scaron"  ,"&#x00160;" }, // capital S, caron -->
		 { "scedil"  ,"&#x0015F;" }, // small s, cedilla -->
		 { "Scedil"  ,"&#x0015E;" }, // capital S, cedilla -->
		 { "scirc"   ,"&#x0015D;" }, // small s, circumflex accent -->
		 { "Scirc"   ,"&#x0015C;" }, // capital S, circumflex accent -->
		 { "tcaron"  ,"&#x00165;" }, // small t, caron -->
		 { "Tcaron"  ,"&#x00164;" }, // capital T, caron -->
		 { "tcedil"  ,"&#x00163;" }, // small t, cedilla -->
		 { "Tcedil"  ,"&#x00162;" }, // capital T, cedilla -->
		 { "tstrok"  ,"&#x00167;" }, // small t, stroke -->
		 { "Tstrok"  ,"&#x00166;" }, // capital T, stroke -->
		 { "ubreve"  ,"&#x0016D;" }, // small u, breve -->
		 { "Ubreve"  ,"&#x0016C;" }, // capital U, breve -->
		 { "udblac"  ,"&#x00171;" }, // small u, double acute accent -->
		 { "Udblac"  ,"&#x00170;" }, // capital U, double acute accent -->
		 { "umacr"   ,"&#x0016B;" }, // small u, macron -->
		 { "Umacr"   ,"&#x0016A;" }, // capital U, macron -->
		 { "uogon"   ,"&#x00173;" }, // small u, ogonek -->
		 { "Uogon"   ,"&#x00172;" }, // capital U, ogonek -->
		 { "uring"   ,"&#x0016F;" }, // small u, ring -->
		 { "Uring"   ,"&#x0016E;" }, // capital U, ring -->
		 { "utilde"  ,"&#x00169;" }, // small u, tilde -->
		 { "Utilde"  ,"&#x00168;" }, // capital U, tilde -->
		 { "wcirc"   ,"&#x00175;" }, // small w, circumflex accent -->
		 { "Wcirc"   ,"&#x00174;" }, // capital W, circumflex accent -->
		 { "ycirc"   ,"&#x00177;" }, // small y, circumflex accent -->
		 { "Ycirc"   ,"&#x00176;" }, // capital Y, circumflex accent -->
		 { "Yuml"    ,"&#x00178;" }, // capital Y, dieresis or umlaut mark -->
		 { "zacute"  ,"&#x0017A;" }, // small z, acute accent -->
		 { "Zacute"  ,"&#x00179;" }, // capital Z, acute accent -->
		 { "zcaron"  ,"&#x0017E;" }, // small z, caron -->
		 { "Zcaron"  ,"&#x0017D;" }, // capital Z, caron -->
		 { "zdot"    ,"&#x0017C;" }, // small z, dot above -->
		 { "Zdot"    ,"&#x0017B;" } }; // capital Z, dot above -->	
		
	private static final String ISO_NUMERIC_ENITITIES[][] =  { // isonum.ent
		 { "amp",    "&#x00026;" }, // ampersand
		 { "apos",   "&#x00027;" }, //=apostrophe -->
		 { "ast",    "&#x0002A;" }, ///ast B: =asterisk -->
		 { "brvbar", "&#x000A6;" }, //=broken (vertical) bar -->
		 { "bsol",   "&#x0005C;" }, ///backslash =reverse solidus -->
		 { "cent",   "&#x000A2;" }, //=cent sign -->
		 { "colon",  "&#x0003A;" }, ///colon P: -->
		 { "comma",  "&#x0002C;" }, //P: =comma -->
		 { "commat", "&#x00040;" }, //=commercial at -->
		 { "copy",   "&#x000A9;" }, //=copyright sign -->
		 { "curren", "&#x000A4;" }, //=general currency sign -->
		 { "darr",   "&#x02193;" }, ///downarrow A: =downward arrow -->
		 { "deg",    "&#x000B0;" }, //=degree sign -->
		 { "divide", "&#x000F7;" }, ///div B: =divide sign -->
		 { "dollar", "&#x00024;" }, //=dollar sign -->
		 { "equals", "&#x0003D;" }, //=equals sign R: -->
		 { "excl",   "&#x00021;" }, //=exclamation mark -->
		 { "frac12", "&#x000BD;" }, //=fraction one-half -->
		 { "frac14", "&#x000BC;" }, //=fraction one-quarter -->
		 { "frac18", "&#x0215B;" }, //=fraction one-eighth -->
		 { "frac34", "&#x000BE;" }, //=fraction three-quarters -->
		 { "frac38", "&#x0215C;" }, //=fraction three-eighths -->
		 { "frac58", "&#x0215D;" }, //=fraction five-eighths -->
		 { "frac78", "&#x0215E;" }, //=fraction seven-eighths -->
		 { "gt",     "&#x0003E;" }, //=greater-than sign R: -->
		 { "half",   "&#x000BD;" }, //=fraction one-half -->
		 { "horbar", "&#x02015;" }, //=horizontal bar -->
		 { "hyphen", "&#x02010;" }, //=hyphen -->
		 { "iexcl",  "&#x000A1;" }, //=inverted exclamation mark -->
		 { "iquest", "&#x000BF;" }, //=inverted question mark -->
		 { "laquo",  "&#x000AB;" }, //=angle quotation mark, left -->
		 { "larr",   "&#x02190;" }, ///leftarrow /gets A: =leftward arrow -->
		 { "lcub",   "&#x0007B;" }, ///lbrace O: =left curly bracket -->
		 { "ldquo",  "&#x0201C;" }, //=double quotation mark, left -->
		 { "lowbar", "&#x0005F;" }, //=low line -->
		 { "lpar",   "&#x00028;" }, //O: =left parenthesis -->
		 { "lsqb",   "&#x0005B;" }, ///lbrack O: =left square bracket -->
		 { "lsquo",  "&#x02018;" }, //=single quotation mark, left -->
		 { "lt",     "&#x00026;" }, //=less-than sign R: -->
		 { "micro",  "&#x000B5;" }, //=micro sign -->
		 { "middot", "&#x000B7;" }, ///centerdot B: =middle dot -->
		 { "nbsp",   "&#x000A0;" }, //=no break (required) space -->
		 { "not",    "&#x000AC;" }, ///neg /lnot =not sign -->
		 { "num",    "&#x00023;" }, //=number sign -->
		 { "ohm",    "&#x02126;" }, //=ohm sign -->
		 { "ordf",   "&#x000AA;" }, //=ordinal indicator, feminine -->
		 { "ordm",   "&#x000BA;" }, //=ordinal indicator, masculine -->
		 { "para",   "&#x000B6;" }, //=pilcrow (paragraph sign) -->
		 { "percnt", "&#x00025;" }, //=percent sign -->
		 { "period", "&#x0002E;" }, //=full stop, period -->
		 { "plus",   "&#x0002B;" }, //=plus sign B: -->
		 { "plusmn", "&#x000B1;" }, ///pm B: =plus-or-minus sign -->
		 { "pound",  "&#x000A3;" }, //=pound sign -->
		 { "quest",  "&#x0003F;" }, //=question mark -->
		 { "quot",   "&#x00022;" }, //=quotation mark -->
		 { "raquo",  "&#x000BB;" }, //=angle quotation mark, right -->
		 { "rarr",   "&#x02192;" }, ///rightarrow /to A: =rightward arrow -->
		 { "rcub",   "&#x0007D;" }, ///rbrace C: =right curly bracket -->
		 { "rdquo",  "&#x0201D;" }, //=double quotation mark, right -->
		 { "reg",    "&#x000AE;" }, ///circledR =registered sign -->
		 { "rpar",   "&#x00029;" }, //C: =right parenthesis -->
		 { "rsqb",   "&#x0005D;" }, ///rbrack C: =right square bracket -->
		 { "rsquo",  "&#x02019;" }, //=single quotation mark, right -->
		 { "sect",   "&#x000A7;" }, //=section sign -->
		 { "semi",   "&#x0003B;" }, //=semicolon P: -->
		 { "shy",    "&#x000AD;" }, //=soft hyphen -->
		 { "sol",    "&#x0002F;" }, //=solidus -->
		 { "sung",   "&#x0266A;" }, //=music note (sung text sign) -->
		 { "sup1",   "&#x000B9;" }, //=superscript one -->
		 { "sup2",   "&#x000B2;" }, //=superscript two -->
		 { "sup3",   "&#x000B3;" }, //=superscript three -->
		 { "times",  "&#x000D7;" }, ///times B: =multiply sign -->
		 { "trade",  "&#x02122;" }, //=trade mark sign -->
		 { "uarr",   "&#x02191;" }, ///uparrow A: =upward arrow -->
		 { "verbar", "&#x0007C;" }, ///vert =vertical bar -->
		 { "yen",    "&#x000A5;" } }; ///yen =yen sign -->

	private static final String ISO_TECHNICAL_ENITITIES[][] =  { // isotech.ent
		 { "acd",      "&#x0223F;" }, // ac current -->
		 { "aleph",    "&#x02135;" }, // /aleph aleph, Hebrew -->
		 { "and",      "&#x02227;" }, // /wedge /land B: logical and -->
		 { "And",      "&#x02A53;" }, // dbl logical and -->
		 { "andand",   "&#x02A55;" }, // two logical and -->
		 { "andd",     "&#x02A5C;" }, // and, horizontal dash -->
		 { "andslope", "&#x02A58;" }, // sloping large and -->
		 { "andv",     "&#x02A5A;" }, // and with middle stem -->
		 { "angrt",    "&#x0221F;" }, // right (90 degree) angle -->
		 { "angsph",   "&#x02222;" }, // /sphericalangle angle-spherical -->
		 { "angst",    "&#x0212B;" }, // Angstrom capital A, ring -->
		 { "ap",       "&#x02248;" }, // /approx R: approximate -->
		 { "apacir",   "&#x02A6F;" }, // approximate, circumflex accent -->
		 { "awconint", "&#x02233;" }, // contour integral, anti-clockwise -->
		 { "awint",    "&#x02A11;" }, // anti clock-wise integration -->
		 { "becaus",   "&#x02235;" }, // /because R: because -->
		 { "bernou",   "&#x0212C;" }, // Bernoulli function (script capital B)  -->
		 { "bne",      "&#x0003D;" }, // reverse not equal -->
		 { "bnequiv",  "&#x02261;" }, // reverse not equivalent -->
		 { "bnot",     "&#x02310;" }, // reverse not -->
		 { "bNot",     "&#x02AED;" }, // reverse not with two horizontal strokes -->
		 { "bottom",   "&#x022A5;" }, // /bot bottom -->
		 { "cap",      "&#x02229;" }, // /cap B: intersection -->
		 { "Cconint",  "&#x02230;" }, // triple contour integral operator -->
		 { "cirfnint", "&#x02A10;" }, // circulation function -->
		 { "compfn",   "&#x02218;" }, // /circ B: composite function (small circle) -->
		 { "cong",     "&#x02245;" }, // /cong R: congruent with -->
		 { "conint",   "&#x0222E;" }, // /oint L: contour integral operator -->
		 { "Conint",   "&#x0222F;" }, // double contour integral operator -->
		 { "ctdot",    "&#x022EF;" }, // /cdots, three dots, centered -->
		 { "cup",      "&#x0222A;" }, // /cup B: union or logical sum -->
		 { "cwconint", "&#x02232;" }, // contour integral, clockwise -->
		 { "cwint",    "&#x02231;" }, // clockwise integral -->
		 { "cylcty",   "&#x0232D;" }, // cylindricity -->
		 { "disin",    "&#x022F2;" }, // set membership, long horizontal stroke -->
		 { "Dot",      "&#x000A8;" }, // dieresis or umlaut mark -->
		 { "DotDot",   "&#x020DC;" }, // four dots above -->
		 { "dsol",     "&#x029F6;" }, // solidus, bar above -->
		 { "dtdot",    "&#x022F1;" }, // /ddots, three dots, descending -->
		 { "dwangle",  "&#x029A6;" }, // large downward pointing angle -->
		 { "epar",     "&#x022D5;" }, // parallel, equal; equal or parallel -->
		 { "eparsl",   "&#x029E3;" }, // parallel, slanted, equal; homothetically congruent to -->
		 { "equiv",    "&#x02261;" }, // /equiv R: identical with -->
		 { "eqvparsl", "&#x029E5;" }, // equivalent, equal; congruent and parallel -->
		 { "exist",    "&#x02203;" }, // /exists at least one exists -->
		 { "fnof",     "&#x00192;" }, // function of (italic small f) -->
		 { "forall",   "&#x02200;" }, // /forall for all -->
		 { "fpartint", "&#x02A0D;" }, // finite part integral -->
		 { "ge",       "&#x02265;" }, // /geq /ge R: greater-than-or-equal -->
		 { "hamilt",   "&#x0210B;" }, // Hamiltonian (script capital H)  -->
		 { "iff",      "&#x021D4;" }, // /iff if and only if  -->
		 { "iinfin",   "&#x029DC;" }, // infinity sign, incomplete -->
		 { "imped",    "&#x1D543;" }, // impedance -->
		 { "infin",    "&#x0221E;" }, // /infty infinity -->
		 { "int",      "&#x0222B;" }, // /int L: integral operator -->
		 { "Int",      "&#x0222C;" }, // double integral operator -->
		 { "intlarhk", "&#x02A17;" }, // integral, left arrow with hook -->
		 { "isin",     "&#x02208;" }, // /in R: set membership  -->
		 { "isindot",  "&#x022F5;" }, // set membership, dot above -->
		 { "isinE",    "&#x022F9;" }, // set membership, two horizontal strokes -->
		 { "isins",    "&#x022F4;" }, // set membership, vertical bar on horizontal stroke -->
		 { "isinsv",   "&#x022F3;" }, // large set membership, vertical bar on horizontal stroke -->
		 { "isinv",    "&#x02208;" }, // set membership, variant -->
		 { "lagran",   "&#x02112;" }, // Lagrangian (script capital L)  -->
		 { "lang",     "&#x02329;" }, // /langle O: left angle bracket -->
		 { "Lang",     "&#x0300A;" }, // left angle bracket, double -->
		 { "lArr",     "&#x021D0;" }, // /Leftarrow A: is implied by -->
		 { "lbbrk",    "&#x03014;" }, // left broken bracket -->
		 { "le",       "&#x02264;" }, // /leq /le R: less-than-or-equal -->
		 { "loang",    "&#x0F558;" }, // left open angular bracket -->
		 { "lobrk",    "&#x0301A;" }, // left open bracket -->
		 { "lopar",    "&#x03018;" }, // left open parenthesis -->
		 { "lowast",   "&#x02217;" }, // low asterisk -->
		 { "minus",    "&#x02212;" }, // B: minus sign -->
		 { "mnplus",   "&#x02213;" }, // /mp B: minus-or-plus sign -->
		 { "nabla",    "&#x02207;" }, // /nabla del, Hamilton operator -->
		 { "ne",       "&#x02260;" }, // /ne /neq R: not equal -->
		 { "nedot",    "&#x02260;" }, // not equal, dot -->
		 { "nhpar",    "&#x02AF2;" }, // not, horizontal, parallel -->
		 { "ni",       "&#x0220B;" }, // /ni /owns R: contains -->
		 { "nis",      "&#x022FC;" }, // contains, vertical bar on horizontal stroke -->
		 { "nisd",     "&#x022FA;" }, // contains, long horizontal stroke -->
		 { "niv",      "&#x0220B;" }, // contains, variant -->
		 { "Not",      "&#x02AEC;" }, // not with two horizontal strokes -->
		 { "notin",    "&#x02209;" }, // /notin N: negated set membership -->
		 { "notindot", "&#x022F6;" }, // negated set membership, dot above -->
		 { "notinva",  "&#x02209;" }, // negated set membership, variant -->
		 { "notinvb",  "&#x022F7;" }, // negated set membership, variant -->
		 { "notinvc",  "&#x022F6;" }, // negated set membership, variant -->
		 { "notni",    "&#x0220C;" }, // negated contains -->
		 { "notniva",  "&#x0220C;" }, // negated contains, variant -->
		 { "notnivb",  "&#x022FE;" }, // contains, variant -->
		 { "notnivc",  "&#x022FD;" }, // contains, variant -->
		 { "nparsl",   "&#x02225;" }, // not parallel, slanted -->
		 { "npart",    "&#x02202;" }, // not partial differential -->
		 { "npolint",  "&#x02A14;" }, // line integration, not including the pole -->
		 { "nvinfin",  "&#x029DE;" }, // not, vert, infinity -->
		 { "olcross",  "&#x029BB;" }, // circle, cross -->
		 { "or",       "&#x02228;" }, // /vee /lor B: logical or -->
		 { "Or",       "&#x02A54;" }, // dbl logical or -->
		 { "ord",      "&#x02A5D;" }, // or, horizontal dash -->
		 { "order",    "&#x02134;" }, // order of (script small o)  -->
		 { "oror",     "&#x02A56;" }, // two logical or -->
		 { "orslope",  "&#x02A57;" }, // sloping large or -->
		 { "orv",      "&#x02A5B;" }, // or with middle stem -->
		 { "par",      "&#x02225;" }, // /parallel R: parallel -->
		 { "parsl",    "&#x02225;" }, // parallel, slanted -->
		 { "part",     "&#x02202;" }, // /partial partial differential -->
		 { "permil",   "&#x02030;" }, // per thousand -->
		 { "perp",     "&#x022A5;" }, // /perp R: perpendicular -->
		 { "pertenk",  "&#x02031;" }, // per 10 thousand -->
		 { "phmmat",   "&#x02133;" }, // physics M-matrix (script capital M)  -->
		 { "pointint", "&#x02A15;" }, // integral around a point operator -->
		 { "prime",    "&#x02032;" }, // /prime prime or minute -->
		 { "Prime",    "&#x02033;" }, // double prime or second -->
		 { "profalar", "&#x0232E;" }, // all-around profile -->
		 { "profline", "&#x02312;" }, // profile of a line -->
		 { "profsurf", "&#x02313;" }, // profile of a surface -->
		 { "prop",     "&#x0221D;" }, // /propto R: is proportional to -->
		 { "qint",     "&#x02A0C;" }, // /iiiint quadruple integral operator -->
		 { "qprime",   "&#x02057;" }, // quadruple prime -->
		 { "quatint",  "&#x02A16;" }, // quaternion integral operator -->
		 { "radic",    "&#x0221A;" }, // /surd radical -->
		 { "rang",     "&#x0232A;" }, // /rangle C: right angle bracket -->
		 { "Rang",     "&#x0300B;" }, // right angle bracket, double -->
		 { "rArr",     "&#x021D2;" }, // /Rightarrow A: implies -->
		 { "rbbrk",    "&#x03015;" }, // right broken bracket -->
		 { "roang",    "&#x0F559;" }, // right open angular bracket -->
		 { "robrk",    "&#x0301B;" }, // right open bracket -->
		 { "ropar",    "&#x03019;" }, // right open parenthesis -->
		 { "rppolint", "&#x02A12;" }, // line integration, rectangular path around pole -->
		 { "scpolint", "&#x02A13;" }, // line integration, semi-circular path around pole -->
		 { "sim",      "&#x0223C;" }, // /sim R: similar -->
		 { "simdot",   "&#x02A6A;" }, // similar, dot -->
		 { "sime",     "&#x02243;" }, // /simeq R: similar, equals -->
		 { "smeparsl", "&#x029E4;" }, // similar, parallel, slanted, equal -->
		 { "square",   "&#x025A1;" }, // /square, square -->
		 { "squarf",   "&#x025AA;" }, // /blacksquare, square, filled  -->
		 { "sub",      "&#x02282;" }, // /subset R: subset or is implied by -->
		 { "sube",     "&#x02286;" }, // /subseteq R: subset, equals -->
		 { "sup",      "&#x02283;" }, // /supset R: superset or implies -->
		 { "supe",     "&#x02287;" }, // /supseteq R: superset, equals -->
		 { "tdot",     "&#x020DB;" }, // three dots above -->
		 { "there4",   "&#x02234;" }, // /therefore R: therefore -->
		 { "tint",     "&#x0222D;" }, // /iiint triple integral operator -->
		 { "top",      "&#x022A4;" }, // /top top -->
		 { "topbot",   "&#x02336;" }, // top and bottom -->
		 { "topcir",   "&#x02AF1;" }, // top, circle below -->
		 { "tprime",   "&#x02034;" }, // triple prime -->
		 { "utdot",    "&#x022F0;" }, // three dots, ascending -->
		 { "uwangle",  "&#x029A7;" }, // large upward pointing angle -->
		 { "vangrt",   "&#x022BE;" }, // right angle, variant -->
		 { "veeeq",    "&#x0225A;" }, // logical or, equals -->
		 { "Verbar",   "&#x02016;" }, // /Vert dbl vertical bar -->
		 { "wedgeq",   "&#x02259;" }, // /wedgeq R: corresponds to (wedge, equals) -->
		 { "xnis",     "&#x022FB;" } }; // large contains, vertical bar on horizontal stroke -->

	private static final String ISO_PUBLIC_ENITITIES[][] =  { // isopub.ent
		 { "blank",  "&#x02423;" }, // significant blank symbol -->
		 { "blk12",  "&#x02592;" }, // =50% shaded block -->
		 { "blk14",  "&#x02591;" }, // =25% shaded block -->
		 { "blk34",  "&#x02593;" }, // =75% shaded block -->
		 { "block",  "&#x02588;" }, // =full block -->
		 { "bull",   "&#x02022;" }, // /bullet B: =round bullet, filled -->
		 { "caret",  "&#x02041;" }, // =caret (insertion mark) -->
		 { "check",  "&#x02713;" }, // /checkmark =tick, check mark -->
		 { "cir",    "&#x025CB;" }, // /circ B: =circle, open -->
		 { "clubs",  "&#x02663;" }, // /clubsuit =club suit symbol  -->
		 { "copysr", "&#x02117;" }, // =sound recording copyright sign -->
		 { "cross",  "&#x02717;" }, // =ballot cross -->
		 { "dagger", "&#x02020;" }, // /dagger B: =dagger -->
		 { "Dagger", "&#x02021;" }, // /ddagger B: =double dagger -->
		 { "dash",   "&#x02010;" }, // =hyphen (true graphic) -->
		 { "diams",  "&#x02666;" }, // /diamondsuit =diamond suit symbol  -->
		 { "dlcrop", "&#x0230D;" }, // downward left crop mark  -->
		 { "drcrop", "&#x0230C;" }, // downward right crop mark  -->
		 { "dtri",   "&#x025BF;" }, // /triangledown =down triangle, open -->
		 { "dtrif",  "&#x025BE;" }, // /blacktriangledown =dn tri, filled -->
		 { "emsp",   "&#x02003;" }, // =em space -->
		 { "emsp13", "&#x02004;" }, // =1/3-em space -->
		 { "emsp14", "&#x02005;" }, // =1/4-em space -->
		 { "ensp",   "&#x02002;" }, // =en space (1/2-em) -->
		 { "female", "&#x02640;" }, // =female symbol -->
		 { "ffilig", "&#x0FB03;" }, // small ffi ligature -->
		 { "fflig",  "&#x0FB00;" }, // small ff ligature -->
		 { "ffllig", "&#x0FB04;" }, // small ffl ligature -->
		 { "filig",  "&#x0FB01;" }, // small fi ligature -->
		 { "flat",   "&#x0266D;" }, // /flat =musical flat -->
		 { "fllig",  "&#x0FB02;" }, // small fl ligature -->
		 { "frac13", "&#x02153;" }, // =fraction one-third -->
		 { "frac15", "&#x02155;" }, // =fraction one-fifth -->
		 { "frac16", "&#x02159;" }, // =fraction one-sixth -->
		 { "frac23", "&#x02154;" }, // =fraction two-thirds -->
		 { "frac25", "&#x02156;" }, // =fraction two-fifths -->
		 { "frac35", "&#x02157;" }, // =fraction three-fifths -->
		 { "frac45", "&#x02158;" }, // =fraction four-fifths -->
		 { "frac56", "&#x0215A;" }, // =fraction five-sixths -->
		 { "hairsp", "&#x0200A;" }, // =hair space -->
		 { "hellip", "&#x02026;" }, // =ellipsis (horizontal) -->
		 { "hybull", "&#x02043;" }, // rectangle, filled (hyphen bullet) -->
		 { "incare", "&#x02105;" }, // =in-care-of symbol -->
		 { "ldquor", "&#x0201E;" }, // =rising dbl quote, left (low) -->
		 { "lhblk",  "&#x02584;" }, // =lower half block -->
		 { "loz",    "&#x025CA;" }, // /lozenge - lozenge or total mark -->
		 { "lozf",   "&#x029EB;" }, // /blacklozenge - lozenge, filled -->
		 { "lsquor", "&#x0201A;" }, // =rising single quote, left (low) -->
		 { "ltri",   "&#x025C3;" }, // /triangleleft B: l triangle, open -->
		 { "ltrif",  "&#x025C2;" }, // /blacktriangleleft R: =l tri, filled -->
		 { "male",   "&#x02642;" }, // =male symbol -->
		 { "malt",   "&#x02720;" }, // /maltese =maltese cross -->
		 { "marker", "&#x025AE;" }, // =histogram marker -->
		 { "mdash",  "&#x02014;" }, // =em dash  -->
		 { "mldr",   "&#x02026;" }, // em leader -->
		 { "natur",  "&#x0266E;" }, // /natural - music natural -->
		 { "ndash",  "&#x02013;" }, // =en dash -->
		 { "nldr",   "&#x02025;" }, // =double baseline dot (en leader) -->
		 { "numsp",  "&#x02007;" }, // =digit space (width of a number) -->
		 { "phone",  "&#x0260E;" }, // =telephone symbol  -->
		 { "puncsp", "&#x02008;" }, // =punctuation space (width of comma) -->
		 { "rdquor", "&#x0201D;" }, // rising dbl quote, right (high) -->
		 { "rect",   "&#x025AD;" }, // =rectangle, open -->
		 { "rsquor", "&#x02019;" }, // rising single quote, right (high) -->
		 { "rtri",   "&#x025B9;" }, // /triangleright B: r triangle, open -->
		 { "rtrif",  "&#x025B8;" }, // /blacktriangleright R: =r tri, filled -->
		 { "rx",     "&#x0211E;" }, // pharmaceutical prescription (Rx) -->
		 { "sext",   "&#x02736;" }, // sextile (6-pointed star) -->
		 { "sharp",  "&#x0266F;" }, // /sharp =musical sharp -->
		 { "spades", "&#x02660;" }, // /spadesuit =spades suit symbol  -->
		 { "squ",    "&#x025A1;" }, // =square, open -->
		 { "squf",   "&#x025AA;" }, // /blacksquare =sq bullet, filled -->
		 { "star",   "&#x022C6;" }, // =star, open -->
		 { "starf",  "&#x02605;" }, // /bigstar - star, filled  -->
		 { "target", "&#x02316;" }, // register mark or target -->
		 { "telrec", "&#x02315;" }, // =telephone recorder symbol -->
		 { "thinsp", "&#x02009;" }, // =thin space (1/6-em) -->
		 { "uhblk",  "&#x02580;" }, // =upper half block -->
		 { "ulcrop", "&#x0230F;" }, // upward left crop mark  -->
		 { "urcrop", "&#x0230E;" }, // upward right crop mark  -->
		 { "utri",   "&#x025B5;" }, // /triangle =up triangle, open -->
		 { "utrif",  "&#x025B4;" }, // /blacktriangle =up tri, filled -->
		 { "vellip", "&#x022EE;" } }; // vertical ellipsis -->

	private static final String ISO_CYRILLIC1_ENITITIES[][] =  { // isocyr1.ent
		 { "acy",    "&#x00430;" }, // =small a, Cyrillic -->
		 { "Acy",    "&#x00410;" }, // =capital A, Cyrillic -->
		 { "bcy",    "&#x00431;" }, // =small be, Cyrillic -->
		 { "Bcy",    "&#x00411;" }, // =capital BE, Cyrillic -->
		 { "chcy",   "&#x00447;" }, // =small che, Cyrillic -->
		 { "CHcy",   "&#x00427;" }, // =capital CHE, Cyrillic -->
		 { "dcy",    "&#x00434;" }, // =small de, Cyrillic -->
		 { "Dcy",    "&#x00414;" }, // =capital DE, Cyrillic -->
		 { "ecy",    "&#x0044D;" }, // =small e, Cyrillic -->
		 { "Ecy",    "&#x0042D;" }, // =capital E, Cyrillic -->
		 { "fcy",    "&#x00444;" }, // =small ef, Cyrillic -->
		 { "Fcy",    "&#x00424;" }, // =capital EF, Cyrillic -->
		 { "gcy",    "&#x00433;" }, // =small ghe, Cyrillic -->
		 { "Gcy",    "&#x00413;" }, // =capital GHE, Cyrillic -->
		 { "hardcy", "&#x0044A;" }, // =small hard sign, Cyrillic -->
		 { "HARDcy", "&#x0042A;" }, // =capital HARD sign, Cyrillic -->
		 { "icy",    "&#x00438;" }, // =small i, Cyrillic -->
		 { "Icy",    "&#x00418;" }, // =capital I, Cyrillic -->
		 { "iecy",   "&#x00435;" }, // =small ie, Cyrillic -->
		 { "IEcy",   "&#x00415;" }, // =capital IE, Cyrillic -->
		 { "iocy",   "&#x00451;" }, // =small io, Russian -->
		 { "IOcy",   "&#x00401;" }, // =capital IO, Russian -->
		 { "jcy",    "&#x00439;" }, // =small short i, Cyrillic -->
		 { "Jcy",    "&#x00419;" }, // =capital short I, Cyrillic -->
		 { "kcy",    "&#x0043A;" }, // =small ka, Cyrillic -->
		 { "Kcy",    "&#x0041A;" }, // =capital KA, Cyrillic -->
		 { "khcy",   "&#x00445;" }, // =small ha, Cyrillic -->
		 { "KHcy",   "&#x00425;" }, // =capital HA, Cyrillic -->
		 { "lcy",    "&#x0043B;" }, // =small el, Cyrillic -->
		 { "Lcy",    "&#x0041B;" }, // =capital EL, Cyrillic -->
		 { "mcy",    "&#x0043C;" }, // =small em, Cyrillic -->
		 { "Mcy",    "&#x0041C;" }, // =capital EM, Cyrillic -->
		 { "ncy",    "&#x0043D;" }, // =small en, Cyrillic -->
		 { "Ncy",    "&#x0041D;" }, // =capital EN, Cyrillic -->
		 { "numero", "&#x02116;" }, // =numero sign -->
		 { "ocy",    "&#x0043E;" }, // =small o, Cyrillic -->
		 { "Ocy",    "&#x0041E;" }, // =capital O, Cyrillic -->
		 { "pcy",    "&#x0043F;" }, // =small pe, Cyrillic -->
		 { "Pcy",    "&#x0041F;" }, // =capital PE, Cyrillic -->
		 { "rcy",    "&#x00440;" }, // =small er, Cyrillic -->
		 { "Rcy",    "&#x00420;" }, // =capital ER, Cyrillic -->
		 { "scy",    "&#x00441;" }, // =small es, Cyrillic -->
		 { "Scy",    "&#x00421;" }, // =capital ES, Cyrillic -->
		 { "shchcy", "&#x00449;" }, // =small shcha, Cyrillic -->
		 { "SHCHcy", "&#x00429;" }, // =capital SHCHA, Cyrillic -->
		 { "shcy",   "&#x00448;" }, // =small sha, Cyrillic -->
		 { "SHcy",   "&#x00428;" }, // =capital SHA, Cyrillic -->
		 { "softcy", "&#x0044C;" }, // =small soft sign, Cyrillic -->
		 { "SOFTcy", "&#x0042C;" }, // =capital SOFT sign, Cyrillic -->
		 { "tcy",    "&#x00442;" }, // =small te, Cyrillic -->
		 { "Tcy",    "&#x00422;" }, // =capital TE, Cyrillic -->
		 { "tscy",   "&#x00446;" }, // =small tse, Cyrillic -->
		 { "TScy",   "&#x00426;" }, // =capital TSE, Cyrillic -->
		 { "ucy",    "&#x00443;" }, // =small u, Cyrillic -->
		 { "Ucy",    "&#x00423;" }, // =capital U, Cyrillic -->
		 { "vcy",    "&#x00432;" }, // =small ve, Cyrillic -->
		 { "Vcy",    "&#x00412;" }, // =capital VE, Cyrillic -->
		 { "yacy",   "&#x0044F;" }, // =small ya, Cyrillic -->
		 { "YAcy",   "&#x0042F;" }, // =capital YA, Cyrillic -->
		 { "ycy",    "&#x0044B;" }, // =small yeru, Cyrillic -->
		 { "Ycy",    "&#x0042B;" }, // =capital YERU, Cyrillic -->
		 { "yucy",   "&#x0044E;" }, // =small yu, Cyrillic -->
		 { "YUcy",   "&#x0042E;" }, // =capital YU, Cyrillic -->
		 { "zcy",    "&#x00437;" }, // =small ze, Cyrillic -->
		 { "Zcy",    "&#x00417;" }, // =capital ZE, Cyrillic -->
		 { "zhcy",   "&#x00436;" }, // =small zhe, Cyrillic -->
		 { "ZHcy",   "&#x00416;" } }; // =capital ZHE, Cyrillic -->	

	private static final String ISO_CYRILLIC2_ENITITIES[][] =  { // isocyr2.ent
		 { "djcy",   "&#x00452;" }, // =small dje, Serbian -->
		 { "DJcy",   "&#x00402;" }, // =capital DJE, Serbian -->
		 { "dscy",   "&#x00455;" }, // =small dse, Macedonian -->
		 { "DScy",   "&#x00405;" }, // =capital DSE, Macedonian -->
		 { "dzcy",   "&#x0045F;" }, // =small dze, Serbian -->
		 { "DZcy",   "&#x0040F;" }, // =capital dze, Serbian -->
		 { "gjcy",   "&#x00453;" }, // =small gje, Macedonian -->
		 { "GJcy",   "&#x00403;" }, // =capital GJE Macedonian -->
		 { "iukcy",  "&#x00456;" }, // =small i, Ukrainian -->
		 { "Iukcy",  "&#x00406;" }, // =capital I, Ukrainian -->
		 { "jsercy", "&#x00458;" }, // =small je, Serbian -->
		 { "Jsercy", "&#x00408;" }, // =capital JE, Serbian -->
		 { "jukcy",  "&#x00454;" }, // =small je, Ukrainian -->
		 { "Jukcy",  "&#x00404;" }, // =capital JE, Ukrainian -->
		 { "kjcy",   "&#x0045C;" }, // =small kje Macedonian -->
		 { "KJcy",   "&#x0040C;" }, // =capital KJE, Macedonian -->
		 { "ljcy",   "&#x00459;" }, // =small lje, Serbian -->
		 { "LJcy",   "&#x00409;" }, // =capital LJE, Serbian -->
		 { "njcy",   "&#x0045A;" }, // =small nje, Serbian -->
		 { "NJcy",   "&#x0040A;" }, // =capital NJE, Serbian -->
		 { "tshcy",  "&#x0045B;" }, // =small tshe, Serbian -->
		 { "TSHcy",  "&#x0040B;" }, // =capital TSHE, Serbian -->
		 { "ubrcy",  "&#x0045E;" }, // =small u, Byelorussian -->
		 { "Ubrcy",  "&#x0040E;" }, // =capital U, Byelorussian -->
		 { "yicy",   "&#x00457;" }, // =small yi, Ukrainian -->
		 { "YIcy",   "&#x00407;" } }; // =capital YI, Ukrainian -->
		 
	private static final String ISO_GREEK1_ENITITIES[][] =  { // isogrk1.ent
		 { "agr",  "&#945;" }, // U03B1 =small alpha, Greek -->
		 { "Agr",  "&#913;" }, // U0391 =capital Alpha, Greek -->
		 { "bgr",  "&#946;" }, // U03B2 =small beta, Greek -->
		 { "Bgr",  "&#914;" }, // U0392 =capital Beta, Greek -->
		 { "dgr",  "&#948;" }, // U03B4 =small delta, Greek -->
		 { "Dgr",  "&#916;" }, // U0394 =capital Delta, Greek -->
		 { "eegr", "&#951;" }, // U03B7 =small eta, Greek -->
		 { "egr",  "&#949;" }, // U03B5 =small epsilon, Greek -->
		 { "EEgr", "&#919;" }, // U0397 =capital Eta, Greek -->
		 { "Egr",  "&#917;" }, // U0395 =capital Epsilon, Greek -->
		 { "ggr",  "&#947;" }, // U03B3 =small gamma, Greek -->
		 { "Ggr",  "&#915;" }, // U0393 =capital Gamma, Greek -->
		 { "igr",  "&#953;" }, // U03B9 =small iota, Greek -->
		 { "Igr",  "&#921;" }, // U0399 =capital Iota, Greek -->
		 { "kgr",  "&#954;" }, // U03BA =small kappa, Greek -->
		 { "khgr", "&#967;" }, // U03C7 =small chi, Greek -->
		 { "Kgr",  "&#922;" }, // U039A =capital Kappa, Greek -->
		 { "KHgr", "&#935;" }, // U03A7 =capital Chi, Greek -->
		 { "lgr",  "&#955;" }, // U03BB =small lambda, Greek -->
		 { "Lgr",  "&#923;" }, // U039B =capital Lambda, Greek -->
		 { "mgr",  "&#956;" }, // U03BC =small mu, Greek -->
		 { "Mgr",  "&#924;" }, // U039C =capital Mu, Greek -->
		 { "ngr",  "&#957;" }, // U03BD =small nu, Greek -->
		 { "Ngr",  "&#925;" }, // U039D =capital Nu, Greek -->
		 { "ogr",  "&#959;" }, // U03BF =small omicron, Greek -->
		 { "ohgr", "&#969;" }, // U03C9 =small omega, Greek -->
		 { "Ogr",  "&#927;" }, // U039F =capital Omicron, Greek -->
		 { "OHgr", "&#937;" }, // U03A9 =capital Omega, Greek -->
		 { "pgr",  "&#960;" }, // U03C0 =small pi, Greek -->
		 { "phgr", "&#966;" }, // U03C6 =small phi, Greek -->
		 { "psgr", "&#968;" }, // U03C8 =small psi, Greek -->
		 { "Pgr",  "&#928;" }, // U03A0 =capital Pi, Greek -->
		 { "PHgr", "&#934;" }, // U03A6 =capital Phi, Greek -->
		 { "PSgr", "&#936;" }, // U03A8 =capital Psi, Greek -->
		 { "rgr",  "&#961;" }, // U03C1 =small rho, Greek -->
		 { "Rgr",  "&#929;" }, // U03A1 =capital Rho, Greek -->
		 { "sfgr", "&#962;" }, // U03C2 =final small sigma, Greek -->
		 { "sgr",  "&#963;" }, // U03C3 =small sigma, Greek -->
		 { "Sgr",  "&#931;" }, // U03A3 =capital Sigma, Greek -->
		 { "tgr",  "&#964;" }, // U03C4 =small tau, Greek -->
		 { "thgr", "&#952;" }, // U03B8 =small theta, Greek -->
		 { "Tgr",  "&#932;" }, // U03A4 =capital Tau, Greek -->
		 { "THgr", "&#920;" }, // U0398 =capital Theta, Greek -->
		 { "ugr",  "&#965;" }, // U03C5 =small upsilon, Greek -->
		 { "Ugr",  "&#933;" }, // U03A5 =capital Upsilon, Greek -->
		 { "xgr",  "&#958;" }, // U03BE =small xi, Greek -->
		 { "Xgr",  "&#926;" }, // U039E =capital Xi, Greek -->
		 { "zgr",  "&#950;" }, // U03B6 =small zeta, Greek -->
		 { "Zgr",  "&#918;" } }; // U0396 =capital Zeta, Greek -->

	private static final String ISO_GREEK2_ENITITIES[][] =  { // isogrk2.ent
		 { "aacgr",      "&#940;" }, // U03AC =small alpha, accent, Greek -->
		 { "Aacgr",      "&#902;" }, // U0386 =capital Alpha, accent, Greek -->
		 { "eacgr",      "&#941;" }, // U03AD =small epsilon, accent, Greek -->
		 { "eeacgr",     "&#942;" }, // U03AE =small eta, accent, Greek -->
		 { "Eacgr",      "&#904;" }, // U0388 =capital Epsilon, accent, Greek -->
		 { "EEacgr",     "&#905;" }, // U0389 =capital Eta, accent, Greek -->
		 { "iacgr",      "&#943;" }, // U03AF =small iota, accent, Greek -->
		 { "idiagr",     "&#912;" }, // U0390 =small iota, dieresis, accent, Greek -->
		 { "idigr",      "&#970;" }, // U03CA =small iota, dieresis, Greek -->
		 { "Iacgr",      "&#906;" }, // U038A =capital Iota, accent, Greek -->
		 { "Idigr",      "&#938;" }, // U03AA =capital Iota, dieresis, Greek -->
		 { "oacgr",      "&#972;" }, // U03CC =small omicron, accent, Greek -->
		 { "ohacgr",     "&#974;" }, // U03CE =small omega, accent, Greek -->
		 { "Oacgr",      "&#908;" }, // U038C =capital Omicron, accent, Greek -->
		 { "OHacgr",     "&#911;" }, // U038F =capital Omega, accent, Greek -->
		 { "uacgr",      "&#973;" }, // U03CD =small upsilon, accent, Greek -->
		 { "udiagr",     "&#944;" }, // U03B0 =small upsilon, dieresis, accent, Greek -->
		 { "udigr",      "&#971;" }, // U03CB =small upsilon, dieresis, Greek -->
		 { "Uacgr",      "&#910;" }, // U038E =capital Upsilon, accent, Greek -->
		 { "Udigr",      "&#939;" } }; // U03AB =capital Upsilon, dieresis, Greek -->

	private static final String ISO_GREEK3_ENITITIES[][] =  { // isogrk3.ent
		 { "alpha",  "&#x003B1;" }, // /alpha small alpha, Greek -->
		 { "beta",   "&#x003B2;" }, // /beta small beta, Greek -->
		 { "chi",    "&#x003C7;" }, // /chi small chi, Greek -->
		 { "delta",  "&#x003B4;" }, // /delta small delta, Greek -->
		 { "Delta",  "&#x00394;" }, // /Delta capital Delta, Greek -->
		 { "epsi",   "&#x003B5;" }, // /straightepsilon, small epsilon, Greek -->
		 { "epsiv",  "&#x0025B;" }, // /varepsilon -->
		 { "eta",    "&#x003B7;" }, // /eta small eta, Greek -->
		 { "gamma",  "&#x003B3;" }, // /gamma small gamma, Greek -->
		 { "Gamma",  "&#x00393;" }, // /Gamma capital Gamma, Greek -->
		 { "gammad", "&#x003DC;" }, // /digamma -->
		 { "Gammad", "&#x003DC;" }, // capital digamma -->
		 { "iota",   "&#x003B9;" }, // /iota small iota, Greek -->
		 { "kappa",  "&#x003BA;" }, // /kappa small kappa, Greek -->
		 { "kappav", "&#x003F0;" }, // /varkappa -->
		 { "lambda", "&#x003BB;" }, // /lambda small lambda, Greek -->
		 { "Lambda", "&#x0039B;" }, // /Lambda capital Lambda, Greek -->
		 { "mu",     "&#x003BC;" }, // /mu small mu, Greek -->
		 { "nu",     "&#x003BD;" }, // /nu small nu, Greek -->
		 { "omega",  "&#x003C9;" }, // /omega small omega, Greek -->
		 { "Omega",  "&#x003A9;" }, // /Omega capital Omega, Greek -->
		 { "phi",    "&#x003C6;" }, // /straightphi - small phi, Greek -->
		 { "Phi",    "&#x003A6;" }, // /Phi capital Phi, Greek -->
		 { "phiv",   "&#x003D5;" }, // /varphi - curly or open phi -->
		 { "pi",     "&#x003C0;" }, // /pi small pi, Greek -->
		 { "Pi",     "&#x003A0;" }, // /Pi capital Pi, Greek -->
		 { "piv",    "&#x003D6;" }, // /varpi -->
		 { "psi",    "&#x003C8;" }, // /psi small psi, Greek -->
		 { "Psi",    "&#x003A8;" }, // /Psi capital Psi, Greek -->
		 { "rho",    "&#x003C1;" }, // /rho small rho, Greek -->
		 { "rhov",   "&#x003F1;" }, // /varrho -->
		 { "sigma",  "&#x003C3;" }, // /sigma small sigma, Greek -->
		 { "Sigma",  "&#x003A3;" }, // /Sigma capital Sigma, Greek -->
		 { "sigmav", "&#x003C2;" }, // /varsigma -->
		 { "tau",    "&#x003C4;" }, // /tau small tau, Greek -->
		 { "theta",  "&#x003B8;" }, // /theta straight theta, small theta, Greek -->
		 { "Theta",  "&#x00398;" }, // /Theta capital Theta, Greek -->
		 { "thetav", "&#x003D1;" }, // /vartheta - curly or open theta -->
		 { "upsi",   "&#x003C5;" }, // /upsilon small upsilon, Greek -->
		 { "Upsi",   "&#x003D2;" }, // /Upsilon capital Upsilon, Greek -->
		 { "xi",     "&#x003BE;" }, // /xi small xi, Greek -->
		 { "Xi",     "&#x0039E;" }, // /Xi capital Xi, Greek -->
		 { "zeta",   "&#x003B6;" } }; // /zeta small zeta, Greek -->

	private static final String ISO_DIA_ENITITIES[][] =  { // isodia.ent
		 { "acute", "&#x000B4;" }, // =acute accent -->
		 { "breve", "&#x002D8;" }, // =breve -->
		 { "caron", "&#x002C7;" }, // =caron -->
		 { "cedil", "&#x000B8;" }, // =cedilla -->
		 { "circ",  "&#x0005E;" }, // circumflex accent -->
		 { "dblac", "&#x002DD;" }, // =double acute accent -->
		 { "die",   "&#x000A8;" }, // =dieresis -->
		 { "dot",   "&#x002D9;" }, // =dot above -->
		 { "grave", "&#x00060;" }, // =grave accent -->
		 { "macr",  "&#x000AF;" }, // =macron -->
		 { "ogon",  "&#x002DB;" }, // =ogonek -->
		 { "ring",  "&#x002DA;" }, // =ring -->
		 { "tilde", "&#x002DC;" }, // =tilde -->
		 { "uml",   "&#x000A8;" } }; // =umlaut mark -->

	private static final String HTML4_LATIN_EXTENDED_ENITITIES[][] =  { // isodia.ent
		{ "fnof",   "&#402;" }, // latin small f with hook = function = florin, U+0192 ISOtech
		{ "OElig",  "&#338;"  }, // latin capital ligature OE, U+0152 ISOlat2 -->
		{ "oelig",  "&#339;"  }, // latin small ligature oe, U+0153 ISOlat2 -->
		{ "Scaron", "&#352;"  }, // latin capital letter S with caron, U+0160 ISOlat2 -->
		{ "scaron", "&#353;"  }, // latin small letter s with caron, U+0161 ISOlat2 -->
		{ "Yuml",   "&#376;"  } };  // latin capital letter Y with diaeresis, U+0178 ISOlat2 -->

	private static final String HTML4_GREEK_ENITITIES[][] =  { // Greek

		 { "Alpha",    "&#913;" }, // greek capital letter alpha, U+0391 -->
		 { "Beta",     "&#914;" }, // greek capital letter beta, U+0392 -->
		 { "Gamma",    "&#915;" }, // greek capital letter gamma, U+0393 ISOgrk3 -->
		 { "Delta",    "&#916;" }, // greek capital letter delta, U+0394 ISOgrk3 -->
		 { "Epsilon",  "&#917;" }, // greek capital letter epsilon, U+0395 -->
		 { "Zeta",     "&#918;" }, // greek capital letter zeta, U+0396 -->
		 { "Eta",      "&#919;" }, // greek capital letter eta, U+0397 -->
		 { "Theta",    "&#920;" }, // greek capital letter theta, U+0398 ISOgrk3 -->
		 { "Iota",     "&#921;" }, // greek capital letter iota, U+0399 -->
		 { "Kappa",    "&#922;" }, // greek capital letter kappa, U+039A -->
		 { "Lambda",   "&#923;" }, // greek capital letter lambda, U+039B ISOgrk3 -->
		 { "Mu",       "&#924;" }, // greek capital letter mu, U+039C -->
		 { "Nu",       "&#925;" }, // greek capital letter nu, U+039D -->
		 { "Xi",       "&#926;" }, // greek capital letter xi, U+039E ISOgrk3 -->
		 { "Omicron",  "&#927;" }, // greek capital letter omicron, U+039F -->
		 { "Pi",       "&#928;" }, // greek capital letter pi, U+03A0 ISOgrk3 -->
		 { "Rho",      "&#929;" }, // greek capital letter rho, U+03A1 -->
		 { "Sigma",    "&#931;" }, // greek capital letter sigma, U+03A3 ISOgrk3 -->
		 { "Tau",      "&#932;" }, // greek capital letter tau, U+03A4 -->
		 { "Upsilon",  "&#933;" }, // greek capital letter upsilon, U+03A5 ISOgrk3 -->
		 { "Phi",      "&#934;" }, // greek capital letter phi, U+03A6 ISOgrk3 -->
		 { "Chi",      "&#935;" }, // greek capital letter chi, U+03A7 -->
		 { "Psi",      "&#936;" }, // greek capital letter psi, U+03A8 ISOgrk3 -->
		 { "Omega",    "&#937;" }, // greek capital letter omega, U+03A9 ISOgrk3 -->
		 { "alpha",    "&#945;" }, // greek small letter alpha, U+03B1 ISOgrk3 -->
		 { "beta",     "&#946;" }, // greek small letter beta, U+03B2 ISOgrk3 -->
		 { "gamma",    "&#947;" }, // greek small letter gamma, U+03B3 ISOgrk3 -->
		 { "delta",    "&#948;" }, // greek small letter delta, U+03B4 ISOgrk3 -->
		 { "epsilon",  "&#949;" }, // greek small letter epsilon, U+03B5 ISOgrk3 -->
		 { "zeta",     "&#950;" }, // greek small letter zeta, U+03B6 ISOgrk3 -->
		 { "eta",      "&#951;" }, // greek small letter eta, U+03B7 ISOgrk3 -->
		 { "theta",    "&#952;" }, // greek small letter theta, U+03B8 ISOgrk3 -->
		 { "iota",     "&#953;" }, // greek small letter iota, U+03B9 ISOgrk3 -->
		 { "kappa",    "&#954;" }, // greek small letter kappa, U+03BA ISOgrk3 -->
		 { "lambda",   "&#955;" }, // greek small letter lambda, U+03BB ISOgrk3 -->
		 { "mu",       "&#956;" }, // greek small letter mu, U+03BC ISOgrk3 -->
		 { "nu",       "&#957;" }, // greek small letter nu, U+03BD ISOgrk3 -->
		 { "xi",       "&#958;" }, // greek small letter xi, U+03BE ISOgrk3 -->
		 { "omicron",  "&#959;" }, // greek small letter omicron, U+03BF NEW -->
		 { "pi",       "&#960;" }, // greek small letter pi, U+03C0 ISOgrk3 -->
		 { "rho",      "&#961;" }, // greek small letter rho, U+03C1 ISOgrk3 -->
		 { "sigmaf",   "&#962;" }, // greek small letter final sigma, U+03C2 ISOgrk3 -->
		 { "sigma",    "&#963;" }, // greek small letter sigma, U+03C3 ISOgrk3 -->
		 { "tau",      "&#964;" }, // greek small letter tau, U+03C4 ISOgrk3 -->
		 { "upsilon",  "&#965;" }, // greek small letter upsilon, U+03C5 ISOgrk3 -->
		 { "phi",      "&#966;" }, // greek small letter phi, U+03C6 ISOgrk3 -->
		 { "chi",      "&#967;" }, // greek small letter chi, U+03C7 ISOgrk3 -->
		 { "psi",      "&#968;" }, // greek small letter psi, U+03C8 ISOgrk3 -->
		 { "omega",    "&#969;" }, // greek small letter omega, U+03C9 ISOgrk3 -->
		 { "thetasym", "&#977;" }, // greek small letter theta symbol, U+03D1 NEW -->
		 { "upsih",    "&#978;" }, // greek upsilon with hook symbol, U+03D2 NEW -->
		 { "piv",      "&#982;" } }; // greek pi symbol, U+03D6 ISOgrk3 -->

	private static final String HTML4_PUNCTUATION_ENITITIES[][] =  { // Punctuation

		 { "bull",     "&#8226;" }, // bullet = black small circle, U+2022 ISOpub  -->
		 { "hellip",   "&#8230;" }, // horizontal ellipsis = three dot leader, U+2026 ISOpub  -->
		 { "prime",    "&#8242;" }, // prime = minutes = feet, U+2032 ISOtech -->
		 { "Prime",    "&#8243;" }, // double prime = seconds = inches, U+2033 ISOtech -->
		 { "oline",    "&#8254;" }, // overline = spacing overscore, U+203E NEW -->
		 { "frasl",    "&#8260;" }, // fraction slash, U+2044 NEW
		 { "circ",     "&#710;"  }, // modifier letter circumflex accent, U+02C6 ISOpub -->
		 { "tilde",    "&#732;"  }, // small tilde, U+02DC ISOdia -->
		 { "ensp",     "&#8194;" }, // en space, U+2002 ISOpub -->
		 { "emsp",     "&#8195;" }, // em space, U+2003 ISOpub -->
		 { "thinsp",   "&#8201;" }, // thin space, U+2009 ISOpub -->
		 { "zwnj",     "&#8204;" }, // zero width non-joiner, U+200C NEW RFC 2070 -->
		 { "zwj",      "&#8205;" }, // zero width joiner, U+200D NEW RFC 2070 -->
		 { "lrm",      "&#8206;" }, // left-to-right mark, U+200E NEW RFC 2070 -->
		 { "rlm",      "&#8207;" }, // right-to-left mark, U+200F NEW RFC 2070 -->
		 { "ndash",    "&#8211;" }, // en dash, U+2013 ISOpub -->
		 { "mdash",    "&#8212;" }, // em dash, U+2014 ISOpub -->
		 { "lsquo",    "&#8216;" }, // left single quotation mark, U+2018 ISOnum -->
		 { "rsquo",    "&#8217;" }, // right single quotation mark, U+2019 ISOnum -->
		 { "sbquo",    "&#8218;" }, // single low-9 quotation mark, U+201A NEW -->
		 { "ldquo",    "&#8220;" }, // left double quotation mark, U+201C ISOnum -->
		 { "rdquo",    "&#8221;" }, // right double quotation mark, U+201D ISOnum -->
		 { "bdquo",    "&#8222;" }, // double low-9 quotation mark, U+201E NEW -->
		 { "dagger",   "&#8224;" }, // dagger, U+2020 ISOpub -->
		 { "Dagger",   "&#8225;" }, // double dagger, U+2021 ISOpub -->
		 { "permil",   "&#8240;" }, // per mille sign, U+2030 ISOtech -->
		 { "lsaquo",   "&#8249;" }, // single left-pointing angle quotation mark, U+2039 ISO proposed -->
		 { "rsaquo",   "&#8250;" }, // single right-pointing angle quotation mark, U+203A ISO proposed -->
		 { "euro",     "&#8364;" } };  // euro sign, U+20AC NEW -->

	private static final String HTML4_LETTERLIKE_ENITITIES[][] =  { // Letter Like

		 { "weierp",   "&#8472;" }, // script capital P = power set = Weierstrass p, U+2118 ISOamso -->
		 { "image",    "&#8465;" }, // blackletter capital I = imaginary part, U+2111 ISOamso -->
		 { "real",     "&#8476;" }, // blackletter capital R = real part symbol, U+211C ISOamso -->
		 { "trade",    "&#8482;" }, // trade mark sign, U+2122 ISOnum -->
		 { "alefsym",  "&#8501;" } }; // alef symbol = first transfinite cardinal, U+2135 NEW -->

	private static final String HTML4_ARROWS_ENITITIES[][] =  { // Arrows

		 { "larr",     "&#8592;" }, // leftwards arrow, U+2190 ISOnum -->
		 { "uarr",     "&#8593;" }, // upwards arrow, U+2191 ISOnum-->
		 { "rarr",     "&#8594;" }, // rightwards arrow, U+2192 ISOnum -->
		 { "darr",     "&#8595;" }, // downwards arrow, U+2193 ISOnum -->
		 { "harr",     "&#8596;" }, // left right arrow, U+2194 ISOamsa -->
		 { "crarr",    "&#8629;" }, // downwards arrow with corner leftwards = carriage return, U+21B5 NEW -->
		 { "lArr",     "&#8656;" }, // leftwards double arrow, U+21D0 ISOtech -->
		 { "uArr",     "&#8657;" }, // upwards double arrow, U+21D1 ISOamsa -->
		 { "rArr",     "&#8658;" }, // rightwards double arrow, U+21D2 ISOtech -->
		 { "dArr",     "&#8659;" }, // downwards double arrow, U+21D3 ISOamsa -->
		 { "hArr",     "&#8660;" } }; // left right double arrow, U+21D4 ISOamsa -->

	private static final String HTML4_MATHS_ENITITIES[][] =  { // Mathematical Operators

		 { "forall",   "&#8704;" }, // for all, U+2200 ISOtech -->
		 { "part",     "&#8706;" }, // partial differential, U+2202 ISOtech  -->
		 { "exist",    "&#8707;" }, // there exists, U+2203 ISOtech -->
		 { "empty",    "&#8709;" }, // empty set = null set = diameter, U+2205 ISOamso -->
		 { "nabla",    "&#8711;" }, // nabla = backward difference, U+2207 ISOtech -->
		 { "isin",     "&#8712;" }, // element of, U+2208 ISOtech -->
		 { "notin",    "&#8713;" }, // not an element of, U+2209 ISOtech -->
		 { "ni",       "&#8715;" }, // contains as member, U+220B ISOtech -->
		 { "prod",     "&#8719;" }, // n-ary product = product sign, U+220F ISOamsb -->
		 { "sum",      "&#8721;" }, // n-ary sumation, U+2211 ISOamsb -->
		 { "minus",    "&#8722;" }, // minus sign, U+2212 ISOtech -->
		 { "lowast",   "&#8727;" }, // asterisk operator, U+2217 ISOtech -->
		 { "radic",    "&#8730;" }, // square root = radical sign, U+221A ISOtech -->
		 { "prop",     "&#8733;" }, // proportional to, U+221D ISOtech -->
		 { "infin",    "&#8734;" }, // infinity, U+221E ISOtech -->
		 { "ang",      "&#8736;" }, // angle, U+2220 ISOamso -->
		 { "and",      "&#8743;" }, // logical and = wedge, U+2227 ISOtech -->
		 { "or",       "&#8744;" }, // logical or = vee, U+2228 ISOtech -->
		 { "cap",      "&#8745;" }, // intersection = cap, U+2229 ISOtech -->
		 { "cup",      "&#8746;" }, // union = cup, U+222A ISOtech -->
		 { "int",      "&#8747;" }, // integral, U+222B ISOtech -->
		 { "there4",   "&#8756;" }, // therefore, U+2234 ISOtech -->
		 { "sim",      "&#8764;" }, // tilde operator = varies with = similar to, U+223C ISOtech -->
		 { "cong",     "&#8773;" }, // approximately equal to, U+2245 ISOtech -->
		 { "asymp",    "&#8776;" }, // almost equal to = asymptotic to, U+2248 ISOamsr -->
		 { "ne",       "&#8800;" }, // not equal to, U+2260 ISOtech -->
		 { "equiv",    "&#8801;" }, // identical to, U+2261 ISOtech -->
		 { "le",       "&#8804;" }, // less-than or equal to, U+2264 ISOtech -->
		 { "ge",       "&#8805;" }, // greater-than or equal to, U+2265 ISOtech -->
		 { "sub",      "&#8834;" }, // subset of, U+2282 ISOtech -->
		 { "sup",      "&#8835;" }, // superset of, U+2283 ISOtech -->
		 { "nsub",     "&#8836;" }, // not a subset of, U+2284 ISOamsn -->
		 { "sube",     "&#8838;" }, // subset of or equal to, U+2286 ISOtech -->
		 { "supe",     "&#8839;" }, // superset of or equal to, U+2287 ISOtech -->
		 { "oplus",    "&#8853;" }, // circled plus = direct sum, U+2295 ISOamsb -->
		 { "otimes",   "&#8855;" }, // circled times = vector product, U+2297 ISOamsb -->
		 { "perp",     "&#8869;" }, // up tack = orthogonal to = perpendicular, U+22A5 ISOtech -->
		 { "sdot",     "&#8901;" } }; // dot operator, U+22C5 ISOamsb -->

	private static final String HTML4_MISC_ENITITIES[][] =  { // Miscellaneous Technical, Geometric Shapes and Symbols

		 { "lceil",    "&#8968;" }, // left ceiling = apl upstile, U+2308 ISOamsc  -->
		 { "rceil",    "&#8969;" }, // right ceiling, U+2309 ISOamsc  -->
		 { "lfloor",   "&#8970;" }, // left floor = apl downstile, U+230A ISOamsc  -->
		 { "rfloor",   "&#8971;" }, // right floor, U+230B ISOamsc  -->
		 { "lang",     "&#9001;" }, // left-pointing angle bracket = bra, U+2329 ISOtech -->
		 { "rang",     "&#9002;" }, // right-pointing angle bracket = ket, U+232A ISOtech -->
		 { "loz",      "&#9674;" }, // lozenge, U+25CA ISOpub -->
		 { "spades",   "&#9824;" }, // black spade suit, U+2660 ISOpub -->
		 { "clubs",    "&#9827;" }, // black club suit = shamrock, U+2663 ISOpub -->
		 { "hearts",   "&#9829;" }, // black heart suit = valentine, U+2665 ISOpub -->
		 { "diams",    "&#9830;" } }; // black diamond suit, U+2666 ISOpub -->

	private static final Object[] ISO_ENITITIES =  { 
			ISO_LATIN1_ENITITIES, 
			ISO_LATIN1_ENITITIES, 
			ISO_NUMERIC_ENITITIES, 
			ISO_TECHNICAL_ENITITIES, 
			ISO_PUBLIC_ENITITIES, 
			ISO_CYRILLIC1_ENITITIES, 
			ISO_CYRILLIC2_ENITITIES, 
			ISO_GREEK1_ENITITIES, 
			ISO_GREEK2_ENITITIES, 
			ISO_GREEK3_ENITITIES,
			ISO_DIA_ENITITIES,
			HTML4_LATIN_EXTENDED_ENITITIES,
			HTML4_GREEK_ENITITIES,
			HTML4_PUNCTUATION_ENITITIES,
			HTML4_LETTERLIKE_ENITITIES,
			HTML4_ARROWS_ENITITIES,
			HTML4_MATHS_ENITITIES,
			HTML4_MISC_ENITITIES };
} 
