package exceptions;

import org.example.exceptions.ValidationExceptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationExceptionsTest {

    // ── Factory methods return non-null instances ──────────────────────────────

    @Test
    void firmToAvoid_returnsNonNull() {
        assertNotNull(ValidationExceptions.firmToAvoid());
    }

    @Test
    void emailValidation_returnsNonNull() {
        assertNotNull(ValidationExceptions.emailValidation());
    }

    @Test
    void countryToAvoid_returnsNonNull() {
        assertNotNull(ValidationExceptions.countryToAvoid());
    }

    @Test
    void emailToAvoid_returnsNonNull() {
        assertNotNull(ValidationExceptions.emailToAvoid());
    }

    @Test
    void firmAlreadyRegisteredInMonth_returnsNonNull() {
        assertNotNull(ValidationExceptions.firmAlreadyRegisteredInMonth());
    }

    @Test
    void emailAlreadyRegistered_returnsNonNull() {
        assertNotNull(ValidationExceptions.emailAlreadyRegistered());
    }

    @Test
    void countryInSetOfCountries_returnsNonNull() {
        assertNotNull(ValidationExceptions.countryInSetOfCountries());
    }

    @Test
    void emailDuplicateOnGlobalLawExperts_returnsNonNull() {
        assertNotNull(ValidationExceptions.emailDuplicateOnGlobalLawExperts());
    }

    @Test
    void testModeActive_returnsNonNull() {
        assertNotNull(ValidationExceptions.testModeActive());
    }

    // ── Messages are non-blank ─────────────────────────────────────────────────

    @Test
    void allFactoryMethods_haveNonBlankMessages() {
        assertAll(
            () -> assertFalse(ValidationExceptions.firmToAvoid().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.emailValidation().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.countryToAvoid().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.emailToAvoid().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.firmAlreadyRegisteredInMonth().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.emailAlreadyRegistered().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.countryInSetOfCountries().getMessage().isBlank()),
            () -> assertFalse(ValidationExceptions.testModeActive().getMessage().isBlank())
        );
    }

    // ── Each factory method produces a distinct message ───────────────────────

    @Test
    void firmToAvoid_andEmailValidation_haveDifferentMessages() {
        assertNotEquals(
            ValidationExceptions.firmToAvoid().getMessage(),
            ValidationExceptions.emailValidation().getMessage()
        );
    }

    @Test
    void countryToAvoid_andEmailToAvoid_haveDifferentMessages() {
        assertNotEquals(
            ValidationExceptions.countryToAvoid().getMessage(),
            ValidationExceptions.emailToAvoid().getMessage()
        );
    }

    // ── Type hierarchy ─────────────────────────────────────────────────────────

    @Test
    void validationExceptions_extendsException() {
        assertTrue(ValidationExceptions.firmToAvoid() instanceof Exception);
    }

    @Test
    void constructor_createsInstanceWithMessage() {
        ValidationExceptions e = new ValidationExceptions("test message");
        assertEquals("test message", e.getMessage());
    }
}
