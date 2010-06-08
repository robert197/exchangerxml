/*
 * $Id: XngrDialogHeader.java,v 1.5 2004/10/19 12:00:33 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import org.bounce.QPanel;


/**
 * @version	$Revision: 1.5 $, $Date: 2004/10/19 12:00:33 $
 * @author Thomas Curley <tcurley@cladonia.com>
 *
 * Extension to JDialog which adds a header with title, description and
 * icon to a dialog header
 */
public class XngrDialogHeader extends JDialog {

    private JPanel pMessage;
    private JLabel dialogTitleLabel;
    private JLabel dialogDescriptionLabel;
    private JPanel main;
    protected boolean cancelled = false;
    public static final int HEADER_HEIGHT = 45;
    
    /**
     * @param frame
     * @param modal
     */
    public XngrDialogHeader(JFrame frame, boolean modal) {
        super(frame,modal);
        
        main = new JPanel();
		main.setLayout(new BorderLayout());
		QPanel header = new QPanel();
		header = buildHeader();
		main.add(header,BorderLayout.NORTH);
		        
    }
    
    public void setContentPane(Container contentPane){
	    main.add(contentPane,BorderLayout.CENTER);
	    main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

	   
	    super.setContentPane(main);
	  
	}

    /**
	 * Builds the header QPanel with a gradient fill.
	 * also sets the dialog title and dialog description and exchangerEditor icon
	 * @return the QPanel
	 */
	public QPanel buildHeader() {
		
	    QPanel header = new QPanel();
		header.setLayout(new BorderLayout());
				
		header.setGradientBackground(true);
		header.setGradientColor( new Color( 199,210,253));
	
		header.setBackground(Color.white);
		
		header.setOpaque(true);
		
		pMessage = new JPanel();
		pMessage.setLayout(new GridLayout(2,1));
		dialogTitleLabel = new JLabel("");
		Font temp = dialogTitleLabel.getFont();
		
		dialogTitleLabel.setFont( dialogTitleLabel.getFont().deriveFont( Font.BOLD));
		dialogDescriptionLabel = new JLabel("");
		dialogDescriptionLabel.setFont( dialogDescriptionLabel.getFont().deriveFont( Font.PLAIN));
		
		pMessage.add(dialogTitleLabel);
		pMessage.add(dialogDescriptionLabel);
		pMessage.setBorder(new EmptyBorder(5,5,5,0));
		JPanel info = new JPanel();
		info.setLayout(new BorderLayout());
		info.add(pMessage,BorderLayout.CENTER);
				
		ImageIcon icon = new ImageIcon("xngr-editor-icon.gif","Help");
		JLabel lIcon = new JLabel(icon);
		lIcon.setBorder(new EmptyBorder(0,0,0,5));
		
		pMessage.setOpaque(false);
		info.setOpaque(false);
		header.add(lIcon,BorderLayout.EAST);
		header.add(info,BorderLayout.CENTER);
		MatteBorder mainBorder = new MatteBorder(0,0,1,0,UIManager.getColor("controlDkShadow"));
		header.setBorder(mainBorder);
		
		return(header);
	}
	
	/** overrides this method to control the window titles
	 * @see java.awt.Dialog#setTitle(java.lang.String)
	 */
	public void setTitle(String windowTitle1) {
		super.setTitle(windowTitle1);
		dialogTitleLabel.setText(windowTitle1);
				
	}
	
	/** overloads this method to control the window titles
	 * and allows a separate dialog title
	 * @see java.awt.Dialog#setTitle(java.lang.String)
	 */
	public void setTitle(String windowTitle1,String dialogTitle1) {
		super.setTitle(windowTitle1);
		dialogTitleLabel.setText(dialogTitle1);
	}
	
	/** overloads this method to control the window titles
	 * and allows a separate dialog title and dialog description
	 * @see java.awt.Dialog#setTitle(java.lang.String)
	 */
	public void setTitle(String windowTitle1,String dialogTitle1,String dialogDescription1) {
		
	    super.setTitle(windowTitle1);
	    dialogTitleLabel.setText(dialogTitle1);
		dialogDescriptionLabel.setText(dialogDescription1);
					
	}
	
	public void setDialogDescription(String dialogDescription) {
	    dialogDescriptionLabel.setText(dialogDescription);
	}

	/**
	 * The method which is called when the cancel button is pressed
	 */
	protected void cancelButtonPressed() {
		cancelled = true;
	    hide();
	}
	
}
