package assistantscheduling.ui;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class DataCollectionButtonPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	public static final JButton btnNext = new JButton("Next>");
	public static final JButton btnBack = new JButton("<Back");
	public static final JButton btnCancel = new JButton("Cancel");

	public DataCollectionButtonPanel() {
		setLayout(new MigLayout("wrap 3", "[]push[][]", "[]"));
		setFocusable(true);

		// Set style and formatting
		btnNext.setFont(StyleSettings.SUBTITLE_FONT);
		btnBack.setFont(StyleSettings.SUBTITLE_FONT);
		btnCancel.setFont(StyleSettings.SUBTITLE_FONT);

		add(btnCancel);
		add(btnBack);
		add(btnNext);
	}
	
	public void nextAddActionListener(ActionListener a) {
		btnNext.addActionListener(a);		
	}

	public void backAddActionListener(ActionListener a) {
		btnBack.addActionListener(a);		
	}

	public void cancelAddActionListener(ActionListener a) {
		btnCancel.addActionListener(a);		
	}

}
