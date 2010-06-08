//var url = new java.net.URL('file:test.xsd'); 
//var instance = exchanger.generateInstanceFromSchemaURL(url, 'document1', false, false, false); 
//exchanger.openNewDocument(instance);
//var selected = exchanger.getSelection();
//exchanger.replaceSelection(selected.toUpperCase());


var saxon1 = Packages.com.cladonia.xngreditor.api.Exchanger.XSLT_PROCESSOR_SAXON_XSLT1;
var params = new java.util.Properties();
params.setProperty("testparam", "help");

var filename1 = "test.xml";
var output = exchanger.executeXSLT(filename1, "test.xsl", "${file}.htm", saxon1, params);
//exchanger.openNewDocument(output);            

var dir = new java.io.File("e:/top/dev/xngr-editor");

var children = dir.list();

for (var i=0; i<children.length; i++) 
{
	// Get filename of file or directory
	var fn = children[i];
	if (fn != null  && fn != undefined && fn.startsWith("test") && fn.endsWith(".xml"))
	{     
		var output = exchanger.executeXSLT(fn, "test.xsl", "${file}.htm", saxon1, params);
		java.lang.System.out.println("transformed " + fn);
		//exchanger.openNewDocument(output);            
	}
}


/*
if (children == null) {
        // Either dir does not exist or is not a directory
} else {
        for (var i=0; i<children.length; i++) {
            // Get filename of file or directory
            var filename = children[i];
            
            if (filename.endsWith(".xml")
            {
													//var output = exchanger.executeXSLT(filename, "test.xsl", "${file}.htm", saxon1, params);
													//exchanger.openNewDocument(output);            
												}
        }
}
*/ 

