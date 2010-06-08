/*
 * $Id: ExchangerOutputFormat.java,v 1.1 2004/05/28 09:14:00 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd. 
 * Use is subject to license terms.
 */

package com.cladonia.xml;

import org.dom4j.io.OutputFormat;

/**
 * This ExchangerOutputFormat is used to ...
 *
 * @version $Revision: 1.1 $, $Date: 2004/05/28 09:14:00 $
 * @author Dogsbay
 */
public class ExchangerOutputFormat extends OutputFormat {
	private String version = "1.0";
	private boolean omitStandalone = true;
	private String standalone = null;

	public ExchangerOutputFormat() {
		super();
	}

	public ExchangerOutputFormat( String arg0) {
		super(arg0);
	}

	public ExchangerOutputFormat( String arg0, boolean arg1) {
		super(arg0, arg1);
	}

	public ExchangerOutputFormat(String arg0, boolean arg1, String arg2) {
		super(arg0, arg1, arg2);
	}
	
	public String getVersion() {
		return version;
	}

	public void setVersion( String version) {
		this.version = version;
	}

	public String getStandalone() {
		return standalone;
	}

	public void setStandalone( String standalone) {
		this.standalone = standalone;
	}
	
	public boolean isOmitStandalone() {
		return omitStandalone;
	}

	public void setOmitStandalone( boolean omit) {
		omitStandalone = omit;
	}
}
