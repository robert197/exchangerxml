/*
 * $Id: NonXMLDocumentChooserDialog.java,v 1.11 2004/10/27 10:43:23 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileFilter;
import org.bounce.FormLayout;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The Non - xml import Document Chooser Dialog. 
 * Allows opening of text, excel or database connections
 *
 * @version	$Revision: 1.11 $, $Date: 2004/10/27 10:43:23 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class NonXMLDocumentChooserDialog extends XngrDialog {
    
    
	private static final Dimension SIZE = new Dimension( 400, 300);
	private JFrame parent			= null;
	private JTextField inputLocationField	= null;
	public final static int TYPE_TEXT = 0;
	public final static int TYPE_EXCEL = 1;
	public final static int TYPE_DATABASE = 2;
	private int type = 0;
    private String[] stringFileEncoding = {"UTF-8","UTF-16","US-ASCII","ISO-8859-1","ISO-8859-2","ISO-8859-3","ISO-8859-4",
			"ISO-8859-5","ISO-8859-6","ISO-8859-7","ISO-8859-8","ISO-8859-9","ISO-8859-13","ISO-8859-15",
			"ISO-2022-JP","Shift_JIS","EUC-JP","GBK","Big5","ISO-2022-KR","GB2312"};
    private JComboBox fileEncodingCombo = null;
    private String textEncoding;
    private JTextField userNameTextField;
    private JPasswordField passwordTextField;
    private JComboBox urlConnectionCombo;
    private Connection con = null;
    public String urlConnection = null;
    public String username = null;
    public String password = null;
    private JComboBox driverCombo;
    public String driver = null;
    private URL urlInputLocation;
    private ConfigurationProperties props;
    private Vector driverHistory;
    private Vector connectionHistory;

    /**
	 * The XSLT execution dialog.
	 *
	 * @param frame the parent frame.
	 */
	public NonXMLDocumentChooserDialog( JFrame parent, String title,final int type, ConfigurationProperties props) {
	    
		super( parent, true);
		//super.setTitle("Import","Import From","Import From Something");
		
		this.type = type;
		this.parent = parent;
		this.props = props;
		
		setResizable( false);
		setTitle( title);
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		//okButton.setEnabled(false);

		//getRootPane().setDefaultButton( okButton);

		JPanel form = new JPanel( new FormLayout( 10, 2));

		// fill all three panels...
		//form.add( getInputPanel(), FormLayout.FULL_FILL);
		
		JPanel inputPanel = new JPanel( new FormLayout( 10, 2));
		inputPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		JPanel locationPanel = null;
		JPanel locButton = null;
		
		if(type!=TYPE_DATABASE) {
			inputLocationField = new JTextField();
			inputLocationField.addAncestorListener( new AncestorListener() {
				public void ancestorAdded( AncestorEvent e) {
				    inputLocationField.requestFocusInWindow();
				}

				public void ancestorMoved( AncestorEvent e) {}
				public void ancestorRemoved( AncestorEvent e) {}
			});
			
	
			JButton inputLocationButton = new JButton( "...");
			inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
			inputLocationButton.setPreferredSize( 
			        new Dimension( inputLocationButton.getPreferredSize().width, 
			                		inputLocationField.getPreferredSize().height));
			
			inputLocationButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					inputLocationButtonPressed();
				}
			});
			
			inputLocationField.setEnabled( true);
			inputLocationButton.setEnabled( true);
	
			locationPanel = new JPanel( new FormLayout(3,2));
			
			locButton = new JPanel(new BorderLayout());
			locButton.add( inputLocationField, BorderLayout.CENTER);
			locButton.add( inputLocationButton, BorderLayout.EAST);
			
			JLabel locationLabel = new JLabel("Location URL:");
			
			locationPanel.add( locationLabel, FormLayout.LEFT);
			locationPanel.add( locButton,FormLayout.RIGHT_FILL);
			
			if(type==TYPE_TEXT) {
			    //need to show file encoding field
			    JLabel fileEncodingLabel = new JLabel("Encoding:");
			    fileEncodingCombo = new JComboBox(stringFileEncoding);
			    fileEncodingCombo.setEditable(true);
	
			    locationPanel.add(fileEncodingLabel,FormLayout.LEFT);
			    locationPanel.add(fileEncodingCombo,FormLayout.RIGHT_FILL);
			    //okButton.setEnabled(true);
			}
			
			
			
				    
		}
		else if(type==TYPE_DATABASE) {
		    locationPanel = new JPanel( new FormLayout(4,2));
		    //need to show username and password,database and driver
		    JLabel driverLabel = new JLabel("Driver:");
		    //driverCombo = new JTextField();
		    driverCombo = new JComboBox();
		    driverCombo.setEditable(true);		    
		    driverCombo.addAncestorListener( new AncestorListener() {
				public void ancestorAdded( AncestorEvent e) {
				    driverCombo.requestFocusInWindow();
				}

				public void ancestorMoved( AncestorEvent e) {}
				public void ancestorRemoved( AncestorEvent e) {}
			});
		    JLabel urlConnectionLabel = new JLabel("Database:");
		    urlConnectionCombo = new JComboBox();
		    urlConnectionCombo.setEditable(true);
		    JLabel userNameLabel = new JLabel("Username:");
		    userNameTextField = new JTextField();
		    JLabel passwordLabel = new JLabel("Password:");
		    passwordTextField = new JPasswordField();
		    
		    /*reuseConnectionCheckBox = new JCheckBox("Reuse Connection");
		    reuseConnectionCheckBox.setSelected(true);*/
		   
		    locationPanel.add(driverLabel,FormLayout.LEFT);
		    locationPanel.add(driverCombo,FormLayout.RIGHT_FILL);
		    locationPanel.add(urlConnectionLabel,FormLayout.LEFT);
		    locationPanel.add(urlConnectionCombo,FormLayout.RIGHT_FILL);
		    locationPanel.add(userNameLabel,FormLayout.LEFT);
		    locationPanel.add(userNameTextField,FormLayout.RIGHT_FILL);
		    locationPanel.add(passwordLabel,FormLayout.LEFT);
		    locationPanel.add(passwordTextField,FormLayout.RIGHT_FILL);
		    /*locationPanel.add(reuseConnectionCheckBox,FormLayout.FULL_FILL);*/
		}
		
		
		inputPanel.add( locationPanel, FormLayout.FULL_FILL);
		form.add(inputPanel,FormLayout.FULL_FILL);
		main.add( form, BorderLayout.CENTER);
		
		
		setContentPane( main);
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( 400, getSize().height));
		
		setLocationRelativeTo( parent);
	}
	
	private void inputLocationButtonPressed() {
		try {
            JFileChooser chooser = getInputFileChooser();
            
            if(this.inputLocationField.getText().length()>0) {
                if(isFile(inputLocationField.getText())) {
                    URL url = new URL(inputLocationField.getText());
                    chooser.setCurrentDirectory(new File(url.getPath()));
                }
            }
            
            if(type==TYPE_EXCEL) 
                chooser.setFileFilter(new ExcelFilter());
            
            int value = chooser.showOpenDialog( getParent());

            if ( value == JFileChooser.APPROVE_OPTION) {
            	File file = chooser.getSelectedFile();
            	Boolean isItAFile = new Boolean(file.isFile());
            	
            	if(isItAFile.booleanValue()) {
	            	URL url = null;
	
	            	
	        		url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
	        		setText( inputLocationField, url.toString());
	        	}
            	else {
            	    MessageHandler.showError( "Could not open the file", "Document Error");
    		        setText(inputLocationField,"");
            	}
            	            	
            }
        }
        catch (Exception e) {
            MessageHandler.showError( "Cannot read file", "Error");
        }
	}
	
	private boolean isFile(String text) {
	    if(text.equalsIgnoreCase(""))
	        return(false);
	    else {
		    File file = URLUtilities.toFile( text);
			if(file==null) 
			    return(false);
			else{
		    	if(file.isFile())
			        return(true);
			    else
			        return(false);
			}
	    }
    }
    
	protected void okButtonPressed() {
	    
	    boolean finished = false;
				
		if(type==TYPE_TEXT) {
//		  if TYPE_TEXT
			// - set the encoding type
		try{
		    textEncoding = (String)fileEncodingCombo.getSelectedItem();
		    URL newFile = new URL(this.inputLocationField.getText());
            urlInputLocation = newFile;
            finished=true;
		}
        catch (MalformedURLException e) {
            //try to see if it's a file
            File file = new File(this.inputLocationField.getText());
            if(file.isFile()) {
                
                try {
                    urlInputLocation = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
                    finished=true;
                }
                catch (MalformedURLException e1) {
                    finished=false;
                }
            }
            else {
                MessageHandler.showError("Cannot find the specified file:\n"+file.toString(),"File Error");
                finished=false;
            }
        }
		}else if(type==TYPE_EXCEL) {
		    
		    try {
                URL newFile = new URL(this.inputLocationField.getText());
                urlInputLocation = newFile;
                finished=true;
                
            }
            catch (MalformedURLException e) {
                //try to see if it's a file
                File file = new File(this.inputLocationField.getText());
                if(file.isFile()) {
                    
                    try {
                        urlInputLocation = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
                        finished=true;
                    }
                    catch (MalformedURLException e1) {
                        finished=false;
                    }
                }
                else {
                    MessageHandler.showError("Cannot find the specified file:\n"+file.toString(),"File Error");
                    finished=false;
                }
            }
			
            
		    
		    
		}else if(type==TYPE_DATABASE) {
			//if TYPE_DATABASE
			// - set the url connection
		    // - set the username
			// - set the password
		    setDriver((String)this.driverCombo.getSelectedItem());
		    setUrlConnection((String)this.urlConnectionCombo.getSelectedItem());
		    setUsername(this.userNameTextField.getText());
		    setPassword(new String(this.passwordTextField.getPassword()));
		    //setDbTable((String)dbTablesCombo.getSelectedItem());
		   
		    con = getConnection(this.getDriver(),
		            		this.getUrlConnection(),
		            		this.getUsername(),
		            		this.getPassword());
		    
		    if(con!=null) {
		   
		        finished=true;
		        saveHistorys();
		       
		    } 
		    else {
		   
		        finished=false;
		    }
		    
		   		    
		}
		if(finished)
		    super.okButtonPressed();
	}
		
    public URL getInputLocation() {
		return urlInputLocation;
	}
	
	public void show( int typeOfFile) {
		
	    type = typeOfFile;
	    //okButton.setEnabled(false);
		if(type==TYPE_EXCEL) {
		    //tablesCombo.removeAll();
		    //this.okButton.setEnabled(true);
		    //inputLocationField.setText( "");
		   
		    			
		}
		else if(type==TYPE_TEXT) {
		    //inputLocationField.setText( "");
		    //okButton.setEnabled(false);
		}
		else if(type==TYPE_DATABASE) {
		    
		    //reset the username and password fields
		    userNameTextField.setText("");
		    passwordTextField.setText("");
		    
		    driverHistory = props.getDatabaseDrivers();
		    this.setDriverHistory();
		    connectionHistory = props.getDatabaseConnections();
		    this.setConnectionHistory();
		}
		
	
		super.show();
	}
	
	private void setDriverHistory()
	{
		driverCombo.removeAllItems();
		
		for (int i=0;i<driverHistory.size();i++)
		{
			driverCombo.addItem((String)driverHistory.get(i));
		}
		
		driverCombo.setSelectedIndex(-1);
	}
	
	private void setConnectionHistory()
	{
		urlConnectionCombo.removeAllItems();
		
		for (int i=0;i<connectionHistory.size();i++)
		{
			urlConnectionCombo.addItem((String)connectionHistory.get(i));
		}
		
		urlConnectionCombo.setSelectedIndex(-1);
	}
	
	public void saveHistorys() {
		// remove previous mappings...
		props.setDatabaseDrivers((String)this.driverCombo.getSelectedItem());
		props.setDatabaseConnections((String)this.urlConnectionCombo.getSelectedItem());
	}
	
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}

	
	// The creation of the file chooser causes an inspection of all 
	// the drives, this can take some time so only do this when necessary.
	private JFileChooser getInputFileChooser() {
		JFileChooser chooser = FileUtilities.getCurrentFileChooser();
		chooser.setFileFilter( chooser.getAcceptAllFileFilter());
		
		File file = URLUtilities.toFile( inputLocationField.getText());

		if ( file != null) {
			chooser.setCurrentDirectory( file);
		}

		chooser.rescanCurrentDirectory();
		
		return chooser;
	}

	private void setText( JTextField field, String text) {
		field.setText( text);
		field.setCaretPosition( 0);
	}
	
	protected boolean isEmpty( String string) {
		if ( string != null && string.trim().length() > 0) {
			return false;
		}
		
		return true;
	}
	
	
	
	
	
    
    /**
     * @return Returns the textEncoding.
     */
    public String getTextEncoding() {

        return textEncoding;
    }
    /**
     * @param textEncoding The textEncoding to set.
     */
    public void setTextEncoding(String textEncoding) {

        this.textEncoding = textEncoding;
    }
    
    /**
     * @return Returns the password.
     */
    public String getPassword() {

        return password;
    }
    /**
     * @param password The password to set.
     */
    public void setPassword(String password) {

        this.password = password;
    }
    /**
     * @return Returns the urlConnection.
     */
    public String getUrlConnection() {

        return urlConnection;
    }
    /**
     * @param urlConnection The urlConnection to set.
     */
    public void setUrlConnection(String urlConnection) {

        this.urlConnection = urlConnection;
    }
    /**
     * @return Returns the username.
     */
    public String getUsername() {

        return username;
    }
    /**
     * @param username The username to set.
     */
    public void setUsername(String username) {

        this.username = username;
    }
    /**
     * @return Returns the dbTable.
     */
    /*public String getDbTable() {

        return dbTable;
    }
    *//**
     * @param dbTable The dbTable to set.
     *//*
    public void setDbTable(String dbTable) {

        this.dbTable = dbTable;
    }*/
    
    private class ExcelFilter extends FileFilter {

        public boolean accept(File f) {

            if (f.isDirectory()) {
                return true;
            }
            String ext = null;
            String s = f.getName();
            int i = s.lastIndexOf('.');
            if (i > 0 && i < s.length() - 1) {
                ext = s.substring(i + 1).toLowerCase();
            }
            String extension = ext;
            if (extension != null) {
                if (extension.equals("xls")) return true;
            }
            else {
                return false;
            }
            return false;
        }

        public String getDescription() {

            return ("Microsoft Excel Spreadsheet (*.xls)");
        }
    }
    /**
     * @return Returns the driver.
     */
    public String getDriver() {

        return driver;
    }
    /**
     * @param driver The driver to set.
     */
    public void setDriver(String driver) {

        this.driver = driver;
    }
    /**
     * @return Returns the con.
     */
    public Connection getCon() {

        return con;
    }
    /**
     * @param con The con to set.
     */
    public void setCon(Connection con) {

        this.con = con;
    }
    
    /*public boolean reuseConnection() {
        return(reuseConnectionCheckBox.isSelected());
    }*/
    
    public Connection getConnection(String driver, String dsn, String user, String password) {
		Connection con1 = null;
		try {
			//Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		    Class.forName(driver);
			
			con1 = DriverManager.getConnection(dsn, user, password);
		
		} catch (Exception e) {
		    //MessageHandler.showError("Cannot connect to database: "+dsn+
		    //        "\n"+e.getMessage(),"Database Error");
		    MessageHandler.showError(parent,e,"Database Error");
		    return(null);
		}
		return (con1);
	}
} 
