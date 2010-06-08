/*
 * $Id: ViewTreePanel.java,v 1.1 2004/03/25 18:52:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.LayoutManager;

/**
 * The base view panel for a Tree Panel.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:52:46 $
 * @author Dogsbay
 */
public abstract class ViewTreePanel extends ViewPanel {
	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewTreePanel() {
		super();
	}

	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewTreePanel( boolean isDoubleBuffered) {
		super( isDoubleBuffered);
	}

	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewTreePanel( LayoutManager layout) {
		super( layout);
	}

	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewTreePanel( LayoutManager layout, boolean isDoubleBuffered) {
		super( layout, isDoubleBuffered);
	}

	public abstract void expandAll();
	public abstract void collapseAll();
} 
