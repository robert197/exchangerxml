/*
 * $Id: InputView.java,v 1.14 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.bounce.event.PopupListener;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xslt.debugger.BreakpointList;

/**
 * The XSLT debug controller.
 *
 * @version	$Revision: 1.14 $, $Date: 2005/08/26 11:03:41 $
 * @author edankert <edankert@cladonia.com>
 */
public class InputView extends JPanel {
	private static final CompoundBorder NORMAL_BORDER = new CompoundBorder( 
														new CompoundBorder( 
															new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
															new MatteBorder( 0, 0, 1, 1, Color.white)),
														new EmptyBorder( 1, 1, 1, 1));

	private static final CompoundBorder SELECTED_BORDER = new CompoundBorder( 
															new CompoundBorder( 
																new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
																new MatteBorder( 0, 0, 1, 1, Color.white)),
															new MatteBorder( 1, 1, 1, 1, UIManager.getColor( "TabbedPane.focus")));

	private static final boolean DEBUG = false;
	
	private RemoveAllBreakpointsAction removeAllBreakpointsAction = null;
	private EnableAllBreakpointsAction enableAllBreakpointsAction = null;
	private DisableAllBreakpointsAction disableAllBreakpointsAction = null;
	private CloseInputPaneAction closeInputPaneAction = null;
	
	private boolean showFoldingMargin = true;
	private boolean showOverviewMargin = true;
	private boolean showLinenumberMargin = true;
	private boolean wrap = false;
	
	private XSLTDebuggerPane parent = null;

	private JTabbedPane tabs = null;
	private Vector views = null;
	private BreakpointList breakpoints = null;
	private boolean selected = false;
	private JPopupMenu popup = null;

	private ConfigurationProperties props;
	
	/**
	 * Construct a view.
	 */
	public InputView( XSLTDebuggerPane pane, ConfigurationProperties props) {
		super( new BorderLayout());
		
		parent = pane;
		this.props = props;
		setBorder( NORMAL_BORDER);
		
		views = new Vector();
		
		tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setFont( tabs.getFont().deriveFont( Font.PLAIN));
//		tabs.setForeground( UIManager.getColor( "controlShadow"));
		tabs.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent event) {
				setFocus( true);
			}
			public void focusLost( FocusEvent event) {
				setFocus( false);
			}
		});
		
		addMouseListener( tabs, new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				InputView.this.popupTriggered( e);
			}
		});

		add( tabs, BorderLayout.CENTER);

		updatePreferences();
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	private void addMouseListener( Component c, MouseListener listener) {    
	  	c.addMouseListener( listener);

	  	String v = System.getProperty( "java.class.version","44.0");
	  	if ( "49.0".compareTo(v) > 0) { // before jdk 1.5 ...
			if ( c instanceof Container) {      
		  		Component[] comps = ((Container)c).getComponents();
		  		
		  		for ( int i = 0; i < comps.length; i++) {
		  			addMouseListener( comps[i], listener);    
		  		}
			}
	  	}
	}

	public void setSelected( boolean selected) {
		if (DEBUG) System.out.println( "InputView.setSelected( "+selected+")");

		boolean previous = this.selected;
		this.selected = selected;
		

		
		if ( selected) {
			setBorder( SELECTED_BORDER);
			
			if ( getSelectedPane() != null && previous != selected) {
				getSelectedPane().setFocus();
			}

			//GMCG 
			parent.setSelectedView( this);
		} else {
			setBorder( NORMAL_BORDER);
		}
	}
	
	public JFrame getFrame() {
		return parent.getFrame();
	}

	public void setBreakpoints( BreakpointList breakpoints) { 
		this.breakpoints = breakpoints;

		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt( i)).setBreakpoints( breakpoints);
		}
	}
	
	public void setWrapping( boolean wrap) { 
		this.wrap = wrap;
		
		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt( i)).setWrapping( wrap);
		}
	}

	public boolean isWrapping() { 
		return wrap;
	}

	public void setShowLinenumberMargin( boolean visible) { 
		showLinenumberMargin = visible;
		
		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt( i)).setShowLinenumberMargin( visible);
		}
	}

	public boolean showLinenumberMargin() { 
		return showLinenumberMargin;
	}

	public void setShowFoldingMargin( boolean visible) { 
		showFoldingMargin = visible;

		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt( i)).setShowFoldingMargin( visible);
		}
	}

	public boolean showFoldingMargin() { 
		return showFoldingMargin;
	}

	public void setShowOverviewMargin( boolean visible) { 
		showOverviewMargin = visible;

		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt( i)).setShowOverviewMargin( visible);
		}
	}

	public boolean showOverviewMargin() { 
		return showOverviewMargin;
	}

	public void updateBreakpoints() {
		setBreakpoints( breakpoints);
	}

	/**
	 *  Updates the preferences in the underlying views.
	 */
	public void updatePreferences() {
		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt( i)).updatePreferences();
		}
	}
	
	/**
	 * Opens the specified document, if the document has 
	 * already been loaded, it selects the specific tab...
	 * 
	 * @param location the path to the document.
	 */
	public InputPane select( String location) {
		if (DEBUG) System.out.println( "InputView.select( "+location+")");
		
		for ( int i = 0; i < views.size(); i++) {
			InputPane pane = (InputPane)views.elementAt( i);
			
			if ( pane.isCurrentSource( location)) {
				tabs.setSelectedIndex( i);
				return pane;
			}
		}
		
		InputPane pane = null;
		
		try {
			pane = new InputPane( this, breakpoints,props);
			pane.setWrapping( wrap);
			pane.setShowFoldingMargin( showFoldingMargin);
			pane.setShowLinenumberMargin( showLinenumberMargin);
			pane.setShowOverviewMargin( showOverviewMargin);

			pane.open( location);
			
			add( pane);
		} catch ( IOException e) {
			MessageHandler.showError( getFrame(), "Could not open file \""+location+"\".", e, "File Error");
		}
		
		return pane;
	}
	
	public InputPane getPane( String location) {
		for ( int i = 0; i < views.size(); i++) {
			InputPane pane = (InputPane)views.elementAt( i);
			
			if ( pane.isCurrentSource( location)) {
				return pane;
			}
		}

		return null;
	}
	
	
	public void popupTriggered( MouseEvent e) {
		InputPane pane = getSelectedPane();
		
		if ( pane != null) {
			if ( popup == null) {
				popup = new JPopupMenu();
			}
			
			popup.removeAll();
			
			if ( pane.hasBreakpoints()) {
				popup.add( pane.getDisableAllBreakpointsAction());
				popup.add( pane.getEnableAllBreakpointsAction());
				popup.addSeparator();
				popup.add( pane.getRemoveAllBreakpointsAction());
			}
	
			popup.addSeparator();
			popup.add( pane.getCloseInputPaneAction());
	
			GUIUtilities.alignMenu( popup);

			popup.show( (Component)e.getSource(), e.getX(), e.getY());
		}
	}

	public InputPane getSelectedPane() {
		return (InputPane)tabs.getSelectedComponent();
	}

	public void update( InputPane pane) {
		int index = tabs.indexOfComponent( pane);
		
		tabs.setIconAt( index, pane.getIcon());
		parent.breakPointsUpdated( this);
	}
	
	public void setFocus( boolean enabled) {
		int index = tabs.getSelectedIndex();
		
		if ( hasFocus()) {
			//GMCG setSelected( true);
			setSelected( enabled);
		}
	}

	public boolean hasFocus() {
		return hasFocus( getComponents());
	}

	private boolean hasFocus( Component[] components) {
		for ( int i = 0; i < components.length; i++) {
			if ( components[i].isFocusOwner()) {
				return true;
			} else {
				if ( components[i] instanceof Container) {
					if ( hasFocus( ((Container)components[i]).getComponents())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	public void setFocus( InputPane pane, boolean enabled) {
		int index = tabs.indexOfComponent( pane);
		
		setFocus( enabled);
	}

	/**
	 * Adds a view to the list of views.
	 * 
	 * @param pane the new view.
	 */
	protected void add( InputPane pane) {
		if (DEBUG) System.out.println( "InputView.add( "+pane+")");
		tabs.add( pane, pane.getSourceName());
		
		int index = tabs.indexOfComponent( pane);
		
		tabs.setIconAt( index, pane.getIcon());
		tabs.setToolTipTextAt( index, pane.getSourceLocation());
		
		views.addElement( pane);

		tabs.setSelectedComponent( pane);
	}

	/**
	 * Removes a view from the list of views.
	 * 
	 * @param pane the view top remove.
	 */
	protected void remove( InputPane pane) {
		if (DEBUG) System.out.println( "InputView.remove( "+pane+")");
		
		tabs.remove( pane);
		pane.cleanup();
		views.removeElement( pane);

		parent.updateSelectedView();

		if ( views.size() == 0) {
			// Workaround for paint bug!
			tabs.setTabLayoutPolicy( JTabbedPane.WRAP_TAB_LAYOUT);
			tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		}
	}

	/**
	 * Removes all the views.
	 */
	public void removeAllViews() {
		if (DEBUG) System.out.println( "InputView.removeAll()");

		tabs.removeAll();
		
		for ( int i = 0; i < views.size(); i++) {
			((InputPane)views.elementAt(i)).cleanup();
		}
		
		// Workaround for paint bug!
		tabs.setTabLayoutPolicy( JTabbedPane.WRAP_TAB_LAYOUT);
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);

		views.removeAllElements();
		parent.updateSelectedView();
	}

	/**
	 * Removes all the views.
	 */
	public void reload() {
		if (DEBUG) System.out.println( "InputView.removeAll()");

		for ( int i = 0; i < views.size(); i++) {
			try {
				((InputPane)views.elementAt(i)).reload();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Opens the document described by the location if not already 
	 * opened and selects the line.
	 * 
	 * @param location the path to the document.
	 * @param line the line in the document to select.
	 */
	public void selectLine( String location, final int lineNumber) {
		if (DEBUG) System.out.println( "InputView.selectLine( "+location+", "+lineNumber+")");

		if ( location != null) {
			final InputPane pane = select( location);
			
			if ( pane != null) {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						pane.selectLine( lineNumber);
					}
				});
			}
		}
	}

	/**
	 * Opens the document described by the location if not already 
	 * opened and selects the line.
	 * 
	 * @param location the path to the document.
	 * @param line the line in the document to select.
	 */
	public void select( String location, final int lineNumber, final int columnNumber, final String qname, final boolean startTag) {
		if (DEBUG) System.out.println( "InputView.select( "+location+", "+lineNumber+", "+columnNumber+")");

		if ( location != null) {
			final InputPane pane = select( location);

			if ( pane != null) {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						pane.select( lineNumber, columnNumber, qname, startTag);
					}
				});
			}
		}
	}
	
	/**
	 * Returns the list of breakpoints.
	 * 
	 * @return the list of breakpoints
	 */
	public BreakpointList getBreakPoints() {
		return breakpoints;
	}

	public EnableAllBreakpointsAction getEnableAllBreakpointsAction() {
		if ( enableAllBreakpointsAction == null) {
			enableAllBreakpointsAction = new EnableAllBreakpointsAction();
		}
		
		return enableAllBreakpointsAction;
	}
	
	public DisableAllBreakpointsAction getDisableAllBreakpointsAction() {
		if ( disableAllBreakpointsAction == null) {
			disableAllBreakpointsAction = new DisableAllBreakpointsAction();
		}
		
		return disableAllBreakpointsAction;
	}

	public RemoveAllBreakpointsAction getRemoveAllBreakpointsAction() {
		if ( removeAllBreakpointsAction == null) {
			removeAllBreakpointsAction = new RemoveAllBreakpointsAction();
		}
		
		return removeAllBreakpointsAction;
	}

	public CloseInputPaneAction getCloseInputPaneAction() {
		if ( closeInputPaneAction == null) {
			closeInputPaneAction = new CloseInputPaneAction();
		}
		
		return closeInputPaneAction;
	}

	private class EnableAllBreakpointsAction extends AbstractAction {
	 	public EnableAllBreakpointsAction() {
	 		super( "Enable All Breakpoints");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
	 		InputPane pane = getSelectedPane();
	 		
	 		if ( pane != null) {
	 			pane.enableAllBreakpoints();
	 		}
	 	}
	}

	private class DisableAllBreakpointsAction extends AbstractAction {
	 	public DisableAllBreakpointsAction() {
	 		super( "Disable All Breakpoints");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
	 		InputPane pane = getSelectedPane();
	 		
	 		if ( pane != null) {
	 			pane.disableAllBreakpoints();
	 		}
	 	}
	}

	private class RemoveAllBreakpointsAction extends AbstractAction {
	 	public RemoveAllBreakpointsAction() {
	 		super( "Remove All Breakpoints");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
	 		InputPane pane = getSelectedPane();
	 		
	 		if ( pane != null) {
	 			pane.removeAllBreakpoints();
	 		}
	 	}
	}

	private class CloseInputPaneAction extends AbstractAction {
	 	public CloseInputPaneAction() {
	 		super( "Close");
	 	}
	 	
	 	public void actionPerformed( ActionEvent e) {
			InputPane pane = getSelectedPane();

			if ( pane != null) {
				remove( pane);
			}
	 	}
	}

	/**
	 * @return
	 */
	public Vector getViews() {

		return (views);
	}

	/**
	 * 
	 */
	public void updateTabState(InputPane inputPane, boolean modified) {

		if((tabs != null) && tabs.getTabCount() > 0) {
		
			int index = tabs.indexOfComponent( inputPane);
			
			if(index != -1) { 
				if(modified == true) {
					if(tabs.getTitleAt(index).indexOf("*") == -1) {
						tabs.setTitleAt(index, tabs.getTitleAt(index)+"*");
					}
				}
				else {
				
					if(tabs.getTitleAt(index).indexOf("*") != -1) {
						tabs.setTitleAt(index, tabs.getTitleAt(index).substring(0, tabs.getTitleAt(index).length()-1));
					}
				}
			}
		}
	}
}
