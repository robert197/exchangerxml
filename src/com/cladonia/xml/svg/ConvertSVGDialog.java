/*
 * $Id: ConvertSVGDialog.java,v 1.6 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.svg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;
import org.bounce.DefaultFileFilter;
import org.bounce.FormConstraints;
import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;

/**
 * Execute a FOP transformation.
 *
 * @version	$Revision: 1.6 $, $Date: 2004/11/04 19:21:48 $
 * @author Dogsbay
 */
public class ConvertSVGDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);
	private static final FormConstraints LEFT_TOP	= new FormConstraints( FormConstraints.LEFT, FormConstraints.LEFT, FormConstraints.TOP);

	public static final int FORMAT_JPEG	= 0;
	public static final int FORMAT_PNG	= 1;
	public static final int FORMAT_TIFF	= 2;

//	private static final FormConstraints LEFT_ALIGN_RIGHT	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private ExchangerDocument document			= null;
	
	private DefaultFileFilter jpegFilter = null;
	private DefaultFileFilter tiffFilter = null;
	private DefaultFileFilter pngFilter = null;
	
	private JFileChooser outputFileChooser = null;
	private JFileChooser inputFileChooser = null;
	
	private ExchangerEditor parent			= null;

	private JTextField inputLocationField		= null;
	private JButton inputLocationButton			= null;
	private JRadioButton inputFromFileButton	= null;
	private JRadioButton inputCurrentButton		= null;

	private JRadioButton outputToJPEGButton		= null;
	private JRadioButton outputToPNGButton		= null;
	private JRadioButton outputToTIFFButton		= null;
	private JCheckBox indexedBox				= null;
	private JSlider qualitySlider				= null;

	private JTextField outputLocationField		= null;
	private JButton outputLocationButton		= null;

	/**
	 * The FO execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	public ConvertSVGDialog( ExchangerEditor parent) {
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Convert SVG");
		setDialogDescription( "Specify SVG Conversion settings");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		//removed for xngr-dialog
		super.okButton.setText("Convert");
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "Convert");
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
		ButtonGroup group = new ButtonGroup();
		
		JPanel centerPanel = new JPanel( new FormLayout( 10, 2));

		JPanel inputSelectionPanel = new JPanel( new FormLayout( 10, 2));
		inputSelectionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Input:"),
									new EmptyBorder( 0, 5, 10, 5)));
							
		inputCurrentButton	= new JRadioButton( "Current Document");
		group.add( inputCurrentButton);

		inputSelectionPanel.add( inputCurrentButton, FormLayout.FULL);

		inputLocationField = new JTextField();

		inputLocationButton = new JButton( "...");
		inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		inputLocationButton.setPreferredSize( new Dimension( inputLocationButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));
		inputLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				inputLocationButtonPressed();
			}
		});
		
		JPanel locationPanel = new JPanel( new BorderLayout());

		locationPanel.add( inputLocationField, BorderLayout.CENTER);
		locationPanel.add( inputLocationButton, BorderLayout.EAST);

		inputFromFileButton	= new JRadioButton( "From URL:");
		group.add( inputFromFileButton);
		inputFromFileButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				inputLocationButton.setEnabled( inputFromFileButton.isSelected());
				inputLocationField.setEnabled( inputFromFileButton.isSelected());
			}
		});
		inputFromFileButton.setPreferredSize( new Dimension( inputFromFileButton.getPreferredSize().width, inputLocationField.getPreferredSize().height));

		inputSelectionPanel.add( inputFromFileButton, FormLayout.LEFT);
		inputSelectionPanel.add( locationPanel, FormLayout.RIGHT_FILL);

		inputLocationButton.setEnabled( false);
		inputLocationField.setEnabled( false);

		centerPanel.add( inputSelectionPanel, FormLayout.FULL_FILL);

		group = new ButtonGroup();

		JPanel conversionPanel = new JPanel( new FormLayout( 10, 2));
		conversionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Format:"),
									new EmptyBorder( 0, 5, 10, 5)));
		
		outputToJPEGButton	= new JRadioButton( "JPEG (Joint Photographic Experts Group)");
		group.add( outputToJPEGButton);
		outputToJPEGButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				qualitySlider.setEnabled( outputToJPEGButton.isSelected());
			}
		});
		
		conversionPanel.add( outputToJPEGButton, FormLayout.FULL);
		
		qualitySlider = new JSlider();
		qualitySlider.setMinimum( 0);
		qualitySlider.setMaximum( 100);
		qualitySlider.setMajorTickSpacing( 10);
		qualitySlider.setMinorTickSpacing( 5);
		qualitySlider.setPaintTicks( true);
		qualitySlider.setPaintLabels( false);
		qualitySlider.setBorder( new EmptyBorder( 0, 0, 10, 0));
		qualitySlider.setValue( 100);

//		Hashtable labels = new Hashtable();
//		for (int i=0; i < 100; i+=10) {
//		    labels.put( new Integer( i), new JLabel("0."+i/10));
//		}
//
//		labels.put( new Integer(100), new JLabel("1"));
//		qualitySlider.setLabelTable( labels);
	
		JPanel sliderPanel = new JPanel( new FormLayout( 10, 2));
		sliderPanel.setBorder( new EmptyBorder( 0, 20, 0, 0));
		JLabel qualityLabel = new JLabel( "Quality:");
		qualityLabel.setVerticalAlignment( SwingConstants.TOP);
		sliderPanel.add( qualityLabel, LEFT_TOP);
		sliderPanel.add( qualitySlider, FormLayout.RIGHT_FILL);

		conversionPanel.add( sliderPanel, FormLayout.FULL_FILL);
		
		qualitySlider.setEnabled( false);

		outputToPNGButton	= new JRadioButton( "PNG (Portable Network Graphics)");
		group.add( outputToPNGButton);
		outputToPNGButton.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				indexedBox.setEnabled( outputToPNGButton.isSelected());
			}
		});
		
		conversionPanel.add( outputToPNGButton, FormLayout.FULL);
		
		indexedBox = new JCheckBox( "Reduce image to 256 color indexed PNG");
		indexedBox.setEnabled( false);

		JPanel indexPanel = new JPanel( new FormLayout( 10, 2));
		indexPanel.setBorder( new EmptyBorder( 0, 20, 10, 0));
		indexPanel.add( indexedBox, FormLayout.FULL);

		conversionPanel.add( indexPanel, FormLayout.FULL_FILL);

		outputToTIFFButton	= new JRadioButton( "TIFF (Tagged Image File Format)");
		group.add( outputToTIFFButton);
		conversionPanel.add( outputToTIFFButton, FormLayout.FULL);
		
		outputToJPEGButton.setSelected( true);
		
		centerPanel.add( conversionPanel, FormLayout.FULL_FILL);

		JPanel outputSelectionPanel = new JPanel( new FormLayout( 10, 2));
		outputSelectionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Output:"),
									new EmptyBorder( 0, 5, 10, 5)));
							
		outputLocationField = new JTextField();

		outputLocationButton = new JButton( "...");
		outputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		outputLocationButton.setPreferredSize( new Dimension( outputLocationButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
		outputLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				outputLocationButtonPressed();
			}
		});
		
		locationPanel = new JPanel( new BorderLayout());

		locationPanel.add( outputLocationField, BorderLayout.CENTER);
		locationPanel.add( outputLocationButton, BorderLayout.EAST);

		outputSelectionPanel.add( new JLabel( "To File:"), FormLayout.LEFT);
		outputSelectionPanel.add( locationPanel, FormLayout.RIGHT_FILL);

		centerPanel.add( outputSelectionPanel, FormLayout.FULL_FILL);

		main.add( centerPanel, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);
/*
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});
*/
		setContentPane( main);
		
		pack();
		
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
		
		setLocationRelativeTo( parent);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	public void show( ExchangerDocument document) {
		this.document = document;
	
		if ( document != null && !document.isError() && isSVG( document)) {
			inputCurrentButton.setEnabled( true);
			inputCurrentButton.setSelected( true);
		} else {
			inputCurrentButton.setEnabled( false);
			inputFromFileButton.setSelected( true);
		}
		
		super.show();
	}
	
	// Execute the values in the dialog...
	public int getFormat() {
		int format = FORMAT_JPEG;

		if ( outputToTIFFButton.isSelected()) {
			format = FORMAT_TIFF;
		} else if ( outputToPNGButton.isSelected()) {
			format = FORMAT_PNG;
		}
		
		return format;
	}

	// Execute the values in the dialog...
	private String getOutputExtension() {
		String ext = "jpg";

		if ( outputToTIFFButton.isSelected()) {
			ext = "tif";
		} else if ( outputToPNGButton.isSelected()) {
			ext = "png";
		}
		
		return ext;
	}

	// Execute the values in the dialog...
	private ImageTranscoder getTranscoder() {
		ImageTranscoder transcoder = null;

		if ( outputToTIFFButton.isSelected()) {
			transcoder = new TIFFTranscoder();

		} else if ( outputToPNGButton.isSelected()) {
			transcoder = new PNGTranscoder();
			transcoder.addTranscodingHint( PNGTranscoder.KEY_FORCE_TRANSPARENT_WHITE, new Boolean(true));

		    if( indexedBox.isSelected()){
				transcoder.addTranscodingHint( PNGTranscoder.KEY_INDEXED, new Integer(256));
		    }
		} else {
			transcoder = new JPEGTranscoder();

			transcoder.addTranscodingHint( JPEGTranscoder.KEY_QUALITY, new Float( qualitySlider.getValue()/100f));
		}
		
		transcoder.addTranscodingHint( TIFFTranscoder.KEY_XML_PARSER_CLASSNAME, "org.apache.xerces.parsers.SAXParser");

		return transcoder;
	}

	public float getQuality() {
		return qualitySlider.getValue()/100f;
	}

	public boolean isPNGIndexed() {
		return indexedBox.isSelected();
	}

	private void inputLocationButtonPressed() {
		JFileChooser chooser = getInputFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;
			
			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
	
				setText( inputLocationField, url.toString());
			} catch ( MalformedURLException e) {}
		}
	}

	private void outputLocationButtonPressed() {
		String location = outputLocationField.getText();
		File current = null;
		
		if ( location != null && location.length() > 2) {
			current = new File( location);
		}
		
		File file = FileUtilities.selectOutputFile( getOutputFileChooser(), getOutputExtension());
		
		if ( file != null) {
			setText( outputLocationField, file.getPath());
		}
	}

	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		if ( inputFileChooser == null) {
			inputFileChooser = FileUtilities.createFileChooser();
			inputFileChooser.addChoosableFileFilter( new DefaultFileFilter( "svg", "Scalable Vector Graphics"));
		} 
		
		URL url = null;
		
		if ( document != null) {
			url = document.getURL();
		}
		
		File file = URLUtilities.toFile( inputLocationField.getText());
		String outputFile = outputLocationField.getText();
		
		if ( file != null) {
			inputFileChooser.setCurrentDirectory( file);
		} else if ( !isEmpty( outputFile)) {
			inputFileChooser.setCurrentDirectory( new File( outputFile));
		} else if ( url != null && url.getProtocol().equals( "file")) {
			inputFileChooser.setCurrentDirectory( new File( url.getFile()));
		} else {
			inputFileChooser.setCurrentDirectory( FileUtilities.getLastOpenedFile());
		}
		
		inputFileChooser.rescanCurrentDirectory();
		
		return inputFileChooser;
	}


	protected void okButtonPressed() {
		final ImageTranscoder transcoder = getTranscoder();
		
		try {
			final TranscoderInput input = getTranscoderInput();
			
			if ( input == null) {
				MessageHandler.showMessage( "Input file not specified.\n"+
											"Please specify an input file.");
				return;
			}
			
			final OutputStream stream = getOutputStream();
			
			if ( stream == null) {
				MessageHandler.showMessage( "Output file not specified.\n"+
											"Please specify an output file.");
				return;
			}

			final TranscoderOutput output = new TranscoderOutput( stream);
			
			parent.setWait( true);
			parent.setStatus( "Converting SVG ...");

			Runnable runner = new Runnable() {
				public void run()  {
					try {
						transcoder.transcode( input, output);
				
						stream.flush();
						stream.close();
					} catch ( final IOException e) {
						SwingUtilities.invokeLater( new Runnable() {
							public void run() {
								MessageHandler.showError( e, "SVG Conversion Error");
							}
						});
					} catch ( final TranscoderException e) {
						final Exception x = e.getException();
						
						SwingUtilities.invokeLater( new Runnable() {
							public void run() {
								if ( x != null) {
									MessageHandler.showError( x, "SVG Conversion Error");
								} else {
									MessageHandler.showError( e, "SVG Conversion Error");
								}
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
			super.okButtonPressed();
		} catch ( IOException e) {
			MessageHandler.showError( e, "SVG Conversion Error");
		}
	}
	
	private TranscoderInput getTranscoderInput() throws IOException {
		ExchangerDocument inputDocument = null;
		
		if ( inputCurrentButton.isSelected()) {
			ByteArrayInputStream stream = new ByteArrayInputStream( document.getText().getBytes( document.getJavaEncoding()));
			InputStreamReader reader = new InputStreamReader( stream, document.getJavaEncoding());
			TranscoderInput input = new TranscoderInput( new BufferedReader( reader));
			if ( document.getURL() != null) {
				input.setURI( document.getURL().toString());
			}
			
			return input;
		} else {
			URL inputURL = URLUtilities.toURL( inputLocationField.getText());
	
			if ( inputURL != null) {
				return new TranscoderInput( inputURL.toString());
			} 
			return null;
		}
	}

	private TranscoderOutput getTranscoderOutput() throws IOException {
		String location = outputLocationField.getText();
		File outputFile = null;

		if ( location != null && location.length() > 1) {
			outputFile = new File( location);
		} else {
			return null;
		}

		return new TranscoderOutput( new BufferedOutputStream( new FileOutputStream( outputFile)));
	}

	private OutputStream getOutputStream() throws IOException {
		String location = outputLocationField.getText();
		File outputFile = null;

		if ( location != null && location.length() > 1) {
			outputFile = new File( location);
		} else {
			return null;
		}

		return new BufferedOutputStream( new FileOutputStream( outputFile));
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
	private JFileChooser getOutputFileChooser() {
		if ( outputFileChooser == null) {
			outputFileChooser = FileUtilities.createFileChooser();

			jpegFilter = new DefaultFileFilter( "jpg jpeg", "JPEG Image");
			pngFilter = new DefaultFileFilter( "png", "Portable Network Graphics");
			tiffFilter = new DefaultFileFilter( "tif tiff", "Tagged Image File Format");

			outputFileChooser.addChoosableFileFilter( jpegFilter);
			outputFileChooser.addChoosableFileFilter( pngFilter);
			outputFileChooser.addChoosableFileFilter( tiffFilter);
		} 
		
		if ( outputToJPEGButton.isSelected()) {
			outputFileChooser.setFileFilter( jpegFilter);
		} else if ( outputToPNGButton.isSelected()) {
			outputFileChooser.setFileFilter( pngFilter);
		} else if ( outputToTIFFButton.isSelected()) {
			outputFileChooser.setFileFilter( tiffFilter);
		}
		
		URL url = null;
		
		if ( document != null) {
			url = document.getURL();
		}

		File input = null;

		if ( inputLocationField.isEnabled()) {
			input = URLUtilities.toFile( inputLocationField.getText());
		}

		String output = outputLocationField.getText();
		
		if ( !isEmpty( output)) {
			outputFileChooser.setCurrentDirectory( new File( output));
		} else if ( input != null) {
			outputFileChooser.setCurrentDirectory( input);
		} else if ( url != null && url.getProtocol().equals( "file")) {
			outputFileChooser.setCurrentDirectory( new File( url.getFile()));
		} else {
			outputFileChooser.setCurrentDirectory( FileUtilities.getLastOpenedFile());
		}

		outputFileChooser.rescanCurrentDirectory();
		
		return outputFileChooser;
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
	
	private boolean isSVG( ExchangerDocument doc) {
		XElement root = doc.getRoot();
		
		if ( root != null) {
			if ( root.getName().equals( "svg")) {
				if ( root.getNamespaceURI().equals( "http://www.w3.org/2000/svg")) {
					return true;
				}
			}
		}
		
		return false;
	}

	public static void main( String[] args) {
		ConvertSVGDialog dialog = new ConvertSVGDialog( null);
		dialog.show();
	}
} 
