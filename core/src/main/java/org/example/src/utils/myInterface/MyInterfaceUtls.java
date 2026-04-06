package org.example.src.utils.myInterface;

import java.util.Objects;

public class MyInterfaceUtls {
    private static MyInterfaceUtls INSTANCE;

    private MyInterfaceUtls() {}

    public static MyInterfaceUtls getINSTANCE() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new MyInterfaceUtls();
        }
        return INSTANCE;
    }


    /**
     * Prints a header with a firm name on it
     */
    public void header(String firm) {
        String title = String.format("| \u001B[1;33m%s\u001B[0;0m |", firm);
        int sizeHeader = (70 - title.length()) / 2;
        System.out.println("-".repeat(sizeHeader) + title + "-".repeat(sizeHeader));
        System.out.println();
    }
}
