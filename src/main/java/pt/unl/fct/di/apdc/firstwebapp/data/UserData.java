package pt.unl.fct.di.apdc.firstwebapp.data;

import pt.unl.fct.di.apdc.firstwebapp.utils.Validator;

public class UserData {

    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String name;
    private String phoneNr;
    private String occupation;
    private String workingPlace;
    private String address;
    private String zipCode;
    private String nif;
    private String pfStatus;

    public UserData() {
    }

    public boolean validRegistration() {
        return getUsername() != null && getPassword() != null
                && getEmail() != null && getName() != null && getPhoneNr() != null
                && getPassword().equals(getConfirmPassword()) && Validator.validEmail(getEmail())
                && Validator.isValidPassword(getPassword()) && Validator.isValidPhoneNr(getPhoneNr());
        //we use static validator method to save memory, this way we don't need to create objects of this class every time we call it
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
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

    public String getPfStatus() {
        return pfStatus;
    }

    public void setPfStatus(String pfStatus) {
        this.pfStatus = pfStatus;
    }
}
