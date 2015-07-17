package model;

import java.util.ArrayList;

import org.springframework.scheduling.Trigger;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;

import stateless4j.PartStates;
import stateless4j.Triggers;

public class WorkPiece {

	private StateMachine<PartStates, Triggers> fsm;
	
	private StateMachineConfig<PartStates, Triggers> fsmc;
	
	private ERPData ERPData = null;
	
	private ArrayList<OPCDataItem> OPCDataItemList;
	
	public ERPData getERPData() {
		return ERPData;
	}

	public void setERPData(ERPData eRPData) {
		ERPData = eRPData;
	}

	public ArrayList<OPCDataItem> getOPCDataItemList() {
		return OPCDataItemList;
	}

	public void setOPCDataItemList(ArrayList<OPCDataItem> oPCDataItemList) {
		OPCDataItemList = oPCDataItemList;
	}

	public WorkPiece(ERPData data)
	{
		configure();
		fsm = new StateMachine<PartStates, Triggers>(PartStates.INIT, fsmc);
		ERPData = data;
	}

	public StateMachine<PartStates, Triggers> getFsm() {
		return fsm;
	}

	public void setFsm(StateMachine<PartStates, Triggers> fsm) {
		this.fsm = fsm;
	} 
	
	public void configure()
	{
		fsmc = new StateMachineConfig<PartStates, Triggers>();
		
		fsmc.configure(PartStates.INIT)
		.permit(Triggers.L1_FALSE, PartStates.L1_IN)
		.onEntry(this::doSomething);
		
		fsmc.configure(PartStates.L1_IN)
		.permit(Triggers.L1_TRUE, PartStates.L1_OUT);

	}

	public void doSomething()
	{
		System.out.println("Holla");
	}
	
	
	
	
	
}
