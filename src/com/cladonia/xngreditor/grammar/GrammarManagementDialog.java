/*
 * $Id: GrammarManagementDialog.java,v 1.14 2004/10/29 15:01:31 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.grammar;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.DefaultFileFilter;
import org.bounce.event.DoubleClickListener;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XDocumentFactory;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.XngrDialogHeader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The grammar properties management dialog.
 *
 * @version	$Revision: 1.14 $, $Date: 2004/10/29 15:01:31 $
 * @author Dogsbay
 */
public class GrammarManagementDialog extends XngrDialogHeader {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 300, 300 + XngrDialogHeader.HEADER_HEIGHT);

	private ConfigurationProperties properties = null;
	private GrammarPropertiesDialog propertiesDialog = null;
	
	private ExchangerEditor parent = null;

	private JFileChooser importChooser	= null;
	private JFileChooser exportChooser	= null;
	private File dir	 				= null;

	private JList list	= null;
//	private QLabel text = null;

	private GrammarListModel model = null;

	private JButton changeButton = null;
	private JButton newButton = null;
	private JButton deleteButton = null;

	private JButton exportButton = null;
	private JButton importButton = null;

	private JButton closeButton = null;

	/**
	 * The dialog that displays the list of grammar properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public GrammarManagementDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, true);
		
		this.parent = parent;
		this.properties = props;
		
		setResizable( false);
		
		setTitle( "Manage XML Types");
		setDialogDescription( "Edit, Add and Delete XML Types");
		
		list = new JList();
		list.setCellRenderer( new GrammarListCellRenderer());
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

		list.setSelectionMode( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		changeButton = new JButton( "Edit");
		changeButton.setMnemonic('E');
		changeButton.setFont( changeButton.getFont().deriveFont( Font.PLAIN));
		changeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				changeButtonPressed();
			}
		});
		
		
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

		JPanel grammarButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));
		grammarButtonPanel.add( changeButton);
//		grammarButtonPanel.add( newButton);
		grammarButtonPanel.add( deleteButton);
		
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
		
//		JPanel northButtonPanel = new JPanel( new BorderLayout());
//		northButtonPanel.add( portButtonPanel, BorderLayout.EAST);
//		northButtonPanel.add( newButton, BorderLayout.WEST);
//		northButtonPanel.setBorder( new EmptyBorder( 2, 0, 5, 0));

		JPanel southButtonPanel = new JPanel( new BorderLayout());
		southButtonPanel.add( grammarButtonPanel, BorderLayout.EAST);
		southButtonPanel.add( newButton, BorderLayout.WEST);
		southButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));

		JPanel basePanel = new JPanel( new BorderLayout());
		basePanel.add( scrollPane, BorderLayout.CENTER);
		basePanel.add( southButtonPanel, BorderLayout.SOUTH);

		JPanel listPanel = new JPanel( new BorderLayout());
		listPanel.setBorder( new CompoundBorder( 
								new TitledBorder( "XML Types"),
								new EmptyBorder( 0, 5, 5, 5)));
		listPanel.add( basePanel, BorderLayout.CENTER);
		listPanel.add( portButtonPanel, BorderLayout.NORTH);

		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.add( listPanel, BorderLayout.CENTER);
		mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		this.setContentPane( mainPanel); 

		setSize( SIZE);
		
		setLocationRelativeTo( parent);
	}
	
	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void show() {
		setProperties( properties.getGrammarProperties());
		
		//super.setVisible(true);
		super.show();
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	private void setProperties( Vector properties) {
		model = new GrammarListModel( properties);
		list.setModel( model);
		
		list.setSelectedIndex( 0);
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void setSelectedGrammar( GrammarProperties grammar) {
		for ( int i = 0; i < model.getSize(); i++) {
			GrammarProperties prop = model.getType( i);
			
			if ( prop == grammar) {
				list.setSelectedIndex( i);
				return;
			}
		}
	}

	/**
	 * Returns the properties for the grammar type.
	 *
	 * @return the grammar type properties.
	 */
	public GrammarProperties getSelectedType() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			return model.getType( index);
		} else {
			return model.getType( 0);
		}
	}
	
	private void changeButtonPressed() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			GrammarProperties props = model.getType( index);
			
			Vector gs = properties.getGrammarProperties();
			Vector names = new Vector();
	
			for ( int i = 0; i < gs.size(); i++) {
				String name = ((GrammarProperties)gs.elementAt( i)).getDescription();
				
				if ( !name.equals( props.getDescription())) {
					names.addElement( name);
				}
			}

			GrammarPropertiesDialog dialog = FileUtilities.getGrammarPropertiesDialog( "OK", false);
			dialog.show( props, false, names);
			
			if ( !dialog.isCancelled()) {
				// update list...
				model.updateGrammar( props);
				parent.updateGrammar( props);
			}
		}
	}
	
	private void newButtonPressed() {
		GrammarProperties props = null;
		
		ExchangerDocument doc = parent.getDocument();
		if ( doc != null) {
			props = new GrammarProperties( properties, doc);
		} else {
			props = new GrammarProperties( properties);
		}
		
		Vector gs = properties.getGrammarProperties();
		Vector names = new Vector();

		for ( int i = 0; i < gs.size(); i++) {
			String name = ((GrammarProperties)gs.elementAt( i)).getDescription();
			
			names.addElement( name);
		}

		GrammarPropertiesDialog dialog = FileUtilities.getGrammarPropertiesDialog( "Create", true);
		dialog.show( props, false, names);
		
		if ( !dialog.isCancelled()) {
			boolean grammarUpdated = false;
			
			for ( int i = 0; i < gs.size(); i++) {
				String name = ((GrammarProperties)gs.elementAt( i)).getDescription();

				if ( name.equals( props.getDescription())) {
					GrammarProperties oldGrammar = (GrammarProperties)gs.elementAt( i);
					oldGrammar.update( props);
					model.updateGrammar( oldGrammar);
					grammarUpdated = true;
					break;
				}
			}
			
			if ( !grammarUpdated) {
				// update list...
				model.addGrammar( props);
			}
		}
	}

	private void deleteButtonPressed() {
		/*int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			GrammarProperties props = model.getType( index);
			int result = MessageHandler.showConfirm( "Are you sure you want to delete "+props.getDescription()+"?");
			
			if ( result == JOptionPane.YES_OPTION) {
				// update list...
				model.removeGrammar( props);
			}
		}*/
		
//	  *******************START new code*********************************
	    
	    //get the selected objects
	    Object[] selectedObjects = list.getSelectedValues();
	    
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
		            message += ((GrammarProperties)selectedObjects[cnt]).getDescription();
		            
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
	                    model.removeGrammar( (GrammarProperties)selectedObjects[cnt]);
	                	deleteAll=true;
	                } 
	                //user choose to delete this object, remove it from the list
	                else if(questionResult==JOptionPane.YES_OPTION) {
	                    model.removeGrammar( (GrammarProperties)selectedObjects[cnt]);
					}
		            
	            } else {
                    model.removeGrammar( (GrammarProperties)selectedObjects[cnt]);
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
				MessageHandler.showError( "Could not import XML Type.", "XML Type Error");
			}
		}
	}
	
	private void importDocument( URL url) throws IOException, SAXParseException {
		ExchangerDocument document = new ExchangerDocument( url);
		document.loadWithoutSubstitution();
		
		XElement root = document.getRoot();

		if ( root.getName().equals( "type")) {
			importType( url, root);
		} else if ( root.getName().equals( "types")) {
			importTypes( url, root);
		} else {
			MessageHandler.showError( "Could not import XML Type.", "XML Type Error");
		}
	}
	
	private void importType( URL url, XElement element) {
		if (DEBUG) System.out.println( "GrammarManagementDialog.importType( "+url.toString()+", "+element.getName()+")");
		
		String name = element.getAttribute( "name");
		
		if ( name != null) {
			Vector gs = properties.getGrammarProperties();
			
			for ( int i = 0; i < gs.size(); i++) {
				GrammarProperties props = (GrammarProperties)gs.elementAt( i);
				String name2 = props.getDescription();
				
				if ( name.equals( name2)) {
					// Error duplicate name, overwrite existing?
					int value = MessageHandler.showConfirm( "A XML Type with this name \""+name+"\" already Exists.\n"+
															"Do you want to overwrite the existing XML Type?");
					if ( value == JOptionPane.YES_OPTION) {
						props.importType( url, element);
						
						if ( !StringUtilities.isEmpty( props.getNamespacePrefix()) && !StringUtilities.isEmpty( props.getNamespace())) {
							properties.addPrefixNamespaceMapping( props.getNamespacePrefix(), props.getNamespace());
						}

						Vector namespaces = props.getNamespaces();
						for ( int j = 0; j < namespaces.size(); j++) {
							NamespaceProperties namespace = (NamespaceProperties)namespaces.elementAt(j);

							if ( !StringUtilities.isEmpty( namespace.getPrefix()) && !StringUtilities.isEmpty( namespace.getURI())) {
								properties.addPrefixNamespaceMapping( namespace.getPrefix(), namespace.getURI());
							}
						}

						XDocumentFactory.getInstance().setXPathNamespaceURIs( properties.getPrefixNamespaceMappings());

						model.updateGrammar( props);
					} 

					return;
				}
			}
			
			GrammarProperties props = new GrammarProperties( properties, url, element);

			if ( !StringUtilities.isEmpty( props.getNamespacePrefix()) && !StringUtilities.isEmpty( props.getNamespace())) {
				properties.addPrefixNamespaceMapping( props.getNamespacePrefix(), props.getNamespace());
			}

			Vector namespaces = props.getNamespaces();
			for ( int j = 0; j < namespaces.size(); j++) {
				NamespaceProperties namespace = (NamespaceProperties)namespaces.elementAt(j);

				if ( !StringUtilities.isEmpty( namespace.getPrefix()) && !StringUtilities.isEmpty( namespace.getURI())) {
					properties.addPrefixNamespaceMapping( namespace.getPrefix(), namespace.getURI());
				}
			}

			XDocumentFactory.getInstance().setXPathNamespaceURIs( properties.getPrefixNamespaceMappings());

			model.addGrammar( props);
		} else {
			// Error name should have a value!
			MessageHandler.showError( "XML Type does not have a name.", "XML Type Error");
		}
	}
	
	private void importTypes( URL url, XElement element) {
		XElement[] types = element.getElements("type");
		
		if ( types != null) {
			for ( int i = 0; i < types.length; i++) {
				importType( url, types[i]);
			}
		} else {
			MessageHandler.showError( "Could not find Types in Document.", "XML Type Error");
		}
	}

	private File getSelectedChooserFile() {
		ExchangerDocument doc = parent.getDocument();
		URL url = null;
		
		if ( dir != null) {
			return dir;
		}

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
			importChooser.addChoosableFileFilter( new DefaultFileFilter( "type types", "Exchanger XML Type(s) Document"));
		} 
		
		importChooser.setCurrentDirectory( getSelectedChooserFile());
		importChooser.rescanCurrentDirectory();
		
		return importChooser;
	}

	private JFileChooser getExportChooser() {
		if ( exportChooser == null) {
			exportChooser = FileUtilities.createFileChooser();
			exportChooser.addChoosableFileFilter( new DefaultFileFilter( "type", "Exchanger XML Type Document"));
		} 
		
		exportChooser.setCurrentDirectory( getSelectedChooserFile());
		exportChooser.rescanCurrentDirectory();
		
		return exportChooser;
	}

	private void exportButtonPressed() {
		int index = list.getSelectedIndex();
		
		if ( index != -1 && model.getSize() > index) {
			GrammarProperties props = model.getType( index);
			JFileChooser chooser = getExportChooser();
			chooser.setSelectedFile( new File( chooser.getSelectedFile(), props.getDescription()+".type"));
			File file = FileUtilities.selectOutputFile( chooser, "type");
			
			dir = chooser.getCurrentDirectory();

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
					MessageHandler.showError( "Could not export XML Type.", "XML Type Error");
				}
			}
		}
	}
	
	private void exportDocument( URL url, GrammarProperties props) throws IOException, SAXParseException {
		ExchangerDocument document = new ExchangerDocument( url, props.exportType( url));
		XMLUtilities.write( document.getDocument(), document.getURL());
	}
	
	class GrammarListModel extends AbstractListModel {
		Vector elements = null;
		
		public GrammarListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				GrammarProperties element = (GrammarProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getDescription().compareToIgnoreCase( ((GrammarProperties)elements.elementAt(j)).getDescription()) <= 0) {
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

		public void addGrammar( GrammarProperties props) {
			properties.addGrammarProperties( props);
			elements.addElement( props);
			
			fireIntervalAdded( this, elements.size()-1, elements.size()-1);
		}

		public void updateGrammar( GrammarProperties props) {
			int index = elements.indexOf( props);

			fireContentsChanged( this, index, index);
		}

		public void removeGrammar( GrammarProperties props) {
			properties.removeGrammarProperties( props);
			int index = elements.indexOf( props);
			elements.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}

		public Object getElementAt( int i) {
			return (GrammarProperties)elements.elementAt( i);
		}

		public GrammarProperties getType( int i) {
			return (GrammarProperties)elements.elementAt( i);
		}
	}
	
	class GrammarListCellRenderer extends JLabel implements ListCellRenderer {
		protected EmptyBorder noFocusBorder;

		public GrammarListCellRenderer() {
			if ( noFocusBorder == null) {
			    noFocusBorder = new EmptyBorder( 1, 1, 1, 1);
			}
			
			setOpaque(true);
			setBorder( noFocusBorder);
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
		
			if ( value instanceof GrammarProperties) {
				GrammarProperties type = (GrammarProperties)value;

				setText( type.getDescription());
				setIcon( IconFactory.getIconForType( type));
			}

			if (selected) {
				setForeground ( list.getSelectionForeground());
				setBackground ( list.getSelectionBackground());
			} else {
				setForeground( list.getForeground());
				setBackground( list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
	}
} 
