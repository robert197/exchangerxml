/*
 * $Id: OutputPanel.java,v 1.13 2005/09/05 13:55:11 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;

import org.xml.sax.SAXParseException;

import com.cladonia.xml.XMLError;
import com.cladonia.xml.editor.Bookmark;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

import com.cladonia.javascript.tools.shell.JSConsole;

/**
 * The panel that shows the different outputs.
 *
 * @version	$Revision: 1.13 $, $Date: 2005/09/05 13:55:11 $
 * @author Dogsbay
 */
public class OutputPanel extends JPanel {
	private static final ImageIcon BOOKMARKS_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Bookmarks16.gif");
	private static final ImageIcon XPATH_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/XPathSearch16.gif");
	private static final ImageIcon ERRORS_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Error16.gif");
	private static final ImageIcon FIND_IN_FILE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/FindInFiles16.gif");
	private static final ImageIcon JSCONSOLE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/JSConsole16.gif");
	//private static final ImageIcon SCHEMATRON_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Error16.gif");

	private ExchangerEditor parent = null;
	private ErrorPane errorPane = null;
	//private SchematronErrorPane schematronErrorPane = null;
	private BookmarkList bookmarkList = null;
	private XPathResults xpathResults = null;
	private JSConsole JSConsole = null;
	private JTabbedPane tabPane = null;
	private FindInFilesResults findInFilesResults = null;
	private boolean locked = false;
	private ConfigurationProperties properties = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public OutputPanel( ExchangerEditor parent, ConfigurationProperties properties) {
		super( new BorderLayout());
		
		this.parent = parent;
		this.properties = properties;
		
		setBorder( new BevelBorder( BevelBorder.LOWERED, Color.white, UIManager.getColor( "control"), UIManager.getColor( "control"), UIManager.getColor( "controlDkShadow")));

		tabPane = new JTabbedPane();
		add( tabPane, BorderLayout.CENTER);
		
		tabPane.addTab( "Errors", ERRORS_ICON, createParseTab());
		tabPane.addTab( "XPath Results", XPATH_ICON, createXPathTab());
		tabPane.addTab( "Bookmarks", BOOKMARKS_ICON, createBookmarksTab());
		tabPane.addTab( "Find in Files", FIND_IN_FILE_ICON, createFindInFilesTab());
		tabPane.addTab( "Scripting", JSCONSOLE_ICON, createJSConsoleTab());
		//tabPane.addTab( "Schematron", SCHEMATRON_ICON, createSchematronTab());
		
/*		
		tabPane.addChangeListener( new ChangeListener() {
			public void stateChanged( ChangeEvent event) {
				//if ( !disabled) {
				///	editor.setView((ExchangerView) tabs.getSelectedComponent());
					int index = tabPane.getSelectedIndex();
					
					if (index == 4) {
					  selectJSConsoleTab();
					
					} else {

					//TODO 
					}
				//}
			}
		});
*/		
		
		setMinimumSize( new Dimension( 0, 30));
	}

	public void setCurrent( Object view) {
		errorPane.setCurrent( view);
		xpathResults.setCurrent( view);
	}
	
	public void startCheck( final String id, final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			errorPane.startCheck( text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
			 		errorPane.startCheck( text);
				}
			});
		}
	}
	
	/*public void startSchematronCheck( final String id, final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			schematronErrorPane.startCheck( text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					schematronErrorPane.startCheck( text);
				}
			});
		}
	}*/

	public void endCheck( final String id, final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			errorPane.endCheck( text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
			 		errorPane.endCheck( text);
				}
			});
		}
	}
	
	/*public void endSchematronCheck( final String id, final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			schematronErrorPane.endCheck( text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					schematronErrorPane.endCheck( text);
				}
			});
		}
	}*/

	public void addError( final String id, final XMLError e) {
		if ( SwingUtilities.isEventDispatchThread()) {
			errorPane.addError( e);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
			 		errorPane.addError( e);
				}
			});
		}
	}
	
	public void addErrorSortedByLineNumber( final String id, final XMLError e) {
		if ( SwingUtilities.isEventDispatchThread()) {
			errorPane.addErrorSortedByLineNumber( e);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
			 		errorPane.addErrorSortedByLineNumber( e);
				}
			});
		}
	}
	
	public void sortErrorListByLineNumber() {
		errorPane.sortErrorsByLineNumber();
	}
	
	/*public void addSchematronError( final String id, final XMLError e) {
		if ( SwingUtilities.isEventDispatchThread()) {
			schematronErrorPane.addError( e);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					schematronErrorPane.addError( e);
				}
			});
		}
	}*/

	public void setErrorList( ErrorList errors) {
		errorPane.setErrorList( errors);
	}

	public void setError(  String id, IOException e) {
		addError( id, new XMLError( e));
	}

	public void selectError(  XMLError error) {
		selectParseTab();
		errorPane.select( error);
	}

	public void setError( String id, SAXParseException e) {
		addError( id, new XMLError( e, XMLError.ERROR));
	}
	public void updatePreferences() {
		errorPane.updatePreferences();
		xpathResults.updatePreferences();
	}

	public void clearErrors() {
		errorPane.clear();
	}

	public void setXPathResults( Vector results) {
		xpathResults.setResults( results);
	}

	public void setXPathList( XPathList results) {
		xpathResults.setXPathList( results);
	}

	public void setLocked( boolean enabled) {
		locked = enabled;
	}

	public boolean isLocked() {
		return locked;
	}

	public void startFindInFiles( String text) {
		findInFilesResults.start( text);
		selectFindInFilesTab();
	}

	public void finishFindInFiles() {
		findInFilesResults.finish();
		selectFindInFilesTab();
	}

	public void addFindInFiles( Vector matches) {
		findInFilesResults.addMatches( matches);
		selectFindInFilesTab();
	}

	public void selectXPathTab() {
		if ( !locked) {
			tabPane.setSelectedIndex( 1);
		}
	}

	public void selectFindInFilesTab() {
		if ( !locked) {
			tabPane.setSelectedIndex( 3);
		}
	}

	public void selectJSConsoleTab() {
		if ( !locked) {
		  //System.out.println("selectJSConsoleTab");
			tabPane.setSelectedIndex( 4);
		}
	}


	public void addBookmark( Bookmark bookmark) {
		bookmarkList.addBookmark( bookmark);
		properties.addBookmark( bookmark);
	}

	public void removeBookmark( Bookmark bookmark) {
		bookmarkList.removeBookmark( bookmark);
		properties.removeBookmark( bookmark);
	}

	public void selectParseTab() {
		if ( !locked) {
			tabPane.setSelectedIndex( 0);
		}
	}

	private JPanel createParseTab() {
		JPanel panel = new JPanel( new BorderLayout());

		errorPane = new ErrorPane( parent);
//		JScrollPane scroller = new JScrollPane( errorPane);
		
		panel.add( errorPane, BorderLayout.CENTER);
		panel.setMinimumSize( new Dimension( 0, 0));
		
		return panel;
	}
	
	/*private JPanel createSchematronTab() {
		JPanel panel = new JPanel( new BorderLayout());

		schematronErrorPane = new SchematronErrorPane( parent);
//		JScrollPane scroller = new JScrollPane( errorPane);
		
		panel.add( schematronErrorPane, BorderLayout.CENTER);
		panel.setMinimumSize( new Dimension( 0, 0));
		
		return panel;
	}*/

	private JPanel createXPathTab() {
		xpathResults = new XPathResults();

		xpathResults.setMinimumSize( new Dimension( 0, 0));

		return xpathResults;
	}

	private JPanel createFindInFilesTab() {
		JPanel panel = new JPanel( new BorderLayout());

		findInFilesResults = new FindInFilesResults( parent);
		JScrollPane scroller = new JScrollPane( findInFilesResults);
		scroller.getViewport().setBackground( findInFilesResults.getBackground());
		
		panel.add( scroller, BorderLayout.CENTER);
		
		panel.setMinimumSize( new Dimension( 0, 0));

		return panel;
	}

	private JPanel createBookmarksTab() {
		bookmarkList = new BookmarkList( parent, properties.getBookmarks());
		bookmarkList.setMinimumSize( new Dimension( 0, 0));

		return bookmarkList;
	}
	
	private JPanel createJSConsoleTab() {
		JSConsole = new JSConsole( parent);
		JSConsole.setMinimumSize( new Dimension( 0, 0));

		return JSConsole;
	}
    /**
     * @return Returns the errorPane.
     */
    public ErrorPane getErrorPane() {

        return errorPane;
    }
    /**
     * @param errorPane The errorPane to set.
     */
    public void setErrorPane(ErrorPane errorPane) {

        this.errorPane = errorPane;
    }
} 
