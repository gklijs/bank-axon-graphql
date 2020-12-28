package nl.openweb.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class MarkTransferCompletedCommand {
    @TargetAggregateIdentifier
    String transferId;
}
