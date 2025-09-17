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

public class FluegelPreissner extends ByPage {

    private final By[] byRoleArray = {
            By.className("ult-ih-heading")
    };

    public FluegelPreissner() {
        super(
                "Flügel Preissner",
                "https://fluegelpreissner.com/en/start/#!/team",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(2000L); // Wait for animations
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner", "counsel"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"team\"]/div[2]/div/div")
                    )
            );
            List<WebElement> lawyers = div.findElements(By.cssSelector("a.ult-ih-link"));
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, false, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getNameFromLink(String link) {
        try {
            String namePart = link.split("/en/")[1].replace("/", "").replace("-en", "");
            String[] nameParts = namePart.split("-");
            StringBuilder formattedName = new StringBuilder();
            for (String part : nameParts) {
                formattedName.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
            return formattedName.toString().trim();
        } catch (Exception e) {
            return "Name not found";
        }
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {
                By.cssSelector("h3.ult-ih-heading")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            String emailText = lawyer.findElement(By.className("mail")).getAttribute("textContent");
            String email = emailText.replace("[at]", "@").trim();
            return new String[]{email, ""};
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String _link = lawyer.getAttribute("href");

        lawyer = lawyer.findElement(By.className("ult-ih-content"));

        String name = this.getNameFromLink(_link);
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", _link.isEmpty() ? this.link : _link,
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", "Germany",
                "practice_area", "",
                "email", socials[0],
                "phone", "49895205730"
        );
    }
}