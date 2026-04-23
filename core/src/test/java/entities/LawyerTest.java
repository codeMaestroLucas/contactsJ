package entities;

import org.example.src.entities.Lawyer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LawyerTest {

    private Lawyer.LawyerBuilder validBase() {
        return Lawyer.builder()
                .link("https://example.com/john-smith")
                .name("John Smith")
                .role("Partner")
                .firm("Test Firm LLP")
                .country("United States")
                .practiceArea("Corporate")
                .email("john.smith@testfirm.com")
                .phone("+1 (212) 555-1234");
    }

    // ── Email normalization ───────────────────────────────────────────────────

    @Test
    void build_normalizesEmailToLowercase() {
        Lawyer l = validBase().email("JOHN.SMITH@TESTFIRM.COM").build();
        assertEquals("john.smith@testfirm.com", l.getEmail());
    }

    @Test
    void build_removesMailtoPrefixFromEmail() {
        Lawyer l = validBase().email("mailto:john@testfirm.com").build();
        assertEquals("john@testfirm.com", l.getEmail());
    }

    @Test
    void build_removesQueryStringFromEmail() {
        Lawyer l = validBase().email("john@firm.com?subject=hi").build();
        assertEquals("john@firm.com", l.getEmail());
    }

    // ── Name fallback ─────────────────────────────────────────────────────────

    @Test
    void build_derivesName_fromEmail_whenNameIsBlank() {
        Lawyer l = validBase().name("").email("alice.doe@firm.com").build();
        assertFalse(l.getName().isBlank(), "Name should not be blank");
        assertTrue(l.getName().endsWith("*****"),
            "Derived name should end with '*****', got: " + l.getName());
    }

    @Test
    void build_usesProvidedName_whenNotBlank() {
        Lawyer l = validBase().name("Mary Jones").build();
        assertTrue(l.getName().contains("Mary"), "Should use provided name");
    }

    // ── Phone normalization ───────────────────────────────────────────────────

    @Test
    void build_normalizesPhoneToDigitsOnly() {
        Lawyer l = validBase().phone("+1 (212) 555-1234").build();
        assertTrue(l.getPhone().matches("\\d*"),
            "Phone should contain only digits, got: " + l.getPhone());
    }

    @Test
    void build_handlesEmptyPhone() {
        assertDoesNotThrow(() -> validBase().phone("").build());
    }

    // ── Specialism derivation ─────────────────────────────────────────────────

    @Test
    void build_setsSpecialismAdvisor_whenRoleIsManager() {
        Lawyer l = validBase().role("Manager").build();
        assertEquals("Advisor", l.getSpecialism());
    }

    @Test
    void build_setsSpecialismLegal_whenRoleIsPartner() {
        Lawyer l = validBase().role("Partner").build();
        assertEquals("Legal", l.getSpecialism());
    }

    @Test
    void build_setsSpecialismAdvisor_whenRoleContainsAdvisor() {
        Lawyer l = validBase().role("Senior Advisor").build();
        assertEquals("Advisor", l.getSpecialism());
    }

    // ── Null / missing fields ─────────────────────────────────────────────────

    @Test
    void build_throwsNullPointerException_whenEmailIsNull() {
        assertThrows(NullPointerException.class, () -> validBase().email(null).build());
    }

    @Test
    void build_allFieldsPresent_withValidInput() {
        Lawyer l = validBase().build();
        assertAll(
            () -> assertNotNull(l.getName()),
            () -> assertNotNull(l.getEmail()),
            () -> assertNotNull(l.getRole()),
            () -> assertNotNull(l.getFirm()),
            () -> assertNotNull(l.getCountry()),
            () -> assertNotNull(l.getSpecialism()),
            () -> assertNotNull(l.getPracticeArea()),
            () -> assertNotNull(l.getLink()),
            () -> assertNotNull(l.getPhone())
        );
    }

    // ── Edge cases ────────────────────────────────────────────────────────────

    @Test
    void build_removesDrTitleFromName() {
        Lawyer l = validBase().name("Dr. Robert Brown").build();
        assertFalse(l.getName().contains("Dr."),
            "Should remove 'Dr.' from name, got: " + l.getName());
    }

    @Test
    void build_doesNotModifyFirmName() {
        String firm = "Smith & Jones LLP";
        Lawyer l = validBase().firm(firm).build();
        assertEquals(firm.trim(), l.getFirm());
    }
}
