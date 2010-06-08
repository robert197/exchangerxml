/*
 * Created on 10-Dec-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.schema.generate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrDialog;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SchemaInstanceGenerationDialog extends XngrDialog /*implements ActionListener*/ {
	private static final Dimension SIZE = new Dimension( 350, 150);

	
	private JComboBox rootElement		= null;

	private JCheckBox optionalAttributesButton			= null;
	private JCheckBox optionalElementsButton	= null;
	private JCheckBox generateDataButton			= null;
	
	private ConfigurationProperties properties = null;
	
	
	
	//private WaitGlassPane waitPane = null;
	private ExchangerEditor parent = null;

	private JButton closeButton = null;
	private ExchangerDocument document = null;

	
	public SchemaInstanceGenerationDialog( ExchangerEditor parent, ConfigurationProperties props) {
		super( parent, true);
		
		setResizable( true);
		setTitle( "Instance Generation");
		setDialogDescription( "Generate Instance from XML Schema.");
		
		// Init a Glass Pane for the wait cursor
		//waitPane = new WaitGlassPane();
		//setGlassPane(waitPane);

		this.parent	= parent;
		//this.config	= props;
		//this.properties	= props.getSOAPProperties();
		
		JPanel main = new JPanel( new BorderLayout());
		main.setBorder( new EmptyBorder( 5, 5, 5, 5));

		main.getActionMap().put( "escapeAction", new AbstractAction() {
			public void actionPerformed( ActionEvent event) {
				cancelled = true;
				hide();
//				setVisible(false);
			}
		});
		main.getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0, false), "escapeAction");

		JPanel sigPanel = new JPanel( new FormLayout( 10, 0));
		sigPanel.setBorder( new EmptyBorder( 0, 0, 5, 0));
		sigPanel.addAncestorListener( new AncestorListener() {
			public void ancestorAdded( AncestorEvent e) {
			  rootElement.requestFocusInWindow();
			  rootElement.getEditor().selectAll();
			}

			public void ancestorMoved( AncestorEvent e) {}
			public void ancestorRemoved( AncestorEvent e) {}
		});
	
		rootElement = new JComboBox();
		rootElement.getEditor().getEditorComponent().addKeyListener( new KeyAdapter() {
			public void keyPressed( KeyEvent e) {
				if ( e.getKeyCode() == KeyEvent.VK_ENTER) {
					okButtonPressed();
				}
			}
		});

		rootElement.setFont( rootElement.getFont().deriveFont( Font.PLAIN));
		rootElement.setPreferredSize( new Dimension( 100, 23));
		rootElement.setEditable(true);

		//sigPanel.add( new JLabel("Root Element:"), FormLayout.LEFT);
		//sigPanel.add( rootElement, FormLayout.RIGHT_FILL);

		optionalAttributesButton = new JCheckBox( "Optional Attributes");
		optionalAttributesButton.setMnemonic( 'A');

		optionalElementsButton = new JCheckBox( "Optional Elements");
		optionalElementsButton.setMnemonic( 'E');

		generateDataButton = new JCheckBox( "Generate Content");
		generateDataButton.setMnemonic( 'C');
//		regularExpressionButton.setFont( regularExpressionButton.getFont().deriveFont( Font.PLAIN));

		JPanel optionsPanel = new JPanel( new FormLayout());
		optionsPanel.setBorder( new TitledBorder( "Options"));
//		matchCasePanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
		optionsPanel.add( optionalAttributesButton, FormLayout.FULL);
		optionsPanel.add( optionalElementsButton, FormLayout.FULL);
		optionsPanel.add( generateDataButton, FormLayout.FULL);

		//removed for xngr-dialog
		super.okButton.setText("OK");
		super.okButton.setMnemonic('O');
		
		//main.add( rootElement, BorderLayout.NORTH);
		main.add( optionsPanel, BorderLayout.CENTER);
		//main.add( buttonPanel, BorderLayout.SOUTH);

		setContentPane( main);
		
		/*addWindowListener( new WindowAdapter() {
			public void windowClosing( WindowEvent e) {
				cancelled = true;
				hide();
			}
		});
*/
		setDefaultCloseOperation( DO_NOTHING_ON_CLOSE);

		pack();
		setSize( new Dimension( Math.max( SIZE.width, getSize().width), getSize().height));

		setLocationRelativeTo( parent);
		
		
	}
	/**
	 * The send button has been pressed!
	 *
	 * @param e the event from the send button.
	 */
	public void actionPerformed( ActionEvent e) {

		if ( e.getSource() == closeButton) {
			hide();
//			setVisible(false);
		} 
	} 

	public void updatePreferences() {
		//inputPane.updatePreferences();
		//messagePane.updatePreferences();
		//outputPane.updatePreferences();
	}

	
	
	protected void okButtonPressed() {
		super.okButtonPressed(); 
	}

	protected void cancelButtonPressed() {
		super.cancelButtonPressed();
	}

	
	/**
	 * Returns the root element... 
	 *
	 * @return the root element.
	 */
	public String getRootElement() {
		String result = null;

		Object item = rootElement.getEditor().getItem();

		if ( item != null) {
			result = item.toString();
		}
		
		return result;
	}

	/**
	 * Wether OptionalAttributes should be generated. 
	 *
	 * @return true when OptionalAttributes should be generated.
	 */
	public boolean isOptionalAttributes() {
		return optionalAttributesButton.isSelected();
	}
	
	public boolean isOptionalElements() {
		return optionalElementsButton.isSelected();
	}

	/**
	 * Wether the search should be from top to bottom. 
	 *
	 * @return true when the search direction should be from top to bottom.
	 */
	public boolean isGenerateData() {
		return generateDataButton.isSelected();
	}

	
	
	/**
	 * Shows the SOAP dialog and sets the document...
	 *
	 * @param document the SOAP envelope.
	 */
	public void show( ExchangerDocument document) { // SOAPException, SAXParseException, IOException {
		this.document = document;
		

			setTitle( "Instance Generator []","XML Schema Document");

	}
	
	/**
	 * Sets the wait cursor on the Exchanger editor frame.
	 *
	 * @param enabled true when wait is enabled.
	 */
/*
	public void setWait(final boolean enabled) {
		parent.setWait( enabled);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				waitPane.setVisible(enabled);
			}
		});
	}

	private class WaitGlassPane extends JPanel {
		public WaitGlassPane() {
			setOpaque(false);
			addKeyListener(new KeyAdapter() {});
			addMouseListener(new MouseAdapter() {});
			super.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
	}

*/	
  public static void main(String[] args)
  {
  }
  
  
  
  
  
  
  
}
