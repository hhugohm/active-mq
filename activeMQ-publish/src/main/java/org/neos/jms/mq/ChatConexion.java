package org.neos.jms.mq;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.InitialContext;

public class ChatConexion implements MessageListener {
	private TopicSession pubSession;
	private TopicPublisher publisher;
	private TopicConnection connection;
	private String username;
	private String messageText;
	
	/* Constructor used to Initialize Chat */
	public ChatConexion(String topicFactory, String topicName, String username)
			throws Exception {
		// Obtain a JNDI connection using the jndi.properties file
		InitialContext ctx = new InitialContext();
		// Look up a JMS connection factory and create the connection
		//TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup(topicFactory);
		TopicConnectionFactory conFactory = (TopicConnectionFactory) ctx.lookup(topicFactory);
		TopicConnection connection = conFactory.createTopicConnection();
		// Create two JMS session objects
		TopicSession pubSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		TopicSession subSession = connection.createTopicSession(false,Session.AUTO_ACKNOWLEDGE);
		// Look up a JMS topic
		Topic chatTopic = (Topic) ctx.lookup(topicName);
		// Create a JMS publisher and subscriber. The additional parameters
		// on the createSubscriber are a message selector (null) and a true
		// value for the noLocal flag indicating that messages produced from
		// this publisher should not be consumed by this publisher.
		TopicPublisher publisher = pubSession.createPublisher(chatTopic);
		TopicSubscriber subscriber = subSession.createSubscriber(chatTopic,
				null, true);
		// Set a JMS message listener
		subscriber.setMessageListener(this);
		// Intialize the Chat application variables
		this.connection = connection;
		this.pubSession = pubSession;
		this.publisher = publisher;
		this.username = username;
		// Start the JMS connection; allows messages to be delivered
		connection.start();
	}
	/* Receive Messages From Topic Subscriber */
	public void onMessage(Message message) {
		try {
			TextMessage textMessage = (TextMessage) message;
			System.out.println(textMessage.getText());
			this.messageText=textMessage.getText();
		} catch (JMSException jmse) {
			jmse.printStackTrace();
		}
		
	}
	/* Create and Send Message Using Publisher */
	public void writeMessage(String text) throws JMSException {
		TextMessage message = pubSession.createTextMessage();
		message.setText(username + ": " + text);
		publisher.publish(message);
	}

	/* Close the JMS Connection */
	public void close() throws JMSException {
		connection.close();
	}
	public String getMessageText() {
		return messageText;
	}
	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}
	

}
