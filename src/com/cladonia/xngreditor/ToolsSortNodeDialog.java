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
import java.awt.Dimension;
import java.awt.Font;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.properties.ConfigurationProperties;


/**
 * Dialog used by the ToolsSortNodeAction class
 *
 * @version	$Revision: 1.8 $, $Date: 2004/11/04 19:21:49 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class ToolsSortNodeDialog extends XngrDialog {

    private static final Dimension SIZE = new Dimension( 400, 315);
    private JFrame parent = null;
    private JPanel main = null;
    private ConfigurationProperties props = null;
    //private JPanel xpathPanel = null;
    private Vector predicateHistory;
    private Vector sortPredicateHistory;
    //private JCheckBox useXpath;
    //private JTextField methodField;
    //private JLabel predicateLabel;
    private JComboBox xpathField;
    //private JLabel preNamespaceMappingLabel;
    public PrefixNamespaceMappingPanel mappingPanel;
    public JRadioButton toNewDocumentRadio;
    private JRadioButton toCurrentDocumentRadio;
    //private JLabel newNameLabel;
    //private JTextField newNameTextField;
    public JRadioButton ascendingRadio;
    private JComboBox sortXPathField;
    //private JLabel sortXPathLabel;
    private JRadioButton descendingRadio;
    private String currentXPath;
    

    /**
     * @param frame
     * @param modal
     */
    public ToolsSortNodeDialog(JFrame frame,ConfigurationProperties props) {

        super( frame, true);
		super.setTitle("XML Tools",
				"Sort By XPath",
				"Enter Query, Sort Query and Options...");
		
		this.parent = frame;
		this.props = props;
				
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));
		
		main.add(this.buildXpathPanel(), BorderLayout.NORTH);
		main.add(this.buildGeneralPanel(),BorderLayout.CENTER);
		
	    setContentPane( main);
	    pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), (getSize().height+15)));
	    
    }
    
    private JPanel buildXpathPanel() {
		JPanel xpathPanel = new JPanel( new FormLayout( 5, 5));
		xpathPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Xpath"),
									new EmptyBorder( 0, 5, 5, 5)));
		
		JTextField methodField = new JTextField();
		JLabel predicateLabel = new JLabel( "XPath:");
		xpathPanel.add(predicateLabel, FormLayout.LEFT);
		
		// the xpath prediate combo, comtains previous predicates used
		xpathField = new JComboBox();
		//xpathField.addItem("TO DO");
		xpathField.setFont( xpathField.getFont().deriveFont( Font.PLAIN));
		xpathField.setPreferredSize( new Dimension( xpathField.getPreferredSize().width, methodField.getPreferredSize().height));
		xpathField.setEditable(true);
		xpathField.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
			    xpathField.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
		xpathPanel.add(xpathField, FormLayout.RIGHT_FILL);
		
		sortXPathField = new JComboBox();
		sortXPathField.setFont( sortXPathField.getFont().deriveFont( Font.PLAIN));
		sortXPathField.setPreferredSize( new Dimension( sortXPathField.getPreferredSize().width, methodField.getPreferredSize().height));
		sortXPathField.setEditable(true);
		JLabel sortXPathLabel = new JLabel("Sort By:");
		
		xpathPanel.add(sortXPathLabel,FormLayout.LEFT);
		xpathPanel.add(sortXPathField,FormLayout.RIGHT_FILL);
		
		xpathPanel.add( new JLabel( " "), FormLayout.FULL_FILL);
		JLabel preNamespaceMappingLabel = new JLabel( "Prefix Namespace Mapping:");
		xpathPanel.add(preNamespaceMappingLabel , FormLayout.FULL_FILL);
		
		mappingPanel = new PrefixNamespaceMappingPanel(parent,props,3);
		
		xpathPanel.add(mappingPanel, FormLayout.FULL_FILL);
		
		return xpathPanel;
	}
    
        

    /**
     * @return
     */
    private JPanel buildGeneralPanel() {

        JPanel main = new JPanel(new BorderLayout());
        
        JPanel options = new JPanel();
        options.setLayout(new FormLayout(5,5));
        options.setBorder(new CompoundBorder(new TitledBorder("Sort Options"),new EmptyBorder(0,5,5,5)));
        
        ascendingRadio = new JRadioButton("Sort Ascending");
        descendingRadio = new JRadioButton("Sort Descending");
        ButtonGroup sortGroup = new ButtonGroup();
        sortGroup.add(ascendingRadio);
        sortGroup.add(descendingRadio);
        ascendingRadio.setSelected(true);
        
        options.add(ascendingRadio,FormLayout.FULL_FILL);
        options.add(descendingRadio,FormLayout.FULL_FILL);
        
        JPanel general = new JPanel();
        general.setLayout(new FormLayout(5,5));
        
        toNewDocumentRadio = new JRadioButton("To New Document");
        toCurrentDocumentRadio = new JRadioButton("To Current Document");
        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(toNewDocumentRadio);
        radioGroup.add(toCurrentDocumentRadio);
        toCurrentDocumentRadio.setSelected(true);
        
        general.add(toNewDocumentRadio,FormLayout.FULL_FILL);
        general.add(toCurrentDocumentRadio,FormLayout.FULL_FILL);
        
        
        general.setBorder(new CompoundBorder(new TitledBorder("Output"),new EmptyBorder(0,5,5,5)));
        main.add(options,BorderLayout.NORTH);
        main.add(general,BorderLayout.CENTER);
        
        return (main);
    }
    
    


    public void show(String currentXPath) {
//      get the predicate history
        this.currentXPath = currentXPath;
		predicateHistory = props.getXPathPredicates();
		sortPredicateHistory = props.getSortXPathPredicates();

		// set the combo
        setPredicateHistory();
		
		// set the prefix namespace mapping
		mappingPanel.init();
		
		//this.disableXPathPanel();
		//this.enableOptionsPanel();
		
        super.show();
    }
    
    public void okButtonPressed() {
    	
        
        if(this.getXpathPredicate()==null) {
    		MessageHandler.showError(parent,"Please Enter the XPath","Tools Sort By XPath");
    	}
        else if(this.getSortXpathPredicate()==null) {
    		MessageHandler.showError(parent,"Please Enter the Sort By XPath","Tools Sort By XPath");
    	}
        else {
    		//props.addXPath(this.getXpathPredicate());
    		props.addXPathPredicate(this.getXpathPredicate());
    		props.addSortXPathPredicate(this.getSortXpathPredicate());
    		mappingPanel.save();
    		super.okButtonPressed();
    	}
    }
    
    private void setPredicateHistory()
	{
		xpathField.removeAllItems();
		sortXPathField.removeAllItems();
		
		xpathField.addItem(currentXPath);
		
		for (int i=0;i<predicateHistory.size();i++)
		{
			xpathField.addItem((String)predicateHistory.get(i));
			
		}
		
		xpathField.setSelectedIndex(0);
		
		for (int i=0;i<sortPredicateHistory.size();i++)
		{
		    sortXPathField.addItem((String)sortPredicateHistory.get(i));
		}
		
		sortXPathField.setSelectedIndex(-1);
	}
    
    public String getXpathPredicate()
	{	
        if(xpathField.getSelectedItem()==null) {
            return(null);
        }
        else {
            String xpath = (String)xpathField.getSelectedItem();
            if(xpath.length()>0) {
                return(xpath);
            }
            else {
                return(null);
            }
        }
	}
    
    public String getSortXpathPredicate()
	{
        if(this.sortXPathField.getSelectedItem()==null) {
            return(null);
        }
        else {
            String xpath = (String)sortXPathField.getSelectedItem();
            if(xpath.length()>0) {
                return(xpath);
            }
            else {
                return(null);
            }
        } 
	}
}
