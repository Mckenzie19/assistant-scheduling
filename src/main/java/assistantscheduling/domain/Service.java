package assistantscheduling.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Service {
	private String name;
	private LocalDate date;
	private LocalTime time;
	private List<Position> positions;
	
	public Service() {}
	
	public Service(String name, LocalDate date, LocalTime time, List<String> positions) {
		this.name = name;
		this.date = date;
		this.time = time;
		this.positions = positions.stream().map(Position::new).collect(Collectors.toList());
	}
	
	public String getName() {
		return name;
	}
	
	public LocalDate getDate() {
		return date;
	}
	
	public LocalTime getTime() {
		return time;
	}
	
	public List<Position> getPositions(){
		return positions;
	}
	
	@Override
	public String toString() {
		return name + ": " + date + " (" + time + ")";
	}
}
