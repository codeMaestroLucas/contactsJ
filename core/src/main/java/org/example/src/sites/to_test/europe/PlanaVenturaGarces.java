package org.example.src.sites.to_test.europe;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class PlanaVenturaGarces extends ByNewPage {

    public PlanaVenturaGarces() {
        super(
                "Plana Ventura Garcés",
                "https://planaventuragarces.com/en/our-team/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.uagb-post__inner-wrap"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.tagName("small")}, true);
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String url = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h5 a")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("h5.uagb-post__title")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("uagb-block-c5b572af"));

        String role = extractor.extractLawyerText(container, new By[]{By.className("uagb-ifb-desc")}, "ROLE", LawyerExceptions::roleException);
        String[] socials = this.getSocials(container.findElements(By.cssSelector(".wp-block-uagb-icon-list-child")), true);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "Spain",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("uagb-ifb-content"), By.tagName("p")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "34932419740" : socials[1]
        );
    }
}
