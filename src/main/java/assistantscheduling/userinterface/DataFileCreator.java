package assistantscheduling.userinterface;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class DataFileCreator {
	
	private File assistantDataFile;
	private LocalDate[] serviceRange;
	private ArrayList<LocalDate> additionalServices;
	private File outputFile;
	
	public DataFileCreator() {}

	public File getAssistantDataFile() {
		return assistantDataFile;
	}

	public void setAssistantDataFile(File assistantDataFile) {
		this.assistantDataFile = assistantDataFile;
	}

	public LocalDate[] getServiceRange() {
		return serviceRange;
	}

	public void setServiceRange(LocalDate[] serviceRange) {
		this.serviceRange = serviceRange;
	}

	public ArrayList<LocalDate> getAdditionalServices() {
		return additionalServices;
	}

	public void setAdditionalServices(ArrayList<LocalDate> additionalServices) {
		this.additionalServices = additionalServices;
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}
	
}
