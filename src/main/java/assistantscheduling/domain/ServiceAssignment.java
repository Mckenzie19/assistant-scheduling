package assistantscheduling.domain;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class ServiceAssignment {

	@PlanningId
	private Long id;

	@PlanningVariable (valueRangeProviderRefs = {"availableAssistants"}, nullable = true) // This allows for some assistants to be unassigned if it creates a better, more cohesive schedule
	private Assistant assistant;
	@PlanningVariable (valueRangeProviderRefs = {"availableServices"})
	private Service service;
	@PlanningVariable (valueRangeProviderRefs = {"availablePositions"})
	private Position position;

	public ServiceAssignment() {}

	public ServiceAssignment(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public Assistant getAssistant() {
		return assistant;
	}

	public void setAssistant(Assistant assistant) {
		this.assistant = assistant;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	@Override
	public String toString() {
		return service + " - " + position + ": " + assistant;
	}
}
