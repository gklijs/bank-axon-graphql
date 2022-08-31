package tech.gklijs.legacy_consumer;

import io.cloudevents.CloudEvent;
import org.axonframework.config.Configurer;
import org.axonframework.extensions.kafka.configuration.KafkaMessageSourceConfigurer;
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
            Configurer configurer,
            KafkaMessageSourceConfigurer kafkaMessageSourceConfigurer,
            SubscribableKafkaMessageSource<String, CloudEvent> subscribableKafkaMessageSource
    ) {
        kafkaMessageSourceConfigurer.configureSubscribableSource(c -> subscribableKafkaMessageSource);
        configurer.registerModule(kafkaMessageSourceConfigurer);
        configurer.eventProcessing()
                  .registerSubscribingEventProcessor(LEGACY_CONSUMER_PROCESSOR_NAME, c -> subscribableKafkaMessageSource);
    }
}
