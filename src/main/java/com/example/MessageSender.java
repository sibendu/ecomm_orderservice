package com.example;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
 
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
 
public class MessageSender {
     
    //URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;
     
    // default broker URL is : tcp://localhost:61616"
    private static String subject = "ORDER_QUEUE"; // Queue Name.You can create any/many queue names as per your requirement. 
     
    public static void main(String[] args) throws JMSException { 
    	MessageSender.send("Test message", subject);
    }
    
    public static void send(String ord, String queue) throws JMSException {        
  	
    	if(System.getenv("ACTIVEMQ_BROKER_URL") != null) {
    		url = System.getenv("ACTIVEMQ_BROKER_URL");
    	}
    	
    	if(queue != null && !queue.equals("")) {
    		subject = queue;
    	}
    	
    	System.out.println("Active MQ = "+url+" ; subject = "+subject);
    	
        // Getting JMS connection from the server and starting it
    	ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
    	connectionFactory.setUserName("admin");
    	connectionFactory.setPassword("admin");
        
        Connection connection = connectionFactory.createConnection();
        connection.start();
         
        System.out.println("Connection to activemq established = "+ connection);    
        
        //Creating a non transactional session to send/receive JMS message.
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);  
        
       
        //Destination represents here our queue 'JCG_QUEUE' on the JMS server. 
        //The queue will be created automatically on the server.
        Destination destination = session.createQueue(subject); 
         
        // MessageProducer is used for sending messages to the queue.
        MessageProducer producer = session.createProducer(destination);
         
        // We will send a small text message saying 'Hello World!!!' 
        TextMessage message = session
                .createTextMessage(ord);
         
        // Here we are sending our message!
        producer.send(message);
         
        connection.close();
    }
}
