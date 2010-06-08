/*
 * $Id: XmlCellRenderer.java,v 1.1 2004/03/25 18:50:40 edankert Exp $
 *
 * The contents of this file are subject to the Mozilla Public License 
 * Version 1.1 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at 
 * http://www.mozilla.org/MPL/ 
 *
 * Software distributed under the License is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License 
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is eXchaNGeR Skeleton code. (org.xngr.skeleton.*)
 *
 * The Initial Developer of the Original Code is Cladonia Ltd.. Portions created 
 * by the Initial Developer are Copyright (C) 2002 the Initial Developer. 
 * All Rights Reserved. 
 *
 * Contributor(s): Dogsbay
 */
package com.cladonia.xml.viewer;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.TreeCellRenderer;

import com.cladonia.xml.viewer.XmlElementNode.Line;

/**
 * The cell renderer for a XmlElementNode.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:50:40 $
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
