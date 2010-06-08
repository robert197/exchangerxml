/*
 * $Id: XPathResults.java,v 1.11 2005/03/21 14:35:46 tcurley Exp $
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.bounce.event.DoubleClickListener;
import org.dom4j.Node;

import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows the results for a XPath search.
 *
 * @version	$Revision: 1.11 $, $Date: 2005/03/21 14:35:46 $
 * @author Dogsbay
 */
public class XPathResults extends JPanel {
	private static final ImageIcon ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ElementIcon.gif");
	private static final ImageIcon ATTRIBUTE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/AttributeIcon.gif");
	private static final ImageIcon TEXT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/TextIcon.gif");

	private static final EmptyBorder NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	private JList list = null;
	private Object view = null;
	private Vector nodes = null;
	private XPathListModel model = null;
	private XPathList xpaths = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public XPathResults() {
		super( new BorderLayout());
		
		list = new JList();
		list.setCellRenderer( new XPathCellRenderer());
		JScrollPane scroller = new JScrollPane( list);
		
		add( scroller, BorderLayout.CENTER);
		
		setResults( null);

		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1 && nodes != null) { // A row is selected...
					// perform the selection.
					pathSelected();
				}
			}
		});
		
		updatePreferences();
	}

	public void updatePreferences() {
		list.setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
	}

	public void setCurrent( Object view) {
		this.view = view;
	}

	public void setXPathList( XPathList xpaths) {
		this.xpaths = xpaths;
		
		if ( xpaths != null) {
			nodes = xpaths.getResults();
		} else { 
			nodes = null;
		}
		
		model = new XPathListModel( nodes);
		list.setModel( model);
	}

	public void setResults( Vector results) {
		if ( results != null && results.size() == 0) {
			results = null;
		}
		
		if ( xpaths != null) {
			xpaths.setResults( results);
		}
		nodes = results;
		
		model = new XPathListModel( results);
		list.setModel( model);
	}
	
	private void pathSelected() {
		Object node = model.getElement( list.getSelectedIndex());
		
		if ( view instanceof Viewer) {
			if ( node instanceof XElement) {
				((Viewer)view).setSelectedElement( (XElement)node, false, -1);
			} else if ( node instanceof XAttribute) {
				((Viewer)view).setSelectedElement( (XElement)((XAttribute)node).getParent(), false, -1);
			} else if ( node instanceof Node) {
				((Viewer)view).setSelectedElement( (XElement)((Node)node).getParent(), false, -1);
			}
		} else if ( view instanceof Designer) {
			if ( node instanceof XElement) {
				((Designer)view).setSelectedNode( (XElement)node, -1);
			} else if ( node instanceof XAttribute) {
				((Designer)view).setSelectedNode( (XAttribute)node, -1);
			} else if ( node instanceof Node) {
				((Designer)view).setSelectedNode( (XElement)((Node)node).getParent(), -1);
			}
		} else if ( view instanceof Editor) {
			if ( node instanceof XElement) {
				((Editor)view).selectElement( (XElement)node);
			} else if ( node instanceof XAttribute) {
				((Editor)view).selectAttribute( (XAttribute)node, -1);
			} else if ( node instanceof Node) {
				((Editor)view).selectElement( (XElement)((Node)node).getParent());
			}
		/*} else if ( view instanceof Grid) {
			if ( node instanceof XElement) {
				((Grid)view).selectElement( (XElement)node);
			} else if ( node instanceof XAttribute) {
				((Grid)view).selectAttribute( (XAttribute)node, -1);
			} else if ( node instanceof Node) {
				((Grid)view).selectElement( (XElement)((Node)node).getParent());
			}*/
		} else if ( view instanceof PluginViewPanel) {
			if ( node instanceof XElement) {
				((PluginViewPanel)view).selectElement( (XElement)node);
			} else if ( node instanceof XAttribute) {
				((PluginViewPanel)view).selectAttribute( (XAttribute)node, -1);
			} else if ( node instanceof Node) {
				((PluginViewPanel)view).selectElement( (XElement)((Node)node).getParent());
			}
		}
		
		
		if ( view != null && view instanceof ViewPanel) {
			((ViewPanel)view).setFocus();
		}
	}

	class XPathListModel extends AbstractListModel {
		Vector nodes = null;
		
		public XPathListModel( Vector list) {
			nodes = list;
		}
		
		public int getSize() {
			if ( nodes != null) {
				return nodes.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return nodes.elementAt( i);
//			if ( nodes != null) {
//				String value = null;
//				Object node = nodes.elementAt( i);
//				
//				if ( node instanceof XElement) {
//					XElement e = (XElement)node;
//					
//					value = "Element: "+e.getUniversalName()+" [ "+e.getUniquePath()+" ]";
//				} else if ( node instanceof XAttribute) {
//					XAttribute a = (XAttribute)node;
//					
//					value = "Attribute: "+a.getUniversalName()+" [ "+a.getUniquePath()+" ]";
//				} else if ( node instanceof Node){
//					String text = ((Node)node).getStringValue();
//					
//					if ( text.length() > 21) {
//						text = text.substring( 0, 18)+"...";
//					}
//					value = "Text: "+text+" ["+((Node)node).getUniquePath()+"]";
//				} else if ( node instanceof String){
//					value = (String)node;
//				}
//			
//				return value;
//			} else {
//				return null;
//			}
		}

		public Object getElement( int i) {
			return nodes.elementAt( i);
		}
	}

	public class XPathCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel icon = null;
		private JLabel name = null;
		private JLabel value = null;
		private JLabel text = null;
		private JPanel textPanel = null;

//		private boolean selected = false;
//		private boolean showValue = false;
//		
//		private boolean global = false;
		
		/**
		 * The constructor for the renderer, sets the font type etc...
		 */
		public XPathCellRenderer() {
			super( new FlowLayout( FlowLayout.LEFT, 0, 0));
			
			icon = new JLabel();

			name = new JLabel();
			name.setBorder( new EmptyBorder( 0, 2, 0, 2));
			name.setOpaque( false);
			name.setFont( name.getFont().deriveFont( Font.BOLD));
			name.setForeground( Color.black);
			
			value = new JLabel();
			value.setBorder( new EmptyBorder( 0, 2, 0, 2));
			value.setOpaque( false);
			value.setFont( value.getFont().deriveFont( Font.PLAIN));
			value.setForeground( Color.black);

			text = new JLabel();
			text.setOpaque( false);
			text.setFont( text.getFont().deriveFont( Font.BOLD));
			text.setForeground( Color.black);
			
			textPanel = new JPanel( new BorderLayout());
			textPanel.setOpaque( true);
			textPanel.add( text, BorderLayout.CENTER);

			this.add( icon);
			this.add( name);
			this.add( value);
		}
		
		public void setPreferredFont( Font font) {
			name.setFont( font.deriveFont( Font.BOLD));
			value.setFont( font.deriveFont( Font.PLAIN));
			text.setFont( font.deriveFont( Font.PLAIN));
		}
		
//		public ImageIcon getIcon() {
//			if ( global) {
//				return GLOBAL_ICON;
//			}
//
//			return LOCAL_ICON;
//		}

//		/**
//		 * Returns the icon that is shown when the node is selected.
//		 *
//		 * @return the selected icon.
//		 */
//		public ImageIcon getSelectedIcon() {
//			ImageIcon icon = getIcon();
//
//			if ( icon != null) {
//				icon = ImageUtilities.createDarkerImage( icon);
//			}
//			
//			return icon;
//		}

		public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
			if ( node instanceof XElement) {
				XElement e = (XElement)node;
				
				icon.setIcon( ELEMENT_ICON);
				
				name.setText( e.getUniversalName());
				value.setText( "["+e.getUniquePath()+"]");

//				value = "Element: "+e.getUniversalName()+" [ "+e.getUniquePath()+" ]";
			} else if ( node instanceof XAttribute) {
				XAttribute a = (XAttribute)node;

				icon.setIcon( ATTRIBUTE_ICON);
				
				name.setText( a.getUniversalName());
				value.setText( "["+a.getUniquePath()+"]");
				
//				value = "Attribute: "+a.getUniversalName()+" [ "+a.getUniquePath()+" ]";
			} else if ( node instanceof Node){
				icon.setIcon( TEXT_ICON);
				String text = ((Node)node).getStringValue();
				
				if ( text.length() > 21) {
					text = text.substring( 0, 18)+"...";
				}
				
				name.setText( text);
				value.setText( "["+((Node)node).getUniquePath()+"]");
			} else {
				text.setText( node.toString());

				if ( selected) {
					text.setForeground( list.getSelectionForeground());
					textPanel.setBackground( list.getSelectionBackground());
				} else {
					text.setForeground( list.getForeground());
					textPanel.setBackground( list.getBackground());
				}

				text.setFont( list.getFont());
				text.setEnabled( list.isEnabled());
				textPanel.setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);

				return textPanel;
			}

//			if ( selected) {
//				setForeground( list.getSelectionForeground());
//				setBackground( list.getSelectionBackground());
//			} else {
//				setForeground( list.getForeground());
//				setBackground( list.getBackground());
//			}

			if ( selected) {
				name.setForeground( list.getSelectionForeground());
				value.setForeground( list.getSelectionForeground());
				this.setBackground( list.getSelectionBackground());
			} else {
				name.setForeground( list.getForeground());
				value.setForeground( list.getForeground());
				this.setBackground( list.getBackground());
			}

			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);

			return this;
		}
	} 
} 
