/*
 * $Id: LatestNewsDialog.java,v 1.0 7 Jun 2007 09:59:20 Administrator Exp $
 * 
 * Copyright (C) 2005, Cladonia Ltd. All rights reserved.
 * 
 * This software is the proprietary information of Cladonia Ltd. Use is subject
 * to license terms.
 */

package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;

import com.l2fprod.common.swing.TipModel;
import com.l2fprod.common.swing.TipModel.Tip;
import com.l2fprod.common.swing.tips.DefaultTipModel;

/**
 * 
 * 
 * @version $Revision: 1.0 $, $Date: 7 Jun 2007 09:59:20 $
 * @author Thomas Curley <tcurley@cladonia.com>
 */
public class LatestNewsDialog extends XngrDialogHeader {

	/**
	 * Key used to store the status of the "Show tip on startup" checkbox"
	 */
	public static final String PREFERENCE_KEY = "ShowTipOnStartup";

	/**
	 * Used when generating PropertyChangeEvents for the "currentTip" property
	 */
	public static final String CURRENT_TIP_CHANGED_KEY = "currentTip";

	private TipModel model;
	private int currentTip = 0;
	private static final Dimension SIZE = new Dimension( 400, 300);

	
	// The components that contain the values
	
	private JEditorPane tipField;

	private JButton closeButton;
	private JButton previousButton;
	private JButton nextButton;


	/**
	 * Initialise the class LatestNewsDialog.java
	 * 
	 * @param frame
	 * @param modal
	 */
	public LatestNewsDialog(JFrame frame, boolean modal) {

		this(frame, modal, new DefaultTipModel(new Tip[0]));
		// TODO Auto-generated constructor stub
	}
	
	public LatestNewsDialog(JFrame frame, boolean modal, TipModel model) {

		super(frame, modal);
		this.model = model;
		
		//setResizable( false);
		setTitle( "Latest News");
		setDialogDescription( "The Latest Cladonia News");
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 10, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelButtonPressed();
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		
		
		//tipField = new TextArea();
		tipField = new JEditorPane();
		tipField.setEditorKit(new HTMLEditorKit());
		tipField.setEditable(false);
		
		JScrollPane tipScrollPane = new JScrollPane(tipField);
		main.add( tipScrollPane, BorderLayout.CENTER);
		//removed for xngr-dialog

		closeButton = new JButton( "Close");
		closeButton.setMnemonic( 'C');
		closeButton.setFont( closeButton.getFont().deriveFont( Font.PLAIN));
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				cancelButtonPressed();
			}
		});

		previousButton = new JButton( "Previous");
		previousButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				previousButtonPressed();
			}
		});
		
		nextButton = new JButton( "Next");
		nextButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				nextButtonPressed();
			}
		});

		getRootPane().setDefaultButton( closeButton);

		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 5, 5));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( previousButton);
		buttonPanel.add( nextButton);
		buttonPanel.add( closeButton);

		main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelButtonPressed();
			}
		});

		setDefaultCloseOperation( HIDE_ON_CLOSE);

		getRootPane().setDefaultButton(closeButton);
		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), Math.max( SIZE.height, getSize().height)));

		setLocationRelativeTo( frame);
		
	}
	
	protected void nextButtonPressed() {
	
		nextTip();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateTip();		
			}
		});
		
	}
	
	protected void previousButtonPressed() {
		
		previousTip();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				updateTip();		
			}
		});
	}
	
	protected void okButtonPressed() {
		
		
		cancelled = false;
	    hide();
		
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	/**
	 * Initialises the values in the dialog.
	 */
	public void show() {
		
		updateTip();
		super.show();
	}

	

	public TipModel getModel() {

		return model;
	}

	public void setModel(TipModel model) {

		TipModel old = this.model;
		this.model = model;
		firePropertyChange("model", old, model);
	}

	public int getCurrentTip() {

		return currentTip;
	}
	
	public void updateTip() {
		if(this.getModel() != null) {
			if(this.getModel().getTipCount() >= this.currentTip) {
				
				Tip tip = this.getModel().getTipAt(this.currentTip);
				if(tip != null) {
					if(tip.getTip() instanceof String) {
						this.tipField.setText((String) tip.getTip());
						if(this.getModel().getTipCount() <= this.currentTip+1) {
							this.nextButton.setEnabled(false);
						}
						else {
							this.nextButton.setEnabled(true);
						}
						
						if(this.currentTip == 0) {
							this.previousButton.setEnabled(false);							
						}
						else {
							this.previousButton.setEnabled(true);
						}
					}
					
				}
			}
			else {
				this.nextButton.setEnabled(false);				
			}
		}
	}

	/**
	 * Sets the index of the tip to show
	 * 
	 * @param currentTip
	 * @throw IllegalArgumentException if currentTip is not within the bounds
	 *        [0, getModel().getTipCount()[.
	 */
	public void setCurrentTip(int currentTip) {

		if (currentTip < 0 || currentTip >= getModel().getTipCount()) {
			throw new IllegalArgumentException(
					"Current tip must be within the bounds [0, "
							+ getModel().getTipCount() + "[");
		}

		int oldTip = this.currentTip;
		this.currentTip = currentTip;
		firePropertyChange(CURRENT_TIP_CHANGED_KEY, oldTip, currentTip);
	}

	/**
	 * Shows the next tip in the list. It cycles the tip list.
	 */
	public void nextTip() {

		int count = getModel().getTipCount();
		if (count == 0) {
			return;
		}

		int nextTip = currentTip + 1;
		if (nextTip >= count) {
			nextTip = 0;
		}
		setCurrentTip(nextTip);
	}

	/**
	 * Shows the previous tip in the list. It cycles the tip list.
	 */
	public void previousTip() {

		int count = getModel().getTipCount();
		if (count == 0) {
			return;
		}

		int previousTip = currentTip - 1;
		if (previousTip < 0) {
			previousTip = count - 1;
		}
		setCurrentTip(previousTip);
	}
	
	

}
