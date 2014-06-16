package com.rbt.mq;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class MQSendTest {

	public static void main(String[] args) throws JMSException {
		// ConnectionFactory ：連接工廠，JMS 用它創建連接
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD,
				"tcp://127.0.0.1:61616");
		// JMS 客戶端到JMS Provider 的連接
		Connection connection = connectionFactory.createConnection();
		connection.start();
		// Session： 一個發送或接收消息的線程
		Session session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
		// Destination ：消息的目的地;消息發送給誰.
		// 獲取session註意參數值my-queue是Query的名字
		Destination destination = session.createQueue("allen");
		// MessageProducer：消息生產者
		MessageProducer producer = session.createProducer(destination);
		// 設置不持久化
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		// 發送一條消息
		sendMsg(session, producer);
		session.commit();
		connection.close();
	}

	/**
	 * 在指定的會話上，通過指定的消息生產者發出一條消息
	 *
	 * @param session 消息會話
	 * @param producer 消息生產者
	 */
	public static void sendMsg(Session session, MessageProducer producer) throws JMSException {
		// 創建一條文本消息
		TextMessage message = session.createTextMessage("Hello ActiveMQ！2");
		message.setJMSCorrelationID("JMSCorrelationID");
		// 通過消息生產者發出消息
		producer.send(message);
		System.out.println("");
	}

}
