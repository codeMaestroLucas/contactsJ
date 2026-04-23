package utils;

import org.example.src.utils.FirmsExhausted;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FirmsExhausted.
 *
 * The class uses a static in-memory HashSet loaded at class initialization.
 * We access it via reflection to reset between tests — the file write is a
 * side effect that may produce a stderr warning when the path doesn't exist,
 * which is acceptable in a test context.
 */
class FirmsExhaustedTest {

    @SuppressWarnings("unchecked")
    private Set<String> getExhaustedSet() throws Exception {
        Field field = FirmsExhausted.class.getDeclaredField("exhaustedSet");
        field.setAccessible(true);
        return (Set<String>) field.get(null);
    }

    @BeforeEach
    void clearInMemorySet() throws Exception {
        getExhaustedSet().clear();
    }

    // ── isFirmExhausted ───────────────────────────────────────────────────────

    @Test
    void isFirmExhausted_returnsFalse_forUnregisteredFirm() {
        assertFalse(FirmsExhausted.isFirmExhausted("NonExistentFirm_" + UUID.randomUUID()));
    }

    @Test
    void isFirmExhausted_returnsTrue_afterRegister() {
        String firm = "TestFirm_" + UUID.randomUUID();
        FirmsExhausted.register(firm);
        assertTrue(FirmsExhausted.isFirmExhausted(firm));
    }

    @Test
    void isFirmExhausted_isCaseInsensitive() {
        FirmsExhausted.register("Smith & Jones LLP");
        assertTrue(FirmsExhausted.isFirmExhausted("SMITH & JONES LLP"),
            "Check should be case-insensitive (uppercase)");
        assertTrue(FirmsExhausted.isFirmExhausted("smith & jones llp"),
            "Check should be case-insensitive (lowercase)");
    }

    @Test
    void isFirmExhausted_handlesTrimmedInput() {
        FirmsExhausted.register("  Padded Firm  ");
        assertTrue(FirmsExhausted.isFirmExhausted("Padded Firm"),
            "Should match after trimming registration whitespace");
        assertTrue(FirmsExhausted.isFirmExhausted("  Padded Firm  "),
            "Should match even when query has whitespace");
    }

    @Test
    void isFirmExhausted_returnsFalse_forEmptySet() throws Exception {
        // Set is already cleared in @BeforeEach
        assertFalse(FirmsExhausted.isFirmExhausted("AnyFirm"));
    }

    // ── register ──────────────────────────────────────────────────────────────

    @Test
    void register_doesNotDuplicateEntry_inMemory() throws Exception {
        String firm = "Dup Firm " + UUID.randomUUID();
        FirmsExhausted.register(firm);
        FirmsExhausted.register(firm);
        FirmsExhausted.register(firm.toUpperCase());

        long count = getExhaustedSet().stream()
                .filter(f -> f.equals(firm.toLowerCase().trim()))
                .count();
        assertEquals(1, count, "Same firm should appear only once in the set");
    }

    @Test
    void register_canRegisterMultipleDifferentFirms() throws Exception {
        String firmA = "FirmAlpha_" + UUID.randomUUID();
        String firmB = "FirmBeta_" + UUID.randomUUID();
        FirmsExhausted.register(firmA);
        FirmsExhausted.register(firmB);

        assertTrue(FirmsExhausted.isFirmExhausted(firmA));
        assertTrue(FirmsExhausted.isFirmExhausted(firmB));
    }

    @Test
    void register_doesNotThrow_whenFileDoesNotExist() {
        // The file write may fail silently (prints to stderr), but must not throw
        assertDoesNotThrow(() -> FirmsExhausted.register("SafeRegisterFirm_" + UUID.randomUUID()));
    }
}
