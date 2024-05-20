package assistantscheduling.userinterface;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class DataSetupDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	static DataIO dataHandler;

	/**
	 * Create the dialog.
	 */
	public DataSetupDialog(JFrame frame, String title, boolean modal, DataIO dataHandler) {
		super(frame, title, modal);
		DataSetupDialog.dataHandler = dataHandler;
		
		setSize(600, 214);
        setLocationRelativeTo(null);
        
        getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(null);
		contentPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JButton btnPg1Next = new JButton("Next >>");
		btnPg1Next.setBounds(503, 151, 91, 29);
		btnPg1Next.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		contentPanel.add(btnPg1Next);
		
		// Used to get information about the assistant data file. Has a label indicating what this is, 
		// a place for the file name to appear, and a button for choosing the file
		JPanel AssistantPanel = new JPanel();
		AssistantPanel.setBounds(0, 0, 600, 46);
		contentPanel.add(AssistantPanel);
		FormLayout fl_AssistantPanel = new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("125px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("355px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("100px"),},
			new RowSpec[] {
				RowSpec.decode("46px"),});
		AssistantPanel.setLayout(fl_AssistantPanel);
		
		JLabel lblAssistantData = new JLabel("Assistant Data File");
		lblAssistantData.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		AssistantPanel.add(lblAssistantData, "1, 1, right, center");
		
		JLabel lblAssisDataFileName = new JLabel("                                                                                                                   ");
		lblAssisDataFileName.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		lblAssisDataFileName.setSize(300, 30);
		lblAssisDataFileName.setHorizontalAlignment(SwingConstants.LEFT);
		lblAssisDataFileName.setBackground(Color.WHITE);
		lblAssisDataFileName.setOpaque(true);
		AssistantPanel.add(lblAssisDataFileName, "3, 1, left, center");
		
		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pickAssistantFile();
			}
		});
		btnSelectFile.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		AssistantPanel.add(btnSelectFile, "5, 1, center, center");
		
		// Used to select the date range of Sunday services to schedule for. Includes a label indicated what this is, 
		// a place for the selected date ranges to appear, and a button for selecting the date ranges
		JPanel ServicePanel = new JPanel();
		ServicePanel.setBounds(0, 50, 600, 50);
		contentPanel.add(ServicePanel);
		ServicePanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("150px"),
				ColumnSpec.decode("300px"),
				ColumnSpec.decode("150px"),},
			new RowSpec[] {
				RowSpec.decode("50px"),}));
		
		JLabel lblServiceDateRange = new JLabel("Service Date Range");
		lblServiceDateRange.setHorizontalAlignment(SwingConstants.RIGHT);
		lblServiceDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		ServicePanel.add(lblServiceDateRange, "1, 1, center, center");
		
		// Place 100 blank spaces for now because otherwise the background will not show up or will be sized to the entire cell
		JLabel lblSelectedDateRange = new JLabel("                                                                                                    ");
		lblSelectedDateRange.setSize(100, 30);
		lblSelectedDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		lblSelectedDateRange.setHorizontalAlignment(SwingConstants.LEFT);
		lblSelectedDateRange.setBackground(Color.WHITE);
		lblSelectedDateRange.setOpaque(true);
		ServicePanel.add(lblSelectedDateRange, "2, 1, center, center");
		
		JButton btnSelectDateRange = new JButton("Select Date Range");
		btnSelectDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		ServicePanel.add(btnSelectDateRange, "3, 1, center, center");
		
		
		// Optional place to select additional services outside of the generated Sunday services from the previous section. Includes a
		// checkbox to indicate if this option is desired as well as select the dates desired and a label to show selected services
		JPanel AdditionalServicesPanel = new JPanel();
		AdditionalServicesPanel.setBounds(0, 100, 600, 50);
		contentPanel.add(AdditionalServicesPanel);
		AdditionalServicesPanel.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("225px"),
				ColumnSpec.decode("10px"),
				ColumnSpec.decode("365px"),},
			new RowSpec[] {
				RowSpec.decode("50px"),}));
		
		JCheckBox chckbxSelectAdditionalServices = new JCheckBox("Select Additional Service Dates");
		chckbxSelectAdditionalServices.setHorizontalAlignment(SwingConstants.CENTER);
		AdditionalServicesPanel.add(chckbxSelectAdditionalServices, "1, 1, center, center");
		chckbxSelectAdditionalServices.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		
		JLabel lblNewLabel = new JLabel("                                                                                                              ");
		lblNewLabel.setSize(300, 30);
		lblNewLabel.setBackground(Color.WHITE);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		lblNewLabel.setOpaque(true);
		AdditionalServicesPanel.add(lblNewLabel, "3, 1, left, center");
		
		
		
//		setBounds(100, 100, 450, 300);
//		getContentPane().setLayout(new BorderLayout());
//		contentPanel.setLayout(new FlowLayout());
//		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
//		getContentPane().add(contentPanel, BorderLayout.CENTER);
//		{
//			JPanel buttonPane = new JPanel();
//			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
//			getContentPane().add(buttonPane, BorderLayout.SOUTH);
//			{
//				JButton okButton = new JButton("OK");
//				okButton.setActionCommand("OK");
//				buttonPane.add(okButton);
//				getRootPane().setDefaultButton(okButton);
//			}
//			{
//				JButton cancelButton = new JButton("Cancel");
//				cancelButton.setActionCommand("Cancel");
//				buttonPane.add(cancelButton);
//			}
//		}
	}
	
	private void pickAssistantFile() {
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
            inputFrame.dispose();
            return;
        }

        // Close the application window after file selection
        inputFrame.dispose();
	}
}
