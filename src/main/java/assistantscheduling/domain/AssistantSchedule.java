package assistantscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;

@PlanningSolution
public class AssistantSchedule {
	
	@ValueRangeProvider (id = "availableAssistants")
	@ProblemFactCollectionProperty
	private List<Assistant> assistantList;
	@ValueRangeProvider (id = "availableServices")
	@ProblemFactCollectionProperty
	private List<Service> serviceList;
	@ValueRangeProvider (id = "availablePositions")
	@ProblemFactCollectionProperty
	private List<Position> positionList;
	@PlanningEntityCollectionProperty
	private List<ServiceAssignment> serviceAssignmentList;
	
	@PlanningScore
	private HardMediumSoftScore score;
	
	public AssistantSchedule() {}
	
	public AssistantSchedule(List<Assistant> assistantList, List<Service> serviceList, List<Position> positionList, List<ServiceAssignment> serviceAssignmentList) {
		this.assistantList = assistantList;
		this.serviceList = serviceList;
		this.positionList = positionList;
		this.serviceAssignmentList = serviceAssignmentList;
	}
	
	public List<Assistant> getAssistantList(){
		return assistantList;
	}
	
	public List<Service> getServiceList(){
		return serviceList;
	}
	
	public List<Position> getPositionList(){
		return positionList;
	}
	
	public List<ServiceAssignment> getServiceAssignmentList(){
		return serviceAssignmentList;
	}
	
	public HardMediumSoftScore getScore() {
		return score;
	}
	
	@Override
	public String toString() {
		return "\n\tAssistants: " + assistantList.toString() + "\n" + 
				"\tServices: " + serviceList.toString() +"\n" +
				"\tPositions: " + positionList.toString();
	}
}
