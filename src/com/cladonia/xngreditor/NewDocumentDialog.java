/*
 * $Id: NewDocumentDialog.java,v 1.7 2004/11/04 19:21:48 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.template.TemplateProperties;

/**
 * The grammar properties selection dialog.
 *
 * @version	$Revision: 1.7 $, $Date: 2004/11/04 19:21:48 $
 * @author Dogsbay
 */
public class NewDocumentDialog extends XngrDialog {
	private static final boolean DEBUG = false;
	private static final Dimension SIZE = new Dimension( 350, 350);

	public static final int NEW_DEFAULT_XML_DOCUMENT 	= 0;
	public static final int NEW_TYPE_DOCUMENT			= 1;
	public static final int NEW_TEMPLATE_DOCUMENT		= 2;
	public static final int NEW_DTD_DOCUMENT 			= 3;
	public static final int NEW_FROM_CLIPBOARD_DOCUMENT = 4;

	private ConfigurationProperties properties = null;

	private JRadioButton dtdDocumentRadio		= null;
	private JRadioButton defaultDocumentRadio	= null;
	private JRadioButton fromClipboardDocumentRadio	= null;
	private JRadioButton forTypeRadio 			= null;
	private JRadioButton selectTemplateRadio	= null;
	private JRadioButton specifyTemplateRadio 	= null;

	private JList typesList		= null;
	private JList templatesList	= null;

	private GrammarListModel typesModel 	= null;
	private TemplateListModel templatesModel = null;

	private JTextField urlField = null;
	private JButton fileSelectionButton = null;
	
	private String clipboardData = null;

	/**
	 * The dialog that displays the list of grammar properties.
	 *
	 * @param frame the parent frame.
	 * @param props the configuration properties.
	 */
	public NewDocumentDialog( JFrame parent, ConfigurationProperties props) {
		super( parent, true);
		
		this.properties = props;
		
		setResizable( false);
		
		setTitle( "New XML Document");
		setDialogDescription( "Specify a Document Type or Template.");
		
		JPanel formPanel = new JPanel( new FormLayout( 10, 2));
		formPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "New Document"),
									new EmptyBorder( 0, 5, 5, 5)));
		
		ButtonGroup group = new ButtonGroup();

		// default
		defaultDocumentRadio = new JRadioButton( "Default XML Document");
		group.add( defaultDocumentRadio);
		formPanel.add( defaultDocumentRadio, FormLayout.FULL);
		formPanel.add( Box.createVerticalStrut( 5), FormLayout.FULL_FILL);
		
		fromClipboardDocumentRadio = new JRadioButton( "Document From Clipboard");
		group.add( fromClipboardDocumentRadio);
		formPanel.add( fromClipboardDocumentRadio, FormLayout.FULL);
		formPanel.add( Box.createVerticalStrut( 5), FormLayout.FULL_FILL);
		
		dtdDocumentRadio = new JRadioButton( "DTD Document");
		group.add( dtdDocumentRadio);
		formPanel.add( dtdDocumentRadio, FormLayout.FULL);
		formPanel.add( Box.createVerticalStrut( 5), FormLayout.FULL_FILL);

		// type
		forTypeRadio = new JRadioButton( "For Type");
		group.add( forTypeRadio);
		forTypeRadio.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				typesList.setEnabled( forTypeRadio.isSelected());
			}
		});
		formPanel.add( forTypeRadio, FormLayout.FULL);

		typesList = new JList();
		typesList.setCellRenderer( new GrammarListCellRenderer());
		typesList.setVisibleRowCount( 3);
		typesList.addMouseListener( new MouseAdapter() { 
			public void mouseReleased( MouseEvent e) {
				int index = typesList.getSelectedIndex();

				if ( index != -1 && typesList.isEnabled()) { // A row is selected...
					if ( e.getClickCount() == 2) {
						cancelled = false;
						//setVisible(false);
						hide();
					}
				}
			}
		});
		
		typesList.setEnabled( false);

		JScrollPane scrollPane = new JScrollPane(	typesList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		typesList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = typesList.getSelectedIndex();
                if(selected>-1) {
                    typesList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		
		JPanel listPanel = new JPanel( new BorderLayout());
		listPanel.setBorder( new EmptyBorder( 0, 20, 0, 0));
		listPanel.add( scrollPane, BorderLayout.CENTER);

		formPanel.add( listPanel, FormLayout.FULL_FILL);
		formPanel.add( Box.createVerticalStrut( 5), FormLayout.FULL_FILL);

		selectTemplateRadio = new JRadioButton( "From Template (Select)");
		group.add( selectTemplateRadio);
		selectTemplateRadio.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				templatesList.setEnabled( selectTemplateRadio.isSelected());
			}
		});

		templatesList = new JList();
		templatesList.setVisibleRowCount( 3);
		templatesList.addMouseListener( new MouseAdapter() { 
			public void mouseReleased( MouseEvent e) {
				int index = templatesList.getSelectedIndex();

				if ( index != -1 && templatesList.isEnabled()) { // A row is selected...
					if ( e.getClickCount() == 2) {
					
						cancelled = false;
						//setVisible(false);
						hide();
					}
				}
			}
		});
		templatesList.setEnabled( false);

		scrollPane = new JScrollPane(	templatesList,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
						
		templatesList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = templatesList.getSelectedIndex();
                if(selected>-1) {
                    templatesList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		listPanel = new JPanel( new BorderLayout());
		listPanel.setBorder( new EmptyBorder( 0, 20, 0, 0));
		listPanel.add( scrollPane, BorderLayout.CENTER);

		formPanel.add( selectTemplateRadio, FormLayout.FULL);
		formPanel.add( listPanel, FormLayout.FULL_FILL);
		formPanel.add( Box.createVerticalStrut( 5), FormLayout.FULL_FILL);

//		selectTemplateRadio.addItemListener( new ItemListener() {
//			public void itemStateChanged( ItemEvent e) {
//				urlField.setEnabled( fromTemplateRadio.isSelected());
//				fileSelectionButton.setEnabled( fromTemplateRadio.isSelected());
//			}
//		});
		
		specifyTemplateRadio = new JRadioButton( "From Template (URL)");
		group.add( specifyTemplateRadio);
		specifyTemplateRadio.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e) {
				urlField.setEnabled( specifyTemplateRadio.isSelected());
				fileSelectionButton.setEnabled( specifyTemplateRadio.isSelected());
			}
		});

		urlField = new JTextField();
		
		JLabel urlLabel = new JLabel( "URL:");

		fileSelectionButton = new JButton( "...");

		fileSelectionButton.setMargin( new Insets( 0, 10, 0, 10));
		fileSelectionButton.setPreferredSize( new Dimension( fileSelectionButton.getPreferredSize().width, urlField.getPreferredSize().height));
		fileSelectionButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				fileSelectionButtonPressed();
			}
		});

		JPanel urlInputPanel = new JPanel( new BorderLayout());
		urlInputPanel.add( urlField, BorderLayout.CENTER);
		urlInputPanel.add( fileSelectionButton, BorderLayout.EAST);

		JPanel urlPanel = new JPanel( new BorderLayout( 3, 0));
		urlPanel.add( urlLabel, BorderLayout.WEST);
		urlPanel.add( urlInputPanel, BorderLayout.CENTER);
		urlPanel.setBorder( new EmptyBorder( 0, 20, 0, 0));
		
		formPanel.add( specifyTemplateRadio, FormLayout.FULL);
		formPanel.add( urlPanel, FormLayout.FULL_FILL);

		urlField.setEnabled( false);
		fileSelectionButton.setEnabled( false);

		//removed for xngr-dialog
		/*okButton = new JButton("OK");
		cancelButton = new JButton("Cancel");
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = false;
				setVisible(false);
			}
		});
		
		cancelButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( okButton);
		buttonPanel.add( cancelButton);
		cancelButton.setFont( cancelButton.getFont().deriveFont( Font.PLAIN));
				
		getRootPane().setDefaultButton( okButton);*/
		
		JPanel mainPanel = new JPanel( new BorderLayout());
		mainPanel.add( formPanel, BorderLayout.CENTER);
		mainPanel.setBorder( new EmptyBorder( 5, 5, 5, 5));
		//mainPanel.add( buttonPanel, BorderLayout.SOUTH);

		this.setContentPane( mainPanel); 
		pack();
		
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), this.getPreferredSize().height+20));
		setLocationRelativeTo( parent);
	}
	
	private void fileSelectionButtonPressed() {
		JFileChooser chooser = FileUtilities.getFileChooser();

		int value = chooser.showOpenDialog( getParent());

		if ( value == JFileChooser.APPROVE_OPTION) {
			try {
				File file = chooser.getSelectedFile();
				URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
				urlField.setText( url.toString());
				urlField.setCaretPosition( 0);
			} catch (Exception x) {
			}
		}
	}

	/**
	 * Set the properties.
	 *
	 * @param properties the list of grammar properties.
	 */
	public void show() {
		typesModel = new GrammarListModel( properties.getGrammarProperties());
		typesList.setModel( typesModel);
		typesList.setSelectedIndex( 0);
		
		templatesModel = new TemplateListModel( properties.getTemplateProperties());
		templatesList.setModel( templatesModel);
		templatesList.setSelectedIndex( 0);
		
		setClipboardData(getClipboardContents());
		if((getClipboardData() != null) && (getClipboardData().length() > 0)) {
			fromClipboardDocumentRadio.setSelected( true);
			fromClipboardDocumentRadio.requestFocus();
		}
		else {
			defaultDocumentRadio.setSelected( true);
		}
		
		super.show();
	}
	
	/**
	  * Get the String residing on the clipboard.
	  *
	  * @return any text found on the Clipboard; if none found, return an
	  * empty String.
	  */
	  private String getClipboardContents() {
	    String result = "";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    //odd: the Object param of getContents is not currently used
	    Transferable contents = clipboard.getContents(null);
	    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
	    if ( hasTransferableText ) {
	      try {
	        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
	      }
	      catch (UnsupportedFlavorException ex){
	        //highly unlikely since we are using a standard DataFlavor
	        //System.out.println(ex);
	        //ex.printStackTrace();
	      }
	      catch (IOException ex) {
	        //System.out.println(ex);
	        //ex.printStackTrace();
	      }
	    }
	    return result;
	  }
	


	/**
	 * Returns the properties for the grammar type.
	 *
	 * @return the grammar type properties.
	 */
	public GrammarProperties getSelectedType() {
		int index = typesList.getSelectedIndex();
		
		if ( index != -1 && typesModel.getSize() > index) {
			return typesModel.getType( index);
		} else {
			return typesModel.getType( 0);
		}
	}
	
	/**
	 * Returns the properties for the template.
	 *
	 * @return the template properties.
	 */
	public TemplateProperties getSelectedTemplate() {
		int index = templatesList.getSelectedIndex();
		
		if ( index != -1 && templatesModel.getSize() > index) {
			return templatesModel.getTemplate( index);
		} else {
			return templatesModel.getTemplate( 0);
		}
	}

	/**
	 * Returns the to be created document type.
	 *
	 * @return the new document type.
	 */
	public int getNewDocumentType() {
		if ( forTypeRadio.isSelected()) {
			return NEW_TYPE_DOCUMENT;
		} else if ( specifyTemplateRadio.isSelected()) {
			return NEW_TEMPLATE_DOCUMENT;
		} else if ( selectTemplateRadio.isSelected()) {
			return NEW_TEMPLATE_DOCUMENT;
		} else if ( dtdDocumentRadio.isSelected()) {
			return NEW_DTD_DOCUMENT;
		} else if (fromClipboardDocumentRadio.isSelected()) {
			return( NEW_FROM_CLIPBOARD_DOCUMENT);
		} else {
			return NEW_DEFAULT_XML_DOCUMENT;
		}
	}

	/**
	 * Returns the template url.
	 *
	 * @return the template url.
	 */
	public URL getTemplateURL() {
		URL result = null;
		
		try {
			if ( specifyTemplateRadio.isSelected()) {
				result = new URL( urlField.getText());
			} else { // if ( selectTemplateRadio.isSelected()) {
				result = getSelectedTemplate().getURL();
			}
		} catch ( Exception e) {
		
		}
		
		return result;
	}

	public void setClipboardData(String clipboardData) {
		this.clipboardData = clipboardData;
	}

	public String getClipboardData() {
		return clipboardData;
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

		public Object getElementAt( int i) {
			return (GrammarProperties)elements.elementAt( i);
		}

		public GrammarProperties getType( int i) {
			return (GrammarProperties)elements.elementAt( i);
		}
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

		public Object getElementAt( int i) {
			return ((TemplateProperties)elements.elementAt( i)).getName();
		}

		public TemplateProperties getTemplate( int i) {
			return (TemplateProperties)elements.elementAt( i);
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
