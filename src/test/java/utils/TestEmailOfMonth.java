package utils;

import org.example.src.utils.EmailOfMonth;

public class TestEmailOfMonth {
    public static void main(String[] args) {
        String path =  "src/test/java/utils/test.txt";
        String email = "email@email";
        EmailOfMonth.registerEmailOfMonth(email, path);
        System.out.println(EmailOfMonth.isEmailRegisteredInMonth(email, path));
    }
}
