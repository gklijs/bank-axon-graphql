package tech.gklijs.graphql_endpoint.model;

import lombok.Value;

@Value
public class MoneyTransferResult {

    String reason;
    boolean success;
    String uuid;
}
