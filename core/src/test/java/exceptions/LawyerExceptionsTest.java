package exceptions;

import org.example.exceptions.LawyerExceptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LawyerExceptionsTest {

    // ── Category mapping ──────────────────────────────────────────────────────

    @Test
    void linkException_hasLinkCategory() {
        LawyerExceptions e = LawyerExceptions.linkException("http://bad.url");
        assertEquals(LawyerExceptions.Category.LINK, e.category);
    }

    @Test
    void nameException_hasNameCategory() {
        assertEquals(LawyerExceptions.Category.NAME,
            LawyerExceptions.nameException("---").category);
    }

    @Test
    void emailException_hasEmailCategory() {
        assertEquals(LawyerExceptions.Category.EMAIL,
            LawyerExceptions.emailException("notanemail").category);
    }

    @Test
    void phoneException_hasPhoneCategory() {
        assertEquals(LawyerExceptions.Category.PHONE,
            LawyerExceptions.phoneException("abc").category);
    }

    @Test
    void roleException_hasRoleCategory() {
        assertEquals(LawyerExceptions.Category.ROLE,
            LawyerExceptions.roleException("Astronaut").category);
    }

    @Test
    void countryException_hasCountryCategory() {
        assertEquals(LawyerExceptions.Category.COUNTRY,
            LawyerExceptions.countryException("Narnia").category);
    }

    @Test
    void practiceAreaException_hasPracticeCategory() {
        assertEquals(LawyerExceptions.Category.PRACTICE,
            LawyerExceptions.practiceAreaException("Alchemy").category);
    }

    @Test
    void socialsException_hasSocialsCategory() {
        assertEquals(LawyerExceptions.Category.SOCIALS,
            LawyerExceptions.socialsException("bad-social-data").category);
    }

    @Test
    void defaultConstructor_hasUnknownCategory() {
        LawyerExceptions e = new LawyerExceptions("generic error");
        assertEquals(LawyerExceptions.Category.UNKNOWN, e.category);
    }

    // ── Message preservation ──────────────────────────────────────────────────

    @Test
    void linkException_messageContainsProvidedValue() {
        String badUrl = "http://not.found/lawyer";
        LawyerExceptions e = LawyerExceptions.linkException(badUrl);
        assertTrue(e.getMessage().contains(badUrl),
            "Message should contain the bad URL, got: " + e.getMessage());
    }

    @Test
    void emailException_messageContainsProvidedValue() {
        String badEmail = "not-an-email";
        LawyerExceptions e = LawyerExceptions.emailException(badEmail);
        assertTrue(e.getMessage().contains(badEmail));
    }

    @Test
    void countryException_messageContainsProvidedValue() {
        String country = "Narnia";
        LawyerExceptions e = LawyerExceptions.countryException(country);
        assertTrue(e.getMessage().contains(country));
    }

    // ── Enum structure ────────────────────────────────────────────────────────

    @Test
    void category_enumContainsAllExpectedValues() {
        LawyerExceptions.Category[] values = LawyerExceptions.Category.values();
        assertAll(
            () -> assertTrue(contains(values, LawyerExceptions.Category.LINK)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.NAME)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.EMAIL)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.PHONE)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.ROLE)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.COUNTRY)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.PRACTICE)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.SOCIALS)),
            () -> assertTrue(contains(values, LawyerExceptions.Category.UNKNOWN))
        );
    }

    private boolean contains(LawyerExceptions.Category[] arr, LawyerExceptions.Category target) {
        for (LawyerExceptions.Category c : arr) if (c == target) return true;
        return false;
    }

    // ── Instanceof check ──────────────────────────────────────────────────────

    @Test
    void lawyerExceptions_extendsException() {
        LawyerExceptions e = LawyerExceptions.linkException("x");
        assertTrue(e instanceof Exception);
    }
}
