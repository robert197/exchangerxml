/*
 * $Id: PauseAction.java,v 1.2 2004/05/28 15:38:21 edankert Exp $
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
 * An action that can be used to pause the debugger.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
 public class PauseAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private DebugController debugger = null;
	
 	/**
	 * The constructor for the action which pauses the debugger.
	 */
 	public PauseAction() {
 		super( "Pause");

		putValue( MNEMONIC_KEY, new Integer( 'P'));
		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_F5, InputEvent.CTRL_MASK, false));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Pause16.gif"));
		putValue( SHORT_DESCRIPTION, "Pause Debugger");
 	}
 	
	public void setDebugger( DebugController debugger) {
		this.debugger = debugger;
	}

	/**
	 * The implementation of the pause debugger action, called 
	 * after a user action.
	 *
	 * @param the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
 		debugger.pauseDebugger();
 	}
 }
