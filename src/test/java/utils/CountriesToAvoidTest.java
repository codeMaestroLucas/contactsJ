package utils;

import org.example.src.utils.Validations;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the two-tier country avoidance system:
 * 1. PERMANENT countries - Always avoided
 * 2. TEMPORARY countries - Only avoided when enabled
 */
public class CountriesToAvoidTest {

    // ========================================================================
    // PERMANENT COUNTRIES TESTS
    // These countries should ALWAYS be avoided regardless of enabled flag
    // ========================================================================

    @Test
    public void testPermanentCountriesAreAlwaysAvoided() {
        // These tests assume the countries are in countriesToAvoidPermanent.json
        // Adjust based on which countries you keep in the permanent list
        
        // Example: If Russia is permanent, it should always be blocked
        // assertTrue(Validations.isACountryToAvoid("Russia"), "Russia should always be avoided (permanent)");
        
        // Add your permanent countries here after you adjust the JSON files
        System.out.println("⚠️  Configure permanent countries in countriesToAvoidPermanent.json first");
    }

    // ========================================================================
    // TEMPORARY COUNTRIES TESTS
    // These countries should only be avoided when their continent is enabled
    // ========================================================================

    @Test
    public void testTemporaryCountriesWhenEnabled() {
        // To run this test:
        // 1. Move countries to countriesToAvoidTemporary.json
        // 2. Set their continent to "enabled": true
        // 3. Uncomment and adjust the assertions below
        
        // Example:
        // assertTrue(Validations.isACountryToAvoid("Japan"), "Japan should be avoided when Asia is enabled");
        // assertTrue(Validations.isACountryToAvoid("India"), "India should be avoided when Asia is enabled");
        
        System.out.println("⚠️  Configure temporary countries and set enabled: true to run this test");
    }

    @Test
    public void testTemporaryCountriesWhenDisabled() {
        // To run this test:
        // 1. Move countries to countriesToAvoidTemporary.json ONLY (not in permanent)
        // 2. Set their continent to "enabled": false
        // 3. Uncomment and adjust the assertions below
        
        // Example:
        // assertFalse(Validations.isACountryToAvoid("Japan"), "Japan should NOT be avoided when Asia is disabled");
        // assertFalse(Validations.isACountryToAvoid("India"), "India should NOT be avoided when Asia is disabled");
        
        System.out.println("⚠️  Configure temporary countries and set enabled: false to run this test");
    }

    // ========================================================================
    // COMBINED TESTS
    // Test countries in both permanent and temporary lists
    // ========================================================================

    @Test
    public void testCountryInBothListsPermanentTakesPriority() {
        // If a country is in BOTH lists, permanent should take priority
        // This means it's blocked even if temporary is disabled
        
        // Example: If "China" is in both lists and Asia temporary is disabled
        // assertTrue(Validations.isACountryToAvoid("China"), "China should be avoided (permanent takes priority)");
        
        System.out.println("⚠️  Add a country to BOTH lists to test priority");
    }

    // ========================================================================
    // ALLOWED COUNTRIES TESTS
    // ========================================================================

    @Test
    public void testAllowedCountries() {
        // Countries NOT in either list should be allowed
        assertFalse(Validations.isACountryToAvoid("Brazil"), "Brazil should NOT be avoided");
        assertFalse(Validations.isACountryToAvoid("Canada"), "Canada should NOT be avoided");
        assertFalse(Validations.isACountryToAvoid("Germany"), "Germany should NOT be avoided");
        assertFalse(Validations.isACountryToAvoid("United Kingdom"), "UK should NOT be avoided");
        assertFalse(Validations.isACountryToAvoid("France"), "France should NOT be avoided");
    }

    // ========================================================================
    // EDGE CASES
    // ========================================================================

    @Test
    public void testCaseInsensitivity() {
        // After configuring your lists, test case insensitivity
        // Example (adjust based on your permanent/temporary countries):
        // assertTrue(Validations.isACountryToAvoid("russia"), "russia (lowercase) should be avoided");
        // assertTrue(Validations.isACountryToAvoid("RUSSIA"), "RUSSIA (uppercase) should be avoided");
        // assertTrue(Validations.isACountryToAvoid("RuSsIa"), "RuSsIa (mixed case) should be avoided");
        
        System.out.println("⚠️  Configure countries first, then test case insensitivity");
    }

    @Test
    public void testWhitespaceHandling() {
        // After configuring your lists, test whitespace handling
        // Example (adjust based on your permanent/temporary countries):
        // assertTrue(Validations.isACountryToAvoid(" Russia "), "Russia with spaces should be avoided");
        // assertTrue(Validations.isACountryToAvoid("Russia   "), "Russia with trailing spaces should be avoided");
        
        System.out.println("⚠️  Configure countries first, then test whitespace handling");
    }

    // ========================================================================
    // INSTRUCTIONS FOR USE
    // ========================================================================

    @Test
    public void printInstructions() {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("📋 INSTRUCTIONS FOR CONFIGURING TESTS");
        System.out.println("=".repeat(80));
        System.out.println("\n1. EDIT countriesToAvoidPermanent.json");
        System.out.println("   - Remove countries that should NOT be permanent");
        System.out.println("   - Keep only countries that should ALWAYS be blocked");
        System.out.println("   - Examples: Russia, North Korea, sanctioned countries");
        
        System.out.println("\n2. EDIT countriesToAvoidTemporary.json");
        System.out.println("   - Remove countries that should NOT be temporary");
        System.out.println("   - Keep only countries for strategic/sporadic blocking");
        System.out.println("   - Set 'enabled: false' for all continents initially");
        System.out.println("   - Enable only when needed");
        
        System.out.println("\n3. UPDATE TESTS");
        System.out.println("   - Uncomment test assertions");
        System.out.println("   - Replace example countries with your actual countries");
        System.out.println("   - Run tests to verify configuration");
        
        System.out.println("\n4. TEST SCENARIOS");
        System.out.println("   ✓ Permanent country → Always blocked");
        System.out.println("   ✓ Temporary enabled → Blocked");
        System.out.println("   ✓ Temporary disabled → Allowed");
        System.out.println("   ✓ Not in any list → Allowed");
        
        System.out.println("\n" + "=".repeat(80) + "\n");
    }

    // ========================================================================
    // QUICK VALIDATION TEST
    // Uncomment and configure after adjusting JSON files
    // ========================================================================

    /*
    @Test
    public void quickValidationTest() {
        System.out.println("\n=== QUICK VALIDATION TEST ===");
        
        // Test your configured countries here
        String[] testCountries = {
            "Russia",        // Should be blocked (permanent)
            "Japan",         // Should be blocked if Asia enabled (temporary)
            "Brazil",        // Should be allowed (not in any list)
            "USA",           // Depends on your configuration
            "United Kingdom" // Should be allowed (not in any list)
        };
        
        for (String country : testCountries) {
            boolean isAvoided = Validations.isACountryToAvoid(country);
            System.out.println(country + ": " + (isAvoided ? "❌ BLOCKED" : "✅ ALLOWED"));
        }
        
        System.out.println("=============================\n");
    }
    */
}
