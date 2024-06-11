package assistantscheduling.dataio;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataFileCreator {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataFileCreator.class);

	private File assistantDataFile;
	private LocalDate[] serviceRange;
	private LocalDate[] additionalServices;
	private LocalDate[] communionDates;
	private File outputFile;

	public DataFileCreator() {}

	public File getAssistantDataFile() {
		return assistantDataFile;
	}

	public void setAssistantDataFile(File assistantDataFile) {
		this.assistantDataFile = assistantDataFile;
		LOGGER.info("Set assistant data import file: " + assistantDataFile.getName());
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
		assistantDataFile = null;
		serviceRange = null;
		additionalServices = null;
		outputFile = null;
	}
}
