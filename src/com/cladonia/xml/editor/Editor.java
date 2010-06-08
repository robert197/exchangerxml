/*
 * $Id: Editor.java,v 1.73 2005/08/09 14:32:31 tcurley Exp $
 *
 * Copyright (C) 2003 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xml.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;
import org.dom4j.Namespace;
import org.dom4j.Node;
import org.xml.sax.SAXParseException;
import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.AttributeValue;
import com.cladonia.schema.ElementInformation;
import com.cladonia.schema.SchemaDocument;
import com.cladonia.xml.CommonEntities;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerDocumentEvent;
import com.cladonia.xml.ExchangerDocumentListener;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLError;
import com.cladonia.xngreditor.ErrorList;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.ViewPanel;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.grammar.FragmentProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.properties.Keystroke;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The editor for an eXchaNGeR document.
 *
 * @version	$Revision: 1.73 $, $Date: 2005/08/09 14:32:31 $
 * @author Dogsbay
 */
public class Editor extends ViewPanel implements CaretListener,
        UndoableEditListener, DocumentListener, ExchangerDocumentListener {

    private static final boolean DEBUG = false;
    private static final ImageIcon ICON = XngrImageLoader.get().getImage(
            "com/cladonia/xml/editor/icons/EditorIcon.gif");
    private static final String[] XSD_SIMPLE_TYPES = { "anyURI",
            "base64Binary", "boolean", "byte", "date", "dateTime", "decimal",
            "double", "duration", "ENTITIES", "ENTITY", "float", "gDay",
            "gMonth", "gMonthDay", "gYear", "gYearMonth", "hexBinary", "ID",
            "IDREF", "IDREFS", "int", "integer", "list", "long", "Name",
            "NCName", "negativeInteger", "NMTOKEN", "NMTOKENS",
            "nonNegativeInteger", "nonPositiveInteger", "normalizedString",
            "NOTATION", "positiveInteger", "QName", "short", "string", "time",
            "token", "union", "unsignedByte", "unsignedInt", "unsignedLong",
            "unsignedShort"};
    private static Vector declarationNames = null; // !DOCTYPE, !ELEMENT, !ATTLIST, !ENTITY, !NOTATION
    private static Vector elementDeclarationTypes = null; // #PCDATA, EMPTY, ANY 
    private static Vector entityDeclarationTypes = null; // SYSTEM, PUBLIC, NDATA
    private static Vector attlistDeclarationAll = null; // CDATA, NMTOKEN, NMTOKENS, ID, IDREF, IDREFS, ENTITY, ENTITIES, NOTATION
    private static Vector attlistDeclarationTypes = null; // CDATA, NMTOKEN, NMTOKENS, ID, IDREF, IDREFS, ENTITY, ENTITIES, NOTATION
    private static Vector attlistDeclarationDefaults = null; // #REQUIRED, #IMPLIED, #FIXED
    private static Vector doctypeDeclarationTypes = null; // SYSTEM, PUBLIC
    private static Vector notationDeclarationTypes = null; // SYSTEM, PUBLIC
    private Vector bookmarks = null;
    private Vector fragments = null;
    private boolean hasLatest = false;
    private boolean updateCaret = false;
    private PopupListener popupListener = null;
    private Pattern pattern = null;
    private ErrorList errors = null;
    private EditorPanel topEditor = null;
    private EditorPanel bottomEditor = null;
    private EditorPanel selectedEditor = null;
    private JToolBar fragmentToolbar = null;
    private JSplitPane split = null;
    private EditorProperties properties = null;
    private ConfigurationProperties props = null;
    private ExchangerEditor parent = null;
    private ExchangerView view = null;
    private JPopupMenu popup = null;
    private NameSelectionPopup selectionPopup = null;
    private static FragmentDialog fragmentDialog = null;
    private ExchangerDocument document = null;
    private Tag currentTag = null;
    private Tag parentStartTag = null;
    private Vector tagTree = null;
    private Vector globalElements = null;
    private Vector allElements = null;
    private Vector schemas = null;
    private Vector namespaces = null;
    private Vector elementNames = null;
    private Hashtable attributeNames = null;
    private Vector entityNames = null;
    private boolean insertHappened = false;
    private boolean caretUpdated = false;
    private boolean keyReleased = true;
    private AbstractAction selectAll = null;
    private AbstractAction tabAction = null;
    private AbstractAction unindentAction = null;
    private AbstractAction endTagAction = null;
    private AbstractAction startTagAction = null;
    private AbstractAction previousAttributeAction = null;
    private AbstractAction nextAttributeAction = null;
    private String downKey = null;
    private String upKey = null;
    private String enterKey = null;
    private String escapeKey = null;
    private Examiner examiner = null;
    private ValuesPopupListener valuesPopupListener = null;
    private AttributesPopupListener attributesPopupListener = null;
    private ElementsPopupListener elementsPopupListener = null;

    /**
     * Constructs an editor for the document.
     *
     * @param parent the ExchangerEditor component.
     * @param props the properties for the editor.
     */
    public Editor(ExchangerEditor _parent, ConfigurationProperties props,
            ExchangerView _view, ErrorList errors) {

        super(new BorderLayout());
        examiner = new Examiner();
        examiner.start();
        this.errors = errors;
        this.props = props;
        this.properties = props.getEditorProperties();
        this.parent = _parent;
        this.view = _view;
        popup = getPopupMenu();
        selectionPopup = new NameSelectionPopup(this);
        selectionPopup.addPropertyChangeListener(new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent e) {

                //System.out.println("PropertyChangeListener.propertyChange( "+e.getPropertyName()+")");
                if (selectionPopup.isVisible()) {
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
                            "tagcomplete-down");
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
                            "tagcomplete-up");
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                            .put(
                                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                                            0, false), "tagcomplete-enter");
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                            .put(
                                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                                            0, false), "tagcomplete-cancel");
                }
                else {
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                            KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false),
                            downKey);
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                            KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false),
                            upKey);
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                            .put(
                                    KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,
                                            0, false), enterKey);
                    getEditor().getInputMap(JComponent.WHEN_FOCUSED)
                            .put(
                                    KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
                                            0, false), escapeKey);
                }
            }
        });
        popupListener = new PopupListener() {

            public void popupTriggered(MouseEvent e) {

                showPopupMenu(e);
            }
        };
        tabAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source).indentSelectedText();
                }
            }
        };
        startTagAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source).gotoStartTag();
                }
            }
        };
        endTagAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source).gotoEndTag();
                }
            }
        };
        nextAttributeAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source).gotoNextAttributeValue();
                }
            }
        };
        previousAttributeAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source)
                            .gotoPreviousAttributeValue();
                }
            }
        };
        unindentAction = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source).unindentSelectedText();
                }
            }
        };
        // the select all action (needed for emacs keys)
        selectAll = new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                if (parent != null) {
                    if (parent.getView() != null) {
                        parent.getView().getEditor().getEditor().selectAll();
                        parent.getView().getEditor().setFocus();
                    }
                }
            }
        };
        bookmarks = new Vector();
        topEditor = createEditorPanel(false);
        topEditor.getEditor().setName("Top Panel");
        topEditor.setVisible(false);
        topEditor.setMinimumSize(new Dimension(0, 0));
        bottomEditor = createEditorPanel(true);
        bottomEditor.getEditor().setName("Bottom Panel");
        topEditor.setOther(bottomEditor);
        bottomEditor.setOther(topEditor);
        selectedEditor = bottomEditor;
        split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topEditor,
                bottomEditor);
        split.setResizeWeight(0);
        //		if ( split.getDividerSize() > 6) {
        split.setDividerSize(2);
        //		}
        Object ui = split.getUI();
        if (ui instanceof BasicSplitPaneUI) {
            ((BasicSplitPaneUI) ui).getDivider().setBorder(
                    new CompoundBorder(new MatteBorder(1, 0, 0, 0, UIManager
                            .getColor("controlDkShadow")), new MatteBorder(1,
                            0, 0, 0, Color.white)));
            ((BasicSplitPaneUI) ui).getDivider().addMouseListener(
                    new MouseAdapter() {

                        //			split.addMouseListener( new MouseAdapter() {
                        public void mouseReleased(MouseEvent event) {

                            if (!topEditor.isInitialised()) {
                                topEditor.initialise();
                            }
                            topEditor.setVisible(true);
                        }

                        public void mouseDragged(MouseEvent event) {

                            if (!topEditor.isInitialised()) {
                                topEditor.initialise();
                            }
                            topEditor.setVisible(true);
                        }

                        public void mousePressed(MouseEvent event) {

                            if (!topEditor.isInitialised()) {
                                topEditor.initialise();
                            }
                            topEditor.setVisible(true);
                        }
                    });
            ((BasicSplitPaneUI) ui).getDivider().addMouseListener(
                    new DoubleClickListener() {

                        public void doubleClicked(MouseEvent event) {

                            if (topEditor.isVisible()) {
                                topEditor.setVisible(false);
                            }
                        }
                    });
        }
        split.setBorder(null);
        split.setDividerLocation(0);
        add(getFragmentToolbar(), BorderLayout.WEST);
        add(split, BorderLayout.CENTER);
        updatePreferences();
    }

    private EditorPanel createEditorPanel(boolean init) {

        final EditorPanel editorPanel = new EditorPanel(this, props, init);
        final XmlEditorPane editor = editorPanel.getEditor();
        editor.setEditable(true);
        editor.addMouseListener(popupListener);
        editor.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent e) {

                if ((e.getKeyCode() == KeyEvent.VK_UP)
                        || (e.getKeyCode() == KeyEvent.VK_DOWN)
                        || (e.getKeyCode() == KeyEvent.VK_LEFT)
                        || (e.getKeyCode() == KeyEvent.VK_RIGHT)
                        || (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN)
                        || (e.getKeyCode() == KeyEvent.VK_PAGE_UP)) {
                    keyReleased = false;
                }
            }

            public void keyReleased(KeyEvent e) {

                keyReleased = true;
                updatePositionalStuff();
            }
        });
        editor.addFocusListener(new FocusListener() {

            public void focusGained(FocusEvent e) {

                //				System.out.println( "["+document.getName()+"] FocusListener.focusGained()");
                if (editor != null) {
                    selectedEditor = editorPanel;
                    if (parent != null) {
                        view.setFocussed();
                        updateHelper();
                    }
                }
            }

            public void focusLost(FocusEvent e) {

                //				System.out.println( "["+document.getName()+"] FocusListener.focusLost()");
            }
        });
        editor.setCaret(new XmlCaret());
        editor.addCaretListener(this);
        //		DropTarget target = new XMLDropTarget( editor.getDropTarget());
        //		editor.setDropTarget( target);
        // >>> Keymap...
        editor.getActionMap().put("tagcomplete-up", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                selectionPopup.selectPrevious();
            }
        });
        editor.getActionMap().put("tagcomplete-down", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                selectionPopup.selectNext();
            }
        });
        editor.getActionMap().put("tagcomplete-enter", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                insertCurrentSelectedName();
            }
        });
        editor.getActionMap().put("tagcomplete-cancel", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                hideSelectionPopup();
            }
        });
        editor.getActionMap().put("xngr-indent", tabAction);
        editor.getActionMap().put("xngr-unindent", unindentAction);
        editor.getActionMap().put("gotoStartTag", startTagAction);
        editor.getActionMap().put("gotoEndTag", endTagAction);
        editor.getActionMap().put("gotoPreviousAttributeValue",
                previousAttributeAction);
        editor.getActionMap()
                .put("gotoNextAttributeValue", nextAttributeAction);
        editor.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, Toolkit
                        .getDefaultToolkit().getMenuShortcutKeyMask(), false),
                "tagcomplete-popup");
        editor.getActionMap().put("tagcomplete-popup", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    ((EditorPanel.XmlEditor) source).promptText();
                }
            }
        });
        editor.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit
                        .getDefaultToolkit().getMenuShortcutKeyMask()
                        + InputEvent.SHIFT_MASK, false), "toggle-full-fold");
        editor.getActionMap().put("toggle-full-fold", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    int pos = ((EditorPanel.XmlEditor) source)
                            .getCaretPosition();
                    int line = ((EditorPanel.XmlEditor) source).getDocument()
                            .getDefaultRootElement().getElementIndex(pos);
                    ((EditorPanel.XmlEditor) source).getFoldingMargin()
                            .toggleFullFold(line);
                }
            }
        });
        editor.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, Toolkit
                        .getDefaultToolkit().getMenuShortcutKeyMask(), false),
                "toggle-fold");
        editor.getActionMap().put("toggle-fold", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                Object source = event.getSource();
                if (source instanceof EditorPanel.XmlEditor) {
                    int pos = ((EditorPanel.XmlEditor) source)
                            .getCaretPosition();
                    int line = ((EditorPanel.XmlEditor) source).getDocument()
                            .getDefaultRootElement().getElementIndex(pos);
                    ((EditorPanel.XmlEditor) source).getFoldingMargin()
                            .toggleFold(line);
                }
            }
        });
        editor.getInputMap().put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F6, Toolkit
                        .getDefaultToolkit().getMenuShortcutKeyMask(), false),
                "toggle-split");
        editor.getActionMap().put("toggle-split", new AbstractAction() {

            public void actionPerformed(ActionEvent event) {

                if (topEditor.getSize().height > 0
                        && bottomEditor.getSize().height > 0) {
                    if (selectedEditor == topEditor) {
                        bottomEditor.setFocus();
                    }
                    else {
                        topEditor.setFocus();
                    }
                }
                if (topEditor.getSize().height <= 5) {
                    if (!topEditor.isInitialised()) {
                        topEditor.initialise();
                        topEditor.setVisible(true);
                    }
                    split.setDividerLocation((double) 0.5);
                    scrollCursorToVisible();
                }
            }
        });
        editor.addMouseMotionListener(new MouseMotionListener() {

            boolean dragged = false;

            public void mouseMoved(MouseEvent e) {

                if (dragged) {
                    SwingUtilities.invokeLater(new Runnable() {

                        public void run() {

                            editor.getCaret().setVisible(true);
                        }
                    });
                }
                dragged = false;
            }

            public void mouseDragged(MouseEvent e) {

                dragged = true;
            }
        });
        return editorPanel;
    }

    public void updateCaretManual(boolean manual) {

        updateCaret = manual;
    }

    //  >> DocumentListener
    public void changedUpdate(DocumentEvent event) {

        //		System.out.println( "DocumentListener.changedUpdate( "+event+")");
        insertHappened = false;
        if (updateCaret) {
            setCursorPosition(event.getOffset());
        }
        updateFolds();
    }

    public void insertUpdate(DocumentEvent event) {

        //		System.out.println( "DocumentListener.insertUpdate( "+event+")");
        if (event.getLength() <= 1) {
            insertHappened = true;
        }
        else {
            insertHappened = false;
        }
        if (updateCaret) {
            setCursorPosition(event.getOffset());
        }
        updateFolds();
    }

    int previousFoldLines = 0;

    private void updateFolds() {

        if (getLines() != previousFoldLines) {
            previousFoldLines = getLines();
            topEditor.getFoldingMargin().checkFolds();
            bottomEditor.getFoldingMargin().checkFolds();
        }
    }

    public void removeUpdate(DocumentEvent event) {

        insertHappened = false;
        if (updateCaret) {
            setCursorPosition(event.getOffset());
        }
        updateFolds();
    }

    // <<< DocumentListener
    public void initBookmarks(Vector bookms) {

        Vector bms = new Vector(bookms);
        this.bookmarks = new Vector();
        if (document.getURL() != null) {
            for (int i = 0; i < bms.size(); i++) {
                Bookmark bm = (Bookmark) bms.elementAt(i);
                if (bm.getURL() != null
                        && bm.getURL().equals(document.getURL().toString())) {
                    int line = bm.getLineNumber();
                    Element root = getEditor().getDocument()
                            .getDefaultRootElement();
                    Element value = root.getElement(line);
                    if (value != null) {
                        bm.init(value, document);
                        bookmarks.addElement(bm);
                    }
                    else {
                        // does no longer exist in this document, remove!
                        parent.removeBookmark(bm);
                    }
                }
            }
        }
    }

    public void resetBookmarks() {

        Vector bms = new Vector(bookmarks);
        for (int i = 0; i < bms.size(); i++) {
            Bookmark bm = (Bookmark) bms.elementAt(i);
            if (!bm.isRemoved()) {
                int line = bm.getLineNumber();
                Element value = null;
                if (line != -1) {
                    Element root = getEditor().getDocument()
                            .getDefaultRootElement();
                    value = root.getElement(line);
                }
                if (value != null) {
                    bm.init(value, document);
                }
                else {
                    // does no longer exist in this document, remove!
                    parent.removeBookmark(bm);
                    bookmarks.removeElement(bm);
                }
            }
            else {
                bookmarks.removeElement(bm);
            }
        }
    }

    public void updateBookmarks() {

        for (int i = 0; i < bookmarks.size(); i++) {
            ((Bookmark) bookmarks.elementAt(i)).update();
        }
    }

    /**
     * Gets the editor icon
     *
     * @return the icon for the editor.
     */
    public ImageIcon getIcon() {

        return ICON;
    }

    /**
     * Paste what ever is on the clipboard in the editor pane.
     */
    public void paste() {

        getEditor().paste();
    }

    /**
     * Update the preferences.
     */
    public void updatePreferences() {

        topEditor.updatePreferences();
        bottomEditor.updatePreferences();
        selectionPopup.setPreferredFont(TextPreferences.getBaseFont());
        showFragmentToolbar(properties.isShowFragmentsToolbar());
        this.revalidate();
        this.repaint();
        // need to set up the keymappings
        String activeConfig = props.getKeyPreferences()
                .getActiveConfiguration();
        setKeyMappings(parent, activeConfig);
    }

    public void scrollCursorToVisible() {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                if (topEditor.isInitialised()) {
                    topEditor.gotoCursor();
                }
                bottomEditor.gotoCursor();
            }
        });
    }

    public void showFragmentToolbar(boolean visible) {

        if (fragments != null && fragments.size() > 0) {
            fragmentToolbar.setVisible(properties.isShowFragmentsToolbar());
        }
        else {
            fragmentToolbar.setVisible(false);
        }
    }

    public void setEndTagCompletion(boolean enabled) {

        topEditor.setEndTagCompletion(enabled);
        bottomEditor.setEndTagCompletion(enabled);
    }

    public void setSmartIndentation(boolean enabled) {

        topEditor.setSmartIndentation(enabled);
        bottomEditor.setSmartIndentation(enabled);
    }

    public void showLinenumberMargin(boolean visible) {

        topEditor.showLinenumberMargin(visible);
        bottomEditor.showLinenumberMargin(visible);
    }

    public void showOverviewMargin(boolean visible) {

        topEditor.showOverviewMargin(visible);
        bottomEditor.showOverviewMargin(visible);
    }

    public void showFoldingMargin(boolean visible) {

        topEditor.showFoldingMargin(visible);
        bottomEditor.showFoldingMargin(visible);
    }

    public void showAnnotationMargin(boolean visible) {

        topEditor.showAnnotationMargin(visible);
        bottomEditor.showAnnotationMargin(visible);
    }

    private void turnOffCtrlX() {

        // remove Ctrl-X from inputmap as needed for alternative editing mode
        KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X,
                InputEvent.CTRL_MASK, false);
        InputMap im = topEditor.getEditor().getInputMap();
        InputMap pem = im;
        while (pem != null) {
            pem.remove(ctrlX);
            pem = pem.getParent();
        }
        im = bottomEditor.getEditor().getInputMap();
        pem = im;
        while (pem != null) {
            pem.remove(ctrlX);
            pem = pem.getParent();
        }
    }

    /**
     *  sets all the keymappings for the specified configuation
     *
     * @param parent The ExchangerEditor
     * @param configName The configuration name 
     */
    public void setKeyMappings(ExchangerEditor parent, String configName) {

        if (configName.equalsIgnoreCase(KeyPreferences.EMACS)) {
            // turn off Ctrl-X for each editor as that will now be used by emacs editing mode
            turnOffCtrlX();
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
                    parent.getStatusbar().addToModeMap(
                            KeyPreferences.SELECT_ALL_ACTION, stroke, action);
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
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "select-all");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "select-all");
    }

    private void setCutActionKey(KeyStroke stroke) {

        removePreviousMapping("cut-to-clipboard");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "cut-to-clipboard");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "cut-to-clipboard");
    }

    private void setTabActionKey(KeyStroke stroke) {

        removePreviousMapping("xngr-indent");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "xngr-indent");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "xngr-indent");
    }

    private void setUnindentActionKey(KeyStroke stroke) {

        removePreviousMapping("xngr-unindent");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "xngr-unindent");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "xngr-unindent");
    }

    private void setGoToStartTagActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoStartTag");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoStartTag");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "gotoStartTag");
    }

    private void setGoToPreviousAttributeValueActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoPreviousAttributeValue");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoPreviousAttributeValue");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "gotoPreviousAttributeValue");
    }

    private void setGoToNextAttributeValueActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoNextAttributeValue");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoNextAttributeValue");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "gotoNextAttributeValue");
    }

    private void setGoToEndTagActionKey(KeyStroke stroke) {

        removePreviousMapping2("gotoEndTag");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "gotoEndTag");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "gotoEndTag");
    }

    private void setUpActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-up");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-up");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-up");
    }

    private void setDownActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-down");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-down");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-down");
    }

    private void setRightActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-forward");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-forward");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-forward");
    }

    private void setLeftActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-backward");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-backward");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-backward");
    }

    private void setPageUpActionKey(KeyStroke stroke) {

        removePreviousMapping2("page-up");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "page-up");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "page-up");
    }

    private void setPageDownActionKey(KeyStroke stroke) {

        removePreviousMapping2("page-down");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "page-down");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "page-down");
    }

    private void setBeginLineActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-begin-line");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-begin-line");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-begin-line");
    }

    private void setEndLineActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-end-line");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-end-line");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-end-line");
    }

    private void setBeginActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-begin");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-begin");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-begin");
    }

    private void setEndActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-end");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-end");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-end");
    }

    private void setPreviousWordActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-previous-word");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-previous-word");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-previous-word");
    }

    private void setNextWordActionKey(KeyStroke stroke) {

        removePreviousMapping2("caret-next-word");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "caret-next-word");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "caret-next-word");
    }

    private void setDeleteNextCharActionKey(KeyStroke stroke) {

        removePreviousMapping2("delete-next");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "delete-next");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "delete-next");
    }

    private void setDeletePrevCharActionKey(KeyStroke stroke) {

        removePreviousMapping2("delete-previous");
        bottomEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(
                stroke, "delete-previous");
        topEditor.getEditor().getInputMap(JComponent.WHEN_FOCUSED).put(stroke,
                "delete-previous");
    }

    private void removePreviousMapping(String actionName) {

        InputMap im = bottomEditor.getEditor().getInputMap();
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
        im = topEditor.getEditor().getInputMap();
        pem = im;
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

        InputMap im = topEditor.getEditor().getInputMap();
        KeyStroke[] strokes = im.allKeys();
        for (int i = 0; i < strokes.length; i++) {
            String actionMapKey = im.get(strokes[i]).toString();
            if (actionMapKey != null
                    && actionMapKey.equalsIgnoreCase(actionName)) {
                im.remove(strokes[i]);
            }
        }
        im = bottomEditor.getEditor().getInputMap();
        strokes = im.allKeys();
        for (int i = 0; i < strokes.length; i++) {
            String actionMapKey = im.get(strokes[i]).toString();
            if (actionMapKey != null
                    && actionMapKey.equalsIgnoreCase(actionName)) {
                im.remove(strokes[i]);
            }
        }
    }

    //	private FoldingMargin getFoldingMargin() { 
    //		return foldingMargin;
    //	}
    //	
    //	private Margin getMargin() { 
    //		return margin;
    //	}
    //
    public void setWrapping(boolean wrap) {

        topEditor.setWrapping(wrap);
        bottomEditor.setWrapping(wrap);
    }

    public JToolBar getFragmentToolbar() {

        if (fragmentToolbar == null) {
            fragmentToolbar = new JToolBar();
            fragmentToolbar.setRollover(true);
            fragmentToolbar.setFloatable(false);
            fragmentToolbar.setOrientation(JToolBar.VERTICAL);
            fragmentToolbar.setVisible(false);
        }
        return fragmentToolbar;
    }

    public void updateFragmentKeys() {

        removeFragmentKeys();
        if (fragments != null) {
            for (int i = 0; i < fragments.size(); i++) {
                FragmentAction action = (FragmentAction) fragments.elementAt(i);
                // Create a name not used by any other mapping
                String name = "xngr.editor.fragment." + i;
                KeyStroke key = (KeyStroke) action
                        .getValue(Action.ACCELERATOR_KEY);
                if (key != null) {
                    topEditor.getEditor().getActionMap().put(name, action);
                    topEditor.getEditor().getInputMap().put(key, name);
                    bottomEditor.getEditor().getActionMap().put(name, action);
                    bottomEditor.getEditor().getInputMap().put(key, name);
                }
            }
        }
    }

    private void removeFragmentKeys() {

        ActionMap map = getEditor().getActionMap();
        Object keys[] = map.allKeys();
        for (int i = 0; i < keys.length; i++) {
            Object key = keys[i];
            if (key instanceof String
                    && ((String) key).startsWith("xngr.editor.fragment.")) {
                topEditor.getEditor().getActionMap().put(key, null);
                bottomEditor.getEditor().getActionMap().put(key, null);
                removePreviousMapping((String) key);
            }
        }
    }

    public void setFragments(Vector frags) {

        frags = sortFragments(frags);
        fragmentToolbar = getFragmentToolbar();
        fragmentToolbar.removeAll();
        fragments = new Vector();
        if (frags != null && frags.size() > 0) {
            for (int i = 0; i < frags.size(); i++) {
                FragmentProperties props = (FragmentProperties) frags
                        .elementAt(i);
                FragmentAction fragment = new FragmentAction(props);
                fragments.addElement(fragment);
                if (props.getIcon() != null && props.getIcon().length() > 0) {
                    fragmentToolbar.add(fragment);
                }
            }
            if (fragmentToolbar.getComponentCount() > 0) {
                fragmentToolbar.setVisible(properties.isShowFragmentsToolbar());
            }
            else {
                fragmentToolbar.setVisible(false);
            }
        }
        else {
            fragmentToolbar.setVisible(false);
        }
        updateFragmentKeys();
    }

    private Vector sortFragments(Vector fragments) {

        Vector result = new Vector();
        for (int i = 0; i < fragments.size(); i++) {
            FragmentProperties fragment = (FragmentProperties) fragments
                    .elementAt(i);
            int index = -1;
            for (int j = 0; j < result.size() && index == -1; j++) {
                // Compare by order
                if (fragment.getOrder() <= ((FragmentProperties) result
                        .elementAt(j)).getOrder()) {
                    index = j;
                }
            }
            if (index != -1) {
                result.insertElementAt(fragment, index);
            }
            else {
                result.addElement(fragment);
            }
        }
        return result;
    }

    /**
     * Cut what ever is selected in the editor pane to the clipboard.
     */
    public void cut() {

        getEditor().cut();
    }

    /**
     * Copy what ever is selected in the editor pane to the clipboard.
     */
    public void copy() {

        getEditor().copy();
    }

    /**
     * Copy what ever is selected in the editor pane to the clipboard.
     */
    public XmlEditorPane getEditor() {

        if (selectedEditor != null) {
            return selectedEditor.getEditor();
        }
        return null;
    }

    /**
     * Replaces all text in the editor and creates one compound 'UndoableEdit'.
     */
    public void setText(String text) {

        if (DEBUG) System.out.println("Editor.setDocument( " + document + ")");
        updateBookmarks();
        view.getChangeManager().startCompound(true);
        getEditor().setText(text);
        getEditor().setCaretPosition(0);
        view.getChangeManager().endCompound();
        resetBookmarks();
    }

    /**
     * Sets the document in the editor.
     *
     * @param document the XML Document.
     */
    public void setDocument(ExchangerDocument document) {

        if (DEBUG) System.out.println("Editor.setDocument( " + document + ")");
        updateBookmarks();
        view.getChangeManager().startCompound(false);
        if (this.document != null) {
            bottomEditor.getEditor().getDocument().removeUndoableEditListener(
                    this);
            bottomEditor.getEditor().getDocument().removeDocumentListener(this);
            this.document.removeListener(this);
        }
        this.document = document;
        if (document != null) {
            document.addListener(this);
            if (document.isXML() || document.isDTD()) {
                bottomEditor.getEditor().setContentType("text/xml");
                if (topEditor.isInitialised()) {
                    topEditor.getEditor().setContentType("text/xml");
                }
            }
            else {
                bottomEditor.getEditor().setContentType("text/txt");
                if (topEditor.isInitialised()) {
                    topEditor.getEditor().setContentType("text/txt");
                }
            }
            XmlDocument doc = (XmlDocument) bottomEditor.getEditor()
                    .getDocument();
            doc.addUndoableEditListener(this);
            doc.addDocumentListener(this);
            bottomEditor.getEditor().setText(document.getText());
            if (topEditor.isInitialised()) {
                topEditor.getEditor().setDocument(doc);
            }
            //			topEditor.getEditor().setText( document.getText());
            doc.resetUpdates();
        }
        else {
            bottomEditor.getEditor().setText("");
        }
        bottomEditor.getEditor().setCaretPosition(0);
        if (topEditor.isInitialised()) {
            topEditor.getEditor().setCaretPosition(0);
        }
        hasLatest = true;
        view.getChangeManager().discardCompound();
        resetBookmarks();
    }

    /**
     * Set the text in the document and report the exceptions.
     */
    public void parse() throws SAXParseException, IOException {

        if (DEBUG) System.out.println("Editor.parse()");
        hasLatest = true;
        try {
            document.setText(getEditor().getText(0,
                    getEditor().getDocument().getLength()));
            if (!document.isError()) {
                ((XmlDocument) getEditor().getDocument()).resetUpdates();
            }
        }
        catch (SAXParseException e) {
            //			e.printStackTrace();
            throw e;
        }
        catch (final IOException e) {
            //		    e.printStackTrace();
            throw e;
        }
        catch (Exception e) {
            //			e.printStackTrace();
        }
        finally {
            view.getChangeManager().markParse();
        }
    }

    //	public void replaceSelection( String replacement) {
    //		view.getChangeManager().startCompound( true);
    //		editor.replaceSelection( replacement);
    //		view.getChangeManager().endCompound();
    //	}
    
   
    	public void replaceSelection( String replacement) {
    		view.getChangeManager().startCompound( true);
    		getEditor().replaceSelection( replacement);
    		view.getChangeManager().endCompound();
    	}
   
    public void replaceAll(String search, String replacement, boolean regExp,
            boolean matchCase, boolean matchword) {

        updateBookmarks();
        view.getChangeManager().startCompound(true);
        if (!regExp) {
            search = "\\Q"
                    + StringUtilities.prepareNonRegularExpression(search)
                    + "\\E";
            replacement = StringUtilities
                    .prepareNonRegularExpressionReplacement(replacement);
        }
        if (matchword) {
            search = "\\b" + search + "\\b";
        }
        try {
            if (!matchCase) {
                pattern = Pattern.compile(search, Pattern.CASE_INSENSITIVE);
            }
            else {
                pattern = Pattern.compile(search);
            }
        }
        catch (PatternSyntaxException ex) {
            parent.setStatus("Invalid Regular Expression \"" + search + "\"!");
            return;
        }
        try {
            int caret = getEditor().getCaretPosition();
            Matcher matcher = pattern.matcher(getEditor().getText(0,
                    getEditor().getDocument().getLength()));
            String text = matcher.replaceAll(replacement);
            getEditor().setText(text);
            getEditor().setCaretPosition(caret);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            view.getChangeManager().endCompound();
            resetBookmarks();
        }
    }

    public void replace(Matcher matcher, String replacement, boolean regExp,
            boolean down) {

        updateBookmarks();
        getEditor().select(matcher.start(), matcher.end());
        if (regExp) {
            StringBuffer sb = new StringBuffer();
            int start = matcher.start();
            matcher.appendReplacement(sb, replacement);
            int end = matcher.end();
            replacement = sb.substring(start);
        }
        view.getChangeManager().startCompound(true);
        try {
            getEditor().replaceSelection(replacement);
            if (!down) { // going up..
                getEditor().setCaretPosition(
                        getEditor().getCaretPosition() - replacement.length());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            view.getChangeManager().endCompound();
            resetBookmarks();
        }
    }

    public Matcher search(String xpath, String search, boolean regExp,
            boolean matchCase, boolean matchword, boolean down, boolean wrap) {

        Vector nodes = null;
        Vector ranges = new Vector();
        try {
            if (xpath != null && xpath.trim().length() > 0) {
                nodes = document.search(xpath);
            }
        }
        catch (Exception e) {
            parent.setStatus("Invalid XPath \"" + xpath + "\"!");
            return null;
        }
        if (nodes != null) {
            for (int i = 0; i < nodes.size(); i++) {
                Node node = (Node) nodes.elementAt(i);
                Range range = null;
                if (node instanceof XElement) {
                    range = new Range((XElement) node);
                }
                else if (node instanceof XAttribute) {
                    range = new Range((XAttribute) node);
                }
                else {
                    parent.setStatus("Invalid Node Type \""
                            + node.getNodeTypeName() + "\"!");
                    return null;
                    //					range = new Range( (XElement)node.getParent());
                }
                addRange(ranges, range);
            }
        }
        else {
            ranges.addElement(new Range(0, getEditor().getDocument()
                    .getLength()));
        }
        Matcher matcher = null;
        try {
            matcher = getEditor().search(ranges, search, regExp, matchCase,
                    matchword, down, wrap);
        }
        catch (PatternSyntaxException ex) {
            parent.setStatus("Invalid Regular Expression \"" + search + "\"!");
            return null;
        }
        if (matcher != null) {
            parent.setStatus("Search complete.");
        }
        else {
            parent.setStatus("String \"" + search + "\" not found!");
        }
        return matcher;
    }

    private void addRange(Vector ranges, Range range) {

        Vector oldRanges = new Vector(ranges);
        for (int i = 0; i < oldRanges.size(); i++) {
            Range r = (Range) oldRanges.elementAt(i);
            if (r.contains(range)) {
                // do not add
                return;
            }
            else if (range.contains(r)) {
                // remove previous ranges
                ranges.removeElement(r);
            }
        }
        oldRanges = new Vector(ranges);
        for (int i = 0; i < oldRanges.size(); i++) {
            Range r = (Range) oldRanges.elementAt(i);
            if (r.getStart() > range.getStart()) {
                ranges.insertElementAt(range, i);
                return;
            }
        }
        ranges.addElement(range);
    }

    /**
     * Perform a search in the text.
     *
     * @param search the text to search for.
     * @param matchcase should the search match the case.
     * @param search down/upward from caret the position.
     */
    //	public Matcher search( String search, boolean regExp, boolean matchCase, boolean matchword, boolean down, boolean wrap) {
    //		if (DEBUG) System.out.println( "Editor.search( "+search+", "+matchCase+", "+down+")");
    //
    //		Matcher matcher = editor.search( search, regExp, matchCase, matchword, down, wrap);
    //
    //		if ( matcher != null) {
    //			parent.setStatus( "Search complete");
    //		} else {
    //			parent.setStatus( "String \""+search+"\" not found!");
    //		}
    //		
    //		return matcher;
    //	}
    /**
     * Set the focus on the editor component!
     */
    public void setFocus() {

        if (!getEditor().hasFocus()) {
            getEditor().requestFocusInWindow();
        }
    }

    /**
     * Selects text associated with the element.
     *
     * @param element the element to select the text for.
     */
    public void selectElement(XElement element) {

        selectElement(element, false, -1);
    }

    public void selectElement(XElement element, boolean endTag, int y) {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        if (!endTag) {
            int start = doc.calculateNewPosition(element
                    .getElementStartPosition());
            if (start != -1) {
                Tag tag = doc.getCurrentTag(start + 1);
                if (tag != null) {
                    getEditor().select(tag.getStart(), tag.getEnd());
                }
            }
        }
        else {
            int end = doc.calculateNewPosition(element.getElementEndPosition());
            if (end != -1) {
                Tag tag = doc.getCurrentTag(end - 1);
                if (tag != null && tag.getType() == Tag.END_TAG) {
                    getEditor().select(tag.getStart(), tag.getEnd());
                }
            }
        }
        if (selectedEditor == bottomEditor) {
            y = y - (topEditor.getSize().height + split.getDividerSize());
        }
        if (y >= 0) {
            try {
                Rectangle r = getEditor().getVisibleRect();
                Rectangle sr = getEditor().modelToView(
                        getEditor().getSelectionStart());
                sr.height = r.height;
                sr.y = sr.y - y;
                if (r.height > y && sr.y >= 0) {
                    getEditor().scrollRectToVisible(sr);
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public void selectError(XMLError error) {

        if (error != null && error.getLineNumber() > 0) {
            selectedEditor.selectError(error);
            //			XmlDocument doc = (XmlDocument)getEditor().getDocument();
            //			Element element = doc.getDefaultRootElement().getElement( error.getLineNumber()-1);
            //			int pos = element.getStartOffset()+(error.getColumnNumber()-1);
            //
            //			if ( Character.isWhitespace( doc.getLastCharacter( pos))) {
            //				getEditor().select( pos, pos+1);
            //			} else {
            //				getEditor().select( pos-1, pos);
            //			}
            //			
            //			parent.getOutputPanel().selectError( error);
        }
    }

    public void updateError(XMLError error) {

        if (error != null && error.getLineNumber() > 0) {
            parent.getOutputPanel().selectError(error);
        }
    }

    /**
     * Selects text associated with the attribute.
     *
     * @param attribute the attribute to select the text for.
     */
    public void selectAttribute(XAttribute attribute, int y) {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        int start = doc.calculateNewPosition(attribute
                .getAttributeStartPosition());
        int end = doc.calculateNewPosition(attribute.getAttributeEndPosition());
        if (start != -1 && end != -1) {
            getEditor().select(start, end);
        }
        if (selectedEditor == bottomEditor) {
            y = y - (topEditor.getSize().height + split.getDividerSize());
        }
        if (y >= 0) {
            try {
                Rectangle r = getEditor().getVisibleRect();
                Rectangle sr = getEditor().modelToView(
                        getEditor().getSelectionStart());
                sr.height = r.height;
                sr.y = sr.y - y;
                if (r.height > y && sr.y >= 0) {
                    getEditor().scrollRectToVisible(sr);
                }
            }
            catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Goto the start of a specific line in the document.
     *
     * @param line the line to go to.
     */
    public void gotoLine(int line) {

        getEditor().gotoLine(line);
    }

    private void showPopupMenu(MouseEvent e) {

        String selectedText = getEditor().getSelectedText();
        JPopupMenu popup = new JPopupMenu();
        if (selectedText != null) {
            if (selectedText.length() > 20) {
                selectedText = selectedText.substring(0, 17);
                selectedText = selectedText + "...";
            }
            URL url = getCurrentURL();
            if (url != null) {
                popup.add(new WrapperAction(parent.getOpenCurrentURLAction(),
                        "Open \"" + URLUtilities.toDisplay(url) + "\""));
                popup.addSeparator();
            }
            popup.add(new WrapperAction(parent.getFindAction(), "Find \""
                    + selectedText + "\""));
            popup.addSeparator();
            boolean separator = false;
            Action toggleEmptyElementAction = parent
                    .getToggleEmptyElementAction();
            if (toggleEmptyElementAction.isEnabled()) {
                popup.add(new WrapperAction(toggleEmptyElementAction,
                        "Expand Empty Element"));
                separator = true;
            }
            Action tagAction = parent.getTagAction();
            if (tagAction.isEnabled()) {
                popup.add(new WrapperAction(tagAction, "Tag Selection"));
                separator = true;
            }
            Action commentAction = parent.getCommentAction();
            if (commentAction.isEnabled()) {
                popup.add(new WrapperAction(commentAction, commentAction
                        .getValue(Action.NAME)
                        + " Selection"));
                separator = true;
            }
            Action cdataAction = parent.getCDATAAction();
            if (cdataAction.isEnabled()) {
                popup.add(new WrapperAction(cdataAction, cdataAction
                        .getValue(Action.NAME)
                        + " Selection"));
                separator = true;
            }
            if (separator) {
                popup.addSeparator();
            }
            popup.add(parent.getSubstituteCharactersAction());
            popup.add(parent.getSubstituteEntitiesAction());
            popup.add(parent.getStripTagsAction());
            popup.addSeparator();
            popup.add(parent.getParseAction());
            popup.add(parent.getValidateAction());
            popup.addSeparator();
            popup.add(parent.getCutAction());
            popup.add(parent.getCopyAction());
            popup.add(parent.getPasteAction());
            popup.addSeparator();
            popup.add(parent.getSaveAction());
            popup.add(parent.getSaveAsAction());
            popup.addSeparator();
            popup.add(parent.getCloseAction());
        }
        else {
            int pos = getEditor().viewToModel(new Point(e.getX(), e.getY()));
            getEditor().setCaretPosition(pos);
            // need specifically the tag at the current position and don't care about the tree!
            Tag tag = ((XmlDocument) getEditor().getDocument())
                    .getCurrentTag(pos);
            Tag parentTag = ((XmlDocument) getEditor().getDocument())
                    .getParentStartTag(pos);
            String currentText = getCurrentText();
            URL url = getCurrentURL();
            if (url != null) {
                popup.add(new WrapperAction(parent.getOpenCurrentURLAction(),
                        "Open \"" + URLUtilities.toDisplay(url) + "\""));
                popup.addSeparator();
            }
            if (currentText != null) {
                if (currentText.length() > 20) {
                    currentText = currentText.substring(0, 17);
                    currentText = currentText + "...";
                }
                popup.add(new WrapperAction(parent.getFindAction(), "Find \""
                        + currentText + "\""));
                popup.addSeparator();
            }
            if (tag != null) {
                if (tag.getType() == Tag.COMMENT_TAG) {
                    popup.add(parent.getCommentAction());
                    popup.addSeparator();
                }
                else if (tag.getType() == Tag.CDATA_TAG) {
                    popup.add(parent.getCDATAAction());
                    popup.addSeparator();
                }
                else if (tag.getType() == Tag.START_TAG) {
                    popup.add(new WrapperAction(parent.getGotoEndTagAction(),
                            "Goto \"" + tag.getQualifiedName() + "\" End Tag"));
                    popup.addSeparator();
                    popup.add(new WrapperAction(
                            parent.getSelectElementAction(), "Select \""
                                    + tag.getQualifiedName() + "\" Element"));
                    popup.add(new WrapperAction(parent
                            .getSelectElementContentAction(), "Select \""
                            + tag.getQualifiedName() + "\" Element Content"));
                    popup.addSeparator();
                }
                else if (tag.getType() == Tag.EMPTY_TAG) {
                    popup.add(new WrapperAction(
                            parent.getSelectElementAction(), "Select \""
                                    + tag.getQualifiedName() + "\" Element"));
                    popup.addSeparator();
                }
                else if (tag.getType() == Tag.END_TAG) {
                    popup
                            .add(new WrapperAction(parent
                                    .getGotoStartTagAction(), "Goto \""
                                    + tag.getQualifiedName() + "\" Start Tag"));
                    popup.addSeparator();
                    popup.add(new WrapperAction(
                            parent.getSelectElementAction(), "Select \""
                                    + tag.getQualifiedName() + "\" Element"));
                    popup.add(new WrapperAction(parent
                            .getSelectElementContentAction(), "Select \""
                            + tag.getQualifiedName() + "\" Element Content"));
                    popup.addSeparator();
                }
            }
            else if (parentTag != null) {
                popup.add(new WrapperAction(parent.getGotoStartTagAction(),
                        "Goto \"" + parentTag.getQualifiedName()
                                + "\" Start Tag"));
                popup
                        .add(new WrapperAction(parent.getGotoEndTagAction(),
                                "Goto \"" + parentTag.getQualifiedName()
                                        + "\" End Tag"));
                popup.addSeparator();
                popup.add(new WrapperAction(parent.getSplitElementAction(),
                        "Split \"" + parentTag.getQualifiedName()
                                + "\" Element"));
                popup.addSeparator();
                popup.add(new WrapperAction(parent.getSelectElementAction(),
                        "Select \"" + parentTag.getQualifiedName()
                                + "\" Element"));
                popup.add(new WrapperAction(parent
                        .getSelectElementContentAction(), "Select \""
                        + parentTag.getQualifiedName() + "\" Element Content"));
                popup.addSeparator();
            }
            popup.add(parent.getParseAction());
            popup.add(parent.getValidateAction());
            popup.addSeparator();
            popup.add(parent.getPasteAction());
            popup.addSeparator();
            popup.add(parent.getSaveAction());
            popup.add(parent.getSaveAsAction());
            popup.addSeparator();
            popup.add(parent.getCloseAction());
        }
        GUIUtilities.alignMenu(popup);
        popup.show(getEditor(), e.getX(), e.getY());
    }
    
    /**
     * Tags the selected text or if the caret is in a tag, 
     * creates the tag after the current tag.
     *
     * @param name the name of the tag.
     */
    public void insertTag(String name) {

        // need the current tag specifically.
        Tag tag = getCurrentTag();
        if (tag != null) {
            if (tag.getType() == Tag.END_TAG) {
                getEditor().setCaretPosition(tag.getStart());
                tagSelectedText(name);
            }
            else if (tag.getType() == Tag.EMPTY_TAG) {
                view.getChangeManager().startCompound(true);
                getEditor().select(tag.getEnd() - 2, tag.getEnd());
                StringBuffer buffer = new StringBuffer("><");
                buffer.append(name);
                buffer.append(">");
                buffer.append("</");
                buffer.append(name);
                buffer.append(">");
                buffer.append("</");
                buffer.append(tag.getQualifiedName());
                buffer.append(">");
                getEditor().replaceSelection(buffer.toString());
                getEditor().setCaretPosition(tag.getEnd() + name.length() + 1);
                view.getChangeManager().endCompound();
            }
            else { // all other tags, place the element after the tag.
                getEditor().setCaretPosition(tag.getEnd());
                tagSelectedText(name);
            }
        }
        else {
            tagSelectedText(name);
        }
    }

    //	public void gotoCursor() {
    //		try{
    //			Rectangle pos = getEditor().modelToView( getEditor().getCaretPosition());
    //			getEditor().scrollRectToVisible( pos);
    //		} catch ( Exception e) {
    //			e.printStackTrace();
    //		}
    //	}
    /**
     * Tags the selected text.
     *
     * @param name the name of the tag.
     */
    public void tagSelectedText(String name) {

        view.getChangeManager().startCompound(true);
        String selection = getEditor().getSelectedText();
        StringBuffer buffer = new StringBuffer("<");
        buffer.append(name);
        buffer.append(">");
        if (selection != null) {
            buffer.append(selection);
        }
        buffer.append("</");
        buffer.append(name);
        buffer.append(">");
        getEditor().replaceSelection(buffer.toString());
        if (selection == null || selection.length() == 0) {
            getEditor().setCaretPosition(
                    getEditor().getCaretPosition() - (name.length() + 3));
        }
        view.getChangeManager().endCompound();
    }
    
    /**
     * Gets the selected editor panel
     * @return selectedEditor
     */
    public EditorPanel getSelectedEditorPanel() {
        
        return(this.selectedEditor);
    }

    /**
     * Appends an attribute to the current tag...
     *
     * @param name the full name of the attribute.
     */
    public void appendAttribute(String name) {

        view.getChangeManager().startCompound(true);
        Tag startTag = getCurrentStartTag();
        if (startTag != null) {
            if (startTag.getType() == Tag.START_TAG) {
                getEditor().setCaretPosition(startTag.getEnd() - 1);
            }
            else { // EMPTY_TAG
                getEditor().setCaretPosition(startTag.getEnd() - 2);
            }
            StringBuffer buffer = new StringBuffer(" ");
            buffer.append(name);
            buffer.append("=\"\"");
            getEditor().replaceSelection(buffer.toString());
            getEditor().setCaretPosition(getEditor().getCaretPosition() - 1);
        }
        view.getChangeManager().endCompound();
    }

    /**
     * Selects the attrubte value of the current tag...
     *
     * @param name the full name of the attribute.
     * @param value the value of the attribute.
     */
    public void selectAttributeValue(String name, String value) {

        Tag startTag = getCurrentStartTag();
        if (startTag != null) {
            int start = startTag.getStart();
            int offset = startTag.getAttributeValueOffset(name);
            getEditor().select(start + offset, start + offset + value.length());
        }
    }

    /**
     * Comments the selected text.
     */
    public void lock() {

        if (getEditor().getLocked() == XmlEditorPane.NOT_LOCKED) {
            topEditor.getEditor().setLocked(XmlEditorPane.LOCKED);
            bottomEditor.getEditor().setLocked(XmlEditorPane.LOCKED);
        }
        else if (getEditor().getLocked() == XmlEditorPane.LOCKED) {
            topEditor.getEditor().setLocked(XmlEditorPane.DOUBLE_LOCKED);
            bottomEditor.getEditor().setLocked(XmlEditorPane.DOUBLE_LOCKED);
        }
        else {
            topEditor.getEditor().setLocked(XmlEditorPane.NOT_LOCKED);
            bottomEditor.getEditor().setLocked(XmlEditorPane.NOT_LOCKED);
        }
        updateHelper();
    }

    /**
     * Comments the selected text.
     */
    public void commentSelectedText() {

        view.getChangeManager().startCompound(true);
        try {
            XmlDocument doc = (XmlDocument) getEditor().getDocument();
            int pos = getEditor().getCaretPosition();
            if (doc.isComment(pos)) {
                String text = doc.getText(0, doc.getLength());
                int start = text.lastIndexOf("<!--", pos);
                if (start != -1) {
                    int end = text.indexOf("-->", start);
                    doc.remove(end, 3);
                    doc.remove(start, 4);
                }
            }
            else {
                String selection = getEditor().getSelectedText();
                StringBuffer buffer = new StringBuffer("<!--");
                if (selection != null) {
                    buffer.append(selection);
                }
                buffer.append("-->");
                getEditor().replaceSelection(buffer.toString());
                if (selection == null) {
                    getEditor().setCaretPosition(
                            getEditor().getCaretPosition() - 3);
                }
            }
        }
        catch (Exception e) {
        }
        finally {
            view.getChangeManager().endCompound();
        }
    }

    private FragmentDialog getFragmentDialog() {

        if (fragmentDialog == null) {
            fragmentDialog = new FragmentDialog(parent);
        }
        return fragmentDialog;
    }

    public void insertFragment(boolean block, String content) {

        if (DEBUG)
                System.out.println("Editor.insertFragment( " + block + ", "
                        + content + ")");
        view.getChangeManager().startCompound(true);
        StringBuffer buffer = null;
        if (block) {
            content = "\n" + content;
        }
        try {
            XmlDocument doc = (XmlDocument) getEditor().getDocument();
            int pos = getEditor().getCaretPosition();
            int selectionStart = getEditor().getSelectionStart();
            String selection = getEditor().getSelectedText();
            if (selection == null) {
                selection = "";
            }
            Tag tag = getCurrentStartTag();
            StringBuffer whitespace = new StringBuffer();
            if (tag != null) {
                int index = doc.getDefaultRootElement().getElementIndex(
                        tag.getStart());
                Element element = doc.getDefaultRootElement().getElement(index);
                int start = element.getStartOffset();
                int end = element.getEndOffset();
                String line = doc.getText(start, end - start);
                whitespace = whitespace.append(TextPreferences.getTabString());
                for (int i = 0; (i < line.length()); i++) {
                    char ch = line.charAt(i);
                    if (((ch != '\n') && (ch != '\f') && (ch != '\r'))
                            && Character.isWhitespace(ch)) {
                        whitespace.append(ch);
                    }
                    else {
                        break;
                    }
                }
            }
            Vector keys = getFragmentKeys(content);
            if (keys.size() > 0) {
                FragmentDialog dialog = getFragmentDialog();
                dialog.show(keys);
                if (!dialog.isCancelled()) {
                    for (int i = 0; i < keys.size(); i++) {
                        String name = (String) keys.elementAt(i);
                        String value = dialog.getValue(name);
                        content = StringUtilities.replace(content, "${" + name
                                + "}", value);
                    }
                }
                else {
                    return;
                }
            }
            content = StringUtilities.replace(content, "${selection}",
                    selection);
            content = StringUtilities.replace(content, "\n", "\n" + whitespace);
            int cursorStart = content.indexOf("${cursor}");
            // add everything before the cursor start.
            if (cursorStart != -1) {
                buffer = new StringBuffer(content.substring(0, cursorStart));
                buffer.append(content.substring(cursorStart + 9));
            }
            else {
                buffer = new StringBuffer(content);
            }
            if (block) {
                buffer.append("\n");
            }
            content = buffer.toString();
            getEditor().replaceSelection(content);
            if (cursorStart == -1) {
                cursorStart = content.length();
            }
            if (selection == null) {
                getEditor().setCaretPosition(pos + cursorStart);
            }
            else {
                getEditor().setCaretPosition(selectionStart + cursorStart);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            view.getChangeManager().endCompound();
        }
    }

    private Vector getFragmentKeys(String content) {

        Vector keys = new Vector();
        int start = content.indexOf("${");
        while (start != -1) {
            int end = content.indexOf('}', start);
            if (end != -1) {
                String key = content.substring(start + 2, end);
                if (!key.equals("cursor") && !key.equals("selection")) {
                    if (!keys.contains(key)) {
                        keys.add(key);
                    }
                }
            }
            else {
                break;
            }
            start = content.indexOf("${", end);
        }
        return keys;
    }

    public void selectElement() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        //		int pos = getEditor().getCaretPosition();
        Tag tag = getCurrentTag();
        if (tag != null) {
            if (tag.getType() == Tag.START_TAG) {
                Tag endTag = doc.getEndTag(tag);
                if (endTag != null) {
                    getEditor().select(tag.getStart(), endTag.getEnd());
                }
                else {
                    System.err
                            .println("WARNING: How strange, could not find an end-tag for: "
                                    + tag.getQualifiedName());
                }
            }
            else if (tag.getType() == Tag.END_TAG) {
                Tag startTag = doc.getStartTag(tag);
                if (startTag != null) {
                    getEditor().select(startTag.getStart(), tag.getEnd());
                }
                else {
                    System.err
                            .println("WARNING: How strange, could not find a start-tag for: "
                                    + tag.getQualifiedName());
                }
            }
            else if (tag.getType() == Tag.EMPTY_TAG) {
                getEditor().select(tag.getStart(), tag.getEnd());
            }
            else {
                selectParentElement();
            }
        }
        else {
            selectParentElement();
        }
    }

    public void selectElementContent() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        //		int pos = getEditor().getCaretPosition();
        Tag tag = getCurrentTag();
        if (tag != null) {
            if (tag.getType() == Tag.START_TAG) {
                Tag endTag = doc.getEndTag(tag);
                if (endTag != null) {
                    getEditor().select(tag.getEnd(), endTag.getStart());
                }
                else {
                    System.err
                            .println("WARNING: How strange, could not find an end-tag for: "
                                    + tag.getQualifiedName());
                }
            }
            else if (tag.getType() == Tag.END_TAG) {
                Tag startTag = doc.getStartTag(tag);
                if (startTag != null) {
                    getEditor().select(startTag.getEnd(), tag.getStart());
                }
                else {
                    System.err
                            .println("WARNING: How strange, could not find a start-tag for: "
                                    + tag.getQualifiedName());
                }
            }
            else {
                selectParentElementContent();
            }
        }
        else {
            selectParentElementContent();
        }
    }

    public void selectParentElement() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Tag startTag = getParentStartTag();
        if (startTag != null) {
            Tag endTag = doc.getEndTag(startTag);
            if (endTag != null) {
                getEditor().select(startTag.getStart(), endTag.getEnd());
            }
            else {
                System.err
                        .println("WARNING: How strange, could not find an end-tag for: "
                                + startTag.getQualifiedName());
            }
        }
    }

    public void gotoEndTag() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Tag tag = getCurrentTag();
        if (tag != null) {
            if (tag.getType() == Tag.START_TAG) {
                Tag endTag = doc.getEndTag(tag);
                if (endTag != null) {
                    getEditor().setCaretPosition(endTag.getEnd());
                    getEditor().moveCaretPosition(endTag.getStart());
                    //					editor.select( endTag.getStart(), endTag.getEnd());
                }
            }
            else if (tag.getType() == Tag.END_TAG) {
                getEditor().setCaretPosition(tag.getEnd());
                getEditor().moveCaretPosition(tag.getStart());
                //				editor.select( tag.getStart(), tag.getEnd());
            }
            else {
                Tag startTag = getParentStartTag();
                if (startTag != null) {
                    Tag endTag = doc.getEndTag(startTag);
                    if (endTag != null) {
                        getEditor().setCaretPosition(endTag.getEnd());
                        getEditor().moveCaretPosition(endTag.getStart());
                        //						editor.select( endTag.getStart(), endTag.getEnd());
                    }
                }
            }
        }
        else {
            Tag startTag = getParentStartTag();
            if (startTag != null) {
                Tag endTag = doc.getEndTag(startTag);
                if (endTag != null) {
                    //					editor.select( endTag.getStart(), endTag.getEnd());
                    getEditor().setCaretPosition(endTag.getEnd());
                    getEditor().moveCaretPosition(endTag.getStart());
                }
            }
        }
    }

    public void gotoStartTag() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Tag tag = getCurrentTag();
        if (tag != null) {
            if (tag.getType() == Tag.START_TAG) {
                getEditor().select(tag.getStart(), tag.getEnd());
            }
            else if (tag.getType() == Tag.END_TAG) {
                Tag startTag = doc.getStartTag(tag);
                if (startTag != null) {
                    getEditor().select(startTag.getStart(), startTag.getEnd());
                }
            }
            else {
                Tag startTag = getParentStartTag();
                if (startTag != null) {
                    getEditor().select(startTag.getStart(), startTag.getEnd());
                }
            }
        }
        else {
            Tag startTag = getParentStartTag();
            if (startTag != null) {
                getEditor().select(startTag.getStart(), startTag.getEnd());
            }
        }
    }

    public void toggleEmptyElement() {

        Tag tag = getCurrentTag();
        if (tag != null && tag.getType() == Tag.EMPTY_TAG) {
            view.getChangeManager().startCompound(true);
            getEditor().select(tag.getEnd() - 2, tag.getEnd());
            StringBuffer buffer = new StringBuffer(">");
            buffer.append("</");
            buffer.append(tag.getQualifiedName());
            buffer.append(">");
            getEditor().replaceSelection(buffer.toString());
            getEditor().setCaretPosition(tag.getEnd() - 1);
            view.getChangeManager().endCompound();
        }
    }

    public void renameCurrentElement(String name) {

        int pos = getCursorPosition();
        Tag endTag = null;
        Tag startTag = getCurrentTag();
        if (startTag != null && startTag.getType() == Tag.END_TAG) {
            endTag = startTag;
            startTag = ((XmlDocument) getEditor().getDocument())
                    .getStartTag(startTag);
        }
        else if (startTag == null
                || (startTag.getType() != Tag.START_TAG && startTag.getType() != Tag.EMPTY_TAG)) {
            startTag = getParentStartTag();
        }
        if (startTag != null) {
            if (startTag.getType() == Tag.EMPTY_TAG) {
                view.getChangeManager().startCompound(true);
                // same start and end-tag ...
                getEditor().select(
                        startTag.getStart() + 1,
                        startTag.getStart() + 1
                                + startTag.getQualifiedName().length());
                getEditor().replaceSelection(name);
                view.getChangeManager().endCompound();
            }
            else {
                if (endTag == null) {
                    endTag = ((XmlDocument) getEditor().getDocument())
                            .getEndTag(startTag);
                }
                if (endTag != null
                        && endTag.getQualifiedName().equals(
                                startTag.getQualifiedName())) {
                    view.getChangeManager().startCompound(true);
                    // same start and end-tag ...
                    getEditor().select(
                            endTag.getStart() + 2,
                            endTag.getStart() + 2
                                    + endTag.getQualifiedName().length());
                    getEditor().replaceSelection(name);
                    getEditor().select(
                            startTag.getStart() + 1,
                            startTag.getStart() + 1
                                    + startTag.getQualifiedName().length());
                    getEditor().replaceSelection(name);
                    view.getChangeManager().endCompound();
                }
            }
        }
    }

    public void gotoNextAttributeValue() {

        int pos = getCursorPosition();
        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Tag tag = getCurrentTag();
        if (tag == null) {
            tag = doc.getNextTag(pos);
        }
        int counter = 0; // time out counter
        while (tag != null && counter < 1000) {
            if (tag.getType() == Tag.START_TAG
                    || tag.getType() == Tag.EMPTY_TAG) {
                Vector names = tag.getAttributeNames();
                if (names != null) {
                    int attributePos = -1;
                    for (int i = 0; i < names.size(); i++) {
                        String name = (String) names.elementAt(i);
                        int offset = tag.getAttributeValueOffset(name)
                                + tag.getStart();
                        if (offset > pos
                                && (attributePos == -1 || attributePos > offset)) {
                            attributePos = offset;
                        }
                    }
                    if (attributePos != -1) {
                        getEditor().setCaretPosition(attributePos);
                        return;
                    }
                }
            }
            if (tag.getEnd() < (doc.getLength() - 1)) {
                tag = doc.getNextTag(tag.getEnd());
            }
            else {
                tag = null;
            }
            counter++;
        }
    }

    public void gotoPreviousAttributeValue() {

        int pos = getCursorPosition();
        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Tag tag = getCurrentTag();
        if (tag == null) {
            tag = doc.getPreviousTag(pos);
        }
        int counter = 0; // time out counter
        while (tag != null && counter < 1000) {
            if (tag.getType() == Tag.START_TAG
                    || tag.getType() == Tag.EMPTY_TAG) {
                Vector names = tag.getAttributeNames();
                if (names != null) {
                    int attributePos = -1;
                    for (int i = 0; i < names.size(); i++) {
                        String name = (String) names.elementAt(i);
                        int offset = tag.getAttributeValueOffset(name)
                                + tag.getStart();
                        if (offset < pos && attributePos < offset) {
                            attributePos = offset;
                        }
                    }
                    if (attributePos != -1) {
                        getEditor().setCaretPosition(attributePos);
                        return;
                    }
                }
            }
            tag = doc.getPreviousTag(tag.getStart());
            counter++;
        }
    }

    public void selectParentElementContent() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        Tag startTag = getParentStartTag();
        if (startTag != null) {
            Tag endTag = doc.getEndTag(startTag);
            if (endTag != null) {
                getEditor().select(startTag.getEnd(), endTag.getStart());
            }
            else {
                System.err
                        .println("WARNING: How strange, could not find an end-tag for: "
                                + startTag.getQualifiedName());
            }
        }
    }

    /**
     * CDATAs the selected text.
     */
    public void cdataSelectedText() {

        view.getChangeManager().startCompound(true);
        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        int pos = getEditor().getCaretPosition();
        try {
            if (doc.isCDATA(pos)) {
                String text = doc.getText(0, doc.getLength());
                int start = text.lastIndexOf("<![CDATA[", pos);
                int end = text.indexOf("]]>", pos);
                getEditor().select(start, end + 3);
                text = getEditor().getSelectedText();
                text = text.substring(9, text.length() - 3);
                text = substituteCDATACharacters(text);
                getEditor().replaceSelection(text);
            }
            else {
                String selection = getEditor().getSelectedText();
                StringBuffer buffer = new StringBuffer("<![CDATA[");
                if (selection != null) {
                    buffer.append(selection);
                }
                buffer.append("]]>");
                getEditor().replaceSelection(buffer.toString());
                if (selection == null) {
                    getEditor().setCaretPosition(
                            getEditor().getCaretPosition() - 3);
                }
            }
        }
        catch (Exception e) {
        }
        finally {
            view.getChangeManager().endCompound();
        }
    }

    /**
     * Unindents the selected text.
     */
    public void unindentSelectedText() {

        view.getChangeManager().startCompound(true);
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
            view.getChangeManager().endCompound();
        }
    }

    /**
     * Substitutes the selected characters with entities.
     */
    public void substituteSelectedCharacters() {

        String text = getEditor().getSelectedText();
        if (text != null) {
            try {
                view.getChangeManager().startCompound(true);
                StringBuffer newText = new StringBuffer();
                for (int i = 0; i < text.length(); i++) {
                    char character = text.charAt(i);
                    if ((character == '<') && properties.isConvertLtToEntity()) {
                        newText.append("&lt;");
                    }
                    else if (character == '>'
                            && properties.isConvertGtToEntity()) {
                        newText.append("&gt;");
                    }
                    else if (character == '&'
                            && properties.isConvertAmpToEntity()) {
                        newText.append("&amp;");
                    }
                    else if (character == '\''
                            && properties.isConvertAposToEntity()) {
                        newText.append("&apos;");
                    }
                    else if (character == '\"'
                            && properties.isConvertQuotToEntity()) {
                        newText.append("&quot;");
                    }
                    else if (properties.isConvertCharacterToNamedEntity()
                            && (CommonEntities.getEntityName(character) != null)
                            && character > 127) {
                        String name = CommonEntities.getEntityName(character);
                        if (!name.equals("lt") && !name.equals("gt")
                                && !name.equals("amp") && !name.equals("apos")
                                && !name.equals("quot")) {
                            newText.append("&");
                            newText.append(name);
                            newText.append(";");
                        }
                    }
                    else if (properties.isEscapeUnicodeCharacters()
                            && character >= properties.getUnicodeStartEntry()) {
                        newText.append("&#");
                        newText.append((int) character);
                        newText.append(";");
                    }
                    else {
                        newText.append((char) character);
                    }
                }
                getEditor().replaceSelection(newText.toString());
            }
            finally {
                view.getChangeManager().endCompound();
            }
        }
    }

    private String substituteCDATACharacters(String text) {

        if (text != null) {
            StringBuffer newText = new StringBuffer();
            for (int i = 0; i < text.length(); i++) {
                char character = text.charAt(i);
                if ((character == '<')) {
                    newText.append("&lt;");
                }
                else if (character == '&') {
                    if (text.length() < i + 5
                            || !(text.charAt(i + 1) == 'a'
                                    && text.charAt(i + 2) == 'm'
                                    && text.charAt(i + 3) == 'p' && text
                                    .charAt(i + 4) == ';')) {
                        newText.append("&amp;");
                    }
                    else {
                        newText.append((char) character);
                    }
                }
                else {
                    newText.append((char) character);
                }
            }
            return newText.toString();
        }
        return null;
    }

    /**
     * Substitutes the selected entities with characters.
     */
    public void substituteSelectedEntities() {

        String text = getEditor().getSelectedText();
        if (text != null) {
            try {
                view.getChangeManager().startCompound(true);
                StringBuffer newText = new StringBuffer();
                for (int i = 0; i < text.length(); i++) {
                    char character = text.charAt(i);
                    if (character == '&') {
                        int entityStart = i;
                        int entityEnd = text.indexOf(';', entityStart) + 1;
                        if (entityEnd != 0) {
                            String entity = text.substring(entityStart,
                                    entityEnd);
                            //							System.out.println( "Entity:"+entity);
                            if (entity.startsWith("&#")) {
                                if (properties.isConvertCharacterEntity()) {
                                    char newCharacter = CommonEntities
                                            .convertCharacterEntity(entity);
                                    if (newCharacter != -1) {
                                        newText.append((char) newCharacter);
                                        i += entity.length() - 1;
                                    }
                                    else {
                                        newText.append((char) character);
                                    }
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                            else if (entity.equals("&lt;")) {
                                if (properties.isConvertLtToCharacter()) {
                                    newText.append("<");
                                    i += entity.length() - 1;
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                            else if (entity.equals("&gt;")) {
                                if (properties.isConvertGtToCharacter()) {
                                    newText.append(">");
                                    i += entity.length() - 1;
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                            else if (entity.equals("&apos;")) {
                                if (properties.isConvertAposToCharacter()) {
                                    newText.append("'");
                                    i += entity.length() - 1;
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                            else if (entity.equals("&amp;")) {
                                if (properties.isConvertAmpToCharacter()) {
                                    newText.append("&");
                                    i += entity.length() - 1;
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                            else if (entity.equals("&quot;")) {
                                if (properties.isConvertQuotToCharacter()) {
                                    newText.append("\"");
                                    i += entity.length() - 1;
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                            else {
                                if (properties
                                        .isConvertNamedEntityToCharacter()) {
                                    char newCharacter = CommonEntities
                                            .getChar(entity.substring(1, entity
                                                    .length() - 1));
                                    if (newCharacter != -1) {
                                        newText.append((char) newCharacter);
                                        i += entity.length() - 1;
                                    }
                                    else {
                                        newText.append((char) character);
                                    }
                                }
                                else {
                                    newText.append((char) character);
                                }
                            }
                        }
                        else {
                            newText.append((char) character);
                        }
                    }
                    else {
                        newText.append((char) character);
                    }
                }
                getEditor().replaceSelection(newText.toString());
            }
            finally {
                view.getChangeManager().endCompound();
            }
        }
    }

    public void splitElement() {

        Tag tag = getParentStartTag();
        if (tag != null) {
            String qname = tag.getQualifiedName();
            StringBuffer buffer = new StringBuffer("</");
            buffer.append(qname);
            buffer.append("><");
            buffer.append(qname);
            buffer.append(">");
            getEditor().replaceSelection(buffer.toString());
        }
    }

    /**
     * Substitutes the selected entities with characters.
     */
    public void stripSelectedTags() {

        String text = getEditor().getSelectedText();
        if (text != null) {
            try {
                view.getChangeManager().startCompound(true);
                StringBuffer newText = new StringBuffer();
                for (int i = 0; i < text.length(); i++) {
                    char character = text.charAt(i);
                    if (character == '<') {
                        int tagStart = i;
                        int tagEnd = text.indexOf('>', tagStart) + 1;
                        if (tagEnd != 0) {
                            String tag = text.substring(tagStart, tagEnd);
                            if (tag.startsWith("<![CDATA[")) {
                                int cdataStart = tagStart;
                                int cdataEnd = text.indexOf("]]>", tagStart) + 3;
                                if (cdataEnd != 2) {
                                    String cdata = text.substring(cdataStart,
                                            cdataEnd);
                                    newText.append(cdata);
                                    i += cdata.length() - 1;
                                }
                                else {
                                    newText.append(text.substring(cdataStart,
                                            text.length()));
                                    i = text.length();
                                }
                            }
                            else if (tag.startsWith("<!--")) {
                                int commentStart = tagStart;
                                int commentEnd = text.indexOf("-->",
                                        commentStart) + 3;
                                if (commentEnd != 2) {
                                    String comment = text.substring(
                                            commentStart, commentEnd);
                                    newText.append(comment);
                                    i += comment.length() - 1;
                                }
                                else {
                                    newText.append(text.substring(commentStart,
                                            text.length()));
                                    i = text.length();
                                }
                            }
                            else { // it must be a tag, don't include it...
                                i += tag.length() - 1;
                                if (((tagStart - 1) >= 0)
                                        && ((i + 1) <= (text.length() - 1))
                                        && !Character.isWhitespace(text
                                                .charAt(tagStart - 1))
                                        && !Character.isWhitespace(text
                                                .charAt(i + 1))) {
                                    newText.append(" ");
                                }
                            }
                        }
                        else {
                            newText.append(text.substring(tagStart, text
                                    .length()));
                            i = text.length();
                        }
                    }
                    else {
                        newText.append((char) character);
                    }
                }
                getEditor().replaceSelection(newText.toString());
            }
            finally {
                view.getChangeManager().endCompound();
            }
        }
    }

    public String getSelectedText() {

        return getEditor().getSelectedText();
    }

    /**
     * Returns either the currently selected text, or the 
     * current string where the caret is postioned.
     *
     * @return the current selected text.
     */
    public String getCurrentText() {

        String value = getEditor().getSelectedText();
        if (value == null) {
            try {
                int pos = getEditor().getCaretPosition();
                XmlDocument document = (XmlDocument) getEditor().getDocument();
                Element root = document.getDefaultRootElement();
                Element element = root.getElement(root.getElementIndex(pos));
                int start = element.getStartOffset();
                int end = element.getEndOffset();
                String line = document.getText(start, end - start);
                start = pos - start;
                end = start;
                while (Character.isLetterOrDigit(line.charAt(start))
                        && start >= 0) {
                    start--;
                }
                while (Character.isLetterOrDigit(line.charAt(end))
                        && end < line.length()) {
                    end++;
                }
                if (start + 1 < end) {
                    value = line.substring(start + 1, end);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (value.indexOf('\n') != -1) {
            value = value.substring(0, value.indexOf('\n'));
        }
        return value;
    }

    public URL getCurrentURL() {

        String value = getEditor().getSelectedText();
        if (value == null) {
            try {
                int pos = getEditor().getCaretPosition();
                XmlDocument document = (XmlDocument) getEditor().getDocument();
                Element root = document.getDefaultRootElement();
                Element element = root.getElement(root.getElementIndex(pos));
                int start = element.getStartOffset();
                int end = element.getEndOffset();
                String line = document.getText(start, end - start);
                start = pos - start;
                end = start;
                while (URLUtilities.isURLCharacter(line.charAt(start))
                        && start >= 0) {
                    start--;
                }
                while (URLUtilities.isURLCharacter(line.charAt(end))
                        && end < line.length()) {
                    end++;
                }
                if (start + 1 < end) {
                    value = line.substring(start + 1, end);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return URLUtilities.toURL(URLUtilities.resolveURL(document.getURL(),
                value));
    }

    /**
     * Indents the selected text.
     *
     * @param tab when true the tab key is used as indentation.
     */
    public void indentSelectedText(boolean tab) {

        view.getChangeManager().startCompound(true);
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
            view.getChangeManager().endCompound();
        }
    }

    /**
     * Returns the editor properties.
     *
     * @return the editor properties.
     */
    public EditorProperties getProperties() {

        return properties;
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
     * Get the cursor position.
     *
     * @return the current position of the cursor.
     */
    public int getCursorPosition() {

        return getEditor().getCaretPosition();
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

    /**
     * Formats the text to a nice DOM4J format.
     */
    /*	public void format() throws SAXParseException, IOException {
     view.getChangeManager().startCompound( true);

     try {
     LicenseManager licenseManager = LicenseManager.getInstance();
     licenseManager.isValid( com.cladonia.license.KeyGenerator.generate(7), "Exchanger XML Editor");
     } catch (Exception x) {
     System.exit(0);
     return;
     }

     try {
     String text = editor.getText( 0, editor.getDocument().getLength());
     ByteArrayInputStream stream = new ByteArrayInputStream( text.getBytes( document.getJavaEncoding()));
     InputStreamReader reader = new InputStreamReader( stream, document.getJavaEncoding());
     URL url = document.getURL();
     String systemId = null;

     if ( url != null) {
     systemId = url.toString();
     }
     
     SAXReader sax = XMLUtilities.createReader( false);
     sax.setStripWhitespaceText( false);
     sax.setMergeAdjacentText( false);

     XDocument doc = XMLUtilities.parse( sax, new BufferedReader( reader), systemId);

     int pos = editor.getCaretPosition();
     
     ByteArrayOutputStream out = new ByteArrayOutputStream();

     OutputFormat format = new OutputFormat();
     format.setEncoding( doc.getEncoding());
     
     XMLFormatter formatter = new XMLFormatter( out, format);
     
     //			EditorProperties properties = config.getEditorProperties();
     switch ( properties.getFormatType()) {
     case EditorProperties.FORMAT_CUSTOM:
     if ( properties.isCustomIndent()) {
     format.setIndent( getTabString());
     } else {
     * format.setIndent( ""); }
     * 
     * format.setNewlines( properties.isCustomNewline()); format.setPadText(
     * properties.isCustomPadText());
     * 
     * if ( properties.isWrapText()) { formatter.setMaxLineLength(
     * properties.getWrappingColumn()); }
     *  // formatter.setIndentSize( TextPreferences.getTabSize());
     * formatter.setTrimText( properties.isCustomStrip());
     * formatter.setPreserveMixedContent(
     * properties.isCustomPreserveMixedContent()); break; case
     * EditorProperties.FORMAT_COMPACT: if ( properties.isWrapText()) {
     * formatter.setMaxLineLength( properties.getWrappingColumn()); }
     *  // formatter.setIndentSize( TextPreferences.getTabSize());
     * 
     * format.setIndent( ""); format.setNewlines( false); format.setPadText(
     * false);
     * 
     * formatter.setTrimText( true); formatter.setPreserveMixedContent( false);
     * break; case EditorProperties.FORMAT_STANDARD: if (
     * properties.isWrapText()) { formatter.setMaxLineLength(
     * properties.getWrappingColumn()); } // formatter.setIndentSize(
     * TextPreferences.getTabSize());
     * 
     * format.setIndent( getTabString());
     * 
     * format.setNewlines( true); format.setPadText( false);
     * 
     * formatter.setTrimText( true); formatter.setPreserveMixedContent( true);
     * break; }
     * 
     * formatter.write( doc); String output = out.toString( doc.getEncoding()); //
     * System.out.println( output);
     * 
     * try { // use the normal XML plus writer... document.setText( output); }
     * catch ( SAXParseException e) { e.printStackTrace(); throw e; } catch (
     * final IOException e) { e.printStackTrace(); throw e; } catch ( Exception e) {
     * e.printStackTrace(); }
     * 
     * formatter.flush();
     * 
     * String result = document.getText();
     * 
     * editor.setText( result);
     * ((XmlDocument)editor.getDocument()).resetUpdates();
     *  // force another parse! // document.setText( result);
     * 
     * if ( result.length() < pos) { pos = result.length(); }
     * 
     * editor.setCaretPosition( pos);
     *  } catch( BadLocationException e) { e.printStackTrace(); } finally { //
     * enableParsing(); view.getChangeManager().endCompound(); } }
     */
    /**
     * Formats the text to a nice DOM4J format.
     */
    /*
     * public void canonicalize() throws Exception, SAXParseException,
     * IOException { view.getChangeManager().startCompound( true);
     * 
     * try { int pos = editor.getCaretPosition();
     * 
     * document.canonicalize(); String text = document.getText();
     * 
     * editor.setText( text);
     * ((XmlDocument)editor.getDocument()).resetUpdates();
     * 
     * if ( text.length() < pos) { pos = text.length(); }
     * 
     * editor.setCaretPosition( pos);
     *  } catch( BadLocationException e) { e.printStackTrace(); } finally {
     * view.getChangeManager().endCompound(); }
     */
    /**
     * Selects the indicated line.
     * 
     * @param line
     *            the line to select.
     */
    public void selectLine(int line) {

        getEditor().selectLine(line);
    }

    public void select(int line, int start, int end) {

        Element root = getEditor().getDocument().getDefaultRootElement();
        Element elem = root.getElement(line - 1);
        if (elem != null) {
            start = elem.getStartOffset() + start;
            end = elem.getStartOffset() + end;
            getEditor().select(start, end);
        }
    }

    /**
     * Selects the indicated line.
     * 
     * @param line
     *            the line to select.
     */
    public void selectLineForOffset(int off) {

        getEditor().selectLineForOffset(off);
    }

    /**
     * Selects the indicated line.
     * 
     * @param line
     *            the line to select.
     */
    public void selectLineWithoutEnd(int line) {

        if (line > 0) {
            Element root = getEditor().getDocument().getDefaultRootElement();
            Element elem = root.getElement(line - 1);
            if (elem != null) {
                int start = elem.getStartOffset();
                int end = elem.getEndOffset() - 1;
                getEditor().select(start, end);
            }
        }
    }

    /**
     * Gets the number of lines in the document.
     * 
     * @return the number of lines in the document.
     */
    public int getLines() {

        return getEditor().getLines();
    }

    /**
     * Gets the number of soft wrapped lines this line makes up.
     * 
     * @return the number of soft lines in the line.
     */
    public int getLineStart(int i) throws BadLocationException {

        return getEditor().getLineStart(i);
    }

    /**
     * Gets the number of soft wrapped lines this line makes up.
     * 
     * @return the number of soft lines in the line.
     */
    public int getLineNumber(int y) throws BadLocationException {

        return getEditor().getLineNumber(y);
    }

    public int getNextLineNumber(int i) throws BadLocationException {

        return getEditor().getNextLineNumber(i);
    }

    public ExchangerDocument getDocument() {

        return document;
    }

    /**
     * Inserts the name, selected in the selection dialog.
     */
    public void insertCurrentSelectedName() {

        view.getChangeManager().startCompound(true);
        try {
            String name = selectionPopup.getSelectedName();
            String prefix = selectionPopup.getPrefix();
            int popupType = selectionPopup.getPopupType();
            boolean attributeCompletion = ((XmlDocument) getEditor()
                    .getDocument()).getAttributeName(getEditor()
                    .getCaretPosition()) != null;
            XmlDocument document = (XmlDocument) getEditor().getDocument();
            if (popupType == NameSelectionPopup.POPUP_TYPE_ATTRIBUTE) {
                if (attributeCompletion && !name.endsWith(":")
                        && !name.endsWith("\"")) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length())
                                    + "=\"\"");
                    getEditor().setCaretPosition(
                            getEditor().getCaretPosition() - 1);
                    promptText();
                }
                else if (attributeCompletion) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length()));
                    if (name.endsWith(":")) {
                        promptText();
                    }
                }
                else { // attribute value
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length()));
                    char next = ((XmlDocument) getEditor().getDocument())
                            .getLastCharacter(getEditor().getCaretPosition() + 1);
                    if ((next == '"' || next == '\'')
                            && !(this.document.isXSL() && name.startsWith("$"))) {
                        getEditor().setCaretPosition(
                                getEditor().getCaretPosition() + 1);
                    }
                }
            }
            else {
                if (prefix.startsWith("/")) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length())
                                    + ">");
                }
                else if (name.equals("!--")) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length())
                                    + "-->");
                    getEditor().setCaretPosition(
                            getEditor().getCaretPosition() - 3);
                }
                else if (name.equals("![CDATA[")) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length())
                                    + "]]>");
                    getEditor().setCaretPosition(
                            getEditor().getCaretPosition() - 3);
                }
                else if (name.equals("&lt;")
                        && (prefix == null || prefix.trim().length() == 0)) {
                    getEditor().select(getEditor().getCaretPosition() - 1,
                            getEditor().getCaretPosition());
                    getEditor().replaceSelection("&lt;");
                }
                else if (popupType == NameSelectionPopup.POPUP_TYPE_ENTITY) {
                    if (name.equals(NameSelectionPopup.SWAP_ENTITY_STRING)) {
                        selectionPopup.swap();
                        selectionPopup.setEntities(prefix);
                    }
                    else if (name
                            .equals(NameSelectionPopup.MORE_ENTITIES_STRING)) {
                        hideSelectionPopup();
                        getEditor().select(getEditor().getCaretPosition() - 1,
                                getEditor().getCaretPosition());
                        parent.getInsertEntityAction().execute();
                    }
                    else if (selectionPopup.isSwapped()) {
                        getEditor().select(
                                getEditor().getCaretPosition()
                                        - prefix.length(),
                                getEditor().getCaretPosition());
                        getEditor().replaceSelection(name);
                    }
                    else {
                        getEditor().select(
                                getEditor().getCaretPosition()
                                        - prefix.length(),
                                getEditor().getCaretPosition());
                        getEditor().replaceSelection(name);
                    }
                }
                else if (name.startsWith("/")) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length())
                                    + ">");
                }
                else if (popupType == NameSelectionPopup.POPUP_TYPE_DECLARATION) {
                    if (name.equals("PUBLIC") || name.equals("SYSTEM")) {
                        getEditor().replaceSelection(
                                name.substring(prefix.length(), name.length())
                                        + " \"\"");
                        getEditor().setCaretPosition(
                                getEditor().getCaretPosition() - 1);
                    }
                    else {
                        getEditor().replaceSelection(
                                name.substring(prefix.length(), name.length()));
                    }
                }
                else if (popupType == NameSelectionPopup.POPUP_TYPE_ELEMENT
                        && name.endsWith(":")) {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length()));
                    promptText();
                }
                else {
                    getEditor().replaceSelection(
                            name.substring(prefix.length(), name.length()));
                }
            }
            if (!name.equals(NameSelectionPopup.SWAP_ENTITY_STRING)
                    && !name.equals(NameSelectionPopup.MORE_ENTITIES_STRING)) {
                hideSelectionPopup();
            }
        }
        finally {
            view.getChangeManager().endCompound();
        }
    }

    /**
     * Inserts the name, selected in the selection dialog.
     */
    public void insert(String text) {

        getEditor().replaceSelection(text);
    }

    /**
     * Has the editor got the latest information in the document.
     * 
     * @return true when no document updates have been received after the
     *         latest text has been set.
     */
    public boolean hasLatestInformation() {

        return hasLatest;
    }

    public void setProperties() {

        //
    }

    public void setHighlight(boolean enabled) {

        topEditor.getEditor().setHighlight(enabled);
        bottomEditor.getEditor().setHighlight(enabled);
        this.revalidate();
        this.repaint();
    }

    public void setErrorHighlighting(boolean enabled) {

        topEditor.getEditor().setErrorHighlighting(enabled);
        bottomEditor.getEditor().setErrorHighlighting(enabled);
        this.revalidate();
        this.repaint();
    }

    public boolean isHighlight() {

        return getEditor().isHighlight();
    }

    // Implementation of the ExchangerDocumentListener interface...
    public void documentUpdated(ExchangerDocumentEvent event) {

        if (DEBUG)
                System.out.println("Editor.documentUpdated( " + event + ")");
        if (event.getType() == ExchangerDocumentEvent.CONTENT_UPDATED
                || event.getType() == ExchangerDocumentEvent.TEXT_UPDATED) {
            hasLatest = false;
        }
    }

    // Implementation of UndoableEditListener
    /**
     * Messaged when the Document has created an edit, the edit is added to
     * <code>undo</code>, an instance of UndoManager.
     */
    public void undoableEditHappened(UndoableEditEvent e) {

        if (DEBUG)
                System.out.println("Editor.undoableEditHappened( "
                        + e.getSource() + ")");
        view.getChangeManager().addEdit(e.getEdit());
        if (!view.getChangeManager().isCompound()) {
            getEditor().repaint();
            if (insertHappened) {
                //		    	System.out.println( "caretUpdate()");
                //				examiner.setPosition( getEditor().getCaretPosition());
                promptText();
            }
        }
    }

    private Vector getCurrentAttributeValues() {

        if (getEditor() != null) {
            int off = getEditor().getCaretPosition();
            Tag tag = getCurrentTag();
            if (tag != null) {
                String attribute = tag.getAttributeName(off);
                return getAttributeValues(attribute);
            }
        }
        return null;
    }

    private Vector getCurrentAttributes() {

        if (selectedEditor != null) {
            Vector result = new Vector();
            SchemaDocument schema = null;
            Tag child = getCurrentTag();
            if (child != null && child.getType() == Tag.END_TAG) {
                child = ((XmlDocument) getEditor().getDocument())
                        .getStartTag(child);
            }
            else if (child == null
                    || (child.getType() != Tag.START_TAG && child.getType() != Tag.EMPTY_TAG)) {
                child = getParentStartTag();
            }
            Tag tag = getParentStartTag(child);
            while (tag != null) {
                ElementInformation element = getElement(tag, child);
                if (element != null) {
                    result.add(element.getAttributes());
                    break;
                }
                tag = getParentStartTag(tag);
            }
            // could not find tag...
            if (child != null && tag == null) {
                Vector elements = getAllElements();
                for (int i = 0; i < elements.size(); i++) {
                    ElementInformation model = (ElementInformation) elements
                            .elementAt(i);
                    if (child.getQualifiedName().equals(
                            model.getQualifiedName())) {
                        result.add(model.getAttributes());
                        break;
                    }
                }
            }
            result.add(getAnyAttributes());
            return result;
        }
        return null;
    }

    public ElementInformation getCurrentElement() {

        if (selectedEditor != null) {
            SchemaDocument schema = null;
            Tag tag = getCurrentTag();
            if (tag != null && tag.getType() == Tag.END_TAG) {
                tag = ((XmlDocument) getEditor().getDocument())
                        .getStartTag(tag);
            }
            else if (tag == null
                    || (tag.getType() != Tag.START_TAG && tag.getType() != Tag.EMPTY_TAG)) {
                tag = getParentStartTag();
            }
            if (schemas != null && schemas.size() > 0 && tag != null) {
                for (int i = 0; i < schemas.size() && schema == null; i++) {
                    Vector elements = ((SchemaDocument) schemas.elementAt(i))
                            .getElements();
                    for (int j = 0; j < elements.size(); j++) {
                        if (((ElementInformation) elements.elementAt(j))
                                .getQualifiedName().equals(
                                        tag.getQualifiedName())) {
                            schema = (SchemaDocument) schemas.elementAt(i);
                            break;
                        }
                    }
                }
            }
            if (schema != null) {
                while (tag != null) {
                    ElementInformation element = getElement(schema, tag);
                    if (element != null) {
                        return element;
                    }
                    tag = getParentStartTag(tag);
                }
            }
        }
        return null;
    }

    private Vector getCurrentChildElements() {

        if (selectedEditor != null) {
            Vector result = new Vector();
            SchemaDocument schema = null;
            Tag tag = getCurrentTag();
            if (tag != null && tag.getType() == Tag.END_TAG) {
                tag = ((XmlDocument) getEditor().getDocument())
                        .getStartTag(tag);
            }
            else if (tag == null
                    || (tag.getType() != Tag.START_TAG && tag.getType() != Tag.EMPTY_TAG)) {
                tag = getParentStartTag();
            }
            if (schemas != null && schemas.size() > 0 && tag != null) {
                for (int i = 0; i < schemas.size() && schema == null; i++) {
                    Vector elements = ((SchemaDocument) schemas.elementAt(i))
                            .getElements();
                    for (int j = 0; j < elements.size(); j++) {
                        if (((ElementInformation) elements.elementAt(j))
                                .getQualifiedName().equals(
                                        tag.getQualifiedName())) {
                            schema = (SchemaDocument) schemas.elementAt(i);
                            break;
                        }
                    }
                }
            }
            if (schema != null) {
                result.addElement(getChildElements(schema, tag));
            }
            if (schemas != null) {
                for (int i = 0; i < schemas.size(); i++) {
                    if (schemas.elementAt(i) != schema) {
                        result.addElement(getChildElements(
                                (SchemaDocument) schemas.elementAt(i), tag));
                    }
                }
            }
            return result;
        }
        return null;
    }

    private Vector getCurrentSiblings() {

        if (DEBUG) System.out.println("Editor.getCurrentSiblings()");
        if (selectedEditor != null) {
            Vector result = new Vector();
            SchemaDocument schema = null;
            Tag tag = getCurrentTag();
            if (tag != null && tag.getType() == Tag.END_TAG) {
                tag = ((XmlDocument) getEditor().getDocument())
                        .getStartTag(tag);
            }
            else if (tag == null
                    || (tag.getType() != Tag.START_TAG && tag.getType() != Tag.EMPTY_TAG)) {
                tag = getParentStartTag();
            }
            if (tag != null) {
                tag = ((XmlDocument) getEditor().getDocument())
                        .getParentStartTag(tag.getStart());
                if (schemas != null && schemas.size() > 0 && tag != null) {
                    for (int i = 0; i < schemas.size() && schema == null; i++) {
                        Vector elements = ((SchemaDocument) schemas
                                .elementAt(i)).getElements();
                        if (elements != null) {
                            for (int j = 0; j < elements.size(); j++) {
                                if (((ElementInformation) elements.elementAt(j))
                                        .getQualifiedName().equals(
                                                tag.getQualifiedName())) {
                                    schema = (SchemaDocument) schemas
                                            .elementAt(i);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (schema != null) {
                    result.addElement(getChildElements(schema, tag));
                }
            }
            if (schemas != null) {
                for (int i = 0; i < schemas.size(); i++) {
                    if (schemas.elementAt(i) != schema) {
                        result.addElement(getChildElements(
                                (SchemaDocument) schemas.elementAt(i), tag));
                    }
                }
            }
            return result;
        }
        return null;
    }

    protected void promptText() {

        if (properties.isTextPrompting()) {
            int off = getEditor().getCaretPosition();
            //			if ( ((XmlDocument)getEditor().getDocument()).isComment( off) ||
            // ((XmlDocument)getEditor().getDocument()).isCDATA( off)) {
            //				return;
            //			}
            if (document.isDTD()) {
                Tag tag = getCurrentTag();
                String value = ((XmlDocument) getEditor().getDocument())
                        .getString(off);
                String name = null;
                if (tag != null) {
                    name = tag.getName();
                }
                if (value == null) {
                    value = "";
                }
                if (name != null) {
                    if (name.equals("ELEMENT")) {
                        showElementDeclarationSelectionPopup(tag, off, value);
                    }
                    else if (name.equals("ATTLIST")) {
                        showAttlistDeclarationSelectionPopup(tag, off, value);
                    }
                    else if (name.equals("ENTITY")) {
                        showEntityDeclarationSelectionPopup(tag, off, value);
                    }
                    else if (name.equals("DOCTYPE")) {
                        showDoctypeDeclarationSelectionPopup(tag, off, value);
                    }
                    else if (name.equals("NOTATION")) {
                        showNotationDeclarationSelectionPopup(tag, off, value);
                    }
                    else if (value != null) {
                        if (tag.getType() == Tag.DECLARATION_TAG) {
                            showDeclarationSelectionPopup(
                                    getDeclarationNames(), "!" + name);
                        }
                        else {
                            showDeclarationSelectionPopup(
                                    getDeclarationNames(), name);
                        }
                    }
                }
                else if (tag != null) {
                    showDeclarationSelectionPopup(getDeclarationNames(), value);
                }
            }
            else {
                char last = ((XmlDocument) getEditor().getDocument())
                        .getLastCharacter(off);
                String attr = ((XmlDocument) getEditor().getDocument())
                        .getAttributeName(off);
                String val = ((XmlDocument) getEditor().getDocument())
                        .getAttributeValue(off);
                Tag parentTag = null;
                Tag tag = getCurrentTag();
                Tag doctypeTag = ((XmlDocument) getEditor().getDocument())
                        .getDoctypeDeclarationTag();
                if (doctypeTag == null) {
                    parentTag = getParentStartTag();
                }
                boolean inDoctype = false;
                if (tag != null && doctypeTag == null && parentTag == null) {
                    String name = tag.getName();
                    if (tag.getName() == null
                            || tag.getName().trim().length() == 0
                            || (tag.getName() != null && tag.getType() == Tag.DECLARATION_TAG)) {
                        inDoctype = true;
                    }
                }
                else if (tag != null && doctypeTag != null
                        && doctypeTag.getEnd() >= tag.getStart()) {
                    inDoctype = true;
                }
                if (inDoctype) {
                    String value = ((XmlDocument) getEditor().getDocument())
                            .getString(off);
                    String name = tag.getName();
                    if (value == null) {
                        value = "";
                    }
                    if (name != null) {
                        //						System.out.println( "name = "+name);
                        if (name.equals("ELEMENT")) {
                            showElementDeclarationSelectionPopup(tag, off,
                                    value);
                        }
                        else if (name.equals("ATTLIST")) {
                            showAttlistDeclarationSelectionPopup(tag, off,
                                    value);
                        }
                        else if (name.equals("ENTITY")) {
                            showEntityDeclarationSelectionPopup(tag, off, value);
                        }
                        else if (name.equals("DOCTYPE")) {
                            showDoctypeDeclarationSelectionPopup(tag, off,
                                    value);
                        }
                        else if (name.equals("NOTATION")) {
                            showNotationDeclarationSelectionPopup(tag, off,
                                    value);
                        }
                        else if (doctypeTag == null) {
                            Vector doctypeName = new Vector();
                            doctypeName.addElement("!DOCTYPE");
                            showDeclarationSelectionPopup(doctypeName, value);
                        }
                        else {
                            if (tag.getType() == Tag.DECLARATION_TAG) {
                                showDeclarationSelectionPopup(
                                        getDeclarationNames(), "!" + name);
                            }
                            else {
                                showDeclarationSelectionPopup(
                                        getDeclarationNames(), name);
                            }
                        }
                    }
                    else {
                        if (doctypeTag == null) {
                            Vector doctypeName = new Vector();
                            doctypeName.addElement("!DOCTYPE");
                            showDeclarationSelectionPopup(doctypeName, value);
                        }
                        else {
                            showDeclarationSelectionPopup(
                                    getDeclarationNames(), value);
                        }
                    }
                }
                else if (val != null && last != '\n' && last != '\r') {
                    if (tag != null
                            && (tag.getType() == Tag.START_TAG || tag.getType() == Tag.EMPTY_TAG)) {
                        if (document.isXSL() && val != null) {
                            int index = val.lastIndexOf('$');
                            if (index != -1) {
                                val = val.substring(index);
                                examiner.getValues(getValuesPopupListener(val));
                            }
                        }
                        examiner.getValues(getValuesPopupListener(val));
                    }
                }
                else if (attr != null && last != '\n' && last != '\r') {
                    if (tag != null
                            && (tag.getType() == Tag.START_TAG || tag.getType() == Tag.EMPTY_TAG)) {
                        examiner.getAttributes(getAttributesPopupListener(attr,
                                tag));
                        //						showAttributeSelectionPopup( attr, tag);
                    }
                }
                else {
                    String elem = ((XmlDocument) getEditor().getDocument())
                            .getElementName(off);
                    if (elem != null) {
                        Tag currentTag = ((XmlDocument) getEditor()
                                .getDocument()).getCurrentTag(off
                                - (elem.length() + 1));
                        if (currentTag == null) {
                            String parent = ((XmlDocument) getEditor()
                                    .getDocument()).getParentElementName(off);
                            String name = null;
                            if (tag != null) {
                                name = tag.getName();
                            }
                            if (elem.startsWith("/")
                                    && elem.length() == 1
                                    && (name == null || name.trim().length() == 0)) {
                                getEditor().replaceSelection(
                                        parent.substring(elem.length() - 1,
                                                parent.length())
                                                + ">");
                            }
                            else if (name == null || !name.equals(parent)) {
                                examiner.getElements(getElementsPopupListener(
                                        elem, parent));
                            }
                        }
                        else if (elem.length() == 0) { // '<' in attribute
                                                       // value...
                            if (currentTag.getType() == Tag.DECLARATION_TAG) {
                                //								 	System.out.println( "In Declaration Tag:
                                // "+currentTag);
                            }
                            else {
                                showEntitySelectionPopup("<");
                            }
                        }
                    }
                    else {
                        String entity = ((XmlDocument) getEditor()
                                .getDocument()).getEntityName(off);
                        Tag currentTag = ((XmlDocument) getEditor()
                                .getDocument()).getCurrentTag(off - 1);
                        if (entity != null) {
                            showEntitySelectionPopup(entity);
                        }
                        else if (currentTag == null) { // the last character
                                                       // typed was in a
                                                       // Element Value
                            char lastChar = ((XmlDocument) getEditor()
                                    .getDocument()).getLastCharacter(off);
                            if (lastChar != -1
                                    && (lastChar == '\'' || lastChar == '"' || lastChar == '>')) {
                                showEntitySelectionPopup("" + lastChar);
                            }
                        }
                        else if (currentTag.getType() == Tag.DECLARATION_TAG) { // were
                                                                                // in
                                                                                // an
                                                                                // attribute
                                                                                // value
                                                                                // ...
                            //								System.out.println( "In Declaration Tag:
                            // "+currentTag);
                        }
                        else if (currentTag.getType() == Tag.START_TAG
                                && currentTag.inAttributeValue(off)) { // were
                                                                       // in an
                                                                       // attribute
                                                                       // value
                                                                       // ...
                            char lastChar = ((XmlDocument) getEditor()
                                    .getDocument()).getLastCharacter(off);
                            if (lastChar != -1
                                    && (lastChar == '\'' || lastChar == '"' || lastChar == '>')) {
                                showEntitySelectionPopup("" + lastChar);
                            }
                        }
                    }
                }
            }
        }
    }

    // return the popup menu
    private JPopupMenu getPopupMenu() {

        if (popup == null) {
            popup = new JPopupMenu();
            popup.add(parent.getSelectElementAction());
            popup.add(parent.getSelectElementContentAction());
            popup.addSeparator();
            popup.add(parent.getParseAction());
            popup.add(parent.getValidateAction());
            popup.addSeparator();
            popup.add(parent.getDefaultScenarioAction());
            popup.add(parent.getSendSOAPAction());
            popup.addSeparator();
            popup.add(parent.getCutAction());
            popup.add(parent.getCopyAction());
            popup.add(parent.getPasteAction());
            popup.addSeparator();
            popup.add(parent.getUndoAction());
            popup.add(parent.getRedoAction());
            popup.addSeparator();
            popup.add(parent.getSaveAction());
            popup.add(parent.getSaveAsAction());
            popup.addSeparator();
            popup.add(parent.getCloseAction());
        }
        return popup;
    }

    public void setSchemas(Vector schemas) {

        this.schemas = schemas;
        allElements = null;
        globalElements = null;
    }
    
    public Vector getSchemas() {
    	return(schemas);
    }

    public void setElementNames(Vector names) {

        this.elementNames = names;
    }

    public void setNamespaces(Vector namespaces) {

        if (namespaces == null || namespaces.size() == 0) {
            this.namespaces = null;
        }
        else {
            this.namespaces = namespaces;
        }
    }

    public void setEntityNames(Vector names) {

        this.entityNames = names;
    }

    public void setAttributeNames(Hashtable names) {

        this.attributeNames = names;
    }

    private String stripPrefix(String name) {

        if (name != null) {
            int index = name.indexOf(':');
            if (index != -1) {
                name = name.substring(index + 1, name.length());
            }
        }
        return name;
    }

    private Vector getAnyElements() {

        Vector elements = new Vector();
        if (schemas != null) {
            for (int i = 0; i < schemas.size(); i++) {
                Vector elems = ((SchemaDocument) schemas.elementAt(i))
                        .getAnyElements();
                if (elems != null) {
                    elements.addAll(elems);
                }
            }
        }
        return elements;
    }

    public Vector getAllElements() {

        if (allElements == null) {
            allElements = new Vector();
            if (schemas != null) {
                for (int i = 0; i < schemas.size(); i++) {
                    Vector elems = ((SchemaDocument) schemas.elementAt(i))
                            .getElements();
                    if (elems != null) {
                        allElements.addAll(elems);
                    }
                }
            }
        }
        return allElements;
    }

    private Vector getAttributeNames() {

        Vector attributes = null;
        if (attributeNames != null) {
            attributes = new Vector(attributeNames.keySet());
        }
        else {
            attributes = new Vector();
        }
        attributes.addAll(getAnyAttributeNames());
        return attributes;
    }

    // Returns the elements as been defined in the document.
    public Vector getElementNames() {

        Vector elements = null;
        if (elementNames != null) {
            elements = new Vector(elementNames);
            elements.addAll(getAnyChildElementNames());
        }
        else {
            elements = new Vector();
        }
        return elements;
    }

    private Vector getAnyAttributes() {

        Vector anyElements = getAnyElements();
        Vector attributes = new Vector();
        for (int i = 0; i < anyElements.size(); i++) {
            Vector attribs = ((ElementInformation) anyElements.elementAt(i))
                    .getAttributes();
            if (attribs != null) {
                for (int j = 0; j < attribs.size(); j++) {
                    if (!attributes.contains(attribs.elementAt(j))) {
                        attributes.addElement(attribs.elementAt(j));
                    }
                }
            }
        }
        return attributes;
    }

    private Vector getAnyAttributeNames() {

        Vector anyElements = getAnyElements();
        Vector attributes = new Vector();
        for (int i = 0; i < anyElements.size(); i++) {
            Vector attribs = ((ElementInformation) anyElements.elementAt(i))
                    .getAttributes();
            for (int j = 0; j < attribs.size(); j++) {
                attributes.addElement(((AttributeInformation) attribs
                        .elementAt(j)).getQualifiedName());
            }
        }
        return attributes;
    }

    private Vector getAnyChildElements() {

        Vector anyElements = getAnyElements();
        Vector elements = new Vector();
        for (int i = 0; i < anyElements.size(); i++) {
            Vector children = ((ElementInformation) anyElements.elementAt(i))
                    .getChildElements();
            if (children != null) {
                elements.addAll(children);
            }
        }
        return elements;
    }

    private Vector getAnyChildElements(SchemaDocument schema) {

        Vector anyElements = schema.getAnyElements();
        Vector elements = new Vector();
        if (anyElements != null) {
            for (int i = 0; i < anyElements.size(); i++) {
                Vector children = ((ElementInformation) anyElements
                        .elementAt(i)).getChildElements();
                if (children != null) {
                    elements.addAll(children);
                }
            }
        }
        return elements;
    }

    private Vector getAnyChildElementNames() {

        Vector anyElements = getAnyElements();
        Vector elements = new Vector();
        for (int i = 0; i < anyElements.size(); i++) {
            Vector elems = ((ElementInformation) anyElements.elementAt(i))
                    .getChildElements();
            for (int j = 0; j < elems.size(); j++) {
                elements.addElement(((ElementInformation) elems.elementAt(j))
                        .getQualifiedName());
            }
        }
        return elements;
    }

    private ElementInformation getElement(Tag parent, Tag child) {

        String parentName = parent.getQualifiedName();
        Vector elements = getAllElements();
        for (int i = 0; i < elements.size(); i++) {
            ElementInformation model = (ElementInformation) elements
                    .elementAt(i);
            if (parentName.equals(model.getQualifiedName())) {
                Vector children = model.getChildElements();
                for (int j = 0; j < children.size(); j++) {
                    ElementInformation element = (ElementInformation) children
                            .elementAt(j);
                    if (child.getQualifiedName().equals(
                            element.getQualifiedName())) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    private ElementInformation getElement(SchemaDocument schema, Tag parent,
            Tag child) {

        String parentName = parent.getQualifiedName();
        Vector elements = schema.getElements();
        //		System.out.println( "getChildElements( "+URLUtilities.getFileName(
        // schema.getURL())+", "+parent.getQualifiedName()+",
        // "+child.getQualifiedName()+")");
        for (int i = 0; i < elements.size(); i++) {
            ElementInformation model = (ElementInformation) elements
                    .elementAt(i);
            if (parentName.equals(model.getQualifiedName())) {
                Vector children = model.getChildElements();
                for (int j = 0; j < children.size(); j++) {
                    ElementInformation element = (ElementInformation) children
                            .elementAt(j);
                    if (child.getQualifiedName().equals(
                            element.getQualifiedName())) {
                        return element;
                    }
                }
            }
        }
        return null;
    }

    // Finds the correct child element for a tag.
    private ElementInformation getElement(SchemaDocument schema, Tag child) {

        Vector elements = schema.getElements();
        String name = child.getQualifiedName();
        //		System.out.println( "getElement( "+URLUtilities.getFileName(
        // schema.getURL())+", "+child.getQualifiedName()+")");
        for (int i = 0; i < elements.size(); i++) {
            ElementInformation model = (ElementInformation) elements
                    .elementAt(i);
            if (name.equals(model.getQualifiedName())) {
                Tag parent = getParentStartTag(child);
                // Found an element, know this element is in this schema.
                // Now get the correct element based on it's parent.
                while (parent != null) {
                    ElementInformation element = getElement(schema, parent,
                            child);
                    if (element != null) {
                        return element;
                    }
                    parent = getParentStartTag(parent);
                }
                return model;
            }
        }
        return null;
    }

    private Vector getChildElements(SchemaDocument schema, Tag tag) {

        Vector result = new Vector();
        while (tag != null) {
            ElementInformation element = getElement(schema, tag);
            if (element != null) {
                result.addAll(element.getChildElements());
                break;
            }
            tag = getParentStartTag(tag);
        }
        if (tag == null) {
            result.addAll(schema.getGlobalElements());
        }
        result.addAll(getAnyChildElements(schema));
        return result;
    }

    private Vector getChildElements(SchemaDocument schema) {

        return getChildElements(schema, getParentStartTag());
    }

    private Vector getAttributes() {

        Vector result = new Vector();
        Tag child = getCurrentTag();
        Tag tag = getCurrentStartTag();
        if (child == null) {
            child = tag;
            tag = getParentStartTag();
        }
        while (tag != null) {
            ElementInformation element = getElement(tag, child);
            if (element != null) {
                result.add(element.getAttributes());
                break;
            }
            tag = getParentStartTag(tag);
        }
        // could not find tag...
        if (child != null && tag == null) {
            Vector elements = getAllElements();
            for (int i = 0; i < elements.size(); i++) {
                ElementInformation model = (ElementInformation) elements
                        .elementAt(i);
                if (child.getQualifiedName().equals(model.getQualifiedName())) {
                    result.add(model.getAttributes());
                    break;
                }
            }
        }
        result.add(getAnyAttributes());
        return result;
    }

    private SchemaDocument getCurrentSchema() {

        Tag tag = getParentStartTag();
        SchemaDocument schema = null;
        if (schemas != null && schemas.size() > 0 && tag != null) {
            schema = (SchemaDocument) schemas.elementAt(0);
            if (schemas.size() > 1) {
                for (int i = 0; i < schemas.size(); i++) {
                    Vector elements = ((SchemaDocument) schemas.elementAt(i))
                            .getElements();
                    for (int j = 0; j < elements.size(); j++) {
                        if (((ElementInformation) elements.elementAt(j))
                                .getQualifiedName().equals(
                                        tag.getQualifiedName())) {
                            return (SchemaDocument) schemas.elementAt(i);
                        }
                    }
                }
            }
            else {
                return schema;
            }
        }
        return null;
    }

    private Vector getChildElements() {

        Vector result = new Vector();
        SchemaDocument currentSchema = getCurrentSchema();
        if (currentSchema != null) {
            result.add(getChildElements(currentSchema));
        }
        if (schemas != null) {
            for (int i = 0; i < schemas.size(); i++) {
                if (schemas.elementAt(i) != currentSchema) {
                    result.add(getChildElements((SchemaDocument) schemas
                            .elementAt(i)));
                }
            }
        }
        return result;
    }

    public void getCurrentElement(CurrentElementListener listener) {

        examiner.getCurrentElement(listener);
    }

    public void getCurrentSiblings(CurrentSiblingsListener listener) {

        examiner.getCurrentSiblings(listener);
    }

    public void getCurrentElements(CurrentElementsListener listener) {

        examiner.getCurrentElements(listener);
    }

    public void getCurrentAttributes(CurrentAttributesListener listener) {

        examiner.getCurrentAttributes(listener);
    }

    private Vector getAttributeValues(String qname) {

        //		System.out.println( "getAttributeValues( "+qname+")");
        String attributeName = stripPrefix(qname);
        Tag child = getCurrentTag();
        Tag tag = getCurrentStartTag();
        if (document.isXSD()) {
            if (attributeName.equals("ref")) {
                if (child.getName().equals("attributeGroup")) {
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root
                                .getElements("attributeGroup");
                        Vector results = new Vector();
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                        return results;
                    }
                }
                else if (child.getName().equals("attribute")) {
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("attribute");
                        Vector results = new Vector();
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                        return results;
                    }
                }
                else if (child.getName().equals("element")) {
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("element");
                        Vector results = new Vector();
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                        return results;
                    }
                }
                else if (child.getName().equals("group")) {
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("group");
                        Vector results = new Vector();
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                        return results;
                    }
                }
            }
            else if (attributeName.equals("base")) {
                Tag parent = getParentStartTag(child);
                if (parent != null
                        && (parent.getName().equals("simpleContent") || parent
                                .getName().equals("simpleType"))) {
                    Vector results = getSimpleTypeValues();
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("simpleType");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                    }
                    return results;
                }
                else {
                    Vector results = new Vector();
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("complexType");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                        return results;
                    }
                }
            }
            else if (attributeName.equals("type")) {
                if (child != null && (child.getName().equals("attribute"))) {
                    Vector results = getSimpleTypeValues();
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("simpleType");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                    }
                    return results;
                }
                else if (child != null && (child.getName().equals("element"))) {
                    Vector results = getSimpleTypeValues();
                    XElement root = document.getLastRoot();
                    if (root != null) {
                        String targetNS = root.getAttribute("targetNamespace");
                        String prefix = null;
                        if (targetNS != null && targetNS.length() > 0) {
                            Namespace ns = root.getNamespaceForURI(targetNS);
                            prefix = ns.getPrefix();
                        }
                        XElement[] elements = root.getElements("simpleType");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                        elements = root.getElements("complexType");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                if (prefix != null && prefix.length() > 0) {
                                    results.addElement(new AttributeValue(
                                            prefix + ":" + value,
                                            AttributeValue.NORMAL_TYPE));
                                }
                                else {
                                    results.addElement(new AttributeValue(
                                            value, AttributeValue.NORMAL_TYPE));
                                }
                            }
                        }
                    }
                    return results;
                }
            }
        }
        else if (document.isRNG()) {
            if (attributeName.equals("name")) {
                if (child.getName().equals("ref")) {
                    XElement root = document.getLastRoot();
                    Vector results = new Vector();
                    if (root != null) {
                        XElement[] elements = root.getElements("define");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                results.addElement(new AttributeValue(value,
                                        AttributeValue.NORMAL_TYPE));
                            }
                        }
                    }
                    return results;
                }
            }
            else if (attributeName.equals("type")) {
                Vector results = new Vector();
                for (int i = 0; i < XSD_SIMPLE_TYPES.length; i++) {
                    results.addElement(new AttributeValue(XSD_SIMPLE_TYPES[i],
                            AttributeValue.NORMAL_TYPE));
                }
                return results;
            }
        }
        else if (document.isXSL()) {
            if (attributeName.equals("name")) {
                if (child.getName().equals("call-template")) {
                    XElement root = document.getLastRoot();
                    Vector results = new Vector();
                    if (root != null) {
                        XElement[] elements = root.getElements("template");
                        for (int i = 0; i < elements.length; i++) {
                            String value = elements[i].getAttribute("name");
                            if (value != null && value.length() > 0) {
                                results.addElement(new AttributeValue(value,
                                        AttributeValue.NORMAL_TYPE));
                            }
                        }
                    }
                    return results;
                }
            }
            else {
                String val = ((XmlDocument) getEditor().getDocument())
                        .getAttributeValue(getCursorPosition());
                int index = val.lastIndexOf('$');
                if (index != -1) {
                    Vector results = new Vector();
                    XElement root = document.getLastRoot();
                    String prefix = "xsl";
                    if (root != null) {
                        Namespace ns = root
                                .getNamespaceForURI("http://www.w3.org/1999/XSL/Transform");
                        prefix = ns.getPrefix();
                    }
                    Vector elements = document
                            .searchLastWellFormedDocument("//" + prefix
                                    + ":param | //" + prefix + ":variable");
                    for (int i = 0; i < elements.size(); i++) {
                        XElement element = (XElement) elements.elementAt(i);
                        String value = element.getAttribute("name");
                        if (value != null && value.length() > 0) {
                            results.addElement(new AttributeValue("$" + value,
                                    AttributeValue.NORMAL_TYPE));
                        }
                    }
                    return results;
                }
            }
        }
        while (tag != null) {
            ElementInformation element = getElement(tag, child);
            if (element != null) {
                Vector attributes = element.getAttributes();
                
                //tcurley - fixed this bug which caused an infinite loop
				//of null pointer exceptions
                if(attributes != null) {
                	
	                attributes.addAll(getAnyAttributes());
	                
	                for (int k = 0; k < attributes.size(); k++) {
	                    AttributeInformation a = (AttributeInformation) attributes
	                            .elementAt(k);
	                    if (a.getName().equals(attributeName)) {
	                        Vector results = new Vector();
	                        Vector values = a.getValues();
	                        if (attributeNames != null) {
	                            Vector otherValues = (Vector) attributeNames
	                                    .get(qname);
	                            if (otherValues != null) {
	                                for (int i = 0; i < otherValues.size(); i++) {
	                                    if (!contains(values, (String) otherValues
	                                            .elementAt(i))) {
	                                        results.addElement(new AttributeValue(
	                                                (String) otherValues
	                                                        .elementAt(i),
	                                                AttributeValue.NORMAL_TYPE));
	                                    }
	                                }
	                            }
	                        }
	                        if (values != null) {
	                            for (int i = 0; i < values.size(); i++) {
	                                results.addElement(values.elementAt(i));
	                            }
	                        }
	                        return results;
	                    }
	                }
	                if (attributeNames != null) {
	                    Vector otherValues = (Vector) attributeNames.get(qname);
	                    if (otherValues != null) {
	                        Vector results = new Vector();
	                        for (int i = 0; i < otherValues.size(); i++) {
	                            results.addElement(new AttributeValue(
	                                    (String) otherValues.elementAt(i),
	                                    AttributeValue.NORMAL_TYPE));
	                        }
	                        return results;
	                    }
	                }
	                return null;
                }
            }
            tag = getParentStartTag(tag);
        }
        if (attributeNames != null) {
            Vector otherValues = (Vector) attributeNames.get(qname);
            if (otherValues != null) {
                Vector results = new Vector();
                for (int i = 0; i < otherValues.size(); i++) {
                    results.addElement(new AttributeValue((String) otherValues
                            .elementAt(i), AttributeValue.NORMAL_TYPE));
                }
                return results;
            }
        }
        return null;
    }

    private boolean contains(Vector values, String value) {

        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                if (value.equals(((AttributeValue) values.elementAt(i))
                        .getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Vector getSimpleTypeValues() {

        Vector result = new Vector();
        XElement root = document.getLastRoot();
        String prefix = "xsd";
        if (root != null) {
            Namespace ns = root
                    .getNamespaceForURI("http://www.w3.org/2001/XMLSchema");
            prefix = ns.getPrefix();
        }
        for (int i = 0; i < XSD_SIMPLE_TYPES.length; i++) {
            if (prefix != null && prefix.length() > 0) {
                result.add(new AttributeValue(prefix + ":"
                        + XSD_SIMPLE_TYPES[i], AttributeValue.NORMAL_TYPE));
            }
            else {
                result.add(new AttributeValue(XSD_SIMPLE_TYPES[i],
                        AttributeValue.NORMAL_TYPE));
            }
        }
        return result;
    }

    // Shows the attribute selection popup
    private void showElementSelectionPopup(final String prefix,
            final Vector children, final String parent) {

        //		System.out.println( "showElementSelectionPopup( "+prefix+",
        // "+parent+")");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    if (prefix.startsWith("/")) {
                        Vector names = new Vector();
                        names.addElement(parent);
                        selectionPopup.setElementNames(names, prefix, parent);
                    }
                    else {
                        Vector elements = getAllElements();
                        if (elements != null && elements.size() > 0) { // there
                                                                       // is a
                                                                       // schema
                                                                       // available.
                            selectionPopup
                                    .setElements(children, prefix, parent);
                        }
                        else { // no schema available so only set previous
                               // names
                            selectionPopup.setElementNames(getElementNames(),
                                    prefix, parent);
                        }
                    }
                    if (selectionPopup.getNames() > 1
                            || (selectionPopup.getNames() == 1 && !prefix
                                    .equals(selectionPopup.getSelectedName()))) {
                        int caretPos = getEditor().getCaretPosition();
                        Rectangle pos = getEditor().modelToView(caretPos);
                        FontMetrics fm = getEditor().getFontMetrics(
                                getEditor().getFont());
                        int height = fm.getHeight();
                        int width = fm.getWidths()[0];
                        selectionPopup.show(getEditor(), pos.x
                                - (prefix.length() * width), pos.y + height);
                        getEditor().requestFocusInWindow();
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Shows the attribute selection popup
    private void showDeclarationSelectionPopup(final Vector names,
            final String prefix) {

        //		System.out.println( "showDeclarationSelectionPopup( "+names+",
        // "+prefix+")");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    selectionPopup.setDeclarationNames(names, prefix);
                    if (selectionPopup.getNames() > 1
                            || (selectionPopup.getNames() == 1 && !prefix
                                    .equals(selectionPopup.getSelectedName()))) {
                        int caretPos = getEditor().getCaretPosition();
                        Rectangle pos = getEditor().modelToView(caretPos);
                        FontMetrics fm = getEditor().getFontMetrics(
                                getEditor().getFont());
                        int height = fm.getHeight();
                        int width = fm.getWidths()[0];
                        selectionPopup.show(getEditor(), pos.x
                                - (prefix.length() * width), pos.y + height);
                        getEditor().requestFocusInWindow();
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showElementDeclarationSelectionPopup(
            Tag elementDeclarationTag, int offset, String value) {

        //		System.out.println( "showElementDeclarationSelectionPopup(
        // "+elementDeclarationTag+", "+offset+", "+value+")");
        Vector values = elementDeclarationTag.getValues(offset);
        if (values.size() > 2 && value != null && value.length() > 0) { // last
                                                                        // item
                                                                        // in
                                                                        // values
                                                                        // ==
                                                                        // value
            if (value.endsWith("(") || value.endsWith("|")
                    || value.endsWith(",")) {
                Vector pcdata = new Vector();
                pcdata.addElement("#PCDATA");
                showDeclarationSelectionPopup(pcdata, "");
            }
            else {
                StringTokenizer tokenizer = new StringTokenizer(value, "(|,");
                String lastToken = null;
                while (tokenizer.hasMoreTokens()) {
                    lastToken = tokenizer.nextToken();
                }
                if ("#PCDATA".startsWith(lastToken)) {
                    Vector pcdata = new Vector();
                    pcdata.addElement("#PCDATA");
                    showDeclarationSelectionPopup(pcdata, lastToken);
                }
                else {
                    showDeclarationSelectionPopup(getElementDeclarationTypes(),
                            value);
                }
            }
        }
        else if (values.size() == 2) { // the values do not contain the current
                                       // value
            showDeclarationSelectionPopup(getElementDeclarationTypes(), value);
        }
    }

    private void showNotationDeclarationSelectionPopup(
            Tag elementDeclarationTag, int offset, String value) {

        //		System.out.println( "showNotationDeclarationSelectionPopup(
        // "+elementDeclarationTag+", "+offset+", "+value+")");
        Vector values = elementDeclarationTag.getValues(offset);
        if (values.size() == 3 && value != null && value.length() > 0) { // last
                                                                         // item
                                                                         // in
                                                                         // values
                                                                         // ==
                                                                         // value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value);
        }
        else if (values.size() == 2 && (value == null || value.length() == 0)) { // the
                                                                                 // values
                                                                                 // do
                                                                                 // not
                                                                                 // contain
                                                                                 // the
                                                                                 // current
                                                                                 // value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value);
        }
    }

    private void showEntityDeclarationSelectionPopup(Tag elementDeclarationTag,
            int offset, String value) {

        //		System.out.println( "showEntityDeclarationSelectionPopup(
        // "+elementDeclarationTag+", "+offset+", "+value+")");
        Vector values = elementDeclarationTag.getValues(offset);
        if (values.size() == 3 && value != null && value.length() > 0
                && !values.elementAt(1).equals("%")) { // last item in values
                                                       // == value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value); // SYSTEM,
                                                                                 // PUBLIC
        }
        else if (values.size() == 2 && !values.elementAt(1).equals("%")
                && (value == null || value.length() == 0)) { // the values do
                                                             // not contain the
                                                             // current value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value);
        }
        else if (values.size() == 4 && value != null && value.length() > 0
                && values.elementAt(1).equals("%")) { // last item in values ==
                                                      // value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value); // SYSTEM,
                                                                                 // PUBLIC
        }
        else if (values.size() == 3 && values.elementAt(1).equals("%")
                && (value == null || value.length() == 0)) { // the values do
                                                             // not contain the
                                                             // current value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value);
        }
        else if (values.size() == 4 && (value == null || value.length() == 0)
                && !values.elementAt(1).equals("%")) { // last item in values
                                                       // == value
            Vector ndata = new Vector();
            ndata.addElement("NDATA");
            showDeclarationSelectionPopup(ndata, value); // SYSTEM, PUBLIC
        }
        else if (values.size() > 4 && !values.elementAt(1).equals("%")) { // last
                                                                          // item
                                                                          // in
                                                                          // values
                                                                          // ==
                                                                          // value
            Vector ndata = new Vector();
            ndata.addElement("NDATA");
            showDeclarationSelectionPopup(ndata, value); // SYSTEM, PUBLIC
        }
    }

    private void showDoctypeDeclarationSelectionPopup(
            Tag elementDeclarationTag, int offset, String value) {

        //		System.out.println( "showDoctypeDeclarationSelectionPopup(
        // "+elementDeclarationTag+", "+offset+", "+value+")");
        Vector values = elementDeclarationTag.getValues(offset);
        if (values.size() == 3 && value != null && value.length() > 0) { // last
                                                                         // item
                                                                         // in
                                                                         // values
                                                                         // ==
                                                                         // value
            if (value.equals("[")) {
                getEditor().replaceSelection("]>");
                getEditor()
                        .setCaretPosition(getEditor().getCaretPosition() - 2);
            }
            else {
                showDeclarationSelectionPopup(getNotationDeclarationTypes(),
                        value);
            }
        }
        else if (values.size() == 2 && (value == null || value.length() == 0)) { // the
                                                                                 // values
                                                                                 // do
                                                                                 // not
                                                                                 // contain
                                                                                 // the
                                                                                 // current
                                                                                 // value
            showDeclarationSelectionPopup(getNotationDeclarationTypes(), value);
        }
    }

    private void showAttlistDeclarationSelectionPopup(
            Tag elementDeclarationTag, int offset, String value) {

        //		System.out.println( "showAttlistDeclarationSelectionPopup(
        // "+elementDeclarationTag+", "+offset+", "+value+")");
        Vector values = elementDeclarationTag.getValues(offset);
        boolean hasEntity = false;
        for (int i = 0; (i < values.size()) && (i < 4); i++) {
            if (isEntity((String) values.elementAt(i))) {
                hasEntity = true;
            }
        }
        if (!hasEntity) {
            if (values.size() == 4 && value != null && value.length() > 0) { // last
                                                                             // item
                                                                             // in
                                                                             // values
                                                                             // ==
                                                                             // value
                showDeclarationSelectionPopup(getAttlistDeclarationTypes(),
                        value);
            }
            else if (values.size() == 3
                    && (value == null || value.length() == 0)) { // the values
                                                                 // do not
                                                                 // contain the
                                                                 // current
                                                                 // value
                showDeclarationSelectionPopup(getAttlistDeclarationTypes(),
                        value);
            }
            else if (values.size() == 5 && value != null && value.length() > 0) { // last
                                                                                  // item
                                                                                  // in
                                                                                  // values
                                                                                  // ==
                                                                                  // value
                showDeclarationSelectionPopup(getAttlistDeclarationDefaults(),
                        value);
            }
            else if (values.size() == 4
                    && (value == null || value.length() == 0)) { // the values
                                                                 // do not
                                                                 // contain the
                                                                 // current
                                                                 // value
                showDeclarationSelectionPopup(getAttlistDeclarationDefaults(),
                        value);
            }
            else if (values.size() > 5) {
                String previousValue = null;
                if (value == null || value.length() == 0) {
                    previousValue = (String) values
                            .elementAt(values.size() - 1);
                }
                else {
                    previousValue = (String) values
                            .elementAt(values.size() - 2);
                }
                if (isEntity(previousValue)) {
                    showDeclarationSelectionPopup(getAttlistDeclarationAll(),
                            value);
                }
                else if (isAttlistType(previousValue)) {
                    showDeclarationSelectionPopup(
                            getAttlistDeclarationDefaults(), value);
                }
                else if (!isAttlistDefault(previousValue)) {
                    showDeclarationSelectionPopup(getAttlistDeclarationTypes(),
                            value);
                }
            }
        }
        else if (values.size() > 1) {
            String previousValue = null;
            if (value == null || value.length() == 0) {
                previousValue = (String) values.elementAt(values.size() - 1);
            }
            else {
                previousValue = (String) values.elementAt(values.size() - 2);
            }
            if (isEntity(previousValue)) {
                showDeclarationSelectionPopup(getAttlistDeclarationAll(), value);
            }
            else if (isAttlistType(previousValue)
                    || previousValue.endsWith(")")) {
                showDeclarationSelectionPopup(getAttlistDeclarationDefaults(),
                        value);
            }
            else if (!isAttlistDefault(previousValue)) {
                showDeclarationSelectionPopup(getAttlistDeclarationTypes(),
                        value);
            }
        }
    }

    private boolean isEntity(String value) {

        if (value != null && value.startsWith("%") && value.endsWith(";")) {
            return true;
        }
        return false;
    }

    private boolean isAttlistType(String type) {

        Vector types = getAttlistDeclarationTypes();
        if (type != null) {
            for (int i = 0; i < types.size(); i++) {
                if (types.elementAt(i).equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAttlistDefault(String def) {

        Vector defaults = getAttlistDeclarationDefaults();
        if (def != null) {
            for (int i = 0; i < defaults.size(); i++) {
                if (defaults.elementAt(i).equals(def)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Shows the attribute selection popup
    private void showEntitySelectionPopup(final String prefix) {

        //		System.out.println( "showEntitySelectionPopup( "+prefix+")");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    //						System.out.println( "showEntitySelectionPopup(
                    // "+prefix+")");
                    selectionPopup.setEntities(prefix);
                    if (selectionPopup.getNames() > 1
                            || (selectionPopup.getNames() == 1 && !prefix
                                    .equals(selectionPopup.getSelectedName()))) {
                        int caretPos = getEditor().getCaretPosition();
                        Rectangle pos = getEditor().modelToView(caretPos);
                        FontMetrics fm = getEditor().getFontMetrics(
                                getEditor().getFont());
                        int height = fm.getHeight();
                        int width = fm.getWidths()[0];
                        selectionPopup.show(getEditor(), pos.x
                                - (prefix.length() * width), pos.y + height);
                        getEditor().requestFocusInWindow();
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Hides the selection popup
    private void hideSelectionPopup() {

        //	    System.out.println( "hideSelectionPopup()");
        if (selectionPopup.isVisible()) {
            selectionPopup.setVisible(false);
        }
    }

    private ValuesPopupListener getValuesPopupListener(String prefix) {

        if (valuesPopupListener == null) {
            valuesPopupListener = new ValuesPopupListener();
        }
        valuesPopupListener.setPrefix(prefix);
        return valuesPopupListener;
    }

    private void showAttributeValueSelectionPopup(final String prefix,
            final Vector values) {

        //if (DEBUG) System.out.println(
        // "Editor.showAttributeValueSelectionPopup( "+prefix+", "+parent+")");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    selectionPopup.setAttributeValues(values, prefix);
                    if (selectionPopup.getNames() > 1
                            || (selectionPopup.getNames() == 1 && !prefix
                                    .equals(selectionPopup.getSelectedName()))) {
                        int caretPos = getEditor().getCaretPosition();
                        Rectangle pos = getEditor().modelToView(caretPos);
                        FontMetrics fm = getEditor().getFontMetrics(
                                getEditor().getFont());
                        int height = fm.getHeight();
                        int width = fm.getWidths()[0];
                        selectionPopup.show(getEditor(), pos.x
                                - (prefix.length() * width), pos.y + height);
                        getEditor().requestFocusInWindow();
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private AttributesPopupListener getAttributesPopupListener(String prefix,
            Tag tag) {

        if (attributesPopupListener == null) {
            attributesPopupListener = new AttributesPopupListener();
        }
        attributesPopupListener.setPrefix(prefix);
        attributesPopupListener.setTag(tag);
        return attributesPopupListener;
    }

    private ElementsPopupListener getElementsPopupListener(String prefix,
            String parent) {

        if (elementsPopupListener == null) {
            elementsPopupListener = new ElementsPopupListener();
        }
        elementsPopupListener.setPrefix(prefix);
        elementsPopupListener.setParent(parent);
        return elementsPopupListener;
    }

    // Shows the attribute selection popup
    private void showAttributeSelectionPopup(final String prefix,
            final Vector attributes, final Tag tag) {

        if (DEBUG)
                System.out.println("Editor.showAttributeSelectionPopup( "
                        + prefix + ", " + attributes + ", " + tag + ")");
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {

                try {
                    Vector elements = getAllElements();
                    if (elements != null && elements.size() > 0) { // there is
                                                                   // a schema
                                                                   // available.
                        selectionPopup.setAttributes(attributes, prefix, tag
                                .getAttributeNames(), namespaces);
                    }
                    else {
                        selectionPopup.setAttributeNames(getAttributeNames(),
                                prefix, tag.getAttributeNames(), namespaces);
                    }
                    if (selectionPopup.getNames() > 1
                            || (selectionPopup.getNames() == 1 && !prefix
                                    .equals(selectionPopup.getSelectedName()))) {
                        int caretPos = getEditor().getCaretPosition();
                        Rectangle pos = getEditor().modelToView(caretPos);
                        FontMetrics fm = getEditor().getFontMetrics(
                                getEditor().getFont());
                        int height = fm.getHeight();
                        int width = fm.getWidths()[0];
                        selectionPopup.show(getEditor(), pos.x
                                - (prefix.length() * width), pos.y + height);
                        getEditor().requestFocusInWindow();
                    }
                }
                catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Point getPosition() {

        Element root = getEditor().getDocument().getDefaultRootElement();
        int line = root.getElementIndex(getCursorPosition());
        int start = root.getElement(line).getStartOffset();
        int col = getCursorPosition() - start;
        String text = "";
        try {
            text = getEditor().getText(start, col);
        }
        catch (BadLocationException b) {
        }
        int index = text.indexOf('\t');
        while (index != -1) {
            col = col + (TextPreferences.getTabSize() - 1);
            index = text.indexOf('\t', index + 1);
        }
        return new Point(line + 1, col + 1);
    }

    //	
    //	private int currentStartTagPos = -1;
    //	private Tag currentStartTag = null;
    //	private Tag currentEndTag = null;
    //	
    //	private Tag getCurrentStartTag( int pos) {
    //		
    //		if ( pos != currentStartTagPos) {
    //			XmlDocument doc = (XmlDocument)getEditor().getDocument();
    //			Tag tag = doc.getCurrentTag( pos);
    //			
    //			if ( tag != null && (tag.getType() == Tag.START_TAG || tag.getType() ==
    // Tag.EMPTY_TAG)) {
    //				currentStartTag = tag;
    //			} else {
    //				currentStartTag = doc.getParentStartTag( pos);
    //			}
    //			
    //			currentEndTag = null;
    //		}
    //		
    //		currentStartTagPos = pos;
    //
    //		return currentStartTag;
    //	}
    //
    //	public Tag getEndTag( Tag startTag) {
    //		
    //		if ( startTag == currentStartTag && currentEndTag != null) {
    //			return currentEndTag;
    //		} else {
    //			XmlDocument doc = (XmlDocument)getEditor().getDocument();
    //			Tag tag = null;
    //	
    //			if ( startTag != null && startTag.getType() == Tag.START_TAG) {
    //				tag = doc.getEndTag( startTag);
    //			}
    //			
    //			if ( startTag == currentStartTag) {
    //				currentEndTag = tag;
    //			}
    //	
    //			return tag;
    //		}
    //	}
    public void collapseAll() {

        selectedEditor.getFoldingMargin().foldAll();
    }

    public void expandAll() {

        selectedEditor.getFoldingMargin().unfoldAll();
    }

    public void updateHelper() {

        XmlDocument doc = (XmlDocument) getEditor().getDocument();
        int caretPos = getEditor().getCaretPosition();
        int selectionStart = getEditor().getSelectionStart();
        int selectionEnd = getEditor().getSelectionEnd();
        Tag tag = getCurrentTag();
        parent.getLockAction().setUnlock(getEditor().getLocked());
        parent.getCollapseAllAction().setEnabled(
                selectedEditor.getFoldingMargin().isVisible());
        parent.getExpandAllAction().setEnabled(
                selectedEditor.getFoldingMargin().isVisible());
        // No range has been selected
        if (caretPos == selectionStart && caretPos == selectionEnd) {
            //			if ( document.isXML()) {
            parent.getStripTagsAction().setEnabled(false);
            parent.getSubstituteCharactersAction().setEnabled(false);
            parent.getSubstituteEntitiesAction().setEnabled(false);
            parent.getLockAction().setEnabled(true);
            if (!(getEditor().getLocked() == XmlEditorPane.NOT_LOCKED)) {
                parent.getCommentAction().setEnabled(false);
                parent.getRenameElementAction().setEnabled(false);
                parent.getCDATAAction().setEnabled(false);
                parent.getSplitElementAction().setEnabled(false);
                parent.getHelper().setElementsEnabled(false);
                parent.getHelper().setAttributesEnabled(false);
                parent.getTagAction().setEnabled(false);
                parent.getToggleEmptyElementAction().setEnabled(false);
            }
            else {
                parent.getTagAction().setEnabled(true);
                parent.getCommentAction().setEnabled(true);
                parent.getRenameElementAction().setEnabled(true);
                parent.getCDATAAction().setEnabled(true);
                parent.getHelper().setElementsEnabled(true);
                parent.getHelper().setAttributesEnabled(true);
                if (tag != null) {
                    parent.getSplitElementAction().setEnabled(false);
                    if (tag.getType() == Tag.COMMENT_TAG) {
                        parent.getCommentAction().setUncomment(true);
                    }
                    else {
                        parent.getCommentAction().setUncomment(false);
                    }
                    if (tag.getType() == Tag.EMPTY_TAG) {
                        parent.getToggleEmptyElementAction().setEnabled(true);
                    }
                    else {
                        parent.getToggleEmptyElementAction().setEnabled(false);
                    }
                    if (tag.getType() == Tag.CDATA_TAG) {
                        parent.getCDATAAction().setUnCDATA(true);
                    }
                    else {
                        parent.getCDATAAction().setUnCDATA(false);
                    }
                }
                else {
                    parent.getToggleEmptyElementAction().setEnabled(false);
                    parent.getSplitElementAction().setEnabled(true);
                    parent.getCommentAction().setUncomment(false);
                    parent.getCDATAAction().setUnCDATA(false);
                }
            }
            //			} else
            if (document.isDTD()) {
                parent.getLockAction().setEnabled(false);
                if (tag != null) {
                    if (tag.getType() == Tag.COMMENT_TAG) {
                        parent.getCommentAction().setUncomment(true);
                    }
                    else {
                        parent.getCommentAction().setUncomment(false);
                    }
                }
                else {
                    parent.getCommentAction().setUncomment(false);
                }
            }
            else if (!document.isXML()) {
                //				parent.getToggleEmptyElementAction().setEnabled( false);
                parent.getLockAction().setEnabled(false);
                //				parent.getCommentAction().setUncomment( false);
            }
        }
        else {
            // There is a selection...
            parent.getCopyAction().setEnabled(true);
            parent.getCutAction().setEnabled(true);
            parent.getToggleEmptyElementAction().setEnabled(false);
            //			if ( document.isXML()) {
            parent.getSplitElementAction().setEnabled(false);
            parent.getLockAction().setEnabled(true);
            if (!(getEditor().getLocked() == XmlEditorPane.NOT_LOCKED)) {
                parent.getStripTagsAction().setEnabled(false);
                parent.getSubstituteCharactersAction().setEnabled(false);
                parent.getSubstituteEntitiesAction().setEnabled(false);
                parent.getCommentAction().setEnabled(false);
                parent.getCDATAAction().setEnabled(false);
                parent.getHelper().setElementsEnabled(false);
                parent.getHelper().setAttributesEnabled(false);
                parent.getTagAction().setEnabled(false);
                parent.getRenameElementAction().setEnabled(false);
            }
            else {
                parent.getStripTagsAction().setEnabled(true);
                parent.getSubstituteCharactersAction().setEnabled(true);
                parent.getSubstituteEntitiesAction().setEnabled(true);
                parent.getHelper().setElementsEnabled(true);
                parent.getHelper().setAttributesEnabled(true);
                String text = getEditor().getSelectedText();
                Tag startTag = doc.getCurrentTag(selectionStart);
                Tag endTag = doc.getCurrentTag(selectionEnd);
                // does the selection contain CDATA??
                if (text.indexOf("<![CDATA[") != -1
                        || text.indexOf("]]>") != -1) {
                    parent.getCDATAAction().setEnabled(false);
                }
                else {
                    parent.getCDATAAction().setEnabled(true);
                }
                // does the selection contain a comment??
                if (text.indexOf("<!--") != -1 || text.indexOf("-->") != -1) {
                    parent.getCommentAction().setEnabled(false);
                }
                else {
                    parent.getCommentAction().setEnabled(true);
                }
                if ((startTag == null && endTag == null)
                        || (startTag != null && endTag != null && startTag
                                .equals(endTag))) {
                    tag = startTag;
                    if (tag != null) {
                        parent.getHelper().setElementsEnabled(true);
                        parent.getTagAction().setEnabled(true);
                        parent.getRenameElementAction().setEnabled(true);
                        if (tag.getType() == Tag.COMMENT_TAG
                                && parent.getCommentAction().isEnabled()) {
                            parent.getCommentAction().setUncomment(true);
                        }
                        else {
                            parent.getCommentAction().setUncomment(false);
                        }
                        if (tag.getType() == Tag.CDATA_TAG
                                && parent.getCDATAAction().isEnabled()) {
                            parent.getCDATAAction().setUnCDATA(true);
                        }
                        else {
                            parent.getCDATAAction().setUnCDATA(false);
                        }
                    }
                    else {
                        Tag parentStartTag = doc
                                .getParentStartTag(selectionStart);
                        Tag parentEndTag = doc.getParentStartTag(selectionEnd);
                        parent.getCommentAction().setUncomment(false);
                        parent.getCDATAAction().setUnCDATA(false);
                        if (parentStartTag != null
                                && parentStartTag.equals(parentEndTag)) {
                            parent.getHelper().setElementsEnabled(true);
                            parent.getTagAction().setEnabled(true);
                            parent.getRenameElementAction().setEnabled(true);
                        }
                        else if (parentStartTag == null && parentEndTag == null) {
                            parent.getHelper().setElementsEnabled(true);
                            parent.getTagAction().setEnabled(true);
                            parent.getRenameElementAction().setEnabled(true);
                        }
                        else {
                            parent.getHelper().setElementsEnabled(false);
                            parent.getTagAction().setEnabled(false);
                            parent.getRenameElementAction().setEnabled(false);
                        }
                    }
                }
                else {
                    parent.getCommentAction().setUncomment(false);
                    parent.getCDATAAction().setUnCDATA(false);
                    parent.getHelper().setElementsEnabled(false);
                    parent.getTagAction().setEnabled(false);
                    parent.getRenameElementAction().setEnabled(false);
                    parent.getHelper().setElement((Tag) null);
                }
            }
            //			} else
            if (document.isDTD()) {
                parent.getLockAction().setEnabled(false);
                String text = getEditor().getSelectedText();
                tag = doc.getCurrentTag(selectionStart);
                // does the selection contain a comment??
                if (text.indexOf("<!--") != -1 || text.indexOf("-->") != -1) {
                    parent.getCommentAction().setEnabled(false);
                }
                else {
                    parent.getCommentAction().setEnabled(true);
                }
                if (tag != null) {
                    if (tag.getType() == Tag.COMMENT_TAG
                            && parent.getCommentAction().isEnabled()) {
                        parent.getCommentAction().setUncomment(true);
                    }
                    else {
                        parent.getCommentAction().setUncomment(false);
                    }
                }
                else {
                    parent.getCommentAction().setUncomment(false);
                }
            }
            else if (!document.isXML()) {
                parent.getLockAction().setEnabled(false);
                //				parent.getCommentAction().setUncomment( false);
            }
        }
        if (parent.getHelper().isElementsEnabled()) {
            tag = getCurrentTag();
            if (tag != null) {
                if (tag.getType() == Tag.START_TAG
                        || tag.getType() == Tag.EMPTY_TAG) {
                    parent.getHelper().setElement(tag);
                }
                else {
                    Tag startTag = getParentStartTag();
                    if (startTag != null) {
                        parent.getHelper().setElement(startTag);
                    }
                    else {
                        parent.getHelper().setElement((Tag) null);
                    }
                }
            }
            else {
                Tag startTag = getParentStartTag();
                if (startTag != null) {
                    parent.getHelper().setElement(startTag);
                }
                else {
                    parent.getHelper().setElement((Tag) null);
                }
            }
        }
        updatePosition();
        if (document != null) {
            int nodePos = -1;
            int pos = doc.calculateOldPosition(getCursorPosition());
            //			XElement element = document.getLastElement(
            // doc.calculateOldPosition( getCursorPosition()));
            Object xpathNode = document.getLastNode(pos, true);
            Object node = document.getLastNode(pos, false);
            boolean end = false;
            if (node != null) {
                if (xpathNode instanceof XElement) {
                    parent.getXPathEditor().setXPath((XElement) xpathNode);
                }
                else {
                    parent.getXPathEditor().setXPath((XAttribute) xpathNode);
                }
                if (node instanceof XElement) {
                    parent.getNavigator().setSelectedElement((XElement) node);
                    if (pos >= ((XElement) node).getContentEndPosition() + 1) {
                        end = true;
                        nodePos = doc.calculateNewPosition(((XElement) node)
                                .getContentEndPosition());
                    }
                    else {
                        nodePos = doc.calculateNewPosition(((XElement) node)
                                .getElementStartPosition());
                    }
                }
                else {
                    nodePos = doc.calculateNewPosition(((XAttribute) node)
                            .getAttributeStartPosition());
                    parent.getNavigator().setSelectedElement(
                            (XElement) ((XAttribute) node).getParent());
                }
                int y = -1;
                try {
                    Rectangle r = getEditor().modelToView(nodePos);
                    if (r != null) {
                        y = r.y;
                        y = y
                                - selectedEditor.getScroller().getViewport()
                                        .getViewRect().y;
                        if (selectedEditor == bottomEditor) {
                            y = y
                                    + (topEditor.getSize().height + split
                                            .getDividerSize());
                        }
                    }
                }
                catch (BadLocationException e) {
                }
                parent.synchronise(view, (Node) node, end, y);
            }
        }
        updateMargins();
        //		bookmarkMargin.revalidate();
        //		bookmarkMargin.repaint();
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
        parent.getStatusbar().setPosition(line + 1, col + 1);
    }

    public void updateSplitPane() {

        if (split.getDividerLocation() < split.getMinimumDividerLocation()) {
            split.setOneTouchExpandable(false);
            split.setDividerSize(3);
            Object ui = split.getUI();
            if (ui instanceof BasicSplitPaneUI) {
                ((BasicSplitPaneUI) ui).getDivider().setBorder(
                        new CompoundBorder(new MatteBorder(1, 0, 0, 0,
                                UIManager.getColor("controlDkShadow")),
                                new MatteBorder(1, 0, 0, 0, Color.white)));
            }
        }
        else if (split.getDividerLocation() > split.getMaximumDividerLocation()) {
            split.setOneTouchExpandable(false);
            split.setDividerSize(3);
            split.setDividerLocation(split.getDividerLocation() + 3);
            Object ui = split.getUI();
            if (ui instanceof BasicSplitPaneUI) {
                ((BasicSplitPaneUI) ui).getDivider().setBorder(
                        new CompoundBorder(new MatteBorder(0, 0, 1, 0,
                                Color.white), new MatteBorder(0, 0, 1, 0,
                                UIManager.getColor("controlDkShadow"))));
            }
        }
        else {
            split.setOneTouchExpandable(true);
            split.setDividerSize(6);
            Object ui = split.getUI();
            if (ui instanceof BasicSplitPaneUI) {
                ((BasicSplitPaneUI) ui).getDivider().setBorder(null);
            }
        }
    }

    private static Vector getAttlistDeclarationAll() {

        if (attlistDeclarationAll == null) {
            attlistDeclarationAll = new Vector();
            attlistDeclarationAll.addElement("CDATA");
            attlistDeclarationAll.addElement("NMTOKEN");
            attlistDeclarationAll.addElement("NMTOKENS");
            attlistDeclarationAll.addElement("ID");
            attlistDeclarationAll.addElement("IDREF");
            attlistDeclarationAll.addElement("IDREFS");
            attlistDeclarationAll.addElement("ENTITY");
            attlistDeclarationAll.addElement("ENTITIES");
            attlistDeclarationAll.addElement("NOTATION");
            attlistDeclarationAll.addElement("#REQUIRED");
            attlistDeclarationAll.addElement("#IMPLIED");
            attlistDeclarationAll.addElement("#FIXED");
        }
        return attlistDeclarationAll;
    }

    private static Vector getAttlistDeclarationTypes() {

        if (attlistDeclarationTypes == null) {
            attlistDeclarationTypes = new Vector();
            attlistDeclarationTypes.addElement("CDATA");
            attlistDeclarationTypes.addElement("NMTOKEN");
            attlistDeclarationTypes.addElement("NMTOKENS");
            attlistDeclarationTypes.addElement("ID");
            attlistDeclarationTypes.addElement("IDREF");
            attlistDeclarationTypes.addElement("IDREFS");
            attlistDeclarationTypes.addElement("ENTITY");
            attlistDeclarationTypes.addElement("ENTITIES");
            attlistDeclarationTypes.addElement("NOTATION");
        }
        return attlistDeclarationTypes;
    }

    private static Vector getAttlistDeclarationDefaults() {

        if (attlistDeclarationDefaults == null) {
            attlistDeclarationDefaults = new Vector();
            attlistDeclarationDefaults.addElement("#REQUIRED");
            attlistDeclarationDefaults.addElement("#IMPLIED");
            attlistDeclarationDefaults.addElement("#FIXED");
        }
        return attlistDeclarationDefaults;
    }

    private static Vector getDeclarationNames() {

        if (declarationNames == null) {
            declarationNames = new Vector();
            //			declarationNames.addElement( "!DOCTYPE");
            declarationNames.addElement("!ELEMENT");
            declarationNames.addElement("!ATTLIST");
            declarationNames.addElement("!ENTITY");
            declarationNames.addElement("!NOTATION");
            declarationNames.addElement("!--");
            declarationNames.addElement("![CDATA[");
        }
        return declarationNames;
    }

    private static Vector getElementDeclarationTypes() {

        if (elementDeclarationTypes == null) {
            elementDeclarationTypes = new Vector();
            elementDeclarationTypes.addElement("(#PCDATA)");
            elementDeclarationTypes.addElement("EMPTY");
            elementDeclarationTypes.addElement("ANY");
        }
        return elementDeclarationTypes;
    }

    private static Vector getEntityDeclarationTypes() {

        if (entityDeclarationTypes == null) {
            entityDeclarationTypes = new Vector();
            entityDeclarationTypes.addElement("SYSTEM");
            entityDeclarationTypes.addElement("PUBLIC");
            entityDeclarationTypes.addElement("NDATA");
        }
        return entityDeclarationTypes;
    }

    private static Vector getDoctypeDeclarationTypes() {

        if (doctypeDeclarationTypes == null) {
            doctypeDeclarationTypes = new Vector();
            doctypeDeclarationTypes.addElement("SYSTEM");
            doctypeDeclarationTypes.addElement("PUBLIC");
        }
        return doctypeDeclarationTypes;
    }

    private static Vector getNotationDeclarationTypes() {

        if (notationDeclarationTypes == null) {
            notationDeclarationTypes = new Vector();
            notationDeclarationTypes.addElement("SYSTEM");
            notationDeclarationTypes.addElement("PUBLIC");
        }
        return notationDeclarationTypes;
    }

    //	public int getCursorLine() {
    //		Element root = getEditor().getDocument().getDefaultRootElement();
    //		return root.getElementIndex( getCursorPosition());
    //	}
    public void toggleBookmarkCurrentLine() {

        Element root = getEditor().getDocument().getDefaultRootElement();
        toggleBookmark(root.getElementIndex(getCursorPosition()));
    }

    public void toggleBookmark(int line) {

        Bookmark bm = getBookmark(line);
        if (bm != null) {
            bookmarks.removeElement(bm);
            parent.removeBookmark(bm);
        }
        else {
            Element root = getEditor().getDocument().getDefaultRootElement();
            Element element = root.getElement(line);
            bm = new Bookmark(element, document);
            parent.addBookmark(bm);
            bookmarks.addElement(bm);
        }
        updateMargins();
    }

    int previousMarginLines = 0;

    private void updateMargins() {

        if (getLines() != previousMarginLines) {
            if (topEditor.isInitialised()) {
                topEditor.updateMargins();
            }
            bottomEditor.updateMargins();
            if (selectedEditor == topEditor) {
                bottomEditor.getEditor().revalidate();
                bottomEditor.getEditor().repaint();
            }
            else if (topEditor.isInitialised()) {
                topEditor.getEditor().revalidate();
                topEditor.getEditor().repaint();
            }
        }
    }

    public Bookmark getBookmark(int line) {

        resetBookmarks();
        for (int i = 0; i < bookmarks.size(); i++) {
            Bookmark b = (Bookmark) bookmarks.elementAt(i);
            if (b.getLineNumber() == line) {
                return b;
            }
        }
        return null;
    }

    public boolean hasBookmark(Bookmark bm) {

        for (int i = 0; i < bookmarks.size(); i++) {
            Bookmark b = (Bookmark) bookmarks.elementAt(i);
            if (b.equals(bm)) {
                return true;
            }
        }
        return false;
    }

    public Vector getBookmarks() {

        resetBookmarks();
        return bookmarks;
    }

    public ErrorList getErrors() {

        return errors;
    }
    
    public void setErrors(ErrorList errorList) {

        this.errors = errorList;
    }
    
    public void addToErrors(ErrorList errorList) {

    	if(this.getErrors() != null) {
    		Vector tempErrors = errorList.getErrors();
    		if(tempErrors != null) {
    			for(int cnt=0;cnt<tempErrors.size();++cnt) {
    				this.errors.addError((XMLError) tempErrors.get(cnt));
    			}
    		}
    	}
    	else {
    		this.errors = errorList;
    	}
    }

    public XMLError getError(int line) {

        Vector es = errors.getCurrentErrors();
        for (int i = 0; i < es.size(); i++) {
            XMLError error = (XMLError) es.elementAt(i);
            if (error.getLineNumber() == (line + 1)) {
                return error;
            }
        }
        return null;
    }

    public boolean isBookmark(int i) {

        return getBookmark(i) != null;
    }

    // Implementation of CaretListener
    /**
     * Messaged when the caret position has changed.
     */
    public void caretUpdate(CaretEvent e) {

        if ((XmlEditorPane) e.getSource() == getEditor()) {
            hideSelectionPopup();
            caretUpdated = true;
            examiner.reset();
            tagTree = new Vector();
            currentTag = null;
            parentStartTag = null;
            updateMargins();
            updatePositionalStuff();
        }
    }

    public boolean isKeyReleased() {

        return keyReleased;
    }

    private void updatePositionalStuff() {

        if (getEditor().hasFocus()) {
            if (caretUpdated && keyReleased) {
                updateHelper();
                caretUpdated = false;
            }
            else {
                updatePosition();
            }
        }
    }

    // Extension of BasicTextUI.BasicCaret
    //	private class XmlCaret extends BasicTextUI.BasicCaret {
    public class XmlCaret extends DefaultCaret implements UIResource {

        boolean focus = false;

        public XmlCaret() {

            setBlinkRate(500);
        }

        protected void adjustVisibility(Rectangle nloc) {

            JTextComponent component = getComponent();
            if (component.hasFocus()) {
                super.adjustVisibility(nloc);
            }
        }

        // Always leave the selection highlighted
        public void setSelectionVisible(boolean whatever) {

            super.setSelectionVisible(true);
        }

        // Always leave the caret visible
        public boolean isVisible() {

            boolean result = true;
            if (focus) {
                result = super.isVisible();
            }
            return result;
        }

        public void focusGained(FocusEvent e) {

            focus = true;
            super.focusGained(e);
        }

        public void focusLost(FocusEvent e) {

            focus = false;
        }
    }

    protected void removeAllListeners() {

        // Guaranteed to return a non-null array
        Object[] list = listenerList.getListenerList();
        for (int i = list.length - 2; i >= 0; i -= 2) {
            listenerList.remove((Class) list[i], (EventListener) list[i + 1]);
        }
    }

    public void cleanup() {

        removeAllListeners();
        removeAll();
        if (topEditor != null) {
            topEditor.cleanup();
        }
        if (bottomEditor != null) {
            bottomEditor.cleanup();
        }
        popup.removeAll();
        finalize();
    }

    protected void finalize() {

        selectedEditor = null;
        popupListener = null;
        pattern = null;
        properties = null;
        parent = null;
        popup = null;
        selectionPopup = null;
        // document is cleanedup somewhere else
        document = null;
        topEditor = null;
        bottomEditor = null;
        selectedEditor = null;
        elementNames = null;
        attributeNames = null;
        entityNames = null;
    }

    class WrapperAction extends AbstractAction {

        Action action = null;

        public WrapperAction(Action action, String name) {

            super(name);
            putValue(SMALL_ICON, action.getValue(SMALL_ICON));
            putValue(ACCELERATOR_KEY, action.getValue(ACCELERATOR_KEY));
            putValue(SHORT_DESCRIPTION, action.getValue(SHORT_DESCRIPTION));
            this.action = action;
        }

        public void actionPerformed(ActionEvent e) {

            action.actionPerformed(e);
        }
    }

    class FragmentAction extends AbstractAction {

        private FragmentProperties fragment = null;

        public FragmentAction(FragmentProperties fragment) {

            super(fragment.getName());
            this.fragment = fragment;
            setIcon();
            String keySequence = fragment.getKey();
            if (keySequence != null) {
                int index = keySequence.lastIndexOf("+");
                String mask = null;
                String value = null;
                if (index != -1) {
                    mask = keySequence.substring(0, index);
                    value = keySequence.substring(index + 1, keySequence
                            .length());
                }
                else {
                    value = keySequence;
                }
                putValue(ACCELERATOR_KEY, props.getKeyPreferences()
                        .getKeyStroke(new Keystroke(mask, value)));
            }
            putValue(SHORT_DESCRIPTION, fragment.getName() + " (" + keySequence
                    + ")");
        }

        private void setIcon() {

            ImageIcon icon = null;
            try {
                icon = XngrImageLoader.get().getImage(new URL(fragment.getIcon()));
                if (icon.getIconHeight() != 16 || icon.getIconWidth() != 16) {
                    icon = new ImageIcon(icon.getImage().getScaledInstance(16,
                            16, Image.SCALE_SMOOTH));
                }
            }
            catch (Exception e) {
                icon = null;
            }
            if (icon == null) {
                icon = XngrImageLoader
                        .get()
                        .getImage(
                                "com/cladonia/xngreditor/icons/DefaultFragmentIcon.gif");
            }
            putValue(SMALL_ICON, icon);
        }

        public void actionPerformed(ActionEvent e) {

            insertFragment(fragment.isBlock(), fragment.getContent());
            setFocus();
        }
    }

    public synchronized Tag getCurrentTag() {

        if (currentTag == null) {
            currentTag = ((XmlDocument) getEditor().getDocument())
                    .getCurrentTag(getCursorPosition());
        }
        return currentTag;
    }

    public synchronized Tag getParentStartTag() {

        if (parentStartTag == null) {
            parentStartTag = ((XmlDocument) getEditor().getDocument())
                    .getParentStartTag(getCursorPosition());
        }
        return parentStartTag;
    }

    public synchronized Tag getParentStartTag(Tag child) {

        if (child != null) {
            if (tagTree != null) {
                int index = tagTree.indexOf(child);
                if (index != -1) {
                    if (tagTree.size() > index + 1) {
                        return (Tag) tagTree.elementAt(index + 1);
                    }
                    else {
                        Tag tag = ((XmlDocument) getEditor().getDocument())
                                .getParentStartTag(child.getStart());
                        tagTree.addElement(tag);
                        return tag;
                    }
                }
            }
            return ((XmlDocument) getEditor().getDocument())
                    .getParentStartTag(child.getStart());
        }
        else {
            return null;
        }
    }

    private synchronized Tag getCurrentStartTag() {

        if (tagTree.size() == 0) {
            Tag tag = selectedEditor.getCurrentStartTag();
            if (tag == null) {
                return null;
            }
            tagTree.addElement(tag);
        }
        return (Tag) tagTree.elementAt(0);
    }

    private class Examiner extends Thread {

        boolean reset = false;
        private Vector elementListeners = null;
        private Vector currentElementsListeners = null;
        private Vector currentSiblingsListeners = null;
        private Vector currentElementListeners = null;
        private Vector currentAttributesListeners = null;
        private Vector attributeListeners = null;
        private Vector valueListeners = null;
        private ElementInformation current = null;
        private Vector currentSiblings = null;
        private Vector currentElements = null;
        private Vector currentAttributes = null;
        private Vector elements = null;
        private Vector attributes = null;
        private Vector values = null;

        public synchronized void reset() {

            currentElements = null;
            currentSiblings = null;
            currentAttributes = null;
            elements = null;
            attributes = null;
            values = null;
            current = null;
            reset = true;
        }

        public synchronized void getCurrentSiblings(
                CurrentSiblingsListener listener) {

            if (currentSiblings != null) {
                listener.setCurrentSiblings(currentSiblings);
            }
            else {
                if (currentSiblingsListeners != null
                        && !currentSiblingsListeners.contains(listener)) {
                    currentSiblingsListeners.addElement(listener);
                }
                else {
                    currentSiblingsListeners = new Vector();
                    currentSiblingsListeners.addElement(listener);
                }
            }
        }

        public synchronized void getCurrentElements(
                CurrentElementsListener listener) {

            if (currentElements != null) {
                listener.setCurrentElements(currentElements);
            }
            else {
                if (currentElementsListeners != null
                        && !currentElementsListeners.contains(listener)) {
                    currentElementsListeners.addElement(listener);
                }
                else {
                    currentElementsListeners = new Vector();
                    currentElementsListeners.addElement(listener);
                }
            }
        }

        public synchronized void getCurrentAttributes(
                CurrentAttributesListener listener) {

            if (currentAttributes != null) {
                listener.setCurrentAttributes(currentAttributes);
            }
            else {
                if (currentAttributesListeners != null
                        && !currentAttributesListeners.contains(listener)) {
                    currentAttributesListeners.addElement(listener);
                }
                else {
                    currentAttributesListeners = new Vector();
                    currentAttributesListeners.addElement(listener);
                }
            }
        }

        public synchronized void getCurrentElement(
                CurrentElementListener listener) {

            if (current != null) {
                listener.setCurrentElement(current);
            }
            else {
                if (currentElementListeners != null
                        && !currentElementListeners.contains(listener)) {
                    currentElementListeners.addElement(listener);
                }
                else {
                    currentElementListeners = new Vector();
                    currentElementListeners.addElement(listener);
                }
            }
        }

        public synchronized void getElements(ElementsListener listener) {

            if (elements != null) {
                listener.setElements(elements);
            }
            else {
                if (elementListeners != null
                        && !elementListeners.contains(listener)) {
                    elementListeners.addElement(listener);
                }
                else {
                    elementListeners = new Vector();
                    elementListeners.addElement(listener);
                }
            }
        }

        public synchronized void getAttributes(AttributesListener listener) {

            if (attributes != null) {
                listener.setAttributes(attributes);
            }
            else {
                if (attributeListeners != null
                        && !attributeListeners.contains(listener)) {
                    attributeListeners.addElement(listener);
                }
                else {
                    attributeListeners = new Vector();
                    attributeListeners.addElement(listener);
                }
            }
        }

        public synchronized void getValues(ValuesListener listener) {

            if (values != null) {
                listener.setValues(values);
            }
            else {
                if (valueListeners != null
                        && !valueListeners.contains(listener)) {
                    valueListeners.addElement(listener);
                }
                else {
                    valueListeners = new Vector();
                    valueListeners.addElement(listener);
                }
            }
        }

        public boolean isReset() {

            return reset;
        }

        // always running
        public void run() {

            while (true) {
                try {
                    reset = false;
                    if (valueListeners != null && valueListeners.size() > 0) {
                        values = getCurrentAttributeValues();
                        if (!reset) {
                            for (int i = 0; i < valueListeners.size(); i++) {
                                ((ValuesListener) valueListeners.elementAt(i))
                                        .setValues(values);
                            }
                            valueListeners.removeAllElements();
                        }
                    }
                    else if (attributeListeners != null
                            && attributeListeners.size() > 0) {
                        attributes = Editor.this.getAttributes();
                        if (!reset) {
                            for (int i = 0; i < attributeListeners.size(); i++) {
                                ((AttributesListener) attributeListeners
                                        .elementAt(i))
                                        .setAttributes(attributes);
                            }
                            attributeListeners.removeAllElements();
                        }
                    }
                    else if (elementListeners != null
                            && elementListeners.size() > 0) {
                        elements = getChildElements();
                        if (!reset) {
                            for (int i = 0; i < elementListeners.size(); i++) {
                                ((ElementsListener) elementListeners
                                        .elementAt(i)).setElements(elements);
                            }
                            elementListeners.removeAllElements();
                        }
                    }
                    else if (currentElementsListeners != null
                            && currentElementsListeners.size() > 0) {
                        currentElements = getCurrentChildElements();
                        if (!reset) {
                            for (int i = 0; i < currentElementsListeners.size(); i++) {
                                ((CurrentElementsListener) currentElementsListeners
                                        .elementAt(i))
                                        .setCurrentElements(currentElements);
                            }
                            currentElementsListeners.removeAllElements();
                        }
                    }
                    else if (currentSiblingsListeners != null
                            && currentSiblingsListeners.size() > 0) {
                        currentSiblings = Editor.this.getCurrentSiblings();
                        if (!reset) {
                            for (int i = 0; i < currentSiblingsListeners.size(); i++) {
                                ((CurrentSiblingsListener) currentSiblingsListeners
                                        .elementAt(i))
                                        .setCurrentSiblings(currentSiblings);
                            }
                            currentSiblingsListeners.removeAllElements();
                        }
                    }
                    else if (currentElementListeners != null
                            && currentElementListeners.size() > 0) {
                        current = Editor.this.getCurrentElement();
                        if (!reset) {
                            for (int i = 0; i < currentElementListeners.size(); i++) {
                                ((CurrentElementListener) currentElementListeners
                                        .elementAt(i))
                                        .setCurrentElement(current);
                            }
                            currentElementListeners.removeAllElements();
                        }
                    }
                    else if (currentAttributesListeners != null
                            && currentAttributesListeners.size() > 0) {
                        currentAttributes = Editor.this.getCurrentAttributes();
                        if (!reset) {
                            for (int i = 0; i < currentAttributesListeners
                                    .size(); i++) {
                                ((CurrentAttributesListener) currentAttributesListeners
                                        .elementAt(i))
                                        .setCurrentAttributes(currentAttributes);
                            }
                            currentAttributesListeners.removeAllElements();
                        }
                    }
                    else {
                        sleep(10);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static interface ElementsListener {

        public void setElements(Vector elements);
    }

    public static interface AttributesListener {

        public void setAttributes(Vector attributes);
    }

    public static interface ValuesListener {

        public void setValues(Vector values);
    }

    public static interface CurrentElementListener {

        public void setCurrentElement(ElementInformation element);
    }

    public static interface CurrentSiblingsListener {

        public void setCurrentSiblings(Vector siblings);
    }

    public static interface CurrentElementsListener {

        public void setCurrentElements(Vector elements);
    }

    public static interface CurrentAttributesListener {

        public void setCurrentAttributes(Vector attributes);
    }

    private class ValuesPopupListener implements ValuesListener {

        String prefix = null;

        public void setPrefix(String prefix) {

            this.prefix = prefix;
        }

        public void setValues(Vector values) {

            showAttributeValueSelectionPopup(prefix, values);
        }
    }

    private class AttributesPopupListener implements AttributesListener {

        String prefix = null;
        Tag tag = null;

        public void setTag(Tag tag) {

            this.tag = tag;
        }

        public void setPrefix(String prefix) {

            this.prefix = prefix;
        }

        public void setAttributes(Vector values) {

            showAttributeSelectionPopup(prefix, values, tag);
        }
    }

    private class ElementsPopupListener implements ElementsListener {

        String prefix = null;
        String parent = null;

        public void setParent(String parent) {

            this.parent = parent;
        }

        public void setPrefix(String prefix) {

            this.prefix = prefix;
        }

        public void setElements(Vector values) {

            showElementSelectionPopup(prefix, values, parent);
        }
    }
    //	private class XMLDropTarget extends DropTarget implements UIResource {
    //		private DropTarget parent = null;
    //		
    //		public XMLDropTarget( DropTarget parent) {
    //			this.parent = parent;
    //		}
    //		
    //		public void dragEnter( DropTargetDragEvent e) {
    //			System.out.println( "XMLDropTarget.dragEnter( "+e+")");
    //			parent.dragEnter( e);
    //		}
    //
    //		public void dragExit( DropTargetEvent e) {
    //			System.out.println( "XMLDropTarget.dragExit( "+e+")");
    //			parent.dragExit( e);
    //		}
    //
    //		public void dragOver( DropTargetDragEvent e) {
    //			System.out.println( "XMLDropTarget.dragOver( "+e+")");
    //			parent.dragOver( e);
    //		}
    //
    //		public void drop( DropTargetDropEvent e) {
    //			System.out.println( "XMLDropTarget.drop( "+e+")");
    //			parent.drop( e);
    //		}
    //
    //		public void dropActionChanged( DropTargetDragEvent e) {
    //			System.out.println( "XMLDropTarget.dropActionChanged( "+e+")");
    //			parent.dropActionChanged( e);
    //		}
    //	}
}