package org.neos.jms.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


public class Consumer {


    private static String NO_GREETING = "no greeting";
    private String clientId;
    private Connection connection;
    private Session session;
    private MessageConsumer messageConsumer;

    public void create(String clientId, String queueName) throws JMSException {
        this.clientId = clientId;

        // con esta sentencia no hace uso de el archivo de propiedades
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
       

        // create the Queue from which messages will be received
        Queue queue = session.createQueue(queueName);

        // create a MessageConsumer for receiving messages
        messageConsumer = session.createConsumer(queue);

        // start the connection in order to receive messages
        connection.start();
    }

    public void closeConnection() throws JMSException {
        connection.close();
        System.out.println("SE HA CERRADO LA CONEXION....");
    }

    public String getGreeting(int timeout, boolean acknowledge)
            throws JMSException {

        String greeting = NO_GREETING;

        // read a message from the queue destination
        Message message = messageConsumer.receive(timeout);

        // check if a message was received
        if (message != null) {
            // cast the message to the correct type
            TextMessage textMessage = (TextMessage) message;

            // retrieve the message content
            String text = textMessage.getText();
            System.out.println(clientId + ": received message with text='{}'" +text);

            if (acknowledge) {
                // acknowledge the successful processing of the message
                message.acknowledge();
                System.out.println(clientId + ": message acknowledged");
            } else {
            	System.out.println(clientId + ": message not acknowledged");
            }

            // create greeting
            greeting = "Hello " + text + "!";
        } else {
        	System.out.println(clientId + ": no message received");
        }

        System.out.println("greeting={}" + greeting);
        return greeting;
    }

	    
	    public static void main(String args[]) {
	    	Consumer consumer = new Consumer();
	    	try {
				consumer.create("camus", "queue1");
				String message = consumer.getGreeting(1000, true);
				System.out.println("MESSAGE: " +message);
				consumer.closeConnection();
			} catch (JMSException e) {
			
				e.printStackTrace();
			}
	    	
	    }
}
