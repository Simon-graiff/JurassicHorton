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
		TextMessage tmpMessage = null;
		ERPData tmpData;
		if (arg0 instanceof TextMessage) {
			tmpMessage = (TextMessage) arg0;
		} else {
			_log.warn("Unknown format, marshalling aborted.");
			return;
		}

		try {
			StringReader sReader = new StringReader(tmpMessage.getText());
			tmpData = (ERPData) _unmarshaller.unmarshal(sReader);
			System.out.println("********** START OF "+tmpData.getOrderNumber()+ " *******************");
			WorkPiece tmpWorkPiece = new WorkPiece(tmpData);
			WorkPieceList.list.add(tmpWorkPiece);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// Do something with the erp data!
	}
}
