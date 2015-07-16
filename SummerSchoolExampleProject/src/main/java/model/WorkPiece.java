package model;

import org.springframework.scheduling.Trigger;

import com.github.oxo42.stateless4j.StateMachine;

import stateless4j.PartStates;
import stateless4j.Triggers;

public class WorkPiece {

	private StateMachine<PartStates, Triggers> fsm;

	public StateMachine<PartStates, Triggers> getFsm() {
		return fsm;
	}

	public void setFsm(StateMachine<PartStates, Triggers> fsm) {
		this.fsm = fsm;
	} 
	
	
	
	
	
}
