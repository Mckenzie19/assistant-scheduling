package assistantscheduling.userinterface;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
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
	
	private File outputFile;
	private File inputFile;

	public DataIO() {}

	/* PUBLIC CLASSES */
	
	public File selectFile(JFrame frame) {
		LOGGER.debug("");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        fileChooser.setDialogTitle("Select a file");
        int result = fileChooser.showOpenDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        } else {
            return null;
        }
    }
	
	public void setInputFile(File file) {
		inputFile = file;
	}
	
	public AssistantSchedule getData() {
		JSONObject jsonData = getInputDataFromFile();
		return convertJsonToProblem(jsonData);
	}
	
	public void saveSolution(AssistantSchedule solution, File file) {
		outputFile = file;
		JSONObject jsonData = convertSolutionToJson(solution);
		writeOutputData(jsonData);
	}
	
	/* INPUT CLASSES */
	
	
	
	private JSONObject getInputDataFromFile() {
		 JSONObject jsonWorkbook = new JSONObject();
	        try (Workbook workbook = new XSSFWorkbook(new FileInputStream(inputFile))) {
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
				unavailableMonths = Arrays.asList(unavailableMonthsString.split(";"));
			}
			
			// Retrieve int frequency
			String rawFreq = assistantJSON.getString("Frequency");
			int frequency;
			switch (rawFreq) {
				case "As often as you need me": frequency = 0;
							break;
				case "Once per month": frequency = 1;
									   break;
				case "Once a quarter": frequency = 2;
										 break;
				case "Twice a year": frequency = 3;
									   break;
				default: frequency = 0; 
			}
			
			// Retrieve List<String> preferredPositions
			String preferredPositionsString = assistantJSON.getString("Preferred Positions");
			List<String> preferredPositions = Arrays.asList(preferredPositionsString.split(";"));
		
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
    		
    		// If the assistant name was null, then no need to update the JSON
    		if (assistantName != " ") {
    			if (jsonData.getJSONObject(serviceName).getJSONObject("Positions").has(positionName)) {
        			String assistantNames = jsonData.getJSONObject(serviceName).getJSONObject("Positions").getString(positionName);
        			assistantNames += ", " + assistantName;
        			jsonData.getJSONObject(serviceName).getJSONObject("Positions").put(positionName, assistantNames);
        		} else {
        			jsonData.getJSONObject(serviceName).getJSONObject("Positions").put(positionName, assistantName);
        		}
    		}
    	}

    	//services = {"Service1": 
    	//				{"Date": "2/4/24", "Time": "09:00 AM", "Positions": 
    	//															{"Lector": "Ann Brown", "Ushers": "Name1, Name2", "Cantor": "Name"}}}
    	
    	return jsonData;
    }
	
	// Helper function for the writeOutputData function
	private ArrayList<JSONObject> convertJSONtoList(JSONObject jsonData){
		ArrayList<JSONObject> listData = new ArrayList<>();
		// Iterate through each service in the data and add it to the list
    	jsonData.keySet().forEach(serviceName ->
    			{
    				listData.add(new JSONObject().put(serviceName, jsonData.getJSONObject(serviceName)));
    			});
    	Collections.sort(listData, new ServiceComparator());
		
		return listData;
	}
	
	// Helper function to create a queue of XSSFCellStyles
	private Queue<ArrayList<XSSFCellStyle>> generateCellStyleList(XSSFWorkbook scheduleWorkbook){
		// Cell Styles are static, so we make a new pair (title & positions) for each color
    	// We will use a LinkedList<> Queue to cycle through these pairs
    	// Title font style for all title styles
    	Font titleFont = scheduleWorkbook.createFont();
    	titleFont.setBold(true);
    	titleFont.setFontHeightInPoints((short) 16);
    	
    	// Position font style for all position styles
    	Font positionFont = scheduleWorkbook.createFont();
    	positionFont.setFontHeightInPoints((short) 14);
    	
    	// Color 1: Light Red
    	ArrayList<XSSFCellStyle> style1 = new ArrayList<>();
    	XSSFCellStyle color1TitleStyle = scheduleWorkbook.createCellStyle();
    	color1TitleStyle.setFont(titleFont);
    	color1TitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	byte[] color1Bytes = {(byte)223, (byte)186, (byte)177};
    	XSSFColor color1  = new XSSFColor(color1Bytes, new DefaultIndexedColorMap());
    	color1TitleStyle.setFillForegroundColor(color1);
    	color1TitleStyle.setBorderBottom(BorderStyle.MEDIUM);
    	style1.add(color1TitleStyle);
    
    	XSSFCellStyle color1PositionStyle = scheduleWorkbook.createCellStyle();
    	color1PositionStyle.setFont(positionFont);
    	color1PositionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	color1PositionStyle.setFillForegroundColor(color1);
    	color1PositionStyle.setBorderBottom(BorderStyle.THIN);
    	style1.add(color1PositionStyle);
    	
    	// Color 2: Light yellow
    	ArrayList<XSSFCellStyle> style2 = new ArrayList<>();
    	XSSFCellStyle color2TitleStyle = scheduleWorkbook.createCellStyle();
    	color2TitleStyle.setFont(titleFont);
    	color2TitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	byte[] color2Bytes = {(byte)248, (byte)229, (byte)208};
    	XSSFColor color2  = new XSSFColor(color2Bytes, new DefaultIndexedColorMap());
    	color2TitleStyle.setFillForegroundColor(color2);
    	color2TitleStyle.setBorderBottom(BorderStyle.MEDIUM);
    	style2.add(color2TitleStyle);
  
    	XSSFCellStyle color2PositionStyle = scheduleWorkbook.createCellStyle();
    	color2PositionStyle.setFont(positionFont);
    	color2PositionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	color2PositionStyle.setFillForegroundColor(color2);
    	color2PositionStyle.setBorderBottom(BorderStyle.THIN);
    	style2.add(color2PositionStyle);
    	
    	// Color 3: Light green
    	ArrayList<XSSFCellStyle> style3 = new ArrayList<>();
    	XSSFCellStyle color3TitleStyle = scheduleWorkbook.createCellStyle();
    	color3TitleStyle.setFont(titleFont);
    	color3TitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	byte[] color3Bytes = {(byte)220, (byte)233, (byte)213};
    	XSSFColor color3  = new XSSFColor(color3Bytes, new DefaultIndexedColorMap());
    	color3TitleStyle.setFillForegroundColor(color3);
    	color3TitleStyle.setBorderBottom(BorderStyle.MEDIUM);
    	style3.add(color3TitleStyle);
  
    	XSSFCellStyle color3PositionStyle = scheduleWorkbook.createCellStyle();
    	color3PositionStyle.setFont(positionFont);
    	color3PositionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	color3PositionStyle.setFillForegroundColor(color3);
    	color3PositionStyle.setBorderBottom(BorderStyle.THIN);
    	style3.add(color3PositionStyle);
    	
    	// Color 4: Light blue
    	ArrayList<XSSFCellStyle> style4 = new ArrayList<>();
    	XSSFCellStyle color4TitleStyle = scheduleWorkbook.createCellStyle();
    	color4TitleStyle.setFont(titleFont);
    	color4TitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	byte[] color4Bytes = {(byte)204, (byte)218, (byte)245};
    	XSSFColor color4  = new XSSFColor(color4Bytes, new DefaultIndexedColorMap());
    	color4TitleStyle.setFillForegroundColor(color4);
    	color4TitleStyle.setBorderBottom(BorderStyle.MEDIUM);
    	style4.add(color4TitleStyle);
  
    	XSSFCellStyle color4PositionStyle = scheduleWorkbook.createCellStyle();
    	color4PositionStyle.setFont(positionFont);
    	color4PositionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	color4PositionStyle.setFillForegroundColor(color4);
    	color4PositionStyle.setBorderBottom(BorderStyle.THIN);
    	style4.add(color4PositionStyle);
    	
    	// Color 5: Light purple
    	ArrayList<XSSFCellStyle> style5 = new ArrayList<>();
    	XSSFCellStyle color5TitleStyle = scheduleWorkbook.createCellStyle();
    	color5TitleStyle.setFont(titleFont);
    	color5TitleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	byte[] color5Bytes = {(byte)216, (byte)211, (byte)231};
    	XSSFColor color5  = new XSSFColor(color5Bytes, new DefaultIndexedColorMap());
    	color5TitleStyle.setFillForegroundColor(color5);
    	color5TitleStyle.setBorderBottom(BorderStyle.MEDIUM);
    	style5.add(color5TitleStyle);
    	
    	XSSFCellStyle color5PositionStyle = scheduleWorkbook.createCellStyle();
    	color5PositionStyle.setFont(positionFont);
    	color5PositionStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    	color5PositionStyle.setFillForegroundColor(color5);
    	color5PositionStyle.setBorderBottom(BorderStyle.THIN);
    	style5.add(color5PositionStyle);
    	
    	// Used to alternate cell colors between services to make it easier to read
    	Queue<ArrayList<XSSFCellStyle>> styleQueue = new LinkedList<>();
    	styleQueue.add(style1);
    	styleQueue.add(style2);
    	styleQueue.add(style3);
    	styleQueue.add(style4);
    	
		return styleQueue;
	}
	
	
    private void writeOutputData(JSONObject jsonData) {   
    	
    	ArrayList<JSONObject> listData = convertJSONtoList(jsonData);
    	
    	XSSFWorkbook scheduleWorkbook = new XSSFWorkbook();
    	XSSFSheet scheduleSheet = scheduleWorkbook.createSheet("Schedule");
    	
    	// Generates all of the styles used in the workbook
    	Queue<ArrayList<XSSFCellStyle>> styleQueue = generateCellStyleList(scheduleWorkbook);
    	
    	// Use int arrays to circumvent the issues with altering local variables in the below loops
    	int[] rowNum = new int[1];
    	int[] colNum = new int[1];
    	
    	for (int i = 0; i < listData.size(); i++) {
    		JSONObject fullService = listData.get(i);
    		// Since there should only be a single key:value pair, the service name is the first index of the names array
    		String serviceName = fullService.names().getString(0);
    		JSONObject service = fullService.getJSONObject(serviceName);
    		
    		// Get the style for the service
    		ArrayList<XSSFCellStyle> serviceStyle = styleQueue.poll();
    		styleQueue.add(serviceStyle);
    		
    		// Create title row (contains date and name of service)
			Row titleRow = scheduleSheet.createRow(rowNum[0]++);
			String serviceDate = service.getString("Date");
    		Cell dateCell = titleRow.createCell(colNum[0]++);
    		dateCell.setCellStyle(serviceStyle.get(0));
			dateCell.setCellValue(serviceDate);
			Cell nameCell = titleRow.createCell(colNum[0]++);
			nameCell.setCellStyle(serviceStyle.get(0));
			nameCell.setCellValue(serviceName);
			// Reset the column number for the next row
			colNum[0] = 0;
			
			// Iterate over positions and create rows for each one
			JSONObject positions = service.getJSONObject("Positions");
			String[] positionNames = JSONObject.getNames(positions);
			Queue<String> possiblePositions = new LinkedList<String>();
			possiblePositions.add("Lector");
			possiblePositions.add("Cantor");
			possiblePositions.add("Head Usher");
			possiblePositions.add("Usher");
			possiblePositions.add("Organist");
			possiblePositions.add("Acolyte");
			possiblePositions.add("Communion Server");
			possiblePositions.add("Greeter");
			possiblePositions.add("Refreshments");
			
			possiblePositions.forEach(pPositionName -> 
			{
				Row positionRow = scheduleSheet.createRow(rowNum[0]++);
				Cell positionNameCell = positionRow.createCell(colNum[0]++);
				positionNameCell.setCellStyle(serviceStyle.get(1));
				positionNameCell.setCellValue(pPositionName);
				Cell positionAssistantsCell = positionRow.createCell(colNum[0]++);
				positionAssistantsCell.setCellStyle(serviceStyle.get(1));
				// Checks if the current position is assigned for this service - if so enter assistant name, otherwise leave blank
				if(Arrays.stream(positionNames).anyMatch(pPositionName::equals)) {
					positionAssistantsCell.setCellValue(positions.getString(pPositionName));
				} else {
					positionAssistantsCell.setCellValue(" ");
				}
				// Reset the column number after each row
				colNum[0] = 0;
			});
    	}
    	
    	// Size the columns to match the inputs given
    	scheduleSheet.autoSizeColumn(0);
    	scheduleSheet.autoSizeColumn(1);

    	// Write workbook to actual file
        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            scheduleWorkbook.write(fos);
            fos.close();
            scheduleWorkbook.close();

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
