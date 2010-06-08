/*
 * $Id: FindInFilesDialog.java,v 1.10 2005/08/31 10:27:16 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.filechooser.FileFilter;

import org.bounce.DefaultFileFilter;
import org.bounce.FormLayout;

import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.l2fprod.common.swing.JDirectoryChooser;

/**
 * The find dialog for the ExchangerEditor editor.
 *
 * @version	$Revision: 1.10 $, $Date: 2005/08/31 10:27:16 $
 * @author Dogsbay
 */
public class FindInFilesDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 350, 150);
	private static final String[] ALL_FILES_FILTER_DESCRIPTION_ARRAY = {"All Files(*.*)", "*.*"};
	
	// The components that contain the values
	private JComboBox searchField		= null;

	private JCheckBox matchCaseButton			= null;
	private JCheckBox regularExpressionButton	= null;
	private JCheckBox matchWholeWordButton			= null;
	
	private ConfigurationProperties properties = null;
	private JComboBox fileTypeField;
	private JTextField folderField;
	private JButton folderButton;
	private ExchangerEditor parent;

	/**
	 * The dialog that displays the properties for the document.
	 *
	 * @param frame the parent frame.
	 */
	public FindInFilesDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, true);
		
		properties = props;
		this.parent = parent;
		
		setResizable( false);
		setTitle( "Find in Files");
		setDialogDescription( "Specify the Search criteria.");

		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = true;
				//setVisible(false);
				//show();
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		JPanel searchPanel = new JPanel( new FormLayout( 10, 5));
		searchPanel.setBorder( new EmptyBorder( 0, 0, 5, 0));
		searchPanel.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
				searchField.requestFocusInWindow();
				searchField.getEditor().selectAll();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
	
		searchField = new JComboBox();
		searchField.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				}
			}
		});

		searchField.setFont( searchField.getFont().deriveFont( Font.PLAIN));
		searchField.setPreferredSize( new Dimension( 100, 23));
		searchField.setEditable(true);

		searchPanel.add( new JLabel("Find:"), FormLayout.LEFT);
		searchPanel.add( searchField, FormLayout.RIGHT_FILL);
		
		fileTypeField = new JComboBox();
		fileTypeField.setFont( searchField.getFont().deriveFont( Font.PLAIN));
		fileTypeField.setPreferredSize( new Dimension( 100, 23));
		fileTypeField.setEditable(true);
		
		
		
		searchPanel.add( new JLabel("In Files:"), FormLayout.LEFT);
		searchPanel.add( fileTypeField, FormLayout.RIGHT_FILL);
		
		JPanel folderPanel = new JPanel(new BorderLayout());
		folderField = new JTextField();
		folderButton = new JButton("...");
		folderButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {

				
				
				JDirectoryChooser chooser = FileUtilities.getDirectoryChooser();
				
				try {
					if((getFolder() != null) && (getFolder().size() > 0)) {
						//chooser = new JDirectoryChooser((File) getFolder().get(0));
						chooser.setSelectedFile((File)getFolder().get(0));
					}
					
				} catch (Exception e2) {
					
				}
				
				
				int value = chooser.showOpenDialog(FindInFilesDialog.this.parent);
				/*JFileChooser chooser = FileUtilities.getDirectoryChooser();
							
			 	int value = chooser.showOpenDialog( FindInFilesDialog.this.parent);
				*/
			 	if ( value == JFileChooser.APPROVE_OPTION) {
				 	File[] files = chooser.getSelectedFiles();
				 	
				 	StringBuffer filesBuffer = new StringBuffer();
				 	for(int cnt=0;cnt<files.length;++cnt) {
				 		
				 		try {
							filesBuffer.append(XngrURLUtilities.getURLFromFile(files[cnt]));
							if(cnt+1 < files.length) {
								filesBuffer.append(";");
							}
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				 	}
					
				 	
					folderField.setText(filesBuffer.toString());
						
					
			 	}
			}
			
		});
		
		folderPanel.add(folderField, BorderLayout.CENTER);
		folderPanel.add(folderButton, BorderLayout.EAST);
		
		searchPanel.add( new JLabel("In Folder: "), FormLayout.LEFT);
		searchPanel.add( folderPanel, FormLayout.RIGHT_FILL);

		matchCaseButton = new JCheckBox( "Case Sensitive");
		matchCaseButton.setMnemonic( 'C');
//		matchCaseButton.setFont( matchCaseButton.getFont().deriveFont( Font.PLAIN));

		matchWholeWordButton = new JCheckBox( "Whole Word");
		matchWholeWordButton.setMnemonic( 'W');
//		matchWholeWordButton.setFont( matchWholeWordButton.getFont().deriveFont( Font.PLAIN));

		regularExpressionButton = new JCheckBox( "Regular Expression");
		regularExpressionButton.setMnemonic( 'x');
//		regularExpressionButton.setFont( regularExpressionButton.getFont().deriveFont( Font.PLAIN));

		JPanel matchCasePanel = new JPanel( new FormLayout());
		matchCasePanel.setBorder( new TitledBorder( "Options"));
//		matchCasePanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		matchCasePanel.add( matchCaseButton, FormLayout.FULL);
		matchCasePanel.add( matchWholeWordButton, FormLayout.FULL);
		matchCasePanel.add( regularExpressionButton, FormLayout.FULL);

		//removed for xngr-dialog
		super.okButton.setText("Find");
		super.okButton.setMnemonic('F');
/*
		cancelButton = new JButton( "Cancel");
		cancelButton.setMnemonic( 'C');
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		okButton = new JButton( "Find");
		okButton.setMnemonic( 'F');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				findButtonPressed();
			}
		});
		getRootPane().setDefaultButton( okButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 10, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
*/
		main.add( searchPanel, BorderLayout.NORTH);
		main.add( matchCasePanel, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});
*/
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
	}
	
	
	/**
	 * Returns the search text... 
	 *
	 * @return the search text.
	 */
	public String getSearch() {
		String result = null;

		Object item = searchField.getEditor().getItem();

		if ( item != null) {
			result = item.toString();
		}
		
		return result;
	}

	/**
	 * Wether the search should be case sensitive. 
	 *
	 * @return true when the search should be case sensitive.
	 */
	public boolean isCaseSensitive() {
		return matchCaseButton.isSelected();
	}
	
	public boolean isMatchWholeWord() {
		return matchWholeWordButton.isSelected();
	}

	/**
	 * Wether the search should be from top to bottom. 
	 *
	 * @return true when the search direction should be from top to bottom.
	 */
	public boolean isRegularExpression() {
		return regularExpressionButton.isSelected();
	}

	protected void okButtonPressed() {
		
	
		if((searchField.getSelectedItem() != null) && (searchField.getSelectedItem().toString().length() > 0)) {
		
			if((this.fileTypeField.getSelectedItem() != null) && (this.fileTypeField.getSelectedItem().toString().length() > 0)) {
				
				if((this.folderField.getText() != null) && (folderField.getText().length() > 0)) {
					
					super.okButtonPressed();	
				}
				else {
					MessageHandler.showError(parent, "Please enter a folder to search in", "Find In Files Error");
					SwingUtilities.invokeLater(new Runnable() {

						public void run() {

							folderField.requestFocus();
						}
										
					});
				}
				
			}
			else {
				MessageHandler.showError(parent, "Please enter a valid file type", "Find In Files Error");
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {

						fileTypeField.requestFocus();
					}
									
				});
			}
		}
		else {
			
			MessageHandler.showError(parent, "Please enter a search term", "Find In Files Error");
			SwingUtilities.invokeLater(new Runnable() {

				public void run() {

					searchField.requestFocus();
				}
								
			});
		}
		 
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	/**
	 * Initialises the values in the dialog.
	 */
	public void init() {
		if ( searchField.getItemCount() > 0) {
			searchField.removeAllItems();
		}
		
		Vector searches = properties.getSearches();
		
		for ( int i = 0; i < searches.size(); i++) {
			searchField.addItem( searches.elementAt(i));
		}

		if ( searchField.getItemCount() > 0) {
			searchField.setSelectedIndex( 0);
		}

		matchCaseButton.setSelected( properties.isMatchCase());
		matchWholeWordButton.setSelected( properties.isMatchWholeWord());
		regularExpressionButton.setSelected( properties.isRegularExpression());
		
		fileTypeField.removeAllItems();
		fileTypeField.addItem( new FileFilters(FileUtilities.getXMLFilter()));
		fileTypeField.addItem( new FileFilters(FileUtilities.getDTDFilter()));
		fileTypeField.addItem( new FileFilters(FileUtilities.getXSLFilter()));
		fileTypeField.addItem( new FileFilters(FileUtilities.getPDFFilter()));
		fileTypeField.addItem( new FileFilters(FileUtilities.getPSFilter()));
		fileTypeField.addItem( new FileFilters(FileUtilities.getSVGFilter()));
		JDirectoryChooser chooser = new JDirectoryChooser();
		fileTypeField.addItem( ALL_FILES_FILTER_DESCRIPTION_ARRAY[0]);
					
		File file = new File( properties.getFindInFilesFolder());
		this.folderField.setText(file.toString());
		
		searchField.requestFocusInWindow();
		searchField.getEditor().selectAll();
		
		
		
	}
	
	public class FileFilters {

		public DefaultFileFilter fileFilter;

		public FileFilters(DefaultFileFilter fileFilter) {

			this.fileFilter = fileFilter;
		}
		
		public FileFilters(FileFilter fileFilter) {

			this.fileFilter = (DefaultFileFilter) fileFilter;
			
		}
		
		public String toString() {
			return(this.fileFilter.getDescription());
		}
		
	}

	/**
	 * @return
	 */
	public DefaultFileFilter getFileType() {

		if(this.fileTypeField.getSelectedItem() != null) {
			if(this.fileTypeField.getSelectedItem() instanceof FileFilters) {
				return(((FileFilters)this.fileTypeField.getSelectedItem()).fileFilter);
			}
			else if(this.fileTypeField.getSelectedItem() instanceof FileFilter) {
				return(null);
			}
			else {
				boolean found = false;
				for(int cnt=0;cnt<ALL_FILES_FILTER_DESCRIPTION_ARRAY.length;++cnt) {
					if(this.fileTypeField.getSelectedItem().toString().equalsIgnoreCase(ALL_FILES_FILTER_DESCRIPTION_ARRAY[cnt])) {
						found = true;
					}	
				}
				if(found == true) {
					return(null);
				}
				else {
					DefaultFileFilter ff = new DefaultFileFilter(this.fileTypeField.getSelectedItem().toString(), "UserDefinedType");
					return(ff);
				}
				
			}
		}
		else {
			return null;
		}
	}


	/**
	 * @return
	 */
	public Vector getFolder() {

		Vector files = new Vector();
		
		if((this.folderField.getText() != null) && (this.folderField.getText().length() > 0)) {
			
			StringTokenizer st = new StringTokenizer(this.folderField.getText(), ";");
			
			
			while (st.hasMoreTokens() == true) {
				try {
					
					File folderFile = getFile(st.nextToken());
					if(folderFile != null) {
						files.add(folderFile);
					}				
										
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
			
		}
		
		return (files);
		
	}
	
	public File getFile(String text) {
		File file = null;
		
		file = new File(text);
		
		if(file == null) {
			
			file = URLUtilities.toFile(text);
		}
		
		if(file != null) {
			
			if(file.isDirectory()) {
				return(file);
			}
			else {
				file = URLUtilities.toFile(text);
				if(file.isDirectory() == true) {
					return(file);
				}
				else {
					return(null);
				}
			}
			
		}
		else {
			return(null);
		}
		
	}
} 
