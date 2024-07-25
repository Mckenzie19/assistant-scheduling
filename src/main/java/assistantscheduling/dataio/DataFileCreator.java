package assistantscheduling.dataio;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFileCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataFileCreator.class);

	private List<List<String>> assistantData;
	private LocalDate[] serviceRange;
	private LocalDate[] additionalServices;
	private LocalDate[] communionDates;
	private File outputFile;

	public DataFileCreator() {}

	public List<List<String>> getAssistantData() {
		return assistantData;
	}

	public void setAssistantData(List<List<String>> assistantData) {
		this.assistantData = assistantData;
		LOGGER.info("Set assistant data. Num Rows: " + assistantData.size());
	}

	public LocalDate[] getServiceRange() {
		return serviceRange;
	}

	public void setServiceRange(LocalDate[] serviceRange) {
		this.serviceRange = serviceRange;
		LOGGER.info("Set service date range: " + Arrays.toString(serviceRange));
	}

	public LocalDate[] getAdditionalServices() {
		return additionalServices;
	}

	public void setAdditionalServices(LocalDate[] additionalServices) {
		this.additionalServices = additionalServices;
		LOGGER.info("Set additional service dates: " + Arrays.toString(additionalServices));
	}

	public LocalDate[] getCommunionDates() {
		return communionDates;
	}

	public void setCommunionDates(LocalDate[] communionDates) {
		this.communionDates = communionDates;
		LOGGER.info("Set communion service dates: " + Arrays.toString(communionDates));
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
		LOGGER.info("Set data output file: " + outputFile.getName());
	}

	public void discardAllData() {
		assistantData = null;
		serviceRange = null;
		additionalServices = null;
		outputFile = null;
	}
}
