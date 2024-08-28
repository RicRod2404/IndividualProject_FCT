package pt.unl.fct.di.apdc.firstwebapp.utils;

import java.util.regex.Pattern;

public class Validator {

    public static boolean validEmail(String email) {
        // Regex to validate email address
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Compile the ReGex
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    public static boolean isValidPhoneNr(String phoneNr) {
        // Regex to validate phone number
        String phoneNrRegex = "^\\+?\\d{9,15}$";

        // Compile the ReGex
        Pattern pattern = Pattern.compile(phoneNrRegex);
        return pattern.matcher(phoneNr).matches();
    }

    public static boolean isValidPassword(String password) {
        int minLength = 8;
        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasSpecialChar = false;

        if (password.length() < minLength) {
            return false;
        }

        // Check if the password contains at least one letter, one number, and one special character
        for (char ch : password.toCharArray()) {
            if (Character.isLetter(ch)) {
                hasLetter = true;
            } else if (Character.isDigit(ch)) {
                hasNumber = true;
            } else if (isSpecialCharacter(ch)) {
                hasSpecialChar = true;
            }

            // If all conditions are met, break the loop early
            if (hasLetter && hasNumber && hasSpecialChar) {
                break;
            }
        }

        // Return true only if all conditions are met
        return hasLetter && hasNumber && hasSpecialChar;
    }

    private static boolean isSpecialCharacter(char ch) {
        // Define special characters allowed in the password
        String specialChars = "!@#$%^&*()-_+=~`[]{}|;:,.<>?";
        return specialChars.indexOf(ch) != -1;
    }

    public static boolean validZipCode(String zipCode) {
        return zipCode.matches("^\\d{4}-\\d{3}$");
    }

    public static boolean validNIF(String nif) {
        return nif.matches("^\\d{9}$");
    }
}
