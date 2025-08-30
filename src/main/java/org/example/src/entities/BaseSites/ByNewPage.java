package org.example.src.entities.BaseSites;

import org.example.src.CONFIG;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class ByNewPage extends Site {

    protected ByNewPage(String name, String link, int totalPages, int maxLawyersForSite) {
        super(name, link, totalPages, maxLawyersForSite, CONFIG.BY_NEW_PAGE_FILE);
    }

    protected ByNewPage(String name, String link, int totalPages) {
        super(name, link, totalPages, 1, CONFIG.BY_NEW_PAGE_FILE);
    }



    @Override
    public void searchForLawyers(boolean showLogs) {
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
                        continue;
                    }

                    this.registerValidLawyer(lawyerDetails, index, i, showLogs);

                } catch (Exception e) {
                    System.out.printf(
                            "Error reading %dth lawyer at the page %d of the firm %s.%nError: %s%n",
                            index + 1, i + 1, this.name, e.getMessage()
                    );
                    System.out.println("#".repeat(70));
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
