package assistantscheduling.solver;

import assistantscheduling.domain.Assistant;
import assistantscheduling.domain.ServiceAssignment;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import static org.optaplanner.core.api.score.stream.ConstraintCollectors.*;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import static org.optaplanner.core.api.score.stream.Joiners.*;

import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

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
	                // check if assigned position is one the assistant is okay with
	                positionConstraint(constraintFactory),
	                // Check that position is only assigned once per service
	                // onePositionConstraint(constraintFactory),	                
	                
	                // Medium Constraints
	                // Check if all assignments have assistants
	                unassignedConstraint(constraintFactory),
	                
	                // Soft Constraints
	                // Check if assistant was scheduled to help two weeks in a row
	                frequentAssignmentsConstraint(constraintFactory)
	                // Check if all assistants are utilized in the schedule
	                //utilizedAssistantsConstraint(constraintFactory)
	        };
	    }

	    private Constraint availabilityConstraint(ConstraintFactory constraintFactory) {
	       return constraintFactory.forEach(ServiceAssignment.class)
	    		   // TODO: check if the filter is correct
	    		   .filter(serviceAssignment -> serviceAssignment.getAssistant().getUnavailableMonths()
	    				   .contains(serviceAssignment.getService().getDate().getMonth().getDisplayName(TextStyle.FULL, Locale.US)))
	    		   .penalize(HardMediumSoftScore.ONE_HARD)
	    		   .asConstraint("Availability Constraint");
	    }
	    
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
	    
	    private Constraint bookingConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(ServiceAssignment.class)
	    			.groupBy(ServiceAssignment::getAssistant, ServiceAssignment::getService, count())
	    			.filter((assistant, service, count) -> count > 1)
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Booking Constraint");
	    }
	    
	    private Constraint positionConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(ServiceAssignment.class)
	    			// TODO: check if this is correct
	    			.filter(assignment -> !assignment.getAssistant().getPreferredPositions().contains(assignment.getPosition()))
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("Position Constraint");
	    }
	    
	    /* 
	    private Constraint onePositionConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(ServiceAssignment.class)
	    			.groupBy(ServiceAssignment::getService, ServiceAssignment::getPosition, count())
	    			.filter((service, position, count) -> count > 1)
	    			.penalize(HardMediumSoftScore.ONE_HARD)
	    			.asConstraint("One Position per Service Constraint");
	    } */
	    
	    private Constraint unassignedConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachIncludingNullVars(ServiceAssignment.class)
	    			.filter(serviceAssignment -> serviceAssignment.getAssistant() == null)
	    			.penalize(HardMediumSoftScore.ONE_MEDIUM)
	    			.asConstraint("Unassigned Assistant Constraint");
	    }
	    
	    private Constraint frequentAssignmentsConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEachUniquePair(ServiceAssignment.class,
	    				equal(ServiceAssignment::getAssistant),
	    				filtering((firstAssignment, secondAssignment) -> 
	    						ChronoUnit.DAYS.between(firstAssignment.getService().getDate(), secondAssignment.getService().getDate()) < 7))
	    			.penalize(HardMediumSoftScore.ONE_SOFT)
	    			.asConstraint("Frequent Assignments Constraint");
	    }
	    /*
	    private Constraint utilizedAssistantsConstraint(ConstraintFactory constraintFactory) {
	    	return constraintFactory.forEach(Assistant.class)
	    			.filter(assistant -> )
	    			.penalize(HardMediumSoftScore.ONE_SOFT)
	    			.asConstraint("Utilized Assistants Constraint");
	    }*/
}

