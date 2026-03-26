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


    /**
     * Calculate the time of an operation and return it as a formatted string
     */
    public String calculateTime(long initTime, long finalTime) {
        long resultTime = finalTime - initTime;

        long seconds = (resultTime / 1000) % 60;
        long minutes = (resultTime / (1000 * 60)) % 60;

        String valueToShow;
        if (minutes > 0) {
            valueToShow = String.format("%02dmin %02ds", minutes, seconds);
        } else {
            valueToShow = String.format("%02ds", seconds);
        }
        return valueToShow;
    }
}
