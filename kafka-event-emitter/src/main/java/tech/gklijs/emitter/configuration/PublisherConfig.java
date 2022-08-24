package tech.gklijs.emitter.configuration;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventhandling.PropagatingErrorHandler;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.cloudevent.CloudEventKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.producer.ConfirmationMode;
import org.axonframework.extensions.kafka.eventhandling.producer.DefaultProducerFactory;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.KafkaPublisher;
import org.axonframework.extensions.kafka.eventhandling.producer.ProducerFactory;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.gklijs.api.bank.event.MoneyCreditedEvent;
import tech.gklijs.api.bank.event.MoneyDebitedEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.axonframework.extensions.kafka.eventhandling.producer.KafkaEventPublisher.DEFAULT_PROCESSING_GROUP;

@Slf4j
@Configuration
public class PublisherConfig {

    @Bean
    ProducerFactory<String, CloudEvent> producerFactory(KafkaProperties kafkaProperties) {
        return new DefaultProducerFactory.Builder<String, CloudEvent>()
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
    public KafkaPublisher<String, CloudEvent> kafkaPublisher(
            ProducerFactory<String, CloudEvent> producerFactory,
            Serializer serializer,
            KafkaMessageConverter<String, CloudEvent> converter) {
        return KafkaPublisher.<String, CloudEvent>builder()
                             .topicResolver(this::resolver)
                             .serializer(serializer)
                             .producerFactory(producerFactory)
                             .messageConverter(converter)
                             .build();
    }

    @Bean
    public KafkaMessageConverter<String, CloudEvent> kafkaMessageConverter(
            @Qualifier("eventSerializer") Serializer eventSerializer,
            org.axonframework.config.Configuration configuration
    ) {
        return CloudEventKafkaMessageConverter
                .builder()
                .serializer(eventSerializer)
                .upcasterChain(configuration.upcasterChain()
                                       != null ? configuration.upcasterChain() : new EventUpcasterChain())
                .build();
    }

    @Bean
    public KafkaEventPublisher<String, CloudEvent> kafkaEventPublisher(
            KafkaPublisher<String, CloudEvent> kafkaPublisher,
            EventProcessingConfigurer eventProcessingConfigurer) {
        KafkaEventPublisher<String, CloudEvent> kafkaEventPublisher =
                KafkaEventPublisher.<String, CloudEvent>builder().kafkaPublisher(kafkaPublisher).build();

        eventProcessingConfigurer.registerEventHandler(configuration -> kafkaEventPublisher)
                                 .registerListenerInvocationErrorHandler(
                                         DEFAULT_PROCESSING_GROUP, configuration -> PropagatingErrorHandler.instance()
                                 )
                                 .assignHandlerTypesMatching(
                                         DEFAULT_PROCESSING_GROUP,
                                         clazz -> clazz.isAssignableFrom(KafkaEventPublisher.class)
                                 );
        eventProcessingConfigurer.registerTrackingEventProcessor(DEFAULT_PROCESSING_GROUP);
        return kafkaEventPublisher;
    }
}
