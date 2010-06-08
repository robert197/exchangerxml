/*
 * $Id: BaseDetailsPanel.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.bounce.FormLayout;
import org.bounce.QPanel;

/**
 * The Base details panel.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class BaseDetailsPanel extends QPanel {
	private JLabel titleLabel = null;
	private JLabel nameLabel = null;

	public BaseDetailsPanel( String title) {
		super( new FormLayout( 5, 3));
		
		JPanel panel = new JPanel( new BorderLayout());

		titleLabel = new JLabel( "("+title+")");
		titleLabel.setFont( titleLabel.getFont().deriveFont( (float)14));
		titleLabel.setBackground( UIManager.getColor( "controlDkShadow"));
//		titleLabel.setBackground( new Color( 102, 102, 102));

		titleLabel.setForeground( Color.white);
		titleLabel.setOpaque( true);
		titleLabel.setBorder( new EmptyBorder( 0, 0, 0, 5));
		
		panel.add( titleLabel, BorderLayout.EAST);

		nameLabel = new JLabel();
		nameLabel.setFont( nameLabel.getFont().deriveFont( (float)14));
//		nameLabel.setBackground( new Color( 102, 102, 102));
		nameLabel.setBackground( UIManager.getColor( "controlDkShadow"));
		nameLabel.setForeground( Color.white);
		nameLabel.setOpaque( true);
		nameLabel.setBorder( new EmptyBorder( 0, 5, 0, 0));

		panel.add( nameLabel, BorderLayout.WEST);
//		panel.setBackground( new Color( 102, 102, 102));
		panel.setBackground( UIManager.getColor( "controlDkShadow"));

		add( panel, FormLayout.FULL_FILL);

		setBackground( Color.white);
		
		setBorder( new CompoundBorder( 
					new EmptyBorder( 2, 2, 2, 2),
					new CompoundBorder( 
						new LineBorder( UIManager.getColor( "controlDkShadow")),
						new EmptyBorder( 2, 2, 2, 2))));
	}

	public void setPreferredFont( Font font) {
		nameLabel.setFont( font.deriveFont( Font.BOLD, (float)14));
	}

	public void setName( String name) {
		nameLabel.setText( name);
	}

	protected void add( String label, JTextField field) {
		add( createTitleLabel( label), FormLayout.LEFT);
		add( field, FormLayout.RIGHT_FILL);
	}

	protected void add( String label, JLabel component) {
		add( createTitleLabel( label), FormLayout.LEFT);
		add( component, FormLayout.RIGHT);
	}

	protected void add( String label, JComponent component) {
		add( createTitleLabel( label), FormLayout.LEFT);
		add( component, FormLayout.RIGHT);
	}

	protected JLabel createTitleLabel( String title) {
		JLabel label = new JLabel( title+":");
		label.setForeground( Color.black);
		label.setHorizontalAlignment( JLabel.RIGHT);
		label.setForeground( UIManager.getColor( "controlShadow"));
		label.setOpaque( false);

		return label;
	}

	protected JLabel createValueLabel() {
		JLabel label = new JLabel();
		label.setForeground( Color.black);
		label.setFont( label.getFont().deriveFont( Font.PLAIN));
		label.setOpaque( false);

		return label;
	}

	protected JTextField createValueTextField() {
		JTextField field = new JTextField();
		field.setBorder( null);
		field.setOpaque( false);
		field.setEditable( false);

		return field;
	}

	protected void addSeparator() {
		JPanel separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 5));
		separator.setOpaque( false);
	
		add( separator, FormLayout.FULL);
	}
	
	protected String toString( Object object) {
		if ( object != null) {
			return object.toString();
		} else {
			return "";
		}
	}

	protected String toString( Object object, String defaultValue) {
		if ( object != null) {
			return object.toString();
		} else {
			return defaultValue;
		}
	}

}