/*
 * $Id: KeyBuilder.java,v 1.1 2004/03/25 18:41:05 edankert Exp $
 *
 * Copyright (C) 2002-2003, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.security.signature;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;


/**
 * loads the keystore and allows access to a particular private key and certificate. 
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:41:05 $
 * @author Dogs bay
 */
public class KeyBuilder {
	
	private static KeyStore keystore = null;
	private static PrivateKey pkey = null;
	private static X509Certificate cert = null;
	
	
	/**
 	 * Sets the required paramters to load the keystore, and also stores the required private
 	 * key and certicate
 	 *
 	 * @param keystoreType the type of keystore (JKS,JCEKS and PKCS12)
 	 * @param keystoreFile path to the keystore file
 	 * @param keystorePass password for the keystore (only proves that the keystore has not been 
 	 * tampered with i.e hasing mechanism, if you dont supply a password the keystore is still 
 	 * accessable)
 	 * @param privateKeyAlias the alias of the private key 
 	 * @param privateKeyAlias the password for the private key
 	 * @param certificateAlias the alias of the public key
 	 */
	public static void setParams(String keystoreType, String keystoreFile,String keystorePass,
		String privateKeyAlias, String privateKeyPass, String certificateAlias) throws Exception
	{
		KeyStore ks = KeyStore.getInstance(keystoreType);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(keystoreFile));
		
		// load the keystore
	    ks.load(bis, keystorePass.toCharArray());
	    keystore = ks;
	    
	    // get the private key
	    pkey = (PrivateKey) ks.getKey(privateKeyAlias,privateKeyPass.toCharArray());
	    
	    // get the certificate
	    if (certificateAlias != null)
	    	cert = (X509Certificate) ks.getCertificate(certificateAlias);
	    else
	    	cert = (X509Certificate) ks.getCertificate(privateKeyAlias);
	}
	
	
	/**
	 * returns the private key, as long as the keystore and private parameters were previously
	 * configured correctly (i.e quasi singleton)
	 * 
	 * @return the private key
	 */
	public static PrivateKey getPrivateKey()
	{
		if(pkey!=null)
            return pkey;
        else
            return null;
	}
	
	/**
	 * returns the certificate, as long as the keystore and certificate parameters were previously
	 * configured correctly 
	 * 
	 * @return the certificate
	 */
	public static X509Certificate getCertificate()
	{
		if(cert!=null)
            return cert;
        else
            return null;
	}
	
	/**
	 * returns the keystore, as long as the keystore parameters were previously configured
	 * correctly
	 * 
	 * @return the keystore
	 */
	public static KeyStore getKeyStore()  
    {
        if(keystore!=null)
            return keystore;
        else
            return null;
    }
	
	
	
	
	
}
