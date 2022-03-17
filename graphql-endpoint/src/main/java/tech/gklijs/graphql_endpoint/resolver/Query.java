package tech.gklijs.graphql_endpoint.resolver;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsQuery;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.gklijs.graphql_endpoint.model.Transaction;
import tech.gklijs.graphql_endpoint.service.TransactionService;

import java.util.List;

@DgsComponent
@AllArgsConstructor
public class Query {

    private final TransactionService transactionService;

    @DgsQuery
    List<Transaction> all_last_transactions() {
        return transactionService.allLastTransactions();
    }

    @DgsQuery
    Transaction transaction_by_id(@InputArgument int id) {
        return transactionService.transactionById(id);
    }

    @DgsQuery
    List<Transaction> transactions_by_iban(@InputArgument String iban, @InputArgument int maxItems) {
        return transactionService.transactionsByIban(iban, maxItems);
    }
}
