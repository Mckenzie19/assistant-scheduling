package assistantscheduling.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

abstract class FormPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public interface MethodRunner{
		public void run(Object arg);
	}
	
	List<MethodRunner> dataSetters = new ArrayList<>();
	List<Object> data = new ArrayList<>();
	
	abstract public void checkData();
	
	public void saveData() {
		for (int i = 0; i < dataSetters.size(); i++) {
			dataSetters.get(i).run(data.get(i));
		}
	}
	
	public void discardData() {
		for (MethodRunner mr : dataSetters) {
			mr.run(null);
		}
	}
}
