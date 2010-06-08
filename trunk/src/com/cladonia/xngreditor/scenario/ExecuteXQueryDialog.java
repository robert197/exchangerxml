/*
 * $Id: ExecuteXQueryDialog.java,v 1.7 2004/10/13 18:30:53 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.bounce.DefaultFileFilter;
import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;

/**
 * The XQuery details dialog.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/10/13 18:30:53 $
 * @author Dogsbay
 */
public class ExecuteXQueryDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);

	private Vector names			= null;
	private ExchangerDocument document	= null;
	
	private JPanel inputPanel = null;
	private JPanel xqPanel = null;
	private JPanel outputPanel = null;

	private JFileChooser outputFileChooser = null;
	private JFileChooser xqFileChooser = null;
	
	private JFrame parent			= null;

	// The components that contain the values
	private JTextField inputLocationField	= null;
	private JButton inputLocationButton		= null;
	private JRadioButton inputCurrentButton		= null;
	private JRadioButton inputFromURLButton		= null;

	private JTextField xqLocationField			= null;
	private JButton xqLocationButton			= null;
	private JRadioButton xqCurrentButton		= null;
	private JRadioButton xqFromURLButton		= null;

//	private JRadioButton outputToConsoleButton		= null;
	private JCheckBox outputToBrowserButton		= null;
	private JRadioButton outputToNewDocumentButton	= null;
	private JRadioButton outputToFileButton			= null;
	private JRadioButton outputToInputButton			= null;

	private JTextField outputLocationField		= null;
	private JButton outputLocationButton		= null;

	/**
	 * The XQuery execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	public ExecuteXQueryDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Execute XQuery");
		setDialogDescription( "Specify XQuery Transformation settings.");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		//removed for xngr-dialog
		super.okButton.setText("Execute");
		super.okButton.setMnemonic( 'x');
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "Execute");
		okButton.setMnemonic( 'x');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				executeButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/
		
		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill all three panels...
		form.add( getInputPanel(), FormLayout.FULL_FILL);
		form.add( getXQueryPanel(), FormLayout.FULL_FILL);
		form.add( getOutputPanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	private void xqLocationButtonPressed() {
		JFileChooser chooser = getXQueryFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( xqLocationField, url.toString());
		}
	}

	private JPanel getInputPanel() {
		if ( inputPanel == null) {
			inputPanel = new JPanel( new FormLayout( 10, 2));
			inputPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Input"),
										new EmptyBorder( 0, 5, 5, 5)));

			inputLocationField = new JTextField();

			inputLocationButton = new JButton( "...");
			inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			inputLocationButton.setPreferredSize( new Dimension( inputLocationButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			inputLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					inputLocationButtonPressed();
				}
			});
			
			inputLocationField.setEnabled( false);
			inputLocationButton.setEnabled( false);

			JPanel locationPanel = new JPanel( new BorderLayout());

			inputFromURLButton	= new JRadioButton( "From URL:");
			inputFromURLButton.setPreferredSize( new Dimension( inputFromURLButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			inputFromURLButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					inputLocationField.setEnabled( inputFromURLButton.isSelected());
					inputLocationButton.setEnabled( inputFromURLButton.isSelected());
				}
			});

			locationPanel.add( inputFromURLButton, BorderLayout.WEST);
			locationPanel.add( inputLocationField, BorderLayout.CENTER);
			locationPanel.add( inputLocationButton, BorderLayout.EAST);

			inputCurrentButton	= new JRadioButton( "Current Document");
			inputCurrentButton.setPreferredSize( new Dimension( inputCurrentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			
			ButtonGroup group = new ButtonGroup();
			group.add( inputCurrentButton);
			group.add( inputFromURLButton);
			
			inputPanel.add( inputCurrentButton, FormLayout.FULL);
			inputPanel.add( locationPanel, FormLayout.FULL_FILL);
		}

		return inputPanel;
	}
	
	private JPanel getOutputPanel() {
		if ( outputPanel == null) {
			outputPanel = new JPanel( new FormLayout( 10, 2));
			outputPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Output"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			outputLocationField = new JTextField();

			outputLocationButton = new JButton( "...");
			outputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			outputLocationButton.setPreferredSize( new Dimension( outputLocationButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					outputLocationButtonPressed();
				}
			});
			
			outputLocationField.setEnabled( false);
			outputLocationButton.setEnabled( false);

			outputToFileButton	= new JRadioButton( "To File:");
			outputToFileButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					outputLocationButton.setEnabled( outputToFileButton.isSelected());
					outputLocationField.setEnabled( outputToFileButton.isSelected());
				}
			});
			
			outputToFileButton.setPreferredSize( new Dimension( outputToFileButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			
			JPanel locationPanel = new JPanel( new BorderLayout());
			locationPanel.add( outputToFileButton, BorderLayout.WEST);
			locationPanel.add( outputLocationField, BorderLayout.CENTER);
			locationPanel.add( outputLocationButton, BorderLayout.EAST);

			outputPanel.add( locationPanel, FormLayout.FULL_FILL);

//			outputToConsoleButton	= new JRadioButton( "To Console");
//			outputToConsoleButton.setPreferredSize( new Dimension( outputToConsoleButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
//			outputPanel.add( outputToConsoleButton, FormLayout.FULL);
	
			outputToNewDocumentButton	= new JRadioButton( "To New Document");
			outputToNewDocumentButton.setPreferredSize( new Dimension( outputToNewDocumentButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToNewDocumentButton, FormLayout.FULL);

			outputToInputButton	= new JRadioButton( "To Input Document");
			outputToInputButton.setPreferredSize( new Dimension( outputToInputButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToInputButton, FormLayout.FULL);

			ButtonGroup outputGroup = new ButtonGroup();
			outputGroup.add( outputToFileButton);
			outputGroup.add( outputToInputButton);
			outputGroup.add( outputToNewDocumentButton);
//			outputGroup.add( outputToBrowserButton);

			outputPanel.add( getSeparator(), FormLayout.FULL_FILL);

			outputToBrowserButton	= new JCheckBox( "Open Output in Browser");
			outputToBrowserButton.setPreferredSize( new Dimension( outputToBrowserButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToBrowserButton, FormLayout.FULL);

			outputToNewDocumentButton.setSelected( true);
			outputToFileButton.setSelected( false);
			outputToBrowserButton.setSelected( false);
		}
		
		return outputPanel;
	}

	private JPanel getXQueryPanel() {
		if ( xqPanel == null) {
			xqPanel = new JPanel( new FormLayout( 10, 2));
			xqPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "XQuery"),
										new EmptyBorder( 0, 5, 5, 5)));
			JPanel xqLocationPanel = new JPanel( new BorderLayout());

			xqLocationField = new JTextField();

			xqLocationButton = new JButton( "...");
			xqLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			xqLocationButton.setPreferredSize( new Dimension( xqLocationButton.getPreferredSize().width, xqLocationField.getPreferredSize().height));
			xqLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					xqLocationButtonPressed();
				}
			});
			
			xqLocationField.setEnabled( false);
			xqLocationButton.setEnabled( false);

			xqFromURLButton	= new JRadioButton( "From URL:");
			xqFromURLButton.setPreferredSize( new Dimension( xqFromURLButton.getPreferredSize().width, xqLocationField.getPreferredSize().height));
			xqFromURLButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					xqLocationField.setEnabled( xqFromURLButton.isSelected());
					xqLocationButton.setEnabled( xqFromURLButton.isSelected());
				}
			});

			xqLocationPanel.add( xqFromURLButton, BorderLayout.WEST);
			xqLocationPanel.add( xqLocationField, BorderLayout.CENTER);
			xqLocationPanel.add( xqLocationButton, BorderLayout.EAST);

			xqCurrentButton	= new JRadioButton( "Current Document");
			xqCurrentButton.setPreferredSize( new Dimension( xqCurrentButton.getPreferredSize().width, xqLocationField.getPreferredSize().height));

			ButtonGroup xqGroup = new ButtonGroup();
			xqGroup.add( xqCurrentButton);
			xqGroup.add( xqFromURLButton);

			xqPanel.add( xqCurrentButton, FormLayout.FULL);
			xqPanel.add( xqLocationPanel, FormLayout.FULL_FILL);

			xqFromURLButton.setSelected( true);
		}

		return xqPanel;
	}

	private void outputLocationButtonPressed() {
		JFileChooser chooser = getOutputFileChooser();
		File file = FileUtilities.selectOutputFile( chooser, null);

		if ( file != null) {
			setText( outputLocationField, file.getPath());
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
		}
	}

	protected void okButtonPressed() {
		super.okButtonPressed();
	}
	
	public ScenarioProperties getScenario() {
		ScenarioProperties properties = new ScenarioProperties();

		// set XML Input stuff
		if ( inputCurrentButton.isSelected()) {
			properties.setInputType( ScenarioProperties.INPUT_CURRENT_DOCUMENT);
		} else {
			properties.setInputType( ScenarioProperties.INPUT_FROM_URL);
			properties.setInputFile( inputLocationField.getText());
		}
			
		properties.setXQueryEnabled( true);

		// set XSL stuff
		if ( xqCurrentButton.isSelected()) {
			properties.setXQueryType( ScenarioProperties.XQUERY_CURRENT_DOCUMENT);
		} else {
			properties.setXQueryType( ScenarioProperties.XQUERY_FROM_URL);
			properties.setXQueryURL( xqLocationField.getText());
		}

		if ( outputToFileButton.isSelected()) {
			properties.setOutputType( ScenarioProperties.OUTPUT_TO_FILE);
			properties.setOutputFile( outputLocationField.getText());
		} else if ( outputToNewDocumentButton.isSelected()) {
			properties.setOutputType( ScenarioProperties.OUTPUT_TO_NEW_DOCUMENT);
		} else if ( outputToInputButton.isSelected()) {
			properties.setOutputType( ScenarioProperties.OUTPUT_TO_INPUT);
		}
			
		properties.setBrowserEnabled( outputToBrowserButton.isSelected());
		
		properties.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT2);
		
		return properties;
	}
	
//	// Execute the values in the dialog...
//	public void execute() {
//		ScenarioUtilities.execute( document, properties);
//	}
//
	public void show( ExchangerDocument document) {
		JPanel form = new JPanel( new FormLayout( 10, 2));

		if ( document != null) {
			this.document = document;

			xqCurrentButton.setEnabled( true);

			if ( document.isError()) {
				inputFromURLButton.setSelected( true);
				inputCurrentButton.setEnabled( false);

				xqCurrentButton.setSelected( true);
			} else {
				inputCurrentButton.setEnabled( true);
				inputCurrentButton.setSelected( true);
				xqFromURLButton.setSelected( true);
			}
			
		} else {
			inputFromURLButton.setSelected( true);

			if ( xqCurrentButton.isSelected()) {
				xqFromURLButton.setSelected( true);
			}

			xqCurrentButton.setEnabled( false);
			inputCurrentButton.setEnabled( false);
		}
		
//		outputToBrowserButton.setSelected( false);

		pack();
		setSize( new Dimension( 400, getSize().height));

		setLocationRelativeTo( parent);

		super.show();
	}
	
	private boolean isStylesheet( ExchangerDocument doc) {
		XElement root = doc.getRoot();

		return (root != null && root.getName().equals( "stylesheet") && root.getNamespaceURI().equals( "http://www.w3.org/1999/XSL/Transform"));
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
	private JFileChooser getXQueryFileChooser() {
		if ( xqFileChooser == null) {
			xqFileChooser = FileUtilities.createFileChooser();

			xqFileChooser.addChoosableFileFilter( new DefaultFileFilter( "xq", "XQuery Document"));
		} 

		File file = URLUtilities.toFile( xqLocationField.getText());

		if ( file == null && inputLocationField != null && inputLocationField.isEnabled() && inputLocationField.isVisible()) {
			file = URLUtilities.toFile( inputLocationField.getText());
		}
		
		if ( file == null && outputLocationField.isEnabled() && !isEmpty( outputLocationField.getText())) {
			file = new File( outputLocationField.getText());
		}
		
		if ( file == null) {
			if ( document != null) {
				file = URLUtilities.toFile( document.getURL());
			} 
			
			if ( file == null) {
				file = FileUtilities.getLastOpenedFile();
			}
		}

		xqFileChooser.setCurrentDirectory( file);
		xqFileChooser.rescanCurrentDirectory();
		
		return xqFileChooser;
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getOutputFileChooser() {
		if ( outputFileChooser == null) {
			outputFileChooser = FileUtilities.createFileChooser();
		} 

		File file = null;

		if ( !isEmpty( outputLocationField.getText())) {
			file = new File( outputLocationField.getText());
		}

		if ( file == null && xqLocationField != null && xqLocationField.isEnabled() && xqLocationField.isVisible()) {
			file = URLUtilities.toFile( xqLocationField.getText());
		}

		if ( file == null && inputLocationField != null && inputLocationField.isEnabled() && inputLocationField.isVisible()) {
			file = URLUtilities.toFile( inputLocationField.getText());
		}
		
		if ( file == null) {
			if ( document != null) {
				file = URLUtilities.toFile( document.getURL());
			} 
			
			if ( file == null) {
				file = FileUtilities.getLastOpenedFile();
			}
		}

		outputFileChooser.setCurrentDirectory( file);
		outputFileChooser.rescanCurrentDirectory();
		
		return outputFileChooser;
	}

	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile( inputLocationField.getText());

		if ( file == null && xqLocationField != null && xqLocationField.isEnabled() && xqLocationField.isVisible()) {
			file = URLUtilities.toFile( xqLocationField.getText());
		}
		
		if ( file == null && outputLocationField.isEnabled() && !isEmpty( outputLocationField.getText())) {
			file = new File( outputLocationField.getText());
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
