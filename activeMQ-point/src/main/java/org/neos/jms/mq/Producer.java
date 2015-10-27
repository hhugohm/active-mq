package org.neos.jms.mq;



import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


public class Producer {
	private String clientId;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    public void create(String clientId, String queueName) throws JMSException {
        this.clientId = clientId;

        // con esta sentencia no hace uso de el archivo de propiedades
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
      
        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        

        // create the Queue to which messages will be sent
        Queue queue = session.createQueue(queueName);

        // create a MessageProducer for sending messages
        messageProducer = session.createProducer(queue);
        
      
        connection.start();
    }

    public void closeConnection() throws JMSException {
        connection.close();
        System.out.println("SE HA CERRADO LA CONEXION....");
    }

    public void sendName(String firstName, String lastName) throws JMSException {
        String text = firstName + " " + lastName;

        // create a JMS TextMessage
        TextMessage textMessage = session.createTextMessage(text);

        // send the message to the queue destination
        messageProducer.send(textMessage);

        System.out.println(clientId + ": sent message with text='{}'"+text);
        closeConnection();
    }

    public static void main(String args[]) {
    	Producer producer = new Producer();
    	try {
			producer.create("hugo", "queue1");
			producer.sendName("HUGO", "HIDALGO");
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
    	
    }
}
