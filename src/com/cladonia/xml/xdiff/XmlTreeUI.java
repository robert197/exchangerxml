/*
 * $Id: XmlTreeUI.java,v 1.1 2004/09/09 15:50:54 knesbitt Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xml.xdiff;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;
import javax.swing.tree.VariableHeightLayoutCache;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The View part for the Xml Tree component.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/09/09 15:50:54 $
 * @author Dogsbay
 * @author Dogs bay
 */
public class XmlTreeUI extends MetalTreeUI {

    protected static XmlTreeUI treeUI = new XmlTreeUI();

    public static ComponentUI createUI(JComponent c) {
	    return new XmlTreeUI();
    }

    /**
     * Paints the expand (toggle) part of a row. The reciever should
     * NOT modify <code>clipBounds</code>, or <code>insets</code>.
     */
    protected void paintExpandControl(Graphics g,
				      Rectangle clipBounds, Insets insets,
				      Rectangle bounds, TreePath path,
				      int row, boolean isExpanded,
				      boolean hasBeenExpanded,
				      boolean isLeaf) {

		Object value = path.getLastPathComponent();

		// Draw icons if not a leaf and either hasn't been loaded,
		// or the model child count is > 0.
		if (!isLeaf && (!hasBeenExpanded || treeModel.getChildCount(value) > 0)) {
		    int x = bounds.x - (getRightChildIndent() - 1);
		    int y = bounds.y;
			
			Icon icon = null;

		    if ( isExpanded) {
				icon = getExpandedIcon();
		    } else {
				icon = getCollapsedIcon();
		    }

			// Draws the icon horizontally centered at (x,y)
			if ( icon != null) {
				icon.paintIcon( tree, g, x - icon.getIconWidth()/2, y);
			}
		}
    }
	
    protected void paintRow(Graphics g, Rectangle clipBounds,
    		    Insets insets, Rectangle bounds, TreePath path,
    		    int row, boolean isExpanded,
    		    boolean hasBeenExpanded, boolean isLeaf) {
	    // Don't paint the renderer if editing this row.
	    if ( editingComponent != null && editingRow == row) {
	        return;
	    }
		
		Object object = path.getLastPathComponent();
		
		Component component = currentCellRenderer.getTreeCellRendererComponent( tree, 
								object, tree.isRowSelected( row), 
								isExpanded, isLeaf, row, false); // hasfocus???
		
		if ( object instanceof XmlElementNode && !((XmlElementNode)object).isEndTag())
		{
			ImageIcon icon = ((XmlElementNode)object).getDiffIcon();
			if (icon != null)
			{
				int x = bounds.x - (getRightChildIndent() - 1);
			    int y = bounds.y;
				//icon.paintIcon(tree, g, x - icon.getIconWidth()/2, y);
			    icon.paintIcon(tree, g, x+icon.getIconWidth(), y+icon.getIconHeight()/2);
			}
		}

		// don't indent the end-tag as far...
		if ( object instanceof XmlElementNode && ((XmlElementNode)object).isEndTag()) {
			int indent = getLeftChildIndent()+getRightChildIndent();
			rendererPane.paintComponent( g, component, tree, bounds.x-indent, bounds.y, bounds.width, bounds.height, true);	
		} else {
		    rendererPane.paintComponent( g, component, tree, bounds.x, bounds.y, bounds.width, bounds.height, true);	
		}
    }

    protected AbstractLayoutCache createLayoutCache() {
	    return new VariableHeightLayoutCache();
    }

    protected void installDefaults() {
		super.installDefaults();

	    setExpandedIcon( XngrImageLoader.get().getImage( "com/cladonia/xml/viewer/icons/ExpandedIcon.gif"));
    	setCollapsedIcon( XngrImageLoader.get().getImage( "com/cladonia/xml/viewer/icons/CollapsedIcon.gif"));

		setLeftChildIndent( 8);
		setRightChildIndent( 12);
    }
}
