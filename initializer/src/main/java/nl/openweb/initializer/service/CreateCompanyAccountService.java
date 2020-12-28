package nl.openweb.initializer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.openweb.api.bank.command.CreateBankAccountCommand;
import nl.openweb.api.bank.command.MoneyTransferCommand;
import nl.openweb.api.bank.query.BankAccount;
import nl.openweb.api.bank.query.FindBankAccountQuery;
import org.axonframework.extensions.reactor.commandhandling.gateway.ReactorCommandGateway;
import org.axonframework.extensions.reactor.queryhandling.gateway.ReactorQueryGateway;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCompanyAccountService {

    private final ReactorCommandGateway commandGateway;
    private final ReactorQueryGateway queryGateway;
    private static final String COMPANY_IBAN = "NL66OPEN0000000000";
    private static final String COMPANY_USERNAME = "openweb";

    @PostConstruct
    public void createCompanyAccountIfNotExisting() {
        log.info("createCompanyAccountIfNotExisting");
        BankAccount bankaccount;
        try {
            bankaccount = queryGateway.query(new FindBankAccountQuery(COMPANY_IBAN), BankAccount.class)
                    .onErrorResume(x -> createCompanyAccount())
                    .block();
        } catch (Exception e) {
            log.warn("Encountered exception getting/creating the company bank account", e);
            throw new CompanyAccountCreationFailed(e);
        }
        if (bankaccount == null) {
            log.warn("Did not get bank account");
            throw new CompanyAccountCreationFailed();
        }
        log.info("Current state of company bank account: {}", bankaccount.toString());
    }

    private Mono<BankAccount> createCompanyAccount() {
        return commandGateway.send(new CreateBankAccountCommand(COMPANY_IBAN, COMPANY_USERNAME))
                .flatMap(x -> setInitialAmount())
                .flatMap(x -> queryGateway.query(new FindBankAccountQuery(COMPANY_IBAN), BankAccount.class));
    }

    private Mono<Object> setInitialAmount() {
        MoneyTransferCommand command = new MoneyTransferCommand(
                UUID.randomUUID().toString(),
                "cash",
                100000000000000000L,
                "cash",
                COMPANY_IBAN,
                "initial funds",
                COMPANY_USERNAME
        );
        return commandGateway.send(command);
    }
}
