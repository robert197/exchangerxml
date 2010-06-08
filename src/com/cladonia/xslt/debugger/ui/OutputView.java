/*
 * $Id: OutputView.java,v 1.6 2005/08/26 11:03:41 tcurley Exp $
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;

import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The XSLT debug controller.
 *
 * @version	$Revision: 1.6 $, $Date: 2005/08/26 11:03:41 $
 * @author edankert <edankert@cladonia.com>
 */
public class OutputView extends JPanel {
	private static final CompoundBorder NORMAL_BORDER = new CompoundBorder( 
									new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
									new MatteBorder( 0, 0, 1, 1, Color.white));

	//	private static final CompoundBorder NORMAL_BORDER = new CompoundBorder( 
//														new CompoundBorder( 
//															new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
//															new MatteBorder( 0, 0, 1, 1, Color.white)),
//														new EmptyBorder( 1, 1, 1, 1));
//
	private static final CompoundBorder SELECTED_BORDER = new CompoundBorder( 
															new CompoundBorder( 
																new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
																new MatteBorder( 0, 0, 1, 1, Color.white)),
															new MatteBorder( 1, 1, 1, 1, UIManager.getColor( "TabbedPane.focus")));

	private static final boolean DEBUG = false;
	
	private XSLTDebuggerPane parent = null;

	private JTabbedPane tabs = null;
	private Vector views = null;
	private boolean selected = false;
	private OutputStreamMultiplexer outputStream = null;
	private OutputPane outputPane = null;
	private MessagePane messagePane = null;

	private boolean showLinenumberMargin = true;
	private boolean wrap = false;

	private ConfigurationProperties props;
	
	/**
	 * Construct a view.
	 */
	public OutputView( XSLTDebuggerPane pane, ConfigurationProperties props) {
		super( new BorderLayout());
		
		parent = pane;
		this.props = props;
		
		setBorder( NORMAL_BORDER);
		
		views = new Vector();
		
		tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.setFont( tabs.getFont().deriveFont( Font.PLAIN));

		tabs.addFocusListener( new FocusListener() {
			public void focusGained( FocusEvent event) {
				setFocus( true);
			}
			public void focusLost( FocusEvent event) {
				setFocus( false);
			}
		});
		
		
		outputPane = new OutputPane( this, "Output", props);
		outputPane.getEditor().addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				setFocus( true);
				
			}

			public void focusLost(FocusEvent e) {

				setFocus( false);
				
			}
			
		});

		tabs.add( outputPane, "Output");
		
		messagePane = new MessagePane(props);
		
		
		tabs.add( messagePane, "Messages");

		outputStream = new OutputStreamMultiplexer( outputPane.getOutputStream());

		add( tabs, BorderLayout.CENTER);

		updatePreferences();
	}
	
	public void setWrapping( boolean wrap) { 
		this.wrap = wrap;

		outputPane.setWrapping( wrap);
		
		for ( int i = 0; i < views.size(); i++) {
			((OutputPane)views.elementAt( i)).setWrapping( wrap);
		}
	}

		
	public boolean isWrapping() { 
		return wrap;
	}

	public void setShowLinenumberMargin( boolean visible) { 
		showLinenumberMargin = visible;
		
		outputPane.setShowLinenumberMargin( visible);

		for ( int i = 0; i < views.size(); i++) {
			((OutputPane)views.elementAt( i)).setShowLinenumberMargin( visible);
		}
	}

	public boolean showLinenumberMargin() { 
		return showLinenumberMargin;
	}

	public boolean isSelected() {
		return selected;
	}
	
	public OutputStream getOutputStream() {
		return outputStream;
	}

	public OutputStream getMessageStream() {
		return messagePane.getOutputStream();
	}

	public void setSelected( boolean selected) {
		if (DEBUG) System.out.println( "OutputView.setSelected( "+selected+")");

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
	
	
	
	/*public void setSelected( boolean selected) {
		this.selected = selected;
		
		if ( selected) {
//			setBorder( SELECTED_BORDER);
			setBorder( NORMAL_BORDER);
//			parent.setSelectedView( this);
		} else {
			setBorder( NORMAL_BORDER);
		}
	}*/

	/**
	 *  Updates the preferences in the underlying views.
	 */
	public void updatePreferences() {
		outputPane.updatePreferences();

		for ( int i = 0; i < views.size(); i++) {
			((OutputPane)views.elementAt( i)).updatePreferences();
		}
	}
	
	/**
	 * Opens the specified document, if the document has 
	 * already been loaded, it selects the specific tab...
	 * 
	 * @param location the path to the document.
	 */
	public OutputPane select( String location) {
		if (DEBUG) System.out.println( "InputView.select( "+location+")");
		
		for ( int i = 0; i < views.size(); i++) {
			OutputPane pane = (OutputPane)views.elementAt( i);
			
			if ( pane.isCurrentSource( location)) {
				tabs.setSelectedIndex( i);
				
				outputStream.open( pane.getOutputStream());
				return pane;
			}
		}
		
		OutputPane pane = new OutputPane( this, location, props);
		pane.setWrapping( wrap);
		pane.setShowLinenumberMargin( showLinenumberMargin);
		outputStream.open( pane.getOutputStream());

		add( pane);
		
		return pane;
	}
	
	public OutputPane getSelectedPane() {
		if(tabs.getSelectedComponent() instanceof OutputPane) {
			return (OutputPane)tabs.getSelectedComponent();
		}
		else {
			return(null);
		}
	}
	
	public MessagePane getSelectedMessagePane() {
		if(tabs.getSelectedComponent() instanceof MessagePane) {
			return (MessagePane)tabs.getSelectedComponent();
		}
		else {
			return(null);
		}
	}
	
	public void close() {
		outputStream.closeCurrent();
	}

	public void update( OutputPane pane) {
		int index = tabs.indexOfComponent( pane);
		
		tabs.setIconAt( index, pane.getIcon());
	}
	
	public void setFocus( boolean enabled) {
		int index = tabs.getSelectedIndex();
		
		if ( hasFocus()) {
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

	public void setFocus( OutputPane pane, boolean enabled) {
		int index = tabs.indexOfComponent( pane);
		
		setFocus( enabled);
	}

	/**
	 * Adds a view to the list of views.
	 * 
	 * @param pane the new view.
	 */
	public void select( OutputPane pane) {
		tabs.setSelectedComponent( pane);
	}

	/**
	 * Adds a view to the list of views.
	 * 
	 * @param pane the new view.
	 */
	protected void add( OutputPane pane) {
		if (DEBUG) System.out.println( "InputView.add( "+pane+")");
		int messageIndex = tabs.indexOfComponent( messagePane);

		tabs.add( pane, pane.getSourceName(), messageIndex);
		
		int index = tabs.indexOfComponent( pane);
		
		tabs.setIconAt( index, pane.getIcon());
		
		views.addElement( pane);

		tabs.setSelectedComponent( pane);
	}

	/**
	 * Removes all the views.
	 */
	public void removeAllViews() {
		if (DEBUG) System.out.println( "InputView.removeAll()");
		
		outputPane.reset();
		messagePane.reset();
		
		for ( int i = 0; i < tabs.getTabCount(); i++) {
			Component comp = tabs.getComponentAt( i);
			
			if ( comp != messagePane && comp != outputPane) {
				tabs.removeTabAt( i);

				if ( comp instanceof OutputPane) {
					((OutputPane)comp).cleanup();
				}
				views.removeElement( comp);
				i--;
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
	public void select( String location, final int lineNumber, final int columnNumber, final boolean startTag) {
		if (DEBUG) System.out.println( "InputView.select( "+location+", "+lineNumber+", "+columnNumber+")");
		final OutputPane pane = select( location);
		
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				pane.select( lineNumber, columnNumber, startTag);
			}
		});
	}
}
