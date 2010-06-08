/*
 * $Id: FindInFilesResults.java,v 1.5 2004/10/11 08:39:25 edankert Exp $
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.bounce.event.DoubleClickListener;

import com.cladonia.xngreditor.project.Match;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows the results for a XPath search.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/10/11 08:39:25 $
 * @author Dogsbay
 */
public class FindInFilesResults extends JPanel implements Scrollable {
	private static final EmptyBorder NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

	private JLabel startLabel = null;
	private JLabel endLabel = null;
	private JList list = null;
	private FindInFilesListModel model = null;
	private ExchangerEditor parent = null;

	/**
	 * The constructor for the about dialog.
	 *
	 * @param frame the parent frame.
	 */
	public FindInFilesResults( ExchangerEditor parent) {
		super( new BorderLayout());
		
		this.parent = parent;
		
		Font baseFont = TextPreferences.getBaseFont().deriveFont( (float)12);
		
		startLabel = new JLabel();
		startLabel.setFont( baseFont);
		add( startLabel, BorderLayout.NORTH);
		list = new JList();
		list.setFont( baseFont);
		list.setCellRenderer( new MatchCellRenderer());

		add( list, BorderLayout.CENTER);
		endLabel = new JLabel();
		endLabel.setFont( baseFont);
		add( endLabel, BorderLayout.SOUTH);
		
		setBackground( list.getBackground());
		endLabel.setBackground( list.getBackground());
		startLabel.setBackground( list.getBackground());

		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					// perform the selection.
					matchSelected();
				}
			}
		});
	}

	public void updatePreferences() {
		Font baseFont = TextPreferences.getBaseFont().deriveFont( (float)12);
		
		list.setFont( baseFont);
		startLabel.setFont( baseFont);
		endLabel.setFont( baseFont);
	}

//	public void setCurrent( Object view) {
//		this.view = view;
//	}

	public void addMatches( Vector results) {
		model.addMatches( results);
	}

	public void start( String text) {
		startLabel.setText( text);
		endLabel.setText( "");

		model = new FindInFilesListModel();
		list.setModel( model);
	}
	
	public void finish() {
		endLabel.setText( "Done");
	}

	public Dimension getPreferredScrollableViewportSize() {
	    return getPreferredSize();
	}

	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}

	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction) {
		return 20;
	}  

	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
	public void cleanup() {
		removeAll();
		
		list = null;
	}

	private void matchSelected() {
		final Match match = model.getMatch( list.getSelectedIndex());
		if ( match.getLineNumber() != -1) {
			parent.getOutputPanel().setLocked( true);

			parent.setWait( true);
			parent.setStatus( "Opening ...");
	
			// Run in Thread!!!
			Runnable runner = new Runnable() {
				public void run()  {
	
					try {
						parent.open( match.getURL(), null, true);
				 	} finally {

				 		parent.setStatus( "Done");
				 		parent.setWait( false);

				 		SwingUtilities.invokeLater( new Runnable() {
				 			public void run() {
				 		 		parent.switchToEditor();
				 		 		parent.getView().getEditor().select( match.getLineNumber(), match.getStart(), match.getEnd());
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

	class FindInFilesListModel extends AbstractListModel {
		Vector nodes = null;
		
		public FindInFilesListModel() {
			nodes = new Vector();
		}
		
		public void addMatches( Vector list) {
			if ( list.size() > 0) {
				int min = nodes.size();

				for ( int i = 0; i < list.size(); i++) {
					nodes.addElement( list.elementAt(i));
				}
				
				fireIntervalAdded( this, min, nodes.size()-1);
			}
		}
		
		public int getSize() {
			if ( nodes != null) {
				return nodes.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return nodes.elementAt(i);
		}

		public Match getMatch( int i) {
			return (Match)nodes.elementAt( i);
		}
	}
	
	public class MatchCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel icon = null;
		private JLabel line = null;
		private JLabel document = null;
		private JLabel contentBefore = null;
		private JLabel contentAfter = null;
		private JLabel contentSelected = null;
		private JPanel westPanel = null;

		/**
		 * The constructor for the renderer, sets the font type etc...
		 */
		public MatchCellRenderer() {
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

			JPanel content = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
			content.setOpaque( false);
			
			contentBefore = new JLabel();
			contentBefore.setOpaque( false);
			contentBefore.setFont( content.getFont().deriveFont( Font.PLAIN));
			contentBefore.setForeground( Color.black);
			
			contentAfter = new JLabel();
			contentAfter.setOpaque( false);
			contentAfter.setFont( content.getFont().deriveFont( Font.PLAIN));
			contentAfter.setForeground( Color.black);

			contentSelected = new JLabel();
			contentSelected.setOpaque( false);
			contentSelected.setFont( content.getFont().deriveFont( Font.BOLD));
			contentSelected.setForeground( Color.black);
			
			content.add( contentBefore);
			content.add( contentSelected);
			content.add( contentAfter);

			westPanel.add( icon);
			westPanel.add( document);
			westPanel.add( line);
			
			this.add( westPanel, BorderLayout.WEST);
			this.add( content, BorderLayout.CENTER);
		}
		
		public void setPreferredFont( Font font) {
			document.setFont( font.deriveFont( Font.BOLD));
			line.setFont( font.deriveFont( Font.PLAIN));
			contentBefore.setFont( font.deriveFont( Font.PLAIN));
			contentAfter.setFont( font.deriveFont( Font.PLAIN));
			contentSelected.setFont( font.deriveFont( Font.BOLD));
		}
		
		public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
			if ( node instanceof Match) {
				Match m = (Match)node;
				
				String name = URLUtilities.getFileName( m.getURL());
				String extension = URLUtilities.getExtension( name);
				
				if ( extension == null) {
					extension = "";
				}

				icon.setIcon( IconFactory.getIconForExtension( extension));
				
				document.setText( name);
				line.setText( "["+(m.getLineNumber())+","+(m.getStart()+1)+"]");
				
				if ( m.getLineNumber() != -1) {
					contentBefore.setText( m.getLineValue().substring( 0, m.getStart()));
					contentSelected.setText( m.getLineValue().substring( m.getStart(), m.getEnd()));
					contentAfter.setText( m.getLineValue().substring( m.getEnd()));
				} else {
					contentBefore.setText( m.getLineValue());
					contentSelected.setText( "");
					contentAfter.setText( "");
				}

				setToolTipText( m.getURL()+" ["+(m.getLineNumber())+","+(m.getStart()+1)+"]");
			}

			if ( selected) {
				document.setForeground( list.getSelectionForeground());
				line.setForeground( list.getSelectionForeground());
				contentBefore.setForeground( list.getSelectionForeground());
				contentAfter.setForeground( list.getSelectionForeground());
				contentSelected.setForeground( list.getSelectionForeground());
				this.setBackground( list.getSelectionBackground());
			} else {
				document.setForeground( list.getForeground());
				line.setForeground( list.getForeground());
				contentBefore.setForeground( list.getForeground());
				contentSelected.setForeground( list.getForeground());
				contentAfter.setForeground( list.getForeground());
				this.setBackground( list.getBackground());
			}

			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);

			return this;
		}
	} 
} 
