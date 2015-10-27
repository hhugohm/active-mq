package org.neos.jms.mq;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class BrowserQue {

	@SuppressWarnings("unused")
	private String clientId;
	private QueueConnection connection;
	private QueueSession session;
	private Enumeration<?> enumMessge;

	public void create(String clientId, String queueName) throws JMSException, NamingException {
		this.clientId = clientId;

		// con esta sentencia se hace uso de el archivo de propiedades
		InitialContext ctx = new InitialContext();

		QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory) ctx.lookup("queueConnectionFactory");

		connection = queueConnectionFactory.createQueueConnection();
		connection.setClientID(clientId);
		session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		this.connection.start();
		Queue queue = (Queue) ctx.lookup(queueName);

		// create a queue browser
		QueueBrowser queueBrowser = session.createBrowser(queue);
		connection.start();
		// browse the messages
		this.enumMessge = queueBrowser.getEnumeration();

	}

	public void closeConnection() throws JMSException {
		connection.close();
		System.out.println("SE HA CERRADO LA CONEXION....");
	}

	public Enumeration<?> getEnumMessge() {
		return enumMessge;
	}

	public static void main(String args[]) {
		BrowserQue browser = new BrowserQue();
		try {
			browser.create("camus", "queue1");
			int numMsgs = 0;

			// count number of messages
			while (browser.getEnumMessge().hasMoreElements()) {
				@SuppressWarnings("unused")
				Message message = (Message) browser.enumMessge.nextElement();
				numMsgs++;
			}

			System.out.println("queue1" + " has " + numMsgs + " messages");

			browser.closeConnection();
		} catch (JMSException | NamingException e) {

			e.printStackTrace();
		}

	}

}
