package assistantscheduling.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;

public class StartButtonPanel extends JPanel{

	private static final long serialVersionUID = 1L;
	private JButton btnHelp;
	private JButton btnBegin;

	/**
	 * Create the panel.
	 */
	public StartButtonPanel() {
		setLayout(new MigLayout("wrap 2", "[]push[]", "[]"));
		setFocusable(true);
		
		btnHelp = new JButton("Help");
		btnBegin = new JButton("Begin");
		
		btnHelp.setFont(StyleSettings.SUBTITLE_FONT);
		btnBegin.setFont(StyleSettings.SUBTITLE_FONT);
		
		btnHelp.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				HelpDialog helpDialog = new HelpDialog();
				helpDialog.setVisible(true);
			}
		});
		
		add(btnHelp);
		add(btnBegin);
	}
	
	public void startAddActionListener(ActionListener a) {
		btnBegin.addActionListener(a);
	}
	
	private class HelpDialog extends JDialog {

		private static final long serialVersionUID = 1L;
		private final JPanel contentPanel = new JPanel();
		private JTable exampleTable;

		/**
		 * Create the dialog.
		 */
		public HelpDialog() {
			setModal(true);
			setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			setFont(StyleSettings.SUBTITLE_FONT);
			setTitle("Help");
			
			// Sets preferred size of popup
			Toolkit tk = Toolkit.getDefaultToolkit();
			Dimension d = tk.getScreenSize();
			Dimension appD = new Dimension(d.width/2, d.height/3);
			setPreferredSize(appD);
			getContentPane().setLayout(new BorderLayout(0, 0));
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			
			getContentPane().add(contentPanel);
			contentPanel.setLayout(new BorderLayout(0, 0));
			{
				String[] columns = {"Name", "Unavailable Months", "Frequency", "Preferred Positions"};
				Object[][] data = {
						{"Jon Doe", "", "Once per month", "Communion Server;Greeter;Refreshments"},
						{"Jane Eyre", "January;April;November", "As often as you need me", "Lector;Usher"},
						{"Derek Enderson", "March", "Twice a year", "Lector;Usher;Greeter;Refreshments"}
				};
				exampleTable = new JTable(data, columns);
				exampleTable.setFont(StyleSettings.CONTENT_FONT);
				JScrollPane scrollPane = new JScrollPane(exampleTable);
				scrollPane.setPreferredSize(new Dimension(75,100));
				// Resize columns
				TableColumn column = null;
				for (int i = 0; i < 4; i++) {
				    column = exampleTable.getColumnModel().getColumn(i);
				    if (i == 3) {
				        column.setPreferredWidth(70); //Fourth column is bigger
				    } else {
				        column.setPreferredWidth(10);
				    }
				}
				contentPanel.add(scrollPane, BorderLayout.CENTER);
			}
			{
				JTextArea helpMessage = new JTextArea(10, 200);
				helpMessage.setEditable(false);
				helpMessage.setLineWrap(true);
				helpMessage.setWrapStyleWord(true);
				helpMessage.setFont(StyleSettings.CONTENT_FONT);
				helpMessage.setText("The assistant data must be stored in an Excel file (.xlsx). There should be 4 columns, with the names \"Name\", \"Unavailable Months\", \"Frequency\", and \"Preferred Positions\".\n\nThe Name column should contain the first and last name of the assistant, with a single space seperating names.\nThe Unavailable Months column should contain a list of months. Each month should be seperated by a single semi-colon (;), with no spaces.\nThe Frequency column should include the exact option that the assistant chose on the sign-up form. Options include: \"Once per month\", \"As often as you need me\", \"Twice a year\", \"Once a quarter\".\nThe Preferred Positions column should contain a list of position names. Each position should be seperated from the next one by a single semicolon (;) and no spaces. \n\nPlease ensure there are no spelling errors, as this can cause the propgram to reject that row. ");
				contentPanel.add(helpMessage, BorderLayout.NORTH);
			}
			
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.PAGE_END);
				{
					JButton okButton = new JButton("OK");
					okButton.setFont(StyleSettings.SUBTITLE_FONT);
					okButton.setActionCommand("OK");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							dispose();
						}
					});
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
			}
			
			// Pack popup and set location
			pack();
			setLocationRelativeTo(null);
			setResizable(false);
		}

	}

}
