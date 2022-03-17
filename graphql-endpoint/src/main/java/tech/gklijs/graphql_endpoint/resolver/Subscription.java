package tech.gklijs.graphql_endpoint.resolver;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import tech.gklijs.graphql_endpoint.model.AccountResult;
import tech.gklijs.graphql_endpoint.model.DType;
import tech.gklijs.graphql_endpoint.model.MoneyTransferResult;
import tech.gklijs.graphql_endpoint.model.Transaction;
import tech.gklijs.graphql_endpoint.service.AccountCreationService;
import tech.gklijs.graphql_endpoint.service.MoneyTransferService;
import tech.gklijs.graphql_endpoint.service.TransactionService;

@DgsComponent
@AllArgsConstructor
public class Subscription {

    private final AccountCreationService accountCreationService;
    private final MoneyTransferService moneyTransferService;
    private final TransactionService transactionService;

    @DgsSubscription
    Publisher<AccountResult> get_account(@InputArgument String password, @InputArgument String username) {
        return accountCreationService.getAccount(password, username);
    }

    @DgsSubscription
    Publisher<MoneyTransferResult> money_transfer(@InputArgument long amount, @InputArgument String descr,
                                                  @InputArgument String from, @InputArgument String to,
                                                  @InputArgument String token,
                                                  String username, String uuid) {
        return moneyTransferService.transfer(amount, descr, from, to, token, username, uuid);
    }

    @DgsSubscription
    Publisher<Transaction> stream_transactions(@InputArgument DType direction, @InputArgument String iban,
                                               @InputArgument Long minAmount, @InputArgument Long maxAmount,
                                               @InputArgument String descrIncludes) {
        return transactionService.stream(direction, iban, minAmount, maxAmount, descrIncludes);
    }
}
