package tech.gklijs.initializer.service;

public class CompanyAccountCreationFailed extends RuntimeException {

    CompanyAccountCreationFailed() {
        super("Company account creation has failed");
    }
}
