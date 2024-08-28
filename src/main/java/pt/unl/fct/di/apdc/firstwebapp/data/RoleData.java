package pt.unl.fct.di.apdc.firstwebapp.data;

import pt.unl.fct.di.apdc.firstwebapp.enums.Roles;

public class RoleData {

    private String username;
    private Roles roleToChangeTo;

    public RoleData() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Roles getRoleToChangeTo() {
        return roleToChangeTo;
    }
}
