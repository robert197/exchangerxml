/*
 * $Id: BreakpointCellRenderer.java,v 1.2 2004/05/28 15:38:21 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.bounce.image.ImageUtilities;

import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.Breakpoint;

/**
 * The cell renderer component for an ElementNode.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/28 15:38:21 $
 * @author Dogsbay
 */
public class BreakpointCellRenderer extends JPanel implements ListCellRenderer {
	private static final boolean DEBUG = false;
	
	private static final ImageIcon ENABLED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointEnabledIcon.gif");
	private static final ImageIcon DISABLED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointDisabledIcon.gif");

	private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private JLabel icon = null;
//	private JPanel selectablePanel = null;
	private JLabel location = null;
	private JLabel linenumber = null;

	private boolean selected = false;
	private boolean showValue = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public BreakpointCellRenderer() {
		super( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
		icon = new JLabel();
		icon.setBorder( new EmptyBorder( 0, 2, 0, 2));

		location = new JLabel();
		location.setBorder( new EmptyBorder( 0, 2, 0, 2));
		location.setOpaque( false);
		
		linenumber = new JLabel();
		linenumber.setBorder( new EmptyBorder( 0, 2, 0, 2));
		linenumber.setOpaque( false);
		linenumber.setFont( linenumber.getFont().deriveFont( Font.PLAIN));
		linenumber.setForeground( Color.gray);

		this.add( icon);
		this.add( linenumber);
		this.add( location);
	}
	
	public void setPreferredFont( Font font) {
		location.setFont( font.deriveFont( Font.PLAIN));
		linenumber.setFont( font.deriveFont( Font.PLAIN));
	}
	
	public ImageIcon getIcon( boolean enabled) {
		if ( enabled) {
			return ENABLED_ICON;
		}

		return DISABLED_ICON;
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public ImageIcon getSelectedIcon( boolean enabled) {
		ImageIcon icon = getIcon( enabled);

		if ( icon != null) {
			icon = ImageUtilities.createDarkerImage( icon);
		}
		
		return icon;
	}

	public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
		if ( node instanceof Breakpoint) {
			Breakpoint bp = (Breakpoint)node;
			
			if ( bp != null) {
				location.setText( bp.getFilename());
				linenumber.setText( "["+bp.getLineNumber()+"]");
			} else {
				location.setText( "");
				linenumber.setText( ""+-1);
			}

			if ( selected) {
				icon.setIcon( getSelectedIcon( bp.isEnabled()));
				
				location.setForeground( list.getSelectionForeground());
				setBackground( list.getSelectionBackground());
			} else {
				icon.setIcon( getIcon( bp.isEnabled()));

				location.setForeground( list.getForeground());
				setBackground( list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
		} else {
			location.setText( "");
			linenumber.setText( ""+-1);
		}

		return this;
	}
} 
