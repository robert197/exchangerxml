/*
 * $Id: NamedXPathPropertiesDialog.java,v 1.4 2004/11/04 19:21:50 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.PrefixNamespaceMappingPanel;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The namespace properties dialog.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/11/04 19:21:50 $
 * @author Dogsbay
 */
public class NamedXPathPropertiesDialog extends XngrDialog {
	private static final Dimension SIZE 	= new Dimension( 400, 400);

	private NamedXPathProperties namedXPath		= null;
	private Vector namedXPaths					= null;
	private ConfigurationProperties properties	= null;

	// The components that contain the values
    public PrefixNamespaceMappingPanel mappingPanel;

    private JTextField nameField		= null;
	private JTextField xpathField		= null;

	private JCheckBox showAttributeNamesCheck	= null;
	private JCheckBox showElementNamesCheck		= null;
	private JCheckBox showAttributesCheck			= null;
	private JCheckBox showElementContentCheck		= null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public NamedXPathPropertiesDialog( JFrame parent, ConfigurationProperties properties) {
		super( parent, true);
		
		this.properties = properties;
		setResizable( false);
		setTitle( "Named XPath Properties");
		setDialogDescription( "Specify Named XPath information");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// GENERAL
		Box generalPanel = Box.createVerticalBox();
		generalPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
	
		JPanel namePanel = new JPanel( new FormLayout( 2, 2));
		namePanel.setBorder( new CompoundBorder( 
								new TitledBorder( "Name"),
								new EmptyBorder( 0, 5, 5, 5)));

		// name
		nameField = new JTextField();

		JLabel nameLabel = new JLabel( "Name:");
		namePanel.add( nameLabel, FormLayout.LEFT);
		namePanel.add( nameField, FormLayout.RIGHT_FILL);
		
		generalPanel.add( namePanel);

		JPanel xpathPanel = new JPanel( new FormLayout( 2, 2));
		xpathPanel.setBorder( new CompoundBorder( 
								new TitledBorder( "XPath"),
								new EmptyBorder( 0, 5, 5, 5)));

		// xpath
		xpathField = new JTextField();

		JLabel xpathLabel = new JLabel( "XPath:");
		xpathPanel.add( xpathLabel, FormLayout.LEFT);
		xpathPanel.add( xpathField, FormLayout.RIGHT_FILL);

		xpathPanel.add( getSeparator(), FormLayout.FULL_FILL);

		mappingPanel = new PrefixNamespaceMappingPanel( parent, properties, 3);
		
		xpathPanel.add( new JLabel( "Prefix Namespace Mappings:"), FormLayout.FULL);
		xpathPanel.add( mappingPanel, FormLayout.FULL_FILL);

		generalPanel.add( xpathPanel);

		JPanel settingsPanel = new JPanel( new FormLayout( 2, 2));
		settingsPanel.setBorder( new CompoundBorder( 
								new TitledBorder( "Settings"),
								new EmptyBorder( 0, 5, 5, 5)));

		showAttributesCheck = new JCheckBox( "Show Attributes");
		showAttributesCheck.setFont( showAttributesCheck.getFont().deriveFont( Font.PLAIN));

		showAttributeNamesCheck = new JCheckBox( "Show Attribute Names");
		showAttributeNamesCheck.setFont( showAttributeNamesCheck.getFont().deriveFont( Font.PLAIN));

		showElementContentCheck = new JCheckBox( "Show Element Content");
		showElementContentCheck.setFont( showElementContentCheck.getFont().deriveFont( Font.PLAIN));

		showElementNamesCheck = new JCheckBox( "Show Element Names");
		showElementNamesCheck.setFont( showElementNamesCheck.getFont().deriveFont( Font.PLAIN));

		settingsPanel.add( showAttributesCheck, FormLayout.FULL);
		settingsPanel.add( showAttributeNamesCheck, FormLayout.FULL);
		settingsPanel.add( showElementContentCheck, FormLayout.FULL);
		settingsPanel.add( showElementNamesCheck, FormLayout.FULL);
		
		generalPanel.add( settingsPanel);

		main.add( generalPanel, BorderLayout.CENTER);

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
		pack();
		
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
		setLocationRelativeTo( parent);
	}
	
	protected void okButtonPressed() {
		if ( checkNamedXPath()) {
        	mappingPanel.save();

        	namedXPath.setName( nameField.getText());
			namedXPath.setXPath( xpathField.getText());
			namedXPath.setShowAttributes( showAttributesCheck.isSelected());
			namedXPath.setShowElementContent( showElementContentCheck.isSelected());
			namedXPath.setShowAttributeNames( showAttributeNamesCheck.isSelected());
			namedXPath.setShowElementNames( showElementNamesCheck.isSelected());
	
			super.okButtonPressed();
		}
	}

	public void show( NamedXPathProperties xpath, Vector xpaths) {
		this.namedXPath = xpath;
		this.namedXPaths = xpaths;
		
		mappingPanel.init();

		setText( nameField, xpath.getName());
		setText( xpathField, xpath.getXPath());
		showAttributeNamesCheck.setSelected( xpath.showAttributeNames());
		showElementNamesCheck.setSelected( xpath.showElementNames());
		showElementContentCheck.setSelected( xpath.showElementContent());
		showAttributesCheck.setSelected( xpath.showAttributes());
		
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

	private boolean checkNamedXPath() {
		String name = nameField.getText();
		String xpath = xpathField.getText();
		
		if ( isEmpty( name)) {
			MessageHandler.showMessage( "Please specify a Name for this XPath.");
			return false;
		}
		
		for ( int i = 0; i < namedXPaths.size(); i++) {
			if ( ((NamedXPathProperties)namedXPaths.elementAt(i)).getName().equals( name)) {
				MessageHandler.showMessage( "A named XPath with this name \""+name+"\" has been defined already.\n"+
											"Please specify a different name.");
				return false;
			}
		}
		
		if ( isEmpty( xpath)) {
			MessageHandler.showMessage( "Please specify a XPath.");
			return false;
		}

		return true;
	}
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}
}