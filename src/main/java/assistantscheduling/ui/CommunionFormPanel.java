package assistantscheduling.ui;

import java.awt.Color;
import java.awt.Component;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
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
	//TODO: Add ability to track which services are communion in the dfc


	// Declare dialog components
	private JLabel lblTitle;
	private JList<LocalDate> lstServices;
	private JScrollPane scrollPane;
	private DefaultListModel<LocalDate> dateModel = new DefaultListModel<LocalDate>();


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
		lblTitle = new JLabel("Select Communion Sundays");
		dateModel.addElement(LocalDate.now()); // Temporary data allows for the creation of the list model. This gets cleared upon data loading.
		lstServices = new JList<LocalDate>(dateModel);
		scrollPane = new JScrollPane(lstServices);

		// Set styles
		lblTitle.setFont(StyleSettings.TITLE_FONT);
		lstServices.setFont(StyleSettings.CONTENT_FONT);
		lstServices.setCellRenderer(getRenderer()); // Sets cell borders and fonts
		lstServices.setFixedCellHeight(25);
		lstServices.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		lstServices.setVisibleRowCount(-1);
		scrollPane.getViewport().setView(lstServices);


		// Set initial settings and content
		lstServices.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		lstServices.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {

			        if (lstServices.getSelectedIndex() == -1) {
			        	// Selection cleared
			        } else {
			        	// Cell selected, flip the Communion tag (on/off)

			        }
			    }
			}
		});
		

		// Add components to panel
		add(lblTitle, "span 2,wrap");
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
		LocalDate firstActualDay = startDate.with(TemporalAdjusters.next(dayOfWeek));
		LocalDate lastActualDay = endDate.with(TemporalAdjusters.previous(dayOfWeek));
		ArrayList<LocalDate> dateList = new ArrayList<>();
		LocalDate temp = firstActualDay;
		while (temp.isBefore(lastActualDay)) {
			dateList.add(temp);
			temp = temp.plusWeeks(1);
		}
		return dateList;
	}

	private ListCellRenderer<? super LocalDate> getRenderer() {
        return new DefaultListCellRenderer(){
            private static final long serialVersionUID = 1L;

			@Override
            public Component getListCellRendererComponent(JList<?> list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel listCellRendererComponent = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,cellHasFocus);
                listCellRendererComponent.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1,Color.GRAY));
                listCellRendererComponent.setHorizontalAlignment(CENTER);
                return listCellRendererComponent;
            }
        };
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
