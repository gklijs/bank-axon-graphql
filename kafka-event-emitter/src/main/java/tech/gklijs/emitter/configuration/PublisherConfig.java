package tech.gklijs.emitter.configuration;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.cloudevent.CloudEventKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.gklijs.api.bank.event.MoneyCreditedEvent;
import tech.gklijs.api.bank.event.MoneyDebitedEvent;

import java.net.URI;
import java.util.Optional;

@Slf4j
@Configuration
public class PublisherConfig {

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
    @SuppressWarnings("unchecked")
    public KafkaPublisher<String, CloudEvent> kafkaPublisher(
            ProducerFactory<?, ?> producerFactory,
            @Qualifier("eventSerializer") Serializer eventSerializer,
            KafkaMessageConverter<String, CloudEvent> converter) {
        return KafkaPublisher.<String, CloudEvent>builder()
                             .topicResolver(this::resolver)
                             .serializer(eventSerializer)
                             .producerFactory((ProducerFactory<String, CloudEvent>) producerFactory)
                             .messageConverter(converter)
                             .build();
    }

    @Bean
    public KafkaMessageConverter<String, CloudEvent> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer,
            org.axonframework.config.Configuration configuration
    ) {
        URI gitUri = URI.create("https://github.com/gklijs/bank-axon-graphql");
        return CloudEventKafkaMessageConverter
                .builder()
                .serializer(eventSerializer)
                .upcasterChain(configuration.upcasterChain()
                                       != null ? configuration.upcasterChain() : new EventUpcasterChain())
                .sourceSupplier(m -> gitUri)
                .build();
    }
}
