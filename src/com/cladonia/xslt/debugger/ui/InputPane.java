/*
 * $Id: InputPane.java,v 1.20 2005/09/05 14:00:54 gmcgoldrick Exp $
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
import java.awt.Dimension;
/*import java.awt.FontMetrics;
import java.awt.Graphics;*/
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
/*import javax.swing.plaf.TextUI;
import javax.swing.plaf.UIResource;*/
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
/*import javax.swing.text.JTextComponent;
import javax.swing.text.Position;*/

import org.bounce.QIcon;
import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.editor.Constants;
import com.cladonia.xml.editor.FoldingMargin;
import com.cladonia.xml.editor.FoldingManager;
import com.cladonia.xml.editor.Margin;
import com.cladonia.xml.editor.ScrollableEditorPanel;
import com.cladonia.xml.editor.Tag;
import com.cladonia.xml.editor.XmlDocument;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.properties.Keystroke;
import com.cladonia.xngreditor.properties.TextPreferences;
import com.cladonia.xslt.debugger.Breakpoint;
import com.cladonia.xslt.debugger.BreakpointList;
import com.cladonia.xslt.debugger.ui.actions.AddBreakpointAction;
import com.cladonia.xslt.debugger.ui.actions.DisableBreakpointAction;
import com.cladonia.xslt.debugger.ui.actions.EnableBreakpointAction;
import com.cladonia.xslt.debugger.ui.actions.RemoveBreakpointAction;

/**
 * A debuggers view, showing the XML content.
 * 
 * @version	$Revision: 1.20 $, $Date: 2005/09/05 14:00:54 $
 * @author Dogsbay
 */
public class InputPane extends JPanel implements FoldingManager {
	private static final boolean DEBUG = false;
	private static final boolean DEBUG1 = false;
	
	private static final ImageIcon ENABLED_BREAKPOINT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointEnabledIcon.gif");
	private static final ImageIcon DISABLED_BREAKPOINT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointDisabledIcon.gif");

	private JPopupMenu popup = null;
	private AddBreakpointAction addBreakpoint = null;
	private RemoveBreakpointAction removeBreakpoint = null;
	private DisableBreakpointAction disableBreakpoint = null;
	private EnableBreakpointAction enableBreakpoint = null;

	private XmlEditorPane editor = null;
	private String location = null;
	private BreakpointList breakpoints = null;
	private int selectedLine = -1;
	private JScrollPane scroller = null;
	private InputView view = null;

	private QIcon normalIcon = null;
	private QIcon enabledBreakpointsIcon = null;
	private QIcon disabledBreakpointsIcon = null;
	
	private boolean keyReleased					= true;
	private boolean caretUpdated				= false;

	private BreakpointMargin breakpointMargin	= null;
	private OverviewMargin overviewMargin	= null;
	private FoldingMargin foldingMargin			= null;
	private Margin margin						= null;
	
	private AbstractAction selectAll = null;
    private AbstractAction tabAction = null;
    private AbstractAction unindentAction = null;
	private String escapeKey;
	private String enterKey;
	private String upKey;
	private String downKey;
	private ConfigurationProperties props;
	
	private String initialVersionOfDocument = null;
	
			
	/**
	 * Construct a view.
	 */
	public InputPane( final InputView view, BreakpointList breakpoints, ConfigurationProperties props) {
		super( new BorderLayout());
		
		if (DEBUG) System.out.println( "InputPane()");
		
		this.breakpoints = breakpoints;
		this.view = view;
		this.props = props;
		
		
		//Thomas Curley 24.08.05
		//for editable debugger
		
		tabAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                indentSelectedText(true);
                
            }
        };
        
        unindentAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                unindentSelectedText();
                
            }
        };
        // the select all action (needed for emacs keys)
        selectAll = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

            	editor.selectAll();
                /*if (parent != null) {
                    if (parent.getView() != null) {
                        parent.getView().getEditor().getEditor().selectAll();
                        parent.getView().getEditor().setFocus();
                    }
                }*/
            }
        };
		
		editor = new XmlEditorPane( false);
		editor.addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( (e.getKeyCode() == KeyEvent.VK_UP) || (e.getKeyCode() == KeyEvent.VK_DOWN) || (e.getKeyCode() == KeyEvent.VK_LEFT) || (e.getKeyCode() == KeyEvent.VK_RIGHT) || (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) || (e.getKeyCode() == KeyEvent.VK_PAGE_UP)) {
					keyReleased = false;
				}
			}

			public void keyReleased( KeyEvent e) {
				keyReleased = true;

				if ( caretUpdated) {
					caretUpdated = false;

					updateMargins();
					if(((XmlDocument)editor.getDocument()).isLoading() == false) {
						updateTabState();
					}
				}
			}
		});
		editor.addCaretListener( new CaretListener() {
			public void caretUpdate( CaretEvent e) {
				caretUpdated = true;

				if ( keyReleased) {
					updateMargins();
					if(((XmlDocument)editor.getDocument()).isLoading() == false) {
					
						updateTabState();
					}
				}
			}
		});
		
		editor.getActionMap().put("xngr-indent", tabAction);
        editor.getActionMap().put("xngr-unindent", unindentAction);
        

		foldingMargin = new FoldingMargin( this, editor);
		editor.setFoldingMargin( foldingMargin);
		
		editor.setCaret( new XmlCaret( editor));
		editor.setEditable( true);
		editor.setCaretPosition( 0);
		
		ScrollableEditorPanel panel = new ScrollableEditorPanel( editor);
		
		scroller = new JScrollPane( panel);
		scroller.getViewport().setPreferredSize( new Dimension( 100, 100));
		
		margin = new Margin( editor);
		margin.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				InputPane.this.popupTriggered( e);
			}
		});

		margin.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				int line = InputPane.this.getLineForOffset( e.getY());

				InputPane.this.setBreakpoint( line);
			}
		});

		breakpointMargin = new BreakpointMargin( this, editor);
		breakpointMargin.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				InputPane.this.popupTriggered( e);
			}
		});

		editor.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				InputPane.this.popupTriggered( e);
			}
		});

		
		JPanel marginPanel = new JPanel( new BorderLayout());
		marginPanel.add( breakpointMargin, BorderLayout.WEST);
		marginPanel.add( margin, BorderLayout.CENTER);
		marginPanel.add( foldingMargin, BorderLayout.EAST);
		
		JPanel corner = new JPanel();
		corner.setBorder( new CompoundBorder( 
				new MatteBorder( 1, 0, 0, 1, UIManager.getColor("controlDkShadow")), 
				new MatteBorder(1, 0, 0, 0, Color.white) ));
		scroller.setCorner( JScrollPane.LOWER_LEFT_CORNER, corner);
		scroller.setRowHeaderView( marginPanel);

		overviewMargin = new OverviewMargin( this, scroller);

		add( scroller, BorderLayout.CENTER);
		add( overviewMargin, BorderLayout.EAST);

		updatePreferences();
	}
	
	public void updateTabState() {
		
		if(this.hasDocumentChanged() == true) {
			
			view.updateTabState(this, true);
		}
		else {
			view.updateTabState(this, false);
		}
	}
	
	/**
     * Unindents the selected text.
     */
    public void unindentSelectedText() {

        int start = Math.min(getEditor().getSelectionStart(), getEditor()
                .getSelectionEnd());
        int end = Math.max(getEditor().getSelectionStart(), getEditor()
                .getSelectionEnd());
        XmlDocument document = (XmlDocument) getEditor().getDocument();
        Element root = document.getDefaultRootElement();
        int startElementIndex = root.getElementIndex(start);
        int endElementIndex = root.getElementIndex(end > start ? end - 1 : end);
        try {
            for (int i = startElementIndex; i < endElementIndex + 1; i++) {
                Element element = root.getElement(i);
                int off = element.getStartOffset();
                try {
                    String text = getEditor().getText(off,
                            getEditor().getTabSize());
                    int spaces = 0;
                    int tab = -1;
                    while (spaces < getEditor().getTabSize()) {
                        if (text.charAt(spaces) == '\t') {
                            tab = off + spaces;
                            break;
                        }
                        else if (text.charAt(spaces) != ' ') {
                            break;
                        }
                        spaces++;
                    }
                    if (tab != -1) {
                        document.remove(tab, 1);
                    }
                    else if (spaces > 0) {
                        document.remove(off, spaces);
                    }
                }
                catch (Exception e) {
                }
            }
        }
        finally {
            
        }
    }
    
    /**
     * Indents the selected text.
     *
     * @param tab when true the tab key is used as indentation.
     */
    public void indentSelectedText(boolean tab) {

    	//getChangeManager().startCompound(true);
        int start = Math.min(getEditor().getSelectionStart(), getEditor()
                .getSelectionEnd());
        int end = Math.max(getEditor().getSelectionStart(), getEditor()
                .getSelectionEnd());
        try {
            if (tab) {
                if (start != end) {
                    XmlDocument document = (XmlDocument) getEditor()
                            .getDocument();
                    Element root = document.getDefaultRootElement();
                    int startElementIndex = root.getElementIndex(start);
                    int endElementIndex = root.getElementIndex(end);
                    if (startElementIndex != endElementIndex) {
                        endElementIndex = root.getElementIndex(end - 1);
                        for (int i = startElementIndex; i < endElementIndex + 1; i++) {
                            Element element = root.getElement(i);
                            int off = element.getStartOffset();
                            try {
                                document.insertString(element.getStartOffset(),
                                        getTabString(), null);
                            }
                            catch (Exception e) {
                            }
                        }
                    }
                    else {
                        getEditor().replaceSelection(getTabString());
                    }
                }
                else {
                    getEditor().replaceSelection(getTabString());
                }
            }
            else {
                XmlDocument document = (XmlDocument) getEditor().getDocument();
                Element root = document.getDefaultRootElement();
                int startElementIndex = root.getElementIndex(start);
                int endElementIndex = root
                        .getElementIndex(end > start ? end - 1 : end);
                for (int i = startElementIndex; i < endElementIndex + 1; i++) {
                    Element element = root.getElement(i);
                    int off = element.getStartOffset();
                    try {
                        document.insertString(element.getStartOffset(),
                                getTabString(), null);
                    }
                    catch (Exception e) {
                    }
                }
            }
        }
        finally {
            //getChangeManager().endCompound();
        }
    }
    
    /**
     * returns the string representation of the tab character.
     *
     * @return the String the tab should be replaced with.
     */
    public String getTabString() {

        return TextPreferences.getTabString();
    }

	
	/**
     *  sets all the keymappings for the specified configuation
     *
     * @param parent The ExchangerEditor
     * @param configName The configuration name 
     */
    public void setKeyMappings(String configName) {

        if (configName.equalsIgnoreCase(KeyPreferences.EMACS)) {
            // turn off Ctrl-X for each editor as that will now be used by emacs editing mode
            //turnOffCtrlX();
        }
        KeyPreferences pref = props.getKeyPreferences();
        Hashtable keyMap = pref.getKeyMapElements(configName);
        Enumeration actionNames = keyMap.keys();
        while (actionNames.hasMoreElements()) {
            String actionName = (String) actionNames.nextElement();
            XElement keymap = (XElement) keyMap.get(actionName);
            Vector keystrokes = pref.getKeystrokes(keymap);
            Keystroke keystroke = null;
            Keystroke keystroke2 = null;
            int keystrokeSize = keystrokes.size();
            if (keystrokeSize == 1) {
                keystroke = (Keystroke) keystrokes.get(0);
            }
            else if (keystrokeSize == 2) {
                keystroke = (Keystroke) keystrokes.get(0);
                keystroke2 = (Keystroke) keystrokes.get(1);
            }
            else {
                // there are no keys assigned for this action, so remove action from map
            }
            if (actionName.equals(KeyPreferences.SELECT_ALL_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setSelectAllActionKey(pref.getKeyStroke(keystroke));
                }
                else if (keystrokeSize == 2) {
                    //	add the second keystroke to the mode's map
                    Action action = selectAll;
                    KeyStroke stroke = pref.getKeyStroke(keystroke2);
                    /*parent.getStatusbar().addToModeMap(
                            KeyPreferences.SELECT_ALL_ACTION, stroke, action);*/
                }
                else {
                    // blank the action
                    setSelectAllActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.TAB_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setTabActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setTabActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.UNINDENT_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setUnindentActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setUnindentActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.UP_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setUpActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setUpActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.DOWN_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setDownActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setDownActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.RIGHT_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setRightActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setRightActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.LEFT_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setLeftActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setLeftActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.PAGE_UP_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setPageUpActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setPageUpActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.PAGE_DOWN_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setPageDownActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setPageDownActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.BEGIN_LINE_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setBeginLineActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setBeginLineActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.END_LINE_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setEndLineActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setEndLineActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.BEGIN_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setBeginActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setBeginActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.END_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setEndActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setEndActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.PREVIOUS_WORD_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setPreviousWordActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setPreviousWordActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.NEXT_WORD_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setNextWordActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setNextWordActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.DELETE_NEXT_CHAR_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setDeleteNextCharActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setDeleteNextCharActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.DELETE_PREV_CHAR_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setDeletePrevCharActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setDeletePrevCharActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.GOTO_START_TAG_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setGoToStartTagActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setGoToStartTagActionKey(null);
                }
            }
            else if (actionName.equals(KeyPreferences.GOTO_END_TAG_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setGoToEndTagActionKey(pref.getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setGoToEndTagActionKey(null);
                }
            }
            else if (actionName
                    .equals(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setGoToNextAttributeValueActionKey(pref
                            .getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setGoToNextAttributeValueActionKey(null);
                }
            }
            else if (actionName
                    .equals(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION)) {
                // update the begin line action
                if (keystrokeSize == 1) {
                    // only a single keystroke
                    setGoToPreviousAttributeValueActionKey(pref
                            .getKeyStroke(keystroke));
                }
                else {
                    // blank the action
                    setGoToPreviousAttributeValueActionKey(null);
                }
            }
        }
        downKey = (String) getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false));
        upKey = (String) getEditor().getInputMap(JComponent.WHEN_FOCUSED).get(
                KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false));
        enterKey = (String) getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false));
        escapeKey = (String) getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                .get(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false));
    }
    
    private void setSelectAllActionKey(KeyStroke stroke) {

        removePreviousMapping("select-all");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "select-all");
        
    }

    private void setCutActionKey(KeyStroke stroke) {

        removePreviousMapping("cut-to-clipboard");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "cut-to-clipboard");
        
    }

    private void setTabActionKey(KeyStroke stroke) {

        removePreviousMapping("xngr-indent");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "xngr-indent");
        
    }

    private void setUnindentActionKey(KeyStroke stroke) {

        removePreviousMapping("xngr-unindent");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "xngr-unindent");
        
    }

    private void setGoToStartTagActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoStartTag");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoStartTag");
        
    }

    private void setGoToPreviousAttributeValueActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoPreviousAttributeValue");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoPreviousAttributeValue");
        
    }

    private void setGoToNextAttributeValueActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoNextAttributeValue");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoNextAttributeValue");
        
    }

    private void setGoToEndTagActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoEndTag");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoEndTag");
        
    }

    private void setUpActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-up");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-up");
        
    }

    private void setDownActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-down");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-down");
        
    }

    private void setRightActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-forward");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-forward");
        
    }

    private void setLeftActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-backward");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-backward");
        
    }

    private void setPageUpActionKey(KeyStroke stroke) {

        removePreviousMapping2("page-up");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "page-up");
        
    }

    private void setPageDownActionKey(KeyStroke stroke) {

        removePreviousMapping2("page-down");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "page-down");
        
    }

    private void setBeginLineActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-begin-line");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-begin-line");
        
    }

    private void setEndLineActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-end-line");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-end-line");
        
    }

    private void setBeginActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-begin");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-begin");
        
    }

    private void setEndActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-end");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-end");
        
    }

    private void setPreviousWordActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-previous-word");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-previous-word");
        
    }

    private void setNextWordActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-next-word");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-next-word");
        
    }

    private void setDeleteNextCharActionKey(KeyStroke stroke) {

        removePreviousMapping2("delete-next");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "delete-next");
        
    }

    private void setDeletePrevCharActionKey(KeyStroke stroke) {

        removePreviousMapping2("delete-previous");
        getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "delete-previous");
        
    }

    private void removePreviousMapping(String actionName) {

        InputMap im = getEditor().getInputMap();
        InputMap pem = im;
        while (pem != null) {
            KeyStroke[] strokes = pem.allKeys();
            for (int i = 0; i < strokes.length; i++) {
                String actionMapKey = pem.get(strokes[i]).toString();
                if (actionMapKey != null
                        && actionMapKey.equalsIgnoreCase(actionName)) {
                    pem.remove(strokes[i]);
                }
            }
            pem = pem.getParent();
        }
        
    }

    // only removes our own custom mappings, not any of the higher inbuilt ones
    private void removePreviousMapping2(String actionName) {

        InputMap im = getEditor().getInputMap();
        KeyStroke[] strokes = im.allKeys();
        for (int i = 0; i < strokes.length; i++) {
            String actionMapKey = im.get(strokes[i]).toString();
            if (actionMapKey != null
                    && actionMapKey.equalsIgnoreCase(actionName)) {
                im.remove(strokes[i]);
            }
        }        
    }
	
	/**
	 * Update the UI stuff...
	 */
	public void updatePreferences() {
		if (DEBUG) System.out.println( "InputPane.updatePreferences()");

		editor.setFont( TextPreferences.getBaseFont());

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

		editor.setAntialiasing( TextPreferences.isAntialiasing());
		editor.setTabSize( TextPreferences.getTabSize());
		
		String activeConfig = props.getKeyPreferences()
        .getActiveConfiguration();
		setKeyMappings(activeConfig);
	}
	
	private void setAttributes( int id, String property) {
		FontType type = TextPreferences.getFontType( property);
		editor.setAttributes( id, type.getColor(), type.getStyle());
	}

	public void search( String search, boolean regExp, boolean matchCase, boolean matchWord, boolean down, boolean wrap) {
		editor.search( null, search, regExp, matchCase, matchWord, down, wrap);
	}
	
	public void collapseAll() {
		foldingMargin.foldAll();
	}

	public void expandAll() {
		foldingMargin.unfoldAll();
	}

	public void setFocus() {
		editor.requestFocus();
	}

	public void gotoLine( int line) {
		editor.gotoLine( line);
	}

	public Action getEnableAllBreakpointsAction() {
		return view.getEnableAllBreakpointsAction();
	}
	
	public Action getDisableAllBreakpointsAction() {
		return view.getDisableAllBreakpointsAction();
	}

	public Action getRemoveAllBreakpointsAction() {
		return view.getRemoveAllBreakpointsAction();
	}

	public Action getCloseInputPaneAction() {
		return view.getCloseInputPaneAction();
	}

	public boolean isMultipleLineTagStart( int line) throws BadLocationException {
		XmlDocument doc = (XmlDocument)editor.getDocument();
		return doc.isMultipleLineTagStart( line);
	}
	
	public void updateMargins() {
		margin.revalidate();
		margin.repaint();

		breakpointMargin.revalidate();
		breakpointMargin.repaint();

		overviewMargin.revalidate();
		overviewMargin.repaint();
	}

	/**
	 * Sets the breakpoints. 
	 * 
	 * @param lineNumber the line number.
	 */
	public void setBreakpoints( BreakpointList breakpoints) {
		if (DEBUG) System.out.println( "InputPane.setBreakpoints( "+breakpoints+")");
		
		this.breakpoints = breakpoints;

		editor.requestFocus();
		view.update( this);

		updateMargins();
		
	}

	/**
	 * Adds a breakpoint, to the list of breakpoints, will update 
	 * the GUI accordingly.
	 * 
	 * @param lineNumber the line number.
	 */
	public void addBreakpoint( Breakpoint breakpoint) {
		if (DEBUG) System.out.println( "InputPane.addBreakpoint( "+breakpoint+")");

		breakpoints.addBreakpoint( breakpoint);

		updateMargins();
	}

	/**
	 * Sets a breakpoint, will create a new breakpoint if no breakpoint 
	 * exist for the line, otherwise it will toggle the breakpoint already 
	 * there.
	 * 
	 * @param lineNumber the line number.
	 */
	public void setBreakpoint( int lineNumber) {
		if (DEBUG) System.out.println( "InputPane.setBreakpoint( "+lineNumber+")");
		
		Breakpoint breakpoint = breakpoints.getBreakpoint( getSourceLocation(), lineNumber+1);
		
		if ( breakpoint != null) {
			breakpoint.toggle();
			updateMargins();
		} else {
			// could not find breakpoint, creat a new one.
			addBreakpoint( new Breakpoint( location, lineNumber+1, true));
		}
		
		editor.requestFocus();
		view.update( this);
	}

	// disables/enables the break point.
	public void toggleBreakpoint() {
		try {
			Element root = editor.getDocument().getDefaultRootElement();
			int line = root.getElementIndex( editor.getCaretPosition());
			
			Breakpoint breakpoint = breakpoints.getBreakpoint( getSourceLocation(), line+1);
			
			if ( breakpoint != null) {
				breakpoint.toggle();
				updateMargins();
			
				editor.requestFocus();
				view.update( this);
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
	}
	
	private int currentStartTagPos = -1;
	private Tag currentStartTag = null;
	private Tag currentEndTag = null;

	private URL url;

	public Tag getCurrentStartTag() {
		return getCurrentStartTag( editor.getCaretPosition());
	}
	
	private Tag getCurrentStartTag( int pos) {
		
		if ( pos != currentStartTagPos) {
			XmlDocument doc = (XmlDocument)editor.getDocument();
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
			XmlDocument doc = (XmlDocument)editor.getDocument();
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
	
	// removes/adds the break point.
	public void setBreakpoint() {
		try {
			Element root = editor.getDocument().getDefaultRootElement();
			int line = root.getElementIndex( editor.getCaretPosition());
			
			Breakpoint breakpoint = breakpoints.getBreakpoint( getSourceLocation(), line+1);
			
			if ( breakpoint != null) {
				breakpoints.removeBreakpoint( getSourceLocation(), line+1);
			} else {
				addBreakpoint( new Breakpoint( location, line+1, true));
			}

			updateMargins();
		
			editor.requestFocus();
			view.update( this);
		} catch( Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Returns true if a breakpoint exists for this line.
	 * 
	 * @param lineNumber the line number.
	 * 
	 * @return true if a breakpoint exists.
	 */
	public boolean isBreakpoint( int lineNumber) {
		if (DEBUG) System.out.println( "InputPane.isBreakpoint( "+lineNumber+")");

		return breakpoints.isBreakpoint( getSourceLocation(), lineNumber+1);
	}

	/**
	 * Returns a breakpoint for this line, null if no breakpoint exists.
	 * 
	 * @param lineNumber the line number.
	 * 
	 * @return the breakpoint.
	 */
	public Breakpoint getBreakpoint( int lineNumber) {
		if (DEBUG) System.out.println( "InputPane.getBreakpoint( "+lineNumber+")");

		return breakpoints.getBreakpoint( getSourceLocation(), lineNumber+1);
	}

	/**
	 * Removes a breakpoint for the line, if this breakpoint exists, 
	 * otherwise it does nothing.
	 * 
	 * @param lineNumber the line number.
	 */
	public void removeBreakpoint( int lineNumber) {
		if (DEBUG) System.out.println( "InputPane.removeBreakpoint( "+lineNumber+")");

		breakpoints.removeBreakpoint( getSourceLocation(), lineNumber+1);
		updateMargins();

		editor.requestFocus();
		view.update( this);
	}
	
	/**
	 * Gets line the indicated by the offset.
	 *
	 * @param off the screen offset.
	 * 
	 * @return the line indicated by the offset.
	 */
	public int getLineForOffset( int off) {
		if (DEBUG) System.out.println( "InputPane.getLineForOffset( "+off+")");

		int pos = editor.viewToModel( new Point( 0, off));
		
		if ( pos >= 0) {
			Element root = editor.getDocument().getDefaultRootElement();
			return root.getElementIndex( pos);
		}
		
		return -1;
	}

	/**
	 * Gets the screen offset for the line.
	 * 
	 * @param line the line to get the offset for.
	 *
	 * @return the number of soft lines in the line.
	 */
	public int getOffsetForLine( int lineNumber) throws BadLocationException {
		if (DEBUG) System.out.println( "InputPane.getOffsetForLine( "+lineNumber+")");

		Element root = editor.getDocument().getDefaultRootElement();
		Element line = root.getElement( lineNumber);
		
		return editor.modelToView( line.getStartOffset()).y;
	}

	public void setSelectedLine( int selectedLine) {
		if (DEBUG) System.out.println( "InputPane.setSelectedLine( "+selectedLine+")");

		this.selectedLine = selectedLine;
	}

	public int getSelectedLine() {
		if (DEBUG) System.out.println( "InputPane.getSelectedLine()");

		return selectedLine;
	}
	
	public void enableAllBreakpoints() {
		Vector breakpoints = getBreakpoints();
		
		for ( int i = 0; i < breakpoints.size(); i++) {
			((Breakpoint)breakpoints.elementAt(i)).setEnabled( true);
		}

		updateMargins();

		view.update( this);
	}

	public void disableAllBreakpoints() {
		Vector breakpoints = getBreakpoints();
		
		for ( int i = 0; i < breakpoints.size(); i++) {
			((Breakpoint)breakpoints.elementAt(i)).setEnabled( false);
		}

		updateMargins();

		view.update( this);
	}

	public void removeAllBreakpoints() {
		Vector breakpoints = getBreakpoints();
		
		for ( int i = 0; i < breakpoints.size(); i++) {
			this.breakpoints.removeBreakpoint( (Breakpoint)breakpoints.elementAt(i));
		}

		updateMargins();

		view.update( this);
	}

	/**
	 * Opens the document at the location provided.
	 * 
	 * @param location the path to the document.
	 */
	public void open( String path) throws IOException {
		url = URLUtilities.toURL( path);
		location = url.toString();
		
//		try {
			String text = XMLUtilities.getText( url);
			editor.setText( text);
			editor.setCaretPosition( 0);
			
			this.setInitialVersionOfDocument(text);
			
			//fix by Thomas Curley
			//wasnt calculating the positions in the document correctly for
			//find and goto because it was reading the above setText as an
			//update.
			((XmlDocument)editor.getDocument()).resetUpdates();
			
			
//		} catch ( IOException e) {
//			MessageHandler.showError( view.getFrame(), "Could not open file \""+path+"\".", e, "File Error");
//			e.printStackTrace();
//		}
	}
	
	public void reload() throws IOException {
		open( location);
	}
	
	public void save() throws SAXParseException, IOException {
		
		ExchangerDocument xngrDoc = new ExchangerDocument(this.getSourceURL());
		xngrDoc.setText(getEditor().getText());
		xngrDoc.save();
	}
	
	public void save(URL newUrl) throws SAXParseException, IOException {
		
		ExchangerDocument xngrDoc = new ExchangerDocument(this.getSourceURL());
		xngrDoc.setText(getEditor().getText());
		xngrDoc.setURL(newUrl);
		xngrDoc.save();
	}

	/**
	 *  Get the location of the current source/file.
	 * 
	 * @return the location of the source/file.
	 */
	public String getSourceLocation() {
		if (DEBUG) System.out.println( "InputPane.getSourceLocation() ["+location+"]");
		return location;
	}
	
	/**
	 *  Get the location of the current source/file.
	 * 
	 * @return the location of the source/file.
	 */
	public URL getSourceURL() {
		if (DEBUG) System.out.println( "InputPane.getSourceURL() ["+url+"]");
		return url;
	}
	
	/**
	 * get the name of the source/file.
	 * 
	 * @return the name of the source/file.
	 */
	public String getSourceName() {
		if (DEBUG) System.out.println( "InputPane.getSourceName()");

		return URLUtilities.getFileName( location);
	}

	/**
	 * get the name of the source/file.
	 * 
	 * @return the name of the source/file.
	 */
	public Icon getIcon() {
		if ( hasBreakpoints()) {
			if ( hasEnabledBreakpoints()) {
				return getEnabledBreakpointsIcon();
			} else {
				return getDisabledBreakpointsIcon();
			}
		} else {
			return getNormalIcon();
		}
	}
	
	public Vector getBreakpoints() {
		Vector result = new Vector();
		Vector bps = breakpoints.getBreakpoints();
		
		for ( int i = 0; i < bps.size(); i++) {
			Breakpoint bp = (Breakpoint)bps.elementAt(i);

			if ( bp.sameFile( getSourceLocation())) {
				result.add( bp);
			}
		}

		return result;
	}

	public boolean hasBreakpoints() {
		Vector bps = breakpoints.getBreakpoints();
		
		for ( int i = 0; i < bps.size(); i++) {
			Breakpoint bp = (Breakpoint)bps.elementAt(i);

			if ( bp.sameFile( getSourceLocation())) {
				return true;
			}
		}

		return false;
	}
	
	public boolean hasEnabledBreakpoints() {
		Vector bps = breakpoints.getBreakpoints();
		
		for ( int i = 0; i < bps.size(); i++) {
			Breakpoint bp = (Breakpoint)bps.elementAt(i);

			if ( bp.sameFile( getSourceLocation()) && bp.isEnabled()) {
				return true;
			}
		}

		return false;
	}

	private Icon getNormalIcon() {
		if ( normalIcon == null) {
			String extension = "xml";
			String name = getSourceName();
			int index = name.lastIndexOf( '.');
			
			if ( index != -1) {
				extension = name.substring( index + 1, name.length());
			}

			normalIcon = new QIcon( IconFactory.getIconForExtension( extension));
		}
		
		return normalIcon;
	}

	public int getLines() {
		return editor.getLines();
	}

	public void gotoCursor() {
		try{
			Rectangle pos = editor.modelToView( editor.getCaretPosition());
			editor.scrollRectToVisible( pos);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	public int getCursorLine() {
		Element root = editor.getDocument().getDefaultRootElement();
		return root.getElementIndex( editor.getCaretPosition());
	}

	private Icon getEnabledBreakpointsIcon() {
		if ( enabledBreakpointsIcon == null) {
			String extension = "xml";
			String name = getSourceName();
			int index = name.lastIndexOf( '.');
			
			if ( index != -1) {
				extension = name.substring( index + 1, name.length());
			}

			enabledBreakpointsIcon = new QIcon( IconFactory.getIconForExtension( extension));
			enabledBreakpointsIcon.addOverlayIcon( ENABLED_BREAKPOINT_ICON, QIcon.SOUTH_EAST);
		}
		
		return enabledBreakpointsIcon;
	}

	private Icon getDisabledBreakpointsIcon() {
		if ( disabledBreakpointsIcon == null) {
			String extension = "xml";
			String name = getSourceName();
			int index = name.lastIndexOf( '.');
			
			if ( index != -1) {
				extension = name.substring( index + 1, name.length());
			}

			disabledBreakpointsIcon = new QIcon( IconFactory.getIconForExtension( extension));
			disabledBreakpointsIcon.addOverlayIcon( DISABLED_BREAKPOINT_ICON, QIcon.SOUTH_EAST);
		}
		
		return disabledBreakpointsIcon;
	}

	/**
	 * Check to see wether the current source is the same 
	 * as the one indicated by the location.
	 * 
	 * @return true when the current location is the source.
	 */
	public boolean isCurrentSource( String path) {
		if (DEBUG) System.out.println( "InputPane.isCurrentSource( "+path+") {"+location+"}");

		URL url = URLUtilities.toURL( path);
		String location = URLUtilities.encodeURL( this.location);
		
		if ( url != null) {
			String file = URLUtilities.encodeURL( url.getFile());
		
			if ( location.endsWith( file)) {
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Opens the document described by the location if not already 
	 * opened and selects the line.
	 * 
	 * @param line the line in the document to select.
	 * @param column the columnNumber in the line to select.
	 */
	public void select( int lineNumber, int columnNumber, String qname, boolean startTag) {
		if (DEBUG1) System.out.println( "InputPane.select( "+lineNumber+", "+columnNumber+", "+qname+")");

		editor.requestFocus();

		XmlDocument doc = (XmlDocument)editor.getDocument();
		
		Element root = doc.getDefaultRootElement();
		
		if ( root.getElementCount() >= lineNumber) {

			if ( lineNumber <= 0) {
				lineNumber = 1;
			}

			Element line = root.getElement( lineNumber-1);
			int start = line.getStartOffset();
			int end = line.getEndOffset();

			if ( start < 0) {
				start = 0;
			}
			
			start = start+columnNumber;
			
			if ( !StringUtilities.isEmpty( qname)) {
				try {
					String text = doc.getText( start, end-start);
					int index = text.indexOf( qname);

					if ( index != -1) {
						start = start + index;
					}
				} catch ( Exception e) {}
			}
			
			Tag tag = doc.getCurrentTag( start);
			
			if ( tag == null) {
				tag = doc.getNextTag( start);
			}
			
			if (tag != null && !startTag && !(tag.getType() == Tag.EMPTY_TAG)) {
				tag = doc.getEndTag( tag);
			}
			
			if ( tag != null) {
				editor.setCaretPosition( tag.getEnd());
				editor.moveCaretPosition( tag.getStart());
			}
		} else {
			System.err.println( "ERROR: line number ["+lineNumber+"] not available!");
		}

		editor.requestFocus();
	}

	public void selectLine( int lineNumber) {
		if (DEBUG) System.out.println( "InputPane.select( "+lineNumber+")");

		editor.requestFocus();
		editor.selectLine( lineNumber);
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
			
			editor.setCaretPosition( pos);
		}
		
		
		updatePreferences();
	}
	
	public void setShowFoldingMargin( boolean visible) {
		foldingMargin.setVisible( visible);
	}

	public void setShowLinenumberMargin( boolean visible) {
		margin.setVisible( visible);
	}

	public void setShowOverviewMargin( boolean visible) {
		overviewMargin.setVisible( visible);
	}

	public void selectLineWithoutEnd( int line) {
		if (DEBUG) System.out.println( "InputPane.select( "+line+")");

		editor.requestFocus();

		if ( line > 0) {
			Element root = editor.getDocument().getDefaultRootElement();
			Element elem = root.getElement( line-1);

			if ( elem != null) {
				int start = elem.getStartOffset();
				int end = elem.getEndOffset()-1;
				
				editor.select( start, end);
			}
		}
	}

		
	private class XmlCaret extends DefaultCaret {
		private boolean initialFocus = false;
		private boolean focus = false;
		private boolean visible = false;
		private XmlEditorPane editor = null;
	
		public XmlCaret( XmlEditorPane editor) {
			this.editor = editor;
//			Thomas Curley 24.08.05
			//if making the debugger editable - set blink rate to 500
			setBlinkRate( 500);
		}

	    // Always leave the selection highlighted
    	public void setSelectionVisible( boolean whatever) {
        	super.setSelectionVisible( true);
	    }

		// Always leave the caret visible ...
		public boolean isVisible() {
			return initialFocus;
		}
		
		public void focusGained(FocusEvent e) { 
			if ( view != null) {
				focus = true;

				view.setFocus( InputPane.this, true);
				initialFocus = true;
				super.focusGained( e);
			}
		}
	
		public void focusLost(FocusEvent e) {
			if ( view != null) {
				//view.setFocus( InputPane.this, false);
				focus = false;
			}
		}
		
		//Thomas Curley 24.08.05
		//if making the debugger editable - no need for this block caret
		/*public void paint( Graphics g) {
			if ( isVisible()) {
				TextUI mapper = editor.getUI();
				try {
					Rectangle r = mapper.modelToView( editor, getDot()-1, Position.Bias.Forward);
					Rectangle r2 = mapper.modelToView( editor, getDot(), Position.Bias.Forward);
					char character = ((XmlDocument)editor.getDocument()).getLastCharacter( getDot());
					
					if ( r != null && r2 != null && r.x < r2.x && getDot() == getMark() && character != '\t') {
			            if ((r == null) || ((r.width == 0) && (r.height == 0))) {
			                return;
			            }
						
			            if (width > 0 && height > 0 && !this._contains( r.x, r.y, r.width, r.height)) {
			                // We seem to have gotten out of sync and no longer
			                // contain the right location, adjust accordingly.
			                Rectangle clip = g.getClipBounds();
			
			                if (clip != null && !clip.contains(this)) {
			                    // Clip doesn't contain the old location, force it
			                    // to be repainted lest we leave a caret around.
			                    repaint();
			                }
			                // This will potentially cause a repaint of something
			                // we're already repainting, but without changing the
			                // semantics of damage we can't really get around this.
			                damage(r);
			            }
		
						r.width = r2.x - r.x;
	
						g.setColor( editor.getCaretColor());
						g.fillRect(r.x, r.y, r.width -1, r.height - 1);
						
						FontMetrics fm = g.getFontMetrics();
						g.setColor( editor.getBackground());
						g.drawChars( new char[] { character }, 0, 1, r.x, r.y + fm.getAscent());
					} else {
						super.paint( g);
					}

					// *****
					//  ***
					//   *
//					g.drawLine(r.x, r.y, r.x+5, r.y);
//					g.drawLine(r.x+1, r.y +1, r.x+4, r.y +1);
//					g.drawLine(r.x+2, r.y +2, r.x+3, r.y + 2);

					//   *
					//  ***
					// *****
//					g.drawLine(r.x, r.y + r.height -1, r.x+5, r.y + r.height -1);
//					g.drawLine(r.x+1, r.y + r.height -2, r.x+4, r.y + r.height -2);
//					g.drawLine(r.x+2, r.y + r.height -3, r.x+3, r.y + r.height -3);
				} catch (BadLocationException e) {
				}
			}
		}*/

		private boolean _contains(int X, int Y, int W, int H) {
		    int w = this.width;
		    int h = this.height;
		    if ((w | h | W | H) < 0) {
		        // At least one of the dimensions is negative...
		        return false;
		    }
		    // Note: if any dimension is zero, tests below must return false...
		    int x = this.x;
		    int y = this.y;
		    if (X < x || Y < y) {
		        return false;
		    }
		    if (W > 0) {
		        w += x;
		        W += X;
		        if (W <= X) {
		            // X+W overflowed or W was zero, return false if...
		            // either original w or W was zero or
		            // x+w did not overflow or
		            // the overflowed x+w is smaller than the overflowed X+W
		            if (w >= x || W > w) return false;
		        } else {
		            // X+W did not overflow and W was not zero, return false if...
		            // original w was zero or
		            // x+w did not overflow and x+w is smaller than X+W
		            if (w >= x && W > w) return false;
		        }
		    }
		    else if ((x + w) < X) {
		        return false;
		    }
		    if (H > 0) {
		        h += y;
		        H += Y;
		        if (H <= Y) {
		            if (h >= y || H > h) return false;
		        } else {
		            if (h >= y && H > h) return false;
		        }
		    }
		    else if ((y + h) < Y) {
		        return false;
		    }
		    return true;
		}
	}
	
	public void popupTriggered( MouseEvent e) {
		
		setSelectedLine( getLineForOffset( e.getY()));
		
		if ( popup == null) {
			popup = new JPopupMenu();
		}
		
		popup.removeAll();

		Breakpoint breakpoint = getBreakpoint( getSelectedLine());
		
		if ( breakpoint == null) {
			popup.add( getAddBreakpointAction());
		} else if ( breakpoint.isEnabled()) {
			popup.add( getDisableBreakpointAction());
			popup.addSeparator();
			popup.add( getRemoveBreakpointAction());
		} else { // disabled
			popup.add( getEnableBreakpointAction());
			popup.addSeparator();
			popup.add( getRemoveBreakpointAction());
		}
		
		if ( hasBreakpoints()) {
			popup.addSeparator();
			popup.add( getDisableAllBreakpointsAction());
			popup.add( getEnableAllBreakpointsAction());
			popup.addSeparator();
			popup.add( getRemoveAllBreakpointsAction());
		}

		popup.addSeparator();
		popup.add( getCloseInputPaneAction());

		GUIUtilities.alignMenu( popup);

		popup.show( (Component)e.getSource(), e.getX(), e.getY());
	}
	
	public AddBreakpointAction getAddBreakpointAction() {
		if ( addBreakpoint == null) {
			addBreakpoint = new AddBreakpointAction( this);
		}
		
		return addBreakpoint;
	}
	
	public RemoveBreakpointAction getRemoveBreakpointAction() {
		if ( removeBreakpoint == null) {
			removeBreakpoint = new RemoveBreakpointAction( this);
		}
		
		return removeBreakpoint;
	}

	public DisableBreakpointAction getDisableBreakpointAction() {
		if ( disableBreakpoint == null) {
			disableBreakpoint = new DisableBreakpointAction( this);
		}
		
		return disableBreakpoint;
	}

	public EnableBreakpointAction getEnableBreakpointAction() {
		if ( enableBreakpoint == null) {
			enableBreakpoint = new EnableBreakpointAction( this);
		}
		
		return enableBreakpoint;
	}

	public void cleanup() {
		editor.cleanup();
		
		editor = null;
		location = null;
		breakpoints = null;
		selectedLine = -1;
		scroller = null;
		view = null;

		normalIcon = null;
		enabledBreakpointsIcon = null;
		disabledBreakpointsIcon = null;
	}

	/**
	 * @return
	 */
	public XmlEditorPane getEditor() {

		// TODO Auto-generated method stub
		return (editor);
	}

	

	
    
    /**
     * Sets the position of the cursor.
     *
     * @param pos the new cursor position.
     */
    public void setCursorPosition(int pos) {

        int length = getEditor().getDocument().getLength();
        if (pos < 0) {
            pos = 0;
        }
        else if (pos >= length && length > 0) {
            pos = length - 1;
        }
        getEditor().setCaretPosition(pos);
    }

    int previousFoldLines = 0;

    private void updateFolds() {

        if (getLines() != previousFoldLines) {
            previousFoldLines = getLines();
            getEditor().getFoldingMargin().checkFolds();            
        }
    }

    
    public boolean isKeyReleased() {

        return keyReleased;
    }

    private void updatePositionalStuff() {

        if (getEditor().hasFocus()) {
            if (caretUpdated && keyReleased) {
                caretUpdated = false;
            }
            else {
                updatePosition();
            }
        }
    }
    
    private void updatePosition() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Element root = doc.getDefaultRootElement();
        int pos = getEditor().getCaretPosition();
        int line = root.getElementIndex(pos);
        int start = root.getElement(line).getStartOffset();
        int col = pos - start;
        String text = "";
        try {
            text = getEditor().getText(start, pos - start);
        }
        catch (BadLocationException b) {
        }
        int index = text.indexOf('\t');
        while (index != -1) {
            col = col + (TextPreferences.getTabSize() - 1);
            index = text.indexOf('\t', index + 1);
        }
        //parent.getStatusbar().setPosition(line + 1, col + 1);
    }

	
	/**
	 * @return Returns the initialVersionOfDocument.
	 */
	public String getInitialVersionOfDocument() {
	
		return initialVersionOfDocument;
	}

	
	/**
	 * @param initialVersionOfDocument The initialVersionOfDocument to set.
	 */
	public void setInitialVersionOfDocument(String initialVersionOfDocument) {
	
		this.initialVersionOfDocument = initialVersionOfDocument;
	}

	public boolean hasDocumentChanged() {
		
		if(this.getInitialVersionOfDocument() != null) {
			String oldText = this.getInitialVersionOfDocument();
			String newText = this.getEditor().getText();
			
			if(oldText.equals(newText)) {
				return(false);
			}
			else {
				return(true);
			}
		}
		else {
			return(false);
		}
	}

	
	/**
	 * @return Returns the view.
	 */
	public InputView getView() {
	
		return view;
	}

	
	/**
	 * @param view The view to set.
	 */
	public void setView(InputView view) {
	
		this.view = view;
	}
	
}
