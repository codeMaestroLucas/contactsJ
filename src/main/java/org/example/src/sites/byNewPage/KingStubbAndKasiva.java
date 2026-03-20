package org.example.src.sites.byNewPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KingStubbAndKasiva extends ByNewPage {

    // Stores [name, role] keyed by link — LinkedHashMap preserves order and deduplicates by link
    private final Map<String, String[]> lawyerCardData = new LinkedHashMap<>();
    private List<String> lawyerLinks = new ArrayList<>();
    private int lawyerDataIndex = 0;

    public KingStubbAndKasiva() {
        super(
                "King Stubb & Kasiva",
                "https://ksandk.com/people/",
                1
        );
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        this.driver.get(this.link);
        MyDriver.waitForPageToLoad();
        MyDriver.clickOnAddBtn(By.xpath("/html/body/div[3]/div/div/div/div[2]/button[2]"));
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        lawyerCardData.clear();
        lawyerDataIndex = 0;

        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName("body")));

            int[] indexes = {2, 3, 4, 5, 6, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 20, 21, 23, 26, 27};

            for (int i : indexes) {
                String xpath = "//*[@id=\"brxe-nyuuec\"]/div/fieldset/div/ul/li[" + i + "]";
                MyDriver.clickOnElement(By.xpath(xpath));
                Thread.sleep(2500);

                List<WebElement> unfilteredLawyers = driver.findElements(By.cssSelector("div.people_card"));
                List<WebElement> filtered = this.siteUtl.filterLawyersInPage(
                        unfilteredLawyers, new By[]{By.cssSelector("div.people_card__designation")}, false);

                // Extract data immediately while elements are still fresh
                for (WebElement card : filtered) {
                    try {
                        String link = card.findElement(By.className("people_card__link")).getAttribute("href");
                        if (link == null || lawyerCardData.containsKey(link)) continue; // deduplicate

                        String name = card.findElement(By.className("people_card__name")).getText().trim();
                        String role = card.findElement(By.className("people_card__designation")).getText().trim();
                        lawyerCardData.put(link, new String[]{name, role});
                    } catch (Exception ignored) {}
                }
            }

            lawyerLinks = new ArrayList<>(lawyerCardData.keySet());

            // Return stable stubs — one per lawyer, getLawyer() uses lawyerDataIndex instead
            WebElement body = driver.findElement(By.tagName("body"));
            List<WebElement> stubs = new ArrayList<>();
            for (int i = 0; i < lawyerLinks.size(); i++) stubs.add(body);
            return stubs;

        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions {
        String link = lawyerLinks.get(lawyerDataIndex);
        MyDriver.openNewTab(link);
        return link;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String link = lawyerLinks.get(lawyerDataIndex);
        String[] data = lawyerCardData.get(link);
        String name = data[0];
        String role = data[1];
        lawyerDataIndex++;

        MyDriver.openNewTab(link);
        WebElement container = driver.findElement(By.className("people_single__inner"));

        String email = extractor.extractLawyerText(container, new By[]{By.id("brxe-d0d9e6")}, "EMAIL", LawyerExceptions::emailException);
        String phone = extractor.extractLawyerText(container, new By[]{By.id("brxe-170c79")}, "PHONE", LawyerExceptions::phoneException);

        return Map.of(
                "link", link,
                "name", name,
                "role", role,
                "firm", this.name,
                "country", "India",
                "practice_area", extractor.extractLawyerText(container, new By[]{By.className("office_accordion_title")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", email,
                "phone", phone.isEmpty() ? "918349804183" : phone
        );
    }
}
