package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class LabbeAbogados extends ByNewPage {

    public LabbeAbogados() {
        super(
                "Labbe Abogados",
                "https://labbe.legal/pages/equipo",
                1
        );
    }

    @Override
    protected void accessPage(int index) {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    private final String[] validRoles = {"socio", "socia"};

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            List<WebElement> lawyers = MyDriver.wait.findElements(By.className("multicolumn-card"));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[] {By.className("rte")}, true, this.validRoles);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.link-multi")}, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.tagName("h3")}, "NAME", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);

        String[] socials = super.getSocialsFromText(extractor.extractLawyerText(driver.findElement(By.tagName("body")), new By[]{By.xpath("//div[3]/p[2]")}, "SOCIALS", LawyerExceptions::socialsException));

        return Map.of(
                "link", link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Chile",
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56228999800" : socials[1]
        );
    }
}
