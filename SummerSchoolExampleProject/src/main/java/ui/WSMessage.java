package ui;

public class WSMessage {

	public String orderNumber;
	int customerNumber;
	int materialNumber;
	String itemName;
	String status;
	Object value;
	String specStatus;

	public WSMessage(String orderNumber, int customerNumber, int materialNumber, String itemName, String status,
			Object value) {
		this.orderNumber = orderNumber;
		this.customerNumber = customerNumber;
		this.materialNumber = materialNumber;
		this.itemName = itemName;
		this.status = status;
		this.value = value;
	}
	
	public WSMessage(String orderNumber, String specStatus){
		this.orderNumber = orderNumber;
		this.specStatus = specStatus;
	}
}
