/*
 * $Id: Project.java,v 1.11 2005/09/05 09:14:18 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor.project;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.bounce.QTree;
import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;
import org.xml.sax.SAXParseException;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xml.XElement;
import com.cladonia.xml.XngrURLUtilities;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.MessageHandler;
import com.cladonia.xngreditor.URLUtilities;
import com.cladonia.xngreditor.XngrProgressDialog;
import com.cladonia.xngreditor.grammar.GrammarProperties;
import com.cladonia.xngreditor.project.actions.AddDirectoryAction;
import com.cladonia.xngreditor.project.actions.AddDirectoryContentsAction;
import com.cladonia.xngreditor.project.actions.AddFileAction;
import com.cladonia.xngreditor.project.actions.AddFolderAction;
import com.cladonia.xngreditor.project.actions.AddRemoteDocumentAction;
import com.cladonia.xngreditor.project.actions.AddVirtualDirectoryAction;
import com.cladonia.xngreditor.project.actions.DeleteAction;
import com.cladonia.xngreditor.project.actions.DeleteProjectAction;
import com.cladonia.xngreditor.project.actions.FindInProjectsAction;
import com.cladonia.xngreditor.project.actions.ImportProjectAction;
import com.cladonia.xngreditor.project.actions.NewProjectAction;
import com.cladonia.xngreditor.project.actions.OpenFileAction;
import com.cladonia.xngreditor.project.actions.ParseAction;
import com.cladonia.xngreditor.project.actions.ProjectPropertiesAction;
import com.cladonia.xngreditor.project.actions.RefreshVirtualDirectoryAction;
import com.cladonia.xngreditor.project.actions.RemoveFileAction;
import com.cladonia.xngreditor.project.actions.RemoveFolderAction;
import com.cladonia.xngreditor.project.actions.RenameFolderAction;
import com.cladonia.xngreditor.project.actions.RenameProjectAction;
import com.cladonia.xngreditor.project.actions.ValidateAction;
import com.cladonia.xngreditor.properties.ConfigurationProperties;
import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.cladonia.xngreditor.template.TemplateProperties;

/**
 * The categories panel.
 *
 * @version	$Revision: 1.11 $, $Date: 2005/09/05 09:14:18 $
 * @author Dogsbay
 */
public class Project extends JPanel implements TreeWillExpandListener  {
	private ExchangerEditor parent = null;

	private QTree tree = null;
	private JScrollPane scrollPane = null;
	private DefaultTreeModel treeModel = null;

	private DefaultMutableTreeNode root = null;
	
	private ConfigurationProperties properties = null; 

	private JPopupMenu filePopup = null;
	private JPopupMenu folderPopup = null;
	private JPopupMenu projectPopup = null;
	private JPopupMenu virtualDirectoryPopup = null;
	
	private ValidateAction validateAction = null;
	private ParseAction parseAction = null;
	
	private AddDirectoryAction addDirectoryAction = null;
	private AddDirectoryContentsAction addDirectoryContentsAction = null;

	private AddFileAction addFileAction = null;
	private FindInProjectsAction findInProjectsAction = null;
	private AddRemoteDocumentAction addRemoteDocumentAction = null;
	private OpenFileAction openFileAction = null;
	private RemoveFileAction removeFileAction = null;

	private AddFolderAction addFolderAction = null;
	private RemoveFolderAction removeFolderAction = null;
	private RenameFolderAction renameFolderAction = null;
	private RenameProjectAction renameProjectAction = null;

	private DeleteProjectAction deleteProjectAction = null;
	private NewProjectAction newProjectAction = null;
	private ImportProjectAction importProjectAction = null;
	private ProjectPropertiesAction projectPropertiesAction = null;

	private DeleteAction deleteAction = null;

	private AddVirtualDirectoryAction addVirtualDirectoryAction;

	private RefreshVirtualDirectoryAction refreshVirtualDirectoryAction;

	

	/**
	 * Constructs an explorer view with the ExplorerProperties supplied.
	 *
	 * @param root the root node.
	 */
	public Project( ExchangerEditor parent, ConfigurationProperties props) {
		super( new BorderLayout());
		
		properties = props;
		this.parent = parent;
		
		this.root = new DefaultMutableTreeNode();
		
		// Create the tree:
		treeModel = new DefaultTreeModel( root);

		tree = new ProjectTree( treeModel);
		tree.setBorder( new EmptyBorder( 2, 2, 2, 2));
		
		ToolTipManager.sharedInstance().registerComponent( tree);

		tree.putClientProperty( "JTree.lineStyle", "None");
		tree.setEditable( false);
		tree.setShowsRootHandles( true);
		tree.setRootVisible( false);
		tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setEditable( true);

		ProjectCellRenderer renderer = new ProjectCellRenderer();
		tree.setCellRenderer( renderer);
		tree.setRootVisible( false);
		ProjectCellEditor editor = new ProjectCellEditor( tree, renderer);
		editor.addCellEditorListener( new CellEditorListener() {
			public void editingCanceled( ChangeEvent e) {
				Project.this.editingStopped();
			}
			public void editingStopped( ChangeEvent e) {
				Project.this.editingStopped();
			}
		});
		tree.setCellEditor( editor);
		tree.setExpandsSelectedPaths( true);

		tree.addTreeSelectionListener( new TreeSelectionListener() {
			public void valueChanged( TreeSelectionEvent event) {
				fireSelectionChanged( getSelectedNode());
			}
		});

		tree.addMouseListener( new PopupListener() {
			public void popupTriggered( MouseEvent e) {
				TreePath path = tree.getPathForLocation( e.getX(), e.getY());

				if ( path != null && path.equals( tree.getSelectionPath())) {
					BaseNode node = (BaseNode) path.getLastPathComponent();
					
					firePopupTriggered( e, node);
				}
			}
		});
		
		tree.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent e) {
				TreePath path = tree.getPathForLocation( e.getX(), e.getY());

				if ( path != null && path.equals( tree.getSelectionPath())) {
					BaseNode node = (BaseNode) path.getLastPathComponent();
					
					fireDoubleClicked( e, node);
				}
			}
		});
		
		tree.addTreeWillExpandListener(this);

		scrollPane = new JScrollPane(	tree,
										JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
										JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		/**
		 * Work around to make sure the scroll pane shows the vertical 
		 * scrollbar for the first time when resized to a size small enough.
		 * JDK 1.3.0-C 
		 *
		 * Got work around from Bug ID: 4243631 (It should be fixed...)
		 *
		 * ED: Check with JDK1.4
		 */
		scrollPane.getViewport().addComponentListener( new ComponentAdapter() {
			public void componentResized( ComponentEvent e) {
				scrollPane.doLayout();
			}
		});
		
		tree.getActionMap().put( "deleteAction", getDeleteAction());
		tree.getActionMap().put( "openAction", getOpenFileAction());

		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "openAction");
		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_DELETE, 0, false), "deleteAction");
		tree.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_SPACE, 0, false), "toggle");
		tree.setRowHeight( 18);

		add( scrollPane, BorderLayout.CENTER);
		
		
		boolean isStartup = true;
		setProjects( properties.getProjectProperties(), isStartup);		
				
		
	}
	
	/**
	 * Collapses all the nodes in the tree.
	 */
	public void updatePreferences() {
		DocumentNode.setShowFullPath( properties.isShowFullPath());
		tree.updateUI();
		tree.setRowHeight( 18);
	}

	/**
	 * Collapses all the nodes in the tree.
	 */
	public void editingStopped() {
		BaseNode node = getSelectedNode();
		
		if ( node.getName() != null && node.getName().trim().length() > 0) {
		
			if ( node instanceof ProjectNode) {
				Enumeration projectNodes = root.children();
				boolean nameExists = false;
				
				while ( projectNodes.hasMoreElements() && !nameExists) {
					ProjectNode pNode = (ProjectNode)projectNodes.nextElement();
					
					if ( pNode != node && pNode.getName().equalsIgnoreCase( node.getName())) {
						nameExists = true;
					}
				}
				
				if ( nameExists) {
					MessageHandler.showMessage( "A Project with the name \""+node.getName()+"\" exists already.\n"+
												"Please specify another name.");
					getTree().startEditingAtPath( new TreePath( node.getPath()));
				}
			} else if ( node instanceof FolderNode) {

				Enumeration folderNodes = node.getParent().children();
				boolean nameExists = false;
				
				while ( folderNodes.hasMoreElements() && !nameExists) {
					BaseNode bNode = (BaseNode)folderNodes.nextElement();
					
					if ( bNode instanceof FolderNode) {
						FolderNode fNode = (FolderNode)bNode;

						if ( fNode != node && fNode.getName().equalsIgnoreCase( node.getName())) {
							nameExists = true;
						}
					}
				}
				
				if ( nameExists) {
					MessageHandler.showMessage( "A Folder with the name \""+node.getName()+"\" exists already.\n"+
												"Please specify another name.");
					getTree().startEditingAtPath( new TreePath( node.getPath()));
				}
			}
		} else {
			if ( node instanceof ProjectNode) {
				MessageHandler.showMessage( "Please specify a Name for this Project.");
			} else if ( node instanceof FolderNode) {
				MessageHandler.showMessage( "Please specify a Name for this Folder.");
			}
	
			getTree().startEditingAtPath( new TreePath( node.getPath()));
		}
		
		properties.save();
	}
	
	/**
	 * Enables/disables all actions...
	 */
	public void setActionsEnabled( boolean enabled) {
//		getNewProjectAction().setEnabled( enabled);

		if ( !enabled) {
			fireSelectionChanged( null);
		} else {
			fireSelectionChanged( getSelectedNode());
		}
	}

	/**
	 * Collapses all the nodes in the tree.
	 */
	public void collapseAll() {
		tree.collapseAll();
	}

	/**
	 * Collapses all the nodes in the tree.
	 *
	 * @param node the node to collapse all nodes for.
	 */
	public void collapseNode( BaseNode node) {
		tree.collapseNode( node);
	}

	/**
	 * Expands all the nodes in the tree.
	 */
	public void expandAll() {
		tree.expandAll();
	}

	/**
	 * Expands all the nodes in the tree from this node down.
	 *
	 * @param node the node to expand all nodes for.
	 */
	public void expandNode( BaseNode node) {
		tree.expandNode( node);
	}

	/**
	 * Set Projects. 
	 *
	 * @param project the project-properties.
	 */
	public void setProjects( Vector projects, boolean isStartup) {
		if ( root.getChildCount() > 0) {
			
			root.removeAllChildren();
		}
		
		for ( int i = 0; i < projects.size(); i++) {
			ProjectNode project = new ProjectNode( treeModel, (ProjectProperties)projects.elementAt(i), isStartup);
			addNode( root, project);
		}
		
		treeModel.reload();
	}

	/**
	 * Adds a project node to the tree... 
	 *
	 * @param project the project-properties.
	 */
	public void addProject(boolean isStartup) {
		// System.out.println( "Project.addProject()");
		// add the folder...
		parent.switchToProjectTab();
		getTree().requestFocus();

		ProjectProperties props = new ProjectProperties( "New Project");
		properties.addProjectProperties( props);
				
		ProjectNode project = new ProjectNode( treeModel, props, isStartup);
		addNode( root, project);
		
		TreePath path = new TreePath( project.getPath());
		getTree().setSelectionPath( path);
		getTree().startEditingAtPath( path);
	}
	
	public void selectProject(String name) {
		
		if(root != null) {
			Enumeration children = root.children();
			while(children.hasMoreElements() == true) {
				Object obj = children.nextElement();
				if(obj instanceof ProjectNode) {
					ProjectNode projectNode = (ProjectNode)obj;
					if(projectNode.getName().equalsIgnoreCase(name)) {
						
						//have a match, now select it
						TreePath path = new TreePath( projectNode.getPath());
						getTree().setSelectionPath( path);
					}
				}
			}
		}
		
	}
	
	public ProjectNode getSelectedProject() {
		
		BaseNode baseNode = getSelectedNode();
		if(baseNode != null) {
			
			if(baseNode instanceof ProjectNode) {
				return((ProjectNode)baseNode);
			}
			else if(baseNode instanceof FolderNode) {
				
				FolderNode folder = (FolderNode)baseNode;
				BaseNode parentNode = (BaseNode) folder.getParent();
				while((!(parentNode instanceof ProjectNode)) && (parentNode != null)){
					parentNode = (BaseNode) parentNode.getParent();					
				}
				
				if((parentNode != null) && (parentNode instanceof ProjectNode)) {
					return(ProjectNode) (parentNode);
				}
				else {
					return(null);
				}
			}
			else if(baseNode instanceof DocumentNode) {
				DocumentNode document = (DocumentNode)baseNode;
				BaseNode parentNode = (BaseNode) document.getParent();
				while((!(parentNode instanceof ProjectNode)) && (parentNode != null)){
					parentNode = (BaseNode) parentNode.getParent();					
				}
				
				if((parentNode != null) && (parentNode instanceof ProjectNode)) {
					return(ProjectNode) (parentNode);
				}
				else {
					return(null);
				}
			}
			else if(baseNode instanceof VirtualFolderNode) {
				
				VirtualFolderNode folder = (VirtualFolderNode)baseNode;
				BaseNode parentNode = (BaseNode) folder.getParent();
				while((!(parentNode instanceof ProjectNode)) && (parentNode != null)){
					parentNode = (BaseNode) parentNode.getParent();					
				}
				
				if((parentNode != null) && (parentNode instanceof ProjectNode)) {
					return(ProjectNode) (parentNode);
				}
				else {
					return(null);
				}
			}
			else {
				return(null);
			}
		}
		else {
			
			return(null);
		}
	}

	/**
	 * Removes a project node from the tree... 
	 */
	public void deleteProject() {
		ProjectNode node = (ProjectNode)getSelectedNode();

		ProjectProperties props = (ProjectProperties)node.getProperties();
		properties.removeProjectProperties( props);
				
		removeNode( node);

		properties.save();
	}

	/**
	 * Removes a project node from the tree... 
	 */
	public void deleteProject( ProjectProperties props) {
		Enumeration projectNodes = root.children();
		
		while ( projectNodes.hasMoreElements()) {
			ProjectNode node = (ProjectNode)projectNodes.nextElement();
			
			if ( node.getProperties().getName().equals( props.getName())) {
				removeNode( node);
				break;
			}
		}
		
		properties.removeProjectProperties( props);

		properties.save();
	}

	/**
	 * Adds a folder node to the current selected folder/project... 
	 */
	public void addFolder(boolean isStartup) {
		// add the folder...
		BaseNode node = getSelectedNode();
		
		if ( node instanceof FolderNode) {
			FolderNode parent = (FolderNode)node;

			FolderProperties props = new FolderProperties( "New Folder");
			parent.getProperties().addFolderProperties( props);
				
			FolderNode folder = new FolderNode( treeModel, props, isStartup);
			addNode( parent, folder);
			getTree().startEditingAtPath( new TreePath( folder.getPath()));
		}
	}

	/**
	 * Searches through all the files in the folder or project
	 */
	public void findInFiles( final String search, final boolean regExp, final boolean matchCase, final boolean wholeWord) {
		// add the folder...
		final BaseNode node = getSelectedNode();
		
		
		if ( node instanceof FolderNode) {
			final XngrProgressDialog progressDialog  = new XngrProgressDialog(parent, true);
 			progressDialog.setTitle("Find In Project: " + node.getName());
			
					
			
			//final ProgressMonitor monitor = new ProgressMonitor( parent, "Searching for \""+search+"\" in:", "", 0, documents.size());
			//monitor.setMillisToDecideToPopup( 10);
			//monitor.setMillisToPopup( 10);

			parent.setWait( true);
			parent.setStatus( "Searching ...");
			
//			 Run in Thread!!!
			Runnable runner = new Runnable() {
				public void run()  {
					try {
						
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								progressDialog.monitor.setIndeterminate(true);
								progressDialog.setVisible(true);
							}
						});
						
						final Vector documents = ((FolderNode)node).getDocuments(progressDialog);
						
						if(documents != null) {
						
							//reset previous find in files + select
				 			SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if(documents != null) {
										parent.getOutputPanel().startFindInFiles( "Searching in "+documents.size()+" Files For: \""+search+"\" ...");
										progressDialog.remakeMonitor(0, documents.size());
									}
								}
				 			});
				 			
							for ( int i = 0; i < documents.size(); i++) {
					 			final DocumentNode doc = (DocumentNode)documents.elementAt(i);
					 			//monitor.setNote( doc.getProperties().getName()+"...");
								//monitor.setProgress( i);
	
					 			final int cnt = i;
					 			SwingUtilities.invokeLater(new Runnable() {
									public void run() {
										if(cnt == 0) {
											progressDialog.monitor.setValue( cnt+1);
										}
										progressDialog.label.setText( "Searching "+doc.getProperties().getURL()+"...");		
									}
								});
					 			if ( progressDialog.isCancelled()) {
									break;
					 			}
					 			
					 			final Vector matches = Finder.find( doc.getProperties().getURL(), search, regExp, matchCase, wholeWord);
					 			
								SwingUtilities.invokeLater( new Runnable() {
									public void run() {
							 			parent.getOutputPanel().addFindInFiles( matches);
									}
								});
								if ( progressDialog.isCancelled()) {
									break;
					 			}
								
					 		}
						}
						
				 	} finally {
				 		
				 		SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								progressDialog.monitor.setValue( progressDialog.monitor.getMaximum()+1);
								progressDialog.setVisible(false);
							}
						});

						parent.getOutputPanel().finishFindInFiles();
				 		parent.setStatus( "Done");
				 		parent.setWait( false);
				 	}
				}
			};

			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
			/*final Vector documents = ((FolderNode)node).getDocuments();

			// reset previous find in files + select 
			parent.getOutputPanel().startFindInFiles( "Searching in "+documents.size()+" Files For: \""+search+"\" ...");
			
			final ProgressMonitor monitor = new ProgressMonitor( parent, "Searching for \""+search+"\" in:", "", 0, documents.size());
			monitor.setMillisToDecideToPopup( 10);
			monitor.setMillisToPopup( 10);

			parent.setWait( true);
			parent.setStatus( "Searching ...");
			
			// Run in Thread!!!
			Runnable runner = new Runnable() {
				public void run()  {
					try {
						for ( int i = 0; i < documents.size(); i++) {
				 			DocumentNode doc = (DocumentNode)documents.elementAt(i);
				 			monitor.setNote( doc.getProperties().getName()+"...");
							monitor.setProgress( i);

				 			final Vector matches = Finder.find( doc.getProperties().getURL(), search, regExp, matchCase, wholeWord);
				 			
							SwingUtilities.invokeLater( new Runnable() {
								public void run() {
						 			parent.getOutputPanel().addFindInFiles( matches);
								}
							});

							if ( monitor.isCanceled()) {
				 				break;
				 			}

							monitor.setProgress( i+1);
				 		}
						
				 	} finally {
						monitor.close();

						parent.getOutputPanel().finishFindInFiles();
				 		parent.setStatus( "Done");
				 		parent.setWait( false);
				 	}
				}
			};

			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();*/
		}
		else if ( node instanceof VirtualFolderNode) {
			
			final XngrProgressDialog progressDialog  = new XngrProgressDialog(parent, true);
 			progressDialog.setTitle("Find In Files");
			
			System.out.println("here - node is virtual node");

			// reset previous find in files + select 
			//parent.getOutputPanel().startFindInFiles( "Searching in "+documents.size()+" Files For: \""+search+"\" ...");
			
			//final ProgressMonitor monitor = new ProgressMonitor( parent, "Searching for \""+search+"\" in:", "", 0, documents.size());
			//monitor.setMillisToDecideToPopup( 10);
			//monitor.setMillisToPopup( 10);

			parent.setWait( true);
			parent.setStatus( "Searching ...");
			
//			 Run in Thread!!!
			Runnable runner = new Runnable() {
				public void run()  {
					try {
						
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								progressDialog.monitor.setIndeterminate(true);
								progressDialog.setVisible(true);
							}
						});
						
						final Vector documents = ((VirtualFolderNode)node).getDocuments();
						
//						 reset previous find in files + select
			 			SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								if(documents != null) {
									parent.getOutputPanel().startFindInFiles( "Searching in "+documents.size()+" Files For: \""+search+"\" ...");
									progressDialog.remakeMonitor(0, documents.size());
								}
							}
			 			});
			 			
						for ( int i = 0; i < documents.size(); i++) {
				 			final DocumentNode doc = (DocumentNode)documents.elementAt(i);
				 			//monitor.setNote( doc.getProperties().getName()+"...");
							//monitor.setProgress( i);

				 			final int cnt = i;
				 			SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									if(cnt == 0) {
										progressDialog.monitor.setValue( cnt+1);
									}
									progressDialog.label.setText( "Searching "+doc.getProperties().getURL()+"...");		
								}
							});
				 			final Vector matches = Finder.find( doc.getProperties().getURL(), search, regExp, matchCase, wholeWord);
				 			
							SwingUtilities.invokeLater( new Runnable() {
								public void run() {
						 			parent.getOutputPanel().addFindInFiles( matches);
								}
							});
							if ( progressDialog.isCancelled()) {
				 				break;
				 			}
							
				 		}
					} catch(Exception e) {
						e.printStackTrace();
					
				 	} finally {
				 		
				 		System.out.println(progressDialog.monitor.getMaximum());
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								System.out.println(progressDialog.monitor.getMaximum());
								progressDialog.monitor.setValue( progressDialog.monitor.getMaximum()+1);		
							}
						});

						parent.getOutputPanel().finishFindInFiles();
				 		parent.setStatus( "Done");
				 		parent.setWait( false);
				 	}
				}
			};

			// Create and start the thread ...
			Thread thread = new Thread( runner);
			thread.start();
		}

	}

	/**
	 * Parses all the selected file, or the files in the selected folder or project.
	 */
	public void parse() {
		parent.setWait( true);
		parent.setStatus( "Parsing ...");

		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {

				try {
				 	// add the folder...
				 	BaseNode node = getSelectedNode();
				 	
				 	if ( node instanceof FolderNode) {
				 		((FolderNode)node).parse();
				 	} else if ( node instanceof DocumentNode) {
				 		((DocumentNode)node).parse();
				 	} else if ( node instanceof VirtualFolderNode) {
				 		((VirtualFolderNode)node).parse();
				 	}
			 	} finally {
			 		parent.setStatus( "Done");
			 		parent.setWait( false);
			 	}
			}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
	}

	/**
	 * Validates all the selected files, or the files in the selected folder or project.
	 */
	public void validateXML() {
		parent.setWait( true);
		parent.setStatus( "Validating ...");

		// Run in Thread!!!
		Runnable runner = new Runnable() {
			public void run()  {

				try {
				 	// add the folder...
				 	BaseNode node = getSelectedNode();
				 	
				 	if ( node instanceof FolderNode) {
				 		((FolderNode)node).validate();
				 	} else if ( node instanceof DocumentNode) {
				 		((DocumentNode)node).validate();
				 	} else if ( node instanceof VirtualFolderNode) {
				 		((VirtualFolderNode)node).validate();
				 	}
			 	} finally {
			 		parent.setStatus( "Done");
			 		parent.setWait( false);
			 	}
			}
		};

		// Create and start the thread ...
		Thread thread = new Thread( runner);
		thread.start();
	}

	/**
	 * Adds a folder node to the current selected folder/project... 
	 */
	public void renameFolder() {
		editSelectedNode();
	}

	/**
	 * Edit a project name... 
	 */
	public void renameProject() {
		editSelectedNode();
	}

	/**
	 * Removes a folder node from the tree... 
	 */
	public void removeFolder() {
		FolderNode node = (FolderNode)getSelectedNode();
		FolderProperties props = node.getProperties();
		FolderProperties parentProps = ((FolderNode)node.getParent()).getProperties();
		
		parentProps.removeFolderProperties( props);
		
		removeNode( node);

		properties.save();
	}

	/**
	 * Removes a file node from the tree... 
	 */
	public void removeFile() {
		DocumentNode node = (DocumentNode)getSelectedNode();
		DocumentProperties props = node.getProperties();
		FolderProperties parentProps = ((FolderNode)node.getParent()).getProperties();
		
		parentProps.removeDocumentProperties( props);
		
		removeNode( node);

		properties.save();
	}
	
	public void removeVirtualFolder() {
		
		VirtualFolderNode node = (VirtualFolderNode)getSelectedNode();
		VirtualFolderProperties props = node.getProperties();
		FolderProperties parentProps = ((FolderNode)node.getParent()).getProperties();
		
		parentProps.removeFolderProperties( props);
		
		removeNode( node);

		properties.save();
	}

	/**
	 * Imports a project from disk. 
	 *
	 * @param project the project file.
	 */
	public void importProject( File project, boolean isStartup) {
		try {
			if ( project.exists()) {
				URL url = XngrURLUtilities.getURLFromFile(project);
				ExchangerDocument document = new ExchangerDocument( url);
				document.loadWithoutSubstitution();
				XElement root = document.getRoot();
				
				if ( root.getName().equals( "xngr")) {
					XElement projects = root.getElement( "projects");
					addProjects( url, projects, isStartup);
					
					XElement scenarios = root.getElement( "scenarios");
					addScenarios( url, scenarios);
					
					XElement types = root.getElement( "types");
					addTypes( url, types);

					XElement templates = root.getElement( "templates");
					addTemplates( url, templates);
				} else {
					MessageHandler.showError( "Not a valid Project file.", "Project Error");
				}
			} else {
				MessageHandler.showError( "Could not find Project file.", "Project Error");
			}
		} catch ( IOException e) {
			MessageHandler.showError( "Could not create Project file.", e, "Project Error");
		} catch (SAXParseException e) {
			MessageHandler.showError( "Could not create Project file.", e, "Project Error");
		}

		properties.save();
	}

	private ProjectProperties getExistingProject( ConfigurationProperties properties, ProjectProperties project) {
		Vector projects = properties.getProjectProperties();
		
		for ( int i = 0; i < projects.size(); i++) {
			ProjectProperties props = (ProjectProperties)projects.elementAt(i);
			
			if ( project.getName().equals( props.getName())) {
				return props;
			}
		}
		
		return null;
	}

	private void addProjects( URL base, XElement root, boolean isStartup) {
		try {
			if ( root.getName().equals( "projects")) {
				XElement[] projects = root.getElements( "project");
				
				if ( projects != null) {
					for ( int i = 0; i < projects.length; i++) {
						String name = projects[i].getAttribute( "name");
						
						if ( name != null && name.trim().length() > 0) {
							ProjectProperties project = new ProjectProperties( name);
							ProjectProperties existingProject = getExistingProject( properties, project);

							if ( existingProject != null) {
								int value = MessageHandler.showConfirm( "A Project with this name already exists \""+existingProject.getName()+"\".\n"+
																		"Do you want to override the existing project?");
								
								if ( value == JOptionPane.OK_OPTION){
									deleteProject( existingProject);
									properties.addProjectProperties( project);

									XElement[] documents = projects[i].getElements( "document");
									addDocuments( base, project, documents);
									
									XElement[] folders = projects[i].getElements( "folder");
									addFolders( base, project, folders);
									
									XElement[] virtualFolders = projects[i].getElements( VirtualFolderProperties.VIRTUAL_FOLDER_PROPERTIES);
									addVirtualFolders( base, project, folders);

									ProjectNode node = new ProjectNode( treeModel, project, isStartup);
									addNode( this.root, node);
									nodeChanged( node);
									
									TreePath path = new TreePath( node.getPath());
									getTree().setSelectionPath( path);
								}
							} else {
								properties.addProjectProperties( project);

								XElement[] documents = projects[i].getElements( "document");
								addDocuments( base, project, documents);
								
								XElement[] folders = projects[i].getElements( "folder");
								addFolders( base, project, folders);

								XElement[] virtualFolders = projects[i].getElements( VirtualFolderProperties.VIRTUAL_FOLDER_PROPERTIES);
								addVirtualFolders( base, project, folders);
								
								ProjectNode node = new ProjectNode( treeModel, project, isStartup);
								addNode( this.root, node);
								nodeChanged( node);
								
								TreePath path = new TreePath( node.getPath());
								getTree().setSelectionPath( path);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds files to the folder/project... 
	 *
	 * @param base the base url.
	 * @param folder folder properties.
	 * @param documents list of document elements.
	 */
	private void addDocuments( URL base, FolderProperties folder, XElement[] documents) {
		// add the folder...
		
		for ( int i = 0; i < documents.length; i++) {
			File file = URLUtilities.toFile( URLUtilities.resolveURL( base, documents[i].getAttribute( "src")));
			
			if ( file != null && file.isFile()) {
				folder.addDocumentProperties( new DocumentProperties( file));
			} else {
				URL url = URLUtilities.toURL( documents[i].getAttribute( "src"));
				folder.addDocumentProperties( new DocumentProperties( url));
			}
		}
	}

	/**
	 * Adds files and folders to the folder/project... 
	 *
	 * @param base the base url.
	 * @param folder folder properties.
	 * @param documents list of document elements.
	 */
	private void addFolders( URL base, FolderProperties parent, XElement[] folders) {
		// add the folder...
		
		for ( int i = 0; i < folders.length; i++) {
			String name = folders[i].getAttribute( "name");
			
			if ( name != null && name.trim().length() > 0) {
				FolderProperties folder = new FolderProperties( name);
				parent.addFolderProperties( folder);
				
				XElement[] documents = folders[i].getElements( "document");
				addDocuments( base, folder, documents);
	
				XElement[] children = folders[i].getElements( "folder");
				addFolders( base, folder, children);
				
				XElement[] virtualFolders = folders[i].getElements( VirtualFolderProperties.VIRTUAL_FOLDER_PROPERTIES);
				addVirtualFolders( base, parent, virtualFolders);
				
			} else {
				MessageHandler.showError( "Folder does not have a Name.", "Project Error");
			}
		}
	}
	
	/**
	 * Adds files and folders to the folder/project... 
	 *
	 * @param base the base url.
	 * @param folder folder properties.
	 * @param documents list of document elements.
	 */
	private void addVirtualFolders( URL base, FolderProperties parent, XElement[] folders) {
		// add the folder...
		
		for ( int i = 0; i < folders.length; i++) {
			String name = folders[i].getAttribute( "name");
			
			if ( name != null && name.trim().length() > 0) {
				VirtualFolderProperties folder = new VirtualFolderProperties( name);
				parent.addVirtualFolderProperties( folder);
				
				
				
			} else {
				MessageHandler.showError( "Virtual Folder does not have a Name.", "Project Error");
			}
		}
	}

	/**
	 * Adds a directory to the current selected folder/project... 
	 *
	 * @param dir the directory.
	 */
	public void addDirectory( File dir, boolean isStartup) {
		// add the folder...
		BaseNode node = getSelectedNode();
		
		if ( !dir.isDirectory()) {
			dir = dir.getParentFile();
		}
		
		if ( node instanceof FolderNode) {
			FolderNode folder = (FolderNode)node;
			FolderProperties props = folder.getProperties();
			
			props.addFolderProperties( new FolderProperties( dir));

			folder.update(isStartup);
			nodeChanged( folder);

			properties.save();
		}
	}

	/**
	 * Adds a directory to the current selected folder/project... 
	 *
	 * @param dir the directory.
	 */
	public void addDirectoryContents( File dir, boolean isStartup) {
		// add the folder...
		BaseNode node = getSelectedNode();
		
		if ( node instanceof FolderNode) {
			if ( !dir.isDirectory()) {
				dir = dir.getParentFile();
			}

			FolderNode folder = (FolderNode)node;
			FolderProperties props = folder.getProperties();
			
			// add all the files...
			File[] files = dir.listFiles();
			
			for ( int i = 0; i < files.length; i++) {
				File file = files[i];
				
				if ( file.isDirectory()) {
					props.addFolderProperties( new FolderProperties( file));
				} else { // file
					props.addDocumentProperties( new DocumentProperties( file));
				}
			}

			folder.update(isStartup);
			nodeChanged( folder);

			properties.save();
		}
	}

	/**
	 * Adds a remote file to the current selected folder/project... 
	 *
	 * @param url the url of the remote document.
	 */
	public void addRemote( URL url, boolean isStartup) {
		// add the folder...
		BaseNode node = getSelectedNode();
		
		if ( node instanceof FolderNode) {
			FolderNode folder = (FolderNode)node;
			FolderProperties props = folder.getProperties();
			
			props.addDocumentProperties( new DocumentProperties( url));

			folder.update(isStartup);
			nodeChanged( folder);

			properties.save();
		}
	}

	/**
	 * Adds a file to the current selected folder/project... 
	 *
	 * @param file the file.
	 */
	public void addFile( File file, boolean isStartup) {
		// add the folder...
		BaseNode node = getSelectedNode();
		
		if ( node instanceof FolderNode) {
			FolderNode folder = (FolderNode)node;
			FolderProperties props = folder.getProperties();
			
			props.addDocumentProperties( new DocumentProperties( file));

			folder.update(isStartup);
			nodeChanged( folder);

			properties.save();
		}
	}

	/**
	 * Adds a node to the parent and fires an event for the 
	 * tree model.
	 *
	 * @param parent the parent node.
	 * @param node the node that needs to be added.
	 */
	private void addNode( DefaultMutableTreeNode parent, DefaultMutableTreeNode node) {
		parent.add( node);

		int[] indices =  { parent.getIndex( node) };
		treeModel.nodesWereInserted( parent, indices);
	}

	/**
	 * Removes a node from the project panel.
	 *
	 * @param node the node to remove.
	 */
	private void removeNode( BaseNode node) {
		treeModel.removeNodeFromParent( node);
	}

	/**
	 * Fires a structure changed event.
	 *
	 * @param node the changed node.
	 */
	private void nodeChanged( DefaultMutableTreeNode node) {
	
		treeModel.nodeStructureChanged( node);
	}

	/**
	 * Gets the tree component.
	 *
	 * @return the tree component.
	 */
	private JTree getTree() {
		return tree;
	}

	/**
	 * Returns the currently selected node, null if nothing 
	 * has been selected.
	 *
	 * @return the selected node.
	 */
	public BaseNode getSelectedNode() {
		BaseNode node = null;
		TreePath path = tree.getSelectionPath();
		
		if ( path != null) {
			node = (BaseNode) path.getLastPathComponent();
		}	
	
		return node;
	}

	/**
	 * Sets the selected node. 
	 *
	 * @param node the selected node.
	 */
	public void setSelectedNode( BaseNode node) {
		if ( node != null) {
			tree.setSelectionPath( new TreePath( node.getPath()));
		} else {
			tree.clearSelection();
		}
	}

/**
 * Get the actions.
 */
	public ParseAction getParseAction() {
		if ( parseAction == null) {
			parseAction = new ParseAction( this);
		}
		
		return parseAction;
	}

	public ValidateAction getValidateAction() {
		if ( validateAction == null) {
			validateAction = new ValidateAction( this);
		}
		
		return validateAction;
	}

	public AddDirectoryContentsAction getAddDirectoryContentsAction() {
		if ( addDirectoryContentsAction == null) {
			addDirectoryContentsAction = new AddDirectoryContentsAction( parent, this);
		}
		
		return addDirectoryContentsAction;
	}

	public AddDirectoryAction getAddDirectoryAction() {
		if ( addDirectoryAction == null) {
			addDirectoryAction = new AddDirectoryAction( parent, this);
		}
		
		return addDirectoryAction;
	}

	public AddFileAction getAddFileAction() {
		if ( addFileAction == null) {
			addFileAction = new AddFileAction( parent, this);
		}
		
		return addFileAction;
	}

	public FindInProjectsAction getFindInProjectsAction() {
		if ( findInProjectsAction == null) {
			findInProjectsAction = new FindInProjectsAction( parent, properties, this);
		}
		
		return findInProjectsAction;
	}

	public AddRemoteDocumentAction getAddRemoteDocumentAction() {
		if ( addRemoteDocumentAction == null) {
			addRemoteDocumentAction = new AddRemoteDocumentAction( properties, parent, this);
		}
		
		return addRemoteDocumentAction;
	}

	public AddFolderAction getAddFolderAction() {
		if ( addFolderAction == null) {
			addFolderAction = new AddFolderAction( this);
		}
		
		return addFolderAction;
	}

	public DeleteProjectAction getDeleteProjectAction() {
		if ( deleteProjectAction == null) {
			deleteProjectAction = new DeleteProjectAction( this);
		}
		
		return deleteProjectAction;
	}

	public DeleteAction getDeleteAction() {
		if ( deleteAction == null) {
			deleteAction = new DeleteAction( this, getDeleteProjectAction(), getRemoveFolderAction(), getRemoveFileAction());
		}
		
		return deleteAction;
	}

	public NewProjectAction getNewProjectAction() {
		if ( newProjectAction == null) {
			newProjectAction = new NewProjectAction( this);
		}
		
		return newProjectAction;
	}

	public ImportProjectAction getImportProjectAction() {
		if ( importProjectAction == null) {
			importProjectAction = new ImportProjectAction( parent, this);
		}
		
		return importProjectAction;
	}

	public OpenFileAction getOpenFileAction() {
		if ( openFileAction == null) {
			openFileAction = new OpenFileAction( parent, this);
		}
		
		return openFileAction;
	}

	public RemoveFileAction getRemoveFileAction() {
		if ( removeFileAction == null) {
			removeFileAction = new RemoveFileAction( this);
		}
		
		return removeFileAction;
	}

	public RemoveFolderAction getRemoveFolderAction() {
		if ( removeFolderAction == null) {
			removeFolderAction = new RemoveFolderAction( this);
		}
		
		return removeFolderAction;
	}

	public RenameProjectAction getRenameProjectAction() {
		if ( renameProjectAction == null) {
			renameProjectAction = new RenameProjectAction( this);
		}
		
		return renameProjectAction;
	}

	public RenameFolderAction getRenameFolderAction() {
		if ( renameFolderAction == null) {
			renameFolderAction = new RenameFolderAction( this);
		}
		
		return renameFolderAction;
	}

	public ProjectPropertiesAction getProjectPropertiesAction() {
		if ( projectPropertiesAction == null) {
			projectPropertiesAction = new ProjectPropertiesAction( parent, this);
		}
		
		return projectPropertiesAction;
	}

	/**
	 * Starts editing the currently selected node.
	 */
	public void editSelectedNode() {
		BaseNode node = (BaseNode)getSelectedNode();

		if(!(node instanceof VirtualFolderNode)) {
			getTree().startEditingAtPath( new TreePath( node.getPath()));
		}
	}

	protected void fireSelectionChanged( BaseNode node) {
		getOpenFileAction().setEnabled( false);
		getRemoveFileAction().setEnabled( false);

		getParseAction().setEnabled( false);
		getValidateAction().setEnabled( false);

		getAddFileAction().setEnabled( false);
		getFindInProjectsAction().setEnabled( true);
		getAddRemoteDocumentAction().setEnabled( false);
		getAddFolderAction().setEnabled( false);
		getAddDirectoryAction().setEnabled( false);
		getAddVirtualDirectoryAction().setEnabled( false);
		getAddDirectoryContentsAction().setEnabled( false);
		getRemoveFolderAction().setEnabled( false);
		getRenameFolderAction().setEnabled( false);
		getRenameProjectAction().setEnabled( false);
		getRefreshVirtualDirectoryAction().setEnabled(false);
		
		getDeleteProjectAction().setEnabled( false);
		getProjectPropertiesAction().setEnabled( false);

		if ( node instanceof DocumentNode) {
			getOpenFileAction().setEnabled( true);
			getRemoveFileAction().setEnabled( true);
			getParseAction().setEnabled( true);
			getValidateAction().setEnabled( true);
			getRefreshVirtualDirectoryAction().setEnabled(false);
		} else if ( node instanceof ProjectNode) {
			getParseAction().setEnabled( true);
			getValidateAction().setEnabled( true);
			//getFindInProjectsAction().setEnabled( true);
			getAddFileAction().setEnabled( true);
			getAddRemoteDocumentAction().setEnabled( true);
			getAddFolderAction().setEnabled( true);
			getDeleteProjectAction().setEnabled( true);
			getProjectPropertiesAction().setEnabled( true);
			getAddDirectoryAction().setEnabled( true);
			getAddVirtualDirectoryAction().setEnabled( true);
			getAddDirectoryContentsAction().setEnabled( true);
			getRenameProjectAction().setEnabled( true);
			getRefreshVirtualDirectoryAction().setEnabled(false);
		} else if ( node instanceof FolderNode) {
			getParseAction().setEnabled( true);
			getValidateAction().setEnabled( true);
			//getFindInProjectsAction().setEnabled( true);
			getAddFileAction().setEnabled( true);
			getAddRemoteDocumentAction().setEnabled( true);
			getAddFolderAction().setEnabled( true);
			getAddDirectoryAction().setEnabled( true);
			getAddVirtualDirectoryAction().setEnabled( true);
			getAddDirectoryContentsAction().setEnabled( true);
			getRemoveFolderAction().setEnabled( true);
			getRenameFolderAction().setEnabled( true);
			getRefreshVirtualDirectoryAction().setEnabled(false);
		} else if ( node instanceof VirtualFolderNode) {
			getParseAction().setEnabled( true);
			getValidateAction().setEnabled( true);
			//getFindInProjectsAction().setEnabled( true);
			getAddFileAction().setEnabled( false);
			getAddRemoteDocumentAction().setEnabled( false);
			getAddFolderAction().setEnabled( false);
			getAddDirectoryAction().setEnabled( false);
			getAddVirtualDirectoryAction().setEnabled( false);
			getAddDirectoryContentsAction().setEnabled( false);
			getRemoveFolderAction().setEnabled( true);
			getRenameFolderAction().setEnabled( false);
			getRefreshVirtualDirectoryAction().setEnabled(true);
		}
	}

	protected void firePopupTriggered( MouseEvent event, BaseNode node) {
		JPopupMenu popup = null;
		
		if ( node instanceof DocumentNode) {
			popup = getFilePopup();
		} else if ( node instanceof ProjectNode) {
			popup = getProjectPopup();
		} else if ( node instanceof FolderNode) {
			popup = getFolderPopup();
		} else if ( node instanceof VirtualFolderNode) {
			popup = getVirtualDirectoryPopup();
		}
		
		if ( popup != null) {
			popup.show( tree, event.getX(), event.getY());
		}
	}

	protected void fireDoubleClicked( MouseEvent event, BaseNode node) {
		if ( node instanceof DocumentNode) {
			getOpenFileAction().execute();
		}
	}
	
	private JPopupMenu getFilePopup() {
		if ( filePopup == null) {
			filePopup = new JPopupMenu();
			filePopup.add( getOpenFileAction());
			filePopup.addSeparator();
			filePopup.add( getParseAction());
			filePopup.add( getValidateAction());
			filePopup.addSeparator();
			filePopup.add( getRemoveFileAction());
		}
		
		return filePopup;
	}
	
	private JPopupMenu getFolderPopup() {
		if ( folderPopup == null) {
			folderPopup = new JPopupMenu();
			folderPopup.add( getAddFileAction());
			folderPopup.add( getAddRemoteDocumentAction());
			folderPopup.add( getAddFolderAction());
			folderPopup.add( getAddDirectoryAction());
			folderPopup.add( getAddDirectoryContentsAction());
			folderPopup.add( getAddVirtualDirectoryAction());
			folderPopup.addSeparator();
			folderPopup.add( getParseAction());
			folderPopup.add( getValidateAction());
			folderPopup.addSeparator();
			folderPopup.add( getRemoveFolderAction());
			folderPopup.addSeparator();
			folderPopup.add( getRenameFolderAction());
			folderPopup.add( getFindInProjectsAction());
		}
		
		return folderPopup;
	}
	
	private JPopupMenu getVirtualDirectoryPopup() {
		if ( virtualDirectoryPopup == null) {
			virtualDirectoryPopup = new JPopupMenu();
			virtualDirectoryPopup.add( getRefreshVirtualDirectoryAction());
			virtualDirectoryPopup.addSeparator();
			virtualDirectoryPopup.add( getParseAction());
			virtualDirectoryPopup.add( getValidateAction());
			virtualDirectoryPopup.addSeparator();
			virtualDirectoryPopup.add( getRemoveFolderAction());
			virtualDirectoryPopup.addSeparator();
			virtualDirectoryPopup.add( getFindInProjectsAction());
		}
		
		return virtualDirectoryPopup;
	}

	private JPopupMenu getProjectPopup() {
		if ( projectPopup == null) {
			projectPopup = new JPopupMenu();
			projectPopup.add( getAddFileAction());
			projectPopup.add( getAddRemoteDocumentAction());
			projectPopup.add( getAddFolderAction());
			projectPopup.add( getAddDirectoryAction());
			projectPopup.add( getAddDirectoryContentsAction());
			projectPopup.add( getAddVirtualDirectoryAction());
			projectPopup.addSeparator();
			projectPopup.add( getParseAction());
			projectPopup.add( getValidateAction());
			projectPopup.addSeparator();
			projectPopup.add( getDeleteProjectAction());
			projectPopup.addSeparator();
			projectPopup.add( getRenameProjectAction());
			projectPopup.add( getFindInProjectsAction());
		}
		
		return projectPopup;
	}

	private void addTemplates( URL base, XElement root) {
		try {
			if ( root.getName().equals( "templates")) {
				XElement[] templates = root.getElements( "template");
				
				if ( templates != null) {
					for ( int i = 0; i < templates.length; i++) {
						TemplateProperties template = new TemplateProperties( base, templates[i]);
						TemplateProperties existingTemplate = getExistingTemplate( properties, template);
						
						// remove previous scenario??
						if ( existingTemplate != null) {
							int value = MessageHandler.showConfirm( "A Template with this name already exists \""+existingTemplate.getName()+"\".\n"+
																	"Do you wan to override the existing template?");
							
							if ( value == JOptionPane.OK_OPTION){
								properties.removeTemplateProperties( existingTemplate);
								properties.addTemplateProperties( template);
							}
						} else {
							properties.addTemplateProperties( template);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}
	
	private void addTypes( URL base, XElement root) {
		try {
			if ( root.getName().equals( "types")) {
				XElement[] types = root.getElements( "type");
				
				if ( types != null) {
					for ( int i = 0; i < types.length; i++) {
						GrammarProperties type = new GrammarProperties( properties, base, types[i]);
						GrammarProperties existingType = getExistingGrammar( properties, type);
						
						// remove previous grammars
						if ( existingType != null) {
							int value = MessageHandler.showConfirm( "A XML Type with this name already exists \""+existingType.getDescription()+"\".\n"+
																	"Do you wan to override the existing type?");
							
							if ( value == JOptionPane.OK_OPTION){
								properties.removeGrammarProperties( existingType);
								properties.addGrammarProperties( existingType);
							}
						} else {
							properties.addGrammarProperties( type);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// nobody cares...
		}
	}
	
	private GrammarProperties getExistingGrammar( ConfigurationProperties properties, GrammarProperties grammar) {
		Vector grammars = properties.getGrammarProperties();
		
		for ( int i = 0; i < grammars.size(); i++) {
			GrammarProperties props = (GrammarProperties)grammars.elementAt(i);
			
			if ( grammar.getDescription().equals( props.getDescription())) {
				return props;
			}
		}
		
		return null;
	}

	private void addScenarios( URL base, XElement root) {
		try {
			if ( root.getName().equals( "scenarios")) {
				XElement[] scenarios = root.getElements( "scenario");
				
				if ( scenarios != null) {
					for ( int i = 0; i < scenarios.length; i++) {
						ScenarioProperties scenario = new ScenarioProperties( base, scenarios[i]);
						ScenarioProperties existingScenario = getExistingScenario( properties, scenario);
						
						// remove previous scenario??
						if ( existingScenario != null) {
							int value = MessageHandler.showConfirm( "A Scenario with this name already exists \""+existingScenario.getName()+"\".\n"+
																	"Do you wan to override the existing scenario?");
							
							if ( value == JOptionPane.OK_OPTION){
								properties.removeScenarioProperties( existingScenario);
								properties.addScenarioProperties( scenario);
							}
						} else {
							properties.addScenarioProperties( scenario);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ScenarioProperties getExistingScenario( ConfigurationProperties properties, ScenarioProperties scenario) {
		Vector scenarios = properties.getScenarioProperties();
		
		for ( int i = 0; i < scenarios.size(); i++) {
			ScenarioProperties props = (ScenarioProperties)scenarios.elementAt(i);
			
			if ( scenario.getName().equals( props.getName())) {
				return props;
			}
		}
		
		return null;
	}

	private TemplateProperties getExistingTemplate( ConfigurationProperties properties, TemplateProperties template) {
		Vector templates = properties.getTemplateProperties();
		
		for ( int i = 0; i < templates.size(); i++) {
			TemplateProperties props = (TemplateProperties)templates.elementAt(i);
			
			if ( template.getName().equals( props.getName())) {
				return props;
			}
		}
		
		return null;
	}

	public class ProjectTree extends QTree {
		public ProjectTree( TreeModel model) {
			super( model);
		}
		
		public boolean isPathEditable( TreePath path) {
			BaseNode node = (BaseNode) path.getLastPathComponent();
			
			if ( node instanceof DocumentNode) {
				return false;
			} else {
				return true;
			}
		}
	}

	/**
	 * @return
	 */
	public AddVirtualDirectoryAction getAddVirtualDirectoryAction() {

		if(addVirtualDirectoryAction == null) {
			addVirtualDirectoryAction = new AddVirtualDirectoryAction(parent, this);
		}
		
		return(addVirtualDirectoryAction);
	}
	
	/**
	 * @return
	 */
	public RefreshVirtualDirectoryAction getRefreshVirtualDirectoryAction() {

		if(refreshVirtualDirectoryAction == null) {
			refreshVirtualDirectoryAction = new RefreshVirtualDirectoryAction(parent, this);
		}
		
		return(refreshVirtualDirectoryAction);
	}

	/**
	 * Adds a virtual directory to the current selected folder/project... 
	 *
	 * @param dir the directory.
	 */
	public void addVirtualDirectory( File dir, boolean isStartup) {
		// add the folder...
		BaseNode node = getSelectedNode();
		
		if ( !dir.isDirectory()) {
			dir = dir.getParentFile();
		}
		
		if ( node instanceof FolderNode) {
			FolderNode folder = (FolderNode)node;
			FolderProperties props = folder.getProperties();
			
			props.addVirtualFolderProperties( new VirtualFolderProperties( dir));

			folder.update(isStartup);
			nodeChanged( folder);

			properties.save();
			tree.expandPath(new TreePath(node.getPath()));
		}
	}

	/**
	 * 
	 */
	public void refreshVirtualDirectory() {

		BaseNode baseNode = getSelectedNode();
		if(baseNode != null) {
			
			if(baseNode instanceof VirtualFolderNode) {
				
				boolean isStartup = false;
				((VirtualFolderNode)baseNode).update(isStartup);
				this.expandNode(baseNode);
			}
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeExpansionListener#treeCollapsed(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillCollapse(TreeExpansionEvent event) {
		//dont have to do anything
		final TreePath path = event.getPath();
		
		if ( path != null ) {
			Object obj = path.getLastPathComponent();
			if(obj instanceof BaseNode) {
				final BaseNode node = (BaseNode) obj;
				
				if(node instanceof VirtualFolderNode) {
					
					((VirtualFolderNode)node).setUpToDate(false);
				}
				else if(node instanceof FolderNode) {
					
					//go through children to set any virtual folders to not up to date
					
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.TreeExpansionListener#treeExpanded(javax.swing.event.TreeExpansionEvent)
	 */
	public void treeWillExpand(TreeExpansionEvent event) {

		final TreePath path = event.getPath();
		
		if ( path != null ) {
			Object obj = path.getLastPathComponent();
			if(obj instanceof BaseNode) {
				final BaseNode node = (BaseNode) obj;
				
				if(node instanceof VirtualFolderNode) {
					
					if(((VirtualFolderNode)node).isUpToDate() == false) {
				        parent.setStatus( "Building File List ...");
						parent.setWait( true);
					    		
						
			            
			            // Run in Thread!!!
			            Runnable runner = new Runnable() {
			                public void run()  {
			                    try {
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											((VirtualFolderNode)node).update(false);
											
											tree.expandPath(path);
										}
									});
			                    } catch ( Exception e) {
			                        // This should never happen, just report and continue
			                        MessageHandler.showError( parent, "Error - Cannot Add Nodes", "Add Node Error");
			                    } finally {
			                    	
									parent.setStatus( "Done");
									parent.setWait( false);
									
			                    }
			                }
			            };
			            
			            // Create and start the thread ...
			            Thread thread = new Thread( runner);
			            thread.start();
					}
	//	          }
		        
					
				}
			}
			else {
				//System.out.println("obj class: "+obj.getClass());
			}
		}
	}
} 
