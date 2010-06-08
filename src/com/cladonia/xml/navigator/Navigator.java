/*
 * $Id: Navigator.java,v 1.14 2005/03/21 14:35:46 tcurley Exp $
 *
 * Copyright (C) 2003 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.navigator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;
import org.dom4j.Attribute;
import org.dom4j.Namespace;
import org.dom4j.Text;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerDocumentEvent;
import com.cladonia.xml.ExchangerDocumentListener;
import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ViewPanel;
import com.cladonia.xngreditor.ViewTreePanel;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.NamedXPathProperties;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * Shows a tree view of a XML document.
 *
 * @version	$Revision: 1.14 $, $Date: 2005/03/21 14:35:46 $
 * @author Dogsbay
 */
public class Navigator extends ViewTreePanel implements ExchangerDocumentListener {
	private static final boolean DEBUG = false;

	public static final int SELECTED_NAMESPACE_TYPE_ALL = -1;
	public static final int SELECTED_NAMESPACE_TYPE_NONE = 0;
	public static final int SELECTED_NAMESPACE_TYPE_OTHER = 1;

	private static final ImageIcon BADGE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Arrow6.gif");

	private static final ImageIcon PLAY_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Play16.gif");
	private static final ImageIcon XPATH_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/XPathSearch16.gif");
	private static final ImageIcon XPATH_BADGE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/XPathSearchBadge16.gif");

	private static final ImageIcon ALL_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16IconAll.gif");
	private static final ImageIcon ALL_BADGE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIconAll.gif");
	private static final ImageIcon PROPERTIES_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/PreferencesBadge16.gif");
	private static final ImageIcon ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ShowElementValues16.gif");
	private static final ImageIcon ATTRIBUTE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ShowAttributes16.gif");

	public static final ImageIcon[] ELEMENT_ICONS = 
	{ 
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon1.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon2.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon3.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon4.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon5.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon6.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon7.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon8.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon9.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16Icon10.gif")
	};

	public static final ImageIcon[] ELEMENT_BADGE_ICONS = 
	{ 
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon1.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon2.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon3.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon4.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon5.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon6.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon7.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon8.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon9.gif"),
		XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NavigatorElement16BadgeIcon10.gif")
	};

	private XmlTree tree = null;
	private JScrollPane scrollPane 			= null;
	private NavigatorProperties properties	= null;
	private boolean selecting = false;
	private SelectedNamespaceAction selectedNamespaceAction		= null;
	private NamespaceSelectionAction namespaceSelectionAction	= null;

	private NavigatorSettings settings = null;

	private ComponentAdapter resizeListener = null;
	
	private ExchangerDocument document			= null;
	private ExchangerEditor xngreditor 				= null;
	
	private CollapseAllAction collapseAllAction	= null;
	private ExpandAllAction expandAllAction		= null;

	private JPopupMenu popup = null;

	private Vector namedXPaths			= null;

	private NamedXPathSelectionAction namedXPathSelectionAction = null;
	private JPanel xpathPanel					= null;
	private JTextField xpathField				= null;
	private JLabel xpathLabel					= null;
	private JButton xpathButton					= null;

	private JToggleButton xpathSelector				= null;
	private JToggleButton selectedNamespaceButton	= null;

	private PropertiesAction propertiesAction 				= null;
	private JPopupMenu propertiesPopup 						= null;
	private JCheckBoxMenuItem showAttributesMenuItem		= null;
	private JCheckBoxMenuItem showAttributeNamesMenuItem	= null;
	private JCheckBoxMenuItem showElementContentMenuItem	= null;
	private JCheckBoxMenuItem showElementNamesMenuItem		= null;

	private boolean attributeInResult		= false;
	private boolean elementContentInResult	= false;

	private boolean hasLatest = false;

	/**
	 * Constructs a Navigator view.
	 *
	 * @param properties the navigator properties.
	 */
	public Navigator( ExchangerEditor parent, NavigatorProperties properties) {
		super( new BorderLayout());
		
		this.xngreditor = parent;
		this.properties = properties;

		try {
			tree = new XmlTree( this);
		} catch (Exception e) {
			e.printStackTrace();
			// should not happen
		}
		
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);

		AbstractAction selectAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e) {
				XElement element = tree.getSelectedElement();

				if ( element != null) {
					ViewPanel current = xngreditor.getCurrent();
					
					if ( current instanceof Viewer) {
						((Viewer)current).setSelectedElement( element, false, -1);
						((Viewer)current).setFocus();
					} else if ( current instanceof Designer) {
						((Designer)current).setSelectedNode( element, -1);
						((Designer)current).setFocus();
					} else if ( current instanceof Editor) {
						selecting = true;
						((Editor)current).selectElement( element);
						((Editor)current).setFocus();
						selecting = false;
					} else if ( current instanceof PluginViewPanel) {
						((PluginViewPanel)current).selectElement( element);
						((PluginViewPanel)current).setFocus();
					}
				}
			}
		};
		
		tree.getActionMap().put( "selectAction", selectAction);

		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "selectAction");
		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, false), "toggle");

		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged( TreeSelectionEvent e) {
				TreePath path = tree.getSelectionPath();
				if ( path != null) {
					XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
					XElement element = node.getElement();
					
					if ( element != null) {
						xngreditor.getXPathEditor().setXPath( node.getElement());
//						xngreditor.synchronise( node.getElement());
					}
				}
			}
		});
		
		tree.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TreePath path = tree.getPathForLocation( e.getX(), e.getY());

				if ( path != null) {
					XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
					XElement element = node.getElement();
					
					ViewPanel current = xngreditor.getCurrent();
					
					if ( current instanceof Viewer) {
						((Viewer)current).setSelectedElement( element, false, -1);
						((Viewer)current).setFocus();
					} else if ( current instanceof Designer) {
						((Designer)current).setSelectedNode( element, -1);
						((Designer)current).setFocus();
					} else if ( current instanceof Editor) {
						selecting = true;
						((Editor)current).selectElement( element);
						((Editor)current).setFocus();
						selecting = false;
					} else if ( current instanceof PluginViewPanel) {
						((PluginViewPanel)current).selectElement( element);
						((PluginViewPanel)current).setFocus();
					}
				}
			}
		});

		collapseAllAction = new CollapseAllAction( this);
		expandAllAction = new ExpandAllAction( this);
		
		JToolBar toolbar = new JToolBar();
		toolbar.setRollover( true);
		toolbar.setFloatable( false);
		toolbar.add( collapseAllAction).setMnemonic(0);
		toolbar.add( expandAllAction).setMnemonic(0);

        toolbar.addSeparator();

		propertiesPopup = new JPopupMenu();
		
		showAttributesMenuItem = new JCheckBoxMenuItem( "Show Attributes");
		showAttributesMenuItem.setSelected( false);
//		showAttributesMenuItem.addItemListener( new ItemListener(){
//			public void itemStateChanged( ItemEvent e) {
		showAttributesMenuItem.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
//				showAttributeNamesMenuItem.setEnabled( showAttributesMenuItem.isSelected());
				updateOutlineInternal();
			}
		});
		propertiesPopup.add( showAttributesMenuItem);

		showAttributeNamesMenuItem = new JCheckBoxMenuItem( "Show Attribute Names");
		showAttributeNamesMenuItem.setSelected( true);
//		showAttributeNamesMenuItem.addItemListener( new ItemListener(){
//			public void itemStateChanged( ItemEvent e) {
		showAttributeNamesMenuItem.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				tree.revalidate();
				tree.repaint();
				tree.updateUI();
			}
		});
		propertiesPopup.add( showAttributeNamesMenuItem);
		propertiesPopup.addSeparator();

		showElementContentMenuItem = new JCheckBoxMenuItem( "Show Element Content");
		showElementContentMenuItem.setSelected( true);
		showElementContentMenuItem.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				updateOutlineInternal();
			}
		});
		propertiesPopup.add( showElementContentMenuItem);

		showElementNamesMenuItem = new JCheckBoxMenuItem( "Show Element Names");
		showElementNamesMenuItem.setSelected( true);
//		showElementNamesMenuItem.addItemListener( new ItemListener(){
//			public void itemStateChanged( ItemEvent e) {
		showElementNamesMenuItem.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				tree.revalidate();
				tree.repaint();
				tree.updateUI();
			}
		});
		propertiesPopup.add( showElementNamesMenuItem);

		propertiesAction = new PropertiesAction();
        

		selectedNamespaceAction = new SelectedNamespaceAction();
		selectedNamespaceButton = new JToggleButton();

		selectedNamespaceButton.setAction( selectedNamespaceAction);
		selectedNamespaceButton.setText( null);
        toolbar.add( selectedNamespaceButton);
        namespaceSelectionAction = new NamespaceSelectionAction();
        
        toolbar.add( namespaceSelectionAction);
        
        scrollPane = new JScrollPane(	tree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		/**
		 * Work around to make sure the scroll pane shows the vertical 
		 * scrollbar for the first time when resized to a size small enough.
		 * JDK 1.3.0-C 
		 *
		 * Got work around from Bug ID: 4243631 (It should be fixed...)
		 *
		 * ED: Check with JDK1.4
		 */
		resizeListener = new ComponentAdapter() {
			public void componentResized( ComponentEvent e) {
				scrollPane.doLayout();
			}
		};
		
		scrollPane.getViewport().addComponentListener( resizeListener);

		this.setBorder( new EmptyBorder( 0, 0, 0, 0));
		
		JPanel toolPanel = new JPanel( new BorderLayout());
		toolPanel.add( toolbar, BorderLayout.NORTH);
		
		xpathField = new JTextField();
		xpathField.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					xpathChanged();
				}
			}
		});
		
		xpathPanel = new JPanel( new FormLayout( 2, 2));
		xpathPanel.setBorder( new EmptyBorder( 2, 0, 2, 0));

		xpathButton = new JButton( PLAY_ICON);
		xpathButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				xpathChanged();
			}
		});
		xpathButton.setMargin( new Insets( 0, 0, 0, 0));
		xpathButton.setFocusPainted( false);
		xpathButton.setToolTipText( "XPath Filter");

        xpathSelector = new JToggleButton( "XPath Filter", XPATH_ICON);
		xpathSelector.setText( null);
		xpathSelector.addItemListener( new ItemListener() {
        	public void itemStateChanged( ItemEvent e) {
       			toggleXPathPanel( xpathSelector.isSelected());
        	} 
        });
		xpathSelector.setToolTipText( "XPath Filter");

		toolbar.add( xpathSelector);
		namedXPathSelectionAction = new NamedXPathSelectionAction();
		toolbar.add( namedXPathSelectionAction);
		namedXPathSelectionAction.setEnabled( false);
		
		ButtonGroup group = new ButtonGroup();
		group.add( selectedNamespaceButton);
		group.add( xpathSelector);

		selectedNamespaceButton.setSelected( true);
		selectedNamespaceButton.addItemListener( new ItemListener() {
        	public void itemStateChanged( ItemEvent e) {
        		if ( selectedNamespaceButton.isSelected()) {
        			settings.setXPathAttributesSelected( showAttributesMenuItem.isSelected());
        			settings.setXPathElementValuesSelected( showElementContentMenuItem.isSelected());
        			settings.setXPathElementNamesSelected( showElementNamesMenuItem.isSelected());
        			settings.setXPathAttributeNamesSelected( showAttributeNamesMenuItem.isSelected());

        			showAttributesMenuItem.setSelected( settings.isAttributesSelected());
        			showElementContentMenuItem.setSelected( settings.isElementValuesSelected());
        			showElementNamesMenuItem.setSelected( settings.isElementNamesSelected());
        			showAttributeNamesMenuItem.setSelected( settings.isAttributeNamesSelected());
        		} else {
        			settings.setAttributesSelected( showAttributesMenuItem.isSelected());
        			settings.setElementValuesSelected( showElementContentMenuItem.isSelected());
        			settings.setElementNamesSelected( showElementNamesMenuItem.isSelected());
        			settings.setAttributeNamesSelected( showAttributeNamesMenuItem.isSelected());

        			showAttributesMenuItem.setSelected( settings.isXPathAttributesSelected());
        			showElementContentMenuItem.setSelected( settings.isXPathElementValuesSelected());
        			showElementNamesMenuItem.setSelected( settings.isXPathElementNamesSelected());
        			showAttributeNamesMenuItem.setSelected( settings.isXPathAttributeNamesSelected());
        		}
        	} 
        });

		toolbar.addSeparator();
        toolbar.add( Box.createHorizontalGlue());
        toolbar.add( propertiesAction);

        xpathLabel = new JLabel( "XPath:");
        xpathLabel.setBorder( new EmptyBorder( 0, 0, 0, 2));
        
        JPanel tempXPathPanel = new JPanel( new BorderLayout());
        
		xpathPanel.add( xpathLabel, FormLayout.LEFT);
		tempXPathPanel.add( xpathField, BorderLayout.CENTER);
		tempXPathPanel.add( xpathButton, BorderLayout.EAST);
		xpathPanel.add( tempXPathPanel, FormLayout.RIGHT_FILL);

		toolPanel.add( xpathPanel, BorderLayout.CENTER);

		this.add( toolPanel, BorderLayout.NORTH);
		this.add( scrollPane, BorderLayout.CENTER);

		((XmlCellRenderer)tree.getCellRenderer()).setFont( TextPreferences.getBaseFont());

		xpathPanel.setVisible( false);
		xpathSelector.setEnabled( false);
		propertiesAction.setEnabled( false);
	}
	
	private void toggleXPathPanel( boolean selected) {
		xpathPanel.setVisible( selected);

		xpathChanged();
	}
	
	private void xpathChanged() { 
//		(new Exception()).printStackTrace();
		if ( isVisible()) {
			xngreditor.setStatus( "Updating Navigator ...");
			xngreditor.setWait( true);
	
			// Run in Thread!!!
			Thread runner = new Thread() {
				public void run()  {
					try {
						// need to do this otherwise it won't update the gui
						sleep(50);
					} catch (Exception e) {}
	
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							try {
								updateOutlineInternal();
							} finally {
								xngreditor.setStatus( "Done");
								xngreditor.setWait( false);
							}
					    }
					});
				}
			};
			
			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
		}
	}
	
	public boolean isShowAttributes() {
		return properties.isShowAttributeValues();
	}
	
	private void updateSettings() {
		settings.setXPathSelected( xpathPanel.isVisible());
		
		if ( xpathPanel.isVisible()) {
			settings.setXPathAttributesSelected( showAttributesMenuItem.isSelected());
			settings.setXPathElementValuesSelected( showElementContentMenuItem.isSelected());
			settings.setXPathElementNamesSelected( showElementNamesMenuItem.isSelected());
			settings.setXPathAttributeNamesSelected( showAttributeNamesMenuItem.isSelected());
		} else {
			settings.setAttributesSelected( showAttributesMenuItem.isSelected());
			settings.setElementValuesSelected( showElementContentMenuItem.isSelected());
			settings.setElementNamesSelected( showElementNamesMenuItem.isSelected());
			settings.setAttributeNamesSelected( showAttributeNamesMenuItem.isSelected());
		}
		
		settings.setType( selectedNamespaceAction.getType());
		settings.setNamespace( selectedNamespaceAction.getNamespace());

		settings.setXPathValue( xpathField.getText());
	}
	
	/**
	 * The tab is selected, make sure everything is up to date.
	 */
	public void updateOutline() {
		if (DEBUG) System.out.println( "Navigator.updateOutline()");
		
		if ( SwingUtilities.isEventDispatchThread()) {
			updateOutlineInternal();
		} else {
			SwingUtilities.invokeLater( new Runnable() {
			    public void run() {
					updateOutlineInternal();
			    }
			});
		}
	}
	
	public boolean showAttributes() {
		return showAttributesMenuItem.isSelected() || !showAttributesMenuItem.isEnabled();
	}
	
	public boolean showAttributeNames() {
		return showAttributeNamesMenuItem.isSelected();
	}

	public boolean showElementContent() {
		return showElementContentMenuItem.isSelected();
	}

	public boolean showElementNames() {
		return showElementNamesMenuItem.isSelected();
	}

	private void updateOutlineInternal() {
		if (DEBUG) System.out.println( "Navigator.updateOutline()");
		
		boolean showAll = false;

		if ( document != null) {
			XElement root = (XElement)document.getLastRoot();
			
			String xpathValue = null;
			
			if ( xpathPanel.isVisible()) {
				xpathValue = xpathField.getText();
			} else {
				int type = selectedNamespaceAction.getType();
				Namespace namespace = selectedNamespaceAction.getNamespace();
				
				if ( showAttributes() && showElementContent()) {
					if ( type == SELECTED_NAMESPACE_TYPE_ALL) {
						showAll = true;

						xpathValue = "//*|//text()|//@*";
					} else if ( type == SELECTED_NAMESPACE_TYPE_NONE) {
						xpathValue = "//*[not(namespace-uri())]|//*[not(namespace-uri())]/text()|//*[not(namespace-uri())]/@*";
					} else if ( type == SELECTED_NAMESPACE_TYPE_OTHER) {
						String prefix = namespace.getPrefix();
						String uri = namespace.getURI();

						if ( prefix != null && prefix.trim().length() > 0) {
							xpathValue = "//"+prefix+":*|//"+prefix+":*/text()|//"+prefix+":*/@*";
						} else {
							xpathValue = "//*[namespace-uri()='"+uri+"']|//*[namespace-uri()='"+uri+"']/text()|//*[namespace-uri()='"+uri+"']/@*";
						}
					}
				} else if ( showAttributes() && !showElementContent()) {
					if ( type == SELECTED_NAMESPACE_TYPE_ALL) {
						showAll = true;

						xpathValue = "//*|//@*";
					} else if ( type == SELECTED_NAMESPACE_TYPE_NONE) {
						xpathValue = "//*[not(namespace-uri())]|//*[not(namespace-uri())]/@*";
					} else if ( type == SELECTED_NAMESPACE_TYPE_OTHER) {
						String prefix = namespace.getPrefix();
						String uri = namespace.getURI();

						if ( prefix != null && prefix.trim().length() > 0) {
							xpathValue = "//"+prefix+":*|//"+prefix+":*/@*";
						} else {
							xpathValue = "//*[namespace-uri()='"+uri+"']|//*[namespace-uri()='"+uri+"']/@*";
						}
					}
				} else if ( !showAttributes() && showElementContent()) {
					if ( type == SELECTED_NAMESPACE_TYPE_ALL) {
						showAll = true;

						xpathValue = "//*|//text()";
					} else if ( type == SELECTED_NAMESPACE_TYPE_NONE) {
						xpathValue = "//*[not(namespace-uri())]|//*[not(namespace-uri())]/text()";
					} else if ( type == SELECTED_NAMESPACE_TYPE_OTHER) {
						String prefix = namespace.getPrefix();
						String uri = namespace.getURI();

						if ( prefix != null && prefix.trim().length() > 0) {
							xpathValue = "//"+prefix+":*|//"+prefix+":*/text()";
						} else {
							xpathValue = "//*[namespace-uri()='"+uri+"']|//*[namespace-uri()='"+uri+"']/text()";
						}
					}
				} else {
					if ( type == SELECTED_NAMESPACE_TYPE_ALL) {
						showAll = true;

						xpathValue = "//*";
					} else if ( type == SELECTED_NAMESPACE_TYPE_NONE) {
						xpathValue = "//*[not(namespace-uri())]";
					} else if ( type == SELECTED_NAMESPACE_TYPE_OTHER) {
						String prefix = namespace.getPrefix();
						String uri = namespace.getURI();

						if ( prefix != null && prefix.trim().length() > 0) {
							xpathValue = "//"+prefix+":*";
						} else {
							xpathValue = "//*[namespace-uri()='"+uri+"']";
						}
					}
				}
			}
			
			Vector results = null;

			try {
				results = document.searchLastWellFormedDocument( xpathValue);

				xpathField.setForeground( Color.black);
			} catch ( Exception e) {
				if ( xpathField.getText() != null && xpathField.getText().trim().length() > 0) {
					xpathField.setForeground( Color.red);
				}
			}

			elementContentInResult = false;
			attributeInResult = false;

			if ( root != null) {
				long time = System.currentTimeMillis();
				
//				System.out.println( "Building Tree...");
				Vector shrinkingNodes = new Vector();

				if ( results != null) {
					for ( int i = 0; i < results.size(); i++) {
						Object object = results.elementAt(i); 

						shrinkingNodes.addElement( object);
						if ( object instanceof Text) {
							elementContentInResult = true;
						}

						if ( object instanceof Attribute) {
							attributeInResult = true;
						}
					}
				}
				XmlElementNode node = new XmlElementNode( this, (XElement)document.getLastRoot(), results, shrinkingNodes, getDeclaredNamespaces(), true, showAll);
				time = System.currentTimeMillis() - time;
//				System.out.println( "Tree Build ["+time+"]");

//				System.out.println( "Showing Tree!");
				tree.setRoot( node);
				tree.expand( 3);

				forceSelectedElement( xngreditor.getView().getSelectedElement());
//				System.out.println( "Tree Shown!");
			} else {
				tree.setRoot( null);
			}
		} else {
			tree.setRoot( null);
		}

		hasLatest = true;
	}

	/**
	 * Return the current selected element.
	 *
	 * @return the current selected Element.
	 */
	public XElement getSelectedElement() {
		return tree.getSelectedElement();
	}

	public void setFocus() {
		tree.requestFocusInWindow();
	}

	/**
	 * Update the preferences.
	 */
	public void updatePreferences() {
		tree.updateUI();
	}

	/**
	 * Adds an element to the list of selected elements.
	 *
	 * @param element the element to select.
	 */
	public void addSelectedElement( XElement element) {
		tree.setSelectedNode( element);
	}

	/**
	 * Clears the current selection.
	 */
	public void clearSelection() {
		tree.clearSelection();
	}

	public void setProperties() {
	}

	/**
	 * Check to find out if namespaces should be visible.
	 *
	 * @return true if namespaces are visible.
	 */
	public void setSelectedElement( XElement element) {
		if ( isVisible() && !selecting) {
			forceSelectedElement( element);
		}
	}

	/**
	 * Check to find out if namespaces should be visible.
	 *
	 * @return true if namespaces are visible.
	 */
	public void forceSelectedElement( XElement element) {
		tree.clearSelection();

		if ( element != null) {
			tree.setSelectedNode( element);
		}
	}

	/**
	 * Collapses all the nodes in the tree.
	 */
	public void collapseAll() {
		tree.collapseAll();
		tree.expand(2);
	}

	/**
	 * Collapses all the nodes in the tree.
	 *
	 * @param node the node to collapse all nodes for.
	 */
	public void collapseNode( XmlElementNode node) {
		tree.collapseNode( node);
	}

	/**
	 * Expands all the nodes in the tree.
	 */
	public void expandAll() {
		tree.expandAll();
	}

	/**
	 * Expands all the nodes in the tree from this node down.
	 *
	 * @param node the node to expand all nodes for.
	 */
	public void expandNode( XmlElementNode node) {
		tree.expandNode( node);
	}

	public void setDocument( ExchangerDocument document) {
		if (DEBUG) System.out.println( "Navigator.setDocument( "+document+")");
		
		if ( this.document != null) {
			this.document.removeListener( this);
		}
		
		if ( settings != null) {
			updateSettings();
		}
		
		this.document = document;

		if ( document != null) {
			settings = xngreditor.getView().getNavigatorSettings();

			expandAllAction.setEnabled( true);
			collapseAllAction.setEnabled( true);
			document.addListener( this);
		} else {
			settings = null;

			expandAllAction.setEnabled( false);
			collapseAllAction.setEnabled( false);
		}
		
		namedXPaths = null;
		xpathSelector.setEnabled( settings != null);
		propertiesAction.setEnabled( settings != null);
		selectedNamespaceAction.setEnabled( settings != null);
		namespaceSelectionAction.setEnabled( settings != null);

		namedXPathSelectionAction.setEnabled( false);

		if ( settings != null) {
			GrammarProperties grammar = xngreditor.getGrammar();

			if ( grammar != null && settings.isFirstTime()) {
				NamedXPathProperties properties = grammar.getDefaultNamedXPath();
				
				if ( properties != null) {
					settings.setXPathAttributeNamesSelected( properties.showAttributeNames());
					settings.setXPathElementNamesSelected( properties.showElementNames());
					settings.setXPathElementValuesSelected( properties.showElementContent());
					settings.setXPathAttributesSelected( properties.showAttributes());
					
					settings.setXPathValue( properties.getXPath());
					settings.setXPathSelected( true);
				}
			}

			selectedNamespaceAction.setNamespace( settings.getNamespace(), settings.getType());
			
			xpathField.setText( settings.getXPathValue());

			if ( settings.isXPathSelected()) {
				if ( xpathSelector.isSelected()) {
					showAttributesMenuItem.setSelected( settings.isXPathAttributesSelected());
					showElementContentMenuItem.setSelected( settings.isXPathElementValuesSelected());
					showAttributeNamesMenuItem.setSelected( settings.isXPathAttributeNamesSelected());
					showElementNamesMenuItem.setSelected( settings.isXPathElementNamesSelected());

					xpathChanged();
				} else {
					showAttributesMenuItem.setSelected( settings.isAttributesSelected());
					showElementContentMenuItem.setSelected( settings.isElementValuesSelected());
					showAttributeNamesMenuItem.setSelected( settings.isAttributeNamesSelected());
					showElementNamesMenuItem.setSelected( settings.isElementNamesSelected());

					xpathSelector.setSelected( true);
				}
			} else {
				if ( selectedNamespaceButton.isSelected()) {
					showAttributesMenuItem.setSelected( settings.isAttributesSelected());
					showElementContentMenuItem.setSelected( settings.isElementValuesSelected());
					showAttributeNamesMenuItem.setSelected( settings.isAttributeNamesSelected());
					showElementNamesMenuItem.setSelected( settings.isElementNamesSelected());

					xpathChanged();
				} else {
					showAttributesMenuItem.setSelected( settings.isXPathAttributesSelected());
					showElementContentMenuItem.setSelected( settings.isXPathElementValuesSelected());
					showAttributeNamesMenuItem.setSelected( settings.isXPathAttributeNamesSelected());
					showElementNamesMenuItem.setSelected( settings.isXPathElementNamesSelected());

					selectedNamespaceButton.setSelected( true);
				}
			}
			
			xpathPanel.setVisible( settings.isXPathSelected());
			
			showElementNamesMenuItem.setEnabled( true);

			if ( xpathSelector.isSelected()) {
				showAttributeNamesMenuItem.setEnabled( true);
			}

			if ( grammar != null) {
				Vector paths = grammar.getNamedXPaths();
				namedXPaths = new Vector();
				
				if ( paths.size() > 0) {
					for ( int i = 0; i < paths.size(); i++) {
						namedXPaths.add( new NamedXPathAction( (NamedXPathProperties)paths.elementAt(i)));
					}

					namedXPathSelectionAction.setEnabled( true);
				}
			}
		} else {
			xpathPanel.setVisible( false);
		}

		GrammarProperties grammar = xngreditor.getGrammar();

		if ( isVisible() && settings == null) {
			updateOutline();
		}
	}
	
	public boolean hasAttributesInResults() {
		return attributeInResult;
	}
		
	public boolean hasElementContentInResults() {
		return elementContentInResult;
	}
	
	public boolean hasLatestInformation() {
		return hasLatest;
	}
	
	private void namespaceSelectionChanged( Namespace namespace, int type) {
		selectedNamespaceButton.setSelected( true);
		selectedNamespaceAction.setNamespace( namespace, type);
		xpathChanged();
	}
	
	private Vector getDeclaredNamespaces() {
		Vector namespaces = new Vector();
		Vector uris = new Vector();
		Vector namespaceList = document.getDeclaredNamespaces();
		
		for ( int i = 0; i < namespaceList.size(); i++) {
			Namespace ns = (Namespace)namespaceList.elementAt(i);
			
			if ( !uris.contains( ns.getURI())) {
				uris.addElement( ns.getURI());
				namespaces.addElement( ns);
			}
		}
		
		return namespaces;
	}

	private void showNamespacePopup( ActionEvent event) {
		popup = new JPopupMenu();
		
		Vector namespaceList = getDeclaredNamespaces();

		popup.add( new SelectNamespaceAction( null, SELECTED_NAMESPACE_TYPE_ALL));
		popup.add( new SelectNamespaceAction( null, SELECTED_NAMESPACE_TYPE_NONE));

		for ( int i = 0; i < namespaceList.size(); i++) {
			popup.add( new SelectNamespaceAction( (Namespace)namespaceList.elementAt(i), SELECTED_NAMESPACE_TYPE_OTHER));
		}
		
		popup.show( (JButton)event.getSource(), 0, ((JButton)event.getSource()).getSize().height);
	}

	private void showPropertiesPopup( ActionEvent event) {
		propertiesPopup.show( (JButton)event.getSource(), 0, ((JButton)event.getSource()).getSize().height);
	}

	private void showNamedXPathsPopup( ActionEvent event) {
		JPopupMenu popup = new JPopupMenu();
		
		for ( int i = 0; i < namedXPaths.size(); i++) {
			popup.add( (NamedXPathAction)namedXPaths.elementAt(i));
		}
			
		popup.show( (JButton)event.getSource(), 0, ((JButton)event.getSource()).getSize().height);
	}
	
	private void executeNamedXPath( NamedXPathProperties properties) {
		if ( properties != null) {
			xpathField.setText( properties.getXPath());

			if ( xpathSelector.isSelected()) {
				showAttributesMenuItem.setSelected( properties.showAttributes());
				showElementContentMenuItem.setSelected( properties.showElementContent());
				showAttributeNamesMenuItem.setSelected( properties.showAttributeNames());
				showElementNamesMenuItem.setSelected( properties.showElementNames());

				xpathChanged();
			} else {
				settings.setXPathAttributeNamesSelected( properties.showAttributeNames());
				settings.setXPathElementNamesSelected( properties.showElementNames());
				settings.setXPathElementValuesSelected( properties.showElementContent());
				settings.setXPathAttributesSelected( properties.showAttributes());

				xpathSelector.setSelected( true);
			}
		}
	}

	// Implementation of the XDocumentListener interface...	
	public void documentUpdated( ExchangerDocumentEvent event) {
		if (DEBUG) System.out.println( "Navigator.documentUpdated( "+event+")");
		
		if ( settings != null) {
			updateSettings();
		}

		if ( isVisible()) {
			updateOutline();
		}
	}
	
	public void documentDeleted( ExchangerDocumentEvent event) {}
	
	public void cleanup() {
		tree.cleanup();
		
		scrollPane.getViewport().removeComponentListener( resizeListener);

		removeAll();

		finalize();
	}
	
	protected void finalize() {
		tree = null;
		scrollPane = null;

		document = null;
		xngreditor = null;
	}
	
	class CollapseAllAction extends AbstractAction {
		private Navigator navigator = null;

		public CollapseAllAction( Navigator navigator) {
			super( "Collapse All");
			
			this.navigator = navigator;
			
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CollapseAll.gif"));
			putValue( SHORT_DESCRIPTION, "Collapses All Nodes");
			
			setEnabled( false);
		}
		
		public void actionPerformed( ActionEvent event) {
			xngreditor.setStatus( "Updating Navigator ...");
			xngreditor.setWait( true);

			// Run in Thread!!!
			Thread runner = new Thread() {
				public void run()  {
					try {
						// need to do this otherwise it won't update the gui
						sleep(50);
					} catch (Exception e) {}

					SwingUtilities.invokeLater( new Runnable() {
					    public void run() {
							try {
								navigator.collapseAll();
							} finally {
								xngreditor.setStatus( "Done");
								xngreditor.setWait( false);
							}
					    }
					});
				}
			};
			
			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
		}
	}
	
	class ExpandAllAction extends AbstractAction {
		private Navigator navigator = null;

		public ExpandAllAction( Navigator navigator) {
			super( "Expand All");
			
			this.navigator = navigator;
			
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExpandAll.gif"));
			putValue( SHORT_DESCRIPTION, "Expand All Nodes");
			
			setEnabled( false);
		}
		
		public void actionPerformed( ActionEvent event) {
			xngreditor.setStatus( "Updating Navigator ...");
			xngreditor.setWait( true);

			// Run in Thread!!!
			Thread runner = new Thread() {
				public void run()  {
					try {
						// need to do this otherwise it won't update the gui
						sleep(50);
					} catch (Exception e) {}

					SwingUtilities.invokeLater( new Runnable() {
					    public void run() {
							try {
								navigator.expandAll();
							} finally {
								xngreditor.setStatus( "Done");
								xngreditor.setWait( false);
							}
					    }
					});
				}
			};
			
			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
		}
	}
	
	class SelectedNamespaceAction extends AbstractAction {
		private Namespace namespace = null;
		private int type = -1;
		
		public SelectedNamespaceAction() {
			setNamespace( null, SELECTED_NAMESPACE_TYPE_ALL);
		}
		
		public void setNamespace( Namespace namespace, int type) {
			this.namespace = namespace;
			String description = null;
			ImageIcon icon = null;
			this.type = type;
			
			if ( type == SELECTED_NAMESPACE_TYPE_ALL) {
				icon = ALL_ICON;
				description = "All Namespaces";
			} else if ( type == SELECTED_NAMESPACE_TYPE_NONE) {
				icon = ELEMENT_ICONS[0];
				description = "No Namespace";
			} else if ( namespace != null) {
				description = namespace.getURI();
				icon = ELEMENT_ICONS[ (getDeclaredNamespaces().indexOf( namespace)+1) % 10];
			}

			putValue( SMALL_ICON, icon);
			putValue( SHORT_DESCRIPTION, "Select Namespace ["+description+"]");
		}
		
		public int getType() {
			return type;
		}
		
		public Namespace getNamespace() {
			return namespace;
		}

		public void actionPerformed( ActionEvent event) {
			// show namespace selection...
			xpathChanged();
		}
	}

	class NamespaceSelectionAction extends AbstractAction {
		public NamespaceSelectionAction() {
			putValue( SMALL_ICON, BADGE_ICON);
			putValue( NAME, "Select Namespace");
			putValue( SHORT_DESCRIPTION, "Select a Namespace Filter");
		}
		
		public void actionPerformed( ActionEvent event) {
			// show namespace selection...
			showNamespacePopup( event);
		}
	}

	class SelectNamespaceAction extends AbstractAction {
		private Namespace namespace = null;
		private int type = -1;
		
		public SelectNamespaceAction( Namespace namespace, int type) {
			this.namespace = namespace;
			String description = null;
			ImageIcon icon = null;
			this.type = type;
			
			if ( type == SELECTED_NAMESPACE_TYPE_ALL) {
				icon = ALL_ICON;
				description = "All Namespaces";
			} else if ( type == SELECTED_NAMESPACE_TYPE_NONE) {
				icon = ELEMENT_ICONS[0];
				description = "Without Namespace";
			} else if ( namespace != null) {
//				String prefix = namespace.getPrefix();

//				if ( prefix != null && prefix.trim().length() > 0) {
//					description = namespace.getURI()+" ("+prefix+")";
//				} else {
				description = namespace.getURI();
//				}

				icon = ELEMENT_ICONS[ (getDeclaredNamespaces().indexOf( namespace)+1) % 10];
			}

			putValue( SMALL_ICON, icon);
			putValue( NAME, description);
			putValue( SHORT_DESCRIPTION, "Select Namespace ["+description+"]");
		}
		
		public void actionPerformed( ActionEvent event) {
			namespaceSelectionChanged( namespace, type);
		}
	}
	
	class PropertiesAction extends AbstractAction {
		public PropertiesAction() {
			putValue( SMALL_ICON, PROPERTIES_ICON);
			putValue( NAME, "Navigator Properties");
			putValue( SHORT_DESCRIPTION, "Select Navigator Properties");
		}
		
		public void actionPerformed( ActionEvent event) {
			showPropertiesPopup( event);
		}
	}

	class NamedXPathSelectionAction extends AbstractAction {
		public NamedXPathSelectionAction() {
			putValue( SMALL_ICON, BADGE_ICON);
			putValue( NAME, "Select Named XPath");
			putValue( SHORT_DESCRIPTION, "Select a Named XPath");
		}
		
		public void actionPerformed( ActionEvent event) {
			showNamedXPathsPopup( event);
		}
	}

	class NamedXPathAction extends AbstractAction {
		private NamedXPathProperties properties = null;

		public NamedXPathAction( NamedXPathProperties properties) {
			this.properties = properties;
//			putValue( SMALL_ICON, PROPERTIES_ICON);
			putValue( NAME, properties.getName());
			putValue( SHORT_DESCRIPTION, "Named XPath Filter: "+properties.getName());
		}
		
		public void actionPerformed( ActionEvent event) {
			if ( properties != null) {
				executeNamedXPath( properties);
			}
		}
	}
} 
