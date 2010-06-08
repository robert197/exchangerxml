/*
 * $Id: DebugScenarioAction.java,v 1.8 2004/10/04 16:24:27 knesbitt Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;

/**
 * An action that can be used to open a XML Document.
 *
 * @version	$Revision: 1.8 $, $Date: 2004/10/04 16:24:27 $
 * @author Dogsbay
 */
public class DebugScenarioAction extends AbstractAction {
 	private static final boolean DEBUG = false;
	
	private ExchangerEditor parent = null;
	private ConfigurationProperties properties = null;
	private XSLTDebuggerFrame frame = null;

 	/**
	 * The constructor for the action which debugs a scenario.
	 *
	 * @param editor the applications frame.
	 * @param props the configuration properties.
	 */
 	public DebugScenarioAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "XSLT Debugger");
// 		super( parent, props, "Open");

		this.parent = parent;
		this.properties = props;

		putValue( MNEMONIC_KEY, new Integer( 'b'));
		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/DebugScenario16.gif"));
		putValue( SHORT_DESCRIPTION, "Debug a XSLT Transformation");
 	}
 	
 	/**
 	 * The implementation of the debug scenario action.
 	 *
 	 * @param the action event.
 	 */
 	public void actionPerformed( ActionEvent e) {
 		
 		parent.getDebugger().setVisible(true);
 		
 		String activeConfig = properties.getKeyPreferences().getActiveConfiguration();
		properties.getKeyPreferences().setKeyMappings(parent,activeConfig);
		
		parent.getDebugger().setVisible(true);

		ExchangerView view = parent.getView();
		
		if ( view != null) {
			view.getCurrentView().setFocus();
		}
 	}
}
