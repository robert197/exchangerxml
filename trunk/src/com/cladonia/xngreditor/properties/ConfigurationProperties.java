/*
 * $Id: ConfigurationProperties.java,v 1.27 2005/08/31 10:09:15 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.cladonia.schema.viewer.SchemaViewerProperties;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.designer.DesignerProperties;
import com.cladonia.xml.editor.Bookmark;
import com.cladonia.xml.editor.EditorProperties;
//import com.cladonia.xml.grid.GridProperties;
import com.cladonia.xml.helper.HelperProperties;
import com.cladonia.xml.navigator.NavigatorProperties;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertiesFile;
import com.cladonia.xml.properties.PropertyList;
import com.cladonia.xml.viewer.ViewerProperties;
import com.cladonia.xml.browser.BrowserProperties;
import com.cladonia.xml.webservice.soap.SOAPProperties;
import com.cladonia.xngreditor.Main;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.plugins.PluginViewProperties;
import com.cladonia.xngreditor.project.ProjectProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.template.TemplateProperties;
import com.cladonia.xslt.debugger.ui.DebuggerProperties;

/**
 * Handles the Xml Plus configuration document.
 *
 * @version	$Revision: 1.27 $, $Date: 2005/08/31 10:09:15 $
 * @author Dogsbay
 */
public class ConfigurationProperties extends Properties {
	
	private static boolean DEBUG = false;
	
	private static final int DEFAULT_MAXIMUM_HEAP_SIZE	= 128;
	private static final int DEFAULT_INITIAL_HEAP_SIZE	= 64;

	private static final int MAX_REPLACES = 10;
	private static final int MAX_SEARCHES = 10;
	private static final int MAX_XPATHS = 10;
	private static final int MAX_XPATH_SEARCHES = 10;
	private static final int MAX_XPATH_PREDICATE_SEARCHES = 10;
	private static final int MAX_SORT_XPATH_PREDICATE_SEARCHES = 10;
	private static final int MAX_XPATH_TOOLS = 10;
	private static final int DEFAULT_SPACES = 4;
	private static final int MAX_DATABASE_DRIVERS = 10;
	private static final int MAX_DATABASE_CONNECTIONS = 10;

	private static final String PREFER_PUBLIC_IDENTIFIERS	= "prefer-public-identifiers";

	private static final String LOOK_AND_FEEL		= "look-and-feel";

	private static final String LICENSE_ACCEPTED	= "license-accepted";

	private static final String XSLT_PROCESSOR		= "xslt-processor";
	public static final String XSLT_PROCESSOR_XALAN			= "org.apache.xalan.processor.TransformerFactoryImpl";
	public static final String XSLT_PROCESSOR_SAXON_XSLT1	= "com.icl.saxon.TransformerFactoryImpl";
	public static final String XSLT_PROCESSOR_SAXON_XSLT2	= "net.sf.saxon.TransformerFactoryImpl";

	private static final String MAXIMUM_HEAP_SIZE	= "maximum-heap-size";
	private static final String INITIAL_HEAP_SIZE	= "initial-heap-size";

	private static final String USE_PROXY	= "use-proxy";
	private static final String PROXY_PORT	= "proxy-port";
	private static final String PROXY_HOST	= "proxy-host";

	private static final String BROWSER	= "browser";

	//	private static final String AUTO_GRAMMAR_CREATION	= "auto-grammar-creation";
	private static final String AUTO_SYNC_SELECTION		= "auto-sync-selection";
	private static final String ATTRIBUTES_NEW_LINE		= "attributes-new-line";

	private static final String SHOW_FULL_PATH		= "show-full-path";

	private static final String SCROLL_DOCUMENT_TABS	= "scroll-document-tabs";

	private static final String SHOW_EDITOR_TOOLBAR		= "show-editor-toolbar";
	private static final String SHOW_MAIN_TOOLBAR		= "show-main-toolbar";

	private static final String SYNCHRONISE_SPLITS		= "synchronise-splits";

	private static final String CHECK_TYPE_ON_OPENING			= "check-type-opening";
	private static final String PROMPT_CREATE_TYPE_ON_OPENING	= "prompt-create-type-opening";
	private static final String VALIDATE_ON_OPENING				= "validate-opening";
	private static final String USE_INTERNAL_SCHEMA				= "use-internal-schema";
	
	private static final String SHOW_DOCUMENT_PROPERTIES					= "show-document-properties";
	private static final String MULTIPLE_DOCUMENT_OCCURRENCES				= "multiple-document-occurrences";

	private static final String HIDE_EXECUTE_SCENARIO_DIALOG_WHEN_COMPLETE	= "hide-execute-scenario-dialog-when-complete";
	private static final String SHOW_EXECUTE_SCENARIO_DIALOG_LOG 			= "show-execute-scenario-dialog-log";
	private static final String OPEN_XINCLUDE_NEW_DOCUMENT 					= "open-xinclude-new-document";
	
	private static final String SEARCH_REGULAR_EXPRESSION	= "search-regular-expression";
	private static final String SEARCH_XPATH				= "search-xpath";
	private static final String SEARCH_MATCH_WHOLE_WORD		= "search-match-whole-word";
	private static final String SEARCH_MATCH_CASE			= "search-match-case";
	private static final String SEARCH_DIRECTION_DOWN		= "search-direction-down";
	private static final String SEARCH_WRAP					= "search-wrap";
	private static final String SEARCH_BASIC				= "search-basic";
	private static final String REPLACE_BASIC				= "replace-basic";
	
	private static final String LOAD_DTD_GRAMMAR			= "load-dtd-grammar";

	private static final String XPATH_SEARCH 	= "xpath-search";
	private static final String XPATH_PREDICATE = "xpath-predicate";
	private static final String SORT_XPATH_PREDICATE = "sort-xpath-predicate";
	private static final String XPATH_TOOLS = "xpath-tools";
	private static final String XPATH_UNIQUE 	= "xpath-unique";
	
	private static final String DATABASE_DRIVERS = "database-drivers";
	private static final String DATABASE_CONNECTIONS = 	"database-connections";

	private static final String FIND_IN_FILES_FOLDER = "find-in-files-folder";
	
	private static final String PREFIX_NAMESPACE_MAPPING	= "prefix-namespace-mapping";

	private static final String SPACES 			= "spaces";
	private static final String SEARCH 			= "search";
	private static final String XPATH 			= "xpath";
	private static final String REPLACE 		= "replace";
	
	private static final String EXTENSION	= "extension";
	private static final String CATALOG		= "catalog";

	private PropertyList extensions = null;
	private PropertyList catalogs = null;
	private PropertyList prefixNamespaceMappings = null;

	private PropertyList replaces = null;
	private PropertyList searches = null;
	private PropertyList xpaths = null;
	private PropertyList xpathSearches = null;
	private PropertyList xpathPredicates = null;
	private PropertyList sortXPathPredicates = null;
	private PropertyList xpathTools = null;
	private PropertyList lastDocuments = null;
	private PropertyList lastURLs = null;
	
	private PropertyList databaseDrivers = null;
	private PropertyList databaseConnections = null;

	public static final String XMLPLUS_HOME = System.getProperty( "user.home")+File.separator+".xngreditor"+File.separator;
	public static final String PROPERTIES_FILE = ".xngreditor.xml";

	public static final String LAST_OPENED_DOCUMENT		= "last-opened-document";
	public static final String LAST_OPENED_URL			= "last-opened-url";
	public static final String LAST_OPENED_GRAMMAR		= "last-opened-grammar";

	public static final String TEXT_EDITOR		= "text-editor";
	public static final String DOM_EDITOR		= "dom-editor";
	public static final String XML_VIEWER 		= "xml-viewer";
	public static final String XML_BROWSER 		= "xml-browser";
	public static final String SCHEMA_VIEWER	= "schema-viewer";
	public static final String XML_GRID			= "xml-grid";

	//public static final String DEBUGGER		= "debugger";

	public static final String HELPER		= "helper";
	public static final String NAVIGATOR	= "navigator";


	public static final String XPOS 	= "xpos";
	public static final String YPOS		= "ypos";
	public static final String WIDTH 	= "width";
	public static final String HEIGHT	= "height";

	public static final int DEFAULT_WIDTH 	= 1024;
	public static final int DEFAULT_HEIGHT	= 768;
	
	public static final String WINDOW_MAXIMISED = "window-maximised";

	private static final String TOP_DIVIDER_LOCATION		= "top-divider-location";
	private static final int DEFAULT_TOP_DIVIDER_LOCATION	= 200;

	private static final String DIVIDER_LOCATION		= "divider-location";
	private static final int DEFAULT_DIVIDER_LOCATION	= 400;
	
	private static final String SAVE_PROPERTIES_INTERVAL = "save-properties-interval";
	private static final long DEFAULT_SAVE_PROPERTIES_INTERVAL = 300000;

	private ExchangerDocument document	= null;

	private DesignerProperties designerProperties 			= null;
	private EditorProperties editorProperties				= null;
	private ViewerProperties viewerProperties				= null;
	private BrowserProperties browserProperties				= null;
	private SchemaViewerProperties schemaViewerProperties	= null;
	//private GridProperties gridProperties					= null;
	

	private DebuggerProperties debuggerProperties		= null;

	private HelperProperties helperProperties		= null;
	private NavigatorProperties navigatorProperties	= null;

	private SOAPProperties soapProperties = null;

	private TextPreferences textPreferences = null;
	private PrintPreferences printPreferences = null;
	private SecurityPreferences securityPreferences = null;
	private KeyPreferences keyPreferences = null;
	
	private XercesProperties xercesProperties = null;
	
	private PropertyList findInFilesFolder;
	
	
	private List pluginProperties							= null;
	private List propertyFiles = null;
	
	
	
	/**
	 * Creates the Configuration Document wrapper.
	 * It reads in the root element and if it has to, it creates the property file.
	 *
	 * @param the url to the XML document.
	 */
	public ConfigurationProperties( ExchangerDocument document) {
		super( document.getRoot());
		this.document = document;

		searches = getList( SEARCH, MAX_SEARCHES);
		xpaths = getList( XPATH, MAX_XPATHS);
		replaces = getList( REPLACE, MAX_REPLACES);
		extensions = getList( EXTENSION, -1);
		catalogs = getList( CATALOG, -1);
		

		prefixNamespaceMappings = getList( PREFIX_NAMESPACE_MAPPING, -1);
		xpathSearches = getList( XPATH_SEARCH, MAX_XPATH_SEARCHES);
		xpathPredicates = getList(XPATH_PREDICATE, MAX_XPATH_PREDICATE_SEARCHES);
		sortXPathPredicates = getList(SORT_XPATH_PREDICATE, MAX_SORT_XPATH_PREDICATE_SEARCHES);
		xpathTools = getList(XPATH_TOOLS, MAX_XPATH_TOOLS);
		
		databaseDrivers = getList(DATABASE_DRIVERS,MAX_DATABASE_DRIVERS);
		databaseConnections = getList(DATABASE_CONNECTIONS,MAX_DATABASE_CONNECTIONS);

		findInFilesFolder = getList(FIND_IN_FILES_FOLDER, 1);
		
		lastDocuments = getList( LAST_OPENED_DOCUMENT, 5);
		lastURLs = getList( LAST_OPENED_URL, 10);

		//editorProperties = new EditorProperties( get( TEXT_EDITOR));
		designerProperties = new DesignerProperties( get( DOM_EDITOR));
		viewerProperties = new ViewerProperties( get( XML_VIEWER));
		//gridProperties = new GridProperties( get( XML_GRID));
		browserProperties = new BrowserProperties( get( XML_BROWSER));
		schemaViewerProperties = new SchemaViewerProperties( get( SCHEMA_VIEWER));
		setPluginProperties(new ArrayList());
		
		
		//debuggerProperties = new DebuggerProperties( get( DEBUGGER));

		helperProperties = new HelperProperties( get( HELPER));
		navigatorProperties = new NavigatorProperties( get( NAVIGATOR));

		soapProperties = new SOAPProperties( get( SOAPProperties.SOAP_PROPERTIES));

		//textPreferences = new TextPreferences( get( TextPreferences.TEXT_PREFERENCES));
		printPreferences = new PrintPreferences( get( PrintPreferences.PRINT_PREFERENCES));
		//securityPreferences = new SecurityPreferences( get( SecurityPreferences.SECURITY_PREFERENCES));
		
		//keyPreferences = new KeyPreferences(get(KeyPreferences.KEY_MAPPINGS));
		
		setPropertyFiles(new ArrayList());
		//tjc 14102008
		//for xerces specific compiler properties
		xercesProperties = new XercesProperties(XercesProperties.XERCES_PROPERTIES+Main.PROPERTIES_FILE, XercesProperties.XERCES_PROPERTIES);
		getPropertyFiles().add(xercesProperties);
		
		keyPreferences = new KeyPreferences(KeyPreferences.KEY_MAPPINGS+Main.PROPERTIES_FILE, KeyPreferences.KEY_MAPPINGS);
		getPropertyFiles().add(keyPreferences);
		
		debuggerProperties = new DebuggerProperties(DebuggerProperties.DEBUGGER+Main.PROPERTIES_FILE, DebuggerProperties.DEBUGGER);
		getPropertyFiles().add(debuggerProperties);
		
		textPreferences = new TextPreferences(TextPreferences.TEXT_PREFERENCES+Main.PROPERTIES_FILE, TextPreferences.TEXT_PREFERENCES);
		getPropertyFiles().add(textPreferences);
		
		securityPreferences = new SecurityPreferences(SecurityPreferences.SECURITY_PREFERENCES+Main.PROPERTIES_FILE, SecurityPreferences.SECURITY_PREFERENCES);
		getPropertyFiles().add(securityPreferences);
		
		editorProperties = new EditorProperties(EditorProperties.TEXT_EDITOR+Main.PROPERTIES_FILE, EditorProperties.TEXT_EDITOR);
		getPropertyFiles().add(editorProperties);
		
		
		
		
		//tjc 16102008
		//the save to disk thread
		Timer timer = new Timer("SavePropertiesToDisk", true);
		
		timer.schedule(new TimerTask() {
			public void run() {
				
				if(DEBUG) System.out.println("Saving properties to disk");
				ConfigurationProperties.this.saveToDisk();				
			}
		}, ConfigurationProperties.this.getSavePropertiesInterval(), ConfigurationProperties.this.getSavePropertiesInterval());
		
		
	}
	
	public static ExchangerDocument createPropertiesFile(String fileName, String rootName) {
		
		ExchangerDocument document = null;
		boolean firstTime = false;

		File dir = new File( Main.XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File( dir, fileName);
		URL url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
		
		firstTime = true;
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				firstTime = false;
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
				return null;
			}
		}
		
		XElement root = null;
		//String namespaceURI = null;
		
		if(firstTime == true) {
			//root = new XElement( rootName, "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
			root = new XElement( rootName);
			root.setText( "\n");
			document = new ExchangerDocument( url, root);
					
			//namespaceURI = root.getNamespaceURI();
		}
		else {
			root = document.getRoot();
			//namespaceURI = root.getNamespaceURI();
		}
		
		return(document);
		
	}
	
	/**
	 * Check to find out if auto sync is selected.
	 *
	 * @return true when auto sync is selected.
	 */
	public boolean isAutoSyncSelection() {
		return getBoolean( AUTO_SYNC_SELECTION, true);
	}

	/**
	 * Set the auto-sync selection.
	 *
	 * @param sync auto sync the selection.
	 */
	public void setAutoSyncSelection( boolean sync) {
		set( AUTO_SYNC_SELECTION, sync);
	}

	/**
	 * Check to find out if the document tabs should scroll.
	 *
	 * @return true when the document tabs should scroll.
	 */
	public boolean isScrollDocumentTabs() {
		return getBoolean( SCROLL_DOCUMENT_TABS, false);
	}

	/**
	 * Set wether the document should scroll.
	 *
	 * @param scroll true when the document tabs should scroll.
	 */
	public void setScrollDocumentTabs( boolean scroll) {
		set( SCROLL_DOCUMENT_TABS, scroll);
	}

	/**
	 * Set the accepted license.
	 */
	public void setLicenseAccepted( String type) {
		set( LICENSE_ACCEPTED, type);
	}

	/**
	 * Check to find out if the license has been accepted.
	 *
	 * @return true when the license has been accepted.
	 */
	public boolean isLicenseAccepted( String type) {
		String current = getText( LICENSE_ACCEPTED);
		
		if ( current != null && current.equals( type)) {
			return true;
		}

		return false;
	}

	/**
	 * Check to find out if attributes should be places on a new line.
	 *
	 * @return true when attributes should be placed on a new line.
	 */
	public boolean isAttributesNewLine() {
		return getBoolean( ATTRIBUTES_NEW_LINE, false);
	}

	/**
	 * Set the attributes on a new line.
	 *
	 * @param enabled set attributes on a new line.
	 */
	public void setAttributesNewLine( boolean enabled) {
		set( ATTRIBUTES_NEW_LINE, enabled);
	}

	/**
	 * Check to find out the full path should be shown for the document.
	 *
	 * @return true when a new type should be checked for.
	 */
	public boolean isShowFullPath() {
		return getBoolean( SHOW_FULL_PATH, false);
	}

	public void setShowFullPath( boolean show) {
		set( SHOW_FULL_PATH, show);
	}

	/**
	 * Check to find out the toolbar should be shown for the document.
	 *
	 * @return true when a the toolbar should be visible.
	 */
	public boolean isShowToolbar() {
		return getBoolean( SHOW_MAIN_TOOLBAR, true);
	}

	public void setShowToolbar( boolean show) {
		set( SHOW_MAIN_TOOLBAR, show);
	}

	/**
	 * Check to find out the splits should be synchronised.
	 *
	 * @return true when the splits should be synchronised.
	 */
	public boolean isSynchroniseSplits() {
		return getBoolean( SYNCHRONISE_SPLITS, false);
	}

	public void setSynchroniseSplits( boolean sync) {
		set( SYNCHRONISE_SPLITS, sync);
	}

	/**
	 * Check to find out wether the xpath editor should show 
	 * unique xpaths.
	 *
	 * @return true when a the xpath editor shows unique xpaths.
	 */
	public boolean isUniqueXPath() {
		return getBoolean( XPATH_UNIQUE, false);
	}

	public void setUniqueXPath( boolean unique) {
		set( XPATH_UNIQUE, unique);
	}

	/**
	 * Check to find out the editor toolbar should be shown for the document.
	 *
	 * @return true when a the editor toolbar should be visible.
	 */
	public boolean isShowEditorToolbar() {
		return getBoolean( SHOW_EDITOR_TOOLBAR, true);
	}

	public void setShowEditorToolbar( boolean show) {
		set( SHOW_EDITOR_TOOLBAR, show);
	}

	/**
	 * Check to find out if new types should be checked for.
	 *
	 * @return true when a new type should be checked for.
	 */
	public boolean isCheckTypeOnOpening() {
		return getBoolean( CHECK_TYPE_ON_OPENING, true);
	}

	public void setCheckTypeOnOpening( boolean check) {
		set( CHECK_TYPE_ON_OPENING, check);
	}

	/**
	 * Check to find out wether the execute dialog should 
	 * stay visible after competion.
	 *
	 * @return true when the dialog should stay visible after completion.
	 */
	public boolean isHideExecuteScenarioDialogWhenComplete() {
		return getBoolean( HIDE_EXECUTE_SCENARIO_DIALOG_WHEN_COMPLETE, false);
	}

	public void setHideExecuteScenarioDialogWhenComplete( boolean enabled) {
		set( HIDE_EXECUTE_SCENARIO_DIALOG_WHEN_COMPLETE, enabled);
	}

	/**
	 * Check to find out wether Resolve XInclude, should open in a new document.
	 *
	 * @return true when Resolve XInclude, should open in a new document.
	 */
	public boolean isOpenXIncludeInNewDocument() {
		return getBoolean( OPEN_XINCLUDE_NEW_DOCUMENT, false);
	}

	public void setOpenXIncludeInNewDocument( boolean enabled) {
		set( OPEN_XINCLUDE_NEW_DOCUMENT, enabled);
	}

	/**
	 * Check to find out wether the execute dialog log should be visible.
	 *
	 * @return true when the dialog log should be visible.
	 */
	public boolean isShowExecuteScenarioDialogLog() {
		return getBoolean( SHOW_EXECUTE_SCENARIO_DIALOG_LOG, true);
	}

	public void setShowExecuteScenarioDialogLog( boolean enabled) {
		set( SHOW_EXECUTE_SCENARIO_DIALOG_LOG, enabled);
	}

	/**
	 * Check to find out wether the document properties should be visible.
	 *
	 * @return true when the document properties  should be visible.
	 */
	public boolean isShowDocumentProperties() {
		return getBoolean( SHOW_DOCUMENT_PROPERTIES, true);
	}

	public void setShowDocumentProperties( boolean enabled) {
		set( SHOW_DOCUMENT_PROPERTIES, enabled);
	}

	/**
	 * Check to find out wether the application should be able to 
	 * load the same document more than once.
	 *
	 * @return true when the application should be able to load the 
	 * 				same document more than once.
	 */
	public boolean isMultipleDocumentOccurrences() {
		return getBoolean( MULTIPLE_DOCUMENT_OCCURRENCES, false);
	}

	public void setMultipleDocumentOccurrences( boolean enabled) {
		set( MULTIPLE_DOCUMENT_OCCURRENCES, enabled);
	}

	/**
	 * Prompt the user when a type could not be found.
	 *
	 * @return true when a the user should be prompted to create a new type.
	 */
	public boolean isPromptCreateTypeOnOpening() {
		return getBoolean( PROMPT_CREATE_TYPE_ON_OPENING, false);
	}

	public void setPromptCreateTypeOnOpening( boolean check) {
		set( PROMPT_CREATE_TYPE_ON_OPENING, check);
	}

	/**
	 * Check to find out if a document should be validated when openened.
	 *
	 * @return true when the document should be validated when openened.
	 */
	public boolean isValidateOnOpening() {
		return getBoolean( VALIDATE_ON_OPENING, false);
	}

	public void setValidateOnOpening( boolean validate) {
		set( VALIDATE_ON_OPENING, validate);
	}

	/**
	 * Check to find out if a the schema defined in the document should 
	 * be used as the template schema.
	 *
	 * @return true when the schema in the document should be used.
	 */
	public boolean useInternalSchema() {
		return getBoolean( USE_INTERNAL_SCHEMA, true);
	}

	public void setUseInternalSchema( boolean use) {
		set( USE_INTERNAL_SCHEMA, use);
	}

	/**
	 * Check to find out if a new Grammar should be created automatically.
	 *
	 * @return true when a new Grammar should be created automatically.
	 */
//	public boolean isAutoCreateGrammar() {
//		return getBoolean( AUTO_GRAMMAR_CREATION, true);
//	}

	/**
	 * Set the auto new grammar creation.
	 *
	 * @param create automatically create a new XML Type.
	 */
//	public void setAutoCreateGrammar( boolean create) {
//		set( AUTO_GRAMMAR_CREATION, create);
//	}

	/**
	 * Use a proxy server?
	 *
	 * @return true when a proxy server should be used.
	 */
	public boolean isUseProxy() {
		return getBoolean( USE_PROXY, false);
	}

	/**
	 * Set wether to use a proxy server.
	 *
	 * @param use wether to use a proxy server.
	 */
	public void setUseProxy( boolean use) {
		set( USE_PROXY, use);
	}

	/**
	 * Prefer public identifiers are for catalogs?
	 *
	 * @return true when public identifiers are preferred for catalogs.
	 */
	public boolean isPreferPublicIdentifiers() {
		return getBoolean( PREFER_PUBLIC_IDENTIFIERS, true);
	}

	/**
	 * Set wether to prefer public identifiers for catalogs.
	 *
	 * @param prefer wether to prefer public identifiers for catalogs.
	 */
	public void setPreferPublicIdentifiers( boolean prefer) {
		set( PREFER_PUBLIC_IDENTIFIERS, prefer);
	}
	
	/**
	 * Get the proxy port.
	 *
	 * @return the proxy port.
	 */
	public String getProxyPort() {
		return getText( PROXY_PORT);
	}

	/**
	 * Set the proxy port.
	 *
	 * @param port the proxy port.
	 */
	public void setProxyPort( String port) {
		set( PROXY_PORT, port);
	}

	/**
	 * Get the look and feel class name.
	 *
	 * @return the look and feel class name.
	 */
	public String getLookAndFeel() {
		return getText( LOOK_AND_FEEL);
	}

	/**
	 * Set the look and feel class name.
	 *
	 * @param laf the look and feel class name.
	 */
	public void setLookAndFeel( String laf) {
		set( LOOK_AND_FEEL, laf);
	}

	/**
	 * Get the maximum heap size.
	 *
	 * @return the maximum heap size.
	 */
	public int getMaximumHeapSize() {
		return getInteger( MAXIMUM_HEAP_SIZE, DEFAULT_MAXIMUM_HEAP_SIZE);
	}

	/**
	 * Set the maximum heap size.
	 *
	 * @param size the maximum heap size in MBs.
	 */
	public void setMaximumHeapSize( int size) {
		set( MAXIMUM_HEAP_SIZE, size);
	}

	/**
	 * Get the initial heap size.
	 *
	 * @return the initial heap size.
	 */
	public int getInitialHeapSize() {
		return getInteger( INITIAL_HEAP_SIZE, DEFAULT_INITIAL_HEAP_SIZE);
	}

	/**
	 * Set the initial heap size.
	 *
	 * @param size the initial heap size in MBs.
	 */
	public void setInitialHeapSize( int size) {
		set( INITIAL_HEAP_SIZE, size);
	}

	/**
	 * Set true when external dtds should be loaded.
	 *
	 * @param enabled when external dtds should be loaded.
	 */
	public void setLoadDTDGrammar( boolean enabled) {
		set( LOAD_DTD_GRAMMAR, enabled);
	}

	public boolean isLoadDTDGrammar() {
		return getBoolean( LOAD_DTD_GRAMMAR, false);
	}

	/**
	 * Get the XSLT Processor.
	 *
	 * @return the XSLT Processor property.
	 */
	public String getXSLTProcessor() {
		return getText( XSLT_PROCESSOR, XSLT_PROCESSOR_SAXON_XSLT1);
	}

	/**
	 * Set the XSLT Processor.
	 *
	 * @param processor the XSLT Processor.
	 */
	public void setXSLTProcessor( String processor) {
		set( XSLT_PROCESSOR, processor);
	}

	/**
	 * Get the proxy host.
	 *
	 * @return the proxy host.
	 */
	public String getProxyHost() {
		return getText( PROXY_HOST);
	}

	/**
	 * Set the proxy host.
	 *
	 * @param host the proxy host.
	 */
	public void setProxyHost( String host) {
		set( PROXY_HOST, host);
	}

	/**
	 * Get the browser string.
	 *
	 * @return the browser string.
	 */
	public String getBrowser() {
		return getText( BROWSER);
	}

	/**
	 * Set the browser.
	 *
	 * @param browser the default browser.
	 */
	public void setBrowser( String host) {
		set( BROWSER, host);
	}

	/**
	 * Check to find out if the search matches the case.
	 *
	 * @return true when the search matches case.
	 */
	public boolean isMatchCase() {
		return getBoolean( SEARCH_MATCH_CASE, false);
	}

	/**
	 * Set the match-case search property.
	 *
	 * @param matchCase the search property.
	 */
	public void setMatchCase( boolean matchCase) {
		set( SEARCH_MATCH_CASE, matchCase);
	}

	/**
	 * Check to find out if the search should use the basic configuration.
	 *
	 * @return true when the basic configuration has been selected.
	 */
	public boolean isBasicSearch() {
		return getBoolean( SEARCH_BASIC, true);
	}

	/**
	 * Set the basic search configuration property.
	 *
	 * @param enabled the basic search configuration property.
	 */
	public void setBasicSearch( boolean enabled) {
		set( SEARCH_BASIC, enabled);
	}

	/**
	 * Check to find out if the replace should use the basic configuration.
	 *
	 * @return true when the basic configuration has been selected.
	 */
	public boolean isBasicReplace() {
		return getBoolean( REPLACE_BASIC, true);
	}

	/**
	 * Set the basic replace configuration property.
	 *
	 * @param enabled the basic replace configuration property.
	 */
	public void setBasicReplace( boolean enabled) {
		set( REPLACE_BASIC, enabled);
	}

	/**
	 * Check to find out if the search matches the whole word.
	 *
	 * @return true when the search matches the whole word.
	 */
	public boolean isMatchWholeWord() {
		return getBoolean( SEARCH_MATCH_WHOLE_WORD, false);
	}

	/**
	 * Set the match-whole-word search property.
	 *
	 * @param matchWord the search property.
	 */
	public void setMatchWholeWord( boolean matchWord) {
		set( SEARCH_MATCH_WHOLE_WORD, matchWord);
	}

	/**
	 * Check to find out if the search wraps.
	 *
	 * @return true when the search wraps.
	 */
	public boolean isWrapSearch() {
		return getBoolean( SEARCH_WRAP, true);
	}

	/**
	 * Set the wrap search property.
	 *
	 * @param wrap the search property.
	 */
	public void setWrapSearch( boolean wrap) {
		set( SEARCH_WRAP, wrap);
	}

	/**
	 * Check to find out if the search is a Regular Expression.
	 *
	 * @return true when the search is a Regular Expression.
	 */
	public boolean isRegularExpression() {
		return getBoolean( SEARCH_REGULAR_EXPRESSION, false);
	}

	/**
	 * Set the Regular Expression search property.
	 *
	 * @param regExp the search property.
	 */
	public void setRegularExpression( boolean regExp) {
		set( SEARCH_REGULAR_EXPRESSION, regExp);
	}

	/**
	 * Check to find out if the search uses a xpath.
	 *
	 * @return true when the search uses a xpath.
	 */
	public boolean isXPath() {
		return getBoolean( SEARCH_XPATH, false);
	}

	/**
	 * Set the XPath search property.
	 *
	 * @param xpath the search property.
	 */
	public void setXPath( boolean xpath) {
		set( SEARCH_XPATH, xpath);
	}

	/**
	 * Set the number of spaces to substitute for a tab.
	 *
	 * @param spaces the number of spaces.
	 */
	public void setSpaces( int spaces) {
		set( SPACES, spaces);
	}

	/**
	 * Gets the number of spaces to substitute for a tab.
	 *
	 * @return the number of spaces.
	 */
	public int getSpaces() {
		return getInteger( SPACES, DEFAULT_SPACES);
	}

	/**
	 * Check to find out if the search direction is down.
	 *
	 * @return true when the search direction is down.
	 */
	public boolean isDirectionDown() {
		return getBoolean( SEARCH_DIRECTION_DOWN, true);
	}

	/**
	 * Set the search direction.
	 *
	 * @param downward the search direction.
	 */
	public void setDirectionDown( boolean downward) {
		set( SEARCH_DIRECTION_DOWN, downward);
	}

	/**
	 * Adds a Search string to the properties.
	 *
	 * @param search the search.
	 */
	public void addSearch( String search) {
		searches.add( search);
		save();
	}

	/**
	 * Returns the list of searches.
	 *
	 * @return the list of searches.
	 */
	public Vector getSearches() {
		return searches.get();
	}

	/**
	 * Adds a XPath Search string to the properties.
	 *
	 * @param search the xpath search.
	 */
	public void addXPath( String search) {
		xpaths.add( search);
		save();
	}

	/**
	 * Returns the list of xpaths used in the replace and find dialogs.
	 *
	 * @return the list of xpaths.
	 */
	public Vector getXPaths() {
		return xpaths.get();
	}

	/**
	 * Adds a Replace string to the properties.
	 *
	 * @param replace the replace string.
	 */
	public void addReplace( String replace) {
		replaces.add( replace);
		save();
	}

	/**
	 * Returns the list of replaces.
	 *
	 * @return the list of replaces.
	 */
	public Vector getReplaces() {
		return replaces.get();
	}

	/**
	 * Adds an extension string to the properties.
	 *
	 * @param extension the extension string.
	 */
	public void addExtension( String extension) {
		extensions.add( extension);
		save();
	}

	/**
	 * Removes an extension string to the properties.
	 *
	 * @param extension the extension string.
	 */
	public void removeExtension( String extension) {
		extensions.remove( extension);
		save();
	}

	/**
	 * Returns the list of extensions.
	 *
	 * @return the list of extensions.
	 */
	public Vector getExtensions() {
		return extensions.get();
	}

	/**
	 * Adds a prefix namespace mapping to the properties.
	 *
	 * @param prefix the prefix for the namespace.
	 * @param uri the namespace uri.
	 */
	public void addPrefixNamespaceMapping( String prefix, String uri) {
		prefixNamespaceMappings.add( prefix+":"+uri);
		save();
	}

	/**
	 * Removes a prefix namespace mapping from the properties.
	 *
	 * @param prefix the prefix for the namespace.
	 * @param uri the namespace uri.
	 */
	public void removePrefixNamespaceMapping( String prefix, String namespace) {
		prefixNamespaceMappings.remove( prefix+":"+namespace);
		save();
	}

	/**
	 * Returns the list of prefix namespace mappings.
	 *
	 * @return the list of prefix namespace mappings.
	 */
	public Map getPrefixNamespaceMappings() {
		Vector mappings = prefixNamespaceMappings.get();
		Map result = new HashMap();
		
		for ( int i = 0; i < mappings.size(); i++) {
			String mapping = (String)mappings.elementAt(i);
			int index = mapping.indexOf( ':');
			
			String prefix = mapping.substring( 0, index);
			String uri = mapping.substring( index+1, mapping.length());
			
			// put( key, value)
			result.put( prefix, uri);
		}

		return result;
	}

	/**
	 * Adds a catalog string to the properties.
	 *
	 * @param catalog the catalog string.
	 */
	public void addCatalog( String catalog) {
		catalogs.add( catalog);
		save();
	}

	/**
	 * Removes an catalog string to the properties.
	 *
	 * @param catalog the catalog string.
	 */
	public void removeCatalog( String catalog) {
		catalogs.remove( catalog);
		save();
	}

	/**
	 * Returns the list of catalogs.
	 *
	 * @return the list of catalogs.
	 */
	public Vector getCatalogs() {
		return catalogs.get();
	}

	/**
	 * Adds a xpath Search string to the properties.
	 *
	 * @param xpath search the search.
	 */
	public void addXPathSearch( String search) {
		xpathSearches.add( search);
		save();
	}
	
	/**
	 * Adds an xpath predicate string to the properties.
	 *
	 * @param search The XPath Predicate
	 */
	public void addXPathPredicate(String predicate) {
		xpathPredicates.add(predicate);
		save();
	}
	
	/**
	 * Adds a sort xpath predicate string to the properties.
	 *
	 * @param search The XPath Predicate
	 */
	public void addSortXPathPredicate(String predicate) {
		sortXPathPredicates.add(predicate);
		save();
	}
	
	/**
	 * Adds an xpath tools string to the properties.
	 *
	 * @param search The XPath tools
	 */
	public void addXPathTools(String predicate) {
		xpathTools.add(predicate);
		save();
	}

	/**
	 * Returns the list of xpath searches.
	 *
	 * @return the list of xpath searches.
	 */
	public Vector getXPathSearches() {
		return xpathSearches.get();
	}
	
	/**
	 * Returns the list of database drivers.
	 *
	 * @return the list of database drivers.
	 */
	public Vector getDatabaseDrivers() {
		return databaseDrivers.get();
	}
	
	/**
	 * Returns the list of database connections.
	 *
	 * @return the list of database connections.
	 */
	public Vector getDatabaseConnections() {
		return databaseConnections.get();
	}
	
	/**
	 * Returns the list of xpath predicates.
	 *
	 * @return the list of xpath predicates.
	 */
	public Vector getXPathPredicates() {
		return xpathPredicates.get();
	}
	
	/**
	 * Returns the list of sort xpath predicates.
	 *
	 * @return the list of sort xpath predicates.
	 */
	public Vector getSortXPathPredicates() {
		return sortXPathPredicates.get();
	}
	
	/**
	 * Returns the list of xpath for the tools.
	 *
	 * @return the list of xpath.
	 */
	public Vector getXPathTools() {
		return xpathTools.get();
	}
	
	/**
	 * Returns the previously used find in files folder
	 * 
	 * @return the last folder
	 */
	public String getFindInFilesFolder() {
		String result = "";
		Vector docs = findInFilesFolder.get();
		
		if ( docs.size() > 0) {
			result = (String)docs.elementAt(0);
		}

		return result;
	}
	
	/**
	 * Sets the the find in files folder.
	 *
	 * @param the database connection
	 */
	public void setFindInFilesFolder( String newFolder) {
		findInFilesFolder.add(newFolder);
		save();
	}


	/**
	 * Returns the x/y position on screen of the editor.
	 *
	 * @return the x/y position on screen.
	 */
	public Point getPosition() {
		// Gather the default values...
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screen = kit.getScreenSize();
		Dimension app = getDimension();
		
		int posx = (screen.width - app.width)/2;
		int posy = (screen.height - app.height)/2;
		
//		if ( posx < 0) {
//			posx = 0;
//		}
		
//		if ( posy < 0) {
//			posy = 0;
//		}

		return new Point( getInteger( XPOS, posx), getInteger( YPOS, posy));
	}

	/**
	 * Sets the x/y position on screen of the editor.
	 *
	 * @return the x/y position on screen.
	 */
	public void setPosition( Point point) {
		set( XPOS, point.x);
		set( YPOS, point.y);
	}
	
//	private static Dimension getScreenSize() {
//		if ( screenSize == null) {
//			Toolkit kit = Toolkit.getDefaultToolkit();
//			Dimension screen = kit.getScreenSize();
//			Dimension app = getDimension();
//		}
//	}

	/**
	 * Returns the dimension on screen of the editor.
	 *
	 * @return the dimension on screen.
	 */
	public Dimension getDimension() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screen = kit.getScreenSize();

		return new Dimension( Math.min( getInteger( WIDTH, DEFAULT_WIDTH), screen.width), Math.min( getInteger( HEIGHT, DEFAULT_HEIGHT), screen.height));
	}

	/**
	 * Returns the path to the last opened document.
	 *
	 * @return the path to the last opened document.
	 */
	public String getLastOpenedDocument() {
		String result = "";
		Vector docs = lastDocuments.get();
		
		if ( docs.size() > 0) {
			result = (String)docs.elementAt(0);
		}

		return result;
	}

	/**
	 * Returns the path to the last opened documents.
	 *
	 * @return the path to the last opened documents.
	 */
	public Vector getLastOpenedDocuments() {
		return lastDocuments.get();
	}

	/**
	 * Sets the path to the last opened document.
	 *
	 * @param path the path to the last opened document.
	 */
	public void setLastOpenedDocument( String path) {
		lastDocuments.add( path);
		save();
	}

	/**
	 * Returns the path to the last opened URL.
	 *
	 * @return the path to the last opened URL.
	 */
	public URL getLastOpenedURL() {
		Vector urls = lastURLs.get();
		
		if ( urls.size() > 0) {
			return URLUtilities.decrypt( (String)urls.elementAt(0));
		}

		return null;
	}

	/**
	 * Returns the list to the last opened URLS.
	 *
	 * @return the list to the last opened URLs.
	 */
	public Vector getLastOpenedURLs() {
		Vector urls = lastURLs.get();
		Vector newURLs = new Vector();

		for ( int i = 0; i < urls.size(); i++) {
			newURLs.addElement( URLUtilities.decrypt( (String)urls.elementAt(i)));
		}

		return newURLs;
	}

	/**
	 * Sets the last opened url.
	 *
	 * @param url the last opened url.
	 */
	public void setLastOpenedURL( URL url) {
		lastURLs.add( URLUtilities.encrypt( url));
		save();
	}
	
	/**
	 * Sets the the database driver.
	 *
	 * @param the database driver
	 */
	public void setDatabaseDrivers( String newDriver) {
		databaseDrivers.add(newDriver);
		save();
	}
	
	/**
	 * Sets the the database connection.
	 *
	 * @param the database connection
	 */
	public void setDatabaseConnections( String newConnection) {
		databaseConnections.add(newConnection);
		save();
	}

	/**
	 * Returns the last opened grammar index.
	 *
	 * @return the last opened grammar index -1 when no grammar selected.
	 */
	public int getLastOpenedGrammar() {
		return getInteger( LAST_OPENED_GRAMMAR, -1);
	}

	/**
	 * Set the last opened grammar.
	 *
	 * @param index the index of the last opened grammar. (-1 for all files)
	 */
	public void setLastOpenedGrammar( int index) {
		set( LAST_OPENED_GRAMMAR, index);
	}

	/**
	 * Sets the dimension on screen of the editor.
	 *
	 * @return the dimension on screen.
	 */
	public void setDimension( Dimension dimension ) {
		set( WIDTH, dimension.width);
		set( HEIGHT, dimension.height);
	}

	/**
	 * Set the split divider location.
	 *
	 * @param location the split divider location.
	 */
	public void setDividerLocation( int location) {
		set( DIVIDER_LOCATION, location);
	}

	/**
	 * Gets the location of the split divider.
	 *
	 * @return the location of the split divider.
	 */
	public int getDividerLocation() {
		return getInteger( DIVIDER_LOCATION, DEFAULT_DIVIDER_LOCATION);
	}

	/**
	 * Set the split divider location.
	 *
	 * @param location the split divider location.
	 */
	public void setTopDividerLocation( int location) {
		set( TOP_DIVIDER_LOCATION, location);
	}

	/**
	 * Gets the location of the split divider.
	 *
	 * @return the location of the split divider.
	 */
	public int getTopDividerLocation() {
		return getInteger( TOP_DIVIDER_LOCATION, DEFAULT_TOP_DIVIDER_LOCATION);
	}

	/**
	 * Returns the scenario properties list.
	 *
	 * @return the scenario properties.
	 */
	public Vector getScenarioProperties() {
		Vector result = new Vector();
		Vector list = getProperties( ScenarioProperties.SCENARIO_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
		
			result.addElement( new ScenarioProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a scenario properties object to this element.
	 *
	 * @param props the scenario properties.
	 */
	public void addScenarioProperties( ScenarioProperties props) {
		add( props);
		save();
	}

	/**
	 * Removes a scenario properties object from this element.
	 *
	 * @param props the scenario properties.
	 */
	public void removeScenarioProperties( ScenarioProperties props) {
		remove( props);
		save();
	}
	
	Vector bookmarks = null;

	/**
	 * Returns the bookmark list.
	 *
	 * @return the bookmarks.
	 */
	public Vector getBookmarks() {
		if ( bookmarks == null) {
			bookmarks = new Vector();
			Vector list = getProperties( Bookmark.BOOKMARK);
			
			for ( int i = 0; i < list.size(); i++) {
				Bookmark bm = new Bookmark( (Properties)list.elementAt(i));
				
				if ( bm.getURL() != null && bm.getURL().trim().length() > 0) {
					bookmarks.addElement( new Bookmark( (Properties)list.elementAt(i)));
				} else {
					removeBookmark( bm);
				}
			}
		}

		return bookmarks;
	}

	/**
	 * Adds a Bookmark object to this element.
	 *
	 * @param props the Bookmark  properties.
	 */
	public void addBookmark( Bookmark props) {
		add( props);
		bookmarks.addElement( props);
		save();
	}

	/**
	 * Removes a Bookmark object from this element.
	 *
	 * @param props the Bookmark.
	 */
	public void removeBookmark( Bookmark props) {
		remove( props);
		bookmarks.removeElement( props);
		save();
	}

	/**
	 * Returns the template properties list.
	 *
	 * @return the template properties.
	 */
	public Vector getTemplateProperties() {
		Vector result = new Vector();
		Vector list = getProperties( TemplateProperties.TEMPLATE_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
			result.addElement( new TemplateProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a template properties object to this element.
	 *
	 * @param props the template properties.
	 */
	public void addTemplateProperties( TemplateProperties props) {
		add( props);
		save();
	}

	/**
	 * Removes a template properties object from this element.
	 *
	 * @param props the template properties.
	 */
	public void removeTemplateProperties( TemplateProperties props) {
		remove( props);
		save();
	}

	/**
	 * Returns the grammar properties list.
	 *
	 * @return the grammar properties.
	 */
	public Vector getGrammarProperties() {
		Vector result = new Vector();
		Vector list = getProperties( GrammarProperties.GRAMMAR_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
		
			result.addElement( new GrammarProperties( this, (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a grammar properties object to this element.
	 *
	 * @param props the grammar properties.
	 */
	public void addGrammarProperties( GrammarProperties props) {
		add( props);
		save();
	}

	/**
	 * Adds a grammar properties object to this element.
	 *
	 * @param props the grammar properties.
	 */
	public void removeGrammarProperties( GrammarProperties props) {
		remove( props);
		save();
	}

	/**
	 * Returns the project properties list.
	 *
	 * @return the project properties.
	 */
	public Vector getProjectProperties() {
		Vector result = new Vector();
		Vector list = getProperties( ProjectProperties.PROJECT_PROPERTIES);
		
		for ( int i = 0; i < list.size(); i++) {
		
			result.addElement( new ProjectProperties( (Properties)list.elementAt(i)));
		}
		
		return result;
	}

	/**
	 * Adds a project properties object to this element.
	 *
	 * @param props the project properties.
	 */
	public void addProjectProperties( ProjectProperties props) {
		add( props);
		save();
	}

	/**
	 * Adds a project properties object to this element.
	 *
	 * @param props the project properties.
	 */
	public void removeProjectProperties( ProjectProperties props) {
		remove( props);
		save();
	}

	/**
	 * Returns the text preferences.
	 *
	 * @return the text preferences.
	 */
	public TextPreferences getTextPreferences() {
		return textPreferences;
	}
	
	/**
	 * Returns the text preferences.
	 *
	 * @return the text preferences.
	 */
	public KeyPreferences getKeyPreferences() {
		return keyPreferences;
	}

	/**
	 * Returns the print preferences.
	 *
	 * @return the print preferences.
	 */
	public PrintPreferences getPrintPreferences() {
		return printPreferences;
	}

	/**
	 * Returns the security preferences.
	 *
	 * @return the security preferences.
	 */
	public SecurityPreferences getSecurityPreferences() {
		return securityPreferences;
	}

	/**
	 * Returns the properties for the SOAP tool.
	 *
	 * @return the SOAP properties.
	 */
	public SOAPProperties getSOAPProperties() {
		return soapProperties;
	}

	/**
	 * Returns the properties for the helper.
	 *
	 * @return the helper properties.
	 */
	public HelperProperties getHelperProperties() {
		return helperProperties;
	}

	/**
	 * Returns the properties for the debugger.
	 *
	 * @return the debugger properties.
	 */
	public DebuggerProperties getDebuggerProperties() {
		return debuggerProperties;
	}

	/**
	 * Returns the properties for the navigator.
	 *
	 * @return the navigator properties.
	 */
	public NavigatorProperties getNavigatorProperties() {
		return navigatorProperties;
	}

	/**
	 * Returns the properties for the text editor.
	 *
	 * @return the text editor properties.
	 */
	public EditorProperties getEditorProperties() {
		return editorProperties;
	}

	/**
	 * Returns the properties for the DOM editor.
	 *
	 * @return the DOM editor properties.
	 */
	public DesignerProperties getDesignerProperties() {
		return designerProperties;
	}

	/**
	 * Returns the properties for the XML viewer.
	 *
	 * @return the XML viewer properties.
	 */
	public ViewerProperties getViewerProperties() {
		return viewerProperties;
	}

	/**
	 * Returns the properties for the XML browser.
	 *
	 * @return the XML browser properties.
	 */
	public BrowserProperties getBrowserProperties() {
		return browserProperties;
	}

	/**
	 * Returns the properties for the Schema viewer.
	 *
	 * @return the Schema viewer properties.
	 */
	public SchemaViewerProperties getSchemaViewerProperties() {
		return schemaViewerProperties;
	}
	
	/**
	 * Returns the properties for the Grid.
	 *
	 * @return the Grid properties.
	 */
	/*public GridProperties getGridProperties() {
		return gridProperties;
	}*/
	
	/**
	 * Returns the properties for the plugin view.
	 *
	 * @return the plugin view properties.
	 */
	public XElement getPluginViewProperties(String propertiesIdentifier) {
		
		return(get( propertiesIdentifier));		
	}

	
	/**
	 * tjc 16102008
	 * Doesnt save to disk anymore
	 * Saves the configuration properties.
	 * 
	 */
	public void save() {
		
		
		// Make sure the properties are up to date...
		designerProperties.update();
		editorProperties.update();
		viewerProperties.update();
		browserProperties.update();
		schemaViewerProperties.update();
		soapProperties.update();
		//gridProperties.update();
		
		if(getPluginProperties() != null) {
			for(int cnt=0;cnt<getPluginProperties().size();++cnt) {
				if(getPluginProperties().get(cnt) instanceof PluginViewProperties) {
					((PluginViewProperties)getPluginProperties().get(cnt)).save();	
				}
			}
		}
		
		if(getPropertyFiles() != null) {
			for(int cnt=0;cnt<getPropertyFiles().size();++cnt) {
				if(getPropertyFiles().get(cnt) instanceof PropertiesFile) {
					((PropertiesFile)getPropertyFiles().get(cnt)).save();	
				}
			}
		}
		
//		Vector bms = getBookmarks();
//		for ( int i = 0; i < bms.size(); i++) {
//			((Bookmark)bms.elementAt(i)).update();
//		}
		
		

		debuggerProperties.update();

		helperProperties.update();
		navigatorProperties.update();

		xercesProperties.update();
		replaces.update();
		searches.update();
		xpaths.update();
		xpathSearches.update();
		xpathPredicates.update();
		sortXPathPredicates.update();
		xpathTools.update();
		lastDocuments.update();
		prefixNamespaceMappings.update();
		lastURLs.update();
		extensions.update();
		catalogs.update();
		databaseDrivers.update();
		databaseConnections.update();
		findInFilesFolder.update();

		/*try {
			setDefaultNamespace( document.getRoot());
			XMLUtilities.write( document.getDocument(), document.getURL());			
			
		} catch( Exception e) {
			e.printStackTrace();
		}*/
	}
	
	
	/**
	 * Saves the properties to disk, should only be done
	 * when shutting down the editor to avoid unneeded writes to disk
	 */
	public void saveToDisk() {
		try {
			//setDefaultNamespace( document.getRoot());
			XMLUtilities.write( document.getDocument(), document.getURL());			
			
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		if(getPluginProperties() != null) {
			for(int cnt=0;cnt<getPluginProperties().size();++cnt) {
				if(getPluginProperties().get(cnt) instanceof PluginViewProperties) {
					((PluginViewProperties)getPluginProperties().get(cnt)).saveToDisk();	
				}
			}
		}
		
		if(getPropertyFiles() != null) {
			for(int cnt=0;cnt<getPropertyFiles().size();++cnt) {
				if(getPropertyFiles().get(cnt) instanceof PropertiesFile) {
					((PropertiesFile)getPropertyFiles().get(cnt)).saveToDisk();	
				}
			}
		}
	}
	
	/*private void setDefaultNamespace( XElement element) {
		XElement[] elements = element.getElements();
		
		for ( int i = 0; i < elements.length; i++) {
			setDefaultNamespace( elements[i]);
		}
		
		QName qname = new QName(element.getName(), Namespace.get( "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/"));
		element.remove(element.getNamespace());
		element.setQName(qname);
		//element.setNamespace( Namespace.get( "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/"));
	}*/

	/**
	 * @param pluginProperties the pluginProperties to set
	 */
	public void setPluginProperties(List pluginProperties) {

		this.pluginProperties = pluginProperties;
	}

	/**
	 * @return the pluginProperties
	 */
	public List getPluginProperties() {

		return pluginProperties;
	}
	
	public boolean isWindowMaximised() {
		return getBoolean( WINDOW_MAXIMISED, true);
	}
	
	public void setWindowMaximised(boolean maximised) {
		set( WINDOW_MAXIMISED, maximised);
	}

	public void setXercesProperties(XercesProperties xercesProperties) {
		this.xercesProperties = xercesProperties;
	}

	public XercesProperties getXercesProperties() {
		return xercesProperties;
	}
	
	public void setSavePropertiesInterval(long interval) {
		set( SAVE_PROPERTIES_INTERVAL, interval);
	}
	
	public long getSavePropertiesInterval() {
		return( getLong(SAVE_PROPERTIES_INTERVAL, DEFAULT_SAVE_PROPERTIES_INTERVAL));
	}

	public void setPropertyFiles(List propertyFiles) {
		this.propertyFiles = propertyFiles;
	}

	public List getPropertyFiles() {
		if(propertyFiles == null) {
			propertyFiles = new ArrayList();
		}
		return propertyFiles;
	}
	
}