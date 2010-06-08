/*
 * $Id: StepOutAction.java,v 1.2 2004/05/28 15:38:21 edankert Exp $
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

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.DebugController;

/**
 * An action that can be used to step with the debugger.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class StepOutAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private DebugController debugger = null;
	
 	/**
	 * The constructor for the action which steps with the debugger.
	 */
 	public StepOutAction() {
 		super( "Step Out");

		putValue( MNEMONIC_KEY, new Integer( 't'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F11, InputEvent.SHIFT_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/StepOut16.gif"));
		putValue( SHORT_DESCRIPTION, "Step Out");
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
 		debugger.stepToTemplateEnd();
 	}
 }
