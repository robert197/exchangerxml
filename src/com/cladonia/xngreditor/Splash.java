/*
 * $Id: Splash.java,v 1.1 2004/03/25 18:52:46 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.bounce.CenterLayout;
import org.bounce.FormLayout;
import org.bounce.LinkButton;
import org.bounce.QPanel;
import org.bounce.util.BrowserLauncher;

/**
 * The splash screen.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:52:46 $
 * @author Dogsbay
 */
public class Splash extends JWindow {
	private static final boolean DEBUG = false;
	
//	private JLabel descriptionField	= null;
//	private JLabel versionField		= null;
//	private JLabel vendorField		= null;
//	private LinkButton referenceField	= null;
//	private JLabel copyrightField	= null;
//	private JLabel statusField		= null;
//	private JLabel titleField 		= null;
//
//	private JProgressBar progressBar = null;
//	private JPanel contentPane = null;
//	private JPanel productPanel = null;
//	private JPanel progressPanel = null;
//	
//	private QButton cancelButton = null;
//	private JPanel progressField = null;

	/**
	 * Constructor for the Splash screen!
	 *
	 * @param frame the parent frame.
	 * @param identity the version and copyright info for the editor.
	 */
	public Splash( JFrame owner, Identity identity) {
	
		// To make sure the Splash window stays 'on top'.
		super( owner);
		
		QPanel contentPane = new QPanel( new BorderLayout());
		contentPane.setGradientBackground( true);
		contentPane.setBackground( Color.white);
		contentPane.setBorder( new CompoundBorder(
				new EmptyBorder( 5, 5, 5, 5),
				new CompoundBorder( 
					new LineBorder( new Color( 51, 102, 153), 2),
					new EmptyBorder( 5, 5, 5, 5))));
		
		JLabel logoLabel = new JLabel( XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/CladoniaLogo.gif"));
		logoLabel.setHorizontalAlignment( SwingConstants.CENTER);

		JLabel titleField = new JLabel( identity.getTitle()+" V "+identity.getVersion());
		Font prevFont = titleField.getFont();
		titleField.setFont( prevFont.deriveFont( Font.BOLD, prevFont.getSize() + 8));
		titleField.setForeground( Color.white);
		titleField.setBackground( new Color( 51, 102, 153));
		titleField.setOpaque( true);
		titleField.setBorder( new EmptyBorder( 5, 5, 5, 5));
		titleField.setHorizontalAlignment( SwingConstants.CENTER);

		final LinkButton referenceField = new LinkButton( identity.getReference());
		referenceField.setForeground( Color.blue);
		referenceField.setOpaque( false);
		referenceField.setHorizontalAlignment( SwingConstants.CENTER);
		referenceField.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				try {
					BrowserLauncher.openURL( referenceField.getText());
				} catch( Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		

		JLabel copyrightField = new JLabel( identity.getCopyright());
		copyrightField.setHorizontalAlignment( SwingConstants.CENTER);
		copyrightField.setForeground( new Color( 51, 102, 153));
		copyrightField.setOpaque( false);
		copyrightField.setBorder( new EmptyBorder( 0, 0, 0, 0));
		
		JPanel referencePanel = new JPanel( new FormLayout( 0, 20));
		referencePanel.add( referenceField, FormLayout.FULL_FILL);
		referencePanel.add( copyrightField, FormLayout.FULL_FILL);
		referencePanel.setBackground( Color.white);
		referencePanel.setOpaque( false);

		JPanel centerPanel = new JPanel( new CenterLayout( CenterLayout.VERTICAL));
		centerPanel.add( titleField, CenterLayout.CENTER);
		centerPanel.setOpaque( false);

		contentPane.add( logoLabel, BorderLayout.NORTH);
		contentPane.add( centerPanel, BorderLayout.CENTER);
		contentPane.add( referencePanel, BorderLayout.SOUTH);

		setSize( 350, 230);
		
		setContentPane( contentPane);
	}
	
	/**
	 * Shows the status of the creation of the main application!
	 *
	 * @param status the description of the status.
	 * @param percentage the percentage of the status bar.
	 */
	public void showStatus( final String status, final int percentage)  {
		if (DEBUG) System.out.println("Splash.showStatus( "+status+", "+percentage+")");

		SwingUtilities.invokeLater( new SplashRunner( this) {
		    public void run() {
//				statusField.setText( status);
//				progressBar.setValue( percentage);
		    }
		});
	}

	/**
	 * Sets the foreground color of the splash screen.
	 *
	 * @param foreground the foreground color.
	 */
//	public void setForeground( Color foreground) {
//		copyrightField.setForeground( foreground);
//		statusField.setForeground( foreground);
//		
//		progressBar.setForeground( foreground);
//		progressField.setBorder( new CompoundBorder( 
//									new EmptyBorder( 4, 0, 4, 10),
//									new LineBorder( foreground, 1)));
//		productPanel.setBackground( foreground);
//		cancelButton.setForeground( foreground);
//		cancelButton.setBorder( new CompoundBorder( 
//									new LineBorder( foreground, 1),
//									new EmptyBorder( 0, 10, 0, 10)));
//		cancelButton.setPressedBackground( foreground);
//	}

	/**
	 * Sets the background color of the splash screen.
	 *
	 * @param background the background color.
	 */
//	public void setBackground( Color background) {
//		titleField.setForeground( background);
//		descriptionField.setForeground( background);
//		versionField.setForeground( background);
//		
//		contentPane.setBackground( background);
//		progressBar.setBackground( background);
//		progressPanel.setBackground( background);
//		progressField.setBackground( background);
//		cancelButton.setBackground( background);
//		cancelButton.setPressedForeground( background);
//	}

	/**
	 * Shows the splash screen for a certain time, needs to be run in a thread for this.
	 *
	 * @param duration the time the splash screen should be shown for.
	 */
	public void start( final long duration)  {
		if (DEBUG) System.out.println("Splash.start( "+duration+")");
		
		// Create a runnable object ...
		Runnable timer = new SplashRunner( this) {
			public void run()  {
				try  {
					getSplash().start();
					Thread.sleep( duration);
					getSplash().stop();
				} catch ( Exception e) {
					e.printStackTrace();
				}
			} 
		};

		// Create and start the thread ...
		Thread thread = new Thread( timer);
		thread.start();
	}

	/**
	 * Shows the splash screen.
	 **/
	public void start()  {
		if (DEBUG) System.out.println("Splash.start()");

		SwingUtilities.invokeLater( new SplashRunner( this) {
		    public void run() {
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

				int width = getSplash().getSize().width;
				int height = getSplash().getSize().height;
				
				getSplash().setLocation( (d.width - width)/2, (d.height - height)/2);

				//getSplash().setVisible(true);
				getSplash().show();
		    }
		});
	}

	/**
	 * Hide the splash screen.
	 **/
	public void stop()  {
		if (DEBUG) System.out.println("Splash.stop()");

		if ( isVisible()) {
			SwingUtilities.invokeLater( new SplashRunner( this) {
			    public void run() {
					//getSplash().setVisible( false);
			    	getSplash().hide();
					getSplash().dispose();
			    }
			});
		}
	}
	
	/**
	 * Hide the splash screen after x milli seconds.
	 *
	 * @param duration the time after which the splash screen should be hidden.
	 **/
	public void stop( final long duration)  {
		if (DEBUG) System.out.println("Splash.stop( "+duration+")");

		// Create a runnable object ...
		Runnable timer = new SplashRunner( this) {
			public void run()  {
				try  {
					Thread.sleep( duration);
					getSplash().stop();
				} catch ( Exception e) {
					e.printStackTrace();
				}
			} 
		};

		// Create and start the thread ...
		Thread thread = new Thread( timer);
		thread.start();
	}

	private abstract class SplashRunner implements Runnable {
		private Splash splash = null;
		
		/**
		 * Constructs a splash runner that contains the Splash object.
		 *
		 * @param the Splash screen.
		 **/
		public SplashRunner( Splash splash) {
			this.splash = splash;
		}
		
		/**
		 * Returns the Splash object.
		 *
		 * @return Splash.
		 **/
		public Splash getSplash() {
			return splash;
		}
		
		/**
		 * Abstract implementation of the run method.
		 **/
		public abstract void run();
	}
} 
