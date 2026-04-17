package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MHRLegal extends ByNewPage {

    public MHRLegal() {
        super(
                "MHR Legal",
                "https://mhrlegal.com/team-2/",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            ArrayList<WebElement> lawyers = new ArrayList<>();
            for (int i = 5; i < 14; i++) {
                if  (i == 11 || i == 12) continue;

                String xpath = "//*[@id=\"Content\"]/div/main/div/section[1]/div/div/div[" + i + "]";
                WebElement div = driver.findElement(By.xpath(xpath));
                lawyers.addAll(div.findElements(By.cssSelector("a[href*='https://mhrlegal.com/']")));
            }

            return lawyers;

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("title")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement subPage) throws LawyerExceptions {
        By[] byArray = {By.className("desc_wrappper_title")};
        return extractor.extractLawyerText(subPage, byArray, "ROLE", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement subPage) {
        try {
            List<WebElement> socials = subPage.findElements(By.tagName("a"));
            return super.getSocials(socials, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        this.openNewTab(lawyer);

        WebElement container = driver.findElement(By.className("team_vertical"));
        String role = this.getRole(container);
        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Argentina",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "541121509779" : socials[1]
        );
    }
}
