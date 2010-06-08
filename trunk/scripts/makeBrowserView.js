//make sure the view doesnt already exist
exchanger.removeUserView("Browser");

//make 1st view
newJPanel = Packages.javax.swing.JPanel(new Packages.java.awt.BorderLayout());

//make the browser and set the initial url
browser = new Packages.org.jdesktop.jdic.browser.WebBrowser();
browser.setURL(new Packages.java.net.URL("http://www.google.ie"));


browser.addWebBrowserListener(
    new JavaAdapter(
        Packages.org.jdesktop.jdic.browser.WebBrowserListener,
        {documentCompleted:function (event) {
            progressBar.setIndeterminate(false);

        }}
    )
    
    
);

browser.addWebBrowserListener(
    new JavaAdapter(
        Packages.org.jdesktop.jdic.browser.WebBrowserListener,
        {downloadCompleted:function (event) {
            progressBar.setIndeterminate(false);

        }}
    )
    
    
);

browser.addWebBrowserListener(
    new JavaAdapter(
        Packages.org.jdesktop.jdic.browser.WebBrowserListener,
        {downloadError:function (event) {
            progressBar.setIndeterminate(false);

        }}
    )
    
    
);

browser.addWebBrowserListener(
    new JavaAdapter(
        Packages.org.jdesktop.jdic.browser.WebBrowserListener,
        {downloadProgress:function (event) {
            
        }}
    )
    
    
);

browser.addWebBrowserListener(
    new JavaAdapter(
        Packages.org.jdesktop.jdic.browser.WebBrowserListener,
        {downloadStarted:function (event) {
            progressBar.setIndeterminate(true);

        }}
    )
    
    
);


//this updates the address bar when you open a new page
browser.addWebBrowserListener(
        new JavaAdapter(
            Packages.org.jdesktop.jdic.browser.WebBrowserListener,
            {statusTextChange:function (event) {
                addressTextField.setText(browser.getURL());

            }}
        )
        
        
    );
    

//this updates the address bar when you open a new page
browser.addWebBrowserListener(
        new JavaAdapter(
            Packages.org.jdesktop.jdic.browser.WebBrowserListener,
            {titleChange:function (event) {
                //addressTextField.setText(browser.getURL());

            }}
        )
        
        
    );





//make an address bar
addressPanel = new Packages.javax.swing.JPanel(new Packages.org.bounce.FormLayout(0,0));

addressLabel = new Packages.javax.swing.JLabel("Address: ");
addressTextField = new Packages.javax.swing.JTextField("http://www.google.ie");

addressTextField.addActionListener(
        new JavaAdapter(
            Packages.java.awt.event.ActionListener,
            {actionPerformed:function (event) {
                browser.setURL(new Packages.java.net.URL(addressTextField.getText()));
            }}
        )
    );

addressPanel.add(addressLabel, Packages.org.bounce.FormLayout.LEFT);
addressPanel.add(addressTextField, Packages.org.bounce.FormLayout.RIGHT_FILL);

//progressBar.setIndeterminate(true);

progressBar = new Packages.javax.swing.JProgressBar();
addressAndProgressPanel = new Packages.javax.swing.JPanel(new Packages.java.awt.BorderLayout());

addressAndProgressPanel.add(addressPanel, Packages.java.awt.BorderLayout.CENTER);
addressAndProgressPanel.add(progressBar, Packages.java.awt.BorderLayout.EAST);

//make a toolbar for all the standard browser buttons
toolbar = new Packages.javax.swing.JToolBar();

backButton = new Packages.javax.swing.JButton("Back");
backButton.addActionListener(
        new JavaAdapter(
            Packages.java.awt.event.ActionListener,
            {actionPerformed:function (event) {
                browser.back();
            }}
        )
    );

toolbar.add(backButton);

forwardButton = new Packages.javax.swing.JButton("Forward");
forwardButton.addActionListener(
        new JavaAdapter(
            Packages.java.awt.event.ActionListener,
            {actionPerformed:function (event) {
                browser.forward();
            }}
        )
    );

toolbar.add(forwardButton);

refreshButton = new Packages.javax.swing.JButton("Refresh");
refreshButton.addActionListener(
        new JavaAdapter(
            Packages.java.awt.event.ActionListener,
            {actionPerformed:function (event) {
                browser.refresh();
                progressBar.setIndeterminate(false);
                
            }}
        )
    );

toolbar.add(refreshButton);

stopButton = new Packages.javax.swing.JButton("Stop");
stopButton.addActionListener(
        new JavaAdapter(
            Packages.java.awt.event.ActionListener,
            {actionPerformed:function (event) {
                browser.stop();
                progressBar.setIndeterminate(false);
            }}
        )
    );

toolbar.add(stopButton);

controlPanel = new Packages.javax.swing.JPanel(new Packages.java.awt.BorderLayout());
controlPanel.add(addressAndProgressPanel, Packages.java.awt.BorderLayout.NORTH);
controlPanel.add(toolbar, Packages.java.awt.BorderLayout.SOUTH);

newJPanel.add(controlPanel, Packages.java.awt.BorderLayout.NORTH);
newJPanel.add(browser, Packages.java.awt.BorderLayout.CENTER);
exchanger.makeUserView(newJPanel, "Browser");

