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

// Search the email, it isn't in the site
public class EmploymentLawAlliance extends ByNewPage {

    public static final Map<String, String> OFFICE_TO_COUNTRY = Map.ofEntries(
            entry("alberta", "Canada"),
            entry("angola", "Angola"),
            entry("argentina", "Argentina"),
            entry("australia", "Australia"),
            entry("austria", "Austria"),
            entry("bangladesh", "Bangladesh"),
            entry("belgium", "Belgium"),
            entry("bermuda", "Bermuda"),
            entry("bolivia", "Bolivia"),
            entry("brazil", "Brazil"),
            entry("british columbia", "Canada"),
            entry("bulgaria", "Bulgaria"),
            entry("cambodia", "Cambodia"),
            entry("channel islands - guernsey", "Guernsey"),
            entry("channel islands - jersey", "Jersey"),
            entry("chile", "Chile"),
            entry("china", "China"),
            entry("colombia", "Colombia"),
            entry("costa rica", "Costa Rica"),
            entry("croatia", "Croatia"),
            entry("cyprus", "Cyprus"),
            entry("czech republic", "the Czech Republic"),
            entry("denmark", "Denmark"),
            entry("dominican republic", "the Dominican Republic"),
            entry("ecuador", "Ecuador"),
            entry("egypt", "Egypt"),
            entry("el salvador", "El Salvador"),
            entry("england and wales", "England"),
            entry("estonia", "Estonia"),
            entry("ethiopia", "Ethiopia"),
            entry("finland", "Finland"),
            entry("france", "France"),
            entry("gabon", "Gabon"),
            entry("georgia", "Georgia"),
            entry("germany", "Germany"),
            entry("ghana", "Ghana"),
            entry("gibraltar", "Gibraltar"),
            entry("greece", "Greece"),
            entry("greenland", "Greenland"),
            entry("guatemala", "Guatemala"),
            entry("haiti", "Haiti"),
            entry("honduras", "Honduras"),
            entry("hong kong", "China"),
            entry("hungary", "Hungary"),
            entry("iceland", "Iceland"),
            entry("india", "India"),
            entry("indonesia", "Indonesia"),
            entry("ireland", "Ireland"),
            entry("isle of man", "Isle of Man"),
            entry("israel", "Israel"),
            entry("italy", "Italy"),
            entry("japan", "Japan"),
            entry("kenya", "Kenya"),
            entry("korea", "Korea (South)"),
            entry("latvia", "Latvia"),
            entry("lebanon", "Lebanon"),
            entry("lithuania", "Lithuania"),
            entry("luxembourg", "Luxembourg"),
            entry("macau", "China"),
            entry("malaysia", "Malaysia"),
            entry("malta", "Malta"),
            entry("manitoba", "Canada"),
            entry("mauritius", "Mauritius"),
            entry("mexico", "Mexico"),
            entry("morocco", "Morocco"),
            entry("mozambique", "Mozambique"),
            entry("myanmar", "Myanmar"),
            entry("netherlands", "the Netherlands"),
            entry("new brunswick", "Canada"),
            entry("new zealand", "New Zealand"),
            entry("newfoundland and labrador", "Canada"),
            entry("nicaragua", "Nicaragua"),
            entry("nigeria", "Nigeria"),
            entry("north macedonia", "North Macedonia"),
            entry("northern ireland", "Northern Ireland"),
            entry("norway", "Norway"),
            entry("nova scotia", "Canada"),
            entry("oman", "Oman"),
            entry("ontario", "Canada"),
            entry("pakistan", "Pakistan"),
            entry("panama", "Panama"),
            entry("paraguay", "Paraguay"),
            entry("peru", "Peru"),
            entry("philippines", "the Philippines"),
            entry("poland", "Poland"),
            entry("portugal", "Portugal"),
            entry("prince edward island", "Canada"),
            entry("qatar", "Qatar"),
            entry("quebec", "Canada"),
            entry("romania", "Romania"),
            entry("russia", "Russia"),
            entry("rwanda", "Rwanda"),
            entry("saskatchewan", "Canada"),
            entry("scotland", "Scotland"),
            entry("serbia", "Serbia"),
            entry("singapore", "Singapore"),
            entry("slovenia", "Slovenia"),
            entry("south africa", "South Africa"),
            entry("spain", "Spain"),
            entry("sri lanka", "Sri Lanka"),
            entry("sweden", "Sweden"),
            entry("switzerland", "Switzerland"),
            entry("taiwan", "Taiwan"),
            entry("tanzania", "Tanzania"),
            entry("thailand", "Thailand"),
            entry("türkiye", "Turkiye"),
            entry("uganda", "Uganda"),
            entry("ukraine", "Ukraine"),
            entry("united arab emirates", "the UAE"),
            entry("uruguay", "Uruguay"),
            entry("venezuela", "Venezuela"),
            entry("vietnam", "Vietnam"),
            entry("yemen", "Yemen"),
            entry("zambia", "Zambia")
    );

    public EmploymentLawAlliance() {
        super(
                "Employment Law Alliance",
                "https://www.ela.law/firms",
                28,
                3
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        if (index == 0) {
            this.driver.get(this.link);
            MyDriver.waitForPageToLoad();
        } else {
            MyDriver.clickOnElement(driver.findElement(By.cssSelector("div.dataTables_paginate > a.next")));
            Thread.sleep(5000);
            MyDriver.waitForPageToLoad();
        }
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("tr[class*='odd'], tr[class*='even']")));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.cssSelector("td:nth-child(3) > a")};
        String link = extractor.extractLawyerAttribute(lawyer, byArray, "LINK", "href", LawyerExceptions::linkException);
        MyDriver.openNewTab(link);
        return link;
    }

    private String getName(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.tagName("h1")};
        String text = extractor.extractLawyerAttribute(lawyer, byArray, "NAME", "textContent", LawyerExceptions::nameException);
        return text.split("\n")[0].trim();
    }

    private String getRole(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio-details-role")};
        String role = extractor.extractLawyerAttribute(lawyer, byArray, "ROLE", "textContent", LawyerExceptions::roleException);
        boolean validPosition = siteUtl.isValidPosition(role, validRoles);
        return validPosition ? role : "Invalid Role";
    }

    private String getCountry(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.id("Jurisdiction")};
        String country = extractor.extractLawyerAttribute(lawyer, byArray, "COUNTRY", "textContent", LawyerExceptions::countryException);
        return siteUtl.getCountryBasedInOffice(OFFICE_TO_COUNTRY, country, "USA");
    }

    private String getFirm(WebElement lawyer) throws LawyerExceptions {
        By[] byArray = {By.className("bio-details-company")};
        return extractor.extractLawyerAttribute(lawyer, byArray, "FIRM", "textContent", LawyerExceptions::roleException);
    }

    private String[] getSocials(WebElement lawyer) {
        try {
            List<WebElement> links = lawyer.findElements(By.cssSelector(".bio-contacts-list li a"));
            return super.getSocials(links, false);
        } catch (Exception e) {
            return new String[]{"", ""};
        }
    }

    @Override
    public Object getLawyer(WebElement lawyer) throws Exception {
        this.openNewTab(lawyer);
        WebElement container = driver.findElement(By.className("bio-area"));

        String role = this.getRole(container);
        if (role.equals("Invalid Role")) return "Invalid Role";

        String[] socials = this.getSocials(container);

        return Map.of(
                "link", Objects.requireNonNull(driver.getCurrentUrl()),
                "name", this.getName(container),
                "role", role,
                "firm", this.getFirm(container),
                "country", this.getCountry(container),
                "practice_area", "Labour & Employment",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}