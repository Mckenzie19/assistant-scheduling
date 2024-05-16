package assistantscheduling.userinterface;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class DataSetupWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public DataSetupWindow() {
		setTitle("Set Assistant and Service Data");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 185);
        setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAssistantData = new JLabel("Assistant Data File");
		lblAssistantData.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		lblAssistantData.setBounds(22, 26, 120, 16);
		contentPane.add(lblAssistantData);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(12, 45, 575, 12);
		contentPane.add(separator);
		
		JTextPane txtAssitantDataFile = new JTextPane();
		
		txtAssitantDataFile.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		txtAssitantDataFile.setBounds(154, 26, 311, 16);
		contentPane.add(txtAssitantDataFile);
		
		JButton btnSelectFile = new JButton("Select File");
		btnSelectFile.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnSelectFile.setBounds(475, 20, 117, 29);
		contentPane.add(btnSelectFile);
		
		JLabel lblServiceDateRange = new JLabel("Service Date Range");
		lblServiceDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		lblServiceDateRange.setBounds(22, 58, 130, 16);
		contentPane.add(lblServiceDateRange);
		
		JTextPane txtAssitantDataFile_1 = new JTextPane();
		txtAssitantDataFile_1.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		txtAssitantDataFile_1.setBounds(154, 58, 293, 16);
		contentPane.add(txtAssitantDataFile_1);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(12, 78, 575, 12);
		contentPane.add(separator_1);
		
		JButton btnSelectDateRange = new JButton("Select Date Range");
		btnSelectDateRange.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		btnSelectDateRange.setBounds(462, 53, 130, 29);
		contentPane.add(btnSelectDateRange);
		
		JCheckBox chckbxSelectAdditionalServices = new JCheckBox("Select Additional Service Dates");
		chckbxSelectAdditionalServices.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		chckbxSelectAdditionalServices.setBounds(12, 86, 201, 23);
		contentPane.add(chckbxSelectAdditionalServices);
		
		JTextPane txtAssitantDataFile_1_1 = new JTextPane();
		txtAssitantDataFile_1_1.setFont(new Font("Proxima Nova", Font.PLAIN, 12));
		txtAssitantDataFile_1_1.setBounds(217, 90, 370, 16);
		contentPane.add(txtAssitantDataFile_1_1);
		
		JButton btnPg1Next = new JButton("Next >>");
		btnPg1Next.setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		btnPg1Next.setBounds(475, 122, 117, 29);
		contentPane.add(btnPg1Next);
	}
}
