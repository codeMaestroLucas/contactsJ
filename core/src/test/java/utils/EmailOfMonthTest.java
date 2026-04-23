package utils;

import org.example.src.utils.EmailOfMonth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EmailOfMonth cache behaviour.
 *
 * EmailOfMonth uses a static Map<path, Set<email>> as an in-memory cache.
 * We clear it via reflection between tests to guarantee isolation.
 * File writes are side effects that may fail silently (stderr warning) when
 * the path doesn't exist — this is acceptable in a test context.
 */
class EmailOfMonthTest {

    @SuppressWarnings("unchecked")
    private Map<String, ?> getCache() throws Exception {
        Field field = EmailOfMonth.class.getDeclaredField("cache");
        field.setAccessible(true);
        return (Map<String, ?>) field.get(null);
    }

    @BeforeEach
    void clearCache() throws Exception {
        getCache().clear();
    }

    /** Unique fake path so tests never share state via the cache key. */
    private String fakePath() {
        return "/nonexistent/test/" + UUID.randomUUID() + "/firm.txt";
    }

    // ── isEmailRegisteredInMonth ──────────────────────────────────────────────

    @Test
    void isEmailRegisteredInMonth_returnsFalse_forFreshPath() {
        assertFalse(EmailOfMonth.isEmailRegisteredInMonth("test@example.com", fakePath()));
    }

    @Test
    void isEmailRegisteredInMonth_returnsTrue_afterRegister() {
        String path = fakePath();
        EmailOfMonth.registerEmailOfMonth("alice@law.com", path);
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("alice@law.com", path));
    }

    @Test
    void isEmailRegisteredInMonth_returnsFalse_forUnregisteredEmail_onSamePath() {
        String path = fakePath();
        EmailOfMonth.registerEmailOfMonth("registered@firm.com", path);
        assertFalse(EmailOfMonth.isEmailRegisteredInMonth("other@firm.com", path));
    }

    @Test
    void isEmailRegisteredInMonth_isCaseInsensitive() {
        String path = fakePath();
        EmailOfMonth.registerEmailOfMonth("Bob@LawFirm.com", path);
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("bob@lawfirm.com", path),
            "Check should be case-insensitive");
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("BOB@LAWFIRM.COM", path),
            "Check should be case-insensitive (all uppercase)");
    }

    @Test
    void isEmailRegisteredInMonth_treatsLeadingTrailingSpaces() {
        String path = fakePath();
        EmailOfMonth.registerEmailOfMonth("  trimmed@firm.com  ", path);
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("trimmed@firm.com", path),
            "Registration with spaces should match trimmed query");
    }

    // ── Path isolation ────────────────────────────────────────────────────────

    @Test
    void differentPaths_areIndependent() {
        String pathA = fakePath();
        String pathB = fakePath();
        EmailOfMonth.registerEmailOfMonth("shared@firm.com", pathA);
        assertFalse(EmailOfMonth.isEmailRegisteredInMonth("shared@firm.com", pathB),
            "Email registered in pathA must not appear in pathB");
    }

    @Test
    void multipleFirms_canCoexist_inCache() {
        String pathFirmA = fakePath();
        String pathFirmB = fakePath();
        EmailOfMonth.registerEmailOfMonth("alpha@a.com", pathFirmA);
        EmailOfMonth.registerEmailOfMonth("beta@b.com", pathFirmB);

        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("alpha@a.com", pathFirmA));
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("beta@b.com", pathFirmB));
        assertFalse(EmailOfMonth.isEmailRegisteredInMonth("alpha@a.com", pathFirmB));
        assertFalse(EmailOfMonth.isEmailRegisteredInMonth("beta@b.com", pathFirmA));
    }

    // ── registerEmailOfMonth ──────────────────────────────────────────────────

    @Test
    void registerEmailOfMonth_doesNotThrow_whenFileDoesNotExist() {
        // File write to a non-existent path should fail silently, not throw
        assertDoesNotThrow(() ->
            EmailOfMonth.registerEmailOfMonth("safe@test.com", fakePath())
        );
    }

    @Test
    void registerEmailOfMonth_normalizesUppercaseEmail() {
        String path = fakePath();
        EmailOfMonth.registerEmailOfMonth("UPPER@CASE.COM", path);
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("upper@case.com", path));
    }

    @Test
    void registerEmailOfMonth_multipleEmails_onSamePath() {
        String path = fakePath();
        EmailOfMonth.registerEmailOfMonth("first@firm.com", path);
        EmailOfMonth.registerEmailOfMonth("second@firm.com", path);
        EmailOfMonth.registerEmailOfMonth("third@firm.com", path);

        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("first@firm.com", path));
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("second@firm.com", path));
        assertTrue(EmailOfMonth.isEmailRegisteredInMonth("third@firm.com", path));
        assertFalse(EmailOfMonth.isEmailRegisteredInMonth("fourth@firm.com", path));
    }
}
