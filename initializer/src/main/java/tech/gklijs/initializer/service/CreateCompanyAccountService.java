package tech.gklijs.initializer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Component;
import tech.gklijs.api.bank.command.CreateBankAccountCommand;
import tech.gklijs.api.bank.command.MoneyTransferCommand;
import tech.gklijs.api.bank.query.BankAccountList;
import tech.gklijs.api.bank.query.FindBankAccountsForUserQuery;

import java.util.UUID;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateCompanyAccountService {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;
    private static final String COMPANY_IBAN = "NL09AXON0000000000";
    private static final String COMPANY_USERNAME = "axonadmin";

    @PostConstruct
    public void createCompanyAccountIfNotExisting() throws InterruptedException {
        log.info("will wait till projector is available");
        waitForProjector();
        log.info("will create company account");
        createCompanyAccount();
        log.info("created company account");
    }

    private void waitForProjector() throws InterruptedException {
        try {
            queryGateway.query(new FindBankAccountsForUserQuery(COMPANY_USERNAME), BankAccountList.class);
            log.info("projector is available");
        } catch (Exception e) {
            log.info("projector not available yet, {}", e.getMessage());
            Thread.sleep(20_000);
            waitForProjector();
        }
    }


    private void createCompanyAccount() throws InterruptedException {
        Object result = commandGateway.sendAndWait(new CreateBankAccountCommand(COMPANY_IBAN, COMPANY_USERNAME));
        log.info("result of creating bank account: {}", result);
        Thread.sleep(5_000);
        IntStream.range(0, 35000).forEach(this::depositEuro);
    }

    private void depositEuro(int serial) {
        MoneyTransferCommand command = new MoneyTransferCommand(
                UUID.randomUUID().toString(),
                "cash",
                100L,
                "cash",
                COMPANY_IBAN,
                String.format("deposit euro %d", serial),
                COMPANY_USERNAME
        );
        commandGateway.sendAndWait(command);
    }
}
