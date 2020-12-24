package nl.openweb.graphql_endpoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.openweb.api.bank.command.MoneyTransferCommand;
import nl.openweb.api.bank.query.TransferResult;
import nl.openweb.api.bank.query.TransferResultQuery;
import nl.openweb.graphql_endpoint.model.MoneyTransferResult;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoneyTransferService {

    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;

    public Publisher<MoneyTransferResult> transfer(long amount, String descr, String from, String to, String
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
                .flatMapMany(x -> queryGateway.subscriptionQueryMany(new TransferResultQuery(uuid), TransferResult.class))
                .filter(r -> r.getState() != TransferResult.TransferState.BEING_PROCESSED)
                .map(r -> new MoneyTransferResult(
                        r.getReason(),
                        r.getState() == TransferResult.TransferState.COMPLETED,
                        uuid
                ));
    }
}
