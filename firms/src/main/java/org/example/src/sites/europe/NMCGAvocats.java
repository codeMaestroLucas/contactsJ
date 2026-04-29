package org.example.src.sites.europe;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.exceptions.LawyerExceptions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class NMCGAvocats extends ByNewPage {

    public NMCGAvocats() {
        super(
                "NMCG Avocats Associés",
                "https://www.nmcg.fr/en/our-expert-lawyers/",
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
        return MyDriver.wait.findElements(By.cssSelector("div.flex.flex-col.card-container"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.text-white")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"expert-page\"]/div/div[2]"));

        String role = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("div.col-span-2.italic")}, "ROLE", "textContent", LawyerExceptions::roleException);
        if (!siteUtl.isValidPosition(role, validRoles)) return "Invalid Role";

        String name = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("h1.text-big.font-bold.font-display")}, "NAME", "textContent", LawyerExceptions::nameException);
        String practiceArea = extractor.extractLawyerAttribute(container, new By[]{By.cssSelector("a[href*='https://www.nmcg.fr/en/expertises/']")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
        String[] socials = super.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "France",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "33170616161" : socials[1]
        );
    }
}
