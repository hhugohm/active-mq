package org.neos.spring.activemq.test;

import javax.jms.Queue;

import org.apache.activemq.command.ActiveMQQueue;
import org.neos.spring.activemq.domain.Mensaje;
import org.neos.spring.activemq.sender.IJmsMessageSender;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMensaje {

	public static void main(String[] args) {
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("app-context.xml");

	
		IJmsMessageSender jmsMessageSender = (IJmsMessageSender) ctx.getBean("jmsMessageSender");
		
		//creando un objeto para el envio
		Mensaje myMessage = new Mensaje("HELLO JMS...","jms.queue");

		//enviando el mensaje por cualquier metodo (ambos son validos)
		jmsMessageSender.send(myMessage);
		//jmsMessageSender.sendMensaje(myMessage);

		// send to a code specified destination
		Queue queue = new ActiveMQQueue("jms.queue");
		jmsMessageSender.send(queue, "hello Another Message");

		// close spring application context
		((ClassPathXmlApplicationContext) ctx).close();
	}

}
