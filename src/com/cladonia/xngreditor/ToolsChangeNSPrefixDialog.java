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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;
import org.dom4j.Namespace;

import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.FontType;
import com.cladonia.xngreditor.properties.TextPreferences;


/**
 * Dialog used by the ToolsChangeNSPrefixAction class
 *
 * @version	$Revision: 1.14 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsChangeNSPrefixDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 600);
    private JFrame parent = null;
    private JPanel main = null;
    private ConfigurationProperties props;
    private JList prefixList;
    private JButton editButton;
    private Vector namespaces;
    private DefaultListModel listModel;
    private JPanel editPanel;
    private PrefixDialog dialog = null;
    public JRadioButton toNewDocumentRadio;
    private JRadioButton toCurrentDocumentRadio;
    
       
    /**
     * @param frame
     * @param modal
     */
    public ToolsChangeNSPrefixDialog(JFrame frame,ConfigurationProperties props) {

        super(frame, true);
        super.setTitle("XML Tools",
				"Rename a Namespace Prefix",
				"\tSelect Namespace to Edit");
	    
        this.parent = frame;
        this.props = props;
	    main = new JPanel();
	    main.setLayout(new BorderLayout());
	    
	    
	    JPanel centerPanel = new JPanel( new BorderLayout());
	    centerPanel.setBorder(new CompoundBorder(new TitledBorder("Namespaces"),new EmptyBorder(5,5,5,5)));
//	    centerPanel.setLayout(new FormLayout(1,1));
	    listModel = new DefaultListModel();
	    //listModel.addElement(" ");
        prefixList = new JList(listModel);

        prefixList.setVisibleRowCount( 5);
        
        prefixList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        prefixList.setLayoutOrientation(JList.VERTICAL);
        prefixList.setCellRenderer( new MappingCellRenderer());
        prefixList.addMouseListener( new DoubleClickListener() {
			public void doubleClicked(MouseEvent arg0) {
			    editButtonPressed();
               
            }
		});
        
        prefixList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = prefixList.getSelectedIndex();
                if(selected>-1) {
                    prefixList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
        
        prefixList.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
			    prefixList.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
        //prefixList.setVisibleRowCount(-1);
        
	    JScrollPane scrollPane = new JScrollPane();
	    scrollPane.getViewport().setView(prefixList);
	    editPanel = new JPanel(new BorderLayout());
	    editPanel.setBorder( new EmptyBorder( 2, 0, 0, 0));
	    
	    editButton = new JButton("Edit");
	    editButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                editButtonPressed();
            }
	        
	    });
	    
	    editPanel.add(editButton, BorderLayout.EAST);
	    editPanel.add(editButton, BorderLayout.EAST);
	    centerPanel.add(scrollPane,BorderLayout.CENTER);
	    centerPanel.add(editPanel,BorderLayout.SOUTH);
	    
	    main.add(centerPanel,BorderLayout.CENTER);
	    main.add(this.buildGeneralPanel(),BorderLayout.SOUTH);
	    
	    main.setBorder(new EmptyBorder(5,5,5,5));
	    
		//setSize( new Dimension( SIZE.width, SIZE.height));
	    
	    setContentPane( main);
	    pack();
	    setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));
	    
    }
    
    /**
     * @return
     */
    private JPanel buildGeneralPanel() {

        JPanel general = new JPanel();
        general.setLayout(new FormLayout(3,2));
        
        toNewDocumentRadio = new JRadioButton("To New Document");
        toCurrentDocumentRadio = new JRadioButton("To Current Document");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(toNewDocumentRadio);
        radioGroup.add(toCurrentDocumentRadio);
        toCurrentDocumentRadio.setSelected(true);
        
        general.add(toNewDocumentRadio,FormLayout.FULL_FILL);
        general.add(toCurrentDocumentRadio,FormLayout.FULL_FILL);
        
        
        general.setBorder(new CompoundBorder(new TitledBorder("Output"),new EmptyBorder(5,5,5,5)));
        
        return (general);
    }
    
    public void show(Vector ns) {
        this.namespaces = ns;
        listModel.removeAllElements();
        for(int cnt=0;cnt<namespaces.size();++cnt) {
            Namespace newNs = (Namespace)namespaces.get(cnt);
            listModel.addElement( newNs.getPrefix()+":"+newNs.getURI());
        }
        //pack();
        super.show();
    }
    
    public void editButtonPressed() {
        if(!prefixList.isSelectionEmpty()) {
	        String prefix = getPrefix((String)prefixList.getSelectedValue());
	        String uri = getURI((String)prefixList.getSelectedValue());
	        
	        //check if it has not been defined already
	        if(dialog==null) {
	            dialog = new PrefixDialog(this.parent);
	        }
        
            dialog.show(this.namespaces,prefix,uri);
        }
        else {
            MessageHandler.showError(parent,"Please select a namespace to edit","Change Namespace Prefix");
        }
        
    }
    
    
    /**
     * @return Returns the namespaces.
     */
    public Vector getNamespaces() {

        return namespaces;
    }
    /**
     * @param namespaces The namespaces to set.
     */
    public void setNamespaces(Vector namespaces) {

        this.namespaces = namespaces;
    }
    
    private String getPrefix( String mapping) {
		int index = mapping.indexOf( ':');
		
		return mapping.substring( 0, index);
	}
	
	private String getURI( String mapping) {
		int index = mapping.indexOf( ':');
		
		return mapping.substring( index+1, mapping.length());
	}
	
	public void updateMainDialog(Vector prefixes) {
	    
	    this.namespaces = prefixes;
	    listModel.removeAllElements();
        for(int cnt=0;cnt<namespaces.size();++cnt) {
            Namespace newNs = (Namespace)namespaces.get(cnt);
            listModel.addElement( newNs.getPrefix()+":"+newNs.getURI());
        }
	}
    
    private class PrefixDialog extends XngrDialog {
		private Vector prefixes		= null;
		private String prefix		= null;

		private boolean cancelled	= false;
		
		private JButton cancelButton	= null;
		private JButton okButton		= null;
        private JLabel oldURILabel;
        private JLabel oldPrefixLabel;
        private JTextField newURITextField;
        private JTextField newPrefixTextField;
        private JLabel prefixLabel;
        private JLabel uriLabel;
        private JLabel prefixLabel2;
        private JLabel uriLabel2;
		
		// The components that contain the values
		

		/**
		 * The dialog that displays the preferences for the editor.
		 *
		 * @param frame the parent frame.
		 */
		public PrefixDialog( JFrame parent) {
			super( parent, true);
			super.setTitle("XML Tools",
					"Change Namespace Prefix",
					"\tEnter the new Prefix...");
			
			setResizable( false);
			setTitle( "Prefix Namespace Mapping");
			
			JPanel main = new JPanel( new BorderLayout());
			main.setBorder( new EmptyBorder( 5, 5, 5, 5));
			
			//generic labels
			uriLabel = new JLabel("URI:");
			prefixLabel = new JLabel("Prefix:");
			
			uriLabel2 = new JLabel("URI:");
			prefixLabel2 = new JLabel("Prefix:");
			
			// OLD
			JPanel oldPanel = new JPanel( new FormLayout( 10, 2));
			oldPanel.setBorder(new CompoundBorder(new TitledBorder("Current Namespace Details"),new EmptyBorder(5,5,5,5)));
		
			oldURILabel = new JLabel();
			oldPrefixLabel = new JLabel();
			
			oldPanel.add(uriLabel2,FormLayout.LEFT);
			oldPanel.add(oldURILabel,FormLayout.RIGHT_FILL);
			
			oldPanel.add(prefixLabel2,FormLayout.LEFT);
			oldPanel.add(oldPrefixLabel,FormLayout.RIGHT_FILL);
			
						
			
			
			// NEW
			JPanel newPanel = new JPanel( new FormLayout( 10, 2));
			newPanel.setBorder(new CompoundBorder(new TitledBorder("Replace With"),new EmptyBorder(5,5,5,5)));
		
			newURITextField = new JTextField();
			newURITextField.setEnabled(false);
			newPrefixTextField = new JTextField();
			newPrefixTextField.addAncestorListener( new AncestorListener() {
				public void ancestorAdded( AncestorEvent e) {
				    newPrefixTextField.requestFocusInWindow();
				}

				public void ancestorMoved( AncestorEvent e) {}
				public void ancestorRemoved( AncestorEvent e) {}
			});
			
			newPanel.add(uriLabel,FormLayout.LEFT);
			newPanel.add(newURITextField,FormLayout.RIGHT_FILL);
			
			newPanel.add(prefixLabel,FormLayout.LEFT);
			newPanel.add(newPrefixTextField,FormLayout.RIGHT_FILL);
			
			main.add( oldPanel, BorderLayout.NORTH);	
			main.add( newPanel, BorderLayout.CENTER);
						
			setContentPane( main);
			
			pack();
			setSize( new Dimension( 400, (getSize().height)));
			
		}
		
		public void okButtonPressed() {
			String prefix = this.newPrefixTextField.getText();
			String uri = this.newURITextField.getText();
			
			if ( checkPrefix( prefix) && checkURI( uri)) {
			    //get the old namespace as well
				Namespace oldNs = new Namespace(oldPrefixLabel.getText(),oldURILabel.getText());
			    //change the prefix and update the super dialog
			    Namespace newNs = new Namespace(prefix,uri);
			    for(int cnt=0;cnt<prefixes.size();++cnt) {
			        Namespace ns = (Namespace)prefixes.get(cnt);
			        if(ns.equals(oldNs)) {
			            //replace this element with new namespace
			            prefixes.setElementAt(newNs,cnt);
			        }
			    }
			    updateMainDialog(prefixes);
			    super.okButtonPressed();
			}
			
		}

		public void show( Vector prefixes, String prefix, String uri) {
			this.prefixes = prefixes;
			this.prefix = prefix;
			
			setText( this.oldPrefixLabel, prefix);
			setText( this.oldURILabel, uri);
			
			setText( this.newPrefixTextField, prefix);
			setText( this.newURITextField, uri);
			
			super.show();
		}

		public void cancelButtonPressed() {
			cancelled = true;
			//setVisible(false);
			hide();
			super.cancelButtonPressed();
		}
		
		public String getPrefix() {
			return this.newPrefixTextField.getText();
		}

		public String getURI() {
			return this.newURITextField.getText();
		}

		/**
		 * When the dialog is cancelled and no selection has been made, 
		 * this method returns true.
		 *
		 * @return true when the dialog has been cancelled.
		 */
		public boolean isCancelled() {
			return cancelled;
		}
		
		private void setText( JTextField field, String text) {
			field.setText( text);
			field.setCaretPosition( 0);
		}
		
		private void setText( JLabel field, String text) {
			field.setText( text);
		}
		
		protected boolean isEmpty( String string) {
			if ( string != null && string.trim().length() > 0) {
				return false;
			}
			
			return true;
		}

		private boolean checkPrefix( String prefix) {
			
			for ( int i = 0; i < prefixes.size(); i++) {
			    Namespace ns = (Namespace)prefixes.elementAt(i);
			    
				if ( ns.getPrefix().equals( prefix) && !ns.getPrefix().equals( this.prefix)) {
				    if(prefix.length()==0) {
				        MessageHandler.showError(parent, "There is already a default namespace declared in this document","Rename Prefix Error");
				    }
				    else {
				        MessageHandler.showError(parent, "Prefix \""+ns.getPrefix()+"\" exists already.\n"+
				    								"Please specify a different prefix.","Rename Prefix Error");
				    }
					return false;
				}
			}
			
			return true;
		}

		private boolean checkURI( String uri) {
			if ( isEmpty( uri)) {
				MessageHandler.showMessage( "Please specify a URI for this Mapping.");
				return false;
			}
			
			return true;
		}
	} 
    
    private static final EmptyBorder noFocusBorder = new EmptyBorder(1, 1, 1, 1);
    
	public class MappingCellRenderer extends JPanel implements ListCellRenderer {
		private JLabel colon = null;
		private JLabel equals = null;
		private JLabel xmlns = null;
		private JLabel prefix = null;
		private JLabel uri = null;
		private JLabel startApos = null;
		private JLabel endApos = null;

		private boolean selected = false;
		private boolean showValue = false;
		
		/**
		 * The constructor for the renderer, sets the font type etc...
		 */
		public MappingCellRenderer() {
			super( new FlowLayout( FlowLayout.LEFT, 0, 0));
			
//			icon = new JLabel();

			xmlns = new JLabel();
			xmlns.setBorder( new EmptyBorder( 0, 0, 0, 0));
			xmlns.setOpaque( false);
			xmlns.setFont( xmlns.getFont().deriveFont( Font.BOLD));
			xmlns.setForeground( Color.black);
			xmlns.setText( "xmlns");
			
			colon = new JLabel();
			colon.setBorder( new EmptyBorder( 0, 0, 0, 0));
			colon.setOpaque( false);
			colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
			colon.setText( ":");

			prefix = new JLabel();
			prefix.setBorder( new EmptyBorder( 0, 0, 0, 0));
			prefix.setOpaque( false);
			prefix.setFont( prefix.getFont().deriveFont( Font.BOLD));
			prefix.setForeground( Color.black);

			equals = new JLabel();
			equals.setBorder( new EmptyBorder( 0, 0, 0, 0));
			equals.setOpaque( false);
			equals.setFont( equals.getFont().deriveFont( Font.BOLD));
			equals.setForeground( Color.black);
			equals.setText( "=");

			startApos = new JLabel();
			startApos.setBorder( new EmptyBorder( 0, 0, 0, 0));
			startApos.setOpaque( false);
			startApos.setFont( startApos.getFont().deriveFont( Font.PLAIN));
			startApos.setText( "\"");

			uri = new JLabel();
			uri.setBorder( new EmptyBorder( 0, 0, 0, 0));
			uri.setOpaque( false);
			uri.setFont( uri.getFont().deriveFont( Font.PLAIN));

			endApos = new JLabel();
			endApos.setBorder( new EmptyBorder( 0, 0, 0, 0));
			endApos.setOpaque( false);
			endApos.setFont( endApos.getFont().deriveFont( Font.PLAIN));
			endApos.setText( "\"");

			//			this.add( icon);
			this.add( xmlns);
			this.add( colon);
			this.add( prefix);
			this.add( equals);
			this.add( startApos);
			this.add( uri);
			this.add( endApos);
			
			updatePreferences();
		}
		
		public void updatePreferences() {
			FontType type = TextPreferences.getFontType( TextPreferences.NAMESPACE_PREFIX);
			prefix.setFont( type.getFont());
			prefix.setForeground( type.getColor());

			type = TextPreferences.getFontType( TextPreferences.SPECIAL);
			
			colon.setFont( type.getFont());
			colon.setForeground( type.getColor());
			
			startApos.setFont( type.getFont());
			startApos.setForeground( type.getColor());

			endApos.setFont( type.getFont());
			endApos.setForeground( type.getColor());
			
			equals.setFont( type.getFont());
			equals.setForeground( type.getColor());

			type = TextPreferences.getFontType( TextPreferences.NAMESPACE_NAME);
			xmlns.setFont( type.getFont());
			xmlns.setForeground( type.getColor());

			type = TextPreferences.getFontType( TextPreferences.NAMESPACE_VALUE);
			uri.setFont( type.getFont());
			uri.setForeground( type.getColor());
		}

		public void setForegroundColor( Color foreground) {
			prefix.setForeground( foreground);
			colon.setForeground( foreground);
			startApos.setForeground( foreground);
			endApos.setForeground( foreground);
			equals.setForeground( foreground);
			xmlns.setForeground( foreground);
			uri.setForeground( foreground);
		}

		public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
			if ( node instanceof String) {
				setPrefix( getPrefix( (String)node));
				uri.setText( getURI( (String)node));
			} else {
				prefix.setText( "");
				uri.setText( "");
			}

			if ( selected) {
				setForegroundColor( list.getSelectionForeground());
				setBackground( list.getSelectionBackground());
			} else {
				updatePreferences();
				setBackground( list.getBackground());
			}

			setEnabled( list.isEnabled());
			updatePreferences();
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
		
		private void setPrefix( String text) {
			if ( text != null && text.length() > 0) {
				colon.setVisible( true);
				prefix.setVisible( true);
				prefix.setText( text);
			} else {
				prefix.setVisible( false);
				colon.setVisible( false);
			}
		}
	} 
}
