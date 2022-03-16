package tech.gklijs.graphql_endpoint.model;

import lombok.Value;

@Value
public class AccountResult {

    String iban;
    String token;
    String reason;
}
