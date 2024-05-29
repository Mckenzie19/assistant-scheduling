package assistantscheduling.userinterface.datagen;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DateRangeSelector extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private static final Logger LOGGER = LoggerFactory.getLogger(DateRangeSelector.class);

	
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
		Integer[] days = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30, 31};
		LocalDate today = LocalDate.now();
		int thisYear = today.getYear();
		int thisMonth = today.getMonthValue();
		Integer[] years = IntStream.rangeClosed(thisYear, thisYear+5).boxed().toArray(Integer[]::new);
		
		// Add gui components
		JLabel lblStartDate = new JLabel("Select Schedule Start Date");
		lblStartDate.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(lblStartDate, "span 2,wrap");
	
		comboStartMonth = new JComboBox<>(months);
		comboStartMonth.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboStartMonth.setSelectedItem(months[thisMonth-1]);
		contentPanel.add(comboStartMonth, "growx");
	
		comboStartDay = new JComboBox<>(days);
		comboStartDay.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboStartDay.setSelectedItem(today.getDayOfMonth());
		contentPanel.add(comboStartDay, "growx");
	
		comboStartYear = new JComboBox<>(years);
		comboStartYear.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboStartYear.setSelectedItem(thisYear);
		contentPanel.add(comboStartYear, "growx,wrap");
	
		JLabel lblEndDate = new JLabel("Select Schedule End Date");
		lblEndDate.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		contentPanel.add(lblEndDate, "span 2,wrap");
	
		comboEndMonth = new JComboBox<>(months);
		comboEndMonth.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboEndMonth.setSelectedItem(months[thisMonth-1]);
		contentPanel.add(comboEndMonth, "growx");
	
		comboEndDay = new JComboBox<>(days);
		comboEndDay.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboEndDay.setSelectedItem(today.getDayOfMonth());
		contentPanel.add(comboEndDay, "growx");
	
		comboEndYear = new JComboBox<>(years);
		comboEndYear.setFont(new Font("Proxima Nova", Font.PLAIN, 13));
		comboEndYear.setSelectedItem(thisYear);
		contentPanel.add(comboEndYear, "growx");
	
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnCancel.setActionCommand("Cancel");
		buttonPane.add(btnCancel);
		
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
				if(allSelected() && dateRangeOkay(startDate, endDate) && dateNotInPast(startDate)) {
					LocalDate[] dateRange = {startDate, endDate};
					dataFileCreator.setServiceRange(dateRange);
					dispose();
				} else if (!dateRangeOkay(startDate, endDate)) {
		            JOptionPane.showMessageDialog(frame, "End date of schedule must occur after the start date.", "Date Range Selection Error", JOptionPane.ERROR_MESSAGE);
				} else if (!dateNotInPast(startDate)){
		            JOptionPane.showMessageDialog(frame, "Start date must not be in the past.", "Date Range Selection Error", JOptionPane.ERROR_MESSAGE);
				} else {
		            JOptionPane.showMessageDialog(frame, "Please ensure all fields have a selection.", "Date Range Selection Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private boolean allSelected() {
		return (comboStartMonth.getSelectedItem() != null) && (comboStartDay.getSelectedItem() != null) && (comboStartYear.getSelectedItem() != null) 
				&& (comboEndMonth.getSelectedItem() != null) && (comboEndDay.getSelectedItem() != null) && (comboEndYear.getSelectedItem() != null);
	}
	
	private boolean dateRangeOkay(LocalDate startDate, LocalDate endDate) {
		return startDate.isBefore(endDate);
	}
	
	private boolean dateNotInPast(LocalDate date) {
		return date.isAfter(LocalDate.now()) || date.isEqual(LocalDate.now());
	}

}
