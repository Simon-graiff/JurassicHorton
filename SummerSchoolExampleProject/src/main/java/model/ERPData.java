	package model;

import java.util.Date;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class represents an ERP data item arriving via JMS 
 * as XML message. 
 * @author julian.reichwald@dhbw-mannheim.de
 *
 */

@XmlRootElement
public class ERPData {
	
	    private String OrderNumber;

	    private int CustomerNumber;

	    private int MaterialNumber;

	    private Date TimeStamp;

	    public ERPData() {

	    }

		public String getOrderNumber() {
			return OrderNumber;
		}

		public void setOrderNumber(String orderNumber) {
			OrderNumber = orderNumber;
		}

		public int getCustomerNumber() {
			return CustomerNumber;
		}

		public void setCustomerNumber(int customerNumber) {
			CustomerNumber = customerNumber;
		}

		public int getMaterialNumber() {
			return MaterialNumber;
		}

		public void setMaterialNumber(int materialNumber) {
			MaterialNumber = materialNumber;
		}

		public Date getTimeStamp() {
			return TimeStamp;
		}

		public void setTimeStamp(Date timeStamp) {
			TimeStamp = timeStamp;
		}
	    
	    
}
