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
public class ERPDataListener implements MessageListener {

	private Logger _log = LogManager.getLogger(ERPDataListener.class);

	private JAXBContext _ctx;

	private Unmarshaller _unmarshaller;

	/* private ArrayList<WorkPiece> workpieces; */

	/**
	 * Default Constructor
	 */
	public ERPDataListener() {
		try {
			_ctx = JAXBContext.newInstance(ERPData.class);
			_unmarshaller = _ctx.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		_log.debug("New data listener created.");
	}

	@Override
	public void onMessage(Message arg0) {
		// _log.debug("New ERP message arrived!");

		TextMessage tmpMessage = null;
		ERPData tmpData;
		if (arg0 instanceof TextMessage) {
			tmpMessage = (TextMessage) arg0;
		} else {
			_log.warn("Unknown format, marshalling aborted.");
			return;
		}

		/*
		 try { _log.debug(tmpMessage.getText()); } catch (JMSException e) {
		 e.printStackTrace(); }
		 */

		try {
			StringReader sReader = new StringReader(tmpMessage.getText());
			tmpData = (ERPData) _unmarshaller.unmarshal(sReader);
			//_log.debug("Object created: " + tmpData.toString());
			//System.out.println(tmpData.getMaterialNumber());
System.out.println("********** START OF "+tmpData.getOrderNumber()+ " *******************");
			WorkPiece tmpWorkPiece = new WorkPiece(tmpData);
			WorkPieceList.list.add(tmpWorkPiece);

			/*
			 * for (int i=0;i<workpieces.size();i++) {
			 * workpieces.get(i).getFsm().fire(trigger); }
			 */

		} catch (Exception fuckYou) {
			fuckYou.printStackTrace();
		}
		// Do something with the erp data!

	}
}
