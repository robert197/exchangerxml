/*
 * $Id: ElementCellRenderer.java,v 1.1 2004/03/25 18:42:44 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.cladonia.xml.XElement;

/**
 * The cell renderer component for an ElementNode.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:42:44 $
 * @author Dogsbay
 */
public class ElementCellRenderer extends JPanel {
	private static final boolean DEBUG = false;
	private static final Border SELECTED_BORDER = new LineBorder( UIManager.getColor( "controlDkShadow"), 1);
	private static final Border UNSELECTED_BORDER = new EmptyBorder( 1, 1, 1, 1);
	
	private JLabel icon = null;
	private JPanel selectablePanel = null;
	private JLabel prefix = null;
	private JLabel colon = null;
	private JLabel name = null;
	private JLabel value = null;

	private boolean selected = false;
	private boolean showValue = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public ElementCellRenderer() {
		super( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
		setOpaque( false);
		
		icon = new JLabel();

		prefix = new JLabel();
		prefix.setOpaque( false);
		prefix.setFont( prefix.getFont().deriveFont( Font.PLAIN + Font.ITALIC));

		colon = new JLabel(":");
		colon.setOpaque( false);
		colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
		colon.setVisible( false);

		name = new JLabel();
		name.setOpaque( false);
		name.setFont( name.getFont().deriveFont( Font.BOLD));
		name.setForeground( Color.black);
		
		value = new JLabel();
		value.setOpaque( false);
		value.setFont( value.getFont().deriveFont( Font.PLAIN));
		value.setForeground( Color.gray);

		selectablePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0 , 0));
		selectablePanel.setOpaque( false);
		selectablePanel.add( prefix);
		selectablePanel.add( colon);
		selectablePanel.add( name);
		selectablePanel.add( value);

		this.add( icon);
		this.add( selectablePanel);
	}
	
	public void setPreferredFont( Font font) {
		name.setFont( font.deriveFont( Font.BOLD));
		prefix.setFont( font.deriveFont( Font.PLAIN + Font.ITALIC));
		colon.setFont( font.deriveFont( Font.PLAIN));
		value.setFont( font.deriveFont( Font.PLAIN));
	}

	public void setValue( ElementNode node, boolean selected) {
		XElement element = node.getElement();

		if ( element != null) {
			String pref = element.getNamespacePrefix();
			String text = element.getText();
			
			if ( pref != null && pref.length() > 0) {
				prefix.setVisible( true);
				colon.setVisible( true);
				
				prefix.setText( pref);
			
			} else {
				prefix.setVisible( false);
				colon.setVisible( false);
			}
			
			if ( isShowValue() && text != null && text.trim().length() > 0) {
				value.setVisible( true);
				text = text.trim();
				int index = text.indexOf( "\n");
				
				if ( index == -1) {
					index = text.indexOf( "\r");
				}
				
				if ( index != -1) {
					text = text.substring( 0, index);
				}
				
				if ( text.length() > 18) {
					text = text.substring( 0, 15)+"...";
				}
				
				value.setText( " \""+text+"\"");
			} else {
				value.setVisible( false);
			}
			
		} else {
			value.setVisible( false);
			prefix.setVisible( false);
			colon.setVisible( false);
		} 

		name.setText( node.getName());
		
		if ( node.isVirtual()) {
			if ( node.isRequired()) {
				name.setForeground( Color.red);
			} else {
				name.setForeground( Color.gray);
			}
		} else {
			name.setForeground( Color.black);
		}
		
		if ( selected) {
			icon.setIcon( node.getSelectedIcon());
			selectablePanel.setBorder( SELECTED_BORDER);
		} else {
			icon.setIcon( node.getIcon());
			selectablePanel.setBorder( UNSELECTED_BORDER);
		}
	}
	
	public void setShowValue( boolean show) {
		this.showValue = show;
	}

	public boolean isShowValue() {
		return showValue;
	}
} 
