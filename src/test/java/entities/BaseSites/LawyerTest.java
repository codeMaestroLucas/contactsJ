package entities.BaseSites;

import org.example.src.entities.Lawyer;

public class LawyerTest{
    public static void main(String[] args) {

        // 1 - Abbreviation cleaning
        Lawyer l1 = Lawyer.builder()
                .name("Dr. John Smith Esq.")
                .email("john.smith@example.com")
                .role("Partner")
                .firm("Test Firm")
                .country(" England ")
                .practiceArea("Employment Law Department")
                .phone("0044-123-456")
                .link("http://example.com")
                .build();
        print(l1);

        // 2 - Name from email
        Lawyer l2 = Lawyer.builder()
                .name("")
                .email("maria.santos@lawfirm.com")
                .role("Counsel")
                .firm("Firm A")
                .country("Brazil")
                .practiceArea("Tax Law")
                .phone("055-11-9999-8888")
                .link("http://firm.com")
                .build();
        print(l2);

        // 3 - Email cleaning
        Lawyer l3 = Lawyer.builder()
                .name("Adrian Adriano KC Legal PARTNER")
                .email("mailto:jane.doe@firm.com?subject=test")
                .role("Associate Counsel")
                .firm("Firm B")
                .country("USA")
                .practiceArea("Corporate Law")
                .phone("+1 (555) 123-4567")
                .link("http://firm.com")
                .build();
        print(l3);

        // 4 - Phone cleaning
        Lawyer l4 = Lawyer.builder()
                .name("Mike Johnson")
                .email("mike@firm.com")
                .role("Director")
                .firm("Firm C")
                .country("USA")
                .practiceArea("Litigation Service")
                .phone("(000) 555-7890")
                .link("http://firm.com")
                .build();
        print(l4);

        // 5 - Role case insensitive
        Lawyer l5 = Lawyer.builder()
                .name("Alice Brown")
                .email("alice@firm.com")
                .role("senior partner")
                .firm("Firm D")
                .country("France")
                .practiceArea("Banking Law")
                .phone("01234 567890")
                .link("http://firm.com")
                .build();
        print(l5);

        // 6 - Practice area cleaning
        Lawyer l6 = Lawyer.builder()
                .name("Carlos Mendes")
                .email("carlos@firm.com")
                .role("Counsel")
                .firm("Firm E")
                .country("Spain")
                .practiceArea("Family Law Services")
                .phone("0034-600-123456")
                .link("http://firm.com")
                .build();
        print(l6);

        // 7 - Advisor default
        Lawyer l7 = Lawyer.builder()
                .name("George White")
                .email("george@firm.com")
                .role("Advisor")
                .firm("Firm F")
                .country("USA")
                .practiceArea("Criminal Law")
                .phone("5551234")
                .link("http://firm.com")
                .build();
        print(l7);

        // 8 - Legal fallback
        Lawyer l8 = Lawyer.builder()
                .name("Helen Black")
                .email("helen@firm.com")
                .role("Manager")
                .firm("Firm G")
                .country("Canada")
                .practiceArea("Immigration Specialist")
                .phone("111222333")
                .link("http://firm.com")
                .build();
        print(l8);

        // 9 - Get name from email with dash/dot
        Lawyer l9 = Lawyer.builder()
                .name("")
                .email("john-doe.silva@company.com")
                .role("Partner")
                .firm("Firm H")
                .country("Portugal")
                .practiceArea("Real Estate Law")
                .phone("987654321")
                .link("http://firm.com")
                .build();
        print(l9);

        // 10 - Null practice area
        Lawyer l10 = Lawyer.builder()
                .name("Laura King")
                .email("laura@firm.com")
                .role("Founder")
                .firm("Firm I")
                .country("Germany")
                .practiceArea(null)
                .phone("12345")
                .link("http://firm.com")
                .build();
        print(l10);
    }

    private static void print(Lawyer lawyer) {
        System.out.println("======================================");
        System.out.println("Name       : " + lawyer.getName());
        System.out.println("Email      : " + lawyer.getEmail());
        System.out.println("Phone      : " + lawyer.getPhone());
        System.out.println("Role       : " + lawyer.getRole());
        System.out.println("Firm       : " + lawyer.getFirm());
        System.out.println("Country    : " + lawyer.getCountry());
        System.out.println("Practice   : " + lawyer.getPracticeArea());
        System.out.println("Specialism : " + lawyer.getSpecialism());
    }
}
