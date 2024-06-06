package assistantscheduling.ui;

import net.miginfocom.swing.MigLayout;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.JTextArea;
import javax.swing.JLabel;

public class StartFormPanel extends FormPanel {

	private static final long serialVersionUID = 1L;
	private JLabel lblWelcomeTitle;
	private JTextArea txtrWelcomeMessage;
	
	/**
	 * Create the panel.
	 */
	public StartFormPanel() {
		setName("Start Panel");
		setLayout(new MigLayout("wrap 1", 
				"1[100%]1", 
				"[][]"));
		setBackground(Color.WHITE);
		setOpaque(true);
		setFocusable(true);
		
		// Initialize panel components
		lblWelcomeTitle = new JLabel("Welcome to the Worship Assistant Schedule Maker!");
		txtrWelcomeMessage = new JTextArea();
		
		// Set styles
		lblWelcomeTitle.setFont(StyleSettings.TITLE_FONT);
		txtrWelcomeMessage.setLineWrap(true);
		txtrWelcomeMessage.setWrapStyleWord(true);
		txtrWelcomeMessage.setEditable(false);
		txtrWelcomeMessage.setMargin(new Insets(5,5,5,5));
		txtrWelcomeMessage.setFont(StyleSettings.CONTENT_FONT);
		txtrWelcomeMessage.setText("Follow the instructions provided in order to create a worship assistant schedule. The steps are as follows:\n1. Select desired schedule dates.\n2. Import worship assistant information.\n3. Select output file location.\n\nIf you are importing new worship assistant information, you will need that file on hand. This will need to follow certain formatting rules, which can be found by clicking the help button below. Please have this file already filled out and saved prior to starting your schedule creation!");
		
		// Add components to panel
		add(lblWelcomeTitle, "shrink");
		add(txtrWelcomeMessage, "growx");

	}

	
	// Start Panel has no data to check so do nothing here
	@Override
	public void checkData() {
		return;
	}


	@Override
	public void loadData() {
		return;
	}

}
