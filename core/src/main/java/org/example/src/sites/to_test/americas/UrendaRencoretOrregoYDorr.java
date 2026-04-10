package org.example.src.sites.to_test.americas;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Map;

public class UrendaRencoretOrregoYDorr extends ByPage {

    public UrendaRencoretOrregoYDorr() {
        super(
                "Urenda Rencoret Orrego y Dörr",
                "https://www.urod.cl/nuestro-equipo/juan-carlos-dorr-bulnes/",
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
        try {
            return MyDriver.wait.findElements(By.className("grve-column-wrapper"));
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.className("grve-title")}, "NAME", LawyerExceptions::nameException);

        WebElement textContainer = lawyer.findElement(By.className("vc_custom_1527626948812"));
        String practiceArea = extractor.extractLawyerText(textContainer, new By[]{By.xpath("./p[1]")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException);

        String[] socials = super.getSocials(lawyer.findElements(By.tagName("a")), false);

        return Map.of(
                "link", this.link,
                "name", name,
                "role", "Partner",
                "firm", this.name,
                "country", "Chile",
                "practice_area", practiceArea,
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "56224995500" : socials[1]
        );
    }

    @Override
    public Runnable searchForLawyers(boolean showLogs) {
        // Overriding because this link is a direct profile, not a list
        this.driver = MyDriver.getINSTANCE();
        try {
            this.accessPage(0);
            WebElement body = driver.findElement(By.tagName("body"));
            Object lawyerDetails = getLawyer(body);
            this.registerValidLawyer(lawyerDetails, 0, 0, showLogs);
        } catch (Exception e) {
            errorLogger.log(this.name, e, showLogs);
        }
        return null;
    }
}
