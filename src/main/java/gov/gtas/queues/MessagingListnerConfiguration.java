package gov.gtas.queues;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;


@Configuration
@EnableJms
public class MessagingListnerConfiguration {

    private final
    ConnectionFactory mqConnectionFactory;

    private final
    ConnectionFactory awsConnectionFactory;

    @Autowired
    public MessagingListnerConfiguration(@Qualifier("activeMqJmsBean") ConnectionFactory mqConnectionFactory, @Qualifier("awsMqJmsBean") ConnectionFactory awsConnectionFactory) {
        this.mqConnectionFactory = mqConnectionFactory;
        this.awsConnectionFactory = awsConnectionFactory;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        return getDefaultJmsListenerContainerFactory(mqConnectionFactory);
    }

    private DefaultJmsListenerContainerFactory getDefaultJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("1-1");
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

    @Bean(name = "awsContainerFactory")
    public DefaultJmsListenerContainerFactory awsContainerFactory() {
        return getDefaultJmsListenerContainerFactory(awsConnectionFactory);
    }

}
