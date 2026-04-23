package org.example.exceptions;

public class LawyerExceptions extends Exception {

    public enum Category { LINK, NAME, EMAIL, PHONE, ROLE, COUNTRY, PRACTICE, SOCIALS, UNKNOWN }

    public final Category category;

    public LawyerExceptions(String message) {
        super(message);
        this.category = Category.UNKNOWN;
    }

    private LawyerExceptions(String message, Category category) {
        super(message);
        this.category = category;
    }

    public static LawyerExceptions linkException(String link) {
        return new LawyerExceptions("Invalid LINK: " + link, Category.LINK);
    }

    public static LawyerExceptions nameException(String name) {
        return new LawyerExceptions("Invalid NAME: " + name, Category.NAME);
    }

    public static LawyerExceptions roleException(String role) {
        return new LawyerExceptions("Invalid ROLE: " + role, Category.ROLE);
    }

    public static LawyerExceptions countryException(String country) {
        return new LawyerExceptions("Invalid COUNTRY: " + country, Category.COUNTRY);
    }

    public static LawyerExceptions practiceAreaException(String practiceArea) {
        return new LawyerExceptions("Invalid PRACTICE AREA: " + practiceArea, Category.PRACTICE);
    }

    public static LawyerExceptions emailException(String email) {
        return new LawyerExceptions("Invalid EMAIL: " + email, Category.EMAIL);
    }

    public static LawyerExceptions phoneException(String phone) {
        return new LawyerExceptions("Invalid PHONE: " + phone, Category.PHONE);
    }

    public static LawyerExceptions socialsException(String socials) {
        return new LawyerExceptions("Invalid SOCIALS: " + socials, Category.SOCIALS);
    }
}
