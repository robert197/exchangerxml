/*
 * $Id: XmlCellRenderer.java,v 1.3 2004/09/28 13:54:17 edankert Exp $
 *
 * Copyright (C) 2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import java.awt.Component;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.TreeCellRenderer;

/**
 * The cell renderer for a XmlElementNode.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/09/28 13:54:17 $
 * @author Dogsbay
 */
public class XmlCellRenderer extends JLabel implements TreeCellRenderer {
	private static final boolean DEBUG = false;

	private boolean selected = false;
	
	private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private XmlElementNode element = null;
	private Navigator navigator = null;

	private boolean showValue = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public XmlCellRenderer( Navigator navigator) {
		this.navigator = navigator;
	}

	/**
	 * Sets the look and feel to the Jump Label UI look and feel.
	 * Override this method if you want to install a different UI.
	 */
	public void updateUI() {
	    setUI( XmlCellRendererUI.createUI( this));
	}

	/**
	  * Configures the renderer based on the passed in components.
	  * The value is set from messaging the tree with
	  * <code>convertValueToText</code>, which ultimately invokes
	  * <code>toString</code> on <code>value</code>.
	  * The foreground color is set based on the selection and the icon
	  * is set based on on leaf and expanded.
	  */
	public Component getTreeCellRendererComponent( JTree tree, Object value,
						  boolean selected, boolean expanded, boolean leaf, 
						  int row,  boolean focus) {
						  
		this.selected = selected;
		
		if ( value instanceof XmlElementNode) {
			element = (XmlElementNode)value;
			
			setToolTipText( element.getDescription());
		} 

		if ( selected) {
		    setForeground( UIManager.getColor("Tree.selectionForeground"));
		    setBackground( UIManager.getColor("Tree.selectionBackground"));
		} else  {
		    setForeground( tree.getForeground());
		    setBackground( tree.getBackground());
		}

	    setComponentOrientation( tree.getComponentOrientation());
		setEnabled( tree.isEnabled());
		setFont( tree.getFont());

		return this;
	}
	
	public Icon getIcon() {
		return element != null ? element.getIcon() : null;
	}
	
	public String getName() {
		return element != null ? element.getName() : null;
	}

	public Vector getAttributes() {
		return element != null ? element.getAttributes() : null;
	}

	public String getValue() {
		return element != null ? element.getValue() : null;
	}

	public boolean isSelected() {
		return selected;
	}

	public boolean showAttributeNames() {
		return navigator.showAttributeNames();
	}

	public boolean showElementNames() {
		return navigator.showElementNames();
	}
} 
