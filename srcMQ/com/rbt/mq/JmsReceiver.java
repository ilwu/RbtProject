package com.rbt.mq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 消息的消費者（接受者）
 *
 * @author leizhimin 2009-8-12 11:41:33
 */
public class JmsReceiver {
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
		// 獲取session註意參數值xingbo.xu-queue是一個服務器的queue，須在在ActiveMq的console配置
		Destination destination = session.createQueue("allen");
		

		// 消費者，消息接收者
		MessageConsumer consumer = session.createConsumer(destination);
		
		while (true) {
			TextMessage message = (TextMessage) consumer.receive(1000);
			if (null != message)
				System.out.println("收到消息：" + message.getText());
			else
				break;
		}
		session.close();
		connection.close();
	}
}
