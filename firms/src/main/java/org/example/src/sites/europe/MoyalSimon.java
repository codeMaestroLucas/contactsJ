package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MoyalSimon extends ByNewPage {

    public MoyalSimon() {
        super(
                "Moyal & Simon",
                "https://moyal-simon.com/en/team/",
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
        return MyDriver.wait.findElements(By.cssSelector("li.card.preview"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("has-text-primary")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("tile"));
        String role = driver.findElement(By.xpath("//*[@id=\"content\"]/section/section[1]/div/div/div/div[2]/div/h2")).getAttribute("innerText");

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        String practiceArea = "";
        try {
            List<WebElement> areas = driver.findElements(By.xpath("//h4[contains(text(), 'Practice Areas')]/following-sibling::table//td"));
            practiceArea = areas.stream().map(WebElement::getText).collect(Collectors.joining(", "));
        } catch (Exception ignored) {}

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Luxembourg",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "352288018" : socials[1]
        );
    }
}
