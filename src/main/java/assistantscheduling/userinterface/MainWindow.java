package assistantscheduling.userinterface;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;

import org.optaplanner.core.api.solver.Solver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import com.sun.tools.javac.launcher.Main;

import assistantscheduling.domain.AssistantSchedule;

public class MainWindow{

	private static final Logger LOGGER = LoggerFactory.getLogger(MainWindow.class);
	private static DataIO dataHandler = new DataIO();
	private JFrame frame;
	private JPanel contentPane;
	private Solver<AssistantSchedule> solver;
	private AssistantSchedule problem;
	
	/**
	 * Create the application.
	 */
	public MainWindow(Solver<AssistantSchedule> solver) {
		// Set the application dock icon
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		URL imageResource = MainWindow.class.getClassLoader().getResource("images/CLCLogoIcon.gif");
		Image image = defaultToolkit.getImage(imageResource);
		Taskbar taskbar = Taskbar.getTaskbar();
		try {
			 //set icon for mac os (and other systems which do support this method)
		    taskbar.setIconImage(image);
		} catch (final UnsupportedOperationException e) {
			LOGGER.debug("The os does not support: 'taskbar.setIconImage'");
		} catch (final SecurityException e) {
		    LOGGER.debug("There was a security exception for: 'taskbar.setIconImage'");
		}
		this.solver = solver;
		initialize(image);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(Image image) {
		frame = new JFrame();
		//set icon for windows os (and other systems which do support this method)
        frame.setIconImage(image);

        frame.setFont(new Font("Proxima Nova", Font.BOLD, 14));
        frame.setBackground(Color.DARK_GRAY);
        frame.setTitle("CLC Worship Assistant Scheduler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(600, 150);
        frame.setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		contentPane.setBackground(Color.LIGHT_GRAY);

		frame.setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnImportData = new JButton("Set Service and Assistant Information");
		btnImportData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setScheduleData();
			}
		});
		btnImportData.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnImportData.setBackground(Color.WHITE);
		btnImportData.setBounds(50, 40, 245, 30);
		contentPane.add(btnImportData);

		JButton btnGenerateNewSchedule = new JButton("Build Schedule");
		btnGenerateNewSchedule.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create a new JFrame container.
		        JFrame outputFrame = new JFrame("Build Schedule");
		        outputFrame.setSize(400, 300);
		        outputFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		        outputFrame.setLocationRelativeTo(null);  // Center the window
		        
		        // Check if schedule data file is present.
		        URL dataURL = MainWindow.class.getClassLoader().getResource("data/data.xlsx");
		        if (dataURL == null) {
		            JOptionPane.showMessageDialog(outputFrame, "No data present. Please set assistant and service data before building schedule.", "SCHEDULE BUILDING ERROR", JOptionPane.ERROR_MESSAGE);
		            outputFrame.dispose();
		            return;
		        }
		        
		        // TODO: Confirm schedule data. If correct, build schedule. If not, set new data.
		        
		        
		        // Create optaplanner problem from the data file
		        problem = dataHandler.getData();
		        if (problem == null) {
		        	JFrame errorFrame = new JFrame("IMPORT ERROR");
		        	JOptionPane.showMessageDialog(errorFrame, "Error occured when importing schedule data. Please try again.", "IMPORT ERROR", JOptionPane.ERROR_MESSAGE);
		        	errorFrame.dispose();
		        }
		        
		        // TODO: Setup a waiting screen during solving
		        JOptionPane.showMessageDialog(outputFrame, "Building schedule. Please do not close window or program!", "Building Schedule", JOptionPane.INFORMATION_MESSAGE);
				AssistantSchedule solution = solver.solve(problem);

		        // After solving is complete, select a file to save the schedule to
		        // Call the method to select a save file
		        File outputFile = dataHandler.selectFile(outputFrame, true);
		        // Process the selected file (if any)
		        if (outputFile != null) {
		            JOptionPane.showMessageDialog(outputFrame, "Selected file: " + outputFile.getAbsolutePath());
		            dataHandler.setOutputFile(outputFile);
		        } else {
		            JOptionPane.showMessageDialog(outputFrame, "No save file selected. Aborting schedule building.", "SAVE ERROR", JOptionPane.ERROR_MESSAGE);
		            // Close the application window after file selection
			        outputFrame.dispose();
		            return;
		        }

		        dataHandler.saveSolution(solution);

	            JOptionPane.showMessageDialog(outputFrame, "Schedule complete!", "Building Schedule", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnGenerateNewSchedule.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnGenerateNewSchedule.setBounds(370, 40, 163, 30);
		contentPane.add(btnGenerateNewSchedule);
	}
	
	public JFrame getFrame() {
		return frame;
	}
	
	private void setScheduleData() {
		// 1: Open window with information such as Selected Assistant Data File, Selected Service Date Range, Special Service Dates, etc
		// 2: Make button for selected the assistant file, selecting service date range, and adding special services

		DataSetupDialog dialog = new DataSetupDialog(frame, "Set Assistant and Service Data", true, dataHandler);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
	}

}
