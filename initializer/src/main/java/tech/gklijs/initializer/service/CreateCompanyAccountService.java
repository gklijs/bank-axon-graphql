package tech.gklijs.initializer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;
import tech.gklijs.api.bank.command.CreateBankAccountCommand;
import tech.gklijs.api.bank.command.MoneyTransferCommand;
import tech.gklijs.api.bank.query.BankAccount;
import tech.gklijs.api.bank.query.FindBankAccountQuery;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCompanyAccountService {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private static final String COMPANY_IBAN = "NL09AXON0000000000";
    private static final String COMPANY_USERNAME = "axonadmin";
    private static final long DEFAULT_TIMEOUT = 10;

    @PostConstruct
    public void createCompanyAccountIfNotExisting() throws ExecutionException, InterruptedException, TimeoutException {
        log.info("createCompanyAccountIfNotExisting");
        BankAccount bankaccount;
        try {
            bankaccount = queryGateway.query(new FindBankAccountQuery(COMPANY_IBAN), BankAccount.class).get(
                    DEFAULT_TIMEOUT,
                    TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            log.info("encountered error, probably account did not exist yet");
            bankaccount = createCompanyAccount().get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
        if (bankaccount == null) {
            log.warn("Did not get bank account");
            throw new CompanyAccountCreationFailed();
        }
        log.info("Current state of company bank account: {}", bankaccount);
    }

    private CompletableFuture<BankAccount> createCompanyAccount()
            throws ExecutionException, InterruptedException, TimeoutException {
        commandGateway.send(new CreateBankAccountCommand(COMPANY_IBAN, COMPANY_USERNAME)).get(DEFAULT_TIMEOUT,
                                                                                              TimeUnit.SECONDS);
        setInitialAmount().get(DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        Thread.sleep(1000);
        return queryGateway.query(new FindBankAccountQuery(COMPANY_IBAN), BankAccount.class);
    }

    private CompletableFuture<Object> setInitialAmount() {
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
