package tech.gklijs.api.bank.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

@Value
public class MarkTransferFailedCommand {
    @TargetAggregateIdentifier
    String transferId;
    String reason;
}
