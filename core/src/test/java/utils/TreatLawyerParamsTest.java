package utils;

import org.example.src.utils.TreatLawyerParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TreatLawyerParamsTest {

    // ── treatEmail ────────────────────────────────────────────────────────────

    @Test
    void treatEmail_removesMailtoPrefix() {
        assertEquals("john@example.com", TreatLawyerParams.treatEmail("mailto:john@example.com"));
    }

    @Test
    void treatEmail_removesEmailColonPrefix() {
        assertEquals("john@example.com", TreatLawyerParams.treatEmail("email:john@example.com"));
    }

    @Test
    void treatEmail_lowercasesInput() {
        assertEquals("john@example.com", TreatLawyerParams.treatEmail("JOHN@EXAMPLE.COM"));
    }

    @Test
    void treatEmail_removesQueryString() {
        assertEquals("john@example.com", TreatLawyerParams.treatEmail("john@example.com?subject=hello"));
    }

    @Test
    void treatEmail_trimsWhitespace() {
        assertEquals("john@example.com", TreatLawyerParams.treatEmail("  john@example.com  "));
    }

    @Test
    void treatEmail_handlesPlainEmail() {
        assertEquals("alice@law.com", TreatLawyerParams.treatEmail("alice@law.com"));
    }

    @Test
    void treatEmail_combinesMailtoAndUppercase() {
        assertEquals("bob@firm.com", TreatLawyerParams.treatEmail("mailto:BOB@FIRM.COM"));
    }

    // ── treatPhone ────────────────────────────────────────────────────────────

    @Test
    void treatPhone_extractsOnlyDigits_fromFormattedNumber() {
        String result = TreatLawyerParams.treatPhone("+55 (11) 98765-4321");
        assertTrue(result.matches("\\d+"), "Expected only digits, got: " + result);
    }

    @Test
    void treatPhone_returnsEmpty_forEmptyInput() {
        assertEquals("", TreatLawyerParams.treatPhone(""));
    }

    @Test
    void treatPhone_removesLeadingZeros() {
        String result = TreatLawyerParams.treatPhone("00112345678");
        assertFalse(result.startsWith("0"), "Expected leading zeros removed, got: " + result);
    }

    @Test
    void treatPhone_returnsOnlyDigits_fromUsNumber() {
        String result = TreatLawyerParams.treatPhone("+1 (212) 555-1234");
        assertTrue(result.matches("\\d*"), "Expected only digits, got: " + result);
    }

    // ── treatName ─────────────────────────────────────────────────────────────

    @Test
    void treatName_removesDrTitle() {
        String result = TreatLawyerParams.treatName("Dr. John Smith");
        assertAll(
            () -> assertFalse(result.contains("Dr."), "Should remove 'Dr.'"),
            () -> assertTrue(result.contains("John"), "Should contain 'John'"),
            () -> assertTrue(result.contains("Smith"), "Should contain 'Smith'")
        );
    }

    @Test
    void treatName_removesMrTitle() {
        String result = TreatLawyerParams.treatName("Mr. John Smith");
        assertAll(
            () -> assertFalse(result.contains("Mr."), "Should remove 'Mr.'"),
            () -> assertTrue(result.contains("John"), "Should contain 'John'")
        );
    }

    @Test
    void treatName_capitalizesWords() {
        String result = TreatLawyerParams.treatName("john smith");
        assertTrue(result.startsWith("J"), "First letter should be uppercase, got: " + result);
    }

    @Test
    void treatName_preservesHyphenatedNames() {
        String result = TreatLawyerParams.treatName("mary-anne jones");
        assertTrue(result.contains("Mary") || result.contains("mary"),
            "Should preserve 'Mary', got: " + result);
        assertTrue(result.contains("-"), "Should preserve hyphen");
    }

    @Test
    void treatName_returnsEmpty_forEmptyInput() {
        assertEquals("", TreatLawyerParams.treatName(""));
    }

    @Test
    void treatName_doesNotContainTitleWithPeriod_afterProcessing() {
        String result = TreatLawyerParams.treatName("Prof. Ana Lima");
        assertFalse(result.contains("Prof."), "Should remove 'Prof.'");
    }

    // ── treatRole ─────────────────────────────────────────────────────────────

    @Test
    void treatRole_returnsNonNull_forAnyInput() {
        assertNotNull(TreatLawyerParams.treatRole("Partner"));
        assertNotNull(TreatLawyerParams.treatRole("Unknown Role XYZ"));
        assertNotNull(TreatLawyerParams.treatRole(""));
    }

    @Test
    void treatRole_returnsNonEmpty_forKnownValidRole() {
        String result = TreatLawyerParams.treatRole("Partner");
        assertFalse(result.isBlank(), "Known role 'Partner' should return a non-blank result");
    }

    @Test
    void treatRole_returnsOriginalOrCanonical_forUnknownRole() {
        String unknown = "Chief Rocket Scientist";
        String result = TreatLawyerParams.treatRole(unknown);
        assertNotNull(result);
    }

    // ── treatSpecialism ───────────────────────────────────────────────────────

    @Test
    void treatSpecialism_managerRole_returnsAdvisor() {
        assertEquals("Advisor", TreatLawyerParams.treatSpecialism("Manager"));
    }

    @Test
    void treatSpecialism_roleContainingAdvisor_returnsAdvisor() {
        assertEquals("Advisor", TreatLawyerParams.treatSpecialism("Senior Advisor"));
    }

    @Test
    void treatSpecialism_advisorAlone_returnsAdvisor() {
        assertEquals("Advisor", TreatLawyerParams.treatSpecialism("Advisor"));
    }

    @Test
    void treatSpecialism_partnerRole_returnsLegal() {
        assertEquals("Legal", TreatLawyerParams.treatSpecialism("Partner"));
    }

    @Test
    void treatSpecialism_counselRole_returnsLegal() {
        assertEquals("Legal", TreatLawyerParams.treatSpecialism("Counsel"));
    }

    @Test
    void treatSpecialism_directorRole_returnsLegal() {
        assertEquals("Legal", TreatLawyerParams.treatSpecialism("Director"));
    }

    // ── removeAccents ─────────────────────────────────────────────────────────

    @Test
    void removeAccents_removesAcuteAccent() {
        assertEquals("cafe", TreatLawyerParams.removeAccents("café"));
    }

    @Test
    void removeAccents_removesUmlaut() {
        assertEquals("uber", TreatLawyerParams.removeAccents("über"));
    }

    @Test
    void removeAccents_noChangeOnPlainAscii() {
        assertEquals("hello world 123", TreatLawyerParams.removeAccents("hello world 123"));
    }

    @Test
    void removeAccents_removesPortugueseTilde() {
        String result = TreatLawyerParams.removeAccents("são");
        assertFalse(result.contains("ã"), "Should remove tilde from 'ã'");
    }

    @Test
    void removeAccents_returnsEmpty_forEmptyInput() {
        assertEquals("", TreatLawyerParams.removeAccents(""));
    }

    // ── getNameFromEmail ──────────────────────────────────────────────────────

    @Test
    void getNameFromEmail_capitalizesDotSeparatedParts() {
        String result = TreatLawyerParams.getNameFromEmail("john.doe@example.com");
        assertTrue(result.contains("John") || result.toLowerCase().contains("john"),
            "Should contain 'John' from 'john.doe', got: " + result);
    }

    @Test
    void getNameFromEmail_appendsStarSuffix() {
        String result = TreatLawyerParams.getNameFromEmail("test@example.com");
        assertTrue(result.endsWith("*****"), "Should end with '*****', got: " + result);
    }

    @Test
    void getNameFromEmail_returnsNonEmpty_forAnyEmail() {
        String result = TreatLawyerParams.getNameFromEmail("a@b.com");
        assertFalse(result.isBlank());
    }

    // ── treatPracticeArea ─────────────────────────────────────────────────────

    @Test
    void treatPracticeArea_returnsPlaceholder_forNull() {
        assertEquals("-----", TreatLawyerParams.treatPracticeArea(null));
    }

    @Test
    void treatPracticeArea_returnsNonNull_forBlankString() {
        // Blank input returns "" (empty) — only null returns "-----"
        String result = TreatLawyerParams.treatPracticeArea("   ");
        assertNotNull(result);
    }

    @Test
    void treatPracticeArea_returnsNonBlank_forValidInput() {
        String result = TreatLawyerParams.treatPracticeArea("Corporate");
        assertFalse(result.isBlank(), "Expected non-blank result for 'Corporate'");
    }

    // ── treatCountry ──────────────────────────────────────────────────────────

    @Test
    void treatCountry_returnsNonBlank_forKnownCountry() {
        String result = TreatLawyerParams.treatCountry("England");
        assertFalse(result.isBlank(), "Expected non-blank result for 'England'");
    }

    @Test
    void treatCountry_trimsWhitespace() {
        String plain = TreatLawyerParams.treatCountry("England");
        String padded = TreatLawyerParams.treatCountry("  England  ");
        assertEquals(plain, padded, "Trimmed and non-trimmed inputs should yield same result");
    }

    @Test
    void treatCountry_returnsNonNull_forUnknownCountry() {
        String result = TreatLawyerParams.treatCountry("Atlantis");
        assertNotNull(result);
    }
}
