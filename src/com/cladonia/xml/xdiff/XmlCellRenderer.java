/*
 * $Id: XmlCellRenderer.java,v 1.1 2004/09/09 15:50:54 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.xdiff;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.cladonia.xml.xdiff.XmlElementNode.Line;

/**
 * The cell renderer for the XML Diff tree
 *
 * @version	$Revision: 1.1 $, $Date: 2004/09/09 15:50:54 $
 * @author Dogsbay
 */
public class XmlCellRenderer extends JLabel implements TreeCellRenderer {
	private static final boolean DEBUG = false;
	
	private boolean selected = false;
	private Line[] lines = null;
	
	public XmlCellRenderer() {
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
						  int row,  boolean hasFocus) {
						  
		this.selected = selected;
		
		if ( value instanceof XmlElementNode) {
			XmlElementNode node = (XmlElementNode)value;
			
			this.lines = node.getLines();
			
			if ( selected) {
			    setForeground( UIManager.getColor("Tree.selectionForeground"));
			} else  {
			    setForeground( UIManager.getColor("Tree.textForeground"));
			}
			
		    setComponentOrientation( tree.getComponentOrientation());
		} 

		return this;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public Line[] getLines() {
		return lines;
	}
} 
