package org.example.src.sites.americas;

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

public class LatinAlliance extends ByNewPage {
    private final String[] validRoles = new String[]{
            "partner",
            "counsel",
            "senior associate"
    };

    public LatinAlliance() {
        super(
            "Latin Alliance",
            "https://latinalliance.co/en/expertoslegal/",
            1
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"post-17499\"]/div/div/div/div[2]"));
        return div.findElements(By.cssSelector("a.et_pb_button[href^='https://latinalliance.co/en/']"));
    }


    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.et_pb_module")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("div.et_pb_module:nth-of-type(2)")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }


    private String[] getSocials(WebElement lawyer) {
        String[] socials = super.getSocialsFromText(lawyer.getText());
        if (socials[0].isEmpty()) socials = super.getSocialsFromText(lawyer.getAttribute("innerText"));
        return socials;

    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = MyDriver.wait
                .findElement(By.className("et_pb_row"))
                .findElement(By.className("et_pb_column_1"));

        String role = this.getRole(div);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", role,
                "firm", this.name,
                "country", "Colombia",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
