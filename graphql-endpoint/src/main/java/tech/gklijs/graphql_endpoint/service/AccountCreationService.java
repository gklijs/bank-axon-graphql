package tech.gklijs.graphql_endpoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.axonframework.queryhandling.QueryExecutionException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import tech.gklijs.api.bank.command.CreateBankAccountCommand;
import tech.gklijs.api.bank.query.BankAccountList;
import tech.gklijs.api.bank.query.FindBankAccountsForUserQuery;
import tech.gklijs.api.bank.utils.IbanUtil;
import tech.gklijs.api.user.command.CreateUserAccountCommand;
import tech.gklijs.api.user.error.UserExceptionStatusCode;
import tech.gklijs.api.user.query.FindUserAccountQuery;
import tech.gklijs.api.user.query.UserAccount;
import tech.gklijs.graphql_endpoint.model.AccountResult;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountCreationService {

    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    private Mono<UserAccount> getExistingAccount(String username) {
        return queryGateway.query(new FindUserAccountQuery(username), UserAccount.class);
    }

    private Mono<BankAccountList> getBankAccounts(String username) {
        return queryGateway.query(new FindBankAccountsForUserQuery(username), BankAccountList.class);
    }

    private Mono<UserAccount> createAccount(String password, String username) {
        String iban = IbanUtil.getIban();
        String encoded = passwordEncoder.encode(password);
        return commandGateway.send(new CreateUserAccountCommand(username, encoded))
                             .flatMap(x -> commandGateway.send(new CreateBankAccountCommand(iban, username)))
                             .map(x -> new UserAccount(username, encoded));
    }

    private UserAccount checkPassword(UserAccount userAccount, String password) {
        if (passwordEncoder.matches(password, userAccount.getPassword())) {
            return userAccount;
        } else {
            throw new IllegalArgumentException("wrong password");
        }
    }

    private boolean wasNotFound(Throwable e) {
        if (e instanceof QueryExecutionException) {
            return ((QueryExecutionException) e).getDetails().
                                                map(x -> x == UserExceptionStatusCode.USER_ACCOUNT_NOT_FOUND)
                                                .orElse(false);
        }
        return false;
    }

    private Mono<AccountResult> mapError(Throwable e) {
        log.info("captured error", e);
        String description = e.getMessage().equals("wrong password") ?
                UserExceptionStatusCode.INVALID_PASSWORD.getDescription() :
                UserExceptionStatusCode.UNKNOWN_EXCEPTION.getDescription();
        AccountResult result = new AccountResult(null, null, description);
        return Mono.just(result);
    }

    public AccountResult getAccount(String password, String username) {
        return getExistingAccount(username)
                .onErrorResume(this::wasNotFound, e -> createAccount(password, username))
                .map(account -> checkPassword(account, password))
                .flatMap(account -> getBankAccounts(account.getUsername()))
                .map(accounts -> accounts.get(0))
                .map(account -> new AccountResult(account.getIban(), account.getToken(), null))
                .onErrorResume(this::mapError)
                .block(Duration.ofSeconds(10L));
    }
}
