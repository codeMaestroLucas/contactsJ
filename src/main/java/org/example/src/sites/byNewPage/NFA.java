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

public class NFA extends ByNewPage {

    public NFA() {
        super(
                "NFA",
                "https://negraoferrari.com.br/en/profissionais/",
                1
        );
    }

    private final String[] validRoles = {"partner", "counsel", "senior associate"};


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("profissional")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2")};
        return extractor.extractLawyerText(subPage, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.className("profissionalBio"), By.tagName("p")};
        String role = extractor.extractLawyerText(subPage, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String[] getSocials(WebElement subPage) {
        try {
            List<WebElement> anchors = subPage.findElements(By.cssSelector("#perfilContato a"));
            return super.getSocials(anchors, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String subLink = this.openNewTab(lawyer);
        WebElement subPage = driver.findElement(By.id("perfilInfo"));
        String[] socials = this.getSocials(subPage);

        String role = this.getRole(subPage);
        if (role.equals("Invalid Role")) return "Invalid Role";

        return Map.of(
                "link", subLink,
                "name", this.getName(subPage),
                "role", role,
                "firm", this.name,
                "country", "Brazil",
                "practice_area", "",
                "email", socials[0].replace("(PT)", "").trim(),
                "phone", "551130470777"
        );
    }
}
