package tech.gklijs.logger.configuration;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Component
@ProcessingGroup("kafka-group")
public class EventLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);
    private static final AtomicLong counter = new AtomicLong();
    private static final AtomicLong start = new AtomicLong();

    @EventHandler
    public void on(EventMessage<?> event){
        long current = counter.incrementAndGet();
        if (current == 100L){
            start.set(Instant.now().toEpochMilli());
        }
        if (current == 100100L){
            long now = Instant.now().toEpochMilli();
            LOGGER.info(String.format("Reading a 100.000 events took: %d milliseconds", now - start.get()));
        }
    }
}
