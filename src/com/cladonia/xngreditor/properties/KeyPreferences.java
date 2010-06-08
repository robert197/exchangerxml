/*
 * $Id: KeyPreferences.java,v 1.51 2005/09/05 15:16:25 tcurley Exp $
 *
 * Copyright (C) 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.JCheckBoxMenuItem;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertiesFile;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XNGRMenuItem;
import com.cladonia.xngreditor.plugins.PluginActionKeyMapping;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;


/**
 * Handles the KeyMapping Preferences.
 *
 * @version	$Revision: 1.51 $, $Date: 2005/09/05 15:16:25 $
 * @author Dogs bay
 */
public class KeyPreferences extends PropertiesFile {

	

	public static final String KEY_MAPPINGS		= "key-mappings";
	private static final String ACTIVE_CONFIG	= "active-configuration";
	private static final String CONFIG			= "configuration";
	private static final String NAME			= "name";
	private static final String KEYMAP			= "keymap";
	private static final String ACTION			= "action";
	private static final String DESCRIPTION		= "description";
	private static final String KEYSTROKE		= "keystroke";
	private static final String KEY				= "key";
	private static final String MASK			= "mask";
	private static final String VALUE			= "value";
	private static final String DEFAULT			= "default";
	public static final String EMACS			= "emacs";
	private static final String CTRL			= "Ctrl";
	private static final String ALT				= "Alt";
	private static final String CTRLSHIFT		= "Ctrl+Shift";
	private static final String CTRLALT			= "Ctrl+Alt";
	private static final String CTRLALTSHIFT	= "Ctrl+Alt+Shift";
	private static final String ALTSHIFT		= "ALT+Shift";
	private static final String META			= "Meta";
	private static final String SHIFT			= "Shift";
	
	//for mac os
	private static final String CMD				= "Command";
	private static final String CMDSHIFT		= "Command+Shift";
	private static final String CMDALT			= "Command+Alt";
	private static final String CMDCTRL			= "Command+Ctrl";
	private static final String CMDALTSHIFT		= "Command+Alt+Shift";
	private static final String CMDCTRLSHIFT	= "Command+Ctrl+Shift";
	private static final String CMDCTRLALTSHIFT	= "Command+Ctrl+Alt+Shift";
	
	
	private List pluginMappings = null;

	//all the possible actions
	public static final String OPEN_ACTION				= "File:  Open";
	public static final String OPEN_ACTION_DESC 		= "Open a file"; 
	
	public static final String CLOSE_ACTION				= "File:  Close";
	public static final String CLOSE_ACTION_DESC 		= "Close current file"; 
	
	public static final String CLOSE_ALL_ACTION			= "File:  CloseAll";
	public static final String CLOSE_ALL_ACTION_DESC 	= "Close all files"; 
	
	public static final String SAVE_ACTION				= "File:  Save";
	public static final String SAVE_ACTION_DESC 		= "Save current file"; 
	
	public static final String SAVE_ALL_ACTION			= "File:  SaveAll";
	public static final String SAVE_ALL_ACTION_DESC 	= "Save all files"; 
	
	public static final String UNDO_ACTION				= "Edit:  Undo";
	public static final String UNDO_ACTION_DESC 		= "Undo last edit"; 
	
	public static final String SELECT_ALL_ACTION		= "Editor:  SelectAll";
	public static final String SELECT_ALL_ACTION_DESC 	= "Select All"; 
	
	public static final String SAVE_AS_ACTION			= "File:  SaveAs";
	public static final String SAVE_AS_ACTION_DESC 		= "Save file as"; 
	
	public static final String SELECT_DOCUMENT_ACTION		= "View:  SelectDocument";
	public static final String SELECT_DOCUMENT_ACTION_DESC 	= "Select Document"; 
	
	public static final String PRINT_ACTION				= "File:  Print";
	public static final String PRINT_ACTION_DESC 		= "Print current document"; 
	
	public static final String FIND_ACTION				= "Edit:  Find";
	public static final String FIND_ACTION_DESC			= "Find in current document";
	
	public static final String FIND_NEXT_ACTION			= "Edit:  FindNext";
	public static final String FIND_NEXT_ACTION_DESC	= "Find next in current document";
	
	public static final String REPLACE_ACTION			= "Edit:  Replace";
	public static final String REPLACE_ACTION_DESC		= "Replace in current document";
	
	public static final String CUT_ACTION 			= "Editor:  Cut";
	public static final String CUT_ACTION_DESC 		= "Cut";
	
	public static final String COPY_ACTION 			= "Editor:  Copy";
	public static final String COPY_ACTION_DESC 	= "Copy";
	
	public static final String PASTE_ACTION 		= "Editor:  Paste";
	public static final String PASTE_ACTION_DESC 	= "Paste";
	
	public static final String COMMENT_ACTION 		= "Edit:  AddComment";
	public static final String COMMENT_ACTION_DESC 	= "Add a comment";
	
	public static final String TAB_ACTION 			= "Edit:  Indent";
	public static final String TAB_ACTION_DESC 		= "Indent";
	
	public static final String GOTO_ACTION 			= "Edit:  GotoLine";
	public static final String GOTO_ACTION_DESC 	= "Goto line number";
	
	public static final String UNINDENT_ACTION 		= "Edit:  Unindent";
	public static final String UNINDENT_ACTION_DESC = "Unindent";
	
	public static final String UP_ACTION 			= "Editor:  Up";
	public static final String UP_ACTION_DESC 		= "Moves insertion point up one line";
	
	public static final String DOWN_ACTION 			= "Editor:  Down";
	public static final String DOWN_ACTION_DESC 	= "Moves insertion point down one line";
	
	public static final String RIGHT_ACTION 		= "Editor:  Forward";
	public static final String RIGHT_ACTION_DESC 	= "Moves insertion point to the right one";
	
	public static final String LEFT_ACTION 			= "Editor:  Backward";
	public static final String LEFT_ACTION_DESC 	= "Moves insertion point to the left one";
	
	public static final String PAGE_UP_ACTION 		= "Editor:  PageUp";
	public static final String PAGE_UP_ACTION_DESC 	= "Moves up one information pane";
	
	public static final String PAGE_DOWN_ACTION 		= "Editor:  PageDown";
	public static final String PAGE_DOWN_ACTION_DESC 	= "Moves down one information pane";
	
	public static final String BEGIN_LINE_ACTION 		= "Editor:  BeginLine";
	public static final String BEGIN_LINE_ACTION_DESC 	= "Moves to beginning of line";
	
	public static final String END_LINE_ACTION 				= "Editor:  EndLine";
	public static final String END_LINE_ACTION_DESC 		= "Moves to end of line";
	
	public static final String BEGIN_ACTION 				= "Editor:  Begin";
	public static final String BEGIN_ACTION_DESC 			= "Moves to beginning of data";
	
	public static final String END_ACTION 					= "Editor:  End";
	public static final String END_ACTION_DESC 				= "Moves to end of data";
	
	public static final String PREVIOUS_WORD_ACTION 		= "Editor:  PreviousWord";
	public static final String PREVIOUS_WORD_ACTION_DESC 	= "Moves to beginning of previous word";
	
	public static final String NEXT_WORD_ACTION 			= "Editor:  NextWord";
	public static final String NEXT_WORD_ACTION_DESC 		= "Moves to beginning of next word";
	
	public static final String DELETE_NEXT_CHAR_ACTION 			= "Editor:  DeleteNextChar";
	public static final String DELETE_NEXT_CHAR_ACTION_DESC 	= "Deletes the next character";
	
	public static final String DELETE_PREV_CHAR_ACTION 			= "Editor:  DeletePrevChar";
	public static final String DELETE_PREV_CHAR_ACTION_DESC 	= "Deletes the previous character";
	
	public static final String WELL_FORMEDNESS_ACTION 			= "XML:  WellFormedness";
	public static final String WELL_FORMEDNESS_ACTION_DESC 	    = "Check Well-formedness";
	
	public static final String VALIDATE_ACTION 					= "XML:  Validate";
	public static final String VALIDATE_ACTION_DESC 	    	= "Validate against a schema";
	
	public static final String START_BROWSER_ACTION 			= "Tools:  StartBrowser";
	public static final String START_BROWSER_ACTION_DESC 	    = "Start Browser";
	
	public static final String NEW_DOCUMENT_ACTION 				= "File:  NewDocument";
	public static final String NEW_DOCUMENT_ACTION_DESC 	    = "New Document";
	
	public static final String REDO_ACTION 						= "Edit:  Redo";
	public static final String REDO_ACTION_DESC 	   		    = "Redo previous undo";
	
	public static final String SELECT_ELEMENT_ACTION 			= "Edit:  SelectElement";
	public static final String SELECT_ELEMENT_ACTION_DESC 	   	= "Select Element";
	
	public static final String SELECT_ELEMENT_CONTENT_ACTION 	  = "Edit:  SelectElementContent";
	public static final String SELECT_ELEMENT_CONTENT_ACTION_DESC = "Select Element Content";
	
	public static final String INSERT_SPECIAL_CHAR_ACTION 	    = "Edit:  InsertSpecialChar";
	public static final String INSERT_SPECIAL_CHAR_ACTION_DESC  = "Insert Special Character";
	
	public static final String TAG_ACTION 	  					= "Edit:  AddTag";
	public static final String TAG_ACTION_DESC 					= "Tag";
	
	public static final String REPEAT_TAG_ACTION 				= "Edit:  RepeatTag";
	public static final String REPEAT_TAG_ACTION_DESC 			= "Repeat last Tag";
	
	public static final String GOTO_START_TAG_ACTION 	  		= "Edit:  GotoStartTag";
	public static final String GOTO_START_TAG_ACTION_DESC 		= "Goto start tag";

	public static final String TOGGLE_EMPTY_ELEMENT_ACTION 	  	= "Edit: ExpandEmptyElement";
	public static final String TOGGLE_EMPTY_ELEMENT_ACTION_DESC = "Expand an empty Element";

	public static final String RENAME_ELEMENT_ACTION 	  		= "Edit: RenameElement";
	public static final String RENAME_ELEMENT_ACTION_DESC 		= "Rename an Element";

	public static final String GOTO_END_TAG_ACTION 	  		    = "Edit:  GotoEndTag";
	public static final String GOTO_END_TAG_ACTION_DESC 		= "Goto end tag";
	

	public static final String TOGGLE_BOOKMARK_ACTION 	  		= "Edit:  ToggleBookmark";
	public static final String TOGGLE_BOOKMARK_ACTION_DESC 		= "Toggle bookmark";

	public static final String GOTO_NEXT_ATTRIBUTE_VALUE_ACTION			= "Edit: GotoNextAttributeValue";
	public static final String GOTO_NEXT_ATTRIBUTE_VALUE_ACTION_DESC	= "Goto next Attribute value";
	
	public static final String GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION		 = "Edit: GotoPreviousAttributeValue";
	public static final String GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION_DESC = "Goto previous Attribute value";


	public static final String SELECT_BOOKMARK_ACTION 	  		= "Edit:  SelectBookmark";
	public static final String SELECT_BOOKMARK_ACTION_DESC 		= "Select Bookmark";
	
	public static final String SELECT_FRAGMENT_ACTION 	  		= "Edit:  SelectFragment";
	public static final String SELECT_FRAGMENT_ACTION_DESC 		= "Select Fragment";

	public static final String SCHEMA_ACTION 	  				= "Views:  Schema";
	public static final String SCHEMA_ACTION_DESC 				= "Switch to schema view";
	
	public static final String OUTLINER_ACTION 	  				= "Views:  Outliner";
	public static final String OUTLINER_ACTION_DESC 			= "Switch to outliner view";
	
	public static final String EDITOR_ACTION 	  				= "Views:  Editor";
	public static final String EDITOR_ACTION_DESC 				= "Switch to editor";
	
	public static final String VIEWER_ACTION 	  				= "Views:  Viewer";
	public static final String VIEWER_ACTION_DESC 				= "Switch to viewer";
	
	//public static final String GRID_ACTION 	  				= "Views:  Grid";
	//public static final String GRID_ACTION_DESC 			= "Switch to grid";
	//public static final String BROWSER_ACTION 	  				= "browserAction";
	//public static final String BROWSER_ACTION_DESC 				= "Switch to browser";
	
	public static final String ADD_ELEMENT_OUTLINER_ACTION 	  	= "Edit:  AddElementOutliner";
	public static final String ADD_ELEMENT_OUTLINER_ACTION_DESC = "Add an element in Outliner view";
	
	public static final String DELETE_ELEMENT_OUTLINER_ACTION 	    = "Edit:  DeleteElementOutliner";
	public static final String DELETE_ELEMENT_OUTLINER_ACTION_DESC	= "Delete an element in Outliner view";
	
	public static final String OPEN_REMOTE_ACTION			= "File:  OpenRemote";
	public static final String OPEN_REMOTE_ACTION_DESC  	= "Open a remote file"; 
	
	public static final String RELOAD_ACTION				= "File:  Reload";
	public static final String RELOAD_ACTION_DESC  			= "Reload the document"; 
	
	public static final String SAVE_AS_TEMPLATE_ACTION		= "File:  SaveAsTemplate";
	public static final String SAVE_AS_TEMPLATE_ACTION_DESC = "Save the current document as a template"; 
	
	public static final String MANAGE_TEMPLATE_ACTION		= "File:  ManageTemplate";
	public static final String MANAGE_TEMPLATE_ACTION_DESC  = "Manage Templates"; 
	
	public static final String PAGE_SETUP_ACTION				= "File:  PageSetUp";
	public static final String PAGE_SETUP_ACTION_DESC   		= "Page Setup"; 
	
	public static final String PREFERENCES_ACTION				= "File:  Preferences";
	public static final String PREFERENCES_ACTION_DESC   		= "Show Preferences Settings"; 
	
	public static final String CREATE_REQUIRED_NODE_ACTION		= "Edit:  CreateRequiredNode";
	public static final String CREATE_REQUIRED_NODE_ACTION_DESC = "Creates the required nodes in the outliner";
	
	public static final String SPLIT_ELEMENT_ACTION		   		= "Edit:  SplitElement";
	public static final String SPLIT_ELEMENT_ACTION_DESC   		= "Splits the element";  
	
	public static final String CONVERT_ENTITIES_ACTION		   	= "Edit:  ConvertEntities";
	public static final String CONVERT_ENTITIES_ACTION_DESC    	= "Convert Entities to Characters"; 
	
	public static final String CONVERT_CHARACTERS_ACTION		 = "Edit:  ConvertCharacters";
	public static final String CONVERT_CHARACTERS_ACTION_DESC    = "Convert Characters to Entities";   
	
	public static final String STRIP_TAG_ACTION		   = "Edit:  StripTags";
	public static final String STRIP_TAG_ACTION_DESC   = "Strips out the tags";
	
	public static final String ADD_CDATA_ACTION		   = "Edit:  AddCDATA";
	public static final String ADD_CDATA_ACTION_DESC   = "Adds a CDATA section";
	
	public static final String LOCK_ACTION		  		= "Edit:  Lock";
	public static final String LOCK_ACTION_DESC   		= "Lock elements and attributes";
	
	public static final String FORMAT_ACTION			= "Edit:  Format";
	public static final String FORMAT_ACTION_DESC   	= "Format the XML";
	
	public static final String EXPAND_ALL_ACTION		= "View:  ExpandAll";
	public static final String EXPAND_ALL_ACTION_DESC   = "Expand All";
	
	public static final String COLLAPSE_ALL_ACTION		= "View:  CollapseAll";
	public static final String COLLAPSE_ALL_ACTION_DESC = "Collapse All";
	
	public static final String SYNCHRONISE_ACTION		= "View:  SynchroniseSelection";
	public static final String SYNCHRONISE_ACTION_DESC  = "Synchronise Selection";
	
	public static final String TOGGLE_FULL_ACTION		= "View:  ToggleFullScreen";
	public static final String TOGGLE_FULL_ACTION_DESC  = "Toggle Full Screen Editing";
	
	public static final String NEW_PROJECT_ACTION		= "Project:  NewProject";
	public static final String NEW_PROJECT_ACTION_DESC  = "Create a New Project";
	
	public static final String IMPORT_PROJECT_ACTION			= "Project:  ImportProject";
	public static final String IMPORT_PROJECT_ACTION_DESC  		= "Import a Project";
	
	public static final String DELETE_PROJECT_ACTION			= "Project:  DeleteProject";
	public static final String DELETE_PROJECT_ACTION_DESC  		= "Delete a Project";
	
	public static final String RENAME_PROJECT_ACTION			= "Project:  RenameProject";
	public static final String RENAME_PROJECT_ACTION_DESC  		= "Rename a Project";
	
	public static final String CHECK_WELLFORMEDNESS_ACTION		 = "Project:  ProjectCheckWellFormedness";
	public static final String CHECK_WELLFORMEDNESS_ACTION_DESC  = "Checks a Project for Well-Formedness";
	
	public static final String VALIDATE_PROJECT_ACTION			= "Project:  ProjectValidate";
	public static final String VALIDATE_PROJECT_ACTION_DESC 	= "Validate a Project";
	
	public static final String FIND_IN_PROJECTS_ACTION			= "Project:  FindInProjects";
	public static final String FIND_IN_PROJECTS_ACTION_DESC    	= "Find in Projects";
	
	public static final String FIND_IN_FILES_ACTION				= "File:  FindInFiles";
	public static final String FIND_IN_FILES_ACTION_DESC    	= "Find in Files";
	
	public static final String ADD_FILE_ACTION					= "Project:  AddFile";
	public static final String ADD_FILE_ACTION_DESC    			= "Add a File to a Project";
	
	public static final String ADD_REMOTE_FILE_ACTION  		  	= "Project:  AddRemoteFile";
	public static final String ADD_REMOTE_FILE_ACTION_DESC    	= "Add a Remote File to a Project";
	
	public static final String REMOVE_FILE_ACTION  		  		= "Project:  RemoveFile";
	public static final String REMOVE_FILE_ACTION_DESC    		= "Remove File from a Project";
	
	public static final String ADD_DIRECTORY_ACTION  	  		= "Project:  AddDirectory";
	public static final String ADD_DIRECTORY_ACTION_DESC   		= "Add a Directory";
	
	public static final String ADD_DIRECTORY_CONTENTS_ACTION  	  	= "Project:  AddDirectoryontents";
	public static final String ADD_DIRECTORY_CONTENTS_ACTION_DESC   = "Add Directory Contents"; 
	
	public static final String ADD_FOLDER_ACTION  	  			= "Project:  AddFolder";
	public static final String ADD_FOLDER_ACTION_DESC   		= "Add Folder"; 
	
	public static final String REMOVE_FOLDER_ACTION  	  		= "Project:  RemoveFolder";
	public static final String REMOVE_FOLDER_ACTION_DESC   		= "Remove Folder"; 
	
	public static final String RENAME_FOLDER_ACTION  	  		= "Project:  RenameFolder";
	public static final String RENAME_FOLDER_ACTION_DESC   		= "Rename Folder"; 
	
	public static final String VALIDATE_XML_SCHEMA_ACTION 		= "Schema:  ValidateXMLSchema";
	public static final String VALIDATE_XML_SCHEMA_ACTION_DESC  = "Validate XML Schema"; 
	
	public static final String VALIDATE_DTD_ACTION 	       		= "Schema:  ValidateDTD";
	public static final String VALIDATE_DTD_ACTION_DESC   		= "Validate DTD"; 
	
	public static final String VALIDATE_RELAXNG_ACTION 	   	 	= "Schema:  ValidateRelaxNG";
	public static final String VALIDATED_RELAXNG_ACTION_DESC	= "Validate Relax NG";
	
	public static final String SET_XML_DECLARATION_ACTION 	   	   = "XML:  SetXMLDeclaration";
	public static final String SET_XML_DECLARATION_ACTION_DESC 	   = "Set XML Declaration";
	
	public static final String SET_DOCTYPE_DECLARATION_ACTION 	   = "XML:  SetDOCTYPEDeclaration";
	public static final String SET_DOCTYPE_DECLARATION_ACTION_DESC = "Set DOCTYPE Declaration";  
	
	public static final String SET_SCHEMA_LOCATION_ACTION 	       = "XML:  SetSchemaLocation";
	public static final String SET_SCHEMA_LOCATION_ACTION_DESC 	   = "Set Schema Location"; 
	
	public static final String RESOLVE_XINCLUDES_ACTION 	   	= "XML:  ResolveXIncludes";
	public static final String RESOLVE_XINCLUDES_ACTION_DESC   	= "Resolve XIncludes"; 
	
	public static final String SET_SCHEMA_PROPS_ACTION 	   	   	= "XML:  SetDocumentProperties";
	public static final String SET_SCHEMA_PROPS_ACTION_DESC   	= "Set Document Properties";
	
	public static final String INFER_SCHEMA_ACTION 	   	   		= "Schema:  InferSchemaProperties";
	public static final String INFER_SCHEMA_ACTION_DESC   		= "Infer an XML Schema";  
	
	public static final String CREATE_TYPE_ACTION 	   	   		= "Schema:  CreateType";
	public static final String CREATE_TYPE_ACTION_DESC   		= "Create a new Type";
	
	public static final String SET_TYPE_ACTION 	   	   			= "Schema:  SetType";
	public static final String SET_TYPE_ACTION_DESC   			= "Set the Type";
	
	public static final String TYPE_PROPERTIES_ACTION 	   	 	= "Schema:  TypeProperties";
	public static final String TYPE_PROPERTIES_ACTION_DESC   	= "Type Properties";  
	
	public static final String MANAGE_TYPES_ACTION 	   	     	= "Schema:  ManageTypes";
	public static final String MANAGE_TYPES_ACTION_DESC      	= "Manage Types";
	
	public static final String CONVERT_SCHEMA_ACTION 	   	 	= "Schema:  ConvertSchema";
	public static final String CONVERT_SCHEMA_ACTION_DESC    	= "Convert Schema";

	
	public static final String SCHEMA_INSTANCE_GENERATION_ACTION 	   	 	= "Schema:  GenerateInstance";
	public static final String SCHEMA_INSTANCE_GENERATION_ACTION_DESC    	= "Generate Instance from Schema";


	public static final String EXECUTE_SCRIPT_ACTION 	   	 	= "Tools:  ExecuteScript";
	public static final String EXECUTE_SCRIPT_ACTION_DESC    	= "Execute Script";

	public static final String GRAPHICAL_SCHEMA_GENERATION_ACTION 	   	 	= "Schema:  GenerateGraph";
	public static final String GRAPHICAL_SCHEMA_GENERATION_ACTION_DESC    	= "Generate Graph from Schema";

	public static final String EXECUTE_SIMPLE_XSLT_ACTION 	   	 		= "Transform:  ExecuteSimpleXSLT";
	public static final String EXECUTE_SIMPLE_XSLT_ACTION_DESC  		= "Execute Simple XSLT";
	
	public static final String EXECUTE_ADVANCED_XSLT_ACTION 	   	 		= "Transform:  ExecuteAdvancedXSLT";
	public static final String EXECUTE_ADVANCED_XSLT_ACTION_DESC  		= "Execute Advanced XSLT";
	
	public static final String EXECUTE_PREVIOUS_XSLT_ACTION 	  = "Transform:  ExecutePreviousXSLT";
	public static final String EXECUTE_PREVIOUS_XSLT_ACTION_DESC  = "Execute Previous XSLT";

	public static final String EXECUTE_FO_ACTION 	   	 		= "Transform:  ExecuteFO";
	public static final String EXECUTE_FO_ACTION_DESC  			= "Execute FO";
	
	public static final String EXECUTE_PREVIOUS_FO_ACTION 	   	= "Transform:  ExecutePreviousFO";
	public static final String EXECUTE_PREVIOUS_FO_ACTION_DESC  = "Execute Previous FO";

	public static final String EXECUTE_XQUERY_ACTION 	   	 	= "Transform:  ExecuteXQuery";
	public static final String EXECUTE_XQUERY_ACTION_DESC  	 	= "Execute XQuery";
	
	public static final String EXECUTE_SCHEMATRON_ACTION 	   	 	= "Transform:  ExecuteSchematron";
	public static final String EXECUTE_SCHEMATRON_ACTION_DESC  	 	= "Execute Schematron";
	
	public static final String EXECUTE_PREVIOUS_XQUERY_ACTION 	   	= "Transform:  ExecutePreviousXQuery";
	public static final String EXECUTE_PREVIOUS_XQUERY_ACTION_DESC  = "Execute Previous XQuery";

	public static final String EXECUTE_SCENARIO_ACTION 	   	 	= "Transform:  ExecuteScenario";
	public static final String EXECUTE_SCENARIO_ACTION_DESC  	= "Execute Scenario";
	
	public static final String EXECUTE_PREVIOUS_SCENARIO_ACTION 		= "Transform:  ExecutePreviousScenario";
	public static final String EXECUTE_PREVIOUS_SCENARIO_ACTION_DESC  	= "Execute Previous Scenario";

	public static final String XSLT_DEBUGGER_ACTION 	   	 	= "Transform:  StartXSLTDebugger";
	public static final String XSLT_DEBUGGER_ACTION_DESC  		= "Start the XSLT Debugger";
	
	public static final String MANAGE_SCENARIOS_ACTION	   	 	= "Transform:  ManageScenarios";
	public static final String MANAGE_SCENARIOS_ACTION_DESC   	= "Manage Scenarios";
	
	public static final String CANONICALIZE_ACTION	   	  		= "Security:  Canonicalize";
	public static final String CANONICALIZE_ACTION_DESC   		= "Canonicalize";
	
	public static final String SIGN_DOCUMENT_ACTION	   	   		= "Security:  SignDocument";
	public static final String SIGN_DOCUMENT_ACTION_DESC   		= "Sign Document";
	
	public static final String VERIFY_SIGNATURE_ACTION	   	 	= "Security:  VerifySignature";
	public static final String VERIFY_SIGNATURE_ACTION_DESC  	= "Verify Signature";
	
	public static final String SHOW_SVG_ACTION	   	 			= "Tools:  ShowSVG";
	public static final String SHOW_SVG_ACTION_DESC  			= "Show SVG";
	
	public static final String CONVERT_SVG_ACTION	   	 		= "Tools:  ConvertSVG";
	public static final String CONVERT_SVG_ACTION_DESC  		= "Convert SVG";
	
	public static final String SEND_SOAP_MESSAGE_ACTION	   	 	= "Tools:  SendSOAPMessage";
	public static final String SEND_SOAP_MESSAGE_ACTION_DESC  	= "Send SOAP Message";
	
	public static final String ANALYSE_WSDL_ACTION	   	 		= "Tools:  AnalyseWSDL";
	public static final String ANALYSE_WSDL_ACTION_DESC  		= "Analyse WSDL";
	
	public static final String CLEAN_UP_HTML_ACTION	   	 		= "Tools:  CleanUpHTML";
	public static final String CLEAN_UP_HTML_ACTION_DESC  		= "Clean Up HTML";
	
	public static final String IMPORT_FROM_TEXT_ACTION			= "File:  ImportFromText";
	public static final String IMPORT_FROM_TEXT_ACTION_DESC		= "Import From Text";
	
	public static final String IMPORT_FROM_EXCEL_ACTION			= "File:  ImportFromExcel";
	public static final String IMPORT_FROM_EXCEL_ACTION_DESC	= "Import From Excel";
	
	public static final String IMPORT_FROM_DBTABLE_ACTION		= "File:  ImportFromDBTable";
	public static final String IMPORT_FROM_DBTABLE_ACTION_DESC	= "Import From Database Table";
	
	public static final String XDIFF_ACTION						= "Tools:  XML Diff and Merge";
	public static final String XDIFF_ACTION_DESC                = "XML Diff and Merge functionality";
	
	public static final String TOOLS_EMPTY_DOCUMENT_ACTION		= "XML:  EmptyDocument";
	public static final String TOOLS_EMPTY_DOCUMENT_ACTION_DESC	= "Empty Document";
	
	public static final String TOOLS_CAPITALIZE_ACTION			= "XML:  Capitalize";
	public static final String TOOLS_CAPITALIZE_ACTION_DESC		= "Capitalize";
	
	public static final String TOOLS_DECAPITALIZE_ACTION		= "XML:  DeCapitalize";
	public static final String TOOLS_DECAPITALIZE_ACTION_DESC	= "DeCapitalize";
	
	public static final String TOOLS_LOWERCASE_ACTION			= "XML:  Lowercase";
	public static final String TOOLS_LOWERCASE_ACTION_DESC		= "Lowercase";
	
	public static final String TOOLS_UPPERCASE_ACTION			= "XML:  Uppercase";
	public static final String TOOLS_UPPERCASE_ACTION_DESC		= "Uppercase";
	
	public static final String IMPORT_FROM_SQLXML_ACTION		= "File:  ImportFromSQLXML";
	public static final String IMPORT_FROM_SQLXML_ACTION_DESC	= "Import From SQL/XML Query";
	
	public static final String TOOLS_MOVE_NS_TO_ROOT_ACTION		= "XML:  MoveNamespacesToRoot";
	public static final String TOOLS_MOVE_NS_TO_ROOT_ACTION_DESC= "Move All Namespace Declarations To The Root";
	
	public static final String TOOLS_MOVE_NS_TO_FIRST_USED_ACTION		= "XML:  MoveNamespacesToWhereFirstUsed";
	public static final String TOOLS_MOVE_NS_TO_FIRST_USED_ACTION_DESC	= "Move All Namespace Declarations To Where They Are First Used";
	
	public static final String TOOLS_CHANGE_NS_PREFIX_ACTION		= "XML:  ChangeNamespacePrefix";
	public static final String TOOLS_CHANGE_NS_PREFIX_ACTION_DESC	= "Change A Namespace Prefix";
	
	public static final String TOOLS_RENAME_NODE_ACTION			= "XML:  RenameNode";
	public static final String TOOLS_RENAME_NODE_ACTION_DESC	= "Rename a Node";
	
	public static final String TOOLS_REMOVE_NODE_ACTION			= "XML:  RemoveNode";
	public static final String TOOLS_REMOVE_NODE_ACTION_DESC	= "Remove a Node";
    
	public static final String TOOLS_ADD_NODE_TO_NS_ACTION			= "XML:  AddNodeToNamespace";
	public static final String TOOLS_ADD_NODE_TO_NS_ACTION_DESC	= "Add a Node To a Namespace";
	
	public static final String TOOLS_SET_NODE_VALUE_ACTION		= "XML:  SetNodeValue";
	public static final String TOOLS_SET_NODE_VALUE_ACTION_DESC = "Set Node Value";
	
	public static final String TOOLS_ADD_NODE_ACTION			= "XML:  AddNode";
	public static final String TOOLS_ADD_NODE_ACTION_DESC		= "Add a Node";

	public static final String TOOLS_REMOVE_UNUSED_NS_ACTION		= "XML:  RemoveUsusedNamespaces";
	public static final String TOOLS_REMOVE_UNUSED_NS_ACTION_DESC	= "Remove Any Usused Namespaces";
	
	public static final String TOOLS_CONVERT_NODE_ACTION		= "XML:  ConvertNode";
	public static final String TOOLS_CONVERT_NODE_ACTION_DESC	= "Convert a Node Type";
	
	public static final String TOOLS_SORT_NODE_ACTION		= "XML:  SortNode";
	public static final String TOOLS_SORT_NODE_ACTION_DESC	= "Sort a Node Type";
	
	// for the debugger
	public static final String DEBUGGER_NEW_TRANSFORMATION_ACTION		= "Debugger:  NewTransformation";
	public static final String DEBUGGER_NEW_TRANSFORMATION_ACTION_DESC	= "New Debugger Transformation";
	
	public static final String DEBUGGER_CLOSE_ACTION		= "Debugger:  Close";
	public static final String DEBUGGER_CLOSE_ACTION_DESC	= "Close the Debugger";
	
	public static final String DEBUGGER_OPEN_SCENARIO_ACTION		= "Debugger:  OpenScenario";
	public static final String DEBUGGER_OPEN_SCENARIO_ACTION_DESC	= "Open a Scenario";
	
	public static final String DEBUGGER_OPEN_INPUT_ACTION		= "Debugger:  OpenInput";
	public static final String DEBUGGER_OPEN_INPUT_ACTION_DESC	= "Open input document";
	
	public static final String DEBUGGER_OPEN_STYLESHEET_ACTION		= "Debugger:  OpenStylesheet";
	public static final String DEBUGGER_OPEN_STYLESHEET_ACTION_DESC	= "Open stylesheet";
	
	public static final String DEBUGGER_CLOSE_TRANSFORMATION_ACTION		= "Debugger:  CloseTransformation";
	public static final String DEBUGGER_CLOSE_TRANSFORMATION_ACTION_DESC	= "Close a Transformation";
	
	public static final String DEBUGGER_SAVE_AS_SCENARIO_ACTION		= "Debugger:  SaveAsScenario";
	public static final String DEBUGGER_SAVE_AS_SCENARIO_ACTION_DESC	= "Save as Scenario";
	
	public static final String DEBUGGER_FIND_ACTION		= "Debugger:  Find";
	public static final String DEBUGGER_FIND_ACTION_DESC	= "Find in debugger files";
	
	public static final String DEBUGGER_FIND_NEXT_ACTION		= "Debugger:  FindNext";
	public static final String DEBUGGER_FIND_NEXT_ACTION_DESC	= "Find next in debugger files";
	
	public static final String DEBUGGER_GOTO_ACTION		= "Debugger:  Goto";
	public static final String DEBUGGER_GOTO_ACTION_DESC	= "Goto line number";
	
	public static final String DEBUGGER_START_ACTION		= "Debugger:  Start";
	public static final String DEBUGGER_START_ACTION_DESC	= "Start debugging";
	
	public static final String DEBUGGER_RUN_END_ACTION		= "Debugger:  RunToEnd";
	public static final String DEBUGGER_RUN_END_ACTION_DESC	= "Run to end";
	
	public static final String DEBUGGER_PAUSE_ACTION		= "Debugger:  Pause";
	public static final String DEBUGGER_PAUSE_ACTION_DESC	= "Pause debugger";
	
	public static final String DEBUGGER_STOP_ACTION		= "Debugger:  Stop";
	public static final String DEBUGGER_STOP_ACTION_DESC	= "Stop debugger";
	
	public static final String DEBUGGER_STEP_INTO_ACTION		= "Debugger:  StepInto";
	public static final String DEBUGGER_STEP_INTO_ACTION_DESC	= "Step into";
	
	public static final String DEBUGGER_STEP_OVER_ACTION		= "Debugger:  StepOver";
	public static final String DEBUGGER_STEP_OVER_ACTION_DESC	= "Step over";
	
	public static final String DEBUGGER_STEP_OUT_ACTION		= "Debugger:  StepOut";
	public static final String DEBUGGER_STEP_OUT_ACTION_DESC	= "Step out";
	
	public static final String DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION		= "Debugger:  RemoveAllBreakpoints";
	public static final String DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION_DESC	= "Remove all breakpoints";
	
	public static final String DEBUGGER_RELOAD_ACTION		= "Debugger:  Reload";
	public static final String DEBUGGER_RELOAD_ACTION_DESC	= "Reload";
	
	public static final String DEBUGGER_EXIT_ACTION		= "Debugger:  Exit";
	public static final String DEBUGGER_EXIT_ACTION_DESC	= "Exit the debugger";
	
	public static final String DEBUGGER_COLLAPSE_ALL_ACTION		= "Debugger:  CollapseAll";
	public static final String DEBUGGER_COLLAPSE_ALL_ACTION_DESC	= "Collapse All";
	
	public static final String DEBUGGER_EXPAND_ALL_ACTION		= "Debugger:  ExpandAll";
	public static final String DEBUGGER_EXPAND_ALL_ACTION_DESC	= "Expand All";
	
	public static final String DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION	= "Debugger:  StylesheetShowLineNumberMargin";
	public static final String DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION_DESC	= "Show the stylesheet line number margin";
	
	public static final String DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION	= "Debugger:  StylesheetShowOverviewMargin";
	public static final String DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION_DESC	= "Show the stylesheet overview margin";
	
	public static final String DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION	= "Debugger:  StylesheetShowFoldingMargin";
	public static final String DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION_DESC	= "Show the stylesheet folding margin";
	
	public static final String DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION	= "Debugger:  StylesheetUseSoftWrapping";	
	public static final String DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION_DESC	= "Use soft wrapping for the stylesheet";
	
	public static final String DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION	= "Debugger:  InputShowLineNumberMargin";
	public static final String DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION_DESC	= "Show the input line number margin";
	
	public static final String DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION	= "Debugger:  InputShowOverviewMargin";
	public static final String DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION_DESC	= "Show the input overview margin";
	
	public static final String DEBUGGER_INPUT_SHOW_FOLDING_ACTION	= "Debugger:  InputShowFoldingMargin";
	public static final String DEBUGGER_INPUT_SHOW_FOLDING_ACTION_DESC	= "Show the input folding margin";
	
	public static final String DEBUGGER_INPUT_SOFT_WRAPPING_ACTION	    = "Debugger:  InputUseSoftWrapping";	
	public static final String DEBUGGER_INPUT_SOFT_WRAPPING_ACTION_DESC	= "Use soft wrapping for the input";
	
	public static final String DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION	    = "Debugger:  OutputShowLineNumberMargin";
	public static final String DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION_DESC	= "Show the output line number margin";
	
	public static final String DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION	        = "Debugger:  OutputUseSoftWrapping";	
	public static final String DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION_DESC	= "Use soft wrapping for the output";
	
	public static final String DEBUGGER_AUTO_OPEN_INPUT_ACTION	        = "Debugger:  AutomaticallyOpenInput";	
	public static final String DEBUGGER_AUTO_OPEN_INPUT_ACTION_DESC	    = "Automatically open input";
	
	public static final String DEBUGGER_ENABLE_TRACING_ACTION	        = "Debugger:  EnableTracing";	
	public static final String DEBUGGER_ENABLE_TRACING_ACTION_DESC	    = "Enable tracing";
	
	public static final String DEBUGGER_REDIRECT_OUTPUT_ACTION	        = "Debugger:  RedirectOutput";	
	public static final String DEBUGGER_REDIRECT_OUTPUT_ACTION_DESC	    = "Redirect output";
	
	public static final String DEBUGGER_SET_PARAMETERS_ACTION	        = "Debugger:  SetParameters";	
	public static final String DEBUGGER_SET_PARAMETERS_ACTION_DESC	    = "Set parameters";
	
	public static final String DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION	        = "Debugger:  DisableAllBreakPoints";	
	public static final String DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION_DESC	    = "Disable all breakpoints";
	
	public static final String DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION	        = "Debugger:  EnableAllBreakPoints";	
	public static final String DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION_DESC	    = "Enable all breakpoints";
	
	// end of debugger keys
	
	public static final String HIGHLIGHT_ACTION	        = "View:  Highlight";	
	public static final String HIGHLIGHT_ACTION_DESC	= "Highlight";
	
	public static final String VIEW_STANDARD_BUTTONS_ACTION	        = "View:  StandardButtons";	
	public static final String VIEW_STANDARD_BUTTONS_ACTION_DESC	= "View standard buttons";
	
	public static final String VIEW_EDITOR_BUTTONS_ACTION	        = "View:  EditorButtons";	
	public static final String VIEW_EDITOR_BUTTONS_ACTION_DESC	    = "View Editor buttons";
	
	public static final String VIEW_FRAGMENT_BUTTONS_ACTION	        = "View:  FragmentButtons";	
	public static final String VIEW_FRAGMENT_BUTTONS_ACTION_DESC	= "View fragment buttons";
	
	public static final String VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION	= "View:  EditorShowLineNumberMargin";
	public static final String VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION_DESC	= "Show the Editor's line number margin";
	
	public static final String VIEW_EDITOR_SHOW_OVERVIEW_ACTION	= "View:  EitordShowOverviewMargin";
	public static final String VIEW_EDITOR_SHOW_OVERVIEW_ACTION_DESC	= "Show the Editor's overview margin";
	
	public static final String VIEW_EDITOR_SHOW_FOLDING_ACTION	= "View:  EditorShowFoldingMargin";
	public static final String VIEW_EDITOR_SHOW_FOLDING_ACTION_DESC	= "Show the Editor's folding margin";
	
	public static final String VIEW_EDITOR_SHOW_ANNOTATION_ACTION		= "View:  EditorShowAnnotationMargin";
	public static final String VIEW_EDITOR_SHOW_ANNOTATION_ACTION_DESC	= "Show the Editor's annotation margin";

	public static final String VIEW_EDITOR_TAG_COMPLETION_ACTION		= "View:  EditorUseTagCompletion";
	public static final String VIEW_EDITOR_TAG_COMPLETION_ACTION_DESC	= "Use the Editor's tag completion";
	
	public static final String VIEW_EDITOR_END_TAG_COMPLETION_ACTION	    = "View:  EditorUseEndTagCompletion";
	public static final String VIEW_EDITOR_END_TAG_COMPLETION_ACTION_DESC	= "Use the Editor's end tag completion";
	
	public static final String VIEW_EDITOR_SMART_INDENTATION_ACTION	        = "View:  EditorUseSmartIndentation";
	public static final String VIEW_EDITOR_SMART_INDENTATION_ACTION_DESC	= "Use the Editor's smart indentation";
	
	public static final String VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION	    = "View:  EditorUseErrorHighlighting";
	public static final String VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION_DESC	= "Highlight errors in the Editor";

	public static final String VIEW_EDITOR_SOFT_WRAPPING_ACTION	        = "View:  EditorUseSoftWrapping";	
	public static final String VIEW_EDITOR_SOFT_WRAPPING_ACTION_DESC	= "Use soft wrapping for the Editor";
	
	public static final String VIEWER_SHOW_NAMESPACES_ACTION	    = "View:  ViewerShowNamespace";	
	public static final String VIEWER_SHOW_NAMESPACES_ACTION_DESC	= "Show namespace in the Viewer";
	
	public static final String VIEWER_SHOW_ATTRIBUTES_ACTION	    = "View:  ViewerShowAttributes";	
	public static final String VIEWER_SHOW_ATTRIBUTES_ACTION_DESC	= "Show attributes in the Viewer";
	
	public static final String VIEWER_SHOW_COMMENTS_ACTION	    = "View:  ViewerShowComments";	
	public static final String VIEWER_SHOW_COMMENTS_ACTION_DESC	= "Show comments in the Viewer";
	
	public static final String VIEWER_SHOW_TEXT_CONTENT_ACTION	    = "View:  ViewerShowtextContent";	
	public static final String VIEWER_SHOW_TEXT_CONTENT_ACTION_DESC	= "Show text content in the Viewer";
	
	public static final String VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION	    = "View:  ViewerShowProcessingInstructions";	
	public static final String VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION_DESC	= "Show processing instructions in the Viewer";
	
	public static final String VIEWER_INLINE_MIXED_CONTENT_ACTION	    		= "View:  ViewerInlineMixedContent";	
	public static final String VIEWER_INLINE_MIXED_CONTENT_ACTION_DESC	= "Inline mixed content in the Viewer";
	
	public static final String OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION	    = "View:  OutlinerShowAttributeValues";	
	public static final String OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION_DESC	= "Show Attribute values in the Outliner";
	
	public static final String OUTLINER_SHOW_ELEMENT_VALUES_ACTION	    = "View:  OutlinerShowElementValues";	
	public static final String OUTLINER_SHOW_ELEMENT_VALUES_ACTION_DESC	= "Show Element values in the Outliner";
	
	public static final String OUTLINER_CREATE_REQUIRED_NODES_ACTION	    = "View:  OutlinerCreateRequiredNodes";	
	public static final String OUTLINER_CREATE_REQUIRED_NODES_ACTION_DESC	= "Automatically create required nodes";
	
	public static final String VIEW_SYNCHRONIZE_SPLITS_ACTION	        = "View:  SynchroniseSplits";	
	public static final String VIEW_SYNCHRONIZE_SPLITS_ACTION_DESC	= "Synchronise the splits";
	
	public static final String VIEW_SPLIT_HORIZONTALLY_ACTION	        = "View:  SplitHorizontally";	
	public static final String VIEW_SPLIT_HORIZONTALLY_ACTION_DESC	    = "Split horizontally";
	
	public static final String VIEW_SPLIT_VERTICALLY_ACTION	        = "View:  SplitVertically";	
	public static final String VIEW_SPLIT_VERTICALLY_ACTION_DESC	= "Split vertically";
	
	public static final String VIEW_UNSPLIT_ACTION	        = "View:  Unsplit";	
	public static final String VIEW_UNSPLIT_ACTION_DESC		= "Unsplit";
	
	/*public static final String GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION	=	"Grid: AddAttributeToSelected";
	public static final String GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION_DESC	=	"Add Attribute To Selected";
	
	public static final String GRID_ADD_TEXT_TO_SELECTED_ACTION			=	"Grid: AddTextToSelected";
	public static final String GRID_ADD_TEXT_TO_SELECTED_ACTION_DESC			=	"Add Text To Selected";
	
	public static final String GRID_ADD_CHILD_TABLE_ACTION				=	"Grid: AddChildTable";
	public static final String GRID_ADD_CHILD_TABLE_ACTION_DESC				=	"Add Child Table";
	
	public static final String GRID_ADD_ELEMENT_BEFORE_ACTION			=	"Grid: AddElementBefore";
	public static final String GRID_ADD_ELEMENT_BEFORE_ACTION_DESC			=	"Add Element Before";
	
	public static final String GRID_ADD_ELEMENT_AFTER_ACTION			=	"Grid: AddElementAfter";
	public static final String GRID_ADD_ELEMENT_AFTER_ACTION_DESC			=	"Add Element After";
	
	public static final String GRID_ADD_ATTRIBUTE_COLUMN_ACTION			=	"Grid: AddAttributeColumn";
	public static final String GRID_ADD_ATTRIBUTE_COLUMN_ACTION_DESC			=	"Add Attribute Column";
	
	public static final String GRID_ADD_TEXT_COLUMN_ACTION				=	"Grid: AddTextColumn";
	public static final String GRID_ADD_TEXT_COLUMN_ACTION_DESC				=	"Add Text Column";
	
	public static final String GRID_DELETE_SELECTED_ATTRIBUTE_ACTION	=	"Grid: DeleteSelectedAttribute";
	public static final String GRID_DELETE_SELECTED_ATTRIBUTE_ACTION_DESC	=	"Delete Selected Attribute";
	
	public static final String GRID_DELETE_SELECTED_TEXT_ACTION			=	"Grid: DeleteSelectedText";
	public static final String GRID_DELETE_SELECTED_TEXT_ACTION_DESC			=	"Delete Selected Text";
	
	public static final String GRID_DELETE_ROW_ACTION					=	"Grid: DeleteRow";
	public static final String GRID_DELETE_ROW_ACTION_DESC					=	"Delete Row";
	
	public static final String GRID_DELETE_CHILD_TABLE_ACTION			=	"Grid: DeleteChildTable";
	public static final String GRID_DELETE_CHILD_TABLE_ACTION_DESC			=	"Delete Child Table";
	
	public static final String GRID_DELETE_COLUMN_ACTION				=	"Grid: DeleteColumn";
	public static final String GRID_DELETE_COLUMN_ACTION_DESC				=	"Delete Column";
	
	public static final String GRID_DELETE_ATTS_AND_TEXT_ACTION			=	"Grid: DeleteAttributesAndText";
	public static final String GRID_DELETE_ATTS_AND_TEXT_ACTION_DESC			=	"Delete Attributes And Text";
	
	public static final String GRID_RENAME_ATTRIBUTE_ACTION			=	"Grid: RenameAttribute";
	public static final String GRID_RENAME_ATTRIBUTE_ACTION_DESC			=	"Rename Attribute";
	
	public static final String GRID_RENAME_SELECTED_ATTRIBUTE_ACTION			=	"Grid: RenameSelectedAttribute";
	public static final String GRID_RENAME_SELECTED_ATTRIBUTE_ACTION_DESC			=	"Rename Selected Attribute";
		
	public static final String GRID_MOVE_ROW_UP_ACTION					=	"Grid: MoveRowUp";
	public static final String GRID_MOVE_ROW_UP_ACTION_DESC					=	"Move Row Up";
	
	public static final String GRID_MOVE_ROW_DOWN_ACTION				=	"Grid: MoveRowDown";
	public static final String GRID_MOVE_ROW_DOWN_ACTION_DESC				=	"Move Row Down";
	
	public static final String GRID_SORT_TABLE_DESCENDING_ACTION					=	"Grid: SortDescending";
	public static final String GRID_SORT_TABLE_DESCENDING_ACTION_DESC					=	"Sort Descending";
	
	public static final String GRID_SORT_TABLE_ASCENDING_ACTION					=	"Grid: SortAscending";
	public static final String GRID_SORT_TABLE_ASCENDING_ACTION_DESC					=	"Sort Ascending";
	
	public static final String GRID_UNSORT_ACTION					=	"Grid: Unsort";
	public static final String GRID_UNSORT_ACTION_DESC					=	"Unsort";
	
	public static final String GRID_GOTO_PARENT_TABLE_ACTION					=	"Grid: GotoParentTable";
	public static final String GRID_COLLAPSE_ACTION_DESC					=	"Goto Parent Table";
	
	public static final String GRID_GOTO_CHILD_TABLE_ACTION					=	"Grid: GotoChildTable";
	public static final String GRID_EXPAND_ACTION_DESC					=	"Goto Child Table";
	
	public static final String GRID_COLLAPSE_ROW_ACTION					=	"Grid: CollapseRow";
	public static final String GRID_COLLAPSE_ROW_ACTION_DESC					=	"Collapse Row";
	
	public static final String GRID_EXPAND_ROW_ACTION					=	"Grid: ExpandRow";
	public static final String GRID_EXPAND_ROW_ACTION_DESC					=	"Expand Row";
	
	public static final String GRID_COPY_SHALLOW_ACTION					=	"Grid: CopyShallow";
	public static final String GRID_COPY_SHALLOW_ACTION_DESC					=	"Copy Shallow";
	
	public static final String GRID_PASTE_AS_CHILD_ACTION					=	"Grid: PasteAsChild";
	public static final String GRID_PASTE_AS_CHILD_ACTION_DESC					=	"Paste As Child";
	
	public static final String GRID_PASTE_BEFORE_ACTION					=	"Grid: PasteBefore";
	public static final String GRID_PASTE_BEFORE_ACTION_DESC					=	"Paste Before";
	
	public static final String GRID_PASTE_AFTER_ACTION					=	"Grid: PasteAfter";
	public static final String GRID_PASTE_AFTER_ACTION_DESC					=	"Paste After";
	
	public static final String GRID_COLLAPSE_CURRENT_TABLE_ACTION		=	"Grid: CollapseCurrentTable";
	public static final String GRID_COLLAPSE_CURRENT_TABLE_ACTION_DESC		=	"Collapse Current Table";
	
	public static final String GRID_DELETE_ACTION		=	"Grid: DeleteCurrentCell";
	public static final String GRID_DELETE_ACTION_DESC		=	"Delete The Current Cell";*/
	
	
	
	
	
	/**
	 * Creates the Key Preferences.
	 *
	 * @param element the security preferences element.
	 */
	/*public KeyPreferences(ExchangerDocument document, XElement element) {
		super( document, element);
		
		//addDefaultConfigurations();
	}*/
	
	public KeyPreferences(String fileName, String rootName) {
		super(loadPropertiesFile(fileName, rootName));
		addDefaultConfigurations();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the active configuration name
	 *
	 * @return The XNGRMenuItem
	 */
	public String getActiveConfiguration() 
	{
		return getText(ACTIVE_CONFIG, DEFAULT);
	}

	/**
	 * Sets the active configuration
	 *
	 * @param name The active configuration name
	 */
	public void setActiveConfiguration(String name) 
	{
		set(ACTIVE_CONFIG, name);
	}
	
	/**
	 * Returns all the configurations
	 *
	 * @return Vector containing the configurations
	 */
	public Vector getConfigurations() 
	{
		return getProperties(CONFIG);
	}	
	
	/**
	 * Returns all the configuration names
	 *
	 * @return Vector containing the configuration names
	 */
	public Vector getConfigurationNames()
	{
		Vector configNames = new Vector();
		
		Vector configs = getConfigurations();
		
		for (int i=0;i<configs.size();i++)
		{
			String name = ((Properties)configs.get(i)).getText(NAME);
			configNames.add(name);
		}
		
		return configNames;
	}
	
	/**
	 * Get the configuration element for a particular name, else create it
	 */
	private XElement getConfiguration(String configName)
	{
		XElement config = null;
		Vector configs = getConfigurations();
		
		for (int i=0;i<configs.size();i++)
		{
			XElement name = ((Properties)configs.get(i)).get(NAME);
			if (name.getText().equals(configName))
			{
				config = ((Properties)configs.get(i)).getElement();
				break;
			}
		}
		
		if (config == null)
		{
			// doesn't already exist, so we need to create it
			config = new XElement(CONFIG);
			
			XElement nameEle = new XElement(NAME);
			nameEle.setText(configName);
			
			config.add(nameEle);
			getElement().add(config);
		}
		
		return config;
	}
	
	/**
	 * adds the default configuration for the first time the app starts
	 */
	private void addDefaultConfigurations()
	{
		
		setActiveConfiguration(getActiveConfiguration());
		
		setDefaultKeyMappings();
		setEmacsKeyMappings();
	}
	
	public void updateDefaultConfigurations()
	{
		setActiveConfiguration(getActiveConfiguration());
		
		setDefaultKeyMappings();
		setEmacsKeyMappings();
	}
	
	/**
	 * sets the default keys for the first time the app starts
	 */
	private void setDefaultKeyMappings()
	{

		Hashtable mappings = getKeyMaps(DEFAULT);
		
		if (System.getProperty("mrj.version") == null)
		{
		
			if (mappings.get(OPEN_ACTION) == null)
			{
				Keystroke keystroke1 = new Keystroke(CTRL,"O");
				setKeyMap(DEFAULT,OPEN_ACTION,OPEN_ACTION_DESC,keystroke1);
			}
		
			if (mappings.get(CLOSE_ACTION) == null)
			{
				Keystroke keystroke2 = new Keystroke(CTRL,"W");
				setKeyMap(DEFAULT,CLOSE_ACTION,CLOSE_ACTION_DESC,keystroke2);
			}
		
			if (mappings.get(CLOSE_ALL_ACTION) == null)
			{
				Keystroke keystroke3 = new Keystroke(CTRLSHIFT,"W");
				setKeyMap(DEFAULT,CLOSE_ALL_ACTION,CLOSE_ALL_ACTION_DESC,keystroke3);
			}
			
			if (mappings.get(SAVE_ACTION) == null)
			{
				Keystroke keystroke4 = new Keystroke(CTRL,"S");
				setKeyMap(DEFAULT,SAVE_ACTION,SAVE_ACTION_DESC,keystroke4);
			}
			
			if (mappings.get(SAVE_ALL_ACTION) == null)
			{
				Keystroke keystroke5 = new Keystroke(CTRLSHIFT,"S");
				setKeyMap(DEFAULT,SAVE_ALL_ACTION,SAVE_ALL_ACTION_DESC,keystroke5);
			}
			
			if (mappings.get(UNDO_ACTION) == null)
			{
				Keystroke keystroke6 = new Keystroke(CTRL,"Z");
				setKeyMap(DEFAULT,UNDO_ACTION,UNDO_ACTION_DESC,keystroke6);
			}
			
			if (mappings.get(SELECT_ALL_ACTION) == null)
			{
				Keystroke keystroke7 = new Keystroke(CTRL,"A");
				setKeyMap(DEFAULT,SELECT_ALL_ACTION,SELECT_ALL_ACTION_DESC,keystroke7);
			}
			
			if (mappings.get(SAVE_AS_ACTION) == null)
			{
				Keystroke keystrokeSaveAs = new Keystroke(null,null);
				setKeyMap(DEFAULT,SAVE_AS_ACTION,SAVE_AS_ACTION_DESC,keystrokeSaveAs);
			}
			
			if (mappings.get(SELECT_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke8 = new Keystroke(ALT,"1");
				setKeyMap(DEFAULT,SELECT_DOCUMENT_ACTION,SELECT_DOCUMENT_ACTION_DESC,keystroke8);
			}
		
			if (mappings.get(PRINT_ACTION) == null)
			{
				Keystroke keystroke9 = new Keystroke(CTRL,"P");
				setKeyMap(DEFAULT,PRINT_ACTION,PRINT_ACTION_DESC,keystroke9);
			}
			
			if (mappings.get(FIND_ACTION) == null)
			{
				Keystroke keystroke10 = new Keystroke(CTRL,"F");
				setKeyMap(DEFAULT,FIND_ACTION,FIND_ACTION_DESC,keystroke10);
			}
			
			if (mappings.get(FIND_NEXT_ACTION) == null)
			{
				Keystroke keystroke11 = new Keystroke(null,"F3");
				setKeyMap(DEFAULT,FIND_NEXT_ACTION,FIND_NEXT_ACTION_DESC,keystroke11);
			}
			
			if (mappings.get(REPLACE_ACTION) == null)
			{	
				Keystroke keystroke12 = new Keystroke(CTRL,"H");
				setKeyMap(DEFAULT,REPLACE_ACTION,REPLACE_ACTION_DESC,keystroke12);
			}
			
			if (mappings.get(CUT_ACTION) == null)
			{
				Keystroke keystroke13 = new Keystroke(CTRL,"X");
				setKeyMap(DEFAULT,CUT_ACTION,CUT_ACTION_DESC,keystroke13);
			}
			
			if (mappings.get(COPY_ACTION) == null)
			{
				Keystroke keystroke14 = new Keystroke(CTRL,"C");
				setKeyMap(DEFAULT,COPY_ACTION,COPY_ACTION_DESC,keystroke14);
			}
			
			if (mappings.get(PASTE_ACTION) == null)
			{
				Keystroke keystroke15 = new Keystroke(CTRL,"V");
				setKeyMap(DEFAULT,PASTE_ACTION,PASTE_ACTION_DESC,keystroke15);
			}
			
			if (mappings.get(COMMENT_ACTION) == null)
			{
				Keystroke keystroke16 = new Keystroke(CTRL,"K");
				setKeyMap(DEFAULT,COMMENT_ACTION,COMMENT_ACTION_DESC,keystroke16);
			}
			
			if (mappings.get(TAB_ACTION) == null)
			{
				Keystroke keystroke17 = new Keystroke(null,"TAB");
				setKeyMap(DEFAULT,TAB_ACTION,TAB_ACTION_DESC,keystroke17);
			}
			
			if (mappings.get(GOTO_ACTION) == null)
			{
				Keystroke keystroke18 = new Keystroke(CTRL,"G");
				setKeyMap(DEFAULT,GOTO_ACTION,GOTO_ACTION_DESC,keystroke18);
			}
			
			if (mappings.get(UNINDENT_ACTION) == null)
			{
				Keystroke keystroke19 = new Keystroke(SHIFT,"TAB");
				setKeyMap(DEFAULT,UNINDENT_ACTION,UNINDENT_ACTION_DESC,keystroke19);
			}
			
			if (mappings.get(UP_ACTION) == null)
			{
				Keystroke keystroke20 = new Keystroke(null,"UP");
				setKeyMap(DEFAULT,UP_ACTION,UP_ACTION_DESC,keystroke20);
			}
			
			if (mappings.get(DOWN_ACTION) == null)
			{
				Keystroke keystroke21 = new Keystroke(null,"DOWN");
				setKeyMap(DEFAULT,DOWN_ACTION,DOWN_ACTION_DESC,keystroke21);
			}
			
			if (mappings.get(RIGHT_ACTION) == null)
			{
				Keystroke keystroke22 = new Keystroke(null,"RIGHT");
				setKeyMap(DEFAULT,RIGHT_ACTION,RIGHT_ACTION_DESC,keystroke22);
			}
			
			if (mappings.get(LEFT_ACTION) == null)
			{
				Keystroke keystroke23 = new Keystroke(null,"LEFT");
				setKeyMap(DEFAULT,LEFT_ACTION,LEFT_ACTION_DESC,keystroke23);
			}
			
			if (mappings.get(PAGE_UP_ACTION) == null)
			{
				Keystroke keystroke24 = new Keystroke(null,"PAGEUP");
				setKeyMap(DEFAULT,PAGE_UP_ACTION,PAGE_UP_ACTION_DESC,keystroke24);
			}
			
			if (mappings.get(PAGE_DOWN_ACTION) == null)
			{
				Keystroke keystroke25 = new Keystroke(null,"PAGEDOWN");
				setKeyMap(DEFAULT,PAGE_DOWN_ACTION,PAGE_DOWN_ACTION_DESC,keystroke25);
			}
			
			if (mappings.get(BEGIN_LINE_ACTION) == null)
			{
				Keystroke keystroke26 = new Keystroke(null,"HOME");
				setKeyMap(DEFAULT,BEGIN_LINE_ACTION,BEGIN_LINE_ACTION_DESC,keystroke26);
			}
			
			if (mappings.get(END_LINE_ACTION) == null)
			{
				Keystroke keystroke27 = new Keystroke(null,"END");
				setKeyMap(DEFAULT,END_LINE_ACTION,END_LINE_ACTION_DESC,keystroke27);
			}
			
			if (mappings.get(BEGIN_ACTION) == null)
			{
				Keystroke keystroke28 = new Keystroke(CTRL,"HOME");
				setKeyMap(DEFAULT,BEGIN_ACTION,BEGIN_ACTION_DESC,keystroke28);
			}
			
			if (mappings.get(END_ACTION) == null)
			{
				Keystroke keystroke29 = new Keystroke(CTRL,"END");
				setKeyMap(DEFAULT,END_ACTION,END_ACTION_DESC,keystroke29);
			}
			
			if (mappings.get(NEXT_WORD_ACTION) == null)
			{
				Keystroke keystroke30 = new Keystroke(CTRL,"RIGHT");
				setKeyMap(DEFAULT,NEXT_WORD_ACTION,NEXT_WORD_ACTION_DESC,keystroke30);
			}
			
			if (mappings.get(PREVIOUS_WORD_ACTION) == null)
			{
				Keystroke keystroke31 = new Keystroke(CTRL,"LEFT");
				setKeyMap(DEFAULT,PREVIOUS_WORD_ACTION,PREVIOUS_WORD_ACTION_DESC,keystroke31);
			}
			
			if (mappings.get(DELETE_NEXT_CHAR_ACTION) == null)
			{
				Keystroke keystroke32 = new Keystroke(null,"DELETE");
				setKeyMap(DEFAULT,DELETE_NEXT_CHAR_ACTION,DELETE_NEXT_CHAR_ACTION_DESC,keystroke32);
			}
			
			if (mappings.get(DELETE_PREV_CHAR_ACTION) == null)
			{
				Keystroke keystroke33 = new Keystroke(null,"BACKSPACE");
				setKeyMap(DEFAULT,DELETE_PREV_CHAR_ACTION,DELETE_PREV_CHAR_ACTION_DESC,keystroke33);
			}
			
			if (mappings.get(WELL_FORMEDNESS_ACTION) == null)
			{
				Keystroke keystroke34 = new Keystroke(null,"F5");
				setKeyMap(DEFAULT,WELL_FORMEDNESS_ACTION,WELL_FORMEDNESS_ACTION_DESC,keystroke34);
			}
			
			if (mappings.get(VALIDATE_ACTION) == null)
			{
				Keystroke keystroke35 = new Keystroke(null,"F7");
				setKeyMap(DEFAULT,VALIDATE_ACTION,VALIDATE_ACTION_DESC,keystroke35);
			}
			
			if (mappings.get(START_BROWSER_ACTION) == null)
			{
				Keystroke keystroke36 = new Keystroke(null,"F9");
				setKeyMap(DEFAULT,START_BROWSER_ACTION,START_BROWSER_ACTION_DESC,keystroke36);
			}
			
			if (mappings.get(NEW_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke37 = new Keystroke(CTRL,"N");
				setKeyMap(DEFAULT,NEW_DOCUMENT_ACTION,NEW_DOCUMENT_ACTION_DESC,keystroke37);
			}
			
			if (mappings.get(REDO_ACTION) == null)
			{
				Keystroke keystroke38 = new Keystroke(CTRL,"Y");
				setKeyMap(DEFAULT,REDO_ACTION,REDO_ACTION_DESC,keystroke38);
			}
			
			if (mappings.get(SELECT_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke39 = new Keystroke(CTRL,"E");
				setKeyMap(DEFAULT,SELECT_ELEMENT_ACTION,SELECT_ELEMENT_ACTION_DESC,keystroke39);
			}
			
			if (mappings.get(SELECT_ELEMENT_CONTENT_ACTION) == null)
			{
				Keystroke keystroke40 = new Keystroke(CTRLSHIFT,"E");
				setKeyMap(DEFAULT,SELECT_ELEMENT_CONTENT_ACTION,SELECT_ELEMENT_CONTENT_ACTION_DESC,keystroke40);
			}
			
			if (mappings.get(INSERT_SPECIAL_CHAR_ACTION) == null)
			{
				Keystroke keystroke41 = new Keystroke(CTRL,"I");
				setKeyMap(DEFAULT,INSERT_SPECIAL_CHAR_ACTION,INSERT_SPECIAL_CHAR_ACTION_DESC,keystroke41);
			}
			
			if (mappings.get(TAG_ACTION) == null)
			{
				Keystroke keystroke42 = new Keystroke(CTRL,"T");
				setKeyMap(DEFAULT,TAG_ACTION,TAG_ACTION_DESC,keystroke42);
			}
			
			if (mappings.get(REPEAT_TAG_ACTION) == null)
			{
				Keystroke keystroke42 = new Keystroke(CTRLSHIFT,"T");
				setKeyMap(DEFAULT,REPEAT_TAG_ACTION,REPEAT_TAG_ACTION_DESC,keystroke42);
			}

			if (mappings.get(TOGGLE_EMPTY_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CTRL,"L");
				setKeyMap(DEFAULT,TOGGLE_EMPTY_ELEMENT_ACTION,TOGGLE_EMPTY_ELEMENT_ACTION_DESC,keystroke44);
			}

			if (mappings.get(RENAME_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CTRL,"R");
				setKeyMap(DEFAULT,RENAME_ELEMENT_ACTION,RENAME_ELEMENT_ACTION_DESC,keystroke44);
			}

			if (mappings.get(GOTO_START_TAG_ACTION) == null)
			{
				Keystroke keystroke43 = new Keystroke(CTRL,"UP");
				setKeyMap(DEFAULT,GOTO_START_TAG_ACTION,GOTO_START_TAG_ACTION_DESC,keystroke43);
			}
			
			if (mappings.get(GOTO_END_TAG_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CTRL,"DOWN");
				setKeyMap(DEFAULT,GOTO_END_TAG_ACTION,GOTO_END_TAG_ACTION_DESC,keystroke44);
			}
			
			if (mappings.get(GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CTRLSHIFT,"UP");
				setKeyMap(DEFAULT,GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION,GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION_DESC,keystroke44);
			}

			if (mappings.get(GOTO_NEXT_ATTRIBUTE_VALUE_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CTRLSHIFT,"DOWN");
				setKeyMap(DEFAULT,GOTO_NEXT_ATTRIBUTE_VALUE_ACTION,GOTO_NEXT_ATTRIBUTE_VALUE_ACTION_DESC,keystroke44);
			}

			if (mappings.get(TOGGLE_BOOKMARK_ACTION) == null)
			{
				Keystroke keystroke45 = new Keystroke(CTRL,"B");
				setKeyMap(DEFAULT,TOGGLE_BOOKMARK_ACTION,TOGGLE_BOOKMARK_ACTION_DESC,keystroke45);
			}
			
			if (mappings.get(SELECT_BOOKMARK_ACTION) == null)
			{
				Keystroke keystroke46 = new Keystroke(CTRLSHIFT,"B");
				setKeyMap(DEFAULT,SELECT_BOOKMARK_ACTION,SELECT_BOOKMARK_ACTION_DESC,keystroke46);
			}
			
			if (mappings.get(SCHEMA_ACTION) == null)
			{
				Keystroke keystroke47 = new Keystroke(CTRL,"1");
				setKeyMap(DEFAULT,SCHEMA_ACTION,SCHEMA_ACTION_DESC,keystroke47);
			}
			
			if (mappings.get(OUTLINER_ACTION) == null)
			{
				Keystroke keystroke48 = new Keystroke(CTRL,"2");
				setKeyMap(DEFAULT,OUTLINER_ACTION,OUTLINER_ACTION_DESC,keystroke48);
			}
			
			if (mappings.get(EDITOR_ACTION) == null)
			{
				Keystroke keystroke49 = new Keystroke(CTRL,"3");
				setKeyMap(DEFAULT,EDITOR_ACTION,EDITOR_ACTION_DESC,keystroke49);
			}
			
			if (mappings.get(VIEWER_ACTION) == null)
			{
				Keystroke keystroke50 = new Keystroke(CTRL,"4");
				setKeyMap(DEFAULT,VIEWER_ACTION,VIEWER_ACTION_DESC,keystroke50);
			}
			
			//Keystroke keystroke51 = new Keystroke(CTRL,"5");
			//setKeyMap(DEFAULT,BROWSER_ACTION,BROWSER_ACTION_DESC,keystroke51);
			/*if (mappings.get(GRID_ACTION) == null)
			{
				Keystroke keystroke50Grid = new Keystroke(CTRL,"5");
				setKeyMap(DEFAULT,GRID_ACTION,GRID_ACTION_DESC,keystroke50Grid);
			}*/
			
			if (mappings.get(ADD_ELEMENT_OUTLINER_ACTION) == null)
			{
				Keystroke keystroke52 = new Keystroke(null,"ENTER");
				setKeyMap(DEFAULT,ADD_ELEMENT_OUTLINER_ACTION,ADD_ELEMENT_OUTLINER_ACTION_DESC,keystroke52);
			}
			
			if (mappings.get(DELETE_ELEMENT_OUTLINER_ACTION) == null)
			{
				Keystroke keystroke53 = new Keystroke(null,"DELETE");
				setKeyMap(DEFAULT,DELETE_ELEMENT_OUTLINER_ACTION,DELETE_ELEMENT_OUTLINER_ACTION_DESC,keystroke53);
			}
			
			if (mappings.get(OPEN_REMOTE_ACTION) == null)
			{
				Keystroke keystroke54 = new Keystroke(null,null);
				setKeyMap(DEFAULT,OPEN_REMOTE_ACTION,OPEN_REMOTE_ACTION_DESC,keystroke54);
			}
			
			if (mappings.get(RELOAD_ACTION) == null)
			{	
				Keystroke keystroke55 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RELOAD_ACTION,RELOAD_ACTION_DESC,keystroke55);
			}
			
			if (mappings.get(SAVE_AS_TEMPLATE_ACTION) == null)
			{
				Keystroke keystroke56 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SAVE_AS_TEMPLATE_ACTION,SAVE_AS_TEMPLATE_ACTION_DESC,keystroke56);
			}
			
			if (mappings.get(MANAGE_TEMPLATE_ACTION) == null)
			{
				Keystroke keystroke57 = new Keystroke(null,null);
				setKeyMap(DEFAULT,MANAGE_TEMPLATE_ACTION,MANAGE_TEMPLATE_ACTION_DESC,keystroke57);
			}
			
			if (mappings.get(PAGE_SETUP_ACTION) == null)
			{
				Keystroke keystroke58 = new Keystroke(null,null);
				setKeyMap(DEFAULT,PAGE_SETUP_ACTION,PAGE_SETUP_ACTION_DESC,keystroke58);
			}
			
			if (mappings.get(PREFERENCES_ACTION) == null)
			{
				Keystroke keystroke59 = new Keystroke(null,null);
				setKeyMap(DEFAULT,PREFERENCES_ACTION,PREFERENCES_ACTION_DESC,keystroke59);
			}
			
			if (mappings.get(CREATE_REQUIRED_NODE_ACTION) == null)
			{
				Keystroke keystroke60 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CREATE_REQUIRED_NODE_ACTION,CREATE_REQUIRED_NODE_ACTION_DESC,keystroke60);
			}
			
			if (mappings.get(SPLIT_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke61 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SPLIT_ELEMENT_ACTION,SPLIT_ELEMENT_ACTION_DESC,keystroke61);
			}
			
			if (mappings.get(CONVERT_ENTITIES_ACTION) == null)
			{
				Keystroke keystroke62 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_ENTITIES_ACTION,CONVERT_ENTITIES_ACTION_DESC,keystroke62);
			}
			
			if (mappings.get(CONVERT_CHARACTERS_ACTION) == null)
			{
				Keystroke keystroke63 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_CHARACTERS_ACTION,CONVERT_CHARACTERS_ACTION_DESC,keystroke63);
			}
			
			if (mappings.get(STRIP_TAG_ACTION) == null)
			{
				Keystroke keystroke64 = new Keystroke(null,null);
				setKeyMap(DEFAULT,STRIP_TAG_ACTION,STRIP_TAG_ACTION_DESC,keystroke64);
			}
			
			if (mappings.get(ADD_CDATA_ACTION) == null)
			{
				Keystroke keystroke65 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_CDATA_ACTION,ADD_CDATA_ACTION_DESC,keystroke65);
			}
			
			if (mappings.get(LOCK_ACTION) == null)
			{
				Keystroke keystroke66 = new Keystroke(null,null);
				setKeyMap(DEFAULT,LOCK_ACTION,LOCK_ACTION_DESC,keystroke66);
			}
			
			if (mappings.get(FORMAT_ACTION) == null)
			{
				Keystroke keystroke67 = new Keystroke(null,null);
				setKeyMap(DEFAULT,FORMAT_ACTION,FORMAT_ACTION_DESC,keystroke67);
			}
			
			if (mappings.get(EXPAND_ALL_ACTION) == null)
			{
				Keystroke keystroke68 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXPAND_ALL_ACTION,EXPAND_ALL_ACTION_DESC,keystroke68);
			}
			
			if (mappings.get(COLLAPSE_ALL_ACTION) == null)
			{
				Keystroke keystroke69 = new Keystroke(null,null);
				setKeyMap(DEFAULT,COLLAPSE_ALL_ACTION,COLLAPSE_ALL_ACTION_DESC,keystroke69);
			}
			
			if (mappings.get(SYNCHRONISE_ACTION) == null)
			{
				Keystroke keystroke70 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SYNCHRONISE_ACTION,SYNCHRONISE_ACTION_DESC,keystroke70);
			}
			
			if (mappings.get(TOGGLE_FULL_ACTION) == null)
			{
				Keystroke keystroke71 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOGGLE_FULL_ACTION,TOGGLE_FULL_ACTION_DESC,keystroke71);
			}
			
			if (mappings.get(NEW_PROJECT_ACTION) == null)
			{
				Keystroke keystroke72 = new Keystroke(null,null);
				setKeyMap(DEFAULT,NEW_PROJECT_ACTION,NEW_PROJECT_ACTION_DESC,keystroke72);
			}
			
			if (mappings.get(IMPORT_PROJECT_ACTION) == null)
			{
				Keystroke keystroke73 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_PROJECT_ACTION,IMPORT_PROJECT_ACTION_DESC,keystroke73);
			}
			
			if (mappings.get(DELETE_PROJECT_ACTION) == null)
			{
				Keystroke keystroke74 = new Keystroke(null,null);
				setKeyMap(DEFAULT,DELETE_PROJECT_ACTION,DELETE_PROJECT_ACTION_DESC,keystroke74);
			}
			
			if (mappings.get(RENAME_PROJECT_ACTION) == null)
			{
				Keystroke keystroke75 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RENAME_PROJECT_ACTION,RENAME_PROJECT_ACTION_DESC,keystroke75);
			}
		
			if (mappings.get(CHECK_WELLFORMEDNESS_ACTION) == null)
			{
				Keystroke keystroke76 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CHECK_WELLFORMEDNESS_ACTION,CHECK_WELLFORMEDNESS_ACTION_DESC,keystroke76);
			}
			
			if (mappings.get(VALIDATE_PROJECT_ACTION) == null)
			{
				Keystroke keystroke77 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_PROJECT_ACTION,VALIDATE_PROJECT_ACTION_DESC,keystroke77);
			}
			
			if (mappings.get(FIND_IN_FILES_ACTION) == null)
			{
				Keystroke keystroke78 = new Keystroke(null,null);
				setKeyMap(DEFAULT,FIND_IN_FILES_ACTION,FIND_IN_FILES_ACTION_DESC,keystroke78);
			}
			
			if (mappings.get(FIND_IN_PROJECTS_ACTION) == null)
			{
				Keystroke keystroke78 = new Keystroke(null,null);
				setKeyMap(DEFAULT,FIND_IN_PROJECTS_ACTION,FIND_IN_PROJECTS_ACTION_DESC,keystroke78);
			}
			
			if (mappings.get(ADD_FILE_ACTION) == null)
			{
				Keystroke keystroke79 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_FILE_ACTION,ADD_FILE_ACTION_DESC,keystroke79);
			}
			if (mappings.get(ADD_REMOTE_FILE_ACTION) == null)
			{
				Keystroke keystroke80 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_REMOTE_FILE_ACTION,ADD_REMOTE_FILE_ACTION_DESC,keystroke80);
			}
			
			if (mappings.get(REMOVE_FILE_ACTION) == null)
			{
				Keystroke keystroke81 = new Keystroke(null,null);
				setKeyMap(DEFAULT,REMOVE_FILE_ACTION,REMOVE_FILE_ACTION_DESC,keystroke81);
			}
			
			if (mappings.get(ADD_DIRECTORY_ACTION) == null)
			{
				Keystroke keystroke82 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_DIRECTORY_ACTION,ADD_DIRECTORY_ACTION_DESC,keystroke82);
			}
			
			if (mappings.get(ADD_DIRECTORY_CONTENTS_ACTION) == null)
			{
				Keystroke keystroke83 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_DIRECTORY_CONTENTS_ACTION,ADD_DIRECTORY_CONTENTS_ACTION_DESC,keystroke83);
			}
			
			if (mappings.get(ADD_FOLDER_ACTION) == null)
			{
				Keystroke keystroke84 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_FOLDER_ACTION,ADD_FOLDER_ACTION_DESC,keystroke84);
			}
			
			if (mappings.get(REMOVE_FOLDER_ACTION) == null)
			{
				Keystroke keystroke85 = new Keystroke(null,null);
				setKeyMap(DEFAULT,REMOVE_FOLDER_ACTION,REMOVE_FOLDER_ACTION_DESC,keystroke85);
			}
			
			if (mappings.get(RENAME_FOLDER_ACTION) == null)
			{
				Keystroke keystroke86 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RENAME_FOLDER_ACTION,RENAME_FOLDER_ACTION_DESC,keystroke86);
			}
			
			if (mappings.get(VALIDATE_XML_SCHEMA_ACTION) == null)
			{
				Keystroke keystroke87 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_XML_SCHEMA_ACTION,VALIDATE_XML_SCHEMA_ACTION_DESC,keystroke87);
			}
			
			if (mappings.get(VALIDATE_DTD_ACTION) == null)
			{
				Keystroke keystroke88 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_DTD_ACTION,VALIDATE_DTD_ACTION_DESC,keystroke88);
			}
			
			if (mappings.get(VALIDATE_RELAXNG_ACTION) == null)
			{
				Keystroke keystroke89 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_RELAXNG_ACTION,VALIDATED_RELAXNG_ACTION_DESC,keystroke89);
			}
			
			if (mappings.get(SET_XML_DECLARATION_ACTION) == null)
			{
				Keystroke keystroke90 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_XML_DECLARATION_ACTION,SET_XML_DECLARATION_ACTION_DESC,keystroke90);
			}
			
			if (mappings.get(SET_DOCTYPE_DECLARATION_ACTION) == null)
			{
				Keystroke keystroke91 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_DOCTYPE_DECLARATION_ACTION,SET_DOCTYPE_DECLARATION_ACTION_DESC,keystroke91);
			}
			
			if (mappings.get(SET_SCHEMA_LOCATION_ACTION) == null)
			{
				Keystroke keystroke92 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_SCHEMA_LOCATION_ACTION,SET_SCHEMA_LOCATION_ACTION_DESC,keystroke92);
			}
			
			if (mappings.get(RESOLVE_XINCLUDES_ACTION) == null)
			{
				Keystroke keystroke93 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RESOLVE_XINCLUDES_ACTION,RESOLVE_XINCLUDES_ACTION_DESC,keystroke93);
			}
			
			if (mappings.get(SET_SCHEMA_PROPS_ACTION) == null)
			{
				Keystroke keystroke94 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_SCHEMA_PROPS_ACTION,SET_SCHEMA_PROPS_ACTION_DESC,keystroke94);
			}
			
			if (mappings.get(INFER_SCHEMA_ACTION) == null)
			{
				Keystroke keystroke95 = new Keystroke(null,null);
				setKeyMap(DEFAULT,INFER_SCHEMA_ACTION,INFER_SCHEMA_ACTION_DESC,keystroke95);
			}
			
			if (mappings.get(CREATE_TYPE_ACTION) == null)
			{
				Keystroke keystroke96 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CREATE_TYPE_ACTION,CREATE_TYPE_ACTION_DESC,keystroke96);
			}
			
			if (mappings.get(SET_TYPE_ACTION) == null)
			{
				Keystroke keystroke97 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_TYPE_ACTION,SET_TYPE_ACTION_DESC,keystroke97);
			}
			
			if (mappings.get(TYPE_PROPERTIES_ACTION) == null)
			{
				Keystroke keystroke98 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TYPE_PROPERTIES_ACTION,TYPE_PROPERTIES_ACTION_DESC,keystroke98);
			}
			
			if (mappings.get(MANAGE_TYPES_ACTION) == null)
			{
				Keystroke keystroke99 = new Keystroke(null,null);
				setKeyMap(DEFAULT,MANAGE_TYPES_ACTION,MANAGE_TYPES_ACTION_DESC,keystroke99);
			}
			
			if (mappings.get(CONVERT_SCHEMA_ACTION) == null)
			{
				Keystroke keystroke100 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_SCHEMA_ACTION,CONVERT_SCHEMA_ACTION_DESC,keystroke100);
			}

			if (mappings.get(EXECUTE_SIMPLE_XSLT_ACTION) == null)
			{
				Keystroke keystroke201 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_SIMPLE_XSLT_ACTION,EXECUTE_SIMPLE_XSLT_ACTION_DESC,keystroke201);	
			}
			
			if (mappings.get(EXECUTE_ADVANCED_XSLT_ACTION) == null)
			{
				Keystroke keystroke101 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_ADVANCED_XSLT_ACTION,EXECUTE_ADVANCED_XSLT_ACTION_DESC,keystroke101);	
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_XSLT_ACTION) == null)
			{
				Keystroke keystroke101 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_XSLT_ACTION,EXECUTE_PREVIOUS_XSLT_ACTION_DESC,keystroke101);	
			}

			if (mappings.get(EXECUTE_FO_ACTION) == null)
			{
				Keystroke keystroke102 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_FO_ACTION,EXECUTE_FO_ACTION_DESC,keystroke102);
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_FO_ACTION) == null)
			{
				Keystroke keystroke102 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_FO_ACTION,EXECUTE_PREVIOUS_FO_ACTION_DESC,keystroke102);
			}

			if (mappings.get(EXECUTE_XQUERY_ACTION) == null)
			{
				Keystroke keystroke103 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_XQUERY_ACTION,EXECUTE_XQUERY_ACTION_DESC,keystroke103);
			}
			
			
			
			if (mappings.get(EXECUTE_PREVIOUS_XQUERY_ACTION) == null)
			{
				Keystroke keystroke103 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_XQUERY_ACTION,EXECUTE_PREVIOUS_XQUERY_ACTION_DESC,keystroke103);
			}

			if (mappings.get(EXECUTE_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke104 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_SCENARIO_ACTION,EXECUTE_SCENARIO_ACTION_DESC,keystroke104);
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke104 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_SCENARIO_ACTION,EXECUTE_PREVIOUS_SCENARIO_ACTION_DESC,keystroke104);
			}

			if (mappings.get(XSLT_DEBUGGER_ACTION) == null)
			{
				Keystroke keystroke105 = new Keystroke(null,null);
				setKeyMap(DEFAULT,XSLT_DEBUGGER_ACTION,XSLT_DEBUGGER_ACTION_DESC,keystroke105);
			}
			
			if (mappings.get(MANAGE_SCENARIOS_ACTION) == null)
			{
				Keystroke keystroke106 = new Keystroke(null,null);
				setKeyMap(DEFAULT,MANAGE_SCENARIOS_ACTION,MANAGE_SCENARIOS_ACTION_DESC,keystroke106);
			}
			
			if (mappings.get(CANONICALIZE_ACTION) == null)
			{
				Keystroke keystroke107 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CANONICALIZE_ACTION,CANONICALIZE_ACTION_DESC,keystroke107);
			}
			
			if (mappings.get(SIGN_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke108 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SIGN_DOCUMENT_ACTION,SIGN_DOCUMENT_ACTION_DESC,keystroke108);
			}
			
			if (mappings.get(VERIFY_SIGNATURE_ACTION) == null)
			{
				Keystroke keystroke109 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VERIFY_SIGNATURE_ACTION,VERIFY_SIGNATURE_ACTION_DESC,keystroke109);
			}
			
			if (mappings.get(SHOW_SVG_ACTION) == null)
			{
				Keystroke keystroke110 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SHOW_SVG_ACTION,SHOW_SVG_ACTION_DESC,keystroke110);
			}
			
			if (mappings.get(CONVERT_SVG_ACTION) == null)
			{
				Keystroke keystroke111 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_SVG_ACTION,CONVERT_SVG_ACTION_DESC,keystroke111);
			}
			
			if (mappings.get(SEND_SOAP_MESSAGE_ACTION) == null)
			{
				Keystroke keystroke112 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SEND_SOAP_MESSAGE_ACTION,SEND_SOAP_MESSAGE_ACTION_DESC,keystroke112);
			}
			
			if (mappings.get(ANALYSE_WSDL_ACTION) == null)
			{
				Keystroke keystroke113 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ANALYSE_WSDL_ACTION,ANALYSE_WSDL_ACTION_DESC,keystroke113);
			}
			
			if (mappings.get(CLEAN_UP_HTML_ACTION) == null)
			{
				Keystroke keystroke114 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CLEAN_UP_HTML_ACTION,CLEAN_UP_HTML_ACTION_DESC,keystroke114);
			}
			if (mappings.get(IMPORT_FROM_TEXT_ACTION) == null)
			{
				Keystroke keystroke115 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_TEXT_ACTION,IMPORT_FROM_TEXT_ACTION_DESC,keystroke115);
			}
			if (mappings.get(IMPORT_FROM_EXCEL_ACTION) == null)
			{
				Keystroke keystroke116 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_EXCEL_ACTION,IMPORT_FROM_EXCEL_ACTION_DESC,keystroke116);
			}
			if (mappings.get(IMPORT_FROM_DBTABLE_ACTION) == null)
			{
				Keystroke keystroke117 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_DBTABLE_ACTION,IMPORT_FROM_DBTABLE_ACTION_DESC,keystroke117);
			}

			if (mappings.get(SELECT_FRAGMENT_ACTION) == null)
			{
				Keystroke keystroke118 = new Keystroke(CTRLSHIFT,"SPACE");
				setKeyMap(DEFAULT,SELECT_FRAGMENT_ACTION,SELECT_FRAGMENT_ACTION_DESC,keystroke118);
			}
			
			if (mappings.get(TOOLS_EMPTY_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke119 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_EMPTY_DOCUMENT_ACTION,TOOLS_EMPTY_DOCUMENT_ACTION_DESC,keystroke119);
			}
			
			if (mappings.get(TOOLS_CAPITALIZE_ACTION) == null)
			{
				Keystroke keystroke120 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_CAPITALIZE_ACTION,TOOLS_CAPITALIZE_ACTION_DESC,keystroke120);
			}
			
			if (mappings.get(TOOLS_DECAPITALIZE_ACTION) == null)
			{
				Keystroke keystroke121 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_DECAPITALIZE_ACTION,TOOLS_DECAPITALIZE_ACTION_DESC,keystroke121);
			}
			
			if (mappings.get(TOOLS_LOWERCASE_ACTION) == null)
			{
				Keystroke keystroke122 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_LOWERCASE_ACTION,TOOLS_LOWERCASE_ACTION_DESC,keystroke122);
			}
			
			if (mappings.get(TOOLS_UPPERCASE_ACTION) == null)
			{
				Keystroke keystroke123 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_UPPERCASE_ACTION,TOOLS_UPPERCASE_ACTION_DESC,keystroke123);
			}
			
			if (mappings.get(IMPORT_FROM_SQLXML_ACTION) == null)
			{
				Keystroke keystroke124 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_SQLXML_ACTION,IMPORT_FROM_SQLXML_ACTION_DESC,keystroke124);
			}
			
			if (mappings.get(TOOLS_MOVE_NS_TO_ROOT_ACTION) == null)
			{
				Keystroke keystroke125 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_MOVE_NS_TO_ROOT_ACTION,TOOLS_MOVE_NS_TO_ROOT_ACTION_DESC,keystroke125);
			}
			
			if (mappings.get(TOOLS_MOVE_NS_TO_FIRST_USED_ACTION) == null)
			{
				Keystroke keystroke126 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_MOVE_NS_TO_FIRST_USED_ACTION,TOOLS_MOVE_NS_TO_FIRST_USED_ACTION_DESC,keystroke126);
			}
			
			if (mappings.get(TOOLS_CHANGE_NS_PREFIX_ACTION) == null)
			{
				Keystroke keystroke127 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_CHANGE_NS_PREFIX_ACTION,TOOLS_CHANGE_NS_PREFIX_ACTION_DESC,keystroke127);
			}
			
			if (mappings.get(TOOLS_RENAME_NODE_ACTION) == null)
			{
				Keystroke keystroke128 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_RENAME_NODE_ACTION,TOOLS_RENAME_NODE_ACTION_DESC,keystroke128);
			}
			
			if (mappings.get(TOOLS_REMOVE_NODE_ACTION) == null)
			{
				Keystroke keystroke129 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_REMOVE_NODE_ACTION,TOOLS_REMOVE_NODE_ACTION_DESC,keystroke129);
			}
			
			if (mappings.get(TOOLS_ADD_NODE_TO_NS_ACTION) == null)
			{
				Keystroke keystroke130 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_ADD_NODE_TO_NS_ACTION,TOOLS_ADD_NODE_TO_NS_ACTION_DESC,keystroke130);
			}
			
			if (mappings.get(TOOLS_SET_NODE_VALUE_ACTION) == null)
			{
				Keystroke keystroke131 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_SET_NODE_VALUE_ACTION,TOOLS_SET_NODE_VALUE_ACTION_DESC,keystroke131);
			}
			
			if (mappings.get(TOOLS_ADD_NODE_ACTION) == null)
			{
				Keystroke keystroke132 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_ADD_NODE_ACTION,TOOLS_ADD_NODE_ACTION_DESC,keystroke132);
			}

			if (mappings.get(TOOLS_REMOVE_UNUSED_NS_ACTION) == null)
			{
				Keystroke keystroke133 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_REMOVE_UNUSED_NS_ACTION,TOOLS_REMOVE_UNUSED_NS_ACTION_DESC,keystroke133);
			}
			
			if (mappings.get(TOOLS_CONVERT_NODE_ACTION) == null)
			{
				Keystroke keystroke134 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_CONVERT_NODE_ACTION,TOOLS_CONVERT_NODE_ACTION_DESC,keystroke134);
			}
			
			if (mappings.get(TOOLS_SORT_NODE_ACTION) == null)
			{
				Keystroke keystroke135 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_SORT_NODE_ACTION,TOOLS_SORT_NODE_ACTION_DESC,keystroke135);
			}
			
			if (mappings.get(XDIFF_ACTION) == null)
			{
				Keystroke keystroke136XDiff = new Keystroke(null,null);
				setKeyMap(DEFAULT,XDIFF_ACTION,XDIFF_ACTION_DESC,keystroke136XDiff);
			}
			
			if (mappings.get(DEBUGGER_NEW_TRANSFORMATION_ACTION) == null)
			{
				Keystroke keystroke137Debugger = new Keystroke(CTRL,"N");
				setKeyMap(DEFAULT,DEBUGGER_NEW_TRANSFORMATION_ACTION,DEBUGGER_NEW_TRANSFORMATION_ACTION_DESC,keystroke137Debugger);
			}
			
			if (mappings.get(DEBUGGER_CLOSE_ACTION) == null)
			{
				Keystroke keystroke138Close = new Keystroke(CTRL,"W");
				setKeyMap(DEFAULT,DEBUGGER_CLOSE_ACTION,DEBUGGER_CLOSE_ACTION_DESC,keystroke138Close);
			}
			
			if (mappings.get(DEBUGGER_OPEN_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke139OpenScenario = new Keystroke(CTRL,"O");
				setKeyMap(DEFAULT,DEBUGGER_OPEN_SCENARIO_ACTION,DEBUGGER_OPEN_SCENARIO_ACTION_DESC,keystroke139OpenScenario);
			}
			
			if (mappings.get(DEBUGGER_CLOSE_TRANSFORMATION_ACTION) == null)
			{
				Keystroke keystroke140CloseTrans = new Keystroke(CTRLSHIFT,"W");
				setKeyMap(DEFAULT,DEBUGGER_CLOSE_TRANSFORMATION_ACTION,DEBUGGER_CLOSE_TRANSFORMATION_ACTION_DESC,keystroke140CloseTrans);
			}
			
			if (mappings.get(DEBUGGER_SAVE_AS_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke141SaveScenario = new Keystroke(CTRL,"S");
				setKeyMap(DEFAULT,DEBUGGER_SAVE_AS_SCENARIO_ACTION,DEBUGGER_SAVE_AS_SCENARIO_ACTION_DESC,keystroke141SaveScenario);
			}
			
			if (mappings.get(DEBUGGER_FIND_ACTION) == null)
			{
				Keystroke keystroke142DebuggerFind = new Keystroke(CTRL,"F");
				setKeyMap(DEFAULT,DEBUGGER_FIND_ACTION,DEBUGGER_FIND_ACTION_DESC,keystroke142DebuggerFind);
			}
			
			if (mappings.get(DEBUGGER_FIND_NEXT_ACTION) == null)
			{
				Keystroke keystroke143DebuggerFindNext = new Keystroke(null,"F3");
				setKeyMap(DEFAULT,DEBUGGER_FIND_NEXT_ACTION,DEBUGGER_FIND_NEXT_ACTION_DESC,keystroke143DebuggerFindNext );
			}
			
			if (mappings.get(DEBUGGER_GOTO_ACTION) == null)
			{
				Keystroke keystroke144DebuggerGoto = new Keystroke(CTRL,"G");
				setKeyMap(DEFAULT,DEBUGGER_GOTO_ACTION,DEBUGGER_GOTO_ACTION_DESC,keystroke144DebuggerGoto);
			}
			
			if (mappings.get(DEBUGGER_START_ACTION) == null)
			{
				Keystroke keystroke145DebugStart = new Keystroke(null,"F5");
				setKeyMap(DEFAULT,DEBUGGER_START_ACTION,DEBUGGER_START_ACTION_DESC,keystroke145DebugStart);
			}
			
			if (mappings.get(DEBUGGER_RUN_END_ACTION) == null)
			{
				Keystroke keystroke146DebugRunEnd = new Keystroke(CTRLSHIFT,"F5");
				setKeyMap(DEFAULT,DEBUGGER_RUN_END_ACTION,DEBUGGER_RUN_END_ACTION_DESC,keystroke146DebugRunEnd);
			}
			
			if (mappings.get(DEBUGGER_PAUSE_ACTION) == null)
			{
				Keystroke keystroke147DebugPause = new Keystroke(CTRL,"F5");
				setKeyMap(DEFAULT,DEBUGGER_PAUSE_ACTION,DEBUGGER_PAUSE_ACTION_DESC,keystroke147DebugPause);
			}
			
			if (mappings.get(DEBUGGER_STOP_ACTION) == null)
			{
				Keystroke keystroke148DebugStop = new Keystroke(SHIFT,"F5");
				setKeyMap(DEFAULT,DEBUGGER_STOP_ACTION,DEBUGGER_STOP_ACTION_DESC,keystroke148DebugStop);
			}
			
			if (mappings.get(DEBUGGER_STEP_INTO_ACTION) == null)
			{
				Keystroke keystroke149DebugStepInto = new Keystroke(null,"F11");
				setKeyMap(DEFAULT,DEBUGGER_STEP_INTO_ACTION,DEBUGGER_STEP_INTO_ACTION_DESC, keystroke149DebugStepInto);
			}
			
			if (mappings.get(DEBUGGER_STEP_OVER_ACTION) == null)
			{
				Keystroke keystroke150DebugStepOver = new Keystroke(null,"F10");
				setKeyMap(DEFAULT,DEBUGGER_STEP_OVER_ACTION,DEBUGGER_STEP_OVER_ACTION_DESC, keystroke150DebugStepOver);
			}
			
			if (mappings.get(DEBUGGER_STEP_OUT_ACTION) == null)
			{
				Keystroke keystroke151DebugStepOut = new Keystroke(SHIFT,"F11");
				setKeyMap(DEFAULT,DEBUGGER_STEP_OUT_ACTION,DEBUGGER_STEP_OUT_ACTION_DESC, keystroke151DebugStepOut);
			}
			
			if (mappings.get(DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION) == null)
			{
				Keystroke keystroke152DebugRemoveAllBreakpoints = new Keystroke(CTRLSHIFT,"F9");
				setKeyMap(DEFAULT,DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION,DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION_DESC, keystroke152DebugRemoveAllBreakpoints);
			}
			
			if (mappings.get(DEBUGGER_OPEN_INPUT_ACTION) == null)
			{
				Keystroke keystroke153OpenInput = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_OPEN_INPUT_ACTION,DEBUGGER_OPEN_INPUT_ACTION_DESC,keystroke153OpenInput);
			}
			
			if (mappings.get(DEBUGGER_OPEN_STYLESHEET_ACTION) == null)
			{
				Keystroke keystroke154OpenStylesheet = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_OPEN_STYLESHEET_ACTION,DEBUGGER_OPEN_STYLESHEET_ACTION_DESC,keystroke154OpenStylesheet);
			}
			
			if (mappings.get(DEBUGGER_RELOAD_ACTION) == null)
			{
				Keystroke keystroke155DebugReload = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_RELOAD_ACTION,DEBUGGER_RELOAD_ACTION_DESC,keystroke155DebugReload);
			}
			
			if (mappings.get(DEBUGGER_EXIT_ACTION) == null)
			{
				Keystroke keystroke156DebugExit = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_EXIT_ACTION,DEBUGGER_EXIT_ACTION_DESC,keystroke156DebugExit);
			}
			
			if (mappings.get(DEBUGGER_COLLAPSE_ALL_ACTION) == null)
			{
				Keystroke keystroke157DebugCollapseAll = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_COLLAPSE_ALL_ACTION,DEBUGGER_COLLAPSE_ALL_ACTION_DESC,keystroke157DebugCollapseAll);
			}
			
			if (mappings.get(DEBUGGER_EXPAND_ALL_ACTION) == null)
			{
				Keystroke keystroke158DebugExpandAll = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_EXPAND_ALL_ACTION,DEBUGGER_EXPAND_ALL_ACTION_DESC,keystroke158DebugExpandAll);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION) == null)
			{
				Keystroke keystroke159DebugShowLine = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION,DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION_DESC, keystroke159DebugShowLine);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION) == null)
			{
				Keystroke keystroke160DebugShowOverview = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION,DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION_DESC, keystroke160DebugShowOverview);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION) == null)
			{
				Keystroke keystroke161DebugShowFolding = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION,DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION_DESC, keystroke161DebugShowFolding);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION) == null)
			{
				Keystroke keystroke162DebugSoftWrap = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION,DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION_DESC, keystroke162DebugSoftWrap);
				
			}
			
			if (mappings.get(DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION) == null)
			{
				Keystroke keystroke163DebugInputShowLine = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION,DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION_DESC, keystroke163DebugInputShowLine);
				
			}
			
			if (mappings.get(DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION) == null)
			{
				Keystroke keystroke164DebugInputShowOverview = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION,DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION_DESC, keystroke164DebugInputShowOverview);
				
			}			
			
			if (mappings.get(DEBUGGER_INPUT_SHOW_FOLDING_ACTION) == null)
     		{
				Keystroke keystroke165DebugInputShowFolding = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SHOW_FOLDING_ACTION,DEBUGGER_INPUT_SHOW_FOLDING_ACTION, keystroke165DebugInputShowFolding);
		
     		}
			
			if (mappings.get(DEBUGGER_INPUT_SOFT_WRAPPING_ACTION) == null)
		    {
				Keystroke keystroke166DebugInputSoftWrap = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_INPUT_SOFT_WRAPPING_ACTION,DEBUGGER_INPUT_SOFT_WRAPPING_ACTION_DESC, keystroke166DebugInputSoftWrap);
			}
			
			if (mappings.get(DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION) == null)
			{
				Keystroke keystroke167DebugOutputShowLine = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION,DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION_DESC, keystroke167DebugOutputShowLine);
				
			}
			
			if (mappings.get(DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION) == null)
		    {
				Keystroke keystroke168DebugOutputSoftWrap = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION,DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION_DESC, keystroke168DebugOutputSoftWrap);
			}
			
			if (mappings.get(DEBUGGER_AUTO_OPEN_INPUT_ACTION) == null)
		    {
				Keystroke keystroke169DebugAutoOpen = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_AUTO_OPEN_INPUT_ACTION,DEBUGGER_AUTO_OPEN_INPUT_ACTION_DESC, keystroke169DebugAutoOpen);
			}
			
			if (mappings.get(DEBUGGER_ENABLE_TRACING_ACTION) == null)
		    {
				Keystroke keystroke170DebugEnableTracing = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_ENABLE_TRACING_ACTION,DEBUGGER_ENABLE_TRACING_ACTION_DESC, keystroke170DebugEnableTracing);
			}
			
			if (mappings.get(DEBUGGER_REDIRECT_OUTPUT_ACTION) == null)
		    {
				Keystroke keystroke171DebugRedirectOutput = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_REDIRECT_OUTPUT_ACTION,DEBUGGER_REDIRECT_OUTPUT_ACTION_DESC, keystroke171DebugRedirectOutput);
			}	
			
			if (mappings.get(DEBUGGER_SET_PARAMETERS_ACTION) == null)
		    {
				Keystroke keystroke172DebugSetParams = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_SET_PARAMETERS_ACTION,DEBUGGER_SET_PARAMETERS_ACTION_DESC, keystroke172DebugSetParams);
			}	
			
			if (mappings.get(DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION) == null)
		    {
				Keystroke keystroke173DebugDisableAllBreak = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION,DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION_DESC, keystroke173DebugDisableAllBreak);
			}	
			
			if (mappings.get(DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION) == null)
		    {
				Keystroke keystroke174DebugEnableAllBreak = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION,DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION_DESC, keystroke174DebugEnableAllBreak);
			}	
			
			if (mappings.get(HIGHLIGHT_ACTION) == null)
		    {
				Keystroke keystroke175Hightlight = new Keystroke(null,null);
			    setKeyMap(DEFAULT,HIGHLIGHT_ACTION,HIGHLIGHT_ACTION, keystroke175Hightlight);
			}	
			
			if (mappings.get(VIEW_STANDARD_BUTTONS_ACTION) == null)
		    {
				Keystroke keystroke176StandardButtons = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_STANDARD_BUTTONS_ACTION,VIEW_STANDARD_BUTTONS_ACTION_DESC, keystroke176StandardButtons);
			}
			
			if (mappings.get(VIEW_EDITOR_BUTTONS_ACTION) == null)
		    {
				Keystroke keystroke177EditorButtons = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_BUTTONS_ACTION,VIEW_EDITOR_BUTTONS_ACTION_DESC, keystroke177EditorButtons);
			}
			
			if (mappings.get(VIEW_FRAGMENT_BUTTONS_ACTION) == null)
		    {
				Keystroke keystroke178FragmentButtons = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_FRAGMENT_BUTTONS_ACTION,VIEW_FRAGMENT_BUTTONS_ACTION_DESC, keystroke178FragmentButtons);
			}	
			
			if (mappings.get(VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION) == null)
		    {
				Keystroke keystroke179EditorShowLineNumber = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION,VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION_DESC, keystroke179EditorShowLineNumber);
			}	
			
			if (mappings.get(VIEW_EDITOR_SHOW_OVERVIEW_ACTION) == null)
		    {
				Keystroke keystroke180EditorShowOverview = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_OVERVIEW_ACTION,VIEW_EDITOR_SHOW_OVERVIEW_ACTION_DESC, keystroke180EditorShowOverview);
			}
			
			if (mappings.get(VIEW_EDITOR_SHOW_FOLDING_ACTION) == null)
		    {
				Keystroke keystroke181EditorShowFolding = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_FOLDING_ACTION,VIEW_EDITOR_SHOW_FOLDING_ACTION_DESC, keystroke181EditorShowFolding);
			}
			
			if (mappings.get(VIEW_EDITOR_SHOW_ANNOTATION_ACTION) == null)
		    {
				Keystroke keystroke = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_ANNOTATION_ACTION,VIEW_EDITOR_SHOW_ANNOTATION_ACTION_DESC, keystroke);
			}

			if (mappings.get(VIEW_EDITOR_TAG_COMPLETION_ACTION) == null)
		    {
				Keystroke keystroke182EditorShowTagCompletion = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_TAG_COMPLETION_ACTION,VIEW_EDITOR_TAG_COMPLETION_ACTION_DESC, keystroke182EditorShowTagCompletion);
			}
			
			if (mappings.get(VIEW_EDITOR_END_TAG_COMPLETION_ACTION) == null)
		    {
				Keystroke keystroke183EditorShowEndTagCompletion = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_END_TAG_COMPLETION_ACTION,VIEW_EDITOR_END_TAG_COMPLETION_ACTION_DESC, keystroke183EditorShowEndTagCompletion);
			}
			
			if (mappings.get(VIEW_EDITOR_SMART_INDENTATION_ACTION) == null)
		    {
				Keystroke keystroke183EditorSmartIndentation = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SMART_INDENTATION_ACTION,VIEW_EDITOR_SMART_INDENTATION_ACTION_DESC, keystroke183EditorSmartIndentation);
			}
			
			if (mappings.get(VIEW_EDITOR_SOFT_WRAPPING_ACTION) == null)
		    {
				Keystroke keystroke184EditorSoftWrap = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SOFT_WRAPPING_ACTION,VIEW_EDITOR_SOFT_WRAPPING_ACTION_DESC, keystroke184EditorSoftWrap);
			}
			
			if (mappings.get(VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION) == null)
		    {
				Keystroke keystroke = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION,VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION_DESC, keystroke);
			}

			if (mappings.get(VIEWER_SHOW_NAMESPACES_ACTION) == null)
		    {
				Keystroke keystroke185ViewerShowNamespaces = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_NAMESPACES_ACTION,VIEWER_SHOW_NAMESPACES_ACTION_DESC, keystroke185ViewerShowNamespaces);
			}
			
			if (mappings.get(VIEWER_SHOW_ATTRIBUTES_ACTION) == null)
		    {
				Keystroke keystroke186ViewerShowAttr = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_ATTRIBUTES_ACTION,VIEWER_SHOW_ATTRIBUTES_ACTION_DESC, keystroke186ViewerShowAttr);
			}
			
			if (mappings.get(VIEWER_SHOW_COMMENTS_ACTION) == null)
		    {
				Keystroke keystroke187ViewerShowComments = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_COMMENTS_ACTION,VIEWER_SHOW_COMMENTS_ACTION_DESC, keystroke187ViewerShowComments);
			}
			
			if (mappings.get(VIEWER_SHOW_TEXT_CONTENT_ACTION) == null)
		    {
				Keystroke keystroke188ViewerShowTextContent = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_TEXT_CONTENT_ACTION,VIEWER_SHOW_TEXT_CONTENT_ACTION_DESC, keystroke188ViewerShowTextContent);
			}
			
			if (mappings.get(VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION) == null)
		    {
				Keystroke keystroke189ViewerShowPIs = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION,VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION_DESC, keystroke189ViewerShowPIs);
			}
			
			if (mappings.get(VIEWER_INLINE_MIXED_CONTENT_ACTION) == null)
		    {
				Keystroke keystroke190ViewerinlineMixed = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_INLINE_MIXED_CONTENT_ACTION,VIEWER_INLINE_MIXED_CONTENT_ACTION_DESC, keystroke190ViewerinlineMixed);
			}
			
			if (mappings.get(OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION) == null)
		    {
				Keystroke keystroke191OutlinerShowAttr = new Keystroke(null,null);
			    setKeyMap(DEFAULT,OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION,OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION_DESC, keystroke191OutlinerShowAttr);
			}
			
			if (mappings.get(OUTLINER_SHOW_ELEMENT_VALUES_ACTION) == null)
		    {
				Keystroke keystroke192OutlinerShowEle = new Keystroke(null,null);
			    setKeyMap(DEFAULT,OUTLINER_SHOW_ELEMENT_VALUES_ACTION,OUTLINER_SHOW_ELEMENT_VALUES_ACTION_DESC, keystroke192OutlinerShowEle);
			}
			
			if (mappings.get(OUTLINER_CREATE_REQUIRED_NODES_ACTION) == null)
		    {
				Keystroke keystroke192OutlinerShowEle = new Keystroke(null,null);
			    setKeyMap(DEFAULT,OUTLINER_CREATE_REQUIRED_NODES_ACTION,OUTLINER_CREATE_REQUIRED_NODES_ACTION_DESC, keystroke192OutlinerShowEle);
			}
			
			if (mappings.get(VIEW_SYNCHRONIZE_SPLITS_ACTION) == null)
		    {
				Keystroke keystroke193SynchroniseSplits = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_SYNCHRONIZE_SPLITS_ACTION,VIEW_SYNCHRONIZE_SPLITS_ACTION_DESC, keystroke193SynchroniseSplits);
			}
			
			if (mappings.get(VIEW_SPLIT_HORIZONTALLY_ACTION) == null)
		    {
				Keystroke keystroke194SplitHorizontally = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_SPLIT_HORIZONTALLY_ACTION,VIEW_SPLIT_HORIZONTALLY_ACTION_DESC, keystroke194SplitHorizontally);
			}
			
			if (mappings.get(VIEW_SPLIT_VERTICALLY_ACTION) == null)
		    {
				Keystroke keystroke195SplitVertically = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_SPLIT_VERTICALLY_ACTION,VIEW_SPLIT_VERTICALLY_ACTION_DESC, keystroke195SplitVertically);
			}
			
			if (mappings.get(VIEW_UNSPLIT_ACTION) == null)
		    {
				Keystroke keystroke196Unsplit = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_UNSPLIT_ACTION,VIEW_UNSPLIT_ACTION_DESC, keystroke196Unsplit);
			}
			
			/*if (mappings.get(GRID_ADD_ATTRIBUTE_COLUMN_ACTION) == null)
		    {
				Keystroke keystroke197AddAttributeColumn = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ATTRIBUTE_COLUMN_ACTION,GRID_ADD_ATTRIBUTE_COLUMN_ACTION_DESC, keystroke197AddAttributeColumn);
			}
			
			if (mappings.get(GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION) == null)
		    {
				Keystroke keystroke198AddAttributeToSelected = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION,GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION_DESC, keystroke198AddAttributeToSelected);
			}
			
			if (mappings.get(GRID_ADD_CHILD_TABLE_ACTION) == null)
		    {
				Keystroke keystroke199AddChildTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_CHILD_TABLE_ACTION,GRID_ADD_CHILD_TABLE_ACTION_DESC, keystroke199AddChildTable);
			}
			
			if (mappings.get(GRID_ADD_ELEMENT_AFTER_ACTION) == null)
		    {
				Keystroke keystroke200AddElementAfter = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ELEMENT_AFTER_ACTION,GRID_ADD_ELEMENT_AFTER_ACTION_DESC, keystroke200AddElementAfter);
			}
			
			if (mappings.get(GRID_ADD_ELEMENT_BEFORE_ACTION) == null)
		    {
				Keystroke keystroke201AddElementBefore = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ELEMENT_BEFORE_ACTION,GRID_ADD_ELEMENT_BEFORE_ACTION_DESC, keystroke201AddElementBefore);
			}
			
			if (mappings.get(GRID_ADD_TEXT_COLUMN_ACTION) == null)
		    {
				Keystroke keystroke202AddTextColumn = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_TEXT_COLUMN_ACTION,GRID_ADD_TEXT_COLUMN_ACTION_DESC, keystroke202AddTextColumn);
			}
			
			if (mappings.get(GRID_ADD_TEXT_TO_SELECTED_ACTION) == null)
		    {
				Keystroke keystroke203AddTextToSelected = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_TEXT_TO_SELECTED_ACTION,GRID_ADD_TEXT_TO_SELECTED_ACTION_DESC, keystroke203AddTextToSelected);
			}
			
			if (mappings.get(GRID_DELETE_ATTS_AND_TEXT_ACTION) == null)
		    {
				Keystroke keystroke204DeleteAttsAndText = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_ATTS_AND_TEXT_ACTION,GRID_DELETE_ATTS_AND_TEXT_ACTION_DESC, keystroke204DeleteAttsAndText);
			}
			
			if (mappings.get(GRID_DELETE_CHILD_TABLE_ACTION) == null)
		    {
				Keystroke keystroke205DeleteChildTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_CHILD_TABLE_ACTION,GRID_DELETE_CHILD_TABLE_ACTION_DESC, keystroke205DeleteChildTable);
			}
			
			if (mappings.get(GRID_DELETE_COLUMN_ACTION) == null)
		    {
				Keystroke keystroke206DeleteColumn = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_COLUMN_ACTION,GRID_DELETE_COLUMN_ACTION_DESC, keystroke206DeleteColumn);
			}
			
			if (mappings.get(GRID_DELETE_ROW_ACTION) == null)
		    {
				Keystroke keystroke207DeleteRow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_ROW_ACTION,GRID_DELETE_ROW_ACTION_DESC, keystroke207DeleteRow);
			}
			
			if (mappings.get(GRID_DELETE_SELECTED_ATTRIBUTE_ACTION) == null)
		    {
				Keystroke keystroke208DeleteSelectedAttribute = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_SELECTED_ATTRIBUTE_ACTION,GRID_DELETE_SELECTED_ATTRIBUTE_ACTION_DESC, keystroke208DeleteSelectedAttribute);
			}
			
			if (mappings.get(GRID_DELETE_SELECTED_TEXT_ACTION) == null)
		    {
				Keystroke keystroke209DeleteSelectedText = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_SELECTED_TEXT_ACTION,GRID_DELETE_SELECTED_TEXT_ACTION_DESC, keystroke209DeleteSelectedText);
			}
			
			if (mappings.get(GRID_RENAME_ATTRIBUTE_ACTION) == null)
		    {
				Keystroke keystroke210EditAttributeName = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_RENAME_ATTRIBUTE_ACTION,GRID_RENAME_ATTRIBUTE_ACTION_DESC, keystroke210EditAttributeName);
			}
			
			if (mappings.get(GRID_RENAME_SELECTED_ATTRIBUTE_ACTION) == null)
		    {
				Keystroke keystroke210RenameSelectedAttribute = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_RENAME_SELECTED_ATTRIBUTE_ACTION,GRID_RENAME_SELECTED_ATTRIBUTE_ACTION_DESC, keystroke210RenameSelectedAttribute);
			}
			
			if (mappings.get(GRID_MOVE_ROW_DOWN_ACTION) == null)
		    {
				Keystroke keystroke211MoveRowDown = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_MOVE_ROW_DOWN_ACTION,GRID_MOVE_ROW_DOWN_ACTION_DESC, keystroke211MoveRowDown);
			}
			
			if (mappings.get(GRID_MOVE_ROW_UP_ACTION) == null)
		    {
				Keystroke keystroke212MoveRowUp = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_MOVE_ROW_UP_ACTION,GRID_MOVE_ROW_UP_ACTION_DESC, keystroke212MoveRowUp);
			}
			
			if (mappings.get(GRID_SORT_TABLE_DESCENDING_ACTION) == null)
		    {
				Keystroke keystroke213SortAscending = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_SORT_TABLE_DESCENDING_ACTION,GRID_SORT_TABLE_DESCENDING_ACTION_DESC, keystroke213SortAscending);
			}
			
			if (mappings.get(GRID_SORT_TABLE_ASCENDING_ACTION) == null)
		    {
				Keystroke keystroke213SortDescending = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_SORT_TABLE_ASCENDING_ACTION,GRID_SORT_TABLE_ASCENDING_ACTION_DESC, keystroke213SortDescending);
			}
			
			if (mappings.get(GRID_UNSORT_ACTION) == null)
		    {
				Keystroke keystroke213Unsort = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_UNSORT_ACTION,GRID_UNSORT_ACTION_DESC, keystroke213Unsort);
			}
			
			if (mappings.get(GRID_GOTO_PARENT_TABLE_ACTION) == null)
		    {
				Keystroke keystroke214Collapse = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_GOTO_PARENT_TABLE_ACTION,GRID_COLLAPSE_ACTION_DESC, keystroke214Collapse);
			}
			
			if (mappings.get(GRID_GOTO_CHILD_TABLE_ACTION) == null)
		    {
				Keystroke keystroke215Expand = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_GOTO_CHILD_TABLE_ACTION,GRID_EXPAND_ACTION_DESC, keystroke215Expand);
			}
			
			if (mappings.get(GRID_COPY_SHALLOW_ACTION) == null)
		    {
				Keystroke keystroke216CopyShallow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_COPY_SHALLOW_ACTION,GRID_COPY_SHALLOW_ACTION_DESC, keystroke216CopyShallow);
			}
			
			if (mappings.get(GRID_PASTE_AFTER_ACTION) == null)
		    {
				Keystroke keystroke217PasteAfter = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_PASTE_AFTER_ACTION,GRID_PASTE_AFTER_ACTION_DESC, keystroke217PasteAfter);
			}
			
			if (mappings.get(GRID_PASTE_AS_CHILD_ACTION) == null)
		    {
				Keystroke keystroke218PasteAsChild = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_PASTE_AS_CHILD_ACTION,GRID_PASTE_AS_CHILD_ACTION_DESC, keystroke218PasteAsChild);
			}
			
			if (mappings.get(GRID_PASTE_BEFORE_ACTION) == null)
		    {
				Keystroke keystroke219PasteBefore = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_PASTE_BEFORE_ACTION,GRID_PASTE_BEFORE_ACTION_DESC, keystroke219PasteBefore);
			}
			
			if (mappings.get(GRID_COLLAPSE_ROW_ACTION) == null)
		    {
				Keystroke keystroke220CollapseRow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_COLLAPSE_ROW_ACTION,GRID_COLLAPSE_ROW_ACTION_DESC, keystroke220CollapseRow);
			}
			
			if (mappings.get(GRID_EXPAND_ROW_ACTION) == null)
		    {
				Keystroke keystroke221ExpandRow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_EXPAND_ROW_ACTION,GRID_EXPAND_ROW_ACTION_DESC, keystroke221ExpandRow);
			}
			
			if (mappings.get(GRID_COLLAPSE_CURRENT_TABLE_ACTION) == null)
		    {
				Keystroke keystroke222CollapseCurrentTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_COLLAPSE_CURRENT_TABLE_ACTION,GRID_COLLAPSE_CURRENT_TABLE_ACTION_DESC, keystroke222CollapseCurrentTable);
			}
			
			if (mappings.get(GRID_DELETE_ACTION) == null)
		    {
				Keystroke keystroke223GridDelete = new Keystroke(null,"DELETE");
			    setKeyMap(DEFAULT,GRID_DELETE_ACTION,GRID_DELETE_ACTION_DESC, keystroke223GridDelete);
			}*/
			
			if (mappings.get(EXECUTE_SCHEMATRON_ACTION) == null)
			{
				Keystroke keystroke224 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_SCHEMATRON_ACTION,EXECUTE_SCHEMATRON_ACTION_DESC,keystroke224);
			}
			
			for(int cnt=0;cnt<getPluginMappings().size();++cnt) {
				Object obj = getPluginMappings().get(cnt);
				if((obj != null) && (obj instanceof PluginActionKeyMapping)) {
					PluginActionKeyMapping keyMapping = (PluginActionKeyMapping)obj;
					
					if(mappings.get(keyMapping.getKeystroke_action_name()) == null) {
						Keystroke pluginKeystroke = new Keystroke(keyMapping.getKeystroke_mask(), keyMapping.getKeystroke_value());
						setKeyMap(DEFAULT, keyMapping.getKeystroke_action_name(), keyMapping.getKeystroke_action_description(), pluginKeystroke);
						
					}
				}
			}
		}
		
		
		else
		{
			// MAC OS
			if (mappings.get(OPEN_ACTION) == null)
			{
				Keystroke keystroke1 = new Keystroke(CMD,"O");
				setKeyMap(DEFAULT,OPEN_ACTION,OPEN_ACTION_DESC,keystroke1);
			}
		
			
			if (mappings.get(CLOSE_ACTION) == null)
			{
				Keystroke keystroke2 = new Keystroke(CMD,"W");
				setKeyMap(DEFAULT,CLOSE_ACTION,CLOSE_ACTION_DESC,keystroke2);
			}
			
			
			if (mappings.get(CLOSE_ALL_ACTION) == null)
			{
				Keystroke keystroke3 = new Keystroke(CMDSHIFT,"W");
				setKeyMap(DEFAULT,CLOSE_ALL_ACTION,CLOSE_ALL_ACTION_DESC,keystroke3);
			}
			
			
			if (mappings.get(SAVE_ACTION) == null)
			{
				Keystroke keystroke4 = new Keystroke(CMD,"S");
				setKeyMap(DEFAULT,SAVE_ACTION,SAVE_ACTION_DESC,keystroke4);
			}
			
			if (mappings.get(SAVE_ALL_ACTION) == null)
			{
				Keystroke keystroke5 = new Keystroke(CMDSHIFT,"S");
				setKeyMap(DEFAULT,SAVE_ALL_ACTION,SAVE_ALL_ACTION_DESC,keystroke5);
			}
			
			if (mappings.get(UNDO_ACTION) == null)
			{
				Keystroke keystroke6 = new Keystroke(CMD,"Z");
				setKeyMap(DEFAULT,UNDO_ACTION,UNDO_ACTION_DESC,keystroke6);
			}
			
			if (mappings.get(SELECT_ALL_ACTION) == null)
			{
				Keystroke keystroke7 = new Keystroke(CMD,"A");
				setKeyMap(DEFAULT,SELECT_ALL_ACTION,SELECT_ALL_ACTION_DESC,keystroke7);
			}
			
			if (mappings.get(SAVE_AS_ACTION) == null)
			{
				Keystroke keystrokeSaveAs = new Keystroke(null,null);
				setKeyMap(DEFAULT,SAVE_AS_ACTION,SAVE_AS_ACTION_DESC,keystrokeSaveAs);
			}
			
			
			if (mappings.get(SELECT_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke8 = new Keystroke(ALT,"1");
				setKeyMap(DEFAULT,SELECT_DOCUMENT_ACTION,SELECT_DOCUMENT_ACTION_DESC,keystroke8);
			}
		
			if (mappings.get(PRINT_ACTION) == null)
			{
				Keystroke keystroke9 = new Keystroke(CMD,"P");
				setKeyMap(DEFAULT,PRINT_ACTION,PRINT_ACTION_DESC,keystroke9);
			}
			
			if (mappings.get(FIND_ACTION) == null)
			{
				Keystroke keystroke10 = new Keystroke(CMD,"F");
				setKeyMap(DEFAULT,FIND_ACTION,FIND_ACTION_DESC,keystroke10);
			}
			
			if (mappings.get(FIND_NEXT_ACTION) == null)
				{
				Keystroke keystroke11 = new Keystroke(null,"F3");
			setKeyMap(DEFAULT,FIND_NEXT_ACTION,FIND_NEXT_ACTION_DESC,keystroke11);
			}
			
			if (mappings.get(REPLACE_ACTION) == null)
			{
				Keystroke keystroke12 = new Keystroke(CMD,"H");
				setKeyMap(DEFAULT,REPLACE_ACTION,REPLACE_ACTION_DESC,keystroke12);
			}
			
			if (mappings.get(CUT_ACTION) == null)
			{
				Keystroke keystroke13 = new Keystroke(CMD,"X");
				setKeyMap(DEFAULT,CUT_ACTION,CUT_ACTION_DESC,keystroke13);
			}
			
			if (mappings.get(COPY_ACTION) == null)
			{
				Keystroke keystroke14 = new Keystroke(CMD,"C");
				setKeyMap(DEFAULT,COPY_ACTION,COPY_ACTION_DESC,keystroke14);
			}
			
			if (mappings.get(PASTE_ACTION) == null)
			{
				Keystroke keystroke15 = new Keystroke(CMD,"V");
				setKeyMap(DEFAULT,PASTE_ACTION,PASTE_ACTION_DESC,keystroke15);
			}
			
			if (mappings.get(COMMENT_ACTION) == null)
			{
				Keystroke keystroke16 = new Keystroke(CMD,"K");
				setKeyMap(DEFAULT,COMMENT_ACTION,COMMENT_ACTION_DESC,keystroke16);
			}
			
			if (mappings.get(TAB_ACTION) == null)
			{
				Keystroke keystroke17 = new Keystroke(null,"TAB");
				setKeyMap(DEFAULT,TAB_ACTION,TAB_ACTION_DESC,keystroke17);
			}
			
			if (mappings.get(GOTO_ACTION) == null)
			{
				Keystroke keystroke18 = new Keystroke(CMD,"G");
				setKeyMap(DEFAULT,GOTO_ACTION,GOTO_ACTION_DESC,keystroke18);
			}
			
			if (mappings.get(UNINDENT_ACTION) == null)
			{
				Keystroke keystroke19 = new Keystroke(SHIFT,"TAB");
				setKeyMap(DEFAULT,UNINDENT_ACTION,UNINDENT_ACTION_DESC,keystroke19);
			}
			
			if (mappings.get(UP_ACTION) == null)
			{
				Keystroke keystroke20 = new Keystroke(null,"UP");
				setKeyMap(DEFAULT,UP_ACTION,UP_ACTION_DESC,keystroke20);
			}
			
			if (mappings.get(DOWN_ACTION) == null)
			{
				Keystroke keystroke21 = new Keystroke(null,"DOWN");
				setKeyMap(DEFAULT,DOWN_ACTION,DOWN_ACTION_DESC,keystroke21);
			}
			
			if (mappings.get(RIGHT_ACTION) == null)
			{
				Keystroke keystroke22 = new Keystroke(null,"RIGHT");
				setKeyMap(DEFAULT,RIGHT_ACTION,RIGHT_ACTION_DESC,keystroke22);
			}
			
			if (mappings.get(LEFT_ACTION) == null)
			{
				Keystroke keystroke23 = new Keystroke(null,"LEFT");
				setKeyMap(DEFAULT,LEFT_ACTION,LEFT_ACTION_DESC,keystroke23);
			}
			
			if (mappings.get(PAGE_UP_ACTION) == null)
			{
				Keystroke keystroke24 = new Keystroke(null,"PAGEUP");
				setKeyMap(DEFAULT,PAGE_UP_ACTION,PAGE_UP_ACTION_DESC,keystroke24);
			}
			
			if (mappings.get(PAGE_DOWN_ACTION) == null)
			{
				Keystroke keystroke25 = new Keystroke(null,"PAGEDOWN");
				setKeyMap(DEFAULT,PAGE_DOWN_ACTION,PAGE_DOWN_ACTION_DESC,keystroke25);
			}
			
			if (mappings.get(BEGIN_LINE_ACTION) == null)
			{
				Keystroke keystroke26 = new Keystroke(null,"HOME");
				setKeyMap(DEFAULT,BEGIN_LINE_ACTION,BEGIN_LINE_ACTION_DESC,keystroke26);
			}
			
			if (mappings.get(END_LINE_ACTION) == null)
			{
				Keystroke keystroke27 = new Keystroke(null,"END");
				setKeyMap(DEFAULT,END_LINE_ACTION,END_LINE_ACTION_DESC,keystroke27);
			}
			
			if (mappings.get(BEGIN_ACTION) == null)
			{
				Keystroke keystroke28 = new Keystroke(CMD,"HOME");
				setKeyMap(DEFAULT,BEGIN_ACTION,BEGIN_ACTION_DESC,keystroke28);
			}
			
			if (mappings.get(END_ACTION) == null)
			{
				Keystroke keystroke29 = new Keystroke(CMD,"END");
				setKeyMap(DEFAULT,END_ACTION,END_ACTION_DESC,keystroke29);
			}
			
			if (mappings.get(NEXT_WORD_ACTION) == null)
			{
				Keystroke keystroke30 = new Keystroke(CMD,"RIGHT");
				setKeyMap(DEFAULT,NEXT_WORD_ACTION,NEXT_WORD_ACTION_DESC,keystroke30);
			}
			
			if (mappings.get(PREVIOUS_WORD_ACTION) == null)
			{
				Keystroke keystroke31 = new Keystroke(CMD,"LEFT");
				setKeyMap(DEFAULT,PREVIOUS_WORD_ACTION,PREVIOUS_WORD_ACTION_DESC,keystroke31);
			}
			
			if (mappings.get(DELETE_NEXT_CHAR_ACTION) == null)
			{
				Keystroke keystroke32 = new Keystroke(null,"DELETE");
				setKeyMap(DEFAULT,DELETE_NEXT_CHAR_ACTION,DELETE_NEXT_CHAR_ACTION_DESC,keystroke32);
			}
			
			if (mappings.get(DELETE_PREV_CHAR_ACTION) == null)
			{
				Keystroke keystroke33 = new Keystroke(null,"BACKSPACE");
				setKeyMap(DEFAULT,DELETE_PREV_CHAR_ACTION,DELETE_PREV_CHAR_ACTION_DESC,keystroke33);
			}
			
			
			if (mappings.get(WELL_FORMEDNESS_ACTION) == null)
			{
				Keystroke keystroke34 = new Keystroke(null,"F5");
				setKeyMap(DEFAULT,WELL_FORMEDNESS_ACTION,WELL_FORMEDNESS_ACTION_DESC,keystroke34);
			}
			
			
			if (mappings.get(VALIDATE_ACTION) == null)
			{
				Keystroke keystroke35 = new Keystroke(null,"F7");
				setKeyMap(DEFAULT,VALIDATE_ACTION,VALIDATE_ACTION_DESC,keystroke35);
			}
			
			
			if (mappings.get(START_BROWSER_ACTION) == null)
			{
				Keystroke keystroke36 = new Keystroke(CMD,"F9");
				setKeyMap(DEFAULT,START_BROWSER_ACTION,START_BROWSER_ACTION_DESC,keystroke36);
			}
			
			if (mappings.get(NEW_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke37 = new Keystroke(CMD,"N");
				setKeyMap(DEFAULT,NEW_DOCUMENT_ACTION,NEW_DOCUMENT_ACTION_DESC,keystroke37);
			}
			
			
			if (mappings.get(REDO_ACTION) == null)
			{
				Keystroke keystroke38 = new Keystroke(CMD,"Y");
				setKeyMap(DEFAULT,REDO_ACTION,REDO_ACTION_DESC,keystroke38);
			}
			
			if (mappings.get(SELECT_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke39 = new Keystroke(CMD,"E");
				setKeyMap(DEFAULT,SELECT_ELEMENT_ACTION,SELECT_ELEMENT_ACTION_DESC,keystroke39);
			}
			
			
			if (mappings.get(SELECT_ELEMENT_CONTENT_ACTION) == null)
			{
				Keystroke keystroke40 = new Keystroke(CMDSHIFT,"E");
				setKeyMap(DEFAULT,SELECT_ELEMENT_CONTENT_ACTION,SELECT_ELEMENT_CONTENT_ACTION_DESC,keystroke40);
			}
			
			if (mappings.get(INSERT_SPECIAL_CHAR_ACTION) == null)
			{
				Keystroke keystroke41 = new Keystroke(CMD,"I");
				setKeyMap(DEFAULT,INSERT_SPECIAL_CHAR_ACTION,INSERT_SPECIAL_CHAR_ACTION_DESC,keystroke41);
			}
			
			if (mappings.get(TAG_ACTION) == null)
			{
				Keystroke keystroke42 = new Keystroke(CMD,"T");
				setKeyMap(DEFAULT,TAG_ACTION,TAG_ACTION_DESC,keystroke42);
			}
			
			if (mappings.get(REPEAT_TAG_ACTION) == null)
			{
				Keystroke keystroke42 = new Keystroke(CMDSHIFT,"T");
				setKeyMap(DEFAULT,REPEAT_TAG_ACTION,REPEAT_TAG_ACTION_DESC,keystroke42);
			}
			
			if (mappings.get(TOGGLE_EMPTY_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CMD,"L");
				setKeyMap(DEFAULT,TOGGLE_EMPTY_ELEMENT_ACTION,TOGGLE_EMPTY_ELEMENT_ACTION_DESC,keystroke44);
			}

			if (mappings.get(RENAME_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CMD,"R");
				setKeyMap(DEFAULT,RENAME_ELEMENT_ACTION,RENAME_ELEMENT_ACTION_DESC,keystroke44);
			}

			if (mappings.get(GOTO_START_TAG_ACTION) == null)
			{
				Keystroke keystroke43 = new Keystroke(CMD,"UP");
				setKeyMap(DEFAULT,GOTO_START_TAG_ACTION,GOTO_START_TAG_ACTION_DESC,keystroke43);
			}
			
			if (mappings.get(GOTO_END_TAG_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CMD,"DOWN");
				setKeyMap(DEFAULT,GOTO_END_TAG_ACTION,GOTO_END_TAG_ACTION_DESC,keystroke44);
			}
			
			if (mappings.get(GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CMDSHIFT,"UP");
				setKeyMap(DEFAULT,GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION,GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION_DESC,keystroke44);
			}

			if (mappings.get(GOTO_NEXT_ATTRIBUTE_VALUE_ACTION) == null)
			{
				Keystroke keystroke44 = new Keystroke(CMDSHIFT,"DOWN");
				setKeyMap(DEFAULT,GOTO_NEXT_ATTRIBUTE_VALUE_ACTION,GOTO_NEXT_ATTRIBUTE_VALUE_ACTION_DESC,keystroke44);
			}

			if (mappings.get(TOGGLE_BOOKMARK_ACTION) == null)
			{
				Keystroke keystroke45 = new Keystroke(CMD,"B");
				setKeyMap(DEFAULT,TOGGLE_BOOKMARK_ACTION,TOGGLE_BOOKMARK_ACTION_DESC,keystroke45);
			}
			
			if (mappings.get(SELECT_BOOKMARK_ACTION) == null)
			{
				Keystroke keystroke46 = new Keystroke(CMDSHIFT,"B");
				setKeyMap(DEFAULT,SELECT_BOOKMARK_ACTION,SELECT_BOOKMARK_ACTION_DESC,keystroke46);
			}
			
			if (mappings.get(SCHEMA_ACTION) == null)
			{
				Keystroke keystroke47 = new Keystroke(CMD,"1");
				setKeyMap(DEFAULT,SCHEMA_ACTION,SCHEMA_ACTION_DESC,keystroke47);
			}
			
			if (mappings.get(OUTLINER_ACTION) == null)
			{
				Keystroke keystroke48 = new Keystroke(CMD,"2");
				setKeyMap(DEFAULT,OUTLINER_ACTION,OUTLINER_ACTION_DESC,keystroke48);
			}
			
			if (mappings.get(EDITOR_ACTION) == null)
			{
				Keystroke keystroke49 = new Keystroke(CMD,"3");
				setKeyMap(DEFAULT,EDITOR_ACTION,EDITOR_ACTION_DESC,keystroke49);
			}
			
			if (mappings.get(VIEWER_ACTION) == null)
			{
				Keystroke keystroke50 = new Keystroke(CMD,"4");
				setKeyMap(DEFAULT,VIEWER_ACTION,VIEWER_ACTION_DESC,keystroke50);
			}
			
			
			//Keystroke keystroke51 = new Keystroke(CMD,"5");
			//setKeyMap(DEFAULT,BROWSER_ACTION,BROWSER_ACTION_DESC,keystroke51);
			
			
			if (mappings.get(ADD_ELEMENT_OUTLINER_ACTION) == null)
			{
				Keystroke keystroke52 = new Keystroke(null,"ENTER");
				setKeyMap(DEFAULT,ADD_ELEMENT_OUTLINER_ACTION,ADD_ELEMENT_OUTLINER_ACTION_DESC,keystroke52);
			}
			
			if (mappings.get(DELETE_ELEMENT_OUTLINER_ACTION) == null)
			{
				Keystroke keystroke53 = new Keystroke(null,"DELETE");
				setKeyMap(DEFAULT,DELETE_ELEMENT_OUTLINER_ACTION,DELETE_ELEMENT_OUTLINER_ACTION_DESC,keystroke53);
			}
			
			if (mappings.get(OPEN_REMOTE_ACTION) == null)
			{
				Keystroke keystroke54 = new Keystroke(null,null);
				setKeyMap(DEFAULT,OPEN_REMOTE_ACTION,OPEN_REMOTE_ACTION_DESC,keystroke54);
			}
			
			
			if (mappings.get(RELOAD_ACTION) == null)
			{
				Keystroke keystroke55 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RELOAD_ACTION,RELOAD_ACTION_DESC,keystroke55);
			}
			
			if (mappings.get(SAVE_AS_TEMPLATE_ACTION) == null)
			{
				Keystroke keystroke56 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SAVE_AS_TEMPLATE_ACTION,SAVE_AS_TEMPLATE_ACTION_DESC,keystroke56);
			}
			
			if (mappings.get(MANAGE_TEMPLATE_ACTION) == null)
			{
				Keystroke keystroke57 = new Keystroke(null,null);
				setKeyMap(DEFAULT,MANAGE_TEMPLATE_ACTION,MANAGE_TEMPLATE_ACTION_DESC,keystroke57);
			}
			
			if (mappings.get(PAGE_SETUP_ACTION) == null)
			{
				Keystroke keystroke58 = new Keystroke(null,null);
				setKeyMap(DEFAULT,PAGE_SETUP_ACTION,PAGE_SETUP_ACTION_DESC,keystroke58);
			}
			
			if (mappings.get(PREFERENCES_ACTION) == null)
			{
				Keystroke keystroke59 = new Keystroke(null,null);
				setKeyMap(DEFAULT,PREFERENCES_ACTION,PREFERENCES_ACTION_DESC,keystroke59);
			}
			
			if (mappings.get(CREATE_REQUIRED_NODE_ACTION) == null)
			{
				Keystroke keystroke60 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CREATE_REQUIRED_NODE_ACTION,CREATE_REQUIRED_NODE_ACTION_DESC,keystroke60);
			}
			
			if (mappings.get(SPLIT_ELEMENT_ACTION) == null)
			{
				Keystroke keystroke61 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SPLIT_ELEMENT_ACTION,SPLIT_ELEMENT_ACTION_DESC,keystroke61);
			}
			
			if (mappings.get(CONVERT_ENTITIES_ACTION) == null)
			{
				Keystroke keystroke62 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_ENTITIES_ACTION,CONVERT_ENTITIES_ACTION_DESC,keystroke62);
			}
			
			if (mappings.get(CONVERT_CHARACTERS_ACTION) == null)
			{
				Keystroke keystroke63 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_CHARACTERS_ACTION,CONVERT_CHARACTERS_ACTION_DESC,keystroke63);
			}
			
			if (mappings.get(STRIP_TAG_ACTION) == null)
			{
				Keystroke keystroke64 = new Keystroke(null,null);
				setKeyMap(DEFAULT,STRIP_TAG_ACTION,STRIP_TAG_ACTION_DESC,keystroke64);
			}
			
			if (mappings.get(ADD_CDATA_ACTION) == null)
			{
				Keystroke keystroke65 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_CDATA_ACTION,ADD_CDATA_ACTION_DESC,keystroke65);
			}
			
			if (mappings.get(LOCK_ACTION) == null)
			{
				Keystroke keystroke66 = new Keystroke(null,null);
				setKeyMap(DEFAULT,LOCK_ACTION,LOCK_ACTION_DESC,keystroke66);
			}
			
			if (mappings.get(FORMAT_ACTION) == null)
			{
				Keystroke keystroke67 = new Keystroke(null,null);
				setKeyMap(DEFAULT,FORMAT_ACTION,FORMAT_ACTION_DESC,keystroke67);
			}
			
			if (mappings.get(EXPAND_ALL_ACTION) == null)
			{
				Keystroke keystroke68 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXPAND_ALL_ACTION,EXPAND_ALL_ACTION_DESC,keystroke68);
			}
			
			if (mappings.get(COLLAPSE_ALL_ACTION) == null)
			{
				Keystroke keystroke69 = new Keystroke(null,null);
				setKeyMap(DEFAULT,COLLAPSE_ALL_ACTION,COLLAPSE_ALL_ACTION_DESC,keystroke69);
			}
			
			if (mappings.get(SYNCHRONISE_ACTION) == null)
			{
				Keystroke keystroke70 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SYNCHRONISE_ACTION,SYNCHRONISE_ACTION_DESC,keystroke70);
			}
			
			if (mappings.get(TOGGLE_FULL_ACTION) == null)
			{
				Keystroke keystroke71 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOGGLE_FULL_ACTION,TOGGLE_FULL_ACTION_DESC,keystroke71);
			}
			
			if (mappings.get(NEW_PROJECT_ACTION) == null)
			{
				Keystroke keystroke72 = new Keystroke(null,null);
				setKeyMap(DEFAULT,NEW_PROJECT_ACTION,NEW_PROJECT_ACTION_DESC,keystroke72);
			}
			
			if (mappings.get(IMPORT_PROJECT_ACTION) == null)
			{
				Keystroke keystroke73 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_PROJECT_ACTION,IMPORT_PROJECT_ACTION_DESC,keystroke73);
			}
			
			if (mappings.get(DELETE_PROJECT_ACTION) == null)
			{
				Keystroke keystroke74 = new Keystroke(null,null);
				setKeyMap(DEFAULT,DELETE_PROJECT_ACTION,DELETE_PROJECT_ACTION_DESC,keystroke74);
			}
			
			if (mappings.get(RENAME_PROJECT_ACTION) == null)
			{
				Keystroke keystroke75 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RENAME_PROJECT_ACTION,RENAME_PROJECT_ACTION_DESC,keystroke75);
			}
			
			if (mappings.get(CHECK_WELLFORMEDNESS_ACTION) == null)
			{
				Keystroke keystroke76 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CHECK_WELLFORMEDNESS_ACTION,CHECK_WELLFORMEDNESS_ACTION_DESC,keystroke76);
			}
			
			if (mappings.get(VALIDATE_PROJECT_ACTION) == null)
			{
				Keystroke keystroke77 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_PROJECT_ACTION,VALIDATE_PROJECT_ACTION_DESC,keystroke77);
			}
			
			if (mappings.get(FIND_IN_FILES_ACTION) == null)
			{
				Keystroke keystroke78 = new Keystroke(null,null);
				setKeyMap(DEFAULT,FIND_IN_FILES_ACTION,FIND_IN_FILES_ACTION_DESC,keystroke78);
			}
			
			if (mappings.get(FIND_IN_PROJECTS_ACTION) == null)
			{
				Keystroke keystroke78 = new Keystroke(null,null);
				setKeyMap(DEFAULT,FIND_IN_PROJECTS_ACTION,FIND_IN_PROJECTS_ACTION_DESC,keystroke78);
			}
			
			if (mappings.get(ADD_FILE_ACTION) == null)
			{
				Keystroke keystroke79 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_FILE_ACTION,ADD_FILE_ACTION_DESC,keystroke79);
			}
			
			
			if (mappings.get(ADD_REMOTE_FILE_ACTION) == null)
			{
				Keystroke keystroke80 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_REMOTE_FILE_ACTION,ADD_REMOTE_FILE_ACTION_DESC,keystroke80);
			}
			
			if (mappings.get(REMOVE_FILE_ACTION) == null)
			{
				Keystroke keystroke81 = new Keystroke(null,null);
				setKeyMap(DEFAULT,REMOVE_FILE_ACTION,REMOVE_FILE_ACTION_DESC,keystroke81);
			}
			
			if (mappings.get(ADD_DIRECTORY_ACTION) == null)
			{
				Keystroke keystroke82 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_DIRECTORY_ACTION,ADD_DIRECTORY_ACTION_DESC,keystroke82);
			}
			
			if (mappings.get(ADD_DIRECTORY_CONTENTS_ACTION) == null)
			{
				Keystroke keystroke83 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_DIRECTORY_CONTENTS_ACTION,ADD_DIRECTORY_CONTENTS_ACTION_DESC,keystroke83);
			}
			
			if (mappings.get(ADD_FOLDER_ACTION) == null)
			{
				Keystroke keystroke84 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ADD_FOLDER_ACTION,ADD_FOLDER_ACTION_DESC,keystroke84);
			}
			
			if (mappings.get(REMOVE_FOLDER_ACTION) == null)
			{
		    	Keystroke keystroke85 = new Keystroke(null,null);
				setKeyMap(DEFAULT,REMOVE_FOLDER_ACTION,REMOVE_FOLDER_ACTION_DESC,keystroke85);
			}
			
			if (mappings.get(RENAME_FOLDER_ACTION) == null)
			{
	   			Keystroke keystroke86 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RENAME_FOLDER_ACTION,RENAME_FOLDER_ACTION_DESC,keystroke86);
			}
			
			if (mappings.get(VALIDATE_XML_SCHEMA_ACTION) == null)
			{
				Keystroke keystroke87 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_XML_SCHEMA_ACTION,VALIDATE_XML_SCHEMA_ACTION_DESC,keystroke87);
			}
			
			if (mappings.get(VALIDATE_DTD_ACTION) == null)
			{
				Keystroke keystroke88 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_DTD_ACTION,VALIDATE_DTD_ACTION_DESC,keystroke88);
			}
			
			if (mappings.get(VALIDATE_RELAXNG_ACTION) == null)
			{
				Keystroke keystroke89 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VALIDATE_RELAXNG_ACTION,VALIDATED_RELAXNG_ACTION_DESC,keystroke89);
			}
			
			if (mappings.get(SET_XML_DECLARATION_ACTION) == null)
			{
				Keystroke keystroke90 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_XML_DECLARATION_ACTION,SET_XML_DECLARATION_ACTION_DESC,keystroke90);
			}
			
			if (mappings.get(SET_DOCTYPE_DECLARATION_ACTION) == null)
			{
				Keystroke keystroke91 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_DOCTYPE_DECLARATION_ACTION,SET_DOCTYPE_DECLARATION_ACTION_DESC,keystroke91);
			}
			
			if (mappings.get(SET_SCHEMA_LOCATION_ACTION) == null)
			{
		 		Keystroke keystroke92 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_SCHEMA_LOCATION_ACTION,SET_SCHEMA_LOCATION_ACTION_DESC,keystroke92);
			}
			
			if (mappings.get(RESOLVE_XINCLUDES_ACTION) == null)
			{
				Keystroke keystroke93 = new Keystroke(null,null);
				setKeyMap(DEFAULT,RESOLVE_XINCLUDES_ACTION,RESOLVE_XINCLUDES_ACTION_DESC,keystroke93);
			}
			
			if (mappings.get(SET_SCHEMA_PROPS_ACTION) == null)
			{
				Keystroke keystroke94 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_SCHEMA_PROPS_ACTION,SET_SCHEMA_PROPS_ACTION_DESC,keystroke94);
			}
			
			if (mappings.get(INFER_SCHEMA_ACTION) == null)
			{
				Keystroke keystroke95 = new Keystroke(null,null);
				setKeyMap(DEFAULT,INFER_SCHEMA_ACTION,INFER_SCHEMA_ACTION_DESC,keystroke95);
			}
			
			if (mappings.get(CREATE_TYPE_ACTION) == null)
			{
				Keystroke keystroke96 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CREATE_TYPE_ACTION,CREATE_TYPE_ACTION_DESC,keystroke96);
			}
			
			if (mappings.get(SET_TYPE_ACTION) == null)
			{
				Keystroke keystroke97 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SET_TYPE_ACTION,SET_TYPE_ACTION_DESC,keystroke97);
			}
			
			if (mappings.get(TYPE_PROPERTIES_ACTION) == null)
			{
				Keystroke keystroke98 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TYPE_PROPERTIES_ACTION,TYPE_PROPERTIES_ACTION_DESC,keystroke98);
			}
			
			if (mappings.get(MANAGE_TYPES_ACTION) == null)
			{
				Keystroke keystroke99 = new Keystroke(null,null);
				setKeyMap(DEFAULT,MANAGE_TYPES_ACTION,MANAGE_TYPES_ACTION_DESC,keystroke99);
			}
			
			if (mappings.get(CONVERT_SCHEMA_ACTION) == null)
			{
				Keystroke keystroke100 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_SCHEMA_ACTION,CONVERT_SCHEMA_ACTION_DESC,keystroke100);
			}

			if (mappings.get(EXECUTE_SIMPLE_XSLT_ACTION) == null)
			{
				Keystroke keystroke201 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_SIMPLE_XSLT_ACTION,EXECUTE_SIMPLE_XSLT_ACTION_DESC,keystroke201);	
			}
			

			if (mappings.get(EXECUTE_ADVANCED_XSLT_ACTION) == null)
			{
				Keystroke keystroke101 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_ADVANCED_XSLT_ACTION,EXECUTE_ADVANCED_XSLT_ACTION_DESC,keystroke101);
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_XSLT_ACTION) == null)
			{
				Keystroke keystroke101 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_XSLT_ACTION,EXECUTE_PREVIOUS_XSLT_ACTION_DESC,keystroke101);
			}

			if (mappings.get(EXECUTE_FO_ACTION) == null)
			{
				Keystroke keystroke102 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_FO_ACTION,EXECUTE_FO_ACTION_DESC,keystroke102);
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_FO_ACTION) == null)
			{
				Keystroke keystroke102 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_FO_ACTION,EXECUTE_PREVIOUS_FO_ACTION_DESC,keystroke102);
			}

			if (mappings.get(EXECUTE_XQUERY_ACTION) == null)
			{
				Keystroke keystroke103 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_XQUERY_ACTION,EXECUTE_XQUERY_ACTION_DESC,keystroke103);
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_XQUERY_ACTION) == null)
			{
				Keystroke keystroke103 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_XQUERY_ACTION,EXECUTE_PREVIOUS_XQUERY_ACTION_DESC,keystroke103);
			}

			if (mappings.get(EXECUTE_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke104 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_SCENARIO_ACTION,EXECUTE_SCENARIO_ACTION_DESC,keystroke104);
			}
			
			if (mappings.get(EXECUTE_PREVIOUS_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke104 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_PREVIOUS_SCENARIO_ACTION,EXECUTE_PREVIOUS_SCENARIO_ACTION_DESC,keystroke104);
			}

			if (mappings.get(XSLT_DEBUGGER_ACTION) == null)
			{
				Keystroke keystroke105 = new Keystroke(null,null);
				setKeyMap(DEFAULT,XSLT_DEBUGGER_ACTION,XSLT_DEBUGGER_ACTION_DESC,keystroke105);
			}
			
			if (mappings.get(MANAGE_SCENARIOS_ACTION) == null)
			{
				Keystroke keystroke106 = new Keystroke(null,null);
				setKeyMap(DEFAULT,MANAGE_SCENARIOS_ACTION,MANAGE_SCENARIOS_ACTION_DESC,keystroke106);
			}
			
			if (mappings.get(CANONICALIZE_ACTION) == null)
			{
				Keystroke keystroke107 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CANONICALIZE_ACTION,CANONICALIZE_ACTION_DESC,keystroke107);
			}
			
			if (mappings.get(SIGN_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke108 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SIGN_DOCUMENT_ACTION,SIGN_DOCUMENT_ACTION_DESC,keystroke108);
			}
			
			if (mappings.get(VERIFY_SIGNATURE_ACTION) == null)
			{
				Keystroke keystroke109 = new Keystroke(null,null);
				setKeyMap(DEFAULT,VERIFY_SIGNATURE_ACTION,VERIFY_SIGNATURE_ACTION_DESC,keystroke109);
			}
			
			if (mappings.get(SHOW_SVG_ACTION) == null)
			{
				Keystroke keystroke110 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SHOW_SVG_ACTION,SHOW_SVG_ACTION_DESC,keystroke110);
			}
			
			if (mappings.get(CONVERT_SVG_ACTION) == null)
			{
				Keystroke keystroke111 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CONVERT_SVG_ACTION,CONVERT_SVG_ACTION_DESC,keystroke111);
			}
			
			if (mappings.get(SEND_SOAP_MESSAGE_ACTION) == null)
			{
				Keystroke keystroke112 = new Keystroke(null,null);
				setKeyMap(DEFAULT,SEND_SOAP_MESSAGE_ACTION,SEND_SOAP_MESSAGE_ACTION_DESC,keystroke112);
			}
			
			if (mappings.get(ANALYSE_WSDL_ACTION) == null)
			{
				Keystroke keystroke113 = new Keystroke(null,null);
				setKeyMap(DEFAULT,ANALYSE_WSDL_ACTION,ANALYSE_WSDL_ACTION_DESC,keystroke113);
			}
			
			if (mappings.get(CLEAN_UP_HTML_ACTION) == null)
			{
				Keystroke keystroke114 = new Keystroke(null,null);
				setKeyMap(DEFAULT,CLEAN_UP_HTML_ACTION,CLEAN_UP_HTML_ACTION_DESC,keystroke114);
			}
			
			if (mappings.get(IMPORT_FROM_TEXT_ACTION) == null)
			{
				Keystroke keystroke115 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_TEXT_ACTION,IMPORT_FROM_TEXT_ACTION_DESC,keystroke115);
			}
			
			if (mappings.get(IMPORT_FROM_EXCEL_ACTION) == null)
			{
				Keystroke keystroke116 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_EXCEL_ACTION,IMPORT_FROM_EXCEL_ACTION_DESC,keystroke116);
			}
			
			if (mappings.get(IMPORT_FROM_DBTABLE_ACTION) == null)
			{
				Keystroke keystroke117 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_DBTABLE_ACTION,IMPORT_FROM_DBTABLE_ACTION_DESC,keystroke117);
			}

			if (mappings.get(SELECT_FRAGMENT_ACTION) == null)
			{
				Keystroke keystroke118 = new Keystroke(CMDSHIFT,"SPACE");
				setKeyMap(DEFAULT,SELECT_FRAGMENT_ACTION,SELECT_FRAGMENT_ACTION_DESC,keystroke118);
			}
			
			if (mappings.get(TOOLS_EMPTY_DOCUMENT_ACTION) == null)
			{
				Keystroke keystroke119 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_EMPTY_DOCUMENT_ACTION,TOOLS_EMPTY_DOCUMENT_ACTION_DESC,keystroke119);
			}
			
			if (mappings.get(TOOLS_CAPITALIZE_ACTION) == null)
			{
				Keystroke keystroke120 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_CAPITALIZE_ACTION,TOOLS_CAPITALIZE_ACTION_DESC,keystroke120);
			}
			
			if (mappings.get(TOOLS_DECAPITALIZE_ACTION) == null)
			{
				Keystroke keystroke121 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_DECAPITALIZE_ACTION,TOOLS_DECAPITALIZE_ACTION_DESC,keystroke121);
			}
			
			if (mappings.get(TOOLS_LOWERCASE_ACTION) == null)
			{
				Keystroke keystroke122 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_LOWERCASE_ACTION,TOOLS_LOWERCASE_ACTION_DESC,keystroke122);
			}
			
			if (mappings.get(TOOLS_UPPERCASE_ACTION) == null)
			{
				Keystroke keystroke123 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_UPPERCASE_ACTION,TOOLS_UPPERCASE_ACTION_DESC,keystroke123);
			}
			
			if (mappings.get(IMPORT_FROM_SQLXML_ACTION) == null)
			{
				Keystroke keystroke124 = new Keystroke(null,null);
				setKeyMap(DEFAULT,IMPORT_FROM_SQLXML_ACTION,IMPORT_FROM_SQLXML_ACTION_DESC,keystroke124);
			}
			
			if (mappings.get(TOOLS_MOVE_NS_TO_ROOT_ACTION) == null)
			{
				Keystroke keystroke125 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_MOVE_NS_TO_ROOT_ACTION,TOOLS_MOVE_NS_TO_ROOT_ACTION_DESC,keystroke125);
			}
			
			if (mappings.get(TOOLS_MOVE_NS_TO_FIRST_USED_ACTION) == null)
			{
				Keystroke keystroke126 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_MOVE_NS_TO_FIRST_USED_ACTION,TOOLS_MOVE_NS_TO_FIRST_USED_ACTION_DESC,keystroke126);
			}
			
			if (mappings.get(TOOLS_CHANGE_NS_PREFIX_ACTION) == null)
			{
				Keystroke keystroke127 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_CHANGE_NS_PREFIX_ACTION,TOOLS_CHANGE_NS_PREFIX_ACTION_DESC,keystroke127);
			}
			
			if (mappings.get(TOOLS_RENAME_NODE_ACTION) == null)
			{
				Keystroke keystroke128 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_RENAME_NODE_ACTION,TOOLS_RENAME_NODE_ACTION_DESC,keystroke128);
			}
			
			if (mappings.get(TOOLS_REMOVE_NODE_ACTION) == null)
			{
				Keystroke keystroke129 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_REMOVE_NODE_ACTION,TOOLS_REMOVE_NODE_ACTION_DESC,keystroke129);
			}
			
			if (mappings.get(TOOLS_ADD_NODE_TO_NS_ACTION) == null)
			{
				Keystroke keystroke130 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_ADD_NODE_TO_NS_ACTION,TOOLS_ADD_NODE_TO_NS_ACTION_DESC,keystroke130);
			}
			
			if (mappings.get(TOOLS_SET_NODE_VALUE_ACTION) == null)
			{
				Keystroke keystroke131 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_SET_NODE_VALUE_ACTION,TOOLS_SET_NODE_VALUE_ACTION_DESC,keystroke131);
			}
			
			if (mappings.get(TOOLS_ADD_NODE_ACTION) == null)
			{
				Keystroke keystroke132 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_ADD_NODE_ACTION,TOOLS_ADD_NODE_ACTION_DESC,keystroke132);
			}

			if (mappings.get(TOOLS_REMOVE_UNUSED_NS_ACTION) == null)
			{
				Keystroke keystroke133 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_REMOVE_UNUSED_NS_ACTION,TOOLS_REMOVE_UNUSED_NS_ACTION_DESC,keystroke133);
			}
			
			if (mappings.get(TOOLS_CONVERT_NODE_ACTION) == null)
			{
				Keystroke keystroke134 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_CONVERT_NODE_ACTION,TOOLS_CONVERT_NODE_ACTION_DESC,keystroke134);
			}
			
			if (mappings.get(TOOLS_SORT_NODE_ACTION) == null)
			{
				Keystroke keystroke135 = new Keystroke(null,null);
				setKeyMap(DEFAULT,TOOLS_SORT_NODE_ACTION,TOOLS_SORT_NODE_ACTION_DESC,keystroke135);
			}
			
			if (mappings.get(XDIFF_ACTION) == null)
			{
				Keystroke keystroke136Xdiff = new Keystroke(null,null);
				setKeyMap(DEFAULT,XDIFF_ACTION,XDIFF_ACTION_DESC,keystroke136Xdiff);
			}
			
			if (mappings.get(DEBUGGER_NEW_TRANSFORMATION_ACTION) == null)
			{
				Keystroke keystroke137Debugger = new Keystroke(CTRL,"N");
				setKeyMap(DEFAULT,DEBUGGER_NEW_TRANSFORMATION_ACTION,DEBUGGER_NEW_TRANSFORMATION_ACTION_DESC,keystroke137Debugger);
			}
			
			if (mappings.get(DEBUGGER_CLOSE_ACTION) == null)
			{
				Keystroke keystroke138Close = new Keystroke(CTRL,"W");
				setKeyMap(DEFAULT,DEBUGGER_CLOSE_ACTION,DEBUGGER_CLOSE_ACTION_DESC,keystroke138Close);
			}
			
			if (mappings.get(DEBUGGER_OPEN_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke139OpenScenario = new Keystroke(CTRL,"O");
				setKeyMap(DEFAULT,DEBUGGER_OPEN_SCENARIO_ACTION,DEBUGGER_OPEN_SCENARIO_ACTION_DESC,keystroke139OpenScenario);
			}
			
			if (mappings.get(DEBUGGER_CLOSE_TRANSFORMATION_ACTION) == null)
			{
				Keystroke keystroke140CloseTrans = new Keystroke(CTRLSHIFT,"W");
				setKeyMap(DEFAULT,DEBUGGER_CLOSE_TRANSFORMATION_ACTION,DEBUGGER_CLOSE_TRANSFORMATION_ACTION_DESC,keystroke140CloseTrans);
			}
			
			if (mappings.get(DEBUGGER_SAVE_AS_SCENARIO_ACTION) == null)
			{
				Keystroke keystroke141SaveScenario = new Keystroke(CTRL,"S");
				setKeyMap(DEFAULT,DEBUGGER_SAVE_AS_SCENARIO_ACTION,DEBUGGER_SAVE_AS_SCENARIO_ACTION_DESC,keystroke141SaveScenario);
			}
			
			if (mappings.get(DEBUGGER_FIND_ACTION) == null)
			{
				Keystroke keystroke142DebuggerFind = new Keystroke(CTRL,"F");
				setKeyMap(DEFAULT,DEBUGGER_FIND_ACTION,DEBUGGER_FIND_ACTION_DESC,keystroke142DebuggerFind);
			}
			
			if (mappings.get(DEBUGGER_FIND_NEXT_ACTION) == null)
			{
				Keystroke keystroke143DebuggerFindNext = new Keystroke(null,"F3");
				setKeyMap(DEFAULT,DEBUGGER_FIND_NEXT_ACTION,DEBUGGER_FIND_NEXT_ACTION_DESC,keystroke143DebuggerFindNext );
			}
			
			if (mappings.get(DEBUGGER_GOTO_ACTION) == null)
			{
				Keystroke keystroke144DebuggerGoto = new Keystroke(CTRL,"G");
				setKeyMap(DEFAULT,DEBUGGER_GOTO_ACTION,DEBUGGER_GOTO_ACTION_DESC,keystroke144DebuggerGoto);
			}
			
			if (mappings.get(DEBUGGER_START_ACTION) == null)
			{
				Keystroke keystroke145DebugStart = new Keystroke(null,"F5");
				setKeyMap(DEFAULT,DEBUGGER_START_ACTION,DEBUGGER_START_ACTION_DESC,keystroke145DebugStart);
			}
			
			if (mappings.get(DEBUGGER_RUN_END_ACTION) == null)
			{
				Keystroke keystroke146DebugRunEnd = new Keystroke(CTRLSHIFT,"F5");
				setKeyMap(DEFAULT,DEBUGGER_RUN_END_ACTION,DEBUGGER_RUN_END_ACTION_DESC,keystroke146DebugRunEnd);
			}
			
			if (mappings.get(DEBUGGER_PAUSE_ACTION) == null)
			{
				Keystroke keystroke147DebugPause = new Keystroke(CTRL,"F5");
				setKeyMap(DEFAULT,DEBUGGER_PAUSE_ACTION,DEBUGGER_PAUSE_ACTION_DESC,keystroke147DebugPause);
			}
			
			if (mappings.get(DEBUGGER_STOP_ACTION) == null)
			{
				Keystroke keystroke148DebugStop = new Keystroke(SHIFT,"F5");
				setKeyMap(DEFAULT,DEBUGGER_STOP_ACTION,DEBUGGER_STOP_ACTION_DESC,keystroke148DebugStop);
			}
			
			if (mappings.get(DEBUGGER_STEP_INTO_ACTION) == null)
			{
				Keystroke keystroke149DebugStepInto = new Keystroke(null,"F11");
				setKeyMap(DEFAULT,DEBUGGER_STEP_INTO_ACTION,DEBUGGER_STEP_INTO_ACTION_DESC, keystroke149DebugStepInto);
			}
			
			if (mappings.get(DEBUGGER_STEP_OVER_ACTION) == null)
			{
				Keystroke keystroke150DebugStepOver = new Keystroke(null,"F10");
				setKeyMap(DEFAULT,DEBUGGER_STEP_OVER_ACTION,DEBUGGER_STEP_OVER_ACTION_DESC, keystroke150DebugStepOver );
			}
			
			if (mappings.get(DEBUGGER_STEP_OUT_ACTION) == null)
			{
				Keystroke keystroke151DebugStepOut = new Keystroke(SHIFT,"F11");
				setKeyMap(DEFAULT,DEBUGGER_STEP_OUT_ACTION,DEBUGGER_STEP_OUT_ACTION_DESC, keystroke151DebugStepOut  );
			}
			
			if (mappings.get(DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION) == null)
			{
				Keystroke keystroke152DebugRemoveAllBreakpoints = new Keystroke(CTRLSHIFT,"F9");
				setKeyMap(DEFAULT,DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION,DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION_DESC, keystroke152DebugRemoveAllBreakpoints);
			}
			
			if (mappings.get(DEBUGGER_OPEN_INPUT_ACTION) == null)
			{
				Keystroke keystroke153OpenInput = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_OPEN_INPUT_ACTION,DEBUGGER_OPEN_INPUT_ACTION_DESC,keystroke153OpenInput);
			}
			
			if (mappings.get(DEBUGGER_OPEN_STYLESHEET_ACTION) == null)
			{
				Keystroke keystroke154OpenStylesheet = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_OPEN_STYLESHEET_ACTION,DEBUGGER_OPEN_STYLESHEET_ACTION_DESC,keystroke154OpenStylesheet);
			}
			
			if (mappings.get(DEBUGGER_RELOAD_ACTION) == null)
			{
				Keystroke keystroke155DebugReload = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_RELOAD_ACTION,DEBUGGER_RELOAD_ACTION_DESC,keystroke155DebugReload);
			}
			
			if (mappings.get(DEBUGGER_EXIT_ACTION) == null)
			{
				Keystroke keystroke156DebugExit = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_EXIT_ACTION,DEBUGGER_EXIT_ACTION_DESC,keystroke156DebugExit);
			}
			
			if (mappings.get(DEBUGGER_COLLAPSE_ALL_ACTION) == null)
			{
				Keystroke keystroke157DebugCollapseAll = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_COLLAPSE_ALL_ACTION,DEBUGGER_COLLAPSE_ALL_ACTION_DESC,keystroke157DebugCollapseAll);
			}
			
			if (mappings.get(DEBUGGER_EXPAND_ALL_ACTION) == null)
			{
				Keystroke keystroke158DebugExpandAll = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_EXPAND_ALL_ACTION,DEBUGGER_EXPAND_ALL_ACTION_DESC,keystroke158DebugExpandAll);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION) == null)
			{
				Keystroke keystroke159DebugShowLine = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION,DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION_DESC, keystroke159DebugShowLine);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION) == null)
			{
				Keystroke keystroke160DebugShowOverview = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION,DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION_DESC, keystroke160DebugShowOverview);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION) == null)
			{
				Keystroke keystroke161DebugShowFolding = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION,DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION_DESC, keystroke161DebugShowFolding);
				
			}
			
			if (mappings.get(DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION) == null)
			{
				Keystroke keystroke162DebugSoftWrap = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION,DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION_DESC, keystroke162DebugSoftWrap);
				
			}
			
			if (mappings.get(DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION) == null)
			{
				Keystroke keystroke163DebugInputShowLine = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION,DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION_DESC, keystroke163DebugInputShowLine);
				
			}
			
			if (mappings.get(DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION) == null)
			{
				Keystroke keystroke164DebugInputShowOverview = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION,DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION_DESC, keystroke164DebugInputShowOverview);
				
			}
			
			if (mappings.get(DEBUGGER_INPUT_SHOW_FOLDING_ACTION) == null)
			{
				Keystroke keystroke165DebugInputShowFolding = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SHOW_FOLDING_ACTION,DEBUGGER_INPUT_SHOW_FOLDING_ACTION, keystroke165DebugInputShowFolding);
				
			}
			
			if (mappings.get(DEBUGGER_INPUT_SOFT_WRAPPING_ACTION) == null)
			{
				Keystroke keystroke166DebugInputSoftWrap = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_INPUT_SOFT_WRAPPING_ACTION,DEBUGGER_INPUT_SOFT_WRAPPING_ACTION_DESC, keystroke166DebugInputSoftWrap);
				
			}
			
			if (mappings.get(DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION) == null)
			{
				Keystroke keystroke167DebugOutputShowLine = new Keystroke(null,null);
				setKeyMap(DEFAULT,DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION,DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION_DESC, keystroke167DebugOutputShowLine);
				
			}
			
			if (mappings.get(DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION) == null)
		    {
				Keystroke keystroke168DebugOutputSoftWrap = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION,DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION_DESC, keystroke168DebugOutputSoftWrap);
			}
			
			if (mappings.get(DEBUGGER_AUTO_OPEN_INPUT_ACTION) == null)
		    {
				Keystroke keystroke169DebugAutoOpen = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_AUTO_OPEN_INPUT_ACTION,DEBUGGER_AUTO_OPEN_INPUT_ACTION_DESC, keystroke169DebugAutoOpen);
			}
			
			if (mappings.get(DEBUGGER_ENABLE_TRACING_ACTION) == null)
		    {
				Keystroke keystroke170DebugEnableTracing = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_ENABLE_TRACING_ACTION,DEBUGGER_ENABLE_TRACING_ACTION_DESC, keystroke170DebugEnableTracing);
			}
			
			if (mappings.get(DEBUGGER_REDIRECT_OUTPUT_ACTION) == null)
		    {
				Keystroke keystroke171DebugRedirectOutput = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_REDIRECT_OUTPUT_ACTION,DEBUGGER_REDIRECT_OUTPUT_ACTION_DESC, keystroke171DebugRedirectOutput);
			}	
			
			if (mappings.get(DEBUGGER_SET_PARAMETERS_ACTION) == null)
		    {
				Keystroke keystroke172DebugSetParams = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_SET_PARAMETERS_ACTION,DEBUGGER_SET_PARAMETERS_ACTION_DESC, keystroke172DebugSetParams);
			}	
			
			if (mappings.get(DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION) == null)
		    {
				Keystroke keystroke173DebugDisableAllBreak = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION,DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION_DESC, keystroke173DebugDisableAllBreak);
			}	
			
			if (mappings.get(DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION) == null)
		    {
				Keystroke keystroke174DebugEnableAllBreak = new Keystroke(null,null);
			    setKeyMap(DEFAULT,DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION,DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION_DESC, keystroke174DebugEnableAllBreak);
			}	
			
			if (mappings.get(HIGHLIGHT_ACTION) == null)
		    {
				Keystroke keystroke175Hightlight = new Keystroke(null,null);
			    setKeyMap(DEFAULT,HIGHLIGHT_ACTION,HIGHLIGHT_ACTION, keystroke175Hightlight);
			}	
			
			if (mappings.get(VIEW_STANDARD_BUTTONS_ACTION) == null)
		    {
				Keystroke keystroke176StandardButtons = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_STANDARD_BUTTONS_ACTION,VIEW_STANDARD_BUTTONS_ACTION_DESC, keystroke176StandardButtons);
			}
			
			if (mappings.get(VIEW_EDITOR_BUTTONS_ACTION) == null)
		    {
				Keystroke keystroke177EditorButtons = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_BUTTONS_ACTION,VIEW_EDITOR_BUTTONS_ACTION_DESC, keystroke177EditorButtons);
			}
			
			if (mappings.get(VIEW_FRAGMENT_BUTTONS_ACTION) == null)
		    {
				Keystroke keystroke178FragmentButtons = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_FRAGMENT_BUTTONS_ACTION,VIEW_FRAGMENT_BUTTONS_ACTION_DESC, keystroke178FragmentButtons);
			}	
			
			if (mappings.get(VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION) == null)
		    {
				Keystroke keystroke179EditorShowLineNumber = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION,VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION_DESC, keystroke179EditorShowLineNumber);
			}	
			
			if (mappings.get(VIEW_EDITOR_SHOW_OVERVIEW_ACTION) == null)
		    {
				Keystroke keystroke180EditorShowOverview = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_OVERVIEW_ACTION,VIEW_EDITOR_SHOW_OVERVIEW_ACTION_DESC, keystroke180EditorShowOverview);
			}
			
			if (mappings.get(VIEW_EDITOR_SHOW_FOLDING_ACTION) == null)
		    {
				Keystroke keystroke181EditorShowFolding = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_FOLDING_ACTION,VIEW_EDITOR_SHOW_FOLDING_ACTION_DESC, keystroke181EditorShowFolding);
			}
			
			if (mappings.get(VIEW_EDITOR_SHOW_ANNOTATION_ACTION) == null)
		    {
				Keystroke keystroke = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SHOW_ANNOTATION_ACTION,VIEW_EDITOR_SHOW_ANNOTATION_ACTION_DESC, keystroke);
			}

			if (mappings.get(VIEW_EDITOR_TAG_COMPLETION_ACTION) == null)
		    {
				Keystroke keystroke182EditorShowTagCompletion = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_TAG_COMPLETION_ACTION,VIEW_EDITOR_TAG_COMPLETION_ACTION_DESC, keystroke182EditorShowTagCompletion);
			}
			
			if (mappings.get(VIEW_EDITOR_END_TAG_COMPLETION_ACTION) == null)
		    {
				Keystroke keystroke183EditorShowEndTagCompletion = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_END_TAG_COMPLETION_ACTION,VIEW_EDITOR_END_TAG_COMPLETION_ACTION_DESC, keystroke183EditorShowEndTagCompletion);
			}
			
			if (mappings.get(VIEW_EDITOR_SMART_INDENTATION_ACTION) == null)
		    {
				Keystroke keystroke183EditorSmartIndentation = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SMART_INDENTATION_ACTION,VIEW_EDITOR_SMART_INDENTATION_ACTION_DESC, keystroke183EditorSmartIndentation);
			}
			
			if (mappings.get(VIEW_EDITOR_SOFT_WRAPPING_ACTION) == null)
		    {
				Keystroke keystroke184EditorSoftWrap = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_SOFT_WRAPPING_ACTION,VIEW_EDITOR_SOFT_WRAPPING_ACTION_DESC, keystroke184EditorSoftWrap);
			}
			
			if (mappings.get(VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION) == null)
		    {
				Keystroke keystroke = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION,VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION_DESC, keystroke);
			}

			if (mappings.get(VIEWER_SHOW_NAMESPACES_ACTION) == null)
		    {
				Keystroke keystroke185ViewerShowNamespaces = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_NAMESPACES_ACTION,VIEWER_SHOW_NAMESPACES_ACTION_DESC, keystroke185ViewerShowNamespaces);
			}
			
			if (mappings.get(VIEWER_SHOW_ATTRIBUTES_ACTION) == null)
		    {
				Keystroke keystroke186ViewerShowAttr = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_ATTRIBUTES_ACTION,VIEWER_SHOW_ATTRIBUTES_ACTION_DESC, keystroke186ViewerShowAttr);
			}
			
			if (mappings.get(VIEWER_SHOW_COMMENTS_ACTION) == null)
		    {
				Keystroke keystroke187ViewerShowComments = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_COMMENTS_ACTION,VIEWER_SHOW_COMMENTS_ACTION_DESC, keystroke187ViewerShowComments);
			}
			
			if (mappings.get(VIEWER_SHOW_TEXT_CONTENT_ACTION) == null)
		    {
				Keystroke keystroke188ViewerShowTextContent = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_TEXT_CONTENT_ACTION,VIEWER_SHOW_TEXT_CONTENT_ACTION_DESC, keystroke188ViewerShowTextContent);
			}
			
			if (mappings.get(VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION) == null)
		    {
				Keystroke keystroke189ViewerShowPIs = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION,VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION_DESC, keystroke189ViewerShowPIs);
			}
			
			if (mappings.get(VIEWER_INLINE_MIXED_CONTENT_ACTION) == null)
		    {
				Keystroke keystroke190ViewerinlineMixed = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEWER_INLINE_MIXED_CONTENT_ACTION,VIEWER_INLINE_MIXED_CONTENT_ACTION_DESC, keystroke190ViewerinlineMixed);
			}
			
			if (mappings.get(OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION) == null)
		    {
				Keystroke keystroke191OutlinerShowAttr = new Keystroke(null,null);
			    setKeyMap(DEFAULT,OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION,OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION_DESC, keystroke191OutlinerShowAttr);
			}
			
			if (mappings.get(OUTLINER_SHOW_ELEMENT_VALUES_ACTION) == null)
		    {
				Keystroke keystroke192OutlinerShowEle = new Keystroke(null,null);
			    setKeyMap(DEFAULT,OUTLINER_SHOW_ELEMENT_VALUES_ACTION,OUTLINER_SHOW_ELEMENT_VALUES_ACTION_DESC, keystroke192OutlinerShowEle);
			}
			
			if (mappings.get(OUTLINER_CREATE_REQUIRED_NODES_ACTION) == null)
		    {
				Keystroke keystroke192OutlinerShowEle = new Keystroke(null,null);
			    setKeyMap(DEFAULT,OUTLINER_CREATE_REQUIRED_NODES_ACTION,OUTLINER_CREATE_REQUIRED_NODES_ACTION_DESC, keystroke192OutlinerShowEle);
			}
			
			if (mappings.get(VIEW_SYNCHRONIZE_SPLITS_ACTION) == null)
		    {
				Keystroke keystroke193SynchroniseSplits = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_SYNCHRONIZE_SPLITS_ACTION,VIEW_SYNCHRONIZE_SPLITS_ACTION_DESC, keystroke193SynchroniseSplits);
			}
			
			if (mappings.get(VIEW_SPLIT_HORIZONTALLY_ACTION) == null)
		    {
				Keystroke keystroke194SplitHorizontally = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_SPLIT_HORIZONTALLY_ACTION,VIEW_SPLIT_HORIZONTALLY_ACTION_DESC, keystroke194SplitHorizontally);
			}
			
			if (mappings.get(VIEW_SPLIT_VERTICALLY_ACTION) == null)
		    {
				Keystroke keystroke195SplitVertically = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_SPLIT_VERTICALLY_ACTION,VIEW_SPLIT_VERTICALLY_ACTION_DESC, keystroke195SplitVertically);
			}
			
			if (mappings.get(VIEW_UNSPLIT_ACTION) == null)
		    {
				Keystroke keystroke196Unsplit = new Keystroke(null,null);
			    setKeyMap(DEFAULT,VIEW_UNSPLIT_ACTION,VIEW_UNSPLIT_ACTION_DESC, keystroke196Unsplit);
			}
			
			/*if (mappings.get(GRID_ACTION) == null)
			{
				Keystroke keystroke197Grid = new Keystroke(CTRL,"5");
				setKeyMap(DEFAULT,GRID_ACTION,GRID_ACTION_DESC,keystroke197Grid);
			}*/
			
			/*if (mappings.get(GRID_ADD_ATTRIBUTE_COLUMN_ACTION) == null)
		    {
				Keystroke keystroke197AddAttributeColumn = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ATTRIBUTE_COLUMN_ACTION,GRID_ADD_ATTRIBUTE_COLUMN_ACTION_DESC, keystroke197AddAttributeColumn);
			}
			
			if (mappings.get(GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION) == null)
		    {
				Keystroke keystroke198AddAttributeToSelected = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION,GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION_DESC, keystroke198AddAttributeToSelected);
			}
			
			if (mappings.get(GRID_ADD_CHILD_TABLE_ACTION) == null)
		    {
				Keystroke keystroke199AddChildTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_CHILD_TABLE_ACTION,GRID_ADD_CHILD_TABLE_ACTION_DESC, keystroke199AddChildTable);
			}
			
			if (mappings.get(GRID_ADD_ELEMENT_AFTER_ACTION) == null)
		    {
				Keystroke keystroke200AddElementAfter = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ELEMENT_AFTER_ACTION,GRID_ADD_ELEMENT_AFTER_ACTION_DESC, keystroke200AddElementAfter);
			}
			
			if (mappings.get(GRID_ADD_ELEMENT_BEFORE_ACTION) == null)
		    {
				Keystroke keystroke201AddElementBefore = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_ELEMENT_BEFORE_ACTION,GRID_ADD_ELEMENT_BEFORE_ACTION_DESC, keystroke201AddElementBefore);
			}
			
			if (mappings.get(GRID_ADD_TEXT_COLUMN_ACTION) == null)
		    {
				Keystroke keystroke202AddTextColumn = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_TEXT_COLUMN_ACTION,GRID_ADD_TEXT_COLUMN_ACTION_DESC, keystroke202AddTextColumn);
			}
			
			if (mappings.get(GRID_ADD_TEXT_TO_SELECTED_ACTION) == null)
		    {
				Keystroke keystroke203AddTextToSelected = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_ADD_TEXT_TO_SELECTED_ACTION,GRID_ADD_TEXT_TO_SELECTED_ACTION_DESC, keystroke203AddTextToSelected);
			}
			
			if (mappings.get(GRID_DELETE_ATTS_AND_TEXT_ACTION) == null)
		    {
				Keystroke keystroke204DeleteAttsAndText = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_ATTS_AND_TEXT_ACTION,GRID_DELETE_ATTS_AND_TEXT_ACTION_DESC, keystroke204DeleteAttsAndText);
			}
			
			if (mappings.get(GRID_DELETE_CHILD_TABLE_ACTION) == null)
		    {
				Keystroke keystroke205DeleteChildTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_CHILD_TABLE_ACTION,GRID_DELETE_CHILD_TABLE_ACTION_DESC, keystroke205DeleteChildTable);
			}
			
			if (mappings.get(GRID_DELETE_COLUMN_ACTION) == null)
		    {
				Keystroke keystroke206DeleteColumn = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_COLUMN_ACTION,GRID_DELETE_COLUMN_ACTION_DESC, keystroke206DeleteColumn);
			}
			
			if (mappings.get(GRID_DELETE_ROW_ACTION) == null)
		    {
				Keystroke keystroke207DeleteRow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_ROW_ACTION,GRID_DELETE_ROW_ACTION_DESC, keystroke207DeleteRow);
			}
			
			if (mappings.get(GRID_DELETE_SELECTED_ATTRIBUTE_ACTION) == null)
		    {
				Keystroke keystroke208DeleteSelectedAttribute = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_SELECTED_ATTRIBUTE_ACTION,GRID_DELETE_SELECTED_ATTRIBUTE_ACTION_DESC, keystroke208DeleteSelectedAttribute);
			}
			
			if (mappings.get(GRID_DELETE_SELECTED_TEXT_ACTION) == null)
		    {
				Keystroke keystroke209DeleteSelectedText = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_DELETE_SELECTED_TEXT_ACTION,GRID_DELETE_SELECTED_TEXT_ACTION_DESC, keystroke209DeleteSelectedText);
			}
			
			if (mappings.get(GRID_RENAME_ATTRIBUTE_ACTION) == null)
		    {
				Keystroke keystroke210EditAttributeName = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_RENAME_ATTRIBUTE_ACTION,GRID_RENAME_ATTRIBUTE_ACTION_DESC, keystroke210EditAttributeName);
			}
			
			if (mappings.get(GRID_RENAME_SELECTED_ATTRIBUTE_ACTION) == null)
		    {
				Keystroke keystroke210RenameSelectedAttribute = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_RENAME_SELECTED_ATTRIBUTE_ACTION,GRID_RENAME_SELECTED_ATTRIBUTE_ACTION_DESC, keystroke210RenameSelectedAttribute);
			}
			
			if (mappings.get(GRID_MOVE_ROW_DOWN_ACTION) == null)
		    {
				Keystroke keystroke211MoveRowDown = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_MOVE_ROW_DOWN_ACTION,GRID_MOVE_ROW_DOWN_ACTION_DESC, keystroke211MoveRowDown);
			}
			
			if (mappings.get(GRID_MOVE_ROW_UP_ACTION) == null)
		    {
				Keystroke keystroke212MoveRowUp = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_MOVE_ROW_UP_ACTION,GRID_MOVE_ROW_UP_ACTION_DESC, keystroke212MoveRowUp);
			}
			
			if (mappings.get(GRID_SORT_TABLE_DESCENDING_ACTION) == null)
		    {
				Keystroke keystroke213SortTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_SORT_TABLE_DESCENDING_ACTION,GRID_SORT_TABLE_DESCENDING_ACTION_DESC, keystroke213SortTable);
			}
			
			if (mappings.get(GRID_SORT_TABLE_ASCENDING_ACTION) == null)
		    {
				Keystroke keystroke213SortDescending = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_SORT_TABLE_ASCENDING_ACTION,GRID_SORT_TABLE_ASCENDING_ACTION_DESC, keystroke213SortDescending);
			}
			
			if (mappings.get(GRID_UNSORT_ACTION) == null)
		    {
				Keystroke keystroke213Unsort = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_UNSORT_ACTION,GRID_UNSORT_ACTION_DESC, keystroke213Unsort);
			}
			
			if (mappings.get(GRID_GOTO_PARENT_TABLE_ACTION) == null)
		    {
				Keystroke keystroke214Collapse = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_GOTO_PARENT_TABLE_ACTION,GRID_COLLAPSE_ACTION_DESC, keystroke214Collapse);
			}
			
			if (mappings.get(GRID_GOTO_CHILD_TABLE_ACTION) == null)
		    {
				Keystroke keystroke215Expand = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_GOTO_CHILD_TABLE_ACTION,GRID_EXPAND_ACTION_DESC, keystroke215Expand);
			}
			
			if (mappings.get(GRID_COPY_SHALLOW_ACTION) == null)
		    {
				Keystroke keystroke216CopyShallow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_COPY_SHALLOW_ACTION,GRID_COPY_SHALLOW_ACTION_DESC, keystroke216CopyShallow);
			}
			
			if (mappings.get(GRID_PASTE_AFTER_ACTION) == null)
		    {
				Keystroke keystroke217PasteAfter = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_PASTE_AFTER_ACTION,GRID_PASTE_AFTER_ACTION_DESC, keystroke217PasteAfter);
			}
			
			if (mappings.get(GRID_PASTE_AS_CHILD_ACTION) == null)
		    {
				Keystroke keystroke218PasteAsChild = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_PASTE_AS_CHILD_ACTION,GRID_PASTE_AS_CHILD_ACTION_DESC, keystroke218PasteAsChild);
			}
			
			if (mappings.get(GRID_PASTE_BEFORE_ACTION) == null)
		    {
				Keystroke keystroke219PasteBefore = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_PASTE_BEFORE_ACTION,GRID_PASTE_BEFORE_ACTION_DESC, keystroke219PasteBefore);
			}
			
			if (mappings.get(GRID_COLLAPSE_ROW_ACTION) == null)
		    {
				Keystroke keystroke220CollapseRow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_COLLAPSE_ROW_ACTION,GRID_COLLAPSE_ROW_ACTION_DESC, keystroke220CollapseRow);
			}
			
			if (mappings.get(GRID_EXPAND_ROW_ACTION) == null)
		    {
				Keystroke keystroke221ExpandRow = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_EXPAND_ROW_ACTION,GRID_EXPAND_ROW_ACTION_DESC, keystroke221ExpandRow);
			}
			
			if (mappings.get(GRID_COLLAPSE_CURRENT_TABLE_ACTION) == null)
		    {
				Keystroke keystroke222CollapseCurrentTable = new Keystroke(null,null);
			    setKeyMap(DEFAULT,GRID_COLLAPSE_CURRENT_TABLE_ACTION,GRID_COLLAPSE_CURRENT_TABLE_ACTION_DESC, keystroke222CollapseCurrentTable);
			}
			
			if (mappings.get(GRID_DELETE_ACTION) == null)
		    {
				Keystroke keystroke223GridDelete = new Keystroke(null,"DELETE");
			    setKeyMap(DEFAULT,GRID_DELETE_ACTION,GRID_DELETE_ACTION_DESC, keystroke223GridDelete);
			}*/
			
			if (mappings.get(EXECUTE_SCHEMATRON_ACTION) == null)
			{
				Keystroke keystroke224 = new Keystroke(null,null);
				setKeyMap(DEFAULT,EXECUTE_SCHEMATRON_ACTION,EXECUTE_SCHEMATRON_ACTION_DESC,keystroke224);
			}
			
			for(int cnt=0;cnt<getPluginMappings().size();++cnt) {
				Object obj = getPluginMappings().get(cnt);
				if((obj != null) && (obj instanceof PluginActionKeyMapping)) {
					PluginActionKeyMapping keyMapping = (PluginActionKeyMapping)obj;
					
					if(mappings.get(keyMapping.getKeystroke_action_name()) == null) {
						Keystroke pluginKeystroke = new Keystroke(keyMapping.getKeystroke_mask(), keyMapping.getKeystroke_value());
						setKeyMap(DEFAULT, keyMapping.getKeystroke_action_name(), keyMapping.getKeystroke_action_description(), pluginKeystroke);
						
					}
				}
			}
		}
		
	}
	
	/**
	 * sets the emacs keys for the first time the app starts
	 */
	private void setEmacsKeyMappings()
	{
		Hashtable mappings = getKeyMaps(EMACS);
		
		if (mappings.get(OPEN_ACTION) == null)
		{
			Keystroke keystroke1 = new Keystroke(CTRL,"X");
			Keystroke keystroke1x = new Keystroke(CTRL,"F");
			setKeyMap(EMACS,OPEN_ACTION,OPEN_ACTION_DESC,keystroke1,keystroke1x);
		}
		
		if (mappings.get(CLOSE_ACTION) == null)
		{
			Keystroke keystroke2 = new Keystroke(CTRL,"X");
			Keystroke keystroke2x = new Keystroke(null,"K");
			setKeyMap(EMACS,CLOSE_ACTION,CLOSE_ACTION_DESC,keystroke2,keystroke2x);
		}
		
		if (mappings.get(CLOSE_ALL_ACTION) == null)
		{
			Keystroke keystroke3 = new Keystroke(CTRL,"X");
			Keystroke keystroke3x = new Keystroke(CTRL,"C");
			setKeyMap(EMACS,CLOSE_ALL_ACTION,CLOSE_ALL_ACTION_DESC,keystroke3,keystroke3x);
		}
		
		if (mappings.get(SAVE_ACTION) == null)
		{
			Keystroke keystroke4 = new Keystroke(CTRL,"X");
			Keystroke keystroke4x = new Keystroke(CTRL,"S");
			setKeyMap(EMACS,SAVE_ACTION,SAVE_ACTION_DESC,keystroke4,keystroke4x);
		}
		
		if (mappings.get(SAVE_ALL_ACTION) == null)
		{
			Keystroke keystroke5 = new Keystroke(CTRL,"X");
			Keystroke keystroke5x = new Keystroke(null,"S");
			setKeyMap(EMACS,SAVE_ALL_ACTION,SAVE_ALL_ACTION_DESC,keystroke5,keystroke5x);
		}
		
		if (mappings.get(UNDO_ACTION) == null)
		{
			Keystroke keystroke6 = new Keystroke(CTRL,"S");
			Keystroke keystroke6x = new Keystroke(null,"U");
			setKeyMap(EMACS,UNDO_ACTION,UNDO_ACTION_DESC,keystroke6,keystroke6x);
		}
		
		if (mappings.get(SELECT_ALL_ACTION) == null)
		{
			Keystroke keystroke7 = new Keystroke(CTRL,"X");
			Keystroke keystroke7x = new Keystroke(null,"H");
			setKeyMap(EMACS,SELECT_ALL_ACTION,SELECT_ALL_ACTION_DESC,keystroke7,keystroke7x);
		}
		
		if (mappings.get(SAVE_AS_ACTION) == null)
		{
			Keystroke keystrokeSaveAs = new Keystroke(CTRL,"X");
			Keystroke keystrokeSaveAsx = new Keystroke(CTRL,"W");
			setKeyMap(EMACS,SAVE_AS_ACTION,SAVE_AS_ACTION_DESC,keystrokeSaveAs,keystrokeSaveAsx);
		}
		
		if (mappings.get(SELECT_DOCUMENT_ACTION) == null)
		{
			Keystroke keystroke8 = new Keystroke(CTRL,"X");
			Keystroke keystroke8x = new Keystroke(CTRL,"B");
			setKeyMap(EMACS,SELECT_DOCUMENT_ACTION,SELECT_DOCUMENT_ACTION_DESC,keystroke8,keystroke8x);
		}
		
		if (mappings.get(PRINT_ACTION) == null)
		{
			Keystroke keystroke9 = new Keystroke(ALT,"F9");
			setKeyMap(EMACS,PRINT_ACTION,PRINT_ACTION_DESC,keystroke9);
		}
		
		if (mappings.get(FIND_ACTION) == null)
		{
			Keystroke keystroke10 = new Keystroke(CTRL,"S");
			setKeyMap(EMACS,FIND_ACTION,FIND_ACTION_DESC,keystroke10);
		}
		
		if (mappings.get(FIND_NEXT_ACTION) == null)
		{
			Keystroke keystroke11 = new Keystroke(null,"F3");
			setKeyMap(EMACS,FIND_NEXT_ACTION,FIND_NEXT_ACTION_DESC,keystroke11);
		}
		
		if (mappings.get(REPLACE_ACTION) == null)
		{
			Keystroke keystroke12 = new Keystroke(ALT,"R");
			setKeyMap(EMACS,REPLACE_ACTION,REPLACE_ACTION_DESC,keystroke12);
		}
		
		if (mappings.get(CUT_ACTION) == null)
		{
			Keystroke keystroke13 = new Keystroke(CTRL,"W");
			setKeyMap(EMACS,CUT_ACTION,CUT_ACTION_DESC,keystroke13);
		}
		
		if (mappings.get(COPY_ACTION) == null)
		{
			Keystroke keystroke14 = new Keystroke(ALT,"W");
			setKeyMap(EMACS,COPY_ACTION,COPY_ACTION_DESC,keystroke14);
		}
		
		if (mappings.get(PASTE_ACTION) == null)
		{
			Keystroke keystroke15 = new Keystroke(CTRL,"Y");
			setKeyMap(EMACS,PASTE_ACTION,PASTE_ACTION_DESC,keystroke15);
		}
		
		if (mappings.get(COMMENT_ACTION) == null)
		{
			Keystroke keystroke16 = new Keystroke(ALT,";");
			setKeyMap(EMACS,COMMENT_ACTION,COMMENT_ACTION_DESC,keystroke16);
		}
		
		if (mappings.get(TAB_ACTION) == null)
		{
			Keystroke keystroke17 = new Keystroke(CTRL,"Q");
			setKeyMap(EMACS,TAB_ACTION,TAB_ACTION_DESC,keystroke17);
		}
		
		if (mappings.get(GOTO_ACTION) == null)
		{
			Keystroke keystroke18 = new Keystroke(CTRL,"X");
			Keystroke keystroke18x = new Keystroke(null,"G");
			setKeyMap(EMACS,GOTO_ACTION,GOTO_ACTION_DESC,keystroke18,keystroke18x);
		}
		
		if (mappings.get(UNINDENT_ACTION) == null)
		{
			Keystroke keystroke19 = new Keystroke(ALT,"Q");
			setKeyMap(EMACS,UNINDENT_ACTION,UNINDENT_ACTION_DESC,keystroke19);
		}
		
		if (mappings.get(UP_ACTION) == null)
		{
			Keystroke keystroke20 = new Keystroke(CTRL,"P");
			setKeyMap(EMACS,UP_ACTION,UP_ACTION_DESC,keystroke20);
		}
		
		if (mappings.get(DOWN_ACTION) == null)
		{
			Keystroke keystroke21 = new Keystroke(CTRL,"N");
			setKeyMap(EMACS,DOWN_ACTION,DOWN_ACTION_DESC,keystroke21);
		}
		
		if (mappings.get(RIGHT_ACTION) == null)
		{
			Keystroke keystroke22 = new Keystroke(CTRL,"F");
			setKeyMap(EMACS,RIGHT_ACTION,RIGHT_ACTION_DESC,keystroke22);
		}
		
		if (mappings.get(LEFT_ACTION) == null)
		{
			Keystroke keystroke23 = new Keystroke(CTRL,"B");
			setKeyMap(EMACS,LEFT_ACTION,LEFT_ACTION_DESC,keystroke23);
		}
		
		if (mappings.get(PAGE_UP_ACTION) == null)
		{
			Keystroke keystroke24 = new Keystroke(ALT,"V");
			setKeyMap(EMACS,PAGE_UP_ACTION,PAGE_UP_ACTION_DESC,keystroke24);
		}
		
		if (mappings.get(PAGE_DOWN_ACTION) == null)
		{
			Keystroke keystroke25 = new Keystroke(CTRL,"V");
			setKeyMap(EMACS,PAGE_DOWN_ACTION,PAGE_DOWN_ACTION_DESC,keystroke25);
		}
		
		if (mappings.get(BEGIN_LINE_ACTION) == null)
		{
			Keystroke keystroke26 = new Keystroke(CTRL,"A");
			setKeyMap(EMACS,BEGIN_LINE_ACTION,BEGIN_LINE_ACTION_DESC,keystroke26);
		}
		
		if (mappings.get(END_LINE_ACTION) == null)
		{
			Keystroke keystroke27 = new Keystroke(CTRL,"E");
			setKeyMap(EMACS,END_LINE_ACTION,END_LINE_ACTION_DESC,keystroke27);
		}
		
		if (mappings.get(BEGIN_ACTION) == null)
		{
			Keystroke keystroke28 = new Keystroke(ALT,"A");
			setKeyMap(EMACS,BEGIN_ACTION,BEGIN_ACTION_DESC,keystroke28);
		}
		
		if (mappings.get(END_ACTION) == null)
		{
			Keystroke keystroke29 = new Keystroke(ALT,"E");
			setKeyMap(EMACS,END_ACTION,END_ACTION_DESC,keystroke29);
		}
		
		if (mappings.get(NEXT_WORD_ACTION) == null)
		{
			Keystroke keystroke30 = new Keystroke(ALT,"F");
			setKeyMap(EMACS,NEXT_WORD_ACTION,NEXT_WORD_ACTION_DESC,keystroke30);
		}
		
		if (mappings.get(PREVIOUS_WORD_ACTION) == null)
		{
			Keystroke keystroke31 = new Keystroke(ALT,"B");
			setKeyMap(EMACS,PREVIOUS_WORD_ACTION,PREVIOUS_WORD_ACTION_DESC,keystroke31);
		}
		
		if (mappings.get(DELETE_NEXT_CHAR_ACTION) == null)
		{
			Keystroke keystroke32 = new Keystroke(CTRL,"D");
			setKeyMap(EMACS,DELETE_NEXT_CHAR_ACTION,DELETE_NEXT_CHAR_ACTION_DESC,keystroke32);
		}
		
		if (mappings.get(DELETE_PREV_CHAR_ACTION) == null)
		{
			Keystroke keystroke33 = new Keystroke(null,"BACKSPACE");
			setKeyMap(EMACS,DELETE_PREV_CHAR_ACTION,DELETE_PREV_CHAR_ACTION_DESC,keystroke33);
		}
		
		if (mappings.get(WELL_FORMEDNESS_ACTION) == null)
		{
			Keystroke keystroke34 = new Keystroke(null,"F5");
			setKeyMap(EMACS,WELL_FORMEDNESS_ACTION,WELL_FORMEDNESS_ACTION_DESC,keystroke34);
		}
		
		if (mappings.get(VALIDATE_ACTION) == null)
		{
			Keystroke keystroke35 = new Keystroke(null,"F7");
			setKeyMap(EMACS,VALIDATE_ACTION,VALIDATE_ACTION_DESC,keystroke35);
		}
		
		if (mappings.get(START_BROWSER_ACTION) == null)
		{
			Keystroke keystroke36 = new Keystroke(null,"F9");
			setKeyMap(EMACS,START_BROWSER_ACTION,START_BROWSER_ACTION_DESC,keystroke36);
		}
		
		if (mappings.get(NEW_DOCUMENT_ACTION) == null)
		{
			Keystroke keystroke37 = new Keystroke(CTRL,"X");
			Keystroke keystroke37x = new Keystroke(CTRL,"N");
			setKeyMap(EMACS,NEW_DOCUMENT_ACTION,NEW_DOCUMENT_ACTION_DESC,keystroke37,keystroke37x);
		}
		
		if (mappings.get(REDO_ACTION) == null)
		{
			Keystroke keystroke38 = new Keystroke(CTRL,"X");
			Keystroke keystroke38x = new Keystroke(null,"R");
			setKeyMap(EMACS,REDO_ACTION,REDO_ACTION_DESC,keystroke38,keystroke38x);
		}
		
		if (mappings.get(SELECT_ELEMENT_ACTION) == null)
		{
			Keystroke keystroke39 = new Keystroke(CTRL,"X");
			Keystroke keystroke39x = new Keystroke(null,"E");
			setKeyMap(EMACS,SELECT_ELEMENT_ACTION,SELECT_ELEMENT_ACTION_DESC,keystroke39,keystroke39x);
		}
		
		if (mappings.get(SELECT_ELEMENT_CONTENT_ACTION) == null)
		{
			Keystroke keystroke40 = new Keystroke(CTRL,"X");
			Keystroke keystroke40x = new Keystroke(null,"C");
			setKeyMap(EMACS,SELECT_ELEMENT_CONTENT_ACTION,SELECT_ELEMENT_CONTENT_ACTION_DESC,keystroke40,keystroke40x);
		}
		
		if (mappings.get(INSERT_SPECIAL_CHAR_ACTION) == null)
		{
			Keystroke keystroke41 = new Keystroke(CTRL,"I");
			setKeyMap(EMACS,INSERT_SPECIAL_CHAR_ACTION,INSERT_SPECIAL_CHAR_ACTION,keystroke41);
		}
		
		if (mappings.get(TAG_ACTION) == null)
		{
			Keystroke keystroke42 = new Keystroke(CTRL,"T");
			setKeyMap(EMACS,TAG_ACTION,TAG_ACTION_DESC,keystroke42);
		}
		
		if (mappings.get(REPEAT_TAG_ACTION) == null)
		{
			Keystroke keystroke42 = new Keystroke(CTRLSHIFT,"T");
			setKeyMap(EMACS,REPEAT_TAG_ACTION,REPEAT_TAG_ACTION_DESC,keystroke42);
		}

		if (mappings.get(TOGGLE_EMPTY_ELEMENT_ACTION) == null)
		{
			Keystroke keystroke44 = new Keystroke(CTRL,"L");
			setKeyMap(EMACS,TOGGLE_EMPTY_ELEMENT_ACTION,TOGGLE_EMPTY_ELEMENT_ACTION_DESC,keystroke44);
		}

		if (mappings.get(RENAME_ELEMENT_ACTION) == null)
		{
			Keystroke keystroke44 = new Keystroke(CTRL,"R");
			setKeyMap(EMACS,RENAME_ELEMENT_ACTION,RENAME_ELEMENT_ACTION_DESC,keystroke44);
		}

		if (mappings.get(GOTO_START_TAG_ACTION) == null)
		{
			Keystroke keystroke43 = new Keystroke(CTRL,"UP");
			setKeyMap(EMACS,GOTO_START_TAG_ACTION,GOTO_START_TAG_ACTION_DESC,keystroke43);
		}
		
		if (mappings.get(GOTO_END_TAG_ACTION) == null)
		{
			Keystroke keystroke44 = new Keystroke(CTRL,"DOWN");
			setKeyMap(EMACS,GOTO_END_TAG_ACTION,GOTO_END_TAG_ACTION_DESC,keystroke44);
		}
		
		if (mappings.get(GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION) == null)
		{
			Keystroke keystroke44 = new Keystroke(CTRLSHIFT,"UP");
			setKeyMap(EMACS,GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION,GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION_DESC,keystroke44);
		}

		if (mappings.get(GOTO_NEXT_ATTRIBUTE_VALUE_ACTION) == null)
		{
			Keystroke keystroke44 = new Keystroke(CTRLSHIFT,"DOWN");
			setKeyMap(EMACS,GOTO_NEXT_ATTRIBUTE_VALUE_ACTION,GOTO_NEXT_ATTRIBUTE_VALUE_ACTION_DESC,keystroke44);
		}

		if (mappings.get(TOGGLE_BOOKMARK_ACTION) == null)
		{
			Keystroke keystroke45 = new Keystroke(CTRL,"M");
			setKeyMap(EMACS,TOGGLE_BOOKMARK_ACTION,TOGGLE_BOOKMARK_ACTION_DESC,keystroke45);
		}
		
		if (mappings.get(SELECT_BOOKMARK_ACTION) == null)
		{
			Keystroke keystroke46 = new Keystroke(ALT,"M");
			setKeyMap(EMACS,SELECT_BOOKMARK_ACTION,SELECT_BOOKMARK_ACTION_DESC,keystroke46);
		}
		
		if (mappings.get(SCHEMA_ACTION) == null)
		{
			Keystroke keystroke47 = new Keystroke(CTRL,"1");
			setKeyMap(EMACS,SCHEMA_ACTION,SCHEMA_ACTION_DESC,keystroke47);
		}
		
		if (mappings.get(OUTLINER_ACTION) == null)
		{
			Keystroke keystroke48 = new Keystroke(CTRL,"2");
			setKeyMap(EMACS,OUTLINER_ACTION,OUTLINER_ACTION_DESC,keystroke48);
		}
		
		if (mappings.get(EDITOR_ACTION) == null)
		{
			Keystroke keystroke49 = new Keystroke(CTRL,"3");
			setKeyMap(EMACS,EDITOR_ACTION,EDITOR_ACTION_DESC,keystroke49);
		}
		
		if (mappings.get(OPEN_ACTION) == null)
		{
			Keystroke keystroke50 = new Keystroke(CTRL,"4");
			setKeyMap(EMACS,VIEWER_ACTION,VIEWER_ACTION_DESC,keystroke50);
		}
		
		//Keystroke keystroke51 = new Keystroke(CTRL,"5");
		//setKeyMap(EMACS,BROWSER_ACTION,BROWSER_ACTION_DESC,keystroke51);
		
		if (mappings.get(ADD_ELEMENT_OUTLINER_ACTION) == null)
		{
			Keystroke keystroke52 = new Keystroke(null,"ENTER");
			setKeyMap(EMACS,ADD_ELEMENT_OUTLINER_ACTION,ADD_ELEMENT_OUTLINER_ACTION_DESC,keystroke52);
		}
		
		if (mappings.get(DELETE_ELEMENT_OUTLINER_ACTION) == null)
		{
			Keystroke keystroke53 = new Keystroke(null,"DELETE");
			setKeyMap(EMACS,DELETE_ELEMENT_OUTLINER_ACTION,DELETE_ELEMENT_OUTLINER_ACTION_DESC,keystroke53);
		}
		
		if (mappings.get(OPEN_REMOTE_ACTION) == null)
		{
			Keystroke keystroke54 = new Keystroke(null,null);
			setKeyMap(EMACS,OPEN_REMOTE_ACTION,OPEN_REMOTE_ACTION_DESC,keystroke54);
		}
		
		if (mappings.get(RELOAD_ACTION) == null)
		{
			Keystroke keystroke55 = new Keystroke(null,null);
			setKeyMap(EMACS,RELOAD_ACTION,RELOAD_ACTION_DESC,keystroke55);
		}
		
		if (mappings.get(SAVE_AS_TEMPLATE_ACTION) == null)
		{
			Keystroke keystroke56 = new Keystroke(null,null);
			setKeyMap(EMACS,SAVE_AS_TEMPLATE_ACTION,SAVE_AS_TEMPLATE_ACTION_DESC,keystroke56);
		}
		
		if (mappings.get(MANAGE_TEMPLATE_ACTION) == null)
		{
			Keystroke keystroke57 = new Keystroke(null,null);
			setKeyMap(EMACS,MANAGE_TEMPLATE_ACTION,MANAGE_TEMPLATE_ACTION_DESC,keystroke57);
		}
		
		if (mappings.get(PAGE_SETUP_ACTION) == null)
		{
			Keystroke keystroke58 = new Keystroke(null,null);
			setKeyMap(EMACS,PAGE_SETUP_ACTION,PAGE_SETUP_ACTION_DESC,keystroke58);
		}
		
		if (mappings.get(PREFERENCES_ACTION) == null)
		{
			Keystroke keystroke59 = new Keystroke(null,null);
			setKeyMap(EMACS,PREFERENCES_ACTION,PREFERENCES_ACTION_DESC,keystroke59);
		}
		
		if (mappings.get(CREATE_REQUIRED_NODE_ACTION) == null)
		{
			Keystroke keystroke60 = new Keystroke(null,null);
			setKeyMap(EMACS,CREATE_REQUIRED_NODE_ACTION,CREATE_REQUIRED_NODE_ACTION_DESC,keystroke60);
		}
		
		if (mappings.get(SPLIT_ELEMENT_ACTION) == null)
		{
			Keystroke keystroke61 = new Keystroke(null,null);
			setKeyMap(EMACS,SPLIT_ELEMENT_ACTION,SPLIT_ELEMENT_ACTION_DESC,keystroke61);}
		
		
		if (mappings.get(CONVERT_ENTITIES_ACTION) == null)
		{
			Keystroke keystroke62 = new Keystroke(null,null);
			setKeyMap(EMACS,CONVERT_ENTITIES_ACTION,CONVERT_ENTITIES_ACTION_DESC,keystroke62);
		}
		
		if (mappings.get(CONVERT_CHARACTERS_ACTION) == null)
		{
			Keystroke keystroke63 = new Keystroke(null,null);
			setKeyMap(EMACS,CONVERT_CHARACTERS_ACTION,CONVERT_CHARACTERS_ACTION_DESC,keystroke63);
		}
		
		if (mappings.get(STRIP_TAG_ACTION) == null)
		{
			Keystroke keystroke64 = new Keystroke(null,null);
			setKeyMap(EMACS,STRIP_TAG_ACTION,STRIP_TAG_ACTION_DESC,keystroke64);
		}
		
		if (mappings.get(ADD_CDATA_ACTION) == null)
		{
			Keystroke keystroke65 = new Keystroke(null,null);
			setKeyMap(EMACS,ADD_CDATA_ACTION,ADD_CDATA_ACTION_DESC,keystroke65);
		}
		
		if (mappings.get(LOCK_ACTION) == null)
		{
			Keystroke keystroke66 = new Keystroke(null,null);
			setKeyMap(EMACS,LOCK_ACTION,LOCK_ACTION_DESC,keystroke66);
		}
		
		if (mappings.get(FORMAT_ACTION) == null)
		{
			Keystroke keystroke67 = new Keystroke(null,null);
			setKeyMap(EMACS,FORMAT_ACTION,FORMAT_ACTION_DESC,keystroke67);
		}
		
		if (mappings.get(EXPAND_ALL_ACTION) == null)
		{
			Keystroke keystroke68 = new Keystroke(null,null);
			setKeyMap(EMACS,EXPAND_ALL_ACTION,EXPAND_ALL_ACTION_DESC,keystroke68);
		}
		
		if (mappings.get(COLLAPSE_ALL_ACTION) == null)
		{
			Keystroke keystroke69 = new Keystroke(null,null);
			setKeyMap(EMACS,COLLAPSE_ALL_ACTION,COLLAPSE_ALL_ACTION_DESC,keystroke69);
		}
		
		if (mappings.get(SYNCHRONISE_ACTION) == null)
		{
			Keystroke keystroke70 = new Keystroke(null,null);
			setKeyMap(EMACS,SYNCHRONISE_ACTION,SYNCHRONISE_ACTION_DESC,keystroke70);
		}
		
		if (mappings.get(TOGGLE_FULL_ACTION) == null)
		{
			Keystroke keystroke71 = new Keystroke(null,null);
			setKeyMap(EMACS,TOGGLE_FULL_ACTION,TOGGLE_FULL_ACTION_DESC,keystroke71);
		}
		
		if (mappings.get(NEW_PROJECT_ACTION) == null)
		{
			Keystroke keystroke72 = new Keystroke(null,null);
			setKeyMap(EMACS,NEW_PROJECT_ACTION,NEW_PROJECT_ACTION_DESC,keystroke72);
		}
		
		if (mappings.get(IMPORT_PROJECT_ACTION) == null)
		{
			Keystroke keystroke73 = new Keystroke(null,null);
			setKeyMap(EMACS,IMPORT_PROJECT_ACTION,IMPORT_PROJECT_ACTION_DESC,keystroke73);
		}
		
		if (mappings.get(DELETE_PROJECT_ACTION) == null)
		{
			Keystroke keystroke74 = new Keystroke(null,null);
			setKeyMap(EMACS,DELETE_PROJECT_ACTION,DELETE_PROJECT_ACTION_DESC,keystroke74);
		}
	
		if (mappings.get(RENAME_PROJECT_ACTION) == null)
		{
			Keystroke keystroke75 = new Keystroke(null,null);
			setKeyMap(EMACS,RENAME_PROJECT_ACTION,RENAME_PROJECT_ACTION_DESC,keystroke75);
		}
	
		if (mappings.get(CHECK_WELLFORMEDNESS_ACTION) == null)
		{
			Keystroke keystroke76 = new Keystroke(null,null);
			setKeyMap(EMACS,CHECK_WELLFORMEDNESS_ACTION,CHECK_WELLFORMEDNESS_ACTION,keystroke76);
		}
	
		if (mappings.get(VALIDATE_PROJECT_ACTION) == null)
		{
			Keystroke keystroke77 = new Keystroke(null,null);
			setKeyMap(EMACS,VALIDATE_PROJECT_ACTION,VALIDATE_PROJECT_ACTION_DESC,keystroke77);
		}
		
		if (mappings.get(FIND_IN_FILES_ACTION) == null)
		{
			Keystroke keystroke78 = new Keystroke(null,null);
			setKeyMap(EMACS,FIND_IN_FILES_ACTION,FIND_IN_FILES_ACTION_DESC,keystroke78);
		}
		
		if (mappings.get(FIND_IN_PROJECTS_ACTION) == null)
		{
			Keystroke keystroke78 = new Keystroke(null,null);
			setKeyMap(EMACS,FIND_IN_PROJECTS_ACTION,FIND_IN_PROJECTS_ACTION_DESC,keystroke78);
		}
	
		if (mappings.get(ADD_FILE_ACTION) == null)
		{
			Keystroke keystroke79 = new Keystroke(null,null);
			setKeyMap(EMACS,ADD_FILE_ACTION,ADD_FILE_ACTION_DESC,keystroke79);
		}
	
		if (mappings.get(ADD_REMOTE_FILE_ACTION) == null)
		{
			Keystroke keystroke80 = new Keystroke(null,null);
			setKeyMap(EMACS,ADD_REMOTE_FILE_ACTION,ADD_REMOTE_FILE_ACTION_DESC,keystroke80);
		}
	
		if (mappings.get(REMOVE_FILE_ACTION) == null)
		{
			Keystroke keystroke81 = new Keystroke(null,null);
			setKeyMap(EMACS,REMOVE_FILE_ACTION,REMOVE_FILE_ACTION_DESC,keystroke81);
		}

		if (mappings.get(ADD_DIRECTORY_ACTION) == null)
		{
			Keystroke keystroke82 = new Keystroke(null,null);
			setKeyMap(EMACS,ADD_DIRECTORY_ACTION,ADD_DIRECTORY_ACTION_DESC,keystroke82);
		}
	
		if (mappings.get(ADD_DIRECTORY_CONTENTS_ACTION) == null)
		{
			Keystroke keystroke83 = new Keystroke(null,null);
			setKeyMap(EMACS,ADD_DIRECTORY_CONTENTS_ACTION,ADD_DIRECTORY_CONTENTS_ACTION_DESC,keystroke83);
		}

		if (mappings.get(ADD_FOLDER_ACTION) == null)
		{
			Keystroke keystroke84 = new Keystroke(null,null);
			setKeyMap(EMACS,ADD_FOLDER_ACTION,ADD_FOLDER_ACTION_DESC,keystroke84);
		}	
		
		if (mappings.get(REMOVE_FOLDER_ACTION) == null)
		{
	    	Keystroke keystroke85 = new Keystroke(null,null);
			setKeyMap(EMACS,REMOVE_FOLDER_ACTION,REMOVE_FOLDER_ACTION_DESC,keystroke85);
		}
	
		if (mappings.get(RENAME_FOLDER_ACTION) == null)
		{
   			Keystroke keystroke86 = new Keystroke(null,null);
			setKeyMap(EMACS,RENAME_FOLDER_ACTION,RENAME_FOLDER_ACTION_DESC,keystroke86);
		}
		
		if (mappings.get(VALIDATE_XML_SCHEMA_ACTION) == null)
		{
			Keystroke keystroke87 = new Keystroke(null,null);
			setKeyMap(EMACS,VALIDATE_XML_SCHEMA_ACTION,VALIDATE_XML_SCHEMA_ACTION_DESC,keystroke87);
		}
		
		if (mappings.get(VALIDATE_DTD_ACTION) == null)
		{
			Keystroke keystroke88 = new Keystroke(null,null);
			setKeyMap(EMACS,VALIDATE_DTD_ACTION,VALIDATE_DTD_ACTION_DESC,keystroke88);
		}
	
		if (mappings.get(VALIDATE_RELAXNG_ACTION) == null)
		{
			Keystroke keystroke89 = new Keystroke(null,null);
			setKeyMap(EMACS,VALIDATE_RELAXNG_ACTION,VALIDATED_RELAXNG_ACTION_DESC,keystroke89);
		}
	
		if (mappings.get(SET_XML_DECLARATION_ACTION) == null)
		{
			Keystroke keystroke90 = new Keystroke(null,null);
			setKeyMap(EMACS,SET_XML_DECLARATION_ACTION,SET_XML_DECLARATION_ACTION_DESC,keystroke90);
		}
	
		if (mappings.get(SET_DOCTYPE_DECLARATION_ACTION) == null)
		{
			Keystroke keystroke91 = new Keystroke(null,null);
			setKeyMap(EMACS,SET_DOCTYPE_DECLARATION_ACTION,SET_DOCTYPE_DECLARATION_ACTION_DESC,keystroke91);
		}
	
		if (mappings.get(SET_SCHEMA_LOCATION_ACTION) == null)
		{
			Keystroke keystroke92 = new Keystroke(null,null);
			setKeyMap(EMACS,SET_SCHEMA_LOCATION_ACTION,SET_SCHEMA_LOCATION_ACTION_DESC,keystroke92);
		}
	
		if (mappings.get(RESOLVE_XINCLUDES_ACTION) == null)
		{
			Keystroke keystroke93 = new Keystroke(null,null);	
			setKeyMap(EMACS,RESOLVE_XINCLUDES_ACTION,RESOLVE_XINCLUDES_ACTION_DESC,keystroke93);
		}
		
		if (mappings.get(SET_SCHEMA_PROPS_ACTION) == null)
		{
			Keystroke keystroke94 = new Keystroke(null,null);
			setKeyMap(EMACS,SET_SCHEMA_PROPS_ACTION,SET_SCHEMA_PROPS_ACTION_DESC,keystroke94);
		}
		
		if (mappings.get(INFER_SCHEMA_ACTION) == null)
		{
			Keystroke keystroke95 = new Keystroke(null,null);
			setKeyMap(EMACS,INFER_SCHEMA_ACTION,INFER_SCHEMA_ACTION_DESC,keystroke95);
		}
		
		if (mappings.get(CREATE_TYPE_ACTION) == null)
		{
			Keystroke keystroke96 = new Keystroke(null,null);
			setKeyMap(EMACS,CREATE_TYPE_ACTION,CREATE_TYPE_ACTION_DESC,keystroke96);
		}
	
		if (mappings.get(SET_TYPE_ACTION) == null)
		{
			Keystroke keystroke97 = new Keystroke(null,null);
			setKeyMap(EMACS,SET_TYPE_ACTION,SET_TYPE_ACTION_DESC,keystroke97);
		}
	
		if (mappings.get(TYPE_PROPERTIES_ACTION) == null)
		{
			Keystroke keystroke98 = new Keystroke(null,null);
			setKeyMap(EMACS,TYPE_PROPERTIES_ACTION,TYPE_PROPERTIES_ACTION_DESC,keystroke98);
		}
	
		if (mappings.get(MANAGE_TYPES_ACTION) == null)
		{
			Keystroke keystroke99 = new Keystroke(null,null);
			setKeyMap(EMACS,MANAGE_TYPES_ACTION,MANAGE_TYPES_ACTION_DESC,keystroke99);
		}
	
		if (mappings.get(CONVERT_SCHEMA_ACTION) == null)
		{
			Keystroke keystroke100 = new Keystroke(null,null);
			setKeyMap(EMACS,CONVERT_SCHEMA_ACTION,CONVERT_SCHEMA_ACTION_DESC,keystroke100);
		}

		if (mappings.get(EXECUTE_SIMPLE_XSLT_ACTION) == null)
		{
			Keystroke keystroke201 = new Keystroke(null,null);
			setKeyMap(DEFAULT,EXECUTE_SIMPLE_XSLT_ACTION,EXECUTE_SIMPLE_XSLT_ACTION_DESC,keystroke201);	
		}
		

		if (mappings.get(EXECUTE_ADVANCED_XSLT_ACTION) == null)
		{
			Keystroke keystroke101 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_ADVANCED_XSLT_ACTION,EXECUTE_ADVANCED_XSLT_ACTION_DESC,keystroke101);
		}
		
		if (mappings.get(EXECUTE_PREVIOUS_XSLT_ACTION) == null)
		{
			Keystroke keystroke101 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_PREVIOUS_XSLT_ACTION,EXECUTE_PREVIOUS_XSLT_ACTION_DESC,keystroke101);
		}

		if (mappings.get(EXECUTE_FO_ACTION) == null)
		{
			Keystroke keystroke102 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_FO_ACTION,EXECUTE_FO_ACTION_DESC,keystroke102);
		}
	
		if (mappings.get(EXECUTE_PREVIOUS_FO_ACTION) == null)
		{
			Keystroke keystroke102 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_PREVIOUS_FO_ACTION,EXECUTE_PREVIOUS_FO_ACTION_DESC,keystroke102);
		}

		if (mappings.get(EXECUTE_XQUERY_ACTION) == null)
		{
			Keystroke keystroke103 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_XQUERY_ACTION,EXECUTE_XQUERY_ACTION_DESC,keystroke103);
		}
	
		if (mappings.get(EXECUTE_PREVIOUS_XQUERY_ACTION) == null)
		{
			Keystroke keystroke103 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_PREVIOUS_XQUERY_ACTION,EXECUTE_PREVIOUS_XQUERY_ACTION_DESC,keystroke103);
		}

		if (mappings.get(EXECUTE_SCENARIO_ACTION) == null)
		{
			Keystroke keystroke104 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_SCENARIO_ACTION,EXECUTE_SCENARIO_ACTION_DESC,keystroke104);
		}
	
		if (mappings.get(EXECUTE_PREVIOUS_SCENARIO_ACTION) == null)
		{
			Keystroke keystroke104 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_PREVIOUS_SCENARIO_ACTION,EXECUTE_PREVIOUS_SCENARIO_ACTION_DESC,keystroke104);
		}

		if (mappings.get(XSLT_DEBUGGER_ACTION) == null)
		{
			Keystroke keystroke105 = new Keystroke(null,null);
			setKeyMap(EMACS,XSLT_DEBUGGER_ACTION,XSLT_DEBUGGER_ACTION_DESC,keystroke105);
		}

		if (mappings.get(MANAGE_SCENARIOS_ACTION) == null)
		{
			Keystroke keystroke106 = new Keystroke(null,null);
			setKeyMap(EMACS,MANAGE_SCENARIOS_ACTION,MANAGE_SCENARIOS_ACTION_DESC,keystroke106);
		}
		
		if (mappings.get(CANONICALIZE_ACTION) == null)
		{
			Keystroke keystroke107 = new Keystroke(null,null);
			setKeyMap(EMACS,CANONICALIZE_ACTION,CANONICALIZE_ACTION_DESC,keystroke107);
		}
		
		if (mappings.get(SIGN_DOCUMENT_ACTION) == null)
		{
			Keystroke keystroke108 = new Keystroke(null,null);
			setKeyMap(EMACS,SIGN_DOCUMENT_ACTION,SIGN_DOCUMENT_ACTION_DESC,keystroke108);
		}
		
		if (mappings.get(VERIFY_SIGNATURE_ACTION) == null)
		{
			Keystroke keystroke109 = new Keystroke(null,null);
			setKeyMap(EMACS,VERIFY_SIGNATURE_ACTION,VERIFY_SIGNATURE_ACTION_DESC,keystroke109);
		}
		
		if (mappings.get(SHOW_SVG_ACTION) == null)
		{
			Keystroke keystroke110 = new Keystroke(null,null);
			setKeyMap(EMACS,SHOW_SVG_ACTION,SHOW_SVG_ACTION_DESC,keystroke110);
		}
		
		if (mappings.get(CONVERT_SVG_ACTION) == null)
		{
			Keystroke keystroke111 = new Keystroke(null,null);
			setKeyMap(EMACS,CONVERT_SVG_ACTION,CONVERT_SVG_ACTION_DESC,keystroke111);
		}
		
		if (mappings.get(SEND_SOAP_MESSAGE_ACTION) == null)
		{
			Keystroke keystroke112 = new Keystroke(null,null);
			setKeyMap(EMACS,SEND_SOAP_MESSAGE_ACTION,SEND_SOAP_MESSAGE_ACTION_DESC,keystroke112);
		}

		if (mappings.get(ANALYSE_WSDL_ACTION) == null)
		{
			Keystroke keystroke113 = new Keystroke(null,null);
			setKeyMap(EMACS,ANALYSE_WSDL_ACTION,ANALYSE_WSDL_ACTION_DESC,keystroke113);
		}

		if (mappings.get(CLEAN_UP_HTML_ACTION) == null)
		{
			Keystroke keystroke114 = new Keystroke(null,null);
			setKeyMap(EMACS,CLEAN_UP_HTML_ACTION,CLEAN_UP_HTML_ACTION_DESC,keystroke114);
		}
		
		if (mappings.get(IMPORT_FROM_TEXT_ACTION) == null)
		{
			Keystroke keystroke115 = new Keystroke(null,null);
			setKeyMap(EMACS,IMPORT_FROM_TEXT_ACTION,IMPORT_FROM_TEXT_ACTION_DESC,keystroke115);
		}
		
		if (mappings.get(IMPORT_FROM_EXCEL_ACTION) == null)
		{
			Keystroke keystroke116 = new Keystroke(null,null);
			setKeyMap(EMACS,IMPORT_FROM_EXCEL_ACTION,IMPORT_FROM_EXCEL_ACTION_DESC,keystroke116);
		}
		
		if (mappings.get(IMPORT_FROM_DBTABLE_ACTION) == null)
		{
			Keystroke keystroke117 = new Keystroke(null,null);
			setKeyMap(EMACS,IMPORT_FROM_DBTABLE_ACTION,IMPORT_FROM_DBTABLE_ACTION_DESC,keystroke117);
		}

		if (mappings.get(SELECT_FRAGMENT_ACTION) == null)
		{
			Keystroke keystroke118 = new Keystroke(CTRLSHIFT,"SPACE");
			setKeyMap(EMACS,SELECT_FRAGMENT_ACTION,SELECT_FRAGMENT_ACTION_DESC,keystroke118);
		}
		
		if (mappings.get(TOOLS_EMPTY_DOCUMENT_ACTION) == null)
		{
			Keystroke keystroke119 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_EMPTY_DOCUMENT_ACTION,TOOLS_EMPTY_DOCUMENT_ACTION_DESC,keystroke119);
		}
		
		if (mappings.get(TOOLS_CAPITALIZE_ACTION) == null)
		{
			Keystroke keystroke120 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_CAPITALIZE_ACTION,TOOLS_CAPITALIZE_ACTION_DESC,keystroke120);
		}
		
		if (mappings.get(TOOLS_DECAPITALIZE_ACTION) == null)
		{
			Keystroke keystroke121 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_DECAPITALIZE_ACTION,TOOLS_DECAPITALIZE_ACTION_DESC,keystroke121);
		}
		
		if (mappings.get(TOOLS_LOWERCASE_ACTION) == null)
		{
			Keystroke keystroke122 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_LOWERCASE_ACTION,TOOLS_LOWERCASE_ACTION_DESC,keystroke122);
		}
		
		if (mappings.get(TOOLS_UPPERCASE_ACTION) == null)
		{
			Keystroke keystroke123 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_UPPERCASE_ACTION,TOOLS_UPPERCASE_ACTION_DESC,keystroke123);
		}
		
		if (mappings.get(IMPORT_FROM_SQLXML_ACTION) == null)
		{
			Keystroke keystroke124 = new Keystroke(null,null);
			setKeyMap(EMACS,IMPORT_FROM_SQLXML_ACTION,IMPORT_FROM_SQLXML_ACTION_DESC,keystroke124);
		}
		
		if (mappings.get(TOOLS_MOVE_NS_TO_ROOT_ACTION) == null)
		{
			Keystroke keystroke125 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_MOVE_NS_TO_ROOT_ACTION,TOOLS_MOVE_NS_TO_ROOT_ACTION_DESC,keystroke125);
		}
		
		if (mappings.get(TOOLS_MOVE_NS_TO_FIRST_USED_ACTION) == null)
		{
			Keystroke keystroke126 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_MOVE_NS_TO_FIRST_USED_ACTION,TOOLS_MOVE_NS_TO_FIRST_USED_ACTION_DESC,keystroke126);
		}
		
		if (mappings.get(TOOLS_CHANGE_NS_PREFIX_ACTION) == null)
		{
			Keystroke keystroke127 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_CHANGE_NS_PREFIX_ACTION,TOOLS_CHANGE_NS_PREFIX_ACTION_DESC,keystroke127);
		}
		
		if (mappings.get(TOOLS_RENAME_NODE_ACTION) == null)
		{
			Keystroke keystroke128 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_RENAME_NODE_ACTION,TOOLS_RENAME_NODE_ACTION_DESC,keystroke128);
		}
		
		if (mappings.get(TOOLS_REMOVE_NODE_ACTION) == null)
		{
			Keystroke keystroke129 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_REMOVE_NODE_ACTION,TOOLS_REMOVE_NODE_ACTION_DESC,keystroke129);
		}
		
		if (mappings.get(TOOLS_ADD_NODE_TO_NS_ACTION) == null)
		{
			Keystroke keystroke130 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_ADD_NODE_TO_NS_ACTION,TOOLS_ADD_NODE_TO_NS_ACTION_DESC,keystroke130);
		}
		
		if (mappings.get(TOOLS_SET_NODE_VALUE_ACTION) == null)
		{
			Keystroke keystroke131 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_SET_NODE_VALUE_ACTION,TOOLS_SET_NODE_VALUE_ACTION_DESC,keystroke131);
		}
		
		if (mappings.get(TOOLS_ADD_NODE_ACTION) == null)
		{
			Keystroke keystroke132 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_ADD_NODE_ACTION,TOOLS_ADD_NODE_ACTION_DESC,keystroke132);
		}

		if (mappings.get(TOOLS_REMOVE_UNUSED_NS_ACTION) == null)
		{
			Keystroke keystroke133 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_REMOVE_UNUSED_NS_ACTION,TOOLS_REMOVE_UNUSED_NS_ACTION_DESC,keystroke133);
		}
		
		if (mappings.get(TOOLS_CONVERT_NODE_ACTION) == null)
		{
			Keystroke keystroke134 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_CONVERT_NODE_ACTION,TOOLS_CONVERT_NODE_ACTION_DESC,keystroke134);
		}
		
		if (mappings.get(TOOLS_SORT_NODE_ACTION) == null)
		{
			Keystroke keystroke135 = new Keystroke(null,null);
			setKeyMap(EMACS,TOOLS_SORT_NODE_ACTION,TOOLS_SORT_NODE_ACTION_DESC,keystroke135);
		}
		
		if (mappings.get(XDIFF_ACTION) == null)
		{
			Keystroke keystroke136Xdiff = new Keystroke(null,null);
			setKeyMap(EMACS,XDIFF_ACTION,XDIFF_ACTION_DESC,keystroke136Xdiff);
		}
		
		if (mappings.get(DEBUGGER_NEW_TRANSFORMATION_ACTION) == null)
		{
			Keystroke keystroke137Debugger = new Keystroke(CTRL,"N");
			setKeyMap(EMACS,DEBUGGER_NEW_TRANSFORMATION_ACTION,DEBUGGER_NEW_TRANSFORMATION_ACTION_DESC,keystroke137Debugger);
		}
		
		if (mappings.get(DEBUGGER_CLOSE_ACTION) == null)
		{
			Keystroke keystroke138Close = new Keystroke(CTRL,"W");
			setKeyMap(EMACS,DEBUGGER_CLOSE_ACTION,DEBUGGER_CLOSE_ACTION_DESC,keystroke138Close);
		}
		
		if (mappings.get(DEBUGGER_OPEN_SCENARIO_ACTION) == null)
		{
			Keystroke keystroke139OpenScenario = new Keystroke(CTRL,"O");
			setKeyMap(EMACS,DEBUGGER_OPEN_SCENARIO_ACTION,DEBUGGER_OPEN_SCENARIO_ACTION_DESC,keystroke139OpenScenario);
		}
		
		if (mappings.get(DEBUGGER_CLOSE_TRANSFORMATION_ACTION) == null)
		{
			Keystroke keystroke140CloseTrans = new Keystroke(CTRLSHIFT,"W");
			setKeyMap(EMACS,DEBUGGER_CLOSE_TRANSFORMATION_ACTION,DEBUGGER_CLOSE_TRANSFORMATION_ACTION_DESC,keystroke140CloseTrans);
		}
		
		if (mappings.get(DEBUGGER_SAVE_AS_SCENARIO_ACTION) == null)
		{
			Keystroke keystroke141SaveScenario = new Keystroke(CTRL,"S");
			setKeyMap(EMACS,DEBUGGER_SAVE_AS_SCENARIO_ACTION,DEBUGGER_SAVE_AS_SCENARIO_ACTION_DESC,keystroke141SaveScenario);
		}
		
		if (mappings.get(DEBUGGER_FIND_ACTION) == null)
		{
			Keystroke keystroke142DebuggerFind = new Keystroke(CTRL,"F");
			setKeyMap(EMACS,DEBUGGER_FIND_ACTION,DEBUGGER_FIND_ACTION_DESC,keystroke142DebuggerFind);
		}
		
		if (mappings.get(DEBUGGER_FIND_NEXT_ACTION) == null)
		{
			Keystroke keystroke143DebuggerFindNext = new Keystroke(null,"F3");
			setKeyMap(EMACS,DEBUGGER_FIND_NEXT_ACTION,DEBUGGER_FIND_NEXT_ACTION_DESC,keystroke143DebuggerFindNext );
		}
		
		if (mappings.get(DEBUGGER_GOTO_ACTION) == null)
		{
			Keystroke keystroke144DebuggerGoto = new Keystroke(CTRL,"G");
			setKeyMap(EMACS,DEBUGGER_GOTO_ACTION,DEBUGGER_GOTO_ACTION_DESC,keystroke144DebuggerGoto);
		}
		
		if (mappings.get(DEBUGGER_START_ACTION) == null)
		{
			Keystroke keystroke145DebugStart = new Keystroke(null,"F5");
			setKeyMap(EMACS,DEBUGGER_START_ACTION,DEBUGGER_START_ACTION_DESC,keystroke145DebugStart);
		}
		
		if (mappings.get(DEBUGGER_RUN_END_ACTION) == null)
		{
			Keystroke keystroke146DebugRunEnd = new Keystroke(CTRLSHIFT,"F5");
			setKeyMap(EMACS,DEBUGGER_RUN_END_ACTION,DEBUGGER_RUN_END_ACTION_DESC,keystroke146DebugRunEnd);
		}
		
		if (mappings.get(DEBUGGER_PAUSE_ACTION) == null)
		{
			Keystroke keystroke147DebugPause = new Keystroke(CTRL,"F5");
			setKeyMap(EMACS,DEBUGGER_PAUSE_ACTION,DEBUGGER_PAUSE_ACTION_DESC,keystroke147DebugPause);
		}
		
		if (mappings.get(DEBUGGER_STOP_ACTION) == null)
		{
			Keystroke keystroke148DebugStop = new Keystroke(SHIFT,"F5");
			setKeyMap(EMACS,DEBUGGER_STOP_ACTION,DEBUGGER_STOP_ACTION_DESC,keystroke148DebugStop);
		}
		
		if (mappings.get(DEBUGGER_STEP_INTO_ACTION) == null)
		{
			Keystroke keystroke149DebugStepInto = new Keystroke(null,"F11");
			setKeyMap(EMACS,DEBUGGER_STEP_INTO_ACTION,DEBUGGER_STEP_INTO_ACTION_DESC, keystroke149DebugStepInto);
		}
		
		if (mappings.get(DEBUGGER_STEP_OVER_ACTION) == null)
		{
			Keystroke keystroke150DebugStepOver = new Keystroke(null,"F10");
			setKeyMap(EMACS,DEBUGGER_STEP_OVER_ACTION,DEBUGGER_STEP_OVER_ACTION_DESC, keystroke150DebugStepOver );
		}
		
		if (mappings.get(DEBUGGER_STEP_OUT_ACTION) == null)
		{
			Keystroke keystroke151DebugStepOut = new Keystroke(SHIFT,"F11");
			setKeyMap(EMACS,DEBUGGER_STEP_OUT_ACTION,DEBUGGER_STEP_OUT_ACTION_DESC, keystroke151DebugStepOut  );
		}
		
		if (mappings.get(DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION) == null)
		{
			Keystroke keystroke152DebugRemoveAllBreakpoints = new Keystroke(CTRLSHIFT,"F9");
			setKeyMap(EMACS,DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION,DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION_DESC, keystroke152DebugRemoveAllBreakpoints);
		}
		
		if (mappings.get(DEBUGGER_OPEN_INPUT_ACTION) == null)
		{
			Keystroke keystroke153OpenInput = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_OPEN_INPUT_ACTION,DEBUGGER_OPEN_INPUT_ACTION_DESC,keystroke153OpenInput);
		}
		
		if (mappings.get(DEBUGGER_OPEN_STYLESHEET_ACTION) == null)
		{
			Keystroke keystroke154OpenStylesheet = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_OPEN_STYLESHEET_ACTION,DEBUGGER_OPEN_STYLESHEET_ACTION_DESC,keystroke154OpenStylesheet);
		}
		
		if (mappings.get(DEBUGGER_RELOAD_ACTION) == null)
		{
			Keystroke keystroke155DebugReload = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_RELOAD_ACTION,DEBUGGER_RELOAD_ACTION_DESC,keystroke155DebugReload);
		}
		
		if (mappings.get(DEBUGGER_EXIT_ACTION) == null)
		{
			Keystroke keystroke156DebugExit = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_EXIT_ACTION,DEBUGGER_EXIT_ACTION_DESC,keystroke156DebugExit);
		}
		
		if (mappings.get(DEBUGGER_COLLAPSE_ALL_ACTION) == null)
		{
			Keystroke keystroke157DebugCollapseAll = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_COLLAPSE_ALL_ACTION,DEBUGGER_COLLAPSE_ALL_ACTION_DESC,keystroke157DebugCollapseAll);
		}
		
		if (mappings.get(DEBUGGER_EXPAND_ALL_ACTION) == null)
		{
			Keystroke keystroke158DebugExpandAll = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_EXPAND_ALL_ACTION,DEBUGGER_EXPAND_ALL_ACTION_DESC,keystroke158DebugExpandAll);
			
		}
		
		if (mappings.get(DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION) == null)
		{
			Keystroke keystroke159DebugShowLine = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION,DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION_DESC, keystroke159DebugShowLine);
			
		}
		
		if (mappings.get(DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION) == null)
		{
			Keystroke keystroke160DebugShowOverview = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION,DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION_DESC, keystroke160DebugShowOverview);
			
		}
		
		if (mappings.get(DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION) == null)
		{
			Keystroke keystroke161DebugShowFolding = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION,DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION_DESC, keystroke161DebugShowFolding);
			
		}
		
		if (mappings.get(DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION) == null)
		{
			Keystroke keystroke162DebugSoftWrap = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION,DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION_DESC, keystroke162DebugSoftWrap);
			
		}
		
		if (mappings.get(DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION) == null)
		{
			Keystroke keystroke163DebugInputShowLine = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION,DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION_DESC, keystroke163DebugInputShowLine);
			
		}
		
		if (mappings.get(DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION) == null)
		{
			Keystroke keystroke164DebugInputShowOverview = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION,DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION_DESC, keystroke164DebugInputShowOverview);
			
		}
		
		if (mappings.get(DEBUGGER_INPUT_SHOW_FOLDING_ACTION) == null)
		{
			Keystroke keystroke165DebugInputShowFolding = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_INPUT_SHOW_FOLDING_ACTION,DEBUGGER_INPUT_SHOW_FOLDING_ACTION, keystroke165DebugInputShowFolding);
			
		}
		
		if (mappings.get(DEBUGGER_INPUT_SOFT_WRAPPING_ACTION) == null)
		{
			Keystroke keystroke166DebugInputSoftWrap = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_INPUT_SOFT_WRAPPING_ACTION,DEBUGGER_INPUT_SOFT_WRAPPING_ACTION_DESC, keystroke166DebugInputSoftWrap);
			
		}
		
		if (mappings.get(DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION) == null)
		{
			Keystroke keystroke167DebugOutputShowLine = new Keystroke(null,null);
			setKeyMap(EMACS,DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION,DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION_DESC, keystroke167DebugOutputShowLine);
			
		}
		
		if (mappings.get(DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION) == null)
	    {
			Keystroke keystroke168DebugOutputSoftWrap = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION,DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION_DESC, keystroke168DebugOutputSoftWrap);
		}
		
		if (mappings.get(DEBUGGER_AUTO_OPEN_INPUT_ACTION) == null)
	    {
			Keystroke keystroke169DebugAutoOpen = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_AUTO_OPEN_INPUT_ACTION,DEBUGGER_AUTO_OPEN_INPUT_ACTION_DESC, keystroke169DebugAutoOpen);
		}
		
		if (mappings.get(DEBUGGER_ENABLE_TRACING_ACTION) == null)
	    {
			Keystroke keystroke170DebugEnableTracing = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_ENABLE_TRACING_ACTION,DEBUGGER_ENABLE_TRACING_ACTION_DESC, keystroke170DebugEnableTracing);
		}
		
		if (mappings.get(DEBUGGER_REDIRECT_OUTPUT_ACTION) == null)
	    {
			Keystroke keystroke171DebugRedirectOutput = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_REDIRECT_OUTPUT_ACTION,DEBUGGER_REDIRECT_OUTPUT_ACTION_DESC, keystroke171DebugRedirectOutput);
		}	
		
		if (mappings.get(DEBUGGER_SET_PARAMETERS_ACTION) == null)
	    {
			Keystroke keystroke172DebugSetParams = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_SET_PARAMETERS_ACTION,DEBUGGER_SET_PARAMETERS_ACTION_DESC, keystroke172DebugSetParams);
		}	
		
		if (mappings.get(DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION) == null)
	    {
			Keystroke keystroke173DebugDisableAllBreak = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION,DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION_DESC, keystroke173DebugDisableAllBreak);
		}	
		
		if (mappings.get(DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION) == null)
	    {
			Keystroke keystroke174DebugEnableAllBreak = new Keystroke(null,null);
		    setKeyMap(EMACS,DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION,DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION_DESC, keystroke174DebugEnableAllBreak);
		}	
		
		if (mappings.get(HIGHLIGHT_ACTION) == null)
	    {
			Keystroke keystroke175Hightlight = new Keystroke(null,null);
		    setKeyMap(EMACS,HIGHLIGHT_ACTION,HIGHLIGHT_ACTION, keystroke175Hightlight);
		}	
		
		if (mappings.get(VIEW_STANDARD_BUTTONS_ACTION) == null)
	    {
			Keystroke keystroke176StandardButtons = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_STANDARD_BUTTONS_ACTION,VIEW_STANDARD_BUTTONS_ACTION_DESC, keystroke176StandardButtons);
		}
		
		if (mappings.get(VIEW_EDITOR_BUTTONS_ACTION) == null)
	    {
			Keystroke keystroke177EditorButtons = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_BUTTONS_ACTION,VIEW_EDITOR_BUTTONS_ACTION_DESC, keystroke177EditorButtons);
		}
		
		if (mappings.get(VIEW_FRAGMENT_BUTTONS_ACTION) == null)
	    {
			Keystroke keystroke178FragmentButtons = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_FRAGMENT_BUTTONS_ACTION,VIEW_FRAGMENT_BUTTONS_ACTION_DESC, keystroke178FragmentButtons);
		}	
		
		if (mappings.get(VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION) == null)
	    {
			Keystroke keystroke179EditorShowLineNumber = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION,VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION_DESC, keystroke179EditorShowLineNumber);
		}	
		
		if (mappings.get(VIEW_EDITOR_SHOW_OVERVIEW_ACTION) == null)
	    {
			Keystroke keystroke180EditorShowOverview = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_SHOW_OVERVIEW_ACTION,VIEW_EDITOR_SHOW_OVERVIEW_ACTION_DESC, keystroke180EditorShowOverview);
		}
		
		if (mappings.get(VIEW_EDITOR_SHOW_FOLDING_ACTION) == null)
	    {
			Keystroke keystroke181EditorShowFolding = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_SHOW_FOLDING_ACTION,VIEW_EDITOR_SHOW_FOLDING_ACTION_DESC, keystroke181EditorShowFolding);
		}
		
		if (mappings.get(VIEW_EDITOR_SHOW_ANNOTATION_ACTION) == null)
	    {
			Keystroke keystroke = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_SHOW_ANNOTATION_ACTION,VIEW_EDITOR_SHOW_ANNOTATION_ACTION_DESC, keystroke);
		}

		if (mappings.get(VIEW_EDITOR_TAG_COMPLETION_ACTION) == null)
	    {
			Keystroke keystroke182EditorShowTagCompletion = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_TAG_COMPLETION_ACTION,VIEW_EDITOR_TAG_COMPLETION_ACTION_DESC, keystroke182EditorShowTagCompletion);
		}
		
		if (mappings.get(VIEW_EDITOR_END_TAG_COMPLETION_ACTION) == null)
	    {
			Keystroke keystroke183EditorShowEndTagCompletion = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_END_TAG_COMPLETION_ACTION,VIEW_EDITOR_END_TAG_COMPLETION_ACTION_DESC, keystroke183EditorShowEndTagCompletion);
		}
		
		if (mappings.get(VIEW_EDITOR_SMART_INDENTATION_ACTION) == null)
	    {
			Keystroke keystroke183EditorSmartIndentation = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_SMART_INDENTATION_ACTION,VIEW_EDITOR_SMART_INDENTATION_ACTION_DESC, keystroke183EditorSmartIndentation);
		}
		
		if (mappings.get(VIEW_EDITOR_SOFT_WRAPPING_ACTION) == null)
	    {
			Keystroke keystroke184EditorSoftWrap = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_EDITOR_SOFT_WRAPPING_ACTION,VIEW_EDITOR_SOFT_WRAPPING_ACTION_DESC, keystroke184EditorSoftWrap);
		}
		
		if (mappings.get(VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION) == null)
	    {
			Keystroke keystroke = new Keystroke(null,null);
		    setKeyMap(DEFAULT,VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION,VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION_DESC, keystroke);
		}

		if (mappings.get(VIEWER_SHOW_NAMESPACES_ACTION) == null)
	    {
			Keystroke keystroke185ViewerShowNamespaces = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEWER_SHOW_NAMESPACES_ACTION,VIEWER_SHOW_NAMESPACES_ACTION_DESC, keystroke185ViewerShowNamespaces);
		}
		
		if (mappings.get(VIEWER_SHOW_ATTRIBUTES_ACTION) == null)
	    {
			Keystroke keystroke186ViewerShowAttr = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEWER_SHOW_ATTRIBUTES_ACTION,VIEWER_SHOW_ATTRIBUTES_ACTION_DESC, keystroke186ViewerShowAttr);
		}
		
		if (mappings.get(VIEWER_SHOW_COMMENTS_ACTION) == null)
	    {
			Keystroke keystroke187ViewerShowComments = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEWER_SHOW_COMMENTS_ACTION,VIEWER_SHOW_COMMENTS_ACTION_DESC, keystroke187ViewerShowComments);
		}
		
		if (mappings.get(VIEWER_SHOW_TEXT_CONTENT_ACTION) == null)
	    {
			Keystroke keystroke188ViewerShowTextContent = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEWER_SHOW_TEXT_CONTENT_ACTION,VIEWER_SHOW_TEXT_CONTENT_ACTION_DESC, keystroke188ViewerShowTextContent);
		}
		
		if (mappings.get(VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION) == null)
	    {
			Keystroke keystroke189ViewerShowPIs = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION,VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION_DESC, keystroke189ViewerShowPIs);
		}
		
		if (mappings.get(VIEWER_INLINE_MIXED_CONTENT_ACTION) == null)
	    {
			Keystroke keystroke190ViewerinlineMixed = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEWER_INLINE_MIXED_CONTENT_ACTION,VIEWER_INLINE_MIXED_CONTENT_ACTION_DESC, keystroke190ViewerinlineMixed);
		}
		
		if (mappings.get(OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION) == null)
	    {
			Keystroke keystroke191OutlinerShowAttr = new Keystroke(null,null);
		    setKeyMap(EMACS,OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION,OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION_DESC, keystroke191OutlinerShowAttr);
		}
		
		if (mappings.get(OUTLINER_SHOW_ELEMENT_VALUES_ACTION) == null)
	    {
			Keystroke keystroke192OutlinerShowEle = new Keystroke(null,null);
		    setKeyMap(EMACS,OUTLINER_SHOW_ELEMENT_VALUES_ACTION,OUTLINER_SHOW_ELEMENT_VALUES_ACTION_DESC, keystroke192OutlinerShowEle);
		}
		
		if (mappings.get(OUTLINER_CREATE_REQUIRED_NODES_ACTION) == null)
	    {
			Keystroke keystroke192OutlinerShowEle = new Keystroke(null,null);
		    setKeyMap(EMACS,OUTLINER_CREATE_REQUIRED_NODES_ACTION,OUTLINER_CREATE_REQUIRED_NODES_ACTION_DESC, keystroke192OutlinerShowEle);
		}
		
		if (mappings.get(VIEW_SYNCHRONIZE_SPLITS_ACTION) == null)
	    {
			Keystroke keystroke193SynchroniseSplits = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_SYNCHRONIZE_SPLITS_ACTION,VIEW_SYNCHRONIZE_SPLITS_ACTION_DESC, keystroke193SynchroniseSplits);
		}
		
		if (mappings.get(VIEW_SPLIT_HORIZONTALLY_ACTION) == null)
	    {
			Keystroke keystroke194SplitHorizontally = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_SPLIT_HORIZONTALLY_ACTION,VIEW_SPLIT_HORIZONTALLY_ACTION_DESC, keystroke194SplitHorizontally);
		}
		
		if (mappings.get(VIEW_SPLIT_VERTICALLY_ACTION) == null)
	    {
			Keystroke keystroke195SplitVertically = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_SPLIT_VERTICALLY_ACTION,VIEW_SPLIT_VERTICALLY_ACTION_DESC, keystroke195SplitVertically);
		}
		
		if (mappings.get(VIEW_UNSPLIT_ACTION) == null)
	    {
			Keystroke keystroke196Unsplit = new Keystroke(null,null);
		    setKeyMap(EMACS,VIEW_UNSPLIT_ACTION,VIEW_UNSPLIT_ACTION_DESC, keystroke196Unsplit);
		}
		
		/*if (mappings.get(GRID_ACTION) == null)
		{
			Keystroke keystroke197Grid = new Keystroke(CTRL,"5");
			setKeyMap(EMACS,GRID_ACTION,GRID_ACTION_DESC,keystroke197Grid);
		}*/
		
		/*if (mappings.get(GRID_ADD_ATTRIBUTE_COLUMN_ACTION) == null)
	    {
			Keystroke keystroke197AddAttributeColumn = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_ATTRIBUTE_COLUMN_ACTION,GRID_ADD_ATTRIBUTE_COLUMN_ACTION_DESC, keystroke197AddAttributeColumn);
		}
		
		if (mappings.get(GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION) == null)
	    {
			Keystroke keystroke198AddAttributeToSelected = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION,GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION_DESC, keystroke198AddAttributeToSelected);
		}
		
		if (mappings.get(GRID_ADD_CHILD_TABLE_ACTION) == null)
	    {
			Keystroke keystroke199AddChildTable = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_CHILD_TABLE_ACTION,GRID_ADD_CHILD_TABLE_ACTION_DESC, keystroke199AddChildTable);
		}
		
		if (mappings.get(GRID_ADD_ELEMENT_AFTER_ACTION) == null)
	    {
			Keystroke keystroke200AddElementAfter = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_ELEMENT_AFTER_ACTION,GRID_ADD_ELEMENT_AFTER_ACTION_DESC, keystroke200AddElementAfter);
		}
		
		if (mappings.get(GRID_ADD_ELEMENT_BEFORE_ACTION) == null)
	    {
			Keystroke keystroke201AddElementBefore = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_ELEMENT_BEFORE_ACTION,GRID_ADD_ELEMENT_BEFORE_ACTION_DESC, keystroke201AddElementBefore);
		}
		
		if (mappings.get(GRID_ADD_TEXT_COLUMN_ACTION) == null)
	    {
			Keystroke keystroke202AddTextColumn = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_TEXT_COLUMN_ACTION,GRID_ADD_TEXT_COLUMN_ACTION_DESC, keystroke202AddTextColumn);
		}
		
		if (mappings.get(GRID_ADD_TEXT_TO_SELECTED_ACTION) == null)
	    {
			Keystroke keystroke203AddTextToSelected = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_ADD_TEXT_TO_SELECTED_ACTION,GRID_ADD_TEXT_TO_SELECTED_ACTION_DESC, keystroke203AddTextToSelected);
		}
		
		if (mappings.get(GRID_DELETE_ATTS_AND_TEXT_ACTION) == null)
	    {
			Keystroke keystroke204DeleteAttsAndText = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_DELETE_ATTS_AND_TEXT_ACTION,GRID_DELETE_ATTS_AND_TEXT_ACTION_DESC, keystroke204DeleteAttsAndText);
		}
		
		if (mappings.get(GRID_DELETE_CHILD_TABLE_ACTION) == null)
	    {
			Keystroke keystroke205DeleteChildTable = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_DELETE_CHILD_TABLE_ACTION,GRID_DELETE_CHILD_TABLE_ACTION_DESC, keystroke205DeleteChildTable);
		}
		
		if (mappings.get(GRID_DELETE_COLUMN_ACTION) == null)
	    {
			Keystroke keystroke206DeleteColumn = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_DELETE_COLUMN_ACTION,GRID_DELETE_COLUMN_ACTION_DESC, keystroke206DeleteColumn);
		}
		
		if (mappings.get(GRID_DELETE_ROW_ACTION) == null)
	    {
			Keystroke keystroke207DeleteRow = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_DELETE_ROW_ACTION,GRID_DELETE_ROW_ACTION_DESC, keystroke207DeleteRow);
		}
		
		if (mappings.get(GRID_DELETE_SELECTED_ATTRIBUTE_ACTION) == null)
	    {
			Keystroke keystroke208DeleteSelectedAttribute = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_DELETE_SELECTED_ATTRIBUTE_ACTION,GRID_DELETE_SELECTED_ATTRIBUTE_ACTION_DESC, keystroke208DeleteSelectedAttribute);
		}
		
		if (mappings.get(GRID_DELETE_SELECTED_TEXT_ACTION) == null)
	    {
			Keystroke keystroke209DeleteSelectedText = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_DELETE_SELECTED_TEXT_ACTION,GRID_DELETE_SELECTED_TEXT_ACTION_DESC, keystroke209DeleteSelectedText);
		}
		
		if (mappings.get(GRID_RENAME_ATTRIBUTE_ACTION) == null)
	    {
			Keystroke keystroke210EditAttributeName = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_RENAME_ATTRIBUTE_ACTION,GRID_RENAME_ATTRIBUTE_ACTION_DESC, keystroke210EditAttributeName);
		}
		
		if (mappings.get(GRID_RENAME_SELECTED_ATTRIBUTE_ACTION) == null)
	    {
			Keystroke keystroke210RenameSelectedAttribute = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_RENAME_SELECTED_ATTRIBUTE_ACTION,GRID_RENAME_SELECTED_ATTRIBUTE_ACTION_DESC, keystroke210RenameSelectedAttribute);
		}
		
		if (mappings.get(GRID_MOVE_ROW_DOWN_ACTION) == null)
	    {
			Keystroke keystroke211MoveRowDown = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_MOVE_ROW_DOWN_ACTION,GRID_MOVE_ROW_DOWN_ACTION_DESC, keystroke211MoveRowDown);
		}
		
		if (mappings.get(GRID_MOVE_ROW_UP_ACTION) == null)
	    {
			Keystroke keystroke212MoveRowUp = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_MOVE_ROW_UP_ACTION,GRID_MOVE_ROW_UP_ACTION_DESC, keystroke212MoveRowUp);
		}
		
		if (mappings.get(GRID_SORT_TABLE_DESCENDING_ACTION) == null)
	    {
			Keystroke keystroke213SortTable = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_SORT_TABLE_DESCENDING_ACTION,GRID_SORT_TABLE_DESCENDING_ACTION_DESC, keystroke213SortTable);
		}
		
		if (mappings.get(GRID_SORT_TABLE_ASCENDING_ACTION) == null)
	    {
			Keystroke keystroke213SortDescending = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_SORT_TABLE_ASCENDING_ACTION,GRID_SORT_TABLE_ASCENDING_ACTION_DESC, keystroke213SortDescending);
		}
		
		if (mappings.get(GRID_UNSORT_ACTION) == null)
	    {
			Keystroke keystroke213Unsort = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_UNSORT_ACTION,GRID_UNSORT_ACTION_DESC, keystroke213Unsort);
		}
		
		if (mappings.get(GRID_GOTO_PARENT_TABLE_ACTION) == null)
	    {
			Keystroke keystroke214Collapse = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_GOTO_PARENT_TABLE_ACTION,GRID_COLLAPSE_ACTION_DESC, keystroke214Collapse);
		}
		
		if (mappings.get(GRID_GOTO_CHILD_TABLE_ACTION) == null)
	    {
			Keystroke keystroke215Expand = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_GOTO_CHILD_TABLE_ACTION,GRID_EXPAND_ACTION_DESC, keystroke215Expand);
		}
		
		if (mappings.get(GRID_COPY_SHALLOW_ACTION) == null)
	    {
			Keystroke keystroke216CopyShallow = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_COPY_SHALLOW_ACTION,GRID_COPY_SHALLOW_ACTION_DESC, keystroke216CopyShallow);
		}
		
		if (mappings.get(GRID_PASTE_AFTER_ACTION) == null)
	    {
			Keystroke keystroke217PasteAfter = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_PASTE_AFTER_ACTION,GRID_PASTE_AFTER_ACTION_DESC, keystroke217PasteAfter);
		}
		
		if (mappings.get(GRID_PASTE_AS_CHILD_ACTION) == null)
	    {
			Keystroke keystroke218PasteAsChild = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_PASTE_AS_CHILD_ACTION,GRID_PASTE_AS_CHILD_ACTION_DESC, keystroke218PasteAsChild);
		}
		
		if (mappings.get(GRID_PASTE_BEFORE_ACTION) == null)
	    {
			Keystroke keystroke219PasteBefore = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_PASTE_BEFORE_ACTION,GRID_PASTE_BEFORE_ACTION_DESC, keystroke219PasteBefore);
		}
		
		if (mappings.get(GRID_COLLAPSE_ROW_ACTION) == null)
	    {
			Keystroke keystroke220CollapseRow = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_COLLAPSE_ROW_ACTION,GRID_COLLAPSE_ROW_ACTION_DESC, keystroke220CollapseRow);
		}
		
		if (mappings.get(GRID_EXPAND_ROW_ACTION) == null)
	    {
			Keystroke keystroke221ExpandRow = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_EXPAND_ROW_ACTION,GRID_EXPAND_ROW_ACTION_DESC, keystroke221ExpandRow);
		}
		
		if (mappings.get(GRID_COLLAPSE_CURRENT_TABLE_ACTION) == null)
	    {
			Keystroke keystroke222CollapseCurrentTable = new Keystroke(null,null);
		    setKeyMap(EMACS,GRID_COLLAPSE_CURRENT_TABLE_ACTION,GRID_COLLAPSE_CURRENT_TABLE_ACTION_DESC, keystroke222CollapseCurrentTable);
		}
		
		if (mappings.get(GRID_DELETE_ACTION) == null)
	    {
			Keystroke keystroke223GridDelete = new Keystroke(null,"DELETE");
		    setKeyMap(EMACS,GRID_DELETE_ACTION,GRID_DELETE_ACTION_DESC, keystroke223GridDelete);
		}*/
		
		if (mappings.get(EXECUTE_SCHEMATRON_ACTION) == null)
		{
			Keystroke keystroke224 = new Keystroke(null,null);
			setKeyMap(EMACS,EXECUTE_SCHEMATRON_ACTION,EXECUTE_SCHEMATRON_ACTION_DESC,keystroke224);
		}
		
		for(int cnt=0;cnt<getPluginMappings().size();++cnt) {
			Object obj = getPluginMappings().get(cnt);
			if((obj != null) && (obj instanceof PluginActionKeyMapping)) {
				PluginActionKeyMapping keyMapping = (PluginActionKeyMapping)obj;
				
				if(mappings.get(keyMapping.getKeystroke_action_name()) == null) {
					Keystroke pluginKeystroke = new Keystroke(keyMapping.getKeystroke_mask(), keyMapping.getKeystroke_value());
					setKeyMap(EMACS, keyMapping.getKeystroke_action_name(), keyMapping.getKeystroke_action_description(), pluginKeystroke);
					
				}
			}
		}
	}
	
	/**
	 * sets the keymap
	 */
	private void setKeyMap(String configName, String action,Keystroke keystroke)
	{
		setKeyMap(configName,action,null,keystroke);
	}
	
	/**
	 * sets the keymap with a description
	 */
	private void setKeyMap(String configName, String action, String description,Keystroke keystroke)
	{
		
		// add the key
		XElement config = getConfiguration(configName);
		
		
		KeyMap keymap = new KeyMap(action,description,keystroke);
		config.add(keymap.getElement());
	}
	
	/**
	 * sets the keymap with a description
	 */
	private void setKeyMap(String configName, String action, String description,Keystroke keystroke,Keystroke keystroke2)
	{
		// add the key
		XElement config = getConfiguration(configName);
		
		KeyMap keymap = new KeyMap(action,description,keystroke,keystroke2);
		config.add(keymap.getElement());
	}
	
	 /**
	 * sets the keymap using an existing KeyMap object
	 *
	 * @param configName The configuration name
	 * @param keymap The new KeyMap object  
	 */
	public void setKeyMap(String configName, KeyMap keymap)
	{
		XElement config = getConfiguration(configName);
		config.add(keymap.getElement());
	}
	
	 /**
	 * Removes the mappings for a particular configuration
	 *
	 * @param configName The configuration name
	 */
	public void removeMapping(String configName)
	{
		XElement configuration = getConfiguration(configName);
		XElement keymapping = getElement();
		
		keymapping.remove(configuration); 
	}
	
	/**
	 * Returns all the KeyMap Elements for a particular configuration
	 *
	 * @param configName The configuration name
	 *
	 * @return Hashtable containing the required KeyMaps Elements using action name as keys
	 */
	public Hashtable getKeyMapElements(String configName)
	{
		Hashtable keyMap = new Hashtable();
		
		XElement config = getConfiguration(configName);
		
		Iterator keymaps = config.elementIterator(KEYMAP); 
		while (keymaps.hasNext())
		{
			XElement keymap = (XElement)keymaps.next();
			XElement action = (XElement)keymap.element(ACTION);
			String actionName = action.getText();
			
			keyMap.put(actionName,keymap);
		}
		
		return keyMap;
	}
	
	/**
	 * Returns all the KeyMap objects for a particular configuration
	 *
	 * @param configName The configuration name
	 *
	 * @return Hashtable containing the required KeyMap Objects using action name as keys
	 */
	public Hashtable getKeyMaps(String configName)
	{
		Hashtable keyMaps = new Hashtable();
		
		Hashtable keyMapElements = getKeyMapElements(configName);
		
		Enumeration elements = keyMapElements.elements();
		while (elements.hasMoreElements())
		{
			XElement keyMapElement = (XElement)elements.nextElement();
			XElement action = (XElement)keyMapElement.element(ACTION);
			String actionName = action.getText();
			
			KeyMap keymap = new KeyMap(keyMapElement);
			keyMaps.put(actionName,keymap);
		}
		
		return keyMaps;
	}
	
	/**
	 * Returns all the Keystrokes for a particular KeyMap
	 *
  	 * @param keymap The particular KeyMap
	 *
	 * @return Vector containing the Keystrokes
	 */
	public Vector getKeystrokes(XElement keymap)
	{
		Vector keyStroke = new Vector();
		
		Iterator keystrokes = keymap.elementIterator(KEYSTROKE); 
		while (keystrokes.hasNext())
		{
			XElement keystroke = (XElement)keystrokes.next();
			String mask = null;
			XElement maskEle = (XElement)keystroke.element(MASK);
			if (maskEle != null)
			{
				mask = maskEle.getText();
			}
			
			XElement valueEle = (XElement)keystroke.element(VALUE);
			String value = null;
			if (valueEle != null)
			{
				value = valueEle.getText();
			}
			
			if (value != null)
			{ 
				Keystroke keystrokeObj = new Keystroke(mask,value);
			
				keyStroke.add(keystrokeObj);
			}
		}
		
		return keyStroke;
	}
	
	/**
	 * Returns a key sequence for particular Keystrokes
	 *
	 * @param keystrokes A vector of keystroke objects
	 *
	 * @return The key sequence 
	 */
	public String getKeySequence(Vector keystrokes)
	{
		StringBuffer sequence = new StringBuffer();
	
		if (keystrokes.size() == 1)
		{
			Keystroke keystroke = (Keystroke)keystrokes.get(0);
			sequence.append(getKeySequence(keystroke));
		}
		else if (keystrokes.size() == 2)
		{
			Keystroke keystroke1 = (Keystroke)keystrokes.get(0);
			Keystroke keystroke2 = (Keystroke)keystrokes.get(1);
			sequence.append(getKeySequence(keystroke1));
			sequence.append(',');
			sequence.append(getKeySequence(keystroke2));
		}
		else
		{
			// no sequence assigned yet
			sequence.append("");
		}
		
		return sequence.toString();
	}
	
	/**
	 * Returns a key sequence for particular Keystroke
	 *
	 * @param keystroke The Keystroke object
	 *
	 * @return The key sequence 
	 */
	public String getKeySequence(Keystroke keystroke)
	{
		StringBuffer sequence = new StringBuffer();
		
		String maskValue = keystroke.getMask();
		if (maskValue != null && !maskValue.equals(""))
		{
			sequence.append(maskValue);
			sequence.append('+');
		}
		
		String value = keystroke.getValue();
		if (value != null)
		{
			sequence.append(value);
		}
		else
		{
			// no assignment yet
			value = "";
			sequence.append(value);
		}
		
		return sequence.toString();
	}
	
	/**
	 * Returns the sorted action names
	 *
	 * @param keyMaps All the keymaps
	 *
	 * @return All the sorted action names
	 */
	public Vector getSortedActionNames(Hashtable keyMaps) 
	{			
		Enumeration names = keyMaps.keys();
		Vector unsortedNames = new Vector();
		while (names.hasMoreElements())
		{
			String name = (String)names.nextElement();
			unsortedNames.add(name);
		}
		
		Vector sortedNames = sort(unsortedNames); 
		return sortedNames;
	}
	
	// sorts the action names
	private Vector sort( Vector list) 
	{
		Vector elements = new Vector(list.size());
		
		for ( int i = 0; i < list.size(); i++) 
		{
			String actionName = (String)list.elementAt(i);

			// Find out where to insert the element...
			int index = -1;

			for ( int j = 0; j < elements.size() && index == -1; j++) {
				// Compare alphabeticaly
				if ( actionName.compareToIgnoreCase ((String)elements.elementAt(j)) <= 0) {
					index = j;
				}
			}
			
			if ( index != -1) {
				elements.insertElementAt( actionName, index);
			} else {
				elements.addElement( actionName);
			}
		}
			
		return elements;
	}
	
	/**
	 * Creates a java.swing.KeyStroke from a given Keystroke object
	 *
     * @param keystroke The particular keystroke
     *
	 * @return the KeyStroke
	 */
	public KeyStroke getKeyStroke(Keystroke keystroke)
	{
		int maskInt = 0;
		int valueInt = 0;
		
		String mask = keystroke.getContentIfExists(MASK);
		String value = keystroke.getContentIfExists(VALUE);
		
		if (mask == null)
		{
			maskInt = 0;
		}
		else if (mask.equalsIgnoreCase(CTRL))
		{
			maskInt = InputEvent.CTRL_MASK;
		}
		else if (mask.equalsIgnoreCase(ALT))
		{
			maskInt = InputEvent.ALT_MASK;
		}
		else if (mask.equalsIgnoreCase(CTRLSHIFT))
		{
			maskInt = InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK;
		}
		else if (mask.equalsIgnoreCase(CTRLALT))
		{
			maskInt = InputEvent.CTRL_MASK+InputEvent.ALT_MASK;
		}
		else if (mask.equalsIgnoreCase(CTRLALTSHIFT))
		{
			maskInt = InputEvent.CTRL_MASK+InputEvent.ALT_MASK+InputEvent.SHIFT_MASK;
		}
		else if (mask.equalsIgnoreCase(ALTSHIFT))
		{
			maskInt = InputEvent.ALT_MASK+InputEvent.SHIFT_MASK;
		}
		else if (mask.equalsIgnoreCase(SHIFT))
		{
			maskInt = InputEvent.SHIFT_MASK;
		}
		//mac os
		else if (mask.equalsIgnoreCase(CMD))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		}
		else if (mask.equalsIgnoreCase(CMDSHIFT))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+InputEvent.SHIFT_MASK;
		}
		else if (mask.equalsIgnoreCase(CMDALT))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+InputEvent.ALT_MASK;
		}
		else if (mask.equalsIgnoreCase(CMDCTRL))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+InputEvent.CTRL_MASK;
		}
		else if (mask.equalsIgnoreCase(CMDALTSHIFT))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+InputEvent.ALT_MASK+InputEvent.SHIFT_MASK;
		}
		else if (mask.equalsIgnoreCase(CMDCTRLSHIFT))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+InputEvent.CTRL_MASK+InputEvent.SHIFT_MASK;
		}
		else if (mask.equalsIgnoreCase(CMDCTRLALTSHIFT))
		{
			maskInt = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()+InputEvent.CTRL_MASK+InputEvent.ALT_MASK+InputEvent.SHIFT_MASK;
		}
		else
		{
			maskInt = 0;
		}
		
		if (value.equals(";"))
		{
			// to cover semicolon, getkeycode doesn't appear to work for semicolon
			valueInt = KeyEvent.VK_SEMICOLON;
		}
		else if (value.equalsIgnoreCase("UP"))
		{
			valueInt = KeyEvent.VK_UP;
		}
		else if (value.equalsIgnoreCase("DOWN"))
		{
			valueInt = KeyEvent.VK_DOWN;
		}
		else if (value.equalsIgnoreCase("RIGHT"))
		{
			valueInt = KeyEvent.VK_RIGHT;
		}
		else if (value.equalsIgnoreCase("LEFT"))
		{
			valueInt = KeyEvent.VK_LEFT;
		}
		else if (value.equalsIgnoreCase("PAGEUP"))
		{
			valueInt = KeyEvent.VK_PAGE_UP;
		}
		else if (value.equalsIgnoreCase("PAGEDOWN"))
		{
			valueInt = KeyEvent.VK_PAGE_DOWN;
		}
		else if (value.equalsIgnoreCase("BACKSPACE"))
		{
			valueInt = KeyEvent.VK_BACK_SPACE;
		}
		else if (value.equalsIgnoreCase("DELETE"))
		{
			valueInt = KeyEvent.VK_DELETE;
		}
		else
		{
			try{
			valueInt = KeyStroke.getKeyStroke(value.toUpperCase()).getKeyCode();
			}
			catch (Exception e)
			{
				System.out.println("Could not create a KeyStroke for the key - "+value);
			}
		}
		
		KeyStroke stroke = null;
		try{
			stroke = KeyStroke.getKeyStroke(valueInt,maskInt,false);
		}
		catch (Exception e)
		{
			// unknown keystroke
			System.out.println("The following error occurred getting the keystroke: "+e.getMessage());
		}
		
		return stroke;
	}
	
	/**
	 *  sets all the keymappings for the specified configuation
	 *
 	 * @param parent The ExchangerEditor
 	 * @param configName The configuration name 
	 */
	public void setKeyMappings(ExchangerEditor parent, String configName)
	{
		if (configName.equalsIgnoreCase(KeyPreferences.EMACS))
		{
			// set extra emac editing mode on
			parent.setEmacsModeOn(true);
		}
		else
		{
			// turn off editing mode
			parent.setEmacsModeOn(false);
		}
		
		Hashtable keyMap = getKeyMapElements(configName);
		
		Enumeration actionNames = keyMap.keys();
		
		while (actionNames.hasMoreElements())
		{
			String actionName = (String)actionNames.nextElement();
			
			XElement keymap = (XElement)keyMap.get(actionName);
			
			Vector keystrokes = getKeystrokes(keymap);
			Keystroke keystroke = null;
			Keystroke keystroke2 = null;
			
			int keystrokeSize = keystrokes.size();
			
			if (keystrokeSize == 1)
			{
				keystroke = (Keystroke)keystrokes.get(0);
			}
			else if (keystrokeSize == 2)
			{
				keystroke = (Keystroke)keystrokes.get(0);
				keystroke2 = (Keystroke)keystrokes.get(1);
			}
			else
			{
				// there are no keys assigned for this acttion
			}
			  
			// to check debugger keys
			XSLTDebuggerFrame debugger = parent.getDebugger();
			
			// only need to change the high level ones here (i.e menu items etc, not editing functionality,
			// that is done in updatePreferences in each Editor)
			
			
			if (actionName.equals(KeyPreferences.OPEN_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.OPEN_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);	
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.OPEN_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.OPEN_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.OPEN_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.OPEN_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CLOSE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLOSE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLOSE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CLOSE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CLOSE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLOSE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
				
			}
			else if (actionName.equals(KeyPreferences.CLOSE_ALL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLOSE_ALL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLOSE_ALL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CLOSE_ALL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CLOSE_ALL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLOSE_ALL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SAVE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SAVE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SAVE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SAVE_ALL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_ALL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_ALL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SAVE_ALL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SAVE_ALL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_ALL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.UNDO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.UNDO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.UNDO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.UNDO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.UNDO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.UNDO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SAVE_AS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if ((keystrokeSize == 2))
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SAVE_AS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SAVE_AS_ACTION,stroke,action);
				}
				else if (keystrokeSize == 2)
				{
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SELECT_DOCUMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SELECT_DOCUMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SELECT_DOCUMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.PRINT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PRINT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PRINT_ACTION);
					if(item != null) item.setAccelerator(null);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.PRINT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.PRINT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PRINT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.FIND_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.FIND_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.FIND_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.FIND_NEXT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_NEXT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_NEXT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.FIND_NEXT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.FIND_NEXT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_NEXT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.REPLACE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REPLACE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REPLACE_ACTION);
					if(item != null) item.setAccelerator(null);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.REPLACE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.REPLACE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REPLACE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CUT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CUT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CUT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CUT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CUT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CUT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.COPY_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COPY_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COPY_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.COPY_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.COPY_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COPY_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.PASTE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PASTE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
					
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PASTE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.PASTE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.PASTE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PASTE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.COMMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COMMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COMMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.COMMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.COMMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COMMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GOTO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GOTO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GOTO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.WELL_FORMEDNESS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.WELL_FORMEDNESS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.WELL_FORMEDNESS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.WELL_FORMEDNESS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.WELL_FORMEDNESS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.WELL_FORMEDNESS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.VALIDATE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VALIDATE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VALIDATE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.START_BROWSER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.START_BROWSER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.START_BROWSER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.START_BROWSER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.START_BROWSER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.START_BROWSER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.NEW_DOCUMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.NEW_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.NEW_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(null);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.NEW_DOCUMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.NEW_DOCUMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.NEW_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.REDO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REDO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REDO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.REDO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.REDO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REDO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SELECT_ELEMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SELECT_ELEMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SELECT_ELEMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.INSERT_SPECIAL_CHAR_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.INSERT_SPECIAL_CHAR_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.INSERT_SPECIAL_CHAR_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.INSERT_SPECIAL_CHAR_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.INSERT_SPECIAL_CHAR_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.INSERT_SPECIAL_CHAR_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.TAG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TAG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TAG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TAG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TAG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TAG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.REPEAT_TAG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REPEAT_TAG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REPEAT_TAG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.REPEAT_TAG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.REPEAT_TAG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REPEAT_TAG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GOTO_START_TAG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_START_TAG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_START_TAG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GOTO_START_TAG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GOTO_START_TAG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_START_TAG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GOTO_END_TAG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_END_TAG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_END_TAG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GOTO_END_TAG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GOTO_END_TAG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_END_TAG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.RENAME_ELEMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.RENAME_ELEMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.RENAME_ELEMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.TOGGLE_BOOKMARK_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_BOOKMARK_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_BOOKMARK_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOGGLE_BOOKMARK_ACTION);	
					parent.getStatusbar().addToModeMap(KeyPreferences.TOGGLE_BOOKMARK_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_BOOKMARK_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SELECT_BOOKMARK_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_BOOKMARK_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_BOOKMARK_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SELECT_BOOKMARK_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SELECT_BOOKMARK_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_BOOKMARK_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SELECT_FRAGMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_FRAGMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_FRAGMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SELECT_FRAGMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SELECT_FRAGMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SELECT_FRAGMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SCHEMA_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					//TODO JRadioButtonMenuItem item = parent.getSchemaViewItem();
					//TODO if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					//TODO JRadioButtonMenuItem item = parent.getSchemaViewItem();
					//TODO if(item != null) item.setAccelerator(null);
				}
			}
			else if (actionName.equals(KeyPreferences.OUTLINER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					//TODO JRadioButtonMenuItem item = parent.getDesignerViewItem();
					//TODO if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					//TODO JRadioButtonMenuItem item = parent.getDesignerViewItem();
					//TODO if(item != null) item.setAccelerator(null);
				}
			}
			else if (actionName.equals(KeyPreferences.EDITOR_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					//TODO JRadioButtonMenuItem item = parent.getEditorViewItem();
					//TODO if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					//TODO JRadioButtonMenuItem item = parent.getEditorViewItem();
					//TODO if(item != null) item.setAccelerator(null);
				}
			}
			else if (actionName.equals(KeyPreferences.VIEWER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					//TODO JRadioButtonMenuItem item = parent.getViewerViewItem();
					//TODO if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					//TODO JRadioButtonMenuItem item = parent.getViewerViewItem();
					//TODO if(item != null) item.setAccelerator(null);
				}
			}
			
			/*else if (actionName.equals(KeyPreferences.GRID_ACTION))
			{
			    // update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JRadioButtonMenuItem item = parent.getGridViewItem();
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JRadioButtonMenuItem item = parent.getGridViewItem();
					if(item != null) item.setAccelerator(null);
				}
			}*/
			
			else if (actionName.equals(KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.OPEN_REMOTE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.OPEN_REMOTE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.OPEN_REMOTE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.OPEN_REMOTE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.OPEN_REMOTE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.OPEN_REMOTE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.RELOAD_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RELOAD_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RELOAD_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.RELOAD_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.RELOAD_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RELOAD_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SAVE_AS_TEMPLATE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_TEMPLATE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_TEMPLATE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SAVE_AS_TEMPLATE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SAVE_AS_TEMPLATE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SAVE_AS_TEMPLATE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.MANAGE_TEMPLATE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_TEMPLATE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_TEMPLATE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.MANAGE_TEMPLATE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.MANAGE_TEMPLATE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_TEMPLATE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.PAGE_SETUP_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PAGE_SETUP_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PAGE_SETUP_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.PAGE_SETUP_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.PAGE_SETUP_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PAGE_SETUP_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.PREFERENCES_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PREFERENCES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PREFERENCES_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.PREFERENCES_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.PREFERENCES_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.PREFERENCES_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CREATE_REQUIRED_NODE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CREATE_REQUIRED_NODE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CREATE_REQUIRED_NODE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CREATE_REQUIRED_NODE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CREATE_REQUIRED_NODE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CREATE_REQUIRED_NODE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SPLIT_ELEMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SPLIT_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SPLIT_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SPLIT_ELEMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SPLIT_ELEMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SPLIT_ELEMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CONVERT_ENTITIES_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_ENTITIES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_ENTITIES_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CONVERT_ENTITIES_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CONVERT_ENTITIES_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_ENTITIES_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CONVERT_CHARACTERS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_CHARACTERS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_CHARACTERS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CONVERT_CHARACTERS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CONVERT_CHARACTERS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_CHARACTERS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.TAB_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TAB_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TAB_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TAB_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TAB_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TAB_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.UNINDENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.UNINDENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.UNINDENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.UNINDENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.UNINDENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.UNINDENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.STRIP_TAG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.STRIP_TAG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.STRIP_TAG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.STRIP_TAG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.STRIP_TAG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.STRIP_TAG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ADD_CDATA_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_CDATA_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_CDATA_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_CDATA_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_CDATA_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_CDATA_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.LOCK_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.LOCK_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.LOCK_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.LOCK_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.LOCK_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.LOCK_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.FORMAT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FORMAT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FORMAT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.FORMAT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.FORMAT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FORMAT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXPAND_ALL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXPAND_ALL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXPAND_ALL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXPAND_ALL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXPAND_ALL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXPAND_ALL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.COLLAPSE_ALL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COLLAPSE_ALL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COLLAPSE_ALL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.COLLAPSE_ALL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.COLLAPSE_ALL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.COLLAPSE_ALL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SYNCHRONISE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SYNCHRONISE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SYNCHRONISE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SYNCHRONISE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SYNCHRONISE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SYNCHRONISE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.TOGGLE_FULL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_FULL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_FULL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOGGLE_FULL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOGGLE_FULL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOGGLE_FULL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.NEW_PROJECT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.NEW_PROJECT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.NEW_PROJECT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.NEW_PROJECT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.NEW_PROJECT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.NEW_PROJECT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.IMPORT_PROJECT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_PROJECT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_PROJECT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.IMPORT_PROJECT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.IMPORT_PROJECT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_PROJECT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.DELETE_PROJECT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.DELETE_PROJECT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.DELETE_PROJECT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.DELETE_PROJECT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.DELETE_PROJECT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.DELETE_PROJECT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.RENAME_PROJECT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_PROJECT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_PROJECT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.RENAME_PROJECT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.RENAME_PROJECT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_PROJECT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CHECK_WELLFORMEDNESS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CHECK_WELLFORMEDNESS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CHECK_WELLFORMEDNESS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CHECK_WELLFORMEDNESS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CHECK_WELLFORMEDNESS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CHECK_WELLFORMEDNESS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.VALIDATE_PROJECT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_PROJECT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_PROJECT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VALIDATE_PROJECT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VALIDATE_PROJECT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_PROJECT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.FIND_IN_FILES_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_IN_FILES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_IN_FILES_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.FIND_IN_FILES_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.FIND_IN_FILES_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_IN_FILES_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.FIND_IN_PROJECTS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_IN_PROJECTS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_IN_PROJECTS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.FIND_IN_PROJECTS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.FIND_IN_PROJECTS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.FIND_IN_PROJECTS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ADD_FILE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_FILE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_FILE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_FILE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_FILE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_FILE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ADD_REMOTE_FILE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_REMOTE_FILE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_REMOTE_FILE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_REMOTE_FILE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_REMOTE_FILE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_REMOTE_FILE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.REMOVE_FILE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REMOVE_FILE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REMOVE_FILE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.REMOVE_FILE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.REMOVE_FILE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REMOVE_FILE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ADD_DIRECTORY_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_DIRECTORY_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_DIRECTORY_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_DIRECTORY_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_DIRECTORY_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_DIRECTORY_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ADD_FOLDER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_FOLDER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_FOLDER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ADD_FOLDER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ADD_FOLDER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ADD_FOLDER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.REMOVE_FOLDER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REMOVE_FOLDER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REMOVE_FOLDER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.REMOVE_FOLDER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.REMOVE_FOLDER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.REMOVE_FOLDER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.RENAME_FOLDER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_FOLDER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_FOLDER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.RENAME_FOLDER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.RENAME_FOLDER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RENAME_FOLDER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.VALIDATE_XML_SCHEMA_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_XML_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_XML_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VALIDATE_XML_SCHEMA_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VALIDATE_XML_SCHEMA_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_XML_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.VALIDATE_DTD_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_DTD_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_DTD_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VALIDATE_DTD_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VALIDATE_DTD_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_DTD_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.VALIDATE_RELAXNG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_RELAXNG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_RELAXNG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VALIDATE_RELAXNG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VALIDATE_RELAXNG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VALIDATE_RELAXNG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SET_XML_DECLARATION_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_XML_DECLARATION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_XML_DECLARATION_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SET_XML_DECLARATION_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SET_XML_DECLARATION_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_XML_DECLARATION_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SET_SCHEMA_LOCATION_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_SCHEMA_LOCATION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_SCHEMA_LOCATION_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SET_SCHEMA_LOCATION_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SET_SCHEMA_LOCATION_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_SCHEMA_LOCATION_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.RESOLVE_XINCLUDES_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RESOLVE_XINCLUDES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RESOLVE_XINCLUDES_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.RESOLVE_XINCLUDES_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.RESOLVE_XINCLUDES_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.RESOLVE_XINCLUDES_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SET_SCHEMA_PROPS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_SCHEMA_PROPS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_SCHEMA_PROPS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SET_SCHEMA_PROPS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SET_SCHEMA_PROPS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_SCHEMA_PROPS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.INFER_SCHEMA_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.INFER_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.INFER_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.INFER_SCHEMA_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.INFER_SCHEMA_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.INFER_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CREATE_TYPE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CREATE_TYPE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CREATE_TYPE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CREATE_TYPE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CREATE_TYPE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CREATE_TYPE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SET_TYPE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_TYPE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_TYPE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SET_TYPE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SET_TYPE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SET_TYPE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.TYPE_PROPERTIES_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TYPE_PROPERTIES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TYPE_PROPERTIES_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TYPE_PROPERTIES_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TYPE_PROPERTIES_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TYPE_PROPERTIES_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.MANAGE_TYPES_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_TYPES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_TYPES_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.MANAGE_TYPES_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.MANAGE_TYPES_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_TYPES_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CONVERT_SCHEMA_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CONVERT_SCHEMA_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CONVERT_SCHEMA_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_SCHEMA_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_FO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_FO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_FO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_FO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_FO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_FO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_XQUERY_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_XQUERY_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_XQUERY_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_XQUERY_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_XQUERY_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_XQUERY_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_SCENARIO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SCENARIO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SCENARIO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_SCENARIO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_SCENARIO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SCENARIO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.XSLT_DEBUGGER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.XSLT_DEBUGGER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.XSLT_DEBUGGER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.XSLT_DEBUGGER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.XSLT_DEBUGGER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.XSLT_DEBUGGER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.MANAGE_SCENARIOS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_SCENARIOS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_SCENARIOS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.MANAGE_SCENARIOS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.MANAGE_SCENARIOS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.MANAGE_SCENARIOS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CANONICALIZE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CANONICALIZE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CANONICALIZE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CANONICALIZE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CANONICALIZE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CANONICALIZE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SIGN_DOCUMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SIGN_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SIGN_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SIGN_DOCUMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SIGN_DOCUMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SIGN_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.VERIFY_SIGNATURE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VERIFY_SIGNATURE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VERIFY_SIGNATURE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VERIFY_SIGNATURE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VERIFY_SIGNATURE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VERIFY_SIGNATURE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SHOW_SVG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SHOW_SVG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SHOW_SVG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SHOW_SVG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SHOW_SVG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SHOW_SVG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CONVERT_SVG_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_SVG_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_SVG_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CONVERT_SVG_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CONVERT_SVG_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CONVERT_SVG_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.SEND_SOAP_MESSAGE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SEND_SOAP_MESSAGE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SEND_SOAP_MESSAGE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.SEND_SOAP_MESSAGE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.SEND_SOAP_MESSAGE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.SEND_SOAP_MESSAGE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.ANALYSE_WSDL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ANALYSE_WSDL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ANALYSE_WSDL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.ANALYSE_WSDL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.ANALYSE_WSDL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.ANALYSE_WSDL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.CLEAN_UP_HTML_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLEAN_UP_HTML_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLEAN_UP_HTML_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.CLEAN_UP_HTML_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.CLEAN_UP_HTML_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.CLEAN_UP_HTML_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.IMPORT_FROM_TEXT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_TEXT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_TEXT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.IMPORT_FROM_TEXT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.IMPORT_FROM_TEXT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_TEXT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.IMPORT_FROM_EXCEL_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_EXCEL_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_EXCEL_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.IMPORT_FROM_EXCEL_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.IMPORT_FROM_EXCEL_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_EXCEL_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.IMPORT_FROM_DBTABLE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_DBTABLE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_DBTABLE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.IMPORT_FROM_DBTABLE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.IMPORT_FROM_DBTABLE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_DBTABLE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_CAPITALIZE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CAPITALIZE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CAPITALIZE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_CAPITALIZE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_CAPITALIZE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CAPITALIZE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_DECAPITALIZE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_DECAPITALIZE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_DECAPITALIZE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_DECAPITALIZE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_DECAPITALIZE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_DECAPITALIZE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_LOWERCASE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_LOWERCASE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_LOWERCASE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_LOWERCASE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_LOWERCASE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_LOWERCASE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_UPPERCASE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_UPPERCASE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_UPPERCASE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_UPPERCASE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_UPPERCASE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_UPPERCASE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.IMPORT_FROM_SQLXML_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_SQLXML_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_SQLXML_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.IMPORT_FROM_SQLXML_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.IMPORT_FROM_SQLXML_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.IMPORT_FROM_SQLXML_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_RENAME_NODE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_RENAME_NODE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_RENAME_NODE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_RENAME_NODE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_RENAME_NODE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_RENAME_NODE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_REMOVE_NODE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_REMOVE_NODE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_REMOVE_NODE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_REMOVE_NODE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_REMOVE_NODE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_REMOVE_NODE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_ADD_NODE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_ADD_NODE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_ADD_NODE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_ADD_NODE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_ADD_NODE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_ADD_NODE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}

			else if (actionName.equals(KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_CONVERT_NODE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CONVERT_NODE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CONVERT_NODE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_CONVERT_NODE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_CONVERT_NODE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_CONVERT_NODE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.TOOLS_SORT_NODE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_SORT_NODE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_SORT_NODE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.TOOLS_SORT_NODE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.TOOLS_SORT_NODE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.TOOLS_SORT_NODE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.XDIFF_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.XDIFF_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.XDIFF_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.XDIFF_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.XDIFF_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.XDIFF_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.HIGHLIGHT_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.HIGHLIGHT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.HIGHLIGHT_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_STANDARD_BUTTONS_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_STANDARD_BUTTONS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_STANDARD_BUTTONS_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_BUTTONS_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_BUTTONS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_BUTTONS_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_FRAGMENT_BUTTONS_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_FRAGMENT_BUTTONS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_FRAGMENT_BUTTONS_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}	
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_SHOW_OVERVIEW_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_OVERVIEW_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_OVERVIEW_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_SHOW_FOLDING_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_FOLDING_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_FOLDING_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_SHOW_ANNOTATION_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_ANNOTATION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SHOW_ANNOTATION_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}

			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_TAG_COMPLETION_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_TAG_COMPLETION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_TAG_COMPLETION_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_END_TAG_COMPLETION_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_END_TAG_COMPLETION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_END_TAG_COMPLETION_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_SMART_INDENTATION_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SMART_INDENTATION_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SMART_INDENTATION_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_SOFT_WRAPPING_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SOFT_WRAPPING_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_SOFT_WRAPPING_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}

			else if (actionName.equals(KeyPreferences.VIEWER_SHOW_NAMESPACES_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_NAMESPACES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_NAMESPACES_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEWER_SHOW_ATTRIBUTES_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_ATTRIBUTES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_ATTRIBUTES_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEWER_SHOW_COMMENTS_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_COMMENTS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_COMMENTS_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEWER_SHOW_TEXT_CONTENT_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_TEXT_CONTENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_TEXT_CONTENT_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEWER_INLINE_MIXED_CONTENT_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_INLINE_MIXED_CONTENT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEWER_INLINE_MIXED_CONTENT_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.OUTLINER_SHOW_ELEMENT_VALUES_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.OUTLINER_SHOW_ELEMENT_VALUES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.OUTLINER_SHOW_ELEMENT_VALUES_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.OUTLINER_CREATE_REQUIRED_NODES_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.OUTLINER_CREATE_REQUIRED_NODES_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.OUTLINER_CREATE_REQUIRED_NODES_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_SYNCHRONIZE_SPLITS_ACTION))
			{
				// update the debugger shortcutt
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_SYNCHRONIZE_SPLITS_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke));
				}
				else
				{
					// just blank the accelerator
					JCheckBoxMenuItem item = parent.getCheckBoxItem(KeyPreferences.VIEW_SYNCHRONIZE_SPLITS_ACTION);
					if(item != null) item.setAccelerator(null);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			
			else if (actionName.equals(KeyPreferences.VIEW_UNSPLIT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_UNSPLIT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);

					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_UNSPLIT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);

					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.VIEW_UNSPLIT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.VIEW_UNSPLIT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.VIEW_UNSPLIT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.EXECUTE_SCHEMATRON_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SCHEMATRON_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SCHEMATRON_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.EXECUTE_SCHEMATRON_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.EXECUTE_SCHEMATRON_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.EXECUTE_SCHEMATRON_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			/*else if (actionName.equals(KeyPreferences.GRID_DELETE_ACTION))
			{
			    // update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ACTION);
					if(item != null) 
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ACTION);
					if(item != null)
						if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}				
			else if (actionName.equals(KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_DELETE_COLUMN_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_COLUMN_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_COLUMN_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_COLUMN_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_COLUMN_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_COLUMN_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_DELETE_ROW_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ROW_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ROW_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_ROW_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_ROW_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_ROW_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_MOVE_ROW_UP_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_MOVE_ROW_UP_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_MOVE_ROW_UP_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_MOVE_ROW_UP_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_MOVE_ROW_UP_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_MOVE_ROW_UP_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_UNSORT_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_UNSORT_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_UNSORT_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_UNSORT_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_UNSORT_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_UNSORT_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}				
			else if (actionName.equals(KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_COPY_SHALLOW_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COPY_SHALLOW_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COPY_SHALLOW_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_COPY_SHALLOW_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_COPY_SHALLOW_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COPY_SHALLOW_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_PASTE_AS_CHILD_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_AS_CHILD_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_AS_CHILD_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_PASTE_AS_CHILD_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_PASTE_AS_CHILD_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_AS_CHILD_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_PASTE_BEFORE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_BEFORE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_BEFORE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_PASTE_BEFORE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_PASTE_BEFORE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_BEFORE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_PASTE_AFTER_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_AFTER_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_AFTER_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_PASTE_AFTER_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_PASTE_AFTER_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_PASTE_AFTER_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_COLLAPSE_ROW_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COLLAPSE_ROW_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COLLAPSE_ROW_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_COLLAPSE_ROW_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_COLLAPSE_ROW_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COLLAPSE_ROW_ACTION);
					if(item != null)
						if(item != null) item.setAccelerator(null,false);
					
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_EXPAND_ROW_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_EXPAND_ROW_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_EXPAND_ROW_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_EXPAND_ROW_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_EXPAND_ROW_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_EXPAND_ROW_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}
			else if (actionName.equals(KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION))
			{
				// update the shortcuts
				if (keystrokeSize == 1)
				{
					// only a single keystroke
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION);
					if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
				}
				else if (keystrokeSize == 2)
				{
					KeyStroke stroke = getKeyStroke(keystroke2);
				
					// we are in emacs mode, turn off accelarator from menu
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION);
					if(item != null) item.setAccelerator(stroke,true);
					
					//add the second keystroke to the mode's map
					Action action = parent.getModeAction(KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION);
					parent.getStatusbar().addToModeMap(KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION,stroke,action);
				}
				else
				{
					// just blank the accelerator
					XNGRMenuItem item = parent.getMenuItem(KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION);
					if(item != null) item.setAccelerator(null,false);
				}
			}*/
			
			// debuggerStart
			
			else if (debugger != null)
			{
				if (actionName.equals(KeyPreferences.DEBUGGER_NEW_TRANSFORMATION_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_NEW_TRANSFORMATION_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_NEW_TRANSFORMATION_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_CLOSE_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_CLOSE_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_CLOSE_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_OPEN_SCENARIO_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_OPEN_SCENARIO_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_OPEN_SCENARIO_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_CLOSE_TRANSFORMATION_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_CLOSE_TRANSFORMATION_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_CLOSE_TRANSFORMATION_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_SAVE_AS_SCENARIO_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_SAVE_AS_SCENARIO_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_SAVE_AS_SCENARIO_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_FIND_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_FIND_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_FIND_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_FIND_NEXT_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_FIND_NEXT_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_FIND_NEXT_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_GOTO_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_GOTO_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_GOTO_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_START_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_START_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_START_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_RUN_END_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_RUN_END_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_RUN_END_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_PAUSE_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_PAUSE_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_PAUSE_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STOP_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STOP_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STOP_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STEP_INTO_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STEP_INTO_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STEP_INTO_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STEP_OVER_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STEP_OVER_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STEP_OVER_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STEP_OUT_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STEP_OUT_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_STEP_OUT_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_OPEN_INPUT_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_OPEN_INPUT_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_OPEN_INPUT_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_OPEN_STYLESHEET_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_OPEN_STYLESHEET_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_OPEN_STYLESHEET_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_RELOAD_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_RELOAD_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_RELOAD_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_EXIT_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_EXIT_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_EXIT_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_COLLAPSE_ALL_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_COLLAPSE_ALL_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_COLLAPSE_ALL_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_EXPAND_ALL_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_EXPAND_ALL_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_EXPAND_ALL_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_INPUT_SHOW_FOLDING_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SHOW_FOLDING_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SHOW_FOLDING_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_INPUT_SOFT_WRAPPING_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SOFT_WRAPPING_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_INPUT_SOFT_WRAPPING_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_AUTO_OPEN_INPUT_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_AUTO_OPEN_INPUT_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_AUTO_OPEN_INPUT_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_ENABLE_TRACING_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_ENABLE_TRACING_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_ENABLE_TRACING_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_REDIRECT_OUTPUT_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_REDIRECT_OUTPUT_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke));
					}
					else
					{
						// just blank the accelerator
						JCheckBoxMenuItem item = debugger.getCheckBoxItem(KeyPreferences.DEBUGGER_REDIRECT_OUTPUT_ACTION);
						if(item != null) item.setAccelerator(null);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_SET_PARAMETERS_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_SET_PARAMETERS_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_SET_PARAMETERS_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				else if (actionName.equals(KeyPreferences.DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION))
				{
					// update the debugger shortcutt
					if (keystrokeSize == 1)
					{
						// only a single keystroke
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION);
						if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
					}
					else
					{
						// just blank the accelerator
						XNGRMenuItem item = debugger.getMenuItem(KeyPreferences.DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION);
						if(item != null) item.setAccelerator(null,false);
					}
				}
				
				
				
				
				
			}
			else {
								
				for(int cnt=0;cnt<getPluginMappings().size();++cnt) {
					Object obj = getPluginMappings().get(cnt);
					if((obj != null) && (obj instanceof PluginActionKeyMapping)) {
						PluginActionKeyMapping keyMapping = (PluginActionKeyMapping)obj;
						
						if(actionName.equals(keyMapping.getKeystroke_action_name())) {
						
							
//							 update the shortcuts
							if (keystrokeSize == 1)
							{
								// only a single keystroke
								XNGRMenuItem item = parent.getMenuItem(keyMapping.getKeystroke_action_name());
								if(item != null) 
									if(item != null) item.setAccelerator(getKeyStroke(keystroke),false);
							}
							else if (keystrokeSize == 2)
							{
								KeyStroke stroke = getKeyStroke(keystroke2);
							
								// we are in emacs mode, turn off accelarator from menu
								XNGRMenuItem item = parent.getMenuItem(keyMapping.getKeystroke_action_name());
								if(item != null)
									if(item != null) item.setAccelerator(stroke,true);
								
								//add the second keystroke to the mode's map
								Action action = parent.getModeAction(keyMapping.getKeystroke_action_name());
								parent.getStatusbar().addToModeMap(keyMapping.getKeystroke_action_name(),stroke,action);
							}
							else
							{
								// just blank the accelerator
								XNGRMenuItem item = parent.getMenuItem(keyMapping.getKeystroke_action_name());
								if(item != null) item.setAccelerator(null,false);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * @param keystroke_action_name
	 * @param keystroke_action_description
	 * @param keystroke_mask
	 * @param keystroke_value
	 */
	public PluginActionKeyMapping addKeyMapping(String keystroke_action_name, String keystroke_action_description, String keystroke_mask, String keystroke_value) {

		PluginActionKeyMapping keyMapping = new PluginActionKeyMapping();
		keyMapping.setKeystroke_action_name(keystroke_action_name);
		keyMapping.setKeystroke_action_description(keystroke_action_description);
		keyMapping.setKeystroke_mask(keystroke_mask);
		keyMapping.setKeystroke_value(keystroke_value);
		
		this.getPluginMappings().add(keyMapping);
		
		return(keyMapping);
	}

	/**
	 * @param pluginMappings the pluginMappings to set
	 */
	public void setPluginMappings(List pluginMappings) {

		this.pluginMappings = pluginMappings;
	}

	/**
	 * @return the pluginMappings
	 */
	public List getPluginMappings() {

		if(pluginMappings == null) {
			pluginMappings = new ArrayList();
		}
		return pluginMappings;
	}
	

	
} 
