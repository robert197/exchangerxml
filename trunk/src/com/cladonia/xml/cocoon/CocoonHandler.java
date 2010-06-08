package com.cladonia.xml.cocoon;

//import java.io.File;
//import java.util.Arrays;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;

//import org.apache.cocoon.Constants;
//
//import org.apache.cocoon.bean.CocoonBean;
//import org.apache.cocoon.bean.helpers.OutputStreamListener;
//import org.apache.cocoon.bean.helpers.BeanConfigurator;
//
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Option;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.PosixParser;

//import org.w3c.dom.Document;
//import org.w3c.dom.NamedNodeMap;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

/**
 * Command line entry point. Parses command line, create Cocoon bean and invokes it
 * with file destination.
 *
 * @author <a href="mailto:stefano@apache.org">Stefano Mazzocchi</a>
 * @author <a href="mailto:nicolaken@apache.org">Nicola Ken Barozzi</a>
 * @author <a href="mailto:vgritsenko@apache.org">Vadim Gritsenko</a>
 * @author <a href="mailto:uv@upaya.co.uk">Upayavira</a>
 * @version CVS $Id: CocoonHandler.java,v 1.1 2004/03/25 18:42:18 edankert Exp $
 */
public class CocoonHandler {

//    private static OutputStreamListener listener;

    /**
     * The <code>main</code> method.
     *
     * @param args a <code>String[]</code> of arguments
     * @exception Exception if an error occurs
     */
    public static void start() throws Exception {

//        listener = new OutputStreamListener( System.out);
//        CocoonBean cocoon = new CocoonBean();
//        cocoon.addListener( listener);
//
//		String destDir = configure( cocoon, listener);
//
//        listener.messageGenerated( CocoonBean.getProlog());
//
//        cocoon.initialize();
//        cocoon.process();
//        cocoon.dispose();
//
//        listener.complete();
    }

	public static void main( String[] args) {
//		try {
//			CocoonHandler.start();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		System.exit(0);
	}
	
//	private static String configure( CocoonBean cocoon, OutputStreamListener listener) 
//	    throws IllegalArgumentException {

//	    String destDir = "build/dest";
//
//        cocoon.setVerbose( true);
//        cocoon.setFollowLinks( true);
//        cocoon.setPrecompileOnly( false);
//        cocoon.setConfirmExtensions( true);
//        cocoon.setContextDir( "build/webapp");
//        cocoon.setWorkDir( "build/work");
//        cocoon.setConfigFile( "WEB-INF/cocoon.xconf");
//		
//		// broken links...
//	    listener.setReportFile( "brokenlinks.xml");
//	    listener.setReportType( "xml");
//		cocoon.setBrokenLinkGenerate( false);
//		cocoon.setBrokenLinkExtension( ".error");
//		
//		// loaded classes...
//		cocoon.addLoadedClass( "com.mysql.jdbc.Driver");
//		
//		// logging...
//	    cocoon.setLogKit( "build/webapp/WEB-INF/logkit.xconf");
//	    cocoon.setLogger( "cli");
//	    cocoon.setLogLevel( "DEBUG");
//		
      //cocoon.setChecksumURI( "wherever/whatever");
//
      //cocoon.setAgentOptions();
//
//        cocoon.setAcceptOptions( "*/*");
//
//		cocoon.setDefaultFilename( "index.html");
//
//		cocoon.addIncludePattern( "**");
//
		//cocoon.addExcludePattern( "docs/apidocs/**");
		//cocoon.addIncludeLinkExtension( ".html");
//		
//		// URI ...
//
//		String src = "test-sql.xml";  // "sql-1/test.html"
//		String type = "append"; // "replace"
//		String root = "test/sql-1/"; // "test/"
//		String dest = "build/dest/test/sql-1/"; // "build/test/sql-1/"
//
//	    if (root != null && type != null & dest != null) {
//	        cocoon.addTarget(type, root, src, dest);
//	    } else if (root != null & dest != null) {
//	        cocoon.addTarget(root, src, dest);
//	    } else if (dest != null) {
//	        cocoon.addTarget(src, dest);
//	    } else {
//	        cocoon.addTarget(src, destDir);
//	    }
//
//		// URIs
//
//	    return destDir;
//	}
}
