/*
 * $Id: OutputPane.java,v 1.10 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
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
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;

import org.bounce.QIcon;
import org.bounce.event.DoubleClickListener;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Constants;
import com.cladonia.xml.editor.Margin;
import com.cladonia.xml.editor.ScrollableEditorPanel;
import com.cladonia.xml.editor.Tag;
import com.cladonia.xml.editor.XmlDocument;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.properties.Keystroke;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * A debuggers view, showing the XML content.
 * 
 * @version	$Revision: 1.10 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
public class OutputPane extends JPanel {
	private static final boolean DEBUG = false;
	
	private static final ImageIcon ENABLED_BREAKPOINT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointEnabledIcon.gif");
	private static final ImageIcon DISABLED_BREAKPOINT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xslt/debugger/ui/icons/BreakpointDisabledIcon.gif");

	private XmlEditorPane editor = null;
	private String location = null;
	private int selectedLine = -1;
//	private EditorMarginPanel marginPanel = null;
	private JScrollPane scroller = null;
	private OutputView view = null;
	private OutputStream outputStream = null;

	private Margin margin	= null;

	private QIcon normalIcon = null;
	private QIcon enabledBreakpointsIcon = null;
	private QIcon disabledBreakpointsIcon = null;

	private AbstractAction tabAction;

	private AbstractAction unindentAction;

	private AbstractAction selectAll;

	private ConfigurationProperties props;

	private String downKey;

	private String upKey;

	private String enterKey;

	private String escapeKey;
	
	/**
	 * Construct a view.
	 */
	public OutputPane( OutputView view, String location, ConfigurationProperties props) {
		super( new BorderLayout());
		
		if (DEBUG) System.out.println( "InputPane()");
		
		this.view = view;
		this.location = location;
		this.props = props;
		
		editor = new XmlEditorPane( false);
		editor.setCaret( new XmlCaret( editor));
		editor.setEditable( true);
		editor.setCaretPosition( 0);
		
//		Thomas Curley 24.08.05
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
        
        editor.getActionMap().put("xngr-indent", tabAction);
        editor.getActionMap().put("xngr-unindent", unindentAction);
        
		margin = new Margin( editor);
		margin.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				int line = OutputPane.this.getLineForOffset( e.getY());

				editor.selectLine( line);
			}
		});
		
		scroller = new JScrollPane( new ScrollableEditorPanel( editor));
		scroller.getViewport().setPreferredSize( new Dimension( 100, 100));

		JPanel corner = new JPanel();
		corner.setBorder( new CompoundBorder( 
				new MatteBorder( 1, 0, 0, 1, UIManager.getColor("controlDkShadow")), 
				new MatteBorder(1, 0, 0, 0, Color.white) ));
		scroller.setCorner( JScrollPane.LOWER_LEFT_CORNER, corner);

		JPanel marginPanel = new JPanel( new BorderLayout());
		marginPanel.add( margin, BorderLayout.CENTER);
		scroller.setRowHeaderView( marginPanel);
		
		add( scroller, BorderLayout.CENTER);

		updatePreferences();
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
	
	/**
	 * Update the UI stuff...
	 */
	/*public void updatePreferences() {
		if (DEBUG) System.out.println( "InputPane.updatePreferences()");

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

		editor.setAntialiasing( TextPreferences.isAntialiasing());
		editor.setTabSize( TextPreferences.getTabSize());
	}*/
	

	private void setAttributes( int id, String property) {
		FontType type = TextPreferences.getFontType( property);
		editor.setAttributes( id, type.getColor(), type.getStyle());
	}

	public void setWrapping( boolean wrap) {
		if ( editor.isWrapped() != wrap) {
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

	public void setShowLinenumberMargin( boolean visible) {
		margin.setVisible( visible);
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
	
	public OutputStream getOutputStream() {
		if ( outputStream == null) { 
			outputStream = new LogOutputStream();
		}
		
		return outputStream;
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
		return getNormalIcon();
	}
	
	public int getLineForOffset( int off) {
		if (DEBUG) System.out.println( "OutputPane.getLineForOffset( "+off+")");

		int pos = editor.viewToModel( new Point( 0, off));
		
		if ( pos >= 0) {
			Element root = editor.getDocument().getDefaultRootElement();
			return root.getElementIndex( pos);
		}
		
		return -1;
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

	/**
	 * Check to see wether the current source is the same 
	 * as the one indicated by the location.
	 * 
	 * @return true when the current location is the source.
	 */
	public boolean isCurrentSource( String path) {
		if (DEBUG) System.out.println( "InputPane.isCurrentSource( "+path+")");

		URL url = URLUtilities.toURL( path);
		
		if ( url != null) {
			String file = url.getFile();
		
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
	public void select( int lineNumber, int columnNumber, boolean startTag) {
		if (DEBUG) System.out.println( "InputPane.select( "+lineNumber+", "+columnNumber+")");

		editor.requestFocus();

		XmlDocument doc = (XmlDocument)editor.getDocument();
		
		Element root = doc.getDefaultRootElement();
		
		if ( root.getElementCount() >= lineNumber) {

			if ( lineNumber <= 0) {
				lineNumber = 1;
			}

			Element line = root.getElement( lineNumber-1);
			int start = line.getStartOffset();
//			int end = line.getEndOffset();

			if ( start < 0) {
				start = 0;
			}
			
			start = start+columnNumber;

			Tag tag = doc.getCurrentTag( start);
			
			if ( tag == null) {
				tag = doc.getNextTag( start);
			}
			
			if ( !startTag) {
				tag = doc.getEndTag( tag);
			}
			
			if ( tag != null) {
				editor.select( tag.getStart(), tag.getEnd());
			}
		} else {
			System.err.println( "ERROR: line number ["+lineNumber+"] not available!");
		}
		((XmlDocument)editor.getDocument()).resetUpdates();
		editor.requestFocus();
	}
	
	public void gotoLine( int line) {
		editor.gotoLine( line);
	}

	public void selectLine( int lineNumber) {
		if (DEBUG) System.out.println( "InputPane.select( "+lineNumber+")");

		editor.requestFocus();
		editor.selectLine( lineNumber);
	}
	
	private class XmlCaret extends DefaultCaret {
		private boolean initialFocus = false;
		private boolean focus = false;
		private boolean visible = false;
		private XmlEditorPane editor = null;
	
		public XmlCaret( XmlEditorPane editor) {
			this.editor = editor;
			setBlinkRate( 0);
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
			focus = true;
			initialFocus = true;
			super.focusGained( e);
		}
	
		public void focusLost(FocusEvent e) {
			focus = false;
		}
		
		/*public void paint( Graphics g) {
			if ( isVisible()) {
				TextUI mapper = editor.getUI();
				try {
					Rectangle r = mapper.modelToView( editor, getDot(), Position.Bias.Forward);
					Rectangle r2 = mapper.modelToView( editor, getDot()+1, Position.Bias.Forward);
					char character = ((XmlDocument)editor.getDocument()).getLastCharacter( getDot()+1);
					
					if ( r.x < r2.x && getDot() == getMark() && character != '\t') {
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
	
	public void reset() {
		try {
			editor.setText( "");
			((XmlDocument)editor.getDocument()).resetUpdates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void append( String text) {
		try {
			editor.getDocument().insertString( editor.getDocument().getLength(), text, null);
			((XmlDocument)editor.getDocument()).resetUpdates();
		} catch ( Exception e) {
			e.printStackTrace();
		}

		editor.gotoLine( editor.getLines());
		view.select( this);
	}

	private void appendText( final String text) {
		try {
			if ( SwingUtilities.isEventDispatchThread()) {
				append( text);
			} else {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						append( text);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cleanup() {
		editor.cleanup();
		
		location = null;
		selectedLine = -1;

		scroller = null;
		view = null;

		try {
			outputStream.close();
		} catch ( Exception e) {}

		normalIcon = null;
		enabledBreakpointsIcon = null;
		disabledBreakpointsIcon = null;
		
		editor = null;
	}
	
	class LogOutputStream extends OutputStream {
		private StringBuffer buffer = new StringBuffer();
		
		public void write(int b) {
//			System.out.print( "'"+(char)b+"'");
//			appendText( ""+(char)b);
			buffer.append( (char)b);
		}
		
		public void flush () {
//			System.out.println( "LogOutputStream.flush()");

			if ( buffer.length() > 0) {
//				System.out.print( "\""+buffer.toString()+"\"");
				appendText( buffer.toString());
				buffer = new StringBuffer();
			}
		}
	}
	
	public void save(URL newUrl) throws SAXParseException, IOException {
		
		ExchangerDocument xngrDoc = new ExchangerDocument(getEditor().getText());
		xngrDoc.setURL(newUrl);
		xngrDoc.save();
	}

	public void search( String search, boolean regExp, boolean matchCase, boolean matchWord, boolean down, boolean wrap) {
		editor.search( null, search, regExp, matchCase, matchWord, down, wrap);
	}
	
	/**
	 * @return Returns the editor.
	 */
	public XmlEditorPane getEditor() {
	
		return editor;
	}

	
	/**
	 * @param editor The editor to set.
	 */
	public void setEditor(XmlEditorPane editor) {
	
		this.editor = editor;
	}

	/**
	 * 
	 */
	public void setFocus() {

		editor.requestFocus();
		
	}
}
