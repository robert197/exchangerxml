/*
 * $Id: SchemaViewerDetails.java,v 1.1 2004/03/25 18:39:54 edankert Exp $
 *
 * Copyright (C) 2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.border.EmptyBorder;

import com.cladonia.schema.ChoiceSchemaModel;
import com.cladonia.schema.SchemaAttribute;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaModel;
import com.cladonia.schema.SequenceSchemaModel;
import com.cladonia.schema.XMLSchema;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The details panel for the selected element in the Schema.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:39:54 $
 * @author Dogsbay
 */
public class SchemaViewerDetails extends JPanel {
	private static ImageIcon icon = null;

	private static ExchangerEditor _parent = null;
	private static SchemaDetailsDialog schemaDialog = null;

	private XMLSchema schema = null;
	
	private JPanel details = null;
	
	private ElementSelectionPanel refsPanel = null;
	private ElementSelectionPanel subsPanel = null;
	private ElementSelectionPanel globalsPanel = null;

	private ElementDetailsPanel elementPanel = null;
	private AnyElementDetailsPanel anyElementPanel = null;
	private AttributeDetailsPanel attributePanel = null;
	private AnyAttributeDetailsPanel anyAttributePanel = null;

	private ChoiceDetailsPanel choicePanel = null;
	private SequenceDetailsPanel sequencePanel = null;
	private AllDetailsPanel allPanel = null;
	
	private JPanel previousPanel = null;
	private JScrollPane scrollPane = null;
	
	public SchemaViewerDetails( ExchangerEditor parent) {
		super( new BorderLayout());
		
		_parent		= parent;
		
		elementPanel		= new ElementDetailsPanel( this);
		anyElementPanel		= new AnyElementDetailsPanel( this);

		attributePanel		= new AttributeDetailsPanel( this);
		anyAttributePanel	= new AnyAttributeDetailsPanel( this);
		
		choicePanel			= new ChoiceDetailsPanel( this);
		sequencePanel		= new SequenceDetailsPanel( this);
		allPanel			= new AllDetailsPanel( this);

		details 			= new DetailsPanel();
		
		JTabbedPane tabs	= new JTabbedPane();
		
		refsPanel 		= new ElementSelectionPanel( _parent, 5);
		subsPanel		= new ElementSelectionPanel( _parent, 5);
		globalsPanel	= new ElementSelectionPanel( _parent, 5);

		tabs.add( "Refs", refsPanel);
		tabs.add( "Subs", subsPanel);
		tabs.add( "Globals", globalsPanel);
		
		tabs.setBorder( new EmptyBorder( 2, 2, 2, 2));

		scrollPane = new JScrollPane(	details,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		add( tabs, BorderLayout.SOUTH);
		add( scrollPane, BorderLayout.CENTER);

//		tree = new TreePanel();
//		
//		tree.addTreePanelListener( new TreePanelListener() {
//			public void popupTriggered( MouseEvent event, SchemaNode node) {}
//			public void doubleClicked( MouseEvent event, SchemaNode node) {}
//			public void selectionChanged( SchemaNode node) {
//				if ( node != null) {
//					if ( previousPanel != null) {
//						details.remove( previousPanel);
//					}
//
//					if ( node instanceof ElementNode) {
//						SchemaElement element = ((ElementNode)node).getElement();
//						
//						if ( ((ElementNode)node).isReference()) {
//							((ElementNode)node).resolveReference();
//							tree.nodeChanged( node);
//						} else if ( element.isRecursive()) {
//							element.recurse();
//
//							((ElementNode)node).parse();
//							tree.nodeChanged( node);
//						}
//						
//						SchemaViewer.this.parent.getHelper().setElement( element);
//
//						elementPanel.setElement( element);
//						refsPanel.setElements( element.getReferers());
//						subsPanel.setElements( element.getSubstitutes());
//						previousPanel = elementPanel;
//					} else if ( node instanceof AnyElementNode) {
//					
//						SchemaViewer.this.parent.getHelper().setElement( ((AnyElementNode)node).getWildcard().getParentElement());
//						anyElementPanel.setWildcard( ((AnyElementNode)node).getWildcard());
//						refsPanel.setElements( null);
//						subsPanel.setElements( null);
//						previousPanel = anyElementPanel;
//					} else if ( node instanceof AttributeNode) {
//						SchemaViewer.this.parent.getHelper().setElement( ((AttributeNode)node).getAttribute().getParentElement());
//						attributePanel.setAttribute( ((AttributeNode)node).getAttribute());
//
//						SchemaAttribute attribute = ((AttributeNode)node).getAttribute();
//						refsPanel.setElements( attribute.getReferers());
//
//						subsPanel.setElements( null);
//						previousPanel = attributePanel;
//					} else if ( node instanceof AnyAttributeNode) {
//						SchemaViewer.this.parent.getHelper().setElement( ((AnyAttributeNode)node).getWildcard().getParentElement());
//						anyAttributePanel.setWildcard( ((AnyAttributeNode)node).getWildcard());
//						refsPanel.setElements( null);
//						subsPanel.setElements( null);
//						previousPanel = anyAttributePanel;
//					} else if ( node instanceof ContentModelNode) {
//						SchemaModel model = ((ContentModelNode)node).getModel();
//						SchemaViewer.this.parent.getHelper().setElement( model.getParentElement());
//
//						if ( model instanceof SequenceSchemaModel) {
//							sequencePanel.setContentModel( model);
//							previousPanel = sequencePanel;
//						} else if ( model instanceof ChoiceSchemaModel) {
//							choicePanel.setContentModel( model);
//							previousPanel = choicePanel;
//						} else {
//							allPanel.setContentModel( model);
//							previousPanel = allPanel;
//						}
//						refsPanel.setElements( null);
//						subsPanel.setElements( null);
//					}
//				} else {
//
//					if ( previousPanel != null) {
//						details.remove( previousPanel);
//					}
//
//					previousPanel = null;
//				}
//				
//				if ( previousPanel != null) {
//					details.add( previousPanel, BorderLayout.CENTER);
//				}
//				
//				details.doLayout();
//				details.revalidate();
//				details.repaint();
//			}
//		}); 

//		split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, tree, rightPanel);
//		split.setDividerLocation( properties.getDividerLocation());
//		split.addPropertyChangeListener( JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
//			public void propertyChange( PropertyChangeEvent e) {
//				SchemaViewer.this.properties.setDividerLocation( split.getDividerLocation());
//			}
//		});
//		split.setBorder( null);
//		((BasicSplitPaneUI)split.getUI()).getDivider().setBorder( null);

		updatePreferences();
	}
	
	public void updateHelper() {
//		SchemaNode node = tree.getSelectedNode();
//
//		if ( node != null) {
//			if ( node instanceof ElementNode) {
//				parent.getHelper().setElement( ((ElementNode)node).getElement());
//			} else if ( node instanceof AnyElementNode) {
//				parent.getHelper().setElement( ((AnyElementNode)node).getWildcard().getParentElement());
//			} else if ( node instanceof AttributeNode) {
//				parent.getHelper().setElement( ((AttributeNode)node).getAttribute().getParentElement());
//			} else if ( node instanceof AnyAttributeNode) {
//				parent.getHelper().setElement( ((AnyAttributeNode)node).getWildcard().getParentElement());
//			} else if ( node instanceof ContentModelNode) {
//				parent.getHelper().setElement( ((ContentModelNode)node).getModel().getParentElement());
//			}
//		}
	}
	
	public void setNode( SchemaNode node) {
		if ( node != null) {
			if ( previousPanel != null) {
				details.remove( previousPanel);
			}

			if ( node instanceof ElementNode) {
				SchemaElement element = ((ElementNode)node).getElement();
				
				elementPanel.setElement( element);
				refsPanel.setElements( element.getReferers());
				subsPanel.setElements( element.getSubstitutes());
				previousPanel = elementPanel;
			} else if ( node instanceof AnyElementNode) {
				anyElementPanel.setWildcard( ((AnyElementNode)node).getWildcard());

				refsPanel.setElements( null);
				subsPanel.setElements( null);
				previousPanel = anyElementPanel;
			} else if ( node instanceof AttributeNode) {
				attributePanel.setAttribute( ((AttributeNode)node).getAttribute());

				SchemaAttribute attribute = ((AttributeNode)node).getAttribute();
				refsPanel.setElements( attribute.getReferers());
				subsPanel.setElements( null);
				previousPanel = attributePanel;
			} else if ( node instanceof AnyAttributeNode) {
				anyAttributePanel.setWildcard( ((AnyAttributeNode)node).getWildcard());
				refsPanel.setElements( null);
				subsPanel.setElements( null);
				previousPanel = anyAttributePanel;
			} else if ( node instanceof ContentModelNode) {
				SchemaModel model = ((ContentModelNode)node).getModel();

				if ( model instanceof SequenceSchemaModel) {
					sequencePanel.setContentModel( model);
					previousPanel = sequencePanel;
				} else if ( model instanceof ChoiceSchemaModel) {
					choicePanel.setContentModel( model);
					previousPanel = choicePanel;
				} else {
					allPanel.setContentModel( model);
					previousPanel = allPanel;
				}
				refsPanel.setElements( null);
				subsPanel.setElements( null);
			}
		} else {
			if ( previousPanel != null) {
				details.remove( previousPanel);
			}

			previousPanel = null;
		}
		
		if ( previousPanel != null) {
			details.add( previousPanel, BorderLayout.CENTER);
		}
		
		details.doLayout();
		details.revalidate();
		details.repaint();
	}

	/**
	 * Update the preferences.
	 */
	public void updatePreferences() {
		Font font = TextPreferences.getBaseFont();

		refsPanel.setPreferredFont( font);
		subsPanel.setPreferredFont( font);
		globalsPanel.setPreferredFont( font);
	
		elementPanel.setPreferredFont( font);
		anyElementPanel.setPreferredFont( font);;
		attributePanel.setPreferredFont( font);
		anyAttributePanel.setPreferredFont( font);
	
		sequencePanel.setPreferredFont( font);
		allPanel.setPreferredFont( font);
		choicePanel.setPreferredFont( font);
		getSchemaDetailsDialog().setPreferredFont( font);
		
		refsPanel.updateUI();
		subsPanel.updateUI();
		globalsPanel.updateUI();
	
		elementPanel.updateUI();
		anyElementPanel.updateUI();
		attributePanel.updateUI();
		anyAttributePanel.updateUI();
	
		sequencePanel.updateUI();
		allPanel.updateUI();
		choicePanel.updateUI();
	}

//	public boolean isInitialised() {
//		return initialised;
//	}
//
//	public void initialise() {
//		initialised = true;
//
//		if ( schema != null) {
//			globalsPanel.setElements( schema.getGlobalElements());
//
//			if ( root != null) {
//				setRoot( root);
//			}
//		} else {
//			globalsPanel.setElements( null);
//		}
//		
//	}

	public void setSchema( XMLSchema schema) {
		this.schema = schema;
		
		if ( schema != null) {
			globalsPanel.setElements( schema.getGlobalElements());
		} else {
			globalsPanel.setElements( null);
		}
	}

	public static SchemaDetailsDialog getSchemaDetailsDialog() {
		if ( schemaDialog == null) {
//			try {
//				LicenseManager licenseManager = LicenseManager.getInstance();
//				licenseManager.isValid( com.joesmyth.license.KeyGenerator.generate(2), "Exchanger XML Editor");
//			} catch (Exception x) {
//				System.exit(0);
//				return null;
//			}

			schemaDialog = new SchemaDetailsDialog( _parent);
			schemaDialog.setLocationRelativeTo( _parent);
		}

		return schemaDialog;
	}
	
	public class DetailsPanel extends JPanel implements Scrollable {
		public DetailsPanel() {
			super( new BorderLayout());
		}

		public Dimension getPreferredScrollableViewportSize() {
			return getPreferredSize();
		}

		public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction) {
			return 20;
		}

		public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction ) {
			return 20;
		}

		public boolean getScrollableTracksViewportHeight() {
			boolean resize = false;

			if (getParent() instanceof JViewport) {
			    if ( ((JViewport)getParent()).getHeight() > getPreferredSize().height) {
					resize = true;
			    }
			}

			return resize;
		}

		public boolean getScrollableTracksViewportWidth() {
			return true;
		}
	}
	
	public void cleanup() {
		removeAll();
		
		finalize();
	}
	
	protected void finalize() {
		schema = null;
	
		details = null;
	
		refsPanel = null;
		subsPanel = null;
		globalsPanel = null;

		elementPanel = null;
		anyElementPanel = null;
		attributePanel = null;
		anyAttributePanel = null;

		choicePanel = null;
		sequencePanel = null;
		allPanel = null;
	
		previousPanel = null;
		scrollPane = null;
	}	
}