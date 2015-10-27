package org.neos.jms.mq;



import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ProducerProperties {
	private String clientId;
    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    public void create(String clientId, String queueName) throws JMSException, NamingException {
        this.clientId = clientId;
        // con esta sentencia se hace uso de el archivo de propiedades
        InitialContext ctx = new InitialContext();
      
        ConnectionFactory connectionFactory =  (ConnectionFactory) ctx.lookup("queueConnectionFactory");
      
        // create a Connection
        connection = connectionFactory.createConnection();
        connection.setClientID(clientId);

        // create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        

        // create the Queue to which messages will be sent
       // Queue queue = session.createQueue(queueName);
        Queue queue = (Queue) ctx.lookup(queueName);

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

    public static void main(String args[]) throws NamingException {
    	ProducerProperties producer = new ProducerProperties();
    	try {
			producer.create("hugo", "queue1");
			producer.sendName("HUGO", "HIDALGO");
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
    	
    }
}
