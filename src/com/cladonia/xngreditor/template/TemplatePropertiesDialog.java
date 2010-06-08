/*
 * $Id: TemplatePropertiesDialog.java,v 1.4 2004/11/04 19:21:50 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.template;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.FormLayout;

import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.grammar.GrammarProperties;

/**
 * The grammar properties selection dialog.
 *
 * @version	$Revision: 1.4 $, $Date: 2004/11/04 19:21:50 $
 * @author Dogsbay
 */
public class TemplatePropertiesDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 350, 300);

	private ExchangerEditor parent = null;

	private JFileChooser chooser = null;
	private File file			 = null;

	private JTextField nameField = null;

	private JTextField urlField = null;
	private JButton fileSelectionButton = null;

	private Vector names = null;
	private boolean save = false;

	/**
	 * The dialog that displays the list of grammar properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public TemplatePropertiesDialog( ExchangerEditor parent, boolean save) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		
		if ( save) {
			setTitle( "Save as Template");
			setDialogDescription( "Specify the Template settings.");
		} else {
			setTitle( "Template Properties");
			setDialogDescription( "Specify the Template settings.");
		}
		
		JPanel formPanel = new JPanel( new FormLayout( 10, 2));
		formPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Template"),
									new EmptyBorder( 0, 5, 5, 5)));
		
		ButtonGroup group = new ButtonGroup();

		// default
		nameField = new JTextField();
		formPanel.add( new JLabel( "Name:"), FormLayout.LEFT);
		formPanel.add( nameField, FormLayout.RIGHT_FILL);
		
		urlField = new JTextField();
		
		JLabel urlLabel = new JLabel( "URL:");

		fileSelectionButton = new JButton( "...");

		fileSelectionButton.setMargin( new Insets( 0, 10, 0, 10));
		fileSelectionButton.setPreferredSize( new Dimension( fileSelectionButton.getPreferredSize().width, urlField.getPreferredSize().height));
		fileSelectionButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				fileSelectionButtonPressed();
			}
		});

		JPanel urlInputPanel = new JPanel( new BorderLayout());
		urlInputPanel.add( urlField, BorderLayout.CENTER);
		urlInputPanel.add( fileSelectionButton, BorderLayout.EAST);

		formPanel.add( urlLabel, FormLayout.LEFT);
		formPanel.add( urlInputPanel, FormLayout.RIGHT_FILL);

		JPanel mainPanel = new JPanel( new BorderLayout());
		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.add( formPanel, BorderLayout.CENTER);

		this.setContentPane( mainPanel); 
		pack();
		
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), this.getPreferredSize().height));
		setLocationRelativeTo( parent);
	}
	
	protected void okButtonPressed() {
		if ( checkName( nameField.getText()) && checkURL( urlField.getText())) {
			super.okButtonPressed();
		}
	}
	
	private JFileChooser getFileChooser() {
		if ( chooser == null) {
			chooser = FileUtilities.getFileChooser();
		} 
		
		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}
		
		return chooser;
	}

	private void fileSelectionButtonPressed() {
		if ( save) {
			GrammarProperties type = parent.getGrammar();
			File file = null;
	
			if ( type != null) {
				file = FileUtilities.selectOutputFile( (File)null, FileUtilities.getExtension( type));
			} else {
				file = FileUtilities.selectOutputFile( (File)null, "xml");
			}
			
			if ( file != null) {
				try {
					urlField.setText( XngrURLUtilities.getURLFromFile(file).toString());
					urlField.setCaretPosition( 0);
				} catch ( Exception e) {}
			}
		} else {
			JFileChooser chooser = getFileChooser();
			int value = chooser.showOpenDialog( parent);
			file = chooser.getSelectedFile();

			if ( value == JFileChooser.APPROVE_OPTION) {
				URL url = null;

				try {
					url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				} catch ( MalformedURLException x) {
					x.printStackTrace(); // should never happen
				}
				
				if ( file != null) {
					try {
						urlField.setText( XngrURLUtilities.getURLFromFile(file).toString());
						urlField.setCaretPosition( 0);
					} catch ( Exception e) {}
				}
			}
			
		}
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void show( String name, URL url, Vector names) {
		nameField.setText( name);
		nameField.setCaretPosition( 0);
		
		if ( url != null) {
			urlField.setText( url.toString());
			urlField.setCaretPosition( 0);
		} else {
			urlField.setText( "");
		}
		
		this.names = names;
		
		super.show();
	}

	/**
	 * Returns the url.
	 *
	 * @return the url.
	 */
	public URL getURL() {
		URL url = null;
		try {
			url = new URL( urlField.getText());
		} catch (Exception e) {
		}

		return url;
	}
	
	/**
	 * Returns the name.
	 *
	 * @return the name.
	 */
	public String getName() {
		return nameField.getText();
	}

	protected boolean isEmpty( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}

	private boolean checkName( String name) {
		if ( isEmpty( name)) {
			MessageHandler.showMessage( "Please specify a Name for this Template.");
			return false;
		}
		
		for ( int i = 0; i < names.size(); i++) {
			if ( ((String)names.elementAt(i)).equalsIgnoreCase( name)) {
				MessageHandler.showMessage( "A Template with the name \""+name+"\" exists already.\n"+
											"Please specify another name.");
				return false;
			}
		}
		
		return true;
	}

	private boolean checkURL( String url) {
		try {
			new URL( url);
		} catch (Exception e) {
			MessageHandler.showMessage( "Please specify a valid URL.");
			return false;
		}
		
		return true;
	}
} 
