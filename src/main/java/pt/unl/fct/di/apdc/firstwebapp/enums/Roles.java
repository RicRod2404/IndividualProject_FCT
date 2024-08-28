package pt.unl.fct.di.apdc.firstwebapp.enums;

public enum Roles {
    SU("SU"),
    GA("GA"),
    GBO("GBO"),
    USER("USER");

    private final String role;

    Roles(String role) {
        this.role = role;
    }
}
