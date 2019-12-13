package me.next.servicebus.example;

import org.apache.qpid.jms.message.JmsTextMessage;
import org.apache.qpid.jms.provider.amqp.message.AmqpJmsTextMessageFacade;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.TextMessage;
import java.util.Date;
import java.util.HashMap;


@RestController
public class Sender {

    private static final Logger logger = LoggerFactory.getLogger(Sender.class);

    private static final String DESTINATION_QUEUE_NAME = "QUEUE_TEST";

    @Autowired
    private JmsTemplate jmsTemplate;

    // Case 1 - Simple Send Message
    @PostMapping("/sendMessageToQueue")
    public void sendMessageToQueue(@RequestBody String message) {
        logger.info("Sending message to queue");
        jmsTemplate.convertAndSend(DESTINATION_QUEUE_NAME, message);
    }

    // Case 2 - Delayed Message, it will throw an exception, because Azure Service Bus does not support delayed message yet.
    // If you need execute this endpoint, you need to remove/comment the qpid 0.3.0 on the gradle build file.
    @PostMapping("/sendDelayedMessageToQueue")
    public void sendDelayedMessageToQueue(@RequestBody String message) {
        logger.info("Sending delayed message to queue");
        jmsTemplate.setDeliveryDelay(1000);
        jmsTemplate.convertAndSend(DESTINATION_QUEUE_NAME, message);
    }

    // Case 3 - Delayed Message Workaround
    // If you need execute this endpoint, you need to add the qpid 0.3.0 on the gradle build file.
    @PostMapping("/sendDelayedMessageToQueueWorkaround")
    public void sendDelayedMessageToQueueWorkaround(@RequestBody String message) {
        logger.info("Sending delayed message to queue workaround");
        jmsTemplate.send(DESTINATION_QUEUE_NAME, setServiceBusDeliveryDelay(message, 60000));
    }

    private static MessageCreator setServiceBusDeliveryDelay(String message, long deliveryDelay) {
        return session -> {

            TextMessage textMessage = session.createTextMessage(message);
            ((JmsTextMessage) textMessage).setValidatePropertyNames(false);

            org.apache.qpid.proton.message.Message amqpMessage = ((AmqpJmsTextMessageFacade) ((JmsTextMessage) textMessage).getFacade()).getAmqpMessage();
            HashMap<String, Object> applicationPropertiesMap = new HashMap<>();
            applicationPropertiesMap.put("operation", "com.microsoft:schedule-message");
            applicationPropertiesMap.put("com.microsoft:server-timeout", 100000000);
            amqpMessage.setApplicationProperties(new ApplicationProperties(applicationPropertiesMap));

            Date delay = new Date(System.currentTimeMillis() + deliveryDelay);

            amqpMessage.getMessageAnnotations().getValue().put(Symbol.valueOf("x-opt-scheduled-enqueue-time"), delay);

            return textMessage;
        };
    }
}
