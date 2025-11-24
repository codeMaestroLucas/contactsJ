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

public class MooreLegalKovacs extends ByPage {
    private final By[] byRoleArray = {
            By.className("widget-description")
    };

    public MooreLegalKovacs() {
        super(
                "Moore Legal Kovács",
                "https://www.mooreglobal.hu/vezetoink?lang=en-gb",
                1
        );
    }

    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "director"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.row > div.col-12 > div.msCategory")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("widget-title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String text = lawyer.findElement(By.className("widget-description")).getText();
        return text.split("\n")[0].trim();
    }

    private String[] getSocials(WebElement lawyer) {
        String email = "";
        String phone = "";

        String[] socials = lawyer.findElement(By.className("widget-description"))
                .getAttribute("outerHTML")
                .split("<br>");

        for (String social : socials) {
            if ((social.contains("mail") || social.contains("@")) && email.isEmpty()) {
                String[] split = social.split("href");
                int indexInit = split[1].indexOf("\"");
                int indexEnd = split[1].lastIndexOf("\"");
                email = split[1].substring(indexInit + 1, indexEnd);

            } else if (phone.isEmpty()) {
                String cleaned = social.replaceAll("[^0-9]", "");
                if (cleaned.length() >= 7) phone = cleaned;
            }

            if (!email.isEmpty() && !phone.isEmpty()) break;
        }

        return new String[]{ email, phone };
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Hungary",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "36703237787" : socials[1]
        );
    }
}