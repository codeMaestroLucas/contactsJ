package org.example.src.sites._standingBy;

import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

public class Ecija extends ByNewPage {
    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("barcelona", "Spain"),
            entry("bogota", "Colombia"),
            entry("braga", "Portugal"),
            entry("buenos aires", "Argentina"),
            entry("guatemala city", "Guatemala"),
            entry("guayaquil", "Ecuador"),
            entry("lima", "Peru"),
            entry("lisbon", "Portugal"),
            entry("madrid", "Spain"),
            entry("managua", "Nicaragua"),
            entry("mexico city", "Mexico"),
            entry("montevideo", "Uruguay"),
            entry("oporto", "Portugal"),
            entry("pamplona", "Spain"),
            entry("panama city", "Panama"),
            entry("quito", "Ecuador"),
            entry("san jose", "Costa Rica"),
            entry("san juan", "USA"),
            entry("san salvador", "El Salvador"),
            entry("santiago de chile", "Chile"),
            entry("tegucigalpa", "Honduras"),
            entry("valencia", "Spain"),
            entry("vitoria", "Spain"),
            entry("zaragoza", "Spain")
    );


    private final By[] byRoleArray = {
            By.cssSelector("p[class*='styles-module--position']")
    };


    public Ecija() {
        super(
            "Ecija",
            "https://www.ecija.com/en/lawyers/",
            1,
            1000
        );
    }


    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
            MyDriver.rollDown(1, 2);

            WebElement page = driver.findElement(By.cssSelector("main"));
            By[] byArray = new By[]{
                    By.xpath("//*[@id=\"___griddo\"]/main/div[2]/section/form/fieldset[2]/div[2]/div")
            };
            WebElement div = this.siteUtl.iterateOverBy(byArray, page);

            // Click the button
            WebElement button = div.findElement(By.cssSelector("button"));
            // Click button to expand dropdown
            MyDriver.clickOnElement(button);

            // Wait for the dropdown <ul> to appear
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            int[] toClickIndex = {1, 2, 3, 4, 5};
            for (int i : toClickIndex) {
                // re-open dropdown
                MyDriver.clickOnElement(button);

                // wait for dropdown to show again
                WebElement ulAgain = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("ul")));

                List<WebElement> lisAgain = ulAgain.findElements(By.tagName("li"));
                if (i < lisAgain.size()) {
                    MyDriver.clickOnElement(lisAgain.get(i));
                    Thread.sleep(300);
                }
            }



        } else {
            WebElement elementToClick = driver.findElement(By.cssSelector("button[class*='styles-module--paginationCta']"));
            MyDriver.clickOnElement(elementToClick);
        }

        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);
    }


    protected List<WebElement> getLawyersInPage() {
        String[] validRoles = new String[]{
                "partner",
                "counsel",
                "chairman",
                "principal associate",
                "senior associate"
        };

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.cssSelector("a[data-link='anchor' href='https://www.ecija.com/en/lawyers/']")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }


    public void openNewTab(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("a[href^='https://www.ecija.com/en/lawyers/']")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        MyDriver.openNewTab(element.getAttribute("href"));
    }


    private String getName(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("section[class*='styles-module--fixed']"),
                By.cssSelector("h1")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getRole(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("section[class*='styles-module--fixed']"),
                By.cssSelector("span")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getPracticeArea(WebElement lawyer) {
        By[] byArray = new By[]{
                By.cssSelector("div[class*='styles-module--servicesWrp']"),
                By.cssSelector("ul > li")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
    }


    private String getCountry(WebElement lawyer) {
        //todo: check if returns valid countries options
        By[] byArray = new By[]{
                By.cssSelector("li:nth-of-type(3)")
        };
        WebElement element = this.siteUtl.iterateOverBy(byArray, lawyer);
        return element.getText();
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


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.cssSelector("div[class*='styles-module--main']"));

        String[] socials = this.getSocials(div);

        return Map.of(
            "link", Objects.requireNonNull(driver.getCurrentUrl()),
            "name", this.getName(div),
            "role", this.getRole(div),
            "firm", this.name,
            "country", this.getCountry(div),
            "practice_area", this.getPracticeArea(div),
            "email", socials[0],
            "phone", socials[1].isEmpty() ? "" : socials[1]
        );
    }

    public static void main(String[] args) {
        Ecija x = new Ecija();
        x.searchForLawyers();
    }
}
