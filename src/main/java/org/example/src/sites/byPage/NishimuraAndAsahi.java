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

public class NishimuraAndAsahi extends ByPage {
    private final By[] byRoleArray = {
            By.cssSelector("ul.tag")
    };

    public NishimuraAndAsahi() {
        super(
                "Nishimura And Asahi",
                "https://www.nishimura.com/en/people/list?search_keywords_all=&job_title_aggregated_field=&field_member_office_all=All&field_member_job_language_all=All",
                25
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.nishimura.com/en/people/list?search_keywords_all=&job_title_aggregated_field=&field_member_office_all=All&field_member_job_language_all=All&currentstatus=0&unselect_seminar_sponsor=0&page=" + index;
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = {"partner", "counsel", "advisor", "adviser", "senior associate", "associate"};

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("li > article")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getLink(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("a")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h3")};
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        return extractor.extractLawyerText(lawyer, byRoleArray, "ROLE", LawyerExceptions::roleException);
    }

    private String getCountry(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("ul.tag > li:nth-child(2)")};
            return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException);
        } catch (Exception e) {
            return "Japan";
        }
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            By[] byArray = {By.cssSelector("li.tel > a")};
            String phone = extractor.extractLawyerAttribute(lawyer, byArray, "PHONE", "href", LawyerExceptions::phoneException);
            return new String[]{"", phone.replace("tel:", "").trim()};
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    private String constructEmail(String name) {
        if (name == null || name.isEmpty()) return "";
        String[] nameParts = name.toLowerCase().trim().split("\\s+");
        if (nameParts.length < 2) return "";

        char firstNameLetter = nameParts[0].charAt(0);
        String lastName = nameParts[nameParts.length - 1];
        return firstNameLetter + "." + lastName + "@nishimura.com";
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = this.getName(lawyer);
        String[] socials = this.getSocials(lawyer);

        return Map.of(
                "link", this.getLink(lawyer),
                "name", name,
                "role", this.getRole(lawyer),
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", this.constructEmail(name),
                "phone", socials[1].isEmpty() ? "81362506200" : socials[1]
        );
    }
}
