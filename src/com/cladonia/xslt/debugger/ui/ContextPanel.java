/*
 * $Id: ContextPanel.java,v 1.3 2004/05/11 17:30:24 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;

/**
 * This StacksPanel, shows the style, input and output stacks.
 *
 * @version $Revision: 1.3 $, $Date: 2004/05/11 17:30:24 $
 * @author Dogsbay
 */
public class ContextPanel extends DetailsPanel {
	private XSLTDebuggerPane debugger = null;

//	private TraceTableModel traceModel = null;

	/**
	 * Constructs a new Variables Panel
	 */
	public ContextPanel( XSLTDebuggerPane debugger) {
		super( "Context", null);

		this.debugger = debugger;

//		traceModel = new TraceTableModel();
//		
//		JTable table = new JTable( traceModel);
//		JScrollPane scroller = new JScrollPane(	table,
//										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		scroller.setPreferredSize( new Dimension( 100, 100));
//		
//		scroller.getViewport().setBackground( table.getBackground());
		JPanel contextPanel = new JPanel( new BorderLayout());
		contextPanel.setPreferredSize( new Dimension( 100, 100));
		contextPanel.setBackground( Color.white);

		setCenterComponent( contextPanel);
	}
	
	public void setContext( Object context) {
//		System.out.println( "TracePanel.setContext( "+context+")");
	}
}
