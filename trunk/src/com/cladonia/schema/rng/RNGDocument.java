package com.cladonia.schema.rng;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.dom4j.Namespace;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cladonia.schema.AttributeInformation;
import com.cladonia.schema.ElementInformation;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.edit.SchemaDocument;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat;
import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;

public class RNGDocument implements com.cladonia.schema.SchemaDocument {
	private Vector elements = null;
	private Vector anyElements = null;
	private Vector grammars = null;
	private URL url = null;
	private boolean compact = false;
	
	public RNGDocument( URL location, boolean compact) throws IOException, SAXException {
		this.url = location;
		this.compact = compact;
		
		grammars = new Vector();
		anyElements = new Vector();
		elements = new Vector();
	
		DefaultRelaxErrorHandler handler = new DefaultRelaxErrorHandler();

		InputFormat inputFormat = null;

		if ( compact) {
			inputFormat = new CompactParseInputFormat();
		} else {
			inputFormat = new SAXParseInputFormat();
		}
		
		try {
			SchemaCollection schemaCollection = inputFormat.load( location.toString(), new String[0], ".rng", handler);

			Iterator iter = schemaCollection.getSchemaDocumentMap().entrySet().iterator();
			while ( iter.hasNext()) {
				Map.Entry entry = (Map.Entry)iter.next();

				RNGGrammar grammar = new RNGGrammar( (SchemaDocument)entry.getValue(), (String)entry.getKey());
				grammars.addElement( grammar);
			}

		} catch ( InputFailedException e) {
			System.err.println( "ERROR: Could not read Input!");
		} catch (InvalidParamsException e) {
		}
		
		// Step 1: resolve all includes...
		resolveIncludes();

		// Step 2: resolve all references ...
		resolveReferences();

		// Step 3: get all declared elements ...
		resolveElements();
	}
	
	public URL getURL() {
		return url;
	}
	
	public int getType() {
		if ( compact) {
			return TYPE_RNC;
		} else {
			return TYPE_RNG;
		}
	}

	public Vector getGlobalElements() {
		return getElements();
	}

	public Vector getAnyElements() {
		if ( anyElements.size() > 1) {
			RNGElement anyElement = (RNGElement)anyElements.elementAt( 0);
			
			for ( int i = 1; i < anyElements.size(); i++) {
				anyElement.merge( (RNGElement)anyElements.elementAt( i));
			}
			
			anyElements = new Vector();
			anyElements.add( anyElement);
		}

		return anyElements;
	}

	public Vector getElements() {
//		Enumeration  = elements.elements();
//		Vector elems = new Vector();
//		
//		while ( .hasMoreElements()) {
//			elems.addElement( .nextElement());
//		}

		return new Vector( elements);
	}

	public void updatePrefixes( Vector declarations) {
		Vector allElements = getElements();
		
		for ( int i = 0; i < allElements.size(); i++) {
			ElementInformation model = (ElementInformation)allElements.elementAt(i);
			Vector attributes = model.getAttributes();
//          Children should be part of the normal elements
//			Vector children = model.getChildElements();
			
//			if ( children != null) {
//				for ( int j = 0; j < children.size(); j++) {
//					ElementInformation child = (ElementInformation)children.elementAt(j);
//					
//					for ( int k = 0; k < declarations.size(); k++) {
//						Namespace ns = (Namespace)declarations.elementAt(k);
//
//						if ( ns.getURI().equals( child.getNamespace())) {// && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
//							child.setPrefix( ns.getPrefix());
//							break;
//						}
//					}
//				}
//			}
			
			if ( attributes != null) {
				for ( int j = 0; j < attributes.size(); j++) {
					AttributeInformation attribute = (AttributeInformation)attributes.elementAt(j);
					
					for ( int k = 0; k < declarations.size(); k++) {
						Namespace ns = (Namespace)declarations.elementAt(k);

						if ( ns.getURI().equals( attribute.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
							attribute.setPrefix( ns.getPrefix());
							break;
						}
					}
				}
			}
			
			for ( int j = 0; j < declarations.size(); j++) {
				Namespace ns = (Namespace)declarations.elementAt(j);

				if ( ns.getURI().equals( model.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
					model.setPrefix( ns.getPrefix());
					break;
				}
			}
		}
		
		Vector anyElements = getAnyElements();

		for ( int i = 0; i < anyElements.size(); i++) {
			ElementInformation model = (ElementInformation)anyElements.elementAt(i);
			Vector attributes = model.getAttributes();
			Vector children = model.getChildElements();
			
			if ( children != null) {
				for ( int j = 0; j < children.size(); j++) {
					ElementInformation child = (ElementInformation)children.elementAt(j);
					
					for ( int k = 0; k < declarations.size(); k++) {
						Namespace ns = (Namespace)declarations.elementAt(k);

						if ( ns.getURI().equals( child.getNamespace())) {// && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
							child.setPrefix( ns.getPrefix());
							break;
						}
					}
				}
			}
			
			if ( attributes != null) {
				for ( int j = 0; j < attributes.size(); j++) {
					AttributeInformation attribute = (AttributeInformation)attributes.elementAt(j);
					
					for ( int k = 0; k < declarations.size(); k++) {
						Namespace ns = (Namespace)declarations.elementAt(k);

						if ( ns.getURI().equals( attribute.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
							attribute.setPrefix( ns.getPrefix());
							break;
						}
					}
				}
			}
			
			for ( int j = 0; j < declarations.size(); j++) {
				Namespace ns = (Namespace)declarations.elementAt(j);

				if ( ns.getURI().equals( model.getNamespace()) ) { // && ns.getPrefix() != null && ns.getPrefix().trim().length() > 0) {
					model.setPrefix( ns.getPrefix());
					break;
				}
			}
		}
	}

	private void resolveElements() {
		for ( int i = 0; i < grammars.size(); i++) {
			RNGGrammar grammar = (RNGGrammar)grammars.elementAt(i);
//			grammar.mergeElements();
			
			resolveElements( grammar.getElements());
		}
	}

	private void resolveElements( Vector elements) {
		for ( int i = 0; i < elements.size(); i++) {
			RNGElement element = (RNGElement)elements.elementAt(i);
			if ( !element.getName().equals( "*")) {
				addElement( element);
			} else { 
				addAnyElement( element);
			}
		}
	}

	private void resolveIncludes() {
		// pass 1 will resolve all first level includes ...
		for ( int i = 0; i < grammars.size(); i++) {
			RNGGrammar grammar = (RNGGrammar)grammars.elementAt(i);
			Vector includes = grammar.getIncludes();
			resolveIncludes( includes);

			for ( int j = 0; j < includes.size(); j++) {
				grammar.resolveInclude( (RNGInclude)includes.elementAt(j));
			}
		}

		// pass 2 will resolve all included includes ...
		for ( int i = 0; i < grammars.size(); i++) {
			RNGGrammar grammar = (RNGGrammar)grammars.elementAt(i);
			Vector includes = grammar.getIncludes();
			resolveIncludes( includes);

			for ( int j = 0; j < includes.size(); j++) {
				grammar.resolveInclude( (RNGInclude)includes.elementAt(j));
			}
		}
	}
	
	private void resolveIncludes( Vector includes) {
		for ( int i = 0; i < includes.size(); i++) {
			RNGInclude include = (RNGInclude)includes.elementAt(i);
			resolveInclude( include);
		}
	}
	
	private void resolveInclude( RNGInclude include) {
		for ( int i = 0; i < grammars.size(); i++) {
			RNGGrammar grammar = (RNGGrammar)grammars.elementAt(i);

			if ( include.include( grammar)) {
//				System.err.println( "Found Grammar for Include \""+grammar.getURI()+"\"");
				return;
			}
		}
		
//		System.err.println( "ERROR: Could not find grammar for Include \""+include.getHref()+"\"");
	}

	private void resolveReferences() {
		for ( int i = 0; i < grammars.size(); i++) {
			RNGGrammar grammar = (RNGGrammar)grammars.elementAt(i);
			resolveReferences( grammar, grammar.getReferences());
		}
	}

	private void resolveReferences( RNGGrammar grammar, Vector references) {
		for ( int i = 0; i < references.size(); i++) {
			RNGReference reference = (RNGReference)references.elementAt(i);
			
			if ( !reference.isResolved()) {
				if ( reference.isExternal()) {
					resolveExternalReference( reference);
				} else {
					resolveReference( grammar, reference);
				}
			}
		}
	}

	private void resolveReference( RNGGrammar grammar, RNGReference reference) {
		RNGDefinition definition = grammar.resolve( reference);
		
//		if ( definition == null) {
//			System.err.println( grammar.getURI()+"\nERROR: Could not find definition for reference \""+reference.getName()+"\"");
//		}

		reference.setDefinition( definition);
	}

	private void resolveExternalReference( RNGReference reference) {
		for ( int i = 0; i < grammars.size(); i++) {
			RNGGrammar grammar = (RNGGrammar)grammars.elementAt(i);
			String uri = grammar.getURI();
			
			if ( uri.endsWith( reference.getName())) {
				RNGDefinition definition = grammar.resolve( reference);

//				if ( definition == null) {
//					System.err.println( grammar.getURI()+"ERROR: Could not find definition for external reference \""+reference.getName()+"\"");
//				}

				reference.setDefinition( definition);
			}
		}
	}

	private void addElement( RNGElement element) {
//		RNGElement elem =  (RNGElement)elements.get( element.getUniversalName());

		if ( !elements.contains( element)) {
			elements.add( element);
		}
	}

	private void addAnyElement( RNGElement element) {
		anyElements.addElement( element);
	}

	private static class DefaultRelaxErrorHandler implements ErrorHandler {
		public void warning( SAXParseException exception) throws SAXParseException {
			System.err.println( "WARNING: "+exception.getMessage());
			// throw exception;
		} 

		public void error( SAXParseException exception) throws SAXParseException {
			//	        System.out.println( "ERROR: "+exception.getMessage());
			throw exception;
		} 

		public void fatalError( SAXParseException exception) throws SAXParseException {
			throw exception;
		} 
	}
}
