package tech.gklijs.legacy_consumer;

import io.cloudevents.CloudEvent;
import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.consumer.subscribable.SubscribableKafkaMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LegacyConsumerApplication {

    public static final String LEGACY_CONSUMER_PROCESSOR_NAME = "legacy_consumer_processor";

    public static void main(String[] args) {
        SpringApplication.run(LegacyConsumerApplication.class, args);
    }

    @Autowired
    public void registerProcessor(
            EventProcessingConfigurer configurer,
            SubscribableKafkaMessageSource<String, CloudEvent> subscribableKafkaMessageSource
    ) {
        configurer
                .registerSubscribingEventProcessor(LEGACY_CONSUMER_PROCESSOR_NAME, c -> subscribableKafkaMessageSource);
        subscribableKafkaMessageSource.start();
    }
}
