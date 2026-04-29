package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.VCard;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import java.util.List;
import java.util.Map;

public class FelsbergAdvogados extends ByPage {

    private final VCard vCard = VCard.withDefaultPatterns();

    public FelsbergAdvogados() {
        super(
                "FELSBERG ADVOGADOS",
                "https://www.felsberg.com.br/en/profissionais/equity-partners/",
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
        List<WebElement> lawyers = MyDriver.wait.findElements(By.cssSelector("article.profissional_padrao_box"));
        return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.cssSelector("p.prof_info_areas")}, true);
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String vcardHref = lawyer.findElement(By.xpath(".//span[text()='vCard']/parent::a")).getAttribute("href");
        String[] socials = vCard.getSocials(vcardHref);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("h2 a")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.tagName("h2")}, "NAME", LawyerExceptions::nameException),
                "role", "Partner",
                "firm", this.name,
                "country", "Brazil",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("p.prof_info_areas")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0].isEmpty() ? extractor.extractLawyerAttribute(lawyer, new By[]{By.xpath(".//span[text()='E-mail']/parent::a")}, "EMAIL", "href", LawyerExceptions::emailException).replace("mailto:", "") : socials[0],
                "phone", socials[1].isEmpty() ? "551131419100" : socials[1]
        );
    }
}
