package tech.gklijs.legacy_consumer.configuration;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.KafkaProperties;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.cloudevent.CloudEventKafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.DefaultConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.serialization.Serializer;
import org.axonframework.serialization.upcasting.event.EventUpcasterChain;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.gklijs.legacy.api.bank.TopicConfiguration;

import java.util.Collections;

@Slf4j
@Configuration
public class FetcherConfig {

    @Bean("axonKafkaConsumerFactory")
    public ConsumerFactory<String, CloudEvent> kafkaConsumerFactory(KafkaProperties properties) {
        return new DefaultConsumerFactory<>(properties.buildConsumerProperties());
    }

    @Bean
    public SubscribableKafkaMessageSource<String, CloudEvent> streamableKafkaMessageSource(
            @Qualifier("eventSerializer") Serializer eventSerializer,
            ConsumerFactory<String, CloudEvent> kafkaConsumerFactory,
            Fetcher<String, CloudEvent, EventMessage<?>> kafkaFetcher,
            KafkaMessageConverter<String, CloudEvent> kafkaMessageConverter
    ) {
        return SubscribableKafkaMessageSource
                .<String, CloudEvent>builder()
                .topics(Collections.singletonList(TopicConfiguration.getTopic()))
                .groupId("axon_calling_consumer")
                .serializer(eventSerializer)
                .consumerFactory(kafkaConsumerFactory)
                .fetcher(kafkaFetcher)
                .messageConverter(kafkaMessageConverter)
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
}
