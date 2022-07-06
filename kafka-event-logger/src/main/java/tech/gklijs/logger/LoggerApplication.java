package tech.gklijs.logger;

import org.axonframework.config.EventProcessingConfigurer;
import org.axonframework.extensions.kafka.eventhandling.consumer.streamable.StreamableKafkaMessageSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LoggerApplication {

    private static final String KAFKA_GROUP = "kafka-group";

    public static void main(String[] args) {
        SpringApplication.run(LoggerApplication.class, args);
    }

    @Autowired
    public void registerProcessor(
            EventProcessingConfigurer configurer,
            StreamableKafkaMessageSource<String, byte[]> streamableKafkaMessageSource){
        configurer.registerPooledStreamingEventProcessor(KAFKA_GROUP, c -> streamableKafkaMessageSource);
    }
}
