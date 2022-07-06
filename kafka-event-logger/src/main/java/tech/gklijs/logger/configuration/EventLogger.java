package tech.gklijs.logger.configuration;

import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tech.gklijs.api.bank.event.MoneyCreditedEvent;
import tech.gklijs.api.bank.event.MoneyDebitedEvent;

@Component
@ProcessingGroup("kafka-group")
public class EventLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventLogger.class);

    @EventHandler
    public void on(MoneyCreditedEvent event){
        LOGGER.info(String.format("Money was credited, %d to %s", event.getAmount(), event.getIban()));
    }

    @EventHandler
    public void on(MoneyDebitedEvent event){
        LOGGER.info(String.format("Money was debited, %d from %s", event.getAmount(), event.getIban()));
    }

    @EventHandler
    public void on(EventMessage<?> event){
        LOGGER.info(String.format("Recieved event with name %s", event.getPayloadType().getName()));
    }
}
