package nl.openweb.graphql_endpoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.openweb.api.bank.query.*;
import nl.openweb.graphql_endpoint.model.DType;
import nl.openweb.graphql_endpoint.model.Transaction;
import nl.openweb.graphql_endpoint.util.CurrencyUtil;
import org.apache.commons.lang3.StringUtils;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionService {

    private final ReactorQueryGateway queryGateway;
    private Flux<Transaction> flux;

    @PostConstruct
    public void setup() {
        flux = queryGateway.subscriptionQueryMany(new LastTransactionQuery(), Transaction.class).share();
    }

    public List<Transaction> allLastTransactions() {
        return queryGateway.query(new AllLastTransactionsQuery(), TransactionList.class)
                .map(l -> l.stream().map(TransactionService::mapTransaction).collect(Collectors.toList()))
                .block();
    }

    public Transaction transactionById(int id) {
        return queryGateway.query(new TransactionByIdQuery(id), nl.openweb.api.bank.query.Transaction.class)
                .map(TransactionService::mapTransaction)
                .block();
    }

    public List<Transaction> transactionsByIban(String iban, int maxItems) {
        return queryGateway.query(new TransactionsByIbanQuery(iban, maxItems), TransactionList.class)
                .map(l -> l.stream().map(TransactionService::mapTransaction).collect(Collectors.toList()))
                .block();
    }

    private Predicate<Transaction> filterFunction(DType direction, String iban, Long minAmount,
                                                  Long maxAmount, String descrIncluded) {
        List<Predicate<Transaction>> predicates = new ArrayList<>();
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

    public Publisher<Transaction> stream(DType direction, String iban, Long minAmount, Long maxAmount,
                                         String descrIncluded) {
        return Flux.from(flux)
                .filter(filterFunction(direction, iban, minAmount, maxAmount, descrIncluded));
    }

    public static Transaction mapTransaction(nl.openweb.api.bank.query.Transaction apiTransaction) {
        return new Transaction(
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
