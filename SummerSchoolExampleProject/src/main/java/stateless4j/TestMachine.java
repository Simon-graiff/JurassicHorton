package stateless4j;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

public class TestMachine {

	private Logger _log = LogManager.getLogger(TestMachine.class); 
	
	public static int millingCounter;
	
	private static int drillingCounter; 
	
	private StateMachineConfig<PartStates, Triggers> fsmc ; 

	private StateMachine<PartStates, Triggers> fsm  ;

	public TestMachine() {
		configure();
	}
	
	public void run() {
		fsm = new StateMachine<PartStates, Triggers>(PartStates.INIT, fsmc);
		
		_log.debug(fsm.getState());
		fsm.fire(Triggers.MILLING_ON);
		_log.debug(fsm.getState());
		
		// This state transition is ignored by the config!
		fsm.fire(Triggers.DRILLING_ON);
		_log.debug(fsm.getState());
		
		fsm.fire(Triggers.MILLING_OFF);
		_log.debug(fsm.getState());
		
		fsm.fire(Triggers.DRILLING_ON);
		_log.debug(fsm.getState());
		
		fsm.fire(Triggers.DRILLING_OFF);
		_log.debug(fsm.getState());
		
		_log.debug("Milling Counter: " + millingCounter);
		_log.debug("Drilling Counter: " + drillingCounter);
		
	}
	
	
	private void configure() {
		fsmc = new StateMachineConfig<PartStates, Triggers>(); 
	}

	/**
	 * Increase the drilling counter
	 */
	public void increaseDrillingCounter() {
		drillingCounter++; 
	}
	
	/**
	 * increase the milling counter 
	 */
	public void increaseMillingCounter() {
		millingCounter++;
	}
}


