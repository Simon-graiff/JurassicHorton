package jurhor.test;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnectionFactory;

//based on http://activemq.apache.org/hello-world.html

public class Test {

	public static void main(String[] args) throws Exception {

		thread(new Consumer(), false);
	}

	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}

	public static class Consumer implements Runnable, ExceptionListener {
		public void run() {
			try {

				// Create a ConnectionFactory
				String conStr = "tcp://localhost:61616";

				ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(conStr);

				// Create a Connection
				Connection connection = connectionFactory.createConnection();
				connection.start();

				connection.setExceptionListener(this);

				// Create a Session
				Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

				// Create the destination (Topic or Queue)
				Destination destination = session.createTopic("TEST.FOO");

				// Create a MessageConsumer from the Session to the Topic or
				// Queue
				MessageConsumer consumer = session.createConsumer(destination);

				

				while (true) {
					// Wait for a message
					Message message = consumer.receive(10000);

						if (message instanceof TextMessage) {
							TextMessage textMessage = (TextMessage) message;
							String text = textMessage.getText();
							System.out.println("Received: " + text);
						} else {
							System.out.println("Received: " + message);
						}
					}
			} catch (Exception e) {
				System.out.println("Caught: " + e);
				e.printStackTrace();
			}
		}

		public synchronized void onException(JMSException ex) {
			System.out.println("JMS Exception occured.  Shutting down client.");
		}
	}

}
