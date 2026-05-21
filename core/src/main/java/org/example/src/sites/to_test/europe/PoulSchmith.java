package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PoulSchmith extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public PoulSchmith() {
        super(
                "Poul Schmith",
                "https://poulschmith.com/people",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.className("employee-card"));
        return siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("employee-card__job-title")}, true, validRoles);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = lawyer.findElement(By.className("employee-card__link")).getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("employee-card__name")}, "NAME", LawyerExceptions::nameException);
        String role = extractor.extractLawyerText(lawyer, new By[]{By.className("employee-card__job-title")}, "ROLE", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("contact-info"));

        String[] socials = this.getSocials(container.findElements(By.tagName("a")), false);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Denmark",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "4533152010" : socials[1]
        );
    }
}
