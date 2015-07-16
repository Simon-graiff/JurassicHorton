package esper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class TestListener implements UpdateListener {

	private Logger _log = LogManager.getLogger(TestListener.class); 
	
	@Override
	public void update(EventBean[] arg0, EventBean[] arg1) {
		_log.debug("new event arrived.");
		EventBean event = arg0[0]; 
		_log.debug("Computed average: " + event.get("avg(customerNumber)"));
		
	}

}
