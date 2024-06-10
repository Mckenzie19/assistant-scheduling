package assistantscheduling.ui;

import java.awt.event.ActionListener;

import javax.swing.JButton;

interface ButtonPanel{
	public static final JButton btnNext = new JButton("Next>");
	public static final JButton btnBack = new JButton("<Back");
	public static final JButton btnCancel = new JButton("Cancel");

	public void nextAddActionListener(ActionListener a); // Adds listener to next button
	public void backAddActionListener(ActionListener a); // Adds listener to back button
	public void cancelAddActionListener(ActionListener a); // Adds listener to cancel button
}
