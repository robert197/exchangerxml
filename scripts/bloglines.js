server   = 'rpc.bloglines.com';


email = 
  Packages.javax.swing.JOptionPane.showInputDialog(null, "Email address:", "Log in to Bloglines", 
			      Packages.javax.swing.JOptionPane.QUESTION_MESSAGE);

password = 
  Packages.javax.swing.JOptionPane.showInputDialog(null, "Password:", "Log in to Bloglines", 
			      Packages.javax.swing.JOptionPane.QUESTION_MESSAGE);

client = new Packages.org.apache.commons.httpclient.HttpClient();
credentials = new Packages.org.apache.commons.httpclient.UsernamePasswordCredentials(email, password);

client.getState().setCredentials("Bloglines RPC", server, credentials);

listsubs   = "http://rpc.bloglines.com/listsubs";


function callBloglines(url) {
    get = new Packages.org.apache.commons.httpclient.methods.GetMethod(url);
    get.setDoAuthentication(true);
    client.executeMethod(get);
    return get.getResponseBodyAsString();
}

out = callBloglines(listsubs);

var opml = new XML(out);



var root = new Packages.javax.swing.tree.DefaultMutableTreeNode("My Feeds");


function parseOutline(parsedXml, treeLevel) {

for each (test in parsedXml.outline)
{
  folderFlag = true;
  for each (var attr in test.@*) 
  {
    if (attr.name()=='xmlUrl')
      folderFlag = false;
  } 
		
		if (folderFlag == false)
		{
	  print("Sub: " + test.@title +  "  " + test.@BloglinesSubId);
   treeLevel.add(new Packages.javax.swing.tree.DefaultMutableTreeNode(test.@title));

	 }
	 else
	 {
	 	print("Folder: " + test.@title);
	  folder = Packages.javax.swing.tree.DefaultMutableTreeNode(test.@title);
	  parseOutline(test, folder);
	  treeLevel.add(folder);
	 }	 
 }
}


parseOutline(opml.body.outline, root);



var controller = exchanger.getControllerTabbedPane();

var model = new Packages.javax.swing.tree.DefaultTreeModel(root);
var tree = new Packages.javax.swing.JTree(model);
var scroller = new Packages.javax.swing.JScrollPane(tree);

controller.addTab("tree3", tree);




