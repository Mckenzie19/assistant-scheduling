package assistantscheduling.userinterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

public class DataSetupDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private DataFileCreator dataFileCreator = new DataFileCreator();
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetupDialog.class);
	private static DataIO dataHandler;
	
	// Declare dialog components
	JLabel lblAssistantData;
	JLabel lblAssisDataFileName;
	JButton btnSelectFile;
	JLabel lblServiceDateRange;
	JLabel lblSelectedDateRange;
	JButton btnSelectDateRange;
	JCheckBox chckbxSelectAdditionalServices;
	JTextField txtAdditionalServices;
	TextPrompt tpAddServices;
	JLabel warningLabel;
	JButton btnCancel;
	JButton btnPg1Next;
	
	/**
	 * Create the dialog.
	 */
	public DataSetupDialog(JFrame frame, String title, boolean modal, DataIO dataHandler) {
		super(frame, title, modal);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		DataSetupDialog.dataHandler = dataHandler;
		setSize(600, 220);
        setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout(
				"wrap 3",     // Layout constraints
				"5[right shrink]5[grow]5[shrink]5", // Column constraints
				"15[shrink][shrink][shrink][shrink]push[]5")); // Row constraints
		
		// Add components to dialog
		// First Row
		lblAssistantData = new JLabel("Assistant Data File");
		lblAssistantData.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(lblAssistantData, "shrinkx");
		
		lblAssisDataFileName = new JLabel(" ");
		lblAssisDataFileName.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		lblAssisDataFileName.setBackground(Color.WHITE);
		lblAssisDataFileName.setOpaque(true);
		contentPanel.add(lblAssisDataFileName, "growx");
		
		btnSelectFile = new JButton("Select File");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickAssistantFile(lblAssisDataFileName);
			}
		});
		btnSelectFile.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		contentPanel.add(btnSelectFile, "shrinkx");

		
		// Second Row
		lblServiceDateRange = new JLabel("Service Date Range");
		lblServiceDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(lblServiceDateRange, "shrinkx");

		lblSelectedDateRange = new JLabel(" ");
		lblSelectedDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		lblSelectedDateRange.setBackground(Color.WHITE);
		lblSelectedDateRange.setOpaque(true);
		contentPanel.add(lblSelectedDateRange, "growx");
		
		btnSelectDateRange = new JButton("Select Date Range");
		btnSelectDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnSelectDateRange.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectDateRange();
			}
		});
		contentPanel.add(btnSelectDateRange, "shrinkx");
		
		
		// Third Row
		chckbxSelectAdditionalServices = new JCheckBox("Select Additional Service Dates");
		chckbxSelectAdditionalServices.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// Add Dates
					chckbxSelectAdditionalServices.setForeground(Color.BLACK);
					tpAddServices.setForeground(Color.GRAY);
					txtAdditionalServices.setEditable(true);
				} else {
					// Clear selection
					chckbxSelectAdditionalServices.setForeground(Color.GRAY);
					txtAdditionalServices.setText("");
					txtAdditionalServices.setBorder(null);
					tpAddServices.setForeground(Color.LIGHT_GRAY);
					txtAdditionalServices.setEditable(false);
					warningLabel.setVisible(false);
					dataFileCreator.setAdditionalServices(null);
				}
			}
		});
		chckbxSelectAdditionalServices.setForeground(Color.GRAY);
		chckbxSelectAdditionalServices.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(chckbxSelectAdditionalServices, "shrinkx");
		
		txtAdditionalServices = new JTextField("");
		txtAdditionalServices.setBackground(Color.WHITE);
		txtAdditionalServices.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		txtAdditionalServices.setOpaque(true);
		txtAdditionalServices.setForeground(Color.BLACK);
		txtAdditionalServices.setEditable(false);
		txtAdditionalServices.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				txtAdditionalServices.setBorder(null);
				warningLabel.setVisible(false);
			}

			@Override
			public void focusLost(FocusEvent e) {
				checkAdditionalDateFormatting();
			}
		});
		txtAdditionalServices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkAdditionalDateFormatting();
			}
		});
		contentPanel.add(txtAdditionalServices, "span 2,growx");
		// Adds a text prompt that overlays the above text field. Will only show up when the window focus is not on the text field.
		tpAddServices = new TextPrompt("MM/DD/YYYY, MM/DD/YYYY, etc.", txtAdditionalServices, TextPrompt.Show.FOCUS_LOST);
		tpAddServices.setForeground(Color.LIGHT_GRAY);
		
		// Fourth Row
		warningLabel = new JLabel("Please ensure dates follow requested formatting");
		warningLabel.setFont(new Font("Proxima Nova", Font.ITALIC, 12));
		warningLabel.setForeground(Color.RED);
		warningLabel.setVisible(false);
		contentPanel.add(warningLabel, "span2,shrinkx,wrap");

		
		// Fifth Row
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(btnCancel, "tag cancel,alignx left");
		
		btnPg1Next = new JButton("Next >>");
		btnPg1Next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		btnPg1Next.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(btnPg1Next, "skip 1,right,tag next");
		
		contentPanel.setFocusable(true);
		contentPanel.addMouseListener(new MyMouseListener());
		
	}
	
	private void pickAssistantFile(JLabel label) {
		// Create a new JFrame container.
        JFrame inputFrame = new JFrame("Select Assistant Data File");
        inputFrame.setSize(400, 300);
        inputFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        inputFrame.setLocationRelativeTo(null);  // Center the window

        // Call the method to select a file
        File selectedFile = dataHandler.selectFile(inputFrame, false);

        // Process the selected file (if any)
        if (selectedFile != null) {
            label.setText("  " + selectedFile.getName());
            dataFileCreator.setAssistantDataFile(selectedFile);
        }

        // Close the application window after file selection
        inputFrame.dispose();
	}

	private void selectDateRange() {
		// Clear old date range
		dataFileCreator.setServiceRange(null);
		lblSelectedDateRange.setText("  ");
		// Get new date range
		DateRangeSelector selector = new DateRangeSelector((JFrame) getParent(), "Select Schedule Date Range", true, dataFileCreator);
		selector.setVisible(true);
		LocalDate[] dateRange = dataFileCreator.getServiceRange();
		if (dateRange != null) lblSelectedDateRange.setText(dateRange[0].toString() + "  :  " + dateRange[1].toString());
	}

	class MyMouseListener extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			// Check if user left clicked
			if (e.getButton() == MouseEvent.BUTTON1) {
				// Check if there are no components were the user clicked, aka if the returned component is the JPanel
				Component c = contentPanel.getComponentAt(e.getPoint());
				if (c instanceof JPanel) {
					LOGGER.info("Content Panel requesting focus in window...");
					contentPanel.requestFocusInWindow();
				}
			}
		}
	}
	
	public void checkAdditionalDateFormatting() {
		String fieldText = txtAdditionalServices.getText();
		if (fieldText.length() == 0) return;
		try {
			String[] splitText = fieldText.split(", ", 0);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[MM/dd/yyyy][MM/d/yyyy][M/dd/yyyy][M/d/yyyy]", Locale.ENGLISH);
			LocalDate[] dates = new LocalDate[splitText.length];
			for (int i = 0; i < splitText.length; i++) {
				dates[i] = LocalDate.parse(splitText[i], dtf);
				// If date is before today's date, then it is not a valid input
				if (dates[i].isBefore(LocalDate.now())) {
					LOGGER.warn("Attempted to add past date.");
					warningLabel.setText("All dates entered must be future dates");
					txtAdditionalServices.setBorder(new LineBorder(Color.RED));
					warningLabel.setVisible(true);
					dataFileCreator.setAdditionalServices(null);
					return;
				}
			}
			dataFileCreator.setAdditionalServices(dates);
			txtAdditionalServices.setBorder(null);
			warningLabel.setVisible(false);
		} catch (DateTimeParseException de) {
			LOGGER.warn("Improper date formatting entered: \n" + de.getMessage());
			warningLabel.setText("Please ensure dates follow requested formatting");
			txtAdditionalServices.setBorder(new LineBorder(Color.RED));
			warningLabel.setVisible(true);
			dataFileCreator.setAdditionalServices(null);
			return;
		}
	}
	
}

