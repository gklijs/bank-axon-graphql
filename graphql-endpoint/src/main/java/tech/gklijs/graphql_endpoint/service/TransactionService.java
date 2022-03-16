package tech.gklijs.graphql_endpoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tech.gklijs.api.bank.query.AllLastTransactionsQuery;
import tech.gklijs.api.bank.query.LastTransactionQuery;
import tech.gklijs.api.bank.query.Transaction;
import tech.gklijs.api.bank.query.TransactionByIdQuery;
import tech.gklijs.api.bank.query.TransactionList;
import tech.gklijs.api.bank.query.TransactionsByIbanQuery;
import tech.gklijs.graphql_endpoint.model.DType;
import tech.gklijs.graphql_endpoint.util.CurrencyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionService {

    private final ReactorQueryGateway queryGateway;
    private Flux<tech.gklijs.graphql_endpoint.model.Transaction> flux;

    @PostConstruct
    public void setup() {
        flux = queryGateway.subscriptionQuery(new LastTransactionQuery(), ResponseTypes.instanceOf(Transaction.class))
                           .map(TransactionService::mapTransaction)
                           .share();
        flux.map(t -> {
            log.info("Received transaction {} by subscription", t.getId());
            return t;
        }).subscribe();
    }

    public List<tech.gklijs.graphql_endpoint.model.Transaction> allLastTransactions() {
        return queryGateway.query(new AllLastTransactionsQuery(), TransactionList.class)
                           .map(l -> l.stream().map(TransactionService::mapTransaction).collect(Collectors.toList()))
                           .block();
    }

    public tech.gklijs.graphql_endpoint.model.Transaction transactionById(int id) {
        return queryGateway.query(new TransactionByIdQuery(id), Transaction.class)
                           .map(TransactionService::mapTransaction)
                           .block();
    }

    public List<tech.gklijs.graphql_endpoint.model.Transaction> transactionsByIban(String iban, int maxItems) {
        return queryGateway.query(new TransactionsByIbanQuery(iban, maxItems), TransactionList.class)
                           .map(l -> l.stream().map(TransactionService::mapTransaction).collect(Collectors.toList()))
                           .block();
    }

    private Predicate<tech.gklijs.graphql_endpoint.model.Transaction> filterFunction(DType direction, String iban,
                                                                                     Long minAmount,
                                                                                     Long maxAmount,
                                                                                     String descrIncluded) {
        List<Predicate<tech.gklijs.graphql_endpoint.model.Transaction>> predicates = new ArrayList<>();
        Optional.ofNullable(direction).ifPresent(d -> predicates.add(t -> t.getDirection() == d));
        Optional.ofNullable(iban).ifPresent(i -> predicates.add(t -> t.getIban().equals(i)));
        Optional.ofNullable(minAmount).ifPresent(a -> predicates.add(t -> t.getAmount() >= a));
        Optional.ofNullable(maxAmount).ifPresent(a -> predicates.add(t -> t.getAmount() <= a));
        Optional.ofNullable(descrIncluded)
                .ifPresent(i -> predicates.add(t -> StringUtils.containsIgnoreCase(t.getDescr(), i)));
        return transaction -> predicates
                .stream()
                .map(predicate -> predicate.test(transaction))
                .filter(result -> !result)
                .findFirst()
                .orElse(Boolean.TRUE);
    }

    public Publisher<tech.gklijs.graphql_endpoint.model.Transaction> stream(DType direction, String iban,
                                                                            Long minAmount, Long maxAmount,
                                                                            String descrIncluded) {
        return Flux.from(flux)
                   .filter(filterFunction(direction, iban, minAmount, maxAmount, descrIncluded));
    }

    public static tech.gklijs.graphql_endpoint.model.Transaction mapTransaction(Transaction apiTransaction) {
        return new tech.gklijs.graphql_endpoint.model.Transaction(
                Math.toIntExact(apiTransaction.getId()),
                CurrencyUtil.toCurrency(Math.abs(apiTransaction.getChangedBy())),
                apiTransaction.getDescription(),
                apiTransaction.getChangedBy() > 0 ? DType.CREDIT : DType.DEBIT,
                apiTransaction.getFromTo(),
                apiTransaction.getIban(),
                CurrencyUtil.toCurrency(apiTransaction.getNewBalance()),
                Math.abs(apiTransaction.getId())
        );
    }
}
