package activemq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QueueConnection {

	/**
	 * connection factory 
	 */
	private ConnectionFactory _conFac; 
	
	/**
	 * Queue connection
	 */
	private Connection _con;
	
	/**
	 * Log instance 
	 */
	private Logger _log; 
	
	
	/**
	 * Default Constructor 
	 */
	public QueueConnection() {
		_log = LogManager.getLogger(QueueConnection.class); 
		_log.debug("Creating queue connection ...");
		
		//Create a queue connection via JMS 
		_conFac = new ActiveMQConnectionFactory("tcp://localhost:61616");
		
		try {
			_con = _conFac.createConnection();
		} catch (JMSException e) {
			_log.error(e.getMessage());
		}
		
		//Listen for incoming items 
		listen();
	}

	/**
	 * Listen for incoming items 
	 */
	private void listen()  {
		_log.debug("Listening to queue.");
		
		Session session = null;
		Destination erpDestination = null; 
		try {
			//Create a session
			session = _con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			//Define the topic to listen on
			erpDestination = session.createTopic("m_orders");
		
		} catch (JMSException e) {
			e.printStackTrace();
		} 
		
		//Create a message consumer using a custom ERP Data Listener 
		MessageConsumer consumerERP; 
		try {
			
			consumerERP = session.createConsumer(erpDestination); 
			consumerERP.setMessageListener(new ERPDataListener());
		
			//Start listening on the connection.
			_con.start();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
		_log.debug("Listener active.");
	}	
}
