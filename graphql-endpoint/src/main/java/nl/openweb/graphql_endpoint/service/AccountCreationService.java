package nl.openweb.graphql_endpoint.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.openweb.api.bank.command.CreateBankAccountCommand;
import nl.openweb.api.bank.query.BankAccount;
import nl.openweb.api.bank.query.FindBankAccountQuery;
import nl.openweb.api.bank.utils.IbanUtil;
import nl.openweb.api.user.command.AddBankAccountCommand;
import nl.openweb.api.user.command.CreateUserAccountCommand;
import nl.openweb.api.user.query.FindUserAccountQuery;
import nl.openweb.api.user.query.UserAccount;
import nl.openweb.graphql_endpoint.model.AccountResult;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.reactivestreams.Publisher;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

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

    private Mono<BankAccount> getBankAccount(String iban) {
        return queryGateway.query(new FindBankAccountQuery(iban), BankAccount.class);
    }

    private Mono<UserAccount> createAccount(String username, String password) {
        String iban = IbanUtil.getIban();
        return commandGateway.send(new CreateUserAccountCommand(username, passwordEncoder.encode(password)))
                .flatMap(x -> commandGateway.send(new CreateBankAccountCommand(iban)))
                .flatMap(x -> getBankAccount(iban))
                .flatMap(x -> commandGateway.send(new AddBankAccountCommand(username, iban, x.getToken())))
                .flatMap(x -> getExistingAccount(username));
    }

    private Mono<UserAccount> checkPassword(UserAccount userAccount, String password) {
        if (passwordEncoder.matches(password, userAccount.getPassword())) {
            return Mono.just(userAccount);
        } else {
            throw new IllegalArgumentException("wrong password");
        }
    }

    public Publisher<AccountResult> getAccount(String username, String password) {
        return getExistingAccount(username)
                .flatMap(user -> user == null ? createAccount(username, password) : checkPassword(user, password))
                .map(account -> account.getBankAccounts().get(0))
                .map(account -> new AccountResult(account.getIban(), account.getIban(), null))
                .onErrorResume(e -> Mono.just(new AccountResult(null, null, e.getMessage())));
    }
}
