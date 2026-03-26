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

public class FIVERS extends ByPage {
    private final String[] links = {
            "https://www.5rs.it/en/the-people/?findByName=Partner",
            "https://www.5rs.it/en/the-people/?findByName=Of+Counsel",
            "https://www.5rs.it/en/the-people/?findByName=Counsel",
            "https://www.5rs.it/en/the-people/?findByName=Senior+Associate"
    };

    private String currentRole = "";

    private final By[] byRoleArray = {
            By.className("font-smallest")
    };

    public FIVERS() {
        super(
                "FIVERS",
                "",
                4
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.links[index]);

        this.getRole(index);

        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.xpath("//div[contains(@class, 'py-md-4')]")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{By.className("link-std")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("font-w-b")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private void getRole(int index) {
        switch (index) {
            case 0:
                this.currentRole = "partner";
                break;
            case 1, 2:
                this.currentRole = "counsel";
                break;
            case 3:
                this.currentRole = "senior associate";
                break;
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String email = lawyer.findElement(By.cssSelector("a[href^='mailto:']")).getAttribute("href");
            return new String[]{email, ""};
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
                "role", currentRole,
                "firm", this.name,
                "country", "Italy",
                "practice_area", "",
                "email", socials[0],
                "phone", "39023041331"
        );
    }
}