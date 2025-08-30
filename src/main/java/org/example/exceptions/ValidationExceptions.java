package org.example.exceptions;

public class ValidationExceptions extends Exception{

    public ValidationExceptions(String message){
        super(message);
    }


    public static ValidationExceptions emailValidation() {
        return new ValidationExceptions("Incomplete lawyer data, skipping...\n");
    }


    public static ValidationExceptions countryToAvoid() {
        return new ValidationExceptions("Country to avoid");
    }


    public static ValidationExceptions emailToAvoid() {
        return new ValidationExceptions("Email to avoid");
    }


    public static ValidationExceptions firmAlreadyRegisteredInMonth() {
        return new ValidationExceptions("Firm already registered in Month file");
    }


    public static ValidationExceptions emailAlreadyRegistered() {
        return new ValidationExceptions("Email already registered previously");
    }


    public static ValidationExceptions countryInSetOfCountries() {
        return new ValidationExceptions("Country in set of countries");
    }
}
