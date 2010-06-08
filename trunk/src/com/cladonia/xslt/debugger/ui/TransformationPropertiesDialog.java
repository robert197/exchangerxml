/*
 * $Id: TransformationPropertiesDialog.java,v 1.8 2005/04/11 14:09:34 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.xml.transform.TransformerException;

import org.bounce.DefaultFileFilter;
import org.bounce.FormConstraints;
import org.bounce.FormLayout;

import com.cladonia.xml.transform.TransformerUtilities;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * The grammar properties dialog.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/04/11 14:09:34 $
 * @author Dogsbay
 */
public class TransformationPropertiesDialog extends XngrDialog {
	private static final Dimension SIZE 					= new Dimension( 425, 550);
	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private XSLTTransformation transformation	= null;

	private JFileChooser inputFileChooser = null;
	private JFileChooser xslFileChooser = null;
	
	private JFrame parent			= null;

	// input
	private JTextField inputLocationField	= null;
	private JButton inputLocationButton		= null;

	// xsl
	private JTextField xslLocationField		= null;
	private JButton xslLocationButton		= null;

	// processor
	private JRadioButton processorSaxon1Button	= null;
	private JRadioButton processorSaxon2Button	= null;
	private JRadioButton processorXalanButton	= null;

	public TransformationPropertiesDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "XSLT Transformation");
		setDialogDescription( "Specify XSLT Transformation settings");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// GENERAL
		JPanel centerPanel = new JPanel( new FormLayout( 10, 2));
		centerPanel.setBorder( new CompoundBorder( 
				new TitledBorder( "XSLT Transformation"),
				new EmptyBorder( 0, 5, 5, 5)));
		
		// >>> input
		JPanel inputLocationPanel = new JPanel( new BorderLayout());

		inputLocationField = new JTextField();
		inputLocationButton = new JButton( "...");

		inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		inputLocationButton.setPreferredSize( new Dimension( inputLocationButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
		inputLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				inputLocationButtonPressed();
			}
		});
		
		inputLocationPanel.add( inputLocationField, BorderLayout.CENTER);
		inputLocationPanel.add( inputLocationButton, BorderLayout.EAST);

		centerPanel.add( new JLabel( "Input URL:"), FormLayout.LEFT);
		centerPanel.add( inputLocationPanel, FormLayout.RIGHT_FILL);
		// <<< input

		// >>> xsl
		JPanel xslLocationPanel = new JPanel( new BorderLayout());
		
		xslLocationField = new JTextField();

		xslLocationButton = new JButton( "...");

		xslLocationButton = new JButton( "...");
		xslLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		xslLocationButton.setPreferredSize( new Dimension( xslLocationButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		xslLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				xslLocationButtonPressed();
			}
		});
		
		xslLocationPanel.add( xslLocationField, BorderLayout.CENTER);
		xslLocationPanel.add( xslLocationButton, BorderLayout.EAST);

		centerPanel.add( new JLabel( "Stylesheet URL:"), FormLayout.LEFT);
		centerPanel.add( xslLocationPanel, FormLayout.RIGHT_FILL);
		// <<< xsl

		centerPanel.add( getSeparator(), FormLayout.FULL_FILL);
		
		// >>> processor
		centerPanel.add( new JLabel( "Processor:"), FormLayout.LEFT);
		
		processorXalanButton = new JRadioButton( "Xalan");
		processorXalanButton.setPreferredSize( new Dimension( processorXalanButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		processorSaxon1Button = new JRadioButton( "Saxon (XSLT 1.0)");
		processorSaxon1Button.setPreferredSize( new Dimension( processorSaxon1Button.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		processorSaxon2Button = new JRadioButton( "Saxon (XSLT 2.0)");
		processorSaxon2Button.setPreferredSize( new Dimension( processorSaxon2Button.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		
		ButtonGroup group = new ButtonGroup();
		group.add( processorXalanButton);
		group.add( processorSaxon1Button);
		group.add( processorSaxon2Button);
		
		centerPanel.add( processorXalanButton, FormLayout.RIGHT);
		centerPanel.add( processorSaxon1Button, FormLayout.RIGHT);
		centerPanel.add( processorSaxon2Button, FormLayout.RIGHT);
		// <<< processor
		
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
		cancelButton.setMnemonic( 'O');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/

		main.add( centerPanel, BorderLayout.NORTH);
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
		
		setSize( new Dimension( 425, getSize().height));
		
		setLocationRelativeTo( parent);
	}
	
	private void xslLocationButtonPressed() {
		JFileChooser chooser = getXSLFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( xslLocationField, url.toString());
		}
	}
	
	private void inputLocationButtonPressed() {
		JFileChooser chooser = getInputFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;
			
			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}
			
			setText( inputLocationField, url.toString());
			
			String stylesheet = null;
			
			try {
				stylesheet = TransformerUtilities.getPIStylesheetLocation( url.toString());
			} catch ( TransformerException x) {
//				x.printStackTrace(); // only happens when no stylesheet can be found.
			}
			
			if ( !StringUtilities.isEmpty( stylesheet) && StringUtilities.isEmpty( xslLocationField.getText())) {
				setText( xslLocationField, stylesheet);
			}
		}
	}
	
//	private XSLTTransformation fillXSLTTransformation() {
//		transformation.setInputURL( inputLocationField.getText());
//		transformation.setStyleURL( xslLocationField.getText());
//		
//		if ( processorSaxon1Button.isSelected()) {
//			transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT1);
//		} else { 
//			transformation.setProcessor( ScenarioProperties.PROCESSOR_XALAN);
//		}
//
//		return transformation;
//	}

	protected void okButtonPressed() {
		try {
			new URL( inputLocationField.getText());
		} catch (MalformedURLException e) {
			MessageHandler.showMessage( parent, "Please provide a valid Input URL");
			return;
		}

		try {
			new URL( xslLocationField.getText());
		} catch (MalformedURLException e) {
			MessageHandler.showMessage( parent, "Please provide a valid Stylesheet URL");
			return;
		}

		transformation.setInputURL( inputLocationField.getText());
		transformation.setStyleURL( xslLocationField.getText());
		
		if ( processorSaxon1Button.isSelected()) {
			transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT1);
		} else if ( processorSaxon2Button.isSelected()) { 
			transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT2);
		} else { 
			transformation.setProcessor( ScenarioProperties.PROCESSOR_XALAN);
		}

		super.okButtonPressed();
	}

	public void show( XSLTTransformation transformation) {
		this.transformation = transformation;
		
		setText( inputLocationField, transformation.getInputURL());
		setText( xslLocationField, transformation.getStyleURL());
		
		int processor = transformation.getProcessor();
		
		if ( processor == ScenarioProperties.PROCESSOR_XALAN) {
			processorXalanButton.setSelected( true);
		} else if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT1) {
			processorSaxon1Button.setSelected( true);
		} else {
			processorSaxon2Button.setSelected( true);
		}

		super.show();
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getXSLFileChooser() {
		if ( xslFileChooser == null) {
			xslFileChooser = FileUtilities.createFileChooser();
			xslFileChooser.addChoosableFileFilter( new DefaultFileFilter( "xsl", "XSL Stylesheet"));
		} 

		File file = URLUtilities.toFile( xslLocationField.getText());

		if ( file == null && inputLocationField != null && inputLocationField.isEnabled()) {
			file = URLUtilities.toFile( inputLocationField.getText());
		}
		
		if ( file == null) {
			file = FileUtilities.getLastOpenedFile();
		}

		xslFileChooser.setCurrentDirectory( file);
		xslFileChooser.rescanCurrentDirectory();
		
		return xslFileChooser;
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile( inputLocationField.getText());

		if ( file == null && xslLocationField != null && xslLocationField.isEnabled()) {
			file = URLUtilities.toFile( xslLocationField.getText());
		}
		
		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}

		chooser.rescanCurrentDirectory();
		
		return chooser;
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
} 
