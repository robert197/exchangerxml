/*
 * $Id: ExchangerTabbedView.java,v 1.12 2004/11/09 10:18:26 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;

import com.cladonia.xngreditor.component.GUIUtilities;

/**
 * This ExchangerTabbedView is used to ...
 *
 * @version $Revision: 1.12 $, $Date: 2004/11/09 10:18:26 $
 * @author Dogsbay
 */
public class ExchangerTabbedView extends JPanel {
	private static final Border UNSELECTED_TABS_BORDER = 
		new CompoundBorder(
			new CompoundBorder(
				new MatteBorder( 1, 1, 0, 0, UIManager.getColor("controlDkShadow")),
				new MatteBorder(0, 0, 1, 1, Color.white)),
			new EmptyBorder( 1, 1, 1, 1));

	private static final Border SELECTED_TABS_BORDER = 
		new CompoundBorder(
			new CompoundBorder(
				new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")),
				new MatteBorder(0, 0, 1, 1, Color.white)),
			new MatteBorder( 1, 1, 1, 1, UIManager.getColor( "TabbedPane.focus")));

	private JTabbedPane tabs = null;
	private ExchangerTabbedView parent = null;
	private PopupListener popupListener = null;
	private DoubleClickListener doubleClickListener = null;
	private MouseAdapter singleClickListener = null;
	private ExchangerEditor editor = null;
	private JPopupMenu tabPopup = null;
	private boolean selected = false;
	private boolean disabled = false;
	
	public ExchangerTabbedView( ExchangerEditor _editor, ExchangerTabbedView _parent) {
		super( new BorderLayout());
		
		this.editor = _editor;
		this.parent = _parent;
		
		setBorder( UNSELECTED_TABS_BORDER);
			
		tabs = new JTabbedPane();
		tabs.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent event) {
				if ( !disabled) {
					editor.setView((ExchangerView) tabs.getSelectedComponent());
					int index = tabs.getSelectedIndex();
					
					if (index != -1) {
						editor.setControllerIcon( tabs.getIconAt( tabs.getSelectedIndex()));
					} else {
						editor.setControllerIcon( null);
					}
				}
			}
		});
		
		tabs.setTabPlacement( JTabbedPane.BOTTOM);
		tabs.setFont( tabs.getFont().deriveFont( Font.PLAIN));
		
		doubleClickListener = new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				editor.toggleFullScreen();

		 		if ( editor.getCurrent() != null) {
		 			editor.getCurrent().setFocus();
		 		}
			}
		};
		
		addMouseListener( tabs, doubleClickListener);
		
		popupListener = new PopupListener() {
			public void popupTriggered(MouseEvent e) {
				getTabPopup().show( (Component)e.getSource(), e.getX(), e.getY());
			}
		};

		addMouseListener( tabs, popupListener);
		
		singleClickListener = new MouseAdapter() {
			public void mouseClicked( MouseEvent e) {
				setSelected( true);
			}
		};

		addMouseListener( tabs, singleClickListener);

		tabs.setRequestFocusEnabled(false);
		
		add( tabs, BorderLayout.CENTER);
	}
	
	public void setScrollTabs( boolean scroll) {
		if ( scroll) {
			tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		} else {
			tabs.setTabLayoutPolicy( JTabbedPane.WRAP_TAB_LAYOUT);
		}
		
		refreshMouseListeners();
	}
	
	private void addMouseListener( Component c, MouseListener listener) {    
		String v = System.getProperty( "java.class.version","44.0");

		if ( "49.0".compareTo(v) <= 0) { // jdk 1.5 or higher...
			c.addMouseListener( listener);
		} else {
			if ( !(c instanceof ExchangerView) && !(c instanceof AbstractButton)) {
			  	c.addMouseListener( listener);
			  	
			  	if ( c instanceof Container) {      
			  		Component[] comps = ((Container)c).getComponents();
			  		
			  		for ( int i = 0; i < comps.length; i++) {
			  			addMouseListener( comps[i], listener);    
			  		}
			  	}
			}
		}
	}
	  
	private void removeMouseListener( Component c, MouseListener listener) {    
		String v = System.getProperty( "java.class.version","44.0");

		if ( "49.0".compareTo(v) <= 0) { // jdk 1.5 or higher...
			c.removeMouseListener( listener);
		} else {
			if ( !(c instanceof ExchangerView) && !(c instanceof AbstractButton)) {
			  	c.removeMouseListener( listener);    
			  	
			  	if ( c instanceof Container) {      
			  		Component[] comps = ((Container)c).getComponents();
			  		
			  		for ( int i = 0; i < comps.length; i++) {
			  			removeMouseListener( comps[i], listener);    
			  		}
			  	}
			}
		}
	}

	private void refreshMouseListeners() {    
		removeMouseListener( tabs, singleClickListener);
		removeMouseListener( tabs, doubleClickListener);
		removeMouseListener( tabs, popupListener);

		addMouseListener( tabs, singleClickListener);
		addMouseListener( tabs, doubleClickListener);
		addMouseListener( tabs, popupListener);
	}

	public void add( ExchangerView view, String name) {
		tabs.add( view, name);

		int index = tabs.indexOfComponent( view);
		URL url = view.getDocument().getURL();
		
		if ( url != null) {
			tabs.setToolTipTextAt( index, URLUtilities.toRelativeString( url));
		} else {
			tabs.setToolTipTextAt( index, name);
		}

		tabs.setRequestFocusEnabled( false);
	}
	
	public ExchangerTabbedView getParentTabbedView() {
		return parent;
	}

	public void setParentTabbedView( ExchangerTabbedView parent) {
		this.parent = parent;
	}

	public void remove( ExchangerView view) {
		tabs.remove( view);

		if ( tabs.getTabCount() > 0) {
			tabs.setRequestFocusEnabled( false);
		} else {
			tabs.setRequestFocusEnabled( true);
			tabs.requestFocus();

			int policy = tabs.getTabLayoutPolicy();
			
			// Workaround for paint bug!
			tabs.setTabLayoutPolicy( JTabbedPane.WRAP_TAB_LAYOUT);
			tabs.setTabLayoutPolicy( policy);
			
			refreshMouseListeners();
		}
	}

	public void setSelected( boolean selected) {
		
//		if ( this.selected != selected) {
		final ExchangerView view = getSelectedView();
		
		if ( view != null) {
//			System.out.println( "["+view.getDocument().getName()+"] ExchangerTabbedView.setSelected( "+selected+"]");
		}

			if ( tabs.getTabCount() > 0) {
				tabs.setRequestFocusEnabled( false);
			} else {
				tabs.setRequestFocusEnabled( true);
				tabs.requestFocus();
			}

			boolean previous = this.selected;
			this.selected = selected;
			
			if ( selected) {
				setBorder( SELECTED_TABS_BORDER);
				editor.setSelected( this);
	
				int index = tabs.getSelectedIndex();
				
				if (index != -1) {
					editor.setControllerIcon( tabs.getIconAt( tabs.getSelectedIndex()));
				} else {
					editor.setControllerIcon( null);
				}
				
				if ( view != null && view.getCurrentView() != null && previous != selected) {
					view.getCurrentView().setFocus();
				}
			} else {
				setBorder( UNSELECTED_TABS_BORDER);
			}
//		}
	}
	
	public void setFocussed() {
		final ExchangerView view = getSelectedView();
		
		if ( view != null) {
//			System.out.println( "["+view.getDocument().getName()+"] ExchangerTabbedView.setFocussed()");
		}

		if ( this.selected == false) {
			this.selected = true;

			setBorder( SELECTED_TABS_BORDER);
			editor.setSelected( this);

			int index = tabs.getSelectedIndex();
			
			if (index != -1) {
				editor.setControllerIcon( tabs.getIconAt( tabs.getSelectedIndex()));
			} else {
				editor.setControllerIcon( null);
			}
		}
	}

	public void select( ExchangerView view) {
		if ( tabs.indexOfComponent( view) != -1) {
			tabs.setSelectedComponent( view);
			setSelected( true);
		}
	}
	
	public ExchangerView getSelectedView() {
		return (ExchangerView)tabs.getSelectedComponent();
	}

	public void setIcon( ExchangerView view, Icon icon) {
		int index = tabs.indexOfComponent( view);

		if ( index != -1) {
			tabs.setIconAt( index, icon);
			editor.setControllerIcon( icon);
		}
	}
	
	public Icon getSelectedIcon() {
		int index = tabs.getSelectedIndex();
		
		if ( index != -1) {
			return tabs.getIconAt(index);
		}

		return null;
	}

	public void setTitle( ExchangerView view, String title) {
		int index = tabs.indexOfComponent( view);
	
		if ( index != -1) {
			URL url = view.getDocument().getURL();
			
			if ( url != null) {
				tabs.setToolTipTextAt( index, URLUtilities.toRelativeString( url));
			} else {
				tabs.setToolTipTextAt( index, title);
			}
			
			tabs.setTitleAt( index, title);
		}
	}

	public Vector getViews() {
		Vector views = new Vector();

		for (int i = 0; i < tabs.getTabCount(); i++) {
			views.addElement( tabs.getComponentAt(i));
		}

		return views;
	}

	public boolean contains( ExchangerView view) {
		return tabs.indexOfComponent( view) != -1;
	}

	public boolean isSelected() {
		return selected;
	}
	
	private JPopupMenu getTabPopup() {
		if (tabPopup == null) {
			tabPopup = new JPopupMenu();
			tabPopup.add( editor.getParseAction());
			tabPopup.add( editor.getValidateAction());
			tabPopup.addSeparator();
			tabPopup.add( editor.getOpenBrowserAction());
			tabPopup.addSeparator();
			tabPopup.add( editor.getReloadAction());
			tabPopup.addSeparator();
			tabPopup.add( editor.getSaveAction());
			tabPopup.add( editor.getSaveAsAction());
			tabPopup.add( editor.getSaveAsRemoteAction());
			tabPopup.addSeparator();
			tabPopup.add( editor.getCloseAction());
			
			GUIUtilities.alignMenu( tabPopup);
		}

		return tabPopup;
	}
	
	public void disableChangeListener( boolean disable) {
		this.disabled = disable;
	}

	public void removeListeners() {
		ChangeListener[] changeListeners = tabs.getChangeListeners();
		
		for ( int i = 0; i < changeListeners.length; i++) {
			tabs.removeChangeListener( changeListeners[i]);
		}

//		MouseListener[] listeners = tabs.getMouseListeners();
//		
//		for ( int i = 0; i < listeners.length; i++) {
//			tabs.removeMouseListener( listeners[i]);
//		}
//		
		removeMouseListener( tabs, singleClickListener);
		removeMouseListener( tabs, doubleClickListener);
		removeMouseListener( tabs, popupListener);
	}
}
