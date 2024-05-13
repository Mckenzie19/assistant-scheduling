package assistantscheduling;

import java.awt.EventQueue;
import java.time.Duration;

import assistantscheduling.domain.AssistantSchedule;
import assistantscheduling.domain.ServiceAssignment;
import assistantscheduling.solver.AssistantSchedulingConstraintProvider;
import assistantscheduling.userinterface.SwingInterface;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssistantSchedulingApp {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(AssistantSchedulingApp.class);

	    public static void main(String[] args) {    	
	    	EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
				    	SolverFactory<AssistantSchedule> solverFactory = SolverFactory.create(new SolverConfig()
		                .withSolutionClass(AssistantSchedule.class)
		                .withEntityClasses(ServiceAssignment.class)
		                .withConstraintProviderClass(AssistantSchedulingConstraintProvider.class)
		                // Sets how long the solver will run for
		                .withTerminationSpentLimit(Duration.ofMinutes(5)));
				    	Solver<AssistantSchedule> solver = solverFactory.buildSolver();
				    	
						SwingInterface frame = new SwingInterface(solver);
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
	    	
//	    	SolverFactory<AssistantSchedule> solverFactory = SolverFactory.create(new SolverConfig()
//	                .withSolutionClass(AssistantSchedule.class)
//	                .withEntityClasses(ServiceAssignment.class)
//	                .withConstraintProviderClass(AssistantSchedulingConstraintProvider.class)
//	                // Sets how long the solver will run for
//	                .withTerminationSpentLimit(Duration.ofMinutes(1)));
//
//	        // Load the problem
//	        DataIO dataHandler = new DataIO();
//	        AssistantSchedule problem = dataHandler.getData();
//	        if (problem == null) return;
//	        
//	        // DEBUG PRINT
//	        // LOGGER.info(problem.toString());
//	        
//	        // Solve the problem
//	        Solver<AssistantSchedule> solver = solverFactory.buildSolver();
//	        AssistantSchedule solution = solver.solve(problem);
//	        LOGGER.info("Solving complete, writing results file now");
//
//	        // Visualize the solution
//	        dataHandler.saveSolution(solution);
//	        LOGGER.info("Schedule file ready.");
	       
	    }
}
