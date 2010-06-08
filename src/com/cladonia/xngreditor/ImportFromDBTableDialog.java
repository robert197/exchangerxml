/*
 * Id: 
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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.Vector;
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

/**
 * Used to import from database table to the editor
 * 
 * @version $Revision: 1.15 $, $Date: 2004/10/28 07:55:20 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ImportFromDBTableDialog extends XngrDialog {
    
    private static final Dimension SIZE = new Dimension(470, 580);
    private JCheckBox checkFirstRow;
    
    private Connection con;
    private JComboBox dbTablesCombo;
    private JTextField whereClauseTextField;
    private JTextField orderByClauseTextField;
    private JTextField groupByClauseTextField;
    private boolean reuseConnection = false;
    private String driver;
    private String dsn;
    private String user;
    private String password;
    private boolean suppressActions;
    private JCheckBox checkConvertChars;
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
    private String importedXML;
    
    public ImportFromDBTableDialog(ExchangerEditor parent) {
        
        super(parent, true);
        super.setTitle("Import...", "Import From a database table",
        "Choose the various options to import with");
        this.parent = parent;
        super.setResizable(true);
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(5, 5, 5, 5));
        JPanel centre = new JPanel();
        centre.setLayout(new GridLayout(2, 1));
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
                    MessageHandler.showError(
                            ImportFromDBTableDialog.this.parent,
                            "Error reading file", "Import from database table error");
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
        checkFirstRow = new JCheckBox(
        "First row contains field names");
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
                        MessageHandler.showError(
                                ImportFromDBTableDialog.this.parent,
                                "Error reading file",
                        "Import from database table error");
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
                        try {
                            ImportUtilities.updateOrder(xmlEditor,table,docField.getText(),rowField.getText(),checkConvertChars.isSelected());
                        }
                        catch (Exception e1) {
                            MessageHandler.showError(
                                    ImportFromDBTableDialog.this.parent,
                                    "Error reading file",
                            "Import from database table error");
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
                    
                    ImportUtilities.updateOrder(xmlEditor, table, docField
                            .getText(), rowField.getText(),checkConvertChars.isSelected());
                    
                }
                catch (Exception e) {
                    MessageHandler.showError(
                            ImportFromDBTableDialog.this.parent,
                            "Error reading file",
                    "Import from database table error");
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
                    ImportUtilities.updateOrder(xmlEditor, table, docField
                            .getText(), rowField.getText(),checkConvertChars.isSelected());
                }
                catch (Exception e1) {
                    MessageHandler.showError(
                            ImportFromDBTableDialog.this.parent,
                            "Error reading file",
                    "Import from database table error");
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
                    MessageHandler.showError(
                            ImportFromDBTableDialog.this.parent,
                            "Error reading file",
                    "Import from database table error");
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
            MessageHandler.showError(ImportFromDBTableDialog.this.parent,
                    "Error reading file", "Import from database table error");
            cancelButtonPressed();
            //e1.printStackTrace();
        }
        centre.add(tablePanel);
        centre.add(editPanel);
        JPanel top = new JPanel();
        top.setLayout(new FormLayout(2, 2));
        JLabel dbTablesLabel = new JLabel("Tables:");
        dbTablesCombo = new JComboBox();
        dbTablesCombo.addActionListener(new ActionListener() {
            
            public void actionPerformed(ActionEvent action) {
                
                //when the user changes the table,
                //update the display
                if (suppressActions == false) {
                    try {
                        updateAll();
                        checkConvertChars.setSelected(true);
                    }
                    catch (Exception e) {
                        MessageHandler.showError(
                                ImportFromDBTableDialog.this.parent,
                                "Error Reading Database", "Database Error");
                        cancelButtonPressed();
                    }
                }
            }
        });
        dbTablesCombo.addAncestorListener(new AncestorListener() {
            
            public void ancestorAdded(AncestorEvent e) {
                
                dbTablesCombo.requestFocusInWindow();
            }
            
            public void ancestorMoved(AncestorEvent e) {
                
            }
            
            public void ancestorRemoved(AncestorEvent e) {
                
            }
        });
        //make criteria panel
        JPanel criteriaPanel = new JPanel();
        criteriaPanel.setLayout(new FormLayout(3, 2));
        JLabel whereClauseLabel = new JLabel("Where Clause: ");
        whereClauseTextField = new JTextField();
        JLabel orderByClauseLabel = new JLabel("Order By Clause: ");
        orderByClauseTextField = new JTextField();
        JLabel groupByClauseLabel = new JLabel("Group By Clause: ");
        groupByClauseTextField = new JTextField();
        criteriaPanel.add(whereClauseLabel, FormLayout.LEFT);
        criteriaPanel.add(whereClauseTextField, FormLayout.RIGHT_FILL);
        criteriaPanel.add(orderByClauseLabel, FormLayout.LEFT);
        criteriaPanel.add(orderByClauseTextField, FormLayout.RIGHT_FILL);
        criteriaPanel.add(groupByClauseLabel, FormLayout.LEFT);
        criteriaPanel.add(groupByClauseTextField, FormLayout.RIGHT_FILL);
        TitledBorder criteriaTitle = BorderFactory
        .createTitledBorder("Criteria");
        criteriaPanel.setBorder(new CompoundBorder(criteriaTitle,
                (new EmptyBorder(5, 5, 5, 5))));
        top.add(dbTablesLabel, FormLayout.LEFT);
        top.add(dbTablesCombo, FormLayout.RIGHT_FILL);
        TitledBorder topTitle;
        topTitle = BorderFactory.createTitledBorder("Tables");
        top.setBorder(new CompoundBorder(topTitle,
                (new EmptyBorder(5, 5, 5, 5))));
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new FormLayout(3, 2));
        optionsPanel.add(docLabel, FormLayout.LEFT);
        optionsPanel.add(docField, FormLayout.RIGHT_FILL);
        optionsPanel.add(rowLabel, FormLayout.LEFT);
        optionsPanel.add(rowField, FormLayout.RIGHT_FILL);
        optionsPanel.add(checkFirstRow, FormLayout.FULL_FILL);
        optionsPanel.add(checkConvertChars, FormLayout.FULL_FILL);
        TitledBorder optionsTitle;
        optionsTitle = BorderFactory.createTitledBorder("Options");
        optionsPanel.setBorder(new CompoundBorder(optionsTitle,
                (new EmptyBorder(5, 5, 5, 5))));
        JPanel north = new JPanel();
        north.setLayout(new BorderLayout());
        north.add(top, BorderLayout.NORTH);
        north.add(optionsPanel, BorderLayout.SOUTH);
        main.add(north, BorderLayout.NORTH);
        main.add(centre, BorderLayout.CENTER);
        main.setPreferredSize(new Dimension(ImportUtilities.mainWidth,
                ImportUtilities.mainHeight));
        main.add(criteriaPanel, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(okButton);
        /*addWindowListener(new WindowAdapter() {
         
         public void windowClosing(WindowEvent e) {
         
         cancelled = true;
         hide();
         }
         });*/
        pack();
        setContentPane(main);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        //setSize( new Dimension( SIZE.width, getSize().height));
        updatePreferences();
    }
    
    protected void okButtonPressed() {
        
        try {
            this.setImportedXML(ImportUtilities.createDBXMLFile(table, docField
                    .getText(), rowField.getText(), con, reuseConnection,
                    driver, dsn, user, password, (String) dbTablesCombo
                    .getSelectedItem(), this.whereClauseTextField
                    .getText(), orderByClauseTextField.getText(),
                    groupByClauseTextField.getText(),checkConvertChars.isSelected()));
            if (this.getImportedXML() == null) {
            }
            else {
                super.okButtonPressed();
            }
        }
        catch (StringIndexOutOfBoundsException e) {
            MessageHandler.showError(parent,
                    "Error with query\nPlease check the 'where',"
                    + "'order by' & 'group by' clauses",
            "Database Error");
        }
        catch (Exception e) {
            MessageHandler.showError(parent, "Error with query:\n" + e,
            "Database Error");
            e.printStackTrace();
        }
    }
    
    public void show(Connection con, boolean reuseConnection, String driver,
            String dsn, String user, String password) {
        
        this.driver = driver;
        this.dsn = dsn;
        this.user = user;
        this.password = password;
        this.reuseConnection = reuseConnection;
        this.con = con;
        try {
            checkConvertChars.setSelected(true);
            //clear the tables combo
            suppressActions = true;
            this.dbTablesCombo.removeAllItems();
            String[] tableNames = getTableNames(con);
            for (int cnt = 0; cnt < tableNames.length; ++cnt) {
                this.dbTablesCombo.addItem(tableNames[cnt]);
            }
            this.dbTablesCombo.setSelectedIndex(0);
            suppressActions = false;
            setTableValues();
            pack();
            setLocationRelativeTo(parent);
            show();
        }
        catch (Exception e) {
            MessageHandler.showError(parent, "Error Reading Database",
            "Database Error");
            cancelButtonPressed();
        }
    }
    
    public void updatePreferences() {
        
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
        // Swing hack
        xmlEditor.revalidate();
        xmlEditor.repaint();
        xmlEditor.updateUI();
    }
    
    private void setAttributes(int id, String property) {
        
        FontType type = TextPreferences.getFontType(property);
        xmlEditor.setAttributes(id, type.getColor(), type.getStyle());
    }
    
    private String[] getTableNames(Connection con) throws Exception {
        
        if (this.reuseConnection == false) {
            //close and reopen connection
            con.close();
            con = ImportUtilities.getConnection(driver, dsn, user, password);
        }
        String[] arr = { "TABLE"};
        DatabaseMetaData dmeta = con.getMetaData();
        ResultSet listOfTables = dmeta.getTables(null, null, null, arr);
        Vector tableNames = new Vector();
        int tableCnt = 0;
        while (listOfTables.next()) {
            //System.out.println(listOfTables.getString("TABLE_NAME"));
            tableNames.add(listOfTables.getString("TABLE_NAME"));
        }
        if (tableNames.size() > 0) {
            String[] array = new String[tableNames.size()];
            for (int cnt = 0; cnt < tableNames.size(); ++cnt) {
                array[cnt] = (String) tableNames.get(cnt);
            }
            return (array);
        }
        else {
            return (null);
        }
    }
    
    public void updateAll() throws Exception {
        
        this.setTableValues();
    }
    
    /**
     * @return Returns the importedXML.
     */
    public String getImportedXML() {
        
        return importedXML;
    }
    
    /**
     * @param importedXML
     *            The importedXML to set.
     */
    public void setImportedXML(String importedXML) {
        
        this.importedXML = importedXML;
    }
    
    private void setTableValues() throws Exception {
        
        //Connection con, boolean reuseConnection,
        //String driver, String dsn, String user, String password,
        //String tableName,String whereString,String orderByString,String
        // groupByString
        tableModel = ImportUtilities.splitDatabaseTable(con, reuseConnection,
                driver, dsn, user, password, (String) this.dbTablesCombo
                .getSelectedItem(),
                this.whereClauseTextField.getText(),
                this.orderByClauseTextField.getText(),
                this.groupByClauseTextField.getText(),checkConvertChars.isSelected());
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
