package assistantscheduling.userinterface;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class GraphicInterface {

	public static void createAnsShowGUI() {
		// Create and setup main window
		JFrame mainFrame = new JFrame("Worship Assistant Scheduler");
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		// mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);  // Center the window

        // Display the window
        mainFrame.pack();
        mainFrame.setVisible(true);
	}



}
