/*
 * $Id: PluginView.java,v 1.0 13 Mar 2007 16:00:51 Administrator Exp $
 *
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.plugins;

import java.awt.BorderLayout;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.Main;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;

import com.cladonia.xngreditor.NavigationButton;
import com.cladonia.xngreditor.XNGRMenuItem;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.NamespaceProperties;
import com.cladonia.xngreditor.project.DocumentProperties;
import com.cladonia.xngreditor.project.FolderProperties;
import com.cladonia.xngreditor.project.ProjectProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.template.TemplateProperties;


/**
 * 
 *
 * @version	$Revision: 1.0 $, $Date: 13 Mar 2007 16:00:51 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class PluginView {

	private static final boolean DEBUG = false;
	
	public static final String DEFAULT_TEMPLATES_LOCATION	= "templates"+File.separator+"all.templates";
	public static final String DEFAULT_TYPES_LOCATION		= "types"+File.separator+"all.types";
	public static final String DEFAULT_SCENARIOS_LOCATION	= "scenarios"+File.separator+"all.scenarios";
	public static final String DEFAULT_SAMPLES_LOCATION		= "projects"+File.separator+"all.projects";
	public static final String NEW_SAMPLES_LOCATION		= "projects"+File.separator+"new.projects";
	
	public static final String ICON_PROPERTY_NAME = "icon";
	public static final String IDENTIFIER_PROPERTY_NAME = "identifier";
	public static final String PLUGIN_VIEW_PANEL_PROPERTY_NAME = "plugin_view_panel_file";
	public static final String MNEMONIC_PROPERTY_NAME = "mnemonic";
	public static final String PROPERTIES_CLASS_PROPERTY_NAME = "properties_class";
	public static final String PROPERTIES_FILE_PROPERTY_NAME = "properties_file";
	public static final String PROPERTIES_ELEMENT_NAME_PROPERTY_NAME = "properties_element_name";
	
	//private String icon = "com/cladonia/xml/grid/icons/GridIcon.gif";
	private String icon = null; 
		
	private ExchangerDocument document = null;
	
	private String pluginPath = null;
	
	//values from .plugin file
	private String propertiesClass = null;
	private String propertiesFile = null;
	private String pluginViewPanelFile = null;
	private String propertyElementName = null;
	
	private NavigationButton button = null;
	private JRadioButtonMenuItem pluginViewItem = null;
	//private PluginViewPanel pluginViewPanel = null;
	private String identifier = null;
	private Character mnemonic = null;
	private PluginViewProperties properties = null;
	
	private List actions = null;
	private List buttonGroups = null;
	
	
	private ExchangerEditor exchangerEditor = null;
	
	public String toString() {
		return("Plugin Path: "+pluginPath+"\nProperties Class: "+propertiesClass+"\nProperties File: "+propertiesFile+
				"\nPluginViewPanelFile: "+pluginViewPanelFile+"\nPropertiesFile: "+propertiesFile);
	}
	
	public PluginView() {
		
	}
	
	public PluginView(ExchangerEditor exchangerEditor) {
		this.setExchangerEditor(exchangerEditor);
	}
	
	public JRadioButtonMenuItem getPluginViewItem() {
		try {
			if (pluginViewItem == null) {
				pluginViewItem = new JRadioButtonMenuItem(identifier);
				pluginViewItem.setIcon( getExchangerEditor().getIcon(icon));
				pluginViewItem.setMnemonic(this.getMnemonic().charValue());
				pluginViewItem.addItemListener(new PluginViewItemListener(this, this.exchangerEditor));
			}
	
			return pluginViewItem;
		}catch(Exception e) {
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load RadioButtonMenuItem from plugin: "+this.getIdentifier());
			return(null);
		}
	}
	
	
	public void setPluginViewItem(JRadioButtonMenuItem viewItem) {
		this.pluginViewItem = viewItem;
	}

	/**
	 * @param button the button to set
	 */
	public void setButton(NavigationButton navigationButton) {

		this.button = navigationButton;
	}

	/**
	 * @return the button
	 */
	public NavigationButton getButton() {
		try {
			if(button == null) {
				button = new NavigationButton( this.getIdentifier(), getExchangerEditor().getIcon(icon));
				button.setToolTipText( this.getIdentifier());
				button.addItemListener(new PluginViewItemListener(this, this.exchangerEditor));
			}
			return button;
		}catch(Exception e) {
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load NavigationButton from plugin: "+this.getIdentifier());
			return(null);
		}
	}

	/**
	 * @param pluginViewPanel the pluginViewPanel to set
	 */
	/*public void setPluginViewPanel(PluginViewPanel pluginViewPanel) {

		this.pluginViewPanel = pluginViewPanel;
	}

	public PluginViewPanel getPluginViewPanel() {
		return(this.pluginViewPanel);
	}*/
	
	/**
	 * @return the pluginViewPanel
	 */
	public PluginViewPanel createNewPluginViewPanel(ExchangerEditor parent, Properties props, ConfigurationProperties configProps,
            ExchangerView _view) {

		if(this.getPluginViewPanelFile() != null) {
			try {
				Class loadedClass = this.getExchangerEditor().getClassLoader().loadClass(this.getPluginViewPanelFile());
				Constructor constructor = loadedClass.getConstructor(new Class[] {PluginView.class, ExchangerEditor.class, Properties.class, ConfigurationProperties.class,ExchangerView.class});
				if(constructor != null) {
					
					Object objClass  = constructor.newInstance(new Object[] {this, parent, props, configProps, _view});
				
					if(objClass != null) {
						if(objClass instanceof PluginViewPanel) {
							//this.setPluginViewPanel((PluginViewPanel)objClass);
							return((PluginViewPanel)objClass);
						}	
					}
					else {
						System.out.println("objClass is null");
					}
				}		
				else {
					System.out.println("constructor is null");
				}
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				if(DEBUG) e.printStackTrace();
				System.err.println("Cannot load PluginViewPanel from plugin: "+this.getIdentifier());
				return(null);
			}
		}
		return (null);
	}
	
	public void loadActions() {
		
		if(DEBUG) System.out.println("PluginView::loadActions - exchangerEditor.getClassLoader(): "+this.getExchangerEditor().getClassLoader());
		if(this.getExchangerEditor() != null) {
			if(this.getDocument() != null) {
				XElement actionsElement = getDocument().getElement("/*[name()='xngr']/*[name()='plugins']/*[name()='plugin']/*[name()='actions']");
				if(actionsElement != null) {
					XElement[] actionElements = actionsElement.getElements("action");
					if(actionElements != null) {
						for (int cnt = 0; cnt < actionElements.length; cnt++) {
							XElement actionElement = actionElements[cnt];
							
							if(actionElement != null) {
								String actionClassName = actionElement.attributeValue("class");
								String keystroke_action_name = actionElement.attributeValue("keystroke_action_name");
								String keystroke_action_description = actionElement.attributeValue("keystroke_action_description");
								String keystroke_mask = actionElement.attributeValue("keystroke_mask");
								String keystroke_value = actionElement.attributeValue("keystroke_value");
								if((keystroke_mask != null) && (keystroke_mask.equals(""))) {
									keystroke_mask = null;
								}
								if((keystroke_value != null) && (keystroke_value.equals(""))) {
									keystroke_value = null;
								}
								
								if(actionClassName != null) {
									
//									instantiate the class
									try {
										if(DEBUG) System.out.println("PluginView::loadActions=========================================");
										
										Class loadedClass = this.getExchangerEditor().getClassLoader().loadClass(actionClassName);
										if(DEBUG) System.out.println("loadedClass: "+loadedClass);
										//Constructor constructor = loadedClass.getConstructor(new Class[] {ExchangerEditor.class, PluginViewPanel.class});
										Constructor constructor = loadedClass.getConstructor(new Class[] {ExchangerEditor.class});
										if(DEBUG) System.out.println("constructor: "+constructor);
										if(constructor != null) {
											Object objClass  = constructor.newInstance(new Object[] {this.getExchangerEditor()});
										
											if(objClass != null) {
												if(objClass instanceof PluginAction) {
													this.getExchangerEditor().getActions().add(objClass);
													PluginActionKeyMapping pluginKeyMapping = this.getExchangerEditor().getProperties().getKeyPreferences().addKeyMapping(keystroke_action_name, keystroke_action_description, keystroke_mask, keystroke_value);
													pluginKeyMapping.setAction((PluginAction) objClass);
													XNGRMenuItem menuItem = (XNGRMenuItem) getExchangerEditor().createMenuItem(pluginKeyMapping.getAction(), pluginKeyMapping.getKeystroke_action_name());													
													this.getActions().add(pluginKeyMapping);												
												}
											}
											else {
												System.out.println("objClass is null");
											}
										}
										else {
											System.out.println("constructor is null");
										}
										
									} catch (ClassNotFoundException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
										
										if(DEBUG) {
											System.out.println("this.getClass: "+this.getClass());
											System.out.println("this.getClass().getClassLoader: "+this.getExchangerEditor().getClassLoader());
											
										}
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
									} catch (NoSuchMethodException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
									} catch (InvocationTargetException e) {
										// TODO Auto-generated catch block
										if(DEBUG) e.printStackTrace();
										System.err.println("Cannot load class: "+actionClassName+" from plugin: "+this.getIdentifier());
									}
								}
							}
	
						}
						this.getExchangerEditor().getProperties().getKeyPreferences().updateDefaultConfigurations();
					}
				}
				
			}
		}
	}

//	 create the menu bar...
	public JMenu createPluginViewMenu() {
		// >>> Menu
		
		if(getDocument() != null) {
			XElement menuElement = getDocument().getElement("/*[name()='xngr']/*[name()='plugins']/*[name()='plugin']/*[name()='menu']");
			if(menuElement != null) {
				JMenu pluginMenu = new JMenu(menuElement.attributeValue("name"));
				try {
					pluginMenu.setMnemonic(new String(menuElement.attributeValue("mnemonic")).charAt(0));
				}catch(Exception e) {
					
				}
				
				XElement[] subMenus = menuElement.getElements("menu");
				if(subMenus != null) {
					for (int cnt = 0; cnt < subMenus.length; cnt++) {
						XElement subMenuElement = subMenus[cnt];
						if(subMenuElement != null) {
							JMenu subMenu = createPluginViewMenu(subMenuElement);
							if(subMenu != null) {
								pluginMenu.add(subMenu);
							}
						}
					}
				}
				
				
				XElement[] menuItems = menuElement.getElements("menuitem");
				if(menuItems != null) {
					for (int cnt = 0; cnt < menuItems.length; cnt++) {
						XElement subMenuItem = menuItems[cnt];
						if(subMenuItem != null) {
							String actionClassName = subMenuItem.attributeValue("action");
							if(actionClassName != null) {
								for(int acnt=0;acnt<this.getActions().size();++acnt) {
									Object actionObject = this.getActions().get(acnt);
									if(actionObject != null) {
										if(actionObject instanceof PluginActionKeyMapping) {
											PluginActionKeyMapping pluginAction = (PluginActionKeyMapping)actionObject;
											String pluginActionClassName = pluginAction.getAction().getClass().getName();
											if(pluginActionClassName.equalsIgnoreCase(actionClassName)) {
												XNGRMenuItem menuItem = getExchangerEditor().getMenuItem((String) ((Action)pluginAction.getAction()).getValue(Action.NAME));
												if(menuItem == null) {
													menuItem = (XNGRMenuItem) getExchangerEditor().createMenuItem(pluginAction.getAction(), pluginAction.getKeystroke_action_name());	
												}
												pluginMenu.add(menuItem);
												
											}
										}
									}
								}
											
									
								
								
							}
							else {
								//just add separator
								pluginMenu.addSeparator();
							}
						}
						
					}
				}
				
				GUIUtilities.alignMenu( pluginMenu);
				return(pluginMenu);
				
			}
			
		}
		
	    return (null);
	}	
	
//	 create the menu bar...
	public JMenu createPluginViewMenu(XElement menuElement) {
		// >>> Menu
		
			if(menuElement != null) {
				JMenu pluginMenu = new JMenu(menuElement.attributeValue("name"));
				try {
					pluginMenu.setMnemonic(new String(menuElement.attributeValue("mnemonic")).charAt(0));
				}catch(Exception e) {
					
				}
				
				XElement[] subMenus = menuElement.getElements("menu");
				if(subMenus != null) {
					for (int cnt = 0; cnt < subMenus.length; cnt++) {
						XElement subMenuElement = subMenus[cnt];
						if(subMenuElement != null) {
							JMenu subMenu = createPluginViewMenu(subMenuElement);
							if(subMenu != null) {
								pluginMenu.add(subMenu);
							}
						}
					}
				}
				
				
				XElement[] menuItems = menuElement.getElements("menuitem");
				if(menuItems != null) {
					for (int cnt = 0; cnt < menuItems.length; cnt++) {
						XElement subMenuItem = menuItems[cnt];
						
						if(subMenuItem != null) {
							String actionClassName = subMenuItem.attributeValue("action");
							if(actionClassName != null) {
								for(int acnt=0;acnt<this.getActions().size();++acnt) {
									Object actionObject = this.getActions().get(acnt);
									if(actionObject != null) {
										if(actionObject instanceof PluginActionKeyMapping) {
											PluginActionKeyMapping pluginAction = (PluginActionKeyMapping)actionObject;
											if(pluginAction.getAction().getClass().getName().equalsIgnoreCase(actionClassName)) {
												XNGRMenuItem menuItem = getExchangerEditor().getMenuItem((String) ((Action)pluginAction.getAction()).getValue(Action.NAME));
												if(menuItem == null) {
													menuItem = (XNGRMenuItem) getExchangerEditor().createMenuItem(pluginAction.getAction(), pluginAction.getKeystroke_action_name());	
												}
												
												pluginMenu.add(menuItem);
												
											}
										}
									}
								}
								
								
							}
							else {
								//just add separator
								pluginMenu.addSeparator();
							}
						}
						
					}
				}
				
				GUIUtilities.alignMenu( pluginMenu);
				return(pluginMenu);
				
			
			
		}
		
	    return (null);
	}
	
//	 create the menu bar...
	public JMenu createPluginViewPropertiesMenu() {
		// >>> Menu
		
		if(getDocument() != null) {
			XElement menuElement = getDocument().getElement("/*[name()='xngr']/*[name()='plugins']/*[name()='plugin']/*[name()='propertyMenu']");
			if(menuElement != null) {
				JMenu pluginMenu = new JMenu(menuElement.attributeValue("name"));
				try {
					//pluginMenu.setMnemonic(new String(menuElement.attributeValue("mnemonic")).charAt(0));
				}catch(Exception e) {
					
				}
				XElement[] subMenus = menuElement.getElements("propertyMenu");
				if(subMenus != null) {
					for (int cnt = 0; cnt < subMenus.length; cnt++) {
						XElement subMenuElement = subMenus[cnt];
						if(subMenuElement != null) {
							JMenu subMenu = createPluginViewPropertiesMenu(subMenuElement);
							if(subMenu != null) {
								pluginMenu.add(subMenu);
							}
						}
					}
				}
				
				
				XElement[] menuItems = menuElement.getElements("propertyMenuitem");
				if(menuItems != null) {
					for (int cnt = 0; cnt < menuItems.length; cnt++) {
						XElement subMenuItem = menuItems[cnt];
						if(subMenuItem != null) {
							String propertyName = subMenuItem.attributeValue("name");
							if(propertyName != null) {
								String propertyLabel = subMenuItem.attributeValue("label");
								if(propertyLabel != null) {
								
									PluginViewPropertyCheckBoxMenuItem menuItem = new PluginViewPropertyCheckBoxMenuItem(this, propertyName, propertyLabel);
									pluginMenu.add(menuItem);
									
									String propertyGroup = subMenuItem.attributeValue("group");
									if(propertyGroup != null) {									
										
										PluginViewButtonGroup buttonGroup = null;										
										buttonGroup = getButtonGroup(propertyGroup);
										
										
										if(buttonGroup == null) {										
											buttonGroup = new PluginViewButtonGroup(propertyGroup);
										}
										
										if(buttonGroup != null) {
											buttonGroup.add(menuItem);
										}
									}
								}
							}
							else {
								//just add separator
								pluginMenu.addSeparator();
							}
						}
						
					}
				}
				
				GUIUtilities.alignMenu( pluginMenu);
				return(pluginMenu);
				
			}
			
		}
		
	    return (null);
	}
	
//	 create the menu bar...
	public JMenu createPluginViewPropertiesMenu(XElement menuElement) {
		// >>> Menu
		
			if(menuElement != null) {
				JMenu pluginMenu = new JMenu(menuElement.attributeValue("name"));
				try {
					//pluginMenu.setMnemonic(new String(menuElement.attributeValue("mnemonic")).charAt(0));
				}catch(Exception e) {
					
				}
				
				XElement[] subMenus = menuElement.getElements("propertyMenu");
				if(subMenus != null) {
					for (int cnt = 0; cnt < subMenus.length; cnt++) {
						XElement subMenuElement = subMenus[cnt];
						if(subMenuElement != null) {
							JMenu subMenu = createPluginViewPropertiesMenu(subMenuElement);
							if(subMenu != null) {
								pluginMenu.add(subMenu);
							}
						}
					}
				}
				
				
				XElement[] menuItems = menuElement.getElements("propertyMenuitem");
				if(menuItems != null) {
					for (int cnt = 0; cnt < menuItems.length; cnt++) {
						XElement subMenuItem = menuItems[cnt];
						if(subMenuItem != null) {
							String propertyName = subMenuItem.attributeValue("name");
							if(propertyName != null) {
								String propertyLabel = subMenuItem.attributeValue("label");
								if(propertyLabel != null) {
								
									PluginViewPropertyCheckBoxMenuItem menuItem = new PluginViewPropertyCheckBoxMenuItem(this, propertyName, propertyLabel);
									pluginMenu.add(menuItem);
									
									String propertyGroup = subMenuItem.attributeValue("group");
									if(propertyGroup != null) {									
										
										PluginViewButtonGroup buttonGroup = null;										
										buttonGroup = getButtonGroup(propertyGroup);
										
										if(buttonGroup == null) {										
											buttonGroup = new PluginViewButtonGroup(propertyGroup);
											this.getButtonGroups().add(buttonGroup);
										}
										
										if(buttonGroup != null) {
											buttonGroup.add(menuItem);											
										}
									}
								}
							}
							else {
								//just add separator
								pluginMenu.addSeparator();
							}
						}
						
					}
				}
				
				//GUIUtilities.alignMenu( pluginMenu);
				return(pluginMenu);
				
			}			
		
		
	    return (null);
	}
	
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {

		this.identifier = identifier;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {

		return identifier;
	}

	/**
	 * @param mnemonic the mnemonic to set
	 */
	public void setMnemonic(Character mnemonic) {

		this.mnemonic = mnemonic;
	}

	/**
	 * @return the mnemonic
	 */
	public Character getMnemonic() {

		return mnemonic;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {

		this.icon = icon;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {

		return icon;
	}

	/**
	 * @param valueValue
	 */
	public void setMnemonic(String value) {

		if((value != null) && (value.length() > 0)) {
			this.setMnemonic(new Character(value.charAt(0)));
		}
		
	}


	/**
	 * @param exchangerEditor the exchangerEditor to set
	 */
	public void setExchangerEditor(ExchangerEditor exchangerEditor) {

		this.exchangerEditor = exchangerEditor;
	}

	/**
	 * @return the exchangerEditor
	 */
	public ExchangerEditor getExchangerEditor() {

		return exchangerEditor;
	}

	
	/*public void setProperties(String propertiesString, XElement element) {
		try {
			Class loadedClass = this.getExchangerEditor().getClassLoader().loadClass(propertiesString);
			Constructor constructor = loadedClass.getConstructor(new Class[] {XElement.class});
			if(constructor != null) {
				Object objClass  = constructor.newInstance(new Object[] {element});
			
				if(objClass != null) {
					if(objClass instanceof Properties) {
						this.setProperties((Properties)objClass);
					}	
				}
				else {
					System.out.println("objClass is null");
				}
			}		
			else {
				System.out.println("constructor is null");
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}*/
	
	
	
	/**
	 * @param properties the properties to set
	 */
	public void setProperties(PluginViewProperties properties) {

		
		this.properties = properties;
	}

	/**
	 * @return the properties
	 */
	public PluginViewProperties getProperties() {

		if(properties == null) {
			
			properties = getPluginViewProperties(this.getPropertyElementName());
			//properties = this.getProperties(propertiesElement);
			if(properties != null) {
				exchangerEditor.getProperties().getPluginProperties().add(properties);
			}
		}
		return properties;
	}
	
	public PluginViewProperties getPluginViewProperties(String rootName) {
		if(rootName != null) {
			ExchangerDocument document = null;
			boolean firstTime = false;

			File dir = new File( Main.XNGR_EDITOR_HOME);

			if ( !dir.exists()) {
				dir.mkdir();
			}
			
			File file = new File( dir, this.getPropertiesFile());
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
				//root = new XElement( "xngr", "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
				root = new XElement( "xngr");
				root.setText( "\n");
				document = new ExchangerDocument( url, root);
						
				//namespaceURI = root.getNamespaceURI();
			}
			else {
				root = document.getRoot();
				//namespaceURI = root.getNamespaceURI();
			}
			
			getProperties(document, root);
			
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

			
			return(getProperties());
		}
		else {
			return(null);
		}
	}
	
	public PluginViewProperties getProperties(ExchangerDocument propDocument, XElement propElement) {

		try {
			Class loadedClass = this.getExchangerEditor().getClassLoader().loadClass(propertiesClass);
			Constructor constructor = loadedClass.getConstructor(new Class[] {PluginView.class, ExchangerDocument.class, XElement.class});
			if(constructor != null) {
				Object objClass  = constructor.newInstance(new Object[] {this, propDocument, propElement});
			
				if(objClass != null) {
					if(objClass instanceof PluginViewProperties) {
						this.setProperties((PluginViewProperties)objClass);
					}	
				}
				else {
					System.out.println("objClass is null");
				}
			}		
			else {
				System.out.println("constructor is null");
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			if(DEBUG) e.printStackTrace();
			System.err.println("Cannot load Properties from plugin: "+this.getIdentifier());
			return(null);
		}	
		return (this.getProperties());
	}

	/**
	 * @param propertiesClass the propertiesClass to set
	 */
	public void setPropertiesClass(String propertiesFile) {

		this.propertiesClass = propertiesFile;
	}

	/**
	 * @return the propertiesClass
	 */
	public String getPropertiesClass() {

		return propertiesClass;
	}

	/**
	 * @param pluginViewPanelFile the pluginViewPanelFile to set
	 */
	public void setPluginViewPanelFile(String pluginViewPanelFile) {

		this.pluginViewPanelFile = pluginViewPanelFile;
	}

	/**
	 * @return the pluginViewPanelFile
	 */
	public String getPluginViewPanelFile() {

		
		return pluginViewPanelFile;
	}

	/**
	 * @param propertyElementName the propertyElementName to set
	 */
	public void setPropertyElementName(String propertyElementName) {

		this.propertyElementName = propertyElementName;
	}

	/**
	 * @return the propertyElementName
	 */
	public String getPropertyElementName() {

		return propertyElementName;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(List actions) {

		this.actions = actions;
	}

	/**
	 * @return the actions
	 */
	public List getActions() {

		if(actions == null) {
			actions = new ArrayList();
		}
		return actions;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(ExchangerDocument document) {

		this.document = document;
	}

	/**
	 * @return the document
	 */
	public ExchangerDocument getDocument() {

		return document;
	}

	/**
	 * @param buttonGroups the buttonGroups to set
	 */
	public void setButtonGroups(List buttonGroups) {

		this.buttonGroups = buttonGroups;
	}

	/**
	 * @return the buttonGroups
	 */
	public List getButtonGroups() {

		if(buttonGroups == null) {
			buttonGroups = new ArrayList();
		}
		return buttonGroups;
	}
	
	public PluginViewButtonGroup getButtonGroup(String groupName) {
		
		for(int cnt=0;cnt<getButtonGroups().size();++cnt) {
			ButtonGroup tempGroup = (ButtonGroup) getButtonGroups().get(cnt);
			if(tempGroup instanceof PluginViewButtonGroup) {
				if(tempGroup != null) {
					if(((PluginViewButtonGroup)tempGroup).getName().equalsIgnoreCase(groupName)) {
						return (PluginViewButtonGroup) (tempGroup);
					}
				}
			}
		}
		return(null);
	}

	/**
	 * @param propertiesFile the propertiesFile to set
	 */
	public void setPropertiesFile(String propertiesFile) {

		this.propertiesFile = propertiesFile;
	}

	/**
	 * @return the propertiesFile
	 */
	public String getPropertiesFile() {

		return propertiesFile;
	}
	
	private void addDefaultTemplates( PluginViewProperties properties) {
		try {
			File file = new File( getPluginPath() +File.separator +  DEFAULT_TEMPLATES_LOCATION);
			
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
							TemplateProperties existingTemplate = getExistingTemplate( exchangerEditor.getProperties(), template);
							
							// remove previous templates
							if ( existingTemplate != null) {
								exchangerEditor.getProperties().removeTemplateProperties( existingTemplate);
							}

							exchangerEditor.getProperties().addTemplateProperties( template);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
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
	
	private void addDefaultSamples( PluginViewProperties properties) {
		try {
			File file = new File( getPluginPath() + File.separator + DEFAULT_SAMPLES_LOCATION);
			
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

	private void addNewSamples( PluginViewProperties properties) {
		boolean localDebug = false;
		
		if(localDebug) System.out.println("PluginView - Add new samples");
		
		try {
			
			File file = new File( getPluginPath() + File.separator + NEW_SAMPLES_LOCATION);
			if(localDebug) System.out.println("PluginView - path: "+file.getAbsolutePath());
			
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

	
	private void addProjects( PluginViewProperties properties, URL base, XElement root) {
		try {
			if ( root.getName().equals( "projects")) {
				XElement[] projects = root.getElements( "project");
				
				if ( projects != null) {
					for ( int i = 0; i < projects.length; i++) {
						String name = projects[i].getAttribute( "name");
						
						if ( name != null && name.trim().length() > 0) {
							Vector list = exchangerEditor.getProperties().getProjectProperties();
							if ( list != null) {
								for ( int j = 0; j < list.size(); j++) {
									ProjectProperties p = (ProjectProperties)list.elementAt( j);
									
									if ( name.equals( p.getName())) {
										exchangerEditor.getProperties().removeProjectProperties( p);
									}
								}
							}

							ProjectProperties project = new ProjectProperties( name);
							exchangerEditor.getProperties().addProjectProperties( project);
							
							exchangerEditor.getProperties().update();
							XElement[] documents = projects[i].getElements( "document");
							addDocuments( base, project, documents);
								
							XElement[] folders = projects[i].getElements( "folder");
							addFolders( base, project, folders);
							
							
						}
					}
					
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							exchangerEditor.getProjectPanel().setProjects(exchangerEditor.getProperties().getProjectProperties(), true);		
						}
					});
					
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

	private void addDefaultTypes( PluginViewProperties properties) {
		try {
			File file = new File( getPluginPath() + File.separator + DEFAULT_TYPES_LOCATION);
			
			if ( file.exists()) {
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();

				if ( root.getName().equals( "types")) {
					XElement[] types = root.getElements( "type");
					
					if ( types != null) {
						for ( int i = 0; i < types.length; i++) {
							GrammarProperties type = new GrammarProperties( exchangerEditor.getProperties(), url, types[i]);
							GrammarProperties existingType = getExistingGrammar( exchangerEditor.getProperties(), type);
							
							// remove previous grammars
							if ( existingType != null) {
								exchangerEditor.getProperties().removeGrammarProperties( existingType);
							}

							if ( !StringUtilities.isEmpty( type.getNamespacePrefix()) && !StringUtilities.isEmpty( type.getNamespace())) {
								exchangerEditor.getProperties().addPrefixNamespaceMapping( type.getNamespacePrefix(), type.getNamespace());
							}

							Vector namespaces = type.getNamespaces();
							for ( int j = 0; j < namespaces.size(); j++) {
								NamespaceProperties namespace = (NamespaceProperties)namespaces.elementAt(j);

								if ( !StringUtilities.isEmpty( namespace.getPrefix()) && !StringUtilities.isEmpty( namespace.getURI())) {
									exchangerEditor.getProperties().addPrefixNamespaceMapping( namespace.getPrefix(), namespace.getURI());
								}
							}

							exchangerEditor.getProperties().addGrammarProperties( type);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}
	
	
	
	private void addDefaultScenarios( PluginViewProperties properties) {
		try {
			File file = new File( getPluginPath() +File.separator +  DEFAULT_SCENARIOS_LOCATION);
			
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
							ScenarioProperties existingScenario = getExistingScenario( exchangerEditor.getProperties(), scenario);
							
							// remove previous templates
							if ( existingScenario != null) {
								exchangerEditor.getProperties().removeScenarioProperties( existingScenario);
							}
	
							exchangerEditor.getProperties().addScenarioProperties( scenario);
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

	/**
	 * @param pluginPath the pluginPath to set
	 */
	public void setPluginPath(String pluginPath) {

		this.pluginPath = pluginPath;
	}

	/**
	 * @return the pluginPath
	 */
	public String getPluginPath() {

		return pluginPath;
	}
}
