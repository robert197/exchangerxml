/*
 * $Id: ConverterDialog.java,v 1.5 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.converter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.filechooser.FileFilter;

import org.bounce.DefaultFileFilter;
import org.bounce.FormConstraints;
import org.bounce.FormLayout;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;

/**
 * Convert one schema-type to another.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/11/04 19:21:48 $
 * @author Dogsbay
 */
public class ConverterDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 225);
	
	private static final FormConstraints LEFT_ALIGN_RIGHT = new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private ExchangerEditor parent = null;
	
	private JFileChooser inputChooser = null;
	private JFileChooser outputChooser = null;
			
	// The components that contain the values
	private JTextField inputField	= null;
	private JTextField outputField	= null;
	private JButton inputButton		= null;
	private JButton outputButton	= null;

	private JComboBox inputTypeBox	= null;
	private JComboBox outputTypeBox	= null;
	
	private DefaultFileFilter xmlFileFilter = null;
	private DefaultFileFilter xsdFileFilter = null;
	private DefaultFileFilter dtdFileFilter = null;
	private DefaultFileFilter rngFileFilter = null;
	private DefaultFileFilter rncFileFilter = null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public ConverterDialog( ExchangerEditor parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Schema Converter");
		setDialogDescription( "Specify Input and Output Grammars");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// >>> GENERAL
		JPanel generalPanel = new JPanel( new FormLayout( 10, 2));
		generalPanel.setBorder( new EmptyBorder( 5, 5, 15, 5));
	
		// >>> Input
		JPanel inputPanel = new JPanel( new FormLayout( 10, 2));
		inputPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Input Grammar"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		inputField = new JTextField();
		inputField.addCaretListener( new CaretListener() {
			public void caretUpdate( CaretEvent e) {
				checkConvert();
			}
		});

		xmlFileFilter = new DefaultFileFilter( "xml", "XML Document");
		xsdFileFilter = new DefaultFileFilter( "xsd", "XML Schema");
		dtdFileFilter = new DefaultFileFilter( "dtd", "Document Type Definition");
		rngFileFilter = new DefaultFileFilter( "rng", "Relax NG (XML Syntax)");
		rncFileFilter = new DefaultFileFilter( "rnc", "Relax NG (Compact Syntax)");

		inputTypeBox = new JComboBox( new String[] { "DTD (Document Type Definition)", "RNC (Relax NG Compact Syntax)", "RNG (Relax NG XML Syntax)", "XML (XML Document)"});
		inputTypeBox.setPreferredSize( new Dimension( inputTypeBox.getPreferredSize().width, inputField.getPreferredSize().height));
		inputTypeBox.setFont( inputTypeBox.getFont().deriveFont( Font.PLAIN));
		
		JLabel typeLabel = new JLabel( "Type:");

		inputPanel.add( typeLabel, FormLayout.LEFT);
		inputPanel.add( inputTypeBox, FormLayout.RIGHT_FILL);

		inputButton = new JButton( "...");
		inputButton.setMargin( new Insets( 0, 10, 0, 10));
		inputButton.setPreferredSize( new Dimension( inputButton.getPreferredSize().width, inputField.getPreferredSize().height));
		inputButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				inputButtonPressed();
			}
		});
		
		JPanel locPanel = new JPanel( new BorderLayout());
		locPanel.add( inputField, BorderLayout.CENTER);
		locPanel.add( inputButton, BorderLayout.EAST);

		JLabel inputLabel = new JLabel( "Input URL:");

		inputPanel.add( inputLabel, FormLayout.LEFT);
		inputPanel.add( locPanel, FormLayout.RIGHT_FILL);

		generalPanel.add( inputPanel, FormLayout.FULL_FILL);
		// <<< Input

		// >>> Output
		JPanel outputPanel = new JPanel( new FormLayout( 10, 2));
		outputPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Output Grammar"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		outputField = new JTextField();
		outputField.addCaretListener( new CaretListener() {
			public void caretUpdate( CaretEvent e) {
				checkConvert();
			}
		});

		outputTypeBox = new JComboBox( new String[] { "XSD (XML Schema)", "DTD (Document Type Definition)", "RNC (Relax NG Compact Syntax)", "RNG (Relax NG XML Syntax)"});
		outputTypeBox.setPreferredSize( new Dimension( outputTypeBox.getPreferredSize().width, inputField.getPreferredSize().height));
		outputTypeBox.setFont( outputTypeBox.getFont().deriveFont( Font.PLAIN));
//		outputTypeBox.addActionListener( new ActionListener() {
//			public void actionPerformed( ActionEvent e) {
		outputTypeBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				int index = outputTypeBox.getSelectedIndex();
				String text = null;
				
				if ( index == 0) {
					text = replaceExtension( outputField.getText(), "xsd");
				} else if ( index == 1) {
					text = replaceExtension( outputField.getText(), "dtd");
				} else if ( index == 2) {
					text = replaceExtension( outputField.getText(), "rnc");
				} else if ( index == 3) {
					text = replaceExtension( outputField.getText(), "rng");
				}
				
				setText( outputField, text);
			}
		});
		
		typeLabel = new JLabel( "Type:");
		outputPanel.add( typeLabel, FormLayout.LEFT);
		outputPanel.add( outputTypeBox, FormLayout.RIGHT_FILL);

		outputButton = new JButton( "...");
		outputButton.setMargin( new Insets( 0, 10, 0, 10));
		outputButton.setPreferredSize( new Dimension( outputButton.getPreferredSize().width, outputField.getPreferredSize().height));
		outputButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				outputButtonPressed();
			}
		});
		
		locPanel = new JPanel( new BorderLayout());
		locPanel.add( outputField, BorderLayout.CENTER);
		locPanel.add( outputButton, BorderLayout.EAST);

		JLabel outputLabel = new JLabel( "Ouput File:");

		outputPanel.add( outputLabel, FormLayout.LEFT);
		outputPanel.add( locPanel, FormLayout.RIGHT_FILL);

		generalPanel.add( outputPanel, FormLayout.FULL_FILL);
		// <<< Output

		//removed for XngrDialog
		super.okButton.setText("Convert");
		/*okButton = new JButton( "Convert");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		JPanel convertButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		convertButtonPanel.add( okButton);

		generalPanel.add( getSeparator(), FormLayout.FULL_FILL);
		generalPanel.add( convertButtonPanel, FormLayout.FULL_FILL);

		cancelButton = new JButton( "Close");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);
		convertButtonPanel.add( Box.createHorizontalStrut(5));
		convertButtonPanel.add( cancelButton);
*/
//		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
//		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
//		buttonPanel.add( okButton);
//		buttonPanel.add( cancelButton);

		main.add( generalPanel, BorderLayout.CENTER);
//		main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
		setLocationRelativeTo( parent);
	}

	private void inputButtonPressed() {
		JFileChooser chooser = getInputFileChooser();

		int value = chooser.showOpenDialog( parent);
		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			FileFilter filter = chooser.getFileFilter();
			
			if ( filter == dtdFileFilter) {
				inputTypeBox.setSelectedIndex( 0);
			} else if ( filter == rncFileFilter) {
				inputTypeBox.setSelectedIndex( 1);
			} else if ( filter == rngFileFilter) {
				inputTypeBox.setSelectedIndex( 2);
			} else {
				inputTypeBox.setSelectedIndex( 3);
			}
			
			URL url = null;

			try {
				//url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( inputField, url.toString());

			checkConvert();
		}
	}	

	private void checkConvert() {
		okButton.setEnabled( !isEmpty( inputField.getText()) && !isEmpty( outputField.getText()));
	}

	private void outputButtonPressed() {
		JFileChooser chooser = getOutputFileChooser();

		int value = chooser.showOpenDialog( parent);
		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			FileFilter filter = chooser.getFileFilter();
			String extension = "xsd";
			
			if ( filter == dtdFileFilter) {
				outputTypeBox.setSelectedIndex( 1);
				extension = "dtd";
			} else if ( filter == rncFileFilter) {
				outputTypeBox.setSelectedIndex( 2);
				extension = "rnc";
			} else if ( filter == rngFileFilter) {
				outputTypeBox.setSelectedIndex( 3);
				extension = "rng";
			} else {
				outputTypeBox.setSelectedIndex( 0);
			}
			
			if ( file.exists()) {
				// show a dialog asking if it is okay to overwrite the existing file
				int result = JOptionPane.showConfirmDialog( 
								parent, 
								"The schema \""+file.getName()+"\" already exists,\n do you want to replace the existing schema?",
								"Please Confirm",
								JOptionPane.YES_NO_OPTION);

				if ( result != JOptionPane.YES_OPTION) {
					return;
				}
			}

			setText( outputField, replaceExtension( file.toString(), extension));

			checkConvert();
		}
	}	

	protected void okButtonPressed() {
		try {
			URL url = null;
			
			try {
				url = new URL( inputField.getText());
			} catch ( MalformedURLException e) {
				url = XngrURLUtilities.getURLFromFile(new File( inputField.getText()));
			}

			File file = new File( outputField.getText());
			
			int inputIndex = inputTypeBox.getSelectedIndex();
			int inputType = Converter.TYPE_DTD;
			if ( inputIndex == 1) {
				inputType = Converter.TYPE_RNC;
			} else if ( inputIndex == 2) {
				inputType = Converter.TYPE_RNG;
			} else if ( inputIndex == 3) {
				inputType = Converter.TYPE_XML;
			}

			int outputIndex = outputTypeBox.getSelectedIndex();
			int outputType = Converter.TYPE_XSD;
			if ( outputIndex == 1) {
				outputType = Converter.TYPE_DTD;
			} else if ( outputIndex == 2) {
				outputType = Converter.TYPE_RNC;
			} else if ( outputIndex == 3) {
				outputType = Converter.TYPE_RNG;
			}
			
			convert( url, inputType, file, outputType);
			
			super.okButtonPressed();
		} catch ( MalformedURLException mue) {
			// should no longer happen, sine I use file: as protocol when it goes wrong
			MessageHandler.showError( "Invalid URL: "+inputField.getText(), mue, "Invalid URL");
			mue.printStackTrace();
//		} catch ( IOException ioe) {
//			MessageHandler.showError( ioe, "Input/Output File Error");
//			ioe.printStackTrace();
//		} catch ( SAXException se) {
//			MessageHandler.showError( se, "Parse Error");
//			se.printStackTrace();
		}
	}
	
	private void convert( final URL input, final int inputType, final File output, final int outputType) {
		parent.setWait( true);
		parent.setStatus( "Converting Schema ...");

		Runnable runner = new Runnable() {
			public void run()  {
				try {
					Converter.convert( input, inputType, output, outputType);
				} catch ( final SAXException se) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							int outputIndex = outputTypeBox.getSelectedIndex();
							String outputType = "XML Schema";
							if ( outputIndex == 1) {
								outputType = "DTD";
							} else if ( outputIndex == 2) {
								outputType = "RelaxNG (Compact)";
							} else if ( outputIndex == 3) {
								outputType = "RelaxNG";
							}

							if ( se instanceof SAXParseException) {
								SAXParseException spe = (SAXParseException)se;
								String systemId = spe.getSystemId();
								
								if ( systemId == null) {
									systemId = inputField.getText();
								}

								MessageHandler.showError( "Could not create "+outputType+" Document\n"+
														  "Error in \""+systemId+"\"\n"+
														  "On line "+spe.getLineNumber()+", column "+spe.getColumnNumber()+"\n"+spe.getMessage(), "Schema Conversion Error");
							} else {
								MessageHandler.showError( "Could not create "+outputType+" Document\n"+se.getMessage(), "Schema Conversion Error");
							}
						}
					});
				} catch ( FileNotFoundException fne) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							MessageHandler.showError( "File \""+inputField.getText()+"\" could not be found.", "File Not Found");
						}
					});
				} catch ( final IOException ioe) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							MessageHandler.showError( ioe, "Input/Output File Error");
						}
					});
				} finally {
					parent.setWait( false);
					parent.setStatus( "Done");
				}
			}
		};
		
		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
	}
	
	public void show( URL url) {
		if ( url != null) {
			String location = url.toString();
			setText( inputField, location);
			
			if ( location.endsWith( "dtd")) {
				inputTypeBox.setSelectedIndex( 0);
			} else if ( location.endsWith( "rnc")) {
				inputTypeBox.setSelectedIndex( 1);
			} else if ( location.endsWith( "rng")) {
				inputTypeBox.setSelectedIndex( 2);
			} else {
				inputTypeBox.setSelectedIndex( 3);
			}
			
		} else {
			setText( inputField, null);
		}

		setText( outputField, null);
		checkConvert();
//		outputTypeBox.setSelectedIndex( 0); // leave as is
	
		super.show();
	}
	
	

	private String replaceExtension( String location, String extension) {
		if ( !isEmpty( location)) {
			location = stripExtension( location);
			location = location+"."+extension;
		}

		return location;
	}

	private String getSchemaLocation( URL url, File file) {
		URL result = null;

		try {
			result = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			String schema = getFile( url);
			schema = stripExtension( schema);
			schema = schema+".xsd";
			
			result = new URL( result, schema);
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		return result.toString();
	}

	private String stripExtension( String string) {
		int dot = string.lastIndexOf(".");
	
		if (dot < 0) {
			return string;
		}
	
		return string.substring( 0, dot);
	}

	private String getFile( URL url) {
		String value = url.toString();
		
		int slash = value.lastIndexOf("/");
	
		if (slash < 0) {
			return value;
		}
	
		return value.substring( slash);
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

	private File getSelectedOutputChooserFile() {
		ExchangerDocument doc = parent.getDocument();
		File result = null;
		
		if ( !isEmpty( outputField.getText())) {
			result = new File( outputField.getText());
		}
		
		if ( result == null) {
			result = URLUtilities.toFile( inputField.getText());
		}
		
		if ( result == null && doc != null) {
			result = URLUtilities.toFile( doc.getURL());
		}
		
		if ( result == null) {
			result = FileUtilities.getLastOpenedFile();
		}

		return result;
	}

	private File getSelectedInputChooserFile() {
		ExchangerDocument doc = parent.getDocument();
		File result = URLUtilities.toFile( inputField.getText());
		
		if ( result == null && !isEmpty( outputField.getText())) {
			result = new File( outputField.getText());
		}
		
		if ( result == null && doc != null) {
			result = URLUtilities.toFile( doc.getURL());
		}
		
		if ( result == null) {
			result = FileUtilities.getLastOpenedFile();
		}

		return result;
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		if ( inputChooser == null) {
			inputChooser = FileUtilities.createFileChooser();
			inputChooser.addChoosableFileFilter( dtdFileFilter);
			inputChooser.addChoosableFileFilter( rngFileFilter);
			inputChooser.addChoosableFileFilter( rncFileFilter);
			inputChooser.addChoosableFileFilter( xmlFileFilter);
		} 
		
		int index = inputTypeBox.getSelectedIndex();
		
		if ( index == 3) {
			inputChooser.setFileFilter( xmlFileFilter);
		} else if ( index == 1) {
			inputChooser.setFileFilter( rncFileFilter);
		} else if ( index == 2) {
			inputChooser.setFileFilter( rngFileFilter);
		} else {
			inputChooser.setFileFilter( dtdFileFilter);
		}
		
		File file = getSelectedInputChooserFile();

		if ( file != null) {
			inputChooser.setSelectedFile( file);
		}

		inputChooser.rescanCurrentDirectory();
		
		return inputChooser;
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getOutputFileChooser() {
		if ( outputChooser == null) {
			outputChooser = FileUtilities.createFileChooser();
			outputChooser.addChoosableFileFilter( xsdFileFilter);
			outputChooser.addChoosableFileFilter( dtdFileFilter);
			outputChooser.addChoosableFileFilter( rngFileFilter);
			outputChooser.addChoosableFileFilter( rncFileFilter);
		} 
		
		int index = outputTypeBox.getSelectedIndex();
		
		if ( index == 1) {
			outputChooser.setFileFilter( dtdFileFilter);
		} else if ( index == 2) {
			outputChooser.setFileFilter( rncFileFilter);
		} else if ( index == 3) {
			outputChooser.setFileFilter( rngFileFilter);
		} else {
			outputChooser.setFileFilter( xsdFileFilter);
		}
		
		File file = getSelectedOutputChooserFile();

		if ( file != null) {
			outputChooser.setSelectedFile( file);
		}

		outputChooser.rescanCurrentDirectory();
		
		return outputChooser;
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
