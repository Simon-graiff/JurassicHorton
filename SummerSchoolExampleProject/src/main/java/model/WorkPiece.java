package model;

import java.util.ArrayList;

import org.springframework.scheduling.Trigger;

import com.github.oxo42.stateless4j.StateMachine;

import stateless4j.PartStates;
import stateless4j.Triggers;

public class WorkPiece {

	private StateMachine<PartStates, Triggers> fsm;
	
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
		ERPData = data;
	}

	public StateMachine<PartStates, Triggers> getFsm() {
		return fsm;
	}

	public void setFsm(StateMachine<PartStates, Triggers> fsm) {
		this.fsm = fsm;
	} 
	
	
	
	
	
}
