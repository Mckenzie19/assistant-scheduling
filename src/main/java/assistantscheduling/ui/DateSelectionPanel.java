package assistantscheduling.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.stream.IntStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import dataio.DataFileCreator;
import net.miginfocom.swing.MigLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateSelectionPanel extends FormPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(DateSelectionPanel.class);
	
	// This setup allows for the parent frame to instruct the panel to either save or discard all data, depending on the button selected
	private static LocalDate[] serviceDateRange;
	private static LocalDate[] additionalServices;

	
	// Declare dialog components
	private JLabel lblTitle;
	private JComboBox<String> comboStartMonth;
	private JComboBox<Integer> comboStartDay;
	private JComboBox<Integer> comboStartYear;
	private JComboBox<String> comboEndMonth;
	private JComboBox<Integer> comboEndDay;
	private JComboBox<Integer> comboEndYear;
	private JLabel lblStartDate;
	private JLabel lblEndDate;
	private JCheckBox chckbxSelectAdditionalServices;
	private JTextField txtAdditionalServices;
	private TextPrompt tpAddServices;
	private JLabel lblAddServiceWarning;
	private JLabel lblIllogicalDates;
	private JLabel lblMissingData;

	/**
	 * Create the panel.
	 */
	public DateSelectionPanel(DataFileCreator dfc) {
		setName("Date Selection Panel");
		setLayout(new MigLayout("wrap 3", "5[][grow][grow]5", "10[]10[][][][][][][][][]5"));
		setBackground(Color.WHITE);
		setOpaque(true);
		setFocusable(true);
		
		dataSetters.add(new MethodRunner() {
			@Override
			public void run(Object arg) {
				dfc.setServiceRange((LocalDate[]) arg);
			}
		});
		dataSetters.add(new MethodRunner() {
			@Override
			public void run(Object arg) {
				dfc.setAdditionalServices((LocalDate[]) arg);
			}
		});
		
		// Setup date selection data
		String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		Integer[] days = IntStream.rangeClosed(1, 31).boxed().toArray(Integer[]::new);
		LocalDate today = LocalDate.now();
		int thisYear = today.getYear();
		int thisMonth = today.getMonthValue();
		Integer[] years = IntStream.rangeClosed(thisYear, thisYear+5).boxed().toArray(Integer[]::new);
		
		// Initialize panel components
		lblTitle = new JLabel("Set Schedule Date Range");
		lblStartDate = new JLabel("Select Schedule Start Date");
		comboStartMonth = new JComboBox<>(months);
		comboStartDay = new JComboBox<>(days);
		comboStartYear = new JComboBox<>(years);
		lblEndDate = new JLabel("Select Schedule End Date");
		comboEndMonth = new JComboBox<>(months);
		comboEndDay = new JComboBox<>(days);
		comboEndYear = new JComboBox<>(years);
		chckbxSelectAdditionalServices = new JCheckBox("Select Additional Service Dates");
		txtAdditionalServices = new JTextField("");
		// Adds a text prompt that overlays the above text field. Will only show up when the window focus is not on the text field.
		tpAddServices = new TextPrompt("MM/DD/YYYY, MM/DD/YYYY, etc.", txtAdditionalServices, TextPrompt.Show.FOCUS_LOST);
		lblAddServiceWarning = new JLabel("Please ensure dates follow requested formatting");
		lblIllogicalDates = new JLabel("Please ensure dates are not in the past.");
		lblMissingData = new JLabel("Please ensure all requested data is present.");
		
		// Set initial values and add action listeners
		comboStartMonth.setSelectedItem(months[thisMonth-1]);
		comboStartDay.setSelectedItem(today.getDayOfMonth());
		comboStartYear.setSelectedItem(thisYear);
		comboEndMonth.setSelectedItem(months[thisMonth-1]);
		comboEndDay.setSelectedItem(today.getDayOfMonth());
		comboEndYear.setSelectedItem(thisYear);
		chckbxSelectAdditionalServices.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// Add Dates
					chckbxSelectAdditionalServices.setForeground(Color.BLACK);
					tpAddServices.setForeground(Color.GRAY);
					txtAdditionalServices.setEditable(true);
					txtAdditionalServices.setBorder(new LineBorder(Color.BLACK));
				} else {
					// Clear selection
					chckbxSelectAdditionalServices.setForeground(Color.GRAY);
					txtAdditionalServices.setText("");
					txtAdditionalServices.setBorder(new LineBorder(Color.GRAY));
					tpAddServices.setForeground(Color.LIGHT_GRAY);
					txtAdditionalServices.setEditable(false);
					lblAddServiceWarning.setVisible(false);
					dfc.setAdditionalServices(null);
				}
			}
		});
		txtAdditionalServices.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				txtAdditionalServices.setBorder(new LineBorder(Color.BLACK));
				lblAddServiceWarning.setVisible(false);
			}

			@Override
			public void focusLost(FocusEvent e) {
				checkAdditionalDateFormatting();
			}
		});
		txtAdditionalServices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkAdditionalDateFormatting();
				// Loose the focus
				LOGGER.info("Content Panel requesting focus in window...");
				requestFocusInWindow();
			}
		});
		
		// Set styles 
		lblTitle.setFont(StyleSettings.TITLE_FONT);
		lblStartDate.setFont(StyleSettings.SUBTITLE_FONT);
		comboStartMonth.setFont(StyleSettings.CONTENT_FONT);
		comboStartDay.setFont(StyleSettings.CONTENT_FONT);
		comboStartYear.setFont(StyleSettings.CONTENT_FONT);
		lblEndDate.setFont(StyleSettings.SUBTITLE_FONT);
		comboEndMonth.setFont(StyleSettings.CONTENT_FONT);
		comboEndDay.setFont(StyleSettings.CONTENT_FONT);
		comboEndYear.setFont(StyleSettings.CONTENT_FONT);
		chckbxSelectAdditionalServices.setForeground(Color.GRAY);
		chckbxSelectAdditionalServices.setFont(StyleSettings.CONTENT_FONT);
		txtAdditionalServices.setBackground(Color.WHITE);
		txtAdditionalServices.setFont(StyleSettings.CONTENT_FONT);
		txtAdditionalServices.setOpaque(true);
		txtAdditionalServices.setForeground(Color.BLACK);
		txtAdditionalServices.setEditable(false);
		txtAdditionalServices.setBorder(new LineBorder(Color.GRAY));
		tpAddServices.setForeground(Color.LIGHT_GRAY);
		lblAddServiceWarning.setFont(StyleSettings.WARNING_FONT);
		lblAddServiceWarning.setForeground(Color.RED);
		lblAddServiceWarning.setVisible(false);
		lblIllogicalDates.setFont(StyleSettings.WARNING_FONT);
		lblIllogicalDates.setForeground(Color.RED);
		lblIllogicalDates.setVisible(false);
		lblMissingData.setFont(StyleSettings.WARNING_FONT);
		lblMissingData.setForeground(Color.RED);
		lblMissingData.setVisible(false);
		
		// Add components to panel
		add(lblTitle, "span 2,wrap");
		add(lblStartDate, "span 2,wrap");
		add(comboStartMonth, "growx");
		add(comboStartDay, "growx");
		add(comboStartYear, "growx");
		add(lblEndDate, "span 2,wrap");
		add(comboEndMonth, "growx");
		add(comboEndDay, "growx");
		add(comboEndYear, "growx");
		add(chckbxSelectAdditionalServices, "shrinkx,wrap");
		add(txtAdditionalServices, "span 3,growx");
		add(lblAddServiceWarning, "span 3,shrinkx,wrap");
		add(lblIllogicalDates, "span 3,shrinkx,wrap");
		add(lblMissingData, "span 3,shrinkx,wrap");
	}
	
	public boolean checkAdditionalDateFormatting() {
		String fieldText = txtAdditionalServices.getText();
		if (fieldText.length() == 0) return false;
		try {
			String[] splitText = fieldText.split(", ", 0);
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("[MM/dd/yyyy][MM/d/yyyy][M/dd/yyyy][M/d/yyyy]", Locale.ENGLISH);
			additionalServices = new LocalDate[splitText.length];
			for (int i = 0; i < splitText.length; i++) {
				additionalServices[i] = LocalDate.parse(splitText[i], dtf);
				// If date is before today's date, then it is not a valid input
				if (additionalServices[i].isBefore(LocalDate.now())) {
					LOGGER.warn("Attempted to add past date.");
					lblAddServiceWarning.setText("All dates entered must be future dates");
					txtAdditionalServices.setBorder(new LineBorder(Color.RED));
					lblAddServiceWarning.setVisible(true);
					return false;
				}
			}
			txtAdditionalServices.setBorder(new LineBorder(Color.BLACK));
			lblAddServiceWarning.setVisible(false);
		} catch (DateTimeParseException de) {
			LOGGER.warn("Improper date formatting entered: \n" + de.getMessage());
			lblAddServiceWarning.setText("Please ensure dates follow requested formatting");
			txtAdditionalServices.setBorder(new LineBorder(Color.RED));
			lblAddServiceWarning.setVisible(true);
			return false;
		}
		return true;
	}

	@Override
	public void checkData() throws Exception {
		// Checks if all fields were selected or entered
		if (allDataEntered()) {
			LOGGER.info("Confirmed all data entered.");
			lblMissingData.setVisible(false);
			DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("[MMMM.dd.yyyy][MMMM.d.yyyy]", Locale.ENGLISH);
			LocalDate startDate = LocalDate.parse((String)comboStartMonth.getSelectedItem()+"."
						+((Integer)comboStartDay.getSelectedItem()).toString()+"."+((Integer)comboStartYear.getSelectedItem()).toString(), dtFormatter);
			LocalDate endDate = LocalDate.parse((String)comboEndMonth.getSelectedItem()+"."
					+((Integer)comboEndDay.getSelectedItem()).toString()+"."+((Integer)comboEndYear.getSelectedItem()).toString(), dtFormatter);
			// Checks of the main date range makes logical sense (is not in the past and start comes before end)
			if (dateRangeLogical(startDate, endDate)) {
				LOGGER.info("Dates confirmed logical.");
				lblIllogicalDates.setVisible(false);
				serviceDateRange = new LocalDate[2];
				serviceDateRange[0] = startDate;
				serviceDateRange[1] = endDate;	
				// Add dates to FormPanel data list to be saved by parent frame
				data.add(serviceDateRange);
				data.add(additionalServices);
			} else {
				lblIllogicalDates.setVisible(true);
				throw new Exception("Date range illogical.");
			}
		} else {
			lblMissingData.setVisible(true);
			throw new Exception("Missing date data.");
		}
	}
	
	private boolean allDataEntered() {
		LOGGER.info("Checking if all data entered...");
		boolean mainRangeSelected = (comboStartMonth.getSelectedItem() != null) && (comboStartDay.getSelectedItem() != null) && (comboStartYear.getSelectedItem() != null) 
				&& (comboEndMonth.getSelectedItem() != null) && (comboEndDay.getSelectedItem() != null) && (comboEndYear.getSelectedItem() != null);
		if (chckbxSelectAdditionalServices.isSelected()) {
			return mainRangeSelected && checkAdditionalDateFormatting();
		} else {
			return mainRangeSelected;
		}
	}
	
	private boolean dateRangeLogical(LocalDate startDate, LocalDate endDate) {
		LOGGER.info("Checking if date range is logical...");
		return startDate.isBefore(endDate) && (startDate.isAfter(LocalDate.now()) || startDate.isEqual(LocalDate.now()));
	}
	
}
