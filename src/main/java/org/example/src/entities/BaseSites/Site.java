package org.example.src.entities.BaseSites;

import lombok.Data;
import org.example.exceptions.ValidationExceptions;
import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;
import org.example.src.entities.MyDriver;
import org.example.src.entities.excel.Sheet;
import org.example.src.utils.EmailOfMonth;
import org.example.src.utils.Extractor;
import org.example.src.utils.Validations;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Data
public abstract class Site {
    public final String name;
    protected final String link;
    public int lawyersRegistered;
    protected final int totalPages;
    public final int maxLawyersForSite;
    protected Set<String> lastCountries = new HashSet<>();
    public final WebDriver driver = MyDriver.getINSTANCE();
    protected final SiteUtils siteUtl = SiteUtils.getINSTANCE();
    protected final Extractor extractor = Extractor.getINSTANCE();
    protected String emailsOfMonthPath;
    protected String emailsToAvoidPath;

    protected static Map<String, String> OFFICE_TO_COUNTRY;
    protected static String[] validRoles;

    protected Site(String name, String link, int totalPages, int maxLawyersForSite, String path) {
        this.name = name;
        this.link = link;
        this.totalPages = totalPages;
        this.maxLawyersForSite = maxLawyersForSite;

        this.generateEmailPaths(path);
    }

    protected Site(String name, String link, int maxLawyersForSite, String path) {
        this.name = name;
        this.link = link;
        this.totalPages = 1;
        this.maxLawyersForSite = maxLawyersForSite;

        this.generateEmailPaths(path);
    }

    private void generateEmailPaths(String folderPath) {
        String sanitizedPath = this.name.trim().replaceAll("\\s+", "");
        this.emailsOfMonthPath = CONFIG.EMAILS_MONTH_FOLDER_FILE + folderPath + sanitizedPath + ".txt";
        this.emailsToAvoidPath = CONFIG.EMAILS_TO_AVOID_FOLDER_FILE + folderPath + sanitizedPath + ".txt";

        ensureFileExists(this.getEmailsOfMonthPath());
        ensureFileExists(this.getEmailsToAvoidPath());
    }

    private static void ensureFileExists(String path) {
        File file = new File(path);
        try {
            if (!file.exists()) {
                boolean dirsCreated = file.getParentFile().mkdirs();

                if (!dirsCreated && !file.getParentFile().exists()) {
                    throw new IOException("Could not create directories: " + file.getParent());
                }
                boolean fileCreated = file.createNewFile();
                if (!fileCreated) {
                    throw new IOException("Could not create file: " + file.getAbsolutePath());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error ensuring file exists: " + path, e);
        }
    }

    protected String[] getSocials(List<WebElement> socials, boolean byText) {
        String email = "";
        String phone = "";

        for (WebElement social : socials) {
            String value = byText
                    ? social.getText().toLowerCase().trim()
                    : Objects.requireNonNull(social.getAttribute("href")).toLowerCase().trim();

            if ((value.contains("mail") || value.contains("@")) && email.isEmpty()) {
                email = value;

                } else if (phone.isEmpty()) {
                // Remove all non-digits values
                String cleaned = value.replaceAll("[^0-9]", "");
                if (cleaned.length() > 5) phone = cleaned;
            }

            if (!email.isEmpty() && !phone.isEmpty()) break;
        }

        return new String[]{email, phone};
    }

    protected void addLawyer(Lawyer lawyer) {
        Sheet sheet = Sheet.getINSTANCE();
        sheet.addLawyer(lawyer, true);

        EmailOfMonth.registerEmailOfMonth(lawyer.getEmail(), this.getEmailsOfMonthPath());

        String country = lawyer.getCountry();
        // Fixed: Use && instead of ||
        if (!country.equals("Not Found") && !country.equals("-----")) {
            this.lastCountries.add(country);
        }

        this.lawyersRegistered++;
    }

    // Updated to return boolean to indicate if processing should stop
    protected boolean registerValidLawyer(Object lawyerDetails, int index, int i, boolean showLogs) {
        if (lawyerDetails instanceof Map<?, ?>) {
            Map<String, String> map = (Map<String, String>) lawyerDetails;

            if (map.get("link") == null || map.get("link").isEmpty() ||
                    map.get("email") == null || map.get("email").isEmpty()) {
                siteUtl.printInvalidLawyer(map);
                return false; // Continue processing
            }

            Lawyer lawyerToRegister = Lawyer.builder()
                    .link(map.get("link"))
                    .name(map.get("name"))
                    .email(map.get("email"))
                    .phone(map.get("phone"))
                    .country(map.get("country"))
                    .role(map.get("role"))
                    .firm(map.get("firm"))
                    .practiceArea(map.get("practice_area"))
                    .build();

            try {
                Validations.makeValidations(
                        lawyerToRegister,
                        this.getLastCountries(),
                        this.getEmailsOfMonthPath(),
                        this.getEmailsToAvoidPath()
                );

                this.addLawyer(lawyerToRegister);

                if (this.getLawyersRegistered() >= this.getMaxLawyersForSite()) {
                    System.out.printf("No more than %d lawyer(s) needed for the firm %s.%n",
                            this.getMaxLawyersForSite(), this.getName());
                    return true; // Signal to stop processing
                }

            } catch (ValidationExceptions e) {
                if (showLogs) {
                    System.err.printf("Error reading %dth lawyer at page %d of firm %s.%nSkipping...%n",
                            index + 1, i + 1, this.name);
                    System.err.println("Validation error " + e.getMessage());

                    siteUtl.printInvalidLawyer(map);

                }
            }
        } else {
            System.out.println("Invalid lawyer data structure.");
        }

        return false; // Continue processing
    }

    // ABSTRACT METHODS
    protected abstract List<WebElement> getLawyersInPage();
    protected abstract Object getLawyer(WebElement lawyer) throws Exception;
    public abstract Runnable searchForLawyers(boolean showLogs) throws Exception;
}
