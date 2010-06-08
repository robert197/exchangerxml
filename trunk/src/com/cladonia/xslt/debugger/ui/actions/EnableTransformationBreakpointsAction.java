/*
 * $Id: EnableTransformationBreakpointsAction.java,v 1.1 2004/05/23 14:46:43 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;

import com.cladonia.xslt.debugger.Breakpoint;
import com.cladonia.xslt.debugger.BreakpointList;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to disable a breakpoint.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/05/23 14:46:43 $
 * @author Dogsbay
 */
 public class EnableTransformationBreakpointsAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private XSLTDebuggerPane debugger = null;
	
 	/**
	 * The constructor for the action which allows enabling of 
	 * breakpoints for the xslt debugger.
	 */
 	public EnableTransformationBreakpointsAction( XSLTDebuggerPane debugger) {
 		super( "Enable All Breakpoints");

		putValue( MNEMONIC_KEY, new Integer( 'E'));
		putValue( SHORT_DESCRIPTION, "Enable all Breakpoints");
		
		this.debugger = debugger;
 	}
 	
	/**
	 * The implementation of the enable breakpoint action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		InputView view = debugger.getXSLTView();
 		BreakpointList list = view.getBreakPoints();
 		Vector breakpoints = list.getBreakpoints();
 		
 		for ( int i = 0; i < breakpoints.size(); i++) {
 			((Breakpoint)breakpoints.elementAt(i)).setEnabled( true);
 		}

 		debugger.breakPointsUpdated( view);
 		view.updateBreakpoints();

 		view = debugger.getInputView();
 		list = view.getBreakPoints();
 		breakpoints = list.getBreakpoints();
 		
 		for ( int i = 0; i < breakpoints.size(); i++) {
 			((Breakpoint)breakpoints.elementAt(i)).setEnabled( true);
 		}
 	
 		debugger.breakPointsUpdated( view);
 		view.updateBreakpoints();
 	}
}
