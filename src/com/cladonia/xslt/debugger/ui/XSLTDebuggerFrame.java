/*
 * $Id: XSLTDebuggerFrame.java,v 1.14 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.net.URL;
import java.util.Hashtable;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;


import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xml.transform.ScenarioUtilities;
import com.cladonia.xngreditor.AboutDialog;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.FileUtilities;
import com.cladonia.xngreditor.IconFactory;
import com.cladonia.xngreditor.Identity;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.StringUtilities;
import com.cladonia.xngreditor.XNGRMenuItem;
import com.cladonia.xngreditor.XngrImageLoader;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.properties.KeyPreferences;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xslt.debugger.ui.actions.CollapseAllAction;
import com.cladonia.xslt.debugger.ui.actions.CopyAction;
import com.cladonia.xslt.debugger.ui.actions.CutAction;
import com.cladonia.xslt.debugger.ui.actions.DisableTransformationBreakpointsAction;
import com.cladonia.xslt.debugger.ui.actions.EnableTransformationBreakpointsAction;
import com.cladonia.xslt.debugger.ui.actions.ExpandAllAction;
import com.cladonia.xslt.debugger.ui.actions.NewTransformationAction;
import com.cladonia.xslt.debugger.ui.actions.CloseTransformationAction;
import com.cladonia.xslt.debugger.ui.actions.CloseWindowAction;
import com.cladonia.xslt.debugger.ui.actions.PasteAction;
import com.cladonia.xslt.debugger.ui.actions.ReloadWindowAction;
import com.cladonia.xslt.debugger.ui.actions.ManageScenarioAction;
import com.cladonia.xslt.debugger.ui.actions.OpenInputAction;
import com.cladonia.xslt.debugger.ui.actions.OpenScenarioAction;
import com.cladonia.xslt.debugger.ui.actions.OpenStylesheetAction;
import com.cladonia.xslt.debugger.ui.actions.RemoveTransformationBreakpointsAction;
import com.cladonia.xslt.debugger.ui.actions.SaveAction;
import com.cladonia.xslt.debugger.ui.actions.SaveAsAction;
import com.cladonia.xslt.debugger.ui.actions.SaveScenarioAction;
import com.cladonia.xslt.debugger.ui.actions.FindAction;
import com.cladonia.xslt.debugger.ui.actions.FindNextAction;
import com.cladonia.xslt.debugger.ui.actions.GotoAction;
import com.cladonia.xslt.debugger.ui.actions.SetParametersAction;

import com.cladonia.xngreditor.XMLDocumentChooserDialog;
import com.cladonia.xml.transform.XSLTProcessorDialog;

/**
 * The XSLT debug frame.
 *
 * @version	$Revision: 1.14 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
public class XSLTDebuggerFrame extends JFrame {
	private static final ImageIcon ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/DebugScenario16.gif");

	public static final String XNGR_EDITOR_HOME = System.getProperty( "user.home")+File.separator+".xngr"+File.separator;
	public static final String PROPERTIES_FILE = ".xngr-editor.xml";
	
	private ManageScenarioAction manageScenarioAction = null;
	private NewTransformationAction newScenarioAction = null;
	private OpenScenarioAction openScenarioAction = null;
	private CloseTransformationAction closeTransformationAction = null;
	private CloseWindowAction closeWindowAction = null;
	private CollapseAllAction collapseAllAction = null;
	private ExpandAllAction expandAllAction = null;
	private ReloadWindowAction reloadWindowAction = null;
	private SaveScenarioAction saveScenarioAction = null;
	private OpenInputAction openInputAction = null;
	private FindAction findAction = null;
	private FindNextAction findNextAction = null;
	private GotoAction gotoAction = null;
	private OpenStylesheetAction openStylesheetAction = null;
	private SetParametersAction setParametersAction = null;
	
	private SaveAction saveAction = null;
	private SaveAsAction saveAsAction = null;
	
	private EnableTransformationBreakpointsAction enableBreakpointsAction = null;
	private DisableTransformationBreakpointsAction disableBreakpointsAction = null;
	private RemoveTransformationBreakpointsAction removeBreakpointsAction = null;

	private AboutDialog aboutDialog = null;
	private HelpSet helpset = null;
	private ExchangerEditor editor = null;
	
	private XSLTDebuggerPane debugPane = null;
	private XSLTTransformation transformation = null;
	private TransformationPropertiesDialog transformationDialog = null;
	private ConfigurationProperties properties = null;
	
	private JCheckBoxMenuItem showStyleLinenumberMarginMenu = null;
	private JCheckBoxMenuItem showStyleOverviewMarginMenu = null;
	private JCheckBoxMenuItem showStyleFoldingMarginMenu = null;
	private JCheckBoxMenuItem wrapStyleMenu = null;

	private JCheckBoxMenuItem showInputLinenumberMarginMenu = null;
	private JCheckBoxMenuItem showInputOverviewMarginMenu = null;
	private JCheckBoxMenuItem showInputFoldingMarginMenu = null;
	private JCheckBoxMenuItem wrapInputMenu = null;

	private JCheckBoxMenuItem showOutputLinenumberMarginMenu = null;
	private JCheckBoxMenuItem wrapOutputMenu = null;

	private JCheckBoxMenuItem openInput = null;
	private JCheckBoxMenuItem enableTrace = null;
	private JCheckBoxMenuItem redirectOutput = null;
	
	private JRadioButtonMenuItem xalanButton = null;
	private JRadioButtonMenuItem saxon1Button = null;
	private JRadioButtonMenuItem saxon2Button = null;
	
	private Hashtable menuItemMap = new Hashtable();

	
	private XMLDocumentChooserDialog chooserXSL = null;
	private XMLDocumentChooserDialog chooserXML = null;
	private XSLTProcessorDialog processorDialog = null;

	private CutAction cutAction;

	private CopyAction copyAction;

	private PasteAction pasteAction;

	public XSLTDebuggerFrame( ConfigurationProperties props, ExchangerEditor parent) {
		super( "Exchanger XSLT Debugger");
		
		setIconImage( ICON.getImage());

		this.properties = props;
		this.editor = parent;
		
		ScenarioDebugUtilities.init( this, props);
		
		debugPane = new XSLTDebuggerPane( this, props);

		debugPane.setHorizontalSplitLocation( properties.getDebuggerProperties().getHorizontalSplitLocation());
		debugPane.setVerticalSplitLocation( properties.getDebuggerProperties().getVerticalSplitLocation());
		debugPane.setDashboardSplitLocation( properties.getDebuggerProperties().getDashboardSplitLocation());
		debugPane.setVariablesSplitLocation( properties.getDebuggerProperties().getVariablesSplitLocation());
		debugPane.setBreakpointsSplitLocation( properties.getDebuggerProperties().getBreakpointsSplitLocation());
		debugPane.setTraceSplitLocation( properties.getDebuggerProperties().getTraceSplitLocation());
		
		setJMenuBar( createMenuBar());
		
		JPanel contentPane = new JPanel( new BorderLayout());
		contentPane.add( createToolBar(), BorderLayout.NORTH);
		contentPane.add( debugPane, BorderLayout.CENTER);

		getContentPane().add( contentPane, BorderLayout.CENTER);

		if(properties.getDebuggerProperties().isWindowMaximised() == true) {
			
			this.setExtendedState(this.getExtendedState() | MAXIMIZED_BOTH);
		}
		else {
			//set it to a specific size
			setSize( properties.getDebuggerProperties().getDimension());
		}
		
		setLocation( properties.getDebuggerProperties().getPosition());
		
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);
		
		/*this.addWindowStateListener(new WindowStateListener() {

			public void windowStateChanged(WindowEvent e) {
				// TODO Auto-generated method stub
				if(XSLTDebuggerFrame.this.getExtendedState() == MAXIMIZED_BOTH) {
					
					XSLTDebuggerFrame.this.properties.getDebuggerProperties().setWindowMaximised(true);
				}
				else if(XSLTDebuggerFrame.this.getExtendedState() == ICONIFIED) {
					XSLTDebuggerFrame.this.properties.getDebuggerProperties().setWindowMaximised(false);
					setSize( properties.getDebuggerProperties().getDimension());
				}
				else {
					XSLTDebuggerFrame.this.properties.getDebuggerProperties().setWindowMaximised(false);
					setSize( properties.getDebuggerProperties().getDimension());
				}				
			}
			
		});*/
		
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
	            	getProperties().getDebuggerProperties().setWindowMaximised(true);
	            	
	            } else if ((oldState & Frame.MAXIMIZED_BOTH) != 0
	                && (newState & Frame.MAXIMIZED_BOTH) == 0) {
	            	if(DEBUG) System.out.println("Frame was minimized");
	            	setSize(getProperties().getDebuggerProperties().getDimension());
	            }
			}
		});
		
		addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				debugPane.saveViewsToDisk();
				exit();
			}
		});
	}
	
	private JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar();
		toolbar.setRollover( true);
		toolbar.setFloatable( false);
		
		toolbar.add( getSaveAction()).setMnemonic(0);
		toolbar.add( getSaveAsAction()).setMnemonic(0);
		toolbar.addSeparator();
		
		toolbar.add( getCutAction()).setMnemonic(0);
		toolbar.add( getCopyAction()).setMnemonic(0);
		toolbar.add( getPasteAction()).setMnemonic(0);
		toolbar.addSeparator();
		
		toolbar.add( debugPane.getContinueAction()).setMnemonic(0);
		toolbar.add( debugPane.getContinueToEndAction()).setMnemonic(0);
		toolbar.add( debugPane.getPauseAction()).setMnemonic(0);
		toolbar.add( debugPane.getStopAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add( debugPane.getStepIntoAction()).setMnemonic(0);
		toolbar.add( debugPane.getStepOutAction()).setMnemonic(0);
		toolbar.add( debugPane.getStepOverAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add( getFindAction()).setMnemonic(0);
		toolbar.add( getFindNextAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add( getGotoAction()).setMnemonic(0);
		toolbar.addSeparator();
		toolbar.add( getCollapseAllAction()).setMnemonic(0);
		toolbar.add( getExpandAllAction()).setMnemonic(0);

		return toolbar;
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menubar = new JMenuBar();
		
		InputMap im = menubar.getInputMap( JMenuBar.WHEN_IN_FOCUSED_WINDOW);        
		KeyStroke f10 = KeyStroke.getKeyStroke("F10");    
		InputMap pim = im;    
		
		while ( pim != null) {      
			pim.remove( f10);      
			pim = pim.getParent();    
		}    // rebind backspace    

		JMenu fileMenu = new JMenu( "File");
		fileMenu.setMnemonic( 'F');
		fileMenu.add( createMenuItem(getNewScenarioAction(),KeyPreferences.DEBUGGER_NEW_TRANSFORMATION_ACTION));

		JMenu openMenu = new JMenu( "Open");
		openMenu.setMnemonic( 'O');
		
		fileMenu.add( openMenu);
		
		openMenu.add( createMenuItem(getOpenScenarioAction(),KeyPreferences.DEBUGGER_OPEN_SCENARIO_ACTION));
		openMenu.addSeparator();
		openMenu.add( createMenuItem(getOpenInputAction(),KeyPreferences.DEBUGGER_OPEN_INPUT_ACTION));
		getOpenInputAction().setEnabled( false);
		openMenu.add( createMenuItem(getOpenStylesheetAction(),KeyPreferences.DEBUGGER_OPEN_STYLESHEET_ACTION));
		getOpenStylesheetAction().setEnabled( false);
		fileMenu.add( createMenuItem(getCloseWindowAction(),KeyPreferences.DEBUGGER_CLOSE_ACTION));
		getCloseWindowAction().setEnabled( false);
		fileMenu.add( createMenuItem(getCloseTransformationAction(),KeyPreferences.DEBUGGER_CLOSE_TRANSFORMATION_ACTION));
		getCloseTransformationAction().setEnabled( false);
		fileMenu.addSeparator();
		fileMenu.add(createMenuItem(getSaveAction(),KeyPreferences.SAVE_ACTION));
		fileMenu.add(createMenuItem(getSaveAsAction(),KeyPreferences.SAVE_AS_ACTION));
		fileMenu.add( createMenuItem(getReloadWindowAction(),KeyPreferences.DEBUGGER_RELOAD_ACTION));
		getReloadWindowAction().setEnabled( false);
		fileMenu.addSeparator();
		fileMenu.add( createMenuItem(getSaveScenarioAction(),KeyPreferences.DEBUGGER_SAVE_AS_SCENARIO_ACTION));
		getSaveScenarioAction().setEnabled( false);
		fileMenu.addSeparator();
		

		XNGRMenuItem exitItem = new XNGRMenuItem( "Exit", 'x');
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processWindowEvent( new WindowEvent( XSLTDebuggerFrame.this, WindowEvent.WINDOW_CLOSING));
			}
		});

		menuItemMap.put(KeyPreferences.DEBUGGER_EXIT_ACTION, exitItem);
		fileMenu.add( exitItem);

		GUIUtilities.alignMenu( fileMenu);
		menubar.add( fileMenu);

		// >>> Edit Menu
		JMenu editMenu = new JMenu( "Edit");
		editMenu.setMnemonic( 'E');
		
		editMenu.add(createMenuItem(getCutAction(), KeyPreferences.CUT_ACTION));
		editMenu.add( createMenuItem(getCopyAction(),KeyPreferences.COPY_ACTION));
		editMenu.add( createMenuItem(getPasteAction(),KeyPreferences.PASTE_ACTION));

		editMenu.addSeparator();
		
		editMenu.add( createMenuItem(getFindAction(),KeyPreferences.DEBUGGER_FIND_ACTION));
		editMenu.add( createMenuItem(getFindNextAction(),KeyPreferences.DEBUGGER_FIND_NEXT_ACTION));
		editMenu.addSeparator();
		editMenu.add( createMenuItem(getGotoAction(),KeyPreferences.DEBUGGER_GOTO_ACTION));

		GUIUtilities.alignMenu( editMenu);
		
		//TODO GMCG fix this
		menubar.add( editMenu);

		// <<< Edit Menu

		// >>> View Menu
		JMenu viewMenu = new JMenu( "View");
		viewMenu.setMnemonic( 'V');

		viewMenu.add( createMenuItem(getCollapseAllAction(),KeyPreferences.DEBUGGER_COLLAPSE_ALL_ACTION));
		viewMenu.add( createMenuItem(getExpandAllAction(),KeyPreferences.DEBUGGER_EXPAND_ALL_ACTION));

		viewMenu.addSeparator();

		JMenu stylesheet = new JMenu( "Stylesheet");
		viewMenu.add( stylesheet);
		
		showStyleLinenumberMarginMenu = new JCheckBoxMenuItem( "Show Linenumber Margin");
		showStyleLinenumberMarginMenu.setSelected( properties.getDebuggerProperties().showStyleLinenumberMargin());
		showStyleLinenumberMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowStyleLinenumberMargin( showStyleLinenumberMarginMenu.isSelected());
				debugPane.getXSLTView().setShowLinenumberMargin( showStyleLinenumberMarginMenu.isSelected());
			}
		});

		stylesheet.add( showStyleLinenumberMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_LINE_NUMBER_ACTION,showStyleLinenumberMarginMenu);
		

		showStyleOverviewMarginMenu = new JCheckBoxMenuItem( "Show Overview Margin");
		showStyleOverviewMarginMenu.setSelected( properties.getDebuggerProperties().showStyleOverviewMargin());
		showStyleOverviewMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowStyleOverviewMargin( showStyleOverviewMarginMenu.isSelected());
				debugPane.getXSLTView().setShowOverviewMargin( showStyleOverviewMarginMenu.isSelected());
			}
		});

		stylesheet.add( showStyleOverviewMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_OVERVIEW_ACTION,showStyleOverviewMarginMenu);

		showStyleFoldingMarginMenu = new JCheckBoxMenuItem( "Show Folding Margin");
		showStyleFoldingMarginMenu.setSelected( properties.getDebuggerProperties().showStyleFoldingMargin());
		showStyleFoldingMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowStyleFoldingMargin( showStyleFoldingMarginMenu.isSelected());
				debugPane.getXSLTView().setShowFoldingMargin( showStyleFoldingMarginMenu.isSelected());
			}
		});

		stylesheet.add( showStyleFoldingMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_STYLESHEET_SHOW_FOLDING_ACTION,showStyleFoldingMarginMenu);

		stylesheet.addSeparator();

		wrapStyleMenu = new JCheckBoxMenuItem( "Soft Wrapping");
		wrapStyleMenu.setSelected( properties.getDebuggerProperties().isWrapStyle());
		wrapStyleMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setWrapStyle( wrapStyleMenu.isSelected());
				debugPane.getXSLTView().setWrapping( wrapStyleMenu.isSelected());
			}
		});

		stylesheet.add( wrapStyleMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_STYLESHEET_SOFT_WRAPPING_ACTION,wrapStyleMenu);

		JMenu input = new JMenu( "Input");
		viewMenu.add( input);
		
		showInputLinenumberMarginMenu = new JCheckBoxMenuItem( "Show Linenumber Margin");
		showInputLinenumberMarginMenu.setSelected( properties.getDebuggerProperties().showInputLinenumberMargin());
		showInputLinenumberMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowInputLinenumberMargin( showInputLinenumberMarginMenu.isSelected());
				debugPane.getInputView().setShowLinenumberMargin( showInputLinenumberMarginMenu.isSelected());
			}
		});

		input.add( showInputLinenumberMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_INPUT_SHOW_LINE_NUMBER_ACTION,showInputLinenumberMarginMenu);

		showInputOverviewMarginMenu = new JCheckBoxMenuItem( "Show Overview Margin");
		showInputOverviewMarginMenu.setSelected( properties.getDebuggerProperties().showInputOverviewMargin());
		showInputOverviewMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowInputOverviewMargin( showInputOverviewMarginMenu.isSelected());
				debugPane.getInputView().setShowOverviewMargin( showInputOverviewMarginMenu.isSelected());
			}
		});

		input.add( showInputOverviewMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_INPUT_SHOW_OVERVIEW_ACTION,showInputOverviewMarginMenu);

		showInputFoldingMarginMenu = new JCheckBoxMenuItem( "Show Folding Margin");
		showInputFoldingMarginMenu.setSelected( properties.getDebuggerProperties().showInputFoldingMargin());
		showInputFoldingMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowInputFoldingMargin( showInputFoldingMarginMenu.isSelected());
				debugPane.getInputView().setShowFoldingMargin( showInputFoldingMarginMenu.isSelected());
			}
		});

		input.add( showInputFoldingMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_INPUT_SHOW_FOLDING_ACTION,showInputFoldingMarginMenu);

		input.addSeparator();

		wrapInputMenu = new JCheckBoxMenuItem( "Soft Wrapping");
		wrapInputMenu.setSelected( properties.getDebuggerProperties().isWrapInput());
		wrapInputMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setWrapInput( wrapInputMenu.isSelected());
				debugPane.getInputView().setWrapping( wrapInputMenu.isSelected());
			}
		});

		input.add( wrapInputMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_INPUT_SOFT_WRAPPING_ACTION,wrapInputMenu);

		JMenu output = new JMenu( "Output");
		viewMenu.add( output);

		showOutputLinenumberMarginMenu = new JCheckBoxMenuItem( "Show Linenumber Margin");
		showOutputLinenumberMarginMenu.setSelected( properties.getDebuggerProperties().showOutputLinenumberMargin());
		showOutputLinenumberMarginMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setShowOutputLinenumberMargin( showOutputLinenumberMarginMenu.isSelected());
				debugPane.getOutputView().setShowLinenumberMargin( showOutputLinenumberMarginMenu.isSelected());
			}
		});

		output.add( showOutputLinenumberMarginMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_OUTPUT_SHOW_LINE_NUMBER_ACTION,showOutputLinenumberMarginMenu);

		output.addSeparator();

		wrapOutputMenu = new JCheckBoxMenuItem( "Soft Wrapping");
		wrapOutputMenu.setSelected( properties.getDebuggerProperties().isWrapOutput());
		wrapOutputMenu.addItemListener( new ItemListener(){
			public void itemStateChanged( ItemEvent e) {
				properties.getDebuggerProperties().setWrapOutput( wrapOutputMenu.isSelected());
				debugPane.getOutputView().setWrapping( wrapOutputMenu.isSelected());
			}
		});

		output.add( wrapOutputMenu);
		menuItemMap.put(KeyPreferences.DEBUGGER_OUTPUT_SOFT_WRAPPING_ACTION,wrapOutputMenu);

		GUIUtilities.alignMenu( viewMenu);
		menubar.add( viewMenu);
		// <<< View Menu

		// >>> Debug Menu
		JMenu debugMenu = new JMenu( "Debug");
		debugMenu.setMnemonic( 'D');
		debugMenu.add( createMenuItem(debugPane.getContinueAction(),KeyPreferences.DEBUGGER_START_ACTION));
		debugMenu.add( createMenuItem(debugPane.getContinueToEndAction(),KeyPreferences.DEBUGGER_RUN_END_ACTION));
		debugMenu.add( createMenuItem(debugPane.getPauseAction(),KeyPreferences.DEBUGGER_PAUSE_ACTION));
		debugMenu.add( createMenuItem(debugPane.getStopAction(),KeyPreferences.DEBUGGER_STOP_ACTION));
		debugMenu.addSeparator();
		debugMenu.add( createMenuItem(debugPane.getStepIntoAction(),KeyPreferences.DEBUGGER_STEP_INTO_ACTION));
		debugMenu.add( createMenuItem(debugPane.getStepOverAction(),KeyPreferences.DEBUGGER_STEP_OVER_ACTION));
		debugMenu.add( createMenuItem(debugPane.getStepOutAction(),KeyPreferences.DEBUGGER_STEP_OUT_ACTION));

		GUIUtilities.alignMenu( debugMenu);
		menubar.add( debugMenu);

		// <<< Debug Menu

		// >>> Transformation Menu
		JMenu transformationMenu = new JMenu( "Transformation");
		transformationMenu.setMnemonic( 'T');

		openInput = new JCheckBoxMenuItem( "Automatically Open Input", properties.getDebuggerProperties().isAutomaticOpenInput());
		openInput.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				properties.getDebuggerProperties().setAutomaticOpenInput( openInput.isSelected());
			}
		});
		transformationMenu.add( openInput);
		menuItemMap.put(KeyPreferences.DEBUGGER_AUTO_OPEN_INPUT_ACTION,openInput);

		enableTrace = new JCheckBoxMenuItem( "Enable Tracing", properties.getDebuggerProperties().isTracingEnabled());
		enableTrace.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				properties.getDebuggerProperties().setTracingEnabled( enableTrace.isSelected());
			}
		});
//		enableTrace.setEnabled( false);
		transformationMenu.add( enableTrace);
		menuItemMap.put(KeyPreferences.DEBUGGER_ENABLE_TRACING_ACTION,enableTrace);

		redirectOutput = new JCheckBoxMenuItem( "Redirect Output", properties.getDebuggerProperties().isRedirectOutput());
		redirectOutput.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				properties.getDebuggerProperties().setRedirectOutput( redirectOutput.isSelected());
			}
		});
//		enableTrace.setEnabled( false);
		transformationMenu.add( redirectOutput);
		menuItemMap.put(KeyPreferences.DEBUGGER_REDIRECT_OUTPUT_ACTION,redirectOutput);

		transformationMenu.addSeparator();

		xalanButton = new JRadioButtonMenuItem( "Xalan", true);
		xalanButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				if ( xalanButton.isSelected()) {
					transformation.setProcessor( ScenarioProperties.PROCESSOR_XALAN);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_XALAN);
				} else if ( saxon2Button.isSelected()){
					transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT2);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_SAXON2);
				} else {
					transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT1);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_SAXON1);
				}
				
			}
		});
		xalanButton.setEnabled( false);
		transformationMenu.add( xalanButton);

		saxon1Button = new JRadioButtonMenuItem( "Saxon (XSLT 1.*)", true);
		saxon1Button.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				if ( xalanButton.isSelected()) {
					transformation.setProcessor( ScenarioProperties.PROCESSOR_XALAN);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_XALAN);
				} else if ( saxon2Button.isSelected()){
					transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT2);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_SAXON2);
				} else {
					transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT1);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_SAXON1);
				}
			}
		});
		transformationMenu.add( saxon1Button);
		saxon1Button.setEnabled( false);

		
		
		saxon2Button = new JRadioButtonMenuItem( "Saxon (XSLT 2.*)", true);
		saxon2Button.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				if ( xalanButton.isSelected()) {
					transformation.setProcessor( ScenarioProperties.PROCESSOR_XALAN);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_XALAN);
				} else if ( saxon1Button.isSelected()){
					transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT1);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_SAXON1);
				}else {
					transformation.setProcessor( ScenarioProperties.PROCESSOR_SAXON_XSLT2);
					debugPane.getStatusbar().setProcessor( Statusbar.PROCESSOR_SAXON2);
				}
			}
		});
		transformationMenu.add( saxon2Button);
		saxon2Button.setEnabled( false);

		
		
		ButtonGroup group = new ButtonGroup();
		group.add( saxon1Button);
		group.add( xalanButton);
		group.add( saxon2Button);
		
		transformationMenu.addSeparator();
		
		transformationMenu.add( createMenuItem(getSetParametersAction(),KeyPreferences.DEBUGGER_SET_PARAMETERS_ACTION));
		getSetParametersAction().setEnabled( false);

		transformationMenu.addSeparator();

		transformationMenu.add( createMenuItem(getDisableTransformationBreakpointsAction(),KeyPreferences.DEBUGGER_DISABLE_ALL_BREAKPOINTS_ACTION));
		getDisableTransformationBreakpointsAction().setEnabled( false);
		transformationMenu.add( createMenuItem(getEnableTransformationBreakpointsAction(),KeyPreferences.DEBUGGER_ENABLE_ALL_BREAKPOINTS_ACTION));
		getEnableTransformationBreakpointsAction().setEnabled( false);
		
		transformationMenu.add( createMenuItem(getRemoveTransformationBreakpointsAction(),KeyPreferences.DEBUGGER_REMOVE_ALL_BREAKPOINTS_ACTION));
		getRemoveTransformationBreakpointsAction().setEnabled( false);

		GUIUtilities.alignMenu( transformationMenu);
		menubar.add( transformationMenu);

		// <<< View Menu

		// >>> Help Menu
		JMenu helpMenu = new JMenu( "Help");
		helpMenu.setMnemonic( 'H');

		JMenuItem aboutItem = new JMenuItem( "About", 'A');
		aboutItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e) {
				showAboutDialog();
			}
		});
		
		JMenuItem contentsItem = new JMenuItem( "Contents", 'C');
		
		HelpBroker broker = createHelpBroker();
		if ( broker != null) {
			broker.enableHelpOnButton( contentsItem, "exchanger.intro", getHelpSet());
			broker.setCurrentView( "TOC");
		} else {
			contentsItem.setEnabled( false);
		}

		JMenuItem indexItem = new JMenuItem( "Index", 'I');
		
		broker = createHelpBroker();
		if ( broker != null) {
			broker.enableHelpOnButton( indexItem, "exchanger.intro", getHelpSet());
			broker.setCurrentView( "Index");
		} else {
			indexItem.setEnabled( false);
		}

		JMenuItem searchItem = new JMenuItem( "Search", 'S');
		
		broker = createHelpBroker();
		if ( broker != null) {
			broker.enableHelpOnButton( searchItem, "exchanger.intro", getHelpSet());
			broker.setCurrentView( "Search");
		} else {
			searchItem.setEnabled( false);
		}

		JMenuItem gettingStartedItem = new JMenuItem( "Getting Started", 'G');
		
		broker = createHelpBroker();
		if ( broker != null) {
			broker.enableHelpOnButton( gettingStartedItem, "start.intro", getHelpSet());
			broker.setCurrentView( "TOC");
		} else {
			gettingStartedItem.setEnabled( false);
		}
		helpMenu.add( contentsItem);
		helpMenu.add( indexItem);
		helpMenu.add( searchItem);

		helpMenu.addSeparator();
		helpMenu.add( gettingStartedItem);

		helpMenu.addSeparator();
		helpMenu.add( aboutItem);
		GUIUtilities.alignMenu( helpMenu);
		menubar.add( helpMenu);
		// <<< Help Menu
		
		return menubar;

	}
	
	private void showAboutDialog() {
		AboutDialog dialog = getAboutDialog();
		dialog.setVisible(true);
	}

	private AboutDialog getAboutDialog() {
		if (aboutDialog == null) {
			aboutDialog = new AboutDialog(this);
			aboutDialog.setLocationRelativeTo(this);
		}

		return aboutDialog;
	}
	
	public void updatePreferences() {
		debugPane.updatePreferences();
	}

	private HelpSet getHelpSet() {
		if (helpset == null) {
			try {
				URL url =
					HelpSet.findHelpSet(
						this.getClass().getClassLoader(),
						"ExchangerHelp.hs");
				//			    System.out.println( "findHelpSet url=" + url);
				String dir = System.getProperty(".dir");

				if (url == null) {
					if (dir != null) {
						url = XngrURLUtilities.getURLFromFile(new File( dir	+ "help" + File.separator + "ExchangerHelp.hs"));
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

	public boolean isOpenInput() {
		return openInput.isSelected();
	}

	public boolean isTracing() {
		return enableTrace.isSelected();
	}

	public boolean isRedirectOutput() {
		return redirectOutput.isSelected();
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
	
	public void debuggingStarted() {
		xalanButton.setEnabled( false);
		saxon1Button.setEnabled( false);
		saxon2Button.setEnabled( false);
		enableTrace.setEnabled( false);
		redirectOutput.setEnabled( false);
		
		getSetParametersAction().setEnabled( false);
	}
	
	public void debuggingStopped() {
		xalanButton.setEnabled( true);
		saxon1Button.setEnabled( true);
		saxon2Button.setEnabled( true);
		enableTrace.setEnabled( true);
		redirectOutput.setEnabled( true);
		
		getSetParametersAction().setEnabled( true);
	}

	private void exit() {
		if ( getCloseTransformationAction().execute()) {

			DebuggerProperties props = properties.getDebuggerProperties();
	
			props.setDimension( getSize());
			props.setPosition( getLocation());
	
			props.setHorizontalSplitLocation( debugPane.getHorizontalSplitLocation());
			props.setVerticalSplitLocation( debugPane.getVerticalSplitLocation());
			props.setTraceSplitLocation( debugPane.getTraceSplitLocation());
			props.setVariablesSplitLocation( debugPane.getVariablesSplitLocation());
			props.setBreakpointsSplitLocation( debugPane.getBreakpointsSplitLocation());
			props.setDashboardSplitLocation( debugPane.getDashboardSplitLocation());
			
			properties.save();
			
			setVisible(false);
		
			if ( editor == null || !editor.isVisible()) {
				System.exit(0);
			} else {
				setVisible(false);
			}
		}
	}
	
	private TransformationPropertiesDialog getTransformationDialog() {
		if ( transformationDialog == null) {
			transformationDialog = new TransformationPropertiesDialog( this);
		}

		return transformationDialog;
	}
	
	public void setTransformation( XSLTTransformation transformation) {
		this.transformation = transformation;
		
		String xslUrl = null;
		String xmlUrl = null;

		if ( transformation != null) 
		{
		  
			if ( StringUtilities.isEmpty( transformation.getInputURL()) ) 
			{
			  
			  //TransformationPropertiesDialog dialog = getTransformationDialog();
				
				//dialog.show( transformation);
				
				//if ( dialog.isCancelled()) {
				//	this.transformation = null;
				//	return;
				//}
			  
				//ExchangerView view = parent.getView();
				
				//if ( view != null) {
				//	view.updateModel();
				//}

				ExchangerDocument document = null;
				if (this.editor != null)
				  document = editor.getDocument();
				
				

				if ( chooserXML == null) {
				  chooserXML = new XMLDocumentChooserDialog( this,  "Select XML Input", "Specify XML Input Document", editor, false, true);
				}

				if ( document != null) {
				  chooserXML.show( document.isXML());
				} else{
				  chooserXML.show( false);
				}
				

				if ( !chooserXML.isCancelled()) {
					try {
						if ( chooserXML.isOpenDocument()) {				  
						  document = chooserXML.getOpenDocument();	
						  xmlUrl = document.getURL().toString();
						}  
						else if ( !chooserXML.isCurrentDocument()) {
							xmlUrl = chooserXML.getInputLocation();

						}

						if (xmlUrl == null || xmlUrl.equals(""))
						{					
						  this.transformation = null;
						  return;
						}
						
						transformation.setInputURL(xmlUrl);

					} 
					catch (Exception ex) {}
//					catch ( IOException x) {
//						MessageHandler.showError( "Could not create the Document:\n"+chooserXML.getInputLocation(), "Document Error");
//					} 
//					catch ( SAXParseException x) {
//						MessageHandler.showError( "Could not parse the Document.", x, "Document Error");
//					}
					
				}		
				else {
					this.transformation = null;
					return;
				}		  
					
			}		
			
			if (  StringUtilities.isEmpty( transformation.getStyleURL())) {
				
				ExchangerDocument xslDocument = null;
				if (this.editor != null)
				  xslDocument = editor.getDocument();
				
					if ( chooserXSL == null) {
					  chooserXSL = new XMLDocumentChooserDialog( this,  "Select XSL Input", "Specify XSL Stylesheet", editor, false, true);
					}

					if ( xslDocument != null) {
					  chooserXSL.show( xslDocument.isXSL());
					} else{
					  chooserXSL.show( false);
					}
					
					if ( !chooserXSL.isCancelled()) {
						try {
							if ( chooserXSL.isOpenDocument()) {				  
							  xslDocument = chooserXSL.getOpenDocument();	
							  xslUrl = xslDocument.getURL().toString();
							}  
							else if ( !chooserXSL.isCurrentDocument()) {
								xslUrl =  chooserXSL.getInputLocation();

							}
							
							if (xslUrl == null || xslUrl.equals(""))
							{					
							  this.transformation = null;
							  return;
							}

							transformation.setStyleURL(xslUrl);

						} 
						catch (Exception ex) {}
//							catch ( IOException x) {
//							MessageHandler.showError( "Could not create the Document:\n"+chooserXSL.getInputLocation(), "Document Error");
//						} 
//						catch ( SAXParseException x) {
//							MessageHandler.showError( "Could not parse the Document.", x, "Document Error");
//						}
						
					}
					else {
						this.transformation = null;
						return;
					}
						
			}		
											
						
			if (transformation.getProcessor() == -1)
			{
				if ( processorDialog == null) {
				  processorDialog = new XSLTProcessorDialog( this);
				}
				
				processorDialog.setProcessor(ScenarioProperties.PROCESSOR_DEFAULT);
				processorDialog.setVisible(true);

				transformation.setProcessor(processorDialog.getProcessor());
			}
						
	/*					
			ScenarioProperties scenario = new ScenarioProperties();
			  if (chooserXML.isCurrentDocument())
			    scenario.setInputType(ScenarioProperties.INPUT_CURRENT_DOCUMENT);
			  else
			  {
			    scenario.setInputType(ScenarioProperties.INPUT_FROM_URL);
			    scenario.setInputFile(xmlUrl);
			  }

			  if (chooserXSL.isCurrentDocument())
			    scenario.setXSLType(ScenarioProperties.XSL_CURRENT_DOCUMENT);
			  else
			  {
			    scenario.setXSLType(ScenarioProperties.XSL_FROM_URL);
			    scenario.setXSLURL(xslUrl);
			  }

			  scenario.setOutputType(ScenarioProperties.OUTPUT_TO_NEW_DOCUMENT);

			  
			  scenario.setXSLEnabled(true);
			  
			  scenario.setProcessor(processorDialog.getProcessor());
			
			  transformation = new XSLTTransformation(scenario);						  
			parent.getExecutePreviousXSLTAction().setScenario( scenario);
			ScenarioUtilities.execute( parent.getDocument(), scenario);
*/			
						
			
			
			setTitle( "Exchanger XSLT Debugger ["+transformation.getName()+"]");
			
			int processor = transformation.getProcessor();
			
			if ( processor == ScenarioProperties.PROCESSOR_XALAN) {
				xalanButton.setSelected( true);
			} else if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT2){
				saxon2Button.setSelected( true);
			}else {
				saxon1Button.setSelected( true);
			}

			xalanButton.setEnabled( true);
			saxon1Button.setEnabled( true);
			saxon2Button.setEnabled( true);
			getSetParametersAction().setEnabled( true);
			
			getEnableTransformationBreakpointsAction().setEnabled( true);
			getDisableTransformationBreakpointsAction().setEnabled( true);
			getRemoveTransformationBreakpointsAction().setEnabled( true);

			getOpenStylesheetAction().setEnabled( true);
			getSaveScenarioAction().setEnabled( true);
			getCloseWindowAction().setEnabled( true);
			getCollapseAllAction().setEnabled( true);
			getExpandAllAction().setEnabled( true);
			getReloadWindowAction().setEnabled( true);
			getCloseTransformationAction().setEnabled( true);
			getOpenInputAction().setEnabled( true);
		} else {
			setTitle( "Exchanger XSLT Debugger");

			xalanButton.setEnabled( false);
			saxon1Button.setEnabled( false);
			saxon2Button.setEnabled( false);
			getSetParametersAction().setEnabled( false);

			getEnableTransformationBreakpointsAction().setEnabled( false);
			getDisableTransformationBreakpointsAction().setEnabled( false);
			getRemoveTransformationBreakpointsAction().setEnabled( false);

			getSaveScenarioAction().setEnabled( false);
			getCloseTransformationAction().setEnabled( false);
			getCloseWindowAction().setEnabled( false);
			getCollapseAllAction().setEnabled( false);
			getExpandAllAction().setEnabled( false);
			getReloadWindowAction().setEnabled( false);
			getOpenStylesheetAction().setEnabled( false);
			getOpenInputAction().setEnabled( false);
		}

		debugPane.setTransformation( transformation);
	}
	
	public OpenInputAction getOpenInputAction() {
		if ( openInputAction == null) {
			openInputAction = new OpenInputAction( this, debugPane, properties);
		} 
		
		return openInputAction;
	}

	public OpenStylesheetAction getOpenStylesheetAction() {
		if ( openStylesheetAction == null) {
			openStylesheetAction = new OpenStylesheetAction( this, debugPane, properties);
		} 
		
		return openStylesheetAction;
	}

	public OpenScenarioAction getOpenScenarioAction() {
		if ( openScenarioAction == null) {
			openScenarioAction = new OpenScenarioAction( this, debugPane, properties);
		} 
		
		return openScenarioAction;
	}

	public EnableTransformationBreakpointsAction getEnableTransformationBreakpointsAction() {
		if ( enableBreakpointsAction == null) {
			enableBreakpointsAction = new EnableTransformationBreakpointsAction( debugPane);
		} 
		
		return enableBreakpointsAction;
	}

	public DisableTransformationBreakpointsAction getDisableTransformationBreakpointsAction() {
		if ( disableBreakpointsAction == null) {
			disableBreakpointsAction = new DisableTransformationBreakpointsAction( debugPane);
		} 
		
		return disableBreakpointsAction;
	}

	public RemoveTransformationBreakpointsAction getRemoveTransformationBreakpointsAction() {
		if ( removeBreakpointsAction == null) {
			removeBreakpointsAction = new RemoveTransformationBreakpointsAction( debugPane);
		} 
		
		return removeBreakpointsAction;
	}

	public CloseTransformationAction getCloseTransformationAction() {
		if ( closeTransformationAction == null) {
			closeTransformationAction = new CloseTransformationAction( this, debugPane, properties);
		} 
		
		return closeTransformationAction;
	}

	public CloseWindowAction getCloseWindowAction() {
		if ( closeWindowAction == null) {
			closeWindowAction = new CloseWindowAction( debugPane);
		} 
		
		return closeWindowAction;
	}

	public CollapseAllAction getCollapseAllAction() {
		if ( collapseAllAction == null) {
			collapseAllAction = new CollapseAllAction( debugPane);
		} 
		
		return collapseAllAction;
	}

	public ExpandAllAction getExpandAllAction() {
		if ( expandAllAction == null) {
			expandAllAction = new ExpandAllAction( debugPane);
		} 
		
		return expandAllAction;
	}

	public ReloadWindowAction getReloadWindowAction() {
		if ( reloadWindowAction == null) {
			reloadWindowAction = new ReloadWindowAction( debugPane);
		} 
		
		return reloadWindowAction;
	}
	
	public CutAction getCutAction() {
		if ( cutAction == null) {
			cutAction = new CutAction( this, debugPane);
		} 
		
		return cutAction;
	}
	
	public CopyAction getCopyAction() {
		if ( copyAction == null) { 
			copyAction = new CopyAction( this, debugPane);
		} 
		
		return copyAction;
	}
	
	public PasteAction getPasteAction() {
		if ( pasteAction == null) {
			pasteAction = new PasteAction( this, debugPane);
		} 
		
		return pasteAction;
	}
	
	public SaveAction getSaveAction() {
		if ( saveAction == null) {
			saveAction = new SaveAction( this, debugPane);
		} 
		
		return saveAction;
	}
	
	public SaveAsAction getSaveAsAction() {
		if ( saveAsAction == null) {
			saveAsAction = new SaveAsAction( this, debugPane);
		} 
		
		return saveAsAction;
	}

	public FindAction getFindAction() {
		if ( findAction == null) {
			findAction = new FindAction( this, properties, debugPane);
		} 
		
		return findAction;
	}

	public GotoAction getGotoAction() {
		if ( gotoAction == null) {
			gotoAction = new GotoAction( this, debugPane);
		} 
		
		return gotoAction;
	}

	public FindNextAction getFindNextAction() {
		if ( findNextAction == null) {
			findNextAction = new FindNextAction( debugPane, properties);
		} 
		
		return findNextAction;
	}

	public NewTransformationAction getNewScenarioAction() {
		if ( newScenarioAction == null) {
			newScenarioAction = new NewTransformationAction( this, debugPane, properties);
		} 
		
		return newScenarioAction;
	}

	public SetParametersAction getSetParametersAction() {
		if ( setParametersAction == null) {
			setParametersAction = new SetParametersAction( this, debugPane);
		} 
		
		return setParametersAction;
	}

	public SaveScenarioAction getSaveScenarioAction() {
		if ( saveScenarioAction == null) {
			saveScenarioAction = new SaveScenarioAction( this, debugPane, properties);
		} 
		
		return saveScenarioAction;
	}

	public static void main(String[] args) {
		System.setProperty( "org.dom4j.factory", "com.cladonia.xml.XDocumentFactory");

		ConfigurationProperties props = getProperties();
		IconFactory.setProperties( props);

		XSLTDebuggerFrame debugger = new XSLTDebuggerFrame( props, null);

		FileUtilities.init( debugger, null, props);
		ScenarioUtilities.init( null, props);
		MessageHandler.init( debugger);

		debugger.setVisible(true);
	}
	
	private static ConfigurationProperties getProperties() {
		ExchangerDocument document = null;
		boolean firstTime = false;

		File dir = new File( XNGR_EDITOR_HOME);

		if ( !dir.exists()) {
			dir.mkdir();
		}
		
		File file = new File( dir, PROPERTIES_FILE);
		URL url = null;

		try {
			url = com.cladonia.xml.XngrURLUtilities.getURLFromFile(file); // MalformedURLException
		} catch( Exception e) {
			// Should never happen, am not sure what to do in this case...
			e.printStackTrace();
		}
		
		if ( file.exists()) {
			try {
				document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
			} catch (Exception e) {
				// should not happen, document should always be valid...
				e.printStackTrace();
				return null;
			}
		}

		if ( document == null) {
			//XElement root = new XElement( "xngr", "http://www.cladonia.com/xngr-editor/"+Identity.getIdentity().getVersion()+"/");
			XElement root = new XElement( "xngr");
			document = new ExchangerDocument( url, root);
		}
		
		return new ConfigurationProperties( document);
	}
	
	/**
	 * creates the nmeu item, and adds a map of action names (defined in KeyPreferences) to menu 
	 * items
	 */
	private JMenuItem createMenuItem(Action action, String actionName) {
		
		XNGRMenuItem temp = getMenuItem(actionName);
		if (temp != null)
		{
			return temp;
		}
		
		//  use XNGRMenuItem instead of JMenuItem, allows for emacs accelerator keys which are set in 
		// KeyPreferences
		XNGRMenuItem item = new XNGRMenuItem(action);
		menuItemMap.put(actionName,item);
		//modeActionMap.put(actionName,action);
		
		return item;
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
	 * @return Returns the editor.
	 */
	public ExchangerEditor getEditor() {
	
		return editor;
	}

	
	/**
	 * @param editor The editor to set.
	 */
	public void setEditor(ExchangerEditor editor) {
	
		this.editor = editor;
	}
}
