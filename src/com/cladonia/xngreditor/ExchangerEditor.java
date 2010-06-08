/*
 * $Id: ExchangerEditor.java,v 1.153 2005/09/06 08:22:29 gmcgoldrick Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.bounce.CenterLayout;
import org.bounce.util.BrowserLauncher;
import org.dom4j.Node;
import org.exolab.castor.xml.schema.SchemaException;
import org.xml.sax.SAXParseException;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationEvent;
import com.apple.eawt.ApplicationListener;
import com.cladonia.schema.SchemaDocument;
import com.cladonia.schema.SchemaElement;
import com.cladonia.schema.XMLSchema;
import com.cladonia.schema.viewer.SchemaViewer;
import com.cladonia.util.loader.ExtensionClassLoader;
import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.ExchangerDocumentEvent;
import com.cladonia.xml.ExchangerDocumentListener;
import com.cladonia.xml.ExchangerXMLWriter;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
//import com.cladonia.xml.browser.Browser;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xml.designer.Designer;
import com.cladonia.xml.editor.Bookmark;
import com.cladonia.xml.editor.Editor;
import com.cladonia.xml.helper.Helper;
import com.cladonia.xml.navigator.Navigator;
import com.cladonia.xml.viewer.Viewer;
import com.cladonia.xngreditor.actions.*;

import com.cladonia.xngreditor.component.AutomaticProgressMonitor;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.grammar.FragmentProperties;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.grammar.TagCompletionProperties;
import com.cladonia.xngreditor.plugins.PluginActionKeyMapping;
import com.cladonia.xngreditor.plugins.PluginUtilities;
import com.cladonia.xngreditor.plugins.PluginView;
import com.cladonia.xngreditor.project.Project;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.properties.TextPreferences;
import com.cladonia.xslt.debugger.ui.XSLTDebuggerFrame;
import com.cladonia.xngreditor.actions.SetSchemaPropertiesAction;
import com.cladonia.xngreditor.actions.ChangeDocumentAction;
import com.cladonia.xngreditor.actions.SubstituteCharactersAction;
import com.cladonia.xngreditor.api.*;

/**
 * The desktop frame, displays the desktop services buttons and 
 * allows for adding, removing and opening services that are 
 * placed on the desktop.
 *
 * @version	$Revision: 1.153 $, $Date: 2005/09/06 08:22:29 $
 * @author Dogsbay
 */
public class ExchangerEditor extends StatusFrame implements ExchangerDocumentListener {
	private static final boolean DEBUG = false;

	private static final CompoundBorder TITLE_BORDER = 
		new CompoundBorder( 
				new CompoundBorder(
						new MatteBorder( 1, 1, 0, 0, UIManager.getColor("controlDkShadow")),
						new MatteBorder( 0, 0, 0, 1, Color.white)),
				new CompoundBorder(
						new MatteBorder( 1, 1, 0, 0, Color.white),
						new MatteBorder(0, 0, 0, 1, UIManager.getColor("controlDkShadow"))));

	private static final String TITLE = "Exchanger XML Editor";

	private static final String ICON =	"com/cladonia/xngreditor/icons/xngr-editor-icon.gif";
	private static final String CLOSE_ICON = "com/cladonia/xngreditor/icons/Close8.gif";

	

	private static final String PROJECT_ICON = "com/cladonia/xngreditor/project/icons/ProjectIcon.gif";
	private static final String NAVIGATOR_ICON = "com/cladonia/xngreditor/icons/XMLDocumentIcon.gif";
	private static final String HELPER_ICON = "com/cladonia/xngreditor/icons/HelperIcon.gif";

	private static final String SYNCHRONISE_SPLITS_ICON = "com/cladonia/xngreditor/icons/SynchroniseSplits16.gif";

	private AboutDialog aboutDialog = null;
	private int newDocumentCounter = 0;
	private Component tabbedViewParent = null;
	private JPanel fullScreenPanel = null;
	
	//	private RootSelectionDialog rootDialog = null;

	private ConfigurationProperties properties = null;
	private XSLTDebuggerFrame debugger = null;
	//	private Properties Properties = null;

	private Hashtable icons = null;
	private Hashtable menuItemMap = new Hashtable();
	private Hashtable modeActionMap = new Hashtable();

	private JPanel mainPanel = null;
	//	private SubstitutionList substitutionList = null;
	
	private JPanel buttonContainer = null; 
	
	/**
	 * this is the panel that holds the xpath editor and view buttons
	 */
	private JPanel northPanel = null; 

	private ExchangerTabbedView selectedTabbedView = null;
	private Vector tabbedViews = null;
	private JTabbedPane controllerTab = null;
	private JPopupMenu tabPopup = null;
	private ExchangerView currentView = null;
	private ExchangerView previousView = null;
	private ExchangerDocument document = null;

	private PropertiesPanel propertiesPanel = null;
	private Project projectPanel = null;
	private Helper helper = null;
	private Navigator navigator = null;
	private Statusbar statusbar = null;
	private OutputPanel outputPanel = null;
	private XPathEditor xpathEditor = null;
	private JSplitPane rightSplit = null;
	private JSplitPane tabSplit = null;
	private JSplitPane split = null;
//	private JMenu fragmentMenu = null;
	private JCheckBoxMenuItem highlightMenuItem = null;
	private JToggleButton highlightButton = null;
	private JPanel toolbarPanel = null;
	private JPanel editorToolbarPanel = null;
	private JPanel centerPanel = null;
	private JPanel splitPanel = null;
	private JPanel editorPanel = null;
	
	private JCheckBoxMenuItem showStandardButtons = null;
	private JCheckBoxMenuItem showEditorButtons = null;
	private JCheckBoxMenuItem showFragmentButtons = null;

	private JButton closeButton = null;

	private CreateSchemaAction createSchema = null;
	private SetSchemaPropertiesAction setSchemaProperties = null;

	private NewAction newDocument = null;
	private OpenAction openDocument = null;
	private OpenCurrentURLAction openCurrentURL = null;
	private OpenRemoteDocumentAction openRemoteDocument = null;
	private CloseAction closeDocument = null;
	private CloseAllAction closeAllDocuments = null;
	private ReloadAction reloadDocument = null;
	private SaveAction saveDocument = null;
	private SaveAllAction saveAllDocuments = null;
	private SaveAsAction saveDocumentAs = null;
	private SaveAsRemoteAction saveDocumentAsRemote = null;
	private SaveAsTemplateAction saveAsTemplate = null;
	private PreferencesAction preferencesAction = null;
	private UnsplitTabsAction unsplitTabs = null;
	private SplitTabsVerticallyAction splitTabsVertically = null;
	private SplitTabsHorizontallyAction splitTabsHorizontally = null;
	private JCheckBoxMenuItem synchroniseSplits = null;

	private NewGrammarAction newGrammar = null;
	private OpenGrammarAction openGrammar = null;
	private GrammarPropertiesAction grammarProperties = null;
	private ManageGrammarAction manageGrammar = null;

	private ConvertGrammarAction convertGrammar = null;
	private ConvertSVGAction convertSVG = null;
	private SendSOAPAction sendSOAP = null;
	private AnalyseWSDLAction analyseWSDL = null;
	private SignDocumentAction signDocument = null;
	private VerifySignatureAction verifySignature = null;

	private SchemaInstanceGenerationAction schemaInstance = null;
	//private GraphicalSchemaGenerationAction graphicalSchema = null;
	private ExecuteScriptAction executeScript = null;

	
	private ExecuteDefaultScenarioAction executeDefaultScenario = null;
	private ExecutePreviousScenarioAction executePreviousScenario = null;
	private DebugScenarioAction debugScenario = null;
	private ExecuteSimpleXSLTAction executeSimpleXSLT = null;
	private ExecuteXSLTAction executeXSLT = null;
	private ExecuteFOAction executeFO = null;
	private ExecuteXQueryAction executeXQuery = null;
	private ExecutePreviousXSLTAction executePreviousXSLT = null;
	private ExecutePreviousFOAction executePreviousFO = null;
	private ExecutePreviousXQueryAction executePreviousXQuery = null;
	private ExecuteSchematronAction executeSchematron = null;

	//	private ExecuteScenarioAction executeScenario				= null;
	private ManageScenarioAction manageScenario = null;
	private ManageTemplateAction manageTemplate = null;

	private UndoAction undoAction = null;
	private RedoAction redoAction = null;

	private CopyAction copyAction = null;
	private CutAction cutAction = null;
	private PasteAction pasteAction = null;

	private FindAction findAction = null;
	private ReplaceAction replaceAction = null;
	private FindNextAction findNextAction = null;

	private ExpandAllAction expandAll = null;
	private CollapseAllAction collapseAll = null;

	private HelpContentsAction helpContents = null;

	// Designer actions
	private AddNodeAction addNode = null;
	private DeleteNodeAction deleteNode = null;
	private CreateRequiredNodesAction createRequiredNodes = null;

	// Editor specific actions:
	private SelectElementAction selectElementAction = null;
	private SelectElementContentAction selectElementContentAction = null;

	private GotoStartTagAction gotoStartTagAction = null;
	private GotoEndTagAction gotoEndTagAction = null;

	private GotoNextAttributeValueAction gotoNextAttributeValueAction = null;
	private GotoPreviousAttributeValueAction gotoPreviousAttributeValueAction = null;

	private ToggleEmptyElementAction toggleEmptyElementAction = null;
	private RenameElementAction renameElementAction = null;

	private HighlightAction highlightAction = null;

	private InsertEntityAction insertEntityAction = null;
	private SubstituteCharactersAction substituteCharactersAction = null;
	private SubstituteEntitiesAction substituteEntitiesAction = null;
	private StripTagsAction stripTagsAction = null;
	private SplitElementAction splitElementAction = null;
	private TagAction tagAction = null;
	private RepeatTagAction repeatTagAction = null;
	private CDATAAction cdataAction = null;
	private CommentAction commentAction = null;
	private LockAction lockAction = null;
	private GotoAction gotoAction = null;
	private ToggleBookmarkAction toggleBookmarkAction = null;
	private SelectBookmarkAction selectBookmarkAction = null;
	private SelectFragmentAction selectFragmentAction = null;
	private IndentAction indentAction = null;
	private UnindentAction unindentAction = null;
	private FormatAction formatAction = null;
	private PrintAction printAction = null;
	private PageSetupAction pageSetupAction = null;

	private ParseAction parseAction = null;
	private ValidateAction validateAction = null;
	private ValidateSchemaAction validateSchemaAction = null;
	private ValidateDTDAction validateDTDAction = null;
	private ValidateRelaxNGAction validateRelaxNGAction = null;
	private SetXMLDeclarationAction setXMLDeclarationAction = null;
	private SetXMLDoctypeAction setXMLDoctypeAction = null;
	private SetSchemaLocationAction setSchemaLocationAction = null;
	private ResolveXIncludesAction resolveXIncludesAction = null;
	private XDiffAction xdiffAction = null;
	private CleanUpHTMLAction cleanUpHTMLAction = null;
	private OpenBrowserAction openBrowserAction = null;
	private OpenSVGAction openSVGAction = null;
	private CanonicalizeAction canonicalizeAction = null;
	private ChangeDocumentAction changeDocumentAction = null;
	
	private ImportFromTextAction importFromTextAction = null;
	private ImportFromExcelAction importFromExcelAction = null;
	private ImportFromDBTableAction importFromDBTableAction = null;
	private ImportFromSQLXMLAction importFromSQLXMLAction = null;
	
	private ToolsStripTextAction toolsStripTextAction		= null;
	private ToolsCapitalizeAction toolsCapitalizeAction		= null;
	private ToolsDeCapitalizeAction toolsDeCapitalizeAction	= null;
	private ToolsLowercaseAction toolsLowercaseAction		= null;
	private ToolsUppercaseAction toolsUppercaseAction		= null;
	private ToolsMoveNSToRootAction toolsMoveNSToRootAction = null;
	private ToolsMoveNSToFirstUsedAction toolsMoveNSToFirstUsedAction = null;
	private ToolsChangeNSPrefixAction toolsChangeNSPrefixAction = null;
	private ToolsRenameNodeAction toolsRenameNodeAction = null;
	private ToolsRemoveNodeAction toolsRemoveNodeAction = null;
	private ToolsAddNodeToNamespaceAction toolsAddNodeToNamespaceAction = null;
	private ToolsSetNodeValueAction toolsSetNodeValueAction = null;
	private ToolsAddNodeAction toolsAddNodeAction = null;
	private ToolsRemoveUnusedNSAction toolsRemoveUnusedNSAction = null;
	private ToolsConvertNodeAction toolsConvertNodeAction = null;
	private ToolsSortNodeAction toolsSortNodeAction = null;
	
	private SynchroniseSelectionAction syncSelection	= null;
	private ToggleFullScreenAction toggleFullScreen		= null;
	private JCheckBoxMenuItem toggleFullScreenMenuItem	= null;

	

	private JCheckBoxMenuItem outlinerAutoCreateRequiredNodes	= null;
	private JCheckBoxMenuItem outlinerShowAttributeValues		= null;
	private JCheckBoxMenuItem outlinerShowElementValues			= null;

	private JCheckBoxMenuItem editorLinenumberMargin	= null;
	private JCheckBoxMenuItem editorOverviewMargin		= null;
	private JCheckBoxMenuItem editorFoldingMargin		= null;
	private JCheckBoxMenuItem editorBookmarkMargin		= null;

	private JCheckBoxMenuItem editorEndTagCompletion	= null;
	private JCheckBoxMenuItem editorTagCompletion		= null;
	private JCheckBoxMenuItem editorSmartIndentation	= null;
	
	private JCheckBoxMenuItem editorErrorHighlighting	= null;

	private JCheckBoxMenuItem editorSoftWrapping		= null;
	
	private JCheckBoxMenuItem viewerShowNamespaces	= null;
	private JCheckBoxMenuItem viewerShowAttributes	= null;
	private JCheckBoxMenuItem viewerShowComments	= null;
	private JCheckBoxMenuItem viewerShowPIs			= null;
	private JCheckBoxMenuItem viewerShowContent		= null;
	private JCheckBoxMenuItem viewerInlineMixed		= null;
	
	private JCheckBoxMenuItem gridHideContainerTables 	= null;

	

	private JMenu fileMenu = null;

	private HelpSet helpset = null;
	private HelpBroker broker = null;

	private static URL macFile = null;
	private static boolean started = false;
	
	private int modeSelected = 0;
	
	private JMenu editMenu			= null;
	private JButton buttonOpen 		= null;
	private JToolBar editorToolbar	= null;
	private JToolBar toolbar		= null;

	
	private Exchanger xngr = null;
	private JPanel documentViewButtonPanel = null;
	private ButtonGroup documentViewButtonGroup = null;
	
	//declare the cut copy paste menuitems so they can be used in grid menu as well
	JMenuItem cutMenuItem = null;
	JMenuItem copyMenuItem = null;
	JMenuItem pasteMenuItem = null;
	
	private List pluginViews = null;
	
	private List actions = null;
	
	//tip of the day
	private LatestNewsModel tipsModel = null;
	private LatestNewsDialog tipOfTheDayDialog = null;
	
	private static ExtensionClassLoader extensionClassLoader = null;
	
	/**
	 * the views menu on the view menu, used to show all the different views
	 * of a document
	 */
	private JMenu viewMenu = null;
	private JMenu documentViewsMenu = null;
    
	public ExchangerEditor( ExtensionClassLoader loader, ConfigurationProperties properties) {
		
		this.setExtensionClassLoader(loader);
		
		setTitle(TITLE);
		setIconImage(getIcon(ICON).getImage());

		mainPanel = new JPanel(new BorderLayout());
		
		setContentPane(mainPanel);

		this.setProperties(properties);

		this.setPluginViews(PluginUtilities.loadPlugins());
		for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
			Object obj = this.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					pluginView.setExchangerEditor(this);
					pluginView.loadActions();
				}
			}
		}
		
		statusbar = new Statusbar(this);
		mainPanel.add(statusbar, BorderLayout.SOUTH);
		
		// check for emacs editing key configuration
		if (properties.getKeyPreferences().getActiveConfiguration().equalsIgnoreCase(KeyPreferences.EMACS))
		{
			setEmacsModeOn(true);
		}

		ExchangerTabbedView startTabbedView = new ExchangerTabbedView( this, null);
		startTabbedView.setScrollTabs( this.getProperties().isScrollDocumentTabs());
		tabbedViews = new Vector();
		tabbedViews.addElement( startTabbedView);
		
     	xngr = new Exchanger(this, properties);
		outputPanel = new OutputPanel(this, properties);

		setProjectPanel(new Project(this, properties));
//		projectPanel.setBorder(new EmptyBorder(2, 2, 1, 1));

		navigator = new Navigator(this, properties.getNavigatorProperties());
//		navigator.setBorder(new EmptyBorder(0, 2, 1, 1));

		helper = new Helper(this, properties.getHelperProperties());
//		helper.setBorder(new EmptyBorder(2, 2, 1, 1));

		controllerTab = new JTabbedPane();
		controllerTab.setFont(controllerTab.getFont().deriveFont(Font.PLAIN));
		controllerTab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent event) {
				Component comp = controllerTab.getSelectedComponent();

				getProjectPanel().setActionsEnabled(comp == getProjectPanel());

				if (comp == navigator) {
					navigator.updateOutline();
				} else if (comp == helper) {
					helper.updateInformation();
				}
			}
		});

		controllerTab.setRequestFocusEnabled(false);
		controllerTab.addTab(null, getIcon(PROJECT_ICON), getProjectPanel());
		controllerTab.setToolTipTextAt( 0, "Projects");
		controllerTab.addTab(null, getIcon(NAVIGATOR_ICON), navigator);
		controllerTab.setToolTipTextAt( 1, "Navigator");
		controllerTab.addTab(null, getIcon(HELPER_ICON), helper);
		controllerTab.setToolTipTextAt( 2, "Helper");

		JPanel controllerTabPanel = new JPanel(new BorderLayout());
		controllerTabPanel.setBorder(
			new CompoundBorder(
				new MatteBorder( 1, 1, 0, 0,
					UIManager.getColor("controlDkShadow")),
					new MatteBorder(0, 0, 1, 1, Color.white)));
		controllerTabPanel.add( controllerTab, BorderLayout.CENTER);
		propertiesPanel = new PropertiesPanel( properties, this);
		controllerTabPanel.add( propertiesPanel, BorderLayout.SOUTH);

		editorToolbar = createEditorToolbar();
		editorToolbar.setVisible( properties.isShowEditorToolbar());

		editorToolbarPanel = new JPanel( new BorderLayout());
		editorToolbarPanel.add( editorToolbar, BorderLayout.CENTER);
		editorToolbarPanel.setVisible( properties.isShowEditorToolbar());
		editorToolbarPanel.setBorder( TITLE_BORDER);

		editorPanel = new JPanel( new BorderLayout());
		editorPanel.add( startTabbedView, BorderLayout.CENTER);
		editorPanel.add( editorToolbarPanel, BorderLayout.NORTH);

		rightSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, editorPanel, outputPanel);
		rightSplit.setResizeWeight( 1);

		if ( rightSplit.getDividerSize() > 6) {
			rightSplit.setDividerSize( 6);
		}

		Object ui = rightSplit.getUI();
		if (ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI) ui).getDivider().setBorder(null);
		}
		rightSplit.setBorder( null);
		rightSplit.setDividerLocation(properties.getDividerLocation());
		rightSplit.setOneTouchExpandable( true);

		split = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, controllerTabPanel, rightSplit);

		if ( split.getDividerSize() > 6) {
			split.setDividerSize( 6);
		}
		split.setBorder(null);
		ui = split.getUI();
		if (ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI) ui).getDivider().setBorder(null);
		}
		split.setDividerLocation( properties.getTopDividerLocation());
		split.setOneTouchExpandable( true);

		splitPanel = new JPanel( new BorderLayout());
		splitPanel.add( split, BorderLayout.CENTER);

		centerPanel = new JPanel( new BorderLayout());
		centerPanel.add( splitPanel, BorderLayout.CENTER);

		mainPanel.add( centerPanel, BorderLayout.CENTER);

		setNorthPanel(new JPanel(new BorderLayout()));
		northPanel.setBorder( new EmptyBorder(2, 0, 0, 0));

		splitPanel.add( northPanel, BorderLayout.NORTH);

		setButtonContainer(new JPanel(new BorderLayout()));
		setDocumentViewButtonPanel(new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0)));
		getDocumentViewButtonPanel().setBorder(new EmptyBorder(0, 0, 2, 0));
		getButtonContainer().add(documentViewButtonPanel, BorderLayout.SOUTH);
		
		documentViewButtonGroup = new ButtonGroup();

		JPanel middlePanel =
			new JPanel(new CenterLayout(CenterLayout.VERTICAL));
		middlePanel.add(getButtonContainer(), CenterLayout.CENTER);
		northPanel.add(middlePanel, BorderLayout.EAST);

		createMenubar();
		
		// set the active configurations keyboard shortcut settings
		String activeConfig = properties.getKeyPreferences().getActiveConfiguration();
		properties.getKeyPreferences().setKeyMappings(this,activeConfig);

		toolbar = createToolbar();
		toolbar.setVisible( properties.isShowToolbar());
		toolbarPanel = new JPanel( new BorderLayout());
		toolbarPanel.add( toolbar, BorderLayout.CENTER);
		toolbarPanel.setVisible( properties.isShowToolbar());
		toolbarPanel.setBorder( new MatteBorder( 0, 0, 1, 0, UIManager.getColor("controlDkShadow")));
		centerPanel.add( toolbarPanel, BorderLayout.NORTH);

		xpathEditor = new XPathEditor( this, outputPanel, properties);
		northPanel.add( xpathEditor, BorderLayout.CENTER);

		
		// set the size of the buttons.
		int height = toolbar.getComponentAtIndex(0).getPreferredSize().height;
		
		fullScreenPanel = new JPanel( new BorderLayout());

		updateGrammarActions();
		//		updateScenarioActions();

		HelpBroker broker = createHelpBroker();
		HelpSet hs = getHelpSet();

		if (hs != null) {
			broker.enableHelpKey(mainPanel, "exchanger.intro", hs);
		}

		if(properties.isWindowMaximised() == true) {
		
			this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
		}
		else {
			//set it to a specific size
			setSize(properties.getDimension());
		}

		setLocation(properties.getPosition());

		if (System.getProperty("mrj.version") != null) {
			//			System.out.println( "Apple Mac found");
			new MacOSApplicationAdapter(this);
		}
		
		this.addWindowStateListener(new WindowStateListener() {

			/*
			 * NORMAL
		     * ICONIFIED
		     * MAXIMIZED_HORIZ
		     * MAXIMIZED_VERT
		     * MAXIMIZED_BOTH
		     * MAXIMIZED_HORIZ
		     * MAXIMIZED_VERT
		     */
		
			public void windowStateChanged(WindowEvent e) {
				boolean DEBUG = false;
				
				int oldState = e.getOldState();
	            int newState = e.getNewState();
	    
	            if ((oldState & Frame.ICONIFIED) == 0
	                && (newState & Frame.ICONIFIED) != 0) {
	            	if(DEBUG) System.out.println("Frame was iconized");
	            } else if ((oldState & Frame.ICONIFIED) != 0
	                && (newState & Frame.ICONIFIED) == 0) {
	            	if(DEBUG) System.out.println("Frame was deiconized");
	            }
	    
	            if ((oldState & Frame.MAXIMIZED_BOTH) == 0
	                && (newState & Frame.MAXIMIZED_BOTH) != 0) {
	            	if(DEBUG) System.out.println("Frame was maximized");
	            	ExchangerEditor.this.properties.setWindowMaximised(true);
	            	
	            } else if ((oldState & Frame.MAXIMIZED_BOTH) != 0
	                && (newState & Frame.MAXIMIZED_BOTH) == 0) {
	            	if(DEBUG) System.out.println("Frame was minimized");
	            	setSize(ExchangerEditor.this.properties.getDimension());
	            }

				


			}
			
		});

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exit();
			}
			
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		ExchangerXMLWriter.setAttributeOnNewLine(
			properties.isAttributesNewLine());

		if (properties.getEditorProperties().isWrapText()) {
			ExchangerXMLWriter.setMaxLineLength(
				properties.getEditorProperties().getWrappingColumn());
		} else {
			ExchangerXMLWriter.setMaxLineLength(-1);
		}

		ExchangerXMLWriter.setIndentString(TextPreferences.getTabString());
		
		startTabbedView.setSelected( true);
		
		
		// Run in Thread!!!
 		Runnable runner = new Runnable() {
 			public void run()  {
		 		try {
		 			//tip of the day
		 			
		 			LatestNewsModel latestNewsModel = new LatestNewsModel();
		 			
		 			ExchangerEditor.this.setTipsModel(latestNewsModel);
		 			
		 			ExchangerEditor.this.tipOfTheDayDialog = new LatestNewsDialog(ExchangerEditor.this, true, ExchangerEditor.this.getTipsModel());
		 			
		 			if(ExchangerEditor.this.getTipsModel() != null) {
						if(ExchangerEditor.this.tipOfTheDayDialog != null) {
							//tipOfTheDayDialog.setCurrentTip(0);		
							if(ExchangerEditor.this.tipsModel.getTipCount() > 0) {
								ExchangerEditor.this.tipOfTheDayDialog.show();
							}
						}
						else {
							//System.err.println("ExchangerEditor::setVisible - tipOftheDayDialog is null");
						}
					}
					else {
						//System.err.println("ExchangerEditor::setVisible - tipModel is null");
					}
		 			
		 		} catch ( Exception e) {
		 			// This should never happen, just report and continue
		 			//MessageHandler.showUnexpectedError( e);
		 			e.printStackTrace();
		 		} finally {
			 		
		 		}
 			}
 		};
 		
 		// Create and start the thread ...
 		Thread thread = new Thread( runner);
 		thread.start();
		
		//tipOfTheDayDialog.setUI(new XngrTipOfTheDayUI());
		
		//tipOfTheDayDialog.setPreferredSize(new Dimension(500, 350));
        
	}
	
	public void hideDocumentViewButton() {
		if((this.getDocumentViewButtonGroup() != null) && (this.getDocumentViewButtonPanel() != null)) {
			
		}
	}
	
	
	
	/**
	 *  turns the emacs editing mode on\off depending on condition boolean
	 *
 	 * @param condition boolean which decides whether to turn emacs editing mode on\off
	 */
	public void setEmacsModeOn(boolean condition)
	{
		KeyStroke ctrlX = KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.CTRL_MASK, false);
		
		if (condition)
		{
			//	add the Ctrl-X editing mode
			
			Action modeAction = new AbstractAction(){	
				public void actionPerformed(ActionEvent e)	
				{	
					statusbar.getModeField().requestFocus();
				}};
			
			mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlX, "MODE");
			mainPanel.getActionMap().put("MODE", modeAction);
			statusbar.setModeFocusable(true);
		}
		else
		{
			mainPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ctrlX, "none");
			statusbar.setModeFocusable(false);
		}
		
	}

	public JTabbedPane getControllerTabbedPane()
	{
	  return controllerTab;
	
	}

	public Vector getGrammarProperties()
	{
	  return getProperties().getGrammarProperties();
	
	}

	
	public void switchToProjectTab() {
		controllerTab.setSelectedIndex(0);
	}
	
	public boolean isFullScreen() {
		return tabbedViewParent != null;
	}
	
	public void updateGrammar( GrammarProperties grammar) {
		Vector views = getViews();

		if ( views != null) {
			for ( int i = 0; i < views.size(); i++) {
				((ExchangerView)views.elementAt(i)).updateGrammar( grammar);
			}
			
			navigator.setDocument( document);
			updateStatus();
		}
	}
	
	int rightSplitDividerLocation = -1;
	int splitDividerLocation = -1;

    private CopyErrorListAction copyErrorListAction;

    /*private GridBridgeAddAttributeToSelectedAction gridBridgeAddAttributeToSelectedAction;

    private GridBridgeAddAttributeColumnAction gridBridgeAddAttributeColumnAction;

    private GridBridgeAddChildTableAction gridBridgeAddChildTableAction;

    private GridBridgeAddElementAfterAction gridBridgeAddElementAfterAction;
    private GridBridgeAddElementBeforeAction gridBridgeAddElementBeforeAction;

    
    private GridBridgeMoveRowUpAction gridBridgeMoveRowUpAction;

    private GridBridgeMoveRowDownAction gridBridgeMoveRowDownAction;

    private GridBridgeEditAttributeNameAction gridBridgeEditAttributeNameAction;

    private GridBridgeDeleteRowAction gridBridgeDeleteElementAction;

    private GridBridgeDeleteColumnAction gridBridgeDeleteColumnAction;

    private GridBridgeAddTextToSelectedAction gridBridgeAddTextToSelectedAction;

    private GridBridgeAddTextColumnAction gridBridgeAddTextColumnAction;

    private GridBridgeDeleteChildTableAction gridBridgeDeleteChildTableAction;

    private GridBridgeDeleteAttsAndTextAction gridBridgeDeleteAttsAndTextAction;

    

    private GridBridgeDeleteSelectedAttributeAction gridBridgeDeleteSelectedAttributeAction;

    private GridBridgeDeleteSelectedTextAction gridBridgeDeleteSelectedTextAction;

    private GridBridgeCopyShallowAction gridBridgeCopyShallowAction;

    private GridBridgePasteAsChildAction gridBridgePasteAsChildAction;

    private GridBridgePasteBeforeAction gridBridgePasteBeforeAction;

    private GridBridgePasteAfterAction gridBridgePasteAfterAction;

    private GridBridgeGotoChildTableAction gridBridgeGotoChildTableAction;

    private GridBridgeGotoParentTableAction gridBridgeGotoParentTableAction;

    private GridBridgeEditSelectedAttributeNameAction gridBridgeEditSelectedAttributeNameAction;

    private GridBridgeSortTableAscendingAction gridBridgeSortTableAscendingAction;

    private GridBridgeSortTableDescendingAction gridBridgeSortTableDescendingAction;

    private GridBridgeUnsortTableAction gridBridgeUnsortTableAction;

    private GridBridgeExpandRowAction gridBridgeExpandRowAction;

    private GridBridgeCollapseRowAction gridBridgeCollapseRowAction;

    private GridBridgeCollapseCurrentTableAction gridBridgeCollapseCurrentTableAction;

    private GridBridgeDeleteAction gridBridgeDeleteAction;
*/
    
    private JCheckBoxMenuItem gridSupportMixedContent;
    
	private JCheckBoxMenuItem gridSchemaAware;

	private JCheckBoxMenuItem gridSchemaHighlightRequired;

	private JRadioButtonMenuItem gridToolbarShowOnLeft;

	private JRadioButtonMenuItem gridToolbarShowOnTop;

	private JRadioButtonMenuItem gridToolbarHide;

	private FindInFilesAction findInFilesAction;

    
	
	public void toggleFullScreen() {
		boolean full = true;
		
		if ( rightSplit.getDividerLocation() < rightSplit.getMaximumDividerLocation()) {
			rightSplitDividerLocation = rightSplit.getDividerLocation();
			rightSplit.setDividerLocation( (double)1.0);
			full = false;
		}

		if ( split.getDividerLocation() > 0) {
			splitDividerLocation = split.getDividerLocation();
			split.setDividerLocation( (double)0);
			full = false;
		} 
		
		if ( full) {
			split.setDividerLocation( splitDividerLocation);
			rightSplit.setDividerLocation( rightSplitDividerLocation);
			
			Vector views = getViews();
			if ( views != null) {
				for ( int i = 0; i < views.size(); i++) {
					ExchangerView view = (ExchangerView)views.elementAt(i);
					view.getEditor().scrollCursorToVisible();
				}
			}
		}

		// Not null, turn full screen off!
//		if ( tabbedViewParent != null) {
//			splitPanel.remove( fullScreenPanel);
//			fullScreenPanel.remove( editorToolbarPanel);
//			fullScreenPanel.remove( selectedTabbedView);
//
//			if ( tabbedViewParent instanceof JSplitPane) {
//				JSplitPane split = (JSplitPane)tabbedViewParent;
//
//				if ( split.getLeftComponent() == null) {
//					split.setLeftComponent( selectedTabbedView);
//				} else {
//					split.setRightComponent( selectedTabbedView);
//				}
//			} else {
//				JPanel panel = (JPanel)tabbedViewParent;
//				panel.add( selectedTabbedView, BorderLayout.CENTER);
//			}
//
//			editorPanel.add( editorToolbarPanel, BorderLayout.NORTH);
//			splitPanel.add( split);
//			splitPanel.revalidate();
//			splitPanel.repaint();
//			tabbedViewParent = null;
//		} else { // turn full screen on!
//			tabbedViewParent = selectedTabbedView.getParent();
//			
//			splitPanel.remove( split);
//			editorPanel.remove( editorToolbarPanel);
//
//			fullScreenPanel.add( editorToolbarPanel, BorderLayout.NORTH);
//			fullScreenPanel.add( selectedTabbedView, BorderLayout.CENTER);
//			
//			splitPanel.add( fullScreenPanel, BorderLayout.CENTER);
//			splitPanel.revalidate();
//			splitPanel.repaint();
//		}
//
//		getSplitTabsHorizontallyAction().setEnabled( selectedTabbedView.getViews().size() > 1 && !isFullScreen());
//		getSplitTabsVerticallyAction().setEnabled( selectedTabbedView.getViews().size() > 1 && !isFullScreen());
//		getUnsplitTabsAction().setEnabled( tabbedViews.size() > 1 && !isFullScreen());
//
//		if ( tabbedViews.size() > 1 && !isFullScreen()) {
//			synchroniseSplits.setEnabled( true);
//		} else {
//			synchroniseSplits.setEnabled( false);
//		}
//		
//		toggleFullScreenMenuItem.setSelected( isFullScreen());
	}
	
	public XPathEditor getXPathEditor() {
		return xpathEditor;
	}

	public Navigator getNavigator() {
		return navigator;
	}

	public Helper getHelper() {
		return helper;
	}

	public ExchangerDocument getDocument() {
		if (currentView != null) {
			return currentView.getDocument();
		}

		return null;
	}

	public ViewPanel getCurrent() {
		if (currentView != null) {
			return currentView.getCurrentView();
		}

		return null;
	}

	public OutputPanel getOutputPanel() {
		return outputPanel;
	}

	public Exchanger getExchangerContext() {
		return xngr;
	}

	public ExchangerView getView( ExchangerDocument document) {
		Vector views = getViews();
		
		for ( int i = 0; i < views.size(); i++) {
			ExchangerView view = (ExchangerView)views.elementAt(i);

			if ( view.getDocument() == document) {
				return view;
			}
		}

		return null;
	}

	public ExchangerView getView() {
		return currentView;
	}
	
	public ExchangerView getPreviousView() {
		return previousView;
	}

	public void addBookmark( Bookmark bookmark) {
		outputPanel.addBookmark( bookmark);
	}

	public void removeBookmark( Bookmark bookmark) {
		outputPanel.removeBookmark( bookmark);
	}

	public void setView( ExchangerView tab) {

		if ( currentView != tab) {
			previousView = currentView;
		}
		
		currentView = tab;

		if (tab != null) {
			ViewPanel current = tab.getCurrentView();
			highlightButton.setSelected( currentView.getEditor().isHighlight());
			highlightMenuItem.setSelected( currentView.getEditor().isHighlight());

			outputPanel.setErrorList( tab.getErrors());
			outputPanel.setXPathList( tab.getXPathList());

			setDocumentInternal( tab.getDocument(), false);
			setSchemaInternal(tab.getSchema());

			navigator.setDocument( tab.getDocument());

			updateGrammarActions();
			updateFragments();
			//			updateScenarioActions();

			currentView.changeView(current);
			
			this.setDocumentViewButtonPanel(tab.getDocumentViewButtonPanel());
			this.updateDocumentViewButtonPanel();
			
			this.setDocumentViewsMenu(tab.getDocumentViewsMenu());
			this.updateDocumentViewMenu();

			ChangeManager manager = currentView.getChangeManager();

			getUndoAction().setChangeManager(manager);
			getRedoAction().setChangeManager(manager);
			getCloseAction().setEnabled(true);
			getCloseAllAction().setEnabled(true);
			getReloadAction().setEnabled(true);

		} else {
			highlightButton.setSelected( false);
			highlightMenuItem.setSelected( false);

			outputPanel.setErrorList( null);
			outputPanel.setXPathList( null);

			getCloseAction().setEnabled(false);
			getCloseAllAction().setEnabled(false);
			getReloadAction().setEnabled(false);

			getUndoAction().setChangeManager(null);
			getRedoAction().setChangeManager(null);

			setDocumentInternal( null, false);
			setSchemaInternal(null);
			updateGrammarActions();
			updateFragments();

			navigator.setDocument( null);
			//			updateScenarioActions();
			setCurrent(null);
		}

		getSplitTabsHorizontallyAction().setEnabled( selectedTabbedView.getViews().size() > 1 && !isFullScreen());
		getSplitTabsVerticallyAction().setEnabled( selectedTabbedView.getViews().size() > 1 && !isFullScreen());
		getUnsplitTabsAction().setEnabled( tabbedViews.size() > 1 && !isFullScreen());

		if ( tabbedViews.size() > 1 && !isFullScreen()) {
			synchroniseSplits.setEnabled( true);
		} else {
			synchroniseSplits.setEnabled( false);
		}

		updateStatus();
		setTitle(currentView);
	}

	public void setCurrent(ViewPanel view) {
		
		outputPanel.setCurrent(view);
		helper.setView(view);
		getCutAction().setView(view);
		getCopyAction().setView(view);
		getPasteAction().setView(view);
		getFindAction().setView(view);
		getFindNextAction().setView(view);
		getReplaceAction().setView(view);

		getSelectElementAction().setView(view);
		getSelectElementContentAction().setView(view);

		getGotoStartTagAction().setView(view);
		getGotoEndTagAction().setView(view);
		getToggleEmptyElementAction().setView(view);
		getRenameElementAction().setView(view);
		getHighlightAction().setView(view);
		getGotoNextAttributeValueAction().setView(view);
		getGotoPreviousAttributeValueAction().setView(view);

		getTagAction().setView(view);
		getRepeatTagAction().setView(view);
		getCommentAction().setView(view);
		getLockAction().setView(view);
		getCDATAAction().setView(view);
		getGotoAction().setView(view);
		getToggleBookmarkAction().setView(view);
		getSelectBookmarkAction().setView(view);
		getSelectFragmentAction().setView(view);
		getParseAction().setView(view);
		getCanonicalizeAction().setView(view);
		getValidateAction().setView(view);
		getValidateSchemaAction().setView(view);
		getValidateDTDAction().setView(view);
		getValidateRelaxNGAction().setView(view);
		getIndentAction().setView(view);
		getInsertEntityAction().setView(view);
		getSubstituteCharactersAction().setView(view);
		getSubstituteEntitiesAction().setView(view);
		getStripTagsAction().setView(view);
		getSplitElementAction().setView(view);
		getUnindentAction().setView(view);
		getFormatAction().setView(view);

		getAddNodeAction().setView(view);
		getCreateRequiredNodesAction().setView(view);
		getDeleteNodeAction().setView(view);

		getExpandAllAction().setView(view);
		getCollapseAllAction().setView(view);
		getSynchroniseSelectionAction().setView(view);
		
		//grid specific
		
		/*getGridBridgeAddAttributeToSelectedAction().setView(currentView);
		getGridBridgeAddAttributeColumnAction().setView(currentView);
		getGridBridgeAddChildTableAction().setView(currentView);
		getGridBridgeDeleteChildTableAction().setView(currentView);
		getGridBridgeAddElementBeforeAction().setView(currentView);
		getGridBridgeAddElementAfterAction().setView(currentView);
		getGridBridgeAddTextToSelectedAction().setView(currentView);
		getGridBridgeAddTextColumnAction().setView(currentView);
		getGridBridgeDeleteAttsAndTextAction().setView(currentView);
		getGridBridgeDeleteColumnAction().setView(currentView);
		getGridBridgeDeleteSelectedAttributeAction().setView(currentView);
		getGridBridgeDeleteSelectedTextAction().setView(currentView);
		getGridBridgeDeleteElementAction().setView(currentView);
		getGridBridgeEditAttributeNameAction().setView(currentView);
		getGridBridgeEditSelectedAttributeNameAction().setView(currentView);
		getGridBridgeMoveRowDownAction().setView(currentView);
		getGridBridgeMoveRowUpAction().setView(currentView);
		getGridBridgeSortTableAscendingAction().setView(currentView);
		getGridBridgeSortTableDescendingAction().setView(currentView);
		getGridBridgeUnsortTableAction().setView(currentView);
		
		getGridBridgeGotoParentTableAction().setView(currentView);
		getGridBridgeGotoChildTableAction().setView(currentView);
		getGridBridgeCopyShallowAction().setView(currentView);
		getGridBridgePasteAfterAction().setView(currentView);
		getGridBridgePasteAsChildAction().setView(currentView);
		getGridBridgePasteBeforeAction().setView(currentView);
		getGridBridgeCollapseRowAction().setView(currentView);
		getGridBridgeExpandRowAction().setView(currentView);
		getGridBridgeCollapseCurrentTableAction().setView(currentView);*/

//		for each of the plugin buttons
		for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
			Object obj = this.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					List actionList = pluginView.getActions();
					for(int acnt=0;acnt<actionList.size();++acnt) {
						PluginActionKeyMapping pluginAction = (PluginActionKeyMapping) actionList.get(acnt);
						pluginAction.getAction().setView(view);
					}
				}				
			}
		}
		
		//refactoring tools
		getToolsStripTextAction().setView(view);
		getToolsCapitalizeAction().setView(view);
		getToolsDeCapitalizeAction().setView(view);
		getToolsLowercaseAction().setView(view);
		getToolsUppercaseAction().setView(view);
		getToolsMoveNSToRootAction().setView(view);
		getToolsMoveNSToFirstUsedAction().setView(view);
		getToolsChangeNSPrefixAction().setView(view);
		getToolsRenameNodeAction().setView(view);
		getToolsRemoveNodeAction().setView(view);
		getToolsAddNodeToNamespaceAction().setView(view);
		getToolsSetNodeValueAction().setView(view);
		getToolsAddNodeAction().setView(view);
		getToolsRemoveUnusedNSAction().setView(view);
		getToolsConvertNodeAction().setView(view);
		getToolsSortNodeAction().setView(view);
		
		if (view instanceof Editor) {
			((Editor) view).updateHelper();
		} else if (view instanceof Designer) {
			((Designer) view).updateHelper();
		} else if (view instanceof Viewer) {
			((Viewer) view).updateHelper();
		} else if (view instanceof SchemaViewer) {
			((SchemaViewer) view).updateHelper();
		/*} else if (currentView instanceof Grid) {
			if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
				((Grid) currentView).updateHelper();
			}*/
		} else if (view instanceof PluginViewPanel) {
			((PluginViewPanel) view).updateHelper();
		}
		
		updateFragments();
	}

	private void setTitle( ExchangerView view) {
		StringBuffer title = new StringBuffer(TITLE);

		if (view != null) {
			ExchangerDocument doc = view.getDocument();

			title.append(" - [");

			URL url = doc.getURL();

			if (url != null) {
				title.append( URLUtilities.toRelativeString( url));
			} else {
				title.append(doc.getName());
			}

			title.append("]");
		}

		setTitle(title.toString());
	}

	public void select( ExchangerView view) {
		for ( int i = 0; i < tabbedViews.size(); i++) {
			ExchangerTabbedView tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(i);

			if ( tabbedView.contains( view)) {
				if ( isFullScreen() && !tabbedView.isSelected()) {
					toggleFullScreen();
				}
				tabbedView.select( view);

				return;
			}
		}
	}

	public void select( ExchangerDocument document) {
		select( getView( document));
	}

	public void setStatus( final String status) {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				statusbar.setStatus(status);
			}
		});
	}

	public void setViewIcon( ExchangerView view, Icon icon) {
		for ( int i = 0; i < tabbedViews.size(); i++) {
			ExchangerTabbedView tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(i);

			if ( tabbedView.contains( view)) {
				tabbedView.setIcon( view, icon);
				return;
			}
		}
	}

	public void setViewTitle( ExchangerView view, String title) {
		for ( int i = 0; i < tabbedViews.size(); i++) {
			ExchangerTabbedView tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(i);

			if ( tabbedView.contains( view)) {
				tabbedView.setTitle( view, title);
				break;
			}
		}

		setTitle( view);
	}

	public Vector getViews() {
		Vector result = new Vector();

		for ( int i = 0; i < tabbedViews.size(); i++) {
			Vector views = ((ExchangerTabbedView)tabbedViews.elementAt(i)).getViews();

			for ( int j = 0; j < views.size(); j++) {
				result.addElement( views.elementAt(j));
			}
		}

		return result;
	}

	public void closeAll() {
		Vector views = getViews();

		int result = 0;
		//possible results can be
		//CANCEL, YES(Save), NO(Dont save), NO TO ALL(Dont save any)

		for ( int i = 0; i < views.size(); i++) {
			ExchangerView view = (ExchangerView) views.elementAt(i);
			view.setProperties();
			
			result = closeNotThreaded( view, result, (views.size()-i));
			
			if (result == MessageHandler.CONFIRM_CANCEL_OPTION) {
				break;
			}
			
			
		}
	}

	public void exit() {
		closeAll();

		if ( getViews().size() == 0) {
			helper.setProperties();

			saveProperties();

			getProperties().setDimension(getSize());

			if ( rightSplitDividerLocation != -1 && rightSplit.getDividerLocation() >= rightSplit.getMaximumDividerLocation()) {
				getProperties().setDividerLocation( rightSplitDividerLocation);
			} else {
				getProperties().setDividerLocation( rightSplit.getDividerLocation());
			}

			if ( split.getDividerLocation() == 0 && splitDividerLocation != -1) {
				getProperties().setTopDividerLocation( splitDividerLocation);
			}  else {
				getProperties().setTopDividerLocation( split.getDividerLocation());
			}
			
			getProperties().setPosition(getLocation());
			getProperties().save();
			getProperties().saveToDisk();

			if ( !debugger.isVisible()) {
				System.exit(0);
			} else {
				setVisible(false);
			}
			return;
		}
	}

	private void saveProperties() {
		//		if ( Properties != null) {
		//			// first time!
		//			if ( Properties.getProperty( ".base.class.path") == null) {
		//				Properties.setProperty( ".nl.java.option.additional", "\"-Xbootclasspath/p:lib/xalan.jar\"");
		//				Properties.setProperty( ".base.class.path", Properties.getProperty( ".class.path"));
		//			}
		//
		//			Properties.setProperty( ".nl.java.option.java.heap.size.initial", ""+(properties.getInitialHeapSize() * 1024 * 1024));
		//			Properties.setProperty( ".nl.java.option.java.heap.size.max", ""+(properties.getMaximumHeapSize() * 1024 * 1024));
		//
		//			StringBuffer classpath = new StringBuffer( Properties.getProperty( ".base.class.path"));
		//			Vector extensions = properties.getExtensions();
		//			for ( int i = extensions.size()-1; i >= 0; i--) {
		//				classpath.append( File.pathSeparator);
		//				classpath.append( (String)extensions.elementAt(i));
		//			}
		//			
		//			Properties.setProperty( ".class.path", classpath.toString());
		//
		//			try {
		//				Properties.store( new FileOutputStream( new File( Main.getFilePath())), "");
		//			} catch (IOException e) {
		//				e.printStackTrace();
		//			}
		//		}
	}

	public ChangeManager getChangeManager() {
		if (currentView != null) {
			return currentView.getChangeManager();
		}

		return null;
	}

	public void open(URL url, GrammarProperties type, boolean monitorProgress) {
		AutomaticProgressMonitor monitor = null;

		if (DEBUG)
		  System.out.println("open(URL url, GrammarProperties type, boolean monitorProgress)");
		
		if ( monitorProgress) {
			monitor = new AutomaticProgressMonitor( this, null, "Opening \""+URLUtilities.toString( url)+"\".", 250);
		}
		setVisible(true);

		boolean unrecoverableError = false;
		boolean alreadyLoaded = false;
		ExchangerDocument document = null;

		try {
			if ( !getProperties().isMultipleDocumentOccurrences()) {
				Vector views = getViews();
	
				for (int i = 0; i < views.size(); i++) {
					ExchangerView view = (ExchangerView) views.elementAt(i);
	
					// file already in editor?
					URL viewURL = view.getDocument().getURL();
					if (viewURL != null
						&& viewURL.toString().equals(url.toString())) {
						select(view);
						alreadyLoaded = true;
					}
				}
			}
			if (!alreadyLoaded) {
				if ( monitor != null) {
					monitor.start();
				}

								
				document = new ExchangerDocument(url);
				document.load();
			}
			// set the document somewhere....
		} catch (SAXParseException spe) {
			// This is returned from the document, do not report???
			// spe.printStackTrace();
		} catch (IOException ie) {
			if ( ie instanceof UnsupportedEncodingException) {
				MessageHandler.showError( "Could not open " + url.getFile()+"\nUnsupported Encoding: "+ie.getMessage(), "Document Creation Error");
			} else {
				MessageHandler.showError( "Could not open " + url.getFile(), ie, "Document Creation Error");
			}

			unrecoverableError = true;
		} catch (Exception e) {
//			System.out.println( "*** ERRROR FOUND!")
			MessageHandler.showError( "Could not open Document.", e, "Document Creation Error");
			e.printStackTrace();
		} finally {
			if ( monitor != null && !monitor.isCanceled()) {
				// the document is loaded, stop monitoring!
				monitor.stop();
			}

			if ( !alreadyLoaded && !unrecoverableError && (monitor == null || !monitor.isCanceled())) {
				if ( url.getProtocol().equals( "file"))	{
					getProperties().setLastOpenedDocument( url.getFile());
				} else {
					getProperties().setLastOpenedURL( url);
				}

				open(document, type);				
			}
		}
	}

	public void open( ExchangerDocument document, GrammarProperties type) {
		if (DEBUG)
		  System.out.println("open( ExchangerDocument document, GrammarProperties type)");

		if (getProperties().isCheckTypeOnOpening()) {
			type = FileUtilities.getType(document, type);
		} else {
			type = null;
		}

		XMLSchema schema = FileUtilities.createSchema(document, type);
		Vector tagCompletionSchemas = FileUtilities.createTagCompletionSchemas( document, type, schema);

		// end result can either be a selected type or no type selected
		// get schema/dtd...
		open(document, schema, tagCompletionSchemas, type);
	}

	public void open( ExchangerDocument document, XMLSchema schema, Vector tagCompletionSchemas, GrammarProperties type) {
		if (DEBUG)
		  System.out.println("open( ExchangerDocument document, XMLSchema schema, Vector tagCompletionSchemas, GrammarProperties type)");

		if (document.getURL() == null) {
			document.setName("New Document " + (newDocumentCounter + 1));
			newDocumentCounter++;
		}

		createView(document, schema, tagCompletionSchemas, type);

		if (getProperties().isValidateOnOpening()
			&& !document.isError()
			&& document.isXML()) {
			getValidateAction().execute();
		}

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
	}

	public int closeNotThreaded( ExchangerView view, int previousResult, int numberOfViews) {
		
		int result = MessageHandler.CONFIRM_YES_OPTION;
		ExchangerDocument doc = view.getDocument();
		if(previousResult == MessageHandler.CONFIRM_NO_TO_ALL_OPTION) {
			result = previousResult;
		}
		
		if (( view.isChanged() == true) && (previousResult != MessageHandler.CONFIRM_NO_TO_ALL_OPTION)) {
			if(numberOfViews > 1) {
				result = MessageHandler.showConfirmYesNoNoToAll( this, "Save changes to " + doc.getName() + "?");
			}
			else {
				result = MessageHandler.showConfirmCancel( this, "Save changes to " + doc.getName() + "?");
			}

			if ( result == MessageHandler.CONFIRM_YES_OPTION) {
				getView().updateModel();

				if ( doc.isReadOnly() || doc.getURL() == null) {
					GrammarProperties type = getGrammar();
					File file = null;

					if (type != null) {
						file = FileUtilities.selectOutputFile( (File) null, FileUtilities.getExtension(type));
					} else {
						file = FileUtilities.selectOutputFile( (File) null, "xml");
					}

					if (file != null) {
						try {
							URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
							URL oldURL = document.getURL();
			
							try {
								doc.setURL( url);
								doc.save();

								getProperties().setLastOpenedDocument( url.getFile());
								setDocument( doc); 
								getChangeManager().discardAllEdits();
								finishClose( view);
								return(result);
							} catch ( IOException ex){
								ex.printStackTrace();
								MessageHandler.showError( "Could not save Document.", ex, "Saving Error");
								return (MessageHandler.CONFIRM_CANCEL_OPTION);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						return (MessageHandler.CONFIRM_CANCEL_OPTION);
					}
				} else { // ( doc.isReadOnly() || doc.getURL() == null)
					try {
						doc.save();
					} catch (IOException x) {
						MessageHandler.showError(this, "Could not save "+document.getName()+"\n"+x.getMessage(), "Save Error");
						return (MessageHandler.CONFIRM_CANCEL_OPTION);
					}
				}
			} 
		}

		if ( result != MessageHandler.CONFIRM_CANCEL_OPTION) {
			finishClose( view);
			return (result);
		}

		return (MessageHandler.CONFIRM_CANCEL_OPTION);
	}

	/**
	 * Close a currentView.
	 *
	 * @param currentView the currentView to close.
	 * @param checkChanged true when the app should check it the currentView is changed.
	 *
	 * @return false when close was cancelled.
	 */
	public boolean close( final ExchangerView view) {
		int result = JOptionPane.YES_OPTION;
		final ExchangerDocument document = view.getDocument();
		
		if ( view.isChanged()) {
			result = JOptionPane.showConfirmDialog( this, "Save changes to " + document.getName() + "?",	"Please Confirm", JOptionPane.YES_NO_CANCEL_OPTION);

			if ( result == JOptionPane.YES_OPTION) {
				getView().updateModel();

				if ( document.isReadOnly() || document.getURL() == null) {
					GrammarProperties type = getGrammar();
					File file = null;

					if (type != null) {
						file = FileUtilities.selectOutputFile( (File) null, FileUtilities.getExtension(type));
					} else {
						file = FileUtilities.selectOutputFile( (File) null, "xml");
					}

					if (file != null) {
						try {
							final URL url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
	
							setWait( true);
							setStatus( "Saving ...");

							// Run in Thread!!!
							Runnable runner = new Runnable() {
								public void run()  {
									URL oldURL = document.getURL();
					
									try {
										document.setURL( url);

										document.save();
									} catch ( IOException ex){
										ex.printStackTrace();
										MessageHandler.showError( "Could not save Document.", ex, "Saving Error");
									} catch ( Exception ex){
										ex.printStackTrace();
									} finally {
										getProperties().setLastOpenedDocument( url.getFile());

										SwingUtilities.invokeLater( new Runnable() {
										    public void run() {
												setDocument( document); 
												getChangeManager().discardAllEdits();
												finishClose( view);
										    }
										});
	
										setStatus( "Done");
										setWait( false);
									}
								}
							};
						
							// Create and start the thread ...
							Thread thread = new Thread( runner);
							thread.start();
							
							return true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						return false;
					}
				} else { // ( doc.isReadOnly() || doc.getURL() == null)
					try {
						document.save();
					} catch (IOException x) {
						JOptionPane.showMessageDialog( this, "Could not save "+document.getName()+"\n"+x.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
						return false;
					}
				}
			} 
		}

		if (result != JOptionPane.CANCEL_OPTION) {
			finishClose( view);
			return true;
		}

		return false;
	}
	
	private void finishClose( ExchangerView view) {
		
		if ( view.getDocument().getURL() == null) {
			Vector bookmarks = view.getEditor().getBookmarks();
			
			for ( int i = 0; i < bookmarks.size(); i++) {
				removeBookmark( (Bookmark)bookmarks.elementAt(i));
			}
		} else {
			view.updateBookmarks();
		}

		removeView( view);

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
	}

	public XMLSchema getSchema() {
		if (currentView != null) {
			return currentView.getSchema();
		}

		return null;
	}

	public Statusbar getStatusbar() {
		return statusbar;
	}

	/*public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ExchangerEditor.super.setVisible(true);
			}
		});

		if (macFile != null && !started) {
			started = true;
			URL url = macFile;
			macFile = null;

			open(url, null, false);
		} else {
			started = true;
			macFile = null;
		}
		
		setIntialFocus();
	}*/

	public void setVisible(boolean value) {
		if(value == true) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ExchangerEditor.super.setVisible(true);
				}
			});
			
			if (macFile != null && !started) {
				started = true;
				URL url = macFile;
				macFile = null;
	
				open(url, null, false);
			} else {
				started = true;
				macFile = null;
			}
			
			
			
			setIntialFocus();
		}
		else {
			super.setVisible(value);
		}
	}
	
	public void setIntialFocus()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (buttonOpen != null)
					buttonOpen.grabFocus();
			}
		});
	}

	public void updatePreferences() {
		Vector views = getViews();
		
		Vector catalogs = getProperties().getCatalogs();
		StringBuffer catalogFiles = new StringBuffer();

		for (int i = 0; i < catalogs.size(); i++) {
			catalogFiles.append((String) catalogs.elementAt(i));
			catalogFiles.append(";");
		}
		//		System.out.println( "xml.catalog.files="+catalogFiles.toString());
		System.setProperty("xml.catalog.files", catalogFiles.toString());

		if (getProperties().isPreferPublicIdentifiers()) {
			System.setProperty("xml.catalog.prefer", "public");
		} else {
			System.setProperty("xml.catalog.prefer", "system");
		}

		//		String laf = properties.getLookAndFeel();
		//		String currentLaf = UIManager.getLookAndFeel().getClass().getName();
		//		
		//		System.out.println( "laf = "+laf);
		//		System.out.println( "currentLaf = "+currentLaf);
		//		
		//		if ( laf != null && laf.length() > 0 && (currentLaf == null || !currentLaf.equals( laf))) {
		//			try {
		//				UIManager.setLookAndFeel( laf);
		//				SwingUtilities.updateComponentTreeUI( this );
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}
		//		}

		xpathEditor.updatePreferences();
		navigator.updatePreferences();
		helper.updatePreferences();
		outputPanel.updatePreferences();
		getProjectPanel().updatePreferences();

		ExchangerXMLWriter.setAttributeOnNewLine(
			getProperties().isAttributesNewLine());

		if (getProperties().getEditorProperties().isWrapText()) {
			ExchangerXMLWriter.setMaxLineLength(
				getProperties().getEditorProperties().getWrappingColumn());
		} else {
			ExchangerXMLWriter.setMaxLineLength(-1);
		}

		//		ExchangerXMLWriter.setIndentSize( properties.getTextPreferences().getTabSize());
		ExchangerXMLWriter.setIndentString(TextPreferences.getTabString());

		for (int i = 0; i < views.size(); i++) {
			((ExchangerView) views.elementAt(i)).updatePreferences();
		}
		
		for (int i = 0; i < tabbedViews.size(); i++) {
			((ExchangerTabbedView)tabbedViews.elementAt(i)).setScrollTabs( getProperties().isScrollDocumentTabs());
		}

		getDebugger().updatePreferences();
		getSendSOAPAction().updatePreferences();
		getAnalyseWSDLAction().updatePreferences();
		
		//getSchemaInstanceGenerationAction().updatePreferences();

	}

	public void updateIcons() {
		IconFactory.reset();

		Vector views = getViews();

		getProjectPanel().updatePreferences();

		for (int i = 0; i < views.size(); i++) {
			((ExchangerView) views.elementAt(i)).setViewIcons();
		}

		Icon icon = selectedTabbedView.getSelectedIcon();

		if ( icon != null) {
			controllerTab.setIconAt(1, icon);
		} else {
			controllerTab.setIconAt(1, getIcon(NAVIGATOR_ICON));
		}
	}

	public void setGrammar(GrammarProperties grammar) {
		if (currentView != null) {
			currentView.setGrammar(grammar);
		}

		updateStatus();
		updateFragments();
		//		updateScenarioActions();
		updateGrammarActions();
	}
	
	public GrammarProperties getGrammar() {
		if (currentView != null) {
			return currentView.getGrammar();
		}

		return null;
	}

	public void setDocument(ExchangerDocument document) {
		if (currentView != null) {
			currentView.setDocument(document);
			updateStatus();
		}
		

		setDocumentInternal( document, false);
	}

	private void setDocumentInternal(ExchangerDocument document, boolean output) {
		XMLSchema schema = getSchema();

		if (this.document != null) {
			this.document.removeListener(this);
		}

		this.document = document;

		if (document != null) {
			document.addListener(this);

			if (document.isXML()) {
				if ( output) {
					outputPanel.startCheck(	"WF", "[" + FileUtilities.getXercesVersion() + "] Checking \"" + document.getName()			+ "\" for Well-formedness ...");
				}

				if (!document.isError()) {
					if ( output) {
						outputPanel.endCheck("WF", "Well-formed Document.");
						outputPanel.selectParseTab();
					}
				} else if ( output) {
					Exception e = document.getError();

					if (e instanceof SAXParseException) {
						outputPanel.setError("WF", (SAXParseException) e);
						outputPanel.endCheck( "WF", "1 Error");
						outputPanel.selectParseTab();
					} else if (e instanceof IOException) {
						outputPanel.setError("WF", (IOException) e);
						outputPanel.endCheck( "WF", "1 Error");
						outputPanel.selectParseTab();
					}
				}

				
				
				/*if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
					
					gridButton.setEnabled(true);
					getGridViewItem().setEnabled(true);
				}*/
				

				
				
				
			} else {
				if ( output) {
					outputPanel.setXPathList( null);
					outputPanel.setErrorList( null);
				}

				
			}

			
//			browserButton.setEnabled(true);
//			getBrowserViewItem().setEnabled(true);

		} else {
			helper.clear();

			if ( output) {
				outputPanel.setXPathList( null);
				outputPanel.setErrorList( null);
			}

			
		}
		
		xpathEditor.setDocument(document);
//		navigator.setDocument(document);

		updateActions(document);
		updateStatus();
	}

	// Implementation of the ExchangerDocumentListener interface...	
	public void documentUpdated(ExchangerDocumentEvent event) {

		// perform on event dispacth thread...
		Runnable runner = new Runnable() {
			public void run() {
				if (document != null) {
					if (document.isXML()) {
						outputPanel.startCheck( "WF", "["+FileUtilities.getXercesVersion()+"] Checking \""+document.getName()+"\" for Well-formedness ...");

						if (document.isError()) {
							Exception e = document.getError();

							if (e instanceof SAXParseException) {
								outputPanel.setError( "WF", (SAXParseException) e);
								outputPanel.endCheck( "WF", "1 Error");
								outputPanel.selectParseTab();
							} else if (e instanceof IOException) {
								outputPanel.setError("WF", (IOException) e);
								outputPanel.endCheck( "WF", "1 Error");
								outputPanel.selectParseTab();
							}
						} else {
							outputPanel.endCheck("WF", "Well-formed Document.");
							outputPanel.selectParseTab();
						}

												
					} else { // Not XML
						
					}
				}

				updateActions(document);
				updateStatus();
				currentView.documentUpdated();
			}
		};

		if ( SwingUtilities.isEventDispatchThread()) {
			runner.run();
		} else {
			SwingUtilities.invokeLater( runner);
		}
				
		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
	}

	// ED: FIX ME, have the actions updating themselves, depending on the document state.
	private void updateActions(ExchangerDocument document) {
		if (document != null) {
			getCleanUpHTMLAction().setEnabled(true);
			getResolveXIncludesAction().setEnabled(true);
			getSetXMLDeclarationAction().setEnabled(true); 
			getSetXMLDoctypeAction().setEnabled(true);
			getChangeDocumentAction().setEnabled(true);
			getSetSchemaLocationAction().setEnabled(true);
			getOpenBrowserAction().setEnabled(true);
			
			getToolsStripTextAction().setEnabled(true);
			getToolsCapitalizeAction().setEnabled(true);
			getToolsDeCapitalizeAction().setEnabled(true);
			getToolsLowercaseAction().setEnabled(true);
			getToolsUppercaseAction().setEnabled(true);
			getToolsMoveNSToRootAction().setEnabled(true);
			getToolsMoveNSToFirstUsedAction().setEnabled(true);
			getToolsChangeNSPrefixAction().setEnabled(true);
			getToolsRenameNodeAction().setEnabled(true);
			getToolsRemoveNodeAction().setEnabled(true);
			getToolsAddNodeToNamespaceAction().setEnabled(true);
			getToolsSetNodeValueAction().setEnabled(true);
			getToolsAddNodeAction().setEnabled(true);
			getToolsRemoveUnusedNSAction().setEnabled(true);
			getToolsConvertNodeAction().setEnabled(true);
			getToolsSortNodeAction().setEnabled(true);
			
			
			if (document.isXML()) {
				if (!document.isError()) {
					getCreateSchemaAction().setEnabled(true);
					getSetSchemaPropertiesAction().setEnabled(true);
					getParseAction().setParsed( !currentView.getChangeManager().isTextChanged());
					getValidateSchemaAction().setEnabled( isSchemaDocument(document));
					getValidateRelaxNGAction().setEnabled( isRelaxNGDocument(document));
					getVerifySignatureAction().setEnabled(true);
					
				} else {
					getParseAction().setParsed(false);
					getValidateSchemaAction().setEnabled(false);
					getValidateRelaxNGAction().setEnabled(false);
					getCreateSchemaAction().setEnabled(false);
					getSetSchemaPropertiesAction().setEnabled(false);
					getVerifySignatureAction().setEnabled(false);
										
//					if ( document.getName().endsWith( "htm") || document.getName().endsWith( "html")) {
//						getOpenBrowserAction().setEnabled(true);
//					} else {
//						getOpenBrowserAction().setEnabled(false);
//					}
				}
			} else {
				getParseAction().setParsed(false);
				getValidateSchemaAction().setEnabled(false);
				getValidateRelaxNGAction().setEnabled(false);
				getCreateSchemaAction().setEnabled(false);
				getSetSchemaPropertiesAction().setEnabled(false);
				getResolveXIncludesAction().setEnabled(false);
				getSetXMLDeclarationAction().setEnabled(false);
				getSetXMLDoctypeAction().setEnabled(false); 
				getSetSchemaLocationAction().setEnabled(false); 
				getVerifySignatureAction().setEnabled(false);
				
				getToolsStripTextAction().setEnabled(false);
				getToolsCapitalizeAction().setEnabled(false);
				getToolsDeCapitalizeAction().setEnabled(false);
				getToolsLowercaseAction().setEnabled(false);
				getToolsUppercaseAction().setEnabled(false);
				getToolsMoveNSToRootAction().setEnabled(false);
				getToolsMoveNSToFirstUsedAction().setEnabled(false);
				getToolsChangeNSPrefixAction().setEnabled(false);
				getToolsRenameNodeAction().setEnabled(false);
				getToolsRemoveNodeAction().setEnabled(false);
				getToolsAddNodeToNamespaceAction().setEnabled(false);
				getToolsSetNodeValueAction().setEnabled(false);
				getToolsAddNodeAction().setEnabled(false);
				getToolsRemoveUnusedNSAction().setEnabled(false);
				getToolsConvertNodeAction().setEnabled(false);
				getToolsSortNodeAction().setEnabled(false);

//				if ( document.getName().endsWith( "htm") || document.getName().endsWith( "html")) {
//					getOpenBrowserAction().setEnabled(true);
//				} else {
//					getOpenBrowserAction().setEnabled(false);
//				}
			}

			if (document.isDTD()) {
				getValidateDTDAction().setEnabled(true);
			} else {
				getValidateDTDAction().setEnabled(false);
			}

			getPrintAction().setEnabled(true);
		} else {
			getValidateSchemaAction().setEnabled(false);
			getValidateRelaxNGAction().setEnabled(false);
			getCreateSchemaAction().setEnabled(false);
			getSetSchemaPropertiesAction().setEnabled(false);
			getOpenBrowserAction().setEnabled(false);
			getPrintAction().setEnabled(false);
			getParseAction().setParsed(false);
			getValidateDTDAction().setEnabled(false);
			getResolveXIncludesAction().setEnabled(false);
			getSetXMLDeclarationAction().setEnabled(false);
			getSetXMLDoctypeAction().setEnabled(false); 
			getSetSchemaLocationAction().setEnabled(false); 
			getCleanUpHTMLAction().setEnabled(false);
			getVerifySignatureAction().setEnabled(false);
			getChangeDocumentAction().setEnabled(false);
			
			getToolsStripTextAction().setEnabled(false);
			getToolsCapitalizeAction().setEnabled(false);
			getToolsDeCapitalizeAction().setEnabled(false);
			getToolsLowercaseAction().setEnabled(false);
			getToolsUppercaseAction().setEnabled(false);
			getToolsMoveNSToRootAction().setEnabled(false);
			getToolsMoveNSToFirstUsedAction().setEnabled(false);
			getToolsChangeNSPrefixAction().setEnabled(false);
			getToolsRenameNodeAction().setEnabled(false);
			getToolsRemoveNodeAction().setEnabled(false);
			getToolsAddNodeToNamespaceAction().setEnabled(false);
			getToolsSetNodeValueAction().setEnabled(false);
			getToolsAddNodeAction().setEnabled(false);
			getToolsRemoveUnusedNSAction().setEnabled(false);
			getToolsConvertNodeAction().setEnabled(false);
			getToolsSortNodeAction().setEnabled(false);
		}

		getSaveAction().setDocument(document);
		getSaveAllAction().setDocument(document);
		getSaveAsAction().setDocument(document);
		getSaveAsRemoteAction().setDocument(document);
		getSaveAsTemplateAction().setDocument(document);

		getSelectElementAction().setDocument(document);
		getSelectElementContentAction().setDocument(document);

		getGotoStartTagAction().setDocument(document);
		getHighlightAction().setDocument(document);
		getToggleEmptyElementAction().setDocument(document);
		getRenameElementAction().setDocument(document);
		getGotoEndTagAction().setDocument(document);
		getGotoNextAttributeValueAction().setDocument(document);
		getGotoPreviousAttributeValueAction().setDocument(document);

		getTagAction().setDocument(document);
		getRepeatTagAction().setDocument(document);
		getCDATAAction().setDocument(document);
		getCommentAction().setDocument(document);
		getLockAction().setDocument(document);
		getCanonicalizeAction().setDocument(document);
		getValidateAction().setDocument(document);
		getInsertEntityAction().setDocument(document);
		getSubstituteCharactersAction().setDocument(document);
		getSubstituteEntitiesAction().setDocument(document);
		getStripTagsAction().setDocument(document);
		getSplitElementAction().setDocument(document);
		getFormatAction().setDocument(document);
		getParseAction().setDocument(document);
		
//		for each of the plugin buttons
		for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
			Object obj = this.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					List actionList = pluginView.getActions();
					for(int acnt=0;acnt<actionList.size();++acnt) {
						PluginActionKeyMapping pluginAction = (PluginActionKeyMapping) actionList.get(acnt);
						pluginAction.getAction().updateActions(document);
					}
				}				
			}
		}

		ChangeManager changeManager = getChangeManager();
		
		if ( changeManager != null) {
			getValidateAction().setValidated( changeManager.isValidated());
		} else {
			getValidateAction().setValidated( false);
		}

		updateGrammarActions();
	}

	public void documentDeleted(ExchangerDocumentEvent event) {
	}

	private HelpSet getHelpSet() {
		if (helpset == null) {
			try {
				URL url = HelpSet.findHelpSet( this.getClass().getClassLoader(), "ExchangerHelp.hs");
				String dir = System.getProperty(".dir");

				if (url == null) {
					if (dir != null) {
						url = XngrURLUtilities.getURLFromFile(new File( dir + "help" + File.separator	+ "ExchangerHelp.hs"));
					} else { // use relative location
						url = XngrURLUtilities.getURLFromFile(new File("help/ExchangerHelp.hs"));
					}
					//					System.out.println("codeBase url=" + url);
				}

				helpset = new HelpSet(this.getClass().getClassLoader(), url);
			} catch (Exception ee) {
				//			    System.out.println ("Trouble in createHelpSet;");
				ee.printStackTrace();
			}
		}

		return helpset;
	}

	private HelpBroker createHelpBroker() {
		HelpBroker broker = null;
		HelpSet set = getHelpSet();

		if (set != null) {
			broker = set.createHelpBroker( "MainWindow");

			Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

			int width = 640;
			int height = 480;

			broker.setSize(new Dimension(width, height));
			broker.setLocation(
				new Point((d.width - width) / 2, (d.height - height) / 2));
		}

		return broker;
	}

	private void setSchemaInternal(XMLSchema schema) {
		XElement docRoot = null;
		SchemaElement schemaRoot = null;
		ExchangerDocument document = getDocument();

		helper.setSchema(schema);

		if (schema != null) {
			currentView.setSchemaInternal(schema);
			
		} else { // schema == null
			
		}
	}

	public void setSchema(XMLSchema schema) {
		if (currentView != null && currentView.setSchema(schema)) {
			setSchemaInternal(schema);

			// make sure this runs after the gui is updated!	
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					System.gc();
				}
			});
		}
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}

	public void setTagCompletionSchemas( Vector schemas) {
		if (currentView != null) {
			currentView.setTagCompletionSchemas( schemas);
		}
	}

	public void setDebugger( XSLTDebuggerFrame debugger) {
		this.debugger = debugger;
	}

	public XSLTDebuggerFrame getDebugger() {
		return debugger;
	}

	public void createView( final ExchangerDocument document, XMLSchema schema, Vector tagCompletionSchemas, GrammarProperties grammar) {
		if(DEBUG) {
			System.out.println("createView (document, schema, tagCompletionSchemas, grammar)");
		}
		previousView = currentView;
		currentView = new ExchangerView( this, getProperties());

		outputPanel.setErrorList( currentView.getErrors());
		outputPanel.setXPathList( currentView.getXPathList());

//		currentView.setDocument(document);
//		updateStatus();

		setDocumentInternal( document, true);
		currentView.setDocument(document);
		
		setDocument( document);
		setSchema( schema);
		setTagCompletionSchemas( tagCompletionSchemas);
		setGrammar( grammar);

		SwingUtilities.invokeLater( 
			new Runnable() {
				public void run() {
					selectedTabbedView.add( currentView, document.getName());
					currentView.initBookmarks();

					currentView.getEditorButton().setSelected( true);
					currentView.getEditorViewItem().setSelected( true);
					currentView.switchToEditor();

					currentView.setViewIcons();

					selectedTabbedView.select( currentView);
				}
			});
	}

	public void openSchema(	URL url, final boolean useForTagCompletion, final boolean switchView) {
		try {
			setGrammar(null);
			
			if ( url != null) {
				final XMLSchema schema = new XMLSchema(url);
				
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						setSchema(schema);
	
						if (useForTagCompletion) {
							Vector schemas = new Vector();
							schemas.addElement( schema);

							setTagCompletionSchemas( schemas);
						}
	
						if (switchView) {
							try {
								switchToSchema();
							} catch (Exception e) {
							}
						}
					}
				});
			} else {
				setSchema( null);
			}
		} catch (SAXParseException e) {
			MessageHandler.showError( "Could not load Schema " + url.getFile(),	e, "Schema Error");
		} catch (SchemaException e) {
			MessageHandler.showError( "Could not load Schema " + url.getFile(),	e, "Schema Error");
		} catch (MalformedURLException e) {
			MessageHandler.showUnexpectedError(e);
		} catch (IOException e) {
			MessageHandler.showError( "Could not load Schema " + url.getFile(), e, "Schema Error");
		}
	}

	public Vector openTagCompletionSchemas( Vector list) {
		Vector result = null;
		setGrammar(null);
		
		if ( list != null && list.size() > 0) {
			result = new Vector();

			for ( int i = 0; i < list.size(); i++) {
				URL url = URLUtilities.toURL( ((TagCompletionProperties)list.elementAt(i)).getLocation());
				int type = ((TagCompletionProperties)list.elementAt(i)).getType();
				
				SchemaDocument schema = FileUtilities.createTagCompletionSchema( url, type, true);
				result.addElement( schema);
			}
		}
		
		return result;
	}

	public void removeView( ExchangerView view) {
		for ( int i = 0; i < tabbedViews.size(); i++) {
			ExchangerTabbedView tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(i);

			if ( tabbedView.contains( view)) {
				tabbedView.remove( view);

				view.cleanup();

				if ( tabbedView.getViews().size() == 0) {
					if ( isFullScreen()) {
						toggleFullScreen();
					}

					unsplit();
				}
				
				setView( (ExchangerView) selectedTabbedView.getSelectedView());
				return;
			}
		}
	}

	public void switchToViewer() throws Exception {
		currentView.switchToViewer();

		

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}
	
	public void switchToUserView(UserView newUserView) throws Exception {
		currentView.switchToUserView(newUserView);

		

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}

//	public void switchToBrowser() throws Exception {
//		currentView.switchToBrowser();
//
//		browserButton.setSelected(true);
//		getBrowserViewItem().setSelected(true);
//
//		// make sure this runs after the gui is updated!	
//		SwingUtilities.invokeLater(new Runnable() {
//			public void run() {
//				System.gc();
//			}
//		});
//		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
//	}
	
	/*public void switchToGrid() throws Exception {
		currentView.switchToGrid();

		gridButton.setSelected(true);
		getGridViewItem().setSelected(true);

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}*/
	
	public void switchToPluginView(PluginView pluginView) throws Exception {
		//currentView.switchToGrid();
		currentView.switchToPluginView(pluginView);

		//gridButton.setSelected(true);
		

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}
	
	public void switchToPluginView(PluginViewPanel pluginViewPanel) throws Exception {
		
		/*for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
			PluginView pluginView = (PluginView)this.getPluginViews().get(cnt);
			if(pluginView != null) {
				if(pluginView.getPluginViewPanel() != null) {
					if(pluginView.getPluginViewPanel().equals(pluginViewPanel)) {
						this.switchToPluginView(pluginView);
					}
				}
			}
		}*/
		if((this.getView() != null) && (this.getView().getPluginViewPanels() != null)) {
			for(int vcnt=0;vcnt<this.getView().getPluginViewPanels().size();++vcnt) {
				PluginViewPanel panel = (PluginViewPanel) this.getView().getPluginViewPanels().get(vcnt);
				if(pluginViewPanel == panel) {
					this.switchToPluginView(pluginViewPanel.getPluginView());
				}
			}								
		}
		
	}

	public void switchToDesigner() throws Exception {
		currentView.switchToDesigner();

		

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}

	public void switchToSchema() throws Exception {
		currentView.switchToSchema();

		

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}

	public void switchToEditor() {
		currentView.switchToEditor();

		currentView.getEditorButton().setSelected(true);
		currentView.getEditorViewItem().setSelected(true);

		// make sure this runs after the gui is updated!	
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				System.gc();
			}
		});
		//		System.out.println( "Total ["+Runtime.getRuntime().totalMemory()+"] Free ["+Runtime.getRuntime().freeMemory()+"] ("+(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())+")");
	}

	private JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setRollover(true);
		toolbar.setFloatable(false);
		toolbar.setBorderPainted(false);

		toolbar.add(getNewAction()).setMnemonic(0);
		//JButton button = toolbar.add(getOpenAction()).setMnemonic(0);
		buttonOpen = toolbar.add(getOpenAction());
		buttonOpen.setMnemonic(0);
		toolbar.add(getOpenRemoteDocumentAction()).setMnemonic(0);
		toolbar.add(getSaveAction()).setMnemonic(0);
		toolbar.add(getSaveAsAction()).setMnemonic(0);
		toolbar.add(getSaveAsRemoteAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add(getPrintAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add(getUndoAction()).setMnemonic(0);
		toolbar.add(getRedoAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add(getCutAction()).setMnemonic(0);
		toolbar.add(getCopyAction()).setMnemonic(0);
		toolbar.add(getPasteAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add(getFindAction()).setMnemonic(0);
		toolbar.add(getFindNextAction()).setMnemonic(0);
		toolbar.add(getReplaceAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add(getValidateAction()).setMnemonic(0);
		toolbar.add(getParseAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add(getSplitTabsHorizontallyAction()).setMnemonic(0);
		toolbar.add(getSplitTabsVerticallyAction()).setMnemonic(0);
		toolbar.add(getUnsplitTabsAction()).setMnemonic(0);
		toolbar.addSeparator();

		toolbar.add(getDefaultScenarioAction()).setMnemonic(0);
		toolbar.add(getDebugScenarioAction()).setMnemonic(0);
		
		return toolbar;
	}

	private JToolBar createEditorToolbar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setRollover( true);
		toolbar.setFloatable( false);
		toolbar.setBorderPainted( false);
//		toolbar.setBorder( new MatteBorder( 1, 1, 0, 1, UIManager.getColor("controlDkShadow")));
		
		toolbar.add( getSelectElementAction()).setMnemonic(0);
		toolbar.add( getSelectElementContentAction()).setMnemonic(0);

		toolbar.addSeparator();

		toolbar.add( getTagAction()).setMnemonic(0);
		toolbar.add( getCommentAction()).setMnemonic(0);
		toolbar.add( getCDATAAction()).setMnemonic(0);

		toolbar.addSeparator();

		toolbar.add( getSplitElementAction()).setMnemonic(0);

//		toolbar.addSeparator();
//
//		toolbar.add( getIndentAction()).setMnemonic(0);
//		toolbar.add( getUnindentAction()).setMnemonic(0);

		toolbar.addSeparator();

		toolbar.add( getSubstituteCharactersAction()).setMnemonic(0);
		toolbar.add( getSubstituteEntitiesAction()).setMnemonic(0);

		toolbar.add( getStripTagsAction()).setMnemonic(0);

		toolbar.addSeparator();

		toolbar.add( getGotoStartTagAction()).setMnemonic(0);
		toolbar.add( getGotoEndTagAction()).setMnemonic(0);
		toolbar.add( getGotoAction()).setMnemonic(0);

		toolbar.addSeparator();
		toolbar.add( getFormatAction()).setMnemonic(0);

		toolbar.addSeparator();
		toolbar.add( getLockAction()).setMnemonic(0);

		toolbar.addSeparator();

		Action a = getHighlightAction();

		highlightButton = new JToggleButton( (String)a.getValue(Action.NAME), (ImageIcon)a.getValue( Action.SMALL_ICON));
		highlightButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				highlightMenuItem.setSelected( highlightButton.isSelected());
			}
		});
		highlightButton.setAction(a);
        highlightButton.setText( null);
        highlightButton.setEnabled( a.isEnabled());
        highlightButton.setToolTipText((String)a.getValue(Action.SHORT_DESCRIPTION));

        toolbar.add( highlightButton);
		
		toolbar.addSeparator();
		toolbar.add(getCollapseAllAction()).setMnemonic(0);
		toolbar.add(getExpandAllAction()).setMnemonic(0);

		return toolbar;
	}

	// create the menu bar...
	private void createMenubar() {
		JMenuBar menu = new JMenuBar();

		menu.add(createFileMenu());
		menu.add(createEditMenu());
		menu.add(createViewMenu());
		menu.add(createProjectMenu());
		menu.add(createXMLMenu());
		menu.add(createGrammarMenu());
		menu.add(createScenarioMenu());
		menu.add(createSecurityMenu());
		menu.add(createToolsMenu());
		//if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
		//	menu.add(createGridMenu());
		//}
		
//		for each of the plugin buttons
		for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
			Object obj = this.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					JMenu pluginMenu = pluginView.createPluginViewMenu();
					if(pluginMenu != null) {
						menu.add(pluginMenu);
						GUIUtilities.alignMenu(pluginMenu);
					}
					
				}				
			}
		}

		// >>> Help Menu
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		

		JMenuItem aboutItem = new JMenuItem("About", 'A');
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showAboutDialog();
			}
		});

		JMenuItem contentsItem = new JMenuItem("Contents", 'C');

		HelpBroker broker = createHelpBroker();
		if (broker != null) {
			broker.enableHelpOnButton( contentsItem, "exchanger.intro", getHelpSet());
			broker.setCurrentView("TOC");
		} else {
			contentsItem.setEnabled(false);
		}

		JMenuItem indexItem = new JMenuItem("Index", 'I');

		broker = createHelpBroker();
		if (broker != null) {
			broker.enableHelpOnButton( indexItem, "exchanger.intro", getHelpSet());
			broker.setCurrentView("Index");
		} else {
			indexItem.setEnabled(false);
		}

		JMenuItem searchItem = new JMenuItem("Search", 'S');

		broker = createHelpBroker();
		if (broker != null) {
			broker.enableHelpOnButton( searchItem, "exchanger.intro", getHelpSet());
			broker.setCurrentView("Search");
		} else {
			searchItem.setEnabled(false);
		}

		JMenuItem gettingStartedItem = new JMenuItem("Getting Started", 'G');

		broker = createHelpBroker();
		if (broker != null) {
			broker.enableHelpOnButton( gettingStartedItem, "start.intro", getHelpSet());
			broker.setCurrentView("TOC");
		} else {
			gettingStartedItem.setEnabled(false);
		}

		/*LicenseManager licenseManager = null;

		try {
			licenseManager = LicenseManager.getInstance();
			licenseManager.isValid( KeyGenerator.generate(2), "Exchanger XML Editor");
		} catch (Exception e) {
			licenseManager = null;
		}*/

		JMenuItem buyItem = new JMenuItem("Buy Now");
		buyItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					BrowserLauncher.openURL( "http://www.exchangerxml.com/editor/buy.html");
				} catch (Exception x) {
				}
			}
		});

		helpMenu.add(contentsItem);
		helpMenu.add(indexItem);
		helpMenu.add(searchItem);

		helpMenu.addSeparator();
		helpMenu.add(gettingStartedItem);

		/*if (licenseManager == null || licenseManager.getLicenseType().equals( LicenseType.LICENSE_TEMPORARY) || licenseManager.getLicenseType().equals( LicenseType.LICENSE_LITE)) {
			helpMenu.addSeparator();
			helpMenu.add(buyItem);
		}*/

		helpMenu.addSeparator();
		helpMenu.add(aboutItem);
		GUIUtilities.alignMenu( helpMenu);
		menu.add(helpMenu);
		// <<< File Menu

		String currentLaf = UIManager.getLookAndFeel().getClass().getName();

		// APPLE Mac
		if (System.getProperty("mrj.version") == null || !currentLaf.equals("apple.laf.AquaLookAndFeel")) {
			menu.add(Box.createHorizontalGlue());

			closeButton = new JButton();
			closeButton.setMargin(new Insets(1, 1, 1, 1));
			closeButton.setAction(getCloseAction());
			closeButton.setText(null);
			closeButton.setIcon(getIcon(CLOSE_ICON));

			menu.add(closeButton);
		}
		
		

		setJMenuBar(menu);
	}

	// create the menu bar...
	private JMenu createFileMenu() {
		// >>> File Menu
		fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		fileMenu.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}
			public void menuSelected(MenuEvent e) {
				updateFileMenu();
			}
			public void menuDeselected(MenuEvent e) {
			}
		});

		updateFileMenu();

		// <<< File Menu
		return fileMenu;
	}

	private void updateFileMenu() {
		if (fileMenu.getItemCount() > 0) {
			fileMenu.removeAll();
		}

		fileMenu.add(createMenuItem(getNewAction(),KeyPreferences.NEW_DOCUMENT_ACTION));

		fileMenu.add(createMenuItem(getOpenAction(),KeyPreferences.OPEN_ACTION));
		fileMenu.add(createMenuItem(getOpenRemoteDocumentAction(),KeyPreferences.OPEN_REMOTE_ACTION));
		fileMenu.add(createMenuItem(getCloseAction(),KeyPreferences.CLOSE_ACTION));
		fileMenu.add(createMenuItem(getCloseAllAction(),KeyPreferences.CLOSE_ALL_ACTION));

		fileMenu.addSeparator();

		fileMenu.add(createMenuItem(getReloadAction(),KeyPreferences.RELOAD_ACTION));
		
		fileMenu.addSeparator();

		fileMenu.add(createMenuItem(getSaveAction(),KeyPreferences.SAVE_ACTION));
		fileMenu.add(createMenuItem(getSaveAsAction(),KeyPreferences.SAVE_AS_ACTION));
		fileMenu.add(createMenuItem(getSaveAsRemoteAction()));
		fileMenu.add(createMenuItem(getSaveAllAction(),KeyPreferences.SAVE_ALL_ACTION));

		fileMenu.addSeparator();
		
		JMenu importMenu = new JMenu( "Import");
		importMenu.setMnemonic( 'I');
		importMenu.setIcon( XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Import16.gif"));
		//importMenu.setIcon();
		importMenu.add(createMenuItem(getImportFromTextAction(),KeyPreferences.IMPORT_FROM_TEXT_ACTION));
		importMenu.add(createMenuItem(getImportFromExcelAction(),KeyPreferences.IMPORT_FROM_EXCEL_ACTION));
		importMenu.add(createMenuItem(getImportFromDBTableAction(),KeyPreferences.IMPORT_FROM_DBTABLE_ACTION));
		importMenu.add(createMenuItem(getImportFromSQLXMLAction(),KeyPreferences.IMPORT_FROM_SQLXML_ACTION));
		
		fileMenu.add(importMenu);
		
		fileMenu.addSeparator();

		fileMenu.add(createMenuItem(getSaveAsTemplateAction(),KeyPreferences.SAVE_AS_TEMPLATE_ACTION));
		fileMenu.add(createMenuItem(getManageTemplateAction(),KeyPreferences.MANAGE_TEMPLATE_ACTION));

		fileMenu.addSeparator();

		fileMenu.add(createMenuItem(getPageSetupAction(),KeyPreferences.PAGE_SETUP_ACTION));
		fileMenu.add(createMenuItem(getPrintAction(),KeyPreferences.PRINT_ACTION));

		fileMenu.addSeparator();

		fileMenu.add(createMenuItem(getPreferencesAction(),KeyPreferences.PREFERENCES_ACTION));

		Vector docs = getProperties().getLastOpenedDocuments();

		if (docs.size() > 0) {
			fileMenu.addSeparator();

			for (int i = 0; i < docs.size(); i++) {
				URL url = null;

				try {
					url = XngrURLUtilities.getURLFromFile(new File((String) docs.elementAt(i)));
				} catch (Exception e) {
					e.printStackTrace();
					// should not happen
				}

				fileMenu.add( createMenuItem(new OpenMRUAction(this, url, i + 1)));
			}
		}

		fileMenu.addSeparator();

		JMenuItem exitItem = new JMenuItem("Exit", 'x');
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processWindowEvent(	new WindowEvent( ExchangerEditor.this, WindowEvent.WINDOW_CLOSING));
			}
		});

		fileMenu.add(exitItem);
		
		GUIUtilities.alignMenu( fileMenu);
	}

	// create the edit menu...
	private JMenu createEditMenu() {
		//JMenu editMenu = new JMenu("Edit");
		editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');

		editMenu.add(createMenuItem(getUndoAction(),KeyPreferences.UNDO_ACTION));
		//		toolbar.add( undo);

		editMenu.add(createMenuItem(getRedoAction(),KeyPreferences.REDO_ACTION));
		//		toolbar.add( redo);

		editMenu.addSeparator();

		editMenu.add(createMenuItem(getCutAction(), KeyPreferences.CUT_ACTION));
		editMenu.add( createMenuItem(getCopyAction(),KeyPreferences.COPY_ACTION));
		editMenu.add( createMenuItem(getPasteAction(),KeyPreferences.PASTE_ACTION));

		editMenu.addSeparator();

		editMenu.add( createMenuItem(getAddNodeAction(),KeyPreferences.ADD_ELEMENT_OUTLINER_ACTION));
		editMenu.add( createMenuItem(getDeleteNodeAction(),KeyPreferences.DELETE_ELEMENT_OUTLINER_ACTION));
		editMenu.add( createMenuItem(getCreateRequiredNodesAction(), KeyPreferences.CREATE_REQUIRED_NODE_ACTION));

		editMenu.addSeparator();

		editMenu.add(createMenuItem(getIndentAction(),KeyPreferences.TAB_ACTION));
		editMenu.add(createMenuItem(getUnindentAction(),KeyPreferences.UNINDENT_ACTION));

		editMenu.addSeparator();

		JMenu xmlMenu = new JMenu( "XML");
		xmlMenu.setMnemonic( 'X');

		xmlMenu.add(createMenuItem(getSelectElementAction(),KeyPreferences.SELECT_ELEMENT_ACTION));
		xmlMenu.add(createMenuItem(getSelectElementContentAction(),KeyPreferences.SELECT_ELEMENT_CONTENT_ACTION));

		xmlMenu.addSeparator();
				
		xmlMenu.add(createMenuItem(getSplitElementAction(),KeyPreferences.SPLIT_ELEMENT_ACTION));

		xmlMenu.addSeparator();

		xmlMenu.add( createMenuItem(getInsertEntityAction(),KeyPreferences.INSERT_SPECIAL_CHAR_ACTION));
		xmlMenu.add( createMenuItem( getSubstituteEntitiesAction(),KeyPreferences.CONVERT_ENTITIES_ACTION));
		xmlMenu.add( createMenuItem( getSubstituteCharactersAction(),KeyPreferences.CONVERT_CHARACTERS_ACTION));

		xmlMenu.addSeparator();

		xmlMenu.add( createMenuItem( getStripTagsAction(),KeyPreferences.STRIP_TAG_ACTION));
		
		xmlMenu.addSeparator();

		xmlMenu.add(createMenuItem(getTagAction(),KeyPreferences.TAG_ACTION));
		xmlMenu.add(createMenuItem(getRepeatTagAction(),KeyPreferences.REPEAT_TAG_ACTION));
		xmlMenu.add(createMenuItem(getRenameElementAction(),KeyPreferences.RENAME_ELEMENT_ACTION));
		xmlMenu.add(createMenuItem(getToggleEmptyElementAction(),KeyPreferences.TOGGLE_EMPTY_ELEMENT_ACTION));
		xmlMenu.add(createMenuItem(getCommentAction(),KeyPreferences.COMMENT_ACTION));
		xmlMenu.add(createMenuItem(getCDATAAction(),KeyPreferences.ADD_CDATA_ACTION));

		xmlMenu.addSeparator();
		xmlMenu.add(createMenuItem(getLockAction(),KeyPreferences.LOCK_ACTION));
		xmlMenu.addSeparator();

		xmlMenu.add(createMenuItem(getFormatAction(),KeyPreferences.FORMAT_ACTION));

		xmlMenu.addSeparator();

		xmlMenu.add(createMenuItem(getGotoStartTagAction(),KeyPreferences.GOTO_START_TAG_ACTION));
		xmlMenu.add(createMenuItem(getGotoEndTagAction(),KeyPreferences.GOTO_END_TAG_ACTION));

		xmlMenu.add( createMenuItem( getGotoPreviousAttributeValueAction(),KeyPreferences.GOTO_PREVIOUS_ATTRIBUTE_VALUE_ACTION));
		xmlMenu.add( createMenuItem( getGotoNextAttributeValueAction(),KeyPreferences.GOTO_NEXT_ATTRIBUTE_VALUE_ACTION));

		GUIUtilities.alignMenu( xmlMenu);
		editMenu.add( xmlMenu);
		
		editMenu.add( createMenuItem( getSelectFragmentAction(),KeyPreferences.SELECT_FRAGMENT_ACTION));

		editMenu.addSeparator();

		editMenu.add( createMenuItem( getToggleBookmarkAction(),KeyPreferences.TOGGLE_BOOKMARK_ACTION));
		editMenu.add( createMenuItem( getSelectBookmarkAction(),KeyPreferences.SELECT_BOOKMARK_ACTION));
		editMenu.addSeparator();

		editMenu.add(createMenuItem(getGotoAction(),KeyPreferences.GOTO_ACTION));
		editMenu.addSeparator();

		editMenu.add(createMenuItem(getFindAction(),KeyPreferences.FIND_ACTION));

		editMenu.add(createMenuItem(getFindNextAction(),KeyPreferences.FIND_NEXT_ACTION));
		editMenu.add(createMenuItem(getReplaceAction(),KeyPreferences.REPLACE_ACTION));
		
		editMenu.addSeparator();
		
		editMenu.add(createMenuItem(getFindInFilesAction(), KeyPreferences.FIND_IN_FILES_ACTION));

		GUIUtilities.alignMenu( editMenu);

		return editMenu;
	}

	
	
	// create the currentView menu...
	private JMenu createViewMenu() {
		viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');

		setDocumentViewsMenu(new JMenu("Document Views"));
		getDocumentViewsMenu().setMnemonic('D');
		ButtonGroup group = new ButtonGroup();
		if(currentView != null) {			
			
			JRadioButtonMenuItem item = currentView.getSchemaViewItem();
			//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_1,InputEvent.CTRL_MASK, false));
			group.add(item);
			getDocumentViewsMenu().add(item);
	
			item = currentView.getDesignerViewItem();
			//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_2,InputEvent.CTRL_MASK, false));
			group.add(item);
			getDocumentViewsMenu().add(item);
	
			item = currentView.getEditorViewItem();
			//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_3,InputEvent.CTRL_MASK, false));
			group.add(item);
			getDocumentViewsMenu().add(item);
	
			item = currentView.getViewerViewItem();
			//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_4,InputEvent.CTRL_MASK, false));
			group.add(item);
			getDocumentViewsMenu().add(item);
			
			/*if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
				item = getGridViewItem();
				documentViewButtonGroup.add(item);
				viewMenu.add(item);
			}*/
			
	//		for each of the plugin buttons
			for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
				Object obj = this.getPluginViews().get(cnt);
				if((obj != null) && (obj instanceof PluginView)) {
					PluginView pluginView = (PluginView)obj;
					if(pluginView != null) {
						
						JRadioButtonMenuItem pluginItem = pluginView.getPluginViewItem();
						if(pluginItem != null) {
							item = pluginItem;
							group.add(item);
							getDocumentViewsMenu().add(item);
						}
						
					}				
				}
			}
			
			GUIUtilities.alignMenu( getDocumentViewsMenu());
	
	//		item = getBrowserViewItem();
			//item.setAccelerator(KeyStroke.getKeyStroke( KeyEvent.VK_5,InputEvent.CTRL_MASK, false));
	//		documentViewButtonGroup.add(item);
	//		viewMenu.add(item);
		}
		
		viewMenu.add(getDocumentViewsMenu());

		viewMenu.addSeparator();

		Action a = getHighlightAction();

		highlightMenuItem = new JCheckBoxMenuItem( (String)a.getValue(Action.NAME), (ImageIcon)a.getValue( Action.SMALL_ICON));
		highlightMenuItem.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				highlightButton.setSelected( highlightMenuItem.isSelected());
			}
		});
		highlightMenuItem.setAction( a);
		highlightMenuItem.setEnabled( a.isEnabled());
		menuItemMap.put(KeyPreferences.HIGHLIGHT_ACTION,highlightMenuItem);
//		highlightButton.setAction(a);
//      highlightButton.setText( null);
//		highlightButton.setEnabled( a.isEnabled());
//      highlightButton.setToolTipText((String)a.getValue(Action.SHORT_DESCRIPTION));

        viewMenu.add( highlightMenuItem);

		viewMenu.addSeparator();

		viewMenu.add(createMenuItem(getCollapseAllAction(), KeyPreferences.COLLAPSE_ALL_ACTION));
		viewMenu.add(createMenuItem(getExpandAllAction(), KeyPreferences.EXPAND_ALL_ACTION));

		viewMenu.addSeparator();

		viewMenu.add(createMenuItem(getSynchroniseSelectionAction(),KeyPreferences.SYNCHRONISE_ACTION));

		viewMenu.addSeparator();
		JMenu toolbars = new JMenu( "Toolbars");
		
		viewMenu.add( toolbars);
		
		showStandardButtons = new JCheckBoxMenuItem( "Standard Buttons");
		showStandardButtons.setSelected( getProperties().isShowToolbar());
		showStandardButtons.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( toolbar != null) { 
					toolbar.setVisible( showStandardButtons.isSelected());
					toolbarPanel.setVisible( showStandardButtons.isSelected());
					getProperties().setShowToolbar( showStandardButtons.isSelected());
				}
			}
		});
		
		toolbars.add( showStandardButtons);
		menuItemMap.put(KeyPreferences.VIEW_STANDARD_BUTTONS_ACTION,showStandardButtons);
		
		showEditorButtons = new JCheckBoxMenuItem( "Editor Buttons");
		showEditorButtons.setSelected( getProperties().isShowEditorToolbar());
		showEditorButtons.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				if ( toolbar != null) { 
					editorToolbar.setVisible( showEditorButtons.isSelected());
					editorToolbarPanel.setVisible( showEditorButtons.isSelected());
					getProperties().setShowEditorToolbar( showEditorButtons.isSelected());
				}
			}
		});

		toolbars.add( showEditorButtons);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_BUTTONS_ACTION,showEditorButtons);

		showFragmentButtons = new JCheckBoxMenuItem( "Fragment Buttons");
		showFragmentButtons.setSelected( getProperties().getEditorProperties().isShowFragmentsToolbar());

		showFragmentButtons.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setShowFragmentsToolbar( showFragmentButtons.isSelected());

				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().showFragmentToolbar( showFragmentButtons.isSelected());
				}
			}
		});

		toolbars.add( showFragmentButtons);
		menuItemMap.put(KeyPreferences.VIEW_FRAGMENT_BUTTONS_ACTION,showFragmentButtons);

		viewMenu.addSeparator();
		JMenu editorProperties = new JMenu( "Editor Properties");
		
		viewMenu.add( editorProperties);
		
		editorBookmarkMargin = new JCheckBoxMenuItem( "Show Annotation Margin");
		editorBookmarkMargin.setSelected( getProperties().getEditorProperties().isShowAnnotationMargin());
		editorBookmarkMargin.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setShowAnnotationMargin( editorBookmarkMargin.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().showAnnotationMargin( editorBookmarkMargin.isSelected());
				}
			}
		});

		editorProperties.add( editorBookmarkMargin);
		menuItemMap.put( KeyPreferences.VIEW_EDITOR_SHOW_ANNOTATION_ACTION, editorBookmarkMargin);

		editorLinenumberMargin = new JCheckBoxMenuItem( "Show Linenumber Margin");
		editorLinenumberMargin.setSelected( getProperties().getEditorProperties().isShowMargin());
		editorLinenumberMargin.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setShowMargin( editorLinenumberMargin.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().showLinenumberMargin( editorLinenumberMargin.isSelected());
				}
			}
		});

		editorProperties.add( editorLinenumberMargin);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_SHOW_LINE_NUMBER_ACTION,editorLinenumberMargin);

		editorFoldingMargin = new JCheckBoxMenuItem( "Show Folding Margin");
		editorFoldingMargin.setSelected( getProperties().getEditorProperties().isShowFoldingMargin());
		editorFoldingMargin.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setShowFoldingMargin( editorFoldingMargin.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().showFoldingMargin( editorFoldingMargin.isSelected());
				}
			}
		});

		editorProperties.add( editorFoldingMargin);
		menuItemMap.put( KeyPreferences.VIEW_EDITOR_SHOW_FOLDING_ACTION,editorFoldingMargin);

		editorOverviewMargin = new JCheckBoxMenuItem( "Show Overview Margin");
		editorOverviewMargin.setSelected( getProperties().getEditorProperties().isShowOverviewMargin());
		editorOverviewMargin.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setShowOverviewMargin( editorOverviewMargin.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().showOverviewMargin( editorOverviewMargin.isSelected());
				}
			}
		});

		editorProperties.add( editorOverviewMargin);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_SHOW_OVERVIEW_ACTION,editorOverviewMargin);

		editorProperties.addSeparator();

		editorTagCompletion = new JCheckBoxMenuItem( "Tag Completion");
		editorTagCompletion.setSelected( getProperties().getEditorProperties().isTextPrompting());
		editorTagCompletion.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setTextPrompting( editorTagCompletion.isSelected());
			}
		});

		editorProperties.add( editorTagCompletion);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_TAG_COMPLETION_ACTION,editorTagCompletion);

		editorEndTagCompletion = new JCheckBoxMenuItem( "End Tag Completion");
		editorEndTagCompletion.setSelected( getProperties().getEditorProperties().isTagCompletion());
		editorEndTagCompletion.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setTagCompletion( editorEndTagCompletion.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().setEndTagCompletion( editorEndTagCompletion.isSelected());
				}
			}
		});

		editorProperties.add( editorEndTagCompletion);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_END_TAG_COMPLETION_ACTION,editorEndTagCompletion);

		editorSmartIndentation = new JCheckBoxMenuItem( "Smart Indentation");
		editorSmartIndentation.setSelected( getProperties().getEditorProperties().isSmartIndentation());
		editorSmartIndentation.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setSmartIndentation( editorSmartIndentation.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().setSmartIndentation( editorSmartIndentation.isSelected());
				}
			}
		});

		editorProperties.add( editorSmartIndentation);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_SMART_INDENTATION_ACTION,editorSmartIndentation);

		editorProperties.addSeparator();

		editorErrorHighlighting = new JCheckBoxMenuItem( "Error Highlighting");
		editorErrorHighlighting.setSelected( getProperties().getEditorProperties().isErrorHighlighting());
		editorErrorHighlighting.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setErrorHighlighting( editorErrorHighlighting.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().setErrorHighlighting( editorErrorHighlighting.isSelected());
				}
			}
		});

		editorProperties.add( editorErrorHighlighting);
		menuItemMap.put( KeyPreferences.VIEW_EDITOR_ERROR_HIGHLIGHTING_ACTION,editorErrorHighlighting);

		editorProperties.addSeparator();

		editorSoftWrapping = new JCheckBoxMenuItem( "Soft Wrapping");
		editorSoftWrapping.setSelected( getProperties().getEditorProperties().isSoftWrapping());
		editorSoftWrapping.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getEditorProperties().setSoftWrapping( editorSoftWrapping.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getEditor().updatePreferences();
				}
			}
		});

		editorProperties.add( editorSoftWrapping);
		menuItemMap.put(KeyPreferences.VIEW_EDITOR_SOFT_WRAPPING_ACTION,editorSoftWrapping);
		
		JMenu viewerProperties = new JMenu( "Viewer Properties");
		viewMenu.add( viewerProperties);
		
		viewerShowNamespaces = new JCheckBoxMenuItem( "Show Namespaces");
		viewerShowNamespaces.setSelected( getProperties().getViewerProperties().isShowNamespaces());
		viewerShowNamespaces.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getViewerProperties().showNamespaces( viewerShowNamespaces.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getViewer().updatePreferences();
				}
			}
		});

		viewerProperties.add( viewerShowNamespaces);
		menuItemMap.put(KeyPreferences.VIEWER_SHOW_NAMESPACES_ACTION,viewerShowNamespaces);

		viewerShowAttributes = new JCheckBoxMenuItem( "Show Attributes");
		viewerShowAttributes.setSelected( getProperties().getViewerProperties().isShowAttributes());
		viewerShowAttributes.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getViewerProperties().showAttributes( viewerShowAttributes.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getViewer().updatePreferences();
				}
			}
		});

		viewerProperties.add( viewerShowAttributes);
		menuItemMap.put(KeyPreferences.VIEWER_SHOW_ATTRIBUTES_ACTION,viewerShowAttributes);

		viewerShowComments = new JCheckBoxMenuItem( "Show Comments");
		viewerShowComments.setSelected( getProperties().getViewerProperties().isShowComments());
		viewerShowComments.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getViewerProperties().showComments( viewerShowComments.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getViewer().updatePreferences();
				}
			}
		});

		viewerProperties.add( viewerShowComments);
		menuItemMap.put(KeyPreferences.VIEWER_SHOW_COMMENTS_ACTION,viewerShowComments);

		viewerShowContent = new JCheckBoxMenuItem( "Show Text Content");
		viewerShowContent.setSelected( getProperties().getViewerProperties().isShowValues());
		viewerShowContent.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getViewerProperties().showValues( viewerShowContent.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getViewer().updatePreferences();
				}
			}
		});

		viewerProperties.add( viewerShowContent);
		menuItemMap.put(KeyPreferences.VIEWER_SHOW_TEXT_CONTENT_ACTION,viewerShowContent);

		viewerShowPIs = new JCheckBoxMenuItem( "Show Processing Instructions");
		viewerShowPIs.setSelected( getProperties().getViewerProperties().isShowPI());
		viewerShowPIs.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getViewerProperties().showPI( viewerShowPIs.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getViewer().updatePreferences();
				}
			}
		});

		viewerProperties.add( viewerShowPIs);
		menuItemMap.put(KeyPreferences.VIEWER_SHOW_PROCESSING_INSTRUCTIONS_ACTION,viewerShowPIs);
		viewerProperties.addSeparator();

		viewerInlineMixed = new JCheckBoxMenuItem( "Inline Mixed Content");
		viewerInlineMixed.setSelected( getProperties().getViewerProperties().isShowInline());
		viewerInlineMixed.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getViewerProperties().showInline( viewerInlineMixed.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getViewer().updatePreferences();
				}
			}
		});

		viewerProperties.add( viewerInlineMixed);
		menuItemMap.put(KeyPreferences.VIEWER_INLINE_MIXED_CONTENT_ACTION,viewerInlineMixed);

		JMenu outlinerProperties = new JMenu( "Outliner Properties");
		
		viewMenu.add( outlinerProperties);
		
		outlinerShowAttributeValues = new JCheckBoxMenuItem( "Show Attribute Values");
		outlinerShowAttributeValues.setSelected( getProperties().getDesignerProperties().isShowAttributeValues());
		outlinerShowAttributeValues.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getDesignerProperties().setShowAttributeValues( outlinerShowAttributeValues.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getDesigner().updatePreferences();
				}
			}
		});

		outlinerProperties.add( outlinerShowAttributeValues);
		menuItemMap.put(KeyPreferences.OUTLINER_SHOW_ATTRIBUTE_VALUES_ACTION,outlinerShowAttributeValues);

		outlinerShowElementValues = new JCheckBoxMenuItem( "Show Element Values");
		outlinerShowElementValues.setSelected( getProperties().getDesignerProperties().isShowElementValues());
		outlinerShowElementValues.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getDesignerProperties().setShowElementValues( outlinerShowElementValues.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getDesigner().updatePreferences();
				}
			}
		});

		outlinerProperties.add( outlinerShowElementValues);
		menuItemMap.put(KeyPreferences.OUTLINER_SHOW_ELEMENT_VALUES_ACTION,outlinerShowElementValues);
		outlinerProperties.addSeparator();

		outlinerAutoCreateRequiredNodes = new JCheckBoxMenuItem( "Auto Create Required Nodes");
		outlinerAutoCreateRequiredNodes.setSelected( getProperties().getDesignerProperties().isAutoCreateRequired());
		outlinerAutoCreateRequiredNodes.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().getDesignerProperties().setAutoCreateRequired( outlinerAutoCreateRequiredNodes.isSelected());
				
				Vector views = getViews();
				for (int i = 0; i < views.size(); i++) {
					((ExchangerView) views.elementAt(i)).getDesigner().updatePreferences();
				}
			}
		});

		outlinerProperties.add( outlinerAutoCreateRequiredNodes);
		menuItemMap.put(KeyPreferences.OUTLINER_CREATE_REQUIRED_NODES_ACTION,outlinerAutoCreateRequiredNodes);

//		for each of the plugin buttons
		for(int cnt=0;cnt<this.getPluginViews().size();++cnt) {
			Object obj = this.getPluginViews().get(cnt);
			if((obj != null) && (obj instanceof PluginView)) {
				PluginView pluginView = (PluginView)obj;
				if(pluginView != null) {
					
					JMenu pluginMenu = pluginView.createPluginViewPropertiesMenu();
					if(pluginMenu != null) {
						viewMenu.add(pluginMenu);
					}
					
				}				
			}
		}
		
		/*if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
			//grid properties
			JMenu gridProperties = new JMenu( "Grid Properties");
	 		viewMenu.add( gridProperties);
		
 		
	 		//gridHideContainerTables = new JCheckBoxMenuItem( "Hide Container Tables");
	 		//gridHideContainerTables.setSelected( properties.getGridProperties().isHideContainerTables());
	 		//gridHideContainerTables.addItemListener( new ItemListener(){
//	 			public void itemStateChanged( ItemEvent e) {
	 				//properties.getGridProperties().hideContainerTables( gridHideContainerTables.isSelected());
	 				
	 				//Vector views = getViews();
	 				//for (int i = 0; i < views.size(); i++) {
//	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				//}
	 			//}
	 		//});
	 		//gridProperties.add( gridHideContainerTables);
	 		
	 		gridSupportMixedContent = new JCheckBoxMenuItem( "Support Mixed Content");
	 		gridSupportMixedContent.setSelected( getProperties().getGridProperties().isMixedContentSupported());
	 		gridSupportMixedContent.addItemListener( new ItemListener(){
	 			public void itemStateChanged( ItemEvent e) {
	 				getProperties().getGridProperties().supportMixedContent( gridSupportMixedContent.isSelected());
	 				
	 				Vector views = getViews();
	 				for (int i = 0; i < views.size(); i++) {
	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				}
	 			}
	 		});
	 		gridProperties.add( gridSupportMixedContent);
	 		
	 		gridSchemaAware = new JCheckBoxMenuItem( "Schema Aware");
	 		gridSchemaAware.setSelected( getProperties().getGridProperties().isGridSchemaAware());
	 		gridSchemaAware.addItemListener( new ItemListener(){
	 			public void itemStateChanged( ItemEvent e) {
	 				getProperties().getGridProperties().setGridSchemaAware( gridSchemaAware.isSelected());
	 				
	 				Vector views = getViews();
	 				for (int i = 0; i < views.size(); i++) {
	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				}
	 			}
	 		});
	 		gridProperties.add( gridSchemaAware);
	 		
	 		gridSchemaHighlightRequired = new JCheckBoxMenuItem( "Highlight Required");
	 		gridSchemaHighlightRequired.setSelected( getProperties().getGridProperties().isGridSchemaHighlightRequired());
	 		gridSchemaHighlightRequired.addItemListener( new ItemListener(){
	 			public void itemStateChanged( ItemEvent e) {
	 				getProperties().getGridProperties().setGridSchemaHighlightRequired( gridSchemaHighlightRequired.isSelected());
	 				
	 				Vector views = getViews();
	 				for (int i = 0; i < views.size(); i++) {
	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				}
	 			}
	 		});
	 		gridProperties.add( gridSchemaHighlightRequired);
	 		
	 		gridProperties.addSeparator();
	 		
	 		JMenu gridToolbarMenu = new JMenu("Toolbar");
	 		ButtonGroup gridToolbarButtonGroup = new ButtonGroup();
	 		
	 		gridToolbarShowOnLeft = new JRadioButtonMenuItem("Show On Left");
	 		
	 		gridToolbarShowOnLeft.setSelected( getProperties().getGridProperties().isGridToolbarShowOnLeft());
	 		gridToolbarShowOnLeft.addItemListener( new ItemListener(){
	 			public void itemStateChanged( ItemEvent e) {
	 				getProperties().getGridProperties().setGridToolbarShowOnLeft( gridToolbarShowOnLeft.isSelected());
	 				
	 				Vector views = getViews();
	 				for (int i = 0; i < views.size(); i++) {
	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				}
	 			}
	 		});
	 		gridToolbarButtonGroup.add(gridToolbarShowOnLeft);
	 		gridToolbarMenu.add( gridToolbarShowOnLeft);
	 		
	 		
	 		gridToolbarShowOnTop = new JRadioButtonMenuItem("Show On Top");
	 		
	 		gridToolbarShowOnTop.setSelected( getProperties().getGridProperties().isGridToolbarShowOnTop());
	 		gridToolbarShowOnTop.addItemListener( new ItemListener(){
	 			public void itemStateChanged( ItemEvent e) {
	 				getProperties().getGridProperties().setGridToolbarShowOnTop( gridToolbarShowOnTop.isSelected());
	 				
	 				Vector views = getViews();
	 				for (int i = 0; i < views.size(); i++) {
	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				}
	 			}
	 		});
	 		gridToolbarButtonGroup.add(gridToolbarShowOnTop);
	 		gridToolbarMenu.add( gridToolbarShowOnTop);
	 		
	 		
	 		gridToolbarHide = new JRadioButtonMenuItem("Hide");
	 		
	 		gridToolbarHide.setSelected( getProperties().getGridProperties().isGridToolbarHide());
	 		gridToolbarHide.addItemListener( new ItemListener(){
	 			public void itemStateChanged( ItemEvent e) {
	 				getProperties().getGridProperties().setGridToolbarHide( gridToolbarHide.isSelected());
	 				
	 				Vector views = getViews();
	 				for (int i = 0; i < views.size(); i++) {
	 					((ExchangerView) views.elementAt(i)).getGrid().updatePreferences();
	 				}
	 			}
	 		});
	 		gridToolbarButtonGroup.add(gridToolbarHide);
	 		gridToolbarMenu.add( gridToolbarHide);
	 		
	 		gridProperties.add(gridToolbarMenu);
			
			viewMenu.addSeparator();
		
		}*/
		
		viewMenu.addSeparator();
		
		viewMenu.add(createMenuItem(getChangeDocumentAction(),KeyPreferences.SELECT_DOCUMENT_ACTION));
		
		viewMenu.addSeparator();
		
		viewMenu.add(createMenuItem(getSplitTabsHorizontallyAction(),KeyPreferences.VIEW_SPLIT_HORIZONTALLY_ACTION));
		viewMenu.add(createMenuItem(getSplitTabsVerticallyAction(),KeyPreferences.VIEW_SPLIT_VERTICALLY_ACTION));
		viewMenu.add(createMenuItem(getUnsplitTabsAction(),KeyPreferences.VIEW_UNSPLIT_ACTION));

		viewMenu.addSeparator();

		synchroniseSplits = new JCheckBoxMenuItem( "Synchronise Splits on XPath", getIcon( SYNCHRONISE_SPLITS_ICON));
		synchroniseSplits.setSelected( getProperties().isSynchroniseSplits());
		synchroniseSplits.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				getProperties().setSynchroniseSplits( synchroniseSplits.isSelected());
			}
		});
		viewMenu.add( synchroniseSplits);
		menuItemMap.put(KeyPreferences.VIEW_SYNCHRONIZE_SPLITS_ACTION,synchroniseSplits);

		
		//		viewMenu.addSeparator();
		//		
		//		JMenu viewerMenu = new JMenu( "Viewer");
		//		showAttributesItem = new JCheckBoxMenuItem( "Attributes", properties.getViewerProperties().isShowAttributes());
		//		showAttributesItem.addItemListener( new ItemListener() {
		//			public void itemStateChanged( ItemEvent e) {
		//				ExchangerView currentView = getView();
		//				
		//				if ( currentView != null) {
		//					Viewer viewer = currentView.getViewer();
		//
		//					viewer.setShowAttributes( showAttributesItem.isSelected());
		//				} else {
		//					properties.getViewerProperties().showAttributes( showAttributesItem.isSelected());
		//				}
		//			}
		//		});
		//		viewerMenu.add( showAttributesItem);
		//
		//		showNamespacesItem = new JCheckBoxMenuItem( "Namespaces", properties.getViewerProperties().isShowNamespaces());
		//		showNamespacesItem.addItemListener( new ItemListener() {
		//			public void itemStateChanged( ItemEvent e) {
		//				ExchangerView currentView = getView();
		//
		//				if ( currentView != null) {
		//					Viewer viewer = currentView.getViewer();
		//
		//					viewer.setShowNamespaces( showNamespacesItem.isSelected());
		//				} else {
		//					properties.getViewerProperties().showNamespaces( showNamespacesItem.isSelected());
		//				}
		//			}
		//		});
		//		viewerMenu.add( showNamespacesItem);
		//
		//		showValuesItem = new JCheckBoxMenuItem( "Values", properties.getViewerProperties().isShowValues());
		//		showValuesItem.addItemListener( new ItemListener() {
		//			public void itemStateChanged( ItemEvent e) {
		//				ExchangerView currentView = getView();
		//
		//				if ( currentView != null) {
		//					Viewer viewer = currentView.getViewer();
		//					viewer.setShowValues( showValuesItem.isSelected());
		//				} else {
		//					properties.getViewerProperties().showValues( showValuesItem.isSelected());
		//				}
		//			}
		//		});
		//		viewerMenu.add( showValuesItem);
		//
		//		showCommentsItem = new JCheckBoxMenuItem( "Comments", properties.getViewerProperties().isShowComments());
		//		showCommentsItem.addItemListener( new ItemListener() {
		//			public void itemStateChanged( ItemEvent e) {
		//				ExchangerView currentView = getView();
		//
		//				if ( currentView != null) {
		//					Viewer viewer = currentView.getViewer();
		//					viewer.setShowComments( showCommentsItem.isSelected());
		//				} else {
		//					properties.getViewerProperties().showComments( showCommentsItem.isSelected());
		//				}
		//			}
		//		});
		//		viewerMenu.add( showCommentsItem);
		//		viewMenu.add( viewerMenu);

		viewMenu.addSeparator();

		viewMenu.add( createMenuItem( getToggleFullScreenAction(), KeyPreferences.TOGGLE_FULL_ACTION));

		GUIUtilities.alignMenu( viewMenu);
		return viewMenu;
	}
	
	// create the Project menu...
	private JMenu createProjectMenu() {
		JMenu projectMenu = new JMenu("Project");
		projectMenu.setMnemonic('P');

		projectMenu.add(createMenuItem(getProjectPanel().getNewProjectAction(),KeyPreferences.NEW_PROJECT_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getImportProjectAction(),KeyPreferences.IMPORT_PROJECT_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getDeleteProjectAction(),KeyPreferences.DELETE_PROJECT_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getRenameProjectAction(),KeyPreferences.RENAME_PROJECT_ACTION));

		projectMenu.addSeparator();

		//		projectMenu.add( createMenuItem( projectPanel.getOpenFileAction()));
		//
		//		projectMenu.addSeparator();

		projectMenu.add(createMenuItem(getProjectPanel().getParseAction(),KeyPreferences.CHECK_WELLFORMEDNESS_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getValidateAction(),KeyPreferences.VALIDATE_PROJECT_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getFindInProjectsAction(),KeyPreferences.FIND_IN_PROJECTS_ACTION));

		projectMenu.addSeparator();

		projectMenu.add(createMenuItem(getProjectPanel().getAddFileAction(),KeyPreferences.ADD_FILE_ACTION));
		projectMenu.add(
			createMenuItem(getProjectPanel().getAddRemoteDocumentAction(),KeyPreferences.ADD_REMOTE_FILE_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getRemoveFileAction(),KeyPreferences.REMOVE_FILE_ACTION));

		projectMenu.addSeparator();

		projectMenu.add(createMenuItem(getProjectPanel().getAddDirectoryAction(),KeyPreferences.ADD_DIRECTORY_ACTION));
		projectMenu.add(
			createMenuItem(getProjectPanel().getAddDirectoryContentsAction(),KeyPreferences.ADD_DIRECTORY_CONTENTS_ACTION));
		projectMenu.add(getProjectPanel().getAddVirtualDirectoryAction());
		projectMenu.add(getProjectPanel().getRefreshVirtualDirectoryAction());
		
		projectMenu.addSeparator();

		projectMenu.add(createMenuItem(getProjectPanel().getAddFolderAction(),KeyPreferences.ADD_FOLDER_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getRemoveFolderAction(),KeyPreferences.REMOVE_FOLDER_ACTION));
		projectMenu.add(createMenuItem(getProjectPanel().getRenameFolderAction(),KeyPreferences.RENAME_FOLDER_ACTION));

		//		projectMenu.addSeparator();
		//
		//		projectMenu.add( createMenuItem( projectPanel.getProjectPropertiesAction()));

		GUIUtilities.alignMenu( projectMenu);
		return projectMenu;
	}
	
	public void updateFragments() {
		if ( currentView != null && getCurrent() instanceof Editor) {
			((Editor)getCurrent()).updateFragmentKeys();
		}
	}

	// create the XML menu...
	private JMenu createXMLMenu() {
		JMenu xmlMenu = new JMenu("XML");
		xmlMenu.setMnemonic('X');

		xmlMenu.add(createMenuItem(getParseAction(),KeyPreferences.WELL_FORMEDNESS_ACTION));
		xmlMenu.add(createMenuItem(getValidateAction(),KeyPreferences.VALIDATE_ACTION));

		xmlMenu.addSeparator();

		xmlMenu.add(createMenuItem(getSetSchemaPropertiesAction(),KeyPreferences.SET_SCHEMA_PROPS_ACTION));
//		xmlMenu.add(createMenuItem(getValidateSchemaAction(),KeyPreferences.VALIDATE_XML_SCHEMA_ACTION));
//		xmlMenu.add(createMenuItem(getValidateDTDAction(),KeyPreferences.VALIDATE_DTD_ACTION));
//		xmlMenu.add(createMenuItem(getValidateRelaxNGAction(),KeyPreferences.VALIDATE_RELAXNG_ACTION));
		xmlMenu.addSeparator();
		
		xmlMenu.add(createMenuItem(getToolsStripTextAction(),KeyPreferences.TOOLS_EMPTY_DOCUMENT_ACTION));
		
		JMenu changeCaseMenu = new JMenu("Change Case");
		changeCaseMenu.setMnemonic('H');
				
		changeCaseMenu.add(createMenuItem(getToolsCapitalizeAction(),KeyPreferences.TOOLS_CAPITALIZE_ACTION));
		changeCaseMenu.add(createMenuItem(getToolsDeCapitalizeAction(),KeyPreferences.TOOLS_DECAPITALIZE_ACTION));
		changeCaseMenu.add(createMenuItem(getToolsUppercaseAction(),KeyPreferences.TOOLS_UPPERCASE_ACTION));
		changeCaseMenu.add(createMenuItem(getToolsLowercaseAction(),KeyPreferences.TOOLS_LOWERCASE_ACTION));
		
		xmlMenu.add(changeCaseMenu);
		
		JMenu namespacesMenu = new JMenu("Namespaces");
		namespacesMenu.setMnemonic('A');
		
		
		namespacesMenu.add(createMenuItem(getToolsMoveNSToRootAction(),KeyPreferences.TOOLS_MOVE_NS_TO_ROOT_ACTION));
		namespacesMenu.add(createMenuItem(getToolsMoveNSToFirstUsedAction(),KeyPreferences.TOOLS_MOVE_NS_TO_FIRST_USED_ACTION));
		namespacesMenu.add(createMenuItem(getToolsChangeNSPrefixAction(),KeyPreferences.TOOLS_CHANGE_NS_PREFIX_ACTION));
		namespacesMenu.add(createMenuItem(getToolsRemoveUnusedNSAction(),KeyPreferences.TOOLS_REMOVE_UNUSED_NS_ACTION));
		
		xmlMenu.add(namespacesMenu);
		
		JMenu nodesMenu = new JMenu("Nodes");
		nodesMenu.setMnemonic('N');
		
		nodesMenu.add(createMenuItem(getToolsAddNodeAction(),KeyPreferences.TOOLS_ADD_NODE_ACTION));
		nodesMenu.add(createMenuItem(getToolsRemoveNodeAction(),KeyPreferences.TOOLS_REMOVE_NODE_ACTION));
		nodesMenu.add(createMenuItem(getToolsSetNodeValueAction(),KeyPreferences.TOOLS_SET_NODE_VALUE_ACTION));
		nodesMenu.add(createMenuItem(getToolsRenameNodeAction(),KeyPreferences.TOOLS_RENAME_NODE_ACTION));
		nodesMenu.add(createMenuItem(getToolsConvertNodeAction(),KeyPreferences.TOOLS_CONVERT_NODE_ACTION));
		nodesMenu.add(createMenuItem(getToolsAddNodeToNamespaceAction(),KeyPreferences.TOOLS_ADD_NODE_TO_NS_ACTION));
		nodesMenu.add(createMenuItem(getToolsSortNodeAction(),KeyPreferences.TOOLS_SORT_NODE_ACTION));
		
		xmlMenu.add(nodesMenu);
				
		xmlMenu.addSeparator();
		xmlMenu.add(createMenuItem(getSetXMLDeclarationAction(),KeyPreferences.SET_XML_DECLARATION_ACTION));
		xmlMenu.add(createMenuItem(getSetXMLDoctypeAction(),KeyPreferences.SET_DOCTYPE_DECLARATION_ACTION));
		xmlMenu.add(createMenuItem(getSetSchemaLocationAction(),KeyPreferences.SET_SCHEMA_LOCATION_ACTION));
		xmlMenu.addSeparator();
		xmlMenu.add(createMenuItem(getResolveXIncludesAction(),KeyPreferences.RESOLVE_XINCLUDES_ACTION));

		GUIUtilities.alignMenu( xmlMenu);

		return xmlMenu;
	}
	
//	 create the security menu...
	private JMenu createSecurityMenu() {
		JMenu securityMenu = new JMenu("Security");
		securityMenu.setMnemonic('c');

		securityMenu.add(createMenuItem(getCanonicalizeAction(),KeyPreferences.CANONICALIZE_ACTION));
		securityMenu.add(createMenuItem(getSignDocumentAction(),KeyPreferences.SIGN_DOCUMENT_ACTION));
		securityMenu.add(createMenuItem(getVerifySignatureAction(),KeyPreferences.VERIFY_SIGNATURE_ACTION));

		GUIUtilities.alignMenu( securityMenu);
		return securityMenu;
	}
	
//	 create the Tools menu...
	private JMenu createToolsMenu() {
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('o');

		toolsMenu.add(createMenuItem(getOpenSVGAction(),KeyPreferences.SHOW_SVG_ACTION));
		toolsMenu.add(createMenuItem(getConvertSVGAction(),KeyPreferences.CONVERT_SVG_ACTION));
		toolsMenu.addSeparator();
		toolsMenu.add(createMenuItem(getSendSOAPAction(),KeyPreferences.SEND_SOAP_MESSAGE_ACTION));
		toolsMenu.add(createMenuItem(getAnalyseWSDLAction(),KeyPreferences.ANALYSE_WSDL_ACTION));
		toolsMenu.addSeparator();
		toolsMenu.add(createMenuItem(getCleanUpHTMLAction(),KeyPreferences.CLEAN_UP_HTML_ACTION));
		if(!Identity.getIdentity().getEdition().equals( Identity.XMLPLUS_EDITION_LITE)) {
		  toolsMenu.addSeparator();
		  toolsMenu.add(createMenuItem(getXDiffAction(),KeyPreferences.XDIFF_ACTION));
		}
		toolsMenu.addSeparator();
		toolsMenu.add(createMenuItem(getOpenBrowserAction(),KeyPreferences.START_BROWSER_ACTION));
		toolsMenu.add(createMenuItem(getExecuteScriptAction(),KeyPreferences.EXECUTE_SCRIPT_ACTION));

		GUIUtilities.alignMenu( toolsMenu);
		return toolsMenu;	
	}
	
//	 create the grid menu bar...
	/*private JMenu createGridMenu() {
		// >>> Grid Menu
	    JMenu gridMenu = new JMenu("Grid");
		gridMenu.setMnemonic('G');
		
		JMenu addMenu = new JMenu("Add");
		JMenu editMenu = new JMenu("Edit");
		JMenu deleteMenu = new JMenu("Delete");
		JMenu moveMenu = new JMenu("Move/Sort");
		JMenu navigateMenu = new JMenu("Navigate");
				
		addMenu.add(createMenuItem(getGridBridgeAddAttributeToSelectedAction(), KeyPreferences.GRID_ADD_ATTRIBUTE_TO_SELECTED_ACTION));
		addMenu.add(createMenuItem(getGridBridgeAddTextToSelectedAction(), KeyPreferences.GRID_ADD_TEXT_TO_SELECTED_ACTION));
		addMenu.add(createMenuItem(getGridBridgeAddChildTableAction(), KeyPreferences.GRID_ADD_CHILD_TABLE_ACTION));
		
		addMenu.addSeparator();
		
		addMenu.add(createMenuItem(getGridBridgeAddAttributeColumnAction(), KeyPreferences.GRID_ADD_ATTRIBUTE_COLUMN_ACTION));
		addMenu.add(createMenuItem(getGridBridgeAddTextColumnAction(), KeyPreferences.GRID_ADD_TEXT_COLUMN_ACTION));
		
		addMenu.addSeparator();
		
		addMenu.add(createMenuItem(getGridBridgeAddElementBeforeAction(), KeyPreferences.GRID_ADD_ELEMENT_BEFORE_ACTION));
		addMenu.add(createMenuItem(getGridBridgeAddElementAfterAction(), KeyPreferences.GRID_ADD_ELEMENT_AFTER_ACTION));
		
		//---------------
		
		//editMenu.add(createMenuItem(getCutAction(), KeyPreferences.CUT_ACTION));
		//editMenu.add(createMenuItem(getCopyAction(), KeyPreferences.COPY_ACTION));
		JMenu copySpecialMenu = new JMenu("Copy Special");
		copySpecialMenu.add(createMenuItem(getGridBridgeCopyShallowAction(), KeyPreferences.GRID_COPY_SHALLOW_ACTION));
		
		editMenu.add(copySpecialMenu);
		//editMenu.add(createMenuItem(getPasteAction(), KeyPreferences.PASTE_ACTION));
		JMenu pasteSpecialMenu = new JMenu("Paste Special");
		pasteSpecialMenu.add(createMenuItem(getGridBridgePasteBeforeAction(), KeyPreferences.GRID_PASTE_BEFORE_ACTION));
		pasteSpecialMenu.add(createMenuItem(getGridBridgePasteAfterAction(), KeyPreferences.GRID_PASTE_AFTER_ACTION));
		pasteSpecialMenu.add(createMenuItem(getGridBridgePasteAsChildAction(), KeyPreferences.GRID_PASTE_AS_CHILD_ACTION));
		editMenu.add(pasteSpecialMenu);
		
		editMenu.addSeparator();
		
		editMenu.add(createMenuItem(getGridBridgeEditSelectedAttributeNameAction(), KeyPreferences.GRID_RENAME_SELECTED_ATTRIBUTE_ACTION));
		editMenu.add(createMenuItem(getGridBridgeEditAttributeNameAction(), KeyPreferences.GRID_RENAME_ATTRIBUTE_ACTION));
				
		//---------------
		
		deleteMenu.add(createMenuItem(getGridBridgeDeleteSelectedAttributeAction(), KeyPreferences.GRID_DELETE_SELECTED_ATTRIBUTE_ACTION));
		deleteMenu.add(createMenuItem(getGridBridgeDeleteSelectedTextAction(), KeyPreferences.GRID_DELETE_SELECTED_TEXT_ACTION));
		deleteMenu.add(createMenuItem(getGridBridgeDeleteElementAction(), KeyPreferences.GRID_DELETE_ROW_ACTION));
		deleteMenu.add(createMenuItem(getGridBridgeDeleteAttsAndTextAction(), KeyPreferences.GRID_DELETE_ATTS_AND_TEXT_ACTION));
		deleteMenu.add(createMenuItem(getGridBridgeDeleteChildTableAction(), KeyPreferences.GRID_DELETE_CHILD_TABLE_ACTION));
		
		deleteMenu.addSeparator();
		
		deleteMenu.add(createMenuItem(getGridBridgeDeleteColumnAction(), KeyPreferences.GRID_DELETE_COLUMN_ACTION));
		
		//---------------
		
		moveMenu.add(createMenuItem(getGridBridgeMoveRowUpAction(), KeyPreferences.GRID_MOVE_ROW_UP_ACTION));		
		moveMenu.add(createMenuItem(getGridBridgeMoveRowDownAction(), KeyPreferences.GRID_MOVE_ROW_DOWN_ACTION));
		
		moveMenu.addSeparator();
		
		moveMenu.add(createMenuItem(getGridBridgeSortTableAscendingAction(), KeyPreferences.GRID_SORT_TABLE_ASCENDING_ACTION));
		moveMenu.add(createMenuItem(getGridBridgeSortTableDescendingAction(), KeyPreferences.GRID_SORT_TABLE_DESCENDING_ACTION));
		moveMenu.add(createMenuItem(getGridBridgeUnsortTableAction(), KeyPreferences.GRID_UNSORT_ACTION));		
		
		//---------------
		navigateMenu.add(createMenuItem(getGridBridgeExpandRowAction(), KeyPreferences.GRID_EXPAND_ROW_ACTION));
		navigateMenu.add(createMenuItem(getGridBridgeCollapseRowAction(), KeyPreferences.GRID_COLLAPSE_ROW_ACTION));
		
		navigateMenu.addSeparator();
		
		navigateMenu.add(createMenuItem(getGridBridgeGotoChildTableAction(), KeyPreferences.GRID_GOTO_CHILD_TABLE_ACTION));
		navigateMenu.add(createMenuItem(getGridBridgeGotoParentTableAction(), KeyPreferences.GRID_GOTO_PARENT_TABLE_ACTION));
		
		navigateMenu.addSeparator();
		
		navigateMenu.add(createMenuItem(getGridBridgeCollapseCurrentTableAction(), KeyPreferences.GRID_COLLAPSE_CURRENT_TABLE_ACTION));
		
		//---------------
		
		//just for keys
		createMenuItem(getGridBridgeDeleteAction(), KeyPreferences.GRID_DELETE_ACTION);
		
		gridMenu.add(addMenu);
		gridMenu.add(editMenu);
		gridMenu.add(deleteMenu);
		gridMenu.add(moveMenu);
		gridMenu.add(navigateMenu);
		
		GUIUtilities.alignMenu( gridMenu);
		// <<< Grid Menu
		return (gridMenu);
	}*/
	
	

	// create the grammar menu...
	private JMenu createGrammarMenu() {
		JMenu grammarMenu = new JMenu("Schema");
		grammarMenu.setMnemonic('S');
		grammarMenu.add(createMenuItem(getValidateSchemaAction(),KeyPreferences.VALIDATE_XML_SCHEMA_ACTION));
		grammarMenu.add(createMenuItem(getValidateDTDAction(),KeyPreferences.VALIDATE_DTD_ACTION));
		grammarMenu.add(createMenuItem(getValidateRelaxNGAction(),KeyPreferences.VALIDATE_RELAXNG_ACTION));

		grammarMenu.addSeparator();
		
//		grammarMenu.add(createMenuItem(getCreateSchemaAction(),KeyPreferences.INFER_SCHEMA_ACTION));
//		
//
//		grammarMenu.addSeparator();

		grammarMenu.add(createMenuItem(getNewGrammarAction(),KeyPreferences.CREATE_TYPE_ACTION));
		grammarMenu.add(createMenuItem(getOpenGrammarAction(),KeyPreferences.SET_TYPE_ACTION));

		//grammarMenu.addSeparator();

		grammarMenu.add(createMenuItem(getGrammarPropertiesAction(),KeyPreferences.TYPE_PROPERTIES_ACTION));

		//grammarMenu.addSeparator();

		grammarMenu.add(createMenuItem(getManageGrammarAction(),KeyPreferences.MANAGE_TYPES_ACTION));

		grammarMenu.addSeparator();

		grammarMenu.add(createMenuItem(getCreateSchemaAction(),KeyPreferences.INFER_SCHEMA_ACTION));
		grammarMenu.add(createMenuItem(getConvertGrammarAction(),KeyPreferences.CONVERT_SCHEMA_ACTION));
		grammarMenu.add(createMenuItem(getSchemaInstanceGenerationAction(),KeyPreferences.SCHEMA_INSTANCE_GENERATION_ACTION));
		//grammarMenu.add(createMenuItem(getGraphicalSchemaGenerationAction(),KeyPreferences.GRAPHICAL_SCHEMA_GENERATION_ACTION));

		GUIUtilities.alignMenu( grammarMenu);
		return grammarMenu;
	}

	private JMenu createScenarioMenu() {
		JMenu scenarioMenu = new JMenu("Transform");
		scenarioMenu.setMnemonic('T');

		scenarioMenu.add(createMenuItem(getExecuteSimpleXSLTAction(),KeyPreferences.EXECUTE_SIMPLE_XSLT_ACTION));
		scenarioMenu.add(createMenuItem(getExecuteAdvancedXSLTAction(),KeyPreferences.EXECUTE_ADVANCED_XSLT_ACTION));
		scenarioMenu.add(createMenuItem(getExecuteFOAction(),KeyPreferences.EXECUTE_FO_ACTION));
		scenarioMenu.add(createMenuItem(getExecuteXQueryAction(),KeyPreferences.EXECUTE_XQUERY_ACTION));
		scenarioMenu.add(createMenuItem(getExecuteSchematronAction(),KeyPreferences.EXECUTE_SCHEMATRON_ACTION));


		scenarioMenu.addSeparator();

		//		scenarioMenu.add( createMenuItem( getNewScenarioAction()));

		//		scenarioMenu.addSeparator();

		scenarioMenu.add(createMenuItem(getDefaultScenarioAction(),KeyPreferences.EXECUTE_SCENARIO_ACTION));
		scenarioMenu.add(createMenuItem(getDebugScenarioAction(),KeyPreferences.XSLT_DEBUGGER_ACTION));
		//		scenarioMenu.add( createMenuItem( getExecuteScenarioAction()));

		scenarioMenu.addSeparator();
		scenarioMenu.add(createMenuItem(getManageScenarioAction(),KeyPreferences.MANAGE_SCENARIOS_ACTION));

		JMenu executePreviousMenu = new JMenu( "Execute Previous");
		executePreviousMenu.setMnemonic( 'P');

		executePreviousMenu.add(createMenuItem(getExecutePreviousXSLTAction(),KeyPreferences.EXECUTE_PREVIOUS_XSLT_ACTION));
		executePreviousMenu.add(createMenuItem(getExecutePreviousFOAction(),KeyPreferences.EXECUTE_PREVIOUS_FO_ACTION));
		executePreviousMenu.add(createMenuItem(getExecutePreviousXQueryAction(),KeyPreferences.EXECUTE_PREVIOUS_XQUERY_ACTION));
		executePreviousMenu.add(createMenuItem(getExecutePreviousScenarioAction(),KeyPreferences.EXECUTE_PREVIOUS_SCENARIO_ACTION));

		GUIUtilities.alignMenu( executePreviousMenu);
		scenarioMenu.add( executePreviousMenu);
		
		
		
		
		GUIUtilities.alignMenu( scenarioMenu);

		return scenarioMenu;
	}
	
	public void updateProperties() {
		if (currentView != null) {
			String validationLocation = PropertiesPanel.INTERNAL_GRAMMAR;
			
			if ( currentView.getValidationGrammar().useExternal()) {
				validationLocation = currentView.getValidationGrammar().getLocation();
			}
			
			String tagCompletionLocation = PropertiesPanel.NO_LOCATION;

			Vector schemas = currentView.getTagCompletionSchemas();
			Vector tagCompletionLocations = new Vector(); 
			if ( schemas != null) {
				for ( int i = 0; i < schemas.size(); i++) {
					tagCompletionLocations.addElement( ((SchemaDocument)schemas.elementAt(i)).getURL().toString());
				}
			}
			
			String schemaLocation = PropertiesPanel.NO_LOCATION;

			XMLSchema schema = currentView.getSchema();
			if ( schema != null) {
				schemaLocation = schema.getURL().toString();
			}
			
			if ( currentView.getGrammar() != null) {
				propertiesPanel.setName( currentView.getGrammar().getDescription());
			} else {
				propertiesPanel.setName( "");
			}

			if ( document != null) {
				propertiesPanel.setEncoding( document.getEncoding());
			}

			propertiesPanel.setValidationLocation( validationLocation);
			propertiesPanel.setTagCompletionLocations( tagCompletionLocations);
			propertiesPanel.setSchemaViewerLocation( schemaLocation);
		} else {
			propertiesPanel.clear();
		}
	}

	public void updateStatus() {
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				if (currentView != null) {
					statusbar.setType(currentView.getStatusType());
					statusbar.setValidator(currentView.getStatusValidator());
					statusbar.setLocation(currentView.getStatusLocation());
					statusbar.setDocumentStatus(currentView.getStatusDocumentStatus());
					statusbar.setPosition( currentView.getEditor().getPosition().x, currentView.getEditor().getPosition().y);
				} else {
					statusbar.setType("");
					statusbar.setValidator("");
					statusbar.setLocation("");
					statusbar.clearPosition();
					statusbar.setDocumentStatus("");
				}

				updateProperties();
			}
		});
	}
	
	public void setSelected( ExchangerTabbedView tabs) {
		if ( selectedTabbedView != tabs) {
			
			for ( int i = 0; i < tabbedViews.size(); i++) {
				ExchangerTabbedView tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(i);

				if ( tabbedView != tabs) {
					tabbedView.setSelected( false);
				}
			}
		
			getSplitTabsHorizontallyAction().setEnabled( tabs.getViews().size() > 1 && !isFullScreen());
			getSplitTabsVerticallyAction().setEnabled( tabs.getViews().size() > 1 && !isFullScreen());
			getUnsplitTabsAction().setEnabled( tabbedViews.size() > 1 && !isFullScreen());

			if ( tabbedViews.size() > 1 && !isFullScreen()) {
				synchroniseSplits.setEnabled( true);
			} else {
				synchroniseSplits.setEnabled( false);
			}

			selectedTabbedView = tabs;
			setView( selectedTabbedView.getSelectedView());
		}
	}

	public TagAction getTagAction() {
		if (tagAction == null) {
			tagAction = new TagAction(this);
		}

		return tagAction;
	}
	
	public RepeatTagAction getRepeatTagAction() {
		if (repeatTagAction == null) {
			repeatTagAction = new RepeatTagAction(this);
		}

		return repeatTagAction;
	}

	public void splitHorizontally() {
		ExchangerTabbedView tabbedView = selectedTabbedView;
		
		if ( tabbedView.getViews().size() > 1) {

			tabbedView.disableChangeListener( true);

			Component parentComponent = tabbedView.getParent();
			
			if ( parentComponent instanceof JSplitPane) {
				JSplitPane split = (JSplitPane)parentComponent;
				boolean right = true;
		
				if ( tabbedView == split.getLeftComponent()) {
					right = false;
				}
				
				ExchangerTabbedView bottomView = new ExchangerTabbedView( this, tabbedView);
				bottomView.setScrollTabs( this.getProperties().isScrollDocumentTabs());
				bottomView.disableChangeListener( true);

				tabbedViews.addElement( bottomView);
				
				ExchangerView view = tabbedView.getSelectedView();
				tabbedView.remove( view);
		
				bottomView.add( view, view.getDocument().getName());
		
				JSplitPane newSplit =	new JSplitPane(	JSplitPane.VERTICAL_SPLIT, tabbedView, bottomView);
				newSplit.setResizeWeight( 0.5);
		
				if ( newSplit.getDividerSize() > 6) {
					newSplit.setDividerSize( 6);
				}
		
				Object ui = newSplit.getUI();
				if (ui instanceof BasicSplitPaneUI) {
					((BasicSplitPaneUI) ui).getDivider().setBorder(null);
				}
		
				newSplit.setBorder( null);
				newSplit.setOneTouchExpandable( true);
		
				if ( right) {
					split.setRightComponent( newSplit);
				} else {
					split.setLeftComponent( newSplit);
				}
				
				bottomView.setSelected( true);
				view.setViewIcons();

				bottomView.disableChangeListener( false);
			} else if ( parentComponent instanceof JPanel) {
				JPanel panel = (JPanel)parentComponent;
				
				ExchangerTabbedView bottomView = new ExchangerTabbedView( this, tabbedView);
				bottomView.setScrollTabs( this.getProperties().isScrollDocumentTabs());
				bottomView.disableChangeListener( true);
				tabbedViews.addElement( bottomView);
				
				ExchangerView view = tabbedView.getSelectedView();
				tabbedView.remove( view);
		
				bottomView.add( view, view.getDocument().getName());
		
				JSplitPane newSplit =	new JSplitPane(	JSplitPane.VERTICAL_SPLIT, tabbedView, bottomView);
				newSplit.setResizeWeight( 0.5);
		
				if ( newSplit.getDividerSize() > 6) {
					newSplit.setDividerSize( 6);
				}
		
				Object ui = newSplit.getUI();
				if (ui instanceof BasicSplitPaneUI) {
					((BasicSplitPaneUI) ui).getDivider().setBorder(null);
				}
		
				newSplit.setBorder( null);
				newSplit.setOneTouchExpandable( true);
		
				panel.removeAll();
				panel.add( editorToolbarPanel, BorderLayout.NORTH);
				panel.add( newSplit, BorderLayout.CENTER);
				
				bottomView.setSelected( true);
				view.setViewIcons();

				bottomView.disableChangeListener( false);
			}

			tabbedView.disableChangeListener( false);
		}

		Vector views = getViews();
		if ( views != null) {
			for ( int i = 0; i < views.size(); i++) {
				ExchangerView view = (ExchangerView)views.elementAt(i);
				view.getEditor().scrollCursorToVisible();
			}
		}
	}

	public void unsplit() {
		if ( tabbedViews.size() > 1) {
			ExchangerTabbedView parentTabbedView = null;
			ExchangerView selectedView = null;
			ExchangerTabbedView tabbedView = selectedTabbedView;
			int index = tabbedViews.indexOf( tabbedView);

			if ( index > 0) {
				parentTabbedView = (ExchangerTabbedView)tabbedViews.elementAt( index-1);

				selectedView = tabbedView.getSelectedView();
				if ( selectedView == null) {
					selectedView = parentTabbedView.getSelectedView();
				}
			} else {
				// Make the next element the tabbedView instead ...
				tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(1);
				// ... and the current the parent.
				parentTabbedView = selectedTabbedView;

				selectedView = parentTabbedView.getSelectedView();

				if ( selectedView == null) {
					selectedView = tabbedView.getSelectedView();
				}
			}

			tabbedView.removeListeners();
			parentTabbedView.disableChangeListener( true);

			JSplitPane split = (JSplitPane)tabbedView.getParent();
			Component parentComponent = split.getParent();
			
			if ( parentComponent instanceof JSplitPane) {
				JSplitPane parentSplit = (JSplitPane)parentComponent;
				boolean right = true;
		
				if ( tabbedView == split.getLeftComponent()) {
					right = false;
				} 
				
				Vector views = tabbedView.getViews();
				
				for ( int i = 0; i < views.size(); i++) {
					ExchangerView view = (ExchangerView)views.elementAt(i);
					tabbedView.remove( view);
				
					parentTabbedView.add( view, view.getDocument().getName());
					view.setViewIcons();
				}
				
				if ( split == parentSplit.getLeftComponent()) {
					if ( right) {
						parentSplit.setLeftComponent( split.getLeftComponent());
					} else {
						parentSplit.setLeftComponent( split.getRightComponent());
					}
				} else {
					if ( right) {
						parentSplit.setRightComponent( split.getLeftComponent());
					} else {
						parentSplit.setRightComponent( split.getRightComponent());
					}
				}
			} else if ( parentComponent instanceof JPanel) {
				JPanel parentPanel = (JPanel)parentComponent;
				
				boolean right = true;
				
				if ( tabbedView == split.getLeftComponent()) {
					right = false;
				} 
	
				Vector views = tabbedView.getViews();
				
				for ( int i = 0; i < views.size(); i++) {
					ExchangerView view = (ExchangerView)views.elementAt(i);
					tabbedView.remove( view);
				
					parentTabbedView.add( view, view.getDocument().getName());
					view.setViewIcons();
				}
				
				parentPanel.removeAll();
				parentPanel.add( editorToolbarPanel, BorderLayout.NORTH);

				if ( right) {
					parentPanel.add( split.getLeftComponent(), BorderLayout.CENTER);
				} else {
					parentPanel.add( split.getRightComponent(), BorderLayout.CENTER);
				}

				parentPanel.revalidate();
				parentPanel.repaint();
			}

			tabbedViews.remove( tabbedView);

			parentTabbedView.disableChangeListener( false);
			parentTabbedView.select( selectedView);

			getSplitTabsHorizontallyAction().setEnabled( selectedTabbedView.getViews().size() > 1 && !isFullScreen());
			getSplitTabsVerticallyAction().setEnabled( selectedTabbedView.getViews().size() > 1 && !isFullScreen());
			getUnsplitTabsAction().setEnabled( tabbedViews.size() > 1 && !isFullScreen());
		}
	}

	public void splitVertically() {
		ExchangerTabbedView tabbedView = selectedTabbedView;
		
		if ( tabbedView.getViews().size() > 1) {
			Component parentComponent = tabbedView.getParent();
			
			tabbedView.disableChangeListener( true);

			if ( parentComponent instanceof JSplitPane) {
				JSplitPane split = (JSplitPane)parentComponent;
				boolean right = true;
		
				if ( tabbedView == split.getLeftComponent()) {
					right = false;
				}
				
				ExchangerTabbedView bottomView = new ExchangerTabbedView( this, tabbedView);
				bottomView.setScrollTabs( this.getProperties().isScrollDocumentTabs());
				bottomView.disableChangeListener( true);
				tabbedViews.addElement( bottomView);
	
				ExchangerView view = tabbedView.getSelectedView();
				tabbedView.remove( view);
		
				bottomView.add( view, view.getDocument().getName());
		
				JSplitPane newSplit =	new JSplitPane(	JSplitPane.HORIZONTAL_SPLIT, tabbedView, bottomView);
				newSplit.setResizeWeight( 0.5);
		
				if ( newSplit.getDividerSize() > 6) {
					newSplit.setDividerSize( 6);
				}
		
				Object ui = newSplit.getUI();
				if (ui instanceof BasicSplitPaneUI) {
					((BasicSplitPaneUI) ui).getDivider().setBorder(null);
				}
		
				newSplit.setBorder( null);
				newSplit.setOneTouchExpandable( true);
		
				if ( right) {
					split.setRightComponent( newSplit);
				} else {
					split.setLeftComponent( newSplit);
				}
	
				bottomView.setSelected( true);
				view.setViewIcons();

				bottomView.disableChangeListener( false);
			} else if ( parentComponent instanceof JPanel) {
				JPanel panel = (JPanel)parentComponent;

				ExchangerTabbedView bottomView = new ExchangerTabbedView( this, tabbedView);
				bottomView.setScrollTabs( this.getProperties().isScrollDocumentTabs());
				bottomView.disableChangeListener( true);
				tabbedViews.addElement( bottomView);
	
				ExchangerView view = tabbedView.getSelectedView();
				tabbedView.remove( view);
		
				bottomView.add( view, view.getDocument().getName());
		
				JSplitPane newSplit =	new JSplitPane(	JSplitPane.HORIZONTAL_SPLIT, tabbedView, bottomView);
				newSplit.setResizeWeight( 0.5);
		
				if ( newSplit.getDividerSize() > 6) {
					newSplit.setDividerSize( 6);
				}
		
				Object ui = newSplit.getUI();
				if (ui instanceof BasicSplitPaneUI) {
					((BasicSplitPaneUI) ui).getDivider().setBorder(null);
				}
		
				newSplit.setBorder( null);
				newSplit.setOneTouchExpandable( true);
		
				panel.removeAll();
				panel.add( editorToolbarPanel, BorderLayout.NORTH);
				panel.add( newSplit, BorderLayout.CENTER);
	
				bottomView.setSelected( true);
				view.setViewIcons();
				bottomView.disableChangeListener( false);
			}

			tabbedView.disableChangeListener( false);
		}

		Vector views = getViews();
		if ( views != null) {
			for ( int i = 0; i < views.size(); i++) {
				ExchangerView view = (ExchangerView)views.elementAt(i);
				view.getEditor().scrollCursorToVisible();
			}
		}
	}

	public SelectElementAction getSelectElementAction() {
		if (selectElementAction == null) {
			selectElementAction = new SelectElementAction(this);
			this.getActions().add(selectElementAction);
		}

		return selectElementAction;
	}

	public SelectElementContentAction getSelectElementContentAction() {
		if (selectElementContentAction == null) {
			selectElementContentAction = new SelectElementContentAction(this);
			this.getActions().add(selectElementContentAction);
		}

		return selectElementContentAction;
	}

	public HighlightAction getHighlightAction() {
		if (highlightAction == null) {
			highlightAction = new HighlightAction(this);
			this.getActions().add(highlightAction);
		}

		return highlightAction;
	}

	public GotoStartTagAction getGotoStartTagAction() {
		if (gotoStartTagAction == null) {
			gotoStartTagAction = new GotoStartTagAction(this);
			this.getActions().add(gotoStartTagAction);
		}

		return gotoStartTagAction;
	}

	public GotoEndTagAction getGotoEndTagAction() {
		if (gotoEndTagAction == null) {
			gotoEndTagAction = new GotoEndTagAction(this);
			this.getActions().add(gotoEndTagAction);
		}

		return gotoEndTagAction;
	}
	
	public ToggleEmptyElementAction getToggleEmptyElementAction() {
		if ( toggleEmptyElementAction == null) {
			toggleEmptyElementAction = new ToggleEmptyElementAction(this);
			this.getActions().add(toggleEmptyElementAction);
		}

		return toggleEmptyElementAction;
	}

	public RenameElementAction getRenameElementAction() {
		if ( renameElementAction == null) {
			renameElementAction = new RenameElementAction(this);
			this.getActions().add(renameElementAction);
		}

		return renameElementAction;
	}

	public GotoNextAttributeValueAction getGotoNextAttributeValueAction() {
		if (gotoNextAttributeValueAction == null) {
			gotoNextAttributeValueAction = new GotoNextAttributeValueAction(this);
			this.getActions().add(gotoNextAttributeValueAction);
		}

		return gotoNextAttributeValueAction;
	}

	public GotoPreviousAttributeValueAction getGotoPreviousAttributeValueAction() {
		if (gotoPreviousAttributeValueAction == null) {
			gotoPreviousAttributeValueAction = new GotoPreviousAttributeValueAction(this);
			this.getActions().add(gotoPreviousAttributeValueAction);
		}

		return gotoPreviousAttributeValueAction;
	}

	public ToolsStripTextAction getToolsStripTextAction() {
		if (toolsStripTextAction == null) {
		    toolsStripTextAction = new ToolsStripTextAction(this,((Editor)getCurrent()),getProperties());
		    this.getActions().add(toolsStripTextAction);
		}

		return toolsStripTextAction;
	}
	
	public ToolsCapitalizeAction getToolsCapitalizeAction() {
		if (toolsCapitalizeAction == null) {
		    toolsCapitalizeAction = new ToolsCapitalizeAction(this,((Editor)getCurrent()),getProperties());
		    this.getActions().add(toolsCapitalizeAction);
		}

		return toolsCapitalizeAction;
	}
	
	public ToolsDeCapitalizeAction getToolsDeCapitalizeAction() {
		if (toolsDeCapitalizeAction == null) {
		    toolsDeCapitalizeAction = new ToolsDeCapitalizeAction(this,((Editor)getCurrent()),getProperties());
		    this.getActions().add(toolsDeCapitalizeAction);
		}

		return toolsDeCapitalizeAction;
	}
	
	public ToolsLowercaseAction getToolsLowercaseAction() {
		if (toolsLowercaseAction == null) {
		    toolsLowercaseAction = new ToolsLowercaseAction(this,((Editor)getCurrent()),getProperties());
		    this.getActions().add(toolsLowercaseAction);
		}

		return toolsLowercaseAction;
	}
	
	public ToolsUppercaseAction getToolsUppercaseAction() {
		if (toolsUppercaseAction == null) {
		    toolsUppercaseAction = new ToolsUppercaseAction(this,((Editor)getCurrent()),getProperties());
		    this.getActions().add(toolsUppercaseAction);
		}

		return toolsUppercaseAction;
	}
	
	public ToolsMoveNSToRootAction getToolsMoveNSToRootAction() {
		if (toolsMoveNSToRootAction == null) {
		    toolsMoveNSToRootAction = new ToolsMoveNSToRootAction(this,((Editor)getCurrent()));
		    this.getActions().add(toolsMoveNSToRootAction);
		}

		return toolsMoveNSToRootAction;
	}
	
	public ToolsMoveNSToFirstUsedAction getToolsMoveNSToFirstUsedAction() {
		if (toolsMoveNSToFirstUsedAction == null) {
		    toolsMoveNSToFirstUsedAction = new ToolsMoveNSToFirstUsedAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsMoveNSToFirstUsedAction);
		}

		return toolsMoveNSToFirstUsedAction;
	}
	
	public ToolsChangeNSPrefixAction getToolsChangeNSPrefixAction() {
		if (toolsChangeNSPrefixAction == null) {
		    toolsChangeNSPrefixAction = new ToolsChangeNSPrefixAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsChangeNSPrefixAction);
		}

		return toolsChangeNSPrefixAction;
	}
	
	public ToolsRenameNodeAction getToolsRenameNodeAction() {
		if (toolsRenameNodeAction == null) {
		    toolsRenameNodeAction = new ToolsRenameNodeAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsRenameNodeAction);
		}

		return toolsRenameNodeAction;
	}

	public ToolsRemoveNodeAction getToolsRemoveNodeAction() {
		if (toolsRemoveNodeAction == null) {
		    toolsRemoveNodeAction = new ToolsRemoveNodeAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsRemoveNodeAction);
		}

		return toolsRemoveNodeAction;
	}
	
	public ToolsAddNodeToNamespaceAction getToolsAddNodeToNamespaceAction() {
		if (toolsAddNodeToNamespaceAction == null) {
		    toolsAddNodeToNamespaceAction = new ToolsAddNodeToNamespaceAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsAddNodeToNamespaceAction);
		}

		return toolsAddNodeToNamespaceAction;
	}
	
	public ToolsSetNodeValueAction getToolsSetNodeValueAction() {
		if (toolsSetNodeValueAction == null) {
		    toolsSetNodeValueAction = new ToolsSetNodeValueAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsSetNodeValueAction);
		}

		return toolsSetNodeValueAction;
	}
	
	public ToolsAddNodeAction getToolsAddNodeAction() {
		if (toolsAddNodeAction == null) {
		    toolsAddNodeAction = new ToolsAddNodeAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsAddNodeAction);
		}

		return toolsAddNodeAction;
	}
	
	public ToolsRemoveUnusedNSAction getToolsRemoveUnusedNSAction() {
		if (toolsRemoveUnusedNSAction == null) {
		    toolsRemoveUnusedNSAction = new ToolsRemoveUnusedNSAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsRemoveUnusedNSAction);
		}

		return toolsRemoveUnusedNSAction;
	}
	
	public ToolsConvertNodeAction getToolsConvertNodeAction() {
		if (toolsConvertNodeAction == null) {
		    toolsConvertNodeAction = new ToolsConvertNodeAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsConvertNodeAction);
		}

		return toolsConvertNodeAction;
	}
	
	public ToolsSortNodeAction getToolsSortNodeAction() {
		if (toolsSortNodeAction == null) {
		    toolsSortNodeAction = new ToolsSortNodeAction(this, ((Editor)getCurrent()), getProperties());
		    this.getActions().add(toolsSortNodeAction);
		}

		return toolsSortNodeAction;
	}
	
	public CommentAction getCommentAction() {
		if (commentAction == null) {
			commentAction = new CommentAction(this);
			this.getActions().add(commentAction);
		}

		return commentAction;
	}

	public LockAction getLockAction() {
		if (lockAction == null) {
			lockAction = new LockAction(this);
			this.getActions().add(lockAction);
		}

		return lockAction;
	}

	public CDATAAction getCDATAAction() {
		if (cdataAction == null) {
			cdataAction = new CDATAAction(this);
			this.getActions().add(cdataAction);
		}

		return cdataAction;
	}

	public GotoAction getGotoAction() {
		if (gotoAction == null) {
			gotoAction = new GotoAction(this);
			this.getActions().add(gotoAction);
		}

		return gotoAction;
	}

	public ToggleBookmarkAction getToggleBookmarkAction() {
		if ( toggleBookmarkAction == null) {
			toggleBookmarkAction = new ToggleBookmarkAction(this);
			this.getActions().add(toggleBookmarkAction);
		}

		return toggleBookmarkAction;
	}

	public SelectBookmarkAction getSelectBookmarkAction() {
		if ( selectBookmarkAction == null) {
			selectBookmarkAction = new SelectBookmarkAction(this);
			this.getActions().add(selectBookmarkAction);
		}

		return selectBookmarkAction;
	}

	public SelectFragmentAction getSelectFragmentAction() {
		if ( selectFragmentAction == null) {
			selectFragmentAction = new SelectFragmentAction(this);
			this.getActions().add(selectFragmentAction);
		}

		return selectFragmentAction;
	}

	public ParseAction getParseAction() {
		if (parseAction == null) {
			parseAction = new ParseAction(this);
			this.getActions().add(parseAction);
		}

		return parseAction;
	}

	public ValidateAction getValidateAction() {
		if (validateAction == null) {
			validateAction = new ValidateAction(this);
			this.getActions().add(validateAction);
		}

		return validateAction;
	}

	public ValidateSchemaAction getValidateSchemaAction() {
		if (validateSchemaAction == null) {
			validateSchemaAction = new ValidateSchemaAction(this);
			this.getActions().add(validateSchemaAction);
		}

		return validateSchemaAction;
	}

	public ValidateDTDAction getValidateDTDAction() {
		if (validateDTDAction == null) {
			validateDTDAction = new ValidateDTDAction(this);
			this.getActions().add(validateDTDAction);
		}

		return validateDTDAction;
	}

	public ValidateRelaxNGAction getValidateRelaxNGAction() {
		if (validateRelaxNGAction == null) {
			validateRelaxNGAction = new ValidateRelaxNGAction(this);
			this.getActions().add(validateRelaxNGAction);
		}

		return validateRelaxNGAction;
	}

	public CleanUpHTMLAction getCleanUpHTMLAction() {
		if (cleanUpHTMLAction == null) {
			cleanUpHTMLAction = new CleanUpHTMLAction(this);
			this.getActions().add(cleanUpHTMLAction);
		}

		return cleanUpHTMLAction;
	}
	
	public SetXMLDeclarationAction getSetXMLDeclarationAction() {
		if (setXMLDeclarationAction == null) {
			setXMLDeclarationAction = new SetXMLDeclarationAction(this);
			this.getActions().add(setXMLDeclarationAction);
		}

		return setXMLDeclarationAction;
	}
	
	
	public SetXMLDoctypeAction getSetXMLDoctypeAction() {
		if (setXMLDoctypeAction == null) {
			setXMLDoctypeAction = new SetXMLDoctypeAction(this);
			this.getActions().add(setXMLDoctypeAction);
		}

		return setXMLDoctypeAction;
	}
	
	public ChangeDocumentAction getChangeDocumentAction() {
		if (changeDocumentAction == null) {
			changeDocumentAction = new ChangeDocumentAction(this);
			this.getActions().add(changeDocumentAction);
		}

		return changeDocumentAction;
	}
	
	public SetSchemaLocationAction getSetSchemaLocationAction() {
		if (setSchemaLocationAction == null) {
			setSchemaLocationAction = new SetSchemaLocationAction(this);
			this.getActions().add(setSchemaLocationAction);
		}

		return setSchemaLocationAction;
	}
	
	public ResolveXIncludesAction getResolveXIncludesAction() {
		if (resolveXIncludesAction == null) {
			resolveXIncludesAction = new ResolveXIncludesAction(this,getProperties());
			this.getActions().add(resolveXIncludesAction);
		}

		return resolveXIncludesAction;
	}
	
	public XDiffAction getXDiffAction() {
		if (xdiffAction == null) {
			xdiffAction = new XDiffAction(this,getProperties());
			this.getActions().add(xdiffAction);
		}

		return xdiffAction;
	}
	
	public OpenBrowserAction getOpenBrowserAction() {
		if (openBrowserAction == null) {
			openBrowserAction = new OpenBrowserAction(this);
			this.getActions().add(openBrowserAction);
		}

		return openBrowserAction;
	}

	public OpenSVGAction getOpenSVGAction() {
		if (openSVGAction == null) {
			openSVGAction = new OpenSVGAction(this);
			this.getActions().add(openSVGAction);
		}

		return openSVGAction;
	}

	public PrintAction getPrintAction() {
		if (printAction == null) {
			printAction = new PrintAction(this, getProperties());
			this.getActions().add(printAction);
		}

		return printAction;
	}

	public PageSetupAction getPageSetupAction() {
		if (pageSetupAction == null) {
			pageSetupAction = new PageSetupAction();
			this.getActions().add(pageSetupAction);
		}

		return pageSetupAction;
	}

	public InsertEntityAction getInsertEntityAction() {
		if (insertEntityAction == null) {
			insertEntityAction = new InsertEntityAction(this);
			this.getActions().add(insertEntityAction);
		}

		return insertEntityAction;
	}

	public SubstituteEntitiesAction getSubstituteEntitiesAction() {
		if (substituteEntitiesAction == null) {
			substituteEntitiesAction =
				new SubstituteEntitiesAction(this, getProperties());
			this.getActions().add(substituteEntitiesAction);
		}

		return substituteEntitiesAction;
	}

	public StripTagsAction getStripTagsAction() {
		if (stripTagsAction == null) {
			stripTagsAction = new StripTagsAction(this);
			this.getActions().add(stripTagsAction);
		}

		return stripTagsAction;
	}

	public SplitElementAction getSplitElementAction() {
		if (splitElementAction == null) {
			splitElementAction = new SplitElementAction(this);
			this.getActions().add(splitElementAction);
		}

		return splitElementAction;
	}

	public SubstituteCharactersAction getSubstituteCharactersAction() {
		if (substituteCharactersAction == null) {
			substituteCharactersAction =
				new SubstituteCharactersAction(this, getProperties());
			this.getActions().add(substituteCharactersAction);
		}

		return substituteCharactersAction;
	}

	public IndentAction getIndentAction() {
		if (indentAction == null) {
			indentAction = new IndentAction();
			this.getActions().add(indentAction);
		}

		return indentAction;
	}

	public UnindentAction getUnindentAction() {
		if (unindentAction == null) {
			unindentAction = new UnindentAction();
			this.getActions().add(unindentAction);
		}

		return unindentAction;
	}

	public FormatAction getFormatAction() {
		if (formatAction == null) {
			formatAction = new FormatAction( this, getProperties());
			this.getActions().add(formatAction);
		}

		return formatAction;
	}

	public CanonicalizeAction getCanonicalizeAction() {
		if (canonicalizeAction == null) {
			canonicalizeAction = new CanonicalizeAction(this,getProperties());
			this.getActions().add(canonicalizeAction);
		}

		return canonicalizeAction;
	}

	//	public void updateScenarioActions() {
	//		ExchangerDocument document = getDocument();
	//		Vector scenarios = properties.getScenarioProperties();

	//		if ( document != null && scenarios.size() > 0) {
	//			getExecuteScenarioAction().setEnabled( true);
	//			getDefaultScenarioAction().setEnabled( true);
	//			getOpenScenarioAction().setEnabled( true);
	//		} else {
	//			getDefaultScenarioAction().setEnabled( false);
	//			getExecuteScenarioAction().setEnabled( false);
	//			getOpenScenarioAction().setEnabled( false);
	//			getNewScenarioAction().setEnabled( false);
	//		}

	//		if ( document != null) {
	//			getNewScenarioAction().setEnabled( true);
	//		}
	//	}

	public void updateGrammarActions() {
		ExchangerDocument document = getDocument();
		Vector grammars = getProperties().getGrammarProperties();
		GrammarProperties grammar = getGrammar();

		if (document != null && document.isXML() && grammars.size() > 0) {
			getOpenGrammarAction().setEnabled(true);
			getNewGrammarAction().setEnabled(true);
			getGrammarPropertiesAction().setEnabled(grammar != null);
		} else {
			getOpenGrammarAction().setEnabled(false);
			getNewGrammarAction().setEnabled(false);
			getGrammarPropertiesAction().setEnabled(false);
		}

		//		if ( document != null) {
		//			getNewGrammarAction().setEnabled( true);
		//		}
	}

	private SetSchemaPropertiesAction getSetSchemaPropertiesAction() {
		if (setSchemaProperties == null) {
			setSchemaProperties = new SetSchemaPropertiesAction(this);
		}

		return setSchemaProperties;
	}

	private Action getCreateSchemaAction() {
		if (createSchema == null) {
			createSchema = new CreateSchemaAction(this);
		}

		return createSchema;
	}

	

	private CreateRequiredNodesAction getCreateRequiredNodesAction() {
		if (createRequiredNodes == null) {
			createRequiredNodes = new CreateRequiredNodesAction();
		}

		return createRequiredNodes;
	}

	private AddNodeAction getAddNodeAction() {
		if (addNode == null) {
			addNode = new AddNodeAction();
		}

		return addNode;
	}

	private DeleteNodeAction getDeleteNodeAction() {
		if (deleteNode == null) {
			deleteNode = new DeleteNodeAction();
		}

		return deleteNode;
	}

	public UndoAction getUndoAction() {
		if (undoAction == null) {
			undoAction = new UndoAction(this);
		}

		return undoAction;
	}

	public RedoAction getRedoAction() {
		if (redoAction == null) {
			redoAction = new RedoAction(this);
		}

		return redoAction;
	}

	public CopyAction getCopyAction() {
		if (copyAction == null) {
			copyAction = new CopyAction(this);
		}

		return copyAction;
	}

	public CutAction getCutAction() {
		if (cutAction == null) {
			cutAction = new CutAction(this);
		}

		return cutAction;
	}

	public PasteAction getPasteAction() {
		if (pasteAction == null) {
			pasteAction = new PasteAction(this);
		}

		return pasteAction;
	}

	public FindAction getFindAction() {
		if (findAction == null) {
			findAction = new FindAction(this, getProperties());
		}

		return findAction;
	}

	public ReplaceAction getReplaceAction() {
		if (replaceAction == null) {
			replaceAction = new ReplaceAction(this, getProperties());
		}

		return replaceAction;
	}

	public FindNextAction getFindNextAction() {
		if (findNextAction == null) {
			findNextAction = new FindNextAction(this, getProperties());
		}

		return findNextAction;
	}
	
	public FindInFilesAction getFindInFilesAction() {
		if (findInFilesAction == null) {
			findInFilesAction = new FindInFilesAction(this, getProperties());
		}

		return findInFilesAction;
	}

	public Action getOpenAction() {
		if (openDocument == null) {
			openDocument = new OpenAction(this, getProperties());
		}

		return openDocument;
	}

	public Action getOpenCurrentURLAction() {
		if ( openCurrentURL == null) {
			openCurrentURL = new OpenCurrentURLAction( this);
		}

		return openCurrentURL;
	}

	private Action getOpenRemoteDocumentAction() {
		if (openRemoteDocument == null) {
			openRemoteDocument = new OpenRemoteDocumentAction(this, getProperties());
		}

		return openRemoteDocument;
	}

	public Action getCloseAction() {
		if (closeDocument == null) {
			closeDocument = new CloseAction(this);
		}

		return closeDocument;
	}

	public SplitTabsHorizontallyAction getSplitTabsHorizontallyAction() {
		if (splitTabsHorizontally == null) {
			splitTabsHorizontally = new SplitTabsHorizontallyAction( this);
		}

		return splitTabsHorizontally;
	}

	public UnsplitTabsAction getUnsplitTabsAction() {
		if ( unsplitTabs == null) {
			unsplitTabs = new UnsplitTabsAction( this);
		}

		return unsplitTabs;
	}

	public SplitTabsVerticallyAction getSplitTabsVerticallyAction() {
		if (splitTabsVertically == null) {
			splitTabsVertically = new SplitTabsVerticallyAction( this);
		}

		return splitTabsVertically;
	}

	public CloseAllAction getCloseAllAction() {
		if (closeAllDocuments == null) {
			closeAllDocuments = new CloseAllAction(this);
		}

		return closeAllDocuments;
	}

	public ReloadAction getReloadAction() {
		if (reloadDocument == null) {
			reloadDocument = new ReloadAction(this);
		}

		return reloadDocument;
	}

	public SaveAction getSaveAction() {
		if (saveDocument == null) {
			saveDocument = new SaveAction(this);
		}

		return saveDocument;
	}

	public SaveAllAction getSaveAllAction() {
		if (saveAllDocuments == null) {
			saveAllDocuments = new SaveAllAction(this);
		}

		return saveAllDocuments;
	}

	public SaveAsAction getSaveAsAction() {
		if (saveDocumentAs == null) {
			saveDocumentAs = new SaveAsAction(this, getProperties());
		}

		return saveDocumentAs;
	}

	public SaveAsRemoteAction getSaveAsRemoteAction() {
		if (saveDocumentAsRemote == null) {
			saveDocumentAsRemote = new SaveAsRemoteAction(this, getProperties());
		}

		return saveDocumentAsRemote;
	}

	public SaveAsTemplateAction getSaveAsTemplateAction() {
		if (saveAsTemplate == null) {
			saveAsTemplate = new SaveAsTemplateAction(this, getProperties());
		}

		return saveAsTemplate;
	}

	private PreferencesAction getPreferencesAction() {
		if (preferencesAction == null) {
			preferencesAction = new PreferencesAction(this, getProperties());
		}

		return preferencesAction;
	}

	private Action getNewAction() {
		if (newDocument == null) {
			newDocument = new NewAction(this, getProperties());
		}

		return newDocument;
	}

	private Action getNewGrammarAction() {
		if (newGrammar == null) {
			newGrammar = new NewGrammarAction(this, getProperties());
		}

		return newGrammar;
	}

	private Action getConvertGrammarAction() {
		if (convertGrammar == null) {
			convertGrammar = new ConvertGrammarAction(this);
		}

		return convertGrammar;
	}

	private Action getConvertSVGAction() {
		if (convertSVG == null) {
			convertSVG = new ConvertSVGAction(this);
		}

		return convertSVG;
	}

	public SendSOAPAction getSendSOAPAction() {
		if (sendSOAP == null) {
			sendSOAP = new SendSOAPAction(this, getProperties());
		}

		return sendSOAP;
	}

	public AnalyseWSDLAction getAnalyseWSDLAction() {
		if (analyseWSDL == null) {
			analyseWSDL = new AnalyseWSDLAction(this, getProperties());
		}

		return analyseWSDL;
	}

	public Action getSignDocumentAction() {
		if (signDocument == null) {
			signDocument = new SignDocumentAction(this, getProperties());
		}

		return signDocument;
	}

	public Action getVerifySignatureAction() {
		if (verifySignature == null) {
			verifySignature = new VerifySignatureAction( this);
		}

		return verifySignature;
	}

	public SchemaInstanceGenerationAction getSchemaInstanceGenerationAction() {
		if (schemaInstance == null) {
		  schemaInstance = new SchemaInstanceGenerationAction(this, getProperties());
		}

		return schemaInstance;
	}

	//public GraphicalSchemaGenerationAction getGraphicalSchemaGenerationAction() {
	//	if (graphicalSchema == null) {
		  //graphicalSchema = new GraphicalSchemaGenerationAction(this, properties);
	//	}
	//
	//	return graphicalSchema;
	//}
	
	
	public ExecuteScriptAction getExecuteScriptAction() {
		if (executeScript == null) {
		  executeScript = new ExecuteScriptAction(this, getProperties());
		}

		return executeScript;
	}
	
	
	private Action getOpenGrammarAction() {
		if (openGrammar == null) {
			openGrammar = new OpenGrammarAction(this, getProperties());
		}

		return openGrammar;
	}

	private Action getGrammarPropertiesAction() {
		if (grammarProperties == null) {
			grammarProperties = new GrammarPropertiesAction(this, getProperties());
		}

		return grammarProperties;
	}

	private Action getManageGrammarAction() {
		if (manageGrammar == null) {
			manageGrammar = new ManageGrammarAction(this, getProperties());
		}

		return manageGrammar;
	}

	public Action getDefaultScenarioAction() {
		if (executeDefaultScenario == null) {
			executeDefaultScenario = new ExecuteDefaultScenarioAction(this, getProperties());
		}

		return executeDefaultScenario;
	}

	public ExecutePreviousScenarioAction getExecutePreviousScenarioAction() {
		if (executePreviousScenario == null) {
			executePreviousScenario = new ExecutePreviousScenarioAction( this);
		}

		return executePreviousScenario;
	}

	public Action getDebugScenarioAction() {
		if (debugScenario == null) {
			debugScenario =	new DebugScenarioAction(this, getProperties());
		}

		return debugScenario;
	}

	public ExecuteXSLTAction getExecuteAdvancedXSLTAction() {
		if (executeXSLT == null) {
			executeXSLT = new ExecuteXSLTAction(this);
		}

		return executeXSLT;
	}
	public ExecuteSimpleXSLTAction getExecuteSimpleXSLTAction() {
		if (executeSimpleXSLT == null) {
			executeSimpleXSLT = new ExecuteSimpleXSLTAction(this);
		}

		return executeSimpleXSLT;
	}


	
	public ExecuteFOAction getExecuteFOAction() {
		if (executeFO == null) {
			executeFO = new ExecuteFOAction( this);
		}

		return executeFO;
	}

	public ExecutePreviousFOAction getExecutePreviousFOAction() {
		if (executePreviousFO == null) {
			executePreviousFO = new ExecutePreviousFOAction( this);
		}

		return executePreviousFO;
	}

	public ExecuteXQueryAction getExecuteXQueryAction() {
		if (executeXQuery == null) {
			executeXQuery = new ExecuteXQueryAction( this);
		}

		return executeXQuery;
	}

	public ExecutePreviousXQueryAction getExecutePreviousXQueryAction() {
		if (executePreviousXQuery == null) {
			executePreviousXQuery = new ExecutePreviousXQueryAction( this);
		}

		return executePreviousXQuery;
	}

	public ExecutePreviousXSLTAction getExecutePreviousXSLTAction() {
		if (executePreviousXSLT == null) {
			executePreviousXSLT = new ExecutePreviousXSLTAction( this);
		}

		return executePreviousXSLT;
	}

	//	private Action getExecuteScenarioAction() {
	//		if ( executeScenario == null) {
	//			executeScenario = new ExecuteScenarioAction( this, properties);
	//		}
	//		
	//		return executeScenario;
	//	}

//	private Action getOpenScenarioAction() {
//		if (openScenario == null) {
//			openScenario = new OpenScenarioAction(this, properties);
//		}
//
//		return openScenario;
//	}
//
	private Action getManageScenarioAction() {
		if (manageScenario == null) {
			manageScenario = new ManageScenarioAction(this, getProperties());
		}

		return manageScenario;
	}

	private Action getManageTemplateAction() {
		if (manageTemplate == null) {
			manageTemplate = new ManageTemplateAction(this, getProperties());
		}

		return manageTemplate;
	}
	
	private Action getImportFromTextAction() {
		if (importFromTextAction == null) {
		    importFromTextAction = new ImportFromTextAction(this);
		}

		return importFromTextAction;
	}
	
	private Action getImportFromExcelAction() {
		if (importFromExcelAction == null) {
		    importFromExcelAction = new ImportFromExcelAction(this);
		}

		return importFromExcelAction;
	}
	
	private Action getImportFromDBTableAction() {
		if (importFromDBTableAction == null) {
		    importFromDBTableAction = new ImportFromDBTableAction(this,getProperties());
		}

		return importFromDBTableAction;
	}
	
	private Action getImportFromSQLXMLAction() {
		if (importFromSQLXMLAction == null) {
		    importFromSQLXMLAction = new ImportFromSQLXMLAction(this,getProperties());
		}

		return importFromSQLXMLAction;
	}

	public ExpandAllAction getExpandAllAction() {
		if (expandAll == null) {
			expandAll = new ExpandAllAction();
		}

		return expandAll;
	}

	public CollapseAllAction getCollapseAllAction() {
		if (collapseAll == null) {
			collapseAll = new CollapseAllAction();
		}

		return collapseAll;
	}

	private HelpContentsAction getHelpContentsAction() {
		if (helpContents == null) {
			helpContents = new HelpContentsAction(this, getHelpSet());
		}

		return helpContents;
	}

	private SynchroniseSelectionAction getSynchroniseSelectionAction() {
		if (syncSelection == null) {
			syncSelection = new SynchroniseSelectionAction(this);
		}

		return syncSelection;
	}

	private ToggleFullScreenAction getToggleFullScreenAction() {
		if (toggleFullScreen == null) {
			toggleFullScreen = new ToggleFullScreenAction(this);
		}

		return toggleFullScreen;
	}
	
	public CopyErrorListAction getCopyErrorListAction() {
		if (copyErrorListAction == null) {
			copyErrorListAction = new CopyErrorListAction(this);
		}

		return copyErrorListAction;
	}

	private void showAboutDialog() {
		AboutDialog dialog = getAboutDialog();
		//dialog.setVisible(true);
		dialog.show();
	}

	private AboutDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(this);
			aboutDialog.setLocationRelativeTo(this);
		}

		return aboutDialog;
	}

	public boolean isAutoSynchroniseSelection() {
		return getProperties().isAutoSyncSelection();
	}

	//	public void setSubstitutes( Vector substitutes) {	
	//		substitutionList.setSubstitutes( substitutes);
	//	}

	//	public RootSelectionDialog getRootSelectionDialog() {
	//		if ( rootDialog == null) {
	//			rootDialog = new RootSelectionDialog( this);
	//			rootDialog.setLocationRelativeTo( this);
	//		}
	//
	//		return rootDialog;
	//	}

	private JMenuItem createMenuItem(Action action) {
		
		// use XNGRMenuItem instead of JMenuItem, allows for emacs accelerator keys which are set in 
		// KeyPreferences
		XNGRMenuItem item = new XNGRMenuItem(action);

		return item;
	}
	
	/**
	 * creates the nmeu item, and adds a map of action names (defined in KeyPreferences) to menu 
	 * items
	 */
	public JMenuItem createMenuItem(Action action, String actionName) {
		
		if(actionName != null) {
			XNGRMenuItem temp = getMenuItem(actionName);
			if (temp != null)
			{
				return temp;
			}
			
			//  use XNGRMenuItem instead of JMenuItem, allows for emacs accelerator keys which are set in 
			// KeyPreferences
			XNGRMenuItem item = new XNGRMenuItem(action);
			menuItemMap.put(actionName,item);
			modeActionMap.put(actionName,action);
			
		
			return item;
		}
		else {
			return(createMenuItem(action));
		}
	}
	
	/**
	 * Returns the required MenuItem
	 *
	 * @return The JMenuItem
	 */
	public XNGRMenuItem getMenuItem(String actionName)
	{
		return (XNGRMenuItem)menuItemMap.get(actionName);
	}
	
	/**
	 * Returns the required JCheckBoxMenuItem
	 *
	 * @return The JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem getCheckBoxItem(String actionName)
	{
		return (JCheckBoxMenuItem)menuItemMap.get(actionName);
	}
	
	/**
	 * Returns the required Action
	 *
	 * @return The action
	 */
	public Action getModeAction(String actionName)
	{
		return (Action)modeActionMap.get(actionName);
	}
	
	//*********************************************************
	//	Grid Specific actions
	//*********************************************************
	
	/*public GridBridgeAddAttributeColumnAction getGridBridgeAddAttributeColumnAction() {
	    
	    if (gridBridgeAddAttributeColumnAction == null) {
	        gridBridgeAddAttributeColumnAction = new GridBridgeAddAttributeColumnAction();
		}

		return gridBridgeAddAttributeColumnAction;
	}
	
	public GridBridgeAddAttributeToSelectedAction getGridBridgeAddAttributeToSelectedAction() {
	    
	    if (gridBridgeAddAttributeToSelectedAction == null) {
	        gridBridgeAddAttributeToSelectedAction = new GridBridgeAddAttributeToSelectedAction();
		}

		return gridBridgeAddAttributeToSelectedAction;
	}
	
	public GridBridgeAddChildTableAction getGridBridgeAddChildTableAction() {
	    
	    if (gridBridgeAddChildTableAction == null) {
	        gridBridgeAddChildTableAction = new GridBridgeAddChildTableAction();
		}

		return gridBridgeAddChildTableAction;
	}
	
	public GridBridgeDeleteChildTableAction getGridBridgeDeleteChildTableAction() {
	    
	    if (gridBridgeDeleteChildTableAction == null) {
	        gridBridgeDeleteChildTableAction = new GridBridgeDeleteChildTableAction();
		}

		return gridBridgeDeleteChildTableAction;
	}
	
	public GridBridgeDeleteAttsAndTextAction getGridBridgeDeleteAttsAndTextAction() {
	    
	    if (gridBridgeDeleteAttsAndTextAction == null) {
	        gridBridgeDeleteAttsAndTextAction = new GridBridgeDeleteAttsAndTextAction();
		}

		return gridBridgeDeleteAttsAndTextAction;
	}
	
	public GridBridgeAddElementAfterAction getGridBridgeAddElementAfterAction() {
	    
	    if (gridBridgeAddElementAfterAction == null) {
	        gridBridgeAddElementAfterAction = new GridBridgeAddElementAfterAction();
		}

		return gridBridgeAddElementAfterAction;
	}
	
	public GridBridgeAddElementBeforeAction getGridBridgeAddElementBeforeAction() {
	    
	    if (gridBridgeAddElementBeforeAction == null) {
	        gridBridgeAddElementBeforeAction = new GridBridgeAddElementBeforeAction();
		}

		return gridBridgeAddElementBeforeAction;
	}
	
	public GridBridgeAddTextColumnAction getGridBridgeAddTextColumnAction() {
	    
	    if (gridBridgeAddTextColumnAction == null) {
	        gridBridgeAddTextColumnAction = new GridBridgeAddTextColumnAction();
		}

		return gridBridgeAddTextColumnAction;
	}
	
	public GridBridgeAddTextToSelectedAction getGridBridgeAddTextToSelectedAction() {
	    
	    if (gridBridgeAddTextToSelectedAction == null) {
	        gridBridgeAddTextToSelectedAction = new GridBridgeAddTextToSelectedAction();
		}

		return gridBridgeAddTextToSelectedAction;
	}
	
	public GridBridgeDeleteColumnAction getGridBridgeDeleteColumnAction() {
	    
	    if (gridBridgeDeleteColumnAction == null) {
	        gridBridgeDeleteColumnAction = new GridBridgeDeleteColumnAction();
		}

		return gridBridgeDeleteColumnAction;
	}
	
	public GridBridgeDeleteSelectedAttributeAction getGridBridgeDeleteSelectedAttributeAction() {
	    
	    if (gridBridgeDeleteSelectedAttributeAction == null) {
	        gridBridgeDeleteSelectedAttributeAction = new GridBridgeDeleteSelectedAttributeAction();
		}

		return gridBridgeDeleteSelectedAttributeAction;
	}
	
	public GridBridgeDeleteSelectedTextAction getGridBridgeDeleteSelectedTextAction() {
	    
	    if (gridBridgeDeleteSelectedTextAction == null) {
	        gridBridgeDeleteSelectedTextAction = new GridBridgeDeleteSelectedTextAction();
		}

		return gridBridgeDeleteSelectedTextAction;
	}
	
	public GridBridgeDeleteRowAction getGridBridgeDeleteElementAction() {
	    
	    if (gridBridgeDeleteElementAction == null) {
	        gridBridgeDeleteElementAction = new GridBridgeDeleteRowAction();
		}

		return gridBridgeDeleteElementAction;
	}
	
	public GridBridgeEditAttributeNameAction getGridBridgeEditAttributeNameAction() {
	    
	    if (gridBridgeEditAttributeNameAction == null) {
	        gridBridgeEditAttributeNameAction = new GridBridgeEditAttributeNameAction();
		}

		return gridBridgeEditAttributeNameAction;
	}
	
	public GridBridgeMoveRowDownAction getGridBridgeMoveRowDownAction() {
	    
	    if (gridBridgeMoveRowDownAction == null) {
	        gridBridgeMoveRowDownAction = new GridBridgeMoveRowDownAction();
		}

		return gridBridgeMoveRowDownAction;
	}
	
	public GridBridgeMoveRowUpAction getGridBridgeMoveRowUpAction() {
	    
	    if (gridBridgeMoveRowUpAction == null) {
	        gridBridgeMoveRowUpAction = new GridBridgeMoveRowUpAction();
		}

		return gridBridgeMoveRowUpAction;
	}
	
	public GridBridgeSortTableAscendingAction getGridBridgeSortTableAscendingAction() {
	    
	    if (gridBridgeSortTableAscendingAction == null) {
	        gridBridgeSortTableAscendingAction = new GridBridgeSortTableAscendingAction();
		}

		return gridBridgeSortTableAscendingAction;
	}
	
	public GridBridgeSortTableDescendingAction getGridBridgeSortTableDescendingAction() {
	    
	    if (gridBridgeSortTableDescendingAction == null) {
	        gridBridgeSortTableDescendingAction = new GridBridgeSortTableDescendingAction();
		}

		return gridBridgeSortTableDescendingAction;
	}
	
	public GridBridgeUnsortTableAction getGridBridgeUnsortTableAction() {
	    
	    if (gridBridgeUnsortTableAction == null) {
	        gridBridgeUnsortTableAction = new GridBridgeUnsortTableAction();
		}

		return gridBridgeUnsortTableAction;
	}
	
	public GridBridgeCopyShallowAction getGridBridgeCopyShallowAction() {
	    
	    if (gridBridgeCopyShallowAction == null) {
	        gridBridgeCopyShallowAction = new GridBridgeCopyShallowAction();
		}

		return gridBridgeCopyShallowAction;
	}
	
	public GridBridgePasteAsChildAction getGridBridgePasteAsChildAction() {
	    
	    if (gridBridgePasteAsChildAction == null) {
	        gridBridgePasteAsChildAction = new GridBridgePasteAsChildAction();
		}

		return gridBridgePasteAsChildAction;
	}
	
	public GridBridgePasteBeforeAction getGridBridgePasteBeforeAction() {
	    
	    if (gridBridgePasteBeforeAction == null) {
	        gridBridgePasteBeforeAction = new GridBridgePasteBeforeAction();
		}

		return gridBridgePasteBeforeAction;
	}
	
	public GridBridgePasteAfterAction getGridBridgePasteAfterAction() {
	    
	    if (gridBridgePasteAfterAction == null) {
	        gridBridgePasteAfterAction = new GridBridgePasteAfterAction();
		}

		return gridBridgePasteAfterAction;
	}
	
	public GridBridgeGotoChildTableAction getGridBridgeGotoChildTableAction() {
	    
	    if (gridBridgeGotoChildTableAction == null) {
	        gridBridgeGotoChildTableAction = new GridBridgeGotoChildTableAction();
		}

		return gridBridgeGotoChildTableAction;
	}
	
	public GridBridgeGotoParentTableAction getGridBridgeGotoParentTableAction() {
	    
	    if (gridBridgeGotoParentTableAction == null) {
	        gridBridgeGotoParentTableAction = new GridBridgeGotoParentTableAction();
		}

		return gridBridgeGotoParentTableAction;
	}
	
	public GridBridgeEditSelectedAttributeNameAction getGridBridgeEditSelectedAttributeNameAction() {
	    
	    if (gridBridgeEditSelectedAttributeNameAction == null) {
	        gridBridgeEditSelectedAttributeNameAction = new GridBridgeEditSelectedAttributeNameAction();
		}

		return gridBridgeEditSelectedAttributeNameAction;
	}
	
	public GridBridgeExpandRowAction getGridBridgeExpandRowAction() {
	    
	    if (gridBridgeExpandRowAction == null) {
	        gridBridgeExpandRowAction = new GridBridgeExpandRowAction();
		}

		return gridBridgeExpandRowAction;
	}
	
	public GridBridgeCollapseRowAction getGridBridgeCollapseRowAction() {
	    
	    if (gridBridgeCollapseRowAction == null) {
	        gridBridgeCollapseRowAction = new GridBridgeCollapseRowAction();
		}

		return gridBridgeCollapseRowAction;
	}
	
	public GridBridgeCollapseCurrentTableAction getGridBridgeCollapseCurrentTableAction() {
	    
	    if (gridBridgeCollapseCurrentTableAction == null) {
	        gridBridgeCollapseCurrentTableAction = new GridBridgeCollapseCurrentTableAction();
		}

		return gridBridgeCollapseCurrentTableAction;
	}
	
	public GridBridgeDeleteAction getGridBridgeDeleteAction() {
	    
	    if (gridBridgeDeleteAction == null) {
	        gridBridgeDeleteAction = new GridBridgeDeleteAction();
		}

		return gridBridgeDeleteAction;
	}
	*/
	/**
	 * Returns the JTabbed pane
	 *
	 * @return tab The JTabbedPane
	 */
	public JTabbedPane getTabbedPane()
	{
		return null;
	}
	
	public void setControllerIcon( Icon icon) {
		if ( icon == null) { 
			icon = getIcon( NAVIGATOR_ICON);
		}
		
		controllerTab.setIconAt(1, icon);
	}
	
	

	/*
	 * Gets an icon for the string.
	 */
	public ImageIcon getIcon(String path) {
		if (icons == null) {
			icons = new Hashtable();
		}

		ImageIcon icon = (ImageIcon) icons.get(path);

		if (icon == null) {
			if(DEBUG) System.out.println("ExchangerEditor::getIcon - path: "+path);
			if(DEBUG) System.out.println("ExchangerEditor::getIcon - XngrImageLoader.get(): "+XngrImageLoader.get());
			icon = XngrImageLoader.get().getImage(path);
			icons.put(path, icon);
		}

		return icon;
	}

	public boolean isSOAPDocument(ExchangerDocument doc) {
		XElement root = doc.getRoot();

		if (root != null) {
			if (root.getName().equals("Envelope")) {
				if (root.getNamespaceURI().equals( "http://schemas.xmlsoap.org/soap/envelope/")) {
					return true;
				} else if (root.getNamespaceURI().equals( "http://www.w3.org/2001/12/soap-envelope")) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isSchemaDocument() {
		return isSchemaDocument(getDocument());
	}

	public boolean isRelaxNGDocument() {
		return isRelaxNGDocument(getDocument());
	}

	private boolean isSchemaDocument(ExchangerDocument doc) {
		if (doc != null) {
			XElement root = doc.getRoot();

			if (root != null) {
				if (root.getName().equals( "schema")	&& root.getNamespaceURI().equals( "http://www.w3.org/2001/XMLSchema")) {
					return true;
				}
			}
		}

		return false;
	}
	private Node synchronisedNode = null;
	private boolean isEndTag = false;
	
	public void synchronise( ExchangerView source, Node node, boolean endTag, int y) {
		if ( synchroniseSplits.isSelected() && synchroniseSplits.isEnabled() && (synchronisedNode != node || endTag != isEndTag)&& source == currentView) {

			for ( int i = 0; i < tabbedViews.size(); i++) {
				ExchangerTabbedView tabbedView = (ExchangerTabbedView)tabbedViews.elementAt(i);
				
				if ( !tabbedView.isSelected()) {
					ExchangerView view = tabbedView.getSelectedView();
	
					if ( view != null) {
						view.setSelectedNode( node, endTag, view.getDocument().getDeclaredNamespaces(), y);
					}
				}
			}
			
			synchronisedNode = node;
			isEndTag = endTag;
		}
	}

	private boolean isRelaxNGDocument(ExchangerDocument doc) {
		if (doc != null) {
			XElement root = doc.getRoot();

			if (root != null) {
				if (root.getName().equals( "grammar") && root.getNamespaceURI().equals( "http://relaxng.org/ns/structure/1.0")) {
					return true;
				}
			}
		}

		return false;
	}
	
	private class MacOSApplicationAdapter implements ApplicationListener {
		private ExchangerEditor parent = null;

		public MacOSApplicationAdapter(ExchangerEditor parent) {
			this.parent = parent;
			Application app = new Application();
			app.setEnabledPreferencesMenu(true);
			app.addApplicationListener(this);
		}

		public void handleAbout(ApplicationEvent e) {
			e.setHandled(true);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					parent.showAboutDialog();
				}
			});
		}

		public void handleQuit(ApplicationEvent e) {
			e.setHandled(true);
			parent.exit();
		}

		public void handlePreferences(ApplicationEvent e) {
			e.setHandled(true);

			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getPreferencesAction().execute();
				}
			});
		}

		public void handleOpenApplication(ApplicationEvent e) {
			//			System.out.println(
			// "MacOSApplicationAdapter.handleOpenApplication(
			// [ApplicationEvent filename:"+e.getFilename()+"
			// handled:"+e.isHandled()+"])");
		}

		public void handleReOpenApplication(ApplicationEvent e) {
			//			System.out.println(
			// "MacOSApplicationAdapter.handleReOpenApplication(
			// [ApplicationEvent filename:"+e.getFilename()+"
			// handled:"+e.isHandled()+"])");
		}

		public void handleOpenFile(final ApplicationEvent e) {
			e.setHandled(true);

			if (ExchangerEditor.started) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							File file = new File(e.getFilename());
							open(XngrURLUtilities.getURLFromFile(file), null, false);
						} catch (MalformedURLException e) {
							// Should not happen!
							e.printStackTrace();
						}
					}
				});
			} else {
				try {
					if (e.getFilename() != null) {
						File file = new File(e.getFilename());
						ExchangerEditor.macFile = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file);
					}
				} catch (MalformedURLException x) {
					// Should not happen!
					x.printStackTrace();
				}
			}
		}

		public void handlePrintFile(ApplicationEvent e) {
			//			System.out.println( "MacOSApplicationAdapter.handlePrintFile(
			// [ApplicationEvent filename:"+e.getFilename()+"
			// handled:"+e.isHandled()+"])");
		}
	}
	
	public JMenu getEditMenu()
	{
		return editMenu;
	}
	
	class FragmentAction extends AbstractAction {
		private FragmentProperties fragment = null;
		private Editor editor = null;
		
		public FragmentAction( Editor editor, FragmentProperties fragment) {
			super( fragment.getName());
			
			this.fragment = fragment;
			this.editor = editor;

			setIcon();
//			putValue( ACCELERATOR_KEY, action.getValue( ACCELERATOR_KEY));
			putValue( SHORT_DESCRIPTION, fragment.getName());
		}
		
		private void setIcon() {
			ImageIcon icon = null;
			
			try {
				icon = XngrImageLoader.get().getImage( new URL( fragment.getIcon()));

				if ( icon.getIconHeight() != 16 || icon.getIconWidth() != 16) {
					icon = new ImageIcon( icon.getImage().getScaledInstance( 16, 16, Image.SCALE_SMOOTH));
				}
			} catch (Exception e) {
				icon = null;
			}
			
			if ( icon == null) {
				icon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/DefaultFragmentIcon.gif");
			}
			
			putValue( SMALL_ICON, icon);
		}

		public void actionPerformed( ActionEvent e) {
			editor.insertFragment( fragment.isBlock(), fragment.getContent());
			editor.setFocus();
		}
	}

    /**
     * 
     */
    public JPanel getDocumentViewButtonPanel() {

        return(documentViewButtonPanel);
        
    }
    
    public void setDocumentViewButtonPanel(JPanel panel) {
    	this.documentViewButtonPanel = panel;
    }
    
    public void updateDocumentViewButtonPanel() {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			getButtonContainer().removeAll();
    			getButtonContainer().add(getDocumentViewButtonPanel(), BorderLayout.SOUTH);
    			
    	    	getButtonContainer().invalidate();
    	    	getButtonContainer().repaint();
    	    	
    	    	getNorthPanel().invalidate();
    	    	getNorthPanel().revalidate();
    	    	getNorthPanel().repaint();
    		}
    	});
    	
    }
    
    public void updateDocumentViewMenu() {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			getViewMenu().remove(0);
    			getViewMenu().insert(getDocumentViewsMenu(), 0);
    			getViewMenu().invalidate();
    			getViewMenu().repaint();
    		}
    	});
    }
    
    /**
     * @return Returns the documentViewButtonGroup.
     */
    public ButtonGroup getDocumentViewButtonGroup() {

        return documentViewButtonGroup;
    }
    /**
     * @param documentViewButtonGroup The documentViewButtonGroup to set.
     */
    public void setDocumentViewButtonGroup(ButtonGroup group) {

        this.documentViewButtonGroup = group;
    }
    /**
     * @return Returns the toolbar.
     */
    public JToolBar getToolbar() {

        return toolbar;
    }
    /**
     * @param toolbar The toolbar to set.
     */
    public void setToolbar(JToolBar toolbar) {

        this.toolbar = toolbar;
    }

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(ConfigurationProperties properties) {

		this.properties = properties;
	}

	/**
	 * @return the properties
	 */
	public ConfigurationProperties getProperties() {

		return properties;
	}

	/**
	 * @param pluginViews the pluginViews to set
	 */
	public void setPluginViews(List pluginViews) {

		this.pluginViews = pluginViews;
	}

	/**
	 * @return the pluginViews
	 */
	public List getPluginViews() {

		if(pluginViews == null) {
			pluginViews = new ArrayList();
		}
		return pluginViews;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(List actions) {

		this.actions = actions;
	}

	/**
	 * @return the actions
	 */
	public List getActions() {

		if(actions == null) {
			actions = new ArrayList();
		}
		return actions;
	}
	
	public Action getAction(String identifier) {
		for(int cnt=0;cnt<getActions().size();++cnt) {
			Object obj = getActions().get(cnt);
			if(obj instanceof Action) {
				String shortDescription = (String) ((Action)obj).getValue(Action.NAME);
				if(shortDescription != null) {					
					if(shortDescription.equals(identifier)) {
						return((Action)obj);
					}
				}
			}
		}
		return(null);
	}

	/**
	 * @param tipsModel the tipsModel to set
	 */
	public void setTipsModel(LatestNewsModel tipsModel) {

		this.tipsModel = tipsModel;
	}

	/**
	 * @return the tipsModel
	 */
	public LatestNewsModel getTipsModel() {

		return tipsModel;
	}

	/**
	 * @param projectPanel the projectPanel to set
	 */
	public void setProjectPanel(Project projectPanel) {

		this.projectPanel = projectPanel;
	}

	/**
	 * @return the projectPanel
	 */
	public Project getProjectPanel() {

		return projectPanel;
	}

	public void setExtensionClassLoader(ExtensionClassLoader _extensionClassLoader) {
		extensionClassLoader = _extensionClassLoader;
	}

	public ExtensionClassLoader getExtensionClassLoader() {
		return extensionClassLoader;
	}
	
	public static ExtensionClassLoader getStaticExtensionClassLoader() {
		return extensionClassLoader;
	}
	
	public ClassLoader getClassLoader() {
		if(extensionClassLoader != null) {
			return(this.getExtensionClassLoader());
		}
		else {
			return(this.getClass().getClassLoader());
		}
	}

	public void setButtonContainer(JPanel buttonContainer) {
		this.buttonContainer = buttonContainer;
	}

	public JPanel getButtonContainer() {
		return buttonContainer;
	}

	public void setNorthPanel(JPanel northPanel) {
		this.northPanel = northPanel;
	}

	public JPanel getNorthPanel() {
		return northPanel;
	}

	public void setViewMenu(JMenu viewMenu) {
		this.viewMenu = viewMenu;
	}

	public JMenu getViewMenu() {
		return viewMenu;
	}

	public void setDocumentViewsMenu(JMenu documentViewsMenu) {
		this.documentViewsMenu = documentViewsMenu;
	}

	public JMenu getDocumentViewsMenu() {
		return documentViewsMenu;
	}

	public ExecuteSchematronAction getExecuteSchematronAction() {
		if (executeSchematron == null) {
			executeSchematron = new ExecuteSchematronAction(this);
		}

		return executeSchematron;
	}
}
