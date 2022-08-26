package tech.gklijs.legacy_consumer.configuration;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.Message;
import org.springframework.stereotype.Component;
import tech.gklijs.api.bank.command.MoneyTransferCommand;
import tech.gklijs.legacy.api.bank.MoneyDebitedEvent;

import static tech.gklijs.legacy_consumer.LegacyConsumerApplication.LEGACY_CONSUMER_PROCESSOR_NAME;

@Slf4j
@Component
@ProcessingGroup(LEGACY_CONSUMER_PROCESSOR_NAME)
public class EventConsumer {

    private final CommandGateway commandGateway;

    public EventConsumer(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @EventHandler
    public void on(MoneyDebitedEvent event, Message<MoneyDebitedEvent> eventMessage){
        MoneyTransferCommand command = new MoneyTransferCommand(
                eventMessage.getIdentifier(),
                "",
                event.getAmount(),
                event.getFrom(),
                event.getIban(),
                event.getDescription(),
                ""
        );
        var result = commandGateway.sendAndWait(command, eventMessage.getMetaData());
        log.info("Result of sending the command: {}", result);
    }
}
