/*
 * Created on 03-Feb-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

import javax.swing.AbstractAction;

import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XMLDocumentChooserDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.schema.generate.SchemaInstanceGenerationDialog;

import com.cladonia.xml.XElement;
import com.cladonia.xml.XMLError;
import com.cladonia.xml.editor.Editor;
import java.io.UnsupportedEncodingException;

import org.mozilla.javascript.*;


/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ExecuteScriptAction  extends AbstractAction  implements ErrorReporter {

	private static final boolean DEBUG = false;
	
	private ConfigurationProperties properties = null;
	private SchemaInstanceGenerationDialog dialog = null;
	//private WSDLDialog dialog = null;
	private XMLDocumentChooserDialog chooser = null;
	private ExchangerEditor parent = null;
	private ExchangerDocument document = null;
    private Editor editor = null;

    private ExchangerDocument doc = null;
    private int currentLineNumber = 0;
    private int newLineCounter = 0;
    private XElement currentNode = null;
    private int numberOfErrors = 0;
    private int numberOfWarnings = 0;


  public static void main(String[] args)
  {
  }
  

	/**
	 * The constructor for the action which allows for ....
	 *
	 * @param parent the parent frame.
	 */
	public ExecuteScriptAction( ExchangerEditor parent, ConfigurationProperties properties) {
		super( "Execute Script");

		putValue( MNEMONIC_KEY, new Integer( 'X'));
		putValue( SHORT_DESCRIPTION, "Execute Script.");
		
		this.parent = parent;
		this.properties = properties;
	}
	
	public void updatePreferences() {
		if ( dialog != null) {
			dialog.updatePreferences();
		}
	}

	/**
	 * The implementation of the .... action.
	 *
	 * @param the action event.
	 */
	public void actionPerformed( ActionEvent e) {
		// System.out.println( "ExecuteScriptAction.actionPerformed( "+e+")");
		if ( chooser == null) {
			chooser = new XMLDocumentChooserDialog( parent, "Open Script", "Specify Script location", parent, false);
			//chooser = new XMLDocumentChooserDialog( parent, "Open Script", "Specify Script location");
		}
		
		ExchangerView view = parent.getView();
		URL url = null;
		
		if ( view != null) {
			view.updateModel();
		}

		ExchangerDocument document = parent.getDocument();
		
		//if (document != null)
		//  System.out.println("*doc=" + document.getText());
		//else
		//  System.out.println("!!!!null doc" );
		
		if ( document != null) {
			chooser.show( true);
		} else{
			chooser.show( false);
		}
		
		if ( !chooser.isCancelled()) {
			try {
				if ( chooser.isOpenDocument()) {				  
				  document = chooser.getOpenDocument();				  
				}  
				else if ( !chooser.isCurrentDocument()) {
					url = new URL( chooser.getInputLocation());
					try {

					document = new ExchangerDocument( url);
					document.load();
					} catch (SAXParseException spe) {
					// This is returned from the document, do not report???
					// spe.printStackTrace();
					} catch (IOException ie) {
					if ( ie instanceof UnsupportedEncodingException) {
						MessageHandler.showError( "Could not open " + url.getFile()+"\nUnsupported Encoding: "+ie.getMessage(), "Document Creation Error");
					} else {
						MessageHandler.showError( "Could not open " + url.getFile(), ie, "Document Creation Error");
					}

					boolean unrecoverableError = true;
					} catch (Exception ex) {
//					System.out.println( "*** ERRROR FOUND!")
					MessageHandler.showError( "Could not open Document.", ex, "Document Creation Error");
					ex.printStackTrace();
					} 					
					
				}

	 			 		
			 		 
                   try {
                     
                     
                     //ExchangerDocument newDocument = new ExchangerDocument( sig.processXSD(url, rootElement, optionalAttributes,  optionalElements, generateData));
                     //parent.open( newDocument, null);

               
                     	Context cx = Context.enter();
	                      Scriptable scope = cx.initStandardObjects(null);
	
	                     // Add a global variable "out" that is a JavaScript reflection
	                     // of System.out
	                     //Object jsOut = Context.javaToJS(System.out, scope);
	                     //ScriptableObject.putProperty(scope, "out", System.out);
	                     
	                     Object jsSig = Context.javaToJS(parent.getExchangerContext(), scope);
	                     ScriptableObject.putProperty(scope, "exchanger", parent.getExchangerContext());
	
	                     //String s = "function copyValue(id1,id2) {var value1 = document.getElementById(id1.value;var obj2 = document.getElementById(id2);obj2.value = value1;} //end copyValue";
	                     //String s = "cx1 = Packages.org.mozilla.javascript.Context.enter(); out.println('3+2=' + cx1.evaluateString(this, '3+2', null, 0, null));";
	                     //String s = "sig = new Packages.com.cladonia.schema.generate.SchemaInstanceGenerator();if (sig) out.println('sig not null');var instance = sig.processXSD('test.xsd', 'dddd'); exchanger.openNewDocument('<test>fbf</test>');";
	                     //String s = "if (sig1) out.println('sig1 not null');sig1.processXSD('test.xsd', 'dddd');";
	                     
	                     //String s = "var url = new java.net.URL('file:test.xsd'); var instance = exchanger.generateInstanceFromSchemaURL(url, 'document1', false, false, false); exchanger.openNewDocument(instance);";
	                     //String s = function;
	                     cx.setGeneratingDebug(true);
	                     cx.setErrorReporter(this);
	
	                     Object result = cx.evaluateString(scope, document.getText(), document.getName(), 1, null);


                     
                     
                     
                 } catch ( Exception ex) {
                   System.out.println("exception thrown:" + ex.toString() + "..."+ ex.getMessage());
                     // This should never happen, just report and continue
                     MessageHandler.showError( parent, "Cannot Execute Script", "Execute Script Error");
                     System.err.println("exception thrown:"  + ex.toString() + "..." + ex.getMessage());
                 } finally {
                     parent.setStatus( "Done");
                     parent.setWait( false);
//	                     Context.exit();
                 }

	
				
				
				
			} catch ( IOException x) {
				MessageHandler.showError( "Could not open the Document:\n"+chooser.getInputLocation(), "Document Error");
			} 
			//catch ( SAXParseException x) {
			//	MessageHandler.showError( "Could not parse the Document.", x, "Document Error");
			//}
		}
	}  




	
	

  //warning(java.lang.String message, java.lang.String sourceName, int line, java.lang.String lineSource, int lineOffset)


  //*********Implementation of ErrorReporter*****************************


  /* (non-Javadoc)
   * @see org.mozilla.javascript.ErrorReporter#warning(java.lang.String, java.lang.String, int, java.lang.String, int)
   */
  public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
      /*String error = "WARNING - ";
      error += arg0;
      error += " - At: "+arg1+":"+arg2+":"+arg4;
      error += "\n\t"+arg3;*/
      numberOfWarnings++;
      javax.swing.text.Element root = editor.getEditor().getDocument().getDefaultRootElement();
      int l = root.getElementIndex( currentNode.getContentStartPosition());

      SAXParseException jsError = new SAXParseException(message,doc.getPublicID() , doc.getSystemID(), line+l-(newLineCounter), lineOffset);
      //SAXParseException jsError = new SAXParseException(message,doc.getPublicID(), doc.getSystemID(), line, lineOffset);
      XMLError e = new XMLError(jsError,XMLError.WARNING);



      parent.getOutputPanel().addError("JS",e);
      //System.out.println(error);

  }



  /* (non-Javadoc)
   * @see org.mozilla.javascript.ErrorReporter#error(java.lang.String, java.lang.String, int, java.lang.String, int)
   *
   * //error(java.lang.String message, java.lang.String sourceName, int line, java.lang.String lineSource, int lineOffset)
   *
   */
  public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {

      /*String error = "ERROR - ";
      error += arg0;
      error += " - At: "+arg1+":"+arg2+":"+arg4;
      error += "\n\t"+arg3;*/
      //SAXParseException(String message, String publicId, String systemId, int lineNumber, int columnNumber, Exception e)
      numberOfErrors++;
      javax.swing.text.Element root = editor.getEditor().getDocument().getDefaultRootElement();
      int l = root.getElementIndex( currentNode.getContentStartPosition());

      SAXParseException jsError = new SAXParseException(message,doc.getPublicID() , doc.getSystemID(), line+l-(newLineCounter), lineOffset);
      //SAXParseException jsError = new SAXParseException(message,doc.getPublicID(), doc.getSystemID(), line, lineOffset);
      XMLError e = new XMLError(jsError,XMLError.ERROR);



      parent.getOutputPanel().addError("JS",e);

      //System.out.println(jsError);

  }



  /* (non-Javadoc)
   * @see org.mozilla.javascript.ErrorReporter#runtimeError(java.lang.String, java.lang.String, int, java.lang.String, int)
   *
   * //runtimeError(java.lang.String message, java.lang.String sourceName, int line, java.lang.String lineSource, int lineOffset)
   *
   */
  public EvaluatorException runtimeError(String arg0, String arg1, int arg2, String arg3, int arg4) {

      /*String error = "\n---------------------------------------------------";
      error += "\nSUMMARY - ";
      error += arg0;
      error += " - At: "+arg1+":"+arg2+":"+arg4;*/
      //error += "\n\t"+arg3;
      parent.getOutputPanel().endCheck("JS", arg0);
      //System.out.println(error);
      return null;
  }

  /**
   * Sets the current view.
   *
   * @param view the current view.
   */
  public void setView( Object view) {
      if ( view instanceof Editor) {
          editor = (Editor)view;
      } else {
          editor = null;
      }

      setDocument( parent.getDocument());
  }

  public void setDocument( ExchangerDocument doc) {
      if ( doc != null && doc.isXML()) {
          setEnabled( editor != null);
      } else {
          setEnabled( false);
      }
  }


  
}
