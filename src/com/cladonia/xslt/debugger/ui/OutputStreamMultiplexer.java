/*
 * $Id: OutputStreamMultiplexer.java,v 1.2 2004/05/12 11:24:52 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xslt.debugger.ui;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

/**
 * This LogOutputStream is used to ...
 *
 * @version $Revision: 1.2 $, $Date: 2004/05/12 11:24:52 $
 * @author Dogsbay
 */
class OutputStreamMultiplexer extends OutputStream {
	private OutputStream current = null;
	private Vector streams = null;
	
	public OutputStreamMultiplexer( OutputStream initial) {
		streams = new Vector();
		streams.addElement( initial);
		
		current = initial;
	}
	
	public void open( OutputStream stream) {
		flush();
		
		if ( current != stream) {
			streams.addElement( stream);
			current = stream;
		}
	}

	public void closeCurrent() {
		flush();

		streams.removeElement( streams.lastElement());
		this.current = (OutputStream)streams.lastElement();
	}

	public void write( int b) {
		try {
			current.write( (char)b);
		} catch ( IOException e) {
			e.printStackTrace();
		}
	}

	public void flush() {
		try {
			if ( current != null) {
				current.flush();
			}
		} catch ( IOException e) {
			e.printStackTrace();
		}
	}
}
