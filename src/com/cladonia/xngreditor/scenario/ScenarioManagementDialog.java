/*
 * $Id: ScenarioManagementDialog.java,v 1.15 2004/11/04 19:21:50 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.scenario;

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
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
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
 * The scenario management selection dialog.
 *
 * @version	$Revision: 1.15 $, $Date: 2004/11/04 19:21:50 $
 * @author Dogsbay
 */
public class ScenarioManagementDialog extends XngrDialogHeader {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 300, 300  + XngrDialogHeader.HEADER_HEIGHT);

	private ConfigurationProperties properties = null;
	
	private ScenarioPropertiesDialog scenarioPropertiesDialog = null;
//	private ScenarioPropertiesDialog newScenarioPropertiesDialog = null;
	
	private JFrame parent = null;
	
	private File dir				 	= null;
	private JFileChooser importChooser	= null;
	private JFileChooser exportChooser	= null;

	private JList list	= null;

	private ScenarioListModel model = null;

	private JButton newButton = null;
	private JButton copyButton = null;
	private JButton changeButton = null;
	private JButton deleteButton = null;

	private JButton exportButton = null;
	private JButton importButton = null;

	private JButton closeButton = null;

	/**
	 * The dialog that displays the list of scenario properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public ScenarioManagementDialog( JFrame parent, ConfigurationProperties props) {
		super( parent, false);
		
		this.parent = parent;
		this.properties = props;
		
		setResizable( false);
		
		setTitle( "Manage Scenarios");
		setDialogDescription( "Edit, Add and Delete scenarios.");
		
		list = new JList();
		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				changeButtonPressed();
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
			

		closeButton = new JButton("Close");
		closeButton.setMnemonic('C');
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				if ( !getScenarioPropertiesDialog().isVisible()) {
					properties.save();
					setVisible(false);
				}
			}
		});
		
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				if ( !getScenarioPropertiesDialog().isVisible()) {
					properties.save();
					setVisible(false);
				}
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( closeButton);
				
		getRootPane().setDefaultButton( closeButton);
		
		JPanel mainPanel = new JPanel( new BorderLayout());

		JPanel listPanel = new JPanel( new BorderLayout());
//		listPanel.add( text, BorderLayout.NORTH);

		listPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Scenarios"),
									new EmptyBorder( 0, 5, 5, 5)));

		JScrollPane scrollPane = new JScrollPane(	list,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		listPanel.add( scrollPane, BorderLayout.CENTER);
		
		JPanel listButtonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 0, 0));

		changeButton = new JButton("Edit");
		changeButton.setMnemonic('E');
		changeButton.setFont( changeButton.getFont().deriveFont( Font.PLAIN));
		changeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				changeButtonPressed();
			}
		});
		listButtonPanel.add( changeButton);

		newButton = new JButton("New");
		newButton.setMnemonic('N');
		newButton.setFont( newButton.getFont().deriveFont( Font.PLAIN));
		newButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				newButtonPressed();
			}
		});

		copyButton = new JButton("Copy");
		copyButton.setMnemonic('N');
		copyButton.setFont( copyButton.getFont().deriveFont( Font.PLAIN));
		copyButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				copyButtonPressed();
			}
		});
//		listButtonPanel.add( newButton);
		
		deleteButton = new JButton("Delete");
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
		exportButton.setMnemonic('E');
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
		JPanel leftButtonPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0 ,0));
		leftButtonPanel.add( newButton);
		leftButtonPanel.add( copyButton);
		southButtonPanel.add( leftButtonPanel, BorderLayout.WEST);
		southButtonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));

		listPanel.add( portButtonPanel, BorderLayout.NORTH);
		listPanel.add( southButtonPanel, BorderLayout.SOUTH);

		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		mainPanel.add( listPanel, BorderLayout.CENTER);
		mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		this.setContentPane( mainPanel); 
		
		pack();

		setSize( new Dimension( Math.max( SIZE.width, getSize().width), Math.max( SIZE.height, getSize().height)));

		setLocationRelativeTo( parent);
	}
	
	public void show() {
		model = new ScenarioListModel( properties.getScenarioProperties());

		list.setModel( model);
		
		list.setSelectedIndex( 0);

		//super.setVisible(true);
		super.show();
	}

	private void changeButtonPressed() {
		if ( !getScenarioPropertiesDialog().isVisible()) {
			ScenarioPropertiesDialog dialog = getScenarioPropertiesDialog();
			
			ScenarioProperties scenario = getSelectedScenario();
			
			if ( scenario != null) {
				Vector scs = properties.getScenarioProperties();
				Vector names = new Vector();
		
				for ( int i = 0; i < scs.size(); i++) {
					String name = ((ScenarioProperties)scs.elementAt( i)).getName();
	
					if ( !name.equals( scenario.getName())) {
						names.addElement( name);
					}
				}
	
				ExchangerDocument document = null;
				
				if ( parent instanceof ExchangerEditor) {
					document = ((ExchangerEditor)parent).getDocument();
				}
	
				dialog.show( scenario, document, names, 
					new ScenarioPropertiesDialogListener() {
						public void scenarioUpdated( ScenarioProperties scenario) {
							model.updateScenario( scenario);
						}
					});
			}
		}
	}
	
	private void newButtonPressed() {
		if ( !getScenarioPropertiesDialog().isVisible()) {
			ScenarioPropertiesDialog dialog = getScenarioPropertiesDialog();
			ScenarioProperties scenario = new ScenarioProperties();
			
			if ( scenario != null) {
				final Vector scs = properties.getScenarioProperties();
				Vector names = new Vector();
		
				for ( int i = 0; i < scs.size(); i++) {
					String name = ((ScenarioProperties)scs.elementAt( i)).getName();
					names.addElement( name);
				}
	
				ExchangerDocument document = null;
				
				if ( parent instanceof ExchangerEditor) {
					document = ((ExchangerEditor)parent).getDocument();
				}
	
				dialog.show( scenario, document, names,
						new ScenarioPropertiesDialogListener() {
							public void scenarioUpdated( ScenarioProperties scenario) {
								boolean scenarioUpdated = false;
								
								for ( int i = 0; i < scs.size(); i++) {
									String name = ((ScenarioProperties)scs.elementAt( i)).getName();
					
									if ( name.equals( scenario.getName())) {
										ScenarioProperties oldScenario = (ScenarioProperties)scs.elementAt( i);
										oldScenario.update( scenario);
										model.updateScenario( oldScenario);
										scenarioUpdated = true;
										break;
									}
								}
								
								if ( !scenarioUpdated) {
									model.addScenario( scenario);
								}
							}
						});
			}
		}
	}

	private void copyButtonPressed() {
		if ( !getScenarioPropertiesDialog().isVisible()) {
			ScenarioPropertiesDialog dialog = getScenarioPropertiesDialog();
			ScenarioProperties selected = getSelectedScenario();
			ScenarioProperties scenario = new ScenarioProperties();
			
			if ( selected != null) {
				scenario.setBrowserEnabled( selected.isBrowserEnabled());
				scenario.setBrowserURL( selected.getBrowserURL());
				scenario.setFOPEnabled( selected.isFOPEnabled());
				scenario.setFOPOutputFile( selected.getFOPOutputFile());
				scenario.setFOPOutputType( selected.getFOPOutputType());
				scenario.setInputFile( selected.getInputURL());
				scenario.setInputType( selected.getInputType());
				scenario.setOutputFile( selected.getOutputFile());
				scenario.setOutputType( selected.getOutputType());
				scenario.setProcessor( selected.getProcessor());
				scenario.setXQueryType( selected.getXQueryType());
				scenario.setXQueryEnabled( selected.isXQueryEnabled());
				scenario.setXQueryURL( selected.getXQueryURL());
				scenario.setXSLEnabled( selected.isXSLEnabled());
				scenario.setXSLType( selected.getXSLType());
				scenario.setXSLURL( selected.getXSLURL());
				
				Vector parameters = selected.getParameters();
				
				if ( parameters != null) {
					for ( int i = 0; i < parameters.size(); i++) {
						ParameterProperties parameter = (ParameterProperties)parameters.elementAt(i);
						scenario.addParameter( new ParameterProperties( parameter));
					}
				}
			}
			
			final Vector scs = properties.getScenarioProperties();
			Vector names = new Vector();
	
			for ( int i = 0; i < scs.size(); i++) {
				String name = ((ScenarioProperties)scs.elementAt( i)).getName();
				names.addElement( name);
			}
	
			ExchangerDocument document = null;
			
			if ( parent instanceof ExchangerEditor) {
				document = ((ExchangerEditor)parent).getDocument();
			}
	
			dialog.show( scenario, document, names,
					new ScenarioPropertiesDialogListener() {
						public void scenarioUpdated( ScenarioProperties scenario) {
							boolean scenarioUpdated = false;
							
							for ( int i = 0; i < scs.size(); i++) {
								String name = ((ScenarioProperties)scs.elementAt( i)).getName();
	
								if ( name.equals( scenario.getName())) {
									ScenarioProperties oldScenario = (ScenarioProperties)scs.elementAt( i);
									oldScenario.update( scenario);
									model.updateScenario( oldScenario);
									scenarioUpdated = true;
									break;
								}
							}
							
							if ( !scenarioUpdated) {
								model.addScenario( scenario);
							}
						}
					});
		}
	}

	private void deleteButtonPressed() {
		if ( !getScenarioPropertiesDialog().isVisible()) {
		
//		  *******************START new code*********************************
		    
		    //get the selected objects
		    int[] selectedIndexes = list.getSelectedIndices();
		    Vector selectedObjects = new Vector();
		    
		    for(int cnt=0;cnt<selectedIndexes.length;++cnt) {
		        selectedObjects.add(model.getScenario(selectedIndexes[cnt]));
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
			            message += ((ScenarioProperties)selectedObjects.get(cnt)).getName();
			            
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
		                    model.removeScenario((ScenarioProperties) selectedObjects.get(cnt));
		                	deleteAll=true;
		                } 
		                //user choose to delete this object, remove it from the list
		                else if(questionResult==JOptionPane.YES_OPTION) {
		                    model.removeScenario((ScenarioProperties) selectedObjects.get(cnt));
						}
			            
		            } else {
	                    model.removeScenario((ScenarioProperties) selectedObjects.get(cnt));
		            }
		        } //end for loop
		    } //end if(selectedObjects.length>1) {
		    
		    	    
		    
		    //*******************END new code*********************************
		    
			
		}
	}

	private ScenarioProperties getSelectedScenario() {
		ScenarioProperties scenario = null;
		int index = list.getSelectedIndex();
		
		if ( index != -1 && index < model.getSize()) {
			scenario = model.getScenario( index);
		}
		
		return scenario;
	}

//	private ScenarioPropertiesDialog getNewScenarioPropertiesDialog() {
//		if ( newScenarioPropertiesDialog == null) {
//			newScenarioPropertiesDialog = new ScenarioPropertiesDialog( parent, true);
//		} 
//		
//		return newScenarioPropertiesDialog;
//	}

	private ScenarioPropertiesDialog getScenarioPropertiesDialog() {
		if ( scenarioPropertiesDialog == null) {
			scenarioPropertiesDialog = new ScenarioPropertiesDialog( parent);
		} 
		
		return scenarioPropertiesDialog;
	}


	private void importButtonPressed() {
		if ( !getScenarioPropertiesDialog().isVisible()) {
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
					MessageHandler.showError( "Could not import Scenario.", "Scenario Error");
				}
			}
		}
	}
	
	private void importDocument( URL url) throws IOException, SAXParseException {
		ExchangerDocument document = new ExchangerDocument( url);
		document.loadWithoutSubstitution();
		
		XElement root = document.getRoot();

		if ( root.getName().equals( "scenario")) {
			importScenario( url, root);
		} else if ( root.getName().equals( "scenarios")) {
			importScenarios( url, root);
		} else {
			MessageHandler.showError( "Could not import Scenario.", "Scenario Error");
		}
	}
	
	private void importScenario( URL url, XElement element) {
		if (DEBUG) System.out.println( "scenarioManagementDialog.importType( "+url.toString()+", "+element.getName()+")");
		
		String name = element.getAttribute( "name");

		if ( name != null) {
			Vector scenarios = properties.getScenarioProperties();
			
			for ( int i = 0; i < scenarios.size(); i++) {
				ScenarioProperties props = (ScenarioProperties)scenarios.elementAt( i);
				String name2 = props.getName();
				
				if ( name.equals( name2)) {
					// Error duplicate name, overwrite existing?
					int value = MessageHandler.showConfirm( "A Scenario with this name \""+name+"\" already Exists.\n"+
															"Do you want to overwrite the existing Scenario?");
					if ( value == JOptionPane.YES_OPTION) {
						props.importScenario( url, element);
						model.updateScenario( props);
					} 

					return;
				}
			}
			
			ScenarioProperties props = new ScenarioProperties( url, element);
			model.addScenario( props);
		} else {
			// Error name should have a value!
			MessageHandler.showError( "Scenario does not have a name.", "Scenario Error");
		}
	}
	
	private void importScenarios( URL url, XElement element) {
		XElement[] scenarios = element.getElements( "scenario");
		
		if ( scenarios != null) {
			for ( int i = 0; i < scenarios.length; i++) {
				importScenario( url, scenarios[i]);
			}
		} else {
			MessageHandler.showError( "Could not find Scenarios in Document.", "Scenario Error");
		}
	}

	private File getSelectedChooserFile() {
		ExchangerDocument doc = null;
			
		if ( dir != null) {
			return dir;
		}

		if ( parent instanceof ExchangerEditor) {
			doc = ((ExchangerEditor)parent).getDocument();
		}

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
			importChooser.addChoosableFileFilter( new DefaultFileFilter( "scenario scenarios", "Exchanger Scenario(s) Document"));
		} 
		
		importChooser.setCurrentDirectory( getSelectedChooserFile());
		importChooser.rescanCurrentDirectory();
		
		return importChooser;
	}

	private JFileChooser getExportChooser() {
		if ( exportChooser == null) {
			exportChooser = FileUtilities.createFileChooser();
			exportChooser.addChoosableFileFilter( new DefaultFileFilter( "scenario", "Exchanger Scenario Document"));
		} 
		
		exportChooser.setCurrentDirectory( getSelectedChooserFile());
		exportChooser.rescanCurrentDirectory();
		
		return exportChooser;
	}

	private void exportButtonPressed() {
		if ( !getScenarioPropertiesDialog().isVisible()) {
			int index = list.getSelectedIndex();
			
			if ( index != -1 && model.getSize() > index) {
				JFileChooser chooser = getExportChooser();
				ScenarioProperties props = model.getScenario( index);
				chooser.setSelectedFile( new File( chooser.getSelectedFile(), props.getName()+".scenario"));
				File file = FileUtilities.selectOutputFile( chooser, "scenario");
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
						MessageHandler.showError( "Could not export Scenario.", "Scenario Error");
					}
				}
			}
		}
	}
	
	private void exportDocument( URL url, ScenarioProperties props) throws IOException, SAXParseException {
		ExchangerDocument document = new ExchangerDocument( url, props.exportScenario( url));
		XMLUtilities.write( document.getDocument(), document.getURL());
	}
	
	class ScenarioListModel extends AbstractListModel {
		Vector elements = null;
		
		public ScenarioListModel( Vector list) {
			elements = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				ScenarioProperties element = (ScenarioProperties)list.elementAt(i);

				// Find out where to insert the element...
				int index = -1;

				for ( int j = 0; j < elements.size() && index == -1; j++) {
					// Compare alphabeticaly
					if ( element.getName().compareToIgnoreCase( ((ScenarioProperties)elements.elementAt(j)).getName()) <= 0) {
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

		public void addScenario( ScenarioProperties props) {
			properties.addScenarioProperties( props);
			elements.addElement( props);
			
			fireIntervalAdded( this, elements.size()-1, elements.size()-1);
		}

		public void updateScenario( ScenarioProperties props) {
			int index = elements.indexOf( props);

			fireContentsChanged( this, index, index);
		}

		public void removeScenario( ScenarioProperties props) {
			properties.removeScenarioProperties( props);
			int index = elements.indexOf( props);
			elements.removeElement( props);

			fireIntervalRemoved( this, index, index);
		}

//		public void setDefault( ScenarioProperties scenario) {
//			defaultScenario = scenario; 
//		}
//
//		public boolean isDefault( ScenarioProperties scenario) {
//			boolean result = false;
//			
//			if ( defaultScenario != null && scenario != null) {
//				result = defaultScenario.getId().equals( scenario.getId()); 
//			}
//
//			return result; 
//		}

		public Object getElementAt( int i) {
			return ((ScenarioProperties)elements.elementAt( i)).getName();
		}

		public ScenarioProperties getScenario( int i) {
			return (ScenarioProperties)elements.elementAt( i);
		}
	}
} 
