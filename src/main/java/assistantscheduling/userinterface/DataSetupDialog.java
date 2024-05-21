package assistantscheduling.userinterface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

public class DataSetupDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static final Logger LOGGER = LoggerFactory.getLogger(DataSetupDialog.class);
	private static DataIO dataHandler;

	/**
	 * Create the dialog.
	 */
	public DataSetupDialog(JFrame frame, String title, boolean modal, DataIO dataHandler) {
		super(frame, title, modal);
		DataSetupDialog.dataHandler = dataHandler;
		setSize(600, 220);
        setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout(
				"wrap 3",     // Layout constraints
				"5[right shrink]5[grow]5[shrink]5", // Column constraints
				"15[shrink][shrink][shrink]push[]5")); // Row constraints
		
		// Add components to dialog
		
		// First Row
		JLabel lblAssistantData = new JLabel("Assistant Data File");
		lblAssistantData.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(lblAssistantData, "shrinkx");
		
		JLabel lblAssisDataFileName = new JLabel("     ");
		lblAssisDataFileName.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		lblAssisDataFileName.setBackground(Color.WHITE);
		lblAssisDataFileName.setOpaque(true);
		contentPanel.add(lblAssisDataFileName, "growx");
		
		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickAssistantFile(lblAssisDataFileName);
			}
		});
		btnSelectFile.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		contentPanel.add(btnSelectFile, "shrinkx");

		// Second Row
		JLabel lblServiceDateRange = new JLabel("Service Date Range");
		lblServiceDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(lblServiceDateRange, "shrinkx");

		// Place 100 blank spaces for now because otherwise the background will not show up or will be sized to the entire cell
		JLabel lblSelectedDateRange = new JLabel("     ");
		lblSelectedDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		lblSelectedDateRange.setBackground(Color.WHITE);
		lblSelectedDateRange.setOpaque(true);
		contentPanel.add(lblSelectedDateRange, "growx");
		
		JButton btnSelectDateRange = new JButton("Select Date Range");
		btnSelectDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		contentPanel.add(btnSelectDateRange, "shrinkx");
		
		// Third Row
		JCheckBox chckbxSelectAdditionalServices = new JCheckBox("Select Additional Service Dates");
		chckbxSelectAdditionalServices.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(chckbxSelectAdditionalServices, "shrinkx");
		
		JLabel lblAdditionalServices = new JLabel("     ");
		lblAdditionalServices.setBackground(Color.WHITE);
		lblAdditionalServices.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		lblAdditionalServices.setOpaque(true);
		contentPanel.add(lblAdditionalServices, "span 2,growx");

		// Fourth Row
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(btnCancel, "tag cancel,alignx left");
		
		JButton btnPg1Next = new JButton("Next >>");
		btnPg1Next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LOGGER.info("Selected Page 1 Next button");
			}
		});
		btnPg1Next.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(btnPg1Next, "skip 1,right,tag next");
		
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
            label.setText(selectedFile.getName());
        }

        // Close the application window after file selection
        inputFrame.dispose();
	}

}
