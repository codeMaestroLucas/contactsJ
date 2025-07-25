package utils;

import org.example.src.entities.Lawyer;
import org.example.src.utils.Validations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TestValidations {

    private static List<Lawyer> setUp() {
        Lawyer lawyer1 = new Lawyer(
                "https://example.com",
                "John Doe",
                "Partner",
                "Firm",
                "Russia",  // country to avoid
                "",
                "john.doe@example.com",
                "123456789"
        );

        Lawyer lawyer2 = new Lawyer(
                "https://example.com",
                "Jane Smith",
                "Partner",
                "Firm",
                "Brazil",
                "",
                "blocked@example.com", // Email to avoid
                "987654321"
        );

        Lawyer lawyer3 = new Lawyer(
                "https://example.com",
                "Alice Brown",
                "Partner",
                "Firm",
                "Canada",
                "",
                "alice.brown@example.com",
                "111222333"
        );

        Lawyer lawyer4 = new Lawyer(
                "https://example.com",
                "Adam Brown",
                "Partner",
                "Firm",
                "Brazil", // Country on Set of countries
                "",
                "alice.brown@example.com",
                "111222444"
        );

        Lawyer lawyer5 = new Lawyer(
                "https://example.com",
                "Julia roberts",
                "Partner",
                "myFIRM",
                "South Africa",
                "",
                "asa.erlandsson@setterwalls.se", // Email in contacts.xlsx
                "111222444"
        );

        return new ArrayList<>(List.of(lawyer1, lawyer2, lawyer3, lawyer4, lawyer5));
    }

    public static void main(String[] args) {
        String emailsOfMonthPath = "src/test/java/utils/testEmailsOfMonthPath.txt";
        String emailsToAvoidPath = "src/test/java/utils/testEmailsToAvoidPath.txt";
        Set<String> lastCountries = new HashSet<>(Set.of("England", "Brazil", "India"));

        List<Lawyer> lawyers = setUp();

        // Test 1: Country to avoid - SHOULD FAIL
        boolean canRegister1 = Validations.makeValidations(
                lawyers.get(0), lastCountries,
                emailsOfMonthPath,
                emailsToAvoidPath
        );
        System.out.println("\n\n[Test 1] Country to avoid Test: " + (canRegister1 ? "FAILED" : "PASSED"));
        System.out.println("CanRegister ---> " + canRegister1);

        // Test 2: Email to avoid - SHOULD FAIL
        boolean canRegister2 = Validations.makeValidations(
                lawyers.get(1), lastCountries,
                emailsOfMonthPath,
                emailsToAvoidPath
        );
        System.out.println("\n\n[Test 2] Email to avoid: " + (canRegister2 ? "FAILED" : "PASSED"));
        System.out.println("CanRegister ---> " + canRegister2);

        // Test 3: Valid lawyer - SHOULD PASS
        boolean canRegister3 = Validations.makeValidations(
                lawyers.get(2), lastCountries,
                emailsOfMonthPath,
                emailsToAvoidPath
        );
        System.out.println("\n\n[Test 3] Valid lawyer: " + (canRegister3 ? "PASSED" : "FAILED"));
        System.out.println("CanRegister ---> " + canRegister3);

        // Test 4: Country already on Firm Set - SHOULD FAIL
        boolean canRegister4 = Validations.makeValidations(
                lawyers.get(3), lastCountries,
                emailsOfMonthPath,
                emailsToAvoidPath
        );
        System.out.println("\n\n[Test 4] Country in Set: " + (canRegister4 ? "FAILED" : "PASSED"));
        System.out.println("CanRegister ---> " + canRegister4);

        // Test 4: Country already on Firm Set - SHOULD FAIL
        boolean canRegister5 = Validations.makeValidations(
                lawyers.get(4), lastCountries,
                emailsOfMonthPath,
                emailsToAvoidPath
        );
        System.out.println("\n\n[Test 5] Email in contacts: " + (canRegister5 ? "FAILED" : "PASSED"));
        System.out.println("CanRegister ---> " + canRegister5);
    }
}
