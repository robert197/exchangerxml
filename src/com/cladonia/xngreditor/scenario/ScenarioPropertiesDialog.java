/*
 * $Id: ScenarioPropertiesDialog.java,v 1.10 2004/10/13 18:30:53 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.bounce.DefaultFileFilter;
import org.bounce.FormConstraints;
import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.transform.ScenarioUtilities;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialogHeader;

/**
 * The grammar properties dialog.
 *
 * @version	$Revision: 1.10 $, $Date: 2004/10/13 18:30:53 $
 * @author Dogsbay
 */
public class ScenarioPropertiesDialog extends XngrDialogHeader {
	private static final Dimension SIZE 					= new Dimension( 425, 550);
	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private ScenarioProperties scenario	= null;
	private Vector names				= null;
	private ExchangerDocument document	= null;

	private boolean allowOverride = false;
	
	private JFileChooser outputFileChooser = null;
	private JFileChooser xslFileChooser = null;
	
	private ParameterManagementDialog parameterManagementDialog	= null;

	private JButton processorButton	= null;
	
	private JFrame parent			= null;

	private JDialog processorDialog			= null;
	
	private JTextField nameField			= null;

	// input
	private JTextField inputLocationField	= null;
	private JButton inputLocationButton		= null;
	private JRadioButton inputFromURLButton	= null;
	private JRadioButton inputPromptButton	= null;
	private JRadioButton inputCurrentButton	= null;

	// xsl
	private JTextField xslLocationField		= null;
	private JButton xslLocationButton		= null;
	private JRadioButton xslFromURLButton	= null;
	private JRadioButton xslPromptButton	= null;
	private JRadioButton xslCurrentButton	= null;
	private JRadioButton xslPIsButton		= null;
	private JCheckBox enableXSLButton		= null;
	private JButton parameterButton			= null;

	// fo
	private JPanel foPanel					= null;
	private JCheckBox useFopCheck			= null;
	private JRadioButton foTypePDFButton	= null;
	private JRadioButton foTypePSButton		= null;
	private JRadioButton foTypeTXTButton	= null;
	private JRadioButton foTypeSVGButton	= null;
	private JRadioButton foToViewerButton	= null;
	private JRadioButton foToFileButton		= null;
	private JRadioButton foPromptButton		= null;
	private JTextField foLocationField		= null;
	private JButton foLocationButton		= null;

	private JPanel outputPanel						= null;
	private JRadioButton outputToNewDocumentButton	= null;
	private JRadioButton outputToInputButton		= null;
	private JRadioButton outputToFileButton			= null;
	private JRadioButton outputPromptButton			= null;
	private JRadioButton outputDoNothingButton			= null;
	private JTextField fileLocationField			= null;
	private JButton fileLocationButton				= null;

	private JCheckBox outputToBrowserButton			= null;
	private JCheckBox browserURLButton				= null;
	private JTextField browserOutputLocationField	= null;
	private JButton browserOutputLocationButton		= null;

	// processor
	private JRadioButton processorDefaultButton	= null;
	private JRadioButton processorXalanButton	= null;
	private JRadioButton processorSaxon1Button	= null;
	private JRadioButton processorSaxon2Button	= null;

	private JPanel genericOutputPanel			= null;

	private JButton executeButton		= null;
	private JButton okButton		= null;
	private JButton cancelButton		= null;
	
	private ScenarioPropertiesDialogListener listener = null;

	public ScenarioPropertiesDialog( JFrame parent, boolean allowOverride) {
		this( parent);
		
		this.allowOverride = allowOverride;
	}

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public ScenarioPropertiesDialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Scenario Properties");
		setDialogDescription( "Specify the Scenario settings.");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// GENERAL
		JPanel centerPanel = new JPanel( new FormLayout( 10, 2));
		centerPanel.setBorder( new EmptyBorder( 5, 5, 10, 5));
		
		// description

		JPanel descriptionPanel = new JPanel( new FormLayout( 10, 2));
		descriptionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Description"),
									new EmptyBorder( 0, 5, 5, 5)));

		nameField = new JTextField();

		JLabel nameLabel = new JLabel("Name:");
		descriptionPanel.add( nameLabel, FormLayout.LEFT);
		descriptionPanel.add( nameField, FormLayout.RIGHT_FILL);
		
		centerPanel.add( descriptionPanel, FormLayout.FULL_FILL);

		// >>> input
		JPanel inputPanel = new JPanel( new FormLayout( 10, 2));
		inputPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "XML"),
									new EmptyBorder( 0, 5, 5, 5)));

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
		
		inputFromURLButton	= new JRadioButton( "From URL:");
		inputFromURLButton.setPreferredSize( new Dimension( inputFromURLButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
		inputFromURLButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				inputLocationButton.setEnabled( inputFromURLButton.isSelected());
				inputLocationField.setEnabled( inputFromURLButton.isSelected());
			}
		});
		
		inputLocationPanel.add( inputFromURLButton, BorderLayout.WEST);
		inputLocationPanel.add( inputLocationField, BorderLayout.CENTER);
		inputLocationPanel.add( inputLocationButton, BorderLayout.EAST);

		inputPanel.add( inputLocationPanel, FormLayout.FULL_FILL);

		inputCurrentButton	= new JRadioButton( "Current Selected Document");
		inputCurrentButton.setPreferredSize( new Dimension( inputCurrentButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
		inputCurrentButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				if ( enableXSLButton.isSelected()) {
					xslCurrentButton.setEnabled( !inputCurrentButton.isSelected());
				}
			}
		});

		inputPanel.add( inputCurrentButton, FormLayout.FULL);

		inputPromptButton	= new JRadioButton( "Prompt For Document");
		inputPromptButton.setPreferredSize( new Dimension( inputPromptButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

		inputPanel.add( inputPromptButton, FormLayout.FULL);
		
		ButtonGroup inputGroup = new ButtonGroup();
		inputGroup.add( inputFromURLButton);
		inputGroup.add( inputCurrentButton);
		inputGroup.add( inputPromptButton);
		
		centerPanel.add( inputPanel, FormLayout.FULL_FILL);
		// <<< input

		// >>> xsl
		JPanel xslPanel = new JPanel( new FormLayout( 10, 2));
		xslPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "XSL"),
									new EmptyBorder( 0, 5, 5, 5)));

		JPanel xslLocationPanel = new JPanel( new BorderLayout());
		
		enableXSLButton = new JCheckBox( "Use XSL Stylesheet");
		enableXSLButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				if ( xslFromURLButton.isSelected()) {
					xslLocationButton.setEnabled( enableXSLButton.isSelected());
					xslLocationField.setEnabled( enableXSLButton.isSelected());
				}

				xslFromURLButton.setEnabled( enableXSLButton.isSelected());
				xslPromptButton.setEnabled( enableXSLButton.isSelected());
				xslPromptButton.setSelected( true);
				processorButton.setEnabled( enableXSLButton.isSelected());
				
				if ( !inputCurrentButton.isSelected()) {
					xslCurrentButton.setEnabled( enableXSLButton.isSelected());
				}
				xslPIsButton.setEnabled( enableXSLButton.isSelected());

				if ( !xslPIsButton.isSelected()) {
					parameterButton.setEnabled( enableXSLButton.isSelected());
				}
			}
		});
		

		xslPanel.add( enableXSLButton, FormLayout.FULL);
		xslPanel.add( getSeparator(), FormLayout.FULL_FILL);

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
		
		xslFromURLButton	= new JRadioButton( "From URL:");
		xslFromURLButton.setPreferredSize( new Dimension( xslFromURLButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		xslFromURLButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				xslLocationButton.setEnabled( xslFromURLButton.isSelected());
				xslLocationField.setEnabled( xslFromURLButton.isSelected());
			}
		});
		
		xslLocationPanel.add( xslFromURLButton, BorderLayout.WEST);
		xslLocationPanel.add( xslLocationField, BorderLayout.CENTER);
		xslLocationPanel.add( xslLocationButton, BorderLayout.EAST);

		xslPanel.add( xslLocationPanel, FormLayout.FULL_FILL);

		xslCurrentButton	= new JRadioButton( "Current Selected Document");
		xslCurrentButton.setPreferredSize( new Dimension( xslCurrentButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		xslCurrentButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				inputCurrentButton.setEnabled( !xslCurrentButton.isSelected());
			}
		});

		xslPanel.add( xslCurrentButton, FormLayout.FULL);

		xslPromptButton	= new JRadioButton( "Prompt For Stylesheet");
		xslPromptButton.setPreferredSize( new Dimension( xslPromptButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));

		xslPanel.add( xslPromptButton, FormLayout.FULL);

		xslPIsButton	= new JRadioButton( "Use Processing Instructions");
		xslPIsButton.setPreferredSize( new Dimension( xslPIsButton.getPreferredSize().width, xslLocationField.getPreferredSize().height));
		xslPIsButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				parameterButton.setEnabled( !xslPIsButton.isSelected());
			}
		});

		xslPanel.add( xslPIsButton, FormLayout.FULL);

		ButtonGroup xslGroup = new ButtonGroup();
		xslGroup.add( xslFromURLButton);
		xslGroup.add( xslCurrentButton);
		xslGroup.add( xslPromptButton);
		xslGroup.add( xslPIsButton);
		
		xslPromptButton.setSelected( true);
		xslFromURLButton.setSelected( false);

		xslPanel.add( getSeparator(), FormLayout.FULL_FILL);

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
		
		centerPanel.add( xslPanel, FormLayout.FULL_FILL);
		
		inputCurrentButton.setSelected( true);
		inputFromURLButton.setSelected( false);

		inputLocationField.setEnabled( false);
		inputLocationButton.setEnabled( false);

		xslLocationButton.setEnabled( false);
		xslLocationField.setEnabled( false);
		xslFromURLButton.setEnabled( false);
		xslPromptButton.setEnabled( false);
		xslCurrentButton.setEnabled( false);
		xslPIsButton.setEnabled( false);
		processorButton.setEnabled( false);
		parameterButton.setEnabled( false);
		// <<< xsl
		
		// >>> output
		JPanel outputPanel = new JPanel( new FormLayout( 10, 2));
		outputPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Output"),
									new EmptyBorder( 0, 5, 5, 5)));
							
				
		useFopCheck	= new JCheckBox( "Use FO Processor");
		useFopCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				genericOutputPanel.removeAll();
				
				if ( useFopCheck.isSelected()) {
					genericOutputPanel.add( getFOPanel(), BorderLayout.CENTER);
				} else {
					genericOutputPanel.add( getOutputPanel(), BorderLayout.CENTER);
				}
				
				genericOutputPanel.doLayout();
				genericOutputPanel.revalidate();
				genericOutputPanel.repaint();
			}
		});
		
		outputPanel.add( useFopCheck, FormLayout.FULL_FILL);
		outputPanel.add( getSeparator(), FormLayout.FULL_FILL);
		
		genericOutputPanel = new JPanel( new BorderLayout());
		outputPanel.add( genericOutputPanel, FormLayout.FULL_FILL);
		
		genericOutputPanel.add( getOutputPanel(), BorderLayout.CENTER);
		getFOPanel();
		
		centerPanel.add( outputPanel, FormLayout.FULL_FILL);
		// << output

		//removed for xngr-dialog
		//okButton.setText(confirmText);
		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
//		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "OK");
		okButton.setMnemonic( 'O');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		executeButton = new JButton( "Execute");
		executeButton.setMnemonic( 'E');
//		executeButton.setFont( executeButton.getFont().deriveFont( Font.PLAIN));
		executeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				executeButtonPressed();
			}
		});

		getProcessorDialog();

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER));
		buttonPanel.setBorder( new EmptyBorder( 0, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( executeButton);
		buttonPanel.add( cancelButton);

		main.add( centerPanel, BorderLayout.NORTH);
		main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
		pack();
		
		setSize( new Dimension( 425, getSize().height));
		
		setLocationRelativeTo( parent);
	}
	
	private JPanel getFOPanel() {
		if ( foPanel == null) {
			foPanel = new JPanel( new FormLayout( 10, 2));
			
			foLocationField = new JTextField();

			foToViewerButton	= new JRadioButton( "To Internal Viewer");
			foToViewerButton.setPreferredSize( new Dimension( foToViewerButton.getPreferredSize().width, foLocationField.getPreferredSize().height));
			foPanel.add( foToViewerButton, FormLayout.FULL);

			foPromptButton	= new JRadioButton( "Prompt For File");
			foPromptButton.setPreferredSize( new Dimension( foPromptButton.getPreferredSize().width, foLocationField.getPreferredSize().height));
			foPanel.add( foPromptButton, FormLayout.FULL);
			foPromptButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					foTypeSVGButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
					foTypeTXTButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
					foTypePSButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
					foTypePDFButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
				}
			});

			foLocationButton = new JButton( "...");
			foLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			foLocationButton.setPreferredSize( new Dimension( foLocationButton.getPreferredSize().width, foLocationField.getPreferredSize().height));
			foLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					foLocationButtonPressed();
				}
			});
			
			foLocationField.setEnabled( false);
			foLocationButton.setEnabled( false);

			foToFileButton	= new JRadioButton( "To File:");
			foToFileButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					foLocationButton.setEnabled( foToFileButton.isSelected());
					foLocationField.setEnabled( foToFileButton.isSelected());
					foTypeSVGButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
					foTypeTXTButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
					foTypePSButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
					foTypePDFButton.setEnabled( foToFileButton.isSelected() || foPromptButton.isSelected());
				}
			});
			
			foToFileButton.setPreferredSize( new Dimension( foToFileButton.getPreferredSize().width, foLocationField.getPreferredSize().height));
			
			JPanel locationPanel = new JPanel( new BorderLayout());
			locationPanel.add( foToFileButton, BorderLayout.WEST);
			locationPanel.add( foLocationField, BorderLayout.CENTER);
			locationPanel.add( foLocationButton, BorderLayout.EAST);

			foPanel.add( locationPanel, FormLayout.FULL_FILL);

			foTypePDFButton	= new JRadioButton( "PDF");
			foTypePDFButton.setPreferredSize( new Dimension( foTypePDFButton.getPreferredSize().width + 10, foLocationField.getPreferredSize().height));
			foTypePDFButton.setFont( foTypePDFButton.getFont().deriveFont( Font.PLAIN));

			foTypePSButton	= new JRadioButton( "PS");
			foTypePSButton.setPreferredSize( new Dimension( foTypePSButton.getPreferredSize().width + 10, foLocationField.getPreferredSize().height));
			foTypePSButton.setFont( foTypePSButton.getFont().deriveFont( Font.PLAIN));

			foTypeTXTButton	= new JRadioButton( "TXT");
			foTypeTXTButton.setPreferredSize( new Dimension( foTypeTXTButton.getPreferredSize().width + 10, foLocationField.getPreferredSize().height));
			foTypeTXTButton.setFont( foTypeTXTButton.getFont().deriveFont( Font.PLAIN));

			foTypeSVGButton	= new JRadioButton( "SVG");
			foTypeSVGButton.setPreferredSize( new Dimension( foTypeSVGButton.getPreferredSize().width + 10, foLocationField.getPreferredSize().height));
			foTypeSVGButton.setFont( foTypeSVGButton.getFont().deriveFont( Font.PLAIN));

			JPanel typesPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
			typesPanel.add( foTypePDFButton);
			typesPanel.add( foTypePSButton);
			typesPanel.add( foTypeTXTButton);
			typesPanel.add( foTypeSVGButton);

			ButtonGroup typesGroup = new ButtonGroup();
			typesGroup.add( foTypeSVGButton);
			typesGroup.add( foTypeTXTButton);
			typesGroup.add( foTypePSButton);
			typesGroup.add( foTypePDFButton);

			foPanel.add( typesPanel, FormLayout.FULL_FILL);

			ButtonGroup foGroup = new ButtonGroup();
			foGroup.add( foToFileButton);
			foGroup.add( foToViewerButton);
			foGroup.add( foPromptButton);

			foToViewerButton.setSelected( true);
			foToFileButton.setSelected( false);

			foTypeSVGButton.setEnabled( false);
			foTypeTXTButton.setEnabled( false);
			foTypePSButton.setEnabled( false);
			foTypePDFButton.setEnabled( false);

			foTypePDFButton.setSelected( true);
		}
		
		return foPanel;
	}
	
	private JPanel getOutputPanel() {
		if ( outputPanel == null) {
			outputPanel = new JPanel( new FormLayout( 10, 2));
					
			outputToFileButton	= new JRadioButton( "To File:");
			outputToFileButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					fileLocationButton.setEnabled( outputToFileButton.isSelected());
					fileLocationField.setEnabled( outputToFileButton.isSelected());
				}
			});
			
			JPanel fileLocationPanel = new JPanel( new BorderLayout());

			fileLocationField = new JTextField();

			fileLocationButton = new JButton( "...");
			fileLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			fileLocationButton.setPreferredSize( new Dimension( fileLocationButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			fileLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					fileLocationButtonPressed();
				}
			});
			
			outputToFileButton.setPreferredSize( new Dimension( outputToFileButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			
			fileLocationPanel.add( outputToFileButton, BorderLayout.WEST);
			fileLocationPanel.add( fileLocationField, BorderLayout.CENTER);
			fileLocationPanel.add( fileLocationButton, BorderLayout.EAST);

			outputPanel.add( fileLocationPanel, FormLayout.FULL_FILL);

			outputPromptButton	= new JRadioButton( "Prompt For File");
			outputPromptButton.setPreferredSize( new Dimension( outputPromptButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			outputPanel.add( outputPromptButton, FormLayout.FULL);

			outputToNewDocumentButton	= new JRadioButton( "To New Document");
			outputToNewDocumentButton.setPreferredSize( new Dimension( outputToNewDocumentButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			outputPanel.add( outputToNewDocumentButton, FormLayout.FULL);

			outputToInputButton	= new JRadioButton( "To Input Document");
			outputToInputButton.setPreferredSize( new Dimension( outputToInputButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			outputPanel.add( outputToInputButton, FormLayout.FULL);

			outputDoNothingButton	= new JRadioButton( "Do Nothing");
			outputDoNothingButton.setPreferredSize( new Dimension( outputDoNothingButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			outputPanel.add( outputDoNothingButton, FormLayout.FULL);

			ButtonGroup outputGroup = new ButtonGroup();
			outputGroup.add( outputToFileButton);
			outputGroup.add( outputToNewDocumentButton);
			outputGroup.add( outputToInputButton);
			outputGroup.add( outputPromptButton);
			outputGroup.add( outputDoNothingButton);

			outputPanel.add( getSeparator(), FormLayout.FULL_FILL);

			outputToBrowserButton	= new JCheckBox( "Open Output in Browser");
			outputToBrowserButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					if ( outputToBrowserButton.isSelected()) {
						browserURLButton.setEnabled( true);
					} else {
						browserURLButton.setSelected( false);
						browserURLButton.setEnabled( false);
					}
				}
			});
			outputToBrowserButton.setPreferredSize( new Dimension( outputToBrowserButton.getPreferredSize().width, fileLocationField.getPreferredSize().height));
			outputPanel.add( outputToBrowserButton, FormLayout.FULL);
			
			JPanel browserPanel = new JPanel( new BorderLayout());
			browserPanel.setBorder( new EmptyBorder( 0, 20, 0, 0));
			
			browserURLButton	= new JCheckBox( "Alternative URL:");
			browserOutputLocationField = new JTextField();

			browserOutputLocationButton = new JButton( "...");
			browserOutputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			browserOutputLocationButton.setPreferredSize( new Dimension( browserOutputLocationButton.getPreferredSize().width, browserOutputLocationField.getPreferredSize().height));
			browserOutputLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					browserOutputLocationButtonPressed();
				}
			});
			
			browserURLButton.setPreferredSize( new Dimension( browserURLButton.getPreferredSize().width, browserOutputLocationField.getPreferredSize().height));
			browserURLButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					browserOutputLocationField.setEnabled( browserURLButton.isSelected());
					browserOutputLocationButton.setEnabled( browserURLButton.isSelected());
				}
			});
			browserPanel.add( browserURLButton, BorderLayout.WEST);
			browserPanel.add( browserOutputLocationField, BorderLayout.CENTER);
			browserPanel.add( browserOutputLocationButton, BorderLayout.EAST);

			outputToBrowserButton.setSelected( true);
			browserURLButton.setSelected( true);
			outputToNewDocumentButton.setSelected( true);
			outputToInputButton.setSelected( false);
			outputToFileButton.setSelected( false);
			fileLocationField.setEnabled( false);
			fileLocationButton.setEnabled( false);

			outputPanel.add( browserPanel, FormLayout.FULL_FILL);
		}
		
		return outputPanel;
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
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	private void foLocationButtonPressed() {
		JFileChooser chooser = getFOFileChooser();

		File file = FileUtilities.selectOutputFile( chooser, null);
		
		if ( file != null) {
			FileFilter filter = chooser.getFileFilter();
			
			if ( filter == FileUtilities.getPDFFilter()) {
				foTypePDFButton.setSelected( true);
			} else if ( filter == FileUtilities.getPSFilter()) {
				foTypePSButton.setSelected( true);
			} else if ( filter == FileUtilities.getTXTFilter()) {
				foTypeTXTButton.setSelected( true);
			} else if ( filter == FileUtilities.getSVGFilter()) {
				foTypeSVGButton.setSelected( true);
			}

			setText( foLocationField, file.getPath());
		}
	}

	private JDialog getProcessorDialog() {
		if ( processorDialog == null) {
		    processorDialog = new XngrDialogHeader( parent,true);
			processorDialog.setTitle( "Advanced");
			
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
		
	private void fileLocationButtonPressed() {
		JFileChooser chooser = getOutputFileChooser();

		File file = FileUtilities.selectOutputFile( chooser, null);

		if ( file != null) {
			setText( fileLocationField, file.getPath());
		}
	}

	private void browserOutputLocationButtonPressed() {
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
			
			setText( browserOutputLocationField, url.toString());
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

	private void processorButtonPressed() {
		JDialog dialog = getProcessorDialog();
		
		dialog.setLocationRelativeTo( ScenarioPropertiesDialog.this);
		dialog.setVisible(true);
	}

	private void executeButtonPressed() {
		ScenarioProperties properties = new ScenarioProperties();
		fillProperties( properties);

 		// execute the scenario...
		ScenarioUtilities.execute( document, properties);
	}
	
	private void fillProperties( ScenarioProperties properties) {
		
		// name
		properties.setName( nameField.getText());
		
		// xml ...
		properties.setInputType( getInputType());
		if ( inputLocationField.isEnabled()) {
			properties.setInputFile( inputLocationField.getText());
		}
		
		Vector params = properties.getParameters();
		for ( int i = 0; i < params.size(); i++) {
			ParameterProperties param = (ParameterProperties)params.elementAt(i);
			properties.removeParameter( param);
		}

		// xsl...
		if ( enableXSLButton.isSelected()) {
			properties.setXSLEnabled( true);
			properties.setXSLType( getXSLType());
			
			if ( xslLocationField.isEnabled()) {
		 		properties.setXSLURL( xslLocationField.getText());
			}
		
			if ( !xslPIsButton.isSelected()) {
				Vector parameters = getParameterManagementDialog().getParameters();

				for ( int i = 0; i < parameters.size(); i++) {
					ParameterProperties param = (ParameterProperties)parameters.elementAt(i);
					
					properties.addParameter( new ParameterProperties( param));
				}
			}
		} else {
			properties.setXSLEnabled( false);
		}

		// output ...
		if ( useFopCheck.isSelected()) {
			properties.setFOPEnabled( true);
			properties.setFOPType( getFOPType());
			properties.setFOPOutputType( getFOPOutputType());

			if ( foLocationField.isEnabled()) {
				properties.setFOPOutputFile( foLocationField.getText());
			}

			properties.setBrowserEnabled( false);
			properties.setBrowserURL( "");
		} else {
			properties.setFOPEnabled( false);

			properties.setOutputType( getOutputType());

			if ( fileLocationField.isEnabled()) {
				properties.setOutputFile( fileLocationField.getText());
			}
			
			properties.setBrowserEnabled( outputToBrowserButton.isSelected());

			if ( browserOutputLocationField.isEnabled()) {
				properties.setBrowserURL( browserOutputLocationField.getText());
			} else {
				properties.setBrowserURL( "");
			}
		}

		properties.setProcessor( getProcessor());
	}

	protected void okButtonPressed() {
		if ( checkName( nameField.getText())) {
						
			fillProperties( scenario);
			
			cancelled = false;

			setVisible(false);

			if ( listener != null) {
				listener.scenarioUpdated( scenario);
			}
		}
	}

	public void show( ScenarioProperties properties, ExchangerDocument document, Vector names) {
		show( properties, document, names, null);
	}

	public void show( ScenarioProperties properties, ExchangerDocument document, Vector names, ScenarioPropertiesDialogListener listener) {
//		updatedParameters = new Vector();
		this.listener = listener;
		this.names = names;
		this.scenario = properties;
		this.document = document;
		
		String name = properties.getName();
		
		if ( name != null && name.trim().length() > 0) {
			setTitle( "Scenario Properties", "\""+name+"\" Scenario Properties");
		} else {
			setTitle( "Scenario Properties", "\"New\" Scenario Properties");
		}

		setModal( listener == null);
		executeButton.setVisible( listener != null);
		
		getParameterManagementDialog().setParameters( properties.getParameters());
		
		setText( nameField, properties.getName());
		
		setText( inputLocationField, "");
		setText( xslLocationField, "");
		setText( foLocationField, "");
		setText( fileLocationField, "");
		setText( browserOutputLocationField, "");

		// xml ...
		setInputType( properties.getInputType());
		if ( inputLocationField.isEnabled()) {
			setText( inputLocationField, properties.getInputURL());
		}
		
//		parameterTableModel.setScenario( properties);

		// xsl...
		if ( properties.isXSLEnabled()) {
			enableXSLButton.setSelected( true);
			setXSLType( properties.getXSLType());
			
			if ( xslLocationField.isEnabled()) {
				setText( xslLocationField, properties.getXSLURL());
			}
		} else {
			enableXSLButton.setSelected( false);
		}

		// output ...
		if ( properties.isFOPEnabled()) {
			useFopCheck.setSelected( true);
			
			setFOPType( properties.getFOPType());
			setFOPOutputType( properties.getFOPOutputType());

			if ( foLocationField.isEnabled()) {
				setText( foLocationField, properties.getFOPOutputFile());
			}

			outputToBrowserButton.setSelected( false);
			browserURLButton.setSelected( false);
		} else {
			useFopCheck.setSelected( false);

			setOutputType( properties.getOutputType());

			if ( fileLocationField.isEnabled()) {
				setText( fileLocationField, properties.getOutputFile());
			}

			if ( properties.isBrowserEnabled())	{
				outputToBrowserButton.setSelected( true);
				String location = properties.getBrowserURL();
				
				if ( location != null && location.trim().length() > 0) {
					browserURLButton.setSelected( true);
					setText( browserOutputLocationField, location);
				} else {
					browserURLButton.setSelected( false);
				}
			} else {
				outputToBrowserButton.setSelected( false);
				browserURLButton.setSelected( false);
			}
		}
		
		setProcessor( properties.getProcessor());

		//super.setVisible(true);
		super.show();
	}

	private int getInputType() {
		int type = ScenarioProperties.INPUT_CURRENT_DOCUMENT;
		
		if ( inputFromURLButton.isSelected()) {
			type = ScenarioProperties.INPUT_FROM_URL;
		} else if ( inputPromptButton.isSelected()) {
			type = ScenarioProperties.INPUT_PROMPT_FOR_DOCUMENT;
		}
		
		return type;
	}

	private void setInputType( int type) {
		switch ( type) {
			case ScenarioProperties.INPUT_FROM_URL:
				inputFromURLButton.setSelected( true);
				break;
			case ScenarioProperties.INPUT_PROMPT_FOR_DOCUMENT:
				inputPromptButton.setSelected( true);
				break;
			default:
				inputCurrentButton.setSelected( true);
				break;
		}
	}

	private int getOutputType() {
		int type = ScenarioProperties.OUTPUT_TO_NEW_DOCUMENT;
		
		if ( outputToInputButton.isSelected()) {
			type = ScenarioProperties.OUTPUT_TO_INPUT;
		} else if ( outputToFileButton.isSelected()) {
			type = ScenarioProperties.OUTPUT_TO_FILE;
		} else if ( outputPromptButton.isSelected()) {
			type = ScenarioProperties.OUTPUT_PROMPT_FOR_FILE;
		} else if ( outputDoNothingButton.isSelected()) {
			type = ScenarioProperties.OUTPUT_DO_NOTHING;
		}
		
		return type;
	}

	private void setOutputType( int type) {
		switch ( type) {
			case ScenarioProperties.OUTPUT_TO_FILE:
				outputToFileButton.setSelected( true);
				break;
			case ScenarioProperties.OUTPUT_TO_INPUT:
				outputToInputButton.setSelected( true);
				break;
			case ScenarioProperties.OUTPUT_PROMPT_FOR_FILE:
				outputPromptButton.setSelected( true);
				break;
			case ScenarioProperties.OUTPUT_DO_NOTHING:
				outputDoNothingButton.setSelected( true);
				break;
			default:
				outputToNewDocumentButton.setSelected( true);
				break;
		}
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

	private void setProcessor( int type) {
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

	private int getXSLType() {
		int type = ScenarioProperties.XSL_FROM_URL;
		
		if ( xslCurrentButton.isSelected()) {
			type = ScenarioProperties.XSL_CURRENT_DOCUMENT;
		} else if ( xslPromptButton.isSelected()) {
			type = ScenarioProperties.XSL_PROMPT_FOR_DOCUMENT;
		} else if ( xslPIsButton.isSelected()) {
			type = ScenarioProperties.XSL_USE_PROCESSING_INSTRUCTIONS;
		}
		
		return type;
	}

	private void setXSLType( int type) {
		switch ( type) {
			case ScenarioProperties.XSL_CURRENT_DOCUMENT:
				xslCurrentButton.setSelected( true);
				break;
			case ScenarioProperties.XSL_FROM_URL:
				xslFromURLButton.setSelected( true);
				break;
			case ScenarioProperties.XSL_PROMPT_FOR_DOCUMENT:
				xslPromptButton.setSelected( true);
				break;
			default:
				xslPIsButton.setSelected( true);
				break;
		}
	}

	private int getFOPType() {
		int type = ScenarioProperties.FOP_TYPE_PDF;
		
		if ( foTypePSButton.isSelected()) {
			type = ScenarioProperties.FOP_TYPE_PS;
		} else if ( foTypeTXTButton.isSelected()) {
			type = ScenarioProperties.FOP_TYPE_TXT;
		} else if ( foTypeSVGButton.isSelected()) {
			type = ScenarioProperties.FOP_TYPE_SVG;
		}
		
		return type;
	}

	private void setFOPType( int type) {
		switch ( type) {
			case ScenarioProperties.FOP_TYPE_PS:
				foTypePSButton.setSelected( true);
				break;
			case ScenarioProperties.FOP_TYPE_TXT:
				foTypeTXTButton.setSelected( true);
				break;
			case ScenarioProperties.FOP_TYPE_SVG:
				foTypeSVGButton.setSelected( true);
				break;
			default:
				foTypePDFButton.setSelected( true);
				break;
		}
	}

	private int getFOPOutputType() {
		int type = ScenarioProperties.FOP_OUTPUT_TO_VIEWER;
		
		if ( foToFileButton.isSelected()) {
			type = ScenarioProperties.FOP_OUTPUT_TO_FILE;
		} else if ( foPromptButton.isSelected()) {
			type = ScenarioProperties.FOP_OUTPUT_PROMPT_FOR_FILE;
		}
		
		return type;
	}

	private void setFOPOutputType( int type) {
		switch ( type) {
			case ScenarioProperties.FOP_OUTPUT_TO_FILE:
				foToFileButton.setSelected( true);
				break;
			case ScenarioProperties.FOP_OUTPUT_PROMPT_FOR_FILE:
				foPromptButton.setSelected( true);
				break;
			default:
				foToViewerButton.setSelected( true);
				break;
		}
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

//	private JLabel createPlainLabel( String text) {
//		JLabel label = new JLabel( text);
//		label.setFont( label.getFont().deriveFont( Font.PLAIN));
//
//		return label;
//	}
//
//	private JCheckBox createPlainCheckBox( String text) {
//		JCheckBox check = new JCheckBox( text);
//		check.setFont( check.getFont().deriveFont( Font.PLAIN));
//
//		return check;
//	}

	private ParameterManagementDialog getParameterManagementDialog() {
		if ( parameterManagementDialog == null) {
			parameterManagementDialog = new ParameterManagementDialog( parent);
		} 
		
		return parameterManagementDialog;
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
		
		if ( file == null && fileLocationField.isEnabled() && fileLocationField.isVisible() && !isEmpty( fileLocationField.getText())) {
			file = new File( fileLocationField.getText());
		}
		
		if ( file == null && foLocationField.isEnabled() && foLocationField.isVisible() && !isEmpty( foLocationField.getText())) {
			file = new File( foLocationField.getText());
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
	private JFileChooser getFOFileChooser() {
		JFileChooser chooser = FileUtilities.getFOFileChooser();
//		if ( foFileChooser == null) {
//			foFileChooser = new JFileChooser();
//
//			pdfFilter = new DefaultFileFilter( "pdf", "Adobe Portable Document Format");
//			psFilter = new DefaultFileFilter( "ps", "Post Script");
//			svgFilter = new DefaultFileFilter( "svg", "Scalable Vector Graphics");
//			txtFilter = new DefaultFileFilter( "txt", "Plain Text");
//
//			foFileChooser.addChoosableFileFilter( pdfFilter);
//			foFileChooser.addChoosableFileFilter( psFilter);
//			foFileChooser.addChoosableFileFilter( svgFilter);
//			foFileChooser.addChoosableFileFilter( txtFilter);
//		} 

		if ( foTypePDFButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getPDFFilter());
		} else if ( foTypePSButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getPSFilter());
		} else if ( foTypeSVGButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getSVGFilter());
		} else if ( foTypeTXTButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getTXTFilter());
		}

		File file = null;

		if ( !isEmpty( foLocationField.getText())) {
			file = new File( foLocationField.getText());
		}

		if ( file == null && inputLocationField != null && inputLocationField.isEnabled()) {
			file = URLUtilities.toFile( inputLocationField.getText());
		}
		
		if ( file == null && xslLocationField != null && xslLocationField.isEnabled()) {
			file = URLUtilities.toFile( xslLocationField.getText());
		}
		
		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}
		
		chooser.rescanCurrentDirectory();
		
		return chooser;
	}

	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getOutputFileChooser() {
		if ( outputFileChooser == null) {
			outputFileChooser = FileUtilities.createFileChooser();
		} 

		File file = null;

		if ( !isEmpty( fileLocationField.getText())) {
			file = new File( fileLocationField.getText());
		}

		if ( file == null && inputLocationField != null && inputLocationField.isEnabled()) {
			file = URLUtilities.toFile( inputLocationField.getText());
		}
		
		if ( file == null && xslLocationField != null && xslLocationField.isEnabled()) {
			file = URLUtilities.toFile( xslLocationField.getText());
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

		if ( file == null && xslLocationField != null && xslLocationField.isEnabled()) {
			file = URLUtilities.toFile( xslLocationField.getText());
		}
		
		if ( file == null && fileLocationField.isEnabled() && fileLocationField.isVisible() && !isEmpty( fileLocationField.getText())) {
			file = new File( fileLocationField.getText());
		}
		
		if ( file == null && foLocationField.isEnabled() && foLocationField.isVisible() && !isEmpty( foLocationField.getText())) {
			file = new File( foLocationField.getText());
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
	
	private boolean checkName( String name) {
		if ( isEmpty( name)) {
			MessageHandler.showMessage( "Please specify a Name for this Scenario.");
			return false;
		}
		
		for ( int i = 0; i < names.size(); i++) {
			if ( ((String)names.elementAt(i)).equalsIgnoreCase( name)) {
				if ( allowOverride) {
					int result = MessageHandler.showConfirm( 
												"A Scenario with the name \""+name+"\" exists already.\n"+
												"Do you want to override the existing Scenario?");
					if ( result == JOptionPane.YES_OPTION) {
						return true;
					} else {
						return false;
					}
				} else {
					MessageHandler.showMessage( "A Scenario with the name \""+name+"\" exists already.\n"+
												"Please specify another name.");
					return false;
				}
			}
		}
		
		return true;
	}

//	private class ParameterTableModel extends AbstractTableModel {
//		private Vector parameters = null;
//		private ScenarioProperties properties = null;
//		
//		public ParameterTableModel() {
//			parameters = new Vector();
//		}
//		
//		public void setScenario( ScenarioProperties props) {
//			this.properties = props;
//			
//			setParameters( props.getParameters());
//		}
//
//		private void setParameters( Vector parameters) {
//			for ( int i = 0; i < parameters.size(); i++) {
//				updatedParameters.addElement( new ParameterProperties( (ParameterProperties)parameters.elementAt(i)));
//			}
//
//			this.parameters = updatedParameters;
//			
//			fireTableDataChanged();
//		}
//
//		public void addParameter( ParameterProperties props) {
//			parameters.addElement( props);
//			fireTableRowsInserted( parameters.size()-2, parameters.size()-1);
//		}
//
//		public void updateParameter( ParameterProperties props) {
//			int index = parameters.indexOf( props);
//
//			fireTableRowsUpdated( index, index);
//		}
//
//		public void removeParameter( ParameterProperties props) {
//			int index = parameters.indexOf( props);
//			parameters.removeElement( props);
//
//			fireTableRowsDeleted( index, index);
//		}
//
//		public int getRowCount() {
//			return parameters.size();
//		}
//		
//		public ParameterProperties getParameter( int row) {
//			ParameterProperties result = null;
//			
//			if ( parameters.size() >= row) {
//				result = (ParameterProperties)parameters.elementAt( row);
//			}
//
//			return result;
//		}
//
//		public int getRow( ParameterProperties props) {
//			return parameters.indexOf( props);
//		}
//
//		public String getColumnName( int column) {
//			String name = "";
//
//			if ( column == 0) {
//				name = "Name";
//			} else if ( column == 1) {
//				name = "Value";
//			}
//			
//			return name;
//		}
//
//		public Class getColumnClass( int column) {
//			return String.class;
//		}
//
//		public int getColumnCount() {
//			return 2;
//		}
//
//		public String getName( int row) {
//			return ((ParameterProperties)parameters.elementAt( row)).getName();
//		}
//
//		public Object getValueAt( int row, int column) {
//			Object result = null;
//			
//			if ( column == 0) {
//				result = ((ParameterProperties)parameters.elementAt( row)).getName();
//			} else if ( column == 1) {
//				result = ((ParameterProperties)parameters.elementAt( row)).getValue();
//			}
//			
//			return result;
//		}
//	}
} 
