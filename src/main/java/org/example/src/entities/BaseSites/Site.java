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

    /**
     * Creates the emailsOfMonthPath and emailsToAvoidPath based on the given name.
     * It sanitizes the name by trimming spaces and removing all whitespace.
     *
     */
    private void generateEmailPaths(String folderPath) {
        String sanitizedPath = this.name.trim().replaceAll("\\s+", "");
        this.emailsOfMonthPath = CONFIG.EMAILS_MONTH_FOLDER_FILE + folderPath + sanitizedPath + ".txt";
        this.emailsToAvoidPath = CONFIG.EMAILS_TO_AVOID_FOLDER_FILE + folderPath + sanitizedPath + ".txt";

        ensureFileExists(this.emailsOfMonthPath);
        ensureFileExists(this.emailsToAvoidPath);
    }


    /**
     * Ensures that the file exists, creating it if necessary (including parent directories).
     *
     * @param path Path to the file.
     */
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
            } else {
            }
        } catch (IOException e) {
            throw new RuntimeException("Error ensuring file exists: " + path, e);
        }
    }


    /**
     * Function used to get the email and phone.
     *
     * @param socials web list of all the socials values to be iterated
     * @param byText  If true, extract text. If false, use the 'href' attribute.
     * @return Array[0] == email; Array[1] == phone
     */
    protected String[] getSocials(List<WebElement> socials, boolean byText) {
        String email = "";
        String phone = "";

        for (WebElement social : socials) {
            String value = byText
                    ? social.getText().toLowerCase().trim()
                    : Objects.requireNonNull(social.getAttribute("href")).toLowerCase().trim();

            // Check if it's an email
            if ((value.contains("mail") || value.contains("@")) && email.isEmpty()) {
                email = value;
            }

            // Check if it's a valid phone number
            else if ((
                    value.contains("tel") || value.contains("cal") || value.contains("+") || value.contains("phone") ||
                            value.matches(".*\\d{5,}.*")) && phone.isEmpty()) {
                String cleaned = value.replaceAll("[^0-9]", "");

                if (cleaned.length() > 5) { // To prevent if an invalid value has been set to phone
                    phone = cleaned;
                }
            }

            if (!email.isEmpty() && !phone.isEmpty()) break;
        }

        return new String[]{email, phone};
    }


    protected void addLawyer(Lawyer lawyer) {
        Sheet sheet = Sheet.getINSTANCE();
        sheet.addLawyer(lawyer, true);

        EmailOfMonth.registerEmailOfMonth(lawyer.email, this.emailsOfMonthPath);

        String country = lawyer.country;
        if (!country.equals("Not Found") || !country.equals("-----")) {
            this.lastCountries.add(country);
        }

        this.lawyersRegistered++;
    }


    protected void registerValidLawyer(Object lawyerDetails, int index, int i, boolean showLogs) {
        if (lawyerDetails instanceof Map<?, ?>) {
            Map<String, String> map = (Map<String, String>) lawyerDetails;

            if (map.get("link") == null || map.get("link").isEmpty() ||
                map.get("email") == null || map.get("email").isEmpty())
            {
                siteUtl.printInvalidLawyer(map);
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
                        this.lastCountries,
                        this.emailsOfMonthPath,
                        this.emailsToAvoidPath
                );

                this.addLawyer(lawyerToRegister);

                if (this.lawyersRegistered == this.maxLawyersForSite) {
                    System.out.printf("No more than %d lawyer(s) needed for the firm %s.%n",
                            this.maxLawyersForSite, this.name);
                }

            } catch (ValidationExceptions e) {
                if (showLogs) {
                    System.err.println("#".repeat(70));
                    System.err.printf("Error reading %dth lawyer at page %d of firm %s.%nSkipping...%n",
                            index + 1, i + 1, this.getName());
                    System.err.println("Validation error " + e.getMessage());
                    System.err.println("#".repeat(70) + "\n");

                    siteUtl.printInvalidLawyer(map);
                }

            }

        } else {
            System.out.println("Invalid lawyer data structure.");
        }
    }


    // ABSTRACT METHODS

    /**
     * Collect all lawyers in the current page.
     */
    protected abstract List<WebElement> getLawyersInPage();

    /**
     * Get the lawyer info.
     *
     * @return HashMap<String, String> if the Lawyer Data is found || String == "Not Found" if no Lawyer Data.
     */
    protected abstract Object getLawyer(WebElement lawyer) throws Exception;

    public abstract void searchForLawyers(boolean showLogs) throws Exception;
}
