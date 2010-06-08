/*
 * $Id: ImportFromTextDialog.java,v 1.14 2004/10/28 07:55:20 tcurley Exp $ 
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */

package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Dimension;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import javax.swing.table.DefaultTableModel;

import javax.swing.table.TableColumn;
import org.bounce.FormLayout;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import com.cladonia.xml.editor.Constants;
import com.cladonia.xml.editor.XmlEditorPane;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.TextPreferences;
import javax.swing.ToolTipManager;
import com.cladonia.xngreditor.ImportUtilities;

/**
 * Used to import from text file(csv etc) to the editor
 * 
 * @version $Revision: 1.14 $, $Date: 2004/10/28 07:55:20 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ImportFromTextDialog extends XngrDialog {
    
    public JTextField docField;
    private String encoding;
    private JComboBox expressionsCombo = null;
    public File file;
    private JFrame parent;
    public JTextField rowField;
    public JTable table;
    private DefaultTableModel tableModel;
    boolean usingFirstRow = true;
    private XmlEditorPane xmlEditor = null;
    public JCheckBox checkConvertChars;
    private JCheckBox checkFirstRow;
    
    public ImportFromTextDialog(ExchangerEditor parent) {
        
        super(parent, true);
        super.setTitle("Import...", "Import From a Text File",
        "Choose the various options to import with");
        this.parent = parent;
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel centre = new JPanel();
        centre.setLayout(new GridLayout(2, 1));
        JPanel top = new JPanel();
        tableModel = new DefaultTableModel();
        table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setForeground(Color.WHITE);
        table.setBackground(Color.WHITE);
        JScrollPane tableScroll = new JScrollPane(table);
        //      get notified of column clicks
        table.getTableHeader().addMouseListener(new MouseAdapter() {
            
            public void mouseClicked(MouseEvent event) {
                
                int selectedIdx = table.getColumnModel().getColumnIndexAtX(
                        event.getX());
                //processClick( selectedIdx );
                try {
                    ImportUtilities.processHeaderClick(selectedIdx, table);
                    ImportUtilities.updateOrder(xmlEditor, table, docField
                            .getText(), rowField.getText(),checkConvertChars.isSelected());
                }
                catch (Exception e) {
                    MessageHandler.showError(ImportFromTextDialog.this.parent, "Error reading file",
                    "Import from text error");
                    cancelButtonPressed();
                    //e1.printStackTrace();
                }
            }
        });
        tableScroll.getViewport().setBackground(table.getBackground());
        ToolTipManager.sharedInstance().unregisterComponent(table);
        ToolTipManager.sharedInstance().unregisterComponent(
                table.getTableHeader());
        tableScroll.setBackground(Color.WHITE);
        checkFirstRow = new JCheckBox("First row contains field names");
        checkFirstRow.setSelected(true);
        usingFirstRow = true;
        checkFirstRow.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent e) {
                
                if (checkFirstRow.isSelected() == false) {
                    try {
                        //add new row to table in 1 and for each column
                        //prompt user for column name
                        //this.tableModel.insertRow(1,null);
                        ImportUtilities.addNewRow(0, table, tableModel);
                        ImportUtilities.updateOrder(xmlEditor,table,docField.getText(),rowField.getText(),checkConvertChars.isSelected());
                    }
                    catch (Exception e1) {
                        MessageHandler.showError(ImportFromTextDialog.this.parent, "Error reading file",
                        "Import from text error");
                        cancelButtonPressed();
                        //e1.printStackTrace();
                    }
                    usingFirstRow = false;
                }
                if (checkFirstRow.isSelected() == true) {
                    if (usingFirstRow == false) {
                        //need to remove first row that
                        //was already inserted
                        tableModel.removeRow(0);
                        try{
                            ImportUtilities.updateOrder(xmlEditor,table,docField.getText(),rowField.getText(),checkConvertChars.isSelected());
                        }
                        catch (Exception e1) {
                            MessageHandler.showError(ImportFromTextDialog.this.parent, "Error reading file",
                            "Import from text error");
                            cancelButtonPressed();
                            //e1.printStackTrace();
                        }
                    }
                    usingFirstRow = true;
                }
            }
        });
        
        checkConvertChars = new JCheckBox("Convert Characters To Entities");
        checkConvertChars.setSelected(true);
        checkConvertChars.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent action) {
                
                try {
                    setTableValues(expressionsCombo.getSelectedIndex());
                    
                }
                catch (Exception e) {
                    MessageHandler.showError(
                            ImportFromTextDialog.this.parent,
                            "Error reading file",
                    "Import from database table error");
                    cancelButtonPressed();
                }
            }
        });
        JLabel fieldDelimiter = new JLabel("Field Delimiter");
        expressionsCombo = new JComboBox(ImportUtilities.splitExpressionsText);
        expressionsCombo.setSelectedIndex(0);
        expressionsCombo.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent action) {
                
                try {
                    setTableValues(expressionsCombo.getSelectedIndex());
                    checkConvertChars.setSelected(true);
                }
                catch (UnsupportedEncodingException e) {
                    MessageHandler
                    .showError(ImportFromTextDialog.this.parent,"Error Reading File: \nUnsupported encoding", "File Error");
                    cancelButtonPressed();
                }
                catch (FileNotFoundException e) {
                    MessageHandler
                    .showError(ImportFromTextDialog.this.parent,"Error Reading File: \nCannot find the specified file", "File Error");
                    cancelButtonPressed();
                }
                catch (IOException e) {
                    MessageHandler
                    .showError(ImportFromTextDialog.this.parent,"Error Reading File: \nSystem error reading file from disk", "File Error");
                    cancelButtonPressed();
                }
                catch (Exception e) {
                    MessageHandler
                    .showError(ImportFromTextDialog.this.parent,"Error Reading File", "File Error");
                    cancelButtonPressed();
                }
            }
        });
        //top.add(checkFirstRow);
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayout(1, 1));
        tablePanel.add(tableScroll);
        TitledBorder tableBorder;
        tableBorder = BorderFactory.createTitledBorder("Select Field Types");
        tablePanel.setBorder(new CompoundBorder(tableBorder, (new EmptyBorder(
                5, 5, 5, 5))));
        JLabel docLabel = new JLabel("Document Element: ");
        JLabel rowLabel = new JLabel("Row Element: ");
        docField = new JTextField("document");
        docField.addFocusListener(new FocusListener() {
            
            public void focusGained(FocusEvent e) {
                
            }
            
            public void focusLost(FocusEvent e) {
                
                try {
                    ImportUtilities.updateOrder(xmlEditor, table, docField.getText(), rowField.getText(),checkConvertChars.isSelected());
                }
                catch (Exception e1) {
                    MessageHandler.showError(ImportFromTextDialog.this.parent, "Error reading file",
                    "Import from text error");
                    cancelButtonPressed();
                }
            }
        });
        docField.addAncestorListener(new AncestorListener() {
            
            public void ancestorAdded(AncestorEvent e) {
                
                docField.requestFocusInWindow();
            }
            
            public void ancestorMoved(AncestorEvent e) {
                
            }
            
            public void ancestorRemoved(AncestorEvent e) {
                
            }
        });
        rowField = new JTextField("row");
        rowField.addFocusListener(new FocusListener() {
            
            public void focusGained(FocusEvent e) {
                
            }
            
            public void focusLost(FocusEvent e) {
                
                try {
                    ImportUtilities.updateOrder(xmlEditor, table, docField
                            .getText(), rowField.getText(),checkConvertChars.isSelected());
                }
                catch (Exception e1) {
                    MessageHandler.showError(ImportFromTextDialog.this.parent, "Error reading file",
                    "Import from text error");
                    cancelButtonPressed();
                }
            }
        });
        JPanel panel = new JPanel(new BorderLayout());
        xmlEditor = new XmlEditorPane(false);
        xmlEditor.setEditable(false);
        xmlEditor.setCaretPosition(0);
        JScrollPane scroller = new JScrollPane(xmlEditor);
        JPanel editPanel = new JPanel(new BorderLayout());
        editPanel.add(scroller, BorderLayout.CENTER);
        TitledBorder editorBorder;
        editorBorder = BorderFactory.createTitledBorder("Preview");
        editPanel.setBorder(new CompoundBorder(editorBorder, (new EmptyBorder(
                5, 5, 5, 5))));
        try {
            ImportUtilities.updateOrder(xmlEditor, table, docField.getText(),
                    rowField.getText(),checkConvertChars.isSelected());
        }
        catch (Exception e1) {
            MessageHandler.showError(ImportFromTextDialog.this.parent, "Error reading file",
            "Import from text error");
            cancelButtonPressed();
            //e1.printStackTrace();
        }
        centre.add(tablePanel);
        centre.add(editPanel);
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FormLayout(2, 2));
        optionsPanel.add(docLabel, FormLayout.LEFT);
        optionsPanel.add(docField, FormLayout.RIGHT_FILL);
        optionsPanel.add(rowLabel, FormLayout.LEFT);
        optionsPanel.add(rowField, FormLayout.RIGHT_FILL);
        optionsPanel.add(fieldDelimiter, FormLayout.LEFT);
        optionsPanel.add(expressionsCombo, FormLayout.RIGHT_FILL);
        optionsPanel.add(checkFirstRow, FormLayout.FULL_FILL);
        optionsPanel.add(checkConvertChars, FormLayout.FULL_FILL);
        top.add(optionsPanel);
        TitledBorder optionsTitle;
        optionsTitle = BorderFactory.createTitledBorder("Options");
        optionsPanel.setBorder(new CompoundBorder(optionsTitle,
                (new EmptyBorder(5, 5, 5, 5))));
        //optionsPanel.setBorder(optionsTitle);
        main.add(optionsPanel, BorderLayout.NORTH);
        main.add(centre, BorderLayout.CENTER);
        main.setPreferredSize(new Dimension(ImportUtilities.mainWidth,
                ImportUtilities.mainHeight));
        //getRootPane().setDefaultButton(okButton);
        pack();
        setContentPane(main);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        updatePreferences();
    }
    
    protected void okButtonPressed() {
        
        //check that the document and row elements are not empty
        if (this.docField.getText().length() == 0) {
            MessageHandler.showError(parent,
                    "Please enter the document element text",
            "Import From Text Error");
        }
        else if (this.rowField.getText().length() == 0) {
            MessageHandler.showError(parent,
                    "Please enter the row element text",
            "Import From Text Error");
        }
        else {
            super.okButtonPressed();
        }
    }
    
    private void setAttributes(int id, String property) {
        
        FontType type = TextPreferences.getFontType(property);
        xmlEditor.setAttributes(id, type.getColor(), type.getStyle());
    }
    
    public void show(URL url, String encoding) {
        
        file = new File(url.getFile());
        //fileName = url.getFile();
        this.encoding = encoding;
        
        try {
            checkConvertChars.setSelected(true);
            setTableValues(-1);
            pack();
            setLocationRelativeTo(parent);
            show();
        }
        catch (UnsupportedEncodingException e) {
            MessageHandler
            .showError(parent,"Error Reading File: \nUnsupported encoding", "File Error");
            cancelButtonPressed();
        }
        catch (FileNotFoundException e) {
            MessageHandler
            .showError(parent,"Error Reading File: \nCannot find the specified file", "File Error");
            cancelButtonPressed();
        }
        catch (IOException e) {
            MessageHandler
            .showError(parent,"Error Reading File: \nSystem error reading file from disk", "File Error");
            cancelButtonPressed();
        }
        catch (Exception e) {
            MessageHandler
            .showError(parent,"Error Reading File", "File Error");
            cancelButtonPressed();
        }
        
    }
    
    private void updatePreferences() {
        
        xmlEditor.setFont(TextPreferences.getBaseFont());
        setAttributes(Constants.ELEMENT_NAME, TextPreferences.ELEMENT_NAME);
        setAttributes(Constants.ELEMENT_VALUE, TextPreferences.ELEMENT_VALUE);
        setAttributes(Constants.ELEMENT_PREFIX, TextPreferences.ELEMENT_PREFIX);
        setAttributes(Constants.ATTRIBUTE_NAME, TextPreferences.ATTRIBUTE_NAME);
        setAttributes(Constants.ATTRIBUTE_VALUE,
                TextPreferences.ATTRIBUTE_VALUE);
        setAttributes(Constants.ATTRIBUTE_PREFIX,
                TextPreferences.ATTRIBUTE_PREFIX);
        setAttributes(Constants.NAMESPACE_NAME, TextPreferences.NAMESPACE_NAME);
        setAttributes(Constants.NAMESPACE_VALUE,
                TextPreferences.NAMESPACE_VALUE);
        setAttributes(Constants.NAMESPACE_PREFIX,
                TextPreferences.NAMESPACE_PREFIX);
        setAttributes(Constants.ENTITY, TextPreferences.ENTITY);
        setAttributes(Constants.COMMENT, TextPreferences.COMMENT);
        setAttributes(Constants.CDATA, TextPreferences.CDATA);
        setAttributes(Constants.SPECIAL, TextPreferences.SPECIAL);
        setAttributes(Constants.PI_TARGET, TextPreferences.PI_TARGET);
        setAttributes(Constants.PI_NAME, TextPreferences.PI_NAME);
        setAttributes(Constants.PI_VALUE, TextPreferences.PI_VALUE);
        setAttributes(Constants.STRING_VALUE, TextPreferences.STRING_VALUE);
        setAttributes(Constants.ENTITY_VALUE, TextPreferences.ENTITY_VALUE);
        setAttributes(Constants.ENTITY_DECLARATION,
                TextPreferences.ENTITY_DECLARATION);
        setAttributes(Constants.ENTITY_NAME, TextPreferences.ENTITY_NAME);
        setAttributes(Constants.ENTITY_TYPE, TextPreferences.ENTITY_TYPE);
        setAttributes(Constants.ATTLIST_DECLARATION,
                TextPreferences.ATTLIST_DECLARATION);
        setAttributes(Constants.ATTLIST_NAME, TextPreferences.ATTLIST_NAME);
        setAttributes(Constants.ATTLIST_TYPE, TextPreferences.ATTLIST_TYPE);
        setAttributes(Constants.ATTLIST_VALUE, TextPreferences.ATTLIST_VALUE);
        setAttributes(Constants.ATTLIST_DEFAULT,
                TextPreferences.ATTLIST_DEFAULT);
        setAttributes(Constants.ELEMENT_DECLARATION,
                TextPreferences.ELEMENT_DECLARATION);
        setAttributes(Constants.ELEMENT_DECLARATION_NAME,
                TextPreferences.ELEMENT_DECLARATION_NAME);
        setAttributes(Constants.ELEMENT_DECLARATION_TYPE,
                TextPreferences.ELEMENT_DECLARATION_TYPE);
        setAttributes(Constants.ELEMENT_DECLARATION_PCDATA,
                TextPreferences.ELEMENT_DECLARATION_PCDATA);
        setAttributes(Constants.ELEMENT_DECLARATION_OPERATOR,
                TextPreferences.ELEMENT_DECLARATION_OPERATOR);
        setAttributes(Constants.NOTATION_DECLARATION,
                TextPreferences.NOTATION_DECLARATION);
        setAttributes(Constants.NOTATION_DECLARATION_NAME,
                TextPreferences.NOTATION_DECLARATION_NAME);
        setAttributes(Constants.NOTATION_DECLARATION_TYPE,
                TextPreferences.NOTATION_DECLARATION_TYPE);
        setAttributes(Constants.DOCTYPE_DECLARATION,
                TextPreferences.DOCTYPE_DECLARATION);
        setAttributes(Constants.DOCTYPE_DECLARATION_TYPE,
                TextPreferences.DOCTYPE_DECLARATION_TYPE);
        xmlEditor.setTabSize(TextPreferences.getTabSize());
        xmlEditor.setAntialiasing(TextPreferences.isAntialiasing());
        // Swing hack
        xmlEditor.revalidate();
        xmlEditor.repaint();
        xmlEditor.updateUI();
    }
    
    private void setTableValues(int index) throws Exception {
//      expressionsCombo.setSelectedIndex(ImportUtilities.splitFile(file,encoding,-1,tableModel,table,headerValues));
        tableModel = ImportUtilities.splitTextFile(file, encoding, index,checkConvertChars.isSelected());
        table.setModel(tableModel);
        for (int cnt = 0; cnt < table.getColumnCount(); ++cnt) {
            TableColumn col = table.getColumnModel().getColumn(cnt);
            ImportUtilities iu = new ImportUtilities();
            
            col.setHeaderRenderer(iu.new HeaderRenderer());
            col.setCellRenderer(iu.new CellRenderer());
            col.setPreferredWidth((ImportUtilities.mainWidth / 4) - 4);
            col.setHeaderValue(ImportUtilities.xmlTypes[0]);
        }
        ImportUtilities.removeBadChars(table,checkConvertChars.isSelected());
        ImportUtilities.removeSpacesFromHeadings(table);
        ImportUtilities.updateOrder(xmlEditor, table, docField.getText(),
                rowField.getText(),checkConvertChars.isSelected());
        checkFirstRow.setSelected(true);
        
    }
}
