package org.example.src.sites.mundial;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByNewPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.Validations;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TagAlliance extends ByNewPage {

    private static final int MAX_PAGES_PER_CONTINENT = 15;
    private static final String BASE_URL = "https://www.tagalliances.com/component/mtree/lawyer-profiles/all";

    // Ordered map: continent name → region id
    private static final LinkedHashMap<String, String> CONTINENTS = new LinkedHashMap<>();

    // Only continents listed here will be scraped
    private static final Set<String> ACTIVE_CONTINENTS;

    static {
        CONTINENTS.put("africa",                     "1");
        CONTINENTS.put("asia + oceania",             "2");
        CONTINENTS.put("canada - (americas)",        "14");
        CONTINENTS.put("central america (americas)", "8");
        CONTINENTS.put("europe",                     "3");
        CONTINENTS.put("south america (americas)",   "4");
        CONTINENTS.put("north america (americas)",   "6");

        ACTIVE_CONTINENTS = Set.of(
                "africa",
                "asia + oceania",
                "canada - (americas)",
                "central america (americas)",
                "europe",
                "south america (americas)",
                "north america (americas)"
        );
    }

    public TagAlliance() {
        super(
                "TAG Alliance",
                BASE_URL,
                1, // totalPages not used; searchForLawyers is overridden
                5
        );
    }

    private String buildUrl(String regionId, int page) {
        String params = "?region=" + regionId + "&cfcat_id=43,127,130";
        if (page == 0) return BASE_URL + params;
        return BASE_URL + "/page" + (page + 1) + params;
    }

    @Override
    public Runnable searchForLawyers(boolean showLogs) {
        if (Validations.isAFirmToAVoid(this.name)) return null;

        this.driver = MyDriver.getINSTANCE();
        errorLogger.startFirm(this.name);

        continentLoop:
        for (Map.Entry<String, String> entry : CONTINENTS.entrySet()) {
            if (!ACTIVE_CONTINENTS.contains(entry.getKey())) continue;

            String continent = entry.getKey();
            String regionId = entry.getValue();
            System.out.printf("Continent: %s%n", continent);

            for (int page = 0; page < MAX_PAGES_PER_CONTINENT; page++) {
                System.out.printf("  Page %d%n", page + 1);

                try {
                    this.driver.get(buildUrl(regionId, page));
                    MyDriver.waitForPageToLoad();
                } catch (Exception e) {
                    errorLogger.log(this.name, e, false, "Error accessing page " + (page + 1) + " of " + continent);
                    break;
                }

                List<WebElement> lawyers;
                try {
                    lawyers = getLawyersInPage();
                } catch (Exception e) {
                    errorLogger.log(this.name, e, false, "Error fetching lawyers on page " + (page + 1) + " of " + continent);
                    break;
                }

                if (lawyers == null || lawyers.isEmpty()) break;

                for (int idx = 0; idx < lawyers.size(); idx++) {
                    WebElement lawyer = lawyers.get(idx);
                    int beforeCount = this.lawyersRegistered;

                    try {
                        Object details = getLawyer(lawyer);
                        if (details instanceof String) continue;

                        boolean maxReached = this.registerValidLawyer(details, idx, page, showLogs);

                        if (this.lawyersRegistered > beforeCount) {
                            continue continentLoop; // 1 registered → next continent
                        }

                        if (maxReached) break continentLoop;

                    } catch (StaleElementReferenceException e) {
                        try {
                            lawyers = getLawyersInPage();
                            if (lawyers != null && idx < lawyers.size()) idx--;
                        } catch (Exception ignored) {}
                    } catch (Exception e) {
                        String context = String.format("Error reading lawyer %d on page %d of %s", idx + 1, page + 1, continent);
                        if (showLogs) System.out.println(context + ": " + e.getMessage());
                        else errorLogger.log(this.name, e, false, context);
                    } finally {
                        if (driver.getWindowHandles().size() > 1) MyDriver.closeCurrentTab();
                    }
                }
            }
        }

        errorLogger.flushFirmLogs(this.name);
        return null;
    }

    @Override
    protected void accessPage(int index) throws InterruptedException {
        // Not used — searchForLawyers is overridden
    }

    @Override
    protected List<WebElement> getLawyersInPage() {
        return MyDriver.wait.findElements(By.className("listing-summary"));
    }

    @Override
    public String openNewTab(WebElement lawyer) throws LawyerExceptions, InterruptedException {
        String url = lawyer.findElement(By.cssSelector(".mt-ls-header h3 a")).getAttribute("href");
        MyDriver.openNewTab(url);
        return url;
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String name = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector("span[itemprop='name']")}, "NAME", LawyerExceptions::nameException);
        String country = extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".jurisdiction .output a")}, "COUNTRY", LawyerExceptions::countryException);
        String firm = extractor.extractLawyerText(lawyer, new By[]{By.className("mt-ls-fields-misc")}, "FIRM", LawyerExceptions::nameException);

        String link = this.openNewTab(lawyer);
        WebElement container = MyDriver.wait.findElement(By.className("main-content-area"));

        String[] socials = this.getSocialsFromText(container.getText());

        return Map.of(
                "link", link,
                "name", name,
                "role", "",
                "firm", firm,
                "country", country,
                "practice_area", "",
                "email", socials[0],
                "phone", socials[1]
        );
    }
}
