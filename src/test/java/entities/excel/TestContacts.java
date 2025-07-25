package entities.excel;

import org.example.src.entities.excel.Contacts;

public class TestContacts {
    public static void main(String[] args) {
        Contacts c = Contacts.getINSTANCE();

        System.out.println(c.isEmailRegistered("this@email.com.br"));             // False
        System.out.println(c.isEmailRegistered("20dominik.vitek@pierstone.com")); // True
        System.out.println(c.isEmailRegistered("zzaparas@zaparaslaw.com.au"));    // True
    }
}
