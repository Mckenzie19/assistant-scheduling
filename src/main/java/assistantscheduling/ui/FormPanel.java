package assistantscheduling.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class FormPanel extends JPanel {

	private static final long serialVersionUID = 2292943616651630830L;
	private final Logger LOGGER = LoggerFactory.getLogger(FormPanel.class);

	public interface MethodRunner{
		public void run(Object arg);
	}

	List<MethodRunner> dataSetters = new ArrayList<>();
	List<Object> data = new ArrayList<>();

	abstract public void checkData() throws Exception;
	abstract public void loadData();

	public void saveData() {
		LOGGER.info("Saving form data...");
		for (int i = 0; i < dataSetters.size(); i++) {
			dataSetters.get(i).run(data.get(i));
		}
		LOGGER.info("Form data saved.");
	}

	public void discardData() {
		LOGGER.info("Discarding form data...");
		for (MethodRunner mr : dataSetters) {
			mr.run(null);
		}
		LOGGER.info("Data Object Contents: ");
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i) != null) LOGGER.info(Arrays.toString((LocalDate[]) data.get(i)));
		}
		LOGGER.info("Clearing information for data variable with Hashcode: " + System.identityHashCode(data));		
		data.clear();
		LOGGER.info("Form data discarded.");
	}
	
}
