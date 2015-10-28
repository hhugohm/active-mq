package org.neos.spring.activemq.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


import org.springframework.stereotype.Service;




/**
 * Listener Implement Spring SessionAwareMessageListener Interface
 *
 */
@Service
public class JmsMessageListener1 implements MessageListener{ 

	@Override
	public void onMessage(Message message) {
		TextMessage msg = (TextMessage) message;
	      
	     try {
			System.out.println("recibido: " + msg.getText());
	
		} catch (JMSException e) {
		
			e.printStackTrace();
		}  
		
	}

	
}