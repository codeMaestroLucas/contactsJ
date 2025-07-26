package org.example.src.entities.BaseSites;

import org.example.src.entities.Lawyer;
import org.example.src.utils.Validations;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public abstract class ByPage extends Site {

    protected ByPage(String name, String link, int totalPages, int maxLawyersForSite) {
        //todo: passing LITERAL (1) instead of ``maxLawyersForSite`` because can't identify why no more than on lawyer has
        // been registered if i decide to collect just one for firm just need to remove the constructors ``maxLawyersForSite``

        super(name, link, totalPages, maxLawyersForSite, "byPage/");
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
                        System.out.println("Invalid role");
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

                        Lawyer lawyerToRegister = new Lawyer(
                            map.get("link"),
                            map.get("name"),
                            map.get("role"),
                            map.get("firm"),
                            map.get("country"),
                            map.get("practiceArea"),
                            map.get("email"),
                            map.get("phone")
                        );

                        //todo: maybe add a PROXY here to identify why the validation failed
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
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }


    // ABSTRACT METHODS
    @Override
    protected abstract List<WebElement> getLawyersInPage();

    @Override
    protected abstract Object getLawyer(WebElement lawyer) throws Exception;

    protected abstract void accessPage(int index) throws InterruptedException;

}
