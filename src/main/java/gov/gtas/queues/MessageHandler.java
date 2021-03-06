package gov.gtas.queues;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;


@Component
public class MessageHandler {


    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    @Value("${aws.connection}")
    private String AWS_DEFAULT_BROKER_URL;

    @Value("${mq.connection}")
    private String DEFAULT_BROKER_URL;

    private static final String FORWARDING_TO_THIS_QUEUE = "GTAS_INBOUND_Q_REDIS";

    private final
    JmsTemplate jmsTemplateFile;

    @Autowired
    public MessageHandler(JmsTemplate jmsTemplateFile) {
        this.jmsTemplateFile = jmsTemplateFile;
    }

    @JmsListener(destination = FORWARDING_TO_THIS_QUEUE, containerFactory = "awsContainerFactory")
    public void receiveMessage(final Message<?> message) {
        logger.info("++++++++Message Received++++++++++++");
        try {
            jmsTemplateFile.convertAndSend(FORWARDING_TO_THIS_QUEUE, message);
        } catch (Exception ex) {
            logger.error("Error forwarding message", ex);
        }
        logger.info("+++++++++++++++++++++++++++++");
    }
}
