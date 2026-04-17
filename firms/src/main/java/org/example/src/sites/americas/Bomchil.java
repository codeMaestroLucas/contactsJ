package org.example.src.sites.americas;

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
import java.util.stream.Collectors;

public class Bomchil extends ByNewPage {

    public Bomchil() {
        super(
                "Bomchil",
                "https://bomchil.com/equipo/",
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
        String[] validRoles = {"socio", "counsel"};
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(".e-loop-item")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector(".elementor-element-32324b7")}, true, validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a.elementor-button-link")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.cssSelector("div.elementor-element-d0b4d35"));

        By[] nameBy = {By.tagName("h2")};
        By[] roleBy = {By.className("elementor-widget-text-editor")};

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        List<WebElement> practiceElements = container.findElements(By.cssSelector(".repeater-item a"));
        String practiceArea = practiceElements.stream().map(WebElement::getText).collect(Collectors.joining(", "));
        String role = extractor.extractLawyerText(container, roleBy, "ROLE", LawyerExceptions::roleException);
        if (role.contains("socio")) role = "Partner";

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", extractor.extractLawyerText(container, nameBy, "NAME", LawyerExceptions::nameException),
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541143217500" : socials[1]
        );
    }
}
