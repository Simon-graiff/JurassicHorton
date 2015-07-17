package model;

import java.util.ArrayList;
import java.util.List;

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

	public WorkPiece(ERPData data) {
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

	public void configure() {
		fsmc = new StateMachineConfig<PartStates, Triggers>();

		fsmc.configure(PartStates.INIT).permit(Triggers.L1_FALSE, PartStates.L1_IN).ignore(null);

		fsmc.configure(PartStates.L1_IN).ignore(null).permit(Triggers.L1_TRUE, PartStates.L1_OUT);

		fsmc.configure(PartStates.L1_OUT).ignore(null).permit(Triggers.L2_FALSE, PartStates.L2_IN);

		fsmc.configure(PartStates.L2_IN).ignore(null).permit(Triggers.L2_TRUE, PartStates.L2_OUT);

		fsmc.configure(PartStates.L2_OUT).ignore(null).permit(Triggers.L3_FALSE, PartStates.L3_IN);

		fsmc.configure(PartStates.L3_IN).ignore(null).permit(Triggers.MILLING_ON, PartStates.MILLING_ON);

		fsmc.configure(PartStates.MILLING_ON).ignore(null).permit(Triggers.MILLING_OFF, PartStates.MILLING_OFF);

		fsmc.configure(PartStates.MILLING_OFF).ignore(null).permit(Triggers.L3_TRUE, PartStates.L3_OUT);

		fsmc.configure(PartStates.L3_OUT).ignore(null).permit(Triggers.L4_FALSE, PartStates.L4_IN);

		fsmc.configure(PartStates.L4_IN).ignore(null).permit(Triggers.DRILLING_ON, PartStates.DRILLING_ON);

		fsmc.configure(PartStates.DRILLING_ON).ignore(null).permit(Triggers.DRILLING_OFF, PartStates.DRILLING_OFF);

		fsmc.configure(PartStates.DRILLING_OFF).ignore(null).permit(Triggers.L4_TRUE, PartStates.L4_OUT);

		fsmc.configure(PartStates.L4_OUT).ignore(null).permit(Triggers.L5_FALSE, PartStates.L5);

		fsmc.configure(PartStates.L5).ignore(null).permit(Triggers.L5_TRUE, PartStates.FINISHED).onExit(this::finish);

	}

	public void finish() {
		List<WorkPiece> list = WorkPieceList.list;
		System.out.println("Size of list "+list.size());
		if(list.size()==1)
		{
			list.remove(0);
		}
		/*
		if (list.size() > 1) {
			list.add(0, list.get(1));
			list.remove(1);
		} else {
			list.remove(0);
		}*/

		System.out.println("*****************************************");
		System.out.println(ERPData.getOrderNumber() + " is finished");
		System.out.println("*****************************************");
	}

}
