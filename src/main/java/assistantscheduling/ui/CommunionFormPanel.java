package assistantscheduling.ui;

import java.awt.Color;
import java.awt.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.dataio.DataFileCreator;
import assistantscheduling.dataio.DateComparator;
import net.miginfocom.swing.MigLayout;

public class CommunionFormPanel extends FormPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(CommunionFormPanel.class);

	private static DataFileCreator dfc;

	// This setup allows for the parent frame to instruct the panel to either save or discard all data, depending on the button selected
	private static LocalDate[] communionServices;


	// Declare dialog components
	private JLabel lblTitle;
	private JTextArea txtInfo;
	private JList<LocalDate> lstServices;
	private JScrollPane scrollPane;
	private DefaultListModel<LocalDate> dateModel = new DefaultListModel<LocalDate>();
	private HashMap<LocalDate, Boolean> internalServiceList = new HashMap<LocalDate, Boolean>();


	/**
	 * Create the panel.
	 */
	public CommunionFormPanel(DataFileCreator dfc) {
		setName("Communion Selection Panel");
		setLayout(new MigLayout("wrap 2", "[grow][]", "[]10[grow]"));
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
		lblTitle = new JLabel("Select Communion Services");
		txtInfo = new JTextArea("Please select which services will include communion. The first two sundays of each month have already been selected for you.");
		lstServices = new JList<LocalDate>(dateModel);
		scrollPane = new JScrollPane(lstServices);
		
		// Set initial settings and content
		// Temporary data allows for the creation of the list model. This gets cleared upon data loading.
		txtInfo.setLineWrap(true);
		txtInfo.setWrapStyleWord(true);
		dateModel.addElement(LocalDate.now()); 
		internalServiceList.put(LocalDate.now(), false);
		lstServices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstServices.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) { // Prevents duplicate fires of the event
			        if (lstServices.getSelectedIndex() != -1) {
			        	LocalDate selectedDate = dateModel.getElementAt(lstServices.getSelectedIndex());
			        	internalServiceList.put(selectedDate, !internalServiceList.get(selectedDate));
			        	lstServices.clearSelection(); // Ensures user can select the same cell twice in a row
			        }
			    }
			}
		});

		// Set styles
		lblTitle.setFont(StyleSettings.TITLE_FONT);
		lstServices.setFont(StyleSettings.CONTENT_FONT);
		lstServices.setCellRenderer(getRenderer()); // Sets cell borders and fonts
		lstServices.setFixedCellHeight(25);
		lstServices.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		lstServices.setVisibleRowCount(-1);
		scrollPane.getViewport().setView(lstServices);

		// Add components to panel
		add(lblTitle, "span 2,wrap");
		add(txtInfo, "span 2,wrap,growx");
		add(scrollPane, "grow");
	}


	/**
	 * Given a start date, end date, and day of the week, returns a list of weekly dates occurring on the given day of week.
	 *
	 * @param startDate - Inclusive start date of the range to return
	 * @param endDate - Inclusive end date of the range to return
	 * @param dayOfWeek - DayOfWeek value that indicates what day of the week to select
	 */
	public ArrayList<LocalDate> getListWeekly(LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek) {
		LocalDate firstActualDay = (startDate.getDayOfWeek() == dayOfWeek) ? startDate : startDate.with(TemporalAdjusters.next(dayOfWeek));
		LocalDate lastActualDay = (endDate.getDayOfWeek() == dayOfWeek) ? endDate : endDate.with(TemporalAdjusters.previous(dayOfWeek));
		ArrayList<LocalDate> dateList = new ArrayList<>();
		LocalDate temp = firstActualDay;
		while (temp.isBefore(lastActualDay) || temp.isEqual(lastActualDay)) {
			dateList.add(temp);
			temp = temp.plusWeeks(1);
		}
		return dateList;
	}

	private ListCellRenderer<? super LocalDate> getRenderer() {
        return new DefaultListCellRenderer(){
            private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,Color.GRAY));
                listCellRendererComponent.setHorizontalAlignment(CENTER);
                if(internalServiceList.get(value)) {
                	listCellRendererComponent.setForeground(Color.WHITE);
                	listCellRendererComponent.setBackground(new Color(63, 131, 81));
                } else {
                	listCellRendererComponent.setForeground(Color.BLACK);
                	listCellRendererComponent.setBackground(Color.WHITE);
                }
                
                return listCellRendererComponent;
            }
        };
    }

	@Override
	/**
	 * Add all selected communion dates to the data tracker structure
	 */
	public void checkData() throws Exception {
		int numServices = Collections.frequency(internalServiceList.values(), true);
		communionServices = new LocalDate[numServices];
		int i = 0;
		for (LocalDate date : internalServiceList.keySet()) {
			if (internalServiceList.get(date)) {
				communionServices[i] = date;
				i++; // Only increase array index when we add something
			}
		}
		data.add(communionServices);
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
			// Check if the date is one of the first two sundays in the month, in which case set as true
			LocalDate firstSunday = date.with(TemporalAdjusters.firstInMonth(DayOfWeek.SUNDAY));
			LocalDate secondSunday = firstSunday.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
			boolean isCommunionDate = (date.isEqual(firstSunday) || date.isEqual(secondSunday)) ? true : false;
			internalServiceList.put(date, isCommunionDate);
		}
		
		// Makes sure each cell has the maximum space it can (assumes 4 columns)
		int listWidth = lstServices.getBounds().width;
		lstServices.setFixedCellWidth(listWidth/4);

		// Dynamically decides how many columns can fix maximum -- Kept in case the hard coded 4 columns doesn't work out
		// Fix the cell width to better fill the space
//		int listWidth = lstServices.getVisibleRect().width;
//		int currCellWidth = lstServices.getCellBounds(0, 0).width;
//		int numCols = listWidth / currCellWidth;
//		int padding = (listWidth % currCellWidth) / numCols; // How much "padding" to add to the width of each cell to evenly distribute the white space
//		lstServices.setFixedCellWidth(currCellWidth+padding);
	}

}
