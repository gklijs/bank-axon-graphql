package tech.gklijs.graphql_endpoint.resolver;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
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

@AllArgsConstructor
@Component
public class Subscription implements GraphQLSubscriptionResolver {

    private final AccountCreationService accountCreationService;
    private final MoneyTransferService moneyTransferService;
    private final TransactionService transactionService;

    Publisher<AccountResult> get_account(String password, String username) {
        return accountCreationService.getAccount(password, username);
    }

    Publisher<MoneyTransferResult> money_transfer(long amount, String descr, String from, String to, String token,
                                                  String username, String uuid) {
        return moneyTransferService.transfer(amount, descr, from, to, token, username, uuid);
    }

    Publisher<Transaction> stream_transactions(DType direction, String iban, Long minAmount, Long maxAmount,
                                               String descrIncludes) {
        return transactionService.stream(direction, iban, minAmount, maxAmount, descrIncludes);
    }
}
