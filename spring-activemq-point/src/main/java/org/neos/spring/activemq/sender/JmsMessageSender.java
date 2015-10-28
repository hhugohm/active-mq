package org.neos.spring.activemq.sender;


import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.activemq.command.ActiveMQQueue;
import org.neos.spring.activemq.domain.Mensaje;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

@Service
public class JmsMessageSender   implements IJmsMessageSender {

	@Autowired
	private JmsTemplate jmsTemplate;

	/**
	 * send Mensaje to default destination
	 * 
	 * @param text
	 */
	public void send(final Mensaje text) {

		this.jmsTemplate.send(new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {

				Message message = session.createObjectMessage(text);
				// set ReplyTo header of Message, pretty much like the concept
				// of email.
				message.setJMSReplyTo(new ActiveMQQueue("jms.queue"));
				System.out.println("Enviado: " + text.getMensaje());
				return message;
			}
		});
	}

	/**
	 * Simplify the send by using convertAndSend
	 * 
	 * @param text
	 */
	public void sendText(final String text) {
		this.jmsTemplate.convertAndSend(text);
	}

	public void sendMensaje(final Mensaje mensaje) {
		System.out.println("Enviado: " + mensaje.getMensaje());
		this.jmsTemplate.convertAndSend(mensaje);
	}

	/**
	 * Send text message to a specified destination
	 * 
	 * @param text
	 */
	public void send(final Destination dest, final String text) {

		this.jmsTemplate.send(dest, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message = session.createTextMessage(text);
				return message;
			}
		});
	}

}