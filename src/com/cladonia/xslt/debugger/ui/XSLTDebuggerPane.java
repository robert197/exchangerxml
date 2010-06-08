/*
 * $Id: XSLTDebuggerPane.java,v 1.17 2005/08/26 11:03:41 tcurley Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.xml.sax.SAXParseException;

// import org.bounce.image.ImageLoader;

import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.BreakpointProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xslt.debugger.Breakpoint;
import com.cladonia.xslt.debugger.BreakpointList;
import com.cladonia.xslt.debugger.ui.actions.ContinueAction;
import com.cladonia.xslt.debugger.ui.actions.ContinueToEndAction;
import com.cladonia.xslt.debugger.ui.actions.PauseAction;
import com.cladonia.xslt.debugger.ui.actions.StepIntoAction;
import com.cladonia.xslt.debugger.ui.actions.StepOutAction;
import com.cladonia.xslt.debugger.ui.actions.StepOverAction;
import com.cladonia.xslt.debugger.ui.actions.StopAction;

/**
 * The XSLT debug controller.
 *
 * @version	$Revision: 1.17 $, $Date: 2005/08/26 11:03:41 $
 * @author Dogsbay
 */
public class XSLTDebuggerPane extends JPanel {
	private static final CompoundBorder NORMAL_BORDER = new CompoundBorder( 
			new CompoundBorder( 
				new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
				new MatteBorder( 0, 0, 1, 1, Color.white)),
			new EmptyBorder( 1, 1, 1, 1));

	private XSLTDebuggerFrame parent = null;
	private ConfigurationProperties properties = null;

	private XSLTTransformation transformation = null;

	private Statusbar statusbar = null;
	
	private JSplitPane verticalSplit = null;
	private JSplitPane horizontalSplit = null;
	private JSplitPane dashboardSplit = null;
	private JSplitPane variablesSplit = null;
	private JSplitPane traceSplit = null;

//	private JSplitPane dashboard = null;
	private VariablesPanel variablesPanel = null;
	private StacksPanel stacksPanel = null;
	private TracePanel tracePanel = null;
	private ContextPanel contextPanel = null;
	private TransformationPanel transformationPanel = null;

	private ContinueAction continueAction = null;
	private ContinueToEndAction continueToEndAction = null;
	private StepOverAction stepOverAction = null;
	private StepIntoAction stepIntoAction = null;
	private StepOutAction stepOutAction = null;
	private StopAction stopAction = null;
	private PauseAction pauseAction = null;

	private XSLTDebugController controller = null;
	private InputView xsltViewer = null;
	
	private InputView inputViewer = null;
	private OutputView outputViewer = null;
	
	private boolean traceEnabled = true;

		
	public XSLTDebuggerPane( XSLTDebuggerFrame parent, ConfigurationProperties properties) {
		super( new BorderLayout());
		
		this.parent = parent;
		this.properties = properties;
		
		DebuggerProperties props = properties.getDebuggerProperties();

		xsltViewer = new InputView( this, properties);
		xsltViewer.setShowFoldingMargin( props.showStyleFoldingMargin());		
		xsltViewer.setShowLinenumberMargin( props.showStyleLinenumberMargin());		
		xsltViewer.setShowOverviewMargin( props.showStyleOverviewMargin());		
		xsltViewer.setWrapping( props.isWrapStyle());
		

		inputViewer = new InputView(this, properties);
		inputViewer.setShowFoldingMargin( props.showInputFoldingMargin());		
		inputViewer.setShowLinenumberMargin( props.showInputLinenumberMargin());		
		inputViewer.setShowOverviewMargin( props.showInputOverviewMargin());		
		inputViewer.setWrapping( props.isWrapInput());
		
		horizontalSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, xsltViewer, inputViewer);
		horizontalSplit.setOneTouchExpandable( true);
		horizontalSplit.setBorder( null);

		horizontalSplit.setResizeWeight( 0.5);

		if ( horizontalSplit.getDividerSize() > 6) {
			horizontalSplit.setDividerSize( 6);
		}

		Object ui = horizontalSplit.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}

		JPanel panel = new JPanel( new BorderLayout());
		panel.setBorder( new EmptyBorder( 2, 2, 2, 2));

		outputViewer = new OutputView( this, this.properties);
		outputViewer.setShowLinenumberMargin( props.showOutputLinenumberMargin());		
		outputViewer.setWrapping( props.isWrapOutput());

		traceSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, outputViewer, getTracePanel());
		traceSplit.setOneTouchExpandable( true);
		traceSplit.setBorder( null);

		traceSplit.setResizeWeight( 1);

		if ( traceSplit.getDividerSize() > 6) {
			traceSplit.setDividerSize( 6);
		}

		ui = traceSplit.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}

		JPanel dashboard = createDashboard();
		
		dashboardSplit = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, horizontalSplit, dashboard);
//		dashboardSplit.setOneTouchExpandable( true);
		dashboardSplit.setBorder( null);

		dashboardSplit.setResizeWeight( 1);

		if ( dashboardSplit.getDividerSize() > 6) {
			dashboardSplit.setDividerSize( 6);
		}

		ui = dashboardSplit.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}

		verticalSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, dashboardSplit, traceSplit);
		verticalSplit.setOneTouchExpandable( true);
		verticalSplit.setBorder( null);

		verticalSplit.setResizeWeight( 1);

		if ( verticalSplit.getDividerSize() > 6) {
			verticalSplit.setDividerSize( 6);
		}

		ui = verticalSplit.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}
		
		getContinueAction().setEnabled( false);
		getContinueToEndAction().setEnabled( false);
		getPauseAction().setEnabled( false);
		getStepIntoAction().setEnabled( false);
		getStepOutAction().setEnabled( false);
		getStepOverAction().setEnabled( false);
		getStopAction().setEnabled( false);
		
		statusbar = new Statusbar( this);

		statusbar.setStatus( Statusbar.STATUS_IDLE, "No Transformation available.");

		add( verticalSplit, BorderLayout.CENTER);
		add( statusbar, BorderLayout.SOUTH);
		
		ActionMap am = getActionMap();
		InputMap im = getInputMap( WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		KeyStroke f9 = KeyStroke.getKeyStroke( KeyEvent.VK_F9, 0, false);    
		
		KeyStroke ctrlF9 = KeyStroke.getKeyStroke( KeyEvent.VK_F9, InputEvent.CTRL_MASK, false);

		im.put( ctrlF9, "toggle-breakpoint");
		im.put( f9, "set-breakpoint");

		//		Keymap defaultKeymap = getKeymap();

		am.put( "toggle-breakpoint", 
				new AbstractAction() {
					public void actionPerformed( ActionEvent event) {
						InputPane pane = getSelectedPane();
						
						if ( pane != null) {
							pane.toggleBreakpoint();
						}
					}
				});

		am.put( "set-breakpoint", 
				new AbstractAction() {
					public void actionPerformed( ActionEvent event) {
						InputPane pane = getSelectedPane();
						
						if ( pane != null) {
							pane.setBreakpoint();
						}
					}
				});
		
		
	}
	
	public int getHorizontalSplitLocation() {
		return horizontalSplit.getDividerLocation();
	}
	
	public void setHorizontalSplitLocation( int location) {
		horizontalSplit.setDividerLocation( location);
	}
	
	public int getVerticalSplitLocation() {
		return verticalSplit.getDividerLocation();
	}

	public void setVerticalSplitLocation( int location) {
		verticalSplit.setDividerLocation( location);
	}
	
	public int getDashboardSplitLocation() {
		return dashboardSplit.getDividerLocation();
	}

	/*public void setSelectedView( InputView view) {
		if ( view == inputViewer) {
			xsltViewer.setSelected( false);
		} else {
			inputViewer.setSelected( false);
		}
	}*/
	
	public void setSelectedView( JPanel view) {
		
		if(view instanceof InputView) {
			if ( view == inputViewer) {
				xsltViewer.setSelected( false);
				outputViewer.setSelected(false);
			} else {
				inputViewer.setSelected( false);
				outputViewer.setSelected(false);
			}
		}
		else if(view instanceof OutputView) {
			
			xsltViewer.setSelected( false);
			inputViewer.setSelected( false);			
		}
	}

	public void updateSelectedView() {
		
		if ( !(inputViewer.getSelectedPane() instanceof InputPane) && !(xsltViewer.getSelectedPane() instanceof InputPane)) {
			xsltViewer.setSelected( false);
			inputViewer.setSelected( false);
		} else if ( !(inputViewer.getSelectedPane() instanceof InputPane)) {
			xsltViewer.setSelected( true);
			inputViewer.setSelected( false);
		} else if ( !(xsltViewer.getSelectedPane() instanceof InputPane)) {
			xsltViewer.setSelected( false);
			inputViewer.setSelected( true);
		}
	}

	public void setDashboardSplitLocation( int location) {
		dashboardSplit.setDividerLocation( location);
	}

	public int getVariablesSplitLocation() {
		return variablesSplit.getDividerLocation();
	}

	public void setVariablesSplitLocation( int location) {
		variablesSplit.setDividerLocation( location);
	}

	public int getTraceSplitLocation() {
		return traceSplit.getDividerLocation();
	}

	public void setTraceSplitLocation( int location) {
		traceSplit.setDividerLocation( location);
	}

	public int getBreakpointsSplitLocation() {
		return transformationPanel.getBreakpointsSplitLocation();
	}

	public void setBreakpointsSplitLocation( int location) {
		transformationPanel.setBreakpointsSplitLocation( location);
//		variablesSplit.setDividerLocation( location);
	}

	public InputView getXSLTView() {
		return xsltViewer;
	}

	public InputView getInputView() {
		return inputViewer;
	}

	public OutputView getOutputView() {
		return outputViewer;
	}

	public XSLTTransformation getTransformation() {
		return transformation;
	}

	public XSLTDebugController getDebugController() {
		return controller;
	}

	public boolean isOpenInput() {
		return parent.isOpenInput();
	}
	
	public JFrame getFrame() {
		return parent;
	}
	
	public void updatePreferences() {
		xsltViewer.updatePreferences();
		inputViewer.updatePreferences();
		outputViewer.updatePreferences();
//		variablesPanel.updatePreferences();
//		stacksPanel.updatePreferences();
//		tracePanel.updatePreferences();
//		contextPanel.updatePreferences();
//		transformationPanel.updatePreferences();
	}
	
	public void updateParameters() {
		transformationPanel.setTransformation( transformation);
	}

	public void startDebugging() {
		if ( controller != null) {
			controller.cleanUp();
			System.gc();
		}

		//Thomas Curley 24.08.05
		//need to see if the documents have changed - 
		//if so - prompt to save
		if( saveViewsToDisk() == true) {
		
			controller = new XSLTDebugController( this, transformation, parent.isTracing(), parent.isRedirectOutput());
			reload();
	
			getStepOutAction().setDebugger( controller);
			getStepOverAction().setDebugger( controller);
			getPauseAction().setDebugger( controller);
			int processor = transformation.getProcessor();
	
			if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT1) {
				statusbar.setProcessor( Statusbar.PROCESSOR_SAXON1);
			} else if ( processor == ScenarioProperties.PROCESSOR_XALAN) {
				statusbar.setProcessor( Statusbar.PROCESSOR_XALAN);
			}
			else  if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT2) {
			  statusbar.setProcessor( Statusbar.PROCESSOR_SAXON2);
			}
		}
		else {
			
			//error saving or reloading documents
			this.stopDebugging();
		}
	}
	
		
	public boolean saveViewsToDisk() {
		
		if((saveXSLTViews() == true) && (saveInputViews() == true)) {
			return(true);
		}
		else {
			return(false);
		}
	}
	
	public boolean saveInputViews() {
		Vector inputViews = inputViewer.getViews();
		for(int cnt=0;cnt<inputViews.size();++cnt) {
			InputPane pane = (InputPane)inputViews.get(cnt);
						
			if((pane != null) && (pane.hasDocumentChanged() == true)) {
				int value = MessageHandler.showConfirm(this.parent, "Document \""+pane.getSourceName()+"\" has been changed.\n"+
				"Do you want to save the changes?");
				
				if ( value == JOptionPane.YES_OPTION) {
					
					try {
						pane.save();
						return(true);
						
					} catch (SAXParseException e) {
						
						MessageHandler.showError(this.parent, "The changed document is not well formed:\n"+e.getMessage(), "Well Formed Error");
						e.printStackTrace();
						return(false);
						
					} catch (IOException e) {
						
						MessageHandler.showError(this.parent, "The changed document cannot be saved", "Save Error");
						e.printStackTrace();
						return(false);
					} catch (Exception e) {
						
						MessageHandler.showError(this.parent, "The changed document cannot be saved", "Save Error");
						e.printStackTrace();
						return(false);
					}
					
				}
				else if( value == JOptionPane.NO_OPTION) {
					
					try {
						pane.reload();
						return(true);
						
					} catch (IOException e) {
						
						MessageHandler.showError(this.parent, "The document cannot be reloaded", "Reload Error");
						e.printStackTrace();
						return(false);
					}
				}
				
			}
			else {
				
				return(true);
			}
		}
		
		//no input views
		return(false);
	}
	
	public boolean saveXSLTViews() {
		
		Vector xsltViews = xsltViewer.getViews();
		for(int cnt=0;cnt<xsltViews.size();++cnt) {
			InputPane pane = (InputPane)xsltViews.get(cnt);
						
			if((pane != null) && (pane.hasDocumentChanged() == true)) {
				int value = MessageHandler.showConfirm(this.parent, "Document \""+pane.getSourceName()+"\" has been changed.\n"+
				"Do you want to save the changes?");
				
				if ( value == JOptionPane.YES_OPTION) {
					
					try {
						pane.save();
						return(true);
						
					} catch (SAXParseException e) {
						
						MessageHandler.showError(this.parent, "The changed document is not well formed:\n"+e.getMessage(), "Well Formed Error");
						e.printStackTrace();
						return(false);
						
					} catch (IOException e) {
						
						MessageHandler.showError(this.parent, "The changed document cannot be saved", "Save Error");
						e.printStackTrace();
						return(false);
					} catch (Exception e) {
						
						MessageHandler.showError(this.parent, "The changed document cannot be saved", "Save Error");
						e.printStackTrace();
						return(false);
					}
					
				}
				else if( value == JOptionPane.NO_OPTION) {
					
					try {
						pane.reload();
						return(true);
						
					} catch (IOException e) {
						
						MessageHandler.showError(this.parent, "The document cannot be reloaded", "Reload Error");
						e.printStackTrace();
						return(false);
					}
				}
				
			}
			else {
				
				return(true);
			}
		}
		
		//no input views
		return(false);
	}
	
	public void reload() {
		
		xsltViewer.reload();
		inputViewer.reload();
	}

	public void continueDebugging() {
		controller.continueToBreakOrEnd();
	}

	public void stepDebugger() {
		controller.step();
	}
	
	public void setRunning( boolean running) {
		if ( running) {
			parent.debuggingStarted();
		} else {
			parent.debuggingStopped();
		}
	}

	public void stopDebugging() {
		if ( controller != null) {
			controller.stop();
			
			controller.cleanUp();
			
			//getStacksPanel().reset();
			//getVariablesPanel().reset();

			controller = null;
			System.gc();
		}
	}
	
	public void runToEnd() {
		controller.runToEnd();
	}

	public InputPane getSelectedPane() {
		
		if ( inputViewer.isSelected()) {
			return inputViewer.getSelectedPane();
		} else if ( xsltViewer.isSelected()) {
			return xsltViewer.getSelectedPane();
		}

		return null;
	}
	
	public OutputPane getSelectedOutputPane() {
		
		return outputViewer.getSelectedPane();
		
	}
	
	public MessagePane getSelectedMessagePane() {

		//if ( outputViewer.isSelected()) {
			return outputViewer.getSelectedMessagePane();
		//}
		//else if( messagePane.){
			
		//}

		//return null;		
	}
	
	public JPanel getSelectedArea() {
		
		if( outputViewer.isSelected() == true) {
			//System.out.println("output viewer is selected");
			return(outputViewer);
		}
		
		if(this.inputViewer.isSelected() == true) {
			//System.out.println("input is selected");
			return(inputViewer);
		}
		
		if(this.xsltViewer.isSelected() == true) {
			//System.out.println("xslt is selected");
			return(xsltViewer);
		}
		
		return(null);
	}

	public void closeSelectedPane() {
		if ( inputViewer.isSelected()) {
			InputPane pane = inputViewer.getSelectedPane();

			if ( pane != null) {
				inputViewer.remove( pane);
			}
		} else if ( xsltViewer.isSelected()) {
			InputPane pane = xsltViewer.getSelectedPane();

			if ( pane != null) {
				xsltViewer.remove( pane);
			}
		}
	}
	
	public void debuggingFinished() {
		MessageHandler.showMessage( parent, "XSLT Transformation Finished");
	}

	private void initBreakpoints( Vector props, BreakpointList breakpoints) {
		for ( int i = 0; i < props.size(); i++) {
			BreakpointProperties prop = (BreakpointProperties)props.elementAt(i);
			
			breakpoints.addBreakpoint( new Breakpoint( prop.getURL(), prop.getLine(), prop.isEnabled()));
		}
	}

	public void reset() {
		outputViewer.removeAllViews();
		
		tracePanel.reset();
		variablesPanel.reset();
		stacksPanel.reset();
		
		System.gc();
	}
	
	public void setTransformation( XSLTTransformation transformation) {
		
		if ( this.transformation != transformation) {
			xsltViewer.removeAllViews();
			inputViewer.removeAllViews();

			transformationPanel.setTransformation( null);
		}

		// TODO Close previous views and update the breakpoints in the scenario.
		this.transformation = transformation;
		
		if ( transformation != null) {
			getContinueAction().setEnabled( true);
			getContinueToEndAction().setEnabled( true);
			getPauseAction().setEnabled( false);
			getStepIntoAction().setEnabled( true);
			getStepOutAction().setEnabled( false);
			getStepOverAction().setEnabled( false);
			getStopAction().setEnabled( false);

			URL stylesheet = URLUtilities.toURL( transformation.getStyleURL());
			URL input = URLUtilities.toURL( transformation.getInputURL());
//			String output = scenario.getOutputFile();
			
			if ( stylesheet == null || input == null) {
//				System.out.println( "Scenarios are null...");
				return;
			}

			int processor = transformation.getProcessor();
			
			//if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT2) {
			//	MessageHandler.showError( parent, "Saxon (XSLT 2.0) is currently not supported.", "Scenario Error");
			//	return;
			//}

			// TODO Fill the breakpoints from the scenario.
			xsltViewer.setBreakpoints( transformation.getStyleBreakpoints());

			// TODO Check for stylesheet.
			xsltViewer.select( stylesheet.toString());
	
			inputViewer.setBreakpoints( transformation.getInputBreakpoints());
			
			transformationPanel.setTransformation( transformation);

			if ( parent.isOpenInput()) {
				inputViewer.select( input.toString());
			}

			statusbar.setInputURL( input.toString());
			statusbar.setStyleURL( stylesheet.toString());
	
			statusbar.setStatus( Statusbar.STATUS_IDLE, "Transformation not started.");

			if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT1) {
				statusbar.setProcessor( Statusbar.PROCESSOR_SAXON1);
			} else if ( processor == ScenarioProperties.PROCESSOR_XALAN) {
				statusbar.setProcessor( Statusbar.PROCESSOR_XALAN);
			}
			else  if ( processor == ScenarioProperties.PROCESSOR_SAXON_XSLT2) {
			  statusbar.setProcessor( Statusbar.PROCESSOR_SAXON2);
			}
			
		} else {
			statusbar.setInputURL( null);
			statusbar.setStyleURL( null);
			statusbar.setProcessor( null);
			statusbar.setStatus( Statusbar.STATUS_IDLE, "No Transformation available.");

			getContinueAction().setEnabled( false);
			getContinueToEndAction().setEnabled( false);
			getStepIntoAction().setEnabled( false);
			getStepOverAction().setEnabled( false);
			getStepOutAction().setEnabled( false);
			getStopAction().setEnabled( false);
			getPauseAction().setEnabled( false);
		}

		reset();
	}
	
	public void openInput( String location) {
		inputViewer.select( location);
	}

	public void openStylesheet( String location) {
		xsltViewer.select( location);
	}

	public Statusbar getStatusbar() {
		return statusbar;
	}
	
	public void setTraceEnabled( boolean enabled) {
		traceEnabled = enabled;
		
		tracePanel.setVisible( enabled);
	}

	public ContinueAction getContinueAction() {
		if ( continueAction == null) {
			continueAction = new ContinueAction( this);
		}
		
		return continueAction;
	}

	public ContinueToEndAction getContinueToEndAction() {
		if ( continueToEndAction == null) {
			continueToEndAction = new ContinueToEndAction( this);
		}
		
		return continueToEndAction;
	}

	public PauseAction getPauseAction() {
		if ( pauseAction == null) {
			pauseAction = new PauseAction();
		}
		
		return pauseAction;
	}

	public StepIntoAction getStepIntoAction() {
		if ( stepIntoAction == null) {
			stepIntoAction = new StepIntoAction( this);
		}
		
		return stepIntoAction;
	}

	public StepOutAction getStepOutAction() {
		if ( stepOutAction == null) {
			stepOutAction = new StepOutAction();
		}
		
		return stepOutAction;
	}

	public StepOverAction getStepOverAction() {
		if ( stepOverAction == null) {
			stepOverAction = new StepOverAction();
		}
		
		return stepOverAction;
	}

	public StopAction getStopAction() {
		if ( stopAction == null) {
			stopAction = new StopAction( this);
		}
		
		return stopAction;
	}
	
	public void update() {
//		System.out.println( "XSLTDebugger.update()");
		getVariablesPanel().setGlobalVariables( controller.getGlobalVariables());
		getVariablesPanel().setLocalVariables( controller.getLocalVariables());

		getStacksPanel().setStyleStack( controller.getStylesheetStack());
		getStacksPanel().setInputStack( controller.getInputStack());
		getStacksPanel().setOutputStack( controller.getOutputStack());
		getStacksPanel().setMixedStack( controller.getMixedStack());

		getTracePanel().setStyleTrace( controller.getStylesheetTrace());
		getTracePanel().setInputTrace( controller.getInputTrace());
		getTracePanel().setOutputTrace( controller.getOutputTrace());
		getTracePanel().setMixedTrace( controller.getMixedTrace());
	}
	
	private VariablesPanel getVariablesPanel() {
		if ( variablesPanel == null) {
			variablesPanel = new VariablesPanel( this);
		}

		return variablesPanel;
	}
	
	private StacksPanel getStacksPanel() {
		if ( stacksPanel == null) {
			stacksPanel = new StacksPanel( this);
		}

		return stacksPanel;
	}

	private TracePanel getTracePanel() {
		if ( tracePanel == null) {
			tracePanel = new TracePanel( this);
		}

		return tracePanel;
	}

	private ContextPanel getContextPanel() {
		if ( contextPanel == null) {
			contextPanel = new ContextPanel( this);
		}

		return contextPanel;
	}
	
	public void breakPointsUpdated( InputView view) { 
		if ( view == xsltViewer) {
			transformationPanel.setStyleBreakpoints( view.getBreakPoints());
		} else if ( view == inputViewer) {
			transformationPanel.setInputBreakpoints( view.getBreakPoints());
		}
	}
	
	public void styleBreakPointsUpdated() {
		xsltViewer.setBreakpoints( xsltViewer.getBreakPoints());
	}

	public void inputBreakPointsUpdated() {
		inputViewer.setBreakpoints( inputViewer.getBreakPoints());
	}

	private JPanel createDashboard() {
		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT);
		
		variablesSplit = new JSplitPane( JSplitPane.VERTICAL_SPLIT, getVariablesPanel(), getStacksPanel());
//		variablesSplit.setOneTouchExpandable( true);
		variablesSplit.setBorder( null);

		variablesSplit.setResizeWeight( 0.5);

		if ( variablesSplit.getDividerSize() > 6) {
			variablesSplit.setDividerSize( 6);
		}

		Object ui = variablesSplit.getUI();
		if ( ui instanceof BasicSplitPaneUI) {
			((BasicSplitPaneUI)ui).getDivider().setBorder( null);
		}
		
		transformationPanel = new TransformationPanel(this);
		
		tabs.addTab( "Details", variablesSplit);
		tabs.addTab( "Settings", transformationPanel);

		JPanel panel = new JPanel( new BorderLayout());
		panel.add( tabs, BorderLayout.CENTER);
		
		panel.setBorder( new CompoundBorder( 
					new MatteBorder( 1, 1, 0, 0, UIManager.getColor( "controlDkShadow")), 
					new MatteBorder( 0, 0, 1, 1, Color.white)));

		return panel;
	}

	
	/**
	 * @return Returns the parent.
	 */
	public XSLTDebuggerFrame getDebuggerFrame() {
	
		return parent;
	}

	

	
	
	
}
