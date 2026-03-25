package org.example.src.sites.to_test;

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

public class BennaniAndAssocies extends ByNewPage {

    public BennaniAndAssocies() {
        super(
                "Bennani & Associes",
                "https://www.bennaniassocies.com/en/partners/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1500L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("bc-link-whole-grid-card")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("et_pb_text_3")}, true);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("data-link-url");
        if (link == null) throw LawyerExceptions.linkException("Link not found");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("et_pb_text_0_tb_body")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("et_pb_text_1_tb_body")};
        String text = extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
        return text.split("\n")[0].trim();
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("et_pb_text_2_tb_body")};
        String text = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        if (text.toLowerCase().contains("casablanca")) return "Morocco";
        if (text.toLowerCase().contains("abidjan")) return "Ivory Coast";
        return text.replace("Office:", "").trim();
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> links = lawyer.findElements(By.cssSelector("a[href^='mailto:']"));
            return super.getSocials(links, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("et_pb_column_0_tb_body"));

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", this.getRole(container),
                "firm", this.name,
                "country", this.getCountry(container),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "212522995252" : socials[1]
        );
    }
}
