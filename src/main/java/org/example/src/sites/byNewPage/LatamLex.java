package org.example.src.sites.byNewPage;

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

public class LatamLex extends ByNewPage {
    private final By[] byRoleArray = {
            By.cssSelector("p[style='text-transform:uppercase']")
    };

    public LatamLex() {
        super(
            "Latam Lex",
            "https://latamlex.com/en/equipo/",
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
        String[] validRoles = new String[]{
                "partner",
                "director",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("div.info > div")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.findElement(By.cssSelector("a[href^='https://latamlex.com/en/detalle-perfil/?id_=']")).getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("nombre")
        };
        return extractor.extractLawyerText(lawyer, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("img[src='https://latamlex.com/en/wp-content/uploads/2021/04/icargo.png']"),
                By.xpath("./parent::p")
        };

        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException)
                .replace("Position: ", "").trim();
    }


    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("dato_")
        };
        return extractor.extractLawyerText(lawyer, byArray, "COUNTRY", LawyerExceptions::countryException)
                .replace("Country: ", "").trim();
    }


    private String getPracticeArea() throws LawyerExceptions {
        WebElement lawyer = driver.findElement(By.cssSelector("div.col-sm-12.col-md-5"));
        By[] byArray = new By[]{
                By.className("menuDeAreas"),
                By.cssSelector("li")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer.findElements(By.className("dato_"));
            return super.getSocials(socials, true);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.cssSelector("div.col-sm-12.col-md-7"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(div),
                "practice_area", this.getPracticeArea(),
                "email", socials[0].replace("email", "").trim(),
                "phone", socials[1]
        );
    }
}
