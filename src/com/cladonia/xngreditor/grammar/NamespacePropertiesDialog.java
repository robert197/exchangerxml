/*
 * $Id: NamespacePropertiesDialog.java,v 1.5 2004/11/04 19:21:50 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

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
 * The namespace properties dialog.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/11/04 19:21:50 $
 * @author Dogsbay
 */
public class NamespacePropertiesDialog extends XngrDialog {
	private static final Dimension SIZE 					= new Dimension( 250, 120);
	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private NamespaceProperties properties	= null;
	private Vector prefixes					= null;

	// The components that contain the values
	private JTextField uriField		= null;
	private JTextField prefixField		= null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public NamespacePropertiesDialog( JFrame parent) {
		super( parent, true);
		
		setResizable( false);
		setTitle( "Namespace Properties");
		setDialogDescription( "Specify a Namespace URI and Prefix");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// GENERAL
		JPanel generalPanel = new JPanel( new FormLayout( 10, 2));
		generalPanel.setBorder( new EmptyBorder( 5, 5, 15, 5));
	
		// prefix
		prefixField = new JTextField();

		JLabel prefixLabel = new JLabel( "Prefix:");
		generalPanel.add( prefixLabel, FormLayout.LEFT);
		generalPanel.add( prefixField, FormLayout.RIGHT_FILL);

		// uri
		uriField = new JTextField();

		JLabel uriLabel = new JLabel( "URI:");
		generalPanel.add( uriLabel, FormLayout.LEFT);
		generalPanel.add( uriField, FormLayout.RIGHT_FILL);

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
		buttonPanel.add( cancelButton);
*/
		main.add( generalPanel, BorderLayout.CENTER);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});
*/
		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	protected void okButtonPressed() {
		String uri = uriField.getText();
		String prefix = prefixField.getText();
		
		if ( checkPrefix( prefix) && checkURI( uri)) {
			
			properties.setURI( uri);
			properties.setPrefix( prefix);
	
			super.okButtonPressed();
		}
	}

	public void show( NamespaceProperties properties, Vector prefixes) {
		this.properties = properties;
		this.prefixes = prefixes;
		
		setText( prefixField, properties.getPrefix());
		setText( uriField, properties.getURI());
		
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

	private boolean checkPrefix( String prefix) {
		if ( isEmpty( prefix)) {
			MessageHandler.showMessage( "Please specify a Prefix for this Namespace.");
			return false;
		}
		
		for ( int i = 0; i < prefixes.size(); i++) {
			if ( ((String)prefixes.elementAt(i)).equals( prefix)) {
				MessageHandler.showMessage( "A namespace with this prefix \""+prefix+"\" has been defined already.\n"+
											"Please specify a different prefix.");
				return false;
			}
		}
		
		return true;
	}

	private boolean checkURI( String value) {
		if ( isEmpty( value)) {
			MessageHandler.showMessage( "Please specify a URI for this Namespace.");
			return false;
		}
		
		return true;
	}
} 
