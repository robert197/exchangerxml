/*
 * Created on 19-Jan-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.xngreditor.api;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.UserView;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.schema.generate.SchemaInstanceGenerator;
import java.net.URL;
import com.cladonia.xngreditor.grammar.FragmentProperties;

import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.scenario.ParameterProperties;
import com.cladonia.xml.transform.ScenarioProcessor;

import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;
import org.dom4j.Node;
import org.w3c.dom.Document;
import org.dom4j.tree.DefaultDocument;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.cladonia.xml.XElement;
import com.cladonia.xml.XAttribute;


/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Exchanger {

	public static final int XSLT_PROCESSOR_DEFAULT		= 0;
	public static final int XSLT_PROCESSOR_XALAN			= 1;
	public static final int XSLT_PROCESSOR_SAXON_XSLT1	= 2;
	public static final int XSLT_PROCESSOR_SAXON_XSLT2	= 3;

  
  
  private ExchangerEditor parent = null;
	private ExchangerDocument document = null;

	private Vector grammarPropertiesList = null;
	
  public Exchanger(ExchangerEditor parent,  ConfigurationProperties properties )
  {
    
    this.parent = parent;
	this.document = parent.getDocument();

  }
  
  public static void main(String[] args)
  {
  }
	public void openNewDocument( final String text) 
	{

      
	    SwingUtilities.invokeLater(new Runnable() {
	        
	        public void run() {
      parent.setWait( true);
      parent.setStatus( "Opening New Document ...");
            
	  final ExchangerDocument newDocument = new ExchangerDocument( text);
	  parent.open( newDocument, null);

      parent.setStatus( "Done");
      parent.setWait( false);
	        }
	    });
      
      
      
      
	}

	public String generateInstanceFromSchemaURL( URL file, String root, boolean optionalAttributes,  boolean optionalElements, boolean generateData) 
	{
	  
	  SchemaInstanceGenerator sig = new SchemaInstanceGenerator();
	  
	  String instance = sig.generateInstanceFromSchemaURL(file,  root,  optionalAttributes,   optionalElements,  generateData);
	  
	  
	  return instance;
	  
	}
	public void insertFragment(String typeName, String fragmentName)	
	{
	   grammarPropertiesList =  parent.getGrammarProperties();
	   GrammarProperties grammarProperties = null;
		  
			for ( int i = 0; i < grammarPropertiesList.size(); i++) {
				GrammarProperties element = (GrammarProperties)grammarPropertiesList.elementAt(i);

				if (element.getDescription().equals(typeName))
				{
				  grammarProperties = element;
				  break;
				}
			}
	
			if (grammarProperties == null)
			  return;

			
			Vector fragments = grammarProperties.getFragments();
			for ( int i = 0; i < fragments.size(); i++) {
				FragmentProperties fragment = (FragmentProperties)fragments.elementAt(i);

				if (fragment.getName().equals(fragmentName))
				{
				  parent.getView().getEditor().insertFragment( fragment.isBlock(), fragment.getContent());
				  parent.getView().getEditor();
				  break;
				}
			}
	}	
	
	public String getGrammarFragments()
	{
	  	XElement types = new XElement( "types");
		
	   grammarPropertiesList =  parent.getGrammarProperties();
	  
		for ( int i = 0; i < grammarPropertiesList.size(); i++) {
			GrammarProperties element = (GrammarProperties)grammarPropertiesList.elementAt(i);

			// Find out where to insert the element...
			int index = -1;

			
			types.add(exportType(element));
		}
		
		
		try
		{
		  ExchangerDocument doc = new ExchangerDocument(types);

		  return doc.getDocument().asXML();
		}
		catch (Exception ex) {}
		
		return null;
		
	}
	private static void addAttribute( XElement element, String name, String value) {
		if ( value != null) {
			element.putAttribute( new XAttribute( name, value));
		}
	}

	private static XElement addElement( XElement element, String name, String value) {
		XElement e = new XElement( name);

		if ( value != null) {
			e.setText( value);
		}

		element.add( e);
		
		return e;
	}

	public XElement exportType(GrammarProperties element ) { // throws IOException, SAXParseException {
		//String namespace = "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/";
		XElement type = new XElement( "type");
		addAttribute( type, "name", element.getDescription());
		addAttribute( type, "icon", element.getIconLocation());
		addAttribute( type, "publicID", element.getPublicID());
		addAttribute( type, "systemID", element.getSystemID());
		addAttribute( type, "extensions", element.getExtensions());


		Vector fragments = element.getFragments();
		for ( int i = 0; i < fragments.size(); i++) {
			FragmentProperties fragment = (FragmentProperties)fragments.elementAt(i);

			XElement fragmentElem = addElement( type, "fragment", null);

			addAttribute( fragmentElem, "block", ""+fragment.isBlock());
			addAttribute( fragmentElem, "name", fragment.getName());
			addAttribute( fragmentElem, "key", fragment.getKey());
			addAttribute( fragmentElem, "order", ""+fragment.getOrder());
			addAttribute( fragmentElem, "icon", fragment.getIcon());

			fragmentElem.setValue( fragment.getContent());
		}


		return type;
	}

	
	
	public JTabbedPane getControllerTabbedPane()
	{
	  
	  return parent.getControllerTabbedPane();
	}
	
	public ExchangerView getExchangerView() {
	    return (parent.getView());
	}
	
	public void makeUserView(JPanel newPanel, String identifier) {
	    
	    //ViewPanel newViewPanel = (ViewPanel) newPanel;
	    
	    final UserView newUserView = new UserView(newPanel, identifier, this.parent);
	    
	    SwingUtilities.invokeLater(new Runnable() {
	        
	        public void run() {
	            getExchangerView().addUserView(newUserView);
	            getExchangerView().revalidate();
	            parent.getRootPane().revalidate();
	        }
	    });
	    
	}
	
	public void removeUserView(final String identifier) {
	    
	    //ViewPanel newViewPanel = (ViewPanel) newPanel;
	    
	    //final UserView newUserView = new UserView(newPanel, identifier, this.parent);
	    
	    SwingUtilities.invokeLater(new Runnable() {
	        
	        public void run() {
	            getExchangerView().removeUserView(identifier);
	            getExchangerView().revalidate();
	            parent.getRootPane().revalidate();
	            parent.switchToEditor();
	        }
	    });
	    
	}

	public String getCurrentDocumentText()
	{
	  String text = parent.getDocument().getText();
	  
	  return text;
	}

	public void insert(String text)
	{
 	  parent.getView().getEditor().insert(text);
	}
	public void gotoLine(int lineNum)
	{
 	  parent.getView().getEditor().gotoLine(lineNum);
	}
	public int getCursorPosition()
	{
	  return parent.getView().getEditor().getCursorPosition();
	}
	public void setCursorPosition(int pos)
	{
	  parent.getView().getEditor().setCursorPosition(pos);
	}

	public void moveCursorPosition(int pos)
	{
	  int cur = parent.getView().getEditor().getCursorPosition();
	  int newPos  =cur + pos; 
	  
	  if (newPos < 1)
	    newPos = 1;
	    
	  parent.getView().getEditor().setCursorPosition(newPos);
	}

	public void selectElement()
	{
	  parent.getView().getEditor().selectElement();
	}

	
	
	public void selectNode(Node node)
	{
	
	if ( node instanceof XElement) {
	  parent.getView().getEditor().selectElement( (XElement)node);
	}
	else
	{
	  
	  System.out.println("selectNode(): Invalid node");
	}
	/*
	else if ( node instanceof XAttribute) {
	  parent.getView().getEditor().selectAttribute( (XAttribute)node, -1);
	} else if ( node instanceof Node) {
	  parent.getView().getEditor().selectElement( (XElement)((Node)node).getParent());
	}
*/
	}
	
	public void selectElementContent()
	{
	  parent.getView().getEditor().selectElementContent();
	}
	
	public void updateCurrentDocument(String text)
	{
      parent.setWait( true);
      parent.setStatus( "Updating Current Document ...");
	  
	  parent.getView().getEditor().setText(text);
      parent.switchToEditor();      
      parent.getView().updateModel();

      parent.setStatus( "Done");
      parent.setWait( false);

	}

	public DefaultDocument getCurrentDocumentDOM4J()
	{
	  ExchangerDocument doc=parent.getDocument();
	  
	  DefaultDocument dd = null;
	  
	  if (doc != null)
	  {
	     dd = (DefaultDocument)doc.getDocument();
	     
	     if (dd == null)
	       System.out.println("doc is null");
	  }
	  
	  return dd;
	}


	
	public DefaultDocument getCopyOfCurrentDocumentDOM4J()
	{
	  ExchangerDocument doc=parent.getDocument();
	  
	  ExchangerDocument tempDoc = new ExchangerDocument(doc.getText());
	  DefaultDocument dd = null;
	  
	  if (tempDoc != null)
	  {
	     dd = (DefaultDocument)tempDoc.getDocument();
	     
	     if (dd == null)
	       System.out.println("tempDoc is null");
	  }
	  
	  return dd;
	}

	
	public Document getCurrentDocumentW3C()
	{
	  ExchangerDocument exDoc = parent.getDocument();
	  Document doc = null;
	  
	  if (exDoc != null)
	  {
	    try
	    {
	     doc = exDoc.getW3CDocument();
	    }
	    catch (Exception ex) {}
	    
	     if (doc == null)
	       System.out.println("W3CDocument is null");
	  }
	  
	  return doc;
	}
	
	
	public String getSelection()
	{
	  String text = parent.getView().getEditor().getSelectedText();
	  
	  return text;
	}

	public  void replaceSelection(String text)
	{
	  parent.getView().getEditor().replaceSelection(text);
	  

	}

	
	public String executeXSLT(String inputFile, String xslFile, String outputFile, int processor, Properties params)
	{
	  String text = null;
	  
	  if (inputFile == null && xslFile == null)
	    return text;

	  ScenarioProperties scenario = new ScenarioProperties();

	  
	  if (inputFile == null)
	    scenario.setInputType(ScenarioProperties.INPUT_CURRENT_DOCUMENT);
	  else
	  {
	    scenario.setInputType(ScenarioProperties.INPUT_FROM_URL);
	    scenario.setInputFile(inputFile);
	  }

	  if (xslFile == null)
	    scenario.setXSLType(ScenarioProperties.XSL_CURRENT_DOCUMENT);
	  else
	  {
	    scenario.setXSLType(ScenarioProperties.XSL_FROM_URL);
	    scenario.setXSLURL(xslFile);
	  }


	  if (outputFile == null)
	  {
	    scenario.setOutputType(ScenarioProperties.OUTPUT_DO_NOTHING);
	  }
	  else
	  {
	    scenario.setOutputType(ScenarioProperties.OUTPUT_TO_FILE);
	    scenario.setOutputFile(outputFile); 
	  }

	  
	  scenario.setXSLEnabled(true);
	  
	  //public static final int XSLT_PROCESSOR_DEFAULT		= 0;
	  //public static final int XSLT_PROCESSOR_XALAN		= 1;
	  //public static final int XSLT_PROCESSOR_SAXON_XSLT1	= 2;
	  //public static final int XSLT_PROCESSOR_SAXON_XSLT2	= 3;

	  switch (processor)
	  {
	    case Exchanger.XSLT_PROCESSOR_XALAN:
	    	processor = ScenarioProperties.PROCESSOR_XALAN;
	    	break;
	    	
	    case Exchanger.XSLT_PROCESSOR_SAXON_XSLT1:
	    	processor = ScenarioProperties.PROCESSOR_SAXON_XSLT1;
	    	break;

	    case Exchanger.XSLT_PROCESSOR_SAXON_XSLT2:
	    	processor = ScenarioProperties.PROCESSOR_SAXON_XSLT2;
	    	break;
	    	
	    	
	    case Exchanger.XSLT_PROCESSOR_DEFAULT:
	    default:
	    	processor = ScenarioProperties.PROCESSOR_DEFAULT;
	    	break;
	    	
	    
	  }
	  
	  scenario.setProcessor(processor);
	  
	  
	  if (params != null)	    	   
	  {
	    Enumeration e = params.propertyNames();
	    
	    while (e.hasMoreElements())
	    {
	     String name = (String)e.nextElement(); 
	    
	     String value = params.getProperty(name);
	     
	     scenario.addParameter(new ParameterProperties(name, value));
	    }
	      
	  }
	  
	  
	  text = executeScenario(scenario);
	  
	  return text;

	}

	
	private String executeScenario(ScenarioProperties scenario)
	{
	  String text = null;

	  System.out.println("new ScenarioProcessor");
	  
	  ScenarioProcessor processor = new ScenarioProcessor(scenario, document);
	  
	  try
	  {
		System.out.println("processor.init");
	    processor.init();
	  
		System.out.println("processor.openInput");
	    processor.openInput();
	    
		System.out.println("processor.openStylesheet");
	    processor.openStylesheet();
	    
		System.out.println("processor.execute");
	    processor.execute();
	    
		System.out.println("processor.getOutputText");
	    text = processor.getOutputText();
	    
	    if (scenario.getOutputType() == ScenarioProperties.OUTPUT_TO_FILE)
	    {
	    	processor.save();
	    }
	    
	  }
	  catch (Exception ex){}
	  
	  
	  return text;
	
	}
	
	public void showAlert(String text)
	{
		MessageHandler.showMessage(text);
	}

	
}
