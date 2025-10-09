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

public class NovaLaw extends ByPage {
    private final By[] byRoleArray = {
            By.xpath("(//h2)[2]")
    };

    public NovaLaw() {
        super(
                "Nova Law",
                "https://novalaw.no/menneskene/",
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
        String[] validRoles = {"partner"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));

            List<WebElement> lawyerBlocks = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//*[@id='content']//div[contains(@class,'e-con-full') and contains(@class,'elementor-element')]")
                    )
            );
            List<WebElement> lawyers = siteUtl.filterLawyersInPage(lawyerBlocks, byRoleArray, false, validRoles);
            return lawyers.subList(0, lawyers.size() - 2); // Remove the last one
        }
        catch (Exception e) {
            System.out.println("Failed to find lawyer elements");
            return null;
        }
    }


    private String getLink(WebElement lawyer) throws LawyerExceptions {
        String href = lawyer.findElement(By.className("jet-engine-listing-overlay-wrap")).getAttribute("data-url");
        if (href == null || href.isEmpty()) {
            throw LawyerExceptions.linkException(href);
        }
        return href;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.xpath("(//h2)[1]")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, byRoleArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
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
                "country", "Norway",
                "practice_area", "",
                "email", socials[0].replace("%20", ""),
                "phone", socials[1].isEmpty() ? "4722007950" : socials[1]
        );
    }
}