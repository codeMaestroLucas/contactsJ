package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.Map;

public class BUDINPartners extends ByNewPage {

    public BUDINPartners() {
        super(
                "BUDIN Partners",
                "https://www.budin.ch/en/lawyers/",
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
        return MyDriver.wait.findElements(By.cssSelector("article.mix"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.tagName("a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    public String extractEmailFromJS(String html) {
        Pattern pattern = Pattern.compile("LTUCMT\\('([^']+)'\\)");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            String encoded = matcher.group(1);

            // Decode Base64
            String decoded = new String(Base64.getDecoder().decode(encoded));

            // Remove "mailto:"
            return decoded.replace("mailto:", "");
        }

        return null;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.className("portfolio_title")}, "NAME", "textContent", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("informations"));

        String practiceArea = extractor.extractLawyerAttribute(container, new By[]{By.className("domaines-expertises")}, "PRACTICE AREA", "textContent", LawyerExceptions::practiceAreaException);
        
        // Use innerHTML to catch encoded email in JS scripts or text
        String[] socials = this.getSocialsFromText(MyDriver.wait.findElement(By.className("portfolio_single")).getAttribute("innerHTML"));
        String html = MyDriver.wait
                .findElement(By.className("portfolio_single"))
                .getAttribute("innerHTML");

        String email = extractEmailFromJS(html);

        return Map.of(
                "link", link,
                "name", name,
                "role", "-----",
                "firm", this.name,
                "country", "Switzerland",
                "practice_area", practiceArea == null ? "" : practiceArea,
                "email", email,
                "phone", socials[1].isEmpty() ? "41228180808" : socials[1]
        );
    }
}
