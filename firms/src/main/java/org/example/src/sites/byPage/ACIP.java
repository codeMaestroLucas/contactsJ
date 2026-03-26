package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.TreatLawyerParams;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class ACIP extends ByPage {

    private final By[] byRoleArray = {
            By.className("job")
    };

    public ACIP() {
        super(
                "ACIP",
                "https://www.advancelaw.cn/zytd/list_81.aspx",
                2
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.advancelaw.cn/zytd/list_81.aspx?page=2";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("table tr")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".name a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector(".name a")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("area")};
        String office = extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        return office.toLowerCase().contains("guangzhou") ? "China" : office;
    }

    private String[] getSocials(WebElement lawyer, String name) {
        try {

            name = TreatLawyerParams.treatNameForEmail(name);
            String[] parts = name.split("\\s+");

            if (parts.length < 2) return new String[]{"", ""};

            String firstName = parts[0].toLowerCase();
            String lastNameFirstLetter = parts[parts.length - 1].substring(0, 1).toLowerCase();
            String email = firstName + lastNameFirstLetter + "@acip.cn";

            return new String[]{email, "861082263399"};
        } catch (Exception e) {
            return new String[]{"", "861082263399"};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name1 = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer, name1);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name1,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
