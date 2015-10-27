package org.neos.jms.mq;

import java.util.Timer;
import java.util.TimerTask;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class ConsumerPropertiesAsynchronous2  implements MessageListener, ExceptionListener{


	private String mensaje;
    @SuppressWarnings("unused")
	private String clientId;
    private QueueConnection connection;
    private QueueSession session;;
    
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
        
        
        // create a queue receiver
        QueueReceiver queueReceiver = session.createReceiver(queue);
        ConsumerPropertiesAsynchronous2 asyncReceiver = new ConsumerPropertiesAsynchronous2();
        queueReceiver.setMessageListener(asyncReceiver);
        
        
        connection.setExceptionListener(asyncReceiver);
        
        
        // start the connection in order to receive messages
        connection.start();
    }


	@Override
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage) message;
	      
	     try {
			System.out.println("received: " + msg.getText());
			 this.mensaje= msg.getText();
		} catch (JMSException e) {
		
			e.printStackTrace();
		}  

	}

	public void closeConnection() throws JMSException {
		connection.close();
		System.out.println("SE HA CERRADO LA CONEXION....");
	}

	@Override
	public void onException(JMSException exception) {
		System.err.println("an error occurred: " + exception);

	}
	
	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}



		public static void main(String args[]) {
	    	ConsumerPropertiesAsynchronous2 consumer = new ConsumerPropertiesAsynchronous2();
	    	try {
				consumer.create("camus", "queue1");
			
				
				TimerTask task = new TimerTask() {
					  @Override
					  public void run() {
							String message;
							message = consumer.getMensaje();
							if (message != null) {
								if (!message.equals("")) {
									System.out.println(message);
									System.out.println("----------------------------");
								}
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
