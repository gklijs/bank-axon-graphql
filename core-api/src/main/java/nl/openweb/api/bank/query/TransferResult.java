package nl.openweb.api.bank.query;

import lombok.Value;

@Value
public class TransferResult {
    TransferState state;
    String reason;

    public enum TransferState {
        BEING_PROCESSED, FAILED, COMPLETED;
    }
}
