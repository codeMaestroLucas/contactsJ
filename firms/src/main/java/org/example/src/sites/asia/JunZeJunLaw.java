package org.example.src.sites.asia;

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

public class JunZeJunLaw extends ByNewPage {

    public JunZeJunLaw() {
        super(
                "JunZeJun Law",
                "https://www.junzejun.com/en/Professionals/search.html?text=&ss_ywly=&ss_bgs=&ss_key=&ss_order=",
                14
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
        } else {
            MyDriver.clickOnElement(By.cssSelector("div.page > span.PEnd > a"));
        }
        Thread.sleep(1000);
        MyDriver.waitForPageToLoad();
        Thread.sleep(500);

    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul.cf > li > a[href*='/en/Professionals/']")));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("con"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = super.getSocials(container.findElements(By.cssSelector(".nr_r li a")), false);
        String phoneText = extractor.extractLawyerText(container, new By[]{By.className("li1")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, new By[]{By.className("tit")}, "NAME", LawyerExceptions::nameException),
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("li6")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", phoneText.isEmpty() ? "8675533988199" : phoneText
        );
    }

    private String getRole(WebElement container) throws LawyerExceptions {
        String role = extractor.extractLawyerText(container, new By[] {By.className("con_top")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}
