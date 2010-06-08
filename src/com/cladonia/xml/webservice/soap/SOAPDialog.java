  /*
 * $Id: SOAPDialog.java,v 1.14 2004/10/29 15:01:31 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.webservice.soap;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.FormLayout;
import org.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerOutputFormat;
import com.cladonia.xml.XDocument;
import com.cladonia.xml.XMLFormatter;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xml.editor.EditorProperties;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrDialogHeader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * This dialog allows the user to send SOAP messages.
 *
 * @version	$Revision: 1.14 $, $Date: 2004/10/29 15:01:31 $
 * @author Dogsbay
 */
public class SOAPDialog extends XngrDialogHeader implements ActionListener {
	private static final Dimension SIZE = new Dimension( 600, 500  + XngrDialogHeader.HEADER_HEIGHT);
	
	private WaitGlassPane waitPane = null;

	private JTabbedPane tabs = null;
	private XMLPane inputPane = null;
	private XMLPane messagePane = null;
	private XMLPane outputPane = null;

	private ExchangerEditor parent = null;
	private SOAPProperties properties = null;
	
	private JFileChooser attachmentChooser = null;

	private SOAPClient client = null;
	private String message = null;
	
	private File lastAttachment = null;

	private ConfigurationProperties config = null;
	
	private JList attachmentList;

	private JButton closeButton = null;
	private JButton sendButton = null;

	private JButton editButton = null;
	private JButton formatButton = null;

	private JButton addAttachmentButton = null;
	private JButton removeAttachmentButton = null;

//	private JCheckBox formatResultBox	= null;
//	private JCheckBox openAsNewDocumentBox	= null;

	private JComboBox urlField = null;
	private JComboBox actionField = null;

	private ExchangerDocument document = null;

	/**
	 * Creates the window to show the SOAP message.
	 */	
	public SOAPDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, false);
		
		setResizable( true);
		setTitle( "Send SOAP Message");
		setDialogDescription( "Specify SOAP Message settings");
		
		// Init a Glass Pane for the wait cursor
		waitPane = new WaitGlassPane();
		setGlassPane(waitPane);

		this.parent	= parent;
		this.config	= props;
		this.properties	= props.getSOAPProperties();
		
		JPanel mainPanel = new JPanel( new BorderLayout());
		JPanel soapPanel = new JPanel( new BorderLayout());
		
		JPanel connectionPanel = new JPanel( new FormLayout( 10, 2));
		connectionPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Connection Details"),
									new EmptyBorder( 0, 5, 5, 5)));

		urlField = new JComboBox( new Vector( properties.getTargets()));
		urlField.setEditable( true);
		urlField.setPreferredSize( new Dimension( 100, 21));
		urlField.setFont( urlField.getFont().deriveFont( Font.PLAIN));
		urlField.setSelectedItem( null);
		initEditor( urlField, "http://");
		

		actionField = new JComboBox( new Vector( properties.getActions()));
		actionField.setEditable( true);
		actionField.setPreferredSize( new Dimension( 100, 21));
		actionField.setFont( actionField.getFont().deriveFont( Font.PLAIN));
		actionField.setSelectedItem( null);
		
		connectionPanel.add( new JLabel( "URL:"), FormLayout.LEFT);
		connectionPanel.add( urlField, FormLayout.RIGHT_FILL);
		
		connectionPanel.add( new JLabel( "SOAP Action:"), FormLayout.LEFT);
		connectionPanel.add( actionField, FormLayout.RIGHT_FILL);
		
		JPanel attachmentPanel = new JPanel( new FormLayout( 10, 2));
		attachmentPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Attachments"),
									new EmptyBorder( 0, 5, 5, 5)));

		attachmentList = new JList();
		attachmentList.setVisibleRowCount( 2);
		attachmentList.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		attachmentList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = attachmentList.getSelectedIndex();
                if(selected>-1) {
                    attachmentList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		JScrollPane scrollPane = new JScrollPane(	
				attachmentList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		attachmentPanel.add( scrollPane, FormLayout.FULL_FILL);
		
		JPanel attachmentButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		addAttachmentButton = new JButton( "Attach ...");
		addAttachmentButton.setMnemonic('A');
		addAttachmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				addAttachmentButtonPressed();
			}
		});
		removeAttachmentButton = new JButton( "Remove");
		removeAttachmentButton.setMnemonic('R');
		removeAttachmentButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				removeAttachmentButtonPressed();
			}
		});
		
		attachmentButtonPanel.add( addAttachmentButton);
		attachmentButtonPanel.add( removeAttachmentButton);
		
		attachmentPanel.add( attachmentButtonPanel, FormLayout.FULL_FILL);

//		openAsNewDocumentBox.addItemListener( new ItemListener() {
//			public void itemStateChanged( ItemEvent event) {
//				properties.setOpenAsNewDocument( openAsNewDocumentBox.isSelected());
//			}
//		});
//		openAsNewDocumentBox.setSelected( properties.isOpenAsNewDocument());
//		openAsNewDocumentBox.setFont( openAsNewDocumentBox.getFont().deriveFont( Font.PLAIN));

//		formatResultBox = new JCheckBox( "Format");
//		formatResultBox.addItemListener( new ItemListener() {
//			public void itemStateChanged( ItemEvent event) {
//				properties.setFormatResult( formatResultBox.isSelected());
//			}
//		});
//		formatResultBox.setSelected( properties.isFormatResult());
//		formatResultBox.setFont( formatResultBox.getFont().deriveFont( Font.PLAIN));
		

		closeButton = new JButton( "Close");
		closeButton.setMnemonic('C');
		closeButton.setFont( closeButton.getFont().deriveFont( Font.PLAIN));
		closeButton.addActionListener( this);

//		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
//		
//		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
//		buttonPanel.add( sendButton);
//		buttonPanel.add( Box.createHorizontalStrut( 5));
//		buttonPanel.add( closeButton);

//		getRootPane().setDefaultButton( sendButton);
		setContentPane( mainPanel);
		
		JPanel formPanel = new JPanel( new FormLayout( 10, 2));
		formPanel.add( connectionPanel, FormLayout.FULL_FILL);
		formPanel.add( attachmentPanel, FormLayout.FULL_FILL);
		
		tabs = new JTabbedPane();
		tabs.add( "SOAP Request", createInputPane());
		tabs.add( "MIME Message", createMessagePane());
		tabs.add( "SOAP Response", createOutputPane());
		tabs.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				Component comp = tabs.getSelectedComponent();

				if ( comp == messagePane) {
					try {
						setMessage();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		mainPanel.add( formPanel, BorderLayout.NORTH);
		mainPanel.add( tabs, BorderLayout.CENTER);
//		mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		setSize( SIZE);
		setLocationRelativeTo( parent);
		
		setDefaultCloseOperation( HIDE_ON_CLOSE);
	}
	
	public void updatePreferences() {
		inputPane.updatePreferences();
		messagePane.updatePreferences();
		outputPane.updatePreferences();
	}
	
	/**
	 * Shows the SOAP dialog and sets the document...
	 *
	 * @param document the SOAP envelope.
	 */
	public void show( ExchangerDocument document) { // SOAPException, SAXParseException, IOException {
		this.document = document;
		
		URL url = document.getURL();
		
		if ( url != null) {
			String location = url.toString();

			if (url.getProtocol().equals("file")) {
				location = location.substring(6, location.length());
			}

			setTitle( "SOAP Message ["+location+"]","Send SOAP Message");
		} else {
			setTitle( "SOAP Message ["+document.getName()+"]", "Send SOAP Message");
		}
		
		System.setProperty( "javax.xml.transform.TransformerFactory", "org.apache.xalan.processor.TransformerFactoryImpl");
		
		attachmentList.setModel( new AttachmentListModel());

		inputPane.setText( document.getText());
		messagePane.setText( "");
		outputPane.setText( "");
		
		tabs.setSelectedIndex( 0);
		
		//super.setVisible(true);
		super.show();

		setMessage();
	}
	
	private void addAttachmentButtonPressed() {
		JFileChooser chooser = getAttachmentChooser();
		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			
			((AttachmentListModel)attachmentList.getModel()).addAttachment( file);
			lastAttachment = file;
		}
		
		setMessage();
	}
	
	private void removeAttachmentButtonPressed() {
		/*int index = attachmentList.getSelectedIndex();
		
		if ( index != -1) {
			((AttachmentListModel)attachmentList.getModel()).removeAttachment( index);
		}
		*/
	    
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    Object[] selectedObjects = attachmentList.getSelectedValues();
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.length>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.length;++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += (new File((String)selectedObjects[cnt])).getName();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.length>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
		                int index = -1;
		                for(int icnt=0;icnt<((AttachmentListModel)attachmentList.getModel()).getSize();++icnt) {
							File fileInList = ((AttachmentListModel)attachmentList.getModel()).getAttachment(icnt);
							File selectedFile = new File((String)selectedObjects[cnt]);
							
		                    if(fileInList.equals(selectedFile)) {
		                    	index = icnt;
		                        break;
		                    }
		                }
		                if(index>-1) {
			                ((AttachmentListModel)attachmentList.getModel()).removeAttachment( index);
		                }
		                deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    int index = -1;
		                for(int icnt=0;icnt<((AttachmentListModel)attachmentList.getModel()).getSize();++icnt) {
							File fileInList = ((AttachmentListModel)attachmentList.getModel()).getAttachment(icnt);
							File selectedFile = new File((String)selectedObjects[cnt]);
							
		                    if(fileInList.equals(selectedFile)) {
		                    	index = icnt;
		                        break;
		                    }
		                }
		                if(index>-1) {
			                ((AttachmentListModel)attachmentList.getModel()).removeAttachment( index);
		                }
					}
		            
	            } else {
	                int index = -1;
	                for(int icnt=0;icnt<((AttachmentListModel)attachmentList.getModel()).getSize();++icnt) {
						File fileInList = ((AttachmentListModel)attachmentList.getModel()).getAttachment(icnt);
						File selectedFile = new File((String)selectedObjects[cnt]);
						
	                    if(fileInList.equals(selectedFile)) {
	                    	index = icnt;
	                        break;
	                    }
	                }
	                if(index>-1) {
		                ((AttachmentListModel)attachmentList.getModel()).removeAttachment( index);
	                }
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	    
		/*int selectedCnt = attachmentList.getSelectedValues().length;
	    if(selectedCnt>0) {
	        int[] selectedIndexes = attachmentList.getSelectedIndices();
	        
	        boolean deleteAll = false;
		    int questionResult = -1;
		    
	        for(int cnt=selectedCnt-1;cnt>-1;--cnt) {
				int index = selectedIndexes[cnt];
				
				File props = ((AttachmentListModel)attachmentList.getModel()).getAttachment( index);
				if ( index != -1) {
				    
				    
				    if(!deleteAll) {
	                   
	                    String message = "Are you sure you want to delete:\n ";
	                    message += props.getName();
	                    if(selectedCnt>1) {
	                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
	                    }
	                    else {
	                        questionResult = MessageHandler.showConfirm(parent,message);
	                    }
	                    
	                    if(questionResult==MessageHandler.CONFIRM_ALL_OPTION)
	                        deleteAll=true;
	                    
	                    if(questionResult==JOptionPane.YES_OPTION) {
		                    ((AttachmentListModel)attachmentList.getModel()).removeAttachment( index);
							
		                }
	                }
	                
	                
				}
	        }
	        if(deleteAll) {
		        for(int cnt=selectedCnt-1;cnt>-1;--cnt) {
					int index = selectedIndexes[cnt];
					
					File props = ((AttachmentListModel)attachmentList.getModel()).getAttachment( index);
					if ( index != -1) {
					    ((AttachmentListModel)attachmentList.getModel()).removeAttachment( index);
					}
		        }
	        }
		}*/
		

		setMessage();
	}

	private JFileChooser getAttachmentChooser() {
		if ( attachmentChooser == null) {
			attachmentChooser = FileUtilities.createFileChooser();
		} 
		
		if ( lastAttachment != null) {
			attachmentChooser.setCurrentDirectory( lastAttachment);
		} else {
			attachmentChooser.setCurrentDirectory( FileUtilities.getLastOpenedFile());
		}
		
		attachmentChooser.rescanCurrentDirectory();
		
		return attachmentChooser;
	}

	// parses the current SOAP envelope text ...
	private void setMessage() { // SOAPException, SAXParseException, IOException {
		try{
			javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			
			javax.xml.parsers.DocumentBuilder db = null;
	
			try {
				db = dbf.newDocumentBuilder();
			} catch ( Exception e) {
				// Should never happen!
				e.printStackTrace();
			}
			
			ByteArrayInputStream stream = new ByteArrayInputStream( inputPane.getText().getBytes());
			
			try {
				Document doc = db.parse( stream);
				client = new SOAPClient( doc);
			} catch ( IOException e) {
				// Should not happen!
				e.printStackTrace();
			} catch ( SAXException e) {
				MessageHandler.showError( "Error parsing the SOAP message.", e, "SOAP Error");
			}
			
			File[] attachments = ((AttachmentListModel)attachmentList.getModel()).getAttachments();
	
			if ( attachments != null) {
				client.addAttachments( attachments);
			}
	
			messagePane.setText( client.getMimeMessage());
		} catch ( SOAPException e) {
			MessageHandler.showError( "Error parsing the SOAP message.", e.getCause(), "SOAP Error");
		}
	}
	
	private JPanel createInputPane() {
		inputPane = new XMLPane( false);
		
		JPanel panel = new JPanel ( new BorderLayout());

		sendButton = new JButton( "Send");
		sendButton.setMnemonic('S');
		sendButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				sendButtonPressed();
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( sendButton);
		
		panel.add( inputPane, BorderLayout.CENTER);
		panel.add( buttonPanel, BorderLayout.SOUTH);
		
		return panel;
	}

	private JPanel createMessagePane() {
		messagePane = new XMLPane( true);
		JPanel panel = new JPanel ( new BorderLayout());

		JButton sendButton = new JButton( "Send");
		sendButton.setMnemonic('S');
		sendButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				sendButtonPressed();
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( sendButton);
		
		panel.add( messagePane, BorderLayout.CENTER);
		panel.add( buttonPanel, BorderLayout.SOUTH);

		return panel;
	}
	
	private void sendButtonPressed() {
		send( (String)((JTextField)urlField.getEditor().getEditorComponent()).getText(), (String)((JTextField)actionField.getEditor().getEditorComponent()).getText());
	}

	private JPanel createOutputPane() {
		outputPane = new XMLPane( true);
		
		editButton = new JButton( "Edit");
		editButton.setMnemonic('E');
		editButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				editButtonPressed();
			}
		});

		formatButton = new JButton( "Format");
		formatButton.setMnemonic('F');
		formatButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				formatButtonPressed();
			}
		});

		JPanel panel = new JPanel ( new BorderLayout());

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( formatButton);
		buttonPanel.add( editButton);
		
		panel.add( outputPane, BorderLayout.CENTER);
		panel.add( buttonPanel, BorderLayout.SOUTH);
		
		return panel;
	}
	
	public void editButtonPressed() {
		// Edit the ouput...
		ExchangerDocument document = new ExchangerDocument( outputPane.getText());
		parent.open( document, null);
		
		//setVisible(false);
		hide();
	}

	public void formatButtonPressed() {
		// Format the information
		String output = outputPane.getText();
		StringReader reader = new StringReader( output);
		try {
			outputPane.setText( format( reader));
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The send button has been pressed!
	 *
	 * @param e the event from the send button.
	 */
	public void actionPerformed( ActionEvent e) {

		if ( e.getSource() == closeButton) {
			//setVisible(false);
			hide();
		} 
	} 

	private void send( final String target, final String action) {
//		System.out.println( "SOAPDialog.send( "+target+", "+action+")");

		// Create the connection where we're going to send the file.
		setMessage();
		final AutomaticProgressMonitor monitor = new AutomaticProgressMonitor( parent, null, "Sending SOAP message ...");
		
		Runnable runner = new Runnable() {
			public void run()  {
				try {
					client.setSOAPAction( action);
					final String response = client.send2( new URL( target));
					
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							monitor.stop();
							outputPane.setText( response);
							tabs.setSelectedIndex(2);
					
							properties.addAction( action);
							properties.addTarget( target);
						}
					});
			
				} catch( SOAPException se) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							monitor.stop();
						}
					});
					MessageHandler.showError( "Error sending the SOAP message.", se.getCause(), "SOAP Error");
				} catch( MalformedURLException mue) {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							monitor.stop();
						}
					});
					MessageHandler.showError( "Invalid target URL :"+target, "SOAP Error");
				} finally {
					SwingUtilities.invokeLater( new Runnable() {
						public void run() {
							fillCombo( urlField, properties.getTargets());
							
							if (!action.equals(""))
							{
								fillCombo( actionField, properties.getActions());
							}
							else
							{
								// if no action supplied
								actionField.setSelectedIndex(-1);
							}
						}
					});
				}

			}
		};
		
		// Create and start the thread ...
		Thread thread = new Thread( runner);
		monitor.setThread( thread);
		monitor.start();
	}
	
	private void fillCombo( JComboBox box, Vector items) {
//		System.out.println( "fillCombo( "+box+", "+items+")");
		box.removeAllItems();
		
		for ( int i = 0; i < items.size(); i++) {
//			System.out.println( "item["+i+"] = "+(String)items.elementAt(i));
			box.addItem( (String)items.elementAt(i));
		}
		
		resetCaretPosition( box);
	}
	
	private void resetCaretPosition( JComboBox box) {
		Component editor = box.getEditor().getEditorComponent();
		
		if ( editor instanceof JTextField) {
			((JTextField)editor).setCaretPosition( 0);
		}
	}
	
	private void initEditor( JComboBox box, String text) {
		Component editor = box.getEditor().getEditorComponent();
		
		if ( editor instanceof JTextField) {
			((JTextField)editor).setText( text);
		}
	}


	private String format( Reader reader) throws SAXParseException, IOException {
		SAXReader sax = XMLUtilities.createReader( false);
		sax.setStripWhitespaceText( false);
		sax.setMergeAdjacentText( false);

		XDocument doc = XMLUtilities.parse( sax, new BufferedReader( reader), null);

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		String indent = "";
		
		ExchangerOutputFormat format = new ExchangerOutputFormat();
		format.setEncoding( doc.getEncoding());

		XMLFormatter formatter = new XMLFormatter( out, format);
		
		EditorProperties properties = config.getEditorProperties();
		switch ( properties.getFormatType()) {
			case EditorProperties.FORMAT_CUSTOM:
				if ( properties.isCustomIndent()) {
					format.setIndent( TextPreferences.getTabString());
				} else {
					format.setIndent( "");
				}
				
				format.setNewlines( properties.isCustomNewline());
				format.setPadText( properties.isCustomPadText());

				formatter.setTrimText( properties.isCustomStrip());
				formatter.setPreserveMixedContent( properties.isCustomPreserveMixedContent());
				break;
			case EditorProperties.FORMAT_COMPACT:
				format.setIndent( "");
				format.setNewlines( false);
				format.setPadText( false);

				formatter.setTrimText( true);
				formatter.setPreserveMixedContent( false);
				break;
			case EditorProperties.FORMAT_STANDARD:
				format.setIndent( TextPreferences.getTabString());

				format.setNewlines( true);
				format.setPadText( false);

				formatter.setTrimText( true);
				formatter.setPreserveMixedContent( true);
				break;
		}
		
		formatter.write( doc);

		return out.toString( doc.getEncoding());
	}
	
	private static String toString( Reader reader) throws IOException {
		ByteArrayInputStream stream = null;
		CharArrayWriter writer = new CharArrayWriter();
		
		char[] buffer = new char[4096];
		int len = 0;
		
		while ( (len = reader.read( buffer)) != -1) {
			writer.write( buffer, 0, len);
		}
		
		return writer.toString();
	}
	
	class AttachmentListModel extends AbstractListModel {
		Vector attachments = null;
		
		public AttachmentListModel() {
			attachments = new Vector();
		}
		
		public int getSize() {
			if ( attachments != null) {
				return attachments.size();
			}
			
			return 0;
		}
		
		public File[] getAttachments() {
			if ( attachments.size() > 0) {
				File[] files = new File[attachments.size()];
				
				for ( int i = 0; i < attachments.size(); i++) {
					files[i] = (File)attachments.elementAt(i);
				}
				
				return files;
			} else {
				return null;
			}
		}

		public void addAttachment( File attachment) {
			attachments.addElement( attachment);
			
			fireIntervalAdded( this, attachments.size()-1, attachments.size()-1);
		}

		public void removeAttachment( int index) {
			attachments.removeElementAt( index);

			fireIntervalRemoved( this, index, index);
		}

		public Object getElementAt( int i) {
			return ((File)attachments.elementAt( i)).getAbsolutePath();
		}

		public File getAttachment( int i) {
			return (File)attachments.elementAt( i);
		}
	}
	
	public class AutomaticProgressMonitor extends ProgressMonitor {
		private int progress = 0;
		private Timer timer = null;
		private Thread thread = null;
		private String title = null;
		private JDialog dialog = null;
		
		public AutomaticProgressMonitor( Component parent, String title, String message) {
			super( parent, message, title, 0 , 100);
			
			this.title = title;
		}
		
		public void setThread( Thread thread) {
			this.thread = thread;
		}
		
		public void start() {
			progress = 0;

			setWait( true);
			timer = new Timer( 200, new ActionListener() {
				public void actionPerformed( ActionEvent e) {
					if ( isCanceled()) {
						thread.stop();
						stop();
					} else {
						progress += 1;
						setProgress( progress);
					}
				}
			});

			thread.start();
			timer.start();
		}
		
		public void stop() {
			setWait( false);
			timer.stop();
			timer = null;
			setProgress( 100);
		}
	}

	/**
	 * Sets the wait cursor on the Exchanger editor frame.
	 *
	 * @param enabled true when wait is enabled.
	 */
	public void setWait(final boolean enabled) {
		parent.setWait( enabled);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				waitPane.setVisible(enabled);
			}
		});
	}

	private class WaitGlassPane extends JPanel {
		public WaitGlassPane() {
			setOpaque(false);
			addKeyListener(new KeyAdapter() {});
			addMouseListener(new MouseAdapter() {});
			super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}
}  
