/*
 * $Id: SignDocumentDialog.java,v 1.10 2004/10/27 13:26:17 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.security.signature;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JComboBox;
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

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.PrefixNamespaceMappingPanel;

/**
 * The XQuery details dialog.
 *
 * @version	$Revision: 1.10 $, $Date: 2004/10/27 13:26:17 $
 * @author Dogsbay
 */
public class SignDocumentDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);

	private Vector names				= null;
	private ExchangerDocument document	= null;

	private JPanel inputPanel		= null;
	private JPanel transformsPanel	= null;
	private JPanel signaturePanel	= null;
	private JPanel outputPanel		= null;

	private JFileChooser outputFileChooser	= null;
	private JFileChooser inputFileChooser	= null;
			
	private JFrame parent			= null;

	// The components that contain the values
	private JLabel idLabel	= null;

	private JTextField inputLocationField	= null;
	private JButton inputLocationButton		= null;
	private JRadioButton inputCurrentButton		= null;
	private JRadioButton inputFromURLButton		= null;

	private JComboBox xpathField			= null;
	private JComboBox c14nMethodField			= null;

	private JTextField idField	= null;
	private JRadioButton envelopeButton		= null;
	private JRadioButton detachedButton		= null;

	private JRadioButton outputToSameDocumentButton	= null;
	private JRadioButton outputToNewDocumentButton		= null;
	private JRadioButton outputToFileButton				= null;

	private JTextField outputLocationField		= null;
	private JButton outputLocationButton		= null;
	
	private ConfigurationProperties props = null;
	private Vector predicateHistory = null;
	
	private PrefixNamespaceMappingPanel mappingPanel = null;
	

	/**
	 * The XQuery execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	public SignDocumentDialog( JFrame parent,ConfigurationProperties props) {
		super( parent, true);
		
		this.parent = parent;
		this.props = props;
		
		setResizable( false);
		setTitle( "Sign XML Document");
		setDialogDescription( "Specify Signature information");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		//removed for xngr-dialog
		super.okButton.setText("Sign");
		/*
		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "Sign");
		okButton.setMnemonic( 'S');
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

		// fill all three panels...
		form.add( getInputPanel(), FormLayout.FULL_FILL);
		form.add( getTransformsPanel(), FormLayout.FULL_FILL);
		form.add( getSignaturePanel(), FormLayout.FULL_FILL);
		form.add( getOutputPanel(), FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);
//		removed for xngr-dialog
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				// save the prefix mappings
				mappingPanel.save();
				hide();
			}
		});
*/
		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
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
			inputCurrentButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					if ( inputCurrentButton.isSelected()) {
						detachedButton.setEnabled( false);
						envelopeButton.setSelected( true);
					} else {
						detachedButton.setEnabled( true);
					}
				}
			});
			
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
			
			outputToNewDocumentButton	= new JRadioButton( "To New Document");
			outputToNewDocumentButton.setPreferredSize( new Dimension( outputToNewDocumentButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToNewDocumentButton, FormLayout.FULL);


			outputToSameDocumentButton	= new JRadioButton( "To Input Document");
			outputToSameDocumentButton.setPreferredSize( new Dimension( outputToSameDocumentButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
			outputPanel.add( outputToSameDocumentButton, FormLayout.FULL);

			
//			outputLocationButton = new JButton( "...");
//			outputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
//			outputLocationButton.setPreferredSize( new Dimension( outputLocationButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
//			outputLocationButton.addActionListener( new ActionListener() {
//				public void actionPerformed( ActionEvent e) {
//					outputLocationButtonPressed();
//				}
//			});
//			
//			outputLocationField.setEnabled( false);
//			outputLocationButton.setEnabled( false);
//
//			outputToFileButton	= new JRadioButton( "To File:");
//			outputToFileButton.addItemListener( new ItemListener() {
//				public void itemStateChanged( ItemEvent event) {
//					outputLocationButton.setEnabled( outputToFileButton.isSelected());
//					outputLocationField.setEnabled( outputToFileButton.isSelected());
//				}
//			});
//			
//			outputToFileButton.setPreferredSize( new Dimension( outputToFileButton.getPreferredSize().width, outputLocationField.getPreferredSize().height));
//			
//			JPanel locationPanel = new JPanel( new BorderLayout());
//			locationPanel.add( outputToFileButton, BorderLayout.WEST);
//			locationPanel.add( outputLocationField, BorderLayout.CENTER);
//			locationPanel.add( outputLocationButton, BorderLayout.EAST);
//
//			outputPanel.add( locationPanel, FormLayout.FULL_FILL);

			ButtonGroup outputGroup = new ButtonGroup();
//			outputGroup.add( outputToFileButton);
			outputGroup.add( outputToSameDocumentButton);
			outputGroup.add( outputToNewDocumentButton);

			outputToSameDocumentButton.setSelected( true);
//			outputToFileButton.setSelected( false);
		}
		
		return outputPanel;
	}

	private JPanel getTransformsPanel() {
		if ( transformsPanel == null) {
			transformsPanel = new JPanel( new FormLayout( 10, 2));
			transformsPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Transforms"),
										new EmptyBorder( 0, 5, 5, 5)));
			JTextField tempField = new JTextField();
			
			c14nMethodField  = new JComboBox();
			c14nMethodField.setFont( c14nMethodField.getFont().deriveFont( Font.PLAIN));
			
			c14nMethodField.addItem( "None");
			c14nMethodField.addItem( "Exclusive");
			c14nMethodField.addItem( "Exclusive With Comments");
			c14nMethodField.addItem( "Inclusive");
			c14nMethodField.addItem( "Inclusive With Comments");
			c14nMethodField.setSelectedIndex( 0);
			c14nMethodField.setPreferredSize( new Dimension( c14nMethodField.getPreferredSize().width, tempField.getPreferredSize().height));

			xpathField = new JComboBox();
			xpathField.setFont( xpathField.getFont().deriveFont( Font.PLAIN));
			xpathField.setPreferredSize( new Dimension( xpathField.getPreferredSize().width, tempField.getPreferredSize().height));
			xpathField.setEditable(true);
			
			transformsPanel.add( new JLabel( "C14N Method:"), FormLayout.LEFT);
			transformsPanel.add( c14nMethodField, FormLayout.RIGHT);

			transformsPanel.add( new JLabel( "XPath predicate:"), FormLayout.LEFT);
			transformsPanel.add( xpathField, FormLayout.RIGHT_FILL);
			
			transformsPanel.add( new JLabel(" "), FormLayout.FULL_FILL);
			
			transformsPanel.add( new JLabel("Prefix Namespace Mapping:"), FormLayout.FULL_FILL);
			
			// the prefix-namespace mapping panel
			mappingPanel = new PrefixNamespaceMappingPanel(parent,props,3);
			
			transformsPanel.add(mappingPanel, FormLayout.FULL_FILL);
		}

		return transformsPanel;
	}

	private JPanel getSignaturePanel() {
		if ( signaturePanel == null) {
			signaturePanel = new JPanel( new FormLayout( 10, 2));
			signaturePanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Signature Type"),
										new EmptyBorder( 0, 5, 5, 5)));
			
			idField = new JTextField();
			idField.setEnabled( false);
			idField.setPreferredSize( new Dimension( 150, idField.getPreferredSize().height));

			envelopeButton	= new JRadioButton( "Envelope");
			envelopeButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					idField.setEnabled( envelopeButton.isSelected());
					idLabel.setEnabled( envelopeButton.isSelected());
				}
			});
			
			envelopeButton.setPreferredSize( new Dimension( envelopeButton.getPreferredSize().width, idField.getPreferredSize().height));
			
			JPanel envelopePanel = new JPanel( new BorderLayout( 5, 0));
			envelopePanel.add( envelopeButton, BorderLayout.WEST);
			
//			JPanel idPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0));
			idLabel = new JLabel( "Id:");
//			idPanel.add( idLabel);
//			idPanel.add( idField);

			envelopePanel.add( idLabel, BorderLayout.WEST);
			envelopePanel.add( idField, BorderLayout.CENTER);

			detachedButton	= new JRadioButton( "Detached");
			detachedButton.setPreferredSize( new Dimension( detachedButton.getPreferredSize().width, idField.getPreferredSize().height));
			detachedButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent event) {
					if ( detachedButton.isSelected()) {
						outputToSameDocumentButton.setEnabled( false);
						outputToNewDocumentButton.setSelected( true);
					} else {
						outputToSameDocumentButton.setEnabled( true);
					}
				}
			});

			signaturePanel.add( envelopeButton, FormLayout.LEFT);
			signaturePanel.add( envelopePanel, FormLayout.RIGHT_FILL);
			signaturePanel.add( detachedButton, FormLayout.FULL);

			ButtonGroup group = new ButtonGroup();
			group.add( envelopeButton);
			group.add( detachedButton);

			envelopeButton.setSelected( true);
			detachedButton.setSelected( false);
		}

		return signaturePanel;
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
		cancelled = false;
		
		String xpathPred = (String)xpathField.getSelectedItem();
		if (xpathPred != null && !xpathPred.equals(""))
		{
			props.addXPathPredicate(xpathPred);
		}
		
		// save the prefix mappings
		mappingPanel.save();
		super.okButtonPressed();
	}
	
	public void show( ExchangerDocument document) 
	{
		JPanel form = new JPanel( new FormLayout( 10, 2));

		if ( document != null) {
			this.document = document;

			if ( document.isError()) {
				outputToSameDocumentButton.setEnabled( false);
				outputToNewDocumentButton.setSelected( true);

				inputCurrentButton.setEnabled( false);
				inputFromURLButton.setSelected( true);
			} else {
				outputToSameDocumentButton.setEnabled( true);
				outputToSameDocumentButton.setSelected( true);

				inputCurrentButton.setEnabled( true);
				inputCurrentButton.setSelected( true);
			}
			
		} else {
			outputToSameDocumentButton.setEnabled( false);
			outputToNewDocumentButton.setSelected( true);

			inputCurrentButton.setEnabled( false);
			inputFromURLButton.setSelected( true);
		}
		
		// set the xpath predicates
		predicateHistory = props.getXPathPredicates();
		
		// set the predicate combo
		setPredicateHistory();
		
		// set the prefix namespace mapping
		mappingPanel.init();
		
		pack();
		setSize( new Dimension( 400, getSize().height));

		setLocationRelativeTo( parent);

		super.show();
	}
	
	protected void cancelButtonPressed() {
		cancelled = true;
		
		// save the prefix mappings
		mappingPanel.save();
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
		} 

		File file = null;

		if ( !isEmpty( outputLocationField.getText())) {
			file = new File( outputLocationField.getText());
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

		if ( file == null && outputLocationField.isEnabled() && !isEmpty( outputLocationField.getText())) {
			file = new File( outputLocationField.getText());
		}
		
		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}

		chooser.rescanCurrentDirectory();
		
		return chooser;
	}
	
	public String getInputLocation() {
		return inputLocationField.getText();
	}

	public String getOutputLocation() {
		return outputLocationField.getText();
	}

	public boolean isOutputToFile() {
		return outputToFileButton.isSelected();
	}

	public boolean isOutputToNewDocument() {
		return outputToNewDocumentButton.isSelected();
	}

	public boolean isOutputToSameDocument() {
		return outputToSameDocumentButton.isSelected();
	}

	public String getID() {
		String id = idField.getText();
		
		if ( id != null && id.trim().length() > 0) {
			return id;
		} else {
			return null;
		}
	}

	public String getC14NMethod() {
		int index = c14nMethodField.getSelectedIndex();
		
		switch ( index) {
			case 0:
				return SignatureGenerator.NO_C14N;
			case 1:
				return SignatureGenerator.TRANSFORM_C14N_EXCL_OMIT_COMMENTS;
			case 2:
				return SignatureGenerator.TRANSFORM_C14N_EXCL_WITH_COMMENTS;
			case 3:
				return SignatureGenerator.TRANSFORM_C14N_OMIT_COMMENTS;
			case 4:
				return SignatureGenerator.TRANSFORM_C14N_WITH_COMMENTS;
			
		}

		return SignatureGenerator.NO_C14N;
	}

	public String getXPath() {
		String xpath = (String)xpathField.getSelectedItem();
		
		if ( xpath != null && xpath.trim().length() > 0) {
			return xpath;
		} else {
			return null;
		}
	}

	public boolean isEnvelope() {
		return envelopeButton.isSelected();
	}

	public boolean isInputCurrentDocument() {
		return inputCurrentButton.isSelected();
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
	
	public static void main( String args[]){
		SignDocumentDialog dialog = new SignDocumentDialog( new JFrame(),null);
		 
		dialog.show( null);
	}
	
	private void setPredicateHistory()
	{
		xpathField.removeAllItems();
		
		for (int i=0;i<predicateHistory.size();i++)
		{
			xpathField.addItem((String)predicateHistory.get(i));
		}
		
		xpathField.setSelectedIndex(-1);
	}
} 
