package activemq;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import stateless4j.PartStates;
import stateless4j.Triggers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.Trigger;

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

	/* private ArrayList<WorkPiece> workpieces; */

	/**
	 * Default Constructor
	 */
	public OPCDataListener() {
		try {
			_ctx = JAXBContext.newInstance(OPCDataItem.class);
			_unmarshaller = _ctx.createUnmarshaller();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		// _log.debug("New OPC data listener created.");
	}

	@Override
	public void onMessage(Message arg0) {
		// _log.debug("New OPC message arrived!");

		TextMessage tmpMessage = null;
		OPCDataItem tmpData;
		if (arg0 instanceof TextMessage) {
			tmpMessage = (TextMessage) arg0;
		} else {
			_log.warn("Unknown format, marshalling aborted.");
			return;
		}
		/*
		 * try { _log.debug("OPCDataMessage: " + tmpMessage.getText()); } catch
		 * (JMSException e) { e.printStackTrace(); }
		 */
		try {
			StringReader sReader = new StringReader(tmpMessage.getText());
			tmpData = (OPCDataItem) _unmarshaller.unmarshal(sReader);
			// _log.debug("OPC Object created: " + tmpData.toString());

			List<WorkPiece> list = WorkPieceList.list;

			synchronized (list) {
				for (int i = 0; i < list.size(); i++) {
					//System.out.println("State before move: " + list.get(i).getFsm().getState());
					list.get(i).handleOPCDataItem(tmpData);

					if (list.size() > 0) {
						//System.out.println("State after move: " + list.get(i).getFsm().getState());
					} else {
					}
				}
			}

			// }
		} catch (Exception fuckYou) {
			fuckYou.printStackTrace();
		}

	}
}
