package com.uchicago.jocelynz;

import java.util.List;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.Component;
import org.apache.camel.component.jms.JmsComponent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils  {
	public static Component createJmsComponent() {
		// connect to ActiveMQ JMS broker listening on localhost on port 61616
		ConnectionFactory connectionFactory = 
				createActiveMQConnectionFactory();
		return JmsComponent.jmsComponentAutoAcknowledge(connectionFactory);
	}
	
	//message broker
	public static ActiveMQConnectionFactory createActiveMQConnectionFactory() {
		return new ActiveMQConnectionFactory("tcp://localhost:61616");
	}

	public static void subscribeToStockData(StockDataSubscriptionRequest r) throws 
			JMSException, JsonProcessingException {
		ActiveMQConnectionFactory connectionFactory = Utils.createActiveMQConnectionFactory();
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Create a Session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create the destination (Topic or Queue)
		Destination destination = session.createQueue(Constants.DATA_REQUEST_QUEUE_NAME);

		// Create a MessageProducer from the Session to the Topic or Queue
		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// Create a messages
		TextMessage message = session.createTextMessage(
				new ObjectMapper().writerWithType(StockDataSubscriptionRequest.class).writeValueAsString(r));

		// Tell the producer to send the message
		producer.send(message);
		// Clean up
		session.close();
		connection.close();
	}

	public static String topicNamesToEndpoint(List<String> names) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.size(); i++) {
			sb.append("jms:topic:").append(names.get(i));
			if (i < names.size() - 1) {
				sb.append(',');
			}
		}
		return sb.toString();
	}
}
