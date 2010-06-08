/*
 * $Id: ParameterPropertiesDialog.java,v 1.4 2004/11/04 19:21:50 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bounce.FormConstraints;
import org.bounce.FormLayout;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrDialog;

/**
 * The parameter properties dialog.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/11/04 19:21:50 $
 * @author Dogsbay
 */
public class ParameterPropertiesDialog extends XngrDialog {
	private static final Dimension SIZE 					= new Dimension( 250, 120);
	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private ParameterProperties properties	= null;
	private Vector names					= null;

	// The components that contain the values
	private JTextField nameField		= null;
	private JTextField valueField		= null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public ParameterPropertiesDialog( JFrame parent) {
		super( parent, true);
		
		setResizable( false);
		setTitle( "Parameter Properties");
		setDialogDescription( "Specify Parameter's Name and Value");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// GENERAL
		JPanel generalPanel = new JPanel( new FormLayout( 10, 2));
		generalPanel.setBorder( new EmptyBorder( 5, 5, 15, 5));
	
		// name
		nameField = new JTextField();

		JLabel nameLabel = new JLabel("Name:");
		generalPanel.add( nameLabel, FormLayout.LEFT);
		generalPanel.add( nameField, FormLayout.RIGHT_FILL);

		// name
		valueField = new JTextField();

		JLabel valueLabel = new JLabel("Value:");
		generalPanel.add( valueLabel, FormLayout.LEFT);
		generalPanel.add( valueField, FormLayout.RIGHT_FILL);

		//removed for xngr-dialog
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "OK");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/

		main.add( generalPanel, BorderLayout.CENTER);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	protected void okButtonPressed() {
		String name = nameField.getText();
		String value = valueField.getText();
		
		if ( checkName( name) && checkValue( value)) {
			
			properties.setName( name);
			properties.setValue( valueField.getText());
	
			super.okButtonPressed();
		}
	}

	public void show( ParameterProperties properties, Vector names) {
		this.properties = properties;
		this.names = names;
		
		setText( nameField, properties.getName());
		setText( valueField, properties.getValue());
		
		super.show();
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}
	
	private void setText( JTextField field, String text) {
		field.setText( text);
		field.setCaretPosition( 0);
	}
	
	protected boolean isEmpty( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}

	private boolean checkName( String name) {
		if ( isEmpty( name)) {
			MessageHandler.showMessage( "Please specify a Name for this Parameter.");
			return false;
		}
		
		for ( int i = 0; i < names.size(); i++) {
			if ( ((String)names.elementAt(i)).equals( name)) {
				MessageHandler.showMessage( "Parameter \""+name+"\" exists already.\n"+
											"Please specify a different parameter.");
				return false;
			}
		}
		
		return true;
	}

	private boolean checkValue( String value) {
		if ( isEmpty( value)) {
			MessageHandler.showMessage( "Please specify a Value for this Parameter.");
			return false;
		}
		
		return true;
	}
} 
