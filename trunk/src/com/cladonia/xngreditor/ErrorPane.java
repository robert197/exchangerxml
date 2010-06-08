/*
 * $Id: ErrorPane.java,v 1.8 2005/06/23 15:14:05 tcurley Exp $
 *
 * Copyright (C) 2002, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.bounce.QLabel;
import org.bounce.event.DoubleClickListener;
import org.bounce.event.PopupListener;

import com.cladonia.xml.XMLError;
import com.cladonia.xml.editor.Editor;
//import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.component.GUIUtilities;
import com.cladonia.xngreditor.component.ScrollableListPanel;
import com.cladonia.xngreditor.plugins.PluginViewPanel;
import com.cladonia.xngreditor.properties.TextPreferences;

/**
 * The panel that shows parsing error information.
 *
 * @version	$Revision: 1.8 $, $Date: 2005/06/23 15:14:05 $
 * @author Dogsbay
 */
 public class ErrorPane extends JPanel {
 	private static final boolean DEBUG = false;
	private static final ImageIcon ERROR_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Error8.gif");
	private static final ImageIcon WARNING_ICON = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Warning8.gif");

	private static final EmptyBorder NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	private static final CompoundBorder TOP_BORDER = new CompoundBorder( new MatteBorder( 1, 0, 0, 0, UIManager.getColor("controlShadow")), new EmptyBorder( 2, 2, 2, 2));
	private static final MatteBorder BOTTOM_BORDER = new MatteBorder( 0, 0, 1, 0, UIManager.getColor("controlShadow"));

	private JList list = null;
	private ExchangerEditor parent = null;
 	private Editor editor = null;
 	//private Grid grid = null; 	
 	protected ErrorList errorList = null;
 	protected ErrorListModel model = null;
 	
 	public JPopupMenu errorPanePopup = null;
 	
 	public ErrorPane( ExchangerEditor xngreditor) {
 		super( new BorderLayout());
		this.parent = xngreditor;
		
		model = new ErrorListModel();
		list = new JList( model);
		list.setCellRenderer( new ErrorCellRenderer());
		list.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scroller = new JScrollPane( new ScrollableListPanel( list));
		
		add( scroller, BorderLayout.CENTER);
		scroller.getViewport().setBackground( list.getBackground());
		
		list.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				int index = list.getSelectedIndex();

				if ( index != -1) { // A row is selected...
					// perform the selection.
					errorSelected();
				}
			}
		});
		
		GotoAction gotoAction = new GotoAction();
		list.getActionMap().put( "gotoAction", gotoAction);
		list.getInputMap( JComponent.WHEN_FOCUSED).put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0, false), "gotoAction");

		updatePreferences();

		setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
		
		buildErrorPanePopupMenu();
		list.addMouseListener ( new PopupListener() {
            public void popupTriggered( MouseEvent e) {
                if(model.getSize() > 0) {
                    ErrorPane.this.parent.getCopyErrorListAction().setEnabled(true);
                }
                else {
                    ErrorPane.this.parent.getCopyErrorListAction().setEnabled(false);
                }
                ErrorPane.this.errorPanePopup.show( list, e.getX(), e.getY());
                
            }
		});
 	}
 	
 	private void buildErrorPanePopupMenu() {

        errorPanePopup = new JPopupMenu();
		
        errorPanePopup.add( this.parent.getCopyErrorListAction());
        
		GUIUtilities.alignMenu( errorPanePopup);
		    
    }
	
 	protected void errorSelected() {
 		Object item = list.getSelectedValue();
 		Object view1 = parent.getView().getCurrentView();
 		
 		if((parent.getView().getCurrentView() instanceof Editor) && (getEditor() != null) && (item != null) && (item instanceof XMLError)) {
 			XMLError error = (XMLError)item;
 			String systemId = error.getSystemId();

 			if ( systemId != null) {
 				String name = parent.getDocument().getName();
 				
 				if ( !systemId.endsWith( name)) {
 					// need to do this in a thread.
 					parent.open( URLUtilities.toURL( systemId), null, false);

 					ExchangerView view = parent.getView();
 					if ( view != null) {
 						view.getEditor().selectError( (XMLError)item);
 						view.getEditor().setFocus();
 					}
 					return;
 				}
 			}
 			getEditor().selectError( error);
			getEditor().setFocus();
 		}
 		else if((parent.getView().getCurrentView() instanceof PluginViewPanel) && (item != null) && (item instanceof XMLError)) {
 			XMLError error = (XMLError)item;
  		   
			String systemId = error.getSystemId();

			if ( systemId != null) {
				String name = parent.getDocument().getName();
				
				if ( !systemId.endsWith( name)) {
					// need to do this in a thread.
					parent.open( URLUtilities.toURL( systemId), null, false);

					ExchangerView view = parent.getView();
					if ( view != null) {
						parent.getView().getCurrentView().setFocus();
						((PluginViewPanel)parent.getView().getCurrentView()).selectError( (XMLError)item);
						//view.getEditor().setFocus();
						
						//ExchangerDocument doc = parent.getDocument();
						//doc.getElement(1);
					}
					return;
				}
			}
			
			//parent.switchToEditor();
			//editor = (Editor) parent.getView().getCurrentView();
			//editor.selectError( error);
			((PluginViewPanel)parent.getView().getCurrentView()).selectError( (XMLError)item);
			//editor.setFocus();
 		}
 		/*else if((parent.getView().getCurrentView() instanceof Grid) && (grid != null) && (item != null) && (item instanceof XMLError)) {
 		   XMLError error = (XMLError)item;
 		   
			String systemId = error.getSystemId();

			if ( systemId != null) {
				String name = parent.getDocument().getName();
				
				if ( !systemId.endsWith( name)) {
					// need to do this in a thread.
					parent.open( URLUtilities.toURL( systemId), null, false);

					ExchangerView view = parent.getView();
					if ( view != null) {
						grid.setFocus();
						view.getGrid().selectError( (XMLError)item);
						//view.getEditor().setFocus();
						
						//ExchangerDocument doc = parent.getDocument();
						//doc.getElement(1);
					}
					return;
				}
			}
			
			//parent.switchToEditor();
			//editor = (Editor) parent.getView().getCurrentView();
			//editor.selectError( error);
			grid.selectError(error);
			//editor.setFocus();
			
			
 		} */
 	}

 	public void setCurrent( Object view) {
		if (DEBUG) System.out.println( "ErrorPane.setCurrent( "+view+")");
		if ( view instanceof Editor) {
			setEditor((Editor)view);
			
		} /*if ( view instanceof Grid) {
			grid = (Grid) view;
			*/
		else if(view instanceof PluginViewPanel) {
			
		} /*else {
			editor = null;
			grid = null;
		}*/
	}

	public void setErrorList( ErrorList errors) {
 		if (DEBUG) System.out.println( "ErrorPane.setErrorList( "+errors+")");
		this.errorList = errors;
		
		list.setSelectedIndex(-1);
		model.clear();
		
		if ( errors != null) {
			model.addText( errors.getHeader());
			model.setList( errors.getErrors());
			model.addText( errors.getFooter());
		}
	}

	public void startCheck( String text) {
 		if (DEBUG) System.out.println( "ErrorPane.startCheck( "+text+")");

		list.setSelectedIndex(-1);
		model.clear();
 		errorList.reset();
 		errorList.setHeader( text);
		model.addText( text);
 	}

 	public void endCheck( String text) {
 		if (DEBUG) System.out.println( "ErrorPane.endCheck( "+text+")");

 		errorList.setFooter( text);
		model.addText( text);
 	}

 	public void clear() {
 		if (DEBUG) System.out.println( "ErrorPane.clear()");

 		errorList = null;
		list.setSelectedIndex(-1);
		model.clear();
 	}

 	public void select( XMLError error) {
 		if (DEBUG) System.out.println( "ErrorPane.select( "+error+")");
 		
 		int index = model.indexOf( error);
 		list.setSelectedIndex( index);
		list.ensureIndexIsVisible( index);
 	}

 	public void updatePreferences() {
 		if (DEBUG) System.out.println( "ErrorPane.updatePreferences()");
 		list.setFont( TextPreferences.getBaseFont().deriveFont( (float)12));
 	}

 	public void addError( XMLError error) {
		if (DEBUG) System.out.println( "ErrorPane.addError( "+error+")");
		errorList.addError( error);
		model.addError( error);
 	}
 	
 	public void addErrorSortedByLineNumber( XMLError error) {
		if (DEBUG) System.out.println( "ErrorPane.addErrorSortedByLineNumber( "+error+")");
		errorList.addErrorSortedByLineNumber( error);
		model.addErrorSortedByLineNumber( error);
 	}
 	
 	public void sortErrorsByLineNumber() {
 		errorList.sortErrorsByLineNumber();
 		setErrorList(errorList);
 	}
 	
	class ErrorListModel extends AbstractListModel {
		Vector errors = null;
		
		public ErrorListModel() {
			errors = new Vector();
		}
		
		public void setList( Vector list) {
			if ( list != null) {
				for ( int i = 0; i < list.size(); i++) {
					errors.addElement( list.elementAt(i));
				}
			}

			fireContentsChanged( this, 0, errors.size()-1);
		}

		public void clear() {
			int size = errors.size();
			errors.removeAllElements();

			fireIntervalRemoved( this, 0, 0);
		}

		public int getSize() {
			if ( errors != null) {
				return errors.size();
			}
			
			return 0;
		}

		public void addErrorSortedByLineNumber( XMLError error) {
			if(error != null) {
				if(errors != null) {
					boolean greaterThanFound = false;
					int cnt = 0;
					while((greaterThanFound == false) && (cnt < errors.size())) {
						
						Object tempObj = errors.get(cnt);
						if(tempObj instanceof XMLError) {
							XMLError tempError = (XMLError) tempObj;
							if(tempError != null) {
								if(error.getLineNumber() > tempError.getLineNumber()) {
									//add after
								}
								else if(error.getLineNumber() < tempError.getLineNumber()) {
									//add before now
									greaterThanFound = true;
								}
								else {
									
									if(error.getColumnNumber() > tempError.getColumnNumber()) {
										//add after
									}
									else if(error.getColumnNumber() < tempError.getColumnNumber()) {
										greaterThanFound = true;
									}
									else {
										//prob wont happen
									}
								}
							}							
						}
						if(greaterThanFound == false) {
							cnt++;
						}
					}
					

					if(errors.size() == 0) {
						errors.add(error);
					}
					else {
						//add before cnt
						errors.add(cnt, error);
						fireIntervalAdded( this, errors.size()-1, errors.size()-1);
					}
				}				
			}
		}
		
		public void addError( XMLError error) {
//			System.out.println( "addError( "+error+")");
			if ( error != null) {
				errors.addElement( error);
	
				fireIntervalAdded( this, errors.size()-1, errors.size()-1);
			}
		}

		public void addText( String text) {
//			System.out.println( "addText( "+text+")");
			if ( text != null) {
				// Find out where to insert the bookmark...
				errors.addElement( text);
	
				try {
					fireIntervalAdded( this, errors.size()-1, errors.size()-1);
				} catch(ArrayIndexOutOfBoundsException e) {
					e.printStackTrace();
				}
			}
		}

		public Object getElementAt( int i) {
			return errors.elementAt( i);
		}

		public Object getElement( int i) {
			return errors.elementAt( i);
		}
		
		public int indexOf( XMLError error) {
			for ( int i = 0; i < errors.size(); i++) {
				if ( errors.elementAt(i) == error) {
					return i;
				}
			}
			
			return -1;
		}
	}

	public class ErrorCellRenderer extends JPanel implements ListCellRenderer {
//		private JLabel icon = null;
		private JLabel text = null;
		private JLabel position = null;
		private QLabel message = null;
//		private JTextArea message = null;
		private JLabel file = null;

		/**
		 * The constructor for the renderer, sets the font type etc...
		 */
		public ErrorCellRenderer() {
			super( new BorderLayout());
			
//			icon = new JLabel();

			position = new JLabel();
			position.setBorder( new EmptyBorder( 0, 2, 0, 2));
			position.setOpaque( false);
			position.setFont( position.getFont().deriveFont( Font.BOLD));
			position.setForeground( Color.black);
			
			text = new JLabel();
			text.setBorder( new EmptyBorder( 2, 2, 2, 2));
			text.setFont( text.getFont().deriveFont( Font.PLAIN));
			text.setForeground( Color.black);
			text.setOpaque( true);
			text.setHorizontalAlignment( JLabel.LEFT);
			

			message = new QLabel();
			message.setBorder( new EmptyBorder( 0, 2, 0, 2));
			message.setOpaque( false);
			message.setFont( message.getFont().deriveFont( Font.PLAIN));
			message.setForeground( Color.black);
			message.setHorizontalAlignment( JLabel.LEFT);
			message.setLines(2);

			file = new JLabel();
			file.setOpaque( false);
			file.setFont( file.getFont().deriveFont( Font.PLAIN));
			file.setForeground( Color.black);
			file.setBorder( new EmptyBorder( 0, 2, 0, 2));
			
			JPanel northPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0));
			northPanel.add( position);
			northPanel.add( file);
			northPanel.setOpaque( false);
			
			this.add( northPanel, BorderLayout.NORTH);
			this.add( message, BorderLayout.CENTER);
			this.setBorder( TOP_BORDER);
		}
		
		public void setPreferredFont( Font font) {
			message.setFont( font.deriveFont( Font.PLAIN));
		}
		
		public Component getListCellRendererComponent( JList list, Object node, int index, boolean selected, boolean focus) {
			if ( node instanceof XMLError) {
	 			XMLError error = (XMLError)node;
	 			String systemId = error.getSystemId();

				file.setText( null);
// 				file.setIcon( null);
 				file.setVisible( false);
//
 				if ( systemId != null && parent.getDocument() != null) {
					String name = parent.getDocument().getName();
	 				
	 				if ( !systemId.endsWith( name)) {
	 					file.setText( "["+systemId+"]");
	 	 				file.setVisible( true);
	 				}
	 			}

				if ( error.getType() == XMLError.WARNING) {
					position.setIcon( WARNING_ICON);
				} else { 
					position.setIcon( ERROR_ICON);
				}

				position.setText( "Ln "+error.getLineNumber()+" Col "+error.getColumnNumber());

				message.setText( error.getMessage());

			} else {
				text.setText( node.toString());

				if ( selected) {
					text.setForeground( list.getSelectionForeground());
					text.setBackground( list.getSelectionBackground());
				} else {
					text.setForeground( list.getForeground());
					text.setBackground( list.getBackground());
				}

				text.setEnabled( list.isEnabled());
				
				int size = list.getModel().getSize();
				if ( size > 2 && size == index+1) {
					text.setBorder( TOP_BORDER);
				} else {
					text.setBorder( new EmptyBorder( 2, 2, 2, 2));
				}

				return text;
			}

			if ( selected) {
				message.setForeground( list.getSelectionForeground());
				position.setForeground( list.getSelectionForeground());
				file.setForeground( list.getSelectionForeground());
				this.setBackground( list.getSelectionBackground());
			} else {
				message.setForeground( list.getForeground());
				position.setForeground( list.getForeground());
				file.setForeground( list.getForeground());
				this.setBackground( list.getBackground());
			}

			setEnabled( list.isEnabled());
			setPreferredFont( list.getFont());

			return this;
		}
	} 

	public class GotoAction extends AbstractAction {
		public GotoAction() {
			super( "Goto Error");
		}

		public void actionPerformed( ActionEvent e) {
			errorSelected();
		}
	};
    /**
     * @return Returns the list.
     */
    public JList getList() {

        return list;
    }
    /**
     * @param list The list to set.
     */
    public void setList(JList list) {

        this.list = list;
    }

	public void setEditor(Editor editor) {
		this.editor = editor;
	}

	public Editor getEditor() {
		return editor;
	}
}
