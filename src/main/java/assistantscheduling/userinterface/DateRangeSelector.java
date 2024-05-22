package assistantscheduling.userinterface;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateRangeSelector extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	// Declare dialog components
	JComboBox<String> comboStartMonth;
	JComboBox<Integer> comboStartDay;
	JComboBox<Integer> comboStartYear;
	JComboBox<String> comboEndMonth;
	JComboBox<Integer> comboEndDay;
	JComboBox<Integer> comboEndYear;
	
	
	/**
	 * Create the dialog.
	 */
	public DateRangeSelector(JFrame frame, String title, boolean modal, DataFileCreator dataFileCreator) {
		super(frame, title, modal);
		setSize(450, 200);
        setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[grow][grow][grow]", "[][][][]"));
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		Integer[] days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
		// TODO: Don't hard code this. Get the current year, then a range of 20 years or so.
		Integer[] years = {2024,2025,2026,2027,2028,2029,2030,2031,2032,2033,2034,2035,2036,2037,2038,2039,2040};
		
		JLabel lblStartDate = new JLabel("Select Schedule Start Date");
		lblStartDate.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(lblStartDate, "span 2,wrap");
	
		comboStartMonth = new JComboBox<>(months);
		comboStartMonth.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(comboStartMonth, "growx");
	
		comboStartDay = new JComboBox<>(days);
		comboStartDay.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(comboStartDay, "growx");
	
		comboStartYear = new JComboBox<>(years);
		comboStartYear.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboStartYear.setSelectedIndex(0);
		contentPanel.add(comboStartYear, "growx,wrap");
	
		JLabel lblEndDate = new JLabel("Select Schedule End Date");
		lblEndDate.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(lblEndDate, "span 2,wrap");
	
		comboEndMonth = new JComboBox<>(months);
		comboEndMonth.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(comboEndMonth, "growx");
	
		comboEndDay = new JComboBox<>(days);
		comboEndDay.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(comboEndDay, "growx");
	
		comboEndYear = new JComboBox<>(years);
		comboEndYear.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboEndYear.setSelectedIndex(0);
		contentPanel.add(comboEndYear, "growx");
	
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.setActionCommand("OK");
		buttonPane.add(btnOK);
		getRootPane().setDefaultButton(btnOK);
		btnOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("[MMMM.dd.yyyy][MMMM.d.yyyy]", Locale.ENGLISH);
				LocalDate startDate = LocalDate.parse((String)comboStartMonth.getSelectedItem()+"."
							+((Integer)comboStartDay.getSelectedItem()).toString()+"."+((Integer)comboStartYear.getSelectedItem()).toString(), dtFormatter);
				LocalDate endDate = LocalDate.parse((String)comboEndMonth.getSelectedItem()+"."
						+((Integer)comboEndDay.getSelectedItem()).toString()+"."+((Integer)comboEndYear.getSelectedItem()).toString(), dtFormatter);
				if(allSelected() && dateRangeOkay(startDate, endDate)) {
					LocalDate[] dateRange = {startDate, endDate};
					dataFileCreator.setServiceRange(dateRange);
					dispose();
				} else {
		            JOptionPane.showMessageDialog(frame, "Error with date selection", "Date Range Selection Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setActionCommand("Cancel");
		buttonPane.add(btnCancel);
	}
	
	private boolean allSelected() {
		return (comboStartMonth.getSelectedItem() != null) && (comboStartDay.getSelectedItem() != null) && (comboStartYear.getSelectedItem() != null) 
				&& (comboEndMonth.getSelectedItem() != null) && (comboEndDay.getSelectedItem() != null) && (comboEndYear.getSelectedItem() != null);
	}
	
	private boolean dateRangeOkay(LocalDate startDate, LocalDate endDate) {
		return startDate.isBefore(endDate);
	}

}
