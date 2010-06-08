/*
 * $Id: StepAction.java,v 1.1 2004/03/25 18:58:13 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.DebugController;

/**
 * An action that can be used to step with the debugger.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:58:13 $
 * @author Dogsbay
 */
 public class StepAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private DebugController debugger = null;
	
 	/**
	 * The constructor for the action which steps with the debugger.
	 */
 	public StepAction() {
 		super( "Step");

		putValue( MNEMONIC_KEY, new Integer( 't'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/StepForward16.gif"));
		putValue( SHORT_DESCRIPTION, "Step to next line");
 	}
 	
	public void setDebugger( DebugController debugger) {
		this.debugger = debugger;
	}
 	
	/**
	 * The implementation of the step action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		debugger.step();
 	}
 }
