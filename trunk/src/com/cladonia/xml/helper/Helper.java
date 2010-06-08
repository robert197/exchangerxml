/*
 * $Id: Helper.java,v 1.13 2005/08/31 16:19:34 tcurley Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.helper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.bounce.FormLayout;
import org.bounce.event.DoubleClickListener;
import org.dom4j.Node;

import com.cladonia.schema.ComplexSchemaType;
import com.cladonia.schema.ElementInformation;
import com.cladonia.schema.SchemaAttribute;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaType;
import com.cladonia.schema.XMLSchema;
import com.cladonia.schema.dtd.DTDAttribute;
import com.cladonia.schema.dtd.DTDElement;
import com.cladonia.schema.rng.RNGAttribute;
import com.cladonia.schema.rng.RNGElement;
import com.cladonia.schema.viewer.SchemaNode;
import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.schema.viewer.SchemaViewerDetails;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.AttributeNode;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.designer.DesignerNode;
import com.cladonia.xml.designer.ElementNode;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.editor.Tag;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
//import com.cladonia.xml.grid.GridTableNode;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.NavigationPanel;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The context sensitive element, attribute, entity selection popup.
 *
 * @version	$Revision: 1.13 $, $Date: 2005/08/31 16:19:34 $
 * @author Dogsbay
 */
public class Helper extends NavigationPanel implements Editor.CurrentElementListener, Editor.CurrentElementsListener, Editor.CurrentAttributesListener{

	private static final CompoundBorder LIST_TITLE_BORDER = 
		new CompoundBorder( 
				new CompoundBorder(
						new MatteBorder( 1, 1, 0, 0, UIManager.getColor("controlDkShadow")),
						new MatteBorder( 0, 0, 0, 1, Color.white)),
				new CompoundBorder( 
					new CompoundBorder(
							new MatteBorder( 1, 1, 0, 0, Color.white),
							new MatteBorder(0, 0, 0, 1, UIManager.getColor("controlDkShadow"))),
					new EmptyBorder( 0, 5, 0, 5)));

	private static final ImageIcon HELPER_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/HelperIcon.gif");
	private static final ImageIcon ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ElementIcon.gif");
	private static final ImageIcon UNKNOWN_ELEMENT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ForeignElementIcon.gif");

	private ExchangerEditor parent	= null;
	private HelperProperties properties = null;
	
	private JPanel switchPanel = null;

	private JSplitPane split = null;
	
	private SchemaViewerDetails schemaDetails	= null;

	private AttributeList attributeList	= null;
	private ElementList elementList		= null;
	
	private Object element = null;

	private JLabel icon = null;
	private JLabel prefix = null;
	private JLabel colon = null;
	private JLabel name = null;
	private JTextField namespace = null;
	private JTextField type = null;

	/**
	 * The name selection popup.
	 *
	 * @param frame the parent frame.
	 */
	public Helper( ExchangerEditor parent, HelperProperties properties) {
		super();
		
		this.parent = parent;
		this.properties = properties;
		
		icon = new JLabel( ELEMENT_ICON);
		icon.setBorder( null);

		prefix = new JLabel();
		prefix.setVisible( false);
//		prefix.setFont( prefix.getFont().deriveFont( Font.PLAIN + Font.ITALIC));

		colon = new JLabel(":");
		colon.setVisible( false);
//		colon.setFont( colon.getFont().deriveFont( Font.PLAIN));
//		icon.setPreferredSize( new Dimension( 16, colon.getPreferredSize().height));

		name = new JLabel();
		JPanel main = new JPanel( new BorderLayout());
		//		name.setFont( name.getFont().deriveFont( Font.BOLD));
		
		JPanel northPanel = new JPanel( new FormLayout( 2, 2));
		northPanel.setBorder( new EmptyBorder( 2, 2, 10, 2));
		JPanel prefixPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
		JPanel elementPanel = new JPanel( new BorderLayout());
		
		prefixPanel.add( icon);
		prefixPanel.add( prefix);
		prefixPanel.add( colon);

		elementPanel.add( prefixPanel, BorderLayout.WEST);
		elementPanel.add( name, BorderLayout.CENTER);
		
		northPanel.add( elementPanel, FormLayout.FULL_FILL);
		
		namespace = createValueTextField();
		
//		JLabel nsLabel = new JLabel( "xmlns:");
//		nsLabel.setFont( nsLabel.getFont().deriveFont( Font.BOLD, (float)11));
//		northPanel.add( nsLabel, FormLayout.LEFT);
		northPanel.add( namespace, FormLayout.FULL_FILL);

		type = createValueTextField();
//		type.setPreferredSize( new Dimension( 100, 12));

//		JLabel typeLabel = new JLabel( "type:");
//		typeLabel.setFont( typeLabel.getFont().deriveFont( Font.BOLD, (float)11));
//		northPanel.add( typeLabel, FormLayout.LEFT);
		northPanel.add( type, FormLayout.FULL_FILL);

		main.add( northPanel, BorderLayout.NORTH);


		JPanel elementListPanel = new JPanel( new BorderLayout());
//		elementListPanel.setBorder( new CompoundBorder(
//			new MatteBorder( 1, 1, 1, 1, new Color( 102, 102, 102)),
//			new EmptyBorder( 2, 2, 2, 2)));
//		elementListPanel.setBorder( 
//					new CompoundBorder(
//						new EtchedBorder(),
//						new EmptyBorder( 0, 2, 2, 2)));

		JLabel elementsLabel = new JLabel( "Elements:");
//		elementsLabel.setBorder( new EmptyBorder( 0, 2, 0, 0));
		elementsLabel.setBorder( LIST_TITLE_BORDER);
		elementListPanel.add( elementsLabel, BorderLayout.NORTH);
		elementList = new ElementList();
		
		elementList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = elementList.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					if ( elementList.isEnabled()) {
						elementDoubleClicked( elementList.getSelectedValue());
					}
				}
			}
		});
		
		elementList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = elementList.getSelectedIndex();
                if(selected>-1) {
                    elementList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		JScrollPane elementScroller = new JScrollPane( elementList);
		elementListPanel.add( elementScroller, BorderLayout.CENTER);
		
		JPanel attributeListPanel = new JPanel( new BorderLayout());
//		attributeListPanel.setBorder( new CompoundBorder(
//			new MatteBorder( 1, 1, 1, 1, new Color( 102, 102, 102)),
//			new EmptyBorder( 2, 2, 2, 2)));

//		attributeListPanel.setBorder( 
//				new CompoundBorder(
//					new EtchedBorder(),
//					new EmptyBorder( 0, 2, 2, 2)));

		JLabel attributesLabel = new JLabel( "Attributes:");
		attributesLabel.setBorder( LIST_TITLE_BORDER);
//		attributesLabel.setBorder( new EmptyBorder( 0, 2, 0, 0));

		attributeListPanel.add( attributesLabel, BorderLayout.NORTH);
		attributeList = new AttributeList();
		
		attributeList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = attributeList.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					// do attribute stuff...
					if ( attributeList.isEnabled()) {
						attributeDoubleClicked( attributeList.getSelectedValue());
					}
				}
			}
		});
		
		attributeList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent arg0) {
                int selected = attributeList.getSelectedIndex();
                if(selected>-1) {
                    attributeList.ensureIndexIsVisible(selected);
                }
            }
		    
		});
		
		JScrollPane attributeScroller = new JScrollPane( attributeList);
		attributeListPanel.add( attributeScroller, BorderLayout.CENTER);
		
		split = new JSplitPane( JSplitPane.VERTICAL_SPLIT, attributeListPanel, elementListPanel);
		split.setResizeWeight( 0.5);
		split.setDividerSize( 2);

		Object ui = split.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}
		split.setBorder( null);
		split.setDividerLocation( properties.getDividerLocation());

		main.add( split, BorderLayout.CENTER);
		
		schemaDetails = new SchemaViewerDetails( parent);
		add( main, "default");
		add( schemaDetails, "schema");
		
		show( "default");
		
		updatePreferences();
	}
	
	private void elementDoubleClicked( Object value) {
		
		if ( value instanceof ElementList.ElementItem) {
			ElementList.ElementItem item = (ElementList.ElementItem)value;
			handleElement( item.getQualifiedName());
		} else if ( value instanceof XElement) {
			XElement element = (XElement)value;
			handleElement( element);
		} else if ( value instanceof ElementInformation) {
			ElementInformation element = (ElementInformation)value;
			handleElement( element.getQualifiedName());
		} else if ( value instanceof ElementNode) {
			handleNode( (ElementNode)value);
		}
		
	}

	private void handleElement( String name) {
		if ( name != null && name.length() > 0) {
			final Object current = parent.getView().getCurrentView();
			
			if ( current instanceof Editor) {
				((Editor)current).insertTag( name);
				((Editor)current).setFocus();
			}
			else if( current instanceof PluginViewPanel) {
				
				((PluginViewPanel)current).addNewElementToSelected(name);				
							
			}
		}
	}
	
	private void handleElement( XElement element) {
		if ( element != null) {
			Object current = parent.getView().getCurrentView();
			
			if ( current instanceof Viewer) {
				((Viewer)current).setSelectedElement( element, false, -1);
				((Viewer)current).setFocus();
			}
		}
	}

	public void setProperties() {
		properties.setDividerLocation( split.getDividerLocation());
	}

	private void handleNode( DesignerNode node) {
		if ( node != null) {
			Object current = parent.getView().getCurrentView();
			
			if ( current instanceof Designer) {
				((Designer)current).add( node);
//				((Designer)current).setFocus();
			}
		}
	}

	private void attributeDoubleClicked( Object value) {
		if ( value instanceof AttributeList.AttributeItem) {
			AttributeList.AttributeItem item = (AttributeList.AttributeItem)value;
			handleAttribute( item.getQualifiedName(), item.getValue(), item.isUsed());
		} else if ( value instanceof AttributeNode) {
			handleNode( (AttributeNode)value);
		}
	}

	private void handleAttribute( String name, String value, boolean used) {
		if ( !used) {
			if ( name != null && name.length() > 0) {
				Object current = parent.getView().getCurrentView();
				
				if ( current instanceof Editor) {
					((Editor)current).appendAttribute( name);
					((Editor)current).setFocus();
				}
				else if(current instanceof PluginViewPanel) {
					((PluginViewPanel)current).addNewAttributeToSelected( name);
					
				}
			}
		} else {
			if ( name != null && name.length() > 0) {
				Object current = parent.getView().getCurrentView();
				
				if ( current instanceof Editor) {
					((Editor)current).selectAttributeValue( name, value);
					((Editor)current).setFocus();
				}
				else if(current instanceof PluginViewPanel){
					((PluginViewPanel)current).selectAttribute(name);					
				}
			}
		}
	}

	public void updateInformation() {
//		System.out.println( "Helper.updateInformation() ["+element+"]");
		if ( element != null) {
			if ( element instanceof XElement) {
				setElementInternal( (XElement)element);
			} else if ( element instanceof Tag) {
				setElementInternal( (Tag)element);
//			} else if ( element instanceof SchemaElement) {
//				setElementInternal( (SchemaElement)element);
			} else if ( element instanceof SchemaNode) {
				setElementInternal( (SchemaNode)element);
			} else if ( element instanceof ElementNode) {
				setElementInternal( (ElementNode)element);
			}
			
		} else {
			if ( parent.getView() != null) {
				Object view = parent.getView().getCurrentView();

				if ( view instanceof Editor) {
					setElementInternal( (Tag)null);
				} else if ( view instanceof Viewer) {
					setElementInternal( (XElement)null);
				} else if ( view instanceof SchemaViewer) {
					setElementInternal( (SchemaNode)null);
				} else if ( view instanceof Designer) {
					setElementInternal( (ElementNode)null);
				} else if ( view instanceof Designer) {
					setElementInternal( (XElement)null);
				} else if (view instanceof PluginViewPanel) {
					setElementInternal( (XElement)null, null);
				} else {
					clear();
				}
			} else {
				clear();
			}
		}
	}
	
	public void updatePreferences() {
		Font base = TextPreferences.getBaseFont();
		
		prefix.setFont( base.deriveFont( Font.ITALIC));
		
		schemaDetails.updatePreferences();

		colon.setFont( base.deriveFont( Font.PLAIN));
		icon.setPreferredSize( new Dimension( 20, colon.getPreferredSize().height));

		name.setFont( base.deriveFont( Font.BOLD));
		namespace.setFont( namespace.getFont().deriveFont( Font.PLAIN, (float)11));
		type.setFont( type.getFont().deriveFont( Font.PLAIN, (float)11));
		
		elementList.setPreferredFont( base);
		attributeList.setPreferredFont( base);
	}
	
	private void setPrefix( String text) {
		if ( text != null && text.trim().length() > 0) {
			prefix.setText( text);
			colon.setVisible( true);
			prefix.setVisible( true);
		} else {
			colon.setVisible( false);
			prefix.setVisible( false);
		}
	}

	private void setElementDetails( String elementName, String elementPrefix, boolean unknown) {
		if ( unknown || elementName == null || elementName.trim().length() == 0) {
			icon.setIcon( UNKNOWN_ELEMENT_ICON);
		} else {
			icon.setIcon( ELEMENT_ICON);
		}
		
		setPrefix( elementPrefix);
		
		if ( elementName != null && elementName.trim().length() > 0) {
			name.setText( elementName);
			name.setForeground( Color.black);
		} else {
			name.setText( "none");
		}
	}

	public void clear() {
		this.element = null;

		setElementDetails( null, null, false);

		setText( namespace, "");
		
		setType( null);

		elementList.setElements( new Vector());
		attributeList.setAttributes( null);
	}

	public void setElement( Tag tag) {
		if ( tag != null && tag.equals( element)) {
			return;
		}
		
		element = tag;
		
		if ( isVisible()) {
			setElementInternal( tag);
		}
	}

	public void setElement( XElement element) {
		this.element = element;

		if ( isVisible()) {
			setElementInternal( element);
		}
	}
	
	public void setElement( XElement element, ElementInformation ei) {
		this.element = element;

		if ( isVisible()) {
			setElementInternal( element, ei);
		}
	}
	
	
	/*public void setElement( GridTableNode node) {
		this.element = node;

		if ( isVisible()) {
			setElementInternal( node);
		}
	}*/

	public void setElement( SchemaNode element) {
		this.element = element;

		if ( isVisible()) {
			setElementInternal( element);
		}
	}

	public void setElement( ElementNode node) {
		this.element = node;
		
		if ( isVisible()) {
			setElementInternal( node);
		}
	}

	private void setElementInternal( SchemaNode node) {
		schemaDetails.setNode( node);
	}

	private void setElementInternal( ElementNode node) {
		attributeList.setEnabled( true);
		elementList.setEnabled( true);

		setElementDetails( node.getElement().getName(), node.getElement().getNamespacePrefix(), false);

		setNamespace( node.getElement().getNamespaceURI());
		
		SchemaElement type = node.getType();
		
		if ( type != null) {
			setType( type.getSchemaType());
		} else {
			setType( null);
		}
		
		Vector attributeNodes = new Vector();
		Vector elementNodes = new Vector();
		
		Enumeration children = node.children();
		
		while ( children.hasMoreElements()) {
			Object child = children.nextElement();

			if ( child instanceof AttributeNode) {
				attributeNodes.addElement( child);
			} else if ( child instanceof ElementNode) {
				elementNodes.addElement( child);
			}
		}

		elementList.setElementNodes( elementNodes);
		attributeList.setAttributeNodes( attributeNodes);
	}
	
	private void setElementInternal( Tag tag) {
//		System.out.println( "Helper.setElementInternal( "+tag+")");

		Vector allElements = parent.getView().getAllElements();
		if ( allElements != null && allElements.size() > 0) {
			parent.getView().getEditor().getCurrentElement( this);
			parent.getView().getEditor().getCurrentElements( this);
			parent.getView().getEditor().getCurrentAttributes( this);
			
//			if ( tag != null) {
//				ElementInformation element = getElementModel( tag);
//
//				if ( element == null) {
//					setElementDetails( tag.getName(), tag.getPrefix(), true);
//					setText( namespace, "unknown");
//					setType( null);
//
//					element = getParentElementModel( tag);
//
//					if ( element != null) {
//						elementList.setElements( element.getChildElements());
//						attributeList.setAttributes( null, null, null);
//					} else {
//						elementList.setElements( parent.getView().getGlobalElements());
//						attributeList.setAttributes( null, null, null);
//					}
//				} else {
//					setElementDetails( tag.getName(), tag.getPrefix(), false);
//					
//					setNamespace( element.getNamespace());
//					setType( element.getType());
//
//					elementList.setElements( element.getChildElements());
//					
//					if ( tag.isIncomplete()) {
//						attributeList.clearSelection();
//						attributeList.setEnabled( false);
//					} else {
//						attributeList.setEnabled( true);
//					}
//	
//					attributeList.setAttributes( getAttributes( tag.getName()), tag.getAttributeNames(), tag.getAttributeValues());
//						
//				}
//			} else {
//				setElementDetails( null, null, true);
//				setText( namespace, "unknown");
//				setType( null);
//
//				elementList.setElements( parent.getView().getGlobalElements());
//				attributeList.setAttributes( null, null, null);
//			}
		} else {
			if (tag != null) {
				if ( tag.isIncomplete()) {
					attributeList.clearSelection();
					attributeList.setEnabled( false);
				} else {
					attributeList.setEnabled( true);
				}

				setElementDetails( tag.getName(), tag.getPrefix(), false);
			} else {
				attributeList.clearSelection();
				attributeList.setEnabled( false);
				setElementDetails( null, null, true);
			}

			setText( namespace, "");
			setType( null);

			elementList.setElementNames( parent.getView().getElementNames(), true);
			
			if ( tag != null) {
				attributeList.setAttributeNames( parent.getView().getAttributeNames(), tag.getAttributeNames(), tag.getAttributeValues()); 
			} else {
				attributeList.setAttributeNames( parent.getView().getAttributeNames(), null, null); 
			}
		}
	}

	public Vector getAttributeNames(XElement element) {
//		System.out.println( "getAttributeNames()");
		XAttribute[] attributes = element.getAttributes();
		Vector names = new Vector();
		String[] sNames = new String[attributes.length];
		
		for ( int i = 0; i < attributes.length; i++) {
			//sNames[i] = attributes[i].getQualifiedName();
			names.add(attributes[i].getQualifiedName());
		}
		//names.add(sNames);
		return(names);
		
	}
	

	
	
	public Vector getAttributeValues(XElement element) {
		
		Vector atts = new Vector();
		
		for(int cnt=0;cnt<element.attributeCount();++cnt) {
			atts.add(element.attribute(cnt).getValue());
		}
		
		return(atts);
	}
	
	private void setElementInternal( XElement element) {
		
		Vector allElements = parent.getView().getAllElements();
		
		attributeList.setEnabled( true);
		elementList.setEnabled( true);

		setElementDetails( element.getName(), element.getNamespacePrefix(), false);

		setNamespace( element.getNamespaceURI());

		ElementInformation model = getElementModel( element);
		
		Vector elementAttributes = new Vector();
		Vector schemaAttributes = new Vector();
		
		Vector elementChildren = new Vector();
		Vector schemaChildren = new Vector();
		
		if ( model != null) {
			setText( type, model.getType());
			
			Vector modAtts = model.getAttributes();
			if(modAtts != null) {
				
				
				for(int cnt=0;cnt<modAtts.size();++cnt) {
					SchemaAttribute sa = (SchemaAttribute) modAtts.get(cnt);
					schemaAttributes.add(sa);
				}
				
			}
			
			Vector modElements = model.getChildElements();
			if(modElements != null) {
				
				for(int cnt=0;cnt<modElements.size();++cnt) {
					SchemaElement sa = (SchemaElement) modElements.get(cnt);
					schemaChildren.add(sa);
				}
				
			}
		
			
		} else {
			setType( null);
		}	
		
		for(int cnt=0;cnt<element.attributeCount();++cnt) {
			
			elementAttributes.add((XAttribute)element.attribute(cnt));
		}
		
		XElement[] childElements = element.getElements();
		for(int cnt=0;cnt<childElements.length;++cnt) {
			elementChildren.add((XElement)childElements[cnt]);
		}		
		
		//this.ementList.setElements( element.getElements());
		//this.elementList.setElements( newElementArray);
		//attributeList.setAttributes( element.getAttributes());
		//attributeList.setAttributes( newAttArray);
		attributeList.setAttributes(schemaAttributes, elementAttributes);
		elementList.setElements(schemaChildren, elementChildren);
	
	}
	
	private void setElementInternal( Node baseNode, ElementInformation ei) {
		try {
			XElement node = null;
			
			if(baseNode.getNodeType() == org.dom4j.Node.ATTRIBUTE_NODE) {
				node = (XElement) baseNode.getParent();
			}
			else if(baseNode.getNodeType() == org.dom4j.Node.ELEMENT_NODE) {
				node = (XElement) baseNode;
			}
			
			SchemaElement element = null;
			
			if(ei != null) {
				
				if(ei instanceof SchemaElement) {
					element = (SchemaElement)ei;
					attributeList.setEnabled( true);
					elementList.setEnabled( true);
							
					setElementDetails( node.getName(), node.getNamespace().getPrefix(), false);
		
					setNamespace( node.getNamespace().getURI());
					
					Vector elementAttributes = new Vector();
					Vector schemaAttributes = new Vector();
					
					Vector elementChildren = new Vector();
					Vector schemaChildren = new Vector();
					
					if ( element != null) {
						setText( type, element.getType());
						
						Vector modAtts = element.getAttributes();
						if(modAtts != null) {
							
							
							for(int cnt=0;cnt<modAtts.size();++cnt) {
								SchemaAttribute sa = (SchemaAttribute) modAtts.get(cnt);
								schemaAttributes.add(sa);
							}
							
						}
						
						Vector modElements = element.getChildElements();
						if(modElements != null) {
							
							for(int cnt=0;cnt<modElements.size();++cnt) {
								SchemaElement sa = (SchemaElement) modElements.get(cnt);
								schemaChildren.add(sa);
							}
							
						}
					
						
					} else {
						setType( null);
					}	
					
					for(int cnt=0;cnt<node.attributeCount();++cnt) {
						
						elementAttributes.add((XAttribute)node.attribute(cnt));
					}
					
					XElement[] childElements = node.getElements();
					for(int cnt=0;cnt<childElements.length;++cnt) {
						
						elementChildren.add((XElement)childElements[cnt]);
					}		
								
		
					//this.ementList.setElements( element.getElements());
					//this.elementList.setElements( newElementArray);
					//attributeList.setAttributes( element.getAttributes());
					//attributeList.setAttributes( newAttArray);
					attributeList.setAttributes(schemaAttributes, elementAttributes);
					elementList.setElements(schemaChildren, elementChildren);
				}
				else if(ei instanceof DTDElement) {
					DTDElement dtdElement = (DTDElement)ei;
					attributeList.setEnabled( true);
					elementList.setEnabled( true);
							
					setElementDetails( node.getName(), node.getNamespace().getPrefix(), false);
		
					setNamespace( node.getNamespace().getURI());
					
					Vector elementAttributes = new Vector();
					Vector schemaAttributes = new Vector();
					
					Vector elementChildren = new Vector();
					Vector schemaChildren = new Vector();
					
					if ( dtdElement != null) {
						setText( type, dtdElement.getType());
						
						Vector modAtts = dtdElement.getAttributes();
						if(modAtts != null) {
							
							
							for(int cnt=0;cnt<modAtts.size();++cnt) {
								DTDAttribute sa = (DTDAttribute) modAtts.get(cnt);
								schemaAttributes.add(sa);
							}
							
						}
						
						Vector modElements = dtdElement.getChildElements();
						if(modElements != null) {
							
							for(int cnt=0;cnt<modElements.size();++cnt) {
								DTDElement sa = (DTDElement) modElements.get(cnt);
								schemaChildren.add(sa);
							}
							
						}
					
						
					} else {
						setType( null);
					}	
					
					for(int cnt=0;cnt<node.attributeCount();++cnt) {
						
						elementAttributes.add((XAttribute)node.attribute(cnt));
					}
					
					XElement[] childElements = node.getElements();
					for(int cnt=0;cnt<childElements.length;++cnt) {
						
						elementChildren.add((XElement)childElements[cnt]);
					}		
								
		
					//this.ementList.setElements( element.getElements());
					//this.elementList.setElements( newElementArray);
					//attributeList.setAttributes( element.getAttributes());
					//attributeList.setAttributes( newAttArray);
					attributeList.setAttributes(schemaAttributes, elementAttributes);
					elementList.setElements(schemaChildren, elementChildren);
				}
				else if(ei instanceof RNGElement) {
					RNGElement rngElement = (RNGElement)ei;
					attributeList.setEnabled( true);
					elementList.setEnabled( true);
							
					setElementDetails( node.getName(), node.getNamespace().getPrefix(), false);
		
					setNamespace( node.getNamespace().getURI());
					
					Vector elementAttributes = new Vector();
					Vector schemaAttributes = new Vector();
					
					Vector elementChildren = new Vector();
					Vector schemaChildren = new Vector();
					
					if ( rngElement != null) {
						setText( type, rngElement.getType());
						
						Vector modAtts = rngElement.getAttributes();
						if(modAtts != null) {
							
							
							for(int cnt=0;cnt<modAtts.size();++cnt) {
								RNGAttribute sa = (RNGAttribute) modAtts.get(cnt);
								schemaAttributes.add(sa);
							}
							
						}
						
						Vector modElements = rngElement.getChildElements();
						if(modElements != null) {
							
							for(int cnt=0;cnt<modElements.size();++cnt) {
								RNGElement sa = (RNGElement) modElements.get(cnt);
								schemaChildren.add(sa);
							}
							
						}
					
						
					} else {
						setType( null);
					}	
					
					for(int cnt=0;cnt<node.attributeCount();++cnt) {
						
						elementAttributes.add((XAttribute)node.attribute(cnt));
					}
					
					XElement[] childElements = node.getElements();
					for(int cnt=0;cnt<childElements.length;++cnt) {
						
						elementChildren.add((XElement)childElements[cnt]);
					}		
								
		
					//this.ementList.setElements( element.getElements());
					//this.elementList.setElements( newElementArray);
					//attributeList.setAttributes( element.getAttributes());
					//attributeList.setAttributes( newAttArray);
					attributeList.setAttributes(schemaAttributes, elementAttributes);
					elementList.setElements(schemaChildren, elementChildren);
				}
				else {
					
				}
			}
			else {
				//System.out.println("one is null");
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	private void setType( SchemaType schemaType) {
		if ( schemaType != null) {
			String name = schemaType.getName();
			
			if ( schemaType instanceof ComplexSchemaType) {
				if ( name != null) {
					setText( type, "complex:"+name);
				} else {
					setText( type, "complex");
				}
			} else {
				if ( name != null) {
					setText( type, "simple:"+schemaType.getName());
				} else {
					setText( type, "simple");
				}
			}
		} else {
			setText( type, "");
		}
	}

	private void setNamespace( String ns) {
		if ( ns != null && ns.trim().length() > 0) {
			setText( namespace, ns);
		} else {
			setText( namespace, "");
		}
	}

	public void setElementsEnabled( boolean enabled) {
		if ( !enabled) {
			elementList.clearSelection();
		}

		elementList.setEnabled( enabled);
	}

	public boolean isElementsEnabled() {
		return elementList.isEnabled();
	}

	public void setAttributesEnabled( boolean enabled) {
		if ( !enabled) {
			attributeList.clearSelection();
		}

		attributeList.setEnabled( enabled);
	}

	private ElementInformation getElementModel( XElement element) {
		Vector elements = parent.getView().getAllElements();
		Vector possibleModels = new Vector();
		if ( elements != null) {
			for ( int i = 0; i < elements.size(); i++) {
				ElementInformation model = (ElementInformation)elements.elementAt(i);
				
				if ( element.getName().equals( model.getName())) {
					
					possibleModels.add(model);
					
				}
			}
		}
		
		if(possibleModels.size() > 0) {
			
			if(possibleModels.size() == 1) {
				return((ElementInformation)possibleModels.get(0));
			}
			else {
				
				//have to search and match parents
				for(int cnt=0;cnt<possibleModels.size();++cnt) {
					
					ElementInformation model = (ElementInformation)possibleModels.get(cnt);
				
					if(model instanceof SchemaElement) {
						
						SchemaElement se = (SchemaElement)model;
						se.getChildElements();
						System.out.println(se.getName()+" - "+se.getChildElements());
						//System.out.println(se.getName()+" -parent: "+model.getParentName()+" - type: "+model.getType());
						
						/*try {
							
							SchemaElement sp = se.getParentElement();
							if((sp.isEmpty() == true) && (sp.isGlobal() == true)) {
								Vector referers = sp.getReferers();
								for(int rcnt=0;rcnt<referers.size();++rcnt) {
									SchemaElement sr = (SchemaElement)referers.get(rcnt);
									System.out.println("referer: "+sr.getName());
								}
							}
							while(sp.isEmpty() == true) {
								System.out.println("next Parent is empty: "+sp.getName());
								sp = sp.getParentElement();
								
							}
							
							System.out.println("real parent"+sp.getName());
						} catch (NullPointerException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
						}*/
											
						
						
						
					}
					/*String parentName = null;
					if(element.isRoot() == false) {
						
						parentName = element.getParent().getName(); 
					}
					
					if(element.getParent().isRootElement() == true) {
						System.out.println("element parent is root");
						parentName = null;
						if(model.getParentName() == null) {
							System.out.println("models parent is root");
							return(model);
						}
					}
					
					if((parentName !=null) && (parentName.equals(model.getParentName()))) {*/
					
				}
				return(null);
			}
		}
		else {

			return null;
		}
	}

	private Vector getChildElements( String tagName) {
		Vector elements = parent.getView().getAllElements();

		if ( tagName != null) {
			for ( int i = 0; i < elements.size(); i++) {
				ElementInformation model = (ElementInformation)elements.elementAt(i);
				
				if ( tagName.equals( model.getName())) {
					return model.getChildElements();
				}
			}
		} 

		return parent.getView().getGlobalElements();
	}
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 6));

		return separator;
	}
	
	/**
	 * Sets the current view.
	 *
	 * @param view the current view.
	 */
	public void setView( Object view) {
		if ( view instanceof SchemaViewer) {
			//show( "schema");
			show( "default");
		} else {
			show( "default");
		}
	}

	public void setSchema( XMLSchema schema) {
		schemaDetails.setSchema( schema);
	}

	protected JTextField createValueTextField() {
		JTextField field = new JTextField();
		field.setBorder( null);
		field.setOpaque( false);
		field.setEditable( false);

		return field;
	}

	private void setText( JTextField field, String text) {
		field.setText( text);
		field.setCaretPosition(0);
		field.setToolTipText( text);
	}
	
	public void setCurrentElement( final ElementInformation info) {
//		System.out.println( "Helper.setCurrentElement( "+info+")");

		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if ( info != null) {
					setElementDetails( info.getName(), info.getPrefix(), false);
					setText( namespace, info.getNamespace());
					setText( type, info.getType());
				} else if ( element instanceof Tag) {
					Tag tag = (Tag)element;
		
					setElementDetails( tag.getName(), tag.getPrefix(), true);
					setText( namespace, "");
					setType( null);
				} else {
					setElementDetails( null, null, true);
					setText( namespace, "");
					setType( null);
				}
			}
		});
	}

	public void setCurrentElements( final Vector elements) {
//		System.out.println( "Helper.setElements( "+elements+")");
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				elementList.setElements( elements);
			}
		});
	}

	public void setCurrentAttributes( final Vector attributes) {
//		System.out.println( "Helper.setAttributes( "+attributes+")");

		try {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					if(element instanceof Tag) {
						Tag tag = (Tag)element;
						
						if ( tag != null) {
							if ( tag.isIncomplete()) {
								attributeList.clearSelection();
								attributeList.setEnabled( false);
							} else {
								attributeList.setEnabled( true);
							}

							attributeList.setAttributes( attributes, tag.getAttributeNames(), tag.getAttributeValues());
						} else {
							attributeList.setAttributes( null, null, null);
						}
					}
					else if(element instanceof XElement) {
						XElement tag = (XElement)element;
						
						if ( tag != null) {
							
							attributeList.setEnabled( true);
							attributeList.setAttributes( attributes, getAttributeNames(tag), getAttributeValues(tag));
						} else {
							attributeList.setAttributes( null, null, null);
						}
					}
					else {
						//System.err.println("unknown type: "+element.getClass());
					}
				}
			});
		} catch (Exception e){
			
			//e.printStackTrace();
		}
	}
	
	
	
} 

