/*
 * $Id: FindInFilesAction.java,v 1.5 2005/09/07 16:20:52 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import org.bounce.DefaultFileFilter;

import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FindInFilesDialog;
import com.cladonia.xngreditor.XngrProgressDialog;
import com.cladonia.xngreditor.project.Finder;
import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * An action that can be used to search through files
 *
 * @version	$Revision: 1.5 $, $Date: 2005/09/07 16:20:52 $
 * @author Dogsbay
 */
public class FindInFilesAction extends AbstractAction {
 	private static final boolean DEBUG = false;

 	private ConfigurationProperties properties = null; 
 	private ExchangerEditor parent = null;
	private FindInFilesDialog findDialog = null;

 	private Project project = null;
 	private Vector totalMatches = null;
 	XngrProgressDialog progressDialog = null;
	
 	/**
	 * The constructor for the add action.
	 *
	 * @param editor the editor to copy information from.
	 */
 	public FindInFilesAction( ExchangerEditor parent, ConfigurationProperties props) {
 		super( "Find in Files ...");
 		
		this.parent = parent;
 		this.project = project;
		this.properties = props;

//		putValue( MNEMONIC_KEY, new Integer( 'V'));
//		putValue( ACCELERATOR_KEY, KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false));
//		putValue( SMALL_ICON, XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Copy16.gif"));
		putValue( SHORT_DESCRIPTION, "Find in Files ...");
		
		setEnabled( true);
 	}
 	
	/**
	 * The implementation of the copy action.
	 *
	 * @param e the action event.
	 */
 	public void actionPerformed( ActionEvent e) {
	 	if ( findDialog == null) {
	 		findDialog = new FindInFilesDialog( parent, properties);
	 	}
	 	
	 	findDialog.init();
		
	 	findDialog.setVisible( true);
	 	
	 	if ( !findDialog.isCancelled()) {
	 		final String search = findDialog.getSearch();
	 		final boolean matchCase = findDialog.isCaseSensitive();
	 		final boolean regExp = findDialog.isRegularExpression();
	 		final boolean wholeWord = findDialog.isMatchWholeWord();
	 		final FileFilter fileType = findDialog.getFileType();
	 		final Vector folders = findDialog.getFolder();

	 		if ( search != null) {
	 			properties.addSearch( search);
	 			properties.setMatchCase( matchCase);
	 			properties.setRegularExpression( regExp);
	 			properties.setMatchWholeWord( wholeWord);
	 			properties.setFindInFilesFolder(((File)folders.get(0)).toString());
				
				//project.findInFiles( search, regExp, matchCase, wholeWord);
	 			progressDialog  = new XngrProgressDialog(parent, true);
	 			progressDialog.setTitle("Find In Files");
	 			
				
				parent.setWait( true);
				parent.setStatus( "Searching ...");
				
				// Run in Thread!!!
				Runnable runner = new Runnable() {
					public void run()  {
						try {
							
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									progressDialog.monitor.setIndeterminate(true);
									progressDialog.setVisible(true);
								}
							});
							
							
				 			final Vector documents = getDocuments(folders, fileType);

							// reset previous find in files + select
				 			SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if(documents != null) {
										parent.getOutputPanel().startFindInFiles( "Searching in "+documents.size()+" Files For: \""+search+"\" ...");
										progressDialog.remakeMonitor(0, documents.size());
									}
								}
				 			});
							
				 			
							//final ProgressMonitor monitor = new ProgressMonitor( parent, "Searching for \""+search+"\" in:", "", 0, documents.size());
							//monitor.setMillisToDecideToPopup( 10);
							//monitor.setMillisToPopup( 10);
							
							
							totalMatches = new Vector();
							
							if(documents != null) {
								for ( int i = 0; i < documents.size(); i++) {
									try {
										final int cnt= i;
							 			final File doc = (File)documents.elementAt(i);
							 			SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												if(cnt == 0) {
													progressDialog.monitor.setValue( cnt+1);
												}
												progressDialog.label.setText( "Searching "+doc.getName()+"...");		
											}
										});
							 			
							 			URL origURL = null;
							 			URL newURL = null;
										try {
											
											newURL = com.cladonia.xml.XngrURLUtilities.getURLFromFile(doc);
											
											final Vector matches = Finder.find( newURL, search, regExp, matchCase, wholeWord);
											
											SwingUtilities.invokeLater( new Runnable() {
												public void run() {
													parent.getOutputPanel().addFindInFiles( matches);
																					 			
												}
											});
											
										} catch (Exception e) {
											try {
												origURL = com.cladonia.xml.XngrURLUtilities.getURLFromFile(doc);
												newURL = new URL(origURL.getProtocol(), "", encodeURL(origURL.getFile()));
												
	
												final Vector matches = Finder.find( newURL, search, regExp, matchCase, wholeWord);
												
												SwingUtilities.invokeLater( new Runnable() {
													public void run() {
														parent.getOutputPanel().addFindInFiles( matches);
																						 			
													}
												});
											}
											catch(Exception se) {
												
											}
										}
							 			
										if ( progressDialog.isCancelled()) {
							 				break;
							 			}
										
									}catch (Exception mue) {
										
									}finally {
										final int cnt= i;
										SwingUtilities.invokeLater(new Runnable() {
											public void run() {
												progressDialog.monitor.setValue( cnt+1);		
											}
										});
										
									}
									
						 		}
							}
							else {
								//user cancelled
							}
							
							
					 	} finally {
					 		
					 		

							parent.getOutputPanel().finishFindInFiles();
							
					 		parent.setStatus( "Done");
					 		parent.setWait( false);	
					 		
					 		SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									progressDialog.setVisible(false);
								}
							});
					 	}
					}
				};

				// Create and start the thread ...
				Thread thread = new Thread( runner);
				thread.start();
				
	 		}
	 	}
 	}
 	
 	public String encodeURL(String url1)
 	{
 		String url2 = "";
 		for (int i=0; i<url1.length(); i++)
 		{
 			if(url1.charAt(i) == '$') {
 				url2 += "%24";
 			}
 			else if(url1.charAt(i) == '&') {
 				url2 += "%26";
 			}
 			else if(url1.charAt(i) == '+') {
 				url2 += "%2B";
 			}
 			else if(url1.charAt(i) == ',') {
 				url2 += "%2C";
 			}
 			else if(url1.charAt(i) == '/') {
 				url2 += "%2F";
 			}
 			else if(url1.charAt(i) == ':') {
 				url2 += "%3A";
 			}
 			else if(url1.charAt(i) == ';') {
 				url2 += "%3B";
 			}
 			else if(url1.charAt(i) == '=') {
 				url2 += "%3D";
 			}
 			
 			else if(url1.charAt(i) == '?') {
 				url2 += "%3F";
 			}
 			else if(url1.charAt(i) == '@') {
 				url2 += "%40";
 			}
 			
 			else if (url1.charAt(i) == ' ') {
 				url2 += "%20";
 			}
 			else if(url1.charAt(i) == '"') {
 				url2 += "%22";
 			}
 			else if(url1.charAt(i) == '<') {
 				url2 += "%3C";
 			}
 			else if(url1.charAt(i) == '>') {
 				url2 += "%3E";
 			}
 			else if(url1.charAt(i) == '#') {
 				url2 += "%23";
 			}
 			else if(url1.charAt(i) == '%') {
 				url2 += "%25";
 			}
 			else if(url1.charAt(i) == '{') {
 				url2 += "%7B";
 			}
 			else if(url1.charAt(i) == '}') {
 				url2 += "%7D";
 			}
 			
 			
 			else if(url1.charAt(i) == '|') {
 				url2 += "%7C";
 			}
 			else if(url1.charAt(i) == '\\') {
 				url2 += "%5C";
 			}
 			else if(url1.charAt(i) == '^') {
 				url2 += "%5E";
 			}
 			else if(url1.charAt(i) == '~') {
 				url2 += "%7E";
 			}
 			else if(url1.charAt(i) == '[') {
 				url2 += "%5B";
 			}
 			else if(url1.charAt(i) == ']') {
 				url2 += "%5D";
 			}
 			else if(url1.charAt(i) == '`') {
 				url2 += "%60";
 			} 
 			else {
 				url2 += url1.charAt(i);
 			}
 			
 		}
 		
 		return url2;
 	}
 	
 	public Vector getDocuments(Vector folderVector, FileFilter fileFilter) {
 		
 		
 		Vector files = new Vector();
 		
 		for(int vcnt=0;vcnt<folderVector.size();++vcnt) {
 			
 			final File folder = (File) folderVector.get(vcnt);
 			SwingUtilities.invokeLater(new Runnable() {
 				public void run() {
 					progressDialog.label.setText("Building File List: "+folder.getPath());		
 				}
 			});
 			if(progressDialog.isCancelled() == false) { 
 			
	 			File[] allFiles;
	 	 		
	 	 		if(fileFilter != null) {
	 	 			allFiles = folder.listFiles(new FindFileFilter(fileFilter));
	 	 		}
	 	 		else {
	 	 			allFiles = folder.listFiles();
	 	 		}
	 	 		
		 		if(allFiles != null) {
			 		for(int cnt=0;cnt<allFiles.length;++cnt) {
			 			if(allFiles[cnt].isFile() == true) {
				 			files.add(allFiles[cnt]);
			 			}
			 			else if(allFiles[cnt].isDirectory() == true) {
			 				files = getDocumentsFromFolder(allFiles[cnt], fileFilter, files);
			 				if(files == null) {
			 					return(null);
			 				}
			 			}
			 		}
		 		}
 			}
 			else {
 				
 				return(null);
 			}
 		}
 		return(files);
 	}
 	
 	public Vector getDocumentsFromFolder(final File folder, FileFilter fileFilter, Vector files) {
 		
 		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				progressDialog.label.setText("Building File List: "+folder.getPath());		
			}
		});
 		
 		if(progressDialog.isCancelled() == false) { 
	 		File[] allFiles;
	 		
	 		if(fileFilter != null) {
	 			allFiles = folder.listFiles(new FindFileFilter(fileFilter));
	 		}
	 		else {
	 			allFiles = folder.listFiles();
	 		}
	 		if(allFiles != null) {
		 		for(int cnt=0;cnt<allFiles.length;++cnt) {
		 			if(allFiles[cnt].isFile() == true) {
			 			files.add(allFiles[cnt]);
		 			}
		 			else if(allFiles[cnt].isDirectory() == true) {
		 				files = getDocumentsFromFolder(allFiles[cnt], fileFilter, files);
		 				if(files == null) {
		 					return(null);
		 				}
		 			}
		 		}
	 		}
	 		
	 		return(files);
 		}
 		else{
 			return(null);
 		}
 	}
 	
 	public class FindFileFilter implements java.io.FileFilter {

 		private String description = null;
 	    private Vector extensions = null;
 	    private String extension = null;
 	    private FileFilter fileFilter = null;

 	    /**
 	     * Creates a file filter that accepts the given file types.
 		 * The extensions should be divided by any of the following 
 		 * tokens " \t\n\r\f;,.:".
 		 * 
 		 * @param extensions the extensions for the file-type.
 	     * @param description the description of the file-type.
 	     */
 	    public FindFileFilter( String extensions, String description) {
 			this.extensions = new Vector();
 			setExtensions( extensions);

 		 	this.description = description;
 	    }
 	    
 	    public FindFileFilter (DefaultFileFilter oldFileFilter) {
 	    	
 	    	this.extensions = oldFileFilter.getExtensions();
 	    	this.description = oldFileFilter.getDescription();
 	    }
 	    
 	   public FindFileFilter (FileFilter oldFileFilter) {
	    	
	    	fileFilter = oldFileFilter;
	    }

 		/**
 		 * Add an extension to the list of extensions.
 		 * 
 		 * @param extension the extension for the file-type without the '.'.
 		 */
 		public void addExtension( String extension) {
 			extensions.add( extension);
 		}

 		/**
 		 * Sets all possible extensions for this filter, 
 		 * divided by any of the following tokens " \t\n\r\f;,.:".
 		 * 
 		 * @param extensions the extensions for the file-type.
 		 */
 		public void setExtensions( String extensions) {
 			StringTokenizer tokenizer = new StringTokenizer( extensions, " \t\n\r\f;,.:");
 			
 			while ( tokenizer.hasMoreTokens()) {
 				this.extensions.add( tokenizer.nextToken());
 			}
 		}

 		// Implementation of FileFilter
 	    public boolean accept( File file) {
 			boolean result = false;
 			
 			if ( file != null) {
 				if( file.isFile()) {

 			    	String ext = getExtension( file);
 		    		if( ext != null && isSupportedExtension( ext, file)) {
 						result = true;
 		    		}
 				} else if ( file.isDirectory()) { 
 					result = true;
 				} 
 			}
 			
 			return result;
 	    }

 		// Implementation of FileFilter
 	    public String getDescription() {
 			StringBuffer result = new StringBuffer( description);
 			
 			if ( extensions.size() > 0) {
 				result.append(" (");
 				for ( int i = 0; i < extensions.size(); i++) {
 					if ( i > 0) {
 						result.append( ",");
 					}

 					result.append( "*.");
 					result.append( (String)extensions.elementAt(i));
 				}
 				result.append(")");
 			}
 			
 			return result.toString();
 	    }

 		private boolean isSupportedExtension( String extension, File file) {
 			if(fileFilter != null) {
 				return(fileFilter.accept(file));
 			}
 			else {
	 			for ( int i = 0; i < extensions.size(); i++) {
	// 				System.out.println( (String)extensions.elementAt(i)+" == "+extension);
	 				if ( ((String)extensions.elementAt(i)).equalsIgnoreCase( extension)) {
	 					return true;
	 				}
	 			}
	 			return(false);
 			}
 			
 		}
 		
 		private String getExtension( File file) {
 			if( file != null) {
 				String filename = file.getName();

 				int i = filename.lastIndexOf('.');

 				if( i > 0 && i < (filename.length() - 1)) {
 					return filename.substring( i+1).toLowerCase();
 				}
 			}

 			return null;
 		}
 		
 	}
}
