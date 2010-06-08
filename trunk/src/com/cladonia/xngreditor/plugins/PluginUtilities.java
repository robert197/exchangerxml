/*
 * $Id: PluginUtilities.java,v 1.0 14 Mar 2007 12:39:37 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xngreditor.Main;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 14 Mar 2007 12:39:37 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginUtilities {
	
	private static final boolean DEBUG = false;
	
	public static List loadPlugins() {
		//get the plugins folder
		String path = Main.getInstallationPath(Main.PLUGINS_LOCATION);
		if(DEBUG) System.out.println("PluginUtilities::loadPlugins - path: "+path);
		File dir = new File(path);
		
		if(dir.exists() == true) {
			
			//now get all the .plugin files
			/*File[] files = dir.listFiles(new FileFilter() {
			
				public boolean accept(File pathname) {
			
					String extension = pathname.getName();
					if(extension.indexOf(".") > -1) {
						extension = extension.substring(extension.indexOf(".")+1);
						
						if(extension.equalsIgnoreCase("plugin")) {
							return(true);
						}
					}
					return false;
				}
			
			});*/
			
			List files = getPluginFiles(dir);
			
			
			List plugins = new ArrayList();
			if((files != null) && (files.size() > 0)) {
				for(int cnt=0;cnt<files.size();++cnt) {
					File pluginFile = (File) files.get(cnt);
					PluginView pluginView = getPluginView(pluginFile);
					if(pluginView != null) {
						plugins.add(pluginView);
					}
				}
			}
			
			return(plugins);
		}
		else {
			if(DEBUG) System.out.println("error - plugins folder does not exist");		
		}
		
		return(null);
	}
	
	public static List getPluginFiles(File parentDir) {
		if(DEBUG) System.out.println("PluginUtilities::getPluginFiles - parentDir: "+parentDir.getAbsolutePath());
		ArrayList pluginFiles = new ArrayList();
		
		File[] files = parentDir.listFiles();
		if((files != null) && (files.length > 0)) {
			
			for (int cnt = 0; cnt < files.length; cnt++) {
				if(files[cnt].isDirectory() == true) {
					
					List tempPlugins = getPluginFiles(files[cnt]);
					if((tempPlugins != null) && (tempPlugins.size() > 0)) {
						for(int icnt=0;icnt<tempPlugins.size();++icnt) {
							
							pluginFiles.add(tempPlugins.get(icnt));
						}
					}
				}
				else if(files[cnt].isFile() == true) {
					String extension = files[cnt].getName();
					if(extension.indexOf(".") > -1) {
						extension = extension.substring(extension.indexOf(".")+1);
						
						if(extension.equalsIgnoreCase("plugin")) {
							if(DEBUG) System.out.println("PluginUtilities::getPluginFiles - adding: "+files[cnt].getName());
							pluginFiles.add(files[cnt]);
						}
					}
				}
			}
		}
		
		return(pluginFiles);
	}
	
	public static PluginView getPluginView(File pluginFile) {
				
        try {
        	ExchangerDocument document = new ExchangerDocument(XngrURLUtilities.getURLFromFile(pluginFile));
			document.load();
			
			if(document != null) {
				XElement pluginElement = document.getElement("/*[name()='xngr']/*[name()='plugins']/*[name()='plugin']");
				if(pluginElement != null) {
					PluginView pluginView = new PluginView();
					pluginView.setDocument(document);
					pluginView.setPluginPath(pluginFile.getParent());
					XElement[] results = document.getElements("/*[name()='xngr']/*[name()='plugins']/*[name()='plugin']/*[name()='property']");
					
					if((results != null) && (results.length > 0)) {
						for(int cnt=0;cnt<results.length;++cnt) {
							Object obj = results[cnt];
							if(obj instanceof XElement) {
								XElement propertyElement = (XElement)obj;
								
								String nameValue = propertyElement.getAttribute("name");
								String valueValue = propertyElement.getAttribute("value");
								
								if((nameValue != null) && (valueValue != null)) {
									//System.out.println("name: "+nameValue+" - value: "+valueValue);
									if(nameValue.equalsIgnoreCase(PluginView.ICON_PROPERTY_NAME)) {
										pluginView.setIcon(valueValue);
									}
									else if(nameValue.equalsIgnoreCase(PluginView.IDENTIFIER_PROPERTY_NAME)) {
										pluginView.setIdentifier(valueValue);
									} 
									else if(nameValue.equalsIgnoreCase(PluginView.MNEMONIC_PROPERTY_NAME)) {
										pluginView.setMnemonic(valueValue);
									}
									else if(nameValue.equalsIgnoreCase(PluginView.PLUGIN_VIEW_PANEL_PROPERTY_NAME)) {
										pluginView.setPluginViewPanelFile(valueValue);
									}
									else if(nameValue.equalsIgnoreCase(PluginView.PROPERTIES_CLASS_PROPERTY_NAME)) {
										pluginView.setPropertiesClass(valueValue);
									}
									else if(nameValue.equalsIgnoreCase(PluginView.PROPERTIES_FILE_PROPERTY_NAME)) {
										pluginView.setPropertiesFile(valueValue);
									}
									else if(nameValue.equalsIgnoreCase(PluginView.PROPERTIES_ELEMENT_NAME_PROPERTY_NAME)) {
										pluginView.setPropertyElementName(valueValue);
									}
								}
								
							}
						}
						
						
					}
					
					if(DEBUG) System.out.println("PluginUtilities::getPluginView: "+pluginView.toString());
					return(pluginView);
					
				}
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
		} catch (SAXParseException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
		}
        
		return(null);
	}

	
}
