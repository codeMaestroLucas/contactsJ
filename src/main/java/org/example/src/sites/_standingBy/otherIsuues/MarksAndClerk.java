package org.example.src.sites._standingBy.otherIsuues;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MarksAndClerk extends ByNewPage {
    private final By[] byRoleArray = {
            By.className(""),
            By.cssSelector("")
    };

    public MarksAndClerk() {
        super(
            "Marks And Clerk",
            "https://www.marks-clerk.com/our-people/",
            18,
            3
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index == 0) {
            MyDriver.clickOnAddBtn(By.cssSelector("button[title='Accept all cookies']"));
        } else {
            WebElement nextButton = driver.findElement(By.cssSelector("a.page-link.page-next"));

            // Use JavascriptExecutor to click the element
            JavascriptExecutor executor = (JavascriptExecutor)driver;
            executor.executeScript("arguments[0].scrollIntoView(true);", nextButton); // Scroll button into view
            executor.executeScript("arguments[0].click();", nextButton); // Perform the click
            Thread.sleep(5000L);



        }

    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "principal",
                "director",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            return null;
//            List<WebElement> lawyers = wait.until(
//                    ExpectedConditions.presenceOfAllElementsLocatedBy(
//                            By.className("")
//                    )
//            );
//            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className(""),
                By.cssSelector("")
        };
        return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className(""))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className(""));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
