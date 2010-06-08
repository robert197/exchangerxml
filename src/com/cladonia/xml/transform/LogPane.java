/*
 * $Id: LogPane.java,v 1.2 2004/10/18 16:07:14 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.transform;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;

import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows parsing error information.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/10/18 16:07:14 $
 * @author Dogsbay
 */
 public class LogPane extends JTextArea {
	private PrintStream prevErr = null;
 	
 	public LogPane() {
		setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
 	}
	
 	public void startLogging( String text) {
 		setText( text+"\n");
		
		prevErr = System.err;

 		System.setErr( new PrintStream( new OutputStream() {
 			private StringBuffer buffer = new StringBuffer();

 			public void write(int b) {
 				buffer.append( (char)b);
 			}
 	
 			public void flush () {
 				append( buffer.toString());
 				buffer = new StringBuffer();
 			}
 		}, true));

 	}

 	public void stopLogging( String text) {
		System.setErr( prevErr);	

 		append( "\n"+text);
 	}

 	public void clear() {
 		setText("");
 	}

 	public void updatePreferences() {
 		setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
 	}
 	
 	public void append( String text) {
 		super.append(text);
 		
 		Element root = getDocument().getDefaultRootElement();
 		Element last = root.getElement( root.getElementCount()-1);
 		final int start = last.getStartOffset();

 		if ( SwingUtilities.isEventDispatchThread()) {
 	 		setCaretPosition( start);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
			 		setCaretPosition( start);
				}
			});
		}
 	}
}
