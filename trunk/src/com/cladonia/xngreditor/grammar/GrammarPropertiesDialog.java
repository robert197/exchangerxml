/*
 * $Id: GrammarPropertiesDialog.java,v 1.26 2004/11/04 19:21:49 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import org.bounce.DefaultFileFilter;
import org.bounce.FormConstraints;
import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XDocumentFactory;
import com.cladonia.xml.XMLGrammar;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.XngrDialogHeader;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.KeyMap;
import com.cladonia.xngreditor.properties.Keystroke;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.scenario.ScenarioSelectionDialog;

/**
 * The grammar properties dialog.
 *
 * @version	$Revision: 1.26 $, $Date: 2004/11/04 19:21:49 $
 * @author Dogsbay
 */
public class GrammarPropertiesDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 450, 600);
	private ConfigurationProperties configurationProperties = null;
	private GrammarProperties properties					= null;
	private Vector names									= null;
	
	private static final FormConstraints LEFT_ALIGN_RIGHT = new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT);

	private boolean allowOverride = false;
	
	private ExchangerEditor parent = null;
	
	private JFileChooser validationChooser = null;
	private JFileChooser schemaChooser = null;

	private JFileChooser iconChooser = null;

	private DefaultFileFilter iconFilter = null;

	private DefaultFileFilter xsdFilter = null;
	private DefaultFileFilter dtdFilter = null;
	private DefaultFileFilter rngFilter = null;
	private DefaultFileFilter rncFilter = null;
	private DefaultFileFilter nrlFilter = null;

	private JTabbedPane tabs 		= null;

	// The components that contain the values
	private JTextField descriptionField	= null;
	private JTextField extensionsField	= null;

	private JLabel iconLabel	= null;
	private JButton iconButton	= null;
	private String iconLocation	= null;

	// Type definition
	private JTextField rootElementField	= null;
	private JTextField namespaceField	= null;
	private JTextField prefixField		= null;
	private JTextField publicIdField	= null;
	private JTextField systemIdField	= null;
	private JButton namespaceButton		= null;

	private JTable namespaceTable			= null;
	private NamespaceTableModel namespaceTableModel	= null;
	private JButton addNamespaceButton		= null;
	private JButton deleteNamespaceButton	= null;
	private JButton changeNamespaceButton	= null;

	private NamespacePropertiesDialog namespacePropertiesDialog	= null;
	private XngrDialogHeader namespaceDialog			= null;

	private Vector updatedNamespaces = null;

	// VALIDATION
	private JCheckBox xmlValidationLocationCheck	= null;
	private JTextField validationLocationField		= null;
	private JButton validationLocationButton		= null;

	private JComboBox validationGrammarBox			= null;

	private JCheckBox useValidationForCompletionCheck	= null;
	private JCheckBox useValidationForSchemaCheck		= null;

//	private JRadioButton xsdCheck					= null;
//	private JRadioButton dtdCheck					= null;
//	private JRadioButton rngCheck					= null;
//	private JRadioButton rncCheck					= null;
//	private JRadioButton nrlCheck					= null;

	// TAG COMPLETION
	private TagCompletionPropertiesDialog tagCompletionPropertiesDialog	= null;

	private JList tagCompletionList							= null;
	private TagCompletionListModel tagCompletionListModel	= null;
	
	private JButton addTagCompletionButton					= null;
	private JButton removeTagCompletionButton				= null;
	private JButton editTagCompletionButton					= null;

	// SCHEMA VIEWER / OUTLINER
	private JLabel schemaLocationLabel		= null;
	private JTextField schemaLocationField	= null;
	private JButton schemaLocationButton	= null;

	// BASE DOCUMENT
//	private JTextField baseLocationField	= null;
//	private JButton baseLocationButton		= null;
	
	// FRAGMENTS
	private FragmentPropertiesDialog fragmentPropertiesDialog	= null;

	private JList fragmentList					= null;
	private FragmentListModel fragmentModel		= null;
	
	private JButton addFragmentButton			= null;
	private JButton removeFragmentButton		= null;
	private JButton editFragmentButton			= null;
	private JButton upFragmentButton			= null;
	private JButton downFragmentButton			= null;

	// SCENARIOS
	private JList scenarioList					= null;
	private ScenarioListModel scenarioModel		= null;

	private JButton addScenarioButton			= null;
	private JButton removeScenarioButton		= null;
	private JButton setDefaultScenarioButton	= null;

	private ScenarioSelectionDialog scenarioSelectionDialog	= null;

	// SCENARIOS
	private JList xpathList					= null;
	private XPathListModel xpathModel	= null;

	private JButton addXPathButton			= null;
	private JButton removeXPathButton		= null;
	private JButton editXPathButton		= null;
	private JButton setDefaultXPathButton	= null;

	private NamedXPathPropertiesDialog namedXPathPropertiesDialog	= null;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public GrammarPropertiesDialog( ExchangerEditor parent, ConfigurationProperties props, String confirmText, boolean allowOverride) {
		this( parent, props, confirmText);

		this.allowOverride = allowOverride;
	}

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public GrammarPropertiesDialog( ExchangerEditor parent, ConfigurationProperties props, String confirmText) {
		super( parent, true);
		
		this.parent = parent;
		this.configurationProperties = props;
		
		setResizable( false);
		setTitle( "XML Type Properties");
		setDialogDescription( "Specify XML Type information");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// >>> GENERAL
		tabs = new JTabbedPane();

		JPanel generalPanel = new JPanel( new BorderLayout());
		generalPanel.setBorder( new EmptyBorder( 0, 0, 10, 0));
		generalPanel.add( tabs, BorderLayout.CENTER);
		
		// Definitions tab
	
		JPanel definitionTab = new JPanel( new FormLayout( 10, 2));
		definitionTab.setBorder( new EmptyBorder( 5, 5, 5, 5));

		// >>> Type Description
		JPanel descriptionPanel = new JPanel( new FormLayout( 10, 2));
		descriptionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Description"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		// icon		
		iconLabel = new JLabel();

		iconButton = new JButton( "Change Icon ...");
		iconButton.setFont( iconButton.getFont().deriveFont( Font.PLAIN));
		iconButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				iconButtonPressed();
			}
		});
		
		iconLabel.setPreferredSize( new Dimension( iconButton.getPreferredSize().height, iconButton.getPreferredSize().height));
		iconLabel.setBorder( new LineBorder( UIManager.getColor( "controlShadow"), 1));
		iconLabel.setHorizontalAlignment( JLabel.CENTER);
		iconLabel.setVerticalAlignment( JLabel.CENTER);

		JPanel iconPanel = new JPanel( new BorderLayout());
//		JPanel iconPanel = new JPanel( new FlowLayout());
//		iconPanel.add( iconLabel);
//		iconPanel.add( iconButton);
		iconPanel.add( iconLabel, BorderLayout.WEST);
		iconPanel.add( iconButton, BorderLayout.EAST);

		JLabel iconText = new JLabel( "Icon:");
		descriptionPanel.add( iconText, FormLayout.LEFT);
		descriptionPanel.add( iconPanel, FormLayout.RIGHT_FILL);

		descriptionPanel.add( getSeparator(), FormLayout.FULL_FILL);

		// description
		descriptionField = new JTextField();

		JLabel descriptionLabel = new JLabel( "Name:");
		descriptionPanel.add( descriptionLabel, FormLayout.LEFT);
		descriptionPanel.add( descriptionField, FormLayout.RIGHT_FILL);

		// extensions
		extensionsField = new JTextField();

		JLabel extensionsLabel = new JLabel( "Extensions:");
		descriptionPanel.add( extensionsLabel, FormLayout.LEFT);
		descriptionPanel.add( extensionsField, FormLayout.RIGHT_FILL);

		definitionTab.add( descriptionPanel, FormLayout.FULL_FILL);

		// >>> Type Definition
		JPanel definitionPanel = new JPanel( new FormLayout( 10, 2));
		definitionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Type Definition"),
									new EmptyBorder( 0, 5, 5, 5)));

		// publicId
		publicIdField = new JTextField();

		JLabel publicIdLabel = new JLabel( "Public ID:");
		definitionPanel.add( publicIdLabel, FormLayout.LEFT);
		definitionPanel.add( publicIdField, FormLayout.RIGHT_FILL);

		// systemId
		systemIdField = new JTextField();

		JLabel systemIdLabel = new JLabel( "System ID:");
		definitionPanel.add( systemIdLabel, FormLayout.LEFT);
		definitionPanel.add( systemIdField, FormLayout.RIGHT_FILL);

		// root-element
		rootElementField = new JTextField();

		JLabel rootElementLabel = new JLabel( "Root Element:");
		definitionPanel.add( rootElementLabel, FormLayout.LEFT);
		definitionPanel.add( rootElementField, FormLayout.RIGHT_FILL);

		definitionPanel.add( getSeparator(), FormLayout.FULL_FILL);

		// prefix
		prefixField = new JTextField();

		JLabel prefixLabel = new JLabel( "Namespace Prefix:");
		definitionPanel.add( prefixLabel, FormLayout.LEFT);
		definitionPanel.add( prefixField, FormLayout.RIGHT_FILL);

		// namespace
		namespaceField = new JTextField();

		JLabel namespaceLabel = new JLabel( "Namespace URI:");
		definitionPanel.add( namespaceLabel, FormLayout.LEFT);
		definitionPanel.add( namespaceField, FormLayout.RIGHT_FILL);

		definitionPanel.add( getSeparator(), FormLayout.FULL_FILL);

		namespaceButton = new JButton( "Additional Namespaces ...");
		namespaceButton.setFont( namespaceButton.getFont().deriveFont( Font.PLAIN));
		namespaceButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				JDialog dialog = getNamespaceDialog();
				dialog.setLocationRelativeTo( GrammarPropertiesDialog.this);
				dialog.setVisible(true);
			}
		});

		JPanel namespaceButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		namespaceButtonPanel.add( namespaceButton);
		
		getNamespaceDialog();

		definitionPanel.add( namespaceButtonPanel, FormLayout.FULL_FILL);
		
		definitionTab.add( definitionPanel, FormLayout.FULL_FILL);

		// <<< Type Definition
		tabs.add( "Definition", definitionTab);

		// Definitions tab
	
		JPanel grammarsTab = new JPanel( new FormLayout( 10, 2));
		grammarsTab.setBorder( new EmptyBorder( 5, 5, 5, 5));

		// >>> Validation
		JPanel validationPanel = new JPanel( new FormLayout( 10, 2));
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
//		validationGrammarBox.addItem( "Schematron");
		
		validationGrammarBox.setSelectedIndex( 0);
		
//		JPanel grammarPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0));
//		grammarPanel.add( xsdCheck);
//		grammarPanel.add( dtdCheck);
//		grammarPanel.add( rngCheck);
//		grammarPanel.add( rncCheck);
//		grammarPanel.add( nrlCheck);

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
				
					int index = validationGrammarBox.getSelectedIndex();
	
					if ( index == 1) {
						useValidationForSchemaCheck.setEnabled( true);
						useValidationForSchemaCheck.setSelected(true);
					} else {
						useValidationForSchemaCheck.setSelected( false);
						useValidationForSchemaCheck.setEnabled( false);
					}                                                      
					if ( index <= 3 && index >= 0) {
						useValidationForCompletionCheck.setEnabled( true);
						useValidationForCompletionCheck.setSelected(true);
					} else {
						useValidationForCompletionCheck.setSelected( false);
						useValidationForCompletionCheck.setEnabled( false);
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
				editTagCompletionButton.setEnabled( !useValidationForCompletionCheck.isSelected());
				addTagCompletionButton.setEnabled( !useValidationForCompletionCheck.isSelected());
				removeTagCompletionButton.setEnabled( !useValidationForCompletionCheck.isSelected());
			}
		});
		
		JPanel useValidationForCompletionPanel = new JPanel( new BorderLayout());
		useValidationForCompletionPanel.add( useValidationForCompletionCheck, BorderLayout.WEST);

		validationPanel.add( useValidationForCompletionPanel, FormLayout.FULL_FILL);

		useValidationForSchemaCheck = new JCheckBox( "Use for Outliner/Schema Viewer");
		useValidationForSchemaCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
//				if ( useValidationForSchemaCheck.isSelected()) {
//					useCompletionForSchemaCheck.setSelected( false);
//				}
//
//				if ( !useValidationForCompletionCheck.isSelected() && xsdTagCompletionCheck.isSelected()) {
//					useCompletionForSchemaCheck.setEnabled( !useValidationForSchemaCheck.isSelected());
//				}

				schemaLocationField.setEnabled( !useValidationForSchemaCheck.isSelected());
				schemaLocationLabel.setEnabled( !useValidationForSchemaCheck.isSelected());
				schemaLocationButton.setEnabled( !useValidationForSchemaCheck.isSelected());
			}
		});
		
		JPanel useValidationForSchemaPanel = new JPanel( new BorderLayout());
		useValidationForSchemaPanel.add( useValidationForSchemaCheck, BorderLayout.WEST);

		validationPanel.add( useValidationForSchemaPanel, FormLayout.FULL_FILL);

		grammarsTab.add( validationPanel, FormLayout.FULL_FILL);
		// <<< Validation

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
		
		tagCompletionList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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

		grammarsTab.add( tagCompletionPanel, FormLayout.FULL_FILL);
		// <<< Tag Completion

		// >>> SchemaViewer/Outliner
		JPanel schemaPanel = new JPanel( new FormLayout( 10, 2));
		schemaPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Outliner/Schema Viewer"),
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
		
		JPanel tempPanel = new JPanel( new BorderLayout());
		tempPanel.add( schemaLocationField, BorderLayout.CENTER);
		tempPanel.add( schemaLocationButton, BorderLayout.EAST);

		schemaLocationLabel = new JLabel( "Schema Location:");
		schemaPanel.add( schemaLocationLabel, FormLayout.LEFT);

		schemaPanel.add( tempPanel, FormLayout.RIGHT_FILL);

		grammarsTab.add( schemaPanel, FormLayout.FULL_FILL);

//		useCompletionForSchemaCheck.setEnabled( false);
		// <<< SchemaViewer/Outliner

		tabs.add( "Grammars", grammarsTab);

		// >>> Other tab
		Box otherTab = Box.createVerticalBox();
		otherTab.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// >>> Fragments
		JPanel fragmentsTab = new JPanel( new BorderLayout());
		fragmentsTab.setBorder( new CompoundBorder( 
				new TitledBorder( "Fragments"),
				new EmptyBorder( 0, 5, 5, 5)));

		// location
		fragmentList = new JList();
		fragmentList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				editFragmentButtonPressed();
			}
		});
		
		fragmentList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = fragmentList.getSelectedIndex();
                if(selected>-1) {
                    fragmentList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		fragmentList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		fragmentList.setVisibleRowCount( 5);
		fragmentList.setCellRenderer( new FragmentListCellRenderer());

		addFragmentButton = new JButton( "Add");
		addFragmentButton.setFont( addFragmentButton.getFont().deriveFont( Font.PLAIN));
		addFragmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addFragmentButtonPressed();
			}
		});
		
		removeFragmentButton = new JButton( "Remove");
		removeFragmentButton.setFont( removeFragmentButton.getFont().deriveFont( Font.PLAIN));
//		removeScenarioButton.setMargin( new Insets( 0, 10, 0, 10));
//		removeScenarioButton.setPreferredSize( new Dimension( removeScenarioButton.getPreferredSize().width, 20));
		removeFragmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				removeFragmentButtonPressed();
			}
		});

		editFragmentButton = new JButton( "Edit");
		editFragmentButton.setFont( editFragmentButton.getFont().deriveFont( Font.PLAIN));
//		setDefaultScenarioButton.setMargin( new Insets( 0, 10, 0, 10));
//		setDefaultScenarioButton.setPreferredSize( new Dimension( setDefaultScenarioButton.getPreferredSize().width, 20));
		editFragmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				editFragmentButtonPressed();
			}
		});

		upFragmentButton = new JButton( "Up");
		upFragmentButton.setFont( upFragmentButton.getFont().deriveFont( Font.PLAIN));
		upFragmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				upFragmentButtonPressed();
			}
		});

		downFragmentButton = new JButton( "Down");
		downFragmentButton.setFont( downFragmentButton.getFont().deriveFont( Font.PLAIN));
		downFragmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				downFragmentButtonPressed();
			}
		});

		scroller = new JScrollPane(	fragmentList,
									JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
									JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		fragmentsTab.add( scroller, BorderLayout.CENTER);

		JPanel orderButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
		orderButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		
		orderButtonPanel.add( upFragmentButton);
		orderButtonPanel.add( downFragmentButton);

		JPanel fragmentButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		fragmentButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		
		fragmentButtonPanel.add( editFragmentButton);
		fragmentButtonPanel.add( addFragmentButton);
		fragmentButtonPanel.add( removeFragmentButton);
		
		JPanel buttonPanel = new JPanel( new BorderLayout());
		buttonPanel.add( fragmentButtonPanel, BorderLayout.EAST);
		buttonPanel.add( orderButtonPanel, BorderLayout.WEST);

		fragmentsTab.add( buttonPanel, BorderLayout.SOUTH);

		otherTab.add( fragmentsTab);

		tabs.add( "Other", otherTab);
		// <<< Fragments

		// >>> Scenario
		JPanel scenariosTab = new JPanel( new BorderLayout());
		scenariosTab.setBorder( new CompoundBorder( 
				new TitledBorder( "Scenarios"),
				new EmptyBorder( 0, 5, 5, 5)));

		// location
		scenarioList = new JList();
		scenarioList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				setDefaultScenarioButtonPressed();
			}
		});
		
		scenarioList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = scenarioList.getSelectedIndex();
                if(selected>-1) {
                    scenarioList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		scenarioList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scenarioList.setVisibleRowCount( 5);
		scenarioList.setCellRenderer( new ScenarioListCellRenderer());

		addScenarioButton = new JButton( "Add");
		addScenarioButton.setFont( addScenarioButton.getFont().deriveFont( Font.PLAIN));
		addScenarioButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addScenarioButtonPressed();
			}
		});
		
		removeScenarioButton = new JButton( "Remove");
		removeScenarioButton.setFont( removeScenarioButton.getFont().deriveFont( Font.PLAIN));
		removeScenarioButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				removeScenarioButtonPressed();
			}
		});

		setDefaultScenarioButton = new JButton( "Set Default");
		setDefaultScenarioButton.setFont( setDefaultScenarioButton.getFont().deriveFont( Font.PLAIN));
		setDefaultScenarioButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				setDefaultScenarioButtonPressed();
			}
		});

		scroller = new JScrollPane(	scenarioList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		scenariosTab.add( scroller, BorderLayout.CENTER);

		JPanel scenarioButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		scenarioButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		
		scenarioButtonPanel.add( setDefaultScenarioButton);
		scenarioButtonPanel.add( addScenarioButton);
		scenarioButtonPanel.add( removeScenarioButton);
		scenariosTab.add( scenarioButtonPanel, BorderLayout.SOUTH);

		otherTab.add( scenariosTab);
		// <<< Scenarios

		// >>> Named XPaths
		JPanel xpathPanel = new JPanel( new BorderLayout());
		xpathPanel.setBorder( new CompoundBorder( 
				new TitledBorder( "Named XPaths"),
				new EmptyBorder( 0, 5, 5, 5)));

		// location
		xpathList = new JList();
		xpathList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				editXPathButtonPressed();
			}
		});
		
		xpathList.addListSelectionListener( new ListSelectionListener() {

            public void valueChanged( ListSelectionEvent arg0) {
                int selected = xpathList.getSelectedIndex();
                if(selected>-1) {
                    xpathList.ensureIndexIsVisible(selected);
                }
            }
		});
		
		xpathList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		xpathList.setVisibleRowCount( 5);
		xpathList.setCellRenderer( new XPathListCellRenderer());

		addXPathButton = new JButton( "Add");
		addXPathButton.setFont( addXPathButton.getFont().deriveFont( Font.PLAIN));
		addXPathButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addXPathButtonPressed();
			}
		});
		
		removeXPathButton = new JButton( "Remove");
		removeXPathButton.setFont( removeXPathButton.getFont().deriveFont( Font.PLAIN));
		removeXPathButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				removeXPathButtonPressed();
			}
		});

		editXPathButton = new JButton( "Edit");
		editXPathButton.setFont( editXPathButton.getFont().deriveFont( Font.PLAIN));
		editXPathButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				editXPathButtonPressed();
			}
		});

		setDefaultXPathButton = new JButton( "Set Default");
		setDefaultXPathButton.setFont( setDefaultXPathButton.getFont().deriveFont( Font.PLAIN));
		setDefaultXPathButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				setDefaultXPathButtonPressed();
			}
		});

		scroller = new JScrollPane(	xpathList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		xpathPanel.add( scroller, BorderLayout.CENTER);

		JPanel xpathButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		xpathButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		
		xpathButtonPanel.add( setDefaultXPathButton);
		xpathButtonPanel.add( editXPathButton);
		xpathButtonPanel.add( addXPathButton);
		xpathButtonPanel.add( removeXPathButton);
		xpathPanel.add( xpathButtonPanel, BorderLayout.SOUTH);

		otherTab.add( xpathPanel);
		// <<< Named XPaths

		//removed for xngr-dialog
		super.okButton.setText(confirmText);
		/*cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( confirmText);
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				okButtonPressed();
			}
		});

		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);*/

		main.add( generalPanel, BorderLayout.NORTH);
		/*main.add( buttonPanel, BorderLayout.SOUTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});*/

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
		
		// >>> temp...
//		useXmlSystemIdCheck.setSelected( false);
//		useXmlSystemIdCheck.setEnabled( false);
//		useXmlValidationSystemIdCheck.setSelected( true);
//		useXmlValidationSystemIdCheck.setEnabled( false);
//
//		useXmlLocationCheck.setSelected( false);
//		useXmlLocationCheck.setEnabled( false);
//		useXmlValidationLocationCheck.setSelected( true);
//		useXmlValidationLocationCheck.setEnabled( false);
		// <<< temp...
		
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
		setLocationRelativeTo( parent);
	}

	private void validationLocationButtonPressed() {
		JFileChooser chooser = getValidationFileChooser();
		
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

//			String text = validationLocationField.getText();
//			
//			if ( text == null || text.trim().length() == 0 && !dtdCheck.isSelected()) {
//				setText( tagCompletionLocationField, url.toString());
//			}
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

//			String text = schemaLocationField.getText();
//			
//			if ( text == null || text.trim().length() == 0 && !dtdCheck.isSelected()) {
//				setText( tagCompletionLocationField, url.toString());
//			}
		}
	}	

	private void iconButtonPressed() {
		JFileChooser chooser = getIconFileChooser();
		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}

			setGrammarIcon( url.toString());

//			String text = schemaLocationField.getText();
//			
//			if ( text == null || text.trim().length() == 0 && !dtdCheck.isSelected()) {
//				setText( tagCompletionLocationField, url.toString());
//			}
		}
	}
	
	private void setGrammarIcon( String location) {
//		System.out.println( "setGrammarIcon( "+location+")");
		iconLocation = location;
		ImageIcon icon = null;
		
		try {
			icon = XngrImageLoader.get().getImage( new URL( location));

			if ( icon.getIconHeight() != 16 || icon.getIconWidth() != 16) {
				icon = new ImageIcon( icon.getImage().getScaledInstance( 16, 16, Image.SCALE_SMOOTH));
			}
		} catch (Exception e) {
//			System.out.println( "setGrammarIcon() [Could not load Icon!]");
			icon = null;
		}
		
		if ( icon == null) {
//			System.out.println( "setGrammarIcon() [Setting default Icon...]");
			icon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/XMLDocumentIcon.gif");
		}
		
		iconLabel.setIcon( icon);
		iconLabel.revalidate();
	}

//	private void baseLocationButtonPressed() {
//		JFileChooser chooser = FileUtilities.getFileChooser();
//		
//		int value = chooser.showOpenDialog( getParent());
//
//		if ( value == JFileChooser.APPROVE_OPTION) {
//			File file = chooser.getSelectedFile();
//			URL url = null;
//
//			try {
//				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
//			} catch ( MalformedURLException x) {
//				x.printStackTrace(); // should never happen
//			}
//
//			setText( baseLocationField, url.toString());
//		}
//	}	

//	private void tagCompletionLocationButtonPressed() {
//		JFileChooser chooser = getTagCompletionFileChooser();
//		
//		int value = chooser.showOpenDialog( getParent());
//
//		if ( value == JFileChooser.APPROVE_OPTION) {
//			File file = chooser.getSelectedFile();
//			URL url = null;
//
//			try {
//				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
//			} catch ( MalformedURLException x) {
//				x.printStackTrace(); // should never happen
//			}
//
//			setText( tagCompletionLocationField, url.toString());
//		}
//	}	

	private void setDefaultScenarioButtonPressed() {
		int index = scenarioList.getSelectedIndex();
		if ( index != -1) {
			ScenarioProperties scenario = scenarioModel.getScenario( index);

			if ( scenario != null) {
				scenarioModel.setDefault( scenario);
			}
		}
	}	

	private void addScenarioButtonPressed() {
		if ( scenarioSelectionDialog == null) {
			scenarioSelectionDialog = new ScenarioSelectionDialog( parent, configurationProperties, "Add", false, false);
		}
		
		scenarioSelectionDialog.setVisible(true);
		
		if ( !scenarioSelectionDialog.isCancelled()) {
			Vector scenarios = scenarioSelectionDialog.getSelectedScenarios();
			
			for ( int i = 0; i < scenarios.size(); i++) {
				scenarioModel.addScenario( (ScenarioProperties)scenarios.elementAt(i));
			}
		}			
	}	

	private void removeScenarioButtonPressed() {
		/*int index = scenarioList.getSelectedIndex();

		if ( index != -1) {
			ScenarioProperties scenario = scenarioModel.getScenario( index);

			if ( scenario != null) {
				scenarioModel.removeScenario( scenario);
			}
		}*/
		
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    //Object[] selectedObjects = scenarioList.getSelectedValues();
	    int[] selectedIndexes = scenarioList.getSelectedIndices();
	    Vector selectedObjects = new Vector();
	    
	    for(int cnt=0;cnt<selectedIndexes.length;++cnt) {
	        selectedObjects.add(scenarioModel.getScenario(selectedIndexes[cnt]));
	    }
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.size()>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.size();++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += ((ScenarioProperties)selectedObjects.get(cnt)).getName();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.size()>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
	                    scenarioModel.removeScenario( (ScenarioProperties)selectedObjects.get(cnt));
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    scenarioModel.removeScenario( (ScenarioProperties)selectedObjects.get(cnt));
					}
		            
	            } else {
                    scenarioModel.removeScenario( (ScenarioProperties)selectedObjects.get(cnt));
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	    
	}	

	private void setDefaultXPathButtonPressed() {
		int index = xpathList.getSelectedIndex();

		if ( index != -1) {
			NamedXPathProperties xpath = xpathModel.getXPath( index);

			if ( xpath != null) {
				xpathModel.setDefault( xpath);
			}
		}
	}	

	private void addXPathButtonPressed() {
		if ( namedXPathPropertiesDialog == null) {
			namedXPathPropertiesDialog = new NamedXPathPropertiesDialog( parent, configurationProperties);
		}
		
		NamedXPathProperties properties = new NamedXPathProperties();
		namedXPathPropertiesDialog.show( properties, xpathModel.getXPaths());
		
		if ( !namedXPathPropertiesDialog.isCancelled()) {
			xpathModel.addXPath( properties);
		}			
	}	

	private void editXPathButtonPressed() {
		if ( namedXPathPropertiesDialog == null) {
			namedXPathPropertiesDialog = new NamedXPathPropertiesDialog( parent, configurationProperties);
		}
		
		int index = xpathList.getSelectedIndex();

		if ( index != -1) {
			NamedXPathProperties properties = xpathModel.getXPath( index);

			Vector xpaths = new Vector();
			Vector allXPaths = xpathModel.getXPaths();
			
			for ( int i = 0; i < allXPaths.size(); i++) {
				if ( allXPaths.elementAt(i) != properties) {
					xpaths.addElement( allXPaths.elementAt(i));
				}
			}
	
			namedXPathPropertiesDialog.show( properties, xpaths);

			if ( !namedXPathPropertiesDialog.isCancelled()) {
				xpathModel.updateXPath( properties);
			}
		}
	}	

	private void removeXPathButtonPressed() {
	    
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    int[] selectedIndexes = xpathList.getSelectedIndices();
	    Vector selectedObjects = new Vector();
	    
	    for(int cnt=0;cnt<selectedIndexes.length;++cnt) {
	        selectedObjects.add(xpathModel.getXPath(selectedIndexes[cnt]));
	    }
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.size()>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.size();++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += ((NamedXPathProperties)selectedObjects.get(cnt)).getName();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.size()>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
	                    xpathModel.removeXPath((NamedXPathProperties)  selectedObjects.get(cnt));
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    xpathModel.removeXPath((NamedXPathProperties)  selectedObjects.get(cnt));
					}
		            
	            } else {
                    xpathModel.removeXPath((NamedXPathProperties)  selectedObjects.get(cnt));
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	    
	}	

	protected void okButtonPressed() {
		String name = descriptionField.getText();
	
		if ( checkName( name)) {
			
		    properties.setDescription( name);
			properties.setExtensions( extensionsField.getText());

			properties.setRootElementName( rootElementField.getText());
			properties.setNamespace( namespaceField.getText());
			properties.setNamespacePrefix( prefixField.getText());
			properties.setPublicID( publicIdField.getText());
			properties.setSystemID( systemIdField.getText());

			if ( !StringUtilities.isEmpty( properties.getNamespacePrefix()) && !StringUtilities.isEmpty( properties.getNamespace())) {
				configurationProperties.addPrefixNamespaceMapping( properties.getNamespacePrefix(), properties.getNamespace());
			}

			properties.setValidationGrammar( getValidationGrammar());
			properties.setValidationLocation( validationLocationField.getText());
			properties.setUseXMLValidationLocation( xmlValidationLocationCheck.isSelected());
			
			Vector previousTagCompletionProperties = properties.getTagCompletionPropertiesList();
			for ( int i = 0; i < previousTagCompletionProperties.size(); i++) {
				properties.removeTagCompletion( (TagCompletionProperties)previousTagCompletionProperties.elementAt(i));
			}

			Vector newTagCompletionProperties = tagCompletionListModel.getTagCompletionList();
			
			for ( int i = 0; i < newTagCompletionProperties.size(); i++) {
				properties.addTagCompletion( (TagCompletionProperties)newTagCompletionProperties.elementAt(i));
			}

			if ( useValidationForCompletionCheck.isSelected()) {
//				properties.setTemplateGrammar( getTagCompletionGrammar());
//				properties.setTemplateLocation( tagCompletionLocationField.getText());
			}

			if ( useValidationForSchemaCheck.isSelected()) {
				properties.setSchemaLocation( "");
			} else {
				properties.setSchemaLocation( schemaLocationField.getText());
			}
//			properties.setBaseLocation( baseLocationField.getText());

			boolean iconsChanged = false;

			if ( !equals( properties.getIconLocation(), iconLocation)) {
				properties.setIconLocation( iconLocation);
				iconsChanged = true;
			}
			
			Vector previousNamespaces = properties.getNamespaces();
			
			for ( int i = 0; i < previousNamespaces.size(); i++) {
				NamespaceProperties namespace = (NamespaceProperties)previousNamespaces.elementAt(i);
				properties.removeNamespace( namespace);
			}

			Vector newNamespaces = namespaceTableModel.getNamespaces();
			
			for ( int i = 0; i < newNamespaces.size(); i++) {
				NamespaceProperties namespace = (NamespaceProperties)newNamespaces.elementAt(i);

				if ( !StringUtilities.isEmpty( namespace.getPrefix()) && !StringUtilities.isEmpty( namespace.getURI())) {
					configurationProperties.addPrefixNamespaceMapping( namespace.getPrefix(), namespace.getURI());
				}
				
				properties.addNamespace( namespace);
			}
			
			Vector previousFragments = properties.getFragments();
			
			for ( int i = 0; i < previousFragments.size(); i++) {
				FragmentProperties fragment = (FragmentProperties)previousFragments.elementAt(i);
				properties.removeFragment( fragment);
			}

			Vector newFragments = fragmentModel.getFragments();
			
			for ( int i = 0; i < newFragments.size(); i++) {
				FragmentProperties fragment = (FragmentProperties)newFragments.elementAt(i);
				properties.addFragment( fragment);
			}

			Vector previousScenarios = properties.getScenarios();
			
			for ( int i = 0; i < previousScenarios.size(); i++) {
				ScenarioProperties scenario = (ScenarioProperties)previousScenarios.elementAt(i);
				properties.removeScenario( scenario);
			}

			Vector newScenarios = scenarioModel.getScenarios();
			
			for ( int i = 0; i < newScenarios.size(); i++) {
				ScenarioProperties scenario = (ScenarioProperties)newScenarios.elementAt(i);
				properties.addScenario( scenario);
			}

			properties.setDefaultScenario( scenarioModel.getDefault());

			Vector previousXPaths = properties.getNamedXPaths();
			
			for ( int i = 0; i < previousXPaths.size(); i++) {
				NamedXPathProperties xpath = (NamedXPathProperties)previousXPaths.elementAt(i);
				properties.removeNamedXPath( xpath);
			}

			Vector newXPaths = xpathModel.getXPaths();
			
			for ( int i = 0; i < newXPaths.size(); i++) {
				NamedXPathProperties xpath = (NamedXPathProperties)newXPaths.elementAt(i);
				properties.addNamedXPath( xpath);
			}

			properties.setDefaultNamedXPath( xpathModel.getDefault());
			
			XDocumentFactory.getInstance().setXPathNamespaceURIs( configurationProperties.getPrefixNamespaceMappings());

			super.okButtonPressed();

			if ( iconsChanged) {
				parent.updateIcons();
			}
		}
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

	private void setValidationGrammar( int type) {
		if ( type == XMLGrammar.TYPE_XSD) {
			validationGrammarBox.setSelectedIndex(1);
		} else if ( type == XMLGrammar.TYPE_RNG) {
			validationGrammarBox.setSelectedIndex(2);
		} else if ( type == XMLGrammar.TYPE_RNC) {
			validationGrammarBox.setSelectedIndex(3);
		} else if ( type == XMLGrammar.TYPE_NRL) {
			validationGrammarBox.setSelectedIndex(4);
		} else {
			validationGrammarBox.setSelectedIndex(0);
		}
	}

	public void show( GrammarProperties properties, boolean create, Vector names) {
		updatedNamespaces = new Vector();
		this.properties = properties;
		this.names = names;
		
		tabs.setSelectedIndex( 0);
		
//		nameField.setText( properties.getName());
		setText( descriptionField, properties.getDescription());
		setText( extensionsField, properties.getExtensions());

		setText( rootElementField, properties.getRootElementName());
		setText( namespaceField, properties.getNamespace());
		setText( prefixField, properties.getNamespacePrefix());
		setText( publicIdField, properties.getPublicID());
		setText( systemIdField, properties.getSystemID());

		String validationLocation = properties.getValidationLocation();
		String schemaLocation = properties.getSchemaLocation();
		
		setValidationGrammar( properties.getValidationGrammar());
		setText( validationLocationField, validationLocation);
		xmlValidationLocationCheck.setSelected( properties.useXMLValidationLocation());

		setGrammarIcon( properties.getIconLocation());
		setText( schemaLocationField, schemaLocation);

		tagCompletionListModel = new TagCompletionListModel( properties.getTagCompletionPropertiesList());
		tagCompletionList.setModel( tagCompletionListModel);

		scenarioModel = new ScenarioListModel( properties.getScenarios());
		scenarioList.setModel( scenarioModel);
		
		scenarioModel.setDefault( properties.getDefaultScenario());
		
		xpathModel = new XPathListModel( properties.getNamedXPaths());
		xpathList.setModel( xpathModel);
		
		xpathModel.setDefault( properties.getDefaultNamedXPath());

		fragmentModel = new FragmentListModel( properties.getFragments());
		fragmentList.setModel( fragmentModel);

		namespaceTableModel.setGrammar( properties);
		
		Vector tags = properties.getTagCompletionPropertiesList();
		if ( tags != null && tags.size() > 0) {
			useValidationForCompletionCheck.setSelected( false);
			
			if ( validationGrammarBox.getSelectedIndex() == 1) {
				if ( schemaLocation == null || schemaLocation.trim().length() == 0) {
					useValidationForSchemaCheck.setSelected( true);
				} else {
					useValidationForSchemaCheck.setSelected( false);
				}
			} else {
				useValidationForSchemaCheck.setSelected( false);
			}
		} else { // if ( tagCompletionLocation == null || tagCompletionLocation.trim().length() == 0) {
			int index = validationGrammarBox.getSelectedIndex();

			if ( index == 1) {
				useValidationForCompletionCheck.setSelected( true);

				if ( schemaLocation == null || schemaLocation.trim().length() == 0) {
					useValidationForSchemaCheck.setSelected( true);
				} else {
					useValidationForSchemaCheck.setSelected( false);
				}
			} else if ( index == 0 || index == 2 || index == 3) {
				useValidationForCompletionCheck.setSelected( true);
				useValidationForSchemaCheck.setSelected( false);
			} else {
				useValidationForCompletionCheck.setSelected( false);
				useValidationForSchemaCheck.setSelected( false);
			}
		}
		
		if (xmlValidationLocationCheck.isSelected())
		{
			useValidationForSchemaCheck.setSelected( false);
			useValidationForSchemaCheck.setEnabled(false);
			
			useValidationForCompletionCheck.setSelected( false);
			useValidationForCompletionCheck.setEnabled(false);
		}      

		//super.setVisible(true);
		super.show();
	}

	private File getSchemaFile( URL url, File file) {
		File result = null;

		try {
			String schema = getFile( url);
			schema = stripExtension( schema);
			schema = schema+".xsd";
			
			result = new File( file, schema);
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		return result;
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

	private JDialog getNamespaceDialog() {
		if ( namespaceDialog == null) {
			namespaceDialog = new XngrDialogHeader( parent, true);
			namespaceDialog.setResizable( false);
			namespaceDialog.setTitle( "Additional Namespaces");
			namespaceDialog.setDialogDescription( "Edit, Add and Delete Additional Namespace information.");
			
			JPanel namespacePanel = new JPanel( new BorderLayout());
			
			namespacePanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Namespaces"),
										new EmptyBorder( 0, 5, 5, 5)));

			namespaceTableModel = new NamespaceTableModel();
			namespaceTable = new JTable( namespaceTableModel);
			namespaceTable.getColumnModel().getColumn( 0).setPreferredWidth( 50);
			namespaceTable.getColumnModel().getColumn( 1).setPreferredWidth( 200);
			namespaceTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			namespaceTable.addAncestorListener( new AncestorListener() {
				public void ancestorAdded( AncestorEvent e) {
				    namespaceTable.requestFocusInWindow();
				}

				public void ancestorMoved( AncestorEvent e) {}
				public void ancestorRemoved( AncestorEvent e) {}
			});
			JScrollPane scrollPane = new JScrollPane(	namespaceTable,
											JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
											JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setPreferredSize( new Dimension( 100, 150));
			namespacePanel.add( scrollPane, BorderLayout.CENTER);
			
			addNamespaceButton = new JButton( "Add");
			addNamespaceButton.setMnemonic('A');
			addNamespaceButton.setFont( addNamespaceButton.getFont().deriveFont( Font.PLAIN));
			addNamespaceButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					addNamespaceButtonPressed();
				}
			});
			
			deleteNamespaceButton = new JButton( "Delete");
			deleteNamespaceButton.setMnemonic('D');
			deleteNamespaceButton.setFont( deleteNamespaceButton.getFont().deriveFont( Font.PLAIN));
			deleteNamespaceButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					deleteNamespaceButtonPressed();
				}
			});

			changeNamespaceButton = new JButton( "Edit");
			changeNamespaceButton.setMnemonic('E');
			changeNamespaceButton.setFont( changeNamespaceButton.getFont().deriveFont( Font.PLAIN));
			changeNamespaceButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					changeNamespaceButtonPressed();
				}
			});

			JPanel namespaceButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
			namespaceButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
			namespaceButtonPanel.add( changeNamespaceButton);
			namespaceButtonPanel.add( addNamespaceButton);
			namespaceButtonPanel.add( deleteNamespaceButton);
			
			namespacePanel.add( namespaceButtonPanel, BorderLayout.SOUTH);
			
			JPanel main = new JPanel( new BorderLayout());
			JPanel dialogPanel = new JPanel(new BorderLayout());
			main.setBorder( new EmptyBorder( 2, 2, 5, 2));
			main.add( namespacePanel, BorderLayout.CENTER);
			
			JButton closeButton = new JButton( "Close");
			closeButton.setMnemonic('C');
			closeButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					getNamespaceDialog().setVisible(false);
				}
			});
			
			JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
			buttonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
			buttonPanel.add( closeButton);
			
			main.add( buttonPanel, BorderLayout.SOUTH);
			dialogPanel.add( main, BorderLayout.CENTER);
			
			namespaceDialog.setContentPane( dialogPanel);
			namespaceDialog.setContentPane( dialogPanel);
			
			namespaceDialog.pack();

		}
		
		return namespaceDialog;
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private NamespacePropertiesDialog getNamespacePropertiesDialog() {
		if ( namespacePropertiesDialog == null) {
			namespacePropertiesDialog = new NamespacePropertiesDialog( parent);
		} 
		
		return namespacePropertiesDialog;
	}

	private void addNamespaceButtonPressed() {
		NamespacePropertiesDialog dialog = getNamespacePropertiesDialog();
		NamespaceProperties properties = new NamespaceProperties();
		
		Vector prefixes = new Vector();
		
		for ( int i = 0; i < namespaceTableModel.getRowCount(); i++) {
			prefixes.addElement( namespaceTableModel.getPrefix(i));
		}

		dialog.show( properties, prefixes);

		if ( !dialog.isCancelled()) {
			namespaceTableModel.addNamespace( properties);
		}
	}

	private void changeNamespaceButtonPressed() {
		int index = namespaceTable.getSelectedRow();

		if ( index != -1) {
			NamespaceProperties properties = namespaceTableModel.getNamespace( index);
			NamespacePropertiesDialog dialog = getNamespacePropertiesDialog();
			Vector prefixes = new Vector();
			
			for ( int i = 0; i < namespaceTableModel.getRowCount(); i++) {
				String prefix = namespaceTableModel.getPrefix(i);

				if ( !prefix.equalsIgnoreCase( properties.getPrefix())) {
					prefixes.addElement( namespaceTableModel.getPrefix(i));
				}
			}
	
			dialog.show( properties, prefixes);

			if ( !dialog.isCancelled()) {
				namespaceTableModel.updateNamespace( properties);
			}
		}
	}

	private void deleteNamespaceButtonPressed() {
		/*int index = namespaceTable.getSelectedRow();

		if ( index != -1) {
			NamespaceProperties properties = namespaceTableModel.getNamespace( index);
			namespaceTableModel.removeNamespace( properties);
		}*/
		
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    int[] selectedIndexes = namespaceTable.getSelectedRows();
	    Object[] selectedObjects = new Object[selectedIndexes.length];
	    for(int cnt=0;cnt<selectedIndexes.length;++cnt) {
	        NamespaceProperties properties = namespaceTableModel.getNamespace( selectedIndexes[cnt]);
	        selectedObjects[cnt] = properties;
	    }
	    
	    //Object[] selectedObjects = prefixNamespaceMappingList.getSelectedValues();
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.length>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.length;++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	                
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            if((((NamespaceProperties) selectedObjects[cnt]).getPrefix().length()>0) && (((NamespaceProperties) selectedObjects[cnt]).getPrefix()!=null)) {
                        message += "xmlns:"+((NamespaceProperties) selectedObjects[cnt]).getPrefix()+"="+((NamespaceProperties) selectedObjects[cnt]).getURI();
                    }
                    else {
                        message += "xmlns="+((NamespaceProperties) selectedObjects[cnt]).getURI();
                    }
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.length>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
	                    namespaceTableModel.removeNamespace((NamespaceProperties) selectedObjects[cnt]);
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    namespaceTableModel.removeNamespace((NamespaceProperties) selectedObjects[cnt]);
					}
		            
	            } else {
	                namespaceTableModel.removeNamespace((NamespaceProperties) selectedObjects[cnt]);
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	    
	}

	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private FragmentPropertiesDialog getFragmentPropertiesDialog() {
		if ( fragmentPropertiesDialog == null) {
			fragmentPropertiesDialog = new FragmentPropertiesDialog( parent, configurationProperties);
		} 
		
		return fragmentPropertiesDialog;
	}

	private void addFragmentButtonPressed() {
		FragmentPropertiesDialog dialog = getFragmentPropertiesDialog();
		FragmentProperties properties = new FragmentProperties();
		
		Vector fragments = fragmentModel.getFragments();
		
		dialog.show( properties, fragments);

		if ( !dialog.isCancelled()) {
			fragmentModel.addFragment( properties);
		}
	}

	private void removeFragmentButtonPressed() {
		/*int index = fragmentList.getSelectedIndex();

		if ( index != -1) {
			FragmentProperties properties = fragmentModel.getFragment( index);
			fragmentModel.removeFragment( properties);
		}*/
	    
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    Object[] selectedObjects = fragmentList.getSelectedValues();
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.length>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.length;++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += ((FragmentProperties)selectedObjects[cnt]).getName();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.length>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
	                    fragmentModel.removeFragment((FragmentProperties) selectedObjects[cnt]);
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    fragmentModel.removeFragment((FragmentProperties) selectedObjects[cnt]);
					}
		            
	            } else {
                    fragmentModel.removeFragment((FragmentProperties) selectedObjects[cnt]);
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************

	}

	private void upFragmentButtonPressed() {
		FragmentProperties fragment = (FragmentProperties)fragmentList.getSelectedValue();

		if( fragment != null) {
            fragmentModel.moveUp( fragment);

            if ( fragmentList.getSelectedIndex() > 0) {
            	fragmentList.setSelectedIndex( fragmentList.getSelectedIndex()-1);
            }
		}
	}

	private void downFragmentButtonPressed() {
		FragmentProperties fragment = (FragmentProperties)fragmentList.getSelectedValue();

		if( fragment != null) {
            fragmentModel.moveDown( fragment);
            
            if ( fragmentList.getSelectedIndex() < (fragmentModel.getSize()-1)) {
            	fragmentList.setSelectedIndex( fragmentList.getSelectedIndex()+1);
            }
		}
	}

	private void editFragmentButtonPressed() {
		int index = fragmentList.getSelectedIndex();

		if ( index != -1) {
			FragmentProperties properties = fragmentModel.getFragment( index);
			FragmentPropertiesDialog dialog = getFragmentPropertiesDialog();
			Vector fragments = new Vector();
			Vector allFragments = fragmentModel.getFragments();
			
			for ( int i = 0; i < allFragments.size(); i++) {
				if ( allFragments.elementAt(i) != properties) {
					fragments.addElement( allFragments.elementAt(i));
				}
			}
	
			dialog.show( properties, fragments);

			if ( !dialog.isCancelled()) {
				fragmentModel.updateFragment( properties);
			}
		}
	}

// <<<<<<< GrammarPropertiesDialog.java
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
		/*int index = tagCompletionList.getSelectedIndex();

		if ( index != -1) {
			TagCompletionProperties properties = tagCompletionListModel.getTagCompletion( index);
			tagCompletionListModel.removeTagCompletion( properties);
		}*/
		
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    Object[] selectedObjects = tagCompletionList.getSelectedValues();
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.length>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.length;++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += ((TagCompletionProperties)selectedObjects[cnt]).toString();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.length>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
		                tagCompletionListModel.removeTagCompletion((TagCompletionProperties) selectedObjects[cnt]);
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    tagCompletionListModel.removeTagCompletion((TagCompletionProperties) selectedObjects[cnt]);
					}
		            
	            } else {
	                tagCompletionListModel.removeTagCompletion((TagCompletionProperties) selectedObjects[cnt]);
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	    
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

	/**
	 * When the dialog is cancelled and no selection has been made, 
	 * this method returns true.
	 *
	 * @return true when the dialog has been cancelled.
	 */
	public boolean isCancelled() {
		return cancelled;
	}
	
// =======
// >>>>>>> 1.6
	private File getSelectedChooserFile() {
		ExchangerDocument doc = parent.getDocument();
		URL url = null;
		
		if ( doc != null) {
			url = doc.getURL();
		}

		if ( url != null && url.getProtocol().equals( "file")) {
			return new File( url.getFile());
		} 
		
		return FileUtilities.getLastOpenedFile();
	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
//	private JFileChooser getSchemaFileChooser() {
//		if ( schemaChooser == null) {
//			schemaChooser = new JFileChooser();
//			schemaChooser.addChoosableFileFilter( new DefaultFileFilter( "xsd", "XML Schema Document"));
//		} 
//		
//		File file = FileUtilities.convertURL2File( templateLocationField.getText());
//		File validationFile = null;
//		
//		if ( validationLocationField.isEnabled()) {
//			validationFile = FileUtilities.convertURL2File( validationLocationField.getText());
//		}
//		
//		if ( file != null) {
//			schemaChooser.setCurrentDirectory( file);
//		} else if ( validationFile != null) {
//			schemaChooser.setCurrentDirectory( validationFile);
//		} else {
//			schemaChooser.setCurrentDirectory( getSelectedChooserFile());
//		}
//
//		schemaChooser.rescanCurrentDirectory();
//		
//		return schemaChooser;
//	}
	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getValidationFileChooser() {
		if ( validationChooser == null) {
			validationChooser = FileUtilities.createFileChooser();

			if ( xsdFilter == null) {
				xsdFilter = new DefaultFileFilter( "xsd", "XML Schema Document");
			}
			
			if ( dtdFilter == null) {
				dtdFilter = new DefaultFileFilter( "dtd", "Document Type Definition");
			}
			
			if ( rngFilter == null) {
				rngFilter = new DefaultFileFilter( "rng", "RelaxNG");
			}

			if ( rncFilter == null) {
				rncFilter = new DefaultFileFilter( "rnc", "RelaxNG Compact Format");
			}

			if ( nrlFilter == null) {
				nrlFilter = new DefaultFileFilter( "nrl", "Namespace Routing Language");
			}

			validationChooser.addChoosableFileFilter( xsdFilter);
			validationChooser.addChoosableFileFilter( dtdFilter);
			validationChooser.addChoosableFileFilter( rngFilter);
			validationChooser.addChoosableFileFilter( rncFilter);
			validationChooser.addChoosableFileFilter( nrlFilter);
		} 
		
		int index = validationGrammarBox.getSelectedIndex();

		if ( index == 1) {
			validationChooser.setFileFilter( xsdFilter);
		} else if ( index == 0) {
			validationChooser.setFileFilter( dtdFilter);
		} else if ( index == 2) {
			validationChooser.setFileFilter( rngFilter);
		} else if ( index == 3) {
			validationChooser.setFileFilter( rncFilter);
		} else if ( index == 4) {
			validationChooser.setFileFilter( nrlFilter);
		}

		File file = URLUtilities.toFile( validationLocationField.getText());
		
		if ( file != null) {
			validationChooser.setCurrentDirectory( file);
		} else {
			validationChooser.setCurrentDirectory( getSelectedChooserFile());
		}
		
		validationChooser.rescanCurrentDirectory();
		
		return validationChooser;
	}


	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getIconFileChooser() {
		if ( iconChooser == null) {
			iconChooser = FileUtilities.createFileChooser();
			iconFilter = new DefaultFileFilter( "gif jpg", "Icon File");

			iconChooser.addChoosableFileFilter( iconFilter);
		} 
		
		iconChooser.setFileFilter( iconFilter);

		File file = URLUtilities.toFile( iconLocation);
		File validationFile = null;
		File schemaFile = null;
		
		if ( validationLocationField.isEnabled()) {
			validationFile = URLUtilities.toFile( validationLocationField.getText());
		}
		
		if ( schemaLocationField.isEnabled()) {
			schemaFile = URLUtilities.toFile( schemaLocationField.getText());
		}

		if ( file != null) {
			iconChooser.setCurrentDirectory( file);
		} else if ( validationFile != null) {
			iconChooser.setCurrentDirectory( validationFile);
		} else if ( schemaFile != null) {
			iconChooser.setCurrentDirectory( schemaFile);
		} else {
			iconChooser.setCurrentDirectory( getSelectedChooserFile());
		}
		
		iconChooser.rescanCurrentDirectory();
		
		return iconChooser;
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
		
		File validationFile = null;

		if ( validationLocationField.isEnabled()) {
			validationFile = URLUtilities.toFile( validationLocationField.getText());
		}

		if ( file != null) {
			schemaChooser.setCurrentDirectory( file);
		} else if ( validationFile != null) {
			schemaChooser.setCurrentDirectory( validationFile);
		} else {
			schemaChooser.setCurrentDirectory( getSelectedChooserFile());
		}
		
		schemaChooser.rescanCurrentDirectory();
		
		return schemaChooser;
	}

//	// The creation of the file chooser causes an inspection of all 
//	// the drives, this can take some time so only do this when necessary.
//	private JFileChooser getDTDFileChooser() {
//		if ( dtdChooser == null) {
//			dtdChooser = new JFileChooser();
//			dtdChooser.addChoosableFileFilter( new DefaultFileFilter( "dtd", "DTD File"));
//		} 
//
//		File file = getSelectedChooserFile();
//
//		if ( file != null) {
//			dtdChooser.setSelectedFile( file);
//		}
//
//		dtdChooser.rescanCurrentDirectory();
//		
//		return dtdChooser;
//	}

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
	
	class ScenarioListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
			Component comp = super.getListCellRendererComponent( list, value, index, selected, focus);

			if ( list.getModel() instanceof ScenarioListModel) {
				ScenarioListModel model = (ScenarioListModel)list.getModel();
				
				ScenarioProperties scenario = model.getScenario( index);

				if ( model.isDefault( scenario)) {
					comp.setFont( comp.getFont().deriveFont( Font.BOLD));
				} else {
					comp.setFont( comp.getFont().deriveFont( Font.PLAIN));
				}
			}
			
			return comp;
		}
	}
	
	class XPathListCellRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
			Component comp = super.getListCellRendererComponent( list, value, index, selected, focus);

			if ( list.getModel() instanceof XPathListModel) {
				XPathListModel model = (XPathListModel)list.getModel();
				
				NamedXPathProperties xpath = model.getXPath( index);

				if ( model.isDefault( xpath)) {
					comp.setFont( comp.getFont().deriveFont( Font.BOLD));
				} else {
					comp.setFont( comp.getFont().deriveFont( Font.PLAIN));
				}
			}
			
			return comp;
		}
	}

	private boolean checkName( String name) {
		if ( isEmpty( name)) {
			MessageHandler.showMessage( "Please specify a Name for this Type.");
			return false;
		}
		
		if ( isEmpty( rootElementField.getText()) && isEmpty( namespaceField.getText()) && isEmpty( publicIdField.getText()) && isEmpty( systemIdField.getText())) {
			MessageHandler.showMessage( "No Type Definition has been declared.\n"+
										"Please specify, a Root Element, Namespace, Public ID or System ID.");
			return false;
		}

		for ( int i = 0; i < names.size(); i++) {
			if ( ((String)names.elementAt(i)).equalsIgnoreCase( name)) {
				if ( allowOverride) {
					int result = MessageHandler.showConfirm( 
												"A Type with the name \""+name+"\" exists already.\n"+
												"Do you want to override the existing Type?");
					if ( result == JOptionPane.YES_OPTION) {
						return true;
					} else {
						return false;
					}
				} else {
					MessageHandler.showMessage( "A Type with the name \""+name+"\" exists already.\n"+
												"Please specify another name.");
					return false;
				}
			}
		}

		return true;
	}

	private class NamespaceTableModel extends AbstractTableModel {
		private Vector namespaces = null;
		private GrammarProperties properties = null;
		
		public NamespaceTableModel() {
			namespaces = new Vector();
		}
		
		public void setGrammar( GrammarProperties props) {
			this.properties = props;
			
			setNamespaces( props.getNamespaces());
		}

		private void setNamespaces( Vector namespaces) {
			for ( int i = 0; i < namespaces.size(); i++) {
				updatedNamespaces.addElement( new NamespaceProperties( (NamespaceProperties)namespaces.elementAt(i)));
			}

			this.namespaces = updatedNamespaces;
			
			fireTableDataChanged();
		}

		public void addNamespace( NamespaceProperties props) {
			namespaces.addElement( props);
			
			if ( namespaces.size() > 1) {
				fireTableRowsInserted( namespaces.size()-2, namespaces.size()-1);
			} else {
				fireTableRowsInserted( 0, 0);
			}
		}

		public void updateNamespace( NamespaceProperties props) {
			int index = namespaces.indexOf( props);

			fireTableRowsUpdated( index, index);
		}

		public void removeNamespace( NamespaceProperties props) {
			int index = namespaces.indexOf( props);
			namespaces.removeElement( props);

			fireTableRowsDeleted( index, index);
		}

		public int getRowCount() {
			return namespaces.size();
		}

		public Vector getNamespaces() {
			return namespaces;
		}
		
		public NamespaceProperties getNamespace( int row) {
			NamespaceProperties result = null;
			
			if ( namespaces.size() >= row) {
				result = (NamespaceProperties)namespaces.elementAt( row);
			}

			return result;
		}

		public int getRow( NamespaceProperties props) {
			return namespaces.indexOf( props);
		}

		public String getColumnName( int column) {
			String name = "";

			if ( column == 0) {
				name = "Prefix";
			} else if ( column == 1) {
				name = "URI";
			}
			
			return name;
		}

		public Class getColumnClass( int column) {
			return String.class;
		}

		public int getColumnCount() {
			return 2;
		}

		public String getPrefix( int row) {
			return ((NamespaceProperties)namespaces.elementAt( row)).getPrefix();
		}

		public Object getValueAt( int row, int column) {
			Object result = null;
			
			if ( column == 0) {
				result = ((NamespaceProperties)namespaces.elementAt( row)).getPrefix();
			} else if ( column == 1) {
				result = ((NamespaceProperties)namespaces.elementAt( row)).getURI();
			}
			
			return result;
		}
	}

	class ScenarioListModel extends AbstractListModel {
		Vector elements = null;
		ScenarioProperties defaultScenario = null; 
		
		public ScenarioListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				ScenarioProperties element = (ScenarioProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getName().compareToIgnoreCase( ((ScenarioProperties)elements.elementAt(j)).getName()) <= 0) {
						index = j;
					}
				}
				
				if ( index != -1) {
					elements.insertElementAt( element, index);
				} else {
					elements.addElement( element);
				}
			}
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public void addScenario( ScenarioProperties props) {
			if ( !contains( props)) {
				elements.addElement( props);
	
				fireIntervalAdded( this, elements.size()-1, elements.size()-1);
			}
		}

		public void removeScenario( ScenarioProperties props) {
			int index = elements.indexOf( props);
			elements.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}
		
		private boolean contains( ScenarioProperties props) {
			for ( int i = 0; i < elements.size(); i++) {
				ScenarioProperties scenario = (ScenarioProperties)elements.elementAt(i);
				if ( scenario.getID().equals( props.getID())) {
					return true;
				}
			}
			
			return false;
		}

		public void setDefault( ScenarioProperties scenario) {
			defaultScenario = scenario;
			
			fireContentsChanged( this, 0, elements.size()-1);
		}

		public boolean isDefault( ScenarioProperties scenario) {
			boolean result = false;
			
			if ( defaultScenario != null && scenario != null) {
				result = defaultScenario.getID().equals( scenario.getID()); 
			}

			return result; 
		}

		public ScenarioProperties getDefault() {
			return defaultScenario; 
		}

		public Vector getScenarios() {
			return elements; 
		}

		public Object getElementAt( int i) {
			return ((ScenarioProperties)elements.elementAt( i)).getName();
		}

		public ScenarioProperties getScenario( int i) {
			return (ScenarioProperties)elements.elementAt( i);
		}
	}
	
	class XPathListModel extends AbstractListModel {
		Vector elements = null;
		NamedXPathProperties defaultXPath = null; 
		
		public XPathListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				NamedXPathProperties element = (NamedXPathProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getName().compareToIgnoreCase( ((NamedXPathProperties)elements.elementAt(j)).getName()) <= 0) {
						index = j;
					}
				}
				
				if ( index != -1) {
					elements.insertElementAt( element, index);
				} else {
					elements.addElement( element);
				}
			}
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public void addXPath( NamedXPathProperties props) {
			if ( !contains( props)) {
				elements.addElement( props);
	
				fireIntervalAdded( this, elements.size()-1, elements.size()-1);
			}
		}

		public void updateXPath( NamedXPathProperties props) {
			int index = elements.indexOf( props);

			fireContentsChanged( this, index, index);
		}

		public void removeXPath( NamedXPathProperties props) {
			int index = elements.indexOf( props);
			elements.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}
		
		private boolean contains( NamedXPathProperties props) {
			for ( int i = 0; i < elements.size(); i++) {
				NamedXPathProperties xpath = (NamedXPathProperties)elements.elementAt(i);
				if ( xpath.getID().equals( props.getID())) {
					return true;
				}
			}
			
			return false;
		}

		public void setDefault( NamedXPathProperties xpath) {
			defaultXPath = xpath;
			
			fireContentsChanged( this, 0, elements.size()-1);
		}

		public boolean isDefault( NamedXPathProperties xpath) {
			boolean result = false;
			
			if ( defaultXPath != null && xpath != null) {
				result = defaultXPath.getID().equals( xpath.getID()); 
			}

			return result; 
		}

		public NamedXPathProperties getDefault() {
			return defaultXPath; 
		}

		public Vector getXPaths() {
			return elements; 
		}

		public Object getElementAt( int i) {
			return ((NamedXPathProperties)elements.elementAt( i)).getName();
		}

		public NamedXPathProperties getXPath( int i) {
			return (NamedXPathProperties)elements.elementAt( i);
		}
	}

	class FragmentListModel extends AbstractListModel {
		Vector elements = null;
		FragmentProperties defaultScenario = null; 
		
		public FragmentListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				FragmentProperties element = (FragmentProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getOrder() <= ((FragmentProperties)elements.elementAt(j)).getOrder()) {
						index = j;
					}
				}
				
				if ( index != -1) {
					elements.insertElementAt( element, index);
				} else {
					elements.addElement( element);
				}
			}
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public void addFragment( FragmentProperties props) {
			elements.addElement( props);
			props.setOrder( elements.size()-1);

			fireIntervalAdded( this, elements.size()-1, elements.size()-1);
		}

		public void updateFragment( FragmentProperties props) {
			int index = elements.indexOf( props);
	
			fireContentsChanged( this, index, index);
		}

		public void moveUp( FragmentProperties props) {
			int index = elements.indexOf( props);
			
			if ( index > 0) {
				FragmentProperties frag = (FragmentProperties)elements.elementAt( index - 1);
				frag.setOrder( index);
				props.setOrder( index - 1);
				
				elements.removeElement( props);
				elements.insertElementAt( props, index -1);

				fireContentsChanged( this, index-1, index);
			}
		}

		public void moveDown( FragmentProperties props) {
			int index = elements.indexOf( props);
			
			if ( index < elements.size() - 1) {
				FragmentProperties frag = (FragmentProperties)elements.elementAt( index + 1);
				frag.setOrder( index);
				props.setOrder( index + 1);
				
				elements.removeElement( props);
				elements.insertElementAt( props, index+1);

				fireContentsChanged( this, index+1, index);
			}
		}

		public void removeFragment( FragmentProperties props) {
			int index = elements.indexOf( props);
			elements.removeElement( props);
			
			for ( int i = index; i < elements.size(); i++) {
				FragmentProperties frag = (FragmentProperties)elements.elementAt(i);
				frag.setOrder( i);
			}

			fireIntervalRemoved( this, index, index);
		}
		
		public Vector getFragments() {
			return elements; 
		}

		public Object getElementAt( int i) {
			return (FragmentProperties)elements.elementAt( i);
		}

		public FragmentProperties getFragment( int i) {
			return (FragmentProperties)elements.elementAt( i);
		}
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

	class FragmentListCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel fragment 	= null;
		private JLabel shortcut		= null;
		
		public FragmentListCellRenderer() {
			super( new BorderLayout());
			
			setBorder( new EmptyBorder( 1, 2, 1, 5));
			setOpaque( true);
			
			fragment = new JLabel();
			fragment.setOpaque( false);
			
			shortcut = new JLabel();
			shortcut.setOpaque( false);
			
			this.add( fragment, BorderLayout.CENTER);
			this.add( shortcut, BorderLayout.EAST);
		}
		
		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected, boolean cellHasFocus) {	
			boolean clash = false;
			
			if ( value instanceof FragmentProperties) {
				FragmentProperties f = (FragmentProperties)value;
				fragment.setText( f.getName());
				setIcon( f.getIcon());
				String key = f.getKey();
				
				clash = isClash( key);

				if ( key != null && key.length() > 0) {
					shortcut.setText( "("+key+")");
				} else {
					shortcut.setText("");
				}
			}
			
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				shortcut.setForeground( clash ? Color.red : list.getSelectionForeground());
				
				fragment.setForeground( list.getSelectionForeground());
			}
			else {
				setBackground(list.getBackground());
				shortcut.setForeground( clash ? Color.red : list.getForeground());
				fragment.setForeground(list.getForeground());
			}

			setEnabled(list.isEnabled());
			
			fragment.setFont( list.getFont().deriveFont( Font.BOLD));
			shortcut.setFont( list.getFont().deriveFont( Font.PLAIN, list.getFont().getSize()-2));

			return this;
		}

		private void setIcon( String location) {
			ImageIcon icon = null;
			
			try {
				icon = XngrImageLoader.get().getImage( new URL( location));

				if ( icon.getIconHeight() != 16 || icon.getIconWidth() != 16) {
					icon = new ImageIcon( icon.getImage().getScaledInstance( 16, 16, Image.SCALE_SMOOTH));
				}
			} catch (Exception e) {
				icon = null;
			}
			
			if ( icon == null) {
				icon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/DefaultFragmentIcon.gif");
			}
			
			fragment.setIcon( icon);
		}
		
		private boolean isClash( String key) {
			if ( key != null) {
				int index =  key.lastIndexOf("+");
				
				if ( index != -1) {
					String mask = key.substring(0, index);
					String value = key.substring( index+1, key.length());
					Keystroke keystroke = new Keystroke( mask, value);
			
					String config = configurationProperties.getKeyPreferences().getActiveConfiguration();
					Hashtable keyMaps = configurationProperties.getKeyPreferences().getKeyMaps( config);
			
					Enumeration actions = keyMaps.keys();
					while ( actions.hasMoreElements()) {
						String actionName = (String)actions.nextElement();
						KeyMap km = (KeyMap)keyMaps.get(actionName);
						
						// get keystroke
						Vector strokes = km.getKeystrokes();
						if ( strokes.size() == 1) {
							Keystroke stroke = (Keystroke)strokes.get(0);

							if ( stroke.equals(keystroke)) {
								// we have a clash
								return true;
							}
						}
					}
				}
			}
			
			return false;
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

	private boolean equals( String string1, String string2) {
		if ( string1 != null && string2 != null) {
			return string1.equals( string2);
		} else if ( string1 == null && string2 == null) {
			return true;
		}
		
		return false;
	}
} 
