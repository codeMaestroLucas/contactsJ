package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SPAAjibadeAndCo extends ByNewPage {

    public SPAAjibadeAndCo() {
        super(
                "SPA Ajibade & Co",
                "https://spaajibade.com/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    private final String[] validRoles = {"partner", "counsel", "senior associate"};


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            WebElement div = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"all-tab\"]/div/section[1]/div/div[1]/div")));
            List<WebElement> elements = div.findElements(By.cssSelector("h2 > a[href*='https://spaajibade.com/people/']"));

            List<WebElement> filtered = new ArrayList<>();

            for (int i = 0; i < elements.size(); i++) {
                if (i % 2 != 0) {   // keep odd indexes
                    filtered.add(elements.get(i));
                }
            }

            return filtered;

        } catch (Exception e) {
            throw new RuntimeException("Error finding lawyers", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return null;
    }

    private String getName(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("h2.elementor-heading-title")};
        String name1 = extractor.extractLawyerText(div, byArray, "NAME", LawyerExceptions::nameException);
        return name1.split(",")[0];
    }

    private String getRole(WebElement div) throws LawyerExceptions {
        By[] byArray = {By.xpath("(//h2[contains(@class, 'elementor-heading-title')])[2]")};
        String role = extractor.extractLawyerText(div, byArray, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getPracticeArea(WebElement div) {
        try {
            return div.findElement(By.cssSelector("ul.sub-menu a.elementor-sub-item")).getAttribute("textContent");
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials()  {
        WebElement lawyer = driver.findElement(By.xpath("/html/body/div[1]/div/section[2]/div/div[2]/div/div[2]/div"));
        List<WebElement> socials = lawyer.findElements(By.tagName("a"));
        return super.getSocials(socials, false);
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        openNewTab(lawyer);

        WebElement header = driver.findElement(By.xpath("/html/body/div[1]/div/section[1]/div[2]/div/div/section[3]/div/div"));
        String role = this.getRole(header);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String name = getName(header);
        String practice = getPracticeArea(driver.findElement(By.tagName("body")));

        String[] socials = this.getSocials();

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Nigeria",
                "practice_area", practice,
                "email", socials[0],
                "phone", socials[1] .isEmpty() ? "2348118903060" : socials[1]
        );
    }
}
