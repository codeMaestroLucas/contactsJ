package org.example.src.entities.excel;

import org.example.src.CONFIG;
import org.example.src.entities.BaseSites.Site;
import org.example.src.utils.FirmsExhausted;
import org.example.src.utils.Stopwatch;

import java.util.Comparator;
import java.util.Objects;

public final class Reports extends Excel {
    private static Reports INSTANCE;

    private int currentRow;

    private Reports() {
        super(CONFIG.REPORTS_FILE);
        this.currentRow = 1;
        this.eraseLastSheet();
    }

    public static Reports getINSTANCE() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new Reports();
        }
        return INSTANCE;
    }


    public void createReportRow(Site site, String time) {
        if (site.lawyersRegistered > 0) return;
        try {
            this.addContentOnRow(currentRow, site.name, time, String.valueOf(site.lawyersRegistered));
            this.currentRow++;
            String pkg       = site.getClass().getPackageName();
            String continent = pkg.substring(pkg.lastIndexOf('.') + 1);
            FirmsExhausted.register(continent + "/" + site.getClass().getSimpleName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sortRows() {
        Comparator<String[]> comparator =
            Comparator.comparingInt((String[] r) -> Stopwatch.parseSeconds(r[2]))
                      .thenComparingInt(r -> -Stopwatch.parseSeconds(r[1]));
        this.sortRows(comparator, 3);
    }
}
