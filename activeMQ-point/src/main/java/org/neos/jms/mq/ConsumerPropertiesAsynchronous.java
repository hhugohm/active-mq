package org.neos.jms.mq;

import java.util.Timer;
import java.util.TimerTask;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ConsumerPropertiesAsynchronous {


    private static String NO_GREETING = "no greeting";
    private String clientId;
    private QueueConnection connection;
    private QueueSession session;
    private MessageConsumer messageConsumer;
    
    public void create(String clientId, String queueName) throws JMSException, NamingException {
        this.clientId = clientId;

        // con esta sentencia se hace uso de el archivo de propiedades
        InitialContext ctx = new InitialContext();
      
        QueueConnectionFactory queueConnectionFactory = (QueueConnectionFactory)ctx.lookup("queueConnectionFactory");
        

        connection = queueConnectionFactory.createQueueConnection();
        connection.setClientID(clientId);
        session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        
        this.connection.start();
        Queue queue = (Queue)ctx.lookup(queueName);

        // create a MessageConsumer for receiving messages
        messageConsumer = session.createConsumer(queue);

        // start the connection in order to receive messages
        connection.start();
    }

    public void closeConnection() throws JMSException {
        connection.close();
        System.out.println("SE HA CERRADO LA CONEXION....");
    }

    public String getGreeting(boolean acknowledge)
            throws JMSException {

        String greeting = NO_GREETING;

        // read a message from the queue destination
        Message message = messageConsumer.receive();
        //messageConsumer.re

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
	    	ConsumerPropertiesAsynchronous consumer = new ConsumerPropertiesAsynchronous();
	    	try {
				consumer.create("camus", "queue1");
			
				
				TimerTask task = new TimerTask() {
					  @Override
					  public void run() {
							String message;
							try {
								message = consumer.getGreeting(true);
								if (message != null) {
									if (!message.equals("")) {
										System.out.println(message);
										System.out.println("----------------------------");
									}
								}
							} catch (JMSException e) {
								e.printStackTrace();
							}
							
					  }
					};
				Timer timer = new Timer();
				timer.schedule(task, 0l, 1000l); 
				//consumer.closeConnection();
			} catch (JMSException |NamingException e) {
			
				e.printStackTrace();
			}
	    	
	    }
}
