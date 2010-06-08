package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.bounce.FormLayout;

import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.l2fprod.common.swing.JDirectoryChooser;

public class DirectoryChooserDialog extends XngrDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Dimension size = new Dimension(350, 180);
	private ExchangerEditor xngr = null;
	private ConfigurationProperties properties = null;
	private JPanel basicPanel = null;
	private JTextField inputLocationField = null;
	private JPanel locationPanel = null;
	private JPanel locButtonPanel = null;
	
	private File selectedFolder = null;
	
	public DirectoryChooserDialog(ExchangerEditor xngr) {
		super(xngr, true);
		
		if( xngr != null) {
			this.xngr = xngr;
		}
		properties = xngr.getProperties();
		
		setResizable( false);
	    setTitle( "Directory Chooser", "Choose a directory");
		setDialogDescription( "Enter or browse for a directory");

		JPanel main = new JPanel( new BorderLayout());

		basicPanel = new JPanel( new BorderLayout());
		
		inputLocationField = new JTextField();
		inputLocationField.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
			    inputLocationField.requestFocusInWindow();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
		

		JButton inputLocationButton = new JButton( "...");
		inputLocationButton.setMargin( new Insets( 0, 10, 0, 10));
		inputLocationButton.setPreferredSize( 
		        new Dimension( inputLocationButton.getPreferredSize().width, 
		                		inputLocationField.getPreferredSize().height));
		
		inputLocationButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				inputLocationButtonPressed();
			}
		});
		
		inputLocationField.setEnabled( true);
		inputLocationButton.setEnabled( true);

		locationPanel = new JPanel( new FormLayout(3,2));
		
		locButtonPanel = new JPanel(new BorderLayout());
		locButtonPanel.add( inputLocationField, BorderLayout.CENTER);
		locButtonPanel.add( inputLocationButton, BorderLayout.EAST);
		
		JLabel locationLabel = new JLabel("Location: ");
		
		locationPanel.add( locationLabel, FormLayout.LEFT);
		locationPanel.add( locButtonPanel,FormLayout.RIGHT_FILL);
		
		basicPanel.add(locationPanel, BorderLayout.CENTER);
		main.add( basicPanel, BorderLayout.CENTER);

		main.setBorder(new CompoundBorder(new TitledBorder("Select Directory"), new EmptyBorder(5,5,5,5)));
		
		setContentPane( main);
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		
		setLocationRelativeTo( xngr);

		setSize(size);
	}
	
	public void show() {
		this.selectedFolder = null;
		super.show();
	}

	protected void okButtonPressed() {
		
		if(selectedFolder == null) {
			if((inputLocationField.getText() != null) && (inputLocationField.getText().length() > 0)) {
				selectedFolder = new File(inputLocationField.getText());
			}
			else {
				MessageHandler.showError("Please enter or select a folder", "Directory Chooser");
			}
		}
		
		
		if(selectedFolder != null) {
			if(selectedFolder.exists() == true) {
				if(selectedFolder.isDirectory() == true) {
					super.okButtonPressed();
				}
				else {
					MessageHandler.showError("The selected folder is not valid", "Directory Chooser");
				}
			}
			else {
				MessageHandler.showError("The selected folder does not exist", "Directory Chooser");
			}
		}
		else {
			MessageHandler.showError("Error selecting folder", "Directory Chooser");		
		}
		
		
	}
	

	private void inputLocationButtonPressed() {
		
		JDirectoryChooser chooser = FileUtilities.getDirectoryChooser();
		
	 	int value = chooser.showOpenDialog( xngr);
		
	 	if ( value == JDirectoryChooser.APPROVE_OPTION) {
		 	setSelectedFolder(chooser.getSelectedFile());
			inputLocationField.setText(selectedFolder.toString());
	 	}
	}



	public void setSelectedFolder(File selectedFolder) {
		this.selectedFolder = selectedFolder;
	}



	public File getSelectedFolder() {
		return selectedFolder;
	}
}
