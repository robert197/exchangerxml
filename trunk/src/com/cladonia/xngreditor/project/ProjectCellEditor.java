/*
 * $Id: ProjectCellEditor.java,v 1.1 2004/03/25 18:54:53 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.awt.Component;
import java.awt.Dimension;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultTreeCellEditor;

/**
 * The cell editor for a Base Project Node.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:54:53 $
 * @author Dogsbay
 */
public class ProjectCellEditor extends DefaultTreeCellEditor { //implements ActionListener, TreeCellEditor, TreeSelectionListener {
	private ProjectCellRenderer renderer = null; 

    /**
     * Constructs a project cell editor.
     *
     * @param tree a JTree object
     * @param renderer the Project TreeCellRenderer
     */
    public ProjectCellEditor( JTree tree, ProjectCellRenderer renderer) {
		super( tree, null, new RealCellEditor( renderer));
		
		this.renderer = renderer;
    }

    /**
     * Returns the editor component.
     */
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
		if ( value instanceof BaseNode) {
			if ( selected) {
				if ( expanded) {
					editingIcon = ((BaseNode)value).getExpandedSelectedIcon();
				} else {
					editingIcon = ((BaseNode)value).getSelectedIcon();
				}
			} else {
				if ( expanded) {
					editingIcon = ((BaseNode)value).getExpandedIcon();
				} else {
					editingIcon = ((BaseNode)value).getIcon();
				}
			}
		}
		Component comp = super.getTreeCellEditorComponent( tree, value, selected, expanded, leaf, row);
		
		if ( editingComponent instanceof JTextField) {
			((JTextField)editingComponent).setText( ((BaseNode)value).getName());
			((JTextField)editingComponent).selectAll();
		}
		return comp;
    }

	// Overrides the stuff in DefaultTreeCellEditor
    protected void determineOffset( JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
	    offset = 16;
    }
	
    private static class RealCellEditor extends DefaultCellEditor {
		public RealCellEditor( ProjectCellRenderer renderer) {
			super( new DefaultTextField( renderer));
		}
	    public boolean shouldSelectCell(EventObject event) {
	    	boolean retValue = super.shouldSelectCell(event);
	    	getComponent().requestFocus();

	    	return retValue;
		}
    }

    private static class DefaultTextField extends JTextField {
		ProjectCellRenderer renderer = null;

	    public DefaultTextField( ProjectCellRenderer renderer) {
			this.renderer = renderer;
			
			setFont( renderer.getFont());
			setBorder( new LineBorder( UIManager.getColor( "controlDkShadow"), 1));
	    }
		
	    public Dimension getPreferredSize() {
	        Dimension size = super.getPreferredSize();
	    	Dimension rSize = renderer.getPreferredSize();
	    	size.height = rSize.height;

	        return size;
	    }
    }
}
