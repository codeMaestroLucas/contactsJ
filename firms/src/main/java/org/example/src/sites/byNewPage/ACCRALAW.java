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

public class ACCRALAW extends ByNewPage {

    public ACCRALAW() {
        super(
                "ACCRALAW",
                "https://accralaw.com/our-lawyers/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("td-people-item")));
            for (int i = 0; i < 5; i++) lawyers.removeFirst();
            return lawyers;
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);
        if (name.contains("†")) return "Invalid Role";

        String link = this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.tagName("body"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String email = extractor.extractLawyerText(container, new By[]{By.xpath("//*[@id=\"main\"]/div/section[2]/div/div[1]/div/div[5]")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath("//*[@id=\"main\"]/div/section[2]/div/div[1]/div/div[6]")}, "PHONE", LawyerExceptions::phoneException);
        String practiceArea = extractor.extractLawyerText(container, new By[]{By.className("elementor-shortcode")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "the Philippines",
                "practice_area", practiceArea,
                "email", email,
                "phone", phone.isEmpty() ? "63288308000" : phone
        );
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        String role = extractor.extractLawyerText(lawyer, new By[]{By.xpath("//*[@id=\"main\"]/div/section[1]/div/div[1]/div/div[1]/h5")}, "ROLE", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }
}
