/*
 * $Id: AttributeList.java,v 1.4 2005/08/31 16:19:34 tcurley Exp $
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


import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.SchemaAttribute;
import com.cladonia.schema.dtd.DTDAttribute;
import com.cladonia.schema.rng.RNGAttribute;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.designer.AttributeNode;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The context sensitive element, attribute, entity selection popup.
 *
 * @version	$Revision: 1.4 $, $Date: 2005/08/31 16:19:34 $
 * @author Dogsbay
 */
public class AttributeList extends JList {

	private static final ImageIcon USED_ATTRIBUTE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/AttributeIcon.gif");
	private static final ImageIcon USED_ATTRIBUTE_REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/RequiredAttributeIcon.gif");
	private static final ImageIcon UNUSED_ATTRIBUTE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualAttributeIcon.gif");
	private static final ImageIcon UNUSED_ATTRIBUTE_REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualRequiredAttributeIcon.gif");

	private AbstractAttributeListModel model	= null;

	/**
	 * The name selection popup.
	 *
	 * @param frame the parent frame.
	 */
	public AttributeList() {
		super();
		
//		list.addMouseListener( new DoubleClickListener() { 
//			public void doubleClicked( MouseEvent e) {
//				int index = list.getSelectedIndex();
//
//				if ( index != -1) { // A row is selected...
//					NameSelectionPopup.this.editor.insertCurrentSelectedName();
//				}
//			}
//		});
		
		setCellRenderer( new AttributeListCellRenderer());
	}
	
	/**
	 * Sets the list of attributes.
	 *
	 * @param attributes the list of attributes (SchemaAttribute).
	 * @param usedAttributeNames the already used attributes (String[], XAttribute).
	 */
	public void setAttributes( Vector lists, Vector usedAttributeNames, Vector usedAttributeValues) {
		Vector usedAttributes = new Vector();
		Vector unusedAttributes = new Vector();
		
		if ( lists != null) {
			for ( int k = 0; k < lists.size(); k++) {
				Vector attributes = (Vector)lists.elementAt(k);
				if ( attributes != null) {
					for ( int i = 0; i < attributes.size(); i++) {
						AttributeInformation attribute = (AttributeInformation)attributes.elementAt(i);
						boolean found = false;
						
						if ( usedAttributeNames != null) {
							for ( int j = 0; j < usedAttributeNames.size(); j++) {
								//String[] names = (String[])usedAttributeNames.elementAt(j);
								//for(int cnt=0;cnt<names.length;++cnt) {
									//System.out.println("names: "+names[cnt]);
									//String name = names[cnt];
								String name = (String)usedAttributeNames.elementAt(j);
									if ( name.equals( attribute.getQualifiedName())) {
										AttributeItem node = null;
										String value = null;
			
										if ( usedAttributeValues != null) {
											
											value = (String)usedAttributeValues.elementAt(j);
										}
										
										if ( attribute.isRequired()) {
											node = new AttributeItem( attribute.getQualifiedName(), value, true, true);
										} else {
											node = new AttributeItem( attribute.getQualifiedName(), value, true, false);
										}
					
										usedAttributes.addElement( node);
										found = true;
										break;
									}
								//}
							}
						}
						
						if ( !found) {
							AttributeItem node = null;
							
							if ( attribute.isRequired()) {
								node = new AttributeItem( attribute.getQualifiedName(), false, true);
							} else {
								node = new AttributeItem( attribute.getQualifiedName(), false, false);
							}
		
							unusedAttributes.addElement( node);
						}
					}
				}
			}
		}
		
		setListModel( new AttributeListModel( usedAttributes, unusedAttributes));
	}
	
	/**
	 * Sets the list of attribute nodes.
	 *
	 * @param nodes the list of attribute nodes.
	 */
	public void setAttributeNodes( Vector nodes) {
		setListModel( new AttributeNodeListModel( nodes));
	}

	/**
	 * Sets the list of attributes.
	 *
	 * @param attributes the list of attributes (XAtribute).
	 */
	public void setAttributes( XAttribute[] attributes) {
		Vector usedAttributes = new Vector();
		
		if ( attributes != null) {
			for ( int i = 0; i < attributes.length; i++) {
				XAttribute attribute = attributes[i];
				boolean found = false;
				AttributeItem node = null;
				
				node = new AttributeItem( attribute.getQualifiedName(), attribute.getValue(), true, false);

				usedAttributes.addElement( node);
			}
		}
		
		setListModel( new AttributeListModel( usedAttributes, new Vector()));
	}
	
	/**
	 * Sets the list of attributes.
	 *
	 * @param attributes the vector of attributes
	 */
	public void setAttributes( Vector schemaAttributes, Vector elementAttributes) {
		
		//System.out.println("schemaAttributes: "+schemaAttributes);
		//System.out.println("elementAttributes: "+elementAttributes);
		
		Vector usedAttributes = new Vector();
		Vector unusedAttributes = new Vector();
		
		if ( schemaAttributes != null) {
			for ( int i = 0; i < schemaAttributes.size();++i) {
				AttributeInformation ai = (AttributeInformation) schemaAttributes.get(i);
				if(ai instanceof SchemaAttribute) {
				
					SchemaAttribute attribute = (SchemaAttribute) ai;
					boolean found = false;
					String value = null;
					
					//try to find it in the elements attributes
					for(int cnt=0;cnt<elementAttributes.size();++cnt) {
						XAttribute xatt = (XAttribute)elementAttributes.get(cnt);
						if(xatt.getQualifiedName().equals(attribute.getQualifiedName())) {
							found = true;
							value = xatt.getValue();
							cnt = elementAttributes.size();
						}
						
					}
					
					
					AttributeItem node = null;
					
					node = new AttributeItem( attribute.getQualifiedName(), value, found, attribute.isRequired());
	
					if(found) {
						usedAttributes.addElement( node);
					}
					else {
						unusedAttributes.addElement(node);
					}
				}
				else if(ai instanceof DTDAttribute) {
					DTDAttribute attribute = (DTDAttribute) ai;
					boolean found = false;
					String value = null;
					
					//try to find it in the elements attributes
					for(int cnt=0;cnt<elementAttributes.size();++cnt) {
						XAttribute xatt = (XAttribute)elementAttributes.get(cnt);
						if(xatt.getQualifiedName().equals(attribute.getQualifiedName())) {
							found = true;
							value = xatt.getValue();
							cnt = elementAttributes.size();
						}
						
					}
					
					
					AttributeItem node = null;
					
					node = new AttributeItem( attribute.getQualifiedName(), value, found, attribute.isRequired());
	
					if(found) {
						usedAttributes.addElement( node);
					}
					else {
						unusedAttributes.addElement(node);
					}
				}
				else if(ai instanceof RNGAttribute) {
					RNGAttribute attribute = (RNGAttribute) ai;
					boolean found = false;
					String value = null;
					
					//try to find it in the elements attributes
					for(int cnt=0;cnt<elementAttributes.size();++cnt) {
						XAttribute xatt = (XAttribute)elementAttributes.get(cnt);
						if(xatt.getQualifiedName().equals(attribute.getQualifiedName())) {
							found = true;
							value = xatt.getValue();
							cnt = elementAttributes.size();
						}
						
					}
					
					
					AttributeItem node = null;
					
					node = new AttributeItem( attribute.getQualifiedName(), value, found, attribute.isRequired());
	
					if(found) {
						usedAttributes.addElement( node);
					}
					else {
						unusedAttributes.addElement(node);
					}
				}
			}
		}
		
		setListModel( new AttributeListModel( usedAttributes, unusedAttributes));
	}

	
	
	/**
	 * Sets the list of attributes-names and the current selected name.
	 *
	 * @param attributeNames the list of names.
	 * @param usedAttributeNames the list of already used attribute names.
	 */
	public void setAttributeNames( Vector attributeNames, Vector usedAttributeNames, Vector usedAttributeValues) {
		Vector usedAttributes = new Vector();
		Vector unusedAttributes = new Vector();

		if ( attributeNames != null) {
			for ( int i = 0; i < attributeNames.size(); i++) {
				String attribute = (String)attributeNames.elementAt(i);
				boolean found = false;
				
				if ( usedAttributeNames != null) {
					for ( int j = 0; j < usedAttributeNames.size(); j++) {
						String name = (String)usedAttributeNames.elementAt(j);
						
						if ( name.equals( attribute)) {
							String value = null;

							if ( usedAttributeValues != null) {
								value = (String)usedAttributeValues.elementAt(j);
							}

							usedAttributes.addElement( new AttributeItem( attribute, value, true, false));

							found = true;
							break;
						}
					}
				}
				
				if ( !found) {
					unusedAttributes.addElement( new AttributeItem( attribute, false, false));
				}
			}
		}
		
		setListModel( new AttributeListModel( usedAttributes, unusedAttributes));
	}

	private void setListModel( AbstractAttributeListModel model) {
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
	public String getSelectedAttributeName() {
		int index = getSelectedIndex();
		
		if ( index != -1) {
			return model.getName( index);
		} else {
			return null;
		}
	}
	
	/**
	 * Return the current selected value.
	 *
	 * @return the selected value.
	 */
	public String getSelectedAttributeValue() {
		int index = getSelectedIndex();
		
		if ( index != -1) {
			return model.getValue( index);
		} else {
			return null;
		}
	}

	/**
	 * Return the current selected name.
	 *
	 * @return the selected name.
	 */
	public boolean isSelectedAttributeUsed() {
		int index = getSelectedIndex();
		
		if ( index != -1) {
			return model.isUsed( index);
		} else {
			return false;
		}
	}

	public class AttributeItem {
		String qname = null;
		String prefix = null;
		String name = null;
		String value = null;
		boolean used = false;
		boolean required = false;
		
		public AttributeItem( String qname, String value, boolean used, boolean required) {
			this.qname = qname;
			this.value = value;
			this.used = used;
			this.required = required;
		}

		public AttributeItem( String qname, boolean used, boolean required) {
			this( qname, null, used, required);
		}

		public AttributeItem( String qname, String value, boolean used) {
			this( qname, value, used, false);
		}

		public AttributeItem( String qname, boolean used) {
			this( qname, null, used, false);
		}

		public AttributeItem( String qname, String value) {
			this( qname, value, false, false);
		}

		public AttributeItem( String qname) {
			this( qname, false, false);
		}
		
		public boolean isUsed() {
			return used;
		}

		public boolean isRequired() {
			return required;
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

		public String getValue() {
			return value;
		}

		public String getQualifiedName() {
			return qname;
		}
	}

	class AttributeListModel extends AbstractAttributeListModel {
		Vector attributes = null;
		
		public AttributeListModel( Vector usedAttributes, Vector unusedAttributes) {
			attributes = new Vector();
			
			orderAttributes( usedAttributes);
			orderAttributes( unusedAttributes);
		}
			
		private void orderAttributes( Vector unorderedAttributes) {
			Vector requiredAttributes = new Vector();
			Vector optionalAttributes = new Vector();

			for ( int i = 0; i < unorderedAttributes.size(); i++) {
				AttributeItem attribute = (AttributeItem)unorderedAttributes.elementAt(i);
				String name = attribute.getQualifiedName();
				
				int index = -1;
				boolean add = true;
				
				if ( attribute.isRequired()) {
					for ( int j = 0; j < requiredAttributes.size() && index == -1; j++) {
						// Compare alphabeticaly
						int comp = name.compareToIgnoreCase( ((AttributeItem)requiredAttributes.elementAt(j)).getQualifiedName());

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
						// Compare alphabeticaly
						int comp = name.compareToIgnoreCase( ((AttributeItem)optionalAttributes.elementAt(j)).getQualifiedName());

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
			
			for ( int i = 0; i < requiredAttributes.size(); i++) {
				attributes.addElement( requiredAttributes.elementAt(i));
			}				

			for ( int i = 0; i < optionalAttributes.size(); i++) {
				attributes.addElement( optionalAttributes.elementAt(i));
			}				
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
			
			if ( object instanceof AttributeItem) {
				return ((AttributeItem)object).getQualifiedName();
			} 
			
			return (String)object;
		}

		public String getValue( int i) {
			Object object = attributes.elementAt( i);
			
			if ( object instanceof AttributeItem) {
				return ((AttributeItem)object).getValue();
			} 
			
			return (String)object;
		}

		public boolean isUsed( int i) {
			Object object = attributes.elementAt( i);
			
			if ( object instanceof AttributeItem) {
				return ((AttributeItem)object).isUsed();
			} 
			
			return false;
		}
	}

	class AttributeNodeListModel extends AbstractAttributeListModel {
		Vector attributes = null;
		
		public AttributeNodeListModel( Vector nodes) {
			attributes = nodes;
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
			
			if ( object instanceof AttributeNode) {
				return ((AttributeNode)object).getName();
			} 
			
			return (String)object;
		}

		public String getValue( int i) {
			Object object = attributes.elementAt( i);
			
			if ( object instanceof AttributeNode) {
				return ((AttributeNode)object).getAttribute().getValue();
			} 
			
			return (String)object;
		}

		public boolean isUsed( int i) {
			Object object = attributes.elementAt( i);
			
			if ( object instanceof AttributeNode) {
				return !((AttributeNode)object).isVirtual();
			} 
			
			return false;
		}
	}

	abstract class AbstractAttributeListModel extends AbstractListModel {
		public abstract String getName( int i);
		public abstract boolean isUsed( int i);
		public abstract String getValue( int i);
	}

	class AttributeListCellRenderer extends JPanel implements ListCellRenderer {
		protected EmptyBorder noFocusBorder;
		private JLabel icon = null;
		private JLabel prefix = null;
		private JLabel colon = null;
		private JLabel name = null;
		private JLabel value = null;

		public AttributeListCellRenderer() {
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
			
			value = new JLabel();
			value.setFont( name.getFont().deriveFont( Font.PLAIN));

			this.add( icon);
			this.add( prefix);
			this.add( colon);
			this.add( name);
			this.add( value);
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean selected, boolean focus) {
		
			if ( value instanceof AttributeItem) {
				AttributeItem attribute = (AttributeItem)value;
				
				if ( attribute.isUsed()) {
					if ( attribute.isRequired()) {
						setIcon( USED_ATTRIBUTE_REQUIRED_ICON);
						setForeground( list.getForeground());
					} else {
						setIcon( USED_ATTRIBUTE_ICON);
						setForeground( list.getForeground());
					}
				} else {
					if ( attribute.isRequired()) {
						setIcon( UNUSED_ATTRIBUTE_REQUIRED_ICON);
						setForeground( Color.red);
					} else {
						setIcon( UNUSED_ATTRIBUTE_ICON);
						setForeground( list.getForeground());
					}
				}
	
				setPrefix( attribute.getPrefix());
				setText( attribute.getName());
				setValue( attribute.getValue());
			} else if ( value instanceof AttributeNode) {
				AttributeNode attribute = (AttributeNode)value;
				
				setIcon( attribute.getIcon());
				
				if ( attribute.isVirtual() && attribute.isRequired()) {
					setForeground( Color.red);
				} else {
					setForeground( list.getForeground());
				}
				
				if ( attribute.isVirtual()) {
					AttributeInformation type = attribute.getType();

					setPrefix( null);
					setText( type.getName());
					setValue( null);
				} else {
					XAttribute impl = attribute.getAttribute();
					setPrefix( impl.getNamespacePrefix());
					setText( impl.getName());
					setValue( impl.getText());
				}
			} else {
				setIcon( null);
				setForeground( list.getForeground());

				setPrefix( null);
				setText( value.toString());
			}

			if ( selected && list.isEnabled()) {
				setForeground( list.getSelectionForeground());
				setBackground( list.getSelectionBackground());
			} else {
				setBackground(list.getBackground());
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
				value.setForeground( color);
				super.setForeground( color);
			}
		}

		public void setBackground( Color color) {
			if (icon != null) {
				icon.setBackground( color);
				prefix.setBackground( color);
				colon.setBackground( color);
				name.setBackground( color);
				value.setBackground( color);
				super.setBackground( color);
			}
		}

		public void setPreferredFont( Font font) {
			prefix.setFont( font.deriveFont( Font.ITALIC));
			colon.setFont( font.deriveFont( Font.PLAIN));
			name.setFont( font.deriveFont( Font.BOLD));
			value.setFont( font.deriveFont( Font.PLAIN));
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

		private void setValue( String text) {
			if ( text != null) {
				value.setText( "=\""+text+"\"");
			} else {
				value.setText( "");
			}
		}
	}
} 
