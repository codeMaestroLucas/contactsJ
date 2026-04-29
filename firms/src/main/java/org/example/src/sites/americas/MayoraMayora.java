package org.example.src.sites.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class MayoraMayora extends ByPage {

    public MayoraMayora() {
        super(
                "Mayora & Mayora",
                "https://mayora-mayora.com/en/team/",
                1
        );
    }

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.of(
            "guatemala city", "Guatemala",
            "guatemala", "Guatemala",
            "roatan", "Honduras",
            "san salvador", "El Salvador",
            "san pedro sula", "Honduras",
            "tegucigalpa", "Honduras"
    );

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        WebElement div = MyDriver.wait.findElement(By.xpath("//*[@id=\"content\"]/article/div/div/section[4]"));
        List<WebElement> lawyers = div.findElements(By.cssSelector("div.elementor-inner-column"));

        div = driver.findElement(By.xpath("//*[@id=\"content\"]/article/div/div/section[7]"));
        lawyers.addAll(div.findElements(By.cssSelector("div.elementor-inner-column")));

        return lawyers;
    }
    
    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        String[] split = lawyer.getText().split("\n");
        String country = split[split.length - 1];
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "");
    }
    

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector("li.elementor-icon-list-item a")), false);

        return Map.of(
                "link", this.link,
                "name", extractor.extractLawyerText(lawyer, new By[]{By.className("elementor-image-box-title")}, "NAME", LawyerExceptions::nameException),
                "role", "",
                "firm", this.name,
                "country", this.getCountry(lawyer),
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "50222235959" : socials[1]
        );
    }
}
