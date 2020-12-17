package nl.openweb.graphql_endpoint.model;

import lombok.Value;

@Value
public class AccountResult {
    private String iban;
    private String reason;
    private String token;
}
