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

	private OPCDataItem tmpData = null;

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

		fsmc.configure(PartStates.INIT).permit(Triggers.L1_FALSE, PartStates.L1_IN).ignore(null)
				.ignore(Triggers.L1_TRUE).ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE);

		fsmc.configure(PartStates.L1_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L2_TRUE)
				.ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L1_TRUE, PartStates.L1_OUT);

		fsmc.configure(PartStates.L1_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L2_FALSE, PartStates.L2_IN);

		fsmc.configure(PartStates.L2_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L2_TRUE, PartStates.L2_OUT);

		fsmc.configure(PartStates.L2_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L3_FALSE, PartStates.L3_IN);

		fsmc.configure(PartStates.L3_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.MILLING_ON, PartStates.MILLING_ON);

		fsmc.configure(PartStates.MILLING_ON).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE)
				.permit(Triggers.MILLING_OFF, PartStates.MILLING_OFF);

		fsmc.configure(PartStates.MILLING_OFF).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L3_TRUE, PartStates.L3_OUT);

		fsmc.configure(PartStates.L3_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L4_FALSE, PartStates.L4_IN);

		fsmc.configure(PartStates.L4_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE)
				.permit(Triggers.DRILLING_ON, PartStates.DRILLING_ON);

		fsmc.configure(PartStates.DRILLING_ON).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE)
				.permit(Triggers.DRILLING_OFF, PartStates.DRILLING_OFF);

		fsmc.configure(PartStates.DRILLING_OFF).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L4_TRUE, PartStates.L4_OUT);

		fsmc.configure(PartStates.L4_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_TRUE).permit(Triggers.L5_FALSE, PartStates.L5);

		fsmc.configure(PartStates.L5).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_FALSE).permit(Triggers.L5_TRUE, PartStates.FINISHED)
				.onExit(this::finish);

	}

	public void finish() {
		List<WorkPiece> list = WorkPieceList.list;
		System.out.println("Size of list " + list.size());
		if (list.size() == 1) {
			list.remove(0);
		}
		/*
		 * if (list.size() > 1) { list.add(0, list.get(1)); list.remove(1); }
		 * else { list.remove(0); }
		 */

		System.out.println("*****************************************");
		System.out.println(ERPData.getOrderNumber() + " is finished");
		System.out.println("*****************************************");
	}

	public void saveToOPCDataItemList() {
		OPCDataItemList.add(tmpData);
	}

	public void handleOPCDataItem(OPCDataItem tmpData) {

		this.tmpData = tmpData;

		String itemName = tmpData.getItemName();

		if (itemName.equals("Milling Heat") || itemName.equals("Milling Speed") || itemName.equals("Drilling Heat")
				|| itemName.equals("Drilling Speed")) {

			// Stuff to do with the values of Milling/Drilling Heat/Speed ->
			// Maybe Ignore?

		} else {
			Boolean value = null;
			value = (Boolean) tmpData.getValue();

			Triggers trigger = null;

			switch (itemName) {
			case "Lichtschranke 1":
				if (value == true) {
					trigger = Triggers.L1_TRUE;
				} else {
					trigger = Triggers.L1_FALSE;
				}
				break;
			case "Lichtschranke 2":
				if (value == true) {
					trigger = Triggers.L2_TRUE;
				} else {
					trigger = Triggers.L2_FALSE;
				}
				break;
			case "Lichtschranke 3":
				if (value == true) {
					trigger = Triggers.L3_TRUE;
				} else {
					trigger = Triggers.L3_FALSE;
				}
				break;
			case "Lichtschranke 4":
				if (value == true) {
					trigger = Triggers.L4_TRUE;
				} else {
					trigger = Triggers.L4_FALSE;
				}
				break;
			case "Lichtschranke 5":
				if (value == true) {
					trigger = Triggers.L5_TRUE;
				} else {
					trigger = Triggers.L5_FALSE;
				}
				break;
			case "Milling Station":
				if (value == true) {
					trigger = Triggers.MILLING_ON;
				} else {
					trigger = Triggers.MILLING_OFF;
				}
				break;
			case "Drilling Station":
				if (value == true) {
					trigger = Triggers.DRILLING_ON;
				} else {
					trigger = Triggers.DRILLING_OFF;
				}
				break;
			}
			System.out.println("Trigger Value: " + trigger);
			fsm.fire(trigger);
		}

	}

}
