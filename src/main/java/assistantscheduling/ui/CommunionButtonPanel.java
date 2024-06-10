package assistantscheduling.ui;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class CommunionButtonPanel extends JPanel implements ButtonPanel{

	private static final long serialVersionUID = 1L;

	public CommunionButtonPanel() {
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
	
	@Override
	public void nextAddActionListener(ActionListener a) {
		btnNext.addActionListener(a);		
	}

	@Override
	public void backAddActionListener(ActionListener a) {
		btnBack.addActionListener(a);		
	}

	@Override
	public void cancelAddActionListener(ActionListener a) {
		btnCancel.addActionListener(a);		
	}

}
