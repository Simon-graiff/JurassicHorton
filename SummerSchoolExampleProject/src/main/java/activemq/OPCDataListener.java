package activemq;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import model.ERPData;
import model.OPCDataItem;
import model.WorkPiece;
import model.WorkPieceList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The listener class takes new messages and unmarshalls them to Java Objects.
 * 
 * @author julian
 *
 */
public class OPCDataListener implements MessageListener {

	private Logger _log = LogManager.getLogger(OPCDataListener.class);

	private JAXBContext _ctx;
	
	private Unmarshaller _unmarshaller; 
	
	/*private ArrayList<WorkPiece> workpieces;*/ 

	/**
	 * Default Constructor
	 */
	public OPCDataListener()  {
		try {
			_ctx = JAXBContext.newInstance(OPCDataItem.class);
			_unmarshaller = _ctx.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		_log.debug("New OPC data listener created.");
	}

	@Override
	public void onMessage(Message arg0) {
		_log.debug("New OPC message arrived!");
		
		TextMessage tmpMessage= null; 
		OPCDataItem tmpData; 
		if (arg0 instanceof TextMessage) {
			tmpMessage = (TextMessage)arg0; 
		} else {
			_log.warn("Unknown format, marshalling aborted.");
			return;
		}
		
		try {
			_log.debug(tmpMessage.getText());
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		
		System.out.println("Jetzt unmarshaling OPC");
		
		try{	
			System.out.println("in try catch OPC");
		StringReader sReader = new StringReader(tmpMessage.getText()); 
		tmpData = (OPCDataItem) _unmarshaller.unmarshal(sReader);
		_log.debug("Object created: " + tmpData.toString());
		System.out.println(tmpData.getItemName());
		
		//TODO create Workpieces FSM and add it to list
		//Workpieces.list.add(tmpData)
		
		/*for (int i=0;i<workpieces.size();i++) {
			workpieces.get(i).getFsm().fire(trigger);
		}*/
		
		
		}
		catch(Exception fuckYou)
		{
			fuckYou.printStackTrace();
		}
		
	}
}
