package entities.excel;

import org.example.src.entities.excel.Reports;

public class TestReports {

    public static void main(String[] args) {
        Reports reports = Reports.getINSTANCE();

        System.out.println("=== Test 1: Creating a Report Row ===");
        reports.createReportRow("Test Firm", "90s", 3);
        reports.createReportRow("Another Firm", "5s", 1);
        reports.createReportRow("More one Firm", "43s", 5);
        System.out.println("Rows created successfully.");
    }
}
