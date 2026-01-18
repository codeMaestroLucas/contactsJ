package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ALPNGAndCo extends ByPage {
    private final By[] byRoleArray = {
            By.className("panel-collapse"),
            By.className("col-sm-8"),
            By.tagName("h5")
    };

    public ALPNGAndCo() {
        super(
                "ALP NGAndCo",
                "https://www.alp.company/our-people",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "senior associate", "managing associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("panel-default")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        // Link is in the accordion toggle
        By[] byArray = {By.className("accordion-toggle")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("panel-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.cssSelector(".person-email a, .person-telephone"));
            String email = "";
            String phone = "";

            for (WebElement el : socials) {
                String text = el.getText();
                String href = el.getAttribute("href");

                if (href != null && href.contains("mailto:")) {
                    email = href.replace("mailto:", "").trim();
                } else if (text.contains("T:")) {
                    phone = text.replace("T:", "").trim();
                }
            }
            return new String[]{email, phone};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "2342017002457" : socials[1]
        );
    }
}
