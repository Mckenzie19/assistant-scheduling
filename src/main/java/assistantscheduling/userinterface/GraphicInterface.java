package assistantscheduling.userinterface;

import java.io.File;

import javax.swing.*;

public class GraphicInterface {

	public static void createAnsShowGUI() {
		// Create and setup main window
		JFrame mainFrame = new JFrame("Worship Assistant Scheduler");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);  // Center the window

        // Display the window
        mainFrame.pack();
        mainFrame.setVisible(true);
	}
	
	
	
}
