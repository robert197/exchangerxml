/*
 * $Id: ElementNode.java,v 1.2 2004/09/23 10:37:30 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xml.designer;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import org.bounce.image.ImageUtilities;
import org.dom4j.Namespace;
import org.dom4j.Node;

import com.cladonia.schema.SchemaAttribute;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.SchemaModel;
import com.cladonia.schema.SimpleSchemaType;
import com.cladonia.xml.XAttribute;
import com.cladonia.xml.XElement;
import com.cladonia.xml.designer.model.ElementBox;
import com.cladonia.xml.designer.model.ModelBase;
import com.cladonia.xngreditor.XngrImageLoader;

/**
 * The default node for a element.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/09/23 10:37:30 $
 * @author Dogsbay
 */
public class ElementNode extends DesignerNode {
	private static final boolean DEBUG = false;

	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ElementIcon.gif");
	private static final ImageIcon REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/RequiredElementIcon.gif");
	private static final ImageIcon FOREIGN_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/ForeignElementIcon.gif");
	private static final ImageIcon VIRTUAL_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualElementIcon.gif");
	private static final ImageIcon VIRTUAL_REQUIRED_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualRequiredElementIcon.gif");
	private static final ImageIcon VIRTUAL_CHOICE_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualChoiceElementIcon.gif");
	private static final ImageIcon VIRTUAL_ABSTRACT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualAbstractElementIcon.gif");
	private static final ImageIcon VIRTUAL_REQUIRED_ABSTRACT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualRequiredAbstractElementIcon.gif");
	private static final ImageIcon VIRTUAL_CHOICE_ABSTRACT_ICON = XngrImageLoader.get().getImage( "com/cladonia/xml/designer/icons/VirtualChoiceAbstractElementIcon.gif");

	private DefaultTreeModel treeModel = null;

	private SchemaElement type = null;
	private SchemaElement substitutedType = null;
	private XElement element = null;
	private ElementNode parent = null;
	private ModelBase model = null;
	private ElementBox box = null;
	
	/**
	 * The constructor for a virtual element node.
	 *
	 * @param parent this nodes parent node.
	 * @param type the schema element type definition.
	 */
	public ElementNode( DefaultTreeModel model, ElementNode parent, SchemaElement type) {
		this( model, parent, type, null);
	}

	/**
	 * The constructor for the element node.
	 *
	 * @param parent this nodes parent node.
	 * @param type the schema element type.
	 * @param element the element declaration for the node.
	 */
	public ElementNode( DefaultTreeModel model, ElementNode parent, SchemaElement type, XElement element) {
		if (DEBUG) System.out.println( "ElementNode( "+model+", "+parent+", "+type+", "+element+")");
		
		this.treeModel	= model;
		this.element	= element;
		this.type		= type;
		this.parent		= parent;
		
		if ( type != null) {
			type.resolveReference();
			type.recurse();
			
			// Handle substitutions!
			if ( type.isAbstract() && element != null) {
				Vector substitutes = type.getSubstituteElements();
				
				for ( int i = 0; i < substitutes.size(); i++) {
					SchemaElement substitute = (SchemaElement)substitutes.elementAt(i);

					if ( substitute.getName().equals( element.getName())) {
						substitutedType = this.type;
						this.type = substitute;

						this.type.resolveReference();
						this.type.recurse();
						break;
					}
				}
			}
		} 

		if ( element != null) {
			initNodes();
		} 
	}
	
	public Object takeSnapShot() {
		return new SnapShot( this);
	}
	
	public void setSnapShot( Object snapShot) {
//		System.out.println( "ElementNode.setSnapShot( "+snapShot+")");
		SnapShot snap = (SnapShot)snapShot;
		
		element = snap.element;
		box = snap.box;
		type = snap.type;
		substitutedType = snap.substitutedType;
		
		if ( box != null) {
			box.set( element);
		}

		removeAllChildren();		
		
		for ( int i = 0; i < snap.childNodes.size(); i++) {
			DesignerNode node = (DesignerNode)snap.childNodes.elementAt(i);
//			System.out.println( "add Node: "+node);
			add( node);
//			treeModel.nodeChanged( node);
		}
		
		if ( element != null) {
			element.removeAllChildren();

			for ( int i = 0; i < snap.childElements.size(); i++) {
				Object node = (Object)snap.childElements.elementAt(i);
//				System.out.println( "add Element/Attribute: "+node);
				element.addChild( node);
			}
		}

//		update();
		treeModel.nodeStructureChanged( this);
	}

	/**
	 * Shows the virtual nodes!
	 */
	public void showVirtualNodes() {
		if (DEBUG) System.out.println( "ElementNode.showVirtualNodes()");

		if ( model == null && type != null && !isForeign()) {
			// SET ATTRIBUTES
			// remove all previous attributes, to make sure they are in the same order always
			int counter = 0;

			while ( counter < getChildCount()) {
				DesignerNode n = (DesignerNode)getChildAt( counter);

				if ( n instanceof AttributeNode) {
					remove( n);
				} else {
					counter++;
				}
			}
			
			Vector nodes = new Vector();
			Vector attributeTypes = type.getAttributes();

			if ( attributeTypes != null) {
				for ( int i = 0; i < attributeTypes.size(); i++) {
					SchemaAttribute attributeType = (SchemaAttribute)attributeTypes.elementAt(i);

					// Don't allow prohibited attribute nodes to be created.
					if ( attributeType.getUse() != SchemaAttribute.USE_PROHIBITED) {
						AttributeNode node = new AttributeNode( this, attributeType);
						nodes.addElement( node);

						insert( node, nodes.size()-1);
					}
				}
			}
	
			// Fill the nodes with attributes!
			XAttribute[] attributes = element.getAttributes();
			
			for ( int i = 0; i < attributes.length; i++) {
				XAttribute attribute = attributes[i];
				boolean found = false;
				
				for ( int j = 0; j < nodes.size(); j++) {
					AttributeNode node = (AttributeNode)nodes.elementAt(j);
	
					if ( node.getName().equals( attribute.getName())) {
						node.setAttribute( attribute);
						found = true;
						break;
					}
				}
				
				if ( !found) { // could not find type for attribute (Foreign Attribute!)
					insert( new AttributeNode( this, null, attribute), nodes.size());
				}
			}
			
			// SET ELEMENTS
			model = new ModelBase( type);

			Vector boxes = model.getElementBoxes();
			XElement[] elements = element.getElements();
			XElement prev = null; // previous element
			
			// feed it the elements...
			for ( int i = 0; i < elements.length; i++) {
				XElement e = elements[i];
				boolean found = false;
				boolean prevFound = false;
				
//				System.out.println("Element = "+elements[i].getName());

				for ( int j = 0; j < boxes.size(); j++) {
					// continue after previous element
					ElementBox box = (ElementBox)boxes.elementAt(j);
					
					if ( !prevFound && prev != null) {
						if ( box.get() == prev) {
							prevFound = true;
						}

					// set when required... 
					} else if ( !box.isOptional() && box.isSameType( e)) {
						box.set( e);
						found = true;
//						System.out.println("Required Box Found");
						break;
					} 
				}
				
				if ( !found) {
					prevFound = false;

					for ( int j = 0; j < boxes.size(); j++) {
						// continue after previous element
						ElementBox box = (ElementBox)boxes.elementAt(j);

						if ( !prevFound && prev != null) {
							if ( box.get() == prev) {
								prevFound = true;
							}

						// set when not required... 
						} else if ( box.isSameType( e)) {
							box.set( e);
							found = true;
//							System.out.println("Not Required Box Found");
							break;
						}
					}
				}
				
				if ( !found) {
					System.err.println( "ERROR: Could not find place for element <"+e.getName()+">");
				} else {
					model.update();
					boxes = model.getElementBoxes();
					prev = e;
				}
			}
			
			int nodePointer = 0;

			Enumeration children = children();
			
			while ( children.hasMoreElements()) {
				if ( children.nextElement() instanceof AttributeNode) {
					nodePointer++;
				}
			}
				
			model.update();
			boxes = model.getElementBoxes();
			
			for ( int i = 0; i < boxes.size(); i++) {
				ElementBox e = (ElementBox)boxes.elementAt(i);
				
//				System.out.println( e.toString());

				// add an abstract node!
				if ( e.isEmpty()) {
					ElementNode n = new ElementNode( treeModel, this, e.getType());
					n.setBox( e);

//					insert( n, i + attributes);
					insert( n, nodePointer);
					nodePointer++;
//					System.out.println( "Insert node["+(i+attributes)+"] = "+n.getType().getName());

				} else { // associate an ElementNode with the box
//					System.out.println( "Update node["+(i+attributes)+"] = "+e);
					XElement ex = e.get();
					ElementNode en = null;
					
					Enumeration dns = children();
					while ( dns.hasMoreElements()) {
						DesignerNode dn = (DesignerNode)dns.nextElement();
						if ( dn instanceof ElementNode) {
							if ( ex == ((ElementNode)dn).getElement()) {
								en = (ElementNode)dn;
								break;
							}
						}
					}
					
					

					en.setBox( e);
					nodePointer = getIndex( en) + 1;
					
//					ElementNode n = (ElementNode)getChildAt( i + attributes);
//					if ( e.isSameType( n.getElement())) {
//						n.setBox( e);
//					}

//					System.out.println( "Update node["+(i+attributes)+"] = "+n.getType().getName());
				}
			}

//			if ( required) {
//				createRequired();
//			}
			
			treeModel.nodeStructureChanged( this);
		}
	}

	/**
	 * Is this element required or optional.
	 *
	 * @return true when this element is required.
	 */
	public boolean isRequired() {
		if ( box != null) {
			return !box.isOptional();
		}

		return false;
	}

	/**
	 * Is this element part of a choice model.
	 *
	 * @return true when this element is part of a choice model.
	 */
	public boolean isChoice() {
		if ( box != null) {
			return box.isChoice();
		}

		return false;
	}

	/**
	 * Creates required elements and attributes
	 */
	public void createRequired() {
		Enumeration nodes = children();
		
		while ( nodes.hasMoreElements()) {
			DesignerNode node = (DesignerNode)nodes.nextElement();
			
			if ( node instanceof ElementNode) {
				if ( ((ElementNode)node).isVirtual() && ((ElementNode)node).isRequired() && !((ElementNode)node).isAbstract()) {
					addNode( (ElementNode)node);
				}
			} else if ( node instanceof AttributeNode) {
				if ( ((AttributeNode)node).isVirtual() && ((AttributeNode)node).isRequired()) {
					addNode( (AttributeNode)node);
				}
			}

			update();
		}
	}
	
	/**
	 * Sets this element box.
	 *
	 * @param box the element box.
	 */
	public void setBox( ElementBox box) {
		if (DEBUG) System.out.println( "ElementNode.setBox( "+box+") ["+getName()+"]");
		this.box = box;
	}

	/**
	 * Gets this element box.
	 *
	 * @return the element box.
	 */
	public ElementBox getBox() {
		return box;
	}

	/**
	 * Find out if the virtual element nodes have been 
	 * created for this node!
	 *
	 * @return true when the virtual element nodes have been created.
	 */
	public boolean virtualNodesVisible() {
		return (model != null || type == null || isForeign());
	}

	/**
	 * Remove an element from this node and inform its parent.
	 */
	public void remove() {
		if ( !isVirtual() && parent != null) {
			if ( substitutedType != null) {
				this.type = substitutedType;
				substitutedType = null;
			}
			
			if ( box != null) {
				box.set( null);
			}
			
			model = null;
			parent.removeNode( this);
//			parent.update();
		}
	}
	
	/**
	 * Substitutes this nodes element and inform its parent.
	 */
	public void substitute( SchemaElement type, boolean required) {
		if ( !isForeign() && isVirtual() && parent != null) {
			this.substitutedType = this.type;
			this.type = type;

			add( required);
		}
	}

	/**
	 * Add an element to this node and inform its parent.
	 */
	public void add( boolean required) {
		if ( !isForeign() && isVirtual() && parent != null) {
			parent.addNode( this);
			showVirtualNodes();

			if ( required) {
				createRequired();
			} else {
				parent.update();
			}
			treeModel.nodeStructureChanged( this);
		}
	}

	/**
	 * Gets the value for the element.
	 *
	 * @return the value.
	 */	
	public String getValue() {
		String value = null;
		
		if ( element != null) {
			value = element.getValue();
		}
	
		return value;
	}

	/**
	 * Sets the value in the element.
	 *
	 * @param value the value.
	 */	
	public void setValue( String value) {
		if ( element != null) {
			element.setValue( value);
		}
	}

	/**
	 * The element represented by this node.
	 *
	 * @return the element.
	 */
	public XElement getElement() {
		return element;
	}

	/**
	 * Sets the element represented by this node.
	 *
	 * @param element the element.
	 */
	public void setElement( XElement element) {
		this.element = element;
		
		if ( box != null) {
			box.set( element);
		}

		if ( element != null) {
			initNodes();
		} else {
			removeAllChildren();
		}
	}

	/**
	 * The type represented by this node.
	 *
	 * @return the type.
	 */
	public SchemaElement getType() {
		return type;
	}

	/**
	 * Returns true when the node is virtual, does not have a element.
	 *
	 * @return true when node is virtual.
	 */
	public boolean isVirtual() {
		return element == null;
	}

	/**
	 * Returns true when the node is foreign, does not have a type.
	 *
	 * @return true when node is foreign.
	 */
	public boolean isForeign() {
		return (box == null && getParentElementNode() != null);
	}

	/**
	 * The name for this node.
	 *
	 * @return the name for the element.
	 */
	public String getName() {
		if ( element != null) {
			return element.getName();
		} else if ( type != null) {
			return type.getName();
		} else {
			return null;
		}
	}

	/**
	 * The description for this node.
	 *
	 * @return the description for the element.
	 */
	public String getDescription() {
		return getName();
	}

	/**
	 * Returns the icon that is shown when the node is selected.
	 *
	 * @return the selected icon.
	 */
	public ImageIcon getSelectedIcon() {
		ImageIcon icon = getIcon();

		if ( icon != null) {
			icon = ImageUtilities.createDarkerImage( icon);
		}
		
		return icon;
	}

	/**
	 * The icon for this node.
	 *
	 * @return the icon for the element.
	 */
	public ImageIcon getIcon() {
		if ( isVirtual()) {
			if ( isAbstract()) {
				if ( isChoice()) {
					return VIRTUAL_CHOICE_ABSTRACT_ICON;
				} else if ( isRequired()) {
					return VIRTUAL_REQUIRED_ABSTRACT_ICON;
				} else {
					return VIRTUAL_ABSTRACT_ICON;
				}
			} else {
				if ( isChoice()) {
					return VIRTUAL_CHOICE_ICON;
				} else if ( isRequired()) {
					return VIRTUAL_REQUIRED_ICON;
				} else {
					return VIRTUAL_ICON;
				}
			}
		} else {
			if ( isForeign()) {
				return FOREIGN_ICON;
			} else if ( isRequired()) {
				return REQUIRED_ICON;
			} else {
				return ICON;
			}
		}
	}
	
	/**
	 * Returns a string version of this node.
	 *
	 * @return the name for the element.
	 */
	public String toString() {
		return getName();
	}
	
	/**
	 * When the node is abstract and needs to be substituted 
	 * by a different node.
	 *
	 * @return true when the node is abstract.
	 */
	public boolean isAbstract() {
		if ( isVirtual() && box != null) {
			return box.isAbstract();
		}
		
		return false;
	}

	/**
	 * The parent for this node.
	 *
	 * @return the parent for the element.
	 */
	public ElementNode getParentElementNode() {
		return parent;
	}

	/**
	 * Wether the current uri has already been declared 
	 * as a namespace?
	 *
	 * @param uri the namespace uri.
	 *
	 * @return true when the namespace has been declared by a parent element.
	 */
	public boolean isNamespaceDeclared() {
		XElement element = parent.getElement();
		Namespace ns = element.getNamespaceForURI( type.getNamespace());
		
		if (DEBUG)  {
			if ( ns != null) {
				System.out.println( "ns: xmlns:"+ns.getPrefix()+"="+ns.getURI());
			}
		}
		
		return ns != null;
	}

	/**
	 * Adds an element to the virtual node supplied.
	 *
	 * @param node the virtual node.
	 */
	protected void addNode( ElementNode node) {
		XElement e = null;
		SchemaElement type = node.getType();
		String uri = type.getNamespace();
		Namespace ns = element.getNamespaceForURI( uri);
		String prefix = null;

		if ( ns != null) {
			prefix = ns.getPrefix();
		}
			
		if ( prefix != null) {
			e = new XElement( type.getName(), uri, prefix, false);
		} else {
			e = new XElement( type.getName(), uri, false);
		}
		
		addNode( node, e);
	}

	/**
	 * Adds an element to the virtual node supplied.
	 *
	 * @param node the virtual node.
	 */
	protected void addNode( ElementNode node, XElement e) {
		SchemaElement type = node.getType();

		// >> Set the default or fixed value
		if ( type.getSchemaType() instanceof SimpleSchemaType) {
			String value = type.getFixed();
		
			if ( value == null) {
				value = type.getDefault();
			}
			
			if ( value != null) {
				e.setValue( value);
			}
		}
		// <<
		int index = 0; 
		
		for ( int i = getIndex( node); i > 0; i--) {
			DesignerNode n = (DesignerNode)getChildAt( i-1);
			
			if ( n instanceof ElementNode && !((ElementNode)n).isVirtual()) {
				index++;
			}
		}
		
		// Set the element in the node 
		element.insert( index, e);
//		element.add( e);
		node.setElement( e);
	}
	
	public void update() {
		if (DEBUG) System.out.println( "ElementNode.update()"); 

//		if ( type != null) {
		if ( !isForeign()) {
			// Update the model and get the 'new' list of elements		
			model.update();
			Vector boxes = model.getElementBoxes();
	
			int nodeIndex = 0;

			Enumeration children = children();
			Vector virtualElements = new Vector();
			
			while ( children.hasMoreElements()) {
				DesignerNode child = (DesignerNode)children.nextElement();
		
				if ( child instanceof AttributeNode) {
					nodeIndex++;
				} else if ( child instanceof ElementNode) {
					if ( ((ElementNode)child).isVirtual()) {
						virtualElements.addElement( child);
					}
				}
			}
			
			// Remove all virtual elements (the boxes are not the same any more)
			for ( int i = 0; i < virtualElements.size(); i++) {
				ElementNode element = (ElementNode)virtualElements.elementAt(i);
				
				treeModel.removeNodeFromParent( element);
//				System.out.println( "Removing \""+element.getName()+"\" ...");
			}
			
			for ( int i = 0; i < boxes.size(); i++) {
				ElementBox box = (ElementBox)boxes.elementAt( i);
				ElementNode node = (nodeIndex) < getChildCount() ? (ElementNode)getChildAt( nodeIndex) : null;
//				System.out.println( "Looking for Box "+box+" ...");

				if ( node == null) {
//					System.out.println( "No node found, creating new ...");
					ElementNode n = new ElementNode( treeModel, this, box.getType());
					n.setBox( box);

					treeModel.insertNodeInto( n, this, nodeIndex);

					nodeIndex++;
				} else if ( node.getBox() != box) {
//					System.out.println("Current node "+node.getName()+" does not have the box ...");
					// Check foreign elements to see if any of the elements would fit the box???
					boolean nodeFound = false;
					ElementNode fn = null;

					// Look forward to see if there are any foreign elements that might fit the box?
					for ( int j = nodeIndex; j < getChildCount(); j++) {
						ElementNode n = (ElementNode)getChildAt( j);

						if ( n.isForeign()) {
							if ( box.isSameType( n.getElement()) && fn == null) {
								fn = n;
							}
						} else if ( box == n.getBox()) {
							nodeFound = true;
							fn = n;
							break;
						}
					}
					
					if ( fn != null) {
						int index = getIndex( fn);
						
						if ( !nodeFound) {
//							System.out.println( "Found foreign node "+fn.getName()+" for box, at: "+index);
							box.set( fn.getElement());
							fn.setBox( box);
						} else {
//							System.out.println( "Found node "+fn.getName()+" for box, at: "+index);
						}
			
						nodeIndex = index;
					} else {
//						System.out.println( "Creating new node for box!");
					
						// Create new node for box!
						ElementNode n = new ElementNode( treeModel, this, box.getType());
						n.setBox( box);

						treeModel.insertNodeInto( n, this, nodeIndex);
						nodeIndex++;
					}
				} else {
					nodeIndex++;
				}
			}
		} else { // this is a foreign node, 
		}
	}

	/**
	 * Adds an attribute to the virtual node supplied.
	 *
	 * @param node the virtual node.
	 */
	protected void addNode( AttributeNode node) {
		if (DEBUG) System.out.println( "ElementNode.addNode( "+node+")");
		SchemaAttribute type = node.getType();
		XAttribute a = new XAttribute( type.getName());
		
		String value = type.getFixed();
		
		if ( value == null) {
			value = type.getDefault();
		}
		
		if ( value == null) {
			value = "";
		}

		a.setValue( value);
		
		element.putAttribute( a);
		node.setAttribute( a);

		treeModel.nodeChanged( node);
	}

	/**
	 * Removes an attribute from the node supplied.
	 *
	 * @param node the node.
	 */
	protected void removeNode( AttributeNode node) {
		element.remove( node.getAttribute());
		node.setAttribute( null);
//		node.removeAllChildren();

		treeModel.nodeChanged( node);
	}

	/**
	 * Removes an element from the node supplied.
	 *
	 * @param node the node.
	 */
	protected void removeNode( ElementNode node) {
		element.remove( node.getElement());
		node.setElement( null);
		node.removeAllChildren();

		treeModel.nodeStructureChanged( node);
//		treeModel.nodeChanged( node);
		
		update();
	}

	private void initNodes() {
		// Fill the nodes with attributes!
		XAttribute[] attributes = element.getAttributes();
		
		for ( int i = 0; i < attributes.length; i++) {
			add( new AttributeNode( this, null, attributes[i]));
		}
		
		XElement[] elements = element.getElements();
		
		// feed it the elements...
		for ( int i = 0; i < elements.length; i++) {
			XElement element = elements[i];

			add( new ElementNode( treeModel, this, getType( element), element));
		}
		
		treeModel.nodeStructureChanged( this);
	}

	/**
	 * Returns the type for the element.
	 */
	private SchemaElement getType( XElement element) {
		SchemaElement elementType = null;
		
		if ( type != null) {
			Vector models = type.getModels();
			
			if ( models != null) {
				for ( int i = 0; i < models.size(); i++) {
					elementType = getType( (SchemaModel)models.elementAt(i), element);
					
					if ( elementType != null) {
						return elementType;
					}
				}
			}
		}
		
		if ( elementType == null) {
			// System.err.println( "WARNING: Foreign Element: "+element.getName());
		}
		
		return elementType;
	}

	/**
	 * Returns the type for the element if found in the model.
	 */
	private SchemaElement getType( SchemaModel model, XElement element) {
		SchemaElement result = null;
		Vector elements = model.getElements();
		
		if ( elements != null) {
			for ( int i = 0; i < elements.size(); i++) {
				SchemaElement type = (SchemaElement)elements.elementAt(i);

				// the reference needs to be resolved to find out wether the type is abstract??
				if ( type.isReference()) {
					type.resolveReference();
				}
//				System.out.println( "Type = "+type.getName()+" Element = "+element.getName()+" abstract?"+type.isAbstract()+" reference?"+type.isReference());

				if ( type.isAbstract()) {
//					System.out.println( type.getName()+" [abstract]");
// ED: SUBS			Vector substitutes = type.getSubstitutes();
					Vector substitutes = type.getSubstituteElements();
					
					for ( int j = 0; j < substitutes.size(); j++) {
						SchemaElement substitute = (SchemaElement)substitutes.elementAt(j);

						if ( substitute.getName().equals( element.getName())) {
//							System.out.println( substitute.getName()+" [substitute] ( type = "+substitute.getType()+")");
							return substitute; // return type;
						}
						
					}
				} else if ( type.getName().equals( element.getName())) {
					return type;
				}
			}
		}
		
		Vector models = model.getModels();

		if ( models != null) {
			for ( int i = 0; i < models.size(); i++) {
				result = getType( (SchemaModel)models.elementAt(i), element);
				
				if ( result != null) {
					return result;
				}
			}
		} 
		
		return result;
	}

	/**
	 * Returns the type for the element.
	 */
	private SchemaAttribute getType( XAttribute attribute) {
		if ( type != null) {
			Vector attributes = type.getAttributes();
			
			if ( attributes != null) {
				for ( int i = 0; i < attributes.size(); i++) {
					SchemaAttribute attributeType = (SchemaAttribute)attributes.elementAt(i);

					if ( attributeType.getName().equals( attribute.getName())) {
						return attributeType;
					}
					
				}
			}
		}
		
		return null;
	}
	
	private int indexOf( ElementBox box) {
		Enumeration nodes = children();
		
		while ( nodes.hasMoreElements()) {
			DesignerNode node = (DesignerNode)nodes.nextElement();
			
			if ( node instanceof ElementNode) {
				if ( ((ElementNode)node).getBox() == box) {
					return getIndex( node);
				}
			}
		}
		
		return -1;
	}
	
	private class SnapShot {
		public SchemaElement type = null;
		public SchemaElement substitutedType = null;

		public XElement element = null;
		public ElementBox box = null;
		public Vector childNodes = new Vector();
		public Vector childElements = null;
	
		public SnapShot( ElementNode node) {
			Enumeration nodes = node.children();
			
			while ( nodes.hasMoreElements()) {
				DesignerNode n = (DesignerNode)nodes.nextElement();
			
				childNodes.addElement( n);
				if (DEBUG) System.out.println( "SnapShot() child nodes :"+n.getName());
			}
			
			element = node.element;
			box = node.box;
			type = node.type;
			substitutedType = node.substitutedType;

			if ( element != null) {
				childElements = element.getChildren();
				
				if (DEBUG) {
					for ( int i = 0; i < childElements.size(); i++) {
						Object child = childElements.elementAt(i);
					
						if ( child instanceof Node) {
							System.out.println( "SnapShot() child elements :"+((Node)child).getName());
						} else if ( child instanceof XAttribute) {
							System.out.println( "SnapShot() child elements :"+((XAttribute)child).getName());
						}
					}
				}
			} else {
				if (DEBUG) System.out.println( "element = null");
			}
		}
	}
} 
