/*
 * $Id: SchemaDetailsDialog.java,v 1.2 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.exolab.castor.xml.schema.Schema;

/**
 * The dialog that shows the details for the dialog.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/11/04 19:21:48 $
 * @author Dogsbay
 */
public class SchemaDetailsDialog extends JDialog {
	private static final Dimension SIZE = new Dimension( 350, 300);

	private SchemaDetailsPanel details = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public SchemaDetailsDialog( JFrame frame) {
		super( frame, false);
		
		setResizable( false);
		
		setTitle( "Schema Details");

		details = new SchemaDetailsPanel();
		JButton okButton = new JButton( "OK");
		
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);

		JPanel contentPane = new JPanel( new BorderLayout());
		contentPane.add( details, BorderLayout.CENTER);
		contentPane.add( buttonPanel, BorderLayout.SOUTH);
		
		setContentPane( contentPane);
		
		getRootPane().setDefaultButton( okButton);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	public void setPreferredFont( Font font) {
		details.setPreferredFont( font);
	}

	public void show( Schema schema) {
		details.setSchema( schema);
		hide();
//		setVisible(false);
	}
	
	
	// Ok button handler
	private void okButtonPressed() {
		hide();
//		setVisible(false);
	}
} 
