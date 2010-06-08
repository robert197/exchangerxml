/*
 * $Id: XDiffTreeDialog.java,v 1.13 2004/10/28 13:37:09 edankert Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.xdiff;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.bounce.FormLayout;
import org.bounce.event.PopupListener;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.dom4j.ProcessingInstruction;
import org.dom4j.Text;
import org.dom4j.tree.DefaultText;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerOutputFormat;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.TextPreferences;




/**
 * The XDiffTree dialog.
 *
 * @version	$Revision: 1.13 $, $Date: 2004/10/28 13:37:09 $
 * @author Dogs bay
 */
public class XDiffTreeDialog extends JDialog {
	
	private boolean cancelled	= false;
	private JFrame parent		= null;
	private JButton closeButton	= null;
	private JPanel treePanel	= null;
	private ExchangerDocument document	= null;
	private XmlTree tree = null;
	private XmlTree mergeTree = null;
	private Statusbar statusbar = null;
	private JTextField xpathField = null;
	private ConfigurationProperties properties = null;
	private JToolBar diffToolbar	= null;
	private JButton buttonNext = null;
	private JTextField baseField = null;
	private JTextField modField = null;
	private JSplitPane splitPane = null;
	private String baseFileURL = null;
	private ExchangerDocument mergeDocument = null;
	private ExchangerDocument diffDocument = null;
	private ExchangerEditor editor = null;
	private JPanel mergeTreePane = null;
	private JPanel diffTreePane = null;
	private JPopupMenu resolveChildrenPopup = null;
	private JPopupMenu insertElementPopup = null;
	private JPopupMenu deleteElementPopup = null;
	private JPopupMenu generalPopup = null;
	private JPopupMenu generalAndChildrenPopup = null;
	private JPanel main = null;
	private JPanel diffTreeLabelPanel = null;
	private JPanel mergeTreeLabelPanel = null;
	
	private LineBorder lineBorder = new LineBorder(UIManager.getColor( "TabbedPane.focus"),1);
	private EmptyBorder emptyBorder = new EmptyBorder(1,1,1,1);
	
	
	// 0 for no, 1 for yes
	private int calledFromOtherTree = 0;
	
	// 0 for left, 1 for right
	private int activeSide = 0;
	
	private ExpandAllAction expandAll = null;
	private CollapseAllAction collapseAll = null;
	private GotoNextDiffAction gotoNextDiff = null;
	private GotoPreviousDiffAction gotoPreviousDiff = null;
	private GotoLastDiffAction gotoLastDiff = null; 
	private GotoFirstDiffAction gotoFirstDiff = null; 
	private ShowMergeTreeAction showMergeTree = null;
	private EditMergeDocumentAction editMergeDocument = null;

	private static final String DIFF_RESULT_PATH = "/diff_result";
	private final static Color COLOR_GREEN = new Color(0,128,64);
	private final static Color COLOR_MERGED = Color.MAGENTA;
	
	private static final Border BEVEL_BORDER = new CompoundBorder( 
			new EmptyBorder( 0, 2, 0, 0),
			new CompoundBorder(  
				new BevelBorder( BevelBorder.LOWERED, Color.white, UIManager.getColor( "control"), UIManager.getColor( "control"), UIManager.getColor( "controlDkShadow")), 
				new EmptyBorder( 0, 2, 0, 0)));
	
	/**
	 * The XDiffTree dialog.
	 *
	 * @param parent the parent frame.
	 */
	public XDiffTreeDialog( JFrame parent,ConfigurationProperties props) {
		super( parent, true);
		
		this.parent = parent;
		this.properties = props;
		
		setModal(false);
		setResizable(true);
		setTitle( "XML Diff and Merge");
		
		main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 0, 0, 0, 0));
		

		getRootPane().setDefaultButton( closeButton);

		JPanel form = new JPanel(new BorderLayout());

		// fill the panel...
		form.add( getTreePanel(), BorderLayout.CENTER);
		
		main.add( form, BorderLayout.CENTER);
		
		statusbar = new Statusbar(this,false);
		statusbar.setBorder( new EmptyBorder( 1, 0, 0, 0));
		main.add(statusbar, BorderLayout.SOUTH);
		
		diffToolbar = createDiffToolbar();
		main.add( diffToolbar, BorderLayout.NORTH);

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				//setVisible(false);
				hide();
			}
		});

		setContentPane( main);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	/**
	 * Creates the tree panel
	 *
	 * @return the tree panel
	 */
	private JPanel getTreePanel() {
		if ( treePanel == null) {
			
			treePanel = new JPanel( new BorderLayout());
			treePanel.setBorder( new MatteBorder( 1, 0, 0, 0, UIManager.getColor("controlDkShadow")));
//			treePanel.setBorder( new EmptyBorder( 0, 0, 0, 0));
		
			tree = new XmlTree();
			tree.setBackground(Color.WHITE);
			
			tree.addTreeSelectionListener( new TreeSelectionListener() {
				public void valueChanged( TreeSelectionEvent e) {
					
					if (calledFromOtherTree == 1)
					{
						return;
					}
					
					TreePath path = tree.getSelectionPath();
					if ( path != null) {
						XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
						XElement element = node.getElement();
						
						if ( element != null) 
						{
							setXPath( element);
							
							if (mergeDocument != null)
							{
								
								if (node.insertElement || node.deleteElement)
								{
									// maybe different then xpath
									selectInsertOrDeleteElement(node,element);
									return;
								}
								
								String xpath = element.getUniquePath();
								
								Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
								Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
								if (xpathResults.size() > 0)
								{
									XElement xpathResult = (XElement)xpathResults.get(0);
									calledFromOtherTree = 1;
									mergeTree.clearSelection();
									mergeTree.setSelectedNode(xpathResult,node.isEndTag());
									calledFromOtherTree = 0;
								}
								else
								{
									// node doesn't exist, just blank merge tree selection
									calledFromOtherTree = 1;
									mergeTree.clearSelection();
									calledFromOtherTree = 0;	
								}
							}
						}
						else
						{						
							XmlTextNode textNode = (XmlTextNode) path.getLastPathComponent();
							if (textNode != null)
							{
								Text text = textNode.getTextNode();
								if (text == null)
								{
									text = textNode.getDummyNode();
								}
								
								if (text != null)
								{
									String uniquePath = text.getUniquePath();
									
									int textNodePos = findTextPosition(text,(XElement)text.getParent());
									
									Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
									
									StringBuffer xpath = new StringBuffer(uniquePath);
									xpath.append("[");
									xpath.append(textNodePos);
									xpath.append("]");
									
									setXPathString( xpath.toString());
									
									Vector xpathResults = mergeDocument.search(xpath.toString(),namespaceMappings);
									if (xpathResults.size() > 0)
									{
										Text xpathResult = (Text)xpathResults.get(0);
										calledFromOtherTree = 1;
										mergeTree.clearSelection();
										mergeTree.setSelectedNode(xpathResult);
										calledFromOtherTree = 0;
									}
									else
									{
										// node doesn't exist, just blank merge tree selection
										calledFromOtherTree = 1;
										mergeTree.clearSelection();
										calledFromOtherTree = 0;	
									}
								}
								else
								{
								    // node doesn't exist, just blank merge tree selection
									calledFromOtherTree = 1;
									mergeTree.clearSelection();
									calledFromOtherTree = 0;	
								}
							}
						}
					}
				}
			});
			
			tree.addMouseListener( new PopupListener() {
				public void popupTriggered( MouseEvent e) {
					
					if (mergeDocument == null)
					{
						return;
					}
					
					TreePath path = tree.getPathForLocation( e.getX(), e.getY());

					if ( path != null && path.equals( tree.getSelectionPath())) {
						XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
						
						firePopupTriggered( e, node);
					}
				}
			});
			
			diffTreePane = new JPanel(new BorderLayout());
			JScrollPane diffTreeScroll = new JScrollPane(tree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
													JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			diffTreePane.add(diffTreeScroll,BorderLayout.CENTER);
			
			
			tree.addMouseListener( new MouseListener(){
				
				public void mousePressed(MouseEvent e) {
				       // do nothing
				    }

				    public void mouseReleased(MouseEvent e) {
				    	// do nothing 				                    
				    }

				    public void mouseEntered(MouseEvent e) {
				    	// do nothing
				    }

				    public void mouseExited(MouseEvent e) {
				    	// do nothing
				    }

				    public void mouseClicked(MouseEvent e) {
				    	if (mergeDocument != null)
				    	{
				    		activeSide = 0;
				    		mergeTreePane.setBorder(emptyBorder);
				    		diffTreePane.setBorder(lineBorder);
				    	}
				    }
			});
			
			
			mergeTree = new XmlTree();
			mergeTree.setBackground(Color.WHITE);
			
			mergeTree.addTreeSelectionListener( new TreeSelectionListener() {
				public void valueChanged( TreeSelectionEvent e) {
					
					if (calledFromOtherTree == 1)
					{
						return;
					}
					
					TreePath path = mergeTree.getSelectionPath();
					if ( path != null) {
						XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
						XElement element = node.getElement();
						
						if ( element != null) 
						{
							setXPath( element);
							
							if (diffDocument != null)
							{
								if (node.getMergeAdded() != null)
								{
									XElement diffEle = node.getMergeAdded();
									calledFromOtherTree = 1;
									tree.clearSelection();
									tree.setSelectedNode(diffEle,false);
									calledFromOtherTree = 0;	
									return;
								}
								else if (node.getMergeDelete() != null)
								{
									XElement diffEle = node.getMergeDelete();
									calledFromOtherTree = 1;
									tree.clearSelection();
									tree.setSelectedNode(diffEle,false);
									calledFromOtherTree = 0;	
									return;
								}
									
								String xpath = element.getUniquePath();
								
								Vector namespaceMappings = mergeDocument.getDeclaredNamespaces();
								Vector xpathResults = diffDocument.search(xpath,namespaceMappings);
								if (xpathResults.size() > 0)
								{
									XElement xpathResult = (XElement)xpathResults.get(0);
									calledFromOtherTree = 1;
									tree.clearSelection();
									tree.setSelectedNode(xpathResult,node.isEndTag());
									calledFromOtherTree = 0;
								}
								else
								{
									// node doesn't exist, should never happen
									calledFromOtherTree = 1;
									tree.clearSelection();
									calledFromOtherTree = 0;
								}
							}
						}
						else
						{						
							XmlTextNode textNode = (XmlTextNode) path.getLastPathComponent();
							if (textNode != null)
							{
								Text text = textNode.getTextNode();
								
								if (text == null)
								{
									text = textNode.getDummyNode();
								}
								
								if (text != null)
								{
									String uniquePath = text.getUniquePath();
									
									
									int textNodePos = findTextPosition(text,(XElement)text.getParent()); 
									
									Vector namespaceMappings = mergeDocument.getDeclaredNamespaces();
									
									StringBuffer xpath = new StringBuffer(uniquePath);
									xpath.append("[");
									xpath.append(textNodePos);
									xpath.append("]");
									
									setXPathString( xpath.toString());

									Vector xpathResults = diffDocument.search(xpath.toString(),namespaceMappings);
									if (xpathResults.size() > 0)
									{
										Text xpathResult = (Text)xpathResults.get(0);
										calledFromOtherTree = 1;
										tree.clearSelection();
										tree.setSelectedNode(xpathResult);
										calledFromOtherTree = 0;
									}
									else
									{
										// node doesn't exist, just blank merge tree selection
										calledFromOtherTree = 1;
										tree.clearSelection();
										calledFromOtherTree = 0;	
									}
								}
								else
								{
								    // node doesn't exist, just blank merge tree selection
									calledFromOtherTree = 1;
									tree.clearSelection();
									calledFromOtherTree = 0;	
								}
							}
						}
					}
				}
			});
			
			mergeTree.addMouseListener( new MouseListener(){
				
				public void mousePressed(MouseEvent e) {
				       // do nothing
				    }

				    public void mouseReleased(MouseEvent e) {
				    	// do nothing 				                    
				    }

				    public void mouseEntered(MouseEvent e) {
				    	// do nothing
				    }

				    public void mouseExited(MouseEvent e) {
				    	// do nothing
				    }

				    public void mouseClicked(MouseEvent e) {
				    	if (splitPane != null)
				    	{
				    		activeSide = 1;
				    		diffTreePane.setBorder(emptyBorder);
				    		mergeTreePane.setBorder(lineBorder);
				    	}
				    }
			});
			
			// add two file text fields
			JPanel filePanel = new JPanel(  new FormLayout( 5, 2));
			filePanel.setBorder( new EmptyBorder( 2, 2, 2, 2));
			
			JLabel baseLabel = new JLabel("Base:");  
			baseField = new JTextField();
			baseField.setOpaque( false);
			baseField.setBorder(null);
			baseField.setEditable(false);
			
			filePanel.add(baseLabel, FormLayout.LEFT);
			filePanel.add(baseField, FormLayout.RIGHT_FILL);
			
			
			JLabel modLabel = new JLabel("Modified:");  
			modField = new JTextField();
			modField.setBackground( getBackground());
			modField.setOpaque( false);
			modField.setBorder(null);
			modField.setEditable(false);
			
			filePanel.add(modLabel, FormLayout.LEFT);
			filePanel.add(modField, FormLayout.RIGHT_FILL);
			
			treePanel.add(filePanel,BorderLayout.NORTH);
			
			treePanel.add(diffTreePane,BorderLayout.CENTER);
		}

		return treePanel;
	}
	
	private int findTextPosition(Text text,XElement parent)
	{
		int position = 0;
		Vector children = parent.getChildren();
		
		for (int i=0;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Text)
			{
				if (text == node)
				{
					position++;
					break;
				}
				else
				{
					if (!isWhiteSpace(node))
					{
						position++;
					}
				}
			}
		}
		
		return position;
	}
	
	private void selectInsertOrDeleteElement(XmlElementNode node,XElement element)
	{
		boolean matchFound = false;
		
		if (node.insertElement)
		{
			// check for inserted element
			String xpath = element.getParent().getUniquePath();
			Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
			Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
			if (xpathResults.size() > 0)
			{
				// get the equivalent element
				XElement mergeParentElement = (XElement)xpathResults.get(0);

				//get it's tree node
				XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
				
				for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
				{
					XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
					if (mergeParentChild.getMergeAdded() == element)
					{
						// this is the one so select it
						calledFromOtherTree = 1;
						mergeTree.clearSelection();
						mergeTree.setSelectedNode(mergeParentChild.getElement(),false);
						calledFromOtherTree = 0;
						
						matchFound = true;
					}
				}
			}
		}
		else if (node.deleteElement)
		{
			// check for inserted element
			String xpath = element.getParent().getUniquePath();
			Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
			Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
			if (xpathResults.size() > 0)
			{
				// get the equivalent element
				XElement mergeParentElement = (XElement)xpathResults.get(0);

				//get it's tree node
				XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
				
				for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
				{
					XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
					if (mergeParentChild.getMergeDelete() == element)
					{
						// this is the one so select it
						calledFromOtherTree = 1;
						mergeTree.clearSelection();
						mergeTree.setSelectedNode(mergeParentChild.getElement(),false);
						calledFromOtherTree = 0;
						
						matchFound = true;
					}
				}
			}
		}
		
		if (!matchFound)
		{
			calledFromOtherTree = 1;
			mergeTree.clearSelection();
			calledFromOtherTree = 0;	
		}
			
	}
	
	
	/**
	 * Displays the xpath expression
	 *
	 * @param element The element whose path to display
	 */
	private void setXPath( XElement element) {
		
		if ( properties.isUniqueXPath()) 
		{
			xpathField.setText(element.getUniquePath());
		} else {
			xpathField.setText(element.getPath());
		}
	}
	
	private void setXPathString(String xpath)
	{
		xpathField.setText(xpath);
	}
		
	/**
	 * Displays the dialog
	 *
	 * @param document The ExchangeDocument
	 */
	public void show(ExchangerDocument document,String baseFile,String modFile,ExchangerEditor editor) 
	{
		this.editor = editor;
		
		JPanel form = new JPanel( new FormLayout(10, 2));
		
		diffDocument = document;
		
		XElement root = null;
		
		try{
			root = (XElement)diffDocument.getRoot();
		}
		catch(Exception e)
		{
			// catch all error handler, catches namespace prefix problem
			MessageHandler.showError( "A parsing error occurred!, please check the source files", "Compare XML Files Error");
			return;
		}
		
		XmlElementNode node = new XmlElementNode(root);
		tree.setRoot( node);
		//tree.expand( 2);
		
		expandDiffNodes(tree, node);
		((XmlCellRenderer)tree.getCellRenderer()).setFont( TextPreferences.getBaseFont());
		
		baseField.setText(baseFile);
		modField.setText(modFile);
	
		//pack();
		setSize( new Dimension( 600, 640));
		setLocationRelativeTo( parent);
		
		// set focus on the find next diff button
		setIntialFocus();
		
		// set the base file for the merge functionality
		this.baseFileURL = baseFile;
		
		// remove the splitpane if showing after the first time
		if (splitPane != null)
		{
			treePanel.remove(splitPane);
			treePanel.add(diffTreePane,BorderLayout.CENTER);
			mergeDocument = null;
			
			resetStatusbar(false);
			
			diffTreePane.setBorder(emptyBorder);
			diffTreePane.remove(diffTreeLabelPanel);
			
			//disable edit merge document action in toolbar
			getEditMergeDocumentAction().setEnabled(false);
		}
		
		activeSide = 0;
		//super.setVisible(true);
		super.show();
	}
	
	/**
	 * Expands nodes that are part of a diff pathway
	 *
	 * @param tree The tree
	 * @param node Then starting node
	 */
	private void expandDiffNodes(XmlTree tree, XmlElementNode node)
	{
		Enumeration children = node.children();
		while (children.hasMoreElements())
		{
			XmlElementNode childNode = (XmlElementNode)children.nextElement();
			if (childNode.getDiffIcon() != null)
			{
				// has a diff
				tree.expandPath(new TreePath(childNode.getPath()));
				expandDiffNodes(tree,childNode);
			}
		}
	}
	
	/**
	 * When the dialog is cancelled and no selection has been made, 
	 * this method returns true.
	 *
	 * @return true when the dialog has been cancelled.
	 */
	public boolean isCancelled() 
	{
		return cancelled;
	}
	
	// set the focus to the next diff button
	private void setIntialFocus()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (buttonNext != null)
					buttonNext.grabFocus();
			}
		});
	}
	
	
	/**
	 * Creates the toolbar
	 *
	 * @return the toolbar
	 */
	private JToolBar createDiffToolbar()
	{
		JToolBar toolbar = new JToolBar();
		toolbar.setRollover( true);
		toolbar.setFloatable( false);
		toolbar.setBorderPainted(true);
//		toolbar.setBorder(new LineBorder(Color.BLACK,1));
//		toolbar.setBorder( new MatteBorder( 0, 0, 1, 0, UIManager.getColor("controlDkShadow")));
		
		toolbar.add(getGotoFirstDiffAction()).setMnemonic(0);
		toolbar.add(getGotoPreviousDiffAction()).setMnemonic(0);
		
		buttonNext = toolbar.add(getGotoNextDiffAction());
		buttonNext.setMnemonic(0);
		buttonNext.grabFocus();
		
		toolbar.add(getGotoLastDiffAction()).setMnemonic(0);
		
		toolbar.addSeparator();

		toolbar.add(getCollapseAllAction()).setMnemonic(0);
		toolbar.add(getExpandAllAction()).setMnemonic(0);
		
		toolbar.addSeparator();

		toolbar.add(getShowMergeTreeAction()).setMnemonic(0);
		
//		toolbar.add(getEditMergeDocumentAction()).setMnemonic(0);
		
		return toolbar;
	}
	
	private GotoNextDiffAction getGotoNextDiffAction() {
		if (gotoNextDiff == null) {
			gotoNextDiff = new GotoNextDiffAction();
		}

		return gotoNextDiff ;
	}
	
	private GotoPreviousDiffAction getGotoPreviousDiffAction() {
		if (gotoPreviousDiff == null) {
			gotoPreviousDiff = new GotoPreviousDiffAction();
		}

		return gotoPreviousDiff ;
	}
	
	private GotoLastDiffAction getGotoLastDiffAction() {
		if (gotoLastDiff == null) {
			gotoLastDiff = new GotoLastDiffAction();
		}

		return gotoLastDiff ;
	}
	
	private GotoFirstDiffAction getGotoFirstDiffAction() {
		if (gotoFirstDiff == null) {
			gotoFirstDiff = new GotoFirstDiffAction();
		}

		return gotoFirstDiff ;
	}
	
	private ExpandAllAction getExpandAllAction() {
		if (expandAll == null) {
			expandAll = new ExpandAllAction();
		}

		return expandAll;
	}

	private CollapseAllAction getCollapseAllAction() {
		if (collapseAll == null) {
			collapseAll = new CollapseAllAction();
		}

		return collapseAll;
	}
	
	private ShowMergeTreeAction getShowMergeTreeAction() {
		if (showMergeTree == null) {
			showMergeTree = new ShowMergeTreeAction();
		}

		return showMergeTree;
	}
	
	private EditMergeDocumentAction getEditMergeDocumentAction() {
		if (editMergeDocument == null) {
			editMergeDocument = new EditMergeDocumentAction();
		}

		return editMergeDocument;
	}
	
	private void firePopupTriggered( MouseEvent event, XmlElementNode node) {
		
		JPopupMenu popup = null;
		
		if (node.insertElement != false && !node.isEndTag())
		{
			popup = getInsertElementPopup();
		}
		else if (node.deleteElement != false  && !node.isEndTag())
		{
			popup = getDeleteElementPopup();
		}
		else if ((node.updateElementFrom != null || node.updateAttributes != null ||
				node.insertAttributes != null || node.deleteAttributes != null) && (!node.isEndTag()))
		{
			if (node.getChildCount() > 0)
			{
				popup = getGeneralAndChildrenPopup();
			}
			else
			{
				popup = getGeneralPopup();
			}
		}
		else if (node.getDiffIcon() != null && !node.isEndTag())
		{
			// resolve all children
			popup = getResolveChildrenPopup();
		}
		
		if ( popup != null) {
			popup.show( tree, event.getX(), event.getY());
		}
	}
	
	private JPopupMenu getDeleteElementPopup() {
		if ( deleteElementPopup == null) {
			deleteElementPopup = new JPopupMenu();
			deleteElementPopup.add( new DeleteElementModifiedAction());
			deleteElementPopup.addSeparator();
			deleteElementPopup.add( new DeleteElementBaseAction());
		}
		
		return deleteElementPopup;
	}
	
	private JPopupMenu getInsertElementPopup() {
		if ( insertElementPopup == null) {
			insertElementPopup = new JPopupMenu();
			insertElementPopup.add( new InsertElementModifiedAction());
			insertElementPopup.addSeparator();
			insertElementPopup.add( new InsertElementBaseAction());
		}
		
		return insertElementPopup;
	}
	
	private JPopupMenu getResolveChildrenPopup() {
		if ( resolveChildrenPopup == null) {
			resolveChildrenPopup = new JPopupMenu();
			resolveChildrenPopup.add( new ResolveChildrenModifiedAction());
			resolveChildrenPopup.addSeparator();
			resolveChildrenPopup.add( new ResolveChildrenBaseAction());
		}
		
		return resolveChildrenPopup;
	}
	
	private JPopupMenu getGeneralPopup() {
		if ( generalPopup == null) {
			generalPopup = new JPopupMenu();
			generalPopup.add( new GeneralModifiedAction());
			generalPopup.addSeparator();
			generalPopup.add( new GeneralBaseAction());
		}
		
		return generalPopup;
	}
	
	private JPopupMenu getGeneralAndChildrenPopup() {
		if ( generalAndChildrenPopup == null) {
			generalAndChildrenPopup = new JPopupMenu();
			generalAndChildrenPopup.add( new GeneralModifiedAction());
			generalAndChildrenPopup.addSeparator();
			generalAndChildrenPopup.add( new GeneralBaseAction());
			generalAndChildrenPopup.addSeparator();
			generalAndChildrenPopup.add( new GeneralAndChildrenModifiedAction());
			generalAndChildrenPopup.addSeparator();
			generalAndChildrenPopup.add( new GeneralAndChildrenBaseAction());
		}
		
		return generalAndChildrenPopup;
	}
	
	/**
	 * The ResolveChildrenBaseAction action class
	 */
	class ResolveChildrenBaseAction extends AbstractAction {
	 	/**
		 * The constructor for the action to use the base value
		 *
		 */
	 	public ResolveChildrenBaseAction() {
			super("Use Base for children");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the resolve children for base 
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) {
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				resolveChildrenBase(node);
			}
	 	}
	}
	
	/**
	 * The ResolveChildrenModifiedAction action class
	 */
	class ResolveChildrenModifiedAction extends AbstractAction {
	 	/**
		 * The constructor for the action to use the base value
		 *
		 */
	 	public ResolveChildrenModifiedAction() {
			super("Use Modifed for children");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the resolve children for base 
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) {
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				resolveChildrenModified(node);
			}		
	 	}
	}
	
	private void resolveChildrenBase(XmlElementNode nodeParent)
	{
		for ( int i = 0; i < nodeParent.getChildCount(); i++) 
		{
			XmlElementNode node = (XmlElementNode)nodeParent.getChildAt(i);
		
			if (node.insertElement != false && !node.isEndTag())
			{
				insertBaseAction(node);
			}
			else if (node.deleteElement != false  && !node.isEndTag())
			{
				deleteBaseAction(node);
			}
			else if ((node.updateElementFrom != null || node.updateAttributes != null ||
					node.insertAttributes != null || node.deleteAttributes != null) && (!node.isEndTag()))
			{
				generalBaseAction(node);	
			}
		
			resolveChildrenBase(node);
			
		}
	}
	
	private void resolveChildrenModified(XmlElementNode nodeParent)
	{
		for ( int i = 0; i < nodeParent.getChildCount(); i++) 
		{
			XmlElementNode node = (XmlElementNode)nodeParent.getChildAt(i);
		
			if (node.insertElement != false && !node.isEndTag())
			{
				insertModifiedAction(node);
			}
			else if (node.deleteElement != false  && !node.isEndTag())
			{
				deleteModifiedAction(node);
			}
			else if ((node.updateElementFrom != null || node.updateAttributes != null ||
					node.insertAttributes != null || node.deleteAttributes != null) && (!node.isEndTag()))
			{
				generalModifiedAction(node);	
			}
		
			resolveChildrenModified(node);
			
		}
	}	
	
	/**
	 * The InsertElementBaseAction action class
	 */
	class InsertElementBaseAction extends AbstractAction {
	 	/**
		 * The constructor for the action 
		 *
		 */
	 	public InsertElementBaseAction() {
			super("Do not insert");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the insert element base action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) {
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				insertBaseAction(node);
			}
	 	}
	}
	
	/**
	 * The InsertElementModifiedAction action class
	 */
	class InsertElementModifiedAction extends AbstractAction {
	 	/**
		 * The constructor for the action to use the base value
		 *
		 */
	 	public InsertElementModifiedAction() {
			super("Insert");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the insert element modified
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) 
			{
				
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				insertModifiedAction(node);
			}
	 	}
	}
	
	private void insertBaseAction(XmlElementNode node)
	{
		XElement element = node.getElement();
		if ( element != null) 
		{
			if (mergeDocument != null)
			{
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
					
					// get it's tree node
					XmlElementNode mergeTreeNode = mergeTree.getNode(mergeElement,false);
					
					if (!insertAlreadyAdded(element))
					{
						// can't find it so must be already removed
						return;
					}
					
					// unmerge with values from base file
					removeIfInsertFound(node,element);
					//unMergeInsert(node,element,mergeTreeNode,mergeElement);
				}
				else
				{	
					removeIfInsertFound(node,element);
				}
			}
		}
	}
	
	private void insertModifiedAction(XmlElementNode node)
	{
		XElement element = node.getElement();		
		if ( element != null) 
		{
			if (mergeDocument != null)
			{
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
					
					if (!insertAlreadyAdded(element))
					{
						// should have been already present
						mergeInsert(node,element);
					}
					
					// already present
					return;
				}
				else
				{	
					if (insertAlreadyAdded(element))
					{
						// don't add it a second time
						return;
					}
					
					// not present need to insert
					mergeInsert(node,element);
				}
			}
		}
	}
	
	
	private boolean insertAlreadyAdded(XElement element)
	{
		String xpath = element.getParent().getUniquePath();
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);

			//get it's tree node
			XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
			
			for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
			{
				XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
				if (mergeParentChild.getMergeAdded() == element)
				{
					return true;
				}
			}
			
			// already present
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	private XElement findInsertNode(XElement element)
	{
		String xpath = element.getParent().getUniquePath();
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);

			//get it's tree node
			XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
			
			for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
			{
				XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
				if (mergeParentChild.getMergeAdded() == element)
				{
					return mergeParentChild.getElement();
				}
			}
		}
		
		return null;
	}
	
	private XElement findDeleteNode(XElement element)
	{
		String xpath = element.getParent().getUniquePath();
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);

			//get it's tree node
			XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
			
			for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
			{
				XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
				if (mergeParentChild.getMergeDelete() == element)
				{
					return mergeParentChild.getElement();
				}
			}
		}
		
		return null;
	}
	
	private void removeIfInsertFound(XmlElementNode node,XElement element)
	{
		String xpath = element.getParent().getUniquePath();
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);

			//get it's tree node
			XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
			
			for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
			{
				XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
				if (mergeParentChild.getMergeAdded() == element)
				{
					// since we have a match, remove this node
					unMergeInsert(node,element,mergeParentChild,mergeParentChild.getElement());
				}
			}
		}
	}
	
	/**
	 * The DeleteElementBaseAction action class
	 */
	class DeleteElementBaseAction extends AbstractAction {
	 	/**
		 * The constructor for the action 
		 *
		 */
	 	public DeleteElementBaseAction() {
			super("Do not delete");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the delete element base action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) {
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				deleteBaseAction(node);
			}
	 	}
	}
	
	/**
	 * The DeleteElementModifiedAction action class
	 */
	class DeleteElementModifiedAction extends AbstractAction {
	 	/**
		 * The constructor for the action
		 *
		 */
	 	public DeleteElementModifiedAction() {
			super("Delete");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the delete element modified
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) 
			{
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				deleteModifiedAction(node);
			}
	 	}
	}
	
	private void deleteBaseAction(XmlElementNode node)
	{
		XElement element = node.getElement();
		
		if ( element != null) 
		{
			if (mergeDocument != null)
			{
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
					
					if (!deleteAlreadyPresent(element))
					{
						// should have been already present
						unMergeDelete(node,element);
					}
					
					// already present so don't need to add it
					return;
				}
				else
				{
					if (deleteAlreadyPresent(element))
					{
						// is already present
						return;
					}
					
					// it was already deleted so need to add it back
					unMergeDelete(node,element);
				}
			}
		}
	}
	
	private void deleteModifiedAction(XmlElementNode node)
	{
		XElement element = node.getElement();
		
		if ( element != null) 
		{
			if (mergeDocument != null)
			{
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
									
					// get it's tree node
					XmlElementNode mergeTreeNode = mergeTree.getNode(mergeElement,false);
					
					if (!deleteAlreadyPresent(element))
					{
						// should not have been already present
						return;
					}
					
					
					// unmerge with values from base file
					removeIfDeleteFound(node,element);
					//mergeDelete(node,element,mergeTreeNode,mergeElement);
				}
				else
				{
					removeIfDeleteFound(node,element);
					// it was already deleted
				}
			}
		}
	}
	
	
	private boolean deleteAlreadyPresent(XElement element)
	{
		String xpath = element.getParent().getUniquePath();
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);

			//get it's tree node
			XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
			
			for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
			{
				XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
				if (mergeParentChild.getMergeDelete() == element)
				{
					return true;
				}
			}
			
			// not already present
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	
	private void removeIfDeleteFound(XmlElementNode node,XElement element)
	{
		String xpath = element.getParent().getUniquePath();
		
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);

			//get it's tree node
			XmlElementNode mergeParentTreeNode = mergeTree.getNode(mergeParentElement,false);
			
			for (int i=0;i<mergeParentTreeNode.getChildCount();i++)
			{
				XmlElementNode mergeParentChild = (XmlElementNode)mergeParentTreeNode.getChildAt(i);
				if (mergeParentChild.getMergeDelete() == element)
				{
					// since we have a match, remove this node
					mergeDelete(node,element,mergeParentChild,mergeParentChild.getElement());
				}
			}
		}
	}

	
	
	/**
	 * The GeneralBaseAction action class
	 */
	class GeneralBaseAction extends AbstractAction {
	 	/**
		 * The constructor for the action to use the base value
		 *
		 */
	 	public GeneralBaseAction() {
			super("Use Base");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the general base action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) {
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				 generalBaseAction(node);
			}
		}
	}
	
	/**
	 * The GeneralModifiedAction action class
	 */
	class GeneralModifiedAction extends AbstractAction {
	 	/**
		 * The constructor for the action to use the base value
		 *
		 */
	 	public GeneralModifiedAction() {
			super("Use Modifed");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the general modified action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) 
			{
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				generalModifiedAction(node);
			}
		}
	}
	
	private void generalBaseAction(XmlElementNode node)
	{
		XElement element = node.getElement();
		
		if ( element != null) 
		{
			if (mergeDocument != null)
			{
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
					
					// get it's tree node
					XmlElementNode mergeTreeNode = mergeTree.getNode(mergeElement,false);
					
					// unmerge with values from base file
					unMergeChanges(node,element,mergeTreeNode,mergeElement);
				}
			}
		}
	}
	
	private void generalModifiedAction(XmlElementNode node)
	{
		XElement element = node.getElement();
		
		if ( element != null) 
		{
			if (mergeDocument != null)
			{
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
					
					// get it's tree node
					XmlElementNode mergeTreeNode = mergeTree.getNode(mergeElement,false);
					
					// merge with values from modified file
					mergeChanges(node,element,mergeTreeNode,mergeElement);
				}
			}
		}
	}		
	
	
	private void mergeChanges(XmlElementNode diffTreeNode,XElement diffElement,
							  XmlElementNode mergeTreeNode,XElement mergeElement)
	{
		boolean needsMerge = false;
		
		// test for the different kinds of diffs
		if (diffTreeNode.updateElementFrom != null)
		{
			if (isMixedElementAndContent(diffElement))
			{
				mergeTextNodes(diffTreeNode, diffElement, mergeTreeNode,mergeElement);
				needsMerge = true;
			}
			else
			{
				// need to change the content
				String modifiedContent = diffElement.getText();
				mergeElement.setText(modifiedContent);
				needsMerge = true;
			}
		}
		
		if (diffTreeNode.updateAttributes != null)
		{
			// need to change attribute values
			
			Enumeration attrNames =  diffTreeNode.updateAttributes.keys();
			while (attrNames.hasMoreElements())
			{
				String attrName = (String)attrNames.nextElement();
				String attrValue = diffElement.getAttribute(attrName);
				mergeElement.setAttributeValue(attrName, attrValue);
				needsMerge = true;
			}
		}
		
		if (diffTreeNode.insertAttributes != null)
		{
			// need to insert attributes
			for (int i=0;i<diffTreeNode.insertAttributes.size();i++)
			{
				String attrName = (String)diffTreeNode.insertAttributes.get(i);
				String attrValue = diffElement.getAttribute(attrName);
				mergeElement.setAttributeValue(attrName, attrValue);
				needsMerge = true;
			}
		}
		
		if (diffTreeNode.deleteAttributes != null)
		{
			// need to insert attributes
			for (int i=0;i<diffTreeNode.deleteAttributes.size();i++)
			{
				String attrName = (String)diffTreeNode.deleteAttributes.get(i);
				Attribute attr = mergeElement.attribute(attrName);
				
				if (attr != null)
				{
					mergeElement.remove(attr);
					needsMerge = true;	
				}
			}
		}
		
		if (needsMerge)
		{
			mergeTreeNode.setMerged(true);
			mergeTreeNode.setElement(mergeElement);
			mergeTreeNode.setDiffIcon();
			mergeTreeNode.updateCurrentAndChildren();
			
			mergeTree.changed(mergeTreeNode);
		}
	}
	
	private void mergeTextNodes(XmlElementNode diffTreeNode, XElement diffElement,
			  							XmlElementNode mergeTreeNode,XElement mergeElement)
	{	
		List children = diffElement.getChildren();
		for (int i=0;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Text)
			{
				Text text = (Text)node;
				
				// find the equivalent merge node
				String uniquePath = text.getUniquePath();
				
				int textNodePos = findTextPosition(text,(XElement)text.getParent());
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				
				StringBuffer xpath = new StringBuffer(uniquePath);
				xpath.append("[");
				xpath.append(textNodePos);
				xpath.append("]");
				
				Vector xpathResults = mergeDocument.search(xpath.toString(),namespaceMappings);
				if (xpathResults.size() > 0)
				{	
					Text xpathResult = (Text)xpathResults.get(0);
					XmlTextNode xmlTextNode = (XmlTextNode)mergeTree.getTextNode(xpathResult);
					
					if (text.getText().equals(""))
					{
						mergeElement.remove(xpathResult);
						mergeTree.removeNode(xmlTextNode);
					}
					else
					{
						xpathResult.setText(text.getText());
					
						xmlTextNode.setMerged(true);
						xmlTextNode.setTextNode(xpathResult);
						xmlTextNode.updateTextNode();
						mergeTree.changed(xmlTextNode);
					}
					
				}
				else
				{
					// new to add new text node
					if (!text.getText().equals(""))
			   		{	
			   		    // add new text node
			   			addNewTextNode(text, text.getText(),true);
			   		}
					
				}
			}
		}
	}
	
	private void unMergeTextNodes(XmlElementNode diffTreeNode, XElement diffElement,
				XmlElementNode mergeTreeNode,XElement mergeElement)
	{	
		List children = diffElement.getChildren();
		for (int i=0;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Text)
			{
				Text text = (Text)node;
				XmlTextNode xmlDiffTextNode = (XmlTextNode)tree.getTextNode(text);
				
				// find the equivalent merge node
				String uniquePath = text.getUniquePath();
			
				int textNodePos = findTextPosition(text,(XElement)text.getParent());
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				
				StringBuffer xpath = new StringBuffer(uniquePath);
				xpath.append("[");
				xpath.append(textNodePos);
				xpath.append("]");
				
				Vector xpathResults = mergeDocument.search(xpath.toString(),namespaceMappings);
				if (xpathResults.size() > 0)
				{	
					Text xpathResult = (Text)xpathResults.get(0);
					XmlTextNode xmlTextNode = (XmlTextNode)mergeTree.getTextNode(xpathResult);
					
					if (xmlDiffTextNode.updateTextFrom == null || xmlDiffTextNode.updateTextFrom.equals(""))
					{
						mergeElement.remove(xpathResult);
						mergeTree.removeNode(xmlTextNode);
					}
					else
					{
						xpathResult.setText(xmlDiffTextNode.updateTextFrom);
						
						xmlTextNode.setMerged(false);
						xmlTextNode.setTextNode(xpathResult);
						xmlTextNode.updateTextNode();
						mergeTree.changed(xmlTextNode);
					}
					
					
			   }
			   else
			   {	
			   		if (xmlDiffTextNode.updateTextFrom != null && !xmlDiffTextNode.updateTextFrom.equals(""))
			   		{
			   		    // add new text node
			   			addNewTextNode(text, xmlDiffTextNode.updateTextFrom, false);
			   		}
			   }
			}
		}
	}
	
	private void addNewTextNode(Text text,String updateText,boolean merging)
	{
		int position = 0;
		
		// get child position
		XElement parent = (XElement)text.getParent();
		
		Vector children = parent.getChildren();
		
		int childPosition = 0;
		int childPositonCounter = 0;
		
		for (int i=0;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Text)
			{
				Text textFound = (Text)node;
				if (textFound == text)
				{
					childPosition = childPositonCounter;
				}
				
				childPositonCounter++;
			}
			else
			{
				// to allow for PIs etc
				childPositonCounter++;
			}
		}
		
		// get the next element or text node down, and find it's equivalent in the mergetree
		XElement mergeNextElement = null;
		Text mergeNextTextNode = null;
		
		for (int i=childPosition+1;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Element)
			{
				XElement nextEle = (XElement)node;
				
				// test for added or deleted elements
				XmlElementNode nextTreeNode = tree.getNode(nextEle,false);
				
				if (nextTreeNode.insertElement)
				{
					mergeNextElement = findInsertNode(nextEle);
					if (mergeNextElement != null)
						break;
				}
				else if (nextTreeNode.deleteElement)
				{
					mergeNextElement = findDeleteNode(nextEle);
					if (mergeNextElement != null)
						break;
				}
				else 
				{
					String xpath = nextEle.getUniquePath();
					
					Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
					Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
					
					if (xpathResults.size() > 0)
					{
						// found the equivalent next element in the merge doucmnet
						mergeNextElement = (XElement)xpathResults.get(0);
						break;
					}
				}
			}
			else if (node instanceof Text)
			{
				Text textNext = (Text)node;
				
				String uniquePath = text.getUniquePath();
				
				int textNodePos = findTextPosition(textNext,(XElement)text.getParent());
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				
				StringBuffer xpath = new StringBuffer(uniquePath);
				xpath.append("[");
				xpath.append(textNodePos);
				xpath.append("]");
				
				Vector xpathResults = mergeDocument.search(xpath.toString(),namespaceMappings);
				if (xpathResults.size() > 0)
				{
					Text foundText = (Text)xpathResults.get(0);
					if (textNext.getText().equals(foundText.getText()));
					{
						// found the equivalent text node in the merge doucmnet
						mergeNextTextNode = foundText;
						break;
					}
					
				}
			}
				
		}
		
		
		// find the child position of this 
		if (mergeNextElement == null && mergeNextTextNode == null)
		{
			// couldn't find equivalent, so add it to the end
			position = -1;
		}
		else
		{
			if (mergeNextElement != null)
			{
			
				// find the position of this equivalent element
				XElement mergeNextParent =  mergeNextElement.parent();
				List mergeNextParentChildren = mergeNextParent.getChildren();
				
				for (int i=0;i<mergeNextParentChildren.size();i++)
				{
					Node mergenode= (Node)mergeNextParentChildren.get(i);
					
					if (mergenode == mergeNextElement)
					{
						position = i;
					}
				}
			}
			else if (mergeNextTextNode != null)
			{	
				// find the position of this equivalent text node
				XElement mergeNextParent =  (XElement)mergeNextTextNode.getParent();
				List mergeNextParentChildren = mergeNextParent.getChildren();
				
				for (int i=0;i<mergeNextParentChildren.size();i++)
				{
					Node mergeChild = (Node)mergeNextParentChildren.get(i);
					if (mergeChild == mergeNextTextNode)
					{
						position = i;
					}
				}
			}
		}
		
		// position should now be where we want to add the element
		
		// need to add element and all it's children to mergeTree, so deep clone the element
		DefaultText newText = new DefaultText(updateText);
		
		// get the equivalent parent on the mergeTree
		String xpath = parent.getUniquePath();
		
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);
			
			// get it's tree node
			XmlElementNode mergeTreeParentNode = mergeTree.getNode(mergeParentElement,false);
			
			// create the new tree node
			XmlTextNode newTreeNode = new XmlTextNode(newText,mergeTreeParentNode);
			newTreeNode.setMerged(merging);
			newTreeNode.updateTextNode();
		
			List mergeParentChildren = mergeParentElement.content();
			if (mergeParentChildren.size() == 0)
			{
				// no children, so just add it anywhere
				mergeParentElement.add(newText);
				
				// reset parent in case the node looks like <a/>
				if (mergeTreeParentNode.getChildCount() < 1)
				{
					XmlElementNode endNode = 
						new XmlElementNode(mergeTreeParentNode.getElement(),true,mergeTreeParentNode);
					
					if (mergeTreeParentNode.getMerged())
					{
						endNode.setMerged(true);
						endNode.update();
					}
					
					mergeTree.insertNode(endNode,mergeTreeParentNode,mergeTreeParentNode.getChildCount());
					
					mergeTreeParentNode.setElement(mergeParentElement);
					mergeTreeParentNode.update();
					mergeTree.nodeChanged(mergeTreeParentNode);
				}
				
				mergeTree.insertNode(newTreeNode,mergeTreeParentNode,0);
			
				// highlight it
				calledFromOtherTree = 1;
				mergeTree.clearSelection();
				mergeTree.setSelectedNode(newTreeNode);
				calledFromOtherTree = 0;
				
			}
			else
			{
				if (position >= mergeParentChildren.size() || position == -1)
				{
					// it should be added to the end of all the children
					mergeParentChildren.add(newText);
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,mergeParentChildren.size()-1);
					
					// highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
				else
				{	
					// add it in the particular position
					mergeParentChildren.add(position,newText);
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,position);
					
					//highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
			}
		}
		
		syncSelection();
	}
	
	private void unMergeChanges(XmlElementNode diffTreeNode,XElement diffElement,
			  XmlElementNode mergeTreeNode,XElement mergeElement)
	{
		if (!mergeTreeNode.getMerged())
		{
			// already in base state
			return;
		}
		
		boolean unsetMerge = false;
		
		// test for the different kinds of diffs
		if (diffTreeNode.updateElementFrom != null)
		{
			if (isMixedElementAndContent(diffElement))
			{
				unMergeTextNodes(diffTreeNode, diffElement, mergeTreeNode,mergeElement);
				unsetMerge = true;
			}
			else
			{
				// need to change the content back to what it was
				mergeElement.setText(diffTreeNode.updateElementFrom);
				unsetMerge = true;
			}
		}
		
		if (diffTreeNode.updateAttributes != null)
		{
			Enumeration attrNames =  diffTreeNode.updateAttributes.keys();
			while (attrNames.hasMoreElements())
			{
				String attrName = (String)attrNames.nextElement();
				String attrValue = (String)diffTreeNode.updateAttributes.get(attrName);
				mergeElement.setAttributeValue(attrName, attrValue);
				unsetMerge = true;
			}
		}
		
		if (diffTreeNode.insertAttributes != null)
		{
			// need to insert attributes
			for (int i=0;i<diffTreeNode.insertAttributes.size();i++)
			{
				String attrName = (String)diffTreeNode.insertAttributes.get(i);
				Attribute attr = mergeElement.attribute(attrName);
				
				if (attr != null)
				{
					mergeElement.remove(attr);
					unsetMerge = true;	
				}
			}
		}
		
		if (diffTreeNode.deleteAttributes != null)
		{
			// need to insert attributes
			for (int i=0;i<diffTreeNode.deleteAttributes.size();i++)
			{
				String attrName = (String)diffTreeNode.deleteAttributes.get(i);
				String attrValue = diffElement.getAttribute(attrName);
				mergeElement.setAttributeValue(attrName, attrValue);
				unsetMerge = true;
			}
		}
		
		if (unsetMerge)
		{
			mergeTreeNode.setMerged(false);
			mergeTreeNode.setElement(mergeElement);
			mergeTreeNode.unSetDiffIcon();
			mergeTreeNode.updateCurrentAndChildren();
			
			mergeTree.changed(mergeTreeNode);
		}
	}
	
	
	private void mergeDelete(XmlElementNode diffTreeNode,XElement diffElement,
				XmlElementNode mergeTreeNode,XElement mergeElement)
	{	
		if (diffTreeNode.deleteElement)
		{
		
			XElement mergeParentElement = mergeElement.parent();
			XmlElementNode mergeTreeParentNode = (XmlElementNode)mergeTreeNode.getParent();
			
			//	need to delete element from DOM and tree model
			mergeElement.detach();
			mergeTree.removeNode(mergeTreeNode);
			
			// check to see if we need to remove this end tag
			if (mergeTreeParentNode != null && mergeParentElement != null)
			{
				if (mergeTreeParentNode.getChildCount() == 1)
				{
					// may have to remove </a> end tag
					XmlElementNode endNode = (XmlElementNode)mergeTreeParentNode.getChildAt(0);
					if (endNode.isEndTag())
					{
						// need to remove this end tag
						mergeTree.removeNode(endNode);
						
						mergeTreeParentNode.setElement(mergeParentElement);
						mergeTreeParentNode.update();
						mergeTree.nodeChanged(mergeTreeParentNode);
					}
				}
			}
		}
	}
	
	private void unMergeDelete(XmlElementNode diffTreeNode,XElement diffElement)
	{
		int position = 0;
		
		// get child position
		XElement parent = diffElement.parent();
		
		if (isMixedElementAndContent(parent))
		{
			unMergeMixedDelete(diffTreeNode, diffElement);
			return;
		}

		
		List children = parent.elements();
		
		int childPosition = 0;
		
		for (int i=0;i<children.size();i++)
		{
			XElement ele = (XElement)children.get(i);
			if (ele == diffElement)
			{
				childPosition = i;
			}
		}
		
		// get the next element down, and find it's equivalent in the mergetree
		XElement mergeNextElement = null;
		for (int i=childPosition+1;i<children.size();i++)
		{
			XElement nextEle = (XElement)children.get(i);
			
			// test for added or deleted elements
			XmlElementNode nextTreeNode = tree.getNode(nextEle,false);
			
			if (nextTreeNode.insertElement)
			{
				mergeNextElement = findInsertNode(nextEle);
				if (mergeNextElement != null)
					break;
			}
			else if (nextTreeNode.deleteElement)
			{
				mergeNextElement = findDeleteNode(nextEle);
				if (mergeNextElement != null)
					break;
			}
			else 
			{
				String xpath = nextEle.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				
				if (xpathResults.size() > 0)
				{
					// found the equivalent next element in the merge doucmnet
					mergeNextElement = (XElement)xpathResults.get(0);
					break;
				}
			}
		}
		
		// find the child position of this 
		if (mergeNextElement == null)
		{
			// couldn't find equivalent, so add it to the end
			position = -1;
		}
		else
		{
			// find the position of this equivalent element
			XElement mergeNextParent =  mergeNextElement.parent();
			List mergeNextParentChildren = mergeNextParent.elements();
			
			for (int i=0;i<mergeNextParentChildren.size();i++)
			{
				XElement mergeEle = (XElement)mergeNextParentChildren.get(i);
				if (mergeEle == mergeNextElement)
				{
					position = i;
				}
			}
		}
		
		// position should now be where we want to add the element
		
		// need to add element and all it's children to mergeTree, so deep clone the element
		XElement clonedEle = removePIs((XElement)diffElement.clone());
		
		// check if there is a namespace prefix
		String prefix = clonedEle.getNamespacePrefix();
		
		// get the equivalent parent on the mergeTree
		String xpath = parent.getUniquePath();
		
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		
		if (xpathResults.size() > 0)
		{
			boolean removeExtraNamespaceDeclaration = false;
			
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);
			
			// check if prefix declaration is already present
			if (prefix != null && !prefix.equals(""))
			{
				XElement tempEle = mergeParentElement;
				Namespace ns = null;
				
				while (tempEle != null)
				{
					ns = tempEle.getNamespaceForPrefix(prefix);
					if (ns != null)
					{
						// found a matching prefix definition
						removeExtraNamespaceDeclaration = true;
						break;
					}
					else
					{
						tempEle = (XElement)tempEle.getParent();
					}
				}
			}
			
			// get it's tree node
			XmlElementNode mergeTreeParentNode = mergeTree.getNode(mergeParentElement,false);
			
			// create the new tree node
			XmlElementNode newTreeNode = new XmlElementNode(clonedEle,mergeTreeParentNode);
			
			// remember taht we have added this delete
			newTreeNode.setMergeDelete(diffElement);
			
			List mergeParentChildren = mergeParentElement.elements();
			if (mergeParentChildren.size() == 0)
			{
				// no children, so just add it anywhere
				mergeParentElement.add(clonedEle);
				if (removeExtraNamespaceDeclaration)
				{
					Namespace eleNS = clonedEle.getNamespaceForPrefix(prefix);
					clonedEle.remove(eleNS);
					newTreeNode.updateCurrentAndChildren();
				}
				
				// reset parent in case the node looks like <a/>
				if (mergeTreeParentNode.getChildCount() < 1)
				{
					XmlElementNode endNode = 
						new XmlElementNode(mergeTreeParentNode.getElement(),true,mergeTreeParentNode);
					
					if (mergeTreeParentNode.getMerged())
					{
						endNode.setMerged(true);
						endNode.update();
					}
					
					mergeTree.insertNode(endNode,mergeTreeParentNode,mergeTreeParentNode.getChildCount());
					
					mergeTreeParentNode.setElement(mergeParentElement);
					mergeTreeParentNode.update();
					mergeTree.nodeChanged(mergeTreeParentNode);
				}
				
				mergeTree.insertNode(newTreeNode,mergeTreeParentNode,0);
			
				// highlight it
				calledFromOtherTree = 1;
				mergeTree.clearSelection();
				mergeTree.setSelectedNode(newTreeNode);
				calledFromOtherTree = 0;
				
			}
			else
			{
				if (position >= mergeParentChildren.size() || position == -1)
				{
					// it should be added to the end of all the children
					mergeParentChildren.add(clonedEle);
					if (removeExtraNamespaceDeclaration)
					{
						Namespace eleNS = clonedEle.getNamespaceForPrefix(prefix);
						clonedEle.remove(eleNS);
						newTreeNode.updateCurrentAndChildren();
					}
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,mergeParentChildren.size()-1);
					
					// highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
				else
				{
					// add it in the particular position
					mergeParentChildren.add(position,clonedEle);
					if (removeExtraNamespaceDeclaration)
					{
						Namespace eleNS = clonedEle.getNamespaceForPrefix(prefix);
						clonedEle.remove(eleNS);
						newTreeNode.updateCurrentAndChildren();
					}
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,position);
					
					//highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
			}
		}
		
		syncSelection();
	}
	
	private void mergeMixedInsert(XmlElementNode diffTreeNode,XElement diffElement)
	{
		int position = 0;
		
		// get child position
		XElement parent = diffElement.parent();
		
		Vector children = parent.getChildren();
		
		int childPosition = 0;
		int childPositonCounter = 0;
		
		for (int i=0;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Element)
			{
				XElement ele = (XElement)node;
				if (ele.equals(diffElement))
				{
					childPosition = childPositonCounter;
				}
				
				childPositonCounter++;
			}
			else if (node instanceof Text)
			{
				if (!isWhiteSpace(node))
				{
					childPositonCounter++;
				}
			}
			else
			{
				// to allow for PIs etc
				childPositonCounter++;
			}
		}
		
		// get the next element or text node down, and find it's equivalent in the mergetree
		XElement mergeNextElement = null;
		Text mergeNextTextNode = null;
		
		for (int i=childPosition+1;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Element)
			{
				XElement nextEle = (XElement)node;
				
				// test for added or deleted elements
				XmlElementNode nextTreeNode = tree.getNode(nextEle,false);
				
				if (nextTreeNode.insertElement)
				{
					mergeNextElement = findInsertNode(nextEle);
					if (mergeNextElement != null)
						break;
				}
				else if (nextTreeNode.deleteElement)
				{
					mergeNextElement = findDeleteNode(nextEle);
					if (mergeNextElement != null)
						break;
				}
				else 
				{
					String xpath = nextEle.getUniquePath();
					
					Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
					Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
					
					if (xpathResults.size() > 0)
					{
						// found the equivalent next element in the merge doucmnet
						mergeNextElement = (XElement)xpathResults.get(0);
						break;
					}
				}
			}
			else if (node instanceof Text)
			{
				Text text = (Text)node;
				
				String uniquePath = text.getUniquePath();
				
				int textNodePos = findTextPosition(text,(XElement)text.getParent());
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				
				StringBuffer xpath = new StringBuffer(uniquePath);
				xpath.append("[");
				xpath.append(textNodePos);
				xpath.append("]");
				
				Vector xpathResults = mergeDocument.search(xpath.toString(),namespaceMappings);
				if (xpathResults.size() > 0)
				{
					Text foundText = (Text)xpathResults.get(0);
					if (text.getText().equals(foundText.getText()));
					{
						// found the equivalent next element in the merge doucmnet
						mergeNextTextNode = foundText;
						break;
					}
					
				}
			}
				
		}
		
		
		// find the child position of this 
		if (mergeNextElement == null && mergeNextTextNode == null)
		{
			// couldn't find equivalent, so add it to the end
			position = -1;
		}
		else
		{
			if (mergeNextElement != null)
			{
			
				// find the position of this equivalent element
				XElement mergeNextParent =  mergeNextElement.parent();
				List mergeNextParentChildren = mergeNextParent.getChildren();
				
				for (int i=0;i<mergeNextParentChildren.size();i++)
				{
					Node mergenode= (Node)mergeNextParentChildren.get(i);
					
					if (mergenode == mergeNextElement)
					{
						position = i;
					}
				}
			}
			else if (mergeNextTextNode != null)
			{	
				// find the position of this equivalent text node
				XElement mergeNextParent =  (XElement)mergeNextTextNode.getParent();
				List mergeNextParentChildren = mergeNextParent.getChildren();
				
				for (int i=0;i<mergeNextParentChildren.size();i++)
				{
					Node mergeChild = (Node)mergeNextParentChildren.get(i);
					if (mergeChild == mergeNextTextNode)
					{
						position = i;
					}
				}
			}
		}
		
		// position should now be where we want to add the element
		
		// need to add element and all it's children to mergeTree, so deep clone the element
		XElement clonedEle = removePIs((XElement)diffElement.clone());
		
		// get the equivalent parent on the mergeTree
		String xpath = parent.getUniquePath();
		
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);
			
			// get it's tree node
			XmlElementNode mergeTreeParentNode = mergeTree.getNode(mergeParentElement,false);
			
			// create the new tree node
			XmlElementNode newTreeNode = new XmlElementNode(clonedEle,mergeTreeParentNode);
			newTreeNode.setMerged(true);
			newTreeNode.setDiffIcon();
			
			// to remeber that we added this merge
			newTreeNode.setMergeAdded(diffElement);
			newTreeNode.updateCurrentAndChildren();
			
			List mergeParentChildren = mergeParentElement.content();
			if (mergeParentChildren.size() == 0)
			{
				// no children, so just add it anywhere
				mergeParentElement.add(clonedEle);
				
				// reset parent in case the node looks like <a/>
				if (mergeTreeParentNode.getChildCount() < 1)
				{
					XmlElementNode endNode = 
						new XmlElementNode(mergeTreeParentNode.getElement(),true,mergeTreeParentNode);
					
					if (mergeTreeParentNode.getMerged())
					{
						endNode.setMerged(true);
						endNode.update();
					}
					
					mergeTree.insertNode(endNode,mergeTreeParentNode,mergeTreeParentNode.getChildCount());
					
					mergeTreeParentNode.setElement(mergeParentElement);
					mergeTreeParentNode.update();
					mergeTree.nodeChanged(mergeTreeParentNode);
				}
				
				mergeTree.insertNode(newTreeNode,mergeTreeParentNode,0);
			
				// highlight it
				calledFromOtherTree = 1;
				mergeTree.clearSelection();
				mergeTree.setSelectedNode(newTreeNode);
				calledFromOtherTree = 0;
				
			}
			else
			{
				if (position >= mergeParentChildren.size() || position == -1)
				{
					// it should be added to the end of all the children
					mergeParentChildren.add(clonedEle);
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,mergeParentChildren.size()-1);
					
					// highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
				else
				{	
					// add it in the particular position
					mergeParentChildren.add(position,clonedEle);
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,position);
					
					//highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
			}
		}
		
		syncSelection();
	}
	
	private void unMergeMixedDelete(XmlElementNode diffTreeNode,XElement diffElement)
	{
		int position = 0;
		
		// get child position
		XElement parent = diffElement.parent();
		
		Vector children = parent.getChildren();
		
		int childPosition = 0;
		int childPositonCounter = 0;
		
		for (int i=0;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Element)
			{
				XElement ele = (XElement)node;
				if (ele.equals(diffElement))
				{
					childPosition = childPositonCounter;
				}
				
				childPositonCounter++;
			}
			else if (node instanceof Text)
			{
				if (!isWhiteSpace(node))
				{
					childPositonCounter++;
				}
			}
			else
			{
				// to allow for PIs etc
				childPositonCounter++;
			}
		}
		
		// get the next element or text node down, and find it's equivalent in the mergetree
		XElement mergeNextElement = null;
		Text mergeNextTextNode = null;
		
		for (int i=childPosition+1;i<children.size();i++)
		{
			Node node = (Node)children.get(i);
			if (node instanceof Element)
			{
				XElement nextEle = (XElement)node;
				
				// test for added or deleted elements
				XmlElementNode nextTreeNode = tree.getNode(nextEle,false);
				
				if (nextTreeNode.insertElement)
				{
					mergeNextElement = findInsertNode(nextEle);
					if (mergeNextElement != null)
						break;
				}
				else if (nextTreeNode.deleteElement)
				{
					mergeNextElement = findDeleteNode(nextEle);
					if (mergeNextElement != null)
						break;
				}
				else 
				{
					String xpath = nextEle.getUniquePath();
					
					Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
					Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
					
					if (xpathResults.size() > 0)
					{
						// found the equivalent next element in the merge doucmnet
						mergeNextElement = (XElement)xpathResults.get(0);
						break;
					}
				}
			}
			else if (node instanceof Text)
			{
				Text text = (Text)node;
				
				String uniquePath = text.getUniquePath();
				
				int textNodePos = findTextPosition(text,(XElement)text.getParent());
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				
				StringBuffer xpath = new StringBuffer(uniquePath);
				xpath.append("[");
				xpath.append(textNodePos);
				xpath.append("]");
				
				Vector xpathResults = mergeDocument.search(xpath.toString(),namespaceMappings);
				if (xpathResults.size() > 0)
				{
					Text foundText = (Text)xpathResults.get(0);
					if (text.getText().equals(foundText.getText()));
					{
						// found the equivalent next element in the merge doucmnet
						mergeNextTextNode = foundText;
						break;
					}
					
				}
			}
				
		}
		
		
		// find the child position of this 
		if (mergeNextElement == null && mergeNextTextNode == null)
		{
			// couldn't find equivalent, so add it to the end
			position = -1;
		}
		else
		{
			if (mergeNextElement != null)
			{
			
				// find the position of this equivalent element
				XElement mergeNextParent =  mergeNextElement.parent();
				List mergeNextParentChildren = mergeNextParent.getChildren();
				
				for (int i=0;i<mergeNextParentChildren.size();i++)
				{
					Node mergenode= (Node)mergeNextParentChildren.get(i);
					
					if (mergenode == mergeNextElement)
					{
						position = i;
					}
				}
			}
			else if (mergeNextTextNode != null)
			{	
				// find the position of this equivalent text node
				XElement mergeNextParent =  (XElement)mergeNextTextNode.getParent();
				List mergeNextParentChildren = mergeNextParent.getChildren();
				
				for (int i=0;i<mergeNextParentChildren.size();i++)
				{
					Node mergeChild = (Node)mergeNextParentChildren.get(i);
					if (mergeChild == mergeNextTextNode)
					{
						position = i;
					}
				}
			}
		}
		
		// position should now be where we want to add the element
		
		// need to add element and all it's children to mergeTree, so deep clone the element
		XElement clonedEle = removePIs((XElement)diffElement.clone());
		
		// get the equivalent parent on the mergeTree
		String xpath = parent.getUniquePath();
		
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		
		if (xpathResults.size() > 0)
		{
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);
			
			// get it's tree node
			XmlElementNode mergeTreeParentNode = mergeTree.getNode(mergeParentElement,false);
			
			// create the new tree node
			XmlElementNode newTreeNode = new XmlElementNode(clonedEle,mergeTreeParentNode);
			
			// remember that we have added this delete
			newTreeNode.setMergeDelete(diffElement);
			
			List mergeParentChildren = mergeParentElement.content();
			if (mergeParentChildren.size() == 0)
			{
				// no children, so just add it anywhere
				mergeParentElement.add(clonedEle);
				
				// reset parent in case the node looks like <a/>
				if (mergeTreeParentNode.getChildCount() < 1)
				{
					XmlElementNode endNode = 
						new XmlElementNode(mergeTreeParentNode.getElement(),true,mergeTreeParentNode);
					
					if (mergeTreeParentNode.getMerged())
					{
						endNode.setMerged(true);
						endNode.update();
					}
					
					mergeTree.insertNode(endNode,mergeTreeParentNode,mergeTreeParentNode.getChildCount());
					
					mergeTreeParentNode.setElement(mergeParentElement);
					mergeTreeParentNode.update();
					mergeTree.nodeChanged(mergeTreeParentNode);
				}
				
				mergeTree.insertNode(newTreeNode,mergeTreeParentNode,0);
			
				// highlight it
				calledFromOtherTree = 1;
				mergeTree.clearSelection();
				mergeTree.setSelectedNode(newTreeNode);
				calledFromOtherTree = 0;
				
			}
			else
			{
				if (position >= mergeParentChildren.size() || position == -1)
				{
					// it should be added to the end of all the children
					mergeParentChildren.add(clonedEle);
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,mergeParentChildren.size()-1);
					
					// highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
				else
				{	
					// add it in the particular position
					mergeParentChildren.add(position,clonedEle);
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,position);
					
					//highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
			}
		}
		
		syncSelection();
	}
	
	private void mergeInsert(XmlElementNode diffTreeNode,XElement diffElement)
	{
		int position = 0;
		
		// get child position
		XElement parent = diffElement.parent();
		
		if (isMixedElementAndContent(parent))
		{
			mergeMixedInsert(diffTreeNode, diffElement);
			return;
		}
		
		List children = parent.elements();
		int childPosition = 0;
		
		for (int i=0;i<children.size();i++)
		{
			XElement ele = (XElement)children.get(i);
			if (ele == diffElement)
			{
				childPosition = i;
			}
		}
		
		// get the next element down, and find it's equivalent in the mergetree
		XElement mergeNextElement = null;
		for (int i=childPosition+1;i<children.size();i++)
		{
			XElement nextEle = (XElement)children.get(i);
			
			// test for added or deleted elements
			XmlElementNode nextTreeNode = tree.getNode(nextEle,false);
			
			if (nextTreeNode.insertElement)
			{
				mergeNextElement = findInsertNode(nextEle);
				if (mergeNextElement != null)
					break;
			}
			else if (nextTreeNode.deleteElement)
			{
				mergeNextElement = findDeleteNode(nextEle);
				if (mergeNextElement != null)
					break;
			}
			else 
			{
				String xpath = nextEle.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				
				if (xpathResults.size() > 0)
				{
					// found the equivalent next element in the merge doucmnet
					mergeNextElement = (XElement)xpathResults.get(0);
					break;
				}
			}
		}
		
		// find the child position of this 
		if (mergeNextElement == null)
		{
			// couldn't find equivalent, so add it to the end
			position = -1;
		}
		else
		{
			// find the position of this equivalent element
			XElement mergeNextParent =  mergeNextElement.parent();
			List mergeNextParentChildren = mergeNextParent.elements();
			
			for (int i=0;i<mergeNextParentChildren.size();i++)
			{
				XElement mergeEle = (XElement)mergeNextParentChildren.get(i);
				if (mergeEle == mergeNextElement)
				{
					position = i;
				}
			}
		}
		
		// position should now be where we want to add the element
		
		
		// need to add element and all it's children to mergeTree, so deep clone the element
		XElement clonedEle = removePIs((XElement)diffElement.clone());
		
		// check if there is a namespace prefix
		String prefix = clonedEle.getNamespacePrefix();
		
		
		// get the equivalent parent on the mergeTree
		String xpath = parent.getUniquePath();
		
		Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
		Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
		
		if (xpathResults.size() > 0)
		{
			boolean removeExtraNamespaceDeclaration = false; 
			
			// get the equivalent element
			XElement mergeParentElement = (XElement)xpathResults.get(0);
			
            // check if prefix declaration is already present
			if (prefix != null && !prefix.equals(""))
			{
				XElement tempEle = mergeParentElement;
				Namespace ns = null;
				
				while (tempEle != null)
				{
					ns = tempEle.getNamespaceForPrefix(prefix);
					if (ns != null)
					{
						// found a matching prefix definition
						removeExtraNamespaceDeclaration = true;
						break;
					}
					else
					{
						tempEle = (XElement)tempEle.getParent();
					}
				}
			}
			
			// get it's tree node
			XmlElementNode mergeTreeParentNode = mergeTree.getNode(mergeParentElement,false);
			
			// create the new tree node
			XmlElementNode newTreeNode = new XmlElementNode(clonedEle,mergeTreeParentNode);
			newTreeNode.setMerged(true);
			newTreeNode.setDiffIcon();
			
			// to remeber that we added this merge
			newTreeNode.setMergeAdded(diffElement);
			newTreeNode.updateCurrentAndChildren();
			
			List mergeParentChildren = mergeParentElement.elements();
			if (mergeParentChildren.size() == 0)
			{
				// no children, so just add it anywhere
				mergeParentElement.add(clonedEle);
				if (removeExtraNamespaceDeclaration)
				{
					Namespace eleNS = clonedEle.getNamespaceForPrefix(prefix);
					clonedEle.remove(eleNS);
					newTreeNode.updateCurrentAndChildren();
				}
				
				// reset parent in case the node looks like <a/>
				if (mergeTreeParentNode.getChildCount() < 1)
				{
					XmlElementNode endNode = 
						new XmlElementNode(mergeTreeParentNode.getElement(),true,mergeTreeParentNode);
					
					if (mergeTreeParentNode.getMerged())
					{
						endNode.setMerged(true);
						endNode.update();
					}
					
					mergeTree.insertNode(endNode,mergeTreeParentNode,mergeTreeParentNode.getChildCount());
					
					mergeTreeParentNode.setElement(mergeParentElement);
					mergeTreeParentNode.update();
					mergeTree.nodeChanged(mergeTreeParentNode);
				}
				
				mergeTree.insertNode(newTreeNode,mergeTreeParentNode,0);
			
				// highlight it
				calledFromOtherTree = 1;
				mergeTree.clearSelection();
				mergeTree.setSelectedNode(newTreeNode);
				calledFromOtherTree = 0;
				
			}
			else
			{
				if (position >= mergeParentChildren.size() || position == -1)
				{
					// it should be added to the end of all the children
					mergeParentChildren.add(clonedEle);
					if (removeExtraNamespaceDeclaration)
					{
						Namespace eleNS = clonedEle.getNamespaceForPrefix(prefix);
						clonedEle.remove(eleNS);
						newTreeNode.updateCurrentAndChildren();
					}
					
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,mergeParentChildren.size()-1);
					
					// highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
				else
				{
					// add it in the particular position
					mergeParentChildren.add(position,clonedEle);
					if (removeExtraNamespaceDeclaration)
					{
						Namespace eleNS = clonedEle.getNamespaceForPrefix(prefix);
						clonedEle.remove(eleNS);
						newTreeNode.updateCurrentAndChildren();
					}
					
					mergeTree.insertNode(newTreeNode,mergeTreeParentNode,position);
					
					//highlight it
					calledFromOtherTree = 1;
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(newTreeNode);
					calledFromOtherTree = 0;
				}
			}
		}
		
		syncSelection();
	}
	
	private void syncSelection()
	{
		TreePath path = tree.getSelectionPath();
		if ( path != null) 
		{
			
			XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
			XElement element = node.getElement();
			if ( element != null) 
			{
				setXPath( element);
				
				if (mergeDocument != null)
				{
					String xpath = element.getUniquePath();
					
					Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
					Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
					if (xpathResults.size() > 0)
					{
						// get the equivalent element
						XElement mergeElement = (XElement)xpathResults.get(0);
					    // get it's tree node
						XmlElementNode mergeTreeNode = mergeTree.getNode(mergeElement,false);
						
						// highlight it
						calledFromOtherTree = 1;
						mergeTree.clearSelection();
						mergeTree.setSelectedNode(mergeTreeNode);
						calledFromOtherTree = 0;
					}
				}
			}
		}
	}
	
	private void unMergeInsert(XmlElementNode diffTreeNode,XElement diffElement,
								XmlElementNode mergeTreeNode,XElement mergeElement)
	{	
		if (diffTreeNode.insertElement)
		{
			
			XElement mergeParentElement = mergeElement.parent();
			XmlElementNode mergeTreeParentNode = (XmlElementNode)mergeTreeNode.getParent();
			
			//	need to delete element from DOM and tree model
			mergeElement.detach();
			mergeTreeNode.unSetDiffIcon();
			mergeTree.removeNode(mergeTreeNode);
			
			// check to see if we need to remove this end tag
			if (mergeTreeParentNode != null && mergeParentElement != null)
			{
				if (mergeTreeParentNode.getChildCount() == 1)
				{
					// may have to remove </a> end tag
					XmlElementNode endNode = (XmlElementNode)mergeTreeParentNode.getChildAt(0);
					if (endNode.isEndTag())
					{
						// need to remove this end tag
						mergeTree.removeNode(endNode);
						
						mergeTreeParentNode.setElement(mergeParentElement);
						mergeTreeParentNode.update();
						mergeTree.nodeChanged(mergeTreeParentNode);
					}
				}
			}
		}
	}
	
	private XElement removePIs(XElement element)
	{
		boolean piFound = false;
		
		for ( int i = 0; i < element.nodeCount(); i++) 
		{
			Node node = element.node( i);
			if (node instanceof ProcessingInstruction)
			{
				element.remove(node);
				piFound = true;
			}
			else if (node instanceof Element)
			{
				removePIs((XElement)node);
			}
		}
		
		if (piFound)
		{
			// parse out the whitespace that was added when the PI was removed
			element.setText(element.getTextTrim());
		}
		return element;
	}
	

	/**
	 * The GeneralAndChildrenBaseAction action class
	 */
	class GeneralAndChildrenBaseAction extends AbstractAction {
	 	/**
		 * The constructor for the action
		 *
		 */
	 	public GeneralAndChildrenBaseAction() {
			super("Use Base and resolve children");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the general base and children action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
			TreePath path = tree.getSelectionPath();
			if ( path != null) 
			{
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				generalBaseAction(node);
				resolveChildrenBase(node);
			}
	 	}
	}
	
	/**
	 * The GeneralAndChildrenModifiedAction action class
	 */
	class GeneralAndChildrenModifiedAction extends AbstractAction {
	 	/**
		 * The constructor for the action
		 *
		 */
	 	public GeneralAndChildrenModifiedAction() {
			super("Use Modifed and resolve children");
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the general modified action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		TreePath path = tree.getSelectionPath();
			if ( path != null) 
			{
				XmlElementNode node = (XmlElementNode) path.getLastPathComponent();
				generalModifiedAction(node);
				resolveChildrenModified(node);
			}
	 	}
	}
	
	/**
	 * The ExpandAll action class
	 */
	class ExpandAllAction extends AbstractAction {
	 	/**
		 * The constructor for the action to collapse all nodes 
		 * in the xdiff tree.
		 *
		 */
	 	public ExpandAllAction() {
			super("Expand XDIFF All");
			
			putValue( MNEMONIC_KEY, new Integer( 'x'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExpandAll.gif"));
			putValue( SHORT_DESCRIPTION, "Expands All Nodes");
			
		 	setEnabled(true);
	 	}

		/**
		 * The implementation of the collapse all action, called 
		 * after a user action.
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		if (activeSide == 0)
	 		{
	 			tree.expandAll();
	 		}
	 		else
	 		{
	 			mergeTree.expandAll();
	 		}
	 	}
	}
	
	/**
	 * The CollapseAll action class
	 */
	class CollapseAllAction extends AbstractAction {
	 	private static final boolean DEBUG = false;
		private Object view = null;
		
	 	/**
		 * The constructor for the action to collapse all nodes 
		 * in the xdiff tree.
		 *
		 */
	 	public CollapseAllAction() {
			super( "Collapse XDIFF All");
			
			putValue( MNEMONIC_KEY, new Integer( 'C'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CollapseAll.gif"));
			putValue( SHORT_DESCRIPTION, "Collapses All Nodes");
			
			setEnabled(true);
	 	}
	 	
		

		/**
		 * The implementation of the collapse all action, called 
		 * after a user action.
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		if (activeSide ==0)
	 		{
	 			tree.collapseAll();
	 			tree.expand(1);
	 		}
	 		else
	 		{
	 			mergeTree.collapseAll();
	 			mergeTree.expand(1);
	 		}
	 	}
	}
	
	/**
	 * The GotoNextDiff action class
	 */
	class GotoNextDiffAction extends AbstractAction {
	 	
	 	/**
		 * The constructor for the action to goto the next difference
		 *
		 */
	 	public GotoNextDiffAction() {
			super( "Next Difference");
			
			putValue( MNEMONIC_KEY, new Integer( 'F'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NextDiff16.gif"));
			putValue( SHORT_DESCRIPTION, "Show Next Difference");
			
			setEnabled(true);
	 	}
	 	
		/**
		 * The implementation of the goto next difference action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		if (activeSide == 0)
	 		{
	 		
		 		XmlElementNode node = null; 
		 		TreePath path = tree.getSelectionPath();
				if ( path != null) 
				{
					node = (XmlElementNode) path.getLastPathComponent();		
				}
				else
				{
					// start at the root
					node = tree.root;
					if (diffFound(node))
		 			{
						tree.clearSelection();
			 			tree.setSelectedNode(node.getElement(),false);
			 			return;
					}
				}
		 	 
				XmlElementNode nodeDiff = getNextDiff(node);
				
				if (nodeDiff != null)
		 		{
		 			tree.clearSelection();
		 			tree.setSelectedNode(nodeDiff.getElement(),false);
		 		}
				else
				{
					// leave it at the last diff
					tree.clearSelection();
		 			tree.setSelectedNode(node.getElement(),false);
				}
	 		}
	 		else
	 		{
	 			XmlElementNode node = null; 
		 		TreePath path = mergeTree.getSelectionPath();
				if ( path != null) 
				{
					node = (XmlElementNode) path.getLastPathComponent();
					
				}
				else
				{
					// start at the root
					node = mergeTree.root;
					if (mergeFound(node))
		 			{
						mergeTree.clearSelection();
						mergeTree.setSelectedNode(node.getElement(),false);
			 			return;
					}
					else
					{
						// no node selected so return
						return;
					}
				}
		 	 
				XmlElementNode nodeDiff = getNextMerge(node);
				
				if (nodeDiff != null)
		 		{
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(nodeDiff.getElement(),false);
		 		}
				else
				{
					// leave it at the last merge
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(node.getElement(),false);
				}
	 		}
	 	}
	}
	
	/**
	 * The GotoPreviousDiff action class
	 */
	class GotoPreviousDiffAction extends AbstractAction {
	 	
		
	 	/**
		 * The constructor for the action to goto the previous difference
		 *
		 */
	 	public GotoPreviousDiffAction() {
			super( "Previous Difference");
			
			putValue( MNEMONIC_KEY, new Integer( 'P'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/PreviousDiff16.gif"));
			putValue( SHORT_DESCRIPTION, "Show Previous Difference");
			
			setEnabled(true);
	 	}
	 	
		/**
		 * The implementation of the goto previous difference action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		if (activeSide == 0)
	 		{
		 		XmlElementNode node = null; 
		 		TreePath path = tree.getSelectionPath();
				if ( path != null) 
				{
					node = (XmlElementNode) path.getLastPathComponent();		
				}
				else
				{
					// no node selected, so just ignore
					return;
				}
		 	 
				XmlElementNode nodeDiff = getPreviousDiff(node);
				
				if (nodeDiff != null)
		 		{
		 			tree.clearSelection();
		 			tree.setSelectedNode(nodeDiff.getElement(),false);
		 		}
				else
				{
					// there is no previous, so leave it at the selected node
					tree.clearSelection();
		 			tree.setSelectedNode(node.getElement(),false);
				}
	 		}
	 		else
	 		{
	 			XmlElementNode node = null; 
		 		TreePath path = mergeTree.getSelectionPath();
				if ( path != null) 
				{
					node = (XmlElementNode) path.getLastPathComponent();		
				}
				else
				{
					// no node selected, so just ignore
					return;
				}
		 	 
				XmlElementNode nodeDiff = getPreviousMerge(node);
				
				if (nodeDiff != null)
		 		{
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(nodeDiff.getElement(),false);
		 		}
				else
				{
					// there is no previous, so leave it at the selected node
					mergeTree.clearSelection();
					mergeTree.setSelectedNode(node.getElement(),false);
				}
	 		}
	 	}
	}
	
	/**
	 * The GotoLastDiff action class
	 */
	class GotoLastDiffAction extends AbstractAction {
	 	
	 	/**
		 * The constructor for the action to goto the last difference
		 *
		 */
	 	public GotoLastDiffAction() {
			super( "Last Difference");
			
			putValue( MNEMONIC_KEY, new Integer( 'L'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/LastDiff16.gif"));
			putValue( SHORT_DESCRIPTION, "Show Last Difference");
			
			setEnabled(true);
	 	}
	 	
		/**
		 * The implementation of the goto last difference action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		
	 		if (activeSide == 0)
	 		{
		 		XmlElementNode node = getLastDiff(); 
		 		
		 		if (node != null)
		 		{
		 			tree.clearSelection();
		 			tree.setSelectedNode(node.getElement(),false);
		 		}
		 		
	 			return;
	 		}
	 		else
	 		{
	 			XmlElementNode node = getLastMerge(); 
		 		
		 		if (node != null)
		 		{
		 			mergeTree.clearSelection();
		 			mergeTree.setSelectedNode(node.getElement(),false);
		 		}
		 		
	 			return;
	 		}
		}
	 }
	
	/**
	 * The GotoFirstDiff action class
	 */
	class GotoFirstDiffAction extends AbstractAction {
	 	
	 	/**
		 * The constructor for the action to goto the first difference
		 *
		 */
	 	public GotoFirstDiffAction() {
			super( "First Difference");
			
			putValue( MNEMONIC_KEY, new Integer( 'F'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/FirstDiff16.gif"));
			putValue( SHORT_DESCRIPTION, "Show First Difference");
			
			setEnabled(true);
	 	}
	 	
		/**
		 * The implementation of the goto next difference action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		
	 		if (activeSide == 0)
	 		{
		 		XmlElementNode node = getFirstDiff(); 
		 		if (node != null)
		 		{
		 			tree.clearSelection();
		 			tree.setSelectedNode(node.getElement(),false);
		 		}
		 		
	 			return;
	 		}
	 		else
	 		{
	 			XmlElementNode node = getFirstMerge(); 
		 		if (node != null)
		 		{
		 			mergeTree.clearSelection();
		 			mergeTree.setSelectedNode(node.getElement(),false);
		 		}
		 		
	 			return;
	 		}
		}
	 }
	
	/**
	 * The ShowMergeTree action class
	 */
	class ShowMergeTreeAction extends AbstractAction {
	 	
	 	/**
		 * The constructor for the action to show the merged tree
		 *
		 */
	 	public ShowMergeTreeAction() {
			super( "Show Merge Tree");
			
			putValue( MNEMONIC_KEY, new Integer( 'M'));
			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Merge16.gif"));
			putValue( SHORT_DESCRIPTION, "Show Merge Tree");
			
			setEnabled(true);
	 	}
	 	
		/**
		 * The implementation of the show merge tree action
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		try
			{
	 			if (mergeDocument != null)
	 			{
	 				// already called before
	 				splitPane.setDividerLocation(treePanel.getWidth()/2);
		 			
		 			treePanel.revalidate();
		 			treePanel.repaint();
		 			return;
	 			}
	 			
	 			
	 			String baseEncoded = URLUtilities.encodeURL(baseFileURL);
	 			URL url = new URL(baseEncoded);
	 			
	 			mergeDocument = new ExchangerDocument(url, true);
	 			mergeDocument.load();
	 			
	 			XElement root = (XElement)mergeDocument.getRoot();
	 			XmlElementNode nodeRoot = new XmlElementNode(root);

	 			mergeTree.setRoot(nodeRoot);
	 			mergeTree.expand(1);
	 			
	 			((XmlCellRenderer)mergeTree.getCellRenderer()).setFont( TextPreferences.getBaseFont());
	 			
	 			
	 			diffTreeLabelPanel = new  JPanel(new BorderLayout());
	 			diffTreeLabelPanel.setBorder(new EmptyBorder( 2, 2, 2, 2));
	 			JLabel diffTreeLabel = new JLabel("Differences");
	 			diffTreeLabel.setVerticalAlignment( JLabel.BOTTOM);
	 			diffTreeLabel.setIcon( XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Diff16.gif"));
	 			diffTreeLabelPanel.add(diffTreeLabel,BorderLayout.WEST);
	 			
	 			mergeTreeLabelPanel = new  JPanel(new BorderLayout());
	 			JLabel mergeTreeLabel = new JLabel( "Merges");
	 			mergeTreeLabel.setVerticalAlignment( JLabel.BOTTOM);
	 			mergeTreeLabel.setIcon( XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Merge16.gif"));
	 			
	 			JButton editButton = new JButton( getEditMergeDocumentAction());
	 			editButton.setFont( editButton.getFont().deriveFont(Font.PLAIN));
	 			editButton.setMargin( new Insets( 1, 10, 1, 10));
	 			mergeTreeLabelPanel.add(mergeTreeLabel,BorderLayout.WEST);
	 			mergeTreeLabelPanel.add(editButton,BorderLayout.EAST);
	 			mergeTreeLabelPanel.setBorder( new EmptyBorder( 2, 2, 2, 2));
	 			
	 			diffTreeLabelPanel.setPreferredSize( mergeTreeLabelPanel.getPreferredSize());
	 			diffTreePane.add(diffTreeLabelPanel,BorderLayout.NORTH);

	 			mergeTreePane = new JPanel(new BorderLayout());
				JScrollPane mergeTreeScroll = new JScrollPane(mergeTree,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
														JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
				mergeTreePane.add(mergeTreeScroll,BorderLayout.CENTER);
	 			mergeTreePane.add(mergeTreeLabelPanel,BorderLayout.NORTH);
	 			
	 			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	 			if (splitPane.getDividerSize() > 6) 
	 			{
	 				splitPane.setDividerSize( 6);
	 			}
	 			splitPane.setResizeWeight(0.5);
	 			splitPane.setLeftComponent(diffTreePane);
	 			splitPane.setRightComponent(mergeTreePane);
	 			splitPane.setOneTouchExpandable(true);
	 			
	 			treePanel.remove(diffTreePane);
	 			treePanel.add(splitPane,BorderLayout.CENTER);
	 		
	 			splitPane.setDividerLocation(treePanel.getWidth()/2);
	 			
	 			// enable the edit document action in the toolbar
	 			getEditMergeDocumentAction().setEnabled(true);
	 			
	 			treePanel.revalidate();
	 			treePanel.repaint();
				
				// reset the status bar, amd make sure xpath is set
				resetStatusbar(true);
				
				// set the active side to be the diff tree
				setDiffActive();
				
				// load all the deleted element state
				loadDeletedState(tree.root,mergeDocument);
				
				
			}
	 		catch(Exception e)
			{
	 			MessageHandler.showError( "An error occurred parsing the following URL\n" +
	 					baseFileURL+"\n\n Please check the above source file", "Show Merge Tree Error");
				return;
	 		}
	 		
 			return;
		}
	 }
	
	/**
	 * The EditMergeDocumentAction action class
	 */
	class EditMergeDocumentAction extends AbstractAction {
	 	
		
	 	/**
		 * The constructor for the action to goto the previous difference
		 *
		 */
	 	public EditMergeDocumentAction() {
			super( "Edit");
			
//			putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/DocumentIcon.gif"));
			putValue( SHORT_DESCRIPTION, "Open merged document in Editor");
			
			setEnabled(false);
	 	}
	 	
		/**
		 * The implementation of open merge document in editor
		 *
		 * @param event the action event.
		 */
	 	public void actionPerformed( ActionEvent event) 
		{
	 		
	 		try{
	 		
	 		ExchangerOutputFormat format = new ExchangerOutputFormat();
	 		String indent = "    ";
	 		boolean newLines = true;
	 		boolean padText = false;
	 		boolean trim = true;
	 		boolean preserveMixed = true;
	 		int lineLength = 80;
	 		
	 		String formattedXML = 
	 			XMLUtilities.format( mergeDocument.getDocument().asXML(),null,"UTF8", indent, newLines, padText, lineLength, trim, preserveMixed, format);
	 		ExchangerDocument document = new ExchangerDocument(formattedXML, true);
			editor.open( document, null);
	 		}
	 		catch(Exception e)
			{
	 			MessageHandler.showError( "An error occured trying to open the merged file in the editor", "XML Diff and Merge");
				return;
	 		}
	 		
	 	}
	}
	
	private void resetStatusbar(boolean mergeShow)
	{
		main.remove(statusbar);
		statusbar = new Statusbar(this,mergeShow);
		statusbar.setBorder( new EmptyBorder( 1, 0, 0, 0));
		main.add(statusbar, BorderLayout.SOUTH);
		
		syncSelection();
	}
	
	private void setDiffActive()
	{
		activeSide = 0;
		mergeTreePane.setBorder(emptyBorder);
		diffTreePane.setBorder(lineBorder);
	}
	
	private void loadDeletedState(XmlElementNode diffNode,ExchangerDocument mergeDocument)
	{
		for (int i=0;i<diffNode.getChildCount();i++)
		{
			XmlElementNode child = (XmlElementNode)diffNode.getChildAt(i);
			if (child.deleteElement)
			{
				// need to record this fact in the new merge tree
				XElement element = child.getElement();
				String xpath = element.getUniquePath();
				
				Vector namespaceMappings = diffDocument.getDeclaredNamespaces();
				Vector xpathResults = mergeDocument.search(xpath,namespaceMappings);
				if (xpathResults.size() > 0)
				{
					// get the equivalent element
					XElement mergeElement = (XElement)xpathResults.get(0);
					
					// get it's tree node
					XmlElementNode mergeTreeNode = mergeTree.getNode(mergeElement,false);
					
					mergeTreeNode.setMergeDelete(element);
				}
			}
			else
			{
				loadDeletedState(child,mergeDocument);
			}
		}
	}
	
	
	/**
	 * whether or not the node contains a difference
	 *
	 * @param node  The node to test
	 */
	private boolean diffFound(XmlElementNode node)
	{
		if (node == null)
		{
			return false;
		}
		
		if (!node.insertElement && node.insertAttributes == null && !node.deleteElement 
			&& node.deleteAttributes == null && node.updateElementFrom == null 
			&& node.updateAttributes == null)
		{
			return false;
		}
		else
		{
			return true;
		}
		
	}
	
	/**
	 * whether or not the node contains a merge
	 *
	 * @param node  The node to test
	 */
	private boolean mergeFound(XmlElementNode node)
	{
		if (node == null)
		{
			return false;
		}
		
		if (node.getMerged())
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	/**
	 * Gets the next node after the given node that contains a difference
	 *
	 * @param node The current selected node
	 * @return The next diff node
	 */
	private XmlElementNode getNextMerge(XmlElementNode currentNode)
	{
		boolean currentFound = false;
		Enumeration allNodes = mergeTree.root.preorderEnumeration();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			
			if (!currentFound)
			{
				if (node.equals(currentNode))
				{
					currentFound = true;
				}
			}
			else
			{
				//check for merge
				if (node.getDiffIcon() != null && !node.isEndTag() && mergeFound(node))
				{
					return node;
				}	
			}
		}
		
		// couldn't fing any more merges
		return null;
	}
	
	/**
	 * Gets the next node after the given node that contains a merge
	 *
	 * @param node The current selected node
	 * @return The next merged node
	 */
	private XmlElementNode getNextDiff(XmlElementNode currentNode)
	{
		boolean currentFound = false;
		Enumeration allNodes = tree.root.preorderEnumeration();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			
			if (!currentFound)
			{
				if (node.equals(currentNode))
				{
					currentFound = true;
				}
			}
			else
			{
				//check for diff
				if (node.getDiffIcon() != null && !node.isEndTag() && diffFound(node))
				{
					return node;
				}	
			}
		}
		
		// couldn't fing any more diffs
		return null;
	}
	
	/**
	 * Gets the previous node before the given node that contains a difference
	 *
	 * @param node The current selected node
	 * @return The previous diff node
	 */
	private XmlElementNode getPreviousDiff(XmlElementNode currentNode)
	{
		Enumeration allNodes = tree.root.preorderEnumeration();
		Vector previousNodes = new Vector();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			
			if (node.equals(currentNode))
			{
				break;
			}
			else
			{
				previousNodes.add(node);
			}
		}

		int previousSize = previousNodes.size();
		
		for (int i=previousSize-1;i>-1;i--)
		{
			XmlElementNode node = (XmlElementNode)previousNodes.get(i);
			
			//check for diff
			if (node.getDiffIcon() != null && !node.isEndTag() && diffFound(node))
			{
				return node;
			}	
		}
		
		// couldn't fing any more diffs
		return null;
	}
	
	/**
	 * Gets the previous node before the given node that contains a merge
	 *
	 * @param node The current selected node
	 * @return The previous diff node
	 */
	private XmlElementNode getPreviousMerge(XmlElementNode currentNode)
	{
		Enumeration allNodes = mergeTree.root.preorderEnumeration();
		Vector previousNodes = new Vector();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			
			if (node.equals(currentNode))
			{
				break;
			}
			else
			{
				previousNodes.add(node);
			}
		}

		int previousSize = previousNodes.size();
		
		for (int i=previousSize-1;i>-1;i--)
		{
			XmlElementNode node = (XmlElementNode)previousNodes.get(i);
			
			//check for diff
			if (node.getDiffIcon() != null && !node.isEndTag() && mergeFound(node))
			{
				return node;
			}	
		}
		
		// couldn't fing any more diffs
		return null;
	}
	
	
	/**
	 * Gets the node that contains the first difference
	 *
	 * @return The first diff node
	 */
	private XmlElementNode getFirstDiff()
	{
		boolean currentFound = false;
		Enumeration allNodes = tree.root.preorderEnumeration();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			
			//check for diff
			if (node.getDiffIcon() != null && !node.isEndTag() && diffFound(node))
			{
				return node;
			}	
		}
		
		// couldn't fing any diffs (shouldn't happen)
		return null;
	}
	
	/**
	 * Gets the node that contains the first merge
	 *
	 * @return The first diff node
	 */
	private XmlElementNode getFirstMerge()
	{
		boolean currentFound = false;
		Enumeration allNodes = mergeTree.root.preorderEnumeration();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			
			//check for diff
			if (node.getDiffIcon() != null && !node.isEndTag() && mergeFound(node))
			{
				return node;
			}	
		}
		
		// couldn't fing any diffs (shouldn't happen)
		return null;
	}
	
	/**
	 * Gets the node that contains the last difference
	 *
	 * @return The last diff node
	 */
	private XmlElementNode getLastDiff()
	{
		Vector reverseNodes = new Vector();
		Enumeration allNodes = tree.root.preorderEnumeration();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			reverseNodes.add(node);
		}
		
		int reverseSize = reverseNodes.size();
		
		for (int i=reverseSize-1;i>-1;i--)
		{
			XmlElementNode node = (XmlElementNode)reverseNodes.get(i);
			
			//check for diff
			if (node.getDiffIcon() != null && !node.isEndTag() && diffFound(node))
			{
				return node;
			}	
		}
		
		// couldn't fing any more diffs
		return null;
	}
	
	/**
	 * Gets the node that contains the last merge
	 *
	 * @return The last merge node
	 */
	private XmlElementNode getLastMerge()
	{
		Vector reverseNodes = new Vector();
		Enumeration allNodes = mergeTree.root.preorderEnumeration();
		
		while (allNodes.hasMoreElements())
		{
			XmlElementNode node = (XmlElementNode)allNodes.nextElement();
			reverseNodes.add(node);
		}
		
		int reverseSize = reverseNodes.size();
		
		for (int i=reverseSize-1;i>-1;i--)
		{
			XmlElementNode node = (XmlElementNode)reverseNodes.get(i);
			
			//check for diff
			if (node.getDiffIcon() != null && !node.isEndTag() && mergeFound(node))
			{
				return node;
			}	
		}
		
		// couldn't fing any more diffs
		return null;
	}
	
	//	does the element have mixed content, i.e content and child elements
	private static boolean isMixedElementAndContent( XElement element) {
		if ( element.hasMixedContent()) {
			boolean elementFound = false;
			boolean textFound = false;

			for ( int i = 0; i < element.nodeCount(); i++) {
				Node node = element.node( i);
				
				if ( node instanceof XElement) {
					elementFound = true;
				} else if ( node instanceof Text){
					if ( !isWhiteSpace( node)) {
						textFound = true;
					}
				}
				
				if ( textFound && elementFound) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static boolean isWhiteSpace( Node node) {
		return node.getText().trim().length() == 0;
	}
	
	/**
	 * The Statusbar class
	 */
	class Statusbar extends JPanel 
	{
		private JDialog parent = null;
		
		public Statusbar(JDialog _parent,boolean mergeShow) 
		{
			super( new BorderLayout());
			this.parent = _parent;
			
			JPanel xpathPanel = new JPanel( new BorderLayout());
			
			JLabel xpathLabel = new JLabel("XPath:");
			xpathLabel.setFont( xpathLabel.getFont().deriveFont( Font.PLAIN));
			xpathLabel.setForeground( Color.BLACK);
			xpathLabel.setPreferredSize( xpathLabel.getPreferredSize());
			xpathLabel.setHorizontalAlignment( JLabel.CENTER);
			
			xpathField = new JTextField("");
			xpathField.setFocusable(true);
			xpathField.setEditable(true);
			xpathField.setFont(xpathField.getFont().deriveFont( Font.PLAIN));
			xpathField.setForeground(Color.BLACK);
			xpathField.setBorder(BEVEL_BORDER);
			xpathField.setBackground( getBackground());
			xpathField.setPreferredSize( new Dimension( xpathField.getPreferredSize().width, 18));
			
			xpathPanel.setBorder( new EmptyBorder( 0, 2, 0, 0));
			xpathPanel.add(xpathLabel,BorderLayout.WEST);
			xpathPanel.add(xpathField,BorderLayout.CENTER);
			
			JPanel flowPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
			
			JTextField addField = new JTextField(" Added ");
			addField.setFocusable(false);
			addField.setEditable( false);
			addField.setFont(addField.getFont().deriveFont( Font.PLAIN));
			addField.setForeground(COLOR_GREEN);
			addField.setBorder(BEVEL_BORDER);
			addField.setBackground( getBackground());
			addField.setPreferredSize( new Dimension( addField.getPreferredSize().width, 18));
			
			JTextField deleteField = new JTextField(" Deleted ");
			deleteField.setFocusable(false);
			deleteField.setEditable( false);
			deleteField.setFont(deleteField.getFont().deriveFont( Font.PLAIN));
			deleteField.setForeground(Color.RED);
			deleteField.setBorder(BEVEL_BORDER);
			deleteField.setBackground( getBackground());
			deleteField.setPreferredSize( new Dimension( deleteField.getPreferredSize().width, 18));
			
			JTextField changeField = new JTextField(" Changed ");
			changeField.setFocusable(false);
			changeField.setEditable( false);
			changeField.setFont(changeField.getFont().deriveFont( Font.PLAIN));
			changeField.setForeground(Color.BLUE);
			changeField.setBorder(BEVEL_BORDER);
			changeField.setBackground( getBackground());
			changeField.setPreferredSize( new Dimension( changeField.getPreferredSize().width, 18));
			
			JTextField mergeField = null;
			if (mergeShow)
			{
				mergeField = new JTextField(" Merged ");
				mergeField.setFocusable(false);
				mergeField.setEditable( false);
				mergeField.setFont(changeField.getFont().deriveFont( Font.PLAIN));
				mergeField.setForeground(COLOR_MERGED);
				mergeField.setBorder(BEVEL_BORDER);
				mergeField.setBackground( getBackground());
				mergeField.setPreferredSize( new Dimension( changeField.getPreferredSize().width, 18));
			}
			
			flowPanel.add(addField);
			flowPanel.add(deleteField);
			flowPanel.add(changeField);
			if (mergeShow)
			{
				flowPanel.add(mergeField);
			}
			
			this.add( xpathPanel, BorderLayout.CENTER);
			this.add( flowPanel, BorderLayout.EAST);
		}
	}
}
