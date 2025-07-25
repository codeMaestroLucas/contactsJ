package entities.excel;

import org.example.src.entities.Lawyer;
import org.example.src.entities.excel.Sheet;

public class TestSheet {
    public static void main(String[] args) {
        Lawyer lawyer1 = new Lawyer(
                "https://example.com",
                "Dr. John P. Doe, Senior Partner",
                "Partner",
                "Doe & Co.",
                "UK",
                "Corporate Law",
                "mailto:john.doe@example.com",
                "+1 (000) 123-4567"
        );

        Lawyer lawyer2 = new Lawyer(
                "https://example.com",
                "pamelA paRTNER K.C. L.M.",
                "Partner",
                "Doe & Co.",
                "UK",
                "Commercial Law",
                "mailto:pamela@example.com",
                "+1 (000) 123-4567"
        );

        Lawyer lawyer3 = new Lawyer(
                "https://example.com",
                "Ms. Jane Smith\nCounsel",
                "Counsel",
                "Carbto",
                "UK",
                "",
                "mailto:jane.smith@smithllp.co.uk",
                "0044 020 7946 0958"
        );

        Sheet s = Sheet.getINSTANCE();
        s.addLawyer(lawyer1);
        s.addLawyer(lawyer2);
        s.addLawyer(lawyer3);
    }
}
