/*
 * Created on 10-Dec-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.xngreditor.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.xml.sax.SAXParseException;

import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.XMLSchema;
import com.cladonia.schema.generate.SchemaInstanceGenerationDialog;
import com.cladonia.schema.generate.SchemaInstanceGenerator;
import com.cladonia.schema.viewer.RootSelectionDialog;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.ExchangerView;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.XMLDocumentChooserDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SchemaInstanceGenerationAction extends AbstractAction
{
  
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
  private RootSelectionDialog rootSelectionDialog = null;
  private XMLSchema schema = null;
  
  private RootSelectionDialog rootDialog = null;
  
  public static void main(String[] args)
  {
  }
  
  /**
   * The constructor for the action which allows for ....
   *
   * @param parent
   *            the parent frame.
   */
  public SchemaInstanceGenerationAction(ExchangerEditor parent,
      ConfigurationProperties properties)
  {
    super("Schema Instance Generation");
    
    putValue(MNEMONIC_KEY, new Integer('I'));
    putValue(SHORT_DESCRIPTION, "Generate instance from XML Schema.");
    
    this.parent = parent;
    this.properties = properties;
  }
  
  public void updatePreferences()
  {
    if (dialog != null)
    {
      dialog.updatePreferences();
    }
  }
  
  /**
   * The implementation of the .... action.
   *
   * @param the
   *            action event.
   */
  public void actionPerformed(ActionEvent e)
  {
    // System.out.println( "SchemaInstanceGenerationAction.actionPerformed(
    // "+e+")");
    if (chooser == null)
    {
      chooser = new XMLDocumentChooserDialog(parent, "Open XML Schema",
          "Specify XML Schema location", parent, false);
    }
    
    ExchangerView view = parent.getView();
    URL url = null;
    
    if (view != null)
    {
      view.updateModel();
    }
    
    ExchangerDocument document = parent.getDocument();
    
    //if (document != null)
    //  System.out.println("*doc=" + document.getText());
    //else
    //  System.out.println("!!!!null doc" );
    
    if (document != null)
    {
      chooser.show(document.isXSD());
      //chooser.show( true);
    }
    else
    {
      chooser.show(false);
    }
    
    if (!chooser.isCancelled())
    {
      try
      {
        if (chooser.isOpenDocument())
        {
          document = chooser.getOpenDocument();
        }
        else if (!chooser.isCurrentDocument())
        {
          
          
          //System.out.println("**input location" + chooser.getInputLocation());
          
          url = new URL(chooser.getInputLocation());
          try
          {
            
            document = new ExchangerDocument(url);
            document.load();
          }
          catch (SAXParseException spe)
          {
            // This is returned from the document, do not report???
            // spe.printStackTrace();
          }
          catch (IOException ie)
          {
            if (ie instanceof UnsupportedEncodingException)
            {
              MessageHandler.showError("Could not open " + url.getFile()
              + "\nUnsupported Encoding: " + ie.getMessage(),
                  "Document Creation Error");
            }
            else
            {
              MessageHandler.showError("Could not open " + url.getFile(), ie,
                  "Document Creation Error");
            }
            
            boolean unrecoverableError = true;
          }
          catch (Exception ex)
          {
            //					System.out.println( "*** ERRROR FOUND!")
            MessageHandler.showError("Could not open Document.", ex,
                "Document Creation Error");
            ex.printStackTrace();
          }
          
        }
        
        try
        {
          //System.out.println("**create schema");
          schema = new XMLSchema(document);
        }
        catch (Exception ex)
        {
          MessageHandler
              .showError(parent, "Cannot Generate Instance from Schema",
              "Generate Instance Error");
          System.err.println("exception thrown:" + ex.toString() + "..."
              + ex.getMessage());
          return;
        }
        
        SchemaElement schemaRoot = null;
        
        if (schema != null)
        {
          if (document != null)
          {
            Vector elements = schema.getGlobalElements();
            
            if (elements.size() == 1)
            {
              schemaRoot = (SchemaElement) elements.elementAt(0);
            }
          }
          
          if (schemaRoot == null)
          {
            
            if (rootDialog == null)
            {
              rootDialog = new RootSelectionDialog(parent);
              rootDialog.setSchema(schema);
              rootDialog.setLocationRelativeTo(parent);
            }
            
            rootDialog.setSchema(schema);
            rootDialog.setVisible(true);
            
            if (!rootDialog.isCancelled())
            {
              schemaRoot = rootDialog.getSelectedElement();
            }
            else
              return;
          }
        }
        
        
        
        
        if (dialog == null)
        {
          dialog = new SchemaInstanceGenerationDialog(parent, properties);
        }
        
        dialog.show(document);
        
        //dialog.init();
        
        dialog.setVisible(true);
        
        if (!dialog.isCancelled())
        {
          //String rootElement = dialog.getRootElement();
          boolean optionalAttributes = dialog.isOptionalAttributes();
          boolean optionalElements = dialog.isOptionalElements();
          boolean generateData = dialog.isGenerateData();
          
          //System.out.println("****rootElement=" + rootElement);
          
          if (schemaRoot != null)
          {
            
            SchemaInstanceGenerator sig = new SchemaInstanceGenerator();
            
            try
            {
              
              //String schemaText = sig.processXSD(url, rootElement,
              // optionalAttributes, optionalElements, generateData);
              
              //System.out.println("**sig.processXSD");
              
              String schemaText = sig.processXSD(document, schemaRoot,
                  optionalAttributes, optionalElements, generateData);
              
              //System.out.println("**back from sig.processXSD");
              
              ExchangerDocument newDocument = new ExchangerDocument(schemaText);
              
              //System.out.println("**call parent.getFormatAction");
              
              
              /*if (parent.getFormatAction() != null && newDocument.getEncoding() != null)
              {
                final String formattedText = parent.getFormatAction().format( schemaText, newDocument.getEncoding(), null);
               
                ExchangerDocument newDocument2 = new ExchangerDocument(formattedText);
               
               
               
                parent.open(newDocument2, null);
              }
              else
              {
                parent.open(newDocument, null);
               
              }*/
              
              parent.open(newDocument, null);
              
            }
            catch (Exception ex)
            {
              //System.out.println("exception thrown:" + ex.toString() + "..."
              //    + ex.getMessage());
              // This should never happen, just report and continue
              MessageHandler.showError(parent,
                  "Cannot Generate Instance from Scehma",
                  "Generate Instance Error");
              //System.err.println("exception thrown:" + ex.toString() + "..."
              //    + ex.getMessage());
            }
            finally
            {
              parent.setStatus("Done");
              parent.setWait(false);
              //	                     Context.exit();
            }
            
          }
        }
        
      }
      catch (IOException x)
      {
        MessageHandler.showError("Could not create the Document:\n"
            + chooser.getInputLocation(), "Document Error");
      }
      //catch ( SAXParseException x) {
      //	MessageHandler.showError( "Could not parse the Document.", x,
      // "Document Error");
      //}
    }
  }
  
  /**
   * Sets the current view.
   *
   * @param view
   *            the current view.
   */
  public void setView(Object view)
  {
    if (view instanceof Editor)
    {
      editor = (Editor) view;
    }
    else
    {
      editor = null;
    }
    
    setDocument(parent.getDocument());
  }
  
  public void setDocument(ExchangerDocument doc)
  {
    if (doc != null && doc.isXML())
    {
      setEnabled(editor != null);
    }
    else
    {
      setEnabled(false);
    }
  }
  
}
