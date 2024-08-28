package pt.unl.fct.di.apdc.firstwebapp.enums;

public enum AccStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String status;

    AccStatus(String status) {
        this.status = status;
    }
}
