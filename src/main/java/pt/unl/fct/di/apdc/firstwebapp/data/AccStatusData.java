package pt.unl.fct.di.apdc.firstwebapp.data;

import pt.unl.fct.di.apdc.firstwebapp.enums.AccStatus;

public class AccStatusData {

    private String username;
    private AccStatus accStatus;

    public AccStatusData() {
    }

    public AccStatus getAccStatus() {
        return accStatus;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
