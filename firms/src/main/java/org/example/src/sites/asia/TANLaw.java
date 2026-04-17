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

public class TANLaw extends ByNewPage {

    public TANLaw() {
        super(
                "TAN Law",
                "http://fddhlaw.com/team.asp",
                39
        );
    }

    private final String[] validRoles = {
            "合伙人", "高级合伙人", "管理合伙人", "首席合伙人", "创始合伙人", "顾问", "高级顾问", "律师", "高级律师", "主任", "主席"
    };

    private static final Map<String, String> ROLE_MAP = Map.ofEntries(
            Map.entry("合伙人",   "Partner"),
            Map.entry("高级合伙人", "Senior Partner"),
            Map.entry("管理合伙人", "Managing Partner"),
            Map.entry("首席合伙人", "Partner"),
            Map.entry("创始合伙人", "Partner"),
            Map.entry("顾问",    "Counsel"),
            Map.entry("高级顾问",  "Senior Counsel"),
            Map.entry("高级律师",  "Senior Associate"),
            Map.entry("主任",    "Partner"),
            Map.entry("主席",    "Partner")
    );


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://fddhlaw.com/teamSearch.asp?officeName=&practiceName=&n=&page=" + (index + 1);
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnElement(By.xpath("//*[@id=\"m-level-search\"]/input"));
        Thread.sleep(2000);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("item")));
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

    private String translateRole(String role) {
        for (Map.Entry<String, String> entry : ROLE_MAP.entrySet()) {
            if (role.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "";
    }


    private String getRole(WebElement lawyer) {
        String roleCHINESE = lawyer.findElement(By.className("identity")).getAttribute("textContent");
        boolean validPosition = siteUtl.isValidPosition(roleCHINESE, validRoles);

        if (!validPosition) return "Invalid Role";

        return this.translateRole(roleCHINESE);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElement(By.className("contact-way")).findElements(By.cssSelector("li.clearfix"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("main-content-container"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", "",
                "role", role,
                "firm", this.name,
                "country", "China",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "02583682418" : socials[1]
        );
    }
}
