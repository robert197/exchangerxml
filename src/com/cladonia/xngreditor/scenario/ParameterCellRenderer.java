/*
 * $Id: ParameterCellRenderer.java,v 1.2 2004/05/31 17:53:28 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

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


/**
 * The cell renderer component for an ElementNode.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/05/31 17:53:28 $
 * @author Dogsbay
 */
public class ParameterCellRenderer extends JPanel implements ListCellRenderer {
	private static final boolean DEBUG = false;
	
//	private static final ImageIcon ENABLED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointEnabledIcon.gif");
//	private static final ImageIcon DISABLED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointDisabledIcon.gif");
//
	private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private JLabel equals = null;
//	private JPanel selectablePanel = null;
	private JLabel name = null;
	private JLabel value = null;

	private boolean selected = false;
	private boolean showValue = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public ParameterCellRenderer() {
		super( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
//		icon = new JLabel();

		name = new JLabel();
		name.setBorder( new EmptyBorder( 0, 0, 0, 0));
		name.setOpaque( false);
		name.setFont( name.getFont().deriveFont( Font.BOLD));
		name.setForeground( Color.black);
		
		equals = new JLabel();
		equals.setBorder( new EmptyBorder( 0, 2, 0, 2));
		equals.setOpaque( false);
		equals.setFont( equals.getFont().deriveFont( Font.PLAIN));
		equals.setText( "=");

		value = new JLabel();
		value.setBorder( new EmptyBorder( 0, 0, 0, 0));
		value.setOpaque( false);
		value.setFont( value.getFont().deriveFont( Font.PLAIN));

//		this.add( icon);
		this.add( name);
		this.add( equals);
		this.add( value);
	}
	
	public void setPreferredFont( Font font) {
		name.setFont( font.deriveFont( Font.BOLD));
		value.setFont( font.deriveFont( Font.PLAIN));
	}
	
	public ImageIcon getIcon( boolean enabled) {
//		if ( enabled) {
//			return ENABLED_ICON;
//		}
//
//		return DISABLED_ICON;
		return null;
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
		if ( node instanceof ParameterProperties) {
			ParameterProperties param = (ParameterProperties)node;
			
			if ( param != null) {
				name.setText( param.getName());
				value.setText( param.getValue());
			} else {
				name.setText( "");
				value.setText( "");
			}

			if ( selected) {
				name.setForeground( list.getSelectionForeground());
				equals.setForeground( list.getSelectionForeground());
				value.setForeground( list.getSelectionForeground());

				setBackground( list.getSelectionBackground());
			} else {
				name.setForeground( list.getForeground());
				equals.setForeground( list.getForeground());
				value.setForeground( list.getForeground());

				setBackground( list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
		} else {
			name.setText( "");
			value.setText( "");
		}

		return this;
	}
} 
