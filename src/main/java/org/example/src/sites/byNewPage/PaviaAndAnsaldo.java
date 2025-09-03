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

import static java.util.Map.entry;

public class PaviaAndAnsaldo extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("7", "Russia"),
            entry("81", "Hong Kong"),
            entry("34", "Spain")
    );


    private final By[] byRoleArray = {
            By.className("tpstyle-6-info"),
            By.cssSelector("h5")
    };


    public PaviaAndAnsaldo() {
        super(
            "Pavia And Ansaldo",
            "https://www.pavia-ansaldo.it/en/lawyers/",
            1,
            2
        );
    }


    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        // Click on add btn
        MyDriver.clickOnElement(By.className("cmplz-accept"));
    }


    @Override
    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[href^='https://www.pavia-ansaldo.it/en/']")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) throws LawyerExceptions {
        MyDriver.openNewTab(lawyer.getAttribute("href"));
    }


    private String getName(WebElement lawyer) throws LawyerExceptions {
        WebElement nameDiv = lawyer.findElement(By.className("wpb_wrapper"));
        String firstName = nameDiv.findElement(By.cssSelector("p > i")).getText();
        String lastName = nameDiv.findElement(By.cssSelector("h1")).getText();

        return firstName + " " +  lastName;
    }


    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.xpath("//*[@id=\"vc_row-68a3e46410dd9\"]/div[2]/div/div/div[3]/div/div/div/div/div/h3")
        };
        return extractor.extractLawyerText(lawyer, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getCountry(String phone) throws LawyerExceptions {
        return this.siteUtl.getCountryBasedInOfficeByPhone(OFFICE_TO_COUNTRY, phone, "Italy");
    }


    private String getPracticeArea(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://www.pavia-ansaldo.it/en/departments/']")
        };
        return extractor.extractLawyerText(lawyer, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
    }


    private String[] getSocials(WebElement lawyer) {
        String email = ""; String phone = "";

        WebElement div = lawyer.findElement(By.xpath("//*[@id=\"vc_row-68a3e46410dd9\"]/div[2]/div/div/div[5]/div/div/div"));

        try {

            email = div.findElement(By.cssSelector("href^='mailto:']")).getAttribute("href");
            phone = div.findElement(By.xpath("//*[@id=\"vc_row-68a3e46410dd9\"]/div[2]/div/div/div[5]/div/div/div/a[3]/div/div[2]/div/div/div/div/p/span")).getText();


        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
        }
        return new String[]{ email, phone };
    }


    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("wpb_wrapper"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", this.getCountry(socials[1]),
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }
}
