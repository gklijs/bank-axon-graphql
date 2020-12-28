package nl.openweb.initializer.service;

public class CompanyAccountCreationFailed extends RuntimeException {
    CompanyAccountCreationFailed() {
        super("Company account creation has failed");
    }

    CompanyAccountCreationFailed(Throwable e) {
        super("Company account creation has failed", e);
    }
}
