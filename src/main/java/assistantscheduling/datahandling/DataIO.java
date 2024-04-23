package assistantscheduling.datahandling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.json.JSONArray;

import assistantscheduling.domain.Assistant;
import assistantscheduling.domain.AssistantSchedule;
import assistantscheduling.domain.Position;
import assistantscheduling.domain.Service;
import assistantscheduling.domain.ServiceAssignment;

public class DataIO {
	
	private String inputFilePath;
	private String outputFilePath;
	private Scanner scanner;

	public DataIO() {
		scanner = new Scanner(System.in);
	}

	/* PUBLIC CLASSES */
	public AssistantSchedule getData() {
		getInputFileName();
		JSONObject jsonData = getInputDataFromFile();
		return convertJsonToProblem(jsonData);
	}
	
	public void saveSolution(AssistantSchedule solution) {
		getOutputFileName();
		JSONObject jsonData = convertSolutionToJson(solution);
		writeOutputData(jsonData);
	}
	
	/* INPUT CLASSES */
	
	private void getInputFileName() {
        System.out.println("Enter the path to the Excel file (without extension):");
        inputFilePath = scanner.nextLine() + ".xlsx";
    }
	
	private JSONObject getInputDataFromFile() {
		 JSONObject jsonWorkbook = new JSONObject();
	        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(new File(inputFilePath)))) {
	            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
	                Sheet sheet = workbook.getSheetAt(i);
	                JSONArray jsonSheet = new JSONArray();
	                Row headerRow = sheet.getRow(0);
	                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
	                    Row currentRow = sheet.getRow(rowIndex);
	                    JSONObject rowObject = new JSONObject();
	                    for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
	                        Cell headerCell = headerRow.getCell(cellIndex);
	                        Cell currentCell = currentRow.getCell(cellIndex);
	                        if (headerCell != null && currentCell != null) {
	                            rowObject.put(headerCell.getStringCellValue(), getCellValue(currentCell));
	                        }
	                    }
	                    jsonSheet.put(rowObject);
	                }
	                jsonWorkbook.put(sheet.getSheetName(), jsonSheet);
	            }
	        } catch (IOException e) {
	        	e.printStackTrace();
	        } catch (NullPointerException e) {
	        	e.printStackTrace();
	        }
	        return jsonWorkbook;
	}
	
	private static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
        case STRING:
            String cellValue = cell.getStringCellValue();
            return cellValue;
        case NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
            	DataFormatter df = new DataFormatter();
            	String stringDateValue = df.formatCellValue(cell);
                return stringDateValue;
            } else {
                return cell.getNumericCellValue();
            }
        case BOOLEAN:
            return cell.getBooleanCellValue();
        case FORMULA:
            return cell.getCellFormula();
        case BLANK:
            return "";
        default:
            return "";
		}
	}
	
	private AssistantSchedule convertJsonToProblem(JSONObject jsonData) {
		// Retrieve Services
		JSONArray services = jsonData.getJSONArray("Services");
		List<Service> serviceList = new ArrayList<Service>();
		// Use this to make sure date entries convert properly
		 DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("[M/d/yy][M/d/yyyy][MM/dd/yy][MM/dd/yyyy]");
		 DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("[hh:mm a][h:mm a][hh a][h a]");
		for(int i = 0; i<services.length(); i++) {
			JSONObject serviceJSON = services.getJSONObject(i);
			// Retrieve String name
			String name = serviceJSON.getString("Name");
			// Retrieve LocalDate date
			LocalDate date = LocalDate.parse(serviceJSON.getString("Date"), dateFormatter);
			// Retrieve LocalTime time
			LocalTime time = LocalTime.parse(serviceJSON.getString("Time"), timeFormatter);
			// Retrieve List<String> positions
			String positionsString = serviceJSON.getString("Positions");
			List<String> positions = Arrays.asList(positionsString.split(", "));
			serviceList.add(new Service(name, date, time, positions));
		}
		
		// Get Positions
		JSONArray positions = jsonData.getJSONArray("Positions");
		List<Position> positionList = new ArrayList<Position>();
		for(int i = 0; i<positions.length(); i++) {
			JSONObject positionJSON = positions.getJSONObject(i);
			// Retrieve String name
			String name = positionJSON.getString("Position");
			positionList.add(new Position(name));
		}
		
		// Get Assistants
		// Assistants MUST HAVE Name, Preferred Positions, and Frequency
		// Assistants have the OPTIONAL attribute of Unavailable Months
		JSONArray assistants = jsonData.getJSONArray("Assistants");
		List<Assistant> assistantList = new ArrayList<Assistant>();
		for(int i = 0; i<assistants.length(); i++) {
			JSONObject assistantJSON = assistants.getJSONObject(i);
			// Retrieve String name
			String name = assistantJSON.getString("Name");
			
			// Check for and retrieve List<String> unavailableMonths
			List<String> unavailableMonths = new ArrayList<String>(); // Initialize this as a blank list in case they did not list any unavailable months
			if (assistantJSON.has("Unavailable Months")){
				String unavailableMonthsString = assistantJSON.getString("Unavailable Months");
				unavailableMonths = Arrays.asList(unavailableMonthsString.split(", "));
			}
			
			// Retrieve int frequency
			String rawFreq = assistantJSON.getString("Frequency");
			int frequency;
			switch (rawFreq) {
				case "Any": frequency = 0;
							break;
				case "Once per month": frequency = 1;
									   break;
				case "Once per quarter": frequency = 2;
										 break;
				case "Twice per year": frequency = 3;
									   break;
				default: frequency = 0; 
			}
			
			// Retrieve List<String> preferredPositions
			String preferredPositionsString = assistantJSON.getString("Preferred Positions");
			List<String> preferredPositions = Arrays.asList(preferredPositionsString.split(", "));
		
			assistantList.add(new Assistant(name, unavailableMonths, frequency, preferredPositions));
		}
		
		// ServiceAssignments will be altered by the solver, and does not need to be populated
		List<ServiceAssignment> serviceAssignments = new ArrayList<ServiceAssignment>();
		Long id = 0L;
		for (int i = 0; i<serviceList.size(); i++) {
			Service service = serviceList.get(i);
			for (int j = 0; j<service.getPositions().size(); j++) {
				serviceAssignments.add(new ServiceAssignment(id++));
			}
		}
		
		return new AssistantSchedule(assistantList, serviceList, positionList, serviceAssignments);
	}
	
	/* OUTPUT CLASSES */
	
	private void getOutputFileName() {
        System.out.println("Enter the path of the output Excel file (without extension):");
        outputFilePath = scanner.nextLine() + ".xlsx";
        scanner.close();
	}
	
	private JSONObject convertSolutionToJson(AssistantSchedule solution) {
    	JSONObject jsonData = new JSONObject();
    	
    	// Add new key:value pair for each service
    	// Each service assignment should be added to the corresponding service
    	// Each service should have the name, date, time, and assigned positions filled out
    	
    	List<Service> serviceList = solution.getServiceList();
    	DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    	DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm a");
    	for (int i = 0; i < serviceList.size(); i++){
    		JSONObject service = new JSONObject();
    		service.put("Date", serviceList.get(i).getDate().format(dateFormat));
    		service.put("Time", serviceList.get(i).getTime().format(timeFormat));
    		JSONObject positions = new JSONObject();
    		service.put("Positions", positions);
    		jsonData.put(serviceList.get(i).getName(), service);
    	}
    	
    	List<ServiceAssignment> assignmentList = solution.getServiceAssignmentList();
    	for (int i = 0; i < assignmentList.size(); i++) {
    		ServiceAssignment assignment = assignmentList.get(i);
    		String serviceName = assignment.getService().getName();
    		String positionName = assignment.getPosition().getName();
    		String assistantName;
    		try {
    			assistantName = assignment.getAssistant().getName();
    		} catch (NullPointerException e) {
    			assistantName = " ";
    		}
    		
    		if (jsonData.getJSONObject(serviceName).getJSONObject("Positions").has(positionName)) {
    			String assistantNames = jsonData.getJSONObject(serviceName).getJSONObject("Positions").getString(positionName);
    			assistantNames += ", " + assistantName;
    			jsonData.getJSONObject(serviceName).getJSONObject("Positions").put(positionName, assistantNames);
    		} else {
    			jsonData.getJSONObject(serviceName).getJSONObject("Positions").put(positionName, assistantName);
    		}
    	}

    	//services = {"Service1": 
    	//				{"Date": "2/4/24", "Time": "09:00 AM", "Positions": 
    	//															{"Lector": "Ann Brown", "Ushers": "Name1, Name2", "Cantor": "Name"}}}
    	
    	return jsonData;
    }
	
    private void writeOutputData(JSONObject jsonData) {   
    	
    	System.out.println(jsonData);
    	
    	XSSFWorkbook scheduleWorkbook = new XSSFWorkbook();
    	XSSFSheet scheduleSheet = scheduleWorkbook.createSheet("Shedule");
    	
    	// Setting up cell styles
    	XSSFCellStyle titleCellStyle = scheduleWorkbook.createCellStyle();
    	Font titleFont = scheduleWorkbook.createFont();
    	titleFont.setBold(true);
    	titleFont.setFontHeightInPoints((short) 14);
    	titleCellStyle.setFont(titleFont);
    	
    	XSSFCellStyle positionStyle = scheduleWorkbook.createCellStyle();
    	Font positionFont = scheduleWorkbook.createFont();
    	positionFont.setFontHeightInPoints((short) 12);
    	positionStyle.setFont(positionFont); // TODO: set alignment
    	
    	// Used to alternate cell colors between services to make it easier to read
    	Queue<IndexedColors> colorQueue = new LinkedList<>();
    	colorQueue.add(IndexedColors.AQUA);
    	colorQueue.add(IndexedColors.CORAL);
    	colorQueue.add(IndexedColors.GREEN);
    	colorQueue.add(IndexedColors.ORCHID);
    	
    	// Use int arrays to circumvent the issues altering local variables in the below loops
    	int[] rowNum = new int[1];
    	int[] colNum = new int[1];
    	
    	// Iterate through each service in the data
    	jsonData.keySet().forEach(serviceName ->
    			{
    				// Get the service's JSON object
    				JSONObject service = jsonData.getJSONObject(serviceName);
    				
    				// Create title row (contains date and name of service)
    				Row titleRow = scheduleSheet.createRow(rowNum[0]++);
    				String serviceDate = service.getString("Date");
    	    		Cell dateCell = titleRow.createCell(colNum[0]++);
    	    		dateCell.setCellStyle(titleCellStyle);
    				dateCell.setCellValue(serviceDate);
    				Cell nameCell = titleRow.createCell(colNum[0]++);
    				nameCell.setCellStyle(titleCellStyle);
    				nameCell.setCellValue(serviceName);
    				// Reset the column number for the next row
    				colNum[0] = 0;
    				
    				// Iterate over positions and create rows for each one
    				JSONObject positions = service.getJSONObject("Positions");	
    				positions.keySet().forEach(positionName -> 
    						{
    							Row positionRow = scheduleSheet.createRow(rowNum[0]++);
    							Cell positionNameCell = positionRow.createCell(colNum[0]++);
    							positionNameCell.setCellStyle(positionStyle);
    							positionNameCell.setCellValue(positionName);
    							Cell positionAssistantsCell = positionRow.createCell(colNum[0]++);
    							positionAssistantsCell.setCellStyle(positionStyle);
    							positionAssistantsCell.setCellValue(positions.getString(positionName));
    							// Reset the column number after each row
    							colNum[0] = 0;
    						});
    			});

        try {

            FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
            scheduleWorkbook.write(fos);
            fos.close();
            scheduleWorkbook.close();

            System.out.println("Excel file '" + outputFilePath + "' created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
