package pt.unl.fct.di.apdc.firstwebapp.data;

import java.time.LocalDateTime;

public class TokenData {
    private String user;
    private String role;
    private boolean isValid;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;
    private String verifier;

    public TokenData(String user, String role, boolean isValid, LocalDateTime validFrom, LocalDateTime validTo, String verifier) {
        this.user = user;
        this.role = role;
        this.isValid = isValid;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.verifier = verifier;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }
}