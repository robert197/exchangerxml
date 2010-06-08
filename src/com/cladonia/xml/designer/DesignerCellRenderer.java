/*
 * $Id: DesignerCellRenderer.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

/**
 * The cell renderer for a XMLTreeNode.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public class DesignerCellRenderer implements TreeCellRenderer {
	private static final boolean DEBUG = false;
	
	private static ElementCellRenderer elementRenderer = null;
	private static AttributeCellRenderer attributeRenderer = null;
	private static JTextField editor = null;

	private Designer designer = null;
//	private Font plainFont = null;
//	private Font boldFont = null;
//	private Font italicFont = null;
	private boolean selected = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public DesignerCellRenderer( Designer designer) {
		this.designer = designer;
//		boldFont = getFont();
//		plainFont = getFont().deriveFont( Font.PLAIN);
//		italicFont = getFont().deriveFont( Font.ITALIC);
	}
	
	/**
	  * Configures the renderer based on the passed in components.
	  * The value is set from messaging the tree with
	  * <code>convertValueToText</code>, which ultimately invokes
	  * <code>toString</code> on <code>value</code>.
	  * The foreground color is set based on the selection and the icon
	  * is set based on on leaf and expanded.
	  */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
						  boolean selected,
						  boolean expanded,
						  boolean leaf, int row,
						  boolean hasFocus) {
		this.selected = selected;
		JComponent component = null;
		
		if ( value instanceof ElementNode) {
			ElementNode node = (ElementNode)value;
			
			component = getElementRenderer();
			((ElementCellRenderer)component).setValue( node, selected || hasFocus);
			
			component.setToolTipText( node.getDescription());
		    component.setComponentOrientation( tree.getComponentOrientation());
		} else if ( value instanceof AttributeNode) {
			AttributeNode node = (AttributeNode)value;
			
			component = getAttributeRenderer();
			((AttributeCellRenderer)component).setValue( node, selected || hasFocus);
			
			component.setToolTipText( node.getDescription());
			component.setComponentOrientation( tree.getComponentOrientation());
		} else { // should not happen
			component = new JLabel();
		}
		
		return component;
	}

	public void setFont( Font font) {
		getAttributeRenderer().setPreferredFont( font);
		getElementRenderer().setPreferredFont( font);
	}

	private JTextField getEditor( ElementNode node) {
		if ( editor == null) {
			editor = new JTextField();
		}
		
		editor.setText( node.getName());
		
		return editor;
	}

	private ElementCellRenderer getElementRenderer() {
		if ( elementRenderer == null) {
			elementRenderer = new ElementCellRenderer();
		}
		
		return elementRenderer;
	}

	private AttributeCellRenderer getAttributeRenderer() {
		if ( attributeRenderer == null) {
			attributeRenderer = new AttributeCellRenderer();
		}
		
		return attributeRenderer;
	}
	
	public void updatePreferences() {
		getElementRenderer().setShowValue( designer.isShowElementValues());
		getAttributeRenderer().setShowValue( designer.isShowAttributeValues());
	}

	/**
	 * Paints this cell.
	 *
	 * @param g the graphics object.
	 */
//	public void paint(Graphics g) {
//
//		if ( selected) {
//			int imageOffset = getLabelStart();
//
//		    Color bsColor = UIManager.getColor("Tree.selectionBorderColor");
//
//		    if (bsColor != null) {
//				g.setColor(bsColor);
//				if(getComponentOrientation().isLeftToRight()) {
//				    g.drawRect(imageOffset, 0, getWidth() - 1 - imageOffset,
//					       getHeight() - 1);
//				} else {
//				    g.drawRect(0, 0, getWidth() - 1 - imageOffset,
//					       getHeight() - 1);
//				}
//		    }
//		}
//		super.paint(g);
//	}
	
} 
