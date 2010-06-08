/*
 * $Id: ViewPanel.java,v 1.1 2004/03/25 18:52:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.LayoutManager;

import javax.swing.JPanel;

/**
 * The base panel for a view component.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:52:46 $
 * @author Dogsbay
 */
public abstract class ViewPanel extends JPanel {
	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewPanel() {
		super();
	}

	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewPanel( boolean isDoubleBuffered) {
		super( isDoubleBuffered);
	}

	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewPanel( LayoutManager layout) {
		super( layout);
	}

	/**
	 * Constructor, calls the super constructor.
	 */
	public ViewPanel( LayoutManager layout, boolean isDoubleBuffered) {
		super( layout, isDoubleBuffered);
	}

	public abstract void setFocus();
	public abstract void updatePreferences();
	public abstract void setProperties();
} 
