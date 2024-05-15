package assistantscheduling.userinterface;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import org.json.JSONObject;

public class ServiceComparator implements Comparator<JSONObject> {

	@Override
	public int compare(JSONObject service1, JSONObject service2) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm a");
		// Get LocalDateTime of each service
		LocalDate date1 = LocalDate.parse(service1.getJSONObject(service1.names().getString(0)).getString("Date"), df);
		LocalTime time1 = LocalTime.parse(service1.getJSONObject(service1.names().getString(0)).getString("Time"), tf);
		LocalDate date2 = LocalDate.parse(service2.getJSONObject(service2.names().getString(0)).getString("Date"), df);
		LocalTime time2 = LocalTime.parse(service2.getJSONObject(service2.names().getString(0)).getString("Time"), tf);
		LocalDateTime dateTime1 = LocalDateTime.of(date1, time1);
		LocalDateTime dateTime2 = LocalDateTime.of(date2, time2);

		return dateTime1.compareTo(dateTime2);
	}

}
