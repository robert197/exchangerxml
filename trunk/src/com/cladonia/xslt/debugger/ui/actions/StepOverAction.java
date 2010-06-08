/*
 * $Id: StepOverAction.java,v 1.3 2004/05/28 15:38:21 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.DebugController;

/**
 * An action that can be used to perform a step over on the debugger.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class StepOverAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private DebugController debugger = null;
	
 	/**
	 * The constructor for the action which performs a step over on the debugger.
	 */
 	public StepOverAction() {
 		super( "Step Over");

		putValue( MNEMONIC_KEY, new Integer( 'O'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/StepOver16.gif"));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F10, 0, false));
		putValue( SHORT_DESCRIPTION, "Step Over");
 	}
 	
	public void setDebugger( DebugController debugger) {
		this.debugger = debugger;
	}
 	
	/**
	 * The implementation of the step over action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		debugger.stepOver();
 	}
 }
