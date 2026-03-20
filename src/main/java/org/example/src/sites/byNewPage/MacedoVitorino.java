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
import java.util.Objects;

public class MacedoVitorino extends ByNewPage {

    public MacedoVitorino() {
        super(
                "Macedo Vitorino",
                "https://www.macedovitorino.com/en/lawyers/?f_l=",
                17
        );
    }

    private final String[] letters = {
            "A", "B", "C", "D", "E", "F", "G", "H", "J", "L", "M", "N", "P", "R", "S", "T", "V"
    };

    @Override
    protected void accessPage(int index) {
        String otherUrl = this.link + letters[index];
        this.driver.get(otherUrl);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul.nav-pills li")));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a[href*='/en/lawyers/']")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h2")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("p")};
        String role = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> p = driver.findElements(By.tagName("p"));
            int index = 0 ;
            for (int i = 1; i < p.size(); i++) {
                String text = p.get(i).getText();
                if (text.contains("Email")) {
                    index = i;
                    break;
                }
            }

            String textContent = p.get(index).getAttribute("textContent");
            return super.getSocialsFromText(textContent);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.xpath("//div[5]/div[3]/div/div/div[2]/div"));
        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", role,
                "firm", this.name,
                "country", "Portugal",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "351213241900" : socials[1]
        );
    }
}
