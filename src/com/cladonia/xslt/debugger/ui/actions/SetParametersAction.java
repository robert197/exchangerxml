/*
 * $Id: SetParametersAction.java,v 1.2 2004/05/31 10:42:31 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.cladonia.xngreditor.scenario.ParameterManagementDialog;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerPane;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;
import com.cladonia.xslt.debugger.ui.XSLTTransformation;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/31 10:42:31 $
 * @author Dogsbay
 */
public class SetParametersAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ParameterManagementDialog parameterManagementDialog = null;

	private XSLTDebuggerFrame parent = null;
	private XSLTDebuggerPane debugger = null;

 	/**
	 * The constructor for the action which allows opening 
	 * of XML Documents.
	 *
	 * @param parent the parent frame.
	 */
 	public SetParametersAction( XSLTDebuggerFrame parent, XSLTDebuggerPane debugger) {
 		super( "Set Parameters");

		this.parent = parent;
		this.debugger = debugger;

		putValue( MNEMONIC_KEY, new Integer( 'P'));
		putValue( SHORT_DESCRIPTION, "Set Parameters");
 	}
 	
	private ParameterManagementDialog getParameterManagementDialog() {
		if ( parameterManagementDialog == null) {
			parameterManagementDialog = new ParameterManagementDialog( parent);
		} 
		
		return parameterManagementDialog;
	}

	/**
 	 * The implementation of the open grammar action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		execute();
 	}
 	
 	public void execute() {
 		ParameterManagementDialog dialog = getParameterManagementDialog();
 		XSLTTransformation transformation = debugger.getTransformation();
 		
 		if ( transformation != null) {
	 		dialog.setParameters( transformation.getParameters());
	 		dialog.setVisible(true);
	 		transformation.setParameters( dialog.getParameters());
	 		debugger.updateParameters();
 		}
 	}
}
