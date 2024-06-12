package assistantscheduling.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.dataio.DataFileCreator;
import assistantscheduling.dataio.DataIO;
import net.miginfocom.swing.MigLayout;

public class AssistantDataFormPanel extends FormPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(AssistantDataFormPanel.class);

	private static DataIO dataIO;
	DefaultTableModel dtm = new DefaultTableModel();
	private static DataFileCreator dfc;
	private static String[] columnNames = {"Name", "Unavailable Months", "Frequency", "Preferred Positions"};
	private static List<List<String>> assistantData = new ArrayList<List<String>>();

	// This setup allows for the parent frame to instruct the panel to either save or discard all data, depending on the button selected
	
	// Declare dialog components
	private JLabel lblTitle;
	private JTextArea txtInfo;
	private JLabel lblAssistantFile;
	private JLabel lblAssistDataFileName;
	private JButton btnSelectFile;
	private JTable tblAssistants;
	private JScrollPane scrollPane;
	private AssistantTableModel tableModel = new AssistantTableModel(assistantData);

	/**
	 * Create the panel.
	 */
	public AssistantDataFormPanel(DataFileCreator dfc, DataIO dataIO) {
		setName("Assistant Data Import Panel");
		setLayout(new MigLayout("wrap 3", "[grow][grow][]", "[]10[][grow]"));
		setBackground(Color.WHITE);
		setOpaque(true);
		setFocusable(true);

		AssistantDataFormPanel.dfc = dfc;
		AssistantDataFormPanel.dataIO = dataIO;
		dataSetters.add(new MethodRunner() {
			@Override
			public void run(Object arg) {
				dfc.setAssistantDataFile((File) arg);
			}
		});

		// Initialize panel components
		lblTitle = new JLabel("Import Assistant Information");
		txtInfo = new JTextArea("Please select the Excel file which contains the worship assistant information. After importing, confirm that the information is correct before continuing.");
		lblAssistantFile = new JLabel("Selected File: ");
		lblAssistDataFileName = new JLabel(" ");
		btnSelectFile = new JButton("Select File");
		tblAssistants = new JTable(tableModel);
		scrollPane = new JScrollPane(tblAssistants);
		
		// Set initial settings and content
		btnSelectFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File dataFile = pickAssistantFile();
					loadAssistantData(dataFile);
				} catch (Exception e1) {
					LOGGER.warn(e1.getMessage() + "\n" + Arrays.toString(e1.getStackTrace()));
				}
			}
		});
		
		// Set styles
		lblTitle.setFont(StyleSettings.TITLE_FONT);
		txtInfo.setLineWrap(true);
		txtInfo.setWrapStyleWord(true);
		txtInfo.setEditable(false);
		tblAssistants.setFont(StyleSettings.CONTENT_FONT);
		tableModel.addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
				ColumnsAutoSizer.sizeColumnsToFit(tblAssistants);
			}
		});
		scrollPane.getViewport().setView(tblAssistants);
		
		// Add components to the panel
		add(lblTitle, "span 3,wrap");
		add(txtInfo, "span 3,wrap,growx");
		add(lblAssistantFile, "shrinkx");
		add(lblAssistDataFileName, "growx");
		add(btnSelectFile, "shrinkx");
		add(scrollPane, "span 3,grow");

	}
	
	public File pickAssistantFile() throws Exception{
		File dataFile = dataIO.selectFile(this, false);
        if (dataFile == null) {
        	throw new Exception("Unable to select file.");
        }
        lblAssistDataFileName.setText(dataFile.getName());
        return dataFile;
	}
	
	private void loadAssistantData(File dataFile) throws IOException,NullPointerException{
		List<List<String>> newData = dataIO.getStringDataFromFile(dataFile);
		for (int i = 0; i<newData.size(); i++) {
			tableModel.addRow(newData.get(i));
		}
		//tableModel.fireTableDataChanged();
	}

	@Override
	public void checkData() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub
		
	}
	
	class AssistantTableModel extends AbstractTableModel {
	    private static final long serialVersionUID = 1L;
		private List<List<String>> assistantList;
		
	    public AssistantTableModel(List<List<String>> assistantList)
	    {
	        this.assistantList = assistantList;
	    }
	     
	    @Override
	    public String getColumnName(int column)
	    {
	        return columnNames[column];
	    }
	 
	    @Override
	    public Class<?> getColumnClass(int columnIndex)
	    {
	        return String.class;
	    }
	 
	    @Override
	    public int getColumnCount()
	    {
	        return columnNames.length;
	    }
	 
	    @Override
	    public int getRowCount()
	    {
	        return assistantList.size();
	    }
	 
	    @Override
	    public Object getValueAt(int rowIndex, int columnIndex)
	    {
	        List<String> row = assistantList.get(rowIndex);
	        return row.get(columnIndex);
	    }
	 
	    @Override
	    public boolean isCellEditable(int rowIndex, int columnIndex)
	    {
	        return true;
	    }
	 
	    @Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
	    {
	    	List<String> row = assistantList.get(rowIndex);
	    	row.set(columnIndex, (String) aValue);
	    	assistantList.set(rowIndex, row);
	    }
	    
	    public void addRow(List<String> rowData) {
	    	addRow(convertToVector(rowData));
	    }
	    
	    public void addRow(Vector<String> rowData) {
	    	insertRow(getRowCount(), rowData);
	    }
	    
	    public void insertRow(int row, Vector<String> rowData) {
	    	if (row >= assistantList.size()) {
	    		assistantList.add(rowData);
	    	} else {
	    		assistantList.set(row, rowData);
	    	}
	    	//justifyRows(row, row+1);
	    	fireTableRowsInserted(row, row);
	    }
	    
	    protected static Vector<String> convertToVector(List<String> rowData){
	    	if (rowData == null) return null;
	    	Vector<String> v = new Vector<String>(rowData.size());
	    	for (String s : rowData) {
	    		v.addElement(s);
	    	}
	    	return v;
	    }
	 
	}

}
