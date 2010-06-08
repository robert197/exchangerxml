/*
 * $Id: DocumentNode.java,v 1.3 2004/05/23 14:40:55 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import javax.swing.Icon;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeModel;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerDocumentEvent;
import com.cladonia.xml.ExchangerDocumentListener;
import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.grammar.GrammarProperties;

/**
 * The default node for a root node for documents.
 *
 * @version	$Revision: 1.3 $, $Date: 2004/05/23 14:40:55 $
 * @author Dogsbay
 */
public class DocumentNode extends BaseNode implements ExchangerDocumentListener {

	private static boolean showFullPath = false;

	private DocumentProperties properties = null;
	private ExchangerDocument document = null;
	private DefaultTreeModel model = null;

	/**
	 * The constructor for a document node.
	 *
	 * @param doc the document for the node.
	 */	
	public DocumentNode( DefaultTreeModel model, DocumentProperties properties) {
		this.properties = properties;
		this.model = model;
	}
	
	/**
	 * Sets the grammar properties.
	 *
	 * @param props the grammar properties.
	 */	
	public void setType( GrammarProperties props) {
		if ( props != null) {
			properties.setType( props.getID());
//		} else if ( props.getType() == null || props) {
//			properties.setType( "");
		}
	}

	/**
	 * Gets the document for this node.
	 *
	 * @return the document.
	 */	
	public void setDocument( ExchangerDocument document) {
		if ( this.document != null) {
			this.document.removeListener( this);
		}
		
		this.document = document;
		
		if ( this.document != null) {
			this.document.addListener( this);

			if ( properties.getState() == DocumentProperties.STATE_UNKNOWN) {
				updateState();
			}

			properties.setReadOnly( document.isReadOnly());
			
			if ( SwingUtilities.isEventDispatchThread()) {
				model.nodeChanged( this);
			} else {
				// perform on event dispatch thread...
				SwingUtilities.invokeLater( new Runnable() {
				    public void run() {
						model.nodeChanged( DocumentNode.this);
				    }
				});
			}
//			properties.setName( document.getName());
//			properties.setURL( document.getURL());
		}
	}

// Implementation of the ExchangerDocumentListener
	public void documentUpdated( ExchangerDocumentEvent e) {
		if ( e.getType() == ExchangerDocumentEvent.SAVED) {
			updateState();

			SwingUtilities.invokeLater( new Runnable() {
			    public void run() {
					model.nodeChanged( DocumentNode.this);
			    }
			});
//		} else if ( document.isReadOnly()) {
//			updateState();
		}
		
		// perform on event dispatch thread...
	}
	
	private void updateState() {

		if ( document.isError() && document.isXML()) {
			properties.setState( DocumentProperties.STATE_ERROR);
//		} else if ( document.isValid()) {
//			properties.setState( DocumentProperties.STATE_VALID);
		} else if ( document.isXML()) {
			properties.setState( DocumentProperties.STATE_WELLFORMED);
		} else {
			properties.setState( DocumentProperties.STATE_UNKNOWN);
		}
	}


	/**
	 * Gets the document for this node.
	 *
	 * @return the document.
	 */	
	public DocumentProperties getProperties() {
		return properties;
	}

	/**
	 * Parses the document for the node.
	 */	
	public void parse() {
		boolean isError = false;
		ExchangerDocument doc = null;
		
		try {
			doc = new ExchangerDocument( properties.getURL());
			doc.load();
		} catch (Exception e) {}
		
		if ( doc.isError() && doc.isXML()) {
			properties.setState( DocumentProperties.STATE_ERROR);
		} else if ( doc.isXML()) {
			properties.setState( DocumentProperties.STATE_WELLFORMED);
		} else {
			properties.setState( DocumentProperties.STATE_UNKNOWN);
		}

		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				model.nodeChanged( DocumentNode.this);
			}
		});
	}

	/**
	 * Validates the document for the node.
	 */	
	public void validate() {
		boolean isError = false;
		ExchangerDocument doc = null;
		
		try {
			doc = new ExchangerDocument( properties.getURL());
			doc.load();
//			XMLUtilities.validate( properties.getURL());
		} catch (Exception e) {
		}
		
		if ( doc.isError() && doc.isXML()) {
			properties.setState( DocumentProperties.STATE_ERROR);
		} else if ( doc.isXML()) {
			boolean valid = true;

			try {
				doc.validate( new SimpleErrorHandler());
			} catch (Exception e) {
				e.printStackTrace();
				valid = false;
			}
			
			if (!valid) {
				properties.setState( DocumentProperties.STATE_WELLFORMED);
			} else {
				properties.setState( DocumentProperties.STATE_VALID);
			}
		} else {
			properties.setState( DocumentProperties.STATE_UNKNOWN);
		}

		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				model.nodeChanged( DocumentNode.this);
			}
		});
	}

	/**
	 * Gets the name for this node.
	 *
	 * @return the name.
	 */	
	public String getName() {
		if ( showFullPath) {
			return URLUtilities.toRelativeString( properties.getURL());
		} else {
			return properties.getName();
		}
	}

	/**
	 * Gets the description for this node.
	 * This description is used for the tooltip text.
	 *
	 * @return the description.
	 */	
	public String getDescription() {
		return URLUtilities.toRelativeString( properties.getURL());
	}

	/**
	 * Returns the icon that is shown when the node is expanded and selected.
	 *
	 * @return the selected expanded icon.
	 */
	public Icon getExpandedSelectedIcon() {
		return getSelectedIcon();
	}


	/**
	 * Returns the icon that is shown when the node is expanded.
	 *
	 * @return the expanded icon.
	 */
	public Icon getExpandedIcon() {
		return getIcon();
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public Icon getSelectedIcon() {
		Icon icon = null;
		String type = properties.getType();
		int state = properties.getState();
		
		if ( type != null && type.length() > 0) {
			if ( properties.isRemote()) {
				if ( state == DocumentProperties.STATE_ERROR) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_ERROR);
				} else if ( state == DocumentProperties.STATE_WELLFORMED) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_WELLFORMED);
				} else if ( state == DocumentProperties.STATE_VALID) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_VALID);
				} else if ( state == DocumentProperties.STATE_UNKNOWN) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL);
				}
			} else if ( properties.isReadOnly()) {
				if ( state == DocumentProperties.STATE_ERROR) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_ERROR);
				} else if ( state == DocumentProperties.STATE_WELLFORMED) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_WELLFORMED);
				} else if ( state == DocumentProperties.STATE_VALID) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_VALID);
				} else if ( state == DocumentProperties.STATE_UNKNOWN) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL);
				}
			} else  {
				if ( state == DocumentProperties.STATE_ERROR) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_ERROR);
				} else if ( state == DocumentProperties.STATE_WELLFORMED) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_WELLFORMED);
				} else if ( state == DocumentProperties.STATE_VALID) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_VALID);
				} else if ( state == DocumentProperties.STATE_UNKNOWN) {
					icon = IconFactory.getDarkerIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL);
				}
			}
		} else { // get the icon for the extension...
			String location = properties.getURL().toString();
			String extension = "";
			
			int pos = location.lastIndexOf( ".");

			if ( pos != -1 && ((pos+1) < location.length())) {
			    extension = location.substring( pos+1, location.length());
			}
			
			String xmlStatus = IconFactory.XML_STATUS_NORMAL;
			
			if ( state == DocumentProperties.STATE_ERROR) {
				xmlStatus = IconFactory.XML_STATUS_ERROR;
			} else if ( state == DocumentProperties.STATE_WELLFORMED) {
				xmlStatus = IconFactory.XML_STATUS_WELLFORMED;
			} else if ( state == DocumentProperties.STATE_VALID) {
				xmlStatus = IconFactory.XML_STATUS_VALID;
			} else if ( state == DocumentProperties.STATE_UNKNOWN) {
				xmlStatus = IconFactory.XML_STATUS_NORMAL;
			}

			if ( properties.isRemote()) {
				icon = IconFactory.getDarkerIconForExtension( extension, IconFactory.FILE_STATUS_REMOTE, xmlStatus);
			} else if ( properties.isReadOnly()) {
				icon = IconFactory.getDarkerIconForExtension( extension, IconFactory.FILE_STATUS_READ_ONLY, xmlStatus);
			} else  {
				icon = IconFactory.getDarkerIconForExtension( extension, IconFactory.FILE_STATUS_NORMAL, xmlStatus);
			}
		}

		return icon;
	}

	/**
	 * Gets the default document icon for this node.
	 *
	 * @return the document icon.
	 */	
	public Icon getIcon() {
//		Icon icon = null;
//		int state = properties.getState();
//		
//		if ( properties.isRemote()) {
//			if ( state == DocumentProperties.STATE_ERROR) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_ERROR);
//			} else if ( state == DocumentProperties.STATE_WELLFORMED) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL);
//			} else if ( state == DocumentProperties.STATE_UNKNOWN) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_UNKNOWN);
//			}
//		} else if ( properties.isReadOnly()) {
//			if ( state == DocumentProperties.STATE_ERROR) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_ERROR);
//			} else if ( state == DocumentProperties.STATE_WELLFORMED) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL);
//			} else if ( state == DocumentProperties.STATE_UNKNOWN) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_UNKNOWN);
//			}
//		} else  {
//			if ( state == DocumentProperties.STATE_ERROR) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_ERROR);
//			} else if ( state == DocumentProperties.STATE_WELLFORMED) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL);
//			} else if ( state == DocumentProperties.STATE_UNKNOWN) {
//				icon = IconFactory.getIcon( IconFactory.DOCUMENT_TYPE_XML, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_UNKNOWN);
//			}
//		}
		
		Icon icon = null;
		String type = properties.getType();
		int state = properties.getState();
		
		if ( type != null && type.length() > 0) {
			if ( properties.isRemote()) {
				if ( state == DocumentProperties.STATE_ERROR) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_ERROR);
				} else if ( state == DocumentProperties.STATE_WELLFORMED) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_WELLFORMED);
				} else if ( state == DocumentProperties.STATE_VALID) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_VALID);
				} else if ( state == DocumentProperties.STATE_UNKNOWN) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_REMOTE, IconFactory.XML_STATUS_NORMAL);
				}
			} else if ( properties.isReadOnly()) {
				if ( state == DocumentProperties.STATE_ERROR) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_ERROR);
				} else if ( state == DocumentProperties.STATE_WELLFORMED) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_WELLFORMED);
				} else if ( state == DocumentProperties.STATE_VALID) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_VALID);
				} else if ( state == DocumentProperties.STATE_UNKNOWN) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_READ_ONLY, IconFactory.XML_STATUS_NORMAL);
				}
			} else  {
				if ( state == DocumentProperties.STATE_ERROR) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_ERROR);
				} else if ( state == DocumentProperties.STATE_WELLFORMED) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_WELLFORMED);
				} else if ( state == DocumentProperties.STATE_VALID) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_VALID);
				} else if ( state == DocumentProperties.STATE_UNKNOWN) {
					icon = IconFactory.getIconForType( type, IconFactory.FILE_STATUS_NORMAL, IconFactory.XML_STATUS_NORMAL);
				}
			}
		} else { // get the icon for the extension...
			String location = properties.getURL().toString();
			String extension = "";
			
			int pos = location.lastIndexOf( ".");

			if ( pos != -1 && ((pos+1) < location.length())) {
			    extension = location.substring( pos+1, location.length());
			}
			
			String xmlStatus = IconFactory.XML_STATUS_NORMAL;
			
			if ( state == DocumentProperties.STATE_ERROR) {
				xmlStatus = IconFactory.XML_STATUS_ERROR;
			} else if ( state == DocumentProperties.STATE_WELLFORMED) {
				xmlStatus = IconFactory.XML_STATUS_WELLFORMED;
			} else if ( state == DocumentProperties.STATE_VALID) {
				xmlStatus = IconFactory.XML_STATUS_VALID;
			} else if ( state == DocumentProperties.STATE_UNKNOWN) {
				xmlStatus = IconFactory.XML_STATUS_NORMAL;
			}

			if ( properties.isRemote()) {
				icon = IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_REMOTE, xmlStatus);
			} else if ( properties.isReadOnly()) {
				icon = IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_READ_ONLY, xmlStatus);
			} else  {
				icon = IconFactory.getIconForExtension( extension, IconFactory.FILE_STATUS_NORMAL, xmlStatus);
			}
		}

		return icon;
	}
	
	public static void setShowFullPath( boolean showFull) {
		showFullPath = showFull;
	}

	public static boolean isShowFullPath() {
		return showFullPath;
	}
	
	private static class SimpleErrorHandler implements ErrorHandler {
		public void warning( SAXParseException exception) throws SAXException {
			// do nothing
		} 

		public void error( SAXParseException exception) throws SAXException {
		    throw exception;
		} 

		public void fatalError( SAXParseException exception) throws SAXException {
		    throw exception;
		} 
	}
} 
