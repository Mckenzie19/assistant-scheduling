package assistantscheduling.domain;

public class Position {
	private String name;

	public Position() {}

	public Position(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		// If object is being compared to itself it is equal
		if (o == this) {
			return true;
		}

		// Check if object is of type Position
		if (!(o instanceof Position)) {
			return false;
		}

		// Typecast so I can pull the name attribute
		Position p = (Position) o;

		//Compare names and see if they are the same
		return getName().equals(p.getName());
	}
}
