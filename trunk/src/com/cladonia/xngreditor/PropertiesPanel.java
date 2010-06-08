/*
 * $Id: PropertiesPanel.java,v 1.10 2004/10/08 11:51:06 edankert Exp $
 *
 * Copyright (C) 2002 - 2004, Cladonia Ltd. All rights reserved.
 *
 * This software is the proprietary information of Cladonia Ltd.  
 * Use is subject to license terms.
 */
package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.MatteBorder;

import org.bounce.FormConstraints;
import org.bounce.FormLayout;
import org.bounce.QButton;
import org.bounce.event.DoubleClickListener;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.grammar.FragmentProperties;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * The panel that shows properties for the current document.
 *
 * @version	$Revision: 1.10 $, $Date: 2004/10/08 11:51:06 $
 * @author Dogsbay
 */
public class PropertiesPanel extends JPanel {
	public final static String INTERNAL_GRAMMAR = "Location defined in Document.";
	public final static String NO_LOCATION = "No location defined.";
	public final static String XML_DEFINED_IN_DOCUMENT = "Inferred from Document.";

	private ExchangerEditor parent = null;
	private ImageIcon downIcon = null;
	private ImageIcon upIcon = null;
	private ConfigurationProperties properties = null;
	
	private String validationLocation = null;
	private String tagCompletionLocation = null;
	private String schemaViewerLocation = null;

	private JList tagCompletionList							= null;
	private TagCompletionListModel tagCompletionListModel	= null;
	private static final FormConstraints LEFT_ALIGN_RIGHT_CENTER	= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT, FormConstraints.CENTER);
	private static final FormConstraints LEFT_ALIGN_RIGHT_TOP		= new FormConstraints( FormConstraints.LEFT, FormConstraints.RIGHT, FormConstraints.TOP);

//	private JList validationList						= null;
//	private TagCompletionListModel validationListModel	= null;
//
//	private JList schemaViewerList						= null;
//	private TagCompletionListModel schemaViewerListModel	= null;

	private JLabel nameField = null;
	private JLabel encodingField = null;
	private QButton validationField = null;
//	private QButton tagCompletionField = null;
	private QButton schemaViewerField = null;

	private JButton closeButton = null;
	private JPanel mainPanel = null;

	/**
	 * The constructor for the properties panel.
	 *
	 * @param properties the configuration properties.
	 */
	public PropertiesPanel( ConfigurationProperties props, ExchangerEditor editor) {
		super( new BorderLayout());
		
		this.parent = editor;
		this.properties = props;
		
		this.setBorder( 
				new CompoundBorder(
						new MatteBorder( 1, 1, 0, 0, Color.white),
						new MatteBorder(0, 0, 1, 1, UIManager.getColor("controlDkShadow"))));
//				new BevelBorder( BevelBorder.RAISED));

		JPanel titelPanel = new JPanel( new BorderLayout());
		titelPanel.add( new JLabel( "Document Properties"), BorderLayout.WEST);
		titelPanel.setBorder( new EmptyBorder( 0, 5, 0, 5));
		
		closeButton = new JButton();
		closeButton.setText(null);
		closeButton.setIcon( getUpIcon());
		closeButton.setBorder( null);
		closeButton.setMargin(new Insets(1, 1, 1, 1));
		closeButton.setOpaque( false);
		closeButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e) {
				mainPanel.setVisible( !mainPanel.isVisible());
				closeButton.setIcon( mainPanel.isVisible() ? getDownIcon() : getUpIcon());
				properties.setShowDocumentProperties( mainPanel.isVisible());
			}
		});
		closeButton.setFocusPainted( false);
		
		titelPanel.add( closeButton, BorderLayout.EAST);
		
		mainPanel = new JPanel( new BorderLayout());
		mainPanel.setBorder(
		new CompoundBorder( 
		new EmptyBorder( 0, 1, 2, 2), 
		new EtchedBorder( Color.white, UIManager.getColor("controlDkShadow"))));

//		new CompoundBorder( 
//				new EmptyBorder( 0, 1, 1, 2), 
//				new MatteBorder( 1, 1, 1, 1, UIManager.getColor("controlDkShadow"))));

		validationField = new QButton();
		validationField.setBorder( new EmptyBorder( 2, 2, 2, 2));
		validationField.setPreferredSize( new Dimension( 100, 20));
		validationField.setHorizontalAlignment( SwingConstants.LEFT);
		validationField.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent event) {
				open( validationLocation);
			}
		});
		validationField.setOpaque( false);
		validationField.setFont( validationField.getFont().deriveFont( Font.PLAIN, (float)11));

//		validationList = new JList();
//		validationList.addMouseListener( new DoubleClickListener() { 
//			public void doubleClicked( MouseEvent e) {
//				String location = (String)validationList.getSelectedValue();
//				
//				if ( location != null) {
//					open( location);
//				}
//			}
//		});
//		
//		validationList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
//		validationList.setVisibleRowCount( 1);
//		validationList.setCellRenderer( new TagCompletionListCellRenderer());
//		validationList.setFont( validationList.getFont().deriveFont( Font.PLAIN, (float)11));
//		
//		validationListModel = new TagCompletionListModel();
//		validationList.setModel( validationListModel);

		JPanel basePanel = new JPanel( new FormLayout( 0, 0));
		basePanel.setBorder( new EmptyBorder( 0, 2, 2, 2));
		basePanel.setBackground( Color.white);

		nameField = new JLabel();
		nameField.setFont( nameField.getFont().deriveFont( Font.PLAIN, (float)11));
		nameField.setOpaque( false);
		nameField.setPreferredSize( new Dimension( 100, 20));
		nameField.setBorder( new EmptyBorder( 0, 2, 0, 0));

		JLabel label = new JLabel( "Type:");
		label.setFont( label.getFont().deriveFont( Font.BOLD, (float)11));
		label.setOpaque( false);
//		basePanel.add( label, LEFT_ALIGN_RIGHT_CENTER);
		basePanel.add( label, FormLayout.LEFT);
		basePanel.add( nameField, FormLayout.RIGHT_FILL);

		encodingField = new JLabel();
		encodingField.setFont( encodingField.getFont().deriveFont( Font.PLAIN, (float)11));
		encodingField.setOpaque( false);
		encodingField.setPreferredSize( new Dimension( 100, 20));
		encodingField.setBorder( new EmptyBorder( 0, 2, 0, 0));

		label = new JLabel( "Encoding:");
		label.setFont( label.getFont().deriveFont( Font.BOLD, (float)11));
		label.setOpaque( false);
//		basePanel.add( label, LEFT_ALIGN_RIGHT_CENTER);
		basePanel.add( label, FormLayout.LEFT);
		basePanel.add( encodingField, FormLayout.RIGHT_FILL);

//		label = new JLabel();
//		label.setOpaque( false);
//		label.setPreferredSize( new Dimension( 100, 5));
//		basePanel.add( label, FormLayout.FULL);

		label = new JLabel( "Validation:");
		label.setFont( label.getFont().deriveFont( Font.BOLD, (float)11));
		label.setOpaque( false);
//		basePanel.add( label, LEFT_ALIGN_RIGHT_CENTER);
		basePanel.add( label, FormLayout.LEFT);

//		JScrollPane scroller = new JScrollPane(	validationList,
//				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
//				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		basePanel.add( validationField, FormLayout.RIGHT_FILL);
		
		schemaViewerField = new QButton();
		schemaViewerField.setBorder( new EmptyBorder( 2, 2, 2, 2));
		schemaViewerField.setPreferredSize( new Dimension( 100, 20));
		schemaViewerField.setHorizontalAlignment( SwingConstants.LEFT);
		schemaViewerField.addMouseListener( new DoubleClickListener() {
			public void doubleClicked( MouseEvent event) {
				open( schemaViewerLocation);
			}
		});
		schemaViewerField.setOpaque( false);
		schemaViewerField.setFont( schemaViewerField.getFont().deriveFont( Font.PLAIN, (float)11));

//		schemaViewerList = new JList();
//		schemaViewerList.addMouseListener( new DoubleClickListener() { 
//			public void doubleClicked( MouseEvent e) {
//				String location = (String)schemaViewerList.getSelectedValue();
//				
//				if ( location != null) {
//					open( location);
//				}
//			}
//		});
//		
//		schemaViewerList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
//		schemaViewerList.setVisibleRowCount( 1);
//		schemaViewerList.setCellRenderer( new TagCompletionListCellRenderer());
//		schemaViewerList.setFont( schemaViewerList.getFont().deriveFont( Font.PLAIN, (float)11));
//		
//		schemaViewerListModel = new TagCompletionListModel();
//		schemaViewerList.setModel( schemaViewerListModel);

		label = new JLabel( "Schema:");
		label.setFont( label.getFont().deriveFont( Font.BOLD, (float)11));
		label.setOpaque( false);
//		basePanel.add( label, LEFT_ALIGN_RIGHT_CENTER);
		basePanel.add( label, FormLayout.LEFT);

		basePanel.add( schemaViewerField, FormLayout.RIGHT_FILL);

//		JPanel schemaViewerPanel = new JPanel( new FormLayout( 0, 0));
//		schemaViewerPanel.setBackground( Color.white);
//		schemaViewerPanel.setBorder( new CompoundBorder( 
//				new TitledBorder( "Schema Viewer/Outliner"),
//				new EmptyBorder( 0, 3, 3, 3)));
//
//		schemaViewerPanel.add( schemaViewerField, FormLayout.FULL_FILL);
//		basePanel.add( schemaViewerPanel, FormLayout.FULL_FILL);

		tagCompletionList = new JList();
		tagCompletionList.addMouseListener( new DoubleClickListener() { 
			public void doubleClicked( MouseEvent e) {
				String location = (String)tagCompletionList.getSelectedValue();
				
				if ( location != null && !location.equals( XML_DEFINED_IN_DOCUMENT)) {
					open( location);
				}
			}
		});
		
		tagCompletionList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION);
		tagCompletionList.setVisibleRowCount( 1);
		tagCompletionList.setCellRenderer( new TagCompletionListCellRenderer());
		tagCompletionList.setFont( tagCompletionList.getFont().deriveFont( Font.PLAIN, (float)11));
		tagCompletionList.setBackground( basePanel.getBackground());
		tagCompletionList.setOpaque( false);
		
		tagCompletionListModel = new TagCompletionListModel();
		tagCompletionList.setModel( tagCompletionListModel);

		label = new JLabel( "Completion:");
		label.setFont( label.getFont().deriveFont( Font.BOLD, (float)11));
		label.setPreferredSize( new Dimension( label.getPreferredSize().width, 19));
		label.setOpaque( false);
		basePanel.add( label, LEFT_ALIGN_RIGHT_TOP);

		JScrollPane scroller = new JScrollPane(	tagCompletionList,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroller.setPreferredSize( new Dimension( 100, tagCompletionList.getPreferredSize().height+2));
		scroller.getViewport().setBackground( basePanel.getBackground());
		basePanel.add( scroller, FormLayout.RIGHT_FILL);
		scroller.setBackground( basePanel.getBackground());
		scroller.setBorder( new EmptyBorder( 2, 1, 0, 0));

		mainPanel.add( basePanel, BorderLayout.CENTER);
//		mainPanel.add( new JLabel( "Schema Viewer/Outliner:"), FormLayout.FULL);
//		mainPanel.add( schemaViewerField, FormLayout.FULL_FILL);

		this.add( titelPanel, BorderLayout.NORTH);
		this.add( mainPanel, BorderLayout.CENTER);
		
		init();
	}
	
	private void open( String location) {
		if ( location == INTERNAL_GRAMMAR) {
			ExchangerDocument doc = parent.getDocument();
			
			URL url = null;
			
			try{ 
				url = doc.checkSchemaLocation();
			} catch ( IOException e){
				// do not care
			}
			
			if ( url != null) {
				location = url.toString();
			} else {
				ExchangerView view = parent.getView();

				if ( view != null) {
					view.getCurrentView().setFocus();
				}

				return;
			}
		}

		if ( location != NO_LOCATION && location != null) {
			final URL url = URLUtilities.toURL( location);
			
			if ( url != null) {
		 		parent.setWait( true);
		 		parent.setStatus( "Opening ...");
		
		 		// Run in Thread!!!
		 		Runnable runner = new Runnable() {
		 			public void run()  {
				 		try {
							parent.open( url, null, true);
				 		} finally {
				 			ExchangerView view = parent.getView();

				 			if ( view != null && view.getCurrentView() != null) {
				 				view.getCurrentView().setFocus();
				 			}
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

		ExchangerView view = parent.getView();

		if ( view != null) {
			view.getCurrentView().setFocus();
		}
	}
	
	public void init() {
		mainPanel.setVisible( properties.isShowDocumentProperties());
		closeButton.setIcon( properties.isShowDocumentProperties() ? getDownIcon() : getUpIcon());
	}

	public void clear() {
		setValidationLocation( null);
		setTagCompletionLocations( null);
		setSchemaViewerLocation( null);
		setName( "");
		setEncoding( "");
//		mainPanel.setVisible( false);
//		properties.setShowDocumentProperties( false);
//		closeButton.setIcon( getUpIcon());
	}
	
	public void setName( String name) {
		nameField.setText( name);
	}

	public void setEncoding( String encoding) {
		encodingField.setText( encoding);
	}

	public void setValidationLocation( String location) {
		validationLocation = location;

		if ( location == null) {
			validationField.setText( "");
			validationField.setIcon( null);
			validationField.setToolTipText( "");
		} else if ( location == INTERNAL_GRAMMAR || location == NO_LOCATION) {
			validationField.setText( location);
			validationField.setIcon( null);
			validationField.setToolTipText( location);
		} else {
			validationField.setText( URLUtilities.getFileName( location));
			validationField.setIcon( IconFactory.getIconForExtension( URLUtilities.getExtension( location)));
			validationField.setToolTipText( URLUtilities.toRelativeString( URLUtilities.toURL( location)));
		}
	}

	public void setTagCompletionLocations( Vector locations) {
		if ( locations != null && locations.size() == 0) {
			locations.addElement( XML_DEFINED_IN_DOCUMENT);
		}

		tagCompletionListModel.setTagCompletionList( locations);
	}

	public void setSchemaViewerLocation( String location) {
		schemaViewerLocation = location;

		if ( location == null) {
			schemaViewerField.setText( "");
			schemaViewerField.setIcon( null);
			schemaViewerField.setToolTipText( "");
		} else if ( location == INTERNAL_GRAMMAR || location == NO_LOCATION) {
			schemaViewerField.setText( location);
			schemaViewerField.setIcon( null);
			schemaViewerField.setToolTipText( location);
		} else {
			schemaViewerField.setText( URLUtilities.getFileName( location));
			schemaViewerField.setIcon( IconFactory.getIconForExtension( URLUtilities.getExtension( location)));
			schemaViewerField.setToolTipText( URLUtilities.toRelativeString( URLUtilities.toURL( location)));
		}
	}
	
	private ImageIcon getUpIcon() { 
		if ( upIcon == null) {
			upIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Up8.gif");
		}
		
		return upIcon;
	}

	private ImageIcon getDownIcon() { 
		if ( downIcon == null) {
			downIcon = XngrImageLoader.get().getImage( "com/cladonia/xngreditor/icons/Down8.gif");
		}
		
		return downIcon;
	}

	class TagCompletionListModel extends AbstractListModel {
		Vector elements = null;
		FragmentProperties defaultScenario = null; 
		
		public TagCompletionListModel() {
			elements = new Vector();
		}
		
		public int getSize() {
			if ( elements != null) {
				return elements.size();
			}
			
			return 0;
		}

		public void setTagCompletionList( Vector list) {
			if ( list != null) {
				this.elements = new Vector( list);
				
				fireContentsChanged( this, 0, elements.size()-1);
			} else {
				this.elements = new Vector();

				fireContentsChanged( this, 0, 0);
			}
		}

		public Vector getTagCompletionList() {
			return elements; 
		}

		public Object getElementAt( int i) {
			return elements.elementAt( i);
		}

		public String getTagCompletionLocation( int i) {
			return (String)elements.elementAt( i);
		}
	}

	class TagCompletionListCellRenderer extends JLabel implements ListCellRenderer {
//		private JLabel type 		= null;
//		private JLabel location		= null;
		
		public TagCompletionListCellRenderer() {
			setOpaque( false);
			setBorder( new EmptyBorder( 0, 1, 0, 0));
		}
		
		public Component getListCellRendererComponent(JList list,Object value,int selectedIndex,boolean isSelected, boolean isFocus) {	
			boolean clash = false;
			
			if ( value instanceof String) {
				if ( !value.equals( XML_DEFINED_IN_DOCUMENT)) {
					setText( URLUtilities.getFileName( (String)value));
					setIcon( IconFactory.getIconForExtension( URLUtilities.getExtension( (String)value)));
				} else {
					setText( XML_DEFINED_IN_DOCUMENT);
					setIcon( null);
				}

				setToolTipText( (String)value);
			}
			
			if (isSelected && list.isEnabled() && isFocus) {
				setOpaque( true);
				setBackground(list.getSelectionBackground());
				setForeground( list.getSelectionForeground());
			} else {
				setOpaque( false);
				setBackground( list.getBackground());
				setForeground( list.getForeground());
			}

			setEnabled(list.isEnabled());
			
			setFont( list.getFont().deriveFont( Font.PLAIN));

			return this;
		}
	}
} 
