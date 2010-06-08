/*
 * $Id: Main.java,v 1.22 2005/09/05 13:55:11 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.bounce.net.DefaultAuthenticator;
import org.xml.sax.SAXParseException;

import com.cladonia.util.loader.ExtensionClassLoader;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XDocumentFactory;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xml.transform.ScenarioUtilities;
import com.cladonia.xngreditor.actions.ImportPreferencesAction;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.NamespaceProperties;
import com.cladonia.xngreditor.project.DocumentProperties;
import com.cladonia.xngreditor.project.FolderProperties;
import com.cladonia.xngreditor.project.ProjectProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.template.TemplateProperties;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;

/**
 * The main class, used to start the XML+ editor application.
 *
 * @version	$Revision: 1.22 $, $Date: 2005/09/05 13:55:11 $
 * @author Dogsbay
 */
public class Main {
	static final int XMLPLUS_PORT = 9601;

	private static final boolean DEBUG = false;
	private static DefaultAuthenticator authenticator = null;

	public static final String DEFAULT_TEMPLATES_LOCATION	= "templates"+File.separator+"all.templates";
	public static final String DEFAULT_TYPES_LOCATION		= "types"+File.separator+"all.types";
	public static final String DEFAULT_SCENARIOS_LOCATION	= "scenarios"+File.separator+"all.scenarios";
	public static final String DEFAULT_SAMPLES_LOCATION		= "projects"+File.separator+"all.projects";
	public static final String NEW_SAMPLES_LOCATION		= "projects"+File.separator+"new.projects";
	public static final String PLUGINS_LOCATION		= "plugins";
		
	public static final String XMLPLUS_HOME = System.getProperty( "user.home")+File.separator+".xngreditor"+File.separator;
	public static final String OLD_PROPERTIES_FILE = ".xngreditor.xml";

	public static final String XNGR_EDITOR_HOME = System.getProperty( "user.home")+File.separator+".xngr"+File.separator;
	public static final String V20_PROPERTIES_FILE = ".xngr-editor.xml";
	public static final String V30_PROPERTIES_FILE = ".xngr-editor-v30.xml";
	public static final String V31_PROPERTIES_FILE = ".xngr-editor-v31.xml";
	public static final String V32_PROPERTIES_FILE = ".xngr-editor-v32.xml";
	public static final String PROPERTIES_FILE = ".xngr-editor-v33.xml";

	private ConfigurationProperties properties = null;

	private ExchangerEditor editor = null;
	private XSLTDebuggerFrame debugger = null;

	/**
	 * Constructor for the main XML+ class, constructs the main frame 
	 * and shows the Splash screen until the application has been completely loaded.
	 */
	public Main( String file) {
		launch( null, file);
	}
	
	public Main( ExtensionClassLoader loader, String file) {
		if(DEBUG) System.out.println("Main::Main(loader, file): ("+loader+", "+file+")");
		launch( loader, file);
		Thread.currentThread().setContextClassLoader(loader);
	}
	
	
	private void start( ExtensionClassLoader loader, String file) {
		if(DEBUG) System.out.println("Main::start(loader, file): ("+loader+", "+file+")");
		if ( !isDebug()) {
			redirectOutput();
		} else {
			System.out.println( "******************* WARNING *******************");
			System.out.println( "***** Exchanger XML Editor Debug Version! *****");
			System.out.println( "*****      Do not use for Production.     *****");
			System.out.println( "***********************************************\n\n");
		}
		
		properties	= getProperties();

		try {
			String laf = properties.getLookAndFeel();
			
			if ( laf != null && laf.length() > 0) {
				UIManager.setLookAndFeel( laf);
			}
			
			
			//long time = System.currentTimeMillis();
	
			//PlasticLookAndFeel.setMyCurrentTheme(new com.jgoodies.looks.plastic.theme.SkyPink());

			//try {
		    //  UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
		   //} catch (Exception e) {System.out.println("Cannot set LAF");}

			  String OS = System.getProperty("os.name").toLowerCase();
			  // System.out.println(OS);
			  if ( (OS.indexOf("nt") > -1)
			  		|| (OS.indexOf("windows 9") > -1)
			         || (OS.indexOf("windows 2000") > -1 )
			         || (OS.indexOf("windows xp") > -1) 
			         || (OS.indexOf("windows vista") > -1) ) {

			    
				//TODO GMCG check for Linux
				if ( (laf == null || !laf.equals( "com.jgoodies.looks.windows.WindowsLookAndFeel")) && (System.getProperty("mrj.version") == null)) {
					UIManager.installLookAndFeel( "JGoodies Windows", "com.jgoodies.looks.windows.WindowsLookAndFeel");
				}
				
				
				if ( (laf == null || !laf.equals( "com.jgoodies.looks.plastic.PlasticLookAndFeel")) && (System.getProperty("mrj.version") == null)) {
					UIManager.installLookAndFeel( "JGoodies Plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel");
				}
				if ( (laf == null || !laf.equals( "com.jgoodies.looks.plastic.Plastic3DLookAndFeel")) && (System.getProperty("mrj.version") == null)) {
					UIManager.installLookAndFeel( "JGoodies Plastic3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
				}
				if ( (laf == null || !laf.equals( "com.jgoodies.looks.plastic.PlasticXPLookAndFeel")) && (System.getProperty("mrj.version") == null)) {
					UIManager.installLookAndFeel( "JGoodies PlasticXP", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
				}
			    
			}

			
			if ( (laf == null || !laf.equals( "com.incors.plaf.kunststoff.KunststoffLookAndFeel")) && (System.getProperty("mrj.version") == null)) {
				UIManager.installLookAndFeel( "Kunststoff", "com.incors.plaf.kunststoff.KunststoffLookAndFeel");
			}

			

			//int s = (int)(System.currentTimeMillis()-time)/1000;
			//int ms   = (int)(System.currentTimeMillis()-time)%1000;

			//System.out.println("Loaded LAF: " + s + " seconds " + ms + " milliseconds");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		ClassLoader classLoader = getClass().getClassLoader();
		
		if ( classLoader instanceof ExtensionClassLoader) {
			Vector extensions = properties.getExtensions();
			
			for ( int i = 0; i < extensions.size(); i++) {
				((ExtensionClassLoader)classLoader).addExtension( new File( (String)extensions.elementAt(i)));
			}
		}
		
		
		
		Vector catalogs = properties.getCatalogs();
		StringBuffer catalogFiles = new StringBuffer();
		
		for ( int i = 0; i < catalogs.size(); i++) {
			catalogFiles.append( (String)catalogs.elementAt(i));
			catalogFiles.append( ";");
		}
		
		System.setProperty( "xml.catalog.ignoreMissing", "true");
		System.setProperty( "xml.catalog.files", catalogFiles.toString());
		
		if ( properties.isPreferPublicIdentifiers()) {
			System.setProperty( "xml.catalog.prefer", "public");
		} else {
			System.setProperty( "xml.catalog.prefer", "system");
		}

		String port = properties.getProxyPort();
		String host = properties.getProxyHost();
		
		if ( properties.isUseProxy()) {
			System.setProperty( "http.proxyHost", host);
			System.setProperty( "http.proxyPort", port);
		}

		String browser = properties.getBrowser();
		
		if ( browser != null && browser.trim().length() > 0) {
			System.setProperty( "org.bounce.browser", browser);
		}

		System.setProperty( "javax.xml.transform.TransformerFactory", properties.getXSLTProcessor());
		System.setProperty( "org.xml.sax.driver", "org.apache.xerces.parsers.SAXParser");
		System.setProperty( "http.agent", Identity.getIdentity().getTitle()+"/"+Identity.getIdentity().getVersion());

		XDocumentFactory.getInstance().setXPathNamespaceURIs( properties.getPrefixNamespaceMappings());
		
		XMLUtilities.setLoadDTDGrammar( properties.isLoadDTDGrammar());
//		XMLUtilities.setResolveEntities( properties.isResolveEntities());

		IconFactory.setProperties( properties);

		
		
		editor = new ExchangerEditor( loader, properties);
		debugger = new XSLTDebuggerFrame( properties, editor);
		editor.setDebugger( debugger);
		//editor.setExtensionClassLoader(loader);
		if(DEBUG) System.out.println("Main::Start - extensionClassLoader: "+editor.getExtensionClassLoader());
		if(DEBUG) System.out.println("Main::Start - exchangerEditor.getClassLoader(): "+editor.getClassLoader());
		
		// set network properties...
		Authenticator.setDefault( createDefaultAuthenticator( editor));

		MessageHandler.init( editor);
		FileUtilities.init( editor, editor, properties);
		ScenarioUtilities.init( editor, properties);
		
		// >>>
		//checkLicense( file, true);
		startApplication( file);
	}
	
	
	
	public static DefaultAuthenticator createDefaultAuthenticator( ExchangerEditor editor) {
		authenticator = new DefaultAuthenticator( editor);

		return authenticator;
	}

	public static DefaultAuthenticator getDefaultAuthenticator() {
		return authenticator;
	}
	
	/*public void checkLicense( String file, boolean first) {
		if (DEBUG) System.out.println( "Main.checkLicense( "+file+")");
		Exception exception = null;
		LicenseManager licenseManager = null;
		
		try {
			licenseManager = LicenseManager.getInstance();
			licenseManager.isValid( KeyGenerator.generate(2), "Exchanger XML Editor");

		} catch( Exception ex) {
			exception = ex;
		}


		if (first && Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
		  //showLicenseInformation( file);
			CommunityLicenseInformationDialog dialog = new CommunityLicenseInformationDialog( editor, this, file, licenseManager, exception);

			while ( !dialog.isVisible()) {
				//dialog.setVisible(true);
				dialog.show();
			}
		}	
		else if (first && licenseManager.getLicenseType().equals( LicenseType.LICENSE_TEMPORARY)) {
			LicenseInformationDialog dialog = new LicenseInformationDialog( editor, this, file, licenseManager, exception);

			while ( !dialog.isVisible()) {
				//dialog.setVisible(true);
				dialog.show();
			}
		} else if ( exception != null && first) {
			LicenseInformationDialog dialog = new LicenseInformationDialog( editor, this, file, licenseManager, exception);

			while ( !dialog.isVisible()) {
				//dialog.setVisible(true);
				dialog.show();
			}
		} else if ( exception != null) {
			System.exit(1);
			return;
		} else {
			// >>>
			showLicenseInformation( file);
		}
	}*/
		
	/*public void showLicenseInformation( String file) {
		if (DEBUG) System.out.println( "Main.showLicenseInformation( "+file+")");
		LicenseManager licenseManager = null;
		
		try {
			licenseManager = LicenseManager.getInstance();
			licenseManager.isValid( KeyGenerator.generate(2), "Exchanger XML Editor");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return;
		}

		String licenseType = licenseManager.getLicenseType();
		if ( !properties.isLicenseAccepted( licenseType)) {
			int type = LicenseDialog.TEMPORARY;
			
			if ( licenseType.equals( LicenseType.LICENSE_PERMANENT)) {
				type = LicenseDialog.PERMANENT;
			} else if ( licenseType.equals( LicenseType.LICENSE_COMPLIMENTARY)) {
				type = LicenseDialog.COMPLIMENTARY;
			} else if ( licenseType.equals( LicenseType.LICENSE_ACADEMIC)) {
				type = LicenseDialog.ACADEMIC;
			}
			else if ( licenseType.equals( LicenseType.LICENSE_LITE)) {
				type = LicenseDialog.LITE;
			}

			LicenseDialog dialog = new LicenseDialog( editor, this, file, type);
			while ( !dialog.isVisible()) {
				dialog.setVisible(true);
			}
		} else {
			// >>>
			startApplication( file);
		}
	}
		
		
	public void acceptLicense( String file) {
		if (DEBUG) System.out.println( "Main.acceptLicense( "+file+")");
		LicenseManager licenseManager = null;
		
		try {
			licenseManager = LicenseManager.getInstance();
			licenseManager.isValid( KeyGenerator.generate(2), "Exchanger XML Editor");
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
			return;
		}

		properties.setLicenseAccepted( licenseManager.getLicenseType());

		// >>>
		startApplication( file);
	}*/


	private void startApplication( final String file) {
		if (DEBUG) System.out.println( "Main.startApplication( "+file+")");
		// Start the splash screen...
		//Splash splash = new Splash( editor, Identity.getIdentity());

		try {
			//splash.start();

			if ( file.equals( "-debugger")) {
				//debugger.setVisible(true);
				debugger.show();
			} else {
				editor.setVisible(true);
				
				if ( file != null) {
					Runnable runner = new Runnable() {
						public void run() {
							try {
								open( file);
							}catch (Exception e) {
								
								e.printStackTrace();
							}
						}
					};
					
					Thread thread = new Thread(runner);
					thread.start();
				}
			}
		} finally {
			//splash.stop( 2000);
		}
	}
	
	private ConfigurationProperties getProperties() {
		ExchangerDocument document = null;
		boolean firstTime = false;

		File dir = new File( XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File( dir, PROPERTIES_FILE);
		URL url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
			} catch (SAXParseException e) {
				MessageHandler.showError("Exchanger XML Editor Error\nThe configuration file seems to be corrupt, \nplease delete this file:\n\t"+url.toExternalForm()+"\nand try to start Exchanger Editor again", "Configuration File Error");
				return(null);
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
				return null;
			}
		} else {
			// Old document check for version 1.0 / 2.0!
			ExchangerDocument oldDocument = getOldProperties();

			if ( oldDocument != null) {
				oldDocument.setURL( url);
				ImportPreferencesAction importPreferencesAction = new ImportPreferencesAction(this.editor, new ConfigurationProperties(oldDocument), document);
				importPreferencesAction.execute();
				
				//document = oldDocument;
			} else {
				firstTime = true;
				//XElement root = new XElement( "xngr", "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
				XElement root = new XElement( "xngr");
				root.setText( "\n");
				document = new ExchangerDocument( url, root);
			}
		}
		
		XElement root = document.getRoot();
		//String namespaceURI = root.getNamespaceURI();
		
		ConfigurationProperties properties = new ConfigurationProperties( document);
		if ( !firstTime) {
			addNewSamples( properties);
		}		
		else if ( !firstTime) {
			addNewSamples( properties);
		}
		else if ( !firstTime) {
			addNewSamples( properties);
		}		
		else if ( !firstTime) {
			addDefaultTemplates( properties);
			addDefaultTypes( properties);
			addDefaultSamples( properties);
			addDefaultScenarios( properties);
		} else if ( firstTime) {
			
			
			addDefaultTemplates( properties);
			addDefaultTypes( properties);
			addDefaultSamples( properties);
			addDefaultScenarios( properties);
		}

		
		return properties;
	}
	
	private void addDefaultTemplates( ConfigurationProperties properties) {
		try {
			File file = new File( getInstallationPath( DEFAULT_TEMPLATES_LOCATION));
			
			if ( file.exists()) {
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();

				if ( root.getName().equals( "templates")) {
					XElement[] templates = root.getElements( "template");
					
					if ( templates != null) {
						for ( int i = 0; i < templates.length; i++) {
							TemplateProperties template = new TemplateProperties( url, templates[i]);
							TemplateProperties existingTemplate = getExistingTemplate( properties, template);
							
							// remove previous templates
							if ( existingTemplate != null) {
								properties.removeTemplateProperties( existingTemplate);
							}

							properties.addTemplateProperties( template);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}
	
	private void addDefaultSamples( ConfigurationProperties properties) {
		try {
			File file = new File( getInstallationPath( DEFAULT_SAMPLES_LOCATION));
			
			if ( file.exists()) {
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();
				
				if ( root.getName().equals( "xngr")) {
					XElement projects = root.getElement( "projects");
					addProjects( properties, url, projects);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}

	private void addNewSamples( ConfigurationProperties properties) {
		try {
			File file = new File( getInstallationPath( NEW_SAMPLES_LOCATION));
			
			if ( file.exists()) {
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();
				
				if ( root.getName().equals( "xngr")) {
					XElement projects = root.getElement( "projects");
					addProjects( properties, url, projects);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}

	
	private void addProjects( ConfigurationProperties properties, URL base, XElement root) {
		try {
			if ( root.getName().equals( "projects")) {
				XElement[] projects = root.getElements( "project");
				
				if ( projects != null) {
					for ( int i = 0; i < projects.length; i++) {
						String name = projects[i].getAttribute( "name");
						
						if ( name != null && name.trim().length() > 0) {
							Vector list = properties.getProjectProperties();
							if ( list != null) {
								for ( int j = 0; j < list.size(); j++) {
									ProjectProperties p = (ProjectProperties)list.elementAt( j);
									
									if ( name.equals( p.getName())) {
										properties.removeProjectProperties( p);
									}
								}
							}

							ProjectProperties project = new ProjectProperties( name);
	
							properties.addProjectProperties( project);
			
							XElement[] documents = projects[i].getElements( "document");
							addDocuments( base, project, documents);
								
							XElement[] folders = projects[i].getElements( "folder");
							addFolders( base, project, folders);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addDocuments( URL base, FolderProperties folder, XElement[] documents) {
		// add the folder...
		
		for ( int i = 0; i < documents.length; i++) {
			File file = URLUtilities.toFile( URLUtilities.resolveURL( base, documents[i].getAttribute( "src")));
		
			if ( file != null && file.isFile()) {
				folder.addDocumentProperties( new DocumentProperties( file));
			} else {
				URL url = URLUtilities.toURL( documents[i].getAttribute( "src"));
				folder.addDocumentProperties( new DocumentProperties( url));
			}
		}
	}

	private void addFolders( URL base, FolderProperties parent, XElement[] folders) {
		// add the folder...
		
		for ( int i = 0; i < folders.length; i++) {
			String name = folders[i].getAttribute( "name");
			
			if ( name != null && name.trim().length() > 0) {
				FolderProperties folder = new FolderProperties( name);
				parent.addFolderProperties( folder);
				
				XElement[] documents = folders[i].getElements( "document");
				addDocuments( base, folder, documents);
			
				XElement[] children = folders[i].getElements( "folder");
				addFolders( base, folder, children);
			}
		}
	}

	private void addDefaultTypes( ConfigurationProperties properties) {
		try {
			File file = new File( getInstallationPath( DEFAULT_TYPES_LOCATION));
			
			if ( file.exists()) {
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();

				if ( root.getName().equals( "types")) {
					XElement[] types = root.getElements( "type");
					
					if ( types != null) {
						for ( int i = 0; i < types.length; i++) {
							GrammarProperties type = new GrammarProperties( properties, url, types[i]);
							GrammarProperties existingType = getExistingGrammar( properties, type);
							
							// remove previous grammars
							if ( existingType != null) {
								properties.removeGrammarProperties( existingType);
							}

							if ( !StringUtilities.isEmpty( type.getNamespacePrefix()) && !StringUtilities.isEmpty( type.getNamespace())) {
								properties.addPrefixNamespaceMapping( type.getNamespacePrefix(), type.getNamespace());
							}

							Vector namespaces = type.getNamespaces();
							for ( int j = 0; j < namespaces.size(); j++) {
								NamespaceProperties namespace = (NamespaceProperties)namespaces.elementAt(j);

								if ( !StringUtilities.isEmpty( namespace.getPrefix()) && !StringUtilities.isEmpty( namespace.getURI())) {
									properties.addPrefixNamespaceMapping( namespace.getPrefix(), namespace.getURI());
								}
							}

							properties.addGrammarProperties( type);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}
	
	private GrammarProperties getExistingGrammar( ConfigurationProperties properties, GrammarProperties grammar) {
		Vector grammars = properties.getGrammarProperties();
		
		for ( int i = 0; i < grammars.size(); i++) {
			GrammarProperties props = (GrammarProperties)grammars.elementAt(i);
			
			if ( grammar.getDescription().equals( props.getDescription())) {
				return props;
			}
		}
		
		return null;
	}

	private TemplateProperties getExistingTemplate( ConfigurationProperties properties, TemplateProperties template) {
		Vector templates = properties.getTemplateProperties();
		
		for ( int i = 0; i < templates.size(); i++) {
			TemplateProperties props = (TemplateProperties)templates.elementAt(i);
			
			if ( template.getName().equals( props.getName())) {
				return props;
			}
		}
		
		return null;
	}

//	private TemplateProperties getExistingTemplate( ConfigurationProperties properties, TemplateProperties template) {
//		Vector templates = properties.getGrammarProperties();
//		
//		for ( int i = 0; i < templates.size(); i++) {
//			TemplateProperties props = (TemplateProperties)templates.elementAt(i);
//			
//			if ( template.getName().equals( props.getName())) {
//				return props;
//			}
//		}
//		
//		return null;
//	}

	private void addDefaultScenarios( ConfigurationProperties properties) {
		try {
			File file = new File( getInstallationPath( DEFAULT_SCENARIOS_LOCATION));
			
			if ( file.exists()) {
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();

				if ( root.getName().equals( "scenarios")) {
					XElement[] scenarios = root.getElements( "scenario");
					
					if ( scenarios != null) {
						for ( int i = 0; i < scenarios.length; i++) {
							ScenarioProperties scenario = new ScenarioProperties( url, scenarios[i]);
							ScenarioProperties existingScenario = getExistingScenario( properties, scenario);
							
							// remove previous templates
							if ( existingScenario != null) {
								properties.removeScenarioProperties( existingScenario);
							}

							properties.addScenarioProperties( scenario);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}

	private ScenarioProperties getExistingScenario( ConfigurationProperties properties, ScenarioProperties scenario) {
		Vector scenarios = properties.getScenarioProperties();
		
		for ( int i = 0; i < scenarios.size(); i++) {
			ScenarioProperties props = (ScenarioProperties)scenarios.elementAt(i);
			
			if ( scenario.getName().equals( props.getName())) {
				return props;
			}
		}
		
		return null;
	}

	public static String getInstallationPath( String file) {
		String path = null;
		String dir = System.getProperty( "lax.dir");
		
		if ( dir != null && dir.trim().length() > 0) {
			path = dir+file;
		} else {
			path = file;
		}
		
		return path;
	}

	private static ExchangerDocument getOldProperties() {
		ExchangerDocument document = null;

		File dir = new File( XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			return null;
		}
		
		File file = new File( dir, V32_PROPERTIES_FILE);
		URL url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
			return null;
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();

				return document;
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
			}
			
			return null;
			
		}
		
		

		file = new File( dir, V31_PROPERTIES_FILE);
		url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
			return null;
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();

				return document;
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
			}
			
			return null;
			
		}
		
		

		

		 file = new File( dir, V30_PROPERTIES_FILE);
		 url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
			return null;
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();

				return document;
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
			}
			
			return null;
			
		}
		
		
		
		
		
		 file = new File( dir, V20_PROPERTIES_FILE);
		 url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();

				return document;
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
			}
		}
		
		dir = new File( XMLPLUS_HOME);

		if ( !dir.exists()) {
			return null;
		}
		
		file = new File( dir, OLD_PROPERTIES_FILE);
		url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
			}
		} else {
			return null;
		}
		
		return document;
	}
	
	public void redirectOutput() {
		File dir = new File( XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File outFile = new File( dir, ".xngr-editor.out");
		File errFile = new File( dir, ".xngr-editor.err");
		
		try {
			System.setErr( new PrintStream( new FileOutputStream( errFile), true));
			System.setOut( new PrintStream( new FileOutputStream( outFile), true));
		} catch ( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
	}
	
	
	public static String getLaxFilePath() {
		String executable = System.getProperty( "lax.application.name");
		String laxPath = null;

		if ( executable != null) {
			String laxName = null;
			int dotIndex = executable.lastIndexOf( '.');

			if ( dotIndex != -1) {
				laxName = executable.substring( 0, dotIndex)+ ".lax";
			} else {
				laxName = executable+".lax";
			}
			
			laxPath = System.getProperty( "lax.dir")+laxName;
		}
		
		return laxPath;
	}

//	private static Properties getLAXProperties() {
//		Properties lax = null;
//		String laxFile = getLaxFilePath();
//		
//		if ( laxFile != null) {
//			File file = new File( laxFile);
//			
//			try {
//				if ( file.exists()) {
//					lax = new LaxProperties();
//					lax.load( new FileInputStream( file));
//					
//	//				Enumeration names = lax.propertyNames();
//	//				
//	//				System.out.println( ">>> LAX");
//	//
//	//				while ( names.hasMoreElements()) {
//	//					String key = (String)names.nextElement();
//	//					System.out.println( key+"="+lax.getProperty( key));
//	//				}
//	//				
//	//				System.out.println( "<<< LAX");
//				}
//			
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		
//		return lax;
//	}

	/**
	 * Attempts to load ExchangerEditor in a single JVM instance only.
	 */
	protected void open( String file) {
		if (DEBUG) System.out.println( "Main.open( "+file+")");
		URL url = null;
		
		if ( file.equals( "-debugger")) {
			//debugger.setVisible(true);
			debugger.show();
		} else if ( file.equals( "-editor")) {
	 	        editor.setVisible(true);
		} else {
			try {
	 	        url = new URL( file);
	 	        editor.open( url, null, false);
	 	        editor.setVisible(true);
			} catch ( MalformedURLException e) {
				try {
					//url = new File( file).toURL();
					url = XngrURLUtilities.getURLFromFile(new File( file));
					editor.open( url, null, true);
		 	        editor.setVisible(true);
				} catch ( MalformedURLException m) {
					MessageHandler.showError( "Could not resolve URL for file \""+file+"\"", m, "URL Error");
				}
			}
		}
	}


	/**
	 * Attempts to load ExchangerEditor in a single JVM instance only.
	 */
	protected Socket findExchangerEditorSocket() {
		try {
			Socket s = new Socket( InetAddress.getLocalHost(), XMLPLUS_PORT);
			return s;
		} catch ( IOException e) {
//			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Attempts to load ExchangerEditor in a single JVM instance only.
	 */
	public void launch( ExtensionClassLoader loader, String path) {
		if(DEBUG) System.out.println("Main::launch(loader, path): ("+loader+", "+path+")");
		boolean launched = false;
		File file = null;
		
		if ( path != null && path.equals( "-noserver")) {
			// force the editor to start without a server.
			start( loader, "-editor");
			return;
		}

		if ( path != null && !path.equals( "-debugger")) {
			file = new File( path);
			path = file.getPath();
		} else if ( path == null) {
			path = "-editor";
		}
		
		while ( !launched) {
			Socket socket = findExchangerEditorSocket();
				
			if ( socket != null) { // already an editor or debugger active.

				// check for a license, do not continue if no license found ...
				/*try {
					LicenseManager licenseManager = LicenseManager.getInstance();
					licenseManager.isValid( KeyGenerator.generate(2), "Exchanger XML Editor");
				} catch( Exception ex) {*/
				  //GMCG COMMUNITY EDITION!!
					//System.err.println( "Server Socket started but could not find a license!");
					//System.exit( 0);
				//}

				try {
					if ( path != null) {
						OutputStream stream = socket.getOutputStream();
						byte[] bytes = path.getBytes();
						stream.write( bytes.length);
						stream.write( bytes);
						stream.close();
					}
					launched = true;
				} catch ( IOException e) {
					e.printStackTrace();
					System.err.println( "ERROR: Could not connect to socket!");
				}
			} else {
				try {
					// Start-up server-socket!
					ServerSocket server = new ServerSocket( XMLPLUS_PORT);

					start( loader, path);

					Thread listener = new ListenerThread( server);
					listener.start();
					
					launched = true;
				} catch ( IOException e) {
					e.printStackTrace();
					System.err.println( "ERROR: Could not create server!");
				}
			}
		}
	
	}

	protected boolean isEmptyString( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}
	
	public static void main(ExtensionClassLoader loader, String[] args) throws Exception {
		
		if(DEBUG) System.out.println("Main::ExtensionClassLoader - main loader: "+loader+" - args: "+args);
		setupSystemUIProperties();
		checkJavaVersion();
		
		String file = null;
		if ( args.length > 0) {
			file = args[0];
		}
		
		try {
			new Main( loader, file);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public static void main( String[] args) throws Exception {
		if(DEBUG) System.out.println("Main::ExtensionClassLoader - main args: "+args);
		setupSystemUIProperties();
		checkJavaVersion();

		String file = null;
		if ( args.length > 0) {
			file = args[0];
		}
		
		try {
			new Main( file);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setupSystemUIProperties() {
		System.setProperty( "apple.laf.useScreenMenuBar", "true");
		System.setProperty( "com.apple.macos.useScreenMenuBar", "true");
		System.setProperty( "com.apple.mrj.application.apple.menu.about.name", "Exchanger XML Editor");
		System.setProperty( "com.apple.mrj.application.live-resize", "true");
		System.setProperty( "com.apple.macos.smallTabs", "true");
		System.setProperty( "org.dom4j.factory", "com.cladonia.xml.XDocumentFactory");
		
 		//		UIManager.setLookAndFeel( "com.incors.plaf.kunststoff.KunststoffLookAndFeel");
//		UIManager.setLookAndFeel( "javax.swing.plaf.metal.MetalLookAndFeel");
//		UIManager.setLookAndFeel( "com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
//		UIManager.setLookAndFeel( "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
//		UIManager.setLookAndFeel( "com.sun.java.swing.plaf.motif.MotifLookAndFeel");

	}
	
	public static void checkJavaVersion() {
		String v = System.getProperty( "java.class.version","44.0");

		if ( "48.0".compareTo(v) > 0) { // not jdk 1.4...
			JOptionPane.showMessageDialog( 	null, 
							  				"Cannot Start The Exchanger XML Editor,\n"+
									  		"Could not find a valid JDK \""+System.getProperty("java.version")+"\"\n"+
									  		"Need at least JDK 1.4 to run.", 
											"Invalid JDK", 
											JOptionPane.ERROR_MESSAGE);
			
			System.exit( -1);
			return;
		}
	}
	
	public class ListenerThread extends Thread {
		ServerSocket server = null;
		
		public ListenerThread( ServerSocket socket) {
			server = socket;
		}
		
		public void run() {
			try {
				while (true) {
					Socket socket = server.accept();
					InputStream stream = socket.getInputStream();
					int length = stream.read();
					byte[] bytes = new byte[length];
					stream.read( bytes);
					
					String file = new String( bytes);
					open( file);
				}
			} catch ( IOException e) {
				e.printStackTrace();
				System.err.println( "ERROR: Could not connect to server!");
			} 
		}
	}
	
	public static boolean isDebug() {
		ClassLoader loader = Main.class.getClassLoader();
		
		try	{
			Class invokedClass = loader.loadClass( "com.cladonia.xngreditor.ExchangerEditor");

			return true;
		} catch( Exception e) {
//			e.printStackTrace();
		}
        
		return false;
	}
}	
 
