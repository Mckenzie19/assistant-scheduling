package assistantscheduling.solver;

import static org.optaplanner.core.api.score.stream.ConstraintCollectors.count;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.countBi;
import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.filtering;

import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.Joiners;

import assistantscheduling.domain.Assistant;
import assistantscheduling.domain.Position;
import assistantscheduling.domain.ServiceAssignment;

public class AssistantSchedulingConstraintProvider implements ConstraintProvider{

	 @Override
	    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
	        return new Constraint[]{
	        		// Hard Constraints
	        		// Check if assistant is available that month
	                availabilityConstraint(constraintFactory),
	                // Checks for >monthly scheduling for those who don't want it
	                monthlyConstraint(constraintFactory),
	                // Checks for >quarterly scheduling for those who don't want it
	                quarterlyConstraint(constraintFactory),
	                // Checks for >twice yearly scheduling for those who don't want it
	                twiceYearlyConstraint(constraintFactory),
	                // Check if assistants are double booked
	                bookingConstraint(constraintFactory),
	                // Check if assigned position is one the assistant is okay with
	                positionConstraint(constraintFactory),
	        		// Check that each position is only assigned once per service (NOTE that a type of position may occur more than once per service)
	                onePositionConstraint(constraintFactory),

	                // Medium Constraints
	                // Check if all assignments have assistants
	                unassignedConstraint(constraintFactory),

	                // Soft Constraints
	                // Check if assistant was scheduled to help two weeks in a row
	                frequentAssignmentsConstraint(constraintFactory),
	                // Check if all assistants are utilized in the schedule
	                unusedAssistantsConstraint(constraintFactory)
	        };
	    }

		// Check if assistant is available that month
	    private Constraint availabilityConstraint(ConstraintFactory constraintFactory) {
	       return constraintFactory.forEach(ServiceAssignment.class)
	    		   .filter(serviceAssignment -> serviceAssignment.getAssistant().getUnavailableMonths()
	    				   .contains(serviceAssignment.getService().getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.US)))
	    		   .penalize(HardMediumSoftScore.ONE_HARD)
	    		   .asConstraint("Availability Constraint");
	    }

        // Checks for >monthly scheduling for those who don't want it
	    private Constraint monthlyConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachUniquePair(ServiceAssignment.class,
	    			equal(ServiceAssignment::getAssistant))
	    			.filter((firstAssignment, secondAssignment) -> firstAssignment.getAssistant().getFrequency() == 1)
	    			.groupBy((firstAssignment, secondAssignment) -> firstAssignment.getAssistant().getName(),
					(firstAssignment, secondAssignment) -> firstAssignment.getService().getDate().getMonth(),
					countBi())
	    			.filter((assistantId, month, count) -> count > 1)
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Monthly Assignment Constraint");
	    }

        // Checks for >quarterly scheduling for those who don't want it
	    private Constraint quarterlyConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachUniquePair(ServiceAssignment.class,
	                equal(ServiceAssignment::getAssistant),
	                filtering((firstAssignment, secondAssignment) -> {
	                    int firstQuarter = (firstAssignment.getService().getDate().getMonthValue() - 1) / 3;
	                    int secondQuarter = (secondAssignment.getService().getDate().getMonthValue() - 1) / 3;
	                    return firstQuarter == secondQuarter;
	                }))
	    			.filter((firstAssignment, secondAssignment) -> firstAssignment.getAssistant().getFrequency() == 2)
	    			// TODO: Check if this groupBy is even needed
	    			.groupBy((firstAssignment, secondAssignment) -> firstAssignment.getAssistant().getName(),
	                        (firstAssignment, secondAssignment) -> {
	                            int quarter = (firstAssignment.getService().getDate().getMonthValue() - 1) / 3;
	                            return quarter;
	                        },
	                        countBi())
	                .filter((assistantId, quarter, count) -> count > 1)
	                .penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Quarterly Assignment Constraint");
	    }

        // Checks for >twice yearly scheduling for those who don't want it
	    private Constraint twiceYearlyConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachUniquePair(ServiceAssignment.class,
	                equal(ServiceAssignment::getAssistant),
	                filtering((firstAssignment, secondAssignment) -> {
	                    int firstYear = (firstAssignment.getService().getDate().getMonthValue() - 1) / 12;
	                    int secondYear = (secondAssignment.getService().getDate().getMonthValue() - 1) / 12;
	                    return firstYear == secondYear;
	                }))
	                .filter((firstAssignment, secondAssignment) -> firstAssignment.getAssistant().getFrequency() == 3)
	                .groupBy((firstAssignment, secondAssignment) -> firstAssignment.getAssistant().getName(),
	                		(firstAssignment, secondAssignment) -> firstAssignment.getService().getDate().getYear(),
	                		countBi())
	                .filter((assistantId, year, count) -> count > 2)
	                .penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Twice Yearly Assignment Constraint");
	    }

        // Check if assistants are double booked
	    private Constraint bookingConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(ServiceAssignment.class)
	    			.groupBy(ServiceAssignment::getAssistant, ServiceAssignment::getService, count())
	    			.filter((assistant, service, count) -> count > 1)
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Booking Constraint");
	    }

        // Check if assigned position is one the assistant is okay with
	    private Constraint positionConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(ServiceAssignment.class)
	    			// TODO: check if this is correct
	    			.filter(assignment -> !assignment.getAssistant().getPreferredPositions().contains(assignment.getPosition()))
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Position Constraint");
	    }

	    // preferredPositions.stream().map(Position::new).collect(Collectors.toList())
		// Check that each position is only assigned once per service (NOTE that a type of position may occur more than once per service)
	    private Constraint onePositionConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(ServiceAssignment.class)
	    			.groupBy(ServiceAssignment::getService, ServiceAssignment::getPosition, count())
	    			.filter((service, position, count) ->
	    				{
	    					// Get the names of the positions for the service
	    					List<String> positions = service.getPositions().stream().map(Position::getName).collect(Collectors.toList());
	    					// Check how many times the specific position occurs in the service
	    					int posCount = Collections.frequency(positions, position.getName());
	    					// See if that count is higher than the number of times the position was actually assigned
	    					return count > posCount;
	    				})
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("One Position per Service Constraint");
	    }

        // Check if all assignments have assistants
	    private Constraint unassignedConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachIncludingNullVars(ServiceAssignment.class)
	    			.filter(serviceAssignment -> serviceAssignment.getAssistant() == null)
	    			.penalize(HardMediumSoftScore.ONE_MEDIUM)
	    			.asConstraint("Unassigned Assistant Constraint");
	    }

        // Check if assistant was scheduled to help two weeks in a row
	    private Constraint frequentAssignmentsConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachUniquePair(ServiceAssignment.class,
	    				equal(ServiceAssignment::getAssistant),
	    				filtering((firstAssignment, secondAssignment) ->
	    						ChronoUnit.DAYS.between(firstAssignment.getService().getDate(), secondAssignment.getService().getDate()) < 10))
	    			.penalize(HardMediumSoftScore.ONE_SOFT)
	    			.asConstraint("Frequent Assignments Constraint");
	    }

        // Check if all assistants are utilized in the schedule
		private Constraint unusedAssistantsConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(Assistant.class)
	    			.ifNotExists(ServiceAssignment.class, Joiners.equal(Function.identity(), ServiceAssignment::getAssistant))
	    			.penalize(HardMediumSoftScore.ONE_SOFT)
	    			.asConstraint("Unused Assistants Constraint");
	    }
}

