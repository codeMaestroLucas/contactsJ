package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class HarperJamesSolicitors extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector(".person-tile__meta p")
    };

    public HarperJamesSolicitors() {
        super(
                "Harper James Solicitors",
                "https://harperjames.co.uk/our-people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("ccc-recommended-settings"));
        MyDriver.rollDown(1, 0.2);

        // More than 15 rolls
        MyDriver.clickOnElementMultipleTimes(
                driver.findElement(By.xpath("//*[@id=\"solicitors\"]/div[2]/div/a")),
                5, 0.5
        );
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "director", "head", "senior solicitor"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person-tile")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='https://harperjames.co.uk/our-people/']")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
            By.className("person-tile__meta"),
            By.tagName("h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getRole(WebElement lawyer) throws LawyerExceptions {
        String role = "";
        String practice = "";

        By[] byArray = {
                By.className("person-tile__meta"),
                By.tagName("p")
        };
        String att = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        role = att.split("-")[0];
        practice = att.split("-")[1];

        return new String[]{role, practice};
    }

    private String[] getSocials(String name) {
        try {
            String cleanName = TreatLawyerParams.treatNameForEmail(name);
            String[] parts = cleanName.split(" ");

            if (parts.length < 1) return new String[]{"", ""};

            String firstName = parts[0];
            String lastName = parts[parts.length - 1];

            String email = firstName + "." + lastName + "@harperjames.co.uk";

            return new String[]{email, ""};
        } catch (Exception e) {
            System.err.println("Error constructing socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(name);
        String[] rolePractice = this.getRole(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", rolePractice[0],
                "firm", this.name,
                "country", "England",
                "practice_area", rolePractice[1],
                "email", socials[0],
                "phone", "448006891700"
        );
    }
}
