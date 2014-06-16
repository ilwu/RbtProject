package com.rbt.mq;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

public class JMSCorrelationIDTest {

	private Queue queue;
	private Session session;

	public JMSCorrelationIDTest() throws JMSException{
		ActiveMQConnectionFactory factory =
				new ActiveMQConnectionFactory(
						ActiveMQConnection.DEFAULT_USER,
						ActiveMQConnection.DEFAULT_PASSWORD,
						"tcp://127.0.0.1:61616");

		Connection connection = factory.createConnection();
		connection.start();

		this.queue = new ActiveMQQueue("testQueue");
		this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		setupConsumer("ConsumerA");
		setupConsumer("ConsumerB");
		setupConsumer("ConsumerC");

		setupProducer("ProducerA", "ConsumerA");
		setupProducer("ProducerB", "ConsumerB");
		setupProducer("ProducerC", "ConsumerC");

		connection.close();
	}

	private void setupConsumer(final String name) throws JMSException {
		//創建一個消費者，它隻接受屬於它自己的消息
		MessageConsumer consumer = this.session.createConsumer(this.queue, "receiver='" + name + "'");
		consumer.setMessageListener(new MessageListener(){
			public void onMessage(Message m) {
				try {
					MessageProducer producer = JMSCorrelationIDTest.this.session.createProducer(JMSCorrelationIDTest.this.queue);
					System.out.println(name + " get:" + ((TextMessage)m).getText());
					//回覆一個消息
					Message replyMessage = JMSCorrelationIDTest.this.session.createTextMessage("Reply from " + name);
					//設置JMSCorrelationID為剛才收到的消息的ID
					replyMessage.setJMSCorrelationID(m.getJMSMessageID());
					producer.send(replyMessage);
				} catch (JMSException e) { }
			}
		});
	}

	private void setupProducer(final String name, String consumerName) throws JMSException {
		MessageProducer producer = this.session.createProducer(this.queue);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		//創建一個消息，並設置一個屬性receiver，為消費者的名字。
		Message message = this.session.createTextMessage("Message from " + name);
		message.setStringProperty("receiver", consumerName);
		producer.send(message);

		//等待回覆的消息
		MessageConsumer replyConsumer = this.session.createConsumer(this.queue, "JMSCorrelationID='" + message.getJMSMessageID() + "'");
		replyConsumer.setMessageListener(new MessageListener(){
			public void onMessage(Message m) {
				try {
					System.out.println(name + " get reply:" + ((TextMessage)m).getText());
				} catch (JMSException e) { }
			}
		});
	}

	public static void main(String[] args) throws Exception {
		new JMSCorrelationIDTest ();
	}
}
