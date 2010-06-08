/*
 * $Id: ConverterOutputDirectory.java,v 1.1 2004/03/25 18:37:50 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.converter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.xml.out.CharRepertoire;
import com.thaiopensource.xml.util.EncodingMap;

public class ConverterOutputDirectory implements OutputDirectory {
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private static final String ENCODING = "UTF-8";
	private static final int LINE_LENGTH = 120;
	private static final int INDENT = 4;

	private OutputStream stream = null;
	private String encoding = null;

	public ConverterOutputDirectory( OutputStream stream, String encoding) {
		this.stream = stream;
		this.encoding = encoding;
	}

	public OutputStream getStream() {
		return stream;
	}

	public OutputDirectory.Stream open( String source, String encoding) throws IOException {
		if (encoding == null) {
			if ( this.encoding == null) {
				encoding = ENCODING;
			} else {
				encoding = this.encoding;
			}
		}

		String javaEncoding = EncodingMap.getJavaName( encoding);
		OutputStreamWriter writer = new OutputStreamWriter( new BufferedOutputStream( stream), javaEncoding);

		return new OutputDirectory.Stream( writer, encoding, CharRepertoire.getInstance(javaEncoding));
	}

	public String reference(String fromSourceUri, String toSourceUri) {
		return toSourceUri;
	}

	public void setEncoding( String encoding) {}

	public String getLineSeparator() {
		return LINE_SEPARATOR;
	}

	public int getLineLength() {
		return LINE_LENGTH;
	}

	public int getIndent() {
		return INDENT;
	}

	public void setIndent( int indent) {}
}