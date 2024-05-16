package assistantscheduling.userinterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.MatteBorder;

import org.optaplanner.core.api.solver.Solver;

// import com.sun.tools.javac.launcher.Main;

import assistantscheduling.domain.AssistantSchedule;

public class SwingInterface extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private AssistantSchedule problem;

	/**
	 * Create the main frame.
	 */
	public SwingInterface(Solver<AssistantSchedule> solver) {

		// Set the application dock icon
		Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
		URL imageResource = SwingInterface.class.getClassLoader().getResource("images/CLCLogoIcon.gif");
		Image image = defaultToolkit.getImage(imageResource);
		Taskbar taskbar = Taskbar.getTaskbar();
		 try {
	            //set icon for mac os (and other systems which do support this method)
	            taskbar.setIconImage(image);
	        } catch (final UnsupportedOperationException e) {
	            System.out.println("The os does not support: 'taskbar.setIconImage'");
	        } catch (final SecurityException e) {
	            System.out.println("There was a security exception for: 'taskbar.setIconImage'");
	        }
	        //set icon for windows os (and other systems which do support this method)
	        setIconImage(image);

		DataIO dataHandler = new DataIO();

		setFont(new Font("Proxima Nova", Font.BOLD, 14));
		setBackground(Color.DARK_GRAY);
		setTitle("CLC Worship Assistant Scheduler");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 589, 113);
		contentPane = new JPanel();
		contentPane.setBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		contentPane.setBackground(Color.LIGHT_GRAY);

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnImportData = new JButton("Set Service and Assistant Information");
		btnImportData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create a new JFrame container.
		        JFrame inputFrame = new JFrame("Select Data File");
		        inputFrame.setSize(400, 300);
		        inputFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		        inputFrame.setLocationRelativeTo(null);  // Center the window

		        // Call the method to select a file
		        File selectedFile = dataHandler.selectFile(inputFrame, false);

		        // Process the selected file (if any)
		        if (selectedFile != null) {
		            JOptionPane.showMessageDialog(inputFrame, "Selected file: " + selectedFile.getAbsolutePath());
		            dataHandler.setInputFile(selectedFile);
		        } else {
		            JOptionPane.showMessageDialog(inputFrame, "No file was selected. Aborting data import.", "IMPORT ERROR", JOptionPane.ERROR_MESSAGE);
		            // Close the application window after file selection
			        inputFrame.dispose();
		            return;
		        }

		        // Close the application window after file selection
		        inputFrame.dispose();
			}
		});
		btnImportData.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnImportData.setBackground(Color.WHITE);
		btnImportData.setBounds(55, 27, 170, 29);
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
		        URL dataURL = SwingInterface.class.getClassLoader().getResource("data/data.xlsx");
		        if (dataURL == null) {
		            JOptionPane.showMessageDialog(outputFrame, "No data present. Please set assistant and service data before building schedule.", "SCHEDULE BUILDING ERROR", JOptionPane.ERROR_MESSAGE);
		            outputFrame.dispose();
		            return;
		        }
		        
		        // TODO: Confirm schedule data. If correct, build schedule. If not, set new data.
		        
		        
		        problem = dataHandler.getData();
		        if (problem == null) {
		        	JFrame errorFrame = new JFrame("IMPORT ERROR");
		        	JOptionPane.showMessageDialog(errorFrame, "Error occured when importing schedule data. Please try again.", "IMPORT ERROR", JOptionPane.ERROR_MESSAGE);
		        	errorFrame.dispose();
		        }
		        
		        // TODO: Setup a waiting screen during solving
		        

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

	            JOptionPane.showMessageDialog(outputFrame, "Building schedule. Please do not close window or program!", "Building Schedule", JOptionPane.INFORMATION_MESSAGE);
				AssistantSchedule solution = solver.solve(problem);
		        dataHandler.saveSolution(solution);

	            JOptionPane.showMessageDialog(outputFrame, "Schedule complete!", "Building Schedule", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnGenerateNewSchedule.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnGenerateNewSchedule.setBounds(365, 27, 163, 29);
		contentPane.add(btnGenerateNewSchedule);
	}

}
