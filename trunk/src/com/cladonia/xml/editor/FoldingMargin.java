/*
 * $Id: FoldingMargin.java,v 1.8 2004/10/21 09:32:01 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventListener;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
//import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import org.bounce.event.DoubleClickListener;
import org.bounce.image.ImageUtilities;

import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The folding margin component for the editor.
 *
 * @version	$Revision: 1.8 $, $Date: 2004/10/21 09:32:01 $
 * @author Dogsbay
 */
public class FoldingMargin extends JComponent {
	// Set right/left margin
	private static final ImageIcon FOLDED_ICON = ImageUtilities.createSilhouetteImage( XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Folded.gif"), UIManager.getColor( "textText"));
	private static final ImageIcon UNFOLDED_ICON = ImageUtilities.createSilhouetteImage( XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Unfolded.gif"), UIManager.getColor( "textText"));

	private final static int LEFT_MARGIN = 1;
	private final static int RIGHT_MARGIN = 2;

	private Vector folds	= null;

	// heights and widths
	private int lineHeight	= 16;
	private int topMargin	= 3;
//	private int width		= 50;

	// Metrics of this LineNumber component
	private FontMetrics fontMetrics = null;
	private XmlEditorPane editor = null;
	private FoldingManager manager = null;

	private boolean changed = false;
	private int lines = 0;

	/**
	 * Convenience constructor for Text Components
	 */
	public FoldingMargin( FoldingManager _manager, XmlEditorPane _editor) {
		this.editor = _editor;
		this.manager = _manager;
		
		folds = new Vector();
		
		setBackground( UIManager.getColor( "control")); //editor.getBackground());
		setForeground( UIManager.getColor( "textText"));

		setFont( editor.getFont());
		
		Insets insets = editor.getMargin();
		
		if ( insets != null) {
			topMargin = insets.top;
		} else {
			topMargin = 0;
		}
		
		editor.addCaretListener( new CaretListener() {
			public void caretUpdate( CaretEvent evt) {
				if ( FoldingMargin.this.editor.getLines() != lines) {
					revalidate();
					repaint();
					
					lines = FoldingMargin.this.editor.getLines();
				}
			}
		});
		
		this.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
//				FoldingMargin.this.editor.selectLineForOffset( e.getY());
			}
		});
		
	
		if ( manager != null) {
			this.addMouseListener( new MouseAdapter() {
				public void mouseClicked( MouseEvent e) {
					manager.setFocus(); 

					if ( e.isShiftDown()) {
					 	try {
							int line = FoldingMargin.this.editor.getLineNumber( e.getY());
	
							toggleFullFold( line);
						} catch ( BadLocationException x) {
							x.printStackTrace();
						}
					} else {
					 	try {
							int line = FoldingMargin.this.editor.getLineNumber( e.getY());
	
							toggleFold( line);
						} catch ( BadLocationException x) {
							x.printStackTrace();
						}
					}
				}
			});
		}
	}
	
	public boolean isChanged() {
		boolean result = changed;
		changed = false;

		return result;
	}
	
	public void foldAll() {
		XmlDocument doc = (XmlDocument)editor.getDocument();
		Element root = doc.getDefaultRootElement();
		int count = root.getElementCount();
		
		try {
			cleanupFolds();
			
			for ( int i = 0; i < count; i++) {
				if ( manager.isMultipleLineTagStart( i)) {
					Element element = root.getElement( i);
					Tag tag = doc.getCurrentTag( element.getStartOffset());
					int end = -1;
					
					if ( tag == null) {
						tag = doc.getNextTag( element.getStartOffset());
					}
		
					if ( tag.getType() == Tag.START_TAG) {
						int start = tag.getStart();
						tag = doc.getEndTag( tag);
						
						if ( tag.getStart() < start) {
							return;
						}
						
						end = root.getElementIndex( tag.getStart());
						
						if ( end - i > 1) {
							Fold fold = new Fold( root.getElement( i), root.getElement( end), true);
							folds.addElement( fold);
						}
						
						i = end;
					}
				}
			}
			
			int caret = editor.getCaretPosition();
			int index = root.getElementIndex( editor.getCaretPosition());
			
			if ( isFolded( index)) {
				Fold fold = (Fold)folds.elementAt(0);
				Element element = root.getElement( fold.getStart());
				editor.setCaretPosition( element.getEndOffset()-1);
			}
			
		} catch ( BadLocationException e) {
			e.printStackTrace();
		}
		
		changed = true;

//		SwingUtilities.invokeLater( new Runnable() {
//			public void run() {
				editor.revalidate();
				editor.repaint();
				
				manager.updateMargins();
		
				manager.revalidate();
				manager.repaint();
//			}
//		});
	}

	public void unfoldAll() {
		cleanupFolds();
		
		changed = true;

//		SwingUtilities.invokeLater( new Runnable() {
//			public void run() {
				editor.revalidate();
				editor.repaint();
				
				manager.updateMargins();
		
				manager.revalidate();
				manager.repaint();
//			}
//		});
	}

	public void toggleFold( int line) {
		try {
			if ( isFolded( line+1)) {
				unfold( line+1);
			} else if ( manager.isMultipleLineTagStart( line)) {
				XmlDocument doc = (XmlDocument)editor.getDocument();
				Element element = doc.getDefaultRootElement().getElement( line);
				Tag tag = doc.getCurrentTag( element.getStartOffset());
				int end = -1;
				
				if ( tag == null) {
					tag = doc.getNextTag( element.getStartOffset());
				}
	
				if ( tag.getType() == Tag.START_TAG) {
					int start = tag.getStart();
					tag = doc.getEndTag( tag);
					
					if ( tag.getStart() < start) {
						return;
					}
	
					end = doc.getDefaultRootElement().getElementIndex( tag.getStart());

					if ( end - line > 1) {
						fold( doc.getDefaultRootElement().getElement( line), doc.getDefaultRootElement().getElement( end), false);
					}
				}
			}
		} catch (BadLocationException e) {}
	}

	public void toggleFullFold( int line) {
		try {
			if ( isFolded( line+1)) {
				unfoldFully( line+1);
			} else if ( manager.isMultipleLineTagStart( line)) {
				XmlDocument doc = (XmlDocument)editor.getDocument();
				Element element = doc.getDefaultRootElement().getElement( line);
				Tag tag = doc.getCurrentTag( element.getStartOffset());
				int end = -1;
				
				if ( tag == null) {
					tag = doc.getNextTag( element.getStartOffset());
				}
	
				if ( tag.getType() == Tag.START_TAG) {
					int start = tag.getStart();
					tag = doc.getEndTag( tag);
					
					if ( tag.getStart() < start) {
						return;
					}
	
					end = doc.getDefaultRootElement().getElementIndex( tag.getStart());

					if ( end - line > 1) {
						fold( doc.getDefaultRootElement().getElement( line), doc.getDefaultRootElement().getElement( end), true);
					}
				}
			}
		} catch (BadLocationException e) {}
	}

	public void setVisible( boolean visible) {
		if ( visible != isVisible()) {
			super.setVisible( visible);
			cleanupFolds();
		}
	}
	
	public void cleanupFolds() {
		for ( int i = 0; i < folds.size(); i++) {
			Fold f = (Fold)folds.elementAt(i);

			f.cleanup();
		}
		
		folds.removeAllElements();
	}

	public Dimension getPreferredSize() {
    	if ( isVisible()) {
			Dimension size = super.getPreferredSize();
			
			return new Dimension( LEFT_MARGIN + FOLDED_ICON.getIconWidth() + RIGHT_MARGIN, editor.getPreferredSize().height);		
		} else {
			return null;
		}
	}

	public Dimension getMaximumSize() {
    	if ( isVisible()) {
			Dimension size = super.getMaximumSize();
			
			return new Dimension( LEFT_MARGIN + FOLDED_ICON.getIconWidth() + RIGHT_MARGIN, editor.getPreferredSize().height);		
		} else {
			return null;
		}
	}
	
	public Dimension getMinimumSize() {
    	if ( isVisible()) {
			Dimension size = super.getMinimumSize();
			
			return new Dimension( LEFT_MARGIN + FOLDED_ICON.getIconWidth() + RIGHT_MARGIN, editor.getPreferredSize().height);		
		} else {
			return null;
		}
	}

	public void setFont( Font font) {
		super.setFont( font);
		
		if ( font != null) {
			fontMetrics = getFontMetrics( font);
			lineHeight = fontMetrics.getHeight();
		}
	}

	/**
	 * The line height defaults to the line height of the font for this
	 * component. The line height can be overridden by setting it to a
	 * positive non-zero value.
	 */
	public int getLineHeight() {
		return lineHeight;
	}

	public int getStartOffset() {
		return 4;
	}
	
	public void paintComponent( Graphics g ) {
//		long time = System.currentTimeMillis();
//		long getLineStartTime = 0;
//		long getNextLineTime = 0;

		if ( fontMetrics != null) {
			int lineHeight = getLineHeight();
			int startOffset = getStartOffset();
			Rectangle drawHere = g.getClipBounds();
			
			int width = getPreferredSize().width;

			// System.out.println( drawHere );
			// Paint the background
			g.setColor( getBackground());
			g.fillRect( drawHere.x, drawHere.y, drawHere.width, drawHere.height );

			g.setColor( UIManager.getColor( "controlShadow"));
			g.drawLine( width-1, drawHere.y, width-1, drawHere.y + drawHere.height);

			// Determine the number of lines to draw in the foreground.
			g.setColor( getForeground());

			int startLineNumber = 0; //( drawHere.y / lineHeight ) + 1;
			int endLineNumber = startLineNumber + ( drawHere.height / lineHeight);
			int start = (( drawHere.y / lineHeight ) * lineHeight + lineHeight - startOffset) + 3;
			
			try{
				startLineNumber = editor.getLineNumber( drawHere.y);
				if ( startLineNumber > 1) {
					startLineNumber = startLineNumber - 1;
				}
			} catch ( Exception e) {
				e.printStackTrace();
			}

			try{
				int lines = editor.getLines();
				endLineNumber = editor.getLineNumber( drawHere.y + drawHere.height);

				if ( endLineNumber <= lines - 1) {
					endLineNumber = endLineNumber+1;
				} else {
					endLineNumber = lines;
				}
			} catch ( Exception e) {
				e.printStackTrace();
			}

			int line = startLineNumber;
			int previous = -1;

			while ( line < endLineNumber) {
				try {
//					long t = System.currentTimeMillis();
					start = editor.getLineStart( line);
//					getLineStartTime += System.currentTimeMillis() - t;

					if ( start != -1) {
						if ( previous != -1 && line - previous > 1) {
							g.setColor( UIManager.getColor( "controlShadow"));
							g.drawLine( 1, start-1, UNFOLDED_ICON.getIconWidth(), start-1);
		
							g.setColor( getForeground());
						}
		
						boolean folded = isFolded( line+1);

						if ( folded) {
							FOLDED_ICON.paintIcon( this, g, 1, start + ((lineHeight - UNFOLDED_ICON.getIconHeight())/2));
						} else { 
							boolean tagStart = manager.isMultipleLineTagStart( line);
							
							if ( tagStart) {
								UNFOLDED_ICON.paintIcon( this, g, 1, start + ((lineHeight - UNFOLDED_ICON.getIconHeight())/2));
							}
						}
					}

					previous = line;
//					t = System.currentTimeMillis();
					line = editor.getNextLineNumber( line);
//					getNextLineTime += System.currentTimeMillis() - t;
				} catch ( Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}

//		time = System.currentTimeMillis() - time;
//		System.out.println( "FoldingMargin.paintComponent() ["+time+"]["+getLineStartTime+"]["+getNextLineTime+"]");
	}
	
	private void fold( Element start, Element end, boolean all) {
		if ( isVisible()) {
			Fold fold = new Fold( start, end, all);
			int startIndex = getNextFoldIndex( fold.getStart());
			
			if ( startIndex != -1) {
				Vector oldFolds = new Vector(folds);
		
				for ( int i = startIndex; i < oldFolds.size(); i++) {
					Fold f = (Fold)oldFolds.elementAt(i);
		
					if ( fold.contains( f.getStart())) {
						fold.add( f);
						folds.removeElement( f);
					} else if ( f.getStart() > fold.getEnd()) {
						break;
					}
				}
			}
			
			addFold( fold);
			
			int caret = editor.getCaretPosition();
			Element e = editor.getDocument().getDefaultRootElement();
			int index = e.getElementIndex( editor.getCaretPosition());
			
			if ( isFolded( index)) {
				editor.setCaretPosition( start.getEndOffset()-1);
			}
			
			changed = true;
	
			editor.revalidate();
			editor.repaint();
			
			manager.updateMargins();
	
			manager.revalidate();
			manager.repaint();
		}
	}
	
	public void unfold( int index) {
		if ( isVisible()) {
			Fold f = getFold( index);

			if ( f != null) {
				int childIndex = folds.indexOf( f);
				folds.removeElement( f);
				
				if ( f.isCollapseAll()) {
					Vector children = getFolds( f.getStart(), f.getEnd());

					if ( children.size() > 0) {
						unfold( children, index);
					}

					for ( int j = 0; j < children.size(); j++) {
						folds.insertElementAt( (Fold)children.elementAt(j), childIndex+j);
					}
				} else {
					f.remove( index, index);
					Vector children = f.getChildren();

					for ( int j = 0; j < children.size(); j++) {
						addFold( (Fold)children.elementAt(j));
					}
				}
				
				f.shallowCleanup();
				changed = true;
			}
			
			if ( changed) {
				manager.updateMargins();
				
				manager.revalidate();
				manager.repaint();

				editor.revalidate();
				editor.repaint();
			}
		}
	}
	
	public void unfoldFully( int index) {
		if ( isVisible()) {
			Fold f = getFold( index);

			if ( f != null) {
				int childIndex = folds.indexOf( f);
				folds.removeElement( f);
				f.cleanup();
				
				changed = true;
			}
			
			if ( changed) {
				manager.updateMargins();
				
				manager.revalidate();
				manager.repaint();

				editor.revalidate();
				editor.repaint();
			}
		}
	}

	private void unfold( Vector list, int index) {
		Vector oldList = new Vector( list);
		
		for ( int i = 0; i < oldList.size(); i++) {
			Fold f = (Fold)oldList.elementAt(i);

			if ( f.contains( index)) {
				int childIndex = list.indexOf( f);
				list.removeElement( f);
				
				if ( f.isCollapseAll()) {
					Vector children = getFolds( f.getStart(), f.getEnd());
					
					if ( children.size() > 0) {
						unfold( children, index);
					}

					for ( int j = 0; j < children.size(); j++) {
						list.insertElementAt( (Fold)children.elementAt(j), childIndex+j);
					}
				}
				
				f.shallowCleanup();
				changed = true;
				break;
			}
		}
	}
	
	private Vector getFolds( int start, int end) {
		Vector result = new Vector();
		XmlDocument doc = (XmlDocument)editor.getDocument();
		Element root = doc.getDefaultRootElement();

		try {
			for ( int i = start+1; i < end; i++) {
				if ( manager.isMultipleLineTagStart( i)) {
					Element element = root.getElement( i);
					Tag tag = doc.getCurrentTag( element.getStartOffset());
					
					if ( tag == null) {
						tag = doc.getNextTag( element.getStartOffset());
					}
		
					if ( tag.getType() == Tag.START_TAG) {
						int tagStart = tag.getStart();
						tag = doc.getEndTag( tag);
						
						if ( tag.getStart() < tagStart) {
							return null;
						}
						
						int tagEnd = root.getElementIndex( tag.getStart());

						if ( tagEnd - i > 1) {
							Fold fold = new Fold( root.getElement( i), root.getElement( tagEnd), true);
							result.addElement( fold);
						}
						
						i = tagEnd;
					}
				}
			}
		} catch ( Exception e) {
		}
		
		return result;
	}

	public Fold getFold( int line) {
		if ( isVisible() && folds != null) {
			int start = 0;
			int end = folds.size() - 1;
			
			while ( end >= start) {
				int index = (((end - start) / 2) + start);
		
				Fold fold = (Fold)folds.elementAt( index);

				if ( line >= fold.getEnd()) {
					start = index+1;
				} else if ( line <= fold.getStart()) {
					end = index-1;
				} else { // if ( line > fold.getStart() && line < fold.getEnd()) {
//					System.out.println( "getFold( "+line+") ["+fold.getStart()+", "+fold.getEnd()+"]");
					return fold;
				}
			}
		}
		
		return null;
	}

	public int getNextFoldIndex( int line) {
		if ( isVisible() && folds != null) {
			int start = 0;
			int end = folds.size() - 1;
			Fold lastFold = null;
			int lastIndex = -1;
			
			while ( end >= start) {
				int index = (((end - start) / 2) + start);
				Fold fold = (Fold)folds.elementAt( index);
		
				lastFold = fold;
				lastIndex = index;

				if ( line >= fold.getEnd()) {
					start = index+1;
				} else if ( line <= fold.getStart()) {
					end = index-1;
				} else { // if ( line > fold.getStart() && line < fold.getEnd()) {
//					System.out.println( "getFold( "+line+") ["+fold.getStart()+", "+fold.getEnd()+"]");
					return index;
				}
			}
			
			if ( lastFold == null || lastFold.getStart() > line) {
				return lastIndex;
			} else {
				return lastIndex + 1;
			}
		}
		
		return -1;
	}

	public boolean isFolded( int line) {
		return getFold( line) != null;
	}
	
	private void addFold( Fold fold) {
		int index = getNextFoldIndex( fold.getStart());

		if ( index != -1 && index < folds.size()) {
			folds.insertElementAt( fold, index);
			return;
		}
		
		folds.addElement( fold);
	}
	
	public Vector getFolds() {
//		checkFolds();

		return folds;
	}
	
	public void checkFolds() {
		if ( isVisible()) {
			Vector oldFolds = new Vector(folds);
	
			// Update the folds, to make sure no line has been deleted.
			for ( int i = 0; i < oldFolds.size(); i++) {
				Fold fold = (Fold)oldFolds.elementAt(i);
				
				if ( !fold.isValid()) {
					fold.checkFolds();
					folds.removeElement( fold);
	
					Vector children = fold.getChildren();
					for ( int j = 0; j < children.size(); j++) {
						addFold( (Fold)children.elementAt(j));
					}
			
					changed = true;
				}
			}
		}
	}

	protected void removeAllListeners() {
		MouseListener[] mouseListeners = getMouseListeners();

		for ( int i = 0; i >= mouseListeners.length; i++) {
			removeMouseListener( mouseListeners[i]);
		}

		// Guaranteed to return a non-null array
		Object[] list = listenerList.getListenerList();
		
		for ( int i = list.length-2; i >= 0; i -= 2) {
			listenerList.remove( (Class)list[i], (EventListener)list[i+1]);
		}
	}

	public void cleanup() {
		removeAllListeners();
		cleanupFolds();
		finalize();
	}
	
	protected void finalize() {
		fontMetrics = null;
		editor = null;
		folds = null;
		manager = null;
	}
}
