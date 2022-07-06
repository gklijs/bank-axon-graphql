package tech.gklijs.logger.configuration;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class PublisherConfig {

    @Bean
    ProducerFactory<String, byte[]> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultProducerFactory.Builder<String, byte[]>()
                .configuration(kafkaProperties.buildProducerProperties())
                .producerCacheSize(10_000)
                .confirmationMode(ConfirmationMode.WAIT_FOR_ACK)
                .build();
    }

    // Needed because autoconfig has it as @ConditionalOnClass
    @Bean
    public KafkaPublisher<String, byte[]> kafkaPublisher(ProducerFactory<String, byte[]> producerFactory) {
        return KafkaPublisher.<String, byte[]>builder()
                             .topic("bank-axon-events")
                             .producerFactory(producerFactory)
                             .build();
    }
}
