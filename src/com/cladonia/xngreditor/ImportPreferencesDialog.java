package com.cladonia.xngreditor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.bounce.FormLayout;

import com.cladonia.xml.ExchangerDocument;
import com.cladonia.xngreditor.properties.ConfigurationProperties;

public class ImportPreferencesDialog extends XngrDialog {

	private static final Dimension SIZE = new Dimension(470, 580);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JCheckBox templatesCheckBox = null;
	private JCheckBox typesCheckBox = null;
	private JCheckBox samplesCheckBox = null;
	private JCheckBox scenariosCheckBox = null;
	
	private JLabel numberOfTemplatesLabel = null;
	private JLabel numberOfTypesLabel = null;
	private JLabel numberOfSamplesLabel = null;
	private JLabel numberOfScenariosLabel = null;
		
	private List oldTemplates = null;
	private List oldTypes = null;
	private List oldSamples = null;
	private List oldScenarios = null;
	
	public ImportPreferencesDialog(JFrame frame, boolean modal) {
		super(frame, modal);
		
		super.setTitle("Import Preferences...", "Import preferences from an older release",
        "Choose the various options to import with");
		
		JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(5, 5, 5, 5));
        
		JLabel questionLabel = new JLabel("Select which items to import: ");
		main.add(questionLabel, BorderLayout.NORTH);
		
		JPanel centrePanel = new JPanel(new FormLayout(3, 2));
		
		setTemplatesCheckBox(new JCheckBox("Templates"));
		numberOfTemplatesLabel = new JLabel("0 Found");
		setTypesCheckBox(new JCheckBox("Types"));
		numberOfTypesLabel = new JLabel("0 Found");
		setSamplesCheckBox(new JCheckBox("Samples"));
		numberOfSamplesLabel = new JLabel("0 Found");
		setScenariosCheckBox(new JCheckBox("Scenarios"));
		numberOfScenariosLabel = new JLabel("0 Found");
		
		centrePanel.add(getTemplatesCheckBox(), FormLayout.LEFT);
		centrePanel.add(numberOfTemplatesLabel, FormLayout.RIGHT_FILL);
		centrePanel.add(getTypesCheckBox(), FormLayout.LEFT);
		centrePanel.add(numberOfTypesLabel, FormLayout.RIGHT_FILL);
		centrePanel.add(getSamplesCheckBox(), FormLayout.LEFT);
		centrePanel.add(numberOfSamplesLabel, FormLayout.RIGHT_FILL);
		centrePanel.add(getScenariosCheckBox(), FormLayout.LEFT);
		centrePanel.add(numberOfScenariosLabel, FormLayout.RIGHT_FILL);
		
		main.add(centrePanel, BorderLayout.CENTER);
		setContentPane(main);
		pack();
        
        setDefaultCloseOperation(HIDE_ON_CLOSE);
	}
	
	
	public void show(List oldTemplates, List oldTypes, List oldSamples, List oldScenarios) {
		// TODO Auto-generated method stub
		
		
		this.oldTemplates = oldTemplates;
		this.numberOfTemplatesLabel.setText(this.oldTemplates.size() + " found");
		this.oldTypes = oldTypes;
		this.numberOfTypesLabel.setText(this.oldTypes.size() + " found");
		this.oldSamples = oldSamples;
		this.numberOfSamplesLabel.setText(this.oldSamples.size() + " found");
		this.oldScenarios = oldScenarios;
		this.numberOfScenariosLabel.setText(this.oldScenarios.size() + " found");
		
		super.show();
	}


	public void setTemplatesCheckBox(JCheckBox templatesCheckBox) {
		this.templatesCheckBox = templatesCheckBox;
	}


	public JCheckBox getTemplatesCheckBox() {
		return templatesCheckBox;
	}


	public void setTypesCheckBox(JCheckBox typesCheckBox) {
		this.typesCheckBox = typesCheckBox;
	}


	public JCheckBox getTypesCheckBox() {
		return typesCheckBox;
	}


	public void setSamplesCheckBox(JCheckBox samplesCheckBox) {
		this.samplesCheckBox = samplesCheckBox;
	}


	public JCheckBox getSamplesCheckBox() {
		return samplesCheckBox;
	}


	public void setScenariosCheckBox(JCheckBox scenariosCheckBox) {
		this.scenariosCheckBox = scenariosCheckBox;
	}


	public JCheckBox getScenariosCheckBox() {
		return scenariosCheckBox;
	}

}
