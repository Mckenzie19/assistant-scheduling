package assistantscheduling;

import java.time.Duration;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import assistantscheduling.domain.AssistantSchedule;
import assistantscheduling.domain.ServiceAssignment;
import assistantscheduling.solver.AssistantSchedulingConstraintProvider;
import assistantscheduling.userinterface.Splash;
import assistantscheduling.userinterface.SwingInterface;

public class AssistantSchedulingApp {

	 private static final Logger LOGGER = LoggerFactory.getLogger(AssistantSchedulingApp.class);
	 public static Solver<AssistantSchedule> solver;

	    public static void main(String[] args) {
	    	LOGGER.info("Loading application... ");
	    	
	    	// Create a runnable event to create the GUI
	    	final Runnable createGUI = new Runnable() {
		    	public void run() {
		    		try {
	                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	                } catch (Exception ex) {
	                    ex.printStackTrace();
	                }
		    		SwingInterface frame = new SwingInterface(solver);
					frame.setVisible(true);
		    	}
		    };
		    
		    // Setup the Opta-planner solver
	    	SolverFactory<AssistantSchedule> solverFactory = SolverFactory.create(new SolverConfig()
	                .withSolutionClass(AssistantSchedule.class)
	                .withEntityClasses(ServiceAssignment.class)
	                .withConstraintProviderClass(AssistantSchedulingConstraintProvider.class)
	                // Sets how long the solver will run for
	                .withTerminationSpentLimit(Duration.ofMinutes(5)));
	    	solver = solverFactory.buildSolver();
	    	
	    	// Splash splash = new Splash();
	    	
	    	Thread appThread = new Thread() {
	    		public void run() {
	    			try {
	    				SwingUtilities.invokeLater(createGUI);
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
	    	};
	    	appThread.start();
	    }
}
