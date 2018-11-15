package gov.gtas.queues;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

@Component
public class JMSConfiguration {

    @Value("${aws.connection}")
    private String AWS_DEFAULT_BROKER_URL;

    @Value("${mq.connection}")
    private String DEFAULT_BROKER_URL;

    @Value("${region.aws}")
    private String awsRegion;

    @Value("${mq.redis.queue.inbound}")
    private String OUTBOUND_QUEUE;

    @Bean(name = "awsMqJmsBean")
    public ConnectionFactory awsConnectionFactory() {
        ProviderConfiguration providerConfiguration = new ProviderConfiguration();
        AmazonSQS amazonSQSClient = AmazonSQSClientBuilder
                .standard()
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(AWS_DEFAULT_BROKER_URL, awsRegion))
                .build();
        return new SQSConnectionFactory(providerConfiguration, amazonSQSClient);
    }

    @Bean(name = "activeMqJmsBean")
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
        return connectionFactory;
    }

    @Bean
    public JmsTemplate jmsTemplateFile() {
        JmsTemplate jmsTemplate = new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setDefaultDestinationName(OUTBOUND_QUEUE);
        return jmsTemplate;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        return new CachingConnectionFactory(connectionFactory());
    }

    @Bean
    public JmsTemplate jmsTemplateJason() {
        return new JmsTemplate(cachingConnectionFactory());
    }

}
