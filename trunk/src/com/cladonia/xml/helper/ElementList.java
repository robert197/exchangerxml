/*
 * $Id: ElementList.java,v 1.4 2005/08/31 16:19:34 tcurley Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;


import com.cladonia.schema.ElementInformation;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.dtd.DTDElement;
import com.cladonia.schema.rng.RNGElement;
import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The context sensitive element, attribute, entity selection popup.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/08/31 16:19:34 $
 * @author Dogsbay
 */
public class ElementList extends JList {

	private static final ImageIcon ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ElementIcon.gif");
	private static final ImageIcon VIRTUAL_ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualElementIcon.gif");
//	private static final ImageIcon TEXT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/ExclamationIcon.gif");
//	private static final ImageIcon ENTITY_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/EntityIcon.gif");
//	private static final ImageIcon NAMESPACE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/NamespaceIcon.gif");

//	private boolean enabled	= true;
	private AbstractNamesListModel model	= null;

	/**
	 * The name selection popup.
	 *
	 * @param frame the parent frame.
	 */
	public ElementList() {
		super();
		
//		addMouseListener( new DoubleClickListener() { 
//			public void doubleClicked( MouseEvent e) {
//				int index = list.getSelectedIndex();
//
//				if ( index != -1) { // A row is selected...
//					NameSelectionPopup.this.editor.insertCurrentSelectedName();
//				}
//			}
//		});

		setCellRenderer( new ElementListCellRenderer());
	}
	
	/**
	 * Sets the list of element names.
	 *
	 * @param names the list of element names.
	 */
	public void setElementNames( Vector names, boolean virtual) {
		setListModel( new ElementNameListModel( names, virtual));
	}

	/**
	 * Sets the list of element nodes.
	 *
	 * @param names the list of element nodes.
	 */
	public void setElementNodes( Vector nodes) {
		setListModel( new ElementNodeListModel( nodes));
	}

	/**
	 * Sets the list of elements.
	 *
	 * @param names the list of element names.
	 */
	public void setElements( XElement[] elements) {
		setListModel( new ElementListModel( elements));
	}

	/**
	 * Sets the list of element names.
	 *
	 * @param names the list of element names.
	 */
//	public void setEnabled( boolean enabled) {
//		this.enabled = enabled;
//		
//		if ( enabled) {
//			setBackground( Color.white);
//		} else {
//			setBackground( new Color( 204, 204, 204));
//		}
//	}
//	
//	public boolean isEnabled() {
//		return enabled;
//	}

	/**
	 * Sets the list of elements.
	 *
	 * @param elements the list of elements.
	 */
	public void setElements( Vector elements) {
		setListModel( new ElementListModel( elements));
	}
	
	private void setListModel( AbstractNamesListModel model) {
		this.model = model;

		setModel( model);
		
		if ( model.getSize() > 0) {
			if ( isEnabled()) {
				setSelectedIndex( 0);
			}
			ensureIndexIsVisible( 0);
			
			if ( model.getSize() < 10) {
				setVisibleRowCount( model.getSize());
			} else {
				setVisibleRowCount( 10);
			}
		}
	}
	
	/**
	 * Sets the list of elements.
	 *
	 * @param elements the vector of elements
	 */
	public void setElements( Vector schemaElements, Vector elementElements) {
		
		//System.out.println("schemaElements: "+schemaElements);
		//System.out.println("elementElements: "+elementElements);
		
		Vector usedElements = new Vector();
		Vector unusedElements = new Vector();
		
		if ( schemaElements != null) {
			for ( int i = 0; i < schemaElements.size();++i) {
				ElementInformation ei = (ElementInformation)schemaElements.get(i);
				if(ei instanceof SchemaElement) {
					SchemaElement sElement = (SchemaElement) ei;
					boolean found = false;
					String value = null;
					XElement xElement = null;
					//try to find it in the elements Elements
					for(int cnt=0;cnt<elementElements.size();++cnt) {
						xElement = (XElement)elementElements.get(cnt);
						if(xElement.getQualifiedName().equals(sElement.getQualifiedName())) {
							found = true;
							value = xElement.getValue();
							cnt = elementElements.size();
						}
						
					}
					
					
					ElementItem node = null;
					
					node = new ElementItem( sElement.getQualifiedName(), found);
	
					//if(found) {
						unusedElements.addElement( node);
					//}
					//else {
					//	unusedElements.addElement(node);
					//}
				}
				else if(ei instanceof DTDElement) {
					DTDElement sElement = (DTDElement) ei;
					boolean found = false;
					String value = null;
					XElement xElement = null;
					//try to find it in the elements Elements
					for(int cnt=0;cnt<elementElements.size();++cnt) {
						xElement = (XElement)elementElements.get(cnt);
						if(xElement.getQualifiedName().equals(sElement.getQualifiedName())) {
							found = true;
							value = xElement.getValue();
							cnt = elementElements.size();
						}
						
					}
					
					
					ElementItem node = null;
					
					node = new ElementItem( sElement.getQualifiedName(), found);
	
					//if(found) {
						unusedElements.addElement( node);
					//}
					//else {
					//	unusedElements.addElement(node);
					//}
				}
				else if(ei instanceof RNGElement) {
					RNGElement sElement = (RNGElement) ei;
					boolean found = false;
					String value = null;
					XElement xElement = null;
					//try to find it in the elements Elements
					for(int cnt=0;cnt<elementElements.size();++cnt) {
						xElement = (XElement)elementElements.get(cnt);
						if(xElement.getQualifiedName().equals(sElement.getQualifiedName())) {
							found = true;
							value = xElement.getValue();
							cnt = elementElements.size();
						}
						
					}
					
					
					ElementItem node = null;
					
					node = new ElementItem( sElement.getQualifiedName(), found);
	
					//if(found) {
						unusedElements.addElement( node);
					//}
					//else {
					//	unusedElements.addElement(node);
					//}
				}
			}
		}
		
		usedElements.add(unusedElements);
		//usedElements.add(elementElements);
		setListModel( new ElementListModel( usedElements /*unusedElements*/));
	}


	/**
	 * Sets the font.
	 */
	public void setPreferredFont( Font font) {
		super.setFont( font);
	}

	/**
	 * Return the current selected name.
	 *
	 * @return the selected name.
	 */
	public String getSelectedName() {
		int index = getSelectedIndex();
		
		if ( index != -1) {
			return model.getName( index);
		} else {
			return null;
		}
	}
	
	public class ElementItem {
		String qname = null;
		String prefix = null;
		String name = null;
		boolean virtual = false;
		
		public ElementItem( String qname, boolean virtual) {
			this.qname = qname;
			this.virtual = virtual;
		}

		public ElementItem( String qname) {
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
		
		public boolean isVirtual() {
			return virtual;
		}
	}

	class ElementNodeListModel extends AbstractNamesListModel {
		Vector elements = null;
		
		public ElementNodeListModel( Vector nodes) {
			elements = nodes;
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
			
			if ( object instanceof ElementNode) {
				return ((ElementNode)object).getName();
			} 

			return object.toString();
		}
	}

	class ElementListModel extends AbstractNamesListModel {
		Vector elements = null;
		
		public ElementListModel( XElement[] list) {
			elements = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.length; i++) {
					elements.addElement( list[i]);
				}
			}
		}

		public ElementListModel( Vector lists) {
			elements = new Vector();
			
			if ( lists != null) {
				for ( int i = 0; i < lists.size(); i++) {
					elements.addAll( orderElements( (Vector)lists.elementAt(i)));
				}
			}
		}
		
		private Vector orderElements( Vector list) {
			Vector elements = new Vector();
			
			for ( int i = 0; i < list.size(); i++) {
				if(list.elementAt(i) instanceof ElementInformation) {
					ElementInformation element = (ElementInformation)list.elementAt(i);
					String name = element.getQualifiedName();
					
					boolean add = true;
					int index = -1;
					
					// Find out where to insert the element...
					for ( int j = 0; j < elements.size() && index == -1; j++) {
						// Compare alphabeticaly
						int comp = 0;
						
						if(elements.elementAt(j) instanceof ElementInformation) {
							comp = name.compareToIgnoreCase( ((ElementInformation)elements.elementAt(j)).getQualifiedName());
						}
						else if(elements.elementAt(j) instanceof XElement) {
							comp = name.compareToIgnoreCase( ((XElement)elements.elementAt(j)).getQualifiedName());
						}
						else if(elements.elementAt(j) instanceof ElementItem) {
							comp = name.compareToIgnoreCase( ((ElementItem)elements.elementAt(j)).getQualifiedName());
						}
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
				else if(list.elementAt(i) instanceof XElement) {
					XElement element = (XElement)list.elementAt(i);
					String name = element.getQualifiedName();
					
					boolean add = true;
					int index = -1;
					
					// Find out where to insert the element...
					for ( int j = 0; j < elements.size() && index == -1; j++) {
						// Compare alphabeticaly
						
						int comp = 0;
						
						if(elements.elementAt(j) instanceof ElementInformation) {
							comp = name.compareToIgnoreCase( ((ElementInformation)elements.elementAt(j)).getQualifiedName());
						}
						else if(elements.elementAt(j) instanceof XElement) {
							comp = name.compareToIgnoreCase( ((XElement)elements.elementAt(j)).getQualifiedName());
						}
						else if(elements.elementAt(j) instanceof ElementItem) {
							comp = name.compareToIgnoreCase( ((ElementItem)elements.elementAt(j)).getQualifiedName());
						}
	
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
				else if(list.elementAt(i) instanceof ElementItem) {
					ElementItem element = (ElementItem)list.elementAt(i);
					String name = element.getQualifiedName();
					
					boolean add = true;
					int index = -1;
					
					// Find out where to insert the element...
					for ( int j = 0; j < elements.size() && index == -1; j++) {
						// Compare alphabeticaly
						
						int comp = 0;
						
						if(elements.elementAt(j) instanceof ElementInformation) {
							comp = name.compareToIgnoreCase( ((ElementInformation)elements.elementAt(j)).getQualifiedName());
						}
						else if(elements.elementAt(j) instanceof XElement) {
							comp = name.compareToIgnoreCase( ((XElement)elements.elementAt(j)).getQualifiedName());
						}
						else if(elements.elementAt(j) instanceof ElementItem) {
							comp = name.compareToIgnoreCase( ((ElementItem)elements.elementAt(j)).getQualifiedName());
						}
	
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
				else {
					System.err.println(list.elementAt(i)+ " is of class: "+list.elementAt(i).getClass());
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
				return ((ElementInformation)object).getQualifiedName();
			} else if ( object instanceof XElement) {
				return ((XElement)object).getQualifiedName();
			} 

			return object.toString();
		}
	}

	class ElementNameListModel extends AbstractNamesListModel {
		Vector elementNames = null;
		
		public ElementNameListModel( Vector list, boolean virtual) {
			elementNames = new Vector();
			
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					String name = (String)list.elementAt(i);
					
					// Find out where to insert the element...
					int index = -1;
					
					for ( int j = 0; j < elementNames.size() && index == -1; j++) {
						// Compare alphabeticaly
						if ( name.compareToIgnoreCase( ((ElementItem)elementNames.elementAt(j)).getQualifiedName()) <= 0) {
							index = j;
						}
					}
				
					if ( index != -1) {
						elementNames.insertElementAt( new ElementItem( name, virtual), index);
					} else {
						elementNames.addElement( new ElementItem( name, virtual));
					}
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
			
			if ( object instanceof ElementItem) {
				return ((ElementItem)object).getQualifiedName();
			} 
			
			return object.toString();
		}
	}

	abstract class AbstractNamesListModel extends AbstractListModel {
		public abstract String getName( int i);
	}

	class ElementListCellRenderer extends JPanel implements ListCellRenderer {
		protected EmptyBorder noFocusBorder;
		private JLabel icon = null;
		private JLabel prefix = null;
		private JLabel colon = null;
		private JLabel name = null;

		public ElementListCellRenderer() {
			super( new FlowLayout( FlowLayout.LEFT, 0, 0));
			
			if (noFocusBorder == null) {
			    noFocusBorder = new EmptyBorder(1, 1, 1, 1);
			}
		
			setBorder( noFocusBorder);

			icon = new JLabel();
	
			prefix = new JLabel();
			prefix.setFont( prefix.getFont().deriveFont( Font.PLAIN + Font.ITALIC));
	
			colon = new JLabel(":");
			colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
			icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));
	
			name = new JLabel();
			name.setFont( name.getFont().deriveFont( Font.BOLD));
			
			this.add( icon);
			this.add( prefix);
			this.add( colon);
			this.add( name);
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
		
			if ( value instanceof ElementInformation) {
				ElementInformation element = (ElementInformation)value;
				
				setIcon( VIRTUAL_ELEMENT_ICON);

				setPrefix( element.getPrefix());
				setText( element.getName());
			} else if ( value instanceof XElement) {
				XElement element = (XElement)value;
				setIcon( ELEMENT_ICON);

				setPrefix( element.getNamespacePrefix());
				setText( element.getName());
			} else if ( value instanceof ElementItem) {
				ElementItem element = (ElementItem)value;
				
				if ( element.isVirtual()) {
					setIcon( VIRTUAL_ELEMENT_ICON);
				} else {
					setIcon( ELEMENT_ICON);
				}

				setPrefix( element.getPrefix());
				setText( element.getName());
			} else if ( value instanceof ElementNode) {
				ElementNode element = (ElementNode)value;
				setIcon( element.getIcon());
				
				if ( element.isVirtual()) {
					setPrefix( null);
					setText( element.getType().getName());
				} else {
					setPrefix( element.getElement().getNamespacePrefix());
					setText( element.getElement().getName());
				}
			} else {
				setIcon( null);

				setPrefix( null);
				setText( value.toString());
			}

			if ( selected && list.isEnabled()) {
				setForeground( list.getSelectionForeground());
				setBackground( list.getSelectionBackground());
			} else {
				setForeground( list.getForeground());
				setBackground( list.getBackground());
			}
		
			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());
			setBorder( (focus) ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);

			return this;
		}
		
		public void setForeground( Color color) {
		
			if (icon != null) {
				icon.setForeground( color);
				prefix.setForeground( color);
				colon.setForeground( color);
				name.setForeground( color);
				super.setForeground( color);
			}
		}

		public void setBackground( Color color) {
			if (icon != null) {
				icon.setBackground( color);
				prefix.setBackground( color);
				colon.setBackground( color);
				name.setBackground( color);
				super.setBackground( color);
			}
		}

		public void setPreferredFont( Font font) {
			prefix.setFont( font.deriveFont( Font.PLAIN + Font.ITALIC));
			colon.setFont( font.deriveFont( Font.PLAIN));
			name.setFont( font.deriveFont( Font.BOLD));
			icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));
		}
		
		private void setIcon( ImageIcon image) {
			icon.setIcon( image);
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

		private void setText( String value) {
			name.setText( value);
		}
	}
} 
