package org.neos.spring.activemq.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.neos.spring.activemq.domain.Mensaje;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.stereotype.Service;

/**
 * Listener Implement Spring SessionAwareMessageListener Interface
 *
 */
@Service
public class JmsMessageListener implements SessionAwareMessageListener<Message> {

	@Override
	public void onMessage(Message message, Session session) throws JMSException {

		if (message instanceof TextMessage) {
			TextMessage mensaje = (TextMessage) message;
			System.out.println("Recibe: " + mensaje.getText());
		} else {
			ObjectMessage objectMessage = (ObjectMessage) message;
			if (objectMessage.getObject() instanceof Mensaje) {
				Mensaje mensaje = (Mensaje) objectMessage.getObject();
				System.out.println("Recibe: " + mensaje.getMensaje());
			}
		}

	}

}