/*
 * $Id: MessagePane.java,v 1.4 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;

import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.XmlDocument;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.properties.Keystroke;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows parsing error information.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
 public class MessagePane extends JPanel {
	private LogOutputStream stream = null;
	private JTextArea textArea = null;
	
	private AbstractAction tabAction;

	private AbstractAction unindentAction;

	private AbstractAction selectAll;

	private ConfigurationProperties props;

	private String downKey;

	private String upKey;

	private String enterKey;

	private String escapeKey;
 	
 	public MessagePane(ConfigurationProperties props) {
 		super( new BorderLayout());

 		this.props = props;
 		textArea = new JTextArea();
		textArea.setFont( TextPreferences.getBaseFont().deriveFont( (float)12));

		
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

            	textArea.selectAll();
                /*if (parent != null) {
                    if (parent.getView() != null) {
                        parent.getView().getEditor().getEditor().selectAll();
                        parent.getView().getEditor().setFocus();
                    }
                }*/
            }
        };
        
        textArea.getActionMap().put("xngr-indent", tabAction);
        textArea.getActionMap().put("xngr-unindent", unindentAction);
        
		JScrollPane scroller = new JScrollPane(	textArea,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		scroller.setPreferredSize( new Dimension( 100, 100));
		
		this.add( scroller, BorderLayout.CENTER);

		stream = new LogOutputStream();
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
	
	 	
 	public OutputStream getOutputStream() {
 		return stream;
 	}
	
 	public void reset() {
 		replace("");
 	}

 	public void updatePreferences() {
 		setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
 		String activeConfig = props.getKeyPreferences().getActiveConfiguration();
		setKeyMappings(activeConfig);
 	}

	private void replace( final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			textArea.setText( text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					textArea.setText( text);
				}
			});
		}
	}

	private void appendln( final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			textArea.append( "\n"+text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					textArea.append( "\n"+text);
				}
			});
		}
	}

	private void appendText( final String text) {
		if ( SwingUtilities.isEventDispatchThread()) {
			textArea.append( text);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					textArea.append( text);
				}
			});
		}
	}
	
	class LogOutputStream extends OutputStream {
		private StringBuffer buffer = new StringBuffer();
		
		public LogOutputStream() {
		}

		public void write(int b) {
			buffer.append( (char)b);
		}
 	
		public void flush () {
			appendText( buffer.toString());
			buffer = new StringBuffer();
		}
		
	}
	
	public JTextArea getEditor() {
		return(textArea);
	}
	
	/*public void search( String search, boolean regExp, boolean matchCase, boolean matchWord, boolean down, boolean wrap) {
		search( null, search, regExp, matchCase, matchWord, down, wrap);
	}
	
	private Pattern pattern = null;
	
	public Matcher search( Vector ranges, String search, boolean regExp, boolean matchCase, boolean matchword, boolean down, boolean wrap) {
		String regularSearch = search;
		
		if ( ranges == null) {
			ranges = new Vector();
			ranges.addElement( new Range( 0, getEditor().getDocument().getLength()));
		}
		
		if ( !regExp) {
			regularSearch = "\\Q"+StringUtilities.prepareNonRegularExpression( regularSearch)+"\\E";
		}
		
		if ( matchword) { 
			regularSearch = "\\b"+regularSearch+"\\b";
		}

		if ( !matchCase) {
			pattern = Pattern.compile( regularSearch, Pattern.CASE_INSENSITIVE);
		} else {
			pattern = Pattern.compile( regularSearch);
		}

		try {
			int caret = Math.max( getEditor().getCaretPosition(), Math.max( getEditor().getSelectionStart(), getEditor().getSelectionEnd()));
			Matcher matcher = pattern.matcher( getEditor().getText( 0, getEditor().getDocument().getLength()));
			
			int start = -1;
			int end = -1;

			if ( down) {
				boolean match = matcher.find( caret);
				
				while ( match && !inRanges( ranges, matcher)) {
					match = matcher.find();
				}
				
				// could not find a match, start from the start...
//				if ( wrap && (!match || !inRanges( ranges, matcher))) {
				if ( wrap && !match) {
					match = matcher.find( 0);
				}
				
				// continue to search
				while ( match && !inRanges( ranges, matcher)) {
					match = matcher.find();
				}

				if ( match) {
					start = matcher.start();
					end = matcher.end();
				}
			} else {
				caret = Math.min( getEditor().getCaretPosition(), Math.min( getEditor().getSelectionStart(), getEditor().getSelectionEnd()));

				while ( matcher.find()) {
					if ( matcher.start() < caret) {
						if ( inRanges( ranges, matcher)) {
							start = matcher.start();
							end = matcher.end();
						}
					} else {
						break;
					}
				}

				if ( start == -1 && wrap) {
					boolean match = matcher.find( caret);
					// get last matching element.
					while ( match) {
						if ( inRanges( ranges, matcher)) {
							start = matcher.start();
							end = matcher.end();
						}

						match = matcher.find();
					}
				}
			}

			if ( end != -1) {
				matcher.find( start);
				//getEditor().select( start, end);
				getEditor().selectAll();
				repaint();
				return matcher;
			} else {
				repaint();
			}
		} catch( Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}*/
	
	private boolean inRanges( Vector ranges, Matcher matcher) {
		/*for ( int i = 0; i < ranges.size(); i++) {
			Range range = (Range)ranges.elementAt(i);
			if ( range == null) {
				
			}
			if ( range.contains( ((Document)getEditor().getDocument()).calculateOldPosition( matcher.start())) && range.contains( ((XmlDocument)getEditor().getDocument()).calculateOldPosition( matcher.end()))) {
				return true;
			}
		}
		return false;*/
		return(true);
	}
	
	
}
