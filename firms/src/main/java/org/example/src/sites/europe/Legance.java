package org.example.src.sites.europe;

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

public class Legance extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("h5")
    };

    public Legance() {
        super(
            "Legance",
            "https://www.legance.com/professionals/",
            56,
                2
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        String otherUrl = "https://www.legance.com/professionals/page/" + (index + 1) + "/";
        String url = index == 0 ? this.link : otherUrl;
        this.driver.get(url);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        if (index > 0) return;

        MyDriver.clickOnAddBtn(By.className("iubenda-cs-accept-btn"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            WebElement div = wait.until(
                    ExpectedConditions.presenceOfElementLocated(
                            By.id("professionisti")
                    )
            );
            List<WebElement> lawyers = div.findElements(By.className("row"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.ruoloProfe")}, true);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyer.findElement(By.cssSelector("a.nomeProfe")).getAttribute("href");
        MyDriver.openNewTab(link);
        return link;
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("/html/body/div[2]/main/section[2]/div/div/div[2]/div[1]/div[1]/span[2]"),
        };
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY",  "textContent",LawyerExceptions::countryException);
        return country.toLowerCase().contains("london") ? "England" : "Italy";
    }

    private String getPracticeArea() {
        return driver.findElement(By.xpath("/html/body/div[2]/main/section[2]/div/div/div[2]/div[3]/a/span/span")).getAttribute("textContent");
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElements(By.cssSelector("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String name = lawyer.findElement(By.cssSelector("a.nomeProfe")).getAttribute("textContent");
        String role = lawyer.findElement(By.cssSelector("p.ruoloProfe")).getAttribute("textContent");

        String link = this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("contact-actions"));
        String[] socials = this.getSocials(div);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(),
                "email", socials[0].replace("%20", ""),
                "phone", "39028963071"
        );
    }
}
