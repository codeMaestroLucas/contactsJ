package org.example.src.sites.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FenechFarrugiaFiott extends ByNewPage {

    public FenechFarrugiaFiott() {
        super(
                "Fenech Farrugia Fiott",
                "https://fff-legal.com/about-us/#our-team",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("div[x-data*='showProfile']"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.text-p1")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String xData = lawyer.getAttribute("x-data");
        if (xData == null || !xData.contains("link = '")) {
            throw LawyerExceptions.linkException("Could not find profile link in x-data");
        }

        String partialUrl = xData.split("link = '")[1].split("'")[0];
        String fullUrl = partialUrl.startsWith("http") ? partialUrl : "https://fff-legal.com" + partialUrl;

        MyDriver.openNewTab(fullUrl);
        return fullUrl;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p.text-t4, p.text-t3")}, "NAME", "textContent", LawyerExceptions::nameException);
        String role = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("p.text-p1")}, "ROLE", "textContent", LawyerExceptions::roleException);

        String link = this.openNewTab(lawyer);

        WebElement container = MyDriver.wait.findElement(By.xpath("//*[@id=\"our-team\"]/div[3]/div[2]/div/div/div[1]/div[3]"));

        String[] socials = super.getSocials(container.findElements(By.tagName("a")), false);

        String practiceArea = container.findElement(By.xpath("//*[@id=\"our-team\"]/div[3]/div[2]/div/div/div[2]/div[1]")).getText();
        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Malta",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "35625496400" : socials[1]
        );
    }
}
