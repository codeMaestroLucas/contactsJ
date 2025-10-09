package org.example.src.sites._standingBy.otherIsuues;

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

public class AmitPollakMatalonAndCo extends ByPage {

    public AmitPollakMatalonAndCo() {
        super(
                "Amit, Pollak, MatalonAndCo",
                "https://www.apm.law/team/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        WebElement roleOpt = driver.findElement(By.id("team-search-position-options-2"));
        roleOpt
                .findElement(By.cssSelector("div.option[data-target=\"33\"]"))
                .click(); // Senior associate
        MyDriver.clickOnElement(By.xpath("//*[@id=\"form-search-team\"]/button"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("member")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerAttribute(lawyer, null, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.className("title")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";
        try {
            String detailsText = lawyer.findElement(By.className("details")).getText();
            String[] parts = detailsText.split("\\|");
            for (String part : parts) {
                if (part.trim().startsWith("E:")) {
                    email = part.replace("E:", "").trim();
                } else if (part.trim().startsWith("T:")) {
                    phone = part.replace("T:", "").trim();
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{email, phone};
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", "Israel",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "97235689000" : socials[1]
        );
    }
}