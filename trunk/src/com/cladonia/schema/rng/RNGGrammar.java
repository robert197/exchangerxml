package com.cladonia.schema.rng;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.thaiopensource.relaxng.edit.AbstractRefPattern;
import com.thaiopensource.relaxng.edit.Annotated;
import com.thaiopensource.relaxng.edit.AnnotationChild;
import com.thaiopensource.relaxng.edit.AnyNameNameClass;
import com.thaiopensource.relaxng.edit.AttributeAnnotation;
import com.thaiopensource.relaxng.edit.AttributePattern;
import com.thaiopensource.relaxng.edit.ChoiceNameClass;
import com.thaiopensource.relaxng.edit.ChoicePattern;
import com.thaiopensource.relaxng.edit.Comment;
import com.thaiopensource.relaxng.edit.Component;
import com.thaiopensource.relaxng.edit.ComponentVisitor;
import com.thaiopensource.relaxng.edit.CompositePattern;
import com.thaiopensource.relaxng.edit.Container;
import com.thaiopensource.relaxng.edit.DataPattern;
import com.thaiopensource.relaxng.edit.DefineComponent;
import com.thaiopensource.relaxng.edit.DivComponent;
import com.thaiopensource.relaxng.edit.ElementAnnotation;
import com.thaiopensource.relaxng.edit.ElementPattern;
import com.thaiopensource.relaxng.edit.EmptyPattern;
import com.thaiopensource.relaxng.edit.ExternalRefPattern;
import com.thaiopensource.relaxng.edit.GrammarPattern;
import com.thaiopensource.relaxng.edit.GroupPattern;
import com.thaiopensource.relaxng.edit.IncludeComponent;
import com.thaiopensource.relaxng.edit.InterleavePattern;
import com.thaiopensource.relaxng.edit.ListPattern;
import com.thaiopensource.relaxng.edit.MixedPattern;
import com.thaiopensource.relaxng.edit.NameClass;
import com.thaiopensource.relaxng.edit.NameClassVisitor;
import com.thaiopensource.relaxng.edit.NameNameClass;
import com.thaiopensource.relaxng.edit.NotAllowedPattern;
import com.thaiopensource.relaxng.edit.NsNameNameClass;
import com.thaiopensource.relaxng.edit.OneOrMorePattern;
import com.thaiopensource.relaxng.edit.OpenNameClass;
import com.thaiopensource.relaxng.edit.OptionalPattern;
import com.thaiopensource.relaxng.edit.Param;
import com.thaiopensource.relaxng.edit.ParentRefPattern;
import com.thaiopensource.relaxng.edit.Pattern;
import com.thaiopensource.relaxng.edit.PatternVisitor;
import com.thaiopensource.relaxng.edit.RefPattern;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.edit.TextAnnotation;
import com.thaiopensource.relaxng.edit.TextPattern;
import com.thaiopensource.relaxng.edit.UnaryPattern;
import com.thaiopensource.relaxng.edit.ValuePattern;
import com.thaiopensource.relaxng.edit.ZeroOrMorePattern;

class RNGGrammar implements PatternVisitor, NameClassVisitor, ComponentVisitor {
	private static final boolean DEBUG = false;
	
	private Vector names = null;

	private Vector includes = null;
	private Vector references = null;
	private Vector elements = null;
	private Hashtable definitions = null;
	
	private RNGInclude include = null;

	private RNGPattern parent = null;
	private RNGAttribute attribute = null;
	private boolean required = true;

	private boolean processingAttribute = false;

	private final String sourceUri;
//	private final String datatypeLibrary;

	public RNGGrammar( SchemaDocument schema, String sourceUri) {
		if (DEBUG) System.out.println( "RNGGrammar( "+schema+", "+sourceUri+")");
		this.sourceUri = sourceUri;
						
		elements = new Vector();
		definitions = new Hashtable();
		references = new Vector();
		includes = new Vector();
		
		schema.getPattern().accept( this);
	}
  
	private RNGReference createReference( String name, boolean external) {
		RNGReference reference = new RNGReference( name, required, external);
		references.addElement( reference);
		
		return reference;
	}

	public Vector getReferences() {
 		Enumeration enumeration = references.elements();
		Vector refs = new Vector();
		
		while ( enumeration.hasMoreElements()) {
			refs.addElement( enumeration.nextElement());
		}

		return refs;
	}

	private void addElement( RNGElement element) {
		elements.addElement( element);
		
//		RNGElement elem =  (RNGElement)elements.get( element.getUniversalName());
//
//		if ( elem != null) {
//			elem.merge( element);
//		} else {
//			elements.put( element.getUniversalName(), element);
//		}
	}
  
	private void addDefinition( RNGDefinition definition) {
		if (DEBUG) System.out.println( "RNGGrammar.addDefinition( "+definition.getName()+")");

		if ( include != null) {
			include.addDefinition( definition);
		} else {
		
			RNGDefinition def = (RNGDefinition)definitions.get( definition.getName());

			if ( def != null) {
				def.combine( definition);
			} else {
				definitions.put( definition.getName(), definition);
			}
		}
	}
	
	public Vector getElements() {
		Enumeration enumeration = elements.elements();
		Vector elems = new Vector();
		
		while ( enumeration.hasMoreElements()) {
			elems.addElement( enumeration.nextElement());
		}

		return elems;
	}

//	public void mergeElements() {
//		Hashtable mergedElements = new Hashtable();
//		
//		for ( int i = 0; i < elements.size(); i++) {
//			RNGElement element = (RNGElement)elements.elementAt(i);
//			RNGElement mergedElement = (RNGElement)mergedElements.get( element.getUniversalName());
//			
//			if ( mergedElement == null) {
//				mergedElements.put( element.getUniversalName(), element);
//			} else {
//				mergedElement.merge( element);
//			}
//		}
//			
//		Enumeration enumeration = mergedElements.elements();
//		elements = new Vector();
//		
//		while ( enumeration.hasMoreElements()) {
//			elements.addElement( enumeration.nextElement());
//		}
//	}

	public Vector getDefinitions() {
		Enumeration enumeration = definitions.elements();
		Vector defs = new Vector();
		
		while ( enumeration.hasMoreElements()) {
			defs.addElement( enumeration.nextElement());
		}

		return defs;
	}

	public RNGDefinition resolve( RNGReference reference) {
		if ( reference.isExternal()) {
			if ( sourceUri.endsWith( reference.getName())) {
				return (RNGDefinition)definitions.get( "start");
			} else {
				return null;
			}
		} else {
			return (RNGDefinition)definitions.get( reference.getName());
		}
	}

	public void resolveInclude( RNGInclude include) {
		if (DEBUG) System.out.println( "resolveInclude( "+include.getHref()+")");
		Vector definitions = include.getDefinitions();

		for ( int i = 0; i < definitions.size(); i++) {
			RNGDefinition definition = (RNGDefinition)definitions.elementAt(i);
			addDefinition( definition);
		}

		Vector refs = include.getReferences();

		for ( int i = 0; i < refs.size(); i++) {
			RNGReference reference = (RNGReference)refs.elementAt(i);

			references.addElement( reference);
		}

		Vector elems = include.getElements();

		for ( int i = 0; i < elems.size(); i++) {
			RNGElement element = (RNGElement)elems.elementAt(i);

			elements.addElement( element);
		}
	}

	public Vector getIncludes() {
		return includes;
	}
	
	public String getURI() {
		return sourceUri;
	}

	public void include( Vector definitions) {
		for ( int i = 0; i < definitions.size(); i++) {
			addDefinition( (RNGDefinition)definitions.elementAt(i));
		}
	}

	public Object visitElement( ElementPattern p) {
		if (DEBUG) System.out.println( "*\tvisitElement( "+p+") ELEMENT= "+print( p.getNameClass())+"]");

		RNGPattern grandParent = parent;
		boolean wasRequired = required;

		NameClass name = p.getNameClass();
		
//		System.out.println( "Element Name = "+name.getClass());

		if ( name instanceof NameNameClass) {
			RNGElement element = new RNGElement( (NameNameClass)name, required);

			parent.addElement( element);

			required = true;
			parent = element;

			Pattern child = p.getChild();
			
			implicitGroup( child);
			
			// Element is filled in, add it to the lis of elements ...
			addElement( element);
		} else if ( name instanceof AnyNameNameClass){
			RNGElement element = new RNGElement( "*", null, null, required);

			parent.addElement( element);

			required = true;
			parent = element;

			Pattern child = p.getChild();
			
			implicitGroup( child);
			
			// Element is filled in, add it to the lis of elements ...
			addElement( element);
		} else {
//			System.out.println( "Absract Element!");
			RNGElement element = new RNGElement( required);

			required = true;
			parent = element;

			Vector parentNames = names;
			names = new Vector();
			
			name.accept(this);

			Pattern child = p.getChild();
			implicitGroup( child);
			
			for ( int i = 0; i < names.size(); i++) {
				RNGElement elem = element.getSubstitute( (NameNameClass)names.elementAt(i));

				grandParent.addElement( elem);
				addElement( elem);
			}

			names = parentNames;
		}
	
		parent = grandParent;
		required = wasRequired;

		end(p);
		return null;
	}

	public Object visitAttribute(AttributePattern p) {
		if (DEBUG) System.out.println( "*\tvisitAttribute( "+p+") ATTRIBUTE= "+print( p.getNameClass())+"]");

		NameClass name = p.getNameClass();
		processingAttribute = true;
		
		if ( name instanceof NameNameClass) {
			RNGAttribute attribute = new RNGAttribute( (NameNameClass)name, null, null, required);

			this.attribute = attribute;

			parent.addAttribute( attribute);
			
			Pattern child = p.getChild();

			if ( !(child instanceof TextPattern) || hasAnnotations(child)) {
				child.accept( this);
			}
		} else if ( name instanceof AnyNameNameClass) {
			RNGAttribute attribute = new RNGAttribute( "*", null, null, null, null, required);

			this.attribute = attribute;

			parent.addAttribute( attribute);

			Pattern child = p.getChild();

			if ( !(child instanceof TextPattern) || hasAnnotations(child)) {
				child.accept( this);
			}
		} else {
			// Abstract Attribute
			RNGAttribute attribute = new RNGAttribute( required);
			this.attribute = attribute;

			Vector parentNames = names;
			names = new Vector();

			name.accept( this);
			
			Pattern child = p.getChild();

			if ( !(child instanceof TextPattern) || hasAnnotations(child)) {
				child.accept( this);
			}

			for ( int i = 0; i < names.size(); i++) {
				RNGAttribute attrib = attribute.getSubstitute( (NameNameClass)names.elementAt(i));

				parent.addAttribute( attrib);
			}

			names = parentNames;
			
//			RNGAttribute element = new RNGElement( required);
//
//			required = true;
//			parent = element;
//			
//			name.accept(this);
//
//			Pattern child = p.getChild();
//			implicitGroup( child);
			
		}

		processingAttribute = false;
		this.attribute = null;
  
  		end(p);
		return null;
	}

//  private boolean tryNameAttribute(NameClass nc, boolean isAttribute) {
//    if (DEBUG) System.out.println( "tryNameAttribute( "+nc+", "+isAttribute+")");
//    if (hasAnnotations(nc))
//      return false;
//    if (!(nc instanceof NameNameClass))
//      return false;
//    NameNameClass nnc = (NameNameClass)nc;
//    String ns = nnc.getNamespaceUri();
//    if (ns == NameClass.INHERIT_NS) {
//      if (isAttribute)
//        return false;
//      xw.attribute("name", nnc.getLocalName());
//      return true;
//    }
//    if (ns.length() == 0) {
//      if (!isAttribute && !"".equals(prefixMap.get("")))
//        return false;
//      xw.attribute("name", nnc.getLocalName());
//      return true;
//    }
//    String prefix = nnc.getPrefix();
//    if (prefix == null) {
//      if (!ns.equals(prefixMap.get("")))
//        return false;
//      xw.attribute("name", nnc.getLocalName());
//    }
//    else {
//      if (!ns.equals(prefixMap.get(prefix)))
//        xw.attribute("xmlns:" + prefix, ns);
//      xw.attribute("name", prefix + ":" + nnc.getLocalName());
//    }
//    return true;
//  }

  public Object visitOneOrMore(OneOrMorePattern p) {
    if (DEBUG) System.out.println( "visitOneOrMore( "+p+")");
    return visitUnary("oneOrMore", p);
  }

  public Object visitZeroOrMore(ZeroOrMorePattern p) {
    if (DEBUG) System.out.println( "visitZeroOrMore( "+p+")");
    return visitUnary("zeroOrMore", p);
  }

  public Object visitOptional(OptionalPattern p) {
    if (DEBUG) System.out.println( "visitOptional( "+p+")");
    return visitUnary("optional", p);
  }

  public Object visitInterleave(InterleavePattern p) {
    if (DEBUG) System.out.println( "visitInterleave( "+p+")");
    return visitComposite("interleave", p);
  }

  public Object visitGroup(GroupPattern p) {
    if (DEBUG) System.out.println( "visitGroup( "+p+")");
    return visitComposite("group", p);
  }

  public Object visitChoice(ChoicePattern p) {
    if (DEBUG) System.out.println( "visitChoice( "+p+")");
    return visitComposite("choice", p);
  }

  public Object visitGrammar(GrammarPattern p) {
    if (DEBUG) System.out.println( "visitGrammar( "+p+")");

    leadingAnnotations( p);
//    xw.startElement("grammar");

    finishContainer( p, p);

    return null;
  }

	public Object visitExternalRef(ExternalRefPattern p) {
		if (DEBUG) System.out.println( "visitExternalRef( "+p+") [EXTERNAL REF="+p.getHref()+"]");

		RNGReference ref = createReference( p.getHref(), true);
		parent.addReference( ref);

		end(p);
		return null;
	}

  public Object visitRef(RefPattern p) {
    if (DEBUG) System.out.println( "visitRef( "+p+") [REF="+p.getName()+"]");
    return visitAbstractRef("ref", p);
  }

  public Object visitParentRef(ParentRefPattern p) {
    if (DEBUG) System.out.println( "visitParentRef( "+p+") [PARENT-REF="+p.getName()+"]");
    return visitAbstractRef("parentRef", p);
  }

	private Object visitAbstractRef(String name, AbstractRefPattern p) {
		if (DEBUG) System.out.println( "*\tvisitAbstractRef( "+name+", "+p+") [ABSTRACT-REF="+p.getName()+"]");

		RNGReference ref = createReference( p.getName(), false);
		parent.addReference( ref);

		end(p);
		return null;
	}

  public Object visitValue(ValuePattern p) {
//  	if (DEBUG) System.out.println( "visitValue( "+p+") [VALUE="+p.getType()+":"+p.getValue()+"]");
    
    if ( attribute != null) {
    	attribute.addValue( p.getValue());
    } else if ( parent != null && parent instanceof RNGElement) {
    	((RNGElement)parent).setType( p.getType());
    }
    
    leadingAnnotations(p);
//    xw.startElement("value");
    if (!p.getType().equals("token")
        || !p.getDatatypeLibrary().equals("")) {
//      xw.attribute("type", p.getType());
//      if (!p.getDatatypeLibrary().equals(datatypeLibrary))
//        xw.attribute("datatypeLibrary", p.getDatatypeLibrary());
      for (Iterator iter = p.getPrefixMap().entrySet().iterator(); iter.hasNext();) {
        Map.Entry entry = (Map.Entry)iter.next();
        String prefix = (String)entry.getKey();
        String ns = (String)entry.getValue();
//        if (ns != NameClass.INHERIT_NS && !ns.equals(prefixMap.get(prefix)))
//          xw.attribute(prefix.length() == 0 ? "ns" : "xmlns:" + prefix,
//                       ns);
      }
    }
    innerAnnotations(p);
//    xw.text(p.getValue());
    end(p);
    return null;
  }

  public Object visitData(DataPattern p) {
    if (DEBUG) System.out.println( "visitData( "+p+") [DATA:"+p.getType()+"]");

    if ( parent != null && parent instanceof RNGElement) {
  		((RNGElement)parent).setType( p.getType());
  	}

    leadingAnnotations(p);
//    xw.startElement("data");
//    xw.attribute("type", p.getType());
//    if (!p.getDatatypeLibrary().equals(datatypeLibrary))
//      xw.attribute("datatypeLibrary", p.getDatatypeLibrary());
    innerAnnotations(p);
    List list = p.getParams();
    for (int i = 0, len = list.size(); i < len; i++) {
      Param param = (Param)list.get(i);
      leadingAnnotations(param);
//      xw.startElement("param");
//      xw.attribute("name", param.getName());
      innerAnnotations(param);
//      xw.text(param.getValue());
      end(param);
    }
    Pattern except = p.getExcept();
    if (except != null) {
//      xw.startElement("except");
      implicitChoice(except);
//      xw.endElement();
    }
    end(p);
    return null;
  }

  public Object visitMixed(MixedPattern p) {
    if (DEBUG) System.out.println( "visitMixed( "+p+")");
    return visitUnary("mixed", p);
  }

  public Object visitList(ListPattern p) {
    if (DEBUG) System.out.println( "visitList( "+p+")");
    return visitUnary("list", p);
  }

  public Object visitText(TextPattern p) {
    if (DEBUG) System.out.println( "visitText( "+p+")");
    return visitNullary("text", p);
  }

  public Object visitEmpty(EmptyPattern p) {
//    if (DEBUG) 
    	System.out.println( "visitEmpty( "+p+")");
    
    if ( parent != null && parent instanceof RNGElement) {
    	System.out.println( ((RNGElement)parent).getName()+ " is EMPTY");
    	((RNGElement)parent).setEmpty( true);
    }

    return visitNullary("empty", p);
  }

  public Object visitNotAllowed(NotAllowedPattern p) {
    if (DEBUG) System.out.println( "visitNotAllowed( "+p+")");
    return visitNullary("notAllowed", p);
  }

  private Object visitNullary(String name, Pattern p) {
    if (DEBUG) System.out.println( "visitNullary( "+name+", "+p+")");
    leadingAnnotations(p);
//    xw.startElement(name);
    innerAnnotations(p);
    end(p);
    return null;
  }

	private Object visitUnary(String name, UnaryPattern p) {
		if (DEBUG) System.out.println( "*\tvisitUnary( "+name+", "+p+")");

		boolean wasRequired = required;
		required = false;

		implicitGroup( p.getChild());

		required = wasRequired;

		end(p);
		return null;
	}

	private Object visitComposite(String name, CompositePattern p) {
		if (DEBUG) System.out.println( "*\tvisitComposite( "+name+", "+p+")");

		boolean wasRequired = required;
		required = false;

		List list = p.getChildren();

		for (int i = 0, len = list.size(); i < len; i++) {
			((Pattern)list.get(i)).accept(this);
		}

		required = wasRequired;

		end(p);
		return null;
	}

	public Object visitChoice( ChoiceNameClass nc) {
		if (DEBUG) System.out.println( "*\tvisitChoice( "+nc+")");

		List list = nc.getChildren();
		for (int i = 0, len = list.size(); i < len; i++) {
			((NameClass)list.get(i)).accept( this);
		}

		end(nc);
		return null;
	}

  public Object visitAnyName(AnyNameNameClass nc) {
    if (DEBUG) System.out.println( "visitAnyName( "+nc+")");
    leadingAnnotations(nc);
//    xw.startElement("anyName");
    innerAnnotations(nc);
    visitExcept(nc);
    end(nc);
    return null;
  }

  public Object visitNsName(NsNameNameClass nc) {
    if (DEBUG) System.out.println( "visitNsName( "+nc+") [NS="+nc.getNs()+"]");
    leadingAnnotations(nc);
//    xw.startElement("nsName");
//    if (nc.getNs() != NameClass.INHERIT_NS
//        && !nc.getNs().equals(prefixMap.get("")))
//      xw.attribute("ns", nc.getNs());
    innerAnnotations(nc);
    visitExcept(nc);
    end(nc);
    return null;
  }

  private void visitExcept(OpenNameClass onc) {
    if (DEBUG) System.out.println( "visitExcept( "+onc+")");
    NameClass except = onc.getExcept();
    if (except == null)
      return;
//    xw.startElement("except");
    implicitChoice(except);
//    xw.endElement();
  }

	public Object visitName(NameNameClass nc) {
		if (DEBUG) System.out.println( "visitName( "+nc+") [NAME="+nc.getLocalName()+"]");
	
		if ( names != null)	 {
			names.addElement( nc);
		}

		end( nc);
		return null;
	}

	public Object visitDefine( DefineComponent c) {
		if (DEBUG) System.out.println( "*\tvisitDefine( "+c+") [DEFINE="+c.getName()+"]");

		RNGPattern oldParent = parent;
		boolean wasRequired = required;

		String name = c.getName();

		RNGDefinition definition = new RNGDefinition( name);

		required = true;
		parent = definition;

		implicitGroup( c.getBody());
		
		// definition is filled in, add to list of definitions.
		addDefinition( definition);

		parent = oldParent;
		required = wasRequired;

		end(c);
		return null;
	}

  public Object visitDiv(DivComponent c) {
    if (DEBUG) System.out.println( "visitDiv( "+c+")");
    leadingAnnotations(c);
//    xw.startElement("div");
    finishContainer(c, c);
    return null;
  }

	public Object visitInclude(IncludeComponent c) {
		if (DEBUG) System.out.println( "visitInclude( "+c+") [INCLUDE="+c.getHref()+"]");

		RNGInclude include = new RNGInclude( c.getHref());
		this.include = include;
		includes.addElement( include);

		finishContainer(c, c);

		this.include = null;
		return null;
	}

  private void finishContainer(Annotated subject, Container container) {
    if (DEBUG) System.out.println( "finishContainer( "+subject+", "+container+")");
    innerAnnotations(subject);
    List list = container.getComponents();
    for (int i = 0, len = list.size(); i < len; i++)
      ((Component)list.get(i)).accept(this);
    end(subject);
  }

  private void leadingAnnotations( Annotated subject) {
//    if (DEBUG) System.out.println( "leadingAnnotations( "+subject+")");
    annotationChildren( subject.getLeadingComments(), true);
  }

  private void innerAnnotations( Annotated subject) {
//    if (DEBUG) System.out.println( "innerAnnotations( "+subject+")");
    annotationAttributes( subject.getAttributeAnnotations());
    annotationChildren( subject.getChildElementAnnotations(), true);
  }

  private void outerAnnotations(Annotated subject) {
//    if (DEBUG) System.out.println( "outerAnnotations( "+subject+")");
    annotationChildren( subject.getFollowingElementAnnotations(), true);
  }

  private void annotationAttributes(List list) {
//    if (DEBUG) System.out.println( "annotationAttributes( "+list+")");
    for (int i = 0, len = list.size(); i < len; i++) {
      AttributeAnnotation att = (AttributeAnnotation)list.get(i);
      if (DEBUG) System.out.println( "\tAttributeAnnotation = "+att.getLocalName());
      String name = att.getLocalName();
      String prefix = att.getPrefix();
//      xw.attribute(prefix == null ? name : prefix + ":" + name,
//                   att.getValue());
    }
  }

  private void annotationChildren(List list, boolean haveDefaultNamespace) {
//    if (DEBUG) System.out.println( "annotationChildren( "+list+", "+haveDefaultNamespace+")");
    for (int i = 0, len = list.size(); i < len; i++) {
      AnnotationChild child = (AnnotationChild)list.get(i);

      if (child instanceof ElementAnnotation) {
        ElementAnnotation elem = (ElementAnnotation)child;
		if (DEBUG) System.out.println( "\tElementAnnotation = "+elem.getLocalName());
        String name = elem.getLocalName();
        String prefix = elem.getPrefix();
        if (prefix == null) {
//          xw.startElement(name);
          if (haveDefaultNamespace) {
//            xw.attribute("xmlns", "");
            haveDefaultNamespace = false;
          }
        }
//        else
//          xw.startElement(prefix + ":" + name);
        annotationAttributes(elem.getAttributes());
        annotationChildren(elem.getChildren(), haveDefaultNamespace);
//        xw.endElement();
      }
      else if (child instanceof TextAnnotation) {
//	      if (DEBUG) System.out.println( "\tTextAnnotation= "+((TextAnnotation)child).getValue());
	  
//        xw.text(((TextAnnotation)child).getValue());
      } else if (child instanceof Comment) {
//        if (DEBUG) System.out.println( "\tComment= "+(((Comment)child).getValue()));

//        xw.comment(fixupComment(((Comment)child).getValue()));
      }
    }
  }

  static private String fixupComment(String comment) {
    if (DEBUG) System.out.println( "fixupComment( "+comment+")");
    int i = 0;
    for (;;) {
      int j = comment.indexOf('-', i);
      if (j < 0)
        break;
      if (j == comment.length() - 1)
        return comment + " ";
      if (comment.charAt(j + 1) == '-')
        return comment.substring(0, j) + "- " + fixupComment(comment.substring(j + 1));
      i = j + 1;
    }
    return comment;
  }

  private void end(Annotated subject) {
    if (DEBUG) System.out.println( "end( "+subject+")");
//    xw.endElement();
    outerAnnotations(subject);
  }

  private void implicitGroup(Pattern p) {
    if (DEBUG) System.out.println( "implicitGroup( "+p+")");
    if (!hasAnnotations(p) && p instanceof GroupPattern) {
      List list = ((GroupPattern)p).getChildren();
      for (int i = 0, len = list.size(); i < len; i++)
        ((Pattern)list.get(i)).accept(this);
    }
    else
      p.accept(this);
  }

  private void implicitChoice(Pattern p) {
    if (DEBUG) System.out.println( "implicitChoice( "+p+")");
    if (!hasAnnotations(p) && p instanceof ChoicePattern) {
      List list = ((ChoicePattern)p).getChildren();
      for (int i = 0, len = list.size(); i < len; i++)
        ((Pattern)list.get(i)).accept(this);
    }
    else
      p.accept(this);
  }

  private void implicitChoice(NameClass nc) {
    if (DEBUG) System.out.println( "implicitChoice( "+nc+")");
    if (!hasAnnotations(nc) && nc instanceof ChoiceNameClass) {
      List list = ((ChoiceNameClass)nc).getChildren();
      for (int i = 0, len = list.size(); i < len; i++)
        ((NameClass)list.get(i)).accept(this);
    }
    else
      nc.accept(this);
  }
  
  private String print( NameClass name) {
	if (!(name instanceof NameNameClass))
		return null;
	
	NameNameClass nnc = (NameNameClass)name;
	String prefix = nnc.getPrefix();
	
	if ( prefix != null) {
		return prefix+":"+nnc.getLocalName();
	}
	
	return nnc.getLocalName();
  }

  private static boolean hasAnnotations(Annotated subject) {
//    if (DEBUG) System.out.println( "hasAnnotations( "+subject+")");
    return (!subject.getLeadingComments().isEmpty()
            || !subject.getAttributeAnnotations().isEmpty()
            || !subject.getChildElementAnnotations().isEmpty()
            || !subject.getFollowingElementAnnotations().isEmpty());
  }
}
