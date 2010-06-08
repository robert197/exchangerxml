/*
 * $Id: EditorPanel.java,v 1.16 2004/10/27 18:05:21 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xml.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.EventListener;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Element;

import com.cladonia.xml.XMLError;
import com.cladonia.xngreditor.ErrorList;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * This EditorPanel displays an editor and its margin.
 *
 * @version $Revision: 1.16 $, $Date: 2004/10/27 18:05:21 $
 * @author Dogsbay
 */
public class EditorPanel extends JPanel implements FoldingManager {
	private static final boolean DEBUG = false;

	private boolean initialised 				= false;
	private EditorProperties properties			= null;
	private Editor parent						= null;

	private EditorPanel other				= null;
	private XmlEditorPane editor				= null;
	private JScrollPane scroller 				= null;
	private Margin margin 						= null;
	private BookmarkMargin bookmarkMargin 		= null;
	private FoldingMargin foldingMargin 		= null;
	private OverviewMargin overviewMargin 		= null;
	private ScrollableEditorPanel editorPanel 	= null;
	
	boolean showing = false;

    public EditorPanel( Editor _parent, ConfigurationProperties props, boolean initialised) {
		super( new BorderLayout());
		
		this.initialised = initialised;
		
		addComponentListener( new ComponentListener() {
			public void componentHidden( ComponentEvent e) {
				//System.out.println( "["+editor.getName()+"] componentHidden( "+e+")");
			}
			public void componentMoved( ComponentEvent e) {
				//System.out.println( "["+editor.getName()+"] componentMoved( "+e+")");
			}
			public void componentResized( ComponentEvent e) {
//				if ( getSize().height > 0) {
//					if ( !isInitialised()) {
//						initialise();
//					}
//					
////					if ( !showing) {
//					if ( !isVisible()) {
//						parent.updateSplitPane();
//						getEditor().requestFocusInWindow();
//					}
//
////					showing = true;
//					setVisible( true);
//				} else {
////					if ( showing) {
//					if ( isVisible()) {
//						parent.updateSplitPane();
//						other.getEditor().requestFocusInWindow();
//					}
//
//					setVisible( false);
////					showing = false;
//				}
			}
			public void componentShown( ComponentEvent e) {
				//System.out.println( "["+editor.getName()+"] componentShown( "+e+")");
			}
		});

		this.properties = props.getEditorProperties();
		this.parent 	= _parent;
		
	    editor = new XmlEditor( properties.isSoftWrapping(), parent.getErrors());
		margin = new Margin( this, editor);
		scroller = new JScrollPane( new ScrollableEditorPanel( editor));
		
		bookmarkMargin = new BookmarkMargin( parent, this, editor);
		foldingMargin = new FoldingMargin( this, editor);
		editor.setFoldingMargin( foldingMargin);
		overviewMargin = new OverviewMargin( parent, this, scroller);
		
		JPanel marginPanel = new JPanel( new BorderLayout());
		marginPanel.add( bookmarkMargin, BorderLayout.WEST);
		marginPanel.add( margin, BorderLayout.CENTER);
		marginPanel.add( foldingMargin, BorderLayout.EAST);
		
		JPanel corner = new JPanel();
		corner.setBorder( new CompoundBorder( 
				new MatteBorder( 1, 0, 0, 1, UIManager.getColor("controlDkShadow")), 
				new MatteBorder(1, 0, 0, 0, Color.white) ));
		scroller.setCorner( JScrollPane.LOWER_LEFT_CORNER, corner);
		scroller.setRowHeaderView( marginPanel);
	    
		if ( properties.isSoftWrapping()) {
			scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		}
		
//		scroller.setBackground( editor.getBackground());
//		scroller.getViewport().setBackground( editor.getBackground());
//		this.setBackground( editor.getBackground());

    	add( scroller, BorderLayout.CENTER);
		add( overviewMargin, BorderLayout.EAST);
		
	    updatePreferences();
    }

 	/**
	 * Update the preferences.
	 */
	public void updatePreferences() {
		int pos = -1;
		
		if ( isInitialised() && editor.getCaret() != null) {
			pos = editor.getCaretPosition();
		}

		setWrapping( properties.isSoftWrapping());

		editor.setAntialiasing( TextPreferences.isAntialiasing());
		editor.setFont( TextPreferences.getBaseFont());
		margin.setFont( TextPreferences.getBaseFont());

		setAttributes( Constants.ELEMENT_NAME, TextPreferences.ELEMENT_NAME);
		setAttributes( Constants.ELEMENT_VALUE, TextPreferences.ELEMENT_VALUE);
		setAttributes( Constants.ELEMENT_PREFIX, TextPreferences.ELEMENT_PREFIX);
	
		setAttributes( Constants.ATTRIBUTE_NAME, TextPreferences.ATTRIBUTE_NAME);
		setAttributes( Constants.ATTRIBUTE_VALUE, TextPreferences.ATTRIBUTE_VALUE);
		setAttributes( Constants.ATTRIBUTE_PREFIX, TextPreferences.ATTRIBUTE_PREFIX);
	
		setAttributes( Constants.NAMESPACE_NAME, TextPreferences.NAMESPACE_NAME);
		setAttributes( Constants.NAMESPACE_VALUE, TextPreferences.NAMESPACE_VALUE);
		setAttributes( Constants.NAMESPACE_PREFIX, TextPreferences.NAMESPACE_PREFIX);
	
		setAttributes( Constants.ENTITY, TextPreferences.ENTITY);
		setAttributes( Constants.COMMENT, TextPreferences.COMMENT);
		setAttributes( Constants.CDATA, TextPreferences.CDATA);
		setAttributes( Constants.SPECIAL, TextPreferences.SPECIAL);

		setAttributes( Constants.PI_TARGET, TextPreferences.PI_TARGET);
		setAttributes( Constants.PI_NAME, TextPreferences.PI_NAME);
		setAttributes( Constants.PI_VALUE, TextPreferences.PI_VALUE);

		setAttributes( Constants.STRING_VALUE, TextPreferences.STRING_VALUE);
		setAttributes( Constants.ENTITY_VALUE, TextPreferences.ENTITY_VALUE);

		setAttributes( Constants.ENTITY_DECLARATION, TextPreferences.ENTITY_DECLARATION);
		setAttributes( Constants.ENTITY_NAME, TextPreferences.ENTITY_NAME);
		setAttributes( Constants.ENTITY_TYPE, TextPreferences.ENTITY_TYPE);
	
		setAttributes( Constants.ATTLIST_DECLARATION, TextPreferences.ATTLIST_DECLARATION);
		setAttributes( Constants.ATTLIST_NAME, TextPreferences.ATTLIST_NAME);
		setAttributes( Constants.ATTLIST_TYPE, TextPreferences.ATTLIST_TYPE);
		setAttributes( Constants.ATTLIST_VALUE, TextPreferences.ATTLIST_VALUE);
		setAttributes( Constants.ATTLIST_DEFAULT, TextPreferences.ATTLIST_DEFAULT);
	
		setAttributes( Constants.ELEMENT_DECLARATION, TextPreferences.ELEMENT_DECLARATION);
		setAttributes( Constants.ELEMENT_DECLARATION_NAME, TextPreferences.ELEMENT_DECLARATION_NAME);
		setAttributes( Constants.ELEMENT_DECLARATION_TYPE, TextPreferences.ELEMENT_DECLARATION_TYPE);
		setAttributes( Constants.ELEMENT_DECLARATION_PCDATA, TextPreferences.ELEMENT_DECLARATION_PCDATA);
		setAttributes( Constants.ELEMENT_DECLARATION_OPERATOR, TextPreferences.ELEMENT_DECLARATION_OPERATOR);

		setAttributes( Constants.NOTATION_DECLARATION, TextPreferences.NOTATION_DECLARATION);
		setAttributes( Constants.NOTATION_DECLARATION_NAME, TextPreferences.NOTATION_DECLARATION_NAME);
		setAttributes( Constants.NOTATION_DECLARATION_TYPE, TextPreferences.NOTATION_DECLARATION_TYPE);

		setAttributes( Constants.DOCTYPE_DECLARATION, TextPreferences.DOCTYPE_DECLARATION);
		setAttributes( Constants.DOCTYPE_DECLARATION_TYPE, TextPreferences.DOCTYPE_DECLARATION_TYPE);

		editor.setTabSize( TextPreferences.getTabSize());
		editor.setErrorHighlighting( properties.isErrorHighlighting());

		setEndTagCompletion( properties.isTagCompletion());
		setSmartIndentation( properties.isSmartIndentation());
		
		showLinenumberMargin( properties.isShowMargin());
		showOverviewMargin( properties.isShowOverviewMargin());
		showFoldingMargin( properties.isShowFoldingMargin());
		showAnnotationMargin( properties.isShowAnnotationMargin());

		this.revalidate();
		this.repaint();
		editor.updateUI();

		if ( isInitialised() && editor.getCaret() != null) {
			editor.setCaretPosition( pos);
		}
	}

	public void setOther( EditorPanel other) {
		this.other = other;
	}
	
	public void setEndTagCompletion( boolean enabled) {
		editor.setTagCompletion( enabled);
	}
	
	public void setSmartIndentation( boolean enabled) {
		editor.setSmartIndent( enabled);
	}
	
	public void showLinenumberMargin( boolean visible) {
		margin.setVisible( visible);
	}

	public void showOverviewMargin( boolean visible) {
		overviewMargin.setVisible( visible);
	}

	public void showFoldingMargin( boolean visible) {
		foldingMargin.setVisible( visible);
	}

	public void showAnnotationMargin( boolean visible) {
		bookmarkMargin.setVisible( visible);
	}

	public void gotoCursor() {
		try{
			Rectangle pos = editor.modelToView( editor.getCaretPosition());
			editor.scrollRectToVisible( pos);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public void selectError( XMLError error) {
		if ( error != null && error.getLineNumber() > 0) {
			XmlDocument doc = (XmlDocument)getEditor().getDocument();
			Element element = doc.getDefaultRootElement().getElement( error.getLineNumber()-1);
			int pos = element.getStartOffset()+(error.getColumnNumber()-1);

			if ( Character.isWhitespace( doc.getLastCharacter( pos))) {
				editor.select( pos, pos+1);
			} else {
				editor.select( pos-1, pos);
			}
			
			parent.updateError( error);
		}
	}


	/**
	 * Selects the indicated line.
	 *
	 * @param line the line to select.
	 */
	public void selectLineWithoutEnd( int line) {
		if ( line > 0) {
			Element root = editor.getDocument().getDefaultRootElement();
			Element elem = root.getElement( line-1);

			if ( elem != null) {
				int start = elem.getStartOffset();
				int end = elem.getEndOffset()-1;
				
				getEditor().select( start, end);
			}
		}
	}

	public void setWrapping( boolean wrap) {
	
		if ( editor.isWrapped() != wrap) {
			foldingMargin.cleanupFolds();

			int pos = editor.getCaretPosition();
			XmlDocument doc = (XmlDocument)editor.getDocument();
			editor.setWrapped( wrap);

			if ( wrap) {
				scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			} else {
				scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			}
			
			editor.setDocument( doc);
			
			if ( isInitialised()) {
				editor.setCaretPosition( pos);
			}
		}
	}

	private void setAttributes( int id, String property) {
		FontType type = TextPreferences.getFontType( property);
		editor.setAttributes( id, type.getColor(), type.getStyle());
	}
	
	public FoldingMargin getFoldingMargin() {
		return foldingMargin;
	}

	public Margin getMargin() {
		return margin;
	}

	public JScrollPane getScroller() {
		return scroller;
	}

	public BookmarkMargin getBookmarkMargin() {
		return bookmarkMargin;
	}

	public OverviewMargin getOverviewMargin() {
		return overviewMargin;
	}

	public XmlEditorPane getEditor() {
		return editor;
	}
	
	public void updateMargins() {
		foldingMargin.revalidate();
		foldingMargin.repaint();

		getBookmarkMargin().revalidate();
		bookmarkMargin.repaint();

		overviewMargin.revalidate();
		overviewMargin.repaint();
		
	}
	
	public void initialise() {
		editor.setContentType( other.getEditor().getContentType());
		editor.setDocument( other.getEditor().getDocument());
		
		initialised = true;
	}

	public boolean isInitialised() {
		return initialised;
	}

	private int currentStartTagPos = -1;
	private Tag currentStartTag = null;
	private Tag currentEndTag = null;

	public Tag getCurrentStartTag() {
		if ( parent.isKeyReleased()) {
			return getCurrentStartTag( getEditor().getCaretPosition());
		} else {
			return null;
		}
	}
	
	private Tag getCurrentStartTag( int pos) {
		
		if ( pos != currentStartTagPos) {
			XmlDocument doc = (XmlDocument)getEditor().getDocument();
			Tag tag = doc.getCurrentTag( pos);
			
			if ( tag != null && (tag.getType() == Tag.START_TAG || tag.getType() == Tag.EMPTY_TAG)) {
				currentStartTag = tag;
			} else {
				currentStartTag = doc.getParentStartTag( pos);
			}
			
			currentEndTag = null;
		}
		
		currentStartTagPos = pos;

		return currentStartTag;
	}

	public Tag getEndTag( Tag startTag) {
		
		if ( startTag == currentStartTag && currentEndTag != null) {
			return currentEndTag;
		} else {
			XmlDocument doc = (XmlDocument)getEditor().getDocument();
			Tag tag = null;
	
			if ( startTag != null && startTag.getType() == Tag.START_TAG) {
				tag = doc.getEndTag( startTag);
			}
			
			if ( startTag == currentStartTag) {
				currentEndTag = tag;
			}
	
			return tag;
		}
	}
	
	/**
	 * Returns wether the current line starts with a multiple line tag .
	 *
	 * @return the true, show folded icon.
	 */
	public boolean isMultipleLineTagStart( int line) throws BadLocationException {
		if ( parent.getDocument().isXML()) {
			XmlDocument doc = (XmlDocument)getEditor().getDocument();
			return doc.isMultipleLineTagStart( line);
		}

		return false;
	}

	public int getCursorLine() {
		Element root = getEditor().getDocument().getDefaultRootElement();
		return root.getElementIndex( editor.getCaretPosition());
	}

	public void setFocus() {
		editor.requestFocusInWindow();
	}

	protected void removeAllListeners() {
		
		// Guaranteed to return a non-null array
		Object[] list = listenerList.getListenerList();
		
		for ( int i = list.length-2; i >= 0; i -= 2) {
			listenerList.remove( (Class)list[i], (EventListener)list[i+1]);
		}
	}

	public void cleanup() {
		removeAllListeners();

		removeAll();
		
		getEditor().removeCaretListener( parent);
		getEditor().setCaret( null);
		getEditor().cleanup();

		margin.cleanup();
		bookmarkMargin.cleanup();
		foldingMargin.cleanup();
		overviewMargin.cleanup();

		finalize();
	}
		
	protected void finalize() {
		properties		= null;
		parent			= null;

		other			= null;
		editor			= null;
		scroller 		= null;
		margin 			= null;
		bookmarkMargin 	= null;
		foldingMargin 	= null;
		overviewMargin 	= null;
		editorPanel 	= null;
	}

		// Extension of XmlEditorPane
	public class XmlEditor extends XmlEditorPane {
	
		public XmlEditor( boolean wrapping, ErrorList errors) {
			super( wrapping, errors);
		}
		
		public void indentSelectedText() {
			parent.indentSelectedText( true);
		}

		public void unindentSelectedText() {
			parent.unindentSelectedText();
		}

		public void gotoEndTag() {
			parent.gotoEndTag();
		}

		public void gotoStartTag() {
			parent.gotoStartTag();
		}

		public void promptText() {
			parent.promptText();
		}
		
		public void gotoNextAttributeValue() {
			parent.gotoNextAttributeValue();
		}

		public void gotoPreviousAttributeValue() {
			parent.gotoPreviousAttributeValue();
		}

		public void setCaret( Caret caret) {
			if ( caret instanceof Editor.XmlCaret) {
				super.setCaret( caret);
			} else {
				super.setCaret( getCaret());
			}
		}
	}

}
