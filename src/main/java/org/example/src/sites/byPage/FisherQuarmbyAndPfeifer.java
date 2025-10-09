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

public class FisherQuarmbyAndPfeifer extends ByPage {

    public FisherQuarmbyAndPfeifer() {
        super(
                "Fisher Quarmby & Pfeifer",
                "https://www.fqp.com.na/our-team/",
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
        // Role is not consistently available on this page.
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));
            WebElement lawyersDiv = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[2]/section[2]/div/div/div")));
            return lawyersDiv.findElements(By.className("elementor-widget-wrap"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.tagName("h3")
        };
        return extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            return super.getSocials(lawyer.findElements(By.cssSelector("a")), true);
        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = this.getSocials(lawyer);

        if (socials[0].isEmpty()) return "Invalid Lawyer";

        return Map.of(
                "link", this.link,
                "name", this.getName(lawyer),
                "role", "Partner",
                "firm", this.name,
                "country", "Namibia",
                "practice_area", "",
                "email", socials[0].split(": ")[1],
                "phone", socials[1].isEmpty() ? "26464461620" : socials[1]
        );
    }
}