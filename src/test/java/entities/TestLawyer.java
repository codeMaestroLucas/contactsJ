package entities;

import org.example.src.entities.Lawyer;

public class TestLawyer {

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
                "Ms. Jane Smith\nCounsel",
                "Counsel",
                "Carbto",
                "UK",
                "",
                "mailto:jane.smith@smithllp.co.uk",
                "0044 020 7946 0958"
        );

        System.out.println(lawyer1);
        System.out.println(lawyer2);
    }
}
