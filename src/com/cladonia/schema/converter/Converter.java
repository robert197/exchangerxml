/*
 * $Id: Converter.java,v 1.1 2004/03/25 18:37:50 edankert Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.schema.converter;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cladonia.schema.converter.input.XmlInputFormat;
import com.thaiopensource.relaxng.edit.SchemaCollection;
import com.thaiopensource.relaxng.input.InputFailedException;
import com.thaiopensource.relaxng.input.InputFormat;
import com.thaiopensource.relaxng.input.dtd.DtdInputFormat;
import com.thaiopensource.relaxng.input.parse.compact.CompactParseInputFormat;
import com.thaiopensource.relaxng.input.parse.sax.SAXParseInputFormat;
import com.thaiopensource.relaxng.output.LocalOutputDirectory;
import com.thaiopensource.relaxng.output.OutputDirectory;
import com.thaiopensource.relaxng.output.OutputFailedException;
import com.thaiopensource.relaxng.output.OutputFormat;
import com.thaiopensource.relaxng.output.dtd.DtdOutputFormat;
import com.thaiopensource.relaxng.output.rnc.RncOutputFormat;
import com.thaiopensource.relaxng.output.rng.RngOutputFormat;
import com.thaiopensource.relaxng.output.xsd.XsdOutputFormat;
import com.thaiopensource.relaxng.translate.util.InvalidParamsException;

/**
 * Convert DTD or relax ng to Schema.
 *
 * @version	$Revision: 1.1 $, $Date: 2004/03/25 18:37:50 $
 * @author Dogsbay
 */
public class Converter {
	public static final int TYPE_XML = 0;
	public static final int TYPE_RNC = 1;
	public static final int TYPE_RNG = 2;
	public static final int TYPE_DTD = 3;
	public static final int TYPE_XSD = 4;

	private static final int DEFAULT_LINE_LENGTH		= 72;
	private static final int DEFAULT_INDENT				= 2;
	private static final String DEFAULT_OUTPUT_ENCODING	= "UTF-8";

	public static void convertDTD2XSD( URL inputURL, File outputDir) throws IOException, SAXException {
		convert( inputURL, TYPE_DTD, outputDir, TYPE_XSD);
	}

	public static void inferSchema( InputSource source, File outputFile) throws IOException, SAXException {
		DefaultRelaxErrorHandler handler = new DefaultRelaxErrorHandler();
	
		try {
			XmlInputFormat inputFormat 	= new XmlInputFormat();
			OutputFormat outputFormat	= new XsdOutputFormat();

			SchemaCollection schemaCollection = inputFormat.load( source, new String[0], getExtension( TYPE_XSD), handler);
		
			OutputDirectory od = new LocalOutputDirectory( schemaCollection.getMainUri(), outputFile, getExtension( TYPE_XSD), DEFAULT_OUTPUT_ENCODING, DEFAULT_LINE_LENGTH, DEFAULT_INDENT);
		
			outputFormat.output( schemaCollection, od, new String[0], getExtension( TYPE_XML), handler);
		} catch (OutputFailedException e) {
			System.err.println( "ERROR: Could not create Output!");
		} catch (InputFailedException e) {
			System.err.println( "ERROR: Could not read Input!");
		} catch (InvalidParamsException e) {
		}
	}

	public static void convert( URL inputURL, int inputType, File outputFile, int outputType) throws IOException, SAXException {
		DefaultRelaxErrorHandler handler = new DefaultRelaxErrorHandler();

		try {
			InputFormat inputFormat 	= getInputFormat( inputType);
			OutputFormat outputFormat	= getOutputFormat( outputType);

			SchemaCollection schemaCollection = inputFormat.load( inputURL.toString(), new String[0], getExtension( outputType), handler);
			
		
			OutputDirectory od = new LocalOutputDirectory( schemaCollection.getMainUri(), outputFile, getExtension( outputType), DEFAULT_OUTPUT_ENCODING, DEFAULT_LINE_LENGTH, DEFAULT_INDENT);
		
			outputFormat.output( schemaCollection, od, new String[0], getExtension( inputType), handler);
		} catch (OutputFailedException e) {
			System.err.println( "ERROR: Could not create Output!");
		} catch (InputFailedException e) {
			System.err.println( "ERROR: Could not read Input!");
		} catch (InvalidParamsException e) {
		}
	}

//	private void error(String message) {
//		eh.printException(new SAXException(message));
//	}

	static private String extension(String s) {
		int dot = s.lastIndexOf(".");
	
		if (dot < 0)
			return "";
	
		return s.substring(dot);
	}
	
	private static InputFormat getInputFormat( int type) {
		InputFormat format = null;
		
		if ( type == TYPE_RNG) {
			format = new SAXParseInputFormat();
		} else if ( type == TYPE_RNC) {
			format = new CompactParseInputFormat();
		} else if ( type == TYPE_DTD) {
			format = new DtdInputFormat();
		} else if ( type == TYPE_XML) {
			format = new XmlInputFormat();
		} 
		
		return format;
	}
	
	private static OutputFormat getOutputFormat( int type) {
		OutputFormat format = null;
		
		if ( type == TYPE_DTD) {
			format = new DtdOutputFormat();
		} else if ( type == TYPE_RNG) {
			format = new RngOutputFormat();
		} else if ( type == TYPE_XSD) {
			format = new XsdOutputFormat();
		} else if ( type == TYPE_RNC) {
			format = new RncOutputFormat();
		}
		
		return format;
	}

	private static String getExtension( int type) {
		String result = ".xsd";
		
		if ( type == TYPE_RNG) {
			result = ".rng";
		} else if ( type == TYPE_RNC) {
			result = ".rnc";
		} else if ( type == TYPE_DTD) {
			result = ".dtd";
		} else if ( type == TYPE_XML) {
			result = ".xml";
		}
		
		return result;
	}

//	private int doMain(String[] args) {
//		List inputParams = new Vector();
//		List outputParams = new Vector();
//	
//		try {
//			OptionParser op = new OptionParser("I:O:i:o:", args);
//			try {
//				while (op.moveToNextOption()) {
//					switch (op.getOptionChar()) {
//					case 'I':
//						inputType = op.getOptionArg();
//						break;
//					case 'O':
//						outputType = op.getOptionArg();
//						break;
//					case 'i':
//						inputParams.add(op.getOptionArg());
//						break;
//					case 'o':
//						outputParams.add(op.getOptionArg());
//						break;
//					}
//				}
//			} catch (OptionParser.InvalidOptionException e) {
//				error(localizer.message("invalid_option", op.getOptionCharString()));
//				return 2;
//			} catch (OptionParser.MissingArgumentException e) {
//				error(localizer.message("option_missing_argument", op.getOptionCharString()));
//				return 2;
//			}
//			args = op.getRemainingArgs();
//		
//			if (args.length < 2) {
//				error(localizer.message("too_few_arguments"));
//				eh.print(localizer.message("usage", Version.getVersion(Driver.class)));
//				return 2;
//			}
//		
//			if (inputType == null) {
//				inputType = extension(args[0]);
//		
//				if (inputType.length() > 0)
//					inputType = inputType.substring(1);
//				}
//		
//				InputFormat inFormat;
//		
//				if (inputType.equalsIgnoreCase("rng"))
//					inFormat = new SAXParseInputFormat();
//				else if (inputType.equalsIgnoreCase("rnc"))
//					inFormat = new CompactParseInputFormat();
//				else if (inputType.equalsIgnoreCase("dtd"))
//					inFormat = new DtdInputFormat();
//				else if (inputType.equalsIgnoreCase("xml"))
//					inFormat = new XmlInputFormat();
//				else {
//					error(localizer.message("unrecognized_input_type", inputType));
//					return 2;
//				}
//			
//				OutputFormat of;
//				String ext = extension(args[args.length - 1]);
//		
//				if (outputType == null) {
//					outputType = ext;
//					
//					if (outputType.length() > 0)
//						outputType = outputType.substring(1);
//				}
//		
//				if (outputType.equalsIgnoreCase("dtd"))
//					of = new DtdOutputFormat();
//				else if (outputType.equalsIgnoreCase("rng"))
//					of = new RngOutputFormat();
//				else if (outputType.equalsIgnoreCase("xsd"))
//					of = new XsdOutputFormat();
//				else if (outputType.equalsIgnoreCase("rnc"))
//					of = new RncOutputFormat();
//				else {
//					error(localizer.message("unrecognized_output_type", outputType));
//					return 2;
//				}
//				String[] inputParamArray = (String[])inputParams.toArray(new String[0]);
//				outputType = outputType.toLowerCase();
//				SchemaCollection sc;
//		
//				if (args.length > 2) {
//					if (!(inFormat instanceof MultiInputFormat)) {
//						error(localizer.message("too_many_arguments"));
//						return 2;
//				}
//
//				String[] uris = new String[args.length - 1];
//				for (int i = 0; i < uris.length; i++)
//					uris[i] = UriOrFile.toUri(args[i]);
//		
//				sc = ((MultiInputFormat)inFormat).load(uris, inputParamArray, outputType, eh);
//			} else
//				sc = inFormat.load(UriOrFile.toUri(args[0]), inputParamArray, outputType, eh);
//		
//			if (ext.length() == 0)
//				ext = outputType;
//			
//			OutputDirectory od = new LocalOutputDirectory(sc.getMainUri(),
//			                        new File(args[args.length - 1]),
//			                        ext,
//			                        DEFAULT_OUTPUT_ENCODING,
//			                        DEFAULT_LINE_LENGTH,
//			                        DEFAULT_INDENT);
//			of.output(sc, od, (String[])outputParams.toArray(new String[0]), inputType.toLowerCase(), eh);
//			return 0;
//		} catch (OutputFailedException e) {
//		} catch (InputFailedException e) {
//		} catch (InvalidParamsException e) {
//		} catch (IOException e) {
//			eh.printException(e);
//		} catch (SAXException e) {
//			eh.printException(e);
//		}
//		
//		return 1;
//	}

	private static class DefaultRelaxErrorHandler implements ErrorHandler {

	    public void warning( SAXParseException exception) throws SAXParseException {
			System.err.println( "WARNING: "+exception.getMessage());
			// throw exception;
	    } 

	    public void error( SAXParseException exception) throws SAXParseException {
//	        System.out.println( "ERROR: "+exception.getMessage());
	        throw exception;
	    } 

	    public void fatalError( SAXParseException exception) throws SAXParseException {
	        throw exception;
	    } 
	}
}
