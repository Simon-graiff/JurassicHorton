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

	private ArrayList<OPCDataItem> OPCDataItemList = null;

	private OPCDataItem tmpData = null;

	private ArrayList<Double> millingHeatList;
	private ArrayList<Integer> millingSpeedList;

	private ArrayList<Double> drillingHeatList;
	private ArrayList<Integer> drillingSpeedList;

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
		OPCDataItemList = new ArrayList<OPCDataItem>();

		millingHeatList = new ArrayList<Double>();
		millingSpeedList = new ArrayList<Integer>();

		drillingHeatList = new ArrayList<Double>();
		drillingSpeedList = new ArrayList<Integer>();
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
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L1_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L2_TRUE)
				.ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L1_TRUE, PartStates.L1_OUT)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L1_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L2_FALSE, PartStates.L2_IN)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L2_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L2_TRUE, PartStates.L2_OUT)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L2_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L3_FALSE, PartStates.L3_IN)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L3_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.MILLING_ON, PartStates.MILLING_ON)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.MILLING_ON).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.MILLING_OFF, PartStates.MILLING_OFF)
				.permitReentry(Triggers.MILLING).onEntry(this::handleMilling).onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.MILLING_OFF).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L3_TRUE, PartStates.L3_OUT)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L3_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L4_FALSE, PartStates.L4_IN)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L4_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.DRILLING_ON, PartStates.DRILLING_ON)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.DRILLING_ON).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE)
				.permit(Triggers.DRILLING_OFF, PartStates.DRILLING_OFF).permitReentry(Triggers.DRILLING)
				.onEntry(this::handleDrilling).onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.DRILLING_OFF).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L4_TRUE, PartStates.L4_OUT)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L4_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_TRUE).permit(Triggers.L5_FALSE, PartStates.L5)
				.onEntry(this::saveToOPCDataItemList);

		fsmc.configure(PartStates.L5).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_FALSE).permit(Triggers.L5_TRUE, PartStates.FINISHED)
				.onEntry(this::saveToOPCDataItemList).onExit(this::finish);

	}

	public void finish() {
		List<WorkPiece> list = WorkPieceList.list;
		// System.out.println("Size of list " + list.size());
		if (list.size() == 1) {
			list.remove(0);
		}
		/*
		 * if (list.size() > 1) { list.add(0, list.get(1)); list.remove(1); }
		 * else { list.remove(0); }
		 */

		/*
		 * System.out.println("Data of the OPCDataItem:\n"); for (int i = 0; i <
		 * OPCDataItemList.size(); i++) {
		 * System.out.print(OPCDataItemList.get(i).getItemName() + " " +
		 * OPCDataItemList.get(i).getTimestamp() + "\n"); }
		 */
		System.out.println("\n\n\n\n\n");
		System.out
				.println("****************** OUTPUT DATA FOR " + ERPData.getOrderNumber() + " ***********************\n");

		/*
		 * System.out.print("\n********\nSpeed:\n"); for (int i = 0; i <
		 * millingSpeedList.size(); i++) {
		 * System.out.print(millingSpeedList.get(i) + " "); }
		 * System.out.print("\n********\nHeat:\n"); for (int i = 0; i <
		 * millingHeatList.size(); i++) {
		 * System.out.print(millingHeatList.get(i) + " "); }
		 */
		System.out.println("AVG Milling heat: " + calcAvg(drillingHeatList));
		System.out.println("AVG milling speed: " + calcAvg(drillingSpeedList));
		System.out.println("AVG Drilling heat: " + calcAvg(millingHeatList));
		System.out.println("AVG Drilling Speed: " + calcAvg(drillingSpeedList));
		System.out.println("Max Milling Heat: " + getPeak(drillingHeatList));
		System.out.println("Max Milling speed: " + getPeak(drillingSpeedList));
		System.out.println("Max Drilling heat: " + getPeak(millingHeatList));
		System.out.println("Max Drilling Speed: " + getPeak(drillingSpeedList));
		System.out.println("Total time " + getTotalTime(OPCDataItemList));
		System.out.println("Customer Number " + ERPData.getCustomerNumber());
		System.out.println("Material Number " + ERPData.getMaterialNumber());
		System.out.println("Order Number " + ERPData.getOrderNumber());
		System.out.println("\n***************** END OF PRODUCT************************");

		System.out.println("\n\n\n\n\n");
	}

	public void saveToOPCDataItemList() {
		OPCDataItemList.add(tmpData);
	}

	public void handleMilling() {
		// System.out.println("*************" + tmpData.getItemName());
		if (tmpData.getItemName().equals("Milling Speed")) {
			millingSpeedList.add((int) tmpData.getValue());
			System.out.println("Milling Speed " + tmpData.getValue());
		} else if (tmpData.getItemName().equals("Milling Heat")) {
			millingHeatList.add((double) tmpData.getValue());
			// System.out.println("Milling Heat " + tmpData.getValue());
		}
	}

	public void handleDrilling() {
		// System.out.println("*************" + tmpData.getItemName());
		if (tmpData.getItemName().equals("Drilling Speed")) {
			drillingSpeedList.add((int) tmpData.getValue());
			// System.out.println("Drilling Speed " +tmpData.getValue());
		} else if (tmpData.getItemName().equals("Drilling Heat")) {
			drillingHeatList.add((double) tmpData.getValue());
			// System.out.println("Drilling Heat "+tmpData.getValue());
		}
	}

	public static long getTotalTime(ArrayList valueList) {
		long start = ((OPCDataItem) valueList.get(0)).getTimestamp();
		long end = ((OPCDataItem) valueList.get(valueList.size() - 1)).getTimestamp();

		return end - start;
	}

	public static double calcAvg(ArrayList valueList) {
		double sum = 0;
		double counter = 0;

		for (int i = 0; i < valueList.size(); i++) {
			try {
				sum = sum + (double) valueList.get(i);
			} catch (Exception e) {
				sum = sum + (double) (int) valueList.get(i);
			}
			counter++;
		}
		return sum / counter;
	}

	public static double getPeak(ArrayList valueList) {
		double max = 0;
		for (int i = 0; i < valueList.size(); i++) {
			try {
				if ((double) valueList.get(i) > max) {
					max = (double) valueList.get(i);
				}
			} catch (Exception e) {
				if ((double) (int) valueList.get(i) > max) {
					max = (double) (int) valueList.get(i);
				}
			}
		}
		return max;
	}

	public void handleOPCDataItem(OPCDataItem tmpData) {

		this.tmpData = tmpData;

		String itemName = tmpData.getItemName();
		Triggers trigger = null;

		if (itemName.equals("Milling Heat") || itemName.equals("Milling Speed") || itemName.equals("Drilling Heat")
				|| itemName.equals("Drilling Speed")) {
			switch (itemName) {
			case "Milling Speed":
				trigger = Triggers.MILLING;
				break;
			case "Milling Heat":
				trigger = Triggers.MILLING;
				break;
			case "Drilling Speed":
				trigger = Triggers.DRILLING;
				break;
			case "Drilling Heat":
				trigger = Triggers.DRILLING;
				break;
			}

		} else {
			Boolean value = null;
			value = (Boolean) tmpData.getValue();

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
		}
		// System.out.println("Trigger Value: " + trigger);
		fsm.fire(trigger);

	}

}
