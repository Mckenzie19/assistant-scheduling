package assistantscheduling;

import java.awt.EventQueue;
import java.time.Duration;
import java.util.Arrays;

import javax.swing.JFrame;
import javax.swing.UIManager;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.domain.AssistantSchedule;
import assistantscheduling.domain.ServiceAssignment;
import assistantscheduling.solver.AssistantSchedulingConstraintProvider;
import assistantscheduling.ui.MainFrame;

public class AssistantSchedulingApp {

	 private static final Logger LOGGER = LoggerFactory.getLogger(AssistantSchedulingApp.class);
	 public static Solver<AssistantSchedule> solver;

	    public static void main(String[] args) {
		    // Setup the Opta-planner solver
	    	LOGGER.info("Creating SolverFactory and problem Solver...");
	    	SolverFactory<AssistantSchedule> solverFactory = SolverFactory.create(new SolverConfig()
	                .withSolutionClass(AssistantSchedule.class)
	                .withEntityClasses(ServiceAssignment.class)
	                .withConstraintProviderClass(AssistantSchedulingConstraintProvider.class)
	                // Sets how long the solver will run for
	                .withTerminationSpentLimit(Duration.ofMinutes(5)));
	    	solver = solverFactory.buildSolver();
	    	
	    	// Splash splash = new Splash();
	    	LOGGER.info("Scheduling application gui...");
	    	EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
	                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                } catch (Exception e) {
	                    LOGGER.error(e.getMessage());
	                    
	                }
					
					try {
						JFrame applicationFrame = new MainFrame();
						applicationFrame.setVisible(true);
					} catch (Exception e) {
						LOGGER.error("Mesage: "+ e.getMessage() +"\n"
								+"Stack Trace: "+Arrays.toString(e.getStackTrace()));
					}
				}
			});
	    }
}
