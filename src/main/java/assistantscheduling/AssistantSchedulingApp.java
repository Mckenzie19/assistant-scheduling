package assistantscheduling;

import java.awt.EventQueue;
import java.time.Duration;

import assistantscheduling.domain.AssistantSchedule;
import assistantscheduling.domain.ServiceAssignment;
import assistantscheduling.solver.AssistantSchedulingConstraintProvider;
import assistantscheduling.userinterface.SwingInterface;
import assistantscheduling.userinterface.Splash;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssistantSchedulingApp {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(AssistantSchedulingApp.class);

	    public static void main(String[] args) {  
	    	LOGGER.info("Loading application... ");
	    	Splash splash = new Splash();
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
	    	LOGGER.info("Closing application... ");	       
	    }
}
