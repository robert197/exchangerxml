/*
 * Created on 14-Mar-2005
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.cladonia.xml.transform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.cladonia.util.loader.ExtensionClassLoader;
import com.cladonia.xngreditor.ExchangerEditor;
import com.cladonia.xngreditor.XngrDialog;
import javax.swing.JFrame;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;

import com.cladonia.xngreditor.scenario.ScenarioProperties;
import com.icl.saxon.Version;

import org.bounce.FormLayout;

/**
 * @author gmcgoldrick
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class XSLTProcessorDialog extends XngrDialog {
	private static final Dimension SIZE = new Dimension( 400, 300);
	private JRadioButton processorDefaultButton	= null;
	private JRadioButton processorXalanButton	= null;
	private JRadioButton processorSaxon1Button	= null;
	private JRadioButton processorSaxon2Button	= null;

	public XSLTProcessorDialog( JFrame parent) {
		super( parent, true);
		
		
		//this.parent = parent;
		
		//setResizable( false);
		//setTitle( "Execute XSLT");
		//setDialogDescription( "Specify XSL Transformation settings."); 
		JPanel processorPanel = new JPanel( new FormLayout( 10, 2));
		
		processorPanel.setBorder( new CompoundBorder( 
									new TitledBorder( "Processor"),
									new EmptyBorder( 0, 5, 5, 5)));
							
		ButtonGroup processorGroup = new ButtonGroup();		

		processorDefaultButton	= new JRadioButton( "Use Default Processor");
		processorPanel.add( processorDefaultButton, FormLayout.FULL);
		processorGroup.add( processorDefaultButton);

		processorPanel.add( getSeparator(), FormLayout.FULL_FILL);

		processorXalanButton	= new JRadioButton( "Xalan");
		processorPanel.add( processorXalanButton, FormLayout.FULL);
		processorGroup.add( processorXalanButton);

		processorSaxon1Button	= new JRadioButton( "Saxon (XSLT 1.X)");
		processorPanel.add( processorSaxon1Button, FormLayout.FULL);
		processorGroup.add( processorSaxon1Button);

		processorSaxon2Button	= new JRadioButton( "Saxon (XSLT 2.0)*");
		processorPanel.add( processorSaxon2Button, FormLayout.FULL);

		JLabel warning = new JLabel( "* Experimental version.");
		warning.setFont( warning.getFont().deriveFont( Font.PLAIN + Font.ITALIC));
		processorPanel.add( warning, FormLayout.FULL);

		processorGroup.add( processorSaxon2Button);
		JPanel dialogPanel = new JPanel(new BorderLayout());
		JPanel main = new JPanel( new BorderLayout());
		
		main.setBorder( new EmptyBorder( 2, 2, 5, 2));
		
		main.add( processorPanel, BorderLayout.CENTER);
		
//		JButton closeButton = new JButton( "OK");
//		closeButton.setMnemonic('O');
//		closeButton.addActionListener( new ActionListener() {
//			public void actionPerformed( ActionEvent e) {
//				hide();
//			}
//		});
		
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER, 0, 0));
		buttonPanel.setBorder( new EmptyBorder( 5, 0, 0, 0));
//		buttonPanel.add( closeButton);
		
		main.add( buttonPanel, BorderLayout.SOUTH);
		
	
		dialogPanel.add(main,BorderLayout.CENTER);
		
		setContentPane( dialogPanel);
		pack();

		setSize( new Dimension( 250, getSize().height));
		
	}
	
	private JComponent getSeparator() {
		JComponent separator = new JPanel();
		separator.setPreferredSize( new Dimension( 100, 10));

		return separator;
	}
	
	
	public int getProcessor() {
		int type = ScenarioProperties.PROCESSOR_DEFAULT;
		
		if ( processorSaxon1Button.isSelected()) {
			type = ScenarioProperties.PROCESSOR_SAXON_XSLT1;
		} else if ( processorSaxon2Button.isSelected()) {
			type = ScenarioProperties.PROCESSOR_SAXON_XSLT2;
		} else if ( processorXalanButton.isSelected()) {
			type = ScenarioProperties.PROCESSOR_XALAN;
		}

			
		return type;
	}

	public void setProcessor( int type) {

		switch ( type) {
			case ScenarioProperties.PROCESSOR_SAXON_XSLT1:
				processorSaxon1Button.setSelected( true);
				break;
			case ScenarioProperties.PROCESSOR_SAXON_XSLT2:
				processorSaxon2Button.setSelected( true);
				break;
			case ScenarioProperties.PROCESSOR_XALAN:
				processorXalanButton.setSelected( true);
				break;
			default:
				processorDefaultButton.setSelected( true);
				break;
		}
	}
}
