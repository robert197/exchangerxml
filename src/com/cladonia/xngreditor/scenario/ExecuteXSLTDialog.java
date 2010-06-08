/*
 * $Id: ExecuteXSLTDialog.java,v 1.9 2005/03/16 17:47:14 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.awt.BorderLayout;
import java.awt.Dimension;
//gmcg
import java.awt.FlowLayout;
import java.awt.Font;

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

//GMCG 
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JComboBox;


import org.bounce.DefaultFileFilter;
import org.bounce.FormLayout;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.OpenDocument;

// gmcg
import com.cladonia.xngreditor.XngrDialogHeader;

/**
 * The grammar properties dialog.
 *
 * @version	$Revision: 1.9 $, $Date: 2005/03/16 17:47:14 $
 * @author Dogsbay
 */
public class ExecuteXSLTDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);

	private Vector names			= null;
	private ExchangerDocument document	= null;

	private JPanel inputPanel = null;
	private JPanel xslPanel = null;
	private JPanel outputPanel = null;

	private JFileChooser outputFileChooser = null;
//	private JFileChooser inputFileChooser = null;
	private JFileChooser xslFileChooser = null;
	
	private JFrame parent				= null;

	// The components that contain the values
	private JTextField inputLocationField	= null;
	private JButton inputLocationButton		= null;
	private JRadioButton inputCurrentButton		= null;
	private JRadioButton inputFromURLButton		= null;
	private JRadioButton inputPromptButton		= null;
	private JRadioButton inputFromOpenDocumentButton		= null;
	private JComboBox inputFromOpenDocumentBox = null;
	
	private JTextField xslLocationField			= null;
	private JButton xslLocationButton			= null;
	private JRadioButton xslCurrentButton		= null;
	private JRadioButton xslFromURLButton		= null;
	private JRadioButton xslFromPIsButton		= null;
	private JRadioButton xslPromptButton		= null;
	private JRadioButton xslFromOpenDocumentButton		= null;
	private JComboBox xslFromOpenDocumentBox = null;



	private JRadioButton outputToInputButton		= null;
	private JRadioButton outputToNewDocumentButton	= null;
	private JRadioButton outputToFileButton			= null;

	private JCheckBox outputToBrowserButton			= null;

	private JTextField outputLocationField		= null;
	private JButton outputLocationButton		= null;

	
	private JButton parameterButton			= null;
	private JButton processorButton	= null;
	private JDialog processorDialog			= null;
	// processor
	private JRadioButton processorDefaultButton	= null;
	private JRadioButton processorXalanButton	= null;
	private JRadioButton processorSaxon1Button	= null;
	private JRadioButton processorSaxon2Button	= null;
	private ParameterManagementDialog parameterManagementDialog	= null;
	
	
	
	/**
	 * The XSLT execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	public ExecuteXSLTDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Execute XSLT");
		setDialogDescription( "Specify XSL Transformation settings.");
//		setSize( SIZE);
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		//removed for xngr-dialog
		super.okButton.setText( "Execute");
		super.okButton.setMnemonic( 'x');
		/*
		cancelButton = new JButton( "Cancel");
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
		form.add( getXSLPanel(), FormLayout.FULL_FILL);
		form.add( getOutputPanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
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

			locationPanel.add( inputLocationField, BorderLayout.CENTER);
			locationPanel.add( inputLocationButton, BorderLayout.EAST);

			inputCurrentButton	= new JRadioButton( "Current Document");
			inputCurrentButton.setPreferredSize( new Dimension( inputCurrentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

			inputFromOpenDocumentButton	= new JRadioButton( "Open Document");
			inputFromOpenDocumentButton.setPreferredSize( new Dimension( inputFromOpenDocumentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

			inputFromOpenDocumentBox = new JComboBox();
			//inputFromOpenDocumentBox.setFont( inputFromOpenDocumentBox.getFont().deriveFont( Font.PLAIN));
			inputFromOpenDocumentBox.setPreferredSize( new Dimension( inputFromOpenDocumentBox.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			inputFromOpenDocumentBox.setEditable(true);
			
			
			inputPromptButton	= new JRadioButton( "Prompt For Document");
			inputPromptButton.setPreferredSize( new Dimension( inputPromptButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

			inputPanel.add( inputPromptButton, FormLayout.FULL);

			ButtonGroup group = new ButtonGroup();
			group.add( inputCurrentButton);
			group.add( inputFromOpenDocumentButton);
			group.add( inputFromURLButton);
			group.add( inputPromptButton);
			
			inputPanel.add( inputCurrentButton, FormLayout.FULL);
			inputPanel.add( inputFromOpenDocumentButton, FormLayout.LEFT);
			inputPanel.add( inputFromOpenDocumentBox, FormLayout.RIGHT_FILL);				
			inputPanel.add( inputFromURLButton, FormLayout.LEFT);
			inputPanel.add( locationPanel, FormLayout.RIGHT_FILL);
			inputPanel.add( inputPromptButton, FormLayout.FULL);
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

			outputToNewDocumentButton	= new JRadioButton( "To New Document");
			outputToNewDocumentButton.setPreferredSize( new Dimension( outputToNewDocumentButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToNewDocumentButton, FormLayout.FULL);

			outputToInputButton	= new JRadioButton( "To Input Document");
			outputToInputButton.setPreferredSize( new Dimension( outputToInputButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToInputButton, FormLayout.FULL);
			
			outputPanel.add( getSeparator(), FormLayout.FULL_FILL);

			outputToBrowserButton	= new JCheckBox( "Open Output in Browser");
			outputToBrowserButton.setPreferredSize( new Dimension( outputToBrowserButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToBrowserButton, FormLayout.FULL);

			ButtonGroup outputGroup = new ButtonGroup();
			outputGroup.add( outputToFileButton);
			outputGroup.add( outputToNewDocumentButton);
			outputGroup.add( outputToInputButton);

			outputToNewDocumentButton.setSelected( true);
			outputToInputButton.setSelected( false);
			outputToFileButton.setSelected( false);
			outputToBrowserButton.setSelected( false);
		}
		
		return outputPanel;
	}

	private JPanel getXSLPanel() {
		if ( xslPanel == null) {
			xslPanel = new JPanel( new FormLayout( 10, 2));
			xslPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "XSL"),
										new EmptyBorder( 0, 5, 5, 5)));
			JPanel xslLocationPanel = new JPanel( new BorderLayout());

			xslLocationField = new JTextField();

			xslLocationButton = new JButton( "...");
			xslLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			xslLocationButton.setPreferredSize( new Dimension( xslLocationButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
			xslLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					xslLocationButtonPressed();
				}
			});
			
			xslLocationField.setEnabled( false);
			xslLocationButton.setEnabled( false);

			xslFromURLButton	= new JRadioButton( "From URL:");
			xslFromURLButton.setPreferredSize( new Dimension( xslFromURLButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
			xslFromURLButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					xslLocationField.setEnabled( xslFromURLButton.isSelected());
					xslLocationButton.setEnabled( xslFromURLButton.isSelected());
				}
			});

			xslLocationPanel.add( xslLocationField, BorderLayout.CENTER);
			xslLocationPanel.add( xslLocationButton, BorderLayout.EAST);

			xslCurrentButton	= new JRadioButton( "Current Document");
			xslCurrentButton.setPreferredSize( new Dimension( xslCurrentButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));

			xslFromOpenDocumentButton	= new JRadioButton( "Open Document");
			xslFromOpenDocumentButton.setPreferredSize( new Dimension( xslFromOpenDocumentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

			xslFromOpenDocumentBox = new JComboBox();
			//inputFromOpenDocumentBox.setFont( inputFromOpenDocumentBox.getFont().deriveFont( Font.PLAIN));
			xslFromOpenDocumentBox.setPreferredSize( new Dimension( xslFromOpenDocumentBox.getPreferredSize().width, inputLocationField.getPreferredSize().height));
			xslFromOpenDocumentBox.setEditable(true);
			
			

			
			xslPromptButton	= new JRadioButton( "Prompt For Stylesheet");
			xslPromptButton.setPreferredSize( new Dimension( xslPromptButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));

			xslPanel.add( xslPromptButton, FormLayout.FULL);

			xslFromPIsButton	= new JRadioButton( "Use Processing Instructions");
			xslFromPIsButton.setPreferredSize( new Dimension( xslFromPIsButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
			xslFromPIsButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					parameterButton.setEnabled( !xslFromPIsButton.isSelected());
				}
			});

			xslPanel.add( xslFromPIsButton, FormLayout.FULL);

			ButtonGroup xslGroup = new ButtonGroup();
			xslGroup.add( xslCurrentButton);
			xslGroup.add( xslFromOpenDocumentButton);
			xslGroup.add( xslFromURLButton);
			xslGroup.add( xslPromptButton);
			xslGroup.add( xslFromPIsButton);

			xslPanel.add( xslCurrentButton, FormLayout.FULL);
			xslPanel.add( xslFromOpenDocumentButton, FormLayout.LEFT);
			xslPanel.add( xslFromOpenDocumentBox, FormLayout.RIGHT_FILL);				
			xslPanel.add( xslFromURLButton, FormLayout.LEFT);
			xslPanel.add( xslLocationPanel, FormLayout.RIGHT_FILL);
			xslPanel.add( xslPromptButton, FormLayout.FULL);
			xslPanel.add( xslFromPIsButton, FormLayout.FULL);				

			xslCurrentButton.setSelected( false);
			xslFromOpenDocumentButton.setSelected( false);
			xslFromPIsButton.setSelected( false);
			xslPromptButton.setSelected( false);
			xslFromURLButton.setSelected( true);
			
			
			
			
			parameterButton = new JButton( "Parameters ...");
			parameterButton.setFont( parameterButton.getFont().deriveFont( Font.PLAIN));
			parameterButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					ParameterManagementDialog dialog = getParameterManagementDialog();
					dialog.setVisible(true);
					
				}
			});

			processorButton = new JButton( "Processor ...");
			processorButton.setFont( processorButton.getFont().deriveFont( Font.PLAIN));
			processorButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					processorButtonPressed();
				}
			});
			
			JPanel paramButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
			paramButtonPanel.add( parameterButton);
			paramButtonPanel.add( processorButton);

			xslPanel.add( paramButtonPanel, FormLayout.FULL_FILL);

			
		}

		return xslPanel;
	}
	private JDialog getProcessorDialog() {
		if ( processorDialog == null) {
		    processorDialog = new XngrDialogHeader( parent,true);
			processorDialog.setTitle( "XSLT Processor");
			
			JPanel processorPanel = new JPanel( new FormLayout( 10, 2));
			
			processorPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Processor"),
										new EmptyBorder( 0, 5, 5, 5)));
								
			ButtonGroup processorGroup = new ButtonGroup();		

			processorDefaultButton	= new JRadioButton( "Use Default Processor");
			processorPanel.add( processorDefaultButton, FormLayout.FULL);
			processorGroup.add( processorDefaultButton);

			processorPanel.add( getSeparator(), FormLayout.FULL_FILL);

			processorXalanButton	= new JRadioButton( "Xalan");
			processorPanel.add( processorXalanButton, FormLayout.FULL);
			processorGroup.add( processorXalanButton);

			processorSaxon1Button	= new JRadioButton( "Saxon (XSLT 1.X)");
			processorPanel.add( processorSaxon1Button, FormLayout.FULL);
			processorGroup.add( processorSaxon1Button);

			processorSaxon2Button	= new JRadioButton( "Saxon (XSLT 2.0)*");
			processorPanel.add( processorSaxon2Button, FormLayout.FULL);

			JLabel warning = new JLabel( "* Experimental version.");
			warning.setFont( warning.getFont().deriveFont( Font.PLAIN + Font.ITALIC));
			processorPanel.add( warning, FormLayout.FULL);

			processorGroup.add( processorSaxon2Button);
			JPanel dialogPanel = new JPanel(new BorderLayout());
			JPanel main = new JPanel( new BorderLayout());
			
			main.setBorder( new EmptyBorder( 2, 2, 5, 2));
			
			main.add( processorPanel, BorderLayout.CENTER);
			
			JButton closeButton = new JButton( "OK");
			closeButton.setMnemonic('O');
			closeButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					getProcessorDialog().setVisible(false);
				}
			});
			
			JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
			buttonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
			buttonPanel.add( closeButton);
			
			main.add( buttonPanel, BorderLayout.SOUTH);
			
		
			dialogPanel.add(main,BorderLayout.CENTER);
			
			processorDialog.setContentPane( dialogPanel);
			processorDialog.pack();

			processorDialog.setSize( new Dimension( 250, processorDialog.getSize().height));
			
		}
		
		
		return processorDialog;
	}
	private int getProcessor() {
		int type = ScenarioProperties.PROCESSOR_DEFAULT;
		
		if ( processorSaxon1Button.isSelected()) {
			type = ScenarioProperties.PROCESSOR_SAXON_XSLT1;
		} else if ( processorSaxon2Button.isSelected()) {
			type = ScenarioProperties.PROCESSOR_SAXON_XSLT2;
		} else if ( processorXalanButton.isSelected()) {
			type = ScenarioProperties.PROCESSOR_XALAN;
		}

			
		return type;
	}

	public void setProcessor( int type) {

		switch ( type) {
			case ScenarioProperties.PROCESSOR_SAXON_XSLT1:
				processorSaxon1Button.setSelected( true);
				break;
			case ScenarioProperties.PROCESSOR_SAXON_XSLT2:
				processorSaxon2Button.setSelected( true);
				break;
			case ScenarioProperties.PROCESSOR_XALAN:
				processorXalanButton.setSelected( true);
				break;
			default:
				processorDefaultButton.setSelected( true);
				break;
		}
		
	}

	private void processorButtonPressed() {
		JDialog dialog = getProcessorDialog();
		
		dialog.setLocationRelativeTo( ExecuteXSLTDialog.this);
		dialog.setVisible(true);
	}

	private ParameterManagementDialog getParameterManagementDialog() {
		if ( parameterManagementDialog == null) {
			parameterManagementDialog = new ParameterManagementDialog( parent);
		} 
		
		return parameterManagementDialog;
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
	
//	// Execute the values in the dialog...
//	public void execute() {
//		ScenarioUtilities.execute( document, getScenario());
//	}
	
	public ScenarioProperties getScenario() {
		ScenarioProperties properties = new ScenarioProperties();

		properties.setXSLEnabled( true);
		properties.setFOPEnabled( false);
		
		// set XML Input stuff
		if ( inputCurrentButton.isSelected()) {
			properties.setInputType( ScenarioProperties.INPUT_CURRENT_DOCUMENT);
		} else if ( inputFromOpenDocumentButton.isSelected()) {
			properties.setInputType( ScenarioProperties.INPUT_FROM_URL);
			properties.setInputFile( ((OpenDocument)inputFromOpenDocumentBox.getSelectedItem()).getDocument().getURL().toString());
		} else if ( inputPromptButton.isSelected()) {
			properties.setInputType( ScenarioProperties.INPUT_PROMPT_FOR_DOCUMENT);
		} else {
			properties.setInputType( ScenarioProperties.INPUT_FROM_URL);
			properties.setInputFile( inputLocationField.getText());
		}
			
		// set XSL stuff
		if ( xslCurrentButton.isSelected()) {
			properties.setXSLType( ScenarioProperties.XSL_CURRENT_DOCUMENT);
		} else if ( xslFromOpenDocumentButton.isSelected()) {
			properties.setXSLType( ScenarioProperties.INPUT_FROM_URL);
			properties.setXSLURL( ((OpenDocument)xslFromOpenDocumentBox.getSelectedItem()).getDocument().getURL().toString());
		} else if ( xslFromPIsButton.isSelected()) {
			properties.setXSLType( ScenarioProperties.XSL_USE_PROCESSING_INSTRUCTIONS);
		} else if ( xslPromptButton.isSelected()) {
			properties.setXSLType( ScenarioProperties.XSL_PROMPT_FOR_DOCUMENT);
		} else {
			properties.setXSLType( ScenarioProperties.XSL_FROM_URL);
			properties.setXSLURL( xslLocationField.getText());
		}

		if ( outputToFileButton.isSelected()) {
			properties.setOutputType( ScenarioProperties.OUTPUT_TO_FILE);
			properties.setOutputFile( outputLocationField.getText());
		} else if ( outputToInputButton.isSelected()) {
			properties.setOutputType( ScenarioProperties.OUTPUT_TO_INPUT);
		} else if ( outputToNewDocumentButton.isSelected()) {
			properties.setOutputType( ScenarioProperties.OUTPUT_TO_NEW_DOCUMENT);
		}
		
		Vector params = getParameterManagementDialog().getParameters();
		
		for ( int i = 0; i < params.size(); i++) {
		  ParameterProperties props = (ParameterProperties)params.elementAt(i);
		  
		properties.addParameter(props);
		  
		}

		properties.setProcessor(getProcessor());

		
		properties.setBrowserEnabled( outputToBrowserButton.isSelected());

		return properties;
	}

	private boolean setOpenDocuments()
	{
	  boolean exists = false;
	  
	  inputFromOpenDocumentBox.removeAllItems();
	  xslFromOpenDocumentBox.removeAllItems();
		
		
		Vector views = ((ExchangerEditor)parent).getViews();

		int result = 0;

		for ( int i = 0; i < views.size(); i++) {
			ExchangerView view = (ExchangerView) views.elementAt(i);
			//URL url = view.getDocument().getURL();
			//if (url != null)
			//{
			  //xslFromOpenDocumentField.addItem(url.toString());
			  //xslFromOpenDocumentField.addItem(view.getDocument().getName());
			//}
			
			URL url = view.getDocument().getURL();
			if (url != null)
			{
			  exists = true;
			  inputFromOpenDocumentBox.addItem(new OpenDocument(view.getDocument().getName(),view.getDocument())) ;
			  xslFromOpenDocumentBox.addItem(new OpenDocument(view.getDocument().getName(),view.getDocument())) ;
			}
		}
		
		inputFromOpenDocumentBox.setSelectedIndex(-1);
		xslFromOpenDocumentBox.setSelectedIndex(-1);
		
		return exists;

	}		
	
	public void show( ExchangerDocument document) {
		JPanel form = new JPanel( new FormLayout( 10, 2));

		
		boolean existsOpenDocuments = setOpenDocuments();

		if ( document != null && !document.isError()) {
			this.document = document;

			inputCurrentButton.setEnabled( true);
			
			if (existsOpenDocuments)
			  inputFromOpenDocumentButton.setEnabled( true);
			else
			  inputFromOpenDocumentButton.setEnabled( false);
			  
			  
			if ( isStylesheet( document)) { // XSL
				xslCurrentButton.setEnabled( true);
				
				if (existsOpenDocuments)
				  xslFromOpenDocumentButton.setEnabled( true);
				else
				  xslFromOpenDocumentButton.setEnabled( false);

				xslCurrentButton.setSelected( true);
				inputPromptButton.setSelected( true);
				inputFromURLButton.setSelected( false);
			} else { // normal XML
				if ( xslCurrentButton.isSelected()) {
					xslFromPIsButton.setSelected( true);
				}

				xslCurrentButton.setEnabled( false);
				if (existsOpenDocuments)
				  xslFromOpenDocumentButton.setEnabled( true);
				else
				  xslFromOpenDocumentButton.setEnabled( false);
				  
				inputCurrentButton.setSelected( true);
			}
		} else {
			//if ( xslCurrentButton.isSelected()) {
			//	xslFromPIsButton.setSelected( true);
			//}
		  
			xslCurrentButton.setEnabled( false);
			if (existsOpenDocuments)
			  xslFromOpenDocumentButton.setEnabled( true);
			else
			  xslFromOpenDocumentButton.setEnabled( false);
			xslPromptButton.setSelected( true);
			xslCurrentButton.setSelected( false);

			inputPromptButton.setSelected( true);
			inputFromURLButton.setSelected( false);
			inputCurrentButton.setEnabled( false);
			
			if (existsOpenDocuments)
			  inputFromOpenDocumentButton.setEnabled( true);
			else
			  inputFromOpenDocumentButton.setEnabled( false);
		}

//		outputToBrowserButton.setSelected( false);

		
		getParameterManagementDialog().setParameters( new Vector());
		getProcessorDialog();
		setProcessor(ScenarioProperties.PROCESSOR_DEFAULT );

		
		pack();
		setSize( new Dimension( 400, getSize().height));

		setLocationRelativeTo( parent);

		//super.setVisible(true);
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
	private JFileChooser getXSLFileChooser() {
		if ( xslFileChooser == null) {
			xslFileChooser = FileUtilities.createFileChooser();

			xslFileChooser.addChoosableFileFilter( new DefaultFileFilter( "xsl", "XSL Document"));
		} 

		File file = URLUtilities.toFile( xslLocationField.getText());

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

		xslFileChooser.setCurrentDirectory( file);
		xslFileChooser.rescanCurrentDirectory();
		
		return xslFileChooser;
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

		if ( file == null && xslLocationField != null && xslLocationField.isEnabled() && xslLocationField.isVisible()) {
			file = URLUtilities.toFile( xslLocationField.getText());
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

		if ( file == null && xslLocationField != null && xslLocationField.isEnabled() && xslLocationField.isVisible()) {
			file = URLUtilities.toFile( xslLocationField.getText());
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

/*	
	public String getOpenDocument() {
		String doc = (String)xslFromOpenDocumentField.getSelectedItem();
		
		if ( doc != null && doc.trim().length() > 0) {
			return doc;
		} else {
			return null;
		}
	}
	
	
	private void setOpenDocuments()
	{
	  xslFromOpenDocumentField.removeAllItems();
		
		
		Vector views = ((ExchangerEditor)parent).getViews();

		int result = 0;

		for ( int i = 0; i < views.size(); i++) {
			ExchangerView view = (ExchangerView) views.elementAt(i);
			URL url = view.getDocument().getURL();
			if (url != null)
			{
			  xslFromOpenDocumentField.addItem(url.toString());
			//xslFromOpenDocumentField.addItem(view.getDocument().getName());
			}
		}
		xslFromOpenDocumentField.setSelectedIndex(-1);
		

	}
*/	
} 
