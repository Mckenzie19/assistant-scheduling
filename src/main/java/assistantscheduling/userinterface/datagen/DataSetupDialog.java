package assistantscheduling.userinterface.datagen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.ui.TextPrompt;
import assistantscheduling.ui.TextPrompt.Show;
import dataio.DataFileCreator;
import dataio.DataIO;
import net.miginfocom.swing.MigLayout;

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
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setSize(600, 220);
        setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		// Rather than creating a number of different dialogs, use the same dialog and use different panels
		page1Panel = new DataSetupPanel1(dataHandler, dataFileCreator, this);

		panels = new LinkedList<JPanel>(Arrays.asList(page1Panel, page2Panel));
		getContentPane().add(panels.poll(), BorderLayout.CENTER);
	}
	
	// Get rid of the old components and load in the new ones
	public void goToNextPanel() {
		getContentPane().removeAll();
		getContentPane().add(panels.poll(), BorderLayout.CENTER);
		repaint();
	}
	
}

