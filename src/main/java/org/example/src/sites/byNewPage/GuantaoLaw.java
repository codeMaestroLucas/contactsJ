package org.example.src.sites.byNewPage;

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

import static java.util.Map.entry;

public class GuantaoLaw extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("beijing", "China"),
            entry("chengdu", "China"),
            entry("chongqing", "China"),
            entry("dalian", "China"),
            entry("fuzhou", "China"),
            entry("guangzhou", "China"),
            entry("haikou", "China"),
            entry("hangzhou", "China"),
            entry("hefei", "China"),
            entry("hong kong", "Hong Kong"),
            entry("jinan", "China"),
            entry("ji nan", "China"),
            entry("kunming", "China"),
            entry("luolong", "China"),
            entry("nanchang", "China"),
            entry("nanjing", "China"),
            entry("new york", "USA"),
            entry("ningbo", "China"),
            entry("qingdao", "China"),
            entry("shanghai", "China"),
            entry("shenzhen", "China"),
            entry("suzhou", "China"),
            entry("sydney", "Australia"),
            entry("taiyuan", "China"),
            entry("tianjin", "China"),
            entry("toronto", "Canada"),
            entry("wenzhou", "China"),
            entry("wuhan", "China"),
            entry("wuxi", "China"),
            entry("xiamen", "China"),
            entry("xian", "China"),
            entry("xi an", "China"),
            entry("zhengzhou", "China")
    );


    private final By[] byRoleArray = {
            By.className("gt_jg_hhr_itemr_ltitle")
    };


    // In the Future, evaluate if the 2 lawyers is valid
    public GuantaoLaw() {
        super(
            "Guantao Law",
            "https://www.guantao.com/en/column41?ly=0&bg=0&zw=0&py=&key=&go=goto26",
            30,
            2
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.guantao.com/en/column41?ly=0&bg=0&zw=0&py=&key=&page26=" + (index + 1) + "&go=goto26";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
        MyDriver.rollDown(1, 0.2);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("gt_jg_hhr_item")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("gt_team_bannercr_title")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("gt_team_bannercr_ltitle")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().split("\\|")[0];
    }


    private String getCountry(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("gt_team_bannercr_ltitle")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String country = element.getText().split("\\|")[1];
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country);
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{
                By.className("gt_team_bannercr_ly")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText().split("·")[0];
    }


    private String getEmail(WebElement lawyer) {
        String text = lawyer
                .findElement(By.className("gt_team_bannercr_tag"))
                .findElement(By.className("gt_team_bannercr_tag_item_text"))
                .getText();
        return text;
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("gt_team_bannercr"));

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", this.getPracticeArea(div),
            "email", this.getEmail(div),
            "phone", ""
        );
    }
}
