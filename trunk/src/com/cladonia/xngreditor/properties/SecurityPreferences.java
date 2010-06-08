/*
 * $Id: SecurityPreferences.java,v 1.2 2004/04/22 10:14:03 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.properties;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.properties.Properties;
import com.cladonia.xml.properties.PropertiesFile;
import com.cladonia.xngreditor.StringUtilities;

/**
 * Handles the Security Preferences.
 *
 * @version	$Revision: 1.2 $, $Date: 2004/04/22 10:14:03 $
 * @author Dogsbay
 */
public class SecurityPreferences extends PropertiesFile {

	public static final String SECURITY_PREFERENCES		= "security-preferences";

	public final static String JKS_KEYSTORE_TYPE		= "JKS";

	private final static String DEFAULT_KEYSTORE_TYPE	= JKS_KEYSTORE_TYPE;

	private final static String KEYSTORE_TYPE			= "keystore-type";

	private final static String DEFAULT_KEYSTORE_FILE	= "keystore.jks";
	private static final String KEYSTORE_FILE			= "keystore-file";

	private final static String DEFAULT_KEYSTORE_PASSWORD	= "xmlexchanger";
	private static final String KEYSTORE_PASSWORD			= "keystore-password";
	
	private final static String DEFAULT_PRIVATEKEY_ALIAS	= "exchanger";
	private static final String PRIVATEKEY_ALIAS			= "privatekey-alias";

	private final static String DEFAULT_PRIVATEKEY_PASSWORD	= "xmlexchanger";
	private static final String PRIVATEKEY_PASSWORD			= "privatekey-password";

	private final static String DEFAULT_CERTIFICATE_ALIAS	= "exchanger";
	private static final String CERTIFICATE_ALIAS			= "certificate-alias";

	/**
	 * Creates the Security Preferences.
	 *
	 * @param element the security preferences element.
	 */
	public SecurityPreferences(String fileName, String rootName) {
		super(loadPropertiesFile(fileName, rootName));
	}

	/*
	 * Get/Set the key store type.
	 */
	public String getKeystoreType() {
		return getText( KEYSTORE_TYPE, DEFAULT_KEYSTORE_TYPE);
	}

	public void setKeystoreType( String type) {
		set( KEYSTORE_TYPE, type);
	}

	/*
	 * Get/Set the key store file location.
	 */
	public String getKeystoreFile() {
		return getText( KEYSTORE_FILE, DEFAULT_KEYSTORE_FILE);
	}

	public void setKeystoreFile( String type) {
		set( KEYSTORE_FILE, type);
	}

	/*
	 * Get/Set the key store password.
	 */
	public String getKeystorePassword() {
		String text = getText( KEYSTORE_PASSWORD);
		
		if ( text != null && text.length() > 0) {
			return StringUtilities.decrypt( text);
		} else {
			return DEFAULT_KEYSTORE_PASSWORD;
		}
	}

	public void setKeystorePassword( char[] password) {
		set( KEYSTORE_PASSWORD, StringUtilities.encrypt( password));
	}
	
	/*
	 * Get/Set the private key alias.
	 */
	public String getPrivatekeyAlias() {
		return getText( PRIVATEKEY_ALIAS, DEFAULT_PRIVATEKEY_ALIAS);
	}

	public void setPrivatekeyAlias( String type) {
		set( PRIVATEKEY_ALIAS, type);
	}

	/*
	 * Get/Set the private key password.
	 */
	public String getPrivatekeyPassword() {
		String text = getText( PRIVATEKEY_PASSWORD);
		
		if ( text != null && text.length() > 0) {
			return StringUtilities.decrypt( text);
		} else {
			return DEFAULT_PRIVATEKEY_PASSWORD;
		}
	}

	public void setPrivatekeyPassword( char[] password) {
		set( PRIVATEKEY_PASSWORD, StringUtilities.encrypt( password));
	}

	/*
	 * Get/Set the certificate alias.
	 */
	public String getCertificateAlias() {
		return getText( CERTIFICATE_ALIAS, DEFAULT_CERTIFICATE_ALIAS);
	}

	public void setCertificateAlias( String type) {
		set( CERTIFICATE_ALIAS, type);
	}
} 
