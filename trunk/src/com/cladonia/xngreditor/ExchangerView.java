/*
 * $Id: ExchangerView.java,v 1.40 2005/09/27 15:22:29 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dom4j.DocumentType;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.QName;

import com.cladonia.schema.SchemaDocument;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.XMLSchema;
import com.cladonia.schema.viewer.RootSelectionDialog;
import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerDocumentEvent;
import com.cladonia.xml.ExchangerDocumentListener;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLError;
import com.cladonia.xml.XMLGrammar;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.browser.Browser;
//import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.navigator.NavigatorSettings;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.NamespaceProperties;
import com.cladonia.xngreditor.plugins.PluginView;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The panel that holds the panels and information for a document.
 *
 * @version	$Revision: 1.40 $, $Date: 2005/09/27 15:22:29 $
 * @author Dogsbay
 */
public class ExchangerView extends NavigationPanel implements ExchangerDocumentListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final boolean DEBUG = false;
	
	public static final String BROWSER_ICON = "com/cladonia/xml/browser/icons/BrowserIcon.gif";
	public static final String VIEWER_ICON = "com/cladonia/xml/viewer/icons/ViewerIcon.gif";
	public static final String EDITOR_ICON = "com/cladonia/xml/editor/icons/EditorIcon.gif";
	public static final String SCHEMA_ICON = "com/cladonia/schema/viewer/icons/SchemaViewerIcon.gif";
	public static final String DESIGNER_ICON =	"com/cladonia/xml/designer/icons/DesignerIcon.gif";
	public static final String GRID_ICON = "com/cladonia/xml/grid/icons/GridIcon.gif";
	
	private static Vector entityNames = null;

	private Vector allElements		= null;
	private Vector anyElements			= null;
	private Vector namespaces			= null;
	private Vector globalElements		= null;
	private Vector elementNames			= null;
	private Hashtable attributeNames	= null;

	private Vector tagSchemas			= null;
	private ErrorList errors			= null;
	private XPathList results			= null;

	private XMLSchema schema			= null;
	private ExchangerDocument document	= null;
	private XElement selectedElement	= null; // sticky selection element
	
	private Hashtable icons				= null;

	private GrammarProperties grammar			= null;
	private XMLGrammarImpl validationGrammar	= null;
	private NavigatorSettings navigatorSettings = null;

	private ViewPanel current			= null;

	private ExchangerEditor main 				= null;
	private ConfigurationProperties properties	= null;

	private Editor editor				= null;
	private Designer designer			= null;
	private Viewer viewer				= null;
	//private Grid grid					= null;
	private SchemaViewer schemaViewer	= null;
	
	private Vector userViews			= null;
	
	private Vector pluginViewPanels 	= null;
	
	
		
	
	private FocusListener focusListener = null;
	
	private ChangeManager changeManager = null;
	
	private RootSelectionDialog rootDialog = null;

	private String defaultValidationGrammar = "";
	
	
	private NavigationButton schemaButton	= null;
	private NavigationButton editorButton	= null;
	private NavigationButton designerButton	= null;
	private NavigationButton viewerButton	= null;
	//private NavigationButton browserButton	= null;
	/*private NavigationButton gridButton = null;*/
	
	private ButtonGroup documentViewButtonGroup = null;
	private JPanel documentViewButtonPanel = null;
	
	private JMenu documentViewsMenu = null;
	
	private JRadioButtonMenuItem schemaItem		= null;
	private JRadioButtonMenuItem editorItem		= null;
	private JRadioButtonMenuItem designerItem	= null;
	private JRadioButtonMenuItem viewerItem		= null;
	private JRadioButtonMenuItem browserItem	= null;
	/*private JRadioButtonMenuItem gridItem 		= null;*/

	public ExchangerView( ExchangerEditor parent, ConfigurationProperties properties) {
		main = parent;
		this.properties = properties;
		
		errors = new ErrorList();
		results = new XPathList();

		focusListener = new FocusListener() {
			public void focusGained( FocusEvent e) {
				current.setFocus();
			}
			public void focusLost( FocusEvent e) {
			}
		};
		
		addFocusListener( focusListener);
		
//		browser = new Browser( parent, properties.getBrowserProperties());
//		add( getBrowser(), "Browser");

		viewer = new Viewer( parent, properties.getViewerProperties(), this);
		add( viewer, "Viewer");

		schemaViewer = new SchemaViewer( parent, properties.getSchemaViewerProperties(), this);
		add( schemaViewer, "Schema");
		
		designer = new Designer( parent, properties.getDesignerProperties(), this);
		add( designer, "Outliner");

		editor = new Editor( parent, properties, this, errors);
		add( editor, "Editor");
		
		//grid = new Grid( parent, properties.getGridProperties(), properties, this);
		//add( grid, "Grid");
		
		setupViewButtons();
		setupViewMenu();
		
		setPluginViewPanels(new Vector());
		
		
//		for each of the plugin buttons
		for(int cnt=0;cnt<parent.getPluginViews().size();++cnt) {
			Object obj = parent.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					//XElement propertiesElement = properties.getPluginViewProperties(pluginView.getPropertyElementName());
					Properties pluginProperties = pluginView.getProperties();
					//if(pluginProperties != null) {
						PluginViewPanel pluginViewPanel = pluginView.createNewPluginViewPanel(parent,pluginProperties, properties, this);
						
						if(pluginViewPanel != null) {
							add(pluginViewPanel, pluginView.getIdentifier());
							getPluginViewPanels().add(pluginViewPanel);
							
						}
					//}
					
				}				
			}
		}
		
		userViews = new Vector();
		
		changeManager = new ChangeManager();
		changeManager.addChangeListener( this);
		changeManager.setLimit( 100);
		validationGrammar = new XMLGrammarImpl();
	}
	
	public void addUserView(UserView newUserView) {
	    
	    //see if a view with that identifier already exists
	    boolean alreadyExists = false;
	    
	    for(int cnt=0;cnt<userViews.size();++cnt) {
	    	
	    	UserView tempUserView = (UserView) userViews.get(cnt);
	    	if(tempUserView.getIdentifier().equals(newUserView.getIdentifier())) {
	    		alreadyExists = true;
	    	}
	    }
	    
	    if(alreadyExists == false) {
		    userViews.add(newUserView);
		    main.getDocumentViewButtonPanel().add(newUserView.getButton());
		    main.getDocumentViewButtonGroup().add(newUserView.getButton());
		
	    
	    	add( newUserView.getPanel(), newUserView.getIdentifier());
	    	this.repaint();
	    }
	    else {
	    	MessageHandler.showError("A view with the identifier "+newUserView.getIdentifier()+ " already exists", "Exchanger Script Error");
	    	
	    }
	}
	
	private void setupViewButtons() {
		
		documentViewButtonGroup = new ButtonGroup();
		setDocumentViewButtonPanel(new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0)));
		getDocumentViewButtonPanel().setBorder(new EmptyBorder(0, 0, 2, 0));
		
		setSchemaButton(new NavigationButton("Schema", getIcon(SCHEMA_ICON)));
		getSchemaButton().setToolTipText( "Schema Viewer");
		getDocumentViewButtonPanel().add(getSchemaButton());
		documentViewButtonGroup.add(getSchemaButton());

		getSchemaButton().addItemListener(new SchemaItemListener());

		setDesignerButton(new NavigationButton("Outliner", getIcon(DESIGNER_ICON)));
		getDesignerButton().setToolTipText( "Tag Free Editor");
		getDocumentViewButtonPanel().add(getDesignerButton());
		documentViewButtonGroup.add(getDesignerButton());

		getDesignerButton().addItemListener(new DesignerItemListener());

		setEditorButton(new NavigationButton( "Editor", getIcon(EDITOR_ICON)));
		getEditorButton().setToolTipText( "Programmers Editor");
		getDocumentViewButtonPanel().add(getEditorButton());
		documentViewButtonGroup.add(getEditorButton());

		getEditorButton().addItemListener(new EditorItemListener());

		setViewerButton(new NavigationButton( "Viewer", getIcon(VIEWER_ICON)));
		getViewerButton().setToolTipText( "XML Viewer");
		getDocumentViewButtonPanel().add(getViewerButton());
		documentViewButtonGroup.add(getViewerButton());

		getViewerButton().addItemListener(new ViewerItemListener());

//		browserButton = new NavigationButton( "Browser", getIcon(BROWSER_ICON));
//		browserButton.setToolTipText( "Browser View");
//		documentViewButtonPanel.add( browserButton);
//		documentViewButtonGroup.add( browserButton);
//
//		browserButton.addItemListener(new BrowserItemListener());

		
		
		/*gridButton = new NavigationButton( "Grid", getIcon(GRID_ICON));
		gridButton.setToolTipText( "Grid Editor");
			
		if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
		
			documentViewButtonPanel.add(gridButton);
			documentViewButtonGroup.add(gridButton);
			gridButton.addItemListener(new GridItemListener());
		}*/
		
		for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
									
					if((pluginView.getPluginViewPanelFile() != null) && (pluginView.getPluginViewPanelFile().length() > 0)) { 
						NavigationButton pluginNavigationButton = pluginView.getButton();
						if(pluginNavigationButton != null) {
							getDocumentViewButtonPanel().add(pluginNavigationButton);
							documentViewButtonGroup.add(pluginNavigationButton);
							
						}
					}
				}				
			}
		}
		
		/*
		 * TODO
		 * T. Curley 17/05/05
		 * Width + 4 to compensate for the 4 pixel increase in border size
		 * when the button is selected so the text isnt cut off
		 */
		int width = getDesignerButton().getPreferredSize().width+4;
		width = Math.max(getViewerButton().getPreferredSize().width, width);
		width = Math.max(getSchemaButton().getPreferredSize().width, width);
		width = Math.max(getEditorButton().getPreferredSize().width, width);
//		width = Math.max(browserButton.getPreferredSize().width, width);
		/*width = Math.max(gridButton.getPreferredSize().width, width);*/
		
		//for each of the plugin buttons
		for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					NavigationButton pluginNavigationButton = pluginView.getButton();
					if(pluginNavigationButton != null) {
						width = Math.max(pluginNavigationButton.getPreferredSize().width, width);
						
					}
				}				
			}
		}
		
		width = width + 4;
		getSchemaButton().setPreferredSize(new Dimension(width, 29));
		getDesignerButton().setPreferredSize(new Dimension(width, 29));
		getEditorButton().setPreferredSize(new Dimension(width, 29));
		getViewerButton().setPreferredSize(new Dimension(width, 29));
//		browserButton.setPreferredSize(new Dimension(width, 29));
		/*gridButton.setPreferredSize(new Dimension(width, 29));*/
		
//		for each of the plugin buttons
		for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					NavigationButton pluginNavigationButton = pluginView.getButton();
					if(pluginNavigationButton != null) {
						pluginNavigationButton.setPreferredSize(new Dimension(width, 29));
						
					}
				}				
			}
		}

		getSchemaButton().setEnabled(false);
		getSchemaViewItem().setEnabled(false);
		getViewerButton().setEnabled(false);
		getViewerViewItem().setEnabled(false);
		getEditorButton().setEnabled(false);
		getEditorViewItem().setEnabled(false);
		getDesignerButton().setEnabled(false);
		getDesignerViewItem().setEnabled(false);
//		browserButton.setEnabled(false);
//		getBrowserViewItem().setEnabled(false);
		/*gridButton.setEnabled(false);
		getGridViewItem().setEnabled(false);*/
		
//		for each of the plugin buttons
		for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					NavigationButton pluginNavigationButton = pluginView.getButton();
					if(pluginNavigationButton != null) {
						pluginNavigationButton.setEnabled(false);
					}
					JRadioButtonMenuItem pluginMenuItem = pluginView.getPluginViewItem();
					if(pluginMenuItem != null) {
						pluginMenuItem.setEnabled(false);
					}
					
				}				
			}
		}
		
		main.setDocumentViewButtonPanel(getDocumentViewButtonPanel());
		main.updateDocumentViewButtonPanel();
		
		
	}
	
	public void setupViewMenu() {
		ButtonGroup group = new ButtonGroup();
		setDocumentViewsMenu(new JMenu("Document Views"));
		getDocumentViewsMenu().setMnemonic('D');
		
		/*if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
			item = getGridViewItem();
			documentViewButtonGroup.add(item);
			viewMenu.add(item);
		}*/
		JRadioButtonMenuItem item = null;
		
//		for each of the plugin buttons
		for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
					if(pluginItem != null) {
						item = pluginItem;
						group.add(item);
						//viewMenu.add(item);
						getDocumentViewsMenu().insert(item, 0);
					}
					
				}				
			}
		}
		
		item = getViewerViewItem();
		//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_4,InputEvent.CTRL_MASK, false));
		group.add(item);
		//viewMenu.add(item);
		getDocumentViewsMenu().insert(item, 0);
		
		item = getEditorViewItem();
		//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_3,InputEvent.CTRL_MASK, false));
		group.add(item);
		//viewMenu.add(item);
		getDocumentViewsMenu().insert(item, 0);
		
		item = getDesignerViewItem();
		//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_2,InputEvent.CTRL_MASK, false));
		group.add(item);
		//viewMenu.add(item);
		getDocumentViewsMenu().insert(item, 0);
		
		item = getSchemaViewItem();
		//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_1,InputEvent.CTRL_MASK, false));
		group.add(item);
		//viewMenu.add(item);
		getDocumentViewsMenu().insert(item, 0);

		GUIUtilities.alignMenu( getDocumentViewsMenu());
		
		main.setDocumentViewsMenu(getDocumentViewsMenu());
		main.updateDocumentViewMenu();
	}
	
	public void addViewToViewMenu(JMenu viewMenu) {
		
	}
	
	public void changeView(ViewPanel current) {
		
		setCurrent(current);
		
		//updateDocNameLabel(document.getName());
		
		
		if (current instanceof Editor) {
			getEditorButton().setSelected(true);
			editorItem.setSelected(true);
		} else if (current instanceof Designer) {
			designerButton.setSelected(true);
			designerItem.setSelected(true);
		} else if (current instanceof Viewer) {
			viewerButton.setSelected(true);
			viewerItem.setSelected(true);
		} else if (current instanceof SchemaViewer) {
			getSchemaButton().setSelected(true);
			schemaItem.setSelected(true);
		//} else if (current instanceof Grid) {
		//	gridButton.setSelected(true);
		//	gridItem.setSelected(true);
//		} else if (current instanceof Browser) {
//			browserButton.setSelected(true);
//			browserItem.setSelected(true);
		} else if(current instanceof PluginViewPanel) {
			
//			for each of the plugin buttons
			if((getPluginViewPanels() != null)) {
				for(int vcnt=0;vcnt<getPluginViewPanels().size();++vcnt) {
					PluginViewPanel panel = (PluginViewPanel) getPluginViewPanels().get(vcnt);
					if(current == panel) {
						NavigationButton pluginButton = panel.getPluginView().getButton();
						if(pluginButton != null) {
							pluginButton.setSelected(true);
						}
						
						JRadioButtonMenuItem pluginItem = panel.getPluginView().getPluginViewItem();
						if(pluginItem != null) {
							pluginItem.setSelected(true);
						}
					}
				}								
			}
						
		} else {
			
//			
			
		    for(int cnt=0;cnt<getUserViews().size();++cnt) {
			    UserView userView = (UserView) getUserViews().get(cnt);
			    
			    //if(current instanceof userView.getPanel())) {
			        userView.getButton().setEnabled(true);
			    //}
			}
		}
	}
	
	public void removeUserView(String identifier) {
	    
	    //see if a view with that identifier already exists
	    boolean alreadyExists = false;
	    
	    for(int cnt=0;cnt<userViews.size();++cnt) {
	    	
	    	UserView tempUserView = (UserView) userViews.get(cnt);
	    	if(tempUserView.getIdentifier().equals(identifier)) {
	    		alreadyExists = true;
	    		
	    		main.getDocumentViewButtonPanel().remove(tempUserView.getButton());
			    main.getDocumentViewButtonGroup().remove(tempUserView.getButton());
	    		
	    		userViews.remove(cnt);
	    		remove(tempUserView.getPanel());
	    	}
	    }
	    
	    
	    this.repaint();
	    if(alreadyExists == false) {
	    	//MessageHandler.showError("A view with the identifier "+identifier+ " could not be found", "Exchanger Script Error");
	    	
	    	
	    }
	}
	
//	public Browser getBrowser() {
//		if ( browser == null) { 
//			browser = new Browser( main, properties.getBrowserProperties());
//		}
//
//		return browser;
//	}
	
	// make sure the tabbed view knows about this.
	public void setFocussed() {
		Component parent = getParent();

		while ( parent != null) {
			if ( parent instanceof ExchangerTabbedView) {
				((ExchangerTabbedView)parent).setFocussed();

				if ( document != null && document.isModified()) {
					int value = MessageHandler.showConfirm( "Document \""+document.getName()+"\" has been changed by another process.\n"+
															"Do you want to reload the document?");

					if ( value == JOptionPane.YES_OPTION) {
						main.getReloadAction().execute();
					}
				}
				
				return;
			}

			parent = parent.getParent();
		}
	}

	public ErrorList getErrors() {
		return errors;
	}

	public XPathList getXPathList() {
		return results;
	}

	public void reload() {
		try {
			document.load();
		} catch( Exception e) {
		}

		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if ( current instanceof Designer) {
					((Designer)current).setDocument( document);
					((Designer)current).updateTree();
				} else if ( current instanceof Viewer) {
					((Viewer)current).setDocument( document);
				} else if ( current instanceof Editor) {
					((Editor)current).setDocument( document);
				} else if ( current instanceof Browser) {
					((Browser)current).setDocument( document);
				//} else if (current instanceof Grid) {
				//    ((Grid)current).setDocument( document);
				} else if (current instanceof PluginViewPanel) {
					((PluginViewPanel)current).setDocument( document);

				}
			}
		});

		changeManager.discardAllEdits();
	}

	public ExchangerDocument getDocument() {
		return document;
	}
	
	public void updateGrammar( GrammarProperties grammar) {
		if ( this.grammar != null && grammar != null && this.grammar.getID().equals( grammar.getID())) {
			XMLSchema schema = FileUtilities.createSchema( document, grammar);
		
			setSchema( schema);
			setTagCompletionSchemas( FileUtilities.createTagCompletionSchemas( document, grammar, schema));
	
			updateValidationGrammar();
		}
	}

	public XMLSchema getSchema() {
		return schema;
	}

	public Vector getTagCompletionSchemas() {
		return tagSchemas;
	}

	public ViewPanel getCurrentView() {
		return current;
	}

	public String getStatusType() {
		if ( grammar != null) {
			return grammar.getDescription();
		} else if ( document != null && document.isDTD()) {
			return "Document Type Definition";
		}
		
		return "";
	}

	public String getStatusValidator() {
		int type = -1;

		if ( document != null && document.isXML()) {
			if ( validationGrammar.useExternal()) {
				type = validationGrammar.getType();
			} else {
				type = document.getInternalGrammarType();
			}
		}
				
		switch ( type) {
			case XMLGrammar.TYPE_XSD:
				return "XSD";
			case XMLGrammar.TYPE_RNG:
				return "RNG";
			case XMLGrammar.TYPE_RNC:
				return "RNC";
			case XMLGrammar.TYPE_NRL:
				return "NRL";
			case XMLGrammar.TYPE_DTD:
				return "DTD";
			default:
				return "";
		}
	}

	public String getStatusLocation() {
		if ( document != null && document.isXML()) {
			return validationGrammar.useExternal() ? "EXT" : "INT";
		} else {
			return "";
		}
	}

	public String getStatusDocumentStatus() {
		if ( document != null && document.isXML()) {
			if ( document.isError()) {
				return Statusbar.DOCUMENT_STATUS_ERROR;
			} else if ( changeManager.isTextChanged()) {
				return Statusbar.DOCUMENT_STATUS_UNKNOWN;
			} else if ( changeManager.isValidated()) {
				return Statusbar.DOCUMENT_STATUS_VALID;
			} else {
				return Statusbar.DOCUMENT_STATUS_WELLFORMED;
			}
		}
		
		return "";
	}

	private void setCurrent( ViewPanel view) {
		current = view;
		main.setCurrent( view);
	}

	public void setProperties() {
		editor.setProperties();
		viewer.setProperties();
		designer.setProperties();
		schemaViewer.setProperties();
		//grid.setProperties();
		
//		for each of the plugin buttons
		/*for(int cnt=0;cnt<this.main.getPluginViews().size();++cnt) {
			Object obj = this.main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					PluginViewPanel pluginViewPanel = pluginView.getPluginViewPanel();
					if(pluginViewPanel != null) {
						pluginViewPanel.setProperties();
					}
				}				
			}
		}*/
		
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {					
					pluginViewPanel.setProperties();					
				}				
			}
		}
	}
	
	public void updateBookmarks() {
		editor.updateBookmarks();
	}

	public void initBookmarks() {
		editor.initBookmarks( properties.getBookmarks());
	}

	public boolean isChanged() {
		return changeManager.isChanged() || document.getURL() == null;
	}

	public void stateChanged( ChangeEvent e) {
//		main.getParseAction().setEnabled( !changeManager.isParsed());
		main.getParseAction().setParsed( !changeManager.isTextChanged() && !document.isError());
		main.getValidateAction().setValidated( changeManager.isValidated());
		updateTitle();
		main.updateStatus();
		setViewIcons();
	}
	
	private void updateTitle() {
		if ( isChanged() || document.getURL() == null) {
			main.setViewTitle( this, document.getName()+"*");
		} else {
			main.setViewTitle( this, document.getName());
		}
	}

	public void setGrammar( GrammarProperties grammar) {
		if (DEBUG) System.out.println( "ExchangerView.setGrammar( "+grammar+")");
		this.grammar = grammar;
		setViewIcons();
		updateValidationGrammar();

		main.updateProperties();
	}

	public GrammarProperties getGrammar() {
		return grammar;
	}

	public XMLGrammarImpl getValidationGrammar() {
		return validationGrammar;
	}

	public void updateValidationGrammar() {
		if (DEBUG) System.out.println( "ExchangerView.updateValidationGrammar()");

		if ( grammar != null) {
			validationGrammar.setExternal( grammar.useExternal());
			validationGrammar.setType( grammar.getType());
			validationGrammar.setLocation( grammar.getLocation());
		}
		
		if ( grammar != null && editor != null) {
			editor.setFragments( grammar.getFragments());
		}

		main.updateFragments();
		main.updateStatus();
	}

	public void updatePreferences() {
		editor.updatePreferences();
		viewer.updatePreferences();
		schemaViewer.updatePreferences();
		designer.updatePreferences();
//		browser.updatePreferences();
		//grid.updatePreferences();
		
//		for each of the plugin buttons
		/*for(int cnt=0;cnt<this.main.getPluginViews().size();++cnt) {
			Object obj = this.main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					PluginViewPanel pluginViewPanel = pluginView.getPluginViewPanel();
					if(pluginViewPanel != null) {
						pluginViewPanel.updatePreferences();
					}
				}				
			}
		}*/
		
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {					
					pluginViewPanel.updatePreferences();					
				}				
			}
		}
	}

	public Editor getEditor() {
		return editor;
	}
	public SchemaViewer getSchemaViewer() {
		return schemaViewer;
	}
	
	public Designer getDesigner() {
		return designer;
	}
	public Viewer getViewer() {
		return viewer;
	}
	//public Grid getGrid() {
	//	return grid;
	//}

	public ChangeManager getChangeManager() {
		return changeManager;
	}
	
	/*public void updateDocNameLabel(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				docNameLabel.setText(text);
				docNameLabel.invalidate();
				docNameLabel.repaint();
			}
		});
	}*/

	public void setDocument( ExchangerDocument document) {
		
		if(document != null) {
			if ( this.document != null) {
				this.document.removeListener( this);
			}
		}
			
		
			
		this.document = document;
		
		errors.setDocument( document);

		if ( document != null) {
			document.addListener( this);
			
			//updateDocNameLabel(document.getName());
			
			document.setGrammar( validationGrammar);

			if ( !document.isError() && document.isXML()) {
				XElement root = document.getRoot();
				XDocument doc = document.getDocument();
				DocumentType docType = null;
				
				if ( doc != null) {
					doc.getDocType();
				}

				if ( docType != null) {
					defaultValidationGrammar = "DTD";
				} else if ( root != null) {
					String location = root.getAttribute( "schemaLocation");
					
					if ( location == null) {
						location = root.getAttribute( "noNamespaceSchemaLocation");
					}
					
					if ( location != null) {
						defaultValidationGrammar = "XSD";
					} else {
						defaultValidationGrammar = "";
					}
				} else {
					defaultValidationGrammar = "";
				}
				
				if (getSchema() != null) {
					getSchemaButton().setEnabled(true);
					getSchemaViewItem().setEnabled(true);
					designerButton.setEnabled(true);
					getDesignerViewItem().setEnabled(true);
				}
				


				viewerButton.setEnabled(true);
				getViewerViewItem().setEnabled(true);
				
//					for each of the plugin buttons
				for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
					Object obj = main.getPluginViews().get(cnt);
					if((obj != null) && (obj instanceof PluginView)) {
						PluginView pluginView = (PluginView)obj;
						if(pluginView != null) {
							
							NavigationButton pluginButton = pluginView.getButton();
							if(pluginButton != null) {
								pluginButton.setEnabled(true);
							}
							
							JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
							if(pluginItem != null) {
								pluginItem.setEnabled(true);
							}
							
						}				
					}
				}
				
				getEditorButton().setEnabled(true);
			}
			else {
				getEditorButton().setEnabled(true);
				
				getSchemaButton().setEnabled(false);
				getSchemaViewItem().setEnabled(false);
				getDocumentViewButtonPanel().remove(getSchemaButton());
				getDocumentViewsMenu().remove(getSchemaViewItem());
				
				getDesignerButton().setEnabled(false);
				getDesignerViewItem().setEnabled(false);
				getDocumentViewButtonPanel().remove(getDesignerButton());
				getDocumentViewsMenu().remove(getDesignerViewItem());
				
				getViewerButton().setEnabled(false);
				getViewerViewItem().setEnabled(false);
				getDocumentViewButtonPanel().remove(getViewerButton());
				getDocumentViewsMenu().remove(getViewerViewItem());
				
				/*gridButton.setEnabled(false);
				getGridViewItem().setEnabled(false);*/
				
//					for each of the plugin buttons
				for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
					Object obj = main.getPluginViews().get(cnt);
					if((obj != null) && (obj instanceof PluginView)) {
						PluginView pluginView = (PluginView)obj;
						if(pluginView != null) {
							
							NavigationButton pluginButton = pluginView.getButton();
							if(pluginButton != null) {
								pluginButton.setEnabled(false);
								getDocumentViewButtonPanel().remove(pluginButton);								
							}
							
							JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
							if(pluginItem != null) {
								pluginItem.setEnabled(false);
								getDocumentViewsMenu().remove(pluginItem);
								
							}
							
						}				
					}
				}
				
				for(int cnt=0;cnt<getUserViews().size();++cnt) {
				    UserView userView = (UserView) getUserViews().get(cnt);
				    userView.getButton().setEnabled(true);
				}
				
				getEditorButton().setEnabled(true);
				getEditorViewItem().setEnabled(true);
			}
			
			getEditorButton().setEnabled(true);
			getEditorViewItem().setEnabled(true);
			
//			setTitle( "ExchangerEditor - "+document.getName());
		}
		else {
			getDesignerButton().setEnabled(false);
			getDesignerViewItem().setEnabled(false);
			
			getDocumentViewButtonPanel().remove(getDesignerButton());
			getDocumentViewsMenu().remove(getDesignerViewItem());
			
			getViewerButton().setEnabled(false);
			getViewerViewItem().setEnabled(false);
			
			getDocumentViewButtonPanel().remove(getViewerButton());
			getDocumentViewsMenu().remove(getViewerViewItem());
			
			getEditorButton().setEnabled(false);
			getEditorViewItem().setEnabled(false);
			
			getDocumentViewButtonPanel().remove(getEditorButton());
			getDocumentViewsMenu().remove(getEditorViewItem());
//				browserButton.setEnabled(false);
//				getBrowserViewItem().setEnabled(false);
			/*gridButton.setEnabled(false);
			getGridViewItem().setEnabled(false);*/
			
//				for each of the plugin buttons
			for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
				Object obj = main.getPluginViews().get(cnt);
				if((obj != null) && (obj instanceof PluginView)) {
					PluginView pluginView = (PluginView)obj;
					if(pluginView != null) {
						
						NavigationButton pluginButton = pluginView.getButton();
						if(pluginButton != null) {
							pluginButton.setEnabled(false);
							getDocumentViewButtonPanel().remove(pluginButton);
							
						}
						
						JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
						if(pluginItem != null) {
							pluginItem.setEnabled(false);
							getDocumentViewsMenu().remove(pluginItem);
						}
						
					}				
				}
			}
		}
			
		editor.setDocument( document);
		designer.setDocument( document);
		//grid.setDocument( document);
//		browser.setDocument( document);
//		viewer.setDocument( document);

//		for each of the plugin buttons
		/*for(int cnt=0;cnt<this.main.getPluginViews().size();++cnt) {
			Object obj = this.main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					PluginViewPanel pluginViewPanel = pluginView.getPluginViewPanel();
					if(pluginViewPanel != null) {
						pluginViewPanel.setDocument(document);
					}
				}				
			}
		}*/
		
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {					
					pluginViewPanel.setDocument(document);					
				}				
			}
		}
		
		updateTitle();
		
		main.updateStatus();
		
		main.setDocumentViewButtonPanel(getDocumentViewButtonPanel());
		main.updateDocumentViewButtonPanel();
		
		main.setDocumentViewsMenu(getDocumentViewsMenu());
		main.updateDocumentViewMenu();
		
		

//		setChanged( false);
//		changeManager.discardAllEdits();
		
	}
	
// Implementation of the ExchangerDocumentListener interface...	
	public void documentUpdated( ExchangerDocumentEvent event) {
		if (DEBUG) System.out.println( "ExchangerView.documentUpdated()");

		// perform on event dispatch thread...
		SwingUtilities.invokeLater( new Runnable() {
		    public void run() {
				if ( document != null) { 
					if ( !document.isError() && document.isXML()) {
						XElement root = document.getRoot();
						XDocument doc = document.getDocument();
						DocumentType docType = null;
						
						if ( doc != null) {
							doc.getDocType();
						}
		
						if ( docType != null) {
							defaultValidationGrammar = "DTD";
						} else if ( root != null) {
							String location = root.getAttribute( "schemaLocation");
							
							if ( location == null) {
								location = root.getAttribute( "noNamespaceSchemaLocation");
								if (DEBUG) System.out.println( "noNamespaceSchemaLocation = "+location);
							} else {
								if (DEBUG) System.out.println( "schemaLocation = "+location);
							}
							
							if ( location != null) {
								defaultValidationGrammar = "XSD";
							} else {
								defaultValidationGrammar = "";
							}
						} else {
							defaultValidationGrammar = "";
						}
					}

					setViewIcons();
					updateNames();
					updateTitle();
					main.updateStatus();
				}
		    }
		});
	}
	
	public void setSchemaInternal(XMLSchema schema) {
		if (schema != null) {
			getSchemaButton().setEnabled(true);
			getSchemaViewItem().setEnabled(true);

			if (document != null && !document.isError() && document.isXML()) {
				designerButton.setEnabled(true);
				getDesignerViewItem().setEnabled(true);
			}
		} else { // schema == null
			//			statusbar.setSchemaType( "");
			getSchemaButton().setEnabled(false);
			getSchemaViewItem().setEnabled(false);
			designerButton.setEnabled(false);
			getDesignerViewItem().setEnabled(false);
		}
	}
	
	public void setViewIcons() {
		if ( document != null) {
			if ( document.isXML()) {
				if ( document.isError()) {
					if ( document.isRemote()) {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_ERROR));
					} else if ( document.isReadOnly()) {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_ERROR));
					} else {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_ERROR));
					}
				} else if ( changeManager != null && changeManager.isTextChanged()) {
						if ( document.isRemote()) {
							main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_UNKNOWN));
						} else if ( document.isReadOnly()) {
							main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_UNKNOWN));
						} else {
							main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_UNKNOWN));
						}
				} else if ( changeManager != null && changeManager.isValidated()) {
					if ( document.isRemote()) {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_VALID));
					} else if ( document.isReadOnly()) {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_VALID));
					} else {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_VALID));
					}
				} else { // if ( !document.isError()) {
					if ( document.isRemote()) {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL));
					} else if ( document.isReadOnly()) {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL));
					} else {
						main.setViewIcon( this, IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL));
					}
				} 
			} else if ( document.isDTD()) {
				// not xml ...
				if ( document.isRemote()) {
					main.setViewIcon( this, IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_DTD, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL));
				} else if ( document.isReadOnly()) {
					main.setViewIcon( this, IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_DTD, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL));
				} else {
					main.setViewIcon( this, IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_DTD, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL));
				}
			} else {
				// not xml ...
				URL url = document.getURL();
				String extension = "";
				
				if ( url != null) {
					String location = url.toString();
					int pos = location.lastIndexOf( ".");

					if ( pos != -1 && ((pos+1) < location.length())) {
					    extension = location.substring( pos+1, location.length());
					}
				}
				
				if ( document.isRemote()) {
					main.setViewIcon( this, IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_REMOTE));
				} else if ( document.isReadOnly()) {
					main.setViewIcon( this, IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_READ_ONLY));
				} else {
					main.setViewIcon( this, IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_NORMAL));
				}
			}
		}
	}

	public Icon getViewIcon() {
		if ( document != null) {
			if ( document.isXML()) {
				if ( document.isError()) {
					if ( document.isRemote()) {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_ERROR);
					} else if ( document.isReadOnly()) {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_ERROR);
					} else {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_ERROR);
					}
				} else if ( changeManager != null && changeManager.isTextChanged()) {
						if ( document.isRemote()) {
							return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_UNKNOWN);
						} else if ( document.isReadOnly()) {
							return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_UNKNOWN);
						} else {
							return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_UNKNOWN);
						}
				} else if ( changeManager != null && changeManager.isValidated()) {
					if ( document.isRemote()) {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_VALID);
					} else if ( document.isReadOnly()) {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_VALID);
					} else {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_VALID);
					}
				} else { // if ( !document.isError()) {
					if ( document.isRemote()) {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL);
					} else if ( document.isReadOnly()) {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL);
					} else {
						return IconFactory.getIconForType( getGrammar(), IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL);
					}
				} 
			} else if ( document.isDTD()) {
				// not xml ...
				if ( document.isRemote()) {
					return IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_DTD, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL);
				} else if ( document.isReadOnly()) {
					return IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_DTD, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL);
				} else {
					return IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_DTD, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL);
				}
			} else {
				// not xml ...
				URL url = document.getURL();
				String extension = "";
				
				if ( url != null) {
					String location = url.toString();
					int pos = location.lastIndexOf( ".");

					if ( pos != -1 && ((pos+1) < location.length())) {
					    extension = location.substring( pos+1, location.length());
					}
				}
				
				if ( document.isRemote()) {
					return IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_REMOTE);
				} else if ( document.isReadOnly()) {
					return IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_READ_ONLY);
				} else {
					return IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_NORMAL);
				}
			}
		}
		
		return null;
	}

	public void documentDeleted( ExchangerDocumentEvent event) {}

	public XElement getSelectedElement() {
		XElement element = null;

		if ( current instanceof Viewer) {
			element = ((Viewer)current).getSelectedElement();
		} else if ( current instanceof Designer) {
			element = ((Designer)current).getSelectedElement();
		} else if ( current instanceof Editor) {
			element = document.getLastElement( ((Editor)current).getCursorPosition());
		//} else if ( current instanceof Grid) {
		//	element = ((Grid)current).getSelectedElement();
		} else if ( current instanceof PluginViewPanel) {
			element = ((PluginViewPanel)current).getSelectedElement();
		}

		return element;
	}

	public XElement getPreviousSelectedElement() {
		return selectedElement;
	}

	public void setSelectedNode( Node node, boolean endTag, Vector namespaces, int y) {
		while ( node != null) {
			Vector results = document.search( node.getUniquePath(), namespaces);
			
			if ( results.size() > 0) {
				Node n = (Node)results.elementAt(0);
				
				if ( n != null) {
					if ( current instanceof Editor) {
						if ( n instanceof XElement) {
							((Editor)current).selectElement( (XElement)n, endTag, y);
						} else if ( n instanceof XAttribute) {
							((Editor)current).selectAttribute( (XAttribute)n, y);
						}
					} else if ( current instanceof Viewer) {
						if ( n instanceof XElement) {
							((Viewer)current).setSelectedElement( (XElement)n, endTag, y);
						} else if ( n instanceof XAttribute) {
							((Viewer)current).setSelectedElement( (XElement)((XAttribute)n).getParent(), false, y);
						}
					} else if ( current instanceof Designer) {
						if ( n instanceof XElement) {
							((Designer)current).setSelectedNode( (XElement)n, y);
						} else if ( n instanceof XAttribute) {
							((Designer)current).setSelectedNode( (XAttribute)n, y);
						}
					/*} else if ( current instanceof Grid) {
						if ( n instanceof XElement) {
							((Grid)current).setSelectedElement( (XElement)n, null);
						} else if ( n instanceof XAttribute) {
							((Grid)current).setSelectedElement( (XAttribute)n, null);
						}*/
					} else if ( current instanceof PluginViewPanel) {
						if ( n instanceof XElement) {
							((PluginViewPanel)current).setSelectedElement( (XElement)n);
						} else if ( n instanceof XAttribute) {
							((PluginViewPanel)current).setSelectedElement( (XAttribute)n);
						}
					}
					
				}
				return;
			}
			
			endTag = false;
			node = (Node)node.getParent();
		}
	}

	// Updates the element name and attribute name list for the 
	private void updateNames() {
		if ( document != null && !document.isError() && document.isXML()) {
			Vector declaredNamespaces = document.getDeclaredNamespaces();

			if ( tagSchemas != null) {
				for ( int i = 0; i < tagSchemas.size(); i++) {
					SchemaDocument schema = (SchemaDocument)tagSchemas.elementAt(i);
					schema.updatePrefixes( declaredNamespaces);
				}
			}
			
			// Add all grammar namespaces!!!
			namespaces = new Vector();
			
			Vector grammars = properties.getGrammarProperties();
			for ( int i = 0; i < grammars.size(); i++) {
				GrammarProperties grammar = (GrammarProperties)grammars.elementAt(i);
				String namespace = grammar.getNamespace();
				
				if ( namespace != null && namespace.length() > 0) {
					String prefix = grammar.getNamespacePrefix();
					String ns = null;

					if ( prefix != null && prefix.length() > 0) {
						ns = "xmlns:"+prefix+"=\""+namespace+"\"";
					} else {
						ns = "xmlns=\""+namespace+"\"";
					}

					if ( !namespaces.contains( ns)) {
						namespaces.addElement( ns);
					}
				}
				
				Vector nss = grammar.getNamespaces();
				for ( int j = 0; j < nss.size(); j++) {
					NamespaceProperties np = (NamespaceProperties)nss.elementAt(j);
					namespace = np.getURI();
					
					if ( namespace != null && namespace.length() > 0) {
						String prefix = np.getPrefix();
						String ns = null;

						if ( prefix != null && prefix.length() > 0) {
							ns = "xmlns:"+prefix+"=\""+namespace+"\"";
						} else {
							ns = "xmlns=\""+namespace+"\"";
						}
						
						if ( !namespaces.contains( ns)) {
							namespaces.addElement( ns);
						}
					}
				}
			}
			
			Vector nss = document.getDeclaredNamespaces();
			for ( int j = 0; j < nss.size(); j++) {
				Namespace np = (Namespace)nss.elementAt(j);
				String namespace = np.getURI();
				
				if ( namespace != null && namespace.length() > 0) {
					String prefix = np.getPrefix();
					String ns = null;

					if ( prefix != null && prefix.length() > 0) {
						ns = "xmlns:"+prefix+"=\""+namespace+"\"";
					} else {
						ns = "xmlns=\""+namespace+"\"";
					}
					
					if ( !namespaces.contains( ns)) {
						namespaces.addElement( ns);
					}
				}
			}

			// Add all other namespaces!!!
			Map namespaceURIs = properties.getPrefixNamespaceMappings();
			Iterator keys = namespaceURIs.keySet().iterator();
			
			while ( keys.hasNext()) {
				Object prefix = keys.next();
				Object namespace = namespaceURIs.get( prefix);
				String ns = "xmlns:"+prefix+"=\""+namespace+"\"";
				
				if ( !namespaces.contains( ns)) {
					namespaces.add( ns);
				}
			}

			Vector documentElementNames = document.getElementNames();
			Vector documentAttributeNames = document.getAttributeNames();

			elementNames = new Vector();
			attributeNames = new Hashtable();
			
			for ( int i = 0; i < documentElementNames.size(); i++) {
				elementNames.addElement( ((QName)documentElementNames.elementAt(i)).getQualifiedName());
			}

			for ( int i = 0; i < documentAttributeNames.size(); i++) {
				QName name = (QName)documentAttributeNames.elementAt(i);
				attributeNames.put( name.getQualifiedName(), document.getAttributeValues( name));
			}
			
			editor.setNamespaces( namespaces);
			editor.setElementNames( elementNames);
			editor.setAttributeNames( attributeNames);
			editor.setEntityNames( getEntityNames());
			editor.setSchemas( tagSchemas);
		}
	}
	
	public Vector getElementNames() {
		return elementNames;
	}
	
	public Vector getAttributeNames() {
		if ( attributeNames != null) {
			return new Vector( attributeNames.keySet());
		}
		
		return null;
	}

	public Vector getGlobalElements() {
		return globalElements;
	}

	public Vector getAllElements() {
		if ( allElements == null) { 
			allElements = new Vector();
			
			if ( tagSchemas != null) {
				for ( int i = 0; i < tagSchemas.size(); i++) {
					Vector elems = ((SchemaDocument)tagSchemas.elementAt(i)).getElements();

					if ( elems != null) {
						allElements.addAll( elems);
					}
				}
			}
		}
		
		return allElements;
	}

	private static Vector getEntityNames() {
		if ( entityNames == null) {
			entityNames = new Vector();
			entityNames.addElement( "&amp;");
			entityNames.addElement( "&gt;");
			entityNames.addElement( "&lt;");
			entityNames.addElement( "&apos;");
			entityNames.addElement( "&quot;");
		}
		
		return entityNames;
	}

	public void setTagCompletionSchemas( Vector schemas) {
		this.tagSchemas = schemas;

		updateNames();

		main.updateProperties();
	}

	public boolean setSchema( XMLSchema schema) {

		XElement docRoot = null;
		SchemaElement schemaRoot = null;
		
		if ( schema != null) {
			if ( document != null) {
				Vector elements = schema.getGlobalElements();
				docRoot = document.getRoot();
				
				if ( elements.size() == 1) {
					schemaRoot = (SchemaElement)elements.elementAt(0);
				} else {
					for ( int i = 0; i < elements.size() && schemaRoot == null; i++) {
						SchemaElement e = (SchemaElement)elements.elementAt(i);

						if ( e.getName().equals( docRoot.getName())) {
							schemaRoot = e;
						}
					}
				}
			}
		
			if ( schemaRoot == null) {
				RootSelectionDialog dialog = getRootSelectionDialog();
				dialog.setSchema( schema);
				//dialog.setVisible( true);
				dialog.show();
				
				if ( !dialog.isCancelled()) {
					schemaRoot = dialog.getSelectedElement();
				} else {
					return false;
				}
			}
		}

		this.schema = schema;
		schemaViewer.setSchema( schema, schemaRoot);
		designer.setSchema( schema);
		//grid.setSchema(schema);
		
//		for each of the plugin buttons
		/*for(int cnt=0;cnt<this.main.getPluginViews().size();++cnt) {
			Object obj = this.main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					PluginViewPanel pluginViewPanel = pluginView.getPluginViewPanel();
					if(pluginViewPanel != null) {
						pluginViewPanel.setSchema(schema);
					}
				}				
			}
		}*/
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {					
					pluginViewPanel.setSchema(schema);					
				}				
			}
		}
		
		main.updateProperties();

		return true;
	}
	
	private SchemaElement getSchemaRoot( XMLSchema schema) {
		XElement docRoot = null;
		SchemaElement schemaRoot = null;
		
		if ( schema != null) {
			if ( document != null) {
				Vector elements = schema.getGlobalElements();
				docRoot = document.getRoot();
				
				if ( elements.size() == 1) {
					schemaRoot = (SchemaElement)elements.elementAt(0);
				} else {
					for ( int i = 0; i < elements.size() && schemaRoot == null; i++) {
						SchemaElement e = (SchemaElement)elements.elementAt(i);

						if ( e.getName().equals( docRoot.getName())) {
							schemaRoot = e;
						}
					}
				}
			}
		
			if ( schemaRoot == null) {
				RootSelectionDialog dialog = getRootSelectionDialog();
				dialog.setSchema( schema);
				//dialog.setVisible( true);
				dialog.show();
				
				if ( !dialog.isCancelled()) {
					schemaRoot = dialog.getSelectedElement();
				}
			}
		}

		return schemaRoot;
	}

	public RootSelectionDialog getRootSelectionDialog() {
		if ( rootDialog == null) {
			rootDialog = new RootSelectionDialog( main);
			rootDialog.setLocationRelativeTo( main);
		}

		return rootDialog;
	}
	
	public void updateModel() {
		Object current = getCurrentView();

		try {
			if ( current instanceof Designer) {
				Designer d = (Designer)current;
				designer.selectionChanged();
	
				if ( changeManager.isModelChanged()) {
					document.update();
				}
			} else if ( current instanceof Editor) {
				Editor editor = (Editor)current;
				
				if ( changeManager.isTextChanged()) {
					editor.parse();
				}
			/*} else if ( current instanceof Grid) {
				Grid g = (Grid)current;
				//grid.selectionChanged();
				grid.updateDocument();
				
				if ( changeManager.isModelChanged()) {
				    
				    editor.parse();
				    document.update();
				}
				else if ( changeManager.isTextChanged()) {
				    editor.parse();
				}*/
			} else if ( current instanceof PluginViewPanel) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)current;
				//grid.selectionChanged();
				pluginViewPanel.updateDocument();
				
				if ( changeManager.isModelChanged()) {
				    
				    editor.parse();
				    document.update();
				}
				else if ( changeManager.isTextChanged()) {
				    editor.parse();
				}
			}
			
			
			
		} catch ( Exception x) {
			// don't do anything, this was just to make sure the 
			// latest information is available in the document.
		}
	}

	public void switchToViewer() throws Exception {
		XElement previousElement = viewer.getSelectedElement();
		
		updateModel();
		
		if ( !document.isError() && document.isXML()) {
			if ( current instanceof Designer) {
				selectedElement = ((Designer)current).getSelectedElement();
			} else if ( current instanceof Editor) {
				selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
			/*} else if ( current instanceof Grid) {
			    selectedElement = ((Grid)current).getSelectedElement();
			    
			    this.setDocument(grid.getDocument());
			    updateModel();*/
			} else if ( current instanceof PluginViewPanel) {
				selectedElement = ((PluginViewPanel)current).getSelectedElement();
			    
			    this.setDocument(((PluginViewPanel)current).getDocument());
			    updateModel();
			}

			if ( !viewer.hasLatestInformation()) {
			    viewer.setDocument( document);
			}

			if ( main.isAutoSynchroniseSelection() && selectedElement != null) {
				viewer.setSelectedElement( selectedElement, false, -1);
			} else {
				viewer.setSelectedElement( previousElement, false, -1);
			}

			show( "Viewer");
			viewerButton.setSelected(true);
			getViewerViewItem().setSelected(true);
	//		viewerButton.setSelected( true);
	//		getViewerViewItem().setSelected( true);
			viewer.setFocus();
			setCurrent( viewer);
		} else {
			throw document.getError();
		}
	}

	public void switchToDesigner() throws Exception {
//		System.out.println( "ExchangerView.switchToDesigner()");
		XElement previousElement = designer.getSelectedElement();
		updateModel();
		if ( !document.isError() && document.isXML()) {
			if ( current instanceof Viewer) {
				selectedElement = ((Viewer)current).getSelectedElement();
			} else if ( current instanceof Designer) {
				selectedElement = ((Designer)current).getSelectedElement();
			} else if ( current instanceof Editor) {
				selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
			//} else if ( current instanceof Grid) {
			//    selectedElement = ((Grid)current).getSelectedElement();
			}else if ( current instanceof PluginViewPanel) {
				selectedElement = ((PluginViewPanel)current).getSelectedElement();
			}
			if ( !designer.hasLatestInformation()) {
				designer.setDocument( document);
				designer.updateTree();				
			}
			else {
				designer.setDocument( document);
				designer.updateTree();
			}

			if ( main.isAutoSynchroniseSelection() && selectedElement != null) {
				designer.setSelectedNode( selectedElement, -1);
			} else {
				designer.setSelectedNode( previousElement, -1);
			}

			show( "Outliner");
			designerButton.setSelected(true);
			getDesignerViewItem().setSelected(true);
	//		designerButton.setSelected( true);
	//		getDesignerViewItem().setSelected( true);
			designer.setFocus();
			setCurrent( designer);
		} else {
			throw document.getError();
		}		
	}

	public void switchToSchema() throws Exception{
		Exception error = null;
		
		if ( document.isXSD()) {
			// validate the schema!
			main.getValidateSchemaAction().execute();
			Vector errs = errors.getErrors();
			
			if ( errs != null && errs.size() > 0 ) {
				XMLError err = (XMLError)errs.elementAt(0);
				throw err.getException();
			}
			
			if ( !document.isError()) {
				// validate the schema!
				XMLSchema schema = new XMLSchema( document);
				main.getHelper().setSchema( schema);
				//schemaViewer.setSchema(schema);

				SchemaElement current = schemaViewer.getRoot();
				SchemaElement root = null;
				
				if ( schema != null) {
					if ( current != null) {
						Vector elements = schema.getGlobalElements();
						
						if ( elements.size() == 1) {
							root = (SchemaElement)elements.elementAt(0);
						} else {
							for ( int i = 0; i < elements.size() && root == null; i++) {
								SchemaElement e = (SchemaElement)elements.elementAt(i);

								if ( e.getName().equals( current.getName())) {
									root = e;
								}
							}
						}
					}
				
					if ( root == null) {
						RootSelectionDialog dialog = getRootSelectionDialog();
						dialog.setSchema( schema);
						//dialog.setVisible( true);
						dialog.show();
						
						if ( !dialog.isCancelled()) {
							root = dialog.getSelectedElement();
						}
					}
				}

				schemaViewer.setSchema( schema, root);
			} else {
				throw document.getError();
			}
		}
		
		if ( current instanceof Viewer) {
			selectedElement = ((Viewer)current).getSelectedElement();
		} else if ( current instanceof Designer) {
			selectedElement = ((Designer)current).getSelectedElement();
		} else if ( current instanceof Editor) {
			selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
		//} else if ( current instanceof Grid) {
		//    selectedElement = ((Grid)current).getSelectedElement();
		} else if ( current instanceof PluginViewPanel) {
			selectedElement = ((PluginViewPanel)current).getSelectedElement();
		    
		}
		
		if ( !schemaViewer.isInitialised()) {
			schemaViewer.initialise();
		}
		
		show( "Schema");
		getSchemaButton().setSelected(true);
		getSchemaViewItem().setSelected(true);
//		schemaButton.setSelected( true);
//		getSchemaViewItem().setSelected( true);
		schemaViewer.setFocus();
		setCurrent( schemaViewer);
	}
	
	public void switchToEditor() {
		int previousPos = editor.getCursorPosition();
		
		updateModel();

		if ( current instanceof Viewer) {
			selectedElement = ((Viewer)current).getSelectedElement();
		} else if ( current instanceof Designer) {
			selectedElement = ((Designer)current).getSelectedElement();
		/*} else if ( current instanceof Grid) {
		    
		    grid.updateDocument();
		    selectedElement = ((Grid)current).getSelectedElement();
		    this.setDocument(grid.getDocument());
		    updateModel();
		    
*/
		} else if ( current instanceof PluginViewPanel) {
			selectedElement = ((PluginViewPanel)current).getSelectedElement();
		    
		    this.setDocument(((PluginViewPanel)current).getDocument());
		    updateModel();
		}
		
		if ( !editor.hasLatestInformation()) {
			editor.setDocument( document);
		}

		if ( main.isAutoSynchroniseSelection() && selectedElement != null) {
			if ( selectedElement.getContentStartPosition() > 0) {
				editor.setCursorPosition( selectedElement.getContentStartPosition());
			} else {
				editor.setCursorPosition( selectedElement.getElementEndPosition());
			}
		} else {
			editor.setCursorPosition( previousPos);
		}
		
		getEditorButton().setSelected(true);
		
		
		show( "Editor");
		editor.setFocus();
		setCurrent( editor);
	}
	
	public void documentUpdated() {
		if (document != null) {
			if (document.isXML()) {
				if (document.isError()) {
				
				} else {
				
				}

				if (getSchema() != null) {
					getSchemaButton().setEnabled(true);
					getSchemaViewItem().setEnabled(true);
					designerButton.setEnabled(true);
					getDesignerViewItem().setEnabled(true);
				}

				viewerButton.setEnabled(true);
				getViewerViewItem().setEnabled(true);
				
//				for each of the plugin buttons
				for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
					Object obj = main.getPluginViews().get(cnt);
					if((obj != null) && (obj instanceof PluginView)) {
						PluginView pluginView = (PluginView)obj;
						if(pluginView != null) {
							
							NavigationButton pluginButton = pluginView.getButton();
							if(pluginButton != null) {
								pluginButton.setEnabled(true);
							}
							
							JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
							if(pluginItem != null) {
								pluginItem.setEnabled(true);
							}
							
						}				
					}
				}
				
			} else { // Not XML
				viewerButton.setEnabled(false);
				getViewerViewItem().setEnabled(false);

				designerButton.setEnabled(false);
				getDesignerViewItem().setEnabled(false);

				getSchemaButton().setEnabled(false);
				getSchemaViewItem().setEnabled(false);
				
				/*gridButton.setEnabled(false);
				getGridViewItem().setEnabled(false);*/
				
//				for each of the plugin buttons
				for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
					Object obj = main.getPluginViews().get(cnt);
					if((obj != null) && (obj instanceof PluginView)) {
						PluginView pluginView = (PluginView)obj;
						if(pluginView != null) {
							
							NavigationButton pluginButton = pluginView.getButton();
							if(pluginButton != null) {
								pluginButton.setEnabled(true);
							}
							
							JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
							if(pluginItem != null) {
								pluginItem.setEnabled(true);
							}
							
						}				
					}
				}
				
				for(int cnt=0;cnt<getUserViews().size();++cnt) {
				    UserView userView = (UserView) getUserViews().get(cnt);
				    userView.getButton().setEnabled(true);
				}
			}
		}

	}
	
	public void switchToBrowser() {
		updateModel();
		
		if ( current instanceof Viewer) {
			selectedElement = ((Viewer)current).getSelectedElement();
		} else if ( current instanceof Designer) {
			selectedElement = ((Designer)current).getSelectedElement();
		} else if ( current instanceof Editor) {
			selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
		}else if ( current instanceof PluginViewPanel) {
			selectedElement = ((PluginViewPanel)current).getSelectedElement();
		    
		}
		
//		browser.update();
		
//		show( "Browser");
//		browser.setFocus();
//		setCurrent( browser);
	}

	/*public void switchToGrid() throws Exception {
	    
	    XElement previousElement = grid.getSelectedElement();
			    
		updateModel();
		
		if ( !document.isError() && document.isXML()) {
			if ( current instanceof Designer) {
				selectedElement = ((Designer)current).getSelectedElement();
			} else if ( current instanceof Editor) {
				selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
			} else if ( current instanceof PluginViewPanel) {
				selectedElement = ((PluginViewPanel)current).getSelectedElement();
			    
			    this.setDocument(((PluginViewPanel)current).getDocument());
			    updateModel();
			}

			if ( !grid.hasLatestInformation()) {
				grid.setDocument( document);
			}

			System.out.println("ExchangerView - switchToGridView - "+main.isAutoSynchroniseSelection());
			if (( main.isAutoSynchroniseSelection()) && (selectedElement != null)) {
			    //grid.setSelectedElement( selectedElement, null);
			    grid.selectElement(selectedElement);
			} else if(previousElement != null) {
				//grid.setSelectedElement( previousElement, null);
			    grid.selectElement(previousElement);
			}
			else {
			    
			}
			
			show( "Grid");
	//		viewerButton.setSelected( true);
	//		getViewerViewItem().setSelected( true);
		
			grid.setFocus();
			setCurrent( grid);
		} else {
			throw document.getError();
		}
		
	    
		//updateModel();
		
		//if ( current instanceof Viewer) {
		//	selectedElement = ((Viewer)current).getSelectedElement();
		//} else if ( current instanceof Designer) {
		//	selectedElement = ((Designer)current).getSelectedElement();
		//} else if ( current instanceof Editor) {
		//	selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
		//}
		
//		//browser.update();
		
		//show( "Grid");
		//grid.setFocus();
		//setCurrent( grid);
	}*/
	
public void switchToPluginView(PluginView pluginView) throws Exception {

	if((this.current!= null) && (this.current.equals(pluginView))) {
		//dont do anything
		//already at that view
	}
	else {
	    //XElement previousElement = pluginView.getPluginViewPanel().getSelectedElement();
		XElement previousElement = null;
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {
					if(pluginViewPanel.getPluginView() == pluginView) {
						previousElement = pluginViewPanel.getSelectedElement();
					}
										
				}				
			}
		}
			    
		updateModel();
		
		if ( !document.isError() && document.isXML()) {
			if ( current instanceof Designer) {
				selectedElement = ((Designer)current).getSelectedElement();
			} else if ( current instanceof Editor) {
				selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
			}else if ( current instanceof PluginViewPanel) {
				selectedElement = ((PluginViewPanel)current).getSelectedElement();
				ExchangerDocument exchangerDocument = ((PluginViewPanel)current).getDocument();
				if(exchangerDocument != null) {
					this.setDocument(exchangerDocument);
					updateModel();
				}
			}

			
				//pluginView.getPluginViewPanel().setDocument( document);
				for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
					Object obj = this.getPluginViewPanels().get(cnt);
					if((obj != null) && (obj instanceof PluginViewPanel)) {
						PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
						if(pluginViewPanel != null) {
							if(pluginViewPanel.getPluginView() == pluginView) {
								if ( pluginViewPanel.hasLatestInformation()) {
									pluginViewPanel.setDocument(document);
								}
							}
												
						}				
					}
				
			}

			if (( main.isAutoSynchroniseSelection()) && (selectedElement != null)) {
			    //grid.setSelectedElement( selectedElement, null);
				//pluginView.getPluginViewPanel().selectElement(selectedElement);
				for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
					Object obj = this.getPluginViewPanels().get(cnt);
					if((obj != null) && (obj instanceof PluginViewPanel)) {
						PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
						if(pluginViewPanel != null) {
							if(pluginViewPanel.getPluginView() == pluginView) {
								pluginViewPanel.selectElement(selectedElement);
							}
												
						}				
					}
				}
			} else if(previousElement != null) {
				//grid.setSelectedElement( previousElement, null);
				//pluginView.getPluginViewPanel().selectElement(previousElement);
				for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
					Object obj = this.getPluginViewPanels().get(cnt);
					if((obj != null) && (obj instanceof PluginViewPanel)) {
						PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
						if(pluginViewPanel != null) {
							if(pluginViewPanel.getPluginView() == pluginView) {
								pluginViewPanel.selectElement(previousElement);
							}
												
						}				
					}
				}
			}
			else {
			    
			}
			
			show( pluginView.getIdentifier());
	//		viewerButton.setSelected( true);
	//		getViewerViewItem().setSelected( true);
		
			for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
				Object obj = this.getPluginViewPanels().get(cnt);
				if((obj != null) && (obj instanceof PluginViewPanel)) {
					PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
					if(pluginViewPanel != null) {
						if(pluginViewPanel.getPluginView() == pluginView) {
							pluginViewPanel.setFocus();
							setCurrent(pluginViewPanel);
						}
											
					}				
				}
			}
			//pluginView.getPluginViewPanel().setFocus();
			//setCurrent( pluginView.getPluginViewPanel());
		} else {
			throw document.getError();
		}
		
		pluginView.getButton().setSelected(true);
		pluginView.getPluginViewItem().setSelected(true);
		/*updateModel();
		
		if ( current instanceof Viewer) {
			selectedElement = ((Viewer)current).getSelectedElement();
		} else if ( current instanceof Designer) {
			selectedElement = ((Designer)current).getSelectedElement();
		} else if ( current instanceof Editor) {
			selectedElement = document.getLastElement( ((Editor)current).getCursorPosition());
		}
		
//		browser.update();
		
		show( "Grid");
		grid.setFocus();
		setCurrent( grid);*/
	}
	}
	
	public void switchToUserView(UserView newUserView) throws Exception {
	    
	    //XElement previousElement = grid.getSelectedElement();
			    
		//updateModel();
		
		//if ( !document.isError() && document.isXML()) {
		newUserView.getButton().setSelected(true);
		//getViewerViewItem().setSelected(true);
		show( newUserView.getIdentifier());
		//.setFocus();
		setCurrent( newUserView.getPanel());
		
				//} else {
		//	throw document.getError();
		//}
		
	}

	/*
	 * Gets an icon for the string.
	 */
	public ImageIcon getIcon( String path) {
		if ( icons == null) {
			icons = new Hashtable();
		}
		
		ImageIcon icon = (ImageIcon)icons.get( path);
		
		if ( icon == null) {
			icon = XngrImageLoader.get().getImage( path);
			icons.put( path, icon);
		}
		
		return icon;
	}
	
	// Navigator settings.
	public NavigatorSettings getNavigatorSettings() {
		if ( navigatorSettings == null) {
			navigatorSettings = new NavigatorSettings();
		}

		return navigatorSettings;
	}
	
	public void cleanup() {
//		System.out.println( ((Object)this).hashCode()+" ["+document.getName()+"] cleanup()");
		removeFocusListener( focusListener);
		
		if ( tagSchemas != null) {
			tagSchemas.removeAllElements();
		}
		
		editor.cleanup();
		viewer.cleanup();
		changeManager.cleanup();
		document.cleanup();
		schemaViewer.cleanup();
		designer.cleanup();
		
//		for each of the plugin buttons
		/*for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					PluginViewPanel pluginViewPanel = pluginView.getPluginViewPanel();
					if(pluginViewPanel != null) {
						pluginViewPanel.cleanup();
					}
					
				}				
			}
		}*/
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {
					pluginViewPanel.cleanup();										
				}				
			}
		}
//		browser.cleanup();
		
		removeAll();

		finalize();
	}
	
	protected void finalize() {
//		System.out.println( "["+this.hashCode()+"] finalize()");

		schema	= null;
		tagSchemas	= null;
		selectedElement	= null; // sticky selection element
		icons = null;
		grammar	= null;
		validationGrammar = null;
		current	= null;
		
//		for each of the plugin buttons
		/*for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
			Object obj = main.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					pluginView.setPluginViewPanel(null);
					
				}				
			}
		}*/
		
		for(int cnt=0;cnt<this.getPluginViewPanels().size();++cnt) {
			Object obj = this.getPluginViewPanels().get(cnt);
			if((obj != null) && (obj instanceof PluginViewPanel)) {
				PluginViewPanel pluginViewPanel = (PluginViewPanel)obj;
				if(pluginViewPanel != null) {
					pluginViewPanel = null;
				}				
			}
		}
		this.setPluginViewPanels(new Vector());
		
		main = null;
		editor = null;
		designer = null;
		viewer = null;
		schemaViewer = null;
//		browser = null;
		changeManager = null;
		rootDialog = null;
		defaultValidationGrammar = null;
		document = null;
		

		
	}
	
	public JRadioButtonMenuItem getSchemaViewItem() {
		if (schemaItem == null) {
			schemaItem = new JRadioButtonMenuItem("Schema");
			schemaItem.setIcon( getIcon(ExchangerView.SCHEMA_ICON));
			schemaItem.setMnemonic('S');
			schemaItem.addItemListener(new SchemaItemListener());
		}

		return schemaItem;
	}

	public JRadioButtonMenuItem getEditorViewItem() {
		if (editorItem == null) {
			editorItem = new JRadioButtonMenuItem("Editor");
			editorItem.setIcon( getIcon(ExchangerView.EDITOR_ICON));
			editorItem.setMnemonic('E');
			editorItem.addItemListener(new EditorItemListener());
		}

		return editorItem;
	}

	public JRadioButtonMenuItem getBrowserViewItem() {
		if (browserItem == null) {
			browserItem = new JRadioButtonMenuItem("Browser");
			browserItem.setMnemonic('B');
			browserItem.addItemListener(new BrowserItemListener());
		}

		return browserItem;
	}

	public JRadioButtonMenuItem getDesignerViewItem() {
		if (designerItem == null) {
			designerItem = new JRadioButtonMenuItem("Outliner");
			designerItem.setIcon( getIcon(ExchangerView.DESIGNER_ICON));
			designerItem.setMnemonic('O');
			designerItem.addItemListener(new DesignerItemListener());
		}

		return designerItem;
	}

	public JRadioButtonMenuItem getViewerViewItem() {
		if (viewerItem == null) {
			viewerItem = new JRadioButtonMenuItem("Viewer");
			viewerItem.setIcon( getIcon(ExchangerView.VIEWER_ICON));
			viewerItem.setMnemonic('V');
			viewerItem.addItemListener(new ViewerItemListener());
		}

		return viewerItem;
	}
	
	/*public JRadioButtonMenuItem getGridViewItem() {
		if (gridItem == null) {
			gridItem = new JRadioButtonMenuItem("Grid");
			gridItem.setIcon( getIcon(GRID_ICON));
			gridItem.setMnemonic('G');
			gridItem.addItemListener(new GridItemListener());
		}

		return gridItem;
	}*/
	
    /**
     * @return Returns the userViews.
     */
    public Vector getUserViews() {

        return userViews;
    }
    /**
     * @param userViews The userViews to set.
     */
    public void setUserViews(Vector userViews) {

        this.userViews = userViews;
    }

	/**
	 * @param pluginViewPanels the pluginViewPanels to set
	 */
	public void setPluginViewPanels(Vector pluginViewPanels) {

		this.pluginViewPanels = pluginViewPanels;
	}

	/**
	 * @return the pluginViewPanels
	 */
	public Vector getPluginViewPanels() {

		return pluginViewPanels;
	}

	public void setSchemaButton(NavigationButton schemaButton) {
		this.schemaButton = schemaButton;
	}

	public NavigationButton getSchemaButton() {
		return schemaButton;
	}

	public void setEditorButton(NavigationButton editorButton) {
		this.editorButton = editorButton;
	}

	public NavigationButton getEditorButton() {
		return editorButton;
	}

	public void setDesignerButton(NavigationButton designerButton) {
		this.designerButton = designerButton;
	}

	public NavigationButton getDesignerButton() {
		return designerButton;
	}

	public void setViewerButton(NavigationButton viewerButton) {
		this.viewerButton = viewerButton;
	}

	public NavigationButton getViewerButton() {
		return viewerButton;
	}

	public void setDocumentViewButtonPanel(JPanel documentViewButtonPanel) {
		this.documentViewButtonPanel = documentViewButtonPanel;
	}

	public JPanel getDocumentViewButtonPanel() {
		return documentViewButtonPanel;
	}
    
	public void setDocumentViewsMenu(JMenu documentViewsMenu) {
		this.documentViewsMenu = documentViewsMenu;
	}

	public JMenu getDocumentViewsMenu() {
		return documentViewsMenu;
	}

	public class SchemaItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				ViewPanel current = ExchangerView.this.getCurrentView();

				if (ExchangerView.this != null && !(current instanceof SchemaViewer)) {
					try {
						main.switchToSchema();
					} catch (Exception e) {
						e.printStackTrace();

						if (current instanceof Editor) {
							ExchangerView.this.getEditorButton().setSelected(true);
							getEditorViewItem().setSelected(true);
						} else if (current instanceof Designer) {
							ExchangerView.this.getDesignerButton().setSelected(true);
							getDesignerViewItem().setSelected(true);
						} else if (current instanceof Viewer) {
							ExchangerView.this.getViewerButton().setSelected(true);
							getViewerViewItem().setSelected(true);
						/*} else if (current instanceof Grid) {
						    gridButton.setSelected(true);
						    getGridViewItem().setSelected(true);*/
						} else if (current instanceof PluginViewPanel) {
//							for each of the plugin buttons
							for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
								Object obj = main.getPluginViews().get(cnt);
								if((obj != null) && (obj instanceof PluginView)) {
									PluginView pluginView = (PluginView)obj;
									if(pluginView != null) {
										if(pluginView.equals(current)) {
										
											NavigationButton pluginButton = pluginView.getButton();
											if(pluginButton != null) {
												pluginButton.setSelected(true);
											}
											JRadioButtonMenuItem pluginMenuItem = pluginView.getPluginViewItem();
											if(pluginMenuItem != null) {
												pluginMenuItem.setSelected(true);
											}
										}										
									}				
								}
							}
							
						}

						MessageHandler.showMessage( "Please ensure the document is a valid XML Schema\nbefore switching to the \"Schema Viewer\".");
						current.setFocus();
					}
				}
			}
		}
	}

	public class DesignerItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				ViewPanel current = ExchangerView.this.getCurrentView();
				if (ExchangerView.this != null && !(current instanceof Designer)) {
					try {
						main.switchToDesigner();
					} catch (Exception e) {
						e.printStackTrace();

						if (current instanceof Editor) {
							ExchangerView.this.getEditorButton().setSelected(true);
							getEditorViewItem().setSelected(true);
						} else if (current instanceof SchemaViewer) {
							ExchangerView.this.getSchemaButton().setSelected(true);
							getSchemaViewItem().setSelected(true);
						/*} else if (current instanceof Grid) {
						    gridButton.setSelected(true);
						    getGridViewItem().setSelected(true);*/
						
//						} else if (current instanceof Browser) {
//							browserButton.setSelected(true);
//							getBrowserViewItem().setSelected(true);
						}else if (current instanceof PluginViewPanel) {
//							for each of the plugin buttons
							for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
								Object obj = main.getPluginViews().get(cnt);
								if((obj != null) && (obj instanceof PluginView)) {
									PluginView pluginView = (PluginView)obj;
									if(pluginView != null) {
										if(pluginView.equals(current)) {
										
											NavigationButton pluginButton = pluginView.getButton();
											if(pluginButton != null) {
												pluginButton.setSelected(true);
											}
											JRadioButtonMenuItem pluginMenuItem = pluginView.getPluginViewItem();
											if(pluginMenuItem != null) {
												pluginMenuItem.setSelected(true);
											}
										}										
									}				
								}
							}
							
						}
						MessageHandler.showMessage( "Please ensure the document is well-formed\nbefore switching to the \"Outliner\".");

						current.setFocus();
					}
				}
			}
		}
	}

	public class EditorItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
		    if (event.getStateChange() == ItemEvent.SELECTED) {
				if (ExchangerView.this != null
					&& !(ExchangerView.this.getCurrentView() instanceof Editor)) {
					main.switchToEditor();
				}
			}
		}
	}

	public class ViewerItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				ViewPanel current = ExchangerView.this.getCurrentView();
				if (ExchangerView.this != null && !(current instanceof Viewer)) {
					try {
						main.switchToViewer();
					} catch (Exception e) {
						e.printStackTrace();
						if (current instanceof Editor) {
							ExchangerView.this.getEditorButton().setSelected(true);
							getEditorViewItem().setSelected(true);
						} else if (current instanceof SchemaViewer) {
							ExchangerView.this.getSchemaButton().setSelected(true);
							getSchemaViewItem().setSelected(true);
						/*} else if (current instanceof Grid) {
						    gridButton.setSelected(true);
						    getGridViewItem().setSelected(true);*/
						
//						} else if (current instanceof Browser) {
//							browserButton.setSelected(true);
//							getBrowserViewItem().setSelected(true);
						}else if (current instanceof PluginViewPanel) {
//							for each of the plugin buttons
							for(int cnt=0;cnt<main.getPluginViews().size();++cnt) {
								Object obj = main.getPluginViews().get(cnt);
								if((obj != null) && (obj instanceof PluginView)) {
									PluginView pluginView = (PluginView)obj;
									if(pluginView != null) {
										if(pluginView.equals(current)) {
										
											NavigationButton pluginButton = pluginView.getButton();
											if(pluginButton != null) {
												pluginButton.setSelected(true);
											}
											JRadioButtonMenuItem pluginMenuItem = pluginView.getPluginViewItem();
											if(pluginMenuItem != null) {
												pluginMenuItem.setSelected(true);
											}
										}										
									}				
								}
							}
							
						}
						MessageHandler.showMessage(	"Please ensure the document is well-formed\nbefore switching to the \"Viewer\".");
						current.setFocus();
					}
				}
			}
		}
	}

	public class BrowserItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
//			if (event.getStateChange() == ItemEvent.SELECTED) {
//				ViewPanel current = currentView.getCurrentView();
//				if (currentView != null && !(current instanceof Browser)) {
//					try {
//						switchToBrowser();
//					} catch (Exception e) {}
//				}
//			}
		}
	}
	
	/*private class GridItemListener implements ItemListener {
		public void itemStateChanged(ItemEvent event) {
			if (event.getStateChange() == ItemEvent.SELECTED) {
				ViewPanel current = currentView.getCurrentView();
				if (currentView != null && !(current instanceof Grid)) {
					try {
						switchToGrid();
					} catch (Exception e) {
					    e.printStackTrace();
						if (current instanceof Editor) {
							getEditorButton().setSelected(true);
							getEditorViewItem().setSelected(true);
						} else if (current instanceof SchemaViewer) {
							getSchemaButton().setSelected(true);
							getSchemaViewItem().setSelected(true);
//						} else if (current instanceof Browser) {
//							browserButton.setSelected(true);
//							getBrowserViewItem().setSelected(true);
						}else if (current instanceof PluginViewPanel) {
//							for each of the plugin buttons
							for(int cnt=0;cnt<getPluginViews().size();++cnt) {
								Object obj = getPluginViews().get(cnt);
								if((obj != null) && (obj instanceof PluginView)) {
									PluginView pluginView = (PluginView)obj;
									if(pluginView != null) {
										if(pluginView.equals(current)) {
										
											NavigationButton pluginButton = pluginView.getButton();
											if(pluginButton != null) {
												pluginButton.setSelected(true);
											}
											JRadioButtonMenuItem pluginMenuItem = pluginView.getPluginViewItem();
											if(pluginMenuItem != null) {
												pluginMenuItem.setSelected(true);
											}
										}										
									}				
								}
							}
							
						}
						
						MessageHandler.showMessage(	"Please ensure the document is well-formed\nbefore switching to the \"Grid\".");
						current.setFocus();
					}
				}
			}
		}
	}*/
} 
