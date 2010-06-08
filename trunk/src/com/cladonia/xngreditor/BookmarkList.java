/*
 * $Id: BookmarkList.java,v 1.5 2004/10/11 08:38:29 edankert Exp $
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
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;

import com.cladonia.xml.editor.Bookmark;
import com.cladonia.xngreditor.component.ScrollableListPanel;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows the results for a XPath search.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/11 08:38:29 $
 * @author Dogsbay
 */
public class BookmarkList extends JPanel {
	private static final ImageIcon BOOKMARK_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Bookmarks16.gif");

	private static final EmptyBorder NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	private JList list = null;
	private Object view = null;
	private BookmarkListModel model = null;
	private ExchangerEditor parent = null;
	private DeleteAction deleteAction = null;
	private GotoAction gotoAction = null;
	private JPopupMenu popup = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public BookmarkList( ExchangerEditor _parent, Vector bookmarks) {
		super( new BorderLayout());
		
		this.parent = _parent;
		
		model = new BookmarkListModel( new Vector( bookmarks));
		list = new JList( model);
		
		list.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				firePopupTriggered( e);
			}
		});
		
		deleteAction = new DeleteAction();
		gotoAction = new GotoAction();
		
		list.getActionMap().put( "deleteAction", deleteAction);
		list.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0, false), "deleteAction");
		
		list.getActionMap().put( "gotoAction", gotoAction);
		list.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "gotoAction");

		list.setCellRenderer( new BookmarkCellRenderer());
		JScrollPane scroller = new JScrollPane( new ScrollableListPanel( list), JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.getViewport().setBackground( list.getBackground());
		
		add( scroller, BorderLayout.CENTER);
		scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1 ) { // A row is selected...
					// perform the selection.
					bookmarkSelected();
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
	
	protected void firePopupTriggered( MouseEvent event) {
		if ( popup == null) {
			popup = new JPopupMenu();
			
			popup.add( gotoAction);
			popup.addSeparator();
			popup.add( deleteAction);
		}

		popup.show( list, event.getX(), event.getY());
	}

	public void addBookmark( Bookmark bookmark) {
//		System.out.println( "BookmarkList.addBookmark( "+bookmark+")");
		model.addBookmark( bookmark);
	}
	
	public void removeBookmark( Bookmark bookmark) {
		model.removeBookmark( bookmark);
	}

	private void bookmarkSelected() {
//		System.out.println( "BookmarkList.bookmarkSelected()");
		final Bookmark bookmark = model.getBookmark( list.getSelectedIndex());

		if ( bookmark.getLineNumber() != -1) {
			parent.getOutputPanel().setLocked( true);

			if ( bookmark.getDocument() != null) {
				parent.select( bookmark.getDocument());

				parent.switchToEditor();
 		 		parent.getView().getEditor().selectLineWithoutEnd( bookmark.getLineNumber()+1);

 		 		parent.getOutputPanel().setLocked(false);
			} else {
				parent.setWait( true);
				parent.setStatus( "Opening ...");
		
				// Run in Thread!!!
				Runnable runner = new Runnable() {
					public void run()  {
		
						try {
							parent.open( new URL( bookmark.getURL()), null, true);
						} catch ( Exception e) {
							e.printStackTrace();
					 	} finally {
	
					 		parent.setStatus( "Done");
					 		parent.setWait( false);
	
					 		SwingUtilities.invokeLater( new Runnable() {
					 			public void run() {
					 		 		parent.switchToEditor();
					 		 		parent.getView().getEditor().selectLineWithoutEnd( bookmark.getLineNumber()+1);
						 			parent.getOutputPanel().setLocked(false);
					 			}
					 		});
					 	}
					}
				};
		
				// Create and start the thread ...
				Thread thread = new Thread( runner);
				thread.start();
			}
		}
	}

	class BookmarkListModel extends AbstractListModel {
		Vector bookmarks = null;
		
		public BookmarkListModel( Vector list) {
			bookmarks = new Vector();
			
			for ( int i = 0; i < list.size(); i++) {
				Bookmark bm = (Bookmark)list.elementAt(i);
				
				// Find out where to insert the bookmark...
				int index = -1;

				for ( int j = 0; j < bookmarks.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( bm.getName().compareToIgnoreCase( ((Bookmark)bookmarks.elementAt(j)).getName()) < 0) {
						index = j;
					} else if ( bm.getName().compareToIgnoreCase( ((Bookmark)bookmarks.elementAt(j)).getName()) == 0) {
						if ( bm.getLineNumber() < ((Bookmark)bookmarks.elementAt(j)).getLineNumber()) {
							index = j;
						}
					}
				}
				
				if ( index != -1) {
					bookmarks.insertElementAt( bm, index);
				} else {
					bookmarks.addElement( bm);
				}
			}
		}
		
		public int getSize() {
			if ( bookmarks != null) {
				return bookmarks.size();
			}
			
			return 0;
		}

		public void removeBookmark( Bookmark bookmark) {
			int index = bookmarks.indexOf( bookmark);
			bookmarks.removeElement( bookmark);

			fireIntervalRemoved( this, index, index);
		}

		public int addBookmark( Bookmark bookmark) {
//			System.out.println( "BookmarkListModel.addBookmark( "+bookmark+")");

			// Find out where to insert the bookmark...
			int index = -1;

			for ( int j = 0; j < bookmarks.size() && index == -1; j++) {
				// Compare alphabeticaly
				if ( bookmark.getName().compareToIgnoreCase( ((Bookmark)bookmarks.elementAt(j)).getName()) < 0) {
					index = j;
				} else if ( bookmark.getName().compareToIgnoreCase( ((Bookmark)bookmarks.elementAt(j)).getName()) == 0) {
					if ( bookmark.getLineNumber() < ((Bookmark)bookmarks.elementAt(j)).getLineNumber()) {
						index = j;
					}
				}
			}
			
			if ( index != -1) {
				bookmarks.insertElementAt( bookmark, index);
			} else {
				index = 0;
				bookmarks.addElement( bookmark);
			}

			fireIntervalAdded( this, index, index);
			
			return index;
		}

		public Object getElementAt( int i) {
			return bookmarks.elementAt( i);
		}

		public Object getElement( int i) {
			return bookmarks.elementAt( i);
		}

		public Bookmark getBookmark( int i) {
			return (Bookmark)bookmarks.elementAt( i);
		}
	}
	
	public class DeleteAction extends AbstractAction {
		public DeleteAction() {
			super( "Delete Bookmark(s)");
		}

		public void actionPerformed( ActionEvent e) {
			int[] indexes = list.getSelectedIndices();
			for ( int i = indexes.length-1; i >= 0; i--) {
				Bookmark bm = model.getBookmark( indexes[i]);
				parent.removeBookmark( bm);
				bm.setRemoved(true);

				ExchangerView view = parent.getView();
				if ( view != null) {
					view.revalidate();
					view.repaint();
				}
			}
		}
	};

	public class GotoAction extends AbstractAction {
		public GotoAction() {
			super( "Goto Bookmark");
		}

		public void actionPerformed( ActionEvent e) {
			bookmarkSelected();
		}
	};
	
	public class BookmarkCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel icon = null;
		private JLabel line = null;
		private JLabel document = null;
		private JLabel content = null;
		private JPanel westPanel = null;

		/**
		 * The constructor for the renderer, sets the font type etc...
		 */
		public BookmarkCellRenderer() {
			super( new BorderLayout());
			
			westPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
			westPanel.setOpaque( false);

			icon = new JLabel();

			line = new JLabel();
			line.setBorder( new EmptyBorder( 0, 2, 0, 2));
			line.setOpaque( false);
			line.setFont( line.getFont().deriveFont( Font.PLAIN));
			line.setForeground( Color.black);
			
			document = new JLabel();
			document.setBorder( new EmptyBorder( 0, 2, 0, 2));
			document.setOpaque( false);
			document.setFont( document.getFont().deriveFont( Font.PLAIN));
			document.setForeground( Color.black);

			content = new JLabel();
			content.setOpaque( false);
			content.setFont( content.getFont().deriveFont( Font.PLAIN));
			content.setForeground( Color.black);
			
			westPanel.add( icon);
			westPanel.add( document);
			westPanel.add( line);
			
			this.add( westPanel, BorderLayout.WEST);
			this.add( content, BorderLayout.CENTER);
		}
		
		public void setPreferredFont( Font font) {
			document.setFont( font.deriveFont( Font.BOLD));
			line.setFont( font.deriveFont( Font.PLAIN));
			content.setFont( font.deriveFont( Font.PLAIN));
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
			if ( node instanceof Bookmark) {
				Bookmark b = (Bookmark)node;
				
				String extension = URLUtilities.getExtension( b.getName());
				
				if ( extension == null) {
					extension = "";
				}

				icon.setIcon( IconFactory.getIconForExtension( extension));
				
				document.setText( b.getName());
				line.setText( "["+(b.getLineNumber()+1)+"]");
				content.setText( b.getContent().trim());
				
				setToolTipText( b.getURL()+" ["+(b.getLineNumber()+1)+"]");
			}

			if ( selected) {
				document.setForeground( list.getSelectionForeground());
				line.setForeground( list.getSelectionForeground());
				content.setForeground( list.getSelectionForeground());
				this.setBackground( list.getSelectionBackground());
			} else {
				document.setForeground( list.getForeground());
				line.setForeground( list.getForeground());
				content.setForeground( list.getForeground());
				this.setBackground( list.getBackground());
			}

			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);

			return this;
		}
	} 
} 
