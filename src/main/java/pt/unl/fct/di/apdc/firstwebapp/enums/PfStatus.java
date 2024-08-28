package pt.unl.fct.di.apdc.firstwebapp.enums;

public enum PfStatus {
    PUBLIC("PUBLIC"),
    PRIVATE("PRIVATE");

    private final String status;

    PfStatus(String status) {
        this.status = status;
    }
}
