/*
 * $Id: ExecuteScenarioDialog.java,v 1.10 2005/08/03 10:19:44 gmcgoldrick Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.transform;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.transform.TransformerException;

import org.apache.fop.apps.FOPException;
import org.bounce.FormLayout;
import org.xml.sax.SAXException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.StatusFrame;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.TextPreferences;
import com.cladonia.xngreditor.scenario.ScenarioProperties;

/**
 * The panel that shows the different outputs.
 *
 * @version	$Revision: 1.10 $, $Date: 2005/08/03 10:19:44 $
 * @author Dogsbay
 */
public class ExecuteScenarioDialog extends StatusFrame {
	private ConfigurationProperties properties = null;
	
	private LogPane logPane = null;
	private JPanel logTab = null;
	private JPanel logPanel = null;

	private JLabel scenarioNameLabel = null;
	private JLabel statusLabel = null;
	private JCheckBox hideOnCompletionBox = null;
	private JCheckBox showBrowserBox = null;
	private JButton logButton = null;
	
	private ScenarioProcessor processor = null;
	private Thread thread = null;
	private ScenarioTableModel scenarioModel = null;
	private boolean busy = false;
	
	private static final Dimension SIZE = new Dimension( 500, 210);
	private static final Dimension LOG_SIZE = new Dimension( 500, 400);

//    private XngrDialogHeader header;

	/**
	 * The constructor for the execute scenario dialog/frame.
	 */
	public ExecuteScenarioDialog( ConfigurationProperties props) {
		setTitle( "Execute Scenario");
		setIconImage( (XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExecuteScenario16.gif")).getImage());
	
		this.properties = props;
//		ScenarioUtilities.init( this, parent, properties);
		
		JPanel buttonPanel = new JPanel( new FormLayout( 5, 2));
		JButton stopButton = new JButton( "Stop");
		stopButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				if ( processor != null) {
					processor.stop();
					thread.stop();
					if ( scenarioModel.getState(0) == scenarioModel.STATUS_PROCESSING) {
						scenarioModel.setState( 0, scenarioModel.STATUS_ERROR);
					} else if ( scenarioModel.getState(1) == scenarioModel.STATUS_PROCESSING) {
						scenarioModel.setState( 1, scenarioModel.STATUS_ERROR);
					} else if ( scenarioModel.getState(2) == scenarioModel.STATUS_PROCESSING) {
						scenarioModel.setState( 2, scenarioModel.STATUS_ERROR);
					}
					stopLogging( "Transformation Interrupted!");
				}
			}
		});

		JButton hideButton = new JButton( "Hide");
		hideButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
//				setVisible(false);
				//setVisible(false);
				hide();
			}
		});

		logButton = new JButton( "<< Log");
		logButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				if ( logPanel.isVisible()) {
					logPanel.setVisible( false);
					properties.setShowExecuteScenarioDialogLog( false);
					logButton.setText( "Log >>");
					pack();
				} else {
					logPanel.setVisible( true);
					properties.setShowExecuteScenarioDialogLog( true);
					logButton.setText( "<< Log");
					setSize( new Dimension( getSize().width, LOG_SIZE.height));
				}
				doLayout();
				repaint();

				logTab.doLayout();
				logTab.revalidate();
				logTab.repaint();
			}
		});

		buttonPanel.add( stopButton, FormLayout.FULL_FILL);
		buttonPanel.add( hideButton, FormLayout.FULL_FILL);
		buttonPanel.add( logButton, FormLayout.FULL);
		buttonPanel.setBorder( new EmptyBorder( 10, 10, 5, 10));

		JPanel mainPanel = new JPanel( new BorderLayout());
		//getContentPane().add( mainPanel, BorderLayout.CENTER);
		
		scenarioModel = new ScenarioTableModel();
		
		JTable table = new JTable( scenarioModel);
		table.setRowMargin( 0);
		table.setIntercellSpacing( new Dimension( 0, 2));
		table.setRowHeight( 20);
		table.setShowGrid( false);

		table.getColumnModel().getColumn( 0).setPreferredWidth( 20);
		table.getColumnModel().getColumn( 1).setPreferredWidth( 1000);
		table.getColumnModel().getColumn( 1).setCellRenderer( new ScenarioTableCellRenderer());
		
		table.setCellSelectionEnabled( false);
		table.setRowSelectionAllowed( true);
		table.setColumnSelectionAllowed( false);
		table.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		
		table.setFocusable( false);

		JScrollPane scroller = new JScrollPane(	table,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setPreferredSize( new Dimension( 380, 80));
		
		scroller.getViewport().setBackground( table.getBackground());
		
		JPanel tablePanel = new JPanel( new BorderLayout());
		tablePanel.setBorder( new EmptyBorder( 0, 0, 5, 10));
		tablePanel.add( scroller, BorderLayout.CENTER);

		
		JPanel topPanel = new JPanel( new BorderLayout());

		JPanel scenarioNamePanel = new JPanel( new FormLayout( 5, 2));
		scenarioNamePanel.add( new JLabel( "Scenario:"), FormLayout.LEFT);
		scenarioNameLabel = new JLabel();
		scenarioNamePanel.add( scenarioNameLabel, FormLayout.RIGHT_FILL);
		scenarioNamePanel.setBorder( new EmptyBorder( 0, 5, 10, 5));
		
		JPanel browserPanel = new JPanel( new BorderLayout());
		showBrowserBox = new JCheckBox( "Show Output in Browser");
		browserPanel.add( showBrowserBox, BorderLayout.WEST);
		browserPanel.setBorder( new EmptyBorder( 0, 5, 5, 5));

		topPanel.add( scenarioNamePanel, BorderLayout.NORTH);
		topPanel.add( tablePanel, BorderLayout.CENTER);
		topPanel.add( buttonPanel, BorderLayout.EAST);
		topPanel.add( browserPanel, BorderLayout.SOUTH);

		mainPanel.add( topPanel, BorderLayout.NORTH);
		mainPanel.setBorder( new EmptyBorder( 10, 10, 10, 10));

		mainPanel.add( createLogTab(), BorderLayout.CENTER);

		
		
		JPanel main = new JPanel(new BorderLayout());
//		main.add(this.buildHeader(this),BorderLayout.NORTH);
		main.add(mainPanel,BorderLayout.CENTER);
		getContentPane().add( main, BorderLayout.CENTER);
		//pack();
		
		if ( properties.isShowExecuteScenarioDialogLog()) {
			logPanel.setVisible( true);
			logButton.setText( "<< Log");
			pack();
			setSize( new Dimension( getSize().width, LOG_SIZE.height));
			
		} else {
			logPanel.setVisible( false);
			logButton.setText( "Log >>");
			pack();
		}
		
		
		
//		doLayout();
//		repaint();
//
//		logTab.doLayout();
//		logTab.revalidate();
//		logTab.repaint();
	}
	
	

	public void startLogging( String text) {
		logPane.startLogging( text);
	}

	public void stopLogging( String text) {
		logPane.stopLogging( text);
	}

	public void updatePreferences() {
		logPane.setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
		logPane.setTabSize( TextPreferences.getTabSize());
	}

	private JPanel createLogTab() {
		logTab = new JPanel( new BorderLayout());

		logPanel = new JPanel( new BorderLayout());

		logPane = new LogPane();
		JScrollPane scroller = new JScrollPane( logPane);
		JLabel logLabel = new JLabel( "Log:");
		logLabel.setBorder( new EmptyBorder( 0, 5, 5, 0));
		logPanel.add( logLabel, BorderLayout.NORTH);
		logPanel.add( scroller, BorderLayout.CENTER);
		
		logTab.add( logPanel, BorderLayout.CENTER);

		statusLabel = new JLabel();
		hideOnCompletionBox = new JCheckBox( "Hide when Complete");
		hideOnCompletionBox.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent event) {
				properties.setHideExecuteScenarioDialogWhenComplete( hideOnCompletionBox.isSelected());
			}
		});
		hideOnCompletionBox.setSelected( properties.isHideExecuteScenarioDialogWhenComplete());

		JPanel statusPanel = new JPanel( new BorderLayout());
		statusPanel.add( statusLabel, BorderLayout.CENTER);
		statusPanel.add( hideOnCompletionBox, BorderLayout.EAST);
		statusPanel.setBorder( new EmptyBorder( 8, 5, 0, 0));
		
		logTab.add( statusPanel, BorderLayout.SOUTH);
				
		return logTab;
	}

	public void setStatus( final String status) {
//		System.out.println( "setStatus( "+status+")");

		if ( SwingUtilities.isEventDispatchThread()) {
			statusLabel.setText( status);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					statusLabel.setText( status);
				}
			});
		}
	}
	
	public void execute( final ScenarioProperties scenario, final ExchangerDocument document) {
		if ( !busy) {
			String name = scenario.getName();
			
			if ( name == null || name.trim().length() == 0) {
				if ( scenario.isXSLEnabled()) {
					name = "Simple XSLT Transformation";
				} else if ( scenario.isFOPEnabled()) {
					name = "Simple XSL:FO Transformation";
				} else if ( scenario.isXQueryEnabled()) {
					name = "Simple XQuery";
				}
			}

			setTitle( "Execute Scenario ["+name+"]");
//			header.setTitle("Execute Scenario ["+name+"]");
			if ( scenario.isBrowserEnabled()) { 
				String url = scenario.getBrowserURL();

				if ( url != null && url.trim().length() > 0) {
					showBrowserBox.setText( "Show \""+url+"\" in Browser");
				} else {
					showBrowserBox.setText( "Show Output in Browser");
				}
				
				showBrowserBox.setSelected( true);
			} else {
				showBrowserBox.setText( "Show Output in Browser");
				showBrowserBox.setSelected( false);
				
				showBrowserBox.setEnabled( !scenario.isFOPEnabled());
			}
			
			hideOnCompletionBox.setSelected( properties.isHideExecuteScenarioDialogWhenComplete());
			
			//setVisible( true);
			show();
			busy = true;
			processor = new ScenarioProcessor( scenario, document);
			processor.init();

			startLogging( "["+FileUtilities.getProcessorVersion()+"] Starting transformation ...");
			scenarioNameLabel.setText( name);
			
			scenarioModel.setScenario( scenario);
			
	 		Runnable runner = new Runnable() {
	 			public void run()  {
	 				try{
						try {
							setStatus( "Opening input ...");
							scenarioModel.setState( 0, scenarioModel.STATUS_PROCESSING);
							
							processor.openInput();
							scenarioModel.setState( 0, scenarioModel.STATUS_PROCESSED);
							setStatus( "Input opened.");
						} catch ( SAXException e) {
							scenarioModel.setState( 0, scenarioModel.STATUS_ERROR);
							throw e;
						} catch ( IOException e) {
							scenarioModel.setState( 0, scenarioModel.STATUS_ERROR);
							throw e;
						} catch ( Exception e) {
							scenarioModel.setState( 0, scenarioModel.STATUS_ERROR);
							throw e;
						}

						try {
							scenarioModel.setState( 1, scenarioModel.STATUS_PROCESSING);
	
							setStatus( "Opening stylesheet ...");
							processor.openStylesheet();
							setStatus( "Stylesheet opened.");
							
							setStatus( "Transforming ...");
							processor.execute();
							scenarioModel.setState( 1, scenarioModel.STATUS_PROCESSED);
							setStatus( "Transformed.");
						} catch ( TransformerException e) {
							scenarioModel.setState( 1, scenarioModel.STATUS_ERROR);
							throw e;
						} catch ( SAXException e) {
							scenarioModel.setState( 1, scenarioModel.STATUS_ERROR);
							throw e;
						//} catch ( FOPException e) {
						//	scenarioModel.setState( 1, scenarioModel.STATUS_ERROR);
						//	throw e;
						} catch ( IOException e) {
							scenarioModel.setState( 1, scenarioModel.STATUS_ERROR);
							throw e;
						} catch ( Exception e) {
							scenarioModel.setState( 1, scenarioModel.STATUS_ERROR);
							throw e;
						}
						
						try {
							scenarioModel.setState( 2, scenarioModel.STATUS_PROCESSING);
							setStatus( "Saving ...");
							processor.save( ExecuteScenarioDialog.this);
							scenarioModel.setState( 2, scenarioModel.STATUS_PROCESSED);
							setStatus( "Saved.");
	
							if ( showBrowserBox.isSelected() && !scenario.isFOPEnabled()) {
								processor.browse();
							}

							stopLogging( "Transformation Completed.");

							if ( hideOnCompletionBox.isSelected() && !(scenario.isFOPEnabled() && scenario.getFOPOutputType() == ScenarioProperties.FOP_OUTPUT_TO_VIEWER)) {
//								setVisible(false);
								//setVisible(false);
								hide();
							}
						} catch ( IOException e) {
							scenarioModel.setState( 2, scenarioModel.STATUS_ERROR);
							throw e;
						} catch ( Exception e) {
							scenarioModel.setState( 2, scenarioModel.STATUS_ERROR);
							throw e;
						}
					} catch ( IOException e) {
						System.err.println( "FATAL ERROR: "+e.getMessage());
						stopLogging( "Transformation Interrupted!");
						e.printStackTrace();
					} catch ( FOPException e) {
						System.err.println( "FATAL ERROR: "+e.getMessage());
						stopLogging( "Transformation Interrupted!");
						e.printStackTrace();
					} catch ( SAXException e) {
						System.err.println( "FATAL ERROR: "+e.getMessage());
						stopLogging( "Transformation Interrupted!");
						e.printStackTrace();
					} catch ( TransformerException e) {
						System.err.println( "FATAL ERROR: "+e.getMessage());
						stopLogging( "Transformation Interrupted!");
						e.printStackTrace();
					} catch ( Exception e) {
						System.err.println( "FATAL ERROR: "+e.getMessage());
						stopLogging( "Transformation Interrupted!");
						e.printStackTrace();
					} finally {
						setStatus( "Done");
						processor.cleanup();
						processor = null;
						System.gc();
						busy = false;
					}
	 			}
	 		};
	 		
	 		// Create and start the thread ...
	 		thread = new Thread( runner);
	 		thread.start();
	 			
		} else {
			toFront();
		}
	}
	
	private class ScenarioTableModel extends AbstractTableModel {
		public final int ROW_TYPE_INPUT			= 0;
		public final int ROW_TYPE_TRANSFORM		= 1;
		public final int ROW_TYPE_OUTPUT		= 2;

		public final int STATUS_NONE		= 0;
		public final int STATUS_PROCESSED	= 1;
		public final int STATUS_PROCESSING	= 2;
		public final int STATUS_ERROR		= 3;

		ScenarioProperties scenario = null;
		int status[] = null;
		
		public ScenarioTableModel() {
			status = new int[3];
			
			status[0] = STATUS_NONE;
			status[1] = STATUS_NONE;
			status[2] = STATUS_NONE;
		}
		
		private void setScenario( ScenarioProperties scenario) {
			this.scenario = scenario;
			
			status = new int[3];
			
			status[0] = STATUS_NONE;
			status[1] = STATUS_NONE;
			status[2] = STATUS_NONE;
			
			fireTableDataChanged();
		}

		public int getRowCount() {
			return 3;
		}
		
		public int getState( int row) {
			return status[row];
		}

		public void setState( final int row, int state) {
			status[row] = state;
			
			if ( SwingUtilities.isEventDispatchThread()) {
				fireTableCellUpdated( row, 0);
			} else {
				SwingUtilities.invokeLater( new Runnable() {
					public void run() {
						fireTableCellUpdated( row, 0);
					}
				});
			}
		}
		
		public String getColumnName( int column) {
			String name = "";

			if ( column == 0) {
				name = "";
			} else if ( column == 1) {
				name = "Type";
			}
			
			return name;
		}

		public Class getColumnClass( int column) {
			if ( column == 0) {
				return ImageIcon.class;
			} else {
				return String.class;
			}
		}

		public int getColumnCount() {
			return 2;
		}
		
		public int getRowType( int row) {
			return row;
		}

		public Object getValueAt( int row, int column) {
			Object result = null;
			
			if ( column == 0) {
				if ( status[row] == STATUS_ERROR) {
					result = XngrImageLoader.get().getImage( "com/cladonia/xml/transform/icons/Error16.gif");
				} else if ( status[row] == STATUS_PROCESSED) {
					result = XngrImageLoader.get().getImage( "com/cladonia/xml/transform/icons/Processed16.gif");
				} else if ( status[row] == STATUS_PROCESSING) {
					result = XngrImageLoader.get().getImage( "com/cladonia/xml/transform/icons/Processing16.gif");
				} else if ( status[row] == STATUS_NONE) {
					result = null;
				}
			} else if ( column == 1) {
				if ( scenario != null) {
					if ( row == 0) {
						switch ( scenario.getInputType()) {
							case ScenarioProperties.INPUT_CURRENT_DOCUMENT :
								result = "XML Input (Current Document)";
								break;
	
							case ScenarioProperties.INPUT_FROM_URL :
								result = "XML Input (URL)";
								break;
	
							case ScenarioProperties.INPUT_PROMPT_FOR_DOCUMENT :
								result = "XML Input (Prompt for Document)";
								break;
						}
					} else if ( row == 1) {
						if ( scenario.isXSLEnabled()) {
							switch ( scenario.getXSLType()) {
								case ScenarioProperties.XSL_CURRENT_DOCUMENT :
									result = "XSLT Transformer (Current Document)";
									break;
		
								case ScenarioProperties.XSL_FROM_URL :
									result = "XSLT Transformer (URL)";
									break;
		
								case ScenarioProperties.XSL_PROMPT_FOR_DOCUMENT :
									result = "XSLT Transformer (Prompt for Stylesheet)";
									break;
		
								case ScenarioProperties.XSL_USE_PROCESSING_INSTRUCTIONS :
									result = "XSLT Transformer (Use Processing Instructions)";
									break;
							}
						} else if ( scenario.isXQueryEnabled()) {
							switch ( scenario.getXQueryType()) {
								case ScenarioProperties.XQUERY_CURRENT_DOCUMENT :
									result = "XQuery (Current Document)";
									break;
		
								case ScenarioProperties.XQUERY_FROM_URL :
									result = "XQuery (URL)";
									break;
		
								case ScenarioProperties.XQUERY_PROMPT_FOR_DOCUMENT :
									result = "XQuery (Prompt for XQuery Document)";
									break;
							}
						} else {
							result = "XSLT Transformer (None)";
						}
					} else if ( row == 2) {
						if ( scenario.isFOPEnabled()) {
							switch ( scenario.getFOPOutputType()) {
								case ScenarioProperties.FOP_OUTPUT_PROMPT_FOR_FILE:
									result = "FOP Output (Prompt for File)";
									break;
		
								case ScenarioProperties.FOP_OUTPUT_TO_FILE :
									result = "FOP Output (File)";
									break;
		
								case ScenarioProperties.FOP_OUTPUT_TO_VIEWER:
									result = "FOP Output (Internal Viewer)";
									break;
							}
						} else {
							switch ( scenario.getOutputType()) {
								case ScenarioProperties.OUTPUT_PROMPT_FOR_FILE :
									result = "XML Output (Prompt for File)";
									break;
		
								case ScenarioProperties.OUTPUT_TO_FILE :
									result = "XML Output (File)";
									break;
		
								case ScenarioProperties.OUTPUT_TO_NEW_DOCUMENT :
									result = "XML Output (New Document)";
									break;
	
								case ScenarioProperties.OUTPUT_TO_INPUT:
									result = "XML Output (Input Document)";
									break;
							}
						}
					}
				} else {
					if ( row == 0) {
						result = "[input] Current Document";
					} else if ( row == 1) {
						result = "[style] From URL";
					} else if ( row == 2) {
						result = "[output] New Document";
					}
				}
			}
			
			return result;
		}
	}
	
	public static void main( String[] args) {
		ExecuteScenarioDialog dialog = new ExecuteScenarioDialog( null);
		//dialog.setVisible(true);
		dialog.show();
		
	}
	
	public class ScenarioTableCellRenderer extends DefaultTableCellRenderer {

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	   		JLabel label = (JLabel)super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column);
	   		
	   		TableModel model = table.getModel();
	   		
	   		if ( model instanceof ScenarioTableModel) {
	   			int rowType = ((ScenarioTableModel)model).getRowType( row);
	   			
	   			if ( rowType == ((ScenarioTableModel)model).ROW_TYPE_INPUT) {
					label.setIcon( XngrImageLoader.get().getImage( "com/cladonia/xml/transform/icons/Input16.gif"));
	   			} else if ( rowType == ((ScenarioTableModel)model).ROW_TYPE_TRANSFORM) {
					label.setIcon( XngrImageLoader.get().getImage( "com/cladonia/xml/transform/icons/Transform16.gif"));
	   			} else if ( rowType == ((ScenarioTableModel)model).ROW_TYPE_OUTPUT) {
					label.setIcon( XngrImageLoader.get().getImage( "com/cladonia/xml/transform/icons/Output16.gif"));
	   			}
	   			
	   			label.setFont( label.getFont().deriveFont( Font.BOLD));
	   		}
	   			
   			return label;
		}
	}
	
//	public QPanel buildHeader(JFrame parent) {
//	    header = new XngrDialogHeader(parent, true);
//	    return(header.buildHeader());
//	}
	
	
}
