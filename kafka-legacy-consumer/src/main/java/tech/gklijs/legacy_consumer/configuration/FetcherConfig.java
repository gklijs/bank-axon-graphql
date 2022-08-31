package tech.gklijs.legacy_consumer.configuration;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
import org.axonframework.extensions.kafka.eventhandling.KafkaMessageConverter;
import org.axonframework.extensions.kafka.eventhandling.consumer.ConsumerFactory;
import org.axonframework.extensions.kafka.eventhandling.consumer.Fetcher;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.axonframework.serialization.Serializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tech.gklijs.legacy.api.bank.TopicConfiguration;

import java.util.Collections;

@Slf4j
@Configuration
public class FetcherConfig {

    @Bean
    KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer() {
        return new KafkaMessageSourceConfigurer();
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
}
