package assistantscheduling.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class FormPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(FormPanel.class);

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
		LOGGER.info("Form data discarded.");
	}
}
