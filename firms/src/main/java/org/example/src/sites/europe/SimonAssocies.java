package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class SimonAssocies extends ByNewPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public SimonAssocies() {
        super(
                "Simon Associés",
                "https://simonassocies.com/en/the-partners/",
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
        try {
            return MyDriver.wait.findElements(By.className("team-content"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector(".name-team a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement div) throws LawyerExceptions {
        return extractor.extractLawyerText(div, new By[]{By.tagName("h1")}, "NAME", LawyerExceptions::nameException);
    }

    private String getPracticeArea(WebElement div) throws LawyerExceptions {
        return extractor.extractLawyerText(div, new By[]{By.className("dep-team")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }

    private String[] getSocials() {
        try {
            String href = driver.findElement(By.cssSelector("a[href$='.vcf']")).getAttribute("href");
            return vCard.getSocials(driver, href);
        } catch (Exception e) {
            System.err.println("SimonAssocies: error fetching vCard — " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement div = driver.findElement(By.id("content-single-team"));
        String[] socials = this.getSocials();

        return Map.of(
                "link", link,
                "name", this.getName(div),
                "role", "Partner",
                "firm", this.name,
                "country", "France",
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "330153962020" : socials[1]
        );
    }
}
