package tech.gklijs.graphql_endpoint.resolver;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsMutation;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tech.gklijs.graphql_endpoint.model.AccountResult;
import tech.gklijs.graphql_endpoint.model.MoneyTransferResult;
import tech.gklijs.graphql_endpoint.service.AccountCreationService;
import tech.gklijs.graphql_endpoint.service.MoneyTransferService;

@Slf4j
@DgsComponent
@AllArgsConstructor
public class Mutation {

    private final AccountCreationService accountCreationService;
    private final MoneyTransferService moneyTransferService;

    @DgsMutation
    AccountResult get_account(@InputArgument String password, @InputArgument String username) {
        try {
            return accountCreationService.getAccount(password, username);
        } catch (Exception e) {
            log.warn("Error getting an account.", e);
            return new AccountResult(null, null, "Timeout on server");
        }
    }

    @DgsMutation
    MoneyTransferResult money_transfer(@InputArgument Integer amount, @InputArgument String descr,
                                       @InputArgument String from, @InputArgument String to,
                                       @InputArgument String token,
                                       @InputArgument String username, @InputArgument String uuid) {
        try {
            return moneyTransferService.transfer(amount, descr, from, to, token, username, uuid);
        } catch (Exception e) {
            log.warn("Error transferring money.", e);
            return new MoneyTransferResult("Timeout on server", false, uuid);
        }
    }
}
