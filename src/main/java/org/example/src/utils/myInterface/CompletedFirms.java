package org.example.src.utils.myInterface;

import lombok.Getter;
import org.example.src.entities.BaseSites.Site;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class CompletedFirms {
    private final static Site[] byPage = _CompletedFirmsData.byPage;
    private final static Site[] byNewPage = _CompletedFirmsData.byNewPage;
    private final static Site[] byFilter = _CompletedFirmsData.byFilter;
    private final static Site[] byClick = _CompletedFirmsData.byClick;

    public final static MyInterfaceUtls interfaceUtls = MyInterfaceUtls.getINSTANCE();


    /**
     * Construct all firms and insert them in an array and then shuffle it.
     */
    public static List<Site> constructFirms() {
        List<Site> collect = Stream.of(byPage, byNewPage, byFilter, byClick)
                .flatMap(array -> Arrays.stream(array).filter(Objects::nonNull))
                .collect(Collectors.toList());

        Collections.shuffle(collect); // Shuffle the list
        return collect;
    }

    /**
     * A log print to count all firms completed
     */
    public static void showCompletedFirmsPrint() {
        String title = "| COMPLETED |";
        int sizeHeader = (70 - title.length()) / 2;
        System.out.println("-".repeat(sizeHeader) + title + "-".repeat(sizeHeader));

        System.out.printf(" -  ByPage:    %-30s To Register: %d%n", byPage.length, countTotalMaxLawyer(byPage));
        System.out.printf(" -  ByNewPage: %-30s To Register: %d%n", byNewPage.length, countTotalMaxLawyer(byNewPage));
        System.out.printf(" -  ByFilter:  %-30s To Register: %d%n", byFilter.length, countTotalMaxLawyer(byFilter));
        System.out.printf(" -  ByClick:   %-30s To Register: %d%n", byClick.length, countTotalMaxLawyer(byClick));

        System.out.println("-".repeat(70));
    }

    private static int countTotalMaxLawyer(Site[] firms) {
        int total = 0;
        for (Site firm : firms) {
             total += firm.maxLawyersForSite;
        }
        return total;
    }

    public static void main(String[] args) {
        showCompletedFirmsPrint();
    }
}
