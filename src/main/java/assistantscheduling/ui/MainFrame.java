package assistantscheduling.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Taskbar;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.SoftBevelBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.dataio.DataFileCreator;
import net.miginfocom.swing.MigLayout;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(MainFrame.class);
	private static final Toolkit tk = Toolkit.getDefaultToolkit();

	private static final String iconLocation = "images/CLCLogoIcon.gif";
	private static final String bannerLocation = "images/BannerImage.gif";

	private final DataFileCreator dfc = new DataFileCreator();

	// Used to track which panel is currently shown. Set to 0 until panels are initialized.
	private static int panelNum = 0;

	private JPanel contentPane;
	private final GridBagLayout gbl = new GridBagLayout();
	private JPanel imagePanel;
	private GridBagConstraints igbc = new GridBagConstraints();
	private MigLayout imageLayout = new MigLayout("", "[shrink]", "2[]2");
	private JPanel formPanel;
	private GridBagConstraints fgbc = new GridBagConstraints();
	private final CardLayout formLayout = new CardLayout(0, 0);
	private JPanel buttonPanel;
	private GridBagConstraints bgbc = new GridBagConstraints();
	private final CardLayout buttonLayout = new CardLayout(0, 0);
	private JLabel lblAppBanner;

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		LOGGER.info("Initializing MainFrame.. ");
		setIcon(iconLocation);
		initializeLayout();
		// This enables the user to "click off of" any internal components
		setFocusable(true);
		addMouseListener(new MyMouseListener());
		LOGGER.info("Initialization complete, gui ready to use.");
	}

	/**
	 * Set application's taskbar icon
	 *
	 * @param iconLocation Location of the .gif file within the project that contains the application icon
	 */
	private void setIcon(String iconLocation) {
		// Set the application dock icon
		URL imageResource = MainFrame.class.getClassLoader().getResource(iconLocation);
		Image image = tk.getImage(imageResource);
		Taskbar taskbar = Taskbar.getTaskbar();
		try {
		    taskbar.setIconImage(image);
		} catch (final UnsupportedOperationException e) {
			LOGGER.warn("The os does not support: 'taskbar.setIconImage'");
		} catch (final SecurityException e) {
		    LOGGER.warn("There was a security exception for: 'taskbar.setIconImage'");
		}
	}

	/**
	 * Sets the initial layout and content of the application gui.
	 */
	private void initializeLayout() {
		setFont(new Font("Proxima Nova", Font.PLAIN, 14));
		setTitle("Worship Assistant Schedule Maker");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		// Sets preferred size of application
		Dimension d = tk.getScreenSize();
		LOGGER.info("Detected Screen Size: "+d.width+":"+d.height);
		Dimension appD = new Dimension(d.width/3, d.height/2);
		LOGGER.info("Set application size to: "+appD.width+":"+appD.height);
		setPreferredSize(appD);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		contentPane.setLayout(gbl);
		setContentPane(contentPane);

		LOGGER.info("Initializing Image Panel...");
		initializeImagePanel();
		LOGGER.info("Initializing Form Panels...");
		initializeFormPanel();
		LOGGER.info("Initializing Button Panels...");
		initializeButtonPanel();

		// Pack application and set location
		pack();
		setLocationRelativeTo(null);
		setResizable(false);
	}

	/**
	 * Creates the image panel, which will hold the banner for the application. This panel does not change once initialized.
	 */
	private void initializeImagePanel() {
		imagePanel = new JPanel();
		imagePanel.setLayout(imageLayout);
		imagePanel.setBackground(Color.WHITE);
		imagePanel.setOpaque(true);

		URL imageResource = MainFrame.class.getClassLoader().getResource(bannerLocation);
		Image image = tk.getImage(imageResource);
		Dimension prefSize = getPreferredSize();
		Image scaledImage = image.getScaledInstance(prefSize.width/4, prefSize.height, java.awt.Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(scaledImage);
		lblAppBanner = new JLabel(icon);
		imagePanel.add(lblAppBanner, "growx,shrinky");

		igbc.fill = GridBagConstraints.VERTICAL;
		igbc.gridx = 0;
		igbc.gridy = 0;
		igbc.weightx = 0.2;
		igbc.weighty = 0.9;
		contentPane.add(imagePanel, igbc);
	}

	/**
	 * Initializes the parent form panel, children form panels, and loads in the first form panel.
	 */
	private void initializeFormPanel() {
		formPanel = new JPanel();

		formPanel.setLayout(formLayout);
		initializeFormPanels();
		panelNum = 1; // Tracks which panel is currently shown
		formLayout.first(formPanel);

		fgbc.fill = GridBagConstraints.BOTH;
		fgbc.anchor = GridBagConstraints.FIRST_LINE_START;
		fgbc.gridx = 1;
		fgbc.gridy = 0;
		fgbc.weightx = 0.8;
		fgbc.weighty = 0.9;
		fgbc.insets = new Insets(0, 0, 0, 0);
		contentPane.add(formPanel, fgbc);
	}

	/**
	 * Initializes the parent button panel, children button panels, and loads in the first button panel.
	 */
	private void initializeButtonPanel() {
		buttonPanel = new JPanel();
		buttonPanel.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		buttonPanel.setLayout(buttonLayout);
		initializeButtonPanels();
		buttonLayout.first(buttonPanel);

		bgbc.fill = GridBagConstraints.HORIZONTAL;
		bgbc.anchor = GridBagConstraints.LAST_LINE_END;
		bgbc.gridx = 0;
		bgbc.gridy = 1;
		bgbc.gridwidth = 2;
		bgbc.weightx = 0;
		bgbc.weighty = 0.1;
		bgbc.ipady = 5;
		contentPane.add(buttonPanel, bgbc);
	}

	/**
	 * Initializes and adds each of the form panels the application will use into the formPanel card layout
	 */
	private void initializeFormPanels() {
		FormPanel startFormPanel = new StartFormPanel();
		FormPanel dateFormPanel = new DateFormPanel(dfc);
		FormPanel communionFormPanel = new CommunionFormPanel(dfc);
		formPanel.add(startFormPanel);
		formPanel.add(dateFormPanel);
		formPanel.add(communionFormPanel);
	}

	/**
	 * Initializes and adds each of the button panels the application will use into the buttonPanel card layout
	 */
	private void initializeButtonPanels() {
		// Initialize button panels
		StartButtonPanel startButtonPanel = new StartButtonPanel();
		DataCollectionButtonPanel dataCollectionButtonPanel = new DataCollectionButtonPanel();

		// Add action listeners to buttons
		startButtonPanel.startAddActionListener(e -> loadFirstPage());
		dataCollectionButtonPanel.nextAddActionListener(e -> loadNextPage());
		dataCollectionButtonPanel.backAddActionListener(e -> loadPreviousPage());
		dataCollectionButtonPanel.cancelAddActionListener(e -> cancel());

		// Add button panels to main button panel
		buttonPanel.add(startButtonPanel);
		buttonPanel.add(dataCollectionButtonPanel);
	}

	/**
	 * Loads the first data collection page (no need to save current data or load in previous data)
	 */
	private void loadFirstPage() {
		formLayout.next(formPanel);
		buttonLayout.next(buttonPanel);
		panelNum++;
	}
	
	/**
	 * Checks and saves the data from the current panel. If that succeeds, loads the next panel, otherwise internal data checks display warnings.
	 */
	private void loadNextPage() {
		FormPanel currPanel = (FormPanel) formPanel.getComponent(panelNum-1);
		try {
			currPanel.checkData();
		} catch (Exception e) {
			LOGGER.warn(e.getMessage());
			return;
		}
		currPanel.saveData();
		FormPanel nextPanel = (FormPanel) formPanel.getComponent(panelNum);
		nextPanel.loadData();
		formLayout.next(formPanel);
		panelNum++;
	}

	/**
	 * Discards the data input to the current form, then loads the previous panel.
	 */
	private void loadPreviousPage() {
		FormPanel currPanel = (FormPanel) formPanel.getComponent(panelNum-1);
		currPanel.discardData();
		formLayout.previous(formPanel);
		panelNum--;
		if(panelNum == 1) buttonLayout.previous(buttonPanel);
	}

	private void cancel() {
		dfc.discardAllData();
		formLayout.first(formPanel);
		buttonLayout.first(buttonPanel);
		panelNum = 1;
	}

	/**
	 * MouseAdapter class that will allow for users to de-select or remove focus from components within the frame, by instead moving the focus to the panel.
	 */
	class MyMouseListener extends MouseAdapter{
		@Override
		public void mouseClicked(MouseEvent e) {
			// Check if user left clicked
			if (e.getButton() == MouseEvent.BUTTON1) {
				// Check if there are no components were the user clicked, aka if the returned component is the Root Frame
				Component c = getComponentAt(e.getPoint());
				if (c instanceof JRootPane) {
					LOGGER.info("Frame component requesting focus in window...");
					requestFocusInWindow();
				}
			}
		}
	}
}
