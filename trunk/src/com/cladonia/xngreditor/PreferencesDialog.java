/*
 * $Id: PreferencesDialog.java,v 1.36 2005/08/31 10:27:16 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.DefaultFileFilter;
import org.bounce.FormConstraints;
import org.bounce.FormLayout;
import org.bounce.QLabel;

import com.cladonia.util.loader.ExtensionClassLoader;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.designer.DesignerProperties;
import com.cladonia.xml.editor.Constants;
import com.cladonia.xml.editor.EditorProperties;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xml.navigator.NavigatorProperties;
import com.cladonia.xml.viewer.ViewerProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.KeyMap;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.properties.Keystroke;
import com.cladonia.xngreditor.properties.PrintPreferences;
import com.cladonia.xngreditor.properties.TextPreferences;
import com.cladonia.xngreditor.properties.SecurityPreferences;
import com.l2fprod.common.swing.JDirectoryChooser;


/**
 * The preferences dialog for the xngreditor application.
 *
 * @version	$Revision: 1.36 $, $Date: 2005/08/31 10:27:16 $
 * @author Dogsbay
 */
public class PreferencesDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 470, 580);
	
	private static final String[] FONT_SIZES =  { "8", "9", "10", "11", "12", "14", "16", "18", "20", "22", "24", "26", "28", "36", "48", "72"};
	private static final String[] SPACES =  { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

	private ExchangerEditor parent = null;

	private Font[] fonts = null;

	private Vector styles 		= null;

	private PrintPreferences printPreferences	= null;
	private SecurityPreferences securityPreferences	= null;
	private TextPreferences textPreferences	= null;
	private ConfigurationProperties properties = null;
	private EditorProperties editorProperties = null;
	private ViewerProperties viewerProperties = null;
	private DesignerProperties designerProperties = null;
	private NavigatorProperties navigatorProperties = null;
	private KeyPreferences keyPreferences = null;

	private FontStylePanel stylePanel		= null;

	private JColorChooser colorChooser	= null;

	private XmlEditorPane xmlEditor	= null;

	private JComboBox fontSelectionBox	= null;
	private JComboBox sizeSelectionBox	= null;
	private JComboBox tabSizeBox		= null;
	private JCheckBox convertTabBox		= null;

	// Editor boxes
	private JCheckBox indentMixedContentBox	= null;
//	private JCheckBox softWrapBox			= null;
//	private JCheckBox tagCompletionBox		= null;
//	private JCheckBox showMarginBox			= null;
//	private JCheckBox showOverviewMarginBox	= null;
//	private JCheckBox showFoldingMarginBox	= null;
//	private JCheckBox autoIndentationBox	= null;
//	private JCheckBox textPromptingBox		= null;
	private JCheckBox antialiasingBox		= null;
//	private JCheckBox strictTextPromptingBox	= null;
	
	// Viewer boxes
//	private JCheckBox showAttributes	= null;
//	private JCheckBox showNamespaces	= null;
//	private JCheckBox showValues		= null;
//	private JCheckBox showComments		= null;
//	private JCheckBox showInline			= null;
//	private JCheckBox showPI			= null;

	// Printing
	private JComboBox printFontSelectionBox	= null;
	private JComboBox printSizeSelectionBox	= null;
	private JCheckBox printWrapText			= null;
	private JCheckBox printLineNumbers		= null;
	private JCheckBox printHeader			= null;

	// Designer boxes
//	private JCheckBox autoCreateRequiredBox	= null;
//	private JCheckBox showAttributeValuesBox	= null;
//	private JCheckBox showElementValuesBox	= null;

	// General boxes
	private JCheckBox autoSyncSelectionBox							= null;
	private JCheckBox scrollDocumentTabsBox							= null;
	private JCheckBox showFullPathBox								= null;
//	private JCheckBox showNavigatorAttributesBox					= null;
	private JCheckBox checkTypeOnOpeningBox							= null;
	private JCheckBox promptCreateTypeOnOpeningBox					= null;
	private JCheckBox validateOnOpeningBox							= null;
	private JCheckBox useInternalSchemaBox							= null;
	private JCheckBox hideScenarioExecutionDialogWhenCompleteBox	= null;
	private JCheckBox openXIncludeInNewDocumentBox					= null;
	private JCheckBox uniqueXPathBox								= null;
	private JCheckBox multipleDocumentsBox							= null;

	// Proxy fields
	private JCheckBox useProxyCheck	= null;
	private JLabel proxyHostLabel	= null;
	private JTextField proxyHostField	= null;
	private JLabel proxyPortLabel	= null;
	private JTextField proxyPortField	= null;
	private JTextField browserField	= null;

	private JRadioButton xalanRadio		= null;
	private JRadioButton saxon1Radio	= null;
	private JRadioButton saxon2Radio	= null;

	private JCheckBox loadDTDGrammarCheck	= null;

	// Format boxes
	private JCheckBox wrapTextCheck					= null;
	private JFormattedTextField wrappingColumnField	= null;

	private JRadioButton customFormatterRadio	= null;
	private JRadioButton compactFormatterRadio	= null;
	private JRadioButton standardFormatterRadio	= null;

	private JCheckBox padTextCheck				= null;
	private JCheckBox indentCheck				= null;
	private JCheckBox newlinesCheck				= null;
	private JCheckBox stripWhitespaceCheck		= null;
	private JCheckBox preserveMixedContentCheck	= null;

	private JList catalogList				= null;
	private DefaultListModel catalogsModel	= null;
	/*private Vector removedCatalogs			= null;
	private Vector addedCatalogs			= null;*/
	
	private PrefixNamespaceMappingPanel prefixNamespaceMappingPanel = null;

	private JCheckBox preferPublicIdentifiersBox	= null;

	private JList extensionList					= null;
	private DefaultListModel extensionsModel	= null;
	//private Vector removedExtensions			= null;
	//private Vector addedExtensions				= null;
	
	private JComboBox lafSelectionBox			= null;
	private JComboBox keySelectionBox			= null;

	private JFileChooser keystoreFileChooser 		= null;
	private JComboBox keystoreTypeBox				= null;
	private JTextField keystoreFileField			= null;
	private JPasswordField keystorePasswordField	= null;
	private JTextField privatekeyAliasField			= null;
	private JPasswordField privatekeyPasswordField	= null;
	private JTextField certificateAliasField		= null;
	
	// stores the current keymapping configuration 
	private JComboBox actionNames 			    = null;
	private JTextArea actionDescription 		= null;
	private JTextField keySequence				= null;
	private Hashtable keyMaps					= null;
	private Hashtable configMap					= null;
	private KeyMapDialog keymapDialog 			= null;

    private Vector initialExtensions;

    private Vector currentExtensions;

	/**
	 * The dialog that displays the preferences for the editor.
	 *
	 * @param frame the parent frame.
	 */
	public PreferencesDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, false);
		
		this.parent = parent;
		this.properties = props;
		this.printPreferences = props.getPrintPreferences();
		this.securityPreferences = props.getSecurityPreferences();
		this.textPreferences = props.getTextPreferences();
		this.editorProperties = props.getEditorProperties();
		this.designerProperties = props.getDesignerProperties();
		this.viewerProperties = props.getViewerProperties();

		this.navigatorProperties = props.getNavigatorProperties();
		this.keyPreferences = props.getKeyPreferences();
	
		
		setResizable( false);
		setTitle( "Preferences");
		setDialogDescription( "Specify the global Exchanger XML Editor settings.");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JTabbedPane tabs = new JTabbedPane();
		
		tabs.add( "Text", createTextTab());
		tabs.add( "Views", createViewsTab());
		tabs.add( "XML", createXMLTab());
		tabs.add( "Format", createFormatTab());
		tabs.add( "Print", createPrintTab());
		tabs.add( "Security", createSecurityTab());
		tabs.add( "Keys",createKeysTab());
		tabs.add( "System", createSystemTab());
		
		// enable/disable the system tab, depending
//		tabs.setEnabledAt( 6, System.getProperty( "lax.dir") != null);
		
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

		main.add( tabs, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	protected void okButtonPressed() {
		if ( isVisible()) {
			int index = fontSelectionBox.getSelectedIndex();
			int size = Integer.parseInt( (String)sizeSelectionBox.getSelectedItem());
			int tabSize = Integer.parseInt( (String)tabSizeBox.getSelectedItem());
	
			if ( index != -1) {
				Font font = fonts[ index].deriveFont( (float)size);
				textPreferences.setFont( font);
			}
	
			for ( int i = 0; i < styles.size(); i++) {
				FontStyle style = (FontStyle)styles.elementAt(i);
				style.update();
			}
			
			textPreferences.setConvertTab( convertTabBox.isSelected());
			textPreferences.setSpaces( tabSize);
	
			// Format boxes
			editorProperties.setWrapText( wrapTextCheck.isSelected());
	
			int value = -1;
			try {
				wrappingColumnField.commitEdit();
				value = ((Long)wrappingColumnField.getValue()).intValue();
			} catch( ParseException e) { 
				e.printStackTrace();
			}
	
			editorProperties.setWrappingColumn( value);
			
			if ( customFormatterRadio.isSelected()) {
				editorProperties.setFormatType( EditorProperties.FORMAT_CUSTOM);
	
				editorProperties.setCustomPadText( padTextCheck.isSelected());
				editorProperties.setCustomIndent( indentCheck.isSelected());
				editorProperties.setCustomNewline( newlinesCheck.isSelected());
				editorProperties.setCustomStrip( stripWhitespaceCheck.isSelected());
				editorProperties.setCustomPreserveMixedContent( preserveMixedContentCheck.isSelected());
			} else if ( compactFormatterRadio.isSelected()) {
				editorProperties.setFormatType( EditorProperties.FORMAT_COMPACT);
			} else { // standardFormatterRadio.isSelected()
				editorProperties.setFormatType( EditorProperties.FORMAT_STANDARD);
			}
		
			// Editor boxes
	//		editorProperties.setIndentMixedContent( indentMixedContentBox.isSelected());
//			editorProperties.setTagCompletion( tagCompletionBox.isSelected());
//			editorProperties.setSoftWrapping( softWrapBox.isSelected());
//			editorProperties.setShowMargin( showMarginBox.isSelected());
//			editorProperties.setShowOverviewMargin( showOverviewMarginBox.isSelected());
//			editorProperties.setShowFoldingMargin( showFoldingMarginBox.isSelected());
//			editorProperties.setSmartIndentation( autoIndentationBox.isSelected());
//			editorProperties.setTextPrompting( textPromptingBox.isSelected());
			textPreferences.setAntialiasing( antialiasingBox.isSelected());
			properties.setUniqueXPath( uniqueXPathBox.isSelected());
			properties.setMultipleDocumentOccurrences( multipleDocumentsBox.isSelected());
			
			//		editorProperties.setStrictTextPrompting( strictTextPromptingBox.isSelected());
	
//			designerProperties.setAutoCreateRequired( autoCreateRequiredBox.isSelected());
//			designerProperties.setShowAttributeValues( showAttributeValuesBox.isSelected());
//			designerProperties.setShowElementValues( showElementValuesBox.isSelected());
	
			properties.setAutoSyncSelection( autoSyncSelectionBox.isSelected());
			properties.setScrollDocumentTabs( scrollDocumentTabsBox.isSelected());
	
			properties.setShowFullPath( showFullPathBox.isSelected());
	//		navigatorProperties.setShowAttributeValues( showNavigatorAttributesBox.isSelected());
	
			properties.setCheckTypeOnOpening( checkTypeOnOpeningBox.isSelected());
			properties.setPromptCreateTypeOnOpening( promptCreateTypeOnOpeningBox.isSelected());
			properties.setValidateOnOpening( validateOnOpeningBox.isSelected());
			properties.setUseInternalSchema( useInternalSchemaBox.isSelected());
	
			properties.setHideExecuteScenarioDialogWhenComplete( hideScenarioExecutionDialogWhenCompleteBox.isSelected());
			properties.setOpenXIncludeInNewDocument( openXIncludeInNewDocumentBox.isSelected());
	
			String port = proxyPortField.getText();
			String host = proxyHostField.getText();
			
			properties.setUseProxy( useProxyCheck.isSelected());
			properties.setProxyPort( port);
			properties.setProxyHost( host);
			
			
			if ( useProxyCheck.isSelected()) {
				System.setProperty( "http.proxyHost", host);
				System.setProperty( "http.proxyPort", port);
			} else {
				System.getProperties().remove( "http.proxyHost");
				System.getProperties().remove( "http.proxyPort");
			}
	
			if ( browserField != null) {
				String browser = browserField.getText();
				
				if ( browser != null && browser.trim().length() > 0) {
					System.setProperty( "org.bounce.browser", host);
				} else {
					System.getProperties().remove( "org.bounce.browser");
				}
		
				properties.setBrowser( browser);
			}
	
			// Heap size
	//		try {
	//			maximumHeapSizeText.commitEdit();
	//
	//			int maxsize = ((Long)maximumHeapSizeText.getValue()).intValue();
	//			properties.setMaximumHeapSize( maxsize);
	//		} catch( ParseException e) { 
	//			e.printStackTrace();
	//		}
	
	//		try {
	//			initialHeapSizeText.commitEdit();
	//
	//			int initsize = ((Long)initialHeapSizeText.getValue()).intValue();
	//			properties.setInitialHeapSize( initsize);
	//		} catch( ParseException e) { 
	//			e.printStackTrace();
	//		}
	
			index = lafSelectionBox.getSelectedIndex();
			
			if ( index >= 0 ) {
				UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
				
				if ( index < info.length) {
					properties.setLookAndFeel( info[ index].getClassName());
				}
			}
			
			properties.setPreferPublicIdentifiers( preferPublicIdentifiersBox.isSelected());
			
			// extensions
			//intital list of extensions: initialExtensions
			//current list of extensions: currentExtensions
			//for each extension in the initial vector, search for it in the current vector
			//if found, remove it from the currentVector and do nothing
			//if not found, need to delete it from the properties and current
			//at the end, any extensions that are left in the current vector are new ones
			//so these must be loaded and added to the properties
			
			int initialSize = initialExtensions.size();
			for(int cnt=0;cnt<initialSize;++cnt) {
			    String initialPath = (String)initialExtensions.get(cnt);
			    
			    //see if it exists in the current vector
			    int currentSize = currentExtensions.size();
			    boolean itExists = false;
			    for(int icnt=0;icnt<currentSize;++icnt) {
			        
			        String currentPath = (String)currentExtensions.get(icnt);
			        if(currentPath.equalsIgnoreCase(initialPath)) {
			            itExists = true;
			            
			            //escape the loop
			            icnt=currentSize;
			            
			        }
			        
			    }
			    
			    if(itExists) {
			        //if it exists in the current vector, just remove it from the current
			        removeStringFromVector(initialPath, currentExtensions);
			        
			    }
			    else {
			        //need to delete it from the properties and then delete it from current
			        removeStringFromVector(initialPath, currentExtensions);
			        properties.removeExtension( initialPath);
			    }
			    			    
			    
			}
			
			//now check the current vector
			
			int currentSize = currentExtensions.size();
			if(currentSize>0) {
			    
			    ClassLoader classLoader = getClass().getClassLoader();
			    
			    //need to add these to properties and load them with classloader
			    for(int cnt=0;cnt<currentSize;++cnt) {
			        
			        String currentPath = (String)currentExtensions.get(cnt);
			        
			        if ( classLoader instanceof ExtensionClassLoader) {
						((ExtensionClassLoader)classLoader).addExtension( new File( currentPath));
					}
			
					properties.addExtension( currentPath);
			    }
			}
			
			/*for ( int i = 0; i < removedExtensions.size(); i++) {
				properties.removeExtension( (String)removedExtensions.elementAt( i));
			}
	
			ClassLoader classLoader = getClass().getClassLoader();
	
			for ( int i = 0; i < addedExtensions.size(); i++) {
				String addedExtension = (String)addedExtensions.elementAt( i);
	
				if ( classLoader instanceof ExtensionClassLoader) {
					((ExtensionClassLoader)classLoader).addExtension( new File( addedExtension));
				}
		
				properties.addExtension( addedExtension);
			}*/

			// catalogs
			saveCatalogs();
			/*for ( int i = 0; i < removedCatalogs.size(); i++) {
				properties.removeCatalog( (String)removedCatalogs.elementAt( i));
			}
	
			for ( int i = 0; i < addedCatalogs.size(); i++) {
				properties.addCatalog( (String)addedCatalogs.elementAt( i));
			}*/
			
			prefixNamespaceMappingPanel.save();
			
			// XSLT processor
			if ( xalanRadio.isSelected()) {
				properties.setXSLTProcessor( ConfigurationProperties.XSLT_PROCESSOR_XALAN);
			} else if ( saxon1Radio.isSelected()) {
				properties.setXSLTProcessor( ConfigurationProperties.XSLT_PROCESSOR_SAXON_XSLT1);
			} else if ( saxon2Radio.isSelected()) {
				properties.setXSLTProcessor( ConfigurationProperties.XSLT_PROCESSOR_SAXON_XSLT2);
			}
			
			System.setProperty( "javax.xml.transform.TransformerFactory", properties.getXSLTProcessor());
			
//			properties.setResolveEntities( resolveEntitiesCheck.isSelected());
			properties.setLoadDTDGrammar( loadDTDGrammarCheck.isSelected());

//			XMLUtilities.setResolveEntities( resolveEntitiesCheck.isSelected());
			XMLUtilities.setLoadDTDGrammar( loadDTDGrammarCheck.isSelected());

			// Printing
	
			// printing
			int printIndex = printFontSelectionBox.getSelectedIndex();
			int printSize = Integer.parseInt( (String)printSizeSelectionBox.getSelectedItem());
	
			if ( index != -1) {
				Font font = fonts[ printIndex].deriveFont( (float)printSize);
				printPreferences.setFont( font);
			}
	
			printPreferences.setPrintLineNumbers( printLineNumbers.isSelected());
			printPreferences.setPrintHeader( printHeader.isSelected());
			printPreferences.setWrapText( printWrapText.isSelected());
			
	//		securityPreferences.setKeystoreType( keystoreFileField.getText());
			securityPreferences.setKeystorePassword( keystorePasswordField.getPassword());
			securityPreferences.setKeystoreFile( keystoreFileField.getText());
			securityPreferences.setPrivatekeyPassword( privatekeyPasswordField.getPassword());
			securityPreferences.setPrivatekeyAlias( privatekeyAliasField.getText());
			securityPreferences.setCertificateAlias( certificateAliasField.getText());
			
			// viewer
//			viewerProperties.showAttributes( showAttributes.isSelected());
//			viewerProperties.showNamespaces( showNamespaces.isSelected());
//			viewerProperties.showComments( showComments.isSelected());
//			viewerProperties.showValues( showValues.isSelected());
//			viewerProperties.showInline( showInline.isSelected());
//			viewerProperties.showPI( showPI.isSelected());
			
			
			// set the active configuration
			String configName = (String)keySelectionBox.getSelectedItem();
			keyPreferences.setActiveConfiguration(configName);
			
			// copy all the keymaps from the cache to keypreferences
			Enumeration configs = configMap.keys();
			while (configs.hasMoreElements())
			{
				String name = (String)configs.nextElement();
				
				// remove this configuration, and replace with the one from the temp cache
				keyPreferences.removeMapping(name);
				
				Hashtable keymaps = (Hashtable)configMap.get(name);
				
				Enumeration actions = keymaps.keys();
				while (actions.hasMoreElements())
				{
					String actionName = (String)actions.nextElement();
					KeyMap km = (KeyMap)keymaps.get(actionName);
					keyPreferences.setKeyMap(name,km);
				}
			}
			
			// need to update all the keymappings (keyboard shortcuts)
			keyPreferences.setKeyMappings(parent,configName);
	
			parent.updatePreferences();
			
			properties.save();
	
			super.okButtonPressed();
		}
	}
	
	private void saveCatalogs() {
		// remove previous catalogs...
	    
		Vector catalogs = properties.getCatalogs();
				
		for ( int i = 0; i < catalogs.size(); i++) {
			String catalog = (String)catalogs.elementAt(i);
			properties.removeCatalog(catalog);
		}

		// add current catalogs...
		for ( int i = 0; i < catalogsModel.getSize(); i++) {
			String catalog = (String)catalogsModel.elementAt( i);
			properties.addCatalog(catalog);
		}
				
	}

	protected void cancelButtonPressed() {
		//setVisible( false);
	    super.cancelButtonPressed();
	}

	private JPanel createTextTab() {
		JPanel panel = new JPanel( new BorderLayout());

		xmlEditor = new XmlEditorPane( false);
		xmlEditor.setText( 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<!DOCTYPE root [\n"+
		"\t<!ENTITY % entity \"child2\">\n"+
		"\t<!ELEMENT root (#PCDATA|child1|%entity;)*>\n"+
		"\t\t<!ATTLIST root name NMTOKEN #FIXED \"Fixed\">\n"+
		"\t<!NOTATION name PUBLIC \"whatever\">\n"+
		"]]>\n\n"+
		"<xml:element xmlns:xml=\"namespaceuri\" xml:attribute=\"value\">\n"+
		"\t<child>\n"+
		"\t\t<!-- I can eat glass and it doesn't hurt me -->\n"+
		"\t\t<arabic>\u0623\u0646\u0627\u0642\u0627\u062f\u0631\u0639\u0644\u0649\u0623\u0643\u0644\u0627\u0644\u0632\u062c\u0627\u062c\u0648\u0647\u0630\u0627\u0644\u0627\u064a\u0624\u0644\u0645\u0646\u064a</arabic>\n"+
		"\t\t<chinese>\u6211\u80fd\u541e\u4e0b\u73bb\u7483\u800c\u4e0d\u4f24\u8eab\u4f53\u3002</chinese>\n"+
		"\t\t<greek>\u039c\u03c0\u03bf\u03c1\u03ce\u03bd\u03b1\u03c6\u03ac\u03c9\u03c3\u03c0\u03b1\u03c3\u03bc\u03ad\u03bd\u03b1\u03b3\u03c5\u03b1\u03bb\u03b9\u03ac\u03c7\u03c9\u03c1\u03af\u03c2\u03bd\u03b1\u03c0\u03ac\u03b8\u03c9\u03c4\u03af\u03c0\u03bf\u03c4\u03b1</greek>\n"+
		"\t\t<hebrew>\u05d0\u05e0\u05d9\u05d9\u05db\u05d5\u05dc\u05dc\u05d0\u05db\u05d5\u05dc\u05d6\u05db\u05d5\u05db\u05d9\u05ea\u05d5\u05d6\u05d4\u05dc\u05d0\u05de\u05d6\u05d9\u05e7\u05dc\u05d9</hebrew>\n"+
		"\t\t<japanese>\u79c1\u306f\u30ac\u30e9\u30b9\u3092\u98df\u3079\u3089\u308c\u307e\u3059\u3002\u305d\u308c\u306f\u79c1\u3092\u50b7\u3064\u3051\u307e\u305b\u3093\u3002</japanese>\n"+
		"\t\t<korean>\ub098\ub294\uc720\ub9ac\ub97c\uba39\uc744\uc218\uc788\uc5b4\uc694\uadf8\ub798\ub3c4\uc544\ud504\uc9c0\uc54a\uc544\uc694</korean>\n"+
		"\t\t<russian>\u042f\u043c\u043e\u0433\u0443\u0435\u0441\u0442\u044c\u0441\u0442\u0435\u043a\u043b\u043e\u043e\u043d\u043e\u043c\u043d\u0435\u043d\u0435\u0432\u0440\u0435\u0434\u0438\u0442</russian>\n"+
		"\t\t<thai>\u0e09\u0e31\u0e19\u0e01\u0e34\u0e19\u0e01\u0e23\u0e30\u0e08\u0e01\u0e44\u0e14\u0e49\u0e41\u0e15\u0e48\u0e21\u0e31\u0e19\u0e44\u0e21\u0e48\u0e17\u0e33\u0e43\u0e2b\u0e49\u0e09\u0e31\u0e19\u0e40\u0e08\u0e47\u0e1a</thai>\n"+
		"\t</child>\n"+
		"</xml:element>");
		xmlEditor.setEditable( false);
		xmlEditor.setCaretPosition( 0);
		JScrollPane scroller = new JScrollPane( xmlEditor);
		scroller.getViewport().setPreferredSize( new Dimension( 100, 100));
		
		JPanel editPanel = new JPanel( new BorderLayout());
		editPanel.setBorder( new EmptyBorder( 5, 0, 5, 0));

//		editPanel.add( scroller, BorderLayout.CENTER);

		panel.add( scroller, BorderLayout.CENTER);

		panel.add( createFontsTextPanel(), BorderLayout.SOUTH);
		panel.setBorder( new EmptyBorder( 0, 5, 0, 5));
		
		return panel;
	}

	private JPanel createFontsTextPanel() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 5, 0, 5, 0));
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fonts = env.getAllFonts();

		Vector names = new Vector();
		for ( int i = 0; i < fonts.length; i++) {
			names.addElement( fonts[i].getName());
		}
			
		fontSelectionBox = new JComboBox( names);
		fontSelectionBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				updateEditor();
			}
		});
		sizeSelectionBox = new JComboBox( FONT_SIZES);
		sizeSelectionBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				updateEditor();
			}
		});

		JPanel fontSpecificationPanel = new JPanel( new BorderLayout());
		JPanel fontSelectionPanel = new JPanel( new BorderLayout());
		JPanel fontSizePanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0));
		JLabel fontSizeLabel = new JLabel( "Size:");
		JLabel fontSelectionLabel = new JLabel( "Font:");
		fontSelectionLabel.setBorder( new EmptyBorder( 0, 0, 0, 5));
		fontSelectionPanel.add( fontSelectionLabel, BorderLayout.WEST);
		fontSelectionPanel.add( fontSelectionBox, BorderLayout.CENTER);
		
		fontSizePanel.add( fontSizeLabel);
		fontSizePanel.add( sizeSelectionBox);

		fontSpecificationPanel.add( fontSelectionPanel, BorderLayout.CENTER);
		fontSpecificationPanel.add( fontSizePanel, BorderLayout.EAST);
		fontSpecificationPanel.setBorder( new CompoundBorder( 
											new TitledBorder( "Font Selection"),
											new EmptyBorder( 0, 5, 5, 0)));
		
		panel.add( fontSpecificationPanel, FormLayout.FULL_FILL);
		
		Font defaultFont = TextPreferences.getDefaultFont();
		boolean canbeBold = hasSameWidth( defaultFont, Font.PLAIN, Font.BOLD);
		
		styles = new Vector();
		styles.addElement( new FontStyle( "Element Name", TextPreferences.getFontType( TextPreferences.ELEMENT_NAME), Constants.ELEMENT_NAME, TextPreferences.DEFAULT_ELEMENT_NAME_STYLE, TextPreferences.DEFAULT_ELEMENT_NAME_COLOR));
		
		if (canbeBold)
			styles.addElement( new FontStyle( "Content", TextPreferences.getFontType( TextPreferences.ELEMENT_VALUE), Constants.ELEMENT_VALUE, TextPreferences.DEFAULT_ELEMENT_VALUE_STYLE, TextPreferences.DEFAULT_ELEMENT_VALUE_COLOR));
		else
			styles.addElement( new FontStyle( "Content", TextPreferences.getFontType( TextPreferences.ELEMENT_VALUE), Constants.ELEMENT_VALUE, Font.PLAIN, TextPreferences.DEFAULT_ELEMENT_VALUE_COLOR));

		styles.addElement( new FontStyle( "Attribute Name", TextPreferences.getFontType( TextPreferences.ATTRIBUTE_NAME), Constants.ATTRIBUTE_NAME, TextPreferences.DEFAULT_ATTRIBUTE_NAME_STYLE, TextPreferences.DEFAULT_ATTRIBUTE_NAME_COLOR));
		
		if (canbeBold)
			styles.addElement( new FontStyle( "Attribute Value", TextPreferences.getFontType( TextPreferences.ATTRIBUTE_VALUE), Constants.ATTRIBUTE_VALUE, TextPreferences.DEFAULT_ATTRIBUTE_VALUE_STYLE, TextPreferences.DEFAULT_ATTRIBUTE_VALUE_COLOR));
		else
			styles.addElement( new FontStyle( "Attribute Value", TextPreferences.getFontType( TextPreferences.ATTRIBUTE_VALUE), Constants.ATTRIBUTE_VALUE, Font.PLAIN, TextPreferences.DEFAULT_ATTRIBUTE_VALUE_COLOR));

		styles.addElement( new FontStyle( "Namespace Prefix", TextPreferences.getFontType( TextPreferences.PREFIX), -1, TextPreferences.DEFAULT_PREFIX_STYLE, TextPreferences.DEFAULT_PREFIX_COLOR));
		styles.addElement( new FontStyle( "Namespace Name", TextPreferences.getFontType( TextPreferences.NAMESPACE_NAME), Constants.NAMESPACE_NAME, TextPreferences.DEFAULT_NAMESPACE_NAME_STYLE, TextPreferences.DEFAULT_NAMESPACE_NAME_COLOR));
		
		if (canbeBold)
			styles.addElement( new FontStyle( "Namespace Value", TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE), Constants.NAMESPACE_VALUE, TextPreferences.DEFAULT_NAMESPACE_VALUE_STYLE, TextPreferences.DEFAULT_NAMESPACE_VALUE_COLOR));
		else
			styles.addElement( new FontStyle( "Namespace Value", TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE), Constants.NAMESPACE_VALUE, Font.PLAIN, TextPreferences.DEFAULT_NAMESPACE_VALUE_COLOR));

		styles.addElement( new FontStyle( "Comment", TextPreferences.getFontType( TextPreferences.COMMENT), Constants.COMMENT, TextPreferences.DEFAULT_COMMENT_STYLE, TextPreferences.DEFAULT_COMMENT_COLOR));
		styles.addElement( new FontStyle( "CDATA", TextPreferences.getFontType( TextPreferences.CDATA), Constants.CDATA, TextPreferences.DEFAULT_CDATA_STYLE, TextPreferences.DEFAULT_CDATA_COLOR));
		styles.addElement( new FontStyle( "Special", TextPreferences.getFontType( TextPreferences.SPECIAL), Constants.SPECIAL, TextPreferences.DEFAULT_SPECIAL_STYLE, TextPreferences.DEFAULT_SPECIAL_COLOR));

		styles.addElement( new FontStyle( "PI Target", TextPreferences.getFontType( TextPreferences.PI_TARGET), Constants.PI_TARGET, Font.PLAIN, TextPreferences.PI_TARGET_COLOR));
		styles.addElement( new FontStyle( "PI Name", TextPreferences.getFontType( TextPreferences.PI_NAME), Constants.PI_NAME, Font.PLAIN, TextPreferences.PI_NAME_COLOR));
		styles.addElement( new FontStyle( "PI Value", TextPreferences.getFontType( TextPreferences.PI_VALUE), Constants.PI_VALUE, Font.PLAIN, TextPreferences.PI_VALUE_COLOR));

		styles.addElement( new FontStyle( "DTD: String Value", TextPreferences.getFontType( TextPreferences.STRING_VALUE), Constants.STRING_VALUE, Font.PLAIN, TextPreferences.STRING_VALUE_COLOR));
		styles.addElement( new FontStyle( "DTD: Entity Reference", TextPreferences.getFontType( TextPreferences.ENTITY_VALUE), Constants.ENTITY_VALUE, Font.PLAIN, TextPreferences.ENTITY_VALUE_COLOR));

		styles.addElement( new FontStyle( "DTD: ENTITY Declaration", TextPreferences.getFontType( TextPreferences.ENTITY_DECLARATION), Constants.ENTITY_DECLARATION, Font.PLAIN, TextPreferences.ENTITY_DECLARATION_COLOR));
		styles.addElement( new FontStyle( "DTD: Entity Name", TextPreferences.getFontType( TextPreferences.ENTITY_NAME), Constants.ENTITY_NAME, Font.PLAIN, TextPreferences.ENTITY_NAME_COLOR));
		styles.addElement( new FontStyle( "DTD: Entity Type", TextPreferences.getFontType( TextPreferences.ENTITY_TYPE), Constants.ENTITY_TYPE, Font.PLAIN, TextPreferences.ENTITY_TYPE_COLOR));

		styles.addElement( new FontStyle( "DTD: ATTLIST Declaration", TextPreferences.getFontType( TextPreferences.ATTLIST_DECLARATION), Constants.ATTLIST_DECLARATION, Font.PLAIN, TextPreferences.ATTLIST_DECLARATION_COLOR));
		styles.addElement( new FontStyle( "DTD: Attribute Name", TextPreferences.getFontType( TextPreferences.ATTLIST_NAME), Constants.ATTLIST_NAME, Font.PLAIN, TextPreferences.ATTLIST_NAME_COLOR));
		styles.addElement( new FontStyle( "DTD: Attribute Type", TextPreferences.getFontType( TextPreferences.ATTLIST_TYPE), Constants.ATTLIST_TYPE, Font.PLAIN, TextPreferences.ATTLIST_TYPE_COLOR));
		styles.addElement( new FontStyle( "DTD: Attribute Enumeration", TextPreferences.getFontType( TextPreferences.ATTLIST_VALUE), Constants.ATTLIST_VALUE, Font.PLAIN, TextPreferences.ATTLIST_VALUE_COLOR));
		styles.addElement( new FontStyle( "DTD: Attribute Defaults", TextPreferences.getFontType( TextPreferences.ATTLIST_DEFAULT), Constants.ATTLIST_DEFAULT, Font.PLAIN, TextPreferences.ATTLIST_DEFAULT_COLOR));
	
		styles.addElement( new FontStyle( "DTD: ELEMENT Declaration", TextPreferences.getFontType( TextPreferences.ELEMENT_DECLARATION), Constants.ELEMENT_DECLARATION, Font.PLAIN, TextPreferences.ELEMENT_DECLARATION_COLOR));
		styles.addElement( new FontStyle( "DTD: Element Name", TextPreferences.getFontType( TextPreferences.ELEMENT_DECLARATION_NAME), Constants.ELEMENT_DECLARATION_NAME, Font.PLAIN, TextPreferences.ELEMENT_DECLARATION_NAME_COLOR));
		styles.addElement( new FontStyle( "DTD: Element Type", TextPreferences.getFontType( TextPreferences.ELEMENT_DECLARATION_TYPE), Constants.ELEMENT_DECLARATION_TYPE, Font.PLAIN, TextPreferences.ELEMENT_DECLARATION_TYPE_COLOR));
		styles.addElement( new FontStyle( "DTD: #PCDATA", TextPreferences.getFontType( TextPreferences.ELEMENT_DECLARATION_PCDATA), Constants.ELEMENT_DECLARATION_PCDATA, Font.PLAIN, TextPreferences.ELEMENT_DECLARATION_PCDATA_COLOR));
		styles.addElement( new FontStyle( "DTD: Element Operator", TextPreferences.getFontType( TextPreferences.ELEMENT_DECLARATION_OPERATOR), Constants.ELEMENT_DECLARATION_OPERATOR, Font.PLAIN, TextPreferences.ELEMENT_DECLARATION_OPERATOR_COLOR));

		styles.addElement( new FontStyle( "DTD: NOTATION Declaration", TextPreferences.getFontType( TextPreferences.NOTATION_DECLARATION), Constants.NOTATION_DECLARATION, Font.PLAIN, TextPreferences.NOTATION_DECLARATION_COLOR));
		styles.addElement( new FontStyle( "DTD: Notation Name", TextPreferences.getFontType( TextPreferences.NOTATION_DECLARATION_NAME), Constants.NOTATION_DECLARATION_NAME, Font.PLAIN, TextPreferences.NOTATION_DECLARATION_NAME_COLOR));
		styles.addElement( new FontStyle( "DTD: Notation Type", TextPreferences.getFontType( TextPreferences.NOTATION_DECLARATION_TYPE), Constants.NOTATION_DECLARATION_TYPE, Font.PLAIN, TextPreferences.NOTATION_DECLARATION_TYPE_COLOR));

		styles.addElement( new FontStyle( "DTD: DOCTYPE Declaration", TextPreferences.getFontType( TextPreferences.DOCTYPE_DECLARATION), Constants.DOCTYPE_DECLARATION, Font.PLAIN, TextPreferences.DOCTYPE_DECLARATION_COLOR));
		styles.addElement( new FontStyle( "DTD: Doctype Type", TextPreferences.getFontType( TextPreferences.DOCTYPE_DECLARATION_TYPE), Constants.DOCTYPE_DECLARATION_TYPE, Font.PLAIN, TextPreferences.DOCTYPE_DECLARATION_TYPE_COLOR));

		stylePanel = new FontStylePanel( styles);

		JPanel typesPanel = new JPanel( new BorderLayout());
		typesPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Styles"),
									new EmptyBorder( 0, 5, 5, 5)));
		typesPanel.add( stylePanel, BorderLayout.CENTER);
		panel.add( typesPanel, FormLayout.FULL_FILL);
		
		JPanel tabPanel = new JPanel( new BorderLayout());
		tabPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Tab"),
									new EmptyBorder( 0, 5, 5, 0)));

		JPanel tabSizePanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0));
		tabSizeBox = new JComboBox( SPACES);
		tabSizeBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				updateEditor();
			}
		});
		tabSizePanel.add( new JLabel( "Tab Size:"));
		tabSizePanel.add( tabSizeBox);

		convertTabBox = new JCheckBox( "Convert to Spaces");
//		convertTabBox.setFont( convertTabBox.getFont().deriveFont( Font.PLAIN));
		tabPanel.add( convertTabBox, BorderLayout.WEST);
		tabPanel.add( tabSizePanel, BorderLayout.EAST);

		panel.add( tabPanel, FormLayout.FULL_FILL);
		
		JButton defaultButton = new JButton( "Set Default");
		defaultButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				setDefault();
			}
		});
		antialiasingBox	= new JCheckBox( "Antialiase Text");
		antialiasingBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				updateEditor();
			}
		});

		JPanel buttonPanel = new JPanel( new BorderLayout());
		buttonPanel.add( defaultButton, BorderLayout.EAST);
		buttonPanel.add( antialiasingBox, BorderLayout.WEST);

		panel.add( buttonPanel, FormLayout.FULL_FILL);

		return panel;
	}

	private JPanel createPrintTab() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		fonts = env.getAllFonts();

		Vector names = new Vector();
		for ( int i = 0; i < fonts.length; i++) {
			names.addElement( fonts[i].getName());
		}
			
		printFontSelectionBox = new JComboBox( names);
		printSizeSelectionBox = new JComboBox( FONT_SIZES);

		JPanel fontSpecificationPanel = new JPanel( new BorderLayout());
		JPanel fontSelectionPanel = new JPanel( new BorderLayout());
		JPanel fontSizePanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 0));
		JLabel fontSizeLabel = new JLabel( "Size:");
		JLabel fontSelectionLabel = new JLabel( "Font:");
		fontSelectionLabel.setBorder( new EmptyBorder( 0, 0, 0, 5));
		fontSelectionPanel.add( fontSelectionLabel, BorderLayout.WEST);
		fontSelectionPanel.add( printFontSelectionBox, BorderLayout.CENTER);
		
		fontSizePanel.add( fontSizeLabel);
		fontSizePanel.add( printSizeSelectionBox);

		fontSpecificationPanel.add( fontSelectionPanel, BorderLayout.CENTER);
		fontSpecificationPanel.add( fontSizePanel, BorderLayout.EAST);
		fontSpecificationPanel.setBorder( new CompoundBorder( 
											new TitledBorder( "Font Selection"),
											new EmptyBorder( 0, 5, 5, 0)));
		
		panel.add( fontSpecificationPanel, FormLayout.FULL_FILL);
		
		// print boxes
		printHeader			= new JCheckBox( "Show Header");
		printLineNumbers	= new JCheckBox( "Show Line Numbers");
		printWrapText		= new JCheckBox( "Wrap Text");
		
		JPanel printPanel 	= new JPanel( new FormLayout( 0, 5));
		printPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Text"),
									new EmptyBorder( 0, 5, 5, 0)));

		printPanel.add( printHeader, FormLayout.FULL);
		printPanel.add( printLineNumbers, FormLayout.FULL);
		printPanel.add( printWrapText, FormLayout.FULL);
		
		panel.add( printPanel, FormLayout.FULL_FILL);

		return panel;
	}

	private void updateEditor() {
		int index = fontSelectionBox.getSelectedIndex();
		int size = Integer.parseInt( (String)sizeSelectionBox.getSelectedItem());
		int tab = Integer.parseInt( (String)tabSizeBox.getSelectedItem());
		
		xmlEditor.setTabSize( tab);
		xmlEditor.setAntialiasing( antialiasingBox.isSelected());
		
		if ( index != -1) {
			Font font = fonts[index];
			font = font.deriveFont( (float)size);

			boolean canbeBold = hasSameWidth( font, Font.PLAIN, Font.BOLD);
			boolean canbeItalic = hasSameWidth( font, Font.PLAIN, Font.ITALIC);
			stylePanel.reset( canbeItalic, canbeBold);
			
			for ( int i = 0; i < styles.size(); i++) {
				FontStyle style = (FontStyle)styles.elementAt(i);

				if ( !canbeItalic && style.isItalic()) {
					style.setStyle( Font.PLAIN);
				}

				if ( !canbeBold && style.isBold()) {
					style.setStyle( Font.PLAIN);
				}

				int id = style.getId();
				
				if ( id != -1) {
					xmlEditor.setAttributes( id, style.getColor(), style.getStyle());
				} else {
					xmlEditor.setAttributes( Constants.NAMESPACE_PREFIX, style.getColor(), style.getStyle());
					xmlEditor.setAttributes( Constants.ELEMENT_PREFIX, style.getColor(), style.getStyle());
					xmlEditor.setAttributes( Constants.ATTRIBUTE_PREFIX, style.getColor(), style.getStyle());
				}
			}
			
			xmlEditor.setFont( font);
		}
	}

	private JComponent createViewsTab() {
		Box panel = Box.createVerticalBox();
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		// General stuff
		JPanel generalPanel 	= new JPanel( new FormLayout( 0, 5));
		generalPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "General"),
									new EmptyBorder( 0, 5, 5, 0)));
		autoSyncSelectionBox	= new JCheckBox( "Synchronise Selection between Views");
		generalPanel.add( autoSyncSelectionBox, FormLayout.FULL);
		multipleDocumentsBox	= new JCheckBox( "Open multiple occurrences of the same Document");
		generalPanel.add( multipleDocumentsBox, FormLayout.FULL);
		scrollDocumentTabsBox	= new JCheckBox( "Scroll Document Tabs");
		generalPanel.add( scrollDocumentTabsBox, FormLayout.FULL);
		panel.add( generalPanel);

		JPanel typesPanel 	= new JPanel( new FormLayout( 0, 5));
		typesPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "XML Types"),
									new EmptyBorder( 0, 5, 5, 0)));

		checkTypeOnOpeningBox	= new JCheckBox( "Check for Type opening Document");
		checkTypeOnOpeningBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				promptCreateTypeOnOpeningBox.setEnabled( checkTypeOnOpeningBox.isSelected());
			}
		});
		typesPanel.add( checkTypeOnOpeningBox, FormLayout.FULL);
		
		promptCreateTypeOnOpeningBox	= new JCheckBox( "Prompt to create a Type when no Type found");
		promptCreateTypeOnOpeningBox.setEnabled( false);

		JPanel promptPanel = new JPanel( new BorderLayout());
		promptPanel.setBorder( new EmptyBorder( 0, 20, 0, 0));
		promptPanel.add( promptCreateTypeOnOpeningBox, BorderLayout.CENTER);
		typesPanel.add( promptPanel, FormLayout.FULL);

		validateOnOpeningBox	= new JCheckBox( "Validate Document on opening");
		typesPanel.add( validateOnOpeningBox, FormLayout.FULL);
		useInternalSchemaBox	= new JCheckBox( "Set Schema or DTD defined in Document");
		typesPanel.add( useInternalSchemaBox, FormLayout.FULL);
		
		panel.add( typesPanel);

		JPanel scenariosPanel 	= new JPanel( new FormLayout( 0, 5));
		scenariosPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Scenario Execution"),
									new EmptyBorder( 0, 5, 5, 0)));

		hideScenarioExecutionDialogWhenCompleteBox	= new JCheckBox( "Hide Scenario Execution Dialog when complete");
		scenariosPanel.add( hideScenarioExecutionDialogWhenCompleteBox, FormLayout.FULL);

		panel.add( scenariosPanel);

		JPanel xincludePanel 	= new JPanel( new FormLayout( 0, 5));
		xincludePanel.setBorder( new CompoundBorder( 
									new TitledBorder( "XInclude"),
									new EmptyBorder( 0, 5, 5, 0)));

		openXIncludeInNewDocumentBox	= new JCheckBox( "Open Resolve XInclude in New Document");
		xincludePanel.add( openXIncludeInNewDocumentBox, FormLayout.FULL);

		panel.add( xincludePanel);

		JPanel xpathPanel 	= new JPanel( new FormLayout( 0, 5));
		xpathPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "XPath Editor"),
									new EmptyBorder( 0, 5, 5, 0)));

		uniqueXPathBox			= new JCheckBox( "Generate Unique XPath");
		xpathPanel.add( uniqueXPathBox, FormLayout.FULL);

		panel.add( xpathPanel);

		//		attributesNewLineBox	= new JCheckBox( "Place Attribute/Namespace on a new Line");
//		generalPanel.add( attributesNewLineBox, FormLayout.FULL);

		JPanel projectPanel 	= new JPanel( new FormLayout( 0, 5));
		projectPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Project"),
									new EmptyBorder( 0, 5, 5, 0)));

		showFullPathBox	= new JCheckBox( "Show full Path for Documents");
		projectPanel.add( showFullPathBox, FormLayout.FULL);

		panel.add( projectPanel);

		return panel;
	}

	private JPanel createFormatTab() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JPanel wrappingPanel 	= new JPanel( new FormLayout( 0, 5));
		wrappingPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Wrapping"),
									new EmptyBorder( 0, 5, 5, 5)));

		wrapTextCheck = new JCheckBox( "Wrap Text");
		wrapTextCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				wrappingColumnField.setEnabled( wrapTextCheck.isSelected());
//				wrapWhileTypingCheck.setEnabled( wrapTextCheck.isSelected());
			}
		});

		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setMinimumIntegerDigits( 0);
		wrappingColumnField = new JFormattedTextField( format);
		wrappingColumnField.setPreferredSize( new Dimension( 50, wrappingColumnField.getPreferredSize().height));
		wrappingColumnField.setHorizontalAlignment( JTextField.RIGHT);
		JLabel wrappingColumnLabel = new JLabel( "Wrapping Column:");
		
		JPanel wrappingColumnPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		wrappingColumnPanel.add( wrappingColumnLabel);
		wrappingColumnPanel.add( Box.createHorizontalStrut( 5));
		wrappingColumnPanel.add( wrappingColumnField);
		
		wrappingPanel.add( wrapTextCheck, FormLayout.LEFT);
		wrappingPanel.add( wrappingColumnPanel, FormLayout.RIGHT_FILL);
		
//		wrapWhileTypingCheck = new JCheckBox( "Wrap Text While Typing");
//		JPanel wrapWhileTypingPanel 	= new JPanel( new FormLayout( 0, 5));
//		wrapWhileTypingPanel.setBorder( new EmptyBorder( 0, 20, 5, 0));
//		wrapWhileTypingPanel.add( wrapWhileTypingCheck, FormLayout.FULL);
//
//		wrappingPanel.add( wrapWhileTypingPanel, FormLayout.FULL_FILL);

		wrappingColumnField.setEnabled( false);
//		wrapWhileTypingCheck.setEnabled( false);

		panel.add( wrappingPanel, FormLayout.FULL_FILL);
	
		JPanel formattersPanel = new JPanel( new FormLayout( 0, 5));
		formattersPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Formatter"),
									new EmptyBorder( 0, 5, 5, 5)));

		customFormatterRadio	= new JRadioButton( "Custom Formatter");
		customFormatterRadio.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				padTextCheck.setEnabled( customFormatterRadio.isSelected());
				indentCheck.setEnabled( customFormatterRadio.isSelected());
				newlinesCheck.setEnabled( customFormatterRadio.isSelected());
				stripWhitespaceCheck.setEnabled( customFormatterRadio.isSelected());
				preserveMixedContentCheck.setEnabled( customFormatterRadio.isSelected());
			}
		});
		formattersPanel.add( customFormatterRadio, FormLayout.FULL);
		
		JPanel customFormatterPanel = new JPanel( new FormLayout( 0, 5));
		customFormatterPanel.setBorder( new EmptyBorder( 5, 20, 5, 5));

		indentCheck					= new JCheckBox( "Indent");
		padTextCheck				= new JCheckBox( "Pad Text");
		newlinesCheck				= new JCheckBox( "Newlines");
		stripWhitespaceCheck		= new JCheckBox( "Trim Text");
		preserveMixedContentCheck	= new JCheckBox( "Preserve Mixed Content");
		
		customFormatterPanel.add( indentCheck, FormLayout.FULL);
		customFormatterPanel.add( padTextCheck, FormLayout.FULL);
		customFormatterPanel.add( newlinesCheck, FormLayout.FULL);
		customFormatterPanel.add( stripWhitespaceCheck, FormLayout.FULL);
		customFormatterPanel.add( preserveMixedContentCheck, FormLayout.FULL);

		indentCheck.setEnabled( false);
		padTextCheck.setEnabled( false);
		newlinesCheck.setEnabled( false);
		stripWhitespaceCheck.setEnabled( false);
		preserveMixedContentCheck.setEnabled( false);

		formattersPanel.add( customFormatterPanel, FormLayout.FULL_FILL);

		compactFormatterRadio	= new JRadioButton( "Compact Formatter");
		formattersPanel.add( compactFormatterRadio, FormLayout.FULL);

		standardFormatterRadio	= new JRadioButton( "Standard Formatter");
		formattersPanel.add( standardFormatterRadio, FormLayout.FULL);
		
		ButtonGroup group = new ButtonGroup();
		group.add( customFormatterRadio);
		group.add( compactFormatterRadio);
		group.add( standardFormatterRadio);
	
		panel.add( formattersPanel, FormLayout.FULL_FILL);

		return panel;
	}

	private JPanel createSecurityTab() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JPanel privatekeyPanel 	= new JPanel( new FormLayout( 5, 5));
		privatekeyPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Private Key Details"),
									new EmptyBorder( 0, 5, 5, 5)));
		
		keystoreTypeBox = new JComboBox();
		keystoreTypeBox.addItem( "JKS");
		keystoreTypeBox.setSelectedIndex(0);

		privatekeyPanel.add( new JLabel("Keystore Type:"), FormLayout.LEFT);
		privatekeyPanel.add( keystoreTypeBox, FormLayout.RIGHT);
		
		JPanel keystoreFilePanel = new JPanel( new BorderLayout());

		keystoreFileField = new JTextField();

		JButton keystoreFileButton = new JButton( "...");
		keystoreFileButton.setMargin( new Insets( 0, 10, 0, 10));
		keystoreFileButton.setPreferredSize( new Dimension( keystoreFileButton.getPreferredSize().width, keystoreFileField.getPreferredSize().height));
		keystoreFileButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				keystoreFileButtonPressed();
			}
		});
		
		keystoreFilePanel.add( keystoreFileField, BorderLayout.CENTER);
		keystoreFilePanel.add( keystoreFileButton, BorderLayout.EAST);

		privatekeyPanel.add( new JLabel("Keystore File:"), FormLayout.LEFT);
		privatekeyPanel.add( keystoreFilePanel, FormLayout.RIGHT_FILL);

		keystorePasswordField = new JPasswordField();
		privatekeyPanel.add( new JLabel("Keystore Password:"), FormLayout.LEFT);
		privatekeyPanel.add( keystorePasswordField, FormLayout.RIGHT_FILL);

		privatekeyPanel.add( getSeparator(), FormLayout.FULL);

		privatekeyAliasField = new JTextField();
		privatekeyPanel.add( new JLabel("Private Key Alias:"), FormLayout.LEFT);
		privatekeyPanel.add( privatekeyAliasField, FormLayout.RIGHT_FILL);

		privatekeyPasswordField = new JPasswordField();
		privatekeyPanel.add( new JLabel("Private Key Password:"), FormLayout.LEFT);
		privatekeyPanel.add( privatekeyPasswordField, FormLayout.RIGHT_FILL);

		privatekeyPanel.add( getSeparator(), FormLayout.FULL);

		certificateAliasField = new JTextField();
		privatekeyPanel.add( new JLabel("Certificate Alias:"), FormLayout.LEFT);
		privatekeyPanel.add( certificateAliasField, FormLayout.RIGHT_FILL);
		
		panel.add( privatekeyPanel, FormLayout.FULL_FILL);

		return panel;
	}

	private JPanel createSystemTab() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

//		JPanel heapPanel 	= new JPanel( new FormLayout( 5, 5));
//		heapPanel.setBorder( new CompoundBorder( 
//									new TitledBorder( "Java Heap Size"),
//									new EmptyBorder( 0, 5, 5, 5)));
//
//		NumberFormat format = NumberFormat.getIntegerInstance();
//		format.setMinimumIntegerDigits( 0);
//
//		initialHeapSizeText	= new JFormattedTextField( format);
//		initialHeapSizeText.setPreferredSize( new Dimension( 50, initialHeapSizeText.getPreferredSize().height));
//		initialHeapSizeText.setHorizontalAlignment( JTextField.RIGHT);
//		
//		JPanel initialHeapPanel = new JPanel( new BorderLayout());
//		initialHeapPanel.add( initialHeapSizeText, BorderLayout.CENTER);
//		initialHeapPanel.add( new JLabel(" (MB)"), BorderLayout.EAST);
//		
//		heapPanel.add( new JLabel("Initial Heap Size (-Xms):"), FormLayout.LEFT);
//		heapPanel.add( initialHeapPanel, FormLayout.RIGHT);
//
//		maximumHeapSizeText	= new JFormattedTextField( format);
//		maximumHeapSizeText.setPreferredSize( new Dimension( 50, maximumHeapSizeText.getPreferredSize().height));
//		maximumHeapSizeText.setHorizontalAlignment( JTextField.RIGHT);
//		
//		JPanel maximumHeapPanel = new JPanel( new BorderLayout());
//		maximumHeapPanel.add( maximumHeapSizeText, BorderLayout.CENTER);
//		maximumHeapPanel.add( new JLabel(" (MB)"), BorderLayout.EAST);
//
//		heapPanel.add( new JLabel("Maximum Heap Size (-Xmx):"), FormLayout.LEFT);
//		heapPanel.add( maximumHeapPanel, FormLayout.RIGHT);
//
//		panel.add( heapPanel, FormLayout.FULL_FILL);

		JPanel proxyPanel 	= new JPanel( new FormLayout( 5, 5));
		proxyPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Proxy Configuration"),
									new EmptyBorder( 0, 5, 5, 5)));

		useProxyCheck	= new JCheckBox( "Use Proxy");
		useProxyCheck.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				proxyHostField.setEnabled( useProxyCheck.isSelected());
				proxyHostLabel.setEnabled( useProxyCheck.isSelected());
				proxyPortField.setEnabled( useProxyCheck.isSelected());
				proxyPortLabel.setEnabled( useProxyCheck.isSelected());
			}
		});

		proxyPanel.add( useProxyCheck, FormLayout.FULL);

		JPanel proxyFieldsPanel = new JPanel( new FormLayout( 5, 5));
		proxyFieldsPanel.setBorder( new EmptyBorder( 5, 20, 5, 0));

		proxyHostField	= new JTextField();
		proxyHostLabel = new JLabel("Host Address:");
		proxyFieldsPanel.add( proxyHostLabel, FormLayout.LEFT);
		proxyFieldsPanel.add( proxyHostField, FormLayout.RIGHT_FILL);

		proxyPortField	= new JTextField();
		proxyPortLabel = new JLabel("Port Number:");
		proxyFieldsPanel.add( proxyPortLabel, FormLayout.LEFT);
		proxyFieldsPanel.add( proxyPortField, FormLayout.RIGHT_FILL);
		
		proxyPanel.add( proxyFieldsPanel, FormLayout.FULL_FILL);

		proxyHostLabel.setEnabled( false);
		proxyPortLabel.setEnabled( false);
		proxyHostField.setEnabled( false);
		proxyPortField.setEnabled( false);

		panel.add( proxyPanel, FormLayout.FULL_FILL);
		
		String osName = System.getProperty( "os.name");
		
		if ( !osName.startsWith( "Mac OS") && !osName.startsWith("Windows")) {
			// this must be Linux/Unix
			JPanel browserPanel 	= new JPanel( new FormLayout( 5, 5));
			browserPanel.setBorder( new CompoundBorder( 
										new TitledBorder( "Browser"),
										new EmptyBorder( 0, 5, 5, 5)));
	
			browserField	= new JTextField();
			browserPanel.add( new JLabel("Browser:"), FormLayout.LEFT);
			browserPanel.add( browserField, FormLayout.RIGHT_FILL);
	
			panel.add( browserPanel, FormLayout.FULL_FILL);
		}

		JPanel extensionsPanel 	= new JPanel( new BorderLayout());
		extensionsPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Extensions"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		extensionList = new JList();
		
		//extensionList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		extensionList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		extensionList.setVisibleRowCount(5);
		extensionsModel = new DefaultListModel();
		extensionList.setModel( extensionsModel);
		extensionList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = extensionList.getSelectedIndex();
                if(selected>-1) {
                    extensionList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 2, 0, 0, 0));

		JButton addDirButton = new JButton("Add Dir ...");
		addDirButton.setFont( addDirButton.getFont().deriveFont( Font.PLAIN));
		addDirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				JDirectoryChooser chooser = FileUtilities.getDirectoryChooser();

				if ( extensionsModel.getSize() > 0) {
					chooser.setCurrentDirectory( new File( (String)extensionsModel.lastElement()));
				}
				
				int result = chooser.showOpenDialog( parent);
				
				if ( result == JDirectoryChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();

					if ( !file.isDirectory()) {
						file = file.getParentFile();
					}
					
					String path = file.getPath();

					if ( extensionsModel.indexOf( path) == -1) {
						extensionsModel.addElement( path);
						//addedExtensions.addElement( path);
						currentExtensions.addElement(path);
					}
				}
			}
		});
		buttonPanel.add( addDirButton);

		JButton addJarButton = new JButton("Add Jar ...");
		addJarButton.setFont( addJarButton.getFont().deriveFont( Font.PLAIN));
		addJarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				JFileChooser chooser = FileUtilities.getJarChooser();

				if ( extensionsModel.getSize() > 0) {
					chooser.setCurrentDirectory( new File( (String)extensionsModel.lastElement()));
				}
				
				int result = chooser.showOpenDialog( parent);
				
				if ( result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					String path = file.getPath();

					if ( !file.isDirectory() && extensionsModel.indexOf( path) == -1) {
						extensionsModel.addElement( path);
						//addedExtensions.addElement( path);
						currentExtensions.addElement(path);
					}
				}
			}
		});
		buttonPanel.add( addJarButton);
		
		
		JButton deleteButton = new JButton( "Delete");
		deleteButton.setFont( deleteButton.getFont().deriveFont( Font.PLAIN));
		deleteButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
			    
//			  *******************START new code*********************************
			    
			    //get the selected objects
			    Object[] selectedObjects = extensionList.getSelectedValues();
			    
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
				            message += (String)selectedObjects[cnt];
				            
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
				                extensionsModel.removeElement( selectedObjects[cnt]);
				                if(!removeStringFromVector((String)selectedObjects[cnt], currentExtensions)) {
								    MessageHandler.showError(parent,"Error Removing Extension","Error");
								}				                
			                	deleteAll=true;
			                } 
			                //user choose to delete this object, remove it from the list
			                else if(questionResult==JOptionPane.YES_OPTION) {
			                    extensionsModel.removeElement( selectedObjects[cnt]);
			                    if(!removeStringFromVector((String)selectedObjects[cnt], currentExtensions)) {
								    MessageHandler.showError(parent,"Error Removing Extension","Error");
								}
							}
				            
			            } else {
			                extensionsModel.removeElement( selectedObjects[cnt]);
			                if(!removeStringFromVector((String)selectedObjects[cnt], currentExtensions)) {
							    MessageHandler.showError(parent,"Error Removing Extension","Error");
							}
			            }
			        } //end for loop
			    } //end if(selectedObjects.length>1) {
			    
			    	    
			    
			    //*******************END new code*********************************
			    			    
			}
		});
		buttonPanel.add( deleteButton);

		extensionsPanel.add( new JScrollPane( extensionList), BorderLayout.CENTER);
		extensionsPanel.add( buttonPanel, BorderLayout.SOUTH);

		panel.add( extensionsPanel, FormLayout.FULL_FILL);
		
		/*JPanel keyPanel 	= new JPanel( new FormLayout( 10, 5));
		keyPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Keyboard Shortcuts"),
									new EmptyBorder( 5, 5, 10, 5)));

		keySelectionBox	= new JComboBox();

		keyPanel.add( new JLabel( "Active Configuration:"), FormLayout.LEFT);
		keyPanel.add( keySelectionBox, FormLayout.RIGHT_FILL);

		panel.add( keyPanel, FormLayout.FULL_FILL);*/
		

		JPanel lafPanel 	= new JPanel( new FormLayout( 10, 5));
		lafPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Look And Feel"),
									new EmptyBorder( 0, 5, 5, 5)));

		QLabel label = new QLabel();
		label.setLines( 2);
		label.setForeground( Color.red);
		
		label.setText( "A restart of the application is required before changes to the Look and Feel take effect.");
		label.setBorder( new EmptyBorder( 10, 10, 10, 10));
		
		lafPanel.add( label, FormLayout.FULL_FILL);

		lafSelectionBox	= new JComboBox();
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		
		for ( int i = 0; i < info.length; i++) {
			lafSelectionBox.addItem( info[i].getName());
		}

		lafPanel.add( new JLabel( "Look and Feel:"), FormLayout.LEFT);
		lafPanel.add( lafSelectionBox, FormLayout.RIGHT_FILL);

		panel.add( lafPanel, FormLayout.FULL_FILL);

		return panel;
	}
	
	private JPanel createKeysTab() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 10, 5, 5, 5));
		
		JPanel keyPanel 	= new JPanel( new FormLayout( 52, 5));
		keyPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Active Configuration"),
									new EmptyBorder( 5, 5, 10, 5)));

		keySelectionBox	= new JComboBox();

		keyPanel.add( new JLabel( "Name:"), FormLayout.LEFT);
		keyPanel.add( keySelectionBox, FormLayout.RIGHT_FILL);
		keySelectionBox.addItemListener(new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				
				String configName = (String)keySelectionBox.getSelectedItem();
				if (configName == null) 
				{
					return;
				}
				
				int previousSelected = actionNames.getSelectedIndex();
				if (previousSelected == -1)
				{
					previousSelected = 0;
				}
					
				actionNames.removeAllItems();
				
				
				// get the keymap from the cache
				keyMaps = (Hashtable)configMap.get(configName);
				if (keyMaps == null)
				{
					// not in the cache so get from file
					keyMaps = keyPreferences.getKeyMaps(configName);
					
					// now put in cache for next time
					configMap.put(configName,keyMaps);
				}
				
				// use getSortedActionNames utility method
				Vector sortedActionNames = keyPreferences.getSortedActionNames(keyMaps);
				
				for (int i=0;i<sortedActionNames.size();i++)
				{
					String name = (String)sortedActionNames.get(i);
					actionNames.addItem(name);
				}
				
				if(actionNames.getItemCount() > 0) {
					actionNames.setSelectedIndex(previousSelected);
				}
				else {
					System.out.println("PreferencesDialog::keySelectionBox::itemStateChanged:: actionNames has no items");
				}
				
				return;
			}
		});
		
		panel.add( keyPanel, FormLayout.FULL_FILL);
		
		JPanel actionPanel 	= new JPanel( new FormLayout( 5, 5));
		actionPanel.setBorder( new CompoundBorder( 
									new TitledBorder("Command"),
									new EmptyBorder( 5, 5, 10, 5)));
		
		actionNames = new JComboBox();
		actionNames.setRenderer(new CommandNamesCellRenderer());
		actionNames.addItemListener(new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				
				String name = (String)actionNames.getSelectedItem();
				if (name == null)
				{
					return;
				}
				
				KeyMap keyMap = (KeyMap)keyMaps.get(name);
				
				actionDescription.setText(keyMap.getDescription());
				
				String keys = keyPreferences.getKeySequence(keyMap.getKeystrokes());
				keySequence.setText(keys);
				
				return;
			}
		});
		
		JLabel nameLabel = new JLabel( "Name:");
		actionPanel.add( nameLabel, FormLayout.LEFT);
		actionPanel.add(actionNames,FormLayout.RIGHT_FILL);
		
		actionDescription = new JTextArea();
		keySequence = new JTextField();
		actionDescription.setBackground(actionPanel.getBackground());
		actionDescription.setBorder(new MatteBorder(1,1,1,1,new Color( 153, 153, 153)));
		actionDescription.setPreferredSize( new Dimension( nameLabel.getPreferredSize().width, (nameLabel.getPreferredSize().height*3)));
		actionDescription.setEditable(false);
		actionDescription.setLineWrap(true);
		
		JPanel descriptionPanel = new JPanel();
		actionPanel.add( new JLabel( "Description:"), new FormConstraints(FormConstraints.LEFT,FormConstraints.LEFT,FormConstraints.TOP));
		actionPanel.add(actionDescription,FormLayout.RIGHT_FILL);
		
		keySequence.setEditable(false);
		actionPanel.add( new JLabel( "Key Sequence:"), FormLayout.LEFT);
		actionPanel.add(keySequence,FormLayout.RIGHT_FILL);
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 2, 0, 0, 0));
		
		JButton editButton = new JButton("Edit..");
		editButton.setFont( editButton.getFont().deriveFont( Font.PLAIN));
		editButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				editKeyMapButtonPressed();
			}
		});
		
		
		JButton deleteButton = new JButton("Delete");
		deleteButton.setFont( deleteButton.getFont().deriveFont( Font.PLAIN));
		deleteButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				String configName = (String)keySelectionBox.getSelectedItem();
				if (configName == null) 
				{
					return;
				}
				
				String actionName = (String)actionNames.getSelectedItem();
				if (actionName == null)
				{
					return;
				}
				
				String description = actionDescription.getText();
				if (description == null)
				{
					description = "";
				}
				
				Keystroke ks = new Keystroke(null,null);
				KeyMap keymap = new KeyMap(actionName,description,ks);
				
				keyMaps.put(actionName,keymap);
				
				// update cache
				configMap.put(configName,keyMaps);
				
				// blank the key sequence
				keySequence.setText("");
			}
		});
		
		buttonPanel.add(editButton);
		buttonPanel.add(deleteButton);
		
		actionPanel.add(buttonPanel, FormLayout.RIGHT_FILL);
		
		panel.add(actionPanel, FormLayout.FULL_FILL);

		return panel;
	}
	
	private void editKeyMapButtonPressed()
	{
		String configName = (String)keySelectionBox.getSelectedItem();
		if (configName == null) 
		{
			return;
		}
		
		String actionName = (String)actionNames.getSelectedItem();
		if (actionName == null)
		{
			return;
		}
		
		String description = actionDescription.getText();
		if (description == null)
		{
			description = "";
		}
		
		String sequence = keySequence.getText();
		if (description == null)
		{
			sequence = "";
		}
		
		KeyMapDialog dialog = getKeyMapDialog();
		dialog.show(configName,actionName,description,sequence,keyMaps);
		
		if (!dialog.isCancelled()) 
		{	
			// update cache
			configMap.put(configName,keyMaps);
			
			// set the key sequence
			keySequence.setText(dialog.getKeySequence());
		}
	}
	
	private KeyMapDialog getKeyMapDialog() 
	{
		if (keymapDialog == null) {
			keymapDialog = new KeyMapDialog(parent);
		}
		
		return keymapDialog;
	}

	private JPanel createXMLTab() {
		JPanel panel = new JPanel( new FormLayout( 0, 5));
		panel.setBorder( new EmptyBorder( 5, 5, 5, 5));

		JPanel processorsPanel = new JPanel( new FormLayout( 5, 5));
		processorsPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Default XSLT Processor"),
									new EmptyBorder( 0, 5, 5, 5)));
		ButtonGroup group = new ButtonGroup();

		xalanRadio = new JRadioButton( "Xalan");
		group.add( xalanRadio);
		processorsPanel.add( xalanRadio, FormLayout.FULL);

		saxon1Radio = new JRadioButton( "Saxon (XSLT 1.*)");
		group.add( saxon1Radio);
		processorsPanel.add( saxon1Radio, FormLayout.FULL);

		saxon2Radio = new JRadioButton( "Saxon (XSLT 2.0)");
		group.add( saxon2Radio);
		processorsPanel.add( saxon2Radio, FormLayout.FULL);

		//		JLabel warning = new JLabel( "* Should not be used in a production environment.");
//		warning.setFont( warning.getFont().deriveFont( Font.PLAIN + Font.ITALIC));
//		processorsPanel.add( warning, FormLayout.FULL);
		
		panel.add( processorsPanel, FormLayout.FULL_FILL);
		
		JPanel advancedPanel = new JPanel( new FormLayout( 5, 5));
		advancedPanel.setBorder( new CompoundBorder( 
				new TitledBorder( "Non Validating Feature"),
				new EmptyBorder( 0, 5, 5, 5)));

//		resolveEntitiesCheck = new JCheckBox( "Always try to resolve Entities");
//		advancedPanel.add( resolveEntitiesCheck, FormLayout.FULL);
		
		loadDTDGrammarCheck = new JCheckBox( "Load DTD Grammar");
		advancedPanel.add( loadDTDGrammarCheck, FormLayout.FULL);

		panel.add( advancedPanel, FormLayout.FULL_FILL);
		
		JPanel catalogsPanel 	= new JPanel( new FormLayout( 0, 5));
		catalogsPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Catalogs"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		JPanel catalogsListPanel = new JPanel( new BorderLayout());

		catalogList = new JList();
		catalogList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		catalogList.setVisibleRowCount( 5);
		catalogsModel = new DefaultListModel();
		catalogList.setModel( catalogsModel);
		catalogList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = catalogList.getSelectedIndex();
                if(selected>-1) {
                    catalogList.ensureIndexIsVisible(selected);
                }
            }
		    
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 2, 0, 0, 0));

		JButton addButton = new JButton("Add ...");
		addButton.setFont( addButton.getFont().deriveFont( Font.PLAIN));
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				JFileChooser chooser = FileUtilities.getCatalogChooser();

				if ( catalogsModel.getSize() > 0) {
					chooser.setCurrentDirectory( new File( (String)catalogsModel.lastElement()));
				}
				
				int result = chooser.showOpenDialog( parent);
				
				if ( result == JFileChooser.APPROVE_OPTION) {
					File file = chooser.getSelectedFile();
					String path = file.getPath();

					if ( !file.isDirectory() && catalogsModel.indexOf( path) == -1) {
						catalogsModel.addElement( path);
						/*addedCatalogs.addElement( path);*/
					}
				}
			}
		});
		buttonPanel.add( addButton);
		
		
		JButton deleteButton = new JButton( "Delete");
		deleteButton.setFont( deleteButton.getFont().deriveFont( Font.PLAIN));
		deleteButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
			    
//				  *******************START new code*********************************
			    
			    //get the selected objects
			    Object[] selectedObjects = catalogList.getSelectedValues();
			    
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
				            message += (String)selectedObjects[cnt];
				            
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
			                    catalogsModel.removeElement( selectedObjects[cnt]);
			                    deleteAll=true;
			                } 
			                //user choose to delete this object, remove it from the list
			                else if(questionResult==JOptionPane.YES_OPTION) {
			                    catalogsModel.removeElement( selectedObjects[cnt]);
			                }
				            
			            } else {
		                    catalogsModel.removeElement( selectedObjects[cnt]);
		                }
			        } //end for loop
			    } //end if(selectedObjects.length>1) {
			    
			    	    
			    
			    //*******************END new code*********************************
			 
			}
		});
		buttonPanel.add( deleteButton);

		catalogsListPanel.add( new JScrollPane( catalogList), BorderLayout.CENTER);
		JPanel preferPublicIdentifiersPanel = new JPanel( new BorderLayout());
		preferPublicIdentifiersPanel.add( buttonPanel, BorderLayout.EAST);

		preferPublicIdentifiersBox = new JCheckBox( "Prefer Public Identifiers");
		preferPublicIdentifiersPanel.add( preferPublicIdentifiersBox, BorderLayout.WEST);
		catalogsListPanel.add( preferPublicIdentifiersPanel, BorderLayout.SOUTH);

		catalogsPanel.add( catalogsListPanel, FormLayout.FULL_FILL);

//		preferPublicIdentifiersBox = new JCheckBox( "Prefer Public Identifiers");
//		catalogsPanel.add( preferPublicIdentifiersBox, FormLayout.FULL);

//		catalogsPanel.add( getSeparator(), FormLayout.FULL);

		panel.add( catalogsPanel, FormLayout.FULL_FILL);

		JPanel mappingsPanel 	= new JPanel( new FormLayout( 0, 5));
		mappingsPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Prefix Namespace Mappings"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		prefixNamespaceMappingPanel = new PrefixNamespaceMappingPanel( parent, properties, 5);

		mappingsPanel.add( prefixNamespaceMappingPanel, FormLayout.FULL_FILL);

		panel.add( mappingsPanel, FormLayout.FULL_FILL);

		return panel;
	}

	private JColorChooser getColorChooser() {
		if ( colorChooser == null) {
			colorChooser = new JColorChooser();
		}
		
		return colorChooser;
	}

	private void setDefault() {
		Font font = TextPreferences.getDefaultFont();
		fontSelectionBox.setSelectedItem( font.getName());
		sizeSelectionBox.setSelectedItem( ""+font.getSize());
		tabSizeBox.setSelectedItem( ""+TextPreferences.DEFAULT_TAB_SIZE);
		xmlEditor.setFont( font);
		xmlEditor.setTabSize( TextPreferences.DEFAULT_TAB_SIZE);
		
		for ( int i = 0; i < styles.size(); i++) {
			FontStyle style = (FontStyle)styles.elementAt(i);
			style.setDefault();
		}
		
		antialiasingBox.setSelected( false);
		convertTabBox.setSelected( false);

		stylePanel.selectFontStyle( (FontStyle)styles.elementAt(0));
	}

	/**
	 * Initialises the values in the dialog.
	 */
	public void show() {
		Font font = textPreferences.getFont();
		
		fontSelectionBox.setSelectedItem( font.getName());
		sizeSelectionBox.setSelectedItem( ""+font.getSize());
		tabSizeBox.setSelectedItem( ""+textPreferences.getSpaces());
		convertTabBox.setSelected( textPreferences.convertTab());
		xmlEditor.setFont( font);
		
		updatePreferences();

		for ( int i = 0; i < styles.size(); i++) {
			FontStyle style = (FontStyle)styles.elementAt(i);
			style.reset();

		}

		stylePanel.selectFontStyle( (FontStyle)styles.elementAt(0));

		// Editor boxes
//		indentMixedContentBox.setSelected( editorProperties.isIndentMixedContent());

//		tagCompletionBox.setSelected( editorProperties.isTagCompletion());
//		softWrapBox.setSelected( editorProperties.isSoftWrapping());
//		showMarginBox.setSelected( editorProperties.isShowMargin());
//		showOverviewMarginBox.setSelected( editorProperties.isShowOverviewMargin());
//		showFoldingMarginBox.setSelected( editorProperties.isShowFoldingMargin());
//		autoIndentationBox.setSelected( editorProperties.isSmartIndentation());
//		textPromptingBox.setSelected( editorProperties.isTextPrompting());
		antialiasingBox.setSelected( TextPreferences.isAntialiasing());
		uniqueXPathBox.setSelected( properties.isUniqueXPath());
		multipleDocumentsBox.setSelected( properties.isMultipleDocumentOccurrences());
//		strictTextPromptingBox.setSelected( editorProperties.isStrictTextPrompting());

		// Designer boxes
//		autoCreateRequiredBox.setSelected( designerProperties.isAutoCreateRequired());
//		showElementValuesBox.setSelected( designerProperties.isShowElementValues());
//		showAttributeValuesBox.setSelected( designerProperties.isShowAttributeValues());

		// General boxes
		autoSyncSelectionBox.setSelected( properties.isAutoSyncSelection());
		scrollDocumentTabsBox.setSelected( properties.isScrollDocumentTabs());
		checkTypeOnOpeningBox.setSelected( properties.isCheckTypeOnOpening());
		showFullPathBox.setSelected( properties.isShowFullPath());

//		showNavigatorAttributesBox.setSelected( navigatorProperties.isShowAttributeValues());

		promptCreateTypeOnOpeningBox.setSelected( properties.isPromptCreateTypeOnOpening());
		validateOnOpeningBox.setSelected( properties.isValidateOnOpening());
		useInternalSchemaBox.setSelected( properties.useInternalSchema());
		hideScenarioExecutionDialogWhenCompleteBox.setSelected( properties.isHideExecuteScenarioDialogWhenComplete());
		openXIncludeInNewDocumentBox.setSelected( properties.isOpenXIncludeInNewDocument());
//		attributesNewLineBox.setSelected( properties.isAttributesNewLine());

		useProxyCheck.setSelected( properties.isUseProxy());
		proxyPortField.setText( properties.getProxyPort());
		proxyPortField.setCaretPosition(0);
		proxyHostField.setText( properties.getProxyHost());
		proxyHostField.setCaretPosition(0);
		
		String processor = properties.getXSLTProcessor();
		if ( processor.equals( ConfigurationProperties.XSLT_PROCESSOR_SAXON_XSLT1)) {
			saxon1Radio.setSelected( true);
		} else if ( processor.equals( ConfigurationProperties.XSLT_PROCESSOR_SAXON_XSLT2)) {
			saxon2Radio.setSelected( true);
		} else {
			xalanRadio.setSelected( true);
		}
		
		if ( browserField != null) {
			browserField.setText( properties.getBrowser());
		}
		
//		resolveEntitiesCheck.setSelected( properties.isResolveEntities());
		loadDTDGrammarCheck.setSelected( properties.isLoadDTDGrammar());

		// printing
		Font printFont = printPreferences.getFont();
		printFontSelectionBox.setSelectedItem( printFont.getName());
		printSizeSelectionBox.setSelectedItem( ""+printFont.getSize());
		
		printLineNumbers.setSelected( printPreferences.isPrintLineNumbers());
		printHeader.setSelected( printPreferences.isPrintHeader());
		printWrapText.setSelected( printPreferences.isWrapText());

//		showNamespaces.setSelected( viewerProperties.isShowNamespaces());
//		showAttributes.setSelected( viewerProperties.isShowAttributes());
//		showValues.setSelected( viewerProperties.isShowValues());
//		showComments.setSelected( viewerProperties.isShowComments());
//		showInline.setSelected( viewerProperties.isShowInline());
//		showPI.setSelected( viewerProperties.isShowPI());
		
		// format
		wrapTextCheck.setSelected( editorProperties.isWrapText());
		wrappingColumnField.setValue( new Integer( editorProperties.getWrappingColumn()));
		
		// heap		
//		initialHeapSizeText.setValue( new Integer( properties.getInitialHeapSize()));
//		maximumHeapSizeText.setValue( new Integer( properties.getMaximumHeapSize()));

		//removedExtensions	= new Vector();
		//addedExtensions	= new Vector();
		
		
		UIManager.LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
		String current = UIManager.getLookAndFeel().getClass().getName();
		
		for ( int i = 0; i < info.length; i++) {		
			if ( current.equals( info[i].getClassName())) {
				lafSelectionBox.setSelectedIndex( i);
				break;
			}
		} 
		
		extensionsModel.removeAllElements();
		initialExtensions = properties.getExtensions();
		Vector extensions = properties.getExtensions();
		currentExtensions = (Vector)extensions.clone();
		for ( int i = extensions.size()-1; i >= 0; i--) {
			extensionsModel.addElement( extensions.elementAt(i));
		}

		/*removedCatalogs	= new Vector();
		addedCatalogs	= new Vector();*/
		
		preferPublicIdentifiersBox.setSelected( properties.isPreferPublicIdentifiers());

		prefixNamespaceMappingPanel.init();
		
		// security
//		securityPreferences.setKeystoreType( keystoreFileField.getText());
		keystorePasswordField.setText( securityPreferences.getKeystorePassword());
		keystoreFileField.setText( securityPreferences.getKeystoreFile());
		privatekeyPasswordField.setText( securityPreferences.getPrivatekeyPassword());
		privatekeyAliasField.setText( securityPreferences.getPrivatekeyAlias());
		certificateAliasField.setText( securityPreferences.getCertificateAlias());

		catalogsModel.removeAllElements();

		Vector catalogs = properties.getCatalogs();
		for ( int i = catalogs.size()-1; i >= 0; i--) {
			catalogsModel.addElement( catalogs.elementAt(i));
		}

		switch ( editorProperties.getFormatType()) {
			case EditorProperties.FORMAT_CUSTOM:
				customFormatterRadio.setSelected( true);

				indentCheck.setSelected( editorProperties.isCustomIndent());
				padTextCheck.setSelected( editorProperties.isCustomPadText());
				newlinesCheck.setSelected( editorProperties.isCustomNewline());
				stripWhitespaceCheck.setSelected( editorProperties.isCustomStrip());
				preserveMixedContentCheck.setSelected( editorProperties.isCustomPreserveMixedContent());
				break;

			case EditorProperties.FORMAT_COMPACT:
				compactFormatterRadio.setSelected( true);
				break;

			case EditorProperties.FORMAT_STANDARD:
				standardFormatterRadio.setSelected( true);
				break;
		}
		
		
		//	create a new temp key cache
		configMap = new Hashtable();
		
		// add the keymapping configurations
		Vector configNames = keyPreferences.getConfigurationNames();
		String activeConfig =  keyPreferences.getActiveConfiguration();
		keySelectionBox.removeAllItems();
		
		for (int i=0;i<configNames.size();i++)
		{
			String name = (String)configNames.get(i);
			keySelectionBox.addItem(name);
			if (name.equals(activeConfig))
			{
				keySelectionBox.setSelectedIndex(i);
			}
		}
		
		super.show();
	}
	
	/**
	 * Update the preferences.
	 */
	private void updatePreferences() {
		setAttributes( Constants.ELEMENT_NAME, TextPreferences.ELEMENT_NAME);
		setAttributes( Constants.ELEMENT_VALUE, TextPreferences.ELEMENT_VALUE);
		setAttributes( Constants.ELEMENT_PREFIX, TextPreferences.ELEMENT_PREFIX);
	
		setAttributes( Constants.ATTRIBUTE_NAME, TextPreferences.ATTRIBUTE_NAME);
		setAttributes( Constants.ATTRIBUTE_VALUE, TextPreferences.ATTRIBUTE_VALUE);
		setAttributes( Constants.ATTRIBUTE_PREFIX, TextPreferences.ATTRIBUTE_PREFIX);
	
		setAttributes( Constants.NAMESPACE_NAME, TextPreferences.NAMESPACE_NAME);
		setAttributes( Constants.NAMESPACE_VALUE, TextPreferences.NAMESPACE_VALUE);
		setAttributes( Constants.NAMESPACE_PREFIX, TextPreferences.NAMESPACE_PREFIX);
	
		setAttributes( Constants.ENTITY, TextPreferences.ENTITY);
		setAttributes( Constants.COMMENT, TextPreferences.COMMENT);
		setAttributes( Constants.CDATA, TextPreferences.CDATA);
		setAttributes( Constants.SPECIAL, TextPreferences.SPECIAL);

		setAttributes( Constants.PI_TARGET, TextPreferences.PI_TARGET);
		setAttributes( Constants.PI_NAME, TextPreferences.PI_NAME);
		setAttributes( Constants.PI_VALUE, TextPreferences.PI_VALUE);

		setAttributes( Constants.STRING_VALUE, TextPreferences.STRING_VALUE);
		setAttributes( Constants.ENTITY_VALUE, TextPreferences.ENTITY_VALUE);

		setAttributes( Constants.ENTITY_DECLARATION, TextPreferences.ENTITY_DECLARATION);
		setAttributes( Constants.ENTITY_NAME, TextPreferences.ENTITY_NAME);
		setAttributes( Constants.ENTITY_TYPE, TextPreferences.ENTITY_TYPE);
		
		setAttributes( Constants.ATTLIST_DECLARATION, TextPreferences.ATTLIST_DECLARATION);
		setAttributes( Constants.ATTLIST_NAME, TextPreferences.ATTLIST_NAME);
		setAttributes( Constants.ATTLIST_TYPE, TextPreferences.ATTLIST_TYPE);
		setAttributes( Constants.ATTLIST_VALUE, TextPreferences.ATTLIST_VALUE);
		setAttributes( Constants.ATTLIST_DEFAULT, TextPreferences.ATTLIST_DEFAULT);

		setAttributes( Constants.ELEMENT_DECLARATION, TextPreferences.ELEMENT_DECLARATION);
		setAttributes( Constants.ELEMENT_DECLARATION_NAME, TextPreferences.ELEMENT_DECLARATION_NAME);
		setAttributes( Constants.ELEMENT_DECLARATION_TYPE, TextPreferences.ELEMENT_DECLARATION_TYPE);
		setAttributes( Constants.ELEMENT_DECLARATION_PCDATA, TextPreferences.ELEMENT_DECLARATION_PCDATA);
		setAttributes( Constants.ELEMENT_DECLARATION_OPERATOR, TextPreferences.ELEMENT_DECLARATION_OPERATOR);

		setAttributes( Constants.NOTATION_DECLARATION, TextPreferences.NOTATION_DECLARATION);
		setAttributes( Constants.NOTATION_DECLARATION_NAME, TextPreferences.NOTATION_DECLARATION_NAME);
		setAttributes( Constants.NOTATION_DECLARATION_TYPE, TextPreferences.NOTATION_DECLARATION_TYPE);

		setAttributes( Constants.DOCTYPE_DECLARATION, TextPreferences.DOCTYPE_DECLARATION);
		setAttributes( Constants.DOCTYPE_DECLARATION_TYPE, TextPreferences.DOCTYPE_DECLARATION_TYPE);
	}
	
	private void setAttributes( int id, String property) {
		xmlEditor.setAttributes( id, TextPreferences.getFontType( property).getColor(), TextPreferences.getFontType( property).getStyle());
	}

	private void keystoreFileButtonPressed() {
		JFileChooser chooser = getKeystoreFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();

			keystoreFileField.setText( file.toString());
			keystoreFileField.setCaretPosition( 0);
		}
	}

	private JFileChooser getKeystoreFileChooser() {
		if ( keystoreFileChooser == null) {
			keystoreFileChooser = FileUtilities.createFileChooser();

			keystoreFileChooser.addChoosableFileFilter( new DefaultFileFilter( "jks", "Java Key Store"));
		} 

		File file = new File( keystoreFileField.getText());

		if ( file == null) {
			file = FileUtilities.getLastOpenedFile();
		}

		keystoreFileChooser.setCurrentDirectory( file);
		keystoreFileChooser.rescanCurrentDirectory();
		
		return keystoreFileChooser;
	}

	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

	private boolean hasSameWidth( Font font, int style1, int style2) {
	    String testString = "<Test test:nms=\"http://test.org\"/>";
		JTextArea pane = new JTextArea();
		
	    Font font1 = font.deriveFont( style1, 12);
	    FontMetrics fm = pane.getFontMetrics( font1);
	    int width1 = fm.stringWidth( testString);

	    Font font2 = font.deriveFont( style2, 12);
	    fm = pane.getFontMetrics( font2);
	    int width2 = fm.stringWidth( testString);

		if ( width1 == width2) {	// && italicWidth == italicBoldWidth) { 
			return true;
		} 
		
		return false;
	}
	
	private boolean removeStringFromVector(String obj, Vector vector) {
	    
	    int size = vector.size();
	    for(int cnt=0;cnt<size;++cnt) {
	        String objectTemp = (String)vector.get(cnt);
	        if(objectTemp.equalsIgnoreCase(obj)) {
	            vector.remove(cnt);
	            return(true);
	        }
	    }
	    return(false);
	}

	private class FontStylePanel extends JPanel implements ActionListener {
		private FontStyle style = null;

		private JRadioButton italicButton = null;
		private JRadioButton boldButton = null;
		private JRadioButton plainButton = null;

		private JComboBox stylesBox = null;
		private ColorIcon icon = null;
		private JButton colorButton = null;
		
		private int id;

		public FontStylePanel( Vector styles) {
			super( new FormLayout( 5, 5));

			stylesBox = new JComboBox( styles);
			stylesBox.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent e) {
					setFontStyle( (FontStyle)stylesBox.getSelectedItem());
				}
			});
			
			ButtonGroup group = new ButtonGroup();

			italicButton = new JRadioButton( "Italic");
			italicButton.setFont( italicButton.getFont().deriveFont( Font.PLAIN));
			italicButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent e) {
					style.setStyle( Font.ITALIC);
					updateEditor();
				}
			});
			group.add( italicButton);

			boldButton = new JRadioButton( "Bold");
			boldButton.setFont( boldButton.getFont().deriveFont( Font.PLAIN));
			boldButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent e) {
					style.setStyle( Font.BOLD);
					updateEditor();
				}
			});
			group.add( boldButton);

			plainButton = new JRadioButton( "Plain");
			plainButton.setFont( plainButton.getFont().deriveFont( Font.PLAIN));
			plainButton.addItemListener( new ItemListener() {
				public void itemStateChanged( ItemEvent e) {
					style.setStyle( Font.PLAIN);
					updateEditor();
				}
			});
			group.add( plainButton);
			
			icon = new ColorIcon( Color.black, 32, 16);
			colorButton = new JButton( icon);
			colorButton.addActionListener( this);
			colorButton.setMargin( new Insets( 1, 1, 1, 1));
			
			JPanel flowPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 0));
			flowPanel.add( plainButton);
			flowPanel.add( italicButton);
			flowPanel.add( boldButton);
			
			JPanel settingsPanel = new JPanel( new BorderLayout());
			settingsPanel.add( flowPanel, BorderLayout.CENTER);
			settingsPanel.add( colorButton, BorderLayout.EAST);
			
			add( new JLabel( "Style:"), FormLayout.LEFT);
			add( stylesBox, FormLayout.RIGHT_FILL);
			add( new JLabel( "Settings:"), FormLayout.LEFT);
			add( settingsPanel, FormLayout.RIGHT_FILL);
		}
		
		public void setFontStyle( FontStyle style) {
			this.style = style;
			
			icon.setColor( style.getColor());
			
			if ( style.getStyle() == Font.BOLD) {
				boldButton.setSelected( true);
			} else if ( style.getStyle() == Font.ITALIC) {
				italicButton.setSelected( true);
			} else {
				plainButton.setSelected( true);
			}

			colorButton.repaint();
		}

		public void selectFontStyle( FontStyle style) {
			stylesBox.setSelectedItem( style);
			
			setFontStyle( style);
		}

		public void reset( boolean italic, boolean bold) {
			if ( !bold) {
				if ( boldButton.isSelected()) {
					plainButton.setSelected( true);
				}
				boldButton.setEnabled( false);
			} else {
				boldButton.setEnabled( true);
			}

			if ( !italic) {
				if ( italicButton.isSelected()) {
					plainButton.setSelected( true);
				}
				italicButton.setEnabled( false);
			} else {
				italicButton.setEnabled( true);
			}
		}
		
		// color button pressed
		public void actionPerformed( ActionEvent e) {
			// get color chooser and set the color chosen in the icon
			Color color = JColorChooser.showDialog( parent, style.getType().getName()+" Color", icon.getColor());
			
			if ( color != null)	{
				icon.setColor( color);
				style.setColor( color);
				updateEditor();
			}
		}
	}

	private class FontStyle {
		private FontType type = null;

		private int defaultStyle	= -1;
		private Color defaultColor	= null;
		
		private Color color	= null;
		private int style	= -1;
		private String name	= null;
		
		private int id		= -1;

		public FontStyle( String name, FontType type, int id, int defaultStyle, Color defaultColor) {
			this.color = type.getColor();
			this.style = type.getStyle();
			this.name = name;
			
			this.defaultColor = defaultColor;
			this.defaultStyle = defaultStyle;
			
			this.type = type;
			this.id = id;
		}
		
		public int getId() {
			return id;
		}

		public FontType getType() {
			return type;
		}

		public int getStyle() {
			return style;
		}

		public void setStyle( int style) {
			this.style = style;
		}

		public void setItalic( boolean enabled) {
			if ( (style & Font.ITALIC) > 0) {
				if ( !enabled) {
					style = style - Font.ITALIC;
				}
			} else {
				if ( enabled) {
					style = style + Font.ITALIC;
				}
			}
		}

		public boolean isItalic() {
			return (style & Font.ITALIC) > 0;
		}

		public void setBold( boolean enabled) {
			if ( (style & Font.BOLD) > 0) {
				if ( !enabled) {
					style = style - Font.BOLD;
				}
			} else {
				if ( enabled) {
					style = style + Font.BOLD;
				}
			}
		}

		public boolean isBold() {
			return (style & Font.BOLD) > 0;
		}

		public Color getColor() {
			return color;
		}

		public void setColor( Color color) {
			this.color = color;
		}

		// Set the values from the fonttype object		
		public void reset() {
			style = type.getStyle();
			color = type.getColor();
		}
		
		// Set the default values	
		public void setDefault() {
			style = defaultStyle;
			color = defaultColor;
		}

		// Set the values in the fonttype object		
		public void update() {
			type.setStyle( style);
			type.setColor( color);
		}

		public String toString() {
			return name;
		}
	}

	private class ColorIcon implements Icon {
		private Color color = null;
		private Dimension size = null;
		
		public ColorIcon( Color color, int w, int h) {
			size = new Dimension( w, h);
			this.color = color;
		}
		
		public void paintIcon( Component c, Graphics g, int x, int y) {
			g.setColor( Color.black);
			g.drawRect( x, y, size.width-1, size.height-1);
			
			g.setColor( color);
			g.fillRect( x+1, y+1, size.width-2, size.height-2);
		}
		
		public void setColor( Color color) {
			this.color = color;
		}

		public Color getColor() {
			return color;
		}

		public int getIconWidth() {
			return size.width;
		}
	
		public int getIconHeight() {
			return size.height;
		}
	}
	
	
	// inner class that renders the action names for the keymappings tab
	class CommandNamesCellRenderer extends JPanel implements ListCellRenderer 
	{
		private JLabel command 		= null;
		private JLabel accelerator	= null;
		private JPanel cellPanel 	= null;
		
		public CommandNamesCellRenderer()
		{
			super( new BorderLayout());
			
			command = new JLabel();
			command.setOpaque( false);
			command.setFont( command.getFont().deriveFont( Font.BOLD));
			command.setForeground( Color.black);
			
			accelerator = new JLabel();
			accelerator.setOpaque( false);
			accelerator.setFont( accelerator.getFont().deriveFont( Font.PLAIN,accelerator.getFont().getSize()-2));
			accelerator.setForeground( Color.black);
			
			this.add( command, BorderLayout.CENTER);
			this.add( accelerator, BorderLayout.EAST);
		}
		
		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected,      
				boolean cellHasFocus)    
		{	
			
			
			// use the object value as selectedIndex doesn't always return correct value
			command.setText(value.toString());
			
			KeyMap keyMap = (KeyMap)keyMaps.get(value.toString());
			String key = keyPreferences.getKeySequence(keyMap.getKeystrokes());
			
			if (!key.equals("") && key != null)
			{
				accelerator.setText("("+key+")");
			}
			else
			{
				accelerator.setText("");
			}
			
			if (isSelected) 
			{
				  setBackground(list.getSelectionBackground());
				  command.setForeground(list.getSelectionForeground());
				  accelerator.setForeground(list.getSelectionForeground());
			}
			else 
			{
				  setBackground(list.getBackground());
				  command.setForeground(list.getForeground());
				  accelerator.setForeground(list.getForeground());
			}

			
			setEnabled(list.isEnabled());
			//setFont(list.getFont());
			setOpaque(true);
			
			setBorder( new EmptyBorder( 0, 2, 1, 15));
			return this;
		}
	}
} 
