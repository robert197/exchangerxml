/*
 * $Id: MessageHandler.java,v 1.10 2005/09/01 08:57:26 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.util.StringTokenizer;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Handles messages.
 *
 * @version	$Revision: 1.10 $, $Date: 2005/09/01 08:57:26 $
 * @author Dogsbay
 */
 public class MessageHandler {
 	private static final boolean DEBUG = false;
 	private static final int MAX_ERROR_LENGTH = 80;

 	private static JFrame parent = null;
 	private static Object[] options = {"Yes", "No", "All"};
 	private static Object[] optionsNoToAll = {"Yes", "No", "No To All", "Cancel"};
 	
 	public static final int CONFIRM_YES_OPTION = JOptionPane.YES_OPTION; 	//0
 	public static final int CONFIRM_NO_OPTION = JOptionPane.NO_OPTION;		//1
 	public static final int CONFIRM_CANCEL_OPTION = JOptionPane.CANCEL_OPTION;	//2
 	public static final int CONFIRM_ALL_OPTION = 3;
 	public static final int CONFIRM_NO_TO_ALL_OPTION = 4;
 	
 	
 	

 	/**
	 * initialises the message handler.
	 *
	 * @param parent the parent frame.
	 */
 	public static void init( JFrame p) {
	 	parent = p;
		
//		GregorianCalendar date = new GregorianCalendar( 2003, 5, 13);
//
//
//		// this gives a warning...
//		if ( date.after( GregorianCalendar.getInstance())) {
//			showMessage( "<html><font color=\"#FF0000\">This evaluation version expires at the 13th of June 2003!</font><br><br>"+
//						 "Thank you for trying-out the eXchaNGeR - XML Editor.<br><br>"+
//						 "For more information about this and other products,<br>"+
//						 "please visit us at: http://www.cladonia.com/</html>");
//		} else {
//			showMessage( "This evaluation version has expired!\n\n"+
//						 "Please download a new version from:\n"+
//						 "http://www.cladonia.com/");
//			System.exit(0);
//		}
 	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param message the message string.
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showError( JFrame frame, String message, String type) {
		showError( frame, message, null, type);

	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param message the message string.
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showError( String message, String type) {
		showError( parent, message, null, type);

	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param message the message string.
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showError( final String message, final Throwable e, final String type) {
		showError( parent, message, e, type);
	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param message the message string.
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showError( final JFrame frame, final String message, final Throwable e, final String type) {
		if ( SwingUtilities.isEventDispatchThread()) {
			if ( e != null) {
				if (DEBUG) e.printStackTrace();
				
				String m = "";
				
				if ( !StringUtilities.isEmpty( e.getMessage())) {
					m = "\n"+wrap( e.getMessage());
				}

				JOptionPane.showMessageDialog( frame, message+m, type, JOptionPane.ERROR_MESSAGE);
			} else {
				JOptionPane.showMessageDialog( frame, message, type, JOptionPane.ERROR_MESSAGE);
			}
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					if ( e != null) {
						if (DEBUG) e.printStackTrace();
						String m = "";
						
						if ( !StringUtilities.isEmpty( e.getMessage())) {
							m = "\n"+wrap( e.getMessage());
						}

						JOptionPane.showMessageDialog( frame, message+m, type, JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog( frame, message, type, JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		}
	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showError( Throwable e, String type) {
		if(e != null) {
			showError( parent, wrap( e.getMessage()), null, type);			
		}
		else {
			showError( parent, "", null, type);
		}
		if (DEBUG) e.printStackTrace();
	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showError( JFrame frame, Throwable e, String type) {
		showError( frame, wrap( e.getMessage()), null, type);
		if (DEBUG) e.printStackTrace();
	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param message the message string.
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showUnexpectedError( Exception e) {
		showUnexpectedError( parent, e);
	}

	/**
	 * Shows an error message dialog.
	 *
	 * @param message the message string.
	 * @param e the exception.
	 * @param type the error type.
	 *
	 * note: this method is thread safe!
	 */
	public static void showUnexpectedError( final JFrame frame, final Exception e) {
		if ( SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog( frame, wrap( e.getMessage()), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog( frame, wrap( e.getMessage()), "Unexpected Error", JOptionPane.ERROR_MESSAGE);
				}
			});
		}

		if (DEBUG) e.printStackTrace();
	}

	/**
	 * Shows a confirm message dialog.
	 *
	 * @param message the message string.
	 */
	public static int showConfirm( String message) {
		return showConfirm( parent, message);
	}

	public static int showConfirm( JFrame frame, String message) {
		return JOptionPane.showConfirmDialog( frame, message, "Please Confirm", JOptionPane.YES_NO_OPTION);
	}

	/**
	 * Shows a confirm message dialog with a cancel option.
	 *
	 * @param message the message string.
	 */
	public static int showConfirmCancel( String message) {
		return showConfirmCancel( parent, message);
	}

	public static int showConfirmCancel( JFrame frame, String message) {
		return JOptionPane.showConfirmDialog( frame, message, "Please Confirm", JOptionPane.YES_NO_CANCEL_OPTION);
	}

	/**
	 * Shows a confirm message dialog with a cancel option.
	 *
	 * @param message the message string.
	 */
	public static int showConfirmOkCancel( String message) {
		return showConfirmOkCancel( parent, message);
	}

	public static int showConfirmOkCancel( JFrame frame, String message) {
		return JOptionPane.showConfirmDialog( frame, message, "Please Confirm", JOptionPane.OK_CANCEL_OPTION);
	}
	
	
	/**
	 * Shows a confirm message dialog with a yes, no and all option.
	 *
	 * @param message the message string.
	 */
	public static int showConfirmYesNoAll( JFrame frame, String message) {
	    int result = JOptionPane.showOptionDialog(frame, message,"Please Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
                						JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
		//return JOptionPane.showConfirmDialog( frame, message, "Please Confirm", JOptionPane.OK_CANCEL_OPTION);
	    if(result == 0) {
	    	return(CONFIRM_YES_OPTION);
	    }
	    else if(result == 1) {
	    	return(CONFIRM_NO_OPTION);
	    }
	    else if(result == 2) {
	    	return(CONFIRM_ALL_OPTION);
	    }
	    else {
	    	return(-1);
	    }
	}
	
	/**
	 * Shows a confirm message dialog with a yes, no and no to all option.
	 *
	 * @param message the message string.
	 */
	public static int showConfirmYesNoNoToAll( JFrame frame, String message) {
	    int result = JOptionPane.showOptionDialog(frame, message,"Please Confirm", JOptionPane.YES_NO_CANCEL_OPTION,
                						JOptionPane.QUESTION_MESSAGE,null,optionsNoToAll,optionsNoToAll[0]);
	    
	    if(result == 0) {
	    	return(CONFIRM_YES_OPTION);
	    }
	    else if(result == 1) {
	    	return(CONFIRM_NO_OPTION);
	    }
	    else if(result == 2) {
	    	return(CONFIRM_NO_TO_ALL_OPTION);
	    }
	    else if(result == 3) {
	    	return(CONFIRM_CANCEL_OPTION);
	    }
	    else {
	    	return(-1);
	    }
		//return JOptionPane.showConfirmDialog( frame, message, "Please Confirm", JOptionPane.OK_CANCEL_OPTION);
	}

	/**
	 * Shows a information message.
	 *
	 * @param message the message string.
	 */
	public static void showMessage( String message) {
		showMessage( parent, message);
	}
	
	/**
	 * Shows a information message.
	 *
	 * @param message the message string.
	 */
	public static void showMessage( String message, String title) {
		showMessage( parent, message,title);
	}

	/**
	 * Shows a information message.
	 *
	 * @param message the message string.
	 */
	public static void showMessage( final JFrame frame, final String message) {
		if ( SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog( frame, message);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog( frame, message);
				}
			});
		}
	}
	
	/**
	 * Shows a information message.
	 *
	 * @param message the message string.
	 */
	public static void showMessage( final JFrame frame, final String message, final String title) {
		if ( SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog( frame, message, title,JOptionPane.INFORMATION_MESSAGE);
		} else {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					JOptionPane.showMessageDialog( frame, message,title,JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}
	
	private static String wrap( String message) {
        StringTokenizer tokenizer = new StringTokenizer( message);
        StringBuffer result = new StringBuffer();
        StringBuffer line = new StringBuffer();
		
        while ( tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            
            if ( line.length() > 0 && ((line.length() + token.length()) > MAX_ERROR_LENGTH)) {
            	result.append( line);
            	result.append( "\n");
            	line = new StringBuffer();
            }

            line.append( token);
        	line.append( " ");
        }
        
        result.append( line);

        return result.toString();
	}
}
