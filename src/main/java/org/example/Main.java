package org.example;

import org.example.src.entities.BaseSites.Site;
import org.example.src.entities.MyDriver;
import org.example.src.entities.excel.Reports;
import org.example.src.utils.FirmsOfWeek;
import org.example.src.utils.myInterface.CompletedFirms;
import org.example.src.utils.myInterface.MyInterfaceUtls;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        MyInterfaceUtls interfaceUtls = CompletedFirms.interfaceUtls;
        List<Site> sites = CompletedFirms.constructFirms();
        Reports reports = Reports.getINSTANCE();

        long initTotalTime = System.currentTimeMillis();

        // Processing Firms
        try {
            // Already making the verification of Site.name registered in the Week firms in the constructor
            for (Site site : sites) {
                long initTimeFirm = System.currentTimeMillis();

                interfaceUtls.header(site.name);

                site.searchForLawyers();

                long finalTimeFirm = System.currentTimeMillis();
                String time = interfaceUtls.calculateTime(initTimeFirm, finalTimeFirm);

                System.out.println("\n  - Time: " + time);
                System.out.println("  - Lawyers registered: " + site.lawyersRegistered + "\n");

                reports.createReportRow(site, time);

                if (site.lawyersRegistered > 0) FirmsOfWeek.registerFirmWeek(site.name);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            long finalTotalTime = System.currentTimeMillis();

            // Final output
            System.out.println();
            System.out.println("=".repeat(70));
            System.out.println("\nTotal time: " + interfaceUtls.calculateTime(initTotalTime, finalTotalTime) + "\n\n");

            MyDriver.quitDriver();
        }

    }
}
