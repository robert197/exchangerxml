/*
 * $Id: SchemaPropertiesDialog.java,v 1.6 2005/08/26 14:08:47 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;

import org.bounce.DefaultFileFilter;
import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;

import com.cladonia.schema.SchemaDocument;
import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XMLGrammar;
import com.cladonia.xngreditor.grammar.FragmentProperties;
import com.cladonia.xngreditor.grammar.TagCompletionProperties;
import com.cladonia.xngreditor.grammar.TagCompletionPropertiesDialog;

/**
 * The Schema properties dialog.
 *
 * @version	$Revision: 1.6 $, $Date: 2005/08/26 14:08:47 $
 * @author Dogs bay
 */
public class SchemaPropertiesDialog extends XngrDialog {

	private ExchangerEditor parent	= null;
	private JCheckBox useValidationForCompletionCheck	= null;
	private JCheckBox useValidationForSchemaCheck		= null;
	
	private JTextField schemaLocationField	= null;
	private JLabel schemaLocationLabel		= null;
	private JButton schemaLocationButton	= null;
	private JFileChooser validationChooser = null;

	private JFileChooser tagCompletionChooser = null;
	private JFileChooser schemaChooser = null;
	
	private JPanel tempPanel = null;
	
	private XMLGrammarImpl grammar	= null;
	
	private JFileChooser grammarChooser = null;
	
	private DefaultFileFilter xsdFilter = null;
	private DefaultFileFilter dtdFilter = null;
	private DefaultFileFilter rngFilter = null;
	private DefaultFileFilter rncFilter = null;
	private DefaultFileFilter nrlFilter = null;
	
	private JCheckBox useForCompletionCheck	= null;
	private JCheckBox xmlValidationLocationCheck	= null;
	private JTextField validationLocationField		= null;
	private JButton validationLocationButton		= null;
	private JComboBox validationGrammarBox			= null;
	
	private JPanel validationPanel = null;
	private JPanel tagCompletionPanel = null;
	private JPanel schemaPanel = null;

	private TagCompletionPropertiesDialog tagCompletionPropertiesDialog	= null;
	private JList tagCompletionList							= null;
	private TagCompletionListModel tagCompletionListModel	= null;
	
	private JButton addTagCompletionButton					= null;
	private JButton removeTagCompletionButton				= null;
	private JButton editTagCompletionButton					= null;

	/**
	 * The SchemaProperties dialog.
	 *
	 * @param parent the parent frame.
	 */
	public SchemaPropertiesDialog( ExchangerEditor parent) 
	{
		super( parent, true);
		
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Set Document Properties");
		setDialogDescription( "Specify Validation, Tag Completion and Schema Viewer properties.");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill the panel...
		form.add(getValidationPanel(), FormLayout.FULL_FILL);
		form.add(getTagCompletionPanel(),FormLayout.FULL_FILL);
		form.add(getSchemaViewerPanel(),FormLayout.FULL_FILL);

		main.add( form, BorderLayout.CENTER);

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	// The schema validation panel
	private JPanel getValidationPanel() {
		validationPanel = new JPanel( new FormLayout( 10, 2));
		validationPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Validation"),
									new EmptyBorder( 0, 5, 5, 5)));

		// grammar
		validationGrammarBox = new JComboBox();
		
		validationGrammarBox.addItem( "Document Type Definition");
		validationGrammarBox.addItem( "XML Schema");
		validationGrammarBox.addItem( "RelaxNG");
		validationGrammarBox.addItem( "RelaxNG Compact Syntax");
		validationGrammarBox.addItem( "Namespace Routing Language");
		
		validationGrammarBox.setSelectedIndex( 0);
		
		JLabel grammarLabel = new JLabel( "Grammar:");
		validationPanel.add( grammarLabel, FormLayout.LEFT);
		validationPanel.add( validationGrammarBox, FormLayout.RIGHT_FILL);
		
		// location
		validationLocationField = new JTextField();

		validationLocationButton = new JButton( "...");
		validationLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		validationLocationButton.setPreferredSize( new Dimension( validationLocationButton.getPreferredSize().width, validationLocationField.getPreferredSize().height));
		validationLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				validationLocationButtonPressed();
			}
		});
		
		validationGrammarBox.setPreferredSize( new Dimension( validationGrammarBox.getPreferredSize().width, validationLocationField.getPreferredSize().height));
		validationGrammarBox.setFont( validationGrammarBox.getFont().deriveFont( Font.PLAIN));
		validationGrammarBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				
				if ( tagCompletionListModel != null && tagCompletionListModel.getSize() == 0 &&
					schemaLocationField.getText().equals(""))
				{
					int index = validationGrammarBox.getSelectedIndex();

					if ( index == 1) {
						useValidationForSchemaCheck.setEnabled( true);
						useValidationForSchemaCheck.setSelected( true);
					} else {
						useValidationForSchemaCheck.setSelected( false);
						useValidationForSchemaCheck.setEnabled( false);
					}
					if ( index <= 3 && index >= 0) {
						useValidationForCompletionCheck.setEnabled( true);
						useValidationForCompletionCheck.setSelected( true);
					} else {
						useValidationForCompletionCheck.setSelected( false);
						useValidationForCompletionCheck.setEnabled( false);
					}
				}
				else
				{
					int index = validationGrammarBox.getSelectedIndex();

					if ( index == 1) {
						useValidationForSchemaCheck.setEnabled( true);
					} else {
						useValidationForSchemaCheck.setSelected( false);
						useValidationForSchemaCheck.setEnabled( false);
					}
					if ( index <= 3 && index >= 0) {
						useValidationForCompletionCheck.setEnabled( true);
					} else {
						useValidationForCompletionCheck.setSelected( false);
						useValidationForCompletionCheck.setEnabled( false);
					}
				}	
			}
		});

		JPanel locPanel = new JPanel( new BorderLayout());
		locPanel.add( validationLocationField, BorderLayout.CENTER);
		locPanel.add( validationLocationButton, BorderLayout.EAST);

		JLabel validationLocationLabel = new JLabel( "Location:");
		validationPanel.add( validationLocationLabel, FormLayout.LEFT);

		validationPanel.add( locPanel, FormLayout.RIGHT_FILL);

		xmlValidationLocationCheck = createPlainCheckBox( "Location defined in Document");
		xmlValidationLocationCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				validationGrammarBox.setEnabled( !xmlValidationLocationCheck.isSelected());
				validationLocationField.setEnabled( !xmlValidationLocationCheck.isSelected());
				validationLocationButton.setEnabled( !xmlValidationLocationCheck.isSelected());
				
				useValidationForSchemaCheck.setSelected( false);
				useValidationForSchemaCheck.setEnabled( !xmlValidationLocationCheck.isSelected());
				
				useValidationForCompletionCheck.setSelected( false);
				useValidationForCompletionCheck.setEnabled( !xmlValidationLocationCheck.isSelected());
				
				if (!xmlValidationLocationCheck.isSelected())
				{
					if ( tagCompletionListModel != null && tagCompletionListModel.getSize() == 0 &&
						schemaLocationField.getText().equals(""))	
					{
						int index = validationGrammarBox.getSelectedIndex();

						if ( index == 1) {
							useValidationForSchemaCheck.setEnabled(true);
							useValidationForSchemaCheck.setSelected(true);
						} else {
							useValidationForSchemaCheck.setSelected(false);
							useValidationForSchemaCheck.setEnabled(false);
						}
						if ( index <= 3 && index >= 0) {
							useValidationForCompletionCheck.setEnabled(true);
							useValidationForCompletionCheck.setSelected(true);
						} else {
							useValidationForCompletionCheck.setSelected(false);
							useValidationForCompletionCheck.setEnabled(false);
						}
						
					}
					else
					{
						int index = validationGrammarBox.getSelectedIndex();

						if ( index == 1) {
							useValidationForSchemaCheck.setEnabled( true);
						} else {
							useValidationForSchemaCheck.setSelected( false);
							useValidationForSchemaCheck.setEnabled( false);
						}
						if ( index <= 3 && index >= 0) {
							useValidationForCompletionCheck.setEnabled( true);
						} else {
							useValidationForCompletionCheck.setSelected( false);
							useValidationForCompletionCheck.setEnabled( false);
						}
					}
				}
			}
		});

		validationPanel.add( xmlValidationLocationCheck, FormLayout.RIGHT_FILL);
		validationPanel.add( getSeparator(), FormLayout.FULL_FILL);

		useValidationForCompletionCheck = new JCheckBox( "Use for Tag Completion");
		useValidationForCompletionCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				tagCompletionList.setEnabled( !useValidationForCompletionCheck.isSelected());
				addTagCompletionButton.setEnabled( !useValidationForCompletionCheck.isSelected());
				removeTagCompletionButton.setEnabled( !useValidationForCompletionCheck.isSelected());
				editTagCompletionButton.setEnabled( !useValidationForCompletionCheck.isSelected());
			}
		});
		
		JPanel useValidationForCompletionPanel = new JPanel( new BorderLayout());
		useValidationForCompletionPanel.add( useValidationForCompletionCheck, BorderLayout.WEST);

		validationPanel.add( useValidationForCompletionPanel, FormLayout.FULL_FILL);

		useValidationForSchemaCheck = new JCheckBox( "Use for Schema Viewer/Outliner/Grid");
		useValidationForSchemaCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				schemaLocationField.setEnabled( !useValidationForSchemaCheck.isSelected());
				schemaLocationLabel.setEnabled( !useValidationForSchemaCheck.isSelected());
				schemaLocationButton.setEnabled( !useValidationForSchemaCheck.isSelected());
			}
		});
		
		JPanel useValidationForSchemaPanel = new JPanel( new BorderLayout());
		useValidationForSchemaPanel.add( useValidationForSchemaCheck, BorderLayout.WEST);

		validationPanel.add( useValidationForSchemaPanel, FormLayout.FULL_FILL);
		
		return validationPanel;
	}
	
	//	 The Tag Completion Schema panel
	private JPanel getTagCompletionPanel() {
		// >>> Tag Completion
		JPanel tagCompletionPanel = new JPanel( new FormLayout( 10, 2));
		tagCompletionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Tag Completion"),
									new EmptyBorder( 0, 5, 5, 5)));

		tagCompletionList = new JList();
		tagCompletionList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				editTagCompletionButtonPressed();
			}
		});
		
		tagCompletionList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		tagCompletionList.setVisibleRowCount( 3);
		tagCompletionList.setCellRenderer( new TagCompletionListCellRenderer());

		addTagCompletionButton = new JButton( "Add");
//		addScenarioButton.setMargin( new Insets( 0, 10, 0, 10));
		addTagCompletionButton.setFont( addTagCompletionButton.getFont().deriveFont( Font.PLAIN));
//		addScenarioButton.setPreferredSize( new Dimension( addScenarioButton.getPreferredSize().width, 20));
		addTagCompletionButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addTagCompletionButtonPressed();
			}
		});
		
		removeTagCompletionButton = new JButton( "Remove");
		removeTagCompletionButton.setFont( removeTagCompletionButton.getFont().deriveFont( Font.PLAIN));
//		removeScenarioButton.setMargin( new Insets( 0, 10, 0, 10));
//		removeScenarioButton.setPreferredSize( new Dimension( removeScenarioButton.getPreferredSize().width, 20));
		removeTagCompletionButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				removeTagCompletionButtonPressed();
			}
		});

		editTagCompletionButton = new JButton( "Edit");
		editTagCompletionButton.setFont( editTagCompletionButton.getFont().deriveFont( Font.PLAIN));
//		setDefaultScenarioButton.setMargin( new Insets( 0, 10, 0, 10));
//		setDefaultScenarioButton.setPreferredSize( new Dimension( setDefaultScenarioButton.getPreferredSize().width, 20));
		editTagCompletionButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				editTagCompletionButtonPressed();
			}
		});

		JScrollPane scroller = new JScrollPane(	tagCompletionList,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JPanel tagCompletionListPanel = new JPanel( new BorderLayout());
		tagCompletionListPanel.add( scroller, BorderLayout.CENTER);

		JPanel tagCompletionButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		tagCompletionButtonPanel.setBorder( new EmptyBorder( 10, 0, 5, 0));
		
		tagCompletionButtonPanel.add( editTagCompletionButton);
		tagCompletionButtonPanel.add( addTagCompletionButton);
		tagCompletionButtonPanel.add( removeTagCompletionButton);
		tagCompletionListPanel.add( tagCompletionButtonPanel, BorderLayout.SOUTH);

		tagCompletionPanel.add( tagCompletionListPanel, FormLayout.FULL_FILL);

		return tagCompletionPanel;
		// <<< Tag Completion
	}
	
	//	 The Schema Viewer/outlioner panel
	private JPanel getSchemaViewerPanel()
	{

		// >>> SchemaViewer/Outliner
		schemaPanel = new JPanel( new FormLayout( 10, 2));
		schemaPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Schema Viewer/Outliner"),
									new EmptyBorder( 0, 5, 5, 5)));

		// location
		schemaLocationField = new JTextField();

		schemaLocationButton = new JButton( "...");
		schemaLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		schemaLocationButton.setPreferredSize( new Dimension( schemaLocationButton.getPreferredSize().width, schemaLocationField.getPreferredSize().height));
		schemaLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				schemaLocationButtonPressed();
			}
		});
		
		tempPanel = new JPanel( new BorderLayout());
		tempPanel.add( schemaLocationField, BorderLayout.CENTER);
		tempPanel.add( schemaLocationButton, BorderLayout.EAST);

		schemaLocationLabel = new JLabel( "Schema Location:");
		schemaPanel.add( schemaLocationLabel, FormLayout.LEFT);

		schemaPanel.add( tempPanel, FormLayout.RIGHT_FILL);
		// <<< SchemaViewer/Outliner
		
		return schemaPanel;
	}
	
	protected void okButtonPressed() {
		super.okButtonPressed();
	}
	
	private TagCompletionPropertiesDialog getTagCompletionPropertiesDialog() {
		if ( tagCompletionPropertiesDialog == null) {
			tagCompletionPropertiesDialog = new TagCompletionPropertiesDialog( parent);
		} 
		
		return tagCompletionPropertiesDialog;
	}

	private void addTagCompletionButtonPressed() {
		TagCompletionPropertiesDialog dialog = getTagCompletionPropertiesDialog();
		TagCompletionProperties properties = new TagCompletionProperties();
		
		dialog.show( properties);

		if ( !dialog.isCancelled()) {
			tagCompletionListModel.addTagCompletion( properties);
		}
	}

	private void removeTagCompletionButtonPressed() {
		int index = tagCompletionList.getSelectedIndex();

		if ( index != -1) {
			TagCompletionProperties properties = tagCompletionListModel.getTagCompletion( index);
			tagCompletionListModel.removeTagCompletion( properties);
		}
	}

	private void editTagCompletionButtonPressed() {
		int index = tagCompletionList.getSelectedIndex();

		if ( index != -1) {
			TagCompletionProperties properties = tagCompletionListModel.getTagCompletion( index);
			TagCompletionPropertiesDialog dialog = getTagCompletionPropertiesDialog();

			dialog.show( properties);

			if ( !dialog.isCancelled()) {
				tagCompletionListModel.updateTagCompletion( properties);
			}
		}
	}

	private void schemaLocationButtonPressed() {
		JFileChooser chooser = getSchemaFileChooser();
		
		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( schemaLocationField, url.toString());
		}
	}	
	
	private JFileChooser getSchemaFileChooser() {
		if ( schemaChooser == null) {
			schemaChooser = FileUtilities.createFileChooser();
			
			if ( xsdFilter == null) {
				xsdFilter = new DefaultFileFilter( "xsd", "XML Schema Document");
			}

			schemaChooser.addChoosableFileFilter( xsdFilter);
		} 
		
		schemaChooser.setFileFilter( xsdFilter);

		File file = URLUtilities.toFile( schemaLocationField.getText());
		
		File tagCompletionFile = null;
		File validationFile = null;
		
		if ( validationLocationField.isEnabled()) {
			validationFile = URLUtilities.toFile( validationLocationField.getText());
		}

		if ( file != null) {
			schemaChooser.setCurrentDirectory( file);
		} else if ( tagCompletionFile != null) {
			schemaChooser.setCurrentDirectory( tagCompletionFile);
		} else if ( validationFile != null) {
			schemaChooser.setCurrentDirectory( validationFile);
		} else {
			schemaChooser.setCurrentDirectory( getSelectedChooserFile());
		}
		
		schemaChooser.rescanCurrentDirectory();
		
		return schemaChooser;
	}
	
	private void validationLocationButtonPressed() {
		JFileChooser chooser = getGrammarFileChooser();
		
		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setText( validationLocationField, url.toString());
			
			FileFilter filter = chooser.getFileFilter();
			
			if ( filter instanceof DefaultFileFilter) {
				if ( filter == xsdFilter) {
					validationGrammarBox.setSelectedIndex( 1);
				} else if ( filter == dtdFilter) {
					validationGrammarBox.setSelectedIndex( 0);
				} else if ( filter == rngFilter) {
					validationGrammarBox.setSelectedIndex( 2);
				} else if ( filter == rncFilter) {
					validationGrammarBox.setSelectedIndex( 3);
				} else if ( filter == nrlFilter) {
					validationGrammarBox.setSelectedIndex( 4);
				}
			}
			
		}
	}	
	
	/**
	 * Displays the dialog
	 *
	 * @param grammar The previously set validation grammar 
	 * @param tagCompletionSchema The previously set tag completion schema
	 * @param viewerSchema The previously set viewer/outliner schema
	 * 
	 */
	public void show( XMLGrammarImpl grammar, Vector tagCompletionSchemas, XMLSchema viewerSchema) {
		this.grammar = grammar;
		
		String location = grammar.getLocation();
		
		// set the validation grammar and text
		setValidationGrammar( grammar.getType());
		setText( validationLocationField, location);
		
		// set the "location defined in document" checkbox
		if ( location != null && location.length() > 0) {
			xmlValidationLocationCheck.setSelected( !grammar.useExternal());
		} else {
			xmlValidationLocationCheck.setSelected( true);
		}
		
		tagCompletionListModel = new TagCompletionListModel( new Vector());
		tagCompletionList.setModel( tagCompletionListModel);

		// set the tag completion schema 
		if ( tagCompletionSchemas != null) {
			for ( int i = 0; i < tagCompletionSchemas.size(); i++) {
				SchemaDocument schema = (SchemaDocument)tagCompletionSchemas.elementAt( i);
				tagCompletionListModel.addTagCompletion( new TagCompletionProperties( schema.getURL().toString(), schema.getType()));
			}
		}
		
		// set the schema viewer/outliner
		if ( viewerSchema != null) {
			schemaLocationField.setText( viewerSchema.getURL().toString());
			schemaLocationField.setCaretPosition( 0);
		} else {
			schemaLocationField.setText( "");
		}
		
		
		pack();
		setSize( new Dimension( 400, getSize().height));
		setLocationRelativeTo( parent);
		
		super.show();
	}
	
	
	/**
	 * Gets the validation schema 
	 *
	 * @return The validation schema location as a String
	 */
	public String getValidationSchema()
	{
		URL url = getValidationURL();
		if (url != null)
			return url.toString();
		else
			return null;
	}
	
	/**
	 * Gets the Viewer schema 
	 *
	 * @return The viewer schema location as a String
	 */
	public String getViewerSchema()
	{
		URL url = getViewerURL();
		if (url != null)
			return url.toString();
		else
			return null;
	}
	
	/**
	 * Gets the validation schema 
	 *
	 * @return The validation schema location as a URL
	 */
	public URL getValidationURL() {
		URL result = null;
		
		if ( !StringUtilities.isEmpty( validationLocationField.getText())) {
			result = URLUtilities.toURL( validationLocationField.getText());
		}

		return result;
	}
	
	/**
	 * Gets the tag completion schema 
	 *
	 * @return The tag completion schema location as a URL
	 */
	public Vector getTagCompletionList() {
		return tagCompletionListModel.getTagCompletionList();
	}
	
	/**
	 * Gets the Viewer schema 
	 *
	 * @return The viewer schema location as a URL
	 */
	public URL getViewerURL() {
		URL result = null;
		
		if ( !StringUtilities.isEmpty( schemaLocationField.getText())) {
			result = URLUtilities.toURL( schemaLocationField.getText());
		}

		return result;
	}
	
	
	/**
	 * Gets the validation grammar type 
	 *
	 * @return The validation grammar type 
	 */
	public int getValidationType() {
		return getValidationGrammar();
	}
	
	private int getValidationGrammar() {
		int result = XMLGrammar.TYPE_DTD;
		int index = validationGrammarBox.getSelectedIndex();

		if ( index == 1) {
			result = XMLGrammar.TYPE_XSD;
		} else if ( index == 2) {
			result = XMLGrammar.TYPE_RNG;
		} else if ( index == 3) {
			result = XMLGrammar.TYPE_RNC;
		} else if ( index == 4) {
			result = XMLGrammar.TYPE_NRL;
		}
		
		return result;
	}
	
	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	private void setValidationGrammar( int type) {
		if ( type == XMLGrammar.TYPE_XSD) {
			validationGrammarBox.setSelectedIndex( 1);
		} else if ( type == XMLGrammar.TYPE_RNG) {
			validationGrammarBox.setSelectedIndex( 2);
			
			useValidationForSchemaCheck.setSelected( false);
			useValidationForSchemaCheck.setEnabled( false);
		} else if ( type == XMLGrammar.TYPE_RNC) {
			validationGrammarBox.setSelectedIndex( 3);
			
			useValidationForSchemaCheck.setSelected( false);
			useValidationForSchemaCheck.setEnabled( false);
		} else if ( type == XMLGrammar.TYPE_NRL) {
			validationGrammarBox.setSelectedIndex( 4);
			useValidationForSchemaCheck.setSelected( false);
			useValidationForSchemaCheck.setEnabled( false);
			useValidationForCompletionCheck.setSelected(false);
			useValidationForCompletionCheck.setEnabled( false);
			
		} else {
			validationGrammarBox.setSelectedIndex( 0);
			useValidationForSchemaCheck.setSelected( false);
			useValidationForSchemaCheck.setEnabled( false);
		}
	}
	
	/**
	 * Whether or not to use the location defined in the document for validation 
	 *
	 * @return boolean
	 */
	public boolean useLocationInDocument() {
		if ( xmlValidationLocationCheck.isEnabled()) {
			return xmlValidationLocationCheck.isSelected();
		}
		
		return false;
	}
	
	/**
	 * Whether or not to use the same schema as validation for tag completion 
	 *
	 * @return boolean
	 */
	public boolean useValidationForTagCompletion() {
		if ( useValidationForCompletionCheck.isEnabled()) {
			return useValidationForCompletionCheck.isSelected();
		}
		
		return false;
	}
	
	/**
	 * Whether or not to use the same schema as validation for viewer/outliner 
	 *
	 * @return boolean
	 */
	public boolean useValidationForViewerSchema() {
		if ( useValidationForSchemaCheck.isEnabled()) {
			return useValidationForSchemaCheck.isSelected();
		}
		
		return false;
	}
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

	private JLabel createPlainLabel( String text) {
		JLabel label = new JLabel( text);
		label.setFont( label.getFont().deriveFont( Font.PLAIN));

		return label;
	}

	private JCheckBox createPlainCheckBox( String text) {
		JCheckBox check = new JCheckBox( text);
		check.setFont( check.getFont().deriveFont( Font.PLAIN));

		return check;
	}

	private File getSelectedChooserFile() {
		ExchangerDocument doc = parent.getDocument();
		URL url = null;
		
		if ( doc != null) {
			url = doc.getURL();
		}

		File file = URLUtilities.toFile( validationLocationField.getText());

		if ( file != null) {
			return file;
		} else if ( url != null && url.getProtocol().equals( "file")) {
			return new File( url.getFile());
		} 
	
		return FileUtilities.getLastOpenedFile();
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getGrammarFileChooser() {
		if ( grammarChooser == null) {
			grammarChooser = FileUtilities.createFileChooser();
			xsdFilter = new DefaultFileFilter( "xsd", "XML Schema Document");
			dtdFilter = new DefaultFileFilter( "dtd", "Document Type Definition");
			rngFilter = new DefaultFileFilter( "rng", "RelaxNG");
			rncFilter = new DefaultFileFilter( "rnc", "RelaxNG Compact Format");
			nrlFilter = new DefaultFileFilter( "nrl", "Namespace Routing Language");

			grammarChooser.addChoosableFileFilter( xsdFilter);
			grammarChooser.addChoosableFileFilter( dtdFilter);
			grammarChooser.addChoosableFileFilter( rngFilter);
			grammarChooser.addChoosableFileFilter( rncFilter);
			grammarChooser.addChoosableFileFilter( nrlFilter);
		} 

		int index = validationGrammarBox.getSelectedIndex();
		
		if ( index == 1) {
			grammarChooser.setFileFilter( xsdFilter);
		} else if ( index == 0) {
			grammarChooser.setFileFilter( dtdFilter);
		} else if ( index == 2) {
			grammarChooser.setFileFilter( rngFilter);
		} else if ( index == 3) {
			grammarChooser.setFileFilter( rncFilter);
		} else if ( index == 4) {
			grammarChooser.setFileFilter( nrlFilter);
		}
		
		grammarChooser.setCurrentDirectory( getSelectedChooserFile());
		grammarChooser.rescanCurrentDirectory();
		
		return grammarChooser;
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
	
	class TagCompletionListModel extends AbstractListModel {
		Vector elements = null;
		FragmentProperties defaultScenario = null; 
		
		public TagCompletionListModel( Vector list) {
			elements = new Vector( list);
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public void addTagCompletion( TagCompletionProperties props) {
			elements.addElement( props);

			fireIntervalAdded( this, elements.size()-1, elements.size()-1);
		}

		public void updateTagCompletion( TagCompletionProperties props) {
			int index = elements.indexOf( props);
	
			fireContentsChanged( this, index, index);
		}

		public void removeTagCompletion( TagCompletionProperties props) {
			int index = elements.indexOf( props);
			elements.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}
		
		public Vector getTagCompletionList() {
			return elements; 
		}

		public Object getElementAt( int i) {
			return (TagCompletionProperties)elements.elementAt( i);
		}

		public TagCompletionProperties getTagCompletion( int i) {
			return (TagCompletionProperties)elements.elementAt( i);
		}
	}

	class TagCompletionListCellRenderer extends JLabel implements ListCellRenderer {
//		private JLabel type 		= null;
//		private JLabel location		= null;
		
		public TagCompletionListCellRenderer() {
//			super( new BorderLayout());
//			
//			setBorder( new EmptyBorder( 1, 2, 1, 5));
//			setOpaque( true);
//			
//			type = new JLabel();
//			type.setBorder( new EmptyBorder( 0, 0, 0, 2));
			setOpaque( true);
//			
//			location = new JLabel();
//			location.setOpaque( false);
//			
//			this.add( type, BorderLayout.WEST);
//			this.add( location, BorderLayout.CENTER);
		}
		
		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected, boolean cellHasFocus) {	
			boolean clash = false;
			
			if ( value instanceof TagCompletionProperties) {
				TagCompletionProperties t = (TagCompletionProperties)value;
				setText( URLUtilities.getFileName( t.getLocation()));
				
				String grammar = "XSD";
				
				switch ( t.getType()) {
					case XMLGrammar.TYPE_DTD:
						grammar = "DTD";
						break;
					case XMLGrammar.TYPE_RNC:
						grammar = "RNC";
						break;
					case XMLGrammar.TYPE_RNG:
						grammar = "RNG";
						break;
				default:
						grammar = "XSD";
						break;
				}
				
				setIcon( IconFactory.getIconForExtension( grammar));
				setToolTipText( t.getLocation());
//				type.setText( "["+grammar+"]");
			}
			
			if (isSelected && list.isEnabled()) {
				setBackground(list.getSelectionBackground());
				setForeground( list.getSelectionForeground());
//				type.setForeground( list.getSelectionForeground());
			} else {
				setBackground( list.getBackground());
				setForeground( list.getForeground());
			}

			setEnabled(list.isEnabled());
			
			setFont( list.getFont().deriveFont( Font.PLAIN));

			return this;
		}
	}
} 
