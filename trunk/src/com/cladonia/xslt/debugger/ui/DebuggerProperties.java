/*
 * $Id: DebuggerProperties.java,v 1.5 2004/09/30 11:29:32 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xslt.debugger.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertiesFile;

/**
 * Handles the properties for the Debugger.
 *
 * @version	$Revision: 1.5 $, $Date: 2004/09/30 11:29:32 $
 * @author Dogsbay
 */
public class DebuggerProperties extends PropertiesFile {
	
	public static final String DEBUGGER = "debugger";
	
	private static final int MAX_XPATHS = 10;

	public static final int DEFAULT_WIDTH 	= 780;
	public static final int DEFAULT_HEIGHT	= 640;
	
	public static final String WINDOW_MAXIMISED = "window-maximised";

	private static final String XPOS	= "xpos";
	private static final String YPOS	= "ypos";
	private static final String WIDTH	= "width";
	private static final String HEIGHT	= "height";

	private static final String HORIZONTAL_SPLIT_LOCATION		= "horizontal-split-location";
	private static final int DEFAULT_HORIZONTAL_SPLIT_LOCATION	= 290;

	private static final String VERTICAL_SPLIT_LOCATION			= "vertical-split-location";
	private static final int DEFAULT_VERTICAL_SPLIT_LOCATION	= 360;

	private static final String VARIABLE_SPLIT_LOCATION			= "variable-split-location";
	private static final int DEFAULT_VARIABLE_SPLIT_LOCATION	= 180;

	private static final String BREAKPOINT_SPLIT_LOCATION		= "breakpoint-split-location";
	private static final int DEFAULT_BREAKPOINT_SPLIT_LOCATION	= 180;

	private static final String DASHBOARD_SPLIT_LOCATION		= "dashboard-split-location";
	private static final int DEFAULT_DASHBOARD_SPLIT_LOCATION	= 580;
	
	private static final String TRACE_SPLIT_LOCATION			= "trace-split-location";
	private static final int DEFAULT_TRACE_SPLIT_LOCATION		= 580;
	
	private static final String ENABLE_TRACING					= "enable-tracing";
	private static final String AUTOMATIC_OPEN_INPUT			= "automatic-open-input";
	private static final String REDIRECT_OUTPUT					= "redirect-output";

	private static final String SHOW_INPUT_OVERVIEW_MARGIN		= "show-input-overview-margin";
	private static final String SHOW_INPUT_LINENUMBER_MARGIN	= "show-input-linenumber-margin";
	private static final String SHOW_INPUT_FOLDING_MARGIN		= "show-input-folding-margin";
	private static final String WRAP_INPUT						= "wrap-input";

	private static final String SHOW_STYLE_OVERVIEW_MARGIN		= "show-style-overview-margin";
	private static final String SHOW_STYLE_LINENUMBER_MARGIN	= "show-style-linenumber-margin";
	private static final String SHOW_STYLE_FOLDING_MARGIN		= "show-style-folding-margin";
	private static final String WRAP_STYLE						= "wrap-style";

	private static final String SHOW_OUTPUT_LINENUMBER_MARGIN	= "show-output-linenumber-margin";
	private static final String WRAP_OUTPUT						= "wrap-output";

	/**
	 * Constructor for the debugger properties.
	 *
	 * @param element the element that contains the properties,
	 *        for the debugger.
	 */
	public DebuggerProperties( ExchangerDocument document, XElement element) {
		super( document, element);
	}
	
	public DebuggerProperties(String fileName, String rootName) {
		super(loadPropertiesFile(fileName, rootName));
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the x/y position on screen of the debugger.
	 *
	 * @return the x/y position on screen.
	 */
	public Point getPosition() {
		// Gather the default values...
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screen = kit.getScreenSize();
		Dimension app = getDimension();
		
		int posx = (screen.width - app.width)/2;
		int posy = (screen.height - app.height)/2;
		
		return new Point( getInteger( XPOS, posx), getInteger( YPOS, posy));
	}

	/**
	 * Sets the x/y position on screen of the debugger.
	 *
	 * @return the x/y position on screen.
	 */
	public void setPosition( Point point) {
		set( XPOS, point.x);
		set( YPOS, point.y);
	}
	
	/**
	 * Returns the dimension on screen of the debugger.
	 *
	 * @return the dimension on screen.
	 */
	public Dimension getDimension() {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screen = kit.getScreenSize();

		return new Dimension( Math.min( getInteger( WIDTH, DEFAULT_WIDTH), screen.width), Math.min( getInteger( HEIGHT, DEFAULT_HEIGHT), screen.height));
	}

	/**
	 * Sets the dimension on screen of the debugger.
	 *
	 * @return the dimension on screen.
	 */
	public void setDimension( Dimension dimension ) {
		set( WIDTH, dimension.width);
		set( HEIGHT, dimension.height);
	}
	
	/**
	 * Set the horizontal split location.
	 *
	 * @param location the horizontal split location.
	 */
	public void setHorizontalSplitLocation( int location) {
		set( HORIZONTAL_SPLIT_LOCATION, location);
	}

	/**
	 * Gets the location of the horizontal split.
	 *
	 * @return the location of the horizontal split.
	 */
	public int getHorizontalSplitLocation() {
		return getInteger( HORIZONTAL_SPLIT_LOCATION, DEFAULT_HORIZONTAL_SPLIT_LOCATION);
	}
	
	/**
	 * Set the vertical split location.
	 *
	 * @param location the vertical split location.
	 */
	public void setVerticalSplitLocation( int location) {
		set( VERTICAL_SPLIT_LOCATION, location);
	}

	/**
	 * Gets the location of the vertical split.
	 *
	 * @return the location of the vertical split.
	 */
	public int getVerticalSplitLocation() {
		return getInteger( VERTICAL_SPLIT_LOCATION, DEFAULT_VERTICAL_SPLIT_LOCATION);
	}

	/**
	 * Set the variable split location.
	 *
	 * @param location the variable split location.
	 */
	public void setVariablesSplitLocation( int location) {
		set( VARIABLE_SPLIT_LOCATION, location);
	}

	public int getVariablesSplitLocation() {
		return getInteger( VARIABLE_SPLIT_LOCATION, DEFAULT_VARIABLE_SPLIT_LOCATION);
	}

	/**
	 * Set the breakpoint split location.
	 *
	 * @param location the breakpoint split location.
	 */
	public void setBreakpointsSplitLocation( int location) {
		set( BREAKPOINT_SPLIT_LOCATION, location);
	}

	public int getBreakpointsSplitLocation() {
		return getInteger( BREAKPOINT_SPLIT_LOCATION, DEFAULT_BREAKPOINT_SPLIT_LOCATION);
	}

	/**
	 * Set the dashboard split location.
	 *
	 * @param location the dashboard split location.
	 */
	public void setDashboardSplitLocation( int location) {
		set( DASHBOARD_SPLIT_LOCATION, location);
	}

	public int getDashboardSplitLocation() {
		return getInteger( DASHBOARD_SPLIT_LOCATION, DEFAULT_DASHBOARD_SPLIT_LOCATION);
	}

	/**
	 * Set the trace split location.
	 *
	 * @param location the trace split location.
	 */
	public void setTraceSplitLocation( int location) {
		set( TRACE_SPLIT_LOCATION, location);
	}

	public int getTraceSplitLocation() {
		return getInteger( TRACE_SPLIT_LOCATION, DEFAULT_TRACE_SPLIT_LOCATION);
	}
	
	/**
	 * The debugger should provide traces.
	 *
	 * @return true when the debugger provides tracing.
	 */
	public boolean isTracingEnabled() {
		return getBoolean( ENABLE_TRACING, true);
	}

	public void setTracingEnabled( boolean tracing) {
		set( ENABLE_TRACING, tracing);
	}

	/**
	 * The debugger should redirect the output.
	 *
	 * @return true when the debugger redirects the output.
	 */
	public boolean isRedirectOutput() {
		return getBoolean( REDIRECT_OUTPUT, true);
	}

	public void setRedirectOutput( boolean redirect) {
		set( REDIRECT_OUTPUT, redirect);
	}

	/**
	 * The debugger should provide traces.
	 *
	 * @return true when the debugger provides tracing.
	 */
	public boolean isAutomaticOpenInput() {
		return getBoolean( AUTOMATIC_OPEN_INPUT, true);
	}

	public void setAutomaticOpenInput( boolean enabled) {
		set( AUTOMATIC_OPEN_INPUT, enabled);
	}

	/**
	 * Show input overview margin.
	 *
	 * @return true when the overview margin should be visible.
	 */
	public boolean showInputOverviewMargin() {
		return getBoolean( SHOW_INPUT_OVERVIEW_MARGIN, true);
	}

	public void setShowInputOverviewMargin( boolean visible) {
		set( SHOW_INPUT_OVERVIEW_MARGIN, visible);
	}

	/**
	 * Show input linenumber margin.
	 *
	 * @return true when the linenumber margin should be visible.
	 */
	public boolean showInputLinenumberMargin() {
		return getBoolean( SHOW_INPUT_LINENUMBER_MARGIN, true);
	}

	public void setShowInputLinenumberMargin( boolean visible) {
		set( SHOW_INPUT_LINENUMBER_MARGIN, visible);
	}

	/**
	 * Show input folding margin.
	 *
	 * @return true when the folding margin should be visible.
	 */
	public boolean showInputFoldingMargin() {
		return getBoolean( SHOW_INPUT_FOLDING_MARGIN, true);
	}

	public void setShowInputFoldingMargin( boolean visible) {
		set( SHOW_INPUT_FOLDING_MARGIN, visible);
	}

	/**
	 * Wrap input.
	 *
	 * @return true when the input should be wrapped.
	 */
	public boolean isWrapInput() {
		return getBoolean( WRAP_INPUT, false);
	}

	public void setWrapInput( boolean wrap) {
		set( WRAP_INPUT, wrap);
	}

	/**
	 * Show style overview margin.
	 *
	 * @return true when the overview margin should be visible.
	 */
	public boolean showStyleOverviewMargin() {
		return getBoolean( SHOW_STYLE_OVERVIEW_MARGIN, true);
	}

	public void setShowStyleOverviewMargin( boolean visible) {
		set( SHOW_STYLE_OVERVIEW_MARGIN, visible);
	}

	/**
	 * Show style linenumber margin.
	 *
	 * @return true when the linenumber margin should be visible.
	 */
	public boolean showStyleLinenumberMargin() {
		return getBoolean( SHOW_STYLE_LINENUMBER_MARGIN, true);
	}

	public void setShowStyleLinenumberMargin( boolean visible) {
		set( SHOW_STYLE_LINENUMBER_MARGIN, visible);
	}

	/**
	 * Show style folding margin.
	 *
	 * @return true when the folding margin should be visible.
	 */
	public boolean showStyleFoldingMargin() {
		return getBoolean( SHOW_STYLE_FOLDING_MARGIN, true);
	}

	public void setShowStyleFoldingMargin( boolean visible) {
		set( SHOW_STYLE_FOLDING_MARGIN, visible);
	}

	/**
	 * Wrap style.
	 *
	 * @return true when the style should be wrapped.
	 */
	public boolean isWrapStyle() {
		return getBoolean( WRAP_STYLE, false);
	}

	public void setWrapStyle( boolean wrap) {
		set( WRAP_STYLE, wrap);
	}

	/**
	 * Show output linenumber margin.
	 *
	 * @return true when the linenumber margin should be visible.
	 */
	public boolean showOutputLinenumberMargin() {
		return getBoolean( SHOW_OUTPUT_LINENUMBER_MARGIN, true);
	}

	public void setShowOutputLinenumberMargin( boolean visible) {
		set( SHOW_OUTPUT_LINENUMBER_MARGIN, visible);
	}

	/**
	 * Wrap output.
	 *
	 * @return true when the output should be wrapped.
	 */
	public boolean isWrapOutput() {
		return getBoolean( WRAP_OUTPUT, false);
	}

	public void setWrapOutput( boolean wrap) {
		set( WRAP_OUTPUT, wrap);
	}
	
	public boolean isWindowMaximised() {
		return getBoolean( WINDOW_MAXIMISED, true);
	}
	
	public void setWindowMaximised(boolean maximised) {
		set( WINDOW_MAXIMISED, maximised);
	}
}