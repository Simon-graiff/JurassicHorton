package esper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

import model.ERPData;

public class EsperTest {

	private Logger _log = LogManager.getLogger(EsperTest.class); 
	
	
	public EsperTest() {
		_log.debug("New espertech test created.");
	}
	
	
	public void run() {
		ERPData[] erpData = new ERPData[5]; 
		
		//Create demo values 
		for (int i=0;i<erpData.length;i++) {
			erpData[i] = new ERPData(); 
			erpData[i].setCustomerNumber(1000 + i);
		}
		_log.debug("Calculating an average value within a sliding time window (10 seconds)"); 
		
		EPServiceProvider epService = EPServiceProviderManager.getDefaultProvider();
		String expression = "SELECT avg(customerNumber) from model.ERPData.win:time(10 sec)"; 
		EPStatement statement = epService.getEPAdministrator().createEPL(expression);
		
		TestListener l = new TestListener(); 
		statement.addListener(l);
		
		for (int i=0;i<erpData.length;i++) {
			_log.debug("Firing event (value: " + erpData[i].getCustomerNumber()+")"); 
			epService.getEPRuntime().sendEvent(erpData[i]);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			_log.debug("Firing no new events for the next 10 seconds");
			_log.debug("The time window continues sliding ...");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	
	}
	
}
