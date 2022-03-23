package tech.gklijs.emitter.configuration;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.Serializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.gklijs.api.bank.event.MoneyCreditedEvent;
import tech.gklijs.api.bank.event.MoneyDebitedEvent;

import java.util.Optional;

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

    private Optional<String> resolver(EventMessage<?> message) {
        var type = message.getPayloadType();
        if (type.isAssignableFrom(MoneyCreditedEvent.class)
                || type.isAssignableFrom(MoneyDebitedEvent.class)) {
            return Optional.of("bank-axon-events");
        } else {
            return Optional.empty();
        }
    }

    @Bean
    public KafkaPublisher<String, byte[]> kafkaPublisher(ProducerFactory<String, byte[]> producerFactory,
                                                         Serializer serializer) {
        return KafkaPublisher.<String, byte[]>builder()
                             .topicResolver(this::resolver)
                             .serializer(serializer)
                             .producerFactory(producerFactory)
                             .build();
    }
}
