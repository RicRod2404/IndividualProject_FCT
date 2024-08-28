package pt.unl.fct.di.apdc.firstwebapp.data;

import pt.unl.fct.di.apdc.firstwebapp.enums.AccStatus;
import pt.unl.fct.di.apdc.firstwebapp.enums.Roles;
import pt.unl.fct.di.apdc.firstwebapp.utils.Validator;

public class UpdateData {

    private String usernameToChange;
    private String email;
    private String name;
    private String phoneNr;
    private String occupation;
    private String workingPlace;
    private String address;
    private String zipCode;
    private String nif;
    private Roles role;
    private AccStatus accStatus;
    private String pfStatus;


    public UpdateData() {
    }

    public boolean validRegistration() {
        return getEmail() != null && getName() != null && getPhoneNr() != null
                && Validator.validEmail(getEmail());
    }

    public String getUsernameToChange() {
        return usernameToChange;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNr() {
        return phoneNr;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getWorkingPlace() {
        return workingPlace;
    }

    public String getAddress() {
        return address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getNif() {
        return nif;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public AccStatus getAccStatus() {
        return accStatus;
    }

    public String getPfStatus() {
        return pfStatus;
    }

    public void setPfStatus(String pfStatus) {
        this.pfStatus = pfStatus;
    }
}
