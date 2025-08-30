package org.example.src.sites.byPage;

import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class GrandwayLaw extends ByPage {
    private final By[] byRoleArray = {
            By.className("teamRig"),
            By.className("teamJob")
    };


    public GrandwayLaw() {
        super(
            "Grandway Law",
            "https://www.grandwaylaw.com/en/zhuanyetuandui/?kwd=&job=&t6=&t2=",
            12
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.grandwaylaw.com/en/zhuanyetuandui.html?kwd=&t1=0&t2=0&t3=&t6=0&job=&p=" + (index * 10);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("ul.team > li > a")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        return lawyer.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("teamRig"),
                By.className("teamTit")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("teamRig"),
                By.className("lefConIco1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText();
        return country.toLowerCase().contains("hong kong") ? "Hong Kong" : "China";
    }

    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("teamRig"),
                By.className("lefConIco5")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String[] practice = element.getText().split(",");
        return practice[practice.length - 1];
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("teamDes"))
                        .findElements(By.cssSelector("p"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);
        return Map.of(
            "link", this.getLink(lawyer),
            "name", this.getName(lawyer),
            "role", this.getRole(lawyer),
            "firm", this.name,
            "country", this.getCountry(lawyer),
            "practice_area", this.getPracticeArea(lawyer),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
