package tech.gklijs.graphql_endpoint.resolver;

import com.netflix.graphql.dgs.DgsComponent;
import com.netflix.graphql.dgs.DgsSubscription;
import com.netflix.graphql.dgs.InputArgument;
import lombok.AllArgsConstructor;
import org.reactivestreams.Publisher;
import tech.gklijs.graphql_endpoint.model.Transaction;
import tech.gklijs.graphql_endpoint.model.dtype;
import tech.gklijs.graphql_endpoint.service.TransactionService;

@DgsComponent
@AllArgsConstructor
public class Subscription {

    private final TransactionService transactionService;

    @DgsSubscription
    Publisher<Transaction> stream_transactions(@InputArgument(collectionType = dtype.class) dtype direction,
                                               @InputArgument String iban,
                                               @InputArgument Integer min_amount, @InputArgument Integer max_amount,
                                               @InputArgument String descr_includes) {
        return transactionService.stream(direction, iban, min_amount, max_amount, descr_includes);
    }
}
