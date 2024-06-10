package assistantscheduling.userinterface.datagen;

import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.dataio.DataFileCreator;
import assistantscheduling.dataio.DataIO;

public class DataSetupDialog extends JDialog {

	private DataFileCreator dataFileCreator = new DataFileCreator();
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetupDialog.class);

	private JPanel page1Panel;
	private JPanel page2Panel;
	private Queue<JPanel> panels;


	/**
	 * Create the dialog.
	 */
	public DataSetupDialog(JFrame frame, String title, boolean modal, DataIO dataHandler) {
		super(frame, title, modal);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 220);
        setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		// Rather than creating a number of different dialogs, use the same dialog and use different panels
		page1Panel = new DataSetupPanel1(dataHandler, dataFileCreator, this);

		panels = new LinkedList<>(Arrays.asList(page1Panel, page2Panel));
		getContentPane().add(panels.poll(), BorderLayout.CENTER);
	}

	// Get rid of the old components and load in the new ones
	public void goToNextPanel() {
		getContentPane().removeAll();
		getContentPane().add(panels.poll(), BorderLayout.CENTER);
		repaint();
	}

}

