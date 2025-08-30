package org.example.exceptions;

public class LawyerExceptions extends Exception{

    public LawyerExceptions(String message){
        super(message);
    }

    public static LawyerExceptions linkException(String link) {
        return new LawyerExceptions("Invalid LINK: " + link);
    }

    public static LawyerExceptions nameException(String name) {
        return new LawyerExceptions("Invalid NAME: " + name);
    }

    public static LawyerExceptions roleException(String role) {
        return new LawyerExceptions("Invalid ROLE: " + role);
    }

    public static LawyerExceptions countryException(String country) {
        return new LawyerExceptions("Invalid COUNTRY: " + country);
    }

    public static LawyerExceptions practiceAreaException(String practiceArea) {
        return new LawyerExceptions("Invalid PRACTICE AREA: " + practiceArea);
    }

    public static LawyerExceptions emailException(String email) {
        return new LawyerExceptions("Invalid EMAIL: " + email);
    }

    public static LawyerExceptions phoneException(String phone) {
        return new LawyerExceptions("Invalid PHONE: " + phone);
    }
}
