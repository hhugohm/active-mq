package org.neos.spring.activemq.sender;

import javax.jms.Destination;

import org.neos.spring.activemq.domain.Mensaje;

public interface IJmsMessageSender {
	
	public void send(final Mensaje text);
	public void sendText(final String text);
	public void sendMensaje(final Mensaje mensaje);
	public void send(final Destination dest, final String text);

}
