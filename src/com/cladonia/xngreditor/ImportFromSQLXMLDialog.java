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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
* Used to import from SQL/XML query to the editor
*
* @version $Revision: 1.10 $, $Date: 2004/11/02 14:58:05 $
 * @author Thomas Curley <tcurley@cladonia.com>
*/
public class ImportFromSQLXMLDialog extends XngrDialog{
    
    private JFrame parent = null;
    private Connection con = null;
    private String importedXML = null;
    private JPanel sqlPanel;
    private JTextArea sqlTextArea;
    private JScrollPane sqlTextAreaScroll;
    private String driver;
    private String dsn;
    private String user;
    private String password;
    private boolean reuseConnection;
    private ConfigurationProperties props;
    
    private NonXMLDocumentChooserDialog chooser = null;

    
	
	/**
     * @param frame
     * @param modal
     */
    public ImportFromSQLXMLDialog(JFrame frame,ConfigurationProperties props) {

        super(frame, true);
        this.parent = frame;
        this.props = props;
                
        
        //make the dialog
        super.setTitle("Import...",
				"Import From a SQL/XML Query",
				"Input the SQL/XML Query");
	    	    
	    JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		sqlPanel = new JPanel();
		sqlPanel.setLayout(new BorderLayout());
		
		TitledBorder sqlBorder;
		sqlBorder = BorderFactory.createTitledBorder("SQL/XML Query");
		sqlPanel.setBorder(new CompoundBorder(sqlBorder,(new EmptyBorder( 5, 5, 5, 5))));
		
		//need to add a JTextArea
		sqlTextArea = new JTextArea(7,75);
		sqlTextAreaScroll = new JScrollPane(sqlTextArea);
		
		sqlTextArea.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
			    sqlTextArea.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		JButton importScript = new JButton("Import Script");
		importScript.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                importScriptButtonPressed();                
            }
		    
		});
		//buttonPanel.add(new JLabel(" "),FormLayout.LEFT);
		buttonPanel.add(importScript,BorderLayout.EAST);
		sqlPanel.add(sqlTextAreaScroll,BorderLayout.CENTER);
		sqlPanel.add(buttonPanel,BorderLayout.SOUTH);
		
		
		
		main.add(sqlPanel);
		pack();
	    setContentPane( main);
		
		
    }
    
    private void importScriptButtonPressed() {
        //use the nonxmmldocumentchooserdialog to allow the user to import a script
        //this also gives them the option to choose the encoding
        
        if ( chooser == null) {
            chooser = new NonXMLDocumentChooserDialog( parent, "Import",NonXMLDocumentChooserDialog.TYPE_TEXT,props);
        }
        chooser.setTitle("Import Script","Import SQL/XML Script","Choose the location of the SQL/XML script");
        chooser.show(NonXMLDocumentChooserDialog.TYPE_TEXT);
        if(!chooser.isCancelled()) {
            URL url = chooser.getInputLocation();
            String encoding = chooser.getTextEncoding();
            
            StringBuffer script = new StringBuffer();
            File file = new File(url.getFile());
            
            try {
                InputStream in = new FileInputStream(file);
                InputStreamReader inReader = new InputStreamReader(in, encoding);
                BufferedReader reader = new BufferedReader(inReader);
                String line;
                boolean firstLine = true;
                            
                reader = new BufferedReader(inReader);
                while ((line = reader.readLine()) != null) {
                    
                    if(firstLine) {
                       script.append(line);
                       firstLine = false;
                    }
                    else {
                        script.append("\n"+line);
                    } 
                }
                
                sqlTextArea.setText(script.toString());
            }
            catch (FileNotFoundException e) {
                MessageHandler.showError( parent, "Cannot find:\n"+file.toString(), "Import SQL/XML Error");
            }
            catch (UnsupportedEncodingException e) {
                MessageHandler.showError( parent, "Unsupported encoding:\n"+encoding, "Import SQL/XML Error");
            }
            catch (IOException e) {
                MessageHandler.showError( parent, "An error occured trying to open:\n"+file.toString(),"Import SQL/XML Error");
            }
        }
    }

    protected void okButtonPressed() {
	    
        try {
            if(this.reuseConnection==false) {
		        //close and reopen connection
		        con.close();
		        con = this.getConnection();
		    }
            Statement stmt = con.createStatement();
            //removed this since it had problems with reading new lines
            //String query = sqlTextArea.getText();
            String query = getParsedQueryString();
            ResultSet rs = stmt.executeQuery(query);
            
            importedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";            
            while (rs.next()) {
                String result = rs.getString("1");
                importedXML += result;
            }
            super.okButtonPressed();
        }
        catch (SQLException e) {
            //MessageHandler.showError("Error With Query: \n"+e.getMessage(),"Database Error");
            MessageHandler.showError(parent,e,"Database Error");
        }
        catch (Exception e) {
            MessageHandler.showError("Error With Database Connection: \n"+e.getMessage(),"Database Error");
        }
        
        
	}
    
    private String getParsedQueryString() {
        
        String text = sqlTextArea.getText();
        
        StringBuffer textBuffer = new StringBuffer();
        for (int cnt = 0; cnt < text.length(); ++cnt) {
            if(text.charAt(cnt) == '\n') {
            	textBuffer.append(' ');                
            }
            else if(text.charAt(cnt) == '\r') {
            	textBuffer.append(' ');
            }
            else {
            	textBuffer.append(text.charAt(cnt));
           	}
           /*if ((text.charAt(i) == '\r') && (i < text.length() - 1) && (text.charAt(i + 1) == '\n')) {
              // Skip \r
               System.out.println("newline at "+i);
              ++i;
              
           }*/
           
        }
        text = textBuffer.toString();
        return(text);
        
    }
	
	public void show(Connection con, boolean reuseConnection, String driver, 
		String dsn, String user, String password) {
	   
	    this.driver = driver;
	    this.dsn = dsn;
	    this.user = user;
	    this.password = password;
	    
	    this.reuseConnection = reuseConnection;
	    
	    this.con = con;
	    
	    
        pack();
		setLocationRelativeTo( parent);
	    show();
	}
	
	
	/**
     * @return Returns the importedXML.
     */
    public String getImportedXML() {

        return importedXML;
    }
    /**
     * @param importedXML The importedXML to set.
     */
    public void setImportedXML(String importedXML) {

        this.importedXML = importedXML;
    }
    
    public Connection getConnection() {
		Connection con1 = null;
		try {
			//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		    Class.forName(driver);
			
			con1 = DriverManager.getConnection(dsn, user, password);
		
		} catch (Exception e) {
		    MessageHandler.showError("Cannot connect to database: "+dsn+
		            "\n"+e.getMessage(),"Database Error");
		    return(null);
		}
		return (con1);
	}

}
