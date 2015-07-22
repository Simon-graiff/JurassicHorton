package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.scheduling.Trigger;

import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import stateless4j.PartStates;
import stateless4j.Triggers;
import ui.Server;
import ui.WSMessage;

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

	private ArrayList<Long> timestampList;
	private long[] duration;

	private long drillingStart = 0;
	private long drillingEnd = 0;
	private long millingStart = 0;
	private long millingEnd = 0;

	private static int size = 0;
	SpectralData specData = new SpectralData();

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

		timestampList = new ArrayList<Long>();
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
				.onEntry(this::saveToOPCDataItemList).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L1_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L2_TRUE)
				.ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L1_TRUE, PartStates.L1_OUT)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L1_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L2_FALSE, PartStates.L2_IN)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L2_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L2_TRUE, PartStates.L2_OUT)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L2_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.MILLING_OFF)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L3_FALSE, PartStates.L3_IN)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L3_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.MILLING_ON, PartStates.MILLING_ON)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.MILLING_ON).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.MILLING_OFF, PartStates.MILLING_OFF)
				.permitReentry(Triggers.MILLING).onEntry(this::handleMilling).onEntry(this::saveToOPCDataItemList)
				.onEntry(this::setMillingStart).onEntry(this::sendToUI);

		fsmc.configure(PartStates.MILLING_OFF).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L4_FALSE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L3_TRUE, PartStates.L3_OUT)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::setMillingEnd).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L3_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L4_FALSE, PartStates.L4_IN)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L4_IN).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_OFF).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.DRILLING_ON, PartStates.DRILLING_ON)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.DRILLING_ON).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.L4_TRUE)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE)
				.permit(Triggers.DRILLING_OFF, PartStates.DRILLING_OFF).permitReentry(Triggers.DRILLING)
				.onEntry(this::handleDrilling).onEntry(this::saveToOPCDataItemList).onEntry(this::setDrillingStart).onEntry(this::sendToUI);

		fsmc.configure(PartStates.DRILLING_OFF).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L5_FALSE).ignore(Triggers.L5_TRUE).permit(Triggers.L4_TRUE, PartStates.L4_OUT)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::setDrillingEnd).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L4_OUT).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_TRUE).permit(Triggers.L5_FALSE, PartStates.L5)
				.onEntry(this::saveToOPCDataItemList).onEntry(this::addTimestamp).onEntry(this::sendToUI);

		fsmc.configure(PartStates.L5).ignore(null).ignore(Triggers.L1_FALSE).ignore(Triggers.L1_TRUE)
				.ignore(Triggers.L2_TRUE).ignore(Triggers.L2_FALSE).ignore(Triggers.L3_FALSE)
				.ignore(Triggers.MILLING_OFF).ignore(Triggers.MILLING_ON).ignore(Triggers.L3_TRUE)
				.ignore(Triggers.L4_FALSE).ignore(Triggers.DRILLING_ON).ignore(Triggers.DRILLING_OFF)
				.ignore(Triggers.L4_TRUE).ignore(Triggers.L5_FALSE).permit(Triggers.L5_TRUE, PartStates.FINISHED)
				.onEntry(this::saveToOPCDataItemList).onExit(this::finish).onEntry(this::addTimestamp)
				.onExit(this::addTimestamp).onEntry(this::sendToUI);

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
		System.out.println(
				"****************** OUTPUT DATA FOR " + ERPData.getOrderNumber() + " ***********************\n");

		/*
		 * System.out.print("\n********\nSpeed:\n"); for (int i = 0; i <
		 * millingSpeedList.size(); i++) {
		 * System.out.print(millingSpeedList.get(i) + " "); }
		 * System.out.print("\n********\nHeat:\n"); for (int i = 0; i <
		 * millingHeatList.size(); i++) {
		 * System.out.print(millingHeatList.get(i) + " "); }
		 */
		System.out.println("AVG Milling heat: " + calcAvg(millingHeatList));
		System.out.println("AVG milling speed: " + calcAvg(millingSpeedList));
		System.out.println("AVG Drilling heat: " + calcAvg(drillingHeatList));
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

		long[] duration = calcTimes();

		System.out.println("Milling time " + calcMillingTime());
		System.out.println("Drilling time " + calcDrillingTime());

		System.out.println("\n\n\n\n\n");

		/*
		 * //UI update WSMessage message = new
		 * WSMessage(ERPData.getOrderNumber()); Gson x = new Gson(); String json
		 * = x.toJson(message); Server.getInstance().sendToAll(json);
		 * System.out.println("Sent to Server; "+json);
		 */

		/*
		 * SpectralData specData = getSpectralData(); while (getSpectralData()
		 * == null) { try { Thread.sleep(1000); } catch (InterruptedException e)
		 * { // TODO Auto-generated catch block e.printStackTrace(); } specData
		 * = getSpectralData(); }
		 */

		readFile();
		System.out.println("Bla bla bla " + specData.getOverallStatus());

		String textUri = "mongodb://admin:admin@ds047602.mongolab.com:47602/hortonmongodb";

		// Create MongoClientURI object from which you get MongoClient obj
		MongoClientURI uri = new MongoClientURI(textUri);

		// Connect to that uri
		MongoClient m = new MongoClient(uri);

		// get the database named sample
		DB d = m.getDB("hortonmongodb");

		// get the collection mycollection in sample
		DBCollection collection = d.getCollection("WorkPiece");

		// Now create a simple BasicDBObject
		BasicDBObject b = new BasicDBObject();
		b.put("orderNumber", ERPData.getOrderNumber());
		b.put("customerNumber", ERPData.getCustomerNumber());
		b.put("materialNumber", ERPData.getMaterialNumber());
		b.put("avgDrillingHeat", calcAvg(drillingHeatList));
		System.out.println(calcAvg(drillingHeatList));
		
		b.put("avgDrillingSpeed", calcSpeedAvg(drillingSpeedList));
		b.put("avgMillingHeat", calcAvg(millingHeatList));
		b.put("avgMillingSpeed", calcSpeedAvg(millingSpeedList));
		b.put("maxDrillingHeat", getPeak(drillingHeatList));
		b.put("maxDrillingSpeed", getPeak(drillingSpeedList));
		b.put("maxMillingHeat", getPeak(millingHeatList));
		b.put("maxMillingSpeed", getPeak(millingSpeedList));
		b.put("totalTime", getTotalTime(OPCDataItemList));
		for (int i = 0; i < duration.length; i++) {
			System.out.println("Duration from L " + i + " :" + duration[i]);
			b.put("L" + (i + 1) + "_duration", duration[i]);
		}
		b.put("millingDuration", calcMillingTime());
		b.put("drillingDuration", calcDrillingTime());
		b.put("timestamp", System.currentTimeMillis());

		b.put("status", specData.getOverallStatus());
		b.put("specDuration", getSpecTime(specData));
		b.put("a1", specData.getA1());
		b.put("a2", specData.getA2());
		b.put("b1", specData.getB1());
		b.put("b2", specData.getB2());
		b.put("em1", specData.getEm1());
		b.put("em2", specData.getEm2());
		b.put("Ts_start", specData.getTs_start());
		b.put("Ts_stop", specData.getTs_stop());

		// Insert that object into the collection
		collection.insert(b);
		m.close();

	}

	private void sendToUI() {
		// UI update
		WSMessage message = new WSMessage(ERPData.getOrderNumber(), ERPData.getCustomerNumber(),
				ERPData.getMaterialNumber(), tmpData.getItemName(), tmpData.getStatus(), tmpData.getValue());
		Gson x = new Gson();
		String json = x.toJson(message);
		Server.getInstance().sendToAll(json);
		System.out.println("Sent to Server; " + json);
	}

	private long getSpecTime(SpectralData specData) {
		System.out.println("Times: " + specData.getTs_start() + specData.getTs_stop());
		return specData.getTs_stop() - specData.getTs_start();
	}

	public SpectralData readFile() {
		try {
			Files.walk(Paths.get(".\\logs\\")).forEach(filePath -> {

				if (Files.isRegularFile(filePath)) {
					size++;
					Path newFile = filePath;
					System.out.println(newFile.getFileName());
					System.out.println(newFile.toString());

					try (BufferedReader br = new BufferedReader(new FileReader(filePath.toString()))) {
						StringBuilder sb = new StringBuilder();
						String line = br.readLine();

						while (line != null) {
							sb.append(line);
							sb.append(System.lineSeparator());
							line = br.readLine();
						}

						br.close();
						// System.out.println(sb.toString());
						Gson gson = new Gson();
						specData = gson.fromJson(sb.toString(), SpectralData.class);
						// System.out.println(specData.toString());
						System.out.println(specData.getOverallStatus());

						// Files.delete(filePath);

						String testPath = (".\\logs\\" + newFile.getFileName());
						Path path = Paths.get(testPath);
						Files.delete(path);

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (size == 0) {
			System.out.println("No file found");
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			readFile();
		} else {
			return specData;
		}
		return null;
	}

	private void setDrillingEnd() {
		drillingEnd = tmpData.getTimestamp();
	}

	private void setDrillingStart() {
		if (drillingStart == 0) {
			drillingStart = tmpData.getTimestamp();
		}
	}

	private long calcDrillingTime() {
		return drillingEnd - drillingStart;
	}

	private void setMillingStart() {
		if (millingStart == 0) {
			millingStart = tmpData.getTimestamp();
		}
	}

	private void setMillingEnd() {
		millingEnd = tmpData.getTimestamp();
	}

	private long calcMillingTime() {
		return millingEnd - millingStart;
	}

	private long[] calcTimes() {
		long[] duration = new long[5];
		int j = 0;
		for (int i = 0; i < timestampList.size(); i = i + 2) {
			duration[j] = timestampList.get(i + 1) - timestampList.get(i);
			j++;
		}
		return duration;
	}

	private void addTimestamp() {
		timestampList.add(tmpData.getTimestamp());
	}

	public void saveToOPCDataItemList() {
		OPCDataItemList.add(tmpData);
	}

	public void handleMilling() {
		// System.out.println("*************" + tmpData.getItemName());
		if (tmpData.getItemName().equals("Milling Speed")) {
			millingSpeedList.add((int) tmpData.getValue());
			// System.out.println("Milling Speed " + tmpData.getValue());
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
	
	public static int calcSpeedAvg(ArrayList valueList)
	{
		int sum = 0;
		int counter = 0;
		
		for(int i = 0; i < valueList.size()-1; i++)
		{
			System.out.println(i +" "+valueList.get(i));
			sum = sum + (int) valueList.get(i);
			counter++;
		}
		
		System.out.println(sum+" "+counter+" "+(sum/counter));
		return (sum/counter);
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
		return (sum / counter);
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
