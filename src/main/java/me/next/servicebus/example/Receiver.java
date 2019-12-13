package me.next.servicebus.example;

import org.apache.qpid.jms.message.JmsTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class Receiver {

    private static final String QUEUE_NAME = "QUEUE_TEST";

    private final Logger logger = LoggerFactory.getLogger(Receiver.class);

    @JmsListener(destination = QUEUE_NAME, containerFactory = "jmsListenerContainerFactory")
    public void receiveMessageFromQueue(JmsTextMessage message) throws JMSException {
        logger.info("Received message from queue: {}", message.getText());
    }
}
