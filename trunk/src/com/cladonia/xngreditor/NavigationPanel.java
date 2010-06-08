/*
 * $Id: NavigationPanel.java,v 1.1 2004/03/25 18:52:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.CardLayout;

import javax.swing.JPanel;

/**
 * The dialog that shows the eXchaNGeR identity.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:52:46 $
 * @author Dogsbay
 */
public class NavigationPanel extends JPanel {
	CardLayout layout = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public NavigationPanel() {
		super();
		
//		setBorder( new BevelBorder( BevelBorder.LOWERED, Color.white, new Color( 204, 204, 204), new Color( 204, 204, 204), new Color( 102, 102, 102)));
		layout = new CardLayout();
		
		setLayout( layout);
	}

	public void show( String identifier) {
		layout.show( this, identifier);
	}

//	public void showDocument( String identifier) {
//		layout.show( this, identifier);
//	}
} 
