package org.example.src.utils;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for downloading and parsing VCard (.vcf) files.
 * <p>
 * Injectable into any {@code ByPage} or {@code ByNewPage} firm class as a field.
 * Because different sites generate VCards with different field structures,
 * the constructor requires explicit regex patterns for email and phone extraction.
 * Each pattern must contain <b>exactly one capturing group</b> that isolates the value.
 *
 * <h3>Typical VCard line formats</h3>
 * <pre>
 *   EMAIL;PREF;INTERNET:someone@example.com
 *   TEL;WORK;VOICE:+44 20 1234 5678
 *   TEL;CELL;VOICE:+55 49 999 000 000
 * </pre>
 *
 * <h3>Usage example inside a firm class</h3>
 * <pre>{@code
 *   // Using the built-in defaults (matches standard VCard 2.1 / 3.0 fields)
 *   private final VCard vCard = VCard.withDefaultPatterns();
 *
 *   // — or — supply custom patterns when the site uses a non-standard structure
 *   private final VCard vCard = new VCard(
 *       "X-EMAIL[^:\\r\\n]*:([^\\r\\n]+)",   // email pattern
 *       "X-TEL[^:\\r\\n]*:([^\\r\\n]+)"      // phone pattern
 *   );
 *
 *   // Inside getLawyer():
 *   String vcardHref = lawyer.findElement(By.cssSelector("a.vcard")).getAttribute("href");
 *   String[] socials  = vCard.getSocials(vcardHref);          // plain HTTP
 *   // — or — if the endpoint needs the active browser session's cookies:
 *   String[] socials  = vCard.getSocials(driver, vcardHref);  // with session cookies
 *   String email = socials[0];
 *   String phone = socials[1];
 * }</pre>
 */
public class VCard {

    /**
     * Default email pattern — matches lines like:
     * {@code EMAIL:someone@firm.com} or {@code EMAIL;PREF;INTERNET:someone@firm.com}
     */
    public static final String DEFAULT_EMAIL_PATTERN = "EMAIL[^:\\r\\n]*:([^\\r\\n]+)";

    /**
     * Default phone pattern — matches lines like:
     * {@code TEL:+44 20 1234 5678} or {@code TEL;WORK;VOICE:+44 20 1234 5678}
     */
    public static final String DEFAULT_PHONE_PATTERN = "TEL[^:\\r\\n]*:([^\\r\\n]+)";

    private final Pattern emailPattern;
    private final Pattern phonePattern;

    // ── construction ─────────────────────────────────────────────────────────

    /**
     * Creates a {@code VCard} parser with custom regex patterns.
     *
     * @param emailPattern Regex with one capturing group for the e-mail value.
     *                     Example: {@code "EMAIL[^:\\r\\n]*:([^\\r\\n]+)"}
     * @param phonePattern Regex with one capturing group for the phone value.
     *                     Example: {@code "TEL[^:\\r\\n]*:([^\\r\\n]+)"}
     */
    public VCard(String emailPattern, String phonePattern) {
        this.emailPattern = Pattern.compile(emailPattern, Pattern.CASE_INSENSITIVE);
        this.phonePattern = Pattern.compile(phonePattern, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Factory method that returns a {@code VCard} instance pre-configured with the
     * standard VCard 2.1 / 3.0 patterns. Suitable for most law-firm websites.
     */
    public static VCard withDefaultPatterns() {
        return new VCard(DEFAULT_EMAIL_PATTERN, DEFAULT_PHONE_PATTERN);
    }

    // ── public API ───────────────────────────────────────────────────────────

    /**
     * Downloads the VCF file at {@code vcardUrl} via a plain HTTP GET request
     * (no browser session required) and extracts email and phone.
     * <p>
     * Use this overload when the VCF endpoint is publicly accessible.
     *
     * @param vcardUrl The full URL of the {@code .vcf} file.
     * @return {@code String[]{ email, phone }} — empty strings if extraction fails.
     */
    public String[] getSocials(String vcardUrl) {
        try {
            String content = fetchContent(vcardUrl, null);
            return parse(content);
        } catch (Exception e) {
            System.err.println("VCard.getSocials: error fetching \"" + vcardUrl + "\" — " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    /**
     * Downloads the VCF file forwarding the cookies from the active Selenium session.
     * <p>
     * Use this overload when the VCF endpoint requires authentication or session state
     * (e.g., the lawyer profile is behind a login wall).
     * The WebDriver is <b>never</b> navigated; only its cookies are copied to the HTTP request.
     *
     * @param driver   The active {@code WebDriver} session (cookies are read, not modified).
     * @param vcardUrl The full URL of the {@code .vcf} file.
     * @return {@code String[]{ email, phone }} — empty strings if extraction fails.
     */
    public String[] getSocials(WebDriver driver, String vcardUrl) {
        try {
            Set<Cookie> cookies = driver.manage().getCookies();
            String content = fetchContent(vcardUrl, cookies);
            return parse(content);
        } catch (Exception e) {
            System.err.println("VCard.getSocials (with session): error fetching \"" + vcardUrl + "\" — " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    /**
     * Parses VCF text that has already been loaded into memory.
     * <p>
     * Use this overload when the firm class obtains the VCF content through other means
     * (e.g., reading a downloaded file, extracting text from a page element, etc.).
     * <p>
     * The extracted values are automatically cleaned via
     * {@link TreatLawyerParams#treatEmail} and {@link TreatLawyerParams#treatPhone}.
     *
     * @param vcfContent Raw text content of the {@code .vcf} file.
     * @return {@code String[]{ email, phone }} — empty strings if nothing is matched.
     */
    public String[] parse(String vcfContent) {
        if (vcfContent == null || vcfContent.isBlank()) {
            return new String[]{"", ""};
        }

        String email = extractFirst(emailPattern, vcfContent);
        String phone  = extractFirst(phonePattern,  vcfContent);

        return new String[]{
            TreatLawyerParams.treatEmail(email),
            TreatLawyerParams.treatPhone(phone)
        };
    }

    // ── private helpers ───────────────────────────────────────────────────────

    /**
     * Returns the content of the first capturing group of {@code pattern}
     * found in {@code content}, or an empty string if there is no match.
     */
    private String extractFirst(Pattern pattern, String content) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.groupCount() > 0
                    ? matcher.group(1).trim()
                    : matcher.group(0).trim();
        }
        return "";
    }

    /**
     * Performs an HTTP GET to {@code vcardUrl} and returns the response body as a string.
     * When {@code cookies} is non-null, they are forwarded in the {@code Cookie} header
     * to simulate the active browser session.
     *
     * @param vcardUrl URL to fetch.
     * @param cookies  Optional set of Selenium cookies to forward; may be {@code null}.
     * @return Response body text.
     * @throws Exception on network or HTTP errors.
     */
    private String fetchContent(String vcardUrl, Set<Cookie> cookies) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(vcardUrl))
                .header("User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .GET();

        if (cookies != null && !cookies.isEmpty()) {
            String cookieHeader = cookies.stream()
                    .map(c -> c.getName() + "=" + c.getValue())
                    .collect(Collectors.joining("; "));
            requestBuilder.header("Cookie", cookieHeader);
        }

        HttpResponse<String> response = client.send(
                requestBuilder.build(),
                HttpResponse.BodyHandlers.ofString()
        );

        int status = response.statusCode();
        if (status < 200 || status >= 300) {
            throw new RuntimeException("HTTP " + status + " received from " + vcardUrl);
        }

        return response.body();
    }
}
