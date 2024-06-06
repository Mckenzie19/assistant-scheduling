package assistantscheduling.ui;

import java.awt.Color;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dataio.DataFileCreator;
import dataio.DateComparator;
import net.miginfocom.swing.MigLayout;
import javax.swing.JList;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

public class CommunionFormPanel extends FormPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CommunionFormPanel.class);
	
	private static DataFileCreator dfc;
	
	// This setup allows for the parent frame to instruct the panel to either save or discard all data, depending on the button selected
	//TODO: Add ability to track which services are communion in the dfc

	
	// Declare dialog components
	private JLabel lblTitle;
	private JList<LocalDate> lstServices;
	private JScrollPane scrollPane;
	private DefaultListModel<LocalDate> dateModel = new DefaultListModel<LocalDate>();;


	/**
	 * Create the panel.
	 */
	public CommunionFormPanel(DataFileCreator dfc) {
		setName("Communion Selection Panel");
		setLayout(new MigLayout("wrap 3", "[][grow][grow]", "[]10[grow]"));
		setBackground(Color.WHITE);
		setOpaque(true);
		setFocusable(true);
		
		CommunionFormPanel.dfc = dfc;
		dataSetters.add(new MethodRunner() {
			@Override
			public void run(Object arg) {
				dfc.setCommunionDates((LocalDate[]) arg);
			}
		});
		
		// Initialize panel components
		lblTitle = new JLabel("Select Communion Sundays");
		dateModel.addElement(LocalDate.now());
		lstServices = new JList<LocalDate>(dateModel);
		scrollPane = new JScrollPane(lstServices);
		
		// Set styles 
		lblTitle.setFont(StyleSettings.TITLE_FONT);
		lstServices.setFont(StyleSettings.CONTENT_FONT);
		
		// Set initial settings and content
		lstServices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		// Add components to panel
		add(lblTitle, "span 2,wrap");
		add(scrollPane, "grow");
	}

	public ArrayList<LocalDate> getListWeekly(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek) {
		LocalDate firstActualDay = startDate.with(TemporalAdjusters.next(dayOfWeek));
		LocalDate lastActualDay = endDate.with(TemporalAdjusters.previous(dayOfWeek));
		ArrayList<LocalDate> dateList = new ArrayList<LocalDate>();
		LocalDate temp = firstActualDay;
		while (temp.isBefore(lastActualDay)) {
			dateList.add(temp);
			temp = temp.plusWeeks(1);
		}
		return dateList;
	}
	
	@Override
	public void checkData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadData() {
		dateModel.removeAllElements(); // Clear out placeholders
		LocalDate[] serviceRange = dfc.getServiceRange();
		ArrayList<LocalDate> dateList = getListWeekly(serviceRange[0], serviceRange[1], DayOfWeek.SUNDAY);
		LocalDate[] additionalDates = dfc.getAdditionalServices();
		// Add additional services and re-sort the list
		if (additionalDates != null) {
			for (LocalDate date : additionalDates) {
				dateList.add(date);
			}
			dateList.sort(new DateComparator());
		}
		// Add all services to the dateModel 
		for (LocalDate date: dateList) {
			dateModel.addElement(date);
		}
	}

}
