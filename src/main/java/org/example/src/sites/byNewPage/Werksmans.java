package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class Werksmans extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("gs-member-desig")
    };


    public Werksmans() {
        super(
                "Werksmans",
                "https://www.werksmans.com/our-people/",
                1
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "head",
                "director",
                "chairman",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("gs_member_info")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.findElement(By.cssSelector("a[href^='https://werksmans.com/team-members/']")).getAttribute("href"));
    }


    public String getLink() {
        return driver.getCurrentUrl();
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("gs-sin-mem-name")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("gs-sin-mem-desig")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getPracticeArea() throws LawyerExceptions {
        WebElement lawyer = driver.findElement(By.xpath("//*[@id=\"gs_team_single\"]/div/div[2]/div[2]/ul"));
        By[] byArray = new By[]{
                By.cssSelector("ul > li")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("gstm-details"))
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("gs-team-single-content"));

        String[] socials = this.getSocials(div);
        return Map.of(
                "link", this.getLink(),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", "South Africa",
                "practice_area", this.getPracticeArea(),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}