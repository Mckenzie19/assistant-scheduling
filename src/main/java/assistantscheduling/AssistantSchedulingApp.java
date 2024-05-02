package assistantscheduling;

import java.time.Duration;

import assistantscheduling.domain.AssistantSchedule;
import assistantscheduling.domain.ServiceAssignment;
import assistantscheduling.solver.AssistantSchedulingConstraintProvider;
import assistantscheduling.datahandling.DataIO;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssistantSchedulingApp {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(AssistantSchedulingApp.class);

	    public static void main(String[] args) {
	        SolverFactory<AssistantSchedule> solverFactory = SolverFactory.create(new SolverConfig()
	                .withSolutionClass(AssistantSchedule.class)
	                .withEntityClasses(ServiceAssignment.class)
	                .withConstraintProviderClass(AssistantSchedulingConstraintProvider.class)
	                // Sets how long the solver will run for
	                .withTerminationSpentLimit(Duration.ofMinutes(5)));

	        // Load the problem
	        DataIO dataHandler = new DataIO();
	        AssistantSchedule problem = dataHandler.getData();
	        if (problem == null) return;
	        
	        // DEBUG PRINT
	        // LOGGER.info(problem.toString());
	        
	        // TODO: Positions get assigned to multiple people when they should not be
	        // TODO: The same people get picked every single week
	        
	        // Solve the problem
	        Solver<AssistantSchedule> solver = solverFactory.buildSolver();
	        AssistantSchedule solution = solver.solve(problem);
	        LOGGER.info("Solving complete, writing results file now");

	        // Visualize the solution
	        dataHandler.saveSolution(solution);
	        LOGGER.info("Schedule file ready.");
	       
	    }
}
