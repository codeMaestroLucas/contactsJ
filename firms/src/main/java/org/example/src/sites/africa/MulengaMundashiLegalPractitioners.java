package org.example.src.sites.africa;

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

public class MulengaMundashiLegalPractitioners extends ByNewPage {

    public MulengaMundashiLegalPractitioners() {
        super(
                "Mulenga Mundashi Legal Practitioners",
                "https://mmlp.co.zm/ourteam/",
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
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("sc_team_item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("sc_team_item_subtitle")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("sc_team_item_title")}, "NAME", LawyerExceptions::nameException);

        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("team_member_page_content"));

        String role = extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(strong,'Designation')]")}, "ROLE", LawyerExceptions::roleException);
        String email = extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(strong,'Email Address')]")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.xpath("//p[contains(strong,'Tel')]")}, "PHONE", LawyerExceptions::phoneException);

        if (email.isEmpty()) email = "mman@mman.co.ke";

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Zambia",
                "practice_area", "",
                "email", email,
                "phone", phone.isEmpty() ? "254208697960" : phone
        );
    }
}
