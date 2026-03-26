package org.example.src.sites.byPage;

import org.example.exceptions.LawyerExceptions;
import org.example.src.entities.BaseSites.ByPage;
import org.example.src.entities.MyDriver;
import org.example.src.utils.Validations;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class JIPYONG extends ByPage {

    private static final String[] VOWELS = {"a", "e", "i", "o", "u"};

    public JIPYONG() {
        super(
                "JIPYONG",
                "https://www.jipyong.com/en/member/member_list.php?business_type=&business_text=Practice%20Area&member_val=",
                VOWELS.length
        );
    }

    @Override
    public Runnable searchForLawyers(boolean showLogs) {
        if (Validations.isAFirmToAVoid(this.name)) return null;

        this.driver = MyDriver.getINSTANCE();
        errorLogger.startFirm(this.name);

        outer:
        for (String vowel : VOWELS) {
            String vowelUrl = this.link + vowel;
            this.driver.get(vowelUrl);
            MyDriver.waitForPageToLoad();

            int maxPage = getMaxPage(vowelUrl);
            System.out.printf("Vowel '%s' — %d page(s)%n", vowel, maxPage);

            for (int p = 1; p <= maxPage; p++) {
                System.out.printf("  Page %d / %d%n", p, maxPage);

                if (p > 1) {
                    this.driver.get(vowelUrl + "&page=" + p);
                    MyDriver.waitForPageToLoad();
                }

                List<WebElement> lawyersInPage;
                try {
                    lawyersInPage = getLawyersInPage();
                } catch (Exception e) {
                    errorLogger.log(this.name, e, false,
                            String.format("Error fetching lawyers — vowel '%s', page %d", vowel, p));
                    continue;
                }

                if (lawyersInPage == null || lawyersInPage.isEmpty()) continue;

                for (int idx = 0; idx < lawyersInPage.size(); idx++) {
                    try {
                        Object details = getLawyer(lawyersInPage.get(idx));
                        if (details instanceof String) continue;

                        boolean shouldStop = this.registerValidLawyer(details, idx, p, showLogs);
                        if (shouldStop) break outer;

                    } catch (Exception e) {
                        String context = String.format(
                                "Error reading lawyer %d — vowel '%s', page %d", idx + 1, vowel, p);
                        if (showLogs) System.out.println(context + ": " + e.getMessage());
                        else errorLogger.log(this.name, e, false, context);
                    }
                }
            }
        }

        errorLogger.flushFirmLogs(this.name);
        return null;
    }

    private int getMaxPage(String vowelUrl) {
        try {
            WebElement endPageBtn = driver.findElement(By.cssSelector(".paginate_complex a.end"));
            String href = endPageBtn.getAttribute("href");
            return Integer.parseInt(href.replaceAll(".*page=(\\d+).*", "$1"));
        } catch (Exception e) {
            return 1;
        }
    }

    @Override
    protected void accessPage(int index) {}

    @Override
    protected List<WebElement> getLawyersInPage() {
        try {
            WebDriverWait wait = new WebDriverWait(this.driver, Duration.ofSeconds(10));
            List<WebElement> lawyers = wait.until(
                    ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("ul.item-list > li")));
            return this.siteUtl.filterLawyersInPage(lawyers, new By[]{By.className("position")}, false);
        } catch (Exception e) {
            return List.of();
        }
    }

    @Override
    protected Object getLawyer(WebElement lawyer) throws Exception {
        String[] socials = super.getSocials(lawyer.findElements(By.cssSelector(".contact a")), false);

        return Map.of(
                "link", extractor.extractLawyerAttribute(lawyer, new By[]{By.cssSelector("a.thumb")}, "LINK", "href", LawyerExceptions::linkException),
                "name", extractor.extractLawyerText(lawyer, new By[]{By.cssSelector(".position a")}, "NAME", LawyerExceptions::nameException),
                "role", extractor.extractLawyerText(lawyer, new By[]{By.className("position")}, "ROLE", LawyerExceptions::roleException).replace(name, "").trim(),
                "firm", this.name,
                "country", "Korea (South)",
                "practice_area", extractor.extractLawyerText(lawyer, new By[]{By.className("specialty")}, "PRACTICE AREA", LawyerExceptions::practiceAreaException),
                "email", socials[0],
                "phone", socials[1].isEmpty() ? "82262001900" : socials[1]
        );
    }
}
