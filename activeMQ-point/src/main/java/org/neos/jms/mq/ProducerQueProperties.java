package org.neos.jms.mq;




import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ProducerQueProperties {
	private String clientId;
    private QueueConnection connection;
    private QueueSession session;
    private MessageProducer messageProducer;

    public void create(String clientId, String queueName) throws JMSException, NamingException {
        this.clientId = clientId;
        // con esta sentencia se hace uso de el archivo de propiedades
        this.clientId = clientId;
        InitialContext ctx = new InitialContext();
        
        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory)ctx.lookup("queueConnectionFactory");

        connection = queueConnectionFactory.createQueueConnection();
        connection.setClientID(clientId);
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        
        this.connection.start();
        Queue queue = (Queue)ctx.lookup(queueName);
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
    	ProducerQueProperties producer = new ProducerQueProperties();
    	try {
			producer.create("hugo", "queue1");
			producer.sendName("HUGO", "HIDALGO");
		} catch (JMSException e) {
			
			e.printStackTrace();
		}
    	
    }
}
