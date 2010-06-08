/*
 * $Id: ExecuteFODialog.java,v 1.5 2004/10/13 18:30:53 edankert Exp $
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
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.bounce.DefaultFileFilter;
import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;

/**
 * Execute a FOP transformation.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/13 18:30:53 $
 * @author Dogsbay
 */
public class ExecuteFODialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);
//	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private Vector names					= null;
	private ExchangerDocument document			= null;

	private JPanel foPanel = null;
	private JPanel outputPanel = null;

//	private JFileChooser outputFileChooser = null;
	private JFileChooser foFileChooser = null;
	
//	private DefaultFileFilter pdfFilter = null;
//	private DefaultFileFilter psFilter = null;
//	private DefaultFileFilter svgFilter = null;
//	private DefaultFileFilter txtFilter = null;

	private JFrame parent					= null;

	// The components that contain the values
	private JTextField foLocationField	= null;
	private JButton foLocationButton	= null;
	private JRadioButton foCurrentButton		= null;
	private JRadioButton foFromURLButton		= null;

	private JTextField outputLocationField		= null;
	private JButton outputLocationButton		= null;
	private JRadioButton outputToFileButton		= null;
	private JRadioButton outputToViewerButton	= null;
	private JRadioButton outputTypePDFButton	= null;
	private JRadioButton outputTypePSButton		= null;
	private JRadioButton outputTypeTXTButton	= null;
	private JRadioButton outputTypeSVGButton	= null;

	/**
	 * The FO execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	public ExecuteFODialog( JFrame parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Execute FO");
		setDialogDescription( "Specify FO Processing settings.");
//		setSize( SIZE);
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		//removed for xngr-dialog
		okButton.setText("Execute");
		okButton.setMnemonic('x');
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
		buttonPanel.add( cancelButton);
		*/
		JPanel form = new JPanel( new FormLayout( 10, 2));

		form.add( getFOPanel(), FormLayout.FULL_FILL);
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
	
	private void foLocationButtonPressed() {
		JFileChooser chooser = getFOFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( foLocationField, url.toString());
		}
	}

	private JPanel getFOPanel() {
		if ( foPanel == null) {
			foPanel = new JPanel( new FormLayout( 10, 2));
			foPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Input FO"),
										new EmptyBorder( 0, 5, 5, 5)));
			foLocationField = new JTextField();

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

			JPanel locationPanel = new JPanel( new BorderLayout());

			foFromURLButton	= new JRadioButton( "From URL:");
			foFromURLButton.setPreferredSize( new Dimension( foFromURLButton.getPreferredSize().width, foLocationField.getPreferredSize().height));
			foFromURLButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					foLocationField.setEnabled( foFromURLButton.isSelected());
					foLocationButton.setEnabled( foFromURLButton.isSelected());
				}
			});

			locationPanel.add( foFromURLButton, BorderLayout.WEST);
			locationPanel.add( foLocationField, BorderLayout.CENTER);
			locationPanel.add( foLocationButton, BorderLayout.EAST);

			foCurrentButton	= new JRadioButton( "Current Document");
			foCurrentButton.setPreferredSize( new Dimension( foCurrentButton.getPreferredSize().width, foLocationField.getPreferredSize().height));
			
			ButtonGroup group = new ButtonGroup();
			group.add( foCurrentButton);
			group.add( foFromURLButton);
			
			foPanel.add( foCurrentButton, FormLayout.FULL);
			foPanel.add( locationPanel, FormLayout.FULL_FILL);
		}

		return foPanel;
	}
	
	private JPanel getOutputPanel() {
		if ( outputPanel == null) {
			outputPanel = new JPanel( new FormLayout( 10, 2));
			outputPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Output"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			outputLocationField = new JTextField();

			outputToViewerButton	= new JRadioButton( "To Internal Viewer");
			outputToViewerButton.setPreferredSize( new Dimension( outputToViewerButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToViewerButton, FormLayout.FULL);

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
					outputTypeSVGButton.setEnabled( outputToFileButton.isSelected());
					outputTypeTXTButton.setEnabled( outputToFileButton.isSelected());
					outputTypePSButton.setEnabled( outputToFileButton.isSelected());
					outputTypePDFButton.setEnabled( outputToFileButton.isSelected());
				}
			});
			
			outputToFileButton.setPreferredSize( new Dimension( outputToFileButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			
			JPanel locationPanel = new JPanel( new BorderLayout());
			locationPanel.add( outputToFileButton, BorderLayout.WEST);
			locationPanel.add( outputLocationField, BorderLayout.CENTER);
			locationPanel.add( outputLocationButton, BorderLayout.EAST);

			outputPanel.add( locationPanel, FormLayout.FULL_FILL);

			outputTypePDFButton	= new JRadioButton( "PDF");
			outputTypePDFButton.setPreferredSize( new Dimension( outputTypePDFButton.getPreferredSize().width + 10, outputLocationField.getPreferredSize().height));
			outputTypePDFButton.setFont( outputTypePDFButton.getFont().deriveFont( Font.PLAIN));

			outputTypePSButton	= new JRadioButton( "PS");
			outputTypePSButton.setPreferredSize( new Dimension( outputTypePSButton.getPreferredSize().width + 10, outputLocationField.getPreferredSize().height));
			outputTypePSButton.setFont( outputTypePSButton.getFont().deriveFont( Font.PLAIN));

			outputTypeTXTButton	= new JRadioButton( "TXT");
			outputTypeTXTButton.setPreferredSize( new Dimension( outputTypeTXTButton.getPreferredSize().width + 10, outputLocationField.getPreferredSize().height));
			outputTypeTXTButton.setFont( outputTypeTXTButton.getFont().deriveFont( Font.PLAIN));

			outputTypeSVGButton	= new JRadioButton( "SVG");
			outputTypeSVGButton.setPreferredSize( new Dimension( outputTypeSVGButton.getPreferredSize().width + 10, outputLocationField.getPreferredSize().height));
			outputTypeSVGButton.setFont( outputTypeSVGButton.getFont().deriveFont( Font.PLAIN));

			JPanel typesPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
			typesPanel.add( outputTypePDFButton);
			typesPanel.add( outputTypePSButton);
			typesPanel.add( outputTypeTXTButton);
			typesPanel.add( outputTypeSVGButton);

			ButtonGroup typesGroup = new ButtonGroup();
			typesGroup.add( outputTypeSVGButton);
			typesGroup.add( outputTypeTXTButton);
			typesGroup.add( outputTypePSButton);
			typesGroup.add( outputTypePDFButton);

			outputPanel.add( typesPanel, FormLayout.FULL_FILL);

			ButtonGroup outputGroup = new ButtonGroup();
			outputGroup.add( outputToFileButton);
			outputGroup.add( outputToViewerButton);

			outputToViewerButton.setSelected( true);
			outputToFileButton.setSelected( false);

			outputTypeSVGButton.setEnabled( false);
			outputTypeTXTButton.setEnabled( false);
			outputTypePSButton.setEnabled( false);
			outputTypePDFButton.setEnabled( false);
			outputTypePDFButton.setSelected( true);
		}
		
		return outputPanel;
	}

	private void outputLocationButtonPressed() {
		JFileChooser chooser = getOutputFileChooser();
		
		File file = FileUtilities.selectOutputFile( chooser, null);
		
		if ( file != null) {
			FileFilter filter = chooser.getFileFilter();
			
			if ( filter == FileUtilities.getPDFFilter()) {
				outputTypePDFButton.setSelected( true);
			} else if ( filter == FileUtilities.getPSFilter()) {
				outputTypePSButton.setSelected( true);
			} else if ( filter == FileUtilities.getTXTFilter()) {
				outputTypeTXTButton.setSelected( true);
			} else if ( filter == FileUtilities.getSVGFilter()) {
				outputTypeSVGButton.setSelected( true);
			}

			setText( outputLocationField, file.getPath());
		}
	}

	protected void okButtonPressed() {
		super.okButtonPressed();
	}
	
	public ScenarioProperties getScenario() {
		ScenarioProperties properties = new ScenarioProperties();
		
		if ( foCurrentButton.isSelected()) {
			properties.setInputType( ScenarioProperties.INPUT_CURRENT_DOCUMENT);
		} else {
			properties.setInputType( ScenarioProperties.INPUT_FROM_URL);
			properties.setInputFile( foLocationField.getText());
		}

		properties.setFOPEnabled( true);
		
		if ( outputToViewerButton.isSelected()) {
			properties.setFOPOutputType( ScenarioProperties.FOP_OUTPUT_TO_VIEWER);
		} else {
			properties.setFOPOutputType( ScenarioProperties.FOP_OUTPUT_TO_FILE);

			int type = ScenarioProperties.FOP_TYPE_PDF;
			if ( outputTypePSButton.isSelected()) {
				type = ScenarioProperties.FOP_TYPE_PS;
			} else if ( outputTypeTXTButton.isSelected()) {
				type = ScenarioProperties.FOP_TYPE_TXT;
			} else if ( outputTypeSVGButton.isSelected()) {
				type = ScenarioProperties.FOP_TYPE_SVG;
			}
			properties.setFOPType( type);
			properties.setFOPOutputFile( outputLocationField.getText());
		}
		
		return properties;
	}

	public void show( ExchangerDocument document) {
		JPanel form = new JPanel( new FormLayout( 10, 2));

		if ( document != null && !document.isError() && isFormattingObject( document)) {
			this.document = document;

			foCurrentButton.setEnabled( true);
			foCurrentButton.setSelected( true);
		} else {
			this.document = null;

			foCurrentButton.setEnabled( false);
			foFromURLButton.setSelected( true);
		}
		
		pack();
		setSize( new Dimension( 400, getSize().height));

		setLocationRelativeTo( parent);
		super.show();
	}
	
	private boolean isFormattingObject( ExchangerDocument doc) {
		XElement root = doc.getRoot();

		return (root != null && root.getName().equals( "root") && root.getNamespaceURI().equals( "http://www.w3.org/1999/XSL/Format"));
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
	private JFileChooser getFOFileChooser() {
		if ( foFileChooser == null) {
			foFileChooser = FileUtilities.createFileChooser();
			foFileChooser.addChoosableFileFilter( new DefaultFileFilter( "fo", "Formatting Document"));
		} 

		File file = URLUtilities.toFile( foLocationField.getText());

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

		foFileChooser.setCurrentDirectory( file);
		foFileChooser.rescanCurrentDirectory();
		
		return foFileChooser;
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getOutputFileChooser() {
		JFileChooser chooser = FileUtilities.getFOFileChooser();
		
		if ( outputTypePDFButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getPDFFilter());
		} else if ( outputTypePSButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getPSFilter());
		} else if ( outputTypeSVGButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getSVGFilter());
		} else if ( outputTypeTXTButton.isSelected()) {
			chooser.setFileFilter( FileUtilities.getTXTFilter());
		}

		File file = URLUtilities.toFile( foLocationField.getText());

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
