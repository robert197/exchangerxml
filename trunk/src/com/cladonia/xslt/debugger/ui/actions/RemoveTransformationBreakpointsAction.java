/*
 * $Id: RemoveTransformationBreakpointsAction.java,v 1.2 2004/05/28 15:38:21 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.KeyStroke;

import com.cladonia.xslt.debugger.Breakpoint;
import com.cladonia.xslt.debugger.BreakpointList;
import com.cladonia.xslt.debugger.ui.InputView;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;

/**
 * An action that can be used to enable a breakpoint.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class RemoveTransformationBreakpointsAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private XSLTDebuggerPane debugger = null;
	
 	/**
	 * The constructor for the action which allows enabling of 
	 * breakpoints for the xslt debugger.
	 */
 	public RemoveTransformationBreakpointsAction( XSLTDebuggerPane debugger) {
 		super( "Remove All Breakpoints");

		putValue( MNEMONIC_KEY, new Integer( 'R'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F9, InputEvent.CTRL_MASK + InputEvent.SHIFT_MASK, false));
		putValue( SHORT_DESCRIPTION, "Remove all Breakpoints");
		
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
 			list.removeBreakpoint( (Breakpoint)breakpoints.elementAt(i));
 		}

 		debugger.breakPointsUpdated( view);
 		view.updateBreakpoints();
 		
 		view = debugger.getInputView();
 		list = view.getBreakPoints();
 		breakpoints = list.getBreakpoints();
 		
 		for ( int i = 0; i < breakpoints.size(); i++) {
 			list.removeBreakpoint( (Breakpoint)breakpoints.elementAt(i));
 		}
 		
 		debugger.breakPointsUpdated( view);
 		view.updateBreakpoints();
 	}
}
