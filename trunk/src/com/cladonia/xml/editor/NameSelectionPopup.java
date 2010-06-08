/*
 * $Id: NameSelectionPopup.java,v 1.12 2005/06/27 11:12:57 tcurley Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.bounce.event.DoubleClickListener;

import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.AttributeValue;
import com.cladonia.schema.ElementInformation;
import com.cladonia.schema.SchemaAttribute;
import com.cladonia.xml.CommonEntities;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The context sensitive element, attribute, entity selection popup.
 *
 * @version	$Revision: 1.12 $, $Date: 2005/06/27 11:12:57 $
 * @author Dogsbay
 */
public class NameSelectionPopup extends JPopupMenu {

	private static final ImageIcon ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ElementIcon.gif");
	private static final ImageIcon TEXT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExclamationIcon.gif");
	private static final ImageIcon ENTITY_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/EntityIcon.gif");
	private static final ImageIcon NAMESPACE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NamespaceIcon.gif");
	private static final ImageIcon ATTRIBUTE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/AttributeIcon.gif");
	private static final ImageIcon ATTRIBUTE_REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/RequiredAttributeIcon.gif");
	
	public static final String SWAP_ENTITY_STRING 	= "<- swap ->";
	public static final String MORE_ENTITIES_STRING = "more...";

	public static final int POPUP_TYPE_ELEMENT 		= 0;
	public static final int POPUP_TYPE_ATTRIBUTE	= 1;
	public static final int POPUP_TYPE_ENTITY 		= 2;
	public static final int POPUP_TYPE_DECLARATION	= 3;
	
	private int popupType = POPUP_TYPE_ELEMENT;
	
	private static boolean swap 		= false;

	private String prefix				= null;
	private JList list					= null;
	private AbstractNamesListModel model	= null;
	private Editor editor				= null;

	/**
	 * The name selection popup.
	 *
	 * @param frame the parent frame.
	 */
	public NameSelectionPopup( Editor editor) {
		super();
		
		this.editor = editor;
		
		setLayout( new BorderLayout());
		list = new JList();
		
		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					NameSelectionPopup.this.editor.insertCurrentSelectedName();
				}
			}
		});
		
		list.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = list.getSelectedIndex();
                if(selected>-1) {
                    list.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		list.setCellRenderer( new NameListCellRenderer());

		JScrollPane scroller = new JScrollPane( list);
		setBorderPainted( true);
		setBorder( new LineBorder( UIManager.getColor( "controlDkShadow")));
		setOpaque( false);
		add( scroller, BorderLayout.CENTER);
		setDoubleBuffered( true );
		setRequestFocusEnabled( false );
		setFocusable( false );
		scroller.setRequestFocusEnabled( false );
		scroller.setBorder( null);
		scroller.setFocusable( false );
		scroller.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		list.setRequestFocusEnabled( false );
		list.setFocusable( false );
//		list.setVisibleRowCount( 4);
		list.setBorder( new EmptyBorder( 2, 2, 2, 2));
	}
	
	/**
	 * Sets the list of names and the current selected name.
	 *
	 * @param names the list of names.
	 * @param current the current selected name.
	 */
//	public void setNames( Vector names, String current, boolean element) {
//		boolean xmlns = false;
//		boolean cdata = false;
//		prefix = current;
//		
//		if ( element) {
//			if ( current.startsWith( "/")) {
//				current = current.substring( 1, current.length());
//			} else {
//				cdata = true;
//			}
//		} else {
//			xmlns = true;
//		}
//		
//		setListModel( new NamesListModel( names, current, cdata, xmlns));
//	}

	public int getPopupType() {
		return popupType;
	}

	/**
	 * Sets the list of names and the current selected name.
	 *
	 * @param current the current selected entity.
	 */
	public void setEntities( String current) {
		prefix = current;
		
		popupType = POPUP_TYPE_ENTITY;
		
		setListModel( new EntitiesListModel( current));
	}
	
	public void swap() {
		swap = !swap;
	}

	public boolean isSwapped() {
		return swap;
	}

	/**
	 * Sets the list of element names and the current selected element-name.
	 *
	 * @param names the list of element names.
	 * @param current the current selected element-name.
	 */
	public void setElementNames( Vector names, String current, String parent) {
		popupType = POPUP_TYPE_ELEMENT;
		prefix = current;
		
		setListModel( new ElementNamesListModel( names, current, parent));
	}

	/**
	 * Sets the list of declaration names.
	 *
	 * @param names the list of declaration names.
	 * @param current the current selected element-name.
	 */
	public void setDeclarationNames( Vector names, String current) {
		if ( current != null) {
			prefix = current;
		} else {
			prefix = "";
		}
		
		popupType = POPUP_TYPE_DECLARATION;
		
		setListModel( new DeclarationListModel( names, prefix));
	}

	/**
	 * Sets the list of attributes and the current selected name.
	 *
	 * @param names the list of names.
	 * @param current the current attribute name, that is edited.
	 */
	public void setAttributes( Vector lists, String current, Vector attributeNames, Vector namespaces) {
		prefix = current;
		Vector unusedAttributes = new Vector();
		
		if ( lists != null) {
			for ( int i = 0; i < lists.size(); i++) {
				Vector attributes = (Vector)lists.elementAt(i);
				
				//tcurley - fixed this bug which caused an infinite loop
				//of null pointer exceptions
				if(attributes != null) {
					
					Vector unused = new Vector();
	
					for ( int j = 0; j < attributes.size(); j++) {
						AttributeInformation attribute = (AttributeInformation)attributes.elementAt(j);
	
						if ( !(attribute instanceof SchemaAttribute) || (((SchemaAttribute)attribute).getUse() != SchemaAttribute.USE_PROHIBITED)) {
							boolean found = false;
							
							for ( int k = 0; k < attributeNames.size(); k++) {
								String name = (String)attributeNames.elementAt(k);
								
								if ( name.equals( attribute.getQualifiedName())) {
									found = true;
									break;
								}
							}
							
							if ( !found) {
								unused.addElement( attribute);
							}
						}
					}
					
					unusedAttributes.addElement( unused);
				}
				
			}
		}
		
		popupType = POPUP_TYPE_ATTRIBUTE;

		setListModel( new AttributesListModel( unusedAttributes, current, namespaces));
	}

	/**
	 * Sets the list of attributes-names and the current selected name.
	 *
	 * @param names the list of names.
	 * @param current the current attribute name, that is edited.
	 */
	public void setAttributeNames( Vector attributes, String current, Vector attributeNames, Vector namespaces) {
		prefix = current;
		Vector unusedAttributes = new Vector();

		if ( attributes != null) {
			for ( int i = 0; i < attributes.size(); i++) {
				String attribute = (String)attributes.elementAt(i);
				boolean found = false;
				
				for ( int j = 0; j < attributeNames.size(); j++) {
					String name = (String)attributeNames.elementAt(j);
					
					if ( name.equals( attribute)) {
						found = true;
						break;
					}
				}
				
				if ( !found) {
					unusedAttributes.addElement( attribute);
				}
			}
		}
		
		popupType = POPUP_TYPE_ATTRIBUTE;

		setListModel( new AttributeNamesListModel( unusedAttributes, current, namespaces));
	}

	/**
	 * Sets the list of declaration names.
	 *
	 * @param names the list of declaration names.
	 * @param current the current selected element-name.
	 */
	public void setAttributeValues( Vector values, String current) {
		if ( current != null) {
			prefix = current;
		} else {
			prefix = "";
		}
		
		popupType = POPUP_TYPE_ATTRIBUTE;
		
		setListModel( new AttributeValueListModel( values, prefix));
	}

	/**
	 * Sets the list of elements and the current selected element.
	 *
	 * @param elements the list of elements.
	 * @param current the current element name, that is edited.
	 */
	public void setElements( Vector elements, String current, String parent) {
		prefix = current;

		popupType = POPUP_TYPE_ELEMENT;

		setListModel( new ElementsListModel( elements, current, parent));
	}
	
	private void setListModel( AbstractNamesListModel model) {
		this.model = model;

		list.setModel( model);
		
		if ( model.getSize() > 0) {
			list.setSelectedIndex( model.getInitialSelectedIndex());
			
			if ( model.getSize() < 10) {
				list.setVisibleRowCount( model.getSize());
			} else {
				list.setVisibleRowCount( 10);
			}

			list.ensureIndexIsVisible( 0);
		}
	}

	/**
	 * Sets the font.
	 */
	public void setPreferredFont( Font font) {
		list.setFont( font);
	}

	/**
	 * Selects the next name in the list.
	 */
	public void selectNext() {
		int index = list.getSelectedIndex();
		
		if ( (index + 1) < model.getSize()) {
			list.setSelectedIndex( index+1);
			list.ensureIndexIsVisible( index+1);
		} 
//		else {
//			setVisible( false);
//		}
	}

	public int getNames() {
		if ( model != null) {
			return model.getSize();
		}
		
		return 0;
	}

	/**
	 * Selects the previous name in the list.
	 */
	public void selectPrevious() {
		int index = list.getSelectedIndex();
		
		if ( index > 0) {
			list.setSelectedIndex( index-1);
			list.ensureIndexIsVisible( index-1);
		} 
//		else {
//			setVisible( false);
//		}
	}

	/**
	 * Return the current prefix.
	 *
	 * @return the prefix.
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * Return the current selected name.
	 *
	 * @return the selected name.
	 */
	public String getSelectedName() {
		int index = list.getSelectedIndex();
		
		if ( index != -1) {
			return model.getName( index);
		} else {
			return null;
		}
	}
	
	class TextNode {
		String text = null;
		
		public TextNode( String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}
	}
	
	class EntityNode {
		String text = null;
		boolean swap = false;
		
		public EntityNode( String text, boolean swap) {
			this.text = text;
			this.swap = swap;
		}

		public EntityNode( String text) {
			this.text = text;
		}
		
		public String getText() {
			return text;
		}

		public boolean isSwapped() {
			return swap;
		}

		public String getEntity() {
			return "&"+text+";";
		}

		public char getChar() {
			return CommonEntities.getChar( text);
		}
	}

	class ElementNode {
		String qname = null;
		String prefix = null;
		String name = null;
		
		public ElementNode( String qname) {
			this.qname = qname;
		}
		
		public String getPrefix() {
			if ( prefix == null) {
				int index = qname.indexOf( ':');
				
				if ( index != -1) {
					prefix = qname.substring( 0, index);
				} else {
					prefix = "";
				}
			}
			
			return prefix;
		}

		public String getName() {
			if ( name == null) {
				int index = qname.indexOf( ':');
				
				if ( index != -1) {
					name = qname.substring( index+1, qname.length());
				} else {
					name = qname;
				}
			}

			return name;
		}

		public String getQualifiedName() {
			return qname;
		}
	}

	class AttributeNode {
		String qname = null;
		String prefix = null;
		String name = null;
		
		public AttributeNode( String qname) {
			this.qname = qname;
		}
		
		public String getPrefix() {
			if ( prefix == null) {
				int index = qname.indexOf( ':');
				
				if ( index != -1) {
					prefix = qname.substring( 0, index);
				} else {
					prefix = "";
				}
			}
			
			return prefix;
		}

		public String getName() {
			if ( name == null) {
				int index = qname.indexOf( ':');
				
				if ( index != -1) {
					name = qname.substring( index+1, qname.length());
				} else {
					name = qname;
				}
			}

			return name;
		}

		public String getQualifiedName() {
			return qname;
		}
	}

	class NamespaceNode {
		String text = null;
		
		public NamespaceNode( String text) {
			this.text = text;
		}
		
		public String getNamespace() {
			if ( text != null) {
				int index = text.indexOf( '"');
				
				if ( index != -1) {
					return text.substring( index+1, text.length()-1);
				}
			}
				
			return text;
		}

		public String getPrefix() {
			if ( text != null) {
				int index = text.indexOf( ':');
				int equals = text.indexOf( '=');
				
				if ( index != -1 && index < equals) {
					return text.substring( index+1, equals);
				} 
			}

			return null;
		}
		
		public String toString() {
			if ( text != null) {
				return text;
			}
			
			return "xmlns:";
		}
	}

	class AttributesListModel extends AbstractNamesListModel {
		Vector attributes = null;
		int initIndex = 0;
		
		public AttributesListModel( Vector list, String prefix, Vector namespaces) {
			attributes = new Vector();
			Vector prefixes = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					Vector ordered = orderAttributes( (Vector)list.elementAt(i), prefixes);
					
					for ( int j = 0; j < ordered.size(); j++) {
						AttributeInformation attribute = (AttributeInformation)ordered.elementAt(j);

						if ( !contains( attributes, attribute)) {
							attributes.addElement( attribute);
						}
					}
				}
				
				for ( int i =0; i < prefixes.size(); i++) {
					attributes.insertElementAt( new AttributeNode( ((String)prefixes.elementAt(i))+":"), i);
				}

				Vector uris = new Vector();
				Vector prefixedURIs = new Vector();

				if ( "xmlns".startsWith( prefix)) {
					for ( int i = 0; i < namespaces.size(); i++) {
						String ns = (String)namespaces.elementAt(i);
						boolean add = true;
						int index = -1;
						
						int colonIndex = ns.indexOf( ':');
						int equalsIndex = ns.indexOf( '=');

						// namespace has prefix
						if ( colonIndex != -1 && colonIndex < equalsIndex) {
							for ( int j = 0; j < prefixedURIs.size(); j++) {
								int comp = ns.compareToIgnoreCase( (String)prefixedURIs.elementAt(j));

								// Compare alphabeticaly
								if ( comp < 0) {
									index = j;
									break;
								} else if ( comp == 0) {
									add = false;
									break;
								}
							}
							
							if ( add) {
								if ( index != -1) {
									prefixedURIs.insertElementAt( ns, index);
								} else {
									prefixedURIs.addElement( ns);
								}
							}
						} else {
							for ( int j = 0; j < uris.size(); j++) {
								int comp = ns.compareToIgnoreCase( (String)uris.elementAt(j));

								// Compare alphabeticaly
								if ( comp < 0) {
									index = j;
									break;
								} else if ( comp == 0) {
									add = false;
									break;
								}
							}
							
							if ( add) {
								if ( index != -1) {
									uris.insertElementAt( ns, index);
								} else {
									uris.addElement( ns);
								}
							}
						}
					}
				} else if ( prefix.startsWith( "xmlns:")) {
					if ( namespaces != null) {
						for ( int i = 0; i < namespaces.size(); i++) {
							String ns = (String)namespaces.elementAt(i);
	
							int index = -1;
							boolean add = true;
							int colon = ns.indexOf( ':');
							
							// namespace has prefix
							if ( colon != -1) {
								if ( ns.startsWith( prefix)) {
									for ( int j = 0; j < prefixedURIs.size(); j++) {
										int comp = ns.compareToIgnoreCase( (String)prefixedURIs.elementAt(j));
		
										// Compare alphabeticaly
										if ( comp < 0) {
											index = j;
											break;
										} else if ( comp == 0) {
											add = false;
											break;
										}
									}
									
									if ( add) {
										if ( index != -1) {
											prefixedURIs.insertElementAt( ns, index);
										} else {
											prefixedURIs.addElement( ns);
										}
									}
								}
							}
						}
					}
				}

				for ( int i = 0; i < uris.size(); i++) {
					attributes.addElement( new NamespaceNode( (String)uris.elementAt(i)));
				}

				for ( int i = 0; i < prefixedURIs.size(); i++) {
					attributes.addElement( new NamespaceNode( (String)prefixedURIs.elementAt(i)));
				}

				if ( "xmlns".startsWith( prefix)) {
					attributes.insertElementAt( new NamespaceNode( null), 0);
					initIndex++;
				}
			}
		}
		
		private boolean contains( Vector list, AttributeInformation attribute) {
			for ( int i = 0; i < list.size(); i++) {
				AttributeInformation item = (AttributeInformation)list.elementAt(i);

				if ( item.getQualifiedName().equals( attribute.getQualifiedName())) {
					return true;
				}
			}
			
			return false;
		}

		private Vector orderAttributes( Vector list, Vector prefixes) {
			Vector attributes = new Vector();
			Vector requiredAttributes = new Vector();
			Vector optionalAttributes = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					AttributeInformation attribute = (AttributeInformation)list.elementAt(i);
					String name = attribute.getQualifiedName();
					
					if ( name.startsWith( prefix)) {
						String pref = attribute.getPrefix();
		
						if ( pref != null && pref.length() > 0 && pref.startsWith( prefix)) {
							if ( !prefixes.contains(pref)) {
								prefixes.add( pref);
								initIndex++;
							}
						}
		
						// Find out where to insert the element...
						int index = -1;
						boolean add = true;
						
						if ( attribute.isRequired()) {
							for ( int j = 0; j < requiredAttributes.size() && index == -1; j++) {
								int comp = name.compareToIgnoreCase( ((AttributeInformation)requiredAttributes.elementAt(j)).getQualifiedName());

								// Compare alphabeticaly
								if ( comp < 0) {
									index = j;
									break;
								} else if ( comp == 0) {
									add = false;
									break;
								}
							}
						
							if ( add) {
								if ( index != -1) {
									requiredAttributes.insertElementAt( attribute, index);
								} else {
									requiredAttributes.addElement( attribute);
								}
							}
						} else {
							for ( int j = 0; j < optionalAttributes.size() && index == -1; j++) {
								int comp = name.compareToIgnoreCase( ((AttributeInformation)optionalAttributes.elementAt(j)).getQualifiedName());

								// Compare alphabeticaly
								if ( comp < 0) {
									index = j;
									break;
								} else if ( comp == 0) {
									add = false;
									break;
								}
							}
						
							if ( add) {
								if ( index != -1) {
									optionalAttributes.insertElementAt( attribute, index);
								} else {
									optionalAttributes.addElement( attribute);
								}
							}
						}
					}
				}
				
				for ( int i = 0; i < requiredAttributes.size(); i++) {
					attributes.addElement( requiredAttributes.elementAt(i));
				}				
		
				for ( int i = 0; i < optionalAttributes.size(); i++) {
					attributes.addElement( optionalAttributes.elementAt(i));
				}
			}

			return attributes;
		}

		public int getSize() {
			if ( attributes != null) {
				return attributes.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return attributes.elementAt( i);
		}

		public String getName( int i) {
			Object object = attributes.elementAt( i);
			
			if ( object instanceof AttributeInformation) {
				AttributeInformation attribute = (AttributeInformation)object;

				Vector values = attribute.getValues();
				if ( values.size() == 1 && ((AttributeValue)values.elementAt(0)).isFixed()) {
					return ((AttributeInformation)object).getQualifiedName()+"=\""+((AttributeValue)values.elementAt(0)).getValue()+"\"";
				}

				return ((AttributeInformation)object).getQualifiedName();
			} 
			
			if ( object instanceof AttributeNode) {
				return ((AttributeNode)object).getPrefix()+":";
			} 

			if ( object instanceof NamespaceNode) {
				return ((NamespaceNode)object).toString();
			} 

			return (String)object;
		}

		public int getInitialSelectedIndex() {
			if ( initIndex > 0) {
				String selectedName = getName( initIndex);

				for ( int i = initIndex-1; i >= 0; i--) {
					if ( selectedName.startsWith( getName( i))) {
						return i;
					}
				}
			} 

			return initIndex;
		}
	}

	class AttributeNamesListModel extends AbstractNamesListModel {
		Vector attributeNames = null;
		Vector prefixes = null;
		private int initIndex = 0;
		
		public AttributeNamesListModel( Vector list, String prefix, Vector namespaces) {
			attributeNames = new Vector();
			prefixes = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					String name = (String)list.elementAt(i);
					
					if ( name.startsWith( prefix)) {
						AttributeNode node = new AttributeNode( name);
						String pref = node.getPrefix();

						if ( pref != null && pref.length() > 0 && pref.startsWith( prefix)) {
							if ( !prefixes.contains(pref)) {
								prefixes.add( pref);
								initIndex++;
							}
						}

						// Find out where to insert the element...
						int index = -1;
						boolean add = true;
						
						for ( int j = 0; j < attributeNames.size(); j++) {
							int comp = name.compareToIgnoreCase( ((AttributeNode)attributeNames.elementAt(j)).getQualifiedName());

							// Compare alphabeticaly
							if ( comp < 0) {
								index = j;
								break;
							} else if ( comp == 0) {
								add = false;
								break;
							}
						}
					
						if ( add) {
							if ( index != -1) {
								attributeNames.insertElementAt( node, index);
							} else {
								attributeNames.addElement( node);
							}
						}
					}
				}

				for ( int i =0; i < prefixes.size(); i++) {
					attributeNames.insertElementAt( new AttributeNode( ((String)prefixes.elementAt(i))+":"), i);
				}

				Vector uris = new Vector();
				Vector prefixedURIs = new Vector();

				if ( "xmlns".startsWith( prefix)) {
					if ( namespaces != null) {
						for ( int i = 0; i < namespaces.size(); i++) {
							String ns = (String)namespaces.elementAt(i);
	
							int index = -1;
							boolean add = true;

							int colonIndex = ns.indexOf( ':');
							int equalsIndex = ns.indexOf( '=');

							// namespace has prefix
							if ( colonIndex != -1 && colonIndex < equalsIndex) {
								for ( int j = 0; j < prefixedURIs.size(); j++) {
									int comp = ns.compareToIgnoreCase( (String)prefixedURIs.elementAt(j));
	
									// Compare alphabeticaly
									if ( comp < 0) {
										index = j;
										break;
									} else if ( comp == 0) {
										add = false;
										break;
									}
								}
								
								if ( add) {
									if ( index != -1) {
										prefixedURIs.insertElementAt( ns, index);
									} else {
										prefixedURIs.addElement( ns);
									}
								}
							} else {
								for ( int j = 0; j < uris.size(); j++) {
									int comp = ns.compareToIgnoreCase( (String)uris.elementAt(j));
	
									// Compare alphabeticaly
									if ( comp < 0) {
										index = j;
										break;
									} else if ( comp == 0) {
										add = false;
										break;
									}
								}
	
								if ( add) {
									if ( index != -1) {
										uris.insertElementAt( ns, index);
									} else {
										uris.addElement( ns);
									}
								}
							}
						}
					} 
				} else if ( prefix.startsWith( "xmlns:")) {
					if ( namespaces != null) {
						for ( int i = 0; i < namespaces.size(); i++) {
							String ns = (String)namespaces.elementAt(i);
	
							int index = -1;
							boolean add = true;
							int colon = ns.indexOf( ':');
							
							// namespace has prefix
							if ( colon != -1) {
								if ( ns.startsWith( prefix)) {
									for ( int j = 0; j < prefixedURIs.size(); j++) {
										int comp = ns.compareToIgnoreCase( (String)prefixedURIs.elementAt(j));
		
										// Compare alphabeticaly
										if ( comp < 0) {
											index = j;
											break;
										} else if ( comp == 0) {
											add = false;
											break;
										}
									}
									
									if ( add) {
										if ( index != -1) {
											prefixedURIs.insertElementAt( ns, index);
										} else {
											prefixedURIs.addElement( ns);
										}
									}
								}
							}
						}
					}
				}

				for ( int i = 0; i < uris.size(); i++) {
					attributeNames.addElement( new NamespaceNode( (String)uris.elementAt(i)));
				}

				for ( int i = 0; i < prefixedURIs.size(); i++) {
					attributeNames.addElement( new NamespaceNode( (String)prefixedURIs.elementAt(i)));
				}

				if ( "xmlns".startsWith( prefix)) {
					attributeNames.insertElementAt( new NamespaceNode( null), 0);
					initIndex++;
				}
			}
		}
		
		public int getSize() {
			if ( attributeNames != null) {
				return attributeNames.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return attributeNames.elementAt( i);
		}

		public String getName( int i) {
			if ( attributeNames.size() > i) {
				Object object = attributeNames.elementAt( i);
				
				if ( object instanceof AttributeNode) {
					return ((AttributeNode)object).getQualifiedName();
				} 
				
				if ( object instanceof NamespaceNode) {
					return ((NamespaceNode)object).toString();
				} 
	
				return (String)object;
			}
			
			return null;
		}
		
		public int getInitialSelectedIndex() {
			if ( initIndex > 0) {
				if ( attributeNames.size() > initIndex) {
					String selectedName = getName( initIndex);
	
					for ( int i = initIndex-1; i >= 0; i--) {
						if ( selectedName.startsWith( getName( i))) {
							return i;
						}
					}
				} else {
					return 0;
				}
			} 

			return initIndex;
		}
	}

	class ElementsListModel extends AbstractNamesListModel {
		int initIndex = 0;
		Vector elements = null;
		
		public ElementsListModel( Vector list, String prefix, String parent) {
			elements = new Vector();
			Vector prefixes = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					Vector ordered = orderElements( (Vector)list.elementAt(i), prefixes);
					
					for ( int j = 0; j < ordered.size(); j++) {
						ElementInformation element = (ElementInformation)ordered.elementAt(j);

						if ( !contains( elements, element)) {
							elements.addElement( element);
						}
					}
				}
				
				if ( parent != null && ("/"+parent).startsWith( prefix)) {
					elements.insertElementAt( new ElementNode( "/"+parent), 0);

					if ( prefix.length() == 0) {
						elements.insertElementAt( new EntityNode( "lt"), 0);
					}
				} else if ( prefix.trim().length() == 0) {
					if ( parent != null && parent.trim().length() > 0) {
						elements.insertElementAt( new EntityNode( "lt"), 0);
						elements.insertElementAt( new ElementNode( "/"+parent), 1);
//					} else {
//						elements.insertElementAt( new TextNode( "!DOCTYPE"), 0);
					}
				}

				for ( int i =0; i < prefixes.size(); i++) {
					elements.insertElementAt( new ElementNode( ((String)prefixes.elementAt(i))+":"), i);
				}

				if ( "!--".startsWith( prefix)) {
					elements.addElement( new TextNode( "!--"));
				}

				if ( "![CDATA[".startsWith( prefix)) {
					elements.addElement( new TextNode( "![CDATA["));
				}
			}
		}
		
		private boolean contains( Vector list, ElementInformation element) {
			for ( int i = 0; i < list.size(); i++) {
				ElementInformation item = (ElementInformation)list.elementAt(i);

				if ( item.getQualifiedName().equals( element.getQualifiedName())) {
					return true;
				}
			}
			
			return false;
		}
		
		private Vector orderElements( Vector list, Vector prefixes) {
			Vector elements = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					ElementInformation element = (ElementInformation)list.elementAt(i);
					String name = element.getQualifiedName();
					
					if ( name.startsWith( prefix)) {
						// Find out where to insert the element...
						int index = -1;
						boolean add = true;

						String pref = element.getPrefix();
						if ( pref != null && pref.length() > 0 && pref.startsWith( prefix)) {
							if ( !prefixes.contains(pref)) {
								prefixes.add( pref);
								initIndex++;
							}
						}

						for ( int j = 0; j < elements.size() && index == -1; j++) {
							// Compare alphabeticaly
							int comp = name.compareToIgnoreCase( ((ElementInformation)elements.elementAt(j)).getQualifiedName());

							if ( comp < 0) {
								index = j;
								break;
							} else if ( comp == 0) {
								add = false;
								break;
							}
						}
					
						if ( add) {
							if ( index != -1) {
								elements.insertElementAt( element, index);
							} else {
								elements.addElement( element);
							}
						}
					}
				}
			}
			
			return elements;
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return elements.elementAt( i);
		}

		public String getName( int i) {
			Object object = elements.elementAt( i);
			
			if ( object instanceof ElementInformation) {
				ElementInformation element = ((ElementInformation)object);
				if ( element.isEmpty() && (element.getAttributes() == null || element.getAttributes().size() == 0)) {
					return ((ElementInformation)object).getQualifiedName()+"/>";
				} else {
					return ((ElementInformation)object).getQualifiedName();
				}
			} 
			
			if ( object instanceof TextNode) {
				return ((TextNode)object).getText();
			} 

			if ( object instanceof ElementNode) {
				return ((ElementNode)object).getQualifiedName();
			} 

			if ( object instanceof EntityNode) {
				return ((EntityNode)object).getEntity();
			} 

			return object.toString();
		}
		
		public int getInitialSelectedIndex() {
			if ( initIndex > 0) {
				String selectedName = getName( initIndex);

				if ( selectedName.equals( "&lt;")) {
					if ( getSize() > initIndex+2) { 
						selectedName = getName( initIndex+2);
					} else {
						return initIndex;
					}
				}

				for ( int i = initIndex-1; i >= 0; i--) {
					if ( selectedName.startsWith( getName( i))) {
						return i;
					}
				}
			} 

			return initIndex;
		}
	}

	class ElementNamesListModel extends AbstractNamesListModel {
		Vector elementNames = null;
		
		public ElementNamesListModel( Vector list, String prefix, String parent) {
			elementNames = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					String name = (String)list.elementAt(i);
					
					if ( name.startsWith( prefix)) {
						// Find out where to insert the element...
						int index = -1;
						boolean add = true;
						
						for ( int j = 0; j < elementNames.size() && index == -1; j++) {
							int comp = name.compareToIgnoreCase( ((ElementNode)elementNames.elementAt(j)).getQualifiedName());

							// Compare alphabeticaly
							if ( comp < 0) {
								index = j;
								break;
							} else if ( comp == 0) {
								add = false;
								break;
							}
						}
					
						if ( add) {
							if ( index != -1) {
								elementNames.insertElementAt( new ElementNode( name), index);
							} else {
								elementNames.addElement( new ElementNode( name));
							}
						}
					}
				}
				
				if ( parent != null && ("/"+parent).startsWith( prefix)) {
					elementNames.insertElementAt( new ElementNode( "/"+parent), 0);

					if ( prefix.length() == 0) {
						elementNames.insertElementAt( new EntityNode( "lt"), 0);
					}
				} else if ( prefix.trim().length() == 0) {
					if ( parent != null && parent.trim().length() > 0) {
						elementNames.insertElementAt( new EntityNode( "lt"), 0);
						elementNames.insertElementAt( new ElementNode( "/"+parent), 1);
					}
				}


				if ( "!--".startsWith( prefix)) {
					elementNames.addElement( new TextNode( "!--"));
				}

				if ( "![CDATA[".startsWith( prefix)) {
					elementNames.addElement( new TextNode( "![CDATA["));
				}
			}
		}
		
		public int getSize() {
			if ( elementNames != null) {
				return elementNames.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return elementNames.elementAt( i);
		}

		public String getName( int i) {
			Object object = elementNames.elementAt( i);
			
			if ( object instanceof ElementNode) {
				return ((ElementNode)object).getQualifiedName();
			} 
			
			if ( object instanceof TextNode) {
				return ((TextNode)object).getText();
			} 

			if ( object instanceof EntityNode) {
				return ((EntityNode)object).getEntity();
			} 

			return object.toString();
		}
		
		public int getInitialSelectedIndex() {
			return 0;
		}
	}

	class EntitiesListModel extends AbstractNamesListModel {
		Vector entities = null;
		
		public EntitiesListModel( String current) {
			entities = new Vector();
			
			if ( current.startsWith( "&")) {
				if ( "&amp;".startsWith( current)) {
					entities.addElement( new EntityNode( "amp"));
				} 
				
				if ( "&apos;".startsWith( current)) {
					entities.addElement( new EntityNode( "apos"));
				} 
				
				if ( "&gt;".startsWith( current)) {
					entities.addElement( new EntityNode( "gt"));
				} 
				
				if ( "&lt;".startsWith( current)) {
					entities.addElement( new EntityNode( "lt"));
				} 
				
				if ( "&quot;".startsWith( current)) {
					entities.addElement( new EntityNode( "quot"));
				}

				if ( current != null && current.equals( "&")) {
					entities.addElement( MORE_ENTITIES_STRING);
				} else {
					Vector result = CommonEntities.getEntityNamesStartingWith( current.substring( 1, current.length()));

					for ( int i = 0; i < result.size(); i++) {
						String ent = (String)result.elementAt(i);

						if ( !entities.contains( ent)) {
							entities.addElement( new EntityNode( (String)result.elementAt(i), swap));
						}
					}
					
					if ( entities.size() > 0) {
						entities.addElement( SWAP_ENTITY_STRING);
					}
				}
			} else {
				if ( "<".equals( current)) {
					entities.addElement( new EntityNode( "lt"));
				} else if ( ">".equals( current)) {
					entities.addElement( new EntityNode( "gt"));
				} else if ( "'".equals( current)) {
					entities.addElement( new EntityNode( "apos"));
				} else if ( "\"".equals( current)) {
					entities.addElement( new EntityNode( "quot"));
				} 
			}
		}
		
		public EntitiesListModel( Vector list, String prefix) {
			entities = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					String name = (String)list.elementAt(i);
					
					if ( name.startsWith( prefix)) {
						// Find out where to insert the element...
						int index = -1;
						
						for ( int j = 0; j < entities.size() && index == -1; j++) {
							// Compare alphabeticaly
							if ( name.compareToIgnoreCase( ((EntityNode)entities.elementAt(j)).getText()) <= 0) {
								index = j;
							}
						}
					
						if ( index != -1) {
							entities.insertElementAt( new EntityNode( name), index);
						} else {
							entities.addElement( new EntityNode( name));
						}
					}
				}
			}
		}
		
		public int getSize() {
			if ( entities != null) {
				return entities.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return entities.elementAt( i);
		}

		public String getName( int i) {
			Object object = entities.elementAt( i);
			
			if ( object instanceof EntityNode) {
				EntityNode node = (EntityNode)object;
				if ( node.isSwapped()) {
					return ""+node.getChar();
				} else {
					return node.getEntity();
				}
			} 

			return object.toString();
		}

		public int getInitialSelectedIndex() {
			return 0;
		}
	}

	class AttributeValueListModel extends AbstractNamesListModel {
		Vector values = null;
		
		public AttributeValueListModel( Vector list, String prefix) {
			values = new Vector();
			AttributeValue defaultValue = null;
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					AttributeValue value = (AttributeValue)list.elementAt(i);
					
					if ( value.getValue().startsWith( prefix)) {
						if ( value.isDefault()) {
							defaultValue = value;
						} else {
							// Find out where to insert the element...
							int index = -1;
							boolean add = true;
	
							for ( int j = 0; j < values.size() && index == -1; j++) {
								// Compare alphabeticaly
								int comp = value.getValue().compareToIgnoreCase( ((AttributeValue)values.elementAt(j)).getValue());

								if ( comp < 0) {
									index = j;
									break;
								} else if ( comp == 0) {
									add = false;
									break;
								}
							}
							
							if ( add) {
								if ( index != -1) {
									values.insertElementAt( value, index);
								} else {
									values.addElement( value);
								}
							}
						}
					}
				}
				
				if ( defaultValue != null) {
					values.insertElementAt( defaultValue, 0);
				}
			}
		}
		
		public int getSize() {
			if ( values != null) {
				return values.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return values.elementAt( i);
		}

		public String getName( int i) {
			return ((AttributeValue)values.elementAt( i)).getValue();
		}
		
		public int getInitialSelectedIndex() {
			return 0;
		}
	}

	class DeclarationListModel extends AbstractNamesListModel {
		Vector names = null;
		
		public DeclarationListModel( Vector list, String prefix) {
			names = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					String name = (String)list.elementAt(i);
					
					if ( name.startsWith( prefix)) {
						// Find out where to insert the element...
						int index = -1;

//						for ( int j = 0; j < names.size() && index == -1; j++) {
//							// Compare alphabeticaly
//							if ( name.compareToIgnoreCase( (String)names.elementAt(j)) <= 0) {
//								index = j;
//							}
//						}
//						
//						if ( index != -1) {
//							names.insertElementAt( name, index);
//						} else {
							names.addElement( name);
//						}
					}
				}
			}
			
//			if ( "!--".startsWith( prefix)) {
//				names.addElement( new TextNode( "!--"));
//			}
//
//			if ( "![CDATA[".startsWith( prefix)) {
//				names.addElement( new TextNode( "![CDATA["));
//			}
		}
		
		public int getSize() {
			if ( names != null) {
				return names.size();
			}
			
			return 0;
		}

		public Object getElementAt( int i) {
			return names.elementAt( i);
		}

		public String getName( int i) {
			Object object = names.elementAt( i);
			
			if ( object instanceof TextNode) {
				return ((TextNode)object).getText();
			} 

			return (String)object;
		}
		
		public int getInitialSelectedIndex() {
			return 0;
		}
	}

//	class NamesListModel extends AbstractNamesListModel {
//		Vector names = null;
//		
//		public NamesListModel( Vector list, String prefix, boolean cdata, boolean xmlns) {
//			names = new Vector();
//			
//			if ( list != null) {
//				for ( int i = 0; i < list.size(); i++) {
//					String name = (String)list.elementAt(i);
//					
//					if ( name.startsWith( prefix)) {
//						// Find out where to insert the element...
//						int index = -1;
//
//						for ( int j = 0; j < names.size() && index == -1; j++) {
//							// Compare alphabeticaly
//							if ( name.compareToIgnoreCase( (String)names.elementAt(j)) <= 0) {
//								index = j;
//							}
//						}
//						
//						if ( index != -1) {
//							names.insertElementAt( name, index);
//						} else {
//							names.addElement( name);
//						}
//					}
//				}
//				
//				if ( cdata && "![CDATA[".startsWith( prefix)) {
//					names.addElement( "![CDATA[");
//				}
//
//				if ( xmlns && "xmlns".startsWith( prefix)) {
//					names.addElement( "xmlns");
//				}
//			}
//		}
//		
//		public int getSize() {
//			if ( names != null) {
//				return names.size();
//			}
//			
//			return 0;
//		}
//
//		public Object getElementAt( int i) {
//			return names.elementAt( i);
//		}
//
//		public String getName( int i) {
//			return (String)names.elementAt( i);
//		}
//	}
	
	abstract class AbstractNamesListModel extends AbstractListModel {
		public abstract String getName( int i);
		public abstract int getInitialSelectedIndex();
	}

	class NameListCellRenderer extends JPanel implements ListCellRenderer {
		protected EmptyBorder noFocusBorder;
		private JLabel icon = null;
		private JLabel prefix = null;
		private JLabel colon = null;
		private JLabel equals = null;
		private JLabel startQuote = null;
		private JLabel endQuote = null;
		private JLabel name = null;
		private JLabel value = null;
		private JLabel entityValue = null;

		public NameListCellRenderer() {
			super( new BorderLayout());
			
			if (noFocusBorder == null) {
			    noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			}
			
			JPanel basePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
			basePanel.setOpaque( false);
		
			setBorder( noFocusBorder);

			icon = new JLabel();
	
			prefix = new JLabel();
			prefix.setFont( prefix.getFont().deriveFont( Font.PLAIN + Font.ITALIC));
	
			colon = new JLabel(":");
			colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
			icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));
	
			name = new JLabel();
			name.setFont( name.getFont().deriveFont( Font.BOLD));
			
			equals = new JLabel("=");
			equals.setFont( equals.getFont().deriveFont( Font.PLAIN));

			startQuote = new JLabel("\"");
			startQuote.setFont( startQuote.getFont().deriveFont( Font.PLAIN));

			value = new JLabel();
			value.setFont( value.getFont().deriveFont( Font.PLAIN));

			endQuote = new JLabel("\"");
			endQuote.setFont( startQuote.getFont().deriveFont( Font.PLAIN));

			basePanel.add( icon);
			basePanel.add( prefix);
			basePanel.add( colon);
			basePanel.add( name);
			basePanel.add( equals);
			basePanel.add( startQuote);
			basePanel.add( value);
			basePanel.add( endQuote);
			
			entityValue = new JLabel();
			entityValue.setFont( entityValue.getFont().deriveFont( Font.PLAIN));

			this.add( basePanel, BorderLayout.WEST);
			this.add( entityValue, BorderLayout.EAST);
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
		
			setPreferredFont( list.getFont());

			if ( value instanceof AttributeInformation) {
				AttributeInformation attribute = (AttributeInformation)value;
				
				if ( attribute.isRequired()) {
					setIcon( ATTRIBUTE_REQUIRED_ICON);
					setForeground( Color.red);
					
				} else {
					setIcon( ATTRIBUTE_ICON);
					setForeground( list.getForeground());
				}
				
				setPrefix( attribute.getPrefix());
				setText( attribute.getName());
				
				Vector values = attribute.getValues();
				if ( values.size() == 1 && ((AttributeValue)values.elementAt(0)).isFixed()) {
					setValue( ((AttributeValue)values.elementAt(0)).getValue());
				} else {
					setValue( null);
				}

				setEntityValue( -1);
				
			} else if ( value instanceof AttributeNode) {
				AttributeNode attribute = (AttributeNode)value;
				
				setIcon( ATTRIBUTE_ICON);
				setForeground( list.getForeground());
				
				setPrefix( attribute.getPrefix());
				setText( attribute.getName());

				if ( attribute.getName() == null || attribute.getName().length() == 0) {
					prefix.setVisible( true);
					colon.setVisible( true);
				}

				setValue( null);
				setEntityValue( -1);
			} else if ( value instanceof ElementInformation) {
				ElementInformation element = (ElementInformation)value;
				setIcon( ELEMENT_ICON);
				setForeground( list.getForeground());

				setPrefix( element.getPrefix());
				setText( element.getName());

				if ( element.getName() == null || element.getName().length() == 0) {
					prefix.setVisible( true);
					colon.setVisible( true);
				}

				setValue( null);
				setEntityValue( -1);
			} else if ( value instanceof ElementNode) {
				ElementNode element = (ElementNode)value;
				setIcon( ELEMENT_ICON);
				setForeground( list.getForeground());

				setPrefix( element.getPrefix());
				setText( element.getName());
				setValue( null);
				setEntityValue( -1);
			} else if ( value instanceof EntityNode) {
				EntityNode node = (EntityNode)value;
				setIcon( ENTITY_ICON);
				setForeground( list.getForeground());

				setPrefix( null);
				if ( node.isSwapped()) {
					setText( ""+node.getChar());
					setEntityValue( node.getText());
				} else {
					setText( node.getText());
					setEntityValue( node.getChar());
				}

				setValue( null);
					
			} else if ( value instanceof TextNode) {
				TextNode node = (TextNode)value;
				setIcon( TEXT_ICON);
				setForeground( list.getForeground());

				setPrefix( null);
				setText( node.getText());
				setValue( null);
				setEntityValue( -1);
			} else if ( value instanceof NamespaceNode) {
				NamespaceNode node = (NamespaceNode)value;
				setIcon( NAMESPACE_ICON);
				setForeground( list.getForeground());

				setPrefix( "xmlns");
				
				if ( node.getPrefix() == null && node.getNamespace() == null) {
					colon.setVisible( true);					

					setText( null);
					setValue( null);
				} else {
					setText( node.getPrefix());

					if ( node.getPrefix() == null) {
						colon.setVisible( false);					
					}

					setValue( node.getNamespace());
				}

				setEntityValue( -1);
			} else if ( value instanceof AttributeValue){
				setIcon( null);
				setForeground( list.getForeground());

				setPrefix( null);
				setText( value.toString());
				
				if ( ((AttributeValue)value).isDefault()) {
					name.setFont( name.getFont().deriveFont( Font.BOLD));
				} else {
					name.setFont( name.getFont().deriveFont( Font.PLAIN));
				}

				setValue( null);
				setEntityValue( -1);
			} else {
				setIcon( null);
				setForeground( list.getForeground());

				setPrefix( null);
				setText( value.toString());
				setValue( null);
				setEntityValue( -1);
			}

			if (selected) {
				setForeground(list.getSelectionForeground());
				setBackground(list.getSelectionBackground());
			} else {
				setBackground(list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
		
		public void setForeground( Color color) {
		
			if (icon != null) {
				icon.setForeground( color);
				prefix.setForeground( color);
				colon.setForeground( color);
				name.setForeground( color);
				entityValue.setForeground( color);
				equals.setForeground( color);
				startQuote.setForeground( color);
				value.setForeground( color);
				endQuote.setForeground( color);
				super.setForeground( color);
			}
		}

		public void setBackground( Color color) {
			if (icon != null) {
				icon.setBackground( color);
				prefix.setBackground( color);
				colon.setBackground( color);
				name.setBackground( color);
				equals.setBackground( color);
				startQuote.setBackground( color);
				value.setBackground( color);
				endQuote.setBackground( color);
				super.setBackground( color);
				entityValue.setBackground( color);
			}
		}

		public void setPreferredFont( Font font) {
			prefix.setFont( font.deriveFont( Font.PLAIN + Font.ITALIC));
			colon.setFont( font.deriveFont( Font.PLAIN));
			name.setFont( font.deriveFont( Font.BOLD));
			equals.setFont( font.deriveFont( Font.PLAIN));
			startQuote.setFont( font.deriveFont( Font.PLAIN));
			value.setFont( font.deriveFont( Font.PLAIN));
			endQuote.setFont( font.deriveFont( Font.PLAIN));
			icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));
			entityValue.setFont( font.deriveFont( Font.PLAIN));
		}
		
		private void setIcon( ImageIcon image) {
			if ( image == null) {
				icon.setVisible( false);
			} else {
				icon.setVisible( true);
				icon.setIcon( image);
			}
		}
		
		private void setEntityValue( int value) {
			if ( value != -1) {
				entityValue.setText( "["+(char)value+"]");
				entityValue.setVisible( true);
			} else {
				entityValue.setVisible( false);
			}
		}

		private void setEntityValue( String value) {
			if ( value != null) {
				entityValue.setText( "["+value+"]");
				entityValue.setVisible( true);
			} else {
				entityValue.setVisible( false);
			}
		}

		private void setPrefix( String value) {
			if ( value != null && value.trim().length() > 0) {
				prefix.setText( value);
				prefix.setVisible( true);
				colon.setVisible( true);
			} else {
				prefix.setVisible( false);
				colon.setVisible( false);
			}
		}

		private void setValue( String text) {
			if ( text != null && text.trim().length() > 0) {
				value.setText( text);
				value.setVisible( true);
				equals.setVisible( true);
				startQuote.setVisible( true);
				endQuote.setVisible( true);
			} else {
				value.setVisible( false);
				equals.setVisible( false);
				startQuote.setVisible( false);
				endQuote.setVisible( false);
			}
		}

		private void setText( String value) {
			name.setText( value);
		}

//		 public void validate() {}
//		 public void revalidate() {}
//		 public void repaint(long tm, int x, int y, int width, int height) {}
//		 public void repaint(Rectangle r) {}
//		 protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
//			// Strings get interned...
//			if (propertyName=="text")
//			    super.firePropertyChange(propertyName, oldValue, newValue);
//		 }
//		
//		 public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {}
//		 public void firePropertyChange(String propertyName, char oldValue, char newValue) {}
//		 public void firePropertyChange(String propertyName, short oldValue, short newValue) {}
//		 public void firePropertyChange(String propertyName, int oldValue, int newValue) {}
//		 public void firePropertyChange(String propertyName, long oldValue, long newValue) {}
//		 public void firePropertyChange(String propertyName, float oldValue, float newValue) {}
//		 public void firePropertyChange(String propertyName, double oldValue, double newValue) {}
//		 public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {}
	}
} 
