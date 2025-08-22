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

import static java.util.Map.entry;

public class TahotaLaw extends ByPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("bangkok", "Thailand"),
            entry("beijing", "China"),
            entry("changsha", "China"),
            entry("chengdu", "China"),
            entry("chongqing", "China"),
            entry("fuzhou", "China"),
            entry("guangzhou", "China"),
            entry("guiyang", "China"),
            entry("haikou", "China"),
            entry("hangzhou", "China"),
            entry("harbin", "China"),
            entry("hong kong", "Hong Kong"),
            entry("jinan", "China"),
            entry("kunming", "China"),
            entry("lhasa", "China"),
            entry("nanchang", "China"),
            entry("nanjing", "China"),
            entry("nepal", "Nepal"),
            entry("pakistan", "Pakistan"),
            entry("qingdao", "China"),
            entry("shanghai", "China"),
            entry("shenzhen", "China"),
            entry("suzhou", "China"),
            entry("sydney", "Australia"),
            entry("taiyuan", "China"),
            entry("tianjin", "China"),
            entry("urumqi", "China"),
            entry("vientiane", "Laos"),
            entry("washington", "USA"),
            entry("wuhan", "China"),
            entry("xian", "China"),
            entry("xining", "China"),
            entry("zhengzhou", "China")
    );


    private final By[] byRoleArray = {
            By.className("lawyer_txt"),
            By.cssSelector("span")
    };


    public TahotaLaw() {
        super(
            "Tahota Law",
            "https://www.tahota.com/EN/05.aspx?",
            11
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.tahota.com/EN/05.aspx?&Page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "director",
                "adviser"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.team_list > ul > li > div.padding15")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    private String getLink(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getAttribute("href");
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("lawyer_txt"),
                By.cssSelector("h2")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        WebElement element = this.siteUtl.iterateOverBy(byRoleArray, lawyer);
        String role = element.getText();
        return role.toLowerCase().contains("adviser") ? "Advisor" : role;
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("lawyer_txt"),
                By.cssSelector("em")
        };
        return siteUtl.getCountryBasedInOffice(
            OFFICE_TO_COUNTRY, this.siteUtl.iterateOverBy(byArray, lawyer)
        );
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                        .findElement(By.className("lawyer_txt"))
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
            "practice_area", "",
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "862886625656" : socials[1]
        );
    }
}
