/*
 * $Id: VariableCellRenderer.java,v 1.3 2004/05/31 17:52:40 edankert Exp $
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
import com.cladonia.xslt.debugger.XSLTVariable;

/**
 * The cell renderer component for an ElementNode.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/31 17:52:40 $
 * @author Dogsbay
 */
public class VariableCellRenderer extends JPanel implements ListCellRenderer {
	private static final boolean DEBUG = false;
	
	private static final ImageIcon GLOBAL_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/GlobalVariable11.gif");
	private static final ImageIcon LOCAL_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/LocalVariable11.gif");

	private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private JLabel icon = null;
	private JPanel selectablePanel = null;
	private JLabel type = null;
	private JLabel name = null;
	private JLabel equals = null;
	private JLabel value = null;

	private boolean selected = false;
	private boolean showValue = false;
	
	private boolean global = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public VariableCellRenderer( boolean global) {
		super( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
		this.global = global;
//		setOpaque( false);
		
		icon = new JLabel();

		type = new JLabel();
		type.setOpaque( false);
		type.setBorder( new EmptyBorder( 0, 2, 0, 2));
		type.setFont( type.getFont().deriveFont( Font.PLAIN + Font.ITALIC));

		equals = new JLabel("=");
		equals.setBorder( new EmptyBorder( 0, 2, 0, 2));
		equals.setOpaque( false);
		equals.setFont( equals.getFont().deriveFont( Font.PLAIN));
		equals.setVisible( true);

		name = new JLabel();
		name.setBorder( new EmptyBorder( 0, 2, 0, 2));
		name.setOpaque( false);
		name.setFont( name.getFont().deriveFont( Font.BOLD));
		name.setForeground( Color.black);
		
		value = new JLabel();
		value.setBorder( new EmptyBorder( 0, 2, 0, 2));
		value.setOpaque( false);
		value.setFont( value.getFont().deriveFont( Font.PLAIN));
//		value.setForeground( Color.gray);

		selectablePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0 , 0));
		selectablePanel.setOpaque( false);
		selectablePanel.add( type);
		selectablePanel.add( name);
		selectablePanel.add( equals);
		selectablePanel.add( value);

		this.add( icon);
		this.add( selectablePanel);
	}
	
	public void setPreferredFont( Font font) {
		name.setFont( font.deriveFont( Font.BOLD));
		type.setFont( font.deriveFont( Font.PLAIN + Font.ITALIC));
		equals.setFont( font.deriveFont( Font.PLAIN));
		value.setFont( font.deriveFont( Font.PLAIN));
	}
	
	public ImageIcon getIcon() {
		if ( global) {
			return GLOBAL_ICON;
		}

		return LOCAL_ICON;
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public ImageIcon getSelectedIcon() {
		ImageIcon icon = getIcon();

		if ( icon != null) {
			icon = ImageUtilities.createDarkerImage( icon);
		}
		
		return icon;
	}

	public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
		if ( node instanceof XSLTVariable) {
			XSLTVariable variable = (XSLTVariable)node;
			
			if ( variable != null) {
				name.setText( variable.getName());
				value.setText( variable.getValue());
				type.setText( convertType( variable.getType()));
			} else {
				name.setText( "name");
				value.setText( "value");
				type.setText( "type");
			}

			if ( selected) {
				icon.setIcon( getSelectedIcon());
				
				type.setForeground( list.getSelectionForeground());
				name.setForeground( list.getSelectionForeground());
				equals.setForeground( list.getSelectionForeground());
				value.setForeground( list.getSelectionForeground());
				setBackground( list.getSelectionBackground());
			} else {
				icon.setIcon( getIcon());

				type.setForeground( list.getForeground());
				name.setForeground( list.getForeground());
				equals.setForeground( list.getForeground());
				value.setForeground( list.getForeground());
				setBackground( list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
		} else {
			name.setText( "name");
			value.setText( "value");
			type.setText( "type");
		}

		return this;
	}
	
	public String convertType( int type) {
		String result = null;

		switch (type) {
			case XSLTVariable.XSLT_TYPE_BOOLEAN :
				result = "boolean";
				break;
			case XSLTVariable.XSLT_TYPE_NODESET :
				result = "nodeset";
				break;
			case XSLTVariable.XSLT_TYPE_ANY :
				result = "any";
				break;
			case XSLTVariable.XSLT_TYPE_OBJECT :
				result = "object";
				break;
			case XSLTVariable.XSLT_TYPE_UNKNOWN :
				result = "unknown";
				break;
			case XSLTVariable.XSLT_TYPE_NUMBER :
				result = "number";
				break;
			default :
				result = "string";
				break;
		}

		return result;
	}

} 
