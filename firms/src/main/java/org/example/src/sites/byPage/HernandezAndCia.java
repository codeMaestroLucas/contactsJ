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

public class HernandezAndCia extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("div.part1 > span")
    };

    public HernandezAndCia() {
        super(
                "Hernández And Cía",
                "",
                4
        );
    }

    private String currentRole = "";
    private final List<Map.Entry<String, String>> otherUrls = List.of(
            Map.entry("Partner", "https://www.ehernandez.com.pe/en/el-equipo/?cargo=partner"),
            Map.entry("Counsel", "https://www.ehernandez.com.pe/en/el-equipo/?cargo=Counsel"),
            Map.entry("Principal Associate", "https://www.ehernandez.com.pe/en/el-equipo/?cargo=Principal+Associate"),
            Map.entry("Senior Associate", "https://www.ehernandez.com.pe/en/el-equipo/?cargo=Senior+Associate")
    );

    protected void accessPage(int index) throws InterruptedException {
        var entry = otherUrls.get(index);
        currentRole = entry.getKey();
        driver.get(entry.getValue());
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            return wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("item-socios")
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("div.part1 > a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byFirst = {By.tagName("h6")};
        By[] byLast = {By.tagName("h5")};
        String first = extractor.extractLawyerText(lawyer, byFirst, "NAME", LawyerExceptions::nameException);
        String last = extractor.extractLawyerText(lawyer, byLast, "NAME", LawyerExceptions::nameException);
        return first + " " + last;
    }

    private String getPracticeArea(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("div.part2 ul")};
            return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.className("btn-green"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", this.getName(lawyer),
                "role", currentRole,
                "firm", this.name,
                "country", "Peru",
                "practice_area", this.getPracticeArea(lawyer),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "5116115151" : socials[1]
        );
    }
}