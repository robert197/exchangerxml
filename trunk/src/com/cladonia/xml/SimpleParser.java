/*
 * $Id: SimpleParser.java,v 1.6 2004/06/03 10:22:36 knesbitt Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.Hashtable;
import java.util.Stack;

import org.apache.xerces.util.XMLChar;


/**
 * @author Dogs bay
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
/**
 * A simple, pull based XML parser. 
 * Keeps track of the character position for elements, attributes and content.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/06/03 10:22:36 $
 * @author Dogs bay
 */


public class SimpleParser implements XmlPullParser {

	static final private String UNEXPECTED_EOF = "Unexpected EOF";
	static final private String ILLEGAL_TYPE = "Wrong event type";
	static final private int LEGACY = 999;

	// general
	
	private boolean reportNspAttr;
	private boolean processNsp;
	private boolean relaxed;
	private Hashtable entityMap;
	private int depth;
	private String[] elementStack = new String[16];
	private String[] nspStack = new String[8];
	private int[] nspCounts = new int[4];

	// source

	private Reader reader;
	private String encoding;
	private char[] srcBuf;

	private int srcPos;
	private int srcCount;

	//    private boolean eof;

	private int line;
	private int column;

	// txtbuffer

	private char[] txtBuf = new char[128];
	private int txtPos;

	// Event-related

	private int type;
	private String text;
	private boolean isWhitespace;
	private String namespace;
	private String prefix;
	private String name;

	private boolean degenerated;
	private int attributeCount;
	private String[] attributes = new String[16];

	private int[] peek = new int[2];
	private int peekCount;
	private boolean wasCR;
	private boolean unresolved;
	private boolean token;
	
	// the number of reads on the stream that have been called, so we 
	// can track where in the stream we are
	private int readCounter = -1;
	
	// holds the start positon of the current element
	private int elementStart = -1;
	
	// holds the end position of the current element
	private int elementEnd = -1;
	
	// holds the start position of the current element's content
	private int contentStart = -1;
	
	// holds the end position of the current element's content
	private int contentEnd = -1;
	
	// holds the start position of all the current element's attribute
	private ArrayList attributeStart = new ArrayList();
	
	// holds the end position of all the current element's attribute
	private ArrayList attributeEnd = new ArrayList();
	
	// stores the previous character
	private int previous = '\n';
	
	
	public SimpleParser() {
		this(Runtime.getRuntime().freeMemory() >= 1048576 ? 8192 : 128);
	}

	public SimpleParser(int sz) {
		if (sz > 1)
			srcBuf = new char[sz];
	}
	
	private StringBuffer karlbuf = new StringBuffer();

	private final boolean adjustNsp() throws XmlPullParserException {

		boolean any = false;

		for (int i = 0; i < attributeCount << 2; i += 4) {
			// * 4 - 4; i >= 0; i -= 4) {

			String attrName = attributes[i + 2];
			int cut = attrName.indexOf(':');
			String prefix;

			if (cut != -1) {
				prefix = attrName.substring(0, cut);
				attrName = attrName.substring(cut + 1);
			} else if (attrName.equals("xmlns")) {
				prefix = attrName;
				attrName = null;
			} else
				continue;

			if (!prefix.equals("xmlns")) {
				any = true;
			} else {
				int j = (nspCounts[depth]++) << 1;

				nspStack = ensureCapacity(nspStack, j + 2);
				nspStack[j] = attrName;
				nspStack[j + 1] = attributes[i + 3];

				if (attrName != null && attributes[i + 3].equals(""))
					exception("illegal empty namespace");

				//  prefixMap = new PrefixMap (prefixMap, attrName, attr.getValue ());

				if (!reportNspAttr) {
					System.arraycopy(
						attributes,
						i + 4,
						attributes,
						i,
						((--attributeCount) << 2) - i);

					i -= 4;
				} else
					any = true;
			}
		}

		if (any) {
			for (int i = (attributeCount << 2) - 4; i >= 0; i -= 4) {

				String attrName = attributes[i + 2];
				int cut = attrName.indexOf(':');

				if (cut == 0)
					throw new RuntimeException(
						"illegal attribute name: " + attrName + " at " + this);

				else if (cut != -1) {
					String attrPrefix = attrName.substring(0, cut);

					attrName = attrName.substring(cut + 1);

					String attrNs = getNamespace(attrPrefix);

					if (attrNs == null)
						throw new RuntimeException("Undefined Prefix: " + attrPrefix + " in " + this);

					attributes[i] = attrNs;
					attributes[i + 1] = attrPrefix;
					attributes[i + 2] = attrName;

					for (int j = (attributeCount << 2) - 4; j > i; j -= 4)
						if (attrName.equals(attributes[j + 2]) && attrNs.equals(attributes[j]))
							exception("Duplicate Attribute: {" + attrNs + "}" + attrName);

				}
			}
		}

		int cut = name.indexOf(':');

		if (cut == 0)
			exception("illegal tag name: " + name);
		else if (cut != -1) {
			prefix = name.substring(0, cut);
			name = name.substring(cut + 1);
		}

		this.namespace = getNamespace(prefix);

		if (this.namespace == null) {
			if (prefix != null)
				exception("undefined prefix: " + prefix);
			this.namespace = NO_NAMESPACE;
		}

		return any;
	}

	private final String[] ensureCapacity(String[] arr, int required) {
		if (arr.length >= required)
			return arr;
		String[] bigger = new String[required + 16];
		System.arraycopy(arr, 0, bigger, 0, arr.length);
		return bigger;
	}

	private final void exception(String desc) throws XmlPullParserException {
		throw new XmlPullParserException(desc, this, null);
	}

	/** common base for next and nextToken. Clears the state, 
	except from txtPos and whitespace. Does not set the type variable */

	private final void nextImpl() throws IOException, XmlPullParserException {

		if (reader == null)
			exception("No Input specified");

		if (type == END_TAG) depth--;

		attributeCount = -1;

		if (degenerated) { 
			degenerated = false;
			type = END_TAG;
			return;
		}

		prefix = null;
		name = null;
		namespace = null;
		text = null;

		type = peekType();

		switch (type) {

			case ENTITY_REF :
				pushEntity();
				break;

			case START_TAG :
				parseStartTag();
				break;

			case END_TAG :
				parseEndTag();
				break;

			case END_DOCUMENT :
				break;

			case TEXT :
				pushText('<', !token);
				if (depth == 0) {
					if (isWhitespace)
						type = IGNORABLE_WHITESPACE;
					// make exception switchable for instances.chg... !!!!
					//	else 
					//    exception ("text '"+getText ()+"' not allowed outside root element");
				}
				break;

			default :
				type = parseLegacy(token);
		}
	}

	private final int parseLegacy(boolean push)
		throws IOException, XmlPullParserException {

		String req = "";
		int term;
		int result;

		read(); // <
		int c = read();

		if (c == '?') {
			term = '?';
			result = PROCESSING_INSTRUCTION;
		} else if (c == '!') {
			if (peek(0) == '-') {
				result = COMMENT;
				req = "--";
				term = '-';
			} else if (peek(0) == '[') {
				result = CDSECT;
				req = "[CDATA[";
				term = ']';
				push = true;
			} else {
				result = DOCDECL;
				req = "DOCTYPE";
				term = -1;
			}
		} else {
			exception("illegal: <" + c);
			return -1;
		}

		for (int i = 0; i < req.length(); i++)
			read(req.charAt(i));

		if (result == DOCDECL)
			parseDoctype(push);
		else {
			while (true) {
				c = read();
				if (c == -1)
					exception(UNEXPECTED_EOF);

				if (push)
					push(c);

				if ((term == '?' || c == term) && peek(0) == term && peek(1) == '>')
					break;
			}
			read();
			read();

			if (push && term != '?')
				txtPos--;

		}
		return result;
	}

	/** precondition: &lt! consumed */

	private final void parseDoctype(boolean push)
		throws IOException, XmlPullParserException {

		int nesting = 1;
		boolean quoted = false;

		while (true) {
			int i = read();
			
		
			switch (i) {

				case -1 :
					exception(UNEXPECTED_EOF);

				//case '\'' :
					//quoted = !quoted;
					//break;

				case '<' :
					if (!quoted)
						nesting++;
					break;

				case '>' :
					if (!quoted) {
						if ((--nesting) == 0)
							return;
					}
					break;
			}
			if (push)
				push(i);
		}
	}

	/* precondition: &lt;/ consumed */

	private final void parseEndTag() throws IOException, XmlPullParserException {

		if (depth == 0)
			exception("element stack empty");

		read(); // '<'
		read(); // '/'
		name = readName();
		
		
		
		// subtract one for the '<' character, and one for the '/' character 
		contentEnd = (readCounter-name.length())-2;

		int sp = (depth-1) << 2;

		if (!name.equals(elementStack[sp + 3]))
			exception("expected: " + elementStack[sp+3]);

		skip();
		read('>');
		
		// add one to allow for the '>' character
		elementEnd = readCounter+1;

		namespace = elementStack[sp];
		prefix = elementStack[sp + 1];
		name = elementStack[sp + 2];
	}

	private final int peekType() throws IOException {		
		switch (peek(0)) {
			case -1 :
				return END_DOCUMENT;
			case '&' :
				return ENTITY_REF;
			case '<' :
				switch (peek(1)) {
					case '/' :
						return END_TAG;
					case '?' :
					case '!' :
						return LEGACY;
					default :
						return START_TAG;
				}
			default :
				return TEXT;
		}
	}

	private final String get(int pos) {
		return new String(txtBuf, pos, txtPos - pos);
	}

	/*
	private final String pop (int pos) {
	String result = new String (txtBuf, pos, txtPos - pos);
	txtPos = pos;
	return result;
	}
	*/

	private final void push(int c) {
		if ((c == '\r' || c == '\n') && (!token || type == START_TAG)) {

			if (c == '\n' && wasCR) {
				wasCR = false;
				return;
			}

			wasCR = c == '\r';
			c = type == START_TAG ? ' ' : '\n';
		} 
		else
			wasCR = false;

		isWhitespace &= c <= ' ';

		if (txtPos == txtBuf.length) {
			char[] bigger = new char[txtPos * 4 / 3 + 4];
			System.arraycopy(txtBuf, 0, bigger, 0, txtPos);
			txtBuf = bigger;
		}

		txtBuf[txtPos++] = (char) c;
	}

	/** Sets name and attributes */

	private final void parseStartTag() throws IOException, XmlPullParserException {

		read(); // <
		name = readName();
		
		// set the elemnt start position
		elementStart = readCounter-name.length();
		
		attributeCount = 0;
		int namespaceAttributeCount = 0;

		while (true) {
			skip();

			int c = peek(0);

			if (c == '/') {
				degenerated = true;
				read();
				skip();
				read('>');
				
				// need to store the end position here for degenerated elements, add one for the '>' 
				// character
				elementEnd = readCounter+1;
				
				// the content end will be the same as the element end
				contentEnd = elementEnd;
				break;
			}

			if (c == '>') {
				read();
				break;
			}

			if (c == -1)
				exception(UNEXPECTED_EOF);


			String attrName = readName();
			
			
			
			// add one at the end to allow for the '=' character
			Integer attrStart = new Integer((readCounter-attrName.length())+1);
			attributeStart.add(attributeCount-namespaceAttributeCount,attrStart);
		
			if (attrName.length() == 0)
				exception("attr name expected");

			skip();
			read('=');
			skip();
			int delimiter = read();

			if (delimiter != '\'' && delimiter != '"') {
				if (!relaxed)
					exception("<" + name + ">: invalid delimiter: " + (char) delimiter);

				delimiter = ' ';
			}

			int i = (attributeCount++) << 2;

			attributes = ensureCapacity(attributes, i + 4);

			attributes[i++] = "";
			attributes[i++] = null;
			attributes[i++] = attrName;

			int p = txtPos;
			pushText(delimiter, true);

			attributes[i] = get(p);
			txtPos = p;

			if (delimiter != ' ')
				read(); // skip endquote
			
			// add one top allow for the '"' character
			Integer attrEnd = new Integer(readCounter+1);
			attributeEnd.add(attributeCount-1,attrEnd);
		}
		
        // add one to the content start for the '>' character 
		contentStart = readCounter+1;
		
		// re-adjust content start for degenerated element 
		if (degenerated == true)
		{
			contentStart = elementStart;
		}

		int sp = depth++ << 2;

		elementStack = ensureCapacity(elementStack, sp + 4);
		elementStack[sp + 3] = name;

		if (depth >= nspCounts.length) {
			int[] bigger = new int[depth + 4];
			System.arraycopy(nspCounts, 0, bigger, 0, nspCounts.length);
			nspCounts = bigger;
		}

		nspCounts[depth] = nspCounts[depth - 1];

		for (int i = attributeCount - 1; i > 0; i--) {
			for (int j = 0; j < i; j++) {
				if (getAttributeName(i).equals(getAttributeName(j)))
					exception("Duplicate Attribute: " + getAttributeName(i));
			}
		}

		if (processNsp)
			adjustNsp();
		else
			namespace = "";

		elementStack[sp] = namespace;
		elementStack[sp + 1] = prefix;
		elementStack[sp + 2] = name;
	}

	/** result: isWhitespace; if the setName parameter is set,
	the name of the entity is stored in "name" */

	private final void pushEntity() throws IOException, XmlPullParserException {

		read(); // &

		int pos = txtPos;

		while (true) {
			int c = read();
			if (c == ';')
				break;
			if (c == -1)
				exception(UNEXPECTED_EOF);
			push(c);
		}

		String code = get(pos);
		txtPos = pos;
		if (token && type == ENTITY_REF)
			name = code;

		if (code.charAt(0) == '#') {
			int c =
				(code.charAt(1) == 'x'
					? Integer.parseInt(code.substring(2), 16)
					: Integer.parseInt(code.substring(1)));
			push(c);
			return;
		}

		String result = (String) entityMap.get(code);

		unresolved = result == null;

		if (unresolved) {
			//if (!token)
				//exception("unresolved: &" + code + ";");
		} else {
			//for (int i = 0; i < result.length(); i++)
				//push(result.charAt(i));
		}
	}

	/** types:
	'<': parse to any token (for nextToken ())
	'"': parse to quote
	' ': parse to whitespace or '>'
	*/

	private final void pushText(int delimiter, boolean resolveEntities)
		throws IOException, XmlPullParserException {

		int next = peek(0);

		while (next != -1 && next != delimiter) { // covers eof, '<', '"' 

			if (delimiter == ' ')
				if (next <= ' ' || next == '>')
					break;

			if (next == '&') {
				if (!resolveEntities)
					break;

				pushEntity();
			} else
				push(read());

			next = peek(0);
		}
	}

	private final void read(char c) throws IOException, XmlPullParserException {
		int a = read();
		if (a != c)
			exception("expected: '" + c + "' actual: '" + ((char) a) + "'");
	}

	private final int read() throws IOException {

		readCounter++;

		int result = peekCount == 0 ? peek(0) : peek[0];
		peek[0] = peek[1];
		peekCount--;

		column++;

		if (result == '\n') {

			line++;
			column = 1;
			
			if (previous == '\r') 
			{
				// we have \r\n so remove one from counter
				readCounter--;
			}
		}
		
		previous = result;
		return result;
	}

	private final int peek(int pos) throws IOException {


		while (pos >= peekCount) {

			int nw;

			if (srcBuf.length <= 1)
				nw = reader.read();
			else if (srcPos < srcCount)
				nw = srcBuf[srcPos++];
			else {
				srcCount = reader.read(srcBuf, 0, srcBuf.length);
				if (srcCount <= 0)
					nw = -1;
				else
					nw = srcBuf[0];

				srcPos = 1;
			}

			peek[peekCount++] = nw;
			
		}
		
		return peek[pos];
	}

	private final String readName() throws IOException, XmlPullParserException {

		int pos = txtPos;
		int c = peek(0);

//      We assume the code is wellformed
//		if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && c != '_' && c != ':')
//			exception("name expected");

		do {
			push(read());
			c = peek(0);
		} while ( XMLChar.isName( (char)c));

//      This would be very dangerous only allowing ascii charcters:
//			(c >= 'a' && c <= 'z')
//				|| (c >= 'A' && c <= 'Z')
//				|| (c >= '0' && c <= '9')
//				|| c == '_'
//				|| c == '-'
//				|| c == ':'
//				|| c == '.');

		String result = get(pos);
		txtPos = pos;
		return result;
	}

	private final void skip() throws IOException {

		while (true) {
			int c = peek(0);
			if (c > ' ' || c == -1)
				break;
			read();
		}
	}

	//--------------- public part starts here... ---------------

	public void setInput(Reader reader) throws XmlPullParserException {
		this.reader = reader;

		line = 1;
		column = 0;
		type = START_DOCUMENT;
		name = null;
		namespace = null;
		degenerated = false;
		attributeCount = -1;
		encoding = null;

		if (reader == null)
			return;

		srcPos = 0;
		srcCount = 0;
		peekCount = 0;
		depth = 0;

		entityMap = new Hashtable();
		entityMap.put("amp", "&");
		entityMap.put("apos", "'");
		entityMap.put("gt", ">");
		entityMap.put("lt", "<");
		entityMap.put("quot", "\"");
	}

	public void setInput(InputStream is, String enc)
		throws XmlPullParserException {

		if (is == null) throw new IllegalArgumentException ();
		// add heuristics here!

		if (is == null) throw new IllegalArgumentException ();

		try {
			setInput(enc == null 
				? new InputStreamReader (is) 
				: new InputStreamReader(is, enc));
			encoding = enc;
		}
		catch (IOException e) {
			throw new XmlPullParserException (e.toString (), this, e);
		}	
	}

	public boolean getFeature(String feature) {
		if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature))
			return processNsp;
		else if (XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES.equals(feature))
			return reportNspAttr;
		else
			return false;
	}

	public String getInputEncoding() {
		return encoding;
	}

	public void defineEntityReplacementText(String entity, String value)
		throws XmlPullParserException {
		entityMap.put(entity, value);
	}

	public Object getProperty(String property) {
		return null;
	}

	public int getNamespaceCount(int depth) {
		if (depth > this.depth)
			throw new IndexOutOfBoundsException();
		return nspCounts[depth];
	}

	public String getNamespacePrefix(int pos) {
		return nspStack[pos << 1];
	}

	public String getNamespaceUri(int pos) {
		return nspStack[(pos << 1) + 1];
	}

	public String getNamespace(String prefix) {

		if ("xml".equals(prefix))
			return "http://www.w3.org/XML/1998/namespace";
		if ("xmlns".equals(prefix))
			return "http://www.w3.org/2000/xmlns/";

		for (int i = (getNamespaceCount(depth) << 1) - 2; i >= 0; i -= 2) {
			if (prefix == null) {
				if (nspStack[i] == null)
					return nspStack[i + 1];
			} else if (prefix.equals(nspStack[i]))
				return nspStack[i + 1];
		}
		return null;
	}

	public int getDepth() {
		return depth;
	}

	public String getPositionDescription() {

		StringBuffer buf =
			new StringBuffer(type < TYPES.length ? TYPES[type] : "unknown");
		buf.append(' ');

		if (type == START_TAG || type == END_TAG) {
			if (degenerated)
				buf.append("(empty) ");
			buf.append('<');
			if (type == END_TAG)
				buf.append('/');

			if (prefix != null)
				buf.append("{" + namespace + "}" + prefix + ":");
			buf.append(name);

			int cnt = attributeCount << 2;
			for (int i = 0; i < cnt; i += 4) {
				buf.append(' ');
				if (attributes[i + 1] != null)
					buf.append("{" + attributes[i] + "}" + attributes[i + 1] + ":");
				buf.append(attributes[i + 2] + "='" + attributes[i + 3] + "'");
			}

			buf.append('>');
		} else if (type == IGNORABLE_WHITESPACE);
		else if (type != TEXT)
			buf.append(getText());
		else if (isWhitespace)
			buf.append("(whitespace)");
		else {
			String text = getText();
			if (text.length() > 16)
				text = text.substring(0, 16) + "...";
			buf.append(text);
		}

		buf.append(" @" + line + ":" + column);
		return buf.toString();
	}

	public int getLineNumber() {
		return line;
	}

	public int getColumnNumber() {
		return column;
	}

	public boolean isWhitespace() throws XmlPullParserException {
		if (type != TEXT && type != IGNORABLE_WHITESPACE && type != CDSECT)
			exception(ILLEGAL_TYPE);
		return isWhitespace;
	}

	public String getText() {
		return type < TEXT || (type == ENTITY_REF && unresolved) ? null : get(0);
	}

	public char[] getTextCharacters(int[] poslen) {
		if (type >= TEXT) {
			if (type == ENTITY_REF) {
				poslen[0] = 0;
				poslen[1] = name.length();
				return name.toCharArray();
			}
			poslen[0] = 0;
			poslen[1] = txtPos;
			return txtBuf;
		}

		poslen[0] = -1;
		poslen[1] = -1;
		return null;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getName() {
		return name;
	}

	public String getPrefix() {
		return prefix;
	}

	public boolean isEmptyElementTag() throws XmlPullParserException {
		if (type != START_TAG)
			exception(ILLEGAL_TYPE);
		return degenerated;
	}

	public int getAttributeCount() {
		return attributeCount;
	}

	public String getAttributeType(int index) {
		return "CDATA";
	}

	public boolean isAttributeDefault(int index) {
		return false;
	}

	public String getAttributeNamespace(int index) {
		if (index >= attributeCount)
			throw new IndexOutOfBoundsException();
		return attributes[index << 2];
	}

	public String getAttributeName(int index) {
		if (index >= attributeCount)
			throw new IndexOutOfBoundsException();
		return attributes[(index << 2) + 2];
	}

	public String getAttributePrefix(int index) {
		if (index >= attributeCount)
			throw new IndexOutOfBoundsException();
		return attributes[(index << 2) + 1];
	}

	public String getAttributeValue(int index) {
		if (index >= attributeCount)
			throw new IndexOutOfBoundsException();
		return attributes[(index << 2) + 3];
	}

	public String getAttributeValue(String namespace, String name) {

		for (int i = (attributeCount << 2) - 4; i >= 0; i -= 4) {
			if (attributes[i + 2].equals(name)
				&& (namespace == null || attributes[i].equals(namespace)))
				return attributes[i + 3];
		}

		return null;
	}

	public int getEventType() throws XmlPullParserException {
		return type;
	}

	public int next() throws XmlPullParserException, IOException {

		txtPos = 0;
		isWhitespace = true;
		int minType = 9999;
		token = false;

		do {
			nextImpl();
			if (type < minType)
				minType = type;
			//	    if (curr <= TEXT) type = curr; 
		} while (minType > CDSECT || (minType >= TEXT && peekType() >= TEXT));

		//        if (type > TEXT) type = TEXT;
		type = minType;

		return type;
	}

	public int nextToken() throws XmlPullParserException, IOException {

		isWhitespace = true;
		txtPos = 0;

		token = true;
		nextImpl();
		return type;
	}

	//----------------------------------------------------------------------
	// utility methods to make XML parsing easier ...

	public int nextTag() throws XmlPullParserException, IOException {

		next();
		if (type == TEXT && isWhitespace)
			next();

		if (type != END_TAG && type != START_TAG)
			exception("unexpected type");

		return type;
	}

	public void require(int type, String namespace, String name)
		throws XmlPullParserException, IOException {

		if (type != this.type
			|| (namespace != null && !namespace.equals(getNamespace()))
			|| (name != null && !name.equals(getName())))
			exception("expected: " + TYPES[type] + " {" + namespace + "}" + name);
	}

	public String nextText() throws XmlPullParserException, IOException {
		if (type != START_TAG)
			exception("precondition: START_TAG");

		next();

		String result;

		if (type == TEXT) {
			result = getText();
			next();
		} else
			result = "";

		if (type != END_TAG)
			exception("END_TAG expected");

		return result;
	}

	public void setFeature(String feature, boolean value)
		throws XmlPullParserException {
		if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature))
			processNsp = value;
		else if (XmlPullParser.FEATURE_REPORT_NAMESPACE_ATTRIBUTES.equals(feature))
			reportNspAttr = value;
		else
			exception("unsupported feature: " + feature);
	}

	public void setProperty(String property, Object value)
		throws XmlPullParserException {
		throw new XmlPullParserException("unsupported property: " + property);
	}
	
	public int getElementStart()
	{
		return elementStart;
	}
	
	public int getElementEnd()
	{
		return elementEnd;
	}
	
	public int getContentStart()
	{
		return contentStart;
	}
	
	public int getContentEnd()
	{
		return contentEnd;
	}
	
	public int getAttributeStart(int i)
	{
		Object obj = attributeStart.get(i);
		if (obj == null)
		{
			return -1;
		}
		return ((Integer)obj).intValue();
	}
	
	public int getAttributeEnd(int i)
	{
		Object obj = attributeEnd.get(i);
		if (obj == null)
		{
			return -1;
		}
		return ((Integer)obj).intValue();
	}
	
	public void writeText(Reader reader,XElement current) throws Exception
	{
		try{
			setInput(reader);

			// create a stack to store the elements
	        Stack xelements = new Stack(); 
	        
	        // create an XElement to store the end element
	        XElement elementEnd = null;
	        
	        // parse the events
			int eventType = getEventType();
			
			// for the root element
			while (eventType != START_TAG)
			{
				// keep iterating until we get to the root element, we can assume there is
				// a root element as the document has previously been checked for 
				// well-formedness
				eventType = next();
			}
			
			// push the root element onto the stack
			xelements.push(current);
			
			// set the root element details
			setElementStartDetails(current,getName(),getElementStart(),getContentStart());
			
			// set the root element's attribute details
			setAttributeDetails(current);
			
            // get the next event
			eventType = next();
						
			// for the rest of the tree
			while (eventType != END_DOCUMENT)
			{
				if(eventType == START_TAG) 
				{
					
					// get the next logical element
					current = getNextElement(current,getName());
					
					// push the element on the stack for the end tag to use
					xelements.push(current);
										
					// set the element details for this start tag
					setElementStartDetails(current,getName(),getElementStart(),getContentStart());
					
					// set the attribute details for this element
					setAttributeDetails(current);	
				}
				else if(eventType == END_TAG) 
				{	
					// get the next end element from the stack
					elementEnd = getNextEndElement(current,xelements);
					
					// set the element details for this end tag
					setElementEndDetails(elementEnd,getName(),getElementEnd(),getContentEnd());
				} 

				// parse for the next event
				eventType = next();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new Exception(e.toString());
		}
	}
	
	// gets the next logical element
	private XElement getNextElement(XElement current,String nextElementName)
	{
		XElement currentParent = null;
		XElement[] children = null;
	
		children = current.getElements();
		if (children.length != 0)
		{
			return children[0];
		}
		
		// recurse up the tree
		return getChildUpTree(current);
	}
	
	// recurses back up the tree to get the next logical element 
	private XElement getChildUpTree(XElement current)
	{
		// get the parent
		XElement currentParent = current.parent();
		
		// keep going back up tree until you find a parent with more than one child
		while (currentParent.getElements().length < 2)
		{
			current = currentParent;
			currentParent = currentParent.parent();
		}
		XElement[] children = currentParent.getElements();
		
		// need to get the child in the position beyond the current
		int found = -1;
		for (int i=0; i<children.length;i++)
		{
			XElement child = children[i];
			if (child.equals(current))
			{
				found = i;
			}
		}
		
		// return the child after the current if there is one
		if (found < (children.length-1))
		{
			return children[found+1];
		}
		else
		{
			// there is no child after the current so recurse up the tree
			return getChildUpTree(currentParent);
		}
	}
	
	
	// gets the matching end element from the stack
	private XElement getNextEndElement(XElement current,Stack xelements) throws Exception
	{
		try
		{			
        	current = (XElement)xelements.peek();
        	if (current.getQualifiedName().equals(getName()))
        	{
        		// we have a match
        		xelements.pop();
        	}
        	else
        	{
        		xelements.pop();
        		current = (XElement)xelements.peek();
			
        		while (!(current.getQualifiedName()).equals(getName()))
        		{
        			xelements.pop();
        			current = (XElement)xelements.peek();
        		}
        	}
        	return current;
		}
		catch (EmptyStackException e)
		{
				throw new Exception("Can not find the matching start element for: "+getName());
		}
	}
	
	// sets the current element's attributes details
	private void setAttributeDetails(XElement current) throws Exception
	{
		int attrCount = getAttributeCount();
		XAttribute[] attributes = null;
		
		if (attrCount > 0)
		{
			// therefore current should also have attributes
			attributes = current.getAttributes();
			if ((attributes.length == 0) || (attributes == null))
			{
				for (int i=0;i<attrCount;i++)
				{
					if (getAttributeName(i).startsWith("xmlns"))
					{
						// namespace declaration so ignore
					}
					else
					{
						throw new Exception("The attributes from the simple parser do not match"+
						" the internal model");
					}
				}
				
				// all namepace declaration so ignore and return
				return;
			}
			
			int namespaceCounter = 0;
			for (int i=0;i<attrCount;i++)
			{
				String attrName = getAttributeName(i);
				
				// don't add namespace declarations to the attribute list
				if (!attrName.equals("xmlns") && (!attrName.startsWith("xmlns:")))
				{
				
					String attrValue = getAttributeValue(i);
					attributes[i-namespaceCounter].setAttributeStartPosition(getAttributeStart(i));
					attributes[i-namespaceCounter].setAttributeEndPosition(getAttributeEnd(i));
				}
				else
				{
					namespaceCounter++;
				}
			}
		}
	}
	
	// sets the start position for the current element, and the start position for the content 
	private void setElementStartDetails(XElement current,String name,int elementStart,
			int contentStart) throws Exception
	{
		String currentName = current.getQualifiedName();
		if (!currentName.equals(name))
		{
			throw new Exception("The name of the element does not match that found in the simple"+
					"parser");
		}
		
		current.setElementStartPosition(elementStart);
		current.setContentStartPosition(contentStart);
	}
	
    // sets the end position for the current element, and the end position for the content 
	private void setElementEndDetails(XElement current,String name,int elementEnd,
			int contentEnd) throws Exception
	{
		String currentName = current.getQualifiedName();
		if (!currentName.equals(name))
		{
			throw new Exception("The name of the element does not match that found in the simple"+
					"parser");
		}
		
		current.setElementEndPosition(elementEnd);
		current.setContentEndPosition(contentEnd);
	}
}