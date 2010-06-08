/*
 * $Id: Designer.java,v 1.9 2004/11/05 11:44:52 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.Keymap;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.undo.UndoableEditSupport;

import org.bounce.QTree;
import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;

import com.cladonia.schema.ComplexSchemaType;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaType;
import com.cladonia.schema.SimpleSchemaType;
import com.cladonia.schema.XMLSchema;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerDocumentEvent;
import com.cladonia.xml.ExchangerDocumentListener;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.actions.AddNodeAction;
import com.cladonia.xml.designer.actions.CreateRequiredAction;
import com.cladonia.xml.designer.actions.DeleteNodeAction;
import com.cladonia.xngreditor.ChangeManager;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.ViewTreePanel;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * A panel that holds a tree that can display explorer nodes.
 *
 * @version	$Revision: 1.9 $, $Date: 2004/11/05 11:44:52 $
 * @author Dogsbay
 */
public class Designer extends ViewTreePanel implements UndoableEditListener, ExchangerDocumentListener {
	private static final boolean DEBUG = false;

	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/DesignerIcon.gif");

	private ExchangerDocument document = null;
	private XMLSchema schema = null;

	private EventListenerList listeners = null;
	private UndoableEditSupport undoSupport = null;

	private JPopupMenu popup = null;

	private QTree tree = null;
	private JScrollPane scrollPane = null;
	private DefaultTreeModel treeModel = null;

	private DesignerNode root = null;
	private DesignerNode selectedNode = null;
	
	private JSplitPane split = null;

	private JPanel updatePanel = null;
	private JLabel nameLabel = null;
	private JLabel typeLabel = null;
	private JTextArea valueEditor = null;
	
	private DeleteNodeAction deleteAction = null;
	private AddNodeAction addAction = null;
	private CreateRequiredAction createRequiredAction = null;

	private DesignerProperties properties = null;

	private ExchangerEditor parent = null;
	private ExchangerView view = null;
	private boolean editorHasFocus = false;

//	private static boolean createRequired = false;
//	private boolean changed = false;
	private boolean hasLatest = false;

	/**
	 * Constructs an explorer view with the ExplorerProperties supplied.
	 *
	 * @param root the root node.
	 */
	public Designer( ExchangerEditor parent, DesignerProperties props, ExchangerView _view) {
		super( new BorderLayout());
		
		this.parent = parent;
		this.properties = props;
		this.view = _view;
		
		listeners = new EventListenerList();
		
		treeModel = new DefaultTreeModel( new DefaultMutableTreeNode());

		// Create the tree:
		tree = new DesignerTree(); // treeModel);
//		tree.setBackground( Color.white);
		tree.setBorder( new EmptyBorder( 2, 2, 2, 2));

		tree.setModel( treeModel);

		ToolTipManager.sharedInstance().registerComponent( tree);
		tree.setShowsRootHandles( true);
		tree.putClientProperty( "JTree.lineStyle", "Angled");
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);

		DesignerCellRenderer renderer = new DesignerCellRenderer( this);
		tree.setCellRenderer( renderer);
		tree.setRootVisible( true);
		tree.setExpandsSelectedPaths( true);
		
		createRequiredAction = new CreateRequiredAction( this);
		deleteAction = new DeleteNodeAction( this);
		addAction = new AddNodeAction( parent, this);
		
		AbstractAction compoundAddAction = new AbstractAction() {
			public void actionPerformed( ActionEvent e) {
				if ( addAction.isEnabled()) {
					addAction.actionPerformed( e);
				} else {
					selectText();
				}
			}
		};

		tree.getActionMap().put( "deleteAction", deleteAction);
		tree.getActionMap().put( "addAction", compoundAddAction);

		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "addAction");
		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0, false), "deleteAction");
		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, false), "toggle");
		
		popup = new JPopupMenu();
		popup.add( deleteAction);
		popup.add( addAction);
		popup.addSeparator();
		popup.add( createRequiredAction);
		popup.addSeparator();
		popup.add( parent.getCollapseAllAction());
		popup.add( parent.getExpandAllAction());
		popup.addSeparator();
		popup.add( parent.getUndoAction());
		popup.add( parent.getRedoAction());
		popup.addSeparator();
		popup.add( parent.getSaveAction());
		popup.add( parent.getSaveAsAction());
		popup.addSeparator();
		popup.add( parent.getCloseAction());
		GUIUtilities.alignMenu( popup);
		
		tree.addTreeWillExpandListener( new TreeWillExpandListener() {
			public void treeWillExpand( TreeExpansionEvent e) throws ExpandVetoException {
				DesignerNode node = (DesignerNode) e.getPath().getLastPathComponent();
				
				if ( node instanceof ElementNode) {
					ElementNode elementNode = (ElementNode)node;
					
					if ( !elementNode.virtualNodesVisible() && !elementNode.isVirtual()) {
						elementNode.showVirtualNodes();
						
						for ( int i = 0; i < elementNode.getChildCount(); i++) {
							DesignerNode dn = (DesignerNode)elementNode.getChildAt( i);

							if ( dn instanceof ElementNode) {
								if ( !((ElementNode)dn).virtualNodesVisible() && !((ElementNode)dn).isVirtual()) {
									((ElementNode)dn).showVirtualNodes();
								}
							}
						}

						tree.expandPath( new TreePath( elementNode.getPath()));
					}
				}
			}
			public void treeWillCollapse( TreeExpansionEvent e) throws ExpandVetoException {
			}
		});
		
		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged( TreeSelectionEvent event) {
				selectionChanged();
			}
		});

		tree.addMouseListener ( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				TreePath path = tree.getPathForLocation( e.getX(), e.getY());

				if ( path != null && path.equals( tree.getSelectionPath())) {
					DesignerNode node = (DesignerNode) path.getLastPathComponent();
					
					popup.show( tree, e.getX(), e.getY());
				}
			}
		});
		
		tree.addMouseListener ( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TreePath path = tree.getPathForLocation( e.getX(), e.getY());

				if ( path != null && path.equals( tree.getSelectionPath())) {
					add();

//					fireDoubleClicked( e, (DesignerNode) path.getLastPathComponent());
				}
			}
		});

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
		scrollPane.getViewport().addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e) {
				scrollPane.doLayout();
			}
		});
		
		valueEditor = new ValueEditor( this);
		Keymap keymap = valueEditor.getKeymap();
		keymap.addActionForKeyStroke( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), 
			new AbstractAction() {
				public void actionPerformed( ActionEvent event) {
					Object source = event.getSource();
			
					if ( source instanceof ValueEditor) {
						editorHasFocus = false;
						((ValueEditor)source).getDesigner().setFocus();
					}
				}
			});
		

		JScrollPane scroller = new JScrollPane( valueEditor);
		scroller.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setPreferredSize( new Dimension( 100, 40));
		
		typeLabel = new JLabel( "Type");
		nameLabel = new JLabel( "Name");

		JPanel topPanel = new JPanel( new BorderLayout());
		topPanel.add( nameLabel, BorderLayout.WEST);
		topPanel.add( typeLabel, BorderLayout.EAST);
		topPanel.setPreferredSize( new Dimension( 100, 20));
		topPanel.setBorder( new EmptyBorder( 0, 5, 0, 5));
		
		MouseListener listener = new MouseAdapter() {
			public void mouseClicked( MouseEvent e) {
				view.setFocussed();
			}
		};

		topPanel.addMouseListener( listener);
		typeLabel.addMouseListener( listener);
		nameLabel.addMouseListener( listener);

		updatePanel = new JPanel( new BorderLayout());
		updatePanel.add( topPanel, BorderLayout.NORTH);
		updatePanel.add( scroller, BorderLayout.CENTER);
		updatePanel.addMouseListener( listener);
		
		split = new JSplitPane( JSplitPane.VERTICAL_SPLIT, updatePanel, scrollPane);

		if ( split.getDividerSize() > 6) {
			split.setDividerSize( 6);
		}

		split.setDividerLocation( properties.getDividerLocation());
		split.addPropertyChangeListener( JSplitPane.DIVIDER_LOCATION_PROPERTY, new PropertyChangeListener() {
			public void propertyChange( PropertyChangeEvent e) {
				properties.setDividerLocation( split.getDividerLocation());
			}
		});
		split.setBorder( null);
		split.addMouseListener( listener);
		Object ui = split.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}
		
		tree.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e) {
				editorHasFocus = false;
				view.setFocussed();
			}

			public void focusLost( FocusEvent e) {}
		});

		valueEditor.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent e) {
				editorHasFocus = true;
				view.setFocussed();
			}

			public void focusLost( FocusEvent e) {}
		});

		add( split, BorderLayout.CENTER);
		
		updatePreferences();
	}
	
	public boolean isShowAttributeValues() {
		return properties.isShowAttributeValues();
	}

	public boolean isShowElementValues() {
		return properties.isShowElementValues();
	}

	public void selectionChanged() {
//		System.out.println( "Designer.selectionChanged()");
		DesignerNode node = getSelectedNode();

		if ( selectedNode != null) {

			if ( selectedNode instanceof ElementNode) {
				SchemaElement type = ((ElementNode)selectedNode).getType();
				
				if ( type != null && 
					( type.getSchemaType() instanceof SimpleSchemaType || 
					  ( type.getSchemaType() instanceof ComplexSchemaType && 
					    ((ComplexSchemaType)type.getSchemaType()).isSimpleContent() ))) {
					String newText = valueEditor.getText();
					String oldText = selectedNode.getValue();
					
					if ( !newText.equals( oldText) && !(isEmptyString( newText) && isEmptyString( oldText))) {
						UndoableElementValueChange valueChange = new UndoableElementValueChange( (ElementNode)selectedNode);
						
						selectedNode.setValue( newText);
						treeModel.nodeChanged( selectedNode);
						
						getUndoSupport().postEdit( valueChange);
					}
				}
			} else if ( selectedNode instanceof AttributeNode) {
				String newText = valueEditor.getText();
				String oldText = selectedNode.getValue();
				
				if ( !newText.equals( oldText) && !(isEmptyString( newText) && isEmptyString( oldText))) {
					UndoableAttributeValueChange valueChange = new UndoableAttributeValueChange( (AttributeNode)selectedNode);
					
					selectedNode.setValue( newText);
					treeModel.nodeChanged( selectedNode);
					
					getUndoSupport().postEdit( valueChange);
				}
			}
		} 

		if ( node instanceof ElementNode) {
			ElementNode elementNode = (ElementNode)node;
			SchemaElement schemaElement = elementNode.getType();
			
			if ( schemaElement != null) {
				SchemaType type = schemaElement.getSchemaType();
				
				if ( !elementNode.isVirtual()) {
					if ( type instanceof ComplexSchemaType) {
						if ( ((ComplexSchemaType)type).isSimpleContent()) {
							// set the editor values...
//							System.out.println( "Element isSimpleContent()");
							valueEditor.setEditable( true);
						} else {
//							System.out.println( "Element !isSimpleContent()");
							valueEditor.setEditable( false);
						}
					} else {
//						System.out.println( "Element isSimpleSchemaType()");
						valueEditor.setEditable( true);
					}
				} else {
//					System.out.println( "Element isVirtual()");
					valueEditor.setEditable( false);
				}

				typeLabel.setText( type.getName());
				nameLabel.setIcon( ((ElementNode)node).getIcon());

				if ( !elementNode.isVirtual()) {
					parent.getNavigator().setSelectedElement( elementNode.getElement());
					parent.getXPathEditor().setXPath( elementNode.getElement());
					
					int y = tree.getPathBounds( new TreePath( node.getPath())).y;
					y = y - scrollPane.getViewport().getViewRect().y;
					y = y + updatePanel.getSize().height+split.getDividerSize();

					parent.synchronise( view, elementNode.getElement(), false, y);

					deleteAction.setEnabled( true);
					addAction.setEnabled( false);
					createRequiredAction.setEnabled( true);
				} else {
					deleteAction.setEnabled( false);
					addAction.setEnabled( true);
					createRequiredAction.setEnabled( false);
				}

				if ( !elementNode.isVirtual() && !elementNode.virtualNodesVisible()) {
					elementNode.showVirtualNodes();
				}
			} else { // this element does not have a type, it must be a foreign element
//				System.out.println( "Element isForeign()");
				valueEditor.setEditable( false);
				typeLabel.setText( "Unknown");
				nameLabel.setIcon( ((ElementNode)node).getIcon());

				parent.getNavigator().setSelectedElement( elementNode.getElement());
				parent.getXPathEditor().setXPath( elementNode.getElement());

				int y = tree.getPathBounds( new TreePath( node.getPath())).y;
				y = y - scrollPane.getViewport().getViewRect().y;
				y = y + updatePanel.getSize().height+split.getDividerSize();

				parent.synchronise( view, elementNode.getElement(), false, y);

				deleteAction.setEnabled( false);
				addAction.setEnabled( false);
				createRequiredAction.setEnabled( false);
			}
			
		} else if ( node instanceof AttributeNode) {
			AttributeNode a = (AttributeNode)node;
//			System.out.println( "isAttribute()");
			valueEditor.setEditable( !a.isVirtual());

			if ( !a.isVirtual()) {
				parent.getNavigator().setSelectedElement( a.getParentElementNode().getElement());
				parent.getXPathEditor().setXPath( a.getAttribute());

				int y = tree.getPathBounds( new TreePath( node.getPath())).y;
				y = y - scrollPane.getViewport().getViewRect().y;
				y = y + updatePanel.getSize().height+split.getDividerSize();

				parent.synchronise( view, a.getAttribute(), false, y);

				deleteAction.setEnabled( true);
				addAction.setEnabled( false);
			} else {
				deleteAction.setEnabled( false);
				addAction.setEnabled( true);
			}

			createRequiredAction.setEnabled( false);

			if ( a.getType() != null) {
				typeLabel.setText( a.getType().getType().getName());
			} else { // foreign attribute
				typeLabel.setText( "");

				deleteAction.setEnabled( false);
				addAction.setEnabled( false);
			}
			
			nameLabel.setIcon( a.getIcon());
		}

		if ( node != null) {
			valueEditor.setText( node.getValue());
			valueEditor.setCaretPosition( 0);
			nameLabel.setText( node.getName());
		} else {
//			System.out.println( "Node is Null");
			valueEditor.setText( null);
			nameLabel.setText( null);
			nameLabel.setIcon( null);
			typeLabel.setText( null);
			valueEditor.setEditable( false);
		}

		selectedNode = node;

		if ( selectedNode != null) {
			TreePath path = new TreePath( selectedNode.getPath());
			tree.scrollPathToVisible( path);
		}
		
		updateHelper();

		fireSelectionChanged( node);
	}
	
	public void updateHelper() {
		if ( selectedNode instanceof ElementNode) {
			ElementNode elementNode = (ElementNode)selectedNode;
			SchemaElement schemaElement = elementNode.getType();
			
			if ( schemaElement != null) {
				if ( !elementNode.isVirtual()) {
					parent.getHelper().setElement( elementNode);
				} else {
					parent.getHelper().setElement( elementNode.getParentElementNode());
				}
			} else { // foreign
				parent.getHelper().setElement( elementNode.getParentElementNode());
			}
		} else if ( selectedNode instanceof AttributeNode) {
			AttributeNode attributeNode = (AttributeNode)selectedNode;
			parent.getHelper().setElement( attributeNode.getParentElementNode());
		}
	}
	
	/**
	 * Updates the navigator if it is visible...
	 */
	public void updateNavigator() {
	 	if ( parent.getNavigator().isVisible()) {
	 		parent.getNavigator().updateOutline();
	 	}
	}
	
	/**
	 * Gets the designer icon
	 *
	 * @return the icon for the designer.
	 */
	public ImageIcon getIcon() {
		return ICON;
	}

	/**
	 * Gets the trees root node.
	 *
	 * @return the trees root node.
	 */
	public DesignerNode getRoot() {
		return root;
	}

	/**
	 * Wether auto create is required.
	 *
	 * @return true when auto create is required.
	 */
	public boolean isAutoCreateRequired() {
		return properties.isAutoCreateRequired();
	}

	/**
	 * Gets the trees root node.
	 *
	 * @return the trees root node.
	 */
//	public boolean isChanged() {
//		return changed;
//	}

	/**
	 * Collapses all the nodes in the tree.
	 */
	public void collapseAll() {
		tree.collapseAll();
		tree.expand(1);
	}

	public void expandAll() {
		tree.expandAll();
	}

	public void expandNode( DesignerNode node) {
		tree.expandNode( node);
	}

	public void collapseNode( DesignerNode node) {
		tree.collapseNode( node);
	}

	/**
	 * Adds the current selected node.
	 */
	public void add( DesignerNode node) {
		setSelectedNode( node);

		if ( node instanceof ElementNode) {
			ElementNode enode = (ElementNode)node;

			if ( enode.isVirtual()) {
				addAction.execute();
			}
		} else if ( node instanceof AttributeNode) {
			AttributeNode anode = (AttributeNode)node;

			if ( anode.isVirtual()) {
				addAction.execute();
			}
		}
	}

	/**
	 * Adds the current selected node.
	 */
	public void add() {
		addAction.execute();
	}

	/**
	 * Creates the required nodes in the current selected element.
	 */
	public void createRequired() {
		createRequiredAction.execute();
	}

	/**
	 * Deletes the current selected node.
	 */
	public void delete() {
		deleteAction.execute();
	}

	/**
	 * Gets the trees root node.
	 *
	 * @return the trees root node.
	 */
	public void setRoot( DesignerNode node) {
		root = node;

		treeModel.setRoot( root);
		treeModel.reload();

		if ( node instanceof ElementNode) {
			ElementNode elementNode = (ElementNode)node;
			
			if ( !elementNode.virtualNodesVisible() && !elementNode.isVirtual()) {
				elementNode.showVirtualNodes();
			}
		}
	}

	/**
	 * Gets the tree component.
	 *
	 * @return the tree component.
	 */
	public QTree getTree() {
		return tree;
	}

	/**
	 * Returns the currently selected node, null if nothing 
	 * has been selected.
	 *
	 * @return a schema node.
	 */
	public DesignerNode getSelectedNode() {
		DesignerNode node = null;
		TreePath path = tree.getSelectionPath();
		
		if ( path != null) {
			node = (DesignerNode) path.getLastPathComponent();
		}	
	
		return node;
	}

	/**
	 * Returns the currently selected element, or parent-element 
	 * if currently an attribute has been selected.
	 *
	 * @return the selected element.
	 */
	public XElement getSelectedElement() {
		XElement result = null;
		DesignerNode node = getSelectedNode();
		
		if ( node != null) {
			if ( node instanceof ElementNode) {
				result = ((ElementNode)node).getElement();
			} else if ( node instanceof AttributeNode) {
				result = ((AttributeNode)node).getParentElementNode().getElement();
			}
		}

		return result;
	}

	public void updateSelection() {
	}

	/**
	 * Sets the selected node.
	 *
	 * @param node the explorer node.
	 */
	public void setSelectedNode( DesignerNode node) {
//		System.out.println("Designer.setSelectedNode( "+node+")");
		if ( node != null) {
			TreePath path = new TreePath( node.getPath());
			tree.scrollPathToVisible( path);
			tree.setSelectionPath( path);
//			selectionChanged();
		} else {
			tree.clearSelection();
		}
	}

	/**
	 * Sets the selected element node.
	 *
	 * @param element the element for the node.
	 */
	public void setSelectedNode( XElement element, int y) {
		if ( element != null) {
			setSelectedNode( getNode( element));

			y = y - (updatePanel.getSize().height+split.getDividerSize());

			if ( y >= 0 && tree.getSelectionPath() != null) {
				Rectangle r = tree.getVisibleRect();
				Rectangle sr = tree.getPathBounds( tree.getSelectionPath());
				sr.height = r.height;
				sr.y = sr.y - y;
	
				if ( r.height > y && sr.y >= 0) {
					tree.scrollRectToVisible( sr);
				}
			}
		} else {
			tree.clearSelection();
		}
	}

	/**
	 * Sets the selected attribute node.
	 *
	 * @param attribute the attribute for the node.
	 */
	public void setSelectedNode( XAttribute attribute, int y) {
		setSelectedNode( getNode( attribute));
		
		y = y - (updatePanel.getSize().height+split.getDividerSize());

		if ( y >= 0 && tree.getSelectionPath() != null) {
			Rectangle r = tree.getVisibleRect();
			Rectangle sr = tree.getPathBounds( tree.getSelectionPath());
			sr.height = r.height;
			sr.y = sr.y - y;

			if ( r.height > y && sr.y >= 0) {
				tree.scrollRectToVisible( sr);
			}
		}
	}

	/**
	 * Returns a node for the XElement supplied.
	 *
	 * @param element the element to get the node for.
	 *
	 * @return the element node.
	 */
	public ElementNode getNode( XElement element) {
		ElementNode node = null;
		
		if ( root != null) {
			node = getNode( (ElementNode)root, element);
		} 
		
		return node;
	}
	
	/**
	 * Selects the text area.
	 */
	public void selectText() {
		if ( valueEditor.isEditable()) {
			valueEditor.requestFocusInWindow();
		}
	}

	/**
	 * Returns a node for the XAttribute supplied.
	 *
	 * @param attribute the attribute to get the node for.
	 *
	 * @return the attribute node.
	 */
	public AttributeNode getNode( XAttribute attribute) {
		AttributeNode node = null;
		
		if ( root != null) {
			node = getNode( (ElementNode)root, attribute);
		} 
		
		return node;
	}

	public boolean hasLatestInformation() {
//		System.out.println( "Designer.hasLatestInformation()");
		return hasLatest;
	}

// Implementation of the XDocumentListener interface...	
	public void documentUpdated( ExchangerDocumentEvent event) {
//		System.out.println( "Designer.documentUpdated( "+event+")");
		if ( event.getType() == ExchangerDocumentEvent.CONTENT_UPDATED || event.getType() == ExchangerDocumentEvent.MODEL_UPDATED) {
			hasLatest = false;
		}
	}
	
	/**
	 * Sets the document for the designer.
	 *
	 * @param document the document.
	 */
	public void setDocument( ExchangerDocument document) {
//		System.out.println( "Designer.setDocument( "+document+")");
		boolean firstTime = false;
		
		if ( this.document != null) {
			this.document.removeListener( this);
		} else {
			firstTime = true;
		}
		
		this.document = document;

		if ( document != null) {
			document.addListener( this);
		}
		
		if ( !firstTime) {
			hasLatest = true;
		}
	}
	
	public void updateTree() {
		setTree();
	}
	
	/**
	 * Sets the schema for the designer.
	 *
	 * @param schema the schema.
	 */
	public void setSchema( XMLSchema schema) {
//		System.out.println( "Designer.setSchema( "+schema+")");
		this.schema = schema;
		hasLatest = false;
	}

	/**
	 * Gets the undo support object, to be used by an undo manager.
	 *
	 * @return undoable edit support object.
	 */
	public UndoableEditSupport getUndoSupport() {
		if ( undoSupport == null) {
			undoSupport = new UndoableEditSupport();
			undoSupport.addUndoableEditListener( this);
		}
		
		return undoSupport;
	}

	public void setFocus() {
		if ( editorHasFocus && !valueEditor.hasFocus()) {
			valueEditor.requestFocusInWindow();
		} else if ( !tree.hasFocus()) {
			tree.requestFocusInWindow();
		}
	}

	public void setProperties() {
		//
	}

	/**
	 * Update the preferences.
	 */
	 public void updatePreferences() {
	 	Font font = TextPreferences.getBaseFont();
	 	((DesignerCellRenderer)tree.getCellRenderer()).updatePreferences();
	 	((DesignerCellRenderer)tree.getCellRenderer()).setFont( font);
	//		schemaDialog = null;
	 	nameLabel.setFont( font.deriveFont( Font.BOLD));;
	 	typeLabel.setFont( font.deriveFont( Font.BOLD));;
	 	valueEditor.setFont( font);;
		
		nameLabel.updateUI();
		typeLabel.updateUI();
		valueEditor.updateUI();

	 	tree.updateUI();
	 }

	/** 
	 * Adds an Explorer listener to the list of listeners.
	 *
	 * @param the explorer listener.
	 */
	public void addDesignerListener( DesignerListener listener) {
		listeners.add( (Class)listener.getClass(), listener);
	}

	/** 
	 * Removes an Explorer listener from the list of listeners.
	 *
	 * @param the explorer listener.
	 */
	public void removeDesignerListener( DesignerListener listener) {
		if ( listeners != null) {
			listeners.remove( (Class)listener.getClass(), listener);
		}
	}

	public void undoableEditHappened( UndoableEditEvent e) {
		ChangeManager manager = parent.getChangeManager();

		if ( manager != null) {
			manager.addEdit( e.getEdit());
		}
	}
	
	private ElementNode getNode( ElementNode node, XElement element) {
		
		if ( element.equals( node.getElement())) {
			return node;
		} else {
			Enumeration e = node.children();

			while ( e.hasMoreElements()) {
				DesignerNode n = (DesignerNode)e.nextElement();
				
				if ( n instanceof ElementNode) {
					ElementNode childNode = getNode( (ElementNode)n, element);

					if ( childNode != null) {
						return childNode;
					}
				}
			}
		}
		
		return null;
	}

	private AttributeNode getNode( ElementNode node, XAttribute attribute) {
		
		Enumeration e = node.children();
		
		while ( e.hasMoreElements()) {
			DesignerNode n = (DesignerNode)e.nextElement();
			
			if ( n instanceof AttributeNode) {
				if ( ((AttributeNode)n).getAttribute() == attribute) {
					return ((AttributeNode)n);
				}
			}
		}

		e = node.children();

		while ( e.hasMoreElements()) {
			DesignerNode n = (DesignerNode)e.nextElement();
			
			if ( n instanceof ElementNode) {
				AttributeNode childNode = getNode( (ElementNode)n, attribute);

				if ( childNode != null) {
					return childNode;
				}
			}
		}
		
		return null;
	}

	private void expandNode( TreeNode node) {
		tree.expandPath( new TreePath( treeModel.getPathToRoot( node)));

		for ( int i = 0; i < node.getChildCount(); i++) {
			expandNode( node.getChildAt( i));
		}
	}

	private void expandNode( TreeNode node, int level) {
		if ( level > 0) {
			tree.expandPath( new TreePath( treeModel.getPathToRoot( node)));

			for ( int i = 0; i < node.getChildCount(); i++) {
				expandNode( node.getChildAt( i), level - 1);
			}
		}
	}

	private void setTree() {
//		System.out.println( "Designer.setTree()");

		if ( schema != null && document != null) {
			Vector elements = schema.getGlobalElements();
			XElement root = document.getRoot();
			SchemaElement virtual = null;
			
			for ( int i = 0; i < elements.size() && virtual == null; i++) {
				SchemaElement e = (SchemaElement)elements.elementAt(i);

				if ( e.getName().equals( root.getName())) {
					virtual = e;
				}
			}
			
			setRoot( new ElementNode( treeModel, null, virtual, root));

			expandNode( this.root, 3);
		}
	}

	/** 
	 * Notifies the listeners about a popup trigger on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
	protected void fireSelectionChanged( DesignerNode node) {
		// Guaranteed to return a non-null array
		Object[] list = listeners.getListenerList();
		
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for ( int i = list.length-2; i >= 0; i -= 2) {
			((DesignerListener)list[i+1]).selectionChanged( node);
		}
	}

	protected boolean isEmptyString( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}

	/** 
	 * Notifies the listeners about a popup trigger on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
//	protected void firePopupTriggered( MouseEvent event, DesignerNode node) {
//		// Guaranteed to return a non-null array
//		Object[] list = listeners.getListenerList();
//		
//		// Process the listeners last to first, notifying
//		// those that are interested in this event
//		for ( int i = list.length-2; i >= 0; i -= 2) {
//		    if ( list[i] == DesignerListener.class) {
//				((DesignerListener)list[i+1]).popupTriggered( event, node);
//		    }
//		}
//	}

	/** 
	 * Notifies the listeners about a double click on a node.
	 *
	 * @param the mouse event.
	 * @param the node.
	 */
//	protected void fireDoubleClicked( MouseEvent event, DesignerNode node) {
//		// Guaranteed to return a non-null array
//		Object[] list = listeners.getListenerList();
//		
//		// Process the listeners last to first, notifying
//		// those that are interested in this event
//		for ( int i = list.length-2; i >= 0; i -= 2) {
//		    if ( list[i] == DesignerListener.class) {
//				((DesignerListener)list[i+1]).doubleClicked( event, node);
//		    }
//		}
//	}
	
	private class UndoableAttributeValueChange extends UndoableDesignerEdit {
		AttributeNode node = null;
		String before = null;
		String after = null;
		
		public UndoableAttributeValueChange( AttributeNode node) {
			this.node = node;
			before = node.getValue();
		}
		
		public void undo() {
			super.undo();

			after = node.getValue();
			setValue( before);
			treeModel.nodeChanged( node);
//			setSelectedNode( node);
		}

		public void redo() {
			super.redo();
			
			setValue( after);
			treeModel.nodeChanged( node);
//			setSelectedNode( node);
		}
		
		private void setValue( String value) {
			DesignerNode n = getSelectedNode();
			
			node.setValue( value);
			treeModel.nodeChanged( node);

			if ( n == node) {
				valueEditor.setText( value);
			} else {
				setSelectedNode( node);
			} 
		}
		
		public String getUndoPresentationName() {
			return "Undo Attribute Value Change";
		}

		public String getRedoPresentationName() {
			return "Redo Attribute Value Change";
		}
	}

	private class UndoableElementValueChange extends UndoableDesignerEdit {
		ElementNode node = null;
		String before = null;
		String after = null;
		
		public UndoableElementValueChange( ElementNode node) {
			this.node = node;
			before = node.getValue();
		}
		
		public void undo() {
			super.undo();

			after = node.getValue();
			setValue( before);
			treeModel.nodeChanged( node);
//			setSelectedNode( node);
		}

		public void redo() {
			super.redo();
			
			setValue( after);
			treeModel.nodeChanged( node);
//			setSelectedNode( node);
		}
		
		private void setValue( String value) {
			DesignerNode n = getSelectedNode();
			
			node.setValue( value);
			treeModel.nodeChanged( node);

			if ( n == node) {
				valueEditor.setText( value);
			} else {
				setSelectedNode( node);
			}
		}

		public String getUndoPresentationName() {
			return "Undo Element Value Change";
		}

		public String getRedoPresentationName() {
			return "Redo Element Value Change";
		}
	}
	
	public void cleanup() {
		tree.setModel( new DefaultTreeModel( new DefaultMutableTreeNode()));
		
		TreeSelectionListener[] selectionListeners = tree.getTreeSelectionListeners();
		
		for ( int i = 0; i < selectionListeners.length; i++) {
			tree.removeTreeSelectionListener( selectionListeners[i]);
		}

		TreeWillExpandListener[] expansionListeners = tree.getTreeWillExpandListeners();
		
		for ( int i = 0; i < expansionListeners.length; i++) {
			tree.removeTreeWillExpandListener( expansionListeners[i]);
		}

		MouseListener[] mouseListeners = getMouseListeners();

		for ( int i = 0; i < mouseListeners.length; i++) {
			removeMouseListener( mouseListeners[i]);
		}
		
		deleteAction.cleanup();
		addAction.cleanup();
		createRequiredAction.cleanup();
		
		popup.removeAll();

		removeAll();
	
		finalize();
	}
	
	protected void finalize() {
		document = null;
		schema = null;
		
		listeners = null;
		undoSupport = null;

		popup = null;

		tree = null;
		scrollPane = null;
		treeModel = null;

		root = null;
		selectedNode = null;
	
		split = null;

		nameLabel = null;
		typeLabel = null;
		valueEditor = null;
	
		deleteAction = null;
		addAction = null;
		createRequiredAction = null;

		properties = null;

		parent = null;
	}
	
	private class ValueEditor extends JTextArea {
		private Designer designer = null;
		
		public ValueEditor( Designer designer) {
			this.designer = designer;
			
			setLineWrap( true);
			setWrapStyleWord( true);
		}
		
		public Designer getDesigner() {
			return designer;
		}
	}
} 
