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

public class BDO extends ByNewPage {
    private final By[] byRoleArray = {
            By.className("person-card__details-subtitle")
    };

    private String currentCountry = "";

    private final String[] validRoles = {
            "partner",
            "counsel",
            "advisor",
            "director",
            "senior associate"
    };

    public BDO() {
        super(
            "BDO",
            "",
            28,
            5
        );
    }

    //TODO: More countries on https://www.bdo.global/en-gb/global-locations
    private final String[] otherLinks = {
            "https://www.bdo.hu/en-gb/our-people",
            "https://www.bdo.co.uk/en-gb/our-people",
            "https://www.bdo.at/en-gb/team",
            "https://www.bdo.be/en-gb/our-people",
            "https://www.bdoafa.bg/en-us/people",
            "https://www.bdo-ea.com/en-gb/our-people",
            "https://www.bdo.com.cy/en-gb/our-people",
            "https://www.bdo.dk/en-gb/our-people",
            "https://www.bdo.com.eg/en-gb/our-people",
            "https://www.bdo.fr/en-gb/our-people",
            "https://www.bdo.gr/en-gb/our-people",
            "https://www.bdo.gg/en-gb/our-people",
            "https://www.bdo.co.il/en-gb/our-people",
            "https://www.bdo.com.jo/en-gb/our-people",
            "https://www.bdo-lb.com/en-gb/our-people",
            "https://www.bdo.lt/en-gb/our-people",
            "https://www.bdo.lu/en-gb/our-people-en",
            "https://www.bdo.com.mt/en-gb/our-people",
            "https://www.bdo.ma/en-gb/our-people",
            "https://www.bdo.nl/en-gb/our-people",
            "https://www.bdo.no/en-gb/people",
            "https://www.bdo.pl/en-gb/our-people",
            "https://www.bdo.pt/en-gb/our-people",
            "https://www.bdo.ro/en-gb/our-people",
            "https://www.bdo.co.za/en-za/our-people",
            "https://www.bdo.es/en-gb/our-people",
            "https://www.bdo.ch/en-gb/our-people",
            "https://www.bdo.tn/en-gb/our-people",
            "https://www.bdo.com.tr/en-gb/our-people",
            "https://www.bdomexico.com/en-gb/our-people",
//            "",
    };


    private String setIndexAndCountry(int index) {
        switch (index) {
            case 0:
                currentCountry = "Hungary";
                break;
            case 1:
                currentCountry = "England";
                break;
            case 2:
                currentCountry = "Austria";
                break;
            case 3:
                currentCountry = "Belgium";
                break;
            case 4:
                currentCountry = "Bulgaria";
                break;
            case 5:
                currentCountry = "*** Africa";
                break;
            case 6:
                currentCountry = "Cyprus";
                break;
            case 7:
                currentCountry = "Denmark";
                break;
            case 8:
                currentCountry = "Egypt";
                break;
            case 9:
                currentCountry = "France";
                break;
            case 10:
                currentCountry = "Greece";
                break;
            case 11:
                currentCountry = "Guernsey";
                break;
            case 12:
                currentCountry = "Israel";
                break;
            case 13:
                currentCountry = "Jordan";
                break;
            case 14:
                currentCountry = "Lebanon";
                break;
            case 15:
                currentCountry = "Lithuania";
                break;
            case 16:
                currentCountry = "Luxembourg";
                break;
            case 17:
                currentCountry = "Malta";
                break;
            case 18:
                currentCountry = "Morocco";
                break;
            case 19:
                currentCountry = "the Netherlands";
                break;
            case 20:
                currentCountry = "Norway";
                break;
            case 21:
                currentCountry = "Poland";
                break;
            case 22:
                currentCountry = "Portugal";
                break;
            case 23:
                currentCountry = "Romania";
                break;
            case 24:
                currentCountry = "South Africa";
                break;
            case 25:
                currentCountry = "Spain";
                break;
            case 26:
                currentCountry = "Switzerland";
                break;
            case 27:
                currentCountry = "Tunisia";
                break;
            case 28:
                currentCountry = "Turkey";
                break;
            case 29:
                currentCountry = "Mexico";
                break;
            default:
                currentCountry = "Unknown";
                break;
        }

        return otherLinks[index];
    }


    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(setIndexAndCountry(index));
        MyDriver.waitForPageToLoad();
        Thread.sleep(1000L);

        MyDriver.clickOnAddBtn(By.id("CybotCookiebotDialogBodyLevelButtonLevelOptinAllowAll"));
        MyDriver.clickOnAddBtn(By.id("el_VSwRDZcpDr"));

        WebElement loadMore = driver.findElement(By.className("people-landing__load-more"));
        String[] split = loadMore
                .findElement(By.className("people-landing__load-more-total"))
                .getAttribute("textContent")
                .split(" ");

        int currentPagination = Integer.parseInt(split[2]);
        int totalPages = Integer.parseInt(split[4]);
        int timesToClick = (totalPages - currentPagination) / 20 + 1;

        if (timesToClick > 5) timesToClick = 5;

        WebElement loadMoreBtn = loadMore.findElement(By.cssSelector("a[role='button']"));
        MyDriver.clickOnElementMultipleTimes(loadMoreBtn, timesToClick, 1);

    }


    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10L));

            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(
                            By.className("person-card__details")
                    )
            );
            return this.siteUtl.filterLawyersInPage(lawyers, byRoleArray, true, validRoles);

        } catch (Exception e) {
            throw new RuntimeException("Failed to find lawyer elements", e);
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.cssSelector("a[href*='/en-gb/our-people/']")

        };
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }


    private String getName(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("bio__title")
        };
        return extractor.extractLawyerText(container, byArray, "NAME", LawyerExceptions::nameException);
    }


    private String getRole(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("bio__subtitle")
        };
        return extractor.extractLawyerText(container, byArray, "ROLE", LawyerExceptions::roleException);
    }


    private String getPracticeArea(WebElement container) throws LawyerExceptions {
        By[] byArray = new By[]{
                By.className("bio__subtitle")
        };
        String practiceArea = extractor.extractLawyerText(container, byArray, "PRACTICE AREA", LawyerExceptions::practiceAreaException);
        return practiceArea.split("\\|")[0];
    }


    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> socials = lawyer
                    .findElement(By.className("contacts"))
                    .findElements(By.tagName("a"));
            return super.getSocials(socials, false);

        } catch (Exception e) {
            System.err.println("Error getting socials: " + e.getMessage());
            return new String[]{"", ""};
        }
    }


    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);

        WebElement div = driver.findElement(By.className("bio"));

        String[] socials = this.getSocials(div);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(div),
                "role", this.getRole(div),
                "firm", this.name,
                "country", currentCountry,
                "practice_area", this.getPracticeArea(div),
                "email", socials[0],
                "phone", socials[1]
        );
    }
}