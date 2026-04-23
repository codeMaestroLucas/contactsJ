package utils;

import org.example.exceptions.LawyerExceptions;
import org.example.exceptions.ValidationExceptions;
import org.example.src.utils.ErrorLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ErrorLogger singleton.
 * Each test resets the logger state via reset() to ensure isolation.
 */
class ErrorLoggerTest {

    private ErrorLogger logger;

    @BeforeEach
    void setUp() {
        logger = ErrorLogger.getINSTANCE();
        logger.reset();
    }

    // ── Filtering behaviour ───────────────────────────────────────────────────

    @Test
    void log_silentlyIgnores_validationExceptions() {
        // ValidationExceptions must never be counted as extraction errors
        assertDoesNotThrow(() ->
            logger.log("FirmA", ValidationExceptions.countryToAvoid(), false)
        );
    }

    @Test
    void log_silentlyIgnores_practiceAreaExceptions() {
        // PRACTICE-category errors are intentionally suppressed (too noisy)
        LawyerExceptions e = LawyerExceptions.practiceAreaException("Corporate Law");
        assertDoesNotThrow(() -> logger.log("FirmA", e, false));
    }

    @Test
    void log_silentlyIgnores_practiceAreaExceptions_withContext() {
        LawyerExceptions e = LawyerExceptions.practiceAreaException("Litigation");
        assertDoesNotThrow(() -> logger.log("FirmA", e, false, "getLawyer context"));
    }

    // ── Recording errors ──────────────────────────────────────────────────────

    @Test
    void log_doesNotThrow_forEmailException() {
        logger.startFirm("FirmA");
        assertDoesNotThrow(() ->
            logger.log("FirmA", LawyerExceptions.emailException("bad@"), false)
        );
    }

    @Test
    void log_doesNotThrow_forLinkException() {
        logger.startFirm("FirmB");
        assertDoesNotThrow(() ->
            logger.log("FirmB", LawyerExceptions.linkException("http://x"), false)
        );
    }

    @Test
    void log_doesNotThrow_forNameException() {
        logger.startFirm("FirmC");
        assertDoesNotThrow(() ->
            logger.log("FirmC", LawyerExceptions.nameException("---"), false)
        );
    }

    @Test
    void log_doesNotThrow_forRoleException() {
        logger.startFirm("FirmD");
        assertDoesNotThrow(() ->
            logger.log("FirmD", LawyerExceptions.roleException("CEO"), false)
        );
    }

    @Test
    void log_doesNotThrow_forPhoneException() {
        logger.startFirm("FirmE");
        assertDoesNotThrow(() ->
            logger.log("FirmE", LawyerExceptions.phoneException("abc"), false)
        );
    }

    @Test
    void log_doesNotThrow_forCountryException() {
        logger.startFirm("FirmF");
        assertDoesNotThrow(() ->
            logger.log("FirmF", LawyerExceptions.countryException("Atlantis"), false)
        );
    }

    @Test
    void log_doesNotThrow_forSocialsException() {
        logger.startFirm("FirmG");
        assertDoesNotThrow(() ->
            logger.log("FirmG", LawyerExceptions.socialsException("bad"), false)
        );
    }

    @Test
    void log_withContext_doesNotThrow_forAnyLawyerException() {
        logger.startFirm("ContextFirm");
        assertDoesNotThrow(() ->
            logger.log("ContextFirm", LawyerExceptions.nameException("x"), false, "getLawyer page 2")
        );
    }

    // ── Firm tracking ─────────────────────────────────────────────────────────

    @Test
    void startFirm_doesNotThrow() {
        assertDoesNotThrow(() -> logger.startFirm("Brand New Firm"));
    }

    @Test
    void startFirm_canBeCalledMultipleTimes_forSameFirm() {
        assertDoesNotThrow(() -> {
            logger.startFirm("DupFirm");
            logger.startFirm("DupFirm");
        });
    }

    @Test
    void recordLawyerRegistered_doesNotThrow_afterStartFirm() {
        assertDoesNotThrow(() -> {
            logger.startFirm("FirmH");
            logger.recordLawyerRegistered("FirmH");
            logger.recordLawyerRegistered("FirmH");
        });
    }

    // ── Flush ─────────────────────────────────────────────────────────────────

    @Test
    void flushAllLogs_doesNotThrow_withNoErrors() {
        assertDoesNotThrow(() -> logger.flushAllLogs());
    }

    @Test
    void flushAllLogs_doesNotThrow_withSeveralErrors() {
        logger.startFirm("FirmI");
        for (int i = 0; i < 15; i++) {
            logger.log("FirmI", LawyerExceptions.nameException("bad-" + i), false);
        }
        assertDoesNotThrow(() -> logger.flushAllLogs());
    }

    @Test
    void flushFirmLogs_doesNotThrow() {
        // flushFirmLogs is a no-op in the current implementation but must not throw
        assertDoesNotThrow(() -> logger.flushFirmLogs("AnyFirm"));
    }

    // ── Reset ─────────────────────────────────────────────────────────────────

    @Test
    void reset_allowsCleanFlush_afterPreviousErrors() {
        logger.startFirm("FirmBefore");
        for (int i = 0; i < 5; i++) logger.recordLawyerRegistered("FirmBefore");
        logger.log("FirmBefore", LawyerExceptions.emailException("bad"), false);
        logger.reset();
        assertDoesNotThrow(() -> logger.flushAllLogs());
    }

    @Test
    void getInstance_returnsSameInstance() {
        assertSame(ErrorLogger.getINSTANCE(), ErrorLogger.getINSTANCE());
    }
}
