package org.example.src.entities.BaseSites;

import org.example.src.CONFIG;
import org.example.src.entities.Lawyer;
import org.example.src.entities.MyDriver;
import org.example.src.utils.Validations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class ByNewPage extends Site {

    protected ByNewPage(String name, String link, int totalPages, int maxLawyersForSite) {
        super(name, link, totalPages, maxLawyersForSite, CONFIG.BY_NEW_PAGE_FILE);
    }

    protected ByNewPage(String name, String link, int totalPages) {
        super(name, link, totalPages, 1, CONFIG.BY_NEW_PAGE_FILE);
    }



    @Override
    public void searchForLawyers() {
        for (int i = 0; i < this.totalPages; i++) {
            System.out.printf("Page %d - - - - - - - - - - ( %d )%n", i + 1, this.totalPages);

            try {
                this.accessPage(i);

            } catch (Exception e) {
                System.out.println("Error accessing page " + (i + 1) + ": " + e.getMessage());
                continue; // Skip this page on error
            }

            List<WebElement> lawyersInPage;
            try {
                lawyersInPage = this.getLawyersInPage();

            } catch (Exception e) {
                System.out.println("Error fetching lawyers on page " + (i + 1) + ": " + e.getMessage());
                continue;
            }

            if (lawyersInPage == null || lawyersInPage.isEmpty()) {
                System.out.printf("No search results found on page %d of the firm %s%n",
                        i + 1, this.name);
                continue; // Skip this page
            }

            // Iterate through lawyers
            for (int index = 0; index < lawyersInPage.size(); index++) {
                WebElement lawyer = lawyersInPage.get(index);

                try {
                    Object lawyerDetails = getLawyer(lawyer);

                    // Invalid Lawyer
                    if (lawyerDetails instanceof String) {
//                        System.out.println("Invalid role"); // to annoying
                        continue;
                    }

                    // Valid Lawyer
                    if (lawyerDetails instanceof Map<?, ?>) {
                        Map<String, String> map = (Map<String, String>) lawyerDetails;

                        if (map.get("link") == null || map.get("link").isEmpty() ||
                            map.get("email") == null || map.get("email").isEmpty()) {
                            siteUtl.printInvalidLawyer(map, index, i, this.name);
                            continue;
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


                        boolean canRegister = Validations.makeValidations(
                                lawyerToRegister,
                                this.lastCountries,
                                this.emailsOfMonthPath,
                                this.emailsToAvoidPath
                        );

                        if (!canRegister) continue;

                        this.addLawyer(lawyerToRegister);

                        if (this.lawyersRegistered == this.maxLawyersForSite) {
                            System.out.printf("No more than %d lawyer(s) needed for the firm %s.%n",
                                    this.maxLawyersForSite, this.name);
                            return;
                        }

                    } else {
                        System.out.println("Invalid lawyer data structure.");
                    }

                } catch (Exception e) {
                    System.out.printf(
                            "Error reading %dth lawyer at the page %d of the firm %s.%nError: %s%n",
                            index + 1, i + 1, this.name, e.getMessage()
                    );
                    System.out.println("#".repeat(70));
                    e.getMessage();
                    e.printStackTrace();
                    System.out.println("#".repeat(70) + "\n");
                } finally {
                    MyDriver.closeCurrentTab();
                }
            }
        }
    }


    // ABSTRACT METHODS
    @Override
    protected abstract List<WebElement> getLawyersInPage();

    public abstract void openNewTab(WebElement lawyer);

    @Override
    protected abstract Object getLawyer(WebElement lawyer) throws Exception;

    protected abstract void accessPage(int index) throws InterruptedException;

}
