package tech.gklijs.legacy_producer;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import tech.gklijs.legacy_producer.configuration.ProducerConfiguration;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class LegacyProducerApplication {

    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(0);
        try(KafkaProducer<String, CloudEvent> producer = new KafkaProducer<>(ProducerConfiguration.getProperties())){
            while (counter.get() < 180){
                producer.send(ProducerConfiguration.getProducerRecord());
                int i = counter.incrementAndGet();
                log.info("Just send event {} of 180", i);
                Thread.sleep(10_000L);
            }
        }catch (Exception e){
            log.warn("Encountered exception, will exit", e);
        }
    }
}
