/*
 * $Id: PreviewDialog.java,v 1.2 2004/04/30 11:06:44 edankert Exp $
 * Copyright (C) 2001-2002 The Apache Software Foundation. All rights reserved.
 * For details on use and redistribution please refer to the
 * LICENSE file included with these sources.
 */

package com.cladonia.xml.transform;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.fop.apps.AWTStarter;
import org.apache.fop.messaging.MessageEvent;
import org.apache.fop.messaging.MessageListener;
import org.apache.fop.render.awt.AWTRenderer;
import org.apache.fop.viewer.ProgressListener;
import org.bounce.CenterLayout;

import com.cladonia.xngreditor.XngrImageLoader;


/**
 * Dialog and User Interface for Preview
 */
public class PreviewDialog extends JDialog implements ProgressListener, MessageListener {
	private static final int DEFAULT_ZOOM_INDEX = 3;

    private JFrame parent;

    private int currentPage = 0;
	private double[] zoom = { 25.0, 50.0, 75.0, 100.0, 150.0, 200.0 };
	private int zoomIndex = DEFAULT_ZOOM_INDEX;

    private AWTRenderer renderer;
    private AWTStarter starter;

    protected JToolBar toolBar = new JToolBar();

    protected PreviewAction printAction;
    protected PreviewAction firstPageAction;
    protected PreviewAction previousPageAction;
    protected PreviewAction nextPageAction;
    protected PreviewAction lastPageAction;
    protected PreviewAction zoomInAction;
    protected PreviewAction zoomOutAction;

	protected JLabel statisticsStatus = new JLabel();
	protected JLabel previewImageLabel = new JLabel();
	protected JButton closeButton = new JButton( "Close");

    /**
     * Create a new PreviewDialog that uses the given renderer and translator.
     *
     * @param aRenderer the to use renderer
     * @param aRes the to use translator
     */
    public PreviewDialog( JFrame parent, AWTRenderer aRenderer) {
		super( parent);
		
		this.parent = parent;
		renderer = aRenderer;
		
		setModal( false);
		
        printAction = new PreviewAction( "Print", "Print FO", 'P', "com/cladonia/xml/transform/icons/Print16.gif") {
            public void execute() {
                print();
            }
        };

        firstPageAction = new PreviewAction( "First Page", "Go to First Page", 'F', "com/cladonia/xml/transform/icons/First16.gif") {
            public void execute() {
                goToFirstPage();
            }
        };

        previousPageAction = new PreviewAction( "Previous Page", "Go to Previous Page", 'r', "com/cladonia/xml/transform/icons/Previous16.gif") {
            public void execute() {
                goToPreviousPage();
            }
        };

        nextPageAction = new PreviewAction( "Next Page", "Go to Next Page", 'N', "com/cladonia/xml/transform/icons/Next16.gif") {
            public void execute() {
                goToNextPage();
            }
        };

        lastPageAction = new PreviewAction( "Last Page", "Go to Last Page", 'L', "com/cladonia/xml/transform/icons/Last16.gif") {
            public void execute() {
                goToLastPage();
            }
        };

        zoomInAction = new PreviewAction( "Zoom In", "Zoom In", 'I', "com/cladonia/xml/transform/icons/ZoomIn16.gif") {
            public void execute() {
                zoomIn();
            }
        };

        zoomOutAction = new PreviewAction( "Zoom Out", "Zoom Out", 'O', "com/cladonia/xml/transform/icons/ZoomOut16.gif") {
            public void execute() {
                zoomOut();
            }
        };

        this.setSize( new Dimension(640, 480));
//        previewArea.setMinimumSize( new Dimension(50, 50));

        this.setTitle( "FOP Output Window");

        renderer.setScaleFactor( 100.0);

		JPanel toolbarPanel = new JPanel( new BorderLayout());
		toolbarPanel.add( toolBar, BorderLayout.WEST);

        this.getContentPane().add( toolbarPanel, BorderLayout.NORTH);
		toolBar.setFloatable( false);
		toolBar.setRollover( true);

        toolBar.add( printAction);
        toolBar.addSeparator();
        toolBar.add( firstPageAction);
        toolBar.add( previousPageAction);
        toolBar.add( nextPageAction);
        toolBar.add( lastPageAction);
        toolBar.addSeparator();
        toolBar.add( zoomOutAction);
        toolBar.add( zoomInAction);

        toolbarPanel.add( statisticsStatus, BorderLayout.CENTER);

        JScrollPane previewArea = new JScrollPane();
        
		this.getContentPane().add( previewArea, BorderLayout.CENTER);
		
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				hide();
				//setVisible(false);
				dispose();
			}
		});
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 3, 0));
		buttonPanel.add( closeButton);
				
		getRootPane().setDefaultButton( closeButton);

		this.getContentPane().add( buttonPanel, BorderLayout.SOUTH);
		
		statisticsStatus.setHorizontalAlignment( JLabel.RIGHT);

		JPanel panel = new JPanel( new CenterLayout());
		panel.add( previewImageLabel, CenterLayout.CENTER);
        previewArea.getViewport().add( panel);

	    setDefaultCloseOperation( DISPOSE_ON_CLOSE);

	    showPage();
    }

    /**
     * Change the current visible page
     *
     * @param number the page number to go to
     */
    private void goToPage( int number) {
        currentPage = number;

        renderer.setPageNumber( number);

        showPage();
    }

    /**
     * Shows the previous page.
     */
    private void goToPreviousPage() {
        if (currentPage > 0) {
	        currentPage--;

    	    goToPage( currentPage);
        }
    }

    /**
     * Shows the next page.
     */
    private void goToNextPage() {
        if ( currentPage < renderer.getPageCount() - 1) {
	        currentPage++;

    	    goToPage( currentPage);
        }
    }

    /**
     * Shows the last page.
     */
    private void goToLastPage() {
        if ( currentPage != renderer.getPageCount() - 1) {
	        currentPage = renderer.getPageCount() - 1;

    	    goToPage(currentPage);
        }
    }

    /**
     * This class is used to reload document  in
     * a thread safe way.
     */
//    private class Reloader extends Thread {
//        public void run() {
//            previewImageLabel.setIcon( null);
//
//            //Cleans up renderer
//            while ( renderer.getPageCount() != 0) {
//                renderer.removePage(0);
//            }
//			
//            try {
//                starter.run();
//            } catch (FOPException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * Shows a page by number.
     */
//    private void goToPage(ActionEvent e) {
//        GoToPageDialog d = new GoToPageDialog( this,
//                                              res.getString("Go to Page"),
//                                              true,
//                                              res);
//        d.setLocation((int)getLocation().getX() + 50,
//                      (int)getLocation().getY() + 50);
//        d.show();
//        currentPage = d.getPageNumber();
//
//        if (currentPage < 1 || currentPage > pageCount)
//            return;
//
//        currentPage--;
//
//        goToPage(currentPage);
//    }

    /**
     * Shows the first page.
     */
    private void goToFirstPage() {
        if (currentPage > 0) {
	        currentPage = 0;

    	    goToPage(currentPage);
        }
    }

    private void print() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        // Not necessary, Pageable gets a Printable.
        // pj.setPrintable(renderer);
        pj.setPageable(renderer);

        if (pj.printDialog()) {
            try {
                pj.print();
            } catch (PrinterException pe) {
                pe.printStackTrace();
            }
        }
    }

    private void zoomIn() {
		if ( zoomIndex < (zoom.length - 1)) {
			zoomIndex++;

			renderer.setScaleFactor( zoom[ zoomIndex]);
			showPage();
		}
    }

    private void zoomOut() {
    	if ( zoomIndex > 0) {
    		zoomIndex--;

    		renderer.setScaleFactor( zoom[ zoomIndex]);
    		showPage();
    	}
    }

    public void progress(int percentage) {
        progress(new String(percentage + "%"));
    }

    public void progress(int percentage, String message) {
        progress(new String(message + " " + percentage + "%"));
    }

    /**
     * Setting the text  of a JLabel is not thread save, it
     * needs to be done in  the EventThread. Here we make sure
     * it is done.
     */
    public void progress(String message) {
        SwingUtilities.invokeLater(new showProgress(message, false));
    }

    /**
     * This class is used to show status and error messages in
     * a thread safe way.
     */
    class showProgress implements Runnable {
        /**
         * The message to display
         */
        Object message;

        /**
         * Is this an errorMessage, i.e. should it be shown in
         * an JOptionPane or in the status bar.
         */
        boolean isErrorMessage = false;

        /**
         * Constructs  showProgress thread
         * @param message message to display
         * @param isErrorMessage show in status bar or in JOptionPane
         */
        public showProgress(Object message, boolean isErrorMessage) {
            this.message = message;
            this.isErrorMessage = isErrorMessage;
        }

        public void run() {
            if (isErrorMessage) {
                JOptionPane.showMessageDialog( parent, message, "Error",
                                              JOptionPane.ERROR_MESSAGE);
            } else {
//                processStatus.setText(message.toString());
            }
        }
    }

    public void showPage() {
        showPageImage viewer = new showPageImage();

        if (SwingUtilities.isEventDispatchThread()) {
            viewer.run();
        } else
            SwingUtilities.invokeLater(viewer);
    }

    /**
     * This class is used to update the page image
     * in a thread safe way.
     */
    class showPageImage implements Runnable {

        /**
         * The run method that does the actuall updating
         */
        public void run() {
//			System.out.println("run >>>");
            BufferedImage pageImage = null;
            Graphics graphics = null;

            renderer.render(currentPage);
            pageImage = renderer.getLastRenderedPage();
            if (pageImage == null)
                return;
            graphics = pageImage.getGraphics();
            graphics.setColor(Color.black);
            graphics.drawRect(0, 0, pageImage.getWidth() - 1,
                              pageImage.getHeight() - 1);

            previewImageLabel.setIcon(new ImageIcon(pageImage));

            statisticsStatus.setText( "Page "+(currentPage + 1)+" of "+renderer.getPageCount()+" ("+(int)zoom[ zoomIndex]+"%)");
//	        System.out.println("<<< run");
        }
    }

    /**
     * Called by MessageHandler if an error message or a
     * log message is received.
     */
    public void processMessage(MessageEvent event) {
//        String error = event.getMessage();
//        String text = processStatus.getText();
//        FontMetrics fmt = processStatus.getFontMetrics(processStatus.getFont());
//      int width = processStatus.getWidth() - fmt.stringWidth("...");
//        showProgress showIt;
//
//        if (event.getMessageType() == event.LOG) {
//            if (!text.endsWith("\n")) {
//                text = text + error;
//                while (fmt.stringWidth(text) > width) {
//                    text = text.substring(1);
//                    width = processStatus.getWidth() - fmt.stringWidth("...");
//                }
//            } else
//                text = error;
//            progress(text);
//        } else {
//            error = error.trim();
//            if (error.equals(">")) {
//                text = text + error;
//                while (fmt.stringWidth(text) > width) {
//                    text = text.substring(1);
//                    width = processStatus.getWidth() - fmt.stringWidth("...");
//                }
//                progress(processStatus.getText() + error);
//                return;
//            }
//            if (error.equals(""))
//                return;
//            if (error.length() < 60) {
//                showIt = new showProgress(error, true);
//            } else {
//                StringTokenizer tok = new StringTokenizer(error, " ");
//                Vector labels = new Vector();
//                StringBuffer buffer = new StringBuffer();
//                String tmp, list[];
//
//                while (tok.hasMoreTokens()) {
//                    tmp = tok.nextToken();
//                    if ((buffer.length() + tmp.length() + 1) < 60) {
//                        buffer.append(" ").append(tmp);
//                    } else {
//                        labels.add(buffer.toString());
//                        buffer = new StringBuffer();
//                        buffer.append(tmp);
//                    }
//                }
//                labels.add(buffer.toString());
//                list = new String[labels.size()];
//                for (int i = 0; i < labels.size(); i++) {
//                    list[i] = labels.get(i).toString();
//                }
//                showIt = new showProgress(list, true);
//            }
//            if (SwingUtilities.isEventDispatchThread()) {
//                showIt.run();
//            } else {
//                try {
//                    SwingUtilities.invokeAndWait(showIt);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    progress(event.getMessage());
//                }
//            }
//        }
    }

    public void reportException(Exception e) {
        String msg = "An exception has occured";
        progress(msg);
        JOptionPane.showMessageDialog(
            getContentPane(),
            "<html><b>" + msg + ":</b><br>"
             + e.getClass().getName() + "<br>" + e.getMessage() + "</html>", "Fatal error",
             JOptionPane.ERROR_MESSAGE
        );
    }
	
    public abstract class PreviewAction extends AbstractAction {
	   	public PreviewAction( String title, String description, char mnemonic, String icon) {
    		super( title);
	   
	    	putValue( MNEMONIC_KEY, new Integer( mnemonic));
	    	putValue( SMALL_ICON, XngrImageLoader.get().getImage( icon));
	    	putValue( SHORT_DESCRIPTION, description);
    	}

    	public void actionPerformed( ActionEvent e) {
			execute();
    	}
		
		public abstract void execute();
    }
}    // class PreviewDialog
