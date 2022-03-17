package tech.gklijs.graphql_endpoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.springframework.stereotype.Component;
import tech.gklijs.api.bank.command.MoneyTransferCommand;
import tech.gklijs.api.bank.query.TransferResult;
import tech.gklijs.api.bank.query.TransferResultQuery;
import tech.gklijs.graphql_endpoint.model.MoneyTransferResult;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyTransferService {

    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;

    public MoneyTransferResult transfer(long amount, String descr, String from, String to, String
            token, String username, String uuid) {
        MoneyTransferCommand command = new MoneyTransferCommand(
                uuid,
                token,
                amount,
                from,
                to,
                descr,
                username
        );
        return commandGateway.send(command)
                             .flatMapMany(x -> queryGateway.subscriptionQuery(new TransferResultQuery(uuid),
                                                                              ResponseTypes.instanceOf(TransferResult.class)))
                             .filter(r -> r.getState() == TransferResult.TransferState.COMPLETED
                                     || r.getState() == TransferResult.TransferState.FAILED)
                             .map(r -> new MoneyTransferResult(
                                     r.getReason(),
                                     r.getState() == TransferResult.TransferState.COMPLETED,
                                     uuid
                             ))
                             .blockFirst(Duration.ofSeconds(10L));
    }
}
