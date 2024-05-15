package assistantscheduling.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Assistant {

	// Name of Worship Assistant
	private String name;
	// List of months that they are not available to help in
	private List<String> unavailableMonths;
	// Max frequency they want (0 = any, 1 = once per month, 2 = once per quarter, 3 = twice per year)
	private int frequency;
	// List of positions they are willing to be assigned
	private List<Position> preferredPositions;


	public Assistant() {}

	public Assistant(String name, List<String> unavailableMonths, int frequency, List<String> preferredPositions) {
		this.name = name;
		this.unavailableMonths = unavailableMonths;
		this.frequency = frequency;
		this.preferredPositions = preferredPositions.stream().map(Position::new).collect(Collectors.toList());
	}

	public String getName() {
		return name;
	}

	public List<String> getUnavailableMonths() {
		return unavailableMonths;
	}

	public int getFrequency() {
		return frequency;
	}

	public List<Position> getPreferredPositions() {
		return preferredPositions;
	}

	@Override
	public String toString() {
		return name;
	}
}
