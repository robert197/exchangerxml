/*
 * $Id: BreakpointList.java,v 1.3 2005/08/29 08:33:24 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * List of breakpoints.
 *
 * @version	$Revision: 1.3 $, $Date: 2005/08/29 08:33:24 $
 * @author Dogsbay
 */
public class BreakpointList {
	private static boolean DEBUG = false;
	private Hashtable breakpoints = null;
	
	/**
	 * Constructs a list of breakpoints.
	 */
	public BreakpointList() {
		if (DEBUG) System.out.println( "BreakpointList()");
		breakpoints = new Hashtable();
	}
	
	/**
	 * Add a breakpoint to the list.
	 * 
	 * @param filename the breakpoint filename.
	 * @param lineNumber the breakpoint line number.
	 */
	public synchronized void addBreakpoint( String filename, int lineNumber) {
		if (DEBUG) System.out.println( "BreakpointList.addBreakpoint( "+filename+", "+lineNumber+")");

		Vector breaks = getBreakpointList( filename);
		
		breaks.addElement( new Breakpoint( filename, lineNumber, true));
	}

	/**
	 * List of breakpoints for the specified filename.
	 * Creates an empty list if no breakpoints exist.
	 * 
	 * @param filename the breakpoint list filename.
	 * 
	 * @return a list of breakpoints for the specified file.
	 */
	private Vector getBreakpointList( String filename) {
		
		filename = Breakpoint.normalizeFilename( filename);

//		System.out.println( "BreakpointList.getBreakpointList( "+filename+")");

		Vector breaks = (Vector)breakpoints.get( filename);
		
		if ( breaks == null) {
			breaks = new Vector();
			breakpoints.put( filename, breaks);
		}
		
		return breaks;
	}

	/**
	 * Add a breakpoint to the list.
	 * 
	 * @param breakpoint the breakpoint to add.
	 */
	public synchronized void addBreakpoint( Breakpoint breakpoint) {
		if (DEBUG) System.out.println( "BreakpointList.addBreakpoint( "+breakpoint+")");

		Vector breaks = getBreakpointList( breakpoint.getFilename());
		breaks.addElement( breakpoint);
	}

	/**
	 * Remove a breakpoint from the list.
	 * 
	 * @param filename the breakpoint filename.
	 * @param lineNumber the breakpoint line number.
	 */
	public synchronized void removeBreakpoint( String filename, int lineNumber) {
		if (DEBUG) System.out.println( "BreakpointList.removeBreakpoint( "+filename+", "+lineNumber+")");

		Breakpoint breakpoint = getBreakpoint( filename, lineNumber);

		if ( breakpoint != null) {
			removeBreakpoint( breakpoint);
		}
	}

	/**
	 * Remove a breakpoint from the list.
	 * 
	 * @param breakpoint the breakpoint to remove.
	 */
	public synchronized void removeBreakpoint( Breakpoint breakpoint) {
		if (DEBUG) System.out.println( "BreakpointList.removeBreakpoint( "+breakpoint+")");

		Vector breaks = getBreakpointList( breakpoint.getFilename());
		breaks.removeElement( breakpoint);
	}

	/**
	 * Does a breakpoint for this filename and 
	 * linenumber exist in the list.
	 * 
	 * @param filename the filename for the breakpoint.
	 * @param lineNumber the line number for the breakpoint.
	 */
	public synchronized boolean isBreakpoint( String filename, int lineNumber) {
		Breakpoint breakpoint = getBreakpoint( filename, lineNumber);
		
		if (DEBUG) System.out.println( "BreakpointList.isBreakpoint( "+filename+", "+lineNumber+") ["+(breakpoint != null)+"]");

		//return breakpoint != null;
		if (breakpoint != null && breakpoint.isEnabled() == true)
		  return true;
		else
		  return false;
	}
	
	/**
	 * Get a breakpoint for this filename and 
	 * linenumber from the list.
	 * 
	 * @param filename the filename for the breakpoint.
	 * @param lineNumber the line number for the breakpoint.
	 */
	public synchronized Breakpoint getBreakpoint( String filename, int lineNumber){
		if (DEBUG) System.out.println( "BreakpointList.getBreakpoint( "+filename+", "+lineNumber+")");

		Vector breaks = getBreakpointList( filename);

		for ( int i = 0; i < breaks.size(); i++) {
			Breakpoint breakpoint = (Breakpoint)breaks.elementAt( i);
			
			if ( breakpoint.sameFile( filename) && breakpoint.getLineNumber() == lineNumber) {
				return breakpoint;
			}
		}
		
		return null;
	}
	
	/**
	 * Get all the breakpoints in this list.
	 * 
	 * @return list of breakpoints.
	 */
	public Vector getBreakpoints() {
		Vector bps = new Vector();
		
		Enumeration enumeration = breakpoints.elements();
		
		while ( enumeration.hasMoreElements()) {
			Vector breaks = (Vector)enumeration.nextElement();
			
			for ( int i = 0; i < breaks.size(); i++) {
				bps.addElement( breaks.elementAt(i));
			}
		}
		
		return bps;
	}
}




