/*
 * $Id: TemplateManagementDialog.java,v 1.14 2004/11/04 19:21:50 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.template;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.DefaultFileFilter;
import org.bounce.event.DoubleClickListener;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XngrDialogHeader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The template properties management dialog.
 *
 * @version	$Revision: 1.14 $, $Date: 2004/11/04 19:21:50 $
 * @author Dogsbay
 */
public class TemplateManagementDialog extends XngrDialogHeader {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 300, 300  + XngrDialogHeader.HEADER_HEIGHT);

	private ConfigurationProperties properties = null;
	
	private ExchangerEditor parent = null;

	private JList list	= null;

	private TemplateListModel model = null;
	private TemplatePropertiesDialog dialog = null;

	private JFileChooser importChooser = null;
	private JFileChooser exportChooser = null;
	private File dir = null;

	private JButton changeButton = null;
	private JButton newButton = null;
	private JButton deleteButton = null;

	private JButton exportButton = null;
	private JButton importButton = null;

	private JButton closeButton = null;

	/**
	 * The dialog that displays the list of template properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public TemplateManagementDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, true);
		
		this.parent = parent;
		this.properties = props;
		
		setResizable( false);
		
		setTitle( "Manage Templates");
		setDialogDescription( "Edit, Add and Delete templates");
		
		list = new JList();
		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					changeButtonPressed();
				}
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = list.getSelectedIndex();
                if(selected>-1) {
                    list.ensureIndexIsVisible(selected);
                }
            }
		    
		});		
		
		JPanel listButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));

		changeButton = new JButton( "Edit");
		changeButton.setMnemonic('E');
		changeButton.setFont( changeButton.getFont().deriveFont( Font.PLAIN));
		changeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				changeButtonPressed();
			}
		});
		listButtonPanel.add( changeButton);

		newButton = new JButton( "New");
		newButton.setMnemonic('N');
		newButton.setFont( newButton.getFont().deriveFont( Font.PLAIN));
		newButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				newButtonPressed();
			}
		});

		deleteButton = new JButton( "Delete");
		deleteButton.setMnemonic('D');
		deleteButton.setFont( deleteButton.getFont().deriveFont( Font.PLAIN));
		deleteButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				deleteButtonPressed();
			}
		});
		listButtonPanel.add( deleteButton);

		importButton = new JButton( "Import");
		importButton.setMnemonic('I');
		importButton.setFont( importButton.getFont().deriveFont( Font.PLAIN));
		importButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				importButtonPressed();
			}
		});

		exportButton = new JButton( "Export");
		exportButton.setMnemonic('X');
		exportButton.setFont( exportButton.getFont().deriveFont( Font.PLAIN));
		exportButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				exportButtonPressed();
			}
		});

		JPanel portButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		portButtonPanel.add( importButton);
		portButtonPanel.add( exportButton);
		portButtonPanel.setBorder( new EmptyBorder( 0, 0, 5, 0));

		JPanel southButtonPanel = new JPanel( new BorderLayout());
		southButtonPanel.add( listButtonPanel, BorderLayout.EAST);
		southButtonPanel.add( newButton, BorderLayout.WEST);
		southButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));

		closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				properties.save();
				setVisible(false);
			}
		});
		
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				properties.save();
				setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( closeButton);
				
		getRootPane().setDefaultButton( closeButton);
		
		JPanel mainPanel = new JPanel( new BorderLayout());
//		mainPanel.add( text, BorderLayout.NORTH);
		JScrollPane scrollPane = new JScrollPane(	list,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
								
		JPanel listPanel = new JPanel( new BorderLayout());
		listPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Templates"),
									new EmptyBorder( 0, 5, 5, 5)));
		listPanel.add( scrollPane, BorderLayout.CENTER);
		listPanel.add( portButtonPanel, BorderLayout.NORTH);
		listPanel.add( southButtonPanel, BorderLayout.SOUTH);

		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.add( listPanel, BorderLayout.CENTER);
		mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		this.setContentPane( mainPanel); 
		
		this.pack();

		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
		setLocationRelativeTo( parent);
	}
	
	/**
	 * Set the properties.
	 *
	 * @param properties the list of template properties.
	 */
	public void show() {
		setProperties( properties.getTemplateProperties());
		
		//super.setVisible(true);
		super.show();
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of template properties.
	 */
	private void setProperties( Vector properties) {
		model = new TemplateListModel( properties);
		list.setModel( model);
		
		list.setSelectedIndex( 0);
	}

	private TemplatePropertiesDialog getPropertiesDialog() {
		if ( dialog == null) {
			dialog = new TemplatePropertiesDialog( parent, false);
		}
		
		return dialog;
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of template properties.
	 */
	public void setSelectedTemplate( TemplateProperties template) {
		for ( int i = 0; i < model.getSize(); i++) {
			TemplateProperties prop = model.getTemplate( i);
			
			if ( prop == template) {
				list.setSelectedIndex( i);
				return;
			}
		}
	}

	/**
	 * Returns the properties for the template type.
	 *
	 * @return the template type properties.
	 */
	public TemplateProperties getSelectedTemplate() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			return model.getTemplate( index);
		} else {
			return model.getTemplate( 0);
		}
	}
	
	private void changeButtonPressed() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			TemplateProperties props = model.getTemplate( index);
			
			Vector gs = properties.getTemplateProperties();
			Vector names = new Vector();
	
			for ( int i = 0; i < gs.size(); i++) {
				String name = ((TemplateProperties)gs.elementAt( i)).getName();
				
				if ( !name.equals( props.getName())) {
					names.addElement( name);
				}
			}
			
			TemplatePropertiesDialog dialog = getPropertiesDialog();
			dialog.show( props.getName(), props.getURL(), names);
			
			if ( !dialog.isCancelled()) {
				// update list...
				props.setName( dialog.getName());
				props.setURL( dialog.getURL());
				model.updateTemplate( props);
			}
		}
	}
	
	private void newButtonPressed() {
		TemplateProperties props = null;
		
		props = new TemplateProperties();
		
		Vector gs = properties.getTemplateProperties();
		Vector names = new Vector();

		for ( int i = 0; i < gs.size(); i++) {
			String name = ((TemplateProperties)gs.elementAt( i)).getName();
			
			names.addElement( name);
		}

		TemplatePropertiesDialog dialog = getPropertiesDialog();
		dialog.show( "", null, names);
		
		if ( !dialog.isCancelled()) {
			// update list...
			props.setName( dialog.getName());
			props.setURL( dialog.getURL());
			model.addTemplate( props);
		}
	}

	private void deleteButtonPressed() {
		/*int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			TemplateProperties props = model.getTemplate( index);
			int result = MessageHandler.showConfirm( "Are you sure you want to delete "+props.getName()+"?");
			
			if ( result == JOptionPane.YES_OPTION) {
				// update list...
				model.removeTemplate( props);
			}
		}*/
		
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    int[] selectedIndexes = list.getSelectedIndices();
	    Vector selectedObjects = new Vector();
	    
	    for(int cnt=0;cnt<selectedIndexes.length;++cnt) {
	        selectedObjects.add(model.getTemplate(selectedIndexes[cnt]));
	    }
	    
	    //only continue with the confirm all if the array has 2 or more items
	    if(selectedObjects.size()>0) {
	        
	        //create the variable that the user can set if they just want to delete all
	        boolean deleteAll = false;
	        
	        //loop through the selected objects
	        for(int cnt=0;cnt<selectedObjects.size();++cnt) {
	            
	            //if the deleteAll flag is false, then ask the user about each individual object
	            if( deleteAll == false) {
	            
		            //create the message for the user
		            String message = "Are you sure you want to delete:\n ";
		            message += ((TemplateProperties)selectedObjects.get(cnt)).getName();
		            
		            //ask the question
		            int questionResult = -1;
		            if(selectedObjects.size()>1) {
                        questionResult = MessageHandler.showConfirmYesNoAll(parent,message);
                    }
                    else {
                        questionResult = MessageHandler.showConfirm(parent,message);
                    }
		            		            
		            //if the user answered All, don't do anything for now and delete them all later
		            if(questionResult==MessageHandler.CONFIRM_ALL_OPTION) {
	                    model.removeTemplate((TemplateProperties) selectedObjects.get(cnt));
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    model.removeTemplate((TemplateProperties) selectedObjects.get(cnt));
					}
		            
	            } else {
                    model.removeTemplate((TemplateProperties) selectedObjects.get(cnt));
	            }
	        } //end for loop
	    } //end if(selectedObjects.length>1) {
	    
	    	    
	    
	    //*******************END new code*********************************
	    	
	}

	private void importButtonPressed() {
		JFileChooser chooser = getImportChooser();
		int value = chooser.showOpenDialog( getParent());
		dir = chooser.getCurrentDirectory();

		if ( value == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			URL url = null;

			try {
				url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
			} catch ( MalformedURLException x) {
				x.printStackTrace(); // should never happen
			}
			
			try {
				importDocument( url);
			} catch (Exception e) {
				MessageHandler.showError( "Could not import Template.", "Template Error");
			}
		}
	}
	
	private void importDocument( URL url) throws IOException, SAXParseException {
		ExchangerDocument document = new ExchangerDocument( url);
		document.loadWithoutSubstitution();
		
		XElement root = document.getRoot();

		if ( root.getName().equals( "template")) {
			importTemplate( url, root);
		} else if ( root.getName().equals( "templates")) {
			importTemplates( url, root);
		} else {
			MessageHandler.showError( "Could not import Template.", "Template Error");
		}
	}
	
	private void importTemplate( URL url, XElement element) {
		if (DEBUG) System.out.println( "TemplateManagementDialog.importTemplate( "+url.toString()+", "+element.getName()+")");
		
		String name = element.getAttribute( "name");

		if ( name != null) {
			Vector templates = properties.getTemplateProperties();
			
			for ( int i = 0; i < templates.size(); i++) {
				TemplateProperties props = (TemplateProperties)templates.elementAt( i);
				String name2 = props.getName();
				
				if ( name.equals( name2)) {
					// Error duplicate name, overwrite existing?
					int value = MessageHandler.showConfirm( "A Template with this name \""+name+"\" already Exists.\n"+
															"Do you want to overwrite the existing Template?");
					if ( value == JOptionPane.YES_OPTION) {
						props.importTemplate( url, element);
						model.updateTemplate( props);
					} 

					return;
				}
			}
			
			TemplateProperties props = new TemplateProperties( url, element);
			model.addTemplate( props);
		} else {
			// Error name should have a value!
			MessageHandler.showError( "Template does not have a name.", "Template Error");
		}
	}
	
	private void importTemplates( URL url, XElement element) {
		XElement[] templates = element.getElements( "template");
		
		if ( templates != null) {
			for ( int i = 0; i < templates.length; i++) {
				importTemplate( url, templates[i]);
			}
		} else {
			MessageHandler.showError( "Could not find Templates in Document.", "Template Error");
		}
	}

	private File getSelectedChooserFile() {
		if ( dir != null) {
			return dir;
		}

		ExchangerDocument doc = parent.getDocument();
		URL url = null;
		
		if ( doc != null) {
			url = doc.getURL();
		}

		if ( url != null && url.getProtocol().equals( "file")) {
			return new File( url.getFile());
		}
		
		return FileUtilities.getLastOpenedFile();
	}

	private JFileChooser getImportChooser() {
		if ( importChooser == null) {
			importChooser = FileUtilities.createFileChooser();
			importChooser.addChoosableFileFilter( new DefaultFileFilter( "template templates", "Exchanger Template(s) Document"));
		} 
		
		importChooser.setCurrentDirectory( getSelectedChooserFile());
		importChooser.rescanCurrentDirectory();
		
		return importChooser;
	}

	private JFileChooser getExportChooser() {
		if ( exportChooser == null) {
			exportChooser = FileUtilities.createFileChooser();
			exportChooser.addChoosableFileFilter( new DefaultFileFilter( "template", "Exchanger Template Document"));
		} 
		
		exportChooser.setCurrentDirectory( getSelectedChooserFile());
		exportChooser.rescanCurrentDirectory();
		
		return exportChooser;
	}

	private void exportButtonPressed() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			JFileChooser exportChooser = getExportChooser();
			TemplateProperties props = model.getTemplate( index);
			exportChooser.setSelectedFile( new File( exportChooser.getSelectedFile(), props.getName()+".template"));
			dir = exportChooser.getCurrentDirectory();

			File file = FileUtilities.selectOutputFile( exportChooser, "template");
			
			if ( file != null) {
				URL url = null;

				try {
					url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				} catch ( MalformedURLException x) {
					x.printStackTrace(); // should never happen
				}
				
				try {
					exportDocument( url, props);
				} catch (Exception e) {
					MessageHandler.showError( "Could not export Template.", "Template Error");
				}
			}
		}
	}
	
	private void exportDocument( URL url, TemplateProperties props) throws IOException, SAXParseException {
		ExchangerDocument document = new ExchangerDocument( url, props.exportTemplate( url));
		XMLUtilities.write( document.getDocument(), document.getURL());
	}

	class TemplateListModel extends AbstractListModel {
		Vector elements = null;
		
		public TemplateListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				TemplateProperties element = (TemplateProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getName().compareToIgnoreCase( ((TemplateProperties)elements.elementAt(j)).getName()) <= 0) {
						index = j;
					}
				}
				
				if ( index != -1) {
					elements.insertElementAt( element, index);
				} else {
					elements.addElement( element);
				}
			}
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public void addTemplate( TemplateProperties props) {
			properties.addTemplateProperties( props);
			elements.addElement( props);
			
			fireIntervalAdded( this, elements.size()-1, elements.size()-1);
		}

		public void updateTemplate( TemplateProperties props) {
			int index = elements.indexOf( props);

			fireContentsChanged( this, index, index);
		}

		public void removeTemplate( TemplateProperties props) {
			properties.removeTemplateProperties( props);
			int index = elements.indexOf( props);
			elements.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}

		public Object getElementAt( int i) {
			return ((TemplateProperties)elements.elementAt( i)).getName();
		}

		public TemplateProperties getTemplate( int i) {
			return (TemplateProperties)elements.elementAt( i);
		}
	}
} 
