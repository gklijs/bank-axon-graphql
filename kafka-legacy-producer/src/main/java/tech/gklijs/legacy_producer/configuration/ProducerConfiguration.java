package tech.gklijs.legacy_producer.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.cloudevents.kafka.CloudEventSerializer;
import lombok.experimental.UtilityClass;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import tech.gklijs.legacy.api.bank.MoneyDebitedEvent;
import tech.gklijs.legacy.api.bank.TopicConfiguration;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

@UtilityClass
public class ProducerConfiguration {

    private final List<String> IBANS = List.of("NL55RABO9071327418",
                                               "NL34ABNA5753546897",
                                               "NL40INGB6359906732",
                                               "NL33RABO5082783165");
    private static final String COMPANY_IBAN = "NL09AXON0000000000";
    private final List<String> DESCRIPTIONS = List.of("consultancy", "license");
    private final Random RANDOM = new Random();
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final URI GIT_URI = URI.create("https://github.com/gklijs/bank-axon-graphql");

    public Properties getProperties() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "redpanda:29092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CloudEventSerializer.class);
        return properties;
    }

    private CloudEvent getCloudEvent() throws JsonProcessingException {
        MoneyDebitedEvent debitedEvent = new MoneyDebitedEvent(
                COMPANY_IBAN,
                200_000L + RANDOM.nextInt(100_000),
                IBANS.get(RANDOM.nextInt(4)),
                DESCRIPTIONS.get(RANDOM.nextInt(2))
        );
        String data = OBJECT_MAPPER.writeValueAsString(debitedEvent);
        return new CloudEventBuilder()
                .withId(UUID.randomUUID().toString())
                .withSource(GIT_URI)
                .withType(debitedEvent.getClass().getCanonicalName())
                .withData(data.getBytes())
                .withExtension("traceid", UUID.randomUUID().toString())
                .withExtension("correlationid", UUID.randomUUID().toString())
                .build();
    }

    public ProducerRecord<String,CloudEvent> getProducerRecord() throws JsonProcessingException {
        CloudEvent cloudEvent = getCloudEvent();
        return new ProducerRecord<>(TopicConfiguration.getTopic(), cloudEvent.getId(), cloudEvent);
    }
}
