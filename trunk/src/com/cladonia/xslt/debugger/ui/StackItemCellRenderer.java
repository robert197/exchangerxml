/*
 * $Id: StackItemCellRenderer.java,v 1.5 2004/05/31 17:52:40 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.tree.TreeCellRenderer;

import org.bounce.image.ImageUtilities;

import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xslt.debugger.DefaultStackItem;
import com.cladonia.xslt.debugger.XSLTemplateStackItem;

/**
 * The cell renderer component for an ElementNode.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/05/31 17:52:40 $
 * @author Dogsbay
 */
public class StackItemCellRenderer extends JPanel implements ListCellRenderer, TreeCellRenderer {
	private static final Border SELECTED_BORDER = new LineBorder( UIManager.getColor( "controlShadow"), 1);
	private static final Border UNSELECTED_BORDER = new EmptyBorder( 1, 1, 1, 1);

	private static final boolean DEBUG = false;
	
	private static final ImageIcon INPUT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Input11.gif");
	private static final ImageIcon OUTPUT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Output11.gif");
	private static final ImageIcon XSLT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/Transformation11.gif");

	private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);

	private JPanel matchPanel = null;
	private JPanel selectablePanel = null;

	private JLabel icon = null;

	private JLabel name = null;

	private AttributeRenderer templateMatch = null;
	private AttributeRenderer templateName = null;
	private AttributeRenderer templateMode = null;
	private AttributeRenderer templatePriority = null;

	private JLabel fileName = null;

	private boolean showValue = false;
	
	/**
	 * The constructor for the renderer, sets the font type etc...
	 */
	public StackItemCellRenderer() {
		super( new FlowLayout( FlowLayout.LEFT, 0, 0));
		
		icon = new JLabel();
		icon.setBorder( new EmptyBorder( 2, 0, 0, 0));

		templateMatch = new AttributeRenderer( "match");
		templateName = new AttributeRenderer( "name");
		templateMode = new AttributeRenderer( "mode");
		templatePriority = new AttributeRenderer( "priority");

		name = new JLabel();
		name.setBorder( new EmptyBorder( 0, 2, 0, 2));
		name.setOpaque( false);
		name.setFont( name.getFont().deriveFont( Font.BOLD));
		name.setForeground( Color.black);
		
		fileName = new JLabel();
		fileName.setBorder( new EmptyBorder( 0, 2, 0, 2));
		fileName.setOpaque( false);
		fileName.setFont( fileName.getFont().deriveFont( Font.PLAIN));
		fileName.setForeground( Color.gray);

		selectablePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0 , 0));
		selectablePanel.setOpaque( false);
		selectablePanel.add( name);
		selectablePanel.add( templateMatch);
		selectablePanel.add( templateName);
		selectablePanel.add( templateMode);
		selectablePanel.add( templatePriority);
		selectablePanel.add( fileName);

		this.add( icon);
		this.add( selectablePanel);
	}
	
	public void setPreferredFont( Font font) {
		name.setFont( font.deriveFont( Font.BOLD));
		fileName.setFont( font.deriveFont( Font.PLAIN));
	}
	
	public ImageIcon getIcon( int type) {
		if ( type == 1 || type == 2) {
			return XSLT_ICON;
		} else if ( type == 3 || type == 4) {
			return INPUT_ICON;
		} else { // if ( type == 5 || || type == 6) {
			return OUTPUT_ICON;
		}
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public ImageIcon getSelectedIcon( int type) {
		ImageIcon icon = getIcon( type);

		if ( icon != null) {
			icon = ImageUtilities.createDarkerImage( icon);
		}
		
		return icon;
	}

	public void setStackItem( Object object, boolean selected) {
		if ( object instanceof XSLTemplateStackItem) {
			XSLTemplateStackItem item = (XSLTemplateStackItem)object;
			
			name.setText( "xsl:template");
			
			String tempName = item.get_name();
			
			if ( tempName != null && tempName.trim().length() > 0) {
				templateName.setVisible( true);
				templateName.setValue( tempName);
			} else {
				templateName.setVisible( false);
			}
			
			String tempMatch = item.get_match();

			if ( tempMatch != null && tempMatch.trim().length() > 0) {
				templateMatch.setVisible( true);
				templateMatch.setValue( tempMatch);
			} else {
				templateMatch.setVisible( false);
			}

			String tempMode = item.get_mode();

			if ( tempMode != null && tempMode.trim().length() > 0) {
				templateMode.setVisible( true);
				templateMode.setValue( tempMode);
			} else {
				templateMode.setVisible( false);
			}

			String tempPriority = item.get_priority();

			if ( tempPriority != null && tempPriority.trim().length() > 0) {
				templatePriority.setVisible( true);
				templatePriority.setValue( tempPriority);
			} else {
				templatePriority.setVisible( false);
			}

			setFileName( item.get_filename());

			if ( selected) {
				icon.setIcon( getSelectedIcon( 1));
			} else {
				icon.setIcon( getIcon( 1));
			}
		} else if ( object instanceof DefaultStackItem) {
			templateName.setVisible( false);
			templateMatch.setVisible( false);
			templatePriority.setVisible( false);
			templateMode.setVisible( false);

			DefaultStackItem item = (DefaultStackItem)object;
			
			name.setText( item.get_name());
			setFileName( item.get_filename());

			if ( selected) {
				icon.setIcon( getSelectedIcon( item.get_type()));
			} else {
				icon.setIcon( getIcon( item.get_type()));
			}
		}
	}

	public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
		
		setStackItem( node, selected);

		if ( selected) {
			name.setForeground( list.getSelectionForeground());
			templateMatch.setForeground( list.getSelectionForeground());
			templateName.setForeground( list.getSelectionForeground());
			templatePriority.setForeground( list.getSelectionForeground());
			templateMode.setForeground( list.getSelectionForeground());

			setBackground( list.getSelectionBackground());
		} else {
			name.setForeground( list.getForeground());
			templateMatch.setForeground( list.getForeground());
			templateName.setForeground( list.getForeground());
			templatePriority.setForeground( list.getForeground());
			templateMode.setForeground( list.getForeground());

			setBackground( list.getBackground());
		}
	
		setEnabled( list.isEnabled());
		setPreferredFont( list.getFont());
		setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

		return this;
	}
	
	private void setFileName( String name) {
		fileName.setText( "["+URLUtilities.getFileName( name)+"]");
	}

	public Component getTreeCellRendererComponent( JTree tree, Object node, boolean selected, boolean expanded, boolean leaf, int row, boolean focus) {
		setOpaque( false);
		if ( node instanceof TraceNode) {
			setStackItem( ((TraceNode)node).getStackItem(), selected);
		} else {
			templatePriority.setVisible( false);
			templateMode.setVisible( false);
			templateName.setVisible( false);
			templateMatch.setVisible( false);
		}

		if ( selected) {
			selectablePanel.setBorder( SELECTED_BORDER);
		} else {
			selectablePanel.setBorder( UNSELECTED_BORDER);
		}
	
		return this;
	}

	private class AttributeRenderer extends JPanel {
		private JLabel name = null;
		private JLabel equals = null;
		private JLabel value = null;
		
		public AttributeRenderer( String text) {
			super( new FlowLayout( FlowLayout.LEFT, 0, 0));
			
			setOpaque( false);
			
			equals = new JLabel("=");
			equals.setBorder( new EmptyBorder( 0, 0, 0, 0));
			equals.setOpaque( false);
			equals.setFont( equals.getFont().deriveFont( Font.PLAIN));
			equals.setVisible( true);

			name = new JLabel( text);
			name.setBorder( new EmptyBorder( 0, 2, 0, 2));
			name.setOpaque( false);
			name.setFont( name.getFont().deriveFont( Font.BOLD));
			name.setForeground( Color.red);
			
			value = new JLabel();
			value.setBorder( new EmptyBorder( 0, 2, 0, 2));
			value.setOpaque( false);
			value.setFont( value.getFont().deriveFont( Font.PLAIN));
			value.setForeground( Color.black);

			this.add( name);
			this.add( equals);
			this.add( value);
		}
		
		public void setForeground( Color color) {
			if ( name != null) {
				name.setForeground( color);
				equals.setForeground( color);
				value.setForeground( color);
			}
		}

		public void setValue( String text) {
			value.setText( "\""+text+"\"");
		}
	}
} 
